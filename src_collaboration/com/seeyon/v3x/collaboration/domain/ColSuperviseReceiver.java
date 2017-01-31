package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColSuperviseReceiver extends BaseModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long logId;
	private long receiver;
	private long parallelismId;
	public long getLogId() {
		return logId;
	}
	public void setLogId(long logId) {
		this.logId = logId;
	}
	public long getParallelismId() {
		return parallelismId;
	}
	public void setParallelismId(long parallelismId) {
		this.parallelismId = parallelismId;
	}
	public long getReceiver() {
		return receiver;
	}
	public void setReceiver(long receiver) {
		this.receiver = receiver;
	}
}
