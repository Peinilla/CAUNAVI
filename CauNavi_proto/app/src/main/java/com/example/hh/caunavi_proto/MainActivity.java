package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.hh.caunavi_proto.common.helpers.BuildingDataHelper;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.button3 :
                startActivity(new Intent(this, NaviPopupActivity.class).putExtra("Mode",1));
                break;
            case R.id.button :
                // 캠퍼스투어 모드
                startActivity(new Intent(this, NaviPopupActivity.class).putExtra("Mode",0));
                break;
            case R.id.button4 :
                // 자유투어 모드
                Intent i = new Intent(this, ARActivity.class);
                i.putExtra("Mode", 1);
                startActivity(i);
                break;
            case R.id.button5 :
                startActivity(new Intent(this,InfoActivity.class));
                break;
        }
    }
}