/**
 * 
 */
package com.seeyon.v3x.collaboration.dao;

import java.util.List;

import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.common.dao.BaseHibernateDao;


/**
 * 类描述：
 * 创建日期：
 *
 * @author Mercurial_lin
 * @version 1.0 
 * @since JDK 5.0
 */
public class ManagementSetDao extends BaseHibernateDao<ManagementSet>{

	public List<ManagementSet> findByDomainId(long domainId){
		String hsql = "from ManagementSet as a where a.domainId = ? order by a.lastUpdate asc";
		return super.find(hsql, domainId);
	}
	
	public List<ManagementSet> findByGrantorIdAndType(long domainId, long grantorId, int type){
		String hsql = "from ManagementSet as a where a.domainId = ? and a.memberId like ? and a.manageRange like ? order by a.lastUpdate asc";
		return super.find(hsql, domainId, "%"+grantorId+"%", "%"+type+"%");
	}
	
	public List<ManagementSet> findByGrantorId(long domainId, long grantorId){
		String hsql = "from ManagementSet as a where a.domainId = ? and a.memberId like ? order by a.lastUpdate asc";
		return super.find(hsql, domainId, "%"+grantorId+"%");
	}
	
}
