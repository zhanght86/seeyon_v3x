package com.seeyon.v3x.main;

import java.io.Serializable;

/**
 * 单位标识类
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class AccountSymbol implements Serializable{
	private static final long serialVersionUID = -643623578900581205L;
	private Long accountId;
    //LOGO路径
    private String logoImagePath;
    //是否隐藏LOGO
    private boolean isHiddenLogo;
    //BANNER路径
    private String bannerImagePath;
    //Banner是否平铺
    private boolean isTileBanner;
    //是否隐藏单位名称
    private boolean isHiddenAccountName;
    //是否隐藏集团名称
    private Boolean isHiddenGroupName = null;

    public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getBannerImagePath() {
        return bannerImagePath;
    }
    
    public void setBannerImagePath(String bannerImagePath) {
        this.bannerImagePath = bannerImagePath;
    }
    
    public boolean isHiddenAccountName() {
        return isHiddenAccountName;
    }
    
    public void setHiddenAccountName(boolean isHiddenAccountName) {
        this.isHiddenAccountName = isHiddenAccountName;
    }
    
    public boolean isHiddenLogo() {
        return isHiddenLogo;
    }
    
    public void setHiddenLogo(boolean isHiddenLogo) {
        this.isHiddenLogo = isHiddenLogo;
    }
    
    public boolean isTileBanner() {
        return isTileBanner;
    }
    
    public void setTileBanner(boolean isTileBanner) {
        this.isTileBanner = isTileBanner;
    }
    
    public String getLogoImagePath() {
        return logoImagePath;
    }
    
    public void setLogoImagePath(String logoImagePath) {
        this.logoImagePath = logoImagePath;
    }

	public Boolean isHiddenGroupName() {
		return isHiddenGroupName;
	}

	public void setHiddenGroupName(Boolean isHiddenGroupName) {
		this.isHiddenGroupName = isHiddenGroupName;
	}
    
}  