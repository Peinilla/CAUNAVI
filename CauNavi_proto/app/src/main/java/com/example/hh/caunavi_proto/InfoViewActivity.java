package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hh.caunavi_proto.common.helpers.BuildingDataHelper;
import com.example.hh.caunavi_proto.common.helpers.BuildingData;



import com.example.hh.caunavi_proto.R;

import org.w3c.dom.Text;

import java.io.InputStream;

public class InfoViewActivity extends AppCompatActivity {

    private Intent intent;
    private String tmp;
    private int ID;
    private TextView name;
    private TextView content;
    private ImageView imgV;

    AssetManager am;
    InputStream is;

    private BuildingData d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_view);

        intent = getIntent();
        tmp = intent.getStringExtra("b_id");
        ID = Integer.parseInt(tmp.substring(0,3));
        d = (BuildingData)new BuildingDataHelper(this).getBuildingData(ID);

        name = (TextView)findViewById(R.id.textView);
        content = (TextView)findViewById(R.id.textView2);
        imgV = (ImageView)findViewById(R.id.imageView);

        name.setText(""+ ID +"관\n"+d.getName());
        content.setText(d.getText());

        String path = "img/"+d.getImageStr();

        try {
          am = this.getResources().getAssets();
          is = am.open(path);

          Bitmap bitmap = BitmapFactory.decodeStream(is);

          imgV.setImageBitmap(bitmap);

          is.close();
        }catch (Exception e){
            Log.i("test",e.getMessage());
        }
    }
}
