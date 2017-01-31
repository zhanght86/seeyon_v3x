package com.seeyon.v3x.space.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the v3x_space_sort database table.
 * 
 * @author BEA Workshop Studio
 */
public class SpaceSort extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private Long memberId;
	private Long accountId;
	private Integer type;
    private Integer sort;
	private String spacePath;
	private Boolean isDeleted;//是否可见

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public SpaceSort(){
		
	}
	
	public SpaceSort(Long memberId, Long accountId, String spacePath, Integer type, Integer sort) {
        this.memberId = memberId;
        this.spacePath = spacePath;
        this.sort = sort;
        this.type = type;
        this.accountId = accountId;
    }

	public Long getMemberId() {
		return this.memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getSort() {
		return this.sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getSpacePath() {
		return this.spacePath;
	}
	public void setSpacePath(String spacePath) {
		this.spacePath = spacePath;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}