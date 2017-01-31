package com.seeyon.v3x.bbs.dao;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsBoardAuth;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.1 2007-9-12
 */
public class BbsBoardAuthDao extends BaseHibernateDao<V3xBbsBoardAuth> {
	
	@SuppressWarnings("unchecked")
	public List<V3xBbsBoardAuth> getAllV3xBbsBoardAuth(){
		return super.getHibernateTemplate().loadAll(V3xBbsBoardAuth.class);
	}
}
