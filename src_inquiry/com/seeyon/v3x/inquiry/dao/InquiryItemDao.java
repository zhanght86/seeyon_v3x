/**
 * 
 */
package com.seeyon.v3x.inquiry.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;

/**
 * @author lin tian 2007-3-12
 */
public class InquiryItemDao extends BaseHibernateDao<InquirySubsurveyitem> {
    /**
     * 更新
     * @param id
     */
	public void update(Long id) {
		StringBuilder sb = new StringBuilder();
		sb.append("update " + InquirySubsurveyitem.class.getName()).append(
				//" a set voteCount=voteCount+1 where a.id = " + id);
				//HQL语句清理 modified by Meng Yang 2009-05-27
				" a set voteCount=voteCount+1 where a.id = ?" );
		//super.getHibernateTemplate().bulkUpdate(sb.toString());
		super.getHibernateTemplate().bulkUpdate(sb.toString(), id);
	}

	/**
	 * 根据当前用户的ID和调查项的ID查出用户所选择的项数
	 *第一个参数传当前用户的ID,第二个参数传调查项的ID
	 */
	
	public List<InquirySubsurveyitem> findByCurUser(Object... obj)
	{
		String hqlStr="select suitem from InquiryVotedefinite inqvote ," +
				"InquirySubsurveyitem suitem where inqvote.userId=? and inqvote.surveyitemId=suitem.id " +
				"and suitem.inquirySurveybasic.id=?";
		List<InquirySubsurveyitem> list = this.getHibernateTemplate().find(hqlStr, obj);
		return list;
	}
}
