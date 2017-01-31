package com.seeyon.v3x.bulletin.util.hql;

import java.util.List;

import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.bulletin.util.Constants.VisitRole;

/**
 * 查询公告列表的影响元素：用户信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-7-30
 */
public class UserInfo {

	/** 访问角色：用户、发起人、审核员或管理员  */
	private Constants.VisitRole role;

	/** 用户ID */
	private Long userId;

	/** 用户对应各种组织模型实体ID集合  */
	private List<Long> domainIds;

	/** 以用户角色查看公告时，该用户是否该板块管理员 */
	private boolean adminAsUser;
	
	public UserInfo(VisitRole role) {
		super();
		this.role = role;
	}

	public UserInfo(VisitRole role, Long userId) {
		this(role);
		this.userId = userId;
	}

	public UserInfo(VisitRole role, Long userId, List<Long> domainIds, boolean adminAsUser) {
		this(role, userId);
		this.domainIds = domainIds;
		this.adminAsUser = adminAsUser;
	}

	public UserInfo() {}
	
	public Constants.VisitRole getRole() {
		return role;
	}

	public void setRole(Constants.VisitRole role) {
		this.role = role;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Long> getDomainIds() {
		return domainIds;
	}

	public void setDomainIds(List<Long> domainIds) {
		this.domainIds = domainIds;
	}

	public boolean isAdminAsUser() {
		return adminAsUser;
	}

	public void setAdminAsUser(boolean adminAsUser) {
		this.adminAsUser = adminAsUser;
	}
	
}
