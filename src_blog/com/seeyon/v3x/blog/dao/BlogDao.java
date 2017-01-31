package com.seeyon.v3x.blog.dao;

/**
 * @author xiaoqiuhe
 * date: 2007-08-01
 */
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogConstants;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;

public class BlogDao extends BaseHibernateDao<BlogFamily> {
	
	/**
	 * 查询所有主题的标题集合
	 */
	@SuppressWarnings("unchecked")
	public List<String> listAllFamilyName()throws Exception{
		//增加人员过滤
		User user = CurrentUser.get();
		String hsql = "select BlogFamily.name from BlogFamily as family "
			+ "where family.employeeId=?";
		List list = super.find(hsql, user.getId());		
		List<String> familyNameList = new ArrayList<String>();
		for(Object obj : list) {
			String familyName = (String)obj;
			familyNameList.add(familyName);
		}
		return familyNameList;
	}

	/**
	 * 查询分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogFamily> listFamily(String type) throws Exception {
		//增加人员过滤
		User user = CurrentUser.get();		
		String hql = "from BlogFamily as family where family.type=? and family.employeeId=? order by  family.createDate";
		
		List<BlogFamily> ret = super.find(hql, null, type, user.getId());
		
		return ret;
	}
	
	/**
	 * 查询分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogFamily> listFamily2(String type) throws Exception {		
//		增加单位过滤--lucx  前端讨论板块列表显示
		User user = CurrentUser.get();		
		String hsql = "from BlogFamily as family where family.type=? and family.employeeId=?";		
	    Object[] values = {type,user.getId()};
	    return super.find(hsql, values);
	}
	/**
	 * 查询他人分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogFamily> listFamilyOther(String type,Long userId) throws Exception {
		//增加人员过滤				
		String hsql = "from BlogFamily as family where family.type=? and family.employeeId=?";
		Object[] values = {type,userId};
	     
	    return super.find(hsql, values);
	}
	
	/**
	 * 查询他人分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogFamily> listFamilyOther2(String type,Long userId) throws Exception {		
		String hsql = "from BlogFamily as family where family.type=? and family.employeeId=?";
		Object[] values = {type,userId};
	     
	    return super.find(hsql, values);
	}

	/**
	 * 新增分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void createFamily(BlogFamily blogFamily) throws Exception {
		this.save(blogFamily);
	}

	/**
	 * 变更分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void modifyFamily(BlogFamily blogFamily) throws Exception {
		this.update(blogFamily);
	}

	/**
	 * 删除分类信息
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public void deleteFamily(Long blogFamilyId) throws Exception {
		this.removeById(blogFamilyId);		
	}
	
	//根据ID获取分类
	public BlogFamily getSingleFamily(Long id){
		return this.get(id);
	}
	/**
	 * 得到默认分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getDefaultFamilyID(Long employeeId,String type) throws Exception{
		Long familyId = null;
		String hsql = "from BlogFamily as family where family.employeeId=? and family.type=? and family.nameFamily=?";
		Object[] values = {employeeId,type,BlogConstants.Blog_FAMILY_DEFAULT};
		List list = super.find("select count(*) " + hsql, values);
		int total = 0;
		if (list != null && !list.isEmpty()) {
			total = (Integer)list.get(0);
		}
		if (total > 0) {
			list = super.find("select family.id " + hsql, values);
			if (list != null && !list.isEmpty()) {
				familyId = (Long)list.get(0);
			}
		}
		else {
			familyId = new Long(0);
		}		
		return familyId;
	}
	/**
	 * 得到私有分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getPrivateFamilyID(Long employeeId,String type) throws Exception{
		Long familyId = null;
		String hsql = "select family.id from BlogFamily as family "
			+ "where family.employeeId=? and family.type=? and family.nameFamily=?";
		Object[] values = {employeeId, type, BlogConstants.Blog_PRIVATE_DEFAULT};
		List list = super.find(hsql, values);
		if (list != null) {
			familyId = (Long)list.get(0);
		}
		return familyId;
	
	}
	/**
	 * 新增收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void createFavorites(BlogFavorites blogFavorites) throws Exception {
		this.save(blogFavorites);
	}

	/**
	 * 变更收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void modifyFavorites(BlogFavorites blogFavorites) throws Exception {
		this.update(blogFavorites);
	}

	/**
	 * 删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavorites(Long blogFavoritesId) throws Exception {
		this.removeById(blogFavoritesId);		
	}
	/**
	 * 检查文章是否已经收藏
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public Integer checkFavorites(Long articleId,Long employeeId) throws Exception {
		Integer favorites = null;
		String hsql = "select count(*) from BlogFavorites as favorite where favorite.BlogArticle=? and favorite.employeeId=?";
//		Object[] values = {articleId, employeeId};
		BlogArticle ba = new BlogArticle();
		ba.setId(articleId);
		List list = super.find(hsql, ba, employeeId);
		if (list != null && !list.isEmpty()) {
			favorites = (Integer)list.get(0);
		}
		return favorites;		
	}
	/**
	 * 删除收藏文章
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavoritesArticle(Long articleId,Long employeeId,Long id) throws Exception {
		Integer ct = checkFavorites(articleId,employeeId);
		if (ct != null){
			this.removeById(id);
		}
	}
	/**
	 * 方法描述：创建文章，该分类的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateArticleNumber(Long familyId, int step)throws Exception{
		BlogFamily family = this.get(familyId);
		family.setArticleNumber(family.getArticleNumber() + step);
		this.update(family);
	}
	
}
