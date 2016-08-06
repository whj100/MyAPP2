package com.wuxing.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.wuxing.dialog.CustomProgressDialog;
import com.wuxing.fragment.CameraFragment;
import com.wuxing.fragment.PickUpListFragment;
import com.wuxing.fragment.PictureListFragment;

@SuppressLint("NewApi")
public class HomeActivity extends FragmentActivity {
	ViewPager mViewPager;
	CameraFragment cameraFragment;
	PickUpListFragment pickUpListFragment;
	PictureListFragment pictureListFragment;
	List<Fragment> mList;
	//private CustomProgressDialog progressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_home);
		MyApplication.getInstance().addActivity(this);
		
		initView();
		int curr = getIntent().getIntExtra("int", 1);
		mViewPager.setCurrentItem(curr);
	
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	public void select(int index) {
		mViewPager.setCurrentItem(index);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOffscreenPageLimit(2);
		mList = new ArrayList<Fragment>();
		mList.add(new PickUpListFragment());
		mList.add(new CameraFragment());
		mList.add(new PictureListFragment());
		mViewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mList.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return mList.get(arg0);
			}
		});
	}


//	/**
//	 * 显示进度框
//	 */
//	public void startProgressDialog(){
//		if (progressDialog == null){
//			progressDialog = CustomProgressDialog.createDialog(this);
//	    	//progressDialog.setMessage("正在加载中...");
//	    	progressDialog.setCanceledOnTouchOutside(false);
//	    	progressDialog.setCancelable(true);
//		}
//		
//    	progressDialog.show();
//	}
//
//	/**
//	 * 隐藏进度框
//	 */
//	public void stopProgressDialog(){
//		if (progressDialog != null){
//			progressDialog.dismiss();
//			progressDialog = null;
//		}
//	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			int index = mViewPager.getCurrentItem();
			switch (index) {
			case 0:
				select(1);
				break;
			case 1:
				dialog();
				break;
			case 2:
				select(1);
				break;

			default:
				break;
			}
			

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		builder.setMessage("确认退出吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				MyApplication.getInstance().exit();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		});
		builder.create().show();
	

	}
}