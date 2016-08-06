package com.wuxing.adapter;

import java.util.List;

import com.amap.api.maps.model.Text;
import com.wuxing.activity.R;
import com.wuxing.bean.Group;
import com.wuxing.bean.GroupBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Group> mList;
	
	public GroupAdapter(Context context, List<Group> mList) {
		super();
		this.context = context;
		this.mList = mList;
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
			arg1 = mInflater.inflate(R.layout.group_item, null);
			vh.iv_headimg = (ImageView) arg1.findViewById(R.id.iv_headimg);
			vh.tv_groupName = (TextView) arg1.findViewById(R.id.tv_groupname);
			arg1.setTag(vh);
		}else {
			vh = (ViewHolder) arg1.getTag();
		}
		Group group = mList.get(arg0);
		vh.iv_headimg.setImageResource(R.drawable.ic_launcher);
		vh.tv_groupName.setText(group.getGroupName());
		return arg1;
	}
	class ViewHolder{
		ImageView iv_headimg;
		TextView tv_groupName;
	}

}
