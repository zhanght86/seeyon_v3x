/**
 * 
 */
package com.seeyon.v3x.inquiry.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquiryClick;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveydiscuss;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.domain.InquiryVotedefinite;
import com.seeyon.v3x.inquiry.util.ConstantsInquiry;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author lin tian
 * 
 * 2007-2-28
 */
public class InquiryBasicDao extends BaseHibernateDao<InquirySurveybasic> {
	
	private static final Log log = LogFactory.getLog(InquiryBasicDao.class);
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	/**
	 * 删除InquirySurveybasic
	 * 
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public void updateInquiryBasic(long id) throws Exception {
		Map map = new HashMap();
		map.put("flag", InquirySurveybasic.FLAG_DELETE);
		super.update(id, map);
	}
	
	/**
	 * 取消发布已发布的调查，把状态置为CENSOR_DRAUGHT(草稿状态)
	 * 
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public void cancelInquiryBasic(long id) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_DRAUGHT);
		super.update(id, map);
	}


	/**
	 * 审核通过不发送
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateInquiryBasicPass(long id, long uid , String checkMind) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_PASS_NO_SEND);
		map.put("censorId", uid);
		map.put("checkMind", checkMind);
		super.update(id, map);
	}

	/**
	 * 审核不通过
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateInquiryBasicNOPass(long id, long uid , String checkMind) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_NO_PASS);
		map.put("censorId", uid);
		map.put("checkMind", checkMind);
		super.update(id, map);
	}

	/**
	 * 立即发送
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateInquiryBasicSend(long id, long uid , String checkMind) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_PASS);
//		map.put("censor", InquirySurveybasic.CENSOR_PASS_NO_BEGIN);
		map.put("censorId", uid);
//		发布者id
		map.put("issuerId", uid);
		map.put("checkMind", checkMind);
		super.update(id, map);
	}

	/**
	 * 发布调查
	 * sendNowOrLater  发送状态   是直接发布还是发布未开始   根据当前时间和发布时间判断
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateCreatorBasicSend(long id , boolean sendNowOrLater) throws Exception {
		Map map = new HashMap();
		if(sendNowOrLater){
			map.put("censor", InquirySurveybasic.CENSOR_PASS);
		}else{
			map.put("censor", InquirySurveybasic.CENSOR_PASS_NO_BEGIN);
		}
		super.update(id, map);
	}

	/**
	 * 发布调查
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateCreatorBasicSendB(long id) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_NO);
		super.update(id, map);
	}

	/**
	 * 发布调查
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateCreatorBasicSendC(long id) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_NO);
		super.update(id, map);
	}

	/**
	 * 发布调查
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void pigeonholeInquiry(long id) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_FILING_YES);
		super.update(id, map);
	}

	/**
	 * 判断当前用户是否点击过当前的调查
	 * 
	 * @param uid
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public boolean getClickByUser(final long uid, final long bid) throws Exception {
		int result = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "Select Count(*) From " + InquiryClick.class.getName()
						+ " As c Where c.userId=:uid AND c.surveybasicId=:bid";
				Query query = session.createQuery(hql).setLong("uid", uid)
						.setLong("bid", bid);
				int result = ((Integer) query.uniqueResult()).intValue();
				return result;
			}
		});
		if (result < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 保存点击记录
	 * 
	 * @param uid
	 * @param bid
	 * @throws Exception
	 */
	public void updatClick(long uid, long bid) throws Exception {
		InquiryClick click = new InquiryClick();
		click.setIdIfNew();
		click.setUserId(uid);
		click.setSurveybasicId(bid);
		super.save(click);
	}

	/**
	 * 更新调查次数
	 */
	public void updateInquiryBasicByVote(long id) throws Exception {
		this.bulkUpdate("update " + InquirySurveybasic.class.getName() + " a set a.voteCount=a.voteCount+1 where a.id = ?", null, id);		
	}

	/**
	 * 更新点击次数
	 */
	public void updateBasicByCilckCount(long id) throws Exception {
		this.bulkUpdate("update " + InquirySurveybasic.class.getName() + " a set a.clickCount=a.clickCount+1 where a.id = ?", null, id);	
	}

	/**
	 * 更新投票次数
	 */
	public void updateInquiryItem(Long id) throws Exception {
		this.bulkUpdate("update " + InquirySubsurveyitem.class.getName() + " a set a.voteCount=a.voteCount+1 where a.id = ?", null, id);
	}

