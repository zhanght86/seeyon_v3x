package com.seeyon.v3x.office.common.manager.impl;
/**
 * 用户申请单管理实现类
 */
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.dao.OfficeApplyDao;
import com.seeyon.v3x.office.common.dao.OfficeTypeDao;
import com.seeyon.v3x.office.common.manager.OfficeApplyManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class OfficeApplyManagerImpl implements OfficeApplyManager {
	
	private OfficeApplyDao officeApplyDao;
	private OfficeTypeDao officeTypeDao;

	/**
	 * 取得管理者对应的申请类型的所有申请单列表
	 * @param applyType
	 * @param applyManager
	 * @throws BusinessException
	 */
	public List getOfficeApplyList(int applyType, User user)  {
		return officeApplyDao.getUserModelManagers(applyType, user);
	}
	
	public String getUserModelManagersIds(final int modelId,final User user)
	{
		String mgrIds="";
		List idsList=officeApplyDao.getUserModelManagers(modelId, user);
		if(idsList==null || idsList.size()<=0)
		{
			return "";
		}
		int i;
		int size = idsList.size();
		for(i=0;i<size;i++)
		{
			mgrIds+=((V3xOrgMember)idsList.get(i)).getId();
			if(i<size-1){mgrIds+=",";}
		}
		return mgrIds;
	}

	public OfficeApplyDao getOfficeApplyDao() {
		return officeApplyDao;
	}

	public void setOfficeApplyDao(OfficeApplyDao officeApplyDao) {
		this.officeApplyDao = officeApplyDao;
	}

	public OfficeTypeDao getOfficeTypeDao() {
		return officeTypeDao;
	}

	public void setOfficeTypeDao(OfficeTypeDao officeTypeDao) {
		this.officeTypeDao = officeTypeDao;
	}

	
}
