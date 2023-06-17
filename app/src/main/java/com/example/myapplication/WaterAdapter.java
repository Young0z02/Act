package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class WaterAdapter extends BaseAdapter {
    private Context context;
    private List<WaterMemo> waterList;

    public WaterAdapter(Context context, List<WaterMemo> memoList) {
        this.context = context;
        this.waterList = memoList;
    }


    @Override
    public int getCount() {
        return waterList.size();
    }

    @Override
    public Object getItem(int position) {
        return waterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_water, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        WaterMemo waterMemo = waterList.get(position);
        titleTextView.setText(waterMemo.getTitle());
        dateTextView.setText(waterMemo.getDate());
        messageTextView.setText(waterMemo.getMessage());

        return convertView;
    }
}
