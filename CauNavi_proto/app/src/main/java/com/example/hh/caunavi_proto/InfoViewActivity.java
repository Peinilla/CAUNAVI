package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hh.caunavi_proto.common.helpers.Adapter;
import com.example.hh.caunavi_proto.common.helpers.BuildingDataHelper;
import com.example.hh.caunavi_proto.common.helpers.BuildingData;

public class InfoViewActivity extends AppCompatActivity {

    private Intent intent;
    private String tmp;
    private int ID;
    private TextView name;
    private TextView content;

    private int[] images = new int[3];
    private int[] images2 = new int[2];
    private ViewPager viewPager;
    private Adapter adapter;

    private BuildingData d;
    private BuildingDataHelper bdh;
    private Button startGuideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_view);


        intent = getIntent();
        tmp = intent.getStringExtra("b_id");
        ID = Integer.parseInt(tmp.substring(0,3));
        bdh = BuildingDataHelper.getInstance(this);
        d = (BuildingData)bdh.getBuildingData(ID);

        name = (TextView)findViewById(R.id.textView);
        content = (TextView)findViewById(R.id.textView2);

        name.setText(""+ ID +"관\n"+d.getName());
        name.setTextSize(30.0f);
        content.setText(d.getText());
        content.setTextSize(24.0f);


        if(ID==209 || ID==304 || ID==309) {
            for (int i = 1; i <= 2; i++) {
                images2[i - 1] = getApplicationContext().getResources().getIdentifier("c_" + ID + "_" + i, "drawable", "com.example.hh.caunavi_proto");
                viewPager = (ViewPager) findViewById(R.id.view);
                adapter = new Adapter(this, images2);
                viewPager.setAdapter(adapter);
            }
        }

        else {
            for (int i = 1; i <= 3; i++) {
                images[i - 1] = getApplicationContext().getResources().getIdentifier("c_" + ID + "_" + i, "drawable", "com.example.hh.caunavi_proto");
                viewPager = (ViewPager) findViewById(R.id.view);
                adapter = new Adapter(this, images);
                viewPager.setAdapter(adapter);
            }
        }

        startGuideButton = (Button)findViewById(R.id.startGuideButton);
        startGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGuide(view);
            }
        });
    }

    public void startGuide(View v){
        Intent i = new Intent(this,ARActivity.class);
        i.putExtra("Build_id",ID);
        startActivity(i);
    }
}
