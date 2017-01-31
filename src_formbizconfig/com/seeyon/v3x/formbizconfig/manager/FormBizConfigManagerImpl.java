package com.seeyon.v3x.formbizconfig.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigColumnDao;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigDao;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigMenuProfileDao;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigShareScopeDao;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigTempletProfileDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigMenuProfile;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigShareScope;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigTempletProfile;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.formbizconfig.webmodel.MenuConfig;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 表单业务配置业务逻辑实现类，包含了对表单业务配置、表单模板与业务配置中间关系、共享范围、栏目挂接项、菜单挂接项的操作<br>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2009-08-12
 */
public class FormBizConfigManagerImpl extends BaseHibernateDao<FormBizConfig> implements FormBizConfigManager {
	private static final Log logger = LogFactory.getLog(FormBizConfigManagerImpl.class);
	private OrgManager orgManager;	
	private FormBizConfigDao formBizConfigDao;
	private FormBizConfigShareScopeDao formBizConfigShareScopeDao;
	private FormBizConfigColumnDao formBizConfigColumnDao;
	private FormBizConfigTempletProfileDao formBizConfigTempletProfileDao;
	private FormBizConfigMenuProfileDao formBizConfigMenuProfileDao;
	private MenuManager menuManager;
	private TempleteManager templeteManager;
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	
	public List<FormBizConfig> findAll(Long memberId, SearchModel searchModel) {
		List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(memberId, this.orgManager);
		return this.formBizConfigDao.searchAllDao(memberId, searchModel, domainIds);
	}
	
	public FormBizConfig findById (Long bizConfigId) {
		return this.formBizConfigDao.get(bizConfigId);
	}	

	public void saveBizConfig(FormBizConfig formBizConfig) {
		this.formBizConfigDao.save(formBizConfig);
	}

	public void updateBizConfig(FormBizConfig formBizConfig) {
		this.formBizConfigDao.update(formBizConfig);
	}
	
	public void updateBizConfigField(Long bizConfigId, String[] fieldNames, Object[] values) {
		this.formBizConfigDao.update(bizConfigId, FormBizConfigUtils.newHashMap(fieldNames, values));
	}
	
	public void deleteReal(List<Long> bizConfigIds) {
		this.formBizConfigShareScopeDao.deleteAll(bizConfigIds);
		this.formBizConfigTempletProfileDao.deleteAll(bizConfigIds);
		this.menuManager.deleteWithBizConfig(bizConfigIds);
		this.formBizConfigColumnDao.deleteWithBizConfigs(bizConfigIds);
		this.formBizConfigMenuProfileDao.deleteAll(bizConfigIds);
		this.formBizConfigDao.delete(bizConfigIds);
	}
	
	public boolean deleteBizConfigs (List<Long> bizConfigIds, Long memberId) throws BusinessException {
		boolean needRefreshMenu = false;
		for(Long bizConfigId : bizConfigIds) {
			FormBizConfig bizConfig = this.findById(bizConfigId);
			if(bizConfig.hasMenuConfig()) {
				needRefreshMenu = true;
				this.deletePersonalMenuSetting(bizConfig);
			}
		}
		this.deleteReal(bizConfigIds);
		return needRefreshMenu;
	}	
	
	public void deletePersonalMenuSetting(FormBizConfig bizConfig) throws BusinessException {
		Long bizConfigId = bizConfig.getId();
		Long creatorId = bizConfig.getCreateUser();
		Menu mainMenu = this.menuManager.getMainMenu4BizConfig(bizConfigId);
		if(mainMenu != null) {
			Long mainMenuId = mainMenu.getId();
			String scopeTypeAndIds = this.getShareScopeIds(bizConfigId);
			List<Long> memberIds = FormBizConfigUtils.getSharerAndCreatorIds(scopeTypeAndIds, creatorId, orgManager);
			this.menuManager.deleteMenuProfiles(memberIds, mainMenuId);
		}
		else {
			logger.warn("业务配置[" + bizConfig.getName() + ", ID=" + bizConfigId + "]具备菜单挂接，但对应的一级菜单却已不存在!");
		}
	}
	
