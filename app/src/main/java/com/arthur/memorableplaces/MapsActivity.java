package com.arthur.memorableplaces;

//import android.app.ActionBar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    ActionBar actionBar;
    int requestedAction;
    Intent requestedI;

    LocationManager locationManager;
    String provider;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUp() {

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } else Log.i("NullBar", "There is no Action Bar");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        requestedI = getIntent();
        //this retrieves the action desired by the Main Activity --Defaults to -1 if failed.
        requestedAction = requestedI.getIntExtra("action", -1);
    }

    private void doAction() {

    }

    private void showOnMap() {
        LatLng locationCoord = new LatLng(requestedI.getDoubleExtra("latitude", 0), requestedI.getDoubleExtra("longtitude", 0));
        String locationName = requestedI.getStringExtra("locationName");
        String locationAddr = requestedI.getStringExtra("address");
        //Log.i("ExtraInfo",requestedI.getIntExtra("action",-1)+","+requestedI.getStringExtra("locationName")+","+requestedI.getDoubleExtra("latitude",0)+","+requestedI.getDoubleExtra("longtitude",0));

        mMap.addMarker(new MarkerOptions().position(locationCoord).title(locationName + ":" + locationAddr).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoord, 10));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUp();

        //Else it's the Add Location Option. Do nothing and wait on Location Change...
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
        if (requestedAction == -1) {
            Toast.makeText(getApplicationContext(), "Something went wrong with action", Toast.LENGTH_LONG).show();
        } else if (requestedAction > 0) {
            //If We're just Viewing the Address
            showOnMap();
        }
        else if(requestedAction==0){
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Geocoder chosenAddr=new Geocoder(getApplicationContext(), Locale.CANADA);
                    String addrString=new Date().toString();
                    try {
                        List<Address>listAddress=chosenAddr.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        if(listAddress!=null&&listAddress.size()>0){
                            Address address=listAddress.get(0);
                            for(int i=0;i<address.getMaxAddressLineIndex();i++){
                                if(i==0){
                                    addrString=address.getAddressLine(i);
                                }
                                else{
                                    addrString=addrString+", "+address.getAddressLine(i);
                                }
                            }
                        }
                        mMap.addMarker(new MarkerOptions().position(latLng).title(addrString));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MainActivity.listViewOptions.add(addrString);
                    MainActivity.locationCoords.add(latLng);
                    MainActivity.locationNames.add(addrString);
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                }
            });
        }
        // Add a marker in Sydney and move the camera

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Do nothing....
        }
        locationManager.removeUpdates(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        locationManager.requestLocationUpdates(provider, 1000, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat=location.getLatitude();
        Double lng=location.getLongitude();
        if(requestedAction==0){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),20));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
