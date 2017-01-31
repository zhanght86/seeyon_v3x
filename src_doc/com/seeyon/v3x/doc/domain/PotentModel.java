package com.seeyon.v3x.doc.domain;

import com.seeyon.v3x.doc.util.Constants;

/**
 * 权限vo
 */
public class PotentModel extends com.seeyon.v3x.common.ObjectToXMLBase implements Comparable<PotentModel> {
	// 用户
	private Long userId;

	private String userName;

	private String userType;

	// all权限的id
	private Long allId;

	// 是否有all权限
	private boolean all = false;

	private Long editId;

	private boolean edit = false;

	private Long addId;

	private boolean add = false;

	private Long readId;

	private boolean read = false;

	private Long listId;

	private boolean list = false;

	private Long browseId;

	private boolean browse = false;

	// 是否继承来的权限
	private boolean isInherit = false;

	// 是否当前库的管理员
	private boolean isLibOwner = false;

	// 是否订阅
	private boolean alert = false;

	private Long alertId;
	
	private int aclOrder;
	
	public int getAclOrder() {
		return aclOrder;
	}

	public void setAclOrder(int aclOrder) {
		this.aclOrder = aclOrder;
	}

	public Long getAlertId() {
		return alertId;
	}

	public void setAlertId(Long alertId) {
		this.alertId = alertId;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean getIsLibOwner() {
		return isLibOwner;
	}

	public void setIsLibOwner(boolean isLibOwner) {
		this.isLibOwner = isLibOwner;
	}

	public boolean isInherit() {
		return isInherit;
	}

	public void setInherit(boolean isInherit) {
		this.isInherit = isInherit;
	}

	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	public Long getAddId() {
		return addId;
	}

	public void setAddId(Long addId) {
		this.addId = addId;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public Long getAllId() {
		return allId;
	}

	public void setAllId(Long allId) {
		this.allId = allId;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Long getEditId() {
		return editId;
	}

	public void setEditId(Long editId) {
		this.editId = editId;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Long getReadId() {
		return readId;
	}

	public void setReadId(Long readId) {
		this.readId = readId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public boolean isBrowse() {
		return browse;
	}

	public void setBrowse(boolean browse) {
		this.browse = browse;
	}

	public Long getBrowseId() {
		return browseId;
	}

	public void setBrowseId(Long browseId) {
		this.browseId = browseId;
	}

	public int compareTo(PotentModel o) {
		if (this.getIsLibOwner() && !o.getIsLibOwner()) {
			return -1;
		} else if (!this.getIsLibOwner() && o.getIsLibOwner()) {
			return 1;
		} else {
			return ((Integer) this.getAclOrder()).compareTo((Integer) o.getAclOrder());
		}
	}
	
	public String descPotent(Long docResId) {
		return this.getUserId() + "," + this.getUserType() + ","
		+ docResId + "," + this.isAll() + "," + this.isEdit()
		+ "," + this.isAdd() + "," + this.isRead() + ","
		+ this.isList() + "," + this.isBrowse() + ","
		+ this.isInherit() + "," + this.isAlert();
	}
	
	/**
	 * 判断有无权限，在all/edit/add/readonly/list/browse中至少有一个具备权限
	 * @return
	 */
	public boolean hasPotent() {
		return this.isAll() || this.isAdd() || this.isEdit() || this.isRead() || this.isBrowse() || this.isList();
	}
	
	/**
	 * 将all/edit/add/readonly/list/browse按照给定的值进行统一设置
	 * @param value
	 */
	public void setAllAcl(boolean value) {
		this.setAdd(value);
		this.setAll(value);
		this.setBrowse(value);
		this.setEdit(value);
		this.setList(value);
		this.setRead(value);
	}
	
	/**
	 * 复制DocAcl的权限信息
	 * @param docAcl	权限信息
	 */
	public void copyAcl(DocAcl docAcl) {
		switch (docAcl.getPotenttype()) {
		case Constants.ALLPOTENT:
			this.setAll(true);
			this.setAllId(docAcl.getId());
			break;
		case Constants.EDITPOTENT:
			this.setEdit(true);
			this.setEditId(docAcl.getId());
			break;
		case Constants.ADDPOTENT:
			this.setAdd(true);
			this.setAddId(docAcl.getId());
			break;
		case Constants.READONLYPOTENT:
			this.setRead(true);
			this.setReadId(docAcl.getId());
			break;
		case Constants.LISTPOTENT:
			this.setList(true);
			this.setListId(docAcl.getId());
			break;
		case Constants.BROWSEPOTENT:
			this.setBrowse(true);
			this.setBrowseId(docAcl.getId());
			break;
		}
	}

}
