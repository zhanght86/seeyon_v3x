package com.seeyon.v3x.indexInterface.domain;

import java.util.List;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class UrlLinkDAO extends BaseHibernateDao {
	/*
	 * 在此要考虑数据库与索引库不同步，数据库中没有记录的情况
	 */
	public String findAffairID(int app,String appId,String memberId){
		String id=null;
		List<Long> list=getHibernateTemplate().find("select affair.id from Affair as affair where affair.objectId=? and  affair.memberId=? and  affair.app=? ", new Object[]{Long.parseLong(appId),Long.parseLong(memberId),app});
		if(list!=null&&!list.isEmpty()) 
		{
			if(list.get(0)!=null)
			{
				id=list.get(0).toString();
			}
		}
		if(id==null||id.equals("0")||id.equals(""))
		{
			return "-1";
		}
//		System.out.println("The affairId isssss::"+affairId);
		return id;
	}
	/**
	 * 在此要考虑数据库与索引库不同步，数据库中没有记录的情况
	 */
	public String findAffairID(List<ApplicationCategoryEnum> apps,String appId,String memberId){
		String id=null;
		StringBuilder sb=new StringBuilder();
		Object[] parameter = new Object[apps.size()+2];
		sb.append("select affair.id from Affair as affair where  affair.objectId=? and affair.memberId=?");
		parameter[0] = Long.parseLong(appId);
		parameter[1] = Long.parseLong(memberId);
		if(apps != null && apps.size()!= 0){
			sb.append(" and (");
			for(int i =0; i<apps.size(); i++){
				ApplicationCategoryEnum app = apps.get(i);
				if(i == 0){
					sb.append(" affair.app=? ");
				}else{
					sb.append(" or affair.app=? ");
				}
				parameter[i+2] = app.key();
			}
			sb.append(")");
		}
		sb.append(" order by affair.receiveTime desc");
		List<Long> list=getHibernateTemplate().find(sb.toString(),parameter);
		
		if(list!=null&&!list.isEmpty()) {
			if(list.get(0)!=null){
				id=list.get(0).toString();
			}
		}
		if(id==null||id.equals("0")||id.equals("")){
			return "-1";
		}
		return id;
	}
}
