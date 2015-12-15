package com.imooc.downloaddemo01.db;

import java.util.List;

import com.imooc.downloaddemo01.model.ThreadInfo;
	/**
	 * 数据库访问接口
	 * @author Lang Junping
	 *
	 */
public interface ThreadDAO {
	/**
	 * 插入线程信息
	 * @param threadInfo
	 */
	public void insert(ThreadInfo threadInfo);
	/**
	 * 删除线程信息
	 * @param url filepath
	 * @param thread_id  threadId
	 */
	public void delete(String url,int thread_id);
	/**
	 * 更新线程下载进度
	 * @param url
	 * @param thread_id
	 */
	public void updateThread(String url,int thread_id,int finished);
	/**
	 * 查询线程的文件信息
	 * @param url
	 * @return 返回一个含有线程信息的List
	 */
	public List<ThreadInfo> getThread(String url);
	/**
	 * 判断线程信息是否
	 * @param url
	 * @param thread_id
	 * @return
	 */
	public boolean isExists(String url,int thread_id);
}
