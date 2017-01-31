package com.seeyon.v3x.office.common.manager.cap;

import java.util.List;

import com.seeyon.cap.office.common.manager.OfficeApplyManagerCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.office.common.manager.OfficeApplyManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class OfficeApplyManagerCAPImpl implements OfficeApplyManagerCAP {

	private OfficeApplyManager officeApplyManager;

	public void setOfficeApplyManager(OfficeApplyManager officeApplyManager) {
		this.officeApplyManager = officeApplyManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<V3xOrgMember> getOfficeApplyList(int applyType, User user) {
		return officeApplyManager.getOfficeApplyList(applyType, user);
	}

}