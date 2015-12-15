package com.imooc.downloaddemo01.model;

import java.io.Serializable;

/**
 * �ļ���Ϣ
 * 
 * @author Lang Junping
 *
 */

public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fileName; // �ļ���
	private int length; // �ļ�����
	private int id; // �ļ�ID
	private String url; // �ļ��������ϵ�url
	private int finished; // �ļ�����

	public FileInfo() {
		super();
	}

	public FileInfo(String fileName, int length, int id, String url,
			int finished) {
		super();
		this.fileName = fileName;
		this.length = length;
		this.id = id;
		this.url = url;
		this.finished = finished;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", length=" + length
				+ ", id=" + id + ", url=" + url + ", finished=" + finished
				+ "]";
	}

}