	public void updateMenuSetting(Long mainMenuId, List<Long> memberIds, boolean toAdd) {
		if(CollectionUtils.isNotEmpty(memberIds)) {
			if(toAdd) {
				List<Long> profileMemberIds = menuManager.getMenuProfileAllMemberIds();
				List<Long> mIds = FormBizConfigUtils.getIntersection(memberIds, profileMemberIds);
				if(CollectionUtils.isNotEmpty(mIds)) {
					for(Long memberId : mIds) {
						this.menuManager.saveMenuProfileAtEnd(memberId, mainMenuId);
					}
				}
			} 
			else {
				this.menuManager.deleteMenuProfiles(memberIds, mainMenuId);
			}
		}
	}	
	
	public void cloneTempleteProfiles(Long orginalBizConfigId, Long newBizConfigId) {
		List<FormBizConfigTempletProfile> profiles = this.formBizConfigTempletProfileDao.getAll(orginalBizConfigId);
		List<FormBizConfigTempletProfile> newProfiles = new ArrayList<FormBizConfigTempletProfile>(profiles.size());
		for(FormBizConfigTempletProfile p : profiles) {
			FormBizConfigTempletProfile np = new FormBizConfigTempletProfile(newBizConfigId, p.getFormTempletId(), p.getSortId());
			newProfiles.add(np);
		}
		this.formBizConfigTempletProfileDao.savePatchAll(newProfiles);
	}
	
	public void cloneSubMenus(Long orginalBizConfigId, Long newBizConfigId, Long newParentMenuId) throws CloneNotSupportedException {
		List<Menu> oldSubMenus = this.menuManager.getSubMenus4BizConfig(orginalBizConfigId);
		int size = oldSubMenus.size();
		List<Menu> newSubMenus = new ArrayList<Menu>(size);
		List<FormBizConfigMenuProfile> fbfMenuProfiles = new ArrayList<FormBizConfigMenuProfile>(size);
		for(Menu menu : oldSubMenus) {
			Menu m = (Menu)menu.clone();
			m.setNewId();
			m.setParentId(newParentMenuId);
			m.setAction(menu.getAction().replaceFirst("bizConfigId=" + orginalBizConfigId, "bizConfigId=" + newBizConfigId));
			newSubMenus.add(m);
			fbfMenuProfiles.add(new FormBizConfigMenuProfile(m.getId(), newBizConfigId));
		}
		this.menuManager.saveAllMenus(newSubMenus);
		this.formBizConfigMenuProfileDao.savePatchAll(fbfMenuProfiles);
	}
	
	public void saveTempProfile(FormBizConfigTempletProfile formBizConfigTempletProfile) {
		this.formBizConfigTempletProfileDao.save(formBizConfigTempletProfile);
	}
	
	public void saveTempleteProfiles(String templeteIds, Long bizConfigId) {
		if(Strings.isNotBlank(templeteIds)) {
			List<Long> formTempIds = FormBizConfigUtils.parseStr2Ids(templeteIds);
			List<FormBizConfigTempletProfile> fbfTempProfiles = new ArrayList<FormBizConfigTempletProfile>();
			int sortId = 0;
			for(Long formTempId : formTempIds) {
				fbfTempProfiles.add(new FormBizConfigTempletProfile(bizConfigId, formTempId, sortId++));
			}
			this.formBizConfigTempletProfileDao.savePatchAll(fbfTempProfiles);
		}
	}
	
	public void saveProfile4Menu(Long menuId, Long formBizConfigId) {
		this.formBizConfigMenuProfileDao.save(new FormBizConfigMenuProfile(menuId, formBizConfigId));
	}
	
	public void saveMainAndSubMenus(String[] menuIdAndCategorys, String[] menuNames, FormBizConfig bizConfig,  
				String templeteIds, List<Long> addMenuProfileList, List<Long> deleteMenuProfileList) throws Exception {
		Long parentMenuId = this.saveMainMenuAndUpdateMenuSetting(bizConfig.getName(), bizConfig.getId(), 
					bizConfig.getCreateUser(), addMenuProfileList, deleteMenuProfileList);
		this.saveSubMenusAndProfiles(bizConfig.getId(), templeteIds, parentMenuId, menuIdAndCategorys, menuNames);
	}
	
