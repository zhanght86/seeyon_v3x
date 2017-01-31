package com.seeyon.v3x.hr.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.HrSalaryPassword;

public class HrSalaryPasswordDao extends BaseHibernateDao<HrSalaryPassword>{
	
	public HrSalaryPassword getSalaryRecordUniq(final long userId){	
		return this.findUniqueBy("userId", userId) ;
	}
	
}
