package com.seeyon.v3x.plugin.identification.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.IdentificationDog;
import com.seeyon.v3x.common.authenticate.domain.IdentificationDogManager;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.identification.NoSuchIndentificationDogException;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-1-16
 */
public class IdentificationManagerImpl implements IdentificationManager {
	private static final Log log = LogFactory.getLog(IdentificationManagerImpl.class);
	
	private static final String CheckIPReg = "([\\d\\*]{1,3})(\\.)([\\d\\*]{1,3})(\\.)([\\d\\*]{1,3})(\\.)([\\d\\*]{1,3})";
	
	public static final String ConfigCategory = "IdentificationValidateDog";
	
	public static final String ConfigItem_HeadDog = "HeadDog";
	
	public static final String ConfigItem_Dog_prefix = "Dog_";
	
	public static final String ConfigItem_MustCheckDog = "MustCheckDog";
	
    private static final String CurrentDogIsGenericDog = "GENERIC_DOG";
    
    private static final String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
    
	private ConfigManager configManager;
	
	private OrgManager orgManager;

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void init(){
		List<ConfigItem> items = this.configManager.listAllConfigByCategory(ConfigCategory);
		if(items != null){
			for (ConfigItem item : items) {
				String itemName = item.getConfigItem();
				
				if(ConfigItem_MustCheckDog.equals(itemName)){
					setSystemMustUseDogLogin(Boolean.parseBoolean(item.getConfigValue()), item.getExtConfigValue());
				}
				else if(itemName.startsWith(ConfigItem_Dog_prefix)){
					String dogId = itemName.substring(ConfigItem_Dog_prefix.length());
					IdentificationDog dog = new IdentificationDog(dogId, item.getConfigValue(), item.getExtConfigValue());
					try {
						setDog(dogId, dog);
					}
					catch (BusinessException e) {
						log.warn("", e);
					}
				}
			}
			log.info("初始化身份验证狗信息完成。");
		}
		
	}
	
