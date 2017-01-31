package com.seeyon.v3x.mobile.webmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 结点细节
 * 
 */
public class Nodes {
	private String nid;//Nodes的Id
	
	private String type;//com.seeyon.v3x.organization.domain.V3xOrgEntity
	
	private String roleName;

	private String nodename;// 用户的显示名字

	private Nodes parent;

	private List<Nodes> children = new ArrayList<Nodes>();
	
	private String permission; // 结点的权限 (专用一个国际化的Key)

	private String state; // 结点的状态
	
	private String isDelete;// 是否需要被删除
	
	private Long uid;

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public List<Nodes> getChildren() {
		return children;
	}

	public void setChildren(List<Nodes> children) {
		this.children = children;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addChild(Nodes n){
		children.add(n);
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public Nodes getParent() {
		return parent;
	}

	public void setParent(Nodes parent) {
		this.parent = parent;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public int getChildrennum(){
		int i=0;
		for(Nodes n :this.children){
			if(n.getIsDelete().equals("false")){
				i = i + 1;
			}
		}
		return i;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}
	
	public String toString(){		
		return nid + "\t" + nodename + "(" + permission + ")\n" + children;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	

}
