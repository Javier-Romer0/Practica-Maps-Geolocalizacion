package cat.itb.practicamaps.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.firebase.FirebaseSettings;
import cat.itb.practicamaps.models.LocationMarker;
import cat.itb.practicamaps.utilities.MarkerAdapter;
import cat.itb.practicamaps.utilities.SwipeToDeleteCallback;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    public static MarkerAdapter adapter;
    Fragment currentFragment;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<LocationMarker> options = new FirebaseRecyclerOptions.Builder<LocationMarker>().setQuery(FirebaseSettings.myRef, LocationMarker.class).build();
        adapter = new MarkerAdapter(options, marker -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", marker.getTitle());
            bundle.putString("description", marker.getDescription());
            bundle.putString("picture", marker.getPictureurl());
            bundle.putString("key", marker.getIdMarker());
            bundle.putDouble("updateLat", marker.getLat());
            bundle.putDouble("updateLon", marker.getLon());
            currentFragment= new FormulariFragment();
            currentFragment.setArguments(bundle);
            changeFragment(currentFragment);
        }, position ->{
            Bundle bundle = new Bundle();
            bundle.putDouble("viewLat", position.getLat());
            bundle.putDouble("viewLon", position.getLon());
            currentFragment = new MapsFragment();
            currentFragment.setArguments(bundle);
            changeFragment(currentFragment);
        });
        recyclerView.setAdapter(adapter);
        setUpRecyclerView();
        return v;
    }

    private void setUpRecyclerView(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void changeFragment(Fragment currentFragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        ListFragment.adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ListFragment.adapter.stopListening();
    }
}