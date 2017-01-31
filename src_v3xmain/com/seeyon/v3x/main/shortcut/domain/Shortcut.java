package com.seeyon.v3x.main.shortcut.domain;

import java.io.Serializable;

/**
 * 我的快捷设置
 */
public class Shortcut extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
    
    private static final long serialVersionUID = 421406939740571909L;
    
    private long memberId;
    
    private String shortcutSet;

    private String toolsSet;

    public String getToolsSet() {
        return toolsSet;
    }
    
    public void setToolsSet(String toolsSet) {
        this.toolsSet = toolsSet;
    }
    
    public long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
    
    public String getShortcutSet() {
        return shortcutSet;
    }
    
    public void setShortcutSet(String shortcutSet) {
        this.shortcutSet = shortcutSet;
    }
}
