/**
 * 
 */
package com.seeyon.v3x.bbs.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleReply;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 
 * @since JDK 5.0
 */
public class BbsArticleReplyDao extends BaseHibernateDao<V3xBbsArticleReply>{
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticleReply> listReplyByArticleId(Long articleId) throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xBbsArticleReply.class)
			.add(Expression.eq("articleId", articleId))
			.add(Expression.eq("state", (byte)0))
			.addOrder(Order.asc("replyTime"))
		;
		
		return super.executeCriteria(criteria, -1, -1);
	}
	
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息   按照指定条数抽取
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticleReply> listReplyByArticleId(final Long articleId , final int beginRow , final int pageSize,final String orderValue
			) throws Exception{
		return (List<V3xBbsArticleReply>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				String hql = "SELECT reply From "
					+ V3xBbsArticleReply.class.getName() + " reply "
					+ " Where  reply.articleId = ? and reply.state = ? order by reply.replyTime " + orderValue;
				
				Query query = session.createQuery(hql);
                query.setLong(0, articleId);
                query.setInteger(1, BbsConstants.BBS_ARTICLE_IS_ACTIVE);
                query.setFirstResult(beginRow);
                query.setMaxResults(pageSize);
				return query.list();
			}
		
		});
	}
	
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息的总数
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int countReplyByArticleId(final Long articleId) throws Exception{
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				String count = "SELECT count(reply.id) From "
					+ V3xBbsArticleReply.class.getName() + " reply "
					+ " Where  reply.articleId = ? and reply.state = ?";
				
				Query queryCount = session.createQuery(count).setLong(0, articleId).setInteger(1, BbsConstants.BBS_ARTICLE_IS_ACTIVE);
				int typeCount = ((Integer) queryCount.uniqueResult()).intValue();
				
				return typeCount;
			}
		
		});
	}
	
	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return V3xBbsArticle
	 * @throws Exception
	 *
	 */
	public V3xBbsArticleReply getReplyPostById(Long postId)throws Exception{
		V3xBbsArticleReply replPost = this.get(postId);
		return replPost;
	}
	
	/**
	 * 方法描述：逻辑删除某一条回复帖信息，,将回复帖的state设置为1（为删除状态）
	 *
	 * @param replyPostId 回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId, Long articleId) throws Exception{
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("state", (byte)1);
		
		this.update(replyPostId, columns);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xBbsArticle.class)
			.setProjection(Projections.property("replyNumber"))
			.add(Expression.eq("id", articleId))
		;
	
		Integer replyNumber = (Integer)super.executeUniqueCriteria(criteria);
		
		Map<String, Object> columns1 = new HashMap<String, Object>();
		columns1.put("replyNumber", replyNumber - 1);
		
		this.update(V3xBbsArticle.class, articleId, columns1);
	}
	
	/**
	 * 方法描述：逻辑删除某一主题下的所有回复帖信息
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void deleteReplyPostByArticleId(Long articleId) throws Exception{	
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("state", (byte)1);
		
		this.update(V3xBbsArticleReply.class, columns, new Object[][]{{"articleId", articleId}});
	}
}
