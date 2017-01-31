package com.seeyon.v3x.bulletin.manager;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulRead;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.util.BulDataLock;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.bulletin.util.hql.SearchInfo;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.util.cache.UpdateClickManager;
/**
 * 公告最重要的Manager的接口。包括了公告发起员、公告审核员、公告管理员、普通用户的操作。
 * @author wolf
 * @editor <a href="mailto:yangm@seeyon.com">Rookie Young</a>
 */
public interface BulDataManager extends UpdateClickManager {

	public List<BulData> searchBulDatas(Long typeId);
	
	/** 真实删除公告以及对应的Office正文、附件、 正文内容、发布范围、阅读信息 */
	public void deleteReal(Long bulDataId) throws BusinessException;

	/** 批量逻辑删除：将删除标识标记为true，并非真实删除 */
	public void deletes(List<Long> ids);

	/**
	 * 按主键ID获取公告，并对其进行初始化操作
	 */
	public BulData getById(Long id);

	/**
	 * 与上面方法内容完全一致，为保持兼容暂不删除
	 */
	public BulData getById(Long id, Long userId);

	/**
	 * 取得对应公告被阅读的次数
	 * @param bulDataId 公告ID
	 */
	public int getReadCount(long bulDataId);

	/**
	 * 判断公告是否有效存在
	 * @param bulId 公告ID
	 */
	public boolean dataExist(Long bulId);

	/**
	 * 保存公告，也包括保存正文、公告发布范围等操作
	 */
	public BulData save(BulData data, boolean isNew);
	/**
	 * 自定义单位保存公告，也包括保存正文、公告发布范围等操作
	 * @param data
	 * @param isNew
	 * @return
	 */
	public BulData saveCustomBul(BulData data, boolean isNew);

	/** 持久化对公告的修改 */
	public void updateDirect(BulData data);

	public void update(Long bulDataId, Map<String, Object> columns);

	/**
	 * 获取用户在某一单位下可以管理的所有公告板块，并设置每个公告板块的公告总数
	 */
	public List<BulType> getTypeList(Long managerUserId, boolean isIgnoreUsed) throws Exception;
	/**
	 * 获取用户在某一自定义单位下可以管理的所有公告板块，并设置每个公告板块的公告总数
	 * @param managerUserId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 * @throws Exception
	 */
	public List<BulType> getTypeList(Long managerUserId, long spaceId, int spaceType) throws Exception;
	/**
	 * 获取用户可以管理的所有公告板块
	 */
	public List<BulType> getTypeListOnlyByMemberId(Long managerUserId, boolean isIgnoreUsed) throws Exception;

	/**
	 * 获取用户可以管理的所有<b>集团</b>公告板块，并设置每个公告板块的公告总数
	 */
	public List<BulType> getManagerGroupBulType(Long managerUserId, boolean isIgnoreUsed) throws Exception;

	/**
	 * 获取用户具有公告发起权限的所有公告板块
	 */
	public List<BulType> getTypeListByWrite(Long writeId, boolean isIgnoreUsed);

	/** 集团空间下待用户审核的公告列表 */
	public List<BulData> getGroupAuditList(Long userId, String property, Object value) throws BulletinException;

	/** 单位空间下待用户审核的公告列表，也可用于集团空间下的情况 */
	public List<BulData> getAuditDataListNew(Long userId, String property, Object value, int spaceType) throws BulletinException;
	/**
	 * 自定义单位空间下待用户审核的公告列表，也可用于自定义集团空间下的情况
	 * @param userId
	 * @param property
	 * @param value
	 * @param spaceType
	 * @param spaceId
	 * @return
	 * @throws BulletinException
	 */
	public List<BulData> getAuditDataListNew(Long userId, String property, Object value, int spaceType, long spaceId) throws BulletinException;
	/**
	 * 此接口<b>不再用于单位最新公告栏目：显示最新发布的8条公告</b>。参考：{@link #findByReadUserForIndex(User)}<br>
	 * 此接口保留，以便V3xInterfaceImpl工程对其的调用可以保持，用于获取单位空间中用户可以访问的公告
	 * 但<b>存在隐患</b>，只按照用户ID获取domainIds时，未区分内部或外部人员
	 * @see com.seeyon.oainterface.impl.exportdata.DataConvertUtils#convertBulletinByRecent
	 */
	public List<BulData> findByReadUserForIndex(long currentUserId, long accountId, Boolean needCount) throws DataAccessException, BulletinException;

