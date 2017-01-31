package com.seeyon.v3x.mobile.message.manager;

import static com.seeyon.v3x.organization.domain.OrgModifyState.HeadModifyState;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.adapter.AdapterMobileWapPushManager;
import com.seeyon.v3x.mobile.dao.MobileMessageDao;
import com.seeyon.v3x.mobile.message.dialect.MobileAppDialect;
import com.seeyon.v3x.mobile.message.domain.AppMessageRule;
import com.seeyon.v3x.mobile.message.domain.MessageReciver;
import com.seeyon.v3x.mobile.message.domain.MobileMessage;
import com.seeyon.v3x.mobile.message.domain.MobileMessageObject;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;
import com.seeyon.v3x.organization.domain.OrgModifyState;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.util.Strings;

/**
 * 移动短信/Wappush的管理接口<br>
 * 概念<br>
 * 1、发送短信 send    ——通过通信录、首页手机图标直接给其他人员发送SMS，控制发送人的权限(<code>isCanSend()</code>)，不控制接收者的权限<br>
 * 2、接收短信 recieve ——将系统消息/在线交流消息转发到我的手机，控制接收者的权限(<code>isCanRecieve()</code>)，不限制发送者的权限<br>
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-3
 */
public class MobileMessageManagerImpl implements MobileMessageManager, ApplicationContextAware {
	private static Log log = LogFactory.getLog(MobileMessageManagerImpl.class);
	
	/**
	 * config_item  : CanUseWap/CanUseSMS/CanUseWappush
	 * config_value : true/false
	 */
	private static final String CONFIG_ITEM_MOBILE_CanUseWap = "CanUseWap";
	private static final String CONFIG_ITEM_MOBILE_CanUseSMS = "CanUseSMS";
	private static final String CONFIG_ITEM_MOBILE_CanUseWappush = "CanUseWappush";
	
	/**
	 * config_item  : AppMessageRules + 应用key
	 * config_value : 首选方式,在线是否发送 如 SMS,true
	 */
	public static final String CONFIG_ITEM_MOBILE_AppMessageRules = "AppMessageRules";
	
	/**
	 * config_item : CanSendAuth/CanRecieveAuth + accountId
	 * extConfigValue : 授权信息
	 * org_account_id : accountId
	 */
	
	private static  boolean isCanUseWap;
	private static boolean isCanUseSMS;
	private static boolean isCanUseWappush;
	
	private static List<Long> accountOfCanUseWap = new ArrayList<Long>();
	private static List<Long> accountOfCanUseSMS = new ArrayList<Long>();
	private static List<Long> accountOfCanUseWappush = new ArrayList<Long>();
	
	private Map<Integer, AppMessageRule> appMessageRules = new HashMap<Integer, AppMessageRule>();

	/**
	 * key 单位Id value 人Id
	 */
	private static Map<Long, Set<Long>> canSendMember = new HashMap<Long, Set<Long>>();
	private static Map<Long, Set<Long>> canRecieveMember = new HashMap<Long, Set<Long>>();
	
	/**
	 * config_item : 移动的特征号
	 * 
	 */
	private static final String CONFIG_ITEM_FeatureCode = "FeatureCode";
	
	private ApplicationContext applicationContext;
	
    private List<Integer> appEnumListOfSMS = new ArrayList<Integer>();
    
    private List<Integer> appEnumListOfNotNeedFeature = new ArrayList<Integer>();
    
    private List<Integer> appEnumListOfWappush = new ArrayList<Integer>();
	
	private OrgManager orgManager;
	
	private MobileMessageDao mobileMessageDao;
	
	private List<MobileMessageObject> messageQueue = Collections.synchronizedList(new ArrayList<MobileMessageObject>(100));
	
	private ConfigManager configManager;
	
	private int featureCodeStep = 100;
	
	private int featureCodeIndex = 0;
	
	private FeatureCodeManager featureCodeManager;
	
	private Map<Integer, MobileAppDialect> mobileAppDialects = new HashMap<Integer, MobileAppDialect>();
	
	private MobileAppDialect baseMobileAppDialect;
	
	private MobileMessageManager mobileMessageManager;
	
	private String url;
	
	private AdapterMobileWapPushManager adapterMobileWapPushManager;
	
	private AdapterMobileMessageManger adapterMobileMessageManger;
	
	private boolean booleanMessage;
	
	private boolean booleanWappush;
	
	private boolean booleanreciverMessage;
	
	private long reciveTime = 1 * 60 * 1000;
	
	private long sleepTime = 100;
	
	private MessageSendThread messageSendThread;
	
	private Date lastModifyOrgTimestamp = new Date();
	
	private int largestNum;
	
	

	public int getLargestNum() {
		return largestNum;
	}

