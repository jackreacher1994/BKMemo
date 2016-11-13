package com.jackreacher.bkmemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String addressOutput;
    private ImageButton btContinue;
    private EditText etLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btContinue = (ImageButton) findViewById(R.id.continue_button);
        btContinue.setEnabled(false);
        etLocation = (EditText) findViewById(R.id.etLocation);
    }

    public void onMapSearch(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etLocation.getWindowToken(), 0);

        EditText locationSearch = (EditText) findViewById(R.id.etLocation);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            addressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(addressOutput))
                    .showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            btContinue.setEnabled(true);
        }
    }

    public void continueAddPlace(View view){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, PlaceAddActivity.class);
        bundle.putDouble("latitude", Double.valueOf(latitude));
        bundle.putDouble("longitude", Double.valueOf(longitude));
        bundle.putString("address", addressOutput);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //mMap.setMyLocationEnabled(true);
    }
}