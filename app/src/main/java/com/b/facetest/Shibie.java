package com.b.facetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.app.AlertDialog;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.guo.android_extend.java.AbsLoop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Shibie extends MainActivity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //private CameraCaptureSession camera;
    protected Camera camera;//摄像头 可能改写为Camera2
    TextView tv_name;

    public static FaceDB mFaceDB;
    FRAbsLoop mFRAbsLoop = null;
    private AFR_FSDKFace mAFR_FSDKFace;
    Handler mHandler = new Handler();
    //private int mFormat = ImageFormat.NV21;//改写
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;
    AFT_FSDKVersion version = new AFT_FSDKVersion();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();

    private int mWidth = 1280;
    private int mHeight = 960;
    AFT_FSDKFace mAFT_FSDKFace = null;
    byte[] mImageNV21 = null;
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    List<AFT_FSDKFace> result1 = new ArrayList<>();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
    List<ASAE_FSDKAge> ages = new ArrayList<>();
    List<ASGE_FSDKGender> genders = new ArrayList<>();

    private final static int IF_ARRIVE  = 4;//因为http传输增加参数   handler    7/17   zj


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shibie);

        tv_name = findViewById(R.id.name);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        String filePath="/sdcard/FaceTestMine/";
        File file=new File(filePath);
        if(!file.exists()){
            boolean mkdirs = file.mkdirs();
            //file.mkdirs();//   原代码
        }
        mFaceDB = new FaceDB(file.getPath());

        mFaceDB.loadFaces();

        System.out.println("mRegister的 长度:" + mFaceDB.mRegister.size());
    }

    @Override
    protected void onResume() {
        super.onResume();

        surfaceHolder.addCallback(this);

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        err = engine.AFT_FSDK_GetVersion(version);

        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);

        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFRAbsLoop.shutdown();
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
        ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(myPreviewCallback);

            Camera.Parameters parameters = camera.getParameters();//按提示改写之后，正常
            //Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            int mFormat = ImageFormat.NV21;//改写成了局部变量  zj
            parameters.setPreviewFormat(mFormat);
            for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
            }
            for( Integer format : parameters.getSupportedPreviewFormats()) {
            }
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            mWidth = camera.getParameters().getPreviewSize().width;
            mHeight = camera.getParameters().getPreviewSize().height;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            System.out.println("销毁相机");
            holder.removeCallback(this);
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();

            System.out.println("销毁完成");
        }
    }

    MyPreviewCallBack myPreviewCallback = new MyPreviewCallBack();
    class MyPreviewCallBack implements Camera.PreviewCallback {
        @Override

      public void onPreviewFrame(byte[] data, Camera camera) {
            AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, result1);
            for (AFT_FSDKFace face : result1) {
            }
            if (mImageNV21 == null) {
                if (!result1.isEmpty()) {
                    mAFT_FSDKFace = result1.get(0).clone();
                    mImageNV21 = data.clone();
                }
            }
            //copy rects
            Rect[] rects = new Rect[result1.size()];
            for (int i = 0; i < result1.size(); i++) {
                rects[i] = new Rect(result1.get(i).getRect());
            }
            result1.clear();
        }
    }

//    public void regist(View view) {
//        camera.release();
//        camera = null;
//        Intent intent = new Intent();
//        intent.setClass(Shibie.this, Register.class);
//        startActivity(intent);
//    }



    //用于考勤打卡传输处理     7/17    zj
    //添加了SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case IF_ARRIVE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    try {
                        if (result.equals("success")) {
                            new AlertDialog.Builder(Shibie.this)
                                    //.setTitle(" ")
                                    .setMessage("打卡考勤成功")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();

                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };



    class FRAbsLoop extends AbsLoop {
        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mResgist = mFaceDB.mRegister;
        List<ASAE_FSDKFace> face1 = new ArrayList<>();
        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            error = engine.AFR_FSDK_GetVersion(version);
        }

        @Override
        public void loop() {
            if (mImageNV21 != null) {
                long time = System.currentTimeMillis();
                System.out.println("进入了FRAbsLoop   " + mResgist.size());
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                for (FaceDB.FaceRegist fr : mResgist) {
                    for (AFR_FSDKFace face : fr.mFaceList) {
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                        }
                    }
                }
                //age & gender
                face1.clear();
                face2.clear();
                face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");


                //告知服务器人脸打卡通过功能     7/17     zj
                if(max > 0.6f){
                    final String id = pref.getString("id","") ;//cookie获取信息  zj
                    new Thread(new Runnable() {
                        @Override
                        public void run() {//开启线程告知服务器验证通过    zj
                            String result = HttpLogin.If_Arrive(id,"true");
                            Bundle bundle = new Bundle();
                            bundle.putString("result",result);
                            Message msg = new Message();
                            msg.what = IF_ARRIVE;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                   //成功后需要跳出loop   待修改    zj
                    //。。。。。。。
                    over();


                } else{
                    Toast.makeText(Shibie.this,"wait...",Toast.LENGTH_SHORT).show();
                }
                mImageNV21 = null;
//              ********************

                //***********************************

//                if (max > 0.6f) {//     zj   注释
//
//
//
////                    原代码
////                    fr success.
//                    final float max_score = max;
//                    final String mNameShow = name;
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            tv_name.setAlpha(1.0f);
//                            tv_name.setText(mNameShow + "  置信度:" + (float)((int)(max_score * 1000)) / 1000.0);
//                            tv_name.setTextColor(Color.RED);
//                        }
//                    });
//                } else {
////                    原代码
//                    final String mNameShow = "未识别";
//                    Shibie.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            tv_name.setAlpha(1.0f);
//                            tv_name.setText(mNameShow + "   " + gender + "," + age);
//                            tv_name.setTextColor(Color.RED);
//                        }
//                    });
//                }
//                mImageNV21 = null;
//            }
//        }

            }

        }
        @Override
        public void over () {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
        }
    }
}
