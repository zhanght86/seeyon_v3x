package com.seeyon.v3x.doc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.doc.domain.DocFromPotent;
import com.seeyon.v3x.doc.exception.DocException;

public class DocFromPotentDao extends BaseHibernateDao<DocFromPotent>{
	public BaseModel insert(BaseModel bm) throws DocException {
		DocFromPotent tm  = (DocFromPotent)bm;
		tm.setId(UUIDLong.longUUID());
		try{
			getHibernateTemplate().save(tm); 
		}catch(Exception e){
			throw new DocException();
		}	
		
		return tm;
	}
	
	public List query(BaseModel bm) throws DocException {
		DocFromPotent fm = (DocFromPotent)bm;
		Map<String, Object> nmap = new HashMap<String, Object>();
		StringBuffer str = new StringBuffer("");

		if(fm.getDocresid() != null){
			str.append(" and fm.docresid=:fdocresid");
			nmap.put("fdocresid", fm.getDocresid());
		}
		if(fm.getAffairid() != null && fm.getAffairid() != 0){ 
			str.append(" and fm.affairid=:faffairid");
			nmap.put("faffairid", fm.getAffairid());
		}	

		return super.find(" from DocFromPotent fm where 1=1 "+ str.toString(), -1, -1,nmap);
	}
	
	
	public boolean del(BaseModel bm) throws DocException {
		try{
			getHibernateTemplate().delete(bm);
		} catch(Exception e){
			throw new DocException();
		}
		return true;
	}
}
