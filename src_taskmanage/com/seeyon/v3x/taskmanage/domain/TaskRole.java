package com.seeyon.v3x.taskmanage.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 任务涉及到的成员角色，包括：创建人、负责人、参与人、查看人
 * 还有一种打酱油的隐藏角色待君细细寻出...
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskRole extends BaseModel {
	
	/**
	 * 任务中的角色枚举，枚举值越小，权限越高
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-19
	 */
	public static enum RoleType {
		/**
		 * 任务创建者
		 */
		Creator(0),
		/**
		 * 任务负责人
		 */
		Manager(1),
		/**
		 * 任务参与者
		 */
		Participator(2);
		
		private int key;
		
		private RoleType(int key) {
			this.key = key;
		}
		
		public int key() {
			return this.key;
		}
		
		public static RoleType valueOf(int key) {
			RoleType[] types = RoleType.values();
			for(RoleType type : types) {
				if(type.key() == key) {
					return type;
				}
			}
			throw new IllegalArgumentException("未定义的角色类型[key=" + key + "]");
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9132254446547077263L;
	
	/**
	 * 角色对应人员ID
	 */
	private Long roleId;
	/**
	 * 角色类型：创建人、负责人、参与人
	 */
	private Integer roleType;
	/**
	 * 所在任务ID
	 */
	private Long taskId;
	
	public TaskRole() {}
	
	public TaskRole(Long roleId, Integer roleType, Long taskId) {
		super();
		this.setNewId();
		this.roleId = roleId;
		this.roleType = roleType;
		this.taskId = taskId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}