	public Long saveMainMenuAndUpdateMenuSetting(String mainMenuName, Long bizConfigId, 
			Long memberId, List<Long> addMenuProfileList, List<Long> deleteMenuProfileList) {
		Long mainMenuId = this.menuManager.saveMainMenu4BizConfig(mainMenuName);
		this.saveProfile4Menu(mainMenuId, bizConfigId);
		// 处理创建者、新增共享对象和取消共享的对象的菜单设置变化
		this.menuManager.saveMenuProfileAtEnd(memberId, mainMenuId);
		this.updateMenuSetting(mainMenuId, addMenuProfileList, true);
		this.updateMenuSetting(mainMenuId, deleteMenuProfileList, false);
		
		return mainMenuId;
	}
	
	public void saveSubMenusAndProfiles(Long bizConfigId, String templeteIds, 
			Long mainMenuId, String[] menuIdAndCategorys, String[] menuNames) throws SeeyonFormException {
		List<Menu> childMenus = new ArrayList<Menu>();
		List<FormBizConfigMenuProfile> fbfMenuProfiles = new ArrayList<FormBizConfigMenuProfile>();
		List<Templete> temps = this.templeteManager.getTempletesByIds(FormBizConfigUtils.parseStr2Ids(templeteIds));
		String formIds = FormBizConfigUtils.getFormIds(temps);
		for(int i = 0; i < menuIdAndCategorys.length; i++) {
			String[] idAndCategory = menuIdAndCategorys[i].split(",");
			Menu childMenu = new Menu();
			childMenu.setNewId();
			childMenu.setParentId(mainMenuId);
			childMenu.setSortId(i);
			childMenu.setType(Menu.TYPE.formBizConfig.ordinal());
			childMenu.setName(menuNames[i]);
			childMenu.setTarget("main");
			childMenu.setIcon("/common/images/left/icon/newMenu.gif");
			// 根据所属业务操作类型及模板ID设定对应的链接地址
			childMenu.setAction(FormBizConfigUtils.getActionURL4Menu(Integer.parseInt(idAndCategory[1]), bizConfigId, idAndCategory[0], templeteIds, formIds));
			childMenus.add(childMenu);
			fbfMenuProfiles.add(new FormBizConfigMenuProfile(childMenu.getId(), bizConfigId));
		}
		this.menuManager.saveAllMenus(childMenus);
		this.formBizConfigMenuProfileDao.savePatchAll(fbfMenuProfiles);
	}
	
	public Menu getSubMenu4Location(Long bizConfigId, int category) {
		List<Menu> subMenus = this.menuManager.getSubMenus4BizConfig(bizConfigId);
		for(Menu menu : subMenus) {
			if(FormBizConfigUtils.getCategoryNum(menu.getAction()) == category) {
				return menu;
			}
		}
		return null;
	}
	
	public Menu getNewAffairMenu4Location(Long bizConfigId, String templeteId) {
		List<Menu> subMenus = this.menuManager.getSubMenus4BizConfig(bizConfigId);
		for(Menu menu : subMenus) {
			if(menu.getAction().indexOf(FormBizConfigConstants.URL_NEW_AFFAIRS + 
					"&flag=formBizConfig&bizConfigId=" + bizConfigId + "&templeteId=" + templeteId) != -1) {
				return menu;
			}
		}
		return null;
	}
	
