package com.seeyon.v3x.menu.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.manager.V3xBizConfigManager;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.plugin.PluginMainMenu;
import com.seeyon.v3x.plugin.PluginMenu;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 菜单与权限管理
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * @version 1.0 2007-12-4
 */
@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator })
public class MenuManagerController extends BaseController {

	private MenuManager menuManager;

	private AppLogManager appLogManager;

	private V3xBizConfigManager v3xBizConfigManager;

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setV3xBizConfigManager(V3xBizConfigManager v3xBizConfigManager) {
		this.v3xBizConfigManager = v3xBizConfigManager;
	}

	/**
	 * 菜单权限管理首页
	 */
	public ModelAndView menuManagerIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// return new ModelAndView("sysMgr/menu/index");
		return new ModelAndView("sysMgr/menu/menuManagerIndex");
	}

	/**
	 * 菜单权限
	 */
	public ModelAndView menuAccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("sysMgr/menu/menuManagerIndex");
	}

	/**
	 * 菜单权限列表
	 */
	public ModelAndView menuPopedomList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/menu/menuPopedomList");
		Collection<Security> securityList = this.menuManager.getAllSecurity();
		int securityListSize = 0;
		if (securityList != null && !securityList.isEmpty()) {
			securityListSize = securityList.size();
		}
		modelAndView.addObject("securityList", securityList);
		modelAndView.addObject("securityListSize", securityListSize);
		return modelAndView;
	}

	/**
	 * 菜单排序
	 */
	public ModelAndView menuSort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/menu/menuSort");
		List<Menu> allSystemMenus = (List<Menu>) menuManager.getAllSystemMenus(false);
		Collections.sort(allSystemMenus);
		return modelAndView.addObject("allSystemMenus", allSystemMenus);
	}

	/**
	 * 菜单排序保存
	 */
	public ModelAndView saveMenuOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean toDefault = Strings.isNotBlank(request.getParameter("toDefault"));
		String menuOrder = request.getParameter("menuOrder");
		Map<Long, Integer> defaultMenuSort = menuManager.getDefaultMenuSort();
		List<Menu> menuList = null;
		if (StringUtils.isNotBlank(menuOrder)) {
			menuList = new ArrayList<Menu>();
			String[] idAndSortIds = menuOrder.split(",");
			for (String idAndSortId : idAndSortIds) {
				if (StringUtils.isNotBlank(idAndSortId)) {
					String[] s = idAndSortId.split("[|]");
					Long id = NumberUtils.toLong(s[0]);
					int sortId = NumberUtils.toInt(s[2]);
					Menu menu = this.menuManager.getMenuById(id);
					if (toDefault) {
						Integer defaultSortId = defaultMenuSort.get(id);
						menu.setSortId(defaultSortId != null ? defaultSortId.intValue() : sortId);
					} else {
						menu.setSortId(sortId);
					}
					menuList.add(menu);
				}
			}
		}

		if (menuList != null) {
			this.menuManager.updateAllMenus(menuList);
		}

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "');");
		out.println("</script>");
		out.flush();
		return super.redirectModelAndView("/menuManager.do?method=menuSort");
	}
    
    /**
     * 新建菜单权限
     */
    public ModelAndView newMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/menu/editMenuPopedom");
        return modelAndView;
    }
    
    
    /**
     * 编辑菜单权限
     */
    public ModelAndView editMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/menu/editMenuPopedom");
        Long securityId = null;
        String id = request.getParameter("id");
        if(id != null && id.length()>0){            
            securityId = Long.parseLong(id);
        }
        Security security = this.menuManager.getSecurity(securityId);
        modelAndView.addObject("security", security);
        return modelAndView;
    }
    
    /**
     * 更新菜单权限
     */
    public ModelAndView updateMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String id = request.getParameter("id");
        String name = request.getParameter("securityName");
        String description = request.getParameter("description");
        Security.STATE state = "true".equals(request.getParameter("enable"))? Security.STATE.nomarl : Security.STATE.invalidation;
        List<Long> menuIds = new ArrayList<Long>();
        String[] menuIdsStr = request.getParameterValues("menuIds");
        if(menuIdsStr != null && menuIdsStr.length >0){
            for(String menuId : menuIdsStr){
                menuIds.add(Long.parseLong(menuId));
            }
        }
        User user = CurrentUser.get();
        if(id != null && id.length()>0){
            Long securityId = Long.parseLong(id);
            menuManager.updateSecurity(securityId, name, state, description, menuIds);
            appLogManager.insertLog(user, AppLogAction.MenuSec_Update, user.getName(), name);
        }
        else{
            menuManager.createSecurity(name, state, description, menuIds);            
            appLogManager.insertLog(user, AppLogAction.MenuSec_New, user.getName(), name);
        }
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.refreshWorkspace();
    }
    
    /**
     * 设置为默认菜单权限
     */
    public ModelAndView toDefault(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String[] securityIdsStr = request.getParameterValues("securityIds");
        if(securityIdsStr!=null && securityIdsStr.length>0){            
            List<Long> securityIds = new ArrayList<Long>();
            for(String securityIdStr : securityIdsStr){
                Long securityId = Long.parseLong(securityIdStr);
                securityIds.add(securityId);
            }
            menuManager.setDefaultSecurities(securityIds);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/menuManager.do?method=menuManagerIndex", "parent");
    }
    
    /**
     * 启用菜单权限
     */
    public ModelAndView enableMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String[] securityIdsStr = request.getParameterValues("securityIds");
        if(securityIdsStr!=null && securityIdsStr.length>0){            
            List<String[]> paramsList = new ArrayList<String[]>();
            String[] securityNamesStr = request.getParameterValues("securityNames");
            List<Long> securityIds = new ArrayList<Long>();
            User user = CurrentUser.get();
            for(int i=0; i<securityIdsStr.length; i++){
                Long securityId = Long.parseLong(securityIdsStr[i]);
                securityIds.add(securityId);
                paramsList.add(new String[]{user.getName(), securityNamesStr[i]});
            }
            menuManager.validateSecurity(securityIds, Security.STATE.nomarl);
            appLogManager.insertLogs(user, AppLogAction.MenuSec_Enable, paramsList);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/menuManager.do?method=menuManagerIndex", "parent");
    }
    
    
    /**
     * 停用菜单权限
     */
    public ModelAndView disableMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String[] securityIdsStr = request.getParameterValues("securityIds");
        if(securityIdsStr!=null && securityIdsStr.length>0){ 
            String[] securityNamesStr = request.getParameterValues("securityNames");
            List<Long> securityIds = new ArrayList<Long>();
            User user = CurrentUser.get();
            List<String[]> paramsList = new ArrayList<String[]>();
            for(int i=0; i<securityIdsStr.length; i++){
                Long securityId = Long.parseLong(securityIdsStr[i]);
                securityIds.add(securityId);
                paramsList.add(new String[]{user.getName(), securityNamesStr[i]});
            }
            menuManager.validateSecurity(securityIds, Security.STATE.invalidation);
            appLogManager.insertLogs(user, AppLogAction.MenuSec_Disable, paramsList);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/menuManager.do?method=menuManagerIndex", "parent");
    }
    
    /**
     * 删除菜单权限
     */
    public ModelAndView deleteMenuPopedom(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String[] securityIdsStr = request.getParameterValues("securityIds");
        if(securityIdsStr!=null && securityIdsStr.length>0){  
            String[] securityNamesStr = request.getParameterValues("securityNames");
            List<String[]> paramsList = new ArrayList<String[]>();
            List<Long> securityIds = new ArrayList<Long>();
            User user = CurrentUser.get();
            for(int i=0; i<securityIdsStr.length; i++){
                Long securityId = Long.parseLong(securityIdsStr[i]);
                securityIds.add(securityId);
                paramsList.add(new String[]{user.getName(), securityNamesStr[i]});
            }
            menuManager.deleteSecurity(securityIds);
            appLogManager.insertLogs(user, AppLogAction.MenuSec_Delete, paramsList);
        }
        return super.redirectModelAndView("/menuManager.do?method=menuManagerIndex", "parent");
    }
    
    /**
     * 显示菜单树
     */
    public ModelAndView showMenuTree(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/menu/menuTree");
        String idStr = request.getParameter("id");
        if(idStr!=null && idStr.length()>0){
            Long securityId = Long.parseLong(idStr);
            Security security = this.menuManager.getSecurity(securityId);
            modelAndView.addObject("menuIds", security.getMenuIds());
        }
        //getAllSystemMenus传递参数区分是否取表单菜单挂接和插件菜单，不在这里过滤
        Collection<Menu> allSystemMenus = this.menuManager.getAllSystemMenus(true);
		// 将客开插件新建的一级菜单加入
		Set<Menu> allMenusSet = new HashSet<Menu>(); // 所有的菜单（包括系统菜单和未设置role或menuCheck的插件菜单）
		allMenusSet.addAll(allSystemMenus);
		List<PluginMainMenu> pluginMainMenus = this.menuManager
				.getAllPluginMenus();
		if(pluginMainMenus != null){
			for (PluginMainMenu pluginMenu : pluginMainMenus) {
				if (pluginMenu.getRefA8MenuId() == null
						&& pluginMenu.getChildren() != null
						&& pluginMenu.getChildren().length > 0) {
					Menu mainMenu = new Menu();
					mainMenu.setId(pluginMenu.getId());
					mainMenu.setSortId(mainMenu.getId().intValue());
					mainMenu.setName(pluginMenu.getName());
					mainMenu.setType(Menu.TYPE.customPlugin.ordinal());
					mainMenu.setParentId(null);
					mainMenu.setPluginId(pluginMenu.getPluginName());
					mainMenu.setAction(null);
					mainMenu.setTarget(null);
					mainMenu.setIcon(null);
					for (PluginMenu pm : pluginMenu.getChildren()) {
						if (pm.getRoles() != null && pm.getRoles().length > 0
								|| pm.getMenuCheck() != null) {
							// 按role或menuCheck管理的不在菜单权限管理中管理
							continue;
						}
						Menu menu = new Menu();
						menu.setId(pm.getId());
						menu.setSortId(menu.getId().intValue());
						menu.setName(pm.getName());
						menu.setType(Menu.TYPE.customPlugin.ordinal());
						menu.setParentId(mainMenu.getId());
						menu.setPluginId(pluginMenu.getPluginName());
						menu.setAction(pm.getUrl());
						menu.setTarget("main");
						menu.setIcon(pm.getIcon());
						mainMenu.addChild(menu);
					}
					if (mainMenu.getChildren() != null
							&& !mainMenu.getChildren().isEmpty()) {
						allMenusSet.add(mainMenu);
					}
				}
			}
		}
		List<Menu> allMenus = new ArrayList<Menu>(allMenusSet);
		Collections.sort(allMenus);

        long[] forceMenus = this.menuManager.getForceMenus();
        Set<Long> excludeMenuIds = this.menuManager.getExcludeMenuIds4Tree();
        if("4".equals(idStr)){//如果是外部权限，剔除一些设计外的权限
        	excludeMenuIds = this.menuManager.getOuterWorkerExtcludeMenus();
        	long[] outerforceMenus = this.menuManager.getOuterIncludeMenus();
        	forceMenus = ArrayUtils.addAll(forceMenus, outerforceMenus);
        }else{
        	excludeMenuIds = this.menuManager.getExcludeMenuIds4Tree();
        	forceMenus = this.menuManager.getForceMenus();
        }
        modelAndView.addObject("excludeMenuIds", excludeMenuIds);
        modelAndView.addObject("allSystemMenus", allMenus);
        modelAndView.addObject("forceMenus", forceMenus);
        modelAndView.addObject("relationshipMenu", this.menuManager.getRelationship());
        
        return modelAndView;
    }
    
	/**
	 * 空间配置系统菜单
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator, RoleType.SpaceManager })
	public ModelAndView showSpaceMenuTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/menu/spaceMenuTree");
		String type = request.getParameter("type");

		List<Menu> allMenus = new ArrayList<Menu>();
		if ("system".equals(type)) {// 系统菜单
			allMenus = (List<Menu>) this.menuManager.getAllSystemMenus(true);
		} else if ("custom".equals(type)) {// 表单业务菜单
			allMenus = this.v3xBizConfigManager.getAllFormBindMenus();
		}
		Collections.sort(allMenus);

		String id = request.getParameter("id");
		if (Strings.isNotBlank(id)) {
			Long spaceId = NumberUtils.toLong(id);
			List<Long> menuIds = this.menuManager.getSpaceMenuIds(spaceId);
			modelAndView.addObject("menuIds", menuIds);
		}

		return modelAndView.addObject("allMenus", allMenus);
	}

    /**
     * 配置人员菜单权限的弹出窗口方法
     * 
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin, RoleType.Administrator, RoleType.HrAdmin, RoleType.DepartmentAdmin})
    public ModelAndView showAllMenuSecurity(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("sysMgr/menu/allMenuSecurities");
        String memberIdStr = request.getParameter("memberId");
        if(memberIdStr!=null && memberIdStr.length()>0){
            List<Security> selectedSecurities = this.menuManager.getSecurityOfMember(Long.parseLong(memberIdStr), CurrentUser.get().getLoginAccount(), false);
            modelAndView.addObject("selectedSecurities", selectedSecurities);
        }
        List<Security> allSecurities = this.menuManager.getAllAvailableSecurity();
        modelAndView.addObject("allSecurities", allSecurities);
        
        return modelAndView;
    }
    
    @Override
    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        return null;
    }
    
	/**
	 * 获取使用菜单权限的人员
	 */
	public String getRoleMembers(Long securityId) {
		List<Long> memberIds = this.menuManager.getRoleMembers(securityId);
		String ids = "";
		for (Long memberId : memberIds) {
			ids += memberId + ",";
		}
		return Functions.parseElements(ids, "Member");
	}

	/**
	 * 菜单权限关系保存
	 */
	public String saveRoleMembers(String typeAndIds, Long securityId) {
		return this.menuManager.saveRoleMembers(typeAndIds, securityId);
	}

}