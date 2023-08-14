package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PlantInfoFragment extends Fragment {

    String key = "";
    List<String> cntntsNoList = new ArrayList<>(); // 모든 식물 번호를 저장할 리스트

    EditText edit;
    TextView result;
    TextView result1;
    private boolean isButtonClicked = false; // 버튼이 클릭되었는지 여부를 저장하는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_info, container, false);

        edit = view.findViewById(R.id.edit);
        result1 = view.findViewById(R.id.result1);
        result = view.findViewById(R.id.result);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isButtonClicked = true; // 버튼이 클릭되었음을 표시
                String cntntsNoInput = edit.getText().toString(); // 사용자가 입력한 값을 가져옴

                String encodedCntntsNo = URLEncoder.encode(cntntsNoInput);

                String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl?"
                        + "apiKey=" + key
                        + "&cntntsNo=" + encodedCntntsNo;

                new DownloadXmlTask().execute(queryUrl);
            }
        });

        // 모든 식물 번호를 가져오기
        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList?"
                + "apiKey=" + key;

        new GetCntntsNoTask().execute(queryUrl);

        return view;
    }

    private class GetCntntsNoTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... urls) {
            List<String> cntntsNoList = new ArrayList<>();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream is = connection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is));

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = xpp.getName();
                        if (tag.equals("cntntsNo")) {
                            xpp.next();
                            cntntsNoList.add(xpp.getText());
                        }
                    }
                    eventType = xpp.next();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return cntntsNoList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null && !result.isEmpty()) {
                cntntsNoList.addAll(result);
                StringBuilder sb = new StringBuilder();
                for (String cntntsNo : cntntsNoList) {
                    String plantInfo = "식물 번호: " + cntntsNo + "\n";
                    sb.append(plantInfo);
                }
                result1.setText(sb.toString());

                // 모든 식물 번호에 대한 정보 가져오기
                for (String cntntsNo : cntntsNoList) {
                    String plantInfoUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl?"
                            + "apiKey=" + key
                            + "&cntntsNo=" + cntntsNo;

                    new DownloadXmlTask().execute(plantInfoUrl);
                }
            } else {
                result1.setText("식물 번호를 가져오는 중 오류가 발생하였습니다.");
            }
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream is = connection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is));

                StringBuilder buffer = new StringBuilder();

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = xpp.getName();
                        if (tag.equals("cntntsNo")) {
                            xpp.next();
                            String cntntsNo = xpp.getText();
                            buffer.append("식물 번호: ").append(cntntsNo).append("\n");
                        } else if (tag.equals("distbNm")) {
                            xpp.next();
                            buffer.append("유통명: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("fmlNm")) {
                            xpp.next();
                            buffer.append("과명: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("growthHgInfo")) {
                            xpp.next();
                            buffer.append("성장 높이: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("fncltyInfo")) {
                            xpp.next();
                            buffer.append("기능성 정보: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("lefStyleInfo")) {
                            xpp.next();
                            buffer.append("잎 형태: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("orgplceInfo")) {
                            xpp.next();
                            buffer.append("원산지: ").append(xpp.getText()).append("\n");
                        } else if (tag.equals("spostngplaceCodeNm")) {
                            xpp.next();
                            buffer.append("특별 관리 정도: ").append(xpp.getText()).append("\n");
                        }
                    }
                    eventType = xpp.next();
                }

                return buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (isButtonClicked) { // 버튼이 클릭되었을 때만 결과를 표시
                    PlantInfoFragment.this.result.setText(result);
                }
            } else {
                PlantInfoFragment.this.result.setText("데이터를 가져오는 중 오류가 발생하였습니다.");
            }
        }
    }
}
