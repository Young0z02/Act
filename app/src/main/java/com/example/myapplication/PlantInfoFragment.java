package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PlantInfoFragment extends Fragment {

    EditText edit;
    TextView text;
    ImageView imageView;

    String key = "20230607WV14UWOSEECVLG5IEBKZG";
    // 수정된 API 키입니다.

    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_info, container, false);

        edit = view.findViewById(R.id.edit);
        text = view.findViewById(R.id.result);
        imageView = view.findViewById(R.id.imageView);
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = edit.getText().toString();
                if (!location.isEmpty()) {
                    String encodedLocation = URLEncoder.encode(location);
                    String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList?"
                            + "apiKey=" + key
                            + "&sType=sText&wordType=word"
                            + "&sText=" + encodedLocation
                            + "&lightChkVal=&grwhstleChkVal=&lefcolrChkVal="
                            + "&lefmrkChkVal=&flclrChkVal=&fmldecolrChkVal="
                            + "&ignSeasonChkVal=&winterLwetChkVal="
                            + "&priceType=&priceTypeSel=&waterCycleSel="
                            + "&pageNo=1&numOfRows=1000";
                    new DownloadXmlTask().execute(queryUrl);
                }
            }
        });

        return view;
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        private String imageUrl;

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
                boolean isMatched = false; // 일치하는 검색어가 있는지 여부를 확인하기 위한 변수
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = xpp.getName();
                        if (tag.equals("item")) {
                            isMatched = false; // 새로운 아이템 시작 시, 초기화
                        } else if (tag.equals("cntntsSj")) {
                            xpp.next();
                            String plantName = xpp.getText();
                            if (plantName.equals(edit.getText().toString())) {
                                buffer.append("식물 이름: ");
                                buffer.append(plantName);
                                buffer.append("\n");
                                isMatched = true; // 검색어와 일치하는 아이템이 있음을 표시
                            }
                        } else if (isMatched) { // 일치하는 아이템인 경우에만 추가 정보를 처리
                            if (tag.equals("cntntsNo")) {
                                buffer.append("콘텐츠 번호: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnFileSeCode")) {
                                buffer.append("파일 구분 코드: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnFileSn")) {
                                buffer.append("파일 일련 번호: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnOrginlFileNm")) {
                                buffer.append("원본 파일명: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnStreFileNm")) {
                                buffer.append("저장 파일명: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnFileCours")) {
                                buffer.append("파일 경로: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnImageDc")) {
                                buffer.append("이미지 설명: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnThumbFileNm")) {
                                buffer.append("썸네일 파일명: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnImgSeCode")) {
                                buffer.append("이미지 구분 코드: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("rtnFileUrl")) {
                                buffer.append("파일 URL: ");
                                xpp.next();
                                String fileUrl = xpp.getText();
                                buffer.append(fileUrl);
                                buffer.append("\n");
                                imageUrl = fileUrl;

                            } else if (tag.equals("rtnThumbFileUrl")) {
                                buffer.append("썸네일 파일 URL: ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            }
                        }
                    }
                    eventType = xpp.next();
                }

                return buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            text.setText(result);

            if (imageUrl != null) {
                Glide.with(getActivity())
                        .load(imageUrl)
                        .into(imageView);
            }
        }
    }
}
