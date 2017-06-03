package co.edu.udea.compumovil.gr04_20171.proyectocm20171.map;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_FRAGMENT = "fragment";
    private static final String ARG_PARAM_GROUPKEY = "groupKey";
    private static final int REQUEST_CHECK_SETTINGS = 12;

    private GoogleMap mMap;

    ArrayList<LatLng> markerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mCurrentLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    DrawInMap drawInMap;
    boolean mRequestingLocationUpdates;

    private TextView tvStart = null;
    private TextView tvNotification = null;

    private TextView tvMsgStart = null;

    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mPreviusFragment = null;
    private String mGroupKey = null;

    private OnFragmentInteractionListener mListener;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_FRAGMENT, param1);
        args.putString(ARG_PARAM_GROUPKEY, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPreviusFragment = getArguments().getString(ARG_PARAM_FRAGMENT);
            if(mPreviusFragment == "group"){
                mGroupKey = getArguments().getString(ARG_PARAM_GROUPKEY);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_maps, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        enableGPS(getActivity());
        tvStart = (TextView) root.findViewById(R.id.tv_start);
        tvNotification = (TextView) root.findViewById(R.id.tv_notification);
        tvMsgStart = (TextView) root.findViewById(R.id.tv_start_label);

        //Location Request
        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMsgStart.setText("En progreso...");
            }
        });

        tvNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng myPotition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myPotition).title("Barado").icon(BitmapDescriptorFactory.fromResource(R.drawable.alert)));
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(myPotition)
                        .zoom(15)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locatioSettingsState = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        // ...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

        // Initializing
        markerPoints = new ArrayList<>();
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Sets the map type to be "hybrid"
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            return;
        }
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Already two locations
                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    drawInMap = new DrawInMap(origin, dest, mMap);
                    drawInMap.drawLine();

                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            return;
        }
        if(mPreviusFragment == "personal"){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mCurrentLocation = mLastLocation;
                LatLng myPotition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myPotition).title("Mi posición"));
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(myPotition)
                        .zoom(15)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }else if(mPreviusFragment == "group"){

            ValueEventListener grouptListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> mapGroup = (Map<String, Object>) dataSnapshot.getValue();
                    Log.e("nombre", (String) mapGroup.get("name"));
                     //Toast.makeText(getActivity(), (CharSequence) mapGroup.get("name"),Toast.LENGTH_LONG).show();
                    Map<String, Boolean> members = (Map<String, Boolean>) mapGroup.get("members");
                    for (String member : members.keySet()){
                        Log.e("miembro", member);
                        ValueEventListener cyclistListener = new ValueEventListener(){
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> mapCyclist = (Map<String, Object>) dataSnapshot.getValue();
                                Map<String ,String> location = (Map<String, String>) mapCyclist.get("location");
                                double latitude = 0;
                                double longitude = 0;
                                latitude = Double.parseDouble(location.get("latitude"));
                                longitude = Double.parseDouble(location.get("longitude"));
                                Log.e("latitude", String.valueOf(latitude));
                                LatLng memberPotition = new LatLng(latitude,longitude);
                                mMap.addMarker(new MarkerOptions().position(memberPotition).title("Ciclista"));
                                CameraPosition cameraPosition = CameraPosition.builder()
                                        .target(memberPotition)
                                        .zoom(15)
                                        .build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        mDatabase.child("cyclist").child(member).addValueEventListener(cyclistListener);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabase.child("group").child(mGroupKey).addValueEventListener(grouptListener);
        }



        //if (mRequestingLocationUpdates) {
        //    startLocationUpdates();
        //}
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) getActivity());

    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("Latitude location update", String.valueOf(mCurrentLocation.getLatitude()));
        Log.d("Longitude location update", String.valueOf(mCurrentLocation.getLongitude()));

        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //updateUI();
    }


    /*private void updateUI() {
        tvLatitude.setText(String.valueOf(mCurrentLocation.getLatitude()));
        tvLongitude.setText(String.valueOf(mCurrentLocation.getLongitude()));
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /*
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }*/

    @Override
    public void onPause() {
        super.onPause();
       // stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void enableGPS(FragmentActivity activity){
        LocationManager service = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }
}
