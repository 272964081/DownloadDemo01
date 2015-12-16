package com.imooc.downloaddemo01.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
	private long mFinished = 0;
	private boolean isPause = false;

	public DownloadTask(Context mContext, FileInfo mFileInfo) {
		this.mContext = mContext;
		this.mFileInfo = mFileInfo;
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
		ThreadInfo threadInfo = null;
		if (threads.size() == 0) {
			//初始化线程信息
			threadInfo = new ThreadInfo(mFileInfo.getId(), mFileInfo.getUrl(),
					0, mFileInfo.getLength(),0);
		}else{
			threadInfo = threads.get(0);
//			mFileInfo.setFinished(threadInfo.getFinished());
		}
		//创建子线程，进行下载
		new Thread(new DownloadThread(threadInfo)).start();
	}

	/**
	 * 下载线程
	 *
	 */
	class DownloadThread implements Runnable {

		private ThreadInfo mThreadInfo;

		public DownloadThread(ThreadInfo mThreadInfo) {
			this.mThreadInfo = mThreadInfo;
		}

		@Override
		public void run() {
			// 向数据库插入线程信息
			if (!mThreadDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
				mThreadDAO.insert(mThreadInfo);
			}
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
				//下载范围
				String range = "bytes=" + start + "-"+ mThreadInfo.getStop();
				conn.setRequestProperty("Range",range);
				// 设置写入位置
				File file = new File(DownloadService.DOWNLOAD_PATH,
						mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent(DownloadService.ACTION_UPDATE);
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
						mFinished += len;
							int progress = (int) (mFinished * 100/ mFileInfo.getLength());
							
							intent.putExtra("finished", progress);
							mContext.sendBroadcast(intent);
						// 下载暂停时保存下载进度
						if (isPause()) {
							mThreadDAO.updateThread(mThreadInfo.getUrl(),
									mThreadInfo.getId(),
									mFinished);
							return;
						}
					}
					// 删除线程信息
					mThreadDAO
							.delete(mThreadInfo.getUrl(), mThreadInfo.getId());

				}else{
					Toast.makeText(mContext, "网络请求失败", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					conn.disconnect();
					raf.close();
					if(input!=null){
						input.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
