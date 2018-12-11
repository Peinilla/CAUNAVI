package com.example.hh.caunavi_proto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hh.caunavi_proto.common.helpers.destinationAdapter;

public class NaviPopupActivity extends Activity {

    private int building_ID;
    private int mode;
    private ListView listView;
    private destinationAdapter adapter;
    private String[] name = {"101관(영신관)", "102관(약학대학 및 R&D센터)", "103관(파이퍼홀)", "104관(수림과학관)", "105관(제1의학관)", "106관(제2의학관)",
            "107관(학생회관)", "201관(본관)", "202관(전산정보관)", "203관(서라벌홀)", "204관(중앙도서관)", "207관(봅스트홀)", "208관(제2공학관)", "209관(창업보육관)",
            "301관(중앙문화예술관)", "302관(대학원)", "303관(법학관)", "304관(미디어공연영상관)", "305관(교수연구동 및 체육관)","308관(블루미르홀308)",
            "309관(블루미르홀309)", "310관(100주년기념관)"};
    private int route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent i = new Intent();
        i = getIntent();
        mode = i.getIntExtra("Mode",-1);
        // 0 : 메인에서 진입 , 1 : 메인에서 캠퍼스투어 , 2 : AR에서 진입
        adapter = new destinationAdapter(this);
        listView = (ListView)findViewById(R.id.listView);

        listView.setAdapter(adapter);

        Log.i("current mode : ", String.valueOf(mode));

        if(mode == 0){
            name = new String[] {"정문에서", "후문에서","시연용"};
        }else if(mode == 1){

        }

        for(int num=0; num<name.length; num++) {
            adapter.addVO(name[num]);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mode == 0) {
                    route = position;
                    sendResult();
                }else if(mode == 1){
                    building_ID = Integer.parseInt(name[position].substring(0, 3));

                    Log.i("checkbuild ", String.valueOf(building_ID));

                    sendResult();
                }else if(mode == 2){
                    building_ID = Integer.parseInt(name[position].substring(0, 3));

                    sendResult();
                }
            }
        });
    }

    public void sendResult(){
        Intent i;
        if(mode == 0) {
            i = new Intent(this, ARActivity.class);
            i.putExtra("CampusTourRoute", route);
            i.putExtra("Mode", 0);
            startActivity(i);
        }else if(mode == 1){
            i = new Intent(this, ARActivity.class);
            i.putExtra("Build_id", getBuilding_ID());
            startActivity(i);
        }
        else if(mode == 2){
            i = new Intent();
            i.putExtra("result", building_ID);
            setResult(RESULT_OK, i);
        }else{

        }

        finish();
    }

//    public void onClick(View v){
//        Button b = (Button) v;
//        building_ID = Integer.parseInt(b.getText().toString().substring(0,3));
//
//        sendResult();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public int getBuilding_ID(){
        int id;

//      청룡탕, 의혈탑 체크
//        if(building_ID != ){
//
//        }
//
        id = building_ID;

        return id;
    }
}
