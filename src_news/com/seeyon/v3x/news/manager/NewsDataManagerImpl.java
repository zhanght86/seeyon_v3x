package com.seeyon.v3x.news.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
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
import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.dao.NewsBodyDao;
import com.seeyon.v3x.news.dao.NewsDataDao;
import com.seeyon.v3x.news.domain.NewsBody;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsRead;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.domain.NewsTypeManagers;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.news.util.NewsDataLock;
import com.seeyon.v3x.news.util.NewsUtils;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.cache.ClickDetail;

/**
 * 新闻最重要的Manager的实现类。包括了新闻发起员、新闻审核员、新闻管理员、普通用户的操作getAllTypeListExcludeDept。
 * @author wolf
 *	对新闻基本信息加上一个文件锁
 */
public class NewsDataManagerImpl extends BaseNewsManager implements NewsDataManager,IndexEnable {
	private NewsDataDao newsDataDao;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	
	private NewsBodyDao newsBodyDao;
	
	private IndexManager indexManager;
	private PartitionManager partitionManager;
	//对新闻基本信息加上一个文件锁
	private Map<Long, NewsDataLock> newsdataLockMap;
	//异步调用模板
	private MtContentTemplateManagerCAP mtContentTemplateManagerCAP;
	private OrgManager orgManager;
	private static final Log log = LogFactory.getLog(NewsDataManagerImpl.class);
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public NewsDataDao getNewsDataDao() {
		return newsDataDao;
	}
	
	public void setNewsDataDao(NewsDataDao newsDataDao) {
		this.newsDataDao = newsDataDao;
	}
	
