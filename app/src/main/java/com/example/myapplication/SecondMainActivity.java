package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationBarView;

public class SecondMainActivity extends AppCompatActivity {
    HomeFragment homeFragment;
    PlantInfoFragment plantInfoFragment;
    WateringFragment wateringFragment;
    WateringManagementFragment wateringManagementFragment;
    NavigationBarView navigationBarView;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.secondactivity_main);

        homeFragment = new HomeFragment();
        plantInfoFragment = new PlantInfoFragment();
        wateringFragment = new WateringFragment();
        wateringManagementFragment = new WateringManagementFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationView);
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
