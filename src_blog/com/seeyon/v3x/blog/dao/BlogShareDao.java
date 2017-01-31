/**
 * 
 */
package com.seeyon.v3x.blog.dao;

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogShare;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
/**
 * 类描述：博客共享
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class BlogShareDao extends BaseHibernateDao<BlogShare>{

	//添加博客共享
	public void BlogShareDao(List<BlogShare> list) throws Exception {
		for (BlogShare BlogShare : list) {
			this.save(BlogShare);
		}
	}
	/**
	 * 查询本部门共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogShare> listDepartmentShare(Long departmentId) throws Exception {
		//增加人员过滤
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();
		
//		Long accountId = user.getAccountId(); V3xOrgMember
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where BlogShare.employeeId=?");//条件
		hql.append(" and BlogShare.shareId in (");
		hql.append(" select V3xOrgMember.id from "+ V3xOrgMember.class.getName() + "  as V3xOrgMember");
		hql.append(" where V3xOrgMember.orgDepartmentId=?");
		hql.append(" )");
		hql.append(" order by BlogShare.shareId");
		
		return super.find(hql.toString(), -1, -1, null, user.getId(), departmentId);		
	}
	/**
	 * 查询共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogShare> listShare() throws Exception {
		//增加人员过滤
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();
		
//		Long accountId = user.getAccountId();
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where " + "BlogShare.employeeId=?");//条件
		hql.append(" order by BlogShare.shareId");
		
		return super.find(hql.toString(), -1, -1, null, user.getId());
	}
	
	/**
	 * 查询共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogShare> listShare2() throws Exception {
		
//		增加单位过滤--lucx  前端讨论板块列表显示
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		//Long accountId = user.getAccountId();
		StringBuffer hql = new StringBuffer();
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where " + "BlogShare.employeeId=?");//条件
		return super.find(hql.toString(), -1, -1, null, currentUserId);
	}
	/**
	 * 查询他人共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogShare> listShareOther() throws Exception {
		//增加人员过滤
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();
		
//		Long accountId = user.getAccountId();
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where BlogShare.employeeId=?");//条件
		hql.append(" order by BlogShare.shareId");
		
		return super.find(hql.toString(), -1, -1, null, user.getId());		
	}
	
	/**
	 * 查询他人共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogShare> listShareOther2() throws Exception {
		
//		增加单位过滤--lucx  前端讨论板块列表显示
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		//Long accountId = user.getAccountId();
		StringBuffer hql = new StringBuffer();
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where " + "BlogShare.employeeId=?");//条件
		
		return super.find(hql.toString(), -1, -1, null, currentUserId);
	}

	/**
	 * 新增共享信息
	 * 
	 * @param BlogShare
	 *            共享信息
	 * @throws Exception
	 */
	public void createShare(BlogShare blogShare) throws Exception {
		this.save(blogShare);
	}

	/**
	 * 变更共享信息
	 * 
	 * @param BlogShare
	 *            共享信息
	 * @throws Exception
	 */
	public void modifyShare(BlogShare BlogShare) throws Exception {
		this.update(BlogShare);
	}

	/**
	 * 删除共享信息
	 * 
	 * @param blogFamilyId
	 *           共享编号
	 * @throws Exception
	 */
	public void deleteShare(Long blogShareId) throws Exception {
		this.removeById(blogShareId);		
	}
	
	//根据ID获取共享
	public BlogShare getSingleShare(Long id){		
		return this.get(id);
	}
	/**
	 * 查询是否已经共享给此员工ID
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeShared(Long employeeId) throws Exception{
		Integer ct = null;
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();
		
		hql.append(" select count(*) ");
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where BlogShare.shareId=? ");
		hql.append(" and BlogShare.employeeId=?");//条件
		
		Object [] obj = new Object[2];
		obj[0] = employeeId;
		obj[1] = user.getId();
		
		ct = (Integer)this.getHibernateTemplate().find(hql.toString(), obj).listIterator().next();
		return ct;
	
	}
	/**
	 * 查询别人是否给我开通共享
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedEmployee(Long employeeId) throws Exception{
		Integer ct = null;
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();		
		hql.append(" select count(*) ");
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where BlogShare.shareId = ?");
		hql.append(" and BlogShare.employeeId =?");//条件
		hql.append(" and BlogShare.type ='Member'");//条件
		
		Object [] obj = new Object[2];
		obj[0] = user.getId();
		obj[1] = employeeId;
		
		ct = (Integer)this.getHibernateTemplate().find(
									hql.toString(), obj).listIterator().next();
		return ct;
	
	}
	/**
	 * 检查此员工是否共享给了我所在的部门
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedDeparment(Long employeeId,Long departmentId) throws Exception{
		Integer ct = null;
		StringBuffer hql = new StringBuffer();
		User user = CurrentUser.get();
		
		hql.append(" select count(*) ");
		hql.append(" from " + BlogShare.class.getName() + " as BlogShare ");
		hql.append(" where BlogShare.shareId = ?");
		hql.append(" and BlogShare.employeeId =?");//条件
		hql.append(" and BlogShare.type ='Department'");//条件
		
		Object [] obj = new Object[2];
		obj[0] = departmentId ;
		obj[1] = employeeId;
		
		ct = (Integer)this.getHibernateTemplate().find(
									hql.toString(), obj).listIterator().next();
		return ct;
	
	}
	
}
