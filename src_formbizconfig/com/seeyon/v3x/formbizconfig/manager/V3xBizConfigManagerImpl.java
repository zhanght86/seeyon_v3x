package com.seeyon.v3x.formbizconfig.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.dao.V3xBizConfigDao;
import com.seeyon.v3x.formbizconfig.domain.V3xBizAuthority;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfig;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfigItem;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 业务生成器配置
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */

public class V3xBizConfigManagerImpl extends BaseHibernateDao<V3xBizConfig> implements V3xBizConfigManager {
	private static final Log logger = LogFactory.getLog(V3xBizConfigManagerImpl.class);
	private OrgManager orgManager;
	private MenuManager menuManager;
	private V3xBizConfigDao v3xBizConfigDao;
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	
	private CacheAccessable cacheFactory = CacheFactory.getInstance(V3xBizConfigManager.class);
	private CacheMap<Long, V3xBizConfig> cache2Id = null;
	private CacheMap<Long, V3xBizConfig> cache2MenuId = null;
	
	public void setV3xBizConfigDao(V3xBizConfigDao v3xBizConfigDao) {
		this.v3xBizConfigDao = v3xBizConfigDao;
	}
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
	
	public void init(){
		cache2Id = cacheFactory.createLinkedMap("V3xBizConfig_ID");
		cache2MenuId = cacheFactory.createLinkedMap("V3xBizConfig_MenuId");
		
		List<V3xBizConfig> allAllV3xBizConfig = this.v3xBizConfigDao.getAllV3xBizConfig();
		List<V3xBizConfigItem> allV3xBizConfigItem = this.v3xBizConfigDao.getAllV3xBizConfigItem();
		List<V3xBizAuthority> allV3xBizAuthority = this.v3xBizConfigDao.getAllV3xBizAuthority();
		
		for (V3xBizConfig c : allAllV3xBizConfig) {
			updateMemory(c);
		}
			
		for (V3xBizAuthority a : allV3xBizAuthority) {
			V3xBizConfig c = cache2Id.get(a.getBizConfigId());
			if(c != null){
				c.getV3xBizAuthorityList().add(a);
			}
		}
			
		for (V3xBizConfigItem i : allV3xBizConfigItem) {
			V3xBizConfig c = cache2Id.get(i.getBizConfigId());
			if(c != null){
				c.getV3xBizConfigItemList().add(i);
			}
		}
	}
	
	private void updateMemory(V3xBizConfig c){
		cache2Id.put(c.getId(), c);
		cache2MenuId.put(c.getMenuId(), c);
	}
	
	private void removeMemory(V3xBizConfig c){
		if(c != null){
			cache2Id.remove(c.getId());
			cache2MenuId.remove(c.getMenuId());
		}
	}
	
	/**
	 * 查询业务配置记录
	 */
	public List<V3xBizConfig> findAllByCondition(SearchModel searchModel) {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		Set<Long> creatorIdSet = Functions.getAllMembersId(V3xOrgEntity.ORGENT_TYPE_ACCOUNT,accountId);
		List<Long> creatorIdList = new ArrayList<Long>(creatorIdSet);
		List<V3xBizConfig> v3xBizConfigList = null;
		if(creatorIdList.size()<=1000){
			v3xBizConfigList = v3xBizConfigDao.findAllByCondition(searchModel, creatorIdList);
		} else {
			v3xBizConfigList = new ArrayList<V3xBizConfig>();
			List<Long>[] arr = Strings.splitList(creatorIdList, 1000);//解决Sql Server下Prepared or callable statement has more than 2000 parameter markers.;
			for (List<Long> list : arr) {
				List<V3xBizConfig> tmpV3xBizConfigList = v3xBizConfigDao.findAllByCondition(searchModel, list);
				if(tmpV3xBizConfigList != null){
					v3xBizConfigList.addAll(tmpV3xBizConfigList);
				}
			}
		}
		if(v3xBizConfigList!=null){
			for (V3xBizConfig bizConfig : v3xBizConfigList) {
				List<V3xBizAuthority> authList = findBizAuthorityByBizConfigId(bizConfig.getId());
				if(authList!=null)
					bizConfig.setV3xBizAuthorityList(authList);
			}
		}
		return v3xBizConfigList;
	}
	
	public V3xBizConfig findBizConfigById (Long bizConfigId) {
		return cache2Id.get(bizConfigId);
	}
	
	public V3xBizConfig findBizConfigByMenuId (Long menuId) {
		return cache2MenuId.get(menuId);
	}
	
	public List<Long> findAccessMenuIdsByScopeIds(List<Long> entIdsList) {
		return v3xBizConfigDao.findAccessMenuIdsByScopeIds(entIdsList);
	}
	
	public List<V3xBizConfigItem> findBizConfigItemByBizConfigId(Long bizConfigId) {
		V3xBizConfig c = findBizConfigById(bizConfigId);
		return c == null ? new ArrayList<V3xBizConfigItem>(0) : c.getV3xBizConfigItemList();
	}
	
	public List<V3xBizAuthority> findBizAuthorityByBizConfigId(Long bizConfigId) {
		V3xBizConfig c = findBizConfigById(bizConfigId);
		return c == null ? new ArrayList<V3xBizAuthority>(0) : c.getV3xBizAuthorityList();
	}

