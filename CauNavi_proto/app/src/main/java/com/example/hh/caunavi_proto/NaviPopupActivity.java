package com.example.hh.caunavi_proto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class NaviPopupActivity extends Activity {

    private int building_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("test","1");
        setContentView(R.layout.activity_navipopup);
        Log.i("test","2");

    }

    public void sendResult(){
        Intent intent = new Intent();
        intent.putExtra("result", building_ID);
        setResult(RESULT_OK, intent);
    }
}
