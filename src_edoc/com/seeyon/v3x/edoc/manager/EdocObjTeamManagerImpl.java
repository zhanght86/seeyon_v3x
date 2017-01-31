package com.seeyon.v3x.edoc.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheObject;
import com.seeyon.v3x.edoc.dao.EdocObjTeamDao;
import com.seeyon.v3x.edoc.dao.EdocObjTeamMemberDao;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.organization.manager.OrgManager;

public class EdocObjTeamManagerImpl implements EdocObjTeamManager {
	
	private CacheObject<Date> modifyTimestamp = null;
	
	private EdocObjTeamDao edocObjTeamDao=null;
	private EdocObjTeamMemberDao edocObjTeamMemberDao=null;
	private OrgManager orgManager;

	public EdocObjTeamDao getEdocObjTeamDao() {
		return edocObjTeamDao;
	}

	public void setEdocObjTeamDao(EdocObjTeamDao edocObjTeamDao) {
		this.edocObjTeamDao = edocObjTeamDao;
	}

	public EdocObjTeamMemberDao getEdocObjTeamMemberDao() {
		return edocObjTeamMemberDao;
	}

	public void setEdocObjTeamMemberDao(EdocObjTeamMemberDao edocObjTeamMemberDao) {
		this.edocObjTeamMemberDao = edocObjTeamMemberDao;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}
	public void init(){
		CacheAccessable cacheFactory = CacheFactory.getInstance(EdocObjTeamManager.class);
		modifyTimestamp = cacheFactory.createObject("modifyTimestamp");
		modifyTimestamp.set(new Date());
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public List<EdocObjTeam> findAll(Long accountId)
	{
		List<EdocObjTeam> ls=edocObjTeamDao.findAllByAccount(accountId, true);
		for(EdocObjTeam eot:ls)
		{
			eot.changeSelObjsStr();
		}
		return ls;		
	}
	
	public List<EdocObjTeam> findByName(String name, Long accountId)
	{
		List<EdocObjTeam> ls=edocObjTeamDao.findAllByName(name,accountId, true);
		for(EdocObjTeam eot:ls)
		{
			eot.changeSelObjsStr();
		}
		return ls;		
	}
	public List<EdocObjTeam> findAllNotPager(Long accountId)
	{
		return edocObjTeamDao.findAllByAccount(accountId, false);
	}
	public EdocObjTeam getById(Long teamId)
	{
		EdocObjTeam eot=edocObjTeamDao.get(teamId);
		eot.changeSelObjsStr();
		return eot;
	}
	
	public void delete(String ids)
	{
		edocObjTeamDao.updateState(ids,EdocObjTeam.STATE_DEL);
		updateModifyTimestamp();
	}
	
	public void save(EdocObjTeam edocObjTeam)
	{
		edocObjTeamDao.save(edocObjTeam);
		updateModifyTimestamp();
	}
	public void update(EdocObjTeam edocObjTeam)
	{
		edocObjTeamDao.update(edocObjTeam);
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String ajaxGetByName(String orgName,String accountIdStr)
	{
		String retStr="-1";
		Long accountId=Long.parseLong(accountIdStr);
		EdocObjTeam et=edocObjTeamDao.findByAccountAndName(accountId, orgName);
		if(et!=null){retStr=et.getId().toString();}
		return retStr;
	}

}
