package com.seeyon.v3x.system.signet.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.event.AbstractOrganizationEventListener;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.MemberAccountChangeEvent;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventException;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class SignetOrganizationEventListener extends AbstractOrganizationEventListener implements OrganizationEventListener{
	private final static Log logger = LogFactory.getLog(OrganizationEventComposite.class);
	private SignetManager signetManager;
	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}
	private void init(){
		OrganizationEventComposite.getInstance().addHandler(this);
	}
	public void addAccount(Object account) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void addDepartment(Object dept) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void addLevel(Object level) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void addMember(Object member) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void addPost(Object post) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void changePassword(Object oldMember, Object newMember)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void changePassword(Object newMember, String oldPassword)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void deleteAccount(Object account) throws OrganizationEventException {
		if( account!= null && account instanceof V3xOrgAccount){
			try {
				signetManager.deleteByAccountId(((V3xOrgAccount)account).getId());
			} catch (Exception e) {
				logger.error(e);
			}	
		}
	}

	public void deleteDepartment(Object dept) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void deleteLevel(Object level) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMember(Object memeber) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void deletePost(Object post) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableDepartment(Object newDept)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableDepartment(Object oldDept, Object newDept)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableLevel(Object newLevel) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableLevel(Object oldLevel, Object newLevel)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableMember(Object newMember)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enableMember(Object oldMember, Object newMember)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enablePost(Object newPost) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void enablePost(Object oldPost, Object newPost)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void insertLevel(Object level) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void moveDepartment(Object oldDept, Object newDept)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void setDepartmentOrder(Object newDept, long oldOrder)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void setDepartmentOrder(Object oldDept, Object newDept)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void setMemberOrder(Object newMember, long oldOrder)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void setMemberOrder(Object oldMember, Object newMember)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void updateAccount(Object account) throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void updateDepartment(Object oldDept, Object newDept)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void updateLevel(Object oldLevel, Object newLevel)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void updateMember(Object oldMember, Object newMember)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}

	public void updatePost(Object oldPost, Object newPost)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addDutyLevel(Object dutyLevel)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateDutyLevel(Object oldLevel, Object newLevel)
			throws OrganizationEventException {
		// TODO Auto-generated method stub
		
	}
	
	@ListenEvent(event = DeleteMemberEvent.class)
	public void deleteMember(DeleteMemberEvent event) {
		try {
			signetManager.clearSignet(event.getMember().getId());
		} catch (Exception e) {
			logger.error("印章监听删除人员事件异常"+e.getLocalizedMessage());
		}
	}

	@ListenEvent(event = MemberAccountChangeEvent.class)
	public void memberAccountChange(MemberAccountChangeEvent event) {
		try {
			signetManager.clearSignet(event.getMember().getId());
		} catch (Exception e) {
			logger.error("印章监听人员跨单位调整事件异常"+e.getLocalizedMessage());
		}
	}

}
