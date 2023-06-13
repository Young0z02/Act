package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MemoListActivity extends AppCompatActivity implements MemoListAdapter.OnMemoClickListener {
    private ListView memoListView;
    private MemoListAdapter memoListAdapter;
    private DBHelper dbHelper;
    private Fragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        memoListView = findViewById(R.id.memoListView);
        dbHelper = new DBHelper(this);

        ArrayList<Memo> memoList = (ArrayList<Memo>) dbHelper.getAllMemos();

        memoListAdapter = new MemoListAdapter(this, memoList);
        memoListAdapter.setOnMemoClickListener(this); // 리스너 설정
        memoListView.setAdapter(memoListAdapter);
        homeFragment = new HomeFragment();
    }

    @Override
    public void onMemoClick(int position) {
        Memo memo = (Memo) memoListAdapter.getItem(position);

        Intent intent = new Intent(this, MemoDetailActivity.class);
        intent.putExtra("memoId", memo.getId());
        startActivity(intent);
    }
}

