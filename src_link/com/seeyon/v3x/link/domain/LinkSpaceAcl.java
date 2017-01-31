package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the link_acl database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkSpaceAcl extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	//private Long id;
	private long userId;
	private String userType;
	private long linkSpaceId;

    public LinkSpaceAcl() {
    }

	public long getUserId() {
		return this.userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}


	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
    
    public long getLinkSpaceId() {
        return linkSpaceId;
    }
    
    public void setLinkSpaceId(long linkSpaceId) {
        this.linkSpaceId = linkSpaceId;
    }
}