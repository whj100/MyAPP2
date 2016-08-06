package com.wuxing.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.methods.HttpOptions;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wuxing.activity.HomeActivity;
import com.wuxing.activity.MapHomeActivity;
import com.wuxing.activity.MyApplication;
import com.wuxing.activity.R;
import com.wuxing.service.MyService;
import com.wuxing.utils.Constant;
import com.wuxing.utils.DisplayUtil;
import com.wuxing.utils.FileUtil;
import com.wuxing.utils.ImageUtil;
import com.wuxing.utils.MyCamera;
import com.wuxing.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements OnClickListener,
		Callback {
	private SurfaceView sfv;
	private float previewRate;
	MediaRecorder mRecorder;
	private SurfaceHolder mSurfaceHolder;
	private Camera camera;
	public boolean isCamera = true;
	private ImageView iv_shexinag, iv_zhaoxiang;
	private TextView tv_map;
	private LinearLayout ll_map;
	MyBroadCastReceiver receiver;
	ImageView iv_right, iv_left;
	private boolean isRecorder = false;

	public CameraFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
	View rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);

		initView(rootView);
		getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
		
//		ll_map = (LinearLayout) rootView.findViewById(R.id.ll_map);
//		ll_map.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				startActivity(new Intent(getActivity(), MapHomeActivity.class));
//			}
//		});
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();


	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (isRecorder) {
			mRecorder.stop();
			mRecorder.reset();
			isRecorder = false;
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
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder.reset();
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
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void initView(View rootView) {
		// TODO Auto-generated method stub
		sfv = (SurfaceView) rootView.findViewById(R.id.sfv);
		iv_shexinag = (ImageView) rootView.findViewById(R.id.iv_shexinag);
		iv_zhaoxiang = (ImageView) rootView.findViewById(R.id.iv_zhaoxiang);
		ll_map = (LinearLayout) rootView.findViewById(R.id.ll_map);
		tv_map = (TextView) rootView.findViewById(R.id.tv_map);
		iv_left = (ImageView) rootView.findViewById(R.id.iv_left);
		iv_right = (ImageView) rootView.findViewById(R.id.iv_right);
		iv_shexinag.setOnClickListener(this);
		iv_zhaoxiang.setOnClickListener(this);
		ll_map.setOnClickListener(this);
		tv_map.setOnClickListener(this);
		iv_left.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		
		
		receiver = new MyBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.CAMERA);
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		getActivity().registerReceiver(receiver, filter);

		mRecorder = new MediaRecorder();

		previewRate = DisplayUtil.getScreenRate(getActivity()); // 默认全屏的比例预览

		mSurfaceHolder = sfv.getHolder();// 取得holder

		mSurfaceHolder.addCallback(this); // holder加入回调接口

		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// setType必须设置，要不出错.
	}

	public class MyBroadCastReceiver extends BroadcastReceiver {
		final String SYSTEM_DIALOG_REASON_KEY = "reason";
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals(Constant.CAMERA)) {
				Intent intent1 = new Intent();
				intent1.setAction(Constant.START_CAMERA);
				getActivity().sendBroadcast(intent1);
			} else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = arg1.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null
						&& reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {

					// 自己随意控制程序，关闭...
					//ToastUtil.show(arg0, "home");
					MyApplication.getInstance().exit();
				}

			}else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				Log.e("电源键关", "");
				MyApplication.getInstance().exit();
			}else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				Log.e("电源键开", "");
			}
			
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_shexinag:
			if (Constant.isCamera) {
				if (isRecorder) {
					Toast.makeText(getActivity(), "正在录制视频中...",
							Toast.LENGTH_SHORT).show();
				} else {
					startRecorder();
					// ToastUtil.show(getActivity(), "开始录制了");

				}

				Constant.isCamera = false;
			} else {
				if (isRecorder) {

					stopRecorder();
					// Intent intent1 = new Intent();
					// intent1.setAction(Constant.STOP_CAMERA);
					// getActivity().sendBroadcast(intent1);
					// ToastUtil.show(getActivity(), "关闭录制了");

				} else {
					Toast.makeText(getActivity(), "请在录制时点击关闭",
							Toast.LENGTH_SHORT).show();
				}
				Constant.isCamera = true;
			}
			break;
		case R.id.iv_zhaoxiang:
			if (!isRecorder) {
				camera.takePicture(mShutterCallback, mRawCallback,
						mJpegPictureCallback);
				iv_zhaoxiang.setEnabled(false);
			} else {
				ToastUtil.show(getActivity(), "正在录像中...");
			}
			break;
		case R.id.ll_map:
		case R.id.tv_map:
			startActivity(new Intent(getActivity(), MapHomeActivity.class));
			break;
		case R.id.iv_left:
			((HomeActivity) getActivity()).select(0);
			break;
		case R.id.iv_right:
			((HomeActivity) getActivity()).select(2);
			break;

		default:
			break;
		}
	}

	/* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
	ShutterCallback mShutterCallback = new ShutterCallback()
	// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback()
	// 拍摄的未压缩原数据的回调,可以为null
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myRawCallback:onPictureTaken...");

		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback()
	// 对jpeg图像数据的回调,最重要的一个回调
	{
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				camera.stopPreview();
				// isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				// 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
				// 90)失效。
				// 图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = null;
				rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
				FileUtil.saveBitmap(rotaBitmap);
				rotaBitmap.recycle();
			}
			
			// 再次进入预览
			camera.startPreview();
			iv_zhaoxiang.setEnabled(true);
		}
	};

	public void stopRecorder() {
		// TODO Auto-generated method stub
		Constant.isRecorder = false;
		iv_shexinag.setImageResource(R.drawable.stop_start_shexiang);
		isRecorder = false;
		try {
			mRecorder.stop();
			mRecorder.reset(); // You can reuse the object by
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void startRecorder() {
		// TODO Auto-generated method stub
		Constant.isRecorder = true;
		iv_shexinag.setImageResource(R.drawable.start_stop_shexiang);
		isRecorder = true;
		try {
			camera.unlock();
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
			//List<Size> sizes = camera.getParameters().getSupportedPictureSizes();
			// mRecorder.setVideoSize(800,480);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			 //mRecorder.setVideoFrameRate(30);// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错

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

			}
		} catch (Exception e) {
			Log.e("开启错误", "");
			e.printStackTrace();
		}
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

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int width,
			int height) {
		// TODO Auto-generated method stub
		// ToastUtil.show(getActivity(), "surfaceChanged");
		if (MyCamera.camera!=null) {
			MyCamera.camera.setPreviewCallback(null);
			MyCamera.camera.stopPreview();
			MyCamera.camera.lock();
			// isPreviewing = false;
			// mPreviwRate = -1f;
			MyCamera.camera.release();
			MyCamera.camera = null;
		}
		camera = Camera.open(); // 获取Camera实例
		try {
			camera.setPreviewDisplay(holder);
			//sfv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		} catch (Exception e) {
			// 如果出现异常，则释放Camera对象
			camera.release();
		}
		camera.setDisplayOrientation(90);// 设置预览视频时时竖屏
		// 启动预览功能
		camera.startPreview();
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
		mSurfaceHolder = holder;

		if (Constant.isRecorder) {
			startRecorder();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		sfv = null;
		mSurfaceHolder = null;
		if (Constant.isRecorder) {
			Intent intent1 = new Intent(getActivity(), MyService.class);
			getActivity().startService(intent1);
		}
	}

}
