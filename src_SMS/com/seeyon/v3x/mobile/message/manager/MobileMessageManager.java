/**
 * 
 */
package com.seeyon.v3x.mobile.message.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.mobile.message.domain.AppMessageRule;
import com.seeyon.v3x.mobile.message.domain.MobileMessageObject;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-19
 */
public interface MobileMessageManager {
	
	public static final String CONFIG_CATEGORY_MOBILE = "v3x_mobile";
	public static final String CONFIG_ITEM_MOBILE_SUFFIX = "suffix";//短信后缀
	/**
	 * config_item  : AccountOfCanUseWap/AccountOfCanUseSMS/AccountOfCanUseSMS
	 * config_value : true/false
	 * org_account_id : accountId
	 */
	public static final String CONFIG_ITEM_MOBILE_AccountOfCanUseWap = "AccountOfCanUseWap";
	public static final String CONFIG_ITEM_MOBILE_AccountOfCanUseSMS = "AccountOfCanUseSMS";
	public static final String CONFIG_ITEM_MOBILE_AccountOfCanUseWappush = "AccountOfCanUseWappush";
	
	public static final String CONFIG_ITEM_MOBILE_CanSendAuth = "CanSendAuth";
	public static final String CONFIG_ITEM_MOBILE_CanRecieveAuth = "CanRecieveAuth";
	
	/**
	 * A8系统 需要发送到手机上的个人消息
	 * 
	 * @param content
	 *            发送的内容
	 * @param senderId
	 *            发送者的Id
	 * @param reciverIds
	 *            接受者的Id
	 */
	public void sendMobilePersonMessage(String content, Long senderId,
			java.util.Date time, Long... reciverIds);

	/**
	 * A8系统 需要发送到手机上的系统消息
	 * 
	 * @param content
	 *            发送的内容
	 * @param ObjectId
	 *            事项的Id
	 * @param type
	 *            事项的类型 {@link ApplicationCategoryEnum}
	 * @param time
	 *            发送消息的时间
	 * @param senderId
	 *            发送者的Id
	 * @param reciverIds
	 *            接受者的Id
	 */
	public void sendMobileSystemMessage(String content, Long ObjectId,
			int category, java.util.Date time, Long senderId,
			Long... reciverIds);

	/**
	 * 
	 * @param content
	 *            发送的内容
	 * @param ObjectId
	 *            事项的Id
	 * @param type
	 *            事项的类型
	 * @param time
	 *            发送消息的时间
	 * @param senderId
	 *            发送者的Id
	 * @param reciverIds
	 *            接受者的Id
	 */
	public void sendMobileSystemMessage(String content, Long ObjectId,
			int category, java.util.Date time, Long senderId,
			Collection<Long> reciverIds);
	
	/**
	 * 发送短信后，更新状态
	 * 
	 * @param messageId
	 * @param isSuccess
	 */
	public void updateMessageState(Long messageId, boolean isSuccess);

	/**
	 * 
	 * @return
	 */
	public MobileMessageObject getMessageQueueObject();
	
	public void saveToMessageQueue(MobileMessageObject obj);

	/**
	 * 系统是否可以使用手机短信，条件：短信发送接口或wappush发送接口存在实现类
	 * 
	 * @return
	 */
	public boolean isValidateMobileMessage();

	/**
	 * 系统是否可以使用普通手机短信，条件：短信发送接口存在实现类
	 * 
	 * @return
	 */
	public boolean isValidateSMS();

	/**
	 * 系统是否可以使用普wappush，条件：wappush发送接口存在实现类
	 * 
	 * @return
	 */
	public boolean isValidateWappush();

	/**
	 * 是否可以使用wap访问协同，由管理员设定
	 * 
	 * @return true - 能
	 */
	public boolean isCanUseWap();

	/**
	 * 设定是否可以使用wap访问协同，由管理员设定
	 * 
	 * @param s
	 *            true - 能
	 */
	public void setCanUseWap(boolean s);

	/**
	 * 是否可以使用短信，由管理员设定
	 * 
	 * @return true - 能
	 */
	public boolean isCanUseSMS();

	/**
	 * 设定是否可以使用短信，由系统/集团管理员设定
	 * 
	 * @param s
	 *            true - 能
	 */
	public void setCanUseSMS(boolean s);

