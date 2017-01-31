package com.seeyon.v3x.edoc.manager;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.exchange.util.Constants;

public class EdocSwitchHelper {
	
	private static String EDOC_SWITCH_allowUpdate="allowUpdate";
	private static String EDOC_SWITCH_createNumber="createNumber";
	private static String EDOC_SWITCH_selfFlow="selfFlow";
	private static String EDOC_SWITCH_handInputEdoc="handInputEdoc";
	private static String EDOC_SWITCH_defaultExchangeType="defaultExchangeType";
	private static String EDOC_SWITCH_timeSort="timesort";
	private static String EDOC_SWITCH_pdfEnable = "pdfEnable";
	
	private static ConfigManager systemConfig=(ConfigManager)ApplicationContextHolder.getBean("configManager");
	
	/**
	 * 时间顺序的修改
	 * @return
	 */
	public static boolean timesortUpdate()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();		
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_timeSort, accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_timeSort, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	
	/**
	 * 外来文登记，是否允许修改
	 * @return
	 */
	public static boolean canUpdateAtOutRegist()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();		
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_allowUpdate, accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_allowUpdate, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	/**
	 * w是否为公文单生成自动编号
	 * @return
	 */
	public static boolean isCreateOutoNumber()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_createNumber,accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_createNumber, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	/**
	 * 公文发起人可否自建流程
	 */
	public static boolean canSelfCreateFlow()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_selfFlow,accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_selfFlow, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	/**
	 * 是否允许拟文人修改附件
	 */
	public static boolean allowUpdateAttachment()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, IConfigPublicKey.Allow_Update_Attachment,accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, IConfigPublicKey.Allow_Update_Attachment, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	
	public static boolean canInputEdocWordNum(){
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();
		return canInputEdocWordNum(accountId);
	}
	/**
	 * 是否允许手工输入文号
	 * @return
	 */
	public static boolean canInputEdocWordNum(Long accountId)
	{
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_handInputEdoc,accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_handInputEdoc, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return true;}
		return "yes".equals(configItem.getConfigValue());
	}
	public static int getDefaultExchangeType(Long accountId)
	{
		
		ConfigItem configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_defaultExchangeType,accountId);
		if(configItem==null)
		{
			configItem=systemConfig.getConfigItem(IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_defaultExchangeType, ConfigItem.Default_Account_Id);
		}
		if(configItem==null){return Constants.C_iExchangeType_Dept;}
		if("depart".equals(configItem.getConfigValue()))
		{
			return Constants.C_iExchangeType_Dept;
		}
		else
		{
			return Constants.C_iExchangeType_Org;
		}
	}
	public static int getDefaultExchangeType()
	{
		User user=CurrentUser.get();
		long accountId=user.getLoginAccount();
		return getDefaultExchangeType(accountId);
	}
	/**
	 * 发送正文时是否转换为PDF格式正文
	 * 
	 * @return
	 */
//	public static boolean canEnablePdfDocChange() {
//		User user = CurrentUser.get();
//		long accountId = user.getLoginAccount();
//		ConfigItem configItem = systemConfig.getConfigItem(
//				IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_pdfEnable,
//				accountId);
//		if (configItem == null) {
//			configItem = systemConfig.getConfigItem(
//					IConfigPublicKey.EDOC_SWITCH_KEY, EDOC_SWITCH_pdfEnable,
//					ConfigItem.Default_Account_Id);
//		}
//		if (configItem == null) {
//			return true;
//		}
//		return "yes".equals(configItem.getConfigValue());
//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}


}
