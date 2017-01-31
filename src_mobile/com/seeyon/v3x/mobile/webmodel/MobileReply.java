package com.seeyon.v3x.mobile.webmodel;

public class MobileReply {
	private Long uid;
	private Integer attend;
	private boolean proxy;
	private String proxyName;
	public Integer getAttend() {
		return attend;
	}
	public void setAttend(Integer attend) {
		this.attend = attend;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public boolean isProxy() {
		return proxy;
	}
	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}
	public String getProxyName() {
		return proxyName;
	}
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	
	
}
