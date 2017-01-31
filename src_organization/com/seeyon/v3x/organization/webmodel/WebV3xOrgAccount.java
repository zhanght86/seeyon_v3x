/**
 * WebV3xOrgAccount.java
 * Created on 2007-4-6
 */
package com.seeyon.v3x.organization.webmodel;

import com.seeyon.v3x.organization.domain.V3xOrgAccount;

/**
 *
 * @author <a href="mailto:zhanghuizheng@use.com.cn">张慧征</a>
 *
 */
public class WebV3xOrgAccount
{
    
    
    private String superiorName;
    private V3xOrgAccount v3xOrgAccount;
    private String chiefLeader;
    private String address;
    private String zipCode;
    private String telephone;
    private String fax;
    private String ipAddress;
    private String accountMail;
    private String adminPass;
    private String accountCategory;
    private String accountLevel;
    private String accountNature;
    private String adminiLevel;  //行政级别
    
    /**
     * @return Returns the v3xOrgAccount.
     */
    public V3xOrgAccount getV3xOrgAccount()
    {
        return v3xOrgAccount;
    }

    /**
     * @param orgAccount The v3xOrgAccount to set.
     */
    public void setV3xOrgAccount(V3xOrgAccount orgAccount)
    {
        v3xOrgAccount = orgAccount;
    }

    /**
     * @return Returns the superiorName.
     */
    public String getSuperiorName()
    {
        return superiorName;
    }

    /**
     * @param superiorName The superiorName to set.
     */
    public void setSuperiorName(String superiorName)
    {
        this.superiorName = superiorName;
    }

    public String getAccountMail()
    {
        return accountMail;
    }

    public void setAccountMail(String accountMail)
    {
        this.accountMail = accountMail;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getChiefLeader()
    {
        return chiefLeader;
    }

    public void setChiefLeader(String chiefLeader)
    {
        this.chiefLeader = chiefLeader;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getAdminPass()
    {
        return adminPass;
    }

    public void setAdminPass(String adminPass)
    {
        this.adminPass = adminPass;
    }

	public String getAccountCategory() {
		return accountCategory;
	}

	public void setAccountCategory(String accountCategory) {
		this.accountCategory = accountCategory;
	}

	public String getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(String accountLevel) {
		this.accountLevel = accountLevel;
	}

	public String getAccountNature() {
		return accountNature;
	}

	public void setAccountNature(String accountNature) {
		this.accountNature = accountNature;
	}

	public String getAdminiLevel() {
		return adminiLevel;
	}

	public void setAdminiLevel(String adminiLevel) {
		this.adminiLevel = adminiLevel;
	}
    
}
