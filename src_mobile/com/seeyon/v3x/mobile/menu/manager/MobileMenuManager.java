package com.seeyon.v3x.mobile.menu.manager;

import java.util.List;

import com.seeyon.v3x.mobile.menu.BaseMobileMenu;
import com.seeyon.v3x.mobile.menu.domain.MobileMenuSetting;

/**
 * 移动应用菜单设置
 * @author dongyj
 *
 */
public interface MobileMenuManager {

	/**
	 * 得到所有的菜单
	 * @return
	 */
	public List<BaseMobileMenu> listAllMenu();

	/**
	 * 得到所有默认的菜单
	 * @return
	 */
	public List<BaseMobileMenu> listDefaultMenu();
	
	/**
	 * 得到用户配置的菜单
	 * @param userId
	 * @return
	 */
	public List<BaseMobileMenu> listMenuByUser(Long userId,Long accountId);
	
	/**
	 * 保存或修改用户的菜单配置
	 * @param userSetting
	 */
	public void saveOrUpdateMenuSetting(List<MobileMenuSetting> userSetting,Long userId);
	
	/**
	 * 根据菜单id 得到菜单
	 * @param menuId
	 * @return
	 */
	public BaseMobileMenu getMenuById(String menuId);
	
	/**
	 * 删除用户配置
	 * @param userId
	 */
	public void deleteMenuSetting(Long userId);
	
}