	/**
	 * 首页 - 单位空间，单位最新公告栏目：显示最新发布的(配置显示条数)条单位公告
	 */
	public List<BulData> findByReadUserForIndex(User user, int count);

	/**
	 * 首页 - 单位空间，单位最新公告栏目：显示某些板块下最新发布的(配置显示条数)条单位公告
	 */
	public List<BulData> findByReadUserForIndex(User user, int count, List<Long> typeList, BulTypeSpaceType spaceType, SearchInfo searchInfo);
	
	/**
	 * 首页 - 自定义单位/集团空间，单位最新公告栏目：显示最新发布的(配置显示条数)条单位公告
	 * @param user
	 * @param spaceId
	 * @param spaceType
	 * @param count
	 * @return
	 */
	public List<BulData> findCustomByReadUserForIndex(User user, long spaceId, int spaceType, int count);

	/**
	 * 获取单位空间下面、按照指定日期区间进行查询的用户可以访问的公告列表
	 * 此接口保留，以便V3xInterfaceImpl工程对其的调用可以保持
	 * 但<b>存在隐患</b>，只按照用户ID获取domainIds时，未区分内部或外部人员
	 * @see com.seeyon.oainterface.impl.exportdata.DataConvertUtils#convertBulletinByDateTime
	 */
	public List<BulData> findByReadUserByDateTime(long currentUserId, long accountId, String beginDateTime, String endDateTime, Boolean needCount) throws BusinessException;

	/**
	 * 单位/集团空间-单位/集团最新公告-更多
	 * @param user			当前用户
	 * @param accountId 	单位或集团ID
	 * @param spaceType 	空间类型：单位、集团
	 * @param searchInfo 	搜索信息
	 */
	public List<BulData> find4UserInAccount(User user, long accountId, int spaceType, SearchInfo searchInfo) throws DataAccessException, BulletinException;
	/**
	 * 政务【我的提醒】查【单位公告】总数 (根据find4UserInAccount改造) wangjingjing
	 * 单位/集团空间-单位/集团最新公告-更多
	 * @param user			当前用户
	 * @param accountId 	单位或集团ID
	 * @param spaceType 	空间类型：单位、集团 
	 * @param searchInfo 	搜索信息
	 */
	public Long find4UserInAccountCount(User user, long accountId, int spaceType, SearchInfo searchInfo) throws DataAccessException, BulletinException;
	/**
	 * 根据参数查询公告列表
	 * @param userId			当前用户id
	 * @param accountId 	单位或集团ID
	 * @param keyword			搜索条件
	 * @param type		 	搜索类型1=按标题,2=按日期,3=按发起者姓名 
	 * @param spaceType 	空间类型：单位、集团
	 * @param startIndex    查询起始位置
	 * @param rowCount 	    每页显示条数
	 */
	public List<BulData> searchInfoByUserAccount(long userId, long accountId, String keyword, int type,int spaceType)throws DataAccessException, BulletinException;
	/**
	 * 首页-个人空间-单版块公告栏目（获取某一个公告板块作为空间栏目出现），显示该板块下最新8条公告
	 */
	public List<BulData> findByReadUser4Section(User user, long typeId, int count) throws DataAccessException, BulletinException;

	/**
	 * 单击单位/集团公告:首页时执行的方法.查出每个类型下对应的所有公告,只显示前六条.
	 * @param typeList 单位或集团全部公告板块，传入时已经进行了校验，不会为空
	 */
	public Map<Long, List<BulData>> findByReadUserHome(long userId, List<BulType> typeList) throws DataAccessException, BulletinException;

