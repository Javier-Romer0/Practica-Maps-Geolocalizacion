package cat.itb.practicamaps.utilities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import cat.itb.practicamaps.firebase.FirebaseSettings;
import cat.itb.practicamaps.models.LocationMarker;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback  {

    private MarkerAdapter adapter;

    public SwipeToDeleteCallback(MarkerAdapter markerAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        adapter = markerAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        LocationMarker deleteMarker = adapter.getItem(position);
        FirebaseSettings.deleteMarker(deleteMarker.getIdMarker());
    }
}
