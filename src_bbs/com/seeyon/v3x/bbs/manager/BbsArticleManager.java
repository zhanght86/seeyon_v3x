package com.seeyon.v3x.bbs.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleIssueArea;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleReply;
import com.seeyon.v3x.bbs.webmodel.AnonymousCountModel;
import com.seeyon.v3x.bbs.webmodel.BbsCountArticle;
import com.seeyon.v3x.util.cache.UpdateClickManager;

/**
 * 类描述：
 * 创建日期：2007-02-08
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public interface BbsArticleManager  extends UpdateClickManager {
	
	/**
	 * 方法描述：获取讨论区所有版块的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticle> listAllArticle(boolean isGroup, String condition, String textfield, String textfield1 , String boardId ) throws Exception ;
	/**
	 * 获取自定义单位讨论区所有版块的所有主题信息
	 * @param spaceId
	 * @param spaceType
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param boardId
	 * @return
	 * @throws Exception
	 */
	public List<V3xBbsArticle> listAllArticle(long spaceId, int spaceType, String condition, String textfield, String textfield1, String boardId) throws Exception;
	/**
	 * 方法描述：获取主页显示的讨论区所有版块的所有主题
	 * add by Dongjw ,2007-05-23
     * @param pagesize
     * @return List
     */
    public List<V3xBbsArticle> queryArticleList(int pageSize , String condition, String textfield, String textfield1);
    /**
     * 获取主页显示的自定义单位讨论区所有版块的所有主题
     * @param spaceId
     * @param spaceType
     * @param pageSize
     * @param condition
     * @param textfield
     * @param textfield1
     * @return
     */
    public List<V3xBbsArticle> queryCustomArticleList(long spaceId, int spaceType, int pageSize, String condition,
			String textfield, String textfield1);
    
    public List<V3xBbsArticle> queryGroupArticleList(int pageSize);
    
    /**
     * 外单讨论列表
     * 
     * @param condition
     * @param textfield
     * @param textfield1
     * @return
     */
    public List<V3xBbsArticle> queryOtherAccountArticleList(String condition, String textfield, String textfield1);
    
    /**
     * 外单位讨论精华列表
     * 
     * @param condition
     * @param textfield
     * @param textfield1
     * @return
     */
    public List<V3xBbsArticle> queryOtherAccountEliteArticleList(String condition, String textfield, String textfield1);
    
    /**
     * 外单位讨论总数
     * 
     * @return
     */
    public int getOtherAccountArticleNumber();
    
    /**
     * 外单位讨论精华总数
     * @return
     */
    public int getOtherAccountEliteArticleNumber();
    
    /**
     * 外单位讨论回复总数
     * @return
     */
    public int getOtherAccountBoardsReplyNumber();
	
    /**
	 * 方法描述：获取主页显示的部门讨论区所有版块的所有主题
	 * add by Dongjw ,2007-05-23
     * @param departmentId 部门Id
     * @param pagesize
     * @return List
     */
    public List<V3xBbsArticle> DeptqueryArticleList(Long departmentId, int pageSize, String condition, String textfield, String textfield1);
    
    /**
     * 获取项目讨论区某阶段的所有主题
     */
    public List<V3xBbsArticle> ProjectqueryArticleList(Long departmentId, int pageSize, Long phaseId, String condition, String textfield, String textfield1);
    
    /**
     * 条件查询项目讨论区某阶段的所有主题
     */
    public List<V3xBbsArticle> projectQueryArticleListByCondition(String queryCondition,Long departmentId, int pageSize, Long phaseId, Map<String,Object> paramMap);
    
	/**
	 * 方法描述：获取讨论区所有版块的精华帖信息	
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticle> listAllElitePost(String condition, String textfield, String textfield1) throws Exception ;
	public List<V3xBbsArticle> listAllGROUPElitePost(String condition, String textfield, String textfield1) throws Exception;
	/**
	 * 方法描述：获取自定义单位或集团讨论区所有版块的精华帖信息	
	 * @param spaceId
	 * @param spaceType
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @return
	 * @throws Exception
	 */
	public List<V3xBbsArticle> listAllElitePost(long spaceId, int spaceType, String condition, String textfield, String textfield1) throws Exception;
	/**
	 * 方法描述：获取讨论区某一版块的精华帖信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticle> listBoardElitePost(Long boardId, String condition, String textfield, String textfield1) throws Exception ;
	
	/**
	 * 方法描述：获取讨论区某一版块的所有主题信息
	 * 
	 * @param boardId 版块编号
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticle> listArticleByBoardId(Long boardId, String condition, String textfield, String textfield1 , boolean isDept) throws Exception;
	
	public List<V3xBbsArticle> listArticleByBoardId(Long boardId, int pageSize) throws Exception;
	
	/**
	 * 单板块讨论的更多列表，置顶显示在前
	 * 
	 */
	public List<V3xBbsArticle> listArticleByBoardId(Long boardId, String condition, String textfield, String textfield1) throws Exception;
	
	/**
	 * 方法描述：获取版块的我能访问的主题数
	 * 
	 * @return Map<Long, Integer> key - 板块Id value - 主题数
	 */
	public Map<Long, Integer> getBoardsArticleNumber(boolean isGroup);
	/**
	 * 自定义单位:获取版块的我能访问的主题数
	 * @param spaceId
	 * @param spaceType
	 * @param isElite 是否为精华帖
	 * @return
	 */
	public Map<Long, Integer> getCustomBoardsArticleNumber(long spaceId, int spaceType, boolean isElite);
	
	/**
	 * 管理员查看其可以管理的讨论版块时，查看每个版块的帖子总数，无需自己在发布范围内
	 */
	public Map<Long, Integer> getBoardsArticleNumber4Admin(List<Long> adminBoardIds);
	
	/**
	 * 管理员查看其可以管理的讨论版块时，查看每个版块的精华帖总数，无需自己在发布范围内
	 */
	public Map<Long, Integer> getBoardsElitePostNumber4Admin(List<Long> adminBoardIds);
	
	/**
	 * 方法描述：获取部门版块的我能访问的主题数
	 * 
	 * @return Map<Long, Integer> key - 板块Id value - 主题数
	 */
	public Map<Long, Integer> getDeptBoardsArticleNumber(boolean isDept);
	
	/**
	 * 方法描述：获取版块的精华帖数
	 *
	 * @return Map<Long, Integer> key - 板块Id value - 进化数
	 */
	public Map<Long, Integer> getBoardsElitePostNumber(boolean isGroup);
	/**
	 * 方法描述：获取部门版块的精华帖数
	 *
	 * @return Map<Long, Integer> key - 板块Id value - 进化数
	 */
	public Map<Long, Integer> getDeptBoardsElitePostNumber();
		
	/**
	 * 方法描述：获取版块的所有主题的回复数
	 * @return Map<Long, Integer> key - 板块Id value - 回复数
	 */
	public Map<Long, Integer> getBoardsReplyNumber(boolean isGroup);
	/**
	 * 方法描述：获取部门版块的所有主题的回复数
	 * @return Map<Long, Integer> key - 板块Id value - 回复数
	 */
	public Map<Long, Integer> getDeptBoardsReplyNumber();
	/**
	 * 方法描述：判断讨论区某一版块今天是否有新的主题
	 * 
	 * @return Boolean
	 * @throws Exception 
	 */
	public Boolean hasNewTodayArticle(Long boardId) throws Exception;
	
	/**
	 * 方法描述：判断讨论区某一版块今天是否有回复信息
	 * 
	 * @return Boolean 
	 * @throws Exception 
	 */
	public Boolean hasNewTodayReplyPost(Long boardId) throws Exception ;

	/**
	 * 方法描述：新建讨论区主题
	 * 
	 * @param v3xBbsArtile  主题信息
	 * @throws Exception
	 */
	public void createArticle(V3xBbsArticle v3xBbsArtile) throws Exception ;
	/**
	 * 方法描述：修改讨论区主题
	 * 
	 * @param v3xBbsArtile  主题信息
	 * @throws Exception
	 */
	public void updateArticle(V3xBbsArticle v3xBbsArtile) throws Exception ;
	
	/**
	 * 方法描述：修改讨论区主题下面的回复
	 * added by Meng Yang 2009-05-07
	 * @param v3xBbsArticleReply  主题下面的回复
	 * @throws Exception
	 */
	public void updateArticleReply(V3xBbsArticleReply v3xBbsArticleReply) throws Exception ;
	
	/**
	 * 方法描述：回复讨论区主题
	 * 
	 * @param v3xBbsArtile  主题信息
	 * @throws Exception
	 */
	public void replyArticle(V3xBbsArticleReply v3xBbsArticleReply, int oldReplyNumber) throws Exception;
	
	/**
	 * 创建讨论回复，同时更新主贴的回复总数、点击总数
	 */
	public void replyArticle(V3xBbsArticleReply v3xBbsArticleReply, int oldReplyNumber, int clickNumber)
		throws Exception;

	/**
	 * 方法描述：根据主题ID查询该帖信息
	 * 
	 * @return V3xBbsArticle
	 * @throws Exception
	 *
	 */
	public V3xBbsArticle getArticleById(Long articleid)throws Exception;
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticleReply> listReplyByArticleId(Long articleId) throws Exception;
	
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息  按条数抽取
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticleReply> listReplyByArticleId(Long articleId , int beginRow , int pageSize,String orderValue) throws Exception;
	
	
	/**
	 * 方法描述：获取讨论区某一主题的回复信息总数
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception 
	 */
	public int countReplyByArticleId(Long articleId) throws Exception;
	
	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return V3xBbsArticleReply
	 * @throws Exception
	 *
	 */
	public V3xBbsArticleReply getReplyPostById(Long postId)throws Exception;
	
	/**
	 * 方法描述：逻辑删除主题信息,将主题的state设置为1（为删除状态）
	 *
	 * @param articleId 主题Id
	 * @throws Exception
	 */
	public void deleteArticle(Long articleId) throws Exception;
	
	/**
	 * 方法描述：逻辑删除某一条回复帖信息，,将回复帖的state设置为1（为删除状态）
	 *
	 * @param replyPostId 回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId, Long articleId) throws Exception;
	
	/**
	 * 方法描述：逻辑删除某一主题下的所有回复帖信息
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
	public void updateClickNumber(Long articleId, int oldClickNumber)throws Exception;
	
	//添加发布范围
	public void addArticleIssueArea(List<V3xBbsArticleIssueArea> list) throws Exception;
//	添加发布范围
	public void addArticleIssueArea(Long articleId, String moduleType, Long moduleId);
	
	//获取发布范围
	public List<V3xBbsArticleIssueArea> getIssueArea(Long articleId) throws Exception ;
	
	/**
	 * 方法描述：获取部门讨论区所有版块的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception 
	 */
	public List<V3xBbsArticle> deptlistAllArticle(long departmentId, String condition, String textfield, String textfield1)throws Exception;
	
	//发贴统计
	public List<BbsCountArticle> countArticle(String countType,String departmentid,Long boardId) throws Exception;
	
	/**
	 * 获取匿名发帖统计结果，包含按照日、周、月和全部的统计结果
	 * @param anonymousList
	 */
	public AnonymousCountModel getAnonymousCount4Statistic(List<BbsCountArticle> anonymousList);

	// 置顶主题
	public void topArticle(String articleId)throws Exception;
	
	// 取消置顶主题
	public void cancelTopArticle(String articleId)throws Exception;
	
	/**
	 * 将管理员选中的讨论主题取消置顶
	 * @param articleIds
	 */
	public void cancelTopArticle(String[] articleIds);
	
	/**
	 * 将管理员选中的讨论主题置顶
	 * @param articleIds
	 */
	public void topArticle(String[] articleIds);
	
	/**
	 * 将管理员选中的讨论主题标识为精华帖
	 * @param articleIds
	 */
	public void eliteArticle(String[] articleIds);
	
	/**
	 * 将管理员选中的讨论主题取消精华标识
	 * @param articleIds
	 */
	public void cancelEliteArticle(String[] articleIds);
	
	// 精华主题
	public void eliteArticle(String articleId)throws Exception;
	
	// 取消精华主题
	public void cancelEliteArticle(String articleId)throws Exception;
	
	// 删除主题
	public void delArticle(String articleId) throws Exception;
	
	/**
	 * 综合查询
	 */
	public List<V3xBbsArticle> iSearch(ConditionModel cModel) throws Exception;
	/**
     * 方法描述：获取单位主页显示的讨论区新主题 供web services调用
     * add by Yongzhang ,2009-02-21
     * @return List
     */
	public List<V3xBbsArticle> queryArticleListToWS(long accountId,long personId,int pageSize, String condition, String textfield , String textfield1 );
	/**
	 * 删除某个讨论的所有发布范围
	 * @param id
	 */
	public void deleteArticleIssueAreasByArticleId(Long articleId);
}