	/**
	 * 获取当前用户可以访问的所有公告
	 * @param user	   当前用户
	 * @param searchInfo   用户输入的搜索信息
	 * @param forSection   是否用于首页栏目显示
	 */
	public List<BulData> findMyBulDatas(User user, SearchInfo searchInfo, boolean forSection);

	/**
	 * 部门空间-部门公告-更多及对应的查询
	 * @param departmentId 		部门ID
	 * @param userId 			用户ID
	 * @param searchInfo 		搜索信息
	 */
	public List<BulData> deptFindByReadUser(long departmentId, long userId, SearchInfo searchInfo);

	/**
	 * 部门空间 - 部门公告栏目，显示该部门下最新8条公告
	 */
	public List<BulData> deptFindByReadUserForIndex(long departmentId, User user) throws DataAccessException, BulletinException;

	/**
	 * 首页 - 特定空间 - 特定空间公告栏目，显示该空间下最新发布的8条空间公告
	 */
	public List<BulData> spaceFindByReadUserForIndex(long departmentId, User user, int spaceType, int count) throws DataAccessException, BulletinException;
	
	/**
	 * 首页空间：集团最新公告栏目，显示最新(配置显示条数)条集团公告
	 */
	public List<BulData> groupFindByReadUserForIndex(User user, int count) throws DataAccessException, BusinessException;

	/**
	 * 获取某一个板块下的所有公告，支持按照公告属性进行搜索
	 * @param typeId  公告板块ID
	 * @param searchInfo  用户输入的搜索信息
	 */
	public List<BulData> findByReadUserByType(Long typeId, SearchInfo searchInfo) throws DataAccessException, BulletinException;

	/** 获取所有集团公告板块 */
	public List<BulType> groupAllBoardList() throws DataAccessException, BulletinException;

	/**
	 * 公告发起人：从单位公告列表页面点击公告发布按钮进入后看到的列表,也适用于集团公告、部门公告
	 */
	public List<BulData> findWriteAll(Long typeId, Long userid) throws BulletinException;

	/**
	 * 将选中的公告置顶
	 */
	public void top(List<Long> ids) throws BusinessException;

	/**
	 * 对公告进行取消置顶操作
	 * @param ids 	要取消置顶的公告列表
	 */
	public void cancelTop(List<Long> ids);

	/**
	 * 获取公告的阅读信息，如果用户是发起人或管理员，则可直接看到所有阅读信息，否则无法看见
	 * @param data 	 	公告
	 * @param userId 	当前用户ID
	 * @deprecated	废弃
	 * @see #getReadListByData(Long)
	 */
	public List<BulRead> getReadListByData(BulData data, Long userId) throws Exception;
	
	/**
	 * 获取公告的所有已阅阅读信息记录
	 * @param bulletinId	公告ID
	 */
	public List<BulRead> getReadListByData(Long bulletinId);

	/** 按照不同统计类型，获取公告统计结果 */
	public List<Object[]> statistics(String type, final long bulTypeId);

	/** 管理员批量归档已发布的公告 */
	public void pigeonhole(List<Long> ids);

	/**
	 * 公告管理员查询所有可管理的公告:板块管理页面点击板块名称进入
	 * @param typeId	公告板块ID
	 * @param searchInfo	搜索信息
	 */
	public List<BulData> findAll4Manager(Long typeId, SearchInfo searchInfo) throws BulletinException, Exception;

	/**
	 * 获取当前用户登录单位的所有单位公告板块
	 */
	public List<BulType> getAllTypeListExcludeDept();
	/**
	 * 获取当前用户登录自定义单位/集团的所有公告板块
	 * @param spaceId 
	 * @param spaceType
	 * @return
	 */
	public List<BulType> getAllTypeListOfCustom(long spaceId, int spaceType);
	/**
	 * 获得某个公告板块下已经置顶的记录个数
	 * @param typeId 	公告板块ID
	 */
	public int getTopedCount(final long typeId);

