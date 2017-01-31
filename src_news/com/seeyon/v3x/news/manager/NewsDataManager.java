package com.seeyon.v3x.news.manager;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.domain.NewsBody;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsRead;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.util.NewsDataLock;
import com.seeyon.v3x.util.cache.UpdateClickManager;
/**
 * 新闻最重要的Manager的接口。包括了新闻发起员、新闻审核员、新闻管理员、普通用户的操作。
 * @author wolf
 *
 */
public interface NewsDataManager extends UpdateClickManager {

	/*//	对新闻基本信息加上一个文件锁
	Map<Long, NewsDataLock> newsdataLockMap=new HashMap<Long, NewsDataLock>();
	*/
	/**
	 * 新闻发起员保存新闻
	 * @param data
	 * @return
	 */
	public NewsData save(NewsData data, boolean isNew);
	/**
	 * 自定义单位新闻发起员保存新闻
	 * @param data
	 * @param isNew
	 * @return
	 */
	public NewsData saveCustomNews(NewsData data, boolean isNew);
	/**
	 * 直接保存
	 */
	public void updateDirect(NewsData data);
	
	/**
	 * 新闻管理员删除新闻。只做标记！
	 * @param id
	 */
	public void delete(Long id);
	
	/**
	 * 新闻发起员实际删除新闻
	 * @param id
	 * @throws BusinessException
	 */
	public void deleteReal(Long id) throws BusinessException;
	
	/**
	 * 删除一个类型下的所有新闻
	 */
	public void deleteRealOfType(long typeId) throws BusinessException;
	
	/**
	 * 新闻管理员批量删除新闻
	 * @param ids
	 */
	public void deletes(List<Long> ids);
	
	/**
	 * 新闻管理员查询所有可管理的新闻
	 * @return
	 * @throws NewsException
	 * @throws Exception 
	 */
	public List<NewsData> findAll() throws NewsException, Exception;
	
	/**
	 * 新闻管理员根据新闻的某一属性查询所有可管理的新闻
	 * @param property
	 * @param value
	 * @return
	 * @throws NewsException
	 * @throws Exception 
	 */
	public List<NewsData> findByProperty(String property,Object value) throws NewsException, Exception;
	
	/**
	 * 根据Id获取新闻
	 * @param id
	 * @return
	 */
	public NewsData getById(Long id);
	
	/**
	 * 获取某个新闻管理员可以管理的新闻类型
	 * @param managerUserId
	 * @param isIgnoreUsed 是否忽略该新闻类型是否启用。true 不处理是否启用字段；false 返回启用的新闻类型
	 * @return
	 * @throws Exception 
	 */
	public List<NewsType> getTypeList(Long managerUserId,boolean isIgnoreUsed) throws Exception;
	public List<NewsType> getTypeList(Long managerUserId,boolean isIgnoreUsed,long loginAccount) throws Exception;
	public List<NewsType> getTypeList(Long managerUserId, int spaceType, long spaceId) throws Exception;
	
	public NewsTypeManager getNewsTypeManager();

	public NewsReadManager getNewsReadManager();

	public NewsTemplateManager getNewsTemplateManager();
	
	public NewsLogManager getNewsLogManager();
	
	/**
	 * 返回新闻审核员可以审核的新闻列表
	 * @param userId
	 * @param property
	 * @param value
	 * @return
	 * @throws NewsException
	 */
	public List<NewsData> getAuditList(Long userId,String property,Object value,long loginAccount) throws NewsException;
	
	/**
	 * 
	 */
	public List<NewsData> getAuditDataListNew(Long userId,String property,Object value, int spaceType) throws NewsException;

	
	
//	public List<NewsData> getAuditGroupList(Long userId,String property,Object value) throws NewsException;
	/**
	 * 返回所有新闻类型
	 * @return
	 */
	public List<NewsType> getAllTypeList();
	public List<NewsType> getAllTypeList(long loginAccount);
	/**
	 * 返回某个自定义空间的所有新闻类型
	 * @param loginAccount
	 * @param spaceType
	 * @return
	 */
	public List<NewsType> getAllTypeList(long loginAccount, String spaceType);
	/**
	 * 返回所有集团新闻类型
	 * @return
	 */
	public List<NewsType> getGroupAllTypeList();
	
	/**
	 * 返回当前用户可以阅读的所有图片新闻或焦点新闻
	 */
	public List<NewsData> findByReadUser4ImageNews(Long userId, long loginAccount, boolean isInternal, Integer imageOrFocus, int spaceType) throws DataAccessException, NewsException;

	/**
	 * 返回当前用户可以阅读的所有图片新闻或焦点新闻（指定板块）
	 */
	public List<NewsData> findByReadUser4ImageNews(User user, Integer imageOrFocus, int spaceType, List<Long> typeIds) throws DataAccessException, NewsException;
	
