package com.wuxing.map.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.wuxing.activity.BaseActivity;
import com.wuxing.activity.GPSNaviActivity;
import com.wuxing.activity.LuXianActivity;
import com.wuxing.activity.R;
import com.wuxing.bean.PositionBean;
import com.wuxing.bean.PositionBean.Data;
import com.wuxing.map.util.AMapUtil;
import com.wuxing.map.util.ToastUtil;
import com.wuxing.utils.Constant;
import com.wuxing.utils.NetUtils;
import com.wuxing.villoy.VolleyInterface;
import com.wuxing.villoy.VolleyRequest;

public class RouteActivity extends BaseActivity implements OnMapClickListener,
		OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
		OnRouteSearchListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private Context mContext;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;
	private BusRouteResult mBusRouteResult;
	private WalkRouteResult mWalkRouteResult;
	private DrivePath drivePath;
	private WalkPath walkPath;
	private LatLonPoint mStartPoint; // = new LatLonPoint(39.917636,
										// 116.397743);// 起点，
	private LatLonPoint mEndPoint;// = new LatLonPoint(39.984947, 116.494689);//
									// 终点，

	public LatLonPoint threadStartPoint; // = new LatLonPoint(39.917636,
	// 116.397743);// 起点，
	public LatLonPoint threadEndPoint;// = new LatLonPoint(39.984947,
										// 116.494689);//
	// 终点，

	private String mCurrentCityName = "北京";

	private final int ROUTE_TYPE_BUS = 1;
	private final int ROUTE_TYPE_DRIVE = 2;
	private final int ROUTE_TYPE_WALK = 3;

	private LinearLayout mBusResultLayout;
	private RelativeLayout mBottomLayout;
	private ImageView mBus;
	private ImageView mDrive;
	private ImageView mWalk;
	private ListView mBusResultList;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private TextView tv_start;
	private ImageView iv_menu, iv_startNavi;
	private String start, end, strType, groupId;
	private boolean run;
	private boolean first = true;
	private List<Marker> markers = new ArrayList<Marker>();
	Handler handler = new Handler();
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				List<Data> datas = (List<Data>) msg.obj;
				if (first) {
					for (int i = 0; i < datas.size(); i++) {
						
						Marker marker = addMarkerPositon(AMapUtil.convertToLatLng(mStartPoint), 1, datas.get(i).getUser_name());
						marker.setVisible(false);
						markers.add(marker);
					}
					first = false;
				}
				for (int i = 0; i < datas.size(); i++) {
					Data data = datas.get(i);
					if (data.getLonandlat() != null) {
						
						double lo = Double
								.parseDouble(data
										.getLonandlat()
										.split(",")[0]);
						double la = Double
								.parseDouble(data
										.getLonandlat()
										.split(",")[1]);
						for (int j = 0; j < markers.size(); j++) {
							Marker marker = markers.get(i);
							if (data.getUser_name().equals(marker.getTitle())) {
								marker.setVisible(true);
								marker.setPosition(new LatLng(la, lo));
								marker.showInfoWindow();
							}
						}

					}
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity);
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		Intent intent = getIntent();

		strType = intent.getStringExtra("strType");
		if ("person".equals(strType)) {
			start = intent.getStringExtra("startJingWei");
			end = intent.getStringExtra("endJingWei");
			getStartAndEnd();
			initType(LuXianActivity.METHOD);
		} else if ("group".equals(strType)) {
			groupId = intent.getStringExtra("group_id");

			initRoute();

			
		}

	}
