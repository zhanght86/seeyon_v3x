package com.seeyon.v3x.bulletin.manager;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.dao.BulBodyDao;
import com.seeyon.v3x.bulletin.dao.BulDataDao;
import com.seeyon.v3x.bulletin.dao.BulPublishScopeDao;
import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulPublishScope;
import com.seeyon.v3x.bulletin.domain.BulRead;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.util.BulDataLock;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.bulletin.util.hql.BulletinHqlUtils;
import com.seeyon.v3x.bulletin.util.hql.PageInfo;
import com.seeyon.v3x.bulletin.util.hql.SearchInfo;
import com.seeyon.v3x.bulletin.util.hql.SearchType;
import com.seeyon.v3x.bulletin.util.hql.TypeInfo;
import com.seeyon.v3x.bulletin.util.hql.UserInfo;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.cache.ClickDetail;

/**
 * 公告最重要的Manager的实现类。包括了公告发起员、公告审核员、公告管理员、普通用户的操作
 */
public class BulDataManagerImpl extends BaseBulletinManager implements BulDataManager, IndexEnable {
	private static final Log log = LogFactory.getLog(BulDataManagerImpl.class);
	
	private BulDataDao bulDataDao;
	private BulPublishScopeDao bulPublishScopeDao;
	private AttachmentManager attachmentManager;
	private BulBodyDao bulBodyDao;	
	private FileManager fileManager;
	private OrgManager orgManager;	
	private IndexManager indexManager;	
	private SpaceManager spaceManager;
	/** 公告编辑、审核时操作加锁  */
	private Map<Long, BulDataLock> buldataLockMap;
	private PartitionManager partitionManager;
	private BulTypeManager bulTypeManager;
	private BulReadManager bulReadManager;

	/** 真实删除公告以及对应的Office正文、附件、 正文内容、发布范围、阅读信息  */
	public void deleteReal(Long bulDataId) throws BusinessException {
		// 删除正文
		BulData data = this.getById(bulDataId);
		try {
			if (Constants.getMSAndWPSTypes().contains(data.getDataFormat())) {
				fileManager.deleteFile(data.getId(), data.getCreateDate(), true);
			}
		} catch (BusinessException e) {
			log.error("", e);
			throw e;
		}

		// 删除附件
		try {
			attachmentManager.deleteByReference(data.getId(), data.getId());
		} catch (BusinessException e) {
			log.error("", e);
			throw e;
		}
		
		// 删除与公告对应的正文内容、发布范围、阅读信息及公告自身
		this.bulBodyDao.delete(new Object[][]{{"bulDataId", bulDataId}});
		this.bulPublishScopeDao.delete(new Object[][]{{"bulDataId", bulDataId}});
		this.bulReadManager.deleteReadByData(data);		
		this.bulDataDao.delete(bulDataId.longValue());
	}
	
	/** 批量逻辑删除：将删除标识标记为true，并非真实删除   */
	public void deletes(List<Long> ids) {
		this.bulDataDao.delete(ids);
	}

	/**
	 * 按主键ID获取公告，并对其进行初始化操作
	 */
	public BulData getById(Long id) {
		BulData data = bulDataDao.get(id);
		if(data != null) {
			this.getBulletinUtils().initData(data);
		}
		return data;
	}
	
	/**
	 * 与上面方法内容完全一致，为保持兼容暂不删除
	 */
	public BulData getById(Long id, Long userId) {
		return this.getById(id);
	}
	
	/**
	 * 取得对应公告被阅读的次数
	 * @param bulDataId 公告ID
	 */
	public int getReadCount(long bulDataId){
		BulData data = bulDataDao.get(bulDataId);
		if(data == null || data.getReadCount() == null)
			return 0;
		else
			return data.getReadCount();
	}
	
	/**
	 * 判断公告是否有效存在
	 * @param bulId 公告ID
	 */
	public boolean dataExist(Long bulId){
		return this.getById(bulId) != null;
	}
	/**
	 * 查询公告板块下的所有公告
	 * @param typeId 公告板块ID
	 */
	public List<BulData> searchBulDatas(Long typeId){
		 List<BulData> l= bulDataDao.getBulDatas(typeId);
		 return l;
	}
	
	/**
	 * 保存公告，也包括保存正文、公告发布范围等操作
	 */
	public BulData save(BulData data, boolean isNew) {
		data.setKeywords("");
		data.setBrief("");
		Long typeId = data.getTypeId();
		BulType type = null;
		if(typeId != null) {
			 type = this.bulTypeManager.getById(typeId);
			 data.setType(type);
		}
		
		if (isNew) {
			if(type != null) {
				data.setAccountId(type.getAccountId());
			}
			else {
				data.setAccountId(CurrentUser.get().getLoginAccount());
			}
			bulDataDao.save(data);
		} else {
			bulDataDao.update(data);
		}
		
		this.bulBodyDao.saveBody(data, isNew);
		this.bulPublishScopeDao.savePublishScope(data, isNew);	
		return data;
	}
	
	public BulData saveCustomBul(BulData data, boolean isNew) {
		data.setKeywords("");
		data.setBrief("");
		Long typeId = data.getTypeId();
		BulType type = null;
		if(typeId != null) {
			 type = this.bulTypeManager.getById(typeId);
			 data.setType(type);
		}
		if (isNew) {
			bulDataDao.save(data);
		} else {
			bulDataDao.update(data);
		}
		this.bulBodyDao.saveBody(data, isNew);
		this.bulPublishScopeDao.savePublishScope(data, isNew);	
		return data;
	}
	
	/** 持久化对公告的修改 */
	public void updateDirect(BulData data) {
		bulDataDao.update(data);
	}	
	
	public void update(Long bulDataId, Map<String, Object> columns) {
		try {
			bulDataDao.update(bulDataId, columns);
		} catch(Exception e){
			log.error("", e);
		}
	}

