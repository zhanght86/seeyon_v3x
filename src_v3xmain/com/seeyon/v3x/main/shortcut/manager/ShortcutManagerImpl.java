package com.seeyon.v3x.main.shortcut.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.shortcut.ShortcutMenu;
import com.seeyon.v3x.main.shortcut.domain.Shortcut;

public class ShortcutManagerImpl extends BaseHibernateDao<Shortcut> implements ShortcutManager {

	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(ShortcutManagerImpl.class);

    private Shortcut defaultShortcut;
    
    private Map<Long, ShortcutMenu> shortcutMenusMap = new HashMap<Long, ShortcutMenu>();

    private Map<Long, ShortcutMenu> toolsMenusMap = new HashMap<Long, ShortcutMenu>();
    
    /**
     * 所有人的个性化工具栏
     */
    private CacheMap<Long, Shortcut> memberShortcuts ;

    /**
     * 初始化工具栏菜单
     * @param toolsMenus
     */
    public void setToolsMenus(List<ShortcutMenu> toolsMenus) {
    	 for(ShortcutMenu toolMenu : toolsMenus){
             toolsMenusMap.put(toolMenu.getId(), toolMenu);
         }
    }

    /**
     * 初始化快捷菜单
     * @param shortcutMenus
     */
    public void setShortcutMenus(List<ShortcutMenu> shortcutMenus) {
        for(ShortcutMenu shortcut : shortcutMenus){
        	if((shortcut.getId()==302 || shortcut.getId()==303) && !SystemEnvironment.hasPlugin("form")) {
        		continue;
        	}
        	if((Boolean)SysFlag.is_gov_only.getFlag()){
        		long shortCutId = shortcut.getId();
        		if(shortCutId == 201){
        			shortcut.setAction("/edocController.do?method=entryManager&entry=sendManager&toFrom=newEdoc");
        		} else if(shortCutId == 202){
        			shortcut.setName("menu.edoc.recEdoc.new");
        			shortcut.setAction("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdoc&comm=new_form");
        		} else if(shortCutId == 204){
        			shortcut.setAction("/edocController.do?method=listEdocSuperviseController");
        		} else if(shortCutId == 206){
        			shortcut.setAction("/edocController.do?method=entryManager&entry=signReport&toFrom=newEdoc");
        		}
        	}
            this.shortcutMenusMap.put(shortcut.getId(), shortcut);
        }
    }
    
    public void setDefaultShortcut(Shortcut defaultShortcut) {
        this.defaultShortcut = defaultShortcut;
    }

    public void init() {
    	memberShortcuts = cacheFactory.createMap("MemberShortcuts");
    	
        List<Shortcut> allShortcat = getAll();
        for (Shortcut shortcut : allShortcat) {
        	memberShortcuts.put(shortcut.getMemberId(), shortcut);
		}
    }
    
    public Shortcut getShortcut(long id) {
        return super.get(id);
    }

    public Shortcut getShortcutByMemberId(Long memberId) {
        return memberShortcuts.get(memberId);
    }

    public void save(Shortcut shortcut) {
        super.save(shortcut);
        memberShortcuts.put(shortcut.getMemberId(), shortcut);
    }

    public void update(Shortcut shortcut) {
        super.update(shortcut);
        memberShortcuts.put(shortcut.getMemberId(), shortcut);
    }

    public List<ShortcutMenu> getMyAvailableShortcutMenus(Set<Long> accessMenuIds, Long memberId, Long loginAccountId) {
        
        //取得我权限之内的系统二级菜单
        /*
        List<Long> menuIdsList = new ArrayList<Long>();
        List<Menu> menusList = menuManager.getAllMenuItems(memberId, loginAccountId);
        for(Menu menu : menusList){
            menuIdsList.add(menu.getId());
        }
        CurrentUser.get().getAccessSystemMenu();
        */
        if(accessMenuIds == null || accessMenuIds.isEmpty()){
            return null;
        }
        //初始化我的快捷可配菜单
        List<ShortcutMenu> newShortcutMenusList = new ArrayList<ShortcutMenu>();
        Collection<ShortcutMenu> shortcutMenus = shortcutMenusMap.values();
        for(ShortcutMenu shortcut : shortcutMenus){
           if(shortcut.getAvailableChecker() == null || shortcut.getAvailableChecker().check(memberId, loginAccountId)){
               if(accessMenuIds.contains(shortcut.getId())){
                   newShortcutMenusList.add(shortcut);
               }
           }
        }
        
        newShortcutMenusList.addAll(this.toolsMenusMap.values());
        
        return newShortcutMenusList;
    }

