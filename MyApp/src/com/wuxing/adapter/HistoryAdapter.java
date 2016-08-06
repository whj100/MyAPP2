package com.wuxing.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.wuxing.activity.R;
import com.wuxing.bean.HistoryBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	private List<HistoryBean> mList;
	private Context context;
	private LayoutInflater mInflater;
	
	public void setmList(List<HistoryBean> mList) {
		this.mList = mList;
	}

	public HistoryAdapter(List<HistoryBean> mList, Context context) {
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
			vh =  new ViewHolder();
			arg1 = mInflater.inflate(R.layout.history_item, null);
			vh.tv_start  = (TextView) arg1.findViewById(R.id.tv_start);
			vh.tv_end  = (TextView) arg1.findViewById(R.id.tv_end);
			arg1.setTag(vh);
		}else {
			vh = (ViewHolder) arg1.getTag();
		}
		HistoryBean hb = mList.get(arg0);
		vh.tv_start.setText(hb.getStart());
		vh.tv_end.setText(hb.getEnd());
		return arg1;
	}
	class ViewHolder{
		TextView tv_start,tv_end;
	}

}
