package com.arthur.memorableplaces;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<String>listViewOptions;
    static ArrayAdapter<String>arrayAdapter;
    Intent mapI;

    //Need a Structure to store the lat and Lng. Each should store 1. Name, 2. Lat, 3. Lng]
    static ArrayList<String>locationNames;
    static ArrayList<LatLng>locationCoords;

    private void setUp(){
        locationNames=new ArrayList<String>();
        locationCoords=new ArrayList<LatLng>();
        listView=(ListView)findViewById(R.id.placesList);
        listViewOptions=new ArrayList<String>();
        listViewOptions.add("Add Location");
        //everytime location changes it should add another location...
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listViewOptions);
        listView.setAdapter(arrayAdapter);
        mapI=new Intent(getApplicationContext(),MapsActivity.class);
        Geocoder geoCoder=new Geocoder(getApplicationContext(), Locale.CANADA);
        String addr="";
        try {
            List<Address>listAddress=geoCoder.getFromLocation(49.288884, -123.110761,1);
            if(listAddress!=null&&listAddress.size()>0){
                Address address=listAddress.get(0);
                for(int i=0;i<address.getMaxAddressLineIndex();i++){
                    if(i==0){
                        addr+=address.getAddressLine(i);
                    }
                    else{
                        addr=addr+", "+address.getAddressLine(i);
                    }
                }
            }
            mapI.putExtra("address",addr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listViewOptions.add("Canada Place");
        locationNames.add("Canada Place");
        locationCoords.add(new LatLng(49.288884, -123.110761));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    //If position =0 --> Add Place! Should go to Second Activity.
                    mapI.putExtra("action",0);
                    startActivity(mapI);
                }
                else{
                    //Select Location --> Go to Map and Zoom in on location.
                    mapI.putExtra("action",position);
                    mapI.putExtra("locationName", locationNames.get(position - 1));
                    mapI.putExtra("latitude", locationCoords.get(position - 1).latitude);
                    mapI.putExtra("longtitude",locationCoords.get(position-1).longitude);
                    startActivity(mapI);

                }
            }
        });

    }
    protected void addLocation(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();
    }
}
