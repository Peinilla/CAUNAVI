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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    public void sendResult(){
        Intent intent = new Intent();
        intent.putExtra("result", building_ID);
        setResult(RESULT_OK, intent);

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
}
