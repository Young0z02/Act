package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    private static final String ServerIP = "tcp://223.195.194.41:1883";
    private static final String TOPIC = "Aplant/water";

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

            mqttClient = new MqttClient(ServerIP, MqttClient.generateClientId(), null);
            mqttClient.connect();

            mqttClient = new MqttClient(ServerIP, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);

            // MQTT 클라이언트 콜백 설정
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // 연결이 끊어졌을 때의 동작 처리
                    Log.d("MQTT", "Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 메시지 수신시의 동작 처리
                    Log.d("MQTT", "Message arrived: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 메시지 전달 완료시의 동작 처리
                    Log.d("MQTT", "Delivery complete");
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
                        showWateringDialog();
                        performWatering();
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
    public void onDestroy() {
        super.onDestroy();
        if (mqttClient != null) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void performWatering() {
        if (mqttClient != null) {
            // MQTT 메시지 발행
            String message = "10ML"; // 발행할 메시지

            try {
                mqttClient.publish(TOPIC, message.getBytes(), 0, false);
                Toast.makeText(this, "물을 주었습니다.", Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    private void showWateringDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Watering");
        builder.setMessage("물을 주시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 다이얼로그 확인 버튼을 눌렀을 때의 동작 처리
                Toast.makeText(SecondMainActivity.this, "물을 주었습니다.", Toast.LENGTH_SHORT).show();
                performWatering();


           }
        });

        builder.show();
    }
}
