package com.seeyon.v3x.mobile.menu.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.mobile.menu.BaseMobileMenu;
import com.seeyon.v3x.mobile.menu.dao.MobileMenuDao;
import com.seeyon.v3x.mobile.menu.domain.MobileMenuSetting;
import com.seeyon.v3x.util.Strings;

public class MobileMenuManagerImpl  implements MobileMenuManager {
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(MobileMenuManagerImpl.class);
	private MobileMenuDao mobileMenuDao;
	private  List<BaseMobileMenu> allMobileMenu = new ArrayList<BaseMobileMenu>();
//	private static Map<String,BaseMobileMenu> defaultMobileMenu =  new HashMap<String,BaseMobileMenu>();
	private static CacheMap<String, BaseMobileMenu> defaultMobileMenu;

	
	/**
	 * 用户菜单缓存
	 */
//	private static Map<Long,List<String>> userMenuCache = new HashMap<Long,List<String>>();
	private static CacheMap<Long,ArrayList<String>> userMenuCache;

	public  void init(){
		defaultMobileMenu = cacheFactory.createMap("DefaultMobileMenu");
		userMenuCache = cacheFactory.createMap("UserMenuCache");		
		Collections.sort(allMobileMenu);
		for(BaseMobileMenu menu : allMobileMenu){
			if(Strings.isNotBlank(menu.getId())){
				if(menu.getIsDefaultChecked()){
					defaultMobileMenu.put(menu.getId(), menu);;
				}
			}
		}
	}
	
	public BaseMobileMenu getMenuById(String menuId) {
		if(menuId != null){
			for(BaseMobileMenu menu : allMobileMenu){
				if(menu.getId().equals(menuId)){
					return menu;
				}
			}
		}
		return null;
	}

	public List<BaseMobileMenu> listAllMenu() {
		return allMobileMenu;
	}

	public List<BaseMobileMenu> listDefaultMenu() {
		List<BaseMobileMenu> all = new ArrayList<BaseMobileMenu>(defaultMobileMenu.values());
		Collections.sort(all);
		return all;
	}

	
	/**
	 * 载入用户菜单配置，如果第一次载入，则从内存中查询
	 */
	public List<BaseMobileMenu> listMenuByUser(Long userId,Long accountId) {
		ArrayList<String> userMenu = userMenuCache.get(userId);
		List<BaseMobileMenu> result = new ArrayList<BaseMobileMenu>();
		if(userMenu == null){
			userMenu = new ArrayList<String>();
			List<MobileMenuSetting> settings = mobileMenuDao.loadMenuSetting(userId);
			for(MobileMenuSetting setting : settings){
				userMenu.add(setting.getMenuId());
			}
			userMenuCache.put(userId, userMenu);
		}
		if(!userMenu.isEmpty()){
			for(BaseMobileMenu menu : allMobileMenu){
				if(userMenu.contains(menu.getId()) && (menu.getMenuCheck() == null || menu.getMenuCheck().check(userId, accountId))){
					result.add(menu);
				}
			}
		}else{
			//如果没有设置 返回默认的
			for(BaseMobileMenu menu : listDefaultMenu()){
				if(menu.getMenuCheck() == null || menu.getMenuCheck().check(userId, accountId)){
					result.add(menu);
				}
			}
		}
		return result;
	}

	public void saveOrUpdateMenuSetting(List<MobileMenuSetting> userSetting,Long userId) {
		mobileMenuDao.removeSeeting(userId);
		mobileMenuDao.saveAll(userSetting);
		
		//保存到内存中去
		if(userSetting != null && !userSetting.isEmpty()){
			ArrayList<String> userCache = new ArrayList<String>();
			for(MobileMenuSetting set: userSetting){
				userCache.add(set.getMenuId());
			}
			userMenuCache.put(userId, userCache);
		}
	}

	public void deleteMenuSetting(Long userId){
		mobileMenuDao.removeSeeting(userId);
		userMenuCache.remove(userId);
	}
	
	public  void setAllMobileMenu(List<BaseMobileMenu> allMobileMenu) {
		this.allMobileMenu = allMobileMenu;
	}

	public void setMobileMenuDao(MobileMenuDao mobileMenuDao) {
		this.mobileMenuDao = mobileMenuDao;
	}

	public  List<BaseMobileMenu> getAllMobileMenu() {
		return this.allMobileMenu;
	}
}
