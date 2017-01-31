package com.seeyon.v3x.plugin.ca.caaccount.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.manager.OrganizationManager;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.plugin.ca.caaccount.dao.CAAccountDao;
import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebCAAccountVo;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebImportCAAccountResultVo;
import com.seeyon.v3x.plugin.ldap.manager.OrganizationLdapEvent;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;


public class CAAccountManagerImpl implements CAAccountManager {
    private static Log log = LogFactory.getLog(CAAccountManagerImpl.class);
    public static final String RESOURCE_NAME = "com.seeyon.v3x.plugin.ca.caaccount.resources.i18n.CAAccountResources";
    private CAAccountDao caAccountDao;
    private OrgManager orgManager;
    private OrgManagerDirect orgManagerDirect;
    @SuppressWarnings("deprecation")
    private SearchManager searchManager;
    private ConfigManager configManager;
    private OrganizationManager organizationManager;

	private OrganizationLdapEvent event = (OrganizationLdapEvent)ApplicationContextHolder.getBean("organizationLdapEvent");
    
    public static final String ConfigItem_MustCheckCA = "MustCheckCA";
    
    public static final String ConfigCategory = "IdentificationValidateCA";
    
    
	public static String getConfigitemMustcheckca() {
		return ConfigItem_MustCheckCA;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public OrgManager getOrgManager() {
        return orgManager;
    }
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setCaAccountDao(CAAccountDao caAccountDao) {
        this.caAccountDao = caAccountDao;
    }
    
    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
    @SuppressWarnings("deprecation")
    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
    
    public void setOrganizationManager(OrganizationManager organizationManager) {
		this.organizationManager = organizationManager;
	}

    @Override
    public void addCAAccount(String keyNum, long memberId) {
        CAAccount caAccount = new CAAccount();
        caAccount.setIdIfNew();
        caAccount.setKeyNum(keyNum);
        caAccount.setMemberId(memberId);
        caAccountDao.save(caAccount);
    }
    
    
    public void updateCAKeyByMemberId(long memberId, CAAccount webCAAccount) {
        CAAccount caaccount = caAccountDao.findByMemberId(memberId);
        if(caaccount != null){
        	caaccount.setCaEnable(webCAAccount.isCaEnable());
        	caaccount.setCaState(webCAAccount.isCaState());
        	caaccount.setMobileEnable(webCAAccount.isMobileEnable());
        	caaccount.setCheckEnable(webCAAccount.isCheckEnable());
            caaccount.setKeyNum(webCAAccount.getKeyNum());
            caAccountDao.update(caaccount);
        }
    }

    @Override
    public boolean isMemberIdExist(long memberId) {
        CAAccount caaccount = caAccountDao.findByMemberId(memberId);
        if(caaccount != null){
            return true;
        }
        return false;
    }
    
    public boolean isKeyNumExist(String keyNum){
    	CAAccount caAccount = caAccountDao.findByKeyNum(keyNum);
    	if(caAccount != null){
    		return true;
    	}
    	return false;
    }
    
    

    @Override
    public CAAccount findByLoginName(String loginName) {
        V3xOrgMember v3xOrgMember = null;
        try {
            v3xOrgMember = orgManager.getMemberByLoginName(loginName, true);
            if(v3xOrgMember == null) return null;
        } catch(BusinessException e) {
            return null;
        }
        return caAccountDao.findByMemberId(v3xOrgMember.getId());
    }

    @Override
    public CAAccount findByMemberId(long memberId) {
        return caAccountDao.findByMemberId(memberId);
    }

    @Override
    public CAAccount findByKeyNum(String keyNum) {
        return caAccountDao.findByKeyNum(keyNum);
    }

    @Override
    public String findKeyNum(String loginName) {
        V3xOrgMember v3xOrgMember = null;
        try {
            v3xOrgMember = orgManager.getMemberByLoginName(loginName, true);
            if(v3xOrgMember == null) return null;
            CAAccount caAccount = caAccountDao.findByMemberId(v3xOrgMember.getId());
            if(caAccount != null) {
                return caAccount.getKeyNum();
            }
        } catch(BusinessException e) {
            return null;
        }
        return null;
    }

    @Override
    public String findLoginName(String keyNum) {
        CAAccount caAccount = caAccountDao.findByKeyNum(keyNum);
        if(caAccount == null){
            return null;
        } else {
            V3xOrgMember v3xOrgMember = null;
            try {
                v3xOrgMember = orgManager.getMemberById(caAccount.getMemberId());
                if(v3xOrgMember == null) {
                    return null;
                } else {
                    return v3xOrgMember.getLoginName();
                }
            } catch(BusinessException e) {
                return null;
            }
        }
    }

    @Override
    public boolean deleteBy(final long[] memberIds) {
       return caAccountDao.deleteByMemberIds(memberIds);
    }
    

    @Override
    public List<WebCAAccountVo> searchByCondition(String condition, String value) throws BusinessException {
        List<WebCAAccountVo> webCAAccountVoList = new ArrayList<WebCAAccountVo>();
        if(condition == null || condition.trim().length() == 0){
            List<CAAccount> caAccountList = caAccountDao.findAllByPage();
            if(caAccountList != null && caAccountList.size() > 0){
                for(int i = 0; i < caAccountList.size(); i++){
                    CAAccount caAccount = caAccountList.get(i);
                    V3xOrgMember v3xOrgMember = orgManager.getMemberById(caAccount.getMemberId());
                    if(v3xOrgMember == null) {
                        log.error("通过memberId" + caAccount.getMemberId() + "找不到v3xOrgMember");
                        continue;
                    }
                    long deptId = v3xOrgMember.getOrgDepartmentId();
                    long accountId = v3xOrgMember.getOrgAccountId();
                    WebV3xOrgMember webMember = new WebV3xOrgMember();
                    webMember.setV3xOrgMember(v3xOrgMember);
                    V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                    if(dept != null) {
                        webMember.setDepartmentName(dept.getName());
                    }
                    V3xOrgAccount v3xOrgAccount = orgManagerDirect.getAccountById(accountId);
                    if(v3xOrgAccount != null){
                        webMember.setAccountName(v3xOrgAccount.getName());
                    }
                    if(LDAPConfig.getInstance().getIsEnableLdap() && SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name())) {
                        // 组装LDAP/AD帐号
                        try {
                            webMember.setStateName(event.getLdapAdLoginName(v3xOrgMember.getLoginName()));
                        } catch(Exception e) {
                            log.error("ldap/ad 显示ldap帐号！", e);
                        }
                    }
                    WebCAAccountVo webCAAccountVo = new WebCAAccountVo();
                    webCAAccountVo.setCaAccount(caAccount);
                    webCAAccountVo.setWebV3xOrgMember(webMember);
                    webCAAccountVoList.add(webCAAccountVo);
                }
            }
            return webCAAccountVoList;
        }
        if(condition != null && condition.trim().equals("keyNum")){
            List<CAAccount> caAccountList = caAccountDao.findByKeyNumFuzzily(value);
            if(caAccountList != null && caAccountList.size() > 0){
                for(int i = 0; i < caAccountList.size(); i++){
                    CAAccount caAccount = caAccountList.get(i);
                    V3xOrgMember v3xOrgMember = orgManager.getMemberById(caAccount.getMemberId());
                    if(v3xOrgMember == null) {
                        log.error("通过memberId" + caAccount.getMemberId() + "找不到v3xOrgMember");
                        continue;
                    }
                    long deptId = v3xOrgMember.getOrgDepartmentId();
                    long accountId = v3xOrgMember.getOrgAccountId();
                    WebV3xOrgMember webMember = new WebV3xOrgMember();
                    webMember.setV3xOrgMember(v3xOrgMember);
                    V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                    if(dept != null) {
                        webMember.setDepartmentName(dept.getName());
                    }
                    V3xOrgAccount v3xOrgAccount = orgManagerDirect.getAccountById(accountId);
                    if(v3xOrgAccount != null){
                        webMember.setAccountName(v3xOrgAccount.getName());
                    }
                    if(LDAPConfig.getInstance().getIsEnableLdap() && SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name())) {
                        // 组装LDAP/AD帐号
                        try {
                            webMember.setStateName(event.getLdapAdLoginName(v3xOrgMember.getLoginName()));
                        } catch(Exception e) {
                            log.error("ldap/ad 显示ldap帐号！", e);
                        }
                    }
                    WebCAAccountVo webCAAccountVo = new WebCAAccountVo();
                    webCAAccountVo.setCaAccount(caAccount);
                    webCAAccountVo.setWebV3xOrgMember(webMember);
                    webCAAccountVoList.add(webCAAccountVo);
                }
            }
            return webCAAccountVoList;
        }
        if(condition != null && condition.trim().length() > 0){
            //系统管理员
            V3xOrgMember systemAdmin = orgManager.getSystemAdmin();
            //审计管理员
            V3xOrgMember auditAdmin = orgManager.getAuditAdmin();
            Long []  systemAndAuditMemberId= null;
            if(condition.equals("loginName")){
            	if(systemAdmin.getLoginName().contains(value) || auditAdmin.getLoginName().contains(value)){
            		systemAndAuditMemberId = new Long[2];
            		systemAndAuditMemberId[0] = systemAdmin.getId();
            		systemAndAuditMemberId[1] = auditAdmin.getId();
            	}
            }else if(condition.equals("name")){
            	if(systemAdmin.getName().contains(value) || auditAdmin.getName().contains(value)){
            		systemAndAuditMemberId = new Long[2];
            		systemAndAuditMemberId[0] = systemAdmin.getId();
            		systemAndAuditMemberId[1] = auditAdmin.getId();
            	}
            }
                List<CAAccount> cAAccountList = caAccountDao.findByMemberIds(condition, value, systemAndAuditMemberId);
                for(int j = 0; j < cAAccountList.size(); j++){
                    CAAccount caAccount = cAAccountList.get(j);
                    V3xOrgMember v3xOrgMember = orgManager.getMemberById(caAccount.getMemberId());
                    if(v3xOrgMember == null) {
                        log.error("通过memberId" + caAccount.getMemberId() + "找不到v3xOrgMember");
                        continue;
                    }
                    long deptId = v3xOrgMember.getOrgDepartmentId();
                    long accountId = v3xOrgMember.getOrgAccountId();
                    WebV3xOrgMember webMember = new WebV3xOrgMember();
                    webMember.setV3xOrgMember(v3xOrgMember);
                    V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                    if(dept != null) {
                        webMember.setDepartmentName(dept.getName());
                    }
                    V3xOrgAccount v3xOrgAccount = orgManagerDirect.getAccountById(accountId);
                    if(v3xOrgAccount != null){
                        webMember.setAccountName(v3xOrgAccount.getName());
                    }
                    if(LDAPConfig.getInstance().getIsEnableLdap() && SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name())) {
                        // 组装LDAP/AD帐号
                        try {
                            webMember.setStateName(event.getLdapAdLoginName(v3xOrgMember.getLoginName()));
                        } catch(Exception e) {
                            log.error("ldap/ad 显示ldap帐号！", e);
                        }
                    }
                    WebCAAccountVo webCAAccountVo = new WebCAAccountVo();
                    webCAAccountVo.setCaAccount(caAccount);
                    webCAAccountVo.setWebV3xOrgMember(webMember);
                    webCAAccountVoList.add(webCAAccountVo);
                }
//            }
            return webCAAccountVoList;
        }
        return webCAAccountVoList;
    }

    @Override
    public DataRecord exportCAAccount(List<WebCAAccountVo> webCAAccountVoList) {
        DataRecord dataRecord = new DataRecord();
        //导出excel文件的国际化
        String exportTitleLabel = ResourceBundleUtil.getString(RESOURCE_NAME, "ca.exportTitle.label");
        String loginNameLabel = ResourceBundleUtil.getString(RESOURCE_NAME, "ca.loginName.label");
        String keyNumLabel = ResourceBundleUtil.getString(RESOURCE_NAME, "ca.keyNum.label");
        if (null != webCAAccountVoList && webCAAccountVoList.size() > 0) {
            DataRow[] datarow = new DataRow[webCAAccountVoList.size()];
            for (int i = 0; i < webCAAccountVoList.size(); i++) {
                WebCAAccountVo webCAAccountVo = webCAAccountVoList.get(i);
                DataRow row = new DataRow();
                row.addDataCell(webCAAccountVo.getWebV3xOrgMember().getV3xOrgMember().getLoginName(), 1);
                row.addDataCell(webCAAccountVo.getCaAccount().getKeyNum(), 1);
                datarow[i] = row;
            }
            try {
                dataRecord.addDataRow(datarow);
            } catch (Exception e) {
                log.error("error",e);
            }
        }
        String[] columnName = {loginNameLabel, keyNumLabel};
        dataRecord.setColumnName(columnName);
        dataRecord.setTitle(exportTitleLabel);
        dataRecord.setSheetName(exportTitleLabel);
        return dataRecord;
    }

    @Override
    public List<WebImportCAAccountResultVo> importCAAccount(String repeat, List<List<String>> caAccountListStr) {
        int caAccountListSize = 0;
        if(caAccountListStr != null) caAccountListSize = caAccountListStr.size();
        //导出excel文件的国际化
        String loginNameNull = ResourceBundleUtil.getString(RESOURCE_NAME, "import.caaccount.report.fail.loginNameNull");
        String keyNumNull = ResourceBundleUtil.getString(RESOURCE_NAME, "import.caaccount.report.fail.keyNumNull");
        String cannotfindmember = ResourceBundleUtil.getString(RESOURCE_NAME, "ca.prompt.cannotfindmember");
        String reportResult = ResourceBundleUtil.getString(RESOURCE_NAME, "import.report.add") + ResourceBundleUtil.getString(RESOURCE_NAME, "import.success");
        String reportFail = ResourceBundleUtil.getString(RESOURCE_NAME, "import.report.fail");
        String resultOvercast = ResourceBundleUtil.getString(RESOURCE_NAME, "import.report.overcast");
        String resultOverleap = ResourceBundleUtil.getString(RESOURCE_NAME, "import.report.overleap");
        Map<String, CAAccount> caAccountMapForAdd = new HashMap<String, CAAccount>();
        Map<String, CAAccount> caAccountMapForUpdate = new HashMap<String, CAAccount>();
        List<WebImportCAAccountResultVo> webImportCAAccountResultVoList = new ArrayList<WebImportCAAccountResultVo>();
        for(int i = 0; i < caAccountListSize; i++){
            List<String> record = caAccountListStr.get(i);
            //防护，以免越界
            if(record.size()<2){
            	log.error("Excel中构造错误数据,停止导入"+i);
            	break;
            }
            
            String loginName = record.get(0);
            String keyNum = record.get(1);
            CAAccount caAccount = new CAAccount();
            caAccount.setIdIfNew();
            caAccount.setKeyNum(keyNum);
            WebImportCAAccountResultVo webImportCAAccountResultVo = new WebImportCAAccountResultVo();
            webImportCAAccountResultVo.setLoginName(loginName);
            webImportCAAccountResultVo.setKeyNum(keyNum);
            if(loginName == null || loginName.trim().length() == 0){
                log.info("loginName is empty when importCAAccount");
                webImportCAAccountResultVo.setResult(reportFail + "," +  loginNameNull);
                webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
                continue;
            }
            V3xOrgMember v3xOrgMember = null;
            try {
                v3xOrgMember = organizationManager.getMemberByLoginName(loginName);
                if(v3xOrgMember == null) {
                	//系统管理员
                    V3xOrgMember systemAdmin = orgManager.getSystemAdmin();
                    //审计管理员
                    V3xOrgMember auditAdmin = orgManager.getAuditAdmin();
                	if(systemAdmin.getLoginName().equals(loginName)){
                		v3xOrgMember = systemAdmin;
                	}else if(auditAdmin.getLoginName().equals(loginName)){
                		v3xOrgMember = auditAdmin;
                	}else{
                		log.error("can not find memeber by loginName:" + loginName);
                        webImportCAAccountResultVo.setResult(reportFail + cannotfindmember);
                        webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
                        continue;
                	}
                }
            } catch(BusinessException e) {
                if(v3xOrgMember == null) {
                    log.error("can not find memeber by loginName:" + loginName);
                    webImportCAAccountResultVo.setResult(reportFail + cannotfindmember);
                    webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
                    continue;
                }
            }
            if(keyNum == null || keyNum.trim().length() == 0){
                log.info("keyNum is empty when importCAAccount");
                webImportCAAccountResultVo.setResult(reportFail + "," +  keyNumNull);
                webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
                continue;
            }
            caAccount.setMemberId(v3xOrgMember.getId());
            CAAccount caAccountExist = caAccountDao.findByMemberId(v3xOrgMember.getId());
            if(caAccountExist != null){
                if(repeat.equals("1")){
                    webImportCAAccountResultVo.setResult(resultOverleap);
                } else {
                    webImportCAAccountResultVo.setResult(resultOvercast);
                    caAccountExist.setKeyNum(keyNum);
                    caAccountMapForUpdate.put(loginName, caAccountExist);
                }
                webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
            } else {
                CAAccount caAccountTmp = caAccountMapForAdd.get(loginName);
                if(caAccountTmp == null){
                    caAccountMapForAdd.put(loginName, caAccount);
                    webImportCAAccountResultVo.setResult(reportResult);
                } else {
                    if(repeat.equals("1")){
                        webImportCAAccountResultVo.setResult(resultOverleap);
                    } else {
                        webImportCAAccountResultVo.setResult(resultOvercast);
                        caAccountMapForAdd.put(loginName, caAccount);
                    }
                    webImportCAAccountResultVoList.add(webImportCAAccountResultVo);
                }
            }
        }
        if(!caAccountMapForAdd.isEmpty()){
            caAccountDao.savePatchAll(caAccountMapForAdd.values());
        }
        if(!caAccountMapForUpdate.isEmpty()){
            try {
                caAccountDao.updatePatchAll(caAccountMapForUpdate.values());
            } catch(BusinessException e) {
                log.error("fail to update ca account when importCAAccount");
            }
        }
        return webImportCAAccountResultVoList;
    }
    
    public void saveSystemMustUseCALogin(boolean isMustUseCALogin, String noCheckIp){
    	this.updateCongifItem(ConfigItem_MustCheckCA, String.valueOf(isMustUseCALogin), noCheckIp);
    }
    
    public boolean getSystemMustUseCALogin(){
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, ConfigItem_MustCheckCA);
		if(item != null){
			return Boolean.parseBoolean(item.getConfigValue());
		}
		
		return false;
	}
    
    public String getSystemNoCheckIP(){
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, ConfigItem_MustCheckCA);
		if(item != null){
			return item.getExtConfigValue();
		}
		
		return null;
	}
    
    public String findKeyNumByLoginName(String loginName){
    	CAAccount caAccount = findByLoginName(loginName);
    	if(caAccount != null){
    		return caAccount.getKeyNum();
    	}
    	return "NORecord";
    }
    
    private void updateCongifItem(String configItem, String configValue, String configExtValue){
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, configItem);
		if(item == null){ //不存在
			item = new ConfigItem();
			item.setIdIfNew();
			item.setConfigCategory(ConfigCategory);
			item.setConfigItem(configItem);
			item.setConfigValue(configValue);
			item.setExtConfigValue(configExtValue);
			Date date=new Date();
			Timestamp stamp=new Timestamp(date.getTime());
			item.setCreateDate(stamp);
			item.setOrgAccountId(1L);
			
			this.configManager.addConfigItem(item);
		}
		else{
			item.setConfigValue(configValue);
			item.setExtConfigValue(configExtValue);
			
			this.configManager.updateConfigItem(item);
		}
	}
}
