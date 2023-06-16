package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.android.material.navigation.NavigationBarView;

public class SecondMainActivity extends AppCompatActivity {
    HomeFragment homeFragment;
    PlantInfoFragment plantInfoFragment;
    WateringFragment wateringFragment;
    WateringManagementFragment wateringManagementFragment;
    NavigationBarView navigationBarView;
    private MqttClient mqttClient;

    private String ServerIP = "tcp://223.195.194.41:1883";
    private String topic = "Aplant/water";
    private String message = "watering";

    private String clientId = MqttClient.generateClientId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity_main);

        homeFragment = new HomeFragment();
        plantInfoFragment = new PlantInfoFragment();
        wateringFragment = new WateringFragment();
        wateringManagementFragment = new WateringManagementFragment();

        // MQTT 클라이언트 초기화
        try {
            mqttClient = new MqttClient(ServerIP, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);

            // MQTT 클라이언트 콜백 설정
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // 연결이 끊어졌을 때의 동작 처리
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 메시지 수신시의 동작 처리
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 메시지 전달 완료시의 동작 처리
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
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
                        // MQTT 클라이언트를 통해 메시지 발행

                        try {
                            if (mqttClient != null) { // null 체크 추가
                                mqttClient.publish(topic, message.getBytes(), 0, false);
                            }
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 MQTT 클라이언트 연결 해제
        try {
            if (mqttClient != null) { // null 체크 추가
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
