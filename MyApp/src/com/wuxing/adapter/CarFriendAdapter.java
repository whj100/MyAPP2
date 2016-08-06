package com.wuxing.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxing.activity.R;
import com.wuxing.bean.CarFriend;

public class CarFriendAdapter extends BaseAdapter {
	private List<CarFriend> mList;
	private LayoutInflater mInflater;
	private Context context;
	
	public CarFriendAdapter(List<CarFriend> mList, Context context) {
		super();
		this.mList = mList;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (arg1 == null) {
			vh = new ViewHolder();
			arg1 = mInflater.inflate(R.layout.carfriend_item,null);
			vh.iv_touxiang = (ImageView) arg1.findViewById(R.id.iv_touxiang);
			vh.tv_uid = (TextView) arg1.findViewById(R.id.tv_nicheng);
			arg1.setTag(vh);
		}else {
			vh = (ViewHolder) arg1.getTag();
		}
		CarFriend cf = mList.get(arg0);
		
		vh.iv_touxiang.setImageResource(Integer.parseInt(cf.getImgId()));
		vh.tv_uid.setText(cf.getId());
		
		return arg1;
	}
	class ViewHolder{
		ImageView iv_touxiang;
		TextView tv_uid;
	}

}
