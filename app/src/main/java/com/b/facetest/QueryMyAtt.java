package com.b.facetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class QueryMyAtt extends AppCompatActivity {

    String myHistory;
    String[] myHistoryTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_my_att);
        Intent intent = getIntent();
        myHistory = intent.getStringExtra("result");
        myHistoryTwo = myHistory.split("\\+");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(QueryMyAtt.this,android.R.layout.simple_list_item_1,myHistoryTwo);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        Toast.makeText(QueryMyAtt.this,"以上为你的所有考勤记录",Toast.LENGTH_SHORT);

    }
}
