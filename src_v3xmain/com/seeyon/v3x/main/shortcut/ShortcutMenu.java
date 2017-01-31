package com.seeyon.v3x.main.shortcut;

import java.io.Serializable;

import com.seeyon.v3x.menu.manager.MenuCheck;

/***
 * 快捷设置菜单
 * 
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class ShortcutMenu implements Serializable{
    public static enum TYPE{
        shortcut, //快捷方式
        tools //工具栏
    }
    
    private long id;
    
    private Long refMenuId;
    //key name
    private String name;

    //icon url
    private String icon;
    //link URL
    private String action;
    //需要检测是否可用的
    private MenuCheck availableChecker;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public MenuCheck getAvailableChecker() {
        return availableChecker;
    }

    public void setAvailableChecker(MenuCheck availableChecker) {
        this.availableChecker = availableChecker;
    }

    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Long getRefMenuId() {
        return refMenuId;
    }

    public void setRefMenuId(Long refMenuId) {
        this.refMenuId = refMenuId;
    }
    
}
