/**
 * 
 */
package com.seeyon.v3x.project.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.domain.ProjectType;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author lin tian 2007-5-16
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-23
 *
 */
public class ProjectDao extends BaseHibernateDao<ProjectSummary> {
	
	/**
	 * 获取当前用户在首页栏目中的项目信息
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getIndexProjectList(long memberid, int maxResult, List<Byte> memberTypeList, List<Long> projectTypeList) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct ps, pm.userProjectSort from " + ProjectSummary.class.getName() + " ps, " + ProjectMember.class.getName() + " pm ");
		hql.append(" where ps.id = pm.projectSummary.id and pm.memberid=:memberid and ps.projectState<:projectState ");
		params.put("memberid", memberid);
		params.put("projectState", ProjectSummary.state_close);

		if (memberTypeList != null) {
			hql.append(" and pm.memberType in(:memberTypeList) ");
			params.put("memberTypeList", memberTypeList);
		}
		
		if (projectTypeList != null) {
			hql.append(" and ps.projectTypeId in(:projectTypeList) ");
			params.put("projectTypeList", projectTypeList);
		}

		hql.append("order by pm.userProjectSort, ps.begintime desc");
		
		int begin = -1;
		int end = -1;
		if (maxResult > 0) {
			begin = 0;
			end = maxResult;
		}

		List tempList = super.find(hql.toString(), begin, end, params);
		List<ProjectSummary> resultList = new ArrayList();
		for (int i = 0; i < tempList.size(); i++) {
			Object[] object = (Object[]) tempList.get(i);
			resultList.add((ProjectSummary) object[0]);
		}
		return resultList;
	}

	/**
	 * 获取当前用户的项目信息列表
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getProjectList( final long memberid) throws Exception {
		return (List<ProjectSummary>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "SELECT DISTINCT ps FROM " + ProjectSummary.class.getName() + " ps, " + ProjectMember.class.getName() + " pm " 
						+ " WHERE ps.id = pm.projectSummary.id AND pm.memberid=:memberid " 
						+ " AND ( ps.projectState < " + ProjectSummary.state_close + " ) " 
						+ " AND (pm.memberType =" + ProjectMember.memberType_manager
						+ " OR pm.memberType =" + ProjectMember.memberType_charge
						+ " OR pm.memberType =" + ProjectMember.memberType_member
						+ " OR pm.memberType =" + ProjectMember.memberType_interfix
						+ " OR pm.memberType =" + ProjectMember.memberType_create
						+ " OR pm.memberType =" + ProjectMember.memberType_assistant
						+ " ) ORDER BY ps.begintime DESC";
				Query query = session.createQuery(hql).setLong("memberid", memberid);
				return query.list();
			}
		});
	}

	/**
	 * @deprecated
	 * 单位管理员获取项目列表
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getProjectListByAdmin()
			throws Exception {
		return (List<ProjectSummary>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "FROM " + ProjectSummary.class.getName() + " ps "
				+ " WHERE ps.projectState < " + ProjectSummary.state_close
//				+ " OR ps.projectState > " + ProjectSummary.state_delete
				+ " ORDER BY ps.begintime DESC";
		Query query = session.createQuery(hql);
		return query.list();
			}
		});
	}
	
	/**
	 * 项目发起人读取本单位的所有项目（包括关闭的的项目）。
	 * @param domainId 单位id
	 * @return List
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getProjectsByAdmin(long domainId, String condition, String textfield, String textfield1) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct ps from ProjectSummary ps where ps.projectState <> " + ProjectSummary.state_delete);
		
		boolean isValid = this.setHqlAndParams(hql, params, condition, textfield, textfield1, true);
		
		if(!isValid){
			return new ArrayList<ProjectSummary>();
		}
		
		hql.append(" and ps.domainId=:domainId order by ps.begintime desc");
		params.put("domainId", domainId);
		return super.find(hql.toString(), -1, -1, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getProjectsOfTypeByAdmin(long domainId, Long projectTypeId, String condition, String textfield, String textfield1) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct ps from ProjectSummary ps where ps.projectTypeId = :projectTypeId and ps.projectState <> " + ProjectSummary.state_delete);
		params.put("projectTypeId", projectTypeId);
		
		boolean isValid = this.setHqlAndParams(hql, params, condition, textfield, textfield1, true);
		if(!isValid){
			return new ArrayList<ProjectSummary>();
		}
		
		hql.append(" and ps.domainId=:domainId order by ps.begintime desc");
		params.put("domainId", domainId);
		return find(hql.toString(), "ps.id", true, params);
	}
	
	/**
	 * 读取本单位所有活动的项目（单位管理员、表单管理员等调用）
	 * @param domainId 单位id
	 * @return List
	 * @throws Exception
	 */
	public List<ProjectSummary> getProjects(long domainId) throws Exception {
		String hsql = "from ProjectSummary  project where project.domainId=? and project.projectState < "+ProjectSummary.state_close+" order by project.begintime desc";
		Object[] values = {domainId};
		return super.find(hsql, values);
	}
	
