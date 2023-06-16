package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

public class WateringManagementFragment extends Fragment {
    private ListView waterListView;
    private WaterAdapter memoListAdapter;
    private WaterHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watering_management, container, false);

        waterListView = rootView.findViewById(R.id.waterListView);
        dbHelper = new WaterHelper(getActivity());

        // 데이터베이스에서 메모 리스트 가져오기
        List<WaterMemo> memoList = dbHelper.getAllWaterMemos();

        // 메모 리스트 어댑터 생성
        memoListAdapter = new WaterAdapter(getActivity(), memoList);

        // 리스트 뷰에 어댑터 설정
        waterListView.setAdapter(memoListAdapter);

        return rootView;
    }
}
