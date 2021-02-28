package cat.itb.practicamaps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cat.itb.practicamaps.R;
import cat.itb.practicamaps.firebase.FirebaseSettings;
import cat.itb.practicamaps.fragments.FormulariFragment;
import cat.itb.practicamaps.fragments.ListFragment;
import cat.itb.practicamaps.fragments.MapsFragment;

public class MainActivity extends AppCompatActivity {

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseSettings.loadDatabase();

        if (savedInstanceState == null){
            currentFragment = new ListFragment();
            changeFragment(currentFragment);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_list_fragment:
                currentFragment = new ListFragment();
                FirebaseSettings.loadDatabase();
                break;
            case R.id.menu_map_fragment:
                currentFragment = new MapsFragment();
                break;
        }
        changeFragment(currentFragment);
        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(Fragment currentFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

}