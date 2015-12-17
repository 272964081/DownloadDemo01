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
 * 下载任务类
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
	//线程池
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
	 * 下载方法
	 */
	public void download() {

		// 读取数据库的线程信息
		List<ThreadInfo> threads = mThreadDAO.getThread(mFileInfo.getUrl());
		// 判断线程信息是否为空
		if (threads.size() == 0) {
			// 如果为空，则创建线程信息
			// 先计算文件分段长度
			int length = mFileInfo.getLength() / threadsCount;
			for (int i = 0; i < threadsCount; i++) {
				ThreadInfo mThreadInfo = new ThreadInfo(i, mFileInfo.getUrl(),
						i * length, (i + 1) * length - 1, 0);
				// 不能整除的情况
				if (i == threadsCount - 1) {
					mThreadInfo.setStop(mFileInfo.getLength());
				}
				threads.add(mThreadInfo);
				// 向数据库插入线程信息
				mThreadDAO.insert(mThreadInfo);
			}
			
		}
		// 启动线程，开始下载
		mThreadList = new ArrayList<DownloadThread>();
		for (ThreadInfo info : threads) {
			DownloadThread dt = new DownloadThread(info);
			// 启动线程
//			new Thread(dt).start();
			//用线程池启动线程
			DownloadTask.sExecutorService.execute(dt);
			// 将线程添加到集合中，方便管理
			mThreadList.add(dt);
		}
	}

	/**
	 * 下载线程
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
				// 开启网络
				URL url = new URL(mThreadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(3000);
				conn.setRequestMethod("GET");
				// 设置下载位置
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				// 下载范围
				String range = "bytes=" + start + "-" + mThreadInfo.getStop();

				conn.setRequestProperty("Range", range);
				// 设置写入位置
				File file = new File(DownloadService.DOWNLOAD_PATH,
						mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				// 累加完成进度
				mFinished += mThreadInfo.getFinished();
				// 开始下载
				if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
					// 读取数据
					input = conn.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					while ((len = input.read(buffer)) != -1) {
						// 写入文件
						raf.write(buffer, 0, len);
						// 累加整个文件完成的进度
						mFinished += len;
						// 累加每个线程的完成进度
						mThreadInfo
								.setFinished(mThreadInfo.getFinished() + len);
						int progress = (int) (mFinished * 100 / mFileInfo
								.getLength());
						// 发送更新UI广播
						Intent intent = new Intent(
								DownloadService.ACTION_UPDATE);
						intent.putExtra("finished", progress);
						intent.putExtra("fileId", mFileInfo.getId());
						mContext.sendBroadcast(intent);
						// 下载暂停时保存线程下载进度
						if (isPause()) {
							mThreadDAO.updateThread(mThreadInfo.getUrl(),
									mThreadInfo.getId(),
									mThreadInfo.getFinished());
							return;
						}
					}
					isThreadFinished = true;
					// 判断所有的线程是否下载完毕
					checkAllThreadIsFinished();
				} else {
					Toast.makeText(mContext, "网络请求失败", Toast.LENGTH_SHORT)
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
			// 遍历集合
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
