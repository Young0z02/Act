package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MemoListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Memo> memoList;
    private OnMemoClickListener listener;

    public MemoListAdapter(Context context, ArrayList<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.memo_list_item, parent, false);
        }

        Memo memo = memoList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView contentTextView = convertView.findViewById(R.id.content);
        TextView dateTextView = convertView.findViewById(R.id.date);

        titleTextView.setText(memo.getTitle());
        contentTextView.setText(memo.getContent());
        dateTextView.setText(memo.getDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMemoClick(position);
                }
            }
        });

        return convertView;
    }

    public void setOnMemoClickListener(OnMemoClickListener listener) {
        this.listener = listener;
    }

    public interface OnMemoClickListener {
        void onMemoClick(int position);
    }
}
