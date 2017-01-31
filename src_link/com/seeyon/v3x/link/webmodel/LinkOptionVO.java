package com.seeyon.v3x.link.webmodel;

import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;

public class LinkOptionVO {
	private LinkOption option;
	private LinkOptionValue value;
	
	public LinkOptionVO(LinkOption option, LinkOptionValue value){
		this.option = option;
		this.value = value;
	}
	
	public LinkOption getOption() {
		return option;
	}
	public void setOption(LinkOption option) {
		this.option = option;
	}
	public LinkOptionValue getValue() {
		return value;
	}
	public void setValue(LinkOptionValue value) {
		this.value = value;
	}
}