	/**
	 * 获取某个版块下面已经置顶的公告
	 * @param bulTypeId		公告板块ID
	 */
	public List<BulData> getTopedBulDatas(Long bulTypeId);

	/**
	 * 更新某条公告的置顶数目
	 * @param bulDataId 	公告ID
	 * @param newTopOrder 	新的置顶数
	 */
	public void updateTopOrder(Long bulDataId, Byte newTopOrder);

	/**
	 * 在公告板块的置顶总个数变化之后，对已经置顶的公告进行处理，使其置顶状态与板块的置顶总数保持一致
	 * @param oldTopCountStr 	旧的板块置顶总数
	 * @param newTopCountStr 	新的板块置顶总数
	 * @param bulTypeId 		公告板块ID
	 */
	public void updateTopOrder(String oldTopCountStr, String newTopCountStr, Long bulTypeId);

	/**
	 * 单位空间是否显示管理按钮
	 */
	public boolean showManagerMenu(long memberId);

	/**
	 * 在用户所登录的单位空间是否显示管理按钮?
	 */
	public boolean showManagerMenuOfLoginAccount(long memberId);
	
	/**
	 * 在用户所登录的自定义单位/集团空间是否显示管理按钮
	 * @param memberId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 */
	public boolean showManagerMenuOfCustomSpace(long memberId, long spaceId, int spaceType);

	/**
	 * 判断某个审核员是否有未审核事项
	 */
	public boolean hasPendingOfUser(Long userId, Long... typeIds);

	/**
	 * 得到某个用户需要审核的数据
	 */
	public List<BulData> getPendingData(Long userId, Long... typeIds);

	/**
	 * 该板块存在待审核公告，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核公告转给新的审核员
	 * @param bulTypeId 		对应的公告板块ID
	 * @param oldAuditorId 		旧审核员ID(对应人员已不可用)
	 * @param newAuditorId 		新审核员ID
	 */
	public void transferWait4AuditBulDatas2NewAuditor(Long bulTypeId, Long oldAuditorId, Long newAuditorId);

	/**
	 * 得到状态
	 */
	public int getStateOfData(long bulDataId);

	/**
	 * 判断某一公告板块是否有效、存在
	 * @param typeId	公告板块ID
	 */
	public boolean typeExist(long typeId);

	/** 综合查询 */
	public List<BulData> iSearch(ConditionModel cModel);

	/** 取正文 */
	public BulBody getBody(long bulDataId);

	/**
	 * 协同转公告
	 */
	public void saveCollBulletion(BulData data) throws BusinessException;

	/**
	 * 获取公告模块的锁操作信息，用于前端展现、测试之用
	 */
	public Map<Long, BulDataLock> getLockInfo4Dump();

	/**
	 * 编辑或审核时加锁，保证操作同步性
	 */
	public BulDataLock lock(Long buldatasid, String action);

	/**
	 * 编辑或审核时加锁，保证操作同步性
	 */
	public BulDataLock lock(Long buldatasid, Long currentUserId, String action);

	/**
	 * 编辑或审核操作完成之后进行解锁，让他人可以对该公告继续进行操作
	 */
	public void unlock(Long buldatasid);

	/** 获取公告信息，用户AJAX调用?此方法有隐患... */
	public String getBulData(String bulTypeId) throws BusinessException;
	/**
	 * 根据空间类型和公告类型获取当前用户可以访问的所有公告
	 * @param user	   当前用户
	 * @param spaceType	   空间类型
	 * @param searchInfo   用户输入的搜索信息
	 */
	public List<BulData> findListBulDatas(User user,int spaceType, SearchInfo searchInfo);
	/** 根据附件ID取正文  */
	public BulBody getBodyByFileId(String fileId);
	/**
	 * 获取当前用户登录单位的所有自定义空间公告板块（自定义单位空间/自定义集团空间）
	 */
	public List<BulType> getCustomAllTypeList() throws BusinessException;
}