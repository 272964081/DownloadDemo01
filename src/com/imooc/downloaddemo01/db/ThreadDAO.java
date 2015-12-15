package com.imooc.downloaddemo01.db;

import java.util.List;

import com.imooc.downloaddemo01.model.ThreadInfo;
	/**
	 * ���ݿ���ʽӿ�
	 * @author Lang Junping
	 *
	 */
public interface ThreadDAO {
	/**
	 * �����߳���Ϣ
	 * @param threadInfo
	 */
	public void insert(ThreadInfo threadInfo);
	/**
	 * ɾ���߳���Ϣ
	 * @param url filepath
	 * @param thread_id  threadId
	 */
	public void delete(String url,int thread_id);
	/**
	 * �����߳����ؽ���
	 * @param url
	 * @param thread_id
	 */
	public void updateThread(String url,int thread_id,int finished);
	/**
	 * ��ѯ�̵߳��ļ���Ϣ
	 * @param url
	 * @return ����һ�������߳���Ϣ��List
	 */
	public List<ThreadInfo> getThread(String url);
	/**
	 * �ж��߳���Ϣ�Ƿ�
	 * @param url
	 * @param thread_id
	 * @return
	 */
	public boolean isExists(String url,int thread_id);
}
