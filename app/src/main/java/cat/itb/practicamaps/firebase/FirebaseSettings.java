package cat.itb.practicamaps.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cat.itb.practicamaps.models.LocationMarker;

public class FirebaseSettings {
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static DatabaseReference imgRef;
    public static StorageReference storageReference;

    public static void loadDatabase(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("LocationMarker");
        imgRef = database.getReference().child("Picture");
        storageReference = FirebaseStorage.getInstance().getReference().child("images");
    }

    public static void addMarker(LocationMarker locationMarker){
        String markerKey = FirebaseSettings.myRef.push().getKey();
        LocationMarker newMarker = new LocationMarker(markerKey, locationMarker.getTitle(), locationMarker.getLat(), locationMarker.getLon(), locationMarker.getDescription(), locationMarker.getPictureurl());
        FirebaseSettings.myRef.child(markerKey).setValue(newMarker);
    }

    public static void updateMarker(LocationMarker locationMarker){
        FirebaseSettings.myRef.child(locationMarker.getIdMarker()).setValue(locationMarker);
    }

    public static void deleteMarker(String key){
        myRef.child(key).removeValue();
    }
}
