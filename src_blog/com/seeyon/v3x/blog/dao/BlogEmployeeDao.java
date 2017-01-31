/**
 * 
 */
package com.seeyon.v3x.blog.dao;

import java.util.List;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：博客员工
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class BlogEmployeeDao extends BaseHibernateDao<BlogEmployee>{

	//添加博客员工
	public void BlogEmployeeDao(List<BlogEmployee> list) throws Exception {
		for (BlogEmployee BlogEmployee : list) {
			this.save(BlogEmployee);
		}
	}
	/**
	 * 查询员工信息(需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogEmployee> listEmployee() throws Exception {
		String hsql = "from BlogEmployee as employee order by employee.id";
		return super.find(hsql);
	}
	
	/**
	 * 查询员工信息(不需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogEmployee> listEmployee2() throws Exception {		
		String hsql = "from BlogEmployee as employee order by employee.id";
		return super.find(hsql);
	}
	/**
	 * 新增员工信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void createEmployee(BlogEmployee blogEmployee) throws Exception {
		this.save(blogEmployee);
	}
	/**
	 * 变更员工
	 * 
	 * @param blogFamily
	 *            员工信息
	 * @throws Exception
	 */
	public void modifyEmployee(BlogEmployee blogEmployee) throws Exception {
		this.update(blogEmployee);
	}

	/**
	 * 删除员工信息
	 * 
	 * @param blogFamilyId
	 *            员工编号
	 * @throws Exception
	 */
	public void deleteEmployee(Long blogEmployeeId) throws Exception {
		this.removeById(blogEmployeeId);
	}
	
	//根据ID获取员工
	public BlogEmployee getSingleEmployee(Long id){		
		return this.get(id);
	}
	public BlogEmployee getEmployeeById(Long employeeId) throws Exception{
		return this.get(employeeId);
	}
	/**
	 * 查询某员工ID是否存在
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeId(Long employeeId) throws Exception{
		Integer count = null;		
		String hsql = "select count(*) from BlogEmployee as employee where employee.id=?";
		List list = super.find(hsql, employeeId);
		if (list != null && !list.isEmpty()) {
			count = (Integer)list.get(0);
		}
		return count;
	
	}
	/**
	 * 方法描述：创建文章，该员工的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateArticleNumber(Long employeeId, int step)throws Exception{
		BlogEmployee employee = this.get(employeeId);
		employee.setArticleNumber(employee.getArticleNumber() + step);
		this.update(employee);
	}
	
}
