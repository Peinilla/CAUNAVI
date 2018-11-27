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
    private ArrayList<Integer> nearBuildingIdList;

    public class buildingInfo {
        String name;
        int id;
        Location location = new Location("building");
    }

    public BuildingGuideManager(Context context) {
        mContext = context;
        AssetManager am = mContext.getResources().getAssets();
        InputStream is =  null;

        buildingList = new ArrayList<>();
        try{
            is = am.open("map/new_buildings.txt");
            BufferedReader bufrd = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            String line = bufrd.readLine();
            while((line = bufrd.readLine()) != null){
                String str[] = line.split("\t");
                buildingInfo b = new buildingInfo();
                b.name = str[3];
                b.id = Integer.parseInt(str[0]);
                b.location.setLatitude(Float.parseFloat(str[1]));
                b.location.setLongitude(Float.parseFloat(str[2]));

                buildingList.add(b);
            }
            bufrd.close();
        }catch (Exception e){
            Log.i("test", e.getMessage());
        }
    }

    public void setNearBuilding(double lat, double lon) {
        nearBuildingIdList = new ArrayList<>();

        Location currentLocation = new Location("Current");
        currentLocation.setLatitude(lat);
        currentLocation.setLongitude(lon);

        for (int inx = 0; inx < buildingList.size(); inx++) {
            if (currentLocation.distanceTo(buildingList.get(inx).location) < 80) {
                // 근처 건물은 5개까지만
                if(nearBuildingIdList.size() < 5) {
                    nearBuildingIdList.add(inx);
                }
            }
        }
    }

    public ArrayList<String> getBearingNearBuilding(double lat, double lon) {
        ArrayList<String> result = new ArrayList<>();

        Location currentLocation = new Location("Current");
        currentLocation.setLatitude(lat);
        currentLocation.setLongitude(lon);

        for (int inx = 0; inx < nearBuildingIdList.size(); inx++) {
            buildingInfo b = buildingList.get(nearBuildingIdList.get(inx));
            float bearing = currentLocation.bearingTo(b.location);
            if(bearing < 0){
                bearing += 360;
            }
            String str = b.name + "\t" + bearing;
            result.add(str);
        }

        return result;
    }
}
