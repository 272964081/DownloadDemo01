package com.imooc.downloaddmeo01.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
		// 初始化数据
		init();
		// 实例化FileInfo
		final FileInfo fileInfo = new FileInfo(
				"QQ_7.9.16638.0_setup.1449542695.exe",
				0,
				0,
				"http://dlsw.baidu.com/sw-search-sp/soft/3a/12350/QQ_7.9.16638.0_setup.1449542695.exe",
				0);
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
	}

	private void init() {
		tv_fileName = (TextView) findViewById(R.id.tv_fileName);
		pb_downLoad = (ProgressBar) findViewById(R.id.pb_download);
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
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
}
