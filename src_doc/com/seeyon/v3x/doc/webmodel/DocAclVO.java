package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocResource;

import com.seeyon.v3x.doc.util.Constants;

/**
 * 权限vo
 * 
 **/
public class DocAclVO {
	private DocResource docResource;

	// 当前用户对当前DocResource对象的权限标记
	private boolean allAcl = false;

	private boolean editAcl = false;

	private boolean addAcl = false;

	private boolean readOnlyAcl = false;

	private boolean browseAcl = false;

	private boolean listAcl = false;
	
	private boolean deptBorrowAcl = false;
	
	// 可以删除标记，主要用来控制文档夹的删除权限
	private boolean canDel = false;	
	
	// 是否借阅共享标记
	private boolean isBorrowOrShare;
	
	// 是否个人文档库标记
	private boolean isPersonalLib;
	
	// 是否虚拟节点标记，即是否表示的借阅共享中的用户，用于控制前台的菜单是否弹出
	private boolean isPerson;	
	
	// docResource的name是否为key，即页面是否需要国际化
	private boolean needI18n;	
	
	// 是否属于系统预置的内容，这些内容不可以进行 移动、删除、重命名
	private boolean isSysInit;
	
    //权限类型 取值 Constansts.lenPotent_xxx
	private Byte lenPotent=Constants.LENPOTENT_ALL;
	
	private String lenPotent2;
	private String edocPotent;

	public boolean getIsSysInit() {
		return isSysInit;
	}

	public void setIsSysInit(boolean isSysInit) {
		this.isSysInit = isSysInit;
	}
	
	public boolean getNeedI18n() {
		return needI18n;
	}

	public void setNeedI18n(boolean needI18n) {
		this.needI18n = needI18n;
	}
	
	public boolean getIsPerson() {
		return isPerson;
	}

	public void setIsPerson(boolean isPerson) {
		this.isPerson = isPerson;
	}

	public boolean getCanDel() {
		return canDel;
	}

	public void setCanDel(boolean canDel) {
		this.canDel = canDel;
	}

	public boolean getIsPersonalLib() {
		return isPersonalLib;
	}

	public void setIsPersonalLib(boolean isPersonalLib) {
		this.isPersonalLib = isPersonalLib;
	}

	public boolean isAddAcl() {
		return addAcl;
	}

	public void setAddAcl(boolean addAcl) {
		this.addAcl = addAcl;
	}

	public boolean isAllAcl() {
		return allAcl;
	}

	public void setAllAcl(boolean allAcl) {
		this.allAcl = allAcl;
	}

	public boolean isBrowseAcl() {
		return browseAcl;
	}

	public void setBrowseAcl(boolean browseAcl) {
		this.browseAcl = browseAcl;
	}

	public boolean isEditAcl() {
		return editAcl;
	}

	public void setEditAcl(boolean editAcl) {
		this.editAcl = editAcl;
	}

	public boolean isListAcl() {
		return listAcl;
	}

	public void setListAcl(boolean listAcl) {
		this.listAcl = listAcl;
	}

	public boolean isReadOnlyAcl() {
		return readOnlyAcl;
	}

	public void setReadOnlyAcl(boolean readOnlyAcl) {
		this.readOnlyAcl = readOnlyAcl;
	}
	
	public boolean isDeptBorrowAcl() {
		return deptBorrowAcl;
	}

	public void setDeptBorrowAcl(boolean deptBorrowAcl) {
		this.deptBorrowAcl = deptBorrowAcl;
	}

	public boolean getIsBorrowOrShare() {
		return isBorrowOrShare;
	}

	public void setIsBorrowOrShare(boolean isBorrowOrShare) {
		this.isBorrowOrShare = isBorrowOrShare;
	}
	
	public DocResource getDocResource() {
		return docResource;
	}

	public void setDocResource(DocResource docResource) {
		this.docResource = docResource;
	}
	
	public DocAclVO(DocResource dr) {
		this.docResource = dr;
		
		this.isSysInit = this.getSysInit(docResource.getFrType());
	}

	public DocAclVO(DocAcl da) {
		if(da!=null){
			this.lenPotent=da.getLenPotent();
			this.lenPotent2=da.getLenPotent2();
		}
	}
	
	public DocAclVO() {}
	
	private boolean getSysInit(long frType) {
		boolean ret = false;
		
		if(frType == Constants.FOLDER_ARC_PRE || frType == Constants.FOLDER_CASE || frType == Constants.FOLDER_CASE_PHASE)
			ret = true;
		
		return ret;
	}

	public Byte getLenPotent() {
		return lenPotent;
	}

	public void setLenPotent(Byte lenPotent) {
		this.lenPotent = lenPotent;
	}

	public String getLenPotent2() {
		return lenPotent2;
	}

	public void setLenPotent2(String lenPotent2) {
		this.lenPotent2 = lenPotent2;
	}

	public String getEdocPotent() {
		if(lenPotent==null){lenPotent=0;}
		if(lenPotent2==null){lenPotent2="00";}
		edocPotent=""+lenPotent+lenPotent2;
		return edocPotent;
	}

	public void setEdocPotent(String edocPotent) {
		this.edocPotent = edocPotent;
	}


}