	/**
	 * 是否可以使用Wappush，由管理员设定
	 * 
	 * @return true - 能
	 */
	public boolean isCanUseWappush();

	/**
	 * 设定是否可以使用Wappush，由管理员设定
	 * 
	 * @param s
	 *            true - 能
	 */
	public void setCanUseWappush(boolean s);

	/**
	 * 获取能够使用wap的单位 只用于查看使用，禁止对得到的list进行修改（集群消息通知除外）
	 * 
	 * @return 单位Id
	 */
	public List<Long> getAccountOfCanUseWap();
	/**
	 * 禁止对得到的list进行修改（集群消息通知除外）
	 * @return
	 */
	public List<Long> getAccountOfCanUseSMS();
	
	public List<Long> getAccountOfCanUseWappush();

	public boolean isAccountOfCanUseWap(long accountId);

	public boolean isAccountOfCanUseSMS(long accountId);

	public boolean isAccountOfCanUseWappush(long accountId);

	/**
	 * 设置能够使用wap的单位
	 * 
	 * @param accountId
	 *            单位Id
	 */
	public void setAccountOfCanUseWap(List<Long> accountId);

	public void setAccountOfCanUseSMS(List<Long> accountId);

	public void setAccountOfCanUseWappush(List<Long> accountId);

	/**
	 * 应用发送短信方式规则: 如果没有找到该应用的规则，就表示该应用不发送短信和wappush
	 * 
	 * @return key : ApplicationCategoryEnum.key
	 */
	public Map<Integer, AppMessageRule> getAppMessageRules();

	public void setAppMessageRules(List<AppMessageRule> rules);
	
	public void removeMessageRules(List<Integer> apps);

	/**
	 * 得到授权信息
	 * 
	 * @param accountId
	 * @return “type|id,”的字符串，如：Department|1235643132,Member|234651314,Team|-109918234
	 */
	public String getCanSendAuth(long accountId);

	/**
	 * 单位管理员授权，哪些用户可以发送短信（通过通信录、首页手机图标等）
	 * 
	 * @param authStr
	 *            “type|id,”的字符串，如：Department|1235643132,Member|234651314,Team|-109918234
	 * @param accountId
	 */
	public void setCanSendAuth(String authStr, long accountId);

	/**
	 * 判断用户是否能够使用“发送短信”功能。 条件：我的单位具有使用SMS的权限，并且单位管理员授给我发送权限。
	 * 
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	public boolean isCanSend(long memberId, long accountId);

	/**
	 * 得到授权信息
	 * 
	 * @param accountId
	 * @return “type|id,”的字符串，如：Department|1235643132,Member|234651314,Team|-109918234
	 */
	public String getCanRecieveAuth(long accountId);

	/**
	 * 单位管理员授权，哪些用户可以接收短信
	 * 
	 * @param authStr
	 *            authStr
	 *            “type|id,”的字符串，如：Department|1235643132,Member|234651314,Team|-109918234
	 * @param accountId
	 */
	public void setCanRecieveAuth(String authStr, long accountId);

	/**
	 * 判断用户是否能够使用“接收短信”功能。 条件：我的单位具有使用SMS或者wappush的权限，并且单位管理员授给我接收权限。
	 * 
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	public boolean isCanRecieve(long memberId, long accountId);

	public List<Integer> getAppEnumListOfSMS();

	public List<Integer> getAppEnumListOfWappush();
    
    /**
     * 给私人通讯录发送短信
     * @param content 内容
     * @param senderId 发送者ID
     * @param time 时间
     * @param mobilePhoneStr 接收手机号码串
     */
    public void sendPersonalMessage(String content, Long senderId, Date time, String mobilePhoneStr);
    
    /**
     * 设置短信后缀
     * @param suffix
     */
    public void setSMSSuffix(String suffix);
    
    /**
     * 得到短信后缀
     * @return
     */
    public String getSMSSuffix();
    
    /**
     * 修改可发送短信授权，仅在内存中修改中使用
     * @param authStr
     * @param accountId
     */
    public void setCanSendMemberInMemory(String authStr,long accountId);
    /**
     * 修改可接受短信授权，仅在内存中修改中使用
     * @param authStr
     * @param accountId
     */
    public void setCanReceiveMemberInMemory(String authStr,long accountId);
    
}
