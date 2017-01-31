package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
/**
 * The persistent class for the link_system database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkSystem extends BaseModel implements Serializable, Comparable<LinkSystem> {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private java.sql.Timestamp createTime;
	private Long createUserId;
	private String description;
	private String image;
	private byte isSystem;
	private java.sql.Timestamp lastUpdate;
	private Long lastUserId;
	private long linkCategoryId;
	private String method;
	private String name;
	private int orderNum;
	private byte status;
	private String url;
	private boolean sameRegion;
	private String agentUrl;
	private boolean needContentCheck;
	private String contentForCheck;
	private Set<LinkAcl> linkAcl;
	private Set<LinkSpace> linkSpaces = new HashSet<LinkSpace>(0);
	/** 
	 * 是否允许配置成空间导航
	 */
	private boolean allowedAsSpace;
	/** 
     * 是否允许配置到栏目
     */
    private boolean allowedAsSection;
	/**
	 * 打开方式
	 */
	private int openType;
	
	/** 无打开方式设定 */
	public static final int OPENTYPE_NONE = 0;
	/** 打开方式：新页面 */
	public static final int OPENTYPE_OPEN = 1;
	/** 打开方式：工作区域 */
	public static final int OPENTYPE_WORKSPACE = 2;
	
	
	private Set<LinkOption> linkOption;

    public Set<LinkOption> getLinkOption() {
		return linkOption;
	}

	public void setLinkOption(Set<LinkOption> linkOption) {
		this.linkOption = linkOption;
	}

	public LinkSystem() {
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

	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public byte getIsSystem() {
		return this.isSystem;
	}
	public void setIsSystem(byte isSystem) {
		this.isSystem = isSystem;
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

	public long getLinkCategoryId() {
		return this.linkCategoryId;
	}
	public void setLinkCategoryId(long linkCategoryId) {
		this.linkCategoryId = linkCategoryId;
	}

	public String getMethod() {
		return this.method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public byte getStatus() {
		return this.status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}

	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public Set<LinkAcl> getLinkAcl() {
		return linkAcl;
	}

	public void setLinkAcl(Set<LinkAcl> linkAcl) {
		this.linkAcl = linkAcl;
	}

	public int compareTo(LinkSystem o) {
		if(o.orderNum >= this.orderNum)
			return -1;
		return 1;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public boolean isAllowedAsSpace() {
		return allowedAsSpace;
	}

	public void setAllowedAsSpace(boolean allowedAsSpace) {
		this.allowedAsSpace = allowedAsSpace;
	}
    
    public boolean isSameRegion() {
        return sameRegion;
    }
    
    public void setSameRegion(boolean sameRegion) {
        this.sameRegion = sameRegion;
    }
    
    public String getAgentUrl() {
        return agentUrl;
    }
    
    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }
    
    public boolean isNeedContentCheck() {
        return needContentCheck;
    }
    
    public void setNeedContentCheck(boolean needContentCheck) {
        this.needContentCheck = needContentCheck;
    }
    
    public String getContentForCheck() {
        return contentForCheck;
    }
    
    public void setContentForCheck(String contentForCheck) {
        this.contentForCheck = contentForCheck;
    }
    
    public boolean isAllowedAsSection() {
        return allowedAsSection;
    }
    
    public void setAllowedAsSection(boolean allowedAsSection) {
        this.allowedAsSection = allowedAsSection;
    }
    
    public Set<LinkSpace> getLinkSpaces() {
        return linkSpaces;
    }
    
    public void setLinkSpaces(Set<LinkSpace> linkSpaces) {
        this.linkSpaces = linkSpaces;
    }
}