	public List<MenuConfig> getMenuConfigs(Long bizConfigId) {
		List<Menu> subMenus = this.menuManager.getSubMenus4BizConfig(bizConfigId);
		List<MenuConfig> menuConfigs = null;
		if(CollectionUtils.isNotEmpty(subMenus)) {
			menuConfigs = new ArrayList<MenuConfig>(subMenus.size());
			for(Menu menu : subMenus) {
				String actionUrl = menu.getAction();
				// 根据菜单链接地址("...&templeteId="+tempId+"&type=...")截取表单模板ID：
				String templeteId = null;
				if(actionUrl.indexOf("&templeteId=") != -1) {
					if(actionUrl.indexOf("&type") != -1) 
						templeteId = actionUrl.substring(actionUrl.indexOf("&templeteId=") + "&templeteId=".length(), actionUrl.indexOf("&type"));
					else
						// 兼容链接地址未加入"&type=menu"之前的数据
						templeteId = actionUrl.substring(actionUrl.indexOf("&templeteId=") + "&templeteId=".length());
				}
				menuConfigs.add(new MenuConfig(menu, FormBizConfigUtils.getCategoryNum(actionUrl), templeteId));
			}
		}
		return menuConfigs;
	}
	
	public void saveShareInfo(String shareScope, Long bizConfigId) {	
		if(Strings.isNotBlank(shareScope)) {
			String[][] scopeInfo = Strings.getSelectPeopleElements(shareScope);
			List<FormBizConfigShareScope> scopes = new ArrayList<FormBizConfigShareScope>(scopeInfo.length);		
			for(int i = 0; i < scopeInfo.length; i++) {			
				scopes.add(new FormBizConfigShareScope(NumberUtils.toLong(scopeInfo[i][1]), scopeInfo[i][0], i, bizConfigId));
			}
			this.formBizConfigShareScopeDao.savePatchAll(scopes);
		}
	}
	
	public void deleteShareScopes(Long bizConfigId) {
		this.formBizConfigShareScopeDao.delete(new Object[][]{{"formBizConfigId", bizConfigId}});
	}
	
	public void deleteTempletProfiles(Long bizConfigId) {
		this.formBizConfigTempletProfileDao.delete(new Object[][]{{"formBizConfigId", bizConfigId}});
	}
	
	public void deleteMenuProfiles(Long bizConfigId) {
		this.formBizConfigMenuProfileDao.delete(new Object[][]{{"formBizConfigId", bizConfigId}});
	}

	public void deleteSubMenuProfiles(Long bizConfigId, Long mainMenuId) {
		this.formBizConfigMenuProfileDao.deleteSubMenuProfiles(bizConfigId, mainMenuId);
	}
	
	public Object[] isInfoCenterMenuExist(FormBizConfig bizConfig) {
		Boolean isExist = false;
		Long infoCenterMenuId = null;
		if(bizConfig != null && bizConfig.hasMenuConfig()) {
			List<Menu> subMenus = this.menuManager.getSubMenus4BizConfig(bizConfig.getId());
			for(Menu menu : subMenus) {
				if(FormBizConfigUtils.getCategoryNum(menu.getAction()) == FormBizConfigConstants.MENU_INFO_CENTER) {
					isExist = true;
					infoCenterMenuId = menu.getId();
					break;
				}
			}
		}
		return new Object[]{isExist, infoCenterMenuId};
	}
	
	@SuppressWarnings("unchecked")
	public List<FormBizConfigShareScope> getShareScopes(Long formBizConfigId) {
		String hql = "from " + FormBizConfigShareScope.class.getName() + " as s where s.formBizConfigId=? order by s.sortId asc";
		return this.formBizConfigShareScopeDao.find(hql, formBizConfigId);
	}
	
	public String getShareScopeIds(Long formBizConfigId) {
		StringBuffer scopeIds = new StringBuffer("");
		List<FormBizConfigShareScope> scopes = this.getShareScopes(formBizConfigId);
		if(CollectionUtils.isNotEmpty(scopes)) {
			for(FormBizConfigShareScope scope : scopes) {
				scopeIds.append(scope.getScopeType() + "|" + scope.getScopeId() + ",");
			}
		}
		
		return Strings.isBlank(scopeIds.toString()) ? "" : scopeIds.substring(0, scopeIds.length() - 1);
	}
	
