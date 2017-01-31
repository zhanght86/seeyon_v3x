package com.seeyon.v3x.edoc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocFormAcl;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;

public class EdocFormExtendInfoDao extends BaseHibernateDao<EdocFormExtendInfo>{
	public void cancelDefaultEdocForm(Long id){
		String hsql="update EdocFormExtendInfo s set s.isDefault = ? where s.id = ?";
		super.bulkUpdate(hsql,null,false,id);
	}
	public EdocFormExtendInfo getDefaultEdocFormExtendInfo(Long domainId,int type){
		String hsql = "select s from EdocForm ef inner join ef.edocFormExtendInfo s  where   s.accountId = ? and ef.type = ? and s.isDefault = ? ";
		List<EdocFormExtendInfo> list = super.find(hsql, new Object[]{domainId,type,true});
		if(list!=null && list.size()!=0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
//	public EdocFormExtendInfo getEdocFormExtendInfo(Long formId,Long accountId){
//		String hql = "from EdocFormExtendInfo info where info.accountId = :accountId and 
//	}
}
