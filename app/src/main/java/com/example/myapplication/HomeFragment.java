package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private String ServerIP = "";
    private String TOPIC_TEMP = "sensor/temp";
    private String TOPIC_HUM = "sensor/hum";
    private String TOPIC_SOIL_HUM = "sensor/soilhum";
    private String TOPIC_WATER = "Aplant/water";

    private MqttClient mqttClient = null;
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView soilHumidityTextView;

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button memolistButton;
    private Button wateringButton;
    private DBHelper dbHelper;
    private int memoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        temperatureTextView = rootView.findViewById(R.id.message1);
        humidityTextView = rootView.findViewById(R.id.message2);
        soilHumidityTextView = rootView.findViewById(R.id.message3);

        titleEditText = rootView.findViewById(R.id.title);
        contentEditText = rootView.findViewById(R.id.content);
        saveButton = rootView.findViewById(R.id.save);
        memolistButton = rootView.findViewById(R.id.memolist);
        wateringButton = rootView.findViewById(R.id.watering);

        dbHelper = new DBHelper(getActivity());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                // 현재 시간
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());

                // 데이터베이스에 메모를 저장
                long id = dbHelper.insertMemo(new Memo(memoId, title, content, date));
                if (id != -1) {
                    Toast.makeText(getActivity(), "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 메모가 저장된 후 메모 목록 화면으로 이동
                    Intent intent = new Intent(getActivity(), MemoListActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "메모 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        memolistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemoListActivity.class);
                startActivity(intent);
            }
        });

        wateringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("물주기 확인");
                builder.setMessage("물을 주시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 클릭한 경우, 물주기 동작 수행
                        performWatering();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", null);

                // wateringFragment로 전환하는 코드
                Fragment wateringFragment = new WateringFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containers, wateringFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // 네비게이션 바에서 "watering" 아이템 선택
                BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigationView);
                navigationView.setSelectedItemId(R.id.watering);

            }
        });

        try {
            mqttClient = new MqttClient(ServerIP, MqttClient.generateClientId(), null);
            mqttClient.connect();

            mqttClient.subscribe(TOPIC_TEMP);
            mqttClient.subscribe(TOPIC_HUM);
            mqttClient.subscribe(TOPIC_SOIL_HUM);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d("MQTTService", "Connection Lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    String message = mqttMessage.toString();
                    handleMQTTMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.d("MQTTService", "Delivery Complete");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void updateSensorData(String temperature, String humidity, double soilHumidity) {
        // 온도 표시
        String temp = String.format(Locale.getDefault(), "%.1f ℃", temperature);
        temperatureTextView.setText(temp);

        // 습도 표시
        String hum = String.format(Locale.getDefault(), "%.1f %%", humidity);
        humidityTextView.setText(hum);
    }

    private void handleMQTTMessage(String topic, String message) {
        String temperature = "";
        String humidity = "";
        if (topic.equals(TOPIC_TEMP)) {
            temperature = message;
            displayTemperature(temperature);
        } else if (topic.equals(TOPIC_HUM)) {
            humidity = message;
            displayHumidity(humidity);
        } else if (topic.equals(TOPIC_SOIL_HUM)) {
            double soilHumidity = Double.parseDouble(message);
            displaySoilHumidity(soilHumidity);
            updateSensorData(temperature, humidity, soilHumidity); // 센서 데이터 업데이트
        }
    }

    private void displayTemperature(String temperature) {
        String temp = String.format(Locale.getDefault(), temperature);
        temperatureTextView.setText(temp);
    }

    private void displayHumidity(String humidity) {
        String hum = String.format(Locale.getDefault(), humidity);
        humidityTextView.setText(hum);
    }

    private void displaySoilHumidity(double soilHumidity) {
        String numOfDroplets;

        // 물방울 개수를 토양 수분량에 따라 조정
        if (soilHumidity >= 0 && soilHumidity < 20)
            numOfDroplets = "";
        else if (soilHumidity >= 4000 && soilHumidity < 5000)
            numOfDroplets = "💧";
        else if (soilHumidity >= 3000 && soilHumidity < 3999)
            numOfDroplets = "💧💧";
        else if (soilHumidity >= 2000 && soilHumidity < 2999)
            numOfDroplets = "💧💧💧";
        else
            numOfDroplets = "💧💧💧";

        soilHumidityTextView.setText(numOfDroplets);

        String soilHum = String.format(Locale.getDefault(), "%d", numOfDroplets);
        soilHumidityTextView.setText(soilHum);
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
        // MQTT 메시지 발행
        String message = "10ML"; // 발행할 메시지

        // 현재시간
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        try {
            mqttClient.publish(TOPIC_WATER, message.getBytes(), 0, false);
            // MQTT 메시지 발행 후 데이터베이스에 저장
            WaterHelper dbHelper = new WaterHelper(getActivity());  // WaterHelper 인스턴스 생성
            dbHelper.insertWater("Watering", date, message);  // insertWater 메서드를 사용하여 데이터베이스에 저장
            Toast.makeText(getActivity(), "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}

