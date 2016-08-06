package com.wuxing.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.wuxing.adapter.CarFriendAdapter;
import com.wuxing.adapter.GroupAdapter;
import com.wuxing.adapter.HistoryAdapter;
import com.wuxing.bean.CarFriend;
import com.wuxing.bean.Group;
import com.wuxing.bean.GroupBean;
import com.wuxing.bean.GroupBean.Data;
import com.wuxing.bean.HistoryBean;
import com.wuxing.map.route.RouteActivity;
import com.wuxing.service.MyService;
import com.wuxing.sortlist.SortListViewMainActivity;
import com.wuxing.utils.Constant;
import com.wuxing.utils.DbUtil;
import com.wuxing.utils.HttpUtil;
import com.wuxing.utils.MyCamera;
import com.wuxing.utils.NetUtils;
import com.wuxing.utils.ToastUtil;
import com.wuxing.villoy.VolleyInterface;
import com.wuxing.villoy.VolleyRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("NewApi")
public class LuXianActivity extends BaseActivity implements OnClickListener,
		OnGeocodeSearchListener {
	ImageView iv_drive, iv_bike, iv_walk, iv_creategroup, iv_navi, camera,
			iv_switch;
	private TextView tv_cancle, tv_search_start, tv_search_end, tv_start,
			tv_end;
	private String start = "", end = "", startJingWei = "", endJingWei = "",
			strType = "";
	public static int METHOD = 2;// 规划那种路线默认是车行 骑行1 步行3
	public int position = 0;
	private ListView lv_history, lv_groupList;
	private HistoryAdapter adapter;
	private GroupAdapter groupAdapter;
	private List<Group> groups;
	private List<HistoryBean> mList;
	private GeocodeSearch geocoderSearch;
	MyReceiver receiver;
	boolean isSwitched = true;
	public static int FLAG = 1;
	DbUtil dbutil;
	LinearLayout ll_person, ll_cargroup, ll_zudui, ll_bike, ll_drive, ll_walk,
			ll_creategroup;
	// 以下是为了记录谁打开的StartEndActivity
	PopupWindow pw;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				adapter.notifyDataSetChanged();
				break;
			case 2:
				stopProgressDialog();
				groupAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_lu_xian);
		MyApplication.getInstance().addActivity(this);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		initView();
		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter("com.action.luxian");
		intentFilter.addAction("com.service.close");
		registerReceiver(receiver, intentFilter);
		dbutil = new DbUtil(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				List<HistoryBean> lists = dbutil.selectAllHistory();
				if (lists.size() > 0)
					mList.clear();
				{
					mList.addAll(lists);
					mHandler.sendEmptyMessage(1);
				}

			}
		}).start();
		
		
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "/wx_ajax_c/get_GroupsForUser");
		map.put("username", sp.getString("hxusername", ""));
		
		String newUrl = Constant.SERVER_ADDRESS
				+ NetUtils.getUrlParamsByMap(map);
		VolleyRequest.requestGet(LuXianActivity.this, newUrl,
				"getgroupList", new VolleyInterface(LuXianActivity.this,
						VolleyInterface.mListener,
						VolleyInterface.mErrorListener) {

					@Override
					public void onMySuccess(String result) {
						// TODO Auto-generated method stub
						stopProgressDialog();
						Log.e("获取gorupList", result);
						Gson gson = new Gson();
						GroupBean groupBean = gson.fromJson(result,
								GroupBean.class);
						List<Data> datas = groupBean.getData();
						groups.clear();
						List<Group> lists = new ArrayList<Group>();
						for (int i = 0; i < datas.size(); i++) {
							Group group = new Group();
							group.setGroupName(datas.get(i).getGroupname());
							group.setGroupId(datas.get(i).getGroupid());
							lists.add(group);
						}
						groups.addAll(lists);
						mHandler.sendEmptyMessage(2);
					}

					@Override
					public void onMyError(VolleyError error) {
						// TODO Auto-generated method stub
						stopProgressDialog();
						ToastUtil.show(LuXianActivity.this,"获取组列表失败");
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopProgressDialog();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constant.START:
				tv_search_start.setText("我的位置");
				startJingWei = Constant.LA + "," + Constant.LO;
				start = "我的位置";
				break;
			case Constant.END:
				tv_search_end.setText("我的位置");
				endJingWei = Constant.LA + "," + Constant.LO;
				end = "我的位置";
				break;
			case Constant.ADD_FRIEND:

				break;
			default:
				break;
			}
		} else if (resultCode == Constant.POIRESULT) {

			if (Constant.FLAG.equals("start")) {
				start = data.getStringExtra("poiAddress");
				tv_search_start.setText(start);
				String wei = data.getStringExtra("la");
				String jing = data.getStringExtra("lo");
				startJingWei = wei + "," + jing;
			} else {
				end = data.getStringExtra("poiAddress");
				tv_search_end.setText(end);
				endJingWei = data.getStringExtra("la") + ","
						+ data.getStringExtra("lo");
			}

		}
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals("com.action.luxian")) {
				if (Constant.FLAG.equals("start")) {
					start = arg1.getStringExtra("address");
					tv_search_start.setText(start);
					startJingWei = arg1.getStringExtra("jingwei");
					Log.e("s起点", startJingWei);
				} else if (Constant.FLAG.equals("end")) {
					end = arg1.getStringExtra("address");
					tv_search_end.setText(end);
					endJingWei = arg1.getStringExtra("jingwei");
					Log.e("s终点", endJingWei);
				} else if ("com.service.close".equals(action)) {
					LuXianActivity.this.startActivity(new Intent(
							LuXianActivity.this, HomeActivity.class));
					finish();

					stopProgressDialog();
				}
			}
		}

	}

	private void initView() {
		// TODO Auto-generated method stub

		iv_drive = (ImageView) findViewById(R.id.iv_drive);
		iv_bike = (ImageView) findViewById(R.id.iv_bike);
		iv_walk = (ImageView) findViewById(R.id.iv_walk);
		iv_creategroup = (ImageView) findViewById(R.id.iv_creategroup);
		ll_creategroup = (LinearLayout) findViewById(R.id.ll_creategroup);
		iv_navi = (ImageView) findViewById(R.id.iv_navi);
		camera = (ImageView) findViewById(R.id.camera);

		ll_bike = (LinearLayout) findViewById(R.id.ll_bike);
		ll_drive = (LinearLayout) findViewById(R.id.ll_drive);
		ll_walk = (LinearLayout) findViewById(R.id.ll_walk);
		ll_person = (LinearLayout) findViewById(R.id.ll_person);
		ll_cargroup = (LinearLayout) findViewById(R.id.ll_cargroup);
		ll_zudui = (LinearLayout) findViewById(R.id.ll_zudui);

		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);

		tv_search_start = (TextView) findViewById(R.id.tv_search_start);
		tv_search_end = (TextView) findViewById(R.id.tv_search_end);
		iv_switch = (ImageView) findViewById(R.id.iv_seitch);

		tv_search_end.setOnClickListener(this);
		tv_search_start.setOnClickListener(this);
		camera.setOnClickListener(this);
		iv_switch.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);
		ll_drive.setOnClickListener(this);
		ll_walk.setOnClickListener(this);
		ll_bike.setOnClickListener(this);
		iv_navi.setOnClickListener(this);
		ll_person.setOnClickListener(this);
		ll_cargroup.setOnClickListener(this);
		iv_creategroup.setOnClickListener(this);

		lv_history = (ListView) findViewById(R.id.lv_history);
		mList = new ArrayList<HistoryBean>();
		adapter = new HistoryAdapter(mList, this);
		lv_history.setAdapter(adapter);
		lv_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HistoryBean his = mList.get(arg2);
				Intent intentNavi = new Intent(LuXianActivity.this,
						RouteActivity.class);
				intentNavi.putExtra("startJingWei", his.getStartJingWei());
				intentNavi.putExtra("endJingWei", his.getEndJingWei());
				intentNavi.putExtra("strType", "person");
				startActivity(intentNavi);
			}
		});
		lv_groupList = (ListView) findViewById(R.id.lv_groupList);
		groups = new ArrayList<Group>();
		groupAdapter = new GroupAdapter(LuXianActivity.this, groups);
		lv_groupList.setAdapter(groupAdapter);
		lv_groupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Group group = (Group) groupAdapter.getItem(arg2);
				Intent intent = new Intent(LuXianActivity.this,
						GroupActivity.class);
				intent.putExtra("groupId", group.getGroupId());
				startActivity(intent);
			}
		});

		select(2);
		ll_person.setBackground(getResources().getDrawable(
				R.drawable.person_group_left_bg));
		ll_cargroup.setBackgroundResource(R.color.transparent);
		lv_history.setVisibility(0);
		ll_zudui.setVisibility(8);
	}

	public void select(int i) {
		// TODO Auto-generated method stub
		METHOD = i;
		iv_bike.setImageResource(R.drawable.bike);
		iv_drive.setImageResource(R.drawable.group_normal);
		iv_walk.setImageResource(R.drawable.walk_normal);

		switch (i) {
		case 1:
			iv_bike.setImageResource(R.drawable.bike_check);
			break;
		case 2:
			iv_drive.setImageResource(R.drawable.group_check);
			break;
		case 3:
			iv_walk.setImageResource(R.drawable.walk_check);
			break;

		default:
			break;
		}
	}

	@SuppressLint({ "ResourceAsColor", "NewApi" })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_search_end:
			Constant.FLAG = "end";
			Intent endIntent = new Intent(LuXianActivity.this,
					StartEndActivity.class);
			endIntent.putExtra("action", "com.action.luxian");
			startActivityForResult(endIntent, Constant.END);
			break;
		case R.id.tv_search_start:
			Constant.FLAG = "start";
			Intent startIntent = new Intent(LuXianActivity.this,
					StartEndActivity.class);
			startIntent.putExtra("action", "com.action.luxian");
			startActivityForResult(startIntent, Constant.START);
			break;
		case R.id.camera:
			startProgressDialog();

			if (Constant.isRecorder) {

				Intent intent = new Intent();
				intent.setAction(Constant.STOP_CAMERA);
				sendBroadcast(intent);
				Intent intentService = new Intent(LuXianActivity.this,
						MyService.class);
				stopService(intentService);

			} else {
				LuXianActivity.this.startActivity(new Intent(
						LuXianActivity.this, HomeActivity.class));
				finish();

			}
			break;
		case R.id.iv_seitch:
			if (isSwitched) {
				String stemp = tv_search_start.getText().toString();
				String etemp = tv_search_end.getText().toString();
				tv_search_end.setText(stemp);
				tv_search_start.setText(etemp);
				String temp1 = startJingWei;
				String temp2 = endJingWei;
				startJingWei = temp2;
				endJingWei = temp1;
				isSwitched = false;
			} else {

				String stemp = tv_search_start.getText().toString();
				String etemp = tv_search_end.getText().toString();
				tv_search_end.setText(stemp);
				tv_search_start.setText(etemp);
				String temp1 = startJingWei;
				String temp2 = endJingWei;
				startJingWei = temp2;
				endJingWei = temp1;
				isSwitched = true;
			}
			break;
		case R.id.tv_cancle:
			finish();
			break;
		case R.id.iv_creategroup:
			createPop();
			break;
		case R.id.ll_cargroup:
			
			ll_cargroup.setBackground(getResources().getDrawable(
					R.drawable.person_group_right_bg));
			ll_person.setBackgroundResource(R.color.transparent);
			lv_history.setVisibility(8);
			ll_zudui.setVisibility(0);
			break;
		case R.id.ll_person:
			ll_person.setBackground(getResources().getDrawable(
					R.drawable.person_group_left_bg));
			ll_cargroup.setBackgroundResource(R.color.transparent);
			lv_history.setVisibility(0);
			ll_zudui.setVisibility(8);
			break;
		case R.id.iv_cuo_end:
			tv_search_end.setText("");
			break;
		case R.id.iv_cuo_start:
			tv_search_start.setText("");
			break;
		case R.id.ll_drive:
			select(2);
			break;
		case R.id.ll_bike:
			select(1);
			break;
		case R.id.ll_walk:
			select(3);
			break;
		case R.id.iv_navi:
			if (lv_history.getVisibility() == 0) {// 个人
				strType = "person";
				if (startJingWei.equals("") || endJingWei.equals("")) {
					ToastUtil.show(LuXianActivity.this, "起始点不能为空");
					return;
				}

				Intent intentNavi = new Intent(LuXianActivity.this,
						RouteActivity.class);
				intentNavi.putExtra("strType", strType);
				intentNavi.putExtra("startJingWei", startJingWei);
				intentNavi.putExtra("endJingWei", endJingWei);

				startActivity(intentNavi);

				if (dbutil.isExist(start, end)) {

				} else {
					HistoryBean hb = new HistoryBean();
					hb.setEnd(end);
					hb.setEndJingWei(endJingWei);
					hb.setStart(start);
					hb.setStartJingWei(startJingWei);
					dbutil.insertHistory(hb);
				}
			} else if (ll_zudui.getVisibility() == 0) {// 组队
				strType = "group";
				// 判断是否是群主点击（群主点击要有起始点并上传路径）
				// http://www.5xclw.cn/ecmobile/?
				// url=/wx_ajax_c/is_owner
				// &user_name=u132GPGT9928
				// &group_id=204263785517547952
				createPopList();
			}
			break;
		default:
			break;
		}
	}

	protected void updateRoute() {
		// TODO Auto-generated method stub
//		http://www.5xclw.cn/ecmobile/
//			?url=
//			&group_id=204263785517547952
//			&road_map=111%E9%83%91%E5%B7%9E
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "/wx_ajax_c/update_roadmap");
		map.put("group_id", ((Group) groupAdapter.getItem(position)).getGroupId());
		map.put("road_map", startJingWei+"@"+endJingWei);
		String updateRouteUrl = Constant.SERVER_ADDRESS+NetUtils.getUrlParamsByMap(map);
		VolleyRequest.requestGet(this, updateRouteUrl, "updateRoute", new VolleyInterface(this,VolleyInterface.mListener,VolleyInterface.mErrorListener) {
			
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
			//	{"success":"True","message":"\u8def\u7ebf\u4e0a\u4f20\u6210\u529f"}
				stopProgressDialog();
				try {
					JSONObject job = new JSONObject(result);
					boolean success = job.getBoolean("success");
					if (success) {
						//路径上传成功
						Intent intentNavi = new Intent(
								LuXianActivity.this,
								RouteActivity.class);
						intentNavi.putExtra("strType", strType);
						intentNavi.putExtra("group_id", ((Group) groupAdapter.getItem(position)).getGroupId());
						startActivity(intentNavi);
					}else {
						ToastUtil.show(LuXianActivity.this, "提交路线失败！");
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
	public void createPopList(){
		WindowManager wm = (WindowManager) LuXianActivity.this
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int mScreenWidth = outMetrics.widthPixels;
		int mScreenHeight = outMetrics.heightPixels;
		View v = LayoutInflater.from(LuXianActivity.this).inflate(
				R.layout.pop_list, null);
		pw = new PopupWindow(v, mScreenWidth / 2, mScreenHeight / 3);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		pw.showAtLocation(
				LayoutInflater.from(LuXianActivity.this).inflate(
						R.layout.activity_group, null), Gravity.CENTER, 0, 0);
		final TextView tv_pwsure = (TextView) v.findViewById(R.id.tv_pwsure);
		final TextView tv_pwcancle = (TextView) v
				.findViewById(R.id.tv_pwcancle);
		final EditText et_content = (EditText) v.findViewById(R.id.et_content);
		final ListView lv_pop_list = (ListView) v.findViewById(R.id.lv_pop_list);
		lv_pop_list.setAdapter(groupAdapter);
		lv_pop_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				pw.dismiss();
				position = arg2;
				Map<String, String> mapowner = new HashMap<String, String>();
				mapowner.put("url", "/wx_ajax_c/is_owner");
				mapowner.put("user_name", sp.getString("hxusername", ""));
				mapowner.put("group_id",
						((Group) groupAdapter.getItem(position)).getGroupId());
				
				String ownerUrl = Constant.SERVER_ADDRESS
						+ NetUtils.getUrlParamsByMap(mapowner);
				VolleyRequest.requestGet(LuXianActivity.this, ownerUrl,
						"is_owner", new VolleyInterface(LuXianActivity.this,
								VolleyInterface.mListener,
								VolleyInterface.mErrorListener) {
							@Override
							public void onMySuccess(String result) {
								// TODO Auto-generated method stub
								// {"success":"True","message":""}
								try {
									JSONObject job = new JSONObject(result);
									boolean success = job.getBoolean("success");
									if (success) {
										if (success) {
											if (!startJingWei.equals("")&&!endJingWei.equals("")) {
												//是群主上传路径
												updateRoute();
											}else  {
												ToastUtil.show(LuXianActivity.this, "请输入起始点");
											}
										}
						
									}else {
										//不是群主进入
										Intent intentNavi = new Intent(
												LuXianActivity.this,
												RouteActivity.class);
										intentNavi.putExtra("strType", strType);
										intentNavi.putExtra("group_id", ((Group) groupAdapter.getItem(position)).getGroupId());
										startActivity(intentNavi);
										
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
			
		});
		OnClickListener listclick = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.tv_pwsure:
//					if (TextUtils.isEmpty(et_content.getText().toString())) {
//						return;
//					}
//					ToastUtil.show(LuXianActivity.this, "wo ");
//					pw.dismiss();

					break;
				case R.id.tv_pwcancle:
					if (pw != null && pw.isShowing()) {
						pw.dismiss();
					}

					break;

				default:
					break;
				}
			}
		};
		tv_pwsure.setOnClickListener(listclick);
		tv_pwcancle.setOnClickListener(listclick);
		
	}
	public void createPop() {
		WindowManager wm = (WindowManager) LuXianActivity.this
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int mScreenWidth = outMetrics.widthPixels;
		int mScreenHeight = outMetrics.heightPixels;
		View v = LayoutInflater.from(LuXianActivity.this).inflate(
				R.layout.popubwindow, null);
		pw = new PopupWindow(v, mScreenWidth / 2, mScreenHeight / 3);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		pw.showAtLocation(
				LayoutInflater.from(LuXianActivity.this).inflate(
						R.layout.activity_group, null), Gravity.CENTER, 0, 0);
		final TextView tv_pwsure = (TextView) v.findViewById(R.id.tv_pwsure);
		final TextView tv_pwcancle = (TextView) v.findViewById(R.id.tv_pwcancle);
		final EditText et_content = (EditText) v.findViewById(R.id.et_content);

		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.tv_pwsure:
					if (TextUtils.isEmpty(et_content.getText().toString())) {
						return;
					}
					String groupName = et_content.getText().toString();
					Intent intent = new Intent(LuXianActivity.this,
							SortListViewMainActivity.class);
					try {
						intent.putExtra("groupName", URLEncoder.encode(groupName, "utf-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivityForResult(intent, Constant.ADD_FRIEND);
					pw.dismiss();

					break;
				case R.id.tv_pwcancle:
					if (pw != null && pw.isShowing()) {
						pw.dismiss();
					}

					break;

				default:
					break;
				}
			}
		};
		tv_pwsure.setOnClickListener(click);
		tv_pwcancle.setOnClickListener(click);
	}

	/**
	 * 响应地理编码
	 * 
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

	// 根据地址搜经纬
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				// aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				// AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
				// geoMarker.setPosition(AMapUtil.convertToLatLng(address
				// .getLatLonPoint()));
				LatLonPoint latLonPoint = address.getLatLonPoint();

				String addressName = "经纬度值:" + address.getLatLonPoint()
						+ "\n位置描述:" + address.getFormatAddress();
				// Log.e("搜索的位置定位jingwei", addressName);
				// ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				// /ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else {
			// ToastUtil.showerror(this, rCode);
		}
	}

	// 根据经纬度搜索
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress();
				// Log.e("当前位置", addressName);

			} else {

			}
		} else {

		}
	}

}
