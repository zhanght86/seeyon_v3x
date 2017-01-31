package com.seeyon.v3x.meetingroom.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.cap.office.admin.manager.AdminManagerCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meetingroom.domain.MeetingRoom;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomApp;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomPerm;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomRecord;
import com.seeyon.v3x.meetingroom.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class MeetingRoomDaoImpl extends BaseHibernateDao implements MeetingRoomDao{
	
	private AdminManagerCAP adminManagerCAP;
	
	public void setAdminManagerCAP(AdminManagerCAP adminManagerCAP) {
		this.adminManagerCAP = adminManagerCAP;
	}

	public void save(MeetingRoom mr){
		super.save(mr);
	}
	
	public void save(MeetingRoomApp mra){
		super.save(mra);
	}
	
	public void save(MeetingRoomPerm mrp){
		super.save(mrp);
	}
	
	public MeetingRoom loadMeetingRoom(Long id) throws Exception{
		return (MeetingRoom)super.getHibernateTemplate().load(MeetingRoom.class, id);
	}
	
	public MeetingRoomPerm loadMeetingRoomPerm(Long id)throws Exception{
		MeetingRoomPerm mrp = (MeetingRoomPerm)super.getHibernateTemplate().load(MeetingRoomPerm.class, id);
		try{
			super.getHibernateTemplate().initialize(mrp);
		}catch(Exception ex){
			mrp = null;
		}
		return mrp;
	}
	
	public MeetingRoomApp loadMeetingRoomApp(Long id)throws Exception{
		MeetingRoomApp mra = (MeetingRoomApp)super.getHibernateTemplate().load(MeetingRoomApp.class, id); 
		try{
			super.getHibernateTemplate().initialize(mra);
		}catch(Exception ex){
			mra = null;
		}
		return mra;
	}
	
	public MeetingRoomRecord loadMeetingRoomRecord(Long id)throws Exception{
		return (MeetingRoomRecord)super.getHibernateTemplate().load(MeetingRoomApp.class, id);
	}

	public void update(MeetingRoom mr){
		super.update(mr);
	}
	
	public void update(MeetingRoomApp mra){
		super.update(mra);
	}
	
	public void update(MeetingRoomPerm mrp){
		super.update(mrp);
	}

	public List find(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status, Integer delFlag, Boolean isPage)throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoom.class);
		criteria = this.createQueryCondition(criteria, v3xOrgMember, accountId, name, place, seatCount, needApp, status, delFlag);
		criteria.addOrder(Order.desc("createDatetime"));
		List list = null;
		int count = this.findCount(v3xOrgMember, accountId, name, place, seatCount, needApp, status, delFlag);
		Pagination.setRowCount(count);
		if(isPage){
			list = super.executeCriteria(criteria, Pagination.getFirstResult(), Pagination.getMaxResults());
		}else{
			list = super.executeCriteria(criteria, 0, count);
		}
		return list;
	}
	
	public int findCount(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status, Integer delFlag)throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoom.class);
		criteria = this.createQueryCondition(criteria, v3xOrgMember, accountId, name, place, seatCount, needApp, status, delFlag);
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public List findApp(List v3xOrgMembers, String name, Integer[] seatCount, Boolean isPage){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoom.class);
		criteria.add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		criteria.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		this.createQueryCondition(criteria, null, null, name, null, seatCount, Constants.Type_MeetingRoom_NeedApp, Constants.Status_MeetingRoom_Normal, Constants.DelFlag_No);
		criteria.addOrder(Order.desc("createDatetime"));
		int count = this.findAppCount(null, name, seatCount);
		Pagination.setRowCount(count);
		List list = null;
		if(isPage){
			list = super.executeCriteria(criteria, Pagination.getFirstResult(), Pagination.getMaxResults());
		}else{
			list = super.executeCriteria(criteria, 0, count);
		}
		return list;
	}

	public int findAppCount(List v3xOrgMembers, String name, Integer[] seatCount){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoom.class);
