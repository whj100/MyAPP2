package com.wuxing.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wuxing.utils.Constant;
import com.wuxing.utils.HttpUtil;
import com.wuxing.utils.NetUtils;
import com.wuxing.utils.ToastUtil;

public class RegisterActivity extends Activity implements OnClickListener {
	private EditText et_phone, et_pwdone, et_pwdtwo, et_msgyzm;
	private TextView tv_send;
	private TextView tv_register;
	private String phone, pwdone, pwdtwo, msgyzm;
	private RequestQueue mRequestQueue;
	private ImageView iv_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_register);
		MyApplication.getInstance().addActivity(this);
		mRequestQueue = Volley.newRequestQueue(this);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pwdone = (EditText) findViewById(R.id.et_pwdone);
		et_pwdtwo = (EditText) findViewById(R.id.et_pwdtwo);
		et_msgyzm = (EditText) findViewById(R.id.et_msgyzm);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_send.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_send:
			// http://192.168.1.123/ecmobile/?
			// url=/wx_ajax/wx_ajax_register
			// &action=GetVerification
			// &phone=13526886333
			if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
				phone = et_phone.getText().toString().trim();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Map<String, String> map = new HashMap<String, String>();
						map.put("url", "/wx_ajax/wx_ajax_register");
						map.put("action", "GetVerification");
						map.put("phone", phone);
						String result = HttpUtil.urlGet(map);
						Log.e("短信", result);
					}
				}).start();
			} else {
				
			}

			break;
		case R.id.tv_register:
			// http://192.168.1.123/ecmobile/?
			// url=/wx_ajax/wx_ajax_register
			// &action=register
			// &phone=13526886333
			// &verification=123456
			// &password=123456
			phone = et_phone.getText().toString().trim();
			pwdone = et_pwdone.getText().toString().trim();
			pwdtwo = et_pwdtwo.getText().toString().trim();
			msgyzm = et_msgyzm.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				ToastUtil.show(RegisterActivity.this, "手机号不能为空");
				return;
			}
			if (TextUtils.isEmpty(pwdone) || TextUtils.isEmpty(pwdtwo)) {
				ToastUtil.show(RegisterActivity.this, "密码不能为空！");
				return;
			}
			if (!pwdone.equals(pwdtwo)) {
				ToastUtil.show(RegisterActivity.this, "俩次密码不一样！");
				return;
			}
			if (TextUtils.isEmpty(msgyzm)) {
				ToastUtil.show(RegisterActivity.this, "验证码不能为空");
				return;
			}

			
			break;

		default:
			break;
		}
	}

}
