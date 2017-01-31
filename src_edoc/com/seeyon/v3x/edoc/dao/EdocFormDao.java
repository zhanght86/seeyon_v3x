package com.seeyon.v3x.edoc.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.util.Strings;

public class EdocFormDao extends BaseHibernateDao<EdocForm>
{
	
	public List<EdocForm> getAllEdocForms(Long domainId)
	{
		String hsql = "select a from EdocForm as a left join a.edocFormExtendInfo as info  where info.accountId = ? and info.status<>? ";
		if(SystemEnvironment.hasPlugin("edoc")==false){
			hsql+=" and a.type=2";
		}
		hsql+=" order by info.status desc,a.type asc , a.lastUpdate asc";
		return super.find(hsql, domainId, EdocForm.C_iStatus_Deleted);
	}
	
	/**
	 * 查询数据库中是否存在同名的公文单
	 * @param id 		公文单ID
	 * @param domainId  单位ID
	 * @param formName  公文单名字
	 * @param type		公文单类型
	 * @return
	 */
	public int getEdocFormByName(String id,Long domainId,String formName,int type)
	{
		String hsql = " from EdocForm as a left join a.edocFormExtendInfo as info  where info.accountId = ? and info.status = ? and a.name =? and a.type=? ";
		if(Strings.isNotBlank(id)){
			hsql+=" and a.id <> ? ";
		}
		if(Strings.isNotBlank(id)){
			return super.getQueryCount(hsql, new Object[]{domainId, Constants.EDOC_USEED,formName,type,Long.valueOf(id)},
					new org.hibernate.type.Type[]{Hibernate.LONG,Hibernate.INTEGER,Hibernate.CHARACTER,Hibernate.INTEGER,Hibernate.LONG});
		}else{
			return super.getQueryCount(hsql, new Object[]{domainId, Constants.EDOC_USEED,formName,type},
					new org.hibernate.type.Type[]{Hibernate.LONG,Hibernate.INTEGER,Hibernate.CHARACTER,Hibernate.INTEGER});
	
		}
		
	}
	
	public List<EdocForm> getAllEdocFormsByName(Long domainId,String domainIds, String edocFormName){
		return getAllEdocFormsByName(domainId,domainIds,edocFormName,true);
	}
	
	/**
	 * 按照名称查询应经授权给这些单位的公文单
	 */
	
	public List<EdocForm> getAllEdocFormsByName(Long domainId,String domainIds, String edocFormName,boolean isOnlyAclEdocForms){
		
		List<Long> formids  = getEdocFormIdsByAcl(domainId,domainIds,null,isOnlyAclEdocForms);
		if(formids == null || formids.isEmpty()) 
			return new ArrayList<EdocForm>();
		StringBuilder sb = new StringBuilder();
		sb.append(" select ef from EdocForm ef inner join ef.edocFormExtendInfo info " );
		sb.append(" where ef.id in (:ids)");	
		sb.append(" and  info.accountId = :accountId ");
		sb.append(" and ef.name like :name ");
		sb.append(" order by info.status desc,ef.type asc , ef.lastUpdate desc");
		
		
		Map<String,Object> parameter = new HashMap<String,Object>();
		
		parameter.put("ids",formids);
		parameter.put("accountId",domainId);
		parameter.put("name", "%" + edocFormName + "%");
		
		List<EdocForm> list  =  super.find(sb.toString(),-1,-1,parameter);
		return EdocUtil.convertExtendInfo2EdocForm(list,domainId); 
	}
	
	public List<EdocForm> getAllEdocFormsForWeb(Long domainId,String domainIds){
		return getEdocForms(domainId,domainIds,null);
	}
	
	/**
	 * 查询授权给这些单位的公文单
	 * @param dimaind :  当前单位
	 * @param domainIds  ：被授权的单位列表
	 * @param isNeedCreateDomainName  ：是否需要查询公文单的制作单位.
	 * @return
	 */
	public List<EdocForm> getAllEdocFormsForWeb(Long domainId,String domainIds,boolean isOnlyAclEdocForms)
	{
		return getEdocForms(domainId,domainIds,null,isOnlyAclEdocForms);
	}
	
