package com.seeyon.v3x.plugin.dee.model;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class SyncListenerBean implements DeeResource {

	private String listenerInfo;
	public SyncListenerBean(){}
	
	public SyncListenerBean(String xml) {
		this.setListenerInfo("<listener class=\"com.seeyon.v3x.dee.common.listener.FlowListener\">");
	}
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toXML(String name) {
		return "<listener class=\"com.seeyon.v3x.dee.common.listener.FlowListener\">";
	}

	public String getListenerInfo() {
		return listenerInfo;
	}

	public void setListenerInfo(String listenerInfo) {
		this.listenerInfo = listenerInfo;
	}

}
