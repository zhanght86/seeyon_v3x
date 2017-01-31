package com.seeyon.v3x.meeting.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtReplyDao;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 会议回执的Manager的实现类
 * @author wolf
 *
 */
public class MtReplyManagerImpl extends BaseMeetingManager implements MtReplyManager {
	private MtReplyDao mtReplyDao;
	private AffairManager affairManager;
	private OrgManager orgManager;
	private static final Log log = LogFactory.getLog(MtReplyManagerImpl.class);
	
	public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }
	
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public MtReplyDao getMtReplyDao() {
		return mtReplyDao;
	}
	
	public void setMtReplyDao(MtReplyDao mtReplyDao) {
		this.mtReplyDao = mtReplyDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		mtReplyDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}
	
	/**
	 * 删除一条会议对应的全部回执记录
	 * @param meetingId
	 */
	public void deleteByMeetingId(Long meetingId) {
		this.mtReplyDao.delete(new Object[][]{{"meetingId", meetingId}});
	}
	
	/**
	 * 在修改会议时，删除被取消与会对象的会议回执记录
	 * @param meetingId 		会议ID
	 * @param reducedConferees	被取消的与会对象集合
	 */
	public void deleteByMeetingId(Long meetingId, List<Long> reducedConferees) {
		this.mtReplyDao.deleteCanceledRecords(meetingId, reducedConferees);
	}

	/**
	 * 初始化会议回执列表
	 * @param list
	 */
	private void initList(List<MtReply> list){
		for(MtReply template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化会议回执 1、初始化创建用户姓名 2、如果不是本单位人员初始化单位名称
	 * @param template
	 */
	private void initTemplate(MtReply template) {
		User user = CurrentUser.get();
		Long loginAccountId = user.getLoginAccount();
		Long userAccountId = null;
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(template.getUserId());
		} catch (BusinessException e) {
			log.error("获取人员失败", e);
		}
		if(member!=null && !member.getIsDeleted()) {
			userAccountId = member.getOrgAccountId();
			template.setUserName(this.getMeetingUtils().getMemberNameByUserId(template.getUserId()));
			if(loginAccountId.intValue()!=userAccountId.intValue()){
				template.setUserAccountName(this.getMeetingUtils().getMemberAccountNameByUserId(template.getUserId()));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtReply> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(MtReply.class);
		List<MtReply> list=this.mtReplyDao.getHibernateTemplate().findByCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtReply> findByProperty(String property, Object value) {
		List<MtReply> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtReply> findByPropertyNoInit(String property, Object value) {
		List<MtReply> list;
		DetachedCriteria dc=DetachedCriteria.forClass(MtReply.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		list=this.getMtReplyDao().getHibernateTemplate().findByCriteria(dc.addOrder(Order.asc("readDate")));
//		list=this.getMtReplyDao().paginate(dc.getExecutableCriteria(this.getMtReplyDao().getHibernateTemplate().getSessionFactory().getCurrentSession()));
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#getById(java.lang.Long)
	 */
	public MtReply getById(Long id) {
		MtReply template= mtReplyDao.get(id);
		//MtReply template= (MtReply) mtReplyDao.getHibernateTemplate().get(MtReply.class, id);
		template.setUserName(this.getMeetingUtils().getMemberNameByUserId(template.getUserId()));
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtReplyManager#save(com.seeyon.v3x.news.domain.MtReply)
	 */
	public MtReply save(MtReply template) throws MeetingException {
		if(template.isNew()){
			template.setIdIfNew();
			template.setReadDate(new Date());
			mtReplyDao.save(template);
		}else{
			template.setReadDate(new Date());
			mtReplyDao.update(template);
		}
		//回执意见为不参加时将affair状态置为不参加
		if(template.getFeedbackFlag()==Constants.FEEDBACKFLAG_UNATTEND){
			List<Affair> al = affairManager.findbymemberIdAndSummaryId(template.getUserId(), template.getMeetingId());
			if(al!=null&&al.size()!=0){
				al.get(0).setState(StateEnum.mt_unAttend.getKey());
			}
		}else{
			List<Affair> al = affairManager.findbymemberIdAndSummaryId(template.getUserId(), template.getMeetingId());
			if(al!=null&&al.size()!=0){
				al.get(0).setState(StateEnum.col_pending.getKey());
			}
		}
		return template;
	}

	public void reply(long meetingId, Long uid, String opinion, int attitude) throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MtReply.class);
		criteria.add(Expression.eq("meetingId", meetingId));
		criteria.add(Expression.eq("userId", uid));
		
		MtReply reply = (MtReply)mtReplyDao.executeUniqueCriteria(criteria);
		reply.setFeedback(opinion);
		reply.setFeedbackFlag(attitude);
		reply.setReadDate(new Date());
		
		mtReplyDao.update(reply);
	}

	public List<MtReply> findByMeetingIdAndUserId(Long meetingId,Long userId) {
		return this.findByMeetingIdAndUserId(meetingId, userId, Constants.ReplyType.all);
	}
	
	public List<MtReply> findByMeetingIdAndUserId(Long meetingId,Long userId, Constants.ReplyType replyType) {
		return this.mtReplyDao.findByMeetingIdAndUserId(meetingId, userId, replyType);
	}
	
	public int getReplyTypeMtNum(Long memberId, int... type) {
	int num = 0;
		for(int i=0;i<type.length;i++){
			num += getReplayTypeMtNum(memberId,type[i]);
		}
		return num;
	}

	private int getReplayTypeMtNum(Long memberId,int type){
		if(type!=-100){
			DetachedCriteria dc=DetachedCriteria.forClass(MtReply.class);
			dc.add(Restrictions.eq("userId",memberId));
			dc.add(Restrictions.eq("feedbackFlag",type));
			
			List<MtReply> list = this.getMtReplyDao().getHibernateTemplate().findByCriteria(dc);
			int num = 0;
			for(MtReply re:list){
				List<Affair> al = affairManager.findbymemberIdAndSummaryId(memberId, re.getMeetingId());
				num = num + (al!=null?al.size():0);
			}
			return num;
		}else{
			List<Affair> all = affairManager.queryAffairList(memberId,ApplicationCategoryEnum.meeting,StateEnum.col_pending.key());
			
			List<Affair> newAll = new ArrayList<Affair>();
			for(Affair affair : all){
				DetachedCriteria dc = DetachedCriteria.forClass(MtReply.class);
				dc.add(Restrictions.eq("userId",memberId));
				dc.add(Restrictions.eq("meetingId",affair.getObjectId()));
				List<MtReply> list = this.getMtReplyDao().getHibernateTemplate().findByCriteria(dc);
				if(list==null || list.size()==0){
					newAll.add(affair);
				}
			}
			return newAll.size();
		}
	}

	public List<Affair> getAffairBySearchType(Long memberId, Integer searchType) {
		List<Affair> l = new ArrayList<Affair>();
		switch(searchType){
		case 1:
			l =  getListAffair(memberId,1);
			break;
		case 2:
			l = getListAffair(memberId,new int[]{-1,-100});
			break;
		case -1:
			l = getListAffair(memberId,new int[]{1,-1,-100});
			break;
		}
		return l;
	}
	
	private List<Affair> getListAffair(Long memberId,int... type){
		List<Affair> l = new ArrayList<Affair>();
		for(int i=0;i<type.length;i++){
			l.addAll(getListAffair(memberId,type[i]));
		}
		return l;
	}
	
	private List<Affair> getListAffair(Long memberId,int type){
		List<Affair> all = new ArrayList<Affair>();
		if(type!=-100){
			DetachedCriteria dc=DetachedCriteria.forClass(MtReply.class);
			dc.add(Restrictions.eq("userId",memberId));
			dc.add(Restrictions.eq("feedbackFlag",type));
			
			List<MtReply> list = this.getMtReplyDao().getHibernateTemplate().findByCriteria(dc);
			for(MtReply re:list){
				all.addAll(affairManager.findbymemberIdAndSummaryId(memberId, re.getMeetingId()));
			}
			return all;
		}else{
			all = affairManager.queryAffairList(memberId,ApplicationCategoryEnum.meeting,StateEnum.col_pending.key());
			
			List<Affair> newAll = new ArrayList<Affair>();
			for(Affair affair : all){
				DetachedCriteria dc = DetachedCriteria.forClass(MtReply.class);
				dc.add(Restrictions.eq("userId",memberId));
				dc.add(Restrictions.eq("meetingId",affair.getObjectId()));
				List<MtReply> list = this.getMtReplyDao().getHibernateTemplate().findByCriteria(dc);
				if(list==null || list.size()==0){
					newAll.add(affair);
				}
			}
			return newAll;
		}
	}
}