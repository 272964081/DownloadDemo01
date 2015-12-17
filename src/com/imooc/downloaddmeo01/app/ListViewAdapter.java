package com.imooc.downloaddmeo01.app;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imooc.downloaddemo01.model.FileInfo;
import com.imooc.downloaddemo01.services.DownloadService;
import com.imooc.downloaddmeo01.R;

public class ListViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<FileInfo> fileList;

	public ListViewAdapter(Context mContext, List<FileInfo> fileList) {
		this.mContext = mContext;
		this.fileList = fileList;
	}

	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final FileInfo mFileInfo = fileList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
			holder.tv_fileName = (TextView) convertView
					.findViewById(R.id.tv_fileName);
			holder.btn_start = (Button) convertView
					.findViewById(R.id.btn_start);
			holder.btn_stop = (Button) convertView.findViewById(R.id.btn_stop);
			holder.pb_download = (ProgressBar) convertView
					.findViewById(R.id.pb_download);

			holder.tv_fileName.setText(mFileInfo.getFileName());
			holder.btn_start.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", mFileInfo);
					mContext.startService(intent);

				}
			});
			holder.btn_stop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", mFileInfo);
					mContext.startService(intent);

				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.pb_download.setProgress(mFileInfo.getFinished());

		return convertView;
	}

	/**
	 * 更新进度条
	 * 
	 * @param id
	 * @param progress
	 */
	public void updateProgress(int fileId, int finished) {
		FileInfo fileInfo = fileList.get(fileId);
		fileInfo.setFinished(finished);
		notifyDataSetChanged();// 调用此方法后系统会重新调用getView方法，完成刷新UI的操作
	}

	static class ViewHolder {
		TextView tv_fileName;
		Button btn_start, btn_stop;
		ProgressBar pb_download;
	}

}
