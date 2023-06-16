package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.xmlpull.v1.XmlPullParserException;

public class PlantInfoFragment extends Fragment {

    EditText edit;
    TextView text;
    ImageView view;

    String key = "20230607WV14UWOSEECVLG5IEBKZG"; // 수정된 API 키입니다.
    String cntntsNo = ""; // cntntsNo 값을 저장할 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_info, container, false);

        edit = view.findViewById(R.id.edit);
        text = view.findViewById(R.id.result);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cntntsNoInput = edit.getText().toString(); // 사용자가 입력한 값을 가져옵니다.

                String encodedCntntsNo = URLEncoder.encode(cntntsNoInput);

                String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl?"
                        + "apiKey=" + key
                        + "&cntntsNo=" + encodedCntntsNo;

                new DownloadXmlTask().execute(queryUrl);
            }
        });

        // 초기화면에서 cntntsNo 값을 표시하기 위해 설정합니다.
        text.setText("cntntsNo: " + cntntsNo);

        return view;
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
                            cntntsNo = xpp.getText();
                        } else if (tag.equals("distbNm")) {
                            xpp.next();
                            buffer.append("배포명: ");
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        } else if (tag.equals("fmlNm")) {
                            xpp.next();
                            buffer.append("과명: ");
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        } else if (tag.equals("growthHgInfo")) {
                            xpp.next();
                            buffer.append("생육 고도 정보: ");
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        } else if (tag.equals("growthAraInfo")) {
                            xpp.next();
                            buffer.append("생육 지역 정보: ");
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        } else if (tag.equals("lefStyleInfo")) {
                            xpp.next();
                            buffer.append("잎 형태 정보: ");
                            buffer.append(xpp.getText());
                            buffer.append("\n");
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
                text.setText("cntntsNo: " + cntntsNo + "\n" + result);
            } else {
                text.setText("데이터를 가져오는 중 오류가 발생하였습니다.");
            }
        }
    }
}
