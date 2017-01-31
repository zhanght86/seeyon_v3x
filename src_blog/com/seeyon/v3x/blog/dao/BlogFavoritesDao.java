package com.seeyon.v3x.blog.dao;

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：博客收藏
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */

public class BlogFavoritesDao extends BaseHibernateDao<BlogFavorites> {
	//添加博客收藏
	public void BlogFavoritesDao(List<BlogFavorites> list) throws Exception {
		for (BlogFavorites BlogFavorites : list) {
			this.save(BlogFavorites);
		}
	}

	/**
	 * 删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavorites(Long blogFavoritesId) throws Exception {
//		//BlogFamily family  = this.get(blogFamilyId);
//		//this.delete(family);
//		Map<String , Object> map = new HashMap<String , Object> ();
//		map.put("id", new Long(blogFavoritesId));
//
//		this.delete(map);		
		super.delete(blogFavoritesId.longValue());
	}
	/**
	 * 根据文章ID删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavoritesByArticleId(Long articleId) throws Exception {
		//BlogFamily family  = this.get(blogFamilyId);
		//this.delete(family);
//		Map<String , Object> map = new HashMap<String , Object> ();
//		map.put("articleId", new Long(articleId));
//
//		this.delete(map);	
		String hql = "delete from BlogFavorites  where BlogArticle.id=?";
		try{
			super.bulkUpdate(hql, null, articleId);
		}catch(Exception e) {
			
		}
		
	}
	
}
