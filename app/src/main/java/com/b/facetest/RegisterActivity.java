package com.b.facetest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private int ResultCode = 2;
    private final static int REGISTER_JUDGE = 2;
    private TextView register,back;
    private EditText id,name,psw_1,psw_2,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (TextView) findViewById(R.id.register_do);
        register.setOnClickListener(this);
        id = (EditText) findViewById(R.id.id_edit);
        name = (EditText)findViewById(R.id.name_edit);
        psw_1 = (EditText) findViewById(R.id.password_edit);
        psw_2 = (EditText) findViewById(R.id.password_edit_1);
        email = (EditText) findViewById(R.id.email_edit);

        back = (TextView) findViewById(R.id.back_edit);//zj  返回MainActivity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    //添加了SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case REGISTER_JUDGE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    try {
                        if (result.equals("success")) {
                            Intent intent = new Intent();
                            intent.putExtra("id",id.getText().toString());
                            intent.putExtra("password",psw_1.getText().toString());
                            setResult(ResultCode,intent);//向上一级发送数据
                            finish();
                        } else if (result.equals("fail")) {
                            Toast.makeText(RegisterActivity.this,"账号已存在！", Toast.LENGTH_LONG).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_do:{
                if( ! psw_1.getText().toString().equals(psw_2.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"两次密码不一致！",Toast.LENGTH_LONG).show();
                } else if (!Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", email.getText().toString())) {
                    Toast.makeText(RegisterActivity.this,"输入邮箱错误！",Toast.LENGTH_LONG).show();
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("提示")//注释拍照完成后需要输入名称
                            //.setIcon(android.R.drawable.ic_dialog_info)
                            //.setView(layout)
                            .setMessage("学号唯一标识且邮箱用于密码找回！确定提交？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String result = HttpLogin.RegisterByPost(id.getText().toString(),name.getText().toString(),
                                                    psw_1.getText().toString(),email.getText().toString());
                                            Bundle bundle = new Bundle();
                                            bundle.putString("result",result);
                                            Message msg = new Message();
                                            msg.what = REGISTER_JUDGE;
                                            msg.setData(bundle);
                                            handler.sendMessage(msg);
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
            break;
        }
    }
}


