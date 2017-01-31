package com.seeyon.v3x.link.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.link.domain.LinkAcl;
import com.seeyon.v3x.util.CommonTools;

public class LinkAclDao extends BaseHibernateDao<LinkAcl> {

	/**
	 * 获取关联系统授权
	 */
	@SuppressWarnings("unchecked")
	public List<Long> findLinkByAcl(String orgIds) {
		List<Long> ids = CommonTools.parseStr2Ids(orgIds);
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<Long>();
		}
		String hql = "select distinct link.linkSystemId from " + LinkAcl.class.getName() + " as link where link.userId in (:ids)";
		return super.find(hql, -1, -1, CommonTools.newHashMap("ids", ids));
	}

}