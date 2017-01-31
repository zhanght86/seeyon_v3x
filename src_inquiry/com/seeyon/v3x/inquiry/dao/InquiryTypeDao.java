/**
 * 
 */
package com.seeyon.v3x.inquiry.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.SQLWildcardUtil;

/**
 * @author lin tian
 * 
 * 2007-3-2
 */
public class InquiryTypeDao extends BaseHibernateDao<InquirySurveytype> {

	private static final Log log = LogFactory.getLog(InquiryTypeDao.class);
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	/**
	 * 获取调查类型列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeList() throws Exception {
		//增加单位的过滤7-19------xut
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		User user = CurrentUser.get();
		Long accountId = user.getAccountId();
		V3xOrgAccount account = new V3xOrgAccount();
		
		
		try {
			account = orgManager.getAccountById(accountId);
		} catch (BusinessException e) {
			log.error("获取单位失败", e);
		}
		
		if(account.getIsRoot()){
			
			accountId = 0L;
			
		}
		
		String count = "Select count(ins) From "
				+ InquirySurveytype.class.getName() + " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()+" and ins.accountId=:accountId";
		
		String hql = "From " + InquirySurveytype.class.getName()
		+ " AS ins Where ins.flag="
		+ InquirySurveytype.FLAG_NORMAL.intValue()+" and ins.accountId=:accountId order by ins.sort";// 查询正常状态下的调查类型列表
		
		
		Query queryCount = session.createQuery(count).setLong("accountId", accountId);
		int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
		Pagination.setRowCount(typeCount);
		Query query = session.createQuery(hql).setLong("accountId", accountId).setFirstResult(
				Pagination.getFirstResult()).setMaxResults(
				Pagination.getMaxResults());
		return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getTypeList() throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
			
				User user = CurrentUser.get();
				Long accountId = user.getAccountId();
				V3xOrgAccount account = new V3xOrgAccount();
				try {
					account = orgManager.getAccountById(accountId);
				} catch (BusinessException e) {
					log.error("获取单位失败", e);
				}
				
				if(account.getIsRoot()){
					accountId = 0L;
				}
				
				String hql = "From " + InquirySurveytype.class.getName()
				+ " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " and ins.accountId=:accountId order by ins.sort"; // 查询正常状态下的调查类型列表
				Query query = session.createQuery(hql).setLong("accountId", accountId);
				return query.list();
			}
		});
	}


	/**
	 * 获取已创建调查名称列表
	 * @deprecated 未区分单位
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTypeNameList( final boolean isGroup) throws Exception {
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String filter = "";
		if(isGroup){
			filter = " and ins.spaceType=1";
		}else{
			filter = " and ins.spaceType=2";
		}
		String hql = "SELECT (ins.typeName) From " + InquirySurveytype.class.getName()
				+ " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue() + filter;// 查询正常状态下的调查类型列表
		Query query = session.createQuery(hql);
		return query.list();
			}
		});
	}

	/**
	 * 按版块名称查询调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeListByTitle( final String title)
			throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String count = "Select count(ins) From "
				+ InquirySurveytype.class.getName() + " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.typeName like :typeName";
		Query queryCount = session.createQuery(count).setString(
				"typeName", "%" + SQLWildcardUtil.escape(title) + "%");
		int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
		Pagination.setRowCount(typeCount);
		String hql = "From " + InquirySurveytype.class.getName()
				+ " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.typeName like :typeName";// 查询正常状态下的调查类型列表
		Query query = session.createQuery(hql).setString("typeName",
				"%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 查询需要审核调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeListByONPass( final String title)
			throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String count = "Select count(ins) From "
				+ InquirySurveytype.class.getName() + " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.censorDesc="
				+ InquirySurveytype.CENSOR_NO_PASS.intValue()
				+ " AND ins.typeName like :typeName";
		Query queryCount = session.createQuery(count).setString(
				"typeName", "%" + SQLWildcardUtil.escape(title) + "%");
		int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
		Pagination.setRowCount(typeCount);
		String hql = "From " + InquirySurveytype.class.getName()
				+ " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.censorDesc="
				+ InquirySurveytype.CENSOR_NO_PASS.intValue()
				+ " AND ins.typeName like :typeName";
		Query query = session.createQuery(hql).setString("typeName",
				"%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 查询不需要审核调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeListByPass( final String title)
			throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String count = "Select count(ins) From "
				+ InquirySurveytype.class.getName() + " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.censorDesc="
				+ InquirySurveytype.CENSOR_PASS.intValue()
				+ " AND ins.typeName like :typeName";
		Query queryCount = session.createQuery(count).setString(
				"typeName", "%" + SQLWildcardUtil.escape(title) + "%");
		int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
		Pagination.setRowCount(typeCount);
		String hql = "From " + InquirySurveytype.class.getName()
				+ " AS ins Where ins.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ins.censorDesc="
				+ InquirySurveytype.CENSOR_PASS.intValue()
				+ " AND ins.typeName like :typeName";
		Query query = session.createQuery(hql).setString("typeName",
				"%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 删除InquirySurveytype
	 * 
	 * @param id
	 */
	public void updateInquirySurveytype(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("flag", InquirySurveytype.FLAG_DELETE);
		super.update(id, map);
	}

