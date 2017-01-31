package com.seeyon.v3x.doc.webmodel;

import java.io.Serializable;
import java.sql.Timestamp;

import com.seeyon.v3x.doc.domain.DocResource;


/**
 * FolderItem 抽象类专为文档内容管理定制，用于返回一个完整的“头+体+尾”结构的文档、文档夹数据， 实现中根据业务的需求继承产生不同的子类，比如
 * Folder, FolderItemLink, FolderItemCol等。
 * 
 * 为了保证父类和子类的属性都能正确设置， 1. 只提供了一个构造方法 FolderItem(DocResource docResource),
 * 子类必须调用它； 2. 子类必须覆盖父类的 setDocResource(DocResource docResorce) 方法。
 * 上面两个方法的子类实现如下所示： public FolderItemXxx(DocResource dr) { super(dr);
 * this.setXxx(...); }
 * 
 * public void setDocResource(DocResource dr) { super.setDocResource(dr);
 * this.setXxx(...); }
 */

public abstract class FolderItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7509082039856202705L;

	/**　保存相关的DocResource对象　*/
	private DocResource docResource;
	
	private Long docResourceId;
	

	private String name;

	private String desc;

	private String type;

	/**　保存物理路径（从根节点开始的所有节点的name连接） */
	private String path;

	private String icon;

	private String createUserName;

	private Timestamp createTime;

	private String lastUserName;

	protected Timestamp lastUpdate;
	
	private String keywords;
	
	/** 是否启用版本管理  */
	protected boolean versionEnabled;
	/** 版本注释 */
	protected String versionComment;
	
	private Integer frOrder;

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DocResource getDocResource() {
		return docResource;
	}

	public void setDocResource(DocResource docResource) {
		this.docResource = docResource;
		this.setFolderItemProperties(docResource);
	}

	/**
	 * 在构造时传入相关的DocResource对象
	 */
	public FolderItem(DocResource docResource) {
		this.docResource = docResource;
		this.setFolderItemProperties(docResource);
	}

	public FolderItem() {}

	public Long getDocResourceId() {
		return docResourceId;
	}

	public void setDocResourceId(Long docResourceId) {
		this.docResourceId = docResourceId;
	}

	private void setFolderItemProperties(DocResource dr) {
		this.setCreateTime(docResource.getCreateTime());
		this.setDesc(docResource.getFrDesc());
		this.setDocResourceId(docResource.getId());
		this.setLastUpdate(docResource.getLastUpdate());
		this.setName(docResource.getFrName());
		this.setKeywords(docResource.getKeyWords());
		this.setFrOrder(docResource.getFrOrder());
		this.setVersionEnabled(docResource.isVersionEnabled());
		this.setVersionComment(docResource.getVersionComment());
	}
	

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getLastUserName() {
		return lastUserName;
	}

	public void setLastUserName(String lastUserName) {
		this.lastUserName = lastUserName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public Integer getFrOrder() {
		return frOrder;
	}

	public void setFrOrder(Integer frOrder) {
		this.frOrder = frOrder;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	public boolean getVersionEnabled() {
		return versionEnabled;
	}

	public void setVersionEnabled(boolean versionEnabled) {
		this.versionEnabled = versionEnabled;
	}
}
