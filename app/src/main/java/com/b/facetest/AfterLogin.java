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
import android.os.Bundle;
import java.io.File;

public class AfterLogin extends Shibie{

    private Button arrive,cantarr,history,faceregist;
    private final static int IF_FACE_REGIST  = 3;//因为http传输增加参数   handler    7/17   zj
    private final static int QUERY_MY_HISTORY = 4;
    private final static int CHECK_FACE_FILE = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);


        final String id = pref.getString("id","");
        @SuppressLint("SdCardPath") final String file_url = "/sdcard/FaceTestMine/"+id+".data";
//        System.out.println(file_url);
<<<<<<< HEAD

=======
>>>>>>> 29f93250714e14ad003ad4849376b34bcbebe023

        arrive = (Button) findViewById(R.id.arrive);
        cantarr = (Button) findViewById(R.id.cant_arr);
        history = (Button) findViewById(R.id.history);
        faceregist = (Button) findViewById(R.id.face_regist);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = pref.getString("id", "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {//开启线程从服务器确定是否可以注册人脸     zj
                        String result = HttpLogin.Query_myhistory(id);
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
                        Message msg = new Message();
                        msg.what = QUERY_MY_HISTORY;
                        msg.setData(bundle);
                        handler_history.sendMessage(msg);
                    }
                }).start();

            }
        });

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileIsExists(file_url)) {
//                    Toast.makeText(AfterLogin.this,"存在",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AfterLogin.this, Shibie.class);
                    startActivity(intent);
                } else {
//                    Toast.makeText(AfterLogin.this,"不存在",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = HttpLogin.If_Face(id, "2");
                            Bundle bundle = new Bundle();
                            bundle.putString("result", result);
                            Message msg = new Message();
                            msg.what = CHECK_FACE_FILE;
                            msg.setData(bundle);
                            check_file.sendMessage(msg);
                        }
                    }).start();
                }


            }
        });

        faceregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera.release();
                //camera = null;//   error
//                final String id = pref.getString("id","") ;//cookie获取信息  zj
                new Thread(new Runnable() {
                    @Override
                    public void run() {//开启线程从服务器确定是否可以注册人脸     zj
                        String result = HttpLogin.If_Face(id, "1");
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
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


        cantarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterLogin.this, AskForLeaveActivity.class);
                startActivity(intent);
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
                            Toast.makeText(AfterLogin.this,"已人脸注册，如需修改请联系教师",Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };
    //查询考勤历史数据

    @SuppressLint("HandlerLeak")
    Handler handler_history = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case QUERY_MY_HISTORY:{
                    Bundle bundle = new Bundle();
                    bundle =msg.getData();
                    String result = bundle.getString("result");
                    try{
                        Intent intent = new Intent(AfterLogin.this,QueryMyAtt.class);
                        intent.putExtra("result",result);
                        startActivity(intent);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler check_file = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CHECK_FACE_FILE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    try {
                        if (result.equals("success")) {
                            Toast.makeText(AfterLogin.this,"请先注册人脸数据",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(AfterLogin.this,"请再次注册人脸数据",Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };




    //判断文件是否存在方法     7/19    zj
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

}
