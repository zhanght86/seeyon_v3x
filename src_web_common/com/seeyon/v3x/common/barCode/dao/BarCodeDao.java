package com.seeyon.v3x.common.barCode.dao;

import com.seeyon.v3x.common.barCode.domain.BarCodeInfo;
import com.seeyon.v3x.common.dao.BaseDao;

public class BarCodeDao extends BaseDao<BarCodeInfo> {

	public BarCodeInfo getByObjectId(Long objectId) {
		return (BarCodeInfo)super.findUnique("from " + BarCodeInfo.class.getName() + " as b where b.objectId=?", null, objectId);
	}
}
