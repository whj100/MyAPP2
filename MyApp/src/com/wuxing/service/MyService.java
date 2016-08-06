package com.wuxing.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.amap.api.navi.enums.BroadcastMode;
import com.wuxing.fragment.CameraFragment.MyBroadCastReceiver;
import com.wuxing.utils.Constant;
import com.wuxing.utils.FileUtil;
import com.wuxing.utils.MyCamera;

public class MyService extends Service implements OnErrorListener,
		OnInfoListener, Callback {
	boolean isRecorder = false;
	SurfaceView mSurfaceView;
	SurfaceHolder mSurfaceHolder;
	WindowManager wManager;
	MediaRecorder mRecorder;
	private Camera camera;
	MyBroadCastReciver myReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mSurfaceView = new SurfaceView(getApplicationContext());
		mSurfaceHolder = mSurfaceView.getHolder();// 取得holder
		mSurfaceHolder.addCallback(this); // holder加入回调接口
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// setType必须设置，要不出错.
		wManager = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		wManager.getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;

		int screenHeigh = dm.heightPixels;

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		// 类型
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.width = screenWidth / screenWidth;
		params.height = screenHeigh / screenHeigh;
		params.gravity = Gravity.CENTER;

		wManager.addView(mSurfaceView, params);
		wManager.updateViewLayout(mSurfaceView, params);
		mRecorder = new MediaRecorder();

		// 广播声明 用来接收主界面发来的消息
		myReceiver = new MyBroadCastReciver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.START_CAMERA);
		filter.addAction(Constant.STOP_CAMERA);
		registerReceiver(myReceiver, filter);

		// 告诉主actiivty可以开始录像
		Intent intent = new Intent();
		intent.setAction(Constant.CAMERA);
		sendBroadcast(intent);
	}

	public class MyBroadCastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			// 接收主Activity是要启动录像还是停止录像
			if (action.equals(Constant.START_CAMERA)) {
				startRecorder();
			} else if (action.equals(Constant.STOP_CAMERA)) {
				stopRecorder();
				// wManager.removeView(mSurfaceView);
				Log.e("服务接收", "停止命令");
				if (mRecorder != null) {
					mRecorder.release();
					mRecorder = null;
				}
				if (camera != null) {
					mSurfaceHolder = null;
					mSurfaceHolder = null;
					camera.setPreviewCallback(null);
					camera.stopPreview();
					camera.lock();
					// isPreviewing = false;
					// mPreviwRate = -1f;
					camera.release();
					camera = null;
					MyCamera.camera = null;
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("服务", "onDestroy命令");
		wManager.removeView(mSurfaceView);
		if (isRecorder) {
			mRecorder.stop();
			mRecorder.reset();
			isRecorder = false;
		}
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (null != camera) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.lock();
			// isPreviewing = false;
			// mPreviwRate = -1f;
			camera.release();
			camera = null;
		}

		Intent intent = new Intent();
		intent.setAction("com.service.close");
		sendBroadcast(intent);
		unregisterReceiver(myReceiver);

		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);

	}

	/**
	 * 使用时间对录像起名
	 * 
	 * @return
	 */
	public static String getDate() {
		Calendar ca = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = sdf.format(ca.getTimeInMillis());
		return date;
	}

	/**
	 * 停止录像
	 */
	public void stopRecorder() {
		// TODO Auto-generated method stub
		isRecorder = false;
		try {
			mRecorder.stop();
			mRecorder.reset(); // You can reuse the object by
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始录像
	 */
	@SuppressLint("NewApi")
	public void startRecorder() {
		// TODO Auto-generated method stub
		isRecorder = true;
		try {
			camera.unlock();
			mRecorder.reset();
			mRecorder.setCamera(camera);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 这两项需要放在setOutputFormat之前
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 设置录制视频源为Camera(相机)
			// mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
			// 1.8 以上
			mRecorder.setProfile(CamcorderProfile
					.get(CamcorderProfile.QUALITY_HIGH));// 这句是让视频文件在PC端也能视频竖屏播放的保证，跟下面注释了的不能联用
			// 这两项需要放在setOutputFormat之后
			// mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);//
			// 设置录制的视频编码h263
			//
//			List<Size> sizes = camera.getParameters()
//					.getSupportedPictureSizes();
//			mRecorder.setVideoSize(sizes.get(0).height,sizes.get(0).width);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			// mRecorder.setVideoFrameRate(30);// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错

			// mRecorder.setMaxDuration(8000);// 设置最大的录制时间
			// mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

			// Set output file path
			String path = FileUtil.initPath();
			if (path != null) {
				File dir = new File(path + "/TestCamera");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				path = dir + "/" + getDate() + ".mp4";
				mRecorder.setOutputFile(path);
				mRecorder.setOrientationHint(90);

				mRecorder.prepare();// 准备录制
				mRecorder.start(); // 开始录制
				mRecorder.setOnErrorListener(this);
				mRecorder.setOnInfoListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int width,
			int height) {
		// TODO Auto-generated method stub
		camera = MyCamera.getInstance(); // 获取Camera实例
		// 获取Camera实例
		try {
			camera.setPreviewDisplay(holder);
			// sfv.setLayoutParams(new LinearLayout.LayoutParams(width,
			// height));
		} catch (Exception e) {
			// 如果出现异常，则释放Camera对象
			camera.release();
		}
		camera.setDisplayOrientation(90);// 设置预览视频时时竖屏
		// 启动预览功能
		camera.startPreview();
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mSurfaceHolder = arg0;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// if (null != camera) {
		// camera.setPreviewCallback(null);
		// camera.stopPreview();
		// camera.lock();
		// // isPreviewing = false;
		// // mPreviwRate = -1f;
		// camera.release();
		// camera = null;
		// }
	}

}