	public void setLargestNum(int largestNum) {
		this.largestNum = largestNum;
	}

	public void setMobileMessageDao(MobileMessageDao mobileMessageDao) {
		this.mobileMessageDao = mobileMessageDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

    public void setAppEnumListOfSMS(List<String> appEnumListOfSMS){
    	for (String string : appEnumListOfSMS) {
    		this.appEnumListOfSMS.add(Integer.parseInt(string));
		}
    	
    	appEnumListOfSMS.clear();
    }

    public void setAppEnumListOfWappush(List<String> appEnumListOfWappush){
    	for (String string : appEnumListOfWappush) {
    		this.appEnumListOfWappush.add(Integer.parseInt(string));
		}
    	
    	appEnumListOfWappush.clear();
    }

	public void setFeatureCodeStep(int featureCodeStep) {
		this.featureCodeStep = featureCodeStep;
	}
	
	public void setReciveTime(int reciveTime) {
		this.reciveTime = reciveTime * 1000;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public void setMobileAppDialect(Map<String, MobileAppDialect> _mobileAppDialect) {
		Set<Map.Entry<String, MobileAppDialect>> en = _mobileAppDialect.entrySet();
		for (Map.Entry<String, MobileAppDialect> entry : en) {
			mobileAppDialects.put(new Integer(entry.getKey()), entry.getValue());
		}
	}

	public void setBaseMobileAppDialect(MobileAppDialect baseMobileAppDialect) {
		this.baseMobileAppDialect = baseMobileAppDialect;
	}
	
	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		AdapterMobileMessageManger adapterMobileMessageManger = null;
		AdapterMobileWapPushManager adapterMobileWapPushManager = null;
		
		{
			Map<String, AdapterMobileMessageManger> aMsgM = applicationContext.getBeansOfType(AdapterMobileMessageManger.class);
			for (Iterator<AdapterMobileMessageManger> iter = aMsgM.values().iterator(); iter.hasNext();) {
				AdapterMobileMessageManger am = iter.next();
				if(am.isAvailability()){
					adapterMobileMessageManger = am;
					break;
				}
			}
		}
		{
			Map<String, AdapterMobileWapPushManager> aWapMsgM = applicationContext.getBeansOfType(AdapterMobileWapPushManager.class);
			if(!aWapMsgM.isEmpty()){
				if(SystemEnvironment.hasPlugin(ProductInfo.PluginNoMapper.mobileWap.name())){
					for (Iterator<AdapterMobileWapPushManager> iter = aWapMsgM.values().iterator(); iter.hasNext();) {
						AdapterMobileWapPushManager am = iter.next();
						if(am.isAvailability()){
							adapterMobileWapPushManager = am;
							break;
						}
					}
				}
				else{
					log.warn("没有移动应用插件，将不启用Wappush");
				}
			}
		}
		
		if(adapterMobileMessageManger != null){
			this.adapterMobileMessageManger = adapterMobileMessageManger;
			this.booleanMessage = true;
			this.booleanreciverMessage = adapterMobileMessageManger.isSupportRecive();
		}
		
		if(adapterMobileWapPushManager != null){
			this.adapterMobileWapPushManager = adapterMobileWapPushManager;
			this.url = SystemEnvironment.getA8InternetSiteURL();
			if(Strings.isBlank(this.url)){
				booleanWappush = false;
				log.warn("没有设置服务器地址，将不启动Wappush");
			}
			else{
				this.booleanWappush = true;
				this.url += "/m?mid=";
			}
		}
		
		if(booleanMessage || booleanWappush){
			messageSendThread = new MessageSendThread();
			messageSendThread.start();
		}
		
		if(isValidateMobileMessage()){
			featureCodeManager = new FeatureCodeManager();
			featureCodeManager.setLength(4);
						
			String featureCode = null;//
			ConfigItem mobileConfig = configManager.getConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_FeatureCode);
			if(mobileConfig == null){ //第一次
				featureCode = "2222";
				featureCodeManager.init(null, 0);
				this.configManager.addConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_FeatureCode, null, ConfigItem.Default_Account_Id);
			}
			else{
				featureCode = mobileConfig.getConfigValue();
				featureCodeManager.init(featureCode, featureCodeStep);
				
				featureCode = featureCodeManager.next();
				
				updateConfigItem(CONFIG_ITEM_FeatureCode, featureCode);
			}
			
			log.debug("加载初始移动的特征号[" + featureCode + "]");
		}
		
		loadConfig();
		
