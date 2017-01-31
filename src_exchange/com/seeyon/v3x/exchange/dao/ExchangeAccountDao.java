package com.seeyon.v3x.exchange.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.util.Constants;

public class ExchangeAccountDao extends BaseHibernateDao<ExchangeAccount> {
	
	public List<ExchangeAccount> getExternalAccounts(Long domainId) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.domainId = ? order by a.createTime asc,a.name";
		return super.find(hsql, false,domainId);
	}
	//lijl添加,isPage表示是否分页-1表示不分页
	public List<ExchangeAccount> getExternalAccounts(Long domainId,int isPage) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.domainId = ? order by a.createTime asc,a.name";
		return super.find(hsql, false,domainId);
	}
	public List<ExchangeAccount> getExternalAccountsByName(String name, Long domainId){
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.name like ? and a.domainId=? order by a.createTime asc,a.name";
		return super.find(hsql, false, "%"+ name +"%", domainId);
	}
	
	public List<ExchangeAccount> getExternalAccount(String name, Long domainId) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.name=? and a.domainId=?";
		Object[] values = {false, name, domainId};
		return super.find(hsql, values);
	}
	
	public List<ExchangeAccount> getExternalAccount(long id, String name, Long domainId) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.id!=? and a.name=? and a.domainId=?";
		Object[] values = {false, id, name, domainId};
		return super.find(hsql, values);
	}
	
	public List<ExchangeAccount> getInternalAccounts(Long domainId) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.domainId = ?  order by a.name";
		return super.find(hsql, true,domainId);
	}
	
	public List<ExchangeAccount> getExchangeOrgs(Long domainId) {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? and a.domainId = ? and (a.accountType=? or a.accountType=?) and status=? order by a.name";
		Object[] values = {false,domainId, Constants.C_iAccountType_Default, Constants.C_iAccountType_Org, ExchangeAccount.C_iStatus_Active};
		return super.find(hsql, values);
	}
	public List<ExchangeAccount> getExternalAccounts() {
		String hsql = "from ExchangeAccount as a where a.isInternalAccount=? order by a.name";
		return super.find(hsql, false);
	}
	
}
