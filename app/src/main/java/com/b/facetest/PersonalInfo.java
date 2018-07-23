package com.b.facetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalInfo extends Activity{

    private TextView id, name, email, face_is_registered;
    private Button modify_pass;
    String result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        id = (TextView) findViewById(R.id.id);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        face_is_registered = (TextView) findViewById(R.id.face_is_registered);
        modify_pass = (Button) findViewById(R.id.modify_pass);

        final Intent intent = getIntent();
        result = intent.getStringExtra("result");
        //JSONArray jsonArray = null;

        try {
            // TODO: 2018/7/20/020 试着改为Array
            //jsonArray = new JSONArray(result);
            //for (int i = 0; i < jsonArray.length(); i++) {
               // JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject jsonObject = new JSONObject(result);
                String id_1 = jsonObject.getString("id");
                String name_1 = jsonObject.getString("name");
                String email_1 = jsonObject.getString("email");
                String face_is_registered_1 = jsonObject.getString("face_is_registered");
                id.setText(id_1);
                name.setText(name_1);
                email.setText(email_1);
                if (face_is_registered_1.equals("true")) {
                    face_is_registered.setText("您已注册人脸");
                } else {
                    face_is_registered.setText("您还未注册人脸");
                }
           // }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //点击修改密码按钮
        modify_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInfo.this, ModifyPassActivity.class);
                startActivity(intent);
            }
        });

    }

}
