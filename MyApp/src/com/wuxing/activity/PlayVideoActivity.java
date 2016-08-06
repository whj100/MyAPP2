package com.wuxing.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.wuxing.service.MyService;
import com.wuxing.utils.Constant;

public class PlayVideoActivity extends BaseActivity implements OnClickListener {
	VideoView videoView;
	TextView tv_start, tv_pause;
	public String path = "";
	MyReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mapplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_play_video);
		MyApplication.getInstance().addActivity(this);
		path = getIntent().getStringExtra("path");

		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub

		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_pause = (TextView) findViewById(R.id.tv_pause);

		tv_start.setOnClickListener(this);
		tv_pause.setOnClickListener(this);

		videoView = (VideoView) findViewById(R.id.videoView1);
		Uri uri = Uri.parse(path);
		videoView.setMediaController(new MediaController(this));
		videoView.setVideoURI(uri);

		videoView.requestFocus();
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				videoView.start();
			}
		});
		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.service.close");
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_start:
			videoView.start();
			break;
		case R.id.tv_pause:
			videoView.pause();
			break;

		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopProgressDialog();
		unregisterReceiver(receiver);
	}
	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String type = arg1.getAction();
			if (type.equals("com.service.close")) {
				Log.e("接收停止服务", "消息");
				Intent intent2 = new Intent(PlayVideoActivity.this,
						HomeActivity.class);
				startActivity(intent2);
				finish();
			}

		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startProgressDialog();
			if (Constant.isRecorder) {
				Intent intent = new Intent();
				intent.setAction(Constant.STOP_CAMERA);
				sendBroadcast(intent);
				Intent intentService = new Intent(PlayVideoActivity.this,
						MyService.class);
				stopService(intentService);
			} else {
				videoView.setVisibility(8);
				Intent intent = new Intent(PlayVideoActivity.this,
						HomeActivity.class);
				intent.putExtra("int", 0);
				startActivity(intent);
				finish();
				
			}
			
		}
		return super.onKeyDown(keyCode, event);

	}
	

}
