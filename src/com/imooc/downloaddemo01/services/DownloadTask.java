package com.imooc.downloaddemo01.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;

import com.imooc.downloaddemo01.db.ThreadDAO;
import com.imooc.downloaddemo01.db.ThreadDAOimpl;
import com.imooc.downloaddemo01.model.FileInfo;
import com.imooc.downloaddemo01.model.ThreadInfo;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * ����������
 *
 */
public class DownloadTask {
	private Context mContext;
	private FileInfo mFileInfo;
	private ThreadDAO mThreadDAO;
	private boolean isPause = false;
	private int threadsCount = 1;
	private List<DownloadThread> mThreadList;
	private long mFinished = 0;
	//�̳߳�
	public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

	public DownloadTask(Context mContext, FileInfo mFileInfo, int threadsCount) {
		this.mContext = mContext;
		this.mFileInfo = mFileInfo;
		this.threadsCount = threadsCount;
		mThreadDAO = new ThreadDAOimpl(mContext);
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	/**
	 * ���ط���
	 */
	public void download() {

		// ��ȡ���ݿ���߳���Ϣ
		List<ThreadInfo> threads = mThreadDAO.getThread(mFileInfo.getUrl());
		// �ж��߳���Ϣ�Ƿ�Ϊ��
		if (threads.size() == 0) {
			// ���Ϊ�գ��򴴽��߳���Ϣ
			// �ȼ����ļ��ֶγ���
			int length = mFileInfo.getLength() / threadsCount;
			for (int i = 0; i < threadsCount; i++) {
				ThreadInfo mThreadInfo = new ThreadInfo(i, mFileInfo.getUrl(),
						i * length, (i + 1) * length - 1, 0);
				// �������������
				if (i == threadsCount - 1) {
					mThreadInfo.setStop(mFileInfo.getLength());
				}
				threads.add(mThreadInfo);
				// �����ݿ�����߳���Ϣ
				mThreadDAO.insert(mThreadInfo);
			}
			
		}
		// �����̣߳���ʼ����
		mThreadList = new ArrayList<DownloadThread>();
		for (ThreadInfo info : threads) {
			DownloadThread dt = new DownloadThread(info);
			// �����߳�
//			new Thread(dt).start();
			//���̳߳������߳�
			DownloadTask.sExecutorService.execute(dt);
			// ���߳���ӵ������У��������
			mThreadList.add(dt);
		}
	}

	/**
	 * �����߳�
	 *
	 */
	class DownloadThread implements Runnable {

		public boolean isThreadFinished = false;
		private ThreadInfo mThreadInfo;

		public DownloadThread(ThreadInfo mThreadInfo) {
			this.mThreadInfo = mThreadInfo;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream input = null;
			try {
				// ��������
				URL url = new URL(mThreadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(3000);
				conn.setRequestMethod("GET");
				// ��������λ��
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				// ���ط�Χ
				String range = "bytes=" + start + "-" + mThreadInfo.getStop();

				conn.setRequestProperty("Range", range);
				// ����д��λ��
				File file = new File(DownloadService.DOWNLOAD_PATH,
						mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				// �ۼ���ɽ���
				mFinished += mThreadInfo.getFinished();
				// ��ʼ����
				if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
					// ��ȡ����
					input = conn.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					while ((len = input.read(buffer)) != -1) {
						// д���ļ�
						raf.write(buffer, 0, len);
						// �ۼ������ļ���ɵĽ���
						mFinished += len;
						// �ۼ�ÿ���̵߳���ɽ���
						mThreadInfo
								.setFinished(mThreadInfo.getFinished() + len);
						int progress = (int) (mFinished * 100 / mFileInfo
								.getLength());
						// ���͸���UI�㲥
						Intent intent = new Intent(
								DownloadService.ACTION_UPDATE);
						intent.putExtra("finished", progress);
						intent.putExtra("fileId", mFileInfo.getId());
						mContext.sendBroadcast(intent);
						// ������ͣʱ�����߳����ؽ���
						if (isPause()) {
							mThreadDAO.updateThread(mThreadInfo.getUrl(),
									mThreadInfo.getId(),
									mThreadInfo.getFinished());
							return;
						}
					}
					isThreadFinished = true;
					// �ж����е��߳��Ƿ��������
					checkAllThreadIsFinished();
				} else {
					Toast.makeText(mContext, "��������ʧ��", Toast.LENGTH_SHORT)
							.show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					conn.disconnect();
					raf.close();
					if (input != null) {
						input.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private synchronized void checkAllThreadIsFinished() {
			boolean allFinished = true;
			// ��������
			for (DownloadThread dt : mThreadList) {
				if (!dt.isThreadFinished) {
					allFinished = false;
					return;
				}
			}

			if (allFinished) {
				Intent intent = new Intent(DownloadService.ACTION_FINISHED);
				intent.putExtra("fileInfo", mFileInfo);
				mContext.sendBroadcast(intent);
				mThreadDAO.delete(mFileInfo.getUrl());
			}
		}

	}
}