	/**
	 * 返回当前用户可以阅读的所有新闻
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> findByReadUser(long id, List<NewsType> typeList,long loginAccount, Integer imageOrFocus) throws DataAccessException, NewsException;
	
	/**
	 * 政务【我的提醒】查【单位新闻】总数 (由findByReadUser改造) wangjingjing
	 * 返回当前用户可以阅读的所有新闻
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public Long findByReadUserCount(long id, List<NewsType> typeList,long loginAccount, Integer imageOrFocus) throws DataAccessException, NewsException;
	/**
	 * 返回当前用户可以阅读的所有集团新闻
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> groupFindByReadUser(long id, List<NewsType> typeList, Integer imageOrFocus) throws DataAccessException, NewsException;
	
	/**
	 * 返回首页显示的当前用户可以阅读的所有新闻
	 */
	public List<NewsData> findByReadUserForIndex(Long userId, long loginAccount, boolean isInternal) throws DataAccessException, NewsException;

	/**
	 * 返回首页显示的当前用户可以阅读的所有新闻（指定板块）
	 */
	public List<NewsData> findByReadUserForIndex(Long userId, long loginAccount, boolean isInternal, List<Long> typeIds) throws DataAccessException, NewsException;

	/**
	 * 返回自定义单位/集团首页显示的当前用户可以阅读的所有新闻
	 * @param userId
	 * @param loginAccount
	 * @param spaceType
	 * @param isInternal
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> findCustomByReadUserForIndex(Long userId, long loginAccount, int spaceType, boolean isInternal) throws DataAccessException, NewsException;
	
	/**
	 * 返回集团空间首页显示的当前用户可以阅读的所有新闻
	 */
	public List<NewsData> groupFindByReadUserForIndex(long id, boolean isInternal) throws DataAccessException, NewsException;

	/**
	 * 返回集团空间首页显示的当前用户可以阅读的所有新闻（指定板块）
	 */
	public List<NewsData> groupFindByReadUserForIndex(long id, boolean isInternal, List<Long> typeIds) throws DataAccessException, NewsException;
	
	/**
	 * 返回当前用户可以阅读的新闻，property=value
	 * @param id
	 * @param property
	 * @param value
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> findByReadUser(long id,String property,Object value, List<NewsType> typeList,long loginAccount) throws DataAccessException, NewsException;
	/**
	 * 返回当前用户可以阅读的自定义单位新闻，property=value
	 * @param id
	 * @param property
	 * @param value
	 * @param typeList
	 * @param loginAccount
	 * @param spaceType
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> findByReadUser(long id, String property, Object value, List<NewsType> typeList,long loginAccount, String spaceType) throws DataAccessException, NewsException;
	/**
	 * 返回当前用户可以阅读的集团新闻，property=value
	 * @param id
	 * @param property
	 * @param value
	 * @return
	 * @throws DataAccessException
	 * @throws NewsException
	 */
	public List<NewsData> groupFindByReadUser(long id,String property,Object value, List<NewsType> typeList) throws DataAccessException, NewsException;
	
	/**
	 * 新闻发起员可以处理的新闻，property=value
	 * @param condition
	 * @param value
	 * @return
	 * @throws NewsException
	 */
	public List<NewsData> findWriteByProperty(String condition, Object value) throws NewsException;

	/**
	 * 新闻发起员可以处理的所有新闻
	 * @return
	 * @throws NewsException
	 */
	public List<NewsData> findWriteAll() throws NewsException;
	
	/**
	 * 获取某个新闻发起员可以管理的新闻类型
	 * @param writeId 发起者用户ID
	 * @param isIgnoreUsed 是否忽略该新闻类型是否启用。true 不处理是否启用字段；false 返回启用的新闻类型
	 * @return
	 */
	public List<NewsType> getTypeListByWrite(Long writeId,boolean isIgnoreUsed);
	
	
//	/**
//	 * 对新闻进行置顶操作
//	 * @param ids 要置顶的新闻列表
//	 */
//	public void top(List<Long> ids);
	
	/**
	 * 根据ID获取新闻，并设置某用户阅读该新闻的阅读情况
	 * @param id
	 * @param userId
	 * @return
	 */
	public NewsData getById(Long id,Long userId);
	
	/**
	 * 根据用户ID获取某新闻的阅读情况统计列表。只有系统管理员和该新闻所属类型的新闻管理员可以查看阅读统计情况
	 * @param data
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public List<NewsRead> getReadListByData(NewsData data,Long userId) throws Exception;
	
	/**
	 * 新闻管理员查看新闻的统计情况
	 * @param type
	 * @return
	 */
//	public List statistics(String type ,final String groupSign);
	public List statistics(String searchType, final long newsTypeId);


	public void pigeonhole(List<Long> idList);

	public List<NewsData> findByProperty(Long typeId, String condition, Object value,long userid) throws Exception;

