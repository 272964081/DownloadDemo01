package com.imooc.downloaddemo01.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.imooc.downloaddemo01.model.ThreadInfo;

public class ThreadDAOimpl implements ThreadDAO {

	private DBHelper mHelper = null;

	public ThreadDAOimpl(Context context) {
		mHelper = new DBHelper(context);
	}

	@Override
	public void insert(ThreadInfo threadInfo) {
		// 创建数据库
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.execSQL(
				"insert into thread_info(thread_id,url,start,stop,finished) values(?,?,?,?,?)",
				new Object[] { threadInfo.getId(), threadInfo.getUrl(),
						threadInfo.getStart(), threadInfo.getStop(),
						threadInfo.getFinished() });
		db.close();

	}

	@Override
	public void delete(String url, int thread_id) {
		
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.execSQL(
				"delete from thread_info where url=? and thread_id=?",
				new Object[] {url,thread_id});
		db.close();

	}

	@Override
	public void updateThread(String url, int thread_id, long finished) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.execSQL(
				"update thread_info set finished=? where url=? and thread_id=?",
				new Object[] {finished,url,thread_id});
		db.close();
		
	}

	@Override
	public List<ThreadInfo> getThread(String url) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		Cursor cur = db.rawQuery("select * from thread_info where url=?",
				new String[] {url});
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		ThreadInfo mThread = null;
		while(cur.moveToNext()){
			mThread = new ThreadInfo();
			mThread.setId(cur.getInt(cur.getColumnIndex("thread_id")));
			mThread.setStart(cur.getInt(cur.getColumnIndex("start")));
			mThread.setStop(cur.getInt(cur.getColumnIndex("stop")));
			mThread.setFinished(cur.getInt(cur.getColumnIndex("finished")));
			mThread.setUrl(cur.getString(cur.getColumnIndex("url")));
			
			list.add(mThread);
		}
		cur.close();
		db.close();
		
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from thread_info where url=? and thread_id=?",
				new String[] {url,thread_id+""});
		boolean exists = cur.moveToNext();
		cur.close();
		db.close();
		return exists;
	}


}
