package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.Constants;
@Deprecated
public class DocLinkVO extends DocAclVO {
	private DocResource docRes;
	private String userName;		//创建人
	private String icon;
	private boolean isShortCut;
	private String type;
	
	// 是否文档夹映射
	private boolean isFolderLink;
	
	private byte docLibType;
		
	public byte getDocLibType() {
		return docLibType;
	}
	public void setDocLibType(byte docLibType) {
		this.docLibType = docLibType;
	}
	public boolean getIsFolderLink() {
		return isFolderLink;
	}
	public void setIsFolderLink(boolean isFolderLink) {
		this.isFolderLink = isFolderLink;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean getIsShortCut() {
		return isShortCut;
	}
	public void setIsShortCut(boolean isShortCut) {
		this.isShortCut = isShortCut;
	}
	public DocLinkVO(DocResource docRes){
		super(docRes);
		this.docRes=docRes;
		this.isFolderLink = (docRes.getFrType() == Constants.LINK_FOLDER);
	}
	public DocResource getDocRes() {
		return docRes;
	}
	public void setDocRes(DocResource docRes) {
		this.docRes = docRes;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
