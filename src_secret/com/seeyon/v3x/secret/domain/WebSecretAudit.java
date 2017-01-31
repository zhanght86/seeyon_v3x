package com.seeyon.v3x.secret.domain;

import java.util.Date;

/**
 * 用于页面展示
 * @author Yang.Yinghai
 * @date 2012-9-4下午03:11:21
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class WebSecretAudit {

    /** ID */
    private Long Id;

    /** 人员ID */
    private String user;

    /** 人员所属单位ID */
    private String account;

    /** 人员所属部门ID */
    private String depment;

    /** 密级（1：非密 2：秘密 3：机密 4：绝密） */
    private Integer secretLevel;

    /** 审核状态（1：审核中 2：审核通过 3：审核未通过） */
    private Integer state;

    /** 审核时间 */
    private Date auditTime;

    private SecretAudit audit;

    /**
     * 获取user
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置user
     * @param user user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取account
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置account
     * @param account account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取depment
     * @return depment
     */
    public String getDepment() {
        return depment;
    }

    /**
     * 设置depment
     * @param depment depment
     */
    public void setDepment(String depment) {
        this.depment = depment;
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

    /**
     * 获取audit
     * @return audit
     */
    public SecretAudit getAudit() {
        return audit;
    }

    /**
     * 设置audit
     * @param audit audit
     */
    public void setAudit(SecretAudit audit) {
        this.audit = audit;
    }

    /**
     * 获取id
     * @return id
     */
    public Long getId() {
        return Id;
    }

    /**
     * 设置id
     * @param id id
     */
    public void setId(Long id) {
        Id = id;
    }
}
