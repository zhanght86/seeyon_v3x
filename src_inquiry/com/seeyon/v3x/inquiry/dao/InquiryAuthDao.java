/**
 * 
 */
package com.seeyon.v3x.inquiry.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquiryAuthority;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;

/**
 * @author lin tian 2007-3-8
 */
public class InquiryAuthDao extends BaseHibernateDao<InquiryAuthority> {
	/**
	 * 根据ID获取InquiryAuthority
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public InquiryAuthority getInquiryAuthority(long id) throws Exception {
		return super.get(id);
	}

	/**
	 * 获取当前用户在某调查类型下的发布权限
	 * 
	 * @param type
	 * @param memberid
	 * @param postid
	 * @param departmentid
	 * @param levelid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquiryAuthority> getInquiryAuthorityByUser(final long tid,
			final String authID) {
		return (List<InquiryAuthority>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From "
						+ InquiryAuthority.class.getName()
						+ " ia ,"
						+ InquirySurveytype.class.getName()
						+ " t "
						+ " Where (ia.inquirySurveytype.id=t.id) AND t.id=:tid  AND t.flag="
						+ InquirySurveytype.FLAG_NORMAL
						//+ "  AND ia.authId IN ("+authID+") ";
						//HQL语句清理 modified by Meng Yang 2009-05-27
						+ "  AND ia.authId IN (:authIds) ";
						
						String[] authIdStrs = authID.split(",");
						Long[] authIds = new Long[authIdStrs.length];
						for (int i = 0; i < authIds.length; i++) {
							authIds[i] = Long.parseLong(authIdStrs[i]);
						}
					
				//Query query = session.createQuery(hql).setLong("tid",tid);
						Query query = session.createQuery(hql).setLong("tid",tid).setParameterList("authIds", authIds);
						
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<InquiryAuthority> getAuthorityList(final long tid) {
		return (List<InquiryAuthority>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From "
					+ InquiryAuthority.class.getName()
					+ " ia "
					+ " Where ia.inquirySurveytype.id=:tid ";
			      Query query = session.createQuery(hql).setLong("tid",
					tid);
			      return query.list();
			}
		});
	}
	
	/**
	 * 判断有多少集团下管理/审核权限
	 * @return
	 */
	public int getCountOfGroupSpaceManage(){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveytypeextend.class.getName()
					+ " ist,"
					+ InquirySurveytype.class.getName()
					+ " it where ist.inquirySurveytype.id = it.id and ist.managerId=:managerId"
					+ " and it.spaceType = " + InquirySurveytype.Space_Type_Group;
				Query querycount = session.createQuery(countHql).setLong("managerId", CurrentUser.get().getId());
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断单位空间下管理/审核权限   用于判断是否显示公共信息管理中调查图标按钮
	 * @return
	 */
	public int getCountOfAccountSpaceManage(){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveytypeextend.class.getName()
					+ " ist,"
					+ InquirySurveytype.class.getName()
					+ " it where ist.inquirySurveytype.id = it.id and ist.managerId=:managerId"
					+ " and it.spaceType = " + InquirySurveytype.Space_Type_Account;
				Query querycount = session.createQuery(countHql).setLong("managerId", CurrentUser.get().getId());
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断单位空间下管理/审核权限   用于判断是否显示公共信息菜单   PublicInfoMenuCheckImpl调用
	 * @return
	 */
	public int getCountOfAccountSpaceManage(final Long memberId){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveytypeextend.class.getName()
					+ " ist,"
					+ InquirySurveytype.class.getName()
					+ " it where ist.inquirySurveytype.id = it.id and ist.managerId=:managerId"
					+ " and it.spaceType = " + InquirySurveytype.Space_Type_Account;
				Query querycount = session.createQuery(countHql).setLong("managerId", memberId);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断有审核权限的数目
	 * @return
	 */
	public int getCountOfCheckAuth(final boolean isGroup){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String groupQuery = "";
				if(isGroup){
					groupQuery = " and ( it.spaceType="+InquirySurveytype.Space_Type_Group+ " ) ";
				}else{
					//groupQuery = " and it.accountId="+CurrentUser.get().getLoginAccount();
					//HQL语句清理 modified by Meng Yang 2009-05-27
					groupQuery = " and it.accountId=:accountId";
				}
				String countHql = " Select count(*) From " 
					+ InquirySurveytypeextend.class.getName()
					+ " ist,"
					+ InquirySurveytype.class.getName()
					+ " it where ( ist.inquirySurveytype.id = it.id and it.flag="+InquirySurveytype.FLAG_NORMAL+" and ist.managerId=:managerId"
					+ " and ist.managerDesc="+InquirySurveytypeextend.MANAGER_CHECK
					+ " ) " + groupQuery;
				//Query querycount = session.createQuery(countHql).setLong("managerId", CurrentUser.get().getId());
				Query querycount = isGroup ? (session.createQuery(countHql).setLong("managerId", CurrentUser.get().getId())) 
										   : (session.createQuery(countHql).setLong("managerId", CurrentUser.get().getId()).setLong("accountId", CurrentUser.get().getAccountId()));				
				
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	
	/**
	 * 判断某板块下是否有调查   逻辑删除用标记 isb.flag=InquirySurveybasic.FLAG_NORMAL判断
	 * 
	 * 改成用CENSOR_PASS = 8;// 发布状态 其他状态下也可以删除板块
	 * @return
	 */
	public int getInquiryCountOfType(final Long typeId){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveybasic.class.getName()
					+ " isb where isb.surveyTypeId =:typeId and isb.censor="+InquirySurveybasic.CENSOR_PASS
					+ " and isb.flag=" + InquirySurveybasic.FLAG_NORMAL;
				Query querycount = session.createQuery(countHql).setLong("typeId", typeId);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断某板块下调查数
	 * @return
	 */
	public int getCountByType(final Long typeId,final String authIDs){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				String countHql = "SELECT Count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s" + " WHERE b.surveyTypeId =:typeId "
//				+ "  and (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
//				+ authIDs + ") AND  b.flag ="
				//HQL语句清理 modified by Meng Yang 2009-05-27
				+ "  and (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in (:authIds) And b.flag = "
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue();
				
				String[] authIdStrs = authIDs.split(",");
				Long[] authIds = new Long[authIdStrs.length];
				for (int i = 0; i < authIds.length; i++) {
					authIds[i] = Long.parseLong(authIdStrs[i]);
				}				
				
				//Query querycount = session.createQuery(countHql).setLong("typeId", typeId);
				Query querycount = session.createQuery(countHql).setLong("typeId", typeId).setParameterList("authIds", authIds);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断某板块下调查数      抽取已发布和已结束的
	 * @return
	 */
	public int getCountByType(final Long typeId){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveybasic.class.getName()
					+ " isb where isb.surveyTypeId =:typeId and ( isb.censor="+InquirySurveybasic.CENSOR_PASS+" or isb.censor="+InquirySurveybasic.CENSOR_CLOSE+" ) and isb.flag="+InquirySurveybasic.FLAG_NORMAL;
				Query querycount = session.createQuery(countHql).setLong("typeId", typeId);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	
	public int getInquiryNoCheckCountByMember(final Long memberId){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveybasic.class.getName()
					+ " isb, " 
					+ InquirySurveytypeextend.class.getName()
					+ " ist "
					+ " where isb.flag="+InquirySurveybasic.FLAG_NORMAL+" and isb.censor="+InquirySurveybasic.CENSOR_NO+" and isb.surveyTypeId = ist.inquirySurveytype.id and ist.managerId=:managerId"
					+ " and ist.managerDesc="+InquirySurveytypeextend.MANAGER_CHECK;
				Query querycount = session.createQuery(countHql).setLong("managerId", memberId);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
	
	/**
	 * 判断某板块下是否有未审核的调查
	 * @param typeId
	 * @return
	 */
	public int getInquiryNoCheckCountByType(final Long typeId){
		int count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String countHql = " Select count(*) From " 
					+ InquirySurveybasic.class.getName()
					+ " isb , InquirySurveytype t where isb.surveyTypeId = t.id and t.id =:typeId and isb.censor="+InquirySurveybasic.CENSOR_NO+" and isb.flag="+InquirySurveybasic.FLAG_NORMAL;
				Query querycount = session.createQuery(countHql).setLong("typeId", typeId);
			    return ((Integer)querycount.uniqueResult()).intValue();
			}
		});
		return count;
	}
}