	public List<NewsData> findAll(Long typeId,long userid) throws Exception;
	
	public List<NewsData> findAllWithOutFilter(Long typeId) throws Exception;
	public int findAllWithOutFilterTotal(Long typeId) throws Exception;
	/**
	 * 得到新闻版块下的所有新闻
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public List<NewsData> getNewsByTypeId(final Long typeId) throws Exception;

	public List<NewsData> findByReadUser(Long userId, Long typeId, String condition, Object value) throws NewsException;

	public List<NewsData> findByReadUser(Long userId, Long typeId) throws NewsException;
	public List<NewsData> findByReadUser(Long userId, Long typeId,long loginAccount) throws NewsException;

	public List<NewsData> findWriteByProperty(Long typeId, String condition, Object value) throws NewsException;

	public List<NewsData> findWriteAll(Long typeId,long userId) throws NewsException;

	public List<NewsType> getManagerGroupBulType(Long managerUserId,boolean isIgnoreUsed)throws Exception;
	
	/**
	 * 更新审核时候操作
	 * @param summaryId
	 * @param columns
	 */
	public void update(Long Id, Map<String, Object> columns);
	
	/**
	 * 初始化
	 */
	public void init();
	
	/**
	 * 判斷某條數據是否存在
	 */
	public boolean dataExist(Long bulId);
	
	/**
	 * 单位空间是否显示管理按钮
	 */
	public boolean showManagerMenu(long memberId);
	public boolean showManagerMenuOfLoginAccount(long memberId);
	/**
	 * 自定义单位/集团空间是否显示管理按钮
	 * @param memberId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 */
	public boolean showManagerMenuOfCustomSpace(long memberId, long spaceId, int spaceType);
	
	public List<NewsData> findByReadUser4Section(Long userId, Long typeId) throws NewsException;
	public List<NewsData> findByReadUser4Mobile(long id,String property,Object value) throws DataAccessException, NewsException;	
	public List<NewsData> findByReadUser4Mobile(long id,String property,Object value,long loginAccount) throws DataAccessException, NewsException;

	/**
	 * 判断某个审核员是否有未审核事项
	 */
	public boolean hasPendingOfUser(Long userId, Long... typeIds);
	
	/**
	 * 
	 */
	public Map<Long, List<NewsData>> findByReadUserHome(long id, List<NewsType> typeList) throws DataAccessException,
	NewsException ;
	
	/**
	 * 
	 */
	/**
	 * 审核员对某个类型的待办总数
	 */
	public int getPendingCountOfUser(Long userId, int spaceType);
	
	/**
	 * 得到状态
	 */
	public int getStateOfData(long id);
	/**
	 * 确认新闻的类型存在,并且启用
	 * @param typeId
	 * @return
	 */
	public boolean typeExist(long typeId);
	
	/**
	 * 
	 */
	public boolean isManagerOfType(long typeId, long userId);
	
	/**
	 * 综合查询
	 */
	public List<NewsData> iSearch(ConditionModel cModel,long loginAccount);
	
	/**
	 * 取正文
	 */
	public NewsBody getBody(long newsDataId);
	
	
	public int readOneTime(long dataId);
	
	/**
	 * 协同转发新闻
	 * @throws BusinessException 
	 */
	public void saveCollNews(NewsData data) throws BusinessException;
	
	/**
	 * 检验文件中否加锁
	 * NewsDataLock:为空表示文件已加锁,不能再访问了
	 * NewsDataLock:不为空表示文件还没有加锁,可以访问,并进行加锁
	 * action表示当前的动作
	 */
	public NewsDataLock lock(Long newdatasid,String action);
	
	/**
	 * 检验文件中否加锁
	 * NewsDataLock:为空表示文件已加锁,不能再访问了
	 * NewsDataLock:不为空表示文件还没有加锁,可以访问,并进行加锁
	 * action表示当前的动作
	 */
	public NewsDataLock lock(Long newsid, Long userid, String action);
	
	/**
	 * 对新闻进行解锁
	 */
	public void unlock(Long newdatasid);
	
	public Map<Long, NewsDataLock> getLockInfo4Dump();
	
	/**
	 * 该板块存在待审核新闻，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核新闻转给新的审核员
	 * @param newsTypeId   对应的新闻板块ID
	 * @param oldAuditorId 旧审核员ID(对应人员已不可用)
	 * @param newAuditorId 新审核员ID
	 */
	public void transferWait4AuditBulDatas2NewAuditor(Long newsTypeId, Long oldAuditorId, Long auditUser);
	/**
	 * 根据附件ID取正文
	 */
	public NewsBody getBodyByFileId(String fileid);
	/**
	 * 获取某单位下的所有自定义空间新闻版块
	 */
	public List<NewsType> getAllCustomTypeList(long loginAccount) throws BusinessException;
}