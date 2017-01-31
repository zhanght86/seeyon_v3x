package com.seeyon.v3x.secret.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.secret.domain.SecretAudit;

/**
 * @author Yang.Yinghai
 * @date 2012-8-31上午11:23:25
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class SecretAuditDao extends BaseHibernateDao<SecretAudit> {

    /**
     * 查询密级审核数据
     * @param orgMemberIds 操作用户ID串
     * @param orgAccountIds 单位ID串
     * @param orgDepartmentIds 部门ID串
     * @param beginDate 起始日期
     * @param endDate 结束日期
     * @param state 状态
     */
    @SuppressWarnings("unchecked")
    public List<SecretAudit> querySecretAudit(List<Long> orgMemberIds, List<Long> orgAccountIds, List<Long> orgDepartmentIds, String beginDate, String endDate, List<Integer> state) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(SecretAudit.class);
        // 人员ID
        if(orgMemberIds != null) {
            criteria.add(Expression.in("orgMemberId", orgMemberIds));
        }
        // 单位ID
        if(orgAccountIds != null) {
            criteria.add(Expression.in("orgAccountId", orgAccountIds));
        }
        // 部门ID
        if(orgDepartmentIds != null) {
            criteria.add(Expression.in("orgDepartmentId", orgDepartmentIds));
        }
        // 开始时间
        if(StringUtils.isNotBlank(beginDate)) {
            criteria.add(Expression.ge("auditTime", formatDateStartTime(beginDate)));
        }
        // 结束时间
        if(StringUtils.isNotBlank(endDate)) {
            criteria.add(Expression.le("auditTime", formatDateEndTime(endDate)));
        }
        // 审核状态
        criteria.add(Expression.in("state", state));
        // 排序
        criteria.addOrder(Order.desc("createTime"));
        return (ArrayList<SecretAudit>)super.executeCriteria(criteria);
    }

    /**
     * 查询当前人员是否已经有密级在审核中
     * @param orgMemberId 人员Id
     * @return true: 已经存在
     */
    public boolean hasWaitAudit(Long orgMemberId) {
        String hql = "from SecretAudit as audit where audit.state = 1 and audit.orgMemberId = ?";
        return super.find(hql, orgMemberId).size() > 0;
    }

    /**
     * 删除指定人员的待审核信息
     * @param orgMemberId 人员Id
     */
    public void deleteWaitAudit(Long orgMemberId) {
        String hql = "delete from SecretAudit as audit where audit.state = 1 and audit.orgMemberId = ?";
        super.bulkUpdate(hql, null, orgMemberId);
    }

    /**
     * 获取人员密级 变更之前的旧密级
     * @param orgMemberId 人员Id
     * @return 密级对象
     */
    public SecretAudit getWaitAuditByMemberId(Long orgMemberId) {
        String hql = "from SecretAudit as audit where audit.state = 1 and audit.orgMemberId = ? order by audit.auditTime desc";
        @SuppressWarnings("unchecked")
        List<SecretAudit> list = super.find(hql, orgMemberId);
        return list == null ? null : list.get(0);
    }

    /**
     * @param dateTime
     * @return
     * @throws Exception
     */
    private Date formatDateEndTime(String dateTime) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if(StringUtils.isNotBlank(dateTime)) {
            dateTime += " 23:59:59";
            date = sdf.parse(dateTime);
        }
        return date;
    }

    /**
     * @param dateTime
     * @return
     * @throws Exception
     */
    private Date formatDateStartTime(String dateTime) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if(StringUtils.isNotBlank(dateTime)) {
            dateTime += " 00:00:00";
            date = sdf.parse(dateTime);
        }
        return date;
    }
}
