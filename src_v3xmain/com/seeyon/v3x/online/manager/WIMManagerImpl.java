package com.seeyon.v3x.online.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;

public class WIMManagerImpl implements WIMManager {
	
	private static final Log log = LogFactory.getLog(WIMManagerImpl.class);

	private OrgManager orgManager;
	
	@Override
	public boolean isColRelativeTeam(long teamid) throws BusinessException {
		// TODO Auto-generated method stub
		V3xOrgTeam orgTeam = orgManager.getTeamById(teamid);
		if(orgTeam == null){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public boolean existRelativeTeam(long summaryId) throws BusinessException {
		// TODO Auto-generated method stub
		V3xOrgTeam orgTeam = this.getRelativeTeamByColId(summaryId);
		if(orgTeam == null){
			return false;
		}else{
			return true;
		}
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	@Override
	public List<V3xOrgTeam> getAllDiscussTeam(Long memberId) throws BusinessException {
		// TODO Auto-generated method stub
		List<V3xOrgTeam> orgTeams = new ArrayList<V3xOrgTeam>();
		List<V3xOrgTeam> teams = orgManager.getTeamsExceptPersonByMember(memberId);
		for (V3xOrgTeam v3xOrgTeam : teams) {
			if(V3xOrgEntity.TEAM_TYPE_COLTEAM!=v3xOrgTeam.getType()){
				orgTeams.add(v3xOrgTeam);
			}
		}
		return orgTeams;
	}

	@Override
	public V3xOrgTeam getRelativeTeamByColId(long summaryId) throws BusinessException {
		// TODO Auto-generated method stub
		List<V3xOrgTeam> teams = orgManager.getTeamByType(V3xOrgEntity.TEAM_TYPE_COLTEAM);
		V3xOrgTeam orgTeam = null;
		for (V3xOrgTeam v3xOrgTeam : teams) {
			if(summaryId == v3xOrgTeam.getOwnerId()){
				orgTeam = v3xOrgTeam;
				break;
			}
		}
		return orgTeam;
	}
}
