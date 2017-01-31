/**
 * 
 */
package com.seeyon.v3x.blog.dao;


import java.util.List;
import com.seeyon.v3x.blog.domain.BlogAttention;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * 类描述：博客关注
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class BlogAttentionDao extends BaseHibernateDao<BlogAttention>{

	//添加博客关注
	public void BlogAttentionDao(List<BlogAttention> list) throws Exception {
		for (BlogAttention BlogAttention : list) {
			this.save(BlogAttention);
		}
	}
	/**
	 * 查询关注信息(需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogAttention> listAttention() throws Exception {
		//增加人员过滤		
		User user = CurrentUser.get();	
		String hsql = "from BlogAttention as attention where attention.employeeId=? order by attention.attentionId";	    
	    return super.find(hsql, user.getId());
	}
	
	/**
	 * 查询关注信息(不需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogAttention> listAttention2() throws Exception {		
		User user = CurrentUser.get();	
		String hsql = "from BlogAttention as attention where attention.employeeId=? order by attention.attentionId";	    
		return super.find(hsql, user.getId());
	}

	/**
	 * 新增关注信息
	 * 
	 * @param BlogAttention
	 *            关注信息
	 * @throws Exception
	 */
	public void createAttention(BlogAttention blogAttention) throws Exception {
		this.save(blogAttention);
	}

	/**
	 * 变更关注信息
	 * 
	 * @param BlogAttention
	 *            关注信息
	 * @throws Exception
	 */
	public void modifyAttention(BlogAttention BlogAttention) throws Exception {
		this.update(BlogAttention);
	}

	/**
	 * 删除关注信息
	 * 
	 * @param blogFamilyId
	 *           关注编号
	 * @throws Exception
	 */
	public void deleteAttention(Long blogAttentionId) throws Exception {
//		this.removeById(blogAttentionId);
		this.bulkUpdate("delete from BlogAttention where id=?", null, blogAttentionId);
	}
	
	//根据ID获取关注
	public BlogAttention getSingleAttention(Long id) {
		return this.get(id);
	}
	/**
	 * 查询某员工ID是否存在
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeAttention(Long employeeId) throws Exception{
		Integer ct = null;
		User user = CurrentUser.get();
		
		String hsql = "select count(*) from BlogAttention as attention where attention.attentionId=? and attention.employeeId=?";
		Object[] values = {employeeId, user.getId()};
		List list = super.find(hsql, values);
		if (list != null && !list.isEmpty()) {
			ct = (Integer)list.get(0);
		}
		return ct;
	
	}
	
}
