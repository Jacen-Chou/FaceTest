package com.b.facetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


import net.lemonsoft.lemonbubble.LemonBubble;

public class MainActivity extends Activity{


    //我随便加一句注释

//    Button login,register;
    private EditText id,password;
    private final static int LOGIN_JUDGE = 1;
    private int RequestCode = 1;

    //cookie功能   7/17     zj
    protected SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView login;
    private TextView register;
    private CheckBox remremberPass;
    private TextView forgetPass;
    //---------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        pref = PreferenceManager.getDefaultSharedPreferences(this);//cookie功能   7/17      zj

        login = (TextView) findViewById(R.id.main_btn_login);
        register = (TextView) findViewById(R.id.sign_up);
        remremberPass = (CheckBox)findViewById(R.id.checkBox);//cookie   7/17     zj
        forgetPass = (TextView)findViewById(R.id.main_btn_forget); //忘记密码

        id = (EditText) findViewById(R.id.input_layout_name_id);
        password = (EditText) findViewById(R.id.input_layout_password);

        //cookie 记住密码     7/17    zj
        boolean isRemember = pref.getBoolean("remember_password",false);
        String account = pref.getString("id","");
        id.setText(account);
        if(isRemember){
            //将账号和密码设置到文本框中
            String pass = pref.getString("password","");
            password.setText(pass);
            remremberPass.setChecked(true);
        }






        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
//                startActivity(intent);
                startActivityForResult(intent,RequestCode);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //使用下面类里的函数，连接servlet，返回一个result，使用handler处理这个result
                        String result = HttpLogin.LoginByPost(id.getText().toString(),password.getText().toString());
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        Message message = new Message();
                        message.setData(bundle);
                        message.what = LOGIN_JUDGE;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        //点击忘记密码按钮
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgetActivity.class);
                startActivityForResult(intent,RequestCode);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==2){
            id.setText(data.getStringExtra("id"));
            password.setText(data.getStringExtra("password"));
        }
        if(requestCode==1&&resultCode==3){
            id.setText(data.getStringExtra("id"));
            password.setText("");
        }
    }
    //添加了SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case LOGIN_JUDGE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();

                    try {
                        if (result.equals("success")) {
                            Toast.makeText(MainActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();

                            //复选框功能   cookie   7/17    zj
                            editor = pref.edit();
                            editor.putString("id",id.getText().toString());
                            if(remremberPass.isChecked()){//检查复选框是否被选中
                                editor.putBoolean("remember_password",true);
                                editor.putString("password",password.getText().toString());
                            }else {
                                editor.clear();
                            }
                            editor.apply();


                            Intent intent = new Intent(MainActivity.this,AfterLogin.class);//登录成功后跳转到最开始界面  zj
                            startActivity(intent);
                        } else if(result.equals("fail")){
                            Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(MainActivity.this,"有错误",Toast.LENGTH_SHORT).show();
                        }

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.login:{
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //使用下面类里的函数，连接servlet，返回一个result，使用handler处理这个result
//                        String result = HttpLogin.LoginByPost(id.getText().toString(),password.getText().toString());
//                        Bundle bundle = new Bundle();
//                        bundle.putString("result",result);
//                        Message message = new Message();
//                        message.setData(bundle);
//                        message.what = LOGIN_JUDGE;
//                        handler.sendMessage(message);
//                    }
//                }).start();
//            }
//            break;
//            case R.id.register:{
//                Intent intent = new Intent(this,RegisterActivity.class);
//                startActivityForResult(intent,RequestCode);
//            }
//            break;
//        }
//    }
}


