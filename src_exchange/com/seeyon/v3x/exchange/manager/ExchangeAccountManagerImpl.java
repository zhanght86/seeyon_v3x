package com.seeyon.v3x.exchange.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheObject;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.exchange.dao.ExchangeAccountDao;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.util.Constants;

public class ExchangeAccountManagerImpl implements ExchangeAccountManager {
	
	private CacheObject<Date> modifyTimestamp = null;
	
	private ExchangeAccountDao exchangeAccountDao;
	
	public void setExchangeAccountDao(ExchangeAccountDao exchangeAccountDao) {
		this.exchangeAccountDao = exchangeAccountDao;
	}
	
	public void init(){
		CacheAccessable cacheFactory = CacheFactory.getInstance(ExchangeAccountManager.class);
		modifyTimestamp = cacheFactory.createObject("modifyTimestamp");
		modifyTimestamp.set(new Date());
	}

	public void create(String accountId,
			String name,
			int accountType,
			String description,
			boolean isInternalAccount,
			long internalOrgId,
			long internalDeptId,
			long internalUserId,
			String exchangeServerId,
			int status) throws Exception {
		ExchangeAccount exchangeAccount = new ExchangeAccount();
		User user = CurrentUser.get();
		exchangeAccount.setIdIfNew();
		exchangeAccount.setAccountId(accountId);
		exchangeAccount.setName(name);
		exchangeAccount.setAccountType(accountType);
		exchangeAccount.setDescription(description);
		exchangeAccount.setIsInternalAccount(isInternalAccount);
		exchangeAccount.setInternalOrgId(internalOrgId);
		exchangeAccount.setInternalDeptId(internalDeptId);
		exchangeAccount.setInternalUserId(internalUserId);
		exchangeAccount.setExchangeServerId(exchangeServerId);
		long l = System.currentTimeMillis();
		exchangeAccount.setCreateTime(new Timestamp(l));
		exchangeAccount.setLastUpdate(new Timestamp(l));
		exchangeAccount.setStatus(status);
		exchangeAccount.setDomainId(user.getLoginAccount());
		exchangeAccountDao.save(exchangeAccount);
		
		updateModifyTimestamp();
	}
	
	public void create(String name, String description) throws Exception {
		User user = CurrentUser.get();
		ExchangeAccount exchangeAccount = new ExchangeAccount();		
		exchangeAccount.setIdIfNew();
		long l = System.currentTimeMillis();
		exchangeAccount.setAccountId(String.valueOf(l)); //为手工设置的外部单位随机生成一个AccountId
		exchangeAccount.setName(name);
		exchangeAccount.setAccountType(Constants.C_iAccountType_Default);
		exchangeAccount.setDescription(description);
		exchangeAccount.setIsInternalAccount(false);		
		exchangeAccount.setCreateTime(new Timestamp(l));
		exchangeAccount.setLastUpdate(new Timestamp(l));
		exchangeAccount.setStatus(ExchangeAccount.C_iStatus_Active);
		exchangeAccount.setDomainId(user.getLoginAccount());
		exchangeAccountDao.save(exchangeAccount);
		
		updateModifyTimestamp();
	}
	
	public void update(ExchangeAccount exchangeAccount) throws Exception {		
		exchangeAccountDao.update(exchangeAccount);
		updateModifyTimestamp();
	}
	
	public ExchangeAccount getExchangeAccount(long id) {
		return exchangeAccountDao.get(id);
	}
	
	public ExchangeAccount getExchangeAccountByAccountId(String accountId) {
		return exchangeAccountDao.findUniqueBy("accountId", accountId);
	}
	
	public List<ExchangeAccount> getExternalAccounts(Long domainId) {
		return exchangeAccountDao.getExternalAccounts(domainId);
	}
	public List<ExchangeAccount> getExternalAccounts(Long domainId,int isPage) {
		return exchangeAccountDao.getExternalAccounts(domainId,isPage);
	}
	public List<ExchangeAccount> getExternalAccountsByName(String name, Long domainId){
		return exchangeAccountDao.getExternalAccountsByName(name, domainId);
	}
	
	public List<ExchangeAccount> getInternalAccounts(Long domainId) {
		return exchangeAccountDao.getInternalAccounts(domainId);
	}
		
	public List<ExchangeAccount> getExternalOrgs(Long domainId) {
		return exchangeAccountDao.getExchangeOrgs(domainId);
	}
	
	public void delete(long id) throws Exception {
		exchangeAccountDao.delete(id);
		updateModifyTimestamp();
	}
	
	/** 返回单位内部帐号（在交换中心创建交换帐号时调用） */
	public List<ExchangeAccount> getExternalAccounts(){
		return exchangeAccountDao.getExternalAccounts();
	}
	
	public boolean containExternalAccount(String name, long domainId) {
		List<ExchangeAccount> accounts = exchangeAccountDao.getExternalAccount(name, domainId);
		if (accounts.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean containExternalAccount(long id, String name, long domainId) {
		List<ExchangeAccount> accounts = exchangeAccountDao.getExternalAccount(id, name, domainId);
		if (accounts.size() > 0) {
			return true;
		}
		return false;
	}	
	
	public void batchCreate(String externalAccounts, String description)
			throws Exception {
		User user = CurrentUser.get();
		List<ExchangeAccount> exchangeAccountList = new ArrayList<ExchangeAccount>();
		String accounts[] = externalAccounts.split("↗");
		for (int i = 0; i < accounts.length; i++) {
			String externalAccount = accounts[i];
			ExchangeAccount exchangeAccount = new ExchangeAccount();
			exchangeAccount.setIdIfNew();
			long l = System.currentTimeMillis();
			exchangeAccount.setAccountId(String.valueOf(l)); // 为手工设置的外部单位随机生成一个AccountId
			exchangeAccount.setName(externalAccount);
			exchangeAccount.setAccountType(Constants.C_iAccountType_Default);
			exchangeAccount.setDescription("");
			exchangeAccount.setIsInternalAccount(false);
			exchangeAccount.setCreateTime(new Timestamp(l));
			exchangeAccount.setLastUpdate(new Timestamp(l));
			exchangeAccount.setStatus(ExchangeAccount.C_iStatus_Active);
			exchangeAccount.setDomainId(user.getLoginAccount());

			exchangeAccountList.add(exchangeAccount);
		}
		exchangeAccountDao.savePatchAll(exchangeAccountList);

		updateModifyTimestamp();
	}

	private void updateModifyTimestamp(){
		modifyTimestamp.set(new Date());
	}
	
	public boolean isModifyExchangeAccounts(Date orginalTimestamp){
		return !modifyTimestamp.equals(orginalTimestamp);
	}
	
	public Date getLastModifyTimestamp(){
		return this.modifyTimestamp.get();
	}
	
}
