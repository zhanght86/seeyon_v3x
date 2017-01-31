package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.doc.util.Constants;

/**
 * 常用文档vo
 */
public class DocFavoriteVO extends DocAclVO {
	private DocFavorite docFavorite;
	// 图标名称
	private String icon;
	// 是否文档连接
	private boolean isLink = false;
	// 是否文档夹连接
	private boolean isFolderLink = false;
	// 文档库id
	private long docLibId;
	// 文档库类型
	private byte docLibType;
	// 内容类型名称
	private String type;
	// 路径
	private String path;
	// 大小 xxx M
	private String size;
	// 创建人
	private String createUserName;
	
//	private byte siteType;
//	private long siteId;
	
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

	public DocFavoriteVO(DocFavorite df){
		super(df.getDocResource());
		this.docFavorite = df;
		DocResource dr = df.getDocResource();	
//		this.docFavorite = df;
		this.docLibId = dr.getDocLibId();
		if(dr.getIsFolder() ||Constants.isPigeonhole(dr.getFrType())||dr.getFrType() == Constants.LINK || dr.getFrType() == Constants.LINK_FOLDER)
			this.size = "";
		else
			this.size = Strings.formatFileSize(dr.getFrSize(), true);
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
	public boolean getIsLink() {
		return isLink;
	}
	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
	}
//	public DocFavorite getDocFavorite() {
//		return docFavorite;
//	}
//	public void setDocFavorite(DocFavorite docFavorite) {
//		this.docFavorite = docFavorite;
//	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean getIsFolderLink() {
		return isFolderLink;
	}

	public void setIsFolderLink(boolean isFolderLink) {
		this.isFolderLink = isFolderLink;
	}

	public DocFavorite getDocFavorite() {
		return docFavorite;
	}

	public void setDocFavorite(DocFavorite docFavorite) {
		this.docFavorite = docFavorite;
	}

//	public long getSiteId() {
//		return siteId;
//	}
//
//	public void setSiteId(long siteId) {
//		this.siteId = siteId;
//	}
//
//	public byte getSiteType() {
//		return siteType;
//	}
//
//	public void setSiteType(byte siteType) {
//		this.siteType = siteType;
//	}
}
