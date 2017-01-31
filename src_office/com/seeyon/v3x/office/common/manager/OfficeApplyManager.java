package com.seeyon.v3x.office.common.manager;

import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;

/**
 * 拥护申请单管理接口类
 * @author lindx
 *
 */
public interface OfficeApplyManager {
	
	public String getUserModelManagersIds(final int modelId,final User user);
	public List getOfficeApplyList(int applyType, User user) ;
}
