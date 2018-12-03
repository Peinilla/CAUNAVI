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

public class MapManager {

    private Context mContext;
    private ArrayList<mapData> mapDataArrayList = new ArrayList<>();
    private ArrayList<Integer> route = new ArrayList<>();

    private int destinationID;
    private int nearPointID;
    private int nextPointID;
    private int prevPointID;
    private int[][] map;
    private int length;

    private boolean isDestination;
    private static Toast mToast;

    public class mapData {
        Location location = new Location("map");
        int index;
        int id;
        String name;
    }

    public MapManager(Context context){
        mContext = context;
        AssetManager am = mContext.getResources().getAssets();
        InputStream is =  null;
        Toast mToast = new Toast(mContext.getApplicationContext());

        try{
            is = am.open("map/new_map.txt");
            BufferedReader bufrd = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            String line = bufrd.readLine();
            while((line = bufrd.readLine()) != null){
                String str[] = line.split("\t");

                mapData md = new mapData();
                md.index = Integer.valueOf(str[0]);
                md.location.setLatitude(Double.valueOf(str[1]));
                md.location.setLongitude(Double.valueOf(str[2]));
                md.id = Integer.valueOf(str[3]);
                md.name = str[4];
                mapDataArrayList.add(md);
            }
            bufrd.close();

            is = am.open("map/new_graph.txt");
            bufrd = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            line = bufrd.readLine();
            line = bufrd.readLine();
            String str[] = line.split("\t");
            length = str.length;
            map = new int[length][length];
            for(int inx = 0; inx < length; inx ++){
                map[0][inx] = Integer.parseInt(str[inx]);
            }
            for(int inx = 1; inx < length; inx ++){
                line = bufrd.readLine();
                String str2[] = line.split("\t");
                for(int jnx = 0; jnx < length; jnx++){
                    map[inx][jnx] = Integer.parseInt(str2[jnx]);
                }
            }

            bufrd.close();

        }catch (Exception e){
            Log.i("test", e.getMessage());
        }
        init();
    }
    public void init() {
        destinationID = -1;
        nearPointID = 0;
        nextPointID = 0;
        prevPointID = 0;
        isDestination = false;
    }

    public void setDestination(int destination, double lat, double lon){
        setNearPointID(lat,lon);
        destinationID = getDestinationID(destination);
        nextPointID = nearPointID;
        prevPointID = nearPointID;

        if(destinationID == -1){
            isDestination = false;
            return;
        }

        route = new ArrayList<>();
        route = getRoute(nearPointID,destinationID);
        if(mToast != null) {
            mToast.cancel();
        }
        Log.i("test", "near : " + nearPointID + "/lat : " + lat);

        Log.i("test", route.toString());

        isDestination = true;
    }
    public void reSearchDest(){
        nextPointID = nearPointID;
        prevPointID = nearPointID;

        route = new ArrayList<>();
        route = getRoute(nearPointID,destinationID);

        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext.getApplicationContext(),"경로 재탐색중", Toast.LENGTH_LONG);
        mToast.show();

        Log.i("test", "near : " + nearPointID);

