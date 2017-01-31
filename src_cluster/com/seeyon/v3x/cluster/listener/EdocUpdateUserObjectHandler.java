package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.beans.NotificationFormLock;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.collaboration.manager.impl.FormLockManager;
import com.seeyon.v3x.common.office.HandWriteManager;
import com.seeyon.v3x.common.office.HtmlHandWriteManager;
import com.seeyon.v3x.common.office.UserUpdateObject;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManagerImpl;
import com.seeyon.v3x.util.annotation.HandleNotification;

public class EdocUpdateUserObjectHandler {
	
	protected static final Log logger = LogFactory.getLog(EdocUpdateUserObjectHandler.class);
	
	EdocSummaryManager edocSummaryManager;
	HtmlHandWriteManager htmlHandWriteManager;
	HandWriteManager  handWriteManager;
	
	
	//html公文正文处理
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectAddHtml)
	public void userOfficeObjectAddhtml(Object o) {
		if(o instanceof UserUpdateObject){
			try{
				UserUpdateObject uo=(UserUpdateObject)o;
				htmlHandWriteManager.addUpdateObj(uo);
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("集群-公文手写签批加锁成功。【EdocUpdateUserObjectHandler.userOfficeObjectAddhtml】"+htmlHandWriteManager.getUseObjectList().get((uo.getObjId())));
				}
			}catch(Exception e){
				logger.error("集群-公文手写签批加锁异常：【EdocUpdateUserObjectHandler.userOfficeObjectAddhtml】", e) ; 
			}
		}
	}
	
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectRomoveHtml)
	public void userOfficeObjectRemovehtml(Object o) {
		if(o instanceof String){
			try{
				Object[] arr=(Object[])o;
				String objId = (String)arr[0];
				Long userId = (Long)arr[1];
				htmlHandWriteManager.deleteUpdateObj(objId,userId);
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("集群-公文手写签批解锁成功。【EdocUpdateUserObjectHandler.userOfficeObjectRemovehtml】"+htmlHandWriteManager.getUseObjectList().get(objId));
				}
			}catch(Exception e){
				logger.error("集群-公文手写签批解锁异常。【EdocUpdateUserObjectHandler userOfficeObjectRemovehtml】 ：", e) ; 
			}
		}
	}
	
	
	//公文Office正文处理
	//html公文正文处理
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectAddOffice)
	public void userOfficeObjectAddOffice(Object o) {
		if(o instanceof UserUpdateObject){
			try{
				UserUpdateObject uo=(UserUpdateObject)o;
				handWriteManager.addUpdateObj(uo);
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("集群-修改公文正文加锁成功！objectId="+uo+" 缓存中是否存在加锁对象："+handWriteManager.getUseObjectList().get((uo.getObjId())));
				}
			}catch(Exception e){
				logger.error("集群-修改公文公文加锁失败！【EdocUpdateUserObjectHandler.userOfficeObjectAddOffice]", e) ; 
			}
		}
	}
	
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectRomoveOffice)
	public void userOfficeObjectRemoveOffice(Object o) {
		if(o instanceof String){
			try{
				String[] array=(String[])o;
				String userId =array[1];
				String objId = array[0];
				handWriteManager.deleteUpdateObj(objId,userId);
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("集群-修改公文正文解锁成功！objectId="+objId+" userId="+ userId+" 缓存中是否存在解锁对象："+handWriteManager.getUseObjectList().get(objId));
				}
			}catch(Exception e){
				logger.error("集群-修改公文正文解锁失败.[EdocUpdateUserObjectHandler.userOfficeObjectAddOffice]", e) ; 
			}
		}
	}
	
	
	
	
	//公文文单处理
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectAdd)
	public void userOfficeObjectAdd(Object o) {
		if(o instanceof UserUpdateObject){
			try{
				UserUpdateObject uo=(UserUpdateObject)o;
				edocSummaryManager.addUpdateObj(uo);
				// 调试信息
				if (logger.isDebugEnabled()) {
					if(edocSummaryManager instanceof EdocSummaryManagerImpl)
					{
						EdocSummaryManagerImpl impl =(EdocSummaryManagerImpl) edocSummaryManager;
						logger.debug("修改公文单加锁成功！【EdocUpdateUserObjectHandler.userOfficeObjectAdd]"+impl.getUseObjectList().get((uo.getObjId())));
					}
				}
			}catch(Exception e){
				logger.error("修改公文单加锁异常.[EdocUpdateUserObjectHandler userOfficeObjectAdd]", e) ; 
			}
		}
	}
	
	@HandleNotification(type = NotificationType.EdocUserOfficeObjectRomove)
	public void userOfficeObjectRemove(Object o) {
		if(o instanceof String[]){
			try{
				String[] arr=(String[])o;
				String objId=arr[0];
				String userId=arr[1];
				edocSummaryManager.deleteUpdateObj(objId,userId);
				// 调试信息
				if (logger.isDebugEnabled()) {
					if(edocSummaryManager instanceof EdocSummaryManagerImpl)
					{
						EdocSummaryManagerImpl impl =(EdocSummaryManagerImpl) edocSummaryManager;
						logger.debug("修改公文单解锁成功！【EdocUpdateUserObjectHandler.userOfficeObjectRemove]"+impl.getUseObjectList().get(objId));
					}
				}
			}catch(Exception e){
				logger.error("修改公文单解锁失败![EdocUpdateUserObjectHandler userOfficeObjectRemove]", e) ; 
			}
		}
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public HandWriteManager getHandWriteManager() {
		return handWriteManager;
	}

	public void setHandWriteManager(HandWriteManager handWriteManager) {
		this.handWriteManager = handWriteManager;
	}

	public HtmlHandWriteManager getHtmlHandWriteManager() {
		return htmlHandWriteManager;
	}

	public void setHtmlHandWriteManager(HtmlHandWriteManager htmlHandWriteManager) {
		this.htmlHandWriteManager = htmlHandWriteManager;
	}
}
