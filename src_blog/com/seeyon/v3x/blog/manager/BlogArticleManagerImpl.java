/**
 * 
 */
package com.seeyon.v3x.blog.manager;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.blog.dao.BlogArticleDao;
import com.seeyon.v3x.blog.dao.BlogDao;
import com.seeyon.v3x.blog.dao.BlogEmployeeDao;
import com.seeyon.v3x.blog.dao.BlogFavoritesDao;
import com.seeyon.v3x.blog.dao.BlogReplyDao;
import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogConstants;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogReply;
import com.seeyon.v3x.blog.webmodel.ArticleModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
/**
 * 类描述： 创建日期：2007-08-01
 * 
 * @author xiaoqiuhe
 * @version 1.0
 * @since JDK 5.0
 */
public class BlogArticleManagerImpl extends BaseHibernateDao<BlogArticle> implements BlogArticleManager,IndexEnable {

	private static Log log = LogFactory.getLog(BlogArticleManagerImpl.class);
	
	private OrgManager orgManager;
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	} 
	
	private BlogArticleDao blogArticleDao;
	private BlogReplyDao blogReplyDao;
	private BlogDao blogDao;
	private BlogEmployeeDao blogEmployeeDao;
	private SearchManager searchManager;
	private AttachmentManager attachmentManager;
	
	private BlogFavoritesDao blogFavoritesDao;
	
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	
	public void setSearchDao(SearchManager searchDao) {
		this.searchManager = searchDao;
	}

	public void setVlogArticleIssueAreaDao(
			BlogEmployeeDao blogEmployeeDao) {
		this.blogEmployeeDao = blogEmployeeDao;
	}

	public void setBlogReplyDao(BlogReplyDao blogReplyDao) {
		this.blogReplyDao = blogReplyDao;
	}

	public void setBlogArticleDao(BlogArticleDao blogArticleDao) {
		this.blogArticleDao = blogArticleDao;
	}
	
	/**
	 * 方法描述：获取一定条数的博客文章
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<ArticleModel> getBlogArticleByCount(Long userId,int count) throws Exception {
		User user=CurrentUser.get();
		List<BlogArticle>  BlogArticleList = this.listAllArticle(user.getId(),(byte)2);
		List<ArticleModel> latestArticleModelList = new ArrayList<ArticleModel>();
		if (BlogArticleList.size() > count) {
			latestArticleModelList = this.getArticleModelList(BlogArticleList.subList(0, count));
		} else {
			latestArticleModelList = this.getArticleModelList(BlogArticleList);
		}
		
		return latestArticleModelList;
	}
   
	/**
	 * 方法描述：获取博客所有版块的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listAllArticle(Long userId,byte state) throws Exception {	     
		StringBuffer hsql = new StringBuffer("from BlogArticle as article where article.employeeId=? ");
		if (state != 2) {
			hsql.append("and article.state = ? ");
		}
		hsql.append("order by article.modifyTime desc");

		if(state!=2) {        	
			Object[] values = {userId, state};
			return super.find(hsql.toString(), values);
		}
		else {
			return super.find(hsql.toString(), userId);
		}
	}
	
	public List<BlogArticle> listAllArticlePaging(Long userId,byte state) throws Exception {
		List<Object> indexParameter = new ArrayList<Object>();
	
		String hql = "from BlogArticle as article where article.employeeId=?";
		indexParameter.add(userId);
		if (state != 2) {
			hql += " and article.state=?";
			indexParameter.add(state);
		}
		String orderStr = " order by article.modifyTime desc ";

        
        final String hqlf = hql + orderStr;
		
        return super.find(hqlf, null, indexParameter);
	}
	
	
	/**
	 * 方法描述：将List<BlogArticle>转换为List<ArticleModel>
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	public List<ArticleModel> getArticleModelList(List<BlogArticle> articleList)
			throws Exception {
		List<ArticleModel> articleModelList = new ArrayList<ArticleModel>();

		if (articleList != null) {
			// 构造显示列表
			for (BlogArticle BlogArticle : articleList) {
				ArticleModel articleModel = new ArticleModel();

				articleModel.setId(BlogArticle.getId());
				articleModel.setSubject(BlogArticle.getSubject());
				articleModel.setFamilyId(BlogArticle.getFamilyId());

				articleModel.setClickNumber((Integer) BlogArticle
						.getClickNumber());

				// 计算该主题的回复数
				articleModel.setReplyNumber((Integer) BlogArticle
						.getReplyNumber());
				//Integer replyNumber = blogArticleManager
				//		.getReplyNumber(BlogArticle.getId());
				//articleModel.setReplyNumber(replyNumber);
				articleModel.setIssueTime(new java.sql.Date(BlogArticle
						.getIssueTime().getTime()));

				List<Attachment> attachment = attachmentManager
						.getByReference(BlogArticle.getId());
				// 是否有附件
				if (attachment != null && attachment.size() > 0) {
					articleModel.setAttachmentFlag((byte) 1);
				} else {
					articleModel.setAttachmentFlag((byte) 0);
				}
//				articleModel.setFavoritesId(BlogArticle.getFavoritesId());

				articleModelList.add(articleModel);
			}
		}

		return articleModelList;
	}
	/**
	 * 方法描述：获取他人博客所有版块的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listAllArticleOther(Long userId) throws Exception {
		String hsql = "from BlogArticle as article where article.state=? and article.employeeId=? order by article.modifyTime desc";
		Object[] values = {(byte)0, userId};

	    return super.find(hsql, values);
	}
	/**
	 * 方法描述：获取博客所有的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllUsersArticle() throws Exception {		
		String hsql = "from BlogArticle as article where article.state=? order by article.employeeId,article.modifyTime desc";	     
	    return super.find(hsql, (byte)0);		
	}
	/**
	 * 方法描述：获取博客所有的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllUsersArticlePaging() throws Exception {		
		//final String hsql = "from BlogArticle as article, V3xOrgMember as amember where article.state=0 "
		//	+ " and article.employeeId = amember.id and amember.orgAccountId = " + CurrentUser.get().getLoginAccount()
		//	+ " order by article.modifyTime desc";	    

	 String hsql = "select article from BlogArticle as article, V3xOrgMember as amember where article.state=0 and article.employeeId = amember.id and amember.orgAccountId=?";
			//+ " order by article.modifyTime desc";	     
		
	    final  String	hsqls = hsql + " order by article.modifyTime desc";	  
        
        return super.find(hsqls, null, CurrentUser.get().getLoginAccount());
	}
	/**
	 * 方法描述：获取博客所有版块的所有收藏的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
//	public List<BlogArticle> listAllFavoritesArticle() throws Exception {		
//		User user = CurrentUser.get();
//		Long currentUserId = user.getId();								
//		String hsql = "select article from BlogArticle as article,BlogFavorites as favorite "
//			+ "where article.id=favorite.articleId and article.state=? and favorite.employeeId=? "
//			+ "order by article.modifyTime desc";
//		Object[] values = {(byte)0, currentUserId};
//	    return super.find(hsql, values);
//	}
	/**
	 * 得到某个用户的收藏博客
	 */
	public List<BlogFavorites> getFavoriteBlogsOfUser(long userId){
		String hql = "select f from BlogFavorites as f, BlogArticle as a where f.employeeId = ?"
			 + " and a.id = f.BlogArticle and a.state = 0 order by a.modifyTime desc";
		return blogFavoritesDao.find(hql, userId);
	}
	public List<BlogFavorites> getFavoriteBlogsOfUserByPage(long userId){
		
		String hql = "from BlogFavorites as f, BlogArticle as a where f.employeeId =? and a.id = f.BlogArticle and a.state = 0 ";
		/**
        if (Pagination.isNeedCount()) {
            List<BlogFavorites> flist = blogFavoritesDao.find(hql, null, userId);
            Pagination.setRowCount(flist.size());
        }
        **/
        hql += " order by a.modifyTime desc";
        final String hqlf = "select f " + hql;
        
        final Long uId = userId;
		/**
        List<BlogFavorites> ret = (List<BlogFavorites>)blogFavoritesDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				/*
				return session.createQuery(hqlf)
				.setParameter(0, uId)
				.setFirstResult(Pagination.getFirstResult())
        		.setMaxResults(Pagination.getMaxResults()).list();
				 List<BlogFavorites> flist = blogFavoritesDao.find(hqlf, null, uId);
				 return flist ;
			}
    	});
        ***/
        List<BlogFavorites> ret =   blogFavoritesDao.find(hqlf, null ,uId) ;
        
        return ret;
	}

	/**
	 * 方法描述：获取博客某一分类的所有主题信息
	 * 
	 * @param familyId
	 *            分类编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listArticleByFamilyId(Long familyId)
			throws Exception {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();		
		String hsql = "from BlogArticle as article where article.state=? and article.employeeId=? and article.familyId=? "
			+ "order by article.modifyTime desc";
		Object[] values = {(byte)0, currentUserId, familyId};	
	    return super.find(hsql, values);
	}
	/**
	 * 方法描述：获取博客某一收藏分类的所有主题信息
	 * 
	 * @param familyId
	 *            分类编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listArticleByFamilyFavoritesId(Long familyId)
			throws Exception {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		String hsql = "select article from BlogArticle as article,BlogFavorites as favorite "
			+ "where article.id=favorite.BlogArticle and article.state=? and favorite.employeeId=? and favorite.familyId=? "
			+ "order by article.modifyTime desc";
		Object[] values = {(byte)0, currentUserId, familyId};
		return super.find(hsql, values);
		//return super.find(hsql, 0, 6, null, values) ;
	}
	
	
	/**
	 * 方法描述：获取博客某一收藏分类的所有主题信息
	 * 
	 * @param familyId
	 *            分类编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 * 只得到该分类下的前六条数据
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listAllArticleByFamilyFavoritesId(Long familyId)
			throws Exception {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		String hsql = "select article from BlogArticle as article,BlogFavorites as favorite "
			+ "where article.id=favorite.BlogArticle and article.state=? and favorite.employeeId=? and favorite.familyId=? "
			+ "order by article.modifyTime desc";
		Object[] values = {(byte)0, currentUserId, familyId};
		//return super.find(hsql, values);
		return super.find(hsql, 0, 6, null, values) ;
	}
	
	
	/**
	 * 方法描述：判断博客某一版块今天是否有新的主题
	 * 
	 * @return Boolean
	 * @throws Exception 
	 */
	public Boolean hasNewTodayArticle(Long boardId) throws Exception{
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		
		// 我能访问的所有Id
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(currentUserId,
					ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM);
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		
		Integer newTodayArticle = null;
		StringBuffer hql = null;
		
		//构造今天的起始时间和终止时间的时间戳
		Timestamp todayStartTimestamp = null;
		Timestamp todayEndTimestamp = null;
		java.util.Date date1 = null;
		java.util.Date date2 = null;
		Calendar cal1 = null;
		Calendar cal2 = null;
		
		cal1 = new GregorianCalendar();
        cal1.set(Calendar.AM_PM, Calendar.AM);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        date1 = cal1.getTime();
        
        cal2 = new GregorianCalendar();
        cal2.set(Calendar.AM_PM, Calendar.PM);
        cal2.set(Calendar.HOUR, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);
        date2 = cal2.getTime();
        
        todayStartTimestamp = new Timestamp(date1.getTime());
		todayEndTimestamp = new Timestamp(date2.getTime());
		
		hql = new StringBuffer();
		hql.append(" select count(*) ");
		hql.append(" from " + BlogArticle.class.getName() + " as BlogArticle ");
		hql.append(" where BlogArticle.state = ? ");
		hql.append(" and BlogArticle.BlogFamily.id = ? ");
		hql.append(" and BlogArticle.issueTime > ? ");
		hql.append(" and BlogArticle.issueTime < ? ");
		hql.append(" and (");
		hql.append("	  	(BlogArticle.employeeId = ?)" );
		hql.append("     )" );
				
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("domainIds", domainIds);
		Object[] values = {(byte)0, boardId,todayStartTimestamp,todayEndTimestamp,currentUserId,currentUserId};
		List list = super.find(hql.toString(), maps, values);
		if (list != null && !list.isEmpty()) {
			newTodayArticle = (Integer)list.get(0);
			if (newTodayArticle != null && newTodayArticle.intValue() > 0) {
				return true;
			}
		}						
		return false;
	}
	
	/**
	 * 方法描述：判断博客某一版块今天是否有回复信息
	 * 
	 * @return Boolean 
	 * @throws Exception 
	 */
	public Boolean hasNewTodayReplyPost(Long boardId) throws Exception {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		
		// 我能访问的所有Id
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(currentUserId,
					ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM);
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		
		Integer newTodayReplyPost = null;
		StringBuffer hql = null;
		
		//构造今天的起始时间和终止时间的时间戳
		Timestamp todayStartTimestamp = null;
		Timestamp todayEndTimestamp = null;
		java.util.Date date1 = null;
		java.util.Date date2 = null;
		Calendar cal1 = null;
		Calendar cal2 = null;
		
		cal1 = new GregorianCalendar();
        cal1.set(Calendar.AM_PM, Calendar.AM);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        date1 = cal1.getTime();
        
        cal2 = new GregorianCalendar();
        cal2.set(Calendar.AM_PM, Calendar.PM);
        cal2.set(Calendar.HOUR, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);
        date2 = cal2.getTime();
        
        todayStartTimestamp = new Timestamp(date1.getTime());
		todayEndTimestamp = new Timestamp(date2.getTime());
		
		hql = new StringBuffer();
		hql.append(" select count(*) ");
		//hql.append(" from " + BlogArticleReply.class.getName() + " as BlogArticleReply ,");
		hql.append(           BlogArticle.class.getName() + " as BlogArticle ,");
		hql.append(			  BlogEmployee.class.getName() + " as BlogEmployee ,");
		//hql.append(			  BlogFamilyAuth.class.getName() + " as BlogFamilyAuth ");
		hql.append(" where  BlogArticle.id = BlogArticleReply.BlogArticle.id ");
		hql.append(" and BlogArticle.state = ? ");
		hql.append(" and BlogArticle.BlogFamily.id = ? ");
		hql.append(" and BlogArticle.issueTime > ? ");
		hql.append(" and BlogArticle.issueTime < ? ");
		hql.append(" and (");
		hql.append("      (BlogArticle.BlogFamily.id=BlogFamilyAuth.BlogFamily.id and BlogFamilyAuth.authType = 0 and BlogFamilyAuth.moduleId = ?)" );
		hql.append("	  or");
		hql.append("	  	(BlogArticle.issueUserId = ?)" );
		hql.append("	  or" );
		hql.append("      (BlogArticle.id = BlogEmployee.BlogArticle.id and BlogEmployee.moduleId in (:domainIds))" );
		hql.append("     )" );
		
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("domainIds", domainIds);
		Object[] values = {(byte)0, boardId,todayStartTimestamp,todayEndTimestamp,currentUserId,currentUserId};
		List list = super.find(hql.toString(), maps, values);
		if (list != null && !list.isEmpty()) {
			newTodayReplyPost = (Integer)list.get(0);
			if (newTodayReplyPost != null && newTodayReplyPost.intValue() > 0) {
				return true;
			}
		}				
		return false;
	}
	
	/**
	 * 方法描述：新建博客主题
	 * 
	 * @param BlogArtile
	 *            主题信息
	 * @throws Exception
	 */
	public void createArticle(BlogArticle BlogArtile) throws Exception {
		blogArticleDao.createArticle(BlogArtile);
	}

	/**
	 * 方法描述：回复博客主题
	 * 
	 * @param BlogArtile
	 *            主题信息
	 * @throws Exception
	 */
	public void replyArticle(BlogReply BlogArticleReply)
			throws Exception {
		blogArticleDao.replyArticle(BlogArticleReply);
	}

	/**
	 * 方法描述：获取某一版块的主题数
	 * 
	 * @param boardId
	 *            版块编号
	 * @return Long 该版块的主题数
	 * @throws Exception
	 */
	public Integer getFamilyArticleNumber(Long boardId) throws Exception {
		return blogArticleDao.getFamilyArticleNumber(boardId);
	}

	/**
	 * 方法描述：获取某一个主题的回复数
	 * @param articleId 主题ID
	 * @return Long 该主题的回复数
	 * @throws Exception
	 */
	public Integer getArticleReplyNumber(Long articleId) throws Exception{
		return blogArticleDao.getArticleReplyNumber(articleId);
	}
		
	/**
	 * 方法描述：获取某一个版块的所有主题的回复数
	 * @param boardId 版块ID
	 * @return Long 该版块所有主题的回复数
	 * @throws Exception
	 */
	public Integer getFamilyReplyNumber(Long boardId) throws Exception{
		return blogArticleDao.getFamilyReplyNumber(boardId);
	}
	/**
	 * 方法描述：根据主题ID查询该帖信息
	 * 
	 * @return BlogArticle
	 * @throws Exception
	 *
	 */
	public BlogArticle getArticleById(Long articleid)throws Exception{
		return blogArticleDao.getArticleById(articleid);
	}
	/**
	 * 方法描述：根据ID获取博客对象，如果对象为空则返回false标记
	 * @return boolean 是否获取到对象的标记
	 * @throws Exception
	 */
	public byte getArticleFlag(long articleId) throws Exception{
		//BlogArticle article = this.getArticleById(articleId);
		 BlogArticle dr = blogArticleDao.get(articleId);
		 byte  flag = 1;
	//	if(article == null){
		if (dr == null){
	         flag = 0;
			
		}
		
		return flag;
	}
	
	/**
	 * 方法描述：获取博客某一主题的回复信息(不包括对他人回复的评论)
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByArticleId(Long articleId) throws Exception{
		return blogReplyDao.listReplyByArticleId(articleId);
	}
	/**
	 * 方法描述：根据parentId获取某一评论的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByParentId(Long parentId) throws Exception{
		return blogReplyDao.listReplyByParentId(parentId);
	}
	/**
	 * 方法描述：取得某文章所有评论的回复信息
	 * 
	 * @return List 评论的回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyHaveParentId(Long articleId) throws Exception{
		return blogReplyDao.listReplyHaveParentId(articleId);
	}
	/**
	 * 方法描述：根据ID查询该回复数
	 * 
	 * @return BlogArticleReply
	 * @throws Exception
	 *
	 */
	public Integer getReplyNumber(Long postId)throws Exception{
		return 1;
	}
	
	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return BlogArticleReply
	 * @throws Exception
	 *
	 */
	public BlogReply getReplyPostById(Long postId)throws Exception{
		return blogReplyDao.getReplyPostById(postId);
	}
	
	/**
	 * 方法描述：删除主题信息
	 *
	 * @param articleId 主题Id
	 * @throws Exception
	 */
	public void deleteArticle(Long articleId) throws Exception{
		blogArticleDao.deleteArticle(articleId);
	}
	
	/**
	 * 方法描述：删除某一条回复帖信息
	 *
	 * @param replyPostId 回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId) throws Exception{
		blogReplyDao.deleteReplyPost(replyPostId);
	}
	/**
	 * 方法描述：检查要删除的恢复主题是否存在
	 * 
	 * @param postId 回复帖ID
	 */
	public boolean checkReply(Long postId) throws Exception{
		BlogReply reply = blogReplyDao.getReplyPostById(postId);
		if(reply!=null){
			return true;
		}else return false;
		
	}
	
	/**
	 * 方法描述：逻辑删除某一主题下的所有回复帖信息
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void deleteReplyPostByArticleId(Long articleId) throws Exception{
		blogReplyDao.deleteReplyPostByArticleId(articleId);
	}
	
	/**
	 * 方法描述：点击某一主题，该主题的点击数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateClickNumber(Long articleId)throws Exception{
		blogArticleDao.updateClickNumber(articleId);
	}
	
	
	public void _updateClickNumber(Long articleId,boolean b)throws Exception{
		blogArticleDao._updateClickNumber(articleId,b);
	}
	
	/**
	 * 方法描述：回复某一主题，该主题的回复数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateReplyNumber(Long articleId, int step)throws Exception{
		blogArticleDao.updateReplyNumber(articleId,step);
	}
	/**
	 * 方法描述：更新文章分类
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateFamilyId(Long articleId, Long familyId) throws Exception{
		blogArticleDao.updateFamilyId(articleId, familyId);
	}
	
	/**
	 * 方法描述：查询符合条件的主题信息
	 *
	 */
	public List<BlogArticle> queryByCondition( Long userId,String condition,
             					String field, String field1) throws Exception{
		StringBuffer hql = new StringBuffer();
		
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		Byte state = 0;
		if(userId.longValue()==currentUserId.longValue()){
			state = 1;
		}
		boolean byStartTime = false;
		boolean byEndTime = false;

		
		hql.append("from " + BlogArticle.class.getName() + " as BlogArticle, V3xOrgMember as amember  ");
		hql.append(" where ");
		if (userId.longValue()!=0){
			hql.append("  BlogArticle.employeeId ="+userId+" ");
			if (state==0){
				hql.append(  " and BlogArticle.state = "+state);
			}
		}else{
			if (state==0){
				hql.append(  " BlogArticle.state = "+state);
			}
			hql.append(" and BlogArticle.employeeId = amember.id and amember.orgAccountId = " + CurrentUser.get().getLoginAccount());
		}
		//查询条件为主题名称
		if(condition.equals("subject")){
			if(field!=null&&!field.equals("")){
				hql.append(" and BlogArticle.subject like '%");
				hql.append(field.replace("'", "''"));
				hql.append("%'");
			}
		}
		//查询条件为年月
		if(condition.equals("yearMonth")){
			if(field!=null&&!field.equals("")){
				hql.append(" and BlogArticle.y = '");
				hql.append(  field);
				hql.append(  "'");
			}
			if(field1!=null&&!field1.equals("")){
				hql.append(" and BlogArticle.m = '");
				hql.append(  field1);
				hql.append(  "'");
			}
		}
		Timestamp beginTime=null;
		Timestamp endTime=null;
		//查询条件为日期
		if(condition.equals("byDate")){
			if(field!=null&&!field.trim().equals("")){
	        		beginTime=new Timestamp(Datetimes.parseDatetime(field).getTime());
	        		endTime=new Timestamp(Datetimes.parseDatetime(field).getTime());
	        		endTime.setTime(endTime.getTime() + 24 * 60 * 60 * 1000);
	        		hql.append(" and (BlogArticle.issueTime between :start and :end)" );
	        		byStartTime = true;
	        		byEndTime = true;
			}
		}
		//查询条件为发布时间
		if(condition.equals("issueTime")){
			if(field!=null&&!field.trim().equals("")){
				beginTime=new Timestamp(Datetimes.parseDatetime(field).getTime());
				hql.append(" and BlogArticle.issueTime >= :start");
				byStartTime = true;
			}
			if(field1!=null&&!field1.trim().equals("")){
        		endTime=new Timestamp(Datetimes.parseDatetime(field1).getTime());
        		endTime.setTime(endTime.getTime() + 24 * 60 * 60 * 1000);
	            hql.append(" and BlogArticle.issueTime <= :end");
	            byEndTime = true;
			}
		}
			    
	    final Timestamp fBeginTime = beginTime;
	    final Timestamp fEndTime = endTime;
	    
	    final String hqlf = hql.toString();
	    
	    final boolean needSetStart = byStartTime;
	    final boolean needSetEnd = byEndTime;
		
        List<BlogArticle> ret = (List<BlogArticle>)blogFavoritesDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String orderStr = " order by b.modifyTime desc";
	        	Query query = session.createQuery("select count(distinct BlogArticle.id) "  + hqlf);
	        	
	        	if(needSetStart)
	        		query.setTimestamp("start", fBeginTime);
	        	if(needSetEnd)
	        		query.setTimestamp("end", fEndTime);
	    		List list2 = query.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
			
	            query = session.createQuery("select b from BlogArticle b where id in(select distinct BlogArticle.id " + hqlf + ")" + orderStr);
	        	if(needSetStart)
	        		query.setTimestamp("start", fBeginTime);
	        	if(needSetEnd)
	        		query.setTimestamp("end", fEndTime);
				List<BlogArticle> ret = (List<BlogArticle>)query.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();
				
				return ret;
			}
    	});
        
        return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.seeyon..index.share.interfaces.IndexEnable#getIndexInfo(long)
	 */
	public IndexInfo getIndexInfo(long id) throws Exception {
		BlogArticle article=getArticleById(id);
		if(article==null) throw new NullPointerException();
		IndexInfo indexInfo=new IndexInfo();
		indexInfo.setTitle(article.getSubject());
		StringBuffer content=new StringBuffer();
		content.append(article.getContent());
		List<BlogReply> replys = listReplyByArticleId(id);
//		System.out.println("The replys ssss::"+replys.size());
		
		Set<BlogReply> replies=article.getBlogReply();
//		System.out.println("The replies size:"+replies.size());
		for(BlogReply reply:replies){
			content.append(reply.getContent());
		}
		
		
		indexInfo.setContent(content.toString());
		indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
		indexInfo.setContentCreateDate(article.getIssueTime());
		indexInfo.setEntityID(article.getId());
//		indexInfo.setKeyword(article.get);
		indexInfo.setAppType(ApplicationCategoryEnum.blog);
		indexInfo.setCreateDate(new Date(article.getIssueTime().getTime()));//目前设定的是发布日期，此处存疑
		//indexInfo.setAuthor(article.getEmployeeId());
		/*
		String scopeStr=getIssueArea(id);
		String[] scopes=StringUtils.split(scopeStr, ",");
		List<String> ownerList=new ArrayList<String>();
		List<String> departmentList=new ArrayList<String>();
		for(String scope:scopes){
			if(scope.startsWith("Member|")){
				int point=scope.indexOf("|");
				String idStr=scope.substring(point+1);
				ownerList.add(idStr);
			}else if(scope.startsWith("Department|")){
				int point=scope.indexOf("|");
				String idStr=scope.substring(point+1);
				departmentList.add(idStr);
			}
		}
		AuthorizationInfo authorizationInfo=new AuthorizationInfo();
		if(ownerList.size()>0)authorizationInfo.setOwner(ownerList);
		if(departmentList.size()>0)authorizationInfo.setDepartment(departmentList);
		indexInfo.setAuthorizationInfo(authorizationInfo);
		*/
		return indexInfo;
	}

	public BlogFavoritesDao getBlogFavoritesDao() {
		return blogFavoritesDao;
	}

	public void setBlogFavoritesDao(BlogFavoritesDao blogFavoritesDao) {
		this.blogFavoritesDao = blogFavoritesDao;
	}
	
	
	/**
	 * 得到分类下的文章总数
	 */
	public int getTotalOfFamily(long familyId){
		String hql = "select count(*) from BlogFavorites where familyId = ?";
		List objects = blogFavoritesDao.getHibernateTemplate().find(hql, familyId);
		int total = (objects == null || objects.isEmpty()) ? 0 : (Integer)objects.get(0);
		return total;
	}
	/**
	 * 得到他人博客共享文章总数
	 * @param userId
	 * @return
	 */