	/**
	 * 保存业务配置记录
	 */
	public void saveBizConfig(V3xBizConfig v3xBizConfig, List<V3xBizConfigItem> v3xBizConfigItemList, List<V3xBizAuthority> v3xBizAuthorityList) {
		v3xBizConfigDao.save(v3xBizConfig);
		v3xBizConfigDao.savePatchAll(v3xBizConfigItemList);
		v3xBizConfigDao.savePatchAll(v3xBizAuthorityList);
		
		v3xBizConfig.setV3xBizAuthorityList(v3xBizAuthorityList);
		v3xBizConfig.setV3xBizConfigItemList(v3xBizConfigItemList);
		
		updateMemory(v3xBizConfig);
	}
	
	/**
	 * 修改业务配置记录
	 */
	public void updateBizConfig(V3xBizConfig v3xBizConfig, List<V3xBizConfigItem> v3xBizConfigItemList, List<V3xBizAuthority> v3xBizAuthorityList) {
		v3xBizConfigDao.update(v3xBizConfig);
		
		List<Long> configIdList = new ArrayList<Long>();
		configIdList.add(v3xBizConfig.getId());
		
		v3xBizConfigDao.deleteBizConfigItem(configIdList);
		v3xBizConfigDao.deleteBizAuthority(configIdList);
		
		v3xBizConfigDao.savePatchAll(v3xBizConfigItemList);
		v3xBizConfigDao.savePatchAll(v3xBizAuthorityList);
		
		v3xBizConfig.setV3xBizAuthorityList(v3xBizAuthorityList);
		v3xBizConfig.setV3xBizConfigItemList(v3xBizConfigItemList);
		
		updateMemory(v3xBizConfig);
	}
	
	/**
	 * 批量删除多条业务配置记录
	 */
	public void deleteBizConfig(List<Long> bizConfigIds) {
		if(bizConfigIds == null || bizConfigIds.isEmpty()){
			return;
		}
		
		v3xBizConfigDao.deleteMenu4BizConfig(bizConfigIds);
		v3xBizConfigDao.deleteBizAuthority(bizConfigIds);
		v3xBizConfigDao.deleteBizConfigItem(bizConfigIds);
		v3xBizConfigDao.deleteBizConfig(bizConfigIds);
		
		for (Long c : bizConfigIds) {
			removeMemory(this.findBizConfigById(c));
		}
	}
	
	public boolean checkBizConfigName(String name, Long accountId, String bizConfigId){
		List result = v3xBizConfigDao.findBizConfigsByName(name, accountId, bizConfigId);
		if(result != null && result.size() >= 1){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<Menu> getAllFormBindMenus() {
		User user = CurrentUser.get();
		boolean isAdmin = user.isAdmin();
		List<Menu> menus = new ArrayList<Menu>();
		Set<Long> menuIds = new HashSet<Long>();

		if (isAdmin) {
			StringBuilder hql = new StringBuilder();
			Map<String, Object> param = new HashMap<String, Object>();
			hql.append(" select m from " + Menu.class.getName() + " m, " + V3xOrgMember.class.getName() + " u, " + V3xBizConfig.class.getName() + " b ");
			hql.append(" where m.id=b.menuId and u.id=b.createUser and u.orgAccountId=:accountId");
			param.put("accountId", user.getLoginAccount());
			List<Menu> list1 = this.find(hql.toString(), -1, -1, param);
			if (CollectionUtils.isNotEmpty(list1)) {
				menus.addAll(list1);
				for (Menu menu : list1) {
					menuIds.add(menu.getId());
				}
			}
		} else {
			Map<Long, Boolean> v3xBizMenuPurviewMap = FormBizConfigUtils.getV3xBizMenuPurviewMap(user.getId(), null);
			if (v3xBizMenuPurviewMap != null && v3xBizMenuPurviewMap.size() > 0) {
				menuIds = v3xBizMenuPurviewMap.keySet();
			}
		}

		if (menuIds.size() > 0) {
			StringBuilder hql1 = new StringBuilder();
			Map<String, Object> param1 = new HashMap<String, Object>();
			hql1.append(" from " + Menu.class.getName() + " m ");
			if (menuIds.size() <= 1000) {
				hql1.append(" where " + (isAdmin ? " m.parentId" : " m.id") + " in(:menuIds) ");
				param1.put("menuIds", menuIds);
			} else {
				hql1.append(" where ( ");
				int k = 0;
				Set<Long> newMenuIdSet = null;
				for (Long menuId : menuIds) {
					if (k % 1000 == 0) {
						String key = "menuIds" + k;
						newMenuIdSet = new HashSet<Long>();
						if (k != 0)
							hql1.append(" or ");
						hql1.append((isAdmin ? " m.parentId" : " m.id") + " in(:" + key + ") ");
						param1.put(key, newMenuIdSet);
					}
					newMenuIdSet.add(menuId);
					k++;
				}
				hql1.append(" ) ");
			}
			hql1.append(" order by m.sortId asc");

			List<Menu> list2 = this.find(hql1.toString(), -1, -1, param1);
			if (CollectionUtils.isNotEmpty(list2)) {
				menus.addAll(list2);
			}
		}

		Map<Long, Menu> mainMenus = new LinkedHashMap<Long, Menu>();
		for (Menu menu : menus) {
			if (menu.getParentId() == null) {
				mainMenus.put(menu.getId(), menu);
			}
		}
		
		for (Menu child : menus) {
			if (child.getParentId() != null) {
				Menu mainMenu = mainMenus.get(child.getParentId());
				if (mainMenu != null) {
					mainMenu.addChild(child);
				}
			}
		}

		List<Menu> result = new ArrayList<Menu>();
		for (Iterator<Map.Entry<Long, Menu>> iterator = mainMenus.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Long, Menu> entry = iterator.next();
			if (!entry.getValue().getChildren().isEmpty()) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

}