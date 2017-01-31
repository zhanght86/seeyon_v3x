package com.seeyon.v3x.secret.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 密级审核实体对象
 * @author Yang.Yinghai
 * @date 2012-8-31上午11:05:51
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class SecretAudit extends BaseModel implements Serializable {

    /** 序列号 */
    private static final long serialVersionUID = 1L;

    /** 审核状态1：待审核 */
    public static final int STATE_WAIT = 1;

    /** 审核状态2：审核通过 */
    public static final int STATE_PASS = 2;

    /** 审核状态3：审核未通过 */
    public static final int STATE_NOTPASS = 3;

    /** 秘密级别1：非密 */
    public static final int SECRET_LEVEL_NORMAL = 1;

    /** 秘密级别1：秘密 */
    public static final int SECRET_LEVEL_SECRET = 2;

    /** 秘密级别1：机密 */
    public static final int SECRET_LEVEL_HIGHLYSECRET = 3;

    /** 秘密级别1：绝密 */
    public static final int SECRET_LEVEL_TOPSECRET = 4;

    /** 人员ID */
    private Long orgMemberId;

    /** 人员所属单位ID */
    private Long orgAccountId;

    /** 人员所属部门ID */
    private Long orgDepartmentId;

    /** 密级（1：非密 2：秘密 3：机密 4：绝密） */
    private Integer secretLevel;

    /** 审核状态（1：审核中 2：审核通过 3：审核未通过） */
    private Integer state;

    /** 审核时间 */
    private Date auditTime;
    
    /** 创建时间 */
    private Date createTime;

    /**
     * 获取createDate
     * @return createDate
     */
    public Date getCreateTime() {
		return createTime;
	}
    /**
     * 设置createDate
     * @param createDate
     */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
     * 获取orgMemberId
     * @return orgMemberId
     */
    public Long getOrgMemberId() {
        return orgMemberId;
    }

    /**
     * 设置orgMemberId
     * @param orgMemberId orgMemberId
     */
    public void setOrgMemberId(Long orgMemberId) {
        this.orgMemberId = orgMemberId;
    }

    /**
     * 获取orgAccountId
     * @return orgAccountId
     */
    public Long getOrgAccountId() {
        return orgAccountId;
    }

    /**
     * 设置orgAccountId
     * @param orgAccountId orgAccountId
     */
    public void setOrgAccountId(Long orgAccountId) {
        this.orgAccountId = orgAccountId;
    }

    /**
     * 获取orgDepartmentId
     * @return orgDepartmentId
     */
    public Long getOrgDepartmentId() {
        return orgDepartmentId;
    }

    /**
     * 设置orgDepartmentId
     * @param orgDepartmentId orgDepartmentId
     */
    public void setOrgDepartmentId(Long orgDepartmentId) {
        this.orgDepartmentId = orgDepartmentId;
    }

    /**
     * 获取secretLevel
     * @return secretLevel
     */
    public Integer getSecretLevel() {
        return secretLevel;
    }

    /**
     * 设置secretLevel
     * @param secretLevel secretLevel
     */
    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    /**
     * 获取state
     * @return state
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置state
     * @param state state
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取auditTime
     * @return auditTime
     */
    public Date getAuditTime() {
        return auditTime;
    }

    /**
     * 设置auditTime
     * @param auditTime auditTime
     */
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
