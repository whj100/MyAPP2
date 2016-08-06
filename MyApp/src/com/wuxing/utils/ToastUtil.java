package com.wuxing.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

public class ToastUtil {
	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	//判断手机是否有摄像头
	private static boolean checkCameraFacing(final int facing) {
	    if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
	        return false;
	    }
	    final int cameraCount = Camera.getNumberOfCameras();
	    CameraInfo info = new CameraInfo();
	    for (int i = 0; i < cameraCount; i++) {
	        Camera.getCameraInfo(i, info);
	        if (facing == info.facing) {
	            return true;
	        }
	    }
	    return false;
	}
	public static boolean hasBackFacingCamera() {
	    final int CAMERA_FACING_BACK = 0;
	    return checkCameraFacing(CAMERA_FACING_BACK);
	}
	public static boolean hasFrontFacingCamera() {
	    final int CAMERA_FACING_BACK = 1;
	    return checkCameraFacing(CAMERA_FACING_BACK);
	}
	public static int getSdkVersion() {
	    return android.os.Build.VERSION.SDK_INT;
	}
	

	/**
	* 判断手机是否有SD卡。
	* 
	* @return 有SD卡返回true，没有返回false。
	*/
	public static boolean hasSDCard() {
	return Environment.MEDIA_MOUNTED.equals(Environment
	.getExternalStorageState());
	}

}
