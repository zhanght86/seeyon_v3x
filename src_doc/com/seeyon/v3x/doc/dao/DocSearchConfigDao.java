package com.seeyon.v3x.doc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocSearchConfig;

/**
 * 文档库搜索条件设置Dao
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-12-14
 */
public class DocSearchConfigDao extends BaseHibernateDao<DocSearchConfig> {

	public List<DocSearchConfig> getSearchConfigs4Lib(Long docLibId) {
		String hql = "from " + DocSearchConfig.class.getCanonicalName() + " where docLibId=? order by sortId";
		return this.find(hql, docLibId);
	}

	public void batchUpdateDocLibId(Long oldLibId, Long newLibId) {
		String hql = "update " + DocSearchConfig.class.getCanonicalName() + " set docLibId = ? where docLibId =?";
		this.bulkUpdate(hql, null, newLibId, oldLibId);
	}

}
