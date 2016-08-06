package com.wuxing.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wuxing.activity.HomeActivity;
import com.wuxing.activity.PlayVideoActivity;
import com.wuxing.activity.R;
import com.wuxing.adapter.VideoAdapter;
import com.wuxing.bean.Video;
import com.wuxing.utils.FileUtil;
import com.wuxing.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 */
@SuppressLint("HandlerLeak")
public class PickUpListFragment extends Fragment implements OnClickListener {
	ListView lv_video;
	List<Video> mList;
	VideoAdapter adapter;
	TextView tv_refrash, tv_back;
	LinearLayout ll_bottom;
	ImageView iv_del, iv_share, iv_update;

	ImageView iv_circle;
	int first, last;

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// Toast.makeText(getContext(), mList.toString(),
			// Toast.LENGTH_SHORT).show();
			adapter.notifyDataSetChanged();
		}

	};

	public PickUpListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_pick_up, container,
				false);
		initView(rootView);
		
		return rootView;

	}

	private void initView(View rootView) {
		ll_bottom = (LinearLayout) rootView.findViewById(R.id.ll_bottom);
		lv_video = (ListView) rootView.findViewById(R.id.lv_video);
		mList = new ArrayList<Video>();
		adapter = new VideoAdapter(getActivity(), mList);
		lv_video.setAdapter(adapter);
		
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

		lv_video.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				ll_bottom.setVisibility(View.VISIBLE);
				for (int i = 0; i < mList.size(); i++) {
					Video v = mList.get(i);
					v.setShow(true);
				}
				adapter.notifyDataSetChanged();
				return true;
			}
		});
		lv_video.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Video v = mList.get(arg2);
				// VISIBLE:0 意思是可见的
				// INVISIBILITY:4 意思是不可见的，但还占着原来的空间
				// GONE:8 意思是不可见的，不占用原来的布局空间
				if (ll_bottom.getVisibility() == 8) {
					// 播放視頻
					Intent intent = new Intent(getActivity(),
							PlayVideoActivity.class);
					intent.putExtra("path", v.getPath());
					startActivity(intent);
					getActivity().finish();
				} else if (ll_bottom.getVisibility() == 0) {

					if (v.getSelected() == true) {
						v.setSelected(false);
					} else {
						v.setSelected(true);
					}
					adapter.notifyDataSetChanged();
				}
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File file = new File(FileUtil.initPath() + "/TestCamera");
				if (!file.exists()) {
					file.mkdirs();
				}
				mList.clear();
				mList.addAll(GetVideoFileName(FileUtil.initPath()
						+ "/TestCamera"));
				handler.sendEmptyMessage(1);
			}
		}).start();

	}

	// 获取当前目录下所有的mp4文件
	public static Vector<Video> GetVideoFileName(String fileAbsolutePath) {
		Vector<Video> vecFile = new Vector<Video>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();

		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				Video v = new Video();
				String filepath = subFile[iFileLength].getAbsolutePath();
				// 判断是否为MP4结尾
				if (filepath.trim().toLowerCase().endsWith(".mp4")) {
					v.setPath(filepath);
					v.setSelected(false);
					v.setShow(false);
					vecFile.add(v);
				}
			}
		}
		return vecFile;
	}

	// 调用系统播放器 播放视频
	private void playVideo(String videoPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String strend = "";
		if (videoPath.toLowerCase().endsWith(".mp4")) {
			strend = "mp4";
		} else if (videoPath.toLowerCase().endsWith(".3gp")) {
			strend = "3gp";
		} else if (videoPath.toLowerCase().endsWith(".mov")) {
			strend = "mov";
		} else if (videoPath.toLowerCase().endsWith(".wmv")) {
			strend = "wmv";
		}

		intent.setDataAndType(Uri.parse(videoPath), "video/" + strend);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_refrash:

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					File file = new File(FileUtil.initPath() + "/TestCamera");
					if (!file.exists()) {
						file.mkdirs();
					}
					mList.clear();
					mList.addAll(GetVideoFileName(FileUtil.initPath()
							+ "/TestCamera"));
					handler.sendEmptyMessage(1);
				}
			}).start();
			ll_bottom.setVisibility(8);
			
			break;
		case R.id.tv_back:
			// VISIBLE:0 意思是可见的
			// INVISIBILITY:4 意思是不可见的，但还占着原来的空间
			// GONE:8 意思是不可见的，不占用原来的布局空间
			if (ll_bottom.getVisibility() == 0) {
				for (int i = 0; i < mList.size(); i++) {
					Video v = mList.get(i);
					v.setSelected(false);
					v.setShow(false);
				}
				adapter.notifyDataSetChanged();
				ll_bottom.setVisibility(8);
			} else {
				((HomeActivity) getActivity()).select(1);
			}
			break;
		case R.id.iv_del:
			int k2 = mList.size();
			for (int i = 0; i < k2; i++) {

				if (mList.get(i).getSelected()) {
					File file = new File(mList.get(i).getPath());
					FileUtil.deleteFile(file);
				}
			}
			mList.clear();
			mList.addAll(GetVideoFileName(FileUtil.initPath() + "/TestCamera"));
			ll_bottom.setVisibility(8);
			adapter.notifyDataSetChanged();
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
