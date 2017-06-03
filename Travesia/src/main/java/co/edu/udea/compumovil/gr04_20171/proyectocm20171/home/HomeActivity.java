package co.edu.udea.compumovil.gr04_20171.proyectocm20171.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist.ProfileFragment;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.group.CreateGroupFragment;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.map.MapsFragment;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.services.UpdateLocation;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.addEdit.AddEditUserActivity;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.login.LoginActivity;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private android.support.v4.app.FragmentManager fragmentManager;

    boolean checkGPS = false;
    boolean checkNetwork = false;
    protected LocationManager locationManager;
    TimerTask timerTask;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        // getting GPS status
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!checkGPS) {
            //Toast.makeText(this, "No Service Provider Available", Toast.LENGTH_SHORT).show();
            showSettingsAlert();
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    UpdateLocation gps = new UpdateLocation(HomeActivity.this);
                    double longitude = gps.getLongitude();
                    double latitude = gps.getLatitude();
                    handler.postDelayed(this,15000);
                }
            };
            handler.postDelayed(runnable,0);

        }

        // Iniciamos el fragment manager
        fragmentManager = getSupportFragmentManager();

        final CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_agregar_eventos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.content_home, createGroupFragment).commit();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onRestart(){
        super.onRestart();
        //updateLocation();
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        // getting GPS status
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e("checkGPS", String.valueOf(checkGPS));
        // getting network status
        checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e("checkNetwork", String.valueOf(checkNetwork));

        if (!checkGPS) {
            //Toast.makeText(this, "No Service Provider Available", Toast.LENGTH_SHORT).show();
            showSettingsAlert();
        } else {
            UpdateLocation gps = new UpdateLocation(HomeActivity.this);
            double longitude = gps.getLongitude();
            double latitude = gps .getLatitude();

            //Toast.makeText(getApplicationContext(),"Longitude:"+Double.toString(longitude)+"\nLatitude:"+Double.toString(latitude),Toast.LENGTH_SHORT).show();
        }
    }


    public void updateLocation(){
        Intent updateLocationService = new Intent(
                getApplicationContext(), UpdateLocation.class);
        startService(updateLocationService);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            setTitle("Perfil");
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_group) {
            setTitle("Crear grupo");
            fragment = new CreateGroupFragment();
            //setTitle("Crear grupo de travesía");
            //fragment = new CreateGroupFragment();
        } else if (id == R.id.nav_crossing) {
            setTitle("Travesías");
            fragment = new MapsFragment();
            Bundle arguments = new Bundle();
            arguments.putString("fragment", "personal");
            fragment.setArguments(arguments);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        if(fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content_home,fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showScreenMap(String groupKey){
        MapsFragment mapsFragment = new MapsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("fragment", "group");
        arguments.putString("groupKey",groupKey);
        mapsFragment.setArguments(arguments);
        fragmentManager.beginTransaction().replace(R.id.content_home, mapsFragment).commit();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        alertDialog.setTitle("GPS no está habilitado");

        alertDialog.setMessage("¿Desea habilitar el GPS?");


        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }


}
