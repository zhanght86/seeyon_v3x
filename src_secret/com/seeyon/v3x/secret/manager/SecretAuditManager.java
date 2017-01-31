package com.seeyon.v3x.secret.manager;

import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.secret.domain.SecretAudit;

/**
 * 密级审核管理器接口
 * @author Yang.Yinghai
 * @date 2012-8-31上午11:44:27
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public interface SecretAuditManager {

    /**
     * 查询密级审核数据
     * @param orgMemberIds 操作用户ID串
     * @param orgAccountIds 单位ID串
     * @param orgDepartmentIds 部门ID串
     * @param beginDate 起始日期
     * @param endDate 结束日期
     * @param state 状态
     */
    public List<SecretAudit> querySecretAuditUnits(List<Long> orgMemberIds, List<Long> orgAccountIds, List<Long> orgDepartmentIds, String beginDate, String endDate, List<Integer> states) throws Exception;

    /**
     * 查询当前人员是否已经有密级在审核中
     * @param orgMemberId 人员Id
     * @return true: 已经存在
     */
    public boolean hasWaitAudit(Long orgMemberId);

    /**
     * 删除指定人员的待审核信息
     * @param orgMemberId 人员Id
     */
    public void deleteWaitAudit(Long orgMemberId);

    /**
     * 获取人员密级 变更之前的旧密级
     * @param orgMemberId 人员Id
     * @return 密级对象
     */
    public SecretAudit getWaitAuditByMemberId(Long orgMemberId);

    /**
     * 更新审核记录
     * @param audit 审核对象
     */
    public void update(SecretAudit audit);

    /**
     * 创建审核对象
     * @param member 人员对象
     * @param state 状态
     */
    public void create(V3xOrgMember member);
}
