package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class EdocOpenController  extends BaseController {
	
	private ConfigManager configManager;
	private AppLogManager appLogManager;

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public ModelAndView showEdocOpenSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// boolean oldFlag = true ;
		boolean allowUpdateAttachment=true;//数据库不存在allowUpdateAttachment这个配置项
		ModelAndView mav = new ModelAndView("edoc/edocSwitch");
		//List<ConfigItem> groupconfigItems = systemConfig.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY, ConfigItem.Default_Account_Id);		
		Long accountId = CurrentUser.get().getAccountId();		
		List<ConfigItem> configItemsTemp = configManager
				.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY,
						accountId);
		List<ConfigItem> configItems = new ArrayList<ConfigItem>();
		for (ConfigItem configItem : configItemsTemp) {
			if (!configItem.getConfigItem().equals(IConfigPublicKey.TimeLable)) {
				configItems.add(configItem);
			}
		}
		if(configItems==null || configItems.size()<=0)
		{
			configManager.saveInitCmpConfigData(IConfigPublicKey.EDOC_SWITCH_KEY, accountId);
			configItems = configManager.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY, accountId);
		}
		for(ConfigItem configItem : configItems) {
			// if(configItem.getConfigItem().equals(IConfigPublicKey.TimeLable))
			// {
			// oldFlag = false ;
			// }
			if(configItem.getConfigItem().equals(IConfigPublicKey.Allow_Update_Attachment)){
				allowUpdateAttachment=false;
			}
		}
		// if(oldFlag) {
		// ConfigItem cf =
		// this.configManager.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY,
		// IConfigPublicKey.TimeLable, ConfigItem.Default_Account_Id) ;
		// ConfigItem ncf = new ConfigItem() ;
		// ncf.setIdIfNew();
		// ncf.setConfigCategory(cf.getConfigCategory());
		// ncf.setConfigCategoryName(cf.getConfigCategoryName());
		// ncf.setConfigDescription(cf.getConfigDescription());
		// ncf.setConfigItem(cf.getConfigItem());
		// ncf.setConfigType(cf.getConfigType());
		// ncf.setConfigValue(cf.getConfigValue());
		// ncf.setCreateDate(cf.getCreateDate());
		// ncf.setExtConfigValue(cf.getExtConfigValue());
		// ncf.setModifyDate(cf.getModifyDate());
		// ncf.setOrgAccountId(accountId);
		// this.configManager.addConfigItem(ncf);
		// }
		if(allowUpdateAttachment){
			ConfigItem cf = this.configManager.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, IConfigPublicKey.Allow_Update_Attachment, ConfigItem.Default_Account_Id) ;
			ConfigItem ncf = new ConfigItem() ;	
			ncf.setIdIfNew();
			ncf.setConfigCategory(cf.getConfigCategory());
			ncf.setConfigCategoryName(cf.getConfigCategoryName());
			ncf.setConfigDescription(cf.getConfigDescription());
			ncf.setConfigItem(cf.getConfigItem());
			ncf.setConfigType(cf.getConfigType());
			ncf.setConfigValue(cf.getConfigValue());
			ncf.setCreateDate(cf.getCreateDate());
			ncf.setExtConfigValue(cf.getExtConfigValue());
			ncf.setModifyDate(cf.getModifyDate());
			ncf.setOrgAccountId(accountId);
			this.configManager.addConfigItem(ncf);
			configItems.add(ncf);
		}
		mav.addObject("configItems",configItems);
		return mav;	
	}
	/**
	 * 读取公文发起权
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showEdocSendSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("edoc/edocSendSet");
		
		Long accountId = CurrentUser.get().getAccountId();
		
		String edocSendCreates="";
		String edocRecCreates="";
		String edocSignCreates="";
		
		ConfigItem edocSendItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SEND, accountId);
		ConfigItem edocRecItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_REC, accountId);
		ConfigItem edocSignItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SIGN, accountId);
		
		if(edocSendItem!=null){edocSendCreates=edocSendItem.getExtConfigValue();}	
		if(edocRecItem!=null){edocRecCreates=edocRecItem.getExtConfigValue();}
		if(edocSignItem!=null){edocSignCreates=edocSignItem.getExtConfigValue();}
				
		mav.addObject("edocSendCreates",edocSendCreates);
		mav.addObject("edocRecCreates",edocRecCreates);
		mav.addObject("edocSignCreates",edocSignCreates);		
		
		return mav;	
	}
	/**
	 * 保存公文发起权
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveEdocSendSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		User user=CurrentUser.get();
		Long accountId = user.getAccountId();	
		
		String edocSendCreates=request.getParameter("edocSendCreates");
		String edocRecCreates=request.getParameter("edocRecCreates");
		String edocSignCreates=request.getParameter("edocSignCreates");
		
		ConfigItem edocSendItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SEND, accountId);
		ConfigItem edocRecItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_REC, accountId);
		ConfigItem edocSignItem=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SIGN, accountId);
		
		if(edocSendItem==null)
		{
			edocSendItem=getNewConfigItem(accountId,IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SEND,edocSendCreates);
			configManager.addConfigItem(edocSendItem);
		}
		else
		{
			edocSendItem.setExtConfigValue(edocSendCreates);
			configManager.updateConfigItem(edocSendItem);
		}
		
		if(edocRecItem==null)
		{
			edocRecItem=getNewConfigItem(accountId,IConfigPublicKey.EDOC_CREATE_ITEM_KEY_REC,edocRecCreates);
			configManager.addConfigItem(edocRecItem);
		}
		else
		{
			edocRecItem.setExtConfigValue(edocRecCreates);
			configManager.updateConfigItem(edocRecItem);
		}
		
		if(edocSignItem==null)
		{
			edocSignItem=getNewConfigItem(accountId,IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SIGN,edocSignCreates);
			configManager.addConfigItem(edocSignItem);
		}
		else
		{
			edocSignItem.setExtConfigValue(edocSignCreates);
			configManager.updateConfigItem(edocSignItem);
		}
		//记录日志
		appLogManager.insertLog(user, AppLogAction.Edoc_SendSetAuthorize, user.getName());
		PrintWriter out = response.getWriter();
        super.printV3XJS(out);
        out.println("<script>");
        //out.println("alert('操作成功!');");
        out.println("alert('"+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources","formapp.saveoperok.label")+"')");
    	out.println("parent.location.reload(true);");
    	out.println("</script>");
    	return null;
	}
	
	private ConfigItem getNewConfigItem(Long accountId,String item,String value)
	{
		ConfigItem edocSendItem=new ConfigItem();
		edocSendItem.setIdIfNew();
		edocSendItem.setConfigCategory(IConfigPublicKey.EDOC_CREATE_KEY);
		edocSendItem.setConfigItem(item);
		edocSendItem.setExtConfigValue(value);
		edocSendItem.setOrgAccountId(accountId);
		return edocSendItem;
	}
	
	public ModelAndView saveEdocOpenSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("edoc/edocSwitch");
		User user=CurrentUser.get();
		Long accountId =user.getAccountId();
		List<ConfigItem> groupconfigItems = configManager.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY, ConfigItem.Default_Account_Id);
		List<ConfigItem> configItems = configManager.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY, accountId);		
		String itemValue=null;
		for(Iterator<ConfigItem> it =configItems.iterator();it.hasNext(); )
		{
			ConfigItem configItem = it.next();
			if (configItem.getConfigItem().equals(IConfigPublicKey.TimeLable)) it.remove();
			itemValue=request.getParameter(configItem.getConfigItem());
			if(itemValue!=null)
			{
				configItem.setConfigValue(itemValue);
				configManager.updateConfigItem(configItem);
			}
		}
		//记录应用日志
		appLogManager.insertLog(user, AppLogAction.Edoc_OpenSetAuthorize, user.getName());
		mav.addObject("configItems",configItems);
		mav.addObject("operateResult",true);
		
		return mav;	
	}

	public ModelAndView defaultEdocOpenSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("edoc/edocSwitch");
		
		Long accountId = CurrentUser.get().getAccountId();
		Long groupAccountId = new Long(1L);
		List<ConfigItem> configItems = configManager.listAllConfigByCategory(IConfigPublicKey.EDOC_SWITCH_KEY, accountId);		
		String itemValue=null;
		for(ConfigItem configItem:configItems)
		{
			itemValue=configManager.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, configItem.getConfigItem(),groupAccountId).getConfigValue();
			if(itemValue!=null)
			{
				configItem.setConfigValue(itemValue);
				configManager.updateConfigItem(configItem);
			}
		}
		mav.addObject("configItems",configItems);	
		mav.addObject("operateResult",true);
		return mav;	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}