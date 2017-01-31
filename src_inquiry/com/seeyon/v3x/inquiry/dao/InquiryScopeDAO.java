package com.seeyon.v3x.inquiry.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.inquiry.domain.InquiryScope;

public class InquiryScopeDAO extends BaseHibernateDao<InquiryScope>
{
	/**
	 * 根据调查类型的ID获取获取发布范围的类型,如果查出来的结果是一条那么它就是
	 * 部门Department,单位调查Account,如果是多个那么调查的类型就是Member
	 * @param id
	 * @return
	 */
	public List<InquiryScope> getInquiryScopeListDAO(long id)
	{
		String hqlStr = "from InquiryScope as insco where insco.inquirySurveybasic.id=?";
		Object[] params = { id };
		List<InquiryScope> list = this.getHibernateTemplate().find(hqlStr,params);
		return list;
	}
}
