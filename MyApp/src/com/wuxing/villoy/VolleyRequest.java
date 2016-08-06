package com.wuxing.villoy;

import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.wuxing.activity.MyApplication;

public class VolleyRequest {
	// 字符串请求
	public static StringRequest stringRequest;
	public static Context mContext;

	/**
	 * get请求
	 */
	public static void requestGet(Context mContext, String url, String tag,
			VolleyInterface vif) {
		MyApplication.getHttpQueue().cancelAll(tag);
		stringRequest = new StringRequest(Method.GET, url, vif.loadListener(),
				vif.errorListener());
		stringRequest.setTag(tag);
		MyApplication.getHttpQueue().add(stringRequest);
		MyApplication.getHttpQueue().start();
	}

	/**
	 * post请求
	 */
	public static void requestPost(Context mContext, String url, String tag,
		final Map<String, String> params, VolleyInterface vif) {
		MyApplication.getHttpQueue().cancelAll(tag);
		stringRequest = new StringRequest(url, vif.loadListener(),
				vif.errorListener()){
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				// TODO Auto-generated method stub
				return params;
			}
		};

		stringRequest.setTag(tag);
		MyApplication.getHttpQueue().add(stringRequest);
		MyApplication.getHttpQueue().start();
		
	}
}