//	protected View getMyView(String img,String pm_val) {
//		View view=getLayoutInflater().inflate(R.layout.mymarker, null);
//		ImageView iv_val = (ImageView) view.findViewById(R.id.imageView1);
//		TextView tv_val=(TextView) view.findViewById(R.id.marker_tv_val);
//		iv_val.setImageResource(R.drawable.text_img);
//		tv_val.setText(pm_val);
//		return view;
//		}
//	protected Bitmap getMyBitmap(String pm_val) {
//		Bitmap bitmap = BitmapDescriptorFactory.fromResource(
//		R.drawable.text_img).getBitmap();
//		bitmap = Bitmap.createBitmap(bitmap, 0 ,0, bitmap.getWidth(),
//		bitmap.getHeight());
//		Canvas canvas = new Canvas(bitmap);
//		TextPaint textPaint = new TextPaint();
//		textPaint.setAntiAlias(true);
//		textPaint.setTextSize(22f);
//		textPaint.setColor(getResources().getColor(R.color.blue));
//		canvas.drawText(pm_val, 17, 35 ,textPaint);// 设置bitmap上面的文字位置
//		return bitmap;
//		}
	protected Marker addMarkerPositon(LatLng lat, int imgid, String title) {
		// TODO Auto-generated method stub
		return aMap
				.addMarker(new MarkerOptions()
						.position(lat)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.text_img))
						.title(title)
						.setInfoWindowOffset(0, 0)
						);
		
	}

	private void initRoute() {
		// TODO Auto-generated method stub
		// http://www.5xclw.cn/ecmobile/
		// ?url=/wx_ajax_c/get_roadmap
		// &group_id=204263785517547952
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "/wx_ajax_c/get_roadmap");
		map.put("group_id", groupId);
		String url = Constant.SERVER_ADDRESS + NetUtils.getUrlParamsByMap(map);
		VolleyRequest.requestGet(this, url, "getRoute",
				new VolleyInterface(this, VolleyInterface.mListener,
						VolleyInterface.mErrorListener) {

					@Override
					public void onMySuccess(String result) {
						// TODO Auto-generated method stub
						// {"success":"True",
						// "message":"34.72034695095486,113.7174915907118@34.74673199237378,113.72367605007257"}
						try {
							JSONObject job = new JSONObject(result);
							if (job.getBoolean("success")) {
								String strRoute = job.getString("message");
								String[] strs = strRoute.split("@");
								start = strs[0];
								end = strs[1];
								mStartPoint = new LatLonPoint(Double
										.parseDouble(start.split(",")[0]),
										Double.parseDouble(start.split(",")[1]));
								mEndPoint = new LatLonPoint(Double
										.parseDouble(end.split(",")[0]), Double
										.parseDouble(end.split(",")[1]));
								initType(LuXianActivity.METHOD);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onMyError(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void updateData() {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {

			@Override
			public void run() {
				// 获取组员的位置
				// http://www.5xclw.cn/ecmobile/
				// ?url=/wx_ajax_c/update_lonandlat
				// &group_id=204612140626084288
				// &user_name=
				// &lonandlat=123,456
				Map<String, String> map = new HashMap<String, String>();
				map.put("url", "/wx_ajax_c/update_lonandlat");
				map.put("group_id", groupId);
				map.put("user_name", sp.getString("hxusername", ""));
				map.put("lonandlat", Constant.LO + "," + Constant.LA);
				String url = Constant.SERVER_ADDRESS
						+ NetUtils.getUrlParamsByMap(map);
				VolleyRequest.requestGet(RouteActivity.this, url,
						"update_lonandlat", new VolleyInterface(
								RouteActivity.this, VolleyInterface.mListener,
								VolleyInterface.mErrorListener) {
							@Override
							public void onMySuccess(String result) {
								// TODO Auto-generated method stub
								Log.e("update_lonandlat", result);
								Gson gson = new Gson();
								PositionBean pb = gson.fromJson(result,
										PositionBean.class);
								List<Data> datas = pb.getData();
								Message msg = mHandler.obtainMessage();
								msg.what = 1;
								msg.obj = datas;
								mHandler.sendMessage(msg);

							}

							@Override
							public void onMyError(VolleyError error) {
								// TODO Auto-generated method stub
								ToastUtil.show(RouteActivity.this, "获取位置失败！");
							}
						});

				if (run) {
					handler.postDelayed(this, 3000);
				}
			}
		});
	}

	private void getStartAndEnd() {
		// TODO Auto-generated method stub
		if (start != null && end != null) {

			mStartPoint = new LatLonPoint(
					Double.parseDouble(start.split(",")[0]),
					Double.parseDouble(start.split(",")[1]));
			mEndPoint = new LatLonPoint(Double.parseDouble(end.split(",")[0]),
					Double.parseDouble(end.split(",")[1]));
			setfromandtoMarker();
		} else {
			Log.e("起始点为空", "");
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_startNavi:

			Intent intentNavi = new Intent(RouteActivity.this,
					GPSNaviActivity.class);
			intentNavi.putExtra("startJingWei", start);
			intentNavi.putExtra("endJingWei", end);
			startActivity(intentNavi);

			break;
		case R.id.iv_navi:
			if (start != null && end != null) {
				Intent intent = new Intent(RouteActivity.this,
						GPSNaviActivity.class);
				intent.putExtra("startJingWei", start);
				intent.putExtra("endJingWei", end);
				startActivity(intent);
			}
			break;
		case R.id.iv_menu:
			switch (LuXianActivity.METHOD) {
			case 1:
				if (walkPath != null && mWalkRouteResult != null) {
					Intent intent = new Intent(mContext,
							WalkRouteDetailActivity.class);
					intent.putExtra("walk_path", walkPath);
					intent.putExtra("walk_result", mWalkRouteResult);
					startActivity(intent);

				} else {
					ToastUtil.show(RouteActivity.this, "无详情");
				}
				break;
			case 2:
				if (drivePath != null && mDriveRouteResult != null) {
					Intent intent = new Intent(mContext,
							DriveRouteDetailActivity.class);
					intent.putExtra("drive_path", drivePath);
					intent.putExtra("drive_result", mDriveRouteResult);
					startActivity(intent);

				} else {
					ToastUtil.show(RouteActivity.this, "无详情");
				}
				break;

			case 3:
				if (walkPath != null && mWalkRouteResult != null) {
					Intent intent = new Intent(mContext,
							WalkRouteDetailActivity.class);
					intent.putExtra("walk_path", walkPath);
					intent.putExtra("walk_result", mWalkRouteResult);
					startActivity(intent);

				} else {
					ToastUtil.show(RouteActivity.this, "无详情");
				}
				break;
			default:
				break;
			}

			break;
		default:
			break;
		}
	}

	public void initType(int type) {
		// TODO Auto-generated method stub
		// ROUTE_TYPE_BUS = 1;
		// ROUTE_TYPE_DRIVE = 2;
		// ROUTE_TYPE_WALK = 3;
		switch (type) {
		case 1:
			walk();
			break;
		case 2:
			drive();
			break;
		case 3:
			walk();
			break;

		default:
			break;
		}
	}

	private void setfromandtoMarker() {
		if (mStartPoint != null && mEndPoint != null) {
			aMap.addMarker(new MarkerOptions().position(
					AMapUtil.convertToLatLng(mStartPoint)).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.start)));
			aMap.addMarker(new MarkerOptions().position(
					AMapUtil.convertToLatLng(mEndPoint)).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.end)));

		} else {

		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {

		if (aMap == null) {
			aMap = mapView.getMap();
		}
		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);

		mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mBusResultList = (ListView) findViewById(R.id.bus_result_list);

		// mRotueTimeDes = (TextView) findViewById(R.id.firstline);
		// mRouteDetailDes = (TextView) findViewById(R.id.secondline);
		UiSettings mUiSettings = aMap.getUiSettings();
		mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

		iv_menu = (ImageView) findViewById(R.id.iv_menu);
		iv_menu.setOnClickListener(this);
		iv_startNavi = (ImageView) findViewById(R.id.iv_startNavi);
		iv_startNavi.setOnClickListener(this);

	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);

	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	public void onBusClick(View view) {
		bus();
	}

	private void bus() {
		// TODO Auto-generated method stub
		searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
		mapView.setVisibility(View.GONE);
		mBusResultLayout.setVisibility(View.VISIBLE);
	}

	public void onDriveClick(View view) {
		drive();
	}

	private void drive() {
		// TODO Auto-generated method stub
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
		mapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}

	public void onWalkClick(View view) {
		walk();
	}

	private void walk() {
		// TODO Auto-generated method stub
		searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
		mapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {

		startProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);

		if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
					mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		stopProgressDialog();
		mBottomLayout.setVisibility(View.GONE);
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mBusRouteResult = result;
					BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(
							mContext, mBusRouteResult);
					mBusResultList.setAdapter(mBusResultListAdapter);
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}
			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
		updateData();
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		stopProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					Log.i("mDriveRouteResult", mDriveRouteResult.toString());
					drivePath = mDriveRouteResult.getPaths().get(0);
					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							this, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos());
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur) + "("
							+ AMapUtil.getFriendlyLength(dis) + ")";
					// mRotueTimeDes.setText(des);
					// mRouteDetailDes.setVisibility(View.VISIBLE);
					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
					// mRouteDetailDes.setText("打车约" + taxiCost + "元");

				} else if (result != null && result.getPaths() == null) {
					Log.e("驾车回调", R.string.no_result + "");
				}

			} else {
				Log.e("驾车回调", R.string.no_result + "");
			}
		} else {
			Log.e("驾车回调", errorCode + "");
		}
		updateData();

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		stopProgressDialog();

		//aMap.clear();// 清理地图上的所有覆盖物

		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mWalkRouteResult = result;

					Log.i("mWalkRouteResult", mWalkRouteResult.toString());
					walkPath = mWalkRouteResult.getPaths().get(0);
					if (LuXianActivity.METHOD == 1) {
						MyWalkRouteOverlay walkRouteOverlay = new MyWalkRouteOverlay(
								this, aMap, walkPath,
								mWalkRouteResult.getStartPos(),
								mWalkRouteResult.getTargetPos());
						walkRouteOverlay.removeFromMap();
						// 节点图标是否可见
						// walkRouteOverlay.setNodeIconVisibility(false);

						walkRouteOverlay.addToMap();
						walkRouteOverlay.zoomToSpan();
					} else {
						WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
								this, aMap, walkPath,
								mWalkRouteResult.getStartPos(),
								mWalkRouteResult.getTargetPos());
						walkRouteOverlay.removeFromMap();
						// 节点图标是否可见
						// walkRouteOverlay.setNodeIconVisibility(false);

						walkRouteOverlay.addToMap();
						walkRouteOverlay.zoomToSpan();
					}

					mBottomLayout.setVisibility(View.VISIBLE);
					// int dis = (int) walkPath.getDistance();
					// int dur = (int) walkPath.getDuration();
					// String des = AMapUtil.getFriendlyTime(dur) + "("
					// + AMapUtil.getFriendlyLength(dis) + ")";
					// mRotueTimeDes.setText(des);
					// mRouteDetailDes.setVisibility(View.GONE);

				} else if (result != null && result.getPaths() == null) {
					Log.e("步行回调", R.string.no_result + "");
				}

			} else {
				Log.e("步行回调", R.string.no_result + "");
			}
		} else {
			Log.e("步行回调", R.string.no_result + "");
		}
		updateData();
	}

	public class MyWalkRouteOverlay extends WalkRouteOverlay {

		public MyWalkRouteOverlay(Context arg0, AMap arg1, WalkPath arg2,
				LatLonPoint arg3, LatLonPoint arg4) {
			super(arg0, arg1, arg2, arg3, arg4);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("static-access")
		@Override
		protected BitmapDescriptor getWalkBitmapDescriptor() {
			// TODO Auto-generated method stub
			BitmapDescriptor reBitmapDescriptor = new BitmapDescriptorFactory()
					.fromResource(R.drawable.bike_blue);

			return reBitmapDescriptor;
		}

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		mapView.onResume();
		run = true;
		initType(LuXianActivity.METHOD);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		run = false;
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

}
