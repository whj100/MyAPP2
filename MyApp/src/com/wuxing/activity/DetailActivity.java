package com.wuxing.activity;

import java.io.File;

import com.wuxing.activity.R;
import com.wuxing.activity.R.layout;
import com.wuxing.utils.FileUtil;
import com.wuxing.utils.SDCardUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends BaseActivity {
	TextView textView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		MyApplication.getInstance().addActivity(this);
		textView1 = (TextView) findViewById(R.id.textView1);
		String str1 = getAvailMemory();
		String str2 = getTotalMemory();
		
		Log.e("", "可用内存："+str1+"总内存："+str2);
		
		long ss1 = SDCardUtil.getAvailableInternalMemorySize();
		long ss2 = SDCardUtil.getTotalInternalMemorySize();
		Log.e("", "可用内存："+Formatter.formatFileSize(getBaseContext(), ss1)+"总内存："+Formatter.formatFileSize(getBaseContext(), ss2));
		
		long ssss1 = SDCardUtil.getAvailableExternalMemorySize();
		long ssss2 = SDCardUtil.getTotalExternalMemorySize();
		Log.e("", "可用内存："+Formatter.formatFileSize(getBaseContext(), ssss1)+"总内存："+Formatter.formatFileSize(getBaseContext(), ssss2));
	
		long size = FileUtil.getFolderSize(new File(FileUtil.initPath()));
		Log.e("", FileUtil.initPath());
		Log.e("",Formatter.formatFileSize(getBaseContext(), size));
		if (size>=0) {
			FileUtil.deleteOldestFile(new File(FileUtil.initPath()));
			long size1 = FileUtil.getFolderSize(new File(FileUtil.initPath()));
			Log.e("", FileUtil.initPath());
			Log.e("",Formatter.formatFileSize(getBaseContext(), size1));
		}
	
	}

	
}
