package com.seeyon.v3x.plugin.menu;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 第三方菜单管理器。
 * 
 * @author wangwy
 * 
 */
public class ThirdpartyMenuManager {
	private final static ThirdpartyMenuManager INSTANCE = new ThirdpartyMenuManager();
	private List<ThirdpartyMenu> menus = new CopyOnWriteArrayList<ThirdpartyMenu>();
	private Map<String, List<ThirdpartyMenu>> moduleMenuMap = new ConcurrentHashMap<String, List<ThirdpartyMenu>>();

	private ThirdpartyMenuManager() {
	};

	public static ThirdpartyMenuManager getInstance() {

		return INSTANCE;
	}

	/**
	 * 注册菜单。
	 * 
	 * @param menu
	 */
	public void addMenu(ThirdpartyMenu menu) {
		menus.add(menu);
		for (String module : menu.getModule()) {
			List<ThirdpartyMenu> list = moduleMenuMap.get(module);
			if (list == null) {
				list = new CopyOnWriteArrayList<ThirdpartyMenu>();
				moduleMenuMap.put(module, list);
			}
			list.add(menu);

		}
	}

	/**
	 * 按模块取菜单。
	 * 
	 * @param module
	 *            对于ThirdpartyAddinMenu，是ModelAndView的viewName，如/doc/rightNew。
	 * @return 模块对应的菜单列表。
	 */
	public List<ThirdpartyMenu> getMenus(String module) {
		List<ThirdpartyMenu> list = moduleMenuMap.get(module);
		if (list == null)
			list = Collections.emptyList();
		return list;
	}

}
