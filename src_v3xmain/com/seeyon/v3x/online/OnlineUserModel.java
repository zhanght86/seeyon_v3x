package com.seeyon.v3x.online;

import java.io.Serializable;

import com.seeyon.v3x.common.constants.Constants.LoginUserOnlineSubState;
import com.seeyon.v3x.common.constants.Constants.LoginUserState;
import com.seeyon.v3x.common.online.OnlineUser;

/**
 * 显示在线人员的Model
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class OnlineUserModel extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
    private static final long serialVersionUID = 910063119409420128L;
    private Long id;
    private String name;
    private String loginName;
    private String loginType;
    private LoginUserState state;
    private LoginUserOnlineSubState onlineSubState;
    private String departmentName;
    private String postName;
    private boolean isPluralist = false;
    private Long loginAccountId ;
    private boolean isInternal = true;
    
    public Long getLoginAccountId() {
		return loginAccountId;
	}

	public void setLoginAccountId(Long loginAccountId) {
		this.loginAccountId = loginAccountId;
	}

	public OnlineUserModel(OnlineUser user) { //根据user对象初始化Model
        this.id = user.getInternalId();
        this.isInternal = user.isInternal();
        this.loginName = user.getLoginName();
        this.loginType  = user.getLoginType();
        this.name = user.getName();
        this.departmentName = user.getDepartmentSimpleName();
        this.postName = user.getPostName();
        this.state = user.getState();
        this.onlineSubState = user.getOnlineSubState();
        this.loginAccountId = user.getCurrentAccountId();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getLoginName() {
        return loginName;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getLoginType() {
        return loginType;
    }
    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPostName() {
        return postName;
    }
    public void setPostName(String postName) {
        this.postName = postName;
    }

    public boolean isPluralist() {
        return isPluralist;
    }

    public void setPluralist(boolean isPluralist) {
        this.isPluralist = isPluralist;
    }

    public LoginUserState getState() {
        return state;
    }

    public void setState(LoginUserState state) {
        this.state = state;
    }

	public LoginUserOnlineSubState getOnlineSubState() {
		return onlineSubState;
	}

	public void setOnlineSubState(LoginUserOnlineSubState onlineSubState) {
		this.onlineSubState = onlineSubState;
	}

	public boolean isInternal() {
		return isInternal;
	}

	public void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}
    
}
