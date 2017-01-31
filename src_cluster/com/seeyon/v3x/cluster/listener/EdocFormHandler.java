package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.util.annotation.HandleNotification;

public class EdocFormHandler {
	protected static final Log logger = LogFactory.getLog(EdocFormHandler.class);
	private EdocFormManager edocFormManager;
	
	public void setEdocFormManager(EdocFormManager edocFormManager) {
		this.edocFormManager = edocFormManager;
	}

	@HandleNotification(type = NotificationType.DefaultEdocFormReSet)
	public void setDefaultEdocForm(Object o){
		try {
			if(o instanceof Object[]){
				Object[] message = (Object[]) o	;
				EdocForm eForm = edocFormManager.getEdocForm(Long.valueOf(message[0].toString()));	
				edocFormManager.setDefaultEdocForm(Long.parseLong(message[1].toString()), eForm.getType(), eForm);
				if(logger.isDebugEnabled()){
					logger.debug("集群：设置默认的edocForm:单位id为"+message[1].toString()+","+BeanUtils.describe(eForm));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.DefaultEdocFormRemove)
	public void removeDefaultEdocForm(Object o){
		try {
			if(o instanceof Object[]){
				Object[] message = (Object[]) o	;
				edocFormManager.removeDefaultEdocForm(Long.parseLong(message[0].toString()), Integer.parseInt(message[1].toString()));
				if(logger.isDebugEnabled()){
					logger.debug("集群，移除默认EdocForm:"+message[0].toString());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
