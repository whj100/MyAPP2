package com.wuxing.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wuxing.activity.R;
import com.wuxing.bean.Picture;
import com.wuxing.utils.LoadLocalImageUtil;

public class PictureAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Picture> mList;

	public PictureAdapter(Context context, List<Picture> mList) {
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
			arg1 = mInflater.inflate(R.layout.picture_item, null);
			vh.iv_img = (ImageView) arg1.findViewById(R.id.iv_img);
			vh.iv_check = (ImageView) arg1.findViewById(R.id.iv_check);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		Picture p = mList.get(arg0);
		File file = new File(p.getPath());
		if (file.exists()) {
			Uri uri = Uri.parse(p.getPath());
			LoadLocalImageUtil.getInstance().displayFromSDCard(uri + "",
					vh.iv_img);
		}
		if (p.getSelected()) {
			vh.iv_check.setVisibility(View.VISIBLE);
		} else {
			vh.iv_check.setVisibility(View.INVISIBLE);
		}
		return arg1;
	}

	class ViewHolder {
		ImageView iv_img, iv_check;
	}

	public static Bitmap resizeBitMapImage1(String filePath, int targetWidth,
			int targetHeight) {
		Bitmap bitMapImage = null;
		// First, get the dimensions of the image
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		double sampleSize = 0;
		// Only scale if we need to
		// (16384 buffer for img processing)
		Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
				.abs(options.outWidth - targetWidth);
		if (options.outHeight * options.outWidth * 2 >= 1638) {
			// Load, scaling to smallest power of 2 that'll get it <= desired
			// dimensions
			sampleSize = scaleByHeight ? options.outHeight / targetHeight
					: options.outWidth / targetWidth;
			sampleSize = (int) Math.pow(2d,
					Math.floor(Math.log(sampleSize) / Math.log(2d)));
		}
		// Do the actual decoding
		options.inJustDecodeBounds = false;
		options.inTempStorage = new byte[128];
		while (true) {
			try {
				options.inSampleSize = (int) sampleSize;
				bitMapImage = BitmapFactory.decodeFile(filePath, options);
				break;
			} catch (Exception ex) {
				try {
					sampleSize = sampleSize * 2;
				} catch (Exception ex1) {
				}
			}
		}
		return bitMapImage;
	}

}
