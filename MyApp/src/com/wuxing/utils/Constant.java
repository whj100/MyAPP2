package com.wuxing.utils;

public class Constant {
	//服务器地址
	public static final String SERVER_ADDRESS = "http://www.5xclw.cn/ecmobile/";
	public static final String SERVER_IMG = "http://www.5xclw.cn/";
	
	public static final int START = 1;
	public static final int END = 2;
	public static final int POIRESULT = 3;
	public static final int  ADD_FRIEND = 4;
	//控制录像
	public static final String CAMERA = "com.action.camera";
	public static final String START_CAMERA = "com.service.startcamera";
	public static final String STOP_CAMERA = "com.service.stopcamera";
	//控制服务要操作的类型
	public static final int START_OPERATION = 1;
	public static final int STOP_OPERATION = 2;
	
	//实时定位的经纬度
	public static double LA; 
	public static double LO; 
	public static  String cityCode = "";
	//用户ID
	public static  String UID= "";
	//判断起始点
	public static String FLAG = "";
	
	//全局监控是否在录像
	public static boolean isRecorder = false;
	public static boolean isCamera = true;

}
