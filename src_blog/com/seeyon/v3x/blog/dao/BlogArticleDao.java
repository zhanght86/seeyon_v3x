package com.seeyon.v3x.blog.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogReply;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述： 博客文章操作
 * 创建日期：2007-08-01
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 * @see BaseHibernateDao
 */
public class BlogArticleDao extends BaseHibernateDao<BlogArticle> {

	/**
	 * 方法描述：新建博客主题
	 * 
	 * @param BlogArtile  主题信息
	 * @throws Exception
	 */
	public void createArticle(BlogArticle BlogArtile) throws Exception {
		this.save(BlogArtile);
	}

	/**
	 * 方法描述：回复博客主题
	 * 
	 * @param BlogArtile  主题信息
	 * @throws Exception
	 */
	public void replyArticle(BlogReply BlogArticleReply) throws Exception {
		this.save(BlogArticleReply);
	}
	
	/**
	 * 方法描述：获取某一分类的主题数
	 * 
	 * @param bamilyId 分类编号
	 * @return Long 该分类的主题数
	 * @throws Exception 
	 */
	public Integer getFamilyArticleNumber(Long familyId)throws Exception{
		Integer familyArticleNumber = null;		
		String hsql = "select count(*) from BlogArticle as article "
			+ "where article.BlogFamily.id=? and article.state=?";
		Object[] values = {familyId, (byte)0};
		List list = super.find(hsql, values);
		if (list != null && !list.isEmpty()) {
			familyArticleNumber = (Integer)list.get(0);
		}
					
		return familyArticleNumber;
	}
		
	/**
	 * 方法描述：获取某一个主题的回复数
	 * @param articleId 主题ID
	 * @return Long 该主题的回复数
	 * @throws Exception
	 */
	public Integer getArticleReplyNumber(Long articleId) throws Exception{		
		Integer articleReplyNumber = null;		
		String hsql = "select count(*) from reply as BlogReply where reply.BlogArticle.id=?";
		List list = super.find(hsql, articleId);
		if (list != null && !list.isEmpty()) {
			articleReplyNumber = (Integer)list.get(0);
		}
		
		return articleReplyNumber;
	}
	
	/**
	 * 方法描述：获取某一个分类的所有主题的回复数
	 * @param familyId 分类ID
	 * @return Long 该分类所有主题的回复数
	 * @throws Exception
	 */
	public Integer getFamilyReplyNumber(Long familyId) throws Exception{		
		Integer familyReplyNumber = null;
		String hsql = "select count(*) from BlogArticle as article,BlogReply as reply "
			+ "where article.id=reply.BlogArticle.id and article.BlogFamily.id=? and article.state=?";
		Object[] values = {familyId, (byte)0};
		List list = super.find(hsql, values);
		if (list != null && !list.isEmpty()) {
			familyReplyNumber = (Integer)list.get(0);
		}
		return familyReplyNumber;
	}

	/**
	 * 方法描述：根据主题ID查询该主题信息
	 * 
	 * @return BlogArticle
	 * @throws Exception
	 *
	 */
	public BlogArticle getArticleById(Long articleid)throws Exception{
		BlogArticle article = this.get(articleid);
		return article;
	}
	
	/**
	 * 方法描述：物理删除
	 *
	 * @param articleId 主题Id
	 * @throws Exception
	 */
	public void deleteArticle(Long articleId) throws Exception{
		super.removeById(articleId);
	}
	
	/**
	 * 方法描述：点击某一主题，该主题的点击数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateClickNumber(Long articleId)throws Exception{
		BlogArticle article = get(articleId);
		
		article.setClickNumber(article.getClickNumber() + 1);
		this.update(article);
	}
	
	public void _updateClickNumber(Long articleId,boolean boo)throws Exception{
		BlogArticle article = get(articleId);
		if (boo)
			article.setClickNumber(article.getClickNumber()+1);
		else
			article.setClickNumber(article.getClickNumber());
		this.update(article);
	}
	
	/**
	 * 方法描述：回复某一主题，该主题的回复数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateReplyNumber(Long articleId, int step)throws Exception{
		BlogArticle article = this.get(articleId);
		article.setReplyNumber(article.getReplyNumber() + step);
		this.update(article);
	}
	/**
	 * 方法描述：更新文章分类
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateFamilyId(Long articleId, Long familyId) throws Exception{
		BlogArticle article = this.get(articleId);
		article.setFamilyId(familyId);
		this.update(article);
	}
	
	public List execute(final String hsql){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.createQuery(hsql).list();
			}
		}, true);
	}
}
