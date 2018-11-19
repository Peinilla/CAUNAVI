package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    public void onClick(View v) {

        Intent intent = new Intent(this, InfoViewActivity.class);
        Button b = (Button) v;

        intent.putExtra("b_id",b.getText());

        startActivity(intent);

    }
}
