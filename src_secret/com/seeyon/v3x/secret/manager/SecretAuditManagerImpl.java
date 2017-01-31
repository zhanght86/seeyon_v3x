package com.seeyon.v3x.secret.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.secret.dao.SecretAuditDao;
import com.seeyon.v3x.secret.domain.SecretAudit;

/**
 * 密级审核管理器接口实现类
 * @author Yang.Yinghai
 * @date 2012-8-31上午11:45:14
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class SecretAuditManagerImpl implements SecretAuditManager {

    private SecretAuditDao secretAuditDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SecretAudit> querySecretAuditUnits(List<Long> orgMemberIds, List<Long> orgAccountIds, List<Long> orgDepartmentIds, String beginDate, String endDate, List<Integer> state) throws Exception {
        return secretAuditDao.querySecretAudit(orgMemberIds, orgAccountIds, orgDepartmentIds, beginDate, endDate, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasWaitAudit(Long orgMemberId) {
        return secretAuditDao.hasWaitAudit(orgMemberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWaitAudit(Long orgMemberId) {
        secretAuditDao.deleteWaitAudit(orgMemberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretAudit getWaitAuditByMemberId(Long orgMemberId) {
        return secretAuditDao.getWaitAuditByMemberId(orgMemberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(SecretAudit audit) {
        secretAuditDao.update(audit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(V3xOrgMember member) {
        SecretAudit audit = new SecretAudit();
        audit.setIdIfNew();
        audit.setOrgAccountId(member.getOrgAccountId());
        audit.setOrgDepartmentId(member.getOrgDepartmentId());
        audit.setOrgMemberId(member.getId());
        audit.setSecretLevel(member.getSecretLevel());
        audit.setState(SecretAudit.STATE_WAIT);
        audit.setCreateTime(new Date());
        secretAuditDao.save(audit);
    }

    /**
     * 设置secretAuditDao
     * @param secretAuditDao secretAuditDao
     */
    public void setSecretAuditDao(SecretAuditDao secretAuditDao) {
        this.secretAuditDao = secretAuditDao;
    }
}
