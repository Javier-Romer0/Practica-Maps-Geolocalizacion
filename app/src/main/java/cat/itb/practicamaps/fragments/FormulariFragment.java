package cat.itb.practicamaps.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.activities.MainActivity;
import cat.itb.practicamaps.firebase.FirebaseSettings;
import cat.itb.practicamaps.models.LocationMarker;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class FormulariFragment extends Fragment {

    private Button button_camera, button_upload;
    private ImageView imageView_camera;
    private EditText editText_title, editText_description;

    private LocationMarker locationMarker = new LocationMarker();
    private Fragment currentFragment;
    private LatLng currentPosition;

    private Bitmap thumb_bitmap;
    private String nameImg, key, pictureUrl;
    private byte[] thumb_byte;
    private File urlImg;
    private boolean imageIsUpdated = false, addingMarker = false;
    private Uri downloadUri;
    private double lat, lon;

    public FormulariFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_formulari, container, false);
        button_camera = v.findViewById(R.id.button_camera);
        imageView_camera = v.findViewById(R.id.imageView_camera);
        button_upload = v.findViewById(R.id.button_upload_marker);
        editText_title = v.findViewById(R.id.edittext_title_formulari);
        editText_description = v.findViewById(R.id.edittext_description_formulari);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){
            currentPosition = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lon"));
            editText_title.setText(getArguments().getString("title"));
            editText_description.setText(getArguments().getString("description"));
            key = getArguments().getString("key");
            pictureUrl = getArguments().getString("picture");
            lat = getArguments().getDouble("updateLat");
            lon = getArguments().getDouble("updateLon");
            addingMarker = getArguments().getBoolean("addmarker");
            Picasso.with(getContext()).load(getArguments().getString("picture")).into(imageView_camera);
        }
        checkAllDataFilled();

        editText_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllDataFilled();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        editText_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllDataFilled();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(getActivity(), FormulariFragment.this);
                imageIsUpdated = true;
            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageIsUpdated){
                    comprimirImagen();
                    subirImagen();
                }else {
                    createMarker();
                }

                currentFragment = new MapsFragment();
                changeFragment(currentFragment);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            recortarImagen(imageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                urlImg = new File(resultUri.getPath());
                Picasso.with(getContext()).load(urlImg).into(imageView_camera);
            }
        }
    }

    private void changeFragment(Fragment currentFragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    private void recortarImagen(Uri imageUri){
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(640, 840)
                .setAspectRatio(2, 2).start(getContext(), FormulariFragment.this);
        checkAllDataFilled();
    }

    private void comprimirImagen(){
        try {
            thumb_bitmap = new Compressor(getContext())
                    .setMaxHeight(125)
                    .setMaxWidth(125)
                    .setQuality(50)
                    .compressToBitmap(urlImg);
        }catch (IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        thumb_byte = byteArrayOutputStream.toByteArray();
    }

    private void subirImagen(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        nameImg = timestamp + ".jpg";

        final StorageReference ref = FirebaseSettings.storageReference.child(nameImg);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("clave1", "Lat: " + currentPosition.latitude)
                .setCustomMetadata("clave2", "Lon: " + currentPosition.longitude)
                .build();
        UploadTask uploadTask = ref.putBytes(thumb_byte, metadata);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                downloadUri = task.getResult();
                FirebaseSettings.imgRef.push().child("urlfoto").setValue(downloadUri.toString());
                createMarker();
            }
        });
    }

    private void createMarker(){
        locationMarker.setTitle(editText_title.getText().toString());
        locationMarker.setDescription(editText_description.getText().toString());
        if (!addingMarker){
            if (!imageIsUpdated){
                locationMarker.setPictureurl(pictureUrl);
            }else {
                locationMarker.setPictureurl(downloadUri.toString());

            }
            locationMarker.setIdMarker(key);
            locationMarker.setLat(lat);
            locationMarker.setLon(lon);
            FirebaseSettings.updateMarker(locationMarker);
        }else {
            locationMarker.setPictureurl(downloadUri.toString());
            locationMarker.setLat(currentPosition.latitude);
            locationMarker.setLon(currentPosition.longitude);
            FirebaseSettings.addMarker(locationMarker);
        }
    }

    private void checkAllDataFilled(){
        String title = editText_title.getText().toString().trim();
        String desc = editText_description.getText().toString().trim();
        if (addingMarker){
            if (title.isEmpty() || desc.isEmpty() || !imageIsUpdated){
                button_upload.setEnabled(false);
            }else {
                button_upload.setEnabled(true);
            }
        }else {
            if (title.isEmpty() || desc.isEmpty()){
                button_upload.setEnabled(false);
            }else {
                button_upload.setEnabled(true);
            }
        }
    }
}