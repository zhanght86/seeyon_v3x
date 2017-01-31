package com.seeyon.v3x.collaboration.templete.domain;

import java.io.Serializable;

/**
 * The persistent class for the v3x_templete_manage database table.
 * 
 * @author BEA Workshop Studio
 */
public class TempleteConfig extends com.seeyon.v3x.common.domain.BaseModel
        implements Serializable
{

    private static final long serialVersionUID = 947773762808598274L;

    private Long memberId;

    private int type;

    private Long templeteId;

    private Long creatorId;

    private int sort;

    private String subject;

    private String templeteType;
    
    private Long categoryId;//模板分类id
    
    private Long accountId;

    private String isSystem;//是否是系统模板
    
    public String getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(String isSystem) {
		this.isSystem = isSystem;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public int getSort()
    {
        return sort;
    }

    public void setSort(int sort)
    {
        this.sort = sort;
    }

    public Long getTempleteId()
    {
        return templeteId;
    }

    public void setTempleteId(Long templeteId)
    {
        this.templeteId = templeteId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTempleteType()
    {
        return templeteType;
    }

    public void setTempleteType(String templeteType)
    {
        this.templeteType = templeteType;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

}