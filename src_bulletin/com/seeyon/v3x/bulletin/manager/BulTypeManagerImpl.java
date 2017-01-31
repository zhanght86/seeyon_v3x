package com.seeyon.v3x.bulletin.manager;

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

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.dao.BulDataDao;
import com.seeyon.v3x.bulletin.dao.BulTypeDao;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.domain.BulTypeManagers;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.util.EntityKeeper;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 公告类型的Manager接口实现类
 * @author wolf
 *
 */
public class BulTypeManagerImpl extends BaseBulletinManager implements BulTypeManager {
	private static final Log log = LogFactory.getLog(BulTypeManagerImpl.class);
	// 2008.04.07 lihf 版块信息的内存加载
	// Map<bulTypeId, BulType>
	private static Map<Long, BulType> typesMap = null;
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
	private SpaceManager spaceManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	@SuppressWarnings("unused")
	private EntityKeeper entityKeeper;
	
	public synchronized void init(){
		if(initialized)
			return;
		typesMap = new HashMap<Long, BulType>();
		writerMap = new HashMap<Long, Set<Long>>();
		managerMap = new HashMap<Long, Set<Long>>();
		auditMap = new HashMap<Long, Set<Long>>();
		List<BulType> list = this.bulTypeDao.getAll();
		if(list != null && list.size() > 0) {
			for(BulType bt : list) {
				typesMap.put(bt.getId(), bt);
				this.addAclOfType(bt);
			}
		}
		this.initStaticNoObj();
		
		initialized = true;
		log.info("公告版块加载完成。");
	}
	
