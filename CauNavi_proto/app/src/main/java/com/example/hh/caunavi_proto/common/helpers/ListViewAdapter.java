package com.example.hh.caunavi_proto.common.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hh.caunavi_proto.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListVO> listVO = new ArrayList<ListVO>();
    private Context context;

    public ListViewAdapter(Context context) {
        this.context = context;
    }
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
        convertView = inflater.inflate(R.layout.list_view,parent,false);

        ImageView image = (ImageView)convertView.findViewById(R.id.imageView);
        TextView textView = (TextView)convertView.findViewById(R.id.textView6);

        ListVO listViewItem = listVO.get(position);

        image.setImageDrawable(listViewItem.getImg());
        textView.setText(listViewItem.getTitle());

        return convertView;
    }

    public void addVO(Drawable icon, String title) {
        ListVO item = new ListVO();

        item.setImg(icon);
        item.setTitle(title);

        listVO.add(item);
    }
}
