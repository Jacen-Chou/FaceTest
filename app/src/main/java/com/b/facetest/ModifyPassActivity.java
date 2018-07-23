package com.b.facetest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ModifyPassActivity extends MainActivity {

    private final static int MODIFY_PASS_JUDGE = 1;

    private EditText old_pass, new_pass, confirm_new_pass;
    private TextView submit_pass, back_main;
    private String id = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pass);

        old_pass = (EditText) findViewById(R.id.old_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        confirm_new_pass = (EditText) findViewById(R.id.confirm_new_pass);
        submit_pass = (TextView) findViewById(R.id.submit_pass);
        back_main = (TextView) findViewById(R.id.back_main);
        id = pref.getString("id", "");

        back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ! new_pass.getText().toString().equals(confirm_new_pass.getText().toString())){
                    Toast.makeText(ModifyPassActivity.this,"两次密码不一致！",Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //使用下面类里的函数，连接servlet，返回一个result，使用handler处理这个result
                            String result = HttpLogin.ModifyPass(id, old_pass.getText().toString(), new_pass.getText().toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("result", result);
                            Message message = new Message();
                            message.setData(bundle);
                            message.what = MODIFY_PASS_JUDGE;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MODIFY_PASS_JUDGE : {
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    if (result.equals("success")) {
                        Toast.makeText(ModifyPassActivity.this,"修改成功！",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ModifyPassActivity.this,AfterLogin.class);
                        startActivity(intent);
                    } else if(result.equals("pass_error")){
                        Toast.makeText(ModifyPassActivity.this,"原密码错误！",Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(ModifyPassActivity.this,"有错误！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    };


}