	private void addName(BulType type){
		if(type == null)
			return;
		if(type.isUsedFlag())
			if(type.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal()){
				this.putStringMap(nameMap, type.getAccountId(), type.getTypeName());
			}else if(type.getSpaceType().intValue() == Constants.BulTypeSpaceType.group.ordinal()){
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
	
	private void addAclOfType(BulType bt){
		if(bt == null)
			return;
		
		long bulTypeId = bt.getId();
		if(bt.getBulTypeManagers() == null)
			bt.setBulTypeManagers(new HashSet<BulTypeManagers>());
		for(BulTypeManagers btm : bt.getBulTypeManagers()){
			if(Constants.MANAGER_FALG.equals(btm.getExt1())){
				this.putMap(managerMap, btm.getManagerId(), bulTypeId);
			}else if(Constants.WRITE_FALG.equals(btm.getExt1())){
				this.putMap(writerMap, btm.getManagerId(), bulTypeId);
			}
		}
		if(bt.isAuditFlag()){
			this.putMap(auditMap, bt.getAuditUser(), bulTypeId);
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
	
	/**
	 * 获取全部板块信息
	 * @return
	 */
	public Collection<BulType> getAllBulletinTypes() {
		return typesMap.values();
	}
	
	/**
	 * 为了支持集群时双机同步数据，将此方法设为public以便监听程序调用
	 * @param bulType
	 */
	public synchronized void initPartAdd(BulType bulType){
		if(bulType == null) {
			return;
		}
		entityKeeper = new EntityKeeper(orgManager);
		initialized = false;
		typesMap.put(bulType.getId(), bulType);
		this.addAclOfType(bulType);
		this.addName(bulType);
		this.initStaticNoObj();
		initialized = true;
		
		// 发送通知
		NotificationManager.getInstance().send(NotificationType.BulletinAddType, bulType);
	}
	
	private void deleteAclOfType(Long typeId){
		if(typeId == null) {
			return;
		}
		this.deleteFromMap(writerMap, typeId);
		this.deleteFromMap(managerMap, typeId);
		this.deleteFromMap(auditMap, typeId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void deleteFromMap(Map<Long, Set<Long>> map, Long typeId){
		if(map == null) {
			map = new HashMap<Long, Set<Long>>();
		}
		Set<Map.Entry<Long, Set<Long>>> mapSet = map.entrySet();
		for (Iterator iter = mapSet.iterator(); iter.hasNext();) {
			Map.Entry<Long, Set<Long>> element = (Map.Entry<Long, Set<Long>>) iter.next();
			if(element.getValue()!=null) {
				element.getValue().remove(typeId);
			}
		}
	}
	
	/**
	 * 为了支持集群时双机同步数据，将此方法设为public以便监听程序调用
	 * @param bulType
	 */
	public synchronized void initPartEdit(BulType bulType){
		if(bulType == null) {
			return;
		}
		initialized = false;
		entityKeeper = new EntityKeeper(orgManager);
		typesMap.put(bulType.getId(), bulType);
		this.deleteAclOfType(bulType.getId());
		this.addAclOfType(bulType);
		this.initStaticNoObj();
		initialized = true;
		// 发送通知
		NotificationManager.getInstance().send(NotificationType.BulletinUpdateType, bulType);
	}
	
	/**
	 * 公告板块删除后来改为逻辑删除，此方法实际已不再使用
	 * @deprecated
	 * @param bulType
	 */
	@SuppressWarnings("unused")
	private synchronized void initPartDelete(BulType bulType){
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
		Collection<BulType> values = typesMap.values();
		for(BulType bt : values){
			this.addName(bt);
			this.setStaticOthers(bt);
		}
	}
	
	private void setStaticOthers(BulType type){
		type.setAuditUserName("");
		type.setManagerUserIds("");
		//审核员
		if(type.isAuditFlag()){
			type.setAuditUserName(this.getBulletinUtils().getMemberNameByUserId(type.getAuditUser()));
		}
		
		List<BulTypeManagers> sortedList = new ArrayList<BulTypeManagers>();
		for(BulTypeManagers tm:type.getBulTypeManagers()){
			if(!Constants.MANAGER_FALG.equals(tm.getExt1())) 
				continue;
			sortedList.add(tm);
		}
		Collections.sort(sortedList);
		
		//管理员
		String ids = "";
		for(BulTypeManagers tm : sortedList) {
			ids += "," + tm.getManagerId();
		}
		type.setManagerUserIds(ids.length()>0 ? ids.substring(1) : "");
	}
	
	private BulTypeDao bulTypeDao;
	private BulDataDao bulDataDao;

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#getBulTypeDao()
	 */
	public BulTypeDao getBulTypeDao() {
		return bulTypeDao;
	}
	
	public void setBulTypeDao(BulTypeDao bulTypeDao) {
		this.bulTypeDao = bulTypeDao;
	}

	BulTemplateManager bulTemplateManager;

	public BulTemplateManager getBulTemplateManager() {
		return bulTemplateManager;
	}
	
	public void setBulTemplateManager(BulTemplateManager bulTemplateManager) {
		this.bulTemplateManager = bulTemplateManager;
	}
	
	BulTypeManagersManager bulTypeManagersManager;

	public BulTypeManagersManager getBulTypeManagersManager() {
		return bulTypeManagersManager;
	}
	
	public void setBulTypeManagersManager(BulTypeManagersManager bulTypeManagersManager) {
		this.bulTypeManagersManager = bulTypeManagersManager;
	}

	/**
	 * 删除部门公告
	 */
	public void delDept(Long id) throws BusinessException {
	}	
	/**
	 * 逻辑删除版块
	 */
	public void setTypeDeleted(List<Long> ids){
		if(ids == null || ids.size() == 0)
			return;
		
		for(Long id : ids){
			BulType bt = this.getById(id);
			bt.setUsedFlag(false);
			this.bulTypeDao.update(bt);
			
			//
			this.initPartEdit(bt);
		}
	}
	
	/**
	 * 初始化公告类型列表
	 * @param list
	 */
	public void initList(List<BulType> list){
		for(BulType type:list){
			initType(type);			
		}
	}

	/**
	 * 初始化公告类型
	 * 1、初始化创建者姓名
	 * 2、初始化审核员姓名
	 * 3、初始化该公告类型的管理员姓名列表
	 * @param type
	 */
	private void initType(BulType type) {
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#findAll()
	 */
	public List<BulType> findAll() {		
		List<BulType> list = new ArrayList<BulType>();
		this.checkTypesMap();
		for(BulType bt : typesMap.values()){
			if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal())
				list.add(bt);
		}
		
		return list;
	}
	/* 后台单位板块列表查询。
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#findAll()
	 */
	public List<BulType> boardFindAll() {
		List<BulType> list = new ArrayList<BulType>();//bulTypeDao.find(hql);
		this.checkTypesMap();
		for(BulType  bt : typesMap.values()){
			if(bt.getAccountId().longValue() == CurrentUser.get().getLoginAccount()
					&& bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		initList(list);
		return list;
	}
	
	/* 后台单位板块列表查询。
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#findAll()
	 */
	public List<BulType> boardFindAllByAccountId(Long accountId) {
		List<BulType> list = new ArrayList<BulType>();
		this.checkTypesMap();
		for(BulType  bt : typesMap.values()){
			if(bt.getAccountId().longValue() == accountId.longValue()
					&& bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		initList(list);
		return list;
	}
	
	/* 
	 * 后台自定义单位或集团板块列表查询。
	 */
	public List<BulType> customAccBoardAllBySpaceId(Long spaceId, int spaceType) {
		List<BulType> list = new ArrayList<BulType>();
		this.checkTypesMap();
		for(BulType  bt : typesMap.values()){
			if(bt.getAccountId().longValue() == spaceId.longValue()
					&& bt.getSpaceType().intValue() == spaceType
					&& bt.isUsedFlag())
				list.add(bt);
		}
		initList(list);
		return list;
	}
	
	
	private void checkTypesMap(){
		if(!initialized)
			this.init();
	}
	
	/**
	 * 查询所有自定义单位或集团公告板块类型-- 支持 分页
	 */
	public List<BulType> customAccBoardFindAllByPage(long spaceId, int spaceType, boolean isPage){
		List<BulType> all = this.customAccBoardAllBySpaceId(spaceId, spaceType);
		this.sortList(all);
		//先排序之后再分页
		if (isPage) {
			List<BulType> list = CommonTools.pagenate(all);		
			return list;
		}
		return all;
	}
	
	/**
	 * 查询所有的单位公告板块--分页
	 */
	public List<BulType> boardFindAllByPage(){
		List<BulType> all = this.boardFindAll();
		this.sortList(all);
		//先排序之后再分页
		List<BulType> list = CommonTools.pagenate(all);		
		return list;
	}
	
	/**
	 * 
	 * 查询所有的单位公告板块--不分页
	 * 
	 */
	
	public List<BulType> boardFindAllByNoPage(){
		List<BulType> list = this.boardFindAll();
		this.sortList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#findAll()
	 */
//	@SuppressWarnings("unchecked")拿到所有集团公告的列表
	public List<BulType> groupFindAll() {		
		List<BulType> list = new ArrayList<BulType>();
		this.checkTypesMap();
		for(BulType  bt : typesMap.values()){
			if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.group.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	
//	@SuppressWarnings("unchecked")拿到所有部门公告的列表
	public List<BulType> departmentFindAll() {		
		List<BulType> list = new ArrayList<BulType>();
		this.checkTypesMap();
		for(BulType  bt : typesMap.values()){
			if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.department.ordinal()
					&& bt.isUsedFlag())
				list.add(bt);
		}
		
		initList(list);
		return list;
	}
	
	/**
	 * 查询所有的集团公告板块--分页
	 */	
	public List<BulType> groupFindAllByPage() {
		List<BulType> all = this.groupFindAll();
		this.sortList(all);
		//先排序之后再分页
		List<BulType> list = CommonTools.pagenate(all);		
		return list;
	}
	
	/**
	 * 查询所有的集团公告板块--不分页
	 */	
	public List<BulType> groupFindAllByNoPage() {
		List<BulType> list =this.groupFindAll();
		this.sortList(list);
		return list;
	}
	
	
	
	/**
	 * 查询所有外单位公告条件
	 */
	public List<BulType> otherFindAll() {
		List<BulType> ret = new ArrayList<BulType>();
		List<BulType> src = this.findAll();
		if(src == null)
			return ret;		
		for(BulType t : src){
			if(t.getAccountId().longValue() != CurrentUser.get().getLoginAccount())
				ret.add(t);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<BulType> findByProperty(String property, Object value) {
		List<BulType> src = this.findByPropertyNoPaging(property, value);
		List<BulType> list = CommonTools.pagenate(src);
		return list;
	}
	
	// 条件过滤
	private List<BulType> filterByProperty(List<BulType> list, String attribute, Object value){
		if(list == null || attribute == null || value == null)
			return list;
		
		List<BulType> ret = new ArrayList<BulType>();
		for(BulType bt : list){
			Object trueVal = this.getBulletinUtils().getAttributeValue(bt, attribute);
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
	private void sortList(List<BulType> list){
		if(list == null)
			return;
		Collections.sort(list);
	}
	
	public List<BulType> findByPropertyNoPaging(String property, Object value) {
		List<BulType> list = this.boardFindAll();

		list = this.filterByProperty(list, property, value);
		this.sortList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#groupFindByProperty(java.lang.String, java.lang.Object)
	 */
	public List<BulType> groupFindByProperty(String property, Object value) {		
		List<BulType> src = this.groupFindAll();
		src = this.filterByProperty(src, property, value);
		this.sortList(src);
		List<BulType> list = CommonTools.pagenate(src);
		
		return list;
	}
	
	public boolean isGroupBulTypeManager(long memberId){				
		this.checkTypesMap();
		Set<Long> set = managerMap.get(memberId);
		boolean ret = this.spaceTypeChecked(set, Constants.BulTypeSpaceType.group);
		if(ret)
			return true;
		set = auditMap.get(memberId);
		return this.spaceTypeChecked(set, Constants.BulTypeSpaceType.group);
	}
	
	/**
	 * 注意！
	 * 此方法不能准确判断用户是否具有某个单位的部门公告管理权限
	 * 其业务逻辑是获取用户所能管理的所有公告版块，如发现其中有部门公告版块类型，即判定其具有部门公告管理权限
	 * commented by Meng Yang 2009-06-23
	 * @deprecated
	 */
	public boolean isDepartmentBulTypeManager(long memberId){
		this.checkTypesMap();
		Set<Long> set = managerMap.get(memberId);
		boolean ret = this.spaceTypeChecked(set, Constants.BulTypeSpaceType.department);
		if(ret)
			return true;
		set = auditMap.get(memberId);
		return this.spaceTypeChecked(set, Constants.BulTypeSpaceType.department);
	}
	
	/**
	 * 判断用户是否当前登陆单位的部门公告管理员,辅助部门公告管理菜单是否出现的权限判断
	 * @param memberId  当前用户ID
	 * @param loginAccountId   登陆单位ID(用户可跨单位办公)
	 * @return 用户是否当前登陆单位的某个部门公告管理员
	 * added by Meng Yang 2009-06-23
	 */
	public boolean isDepartmentBulTypeManager(long memberId, long loginAccountId) {
		boolean isDepartmentBulTypeManager = false;
		// 获取当前用户所能管理的部门公告版块类型
		List<BulType> types = this.getManagerTypeByMember(memberId, BulTypeSpaceType.department, null);
		// 遍历判断其中的部门公告版块对应部门是否属于当前登陆单位
		try {
			for(BulType type : types) {
				// 如果部门公告对应部门属于当前单位,即表明当前用户具有当前登陆单位的部门公告管理权			
				if(orgManager.getDepartmentById(type.getId()).getOrgAccountId().longValue()==loginAccountId) {
					isDepartmentBulTypeManager = true;
					break;
				}				
			} 
		}catch (BusinessException e) {
			log.error("判断用户是否当前单位部门公告管理员出现异常", e);
		}
		return isDepartmentBulTypeManager;
	}
	
	/**
	 * 判断用户是否某一特定部门的部门公告管理员，用于跨单位兼职时的权限判断
	 * @param memberId 当前用户ID
	 * @param deptId   当前部门ID
	 * @return 是否为当前部门公告管理员
	 * @throws BusinessException 
	 */
	public boolean isManagerOfThisDept(long memberId, Long deptId) throws BusinessException {
		List<Long> managerDepartments = spaceManager.getManagerDepartments(memberId, CurrentUser.get().getLoginAccount());
		return CollectionUtils.isNotEmpty(managerDepartments) && managerDepartments.contains(deptId);
	}
	
	private boolean spaceTypeChecked(Collection<Long> typeIds, Constants.BulTypeSpaceType spaceType){
		if(typeIds == null || spaceType == null)
			return false;		
		
		for(Long id : typeIds){
			BulType bt = typesMap.get(id);
			if(bt == null)
				continue;
			if(bt.getSpaceType().intValue() == spaceType.ordinal())
				return true;
		}
		
		return false;
	}
	
	public boolean isGroupBulTypeAuth(long memberId){
		this.checkTypesMap();
		Set<Long> set = auditMap.get(memberId);
		return this.spaceTypeChecked(set, Constants.BulTypeSpaceType.group);
	}
	
	//取的有我来审核集团公告板块的列表
	public List<BulType> getAuditGroupBulType(long memberId){
		return this.getAuditTypeByMember(memberId, Constants.BulTypeSpaceType.group, null);
		
	}
	
	//取的有我来审核单位公告板块的列表
	public List<BulType> getAuditUnitBulType(long memberId){
		return this.getAuditTypeByMember(memberId, Constants.BulTypeSpaceType.corporation, CurrentUser.get().getLoginAccount());
		
	}
	
	public List<BulType> getAuditUnitBulType(long memberId, long spaceId, int spaceType){
		return this.getAuditTypeByMember(memberId, spaceType == 5 ? Constants.BulTypeSpaceType.public_custom : Constants.BulTypeSpaceType.public_custom_group, spaceId);
	}
	/**
	 * 只根据用户，不根据当前单位
	 */
	public List<BulType> getAuditUnitBulTypeOnlyByMember(long memberId){
		return this.getAuditTypeByMember(memberId, Constants.BulTypeSpaceType.corporation, null);
	}
	
	/**
	 * 得到可以审核的版块列表
	 * 单位类型时，accountId 为 null 说明不验证单位
	 *
	 */
	public List<BulType> getAuditTypeByMember(Long memberId, Constants.BulTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.BulTypeAclType.audit);
	}
	/**
	 * 得到可以管理的版块列表
	 * 单位类型时，accountId 为 null 说明不验证单位
	 *
	 */
	public List<BulType> getManagerTypeByMember(Long memberId, Constants.BulTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.BulTypeAclType.manager);
	}
	
	public List<BulType> getWriterTypeByMember(Long memberId, Constants.BulTypeSpaceType spaceType, Long accountId){
		return this.getAclTypeByMember(memberId, spaceType, accountId, Constants.BulTypeAclType.writer);
	}
	
	private Map<Long, Set<Long>>  getAclMap(Constants.BulTypeAclType aclType){
		this.checkTypesMap();
		int param = aclType.ordinal();
		if(param == Constants.BulTypeAclType.manager.ordinal())
			return managerMap;
		else if(param == Constants.BulTypeAclType.audit.ordinal())
			return auditMap;
		else if(param == Constants.BulTypeAclType.writer.ordinal())
			return writerMap;
		
		return new HashMap<Long, Set<Long>>();
	}

	private List<BulType> getAclTypeByMember(Long memberId, Constants.BulTypeSpaceType spaceType, 
			Long accountId, Constants.BulTypeAclType aclType){
		List<BulType> ret = new ArrayList<BulType>();
		if(memberId == null || aclType == null)
			return ret;
		Map<Long, Set<Long>> map = this.getAclMap(aclType);		
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
		Set<BulType> src = this.getTypesByIds(set);
		if(spaceType == null){
			for(BulType bt : src){
				if(bt.isUsedFlag()){
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.BulTypeSpaceType.corporation.ordinal()){
			for(BulType bt : src){
				if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.corporation.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.BulTypeSpaceType.public_custom.ordinal()){
			for(BulType bt : src){
				if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.public_custom.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.BulTypeSpaceType.public_custom_group.ordinal()){
			for(BulType bt : src){
				if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.public_custom_group.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.BulTypeSpaceType.group.ordinal()){
			for(BulType bt : src){
				if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.group.ordinal() && bt.isUsedFlag()){
					ret.add(bt);
				}
			}
		}else if(spaceType.ordinal() == Constants.BulTypeSpaceType.department.ordinal()){
			for(BulType bt : src){
				if(bt.getSpaceType().intValue() == Constants.BulTypeSpaceType.department.ordinal() && bt.isUsedFlag()){
					if(accountId != null && bt.getAccountId().longValue() != accountId.longValue())
						continue;
					ret.add(bt);
				}
			}
		}
		
		this.initList(ret);
		
		return ret;
	}
	// 
	private Set<BulType> getTypesByIds(Collection<Long> ids){
		Set<BulType> set = new HashSet<BulType>();
		if(ids == null)
			return set;
		this.checkTypesMap();
		for(Long id : ids){
			BulType bt = typesMap.get(id);
			if(bt != null)
				set.add(bt);
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#getById(java.lang.Long)
	 */
	public BulType getById(Long id) {
		if(id == null)
			return null;
		this.checkTypesMap();
		BulType type = typesMap.get(id);//bulTypeDao.get(id);
		
		if(type == null){			
			try {
				V3xOrgDepartment dept = orgManager.getDepartmentById(id);
				if(dept == null)
					return null;
				else{
					type = this.getBulletinUtils().createBulTypeByDept(dept.getName(), dept.getId(), dept.getOrgAccountId());
				}
			} catch (BusinessException e) {
			}

		}
		
		if(type != null)
		{
			initType(type);
		}
			
		return type;
		
	}
	
	public BulType getByDeptId(Long id) {
		if(id == null)
			return null;
		this.checkTypesMap();
		return typesMap.get(id);
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#save(com.seeyon.v3x.bulletin.domain.BulType)
	 */
	public BulType save(BulType type) throws BulletinException {
		if(type == null)
			return null;
		return this.saveBulType(type, type.isNew());
	}
	
	public BulType saveBulType(BulType type, boolean isNew){
		if(isNew){
			type.setIdIfNew();
			if(type.getAccountId()==null)
				type.setAccountId(CurrentUser.get().getLoginAccount());
			type.setUsedFlag(true);
			bulTypeDao.save(type);
		}else{ 
			bulTypeDao.update(type);
		}
		
		//保存公告管理员
		String[] manages = new String[0];
		if(Strings.isNotBlank(type.getManagerUserIds())){
			manages=type.getManagerUserIds().split(",");
		}	
		bulTypeManagersManager.saveAclByTypeManager(type, manages, Constants.MANAGER_FALG);
		
		// 内存加载
		if(isNew)
			this.initPartAdd(type);
		else
			this.initPartEdit(type);
		
		return type;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTypeManager#saveWriteByType(java.lang.Long, java.lang.Long[])
	 */
	public void saveWriteByType(Long typeId,String[][] writeIds){
		BulType type=this.getById(typeId);
		this.bulTypeManagersManager.saveAclByType(type, writeIds, Constants.WRITE_FALG);//.saveWriteByType(type, writeIds);
		// 内存同步
		this.initPartEdit(type);
	}

	/**
	 * 得到 writeFlag 的BulTypeManagers
	 * 
	 */
	public List<BulTypeManagers> findTypeWriters(BulType bt) {
		List<BulTypeManagers> ret = new ArrayList<BulTypeManagers>();
		if(bt == null)
			return ret;
		Set<BulTypeManagers> set = bt.getBulTypeManagers();//this.getBulTypeManagersManager().findTypeManagerId(typeId);
		
		if(set != null)
			for(BulTypeManagers btm : set){
				if(Constants.WRITE_FALG.equals(btm.getExt1()))
						ret.add(btm);
			}
		
		return ret;
	}

	public BulDataDao getBulDataDao() {
		return bulDataDao;
	}

	public void setBulDataDao(BulDataDao bulDataDao) {
		this.bulDataDao = bulDataDao;
	}
	
	/**
	 * 初始化类型下总数
	 */
	@SuppressWarnings("unchecked")
	public void setTotalItemsOfType(List<BulType> types){
		if(types == null || types.size() == 0)
			return;
		Map<Long, Integer> countMap = new HashMap<Long, Integer>();
		List<Long> typeIds = new ArrayList<Long>();
		for(BulType t : types){
			countMap.put(t.getId(), 0);
			typeIds.add(t.getId());
		}
		
		try{
			final String hqlf = "select data.typeId from BulData as data where data.typeId in (:typeIds) "
								+ " and data.state=:state and data.deletedFlag=false ";
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("typeIds", typeIds);
			parameterMap.put("state", Constants.DATA_STATE_ALREADY_PUBLISH);
			List<Object> list = bulTypeDao.find(hqlf, -1, -1, parameterMap);
			
			for(Object o : list){
				Long typeId = (Long)o;
				countMap.put(typeId, countMap.get(typeId) + 1);
			}
	
			for(BulType t : types){
				Integer total = countMap.get(t.getId());
				if(total == null)
					continue;
				t.setTotalItems(total);
			}				
		}catch(Exception e){
			log.error("", e);
		}
	}
	
	/**
	 * 用于新建单位时初始化新闻板块
	 * @param accountId  单位id
	 */
	public void initBulType(long accountId) {
		List<BulType> types = this.bulTypeDao.getByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if(types != null) {
			Date today = new Date();
			for(BulType type:types) {
				BulType newType = new BulType();
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
				newType.setSortNum(type.getSortNum());
				newType.setExt1("0");
				try {
					this.save(newType);
				} catch (BulletinException e) {
				}
			}
		}
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
	 * 用于更新公告板块的排序顺序
	 */
	public void updateBulTypeOrder(String[] bulTypeIds) {
		if (bulTypeIds == null) {
			return;
		}
		int i = 0;
		for (String bulTypeId : bulTypeIds) {
			i++;
			BulType type = this.getById(Long.valueOf(bulTypeId));
			type.setSortNum(i);
			this.initPartEdit(type);
			bulTypeDao.update(type);
		}
	}
	
	/**
	 * 取得某个用户有新建权限的所有版块
	 */
	public List<BulType> getTypesCanNew(Long memberId, Constants.BulTypeSpaceType spaceType, Long accountId){
		List<BulType> list2 = new ArrayList<BulType>();
		if(memberId == null)
			return list2;
		Set<BulType> list1 = new HashSet<BulType>();
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
	
	/**
	 * 取得某个用户有新建权限的所有版块
	 */
	public List<BulType> getTypesCanCreate(Long memberId, Constants.BulTypeSpaceType spaceType, Long accountId){
		List<BulType> list2 = new ArrayList<BulType>();
		if(memberId == null)
			return list2;
		Set<BulType> list1 = new HashSet<BulType>();
		list1.addAll(this.getManagerTypeByMember(memberId, spaceType, accountId));
		list1.addAll(this.getWriterTypeByMember(memberId, spaceType, accountId));
		list2.addAll(list1);
		return list2;
	}
	
	public List<BulType> getBulByTypeName(Long memberID , String bulTypeName ,boolean isIgnoreUsed, int spaceType){
		 
		List<BulType> list = new ArrayList<BulType>() ; 
		try{	
			if(bulTypeName == null || bulTypeName.equals("")){
				if(spaceType == Constants.BulTypeSpaceType.corporation.ordinal()){
					list = this.getManagerTypeByMember(memberID,
							Constants.BulTypeSpaceType.corporation, 
							CurrentUser.get().getLoginAccount());					
				}else if(spaceType == Constants.BulTypeSpaceType.group.ordinal()){
					list = this.getManagerTypeByMember(memberID,
							Constants.BulTypeSpaceType.group, 
							CurrentUser.get().getLoginAccount());					
				}else if(spaceType == Constants.BulTypeSpaceType.department.ordinal()){
					list = this.getManagerTypeByMember(memberID,
							Constants.BulTypeSpaceType.department, 
							CurrentUser.get().getLoginAccount());										
				}

			}
			else { //查询得到所有的满足该名字的公告模块
				List<BulType> typeList = this.bulTypeDao.getAllBulType(memberID, bulTypeName) ;
				Set<Long> set = new HashSet<Long>();
				 for(BulType t : typeList) {
						if(t.getSpaceType().intValue() == spaceType && t.isUsedFlag()){ //判断此模块是不是属于此空间
							 Set<BulTypeManagers> bulTypeManagers = t.getBulTypeManagers() ; //得到该模块的管理员的集合
							 for(BulTypeManagers bt : bulTypeManagers){
								 if(bt.getManagerId().intValue() == memberID.intValue()){ //判断此人是不是该模块的管理员
									 set.add(t.getId()) ;
								 }
							 }
						}
					}
				 Set<BulType> src = this.getTypesByIds(set) ;
				 list.addAll(src) ;
			}
			
 
		}catch(Exception e){
			log.error("", e);
		}	
		this.initList(list) ;
		this.setTotalItemsOfType(list) ;
		return list ;
	}
	

    /**
     * 按公告数量的查询
     * @param 
     */
	public List<BulType> getBulByTol(Long memberID , String totals , String matches, boolean isIgnoreUsed ,int spaceType){
		List<BulType> list = new ArrayList<BulType>() ; 
		
		try{
			//查询得到所有的公告信息列表
			List<BulType> typeList = new ArrayList<BulType>() ;
			if(spaceType == Constants.BulTypeSpaceType.corporation.ordinal()){
				typeList = this.getManagerTypeByMember(memberID,
						Constants.BulTypeSpaceType.corporation, 
						CurrentUser.get().getLoginAccount());					
			}else if(spaceType == Constants.BulTypeSpaceType.group.ordinal()){
				typeList = this.getManagerTypeByMember(memberID,
						Constants.BulTypeSpaceType.group, 
						CurrentUser.get().getLoginAccount());					
			}else if(spaceType == Constants.BulTypeSpaceType.department.ordinal()){
				typeList = this.getManagerTypeByMember(memberID,
						Constants.BulTypeSpaceType.department, 
						CurrentUser.get().getLoginAccount());										
			}
			
			//判断写入的数字是不是空的
			if(!totals.equals("")){
				int total = Integer.parseInt(totals) ;
				if(matches.equals("equal")){ //选择的是等于
					for(BulType t :typeList ) {	
						if(t.getTotalItems() == total&&t.getSpaceType().intValue()==spaceType ){
							list.add(t) ;
						}
					}				
				}else if(matches.equals("more")){ //选择的是大于
					for(BulType t :typeList ) {
						if(t.getTotalItems() > total&&t.getSpaceType().intValue()==spaceType){
							list.add(t) ;
						}
					}				
				}else if(matches.equals("less")){ //选择的是小于
					for(BulType t :typeList ) {
						if(t.getTotalItems() < total && t.getSpaceType().intValue()==spaceType){
							list.add(t) ;
						}
					}					
				}else {
					for(BulType t :typeList){
						list.add(t) ;
					}
				}		
			}else{
                //为空
				for(BulType t :typeList){
					list.add(t) ;
				}
			}
		}catch(Exception e){
			log.error("" ,e) ;
		}
		this.setTotalItemsOfType(list) ;
		return list ;
	}
	
	/**
	 * 按公告是否需要审核查询
	 */
	public List<BulType> findByAuditFlag(Long memberId , String flag , boolean isIgnoreUsed ,int spaceType){
      List<BulType> list = new ArrayList<BulType>() ; 
		
		try{
			//查询得到所有的公告信息列表
			List<BulType> typeList = new ArrayList<BulType>() ;
			if(spaceType == Constants.BulTypeSpaceType.corporation.ordinal()){
				typeList = this.getManagerTypeByMember(memberId,
						Constants.BulTypeSpaceType.corporation, 
						CurrentUser.get().getLoginAccount());					
			}else if(spaceType == Constants.BulTypeSpaceType.group.ordinal()){
				typeList = this.getManagerTypeByMember(memberId,
						Constants.BulTypeSpaceType.group, 
						CurrentUser.get().getLoginAccount());					
			}else if(spaceType == Constants.BulTypeSpaceType.department.ordinal()){
				typeList = this.getManagerTypeByMember(memberId,
						Constants.BulTypeSpaceType.department, 
						CurrentUser.get().getLoginAccount());										
			}
						//判断标记的值	
			if(flag.equals("")){
				list.addAll(typeList) ;
			}else{
				if(flag.equals("false")){  //不需要验证
					for(BulType t : typeList){
						if(!t.isAuditFlag() && t.getSpaceType().intValue()==spaceType){
							list.add(t) ;
						}
					}
				}else if(flag.equals("true")){ //需要验证
					for(BulType t : typeList){
						if(t.isAuditFlag()&&t.getSpaceType().intValue()==spaceType){
							list.add(t) ;
						}
					}					
				}else{
					list.addAll(typeList) ;
				}
			}
			}catch(Exception e){
				log.error("", e) ;
			}
	  this.setTotalItemsOfType(list) ;
	  return list ;
	}
	/**
	 * 按公告审核员的名字查询
	 */
	public List<BulType> findByAuditUserName(Long memberId , String username , boolean isIgnoreUsed ,int spaceType){
		List<BulType> list = new ArrayList<BulType>() ; 
		
		try{
			//用户输入的名字为空
			if(username == null || username.equals("")){
				if(spaceType == Constants.BulTypeSpaceType.corporation.ordinal()){ //得到单位公告
					list = this.getManagerTypeByMember(memberId,
							Constants.BulTypeSpaceType.corporation, 
							CurrentUser.get().getLoginAccount());					
				}else if(spaceType == Constants.BulTypeSpaceType.group.ordinal()){//得到集团公告
					list = this.getManagerTypeByMember(memberId,
							Constants.BulTypeSpaceType.group, 
							CurrentUser.get().getLoginAccount());					
				}else if(spaceType == Constants.BulTypeSpaceType.department.ordinal()){ //得到部门公告
					list = this.getManagerTypeByMember(memberId,
							Constants.BulTypeSpaceType.department, 
							CurrentUser.get().getLoginAccount());										
				}
		}else{
				List<BulType> typeList = this.bulTypeDao.getAllBulTypeByMember(memberId, username) ;
				Set<Long> set = new HashSet<Long>();
				for(BulType bt : typeList){
					if(bt.getSpaceType().intValue() == spaceType && bt.isUsedFlag()){
						 Set<BulTypeManagers> bulTypeManagers = bt.getBulTypeManagers() ; //得到该模块的管理员的集合
						 for(BulTypeManagers btm : bulTypeManagers){
							 if(btm.getManagerId().intValue() == memberId.intValue()){ //判断此人是不是该模块的管理员
								 set.add(bt.getId()) ;
							 }
						 }
					}
				}
			 Set<BulType> src = this.getTypesByIds(set) ;
			 list.addAll(src) ;			
			}
			
		}catch(Exception e){
			log.error("", e) ;
		}
		this.initList(list) ;
		this.setTotalItemsOfType(list) ;
		return list ;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	
	public boolean isAuditorOfBul(Long memberId) {
		List<BulType> auditTypes = this.getAclTypeByMember(memberId, null, null, Constants.BulTypeAclType.audit);
		if (auditTypes != null && auditTypes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void delMember(Long id) throws BusinessException {
		List<BulType> managerTypes = this.getAclTypeByMember(id, null, null, Constants.BulTypeAclType.manager);
		List<BulType> auditTypes = this.getAclTypeByMember(id, null, null, Constants.BulTypeAclType.audit);
		List<BulType> writeTypes = this.getAclTypeByMember(id, null, null, Constants.BulTypeAclType.writer);
		List<BulType> types = CommonTools.getSumCollection(managerTypes, CommonTools.getSumCollection(auditTypes, writeTypes));
		if(types != null){
			for(BulType bean : types){
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
					if (newIds.length() > 0) {
						newIds = newIds.substring(0, newIds.length() - 1);
					}
				}
				if(StringUtils.isNotBlank(oldNames)){
					String[] oldName = oldNames.split("、");
					for (int i = 0; i < oldName.length; i ++) {
						if(!oldName[i].equals(orgManager.getMemberById(id).getName())){
							newNames += oldName[i] + "、";
						}
					}
					if (newNames.length() > 0) {
						newNames = newNames.substring(0, newNames.length() - 1);
					}
				}
				boolean flag=false;
				if(!oldIds.equals(newIds)){
					bean.setManagerUserIds(newIds);
					bean.setManagerUserNames(newNames);
					flag=true;
				}
				//人员离职时不去掉审核员记录
				/*if(id.equals(bean.getAuditUser())){
					bean.setAuditUser(-1L);
					bean.setAuditUserName("");
					flag=true;
				}*/
				if(flag){
					this.save(bean);
				}
			
				//重新设置授权人员
				Set<BulTypeManagers> oldSet = bean.getBulTypeManagers();
				if(oldSet != null && oldSet.size() > 0){
					Set<BulTypeManagers> newSet=new HashSet<BulTypeManagers>();
					for(BulTypeManagers btm : oldSet){
						if(Constants.WRITE_FALG.equals(btm.getExt1()) && !id.equals(btm.getManagerId())){
							newSet.add(btm);
						}
					}
					String[][] writeIds=new String[newSet.size()][2];
					int j = 0;
					for(BulTypeManagers btm : newSet){
						writeIds[j][0] = btm.getExt2();
						writeIds[j][1] = String.valueOf(btm.getManagerId());
						j ++;
					}
					this.saveWriteByType(bean.getId(), writeIds);
				}
			}
		}
	}
	
	public BulType saveCustomBulType(Long spaceId, String spaceName) {
		BulType type = new BulType();
		type.setId(spaceId);
		type.setTypeName(spaceName);
		type.setAccountId(spaceId);
		type.setAuditFlag(false);
		type.setAuditUser(0L);
		type.setCreateDate(new Date());
		type.setCreateUser(CurrentUser.get().getId());
		type.setSpaceType(Constants.BulTypeSpaceType.custom.ordinal());
		type.setTopCount(Constants.BUL_DEPT_DEFAULT_TOP_COUNT);
		type.setSortNum(0);
		type.setExt1("0");
		try {
			type = this.saveBulType(type, true);
		} catch (Exception e) {
			log.error("", e);
		}
		return type;
	}

	@Override
	public List<BulType> boardFindAllCustom() throws BusinessException {
		List<BulType> list = new ArrayList<BulType>();//bulTypeDao.find(hql);
		this.checkTypesMap();
		List<Long> customSpaceIds = spaceManager.getAllCustomSpace();
		for(BulType  bt : typesMap.values()){
			if(customSpaceIds.contains(bt.getAccountId())
					&& bt.isUsedFlag())
				list.add(bt);
		}
		initList(list);
		return list;
	}

}