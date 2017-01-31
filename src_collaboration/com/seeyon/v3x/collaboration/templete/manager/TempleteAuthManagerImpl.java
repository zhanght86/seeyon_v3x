/**
 * 
 */
package com.seeyon.v3x.collaboration.templete.manager;

import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-4-25
 */
public class TempleteAuthManagerImpl extends BaseHibernateDao<TempleteAuth> implements TempleteAuthManager {

	public void delete(long objectId) {
		super.delete(new Object[][]{{TempleteAuth.PROP_objectId, objectId}});
	}

}
