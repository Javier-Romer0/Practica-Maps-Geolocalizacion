package cat.itb.practicamaps.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.File;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.fragments.FormulariFragment;
import cat.itb.practicamaps.models.LocationMarker;

public class MarkerAdapter extends FirebaseRecyclerAdapter<LocationMarker, MarkerAdapter.LocationMarkerHolder> {
    private Context itemContext;
    private OnClickItemHolder onClickItemHolder;
    private OnImageClick onImageClick;

    public MarkerAdapter(@NonNull FirebaseRecyclerOptions<LocationMarker> options, OnClickItemHolder onClickItemHolder, OnImageClick onImageClick) {
        super(options);
        this.onClickItemHolder = onClickItemHolder;
        this.onImageClick = onImageClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull LocationMarkerHolder holder, int position, @NonNull LocationMarker model) {
        holder.bind(model);

        holder.itemView.setOnClickListener(view -> onClickItemHolder.OnClickItem(model));
    }

    @NonNull
    @Override
    public LocationMarkerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marker, parent, false);
        return new LocationMarkerHolder(v);
    }

    public class LocationMarkerHolder extends RecyclerView.ViewHolder {
        ImageView imageview_picture;
        TextView textview_title, textview_description;
        ImageView imageview_position;

        public LocationMarkerHolder(@NonNull View itemView) {
            super(itemView);
            imageview_picture = itemView.findViewById(R.id.imageview_item_picture);
            textview_title = itemView.findViewById(R.id.textview_item_title);
            textview_description = itemView.findViewById(R.id.textview_item_description);
            imageview_position = itemView.findViewById(R.id.imageview_item_position);
            itemContext = itemView.getContext();
        }

        public void bind(final LocationMarker locationMarker){
            Picasso.with(itemContext).load(locationMarker.getPictureurl()).into(imageview_picture);
            textview_title.setText(locationMarker.getTitle());
            textview_description.setText(locationMarker.getDescription());
            imageview_position.setOnClickListener(view -> {onImageClick.OnButtonClick(locationMarker);});
        }
    }

    public interface OnClickItemHolder{
        void OnClickItem(LocationMarker locationMarker);
    }

    public interface OnImageClick{
        void OnButtonClick(LocationMarker locationMarker);
    }
}
