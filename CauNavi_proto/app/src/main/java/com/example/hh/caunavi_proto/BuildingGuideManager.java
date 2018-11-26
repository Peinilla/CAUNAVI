package com.example.hh.caunavi_proto;

import android.content.Context;
import android.location.Location;

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

    public void setNearBuilding(double lat, double lon) {
        nearBuildingIdList = new ArrayList<>();

        Location currentLocation = new Location("Current");
        currentLocation.setLatitude(lat);
        currentLocation.setLatitude(lon);

        for (int inx = 0; inx < buildingList.size(); inx++) {
            if (currentLocation.distanceTo(buildingList.get(inx).location) > 80) {
                nearBuildingIdList.add(inx);
            }
        }
    }

    public ArrayList<String> getBearingNearBuilding(double lat, double lon) {
        ArrayList<String> result = new ArrayList<>();

        Location currentLocation = new Location("Current");
        currentLocation.setLatitude(lat);
        currentLocation.setLatitude(lon);

        for (int inx = 0; inx < nearBuildingIdList.size(); inx++) {
            buildingInfo b = buildingList.get(nearBuildingIdList.get(inx));
            String str = b.name + "\t" + currentLocation.bearingTo(b.location);
            result.add(str);
        }

        return result;
    }
}
