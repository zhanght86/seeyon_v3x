/**
 * 
 */
package com.seeyon.v3x.mobile.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.pipeline.Message;
import com.seeyon.v3x.common.usermessage.pipeline.MessagePipeline;
import com.seeyon.v3x.mobile.message.domain.AppMessageRule;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-9-2
 */
public class MobileMessagePipeline implements MessagePipeline {

	private MobileMessageManager mobileMessageManager;
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}
	
	public void invoke(Message[] messages) {
		for (Message message : messages) {
			int category = message.getCategory();
			if (category == ApplicationCategoryEnum.meetingroom.key()) {
				category = ApplicationCategoryEnum.meeting.key();
			}
			AppMessageRule appMessageRule = mobileMessageManager.getAppMessageRules().get(category);
			if(appMessageRule == null){
				continue;
			}
			
			boolean isOnline = message.isReceiverOnline();
			
			if((isOnline && appMessageRule.isSendOfOnline()) || !isOnline){ //不在线或者应用允许发送在线用户收短信
				long senderMemberId = message.getSenderMemberId();
				//修改erp发送短信没有发送人，id是-1所以兼容修改去掉判断id-1 youhb 2014年3月28日14:08:10
				try {
					mobileMessageManager.sendMobileSystemMessage(message.getContent(),
								message.getReferenceId(), message.getCategory(),
								message.getCreateDate(), senderMemberId, message.getReceiverMember().getId());
				}
				catch (Throwable e) {
				}
			}
		}
	}

	public boolean isAvailability() {
		return mobileMessageManager.isValidateMobileMessage();
	}

	public String getName() {
		return "sms";
	}

	public List<Integer> getAllowSettingCategory(User currentUser) {
        List<Integer> enabledAppEnum = new ArrayList<Integer>();
        Map<Integer, AppMessageRule> messageRuleMap = mobileMessageManager.getAppMessageRules();
        for(Integer i: messageRuleMap.keySet()){
            if(messageRuleMap.get(i) != null){
                enabledAppEnum.add(i);
            }
        }
        
        return enabledAppEnum;
	}

	public String isAllowSetting(User currentUser) {
		if(!mobileMessageManager.isCanRecieve(currentUser.getId(), currentUser.getAccountId())){
			return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", currentUser.getLocale(), "message.setting.sms.cannotRec");
		}
		
		try {
			if(Strings.isBlank(orgManager.getMemberById(currentUser.getId()).getTelNumber())){
				return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", currentUser.getLocale(), "message.setting.sms.noTelNumber");
			}
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
	public String getShowName() {
		return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "message.setting.type.sms");
	}

	public int getSortId() {
		return 2;
	}

	public boolean isDefaultSend() {
		return false;
	}

}
