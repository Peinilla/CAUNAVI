package com.example.hh.caunavi_proto;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapManager {

    Context mContext;
    private ArrayList<mapData> mapDataArrayList = new ArrayList<>();
    private ArrayList<Integer> route = new ArrayList<>();
    private int destinationID;
    private int currentID;
    private int distanceToCurrentID;

    public class mapData {
        Location location = new Location("map");
        int id;
        String name;
    }

    public MapManager(Context context){
        mContext = context;
        AssetManager am = mContext.getResources().getAssets();
        InputStream is =  null;

        try{
            is = am.open("map/backGate_to_208.txt");
            BufferedReader bufrd = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            String line = bufrd.readLine();
            while((line = bufrd.readLine()) != null){
                String str[] = line.split("\t");

                mapData md = new mapData();
                md.location.setLatitude(Double.valueOf(str[0]));
                md.location.setLongitude(Double.valueOf(str[1]));
                md.id = Integer.valueOf(str[2]);
                md.name = str[3];
                mapDataArrayList.add(md);
            }

            bufrd.close();
            is.close();

        }catch (Exception e){
            Log.i("test", e.getMessage());
        }
    }

    public void setDestination(int destinationID){
        this.destinationID = destinationID;

        // 테스트용
        route.add(0);
        route.add(1);
        route.add(2);
        route.add(3);
        route.add(4);
        route.add(5);
        route.add(6);
        route.add(7);
        //
    }

    public void setCurrentID(double lat, double lon){
        Location tempLoc = new Location("temp");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lon);

        int distance = Integer.MAX_VALUE;
        int currentID = -1;

        for(int inx = 0; inx < mapDataArrayList.size(); inx ++ ){
            int tempDis = (int)mapDataArrayList.get(inx).location.distanceTo(tempLoc);
            if(distance > tempDis){
                distance = tempDis;
                currentID = mapDataArrayList.get(inx).id;
            }
        }
        if(currentID != -1){
            this.currentID = currentID;
            distanceToCurrentID = distance;
        }
        //위도와 경도를 이용해 현재위치와 가장 가까운 지점의 ID를 설정
    }

    public float getNextPointBearing(double lat, double lon){
        setCurrentID(lat,lon);

        Location tempLoc = new Location("temp");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lon);

        int nextPoint = getnextPoint();
        return tempLoc.bearingTo(mapDataArrayList.get(nextPoint).location);
    }

    public int getnextPoint(){
        for(int inx = 0; inx < route.size(); inx ++){
            if(route.get(inx) == currentID){
                return route.get(inx + 1);
            }
            else{
                //현재 위치가 경로에 없을 경우, 경로 재탐색
            }
        }
        Log.i("test","현재 위치 ID : " + currentID);
        return currentID;
    }
}


