package com.seeyon.v3x.plugin.ca.caaccount.webmodel;

import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;


public class WebCAAccountVo {
    private CAAccount caAccount;
    private WebV3xOrgMember webV3xOrgMember;        
    
    public CAAccount getCaAccount() {
        return caAccount;
    }
    
    public void setCaAccount(CAAccount caAccount) {
        this.caAccount = caAccount;
    }
    
    public WebV3xOrgMember getWebV3xOrgMember() {
        return webV3xOrgMember;
    }
    
    public void setWebV3xOrgMember(WebV3xOrgMember webV3xOrgMember) {
        this.webV3xOrgMember = webV3xOrgMember;
    }
}
