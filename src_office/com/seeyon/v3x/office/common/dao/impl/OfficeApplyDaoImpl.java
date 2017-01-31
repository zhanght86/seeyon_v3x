package com.seeyon.v3x.office.common.dao.impl;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.common.dao.OfficeApplyDao;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.domain.OfficeLossInfo;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class OfficeApplyDaoImpl extends BaseHibernateDao implements OfficeApplyDao {
	
	
	/**
	 * 创建申请单
	 * @param officeApply  申请单对象
	 * @return  生成的申请单对象主键ApplyId值
	 */
	public Long createOfficeApply(OfficeApply officeApply){
		officeApply.setApplyId(this.getMaxApplyNo());
		super.save(officeApply);
		return officeApply.getApplyId();
		
		//return (Long)this.getHibernateTemplate().save(officeApply);
	}
	
	/**
	 * 根据申请单ID取得申请单对象
	 * @param applyId  申请单对象
	 * @return  申请单对象
	 */
	public OfficeApply getOfficeApply(Long applyId){
		return (OfficeApply)this.getHibernateTemplate().get(OfficeApply.class, applyId);
	}
	
	
	/**
	 * 更新申请单情报
	 * @param officeApply  申请单对象
	 */
	public void updateOfficeApply(OfficeApply officeApply){
		super.update(officeApply);
		//this.getHibernateTemplate().update(officeApply);
	}
	
	/**
	 * 根据申请号ID集批量删除
	 * @param applyIds	申请号ID集
	 * @return  操作记录数
	 */
	public int deleteOfficeApplyByIds(final String applyIds) {
		String[] strids = applyIds.split(",");
		Long[] ids = new Long[strids.length];
		for(int i =0;i< strids.length;i++){
			ids[i] = Long.parseLong(strids[i]);
		}
		String strSql = "update OfficeApply apply set apply.deleteFlag=:deleteFlag where apply.applyId in (:applyId)  ";
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("applyId", ids);
		parameter.put("deleteFlag", 1);
		return super.bulkUpdate(strSql, parameter);
		/*
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				String strSql = "update OfficeApply apply set apply.deleteFlag=1 where apply.applyId in ("+applyIds+") where apply.applyState<>1";
				return session.createQuery(strSql)
							       .executeUpdate();
			}
		});	*/
	}
    
    /**
     * 根据申请号ID单个删除
     * @param applyIds  申请号ID集
     * @return  操作记录数
     */
    public int deleteOfficeApplyById(final String applyId) {
    	 String strSql = "update OfficeApply apply set apply.deleteFlag=1 where apply.applyId =? ";
    	 return super.bulkUpdate(strSql, null, Long.parseLong(applyId));
        /*return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                
                String strSql = "update OfficeApply apply set apply.deleteFlag=1 where apply.applyId ="+applyId;
                return session.createQuery(strSql).executeUpdate();
            }
        }); */
    }
	/**
	 * 新建丢失报损信息
	 * @param lossInfo　　丢失报损信息对象
	 * @return　　编号
	 */
	public void createOfficeLoss(OfficeLossInfo lossInfo){
		super.save(lossInfo);
		
		//return (Long)this.getHibernateTemplate().save(lossInfo);
	}
	
	/**
	 * 修改丢失报损信息
	 * @param lossInfo　丢失报损信息对象
	 */
	public void updateOfficeLoss(OfficeLossInfo lossInfo){
		super.update(lossInfo);
		//this.getHibernateTemplate().update(lossInfo);
	}
	
	/**
	 * 根据编号取得丢失报损信息情报
	 * @param lossId	编号
	 * @return　　丢失报损信息对象
	 */
	public OfficeLossInfo findOfficeLossById(Long lossId){
		
		return (OfficeLossInfo)this.getHibernateTemplate().get(OfficeLossInfo.class, lossId);
	}
	
	
	/**
	 * 取得管理者的所有丢失报损信息一览列表
	 * @param fieldName  字段名
	 * @param fieldValue	字段值
	 * @param lossManager	管理者
	 * @return 丢失报损信息一览列表
	 */
	public List findOfficeLossList(final String fieldName, final String fieldValue, final Long lossManager){
		Map<String,Object> map = new HashMap<String,Object>();
		StringBuffer sb = new StringBuffer("select m,o ");
		sb.append(" from m_loss_info m,v3x_org_member o ");
		sb.append(" where m.del_flag=0");
		sb.append(" and m.create_user = o.id");
		sb.append(" and m.loss_mge= :lossManager ");
		map.put("lossManager", lossManager);
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			
			sb.append(" and ("+fieldName +" like ? )");
			map.put(fieldName, "%"+SQLWildcardUtil.escape(fieldValue)+"%");
		}
		sb.append(" order by m.loss_id desc");
		return super.find(sb.toString(), map);
		/*
		
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String fieldValue2 = fieldValue.trim();
				StringBuffer sb = new StringBuffer();
				sb.append(" from m_loss_info m,v3x_org_member o ");
				sb.append(" where m.del_flag=0");
				sb.append(" and m.create_user = o.id");
				sb.append(" and m.loss_mge="+lossManager);
				
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					sb.append(" and ("+fieldName +" like "+"'%"+fieldValue2+"%')");
				}
				
				String countSql = "select count(loss_id) as myTotalCount"+sb.toString();
//				if(!Pagination.isNeedCount().booleanValue()){
					int size = getCount(countSql,null,null);
					Pagination.setRowCount(size);
//				}
				
				String sql ="select m,o "+sb.toString();
				
				
				sql+=" order by m.loss_id desc";
				
				SQLQuery query = session.createSQLQuery(sql);
				
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				
				query.addEntity(OfficeLossInfo.class);
				query.addEntity(V3xOrgMember.class);		
				//return this.paginate(query);
				return query.list();
			}
		});	*/
	}

	/**
	 * 取得管理员负责的丢失报损一览列表
	 * @param fieldName
	 * @param fieldValue
	 * @param lossManager
	 * @return  丢失报损一览列表
	 */
	public List findLossOfManager(final String fieldName,final String fieldValue,final Long lossManager){
		Map<String,Object> map = new HashMap<String,Object>();
		StringBuffer sb = new StringBuffer("select m,o ");
		sb.append(" from m_loss_info m,v3x_org_member o ");
		sb.append(" where m.create_user = o.id");
		sb.append(" and m.create_user = o.id");
		sb.append(" and m.loss_mge= :lossManager ");
		sb.append(" and m.del_flag=:delFlag ");
		map.put("lossManager", lossManager);
		map.put("delFlag", 0);
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			sb.append(" and ("+fieldName +" like ? )");
			map.put(fieldName, "%"+SQLWildcardUtil.escape(fieldValue)+"%");
		}
		sb.append(" order by m.loss_id desc");
		return super.find(sb.toString(), map);
		
		/*return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Map<String,Object> map = new HashMap<String,Object>();
				
				String fieldValue2 = fieldValue.trim();
				
				StringBuffer sb = new StringBuffer();
				sb.append(" from m_loss_info m,v3x_org_member o ");
				sb.append(" where m.create_user = o.id");
				sb.append(" and m.create_user = o.id");
				sb.append(" and m.loss_mge="+lossManager);
				sb.append(" and m.del_flag=0");
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					sb.append(" and ("+fieldName+" like :"+fieldName);
					map.put(fieldName, "%"+SQLWildcardUtil.escape(fieldValue)+"%");
				}
				
				String countSql ="select count(*) as myTotalCount" + sb.toString();
				
				//				if(!Pagination.isNeedCount().booleanValue()){
					int size = getCount(countSql,null,null);
					Pagination.setRowCount(size);
//				}
				
				String sql = "select m.*,o.* "+sb.toString() +" order by m.loss_id desc";
				SQLQuery query = session.createSQLQuery(sql);
				
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				
				query.addEntity(OfficeLossInfo.class);
				query.addEntity(V3xOrgMember.class);
				
				
				//return this.paginate(query);
				return query.list();
			}
		});	*/
	}
	
	/**
	 * 批量修改丢失报损的删除状态为1
	 * @param stockIds 丢失编号集 格式：1000,10002,1004
	 * @return  int 数据库修改结果
	 */
	public int deleteOfficeLossbyIds(final String lossIds) {
		
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String strSql = "update OfficeLossInfo loss set loss.deleteFlag=1 where loss.lossId in (:ids)";
				/*
				Map<String, Object> namedPars = new HashMap<String, Object>();
				namedPars.put("ids", Constants.parseStrings2Longs(lossIds, ","));
				*/
				//super.(strSql, namedPars);
		    return session.createQuery(strSql).setParameterList("ids",  Constants.parseStrings2Longs(lossIds, ","))
							       .executeUpdate();
			}
		});	
	}

	public boolean checkAdminDepart(User user){
		
		boolean hasRight = false;
		String countSql = " select *  from m_admin_dep "
						+ " where admin= ? "
						+ " and del_flag=0 "
						+ " and mngdep_id like ? " ;
		List<Object> list = new ArrayList<Object>(); 
		list.add(user.getId()) ;
		list.add("%" +user.getDepartmentId() + "%") ;
		//super.find(countSql,-1,-1, null,list) ;
		
		if(super.find(countSql,-1,-1, null,list).size()>0){
			hasRight = true;
		}
		
		return hasRight;
	}
	/**
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return ModelAndView 
     */
	public List getModelManagers(final int modelId,final User user){
		String likeStr = "";
		switch(modelId){
			case 1:{
				likeStr = "1____";break;
			}
			case 2:{
				likeStr = "_1___";break;
			}
			case 3:{
				likeStr = "__1__";break;
			}
			case 4:{
				likeStr = "___1_";break;
			}
//	      =============================CaoFei 2008 - 9 - 17 Meeting Management add metting validate========================================
			case 5:{
				likeStr = "____1";break;
			}
		}
		String sql = "select m from  MAdminSetting s,V3xOrgMember m where s.id.admin=m.id "
//			+ " and s.domainId=? "
			+ " and s.delFlag=? and s.adminModel like ? ";
		List<Object> list = new ArrayList<Object>();
//		list.add(user.getLoginAccount());
		list.add(0);
		list.add(likeStr);
		return super.find(sql,-1,-1, null,list);
		/*
		
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String likeStr = "";
				switch(modelId){
					case 1:{
						likeStr = "1____";break;
					}
					case 2:{
						likeStr = "_1___";break;
					}
					case 3:{
						likeStr = "__1__";break;
					}
					case 4:{
						likeStr = "___1_";break;
					}
//			      =============================CaoFei 2008 - 9 - 17 Meeting Management add metting validate========================================
					case 5:{
						likeStr = "____1";break;
					}
				}
				
				String sql = "select m.* from m_admin_setting s,v3x_org_member m where s.admin=m.id and "
					+ " s.domain_id="+user.getLoginAccount()
					+ " and s.del_flag=0 and s.admin_model like '"+likeStr+"'";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(V3xOrgMember.class);
				return query.list();
			}
		});	*/
	}
	/**
     * 批量去修改 删除状态为1
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return ModelAndView 
     */
	public List getUserModelManagers(final int modelId,final User user)
	{		
		String likeStr = "";
		switch(modelId){
			case 1:{
				likeStr = "1____";break;
			}
			case 2:{
				likeStr = "_1___";break;
			}
			case 3:{
				likeStr = "__1__";break;
			}
			case 4:{
				likeStr = "___1_";break;
			}
//		      =============================CaoFei 2008 - 9 - 17 Meeting Management add metting validate========================================
			case 5:{
				likeStr = "____1";break;
			}
			
		}
		
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List<String> dep = new ArrayList<String>();
		//String depIds="'"+user.getDepartmentId()+"'";
		try{
			List <Long>ids=orgManager.getUserDomainIDs(user.getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
			//depIds="";
			for(Long id:ids)
			{
				//depIds+="'"+id+"',";		
				dep.add(id.toString());
			}
			
			//添加外部人员在工作范围内的对综合办公申请的权限
			List<V3xOrgDepartment> fDeps = orgManager.getAllParentDepartments(user.getDepartmentId());
			for (V3xOrgDepartment v : fDeps) {		
				dep.add(v.getId().toString());	
			}
			//不仅仅是登录单位  应该是所在的所有单位
//			dep.add(user.getLoginAccount()+"");//添加单位管理权
			//depIds=depIds.substring(0,depIds.length()-1);
		}catch(Exception e){}
		String sql = "select m from MAdminSetting s,V3xOrgMember m where s.id.admin=m.id  "
			//+ " and s.domainId="+user.getLoginAccount()
			//+ " and s.mngdep_id='"+user.getDepartmentId()+"'"
			+ " and s.id.mngdepId in (:depIds)"
			+ " and s.delFlag=:delFlag and s.adminModel like :likeStr ";
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("depIds", dep);
		map.put("delFlag", 0);
		map.put("likeStr", likeStr);
		return super.find(sql, -1, -1, map, new ArrayList());
	}
	/**
     * 批量去修改 删除状态为1
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return ModelAndView 
     */
	public int checkAdminModel(final int modelId,final User user){
		String likeStr = "";
		switch(modelId){
			case 1:{
				likeStr = "1____";break;
			}
			case 2:{
				likeStr = "_1___";break;
			}
			case 3:{
				likeStr = "__1__";break;
			}
			case 4:{
				likeStr = "___1_";break;
			}
//		      =============================CaoFei 2008 - 9 - 17 Meeting Management add metting validate========================================
			case 5:{
				likeStr = "____1";break;
			}
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MAdminSetting.class);
		criteria.add(Restrictions.eq("id.admin",user.getId()));
		criteria.add(Restrictions.eq("delFlag",0));
		//管理员可以管理其他单位的权限
//		criteria.add(Restrictions.eq("domainId",user.getLoginAccount()));
		criteria.add(Restrictions.like("adminModel",likeStr));
		int count = super.getCountByCriteria(criteria);
		return count <=0 ? 1 : 0;
		/*return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
		int iResult = 0;
		String likeStr = "";
		switch(modelId){
			case 1:{
				likeStr = "1____";break;
			}
			case 2:{
				likeStr = "_1___";break;
			}
			case 3:{
				likeStr = "__1__";break;
			}
			case 4:{
				likeStr = "___1_";break;
			}
//		      =============================CaoFei 2008 - 9 - 17 Meeting Management add metting validate========================================
			case 5:{
				likeStr = "____1";break;
			}
		}
		String sql = "select * from m_admin_setting where"
						+ " admin=? "
						+ " and del_flag=? and domain_id=? and admin_model like ? ";
		
		//System.out.println(sql);
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(1, user.getId());
		query.setInteger(2, 0);
		query.setLong(3, user.getLoginAccount());
		query.setString(4, likeStr);
		query.addEntity(MAdminSetting.class);
		
		List list =query.list();
		if(list==null || list.size()<=0){
			iResult = 1;
		}else{
			iResult =0;

//			MAdminSetting adminSetting = (MAdminSetting)list.get(0);
//			String adminModel = adminSetting.getAdminModel();
//			
//			if(adminModel ==null)adminModel ="";
//			
//			if(adminModel.length()<modelId){
//				iResult=2;
//				
//			}else{
//				if(adminModel.substring(modelId-1,modelId).equals("1")){
//					
//					iResult = 0;
//				
//				}else{
//					iResult =2;
//				}
//			}
			//System.out.println(adminModel +":"+iResult);
		}
		//System.out.println("iResult:"+iResult);
		return iResult;
			}
		});	*/
	}
	
	public boolean checkAdminModel(final User user){
		
		return (Boolean)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
		String sql = "select * from m_admin_setting where"
						+ " admin=? "
						+ " and del_flag=? and domain_id=? ";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(1, user.getId());
		query.setInteger(2, 0);
		query.setLong(3, user.getLoginAccount());
		query.addEntity(MAdminSetting.class);
		
		List list =query.list();
		
		return list.size() > 0 ? true : false;
		
			}
		});	
	}
	
	/**
	 * 计算总行数
	 * @param sql
	 * @return
	 */
	public int getCount(final String sql,Object[] values,Type[] types){
		return super.getQueryCount(sql, values, types);
		/*return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				Integer totalCount = (Integer)query.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
				return totalCount.intValue();
			}
		});	*/
	}
	
	public Long getMaxApplyNo(){
		return (Long)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select max(apply_id) as maxNo from t_applylist";
				SQLQuery query = session.createSQLQuery(sql);
				Long maxNo = (Long)query.addScalar("maxNo", Hibernate.LONG).uniqueResult();
				if(maxNo==null){
					maxNo =new Long(10000);
				}
				if(maxNo.longValue()<10000){
					maxNo = new Long(10000);
				}
				maxNo= Long.valueOf(maxNo.longValue()+1);
				return maxNo;
			}
		});	
	}
	/**
	 * 取得Query对象
	 * @param sql
	 * @return
	 */
	public SQLQuery createQuery(String sql){
		return this.getSession().createSQLQuery(sql);
	}
	
	public Session getCurSession(){
		return this.getSession();
	}
	
	public List getTableRecords(final String sql,final Class clazz){
		
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				SQLQuery query = session.createSQLQuery(sql);
				
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				
				query.addEntity(clazz);
				return query.list();
				
			}
		});	
	}
}
