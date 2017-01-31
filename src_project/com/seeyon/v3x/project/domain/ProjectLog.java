/**
 * 
 */
package com.seeyon.v3x.project.domain;

import java.io.Serializable;

/**
 * @author tian lin
 * 
 */
public class ProjectLog extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	
	private long userid;
	
	private java.sql.Timestamp optionDate;   
	
	private String projectDesc;//项目操作描述        增加：add  删除：delete 修改：update   增加项目进展：addEvolution  
	
	private String memberDesc = "";//成员操作描述   修改成员："123456,45678,98765 | 34567,56789,019223" '|'之前的为增加的 之后为删除的
	
	private String managerDesc = "";//负责人         增加：addManager  删除：deleteManager 
	
	private String chargeDesc = "";//领导            增加：addCharge  删除：deleteCharge 
	
	private String interfixDesc = "";//相关人        增加：addInterfix  删除：deleteInterfix 
	
	private String assistantDesc = "";//助理

	private long  projectId ;
	
	public String getAssistantDesc() {
		return assistantDesc;
	}

	public void setAssistantDesc(String assistantDesc) {
		this.assistantDesc = assistantDesc;
	}

	public String getChargeDesc() {
		return chargeDesc;
	}

	public void setChargeDesc(String chargeDesc) {
		this.chargeDesc = chargeDesc;
	}

	public String getInterfixDesc() {
		return interfixDesc;
	}

	public void setInterfixDesc(String interfixDesc) {
		this.interfixDesc = interfixDesc;
	}

	public String getManagerDesc() {
		return managerDesc;
	}

	public void setManagerDesc(String managerDesc) {
		this.managerDesc = managerDesc;
	}

	public String getMemberDesc() {
		return memberDesc;
	}

	public void setMemberDesc(String memberDesc) {
		this.memberDesc = memberDesc;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public java.sql.Timestamp getOptionDate() {
		return optionDate;
	}

	public void setOptionDate(java.sql.Timestamp optionDate) {
		this.optionDate = optionDate;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
}
