package com.seeyon.v3x.organization.webmodel;

import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;

public class WebV3xOrgPost implements Comparable<WebV3xOrgPost> {
	
	private V3xOrgPost v3xOrgPost;
	private List<V3xOrgMember> members;
	private String postType;
	
	public List<V3xOrgMember> getMembers() {
		return members;
	}
	public void setMembers(List<V3xOrgMember> members) {
		this.members = members;
	}
	public V3xOrgPost getV3xOrgPost() {
		return v3xOrgPost;
	}
	public void setV3xOrgPost(V3xOrgPost orgPost) {
		v3xOrgPost = orgPost;
	}
	public int compareTo(WebV3xOrgPost o) {
		return (this.v3xOrgPost.getSortId() - o.v3xOrgPost.getSortId());
	}
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}

}