    public List<ShortcutMenu> getAllToolsMenus(Set<Long> accessMenuIds) {
        List<ShortcutMenu> tools = new ArrayList<ShortcutMenu>();
        Collection<ShortcutMenu> toolsMenus = toolsMenusMap.values();
        for(ShortcutMenu shortcut : toolsMenus){
            if(shortcut.getRefMenuId()==null || accessMenuIds.contains(shortcut.getRefMenuId())){
                tools.add(shortcut);
            }
        }
        return tools;
    }

    public Map<String, List<ShortcutMenu>> getShortcutMenus(Set<Long> accessMenuIds, long memberId, long loginAccountId) {
        
        List<ShortcutMenu> myAvailableShortcutMenus = getMyAvailableShortcutMenus(accessMenuIds, memberId, loginAccountId);

        return getShortcutMenus(myAvailableShortcutMenus, accessMenuIds, memberId, loginAccountId);
        
    }

    public Map<String, List<ShortcutMenu>> getShortcutMenus(List<ShortcutMenu> shortcutMenusList, Set<Long> accessMenuIds, long memberId, long loginAccountId) {

        Map<String, List<ShortcutMenu>> myShortcutMenusMap = new HashMap<String, List<ShortcutMenu>>();
        Shortcut myShortcut = getShortcutByMemberId(memberId);
        //取我的快捷设置，没有取默认
        if(myShortcut == null){
            Shortcut defaultShortcut = this.defaultShortcut;
            if(defaultShortcut != null){
                myShortcut = defaultShortcut;
            }
            else{
                return  myShortcutMenusMap;
            }
        }
        //过滤掉外部人员的个人考勤
		User user = CurrentUser.get();
        if (myShortcut != null && !user.isInternal()) {
    		List<String> cutList = new ArrayList<String>();
    		String[] shortCut = myShortcut.getShortcutSet().split(",");
    		for(int i = 0 ; i < shortCut.length ; i++) {
    			if ("803".equals(shortCut[i])) {
    				continue;
    			}
    			cutList.add(shortCut[i]);
    		}
    		StringBuffer sb = new StringBuffer();
    		if (cutList.size() > 0) {
    			for(int i = 0 ; i<cutList.size() ; i++) {
    				sb.append(cutList.get(i));
    				sb.append(",");
    			}
    		}
    		sb.deleteCharAt(sb.length()-1);
    		myShortcut.setShortcutSet(sb.toString());
    	}
        
        //过滤我的快捷设置
        String shortcutSet = myShortcut.getShortcutSet();
        if(shortcutSet!=null && shortcutSet.length()>0){
            List<ShortcutMenu> enabledShortcutMenus = new ArrayList<ShortcutMenu>();
            StringTokenizer token = new StringTokenizer(shortcutSet, ",");
            while(token.hasMoreTokens()){
                String s = token.nextToken();
                ShortcutMenu shortcut = shortcutMenusMap.get(Long.parseLong(s));
                if(shortcut == null){
                	shortcut = this.toolsMenusMap.get(Long.parseLong(s));
                }
                if(shortcutMenusList.contains(shortcut)){
                    //最多显示8项快捷设置
                    if(enabledShortcutMenus.size() >= 8){
                        break;
                    }
                    enabledShortcutMenus.add(shortcut);
                }
            }
            myShortcutMenusMap.put(ShortcutMenu.TYPE.shortcut.name(), enabledShortcutMenus);
        }
        
        //取我的工具栏设置
        String toolsSet = myShortcut.getToolsSet();
        if(toolsSet!=null && toolsSet.length()>0){
            List<ShortcutMenu> enabledToolsMenus = new ArrayList<ShortcutMenu>();
            StringTokenizer token = new StringTokenizer(toolsSet, ",");
            while(token.hasMoreTokens()){
                String s = token.nextToken();
                ShortcutMenu shortcut = toolsMenusMap.get(Long.parseLong(s));
                if(shortcut.getRefMenuId()==null || accessMenuIds.contains(shortcut.getRefMenuId())){
                    enabledToolsMenus.add(shortcut);
                }
            }
            myShortcutMenusMap.put(ShortcutMenu.TYPE.tools.name(), enabledToolsMenus);
        }
        
        return myShortcutMenusMap;
    }
    
}
