package com.imooc.downloaddemo01.model;

import java.io.Serializable;

/**
 * 文件信息
 * 
 * @author Lang Junping
 *
 */

public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fileName; // 文件名
	private int length; // 文件长度
	private int id; // 文件ID
	private String url; // 文件在网络上的url
	private int finished; // 文件进度

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
