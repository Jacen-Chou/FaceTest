package com.b.facetest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServlet;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FaceDB extends MainActivity{
	private final String TAG = this.getClass().toString();
//	private static String Url,filepath;


	public static String appid = "Byegb1f3kuthwQmD6hcfp8WHVewxohnX5Lb8Lza5jo3q";
	public static String ft_key = "FsfhC1wjGjMVy23q2o7DtU5s1D34fcSL3Ee7m2Ntq7Em";//人脸追踪
	public static String fd_key = "FsfhC1wjGjMVy23q2o7DtU5zAcJJAFBqge8mUZubQGsr";//人脸检测
	public static String fr_key = "FsfhC1wjGjMVy23q2o7DtU67L1ZSYkrbUZun5ZzeD6th";//人脸识别
	public static String age_key = "FsfhC1wjGjMVy23q2o7DtU6j91sLjzk9XdivjZvM1BeV";//年龄识别
	public static String gender_key = "FsfhC1wjGjMVy23q2o7DtU6rJR8W3U3YoxARdnF1em63";//性别识别
	private final String file_url = "/sdcard/FaceTestMine/";

	String mDBPath;
	List<FaceRegist> mRegister;
	AFR_FSDKEngine mFREngine;
	AFR_FSDKVersion mFRVersion;
	boolean mUpgrade;

	class FaceRegist {
		String mName;
		List<AFR_FSDKFace> mFaceList;

		public FaceRegist(String name) {
			mName = name;
			mFaceList = new ArrayList<>();
		}
	}

	public FaceDB(String path) {
		mDBPath = path;
		mRegister = new ArrayList<>();
		mFRVersion = new AFR_FSDKVersion();
		mUpgrade = false;
		mFREngine = new AFR_FSDKEngine();
		AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
		if (error.getCode() != AFR_FSDKError.MOK) {
			Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
		} else {
			mFREngine.AFR_FSDK_GetVersion(mFRVersion);
			Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
		}
	}

	public void destroy() {
		if (mFREngine != null) {
			mFREngine.AFR_FSDK_UninitialEngine();
		}
	}

	private boolean saveInfo() {
		try {
			FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
			ExtOutputStream bos = new ExtOutputStream(fs);
			bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 获取当前目录下所有的data文件
	public static ArrayList<String > GetFileName(String fileAbsolutePath) {
		ArrayList<String> vecFile = new ArrayList<String>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();

		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				String filename = subFile[iFileLength].getName();
				// 判断是否为.data结尾
				if (filename.trim().toLowerCase().endsWith(".data")) {
					vecFile.add(filename.substring(0,8));
				}
			}
		}
		return vecFile;
	}


	private boolean loadInfo() {
		if (!mRegister.isEmpty()) {//不是空返回flase
			return false;
		}
		try {
			FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
			ExtInputStream bos = new ExtInputStream(fs);
			//load version
			String version_saved = bos.readString();
			ArrayList<String>GetFile = GetFileName(file_url);


			if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
				mUpgrade = true;
			}
			//load all regist name.
//			if (version_saved != null) {
				//约束文件为当前用户文件     7/20  zj
				for (int i=0; i<GetFile.size();i++){
					System.out.println(GetFile.get(i));
					if (new File(mDBPath + "/" + GetFile.get(i) + ".data").exists()&&id_name.equals(GetFile.get(i))) {
//						System.out.println("gggg===="+GetFile.get(i));
						mRegister.add(new FaceRegist(new String(GetFile.get(i))));
					}
				}
//			}
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


//public void downloadFaces(final String name, final String file_url) {
//    Thread s = new Thread() {
//        @Override
//        public void run() {// zj
//            OkHttpClient mOkHttpClient = new OkHttpClient();
//            Request request = new Request.Builder().url(file_url).build();
//            mOkHttpClient.newCall(request).enqueue(new Callback() {
//				@Override
//				public void onFailure(Call call, IOException e) {
//
//				}
//
//				@Override
//				public void onResponse(Call call, Response response) throws IOException {
//					InputStream inputStream = response.body().byteStream();
//					FileOutputStream fileOutputStream = null;
//					try {
//						fileOutputStream = new FileOutputStream(new File(mDBPath + "/" + name + ".data"));
//						byte[] buffer = new byte[2048];
//						int len = 0;
//						while ((len = inputStream.read(buffer)) != -1) {
//							fileOutputStream.write(buffer, 0, len);
//						}
//						fileOutputStream.flush();
//					} catch (IOException e) {
//						//Log.i("wangshu", "IOException");
//						e.printStackTrace();
//					}
//
//					Log.d("data", "文件下载成功");
//
//				}
//			});
//
//
//        }
//    };
//    s.start();
//}



	public boolean loadFaces(){
		if (loadInfo()) {
			try {
				for (FaceRegist face : mRegister) {
//					if(face.mName.equals(id)){
						Log.d(TAG, "load name:" + face.mName + "'s face feature data.");
						FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
						ExtInputStream bos = new ExtInputStream(fs);
						AFR_FSDKFace afr = null;
						do {
							if (afr != null) {
								if (mUpgrade) {
									//upgrade data.
								}
								face.mFaceList.add(afr);
							}
							afr = new AFR_FSDKFace();
						} while (bos.readBytes(afr.getFeatureData()));
						//数据文件
						bos.close();
						fs.close();
						Log.d(TAG, "load name: size = " + face.mFaceList.size());

//					}
//					else{
//						System.out.println("id  !=  data");
//					}
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public	void addFace(String name, AFR_FSDKFace face) {
		try {

			System.out.println("添加学号信息");

			//check if already registered.
			boolean add = true;
			for (FaceRegist frface : mRegister) {
				if (frface.mName.equals(name)) {
					frface.mFaceList.add(face);
					add = false;
					break;
				}
			}
			if (add) { // not registered.
				FaceRegist frface = new FaceRegist(name);
				frface.mFaceList.add(face);
				mRegister.add(frface);
			}

			if (saveInfo()) {
				//update all names
				FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
				ExtOutputStream bos = new ExtOutputStream(fs);
				for (FaceRegist frface : mRegister) {
					bos.writeString(frface.mName);
				}
				bos.close();
				fs.close();

				//save new feature
				fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
				bos = new ExtOutputStream(fs);
				bos.writeBytes(face.getFeatureData());

				System.out.println("图片关键信息:" + face.getFeatureData().toString());
				System.out.println("length:" + face.getFeatureData().length);
				//添加人脸信息的data数据
				bos.close();
				fs.close();

//				//关于传输。。。。。开启子线程    zj
//				filepath = mDBPath+"/"+name+".data";
//				Url = "http://192.168.43.139/FaceAttendanceSystem/Upload.do?id="+name;
//				System.out.println(filepath);
//
//				Thread t = new Thread() {
//                    @Override
//                    public void run() {// zj
//                        try {
//                            URL url = new URL(Url);
//                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
//
//                            // 允许Input、Output，不使用Cache
//                            con.setDoInput(true);
//                            con.setDoOutput(true);
//                            con.setUseCaches(false);
//
//                            con.setConnectTimeout(50000);
//                            con.setReadTimeout(50000);
//                            // 设置传送的method=POST
//                            con.setRequestMethod("POST");
//                            //在一次TCP连接中可以持续发送多份数据而不会断开连接
//                            con.setRequestProperty("Connection", "Keep-Alive");
//                            //设置编码
//                            //con.setRequestProperty("Charset", "UTF-8");
//                            //text/plain能上传纯文本文件的编码格式
//                            con.setRequestProperty("Content-Type", "text/plain");
//
//                            // 设置DataOutputStream
//                            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//
//                            // 取得文件的FileInputStream
//                            FileInputStream fStream = new FileInputStream(filepath);
//                            // 设置每次写入1024bytes
//                            int bufferSize = 1024;
//                            byte[] buffer = new byte[bufferSize];
//
//                            int length = -1;
//                            // 从文件读取数据至缓冲区
//                            while ((length = fStream.read(buffer)) != -1) {
//                                // 将资料写入DataOutputStream中
//                                ds.write(buffer, 0, length);
//                            }
//                            ds.flush();
//                            fStream.close();
//                            ds.close();
//                            if(con.getResponseCode() == 200){
//                                System.out.println("上传成功");
//                                //logger.info("文件上传成功！上传文件为：" + filepath);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            System.out.println("上传失败");
//                            //logger.info("文件上传失败！上传文件为：" + filepath);
//                            //logger.error("报错信息toString：" + e.toString());
//                        }
//
//
//                    }
//                };
//				t.start();
//
//
//			}
////		} catch (IOException e) {
//            e.printStackTrace();
//        }
			}
		}catch (IOException e){
			e.printStackTrace();
		}

	}

//	public boolean delete(String name) {
//		try {
//			//check if already registered.
//			boolean find = false;
//			for (FaceRegist frface : mRegister) {
//				if (frface.mName.equals(name)) {
//					File delfile = new File(mDBPath + "/" + name + ".data");
//					if (delfile.exists()) {
//						delfile.delete();
//					}
//					mRegister.remove(frface);
//					find = true;
//					break;
//				}
//			}
//
//			if (find) {
//				if (saveInfo()) {
//					//update all names
//					FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
//					ExtOutputStream bos = new ExtOutputStream(fs);
//					for (FaceRegist frface : mRegister) {
//						bos.writeString(frface.mName);
//					}
//					bos.close();
//					fs.close();
//				}
//			}
//			return find;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	public boolean upgrade() {
//		return false;
//	}
}
