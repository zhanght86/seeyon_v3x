package com.seeyon.v3x.link.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSpaceAcl;

public class LinkSpaceDao extends BaseHibernateDao<LinkSpace> {

	/**
	 * 获取当前用户可以访问的扩展空间
	 */
	@SuppressWarnings("unchecked")
	public List<LinkSpace> getLinkSpacesCanAccess(List<Long> domainIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		String hql = "select sp from " + LinkSpace.class.getName() + " sp, " + LinkSpaceAcl.class.getName() + " acl " +
				" where acl.linkSpaceId=sp.id and acl.userId in (:domainIds) order by sp.sort asc ";
		return this.find(hql, -1, -1, params);
	}

	/**
	 * 校验当前用户是否能够继续使用扩展空间
	 */
	@SuppressWarnings("unchecked")
	public boolean canUseTheLinkSpace(List<Long> domainIds, Long linkSpaceId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		params.put("linkSpaceId", linkSpaceId);
		String hql = "select count(sp.id) from " + LinkSpace.class.getName() + " sp, " + LinkSpaceAcl.class.getName() + " acl " +
				" where acl.linkSpaceId=sp.id and sp.id=:linkSpaceId and acl.userId in (:domainIds) ";
		List<Integer> result = this.find(hql, -1, -1, params);
		int count = 0;
		if (result != null && result.size() == 1) {
			count = result.get(0);
		}
		return count > 0;
	}

}
