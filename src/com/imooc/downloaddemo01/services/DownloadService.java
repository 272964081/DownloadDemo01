package com.imooc.downloaddemo01.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.imooc.downloaddemo01.model.FileInfo;

public class DownloadService extends Service {

	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	public static final String ACTION_FINISHED = "ACTION_FINISHED";
	public static final String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
	public static final int THREAD_COUNT = 3; //�����߳���

	public static final int MSG_INIT = 0x001;
	
	private InitThread mInitThread = null;
	
//	private DownloadTask mTask = null;
	private Map<Integer,DownloadTask> mTasks = new LinkedHashMap<Integer,DownloadTask>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
		if (ACTION_START.equals(intent.getAction())) {
			//��������
			mInitThread = new InitThread(fileInfo);
//			mInitThread.start();
			DownloadTask.sExecutorService.execute(mInitThread);

		} else if (ACTION_STOP.equals(intent.getAction())) {
			//��ͣ����
			int id = fileInfo.getId();
			if(mTasks!=null){
				DownloadTask mTask = mTasks.get(id);
				mTask.setPause(true);
			}
			
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
				//��������
				DownloadTask mTask= new DownloadTask(DownloadService.this, mFileInfo,THREAD_COUNT);
				mTask.download();
				mTasks.put(mFileInfo.getId(), mTask);
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

	class InitThread extends Thread {
		private FileInfo mFileInfo;

		public InitThread(FileInfo mFileInfo) {
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
