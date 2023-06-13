package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditMemoActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;

    private DBHelper dbHelper;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
        saveButton = findViewById(R.id.save);

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        int memoId = intent.getIntExtra("memoId", -1);
        memo = dbHelper.getMemoById(memoId);

        titleEditText.setText(memo.getTitle());
        contentEditText.setText(memo.getContent());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String date = getCurrentDate();

                memo.setTitle(title);
                memo.setContent(content);
                memo.setDate(date);

                dbHelper.updateMemo(memo);

                Toast.makeText(EditMemoActivity.this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}