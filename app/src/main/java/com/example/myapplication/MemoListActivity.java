package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MemoListActivity extends AppCompatActivity implements MemoListAdapter.OnMemoClickListener {
    private ListView memoListView;
    private MemoListAdapter memoListAdapter;
    private DBHelper dbHelper;
    private Fragment homeFragment;
    private BottomNavigationView navigationBarView;
    private Fragment wateringFragment;
    private Fragment wateringManagementFragment;
    private Fragment plantInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_memo_list);

        memoListView = findViewById(R.id.memoListView);
        dbHelper = new DBHelper(this);

        ArrayList<Memo> memoList = (ArrayList<Memo>) dbHelper.getAllMemos();

        memoListAdapter = new MemoListAdapter(this, memoList);
        memoListAdapter.setOnMemoClickListener(this); // 리스너 설정
        memoListView.setAdapter(memoListAdapter);
        homeFragment = new HomeFragment();
        wateringFragment = new WateringFragment();
        wateringManagementFragment = new WateringManagementFragment();
        plantInfoFragment = new PlantInfoFragment();

        navigationBarView = findViewById(R.id.bottom_navigationView);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
                        break;
                    case R.id.watering:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, wateringFragment).commit();
                        break;
                    case R.id.watering_management:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, wateringManagementFragment).commit();
                        break;
                    case R.id.plant_info:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, plantInfoFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onMemoClick(int position) {
        Memo memo = (Memo) memoListAdapter.getItem(position);

        Intent intent = new Intent(this, MemoDetailActivity.class);
        intent.putExtra("memoId", memo.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_btn1) {
            // action_btn1 메뉴 아이템을 선택한 경우
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