	/**
	 * 根据ID获取InquirySurveytype
	 * 
	 * @param id
	 * @return
	 */
	public InquirySurveytype getInquirySurveytypeBYID( final long id) throws Exception {
		return (InquirySurveytype) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "From " + InquirySurveytype.class.getName()
					+ " AS inq Where inq.id=:id And inq.flag ="
					+ InquirySurveytype.FLAG_NORMAL.intValue();
		Query query = session.createQuery(hql).setLong("id", id);
		return (InquirySurveytype) query.uniqueResult();
			}
		});
	}
	
	public InquirySurveytype getInquirySurveytypeByIdNoFlag( final long id) throws Exception {
		return (InquirySurveytype) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "From " + InquirySurveytype.class.getName()
				+ " AS inq Where inq.id=:id";
		Query query = session.createQuery(hql).setLong("id", id);
		return (InquirySurveytype) query.uniqueResult();
	}
		});
	}

	/**
	 * 获取当前用户有权发布的调查类型列表
	 * 用户必须是管理员
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeListByUserAuth( final String authID, final long memberid)
			throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql = "SELECT DISTINCT t From InquirySurveytype t left outer join t.inquiryAuthorities  a  "
			+ " left outer join t.inquirySurveytypeextends  e"
			+ "  Where t.flag=:tFlag and t.accountId!=0 and ( e.managerId =:managerId and e.managerDesc=:mDesc or a.authId IN "
			+ " (:authIds) )";
		params.put("authIds", CommonTools.parseStr2Ids(authID));
		params.put("tFlag", InquirySurveytype.FLAG_NORMAL);
		params.put("managerId", memberid);
		//当前用户必须是管事员
		params.put("mDesc", InquirySurveytypeextend.MANAGER_SYSTEM);
		List<Object> paramsFlag=null;
		List<InquirySurveytype> list=this.find(hql, params, paramsFlag);
		return list;
	}
	/**
	 * 自定义空间获取当前用户有权发布的调查类型列表
	 * 用户必须是管理员
	 * @param spaceId
	 * @param authID
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getCustomInquiryTypeByUserAuth(long spaceId, final String authID, final long memberid)
			throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql = "SELECT DISTINCT t From InquirySurveytype t left outer join t.inquiryAuthorities  a  "
			+ " left outer join t.inquirySurveytypeextends  e"
			+ "  Where t.flag=:tFlag and t.accountId =:spaceId and ( e.managerId =:managerId and e.managerDesc=:mDesc or a.authId IN "
			+ " (:authIds) )";
		params.put("authIds", CommonTools.parseStr2Ids(authID));
		params.put("tFlag", InquirySurveytype.FLAG_NORMAL);
		params.put("managerId", memberid);
		params.put("spaceId", spaceId);
		//当前用户必须是管事员
		params.put("mDesc", InquirySurveytypeextend.MANAGER_SYSTEM);
		List<Object> paramsFlag=null;
		List<InquirySurveytype> list=this.find(hql, params, paramsFlag);
		return list;
	}
	/**
	 * 自定义空间获取所有正常状态的调查类型列表
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getAllCustomInquiryType(long spaceId) throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql = "SELECT DISTINCT t From InquirySurveytype t " + "Where t.flag=:tFlag and t.accountId =:spaceId";
		params.put("tFlag", InquirySurveytype.FLAG_NORMAL);
		params.put("spaceId", spaceId);
		List<InquirySurveytype> list = this.find(hql, -1, -1, params);
		return list;
	}
	
	/**
	 * 获取当前用户有权发布的集团调查类型列表
	 * 
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getGroupInquiryTypeListByUserAuth( final String authID, final long memberid) throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		String hql = "SELECT DISTINCT t From InquirySurveytype t left outer join t.inquiryAuthorities  a  "
			+ " left outer join t.inquirySurveytypeextends  e"
			+ "  Where (a.authId IN (:authIds) OR  ( e.managerId =:managerId and e.managerDesc=:mDesc )) and t.flag=:tFlag and t.spaceType=:tsType";//过滤掉集团空间的调查
		params.put("authIds", CommonTools.parseStr2Ids(authID));
		params.put("tFlag", InquirySurveytype.FLAG_NORMAL);
		params.put("tsType", InquirySurveytype.Space_Type_Group);
		params.put("managerId", memberid);
		//当前用户必须是管事员
		params.put("mDesc", InquirySurveytypeextend.MANAGER_SYSTEM);
		List<Object> paramsFlag=null;
		List<InquirySurveytype> list=this.find(hql, params, paramsFlag);
		return list;
	}
	/**
	 * 获取当前用户有管理权限的调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getAuthoritiesTypeList( final long memberID) throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				String count = "SELECT count(t.id) From "
				+ InquirySurveytype.class.getName() + " t,"
				+ InquirySurveytypeextend.class.getName() +" e" 
				+ " Where  e.inquirySurveytype.id = t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL +" AND e.managerId =:managerId AND e.managerDesc = " +InquirySurveytypeextend.MANAGER_SYSTEM.intValue() + " and t.accountId=:accountId" ;
				Query queryCount = session.createQuery(count).setLong("managerId", memberID).setLong("accountId", CurrentUser.get().getLoginAccount());
				int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
				
				Pagination.setRowCount(typeCount);	
				
				
				String hql ="SELECT DISTINCT " + " t From "
				+ InquirySurveytype.class.getName() + " t,"
				+ InquirySurveytypeextend.class.getName() +" e" 
				+ " Where  e.inquirySurveytype.id = t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL +" AND e.managerId =:managerId AND e.managerDesc = " +InquirySurveytypeextend.MANAGER_SYSTEM.intValue() + " and t.accountId=：accountId";
				
				Query query = session.createQuery(hql).setLong("managerId", memberID).setLong("accountId", CurrentUser.get().getLoginAccount()).setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}
	
	/**
	 * 获取当前用户有管理权限的集团调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getAuthoritiesGroupTypeList( final long memberID) throws Exception {
		return (List<InquirySurveytype>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {		
		
				String count = "SELECT count(t.id) From "
				+ InquirySurveytype.class.getName() + " t,"
				+ InquirySurveytypeextend.class.getName() +" e" 
				+ " Where  e.inquirySurveytype.id = t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL +" AND e.managerId =:managerId AND e.managerDesc = " +InquirySurveytypeextend.MANAGER_SYSTEM.intValue() + " and t.spaceType="+InquirySurveytype.Space_Type_Group;
				Query queryCount = session.createQuery(count).setLong("managerId", memberID);
				int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
				
				Pagination.setRowCount(typeCount);
				
				String hql ="SELECT DISTINCT " + " t From "
				+ InquirySurveytype.class.getName() + " t,"
				+ InquirySurveytypeextend.class.getName() +" e" 
				+ " Where  e.inquirySurveytype.id = t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL +" AND e.managerId =:managerId AND e.managerDesc = " +InquirySurveytypeextend.MANAGER_SYSTEM.intValue() + " and t.spaceType="+InquirySurveytype.Space_Type_Group;
				Query query = session.createQuery(hql).setLong("managerId", memberID).setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		
		});
	}

	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getInquiryTypeByAccountId(long accountId){
		DetachedCriteria criteria = DetachedCriteria.forClass(InquirySurveytype.class);
		criteria.add(Restrictions.eq("accountId", accountId));
		return super.executeCriteria(criteria, -1, -1);
	}
	
	/**
	 * 更新调查版块排序
	 * @param surveyTypeId
	 * @param i
	 */
	public void updateSurveyTypeSort(Long surveyTypeId,int i){
		try{
			String hql="update InquirySurveytype type set sort=? where type.id=?";
			super.bulkUpdate(hql, null,new Object[]{i,surveyTypeId});
		}catch(Exception e){
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveytype> getAllInquirySurveytype(String username) throws Exception  {
		List<InquirySurveytype> inqsTypeList = new ArrayList<InquirySurveytype>() ;
		
		try{
			/**
	         * 得到与该名字相近的所有的用户
	         */
			List<Object> param = new ArrayList<Object>();
			final String hql = "from "+ V3xOrgMember.class.getName() +" as m where m.name like ? ";
			param.add("%" + SQLWildcardUtil.escape(username) + "%") ;
			List<V3xOrgMember> memberList = this.find(hql, -1,-1,null, param) ; 
			
			Map<String, Object> namedParameterMap = new HashMap<String, Object>();
			final String hqlf = "from "+ InquirySurveytypeextend.class.getName() + " as inq_type_st where inq_type_st.managerId in (:managerIds)"
								+ " and inq_type_st.managerDesc= " + InquirySurveytypeextend.MANAGER_CHECK;
			List<Long> managerIds = new ArrayList<Long>();
			if(memberList==null || memberList.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				for(V3xOrgMember member : memberList) {
					managerIds.add(member.getId());
				}
			}
			namedParameterMap.put("managerIds", managerIds);
			List<InquirySurveytypeextend> inqsrtList = this.find(hqlf, -1, -1, namedParameterMap);
			
			final String hqlTpe = "from "+InquirySurveytype.class.getName()+" as inqs_type where inqs_type.id in (:typeIds) " 
								         + " and inqs_type.flag = " +InquirySurveytype.FLAG_NORMAL ;
			List<Long> typeIds = new ArrayList<Long>();
			if(inqsrtList!=null && inqsrtList.size()>0) {
				for(InquirySurveytypeextend  extend: inqsrtList) {
					typeIds.add(extend.getInquirySurveytype().getId());
				}
			}
			namedParameterMap.put("typeIds", typeIds);
			inqsTypeList =  this.find(hqlTpe, -1, -1, namedParameterMap) ;
			
		}catch(Exception e){
			log.error("" ,e) ;
		}

		return inqsTypeList ; 
	}

	@SuppressWarnings("unchecked")
	public List<String> getTypeNameList(boolean isGroup, Long loginAccountId) {
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT (ins.typeName) From " + InquirySurveytype.class.getName() + 
		" AS ins Where ins.flag=? and ins.spaceType=? ";
		params.add(InquirySurveytype.FLAG_NORMAL.intValue());
		params.add(isGroup ? InquirySurveytype.Space_Type_Group : InquirySurveytype.Space_Type_Account);
		if(!isGroup){
			hql += " and ins.accountId=?";
			params.add(loginAccountId);
		}
		return super.find(hql, -1, -1, null, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getTypeNameList(long spaceId, int spaceType) {
		List<Object> params = new ArrayList<Object>();
		String hql = "SELECT (ins.typeName) From " + InquirySurveytype.class.getName() + 
		" AS ins Where ins.flag=? and ins.spaceType=? ";
		params.add(InquirySurveytype.FLAG_NORMAL.intValue());
		params.add(spaceType);
		hql += " and ins.accountId=?";
		params.add(spaceId);
		return super.find(hql, -1, -1, null, params);
	}
}
