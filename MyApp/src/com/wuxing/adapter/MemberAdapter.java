package com.wuxing.adapter;

import java.util.List;

import com.amap.api.maps.model.Text;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuxing.activity.R;
import com.wuxing.bean.Group;
import com.wuxing.bean.GroupBean;
import com.wuxing.bean.Member;
import com.wuxing.utils.Constant;
import com.wuxing.views.CircleImageView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Member> mList;
	
	public MemberAdapter(Context context, List<Member> mList) {
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
			arg1 = mInflater.inflate(R.layout.member_item, null);
			vh.iv_headimg = (ImageView) arg1.findViewById(R.id.iv_headimg);
			vh.tv_name = (TextView) arg1.findViewById(R.id.tv_name);
			arg1.setTag(vh);
		}else {
			vh = (ViewHolder) arg1.getTag();
		}
		Member member = mList.get(arg0);
		if (arg0 == (mList.size()-1)) {
			vh.iv_headimg.setImageResource(R.drawable.jianpeople);
			vh.tv_name.setText("");
		}else if (arg0 == mList.size()-2) {
			vh.iv_headimg.setImageResource(R.drawable.jiapeople);
			vh.tv_name.setText("");
		}else {
			
			try {
				ImageLoader.getInstance().displayImage(Constant.SERVER_IMG+member.getHeadimg().replace("\\", "/"), vh.iv_headimg);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("图片路径", e.getMessage());
			}
			vh.tv_name.setText(member.getUser_name());
		}
		return arg1;
	}
	class ViewHolder{
		ImageView iv_headimg;
		TextView tv_name;
	}

}
