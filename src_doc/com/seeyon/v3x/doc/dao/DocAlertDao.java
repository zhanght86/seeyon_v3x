package com.seeyon.v3x.doc.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocAlert;

public class DocAlertDao extends BaseHibernateDao<DocAlert> {
//	public boolean findDocAlert(long resourceId, long userId){
//		String hsql="from DocAlert as doc where doc.docResourceId=?  and doc.createUserId=?";
//		List list=super.find(hsql, resourceId,userId);
//		if(!list.isEmpty()){
//			return false;
//		}else{
//			hsql="from DocAlert as doc where doc.docResourceId=?  and doc.alertUserId=?";
//			List _list=super.find(hsql, resourceId,userId);
//			if(!_list.isEmpty()){
//				return false;
//			}else{
//				return true;
//			}
//		}
//	}
//	
//	public DocAlert findDocAlerts(long userId,long resourceId){
//		String hsql="from DocAlert as doc where doc.alertUserId=? and doc.docResourceId=?";
//		List list=super.find(hsql,userId,resourceId);
//		if(!list.isEmpty()){
//			return (DocAlert)list.get(0);
//		}else{
//			return null;
//		}
//	}
//	
//	public List<DocAlert> getDocAlerts(long userId, long docResId) {
//		String hsql = "from DocAlert as a where a.createUserId=? and a.docResourceId=?";		
//		return super.find(hsql, userId, docResId);
//	}
//	
////	public List findAllByDocAlert(List list,DocResource docRes){
////		if(list.isEmpty())return null;
////		String path=docRes.getLogicalPath();
////		StringBuffer buffer=new StringBuffer();
////		buffer.append("from DocAlert as doc where doc.docResourceId in(");
//////		buffer.append("select res.id from ");
//////		buffer.append("(select docRes.id from DocResource as docRes where docRes.logicalPath like '"+path+"%'");
//////		for(int i=0;i<list.size();i++){
//////			String temp=(String)list.get(i);
//////			buffer.append(" or docRes.id="+Integer.valueOf(temp));
//////		}
//////		buffer.append(") as res where res.id ");
//////		buffer.append(" in (select distinct doc.id from DocAlert as doc)");
////		
////		List  the_list=super.find(buffer.toString());
////		return the_list;
////	}
//	
//	public List findAllByDocAlerts(List list,DocResource docRes){
//		if(list.isEmpty())return null;
//		String path=docRes.getLogicalPath();
//		StringBuffer buffer=new StringBuffer();
//		buffer.append("from DocAlert as doc where doc.docResourceId in (select res.id from DocResource as res where res.logicalPath like '"+path+".%' or " );
//		for(int i=0;i<list.size();i++){
//			String ResId=(String)list.get(i);
//			buffer.append("res.id="+Integer.valueOf(ResId));
//			if(i!=list.size()-1){
//				buffer.append(" or ");
//			}
//		}
//		buffer.append(")");
//		List the_list=super.find(buffer.toString());
//	//	System.out.println("the size of the list is :"+the_list.size());
//		return the_list;
//	}
//	
//	public List findDocAlertsByDocResId(DocResource docRes){
//		long id=docRes.getId();
//		String path=docRes.getLogicalPath();
//		StringBuffer buffer=new StringBuffer();
//		buffer.append("from DocAlert as doc where doc.docResourceId in (select res.id from DocResource as res where res.logicalPath like '"+path+".%' or res.id="+id);
//		buffer.append(")");
//		
//		List list=super.find(buffer.toString());
//		return list;
//	}
	
//	public Session getASession(){
//		return super.getSession();
//	}
//	public void releaseTheSession(Session session){
//		super.releaseSession(session);
//	}
	public void deleteAlerts(String str){
		   super.getHibernateTemplate().bulkUpdate(str);
	   }

}