	private List<Long> getEdocFormIdsByAcl(Long domainId,String domainIds,Integer type){
		return getEdocFormIdsByAcl(domainId,domainIds,type,true);
	}
	
	private List<Long> getEdocFormIdsByAcl(Long domainId,String domainIds,Integer type,boolean isOnlyAclEdocForms){
		Map<String,Object> parameter = new HashMap<String,Object>();
		StringBuilder sb = new StringBuilder();
		List<Long> idList = null;
		if(domainIds != null){
			idList = new ArrayList<Long>();
			String[] tmps = domainIds.split(",");
			for(String id:tmps)
				idList.add(Long.valueOf(id));
		}
		
		sb.append(" select distinct a.id  ");
		sb.append(" from EdocForm a  left join a.edocFormAcls acl");
		
		boolean isGroupVer = (Boolean) (SysFlag.sys_isGroupVer.getFlag());// 判断是否为集团版
		//单组织版，公文单不能授权
		if(!isGroupVer) isOnlyAclEdocForms = false;
		
		if (isOnlyAclEdocForms) {
			sb.append(" where acl.domainId in (:domainId)");
			parameter.put("domainId",idList);
		}
		else {
			sb.append(" where (acl.domainId in (:domainId) or a.domainId = :accountId)");
			parameter.put("domainId",idList);
			parameter.put("accountId",domainId);
		}
			
		if(SystemEnvironment.hasPlugin("edoc")==false){
			sb.append(" and a.type=2");
		}else{
			if(type!= null){
				sb.append(" and type = :type");
				parameter.put("type", type);
			}
		}
		return (List<Long>)super.find(sb.toString(),-1,-1,parameter);
	}
	public List<EdocForm> getEdocForms(Long domainId,String domainIds,Integer type){
		return getEdocForms(domainId,domainIds,type,true);
	}
	public List<EdocForm> getEdocForms(Long domainId,String domainIds,Integer type,boolean isOnlyAclEdocForms)
	{
		
		//由于Hibernate解析左内连接的时候出错，所以专门拆出来一个方法查询公文单ID
		 
		List<Long> formids  = getEdocFormIdsByAcl(domainId,domainIds,type,isOnlyAclEdocForms);
		if(formids == null || formids.isEmpty()) 
			return new ArrayList<EdocForm>();
		StringBuilder sb = new StringBuilder();
		sb.append(" select ef from EdocForm ef inner join ef.edocFormExtendInfo info " );
		sb.append(" where ef.id in (:ids)");	
		sb.append(" and  info.accountId = :accountId ");
		sb.append(" order by info.status desc,ef.type asc , ef.lastUpdate desc");
		
		
		Map<String,Object> parameter = new HashMap<String,Object>();
		
		parameter.put("ids",formids);
		parameter.put("accountId",domainId);
		
		List<EdocForm> list  =  super.find(sb.toString(),-1,-1,parameter);
		return EdocUtil.convertExtendInfo2EdocForm(list,domainId); 
	}
	public List<EdocForm> getAllEdocFormsByType(Long domainId,int type)
	{
		String hsql = "select a from EdocForm as a left join a.edocFormExtendInfo info where info.accountId = ? and info.status<>? and a.type=?";
		if(SystemEnvironment.hasPlugin("edoc")==false){
			hsql+=" and a.type=2";
		}
		hsql+=" order by a.lastUpdate asc";
		Object[] values = new Object[]{domainId, EdocForm.C_iStatus_Deleted, type};
		return super.find(hsql, values);		
	}
	
	public List<EdocForm> getAllEdocFormsByStatus(Long domainId,int status)
	{
		String hsql = "select a from EdocForm as a left join a.edocFormExtendInfo info where info.accountId = ? and info.status=? order by a.type asc , a.lastUpdate asc";
		return super.find(hsql, domainId,status);
	}
	
