package com.seeyon.v3x.main.shortcut.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.main.shortcut.ShortcutMenu;
import com.seeyon.v3x.main.shortcut.domain.Shortcut;

public interface ShortcutManager {
    
    public Shortcut getShortcut(long id);
    
    public void save(Shortcut shortcut);
    
    public void update(Shortcut shortcut);
    
    /**
     * 根据用户Id取得用户的快捷设置
     * @param memberId
     * @return
     */
    public Shortcut getShortcutByMemberId(Long memberId);

    /**
     * 取得所有备选快捷菜单
     * @return
     */
    public List<ShortcutMenu> getMyAvailableShortcutMenus(Set<Long> accessMenuIds, Long memberId, Long loginAccountId);
    
    /**
     * 取得所有备选工具栏菜单
     * @return
     */
    public List<ShortcutMenu> getAllToolsMenus(Set<Long> accessMenuIds);

    /**
     * 取得用户配置的快捷和工具栏菜单<br>
     * (快捷设置在此过滤权限)
     * @param memberId
     * @param loginAccountId
     * @return Map<String, List<ShortcutMenu>>
     *         "shortcut",
     *         "tools"
     */
    public Map<String, List<ShortcutMenu>> getShortcutMenus(Set<Long> accessMenuIds, long memberId, long loginAccountId);

    /**
     * 取得用户配置的快捷和工具栏菜单<br>
     * (快捷设置在此过滤权限)
     * @param shortcutMenusList
     * @param memberId
     * @param loginAccountId
     * @return Map<String, List<ShortcutMenu>>
     *         "shortcut",
     *         "tools"
     */
    public Map<String, List<ShortcutMenu>> getShortcutMenus(List<ShortcutMenu> shortcutMenusList, Set<Long> accessMenuIds, long memberId, long loginAccountId);
    
}
