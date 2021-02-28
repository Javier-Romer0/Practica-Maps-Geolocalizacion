package cat.itb.practicamaps.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.models.LocationMarker;
import cat.itb.practicamaps.utilities.ConvertURLToBitmap;
import cat.itb.practicamaps.utilities.CustomInfoWindowAdapter;

import static cat.itb.practicamaps.firebase.FirebaseSettings.myRef;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;

    private FloatingActionButton addLocationButton;
    private Fragment currentFragment;
    private LatLng currentPosition;
    private double lat, lon;
    private List<LocationMarker> locationMarkerList;
    private double markerLat, markerLon;
    private boolean viewMarker = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        if (getArguments() != null){
            markerLat = getArguments().getDouble("viewLat");
            markerLon = getArguments().getDouble("viewLon");
            viewMarker = true;
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = rootView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        addLocationButton = view.findViewById(R.id.button_add_marker);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                bundle.putDouble("lon", lon);
                bundle.putBoolean("addmarker", true);
                currentFragment = new FormulariFragment();
                currentFragment.setArguments(bundle);
                changeFragment(currentFragment);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        enableMyLocation();
        getLastLocation();

        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity())));

        gMap.setMinZoomPreference(10);
        gMap.setMaxZoomPreference(20);

        locationMarkerList = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    LocationMarker marker = s.getValue(LocationMarker.class);
                    locationMarkerList.add(marker);
                    for (int i = 0; i < locationMarkerList.size(); i++) {
                        Marker newMarker = gMap.addMarker(new MarkerOptions()
                                .position(new LatLng(locationMarkerList.get(i).getLat(), locationMarkerList.get(i).getLon()))
                                .title(locationMarkerList.get(i).getTitle())
                                .snippet(locationMarkerList.get(i).getDescription())
//                                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_info_details))
                                .icon(BitmapDescriptorFactory.fromBitmap(ConvertURLToBitmap.getBitmapFromURL(locationMarkerList.get(i).getPictureurl())))
                                .draggable(false)
                        );
                        newMarker.setTag(locationMarkerList.get(i));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void changeFragment(Fragment currentFragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private LatLng getLastLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (viewMarker) currentPosition = new LatLng(markerLat, markerLon);
                    else currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    CameraPosition camera = new CameraPosition.Builder()
                            .target(currentPosition)
                            .zoom(15)
                            .bearing(0)
                            .tilt(30)
                            .build();
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
                }
            }
        });
        return currentPosition;
    }

}