//		criteria.add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		this.createQueryCondition(criteria, null, null, name, null, seatCount, Constants.Type_MeetingRoom_NeedApp, Constants.Status_MeetingRoom_Normal, Constants.DelFlag_No);
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public List findForPerm(V3xOrgMember v3xOrgMember, MeetingRoom mr, V3xOrgMember appMember, Integer isAllowed){
		//int count = this.findForPermCount(v3xOrgMember, mr, appMember, isAllowed);
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomPerm.class);
		criteria.add(Restrictions.eq("delFlag", Constants.DelFlag_No));
		DetachedCriteria mraCriteria = criteria.createCriteria("meetingRoomApp");
		mraCriteria.createCriteria("meetingRoom").add(Restrictions.eq("v3xOrgMember", v3xOrgMember)).add(Restrictions.eq("delFlag",Constants.DelFlag_No));
		//mraCriteria.createCriteria("v3xOrgDepartment").add(Restrictions.in("id", departId));
		if(mr != null){
			mraCriteria.add(Restrictions.eq("meetingRoom", mr));
		}
		if(isAllowed != null){
			criteria.add(Restrictions.eq("isAllowed", isAllowed));
		}
		if(appMember != null){
			mraCriteria.add(Restrictions.eq("v3xOrgMember", appMember));
		}
		criteria.addOrder(Order.asc("isAllowed"));
		mraCriteria.addOrder(Order.desc("startDatetime"));
		//Pagination.setRowCount(count);
		List list = super.executeCriteria(criteria);
		return list;
	}
	
	public int findForPermCount(V3xOrgMember v3xOrgMember, MeetingRoom mr, V3xOrgMember appMember, Integer isAllowed){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomPerm.class);
		criteria.add(Restrictions.eq("delFlag", Constants.DelFlag_No));
		DetachedCriteria mraCriteria = criteria.createCriteria("meetingRoomApp");
		mraCriteria.createCriteria("meetingRoom").add(Restrictions.eq("v3xOrgMember", v3xOrgMember));
		if(mr != null){
			mraCriteria.add(Restrictions.eq("meetingRoom", mr));
		}
		if(isAllowed != null){
			criteria.add(Restrictions.eq("isAllowed", isAllowed));
		}
		if(appMember != null){
			mraCriteria.add(Restrictions.eq("v3xOrgMember", appMember));
		}
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public int checkAppCount(MeetingRoom mr, Date startDatetime, Date endDatetime){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteria.add(Restrictions.eq("meetingRoom", mr));
		criteria.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteria.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public List checkAppPermList(MeetingRoom mr, Date startDatetime, Date endDatetime){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteria.add(Restrictions.eq("meetingRoom", mr));
		criteria.add(Restrictions.eq("status", Constants.Status_App_Wait));
		criteria.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		List list = super.executeCriteria(criteria);
		return list;
	}
	
	public List findUseDetailsByDay(List v3xOrgMember, Date startDatetime, Date endDatetime, boolean pageFlag){
		int count1 = this.findAppUseDetailsByDayCount(v3xOrgMember, startDatetime, endDatetime);
		int count2 = this.findNoAppUseDetailsByDayCount(v3xOrgMember, startDatetime, endDatetime);
		DetachedCriteria criteriaApp = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaApp.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteriaApp.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteriaApp.add(Restrictions.isNull("meeting"));
		criteriaApp.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteriaApp.addOrder(Order.asc("startDatetime"));
		
		DetachedCriteria criteriaNoApp = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaNoApp.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteriaNoApp.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteriaNoApp.addOrder(Order.asc("startDatetime"));
		
		List list = new ArrayList();
		List listApp = null;
		List listNoApp = null;
		Pagination.setRowCount(count1 + count2);
		int max1 = count1;
		int max2 = count2;
		if(pageFlag){
			if((Pagination.getFirstResult()+Pagination.getMaxResults()) <= count1){
				listApp = super.executeCriteria(criteriaApp, Pagination.getFirstResult(), Pagination.getMaxResults());
			}else if(Pagination.getFirstResult() < count1){
				listApp = super.executeCriteria(criteriaApp, Pagination.getFirstResult(), count1);
				listNoApp = super.executeCriteria(criteriaNoApp, 0, Pagination.getMaxResults() - count1);
			}else{
				listNoApp = super.executeCriteria(criteriaNoApp, Pagination.getFirstResult() - count1, Pagination.getMaxResults());
			}
		}else{
			listApp = super.executeCriteria(criteriaApp, 0, max1);
			listNoApp = super.executeCriteria(criteriaNoApp, 0, max2);
		}
		if(listApp != null && listApp.size() > 0){
			for(Object obj : listApp){
				list.add(obj);
			}
		}
		if(listNoApp != null && listNoApp.size() > 0){
			for(Object obj : listNoApp){
				list.add(obj);
			}
		}
		return list;
	}

	public List findUseDetailsByDay(List v3xOrgMember, Date startDatetime, Date endDatetime, List meetingRoom, boolean pageFlag){
		int count1 = this.findAppUseDetailsByDayCount(v3xOrgMember, startDatetime, endDatetime);
		int count2 = this.findNoAppUseDetailsByDayCount(v3xOrgMember, startDatetime, endDatetime);
		DetachedCriteria criteriaApp = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaApp.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteriaApp.add(Restrictions.in("meetingRoom", meetingRoom));
		criteriaApp.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteriaApp.add(Restrictions.isNull("meeting"));
		criteriaApp.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteriaApp.addOrder(Order.asc("startDatetime"));
		
		DetachedCriteria criteriaNoApp = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaNoApp.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteriaNoApp.add(Restrictions.in("meetingRoom", meetingRoom));
		criteriaNoApp.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteriaNoApp.addOrder(Order.asc("startDatetime"));
		
		List list = new ArrayList();
		List listApp = null;
		List listNoApp = null;
		Pagination.setRowCount(count1 + count2);
		int max1 = count1;
		int max2 = count2;
		if(pageFlag){
			if((Pagination.getFirstResult()+Pagination.getMaxResults()) <= count1){
				listApp = super.executeCriteria(criteriaApp, Pagination.getFirstResult(), Pagination.getMaxResults());
			}else if(Pagination.getFirstResult() < count1){
				listApp = super.executeCriteria(criteriaApp, Pagination.getFirstResult(), count1);
				listNoApp = super.executeCriteria(criteriaNoApp, 0, Pagination.getMaxResults() - count1);
			}else{
				listNoApp = super.executeCriteria(criteriaNoApp, Pagination.getFirstResult() - count1, Pagination.getMaxResults());
			}
		}else{
			listApp = super.executeCriteria(criteriaApp, 0, max1);
			listNoApp = super.executeCriteria(criteriaNoApp, 0, max2);
		}
		if(listApp != null && listApp.size() > 0){
			for(Object obj : listApp){
				list.add(obj);
			}
		}
		if(listNoApp != null && listNoApp.size() > 0){
			for(Object obj : listNoApp){
				list.add(obj);
			}
		}
		return list;
	}
	private int findAppUseDetailsByDayCount(List v3xOrgMember, Date startDatetime, Date endDatetime){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteria.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteria.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteria.add(Restrictions.isNull("meeting"));
		criteria.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	private int findNoAppUseDetailsByDayCount(List v3xOrgMember, Date startDatetime, Date endDatetime){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteria.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMember));
		criteria.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria));
		return count;
	}
	
	public void clearPerm(List id){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomPerm.class);
		criteria.add(Restrictions.in("appId", id));
		List list = super.executeCriteria(criteria);
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				MeetingRoomPerm mrp = (MeetingRoomPerm)list.get(i);
				mrp.setDelFlag(Constants.DelFlag_Yes);
				super.update(mrp);
			}
		}
	}
	
	public boolean checkMeetingRoomName(Long id, String name)throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoom.class);
		criteria.add(Restrictions.eq("delFlag", Constants.DelFlag_No));
		criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		if(id != null){
			criteria.add(Restrictions.not(Restrictions.eq("id", id)));
		}
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria));
		if(count > 0){
			return false;
		}
		return true;
	}
	
	public boolean checkAdmin(Long id){
		return adminManagerCAP.checkAdmin(id);
	}
	
	public List getMyAdmin(List departmentId){
		return adminManagerCAP.getMyAdmin(departmentId);
	}
	
	public List getCancals(V3xOrgMember adminMember, V3xOrgMember appMember, MeetingRoom mr, Integer isAllowed){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		Criterion c_EndDatetime = Restrictions.gt("endDatetime", new Date());
		Criterion c_IsNull = Restrictions.isNull("meeting");
		Criterion c_And = Restrictions.and(c_EndDatetime, c_IsNull);
		if(appMember != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("v3xOrgMember", appMember));
		}
		if(mr != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("meetingRoom", mr));
		}
		if(isAllowed != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("status", isAllowed));
		}
		criteria.add(c_And);
		if(adminMember != null){
			Criterion c_Or = Restrictions.or(Restrictions.eq("v3xOrgMember", adminMember), Restrictions.not(Restrictions.eq("id", new Long(0))));
			criteria.createCriteria("meetingRoom").add(c_Or);
		}
		criteria.addOrder(Order.asc("startDatetime"));
		int count = this.getCancelsCount(adminMember, appMember, mr, isAllowed);
		Pagination.setRowCount(count);
		List list = super.executeCriteria(criteria, Pagination.getFirstResult(), Pagination.getMaxResults());
		return list;
	}
	
	/**
	 * 获取会议室管理员所能查看的预定撤销会议申请列表：自己发起的申请的和他人对自己所管理会议室发出的申请
	 * @param adminId    	会议室管理员ID
	 * @param meetingRoomId	在查询时指定某个会议室
	 * @param state			在查询时指定申请审核状态
	 * @param applicantId   在查询时指定申请人
	 */
	public List<MeetingRoomApp> getCanCanceledMeetingRoomApps4Admin(Long adminId, Long meetingRoomId, Integer state, Long applicantId) {
		return this.getCanCanceledMeetingRoomApps(adminId, meetingRoomId, state, applicantId);
	}
	
	/**
	 * 获取普通用户（非会议室管理员）所能查看的预定撤销会议申请列表：自己发起的申请
	 * @param userId        当前用户（非会议室管理员）ID
	 * @param meetingRoomId 在查询时指定某个会议室
	 * @param state			在查询时指定申请审核状态
	 */
	public List<MeetingRoomApp> getCanCanceledMeetingRoomApps4User(Long userId, Long meetingRoomId, Integer state) {
		return this.getCanCanceledMeetingRoomApps(null, meetingRoomId, state, userId);
	}
	
	private List<MeetingRoomApp> getCanCanceledMeetingRoomApps(Long adminId, Long meetingRoomId, Integer state, Long applicantId) {
		StringBuffer hql = new StringBuffer("from " + MeetingRoomApp.class.getName() + " as mra where ");
		Map<String, Object> params = new HashMap<String, Object>();
		if(adminId!=null) {
			hql.append(" (mra.v3xOrgMember.id=:adminId or mra.meetingRoom.v3xOrgMember.id=:adminId) ");
			params.put("adminId", adminId);
		} else {
			hql.append(" (mra.v3xOrgMember.id=:applicantId) ");
			params.put("applicantId", applicantId);
		}
		if(meetingRoomId!=null) {
			hql.append(" and mra.meetingRoom.id=:meetingRoomId ");
			params.put("meetingRoomId", meetingRoomId);
		}
		if(state!=null) {
			hql.append(" and mra.status=:state ");
			params.put("state", state);
		}
		if(applicantId!=null && adminId!=null) {
			hql.append(" and mra.v3xOrgMember.id=:applicantId ");
			params.put("applicantId", applicantId);
		}
		
		hql.append(" and mra.meeting.id=null and mra.endDatetime >:now order by mra.startDatetime asc");
		params.put("now", new Date());
		return this.find(hql.toString(), params);
		
	}
	
	public int getCancelsCount(V3xOrgMember adminMember, V3xOrgMember appMember, MeetingRoom mr, Integer isAllowed){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		/*if(adminMember != null){
			criteria.createCriteria("meetingRoom").add(Restrictions.eq("v3xOrgMember", adminMember));
		}
		if(appMember != null){
			criteria.add(Restrictions.eq("v3xOrgMember", appMember));
		}
		if(mr != null){
			criteria.add(Restrictions.eq("meetingRoom", mr));
		}
		if(isAllowed != null){
			criteria.add(Restrictions.eq("status", isAllowed));
		}
		criteria.add(Restrictions.gt("endDatetime", new Date()));
		criteria.add(Restrictions.isNull("meeting"));*/
		Criterion c_EndDatetime = Restrictions.gt("endDatetime", new Date());
		Criterion c_IsNull = Restrictions.isNull("meeting");
		Criterion c_And = Restrictions.and(c_EndDatetime, c_IsNull);
		if(appMember != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("v3xOrgMember", appMember));
		}
		if(mr != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("meetingRoom", mr));
		}
		if(isAllowed != null){
			c_And = Restrictions.and(c_And, Restrictions.eq("status", isAllowed));
		}
		criteria.add(c_And);
		if(adminMember != null){
			Criterion c_Or = Restrictions.or(Restrictions.eq("v3xOrgMember", adminMember), Restrictions.not(Restrictions.eq("id", new Long(0))));
			criteria.createCriteria("meetingRoom").add(c_Or);
		}
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public void execCancel(List list)throws Exception{
		for(int i = 0; i < list.size(); i++){
			MeetingRoomApp mra = (MeetingRoomApp)list.get(i);
			MeetingRoomPerm mrp = this.loadMeetingRoomPerm(mra.getId());
			DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
			criteria.add(Restrictions.eq("meetingRoom", mra.getMeetingRoom()));
			criteria.add(Restrictions.eq("meetingRoomApp", mra));
			List rl = super.executeCriteria(criteria);
			for(int j = 0; j < rl.size(); j++){
				super.deleteObject(rl.get(i));
			}
			super.deleteObject(mra);
			super.deleteObject(mrp);
		}
	}
	
	public boolean checkUsed(MeetingRoom mr)throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteria.add(Restrictions.eq("meetingRoom", mr));
		Criterion cWait = Restrictions.eq("status", Constants.Status_App_Wait);
		Criterion cStart = Restrictions.gt("startDatetime", new Date());
		Criterion cAllowed = Restrictions.eq("status", Constants.Status_App_Yes);
		Criterion cor = Restrictions.or(cWait, Restrictions.and(cStart, cAllowed));
		criteria.add(cor);
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria));
		if(count == 0){
			criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
			criteria.add(Restrictions.eq("meetingRoom", mr));
			criteria.add(Restrictions.gt("startDatetime", new Date()));
			criteria.setProjection(Projections.rowCount());
			count = ((Integer)super.executeUniqueCriteria(criteria));
			if(count == 0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public List getUsedList(MeetingRoom mr)throws Exception{
		DetachedCriteria criteriaCount = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaCount.add(Restrictions.eq("meetingRoom", mr));
		Criterion cWait = Restrictions.eq("status", Constants.Status_App_Wait);
		Criterion cStart = Restrictions.gt("startDatetime", new Date());
		Criterion cAllowed = Restrictions.eq("status", Constants.Status_App_Yes);
		Criterion cor = Restrictions.or(cWait, Restrictions.and(cStart, cAllowed));
		criteriaCount.add(cor);
		criteriaCount.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteriaCount));
		DetachedCriteria criteriaApp = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaApp.add(Restrictions.eq("meetingRoom", mr));
		criteriaApp.add(cor);
		List<MeetingRoomApp> listApp = super.executeCriteria(criteriaApp, 0, count);
		criteriaCount = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaCount.add(Restrictions.eq("meetingRoom", mr));
		criteriaCount.add(Restrictions.gt("startDatetime", new Date()));
		criteriaCount.setProjection(Projections.rowCount());
		count = ((Integer)super.executeUniqueCriteria(criteriaCount));
		criteriaApp = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaApp.add(Restrictions.eq("meetingRoom", mr));
		criteriaApp.add(Restrictions.gt("startDatetime", new Date()));
//		criteriaApp.setProjection(Projections.rowCount());
		List<MeetingRoomRecord> listRecord = super.executeCriteria(criteriaApp, 0, count);
		List list = new ArrayList();
		if(listApp != null && listApp.size() > 0 ){
			for(MeetingRoomApp mra : listApp){
				list.add(mra);
			}
		}
		if(listRecord != null && listRecord.size() > 0){
			for(MeetingRoomRecord mrr : listRecord){
				list.add(mrr);
			}
		}
		return list;
	}
	
	public void execCancelMeeting(MtMeeting meeting)throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteria.add(Restrictions.eq("meeting", meeting));
		List list = super.executeCriteria(criteria);
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				MeetingRoomRecord mrr = (MeetingRoomRecord)list.get(i);
				MeetingRoomApp mra = mrr.getMeetingRoomApp();
				if(mra != null){
					mra.setMeeting(null);
					super.update(mra);
				}
