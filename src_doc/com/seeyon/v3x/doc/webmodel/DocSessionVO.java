package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocSession;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 最近文档vo
 */
@Deprecated
public class DocSessionVO extends DocAclVO {
	private DocSession docSession;
	private String icon;
	private boolean isLink = false;
	private long docLibId;
	private byte docLibType;
	private String type;
	private String size;
	private String createUserName;
	
	public DocSessionVO(DocSession docSession, DocResource dr) {
		super(dr);
		this.docSession = docSession;
		this.docLibId = dr.getDocLibId();
		if(dr.getIsFolder() || dr.getFrType() == Constants.LINK || dr.getFrType() == Constants.LINK_FOLDER)
			this.size = "";
		else
			this.size = Strings.formatFileSize(dr.getFrSize(), true);
	}

	public DocSession getDocSession() {
		return docSession;
	}

	public void setDocSession(DocSession docSession) {
		this.docSession = docSession;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean getIsLink() {
		return isLink;
	}

	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
