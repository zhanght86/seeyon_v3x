package com.seeyon.v3x.collaboration.manager.impl;

import java.util.List;

import www.seeyon.com.v3x.form.base.SelectPersonOperation;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.dao.ColRelationAuthorityDao;
import com.seeyon.v3x.collaboration.domain.ColRelationAuthority;
import com.seeyon.v3x.collaboration.manager.ColRelationAuthorityManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.util.Strings;

/**
 * @author DEV23
 * 
 */
public class ColRelationAuthorityImpl implements ColRelationAuthorityManager {

	private ColRelationAuthorityDao colRelationAuthorityDao;
	private AffairManager affairManager;

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setColRelationAuthorityDao(
			ColRelationAuthorityDao colRelationAuthorityDao) {
		this.colRelationAuthorityDao = colRelationAuthorityDao;
	}

	public void saveColRelationAuthority(
			ColRelationAuthority colRelationAuthority) {
		if (colRelationAuthority == null) {
			return;
		}
		this.colRelationAuthorityDao.create(colRelationAuthority);
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.collaboration.manager.ColRelationAuthorityManager#create(java.lang.String[], java.lang.String)
	 */
	@Override
	public boolean create(String[] summaryIds, String authorities)
			throws Exception {
		SelectPersonOperation selectPersonOperation = new SelectPersonOperation();
		boolean isRelationAuthority = false;
		String[][] authInfos = Strings.getSelectPeopleElements(authorities);
		if (authInfos != null && authInfos.length > 0) {
			isRelationAuthority = true;
		}
		for (String summaryId : summaryIds) {
			this.colRelationAuthorityDao.delRelationAuthority(Long.valueOf(summaryId));
			if (authInfos != null) {
				for (String[] strings : authInfos) {
					ColRelationAuthority colRelationAuthority = new ColRelationAuthority();
					colRelationAuthority.setIdIfNew();
					colRelationAuthority.setSummaryId(Long.valueOf(summaryId));
					colRelationAuthority.setUsertype(selectPersonOperation.changeType(strings[0]));
					colRelationAuthority.setUserid(Long.valueOf(strings[1]));
					saveColRelationAuthority(colRelationAuthority);
				}
			}
			this.affairManager.updateRelationAuthority(ApplicationCategoryEnum.collaboration, Long.valueOf(summaryId), StateEnum.col_sent.key(), isRelationAuthority);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.collaboration.manager.ColRelationAuthorityManager#getAuthoritiesBySummaryId(java.lang.String)
	 */
	@Override
	public String getAuthoritiesBySummaryId(String summaryId) throws Exception {
		List<ColRelationAuthority> authorityList = colRelationAuthorityDao.getAuthorityList(Long.valueOf(summaryId));
		StringBuffer authorities = new StringBuffer();
		for(ColRelationAuthority authority : authorityList){
			authorities.append(authority.getUserType());
			authorities.append("|");
			authorities.append(authority.getUserid());
			authorities.append(",");
		}
		return Functions.parseElements(authorities.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.collaboration.manager.ColRelationAuthorityManager#delete(java.lang.Long)
	 */
	public boolean delete(Long summaryId,boolean isUpdateAffairAuthority) throws Exception {
		this.colRelationAuthorityDao.delRelationAuthority(summaryId);
		if(isUpdateAffairAuthority){
			this.affairManager.updateRelationAuthority(ApplicationCategoryEnum.collaboration, summaryId, StateEnum.col_sent.key(), false);
		}
		return true;
	}
}
