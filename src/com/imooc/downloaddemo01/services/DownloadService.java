package com.imooc.downloaddemo01.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.imooc.downloaddemo01.model.FileInfo;

public class DownloadService extends Service {

	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";
	public static final String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";

	public static final int MSG_INIT = 0x001;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
		if (ACTION_START.equals(intent.getAction())) {

			Log.i("lang", "start:" + fileInfo.toString());

			new initThread(fileInfo).start();

		} else if (ACTION_STOP.equals(intent.getAction())) {

			Log.i("lang", "stop:" + fileInfo.toString());
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				FileInfo mFileInfo = (FileInfo) msg.obj;
				Log.i("lang", "handler:" + mFileInfo.toString());
				break;

			default:
				break;
			}
		}
	};

	/**
	 * ��ʼ���߳�����
	 * 
	 * @author Lang Junping
	 *
	 */

	class initThread extends Thread {
		private FileInfo mFileInfo;

		public initThread(FileInfo mFileInfo) {
			this.mFileInfo = mFileInfo;
		}

		@Override
		public void run() {
			// �������磬��ѯ���ȣ������ļ���������Ϣ
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			try {
				// ��������
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5000);
				// ��ȡ�ļ�����
				int length = -1;
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					length = conn.getContentLength();
				}
				if (length <= 0) {
					return;
				}
				// �����ļ���
				File dir = new File(DOWNLOAD_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// �����ļ�
				File mFile = new File(dir, mFileInfo.getFileName());
				// ����RAF�ļ�
				raf = new RandomAccessFile(mFile, "rwd");
				raf.setLength(length);
				// �����ļ����ȣ���֪ͨ���߳�
				mFileInfo.setLength(length);
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				conn.disconnect();
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
