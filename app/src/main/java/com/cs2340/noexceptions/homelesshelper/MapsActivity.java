package com.cs2340.noexceptions.homelesshelper;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity containing the map of Shelters
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String currentShelter;
    private final Map<String,Marker> shelterMarkers = new HashMap<>();
    private final Collection<Shelter> shelters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("shelters");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.753746,-84.386330)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Shelter s = snapshot.getValue(Shelter.class);
                    if (s != null) {
                        s.updateAllNeeded();
                    }
                    double latitude = 0;
                    double longitude = 0;
                    if (s != null) {
                        latitude = Double.parseDouble(s.getLatitude());
                        longitude = Double.parseDouble(s.getLongitude());

                    }
                    LatLng shelter = new LatLng(latitude,longitude);
                    Marker marker;
                    if (s != null) {
                        marker = mMap.addMarker(new MarkerOptions().position(shelter)
                                .title(s.getName()).snippet(s.getTelephoneNumber()));
                        shelterMarkers.put(s.getName(), marker);
                        shelters.add(s);

                    }
                }
                updateMarkers();
                mapMarkerOnClick();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * A method that will return the current shelter's name clicked
     * @return The current shelter clicked
     */
    public static String getCurrentShelter() {
        return currentShelter;
    }
    private void mapMarkerOnClick() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle() != null) {
                    currentShelter = marker.getTitle();
                    Intent shelterInfo = new Intent(getBaseContext(), ShelterInfo.class);
                    shelterInfo.putExtra("activity", "maps");
                    startActivity(shelterInfo);
                }
            }
        });
    }
    private void updateMarkers() {

        EditText searchString = findViewById(R.id.searchShelter);
        searchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                for (Marker m: shelterMarkers.values()) {
                    m.setVisible(true);
                }
                if ((charSequence != null) && (charSequence.length() != 0)) {
                    String search = charSequence.toString().toLowerCase();
                    Collection<Marker> visibleMarkers = new ArrayList<>();
                    for (Shelter s : shelters) {
                        if (s.getName().toLowerCase().contains(search)
                                && !visibleMarkers.contains(shelterMarkers.get(s.getName()))) {
                            visibleMarkers.add(shelterMarkers.get(s.getName()));
                        }
                        if ((s.getAge().toLowerCase().contains(search)
                                || s.getAge().toLowerCase().startsWith(search)
                                || s.getGender().contains("Anyone"))
                                && !visibleMarkers.contains(shelterMarkers.get(s.getName()))) {
                            visibleMarkers.add(shelterMarkers.get(s.getName()));
                        }
                        if (((s.getGender().toLowerCase().startsWith(search)
                                || (s.getGender().toLowerCase().contains("/")))
                                && (("male".equals(search)
                                || "female".equals(search))))
                                && ((!visibleMarkers.contains(shelterMarkers.get(s.getName()))))) {
                            visibleMarkers.add(shelterMarkers.get(s.getName()));
                        }
                    }
                    for (Marker m: shelterMarkers.values()) {
                        m.setVisible(false);
                    }
                    for (Marker m : visibleMarkers) {
                        m.setVisible(true);
                    }
                    visibleMarkers.clear();
                }
            }
                @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
