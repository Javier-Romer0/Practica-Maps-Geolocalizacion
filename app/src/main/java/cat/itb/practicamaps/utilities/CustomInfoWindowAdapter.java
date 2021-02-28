package cat.itb.practicamaps.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.models.LocationMarker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.infowindow_maps, null);
        ImageView imageView = v.findViewById(R.id.marker_imageview);
        TextView tv_title = v.findViewById(R.id.marker_title);
        TextView tv_snippet = v.findViewById(R.id.marker_description);

        LocationMarker locMarker = (LocationMarker) marker.getTag();
        Picasso.with(v.getContext()).load(locMarker.getPictureurl()).into(imageView);
        tv_title.setText(marker.getTitle());
        tv_snippet.setText(marker.getSnippet());

        return v;
    }
}
