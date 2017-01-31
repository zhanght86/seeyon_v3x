	package com.seeyon.v3x.news.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.dao.NewsTypeDao;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.domain.NewsTypeManagers;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.util.EntityKeeper;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 新闻类型的Manager接口实现类
 * @author wolf
 *
 */
public class NewsTypeManagerImpl extends BaseNewsManager implements NewsTypeManager {
	private static final Log log = LogFactory.getLog(NewsTypeManagerImpl.class);
	private SpaceManager spaceManager;
	// 2008.04.14 lihf 版块信息的内存加载
	// Map<bulTypeId, BulType>
	private static Map<Long, NewsType> typesMap = null;
	// Map<memberId, Set<BulTypeId>>
	private static Map<Long, Set<Long>> writerMap = null;
	// Map<memberId, Set<BulTypeId>>
	private static Map<Long, Set<Long>> managerMap = null;
	// Map<memberId, Set<BulTypeId>>
	private static Map<Long, Set<Long>> auditMap = null;
	// Map<accountId, Set<typeName>> 集团id用 -1 代表， 不包含部门
	public static final Long GROUP_ACCOUNT_ID = -1L;
	private static Map<Long, Set<String>> nameMap = null;
	private static boolean initialized = false;
	private OrgManager orgManager;
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	private EntityKeeper entityKeeper;
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	
	private String getMemberNameByUserId(long id)
	{
		if(entityKeeper==null) return this.getNewsUtils().getMemberNameByUserId(id);
		V3xOrgMember member;
		try {
			member = entityKeeper.getMemberById(id);
			return (member!=null)?member.getName():"";
		} catch (BusinessException e) {
			log.error("获取实体出错", e);
		}
		return "";
	}
	
	public synchronized void init(){
		if(initialized)
			return;
		typesMap = new HashMap<Long, NewsType>();
		writerMap = new HashMap<Long, Set<Long>>();
		managerMap = new HashMap<Long, Set<Long>>();
		auditMap = new HashMap<Long, Set<Long>>();
		List<NewsType> list = this.newsTypeDao.getAll();
		if(list != null && list.size() > 0){
			for(NewsType nt : list){
				typesMap.put(nt.getId(), nt);
				this.addAclOfType(nt);
			}
		}
		this.initStaticNoObj();
		
		initialized = true;
		log.info("新闻版块加载完成。");
	}
	
	/**
	 * 获取全部板块信息
	 * @return
	 */
	public Collection<NewsType> getAllNewsTypes() {
		return typesMap.values();
	}
	
