package com.seeyon.v3x.bulletin.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.SQLWildcardUtil;

/**
 * @author Administrator
 *
 */
public class BulTypeDao extends BaseHibernateDao<BulType> {

	public List<BulType> getByAccountId(long accountId){
		DetachedCriteria criteria = DetachedCriteria.forClass(BulType.class);
		criteria.add(Restrictions.eq("accountId", accountId));
		return super.executeCriteria(criteria, -1, -1);
	}
	
	/**
	 * 按公告名字查询得到
	 * @param memberId
	 * @param typename
	 * @return
	 * @throws BulletinException
	 */
	public List<BulType> getAllBulType(Long memberId ,String typename) throws BulletinException {
		final String hqlf = " from BulType as bt where bt.typeName like ? order by bt.createDate desc" ;
		return this.find(hqlf, -1, -1, null, "%" + SQLWildcardUtil.escape(typename) + "%") ;
	}
	
	/**
	 * 按审核员进行查询
	 * @param memberId
	 * @param name
	 * @return
	 * @throws BulletinException
	 */
	public List<BulType> getAllBulTypeByMember(Long memberId , String name)throws BulletinException {
		List<BulType> list = new ArrayList<BulType>();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("name", "%" + SQLWildcardUtil.escape(name) + "%");
		
		final String hql = "select t from " + BulType.class.getName() + " as t, " + V3xOrgMember.class.getName() + " as m "
							+ " where t.auditUser=m.id and m.name like:name order by t.createDate";
		
		List<BulType> bulTypeList = this.find(hql, -1, -1, parameterMap);
		if(bulTypeList.isEmpty()) {
			return list ;
		}
		return bulTypeList ;
		
	}
}
