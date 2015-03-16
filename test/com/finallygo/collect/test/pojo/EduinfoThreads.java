package com.finallygo.collect.test.pojo;

import java.util.Date;

public class EduinfoThreads {
	private Long threadId;
	private String threadTitle;
	private String threadContent;
	private String userName;
	private Date postDate;
	private EduinfoThreadType threadType;
	
	public String getThreadTitle() {
		return threadTitle;
	}
	public void setThreadTitle(String threadTitle) {
		this.threadTitle = threadTitle;
	}
	public String getThreadContent() {
		return threadContent;
	}
	public void setThreadContent(String threadContent) {
		this.threadContent = threadContent;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	public Long getThreadId() {
		return threadId;
	}
	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}
	public EduinfoThreadType getThreadType() {
		return threadType;
	}
	public void setThreadType(EduinfoThreadType threadType) {
		this.threadType = threadType;
	}
}
