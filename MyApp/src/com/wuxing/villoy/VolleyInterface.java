package com.wuxing.villoy;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import android.content.Context;

public abstract class VolleyInterface {
	public Context context;
	public static Listener<String> mListener;
	public static ErrorListener mErrorListener;
	
	public VolleyInterface(Context context, Listener<String> listener,
			ErrorListener errorListener) {
		this.context = context;
		this.mListener = listener;
		this.mErrorListener = errorListener;
		
	}
	public abstract void onMySuccess(String result);
	public abstract void onMyError(VolleyError error);
	public Listener<String> loadListener(){
		mListener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				onMySuccess(response);
			}
		};
		
		return mListener;
		
	}
	
	public ErrorListener errorListener(){
		mErrorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				onMyError(error);
			}
		};
		return mErrorListener;
		
	}

}
