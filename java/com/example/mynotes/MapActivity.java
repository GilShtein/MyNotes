package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;

    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    private GeoApiContext geoApiContext;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mReferenceNotes;
    private List<Note> notes= new ArrayList<>();

    private List<MarkerOptions> markerOptions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
       // setupMap();
    }

    void setupMap()
    {

    }
    /**
     * Get permission to the map to get the user's location
     */
    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }
    }

    /**
     * Initialize the map components on the Fragment in the xml through Google's API
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey("")
                    .build();
        }
    }


    /**
     * Get permission results to the map to get the user's locations
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;


                }
            }
        }
    }

    /**
     * Get user's live current location (latitude and longlitude)
     * get the origin location and the destination location (latitude and longlitude)
     */



    /**
     * Initialize Gopogl\e's map
     * @param googleMap from Google's API get map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markNote();
    }


    void markNote()
    {

        LatLng latlng=new LatLng(32.16,34.84);
        CameraUpdate center = CameraUpdateFactory.newLatLng(latlng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

      //  FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
       // return FirebaseFirestore.getInstance().collection("notes")
             //   .document(currentUser.getUid()).collection("my_notes");


        mDatabase =FirebaseDatabase.getInstance();
        mReferenceNotes=mDatabase.getReference("notes");
        mReferenceNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notes.clear();
                List<String> key= new ArrayList<>();
                for(DataSnapshot keyNode: snapshot.getChildren())
                {
                   // String title = keyNode.child("title").getValue(String.class);
                    //String content =keyNode.child("content").getValue(String.class);;
                   // Timestamp timestamp = keyNode.child("timestamp").getValue(Timestamp.class);;
                    //String location = keyNode.child("location").getValue(String.class);
                   // double latitude = keyNode.child("latitude").getValue(double.class);
                   // double longlitude = keyNode.child("longlitude").getValue(double.class);;
                    key.add(keyNode.getKey());
                    Note note= new Note();
                    note = keyNode.getValue(Note.class);
                   // note.setTitle(title);
                    //note.setContent(content);
                    //note.setTimestamp(Timestamp.now());
                    //note.setLocation(location);
                   // note.setLatitude(latitude);
                    //note.setLonglitude(longlitude);
                    //note.setTimestamp(timestamp);
                    notes.add(note);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Add Marker on route starting position

       // MarkerOptions startMarker = new MarkerOptions();
        for(int i=0; i<notes.size();i++)
        {
            LatLng ltln = new LatLng(notes.get(i).getLatitude(),notes.get(i).getLonglitude());
            MarkerOptions temp = new MarkerOptions();
            temp.position(ltln);
            temp.title(notes.get(i).getTitle());
            markerOptions.add(temp);
            mMap.addMarker(markerOptions.get(i));
        }
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position( latlng);
        mMap.addMarker(startMarker);






       //LatLng latlng= markerOptions.get(0).getPosition();
        float z = 9f ;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, z));

    }
    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    /**
     * On Route success show the markers of origin and destination on the map,
     * Show the most short route on the map
     * @param route the route
     * @param shortestRouteIndex the shorthest route
     */
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        LatLng latlng=new LatLng(32.16,34.84);
        //CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);


        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        //startMarker.position(polylineStartLatLng);
        startMarker.title("Origin");
        mMap.addMarker(startMarker);
        Toast.makeText(this,
                "\t\tPress Marker\nthen Press little Arrow to start navigation" ,
                Toast.LENGTH_LONG).show();

        /**
         * Add Marker on route ending position Just for the driver
         * Passengers can route to Driver's start trip location
         */


        //LatLng latLng = start ;
        float z = 9f ;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, z));

        // if User is passenger navigate to the Driver's Position
        // if User is driver navigate to destination


    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}