	public List<ProjectSummary> getAllProjectList(long memberid) throws Exception {
		return this.getAllProjectList(memberid, false, true);
	}

	/**
	 * 获取当前用户所有项目信息
	 * @param memberid	用户ID
	 * @param filter	状态过滤：是否只取未结束的有效项目
	 * @param pagination	是否分页
	 */
	public List<ProjectSummary> getAllProjectList(long memberid, boolean filter, boolean pagination) throws Exception {
		return this.getAllProjectList(memberid, -1l, filter, pagination);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getAllProjectList(long memberid, long accountId, boolean filter, boolean pagination) throws Exception {
		String stateSql = filter ? (" AND ps.projectState < " + ProjectSummary.state_close) : 
								   (" AND ps.projectState <> " + ProjectSummary.state_delete);
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "SELECT DISTINCT ps, pm.userProjectSort FROM "
				+ ProjectSummary.class.getName() + " ps," + ProjectMember.class.getName() + " pm"
				+ " WHERE ps.id = pm.projectSummary.id " + stateSql
				+ " AND pm.memberid=:memberid ";
		params.put("memberid", memberid);
		
		if(accountId != -1l) {
			hql += " AND ps.domainId = :accountId ";
			params.put("accountId", accountId);
		}
		hql += " ORDER BY pm.userProjectSort, ps.begintime DESC";
		
		List<Object[]> tempList = pagination ? this.find(hql, "ps.id", true, params) : this.find(hql, -1, -1, params);
		List<ProjectSummary> retList = new ArrayList<ProjectSummary>();
		if(CollectionUtils.isNotEmpty(tempList)) {
			for(Object[] arr : tempList) {
				retList.add((ProjectSummary)arr[0]);
			}
		}
		return retList;
	}
	
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1) throws Exception {
		return this.getAllUserProjectList(memberid, condition, textfield, textfield1, null);
	}
	
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList) throws Exception {
	    return getAllUserProjectList(memberid, condition, textfield, textfield1, memberTypeList,null);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList,Map<String,Object> cndMap) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT DISTINCT ps, pm.userProjectSort FROM " + ProjectSummary.class.getName() + " ps, " + ProjectMember.class.getName() + " pm " 
			+ " WHERE ps.id=pm.projectSummary.id " + " AND ps.projectState <> " + ProjectSummary.state_delete 
			+ " AND (pm.memberid=:memberid) ");
		params.put("memberid", memberid);
		
		boolean isValid = this.setHqlAndParams(hql, params, condition, textfield, textfield1, false);
		
        if (cndMap != null && cndMap.get("projectState") != null) {//未循环迭代，暂时不用
            isValid = this.setHqlAndParams(hql, params, cndMap.get("projectState").toString(),"2", null, false);
        }
		
		if(!isValid){
			return new ArrayList<ProjectSummary>();
		}
		
		if (memberTypeList != null) {
			hql.append(" and pm.memberType in(:memberTypeList) ");
			params.put("memberTypeList", memberTypeList);
		}
		
		hql.append(" ORDER BY pm.userProjectSort, ps.begintime DESC");
		
		List tempList = super.find(hql.toString(), "ps" , true, params);
		
		List<ProjectSummary> retList = new ArrayList();
		if(tempList == null){
			return retList;
		}
		for(int i=0;i<tempList.size();i++){
			Object[] object = (Object[])tempList.get(i);
			retList.add((ProjectSummary)object[0]);
		}
		return retList ;
	}

	
	/**
	 * 根据条件获取当前用户所有项目信息
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getAllProjectListByCondition( final long memberid,final String condition, final String field,final String field1)
			throws Exception {
		return (List<ProjectSummary>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List<ProjectSummary> retList=new ArrayList();
				if(condition.equals("projectDate")){
					Timestamp beginTime=null;
					Timestamp endTime=null;
					if(field!=null&&!field.trim().equals("")){
						beginTime=new Timestamp(Datetimes.parseDatetime(field).getTime());
					}
					if(field1!=null&&!field1.trim().equals("")){
		        		endTime=new Timestamp(Datetimes.parseDatetime(field1).getTime());
		        		endTime.setTime(endTime.getTime() + 24 * 60 * 60 * 1000);
					}
					String count = "SELECT  Count(DISTINCT ps) FROM "
						+ ProjectSummary.class.getName() + " ps,"
						+ ProjectMember.class.getName() + " pm"
						+ " WHERE ps.id = pm.projectSummary.id "
						+ " AND ps.projectState <> " + ProjectSummary.state_delete
						+ " AND ( pm.memberid=:memberid or ps.projectCreator=:createrId)";
						if(!field.equals("")&&!field1.equals(""))
							count+= " AND ( ps.begintime >= :start AND ps.closetime <= :end)";
				Query queryCount = session.createQuery(count).setLong(
						"memberid", memberid).setLong("createrId", memberid);
				if(!field.equals("")&&!field1.equals("")){
				queryCount.setTimestamp("start", beginTime);
				queryCount.setTimestamp("end", endTime);
				}
				int projectCount = ((Integer) queryCount.uniqueResult()).intValue();
				Pagination.setRowCount(projectCount);

				String hql = "SELECT DISTINCT ps,pm.userProjectSort FROM "
						+ ProjectSummary.class.getName() + " ps,"
						+ ProjectMember.class.getName() + " pm"
						+ " WHERE ps.id = pm.projectSummary.id "
						+ " AND ps.projectState <> " + ProjectSummary.state_delete
						+ " AND ( pm.memberid=:memberid )" ;
						if(!field.equals("")&&!field1.equals(""))
							hql+= " AND ( ps.begintime >= :start AND ps.closetime <= :end)";
						hql+="ORDER BY pm.userProjectSort, ps.begintime DESC";
				Query query = session .createQuery(hql).setLong("memberid",
						memberid).setMaxResults(Pagination.getMaxResults())
						.setFirstResult(Pagination.getFirstResult());
				if(!field.equals("")&&!field1.equals("")){
				query.setTimestamp("start", beginTime);
				query.setTimestamp("end", endTime);
				}
				List tempList=query.list();
				int i;
				for(i=0;i<tempList.size();i++)
				{
					Object[] object = (Object[]) tempList.get(i);
					retList.add((ProjectSummary)object[0]);
				}
			}
				else{
					String count = "SELECT  Count(DISTINCT ps) FROM "
						+ ProjectSummary.class.getName() + " ps,"
						+ ProjectMember.class.getName() + " pm"
						+ " WHERE ps.id = pm.projectSummary.id "
						+ " AND ps.projectState <> " + ProjectSummary.state_delete
						+" AND ( ps."+condition+" like :field )"
						+" AND ( pm.memberid=:memberid or ps.projectCreator=:createrId )";
				Query queryCount = session.createQuery(count).setLong(
						"memberid", memberid).setLong("createrId", memberid);
				queryCount.setString("field", "%" + field.replace("'", "''")+"%") ;
				int projectCount = ((Integer) queryCount.uniqueResult()).intValue();
				Pagination.setRowCount(projectCount);
				
				String hql = "SELECT DISTINCT ps,pm.userProjectSort FROM "
						+ ProjectSummary.class.getName() + " ps,"
						+ ProjectMember.class.getName() + " pm"
						+ " WHERE ps.id = pm.projectSummary.id "
						+ " AND ps.projectState <> " + ProjectSummary.state_delete
						+ " AND ( ps."+condition+" like :field )"
					//	+ " AND ( ps."+condition+" like '%"+field.replace("'", "''")+"%' )"
						+ " AND ( pm.memberid=:memberid ) ORDER BY pm.userProjectSort, ps.begintime DESC";
				Query query = session .createQuery(hql).setLong("memberid",
						memberid).setMaxResults(Pagination.getMaxResults())
						.setFirstResult(Pagination.getFirstResult());
				queryCount.setString("field", "%" + field.replace("'", "''")+"%") ;
				List tempList=query.list();
				int i;
				for(i=0;i<tempList.size();i++)
				{
					Object[] object = (Object[]) tempList.get(i);
					retList.add((ProjectSummary)object[0]);
				}
        }
				return retList;
			}
		});
	}
	/**
	 * 获取当前类型下的所有项目信息
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getAllProjectListByProjectTypeName(final long memberid, final String projectTypeId, 
			String condition, String textfield, String textfield1) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT DISTINCT ps FROM " + ProjectSummary.class.getName() + " ps," + ProjectMember.class.getName() + " pm" 
					 + " WHERE ps.id = pm.projectSummary.id " + " AND ps.projectState <> " + ProjectSummary.state_delete 
					 + " AND ps.projectTypeId =:projectTypeId " + " AND (pm.memberid=:memberid or ps.projectCreator=:memberid2) ");
		params.put("projectTypeId", Long.valueOf(projectTypeId));
		params.put("memberid", memberid);
		params.put("memberid2", memberid);
		
		boolean isValid = this.setHqlAndParams(hql, params, condition, textfield, textfield1, false);
		
		if(!isValid){
			return new ArrayList<ProjectSummary>();
		}
		
		hql.append(" ORDER BY ps.begintime DESC");
		return this.find(hql.toString(), "ps.id", true, params);
	}
	/**
	 * 根据id得到项目信息
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public ProjectSummary getProject( final long projectId) throws Exception {
		return (ProjectSummary) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "FROM " + ProjectSummary.class.getName() + " ps"
				+ " WHERE ps.id=:id";
		Query query = session.createQuery(hql).setLong("id",
				projectId);
		return (ProjectSummary) query.uniqueResult();
			}
		});
	}
	
	/**
	 * 根据项目阶段ID获取项目信息
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getProjectByPhase(final Long phaseId) {
		return (List<Object[]>) this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select ps.id, ps.project_name, ps.project_creator from project_summary ps, project_phase ph " +
						     "where ps.id=ph.project_id and ph.id=:phaseId";
				SQLQuery query = session.createSQLQuery(sql);
				query.setLong("phaseId", phaseId);
				return query.list();
			}
		});
	}
	
	/**
	 * 根据项目ID获取项目负责人、项目助理
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getProjectMembersByProject(final Long projectId) {
		return (List<Object>) this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select pm.memberid from project_member pm where pm.project_id=:projectId " +
						"and (pm.member_type=0 or pm.member_type=5)";
				SQLQuery query = session.createSQLQuery(sql);
				query.setLong("projectId", projectId);
				return query.list();
			}
		});
	}

	/**
	 * 获取当前项目的成员列表(成员、管理员)
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getProjectMemberList( final long projectId) throws Exception {
		return (List<Long>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = " SELECT DISTINCT m.memberid FROM "
				+ ProjectMember.class.getName() + " m"
				+ " WHERE m.projectSummary.id=:projectId AND (m.memberType="
				+ ProjectMember.memberType_manager + " OR m.memberType= "
				+ ProjectMember.memberType_member + ")";
		Query query = session.createQuery(hql).setLong("projectId",
				projectId);
		return query.list();
			}
		});
	}

	/**
	 * 获取当前用户创建的项目名称
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getProjectSummaryByUser( final long uid) throws Exception {
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "SELECT p.projectName FROM "
				+ ProjectSummary.class.getName() + " p"
				+ " WHERE p.projectCreator = :uid";
		Query query = session.createQuery(hql).setLong("uid", uid);
		return query.list();
			}
		});
	}

	/**
	 * 标记删除项目
	 * 
	 * @param projectId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void deleteProjectSummary(long projectId) throws Exception {
		Map map = new HashMap();
		map.put("projectState", ProjectSummary.state_delete);
		super.update(projectId, map);
	}

	@SuppressWarnings("unchecked")
	public List<ProjectType> getAllProjectTypes() {
		return this.getHibernateTemplate().loadAll(ProjectType.class);
	}

	//删除项目分类--根据主键ID--可以删除多个
	public void del(final Long id) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete ProjectType");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString()).setLong("ids", id);
			
				return query.executeUpdate();
			}
		});
	}

	//根据ID 查询单个项目分类
	public ProjectType findProjectTypeById(final Long projectId) {
		
		return (ProjectType) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "FROM ProjectType pt" 
				+ " WHERE pt.id=:id";
		Query query = session.createQuery(hql).setLong("id",
				projectId);
		return (ProjectType) query.uniqueResult();
			}
		});
	}

	/**
	 * 根据项目管理员ID来获取项目的信息。
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getManagerProjectList(final Long ids) {		
		return (List<ProjectSummary>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hql = "FROM ProjectSummary ps"
				+ " WHERE ps.projectManager=:ids"
				+ " AND  (ps.projectState < " + ProjectSummary.state_close
				+ " ) ";				
		Query query = session.createQuery(hql).setLong("ids",
				ids);
		return query.list();
			}
		});
	}
	
	public void updateUserProjectSort(Long proId,Long userId,int i)
	{
		try{
			String hql="update ProjectMember pm set userProjectSort=? where pm.projectSummary.id=? and pm.memberid=?";
			int updNum=super.bulkUpdate(hql, null,new Object[]{i,proId,userId});
			/*
			if(updNum<=0)
			{//没有发起人排序数据
				ProjectSummary ps=this.get(proId);
				ProjectMember pm=new ProjectMember();
				pm.setIdIfNew();
				pm.setMemberid(userId);
				pm.setMemberSort(-1);
				pm.setMemberType(ProjectMember.memberType_create);
				pm.setUserProjectSort(i);
				pm.setProjectSummary(ps);
				ps.getProjectMembers().add(pm);
				super.save(ps);
			}
			*/
		}catch(Exception e)
		{
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectType> getProjectTypeByAccountId(long accountId) {
		DetachedCriteria c = DetachedCriteria.forClass(ProjectType.class);
		c.add(Restrictions.eq("accountId", accountId));
		return super.executeCriteria(c, -1, -1);
	}
    
    //根据项目类型名称 查询单个项目
    public List findProjectByProjectTypeName(final String projectType) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Long accountId = CurrentUser.get().getLoginAccount();
        String hql = "FROM ProjectSummary" 
                + " WHERE projectTypeName=?  and domainId=?";
        Query query = session.createQuery(hql)
                             .setString(0, projectType)
                             .setLong(1, accountId);
        return (List) query.list();
      
            }
        });
    }
    /**
     * 批量更新ProjectSummary里相关联的项目类型名称
     * @param projectTypeName
     * @param pt
     */
    public void updateProTypeofProjectSummary(final String projectTypeName,final String pt) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session s) throws HibernateException, SQLException {
                Long accountId = CurrentUser.get().getLoginAccount();
              Query query = s.createQuery("update ProjectSummary  set projectTypeName=? where projectTypeName=? and domainId=?");
              query.setString(0, projectTypeName);
              query.setString(1, pt);
              query.setLong(2, accountId);
              query.executeUpdate();
              return null;
            }
          });
      }
    /**
     * 更新项目类型
     * @param object
     */
    public void  updateProTypeName(Object object){
        getHibernateTemplate().clear();
        super.update(object);
    }
    /**
     * 以条件项目类型名称和单位登录id查找ProjectSummary
     * @param object
     */
    public List  findProjectSummaryUseType(Long projectId){
        final ProjectType ptype= this.findProjectTypeById(projectId);
          return (List) getHibernateTemplate().execute(new HibernateCallback() {
          public Object doInHibernate(Session session) throws HibernateException {
              Long accountId = CurrentUser.get().getLoginAccount();
              String hql = "FROM ProjectSummary  WHERE projectTypeName=? and domainId=?";
              Query query = session.createQuery(hql)
                           .setString(0, ptype.getName())
                           .setLong(1, accountId);
           return (List) query.list();
          }
      });
      
    }
    
    /**
     * @deprecated
     * 检查此人员是否为项目负责人
     * 
     * @param memId
     * @return List
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List checkProjectManager(final Long memId)
            throws Exception {
        return (List) getHibernateTemplate().execute(new HibernateCallback() 
        {
            public Object doInHibernate(Session session) throws HibernateException 
            {
        String hql = "FROM " + ProjectMember.class.getName() + " mem "
                + " WHERE mem.memberid=:memberid and mem.memberType="+ProjectMember.memberType_manager;
        Query query = session.createQuery(hql);
              query.setLong("memberid", memId);
              return query.list();
            }
        });
    }
    
	@SuppressWarnings("unchecked")
	public List<ProjectSummary> getAllProjectListByMemberId( final long memberid)throws Exception {
		
	return (List<ProjectSummary>) getHibernateTemplate().execute(new HibernateCallback() {
		public Object doInHibernate(Session session) throws HibernateException {
			String hql = "SELECT DISTINCT ps FROM "
			+ ProjectSummary.class.getName() + " ps,"
			+ ProjectMember.class.getName() + " pm"
			+ " WHERE ps.id = pm.projectSummary.id "
			+ " AND ps.projectState <> " + ProjectSummary.state_delete
			+ " AND ( pm.memberid=:memberid)";
			Query query = session .createQuery(hql).setLong("memberid",memberid);
	
			return query.list();
				}
			});
		}
	
	/**
	 * 判断用户能否查阅项目内容
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public boolean canUserViewProject(Long projectId, Long userId) {
		String hql = "select count(pm.id) from " + ProjectMember.class.getName() + " as pm where pm.projectSummary.id=? and pm.memberid=?";
		return (Integer)super.findUnique(hql, null, projectId, userId) > 0;
	}
	
	/**
	 * 封装查询hql
	 */
	private boolean setHqlAndParams(StringBuilder hql, Map<String, Object> params, String condition, String textfield, String textfield1, boolean projectMember){
		if(StringUtils.isNotBlank(condition)){
			if("projectName".equals(condition)){
				if(StringUtils.isNotBlank(textfield)){
					hql.append(" AND ps.projectName LIKE :projectName ");
					params.put("projectName", "%" + SQLWildcardUtil.escape(textfield) + "%");
				}
			}else if("projectManager".equals(condition)){
				if(StringUtils.isNotBlank(textfield)){
					if(projectMember){
						hql.setLength(0);
						hql.append("SELECT DISTINCT ps FROM ProjectSummary ps, ProjectMember pm WHERE ps.projectTypeId = :projectTypeId AND ps.id=pm.projectSummary.id "
							+ " AND ps.id in (:ids) " + " AND ps.projectState <> " + ProjectSummary.state_delete);
					}else{
						hql.append(" AND ps.id in (:ids) ");
					}
					
					List<Long> ids = this.getProjectIdsByManagerId(NumberUtils.toLong(textfield));
					// 查询不到有效记录，直接返回
					if(CollectionUtils.isEmpty(ids)){
						return false;
					}
					params.put("ids", ids);
				}
			}else if("projectDate".equals(condition)){
				if(Strings.isNotBlank(textfield)){
					hql.append(" AND ps.begintime>=:begintime ");
					params.put("begintime", Datetimes.getTodayFirstTime(textfield));
				}
				
				if(Strings.isNotBlank(textfield1)){
					hql.append(" AND ps.closetime<=:closetime ");
					params.put("closetime", Datetimes.getTodayLastTime(textfield1));
				}
			}else if("projectState".equals(condition)){
				if(StringUtils.isNotBlank(textfield)){
					hql.append(" AND ps.projectState=:projectState ");
					params.put("projectState", Byte.valueOf(textfield));
				}
			}else if("projectState_lt".equals(condition)){
                if(StringUtils.isNotBlank(textfield)){
                    hql.append(" AND ps.projectState<:projectState ");
                    params.put("projectState", Byte.valueOf(textfield));
                }
            }else if("projectState_ge".equals(condition)){
                if(StringUtils.isNotBlank(textfield)){
                    hql.append(" AND ps.projectState>=:projectState ");
                    params.put("projectState", Byte.valueOf(textfield));
                }
            }else if("projectRole".equals(condition)){
				if(StringUtils.isNotBlank(textfield)){
					hql.append(" AND pm.memberid = "+params.get("memberid"));
					hql.append(" AND pm.memberType=:projectRole ");
					params.put("projectRole", Byte.valueOf(textfield));
				}
			}else if("projectType".endsWith(condition)){
				if(StringUtils.isNotBlank(textfield)){
					hql.append(" AND ps.projectTypeId=:projectTypeId ");
					params.put("projectTypeId", Long.valueOf(textfield));
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据负责人ID查询项目ID集合
	 */
	@SuppressWarnings("unchecked")
	private List<Long> getProjectIdsByManagerId(Long managerId) {
		String hql = "select pm.projectSummary.id from " + ProjectMember.class.getName() + " pm where pm.memberid=? and pm.memberType=?";
		return super.find(hql, -1, -1, null, managerId, ProjectMember.memberType_manager);
	}
	
}
