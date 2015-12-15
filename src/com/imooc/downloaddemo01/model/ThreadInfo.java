package com.imooc.downloaddemo01.model;

/**
 * �߳���Ϣ
 * 
 * @author Lang Junping
 *
 */

public class ThreadInfo {
	private int id;
	private String url;
	private int start; // ��ʼ���ص�λ��
	private int stop; // ֹͣ���ص�λ��
	private int finished; // ���ؽ���

	public ThreadInfo() {
		super();
	}

	public ThreadInfo(int id, String url, int start, int stop, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.stop = stop;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "TheadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", stop=" + stop + ", finished=" + finished + "]";
	}

}
