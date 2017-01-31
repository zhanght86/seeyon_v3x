package com.seeyon.v3x.blog.manager;

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogReply;
import com.seeyon.v3x.blog.webmodel.ArticleModel;

/**
 * 类描述：博客文章管理
 * 创建日期：2007-08-01
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public interface BlogArticleManager {
	
	/**
	 * 方法描述：获取一定条数的博客文章
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<ArticleModel> getBlogArticleByCount(Long userId,int count) throws Exception ;
	/**
	 * 方法描述：获取博客所有分类的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllArticle(Long userId,byte state) throws Exception ;
	public List<BlogArticle> listAllArticlePaging(Long userId,byte state) throws Exception;
	/**
	 * 方法描述：获取他人博客所有分类的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllArticleOther(Long userId) throws Exception ;
	/**
	 * 方法描述：获取博客所有的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllUsersArticle() throws Exception ;
	/**
	 * 方法描述：获取博客所有版块的所有收藏的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
//	public List<BlogArticle> listAllFavoritesArticle() throws Exception;	
	
	/**
	 * 方法描述：获取博客某一分类的所有主题信息
	 * 
	 * @param boardId 分类编号
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	//public List<BlogArticle> listArticleByBoardId(Long boardId) throws Exception;
	
	/**
	 * 方法描述：判断博客某一分类今天是否有新的主题
	 * 
	 * @return Boolean
	 * @throws Exception 
	 */
	public Boolean hasNewTodayArticle(Long boardId) throws Exception;
	
	/**
	 * 方法描述：判断博客某一分类今天是否有回复信息
	 * 
	 * @return Boolean 
	 * @throws Exception 
	 */
	public Boolean hasNewTodayReplyPost(Long boardId) throws Exception ;

	/**
	 * 方法描述：新建博客主题
	 * 
	 * @param v3xBlogArtile  主题信息
	 * @throws Exception
	 */
	public void createArticle(BlogArticle v3xBlogArtile) throws Exception ;

	/**
	 * 方法描述：回复博客主题
	 * 
	 * @param v3xBlogArtile  主题信息
	 * @throws Exception
	 */
	public void replyArticle(BlogReply v3xBlogReply) throws Exception;
	/**
	 * 方法描述：根据ID查询该回复数
	 * 
	 * @return BlogArticleReply
	 * @throws Exception
	 *
	 */
	public Integer getReplyNumber(Long postId)throws Exception;
	
	/**
	 * 方法描述：获取某一分类的主题数
	 * 
	 * @param boardId 分类编号
	 * @return Long 该分类的主题数
	 * @throws Exception 
	 */
	//public Integer getBoardArticleNumber(Long boardId)throws Exception;
	
	/**
	 * 方法描述：获取某一个主题的回复数
	 * @param articleId 主题ID
	 * @return Long 该主题的回复数
	 * @throws Exception
	 */
	//public Integer getArticleReplyNumber(Long articleId) throws Exception;
		
	/**
	 * 方法描述：获取某一个分类的所有主题的回复数
	 * @param boardId 分类ID
	 * @return Long 该分类所有主题的回复数
	 * @throws Exception
	 */
	//public Integer getBoardReplyNumber(Long boardId) throws Exception;
		
	/**
	 * 方法描述：根据主题ID查询该帖信息
	 * 
	 * @return BlogArticle
	 * @throws Exception
	 *
	 */
	public BlogArticle getArticleById(Long articleid)throws Exception;
	
	/**
	 * 方法描述：获取博客是否已经被删除的标记
	 * * @return boolean 是否获取到对象的标记
	 * @throws Exception
	 */
	public byte getArticleFlag(long articleId) throws Exception;
	/**
	 * 方法描述：获取博客某一分类的所有主题信息
	 * 
	 * @param familyId
	 *            分类编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listArticleByFamilyId(Long familyId) throws Exception;
	/**
	 * 方法描述：获取博客某一收藏分类的所有主题信息
	 * 
	 * @param familyId
	 *            分类编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listArticleByFamilyFavoritesId(Long familyId) throws Exception;
	
	public List<BlogArticle> listAllArticleByFamilyFavoritesId(Long familyId)throws Exception ;

	/**
	 * 方法描述：获取博客某一主题的回复信息(不包括对他人回复的评论)
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByArticleId(Long articleId) throws Exception;
	/**
	 * 方法描述：根据parentId获取某一评论的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyByParentId(Long parentId) throws Exception;
	/**
	 * 方法描述：取得某文章所有评论的回复信息
	 * 
	 * @return List 评论的回复信息的集合
	 * @throws Exception 
	 */
	public List<BlogReply> listReplyHaveParentId(Long articleId) throws Exception;
	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return BlogReply
	 * @throws Exception
	 *
	 */
	public BlogReply getReplyPostById(Long postId)throws Exception;
	
	/**
	 * 方法描述：删除主题信息
	 *
	 * @param articleId 主题Id
	 * @throws Exception
	 */
	public void deleteArticle(Long articleId) throws Exception;
	/**
	 * 方法描述：删除某一条回复帖信息
	 *
	 * @param replyPostId 回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId) throws Exception;
	/**
	 * 方法描述：检查要删除的恢复主题是否存在
	 * 
	 * @param postId 回复帖ID
	 */
	public boolean checkReply(Long postId) throws Exception;
	
	
	/**
	 * 方法描述：删除某一主题下的所有回复帖信息
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void deleteReplyPostByArticleId(Long articleId) throws Exception;
	
	/**
	 * 方法描述：点击某一主题，该主题的点击数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateClickNumber(Long articleId)throws Exception;
	/**
	 * 方法描述：回复某一主题，该主题的回复数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 *
	 */
	
	public void _updateClickNumber(Long articleId,boolean b)throws Exception;
	
	
	public void updateReplyNumber(Long articleId, int step)throws Exception;
	/**
	 * 方法描述：更新文章分类
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateFamilyId(Long articleId, Long familyId) throws Exception;
	/**
	 * 方法描述：查询符合条件的主题信息
	 *
	 */
	public List queryByCondition(Long userId,String condition,
             					String field, String field1) throws Exception;
	
	/**
	 * 得到某个用户的收藏博客
	 */
	public List<BlogFavorites> getFavoriteBlogsOfUser(long userId);
	public List<BlogFavorites> getFavoriteBlogsOfUserByPage(long userId);
	
	/**
	 * 得到分类下的文章总数
	 */
	public int getTotalOfFamily(long familyId);
	
	/**
	 * 得到某人共享的最新文章
	 */
	public BlogArticle getLatestSharedArticle(long userId);
	
	/**
	 * 方法描述：获取博客所有的主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<BlogArticle> listAllUsersArticlePaging() throws Exception;
	/**
	 * 取得当前博客的共享状态
	 * @param articleid
	 * @param state
	 */
	
	public boolean getState(long id);
	
	/**
	 * 改变当前博客的共享状态
	 * @param articleid
	 * @param state
	 */
	
	public byte modifyShareState(long id);
	
	   /**
	    * 判断收藏分类的名字是否有效
	    * @param id 新建的时候传0；编辑的时候传id
	    */
	   public boolean nameValid(String name, long id, long memberId);
	public void updateArticle(BlogArticle article);
}