//				MeetingRoomApp mra = mrr.getMeetingRoomApp();
//				if(mra != null){
//					MeetingRoomPerm mrp = this.loadMeetingRoomPerm(mra.getId());
//					super.deleteObject(mrp);
//					super.deleteObject(mra);
//				}
				super.deleteObject(mrr);
			}
		}
	}
    public void execCancelMeetingRec(MtMeeting meeting)throws Exception{
        DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
        criteria.add(Restrictions.eq("meeting", meeting));
        List list = super.executeCriteria(criteria);
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                MeetingRoomRecord mrr = (MeetingRoomRecord)list.get(i);
              MeetingRoomApp mra = mrr.getMeetingRoomApp();
              if(mra != null){
                  mra.setMeeting(null);
                  super.update(mra);
              }
                super.deleteObject(mrr);
            }
        }
    }
	public List getTotal(V3xOrgMember v3xOrgMember, Date startDatetime, Date endDatetime, Boolean isPage)throws Exception{
		//"yyyy-MM-dd HH:mm:ss S"
		Calendar sMonth = Calendar.getInstance();
		Calendar eMonth = Calendar.getInstance();
		sMonth.set(Calendar.DAY_OF_MONTH, 1);
		sMonth.set(Calendar.HOUR_OF_DAY, 0);
		sMonth.set(Calendar.MINUTE, 0);
		sMonth.set(Calendar.SECOND, 0);
		sMonth.set(Calendar.MILLISECOND, 0);
		eMonth.setTime(sMonth.getTime());
		eMonth.roll(Calendar.DAY_OF_MONTH, -1);
		
		List<HashMap> list = new ArrayList<HashMap>();
		int allCount = this.getTotalCount(v3xOrgMember);
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteria.createCriteria("meetingRoom").add(Restrictions.eq("v3xOrgMember", v3xOrgMember));
		criteria.addOrder(Order.asc("startDatetime"));
		List cl = super.executeCriteria(criteria, 0, allCount);
	for1:for(int i = 0; i < cl.size(); i++){
			MeetingRoomRecord mrr = (MeetingRoomRecord)cl.get(i);
		for2:for(int j = 0; j < list.size(); j++){
				HashMap h = list.get(j);
				MeetingRoomRecord h_Mrr = (MeetingRoomRecord)h.get("MeetingRoomRecord");
				if(h_Mrr.getMeetingRoom().getId().equals(mrr.getMeetingRoom().getId())){
					Long month = (Long)h.get("MonthTotal");
					Long all = (Long)h.get("AllTotal");
					Long section = (Long)h.get("SectionTotal");
					month = month + this.computeDatetime(mrr, sMonth.getTime(), eMonth.getTime());
					all = all + ((mrr.getEndDatetime().getTime() - mrr.getStartDatetime().getTime()) / 1000 / 60);
					section = section + this.computeDatetime(mrr, startDatetime, endDatetime);
					h.put("AllTotal", all);
					h.put("MonthTotal", month);
					h.put("SectionTotal", section);
					continue for1;
				}
			}
			HashMap h = new HashMap();
			Long month = this.computeDatetime(mrr, sMonth.getTime(), eMonth.getTime());
			Long all = (mrr.getEndDatetime().getTime() - mrr.getStartDatetime().getTime()) / 1000 / 60;
			Long section = this.computeDatetime(mrr, startDatetime, endDatetime);
			h.put("MeetingRoomRecord", mrr);
			h.put("MonthTotal", month);
			h.put("AllTotal", all);
			h.put("SectionTotal", section);
			list.add(h);
		}
		int count = list.size();
		Pagination.setRowCount(count);
		List<HashMap> resultList = new ArrayList<HashMap>();
		int max = list.size();
		if((Pagination.getFirstResult()+Pagination.getMaxResults()) < list.size() && isPage){
			max = Pagination.getFirstResult()+Pagination.getMaxResults();
		}
		if(count > 0){
			for(int i = Pagination.getFirstResult(); i < max; i++){
				HashMap h = list.get(i);
				Long month = (Long)h.get("MonthTotal");
				Long all = (Long)h.get("AllTotal");
				Long section = (Long)h.get("SectionTotal");
				if(month != 0){
					Long h_Month = month / 60;
					if(month % 60 > 0){
						h_Month++;
					}
					h.put("MonthTotal", h_Month);
				}
				if(all != 0){
					Long h_All = all / 60;
					if(all % 60 > 0){
						h_All++;
					}
					h.put("AllTotal", h_All);
				}
				if(section != 0){
					Long h_Section = section / 60;
					if(section % 60 > 0){
						h_Section++;
					}
					h.put("SectionTotal", h_Section);
				}
				resultList.add(h);
			}
		}
		return resultList;
	}
	
	public int getTotalCount(V3xOrgMember v3xOrgMember){
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteria.createCriteria("meetingRoom").add(Restrictions.eq("v3xOrgMember", v3xOrgMember));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		return count;
	}
	
	public List getMeetingRoomForMeeting(List v3xOrgMembers, V3xOrgMember appMember, Date startDatetime, Date endDatetime){
		DetachedCriteria criteriaAppCount = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaAppCount.add(Restrictions.eq("v3xOrgMember", appMember));
		criteriaAppCount.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteriaAppCount.add(Restrictions.isNull("meeting"));
		criteriaAppCount.add(Restrictions.ge("endDatetime", new Date()));
		criteriaAppCount.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		criteriaAppCount.setProjection(Projections.rowCount());
		int countApp = ((Integer)super.executeUniqueCriteria(criteriaAppCount)).intValue();
		DetachedCriteria criteriaApp = DetachedCriteria.forClass(MeetingRoomApp.class);
		criteriaApp.add(Restrictions.eq("v3xOrgMember", appMember));
		criteriaApp.add(Restrictions.eq("status", Constants.Status_App_Yes));
		criteriaApp.add(Restrictions.isNull("meeting"));
		criteriaApp.add(Restrictions.ge("endDatetime", new Date()));
		criteriaApp.createCriteria("meetingRoom").add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		List<MeetingRoomApp> listApp = super.executeCriteria(criteriaApp, 0, countApp);
		
		/*DetachedCriteria criteriaRecordCount = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaRecordCount.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		criteriaRecordCount.setProjection(Projections.rowCount());
		int countRecord = ((Integer)super.executeUniqueCriteria(criteriaRecordCount));
		DetachedCriteria criteriaRecord = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteriaRecord.add(this.createStartAndEndDatetimeCondition(startDatetime, endDatetime));
		List<MeetingRoomRecord> listRecord = super.executeCriteria(criteriaRecord, 0, countRecord);*/
		
		DetachedCriteria criteriaNoAppCount = DetachedCriteria.forClass(MeetingRoom.class);
		criteriaNoAppCount.add(Restrictions.eq("needApp", Constants.Type_MeetingRoom_NoNeedApp));
		criteriaNoAppCount.add(Restrictions.eq("status", Constants.Status_MeetingRoom_Normal));
		criteriaNoAppCount.add(Restrictions.eq("delFlag", Constants.DelFlag_No));
		criteriaNoAppCount.add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		criteriaNoAppCount.setProjection(Projections.rowCount());
		int countNoApp = ((Integer)super.executeUniqueCriteria(criteriaNoAppCount));
		DetachedCriteria criteriaNoApp = DetachedCriteria.forClass(MeetingRoom.class);
		criteriaNoApp.add(Restrictions.eq("needApp", Constants.Type_MeetingRoom_NoNeedApp));
		criteriaNoApp.add(Restrictions.eq("status", Constants.Status_MeetingRoom_Normal));
		criteriaNoApp.add(Restrictions.eq("delFlag", Constants.DelFlag_No));
		criteriaNoApp.add(Restrictions.in("v3xOrgMember", v3xOrgMembers));
		List<MeetingRoom> listNoApp = super.executeCriteria(criteriaNoApp, 0, countNoApp);
		
		List list = new ArrayList();
		if(listApp != null && listApp.size() > 0){
			for(MeetingRoomApp mra : listApp){
				list.add(mra);
			}
		}
		if(listNoApp != null && listNoApp.size() > 0){
			for(MeetingRoom mr : listNoApp){
				list.add(mr);
			}
		}
		return list;
	}
	
	public String checkMeetingRoomForMeeting(V3xOrgMember v3xOrgMember, MeetingRoom mr, MeetingRoomApp mra, MtMeeting meeting, Date startDatetime, Date endDatetime){
		if(mra == null && mr.getNeedApp() == Constants.Type_MeetingRoom_NeedApp){
			return "appfalse";
		}
		if(mra == null || mr.getNeedApp() == Constants.Type_MeetingRoom_NoNeedApp){
			DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
			criteria.add(Restrictions.eq("meetingRoom", mr));
			Criterion c = this.createStartAndEndDatetimeCondition(startDatetime, endDatetime);
			criteria.add(c);
			//criteria.setProjection(Projections.rowCount());
			List list = super.executeCriteria(criteria);
			if(list != null && list.size() > 0){
				if(list.size() > 1){
					return "timefalse";//已经被占用
				}else{
					return String.valueOf(((MeetingRoomRecord)list.get(0)).getMeeting().getId());
				}
			}
			return "true";
		}else{
			DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomApp.class);
			criteria.add(Restrictions.eq("meetingRoom", mr));
			criteria.add(Restrictions.eq("v3xOrgMember", v3xOrgMember));
			criteria.add(Restrictions.eq("status", Constants.Status_App_Yes));
			criteria.add(Restrictions.le("startDatetime", startDatetime));
			criteria.add(Restrictions.ge("endDatetime", endDatetime));
			
			//修改时数据库中还未更新修改后最新的meetingId,故不能以此为where条件
//			if(meeting != null){
//				criteria.add(Restrictions.eq("meeting", meeting));
//			} else {
//				criteria.add(Restrictions.isNull("meeting"));
//			}
			criteria.setProjection(Projections.rowCount());
			int count = ((Integer)super.executeUniqueCriteria(criteria));
			if(count == 1){
				return "true";
			}else{
				return "timeerror";//请检查会议开始时间和结束时间是否在会议室申请使用的时间范围内
			}
		}
	}
	
	public void execMeeting(com.seeyon.v3x.meeting.domain.MtMeeting meeting, MeetingRoom mr, MeetingRoomApp mra, Date startDatetime, Date endDatetime)throws Exception{
		if(mra != null){
			mra.setMeeting(meeting);
			super.update(mra);
			MeetingRoomPerm mrp = this.loadMeetingRoomPerm(mra.getId());
			MeetingRoomRecord mrr = new MeetingRoomRecord();
			mrr.setId(UUIDLong.longUUID());
			mrr.setMeeting(meeting);
			mrr.setMeetingRoom(mr);
			mrr.setMeetingRoomApp(mra);
			mrr.setStartDatetime(mra.getStartDatetime());
			mrr.setEndDatetime(mra.getEndDatetime());
			super.save(mrr);
		}else{
			MeetingRoomRecord mrr=  new MeetingRoomRecord();
			mrr.setId(UUIDLong.longUUID());
			mrr.setMeeting(meeting);
			mrr.setMeetingRoom(mr);
			mrr.setStartDatetime(startDatetime);
			mrr.setEndDatetime(endDatetime);
			super.save(mrr);
		}
	}
	
	public String[] getByMeeting(MtMeeting meeting){
		String[] str = new String[3];
		MeetingRoom mr = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MeetingRoomRecord.class);
		criteria.add(Restrictions.eq("meeting", meeting));
		List list = super.executeCriteria(criteria);
		if(list != null && list.size() > 0){
			MeetingRoomRecord mrr = (MeetingRoomRecord)list.get(0);
			str[0] = String.valueOf(mrr.getMeetingRoom().getId());
			if(mrr.getMeetingRoomApp() != null){
				str[1] = String.valueOf(mrr.getMeetingRoomApp().getId());
			}else{
				str[1] = "";
			}
			str[2] = mrr.getMeetingRoom().getName();
		}else{
			return null;
		}
		return str;
	}

	private Criterion createStartAndEndDatetimeCondition(Date startDatetime, Date endDatetime){
		Criterion scl = Restrictions.ge("startDatetime", startDatetime);
		Criterion scr = Restrictions.lt("startDatetime", endDatetime);
		Criterion sc = Restrictions.and(scl, scr);
		Criterion ecl = Restrictions.gt("endDatetime", startDatetime);
		Criterion ecr = Restrictions.le("endDatetime", endDatetime);
		Criterion ec = Restrictions.and(ecl, ecr);
		Criterion lc = Restrictions.le("startDatetime", startDatetime);
		Criterion rc = Restrictions.ge("endDatetime", endDatetime);
		Criterion andc = Restrictions.and(lc, rc);
		Criterion orc = Restrictions.or(sc, ec);
		orc = Restrictions.or(orc, andc);
		return orc;
	}
	
	private DetachedCriteria createQueryCondition(DetachedCriteria criteria, V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status, Integer delFlag){
		if(v3xOrgMember != null){
			criteria.add(Restrictions.eq("v3xOrgMember", v3xOrgMember));
		}
		if(accountId != null){
			criteria.add(Restrictions.eq("accountId", accountId));
		}
		if(name != null){
			criteria.add(Restrictions.like("name", "%"+name+"%"));
		}
		if(place != null){
			criteria.add(Restrictions.like("place", "%"+place+"%"));
		}
		if(seatCount != null){
			switch(seatCount[0]){
				case Constants.Condition_eq:{
					criteria.add(Restrictions.eq("seatCount", seatCount[1]));
					break;
				}
				case Constants.Condition_ge:{
//					criteria.add(Restrictions.ge("seatCount", seatCount[1]));
                    //2
                    criteria.add(Restrictions.gt("seatCount", seatCount[1]));
					break;
				}
//				case Constants.Condition_gt:{
//					criteria.add(Restrictions.gt("seatCount", seatCount[1]));
//					break;
//				}
				case Constants.Condition_le:{
//					criteria.add(Restrictions.le("seatCount", seatCount[1]));
                    //4
                    criteria.add(Restrictions.lt("seatCount", seatCount[1]));
					break;
				}
//				case Constants.Condition_lt:{
//					criteria.add(Restrictions.lt("seatCount", seatCount[1]));
//					break;
//				}
				case Constants.Condition_not:{
					criteria.add(Restrictions.not(Expression.eq("seatCount", seatCount[1])));
					break;
				}
			}
		}
		if(needApp != null){
			criteria.add(Restrictions.eq("needApp", needApp));
		}
		if(status != null){
			criteria.add(Restrictions.eq("status", status));
		}
		if(delFlag != null){
			criteria.add(Restrictions.eq("delFlag", delFlag));
		}
		return criteria;
	}
	
	private long computeDatetime(MeetingRoomRecord mrr, Date startDatetime, Date endDatetime ){
		//"yyyy-MM-dd HH:mm:ss S"
		long secend = 0;
		if((mrr.getStartDatetime().after(startDatetime)||mrr.getStartDatetime().equals(startDatetime))&&
			mrr.getStartDatetime().before(endDatetime)){
			if(mrr.getEndDatetime().before(endDatetime)||mrr.getEndDatetime().equals(endDatetime)){
				secend = (mrr.getEndDatetime().getTime() - mrr.getStartDatetime().getTime());
			}else{
				secend = (endDatetime.getTime() - mrr.getStartDatetime().getTime());
			}
		}
		if(mrr.getEndDatetime().after(startDatetime)&&
			(mrr.getEndDatetime().before(endDatetime)||mrr.getEndDatetime().equals(endDatetime))){
			if(mrr.getStartDatetime().after(startDatetime)||mrr.getStartDatetime().equals(startDatetime)){
				secend = (mrr.getEndDatetime().getTime() - mrr.getStartDatetime().getTime());
			}else{
				secend = (mrr.getEndDatetime().getTime() - startDatetime.getTime());
			}
		}
		if((mrr.getStartDatetime().before(startDatetime)||mrr.getStartDatetime().equals(startDatetime))&&
			(mrr.getEndDatetime().after(endDatetime)||mrr.getEndDatetime().equals(endDatetime))){
			secend = endDatetime.getTime() - startDatetime.getTime();
		}
		if(secend != 0){
			secend = secend / 1000 / 60;
		}
		return secend;
	}


	public void updateBookMangerBatch(final long adminIdLong, final long admin_newLong, final User user) {
		this.updateBookMangerBatch(adminIdLong, admin_newLong, user,true);
	}
	
	public void updateBookMangerBatch(final long adminIdLong, final long admin_newLong, final User user,final boolean fromFlag) {
		if (fromFlag) {
			Map<String,Object> columns = new HashMap<String,Object>();
			columns.put("v3xOrgMember.id", admin_newLong);
			columns.put("accountId", user.getAccountId());
			super.update(MeetingRoom.class, columns, new Object[][]{{"v3xOrgMember.id",adminIdLong}});
		}else {
			super.getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					String sql = "update MeetingRoom  set v3xOrgMember.id=:admin_newLong where accountId=:accountId";
					Query query = session.createQuery(sql);
					query.setLong("admin_newLong", admin_newLong);
					query.setLong("accountId", user.getLoginAccount());
					query.executeUpdate();
					return null;
				}

			});
		}
		
	}
}