	/**
	 * 终止调查
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateBasicClose(long id) throws Exception {
		Map map = new HashMap();
		map.put("censor", InquirySurveybasic.CENSOR_CLOSE);
		super.update(id, map);
	}

	/**
	 * 根据ID删除评论
	 * 
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public void removeDiscuss(long did) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("delete " + InquirySurveydiscuss.class.getName()).append(" a where a.id = ?");
		super.getHibernateTemplate().bulkUpdate(sb.toString(), did);

	}
	
	/**
	 * 显示集团最新调查列表 重载原先的方法
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getGroupInquiryBasicListByUserScope(List<Long> domainIds, int size, long userId) {
		String hql = "SELECT DISTINCT " + " b " + "FROM " + InquirySurveybasic.class.getName() + " b,"
					+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
					+ " s.inquirySurveybasic.id = b.id AND ( s.scopeId IN (:domainIds) "
					+ "or b.createrId=:userId ) AND  b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
					+ " AND b.censor=" + InquirySurveybasic.CENSOR_PASS.intValue()
					+ " AND b.surveyTypeId = t.id and t.flag="
					+ InquirySurveytype.FLAG_NORMAL.intValue()
					+ " AND t.accountId =0 ORDER BY b.sendDate DESC";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		params.put("userId", userId);
		return this.find(hql, 0, size, params);
		
	}

	/**
	 * 显示用户查看权限内的最新n条信息
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getInquiryBasicListByUserScope( final String authID, final int size )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
//				判断版本是否显示集团信息
				boolean flag = (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
				String authIDs = authID;
				Long accountId = CurrentUser.get().getLoginAccount();
				if(flag){
					Long rootAccountId = null;
					try {
						rootAccountId = orgManager.getRootAccount().getId();
					} catch (BusinessException e) {
						log.error("获取根单位失败", e);
					}
					authIDs+=","+rootAccountId;
				}
				String hql = "SELECT DISTINCT " + " b " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
						+ " s.inquirySurveybasic.id = b.id AND ( s.scopeId IN (:scopeIds) "
						+ "or b.createrId=:createUserId ) AND  b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
						+ " AND b.censor=" + InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.surveyTypeId = t.id and t.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						+ " AND t.spaceType="+InquirySurveytype.Space_Type_Account
						+ " AND t.accountId =:accountId"   //加了个单位判断，兼职查看单位空间的信息过滤掉原单位数据
						+ " ORDER BY b.sendDate DESC";
				//Query query = session.createQuery(hql).setLong("createUserId", CurrentUser.get().getId()).setFirstResult(0).setMaxResults(size);
				String[] scopeIdStrs = authIDs.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				
				Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds)
													  .setLong("createUserId", CurrentUser.get().getId())
													  .setLong("accountId", accountId)
													  .setFirstResult(0).setMaxResults(size);
				return query.list();
			}
		});
	}
	/**
	 * 显示自定义空间用户查看权限内的最新n条信息
	 * @param spaceId
	 * @param authID
	 * @param size
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getCustomInquiryBasicListByUserScope(final long spaceId, final String authID, final int size )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String authIDs = authID;
				String hql = "SELECT DISTINCT " + " b " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
						+ " s.inquirySurveybasic.id = b.id AND ( s.scopeId IN (:scopeIds) "
						+ "or b.createrId=:createUserId ) AND  b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
						+ " AND b.censor=" + InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.surveyTypeId = t.id and t.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						+ " AND t.spaceType!="+InquirySurveytype.Space_Type_Group   //集团类型的过滤掉
						+ " AND t.accountId =:accountId"
						+ " ORDER BY b.sendDate DESC";
				String[] scopeIdStrs = authIDs.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds)
													  .setLong("createUserId", CurrentUser.get().getId())
													  .setLong("accountId", spaceId)
													  .setFirstResult(0).setMaxResults(size);
				return query.list();
			}
		});
	}
	
	/**
	 * 显示集团最新调查列表
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getGroupInquiryBasicListByUserScope( final String authID, final int size ) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authID.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
				//+ " s.inquirySurveybasic.id = b.id AND s.scopeId IN (" + authID
				//+ ") AND  b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " s.inquirySurveybasic.id = b.id AND s.scopeId IN (:scopeIds)"
				+ " AND  b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.censor=" + InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND t.accountId=0 ORDER BY b.sendDate DESC";
		//Query query = session.createQuery(hql).setFirstResult(0).setMaxResults(size);
		Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds).setFirstResult(0).setMaxResults(size);
		return query.list();
			}
		});
	}

	/**
	 * 显示用户查看权限内的全部调查
	 * 调查主列表页面右上角的查询模块
	 */
	public List<InquirySurveybasic> getALLBasicListByUserScope( final String authID , final String condition , final String textfield , final String textfield1 , final boolean isGroup , final String typeId )
			throws Exception {
		//判断版本是否显示集团信息
		boolean flag = (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
		return this.getALLBasicListByUserScope(authID, condition, textfield, textfield1, isGroup, typeId, flag, null);
	}
	
	/**
	 * 自定义空间
	 * 显示用户查看权限内的全部调查
	 * 调查主列表页面右上角的查询模块
	 */
	public List<InquirySurveybasic> getALLCustomBasicListByUserScope(int spaceType, final String authID , final String condition , final String textfield , final String textfield1 , final boolean isGroup , final String typeId ) throws Exception {
		//自定义空间不显示集团信息
		boolean flag = false;
		String spaceQueryString = " AND t.spaceType = 5";
		if (spaceType == 6) {
			spaceQueryString = " AND t.spaceType = 6";
		} else if (spaceType == 4) {
			spaceQueryString = " AND t.spaceType = 4";
		}
		return this.getALLBasicListByUserScope(authID, condition, textfield, textfield1, isGroup, typeId, flag, spaceQueryString);
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getALLBasicListByUserScope(final String authID, final String condition, final String textfield, final String textfield1, final boolean isGroup, final String typeId, final boolean flag, final String spaceQueryString) throws Exception {
		String authIDs = authID;
		if(flag && CurrentUser.get().isInternal()){
			Long rootAccountId = null;
			try {
				rootAccountId = orgManager.getRootAccount().getId();
			} catch (BusinessException e) {
				log.error("获取根单位失败", e);
			}
			authIDs+=","+rootAccountId;
		}
		Map params=new HashMap();
		String isGroupQueryString;
		if (spaceQueryString != null) {
			isGroupQueryString = spaceQueryString;
		} else {
			isGroupQueryString = " AND t.spaceType =:spacetype";
			if(isGroup){
				params.put("spacetype", InquirySurveytype.Space_Type_Group);
			}else{
				params.put("spacetype", InquirySurveytype.Space_Type_Account);
				isGroupQueryString += " AND t.accountId =:accountId";
				params.put("accountId", CurrentUser.get().getLoginAccount());
				
			}
			
		}
		
		String querySql = "";
		String startDate = textfield;
		String endDate = textfield1;
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				querySql = " AND b.surveyName like :textfield";
				params.put("textfield", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}else if(condition.equals("creater")){
				querySql = " AND b.createrId=m.id AND m.name like :name";
				params.put("name", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND b.sendDate between :startDate and :endDate ";
					params.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					params.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND b.sendDate > :startDate ";
					params.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND b.sendDate < :endDate ";
					params.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				}
			}
		}
		
		if(typeId!=null&&!typeId.equals("")){
			querySql+= " and b.surveyTypeId=:typeid "; 
			params.put("typeid", Long.valueOf(typeId));
		}
		
		String[] scopeIdStrs = authIDs.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		String hql = null;
		if (typeId == null) {
			hql = "SELECT DISTINCT " + " b " + "FROM "
			+ InquirySurveybasic.class.getName() + " b,"
			+ V3xOrgMember.class.getName() + " m,"
			+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " where"
			+ " (s.inquirySurveybasic.id = b.id) AND" + " (s.scopeId in (:scopeIds) or b.createrId=:userId) " 
			+ "AND  b.flag =:bflag " 
			+ "AND (b.censor=" + InquirySurveybasic.CENSOR_PASS + ") "
			+ "AND b.surveyTypeId = t.id and"
			+" t.flag=:tflag" +isGroupQueryString
			+ querySql
			+ " ORDER BY b.sendDate DESC";
		} else {
			hql = "SELECT DISTINCT " + " b " + "FROM "
			+ InquirySurveybasic.class.getName() + " b,"
			+ V3xOrgMember.class.getName() + " m,"
			+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " where"
			+ " (s.inquirySurveybasic.id = b.id) AND" + " (s.scopeId in (:scopeIds) or b.createrId=:userId) " 
			+ "AND  b.flag =:bflag " 
			+ "AND (b.censor=" + InquirySurveybasic.CENSOR_PASS + " or b.censor=" + InquirySurveybasic.CENSOR_CLOSE + ") "
			+ "AND b.surveyTypeId = t.id and"
			+" t.flag=:tflag" +isGroupQueryString
			+ querySql
			+ " ORDER BY b.sendDate DESC";
		}
		params.put("bflag", InquirySurveybasic.FLAG_NORMAL.intValue());
		params.put("tflag", InquirySurveytype.FLAG_NORMAL.intValue());
		params.put("scopeIds", scopeIds);
		params.put("userId", CurrentUser.get().getId());
		return this.find(hql, "b.id", true, params);
	}
	
	/**
	 * 显示用户查看权限内的全部调查   不需要分页
	 * 在集团列表页面点击更多按钮时的操作
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getALLBasicListByUserScope( final String authID , final boolean isGroup )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
//				判断版本是否显示集团信息
				boolean flag = (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
				String authIDs = authID;
				if(flag){
					Long rootAccountId = null;
					try {
						rootAccountId = orgManager.getRootAccount().getId();
					} catch (BusinessException e) {
						log.error("获取根单位失败", e);
					}
					authIDs+=","+rootAccountId;
				}
				
				String isGroupQueryString = " AND b.inquirySurveytype.spaceType !="+InquirySurveytype.Space_Type_Group;
				if(isGroup){
					isGroupQueryString = " AND b.inquirySurveytype.spaceType ="+InquirySurveytype.Space_Type_Group;
				}
				
				//HQL语句清理 modified by Meng Yang 2009-05-31
				String[] scopeIdStrs = authIDs.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				String hql = "SELECT DISTINCT " + " b " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s" + " WHERE"
						+ " (s.inquirySurveybasic.id = b.id) AND" + " (s.scopeId in (:scopeIds) or b.createrId=:userId) AND b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
						+ InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.inquirySurveytype.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						+ isGroupQueryString
						+ " ORDER BY b.sendDate DESC";
				
				Query query = session.createQuery(hql);
				return query.setParameterList("scopeIds", scopeIds).setLong("userId", CurrentUser.get().getId()).list();
			}
		});
	}
	
	/**
	 * 获取集团空间调查列表
	 * @param sendtime
	 * @param closetime
	 * @param authID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getGroupBasicListByUserScope( final String authID , final String condition , final String textfield , final String textfield1 , final String typeId )
			throws Exception {
		/*String querySql = "";
		String startDate = textfield;
		String endDate = textfield1;
		Map params=new HashMap();
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				querySql = " AND b.surveyName like '%"+textfield+"%'";
			}else if(condition.equals("issueUser")){
				
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND b.sendDate between :startDate and :endDate ";
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND b.sendDate > :startDate ";
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND b.sendDate < :endDate ";
				}
				
				String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " where"
				+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
				+ authID + ") AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND t.spaceType="+InquirySurveytype.Space_Type_Group
				+ querySql
				+ " ORDER BY b.sendDate DESC";
				
				
			}
		}
		return null;*/
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String querySql = "";
		
		String startDate = textfield;
		String endDate = textfield1;
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authID.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				//querySql = " AND b.surveyName like '%"+textfield+"%'";
				querySql = " AND b.surveyName like :surveyName";
			}else if(condition.equals("issueUser")){
				
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND b.sendDate between :startDate and :endDate ";
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND b.sendDate > :startDate ";
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND b.sendDate < :endDate ";
				}
//				if(textfield.equals("")&&textfield1.equals("")){
//					sendtime = new Timestamp(System.currentTimeMillis());
//				}else{
//					sendtime = new Timestamp(Datetimes.parseDate(textfield).getTime());
//				}
//				querySql = " AND b.sendDate > :sendtime ";
			}
		}
		
		if(typeId!=null&&!typeId.equals("")){
			//querySql+= " and b.surveyTypeId = " + typeId;
			querySql+= " and b.surveyTypeId = :typeId ";
		}
		
		String count = "SELECT Count(DISTINCT " + " b) " + " FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
				//+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
				//+ authID + ") AND  b.flag ="
				+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in (:scopeIds) AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND t.spaceType="+InquirySurveytype.Space_Type_Group
				+ querySql;
		Query queryCount = session.createQuery(count);
		
		queryCount.setParameterList("scopeIds", scopeIds);
		if(condition != null && !condition.equals("") && condition.equals("subject")){
			//queryCount.setString("subject", "%" + textfield + "%");
			//需要使用SQL通配符转义类以解决不同数据库下的查询问题 modified by Meng Yang 2009-06-10
			queryCount.setString("subject", "%" + SQLWildcardUtil.escape(textfield) + "%");
		}
		
		if(typeId!=null&&!typeId.equals("")) {
			queryCount.setLong("typeId", Long.parseLong(typeId));
		}
		
		if(condition != null && !condition.equals("") && condition.equals("createDate")){
//			queryCount.setTimestamp("sendtime", sendtime);
			if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
				queryCount.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				queryCount.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
			} else if (StringUtils.isNotBlank(textfield)
					&& !StringUtils.isNotBlank(textfield1)) {
				queryCount.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
			} else if (!StringUtils.isNotBlank(textfield)
					&& StringUtils.isNotBlank(textfield1)) {
				queryCount.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
			}
		}
		int blistCount = ((Integer) queryCount.uniqueResult()).intValue();
		Pagination.setRowCount(blistCount);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
				//+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
				//+ authID + ") AND  b.flag ="
				+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in (:scopeIds) AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND t.spaceType="+InquirySurveytype.Space_Type_Group
				+ querySql
				+ " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql);
		query.setParameterList("scopeIds", scopeIds);
		if(condition != null && !condition.equals("") && condition.equals("subject")){
			//query.setString("subject", "%" + textfield + "%");
			//需要使用SQL通配符转义类以解决不同数据库下的查询问题 modified by Meng Yang 2009-06-10
			query.setString("subject", "%" + SQLWildcardUtil.escape(textfield) + "%");
		}
		
		if(typeId!=null&&!typeId.equals("")) {
			query.setLong("typeId", Long.parseLong(typeId));
		}
		
		if(condition != null && !condition.equals("") && condition.equals("createDate")){
//			query.setTimestamp("sendtime", sendtime);
			if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
				query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
			} else if (StringUtils.isNotBlank(textfield)
					&& !StringUtils.isNotBlank(textfield1)) {
				query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
			} else if (!StringUtils.isNotBlank(textfield)
					&& StringUtils.isNotBlank(textfield1)) {
				query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
			}
		}
		query.setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 管理员获取当前调查类型下调查列表
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getManagerBasicListByType(final long tid , final String condition, final String textfield, final String textfield1)
			throws Exception {
		//分三种情况查询,没有查询条件,有一个查询条件,有两个查询条件(按时间段查询)
		//查出的状态为发布和终止状态
		String querySql = "";
		String startDate = textfield;
		String endDate = textfield1;
		String condsql="";
		if(Strings.isNotBlank(condition)){
			if(condition.equals("subject")){
				querySql = " AND isb.surveyName like :isbSurveyName";
			} else if(condition.equals("sender")){	//应当允许用户以空值查找并避免抛出异常 modified by Meng Yang 2009-05-31
				//发布者按模糊查询
				condsql=","	+ V3xOrgMember.class.getName()+ " as m";
				querySql = " and isb.createrId=m.id and m.name like :mName ";
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND isb.sendDate between :startDate and :endDate";
				} else if (StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND isb.sendDate > :startDate ";
				} else if (!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND isb.sendDate < :endDate ";
				}
			}
		}
		String hqlStr = "SELECT DISTINCT "
			+ " isb From "
			+ InquirySurveybasic.class.getName()
			+ " isb, "
			+ InquirySurveytype.class.getName()
			+ " t  "+condsql
			+ " Where isb.flag=:isbFlag"
			+ " AND ( isb.censor=:isbCensor1"
			+ "  OR isb.censor=:isbCensor2"
			+ ") AND isb.surveyTypeId=t.id AND t.id=:tId AND t.flag=:tFlag"
			+ querySql
			+ " ORDER BY isb.sendDate DESC";
		
		Map<String,Object> paramsMap=new HashMap<String,Object>();
		paramsMap.put("isbFlag", InquirySurveybasic.FLAG_NORMAL.intValue());
		paramsMap.put("isbCensor1", InquirySurveybasic.CENSOR_PASS.intValue());
		paramsMap.put("isbCensor2", InquirySurveybasic.CENSOR_CLOSE.intValue());
		paramsMap.put("tId", tid);
		paramsMap.put("tFlag", InquirySurveytype.FLAG_NORMAL.intValue());
		
		if(Strings.isNotBlank(condition)){
			if("createDate".equals(condition)){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					paramsMap.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					paramsMap.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				} else if (StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)) {
					paramsMap.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				} else if (!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					paramsMap.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				}
			} else if("subject".equals(condition)){
				paramsMap.put("isbSurveyName","%" + SQLWildcardUtil.escape(textfield) + "%");
			} else if("sender".equals(condition)) {
				paramsMap.put("mName","%" + SQLWildcardUtil.escape(textfield) + "%");
			}
		}
		return this.find(hqlStr, "isb.id", true, paramsMap);
	}

	/**
	 * 根据用户权限和当前调查类型获取调查列表
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getInquiryBasicListByUserScopeAndType(
			final Timestamp sendtime, final Timestamp closetime, final String authID, final long tid , final String condition, final String textfield, final String textfield1 )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
//		判断版本是否显示集团信息
		boolean flag = (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
		String authIDs = authID;
		if(flag){
			Long rootAccountId = null;
			try {
				rootAccountId = orgManager.getRootAccount().getId();
			} catch (BusinessException e) {
				log.error("获取根单位失败", e);
			}
			authIDs+=","+rootAccountId;
		}
//		12-18 增加查询  xut
		String querySql = "";
		String startDate = textfield;
		String endDate = textfield1;
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authIDs.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				//querySql = " AND b.surveyName like '%"+textfield+"%'";
				querySql = " AND b.surveyName like :surveyName";
			}else if(condition.equals("sender")&&!textfield.equals("")){
//				querySql = " AND b.createrId=:senderId";
				//querySql = " and b.createrId=m.id and m.name like '%" + textfield + "%' ";
				querySql = " and b.createrId=m.id and m.name like :senderName";
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND b.sendDate between :startDate and :endDate ";
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND b.sendDate > :startDate ";
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND b.sendDate < :endDate ";
				}
			}
		}
		
		String count = "SELECT Count(DISTINCT "
				+ " b) "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b,"
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t ,"
				+ V3xOrgMember.class.getName() 
				+ " as m "				
				+ " WHERE"
				+ " b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				//+ " and ( (b.createrId =:createrId) or s.scopeId in ("
				//+ authIDs
				//+ ") ) "
				+ " and ( (b.createrId =:createrId) or s.scopeId in (:scopeIds) )"
				+ " AND (s.inquirySurveybasic.id = b.id) "
				+ " AND b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()+querySql;
		//Query countquery =session.createQuery(count).setLong("createrId", memberid).setLong("tid", tid);
		Query countquery =session.createQuery(count).setLong("createrId", memberid).setParameterList("scopeIds", scopeIds).setLong("tid", tid);
		
		if(condition != null && !condition.equals("")){
			if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					countquery.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					countquery.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					countquery.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					countquery.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				}
			}
			//else if(condition.equals("sender")&&!textfield1.equals("")){
			//	countquery.setLong("senderId", new Long(textfield1));
			//}
			else if(condition.equals("subject")) {
				countquery.setString("surveyName", textfield);
			} else if(condition.equals("sender")&&!textfield.equals("")) {
				countquery.setString("senderName", textfield);
			}
			
		}
		
		int bcount = ((Integer) countquery.uniqueResult()).intValue();
		Pagination.setRowCount(bcount);

		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b,"
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t ,"
				+ V3xOrgMember.class.getName() 
				+ " as m "
				+ " WHERE"
				+ " b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				//+ " and ( (b.createrId =:createrId) or s.scopeId in ("
				//+ authIDs
				//+ ") ) "
				+ " and ( (b.createrId =:createrId) or s.scopeId in (:scopeIds) )"
				+ " AND (s.inquirySurveybasic.id = b.id) "
				+ " AND b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()+querySql
				+ " ORDER BY b.sendDate DESC";
		//Query query = session.createQuery(hql).setLong("tid", tid).setLong("createrId", memberid);
		Query query = session.createQuery(hql).setLong("tid", tid).setLong("createrId", memberid).setParameterList("scopeIds", scopeIds);
		
		if(condition != null && !condition.equals("")){
			if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				} else if (StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)) {
					query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				} else if (!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				}
			}
			//else if(condition.equals("sender")&&!textfield1.equals("")){
			//	query.setLong("senderId", new Long(textfield1));
			//}
			else if(condition.equals("subject")) {
				query.setString("surveyName", textfield);
			} else if(condition.equals("sender")&&!textfield.equals("")) {
				query.setString("senderName", textfield);
			}
		}
		
		query.setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}
	
	/**
	 * 根据用户权限和当前调查类型获取调查列表
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getInquiryBasicListByUserScopeAndType( final long tid , final String authID,  final int size )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
//		判断版本是否显示集团信息
		boolean flag = (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
		String authIDs = authID;
		if(flag){
			Long rootAccountId = null;
			try {
				rootAccountId = orgManager.getRootAccount().getId();
			} catch (BusinessException e) {
				log.error("获取根单位失败", e);
			}
			authIDs+=","+rootAccountId;
		}
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authIDs.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		String count = "SELECT Count(DISTINCT "
				+ " b) "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b,"
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				//+ " and ( (b.createrId =:createrId) or s.scopeId in ("
				//+ authIDs
				//+ ") ) "
				+ " and ( (b.createrId =:createrId) or s.scopeId in (:scopeIds) )"
				+ " AND (s.inquirySurveybasic.id = b.id) "
				+ " AND b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue();
		//Query countquery =session.createQuery(count).setLong("createrId", memberid).setLong("tid", tid);
		Query countquery =session.createQuery(count).setLong("createrId", memberid).setParameterList("scopeIds", scopeIds).setLong("tid", tid);
		
		int bcount = ((Integer) countquery.uniqueResult()).intValue();
		Pagination.setRowCount(bcount);

		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b,"
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				//+ " and ( (b.createrId =:createrId) or s.scopeId in ("
				//+ authIDs
				//+ ") ) "
				+ " and ( (b.createrId =:createrId) or s.scopeId in (:scopeIds) )"
				+ " AND (s.inquirySurveybasic.id = b.id) "
				+ " AND b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " ORDER BY b.sendDate DESC";
		//Query query = session.createQuery(hql).setLong("tid", tid).setLong("createrId", memberid);
		Query query = session.createQuery(hql).setLong("tid", tid).setParameterList("scopeIds", scopeIds).setLong("createrId", memberid);
		
		query.setFirstResult(0).setMaxResults(size);
		return query.list();
			}
		});
	}

	/**
	 * 获取当前调查审核员待审核的最新5条调查
	 * 
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getCheckSurveyBasicListByChecker(
			final long managerId) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT DISTINCT "
				+ " isb  From "
				+ InquirySurveybasic.class.getName()
				+ " isb , "
				+ InquirySurveytypeextend.class.getName()
				+ " ist"
				+ " Where isb.inquirySurveytype.censorDesc="
				+ InquirySurveytype.CENSOR_NO_PASS.intValue()
				+ " AND isb.flag="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND isb.censor="
				+ InquirySurveybasic.CENSOR_NO.intValue()
				+ " AND isb.inquirySurveytype.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND  (isb.inquirySurveytype.id =ist.inquirySurveytype.id ) AND ist.managerId =:managerId"
				+ " AND ist.managerDesc="
				+ InquirySurveytypeextend.MANAGER_CHECK.intValue()
				+ " order by isb.sendDate desc";
		Query query = session.createQuery(hql).setFirstResult(0)
				.setMaxResults(5).setLong("managerId", managerId);
		return query.list();
			}
		});
	}

	/**
	 * 获取当前调查审核员待审核的全部调查列表
	 * 
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getWaitCensorBasicListByChecker( final long managerId , final String condition, final String textfield, final String textfield1,String surveyTypeId ) throws Exception {
		return getWaitCensorBasicList(managerId, condition, textfield, textfield1, surveyTypeId, null);
	}
	/**
	 * 自定义空间获取当前调查审核员待审核的全部调查列表
	 * @param managerId
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getCustomWaitCensorBasicListByChecker(final long managerId, final String condition, final String textfield, final String textfield1, String surveyTypeId, long spaceId) throws Exception {
		return getWaitCensorBasicList(managerId, condition, textfield, textfield1, surveyTypeId, spaceId);
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getWaitCensorBasicList(final long managerId, final String condition, final String textfield, final String textfield1, String surveyTypeId, Long spaceId) throws Exception {
		String querySql="";
		String condsql="";
		Map<String,Object> params=new HashMap<String,Object>();
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				querySql = " AND isb.surveyName like :surverName";
				params.put("surverName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}else if(condition.equals("sender")&&!textfield.equals("")){
				condsql="," + V3xOrgMember.class.getName() + " as m ";
				querySql = " and isb.createrId=m.id and m.name like :mName ";
				params.put("mName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}
		}
		String hqlid="";
		if(surveyTypeId!=null&&!"".equals(surveyTypeId))
		{
			hqlid="AND isb.surveyTypeId = :isbTypeId";
			params.put("isbTypeId", Long.valueOf(surveyTypeId));
		}
		String hql = "SELECT DISTINCT "
			+ " isb  From "
			+ InquirySurveybasic.class.getName()
			+ " isb , "
			+ InquirySurveytypeextend.class.getName()
			+ " ist , "
			+ InquirySurveytype.class.getName()
			+ " t "+condsql
			+ " Where ( isb.censor=:iscen1 or isb.censor=:iscen2 ) "+hqlid+" and t.flag=:tflag "
			+ " and t.censorDesc=:tcenDesc AND isb.flag=:isbFlag AND (isb.surveyTypeId =ist.inquirySurveytype.id ) " 
			+ "AND  (ist.inquirySurveytype.id=t.id and t.accountId=:accId ) AND ist.managerId =:managerId"
			+ " AND ist.managerDesc=:istManDesc" + querySql
			+ " order by isb.sendDate desc";
		params.put("iscen1", InquirySurveybasic.CENSOR_NO.intValue());
		params.put("iscen2", InquirySurveybasic.CENSOR_PASS_NO_SEND.intValue());
		params.put("tflag", InquirySurveytype.FLAG_NORMAL.intValue());
		params.put("tcenDesc", InquirySurveytype.CENSOR_NO_PASS.intValue());
		params.put("isbFlag", InquirySurveybasic.FLAG_NORMAL.intValue());
		params.put("accId", spaceId != null ? spaceId : CurrentUser.get().getLoginAccount());
		params.put("managerId", managerId);
		params.put("istManDesc", InquirySurveytypeextend.MANAGER_CHECK.intValue());
		List<InquirySurveybasic> list=this.find(hql, "isb.id", true, params);
		return list;
	}
	
	/**
	 * 获取当前调查审核员待审核的全部集团调查列表
	 * 
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getWaitCensorGroupBasicListByChecker( final long managerId , final String condition, final String textfield, final String textfield1,String surveyTypeId) throws Exception {
		String querySql="";
		String condhql="";
		Map<String,Object> params=new HashMap<String,Object>();
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				querySql = " AND isb.surveyName like :sName";
				//params.put("sName", "%"+textfield+"%");
				params.put("sName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}else if(condition.equals("sender")&&!textfield.equals("")){
				//querySql = " AND isb.createrId=:senderId";
				condhql=","+ V3xOrgMember.class.getName()+ " as m ";
				querySql = " and isb.createrId=m.id and m.name like :mName ";
				//params.put("mName", "%"+textfield+"%");
				params.put("mName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}
		}
		String hqlid="";
		if(surveyTypeId!=null&&!"".equals(surveyTypeId))
		{
			hqlid="isb.surveyTypeId = :isbTypeId and ";
			params.put("isbTypeId", Long.valueOf(surveyTypeId));
		}else
		{
			hqlid="isb.surveyTypeId = ist.inquirySurveytype.id and ";
		}
		String hql = "SELECT DISTINCT "
			+ " isb  From "
			+ InquirySurveybasic.class.getName()
			+ " isb , "
			+ InquirySurveytypeextend.class.getName()
			+ " ist  "+condhql
			+ " Where "+hqlid+" ist.inquirySurveytype.censorDesc=:inqcen AND " 
			+ "isb.flag=:isbFlag AND ( isb.censor= :isbx or isb.censor=:isbd ) AND (ist.inquirySurveytype.flag=:isFlag"
			+ " AND  ist.inquirySurveytype.spaceType=:spaceType) AND ist.managerId =:managerId"
			+ " AND ist.managerDesc=:istDesc"+ querySql
			+ " order by isb.sendDate desc";
		params.put("inqcen", InquirySurveytype.CENSOR_NO_PASS.intValue());
		params.put("isbFlag", InquirySurveybasic.FLAG_NORMAL.intValue());
		params.put("isbx", InquirySurveybasic.CENSOR_NO.intValue());
		params.put("isbd", InquirySurveybasic.CENSOR_PASS_NO_SEND.intValue());
		params.put("isFlag", InquirySurveytype.FLAG_NORMAL.intValue());
		params.put("spaceType", InquirySurveytype.Space_Type_Group);
		params.put("managerId", managerId);
		params.put("istDesc", InquirySurveytypeextend.MANAGER_CHECK.intValue());
		return this.find(hql, "isb.id", true, params);
	}

	/**
	 * 获取当前调查审核员待审核的全部调查列表
	 * 诊对调查列表类型页面的数据
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorBasicListByCheckerInt( final long managerId , final String condition, final String textfield, final String textfield1,String surveyTypeId ) throws Exception {
		return getWaitCensorBasicListSize(managerId, condition, textfield, textfield1, surveyTypeId, null);
	}
	/**
	 * 自定义空间获取当前调查审核员待审核的全部调查列表
	 * 诊对调查列表类型页面的数据
	 * @param managerId
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @return
	 * @throws Exception
	 */
	public int getCustomWaitCensorBasicListByCheckerInt( final long managerId , final String condition, final String textfield, final String textfield1,String surveyTypeId, long spaceId) throws Exception {
		return getWaitCensorBasicListSize(managerId, condition, textfield, textfield1, surveyTypeId, spaceId);
	}
	/**
	 * 当前调查审核员待审核的全部调查列表
	 * @param managerId
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorBasicListSize(final long managerId, final String condition, final String textfield, final String textfield1, String surveyTypeId, Long spaceId) throws Exception {
		String hql = "SELECT count(distinct isb.id)"
			+ " From "
			+ InquirySurveybasic.class.getName()
			+ " isb , "
			+ InquirySurveytypeextend.class.getName()
			+ " ist , "
			+ InquirySurveytype.class.getName()
			+ " t "
			+ " Where ( isb.censor=? or isb.censor=? )  and t.flag=? "
			+ " and t.censorDesc=? AND isb.flag=? AND (isb.surveyTypeId =ist.inquirySurveytype.id ) " 
			+ "AND  (ist.inquirySurveytype.id=t.id and t.accountId=? ) AND ist.managerId =?"
			+ " AND ist.managerDesc=?";
		
		Object[] obj={InquirySurveybasic.CENSOR_NO.intValue(),
				InquirySurveybasic.CENSOR_PASS_NO_SEND.intValue(),
				InquirySurveytype.FLAG_NORMAL.intValue(),
				InquirySurveytype.CENSOR_NO_PASS.intValue(),
				InquirySurveybasic.FLAG_NORMAL.intValue(),
				spaceId != null ? spaceId : CurrentUser.get().getLoginAccount(),
				managerId,
				InquirySurveytypeextend.MANAGER_CHECK.intValue()};
		List list=this.getHibernateTemplate().find(hql, obj);
		if(list!=null&&list.size()>0)
		{
			return (Integer)list.get(0);
		}
		return 0;
	}
	
	/**
	 * 获取当前调查审核员待审核的全部集团调查列表
	 * 
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int getWaitCensorGroupBasicListByCheckerInt( final long managerId , final String condition, final String textfield, final String textfield1,String surveyTypeId) throws Exception {
		String hql = "SELECT count(distinct isb.id)"
			+ " From "
			+ InquirySurveybasic.class.getName()
			+ " isb , "
			+ InquirySurveytypeextend.class.getName()
			+ " ist  "
			+ " Where isb.surveyTypeId = ist.inquirySurveytype.id and ist.inquirySurveytype.censorDesc=? AND " 
			+ "isb.flag=? AND ( isb.censor= ? or isb.censor=? ) AND (ist.inquirySurveytype.flag=?"
			+ " AND  ist.inquirySurveytype.spaceType=?) AND ist.managerId =?"
			+ " AND ist.managerDesc=?";
	
		Object[] obj={
				InquirySurveytype.CENSOR_NO_PASS.intValue(),
				InquirySurveybasic.FLAG_NORMAL.intValue(),
				InquirySurveybasic.CENSOR_NO.intValue(),
				InquirySurveybasic.CENSOR_PASS_NO_SEND.intValue(),
				InquirySurveytype.FLAG_NORMAL.intValue(),
				InquirySurveytype.Space_Type_Group,
				managerId,
				InquirySurveytypeextend.MANAGER_CHECK.intValue()
		};
		List list=this.getHibernateTemplate().find(hql, obj);
		if(list!=null&&list.size()>0)
		{
			return (Integer)list.get(0);
		}
		return 0;
	}
	
	/**
	 * 根据ID获取用户有权看见 未过期的调查
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getInquiryBasicByUserScopeAndBasicID(
			final Timestamp sendtime, final Timestamp closetime, final String authID, final long basicid)
			throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authID.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b,"
				+ InquiryScope.class.getName() + " s , InquirySurveytype t " + " WHERE"
				+ " (s.inquirySurveybasic.id = b.id) AND b.id=:id AND"				
				+ "  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				//+ " AND ( s.scopeId in (" + authID + ") OR (b.createrId = :createrId))";
				+ " AND ( s.scopeId in (:scopeIds) OR (b.createrId = :createrId))";
		//Query query = session.createQuery(hql).setLong("createrId", memberid).setLong("id", basicid);
		Query query = session.createQuery(hql).setLong("createrId", memberid).setParameterList("scopeIds", scopeIds).setLong("id", basicid);
		return (InquirySurveybasic) query.uniqueResult();
			}
		});
	}
	
	/**
	 * 判断当前用户是否能够对当前调查进行投票或评论
	 * 如果用户对当前调查已经进行了投票或评论，则不允许其继续处理，反之可以
	 * @param userId   			用户ID
	 * @param inquiryBasicId	调查ID
	 * @return 用户能处理当前调查-true, 用户已经处理过当前调查，现在不能继续处理-false
	 */
	public boolean canUserHandleTheInquiry(Long userId, Long inquiryBasicId) {
		return (this.getUserVoteOrCommentCount(InquiryVotedefinite.class, userId, inquiryBasicId) < 1) && 
			   (this.getUserVoteOrCommentCount(InquirySurveydiscuss.class, userId, inquiryBasicId) < 1);
	}
	
	/**
	 * 获取某一用户对某一调查的投票或评论总数，辅助判断用户是否处理了该调查
	 * @param clazz		  	  投票或评论表对应Class：<code>InquiryVotedefinite.class</code>、<code>InquirySurveydiscuss.class</code>
	 * @param userId		  用户ID
	 * @param inquiryBasicId  调查ID
	 */
	@SuppressWarnings("unchecked")
	public int getUserVoteOrCommentCount(Class clazz, Long userId, Long inquiryBasicId) {
		String hql = "from " + clazz.getName() + " as v where v.userId=? and v.inquirySurveybasic.id=?";
		Object[] values = {userId, inquiryBasicId};
		Type[] types = {Hibernate.LONG, Hibernate.LONG};
		return super.getQueryCount(hql, values, types);	
	}
		
	/**
	 * 判断用户有无对当前调查填写权限
	 * @deprecated 偶发判断有误：在用户已经投票的情况下，判断结果是用户仍能继续投票
	 * @see #canUserHandleTheInquiry
	 */
	public boolean getUserScopeAndBasicID( final Timestamp sendtime,
			final Timestamp closetime, final long memberid, final long basicid) throws Exception {
		int in =  (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT count(DISTINCT b)"
				+ " FROM "
				+ InquirySurveybasic.class.getName()
				+ " b,"
				+ InquiryVotedefinite.class.getName()
				+ " v,"
				+InquirySurveydiscuss.class.getName()
				+" d, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE "
				+ " ((v.inquirySurveybasic.id = b.id  AND v.userId=:memberid) or " +
						"(d.inquirySurveybasic.id = b.id and d.userId = :memberid2))  AND b.id=:id AND "
				+ " b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.censor=" + InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.surveyTypeId = t.id and t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue();
//				+ " AND b.sendDate < :sendtime  AND b.closeDate > :closetime";

		Query query = session.createQuery(hql).setLong("memberid", memberid).setLong("memberid2", memberid).setLong("id", basicid);
		int in = ((Integer) query.uniqueResult()).intValue();
		return in;
			}
		});
		if (in < 1) {
			// 有权！
			return true;
		}
		// WU权！
		return false;
	}

	/**
	 * 审核员根据ID获取未审核调查
	 * 
	 * @param sendtime
	 * @param closetime
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getInquiryBasic( final long basicid) throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				String hql = "FROM " + InquirySurveybasic.class.getName() + " b"
						+ " WHERE  b.id=:id AND b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue();
				
				Query query = session.createQuery(hql).setLong("id", basicid);
				return (InquirySurveybasic) query.uniqueResult();
		
			}
		});
	}

	/**
	 * 获取当前用户在当前调查类型下发布的调查列表
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getSendBasicListByCreator( final long tid, final long uid)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String counthql = "SELECT count(DISTINCT "
				+ " b)  FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquirySurveytype.class.getName()
				+ " t"
				+ " WHERE  b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue() + " AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.createrId=:uid";
		Query querycount = session.createQuery(counthql).setLong(
				"tid", tid).setLong("uid", uid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT "
				+ " b  FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquirySurveytype.class.getName()
				+ " t"
				+ " WHERE  b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue() + " AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.createrId=:uid" + " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid", tid)
				.setLong("uid", uid)
				.setFirstResult(Pagination.getFirstResult()).setMaxResults(
						Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 获取当前用户在当前调查类型下未发布的调查列表
	 * 调查发布人员点击调查发布后所看到的列表
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getNOSendBasicListByCreator( final long tid,
			final long uid , final String condition, final String textfield, final String textfield1 ) throws Exception {
		String querySql = "";
		String startDate = textfield;
		String endDate = textfield1;
		Map params=new HashMap();
		if(condition != null && !condition.equals("")){
			if(condition.equals("subject")){
				querySql = " AND b.surveyName like :textfield";
				//params.put("textfield", "%"+textfield+"%");
				params.put("textfield", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}else if(condition.equals("issueUser")){
				
			}else if(condition.equals("createDate")){
				if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					endDate += " 23:59:59";
					querySql = " AND b.sendDate between :startDate and :endDate ";
					params.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					params.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				} else if (StringUtils.isNotBlank(textfield)
						&& !StringUtils.isNotBlank(textfield1)) {
					startDate += " 00:00:00";
					querySql = " AND b.sendDate > :startDate ";
					params.put("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
				} else if (!StringUtils.isNotBlank(textfield)
						&& StringUtils.isNotBlank(textfield1)) {
					endDate += " 23:59:59";
					querySql = " AND b.sendDate < :endDate ";
					params.put("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
				}
			}
		}
		String hqlStr = "SELECT DISTINCT "
			+ " b  FROM "
			+ InquirySurveybasic.class.getName()
			+ " b, "
			+ InquirySurveytype.class.getName()
			+ " t"
			+ " WHERE  b.surveyTypeId=t.id AND t.id=:tid AND t.flag=:tflag  AND b.flag =:bflag AND b.censor <= :bcensor"
			+ querySql
			+ " AND b.createrId=:uid" + " ORDER BY b.sendDate DESC";
		params.put("tid", tid);
		params.put("tflag", InquirySurveytype.FLAG_NORMAL.intValue());
		params.put("bflag", InquirySurveybasic.FLAG_NORMAL.intValue());
		params.put("bcensor", InquirySurveybasic.CENSOR_PASS.intValue());
		params.put("uid", uid);
		List<Object> paramFlag=null;
		List<InquirySurveybasic> list=this.find(hqlStr, params, paramFlag);
		return list;
	}

	/**
	 * 根据ID获取用户创建的未发布、未归档的调查
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getNOSendBasicByCreator( final long tid, final long uid,
			final long bid) throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT DISTINCT "
				+ " b  FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquirySurveytype.class.getName()
				+ " t"
				+ " WHERE b.id=:bid AND b.surveyTypeId=t.id AND t.id=:tid AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue() + " AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.censor <>"
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND b.censor <>"
				+ InquirySurveybasic.CENSOR_FILING_YES.intValue()
				+ " AND b.createrId=:uid" + " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid", tid)
				.setLong("uid", uid).setLong("bid", bid);
		return (InquirySurveybasic) query.uniqueResult();
			}
		});
	}

	/**
	 * 按标题查找当前用户在当前调查类型下可见 并未过期的调查
	 * 
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @param title
	 *            调查标题
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getUserQuerySurveyByTitle( final String authID,
			final long tid, final String title , final boolean isOtherAccount) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		long accountId = member.getLoginAccount();
//		关于单位的过滤条件
		String accountQuery = "";
		String typeQuery = "";
		if(isOtherAccount){
			//accountQuery = " AND t.accountId!="+member.getLoginAccount()+" AND t.spaceType!="+InquirySurveytype.Space_Type_Group;
			accountQuery = " AND t.accountId <> :accountId AND t.spaceType <>"+InquirySurveytype.Space_Type_Group;
		}else{
			//accountQuery = " AND t.accountId="+member.getLoginAccount();
			accountQuery = " AND t.accountId=:accountId";
//			如果不是外单位的加上板块类型的过滤
			typeQuery = " AND t.id=:tid AND b.inquirySurveytype.id=t.id";
		}
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authID.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		String hqlcount = "SELECT count(DISTINCT "
				+ " b) "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " (s.inquirySurveybasic.id = b.id) AND"
				//+ " ((s.scopeId in ("
				//+ authID
				+ " ((s.scopeId in (:scopeIds)"
				+ " AND b.sendDate < :sendtime  AND b.closeDate > :closetime ) OR (b.createrId =:createrId) ) AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ typeQuery+" AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ accountQuery
				+ " AND b.surveyName like :title";
		
		//Query querycount = session.createQuery(hqlcount).setLong(
		Query querycount = session.createQuery(hqlcount).setParameterList("scopeIds", scopeIds).setLong(
				"createrId", memberid).setTimestamp("sendtime", time)
				.setTimestamp("closetime", time);
		
		if(isOtherAccount){
			//querycount.setString("title", "%" + title + "%");
			querycount.setLong("accountId", accountId).setString("title", "%" + SQLWildcardUtil.escape(title) + "%");
		}else{
			//querycount.setLong("tid", tid).setString("title", "%" + title + "%");
			querycount.setLong("accountId", accountId).setLong("tid", tid).setString("title", "%" + SQLWildcardUtil.escape(title) + "%");
		}
		
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " (s.inquirySurveybasic.id = b.id) AND"
				//+ " (( s.scopeId in ("
				//+ authID
				+ " ((s.scopeId in (:scopeIds)"
				+ " AND b.sendDate < :sendtime  AND b.closeDate > :closetime) OR (b.createrId =:createrId)) AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ typeQuery+" AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ accountQuery
				+ " AND b.surveyName like :title "
				+ " ORDER BY b.sendDate DESC";
		//Query query = session.createQuery(hql).setLong("createrId",
		Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds).setLong("createrId",
				memberid).setTimestamp("sendtime", time).setTimestamp(
				"closetime", time);
		if(isOtherAccount){
			//query.setString("title", "%" + title + "%").setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
			query.setLong("accountId", accountId).setString("title", "%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
		}else{
			//query.setLong("tid", tid).setString("title", "%" + title + "%").setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
			query.setLong("accountId", accountId).setLong("tid", tid).setString("title", "%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
		}
		return query.list();
			}
		});
	}

	/**
	 * 按标题查找在当前调查类型下的调查(当前用户为管理员)
	 * 
	 * @param tid
	 * @param title
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getManagerQuerySurveyByTitle( final long tid,
			final String title) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.surveyName like :title";
		Query querycount = session.createQuery(hqlcount).setLong(
				"tid", tid).setString("title", "%" + SQLWildcardUtil.escape(title) + "%");
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.surveyName like :title "
				+ " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid", tid)
				.setString("title", "%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(
						Pagination.getFirstResult()).setMaxResults(
						Pagination.getMaxResults());
		return query.list();
			}
		});

	}

	/**
	 * 按发布人查找当前用户在当前调查类型下可见 并未过期的调查
	 * 
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @param tid
	 * @param creator
	 *            发布者
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getUserQuerySurveyByCreator( final String authID,
			final long tid, final long uid) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		if (String.valueOf(uid).equals(String.valueOf(memberid))) {// 如果当前查询的用户是当前在线用户
			String hqlcount = "SELECT count(DISTINCT "
				+ " b) "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.createrId=:uid ";

		Query querycount = session.createQuery(hqlcount).setLong("tid", tid).setLong("uid", uid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.createrId=:uid "
				+ " ORDER BY b.sendDate DESC";

		Query query = session.createQuery(hql).setLong(
				"tid", tid).setLong("uid", uid).setFirstResult(
				Pagination.getFirstResult()).setMaxResults(
				Pagination.getMaxResults());
		return query.list();
		} else {
			//HQL语句清理 modified by Meng Yang 2009-05-31
			String[] scopeIdStrs = authID.split(",");
			Long[] scopeIds = new Long[scopeIdStrs.length];
			for (int i = 0; i < scopeIds.length; i++) {
				scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
			}
			
			String hqlcount = "SELECT count(DISTINCT "
					+ " b) "
					+ "FROM "
					+ InquirySurveybasic.class.getName()
					+ " b, "
					+ InquiryScope.class.getName()
					+ " s, "
					+ InquirySurveytype.class.getName()
					+ " t "
					+ " WHERE"
					+ " s.inquirySurveybasic.id = b.id AND"
					//+ " s.scopeId in ("
					//+ authID
					+ " s.scopeId in (:scopeIds"
					+ ") AND  b.flag ="
					+ InquirySurveybasic.FLAG_NORMAL.intValue()
					+ " AND b.censor="
					+ InquirySurveybasic.CENSOR_PASS.intValue()
					+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
					+ InquirySurveytype.FLAG_NORMAL.intValue()
					+ " AND b.createrId=:uid AND b.sendDate < :sendtime  AND b.closeDate > :closetime";

			//Query querycount = session.createQuery(hqlcount)
			Query querycount = session.createQuery(hqlcount).setParameterList("scopeIds", scopeIds)
					.setTimestamp("sendtime", time).setTimestamp("closetime",
							time).setLong("tid", tid).setLong("uid", uid);
			int count = ((Integer) querycount.uniqueResult()).intValue();
			Pagination.setRowCount(count);

			String hql = "SELECT DISTINCT "
					+ " b "
					+ "FROM "
					+ InquirySurveybasic.class.getName()
					+ " b, "
					+ InquiryScope.class.getName()
					+ " s, "
					+ InquirySurveytype.class.getName()
					+ " t "
					+ " WHERE"
					+ " s.inquirySurveybasic.id = b.id AND"
					//+ " s.scopeId in ("
					//+ authID
					+ " s.scopeId in (:scopeIds"
					+ ") AND  b.flag ="
					+ InquirySurveybasic.FLAG_NORMAL.intValue()
					+ " AND b.censor="
					+ InquirySurveybasic.CENSOR_PASS.intValue()
					+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
					+ InquirySurveytype.FLAG_NORMAL.intValue()
					+ " AND b.createrId=:uid AND b.sendDate < :sendtime  AND b.closeDate > :closetime"
					+ " ORDER BY b.sendDate DESC";

			//Query query = session.createQuery(hql).setTimestamp(
			Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds).setTimestamp(
					"sendtime", time).setTimestamp("closetime", time).setLong(
					"tid", tid).setLong("uid", uid).setFirstResult(
					Pagination.getFirstResult()).setMaxResults(
					Pagination.getMaxResults());
			return query.list();
		}
			}
		});
	}

	/**
	 * 按发布人查找当前调查类型下的调查(当前用户为管理员)
	 * 
	 * @param tid
	 * @param creator
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getManagerQuerySurveyByCreator( final long tid,
			final long uid) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t "
				+ " WHERE  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.createrId=:uid ";

		Query querycount = session.createQuery(hqlcount).setLong(
				"tid", tid).setLong("uid", uid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t "
				+ " WHERE  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.createrId=:uid " + " ORDER BY b.sendDate DESC";

		Query query = session.createQuery(hql).setLong("tid", tid)
				.setLong("uid", uid)
				.setFirstResult(Pagination.getFirstResult()).setMaxResults(
						Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 按发布日期查找当前用户在当前调查类型下可见 并未过期的调查
	 * 
	 * @param memberid
	 * @param departmentid
	 * @param levelid
	 * @param postid
	 * @param tid
	 * @param sendDate
	 *            发布日期
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getUserQuerySurveyBySendDate( final String authID,
			final long tid, final Timestamp date_one, final Timestamp date_two) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String[] scopeIdStrs = authID.split(",");
		Long[] scopeIds = new Long[scopeIdStrs.length];
		for (int i = 0; i < scopeIds.length; i++) {
			scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
		}
		
		String hqlcount = "SELECT count(DISTINCT "
				+ " b) "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " s.inquirySurveybasic.id = b.id AND"
				//+ " (( s.scopeId in ("
				//+ authID
				+ " (( s.scopeId in (:scopeIds"
				+ ") AND  b.sendDate < :sendtime  AND b.closeDate > :closetime ) OR (b.createrId =:createrId)) AND b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.sendDate <= :date_two And b.sendDate >= :date_one ";

		//Query querycount = session.createQuery(hqlcount).setLong(
		Query querycount = session.createQuery(hqlcount).setParameterList("scopeIds", scopeIds).setLong(
				"createrId", memberid).setTimestamp("sendtime", time)
				.setTimestamp("closetime", time).setLong("tid", tid)
				.setTimestamp("date_two", date_two).setTimestamp("date_one",
						date_one);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b, "
				+ InquiryScope.class.getName()
				+ " s, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " WHERE"
				+ " s.inquirySurveybasic.id = b.id AND"
				//+ " ((s.scopeId in ("
				//+ authID
				+ " (( s.scopeId in (:scopeIds"
				+ ") AND b.sendDate < :sendtime  AND b.closeDate > :closetime ) OR (b.createrId =:createrId)) AND  b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.sendDate < :date_two And b.sendDate > :date_one "
				+ " ORDER BY b.sendDate DESC";

		//Query query = session.createQuery(hql).setLong("createrId",
		Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds).setLong("createrId",
				memberid).setTimestamp("sendtime", time).setTimestamp(
				"closetime", time).setLong("tid", tid).setTimestamp("date_two",
				date_two).setTimestamp("date_one", date_one).setFirstResult(
				Pagination.getFirstResult()).setMaxResults(
				Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 按发布日期查找在当前调查类型的调查(当前用户为管理员)
	 * 
	 * @param tid
	 * @param date_one
	 * @param date_two
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getManagerQuerySurveyBySendDate( final long tid,
			final Timestamp date_one, final Timestamp date_two) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.sendDate >= :date_one AND b.sendDate <=:date_two";
		
		Query querycount = session.createQuery(hqlcount).setLong(
				"tid", tid).setTimestamp("date_one", date_one).setTimestamp(
				"date_two", date_two);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_PASS.intValue()
				+ " OR b.censor="+InquirySurveybasic.CENSOR_CLOSE.intValue()
				+ " AND t.id=:tid AND b.inquirySurveytype.id=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND b.sendDate >= :date_one AND b.sendDate <= :date_two "
				+ " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid", tid)
				.setTimestamp("date_one", date_one).setTimestamp("date_two",
						date_two).setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 根据当前用户和调查ID获取调查
	 * 
	 * @param uid
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getInquirySurveybasicByCrestorAndID(final long uid,
			final long bid) throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "From " + InquirySurveybasic.class.getName()
				+ " b Where b.id=:bid AND b.createrId=:uid";
		Query query = session.createQuery(hql).setLong("bid", bid)
				.setLong("uid", uid);
		return (InquirySurveybasic) query.uniqueResult();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getInquirySurveybasicByTypeId(final Long typeId) {
		List<InquirySurveybasic> inquiryList = new ArrayList<InquirySurveybasic>();
		String hql = "from InquirySurveybasic where surveyTypeId = ? " + " and flag != 1 and censor != 3 " + " and censor != 10 ";
		Object[] params = new Object[]{typeId};
		inquiryList = getHibernateTemplate().find(hql, params);
		return inquiryList;
	}

	/**
	 * 获取调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getTemplateList( final long memberid)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT Count(b) From "
				+ InquirySurveybasic.class.getName() + " b Where b.flag="
				+ InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";
		Query querycount = session.createQuery(hqlcount).setLong(
				"createrId", memberid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "From " + InquirySurveybasic.class.getName()
				+ " b Where b.flag=" + InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";
		Query query = session.createQuery(hql).setLong("createrId",
				memberid).setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}
	
	/**
	 * 获取单位或者集团调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getSpaceTemplateList( final long memberid ,final String spaceType)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT Count(b) From "
				+ InquirySurveybasic.class.getName() + " b Where b.flag="
				+ InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";
		
		if ("custom".equals(spaceType)) {
			hqlcount+=" and b.censor = " + InquirySurveybasic.CENSOR_CUSTOM_TEM.intValue();
		} else if ("public_custom".equals(spaceType)) {
			hqlcount+=" and b.censor = " + InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_TEM.intValue();
		} else {
			hqlcount+=" and b.censor = " + InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_GROUP_TEM.intValue();
		}
		
		Query querycount = session.createQuery(hqlcount).setLong(
				"createrId", memberid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "From " + InquirySurveybasic.class.getName()
				+ " b Where b.flag=" + InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";
		
		if ("custom".equals(spaceType)) {
			hql+=" and b.censor = " + InquirySurveybasic.CENSOR_CUSTOM_TEM.intValue(); 
		} else if ("public_custom".equals(spaceType)) {
			hql+=" and b.censor = " + InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_TEM.intValue(); 
		} else {
			hql+=" and b.censor = " + InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_GROUP_TEM.intValue(); 
		}
		
		Query query = session.createQuery(hql).setLong("createrId",
				memberid).setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}
	
	/**
	 * 获取单位或者集团调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getAccOrGroupTemplateList( final long memberid ,final String group)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT Count(b) From "
				+ InquirySurveybasic.class.getName() + " b Where b.flag="
				+ InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";
		
		if(group!=null && !group.equals("") && "group".equals(group)){
			hqlcount+=" and b.censor = " + InquirySurveybasic.CENSOR_GROUP_TEM.intValue(); 
		}else{
			hqlcount+=" and b.censor = " + InquirySurveybasic.CENSOR_ACC_TEM.intValue(); 
		}
		
		Query querycount = session.createQuery(hqlcount).setLong(
				"createrId", memberid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "From " + InquirySurveybasic.class.getName()
				+ " b Where b.flag=" + InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.createrId = :createrId";

		if(group!=null && !group.equals("") && "group".equals(group)){
			hql+=" and b.censor = " + InquirySurveybasic.CENSOR_GROUP_TEM.intValue(); 
		}else{
			hql+=" and b.censor = " + InquirySurveybasic.CENSOR_ACC_TEM.intValue(); 
		}
		
		Query query = session.createQuery(hql).setLong("createrId",
				memberid).setFirstResult(Pagination.getFirstResult())
				.setMaxResults(Pagination.getMaxResults());
		return query.list();
			}
		});
	}
	

	/**
	 * 根据ID获取调查
	 * 
	 * @param bid
	 * @param boolean getTemp   区分取出的是否为模板
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getInquirySurveybasicID( final long bid , final boolean getTemp)
			throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				int flagQuery = InquirySurveybasic.FLAG_NORMAL;
				if(getTemp){
					flagQuery = InquirySurveybasic.FLAG_TEM;
				}
				String hql = "From " + InquirySurveybasic.class.getName()
						+ " b Where b.flag = "+flagQuery+" and b.id=:id";
				Query query = session.createQuery(hql).setLong("id", bid);
				return (InquirySurveybasic) query.uniqueResult();
			}
		});
	}
	
	public InquirySurveybasic getInquirySurveybasicByID( final long bid)
			throws Exception {
		return (InquirySurveybasic) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From " + InquirySurveybasic.class.getName()
						+ " b Where b.id=:id";
				Query query = session.createQuery(hql).setLong("id", bid);
				return (InquirySurveybasic) query.uniqueResult();
			}
		});
	}

	/**
	 * 获取当前调查版块的未审核的调查列表
	 * 
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getCheckListByType( final long tid)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT count(DISTINCT b )" + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_NO.intValue()
				+ " AND t.id=:tid AND b.surveyTypeId=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue();
		Query querycount = session.createQuery(hqlcount).setLong(
				"tid", tid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b, "
				+ InquirySurveytype.class.getName() + " t " + " WHERE b.flag ="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
				+ InquirySurveybasic.CENSOR_NO.intValue()
				+ " AND t.id=:tid AND b.surveyTypeId=t.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid", tid)
				.setFirstResult(Pagination.getFirstResult()).setMaxResults(
						Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	// =================================================================
	/**
	 * 按发布时间查询当前用户创建调查
	 * 
	 * @param titles
	 * @param typeid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getBasicByCreatorAndTitle( final String title,
			final String typeid, final long memberid, final boolean b) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String publicOrNoPublic = "";
		if (b) {// 查询发布的调查
			publicOrNoPublic = "b.censor ="
					+ InquirySurveybasic.CENSOR_PASS.intValue();
		} else {// 查询未发布的调查
			publicOrNoPublic = "b.censor >"
					+ InquirySurveybasic.CENSOR_PASS.intValue()
					+ " AND  b.censor <"
					+ InquirySurveybasic.CENSOR_FILING_YES.intValue();
		}
		String hqlcount = "SELECT count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b " + " WHERE"
				+ " b.createrId =:createrId "
				+ " AND b.inquirySurveytype.id =:tid"
				+ " AND b.surveyName like :title" + " AND b.flag="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND "
				+ publicOrNoPublic;
		Query querycount = session.createQuery(hqlcount).setLong(
				"createrId", memberid).setLong("tid", Long.parseLong(typeid))
				.setString("title", "%" + SQLWildcardUtil.escape(title) + "%");
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b " + " WHERE"
				+ " b.createrId =:createrId "
				+ " AND b.inquirySurveytype.id =:tid"
				+ " AND b.surveyName like :title" + " AND b.flag="
				+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND "
				+ publicOrNoPublic + " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("createrId",
				memberid).setLong("tid", Long.parseLong(typeid)).setString(
				"title", "%" + SQLWildcardUtil.escape(title) + "%").setFirstResult(
				Pagination.getFirstResult()).setMaxResults(
				Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 按发布时间查询当前用户创建调查
	 * 
	 * @param date_one：发布时间
	 * @param typeid
	 * @param memberid
	 * @param b
	 *            :ture:为发布状态的，flase为未发布状态的
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getBasicByCreatorAndSendTime(
			final Timestamp date_one, final String typeid, final long memberid,  final boolean b)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String publicOrNoPublic = "";
		if (b) {// 查询发布的调查
			publicOrNoPublic = "b.censor ="
					+ InquirySurveybasic.CENSOR_PASS.intValue();
		} else {// 查询未发布的调查
			publicOrNoPublic = "b.censor >"
					+ InquirySurveybasic.CENSOR_PASS.intValue()
					+ " AND  b.censor <"
					+ InquirySurveybasic.CENSOR_FILING_YES.intValue();
		}
		String hqlcount = "SELECT count(DISTINCT " + " b) " + "FROM "
				+ InquirySurveybasic.class.getName() + " b " + " WHERE "
				+ " b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.inquirySurveytype.id=:tid "
				+ " AND b.sendDate  >=:date_one "
				+ " AND b.createrId =:createrId AND " + publicOrNoPublic;
		
		Query querycount = session.createQuery(hqlcount).setLong(
				"tid", Long.parseLong(typeid)).setTimestamp("date_one",
				date_one).setLong("createrId", memberid);
		int count = ((Integer) querycount.uniqueResult()).intValue();
		Pagination.setRowCount(count);

		String hql = "SELECT DISTINCT " + " b " + "FROM "
				+ InquirySurveybasic.class.getName() + " b " + " WHERE "
				+ " b.flag =" + InquirySurveybasic.FLAG_NORMAL.intValue()
				+ " AND b.inquirySurveytype.id=:tid "
				+ " AND b.sendDate >=:date_one "
				+ " AND b.createrId =:createrId AND " + publicOrNoPublic
				+ " ORDER BY b.sendDate DESC";
		Query query = session.createQuery(hql).setLong("tid",
				Long.parseLong(typeid)).setTimestamp("date_one", date_one)
				.setLong("createrId", memberid).setFirstResult(
						Pagination.getFirstResult()).setMaxResults(
						Pagination.getMaxResults());
		return query.list();
			}
		});
	}

	/**
	 * 判断是否有同名的模板调查
	 * 
	 * @param tid
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> isTheSameName( final String name,final long memberid)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b "
				+ " WHERE"
				+ " b.flag= "
				+ InquirySurveybasic.FLAG_TEM.intValue()
				+ " AND b.surveyName = :surveyName AND b.createrId = :createrId";
		Query query = session.createQuery(hql).setString(
				"surveyName", name).setLong("createrId", memberid);
		return query.list();
			}
		});
	}
	
	/**
	 * 判断是否有同名的调查
	 * 
	 * @param typeid
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> isInquiryExist( final String name, final Long typeId)
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT DISTINCT "
				+ " b "
				+ "FROM "
				+ InquirySurveybasic.class.getName()
				+ " b "
				+ " WHERE b.censor = "+InquirySurveybasic.CENSOR_PASS
				+ " and b.flag= "
				+ InquirySurveybasic.FLAG_NORMAL.intValue() //只判断已发布状态的是否重复
				+ " AND b.surveyName = :surveyName AND b.surveyTypeId = :typeId";
		Query query = session.createQuery(hql).setString(
				"surveyName", name).setLong("typeId", typeId);
		return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<InquirySurveytypeextend> getSerById(Long surveryId,int type){
		
		/*List<InquirySurveytypeextend> list = getHibernateTemplate().find(
				"from InquirySurveytypeextend as ist where ist.inquirySurveytype.id ='"
						+ surveryId + "' and ist.managerDesc='"+type+"'");*/
		
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String hql = "from InquirySurveytypeextend as ist where ist.inquirySurveytype.id =? and ist.managerDesc=?";
		Object[] params = new Object[]{surveryId, type};
		List<InquirySurveytypeextend> list = getHibernateTemplate().find(hql, params);
		return list;
	}
	
	
	/**
	 * 获取所有外单位调查    支持查询
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getALLOtherAccountBasicList( final String authID , final String condition , final String textfield , final String textfield1 )
			throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				//HQL语句清理 modified by Meng Yang 2009-05-31
				String[] scopeIdStrs = authID.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				
				String querySql = "";
				String startDate = textfield;
				String endDate = textfield1;
				if(condition != null && !condition.equals("")){
					if(condition.equals("subject")){
						//querySql = " AND b.surveyName like '%"+textfield+"%'";
						querySql = " AND b.surveyName like :surveyName ";
					}else if(condition.equals("createDate")){
						if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
							startDate += " 00:00:00";
							endDate += " 23:59:59";
							querySql = " AND b.sendDate between :startDate and :endDate ";
						} else if (StringUtils.isNotBlank(textfield)
								&& !StringUtils.isNotBlank(textfield1)) {
							startDate += " 00:00:00";
							querySql = " AND b.sendDate > :startDate ";
						} else if (!StringUtils.isNotBlank(textfield)
								&& StringUtils.isNotBlank(textfield1)) {
							endDate += " 23:59:59";
							querySql = " AND b.sendDate < :endDate ";
						}
					}
				}
				
				String count = "SELECT Count(DISTINCT " + " b) " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s" + " WHERE"
						+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
						//+ authID + ") AND  b.flag ="
						+ ":scopeIds) AND  b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
						+ InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.inquirySurveytype.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						//+ " AND b.inquirySurveytype.accountId !="+CurrentUser.get().getLoginAccount()
						+ " AND b.inquirySurveytype.accountId <> :accountId "
						+ querySql;
				
				Long accountId = CurrentUser.get().getLoginAccount();
				
				//Query queryCount = session.createQuery(count);
				Query queryCount = session.createQuery(count).setParameterList("scopeIds", scopeIds).setLong("accountId", accountId);
				if(condition != null && !condition.equals("") && condition.equals("createDate")){
					if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
						queryCount.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
						queryCount.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
					} else if (StringUtils.isNotBlank(textfield)
							&& !StringUtils.isNotBlank(textfield1)) {
						queryCount.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					} else if (!StringUtils.isNotBlank(textfield)
							&& StringUtils.isNotBlank(textfield1)) {
						queryCount.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
					}
				} else if(condition != null && !condition.equals("") && condition.equals("subject")) {
					queryCount.setString("surveyName", "%" + SQLWildcardUtil.escape(textfield) + "%");
				}
				
				
				int blistCount = ((Integer) queryCount.uniqueResult()).intValue();
				Pagination.setRowCount(blistCount);

				String hql = "SELECT DISTINCT " + " b " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s" + " WHERE"
						+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
						//+ authID + ") AND  b.flag ="
						+ ":scopeIds) AND  b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
						+ InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.inquirySurveytype.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						//+ " AND b.inquirySurveytype.accountId !="+CurrentUser.get().getLoginAccount()
						+ " AND b.inquirySurveytype.accountId <> :accountId "
						+ " AND b.inquirySurveytype.spaceType !="+InquirySurveytype.Space_Type_Group
						+ querySql
						+ " ORDER BY b.sendDate DESC";
				//Query query = session.createQuery(hql);
				Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds).setLong("accountId", accountId);
				if(condition != null && !condition.equals("") && condition.equals("createDate")){
					if (StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)) {
						query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
						query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
					} else if (StringUtils.isNotBlank(textfield)
							&& !StringUtils.isNotBlank(textfield1)) {
						query.setTimestamp("startDate", new Timestamp(Datetimes.parseDate(startDate).getTime()));
					} else if (!StringUtils.isNotBlank(textfield)
							&& StringUtils.isNotBlank(textfield1)) {
						query.setTimestamp("endDate", new Timestamp(Datetimes.parseDate(endDate).getTime()));
					} else if(condition != null && !condition.equals("") && condition.equals("subject")) {
						query.setString("surveyName", "%" + SQLWildcardUtil.escape(textfield) + "%");
					} 
				}
				query.setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}
	
	
	/**
	 * 获取所有外单位调查总数
	 * @return
	 * @throws Exception
	 */
	public int getALLOtherAccountBasicCount( final String authID )
			throws Exception {
		int count =  (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				//HQL语句清理 modified by Meng Yang 2009-05-31
				String[] scopeIdStrs = authID.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				Long accountId = CurrentUser.get().getLoginAccount();
				
				String count = "SELECT Count(DISTINCT " + " b) " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s" + " WHERE"
						+ " (s.inquirySurveybasic.id = b.id) AND" + " s.scopeId in ("
						//+ authID + ") AND  b.flag ="
						+ ":scopeIds) AND  b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
						+ InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.inquirySurveytype.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue()
						//+ " AND b.inquirySurveytype.accountId !="+CurrentUser.get().getLoginAccount()
						+ " AND b.inquirySurveytype.accountId != :accountId "
						+ " AND b.inquirySurveytype.spaceType !="+InquirySurveytype.Space_Type_Group;
				
				//Query queryCount = session.createQuery(count);
				Query queryCount = session.createQuery(count).setParameterList("scopeIds", scopeIds).setLong("accountId", accountId);
				
				int qcount = ((Integer) queryCount.uniqueResult()).intValue();
				return qcount;
			}
		});
		return count;
	}
	
	/*
	 * 按板块查有权限看的最新6条调查
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> getALLBasicListByTypeId( final String authID , final Long typeId,final Long memberId) throws Exception {
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				String hql = "SELECT DISTINCT " + " b " + "FROM "
						+ InquirySurveybasic.class.getName() + " b,"
						+ InquiryScope.class.getName() + " s  WHERE b.surveyTypeId = :typeId"
						+ " and (s.inquirySurveybasic.id = b.id) AND " + " (s.scopeId in (:scopeIds) or b.createrId = :createrId)"
						+ " AND  b.flag = " + InquirySurveybasic.FLAG_NORMAL.intValue()
						//显示正在进行中和已经结束的调查
						+ " AND (b.censor=" + InquirySurveybasic.CENSOR_PASS + " or b.censor=" + InquirySurveybasic.CENSOR_CLOSE + ")"
						+ " ORDER BY b.sendDate DESC";
				
				String[] scopeIdStrs = authID.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}
				
				Query query = session.createQuery(hql).setLong("typeId", typeId).setParameterList("scopeIds", scopeIds).setLong("createrId", memberId).setFirstResult(0).setMaxResults(6);
				return query.list();
			}
		});
	}
	
	/**
     * 分页显示用户查看权限内的最新n条信息
     * 
     * @param authID
     * @param accountId
     * @param personId
     * @param firstNum
     * @param size
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<InquirySurveybasic> getInquiryBasicListByUserScopeByRecent(final String authID,
			final long accountId, final long personId, final int firstNum, final int size)
			throws Exception
	{
		return (List<InquirySurveybasic>) getHibernateTemplate().execute(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				// 判断版本是否显示集团信息
				boolean flag = (Boolean) (SysFlag.inquiry_showOtherAccountInquiry.getFlag());
				String authIDs = authID;
				if (flag) {
					Long rootAccountId = null;
					try {
						rootAccountId = orgManager.getRootAccount().getId();
					}
					catch (BusinessException e) {
						log.error("获取根单位失败", e);
					}
					authIDs += "," + rootAccountId;
				}
				String hql = "SELECT DISTINCT "
						+ " b "
						+ "FROM "
						+ InquirySurveybasic.class.getName()
						+ " b,"
						+ InquiryScope.class.getName()
						+ " s , InquirySurveytype t "
						+ " WHERE"
						// + " s.inquirySurveybasic.id = b.id AND ( s.scopeId IN
						// (" + authIDs
						// + ") or b.createrId=:createUserId ) AND b.flag =" +
						// InquirySurveybasic.FLAG_NORMAL.intValue()
						// HQL语句清理 modified by Meng Yang 2009-05-27
						+ " s.inquirySurveybasic.id = b.id AND ( s.scopeId IN (:scopeIds) "
						+ "or b.createrId=:createUserId ) AND  b.flag ="
						+ InquirySurveybasic.FLAG_NORMAL.intValue() + " AND b.censor="
						+ InquirySurveybasic.CENSOR_PASS.intValue()
						+ " AND b.surveyTypeId = t.id and t.flag="
						+ InquirySurveytype.FLAG_NORMAL.intValue() + " AND t.spaceType!="
						+ InquirySurveytype.Space_Type_Group // 集团类型的过滤掉
						// + " AND t.accountId ="+accountId
						// //加了个单位判断，兼职查看单位空间的信息过滤掉原单位数据
						+ " AND t.accountId =:accountId" // 加了个单位判断，兼职查看单位空间的信息过滤掉原单位数据
						+ " ORDER BY b.sendDate DESC";
				// Query query =
				// session.createQuery(hql).setLong("createUserId",
				// CurrentUser.get().getId()).setFirstResult(0).setMaxResults(size);
				String[] scopeIdStrs = authIDs.split(",");
				Long[] scopeIds = new Long[scopeIdStrs.length];
				for (int i = 0; i < scopeIds.length; i++) {
					scopeIds[i] = Long.parseLong(scopeIdStrs[i]);
				}

				Query query = session.createQuery(hql).setParameterList("scopeIds", scopeIds)
						.setLong("createUserId", personId).setLong("accountId", accountId)
						.setFirstResult(0).setMaxResults(size);
				return query.list();
			}
		});
	}
    
    /**
	 * 将某一指定调查板块下待审核的调查对应待办事项转到新审核员名下<br>
	 * 由于旧的待办事项可能是较早以前的，在转移时，将其时间改为当前时间，便于新的审核员在其待办事项最开始几项中看到<br>
	 * 这种情况发生的场景：旧审核员离职了，而其具有审核权的调查板块还有待审核调查<br>
	 * @param typeId        调查板块ID
	 * @param oldCheckerId  旧审核员ID
	 * @param newCheckerId  新审核员ID
	 */
	public void transfer2NewAuditor(Long typeId, Long oldCheckerId, Long newCheckerId) {
		String hql = "update " + Affair.class.getName() + " as af set af.memberId=:newCheckerId, af.createDate=:now, af.receiveTime=:now where af.app=:inquiry and " +
		 			 "af.memberId=:oldCheckerId and af.objectId in (select inq.id from " + InquirySurveybasic.class.getName() + " as inq " +
		 			 "where inq.surveyTypeId=:typeId and inq.censor=:wait4Audit)";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newCheckerId", newCheckerId);
		params.put("inquiry", ApplicationCategoryEnum.inquiry.key());
		params.put("oldCheckerId", oldCheckerId);
		params.put("typeId", typeId);
		params.put("wait4Audit", InquirySurveybasic.CENSOR_NO);
		params.put("now", new Timestamp(System.currentTimeMillis()));
		super.bulkUpdate(hql, params);
		
	}
	
	/**
	 * 修改某一指定调查板块下的待审核调查的审核员
	 * @param typeId        板块ID
	 * @param newCheckerId  新审核员ID
	 */
	public void updateInquiryChecker(Long typeId, Long newCheckerId) {
		String hql = "update " + InquirySurveybasic.class.getName() + " as a set a.censorId=:censorId where a.surveyTypeId=:surveyTypeId and a.censor=:censor";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("censorId", newCheckerId);
		paramMap.put("surveyTypeId", typeId);
		paramMap.put("censor", ConstantsInquiry.INQUIRY_NO_AUDIT);
		super.bulkUpdate(hql, paramMap);
	}

	/**
	 * 从给定的调查中过滤出已结束的调查，配合归档使用：只有已结束的调查才允许归档
	 * @param ids
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getEndedInquiryIds(List<Long> ids) {
		String hql = "select inq.id from " + InquirySurveybasic.class.getName() + " as inq where inq.id in (:ids) and inq.censor=:ended ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		params.put("ended", InquirySurveybasic.CENSOR_CLOSE);
		return (List<Long>)super.find(hql, -1, -1, params);
	}
}
