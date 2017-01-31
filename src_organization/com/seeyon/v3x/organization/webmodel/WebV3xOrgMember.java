package com.seeyon.v3x.organization.webmodel;
import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class WebV3xOrgMember
{
    private V3xOrgMember v3xOrgMember;
    private String departmentName;
    private String levelName;
    private Long levelId;
    private String postName;
    private Long postId;
    private String typeName;
    private String stateName;
    private String secondPosts;
    private String accountName;
    private String officeNum;
    private List workscope;
    /** 审核中的人员密级 */
    private Integer newSecretLevel;
    
  //branches_a8_v350_r_gov GOV-1277 杨帆 添加职级属性 start
    private String dutyLevelName;
    
    public String getDutyLevelName() {
		return dutyLevelName;
	}

	public void setDutyLevelName(String dutyLevelName) {
		this.dutyLevelName = dutyLevelName;
	}
	//branches_a8_v350_r_gov GOV-1277 杨帆 添加职级属性end

	public List getWorkscope() {
		return workscope;
	}

	public void setWorkscope(List workscope) {
		this.workscope = workscope;
	}

	public String getOfficeNum() {
		return officeNum;
	}

	public void setOfficeNum(String officeNum) {
		this.officeNum = officeNum;
	}

	public V3xOrgMember getV3xOrgMember()
    {
        return v3xOrgMember;
    }

    public void setV3xOrgMember(V3xOrgMember orgMember)
    {
        v3xOrgMember = orgMember;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    public String getSecondPosts()
    {
        return secondPosts;
    }

    public void setSecondPosts(String secondPosts)
    {
        this.secondPosts = secondPosts;
    }

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getLevelId() {
		return levelId;
	}

	public void setLevelId(Long levelId) {
		this.levelId = levelId;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

    
    /**
     * 获取newSecretLevel
     * @return newSecretLevel
     */
    public Integer getNewSecretLevel() {
        return newSecretLevel;
    }

    
    /**
     * 设置newSecretLevel
     * @param newSecretLevel newSecretLevel
     */
    public void setNewSecretLevel(Integer newSecretLevel) {
        this.newSecretLevel = newSecretLevel;
    }
    
    
}
