package com.b.facetest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
        import android.widget.TextView;
import android.widget.Toast;

import com.b.facetest.widget.CustomDatePicker;

        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Locale;

/**
 * Created by liuwan on 2016/9/28.
 */
public class AskForLeaveActivity extends MainActivity{

    private RelativeLayout selectDate, selectTime;
    private TextView currentDate, currentTime, Reason;
    private TextView ask_submit;

    private CustomDatePicker customDatePicker1, customDatePicker2;
    private String Leave_id = "";
    private final static int ASK_FOR_LEAVE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_leave);

        //selectTime = (RelativeLayout) findViewById(R.id.selectTime);
        //selectTime.setOnClickListener(this);

        selectDate = (RelativeLayout) findViewById(R.id.selectDate);
        currentDate = (TextView) findViewById(R.id.currentDate);
        currentTime = (TextView) findViewById(R.id.days);
        Reason = (TextView) findViewById(R.id.reason);
        Leave_id = pref.getString("id", "");
        //currentTime = (TextView) findViewById(R.id.currentTime);
        ask_submit = (TextView)findViewById(R.id.ask_submit);


        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker1.show(currentDate.getText().toString());
            }
        });

        ask_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("runrunrun");
                        String result = HttpLogin.AskForLeave(Leave_id, currentDate.getText().toString(),
                                currentTime.getText().toString(), Reason.getText().toString());
                        System.out.println("resultttttt:"+result);
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
                        Message msg = new Message();
                        msg.what = ASK_FOR_LEAVE;
                        msg.setData(bundle);
                        LeaveHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
        initDatePicker();
    }


    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        currentDate.setText(now.split(" ")[0]);
//        currentTime.setText(now);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2030-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

//        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) { // 回调接口，获得选中的时间
//                currentTime.setText(time);
//            }
//        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
//        customDatePicker2.showSpecificTime(true); // 显示时和分
//        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }


    @SuppressLint("HandlerLeak")
    Handler LeaveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ASK_FOR_LEAVE: {
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    try {
                        if (result.equals("success")) {
                            final AlertDialog alert = new AlertDialog.Builder(AskForLeaveActivity.this).create();
                            alert.setMessage("请假申请提交成功");
                            alert.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alert.dismiss();
                                    finish();
                                }
                            }, 2000);
                        }else{
                            Toast.makeText(AskForLeaveActivity.this,"提交失败，请重试",Toast.LENGTH_SHORT);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    };
}