	private void setDog(String id, IdentificationDog dog) throws BusinessException{
		IdentificationDogManager.getInstance().setDog(id, dog);
		if((dog.isMustUseDog() || dog.isCanAccessMobile()) && dog.isEnabled()){
			String loginName;
			try {
				long dogMemberId = dog.getMemberId();
            	if(dogMemberId == 1L){
    				loginName = Constants.SYSTEM_LOGIN_NAME;
    			} else if(dogMemberId == 0L){
    				loginName = Constants.AUDIT_ADMIN_LOGIN_NAME;
    			} else {
    				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, dogMemberId);
    				loginName = member.getLoginName();
    			}
			}
			catch (BusinessException e) {
				throw e;
			}
			if(dog.isMustUseDog())
				IdentificationDogManager.getInstance().addUserIdOfMustUseDogLogin(loginName);
			if(dog.isCanAccessMobile())
				IdentificationDogManager.getInstance().addUserOfCanAccessMobile(loginName);
		}
	}
	
	private void removeDog(String id){
		IdentificationDogManager.getInstance().removeDog(id);
	}
	
	private static String getDogItem(String dogId){
		return ConfigItem_Dog_prefix + dogId;
	}
	
	private void setSystemMustUseDogLogin(boolean p, String noCheckIP){
		Set<String> ips = new HashSet<String>();
		
		if(Strings.isNotBlank(noCheckIP)){
			String[] noCheckIPs = noCheckIP.split(";");
			
			for (String ip : noCheckIPs) {
				ip = ip.trim();
				
				if(Pattern.matches(CheckIPReg, ip)){
					ips.add(ip);
				}
			}
		}
		
		IdentificationDogManager.getInstance().setSystemMustUseDogLogin(p, ips);
	}
	
	public List<IdentificationDog> getAllDog(){
		List<IdentificationDog> dogs = new ArrayList<IdentificationDog>();
		
		dogs.addAll(IdentificationDogManager.getInstance().all());
		
		return dogs;
	}
	
	public IdentificationDog getDog(String dogId){
		return IdentificationDogManager.getInstance().getDog(dogId);
	}

	public boolean checkDogHead(String dogHeadId) {
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, ConfigItem_HeadDog);
		if(item != null){
			return dogHeadId.equals(item.getConfigValue());
		}
		else{
			item = new ConfigItem();
			item.setIdIfNew();
			item.setConfigCategory(ConfigCategory);
			item.setConfigItem(ConfigItem_HeadDog);
			item.setConfigValue(dogHeadId);
			Date date=new Date();
			Timestamp stamp=new Timestamp(date.getTime());
			item.setCreateDate(stamp);
			item.setOrgAccountId(1L);
			
			this.configManager.addConfigItem(item);
			
			return true;
		}
	}

	public void deleteDog(String... dogIds) throws BusinessException {
		if(dogIds != null){
			for (String id : dogIds) {
                
                removeUserOfMustUseDogLogin(id);
                
                ConfigItem item = this.configManager.getConfigItem(ConfigCategory, getDogItem(id));
				if(item != null){
					this.configManager.deleteCriteria(item.getId());
				}
				else{
					log.warn("身份验证狗[" + id + "]不存在");
				}
				
				removeDog(id);
			}
		}
	}
	
    /**
     * 根据加密后的dogId删除狗对象
     * @param dogId
     */
    public void deleteDogByEncodeId(String dogId) throws BusinessException{
        dogId = IdentificationDogManager.getInstance().decodeString(dogId);
        
        removeUserOfMustUseDogLogin(dogId);
        
        ConfigItem item = this.configManager.getConfigItem(ConfigCategory, getDogItem(dogId));
        if(item != null){
            this.configManager.deleteCriteria(item.getId());
        }
        else{
            log.warn("身份验证狗[" + dogId + "]不存在");
        }
        removeDog(dogId);
    }
    
	public void enabledDog(boolean enabled, String... dogIds) throws BusinessException {
		if(dogIds != null){
			for (String dogId : dogIds) {
				IdentificationDog dog = getDog(dogId);
				if(dog != null){
                    
                    //删除需要USB-Key登录的用户MAP
                    if(!enabled && dog.isMustUseDog() && dog.isEnabled()){
                        String loginName = null;
                        try {
                        	long dogMemberId = dog.getMemberId();
                        	if(dogMemberId == 1L){
                				loginName = Constants.SYSTEM_LOGIN_NAME;
                			} else if(dogMemberId == 0L){
                				loginName = Constants.AUDIT_ADMIN_LOGIN_NAME;
                			} else {
                				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, dogMemberId);
                				loginName = member.getLoginName();
                			}
                        }
                        catch (BusinessException e1) {
                            throw e1;
                        }
                        IdentificationDogManager.getInstance().removeUserIdOfMustUseDogLogin(loginName);
                        IdentificationDogManager.getInstance().removeUserOfCanAccessMobile(loginName);
                    }
                    
					dog.setEnabled(enabled);
					this.updateCongifItem(getDogItem(dogId), dog.getName(), dog.ser());
                    setDog(dogId, dog);
				}
				else{
					log.warn("身份验证狗[" + dogId + "]不存在");
				}
			}
		}
	}

	public void makeDog(String dogId, String name, boolean isGenericDog, long memberId, boolean isNeedCheckUsername, boolean isMustUseDog, boolean isEnabled,boolean canAccessMoile) throws BusinessException {
		IdentificationDog dog = new IdentificationDog(dogId, name, isGenericDog, memberId, isNeedCheckUsername, isMustUseDog, isEnabled,canAccessMoile);
		this.updateCongifItem(getDogItem(dogId), name, dog.ser());
		setDog(dogId, dog);
		
		//TODD 是否有必要，如果存在肯能会有bug。
		if(isEnabled && isMustUseDog){
			String loginName;
			try {
            	if(memberId == 1L){
    				loginName = Constants.SYSTEM_LOGIN_NAME;
    			} else if(memberId == 0L){
    				loginName = Constants.AUDIT_ADMIN_LOGIN_NAME;
    			} else {
    				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId);
    				loginName = member.getLoginName();
    			}
			}
			catch (BusinessException e) {
				throw e;
			}
			
			IdentificationDogManager.getInstance().addUserIdOfMustUseDogLogin(loginName);
		}
	}

	public void saveSystemMustUseDogLogin(boolean p, String noCheckIP) {
		setSystemMustUseDogLogin(p, noCheckIP);
		this.updateCongifItem(ConfigItem_MustCheckDog, String.valueOf(p), noCheckIP);	
	}
	
	public boolean getSystemMustUseDogLogin(){
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, ConfigItem_MustCheckDog);
		if(item != null){
			return Boolean.parseBoolean(item.getConfigValue());
		}
		
		return false;
	}
	
	public String getSystemNoCheckIP(){
		ConfigItem item = this.configManager.getConfigItem(ConfigCategory, ConfigItem_MustCheckDog);
		if(item != null){
			return item.getExtConfigValue();
		}
		
		return null;
	}

	public void updateDog(String dogId, String name, boolean isGenericDog, long memberId, boolean isNeedCheckUsername, boolean isMustUseDog, boolean isEnabled,boolean canAccessMobile) 
		throws NoSuchIndentificationDogException, BusinessException {
		IdentificationDog dog = getDog(dogId);
		if(dog != null){
            
            //需要移除MustUseDogLoginMap中的值的情况
            if(!dog.isGenericDog()){
                /*
                 * 1.由专用狗改为通狗
                 * 2.更换了所属人 
                 * 3.取消了必须用狗登录的设置
                 */
                if(isGenericDog || dog.getMemberId()!=memberId || (dog.isMustUseDog() && !isMustUseDog) || (dog.isCanAccessMobile() && !canAccessMobile) ){
                    String loginName = null;
                    try {
                    	long dogMemberId = dog.getMemberId();
                    	if(dogMemberId == 1L){
            				loginName = Constants.SYSTEM_LOGIN_NAME;
            			} else if(dogMemberId == 0L){
            				loginName = Constants.AUDIT_ADMIN_LOGIN_NAME;
            			} else {
            				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, dogMemberId);
            				loginName = member.getLoginName();
            			}
                    }
                    catch (BusinessException e1) {
                        throw e1;
                    }
                    if((dog.isMustUseDog())){
                    	IdentificationDogManager.getInstance().removeUserIdOfMustUseDogLogin(loginName);
                    }else if(dog.isCanAccessMobile()){
                    	IdentificationDogManager.getInstance().removeUserOfCanAccessMobile(loginName);
                    }
                }
            }
            
			dog.update(name, isGenericDog, memberId, isNeedCheckUsername, isMustUseDog, isEnabled,canAccessMobile);
			this.updateCongifItem(getDogItem(dogId), name, dog.ser());
			setDog(dogId, dog);
		}
		else{
			throw new NoSuchIndentificationDogException();
		}
	}
	
    public String checkDogIsUsed(String dogId) throws BusinessException{  
        dogId = IdentificationDogManager.getInstance().decodeString(dogId);
        IdentificationDog dog = getDog(dogId);
        if(dog != null){
            if(dog.isGenericDog()){
                return CurrentDogIsGenericDog;
            }else{
                String memberName;
                try {
                	long memberId = dog.getMemberId();
                	if(memberId == 1L){
                		memberName = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.systemAdminName.value");
        			} else if(memberId == 0L){
        				memberName = ResourceBundleUtil.getString(OrganizationResources, "org.auditAdminName.value");
        			} else {
        				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId);
        				if(member.getIsAdmin()){
        					V3xOrgAccount account = orgManager.getAccountById(member.getOrgAccountId());
        					if(account.getIsRoot()){
        						memberName = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.groupAdminName.value" + (String)SysFlag.EditionSuffix.getFlag());
        					} else {
        						memberName = account.getName() + ResourceBundleUtil.getString(OrganizationResources, "org.account_form.adminName.value");
        					}
        				} else {
        					memberName = member.getName();
        				}
        			}
                }
                catch(BusinessException e){
                	log.error("", e);
                    throw e;
                }
                return memberName;
            }
        }else{
            return null;
        }
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
    
    /**
     * 根据dogId 删除 需要USB-Key登录的用户MAP
     * @param dogId
     * @throws BusinessException
     */
    private void removeUserOfMustUseDogLogin(String dogId) throws BusinessException{
        IdentificationDog dog = getDog(dogId);
        if(dog!=null && dog.isMustUseDog() && dog.isEnabled()){
            String loginName = null;
            try {
            	long memberId = dog.getMemberId();
            	if(memberId == 1L){
    				loginName = Constants.SYSTEM_LOGIN_NAME;
    			} else if(memberId == 0L){
    				loginName = Constants.AUDIT_ADMIN_LOGIN_NAME;
    			} else {
    				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId);
    				loginName = member.getLoginName();
    			}
            }
            catch (BusinessException e1) {
                throw e1;
            }
            IdentificationDogManager.getInstance().removeUserIdOfMustUseDogLogin(loginName);
            IdentificationDogManager.getInstance().removeUserOfCanAccessMobile(loginName);
        }
    }
    
}
