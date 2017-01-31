package com.seeyon.v3x.notepager.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.notepager.domain.Notepage;

public class NotepageDao extends BaseHibernateDao<Notepage> {
	public Notepage getNotepageByMemberID(Long memberId){
		Notepage notepage=new Notepage();
		notepage.setMemberId(memberId);
		
		List<Notepage> ns = super.findByExample(notepage);
		if(ns != null && !ns.isEmpty()){
			return ns.get(0);
		}
		
		return null;
	}

}
