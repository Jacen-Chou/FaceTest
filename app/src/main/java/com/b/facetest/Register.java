package com.b.facetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtImageView;

import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;



public class Register extends MainActivity {

    Uri mImage;
    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;

    private final static int FACE_JUDGE = 3;//因为http传输增加参数   handler    7/17   zj
    private int ResultCode = 2;

    private AFR_FSDKFace mAFR_FSDKFace;
    private UIHandler mUIHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mUIHandler = new UIHandler();

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        mImage = uri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

//        MainActivity.mCamera.release();
//        MainActivity.mCamera = null;
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);

    }

    //添加了SuppressLint("HandlerLeak")    //以下因注册发送数据需要该handler    增加内容   7/17   zj
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case FACE_JUDGE:{
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    String result = bundle.getString("result");
                    //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    try {
                        if (result.equals("success")) {
                            Toast.makeText(Register.this,"人脸提交成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Register.this,"人脸提交失败，请重试！",Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };

    //按提示@
    @SuppressLint("HandlerLeak")
    class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE) {
                if (msg.arg1 == MSG_EVENT_REG) {
                    LayoutInflater inflater = LayoutInflater.from(Register.this);
                    View layout = inflater.inflate(R.layout.dialog_register, null);
                    final EditText mEditText = (EditText) layout.findViewById(R.id.editview);
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                    ExtImageView mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
                    mExtImageView.setImageBitmap((Bitmap) msg.obj);
                    final Bitmap face = (Bitmap) msg.obj;
                    new AlertDialog.Builder(Register.this)
                            //.setTitle("请输入注册学号")//注释拍照完成后需要输入名称
                            //.setIcon(android.R.drawable.ic_dialog_info)
                            //.setView(layout)
                            .setMessage("确定提交该信息？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final String id = pref.getString("id","") ;//cookie获取信息  zj
                                    final String password = pref.getString("password","");
                                    Shibie.mFaceDB.addFace(id,mAFR_FSDKFace);//用cookie的账户命名上传data文件    zj
//                                    Shibie.mFaceDB.addFace(mEditText.getText().toString(), mAFR_FSDKFace);//生成本地data文件，上传
//                                    dialog.dismiss();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String result = HttpLogin.FaceRegistByPost(id,password,"true");
                                            Bundle bundle = new Bundle();
                                            bundle.putString("result",result);
                                            Message msg = new Message();
                                            msg.what = FACE_JUDGE;
                                            msg.setData(bundle);
                                            handler.sendMessage(msg);
                                        }
                                    }).start();

                                    finish();//添加测试的，不确定。回到afterlogin界面   zj
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();//添加测试的，不确定。回到AfterLogin界面   zj
                                }
                            })
                            .show();
                } else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
                    Toast.makeText(Register.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if(msg.arg1 == MSG_EVENT_NO_FACE ){
                    Toast.makeText(Register.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
                    Toast.makeText(Register.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                } else if(msg.arg1 == MSG_EVENT_FR_ERROR){
                    Toast.makeText(Register.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                }
            }
            //finish();//添加测试的，不确定。回到AfterLogin界面    zj
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("拍照完成返回了1");
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            System.out.println("拍照完成返回了2");
            Uri mPath = mImage;
            String file = getPath(mPath);
            Bitmap bmp = decodeImage(file);
//            startRegister(bmp, file);
            byte[] bmpdata = new byte[bmp.getWidth() * bmp.getHeight() * 3 / 2];
            ImageConverter convert = new ImageConverter();
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, bmpdata)) {
                System.out.println("convert ok");
            }
            convert.destroy();

            AFD_FSDKEngine engine = new AFD_FSDKEngine();
            AFD_FSDKVersion version = new AFD_FSDKVersion();
            List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();// 用来存放检测到的人脸信息列表
            //初始化人脸检测引擎
            AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);

            System.out.println("AFD_FSDK_InitialFaceEngine = " + err.getCode());
            if (err.getCode() != AFD_FSDKError.MOK) {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_FD_ERROR;
                reg.arg2 = err.getCode();
                mUIHandler.sendMessage(reg);
            }
            err = engine.AFD_FSDK_GetVersion(version);
            System.out.println("AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());

            //输入的 data 数据为 NV21 格式（如 Camera 里 NV21 格式的 preview 数据），其中 height 不能为奇数，人脸检测返回结果保存在 result
            err  = engine.AFD_FSDK_StillImageFaceDetection(bmpdata, bmp.getWidth(), bmp.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
            System.out.println("AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

            if (!result.isEmpty()) {
                AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                AFR_FSDKFace result1 = new AFR_FSDKFace();
                AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
                if (error1.getCode() != AFD_FSDKError.MOK) {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_FR_ERROR;
                    reg.arg2 = error1.getCode();
                    mUIHandler.sendMessage(reg);
                }
                error1 = engine1.AFR_FSDK_GetVersion(version1);
                Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                error1 = engine1.AFR_FSDK_ExtractFRFeature(bmpdata, bmp.getWidth(), bmp.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
                if(error1.getCode() == error1.MOK) {
                    mAFR_FSDKFace = result1.clone();
                    int width = result.get(0).getRect().width();
                    int height = result.get(0).getRect().height();
                    Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    Canvas face_canvas = new Canvas(face_bitmap);
                    face_canvas.drawBitmap(bmp, result.get(0).getRect(), new Rect(0, 0, width, height), null);
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_REG;
                    reg.obj = face_bitmap;
                    mUIHandler.sendMessage(reg);
                } else {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_NO_FEATURE;
                    mUIHandler.sendMessage(reg);
                }
                error1 = engine1.AFR_FSDK_UninitialEngine();
                Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
            } else {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_NO_FACE;
                mUIHandler.sendMessage(reg);
            }
            //销毁人脸检测引擎
            err = engine.AFD_FSDK_UninitialFaceEngine();
            System.out.println("aaaa");
        }
        System.out.println("bbbb");
    }
    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(this, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(this, contentUri, selection, selectionArgs);
                }
            }
        }
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }
    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
