package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.util.InquiryLock;
import com.seeyon.v3x.util.annotation.HandleNotification;
/**
 * 调查监听，包括载入全部调查板块以及在修改或审核调查时的加锁、解锁
 */
public class InquiryNotificationHandler {
	private static final Log logger = LogFactory.getLog(InquiryNotificationHandler.class);
	private InquiryManager inquiryManager;

	@HandleNotification(type=NotificationType.InquiryLoadAllTypes)
	public void loadAllInquiryTypes(Object o) {
		try {
			this.getInquiryManager().initAllSurveyType();
			if(logger.isDebugEnabled()) {
				logger.debug("双机同步，备机载入全部调查板块成功.");
			}
		} catch(Exception e) {
			logger.error("双机同步，备机载入全部调查板块时出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.InquiryLock)
	public void lockInquiryWhenEditOrAudit(Object o) {
		try {
			if(o!=null && o instanceof InquiryLock) {
				InquiryLock lock = (InquiryLock)o;
				this.getInquiryManager().lock(lock.getInquiryid(), lock.getUserid(), lock.getAction());
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机调查加锁动作成功，锁信息为：\n" + BeanUtils.describe(lock));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机调查加锁出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.InquiryUnLock)
	public void unlockInquiryAfterEditOrAudit(Object o) {
		try {
			if(o!=null) {
				Long inquirId = (Long) o;
				this.getInquiryManager().unlock(inquirId);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机调查解锁动作[调查ID：" + inquirId + "]成功.");
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机调查解锁出现异常：", e);
		}
	}
	
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}
	
}
