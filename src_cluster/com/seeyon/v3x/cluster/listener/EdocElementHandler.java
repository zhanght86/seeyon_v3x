package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.annotation.HandleNotification;

public class EdocElementHandler {
	protected static final Log logger = LogFactory.getLog(EdocElementHandler.class);
	private EdocElementManager edocElementManager;
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}

	//传递loginAccount,elementId
	@HandleNotification(type = NotificationType.EdocElementElementTable)
	public void saveElementTable(Object o){
		try {
			if(o instanceof Object[]){
				Object[] message = (Object[])o;
				EdocElement element = edocElementManager.getEdocElementsById(Long.parseLong(message[1].toString()));
				if(message.length == 2){//传递 loginAccountId,elementId
					edocElementManager.updateElementTable(message[0].toString(),element);
				}
				if(logger.isDebugEnabled()){
					logger.debug("集群-更新elemntTable："+BeanUtils.describe(element));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.EdocElementCmpElementsTable)
	public void saveCmpElementsTable(Object o){
		try {
			List<V3xOrgAccount> accounts=null;
	    	try{
	    		accounts=orgManager.getAllAccounts();
	    	}catch(Exception e)
	    	{
	    		logger.error("",e);
	    	}
	    	List<EdocElement> groupEles = edocElementManager.listElementByAccount(0L);
	    	for(V3xOrgAccount account: accounts){
	    		List<EdocElement> allElements=edocElementManager.listElementByAccount(account.getId());
	    		if(allElements != null && !allElements.isEmpty()){
	    			continue;
	    		}
	    		allElements = new ArrayList<EdocElement>();
	    		for(EdocElement ele:groupEles)
	    		{
	    			EdocElement tempEle=ele.clone(account.getId());
	    			allElements.add(tempEle);
	    		}
	    		edocElementManager.saveCmpElementTable(account.getId(), allElements);
	    	}
	    	if(logger.isDebugEnabled()){
	    		logger.debug("集群-重新加载 单位：元素：");
	    	}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
