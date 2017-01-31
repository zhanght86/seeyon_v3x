package com.seeyon.v3x.messageManager.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.messageManager.domain.MessageDelset;

public class MessageDelsetDao extends BaseHibernateDao<MessageDelset> {

	@SuppressWarnings("unchecked")
	public MessageDelset get() {
		List<MessageDelset> list = getHibernateTemplate().find("from MessageDelset");
		if(!list.isEmpty()){
			return list.get(0);
		}
		
		return null;
	}
	
	public void update(MessageDelset mds){
		getHibernateTemplate().saveOrUpdate(mds);
	}
	
	public void save(MessageDelset mds){
		getHibernateTemplate().save(mds);
	}

}
