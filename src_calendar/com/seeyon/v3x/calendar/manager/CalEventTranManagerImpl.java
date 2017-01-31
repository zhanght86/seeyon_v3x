package com.seeyon.v3x.calendar.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.calendar.constants.ShareType;
import com.seeyon.v3x.calendar.dao.CalEventTranDao;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventTran;
import com.seeyon.v3x.calendar.util.CalendarUtils;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class CalEventTranManagerImpl extends BaseCalendarManager implements
		CalEventTranManager {

	private CalEventTranDao calEventTranDao;
	
	public CalEventTranDao getCalEventTranDao() {
		return calEventTranDao;
	}

	public void setCalEventTranDao(CalEventTranDao calEventTranDao) {
		this.calEventTranDao = calEventTranDao;
	}


	/**
	 * 基础删除
	 * 
	 */
	public void delete(Long eventTranId) {
		calEventTranDao.delete(eventTranId.longValue());
	}
	
	public void deleteByEventId(Long eventId) {
		this.calEventTranDao.bulkUpdate("delete from CalEventTran where eventId = ?", null, eventId);
	}
	
	public void deleteByEventId(Long eventId, Integer type) {
		this.calEventTranDao.bulkUpdate("delete from CalEventTran where eventId = ? and type = ?", null, eventId, type);
	}

	/**
	 * 删除 ---根据人员id和事件id
	 * 
	 */
	public void deleteByEventAndUserId(Long eventId, Long userId) {
		calEventTranDao.bulkUpdate("delete from CalEventTran as tran where tran.eventId=? and tran.entityId = ?", null, eventId, userId);
	}

	/**
	 * 基础保存
	 * 
	 */
	public void save(CalEventTran eventTran) {
		if (eventTran.isNew()) {
			eventTran.setIdIfNew();
			calEventTranDao.save(eventTran);
		} else {
			calEventTranDao.update(eventTran);
		}
	}

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在 公开类型为其他
	 * 
	 */
	public void saveTranEvents(CalEvent event, Long[] userIds, int type,
			Long[] receiveIds) {
		for (Long userId : userIds) {
			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setEntityId(userId);
			tran.setEventId(event.getId());
			tran.setType(type);
			this.save(tran);
		}
		for (Long receiveId : receiveIds) {
			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setReceiveId(receiveId);
			tran.setEventId(event.getId());
			tran.setType(type);
			this.save(tran);
		}
	}

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在 公开类型为项目
	 * 
	 */
	public void saveTranEvents(CalEvent event, Long userId, int type,
			Long[] receiveIds) {

		for (Long receiveId : receiveIds) {
			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setReceiveId(receiveId);
			tran.setEntityId(userId);
			tran.setEventId(event.getId());
			tran.setType(type);
			this.save(tran);
		}
	}

	/**
	 * 保存 ---只有事件的公开类型接受者存在
	 * 
	 */
	public void saveTranEvents(CalEvent event, Long[] userIds, int type) {
		for (Long userId : userIds) {

			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setEntityId(userId);
			tran.setEventId(event.getId());
			tran.setType(type);
			this.save(tran);
		}
	}
	
	public void saveProjectTranEvents(CalEvent event, Long projectId, int type, String [] userIds) {
		for (String userId : userIds) {
			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setEntityId(projectId);
			tran.setEventId(event.getId());
			tran.setReceiveId(Long.parseLong(userId));
			tran.setType(type);
			this.save(tran);
		}
	}

	/**
	 * 保存 ---只有事件的委托和安排接受者存在
	 * 
	 */
	public void saveTranEvents1(CalEvent event, Long[] userIds, int type) {
		for (Long userId : userIds) {

			CalEventTran tran = new CalEventTran();
			tran.setSourceRecordId(event.getCreateUserId());
			tran.setReceiveId(userId);
			tran.setEntityId(userId);
			tran.setEventId(event.getId());
			tran.setType(type);
			this.save(tran);
		}
	}

	/**
	 * 保存 ---只有事件的公开类型为项目（从关联项目只能得到单纯的 id ）存在
	 * 
	 */
	public void saveTranEvents(CalEvent event, Long userId, int type) {

		CalEventTran tran = new CalEventTran();
		tran.setSourceRecordId(event.getCreateUserId());
		tran.setEntityId(userId);
		tran.setEventId(event.getId());
		tran.setType(type);
		this.save(tran);
	}

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在
	 * 
	 */
	public void saveTranEvents(CalEvent event, String typeIds, int type,String reIds) {
		CalendarUtils calendarUtils = (CalendarUtils) ApplicationContextHolder
				.getBean("calendarUtils");
		String str = "";
		String strre = "";
		Long[] userIds = null;
		Long[] receiveIds = null;

		List<V3xOrgMember> listMem = new ArrayList<V3xOrgMember>();
		List<V3xOrgDepartment> listDep = new ArrayList<V3xOrgDepartment>();
		List<V3xOrgAccount> listAcc = new ArrayList<V3xOrgAccount>();

		strre = calendarUtils.getMembersIdandDepId(reIds);
		if ((strre != "" || strre != null) && strre.equals("member")) {
			listMem = calendarUtils.getMembersId(reIds);
			receiveIds = new Long[listMem.size()];
			for (int i = 0; i < listMem.size(); i++) {
				V3xOrgMember member = listMem.get(i);
				receiveIds[i] = member.getId();
			}
			if (type != 4) {
				if(type==6){
					this.saveTranEvents(event, Long.valueOf(typeIds), type, receiveIds);
				}else{
					str = calendarUtils.getMembersIdandDepId(typeIds);
					if (str == "") {
						this.saveTranEvents1(event, receiveIds, type);
					} else if (str != "") {
						if (str.equals("member")) {
							listMem = calendarUtils.getMembersId(typeIds);
							userIds = new Long[listMem.size()];
							for (int i = 0; i < listMem.size(); i++) {
								V3xOrgMember member = listMem.get(i);
								userIds[i] = member!=null?member.getId():0;
							}
						}
						if (str.equals("department")) {
							listDep = calendarUtils.getDepId(typeIds);
							userIds = new Long[listDep.size()];
							for (int i = 0; i < listDep.size(); i++) {
								V3xOrgDepartment dep = listDep.get(i);
								userIds[i] = dep!=null?dep.getId():0;
							}
						}
						if (str != "" && str.equals("account")) {
							listAcc = calendarUtils.getAccountId(typeIds);
							userIds = new Long[listAcc.size()];
							for (int i = 0; i < listAcc.size(); i++) {
								V3xOrgAccount account = listAcc.get(i);
								userIds[i] = account!=null?account.getId():0;
							}
						}
						this.saveTranEvents(event, userIds, type, receiveIds);
					}
				}
				
			} else if (type == 4) {
				this.saveTranEvents(event, NumberUtils.toLong(typeIds), type,
						receiveIds);
			}
		} else {

			if (type != 4) {
				str = calendarUtils.getMembersIdandDepId(typeIds);
				if (str != "" && str.equals("member")) {
					listMem = calendarUtils.getMembersId(typeIds);
					userIds = new Long[listMem.size()];
					for (int i = 0; i < listMem.size(); i++) {
						V3xOrgMember member = listMem.get(i);
						userIds[i] = member!=null?member.getId():0;
					}
				}
				if (str != "" && str.equals("department")) {
					listDep = calendarUtils.getDepId(typeIds);
					userIds = new Long[listDep.size()];
					for (int i = 0; i < listDep.size(); i++) {
						V3xOrgDepartment dep = listDep.get(i);
						userIds[i] = dep!=null?dep.getId():0;
					}
				}
				if (str != "" && str.equals("account")) {
					listAcc = calendarUtils.getAccountId(typeIds);
					userIds = new Long[listAcc.size()];
					for (int i = 0; i < listAcc.size(); i++) {
						V3xOrgAccount dep = listAcc.get(i);
						userIds[i] = dep!=null?dep.getId():0;
					}
				}
				this.saveTranEvents(event, userIds, type);
			} else if (type == 4) {
				this.saveTranEvents(event, Long.valueOf(typeIds), type);
			}
		}

	}

	/**
	 * 保存 ---事件的公开接受者 或是 事件委托安排接受者，只存在其中之一
	 * 
	 */
	public void saveTranEvents(CalEvent event, String typeIds, int type, boolean isFromTask) {
		CalendarUtils calendarUtils = (CalendarUtils) ApplicationContextHolder.getBean("calendarUtils");
		String str = "";
		Long[] userIds = null;
		ShareType shareType = ShareType.valueOf(type);
		List<V3xOrgMember> listMem = new ArrayList<V3xOrgMember>();
		List<V3xOrgDepartment> listDep = new ArrayList<V3xOrgDepartment>();
		List<V3xOrgAccount> listAcc = new ArrayList<V3xOrgAccount>();
		List<Long> listUserIds = new ArrayList<Long>();

		if (shareType != ShareType.junior) {
			String[] _typeIds=typeIds.split(",");
			for(String typeId : _typeIds) {
				str = calendarUtils.getMembersIdandDepId(typeId);
				if (str != "" && str.equals("member")) {
					listMem = calendarUtils.getMembersId(typeId);
					for (int i = 0; i < listMem.size(); i++) {
						V3xOrgMember member = listMem.get(i);
						listUserIds.add(member!=null?member.getId():0);
					}
				}
				if (str != "" && str.equals("department")) {
					listDep = calendarUtils.getDepId(typeId);
					for (int i = 0; i < listDep.size(); i++) {
						V3xOrgDepartment dep = listDep.get(i);
						listUserIds.add(dep!=null?dep.getId():0);
					}
				}
				if (str != "" && str.equals("account")) {
					listAcc = calendarUtils.getAccountId(typeId);
					for (int i = 0; i < listAcc.size(); i++) {
						V3xOrgAccount dep = listAcc.get(i);
						listUserIds.add(dep!=null?dep.getId():0);
					}
				}
			}
//			if (type == 5) {
//				this.saveTranEvents1(event, userIds, type);
//			} else {
//				this.saveTranEvents(event, userIds, type);
//			}
			if(listUserIds.size() != 0) {
				userIds = new Long[listUserIds.size()];
				for(int i = 0; i < listUserIds.size(); i++) {
					userIds[i] = listUserIds.get(i);
				}
			}
			if(shareType == ShareType.department||shareType == ShareType.publicity||shareType == ShareType.project){
				if(shareType == ShareType.project){
					if(userIds!=null&&userIds[0]==0){
						userIds[0] = Long.parseLong(typeIds);
					}
				}
				if(shareType == ShareType.publicity && !isFromTask) {
					this.saveTranEvents(event, userIds, 1);
				} else {
					this.saveTranEvents1(event, userIds, 1);
				}
			}
		} else if (shareType == ShareType.junior) {
			this.saveTranEvents(event, Long.valueOf(typeIds), type);
		}
	}

	/**
	 * 根据事件id得到委托，安排，公开项目，公开个人，公开部门事件列表
	 * 
	 */
	public List<CalEventTran> getEventTranListByEventId(Long eventId) {
		return calEventTranDao.find(
				"from CalEventTran as tran where tran.eventId = ?", eventId);
	}

	/**
	 * 根据事件id和人员id得到转发事件列表
	 * 
	 */
	public List<CalEventTran> getEventTranListByEventAndUserId(Long eventId,
			Long destUserId) {
		List<CalEventTran> list = this.calEventTranDao
				.find(
						"from CalEventTran as tran where tran.eventId=? and tran.entityId=?",
						new Object[] { eventId, destUserId });
		return list;
	}

	/**
	 * 根据人员id得到委托，安排，公开项目，公开个人，公开部门事件列表
	 * 
	 */
	public List<CalEventTran> getEventTranListByUserId(Long userId) {
		return calEventTranDao.find(
				"from CalEventTran as tran where tran.entityId = ?", userId);
	}

	/**
	 * 得到事件的总数
	 * 
	 */
	public int getTotal() {
		return this.calEventTranDao.getQueryCount("from CalEventTran", null,
				null);
	}

	public List<CalEventTran> getEventTranListByRecIdAndCreatureId(Long receiverId,Long creatureId){
		return calEventTranDao.find("from CalEventTran as tran where tran.sourceRecordId = ? and tran.receiveId = ?", new Object[] { creatureId, receiverId });
	}
	
	public List<CalEventTran> getEventTranListByEntityIdAndCreatureId(Long creatureId, Long entityId) {
		return calEventTranDao.find("from CalEventTran as tran where tran.sourceRecordId = ? and tran.entityId = ?", new Object[] { creatureId, entityId });
	}
	
	public List<CalEventTran> getEventTranListByRecId(Long recId){
		return calEventTranDao.find(
				"from CalEventTran as tran where tran.receiveId = ?", recId);
	}
	
	public List<CalEventTran> getEventTranListByEntityId(Long entityId){
		return calEventTranDao.find(
				"from CalEventTran as tran where tran.entityId = ?", entityId);
	}

	public boolean validateCurrentUserIsCanViewEvent(Long eventId,List<V3xOrgEntity> entitys) {
		StringBuilder builder = new StringBuilder("from CalEventTran as tran where tran.eventId = ? and ( ");
		if(entitys!=null){
			List<Object> listId = new ArrayList<Object>();
			listId.add(eventId);
			int size = entitys.size();
			for(int i=0;i<entitys.size();i++){
				listId.add(entitys.get(i).getId());
				if(i==size-1){
					builder.append(" tran.entityId = ? or tran.receiveId = ? ) ");
				}else{
					builder.append(" tran.entityId = ? or "); 
				}
			}
			int size1 = listId.size();
			listId.add(listId.get(size1-1));
			List<CalEventTran> list = calEventTranDao.find(builder.toString(), listId.toArray());
			if(list!=null && list.size()!=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	public List<CalEventTran> getEventTranListByCidAndByRid(Long currentId,Long relationId){
		
		List<CalEventTran> list1 = calEventTranDao.find("from CalEventTran as tran where tran.entityId = ?", currentId);
		List<CalEventTran> list2 = calEventTranDao.find("from CalEventTran as tran where tran.receiveId = ?", relationId);
		
		List<CalEventTran> newList = new ArrayList<CalEventTran>();
		for(CalEventTran tr : list2){
			for(CalEventTran tran : list1){
				if(tr.getEventId()==tran.getEventId()){
					newList.add(tr);
				}
			}
		}
		return newList;
	}



}
