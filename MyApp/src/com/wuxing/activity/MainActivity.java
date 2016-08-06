package com.wuxing.activity;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wuxing.bean.LoginBean;
import com.wuxing.utils.Constant;
import com.wuxing.utils.NetUtils;
import com.wuxing.utils.ToastUtil;
import com.wuxing.villoy.VolleyInterface;
import com.wuxing.villoy.VolleyRequest;

public class MainActivity extends BaseActivity implements OnClickListener {
	private TextView tv_register, tv_forgetPwd;
	private EditText et_userName, et_userpwd;
	private Button btn_login;
	private String userName, passWord;
	private static final int REGISTER = 1;
	private RequestQueue mRequestQueue;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ToastUtil.show(MainActivity.this, "登录失败");
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
		setContentView(R.layout.activity_main);
		mRequestQueue = Volley.newRequestQueue(this);
		MyApplication.getInstance().addActivity(this);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);

		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setOnClickListener(this);
		tv_forgetPwd = (TextView) findViewById(R.id.tv_forgetPwd);
		tv_forgetPwd.setOnClickListener(this);

		et_userName = (EditText) findViewById(R.id.et_username);
		et_userpwd = (EditText) findViewById(R.id.et_userpwd);

		et_userName.setText("13276909928");
		et_userpwd.setText("123456");

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_register:
			startActivityForResult(new Intent(MainActivity.this,
					RegisterActivity.class), REGISTER);
			break;
		case R.id.tv_forgetPwd:

			break;

		case R.id.btn_login:
			startProgressDialog();
			userName = et_userName.getText().toString();
			passWord = et_userpwd.getText().toString();
			if (TextUtils.isEmpty(userName)) {
				ToastUtil.show(MainActivity.this, "用户名不能为空");
				return;
			}
			if (TextUtils.isEmpty(passWord)) {
				ToastUtil.show(MainActivity.this, "密码不能为空");
				return;
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("url", "/wx_ajax_c/login/");
			map.put("username", userName);
			map.put("password", passWord);

			// ?url=/wx_ajax_c/login/&username=13676986382&password=12345678"
			String newUrl = Constant.SERVER_ADDRESS
					+ NetUtils.getUrlParamsByMap(map);
			Log.e("", newUrl);
			VolleyRequest.requestGet(MainActivity.this, newUrl, "login",
					new VolleyInterface(MainActivity.this,
							VolleyInterface.mListener,
							VolleyInterface.mErrorListener) {

						@Override
						public void onMySuccess(String result) {
							// TODO Auto-generated method stub
							Log.e("登录返回", result);
							stopProgressDialog();

							// {
							// "data":
							// {"session":
							// {"user_id":"666",
							// "hxusername":"13676986382",
							// "hxpassword":"550e1bafe077ff0b0b67f4e32f29d751"}
							// },
							// "status":{"succeed":1}
							// }

							Gson gson = new Gson();
							LoginBean loginBean = gson.fromJson(result,
									LoginBean.class);
							Log.e("sdfd", loginBean.getData().getSession()
									.getHxpassword());
							if (loginBean.getStatus().getSucceed() == 1) {
								sp.edit()
										.putString(
												"user_id",
												loginBean.getData().getSession()
														.getUser_id())
										.putString(
												"hxusername",
												loginBean.getData().getSession()
														.getHxusername())
										.putString(
												"hxpassword",
												loginBean.getData().getSession()
														.getHxpassword())
										.commit();

								//ToastUtil.show(MainActivity.this, result);

								Intent intent = new Intent(MainActivity.this,
										HomeActivity.class);
								startActivity(intent);
								finish();
							} else {
								ToastUtil.show(MainActivity.this, "succeed:"+loginBean.getStatus().getSucceed()+"");
							}
						}

						@Override
						public void onMyError(VolleyError error) {
							// TODO Auto-generated method stub
							stopProgressDialog();
							ToastUtil.show(MainActivity.this,
									"登录失败" + error.getMessage());
						}
					});
			;

		default:
			break;
		}
	}

}
