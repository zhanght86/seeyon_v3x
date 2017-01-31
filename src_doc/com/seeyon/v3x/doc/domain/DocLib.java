package com.seeyon.v3x.doc.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.doc.util.Constants;

/**
 * 文档库
 */
public class DocLib extends BaseModel implements Serializable, Comparable<DocLib> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8760917014724014258L;
	
	// 是否可以编辑显示栏目
	private boolean columnEditable;			//y
	/** 是否可以编辑查询属性  */
	private boolean searchConditionEditable;
	private java.sql.Timestamp createTime;
	private Long createUserId;
	private String description;
	// 是否可以创建子文档夹
	private boolean folderEnabled;				//y
	// 是否在文档树上隐藏，暂未使用
	private boolean isHidden;
	// 最近的修改时间
	private java.sql.Timestamp lastUpdate;
	private Long lastUserId;
	// 名称
	private String name;
	// 状态
	private byte status = Constants.DOC_LIB_ENABLED;
	// 类型 Constants.xxxLibType
	private byte type;
	// 是否可以编辑内容类型
	private boolean typeEditable = false;
	// 是否采用系统默认显示栏目
	private boolean isDefault;//是否为系统默认显示栏目
	// 是否采用系统默认显示的查询条件设置
	private boolean isSearchConditionDefault;
	// 在同一个单位下的排序号
	private int orderNum;
	private long domainId; // 文档库所属单位id
	
	// 2007.6.5 添加
	// 是否以默认排序显示列表
	private boolean listByDefaultOrder = true;   //y
	// 是否允许创建office文档
	private boolean officeEnabled;		  //y
	// 是否允许创建A6文档
	private boolean a6Enabled;			  //y
	// 是否允许上传文件
	private boolean uploadEnabled;
	// 是否对查看、下载操作记录日志
	private boolean logView;			  //y
	//是否记录打印日志
	private Boolean printLog;
	//是否记录下载日志
	private Boolean downloadLog;
	
	/**
	 * 文档库是否处于启用状态
	 * @return
	 */
	public boolean isEnabled() {
		return this.status == Constants.DOC_LIB_ENABLED;
	}
	
	/**
	 * 文档库是否处于停用状态
	 * @return
	 */
	public boolean isDisabled() {
		return this.status == Constants.DOC_LIB_DISABLED;
	}
	
	/**
	 * 文档库是否"我的文档"
	 * @return	是否私人文档库
	 */
	public boolean isPersonalLib() {
		return this.getType() == Constants.PERSONAL_LIB_TYPE.byteValue();
	}
	
	/**
	 * 文档库是否"集团文档"
	 * @return	是否集团文档库
	 */
	public boolean isGroupLib() {
		return this.getType() == Constants.GROUP_LIB_TYPE.byteValue();
	}
	
	/**
	 * 文档库是否"项目文档"
	 * @return	是否项目文档库
	 */
	public boolean isProjectLib() {
		return this.getType() == Constants.PROJECT_LIB_TYPE.byteValue();
	}
	
	/**
	 * 文档库是否"用户自定义文档"
	 * @return	是否自定义文档库
	 */
	public boolean isUserCustomizedLib() {
		return this.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue();
	}
	
	/**
	 * 文档库是否"单位文档"
	 * @return	是否单位文档库
	 */
	public boolean isAccountLib() {
		return this.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue();
	}
	
	/**
	 * 文档库是否"公文档案"
	 * @return	是否公文档案库
	 */
	public boolean isEdocLib() {
		return this.getType() == Constants.EDOC_LIB_TYPE.byteValue();
	}
	
	public boolean getA6Enabled() {
		return a6Enabled;
	}

	public void setA6Enabled(boolean enabled) {
		a6Enabled = enabled;
	}

	public boolean getListByDefaultOrder() {
		return listByDefaultOrder;
	}

	public void setListByDefaultOrder(boolean listByDefaultOrder) {
		this.listByDefaultOrder = listByDefaultOrder;
	}
	
	public boolean getUploadEnabled() {
		return uploadEnabled;
	}
	
	public void setUploadEnabled(boolean uploadEnabled) {
		this.uploadEnabled = uploadEnabled;
	}

	public boolean getLogView() {
		return logView;
	}

	public void setLogView(boolean logView) {
		this.logView = logView;
	}

	public boolean getOfficeEnabled() {
		return officeEnabled;
	}

	public void setOfficeEnabled(boolean officeEnabled) {
		this.officeEnabled = officeEnabled;
	}

	public boolean getColumnEditable() {
		return this.columnEditable;
	}
	public void setColumnEditable(boolean columnEditable) {
		this.columnEditable = columnEditable;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUserId() {
		return this.createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getFolderEnabled() {
		return this.folderEnabled;
	}
	public void setFolderEnabled(boolean folderEnabled) {
		this.folderEnabled = folderEnabled;
	}

	public boolean getIsHidden() {
		return this.isHidden;
	}
	public void setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public java.sql.Timestamp getLastUpdate() {
		return this.lastUpdate;
	}
	public void setLastUpdate(java.sql.Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getLastUserId() {
		return this.lastUserId;
	}
	public void setLastUserId(Long lastUserId) {
		this.lastUserId = lastUserId;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public byte getStatus() {
		return this.status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getType() {
		return this.type;
	}
	public void setType(byte type) {
		this.type = type;
	}

	public boolean getTypeEditable() {
		return this.typeEditable;
	}
	public void setTypeEditable(boolean typeEditable) {
		this.typeEditable = typeEditable;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	
	public long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public int compareTo(DocLib o) {
		return (this.orderNum - o.orderNum);
	}

	public Boolean getDownloadLog() {
		return downloadLog;
	}

	public void setDownloadLog(Boolean downloadLog) {
		this.downloadLog = downloadLog;
	}

	public Boolean getPrintLog() {
		return printLog;
	}

	public void setPrintLog(Boolean printLog) {
		this.printLog = printLog;
	}
	
	public boolean isSearchConditionEditable() {
		return searchConditionEditable;
	}
	
	public void setSearchConditionEditable(boolean searchConditionEditable) {
		this.searchConditionEditable = searchConditionEditable;
	}
	public boolean getIsSearchConditionDefault() {
		return isSearchConditionDefault;
	}

	public void setIsSearchConditionDefault(boolean isSearchConditionDefault) {
		this.isSearchConditionDefault = isSearchConditionDefault;
	}
	
}