	private void addName(NewsType type){
		if(type == null)
			return;
		if(type.isUsedFlag())
			if(type.getSpaceType().intValue() == Constants.NewsTypeSpaceType.corporation.ordinal()){
				this.putStringMap(nameMap, type.getAccountId(), type.getTypeName());
			}else if(type.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal()){
				this.putStringMap(nameMap, GROUP_ACCOUNT_ID, type.getTypeName());
			}
	}
	private void putStringMap(Map<Long, Set<String>> map, Long key, String value){
		if(map == null || key == null || value == null)
			return;

		Set<String> set = map.get(key);
		if(set == null)
			set = new HashSet<String>();
		set.add(value);
		map.put(key, set);
	}
	private void addAclOfType(NewsType type){
		if(type == null)
			return;
		
		long newsTypeId = type.getId();
		if(type.getNewsTypeManagers() == null)
			type.setNewsTypeManagers(new HashSet<NewsTypeManagers>());
		for(NewsTypeManagers ntm : type.getNewsTypeManagers()){
			if(Constants.MANAGER_FALG.equals(ntm.getExt1())){
				this.putMap(managerMap, ntm.getManagerId(), newsTypeId);
			}else if(Constants.WRITE_FALG.equals(ntm.getExt1())){
				this.putMap(writerMap, ntm.getManagerId(), newsTypeId);
			}
		}
		if(type.isAuditFlag()){
			this.putMap(auditMap, type.getAuditUser(), newsTypeId);
		}
	}
	private void putMap(Map<Long, Set<Long>> map, Long key, Long value){
		if(map == null || key == null || value == null)
			return;

		Set<Long> set = map.get(key);
		if(set == null)
			set = new HashSet<Long>();
		set.add(value);
		map.put(key, set);
	}
	public synchronized void initPartAdd(NewsType newsType){
		if(newsType == null)
			return;
		entityKeeper = new EntityKeeper(orgManager);
		initialized = false;
		typesMap.put(newsType.getId(), newsType);
		this.addAclOfType(newsType);
		this.addName(newsType);
		this.initStaticNoObj();
		initialized = true;
		// 发送通知
		NotificationManager.getInstance().send(NotificationType.NewsAddType, newsType);
	}
	private void deleteAclOfType(Long typeId){
		if(typeId == null)
			return;
		this.deleteFromMap(writerMap, typeId);
		this.deleteFromMap(managerMap, typeId);
		this.deleteFromMap(auditMap, typeId);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deleteFromMap(Map<Long, Set<Long>> map, Long typeId){
		if(map == null)
			map = new HashMap<Long, Set<Long>>();
		/*Set<Long> wks = map.keySet();
		for(Long ks : wks){
			Set<Long> v = map.get(ks);
			if(v != null)
				v.remove(typeId);
		}*/
		Set<Map.Entry<Long, Set<Long>>> mapSet = map.entrySet();
		for (Iterator iter = mapSet.iterator(); iter.hasNext();) {
			Map.Entry<Long, Set<Long>> element = (Map.Entry<Long, Set<Long>>) iter.next();
			if(element.getValue()!=null) {
				element.getValue().remove(typeId);
			}			
		}
	}
	public synchronized void initPartEdit(NewsType newsType){
		if(newsType == null)
			return;
		entityKeeper = new EntityKeeper(orgManager);
		initialized = false;
		typesMap.put(newsType.getId(), newsType);
		this.deleteAclOfType(newsType.getId());
		this.addAclOfType(newsType);
		this.initStaticNoObj();
		initialized = true;
		// 发送通知
		NotificationManager.getInstance().send(NotificationType.NewsUpdateType, newsType);
	}
	/**
	 * 新闻板块删除后来改为逻辑删除，此方法实际已不再使用
	 * @deprecated
	 * @param bulType
	 */
	@SuppressWarnings("unused")
	private synchronized void initPartDelete(NewsType bulType){
		if(bulType == null)
			return;
		initialized = false;
		typesMap.remove(bulType.getId());
		this.deleteAclOfType(bulType.getId());
		this.initStaticNoObj();
		initialized = true;
	}
	private void initStaticNoObj(){
		nameMap = new HashMap<Long, Set<String>>();
		Collection<NewsType> values = typesMap.values();
		for(NewsType bt : values){
			this.addName(bt);
			this.setStaticOthers(bt);
		}
	}
	private void setStaticOthers(NewsType type){
		type.setAuditUserName("");
		type.setManagerUserIds("");
		type.setManagerUserNames("");
		//审核员
		if(type.isAuditFlag()){
			type.setAuditUserName(getMemberNameByUserId(type.getAuditUser()));
		}
		//管理员
		String ids="";
		String names="";
		
		// 保持排序 added by Meng Yang at 2009-06-30
		List<NewsTypeManagers> sortedList = new ArrayList<NewsTypeManagers>();
		for(NewsTypeManagers tm:type.getNewsTypeManagers()){
			if(!Constants.MANAGER_FALG.equals(tm.getExt1())) {
				continue;
			}
			sortedList.add(tm);
		}
		Collections.sort(sortedList);
		
		//for(NewsTypeManagers tm:type.getNewsTypeManagers()){
			//if(!Constants.MANAGER_FALG.equals(tm.getExt1())) continue;
		for(NewsTypeManagers tm : sortedList){
			ids=ids+","+tm.getManagerId();
			names=names+","+getMemberNameByUserId(tm.getManagerId());				
		}
		type.setManagerUserIds(ids.length()>0?ids.substring(1):"");
		type.setManagerUserNames(names.length()>0?names.substring(1):"");
	}
	
	private NewsTypeDao newsTypeDao;

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#getNewsTypeDao()
	 */
	public NewsTypeDao getNewsTypeDao() {
		return newsTypeDao;
	}
	
	public void setNewsTypeDao(NewsTypeDao newsTypeDao) {
		this.newsTypeDao = newsTypeDao;
	}

	NewsTemplateManager newsTemplateManager;

	public NewsTemplateManager getNewsTemplateManager() {
		return newsTemplateManager;
	}
	
	public void setNewsTemplateManager(NewsTemplateManager newsTemplateManager) {
		this.newsTemplateManager = newsTemplateManager;
	}
	
	NewsTypeManagersManager newsTypeManagersManager;

	public NewsTypeManagersManager getNewsTypeManagersManager() {
		return newsTypeManagersManager;
	}
	
	public void setNewsTypeManagersManager(NewsTypeManagersManager newsTypeManagersManager) {
		this.newsTypeManagersManager = newsTypeManagersManager;
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#delete(java.lang.Long)
	 */
//	public void delete(Long id) throws BusinessException {
//		NewsType type=this.getNewsTypeDao().get(id);
//		if(type==null) throw new BusinessException("news_type_NoExists");
//		if(type.getSpaceType().intValue()==2){
//			throw new BusinessException("news_type_system");
//		}
////		if(type.getNewsDatas().size()>0) throw new BusinessException("news_type_AlreadyUsed");
//		
//		if(type.getNewsTypeManagers()!=null){
//			this.getNewsTypeManagersManager().deletes(type);
//		}
//		newsTypeDao.getSessionFactory().getCurrentSession().flush();
//		newsTypeDao.delete(id);
//	}
	
//	public void delDept(Long id) throws BusinessException {
//		
////		//级联删除新闻板块-管理员-板块下数据-数据
////		Session session = newsTypeDao.getSessionFactory().openSession();
////		NewsType a = (NewsType)session.get(NewsType.class,id);
////		if(a!=null){
////			session.delete(a);
////			session.flush();
////			session.close();
////		}
//		
//		NewsType type = newsTypeDao.get(id);
//		if(type != null)
//			newsTypeDao.delete(type);
//		
////		newsTypeDao.delete(id);
//	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#deletes(java.util.List)
	 */
//	public void deletes(List<Long> ids) throws BusinessException {
//		for(Long id:ids){			
////			delete(id);
//			delDept(id);
//		}
//	}
	
	/**
	 * 初始化新闻类型列表
	 * @param list
	 */
	public void initList(List<NewsType> list){		
//		// 一次加载所有管理员
//		List<NewsTypeManagers> mngrs = newsTypeManagersManager.getTypeManagers(list);
//		Map<Long, Set<NewsTypeManagers>> map = new HashMap<Long, Set<NewsTypeManagers>>();
//		if(mngrs != null){			
//			for(NewsTypeManagers t : mngrs){
//				Set<NewsTypeManagers> set = map.get(t.getType().getId());
//				if(set == null)
//					set = new HashSet<NewsTypeManagers>();
//				set.add(t);
//				map.put(t.getType().getId(), set);
//			}
//		}
		
		for(NewsType type:list){
//			Set<NewsTypeManagers> set = map.get(type.getId());
//			if(set == null){
//				set = new HashSet<NewsTypeManagers>();
//			}
//			Set<NewsTypeManagers> oldset = type.getNewsTypeManagers();
//			if(oldset == null)
//				oldset = new HashSet<NewsTypeManagers>();
//			oldset.addAll(set);
//			type.setNewsTypeManagers(oldset);
			
			initType(type);			
		}
	}

	/**
	 * 初始化新闻类型
	 * 1、初始化创建者姓名
	 * 2、初始化审核员姓名
	 * 3、初始化该新闻类型的管理员姓名列表
	 * @param type
	 */
	private void initType(NewsType type) {
//		User user = CurrentUser.get();
//		long cur = -1L;
//		if(user != null)
//			cur = user.getId();
//		//创建者
//		//type.setCreateUserName(this.getNewsUtils().getMemberNameByUserId(type.getCreateUser()));
//		//审核员
//		if(type.isAuditFlag()){
//			type.setAuditUserName(this.getNewsUtils().getMemberNameByUserId(type.getAuditUser()));
//		
//			if(type.getAuditUser().longValue() == cur){
//				type.setCanAuditOfCurrent(true);
//
//			}
//		}
//		//管理员
//		String ids="";
//		String names="";
//		for(NewsTypeManagers tm:type.getNewsTypeManagers()){
//			if(tm.getManagerId().longValue() == cur){
//				if(Constants.MANAGER_FALG.equals(tm.getExt1())){
//					type.setCanAdminOfCurrent(true);
//					type.setCanNewOfCurrent(true);
//				}
//					
//				if(Constants.WRITE_FALG.equals(tm.getExt1()))
//					type.setCanNewOfCurrent(true);
//			}
//			
//			if(!Constants.MANAGER_FALG.equals(tm.getExt1())) continue;
//			ids=ids+","+tm.getManagerId();
//			names=names+","+this.getNewsUtils().getMemberNameByUserId(tm.getManagerId());				
//		}
//		type.setManagerUserIds(ids.length()>0?ids.substring(1):"");
//		type.setManagerUserNames(names.length()>0?names.substring(1):"");
		

	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#findAll()
	 *	为了在Manager层不用CurrentUser
	 */
	public List<NewsType> findAll(long loginAccount) {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		for(NewsType bt : typesMap.values()){
			if(bt.getAccountId().longValue() == loginAccount
					&& bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.corporation.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	/**
	 * 获取当前用户登录单位的所有自定义空间新闻板块（自定义单位空间/自定义集团空间）
	 * @param loginAccount
	 * @return
	 * @throws BusinessException 
	 */
	public List<NewsType> findAllCustom(long loginAccount) throws BusinessException {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		List<Long> customSpaceIds = spaceManager.getAllCustomSpace();
		for(NewsType bt : typesMap.values()){
			if(customSpaceIds.contains(bt.getAccountId())
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	/**
	 * 获取自定义单位/集团所有的新闻板块
	 * @param spaceId
	 * @param spaceType
	 * @return
	 */
	public List<NewsType> findAllOfCustomAcc(long spaceId, int spaceType) {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		for(NewsType bt : typesMap.values()){
			if(bt.getAccountId().longValue() == spaceId
					&& bt.getSpaceType().intValue() == spaceType
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	
	public List<NewsType> findAllOfCustom(long loginAccount, String spaceType) {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		if ("custom".equals(spaceType)) {
			for(NewsType bt : typesMap.values()){
				if(bt.getAccountId().longValue() == loginAccount
						&& bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.custom.ordinal()
						&& bt.isUsedFlag())
					list.add(bt);
			}
		} else if ("publicCustom".equals(spaceType)) {
			for(NewsType bt : typesMap.values()){
				if(bt.getAccountId().longValue() == loginAccount
						&& bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.public_custom.ordinal()
						&& bt.isUsedFlag())
					list.add(bt);
			}
		} else if ("publicCustomGroup".equals(spaceType)) {
			for(NewsType bt : typesMap.values()){
				if(bt.getAccountId().longValue() == loginAccount
						&& bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.public_custom_group.ordinal()
						&& bt.isUsedFlag())
					list.add(bt);
			}
		}
		initList(list);
		return list;
	}
	
	/**
	 * 这个是为了保留以前的方法,将来会删除的
	 * @return
	 */
	public List<NewsType> findAll() {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		for(NewsType bt : typesMap.values()){
			if(bt.getAccountId().longValue() == CurrentUser.get().getLoginAccount()
					&& bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.corporation.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	
	
	/**
	 * 
	 * 查询所有的单位新闻板块---分页
	 * 
	 */
	public List<NewsType> findAllByPage(long loginAccount) {
//		DetachedCriteria dc=DetachedCriteria.forClass(NewsType.class);
//		添加集团化处理
//		dc.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
//		dc.add(Restrictions.eq("spaceType", Constants.NewsTypeSpaceType.corporation.ordinal()));
//		List<NewsType> list= dc.getExecutableCriteria(this.newsTypeDao.getSessionFactory().getCurrentSession()).list();
		
//		String hql = " from NewsType where accountId = " + CurrentUser.get().getLoginAccount()
//			+ " and spaceType = " + Constants.NewsTypeSpaceType.corporation.ordinal()
//			+ " and usedFlag = true";
//	
////	    if (Pagination.isNeedCount()) {
//	        int rowCount = newsTypeDao.getQueryCount(hql, null, null);
//	        Pagination.setRowCount(rowCount);
////	    }
//	    final String hqlf = hql;
//		
//	    List<NewsType> list = (List<NewsType>)newsTypeDao.getHibernateTemplate().execute(new HibernateCallback(){
//			public Object doInHibernate(Session session) throws HibernateException, SQLException {
//				return session.createQuery(hqlf).setFirstResult(Pagination.getFirstResult())
//	    		.setMaxResults(Pagination.getMaxResults()).list();
//			}
//		});
//		
//		
//		initList(list);
//		return list;
		List<NewsType> list = this.findAll(loginAccount);
		this.sortList(list);
		List<NewsType> subList = this.getNewsUtils().paginate(list);		
		return subList;
		
	}
	
	/**
	 * 查询所有的自定义单位新闻板块
	 */
	public List<NewsType> findAllByCustomAccId(long spaceId, int spaceType, boolean isPage) {
		List<NewsType> list = this.findAllOfCustomAcc(spaceId, spaceType);
		this.sortList(list);
		if (isPage) {
			List<NewsType> subList = this.getNewsUtils().paginate(list);
			return subList;
		}
		return list;
	}
	
	/**
	 * 
	 * 查询所有的单位新闻板块---不分页
	 * 
	 */
	
	public List<NewsType> findAllByNoPage(long loginAccount) {
		List<NewsType> list = this.findAll(loginAccount);
		this.sortList(list);
		return list;
		
	}
	
	/**
	 * 
	 */
	public List<NewsType> groupFindAll() {
		List<NewsType> list = new ArrayList<NewsType>();
		this.checkTypesMap();
		for(NewsType bt : typesMap.values()){
			if(bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	
	

	/**
	 * 
	 * 查询所有的集团新闻板块--分页
	 * 
	 */
	public List<NewsType> groupFindAllByPage() {
//		
//		String hql = " from NewsType where spaceType = " + Constants.NewsTypeSpaceType.group.ordinal();
//	
////	    if (Pagination.isNeedCount()) {
//	        int rowCount = newsTypeDao.getQueryCount(hql, null, null);
//	        Pagination.setRowCount(rowCount);
////	    }
//	    final String hqlf = hql;
//		
//	    List<NewsType> list = (List<NewsType>)newsTypeDao.getHibernateTemplate().execute(new HibernateCallback(){
//			public Object doInHibernate(Session session) throws HibernateException, SQLException {
//				return session.createQuery(hqlf).setFirstResult(Pagination.getFirstResult())
//	    		.setMaxResults(Pagination.getMaxResults()).list();
//			}
//		});
//		
//		
//		initList(list);
//		return list;
		List<NewsType> all = this.groupFindAll();
		this.sortList(all);
		List<NewsType> list = this.getNewsUtils().paginate(all);		
		return list;
		
	}
	
	
	
	/**
	 * 
	 * 查询所有的集团新闻板块--不分页
	 * 
	 */
	
	public List<NewsType> groupFindAllByNoPage() {
		
		List<NewsType> list = this.groupFindAll();
		this.sortList(list);
		return list;
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<NewsType> findByProperty(String property, Object value,long loginAccount) {
		List<NewsType> all = this.findByPropertyNoPaging(property, value,loginAccount);
		this.sortList(all);
		return this.getNewsUtils().paginate(all);
	}
	public List<NewsType> findByPropertyNoPaging(String property, Object value,long loginAccount) {
		List<NewsType> list = this.findAll(loginAccount);

		list = this.filterByProperty(list, property, value);
		this.sortList(list);
		return list;
	}
	
	public List<NewsType> groupFindByProperty(String property, Object value) {
		List<NewsType> src = this.groupFindAll();
		src = this.filterByProperty(src, property, value);
		this.sortList(src);
		List<NewsType> list = this.getNewsUtils().paginate(src);
		
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#getById(java.lang.Long)
	 */
	public NewsType getById(Long id) {
		this.checkTypesMap();
		NewsType type = typesMap.get(id);
		if(type == null){
			return null;
		}
		initType(type);
		return type;
	}

	public NewsType save(NewsType type) throws NewsException {
		boolean isNew = false;
		if (type.isNew()) {
			isNew = true;
			if (type.getSpaceType() == 4) {
				type.setId(type.getAccountId());
			} else {
				type.setIdIfNew();
			}
			if (type.getAccountId() == null) {
				type.setAccountId(CurrentUser.get().getLoginAccount());
			}
			type.setUsedFlag(true);
			newsTypeDao.save(type);
		} else {
			newsTypeDao.update(type);
		}

		// 保存新闻管理员
		String[] manages = new String[0];
		if (Strings.isNotBlank(type.getManagerUserIds())) {
			manages = type.getManagerUserIds().split(",");
		}
		newsTypeManagersManager.saveAclByTypeManager(type, manages, Constants.MANAGER_FALG);

		// 内存加载
		if (isNew)
			this.initPartAdd(type);
		else
			this.initPartEdit(type);

		return type;
	}
		
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#saveWriteByType(java.lang.Long, java.lang.Long[])
	 */
	public void saveWriteByType(Long typeId,String[][] writeIds){
		NewsType type=this.getById(typeId);
		this.getNewsTypeManagersManager().saveAclByType(type, writeIds, Constants.WRITE_FALG);//.saveWriteByType(type, writeIds);
		
		// 内存同步
		this.initPartEdit(type);
	}

	public boolean isGroupNewsTypeManager(long memberId){
		this.checkTypesMap();
		Set<Long> set = managerMap.get(memberId);
		return this.spaceTypeChecked(set, Constants.NewsTypeSpaceType.group);
	}
	private boolean spaceTypeChecked(Collection<Long> typeIds, Constants.NewsTypeSpaceType spaceType){
		if(typeIds == null || spaceType == null)
			return false;		
		
		for(Long id : typeIds){
			NewsType bt = typesMap.get(id);
			if(bt == null)
				continue;
			if(bt.getSpaceType().intValue() == spaceType.ordinal())
				return true;
		}
		
		return false;
	}
	
	public boolean isGroupNewsTypeAuth(long memberId){
		this.checkTypesMap();
		Set<Long> set = auditMap.get(memberId);
		return this.spaceTypeChecked(set, Constants.NewsTypeSpaceType.group);
	}
	
	public List<NewsType> getAuditGroupNewsTypeNoPaging(long memberId){
		return this.getAclTypeByMember(memberId, Constants.NewsTypeSpaceType.group, null, Constants.NewsTypeAclType.audit);
	}
	//取的有我来审核的集团新闻板块
	public List<NewsType> getAuditUnitNewsTypeNoPaging(long memberId){
		return this.getAclTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount(), Constants.NewsTypeAclType.audit);
	}
	
	public List<NewsType> getCustomAuditUnitNewsTypeNoPaging(long userId, long spaceId, int spaceType) {
		if (spaceType == 5) {
			return this.getAclTypeByMember(userId, Constants.NewsTypeSpaceType.public_custom, spaceId, Constants.NewsTypeAclType.audit);
		} else {
			return this.getAclTypeByMember(userId, Constants.NewsTypeSpaceType.public_custom_group, spaceId, Constants.NewsTypeAclType.audit);
		}
	}
	
	public List<NewsType> getAuditUnitNewsTypeOnlyByMember(long memberId){
		return this.getAclTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, null, Constants.NewsTypeAclType.audit);
	}
	
	/**
	 * 初始化类型下总数
	 */
	@SuppressWarnings("rawtypes")
	public void setTotalItemsOfType(List<NewsType> types){
		if(types == null || types.size() == 0)
			return;
		
		List<Long> typeIds = new ArrayList<Long>();
		Map<Long, Integer> countMap = new HashMap<Long, Integer>();
		for(NewsType t : types){
			typeIds.add(t.getId());
			countMap.put(t.getId(), 0);
		}
		
		try{
			
		    Map<String,Object> parameter = new HashMap<String,Object>();
			final String hqlf = "select data.typeId from NewsData as data where data.typeId in (:typeId) "
				+ " and data.state=:state" 
				+ " and data.deletedFlag=false ";
			parameter.put("typeId", typeIds);
            parameter.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
            
			List list = newsTypeDao.find(hqlf, -1, -1, parameter);
			/*List list = (List)newsTypeDao.getHibernateTemplate().execute(new HibernateCallback(){
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					return session.createQuery(hqlf).list();
				}
	    	});*/
						
			for(Object o : list){
				Long typeId = (Long)o;
				countMap.put(typeId, countMap.get(typeId) + 1);
			}
	
			for(NewsType t : types){
				Integer total = countMap.get(t.getId());
				if(total == null)
					continue;
				t.setTotalItems(total);
			}
		}catch(Exception e){
		}
	}
	
	/**
	 * 逻辑删除版块
	 */
	public void setTypeDeleted(List<Long> ids){
		if(ids == null || ids.size() == 0)
			return;
		
		for(Long id : ids){
			NewsType bt = this.getById(id);
			bt.setUsedFlag(false);
			this.newsTypeDao.update(bt);
			
			//
			this.initPartEdit(bt);
		}
	}
	
	/**
	 * 用于新建单位时初始化新闻板块
	 * @param accountId  单位id
	 */
	public void initNewsType(long accountId) {
		List<NewsType> types = this.newsTypeDao.getByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if(types != null) {
			Date today = new Date();
			for(NewsType type:types) {
				NewsType newType = new NewsType();
//				newType.setIdIfNew();
				newType.setAccountId(accountId);
				newType.setAuditFlag(type.isAuditFlag());
				newType.setAuditUser(type.getAuditUser());
				newType.setCreateDate(today);
				newType.setCreateUser(type.getCreateUser());
				newType.setSpaceType(type.getSpaceType());
				newType.setTopCount(type.getTopCount());
				newType.setTypeName(type.getTypeName());
				newType.setUpdateDate(today);
				newType.setUsedFlag(true);
				newType.setOutterPermit(type.getOutterPermit());
				newType.setSortNum(type.getSortNum());
				try {
					this.save(newType);
				} catch (NewsException e) {
				}
			}
		}
	}
	
	private List<NewsType> getAclTypeByMember(Long memberId, Constants.NewsTypeSpaceType spaceType, 
			Long accountId, Constants.NewsTypeAclType aclType){
		List<NewsType> ret = new ArrayList<NewsType>();
		if(memberId == null || aclType == null)
			return ret;
		Map<Long, Set<Long>> map = this.getAclMap(aclType);		
//		Set<Long> set = map.get(memberId);
		Set<Long> set = new HashSet<Long>();
		
		List<Long> orgIds=null;
		try {
			orgIds = orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
					V3xOrgEntity.ORGENT_TYPE_ACCOUNT,
					V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
					V3xOrgEntity.ORGENT_TYPE_TEAM,
					V3xOrgEntity.ORGENT_TYPE_POST,
					V3xOrgEntity.ORGENT_TYPE_LEVEL,
					V3xOrgEntity.ORGENT_TYPE_MEMBER);
		} catch (BusinessException e) {
			
		}
		
		if(orgIds != null){
			for(long key : map.keySet()){
				if(orgIds.contains(key))
					set.addAll(map.get(key));
				
			}
		}
		Set<NewsType> src = this.getTypesByIds(set);
		if(spaceType == null){
			for(NewsType bt : src){
				if(bt.isUsedFlag()){
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.NewsTypeSpaceType.corporation.ordinal()){
			for(NewsType bt : src){
				if(bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.corporation.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.NewsTypeSpaceType.group.ordinal()){
			for(NewsType bt : src){
				if(bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group.ordinal() && bt.isUsedFlag()){
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.NewsTypeSpaceType.public_custom.ordinal()){
			for(NewsType bt : src){
				if(bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.public_custom.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.NewsTypeSpaceType.public_custom_group.ordinal()){
			for(NewsType bt : src){
				if(bt.getSpaceType().intValue() == Constants.NewsTypeSpaceType.public_custom_group.ordinal() && bt.isUsedFlag()){
					if (accountId != null && bt.getAccountId().longValue() != accountId.longValue()) {
						continue;
					}
					ret.add(bt);
				}
			}
		}
		
		this.initList(ret);
		
		return ret;
	}
	private Map<Long, Set<Long>>  getAclMap(Constants.NewsTypeAclType aclType){
		this.checkTypesMap();
		int param = aclType.ordinal();
		if(param == Constants.NewsTypeAclType.manager.ordinal())
			return managerMap;
		else if(param == Constants.NewsTypeAclType.audit.ordinal())
			return auditMap;
		else if(param == Constants.NewsTypeAclType.writer.ordinal())
			return writerMap;
		
		return new HashMap<Long, Set<Long>>();
	}
	// 
	private Set<NewsType> getTypesByIds(Collection<Long> ids){
		Set<NewsType> set = new HashSet<NewsType>();
		if(ids == null)
			return set;
		this.checkTypesMap();
		for(Long id : ids){
			NewsType bt = typesMap.get(id);
			if(bt != null)
				set.add(bt);
		}
		return set;
	}
	private void checkTypesMap(){
		if(!initialized)
			this.init();
	}
	// 条件过滤
	private List<NewsType> filterByProperty(List<NewsType> list, String attribute, Object value){
		if(list == null || attribute == null || value == null)
			return list;
		
		List<NewsType> ret = new ArrayList<NewsType>();
		for(NewsType bt : list){
			Object trueVal = this.getNewsUtils().getAttributeValue(bt, attribute);
			if(trueVal == null)
				continue;
			boolean passed = false;
			if(value instanceof String){
				if(trueVal.toString().toLowerCase().indexOf(value.toString().toLowerCase()) != -1)
					passed = true;
			}else{
				if(value.equals(trueVal))
					passed = true;
			}
			if(passed)
				ret.add(bt);
		}
		
		return ret;
	}
	// 
	private void sortList(List<NewsType> list){
		if(list == null)
			return;
		Collections.sort(list);
	}
	
	
	/**
	 * 得到 writeFlag 的NewsTypeManagers
	 * 
	 */
	public List<NewsTypeManagers> findTypeWriters(NewsType bt) {
		List<NewsTypeManagers> ret = new ArrayList<NewsTypeManagers>();
		if(bt == null)
			return ret;
		Set<NewsTypeManagers> set = bt.getNewsTypeManagers();
		
		if(set != null)
			for(NewsTypeManagers btm : set){
				if(Constants.WRITE_FALG.equals(btm.getExt1()))
						ret.add(btm);
			}
		
		return ret;
	}
	
	/**
	 * 得到可以审核的版块列表
	 * 单位类型时，accountId 为 null 说明不验证单位
	 *
	 */
	public List<NewsType> getAuditTypeByMember(Long memberId, Constants.NewsTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.NewsTypeAclType.audit);
	}
	public List<NewsType> getManagerTypeByMember(Long memberId, Constants.NewsTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.NewsTypeAclType.manager);
	}
	public List<NewsType> getWriterTypeByMember(Long memberId, Constants.NewsTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.NewsTypeAclType.writer);
	}
	
	/**
	 * 判断用户是否管理员
	 */
	public boolean isManagerOfType(long typeId, long userId){
		this.checkTypesMap();
		Set<Long> set = managerMap.get(userId);
		if(set == null)
			return false;
		return set.contains(typeId);
		
	}
	
	/**
	 * 用于更新新闻板块的排序顺序
	 */
	public void updateNewsTypeOrder(String[] newsTypeIds) {
		if (newsTypeIds == null) {
			return;
		}
		int i = 0;
		for (String newsTypeId : newsTypeIds) {
			i++;
			NewsType type = this.getById(Long.valueOf(newsTypeId));
			type.setSortNum(i);
			this.initPartEdit(type);
			newsTypeDao.update(type);
		}
	}
	
	/**
	 * 取得某个用户有新建权限的所有版块
	 */
	public List<NewsType> getTypesCanNew(Long memberId, Constants.NewsTypeSpaceType spaceType, Long accountId){
		List<NewsType> list2= new ArrayList<NewsType>();
		if(memberId == null)
			return list2;
		Set<NewsType> list1 = new HashSet<NewsType>();
		list1.addAll(this.getManagerTypeByMember(memberId, spaceType, accountId));
		list1.addAll(this.getWriterTypeByMember(memberId, spaceType, accountId));
		list1.addAll(this.getAuditTypeByMember(memberId, spaceType, accountId));
		list2.addAll(list1);
		return list2;
	}
	
	/**
	 * 判断用户是否有新建权限
	 */
	public boolean hasAuth(Long memberId, Long accountId){
		return CollectionUtils.isNotEmpty(getTypesCanNew(memberId, null, accountId));
	}
	
	public List<NewsType> getTypesCanNewByMember(Long memberId, Constants.NewsTypeSpaceType spaceType, Long accountId){
		List<NewsType> list2= new ArrayList<NewsType>();
		if(memberId == null)
			return list2;
		Set<NewsType> list1 = new HashSet<NewsType>();
		list1.addAll(this.getManagerTypeByMember(memberId, spaceType, accountId));
		list1.addAll(this.getWriterTypeByMember(memberId, spaceType, accountId));
		list2.addAll(list1);
		return list2;
	}
	
	/**
	 * 按模块名称查询某用户有管理权限的模块
	 */
	public List<NewsType> getNewsTypeList(Long memberId ,String typename , boolean isIgnoreUsed ,int spaceType) {	
		 List<NewsType> newsTypeList = new ArrayList<NewsType>() ;
		 
		 try{
			 if(typename != null && typename.equals("")){
				 if(spaceType == Constants.NewsTypeSpaceType.group.ordinal()){
					 newsTypeList = this.getManagerTypeByMember(memberId ,Constants.NewsTypeSpaceType.group, CurrentUser.get().getLoginAccount()) ;
				 }else if(spaceType == Constants.NewsTypeSpaceType.corporation.ordinal()){
					 newsTypeList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount()) ;
				 }else{
					 newsTypeList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.department, CurrentUser.get().getLoginAccount()) ;
				 }
			 }else{
				 List<NewsType> tmpList = this.newsTypeDao.getAllNewsType(memberId, typename) ;
				 Set<Long> newsTypeIds = new HashSet<Long>() ;
				 for(NewsType newstype : tmpList) {
					 if(newstype.isUsedFlag() && newstype.getSpaceType().intValue() == spaceType){
						 if(this.isManagerOfType(newstype.getId(), memberId)){
							 newsTypeIds.add(newstype.getId()) ;
						 }
					 }
				 }
				 newsTypeList.addAll(this.getTypesByIds(newsTypeIds)) ;
			 }
		 }catch(Exception e){
			 log.error("", e) ;
		 }
		 this.initList(newsTypeList) ;
		 this.setTotalItemsOfType(newsTypeList) ;
		 return newsTypeList ;
	}
   /**
    * 按总数的查询
    */
	public List<NewsType> getNewsTypeList(Long memberId ,String num , String match, boolean isIgnoreUsed ,int spaceType) {
		
		List<NewsType> list = new ArrayList<NewsType>() ;
		try{
			List<NewsType> allList = new ArrayList<NewsType>() ;	
			//得到所有的列表
			 if(spaceType == Constants.NewsTypeSpaceType.group.ordinal()){
				 allList = this.getManagerTypeByMember(memberId ,Constants.NewsTypeSpaceType.group, CurrentUser.get().getLoginAccount()) ;
			 }else if(spaceType == Constants.NewsTypeSpaceType.corporation.ordinal()){
				 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount()) ;
			 }else{
				 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.department, CurrentUser.get().getLoginAccount()) ;
			 }
			 //数字为空
			 if(num !=null && num.equals("")){
				 list.addAll(allList) ;
			 }else{
				 if(match != null && match.equals("equal")){  //等于
					 for(NewsType newstype : allList){
						 if(newstype.getTotalItems() == Integer.parseInt(num))
							 list.add(newstype) ;
					 }
				 }else if(match != null && match.equals("more")){  //大于
					 for(NewsType newstype : allList){
						 if(newstype.getTotalItems() > Integer.parseInt(num))
							 list.add(newstype) ;
					 }
				 }else if(match != null && match.equals("less")){//小于
					 for(NewsType newstype : allList){
						 if(newstype.getTotalItems() < Integer.parseInt(num))
							 list.add(newstype) ;
					 }	 
				 }else{
					 list.addAll(allList) ;
				 }					 
			 }
	
		}catch(Exception e){
			log.error("", e) ;
		}	
		return list ;		
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManager#getNewsTypeList(java.lang.Long, boolean, boolean, int)
	 * 按是否审核进行查询
	 */
	public List<NewsType> getNewsTypeListByAuditFlag(Long memberId ,String flag , boolean isIgnoreUsed ,int spaceType) {	
		List<NewsType> list = new ArrayList<NewsType>() ;
		try{
			List<NewsType> allList = new ArrayList<NewsType>() ;	
			//得到所有的列表
			 if(spaceType == Constants.NewsTypeSpaceType.group.ordinal()){
				 allList = this.getManagerTypeByMember(memberId ,Constants.NewsTypeSpaceType.group, CurrentUser.get().getLoginAccount()) ;
			 }else if(spaceType == Constants.NewsTypeSpaceType.corporation.ordinal()){
				 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount()) ;
			 }else{
				 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.department, CurrentUser.get().getLoginAccount()) ;
			 }
			 
			 //判断传入的值的内容，得到符合条件的newstype
			 if(flag != null && flag.equals("")){
				 list.addAll(allList) ;
			 }else {
				 if(flag != null && flag.equals("true")){   //需要审核
					 for( NewsType newstype: allList){
						 if(newstype.isAuditFlag()&&newstype.isUsedFlag())
							 list.add(newstype) ;
					 }
				 }else if(flag != null && flag.equals("false")){   //不需要审核
					 for( NewsType newstype: allList){
						 if(!newstype.isAuditFlag()&&newstype.isUsedFlag())
							 list.add(newstype) ;
					 }
				 }else{
					 list.addAll(allList) ;
				 }
			 }			 
		}catch(Exception e){
			log.error("", e) ;
		}
		return list ;
	}
	
	/**
	 * 按审核员名字进行查询
	 */
	public List<NewsType> getNewsTypeListByAuditUsername(Long memberId ,String username , boolean isIgnoreUsed ,int spaceType) {
		List<NewsType> list = new ArrayList<NewsType>() ;
		
		try{
			 List<NewsType> allList = new ArrayList<NewsType>() ; 
			 //用户名为空
			 if(username == null || username.equals("")){
				 if(spaceType == Constants.NewsTypeSpaceType.group.ordinal()){
					 allList = this.getManagerTypeByMember(memberId ,Constants.NewsTypeSpaceType.group, CurrentUser.get().getLoginAccount()) ;
				 }else if(spaceType == Constants.NewsTypeSpaceType.corporation.ordinal()){
					 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.corporation, CurrentUser.get().getLoginAccount()) ;
				 }else{
					 allList = this.getManagerTypeByMember(memberId, Constants.NewsTypeSpaceType.department, CurrentUser.get().getLoginAccount()) ;
				 }
				 list.addAll(allList) ;
			 }else{
				 allList = this.newsTypeDao.getAllNewsType(username) ;
				 Set<Long> newsTypeIds = new HashSet<Long>() ;
				 for(NewsType newstype : allList) {
					 if(newstype.isUsedFlag() && newstype.getSpaceType().intValue() == spaceType){
						 if(this.isManagerOfType(newstype.getId(), memberId)){
							 newsTypeIds.add(newstype.getId()) ;
						 }
					 }
				 }
				 list.addAll(this.getTypesByIds(newsTypeIds)) ;
			 }		
		}catch(Exception e){
			log.error("" ,e) ;
		}
		
		return list ;
	}
	
	public boolean isAuditorOfNews(Long memberId) {
		List<NewsType> auditTypes = this.getAclTypeByMember(memberId, null, null, Constants.NewsTypeAclType.audit);
		if (auditTypes != null && auditTypes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void delMember(Long id) throws BusinessException {
		List<NewsType> managerTypes = this.getAclTypeByMember(id, null, null, Constants.NewsTypeAclType.manager);
		List<NewsType> auditTypes = this.getAclTypeByMember(id, null, null, Constants.NewsTypeAclType.audit);
		List<NewsType> writeTypes = this.getAclTypeByMember(id, null, null, Constants.NewsTypeAclType.writer);
		List<NewsType> types = CommonTools.getSumCollection(managerTypes, CommonTools.getSumCollection(auditTypes, writeTypes));
		if(types != null){
			for(NewsType bean : types){
				//重新设置管理员、审核员
				String oldIds=bean.getManagerUserIds();
				String oldNames=bean.getManagerUserNames();
				String newIds="";
				String newNames="";
				if(StringUtils.isNotBlank(oldIds)){
					String[] oldId = oldIds.split(",");
					for (int i = 0; i < oldId.length; i ++) {
						if(!oldId[i].equals(String.valueOf(id))){
							newIds += oldId[i] + ",";
						}
					}
					newIds=StringUtils.removeEnd(newIds, ",");
				}
				if(StringUtils.isNotBlank(oldNames)){
					String[] oldName = oldNames.split(",");
					for (int i = 0; i < oldName.length; i ++) {
						if(!oldName[i].equals(orgManager.getMemberById(id).getName())){
							newNames += oldName[i] + ",";
						}
					}
					newNames=StringUtils.removeEnd(newNames, ",");
				}
				boolean flag=false;
				if(!oldIds.equals(newIds)){
					bean.setManagerUserIds(newIds);
					bean.setManagerUserNames(newNames);
					flag=true;
				}
				//人员离职不删除审核员记录
				/*if(id.equals(bean.getAuditUser())){
					bean.setAuditUser(-1L);
					bean.setAuditUserName("");
					flag=true;
				}*/
				if(flag){
					this.save(bean);
				}
			
				//重新设置授权人员
				Set<NewsTypeManagers> oldSet = bean.getNewsTypeManagers();
				if(oldSet != null && oldSet.size() > 0){
					Set<NewsTypeManagers> newSet=new HashSet<NewsTypeManagers>();
					for(NewsTypeManagers btm : oldSet){
						if(Constants.WRITE_FALG.equals(btm.getExt1()) && !id.equals(btm.getManagerId())){
							newSet.add(btm);
						}
					}
					String[][] writeIds=new String[newSet.size()][2];
					int j = 0;
					for(NewsTypeManagers btm : newSet){
						writeIds[j][0] = btm.getExt2();
						writeIds[j][1] = String.valueOf(btm.getManagerId());
						j ++;
					}
					this.saveWriteByType(bean.getId(), writeIds);
				}
			}
		}
	}
	
	public NewsType saveCustomNewsType(Long spaceId, Long entityId, String spaceName) {
		NewsType type = new NewsType();
		type.setTypeName(spaceName);
		type.setAccountId(spaceId);
		type.setSpaceType(4);
		type.setTopCount((byte) 3);
		type.setCreateDate(new Date());
		type.setCreateUser(entityId);
		type.setUpdateDate(new Date());
		type.setUpdateUser(entityId);
		type.setSortNum(0);
		type.setAuditFlag(false);
		type.setOutterPermit(false);
		type.setAuditUser(0L);
		try {
			type = this.save(type);
		} catch (Exception e) {
			log.error("", e);
		}
		return type;
	}

}