package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.util.annotation.HandleNotification;
/**
 * 监听对HR职员信息所作的增、删、改操作，同步缓存
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-13
 */
public class StaffInfoNotificationHandler {
	private static final Log logger = LogFactory.getLog(StaffInfoNotificationHandler.class);
	
	private StaffInfoManager staffInfoManager;
	
	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}
	
	@HandleNotification(type=NotificationType.HrAddOrUpdateStaffInfo)
	public void addOrUpdateStaffInfo(Object o) {
		if(o != null) {
			try {
				Long memberId = (Long) o;
				StaffInfo staffinfo = this.staffInfoManager.getStaffInfoByIdFromDB(memberId);
				if(staffinfo != null) {
					this.staffInfoManager.syncCache(staffinfo, null, Constants.ActionType.create);
					
					if(logger.isDebugEnabled()) {
						logger.debug("增加或修改职员信息时，同步集群缓存成功：" + BeanUtils.describe(staffinfo));
					}
				}
			} catch(Exception e) {
				logger.error("增加或修改职员信息时，同步集群缓存过程中出现异常：", e);
			}
		}
	}

	@HandleNotification(type=NotificationType.HrDeleteStaffInfo)
	public void deleteStaffInfos(Object o) {
		if(o != null) {
			try {
				Long memberId = (Long) o;
				this.staffInfoManager.syncCache(null, memberId, Constants.ActionType.delete);
				
				if(logger.isDebugEnabled()) {
					logger.debug("删除职员信息时，同步集群缓存成功[职员ID=" + memberId + "]");
				}
			} catch(Exception e) {
				logger.error("删除职员信息时，同步集群缓存过程中出现异常：", e);
			}
		}
	}
	
}
