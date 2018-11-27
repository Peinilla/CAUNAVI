package com.example.hh.caunavi_proto.common.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hh.caunavi_proto.R;

import java.util.ArrayList;

public class destinationAdapter extends BaseAdapter {
    private ArrayList<ListVO> listVO = new ArrayList<ListVO>();
    private Context context;

    public destinationAdapter(Context context) { this.context = context;}

    @Override
    public int getCount() {
        return listVO.size();
    }

    @Override
    public Object getItem(int position) {
        return listVO.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.destination_list,parent,false);

        TextView textView = (TextView)convertView.findViewById(R.id.textView7);

        ListVO listViewItem = listVO.get(position);

        textView.setText(listViewItem.getTitle());

        return convertView;
    }

    public void addVO(String title) {
        ListVO item = new ListVO();

        item.setTitle(title);

        listVO.add(item);
    }
}
