/**
 * 
 */
package com.seeyon.v3x.blog.dao;

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogReply;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：博客回复
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class BlogReplyDao extends BaseHibernateDao<BlogReply>{
	//添加博客回复
	public void BlogReplyDao(List<BlogReply> list) throws Exception {
		for (BlogReply BlogReply : list) {
			this.save(BlogReply);
		}
	}
	/**
	 * 方法描述：获取某一主题的回复信息(不包括对他人回复的评论)
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByArticleId(Long articleId) throws Exception{
		List<BlogReply> replyList = null;
		StringBuffer hql = null;
	
		hql = new StringBuffer();
		hql.append(" from " + BlogReply.class.getName() + " as BlogReply ");
		hql.append(" where BlogReply.BlogArticle.id = ? and BlogReply.parentId is null");
		hql.append("  order by BlogReply.issueTime asc");
		
//		System.out.println("listReplyByArticleId=="+hql);
		replyList = this.find(hql.toString(), articleId );
		
		if ((replyList != null) && (!replyList.isEmpty())) {
			return replyList;
		}
		
		return null;
	}
	/**
	 * 方法描述：根据parentId获取某一评论的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByParentId(Long parentId) throws Exception{
		List<BlogReply> replyList = null;
		StringBuffer hql = null;
	
		hql = new StringBuffer();
		hql.append(" from " + BlogReply.class.getName() + " as BlogReply ");
		hql.append(" where BlogReply.parentId = ?");
		hql.append("  order by BlogReply.issueTime asc");
		
//		System.out.println("listReplyByParentId=="+hql+",parentId=="+parentId);
		replyList = this.find(hql.toString(), parentId );
		
		if ((replyList != null) && (!replyList.isEmpty())) {
			return replyList;
		}
		
		return null;
	}
	/**
	 * 方法描述：取得某文章所有评论的回复信息
	 * 
	 * @return List 评论的回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyHaveParentId(Long articleId) throws Exception{
		List<BlogReply> replyList = null;
		StringBuffer hql = null;
	
		hql = new StringBuffer();
		hql.append(" from " + BlogReply.class.getName() + " as BlogReply ");
		hql.append(" where BlogReply.BlogArticle.id = ? and BlogReply.parentId is not null");
		hql.append("  order by BlogReply.issueTime,BlogReply.parentId asc");
		
//		System.out.println("listReplyHaveParentId=="+hql+",articleId=="+articleId);
		replyList = this.find(hql.toString(), articleId );
		
		if ((replyList != null) && (!replyList.isEmpty())) {
			return replyList;
		}
		
		return null;
	}
	
	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return BlogArticle
	 * @throws Exception
	 *
	 */
	public BlogReply getReplyPostById(Long postId)throws Exception{
		return this.get(postId);
	}
	
	/**
	 * 方法描述：物理删除某一条回复帖信息
	 *
	 * @param replyPostId 回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId) throws Exception{
		this.removeById(replyPostId);
	}
	
	/**
	 * 方法描述：逻辑删除某一主题下的所有回复帖信息
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void deleteReplyPostByArticleId(Long articleId) throws Exception{
//		Map<String , Object> map = new HashMap<String , Object> ();
//		map.put("BlogArticle.id", new Long(articleId));
//
//		this.delete(map);
		String hql = "delete from BlogReply where BlogArticle.id=?";
		super.bulkUpdate(hql, null, articleId);
	}
}
