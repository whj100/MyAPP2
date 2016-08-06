package com.wuxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wuxing.map.util.AMapUtil;
import com.wuxing.map.util.ToastUtil;
import com.wuxing.utils.Constant;

public class DiTuXuanWeiActivity extends BaseActivity implements
		OnCameraChangeListener, OnGeocodeSearchListener, OnMapLoadedListener {
	// 显示地图需要的变量
	private MapView mapView;// 地图控件
	private AMap aMap;// 地图对象
	private TextView tv_address;
	private Button btn_sure;
	private Marker marker;
	private String addressName ;
	private GeocodeSearch geocoderSearch;
	private String jingwei ;//地图选点返回的经纬度
	private ImageView iv_location;
	private String action ,address ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_di_tu_xuan_wei);
		MyApplication.getInstance().addActivity(this);
		startProgressDialog();
		action = getIntent().getStringExtra("action");
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		aMap = mapView.getMap();
		UiSettings mUisSettings = aMap.getUiSettings();
		mUisSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		// 将地图移动到定位点
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
				Constant.LA, Constant.LO)));
		addMarker(new LatLng(Constant.LA, Constant.LO));
		
		aMap.setOnCameraChangeListener(this);
		
		

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		aMap.setOnMapLoadedListener(this);
		
		tv_address = (TextView) findViewById(R.id.tv_address);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		iv_location = (ImageView) findViewById(R.id.iv_location);
		iv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						Constant.LA, Constant.LO)));
				addMarker(new LatLng(Constant.LA, Constant.LO));
			
			}
		});
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str = tv_address.getText().toString();
				address= addressName;
				if (address==null||jingwei==null||TextUtils.isEmpty(str)) {
					ToastUtil.show(DiTuXuanWeiActivity.this, "选择位置中");
					return;
				}else {
					Intent intent = new Intent();
					intent.putExtra("address", address);
					intent.putExtra("jingwei", jingwei);
					if (action!=null) {
						intent.setAction(action);
					}
					sendBroadcast(intent
							
							);
					finish();
					
				}
			}
		});
	}
	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {

		GeocodeQuery query = new GeocodeQuery(name, Constant.cityCode);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}
	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 往地图上添加marker
	 * 
	 * @param latLng
	 */
	private void addMarker(LatLng latLng) {
		aMap.clear();
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		// markerOptions.title("[我的位置]");
		// markerOptions.snippet(desc);
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
		marker= aMap.addMarker(markerOptions);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
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
		mapView.onDestroy();
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		Log.e("屏幕中心的位置", arg0.target.latitude + "" + arg0.target.longitude);
		marker.setPosition(new LatLng(arg0.target.latitude,
				arg0.target.longitude));
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		Log.e("屏幕中心的位置", arg0.target.latitude + "" + arg0.target.longitude);
		jingwei = arg0.target.latitude+","+arg0.target.longitude;
		getAddress(new LatLonPoint(arg0.target.latitude, arg0.target.longitude));

	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
				// geoMarker.setPosition(AMapUtil.convertToLatLng(address
				// .getLatLonPoint()));
				// addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
				// + address.getFormatAddress();
				// ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else {
			// ToastUtil.showerror(this, rCode);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				// result.getRegeocodeAddress().getCity()
				// + result.getRegeocodeAddress().getDistrict()
				if (result.getRegeocodeAddress().getNeighborhood().equals("")) {
					addressName = result.getRegeocodeAddress().getFormatAddress()
							+ "附近";
					if (!addressName.equals("")) {
						tv_address.setText((addressName.split("省")[1]));
						Log.e("当前位置", addressName);
					}else {
						Log.e("addressName", "为空");
					}
				}else {
					addressName =result.getRegeocodeAddress().getNeighborhood();
					tv_address.setText(addressName);
				}
				
				
			} else {
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else {
			// ToastUtil.showerror(this, rCode);
		}
	}
	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		stopProgressDialog();
	}

}
