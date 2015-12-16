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
 * ����������
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
	 * ���ط���
	 */
	public void download() {
		// ��ȡ���ݿ���߳���Ϣ
		List<ThreadInfo> threads = mThreadDAO.getThread(mFileInfo.getUrl());
		ThreadInfo threadInfo = null;
		if (threads.size() == 0) {
			//��ʼ���߳���Ϣ
			threadInfo = new ThreadInfo(mFileInfo.getId(), mFileInfo.getUrl(),
					0, mFileInfo.getLength(),0);
		}else{
			threadInfo = threads.get(0);
//			mFileInfo.setFinished(threadInfo.getFinished());
		}
		//�������̣߳���������
		new Thread(new DownloadThread(threadInfo)).start();
	}

	/**
	 * �����߳�
	 *
	 */
	class DownloadThread implements Runnable {

		private ThreadInfo mThreadInfo;

		public DownloadThread(ThreadInfo mThreadInfo) {
			this.mThreadInfo = mThreadInfo;
		}

		@Override
		public void run() {
			// �����ݿ�����߳���Ϣ
			if (!mThreadDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
				mThreadDAO.insert(mThreadInfo);
			}
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
				//���ط�Χ
				String range = "bytes=" + start + "-"+ mThreadInfo.getStop();
				conn.setRequestProperty("Range",range);
				// ����д��λ��
				File file = new File(DownloadService.DOWNLOAD_PATH,
						mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent(DownloadService.ACTION_UPDATE);
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
						mFinished += len;
							int progress = (int) (mFinished * 100/ mFileInfo.getLength());
							
							intent.putExtra("finished", progress);
							mContext.sendBroadcast(intent);
						// ������ͣʱ�������ؽ���
						if (isPause()) {
							mThreadDAO.updateThread(mThreadInfo.getUrl(),
									mThreadInfo.getId(),
									mFinished);
							return;
						}
					}
					// ɾ���߳���Ϣ
					mThreadDAO
							.delete(mThreadInfo.getUrl(), mThreadInfo.getId());

				}else{
					Toast.makeText(mContext, "��������ʧ��", Toast.LENGTH_SHORT).show();
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
