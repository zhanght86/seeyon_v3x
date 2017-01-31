package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * The persistent class for the link_acl database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkSpace extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	//private Long id;
	private String spaceName;
	private String targetPageUrl;
	private int openType;
	private int sort;
	private long linkSystemId;
	private Set<LinkSpaceAcl> linkSpaceAcls = new HashSet<LinkSpaceAcl>();
    private String linkSpaceAclStrs;
	private List<V3xOrgEntity> entitys;

    public LinkSpace() {
    }

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
    
    public String getSpaceName() {
        return spaceName;
    }
    
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }
    
    public String getTargetPageUrl() {
        return targetPageUrl;
    }
    
    public void setTargetPageUrl(String targetPageUrl) {
        this.targetPageUrl = targetPageUrl;
    }
    
    public int getOpenType() {
        return openType;
    }
    
    public void setOpenType(int openType) {
        this.openType = openType;
    }
    
    public int getSort() {
        return sort;
    }
    
    public void setSort(int sort) {
        this.sort = sort;
    }
    
    public long getLinkSystemId() {
        return linkSystemId;
    }
    
    public void setLinkSystemId(long linkSystemId) {
        this.linkSystemId = linkSystemId;
    }
    
    public Set<LinkSpaceAcl> getLinkSpaceAcls() {
        return linkSpaceAcls;
    }
    
    public void setLinkSpaceAcls(Set<LinkSpaceAcl> linkSpaceAcls) {
        this.linkSpaceAcls = linkSpaceAcls;
    }
    
    public String getLinkSpaceAclStrs() {
        return linkSpaceAclStrs;
    }
    
    public void setLinkSpaceAclStrs(String linkSpaceAclStrs) {
        this.linkSpaceAclStrs = linkSpaceAclStrs;
    }
    
    public List<V3xOrgEntity> getEntitys() {
        return entitys;
    }
    
    public void setEntitys(List<V3xOrgEntity> entitys) {
        this.entitys = entitys;
    }
}