	public List<EdocForm> getAllEdocFormsByTypeAndStatus(Long domainId,int type, int status)
	{
		String hsql = "select a from EdocForm as a left join a.edocFormExtendInfo info where info.accountId = ? and info.status=? and a.type=? order by a.lastUpdate asc";
		Object[] values = new Object[]{domainId, status, type};
		return super.find(hsql, values);
	}
	
	/**
	 * 查可用公文单
	 * @param domainId
	 * @param domainIds
	 * @param type
	 * @return
	 */
//	public List<EdocForm> getEdocForms(Long domainId,String domainIds, int type)
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append(" select ef ");
//		sb.append(" from EdocForm ef inner join ef.edocFormExtendInfo s  left join ef.edocFormAcls acl  ");
//		sb.append(" where ");
//		sb.append(" acl.entityType = :entityType and  (acl.domainId in (:domainIds) or ef.domainId = :domainId)  and s.status = :status ");
//		sb.append(" and s.accountId = :domainId and ef.type = :type");
//		sb.append(" order by ef.lastUpdate asc");
//		
//		Map parameter = new HashMap();
//		parameter.put("entityType",V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
//		List<Long> idList = null;
//		if(domainIds != null){
//			idList = new ArrayList<Long>();
//			String[] tmps = domainIds.split(",");
//			for(String id:tmps)
//				idList.add(Long.valueOf(id));
//		}
//		parameter.put("domainIds",idList);
//		parameter.put("domainId",domainId);
//		parameter.put("status", EdocForm.C_iStatus_Published);
//		parameter.put("type", type);
//		
//		List<EdocForm> list =  super.find(sb.toString(),-1,-1,parameter);
//		return EdocUtil.convertExtendInfo2EdocForm(list,domainId);
//	}
	
	public List<EdocForm> getEdocFormByAcl(String domainIds){
		StringBuilder sb = new StringBuilder();
		sb.append(" select ef ");
		sb.append(" from EdocForm ef inner join ef.edocFormAcls acl  ");
		sb.append(" where ");
		sb.append(" acl.domainId in (:domainIds)");
		
		Map parameter = new HashMap();
		List<Long> idList = null;
		if(domainIds != null){
			idList = new ArrayList<Long>();
			String[] tmps = domainIds.split(",");
			for(String id:tmps)
				idList.add(Long.valueOf(id));
		}
		parameter.put("domainIds",idList);
		return  super.find(sb.toString(),-1,-1,parameter);
	}
	

	
	public List<EdocForm> getEdocForms(String formIds)
	{		
		Map<String,Object> namedParameter = new HashMap<String,Object>();
		List<Long> ids = new ArrayList<Long>();
		String[] tmp = formIds.split(",");
		for(String id:tmp)
			ids.add(Long.valueOf(id));
		namedParameter.put("ids", ids);
		String hsql = "from EdocForm as a where a.id in (:ids)";		
		return super.find(hsql,namedParameter);
	}
	
	public EdocForm findDefaultFormByDomainIdAndType(Long domainId,int type){
		String hsql="from EdocForm as edocForm where edocForm.domainId = ? and edocForm.type = ? and edocForm.isDefault = ? order by edocForm.lastUpdate asc";		
		Object [] values={domainId,type,true};
		List<EdocForm> list = super.find(hsql, values);
		if(null!=list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public void updateDefaultEdocForm(Long domainId,int type){
		String hsql="update EdocForm as edocForm set edocForm.isDefault = ? where edocForm.domainId = ? and edocForm.type = ? and edocForm.isDefault = ? ";
		super.bulkUpdate(hsql,null,false,domainId,type,true);
	}

	public boolean isReferenced(Long formId){
		String hql = "from EdocSummary as es where es.formId = ?";
		Long[] values = {formId};
		Type[] types = {Hibernate.LONG};
		int count = super.getQueryCount(hql, values, types);
		
		if(count>0){
			return true;
		}	
		return false;
	}
	public boolean isExsit(Long formId){
		String hql = "from EdocForm as ef where ef.id = ?";
		Long[] values = {formId};
		Type[] types = {Hibernate.LONG};
		int count = super.getQueryCount(hql, values, types);
		
		if(count>0){
			return true;
		}	
		return false;
	}
}