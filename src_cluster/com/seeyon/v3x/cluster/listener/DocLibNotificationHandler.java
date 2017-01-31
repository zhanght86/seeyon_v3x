package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.DocLibManagerImpl;
import com.seeyon.v3x.doc.util.Constants.OperEnum;

public class DocLibNotificationHandler {
	
	protected static final Log logger = LogFactory.getLog(DocLibNotificationHandler.class);
	
	DocLibManager docLibManager;
	
	//html公文正文处理
	@HandleNotification(type = NotificationType.DocLibManagerInitPart)
	public void DocLibManagerInitPart(Object o) {
		if(o instanceof Object[]){
			try{
				
				Object[] arg=(Object[])o;
				OperEnum oper=(OperEnum)arg[0];
				List<Long> docLibIds=(List<Long>)arg[1];
				List<DocLib> docLibs=docLibManager.getDocLibByIds(docLibIds);
//				
				if(docLibManager instanceof DocLibManagerImpl){
					DocLibManagerImpl impl=(DocLibManagerImpl)docLibManager;
					impl.initPart(oper, docLibs);
					//	调试信息
					if (logger.isDebugEnabled()) {
						logger.debug("集群-文档库处理initpart执行成功!【DocLibNotificationHandler.DocLibManagerInitPart】");
					}
				}
				
			}catch(Exception e){
				logger.error("集群-文档库处理initpart执行失败。【DocLibNotificationHandler.DocLibManagerInitPart】", e) ; 
			}
		}
	}
	

	//html公文正文处理
	@HandleNotification(type = NotificationType.DocLibManagerUpdate)
	public void DocLibManagerUpdate(Object o) {
		if(o instanceof Object[]){
			try{
				Object[] arg=(Object[])o;
				Observable ob=(Observable)arg[0];
				if(docLibManager instanceof DocLibManagerImpl){
					DocLibManagerImpl impl=(DocLibManagerImpl)docLibManager;
					impl.update(ob, arg[1]);
					//	调试信息
					if (logger.isDebugEnabled()) {
						logger.debug("集群-文档库处理update执行成功");
						
					}
				}
				
			}catch(Exception e){
				logger.error("集群-文档库处理update执行失败：", e) ; 
			}
		}
	}


	public DocLibManager getDocLibManager() {
		return docLibManager;
	}


	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
}