        Log.i("test", route.toString());
    }

    public void setNearPointID(double lat, double lon){
        Location tempLoc = new Location("temp");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lon);

        int distance = Integer.MAX_VALUE;
        int nearID = -1;

        for(int inx = 0; inx < mapDataArrayList.size(); inx ++ ){
            int tempDis = (int)mapDataArrayList.get(inx).location.distanceTo(tempLoc);
            if(distance > tempDis){
                distance = tempDis;
                nearID = mapDataArrayList.get(inx).index;
            }
        }
        if(nearID != -1){
            nearPointID = nearID;
        }
        //위도와 경도를 이용해 현재위치와 가장 가까운 지점의 ID를 설정
    }

    public float getNextPointBearing(double lat, double lon){
        setNearPointID(lat,lon);

        Location tempLoc = new Location("temp");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lon);

        int distNext = (int) tempLoc.distanceTo(mapDataArrayList.get(nextPointID).location);
        if(distNext > 8){
            if(nextPointID != nearPointID && prevPointID != nearPointID){
                setDestination(destinationID,lat,lon); // 경로 재설정
                return getNextPointBearing(lat,lon);
            } else{
                String nameNext = mapDataArrayList.get(nextPointID).name;
                float bearing = tempLoc.bearingTo(mapDataArrayList.get(nextPointID).location);
                return bearing;
            }
        }else{
            prevPointID = nextPointID;
            nextPointID = getnextPoint();
            return tempLoc.bearingTo(mapDataArrayList.get(nextPointID).location);
        }
    }

    // 현재 위치에서 다음목적지가 아닌, 지나온 목적지에서 다음 목적지로
    public float getNextBearingTest(double lat, double lon){
        setNearPointID(lat,lon);

        Location tempLoc = new Location("temp");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lon);

        int distNext = (int) tempLoc.distanceTo(mapDataArrayList.get(nextPointID).location);
        if(distNext > 12){
            if(nextPointID != nearPointID && prevPointID != nearPointID){
                // 경로재탐색
                reSearchDest();
                return getNextBearingTest(lat,lon);
            } else if(prevPointID == nextPointID){
                // 첫번째 포인트
                String namePrev = mapDataArrayList.get(prevPointID).name;
                String nameNext = mapDataArrayList.get(nextPointID).name;
                float bearing = tempLoc.bearingTo(mapDataArrayList.get(nextPointID).location);

                if(bearing < 0){
                    bearing += 360;
                }
                return bearing;
            }else{
                String namePrev = mapDataArrayList.get(prevPointID).name;
                String nameNext = mapDataArrayList.get(nextPointID).name;
                float bearing = mapDataArrayList.get(prevPointID).location.bearingTo(mapDataArrayList.get(nextPointID).location);

                if(bearing < 0){
                    bearing += 360;
                }
                return bearing;
            }
        }else{
            prevPointID = nextPointID;
            if(nextPointID != getnextPoint()){
                nextPointID = getnextPoint();
                return getNextBearingTest(lat,lon);
            }
            else{
                float bearing = mapDataArrayList.get(prevPointID).location.bearingTo(mapDataArrayList.get(nextPointID).location);
                if(bearing < 0){
                    bearing += 360;
                }
                return bearing;
            }
        }
    }

    public int getnextPoint(){
        int index = route.indexOf(nearPointID);
        Log.i("test",index + "getNext Index");
        if(index + 1 != route.size()){
            return route.get(index+1);
        }else{
            return route.get(index);
        }
    }

    public int getDestinationID(int destination){
        try {
            ArrayList<Integer> candidate = new ArrayList<>();

            for (int inx = 0; inx < mapDataArrayList.size(); inx++) {
                if (mapDataArrayList.get(inx).id == destination) {
                    candidate.add(mapDataArrayList.get(inx).index);
                }
            }
            int resultID = candidate.get(0);
            int min = getRoute(nearPointID, candidate.get(0)).size();
            for (int inx = 0; inx < candidate.size(); inx++) {
                if (getRoute(nearPointID, candidate.get(inx)).size() < min) {
                    min = getRoute(nearPointID, candidate.get(inx)).size();
                    resultID = candidate.get(inx);
                }
            }
            return resultID;
        }catch (Exception e){
            Log.i("test",e.getMessage());
        }

        return -1;
    }

    public ArrayList<Integer> getRoute(int start, int end) {

        ArrayList<Integer> route = new ArrayList<>();

        int dist[] = new int[length];
        boolean visit[] = new boolean[length];
        int inf = Integer.MAX_VALUE;
        int prev[] = new int[length];
        int stack[] = new int[length];

        for(int inx = 0; inx <length; inx++){
            dist[inx] = inf;
            prev[inx] = 0;
            visit[inx] = false;
        }

        dist[start] = 0;

        for(int inx = 0; inx < length; inx ++){
            int min = inf;
            int tmp = 0;
            for(int jnx = 0; jnx < length; jnx ++){
                if(!visit[jnx] && min > dist[jnx]){
                    min = dist[jnx];
                    tmp = jnx;
                }
            }
            visit[tmp] = true;

            for(int jnx = 0; jnx < length; jnx++){
                if(map[tmp][jnx] != 0 && dist[jnx] > dist[tmp] + map[tmp][jnx]){
                    dist[jnx] = dist[tmp] + map[tmp][jnx];
                    prev[jnx] = tmp;
                }
            }
        }
        int jnx = 0;
        int inx = end;

        while(true){
            stack[jnx++] = inx;
            if(inx == start){
                break;
            }
            inx = prev[inx];
        }

        for(int mnx = jnx-1;mnx > -1; mnx--){
            route.add(stack[mnx]);
        }

        return route;
    }

    public String getNearPoint(){
        return mapDataArrayList.get(nearPointID).name;
    }

    public boolean isArrivalDest(){
        if(!isDestination){
            return false;
        }else {
            return (nearPointID == destinationID);
        }
    }
}


