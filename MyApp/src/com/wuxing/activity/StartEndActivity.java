package com.wuxing.activity;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.wuxing.adapter.PoiListAdapter;
import com.wuxing.utils.Constant;
import com.wuxing.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StartEndActivity extends BaseActivity implements OnClickListener, OnPoiSearchListener {
	ImageView iv_back;
	EditText et_search;
	TextView tv_search, tv_myLocation, tv_mapSelectLocation;
	ListView lv_historyposition;
	String address;
	String action = "";
	//搜索
	List<PoiItem> poiItems =  new ArrayList<PoiItem>();
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private PoiResult poiResult; // poi返回的结果
	//private ProgressDialog progDialog = null;// 搜索时进度条
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ToastUtil.show(StartEndActivity.this, "请输入更详细的地址");
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_start_end);
		MyApplication.getInstance().addActivity(this);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		action = getIntent().getStringExtra("action");
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_search = (TextView) findViewById(R.id.tv_search);
		tv_myLocation = (TextView) findViewById(R.id.tv_myLocation);
		tv_mapSelectLocation = (TextView) findViewById(R.id.tv_mapSelectLocation);
		tv_myLocation.setOnClickListener(this);
		tv_mapSelectLocation.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		
		lv_historyposition = (ListView) findViewById(R.id.lv_historyposition);
	
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length()>0) {
					tv_search.setText("确定");
				}else {
					tv_search.setText("搜索");
				}
			}
		});
	}
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(String keyWord) {
		//showProgressDialog();// 显示进度框
		//currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", Constant.cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(0);// 设置查第一页
	
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_search:
			String etStr = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(etStr)) {
				ToastUtil.show(StartEndActivity.this, "请输入搜索内容");
				return;
			}
			startProgressDialog();
			doSearchQuery(etStr);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_myLocation:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.tv_mapSelectLocation:
			Intent intent = new Intent(StartEndActivity.this,DiTuXuanWeiActivity.class);
			if (!action.equals("")) {
				intent.putExtra("action", action);
			}
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					
				
					if (poiItems != null && poiItems.size() > 0) {
						//aMap.clear();// 清理之前的图标
						//PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						//poiOverlay.removeFromMap();
						//poiOverlay.addToMap();
						//poiOverlay.zoomToSpan();
						stopProgressDialog();
						Intent intentse = new Intent();
						intentse.putExtra("poiAddress", poiItems.get(0).getTitle());
						intentse.putExtra("la", poiItems.get(0).getLatLonPoint().getLatitude()+"");
						intentse.putExtra("lo", poiItems.get(0).getLatLonPoint().getLongitude()+"");
						setResult(Constant.POIRESULT, intentse);
						finish();

						
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						//showSuggestCity(suggestionCities);
						Log.e("搜素相近结果", "");
					} else {
						//ToastUtil.show(PoiKeywordSearchActivity.this,
							//	R.string.no_result);
						Log.e("搜素无结果", "");
					}
				}
			} else {
				//ToastUtil.show(PoiKeywordSearchActivity.this,
					//	R.string.no_result);
			}
		} else {
			
			//ToastUtil.showerror(this, rCode);
		}
	}

}
