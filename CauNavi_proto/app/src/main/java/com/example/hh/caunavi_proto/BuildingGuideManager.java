package com.example.hh.caunavi_proto;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class BuildingGuideManager {

    private Context mContext;
    private ArrayList<buildingInfo> buildingList;

    public class buildingInfo {
        String name;
        int id;
        Location location = new Location("building");
    }

    public BuildingGuideManager(Context context) {
        mContext = context;

        buildingList = new ArrayList<>();

        ///test
        buildingInfo b = new buildingInfo();
        b.name = "310관";
        b.id = 310;
        b.location.setLatitude(37.503835);
        b.location.setLongitude(126.955869);

        buildingList.add(b);

        b = new buildingInfo();
        b.name = "208관";
        b.id = 208;
        b.location.setLatitude(37.503552);
        b.location.setLongitude(126.957046);
        buildingList.add(b);
        ///
    }

    public ArrayList<String> getBearingNearBuilding(double lat, double lon) {
        ArrayList<String> result = new ArrayList<>();

        Location currentLocation = new Location("Current");
        currentLocation.setLatitude(lat);
        currentLocation.setLatitude(lon);

        for (int inx = 0; inx < buildingList.size(); inx++) {
            if (currentLocation.distanceTo(buildingList.get(inx).location) < 80) {
                String str = buildingList.get(inx).name + "\t" + currentLocation.bearingTo(buildingList.get(inx).location);
            }
        }

        return result;
    }
}
