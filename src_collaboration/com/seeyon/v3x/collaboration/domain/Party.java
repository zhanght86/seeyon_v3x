package com.seeyon.v3x.collaboration.domain;

import net.joinwork.bpm.definition.BPMSeeyonPolicy;

/**
 * User: lius Date: 2007-2-5 Time: 10:10:19
 */
public class Party {
	public String type;

	public String id;

	public String name;

	public String policy;
	
	public String accountId;
	
	public String accountShortName;
	
	BPMSeeyonPolicy seeyonPolicy =null;
	
	private String activityId = null;

	private boolean includeChild=true;
	
	public boolean isIncludeChild() {
		return includeChild;
	}

	public void setIncludeChild(boolean includeChild) {
		this.includeChild = includeChild;
	}

	public BPMSeeyonPolicy getSeeyonPolicy() {
		return seeyonPolicy;
	}

	public void setSeeyonPolicy(BPMSeeyonPolicy seeyonPolicy) {
		this.seeyonPolicy = seeyonPolicy;
	}

	public Party() {
	}

	public Party(String type, String id, String name, String accountId, String accountShortName) {
		this(type, id, name, accountId, accountShortName, null, null);
	}

	public Party(String type, String id, String name,String accountId, String accountShortName, String policy) {
		this(type, id, name, accountId, accountShortName, policy, null);
	}
	
	public Party(String type, String id, String name,String accountId, String accountShortName, String policy, String activityId) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.accountId = accountId;
		this.accountShortName = accountShortName;
		this.policy = policy;
		this.activityId = activityId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountShortName() {
		return accountShortName;
	}

	public void setAccountShortName(String accountShortName) {
		this.accountShortName = accountShortName;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((activityId == null) ? 0 : activityId.hashCode());
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Party other = (Party) obj;
		if (activityId == null) {
			if (other.activityId != null) return false;
		}
		else if (!activityId.equals(other.activityId)) return false;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (type == null) {
			if (other.type != null) return false;
		}
		else if (!type.equals(other.type)) return false;
		return true;
	}
	
	
}
