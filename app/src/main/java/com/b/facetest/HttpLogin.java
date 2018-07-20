package com.b.facetest;

import net.lemonsoft.lemonbubble.LemonBubble;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.net.URLEncoder.*;

public class HttpLogin {

    private static  String tools(String address,String data){
        String result="";
        try{
            URL url = new URL(address);//初始化URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");//请求方式

            //超时信息
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            //post方式不能设置缓存，需手动设置为false
            conn.setUseCaches(false);

            //获取输出流
            OutputStream out = conn.getOutputStream();

            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();

            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                result = new String(message.toByteArray());
            }

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    public static String LoginByPost(String id,String password) {
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidLogin.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8")+
                    "&password="+ encode(password,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = tools(address,data);
        return result;
    }

    public static String RegisterByPost(String id,String name,String password,String email){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidStuRegister.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8")+
                    "&name="+ encode(name,"UTF-8")+
                    "&password="+ encode(password,"UTF-8")+
                    "&email="+ encode(email,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;

    }

    public static String FaceRegistByPost(String id,String password,String if_data){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidRegisterFace.do";
        String result = "";
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8")+
                    "&password="+ encode(password,"UTF-8")+
                    "&data="+ encode(if_data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }

    public static String If_Face(String id,String flag){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidIfRegisterFace.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8")
                    + "&flag="+ encode(flag,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }

    public static String If_Arrive(String id,String if_success){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidAttendance.do";//需要修改地址
        String result = "";
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8")+
                    "&if_success="+ encode(if_success,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }


    public static String Query_myhistory(String id){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidQueryAllAttById.do";//需要修改地址
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }


    public static String ForgetPassword(String id){
        String address = "http://192.168.43.108/FaceAttendanceSystem/AndroidStuForget.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id="+ encode(id,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }

}