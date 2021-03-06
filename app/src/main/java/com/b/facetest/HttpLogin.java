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

    private static String tools(String address, String data) {
        String result = "";
        try {
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 登陆
     * @param id
     * @param password
     * @return
     */
    public static String LoginByPost(String id, String password) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidLogin.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8") +
                    "&password=" + encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = tools(address, data);
        return result;
    }

    /**
     * 注册
     * @param id
     * @param name
     * @param password
     * @param email
     * @return
     */
    public static String RegisterByPost(String id, String name, String password, String email) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidStuRegister.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8") +
                    "&name=" + encode(name, "UTF-8") +
                    "&password=" + encode(password, "UTF-8") +
                    "&email=" + encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;

    }

    /**
     * 注册人脸
     * @param id
     * @param password
     * @param if_data
     * @return
     */
    public static String FaceRegistByPost(String id, String password, String if_data) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidRegisterFace.do";
        String result = "";
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8") +
                    "&password=" + encode(password, "UTF-8") +
                    "&data=" + encode(if_data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;
    }

    /**
     * 判断是否注册过人脸
     * @param id
     * @param flag
     * @return
     */
    public static String If_Face(String id, String flag) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidIfRegisterFace.do";
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8")
                    + "&flag=" + encode(flag, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;
    }

    /**
     * 考勤打卡
     * @param id
     * @param if_success
     * @return
     */

    public static String If_Arrive(String id, String if_success,String IP) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidAttendance.do";//需要修改地址
        String result = "";
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8") +
                    "&if_success=" + encode(if_success, "UTF-8")+
                    "&IP="+encode(IP,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;
    }

    /**
     * 查询考勤记录
     * @param id
     * @return
     */
    public static String Query_myhistory(String id) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidQueryAllAttById.do";//需要修改地址
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;
    }

    /**
     * 忘记密码
     * @param id
     * @return
     */
    public static String ForgetPassword(String id) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidStuForget.do";
        String result = "";
        try {
            URL url = new URL(address);//初始化URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");//请求方式

            //超时信息
            conn.setReadTimeout(500000);
            conn.setConnectTimeout(500000);

            //post方式不能设置缓存，需手动设置为false
            conn.setUseCaches(false);

            //获取输出流
            OutputStream out = conn.getOutputStream();

            String data = "id=" + encode(id, "UTF-8");

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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查询个人信息
     * @param id
     * @return
     */
    public static String Query_personalInfo(String id) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidQueryPersonalInfo.do";//需要修改地址
        String result = "";
        //我们请求的数据
        String data = null;
        try {
            data = "id=" + encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        result = tools(address, data);
        return result;
    }

    /**   zj    7/21
     * 请假
     */
    public static String AskForLeave(String id,String start,String days,String reason){
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidStuAskForLeave.do";
        String result = "";
        String data = null;
        try{
            data = "stuid="+encode(id,"UTF-8")
                    +"&start="+encode(start,"UTF-8")
                    +"&days="+encode(days,"UTF-8")
                    +"&reason="+encode(reason,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }

    /**
     * 修改密码
     * @param id
     * @param old_pass
     * @param new_pass
     * @return
     */
    public static String ModifyPass(String id, String old_pass, String new_pass) {
        String address = "http://154.8.140.224:8080/FaceAttendanceSystem/AndroidStuModifyPassword.do";
        String result = "";
        String data = null;
        try{
            data = "id="+encode(id,"UTF-8")
                    +"&old_pass="+encode(old_pass,"UTF-8")
                    +"&new_pass="+encode(new_pass,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        result = tools(address,data);
        return result;
    }

}

