package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.appcompat.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private String ServerIP = "tcp://223.195.194.41:1883";
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
                // 입력 필드 초기화
                titleEditText.setText("");
                contentEditText.setText("");
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

                // wateringFragment로 전환하는 코드
                Fragment wateringFragment = new WateringFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containers, wateringFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // 현재시간
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());

                // MQTT 메시지 발행
                String message = "Watering"; // 발행할 메시지
                try {
                    mqttClient.publish(TOPIC_WATER, message.getBytes(), 0, false);
                    // MQTT 메시지 발행 후 데이터베이스에 저장
                    dbHelper.insertMemo(new Memo(memoId, "Watering", date, message));
                    Toast.makeText(getActivity(), "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
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

        // 토양 수분 표시
        int numOfDroplets;

        // 물방울 개수를 토양 수분에 따라 설정
        if (soilHumidity <= 10) {
            numOfDroplets = 1;
        } else if (soilHumidity > 10 && soilHumidity <= 30) {
            numOfDroplets = 2;
        } else if (soilHumidity > 30 && soilHumidity <= 50) {
            numOfDroplets = 3;
        } else if (soilHumidity > 50 && soilHumidity <= 70) {
            numOfDroplets = 4;
        } else {
            numOfDroplets = 5;
        }

        // 물방울 표시 로직 (예시로 물방울 형태의 텍스트로 표시)
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numOfDroplets; i++) {
            stringBuilder.append("💧"); // 물방울 이모지 사용
        }
        soilHumidityTextView.setText(stringBuilder.toString());
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
        String soilHum = String.format(Locale.getDefault(), "%.1f %%", soilHumidity);
        soilHumidityTextView.setText(soilHum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