	public boolean isShowToUser(long userId, Menu menu) {
		String hql = "select f from " + FormBizConfig.class.getName() + " f, " + FormBizConfigMenuProfile.class.getName() + " t" + 
					 " where (f.bizConfigType=? or f.bizConfigType=?) and f.id=t.formBizConfigId and t.menuId=? ";
		Object[] params = new Object[]{FormBizConfigConstants.CONFIG_TYPE_MENU, FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU, menu.getId()};
		FormBizConfig bizConfig = (FormBizConfig)this.formBizConfigDao.findUnique(hql, null, params);
		return this.isCreatorOrInShareScope(bizConfig, userId);
	}
	
	public boolean isCreatorOrInShareScope(FormBizConfig bizConfig, Long memberId) {
		return bizConfig != null && (bizConfig.getCreateUser().equals(memberId) || this.isInShareScopeNotCreator(bizConfig.getId(), memberId));
	}
	
	public boolean isInShareScopeNotCreator(Long bizConfigId, Long memberId) {
		String hql = "select count(s.id) from " + FormBizConfigShareScope.class.getName() + " as s " +
					 "where s.formBizConfigId=:bizConfigId and s.scopeId in (:domainIds)";
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(memberId, this.orgManager);
		params.put("bizConfigId", bizConfigId);
		params.put("domainIds", domainIds);
		int count = (Integer)this.formBizConfigShareScopeDao.findUnique(hql, params);
		return count > 0;
	}
	
	public Boolean[] validateIsExistAndInShareScope(Long bizConfigId, Long memberId) {
		FormBizConfig bizConfig = this.findById(bizConfigId);
		return new Boolean[]{bizConfig != null, this.isCreatorOrInShareScope(bizConfig, memberId)};
	}
	
	public String getTempleteIds(Long bizConfigId) {
		List<FormBizConfigTempletProfile> profiles = this.formBizConfigTempletProfileDao.getAll(bizConfigId);
		return Functions.join(profiles, FormBizConfigTempletProfile.PROP_TEMPLET_ID, ",");
	}
	
	public List<FormBizConfig> getFormBizConfigs4Column(Long memberId) {
		return this.formBizConfigDao.findAll4SpaceConfig(memberId, FormBizConfigUtils.getUserDomainIds(memberId, this.orgManager));
	}
	
	public int getSuperviseTotalCount4BizConfig(long userId, int status, List<Long> templeteIds){       
        StringBuffer hql = new StringBuffer("select count(de.id) from ");
		hql.append(" ColSuperviseDetail as de,ColSupervisor as su, ColSummary as summ ")
           .append(" where su.superviseId=de.id and de.entityId=summ.id ")
           .append(" and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ")
           .append(" and summ.templeteId in(:templeteIds) ");
        
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("templeteIds", templeteIds);
        parameterMap.put("userId", userId);
        parameterMap.put("entityType", Constant.superviseType.summary.ordinal());
        parameterMap.put("status", status);
        
        return (Integer)super.findUnique(hql.toString(), parameterMap);
	}

	public boolean checkFormQueryPlanName(String planName) {
		return formDaoManager.checkFormQueryPlanName(planName);
	}

	public void setFormBizConfigDao(FormBizConfigDao formBizConfigDao){
		this.formBizConfigDao = formBizConfigDao;
	}
	public void setFormBizConfigColumnDao(FormBizConfigColumnDao formBizConfigColumnDao) {
		this.formBizConfigColumnDao = formBizConfigColumnDao;
	}
	public void setFormBizConfigTempletProfileDao(FormBizConfigTempletProfileDao formBizConfigTempletProfileDao) {
		this.formBizConfigTempletProfileDao = formBizConfigTempletProfileDao;
	}
	public void setFormBizConfigShareScopeDao(FormBizConfigShareScopeDao formBizConfigShareScopeDao) {
		this.formBizConfigShareScopeDao = formBizConfigShareScopeDao;
	}
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setFormBizConfigMenuProfileDao(FormBizConfigMenuProfileDao formBizConfigMenuProfileDao) {
		this.formBizConfigMenuProfileDao = formBizConfigMenuProfileDao;
	}
	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
}