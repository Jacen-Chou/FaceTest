package com.b.facetest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.content.Intent;
import android.view.View;


public class AfterLogin extends Shibie{

    private Button arrive,cantarr,history,faceregist;

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
                Intent intent = new Intent();
                intent.setClass(AfterLogin.this, Register.class);
                startActivity(intent);
            }
        });
    }

}