		mobileMessageDao.updateMobileMessageState();
	}
	
	private void loadConfig(){
		boolean _isValidateMobileMessage = isValidateMobileMessage();
		List<ConfigItem> items = this.configManager.listAllConfigByCategory(CONFIG_CATEGORY_MOBILE);
		
		for (ConfigItem item : items) {
			String itemName = item.getConfigItem();
			String itemvalue = item.getConfigValue();
			String itemExtValue = item.getExtConfigValue();
			
			if(CONFIG_ITEM_MOBILE_CanUseWap.equals(itemName)){//是否允许访问wap
				isCanUseWap = Boolean.parseBoolean(itemvalue);
				continue;
			}
			else if(CONFIG_ITEM_MOBILE_AccountOfCanUseWap.equals(itemName)){ //允许访问wap的单位
				sp(accountOfCanUseWap, itemExtValue);
			}
			
			if(_isValidateMobileMessage){
                if(CONFIG_ITEM_MOBILE_CanUseSMS.equals(itemName)){ //是否启用SMS
					isCanUseSMS = Boolean.parseBoolean(itemvalue);
				}
				else if(CONFIG_ITEM_MOBILE_CanUseWappush.equals(itemName)){ //是否启用wappush
					isCanUseWappush = Boolean.parseBoolean(itemvalue);
				}
				else if(CONFIG_ITEM_MOBILE_AccountOfCanUseSMS.equals(itemName)){ //允许使用SMS的单位
					sp(accountOfCanUseSMS, itemExtValue);
				}
				else if(CONFIG_ITEM_MOBILE_AccountOfCanUseWappush.equals(itemName)){ //允许使用wappush的单位
					sp(accountOfCanUseWappush, itemExtValue);
				}
				else if(itemName.startsWith(CONFIG_ITEM_MOBILE_AppMessageRules)){ //应用消息发送规则
					AppMessageRule rule = new AppMessageRule(itemName, itemvalue);
					//首选短信，但又没有短信插件
					if(rule.getPreferred().equals(AppMessageRule.AppMessagePreferred.SMS) && !booleanMessage){
						continue;
					}
					//首选wappush，但又没有wappush插件
					if(rule.getPreferred().equals(AppMessageRule.AppMessagePreferred.WAPPUSH) && !booleanWappush){
						continue;
					}
					
					appMessageRules.put(rule.getApp(), rule);
				}
				else if(itemName.startsWith(CONFIG_ITEM_MOBILE_CanSendAuth)){ //短信发送权限
					long account = Long.parseLong(itemName.substring(CONFIG_ITEM_MOBILE_CanSendAuth.length()));
					m(canSendMember, account, itemExtValue);
				}
				else if(itemName.startsWith(CONFIG_ITEM_MOBILE_CanRecieveAuth)){ //短信发送权限
					long account = Long.parseLong(itemName.substring(CONFIG_ITEM_MOBILE_CanRecieveAuth.length()));
					m(canRecieveMember, account, itemExtValue);
				}else if(itemName.equals(CONFIG_ITEM_MOBILE_SUFFIX)){//短信后缀
                	SMSSuffix = itemvalue;
                }
			}
		}
		
		log.debug("加载移动应用配置[" + isCanUseWap + ", " + isCanUseSMS + ", " + isCanUseWappush + "]");
	}
	
	public void destroy(){
		if(messageSendThread != null){
			messageSendThread.running = false;
		}
	}
	
	private void m(Map<Long, Set<Long>> map, long accountId, String typeIds){
		map.remove(accountId);
		
		if(Strings.isNotBlank(typeIds)){
			try {
				Set<Long> s = new HashSet<Long>();
				Set<V3xOrgMember> ms = orgManager.getMembersByTypeAndIds(typeIds);
				for (V3xOrgMember member : ms) {
					if(member!=null){						
						s.add(member.getId());
					}
				}
				
				map.put(accountId, s);
			}
			catch (BusinessException e) {
				log.error("", e);
			}
		}
		
		try {
			lastModifyOrgTimestamp = orgManager.getModifiedTimeStamp(OrgModifyState.HeadModifyState, accountId);
		}
		catch (Exception e) {
			log.error("", e);
		}
	}
	
	private static void sp(List<Long> list, String itemvalue){
		if(Strings.isNotBlank(itemvalue)){
			String[] itemvalues = itemvalue.split(",");
			for (String string : itemvalues) {
				list.add(new Long(string.trim()));
			}
		}
	}
	
	//1 需要保存到数据库中
	//2 在Queue 中写一份
	public void sendMobilePersonMessage(String content, Long senderId, Date time, Long... reciverIds) {
		for(Long reciverId : reciverIds){
			this.saveMobileToDBAndSendToQueue(booleanreciverMessage, null, ApplicationCategoryEnum.communication.key(), content, senderId, time, reciverId, MobileMessage.SMSType.sms);
		}
	}

	//1 给私人通讯录发送短信
    //
    public void sendPersonalMessage(String content, Long senderId, Date time, String mobilePhoneStr) {
        if(Strings.isBlank(mobilePhoneStr)){
            return;
        }
        String[] mobilePhones = mobilePhoneStr.split(",");
        for (String phoneNum : mobilePhones) {
            String featureCode = null;
            if(booleanreciverMessage){
                featureCode = this.getNextFeatureCode();
            }
            
            MessageReciver col = null;
            Locale locale = null;
            MobileMessage mm = new MobileMessage();
            
            Long uuidint = newId();
            mm.setId(uuidint);
            mm.setUid(-1L); //接收人ID
            mm.setObjectId(-1L); //对应的对象ID
            mm.setDepartmentId(-1L);//部门ID
            mm.setAccountId(-1L); //单位ID
            mm.setFeatureCode(featureCode);
            mm.setContent(content);
            mm.setSenderId(senderId);
            mm.setMessageType(1);
            mm.setTime(time);
            mm.setSmsType(MobileMessage.SMSType.sms);
            mm.setReciverPhoneNumber(phoneNum);
            mm.setType(ApplicationCategoryEnum.communication.key());
            try {
                mm.setSenderPhoneNumber(orgManager.getMemberById(senderId).getTelNumber());
            } catch (BusinessException e) {
                log.error("在发送移动的个人消息,读取用户的手机号时出错!!!", e);
            }
            mobileMessageDao.saveMobileMessage(mm);
            col = new MessageReciver(-1L, phoneNum);
            MobileMessageObject mb = new MobileMessageObject();
            mb.setMessageId(uuidint);
            mb.setContent(content);
            mb.setId(-1L);
            mb.setSid(senderId);
            mb.setLocale(locale);
            mb.setFeatureCode(featureCode);
            mb.setType(ApplicationCategoryEnum.communication.key());
            mb.setReciever(col);
            messageQueue.add(mb);
        }
    }
    
	public void sendMobileSystemMessage(String content, Long objectId, int category, Date time, Long senderId, Long... reciverIds) {
		if(reciverIds != null && reciverIds.length > 0){
			AppMessageRule rule = getAppMessageRules().get(category);
			boolean isNotNeedFeatureCode = (booleanWappush && rule != null && rule.isSendWappush()) || !booleanreciverMessage;
			
			MobileMessage.SMSType smsType = MobileMessage.SMSType.sms;
			
			if(booleanWappush && rule != null && rule.isSendWappush()){
				smsType = MobileMessage.SMSType.wappush;
			}
			
			for (Long reciverId : reciverIds) {
				try {
					V3xOrgMember m = orgManager.getMemberById(reciverId);
					if (!this.isCanRecieve(reciverId, m.getOrgAccountId())) {
						continue;
					}
				} catch (BusinessException e) {
					log.error("", e);
				}
				this.saveMobileToDBAndSendToQueue(!isNotNeedFeatureCode, objectId, category, content, senderId, time, reciverId, smsType);
			}
		}
	}
	
	public void sendMobileSystemMessage(String content, Long objectId, int category, Date time, Long senderId, Collection<Long> reciverIds) {
		if(reciverIds != null){
			for (Long reciverId : reciverIds) {
				sendMobileSystemMessage(content, objectId, category, time, senderId, reciverId);
			}
		}
	}
	
	public void updateMessageState(Long messageId, boolean isSuccess){
		this.mobileMessageDao.updateById(messageId, isSuccess);
	}
	
	private void saveMobileToDBAndSendToQueue(boolean isNeedFeatureCode, Long objectId, int category, String content, Long senderId, Date time, Long reciverId, MobileMessage.SMSType smsType){
		if(reciverId == null){
			return;
		}
		
		MessageReciver col = null;
		Locale locale = null;
		String phone = "";
		MobileMessage mm = new MobileMessage();
		try {
			V3xOrgMember m = orgManager.getMemberById(reciverId);
			phone = m.getTelNumber();
			if(Strings.isBlank(phone)){
				return;
			}
			
			mm.setDepartmentId(m.getOrgDepartmentId());
			mm.setAccountId(m.getOrgAccountId());
			
			col = new MessageReciver(reciverId, m.getTelNumber());
			
			locale = m.getLocale();
		}
		catch (BusinessException e) {
			log.error("在发送移动的个人消息,读取用户的手机号时出错!!!", e);
			return;
		}
		
		String featureCode = null;
		if(isNeedFeatureCode){
			featureCode = this.getNextFeatureCode();
		}
		Long uuidint = newId();
		mm.setId(uuidint);
		mm.setFeatureCode(featureCode);
		mm.setContent(content);
		mm.setSenderId(senderId);
		mm.setMessageType(1);
		mm.setObjectId(objectId);
		mm.setTime(time);
		mm.setSmsType(smsType);
		mm.setUid(reciverId);
		mm.setReciverPhoneNumber(phone);
		mm.setType(category);
		if (senderId != null && !senderId.equals(-1)) {
			try {
				mm.setSenderPhoneNumber(orgManager.getMemberById(senderId).getTelNumber());
			} catch (Exception e) {
				log.error("在发送移动的个人消息,读取用户的手机号时出错!!!", e);
			}
		} else {
			log.info("发送人id为 -1");
		}
		mobileMessageDao.saveMobileMessage(mm);
		
		MobileMessageObject mb = new MobileMessageObject();
		mb.setMessageId(uuidint);
		mb.setContent(content);
		mb.setId(objectId);
		mb.setSid(senderId);
		mb.setLocale(locale);
		mb.setFeatureCode(featureCode);
		mb.setType(category);
		mb.setReciever(col);
		
		messageQueue.add(mb);
	}
	
	public MobileMessageObject getMessageQueueObject() {
		if(!messageQueue.isEmpty()){
			return messageQueue.remove(0);
		}
		
		return null;
	}
	
	public void saveToMessageQueue(MobileMessageObject obj){
		messageQueue.add(obj);
	}
	
	public boolean isValidateMobileMessage(){
		return (isValidateSMS() || isValidateWappush());
	}
	
	public boolean isValidateSMS(){
		return booleanMessage;
	}
	
	public boolean isValidateWappush(){
		return booleanWappush;
	}
	
	private static Long newId(){
		UUID uuid = UUID.randomUUID();
		Long uuidlong = uuid.getMostSignificantBits();
		return uuidlong;
	}

	public List<Long> getAccountOfCanUseSMS() {
		return accountOfCanUseSMS;
	}

	public List<Long> getAccountOfCanUseWap() {
		return accountOfCanUseWap;
	}

	public List<Long> getAccountOfCanUseWappush() {
		return accountOfCanUseWappush;
	}
	
	public boolean isAccountOfCanUseWap(long accountId){
		return isCanUseWap() && this.getAccountOfCanUseWap().contains(accountId);
	}
	
	public boolean isAccountOfCanUseSMS(long accountId){
		return isCanUseSMS() && this.getAccountOfCanUseSMS().contains(accountId);
	}
	
	public boolean isAccountOfCanUseWappush(long accountId){
		return isCanUseWappush() && this.getAccountOfCanUseWappush().contains(accountId);
	}

	public Map<Integer, AppMessageRule> getAppMessageRules() {
		return this.appMessageRules;
	}

	public String getCanRecieveAuth(long accountId) {
		ConfigItem item = this.configManager.getConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_MOBILE_CanRecieveAuth + accountId);
		if(item != null){
			return item.getExtConfigValue();
		}
		
		return null;
	}

	public String getCanSendAuth(long accountId) {
		ConfigItem item = this.configManager.getConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_MOBILE_CanSendAuth + accountId);
		if(item != null){
			return item.getExtConfigValue();
		}
		
		return null;
	}

	public boolean isCanRecieve(long memberId, long accountId) {
		try {
			if (orgManager.isModified(HeadModifyState, lastModifyOrgTimestamp, accountId)){ //组织模型有变化
				m(canRecieveMember, accountId, getCanRecieveAuth(accountId));
				m(canSendMember, accountId, getCanSendAuth(accountId));
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return (isAccountOfCanUseSMS(accountId) || isAccountOfCanUseWappush(accountId)) 
			&& canRecieveMember.containsKey(accountId)
			&& canRecieveMember.get(accountId).contains(memberId)
		;
	}

	public boolean isCanSend(long memberId, long accountId) {
		try {
			if (orgManager.isModified(HeadModifyState, lastModifyOrgTimestamp, accountId)){ //组织模型有变化
				m(canRecieveMember, accountId, getCanRecieveAuth(accountId));
				m(canSendMember, accountId, getCanSendAuth(accountId));
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return isAccountOfCanUseSMS(accountId) 
			&& canSendMember.containsKey(accountId) 
			&& canSendMember.get(accountId).contains(memberId)
		;
	}

	public boolean isCanUseSMS() {
		return isCanUseSMS;
	}

	public  boolean isCanUseWap() {
		return isCanUseWap;
	}

	public boolean isCanUseWappush() {
		return isCanUseWappush;
	}

	public void setAccountOfCanUseSMS(List<Long> accountId) {
		//删除老的
		//this.configManager.deleteConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_MOBILE_AccountOfCanUseSMS);
		
		this.updateCongifItem(CONFIG_ITEM_MOBILE_AccountOfCanUseSMS, join(accountId));
		
		accountOfCanUseSMS.clear();
		accountOfCanUseSMS.addAll(accountId);
		NotificationManager.getInstance().send(NotificationType.AccountOfCanUseSMSModify,null);
	}

	public void setAccountOfCanUseWap(List<Long> accountId) {
		//删除老的
		//this.configManager.deleteConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_MOBILE_AccountOfCanUseWap);
		
		this.updateCongifItem(CONFIG_ITEM_MOBILE_AccountOfCanUseWap, join(accountId));
		
		accountOfCanUseWap.clear();
		accountOfCanUseWap.addAll(accountId);
		NotificationManager.getInstance().send(NotificationType.AccountOfCanUseWapModify,null);
	}

	public void setAccountOfCanUseWappush(List<Long> accountId) {
		//删除老的
		//this.configManager.deleteConfigItem(CONFIG_CATEGORY_MOBILE, CONFIG_ITEM_MOBILE_AccountOfCanUseWappush);
		
		this.updateCongifItem(CONFIG_ITEM_MOBILE_AccountOfCanUseWappush, join(accountId));
		
		accountOfCanUseWappush.clear();
		accountOfCanUseWappush.addAll(accountId);
		NotificationManager.getInstance().send(NotificationType.AccountOfCanUseWappushModify,null);
	}

	public void setAppMessageRules(List<AppMessageRule> rules) {
		this.appMessageRules.clear();
		for (AppMessageRule rule : rules) {
			this.appMessageRules.put(rule.getApp(), rule);
			
			this.updateConfigItem(rule.getConfigItem(), rule.getConfigValue());
		}
	}
	public void removeMessageRules(List<Integer> apps){
		if(apps != null)
			for(Integer app : apps){
				if(appMessageRules.containsKey(app)){
					appMessageRules.remove(app);
				}
				this.configManager.deleteConfigItem(CONFIG_CATEGORY_MOBILE, MobileMessageManagerImpl.CONFIG_ITEM_MOBILE_AppMessageRules+app);
			}
	}
	public void setCanRecieveAuth(String authStr, long accountId) {
		this.updateCongifItem(CONFIG_ITEM_MOBILE_CanRecieveAuth + accountId, authStr);
		m(canRecieveMember, accountId, authStr);
	}

	public void setCanSendAuth(String authStr, long accountId) {
		this.updateCongifItem(CONFIG_ITEM_MOBILE_CanSendAuth + accountId, authStr);
		m(canSendMember, accountId, authStr);
	}

	public void setCanSendMemberInMemory(String authStr,long accountId){
		m(canSendMember, accountId, authStr);
	}
	public void setCanReceiveMemberInMemory(String authStr,long accountId){
		m(canRecieveMember, accountId, authStr);
	}
	
	public void setCanUseSMS(boolean s) {
		isCanUseSMS = s;
		updateConfigItem(CONFIG_ITEM_MOBILE_CanUseSMS, String.valueOf(s));
	}

	public void setCanUseWap(boolean s) {
		isCanUseWap = s;
		updateConfigItem(CONFIG_ITEM_MOBILE_CanUseWap, String.valueOf(s));
	}

	public void setCanUseWappush(boolean s) {
		isCanUseWappush = s;
		updateConfigItem(CONFIG_ITEM_MOBILE_CanUseWappush, String.valueOf(s));
	}
	
	private void updateCongifItem(String configItem, String configExtValue){
		ConfigItem item = this.configManager.getConfigItem(CONFIG_CATEGORY_MOBILE, configItem);
		if(item == null){ //不存在
			item = new ConfigItem();
			item.setIdIfNew();
			item.setConfigCategory(CONFIG_CATEGORY_MOBILE);
			item.setConfigItem(configItem);
			item.setExtConfigValue(configExtValue);
			Date date=new Date();
			Timestamp stamp=new Timestamp(date.getTime());
			item.setCreateDate(stamp);
			item.setOrgAccountId(1L);
			
			this.configManager.addConfigItem(item);
		}
		else{
			item.setExtConfigValue(configExtValue);
			this.configManager.updateConfigItem(item);
		}
	}
		
	private void updateConfigItem(String configItem, String configValue){
		ConfigItem item = this.configManager.getConfigItem(CONFIG_CATEGORY_MOBILE, configItem);
		if(item == null){ //不存在
			this.configManager.addConfigItem(CONFIG_CATEGORY_MOBILE, configItem, configValue);
		}
		else{
			item.setConfigValue(configValue);
			this.configManager.updateConfigItem(item);
		}
	}

	private static String join(List list){
		if(list == null || list.isEmpty()){
			return "";
		}
		
		String a = "";
		for (int i = 0; i < list.size(); i++) {
			if(i > 0){
				a += ",";
			}
			
			a += list.get(i);
		}
		
		return a;
	}

	public List<Integer> getAppEnumListOfSMS() {
		return appEnumListOfSMS;
	}

	public List<Integer> getAppEnumListOfWappush() {
		return appEnumListOfWappush;
	}
	
	private String getNextFeatureCode(){
		String featureCode = this.featureCodeManager.next();
		if(featureCodeIndex++ > featureCodeStep){
			updateConfigItem(CONFIG_ITEM_FeatureCode, featureCode);
			featureCodeIndex = 0;
		}
		
		return featureCode;
	}

	private void reciveMessage(List<MobileReciver> list) {
		if(list==null || list.isEmpty()){
		    return;
        }
		for(MobileReciver mr : list){
			if(mr!=null && Strings.isNotBlank(mr.getContent()) && mr.getContent().length() >= 4){
				String featureCode = mr.getContent().substring(0, 4);
                String content = mr.getContent().substring(4);
				MobileMessage mm = mobileMessageDao.getMobileMessageByFeatureCode(featureCode);
				if(mm == null){
					continue;
				}
				Long memberId = null;
				try {
                    List<V3xOrgMember> srcMembers = orgManager.getMembersByProperty("telNumber", mr.getSrcPhone(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
                    if(srcMembers!=null && !srcMembers.isEmpty()){
                        memberId = srcMembers.get(0).getId();
                    }
                } catch (BusinessException e) {
					log.error("根据手机号得到用户出错!", e);
					continue;
				}
				
				MobileAppDialect mobileAppDialect = mobileAppDialects.get(mm.getType());
				if((mobileAppDialect!=null)&&(mm.getType()==ApplicationCategoryEnum.meeting.getKey())){
					mobileAppDialect.parseRecieve(content, mm.getObjectId(), mm.getSenderId(), memberId);
				}else{
					baseMobileAppDialect.parseRecieve(content, mm.getObjectId(), memberId, mm.getSenderId());
				}
			}
		}
	}

	private String getContent(String baseContent, String featureCode, int type, Locale locale,V3xOrgMember member) {
		if(member != null && Strings.isBlank(featureCode)){
			if (type == ApplicationCategoryEnum.communication.getKey()){
				baseContent = member.getName() + ":" + baseContent;
			}
			return baseContent.toString()+ getSMSSuffix();
		}
		MobileAppDialect mobileAppDialect = mobileAppDialects.get(type);
		String suffix = "";
		if(adapterMobileMessageManger.isSupportRecive() && !appEnumListOfNotNeedFeature.contains(type)){
			if(mobileAppDialect == null){
				suffix = baseMobileAppDialect.getAppDialect(locale, featureCode);
			}
			else{
				suffix = mobileAppDialect.getAppDialect(locale, featureCode);
			}
			
			if (member != null && type == ApplicationCategoryEnum.communication.getKey()) {
				return getSMSSuffix() + member.getName() + ":" + baseContent + " " + suffix;
			}
			else {
				return getSMSSuffix() + baseContent + " " + suffix;
			}
		}else{
			return getSMSSuffix() + baseContent;
		}
		
	}
	
	/**
	 * 将 短信的内容进行分页
	 * @param baseContent
	 * @return
	 */
	private String[] getSplitString(String baseContent){
		List<String> basCon = new ArrayList<String>();
		//如果短息服务商支持 拆分，并且没有限制最大字数时
		int strLength = 0;
		int contentLength = baseContent!=null?baseContent.length():0;
		int lengthOfContent = baseContent!=null?baseContent.getBytes().length:0;
		int pageNum = lengthOfContent/(largestNum-10);
		if(lengthOfContent%(largestNum-10)!=0){
			pageNum = pageNum + 1;
		}
		if(pageNum==1){
			basCon.add(0, baseContent);
		}else{
			for(int i=1;i<=pageNum;i++){
				String str = "";
				try { 
					if(i==1){
						str = Strings.getLimitLengthString("("+(1)+"/"+pageNum+")"+baseContent, null, largestNum-10, null);
					}else{
						str = Strings.getLimitLengthString("("+(i)+"/"+pageNum+")"+baseContent.substring(strLength, contentLength), null, largestNum-10, null);
					}
				} catch (UnsupportedEncodingException e) {
					log.error("移动拆分短信报错!");
				}
				strLength = strLength + str.length()-5;
				basCon.add(str);
				if(strLength>=contentLength){
					break;
				}
			}
		}
		
		String[] str = {};
		return basCon.toArray(str);
	}
	
	/**
	 * 发送线程
	 *
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2008-7-3
	 */
	class MessageSendThread extends Thread{
		
		boolean running = true;
		
		public MessageSendThread(){
			super.setName("SMSSendThread");
		}
		
		public void start(){
			super.start();
			log.info("启动手机短信收发线程[" + (adapterMobileMessageManger == null && !booleanMessage ? "?" : adapterMobileMessageManger.getName()) + ", " +
					"" + (adapterMobileWapPushManager == null && !booleanWappush ? "?" : adapterMobileWapPushManager.getName()) + "]");
		}
		
		/**
		 * 发送短信机器
		 * @param mo
		 */
		private void sender(MobileMessageObject mo) {
			
			boolean result = sendMessageObj(mo);
			if(!result){//发送不成功
				//发送不成功，添加到队列中去
				try {
					sleep(5 * 1000);//睡 5 秒钟
				} catch (InterruptedException e) {
					log.warn("发送短信，没发出去：",e);
				}
				mobileMessageManager.saveToMessageQueue(mo);
			}else{
				mobileMessageManager.updateMessageState(mo.getMessageId(), result);
			}
		}
		
		/**
		 * 发送短信
		 * @param mo
		 * @return
		 */
		private boolean sendMessageObj(MobileMessageObject mo){
			Long messageId = mo.getMessageId();
			V3xOrgMember sender = null;
			try{
				sender = orgManager.getMemberById(mo.getSid());
				if(sender == null){
					log.info("发送人为null 取接受人");
				}
			}
			catch(Exception e){
				log.error("在发送消息，得到发送者的手机号报错！！！", e);
				return false;
			}
			boolean  sendState = false;
			String dstPhone = mo.getReciever().getPhonenumber();
			//得到 发送消息的内容
			String content = getContent(mo.getContent(), mo.getFeatureCode(), mo.getType(), mo.getLocale(), sender);
			//把HTML字符过滤掉
			content = StrExtractor.getHTMLContent(content);
			String[] str = getSplitString(content);
			if(sender != null && booleanWappush && isCanUseWapPush(sender.getOrgAccountId(), mo.getType())){//走WapPush 通道
				for(int i=0;i<str.length;i++){
					//TODO YOUHB 原有方法参数为int类型，但是uuid转int时候会重复所以参数改成了long类型，此处兼容tidymq.jar所以转化下int
					sendState = adapterMobileWapPushManager.sendMessage(mo.getMessageId().intValue(), sender.getTelNumber(), dstPhone, str[i], url + String.valueOf(messageId));
				}
			}
			else{//如果不支持，则走短信通道
				for(int i=0;i<str.length;i++){
					String telNumber = sender != null ? sender.getTelNumber() : "";
					sendState = adapterMobileMessageManger.sendMessage(mo.getMessageId(), telNumber, dstPhone, str[i]);
				}
			}
			return sendState;
		}
		
		private boolean isCanUseWapPush(long accountId, int type){
			Map<Integer,AppMessageRule> map= mobileMessageManager.getAppMessageRules();
			return mobileMessageManager.isAccountOfCanUseWappush(accountId) && map.containsKey(type) && map.get(type).isSendWappush();
		}
		
		public void run(){
			while(running){
				try {
					MobileMessageObject mo = mobileMessageManager.getMessageQueueObject();
					if(mo != null){
						sender(mo);
					}
					//在任何时候，这个线程都需要接收消息
					if(booleanreciverMessage){
						Long currentTime = System.currentTimeMillis();
						if(currentTime - lastReciveTime >= reciveTime){
							try {
								List<MobileReciver> list = adapterMobileMessageManger.recive();
								if(list != null && list.size() != 0){
									reciveMessage(list);
								}
							}
							catch (Throwable e) {
								log.error("", e);
							}
							lastReciveTime = currentTime;
						}
					}
				}
				catch (Throwable e) {
					log.error("", e);
				}
				
				try {
					sleep(sleepTime);
				}
				catch (Throwable e) {
					log.error("发送消息后，发送消息的线程处于睡眠状态报错！！！",e);
				}
			}
		}
		
		private long lastReciveTime = 0L;
	}

	public void setAppEnumListOfNotNeedFeature(
			List<Integer> appEnumListOfNotNeedFeature) {
		this.appEnumListOfNotNeedFeature = appEnumListOfNotNeedFeature;
	}
	
	private static String SMSSuffix;//短信后缀
	private String getDefaultSMSSuffix(){
		return ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","mobile.default.SMS.suffix");
	}
	public void setSMSSuffix(String suffix){
		SMSSuffix = suffix;
		updateConfigItem(CONFIG_ITEM_MOBILE_SUFFIX,suffix);
		NotificationManager.getInstance().send(NotificationType.SuffixModify,null);
	}
	public String getSMSSuffix(){
		if(SMSSuffix == null){
			return getDefaultSMSSuffix();
		}
		return SMSSuffix;
	}
}