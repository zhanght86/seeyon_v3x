package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.beans.NotificationFormLock;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.collaboration.manager.impl.FormLockManager;
import com.seeyon.v3x.util.annotation.HandleNotification;

 public class FormLockHandler {
	protected static final Log logger = LogFactory.getLog(FormLockHandler.class);
	
	@HandleNotification(type = NotificationType.FormLockAdd)
	public void formLockAdd(Object o) {
		if(o instanceof NotificationFormLock){
			try{
				NotificationFormLock bean = (NotificationFormLock)o ;
				if (logger.isDebugEnabled()) {
					logger.debug("notificationFormLock的属性的值：" + bean.toString()) ;
				}
				FormLockManager.add(bean.getSummaryId(), bean.getAffairId(), bean.getMemberId(), bean.getLoginName(),bean.getLoginTimestamp());
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("NotificationFormLock formLockAdd：" + BeanUtils.describe(bean));
				}
			}catch(Exception e){
				logger.error("NotificationFormLock formLockAdd：", e) ; 
			}
		}
	}
	
	@HandleNotification(type = NotificationType.FormLockRemove)
	public void formLockRemove(Object o) {
		if(o instanceof Long){
			try{
				Long summaryId = (Long)o ;
				if (logger.isDebugEnabled()) {
					logger.debug("formLockRemove：" +summaryId) ;
				}
				FormLockManager.remove(summaryId);
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("NotificationFormLock  formLockRemove："+summaryId);
				}
			}catch(Exception e){
				logger.error("NotificationFormLock  formLockRemove", e) ; 
			}
		}
	}
}
