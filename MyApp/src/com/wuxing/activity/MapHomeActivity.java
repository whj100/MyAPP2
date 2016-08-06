package com.wuxing.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wuxing.map.util.AMapUtil;
import com.wuxing.service.MyService;
import com.wuxing.utils.Constant;
import com.wuxing.utils.ToastUtil;

public class MapHomeActivity extends BaseActivity implements OnClickListener,
		LocationSource, OnGeocodeSearchListener, AMapLocationListener {
	private AMap aMap;
	private MapView mapView;
	private TextView tv_des;
	ImageView iv_right, iv_camera, iv_navi,iv_loc;
	private boolean isFirst = true;
	Marker marker;
	private GeocodeSearch geocoderSearch;
	String str = "";
	private TextView et_search;
	public static final int HOME = 99;
	LatLng latlng;
	private String endJingWei;
	MyReceiver receiver;
	// 声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	// 声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;
	private OnLocationChangedListener mListener = null;// 定位监听器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_home);
		MyApplication.getInstance().addActivity(this);
		startProgressDialog();
		mapView = (MapView) findViewById(R.id.map);
		tv_des = (TextView) findViewById(R.id.tv_des);
		et_search = (TextView) findViewById(R.id.et_search);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_camera = (ImageView) findViewById(R.id.iv_camera);
		iv_navi = (ImageView) findViewById(R.id.iv_navi);
		iv_loc = (ImageView) findViewById(R.id.iv_loc);
		
		et_search.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		iv_camera.setOnClickListener(this);
		iv_navi.setOnClickListener(this);
		iv_loc.setOnClickListener(this);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		// 设置定位监听
		aMap = mapView.getMap();
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setLocationSource(this);
		// 设置定位层可用
		aMap.setMyLocationEnabled(true);
		
		UiSettings mUisSettings = aMap.getUiSettings();
		mUisSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
		// 初始化定位
		mLocationClient = new AMapLocationClient(getApplication());
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 声明和设置定位回调监听器
		mLocationClient.setLocationListener(this);

		// 启动定位
		mLocationClient.startLocation();
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_red));
		myLocationStyle.radiusFillColor(android.R.color.transparent);
		myLocationStyle.strokeColor(android.R.color.transparent);
		aMap.setMyLocationStyle(myLocationStyle);

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.action.luxiang");
		intentFilter.addAction("com.service.close");
		registerReceiver(receiver, intentFilter);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.iv_loc:
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					Constant.LA, Constant.LO)));
			break;
		case R.id.et_search:
			Intent intent = new Intent(MapHomeActivity.this,
					StartEndActivity.class);
			intent.putExtra("action", "com.action.luxiang");
			startActivityForResult(intent, HOME);
			break;
		case R.id.iv_right:
		
			startActivity(new Intent(MapHomeActivity.this, LuXianActivity.class));
			break;
		case R.id.iv_camera:
			iv_camera.setEnabled(false);
			startProgressDialog();
			if (Constant.isRecorder) {
				Intent intent3 = new Intent();
				intent3.setAction(Constant.STOP_CAMERA);
				sendBroadcast(intent3);
				Intent intentService = new Intent(MapHomeActivity.this,
						MyService.class);
				stopService(intentService);
			} else {
				
				Intent intent3 = new Intent(MapHomeActivity.this,
						HomeActivity.class);	
				startActivity(intent3);
				finish();
				
			}
		
			break;
		case R.id.iv_navi:
			if (endJingWei != null) {
				Intent intentNavi = new Intent(MapHomeActivity.this,
						GPSNaviActivity.class);
				intentNavi.putExtra("startJingWei", Constant.LA + ","
						+ Constant.LO);
				intentNavi.putExtra("endJingWei", endJingWei);
				startActivity(intentNavi);
				endJingWei = null;
			} else {
				ToastUtil.show(MapHomeActivity.this, "请选择终点！");
			}
			break;

		default:
			break;
		}
	}
	/**
	 * 往地图上添加marker
	 * 
	 * @param latLng
	 */
	private void addMarker(LatLng latLng) {

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		// markerOptions.title("[我的位置]");
		// markerOptions.snippet(desc);
		markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_red));
		marker = aMap.addMarker(markerOptions);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (Constant.isRecorder) {
				Intent intent3 = new Intent();
				intent3.setAction(Constant.STOP_CAMERA);
				sendBroadcast(intent3);
				Intent intentService = new Intent(MapHomeActivity.this,
						MyService.class);
				stopService(intentService);
			} else {
				
				Intent intent3 = new Intent(MapHomeActivity.this,
						HomeActivity.class);	
				startActivity(intent3);
				finish();
				
			}
