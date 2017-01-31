package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 最新订阅vo
 */
public class DocAlertLatestVO extends DocAclVO {
	private DocAlertLatest docAlertLatest;
	private String lastUserName;
	private boolean isLink = false;
	private long docLibId;
	private byte docLibType;
	private String icon;
	private String type;
	private String path;
	private String size;
	private String createUserName;
	private String oprType;
	private boolean hasAttachments ;
	
	public boolean getHasAttachments(){
		return  hasAttachments ;
	}
	public void setHasAttachments(boolean hasAttachments){
		this.hasAttachments = hasAttachments ;
	}
	
	public String getOprType() {
		return oprType;
	}

	public void setOprType(String oprType) {
		this.oprType = oprType;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getDocLibId() {
		return docLibId;
	}

	public void setDocLibId(long docLibId) {
		this.docLibId = docLibId;
	}

	public byte getDocLibType() {
		return docLibType;
	}

	public void setDocLibType(byte docLibType) {
		this.docLibType = docLibType;
	}

	public DocAlertLatestVO(DocAlertLatest docAlertLatest, DocResource dr){
		super(dr);
		this.docAlertLatest = docAlertLatest;
		this.docLibId = dr.getDocLibId();
//		if(dr.getIsFolder() || dr.getFrType() == Constants.LINK || dr.getFrType() == Constants.LINK_FOLDER)
//			this.size = "";
//		else
//			this.size = Strings.formatFileSize(dr.getFrSize(), false);
		this.setHasAttachments(dr.getHasAttachments()) ;
	}
	
	public boolean getIsLink() {
		return isLink;
	}

	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
	}
	
	public String getLastUserName() {
		return lastUserName;
	}
	public void setLastUserName(String lastUserName) {
		this.lastUserName = lastUserName;
	}
	public DocAlertLatest getDocAlertLatest() {
		return docAlertLatest;
	}
	public void setDocAlertLatest(DocAlertLatest docAlertLatest) {
		this.docAlertLatest = docAlertLatest;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
