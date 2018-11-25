package com.example.hh.caunavi_proto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class NaviPopupActivity extends Activity {

    private int building_ID;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Intent i = new Intent();
        i = getIntent();
        mode = i.getIntExtra("Mode",-1);
    }

    public void sendResult(){
        Intent i;
        if(mode == 1) {
            i = new Intent(this, ARActivity.class);
            i.putExtra("Build_id", getBuilding_ID());
            startActivity(i);
        }else if(mode == 0){
            i = new Intent();
            i.putExtra("result", building_ID);
            setResult(RESULT_OK, i);
        }else{

        }

        finish();
    }

    public void onClick(View v){
        Button b = (Button) v;
        building_ID = Integer.parseInt(b.getText().toString().substring(0,3));

        sendResult();
    }

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
