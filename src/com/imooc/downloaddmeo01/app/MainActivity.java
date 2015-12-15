package com.imooc.downloaddmeo01.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.downloaddemo01.model.FileInfo;
import com.imooc.downloaddemo01.services.DownloadService;
import com.imooc.downloaddmeo01.R;

public class MainActivity extends Activity {

	private TextView tv_fileName;
	private ProgressBar pb_downLoad;
	private Button btn_start, btn_stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 实例化FileInfo
		final FileInfo fileInfo = new FileInfo(
				"mukewang.apk",
				0,
				0,
				"http://www.imooc.com/mobile/mukewang.apk",
				0);
		// 初始化数据
		init(fileInfo);
		// 添加点击事件
		btn_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra("fileInfo", fileInfo);
				startService(intent);
			}
		});
		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra("fileInfo", fileInfo);
				startService(intent);
			}
		});
		
		//给Activity注册广播
		IntentFilter mFilter = new IntentFilter(DownloadService.ACTION_UPDATE);
		registerReceiver(mReceiver, mFilter);
	}
	/**
	 * 创建广播接收器
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		long time = System.currentTimeMillis();
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
				int finished = intent.getIntExtra("finished", 0);
				if((System.currentTimeMillis()-time)>500){
					time = System.currentTimeMillis();
					pb_downLoad.setProgress(finished);
					if(finished==100){
						Toast.makeText(MainActivity.this, "下载已经完成", Toast.LENGTH_SHORT).show();
						btn_start.setText("完成");
						//TODO
						btn_stop.setText("删除");
					}
				}
			}
		}
	};

	private void init(FileInfo file) {
		tv_fileName = (TextView) findViewById(R.id.tv_fileName);
		pb_downLoad = (ProgressBar) findViewById(R.id.pb_download);
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		pb_downLoad.setMax(100);
		tv_fileName.setText(file.getFileName());
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//注销Receiver
		unregisterReceiver(mReceiver);
	}
}
