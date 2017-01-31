package com.seeyon.v3x.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Base64;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 首页数据加载器<br>
 * 装载登陆背景、单位标识等频繁访问的数据长驻内存
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class MainDataLoader{
    private static Log log = LogFactory.getLog(MainDataLoader.class);
    private static final CacheAccessable cacheFactory = CacheFactory.getInstance(MainDataLoader.class);

    
	// 登陆图片背景
	private String loginImagePath = null;
    
    private OrgManager orgManager;
    
    private ConfigManager configManager;

//  private Map<Long, AccountSymbol> accountSymbolMap = new HashMap<Long, AccountSymbol>();
    private CacheMap<Long, AccountSymbol> accountSymbolMap = cacheFactory.createMap("AccountSymbolMap");

    
    private static MainDataLoader instance =  new MainDataLoader();

    public static MainDataLoader getInstance(){
        return instance;
    }
    
    private Long groupAccountId = null;
   // private boolean isGroupEdtion = false;
    
    //
    private MainDataLoader(){
        
        orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
        configManager = (ConfigManager) ApplicationContextHolder.getBean("configManager");
        
        //登陆背景
        ConfigItem configItem_login = configManager.getConfigItem(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE);
        if(configItem_login != null){
            this.loginImagePath = configItem_login.getConfigValue();
            refreshLocalImage(configItem_login);
        }
        
        //单位标识
        List<V3xOrgAccount> accountList = new ArrayList<V3xOrgAccount>();
        try {
            accountList = orgManager.getAllAccounts();
        }
        catch (BusinessException e) {
            log.error("单位标识中取得单位列表异常:", e);
        }
        for(V3xOrgAccount account : accountList){
            Long accountId = account.getId();            
            AccountSymbol accountSymbol = null;
            
            List<ConfigItem> configItemList = configManager.listAllConfigByCategory(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, accountId);
            if(configItemList != null && !configItemList.isEmpty()){
                accountSymbol = new AccountSymbol();
            }
            
            for(ConfigItem configItem : configItemList){
                
                String configItemStr = configItem.getConfigItem();
                String configValue = configItem.getConfigValue();
                if(Constants.CONFIG_ITEM_LOGO.equals(configItemStr)){
                    accountSymbol.setLogoImagePath(configValue);
                    refreshLocalImage(configItem);
                }
                else if(Constants.CONFIG_ITEM_LOGO_IsHidden.equals(configItemStr)){
                    accountSymbol.setHiddenLogo(Boolean.parseBoolean(configValue));
                }
                else if(Constants.CONFIG_ITEM_BANNER.equals(configItemStr)){
                    accountSymbol.setBannerImagePath(configValue);
                    refreshLocalImage(configItem);
                }
                else if(Constants.CONFIG_ITEM_BANNER_IsTile.equals(configItemStr)){
                    accountSymbol.setTileBanner(Boolean.parseBoolean(configValue));
                }
                else if(Constants.CONFIG_ITEM_ACCOUNTNAME_IsHidden.equals(configItemStr)){
                    accountSymbol.setHiddenAccountName(Boolean.parseBoolean(configValue));
                }
                else if(Constants.CONFIG_ITEM_GROUPNAME_IsHidden.equals(configItemStr)){
                    accountSymbol.setHiddenGroupName(Boolean.parseBoolean(configValue));
                }
            }
            accountSymbolMap.put(accountId, accountSymbol);

            if(account.getIsRoot()){
                groupAccountId = accountId;
            }
        }
        
    }

    
	public String getLoginImagePath() {
		String defaultLoginImagePath = "/common/skin/default" + com.seeyon.v3x.skin.Constants.getSkinSuffix() + "/images/login.gif";

		if (loginImagePath == null) {
			return defaultLoginImagePath;
		}

		if (loginImagePath.contains("/common/skin/default")) {
			return defaultLoginImagePath;
		}

		return loginImagePath;
	}

    public void setLoginImagePath(String loginImagePath) {
        this.loginImagePath = loginImagePath;
    }
    
    /**
     * 取得单位标识
     * @param accountId
     * @return
     */
    public AccountSymbol getAccountSymbol(long accountId){
        
        AccountSymbol accountSymbol = accountSymbolMap.get(accountId);
        
        if(accountSymbol==null || accountSymbol.getLogoImagePath()==null || accountSymbol.getBannerImagePath()==null || accountSymbol.isHiddenGroupName() == null){
            //如果当前单位是集团下的单位且没有设置标识，则加载集团的配置
            if((Boolean)SysFlag.frontPage_isNeedGetSymbolFromGroup.getFlag()){
                boolean isNeedGetSymbolFromGroup = false;
                try {
                    isNeedGetSymbolFromGroup = accountId==1L || (accountId!=groupAccountId && orgManager.isAccountInGroupTree(accountId));
                }
                catch (BusinessException e) {
                    log.error("单位标识中判断单位是否在集团树下出现异常:", e);
                }
                if(isNeedGetSymbolFromGroup){
                    AccountSymbol groupAccountSymbol = accountSymbolMap.get(groupAccountId);
                    if(accountSymbol == null){
                        accountSymbol = groupAccountSymbol;
                    }
                    else{
                        if(accountSymbol.getLogoImagePath()==null && groupAccountSymbol!=null){
                            accountSymbol.setLogoImagePath(groupAccountSymbol.getLogoImagePath());                                                    
                        }
                        if(accountSymbol.getBannerImagePath()==null && groupAccountSymbol!=null){
                            accountSymbol.setBannerImagePath(groupAccountSymbol.getBannerImagePath());
                        }
                        if(accountSymbol.isHiddenGroupName() == null && groupAccountSymbol!=null && groupAccountSymbol.isHiddenGroupName() != null){
                            accountSymbol.setHiddenGroupName(groupAccountSymbol.isHiddenGroupName());
                        }
                    }
                }
            }
            else{
                //企业版系统管理员登陆
                if(accountId==1L){
                    try {
                        List<V3xOrgAccount> accountsList = orgManager.getAllAccounts();
                        for(V3xOrgAccount account : accountsList){
                            if(!account.getIsRoot()){
                                accountSymbol = accountSymbolMap.get(account.getId());
                                break;
                            }
                        }
                    }
                    catch (BusinessException e) {
                        log.error("企业版系统管理员取得单位标识异常:", e);
                    }
                }
            }
            if(accountSymbol == null){
                accountSymbol = new AccountSymbol();
            }
            if(accountSymbol.getLogoImagePath()==null){
                accountSymbol.setLogoImagePath(Constants.getDefaultLogoName());
            }
            if(accountSymbol.getBannerImagePath()==null){
                accountSymbol.setBannerImagePath(Constants.DEFAULT_BANNER_NAME);
            }
            if(accountSymbol.isHiddenGroupName() == null){
                accountSymbol.setHiddenGroupName(false);
            }
        }
        
        return accountSymbol;
    }
    
    /**
     * 判断MAP里是否有值
     * @param accountId
     * @return
     */
    public AccountSymbol getAccountSymbolFromMap(long accountId){
        return accountSymbolMap.get(accountId);
    }
    
    public void updateAccountSymbol(long accountId, AccountSymbol accountSymbol){
        if(accountSymbol == null){
            return;
        }       
        accountSymbolMap.put(accountId, accountSymbol);
        //accountSymbolMap.notifyUpdate(accountId);
    }
    
    public void notifyUpdateAccountSymbol(long accountId){
        accountSymbolMap.notifyUpdate(accountId);
    }
    
    
    /**
     * 删除单位标识/恢复默认
     * @param accountId
     */
    public void deleteAccountSymbol(long accountId){
        accountSymbolMap.remove(accountId);
    }
    
    /**
     * 取得单位LOGO路径
     * @param accountId
     * @return
     */
    public String getLogoImagePath(long accountId){
        AccountSymbol accountSymbol = getAccountSymbol(accountId);
        if(accountSymbol!=null){
            return accountSymbol.getLogoImagePath();
        }
        else{
            return Constants.getDefaultLogoName();
        }
    }
    /**
     * 根据数据库中存储的图片刷新本地USER－DATA中的文件(登录背景、单位Logo、单位Banner、集团Logo、集团Banner)缓存。
     * 1、变更时将图片Base64，保存在数据库中。<br/>
     * 2、变更时通知远程更新。<br/>
     * 3、启动时按数据库刷新本地文件。
     * @param item
     */
	public void refreshLocalImage(ConfigItem item) {
		if(item==null) return;
		String path = SystemEnvironment.getA8ApplicationFolder()
				+ item.getConfigValue();
		String base64 = item.getExtConfigValue();
		// 或为历史数据，数据库中没有保存，忽略
		if (Strings.isEmpty(base64))
			return;
		final byte[] data = new Base64().decode(base64.getBytes());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			out.write(data);
		} catch (IOException e) {
			log.error("更新本地标识图片出错！" + item.getConfigValue(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {

				}
			}
		}
	}    
    
}

