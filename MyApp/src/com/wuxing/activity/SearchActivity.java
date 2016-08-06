package com.wuxing.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.wuxing.adapter.PoiListAdapter;
import com.wuxing.utils.Constant;
import com.wuxing.utils.ToastUtil;

public class SearchActivity extends Activity implements OnClickListener, OnPoiSearchListener {
	ImageView iv_back;
	EditText et_search;
	TextView tv_search;
	ListView lv_searchResult;
	PoiListAdapter adapter;
	List<PoiItem> poiItems;
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private PoiResult poiResult; // poi返回的结果
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_search);
		MyApplication.getInstance().addActivity(this);
		poiItems = new ArrayList<PoiItem>();
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		iv_back = (ImageView) findViewById(R.id.iv_back);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_search = (TextView) findViewById(R.id.tv_search);
		iv_back.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		lv_searchResult = (ListView) findViewById(R.id.lv_searchResult);
		lv_searchResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("La", poiItems.get(arg2).getLatLonPoint().getLatitude());
				intent.putExtra("Lo", poiItems.get(arg2).getLatLonPoint().getLongitude());
				setResult(11, intent);
				finish();
			}
		});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_back:
			finish();	
			break;
		case R.id.tv_search:
			String etStr = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(etStr)) {
				ToastUtil.show(SearchActivity.this, "请输入搜索内容");
				return;
			}
			doSearchQuery(etStr);
			break;
		default:
			break;
		}
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
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		//dissmissProgressDialog();// 隐藏对话框
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
						adapter = new PoiListAdapter(SearchActivity.this, poiItems);
						lv_searchResult.setAdapter(adapter);
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
