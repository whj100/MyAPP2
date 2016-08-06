package com.wuxing.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviGuide;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.wuxing.map.route.DriveRouteDetailActivity;
import com.wuxing.map.util.AMapUtil;
import com.wuxing.map.util.TTSController;
import com.wuxing.map.util.ToastUtil;

/**

 */

public class GPSNaviActivity extends BaseActivity implements AMapNaviListener,
		AMapNaviViewListener, OnClickListener, OnMapLoadedListener {
	AMapNaviView mAMapNaviView;
	AMapNavi mAMapNavi;
	ImageView iv_menu;

	TTSController mTtsManager;
	NaviLatLng mEndLatlng; // = new NaviLatLng(39.925846, 116.432765);
	NaviLatLng mStartLatlng;// = new NaviLatLng(39.925041, 116.437901);
	List<NaviLatLng> mStartList = new ArrayList<NaviLatLng>();
	List<NaviLatLng> mEndList = new ArrayList<NaviLatLng>();
	List<NaviLatLng> mWayPointList;

	private List<AMapNaviStep> steps;
	private List<AMapNaviLink> links;
	private List<AMapNaviGuide> guides;
	private AMapNaviPath mAMapNaviPath;

	String startJingWei, endJingWei;
	TextView  tv_up, tv_down,  tv_distance, tv_time;
	ImageView iv_over,iv_zhinanzhen, iv_lukuang, iv_jia, iv_jian,tv_jixu;
	boolean b = true;
	boolean isQuanLan = false;
	private float max;
	private float min;
	private float level = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_basic_navi);
		MyApplication.getInstance().addActivity(this);
		startProgressDialog();
		mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);

		mAMapNaviView.onCreate(savedInstanceState);
		initStartAndEnd();
		mAMapNaviView.setAMapNaviViewListener(this);

		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();
		mTtsManager.startSpeaking();

		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(this);
		mAMapNavi.addAMapNaviListener(mTtsManager);
		mAMapNavi.setEmulatorNaviSpeed(150);

		AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
		options.setLayoutVisible(false);
		mAMapNaviView.setViewOptions(options);
		mAMapNaviView.getMap().moveCamera(CameraUpdateFactory.zoomTo(level));
		UiSettings mUisSettings = mAMapNaviView.getMap().getUiSettings();
		mUisSettings.setCompassEnabled(true);
		max = mAMapNaviView.getMap().getMaxZoomLevel();
		min = mAMapNaviView.getMap().getMinZoomLevel();
		mAMapNaviView.getMap().setOnMapLoadedListener(this);
		initView();

	}

	private void initView() {
		// TextView tv_jieshu,tv_up,tv_down,tv_jixu,tv_distance,tv_time;
		// ImageView iv_zhinanzhen,iv_quanlan,iv_lukuang,iv_jia,iv_jian;
		iv_over = (ImageView) findViewById(R.id.iv_over);
		tv_up = (TextView) findViewById(R.id.tv_up);
		tv_down = (TextView) findViewById(R.id.tv_down);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_jixu = (ImageView) findViewById(R.id.tv_jixu);
		iv_lukuang = (ImageView) findViewById(R.id.iv_lukuang);
		iv_jia = (ImageView) findViewById(R.id.iv_jia);
		iv_jian = (ImageView) findViewById(R.id.iv_jian);

		iv_over.setOnClickListener(this);
		tv_jixu.setOnClickListener(this);

		iv_lukuang.setOnClickListener(this);
		iv_jia.setOnClickListener(this);
		iv_jian.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_over:
			onNaviCancel();
			break;
		case R.id.tv_jixu:
			if (isQuanLan) {
				mAMapNaviView.recoverLockMode();
				tv_jixu.setImageResource(R.drawable.quanlan_normal);
				isQuanLan = false;
			}else {
				mAMapNaviView.displayOverview();
				tv_jixu.setImageResource(R.drawable.quanlan_press);
				isQuanLan = true;
			}
			break;
		case R.id.iv_lukuang:

			if (b) {
				mAMapNaviView.setTrafficLine(b);
				iv_lukuang.setImageResource(R.drawable.lukuang_press);
				
				b = false;
			} else {
				mAMapNaviView.setTrafficLine(b);
				iv_lukuang.setImageResource(R.drawable.lukuang_normal);
				b = true;
			}
			break;
		case R.id.iv_jia:
			level++;
			zoomAMapout(level);
			
			break;

		case R.id.iv_jian:
			// 设置缩放级别
			level--;
			zoomAMapin(level);
			break;
		default:
			break;
		}

	}

	public void zoomAMapin(float level) {

		if (level >= min) {
			mAMapNaviView.getMap()
					.moveCamera(CameraUpdateFactory.zoomTo(level));
			//Log.e("缩小级别", level + "");
		} else {
			ToastUtil.show(GPSNaviActivity.this, "已经是最小了");
		}
	}

	public void zoomAMapout(float level) {

		if (level <= max) {
			mAMapNaviView.getMap()
					.moveCamera(CameraUpdateFactory.zoomTo(level));
			//Log.e("缩小级别", level + "");
		} else {
			ToastUtil.show(GPSNaviActivity.this, "已经是最大了");
		}
	}

	private void initStartAndEnd() {
		// TODO Auto-generated method stub
		startJingWei = getIntent().getStringExtra("startJingWei");
		endJingWei = getIntent().getStringExtra("endJingWei");
		if (startJingWei!=null&&endJingWei!=null) {

			mStartLatlng = new NaviLatLng(Double.parseDouble(startJingWei
					.split(",")[0]), Double.parseDouble(startJingWei.split(",")[1]));

			mEndLatlng = new NaviLatLng(
					Double.parseDouble(endJingWei.split(",")[0]),
					Double.parseDouble(endJingWei.split(",")[1]));
		}else {
			ToastUtil.show(this, "起始点异常");
			return;
		}
		//Log.i("导航起点", mStartLatlng.toString());
		//Log.i("导航终点", mEndLatlng.toString());
	}

	/**
	 * 如果使用无起点算路，请这样写
	 */
	private void noStartCalculate() {
		// 无起点算路须知：
		// AMapNavi在构造的时候，会startGPS，但是GPS启动需要一定时间
		// 在刚构造好AMapNavi类之后立刻进行无起点算路，会立刻返回false
		// 给人造成一种等待很久，依然没有算路成功 算路失败回调的错觉
		// 因此，建议，提前获得AMapNavi对象实例，并判断GPS是否准备就绪

		if (mAMapNavi.isGpsReady())
			if (LuXianActivity.METHOD == 2) {
				mAMapNavi.calculateDriveRoute(mEndList, mWayPointList,
						PathPlanningStrategy.DRIVING_DEFAULT);
			}else {
				mAMapNavi.calculateWalkRoute(mStartLatlng, mEndLatlng);
			}
	}

	@Override
	public void onNaviInfoUpdate(NaviInfo naviinfo) {
		// TODO Auto-generated method stub
//		String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
//		String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
		String roadName = naviinfo.getCurrentRoadName();
		int distance = naviinfo.getCurStepRetainDistance();
		int time = naviinfo.getCurStepRetainTime();
		if (roadName.equals("无名道路")) {
			roadName = "";
		}
		tv_up.setText(AMapUtil.getFriendlyLength(distance));
		tv_down.setText(roadName);
		
		int pathDistance = naviinfo.getPathRetainDistance();
		int pathTime = naviinfo.getPathRetainTime();
		
		tv_distance.setText(AMapUtil.getFriendlyLength(pathDistance));
		tv_time.setText(AMapUtil.getFriendlyTime(pathTime));
		
		ToastUtil.show(GPSNaviActivity.this, naviinfo.getDirection());
		
		
		
		int currentStep = naviinfo.getCurStep();
		int currentLink = naviinfo.getCurLink();
		//Log.e("currentLink", currentLink + "");
		//Log.e("currentStep", currentStep + "");
		
		
		

	}
	@SuppressWarnings("deprecation")
	@Override
	public void onNaviInfoUpdated(AMapNaviInfo aMapNaviinfo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCalculateRouteSuccess() {
		mAMapNavi.startNavi(NaviType.GPS);
//	     //概览
//        guides = mAMapNavi.getNaviGuideList();
//
//        //详情
//        mAMapNaviPath = mAMapNavi.getNaviPath();
//        steps = mAMapNaviPath.getSteps();
//
//        if (guides.size() == steps.size()) {
//
//            Toast.makeText(this, "看log", Toast.LENGTH_SHORT).show();
//            for (int i = 0; i < steps.size() - 1; i++) {
//                //guide step相生相惜，指的是大导航段
//                AMapNaviGuide guide = guides.get(i);
//                Log.d("wlx", "AMapNaviGuide 路线经纬度:" + guide.getCoord() + "");
//                Log.d("wlx", "AMapNaviGuide 路线名:" + guide.getName() + "");
//                Log.d("wlx", "AMapNaviGuide 路线长:" + guide.getLength() + "m");
//                Log.d("wlx", "AMapNaviGuide 路线耗时:" + guide.getTime() + "s");
//                Log.d("wlx", "AMapNaviGuide 路线IconType" + guide.getIconType() + "");
//                AMapNaviStep step = steps.get(i);
//                Log.d("wlx", "AMapNaviStep 距离:" + step.getLength() + "m" + " " + "耗时:" + step.getTime() + "s");
//                Log.d("wlx", "AMapNaviStep 红绿灯个数:" + step.getTrafficLightNumber());
//
//
//                //link指的是大导航段中的小导航段
//                links = step.getLinks();
//                for (AMapNaviLink link : links) {
////          请看com.amap.api.navi.enums.RoadClass，以及帮助文档
//                    Log.d("wlx", "AMapNaviLink 道路名:" + link.getRoadName() + " " + "道路等级:" + link.getRoadClass());
////          请看com.amap.api.navi.enums.RoadType，以及帮助文档
//                    Log.d("wlx", "AMapNaviLink 道路类型:" + link.getRoadType());
//
//                }
//            }
//
//        } else {
//            Toast.makeText(this, "BUG！请联系我们", Toast.LENGTH_SHORT).show();
//        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAMapNaviView.onResume();
		if (mStartLatlng!=null&&mEndLatlng!=null) {
			mStartList.add(mStartLatlng);
			mEndList.add(mEndLatlng);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAMapNaviView.onPause();

		// 仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
		mTtsManager.stopSpeaking();
		//
		// 停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
		// mAMapNavi.stopNavi();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNaviView.onDestroy();
		// since 1.6.0
		// 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();
		// 请自行执行
		mAMapNavi.stopNavi();
		mAMapNavi.destroy();
		mTtsManager.destroy();
	}

	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNaviBackClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNaviCancel() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviViewLoaded() {
		// TODO Auto-generated method stub
		Log.i("wlx", "导航页面加载成功");
	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideCross() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideLaneInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyParallelRoad(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub
		Log.i("onArriveDestination", "到达目的地");
	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateMultipleRoutesSuccess(int[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		// TODO Auto-generated method stub
		Log.e("规划路径失败", "");
	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub
		if (arg0) {
			
		}else {
			ToastUtil.show(GPSNaviActivity.this, "请开启GPS!");
		}
	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub
		
			
		if (LuXianActivity.METHOD == 3||LuXianActivity.METHOD == 1) {
			mAMapNavi.calculateWalkRoute(mStartLatlng, mEndLatlng);
			return;
		}
		mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,
				PathPlanningStrategy.DRIVING_DEFAULT);
	}
	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCross(AMapNaviCross arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAimlessModeStatistics(AimLessModeStat arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		stopProgressDialog();
	}

}
