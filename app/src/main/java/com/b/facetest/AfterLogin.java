package com.b.facetest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class AfterLogin extends Shibie{

    private Button arrive,cantarr,history,faceregist;
    private final static int IF_FACE_REGIST  = 3;//因为http传输增加参数   handler    7/17   zj

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);

        arrive = (Button)findViewById(R.id.arrive);
        cantarr = (Button)findViewById(R.id.cant_arr);
        history = (Button)findViewById(R.id.history);
        faceregist = (Button)findViewById(R.id.face_regist);

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterLogin.this,Shibie.class);
                startActivity(intent);
            }
        });

        faceregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera.release();
                //camera = null;//   error
                final String id = pref.getString("id","") ;//cookie获取信息  zj
                new Thread(new Runnable() {
                    @Override
                    public void run() {//开启线程从服务器确定是否可以注册人脸     zj
                        String result = HttpLogin.If_Face(id);
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        Message msg = new Message();
                        msg.what = IF_FACE_REGIST;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();

//                原来的注册人脸代码    zj
//                Intent intent = new Intent();
//                intent.setClass(AfterLogin.this, Register.class);
//                startActivity(intent);
            }
        });
    }



    //为了判断能否注册人脸添加    zj
    //添加了SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case IF_FACE_REGIST:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    try {
                        if (result.equals("success")) {
                            Intent intent = new Intent();
                            intent.setClass(AfterLogin.this, Register.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(AfterLogin.this,"你已完成人脸注册，如需修改请联系教师",Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };
}
