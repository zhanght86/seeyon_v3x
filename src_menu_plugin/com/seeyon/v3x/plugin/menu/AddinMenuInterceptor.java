package com.seeyon.v3x.plugin.menu;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plugin.menu.permission.MenuItemAccessCheck;

/**
 * 第三方自定义加载项菜单Interceptor。
 * 
 * @author wangwy
 * 
 */
public class AddinMenuInterceptor extends HandlerInterceptorAdapter {
	private static final Log log = LogFactory
			.getLog(AddinMenuInterceptor.class);

	public void init() {

	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		super.postHandle(request, response, handler, modelAndView);
		if (modelAndView == null)
			return;
		String viewName = modelAndView.getViewName();
		if (viewName == null)
			return;
		List<ThirdpartyMenu> menus = ThirdpartyMenuManager.getInstance()
				.getMenus(viewName);
		if (menus.size() > 0) {
			List<ThirdpartyAddinMenu> addinMenus = new ArrayList<ThirdpartyAddinMenu>();
			for (ThirdpartyMenu menu : menus) {
				if (menu instanceof ThirdpartyAddinMenu) {

					if (isShowAddinMenu((ThirdpartyAddinMenu) menu, request,
							modelAndView))
						addinMenus.add((ThirdpartyAddinMenu) menu);
				}
			}

			modelAndView.addObject("AddinMenus", addinMenus);
		}

	}

	public boolean isShowAddinMenu(ThirdpartyAddinMenu menu,
			HttpServletRequest request, ModelAndView modelAndView) {

		MenuItemAccessCheck accessCheck = menu.getAccessCheck();
		if (accessCheck != null) {
			return accessCheck.check(CurrentUser.get().getId(), request,
					modelAndView);
		}
		List<String> roles = menu.getRoles();
		if (roles != null) {

		}
		return true;

	}

}