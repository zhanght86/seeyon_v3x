package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystem;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystemManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.mobile.dao.MobileMessageDao;
import com.seeyon.v3x.mobile.message.StatisticAccount;
import com.seeyon.v3x.mobile.message.StatisticDepartment;
import com.seeyon.v3x.mobile.message.domain.AppMessageRule;
import com.seeyon.v3x.mobile.message.domain.MobileMessage;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 移动应用后台管理
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * @version 1.0 2007-11-15
 */
public class MobileManagerController extends BaseController
{
    private OrgManager orgManager;
    
    private MobileMessageDao mobileMessageDao;
    
    private MobileMessageManager mobileMessageManager;

    private ExtendedMessageSystemManager extendedMessageSystemManager;
    
    private AppLogManager appLogManager;
    
    public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setMobileMessageDao(MobileMessageDao mobileMessageDao) {
        this.mobileMessageDao = mobileMessageDao;
    }

    public void setOrgManager(OrgManager orgManager){
        this.orgManager = orgManager;
    }
    
    public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}
    
    public void setExtendedMessageSystemManager(
			ExtendedMessageSystemManager extendedMessageSystemManager) {
		this.extendedMessageSystemManager = extendedMessageSystemManager;
	}

	/**
     * (集团版集团管理员)移动权限管理 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
    public ModelAndView popedomManage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/popedomManage");
        
        List<V3xOrgAccount> accountList = orgManager.getAllAccounts();
        modelAndView.addObject("accountList", accountList);
        modelAndView.addObject("accountCount", accountList.size());

        modelAndView.addObject("isCanUseWap", mobileMessageManager.isCanUseWap());
        modelAndView.addObject("isCanUseSMS", mobileMessageManager.isCanUseSMS());
        modelAndView.addObject("isCanUseWappush", mobileMessageManager.isCanUseWappush());

        modelAndView.addObject("isValidateMobileMessage", mobileMessageManager.isValidateMobileMessage());
        modelAndView.addObject("isValidateSMS", mobileMessageManager.isValidateSMS());
        modelAndView.addObject("isValidateWappush", mobileMessageManager.isValidateWappush());
        modelAndView.addObject("SMSSuffix", mobileMessageManager.getSMSSuffix());
        
        modelAndView.addObject("canUseWapAccountList", mobileMessageManager.getAccountOfCanUseWap());
        modelAndView.addObject("canUseSMSAccountList", mobileMessageManager.getAccountOfCanUseSMS());
        modelAndView.addObject("canUseWappushAccountList", mobileMessageManager.getAccountOfCanUseWappush());
        
        return modelAndView;
    }

    /**
     * (集团版集团管理员)移动权限管理 - FORM处理
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
    public ModelAndView updatePopedomManage(HttpServletRequest request, HttpServletResponse response) throws Exception{

        boolean isCanUseWap = "true".equals(request.getParameter("isCanUseWap"));
        boolean isCanUseSMS = "true".equals(request.getParameter("isCanUseSMS"));
        boolean isCanUseWappush = "true".equals(request.getParameter("isCanUseWappush"));
        
        //是否允许移动设备访问
        if(isCanUseWap){
            String[] canUseWapAcounts = request.getParameterValues("canUseWapAccounts");
            List<Long> accountsList = new ArrayList<Long>();
            if(canUseWapAcounts != null && canUseWapAcounts.length > 0){                
                for(String acountId : canUseWapAcounts){
                    accountsList.add(Long.parseLong(acountId));
                }
            }
            mobileMessageManager.setAccountOfCanUseWap(accountsList);
        }
        //是否允许使用短信
        if(isCanUseSMS){
            String[] canUseSMSAccounts = request.getParameterValues("canUseSMSAccounts");
            List<Long> accountsList = new ArrayList<Long>();
            //StringBuffer sb = new StringBuffer();
            if(canUseSMSAccounts != null && canUseSMSAccounts.length > 0){                
                for(String acountId : canUseSMSAccounts){
                    accountsList.add(Long.parseLong(acountId));
                   /* V3xOrgAccount account = orgManager.getAccountById(Long.parseLong(acountId));
                    if(account != null){
                    	if(sb.length() != 0){
                    		sb.append(",");
                    	}
                    	sb.append(account.getName());
                    }*/
                }
            }
            User user = CurrentUser.get();
            List<Long> canUseAccount = mobileMessageManager.getAccountOfCanUseSMS();
            if(canUseAccount == null || canUseAccount.isEmpty()){
            	appLogManager.insertLog(user, AppLogAction.SMSAuthorityModify_Set);
            }else{
            	appLogManager.insertLog(user, AppLogAction.SMSAuthorityModify_ReSet);
            }
            mobileMessageManager.setAccountOfCanUseSMS(accountsList);
            
            String suffix = request.getParameter("smsSuffix");
            mobileMessageManager.setSMSSuffix(suffix);
        }
        //是否允许使用Wappush
        if(isCanUseWappush){
            String[] canUseWappushAccounts = request.getParameterValues("canUseWappushAccounts");
            List<Long> accountsList = new ArrayList<Long>();
            if(canUseWappushAccounts != null && canUseWappushAccounts.length > 0){                
                for(String acountId : canUseWappushAccounts){
                    accountsList.add(Long.parseLong(acountId));
                }
            }
            mobileMessageManager.setAccountOfCanUseWappush(accountsList);
        }
        
        mobileMessageManager.setCanUseWap(isCanUseWap);
        mobileMessageManager.setCanUseSMS(isCanUseSMS);
        mobileMessageManager.setCanUseWappush(isCanUseWappush);
        NotificationManager.getInstance().send(NotificationType.MobileCanUseModify, new Object[]{isCanUseWap,isCanUseSMS,isCanUseWappush});
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/mobileManager.do?method=popedomManage");
    }
    

    /**
     * (企业版单位管理员)权限管理 - 显示
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView popedomManageENT(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/popedomManage_ENT");
        
        Long accountId = CurrentUser.get().getLoginAccount();
        
        modelAndView.addObject("isCanUseWap", mobileMessageManager.isCanUseWap());
        modelAndView.addObject("isCanUseSMS", mobileMessageManager.isCanUseSMS());
        modelAndView.addObject("isCanUseWappush", mobileMessageManager.isCanUseWappush());
        
        modelAndView.addObject("isValidateMobileMessage", mobileMessageManager.isValidateMobileMessage());
        modelAndView.addObject("isValidateSMS", mobileMessageManager.isValidateSMS());
        modelAndView.addObject("isValidateWappush", mobileMessageManager.isValidateWappush());
        modelAndView.addObject("SMSSuffix", mobileMessageManager.getSMSSuffix());
        
        modelAndView.addObject("canSendAuth", mobileMessageManager.getCanSendAuth(accountId));
        modelAndView.addObject("canRecieveAuth", mobileMessageManager.getCanRecieveAuth(accountId));
        
        return modelAndView;
    }

    
    
    /**
     * (企业版单位管理员)权限管理 - FORM处理
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView updatePopedomManageENT(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long accountId = CurrentUser.get().getLoginAccount();
        
        boolean isCanUseWap = "true".equals(request.getParameter("isCanUseWap"));
        boolean isCanUseSMS = "true".equals(request.getParameter("isCanUseSMS"));
        boolean isCanUseWappush = "true".equals(request.getParameter("isCanUseWappush"));
        
        String sendAuthStr = request.getParameter("sendAuth");
        String recieveAuthStr = request.getParameter("recieveAuth");

        List<Long> accountsList = new ArrayList<Long>();
        accountsList.add(accountId);
        //是否允许移动设备访问
        if(isCanUseWap){
              mobileMessageManager.setAccountOfCanUseWap(accountsList);
        }
        //是否允许使用短信
        if(isCanUseSMS){             
            mobileMessageManager.setAccountOfCanUseSMS(accountsList); 
            
            String suffix = request.getParameter("smsSuffix");
            mobileMessageManager.setSMSSuffix(suffix);
        }
        //是否允许使用Wappush
        if(isCanUseWappush){
            mobileMessageManager.setAccountOfCanUseWappush(accountsList);
        }
        
        mobileMessageManager.setCanUseWap(isCanUseWap);
        mobileMessageManager.setCanUseSMS(isCanUseSMS);
        mobileMessageManager.setCanUseWappush(isCanUseWappush);
        NotificationManager.getInstance().send(NotificationType.MobileCanUseModify, new Object[]{isCanUseWap,isCanUseSMS,isCanUseWappush});
        
        mobileMessageManager.setCanSendAuth(sendAuthStr, accountId);
        mobileMessageManager.setCanRecieveAuth(recieveAuthStr, accountId);
        NotificationManager.getInstance().send(NotificationType.SMSCanSendOrReceiveMemberReload,accountId);
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/mobileManager.do?method=popedomManageENT");
    }
    
    
    
    /**
     * 应用消息通道管理 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
    public ModelAndView msgGateManage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/msgGateManage");
        
        modelAndView.addObject("from", request.getParameter("from"));
        modelAndView.addObject("isValidateMobileMessage", mobileMessageManager.isValidateMobileMessage());
        modelAndView.addObject("isValidateSMS", mobileMessageManager.isValidateSMS());
        modelAndView.addObject("isValidateWappush", mobileMessageManager.isValidateWappush());
        
        Map<Integer, AppMessageRule> appMessageRules = mobileMessageManager.getAppMessageRules();
        
        //取得可配应用
        List<Integer> appEnumList = this.mobileMessageManager.getAppEnumListOfSMS();
        List<Integer> appEnumListOfWapPush = this.mobileMessageManager.getAppEnumListOfWappush();
        //追加插件等其他消息系统应用ID
        List<ExtendedMessageSystem> otherMsgSystemList = extendedMessageSystemManager.getAllExtendedSystem();
        modelAndView.addObject("otherMsgSystemList", otherMsgSystemList);
        
        modelAndView.addObject("appEnumList", appEnumList);
        modelAndView.addObject("appEnumListOfWapPush", appEnumListOfWapPush);
        modelAndView.addObject("appMessageRules", appMessageRules);
        return modelAndView;
    }

    
    /**
     * 应用消息通道管理 - FORM处理
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
    public ModelAndView updateMsgGateManage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        
        List<AppMessageRule> appMessageRuleList = new ArrayList<AppMessageRule>();
        List<Integer> removeRuleList = new ArrayList<Integer>();
        Integer[] edocEnum = {16,19,20,21,22,23,24}; //公文需要添加的项
        Set<Integer> appEnumList = new HashSet<Integer>(); 
        appEnumList.addAll(this.mobileMessageManager.getAppEnumListOfSMS());
        //追加插件应用ID
        List<ExtendedMessageSystem> otherMsgSystemList = extendedMessageSystemManager.getAllExtendedSystem();
        if(otherMsgSystemList != null && !otherMsgSystemList.isEmpty()){
        	for (ExtendedMessageSystem sys : otherMsgSystemList) {
        		appEnumList.add(sys.getApplicationCategory());
			}
        }
        for(Integer key : appEnumList){
            if(key==16 || key==19 || key==20 || key==21 || key==22 || key==23 || key==24){
                continue;
            }
            
            String preferred = request.getParameter("preferred" + key);
            boolean isSendOfOnline = "true".equals(request.getParameter("isSendOfOnline" + key));

            AppMessageRule.AppMessagePreferred preferredEnum = null;
            if("SMS".equals(preferred)){
                preferredEnum = AppMessageRule.AppMessagePreferred.SMS;
            }
            else if("WAPPUSH".equals(preferred)){
                preferredEnum = AppMessageRule.AppMessagePreferred.WAPPUSH;
            }
            if(preferredEnum != null){
                AppMessageRule appMessageRule = new AppMessageRule(key, preferredEnum, isSendOfOnline);
                appMessageRuleList.add(appMessageRule);
                if(key == 4){
                    for(Integer i : edocEnum){
                        appMessageRuleList.add(new AppMessageRule(i, preferredEnum, isSendOfOnline));                        
                    }
                }
			} else {
				removeRuleList.add(key);
				if (key == 4) {
					for (Integer i : edocEnum) {
						removeRuleList.add(i);
					}
				}
			}
		}

        mobileMessageManager.setAppMessageRules(appMessageRuleList);
        mobileMessageManager.removeMessageRules(removeRuleList);
        
        //发送消息通知其他服务器更新规则
        NotificationManager.getInstance().send(NotificationType.AppMessageRulesReload,null);
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/mobileManager.do?method=msgGateManage&from=" + request.getParameter("from"));
    }

    
    /**
     * 集团版 单位管理员 - 消息授权 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView messagePopedom(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/messagePopedom");
        
        Long accountId = CurrentUser.get().getLoginAccount();
        String canUseWapStr = mobileMessageManager.isAccountOfCanUseWap(accountId) ? "allow" : "unallow";
        String canUseSMSStr = mobileMessageManager.isAccountOfCanUseSMS(accountId) ? "allow" : "unallow";
        String canUseWapPushStr = (mobileMessageManager.isValidateWappush() && mobileMessageManager.isAccountOfCanUseWappush(accountId)) ? "allow" : "unallow";
        
        String canRecieveAuth = mobileMessageManager.getCanRecieveAuth(accountId);
       
        modelAndView.addObject("canUseWapStr", canUseWapStr);
        modelAndView.addObject("canUseSMSStr", canUseSMSStr);
        modelAndView.addObject("canUseWapPushStr", canUseWapPushStr);

        modelAndView.addObject("canSendAuth", mobileMessageManager.getCanSendAuth(accountId));
        modelAndView.addObject("canRecieveAuth",  canRecieveAuth!=null?canRecieveAuth.replace("\n", ""):"");

        return modelAndView;
    }

    /**
     * 集团版 单位管理员 - 消息授权 - FORM处理
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView updateMessagePopedom(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get() ;
    	Long accountId = user.getLoginAccount();
        String sendAuthStr = request.getParameter("sendAuth");
        String recieveAuthStr = request.getParameter("recieveAuth");
        mobileMessageManager.setCanSendAuth(sendAuthStr, accountId);
        if(Strings.isNotBlank(sendAuthStr)){
        	V3xOrgAccount account = orgManager.getAccountById(accountId);
        	appLogManager.insertLog(user, AppLogAction.SMSAuthAccount_Set,account.getName());
        }
        mobileMessageManager.setCanRecieveAuth(recieveAuthStr, accountId);
        NotificationManager.getInstance().send(NotificationType.SMSCanSendOrReceiveMemberReload,accountId);
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/mobileManager.do?method=messagePopedom");
    }
    
    
    /**
     * 集团版 集团管理员 - 移动状态查询
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
    public ModelAndView stateQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/stateQuery");
        Date startDate = null;
        Date toDate = null;
        String startDateStr = request.getParameter("startDate");
        String toDateStr = request.getParameter("toDate");
        if(Strings.isNotBlank(startDateStr) && Strings.isNotBlank(toDateStr)){
            startDate = Datetimes.parseDate(startDateStr);
            toDate = Datetimes.parseDate(toDateStr);
        }
        List<V3xOrgAccount> accountList = orgManager.getAllAccounts();
        modelAndView.addObject("accountList", accountList);
        
        //统计
        Map<Long, Integer> SMSCountMap = new HashMap<Long, Integer>();
        Map<Long, Integer> WappushCountMap = new HashMap<Long, Integer>();
        for(V3xOrgAccount account : accountList){
            SMSCountMap.put(account.getId(), 0);
            WappushCountMap.put(account.getId(), 0);
        }
        List<StatisticAccount> statisticAccountList = mobileMessageDao.statisticByAccount(startDate, toDate);
        for (StatisticAccount stat : statisticAccountList) {
            if(stat.getType() == MobileMessage.SMSType.sms){
                SMSCountMap.remove(stat.getAccountId());
                SMSCountMap.put(stat.getAccountId(), stat.getCount());
            }
            else if(stat.getType() == MobileMessage.SMSType.wappush){
                WappushCountMap.remove(stat.getAccountId());
                WappushCountMap.put(stat.getAccountId(), stat.getCount());                
            }
        }
        
        modelAndView.addObject("SMSCountMap", SMSCountMap);
        modelAndView.addObject("WappushCountMap", WappushCountMap);
        
        modelAndView.addObject("startDate", startDateStr);
        modelAndView.addObject("toDate", toDateStr);
        
        modelAndView.addObject("canUseWapPush", mobileMessageManager.isCanUseWappush());
        
        return modelAndView;
    }

    /**
     * 单位管理员 - 消息统计
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView messageCount(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/mobile/messageCount");
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        Date startDate = null;
        Date toDate = null;
        String startDateStr = request.getParameter("startDate");
        String toDateStr = request.getParameter("toDate");
        String departmentIdStr = request.getParameter("departmentId");
        if(Strings.isNotBlank(startDateStr) && Strings.isNotBlank(toDateStr)){
            startDate = Datetimes.parseDate(startDateStr);
            toDate = Datetimes.parseDate(toDateStr);
        }
        
        Long departmentId = accountId;
        List<Long> departmentIds = new ArrayList<Long>();

        if(Strings.isNotBlank(departmentIdStr)){
        	String[] departIds = departmentIdStr.split(",");
        	if(departIds!=null && departIds.length!=0){
        		for(String s : departIds){
        			String[] id = s.split("[|]");
        			 Long dId = Long.parseLong(id[1]);
        			 departmentIds.add(dId);
        			 modelAndView.addObject("selectedDeptId", departmentId);
        		}
        	}
           
          
        }
        List<StatisticDepartment> statisticDepartmentList = mobileMessageDao.statisticByDepartment(accountId, departmentIds, startDate, toDate);
        
        Map<Long, Integer> SMSCountMap = new HashMap<Long, Integer>();
        Map<Long, Integer> WappushCountMap = new HashMap<Long, Integer>();
        
        Map<Long, Integer> baseSMSCountMap = new HashMap<Long, Integer>();
        Map<Long, Integer> baseWappushCountMap = new HashMap<Long, Integer>();
        
        for (StatisticDepartment stat : statisticDepartmentList) {
            if(stat.getType() == MobileMessage.SMSType.sms){
                baseSMSCountMap.put(stat.getDepartmentId(), stat.getCount());
            }
            else if(stat.getType() == MobileMessage.SMSType.wappush){
                baseWappushCountMap.put(stat.getDepartmentId(), stat.getCount());                
            }
        }
        
        List<V3xOrgDepartment> firstLayerDeptList = new ArrayList<V3xOrgDepartment>();
        
        if(departmentIds!=null && !departmentIds.isEmpty()){
        	for(Long l : departmentIds){
        		V3xOrgDepartment d = orgManager.getDepartmentById(l);
        		firstLayerDeptList.add(d);
        	}
        }
        
        if(firstLayerDeptList!=null && !firstLayerDeptList.isEmpty()){
            for(V3xOrgDepartment dept : firstLayerDeptList){           
                int smscount = 0;
                if(baseSMSCountMap.get(dept.getId()) != null){
                    smscount = baseSMSCountMap.get(dept.getId());
                }
                int wappushcount = 0;
                if(baseWappushCountMap.get(dept.getId()) != null){
                    wappushcount = baseWappushCountMap.get(dept.getId());
                }
                SMSCountMap.put(dept.getId(), smscount);
                WappushCountMap.put(dept.getId(), wappushcount);
            }            
        }else{
            //firstLayerDeptList.add(orgManager.getAccountById(departmentId));
            //SMSCountMap.put(departmentId, baseSMSCountMap.get(departmentId));
            //WappushCountMap.put(departmentId, baseWappushCountMap.get(departmentId));            
        }

        modelAndView.addObject("deptList", firstLayerDeptList);
        modelAndView.addObject("SMSCountMap", SMSCountMap);
        modelAndView.addObject("WappushCountMap", WappushCountMap);
        modelAndView.addObject("startDate", startDateStr);
        modelAndView.addObject("toDate", toDateStr);
        modelAndView.addObject("departmentId", departmentIdStr);
        modelAndView.addObject("isShowResult", firstLayerDeptList.isEmpty());

        modelAndView.addObject("isValidateMobileMessage", mobileMessageManager.isValidateMobileMessage());

        String productEdition = (String)(SysFlag.valueOf("frontPage_showMenu").getFlag());
        boolean canUseWapPush = mobileMessageManager.isCanUseWappush();
        if(!"ENT".equals(productEdition) && canUseWapPush){
            canUseWapPush = mobileMessageManager.isAccountOfCanUseWap(accountId);            
        }
        modelAndView.addObject("canUseWapPush", canUseWapPush);
        
        modelAndView.addObject("productEdition", productEdition);
        
        return modelAndView;
    }
    
    @Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        return null;
    }

}
