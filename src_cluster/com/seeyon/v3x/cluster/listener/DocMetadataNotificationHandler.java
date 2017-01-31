package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.beans.NotificationDocMetadataDefinition;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.doc.manager.MetadataDefManager;
import com.seeyon.v3x.util.annotation.HandleNotification;

public class DocMetadataNotificationHandler {
	protected static final Log logger = LogFactory.getLog(DocMetadataNotificationHandler.class);
	
	private MetadataDefManager detadataDefManager ;
	
	@HandleNotification(type = NotificationType.DocMetadataInitPart)
	public void docMetadataInitPart(Object o) {
		if(o instanceof NotificationDocMetadataDefinition){
			try{
				NotificationDocMetadataDefinition bean = (NotificationDocMetadataDefinition)o ;
				if (logger.isDebugEnabled()) {
					logger.debug("NotificationDocMetadataDefinition的属性的值：" + bean.toString()) ;
				}
				//detadataDefManager.addMetadataDef(], metadataOptions)(bean.getOper(),bean.getDefs());
				detadataDefManager.initPart(bean.getOper(), bean.getDefs()) ;
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("NotificationDocMetadataDefinition docMetadataInitPart：" + BeanUtils.describe(bean));
				}
			}catch(Exception e){
				logger.error("NotificationDocMetadataDefinition docMetadataInitPart：", e) ; 
			}
		}
	}

	public MetadataDefManager getDetadataDefManager() {
		return detadataDefManager;
	}

	public void setDetadataDefManager(MetadataDefManager detadataDefManager) {
		this.detadataDefManager = detadataDefManager;
	}
}
