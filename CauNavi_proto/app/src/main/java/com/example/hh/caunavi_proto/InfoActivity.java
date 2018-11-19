package com.example.hh.caunavi_proto;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    private ListView listView;
    private ListViewAdapter adapter;
    private int[] img = {R.drawable.c_101_1, R.drawable.c_102_1, R.drawable.c_103_1, R.drawable.c_104_1, R.drawable.c_105_1, R.drawable.c_106_1, R.drawable.c_107_1,
            R.drawable.c_201_1, R.drawable.c_202_1, R.drawable.c_203_1, R.drawable.c_204_1, R.drawable.c_207_1, R.drawable.c_208_1, R.drawable.c_209_1, R.drawable.c_301_1,
            R.drawable.c_302_1, R.drawable.c_303_1, R.drawable.c_304_1, R.drawable.c_305_1, R.drawable.c_308_1, R.drawable.c_309_1, R.drawable.c_310_1};
    private String[] name = {"101관(영신관)", "102관(약학대학 및 R&D센터)", "103관(파이퍼홀)", "104관(수림과학관)", "105관(제1의학관)", "106관(제2의학관)",
            "107관(학생회관)", "201관(본관)", "202관(전산정보관)", "203관(서라벌홀)", "204관(중앙도서관)", "207관(봅스트홀)", "208관(제2공학관)", "209관(창업보육관)",
            "301관(중앙문화예술관)", "302관(대학원)", "303관(법학관)", "304관(미디어공연영상관)", "305관(교수연구동 및 체육관)","308관(블루미르홀308)",
            "309관(블루미르홀309)", "310관(100주년기념관)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        adapter = new ListViewAdapter(this);
        listView = (ListView)findViewById(R.id.listView);

        listView.setAdapter(adapter);

        for(int i=0; i<img.length;i++) {
            adapter.addVO(ContextCompat.getDrawable(this,img[i]),name[i]);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),InfoViewActivity.class);
                intent.putExtra("b_id",name[position]);

                startActivity(intent);
            }
        });
    }
}

//    public void onClick(View v) {
//
//        Intent intent = new Intent(this, InfoViewActivity.class);
//        Button b = (Button) v;
//
//        intent.putExtra("b_id",b.getText());
//
//        startActivity(intent);
//
//    }
//}
