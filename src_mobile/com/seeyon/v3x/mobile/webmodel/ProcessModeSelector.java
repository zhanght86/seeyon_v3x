package com.seeyon.v3x.mobile.webmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程选人
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-20
 */
public class ProcessModeSelector {
	private String id;

	private String name;

	/**
	 * 0-人的id， 1-人的名字
	 */
	private List<Object[]> memberId;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Object[]> getMemberId() {
		return memberId;
	}

	public void addMemberId(Long memberId, String name) {
		if (this.memberId == null) {
			this.memberId = new ArrayList<Object[]>();
		}

		this.memberId.add(new Object[] { memberId, name });
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return id + "\t" + name + "\t" + type + "\t" + memberId;
	}
	
	public int getmemberNumuber(){
		if(memberId!=null){
			return memberId.size();
		}else{
			return 0;
		}
		
	}

}
