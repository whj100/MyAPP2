package com.wuxing.activity;

import java.security.acl.Group;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wuxing.map.util.AMapUtil;
import com.wuxing.utils.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShareActivity extends Activity implements OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private String start , end;
	private LatLonPoint mStartPoint,mEndPoint;
	private ImageView iv_arrow_down,iv_add;
	private LinearLayout ll_add;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_share);
		MyApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		start = intent.getStringExtra("start");
		end = intent.getStringExtra("end");
		
		if (start!=null&&end!=null) {
			mStartPoint = new LatLonPoint(Double.parseDouble(start.split(",")[0]),
					Double.parseDouble(start.split(",")[1]));
			
			mEndPoint = new LatLonPoint(Double.parseDouble(end.split(",")[0]),
					Double.parseDouble(end.split(",")[1]));
		}

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		aMap = mapView.getMap();
		
		
		
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				AMapUtil.convertToLatLng(new LatLonPoint(Constant.LA, Constant.LO)), 15));
		addMarker(new LatLng(Constant.LA,  Constant.LO));
		
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		iv_arrow_down = (ImageView) findViewById(R.id.iv_arrow_down);
		iv_arrow_down.setOnClickListener(this);
		ll_add = (LinearLayout) findViewById(R.id.ll_add);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		iv_add.setOnClickListener(this);
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
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_normal));
		Marker marker = aMap.addMarker(markerOptions);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_arrow_down:
			ll_add.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_add:
			Intent intentGroup = new Intent(ShareActivity.this,GroupActivity.class);
			intentGroup.putExtra("start", start);
			intentGroup.putExtra("end", end);
			startActivity(intentGroup);
			ll_add.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}



	
}