	NewsTypeManager newsTypeManager;

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}
	
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}
	
	NewsReadManager newsReadManager;

	public NewsReadManager getNewsReadManager() {
		return newsReadManager;
	}

	public void setNewsReadManager(NewsReadManager newsReadManager) {
		this.newsReadManager = newsReadManager;
	}
	
	NewsTemplateManager newsTemplateManager;

	public NewsTemplateManager getNewsTemplateManager() {
		return newsTemplateManager;
	}
	
	public void setNewsTemplateManager(NewsTemplateManager newsTemplateManager) {
		this.newsTemplateManager = newsTemplateManager;
	}
	
	private NewsTypeManagersManager newsTypeManagersManager; 
	
	private NewsLogManager newsLogManager;
	
	
	/**
	 * 初始化新闻 1、初始化发起者姓名 2、初始化新闻是否存在附件标志 3、初始化新闻发布部门的中文名称
	 * @param data
	 */
	private void initData(NewsData data){		
		//创建者
		data.setCreateUserName(this.getNewsUtils().getMemberNameByUserId(data.getCreateUser()));
		
		if(data.getPublishDepartmentId()==null){
			//设置为发起者所在部门
			Long userId=data.getCreateUser();
			Long depId=this.getNewsUtils().getMemberById(userId).getOrgDepartmentId();
			data.setPublishDepartmentId(depId);
		}		
		
		NewsType theType = this.newsTypeManager.getById(data.getTypeId());
		data.setType(theType);
		boolean groupType = false;
		if(theType.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal())
			groupType = true;
		
		data.setPublishDepartmentName(this.getNewsUtils().getDepartmentNameById(data.getPublishDepartmentId(), groupType));
		data.setTypeName(data.getType().getTypeName());
//		initPublishScope(data);
		
		//设置[New]标记
		if((data.getState().intValue()==Constants.DATA_STATE_ALREADY_PUBLISH) 
				&& (data.getTopOrder().byteValue() > Byte.parseByte("0"))){
			int top=data.getType().getTopCount();
			Calendar cal=Calendar.getInstance();
			if(data.getPublishDate()!=null){
				cal.setTime(data.getPublishDate());
				cal.add(Calendar.DAY_OF_MONTH, top);
				if(((new Date()).after(cal.getTime()))){
					data.setTopOrder(Byte.valueOf("0"));
					this.updateDirect(data);
				}
			}
		}
		
		int state = data.getState();
		if(state == Constants.DATA_STATE_ALREADY_CREATE){
			data.setNoDelete(true);
			data.setNoEdit(true);
		}else if(state == Constants.DATA_STATE_ALREADY_AUDIT){
			data.setNoEdit(true);
		}
		
		if(Strings.isNotBlank(data.getBrief()))
			data.setShowBriefArea(true);
		if(Strings.isNotBlank(data.getKeywords()))
			data.setShowKeywordsArea(true);
		
		if(data.getReadCount() == null)
			data.setReadCount(0);
	}
	
	/**
	 * 初始化新闻列表
	 * @param list
	 */
	private void initList(List<NewsData> list){
		for(NewsData data:list){
			initData(data);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#deleteReal(java.lang.Long)
	 */
	public void deleteReal(Long id) throws BusinessException{
		//删除正文
		NewsData data=this.getById(id);
		//如果是已经发布的就提示不能删除
//		if (data.getState() > Constants.DATA_STATE_ALREADY_CREATE) {
//			throw new NewsException("news_type_delAlreadyUsed");
//		}
			
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(data.getDataFormat())
				|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(data.getDataFormat())	
				|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(data.getDataFormat())
				|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(data.getDataFormat())
		){				
			try {
				fileManager.deleteFile(data.getId(), data.getCreateDate(), true);
			} catch (BusinessException e) {
				log.error(e.getMessage(),e);
				throw e;
			}
		}
		
		//删除附件
		
		this.newsBodyDao.deleteByDataId(id);
		
		try {
			attachmentManager.deleteByReference(data.getId(), data.getId());
		} catch (BusinessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
		
		// 
		newsReadManager.deleteReadByData(data);
		
		newsDataDao.delete(id.longValue());
	}
	
	/**
	 * 删除一个类型下的所有新闻
	 */
	public void deleteRealOfType(long typeId) throws BusinessException{
		String hql = " from NewsData where typeId = ?";
		List<NewsData> list = newsDataDao.find(hql, typeId);
		if(list != null && list.size() > 0)
			for(NewsData data : list)
				this.deleteReal(data.getId());
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#delete(java.lang.Long)
	 */
	public void delete(Long id) {
		NewsData data=this.getById(id);
		data.setDeletedFlag(true);
		this.updateDirect(data);

//		NewsLog log=new NewsLog();
//		log.setIdIfNew();
//		log.setRecordDate(new Date());
//		log.setUserId(CurrentUser.get().getId());
//		log.setTableName(NewsData.REF);
//		log.setOperType("delete");
//		log.setRecordId(id);
//		log.setResult(Constants.RESULT_SUCCESS);
//		log.setExt1(data.getType().getTypeName());
//		this.getNewsLogManager().record(log);
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 得到hql
	 * hql目的：查询当前用户创建的未删除的新闻列表
	 * @return [0] " select t_data "
	 *  	   [1] " from NewsData t_data where "
	 *         [2] " state... createUser... deletedFlag... " 当前用户创建、未删除
	 *         [3] " order by t_data.createDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */	
	private Object[] filterByManagerGetHql(String property,Object value) throws NewsException{
		StringBuffer sb = new StringBuffer();
		sb.append(" select t_data2.id, t_data2.title, t_data2.publishScope, t_data2.publishDepartmentId, t_data2.dataFormat, " +
				"t_data2.createDate, t_data2.createUser, t_data2.publishDate, t_data2.publishUserId, t_data2.readCount, " +
				"t_data2.topOrder, t_data2.accountId, t_data2.typeId, t_data2.state, t_data2.attachmentsFlag, t_data2.auditUserId ");
		sb.append(" from NewsData t_data2 where t_data2.id in ( select distinct t_data.id from NewsData as t_data,"+ V3xOrgMember.class.getName() + " as m  where ");//联合人员表查询	
	
		List<NewsType> inList = this.newsTypeManager.getManagerTypeByMember(CurrentUser.get().getId(), null, null);
		HashMap<String,Object> parameterMap = new HashMap<String,Object>();
		List<Long> typeIds = new ArrayList<Long>();
		if (inList == null || inList.size() == 0)
			typeIds.add(new Long(1l));
		else{
			for(NewsType t : inList){
				typeIds.add(t.getId());
			}
		}
		parameterMap.put("typeIds", typeIds);
		
		String hqlType = "";
		String hqlOther = "";
		if (StringUtils.isNotBlank(property) && value != null) {
			if (value instanceof String && Strings.isNotBlank(value.toString())){
				if (property.equals("type")) {
					hqlType = "  t_data.typeId = :typeId and ";
					parameterMap.put("typeId", Long.parseLong(value.toString()));
				}else if(property.equals("publishUserId")){
					hqlOther = " and t_data.createUser=m.id and m.name like :name )";
					parameterMap.put("name", "%"+SQLWildcardUtil.escape(value.toString())+"%");
				} else {
					hqlOther = " and t_data." + property + " like :property )";
					parameterMap.put("property", "%"+SQLWildcardUtil.escape(value.toString())+"%");
				}
			}
			else{
				hqlOther = " and t_data." + property + " = :property )";
				parameterMap.put("property", value);
			}
		}
		sb.append(hqlType);
		sb.append(" t_data.typeId in (:typeIds) and t_data.state in (:state) and t_data.deletedFlag = false ");
		sb.append(hqlOther);
		parameterMap.put("state", Constants.getDataStatesCanManage());
		sb.append(" order by t_data2.state, t_data2.updateDate desc ");
		return new Object[]{sb.toString(),parameterMap};
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findAll() throws Exception {
		//外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();

		List<NewsType> inList = this.newsTypeManager.getManagerTypeByMember(CurrentUser.get().getId(), null, null);
		List<Long> typeIds = new ArrayList<Long>();
		
		StringBuffer hqlSb = new StringBuffer(
			"select t_data.id, t_data.title, t_data.brief, t_data.keywords, t_data.publishScope, t_data.publishDepartmentId, t_data.dataFormat, t_data.createDate, t_data.createUser, " +
			" t_data.publishDate, t_data.publishUserId, t_data.readCount, t_data.topOrder, t_data.accountId, t_data.typeId, t_data.state, " +
			" t_data.attachmentsFlag, t_data.auditUserId, t_data.imageNews, t_data.focusNews, t_data.imageId, reads.managerId from NewsData as t_data " +
			"left join t_data.newsReads as reads where t_data.typeId in (:typeIds) and  t_data.state in (:state) " +
			"and t_data.deletedFlag = false  order by t_data.state, t_data.updateDate desc  ");
			
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		if (inList == null || inList.size() == 0)
			typeIds.add(new Long(1l));
		else{
			for(NewsType t : inList){
				typeIds.add(t.getId());
			}
		}
		parameterMap.put("typeIds", typeIds);
		parameterMap.put("state", Constants.getDataStatesCanManage());
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>)newsDataDao.find(hqlSb.toString(), parameterMap));
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findByProperty(String property, Object value) throws Exception {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();		
		Object[] hqlArr = this.filterByManagerGetHql(property, value);
		List<NewsData> list = newsDataDao.find(hqlArr[0].toString(), (HashMap)hqlArr[1],new ArrayList());
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getById(java.lang.Long)
	 */
	public NewsData getById(Long id) {
		 NewsData data=newsDataDao.get(id);
		if(data==null){
			return data;
		}else{
			initData(data);		
			return data;
		}
	}
	
	/**
	 * 判斷某條數據是否存在
	 */
	public boolean dataExist(Long bulId){
		NewsData data = this.getById(bulId);
		if(data == null)
			return false;
		else
			return true;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getById(java.lang.Long, java.lang.Long)
	 */
	public NewsData getById(Long id,Long userId) {
		NewsData data=newsDataDao.get(id);
		if(data!=null){
			initData(data);		
		}
		return data;
	}

	/**
	 * 初始化新闻发布范围的中文显示名称
	 * @param data
	 */
	private void initPublishScope(NewsData data) {
		//初始化发布范围
		String ids=data.getPublishScope();
		String names="";
		if(StringUtils.isNotBlank(ids)){
			String[] idA=ids.split(",");		
			
			for(String idStr:idA){
				if(Constants.IS_TEST){
					if(idStr.indexOf("|")>-1) idStr=idStr.substring(idStr.indexOf("|")+1);
				}
				names=names+","+this.getNewsUtils().getMemberNameByUserId(Long.valueOf(idStr));				
			}
			data.setPublishScopeNames(ids.length()>0?names.substring(1):"");
		}
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#save(com.seeyon.v3x.news.domain.NewsData)
	 */
	public NewsData save(NewsData data, boolean isNew) {
		if(data.getTypeId()!=null)
			data.setType(newsTypeManager.getById(data.getTypeId()));
		if(isNew){
			data.setAccountId(CurrentUser.get().getLoginAccount());
			newsDataDao.save(data);
		}else{
			newsDataDao.update(data);
		}
		this.saveBody(data, isNew);
		return data;
	}
	
	public NewsData saveCustomNews(NewsData data, boolean isNew) {
		if(data.getTypeId()!=null)
			data.setType(newsTypeManager.getById(data.getTypeId()));
		if(isNew){
			newsDataDao.save(data);
		}else{
			newsDataDao.update(data);
		}
		this.saveBody(data, isNew);
		return data;
	}
	
	public void saveBody(NewsData data, boolean isNew){
		NewsBody body = new NewsBody();
		body.setBodyType(data.getDataFormat());
		body.setNewsDataId(data.getId());
		body.setContent(data.getContent());
		body.setCreateDate(data.getCreateDate());
		if(isNew){
			this.newsBodyDao.save(body);
		}else{
			this.newsBodyDao.update(body);
		}
	}
	
	public void updateDirect(NewsData data){
		newsDataDao.update(data);
	}
	
	public int readOneTime(long dataId){
		String hql = "update NewsData set readCount = readCount + 1 where id = :nid";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("nid", dataId);
		this.newsDataDao.bulkUpdate(hql, amap);
		
		String hql2 = "select readCount from NewsData where id = ?";
		List list = this.newsDataDao.getHibernateTemplate().find(hql2, dataId);
		if(list == null || list.size() == 0)
			return 1;
		else
			return (Integer)(list.get(0));
		
			
	}
	
	public void update(Long Id, Map<String, Object> columns) {
		// TODO Auto-generated method stub
		try{
			newsDataDao.update(Id, columns);
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getAllTypeList()
	 */
	public List<NewsType> getAllTypeList(long loginAccount){
		List<NewsType> list=this.getNewsTypeManager().findAll(loginAccount);
		Collections.sort(list);
		return list;
	}

	public List<NewsType> getAllCustomTypeList(long loginAccount) throws BusinessException{
		List<NewsType> list=this.getNewsTypeManager().findAllCustom(loginAccount);
		Collections.sort(list);
		return list;
	}
	public List<NewsType> getAllTypeList(long loginAccount, String spaceType){
		List<NewsType> list=this.getNewsTypeManager().findAllOfCustom(loginAccount, spaceType);
		return list;
	}
	
	/**
	 * 此方法作为备用,将来可以删除
	 */
	public List<NewsType> getAllTypeList(){
		List<NewsType> list=this.getNewsTypeManager().findAll();
		return list;
	}
	
	//返回只是集团板块列表
	public List<NewsType> getGroupAllTypeList(){
		List<NewsType> list=this.getNewsTypeManager().groupFindAll();
		Collections.sort(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getTypeList(java.lang.Long, boolean)
	 * 这个方法是为了防止其它报错,才加上的,将来可以删掉
	 */
	public List<NewsType> getTypeList(Long managerUserId,boolean isIgnoreUsed) throws Exception{
		List<NewsType> list=this.newsTypeManager.getManagerTypeByMember(managerUserId,
				Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount());//new ArrayList<NewsType>();
		this.newsTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	public List<NewsType> getTypeList(Long managerUserId, int spaceType, long spaceId) throws Exception {
		List<NewsType> list = new ArrayList<NewsType>();
		if (spaceType == 5) {
			list = this.newsTypeManager.getManagerTypeByMember(managerUserId, Constants.NewsTypeSpaceType.public_custom, spaceId);
		} else {
			list = this.newsTypeManager.getManagerTypeByMember(managerUserId, Constants.NewsTypeSpaceType.public_custom_group, spaceId);
		}
		this.newsTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getTypeList(java.lang.Long, boolean)
	 */
	public List<NewsType> getTypeList(Long managerUserId,boolean isIgnoreUsed,long loginAccount) throws Exception{
		List<NewsType> list=this.newsTypeManager.getManagerTypeByMember(managerUserId,
				Constants.NewsTypeSpaceType.corporation, loginAccount);//new ArrayList<NewsType>();
		this.newsTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	public List<NewsType> getTypeListOnlyByMember(Long managerUserId,boolean isIgnoreUsed) throws Exception{
		List<NewsType> list=this.newsTypeManager.getManagerTypeByMember(managerUserId,  Constants.NewsTypeSpaceType.corporation, null);
		return list;
	}
	/**
	 * 取得有我来管理的集团新闻板块
	 */
	public List<NewsType> getManagerGroupBulType(Long managerUserId, boolean isIgnoreUsed) throws Exception {		
		List<NewsType> list=this.newsTypeManager.getManagerTypeByMember(managerUserId, Constants.NewsTypeSpaceType.group, null);
		this.newsTypeManager.setTotalItemsOfType(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getTypeListByWrite(java.lang.Long, boolean)
	 */
	public List<NewsType> getTypeListByWrite(Long writeId,boolean isIgnoreUsed){
		return this.newsTypeManager.getWriterTypeByMember(writeId, null, null);
	}
	

	public List<NewsData> getAuditList(Long userId,String property,Object value,long loginAccount) throws NewsException{
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		DetachedCriteria dc=DetachedCriteria.forClass(NewsData.class);		
		dc.add(Restrictions.in("state", new Integer[]{Constants.DATA_STATE_ALREADY_CREATE,Constants.DATA_STATE_ALREADY_AUDIT}));
		if(StringUtils.isNotBlank(property) && value!=null){
			if(property.equals("type")){
				Long typeId = new Long(value.toString());
				dc.add(Restrictions.eq("typeId", typeId ));
			}else{				
				dc.add(Restrictions.eq(property, value));				
				List<NewsType> typeList=this.newsTypeManager.findByPropertyNoPaging("auditUser", userId,loginAccount);
				if(typeList.size()==0) 
					throw new NewsException("news_no_purview");					
				dc.add(Restrictions.in("typeId", this.getIdSet(typeList)));
			}
		}else{
			List<NewsType> typeList=this.newsTypeManager.findByPropertyNoPaging("auditUser", userId,loginAccount);
			if(typeList.size()==0) 
				throw new NewsException("news_no_purview");				
			dc.add(Restrictions.in("typeId", this.getIdSet(typeList)));
		}
		
		dc.addOrder(Order.desc("createDate"));		
		List<NewsData> list=this.newsDataDao.executeCriteria(dc);		
		initList(list);
		return list;
	}
	
	/**
	 * 点击板块管理后,再点击新闻审核后所看到的列表
	 */
	public List<NewsData> getAuditDataListNew(Long userId,String property,Object value, int spaceType) throws NewsException{
		List<NewsType> auditTypeList = this.newsTypeManager.getAuditTypeByMember(userId, 
				Constants.valueOfSpaceType(spaceType), null);
		if(auditTypeList == null || auditTypeList.size() == 0)
			return Collections.EMPTY_LIST;
		List<NewsData> list=newsDataDao.getAuditDataListNewDAO(userId, property, value,auditTypeList);
		initList(list);
		return list;
	}
	

	public NewsTypeManagersManager getNewsTypeManagersManager() {
		return newsTypeManagersManager;
	}

	public void setNewsTypeManagersManager(
			NewsTypeManagersManager newsTypeManagersManager) {
		this.newsTypeManagersManager = newsTypeManagersManager;
	}

	
	/**
	 * 得到hql
	 * hql目的：查询当前用户可以看到的所有已发布、未删除的公告
	 * @return [0] " select t_data "
	 *  	   [1] " from NewsData t_data where "
	 *         [2] " state... deletedFlag... " 已发布的，未删除的 条件过滤
	 *         [3] " order by t_data.topOrder desc, t_data.publishDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */	
	private DetachedCriteria filterByReadGetHql(){
		DetachedCriteria criteria = DetachedCriteria.forClass(NewsData.class);
		criteria.add(Restrictions.eq("state", Constants.DATA_STATE_ALREADY_PUBLISH));
		criteria.add(Restrictions.eq("deletedFlag", false));
		criteria.addOrder(Order.desc("publishDate"));		
		return criteria;
	}

	/**
	 * 得到hql
	 * hql目的：查询当前用户可以看到的所有已发布、未删除的公告
	 * @return [0] " select t_data "
	 *  	   [1] " from BulData t_data, BulPublishScope t_scope where "
	 *         [2] " [type]... state... deletedFlag... userId... [other]..." 有权限的，已发布的，未删除的 条件过滤
	 *         [3] " order by t_data.topOrder desc, t_data.publishDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */
	private DetachedCriteria filterByReadGetHqlss(String property, Object value) {	
		DetachedCriteria criteria = this.filterByReadGetHql();
		if (StringUtils.isNotBlank(property) && value != null) {
			if (value instanceof String)
				if (property.equals("type")) 
					criteria.add(Restrictions.eq("typeId", Long.parseLong(value.toString())));
				else 
					criteria.add(Restrictions.like("property", "%"+SQLWildcardUtil.escape(value.toString())+"%"));
			else 
				criteria.add(Restrictions.eq("property", value));
		}
		return criteria;
	}
	
	/**
	 * 得到hql(传参方式)
	 * hql目的：查询当前用户可以看到的所有已发布、未删除的公告
	 * @return [0] " select t_data "
	 *  	   [1] " from BulData t_data, BulPublishScope t_scope where "
	 *         [2] " [type]... state... deletedFlag... userId... [other]..." 有权限的，已发布的，未删除的 条件过滤
	 *         [3] " order by t_data.topOrder desc, t_data.publishDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */
	private DetachedCriteria filterByReadGetHqlAndValue(String property, Object value) {	
		DetachedCriteria criteria = this.filterByReadGetHql();
		if (StringUtils.isNotBlank(property) && value != null) {
			if (value instanceof String)
				if (property.equals("type")) {
					criteria.add(Restrictions.eq("typeId", Long.parseLong(value.toString())));
				} else {
					criteria.add(Restrictions.eq(property, "%"+SQLWildcardUtil.escape(value.toString())+"%"));
				}
			else{
				criteria.add(Restrictions.eq(property, value));
			}
		}
		return criteria;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findByReadUser(long)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findByReadUser(long id, List<NewsType> typeList,long loginAccount, Integer imageOrFocus) throws DataAccessException, NewsException {
		List<NewsData> list;
		List<NewsType> typeList2 = new ArrayList<NewsType>();
		typeList2.addAll(typeList);
		if(typeList2 != null && !CurrentUser.get().isInternal())
			for(NewsType nt : typeList2){
				if(!nt.getOutterPermit())
					typeList.remove(nt);
			}
		
		//集团化处理，排除部门新闻
		if(typeList == null)
			typeList=this.getAllTypeList(loginAccount);
		List<NewsType> inList=new ArrayList<NewsType>();
		for(NewsType type:typeList){
			if( type.getSpaceType().intValue() == 1 || type.getSpaceType().intValue() == 4
					|| type.getSpaceType().intValue() == 5 || type.getSpaceType().intValue() == 6){
				inList.add(type);
			}
		}
    	list = newsDataDao.findByReadUserDAO(id, inList, imageOrFocus);
		initList(list);
		return list;
	}
	/**
	 * 政务【我的提醒】查【单位新闻】总数 (由findByReadUser改造) wangjingjing
	 */
	@SuppressWarnings("unchecked")
	public Long findByReadUserCount(long id, List<NewsType> typeList,long loginAccount, Integer imageOrFocus) throws DataAccessException, NewsException {
		List<NewsType> typeList2 = new ArrayList<NewsType>();
		typeList2.addAll(typeList);
		if(typeList2 != null && !CurrentUser.get().isInternal())
			for(NewsType nt : typeList2){
				if(!nt.getOutterPermit())
					typeList.remove(nt);
			}
		
		//集团化处理，排除部门新闻
		if(typeList == null)
			typeList=this.getAllTypeList(loginAccount);
		List<NewsType> inList=new ArrayList<NewsType>();
		for(NewsType type:typeList){
			if( type.getSpaceType().intValue() == 1 || type.getSpaceType().intValue() == 4
					|| type.getSpaceType().intValue() == 5 || type.getSpaceType().intValue() == 6){
				inList.add(type);
			}
		}
		return newsDataDao.findByReadUserDAOCount(id, inList, imageOrFocus);
	}
	
	public Map<Long, List<NewsData>> findByReadUserHome_(long id, List<NewsType> typeList) throws DataAccessException,
			NewsException {
		List<NewsData> list = new ArrayList<NewsData>();		
		List<NewsType> inList = typeList;		
		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new HashMap<Long, List<NewsData>>();
		}else{
			for(NewsType t : inList){
				types.add(t.getId());
			}
		}
		DetachedCriteria criteria = this.filterByReadGetHql();		
		criteria.add(Restrictions.in("typeId", types));
		final int count = Constants.NEWS_HOMEPAGE_TABLE_COLUMNS;
		final int getCounts = count * typeList.size();// * 3;
		long start = System.currentTimeMillis();
		int dbConnTime = 1;
		list = newsDataDao.executeCriteria(criteria,0,getCounts);		
		
		// 2008.02.27
		Map<Long, List<NewsData>> amap = new HashMap<Long, List<NewsData>>();

		for(NewsType bt : typeList){
			List<NewsData> alist = new ArrayList<NewsData>();
			amap.put(bt.getId(), alist);

		}
		for(NewsData bd : list){
			List<NewsData> alist = amap.get(bd.getTypeId());
			if(alist == null)
				alist = new ArrayList<NewsData>();
			if(alist.size() >= count)
				continue;
			alist.add(bd);
		}
		
		List<NewsType> typeList2 = new ArrayList<NewsType>();
		for(NewsType t : typeList){
			int size = amap.get(t.getId()).size();
			if(size < count)
				typeList2.add(t);
		}
		if(typeList2.size() > 0){
			this.getNewsTypeManager().setTotalItemsOfType(typeList2);
			List<NewsType> typeList3 = new ArrayList<NewsType>();
			for(NewsType t2 : typeList2){
				int asize = amap.get(t2.getId()).size();
				if(asize < t2.getTotalItems()){
					typeList3.add(t2);
				}
			}
			if(typeList3.size() > 0){
				//
				List<Long> types2 = new ArrayList<Long> ();
				//String typeStr2 = "";
				for(NewsType t : typeList3){
					types2.add(t.getId());
					amap.put(t.getId(), new ArrayList<NewsData>());
				}			
				criteria.add(Restrictions.in("typeId", types2));
				list = newsDataDao.executeCriteria(criteria,0,getCounts);
				dbConnTime++;
				for(NewsData bd : list){
					List<NewsData> alist = amap.get(bd.getTypeId());
					if(alist == null)
						alist = new ArrayList<NewsData>();
					if(alist.size() >= count)
						continue;
					alist.add(bd);
				}
				for(;;){				
					List<NewsType> typeList22 = new ArrayList<NewsType>();
					for(NewsType t : typeList){
						int size = amap.get(t.getId()).size();
						if(size < count)
							typeList22.add(t);
					}
					if(typeList22.size() == 0)
						break;
					List<NewsType> typeList32 = new ArrayList<NewsType>();
					for(NewsType t2 : typeList22){
						int asize = amap.get(t2.getId()).size();
						if(asize < t2.getTotalItems()){
							typeList32.add(t2);
						}
					}
					if(typeList32.size() == 0)
						break;					
					
					
					List<Long> types3 = new ArrayList<Long> ();
					for(NewsType t : typeList32){
						types3.add(t.getId());
						
						amap.put(t.getId(), new ArrayList<NewsData>());
					}			
					criteria.add(Restrictions.in("typeId", types3));
					list = newsDataDao.executeCriteria(criteria,0,getCounts);
					dbConnTime++;
					
					for(NewsData bd : list){
						List<NewsData> alist = amap.get(bd.getTypeId());
						if(alist == null)
							alist = new ArrayList<NewsData>();
						if(alist.size() >= count)
							continue;
						alist.add(bd);
					}		
				}
			}
				
		}
			
		log.info("非遍历版块算法时间：" + (System.currentTimeMillis() - start) + "   数据库连接：" + dbConnTime);
		for(NewsType btt : typeList){
			initList(amap.get(btt.getId()));
		}		
		return amap;
	}
	
	//进入新闻首页要先查出所有的新闻的类型,然后查出每个新闻类型里面所有的新闻
	public Map<Long, List<NewsData>> findByReadUserHome(long id, List<NewsType> typeList) throws DataAccessException,
			NewsException {
		Map<Long, List<NewsData>> amap = newsDataDao.findByReadUserHomeDAO(id, typeList);
		for(NewsType btt : typeList){
			initList(amap.get(btt.getId()));
		}
		return amap;
	}
	
	
	//集团,集团页面点击更多,查询出所有类型的新闻
	public List<NewsData> groupFindByReadUser(long id, List<NewsType> typeList, Integer imageOrFocus) throws DataAccessException, NewsException {
		List<NewsData> list = newsDataDao.findByReadUserDAO(id, typeList, imageOrFocus);
		initList(list);			
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<NewsData> findByReadUser(Long userId, long loginAccount,boolean isInternal, Integer imageOrFocus, int spaceType, List<Long> typeIds) throws DataAccessException, NewsException {
		List<NewsType> typeList = new ArrayList<NewsType>();
		if(spaceType == Constants.NewsTypeSpaceType.group.ordinal()){
			typeList.addAll(this.getGroupAllTypeList());
		} else if(spaceType == Constants.NewsTypeSpaceType.corporation.ordinal()) {
			typeList.addAll(this.getAllTypeList(loginAccount));
		} else if (spaceType == Constants.NewsTypeSpaceType.custom.ordinal()) {
			typeList.addAll(this.getAllTypeList(loginAccount, "custom"));
		} else if (spaceType == Constants.NewsTypeSpaceType.public_custom.ordinal()) {
			typeList.addAll(this.getAllTypeList(loginAccount, "publicCustom"));
		} else if (spaceType == Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
			typeList.addAll(this.getAllTypeList(loginAccount, "publicCustomGroup"));
		}
		
		List<NewsType> inList = typeList;
		if (typeList == null || typeList.isEmpty()) {
			return new ArrayList<NewsData>();
		} else if (!isInternal) {
			inList = new ArrayList<NewsType>();
			for (NewsType t : typeList) {
				if (t.getOutterPermit()) {
					inList.add(t);
				}
			}
		}
		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new ArrayList<NewsData>();
		} else {
			for (NewsType t : inList) {
				if (typeIds != null) {
					if (typeIds.contains(t.getId())) {
						types.add(t.getId());
					}
				} else {
					types.add(t.getId());
				}
			}
		}
		
		//新闻左外联新闻阅读信息表
		StringBuilder hql = new StringBuilder();
		hql.append("select news.id, news.title, news.brief, news.keywords, news.publishScope, news.publishDepartmentId, news.dataFormat, news.createDate, news.createUser, ");
		hql.append(" news.publishDate, news.publishUserId, news.readCount, news.topOrder, news.accountId, news.typeId, news.state, ");
		hql.append("news.attachmentsFlag, news.auditUserId, news.imageNews, news.focusNews, news.imageId, reads.managerId ");
		hql.append(" from NewsData as news left join news.newsReads as reads with reads.managerId=:currentUserId where news.state=:state and news.deletedFlag=false");
		if(imageOrFocus != null){
			if(imageOrFocus.intValue() == Constants.ImageNews){
				hql.append(" and news.imageNews=true ");
			} else {
				hql.append(" and news.focusNews=true ");
			}
		}
		hql.append(" and news.typeId in (:types) order by news.publishDate desc");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentUserId", userId);
		params.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
		params.put("types", types);
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>)newsDataDao.find(hql.toString(), params));
		initList(list);
		return list;
	}
	
	public List<NewsData> findByReadUser4ImageNews(User user, Integer imageOrFocus, int spaceType, List<Long> typeIds) throws DataAccessException, NewsException {
		return findByReadUser(user.getId(), user.getLoginAccount(), user.isInternal(), imageOrFocus, spaceType, typeIds);
	}
	
	//图片新闻栏目或焦点新闻栏目
	public List<NewsData> findByReadUser4ImageNews(Long userId, long loginAccount,boolean isInternal, Integer imageOrFocus, int spaceType)
			throws DataAccessException, NewsException {
		return findByReadUser(userId, loginAccount, isInternal, imageOrFocus, spaceType, null);
	}
	
	public List<NewsData> findByReadUserForIndex(Long userId, long loginAccount, boolean isInternal) throws DataAccessException, NewsException {
		return this.findByReadUserForIndex(userId, loginAccount, isInternal, null);
	}

	@SuppressWarnings("unchecked")
	public List<NewsData> findByReadUserForIndex(Long userId, long loginAccount, boolean isInternal, List<Long> typeIds) throws DataAccessException, NewsException {
		List<NewsType> typeList = this.newsTypeManager.findAll(loginAccount);
		List<NewsType> inList = typeList;
		if (typeList == null || typeList.isEmpty()) {
			return new ArrayList<NewsData>();
		} else if (!isInternal) {
			inList = new ArrayList<NewsType>();
			for (NewsType t : typeList) {
				if (t.getOutterPermit()) {
					inList.add(t);
				}
			}
		}

		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new ArrayList<NewsData>();
		} else {
			for (NewsType t : inList) {
				if (typeIds != null) {
					if (typeIds.contains(t.getId())) {
						types.add(t.getId());
					}
				} else {
					types.add(t.getId());
				}
			}
		}

		// 新闻左外联新闻阅读信息表
		String hql = "select news.id, news.title, news.brief, news.keywords, news.publishScope, news.publishDepartmentId, news.dataFormat, news.createDate, news.createUser, "
				+ " news.publishDate, news.publishUserId, news.readCount, news.topOrder, news.accountId, news.typeId, news.state, "
				+ "news.attachmentsFlag, news.auditUserId, news.imageNews, news.focusNews, news.imageId, reads.managerId "
				+ " from NewsData as news left join news.newsReads as reads with reads.managerId=:currentUserId where news.state=:state and news.deletedFlag=false"
				+ " and news.typeId in (:types) order by news.publishDate desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentUserId", userId);
		params.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
		params.put("types", types);
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>) newsDataDao.find(hql, params));
		initList(list);
		return list;
	}
	
	public List<NewsData> findCustomByReadUserForIndex(Long userId, long loginAccount, int spaceType, boolean isInternal) throws DataAccessException, NewsException {
		List<NewsType> typeList = this.newsTypeManager.findAllOfCustomAcc(loginAccount, spaceType);
		List<NewsType> inList=typeList;
		if(typeList == null)
			return new ArrayList<NewsData>();
		else if(!isInternal){
			inList = new ArrayList<NewsType>();
			for(NewsType t : typeList){
				if(t.getOutterPermit())
					inList.add(t);
			}
		}

		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new ArrayList<NewsData>();
		}else{
			for(NewsType t : inList){
				types.add(t.getId());
			}
		}
		
		//新闻左外联新闻阅读信息表
		String hql = "select news.id, news.title, news.brief, news.keywords, news.publishScope, news.publishDepartmentId, news.dataFormat, news.createDate, news.createUser, " +
					 " news.publishDate, news.publishUserId, news.readCount, news.topOrder, news.accountId, news.typeId, news.state, " +
					 "news.attachmentsFlag, news.auditUserId, news.imageNews, news.focusNews, news.imageId, reads.managerId " +
					 " from NewsData as news left join news.newsReads as reads with reads.managerId=:currentUserId where news.state=:state and news.deletedFlag=false" +
					 " and news.typeId in (:types) order by news.publishDate desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentUserId", userId);
		params.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
		params.put("types", types);
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>)newsDataDao.find(hql, params));
		initList(list);			
		return list;		
	}
	
	public List<NewsData> groupFindByReadUserForIndex(long id, boolean isInternal) throws DataAccessException, NewsException {
		return this.groupFindByReadUserForIndex(id, isInternal, null);
	}

	@SuppressWarnings("unchecked")
	public List<NewsData> groupFindByReadUserForIndex(long id, boolean isInternal, List<Long> typeIds) throws DataAccessException, NewsException {
		if (!isInternal) {
			return new ArrayList<NewsData>();
		}

		List<NewsType> typeList = this.newsTypeManager.groupFindAll();
		List<Long> types = new ArrayList<Long>();
		if (typeList == null || typeList.isEmpty()) {
			return new ArrayList<NewsData>();
		} else {
			for (NewsType t : typeList) {
				if (typeIds != null) {
					if (typeIds.contains(t.getId())) {
						types.add(t.getId());
					}
				} else {
					types.add(t.getId());
				}
			}
		}
		
		// 新闻左外联新闻阅读信息表
		String hql = "select news.id, news.title, news.brief, news.keywords, news.publishScope, news.publishDepartmentId, news.dataFormat, news.createDate, news.createUser, "
				+ " news.publishDate, news.publishUserId, news.readCount, news.topOrder, news.accountId, news.typeId, news.state, "
				+ "news.attachmentsFlag, news.auditUserId, news.imageNews, news.focusNews, news.imageId, reads.managerId "
				+ " from NewsData as news left join news.newsReads as reads with reads.managerId=:currentUserId where news.state=:state and news.deletedFlag=false"
				+ " and news.typeId in (:types) order by news.publishDate desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentUserId", id);
		params.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
		params.put("types", types);
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>) newsDataDao.find(hql, params));
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findByReadUser(long, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findByReadUser(long id,String property,Object value, List<NewsType> typeList,long loginAccount) throws DataAccessException, NewsException {
		List<NewsType> inList = new ArrayList<NewsType>();
		if(typeList == null)
			typeList = this.getAllTypeList(loginAccount);
		//property,是属性,value是字段		
		//集团化处理，排除部门公告
		//把单位新闻的类型加到列表里面
		for (NewsType type : typeList) {
			if (type.getSpaceType().intValue() == 1 || type.getSpaceType().intValue() == 4) {
				inList.add(type);
			}
		}
		List<NewsData> list=newsDataDao.findByReadUserDAO(id, property, value, inList);			
		initList(list);
		return list;
	}
	
	public List<NewsData> findByReadUser(long id, String property, Object value, List<NewsType> typeList,long loginAccount, String spaceType) throws DataAccessException, NewsException {
		List<NewsType> inList = new ArrayList<NewsType>();
		if(typeList == null)
			typeList = this.getAllTypeList(loginAccount, spaceType);
		//property,是属性,value是字段		
		for (NewsType type : typeList) {
			if (type.getSpaceType().intValue() == 4 || type.getSpaceType().intValue() == 5 || type.getSpaceType().intValue() == 6) {
				inList.add(type);
			}
		}
		List<NewsData> list=newsDataDao.findByReadUserDAO(id, property, value, inList);			
		initList(list);
		return list;
	}
	
	public List<NewsData> findByReadUser4Mobile(long id,String property,Object value,long loginAccount) throws DataAccessException, NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		
		List<NewsData> list;

		DetachedCriteria criteria = DetachedCriteria.forClass(NewsData.class);
		criteria.add(Restrictions.eq("state", Constants.DATA_STATE_ALREADY_PUBLISH));
		criteria.add(Restrictions.eq("deletedFlag", false));
		criteria.addOrder(Order.desc("publishDate"));
		List<NewsType> inList = this.getAllTypeList(loginAccount);
		if(inList == null)
			inList = new ArrayList<NewsType>();
		List<NewsType> types2 = this.getGroupAllTypeList();
		if(types2 != null)
			inList.addAll(types2);
		
		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new ArrayList<NewsData>();
		}else{
			for(NewsType t : inList){
				types.add(t.getId());
			}
		}
		criteria.add(Restrictions.in("typeId", types));
		if(value != null && Strings.isNotBlank(value.toString())){
			criteria.add(Restrictions.like(property, "%"+SQLWildcardUtil.escape(value.toString())+"%"));
		}
		list = newsDataDao.executeCriteria(criteria);				
		initList(list);
		return list;
		
		
	}
	/**
	 * 此方法作为备用,将来要可能会删除,
	 * hql 优化 dongyj
	 */
	public List<NewsData> findByReadUser4Mobile(long id,String property,Object value) throws DataAccessException, NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		
		List<NewsData> list;		
		DetachedCriteria criteria = this.filterByReadGetHql();		
		List<NewsType> inList = this.getAllTypeList();
		if(inList == null)
			inList = new ArrayList<NewsType>();
		List<NewsType> types2 = this.getGroupAllTypeList();
		if(types2 != null)
			inList.addAll(types2);
		
		List<Long> types = new ArrayList<Long>();
		if (inList == null || inList.isEmpty()) {
			return new ArrayList<NewsData>();
		}else{
			for(NewsType t : inList){
				types.add(t.getId());
			}
		}
		criteria.add(Restrictions.in("typeId", types));
		if(value != null && Strings.isNotBlank(value.toString())){
			criteria.add(Restrictions.like(property, "%"+SQLWildcardUtil.escape(value.toString())+"%"));
		}
		list = newsDataDao.executeCriteria(criteria);				
		initList(list);
		return list;		
	}
	
	//在单位新闻列表页面右上角的查询功能
	@SuppressWarnings("unchecked")
	public List<NewsData> groupFindByReadUser(long id,String property,Object value, List<NewsType> typeList) throws DataAccessException, NewsException {
		List<NewsType> inList=new ArrayList<NewsType>();
		if(typeList != null)
		{
			for(NewsType type:typeList){
				if(type.getSpaceType().intValue()==0 ){
					inList.add(type);
				}
			}
		}
		List list=newsDataDao.findByReadUserDAO(id, property, value, inList);
		initList(list);
		return list;
	}

	/**
	 * 得到hql
	 * hql目的：查询当前用户创建的未删除的新闻列表
	 * @return [0] " select t_data "
	 *  	   [1] " from NewsData t_data where "
	 *         [2] " state... createUser... deletedFlag... " 当前用户创建、未删除
	 *         [3] " order by t_data.createDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */	
	private DetachedCriteria filterByWriteGetHql(){
		Set<Integer> sset = Constants.getDataStatesNoPublish();
		DetachedCriteria criteria = DetachedCriteria.forClass(NewsData.class);
		criteria.add(Restrictions.in("state", sset));
		criteria.add(Restrictions.eq("createUser", CurrentUser.get().getId()));
		criteria.add(Restrictions.eq("deletedFlag", false));
		criteria.addOrder(Order.desc("createDate"));
		
		
		return criteria;
	}

	/**
	 * 得到hql
	 * hql目的：查询当前用户创建的未删除的新闻列表
	 * @return [0] " select t_data "
	 *  	   [1] " from NewsData t_data where "
	 *         [2] " state... createUser... deletedFlag... " 当前用户创建、未删除
	 *         [3] " order by t_data.createDate desc "
	 * 可以在[1][2]之间加入 " xxx... and " 
	 * 或者在[2][3]之间加入 " and xxx... "
	 * 以配合索引进行查询
	 */	
	private DetachedCriteria filterByWriteGetHql(String property,Object value) throws NewsException{
		Set<Integer> sset = Constants.getDataStatesNoPublish();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(NewsData.class);
		criteria.add(Restrictions.in("state", sset));
		criteria.add(Restrictions.eq("createrUser", CurrentUser.get().getId()));
		criteria.add(Restrictions.eq("deletedFlag", false));
		criteria.addOrder(Order.desc("createDate"));

		if (StringUtils.isNotBlank(property) && value != null) {
			if (value instanceof String && Strings.isNotBlank(value.toString()))
				if (property.equals("type")) {
					criteria.add(Restrictions.eq("typeId", Long.parseLong(value.toString())));
				} else {
					criteria.add(Restrictions.like(property, "%"+SQLWildcardUtil.escape(value.toString())+"%"));
				}
			else{
				criteria.add(Restrictions.eq(property, value));
			}
		}		
		return criteria;
	}
	
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findWriteAll()
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findWriteAll() throws NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		//hql 优化
		DetachedCriteria criteria = this.filterByWriteGetHql();
		List<NewsData> list = newsDataDao.executeCriteria(criteria);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#findWriteByProperty(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findWriteByProperty(String property, Object value) throws NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		DetachedCriteria criteria = this.filterByWriteGetHql(property,value);
		List<NewsData> list = newsDataDao.executeCriteria(criteria);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsDataManager#getReadListByData(com.seeyon.v3x.news.domain.NewsData, java.lang.Long)
	 */
	@SuppressWarnings("static-access")
	public List<NewsRead> getReadListByData(NewsData data,Long userId) throws Exception{
		List<NewsRead> readList=null;
		
		boolean isManager = false;
		if(userId.longValue() == data.getCreateUser().longValue()
				|| userId.longValue() == data.getPublishUserId().longValue()
				|| userId.longValue() == data.getType().getAuditUser().longValue())
			isManager = true;
		else
			isManager = this.newsTypeManager.isManagerOfType(data.getTypeId(), userId);
		
		if(isManager){
			readList=this.getNewsReadManager().getReadListByData(data);
			for(NewsRead read:readList){
				if(read.getManagerId()!=null){
					read.setManagerName(this.getNewsUtils().getMemberNameByUserId(read.getManagerId()));
				}
			}
		}
		
		return readList;
	}
	
	
	public List statistics(String type ,final long newsTypeId) {
		List list = new ArrayList();
		
		if (type.equals("byRead")) {
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(Constants.DATA_STATE_ALREADY_PUBLISH );
		    parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		    parameter.add(newsTypeId);
		    
			String hqlCount = "select news_data.typeId,news_type.typeName,news_data.id,news_data.title,news_data.createUser,v3x_org_member.name,news_data.readCount from NewsData news_data, NewsType news_type, V3xOrgMember v3x_org_member ";
				hqlCount += "where news_data.typeId=news_type.id  and   (news_data.state=? or news_data.state=?) ";
				hqlCount += "and news_data.createUser=v3x_org_member.id ";
				hqlCount += " and news_type.id = ? ";
				hqlCount += "order by read_count desc";
			list = newsDataDao.find(hqlCount,null, parameter);
			
			List<Object[]> arrs = (List<Object[]>)list;
			List<Object[]> todeal = new ArrayList<Object[]>();
			List<Object[]> keep = new ArrayList<Object[]>();
			for(Object[] objs : arrs){
				if(objs[6] == null)
					todeal.add(objs);
				else
					keep.add(objs);
			}
			for(Object[] objs : todeal){
				objs[6] = Integer.valueOf(0);
			}
			keep.addAll(keep.size(), todeal);
			
			return keep;
		} else if (type.equals("byWrite")) {
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(Constants.DATA_STATE_ALREADY_PUBLISH );
		    parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		    parameter.add(newsTypeId);
			String sql = "select news_data.createUser from NewsData news_data where (news_data.state=? or  news_data.state=? )";
			
		    sql += " and news_data.typeId = ? " ;			
			int count = 0;
			List lista = newsDataDao.find(sql, -1,-1,null,parameter);			
			
			Map<String, Integer> map = new HashMap<String, Integer>();			
			if(lista != null){				
				for(Object obj : lista){
					String cu = obj.toString();
					Integer tot = map.get(cu);
					if(tot == null){
						map.put(cu, 1);						
					}else
						map.put(cu, tot + 1);		
				}
				count = map.size();
			}
			Pagination.setRowCount(count);	
			
			NewsType theType = newsTypeManager.getById(newsTypeId);
			boolean isGroup = (theType != null && (theType.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal()));
//			
			Set<String> keyset = map.keySet();
			for(String bi : keyset){
				Object[] arr = new Object[3];
				arr[0] = bi;
				String name = this.getNewsUtils().getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, Long.valueOf(bi.toString()), isGroup);
				arr[1] = name;
				arr[2] = map.get(bi);	
				
				list.add(arr);
			}
			
			return list;
		} else if (type.equals("byPublishDate")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			for (int i = 0; i < 12; i++) {

				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0,
						0, 0);
				final Date endDate = cal.getTime();

				cal.add(Calendar.MONTH, -1);
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0,
						0, 0);
				final Date beginDate = cal.getTime();
				List<Object> parameter = new ArrayList<Object>();
				parameter.add(Constants.DATA_STATE_ALREADY_PUBLISH );
			    parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
			    parameter.add(endDate);
			    parameter.add(beginDate);
			    parameter.add(newsTypeId);
			    
			    String hql = " select count(*) from NewsData as newsdata, NewsType as newstype where newsdata.typeId = newstype.id ";
				hql += " and (newsdata.state = ? or newsdata.state = ?) ";
				hql += " and newsdata.publishDate <=? and newsdata.publishDate >=? ";
				hql += " and newstype.id = ? ";
				
				Integer total = (Integer)newsDataDao.findUnique(hql, null, parameter);
				list.add(new Object[] { beginDate, total });
			}
			return list;
		} else if (type.equals("byState")) {
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(Constants.DATA_STATE_ALREADY_PUBLISH );
		    parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		    parameter.add(newsTypeId);
			String sql = "select news_data.state "
					+ "from NewsData news_data  where  (news_data.state=? or  news_data.state=? ) ";
			 
			sql += " and news_data.typeId = ? " ;
			List lista = newsDataDao.find(sql, -1,-1,null,parameter);
			Map<String, Integer> map = new HashMap<String, Integer>();
			if(lista != null){				
				for(Object obj : lista){
					String sta = obj.toString();
					Integer tot = map.get(sta);
					if(tot == null){
						map.put(sta, 1);						
					}else
						map.put(sta, tot + 1);		
				}
			}
			
			Set<String> keyset = map.keySet();
			for(String sta : keyset){
				Object[] arr = new Object[2];
				arr[0] = sta;
				arr[1] = map.get(sta);
				list.add(arr);
			}
			
			if(list.size() == 0){
				Object[] pub = new Object[2];
				pub[0] = Constants.DATA_STATE_ALREADY_PUBLISH;
				pub[1] = 0;
				list.add(pub);
				
				Object[] pig = new Object[2];
				pig[0] = Constants.DATA_STATE_ALREADY_PIGEONHOLE;
				pig[1] = 0;
				list.add(pig);
			}else if(list.size() == 1){
				Object[] cur = (Object[])list.get(0);
				if(cur[0].toString().equals(Constants.DATA_STATE_ALREADY_PUBLISH + "")){
					Object[] pig = new Object[2];
					pig[0] = Constants.DATA_STATE_ALREADY_PIGEONHOLE;
					pig[1] = 0;
					list.add(pig);
				}else{
					list = new ArrayList();					
					Object[] pub = new Object[2];
					pub[0] = Constants.DATA_STATE_ALREADY_PUBLISH;
					pub[1] = 0;					
					list.add(pub);
					list.add(cur);						
				}
			}	
			return list;
		}

		return list;
	}

	public NewsLogManager getNewsLogManager() {
		return newsLogManager;
	}

	public void setNewsLogManager(NewsLogManager newsLogManager) {
		this.newsLogManager = newsLogManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public void pigeonhole(List<Long> ids){
		for(Long id:ids){
			NewsData data=this.getById(id);
			if(data.getState()!=Constants.DATA_STATE_ALREADY_PIGEONHOLE){
				data.setState(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
				this.updateDirect(data);
			}
			
			// 删除全文检索
			try {
				IndexManager indexManager = (IndexManager)ApplicationContextHolder.getBean("indexManager");
				indexManager.deleteFromIndex(ApplicationCategoryEnum.news, id);
			} catch (IOException e) {
				log.error("从indexManager删除检索项。", e);
			}
		}
	}
	/**
	 * 用户模块管理页面什么也不输入的时候进行的查询
	 */
	@SuppressWarnings("unchecked")
	public List<NewsData> findAll(Long typeId,long userid) throws Exception {
		List<NewsType> inList = this.newsTypeManager.getManagerTypeByMember(userid, null, null);
		List<NewsData> list=null;
		if (inList == null || inList.size() == 0)
			return Collections.EMPTY_LIST;
		else
			list=newsDataDao.findAllDAO(userid, typeId,inList);
		initList(list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<NewsData> findAllWithOutFilter(Long typeId) throws Exception{
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		
		List<NewsData> list = null;
		DetachedCriteria dc=DetachedCriteria.forClass(NewsData.class);
		dc.add(Restrictions.eq("typeId", typeId));
		dc.add(Restrictions.eq("deletedFlag", false));
		list=newsDataDao.executeCriteria(dc);
		return list;
	}
	
	public int findAllWithOutFilterTotal(final Long typeId) throws Exception{
		int ret = 0;
		String hql = " select count(*) from NewsData where typeId = ? " 
		+ " and deletedFlag = false and state != ? "
			+ " and state != ? ";
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(typeId);
		parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		parameter.add(Constants.DATA_STATE_NO_SUBMIT);
		ret = (Integer)newsDataDao.findUnique(hql, null, parameter);		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public List<NewsData> getNewsByTypeId(final Long typeId) throws Exception {
		List<NewsData> newsList = new ArrayList<NewsData>();
		String hql = "from NewsData where typeId = ? " + " and deletedFlag = false and state != ? " + " and state != ? ";
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(typeId);
		parameter.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		parameter.add(Constants.DATA_STATE_NO_SUBMIT);
		newsList = newsDataDao.find(hql, -1,-1,null,parameter);
		return newsList;
	}
	
	//用户在模块管理页面输入相关的查询条件
	@SuppressWarnings("unchecked")
	public List<NewsData> findByProperty(Long typeId, String condition, Object value,long userid) throws Exception {
		List<NewsType> inList = this.newsTypeManager.getManagerTypeByMember(userid, null, null);
		List list=newsDataDao.findByPropertyDAO(userid, typeId, condition, value,inList);
		initList(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<NewsData> findByReadUser(Long userId, Long typeId, String condition, Object value) throws NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		
		List<NewsData> list;
		DetachedCriteria criteria = this.filterByReadGetHql();
		if (StringUtils.isNotBlank(condition) && value != null) {
			if (value instanceof String && Strings.isNotBlank(value.toString()))
				if (condition.equals("type")) {
					criteria.add(Restrictions.eq("typeId", Long.parseLong(value.toString())));
				} else {
					criteria.add(Restrictions.like(condition, "%"+SQLWildcardUtil.escape(value.toString())+"%"));
				}
			else{
				criteria.add(Restrictions.eq("condition", value));
			}
		}
		criteria.add(Restrictions.eq("typeId", typeId));
		criteria.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		list  = newsDataDao.executeCriteria(criteria);				
		initList(list);
		return list;
	}

	/**
	 * 这是从集团新闻首页新闻板块下点击"更多"按钮
	 */
	public List<NewsData> findByReadUser(Long userId, Long typeId,long loginAccount) throws NewsException {
		List<NewsData> list=newsDataDao.findByReadUserDAO(userId, typeId,loginAccount);
		initList(list);
		return list;
	}
	
	public List<NewsData> findByReadUser(Long userId, Long typeId) throws NewsException {
		List<NewsData> list=newsDataDao.findByReadUserDAO(userId, typeId,CurrentUser.get().getLoginAccount());
		initList(list);
		return list;
	}
	
	public List<NewsData> findByReadUser4Section(Long userId, Long typeId) throws NewsException {
		NewsType type = this.newsTypeManager.getById(typeId);
		User user = CurrentUser.get();
		//外部人员，没有该板块的权限
		if(!user.isInternal() && !type.getOutterPermit()){
			return new ArrayList<NewsData>();
		}
		
		String hql = "select news.id, news.title, news.brief, news.keywords, news.publishScope, news.publishDepartmentId, news.dataFormat, " +
		 "news.createDate, news.createUser, news.publishDate, news.publishUserId, news.readCount, news.topOrder, " +
		 "news.accountId, news.typeId, news.state, news.attachmentsFlag, news.auditUserId, news.imageNews, news.focusNews, news.imageId, reads.managerId " +
		 " from NewsData as news left join news.newsReads as reads with reads.managerId=:currentUserId where " +
		 "news.state=:state and news.deletedFlag=false and news.typeId=:typeId order by news.publishDate desc";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentUserId", userId);
		params.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
		params.put("typeId", typeId);
		
		List<NewsData> list = NewsUtils.objArr2News((List<Object[]>)newsDataDao.find(hql, params));
		initList(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<NewsData> findWriteAll(Long typeId,long userId) throws NewsException {
		List list=newsDataDao.findWriteAllDAO(typeId,userId);
		initList(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<NewsData> findWriteByProperty(Long typeId, String condition, Object value) throws NewsException {
		// 外部人员guolv
		User user = CurrentUser.get();
		if(user == null || (!user.isInternal()))
			return new ArrayList<NewsData>();
		DetachedCriteria criteria = this.filterByWriteGetHql(condition, value);
		criteria.add(Restrictions.eq("typeId", typeId));
		List<NewsData> list = newsDataDao.executeCriteria(criteria);
		initList(list);
		return list;
	}
	
	/*
	 * 根据ID取到所需要的全文检索数据
	 * @see com.seeyon.v3x.index.share.interfaces.IndexEnable#getIndexInfo(long)
	 */
	public IndexInfo getIndexInfo(long id) throws Exception {
		// 首先取得ID取得NewsData
		NewsData newsData = getById(id);
		if(newsData==null) {return null;}
		V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, newsData.getPublishUserId());
		if(member==null){return null;}
		newsData.setContent(this.getBody(id).getContent());
		String createUserName = member.getName();
		IndexInfo indexInfo=new IndexInfo();
		indexInfo.setTitle(newsData.getTitle());
		indexInfo.setStartMemberId(newsData.getPublishUserId());
		indexInfo.setHasAttachment(newsData.getAttachmentsFlag());
		indexInfo.setTypeId(newsData.getTypeId());
		indexInfo.setContentCreateDate(newsData.getCreateDate());
		indexInfo.setEntityID(newsData.getId());
		
		String formatType = newsData.getDataFormat();
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(formatType)){
			indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			indexInfo.setContent(newsData.getContent());
		} 
		else
		{
			String contentPath = this.fileManager.getFolder(newsData.getCreateDate(), false);
			Partition partition = partitionManager.getPartition(newsData.getCreateDate(), true);
			 if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WORD);
			}else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_XLS);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			}
			 indexInfo.setContentID(Long.parseLong(newsData.getContent()));
			 indexInfo.setContentAreaId(partition.getId().toString());
			 indexInfo.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}
		
		StringBuilder newsKeyword = new StringBuilder();
		if(newsData.getKeywords() != null)
			newsKeyword.append(newsData.getKeywords());
		if(newsData.getBrief() != null)
			newsKeyword.append("  "+newsData.getBrief());
		
		if(newsData.getPublishDepartmentName()==null)
		{
			V3xOrgDepartment dept = orgManager.getEntityById(V3xOrgDepartment.class, member.getOrgDepartmentId());
			newsKeyword.append(" "+dept.getName());
		}else{
			newsKeyword.append(" "+newsData.getPublishDepartmentName());
		}
		
		indexInfo.setKeyword(newsKeyword.toString());
		
		indexInfo.setAppType(ApplicationCategoryEnum.news);
		indexInfo.setCreateDate(newsData.getPublishDate());//目前设定的是发布日期，此处存疑
		indexInfo.setAuthor(createUserName);
		AuthorizationInfo authorizationInfo=new AuthorizationInfo();
		if(newsData.getType().getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal()) {
			List<String> owner=new ArrayList<String>();
			owner.add("ALL");
			authorizationInfo.setOwner(owner);
		}else {
			List<String> account=new ArrayList<String>();
			account.add(String.valueOf(newsData.getType().getAccountId()));
			authorizationInfo.setAccount(account);
		}
		indexInfo.setAuthorizationInfo(authorizationInfo);
        IndexUtil.convertToAccessory(indexInfo);
		return indexInfo;
	}

	
	/**
	 * 初始化
	 */
	public void init(){	
		// 12.4 暂时注释，不用升级
		
//		// 附件标记升级数据
//		String hql2 = "from NewsData where attachmentsFlag is null";
//		int total2 = newsDataDao.getQueryCount(hql2, null, null);
//		if(total2 == 0){
//			log.info("新闻数据表不用初始化附件标记，没有 null 数据。");
//		}else{			
//			List<NewsData> allData = newsDataDao.getAll();
//			if(allData == null && allData.size() == 0){
//				log.info("新闻数据表不用初始化附件标记，没有新闻数据。");
//			}else{
//				for(NewsData data : allData){
//					data.setAttachmentsFlag(attachmentManager.hasAttachments(data.getId(),data.getId()));	
//					newsDataDao.update(data);
//				}
//
//				log.info("新闻数据表初始化附件标记完成，共 " + allData.size() + " 条。");
//			}
//		}
	}
	
	/**
	 * 单位空间是否显示管理按钮
	 */
	public boolean showManagerMenu(long memberId){
		boolean isShowAudit =!newsTypeManager.getAuditUnitNewsTypeOnlyByMember(memberId).isEmpty();
		if(isShowAudit)
			return true;
		List list = null;
		try {
			list = this.getTypeListOnlyByMember(memberId, true);
		} catch (Exception e) {
		}
		if(list == null || list.size() == 0)
			return false;
		else
			return true;
	}
	/**
	 * 单位空间是否显示管理按钮
	 */
	public boolean showManagerMenuOfLoginAccount(long memberId){
		boolean isShowAudit =!newsTypeManager.getAuditUnitNewsTypeNoPaging(memberId).isEmpty();
		if(isShowAudit)
			return true;
		List list = null;
		try {
			list = this.getTypeList(memberId, true);
		} catch (Exception e) {
		}
		if(list == null || list.size() == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * 自定义单位/集团空间是否显示管理按钮
	 */
	public boolean showManagerMenuOfCustomSpace(long memberId, long spaceId, int spaceType){
		boolean isShowAudit =!newsTypeManager.getCustomAuditUnitNewsTypeNoPaging(memberId, spaceId, spaceType).isEmpty();
		if(isShowAudit) {
			return true;
		}
		List<NewsType> list = null;
		try {
			list = this.getTypeList(memberId, spaceType, spaceId);
		} catch (Exception e) {
		}
		if(list == null || list.size() == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * 判断某个审核员是否有未审核事项
	 */
	public boolean hasPendingOfUser(Long userId, Long... typeIds){
		List<NewsData> list = this.getPendingData(userId, typeIds);
		if(list != null && list.size() != 0)
			return true;
		else 
			return false;
	}
	
	/**
	 * 得到某个用户需要审核的数据
	 */
	private List<NewsData> getPendingData(Long userId, Long... typeIds){
		if(userId == null)
			return null;
		
		List<NewsType> auditTypes = this.newsTypeManager.getAuditTypeByMember(userId, null, null);
		Set<Long> idset = null;
		if(auditTypes == null || auditTypes.size() == 0)
			return new ArrayList<NewsData>();
		else{
			idset = this.getIdSet(auditTypes);
		}
		
		Set<Long> set = new HashSet<Long>();		
		if(typeIds != null && typeIds.length > 0){
			for(Long id : typeIds){
				if(idset.contains(id))
					set.add(id);
			}			
		}else{
			set = idset;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(NewsData.class);
		
		if(set.size() > 0)
			criteria.add(Restrictions.in("typeId", set));
		else
			return null;
		criteria.add(Restrictions.eq("state", Constants.DATA_STATE_ALREADY_CREATE));
		List<NewsData> list = newsDataDao.executeCriteria(criteria,-1,-1);		
		return list;
	}
	
	/**
	 * 审核员对某个类型的待办总数
	 */
	public int getPendingCountOfUser(Long userId, int spaceType){
		if(userId == null)
			return 0;	
		
		List<NewsType> auditList = this.newsTypeManager.getAuditTypeByMember(userId, 
				Constants.valueOfSpaceType(spaceType), null);
		if(auditList == null || auditList.size() == 0)
			return 0;
		List list = newsDataDao.getPendingCountOfUserDAO(auditList);		
		return (list == null ? 0 : list.size());
	}
	
	/**
	 * 得到状态
	 */
	public int getStateOfData(long id){
		NewsData data = this.getById(id);
		if(data == null)
			return 0;
		else
			return data.getState();
	}
	
	/**
	 * 判断新闻板块审核员是否可用
	 * @param typeId
	 * @return
	 */
	public boolean isAuditUserEnabled(Long typeId) throws Exception {
		NewsType type = this.newsTypeManager.getById(typeId);
		V3xOrgMember auditUser = this.orgManager.getMemberById(type.getAuditUser());
		if(auditUser != null){
			if(!auditUser.getEnabled() || auditUser.getIsDeleted()){
				return false;
			}
		}
		return true;
	}
	
	public boolean typeExist(long typeId){
		NewsType type = this.getNewsTypeManager().getById(typeId);
		return (type != null && type.isUsedFlag());
	}
	
	/**
	 * 
	 */
	public boolean isManagerOfType(long typeId, long userId){
		NewsType type = this.newsTypeManager.getById(typeId);
		if(type == null)
			return false;
		else{
			Set<NewsTypeManagers> set = type.getNewsTypeManagers();
			for(NewsTypeManagers t : set){
				if(t.getManagerId().longValue() == userId && t.getExt1().equals(Constants.MANAGER_FALG))
					return true;
			}
			return false;
		}
			
	}
	
	
	/**
	 * 综合查询
	 */
	public List<NewsData> iSearch(ConditionModel cModel,long loginAccount){
		List<NewsType> inList= new ArrayList<NewsType>();
			
		List<NewsType> 	typeList2 = this.getAllTypeList(loginAccount);			
		List<NewsType> 	typeList3 = this.getGroupAllTypeList();
		if(typeList2 != null)
			inList.addAll(typeList2);
		if(typeList3 != null)
			inList.addAll(typeList3);
		
		List<NewsData> list = null;
		
		List<Long> typeIds = new ArrayList<Long>();
		Map<String,Object> parameter = new HashMap<String,Object>();
		if (inList.isEmpty()) {
			return new ArrayList<NewsData>();
		}else{
			for(NewsType t : inList){
				typeIds.add(t.getId());
			}
		}
		DetachedCriteria criteria = this.filterByReadGetHql();
		criteria.add(Restrictions.in("typeId", typeIds));
		
		String title = cModel.getTitle();
		final Date beginDate = cModel.getBeginDate();
		final Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		
		StringBuffer sb = new StringBuffer();
		if(Strings.isNotBlank(title)){
			criteria.add(Restrictions.like("title", "%"+SQLWildcardUtil.escape(title)+"%"));
		}
		if(fromUserId != null ){
			criteria.add(Restrictions.eq("publishUserId", fromUserId));
		}
		if(beginDate != null){
			criteria.add(Restrictions.ge("publishDate", beginDate));
		}
		if(endDate != null){
			criteria.add(Restrictions.le("publishDate", endDate));
		}
		
		list = newsDataDao.executeCriteria(criteria);	
		this.initList(list);
		return list;
	}
	private Set<Long> getIdSet(Collection<NewsType> coll){
		Set<Long> set = new HashSet<Long>();
		if(coll == null || coll.size() == 0)
			return set;
		for(NewsType bt : coll){
			set.add(bt.getId());
		}
		return set;
	}

	public NewsBodyDao getNewsBodyDao() {
		return newsBodyDao;
	}

	public void setNewsBodyDao(NewsBodyDao newsBodyDao) {
		this.newsBodyDao = newsBodyDao;
	}
	
	/**
	 * 取正文
	 */
	public NewsBody getBody(long newsDataId){
		return this.newsBodyDao.getByDataId(newsDataId);
	}	

	/**
	 * 协同转新闻
	 * @throws BusinessException 
	 */
	public void saveCollNews(NewsData data) throws BusinessException {
		//板块类型
		Long typeId=data.getTypeId();
		NewsType type=this.getNewsTypeManager().getById(typeId);
		data.setType(type);
		
//		状态
		if(data.getState() == null){
			data.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
		}
		//保存标新天数
		NewsType bulType=this.getNewsTypeManager().getById(data.getTypeId());
		data.setTopOrder(bulType.getTopCount());
		
		boolean isNew = true;
		boolean firstInitFlag=true;
		save(data, isNew);
		
//		这里加入全文检索
		try {
			if(data.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)){
				if(firstInitFlag){
					IndexInfo info=this.getIndexInfo(data.getId());
					indexManager.index(info);
				}
			}
		} catch (Exception e) {
			log.error("全文检索: ", e);
		}
		
	}

	public void updateClick(long dataId, int clickNumTotal, Collection<ClickDetail> details) {
//		log.info("新闻开始更新点击数据 " + dataId + " 点击次数：" + clickNumTotal);
//		log.info("详细：" + details);
		String hql = "update NewsData set readCount = ? where id = ?";
		this.newsDataDao.bulkUpdate(hql, null, clickNumTotal, dataId);
		
	}
	
	public NewsDataLock lock(Long newdatasid, String action) {
		return this.lock(newdatasid, CurrentUser.get().getId(), action);
	}
	
	public Map<Long, NewsDataLock> getLockInfo4Dump() {
		return this.newsdataLockMap;
	}
	
	public NewsDataLock lock(Long newdatasid, Long currentUserId, String action) {
		//进行文件锁的检查,方件锁是接口中的一个对象是不会抛空指针的
		NewsDataLock newslock=null;
		if(this.newsdataLockMap==null)
		{
			this.newsdataLockMap=new HashMap<Long, NewsDataLock>();
		}
		if(this.newsdataLockMap.containsKey(newdatasid))
		{
			//文件已加锁
			newslock=this.newsdataLockMap.get(newdatasid);
			/**
			 * 如果操作类型相同，且锁的对象与当前用户相同，也允许用户继续进行同一操作
			 * 仅当两种不同操作同时在进行时，锁才确定生效，比如同一人进行编辑和审核操作，或者两人分别进行编辑或审核操作
			 */
			if(newslock.getUserid()==currentUserId && action.equals(newslock.getAction()))
				return null;
			
			return newslock;
		}else
		{
			//文件没有加锁,对其加锁,继续进行相关的操作
			newslock=new NewsDataLock();
			newslock.setNewsid(newdatasid);
			newslock.setUserid(currentUserId);
			newslock.setAction(action);
			this.newsdataLockMap.put(newdatasid, newslock);
			//发送通知
			NotificationManager.getInstance().send(NotificationType.NewsLock, newslock);
			return null;
		}
	}

	public void unlock(Long newdatasid) {
		if(this.newsdataLockMap==null)
		{
			this.newsdataLockMap=new HashMap<Long, NewsDataLock>();
		}
		if(this.newsdataLockMap.containsKey(newdatasid))
		{
			this.newsdataLockMap.remove(newdatasid);
			// 发送通知
			NotificationManager.getInstance().send(NotificationType.NewsUnLock, newdatasid);
		}
	}
	
	/**
	 * 该板块存在待审核新闻，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核新闻转给新的审核员
	 * @param newsTypeId   对应的新闻板块ID
	 * @param oldAuditorId 旧审核员ID(对应人员已不可用)
	 * @param newAuditorId 新审核员ID
	 */
	public void transferWait4AuditBulDatas2NewAuditor(Long newsTypeId, Long oldAuditorId, Long newAuditorId) {
		this.newsDataDao.transfer2NewAuditor(newsTypeId, oldAuditorId, newAuditorId);
	}
	/**
	 * 根据附件ID取正文
	 */
	public NewsBody getBodyByFileId(String fileid){
		return this.newsBodyDao.getByFileId(fileid);
	}	
	//异步调用格式
	public String newsFormat(String templateId)
	{
		if (StringUtils.isNotBlank(templateId))
		{
			MtContentTemplateCAP template = mtContentTemplateManagerCAP
					.getById(Long.valueOf(templateId));// 根据新闻格式ID取格式
			if (template != null)
			{
				return template.getContent();
			}
		}
		return "";
	}
	
	public void setMtContentTemplateManagerCAP(MtContentTemplateManagerCAP mtContentTemplateManagerCAP) {
		this.mtContentTemplateManagerCAP = mtContentTemplateManagerCAP;
	}

	public PartitionManager getPartitionManager()
	{
		return partitionManager;
	}

	public void setPartitionManager(PartitionManager partitionManager)
	{
		this.partitionManager = partitionManager;
	}
}