//			if (Constant.isRecorder) {
//				Intent intent = new Intent();
//				intent.setAction(Constant.STOP_CAMERA);
//				sendBroadcast(intent);
//				Intent intentService = new Intent(MapHomeActivity.this,
//						MyService.class);
//				stopService(intentService);
//			} else {
//				Intent intent2 = new Intent(MapHomeActivity.this,
//						HomeActivity.class);
//				MapHomeActivity.this.startActivity(intent2);
//			}
			return true;
		} else
			return super.onKeyDown(keyCode, event);

	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String type = arg1.getAction();
			Log.e("guanbosdfsdfsd", type);
			if (type.equals("com.action.luxiang")) {
				String jingWei = arg1.getStringExtra("jingwei");
				Log.e("s起点", jingWei);
				endJingWei = jingWei;
				String str[] = jingWei.split(",");
				latlng = new LatLng(Double.parseDouble(str[0]),
						Double.parseDouble(str[1]));
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
				if (marker == null) {
					addMarker(latlng);
				}else {
					marker.setPosition(latlng);
				}
			} else if (type.equals("com.service.close")) {

				Log.e("接收停止服务", "消息");
				Intent intent2 = new Intent(MapHomeActivity.this,
						HomeActivity.class);
				startActivity(intent2);

			}

		}
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {

		GeocodeQuery query = new GeocodeQuery(name, Constant.cityCode);//
		// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);//
		// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	//
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		iv_camera.setEnabled(true);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		// deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopProgressDialog();
		unregisterReceiver(receiver);
		mapView.onDestroy();
		if (null != mLocationClient) {
			mLocationClient.onDestroy();
			mLocationClient = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			LatLng loclat = new LatLng(Constant.LA, Constant.LO);
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(loclat));
			if (marker == null) {
				addMarker(loclat);
			}else {
				marker.setPosition(loclat);
			}
			break;
		case Constant.POIRESULT:
			// start = data.getStringExtra("poiAddress");
			String wei = data.getStringExtra("la");
			String jing = data.getStringExtra("lo");
			if (wei == null || jing == null) {
				return;
			}
			endJingWei = wei + "," + jing;
			LatLng lat = new LatLng(Double.parseDouble(wei),
					Double.parseDouble(jing));
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(lat));
			if (marker == null) {
				addMarker(lat);
			}else {
				marker.setPosition(lat);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
	}
	// 地理搜索
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				// aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				// AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));

				String addressName = "经纬度值:" + address.getLatLonPoint()
						+ "\n位置描述:" + address.getFormatAddress();

				Log.e("搜索的位置定位", addressName);
				// ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				// /ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else {
			// ToastUtil.showerror(this, rCode);
		}
	}

	// 逆地理
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			stopProgressDialog();
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getNeighborhood();

				tv_des.setText(addressName);

				Constant.cityCode = result.getRegeocodeAddress().getCityCode();
				// Log.e("当前位置", addressName);

			} else {

			}
		} else {

		}
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			stopProgressDialog();
			int s = amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
			double d1 = amapLocation.getLatitude();// 获取纬度
			double d2 = amapLocation.getLongitude();// 获取经度
			Constant.LA = d1;
			Constant.LO = d2;
			double d3 = amapLocation.getAccuracy();// 获取精度信息
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(amapLocation.getTime());
			String Stime = df.format(date);// 定位时间
			String s1 = amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果
			String s2 = amapLocation.getCountry();// 国家信息
			String s3 = amapLocation.getProvince();// 省信息
			String s4 = amapLocation.getCity();// 城市信息
			String s5 = amapLocation.getDistrict();// 城区信息
			String s6 = amapLocation.getRoad();// 街道信息
			String s7 = amapLocation.getCityCode();// 城市编码
			Constant.cityCode = s7;
			String s8 = amapLocation.getAdCode();// 地区编码
			String s9 = amapLocation.getLocationDetail();
			tv_des.setText(s1);
			// 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
			if (isFirst) {
				// 设置缩放级别
				//aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
				// 将地图移动到定位点
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						amapLocation.getLatitude(), amapLocation.getLongitude())));
				// 点击定位按钮 能够将地图的中心移动到定位点
				mListener.onLocationChanged(amapLocation);
				isFirst = false;
			}

		}
	}

}
