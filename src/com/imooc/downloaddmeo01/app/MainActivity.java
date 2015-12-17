package com.imooc.downloaddmeo01.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.imooc.downloaddemo01.model.FileInfo;
import com.imooc.downloaddemo01.services.DownloadService;
import com.imooc.downloaddmeo01.R;

public class MainActivity extends Activity {

	private ListView mListView;
	private List<FileInfo> mFileList;
	private ListViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initList();

		mListView = (ListView) findViewById(R.id.lv_listView);
		mAdapter = new ListViewAdapter(MainActivity.this, mFileList);
		mListView.setAdapter(mAdapter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_FINISHED);
		filter.addAction(DownloadService.ACTION_UPDATE);
		registerReceiver(receiver, filter);
	}

	private void initList() {
		mFileList = new ArrayList<FileInfo>();
		FileInfo mFileInfo = new FileInfo("mukewang0.apk", 0, 0,
				"http://www.imooc.com/mobile/mukewang.apk", 0);
		FileInfo mFileInfo1 = new FileInfo(
				"知乎.apk",
				0,
				1,
				"http://zhstatic.zhihu.com/pkg/store/zhihu/zhihu-android-app-zhihu-release-2.4.4-244.apk",
				0);
		FileInfo mFileInfo2 = new FileInfo(
				"知乎日报.apk",
				0,
				2,
				"http://zhstatic.zhihu.com/pkg/store/daily/zhihu-daily-zhihu-2.5.3(390).apk",
				0);
		mFileList.add(mFileInfo);
		mFileList.add(mFileInfo1);
		mFileList.add(mFileInfo2);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		long time = System.currentTimeMillis();

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			FileInfo mFileInfo = (FileInfo) intent
					.getSerializableExtra("fileInfo");
			if (DownloadService.ACTION_UPDATE.equals(action)) {
				// 更新UI
				int finished = intent.getIntExtra("finished", -1);
				int fileId = intent.getIntExtra("fileId", -1);
				// 调用Adpter中的更新UI方法；
				if ((System.currentTimeMillis() - time) > 1000) {
					time = System.currentTimeMillis();
					mAdapter.updateProgress(fileId, finished);
				}
			} else if (DownloadService.ACTION_FINISHED.equals(action)) {
				// 下载结束后重置，并弹出Toast提示
				Toast.makeText(MainActivity.this,
						mFileInfo.getFileName() + " 下载完成！", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