	/**
	 * 获取用户在某一单位下可以管理的所有公告板块，并设置每个公告板块的公告总数
	 */
	public List<BulType> getTypeList(Long managerUserId, boolean isIgnoreUsed) throws Exception {
		List<BulType> list = this.bulTypeManager.getManagerTypeByMember(managerUserId, Constants.BulTypeSpaceType.corporation, CurrentUser.get().getLoginAccount());
		bulTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	public List<BulType> getTypeList(Long managerUserId, long spaceId, int spaceType) throws Exception {
		List<BulType> list = this.bulTypeManager.getManagerTypeByMember(managerUserId, spaceType == 5 ? Constants.BulTypeSpaceType.public_custom : Constants.BulTypeSpaceType.public_custom_group, spaceId);
		bulTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	/**
	 * 获取用户可以管理的所有公告板块
	 */
	public List<BulType> getTypeListOnlyByMemberId(Long managerUserId, boolean isIgnoreUsed) throws Exception {
		List<BulType> list = this.bulTypeManager.getManagerTypeByMember(managerUserId, Constants.BulTypeSpaceType.corporation, null);
		return list;
	}
	
	/**
	 * 获取用户可以管理的所有<b>集团</b>公告板块，并设置每个公告板块的公告总数
	 */
	public List<BulType> getManagerGroupBulType(Long managerUserId, boolean isIgnoreUsed) throws Exception {
		List<BulType> list = this.bulTypeManager.getManagerTypeByMember(managerUserId, Constants.BulTypeSpaceType.group, null);
		bulTypeManager.setTotalItemsOfType(list);
		return list;
	}

	/**
	 * 获取用户具有公告发起权限的所有公告板块
	 */
	public List<BulType> getTypeListByWrite(Long writeId, boolean isIgnoreUsed) {
		return this.bulTypeManager.getWriterTypeByMember(writeId, null, null);
	}

	/** 集团空间下待用户审核的公告列表  */
	public List<BulData> getGroupAuditList(Long userId, String property, Object value) throws BulletinException {
		return this.getAuditDataListNew(userId, property, value, Constants.BulTypeSpaceType.group.ordinal());
	}
	
	/** 单位空间下待用户审核的公告列表，也可用于集团空间下的情况   */
	public List<BulData> getAuditDataListNew(Long userId, String property, Object value, int spaceType) throws BulletinException {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.Auditor, userId);
		Long accountId = BulletinUtils.getAccountId(spaceType, orgManager);
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.valueOfSpaceType(spaceType));
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(property, value);
		
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	public List<BulData> getAuditDataListNew(Long userId, String property, Object value, int spaceType, long spaceId) throws BulletinException {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.Auditor, userId);
		TypeInfo typeInfo = new TypeInfo(null, spaceId, Constants.valueOfSpaceType(spaceType));
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(property, value);
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/**
	 * 此接口<b>不再用于单位最新公告栏目：显示最新发布的8条公告</b>。参考：{@link #findByReadUserForIndex(User)}<br>
	 * 此接口保留，以便V3xInterfaceImpl工程对其的调用可以保持，用于获取单位空间中用户可以访问的公告
	 * 但<b>存在隐患</b>，只按照用户ID获取domainIds时，未区分内部或外部人员
	 * @see com.seeyon.oainterface.impl.exportdata.DataConvertUtils#convertBulletinByRecent
	 */
	public List<BulData> findByReadUserForIndex(long currentUserId, long accountId, Boolean needCount) throws DataAccessException, BulletinException {
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(currentUserId, orgManager);
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.BulTypeSpaceType.corporation);
		PageInfo pageInfo = new PageInfo(needCount);
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, null, pageInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/**
	 * 用于获取各种类型公告栏目所需的最新(配置显示条数)条公告，包括：
	 * 单位最新公告、集团最新公告、部门公告、我的公告、单版块单位公告、单版块集团公告栏目
	 * @param user			当前用户
	 * @param typeId		对应板块ID：仅单版块公告栏目或部门公告栏目此项有效，为板块ID或部门ID
	 * @param accountId		单位或集团ID
	 * @param spaceType		所属空间类型：部门、单位、集团、无
	 */
	private List<BulData> findBulletins4Section(User user, Long typeId, Long accountId, Constants.BulTypeSpaceType spaceType, int count) {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, user.getId());
		//单版块公告栏目，区分管理员(无需发布范围匹配)和普通用户(需要发布范围匹配)
		if(typeId != null && spaceType != Constants.BulTypeSpaceType.department && spaceType != Constants.BulTypeSpaceType.custom) {
			boolean isAdmin = this.bulTypeManager.isManagerOfType(typeId, user.getId());
			if(isAdmin)
				userInfo.setAdminAsUser(true);
			else
				userInfo.setDomainIds(CommonTools.getUserDomainIds(user, orgManager));
		} 
		else {
			userInfo.setDomainIds(CommonTools.getUserDomainIds(user, orgManager));
		}
		
		TypeInfo typeInfo = new TypeInfo(typeId, accountId, spaceType);
		PageInfo pageInfo = new PageInfo(false, 0, count);
		
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, null ,pageInfo, bulDataDao);
		if(typeId != null || spaceType != Constants.BulTypeSpaceType.department) {
			this.getBulletinUtils().initList(list);
		}
		return list;
	}
	
	public List<BulData> findByReadUserForIndex(User user, int count) {
		return this.findBulletins4Section(user, null, user.getLoginAccount(), Constants.BulTypeSpaceType.corporation, count);
	}
	
	@SuppressWarnings("unchecked")
	public List<BulData> findByReadUserForIndex(User user, int count, List<Long> typeList, BulTypeSpaceType spaceType, SearchInfo searchInfo) {
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuilder select = new StringBuilder("select " + BulletinHqlUtils.Hql_Selected_Fields);
        StringBuilder from = new StringBuilder(" from ");
        StringBuilder where = new StringBuilder(" where ");
        StringBuilder orderBy = new StringBuilder(" order by ");

        from.append(BulData.class.getName() + " as t_bul_data "
                + " left join t_bul_data.bulReads as t_bul_read with t_bul_read.managerId=:userId ");
        
        if (!spaceType.equals(SpaceType.public_custom_group) && !spaceType.equals(SpaceType.public_custom)) {
            where.append(" t_bul_data.accountId=:accountId and ");
            Long accountId = BulletinUtils.getAccountId(spaceType.ordinal(), orgManager);
            params.put("accountId", accountId);
        }
        where.append(" t_bul_data.id in(select s.bulDataId from " + BulPublishScope.class.getName() + " as s where s.bulDataId=t_bul_data.id ");
        where.append(" and (t_bul_data.createUser=:userId or t_bul_data.auditUserId=:userId or t_bul_data.publishUserId=:userId or s.userId in(:userDomainIds))) ");
        if (typeList != null) {
            where.append(" and t_bul_data.typeId in(:typeList)");
            params.put("typeList", typeList);
        }
        params.put("userId", user.getId());
        where.append(" and t_bul_data.state=:published and t_bul_data.deletedFlag=false ");
        params.put("published", Constants.DATA_STATE_ALREADY_PUBLISH);
        //params.put("userDomainIds", CommonTools.getUserDomainIds(user, orgManager));
        params.put("userDomainIds", CommonTools.getUserDomainIds(user.getId(), orgManager));

        orderBy.append(" t_bul_data.publishDate desc");

        BulletinHqlUtils.handleSearchInfo(searchInfo, from, where, params);

        if (count != -1) {
            Pagination.setNeedCount(false);
            Pagination.setFirstResult(0);
            Pagination.setMaxResults(count);
        }

        List<Object[]> objs = bulDataDao
                .find(select.toString() + from + where + orderBy, "t_bul_data.id", true, params);
        List<BulData> list = BulletinHqlUtils.parseObjArrs2BulDatas(objs, false, bulDataDao);
        this.getBulletinUtils().initList(list);
        return list;
	}

	public List<BulData> findCustomByReadUserForIndex(User user, long spaceId, int spaceType, int count) {
		if (spaceType == 5) {
			return this.findBulletins4Section(user, null, spaceId, Constants.BulTypeSpaceType.public_custom, count);
		} else {
			return this.findBulletins4Section(user, null, spaceId, Constants.BulTypeSpaceType.public_custom_group, count);
		}
	}
	
	public List<BulData> groupFindByReadUserForIndex(User user, int count) throws DataAccessException, BusinessException {
		Long groupId = BulletinUtils.getAccountId(Constants.BulTypeSpaceType.group.ordinal(), orgManager);
		return this.findBulletins4Section(user, null, groupId, Constants.BulTypeSpaceType.group, count);
	}
	
	/**
	 * 首页 - 部门空间 - 部门公告栏目，显示该部门下最新发布的8条部门公告
	 */
	public List<BulData> deptFindByReadUserForIndex(long departmentId, User user) throws DataAccessException, BulletinException {
		return this.findBulletins4Section(user, departmentId, null, Constants.BulTypeSpaceType.department, Constants.SECTION_TABLE_COLUMNS);
	}
	
	/**
	 * 首页 - 特定空间 - 特定空间公告栏目，显示该空间下最新发布的8条空间公告
	 */
	public List<BulData> spaceFindByReadUserForIndex(long spaceId, User user, int spaceType, int count) throws DataAccessException, BulletinException {
		switch (spaceType) {
		case 4: 
			return this.findBulletins4Section(user, spaceId, null, Constants.BulTypeSpaceType.custom, count);
		case 5:
			return this.findBulletins4Section(user, spaceId, null, Constants.BulTypeSpaceType.public_custom, count);
		default:
			return this.findBulletins4Section(user, spaceId, null, Constants.BulTypeSpaceType.public_custom_group, count);
		}
	}
	
	/**
	 * 首页 - 个人空间-单版块公告栏目（获取某一个公告板块作为空间栏目出现），显示该板块下最新8条公告
	 */
	public List<BulData> findByReadUser4Section(User user, long typeId, int count) throws DataAccessException, BulletinException {		
		return this.findBulletins4Section(user, typeId, null, null, count);
	}
	
	/**
	 * 获取单位空间下面、按照指定日期区间进行查询的用户可以访问的公告列表
	 * 此接口保留，以便V3xInterfaceImpl工程对其的调用可以保持
	 * 但<b>存在隐患</b>，只按照用户ID获取domainIds时，未区分内部或外部人员
	 * @see com.seeyon.oainterface.impl.exportdata.DataConvertUtils#convertBulletinByDateTime
	 */
	public List<BulData> findByReadUserByDateTime(long currentUserId, long accountId, 
			String beginDateTime, String endDateTime, Boolean needCount) throws BusinessException {
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(currentUserId, orgManager);
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.BulTypeSpaceType.corporation);
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(SearchType.By_Publish_Date.value(), beginDateTime, endDateTime);
		PageInfo pageInfo = new PageInfo(needCount);
		
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, pageInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/**
	 * 单位/集团空间-单位/集团最新公告-更多
	 * @param user			当前用户
	 * @param accountId 	单位或集团ID
	 * @param spaceType 	空间类型：单位、集团
	 * @param searchInfo 	搜索信息
	 */
	public List<BulData> find4UserInAccount(User user, long accountId, int spaceType, SearchInfo searchInfo) throws DataAccessException, BulletinException {
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(user, orgManager);
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.valueOfSpaceType(spaceType));
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/**
	 * 政务【我的提醒】查【单位公告】总数 (根据find4UserInAccount改造) wangjingjing
	 * 单位/集团空间-单位/集团最新公告-更多
	 * @param user			当前用户
	 * @param accountId 	单位或集团ID
	 * @param spaceType 	空间类型：单位、集团
	 * @param searchInfo 	搜索信息
	 */
	public Long find4UserInAccountCount(User user, long accountId, int spaceType, SearchInfo searchInfo) throws DataAccessException, BulletinException {
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(user, orgManager);
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.valueOfSpaceType(spaceType));
		return BulletinHqlUtils.findBulDatasCount(userInfo, typeInfo, searchInfo, bulDataDao);
	}
	
	/**
	 * 单击单位/集团公告:首页时执行的方法.查出每个类型下对应的所有公告,只显示前六条.
	 * @param typeList 单位或集团全部公告板块，传入时已经进行了校验，不会为空
	 */
	public Map<Long, List<BulData>> findByReadUserHome(long userId, List<BulType> typeList) throws DataAccessException, BulletinException {
		Map<Long, List<BulData>> result = new HashMap<Long, List<BulData>>();
		
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, userId);
		userInfo.setDomainIds(CommonTools.getUserDomainIds(orgManager));
		
		for(BulType type : typeList) {
			Long typeId = type.getId();
			boolean isManager = this.bulTypeManager.isManagerOfType(typeId, userId);
			userInfo.setAdminAsUser(isManager);
			TypeInfo typeInfo = new TypeInfo(typeId);
			PageInfo pageInfo = new PageInfo(false, 0, Constants.BUL_HOMEPAGE_TABLE_COLUMNS);
			List<BulData> bulletins = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, null, pageInfo, bulDataDao);
			
			result.put(typeId, bulletins);
		}
		return result;
	}
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
	public List<BulData> searchInfoByUserAccount(long userId, long accountId, String keyword, int type,int spaceType) throws DataAccessException, BulletinException {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, userId);
		userInfo.setDomainIds(CommonTools.getUserDomainIds(orgManager));
		TypeInfo typeInfo = new TypeInfo(null, accountId, Constants.valueOfSpaceType(spaceType));
		SearchInfo searchInfo=new SearchInfo();
		switch(type)
		{
		case 1:
			searchInfo.setTitle(keyword);
			break;
		case 2:
			searchInfo.setBeginDate(parseDate(keyword.split(";")[0], "yyyy-MM-dd HH:mm:ss"));
			searchInfo.setEndDate(parseDate(keyword.split(";")[1], "yyyy-MM-dd HH:mm:ss"));
			break;
		case 3:
			searchInfo.setCreatorName(keyword);
			break;
		default:
			searchInfo=null;
		}
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, null, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	/**
	 * 获取当前用户可以访问的所有公告
	 * @param user	   当前用户
	 * @param searchInfo   用户输入的搜索信息
	 * @param forSection   是否用于首页栏目显示
	 */
	public List<BulData> findMyBulDatas(User user, SearchInfo searchInfo, boolean forSection) {
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(user, orgManager);
		TypeInfo typeInfo = new TypeInfo(Constants.BulTypeSpaceType.none);
		PageInfo pageInfo = forSection ? new PageInfo(false, 0, Constants.SECTION_TABLE_COLUMNS) : null;
		
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, pageInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/**
	 * 部门空间-部门公告-更多及对应的查询
	 * @param departmentId	部门ID
	 * @param userId		用户ID
	 * @param searchInfo	搜索信息
	 */
	public List<BulData> deptFindByReadUser(long departmentId, long userId, SearchInfo searchInfo) {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, userId);
		TypeInfo typeInfo = new TypeInfo(departmentId, null, Constants.BulTypeSpaceType.department);
		return BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
	}
	
	/**
	 * 获取某一个板块下的所有公告，支持按照公告属性进行搜索
	 * @param typeId	公告板块ID
	 * @param searchInfo	用户输入的搜索信息
	 */
	public List<BulData> findByReadUserByType(Long typeId, SearchInfo searchInfo) throws DataAccessException, BulletinException {
		User user = CurrentUser.get();
		boolean isAdmin = this.bulTypeManager.isManagerOfType(typeId, user.getId());
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, user.getId(), CommonTools.getUserDomainIds(user, orgManager), isAdmin);
		TypeInfo typeInfo = new TypeInfo(typeId);
		return BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
	}

	/** 查询点击集团公告显示的列表 groupIndex方法  */	
	public List<BulType> groupAllBoardList() throws DataAccessException, BulletinException {
		List<BulType> typeList = this.bulTypeManager.groupFindAll();
		if (typeList == null || typeList.isEmpty()) {
			return new ArrayList<BulType>();
		}
		return typeList;
	}
	
	/**
	 * 公告发起人：从单位公告列表页面点击公告发布按钮进入后看到的列表,也适用于集团公告、部门公告
	 */
	public List<BulData> findWriteAll(Long typeId, Long userId) throws BulletinException {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.Poster, userId);
		TypeInfo typeInfo = new TypeInfo(typeId);
		return BulletinHqlUtils.findBulDatas(userInfo, typeInfo, bulDataDao);
	}

	/**
	 * 将选中的公告置顶
	 */
	public void top(List<Long> ids) throws BusinessException {
		//TODO 此处的算法待优化，生成的update sql语句过多
		for (Long id : ids) {
			BulData data = this.getById(id);
			
			if (data.getState() == Constants.DATA_STATE_ALREADY_PIGEONHOLE) {
				throw new BusinessException("");
			}
			// 发布后才能置顶
			if (data.getState() != Constants.DATA_STATE_ALREADY_PUBLISH)
				continue;

			Byte topCount = data.getType().getTopCount();
			// 对以前置顶的公告次序均减1，直至为0
			List<BulData> dataList = this.bulDataDao.getTopedBulDatas(data.getTypeId());
			Byte currentOrder = topCount;
			if(CollectionUtils.isNotEmpty(dataList)) {
				for (BulData topData : dataList) {
					do {
						topData.setTopOrder(Byte.valueOf(String.valueOf(topData.getTopOrder() - 1)));
					} while ((topData.getTopOrder() >= currentOrder));
					currentOrder = Byte.valueOf(String.valueOf(currentOrder - 1));
					this.bulDataDao.save(topData);
				}
			}

			// 设置当前置顶次序为该公告类型的最大置顶个数
			data.setTopOrder(topCount);
			this.bulDataDao.save(data);
		}
	}
	
	/**
	 * 对公告进行取消置顶操作
	 * @param ids 要取消置顶的公告列表
	 */
	public void cancelTop(List<Long> ids) {
		String hql = "update " + BulData.class.getName() + " as b set b.topOrder=:noTop where b.id in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("noTop", Byte.valueOf("0"));
		params.put("ids", ids);
		this.bulDataDao.bulkUpdate(hql, params);
	}
	
	public List<BulRead> getReadListByData(Long bulletinId) {
		return this.bulReadManager.getReadListByData(bulletinId);
	}
	

	@Deprecated
	public List<BulRead> getReadListByData(BulData data, Long userId) throws Exception {
//		List<BulRead> readList = null;
//
//		boolean isManager = false;
//		if(userId.longValue() == data.getCreateUser().longValue())
//			isManager = true;
//		else
//			isManager = this.bulTypeManager.isManagerOfType(data.getTypeId(), userId);
//
//		if (isManager) {
//			readList = this.bulReadManager.getReadListByData(data);
//			for (BulRead read : readList) {
//				if (read.getManagerId() != null) {
//					read.setManagerName(this.getBulletinUtils().getMemberNameByUserId(read.getManagerId()));
//				}
//			}
//		}
//		return readList;
		return this.bulReadManager.getReadListByData(data.getId());
	}
	
	/** 按照不同统计类型，获取公告统计结果  */
	public List<Object[]> statistics(String type, final long bulTypeId) {
		return this.bulDataDao.getStatisticInfo(type, bulTypeId);
	}

	/** 管理员批量归档已发布的公告 */
	public void pigeonhole(List<Long> ids) {
		for (Long id : ids) {
			BulData data = this.getById(id);
			if (data.getState() != Constants.DATA_STATE_ALREADY_PIGEONHOLE) {
				data.setState(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
				data.setTopOrder(Byte.valueOf("0"));
				this.updateDirect(data);
			}
			
			// 删除全文检索
			try {
				IndexManager indexManager = (IndexManager)ApplicationContextHolder.getBean("indexManager");
				indexManager.deleteFromIndex(ApplicationCategoryEnum.bulletin, id);
			} catch (IOException e) {
				log.error("从indexManager删除检索项。", e);
			}
		}
	}
	
	/**
	 * 公告管理员查询所有可管理的公告:板块管理页面点击板块名称进入
	 */
	public List<BulData> findAll4Manager(Long typeId, SearchInfo searchInfo) throws BulletinException, Exception {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.Admin);
		TypeInfo typeInfo = new TypeInfo(typeId);
		
		return BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
	}
	
	/**
	 * 获取当前用户登录单位的所有单位公告板块
	 */
	public List<BulType> getAllTypeListExcludeDept() {
		List<BulType> typeList = this.bulTypeManager.boardFindAll();
		List<BulType> result = new ArrayList<BulType>();
		for (BulType type : typeList) {
			if(type.getAccountId().longValue() == CurrentUser.get().getLoginAccount() && 
					type.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal()) {
				result.add(type);
			}
		}
		return result;
	}
	/**
	 * 获取当前用户登录单位的所有自定义空间公告板块（自定义单位空间/自定义集团空间）
	 */
	public List<BulType> getCustomAllTypeList() throws BusinessException{
		List<BulType> typeList = this.bulTypeManager.boardFindAllCustom();
		Collections.sort(typeList);
		return typeList;
	}
	/**
	 * 获取当前用户登录自定义单位的所有单位公告板块
	 */
	public List<BulType> getAllTypeListOfCustom(long spaceId, int spaceType) {
		List<BulType> typeList = this.bulTypeManager.customAccBoardAllBySpaceId(spaceId, spaceType);
		return typeList;
	}

	/** 
	 * 全文检索所需要的方法，根据Id取得信息，组织成为IndexInfo<br>
	 * <b>部门的有待日后添加</b>　
	 */
	public IndexInfo getIndexInfo(long id) throws Exception {
		BulData bulData = getById(id);
		if (bulData == null){
			log.warn("ID为"+id+"的公告不存在");
			return null;
		}
		
		bulData.setContent(this.getBody(id).getContent());
		String createUserName = "";
		Long startMemberId=-1l;
		if(bulData.getPublishUserId() != null){
			createUserName = this.orgManager.getMemberById(bulData.getPublishUserId()).getName();
			startMemberId=bulData.getPublishUserId();
		}
		
		IndexInfo indexInfo = new IndexInfo();
		indexInfo.setTitle(bulData.getTitle());
		indexInfo.setStartMemberId(startMemberId);
		indexInfo.setHasAttachment(bulData.getAttachmentsFlag());
		indexInfo.setTypeId(bulData.getTypeId());
		indexInfo.setEntityID(bulData.getId());
	
		indexInfo.setAppType(ApplicationCategoryEnum.bulletin);
		indexInfo.setCreateDate(bulData.getCreateDate());
		
		String formatType = bulData.getDataFormat();
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(formatType)){
			indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			indexInfo.setContent(bulData.getContent());
		} 
		else {
			Partition partition = partitionManager.getPartition(bulData.getCreateDate(), true);
			String contentPath = this.fileManager.getFolder(bulData.getCreateDate(), false);
			if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WORD);
			}else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_XLS);
			}else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			}else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			}else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_PDF);
			}
			indexInfo.setContentID(Long.parseLong(bulData.getContent()));
			indexInfo.setContentAreaId(partition.getId().toString());
			indexInfo.setContentPath(contentPath.substring(contentPath.length() -11 )+System.getProperty("file.separator"));
		}		
		
		indexInfo.setAuthor(createUserName);
		// 在此处理字符串
		String scope = bulData.getPublishScope();
		String[] scopes = StringUtils.split(scope, ",");
		List<String> ownerList = new ArrayList<String>();
		List<String> departmentList = new ArrayList<String>();
		List<String> accountList = new ArrayList<String>();
		List<String> postList = new ArrayList<String>();
		
		ownerList.add(bulData.getCreateUser().toString());// 创建者在此加入，管理员也加入
		String ids = bulTypeManager.getById(bulData.getTypeId()).getManagerUserIds();
		String[] managers = StringUtils.split(ids, ",");
		for (String managerId : managers) {
			ownerList.add(managerId);
		}
		StringBuilder sb=new StringBuilder();
		for (String scopeStr : scopes) {
			if (scopeStr.startsWith("Member|")) {
				int point = scopeStr.indexOf("|");
				String idStr = scopeStr.substring(point + 1);
				V3xOrgMember member=orgManager.getMemberById(Long.parseLong(idStr));
				if(member==null){continue;}
				ownerList.add(idStr);
				sb.append(" "+member.getName());
			} else if (scopeStr.startsWith("Department|")) {
				int point = scopeStr.indexOf("|");
				String idStr = scopeStr.substring(point + 1);
				try {
					Long pid = Long.parseLong(idStr);// 如果有子部门则加入它
					List<V3xOrgDepartment> departments = orgManager
							.getChildDepartments(pid, false);
					if (departments != null) {
						for (V3xOrgDepartment department : departments) {
							Boolean isInternal = department.getIsInternal();
							if (!isInternal) {
								continue;
							}
							if(department==null){continue;}
							departmentList.add(department.getId().toString());
							sb.append(" "+department.getName());
						}
					}
				} catch (Exception e) {
					 log.error("公告全文检索出错", e);
				}
				departmentList.add(idStr);
			}else if(scopeStr.startsWith("Account|")){
				int point = scopeStr.indexOf("|");
				String idStr = scopeStr.substring(point + 1);
				//判断是否是集团
				V3xOrgAccount account = orgManager.getAccountById(Long.parseLong(idStr));
				if(account==null){continue;}
				if(account.getIsRoot())
				{
					ownerList.add("ALL");
				}
				else
				{
					accountList.add(idStr);
				}
					sb.append(" "+account.getName());
			}else if(scopeStr.startsWith("Post|")){
				int point = scopeStr.indexOf("|");
				String idStr = scopeStr.substring(point + 1);
				V3xOrgPost post=orgManager.getPostById(Long.parseLong(idStr));
				if(post==null){continue;}
				postList.add(idStr);
				sb.append(" "+post.getName());
			}
		}	
		String createDepartment=bulData.getPublishDeptName();
	if(createDepartment==null)
	{
		if(bulData.getPublishUserId() != null){
			createDepartment=orgManager.getDepartmentById(bulData.getPublishUserId()).getName();
		}
	}
	    sb.append(" "+createDepartment);
		indexInfo.setKeyword(bulData.getKeywords()+sb.toString());

		AuthorizationInfo authorizationInfo = new AuthorizationInfo();
		if (ownerList.size() > 0)
			authorizationInfo.setOwner(ownerList);
		if (departmentList.size() > 0)
			authorizationInfo.setDepartment(departmentList);
		if (accountList.size() > 0)
			authorizationInfo.setAccount(accountList);
		if (postList.size() > 0)
			authorizationInfo.setPost(postList);
		
		indexInfo.setAuthorizationInfo(authorizationInfo);
		IndexUtil.convertToAccessory(indexInfo);
		return indexInfo;
	}
	
	/**
	 * 获得某个公告板块下已经置顶的记录个数
	 * @param typeId 公告板块ID
	 */
	public int getTopedCount(final long typeId){
		return this.bulDataDao.getTopedCount(typeId);
	}
	
	/**
	 * 获取某个版块下面已经置顶的公告
	 * @param bulTypeId
	 */
	public List<BulData> getTopedBulDatas(Long bulTypeId) {
		return this.bulDataDao.getTopedBulDatas(bulTypeId);
	}
	
	/**
	 * 更新某条公告的置顶数目
	 * @param bulDataId		公告ID
	 * @param newTopOrder	新的置顶数
	 */
	public void updateTopOrder(Long bulDataId, Byte newTopOrder) {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("topOrder", newTopOrder);
		this.bulDataDao.update(bulDataId, columns);
	}
	
	/**
	 * 在公告板块的置顶总个数变化之后，对已经置顶的公告进行处理，使其置顶状态与板块的置顶总数保持一致
	 * @param oldTopCountStr  旧的板块置顶总数
	 * @param newTopCountStr  新的板块置顶总数
	 * @param bulTypeId       公告板块ID
	 */
	public void updateTopOrder(String oldTopCountStr, String newTopCountStr, Long bulTypeId) {
		if(!oldTopCountStr.equals(newTopCountStr)) {
	    	int newTopCount = Integer.valueOf(newTopCountStr);
	    	List<BulData> topedBulDatas = this.getTopedBulDatas(bulTypeId);
	    	if(topedBulDatas!=null && topedBulDatas.size()>0) {
	    		int topOrderNum = newTopCount;
	    		for(int i=0; i<topedBulDatas.size(); i++) {
	    			BulData bulData = topedBulDatas.get(i);
	    			
	    			if(i <= newTopCount-1) {
	        			this.updateTopOrder(bulData.getId(), Byte.valueOf(topOrderNum + ""));
	        			topOrderNum--;
	    			} 
	    			else {
	            		this.updateTopOrder(bulData.getId(), Byte.valueOf(0 + ""));
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * 单位空间是否显示管理按钮
	 */
	public boolean showManagerMenu(long memberId){
		List<BulType> types = this.bulTypeManager.getAuditUnitBulTypeOnlyByMember(memberId);
		if(CollectionUtils.isNotEmpty(types)) {
			return true;
		}
		else {
			try {
				types = this.getTypeListOnlyByMemberId(memberId, true);
			} catch (Exception e) {
				log.error("", e);
			}
			return CollectionUtils.isNotEmpty(types);
		}
	}
	
	/**
	 * 在用户所登录的单位空间是否显示管理按钮?
	 */
	public boolean showManagerMenuOfLoginAccount(long memberId){
		List<BulType> types = this.bulTypeManager.getAuditUnitBulType(memberId);
		if(CollectionUtils.isNotEmpty(types)) {
			return true;
		}
		else {
			try {
				types = this.getTypeList(memberId, true);
			} catch (Exception e) {
				log.error("", e);
			}
			return CollectionUtils.isNotEmpty(types);
		}
	}
	
	/**
	 * 在用户所登录的自定义单位/集团空间是否显示管理按钮
	 */
	public boolean showManagerMenuOfCustomSpace(long memberId, long spaceId, int spaceType) {
		List<BulType> types = this.bulTypeManager.getAuditUnitBulType(memberId, spaceId, spaceType);
		if(CollectionUtils.isNotEmpty(types)) {
			return true;
		}
		else {
			try {
				types = this.getTypeList(memberId, spaceId, spaceType);
			} catch (Exception e) {
				log.error("", e);
			}
			return CollectionUtils.isNotEmpty(types);
		}
	}
	
	/**
	 * 判断某个审核员是否有未审核事项
	 */
	public boolean hasPendingOfUser(Long userId, Long... typeIds){
		List<BulData> list = this.getPendingData(userId, typeIds);
		return CollectionUtils.isNotEmpty(list);
	}
	
	/**
	 * 得到某个用户需要审核的数据
	 */
	@SuppressWarnings("unchecked")
	public List<BulData> getPendingData(Long userId, Long... typeIds){
		if(userId == null)
			return null;
		
		List<BulType> auditTypes = this.bulTypeManager.getAuditTypeByMember(userId, null, null);
		Set<Long> idset = null;
		if(auditTypes == null || auditTypes.size() == 0)
			return new ArrayList<BulData>();
		else
			idset = BulletinUtils.getIdSet(auditTypes);
		
		Set<Long> set = new HashSet<Long>();		
		if(typeIds != null && typeIds.length > 0){
			for(Long id : typeIds){
				if(idset.contains(id))
					set.add(id);
			}			
		}else{
			set = idset;
		}
		String hql = "from BulData as t where t.typeId in (:ids) and t.state=:state";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", set);
		params.put("state", Constants.DATA_STATE_ALREADY_CREATE);
		return this.bulDataDao.find(hql, params);		
	}
	
	/**
	 * 该板块存在待审核公告，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核公告转给新的审核员
	 * @param bulTypeId    对应的公告板块ID
	 * @param oldAuditorId 旧审核员ID(对应人员已不可用)
	 * @param newAuditorId 新审核员ID
	 */
	public void transferWait4AuditBulDatas2NewAuditor(Long bulTypeId, Long oldAuditorId, Long newAuditorId) {
		this.bulDataDao.transfer2NewAuditor(bulTypeId, oldAuditorId, newAuditorId);
	}
	
	/**
	 * 得到状态
	 */
	public int getStateOfData(long id){
		BulData data = this.getById(id);
		return data == null ? 0 : data.getState();
	}
	
	/**
	 * 判断公告板块审核员是否可用
	 * @param typeId
	 * @return
	 */
	public boolean isAuditUserEnabled(Long typeId) throws Exception {
		BulType type = this.bulTypeManager.getById(typeId);
		V3xOrgMember auditUser = this.orgManager.getMemberById(type.getAuditUser());
		if(auditUser != null){
			if(!auditUser.getEnabled() || auditUser.getIsDeleted()){
				return false;
			}
		}
		return true;
	}
	
	public boolean typeExist(long typeId){
		BulType type = this.bulTypeManager.getById(typeId);
		
		if(type == null){			
			try {
				V3xOrgDepartment dept = orgManager.getDepartmentById(typeId);
				if(dept == null) {
					return false;
				} else {
					super.getBulletinUtils().createBulTypeByDept(dept.getName(), dept.getId(), dept.getOrgAccountId());
					return true;
				}
			} catch (BusinessException e) {
				return false;
			}

		} else {
			return (type.isUsedFlag());
		}
	}
	
	/** 综合查询  */
	public List<BulData> iSearch(ConditionModel cModel) {	
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(cModel, orgManager);
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(cModel);
		TypeInfo typeInfo = new TypeInfo(Constants.BulTypeSpaceType.none);
		
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	
	/** 取正文  */
	public BulBody getBody(long bulDataId){
		return this.bulBodyDao.getByDataId(bulDataId);
	}
	/** 根据附件ID取正文  */
	public BulBody getBodyByFileId(String fileId){
		return this.bulBodyDao.getByFileId(fileId);
	}
	/**
	 * 协同转公告
	 */
	public void saveCollBulletion(BulData data) throws BusinessException {
		//板块类型
		BulType type = data.getType();
		if(type == null){
			Long typeId = data.getTypeId();
			type = this.bulTypeManager.getById(typeId);
			data.setType(type);
		}
		//状态
		if(data.getState() == null){
			data.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
		}
		
		boolean isNew = true;		
		data.setExt1("1");
		this.save(data, isNew);//保存
		
		//全文检索
		Boolean firstIndexFlag=true;
		try {
			if(data.getState()==Constants.DATA_STATE_ALREADY_PUBLISH){
				if(firstIndexFlag) {
					IndexInfo indexInfo=this.getIndexInfo(data.getId());
					indexManager.index(indexInfo);
				}
			}
		} catch (Exception e) {
			log.error("全文检索：", e);
		}
	}

	/**
	 * 更新公告的被点击次数
	 */
	public void updateClick(long dataId, int clickNumTotal, Collection<ClickDetail> details) {
		String hql = "update BulData set readCount = ? where id = ?";
		this.bulDataDao.bulkUpdate(hql, null, clickNumTotal, dataId);
	}
	
	/**
	 * 获取公告模块的锁操作信息，用于前端展现、测试之用
	 */
	public Map<Long, BulDataLock> getLockInfo4Dump() {
		return this.buldataLockMap;
	}
	
	/**
	 * 编辑或审核时加锁，保证操作同步性
	 */
	public BulDataLock lock(Long buldatasid, String action) {
		return this.lock(buldatasid, CurrentUser.get().getId(), action);
	}
	
	/**
	 * 编辑或审核时加锁，保证操作同步性
	 */
	public BulDataLock lock(Long buldatasid, Long currentUserId, String action) {
		//进行文件锁的检查,方件锁是接口中的一个对象是不会抛空指针的
		BulDataLock bullock = null;
		if(this.buldataLockMap == null)
			this.buldataLockMap = new HashMap<Long, BulDataLock>();
		
		if(this.buldataLockMap.containsKey(buldatasid)) {
			//文件已加锁
			bullock = this.buldataLockMap.get(buldatasid);
			/**
			 * 如果操作类型相同，且锁的对象与当前用户相同，也允许用户继续进行同一操作
			 * 仅当两种不同操作同时在进行时，锁才确定生效，比如同一人进行编辑和审核操作，或者两人分别进行编辑或审核操作
			 */
			if(bullock.getUserid() == currentUserId && action.equals(bullock.getAction()))
				return null;
			return bullock;
		} 
		else {
			//文件没有加锁,对其加锁,继续进行相关的操作
			bullock = new BulDataLock();
			bullock.setNewsid(buldatasid);
			bullock.setUserid(currentUserId);
			bullock.setAction(action);
			this.buldataLockMap.put(buldatasid, bullock);
			//发送通知
			NotificationManager.getInstance().send(NotificationType.BulletinLock, bullock);
			return null;
		}
	}
	
	/**
	 * 编辑或审核操作完成之后进行解锁，让他人可以对该公告继续进行操作
	 */
	public void unlock(Long buldatasid) {
		if(this.buldataLockMap == null) {
			this.buldataLockMap = new HashMap<Long, BulDataLock>();
		}
		if(this.buldataLockMap.containsKey(buldatasid)) {
			this.buldataLockMap.remove(buldatasid);
			// 发送通知
			NotificationManager.getInstance().send(NotificationType.BulletinUnLock, buldatasid);
		}
	}	

	/** 获取公告信息，用户AJAX调用?此方法有隐患... */
	public String getBulData(String bulTypeId) throws BusinessException{
		BulData bean = new BulData();
		User user = CurrentUser.get();
		bean.setCreateDate(new Timestamp(System.currentTimeMillis()));
		bean.setCreateUser(user.getId());
		bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		bean.setReadCount(0);
		BulType type = bulTypeManager.getById(Long.valueOf(bulTypeId));
		bean.setTypeId(Long.valueOf(bulTypeId));
		bean.setType(type);
		bean.setTypeName(type.getTypeName());
		String publisthScopeDep2 = "";
		String publisthScopeDepName = "";
		// 对部门公告的发布范围单独处理,需要加上本部门,部门访问者,
		if (type.getSpaceType().intValue() == Constants.BulTypeSpaceType.department.ordinal()) {
			StringBuffer publisthScopeDep = new StringBuffer();
			publisthScopeDep = publisthScopeDep.append("Department|" + type.getId().toString());
			List<Object[]> _issueAreas = this.spaceManager.getSecuityOfDepartment(type.getId());
			for (Object[] objects : _issueAreas) {
				if (V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equalsIgnoreCase(objects[0] + "")
						&& type.getId() != ((Long) objects[1]).longValue()) {
					publisthScopeDep = publisthScopeDep.append(",Department|" + objects[1]);
				} else if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equalsIgnoreCase(objects[0] + "")) {
					// 人员的ID是当前部门的,应该去掉
					if (orgManager.getMemberById((Long) objects[1]).getOrgDepartmentId() != user.getDepartmentId()) {
						publisthScopeDep = publisthScopeDep.append(",Member|" + objects[1]);
					}
				} else if (V3xOrgEntity.ORGENT_TYPE_TEAM.equalsIgnoreCase(objects[0] + "")) {
					publisthScopeDep = publisthScopeDep.append(",Team|" + objects[1]);
				}
			}
			publisthScopeDep2 = publisthScopeDep.toString();
			String[] arryDepartment = publisthScopeDep2.split("\\|");
			try{
				publisthScopeDepName = orgManager.getDepartmentById(Long.valueOf(arryDepartment[1].split(",")[0])).getName();
			}catch(Exception e){
				log.error("", e);
			}
		}
		
		Long depId2 = null;
		// 设置发布部门
		if (bean.getPublishDepartmentId() == null) {
			// 设置为发起者所在部门
			Long userId = bean.getCreateUser();
			Long depId = this.getBulletinUtils().getMemberById(userId).getOrgDepartmentId();
			depId2 = depId;
			bean.setPublishDepartmentId(depId);
		}
		
		return String.valueOf(type.getId())+";"+type.getTypeName()+";"+String.valueOf(new Timestamp(System.currentTimeMillis()))+";"+
			   String.valueOf(user.getId())+";"+String.valueOf(Constants.DATA_STATE_NO_SUBMIT)+";"+String.valueOf(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML)+";"+
			   "0"+";"+bulTypeId+";"+publisthScopeDep2+";"+publisthScopeDepName+";"+String.valueOf(depId2);
	
	}
	
	/**
	 * 查看某个公告板块下是否存在未归档、不是暂存并且没有删除的公告
	 * @param typeId
	 * @return
	 */
	public int findAllWithOutFilterTotal(Long typeId) {
		return this.bulDataDao.findAllWithOutFilterTotal(typeId);
	}
	/**
     * 格式化日期
     * @param dateStr 字符型日期
     * @param format 格式
     * @return 返回日期
     */
    public static java.util.Date parseDate(String dateStr, String format) {
        java.util.Date date = null;
        try {
            String dt = dateStr.replaceAll("-", "/");
            if ((!dt.equals("")) && (dt.length() < format.length())) {
                dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]",
                        "0");
            }
            date = Datetimes.parse(dt, format);
        } catch (Exception e) {
        }
        return date;
    }
    
    public List<BulData> findListBulDatas(User user,int spaceType, SearchInfo searchInfo)
	{
		UserInfo userInfo = BulletinHqlUtils.getUserInfo(user, orgManager);
		TypeInfo typeInfo = new TypeInfo(Constants.valueOfSpaceType(spaceType));
		typeInfo.setAccountId(user.getAccountId());
		typeInfo.setTypeId(searchInfo.getBulTypeId());
		List<BulData> list = BulletinHqlUtils.findBulDatas(userInfo, typeInfo, searchInfo, null, bulDataDao);
		this.getBulletinUtils().initList(list);
		return list;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}
	public void setBulBodyDao(BulBodyDao bulBodyDao) {
		this.bulBodyDao = bulBodyDao;
	}
	public void setBulPublishScopeDao(BulPublishScopeDao bulPublishScopeDao) {
		this.bulPublishScopeDao = bulPublishScopeDao;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setBulDataDao(BulDataDao bulDataDao) {
		this.bulDataDao = bulDataDao;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public void setBulReadManager(BulReadManager bulReadManager) {
		this.bulReadManager = bulReadManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

}