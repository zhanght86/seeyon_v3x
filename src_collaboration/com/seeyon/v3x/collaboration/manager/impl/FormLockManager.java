package com.seeyon.v3x.collaboration.manager.impl;

import java.util.List;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.domain.LockObject;
import com.seeyon.v3x.common.lock.domain.Lock;
import com.seeyon.v3x.common.lock.manager.LockManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;

public class FormLockManager {
	
	private static AffairManager affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
	private static OnLineManager onLineManager = (OnLineManager)ApplicationContextHolder.getBean("onLineManager");
	private static LockManager lockManager = (LockManager)ApplicationContextHolder.getBean("formLockManager");
	private static OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
	private static Object lock = new Object();
	
	/**
	   * 增加锁定
	   * @param summaryId 流程id
	   * @param loginName  登录名
	   * @return 如果此流程已经被别人锁定，则返回已有的锁定信息；如果没有被锁定或者锁定人不在线，将当前人信息加入，并返回
	   */
	public static LockObject add(Long summaryId,Long affairId,Long memberId,String loginName,Long loginTimestamp){
//		TODO 此处存在bug，传入的loginTimestamp是CurrentUser中记录的，比较的是onlineUser中的loginTime，这个参数应该废弃，后续处理
		Affair affair = affairManager.getById(affairId);
		LockObject obj;
		long onlineLoginTime = 0l;
		OnlineUser onlineUser = OnlineRecorder.getOnlineUser(loginName);
		if(onlineUser != null && onlineUser.getLoginTime() != null)
			onlineLoginTime = onlineUser.getLoginTime().getTime();
		synchronized(lock){
			obj = getLockObject(summaryId);
			if(affair.getState()!= StateEnum.col_pending.key())
				return obj;
			if(obj==null || (obj.getOwner()!=memberId.longValue() && !onLineManager.isSameLogin(obj.getLoginName(),obj.getLoginTimestamp()))){
				if(obj==null)
					obj = new LockObject();
				obj.setOwner(memberId);
				obj.setLoginName(loginName);
				obj.setLoginTimestamp(onlineLoginTime);
				if(lockManager.check(memberId, summaryId))
					if(!lockManager.lock(memberId, summaryId))
						return null;
			}
		}
//		如果是同一个人，但登录时间不一致，更新时间戳
	    if(obj.getOwner() == memberId.longValue() && obj.getLoginTimestamp() != onlineLoginTime){
	    	obj.setLoginTimestamp(onlineLoginTime);
	    }
	    return obj;
	}
	
	 /**
	   * 删除锁定
	   * @param summaryId  流程id，cache中的key
	   */
	public static void remove(Long summaryId){
		synchronized(lock){
			lockManager.unlock(summaryId);
		}
	}
	
	private static LockObject getLockObject(Long summaryId){
		List<Lock> locks = lockManager.getLocks(summaryId);
		if(locks!=null && !locks.isEmpty()){
			Lock lk = locks.get(0);
			if(lk!=null){
				LockObject obj = new LockObject();
				obj.setOwner(lk.getOwner());
				V3xOrgMember member =null;
				try {
					member = (V3xOrgMember) orgManager.getMemberById(lk.getOwner());
				} catch (Exception e) {
					member=null;
				}
				if(member!=null)
					obj.setLoginName(member.getLoginName());
				obj.setLoginTimestamp(lk.getLoginTime());
				return obj;
			}
		}
		return null;
	}
	
	/**
	 * 删除锁定，只有删除人是加锁人时才能删除锁
	 * @param summaryId
	 * @param userId
	 */
	public static void remove(Long summaryId,Long userId){
		if(summaryId == null || userId == null)
			return;
		synchronized(lock){
			LockObject lock = getLockObject(summaryId);
			if(lock == null || lock.getOwner() != userId)
				return;
			lockManager.unlock(summaryId);
		}
	}
}
