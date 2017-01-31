/**
 * $Id: AddressBookTeam.java,v 1.3 2007/07/03 02:02:52 jincm Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * <p/> Title:通讯录个人组/个人类别<实体>
 * </p>
 * <p/> Description:通讯录个人组/个人类别<实体>
 * </p>
 * <p/> Date:5/24/07
 * 
 * @author Paul(qdlake@gmail.com)
 *
 */
public class AddressBookTeam extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1111319755535113511L;
	
	public static final int TYPE_SYSTEAM = 0;
	public static final int TYPE_OWNTEAM = 1;
	public static final int TYPE_CATEGORY = 2;
	
	private String name; //组名
	private int type; //组类型(1：个人组；2：类别)
	private Long creatorId; //创建人ID
	private String creatorName; //创建人
	private Date createdTime; //创建时间
	private Date modifiedTime; //修改时间
	private String memo; //备注

	
	public Date getCreatedTime() { 
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
