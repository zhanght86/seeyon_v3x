package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.systemvalue.UserFlowId;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FormFlowid;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.util.annotation.HandleNotification;

/**
 * 表单集群通知监听。
 * 
 * @author wangwenyou
 * 
 */
public class FormNotificationHandler {
	protected static final Log logger = LogFactory
			.getLog(FormNotificationHandler.class);

	@HandleNotification(type = NotificationType.FormAppReload)
	public void formAppReload(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("Reload form application：" + o);
				Long id = (Long) o;
				SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
				ISeeyonFormAppManager fmanager = fruntime.getAppManager();
				ISeeyonForm_Application afapp = fmanager.findById((Long) o);
				if(afapp != null){
					afapp.setFId((Long) o);
					afapp.loadFromDB();
				}else{
					SeeyonForm_ApplicationImpl app = new SeeyonForm_ApplicationImpl();
					app.setFId(id);
					app.loadFromDB();
					fmanager.regApp(app);
				}
			} catch (Exception e) {
				logger.error("Reload form application：", e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}	
	@HandleNotification(type = NotificationType.FormAppUnloadHibernateResource)
	public void unloadAppHibernateResource(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("unload hibernate resource：" + o);
				Long id = (Long) o;
				SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
				ISeeyonFormAppManager fmanager = fruntime.getAppManager();
				ISeeyonForm_Application afapp = fmanager.findById((Long) o);
				if(afapp != null){
					afapp.unloadAppHibernatResorece();
				}else{
					logger.warn("not found" + id );
				}
			} catch (Exception e) {
				logger.error("unload hibernate resource：", e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}
	@HandleNotification(type = NotificationType.FormAppUnreg)
	public void formAppUnreg(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("Unreg form application：" + o);
				Long id = (Long) o;
				SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
				ISeeyonFormAppManager fmanager = fruntime.getAppManager();
				if (fmanager.findById(id, true) != null)
					fmanager.unRegApp(id);
			} catch (Exception e) {
				logger.error("Unreg form application：" + o, e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}		
	@HandleNotification(type = NotificationType.FormAppReg)
	public void formAppReg(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("Reg form application：" + o);
				Long id = (Long) o;
				SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
				ISeeyonFormAppManager fmanager=fruntime.getAppManager();
				SeeyonForm_ApplicationImpl app = new SeeyonForm_ApplicationImpl();
				app.setFId(id);
				app.loadFromDB();
				fmanager.regApp(app);
			} catch (Exception e) {
				logger.error("Reg form application：" + o, e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}	
	
	@HandleNotification(type = NotificationType.FormFlowIdReg)
	public void formFlowIdReg(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("Reg FormFlowIdReg：" + o);
				IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
				Long id = (Long) o;
				FormFlowid formFlowid = iOperBase.queryFlowIdById(id);
				if(formFlowid !=null){
					UserFlowId userFlowId = new UserFlowId();
			    	BeanUtils.copyProperties(userFlowId, formFlowid);
			    	SeeyonForm_Runtime.getInstance().getSystemValueManager().reg(formFlowid.getId().toString(), userFlowId);
				}
			} catch (Exception e) {
				logger.error("Reg FormFlowIdReg：" + o, e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}
	
	@HandleNotification(type = NotificationType.FormFlowIdUnReg)
	public void formFlowIdUnReg(Object o) {
		if (o instanceof Long) {
			try {
				logger.debug("UnReg FormFlowIdUnReg：" + o);
				Long id = (Long) o;
				SeeyonForm_Runtime.getInstance().getSystemValueManager().unReg(id.toString());
			} catch (Exception e) {
				logger.error("UnReg FormFlowIdUnReg：" + o, e);
			}
		} else {
			logger.warn("不可识别的参数：" + o);
		}
	}
}
