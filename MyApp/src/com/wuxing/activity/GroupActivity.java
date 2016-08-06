package com.wuxing.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.wuxing.adapter.MemberAdapter;
import com.wuxing.bean.Member;
import com.wuxing.bean.MemberBean;
import com.wuxing.sortlist.SortListViewMainActivity;
import com.wuxing.utils.Constant;
import com.wuxing.utils.NetUtils;
import com.wuxing.villoy.VolleyInterface;
import com.wuxing.villoy.VolleyRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GroupActivity extends Activity implements OnClickListener {
	String groupId;
	GridView gv_carfriendlist;
	MemberAdapter memberAdapter;
	List<Member> mList;
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				memberAdapter.notifyDataSetChanged();
				break;
			case 2:

				break;
			case 3:

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
		setContentView(R.layout.activity_group);
		MyApplication.getInstance().addActivity(this);
		groupId = getIntent().getStringExtra("groupId");
		initView();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		// http://www.5xclw.cn/ecmobile/?
		// url=/wx_ajax_c/get_groupusers
		// &groupid=204221173083406764
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "/wx_ajax_c/get_groupusers");
		map.put("groupid", groupId);
		String newUrl = Constant.SERVER_ADDRESS
				+ NetUtils.getUrlParamsByMap(map);
		VolleyRequest.requestGet(this, newUrl, "getMember",
				new VolleyInterface(this, VolleyInterface.mListener,
						VolleyInterface.mErrorListener) {

					@Override
					public void onMySuccess(String result) {
						// TODO Auto-generated method stub
						Log.e("getMember", result);
						Gson gson = new Gson();
						MemberBean memberBean = gson.fromJson(result,
								MemberBean.class);
						if (memberBean.getStatus().getSucceed() == 1) {
							List<com.wuxing.bean.MemberBean.Data.Member> members = memberBean.getData().getMember();
							mList.clear();
							for (int i = 0; i < members.size(); i++) {
								Member member = new Member();
								member.setUser_name(members.get(i).getUser_name());
								member.setHeadimg(members.get(i).getHeadimg());
								member.setAlias(members.get(i).getAlias());
								mList.add(member);
							}
							Member member1 = new Member();
							member1.setUser_name("");
							member1.setHeadimg(R.drawable.jiapeople+"");
							member1.setAlias("");
							mList.add(member1);
							Member member2 = new Member();
							member2.setUser_name("");
							member2.setHeadimg(R.drawable.jianpeople+"");
							member2.setAlias("");
							mList.add(member2);
							mHandler.sendEmptyMessage(1);
						}
					}

					@Override
					public void onMyError(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	public void initView() {
		// TODO Auto-generated method stub
		gv_carfriendlist = (GridView) findViewById(R.id.gv_carfriendlist);
		mList = new ArrayList<Member>();
		memberAdapter = new MemberAdapter(this, mList);
		gv_carfriendlist.setAdapter(memberAdapter);
		gv_carfriendlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if (arg2 == mList.size()-1 ) {
					
					//删除 传本组的成员列表过去
					mList.remove(mList.size()-1);
					mList.remove(mList.size()-1);
					Intent intent = new Intent(GroupActivity.this,SortListViewMainActivity.class);
					intent.putExtra("flag", "del");
					intent.putExtra("groupid", groupId);
					intent.putExtra("mList", (Serializable)mList);
					startActivity(intent);
					finish();
				}else if (arg2 == mList.size()-2) {
					//添加
					mList.remove(mList.size()-1);
					mList.remove(mList.size()-1);
					Intent intent = new Intent(GroupActivity.this,SortListViewMainActivity.class);
					intent.putExtra("mList", (Serializable)mList);
					intent.putExtra("groupid", groupId);
					intent.putExtra("flag", "add");
					startActivity(intent);
					finish();
				}else {
					
				}
			}
		});
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
