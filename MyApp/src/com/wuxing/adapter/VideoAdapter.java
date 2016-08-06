package com.wuxing.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuxing.activity.R;
import com.wuxing.bean.Video;
import com.wuxing.imagecache.MyVideoThumbLoader;

public class VideoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Video> mList;
	public Map<String, Integer> maps;
    private MyVideoThumbLoader mVideoThumbLoader;

	public VideoAdapter(Context context, List<Video> mList) {
		super();
		this.context = context;
		this.mList = mList;
		mInflater = LayoutInflater.from(context);
		mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
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
			arg1 = mInflater.inflate(R.layout.video_item, null);
			vh.iv_img = (ImageView) arg1.findViewById(R.id.iv_img);
			vh.tv_name = (TextView) arg1.findViewById(R.id.tv_name);
			vh.iv_circle = (ImageView) arg1.findViewById(R.id.iv_circle);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();

		}
		Video v = mList.get(arg0);
		
		vh.iv_img.setTag(v.getPath());//绑定imageview
		mVideoThumbLoader.showThumbByAsynctack(v.getPath(), vh.iv_img);

		vh.tv_name.setText(v.getPath().subSequence(v.getPath().lastIndexOf("/")+1, v.getPath().length()));
		
		boolean b = v.getShow();
		if (b) {
			vh.iv_circle.setVisibility(View.VISIBLE);
			if (v.getSelected()) {
				vh.iv_circle.setImageResource(R.drawable.checked);
			}else {
				vh.iv_circle.setImageResource(R.drawable.checked_no);
			}
		}else {
			vh.iv_circle.setVisibility(View.INVISIBLE);
		}

		return arg1;
	}

	class ViewHolder {
		ImageView iv_img,iv_circle;
		TextView tv_name,tv_date,tv_time,tv_distance,tv_lenth;
	}
	

}
