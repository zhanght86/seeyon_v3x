package com.seeyon.v3x.doc.webmodel;
import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * 文档日志vo
 */
public class ListDocLog {
	private OperationLog operationLog;
	
	private  String  userName;

	private V3xOrgMember member;
	
	private V3xOrgAccount account;
	
	public  V3xOrgAccount getAccount(){
		return account;
		
	}
	
	public void setAccount(V3xOrgAccount account){
		
		this.account = account;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}

	public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public OperationLog getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(OperationLog operationLog) {
		this.operationLog = operationLog;
	}
}
