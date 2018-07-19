package com.b.facetest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.lemonsoft.lemonbubble.LemonBubble;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener {

    private int ResultCode = 3;
    private final static int FORGET_JUDGE = 3;
    private Button forget;
    private EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        id = (EditText) findViewById(R.id.forget_id);
        forget = (Button) findViewById(R.id.forget_do);
        forget.setOnClickListener(this);
    }

    //添加了SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler_forget = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FORGET_JUDGE: {
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    try {
                        if (result.equals("success")) {
                            LemonBubble.hide();
                            //LemonBubble.showRight(ForgetActivity.this, "获取密码成功，请前往邮箱查看密码，并尽快修改！", 2000);
                            Toast.makeText(ForgetActivity.this, "获取密码成功，请前往邮箱查看密码，并尽快修改！", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("id", id.getText().toString());
                            setResult(ResultCode, intent);//向上一级发送数据
                            //Toast.makeText(ForgetActivity.this, "获取密码成功，请前往邮箱查看密码，并尽快修改！", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (result.equals("fail")) {
                            LemonBubble.hide();
                            Toast.makeText(ForgetActivity.this, "获取密码失败，请检查账号是否正确或是否连接网络！", Toast.LENGTH_LONG).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_do: {
                System.out.println(id.getText().toString());
                if (TextUtils.isEmpty(id.getText())) {
                    LemonBubble.showError(ForgetActivity.this, "请输入账号！", 2000);
                    //Toast.makeText(ForgetActivity.this, "请输入账号！", Toast.LENGTH_LONG).show();
                } else {
                    HandlerThread handlerThread = new HandlerThread("myHandlerThread");
                    handlerThread.start();
                    MyHandler handler = new MyHandler(handlerThread.getLooper());
                    handler.post(new Runnable() {
                        //new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //LemonBubble.showRoundProgress(ForgetActivity.this, "等待中...");
                            String result = HttpLogin.ForgetPassword(id.getText().toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("result", result);
                            Message msg = new Message();
                            msg.what = FORGET_JUDGE;
                            msg.setData(bundle);
                            handler_forget.sendMessage(msg);
                        }
                    });
                    //}).start();
                }
            }
            break;
        }
    }

    class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
            LemonBubble.showRoundProgress(ForgetActivity.this, "等待中...");
        }

        @Override
        public void handleMessage(Message msg) {
            LemonBubble.showRoundProgress(ForgetActivity.this, "等待中...");
        }
    }
}
