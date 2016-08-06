package com.wuxing.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuxing.activity.HomeActivity;
import com.wuxing.activity.ImagePagerActivity;
import com.wuxing.activity.R;
import com.wuxing.adapter.PictureAdapter;
import com.wuxing.bean.Picture;
import com.wuxing.bean.Video;
import com.wuxing.utils.FileUtil;
import com.wuxing.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 */
@SuppressLint("DefaultLocale")
public class PictureListFragment extends Fragment implements OnClickListener {
	GridView gv_pictureList;
	LinearLayout ll_bottom;
	PictureAdapter adapter;
	List<Picture> mList;
	TextView tv_refrash,tv_back;
	ImageView iv_del, iv_share, iv_update,iv_check;
	int first;
	int last;
	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		
			adapter.notifyDataSetChanged();
		}

	};

	public PictureListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_picture, container,
				false);
	
		initView(rootView);
		
		return rootView;

	}

	private void initView(View rootView) {
		// TODO Auto-generated method stub
		gv_pictureList = (GridView) rootView.findViewById(R.id.gv_pictureList);
		mList = new ArrayList<Picture>();
		adapter = new PictureAdapter(getActivity(), mList);
		/*以下为新增部分*/    
		gv_pictureList.setAdapter(adapter);
		
		ll_bottom = (LinearLayout) rootView.findViewById(R.id.ll_bottom);

		tv_refrash = (TextView) rootView.findViewById(R.id.tv_refrash);
		tv_back = (TextView) rootView.findViewById(R.id.tv_back);
		
		iv_del = (ImageView) rootView.findViewById(R.id.iv_del);
		iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
		iv_update = (ImageView) rootView.findViewById(R.id.iv_update);

		iv_del.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		iv_update.setOnClickListener(this);

		tv_refrash.setOnClickListener(this);
		tv_back.setOnClickListener(this);
		gv_pictureList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			/**
			 * arg1: firstVisibleItem
			 * arg2；visibleItemCount
			 * arg3:totalItemCount
			 */
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				first = arg1;
				last =arg1+arg2-1;
			}
		});
		gv_pictureList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// VISIBLE:0 意思是可见的
				// INVISIBILITY:4 意思是不可见的，但还占着原来的空间
				// GONE:8 意思是不可见的，不占用原来的布局空间
				if (ll_bottom.getVisibility() == 0) {
					Picture p = mList.get(arg2);
					if (p.getSelected() == true) {
						p.setSelected(false);
					}else {
						p.setSelected(true);
					}
					adapter.notifyDataSetChanged();
				} else {
					// 显示大图
					String[] strs = new String[mList.size()]; 
					for (int i = 0; i < mList.size(); i++) {
						Picture p = mList.get(i);
						Uri uri = Uri.parse(p.getPath());
						strs[i] = uri.toString();
					}
					imageBrower(arg2,strs);
				}
			}

			private void imageBrower(int arg2, String[] strs) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, strs);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, arg2);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		});
		
		gv_pictureList
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						ll_bottom.setVisibility(View.VISIBLE);
						return true;
					}
				});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File file = new File(FileUtil.initPath());
				if (!file.exists()) {
					file.mkdirs();

				}
				mList.clear();
				mList.addAll(getPictures(FileUtil.initPath()));
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	// 1.获取SDCard中某个目录下图片路径集合
	public List<Picture> getPictures(final String strPath) {
		List<Picture> list = new ArrayList<Picture>();
		File file = new File(strPath);
		File[] allfiles = file.listFiles();
		if (allfiles == null) {
			return null;
		}
		for (int k = 0; k < allfiles.length; k++) {
			final File fi = allfiles[k];
			if (fi.isFile()) {
				int idx = fi.getPath().lastIndexOf(".");
				if (idx <= 0) {
					continue;
				}
				String suffix = fi.getPath().substring(idx);
				if (suffix.toLowerCase().equals(".jpg")

				|| suffix.toLowerCase().equals(".jpeg")
						|| suffix.toLowerCase().equals(".bmp")
						|| suffix.toLowerCase().equals(".png")
						|| suffix.toLowerCase().equals(".gif")) {
					Picture p = new Picture();
					p.setPath(fi.getPath());
					p.setSelected(false);
					list.add(p);
				}
			}
		}
		return list;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_refrash:
			ll_bottom.setVisibility(8);
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					File file = new File(FileUtil.initPath());
					if (!file.exists()) {
						file.mkdirs();
					}
					mList.clear();
					mList.addAll(getPictures(FileUtil.initPath()));
					handler.sendEmptyMessage(1);
				}
			}).start();
			break;
		case R.id.tv_back:
			// VISIBLE:0 意思是可见的
			// INVISIBILITY:4 意思是不可见的，但还占着原来的空间
			// GONE:8 意思是不可见的，不占用原来的布局空间
			if (ll_bottom.getVisibility() == 0) {
				for (int i = 0; i < mList.size(); i++) {
					Picture v = mList.get(i);
					v.setSelected(false);
				}
				adapter.notifyDataSetChanged();
				ll_bottom.setVisibility(8);
			} else {
				((HomeActivity) getActivity()).select(1);
			}
			break;
		case R.id.iv_del:

			for (int i = 0; i < mList.size(); i++) {
					
					if (mList.get(i).getSelected() == true) {
						FileUtil.deleteFile(new File(mList.get(i).getPath()));
					}	
			}
			mList.clear();
			mList.addAll(getPictures(FileUtil.initPath()));
			adapter.notifyDataSetChanged();
			ll_bottom.setVisibility(8);
			break;
		case R.id.iv_share:
			break;
		case R.id.iv_update:
			break;

		default:
			break;
		}
	}
}
