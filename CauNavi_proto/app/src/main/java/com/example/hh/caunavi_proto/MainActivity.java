package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hh.caunavi_proto.common.helpers.BuildingDataHelper;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.main_popup);
        builder.setView(imageView);

        builder.setTitle("스마트폰 나침반 초기화를 위해\n스마트폰을 8자로 회전시켜주세요.");
        builder.show();

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