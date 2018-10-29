package com.example.hh.caunavi_proto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InfoActivity extends AppCompatActivity {
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getCurrentFocus();
        setContentView(R.layout.activity_info);
    }



    @Override
    public void onBackPressed() {
        if(this.view == getCurrentFocus())
            super.onBackPressed();
        else
            setContentView(R.layout.activity_info);
    }
}