//	
//	public int getTotalOfUserShare(long userId){
//		String hql = "from BlogArticle as article where article.employeeId = " + userId;
//		hql += " and article.state = 0 ";
//		
//		 
//		//int total = blogFavoritesDao.getQueryCount(hql,null,null);
//		return 0;
//		
//	}
	
	/**
	 * 得到某人共享的最新文章
	 */
	public BlogArticle getLatestSharedArticle(final long userId){		
		String hql = "from BlogArticle as article where article.employeeId =? and article.state = 0 order by article.issueTime desc ";
        final String hqlf = hql;
		List<BlogArticle> ret = (List<BlogArticle>)super.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hqlf)
				.setParameter(0, userId)
				.setFirstResult(0)
        		.setMaxResults(1)
        		.list();
			}
    	});
        if(ret == null || ret.size() == 0)
        	return null;
        else
        	return ret.get(0);
	}
	public boolean getSshareState(long id){
		boolean shareFlag=true;
				return shareFlag;
		
		
	}
	/**
	 * 取得当前博客的状态
	 */
	
	public boolean getState(long id){
		BlogArticle dr = blogArticleDao.get(id);
		if (dr.getState()== 1){
			return false;
		}else return true;
	}

   public byte modifyShareState(long id ) {
	 //  BlogArticle article=getArticleById(id);
	//      if (article.getState() == 1){
	   // 	  article.setState((byte) 0);
	  //  	  return state;
	  //      }
	  //    article.setState((byte) 1);
	   //    return state;
	  // BlogArticle article=getArticleById(id);
	   BlogArticle dr = blogArticleDao.get(id);
	    
	    	   
		
           if (dr.getState()== 1){
              dr.setState((byte) 0);
	          byte ret = dr.getState();
		     return ret ;
		   
	        }
	          dr.setState((byte) 1);	   
	           byte ret = dr.getState();
	   
	
	         return ret;
	       
	         
	
	             
   }  
   
   /**
    * 判断收藏分类的名字是否有效
    * @param id 新建的时候传0；编辑的时候传id
    */
   public boolean nameValid(String name, long id, long memberId){
	   String hql = "select id, nameFamily from BlogFamily where employeeId = ? and type = '" + BlogConstants.Blog_FAMILY_TYPE2 + "'";
	   List<Object[]> list = this.blogFavoritesDao.getHibernateTemplate().find(hql, memberId);
	   
	   if(list == null || list.size() == 0)
		   return true;
	   else{
		   for(Object[] arr : list){
			   if(name.equals((String)arr[1])){
				   if(id == 0L)
					   return false;
				   else if(id == (((Long)arr[0]).longValue()))
					   return true;
				   else
					   return false;
			   }
		   }
		   
		   return true;
	   }
   }

   public void updateArticle(BlogArticle article) {
	   
	this.blogArticleDao.update(article);
   }
}
	   

