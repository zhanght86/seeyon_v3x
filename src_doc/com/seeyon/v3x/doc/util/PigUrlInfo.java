package com.seeyon.v3x.doc.util;

/**
 * 是否归档及其URL组合信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-25
 */
public class PigUrlInfo {

	private boolean pig;
	private String url;
	
	public PigUrlInfo() {}
	
	public PigUrlInfo(boolean pig, String url) {
		super();
		this.pig = pig;
		this.url = url;
	}

	public boolean isPig() {
		return pig;
	}
	public void setPig(boolean pig) {
		this.pig = pig;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
