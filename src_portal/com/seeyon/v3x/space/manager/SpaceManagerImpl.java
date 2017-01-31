package com.seeyon.v3x.space.manager;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.CacheObject;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpace;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpaceManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.portal.SectionPortletFunction;
import com.seeyon.v3x.portal.util.CompareSortFragment;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.Constants.SpaceState;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.Constants.SpaceTypeClass;
import com.seeyon.v3x.space.domain.Banner;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.domain.SpacePage;
import com.seeyon.v3x.space.domain.SpaceSecurity;
import com.seeyon.v3x.space.domain.SpaceSort;
import com.seeyon.v3x.space.domain.UserFix;
import com.seeyon.v3x.space.domain.SpaceSecurity.SecurityType;
import com.seeyon.v3x.space.page.PageManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-6
 */
public class SpaceManagerImpl extends BaseHibernateDao<SpaceFix> implements
		SpaceManager {
	private static final Log log = LogFactory.getLog(SpaceManagerImpl.class);
	
	private final static String SPACEID = "spaceId";
	/**
	 * key:memberId
	 * value:("editKeyId",editKeyId),("spaceId",spaceId)
	 */
	private CacheMap<Long,HashMap<String,Long>> editSpaceKeyMap;
	/**
	 * Key:memberId
	 */
	private CacheMap<Long,ArrayList<Fragment>> cacheSaveFragment;
	/**
	 * Key:memberId
	 */
	private CacheMap<Long,ArrayList<Fragment>> cacheRemoveFragment;
	/**
	 * Key - 单位Id
	 */
	private CacheMap<Long, Long> defaultPersonalSpaces;
	private CacheMap<Long, Long> defaultDepartmentSpaces;
	private CacheMap<Long, Long> defaultLeaderSpaces;
	/**
	 * 单位个人自定义空间
	 */
	private CacheMap<Long, ArrayList<Long>> defaultPersonalCustomSpace;

	/**
	 * 单位默认外部人员空间
	 */
	private CacheMap<Long, Long> defaultOutPersonalSpace;

	/**
	 * 单位自定义空间列表
	 */
	private CacheMap<Long, ArrayList<Long>> customSpaces;

	private CacheMap<Long, SpaceFix> allCacheSpace;
	/**
	 * 公共自定义空间--单位
	 */
	private CacheMap<Long, ArrayList<Long>> publicCustomSpaces;
	
	private CacheMap<Long,ArrayList<Long>> publicCustomGroupSpaces;

	// key 部门id
	private CacheMap<Long, Long> departmentSpaces;

	private CacheMap<Long, Long> accountSpaces;

	private CacheObject<Long> groupSpace;

	// ***************//

	private PageManager pageManager = null;

	private OrgManager orgManager;

	private OuterlinkManager outerlinkManager;

	private ProjectManager projectManager;

	private PortletEntityPropertyManager portletEntityPropertyManager;

	private UserFixManager userFixManager;
	
	private SpaceSortManager spaceSortManager;

	public void setSpaceSortManager(SpaceSortManager spaceSortManager) {
		this.spaceSortManager = spaceSortManager;
	}

	public void setUserFixManager(UserFixManager userFixManager) {
		this.userFixManager = userFixManager;
	}

	public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setPageManager(PageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		CacheAccessable cacheFactory = CacheFactory
				.getInstance(SpaceManager.class);
		
		editSpaceKeyMap = cacheFactory.createMap("editSpaceKeyMap");
		cacheSaveFragment = cacheFactory.createMap("cacheSaveFragment");
		cacheRemoveFragment = cacheFactory.createMap("cacheRemoveFragment");

		defaultPersonalSpaces = cacheFactory.createMap("defaultPersonalSpaces");
		defaultDepartmentSpaces = cacheFactory
				.createMap("defaultDepartmentSpaces");
		defaultLeaderSpaces = cacheFactory.createMap("defaultLeaderSpaces");
		customSpaces = cacheFactory.createMap("customSpaces");
		departmentSpaces = cacheFactory.createMap("departmentSpaces");
		accountSpaces = cacheFactory.createMap("accountSpaces");
		groupSpace = cacheFactory.createObject("groupSpace");
		defaultPersonalCustomSpace = cacheFactory
				.createMap("defaultPersonalCustomSpace");
		defaultOutPersonalSpace = cacheFactory
				.createMap("defaultOutPersonalSpace");
		publicCustomSpaces = cacheFactory.createMap("publicCustomSpaces");
		allCacheSpace = cacheFactory.createMap("allCacheSpace");
		publicCustomGroupSpaces = cacheFactory.createMap("publicCustomGroupSpaces");
		allCacheSpace.setDataLoader(new AbstractMapDataLoader<Long, SpaceFix>(allCacheSpace) {
			@Override
			protected Map<Long, SpaceFix> loadLocal() {
				Map<Long, SpaceFix> result = new HashMap<Long, SpaceFix>();
				DetachedCriteria criteria = DetachedCriteria.forClass(SpaceFix.class)
				.add(Expression.in("type", new Integer[] {
								Constants.SpaceType.corporation.ordinal(),
								Constants.SpaceType.Default_personal.ordinal(),
								Constants.SpaceType.Default_department.ordinal(),
								Constants.SpaceType.department.ordinal(),
								Constants.SpaceType.group.ordinal(),
								Constants.SpaceType.custom.ordinal(),
								Constants.SpaceType.default_leader.ordinal(),
								Constants.SpaceType.Default_out_personal.ordinal(),
								Constants.SpaceType.Default_personal_custom.ordinal(),
								Constants.SpaceType.public_custom.ordinal(),
								Constants.SpaceType.public_custom_group.ordinal()})
								);

				List<SpaceFix> list = (List<SpaceFix>) executeCriteria(criteria, -1, -1);
				for (SpaceFix fix : list) {
					initSpaceFix(fix);
					result.put(fix.getId(), fix);
				}
				return result;
			}

			private void initSpaceFix(SpaceFix fix) {
				if(fix == null) return;
				Constants.SpaceType type = com.seeyon.v3x.util.EnumUtil
						.getEnumByOrdinal(Constants.SpaceType.class, fix.getType());
				Long account = fix.getAccountId();
				fix.init();
				switch (type) {
				case Default_personal:
				case Default_department:
					break;
				default:
					fix.setSpaceSecurities((ArrayList<SpaceSecurity>) getSpaceSecurity(fix.getId()));
					break;
				}
//					putToMemory(type, fix, account);
				updateCacheRef(type, fix, account);
			}

			@Override
			protected SpaceFix loadLocal(Long k) {
				final SpaceFix fix = SpaceManagerImpl.super.get(k);
				initSpaceFix(fix);
				return fix;
			}
		});
		allCacheSpace.reload();
	}

	private void putToMemory(SpaceType spaceType, SpaceFix fix, Long account) {
		updateCacheRef(spaceType, fix, account);
		allCacheSpace.put(fix.getId(), fix);
	}

	private void updateCacheRef(SpaceType spaceType, SpaceFix fix, Long account) {
		switch (spaceType) {
		case corporation:
			accountSpaces.put(account, fix.getId());
			break;
		case Default_personal:
			defaultPersonalSpaces.put(account, fix.getId());
			break;
		case Default_department:
			defaultDepartmentSpaces.put(account, fix.getId());
			break;
		case department:
			departmentSpaces.put(fix.getEntityId(), fix.getId());
			break;
		case custom:
			addToMap(customSpaces, account, fix.getId());
			break;
		case group:
			groupSpace.set(fix.getId());
			break;
		case default_leader:
			defaultLeaderSpaces.put(account, fix.getId());
			break;
		case Default_out_personal:
			defaultOutPersonalSpace.put(account, fix.getId());
			break;
		case Default_personal_custom:
			addToMap(defaultPersonalCustomSpace, account, fix.getId());
			break;
		case public_custom:
			addToMap(publicCustomSpaces, account, fix.getId());
			break;
		case public_custom_group:
			addToMap(publicCustomGroupSpaces, account, fix.getId());
			break;
		}
	}

	public synchronized SpaceFix createPersonalSpace(Long memberId,
			Long accountId) throws SpaceException {
		SpaceFix fix = this.getPersonalSpace(memberId);
		if (fix == null || fix.isNew()) {
			Long spaceId = this.getPersonalSpaceId(memberId);
			SpaceFix defaultFix = this.getSpace(spaceId);
			SpaceType defaultType = SpaceType.personal;
			String pagePath = Constants.PERSONAL_FOLDER + memberId
					+ Constants.DOCUMENT_TYPE;
			if (defaultFix != null) {
				defaultType = Constants
						.parseDefaultSpaceType(EnumUtil.getEnumByOrdinal(
								SpaceType.class, defaultFix.getType()));
				SpaceFix personalFix = this.getSpaceFix(pagePath);
				if (personalFix != null) {
					return personalFix;
				}
				switch (defaultType) {
				case personal_custom:
					pagePath = Constants.PERSONAL_CUSTOM_FOLDER + memberId
							+ Constants.DOCUMENT_TYPE;
					break;
				case leader:
					pagePath = Constants.LEADER_FOLDER + memberId
							+ Constants.DOCUMENT_TYPE;
					break;
				case outer:
					pagePath = Constants.OUTER_FOLDER + memberId
							+ Constants.DOCUMENT_TYPE;
					break;
				}
			}

			// 老数据防护
			if (this.pageManager.getPage(pagePath) != null) {
				try {
					this.removePage(pagePath);
				} catch (Exception e) {
					logger.error("删除个人空间" + e);
				}
			}
			SpaceType userSpaceType = Constants.SpaceType.personal;
			if (spaceId == null || defaultFix == null) {
				this.copyPage(this.getDefaultPersonalSpacePath(accountId),
						pagePath);
			} else {
				this.copyPage(defaultFix.getPagePath(), pagePath);
				userSpaceType = defaultType;
			}
			fix = new SpaceFix(userSpaceType, memberId, pagePath, accountId);
			fix.setIdIfNew();
			super.save(fix);
		}
		return fix;
	}

	public synchronized SpaceFix createPersonalDefineSpace(Long memberId,
			Long accountId,Long spaceId) throws SpaceException{
			SpaceFix fix = this.getSpace(spaceId);
			if (fix != null) {
				String pagePath = fix.getPagePath();
				SpaceType defaultType = EnumUtil.getEnumByOrdinal(
								SpaceType.class, fix.getType());
				String personalPagePath = pagePath;
				boolean isPersonalCustom = false;
				switch (defaultType) {
				case Default_personal:
					personalPagePath = Constants.PERSONAL_FOLDER + memberId
					+ Constants.DOCUMENT_TYPE;
					break;
				case Default_personal_custom:
					//个人自定义空间允许多个，采用UUID
					SpaceFix defineSpace = getDefinedPersonalCustomSpace(memberId,fix.getId());
					if(defineSpace!=null){
						return defineSpace;
					}else{
						Long spaceFixId = UUIDLong.longUUID();
						personalPagePath = Constants.PERSONAL_CUSTOM_FOLDER + spaceFixId
						+ Constants.DOCUMENT_TYPE;
						isPersonalCustom = true;
					}
					break;
				case default_leader:
					personalPagePath = Constants.LEADER_FOLDER + memberId
							+ Constants.DOCUMENT_TYPE;
					break;
				case Default_out_personal:
					personalPagePath = Constants.OUTER_FOLDER + memberId
							+ Constants.DOCUMENT_TYPE;
					break;
				}
				SpaceFix spaceFix = this.getSpaceFix(personalPagePath);
				if (spaceFix != null) {
					return spaceFix;
				}else{
					this.copyPage(pagePath, personalPagePath);
					SpaceType customType = Constants.parseDefaultSpaceType(defaultType);
					SpaceFix newSpaceFix = new SpaceFix(customType, memberId, personalPagePath, accountId);
					newSpaceFix.setIdIfNew();
					String spaceName = fix.getSpaceName();
					String slogan = fix.getSlogan();
					if(Strings.isNotBlank(spaceName)){
						newSpaceFix.setSpaceName(spaceName);
					}
					if(Strings.isNotBlank(slogan)){
						newSpaceFix.setSlogan(slogan);
					}
					newSpaceFix.setAllowdefined(fix.isAllowdefined());
					// 为个人自定义空间添加父ID，作为空间个性化以后与默认个人自定义空间关联关系
					if(isPersonalCustom){
						newSpaceFix.setParentId(fix.getId());
					}
					super.save(newSpaceFix);
					// 更新排序表
					SpaceSort sort = spaceSortManager.getSpaceSort(memberId, fix.getId());
					if(sort!=null){
						sort.setSpacePath(newSpaceFix.getId().toString());
						spaceSortManager.updateSpaceSort(sort);
					}
					// 更新userfix表,如果用户的默认空间是DefaultSpace,更新为个人自定义后的spaceId
					String defaultSpaceId = userFixManager.getFixValue(memberId, "spaceId");
					if(Strings.isNotBlank(defaultSpaceId)&&Long.valueOf(defaultSpaceId)==spaceId){
						userFixManager.saveOrUpdate(memberId, "spaceId", newSpaceFix.getId().toString());
					}
					
					//更新授权表
					SpaceSecurity security = new SpaceSecurity(newSpaceFix.getId(),SecurityType.used,"Member",memberId,0);
					security.setIdIfNew();
					super.save(security);
					putToMemory(customType, newSpaceFix, accountId);
					return newSpaceFix;
				}		
			}else{
				log.error("空间不存在，ID为："+spaceId);
				return null;
			}
	}

	@SuppressWarnings("unchecked")
	private SpaceFix getDefinedPersonalCustomSpace(Long memberId,Long parentSpaceId){
		String hql = "From "+SpaceFix.class.getName()+" where entityId = ? and parentId = ?";
		List<SpaceFix> list = super.find(hql, memberId,parentSpaceId);
		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public synchronized SpaceFix createDepartmentSpace(Long departmentId,
			String departmentName, Long accountId) throws SpaceException {
		SpaceFix fix = getSpaceFix(Constants.SpaceType.department,
				departmentId, null);
		if (fix != null) {
			if(fix.getState() != Constants.SpaceState.normal.ordinal()){
				enableDepartmentSpace(departmentId, true);
			}
			return fix;
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());
		String depPath = Constants.DEPARTMENT_FOLDER + departmentId
				+ Constants.DOCUMENT_TYPE;

		fix = new SpaceFix(Constants.SpaceType.department, departmentId,
				depPath, accountId);
		fix.setIdIfNew();
		fix.setUpdateTime(now);

		SpaceSecurity spaceSecurity = new SpaceSecurity(fix.getId(),
				SpaceSecurity.SecurityType.used,
				V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, 0);

		this.copyPage(this.getDefaultDepartmentSpacePath(accountId), depPath);
		
		List<SpaceSecurity> s = new ArrayList<SpaceSecurity>(1);
		s.add(spaceSecurity);
		
		fix.setSpaceSecurities(s);

		super.save(fix);
		super.save(spaceSecurity);

		putToMemory(Constants.SpaceType.department, fix, fix.getEntityId());

		return fix;
	}

	public void deleteDepartmentSpace(Long departmentId) throws SpaceException {
		SpaceFix fix = getSpaceFix(Constants.SpaceType.department,
				departmentId, null);
		if (fix != null) {
			try {
				String pagePath = fix.getPagePath();
				this.deleteSpaceAllSecurity(fix.getId());
				this.deleteSpaceFix(Constants.SpaceType.department,
						departmentId);
				removePage(pagePath);
				departmentSpaces.remove(fix.getEntityId());
				allCacheSpace.remove(fix.getId());
			} catch (Exception e) {
				log.error("删除部门空间异常", e);
				throw new SpaceException(e);
			}
		}
	}
	
	public void enableDepartmentSpace(Long departmentId, boolean isEnabled) throws SpaceException{
		SpaceFix fix = getSpaceFix(Constants.SpaceType.department, departmentId, null);
		if (fix != null) {
			fix.setState(isEnabled ? Constants.SpaceState.normal.ordinal() : Constants.SpaceState.invalidation.ordinal());

			super.update(fix);

			putToMemory(EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, fix
					.getType().intValue()), fix, fix.getAccountId());
		}
	}

	public void deletePersonalSpace(Long memerId) throws SpaceException {
		SpaceFix fix = getPersonalSpace(memerId);
		if (fix != null && !fix.isNew()) {
			String pagePath = fix.getPagePath();
			try {
				removePage(pagePath);
			} catch (Exception e) {
				logger.error("恢复默认空间之删除空间:" + e);
			}
			super.delete(fix.getId());
			allCacheSpace.remove(fix.getId());
		}
	}

	public void deleteGroupSpace() throws SpaceException {
		long accountId = 1L;
		SpaceFix fix = getSpaceFix(Constants.SpaceType.group, accountId, null);
		if (fix != null) {
			try {
				// 删除授权
				this.deleteSpaceAllSecurity(fix.getId());// //不删除违反数据完整约束
				String pagePath = fix.getPagePath();
				this.deleteSpaceFix(Constants.SpaceType.group, accountId);

				removePage(pagePath);
				groupSpace.clear();
				allCacheSpace.remove(fix.getId());
			} catch (Exception e) {
				log.error("删除空间异常", e);
				throw new SpaceException(e);
			}
		}
	}

	public void deleteCorporationSpace(Long accountId) throws SpaceException {
		deleteDefaultSpace(Constants.SpaceType.corporation, accountId);
	}

	public void deleteDefaultPersonalSpace(Long accountId)
			throws SpaceException {
		deleteDefaultSpace(Constants.SpaceType.Default_personal, accountId);
	}

	public void deleteDefaultDepartmentSpace(Long accountId)
			throws SpaceException {
		deleteDefaultSpace(Constants.SpaceType.Default_department, accountId);
	}

	public void deleteCustomSpace(Long spaceId, Long accountId,
			boolean isFromAjax) throws SpaceException {
		// 保存需要删除的SpaceFix，避免ConcurrentModificationException
		SpaceFix fix = getSpace(spaceId);
		if (fix != null) {
			try {
				String pagePath = fix.getPagePath();
				this.deleteSpaceAllSecurity(fix.getId());
				super.delete(new Object[][] { { "id", spaceId } });
				if (!isFromAjax) {
					removePage(pagePath);
					// 删除自定义空间后同步个人排序表
					super.delete(SpaceSort.class, new Object[][] { {
							"spacePath", fix.getId().toString() } });
				}
				SpaceType type = EnumUtil.getEnumByOrdinal(SpaceType.class, fix
						.getType());
				List<SpaceFix> customFix = null;
				switch (type) {
				case custom:
					customFix = getSpaces(customSpaces.get(accountId));
					if (customFix != null) {
						customFix.remove(fix);
						customSpaces.notifyUpdate(accountId);
					}
					break;
				case Default_personal_custom:
					final ArrayList<Long> accountFixs = this.defaultPersonalCustomSpace
							.get(accountId);
					customFix = getSpaces(accountFixs);
					if (customFix != null) {
						customFix.remove(fix);
						defaultPersonalCustomSpace.notifyUpdate(accountId);
					}
				case public_custom:
					customFix = getSpaces(publicCustomSpaces.get(accountId));
					if (customFix != null) {
						customFix.remove(fix);
						publicCustomSpaces.notifyUpdate(accountId);
						// defaultPersonalCustomSpace.put(accountId, customFix);
					}
				}
				this.allCacheSpace.remove(spaceId);

			} catch (Exception e) {
				log.error("删除空间异常", e);
			}
		}
	}

	private void removePage(String pagePath) throws Exception {
		SpacePage page = pageManager.getPage(pagePath);

		Fragment root = page.getRootFragment();
		List<Fragment> fragments = root.getChildFragments();

		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);

			this.portletEntityPropertyManager
					.deleteProperties(fragment.getId());
		}

		pageManager.removePage(pagePath);
	}

	public synchronized SpaceFix createCorporationSpace(Long accountId)
			throws SpaceException {
		SpaceFix fix = getSpaceFix(Constants.SpaceType.corporation, accountId,
				null);
		if (fix != null) {
			return fix;
		}

		String pagePath = Constants.CORPORATION_FOLDER + accountId
				+ Constants.DOCUMENT_TYPE;

		this.copyPage(this.getDefaultCorportionSpacePath(accountId), pagePath);

		fix = new SpaceFix(Constants.SpaceType.corporation, accountId,
				pagePath, accountId);
		fix.setIdIfNew();
		fix.setSlogan(Constants.getSloganKey());
		fix.setBanner(Constants.DEFAULT_BANNER);
		fix.serialExtProperties();

		super.save(fix);

		putToMemory(Constants.SpaceType.corporation, fix, accountId);

		return fix;
	}

	// 修改 不用同步
	public SpaceFix createGroupSpace() throws SpaceException {
		long accountId = 1L;
		SpaceFix fix = getSpaceFix(Constants.SpaceType.group, accountId, null);
		if (fix != null) {
			return fix;
		}

		String pagePath = Constants.GROUP_FOLDER + accountId
				+ Constants.DOCUMENT_TYPE;

		this.copyPage(this.getDefaultGroupSpacePath(accountId), pagePath);

		fix = new SpaceFix(Constants.SpaceType.group, accountId, pagePath,
				accountId);
		fix.setIdIfNew();
		Banner banner = new Banner();
		banner.setSlogan(Constants.getSloganKey());
		banner.setBanner(Constants.DEFAULT_BANNER);
		banner.setShowSearch("1");
		fix.setSpaceBanner(banner);
		fix.serialExtProperties();
		super.save(fix);

		putToMemory(Constants.SpaceType.group, fix, accountId);

		return fix;
	}

	public SpaceFix getDefualtGroupSpaceFix() {
		SpaceFix fix = null;
		Long accountId = 1l;
		String pagePath = Constants.DEFAULT_GROUP_PAGE_PATH;
		fix = new SpaceFix(Constants.SpaceType.group, accountId, pagePath,
				accountId);
		fix.setIdIfNew();
		fix.setSlogan(Constants.getValueOfKey(Constants.getSloganKey()));
		fix.setBanner(Constants.DEFAULT_BANNER);
		fix.serialExtProperties();
		return fix;
	}

	/**
	 * 创建自定义空间<br>
	 * 包括个人自定义、协作自定义、公共自定义空间<br>
	 * 
	 * @param customType
	 *            Default_personal_custom,custom,public_custom
	 * @param spaceId
	 *            判断是否存在
	 * @param accountId
	 *            单位id
	 */
	public synchronized SpaceFix createCustomSpace(SpaceType customType,
			Long spaceId, Long accountId) throws SpaceException {
		SpaceFix fix = null;
		if (spaceId != null) {
			fix = getSpaceFix(customType, accountId, spaceId);
			if (fix != null) {
				return fix;
			}
		}
		Long spaceFixId = UUIDLong.longUUID();
		String pagePath = Constants.CUSTOM_FOLDER + spaceFixId
				+ Constants.DOCUMENT_TYPE;
		String resourcePagePath = "";
		switch (customType) {
		case Default_personal_custom:
			pagePath = Constants.PERSONAL_CUSTOM_FOLDER + spaceFixId
					+ Constants.DOCUMENT_TYPE;
			resourcePagePath = Constants.DEFAULT_CUSTUM_PERSONAL;
			break;
		case custom:
			pagePath = Constants.CUSTOM_FOLDER + spaceFixId + Constants.DOCUMENT_TYPE;
			resourcePagePath = Constants.DEFAULT_CUSTOM_PAGE_PATH;
			break;
		case public_custom:
			pagePath = Constants.PUBLIC_FOLDER + spaceFixId + Constants.DOCUMENT_TYPE;
			resourcePagePath = Constants.DEFAULT_PUBLIC_PAGE_PATH;
			break;
		case public_custom_group:
			pagePath = Constants.PUBLIC_FOLDER + spaceFixId + Constants.DOCUMENT_TYPE;
			resourcePagePath = Constants.DEFAULT_PUBLIC_PAGE_PATH;
			break;
		}
		this.copyPage(resourcePagePath, pagePath);
		fix = new SpaceFix(customType, accountId, pagePath, accountId);

		fix.setId(spaceFixId);
		fix.setSlogan(Constants.getValueOfKey(Constants.getSloganKey()));
		fix.serialExtProperties();
		super.save(fix);

		putToMemory(customType, fix, accountId);

		return fix;
	}

	public boolean isCreateDepartmentSpace(Long departmentId) {
		SpaceFix fix = getSpaceFix(Constants.SpaceType.department,
				departmentId, null);
		return fix != null && fix.getState() == Constants.SpaceState.normal.ordinal();
	}

	public boolean isChangePersonalSpace(long memberId) {
		return getSpaceFix(Constants.SpaceType.personal, memberId, null) != null;
	}

	public boolean isChangeCorporationSpace(long accountId) {
		return getSpaceFix(Constants.SpaceType.corporation, accountId, null) != null;
	}

	public SpaceFix getSpaceFix(Constants.SpaceType type, long entityId,
			Long customSpaceId) {
		switch (type) {
		case Default_personal:
			return getSpace(defaultPersonalSpaces.get(entityId));
		case Default_department:
			return getSpace(defaultDepartmentSpaces.get(entityId));
		case custom:
			List<SpaceFix> customFix = getSpaces(customSpaces.get(entityId));
			if (customFix != null) {
				for (SpaceFix fix : customFix) {
					if (fix.getId().equals(customSpaceId)) {
						return fix;
					}
				}
			}
			break;
		case department:
			return getSpace(departmentSpaces.get(entityId));
		case corporation:
			SpaceFix result = getSpace(accountSpaces.get(entityId));
			return result;
		case group:
			return getSpace(groupSpace.get());
		case default_leader:// 领导空间
			return getSpace(defaultLeaderSpaces.get(entityId));
		case Default_out_personal:
			return getSpace(defaultOutPersonalSpace.get(entityId));
		case Default_personal_custom:
/*			List<SpaceFix> personalCustomFix = this.defaultPersonalCustomSpace
					.get(entityId);
			if (personalCustomFix != null) {
				for (SpaceFix fix : personalCustomFix) {
					if (fix.getId().equals(customSpaceId)) {
						return fix;
					}
				}
			}*/
			final ArrayList<Long> personalCustomFix = this.defaultPersonalCustomSpace.get(entityId);			
			if (personalCustomFix != null) {
				for (Long fixId : personalCustomFix) {
					if (fixId.equals(customSpaceId)) {
						return getSpace(fixId);
					}
				}
			}
			return null;
		case public_custom:
			List<SpaceFix> publicCustom = getSpaces(publicCustomSpaces.get(entityId));
			if (publicCustom != null && customSpaceId != null) {
				for (SpaceFix fix : publicCustom) {
					if (fix.getId().equals(customSpaceId)) {
						return fix;
					}
				}
			}
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SpaceFix.class);
		if (customSpaceId != null) {
			criteria.add(Expression.eq("id", customSpaceId));
		} else {
			criteria.add(Expression.eq("entityId", entityId)).add(
					Expression.eq("type", type.ordinal()));
		}

		return (SpaceFix) super.executeUniqueCriteria(criteria);
	}

	private void deleteSpaceFix(Constants.SpaceType type, long entityId) {
		super.delete(new Object[][] { { "entityId", entityId },
				{ "type", type.ordinal() } });
	}

	public String getPersonalSpacePath(long memberId, long accountId) {
		// 个人修改的
		SpaceFix spaceFix = this.getSpaceFix(Constants.SpaceType.personal,
				memberId, null);
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		}

		return this.getDefaultPersonalSpacePath(accountId);
	}

	public String getLeaderPath(Long memberId, Long accountId) {
		String leaderSpacePath = Constants.PERSONAL_FOLDER + memberId + "_L"
				+ Constants.DOCUMENT_TYPE;
		SpaceFix spaceFix = getSpaceFix(leaderSpacePath);
		if (spaceFix != null) {
			return leaderSpacePath;
		}
		return this.getDefaultLeaderSpacePath(accountId);
	}

	public String getCorporationSpacePath(long accountId) {
		SpaceFix spaceFix = getSpace(accountSpaces.get(accountId));
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		}

		return this.getDefaultCorportionSpacePath(accountId);
	}

	public boolean isAllowedUserDefinedPersonalSpace(long memberId) {
		Long spaceId = getPersonalSpaceId(memberId);
		if (spaceId != null) {
			SpaceFix fix = this.getSpace(spaceId);
			if (fix != null) {
				return fix.isAllowdefined();
			}
		}

		return true;
	}

	public boolean isAllowedUserDefinedDepartmentSpace(long accountId) {
		SpaceFix spaceFix = getSpace(defaultDepartmentSpaces.get(accountId));
		if (spaceFix != null) {
			return spaceFix.isAllowdefined();
		} else {
			return true;
		}
	}

	public String getDefaultPersonalSpacePath(long accountId) {
		SpaceFix spaceFix = getSpace(defaultPersonalSpaces.get(accountId));
		// 单位默认的个人空间
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		}

		// 系统默认的
		return Constants.DEFAULT_PERSONAL_PAGE_PATH;
	}

	public String getDefaultLeaderSpacePath(long accountId) {
		SpaceFix spaceFix = getSpaceFix(SpaceType.default_leader, accountId,
				null);
		// 单位默认的领导空间
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		}

		// 系统默认的
		return Constants.DEFAULT_LEADER_PAGE_PATH;
	}

	public String getDefaultOutSpacePath(long accountId) {
		SpaceFix spaceFix = getSpaceFix(SpaceType.Default_out_personal,
				accountId, null);
		// 单位默认的领导空间
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		}

		// 系统默认的
		return Constants.DEFAULT_OUT_PERSONAL_PAGE_PATH;
	}

	public String getDefaultDepartmentSpacePath(long accountId) {
		SpaceFix spaceFix = getSpace(defaultDepartmentSpaces.get(accountId));
		// 单位管理员修改的默认地址
		if (spaceFix != null) {
			SpacePage page = pageManager.getPage(spaceFix.getPagePath());
			if(page != null){
				return spaceFix.getPagePath();
			}
		}

		// 系统默认的
		return Constants.DEFAULT_DEPARTMENT_PAGE_PATH;
	}

	public String getDefaultCorportionSpacePath(long accountId) {
		// 系统默认的
		return Constants.DEFAULT_CORPORATION_PAGE_PATH;
	}

	public String getDefaultGroupSpacePath(long accountId) {
		// 系统默认的
		return Constants.DEFAULT_GROUP_PAGE_PATH;
	}

	private synchronized SpaceFix createDefaultPersonalSpaceFix(long accountId)
			throws SpaceException {
		String pagePath = null;
		SpaceFix spaceFix = getSpace(defaultPersonalSpaces.get(accountId));
		if (spaceFix != null) {
			return spaceFix;
		} else {
			pagePath = Constants.PERSONAL_FOLDER + accountId + "_D"
					+ Constants.DOCUMENT_TYPE;
			spaceFix = createDefaultSpace(Constants.SpaceType.Default_personal,
					accountId, this.getDefaultPersonalSpacePath(accountId),
					pagePath, null);
		}

		putToMemory(Constants.SpaceType.Default_personal, spaceFix, accountId);

		return spaceFix;
	}

	public String createDefaultPersonalSpace(long accountId)
			throws SpaceException {
		return this.createDefaultPersonalSpaceFix(accountId).getPagePath();
	}

	public synchronized SpaceFix createDefaultLeaderSpace(Long accountId)
			throws SpaceException {
		String pagePath = null;
		SpaceFix spaceFix = getSpaceFix(SpaceType.default_leader, accountId,
				null);
		if (spaceFix != null) {
			return spaceFix;
		} else {
			pagePath = Constants.LEADER_FOLDER + accountId + "_Leader"
					+ Constants.DOCUMENT_TYPE;
			spaceFix = createDefaultSpace(Constants.SpaceType.default_leader,
					accountId, this.getDefaultLeaderSpacePath(accountId),
					pagePath, null);
		}
		putToMemory(Constants.SpaceType.default_leader, spaceFix, accountId);
		return spaceFix;
	}
	
	public synchronized SpaceFix createDefaultDeptManagerSpace(Long accountId) throws SpaceException {
		String pagePath = Constants.PERSONAL_CUSTOM_FOLDER + "DeptManager" + accountId + Constants.DOCUMENT_TYPE;
		SpaceFix spaceFix = getSpaceFix(pagePath);
		if (spaceFix != null) {
			return spaceFix;
		} else {
			String spaceName = ResourceBundleUtil.getString(Constants.resource_main, "seeyon.top.deptManager.space.label");
			spaceFix = createDefaultSpace(SpaceType.Default_personal_custom, accountId, Constants.DEFAULT_DEPTMANAGER_PERSONAL, pagePath, spaceName);
			putToMemory(SpaceType.Default_personal_custom, spaceFix, accountId);
		}
		return spaceFix;
	}

	/**
	 * 创建默认的外部人员空间
	 * 
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public synchronized SpaceFix createDefaultOutSpace(Long accountId)
			throws SpaceException {
		String pagePath = null;
		SpaceFix spaceFix = getSpaceFix(SpaceType.Default_out_personal,
				accountId, null);
		if (spaceFix != null) {
			return spaceFix;
		} else {
			pagePath = Constants.OUTER_FOLDER + accountId + "_Out"
					+ Constants.DOCUMENT_TYPE;
			spaceFix = createDefaultSpace(
					Constants.SpaceType.Default_out_personal, accountId, this
							.getDefaultOutSpacePath(accountId), pagePath, null);
		}
		putToMemory(Constants.SpaceType.Default_out_personal, spaceFix,
				accountId);
		return spaceFix;
	}

	public SpaceFix getDefaultLeaderSpace(Long accountId) throws SpaceException {
		SpaceFix spaceFix = getSpaceFix(SpaceType.default_leader, accountId,
				null);
		if (spaceFix == null) {
			spaceFix = createDefaultLeaderSpace(accountId);
		}
		return spaceFix;
	}

	public synchronized String createDefaultDepartmentSpace(long accountId)
			throws SpaceException {
		String pagePath = null;
		SpaceFix spaceFix = getSpace(defaultDepartmentSpaces.get(accountId));
		if (spaceFix != null) {
			return spaceFix.getPagePath();
		} else {
			pagePath = Constants.DEPARTMENT_FOLDER + accountId + "_D"
					+ Constants.DOCUMENT_TYPE;
			spaceFix = createDefaultSpace(
					Constants.SpaceType.Default_department, accountId, this
							.getDefaultDepartmentSpacePath(accountId), pagePath, null);
		}

		putToMemory(Constants.SpaceType.Default_department, spaceFix, accountId);

		return pagePath;
	}

	private SpaceFix createDefaultSpace(Constants.SpaceType type,
			long accountId, String srcPagePath, String destPagePath, String spaceName)
			throws SpaceException {
		this.copyPage(srcPagePath, destPagePath);

		SpaceFix fix = new SpaceFix(type, accountId, destPagePath, accountId);
		fix.setIdIfNew();
		if (spaceName != null) {
			fix.setSpaceName(spaceName);
		}
		Banner banner = new Banner();
		banner.setSlogan(Constants.getSloganKey());
		banner.setShowSearch("1");
		fix.setSpaceBanner(banner);

		fix.setAllowdefined(Constants.DEFAULT_Allowdefined); // 是否允许自定义
		fix.serialExtProperties();
		super.save(fix);
		putToMemory(EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, fix
				.getType().intValue()), fix, fix.getAccountId());
		return fix;
	}

	private SpacePage copyPage(String srcPagePath, String destPagePath)
			throws SpaceException {
		try {
			SpacePage page = pageManager.getPage(destPagePath);
			if (page != null) {
				return page;
			}
		} catch (Exception e1) {
			log.error(e1.getMessage(), e1);
		}

		try {
			SpacePage template = pageManager.getPage(srcPagePath);
			SpacePage copy = pageManager.copyPage(srcPagePath, destPagePath);

			List<Fragment> templateFragments = template.getRootFragment()
					.getChildFragments();
			List<Fragment> copyFragments = copy.getRootFragment()
					.getChildFragments();

			for (int i = 0; i < templateFragments.size(); i++) {
				Fragment templateFragment = templateFragments.get(i);
				Fragment copyFragment = copyFragments.get(i);

				this.portletEntityPropertyManager.copyPropertys(
						templateFragment.getId(), copyFragment.getId());
			}

			return copy;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SpaceException(e);
		}
	}

	public SpaceFix getSpaceFix(String pagePath) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SpaceFix.class)
				.add(Expression.eq("pagePath", pagePath));

		return (SpaceFix) super.executeUniqueCriteria(criteria);
	}
	
	public void updateFixDCGSpaceInfo(String pagePath, String[][] managers,
			String[][] securities, String[][] vistors, Banner banner,
			String motto, boolean allowdefined, String spaceName, int state) {
		this.updateFixDCGSpaceInfo(pagePath, managers, securities, vistors, banner, motto, allowdefined, spaceName, state, false);
	}

	public void updateFixDCGSpaceInfo(String pagePath, String[][] managers,
			String[][] securities, String[][] vistors, Banner banner,
			String motto, boolean allowdefined, String spaceName, int state, boolean spaceMenuEnabled) {
		SpaceFix fix = this.getSpaceFix(pagePath);
		if (Strings.isNotBlank(spaceName)) {
			fix.setSpaceName(spaceName);
		}
		fix.setSpaceBanner(banner);

		fix.setAllowdefined(allowdefined);
		fix.serialExtProperties();
		fix.setState(state);
		fix.setSpaceMenuEnabled(spaceMenuEnabled);

		initSpaceSecurity(fix, managers, securities, vistors);

		super.update(fix);

		putToMemory(EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, fix
				.getType().intValue()), fix, fix.getAccountId());
	}

	private void deleteSpaceAllSecurity(long spaceFixId) {
		super.delete(SpaceSecurity.class, new Object[][] { { "spaceFixId",
				spaceFixId } });
	}

	@SuppressWarnings("unchecked")
	private List<SpaceSecurity> getSpaceSecurity(long spaceFixId) {
		DetachedCriteria departmentSecurities = DetachedCriteria.forClass(
				SpaceSecurity.class).add(
				Expression.eq("spaceFixId", spaceFixId));

		return super.executeCriteria(departmentSecurities, -1, -1);
	}

	public void updateFixPersonalSpaceInfo(long accountId, String spaceName,
			Banner banner, boolean allowdefined) {
		SpaceFix fix = getSpace(defaultPersonalSpaces.get(accountId));

		fix.setSpaceName(spaceName);
		fix.setAllowdefined(allowdefined);
		fix.setSpaceBanner(banner);
		fix.serialExtProperties();
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("extAttributes", fix.getExtAttributes());
		columns.put("spaceName", fix.getSpaceName());

		super.update(SpaceFix.class, fix.getId(), columns);
		putToMemory(SpaceType.Default_personal, fix, accountId);
	}

	public SpaceFix getSpace(Long id) {
		if (id == null)
			return null;
		SpaceFix fix = this.allCacheSpace.get(id);
		if (fix != null) {
			return fix;
		}
		this.allCacheSpace.reload(id);
		return this.allCacheSpace.get(id);
	}

	private List<SpaceFix> getSpaces(ArrayList<Long> idList) {
		if(idList==null) return null;
		List<SpaceFix> list = new ArrayList<SpaceFix>(idList.size());
		for (Long id : idList) {
			final SpaceFix space = getSpace(id);
			if(space==null){
				log.warn("指定的Space不存在："+id);
				continue;
			}
			list.add(space);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Map<Constants.SpaceType, List<SpaceModel>> getAccessSpace(Long memberId, Long accountId) throws SpaceException {
		Map<Constants.SpaceType, List<SpaceModel>> result = new EnumMap<Constants.SpaceType, List<SpaceModel>>(Constants.SpaceType.class);

		/**
		 * 个人空间（查询权限，如果有个人空间只能是领导空间、个人自定义空间），如果为空，给予默认空间
		 */
		List<SpaceModel> personalPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> personalCustomPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> leaderPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> outerPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> customPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> publicCustomPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> publicCustomGroupPaths = new ArrayList<SpaceModel>();
		List<SpaceModel> departmentSpace = new ArrayList<SpaceModel>();
		List<SpaceModel> corporationPagePaths = new ArrayList<SpaceModel>();
		
		// 我能访问的所有Id
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_LEVEL,ORGENT_TYPE_POST);
		} catch (BusinessException e) {
			log.error("", e);
		}
		
		List<Long> departmentIds = this.getDeptsByManager(memberId);// 我是主管的部门

		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		hql.append("select distinct fix from ").append(SpaceFix.class.getName()).append(" as fix, ").append(SpaceSecurity.class.getName()).append(" as sec");
		hql.append(" where (fix.id=sec.spaceFixId)");
		hql.append(" and (fix.state=:state)");
		hql.append(" and (");
		hql.append(" (sec.entityId in (:domainIds))");

		if (departmentIds != null && !departmentIds.isEmpty()) {
			hql.append(" or (fix.entityId in (:departmentIds)) ");
			parameterMap.put("departmentIds", departmentIds);
		}
		hql.append(")");

		parameterMap.put("state", Constants.SpaceState.normal.ordinal());
		parameterMap.put("domainIds", domainIds);

		List<SpaceFix> temp = super.find(hql.toString(), -1, -1, parameterMap);
		for (SpaceFix spaceFix : temp) {
			String path = spaceFix.getPagePath();
			Long entityId = spaceFix.getEntityId();
			SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, spaceFix.getType());
			String spaceName = Constants.getSpaceName(spaceFix);
			SpaceModel spaceModel = null;
			SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);

			switch (spaceType) {
			case personal:
				// 获取可访问个人空间时，判断个人空间是否进行过自定义
				personalPaths.add(new SpaceModel(spaceFix.getId(), null, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, spaceFix.isAllowdefined(),spaceFix.getParentId()));
				break;
			case personal_custom:
				// 个人自定义空间,model添加parentId,区别并关联默认个人自定义空间
				if(spaceFix.getAccountId().longValue() != accountId && spaceFix.getAccountId()!= 1L){
					spaceName += "(" + Functions.getAccountShortName(spaceFix.getAccountId()) + ")";
				}
				personalCustomPaths.add(new SpaceModel(spaceFix.getId(), null, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, spaceFix.isAllowdefined(),spaceFix.getParentId()));
				break;
			case leader:
				// 领导空间
				leaderPaths.add(new SpaceModel(spaceFix.getId(), null, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, spaceFix.isAllowdefined()));
				break;
			case outer:
				// 外部人员空间
				outerPaths.add(new SpaceModel(spaceFix.getId(), null, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, spaceFix.isAllowdefined()));
				break;
			case department:
				// 部门空间
				int sort = 1;
				Long id = spaceFix.getEntityId();
				V3xOrgDepartment dep = null;
				try {
					dep = this.orgManager.getDepartmentById(entityId);
					// 部门被删除或者停用后，都不在显示部门空间了
					if (dep == null || !dep.isValid() || dep.getIsDeleted()) {
						continue;
					}
					spaceName = dep.getName();
					if (dep.getOrgAccountId().longValue() != accountId && spaceFix.getAccountId()!= 1L) {
						spaceName += "(" + Functions.getAccountShortName(dep.getOrgAccountId()) + ")";
					}
				} catch (Exception e) {
					log.warn("查询部门名称：", e);
				}
				departmentSpace.add(new SpaceModel(id, spaceName, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, sort, spaceFix.getEntityId()));
				break;
			case corporation:
				// 单位空间
				if (entityId.longValue() != accountId.longValue()) {
					continue;
				}

				spaceModel = new SpaceModel(spaceFix.getId(), spaceName, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, 3);
				spaceModel.setSpaceMenuEnabled(spaceFix.isSpaceMenuEnabled());
				corporationPagePaths.add(spaceModel);
				break;
			// 团队自定义空间、单位自定义空间、集团自定义空间
			case custom:
				if(spaceFix.getAccountId().longValue() != accountId && spaceFix.getAccountId()!= 1L){
					spaceName += "(" + Functions.getAccountShortName(spaceFix.getAccountId()) + ")";
				}
				spaceModel = new SpaceModel(spaceFix.getId(), spaceName, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, 3, spaceFix.getId());
				spaceModel.setSpaceMenuEnabled(spaceFix.isSpaceMenuEnabled());
				customPaths.add(spaceModel);
				break;
			case public_custom:
				if(spaceFix.getAccountId().longValue() != accountId && spaceFix.getAccountId()!= 1L){
					spaceName += "(" + Functions.getAccountShortName(spaceFix.getAccountId()) + ")";
				}
				spaceModel = new SpaceModel(spaceFix.getId(), spaceName, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, 3);
				spaceModel.setSpaceMenuEnabled(spaceFix.isSpaceMenuEnabled());
				publicCustomPaths.add(spaceModel);
				break;
			case public_custom_group:
				if(spaceFix.getAccountId().longValue() != accountId && spaceFix.getAccountId()!= 1L){
					spaceName += "(" + Functions.getAccountShortName(spaceFix.getAccountId()) + ")";
				}
				spaceModel = new SpaceModel(spaceFix.getId(), spaceName, path, "", spaceName, spaceType, Constants.SpaceState.normal, null, 3);
				spaceModel.setSpaceMenuEnabled(spaceFix.isSpaceMenuEnabled());
				publicCustomGroupPaths.add(spaceModel);
				break;
			}
		}
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(memberId);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		//  调整备选空间：个人空间、领导空间、个人自定义空间不再互斥
		if(member!=null&&member.getIsInternal()){
			String personalPath = Constants.PERSONAL_FOLDER + memberId + Constants.DOCUMENT_TYPE;
			SpaceFix space_fix = this.getSpaceFix(personalPath);
			SpacePage personalPage = pageManager.getPage(personalPath);
			SpaceFix d_Fix = this.createDefaultPersonalSpaceFix(accountId);
			if (space_fix != null&&d_Fix!=null&&d_Fix.isAllowdefined()) {
				if(personalPage!=null){
					// 不存在自定义过的个人空间时，选用default
					SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, space_fix.getType());
					String spaceName = Constants.getSpaceName(space_fix);
					SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);
					if (CollectionUtils.isNotEmpty(personalPaths)) {
						personalPaths.clear();
					}
					personalPaths.add(new SpaceModel(space_fix.getId(), null, personalPath, "", spaceName, spaceType, Constants.SpaceState.normal, null, space_fix.isAllowdefined()));
				}else{
					log.info("spaceManagerImpl删除空间"+space_fix.getPagePath());
					this.allCacheSpace.remove(space_fix.getId());
					this.deleteSpaceAllSecurity(space_fix.getId());
					this.delete(space_fix);
				}
			}else{
				if (space_fix != null){
					log.info("spaceManagerImpl删除空间"+space_fix.getPagePath());
					this.allCacheSpace.remove(space_fix.getId());
					this.deleteSpaceAllSecurity(space_fix.getId());
					this.delete(space_fix);
				}
				// 取默认个人空间
				String path = d_Fix.getPagePath();
				SpaceType d_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, d_Fix.getType());
				String spaceName = Constants.getSpaceName(d_Fix);
				d_Type = Constants.parseDefaultSpaceType(d_Type);
				if (CollectionUtils.isNotEmpty(personalPaths)) {
					personalPaths.clear();
				}
				personalPaths.add(new SpaceModel(d_Fix.getId(), null, path, "", spaceName, d_Type, Constants.SpaceState.normal, null, d_Fix.isAllowdefined()));
			}
			if (CollectionUtils.isNotEmpty(personalPaths)) {
				result.put(SpaceType.personal, personalPaths);
			}
		}
		
		
		// 领导
		if(CollectionUtils.isNotEmpty(leaderPaths)){
			SpaceFix defautlLeaderSpace = this.createDefaultLeaderSpace(accountId);
			boolean hasMatched = false;
			if(defautlLeaderSpace!=null&&defautlLeaderSpace.getState()==SpaceState.normal.ordinal()){
				for(SpaceModel sModel : leaderPaths){
					if(sModel.getId().equals(defautlLeaderSpace.getId())){
						hasMatched = true;
					}
				}
				if(hasMatched){
					String leaderPath = Constants.LEADER_FOLDER + memberId + Constants.DOCUMENT_TYPE;
					SpaceFix leaderSpace = this.getSpaceFix(leaderPath);
					SpacePage leaderPage = pageManager.getPage(leaderPath);
					if(leaderSpace!=null&&leaderPage!=null&&defautlLeaderSpace.isAllowdefined()){
						SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, leaderSpace.getType());
						String spaceName = Constants.getSpaceName(leaderSpace);
						SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);
						leaderPaths.clear();
						leaderPaths.add(new SpaceModel(leaderSpace.getId(), null, leaderPath, "", spaceName, spaceType, Constants.SpaceState.normal, null, leaderSpace.isAllowdefined()));
						result.put(SpaceType.leader, leaderPaths);
					}else{
						SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, defautlLeaderSpace.getType());
						String spaceName = Constants.getSpaceName(defautlLeaderSpace);
						SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);
						leaderPaths.clear();
						leaderPaths.add(new SpaceModel(defautlLeaderSpace.getId(), null, defautlLeaderSpace.getPagePath(), "", spaceName, spaceType, Constants.SpaceState.normal, null, defautlLeaderSpace.isAllowdefined()));
						result.put(SpaceType.leader, leaderPaths);
						
					}
				}
			}
			
		}
		// 外部人员
		if(member!=null&&!member.getIsInternal()){
			String outPath = Constants.OUTER_FOLDER + memberId + Constants.DOCUMENT_TYPE;
			SpaceFix outSpace = this.getSpaceFix(outPath);
			SpacePage outerPage = pageManager.getPage(outPath);
			SpaceFix d_Fix = this.createDefaultOutSpace(accountId);
			if (outSpace != null&&d_Fix!=null&&d_Fix.isAllowdefined()) {
				if(outerPage!=null){
					// 不存在自定义过的个人空间时，选用default
					SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, outSpace.getType());
					String spaceName = Constants.getSpaceName(outSpace);
					SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);
					if (CollectionUtils.isNotEmpty(outerPaths)) {
						outerPaths.clear();
					}
					outerPaths.add(new SpaceModel(outSpace.getId(), null, outPath, "", spaceName, spaceType, Constants.SpaceState.normal, null, outSpace.isAllowdefined()));
				}else{
					log.info("spaceManagerImpl删除空间"+outSpace.getPagePath());
					this.allCacheSpace.remove(outSpace.getId());
					this.deleteSpaceAllSecurity(outSpace.getId());
					this.delete(outSpace);
				}
			}else{
				if(outSpace != null){
					log.info("spaceManagerImpl删除空间"+outSpace.getPagePath());
					this.allCacheSpace.remove(outSpace.getId());
					this.deleteSpaceAllSecurity(outSpace.getId());
					this.delete(outSpace);
				}
				// 取默认外部人员空间
				String path = d_Fix.getPagePath();
				SpaceType d_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, d_Fix.getType());
				String spaceName = Constants.getSpaceName(d_Fix);
				d_Type = Constants.parseDefaultSpaceType(d_Type);
				if (CollectionUtils.isNotEmpty(outerPaths)) {
					outerPaths.clear();
				}
				outerPaths.add(new SpaceModel(d_Fix.getId(), null, path, "", spaceName, d_Type, Constants.SpaceState.normal, null, d_Fix.isAllowdefined()));
			}
			if (CollectionUtils.isNotEmpty(outerPaths)) {
				result.put(SpaceType.outer, outerPaths);
			}
		}
		
		// 个人自定义
		if (CollectionUtils.isNotEmpty(personalCustomPaths)) {
			// 遍历清除被个性化过的默认个人自定义空间
			List<Long> customedDefSpaceIds = new ArrayList<Long>();
			List<Long> customedAllSpaceIds = new ArrayList<Long>();
			List<SpaceModel> newPersonalCustomPaths = new ArrayList<SpaceModel>();
			for(SpaceModel sModel : personalCustomPaths){
				customedAllSpaceIds.add(sModel.getId());
				Long customedDefSpaceId = sModel.getParentId();
				if(customedDefSpaceId!=null){
					SpaceFix parentPersonalCustomSpace = this.getSpace(customedDefSpaceId);
					if(parentPersonalCustomSpace!=null&&parentPersonalCustomSpace.isAllowdefined()){
						customedDefSpaceIds.add(customedDefSpaceId);
					}else{
						SpaceFix pcSpace = this.get(sModel.getId());
						if(pcSpace!=null){
							log.info("spaceManagerImpl删除空间"+pcSpace.getPagePath());
							this.allCacheSpace.remove(pcSpace.getId());
							this.deleteSpaceAllSecurity(pcSpace.getId());
							this.delete(pcSpace);
						}
					}
				}
			}
			if(CollectionUtils.isNotEmpty(customedDefSpaceIds)){
				for(Long id : customedDefSpaceIds){
					//父空间被授权，个性化空间才允许显示
					if(customedAllSpaceIds.contains(id)){
						for(int i=0; i<personalCustomPaths.size();i++){
							SpaceModel m = personalCustomPaths.get(i);
							if(m.getParentId()!=null&&m.getParentId().equals(id)){
								newPersonalCustomPaths.add(m);
								break;
							}
						}
					}
				}
			}
			for(SpaceModel sModel : personalCustomPaths){
				if(sModel.getParentId()==null&&!customedDefSpaceIds.contains(sModel.getId())){
					newPersonalCustomPaths.add(sModel);
				}
			}
			result.put(SpaceType.personal_custom, newPersonalCustomPaths);
		}
		// 团队自定义
		if (CollectionUtils.isNotEmpty(customPaths)) {
			result.put(SpaceType.custom, customPaths);
		}
		// 部门
		if (CollectionUtils.isNotEmpty(departmentSpace)) {
			result.put(Constants.SpaceType.department, departmentSpace);
		}
		// 单位
		if (corporationPagePaths.isEmpty()) {
			corporationPagePaths = getAccessCorporationSpace(accountId, true);
		}
		result.put(Constants.SpaceType.corporation, corporationPagePaths);
		// 单位自定义
		if (CollectionUtils.isNotEmpty(publicCustomPaths)) {
			result.put(SpaceType.public_custom, publicCustomPaths);
		}
		
		// 集团自定义公共空间
		if (CollectionUtils.isNotEmpty(publicCustomGroupPaths)) {
			result.put(SpaceType.public_custom_group, publicCustomGroupPaths);
		}
		
		// 集团
		List<SpaceModel> groupPagePaths = getAccessGroupSpace(true, memberId);
		result.put(Constants.SpaceType.group, groupPagePaths);

		return result;
	}

	public List<SpaceModel> getAccessCorporationSpace(long accountId,
			boolean isCheckEnabled) throws SpaceException {
		List<SpaceModel> corporationPagePaths = new ArrayList<SpaceModel>();
		User user = CurrentUser.get();
		boolean isInternal = true;
		if(user != null){
			isInternal = user.isInternal();
		}
		try {
			SpaceFix spaceFix = getSpace(accountSpaces.get(accountId));
			String spaceName = null;
			boolean spaceMenuEnabled = false;
			if (spaceFix != null) {
				spaceName = Constants.getSpaceName(spaceFix);
				spaceMenuEnabled = spaceFix.isSpaceMenuEnabled();
			} else {
				spaceName = Constants.getDefaultSpaceName(Constants.SpaceType.corporation);
			}
			boolean isEnabled = true; // 是否启用了该空间
			String pagePath = null;
			Constants.SpaceState spaceState = Constants.SpaceState.normal;
			if (spaceFix != null) {
				pagePath = spaceFix.getPagePath();
				if (spaceFix.getState() == Constants.SpaceState.invalidation.ordinal()) {
					isEnabled = false;
					spaceState = Constants.SpaceState.invalidation;
				}
				if (Strings.isNotBlank(spaceFix.getSpaceName())) {
					spaceName = spaceFix.getSpaceName();
				}
			} else {
				pagePath = this.getDefaultCorportionSpacePath(accountId);
				spaceFix = this.getSpaceFix(pagePath);
				if (spaceFix != null
						&& spaceFix.getState() == Constants.SpaceState.invalidation
								.ordinal()) {
					isEnabled = false;
					spaceState = Constants.SpaceState.invalidation;
				}
			}
			if (spaceFix != null) {
				// 如果已经授权，不在显示 或者没有授权，但是是外部人员
				List<SpaceSecurity> se = spaceFix.getSpaceUsers();
				if (se != null && (!se.isEmpty() || (se.isEmpty() && !isInternal))) {
					return corporationPagePaths;
				}
			}
			// 本单位
			if (isEnabled || !isCheckEnabled) {
				String corporationName = this.orgManager.getAccountById( accountId).getName();
				SpaceModel spaceModel = new SpaceModel(accountId, corporationName, pagePath, "", spaceName, Constants.SpaceType.corporation, spaceState, null);
				spaceModel.setSpaceMenuEnabled(spaceMenuEnabled);
				corporationPagePaths.add(spaceModel);
			}
		} catch (Exception e) {
			log.warn("查询单位名称", e);
		}

		return corporationPagePaths;
	}

	// 得到可以访问的集团空间
	public List<SpaceModel> getAccessGroupSpace(boolean isCheckEnabled,
			Long memberId) {
		List<SpaceModel> groupPagePaths = new ArrayList<SpaceModel>();
		if ((Boolean) SysFlag.frontPage_showGroupSpace.getFlag()) {
			try {
				V3xOrgAccount group = this.orgManager.getRootAccount();
				if (group != null) {
					boolean isEnabled = true; // 是否启用了该空间
					boolean isUser = true;// 是否对登陆者进行了授权
					String spaceName = Constants
							.getDefaultSpaceName(Constants.SpaceType.group);
					String groupName = group.getName();
					String groupPath = Constants.DEFAULT_GROUP_PAGE_PATH;
					Constants.SpaceState spaceState = Constants.SpaceState.normal;
					SpaceFix groupSpaceFix = getSpace(groupSpace.get());
					boolean spaceMenuEnabled = false;
					if (groupSpaceFix != null) {
						groupPath = groupSpaceFix.getPagePath();
						// 判断是否有使用权限
						List<SpaceSecurity> groupUserList = groupSpaceFix.getSpaceUsers();
						List<SpaceSecurity> groupAllList = groupSpaceFix.getSpaceSecurities();
						
						//如果没有授权使用者，则默认所有；如果有使用者授权，那空间管理员也可以访问
						if (groupUserList != null && !groupUserList.isEmpty()) {
							int temp = 0;
							List<Long> userDomainIds = orgManager.getUserDomainIDs(memberId,
											V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
											 ORGENT_TYPE_MEMBER,ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT,
												ORGENT_TYPE_TEAM, ORGENT_TYPE_LEVEL,ORGENT_TYPE_POST);
							if (userDomainIds != null && !userDomainIds.isEmpty()) {
								Set<Long> userDomainIdSets = new HashSet<Long>(userDomainIds);
								for (int i = 0; i < groupAllList.size(); i++) {
									SpaceSecurity security = groupAllList.get(i);
									if (userDomainIdSets.contains(security.getEntityId().longValue())
											|| security.getEntityId().longValue() == group.getId().longValue()) {
										temp++;
										break;
									}
								}
							}
							if (temp == 0)
								isUser = false;
						}else{
							//没有授权。判断是不是集团下的单位。是不是内部人员
							V3xOrgMember member = orgManager.getMemberById(memberId);
							if(member != null){
								if(!member.getIsInternal() || !Functions.isAccountInGroup(member.getOrgAccountId())){
									return groupPagePaths;
								}
							}
						}
						if (groupSpaceFix.getState() == Constants.SpaceState.invalidation.ordinal()) {
							isEnabled = false;
							spaceState = Constants.SpaceState.invalidation;
						}
						if (Strings.isNotBlank(groupSpaceFix.getSpaceName())) {
							spaceName = groupSpaceFix.getSpaceName();
						}
						spaceMenuEnabled = groupSpaceFix.isSpaceMenuEnabled();
					}
					if ((isEnabled || !isCheckEnabled) && isUser) {
						SpaceModel spaceModel = new SpaceModel(null, groupName,groupPath, "", spaceName,Constants.SpaceType.group, spaceState, null);
						spaceModel.setSpaceMenuEnabled(spaceMenuEnabled);
						groupPagePaths.add(spaceModel);
					}
				}
			} catch (Exception e) {
				log.warn("查询集团名称", e);
			}
		}
		return groupPagePaths;
	}

	private void removeByKey(String key, Set<String> keyList,
			List<String[]> list) {
		for (int i = 0; i < list.size(); i++) {
			String[] str = list.get(i);
			if (key.equals(str[1])) {
				list.remove(str);
				keyList.remove(key);
				i--;
			}
		}
	}

	private List<SpaceModel> chooseNotNull(List<SpaceModel>... args) {
		for (List<SpaceModel> list : args) {
			if (CollectionUtils.isNotEmpty(list))
				return list;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getSpaceSort(Long memberId, Long accountId,
			Locale userLocale, boolean isDefault,
			Map<SpaceType, List<SpaceModel>> accessSpaces)
			throws SpaceException {
		Set<String> queryResult = new HashSet<String>();
		Set<String> typeResult = new HashSet<String>();
		List<String[]> resultList = new ArrayList<String[]>();
		Set<String> personalResult = new HashSet<String>();
		Set<Long> parentIdResult = new HashSet<Long>(); 
		// 取得我可以访问的空间
		if (accessSpaces == null) {
			accessSpaces = this.getAccessSpace(memberId, accountId);
		}
		List<SpaceModel> personalPaths = accessSpaces.get(SpaceType.personal);
		List<SpaceModel> leaderPaths = accessSpaces.get(SpaceType.leader);
		List<SpaceModel> outPaths = accessSpaces.get(SpaceType.outer);
		List<SpaceModel> personalCustom = accessSpaces
				.get(SpaceType.personal_custom);

		List<SpaceModel> departmentPaths = accessSpaces
				.get(SpaceType.department);
		List<SpaceModel> corporationPaths = accessSpaces
				.get(SpaceType.corporation);
		List<SpaceModel> groupPaths = accessSpaces.get(SpaceType.group);
		List<SpaceModel> customPaths = accessSpaces.get(SpaceType.custom);
		List<SpaceModel> publicCustom = accessSpaces.get(SpaceType.public_custom);
		List<SpaceModel> publicCustomGroup = accessSpaces.get(SpaceType.public_custom_group);
		List<LinkSpace> linkSpaces = outerlinkManager.findLinkSpacesCanAccess(memberId);
		if (!isDefault) {
			// 先查SpaceSort，然后筛选
			String hql1 = "select ss.type, ss.spacePath,ss.isDeleted from "
					+ SpaceSort.class.getName()
					+ " as ss "
					+ " where ss.memberId=? and ss.accountId in(?,1) order by ss.sort asc";
			List<Object[]> result = super.find(hql1, -1, -1, null, memberId,
					accountId);
			if (result != null && !result.isEmpty()) {
				for (Object[] obj : result) {
					String spacePathId = String.valueOf(obj[1]);
					boolean isValid = true;
					SpaceType type = EnumUtil.getEnumByOrdinal(SpaceType.class, (Integer) obj[0]);
					type = Constants.parseDefaultSpaceType(type);
					String spaceName = Constants.getDefaultSpaceName(type);
					switch (type) {
					case personal:
						if (CollectionUtils.isNotEmpty(personalPaths)) {
							SpaceModel m = personalPaths.get(0);
							spaceName = m.getSpaceName();
							spacePathId = m.getId().toString();
						}else{
							isValid = false;
						}
						break;
					case leader:
						if (CollectionUtils.isNotEmpty(leaderPaths)) {
							SpaceModel m = leaderPaths.get(0);
							spaceName = m.getSpaceName();
							spacePathId = m.getId().toString();
						}else{
							isValid = false;
						}
						break;
					case personal_custom:
						isValid = false;
						if(CollectionUtils.isNotEmpty(personalCustom)){
							for(SpaceModel sm : personalCustom){
								Long smId = sm.getId();
								Long parentId = sm.getParentId();
								if(spacePathId.equals(String.valueOf(smId))||(parentId!=null&&spacePathId.equals(String.valueOf(parentId)))){
									if(parentId!=null){
										parentIdResult.add(parentId);
									}
									spaceName = sm.getSpaceName();
									spacePathId = String.valueOf(sm.getId());
									isValid = true;
									break;
								}
							}
						}						
						break;
					case outer:
						if (CollectionUtils.isNotEmpty(outPaths)) {
							SpaceModel m = outPaths.get(0);
							spaceName = m.getSpaceName();
							spacePathId = m.getId().toString();
						}else{
							isValid = false;
						}
						break;
					case custom:// 自定义空间
						Long customSpaceId = Long.parseLong(String.valueOf(obj[1]));
						if (CollectionUtils.isNotEmpty(customPaths)) {
							boolean isBreakThisCustomSpace = true;
							for (SpaceModel m : customPaths) {
								if (customSpaceId.equals(m.getId()) && !m.getState().equals(Constants.SpaceState.invalidation)) {
									isBreakThisCustomSpace = false;
									spaceName = m.getSpaceName();
									spacePathId = m.getId().toString();
									break;
								}
							}
							if (isBreakThisCustomSpace) {
								continue;
							}
						} else {
							continue;// 如果没有适合权限的自定义空间，不显示
						}
						break;
					case corporation:// 单位空间
						if(CollectionUtils.isNotEmpty(corporationPaths)){
							for(SpaceModel m : corporationPaths){
								String name = m.getSpaceName();
								if(m.getState().equals(Constants.SpaceState.normal)&&Strings.isNotBlank(name)){
									spaceName = m.getSpaceName();
									spacePathId = m.getId().toString();
									break;
								}
							}
						}else{
							continue;
						}
						break;
					case group:// 集团空间
						if (CollectionUtils.isEmpty(groupPaths)) {
							continue;
						}

						SpaceFix groupSpaceFix = getSpace(groupSpace.get());
						if (groupSpaceFix != null && Strings.isNotBlank(groupSpaceFix.getSpaceName())) {
							spaceName = groupSpaceFix.getSpaceName();
						}
						break;
					case department:// 部门空间
						// 最后一个判断是防护老数据
						if (CollectionUtils.isEmpty(departmentPaths)) {
							continue;
						} else {
							boolean containDep = false;
							for (SpaceModel model : departmentPaths) {
								if (model.getEntityId() != null && model.getEntityId().toString().equals(obj[1])) {
									spaceName = model.getSpaceName();
									containDep = true;
								}
							}
							if (!containDep) {
								continue;
							}
						}
						break;
					case public_custom:
						if(CollectionUtils.isNotEmpty(publicCustom)){
							boolean contain = false;
							for(SpaceModel model : publicCustom){
								if(String.valueOf(model.getId()).equals((String) obj[1])&& !model.getState().equals(Constants.SpaceState.invalidation)){
									spaceName = model.getSpaceName();
									contain = true;
									break;
								}
							}
							if(!contain){
								continue;
							}
						}
						break;
					case public_custom_group:
						if(CollectionUtils.isNotEmpty(publicCustomGroup)){
							boolean contain = false;
							for(SpaceModel model : publicCustomGroup){
								if(String.valueOf(model.getId()).equals((String) obj[1])&& !model.getState().equals(Constants.SpaceState.invalidation)){
									spaceName = model.getSpaceName();
									spacePathId = String.valueOf(model.getId());
									contain = true;
									break;
								}
							}
							if(!contain){
								continue;
							}
						}
						break;
					case related_system:// 关联系统扩展空间
						isValid = false;
                        if(CollectionUtils.isNotEmpty(linkSpaces)){
                        	Long linkSpaceId = Long.valueOf(obj[1].toString());
                        	for(LinkSpace linkSpace : linkSpaces){
                        		if(linkSpace.getId().equals(linkSpaceId)){
                        			spaceName = linkSpace.getSpaceName();
                        			if(Strings.isNotBlank(spaceName)){
                        				isValid = true;
                        				break;
                        			}
                        		}
                        	}
                        }
						break;
					case related_project:// 关联项目
						Long projectId = Long.valueOf(obj[1].toString());
						ProjectSummary project = null;
						try {
							project = projectManager.getProject(projectId);
						} catch (Exception e) {
							log.error("获取空间导航配置顺序对应关联项目时出现异常：", e);
						}
						if (project == null || project.getProjectState() >= ProjectSummary.state_close){
							isValid = false;
						}else{
							spaceName = project.getProjectName();
						}
						break;
					}
					if (isValid) {
						String[] objStr = { String.valueOf(type.ordinal()), spacePathId, spaceName, String.valueOf(obj[2]) };
						if(!queryResult.contains(spacePathId)){
							resultList.add(objStr);
							queryResult.add(spacePathId);
							typeResult.add(String.valueOf(type.ordinal()));
							if((type.ordinal()==0||type.ordinal()==10||type.ordinal()==15||type.ordinal()==16)&&String.valueOf(obj[2]).equals("false")){
								personalResult.add(spacePathId);
							}
						}
					}
				}
			}
		}
		if(CollectionUtils.isEmpty(personalResult)){
			// 追加用户排序以后新增加的空间，需要筛选一遍
			extraPersonal(typeResult, resultList, personalPaths, leaderPaths,outPaths, personalCustom);
		}
		// 部门空间---所有的部门空间全部给显示。
		if (CollectionUtils.isNotEmpty(departmentPaths)) {
			String typeStr = String.valueOf(SpaceType.department.ordinal());
			for (SpaceModel model : departmentPaths) {
				if (model.getEntityId() != null && !queryResult.contains(String.valueOf(model.getEntityId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getEntityId()), model.getSpaceName(), "false" };
					resultList.add(s);
				}
			}
		}
		if (!typeResult.contains("2") && corporationPaths != null && !corporationPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.corporation.ordinal());
			String[] s = { typeStr, typeStr, corporationPaths.get(0).getSpaceName(), "false" };
			resultList.add(s);
		}
		if (!typeResult.contains("3") && groupPaths != null && !groupPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.group.ordinal());
			String[] s = { typeStr, typeStr, groupPaths.get(0).getSpaceName(), "false" };
			resultList.add(s);
		}
		if(CollectionUtils.isNotEmpty(personalCustom)){
			int type = SpaceType.personal_custom.ordinal();
			for(SpaceModel model : personalCustom){
				String spaceId = String.valueOf(model.getId());
				Long parentId = model.getParentId();
				SpaceState state = model.getState();
				if(parentId!=null){
					if(!queryResult.contains(spaceId)&&!state.equals(SpaceState.invalidation)){
						String[] s = { String.valueOf(type), spaceId, model.getSpaceName(), "false" };
						resultList.add(s);
					}
				}else{
					if(!queryResult.contains(spaceId)&&!parentIdResult.contains(spaceId)&&!state.equals(SpaceState.invalidation)){
						String[] s = { String.valueOf(type), spaceId, model.getSpaceName(), "false" };
						resultList.add(s);
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(customPaths)) {
			int type = SpaceType.custom.ordinal();
			for (SpaceModel model : customPaths) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(publicCustom)) {
			int type = SpaceType.public_custom.ordinal();
			for (SpaceModel model : publicCustom) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(publicCustomGroup)) {
			int type = SpaceType.public_custom_group.ordinal();
			for (SpaceModel model : publicCustomGroup) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
				}
			}
		}
		// 集成第三方系统
		List<ThirdpartySpace> thirdpartySpaces = ThirdpartySpaceManager.getInstance().getAccessSpaces(orgManager, memberId);
		if (CollectionUtils.isNotEmpty(thirdpartySpaces)) {
			int type = Constants.SpaceType.thirdparty.ordinal();
			for (ThirdpartySpace thirdParty : thirdpartySpaces) {
				if (!queryResult.contains(thirdParty.getId())) {
					String[] s = { String.valueOf(type), thirdParty.getId(),thirdParty.getNameOfResouceBundle(userLocale),"false" };
					resultList.add(s);
				} else {
					for (int i = 0; i < resultList.size(); i++) {
						if (thirdParty.getId().equals(resultList.get(i)[1])) {
							resultList.get(i)[2] = thirdParty.getNameOfResouceBundle(userLocale);
							break;
						}
					}
				}
			}
		}

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	private void extraPersonal(Set<String> typeResult,
			List<String[]> resultList, List<SpaceModel> personalPaths,
			List<SpaceModel> leaderPaths, List<SpaceModel> outPaths,
			List<SpaceModel> personalCustom) {
		List<SpaceModel> defaultPersonal = chooseNotNull(leaderPaths,
				personalPaths, outPaths, personalCustom);
		boolean isAdd = false;
		if (typeResult.contains("10")) {
			removeByKey("10", typeResult, resultList);
			isAdd = true;
		} else if (typeResult.contains("0")) {
			removeByKey("0", typeResult, resultList);
			isAdd = true;
		} else if (typeResult.contains("16")) {
			removeByKey("16", typeResult, resultList);
			isAdd = true;
		} else if (typeResult.contains("15")) {
			removeByKey("15", typeResult, resultList);
			isAdd = true;
		} else {
			isAdd = true;
		}
		if (isAdd && defaultPersonal != null && defaultPersonal.size() > 0) {
			SpaceModel defSpace = defaultPersonal.get(0);
			String typeStr = String.valueOf(defSpace.getType().ordinal());
			String[] s = { typeStr, defSpace.getId().toString(), defSpace.getSpaceName(), "false" };
			if(CollectionUtils.isNotEmpty(resultList)){
				List<String[]> list = new ArrayList<String[]>();
				for(String[] result : resultList){
					list.add(result);
				}
				resultList.clear();
				resultList.add(s);
				for(String[] li : list){
					resultList.add(li);
				}
			}else{
				resultList.add(s);
			}
		}
	}

	public void deleteSpaceSort(Long memberId, Long accountId) {
		String hql = "delete from "+SpaceSort.class.getName()+" where memberId = :memberId and (accountId = :accountId or accountId = 1)";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("memberId", memberId);
		paramMap.put("accountId", accountId);
		super.bulkUpdate(hql, paramMap);
		
	}

	/**
	 * 根据类型及对应ID删除不可用的空间排序 如：关联系统被删除，关联项目被取消或废弃等情况，需直接删除对应的空间排序记录
	 * 
	 * @param memberId
	 * @param accountId
	 * @param spacePath
	 * @param type
	 */
	public void deleteSpaceSortByTypeAndSpacePath(Long memberId,
			Long accountId, String spacePath, int type) {
		super.delete(SpaceSort.class, new Object[][] {
				{ "memberId", memberId }, { "accountId", accountId },
				{ "spacePath", spacePath }, { "type", type } });
	}

	public void updateSpaceSort(List<String[]> sortList,
			List<String[]> disSort, Long memberId, Long accountId) {
		this.deleteSpaceSort(memberId, accountId);
		List<SpaceSort> list = new ArrayList<SpaceSort>();
		if (sortList != null && !sortList.isEmpty()) {
			int i = 1;
			for (String[] strings : sortList) {
				SpaceSort ss = new SpaceSort(memberId, accountId, strings[1],
						Integer.parseInt(strings[0]), i++);
				ss.setIdIfNew();
				ss.setIsDeleted(false);
				list.add(ss);
			}
			// 不能查看的空间
			if (CollectionUtils.isNotEmpty(disSort)) {
				for (String[] strings : disSort) {
					SpaceSort ss = new SpaceSort(memberId, accountId,
							strings[1], Integer.parseInt(strings[0]), i++);
					ss.setIdIfNew();
					ss.setIsDeleted(true);
					list.add(ss);
				}
			}
			if (!list.isEmpty()) {
				super.savePatchAll(list);
			}
			//增加更新个人默认空间
			userFixManager.saveOrUpdate(memberId, "spaceId", sortList.get(0)[1].toString());
		}
	}

	public String[] getLayoutType(String pagePath) {
		try {
			SpacePage page = this.pageManager.getPage(pagePath);
			String fragment = page.getRootFragment().getName();
			String decorator = page.getDefaultLayoutDecorator();

			return new String[] { fragment, decorator };
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	public Map<String, Map<String, Fragment>> getFragments(String pagePath)
			throws SpaceException {
		Map<String, Map<String, Fragment>> result = new LinkedHashMap<String, Map<String, Fragment>>();
		List<Integer> columnNumber = new ArrayList<Integer>();
		List<TheFragment> theFragments = new ArrayList<TheFragment>();
		try {
			SpacePage page = this.pageManager.getPage(pagePath);
			List<Fragment> fragments = page.getRootFragment().getChildFragments();
			if(CollectionUtils.isNotEmpty(fragments)){
				for (Fragment fragment : fragments) {
					TheFragment theFragment = new TheFragment(fragment);
					theFragments.add(theFragment);
					
					if (!columnNumber.contains(theFragment.getY())) {
						columnNumber.add(theFragment.getY());
					}
				}
				
			}
			Collections.sort(theFragments);
			Collections.sort(columnNumber);
			
			for (Integer row : columnNumber) {
				result.put(row.toString(), getCells(theFragments, row));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SpaceException(e);
		}

		return result;
	}
	
	public Map<String, Map<String, Fragment>> getFragments(String pagePath,Long editKeyId,Long memberId)throws SpaceException{
		Map<String, Map<String, Fragment>> result = new LinkedHashMap<String, Map<String, Fragment>>();
		List<Integer> columnNumber = new ArrayList<Integer>();
		List<TheFragment> theFragments = new ArrayList<TheFragment>();
		try {
			boolean isCurrentEdit = this.isCurrentEdit(editKeyId, memberId);
			List<Fragment> fragments;
			if(isCurrentEdit){
				fragments = cacheSaveFragment.get(memberId);
			}else{
				SpacePage page = this.pageManager.getPage(pagePath);
				fragments = page.getRootFragment().getChildFragments();
			}
			if(CollectionUtils.isNotEmpty(fragments)){
				for (Fragment fragment : fragments) {
					TheFragment theFragment = new TheFragment(fragment);
					theFragments.add(theFragment);
					
					if (!columnNumber.contains(theFragment.getY())) {
						columnNumber.add(theFragment.getY());
					}
				}
			}
			
			Collections.sort(theFragments);
			Collections.sort(columnNumber);
			
			for (Integer row : columnNumber) {
				result.put(row.toString(), getCells(theFragments, row));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SpaceException(e);
		}

		return result;
	}

	private static Map<String, Fragment> getCells(
			List<TheFragment> theFragments, int y) {
		List<Fragment> cells = new ArrayList<Fragment>();

		for (int i = 0; i < theFragments.size(); i++) {
			TheFragment theFragment = theFragments.get(i);

			if (theFragment.getY() == y) {
				cells.add(theFragment.getFragment());
				theFragments.remove(i);
				i--;
			}
		}
		Map<String, Fragment> result = new LinkedHashMap<String, Fragment>();
		for (Fragment fragment : cells) {
			result.put(fragment.getLayoutRow().toString(), fragment);
		}
		return result;
	}

	protected static class TheFragment implements Comparable<TheFragment>,
			Serializable {
		private static final long serialVersionUID = 6635790930851495256L;

		private int x;

		private int y;

		private Fragment fragment;

		public TheFragment(Fragment fragment) {
			x = fragment.getLayoutRow();
			y = fragment.getLayoutColumn();
			this.fragment = fragment;
		}

		public Fragment getFragment() {
			return fragment;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public String toString() {
			return y + "," + x;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TheFragment other = (TheFragment) obj;
			if (fragment == null) {
				if (other.fragment != null)
					return false;
			} else if (!fragment.equals(other.fragment))
				return false;
			return true;
		}

		public int compareTo(TheFragment o) {
			if (!this.equals(o)) {
				if (y == o.y) { // 同一列
					return x > o.x ? 1 : -1;
				} else {
					return y > o.y ? 1 : -1;
				}

			} else {
				return 0;
			}
		}
	}

	public List<Object[]> getSecuityOfDepartment(Long departmentId) {
		return this.getSecuityOfDepartment(departmentId, -1);
	}

	public List<Object[]> getSpaceAdminsOfDepartment(Long departmentId) {
		return this.getSecuityOfDepartment(departmentId,
				SpaceSecurity.SecurityType.manager.ordinal());
	}

	/**
	 * 获取部门空间指定访问权限类型的组织模型实体信息
	 * 
	 * @param departmentId
	 *            部门ID
	 * @param securityType
	 *            访问权限类型：使用(0)、管理(1)、不限定类型(-1)
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getSecuityOfDepartment(Long departmentId,
			int securityType) {
		StringBuffer hql = new StringBuffer("");
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" select sec.entityType,sec.entityId from "
				+ SpaceFix.class.getName() + " as space, "
				+ SpaceSecurity.class.getName() + " as sec ");

		hql
				.append(" where (space.id=sec.spaceFixId) and (space.entityId=:departmentId) ");
		params.put("departmentId", departmentId);

		if (securityType != -1) {
			hql.append(" and (sec.securityType=:securityType) )");
			params.put("securityType", securityType);
		}
		hql.append(" order by sec.sort ");

		List<Object[]> result = super.find(hql.toString(), -1, -1, params);

		if (securityType != SpaceSecurity.SecurityType.manager.ordinal())
			result.add(new Object[] { ORGENT_TYPE_DEPARTMENT, departmentId });

		return result;
	}
	
	/**
	 * 获取空间的组织模型实体信息
	 * 
	 * @param departmentId
	 *            空间ID
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getSecuityOfSpace(Long spaceId) {
		StringBuffer hql = new StringBuffer("");
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" select sec.entityType,sec.entityId from "+ SpaceFix.class.getName() + " as space, " + SpaceSecurity.class.getName() + " as sec ");
		hql.append(" where (space.id=sec.spaceFixId) and (sec.spaceFixId=:spaceId) ");
		params.put("spaceId", spaceId);
		hql.append(" order by sec.sort ");

		List<Object[]> result = super.find(hql.toString(), -1, -1, params);

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xOrgMember> getSpaceMemberBySecurity(Long spaceId, int securityType) {
		StringBuffer hql = new StringBuffer("");
		Set<V3xOrgMember> members = new HashSet<V3xOrgMember>();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" select sec.entityType,sec.entityId from " + SpaceFix.class.getName() + " as space, " + SpaceSecurity.class.getName() + " as sec");
		hql.append(" where (space.id=sec.spaceFixId) and (sec.spaceFixId=:spaceId) ");
		params.put("spaceId", spaceId);
		if (securityType != -1) {
			hql.append(" and (sec.securityType=:securityType) )");
			params.put("securityType", securityType);
		}
		hql.append(" order by sec.sort ");

		List<Object[]> result = super.find(hql.toString(), -1, -1, params);
		StringBuffer entityInfos = new StringBuffer();
		try {
			if(result != null && result.size() > 0) {
				for(Object[] arr : result) {
					entityInfos.append(StringUtils.join(arr, "|") + ",");
				}
				members = this.orgManager.getMembersByTypeAndIds(entityInfos.substring(0, entityInfos.length() - 1));
			}
		} catch (BusinessException e) {
			log.error("获取空间访问权限类型为" + securityType + "的组织模型实体信息异常", e);
		}
		List<V3xOrgMember> list = new ArrayList<V3xOrgMember>(members);
		Collections.sort(list,CompareSortEntity.getInstance());
		return list;
	}

	public List<Long> getSpaceAdminIdsOfDepartment(Long departmentId) {
		List<Object[]> typeAndIds = this
				.getSpaceAdminsOfDepartment(departmentId);
		String str = CommonTools.getTypeAndIdStrs(typeAndIds);
		return CommonTools.getMemberIdsByTypeAndId(str, orgManager);
	}

	/**
	 * 我是主管的部门
	 * 
	 * @param memberId
	 * @return
	 */
	private List<Long> getDeptsByManager(Long memberId) {
		List<Long> departmentIds = new ArrayList<Long>();
		try {
			List<V3xOrgDepartment> dm = this.orgManager
					.getDeptsByManager(memberId);
			if (dm != null && !dm.isEmpty()) {
				for (V3xOrgDepartment department : dm) {
					departmentIds.add(department.getId());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return departmentIds;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getCanManagerSpace(Long memberId) {
		List<Long> result = new ArrayList<Long>();
		try {
			List<Long> departmentIds = this.getDeptsByManager(memberId);// 我是部门主管，可以管理部门空间
			if (CollectionUtils.isNotEmpty(departmentIds)) {
				result.addAll(departmentIds);
			}

			List<Long> domainIds = this.orgManager.getUserDomainIDs(memberId,
					ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL, ORGENT_TYPE_TEAM,
					ORGENT_TYPE_MEMBER);

			if (CollectionUtils.isNotEmpty(domainIds)) {
				StringBuffer hql = new StringBuffer();
				hql.append("select space.id, space.entityId, space.type from ");
				hql.append(SpaceFix.class.getName()).append(" as space, ").append(SpaceSecurity.class.getName()).append(" as security ");
				hql.append(" where space.id=security.spaceFixId ");
				hql.append(" and security.securityType=:securityType and space.type in (:spaceTypes) and security.entityId in (:mEntityIds)");

				Map<String, Object> nameParameters = new HashMap<String, Object>();
				nameParameters.put("securityType", SpaceSecurity.SecurityType.manager.ordinal());
				nameParameters.put("spaceTypes", new Integer[] {
						SpaceType.department.ordinal(),
						SpaceType.corporation.ordinal(),
						SpaceType.group.ordinal(), SpaceType.custom.ordinal(),
						SpaceType.public_custom.ordinal(),
						SpaceType.public_custom_group.ordinal() });
				nameParameters.put("mEntityIds", domainIds);
				List<Object[]> list = super.find(hql.toString(), -1, -1, nameParameters);
				if (list != null) {
					for (Object[] object : list) {
						result.add(NumberUtils.toLong(object[0].toString()));
						int type = NumberUtils.toInt(object[2].toString());
						if (type == SpaceType.department.ordinal()
								|| type == SpaceType.corporation.ordinal()
								|| type == SpaceType.group.ordinal()) {
							result.add(NumberUtils.toLong(object[1].toString()));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getCanManagerCustomSpace(Long memberId) throws Exception {
		List<Long> result = new ArrayList<Long>();
		try {
			List<Long> domainIds = this.orgManager.getUserDomainIDs(memberId, 
					ORGENT_TYPE_ACCOUNT, 
					ORGENT_TYPE_DEPARTMENT, 
					ORGENT_TYPE_POST, 
					ORGENT_TYPE_LEVEL, 
					ORGENT_TYPE_TEAM, 
					ORGENT_TYPE_MEMBER);

			if (CollectionUtils.isNotEmpty(domainIds)) {
				StringBuffer hql = new StringBuffer();
				hql.append("select space.id from ");
				hql.append(SpaceFix.class.getName()).append(" as space, ").append(SpaceSecurity.class.getName()).append(" as security ");
				hql.append(" where space.id=security.spaceFixId ");
				hql.append(" and security.securityType=:securityType and space.type=:spaceType and security.entityId in (:domainIds)");

				Map<String, Object> nameParameters = new HashMap<String, Object>();
				nameParameters.put("securityType", SpaceSecurity.SecurityType.manager.ordinal());
				nameParameters.put("spaceType", SpaceType.custom.ordinal());
				nameParameters.put("domainIds", domainIds);
				result = this.find(hql.toString(), -1, -1, nameParameters);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getManagerDepartments(Long memberId, Long accountId) {
		Set<Long> result = new HashSet<Long>();

		try {
			List<Long> departmentIds = this.getDeptsByManager(memberId);// 我是主管的部门
			if (departmentIds != null && !departmentIds.isEmpty()) {
				result.addAll(departmentIds);
			}

			List<Long> domainIds = this.orgManager.getUserDomainIDs(memberId,
					ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_TEAM);
			if (domainIds != null && !domainIds.isEmpty()) {
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct space.entityId from ");
				hql.append(SpaceFix.class.getName()).append(" as space, ")
						.append(SpaceSecurity.class.getName()).append(" as s ");
				hql.append(" where (space.id=s.spaceFixId) ");
				hql.append(" and (space.accountId=?) and (space.type=?)");
				hql
						.append(" and (s.securityType=?) and (s.entityId in (:mEntityIds))");

				Map<String, Object> nameParameters = new HashMap<String, Object>();
				nameParameters.put("mEntityIds", domainIds);
				List<Long> list = super.find(hql.toString(), -1, -1,
						nameParameters, accountId,
						Constants.SpaceType.department.ordinal(),
						SpaceSecurity.SecurityType.manager.ordinal());
				if (list != null) {
					result.addAll(list);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		List<Long> depdIds = new ArrayList<Long>();
		for (Long id : result) {
			depdIds.add(id);
		}

		java.util.Collections.sort(depdIds);

		return depdIds;
	}

	@SuppressWarnings("unchecked")
	public boolean canManagerDepartments(Long memberId, Long departmentId) {
		try {
			List<V3xOrgDepartment> result = this.orgManager
					.getDeptsByManager(memberId);// 我是主管的
			if (result != null && !result.isEmpty()) {
				for (V3xOrgDepartment department : result) {
					if (departmentId.equals(department.getId())) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		String hql = "select space.entityId from " + SpaceFix.class.getName()
				+ " as space, " + SpaceSecurity.class.getName() + " as s ";
		hql += " where space.id=s.spaceFixId and s.securityType=?";
		hql += " and space.type=?";
		hql += " and s.entityId in (:mEntityIds)";

		try {
			List<Long> domainIds = this.orgManager.getUserDomainIDs(memberId,
					ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_TEAM);

			Map<String, Object> nameParameters = new HashMap<String, Object>();
			nameParameters.put("mEntityIds", domainIds);
			List<Long> list = super.find(hql, -1, -1, nameParameters,
					SpaceSecurity.SecurityType.manager.ordinal(),
					Constants.SpaceType.department.ordinal());
			if (list != null) {
				return list.contains(departmentId);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return false;
	}

	public EnumMap<PortletEntityProperty.PropertyName, String> getPortletEntityProperty(
			String pagePath) {
		EnumMap<PortletEntityProperty.PropertyName, String> result = new EnumMap<PortletEntityProperty.PropertyName, String>(
				PortletEntityProperty.PropertyName.class);
		String[] re = new String[] { "", "", "" };
		int pos = pagePath.indexOf(Constants.SEEYON_FOLDER);
		if (pos > -1) {
			re[0] = pagePath.substring(pos);

			String[] a = re[0].split("/");
			if (a == null || a.length != 4) {
				log.error("空间地址不正确，标准格式为[/seeyon/personal/***.psml]。当前URI : "
						+ re[0]);
				return null;
			}

			re[1] = a[2];
			re[2] = a[3].substring(0, a[3].lastIndexOf("."));

			if (re[2].equalsIgnoreCase("default-page")) {
				if (Constants.SpaceType.personal.name().equals(re[1])
						|| Constants.SpaceType.Default_personal.name().equals(
								re[1])) {
					re[2] = String.valueOf(CurrentUser.get().getId());
				} else if (Constants.SpaceType.corporation.name().equals(re[1])) {
					re[2] = String.valueOf(CurrentUser.get().getLoginAccount());
				} else {
					re[2] = "";
				}
			} else if (re[2].endsWith(Constants.DEFAULT_SPACE_SUBFIX)) {
				re[2] = re[2].substring(0, re[2].length()
						- Constants.DEFAULT_SPACE_SUBFIX.length());
			} else if (re[2].endsWith("_Out")) {
				re[2] = re[2].substring(0, re[2].length() - 4);
			}

			result.put(PortletEntityProperty.PropertyName.spaceType, re[1]);
			result.put(PortletEntityProperty.PropertyName.ownerId, re[2]);
		}

		return result;
	}

	public List<SpaceModel> getAdminCanManagerSpace(Long accountId,
			SpaceTypeClass spaceType, String condition, String value,
			Boolean needPage) throws SpaceException {
		if (accountId == 1L) {
			if (this.groupSpace.get() == null) {
				this.createGroupSpace();
			}
		} else {
			if (this.defaultOutPersonalSpace.get(accountId) == null || this.publicCustomSpaces.get(accountId) == null) {
				this.initAccountSpace(accountId);
			}
		}
		List<SpaceModel> result = new ArrayList<SpaceModel>();
		List<SpaceFix> showList = getAccountSpace(accountId, spaceType);
		List<SpaceFix> splitList = new ArrayList<SpaceFix>();
		if (showList != null && !showList.isEmpty()) {
			if (Strings.isNotBlank(condition)) {
				if ("name".equals(condition)) {
					for (SpaceFix fix : showList) {
						if (fix == null) {
							continue;
						}
						String spaceName = Constants.getSpaceName(fix);
						if (spaceName.indexOf(value) >= 0) {
							splitList.add(fix);
						}
					}
				} else if ("state".equals(condition)) {
					int state = Integer.parseInt(value);
					for (SpaceFix fix : showList) {
						if (fix != null && fix.getState() == state) {
							splitList.add(fix);
						}
					}
				}
			} else {
				splitList = showList;
			}
			if (needPage) {
				splitList = Constants.pagenate(splitList);
			}

			Collections.sort(splitList);
			for (SpaceFix fix : splitList) {
				if (fix != null) {
					Constants.SpaceState spaceState = Constants.SpaceState.normal;
					if (fix.getState() == Constants.SpaceState.invalidation
							.ordinal()) {
						spaceState = Constants.SpaceState.invalidation;
					}
					SpaceType type = EnumUtil.getEnumByOrdinal(SpaceType.class,
							fix.getType());
					String spaceName = Constants.getSpaceName(fix);
					result.add(new SpaceModel(fix.getId(), spaceName, fix
							.getPagePath(), "", spaceName, type, spaceState,
							fix.getSpaceUsers()));
				}
			}
		}
		return result;
	}

	public List<SpaceModel> getAdminCanManagerSpace(Long accountId,
			SpaceTypeClass spaceType, String condition, String value)
			throws SpaceException {
		return getAdminCanManagerSpace(accountId, spaceType, condition, value,
				true);
	}

	public boolean deleteCustomSpaces4AJAX(String[] beDeleteSpaceIds,
			String accountId) throws SpaceException {
		if (beDeleteSpaceIds != null && beDeleteSpaceIds.length > 0) {
			for (String idStr : beDeleteSpaceIds) {
				if (Strings.isNotBlank(idStr)) {
					deleteCustomSpace(Long.parseLong(idStr), Long
							.parseLong(accountId), true);
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private static <K extends Serializable, V extends Serializable> void addToMap(
			Map<K, ArrayList<V>> map, K k, V v) {
		ArrayList<V> list = map.get(k);

		if (list == null) {
			list = new ArrayList<V>();
			map.put(k, list);
		}
		for (int i = 0; i < list.size(); i++) {
			V ve = list.get(i);
			if (ve.equals(v)) {
				list.remove(ve);
				i--;
			}
		}
		list.add(v);
	}

	private static <K extends Serializable, V extends Serializable> void addToMap(
			CacheMap<K, ArrayList<V>> map, K k, V v) {
		ArrayList<V> list = map.get(k);

		if (list == null) {
			list = new ArrayList<V>();
		}
		for (int i = 0; i < list.size(); i++) {
			V ve = list.get(i);
			if (ve.equals(v)) {
				list.remove(ve);
				i--;
			}
		}
		list.add(v);
		map.put(k, list);
	}

	/**
	 * 删除默认的领导空间
	 */
	public void deleteDefaultLeaderSpace(Long accountId) throws SpaceException {
		deleteDefaultSpace(Constants.SpaceType.default_leader, accountId);
	}

	public void deleteDefaultSpace(SpaceType spaceType, Long accountId)
			throws SpaceException {
		SpaceFix fix = getSpaceFix(spaceType, accountId, null);
		if (fix != null) {
			try {
				this.deleteSpaceAllSecurity(fix.getId());// 不删除违反数据完整约束
				String pagePath = fix.getPagePath();
				this.deleteSpaceFix(Constants.SpaceType.default_leader,
						accountId);

				removePage(pagePath);
			} catch (Exception e) {
				log.error("删除空间异常", e);
				throw new SpaceException(e);
			}
		}
		switch (spaceType) {
		case Default_personal:
			this.defaultPersonalSpaces.remove(accountId);
			break;
		case default_leader:
			this.defaultLeaderSpaces.remove(accountId);
			break;
		case corporation:
			this.accountSpaces.remove(accountId);
			break;
		case Default_out_personal:
			this.defaultOutPersonalSpace.remove(accountId);
			break;
		case Default_department:
			defaultDepartmentSpaces.remove(accountId);
			break;
		}
		allCacheSpace.remove(fix.getId());
	}

	public void transplantDepartmentSpace(Long departmentId, Long newAccountId) {
		SpaceFix fix = getSpaceFix(Constants.SpaceType.department,
				departmentId, null);
		if (fix != null) {
			fix.setAccountId(newAccountId);
			super.update(fix);
		}
	}

	public boolean removePersonalSpaceSection(Long memberId, Long accountId,
			String sectionId, String singleBoardId) throws SpaceException {
		if (Strings.isBlank(sectionId)
				|| (sectionId.startsWith("singleBoard") && Strings
						.isBlank(singleBoardId))) {
			return false;
		}
		boolean result = false;
		try {
		V3xOrgMember member = orgManager.getMemberById(memberId);
		List<String[]> spaceSorts = this.getSpaceSort(memberId, accountId, member.getLocale(), false, null);
		if(CollectionUtils.isNotEmpty(spaceSorts)){
			for(String[] sort : spaceSorts){
				String type = sort[0];
				String spaceId = sort[1];
				String isDelete = sort[3];
				if((type.equals("0")||type.equals("10")||type.equals("15")||type.equals("16")||type.equals("4"))&&isDelete.equals("false")&&Strings.isNotBlank(spaceId)){
					SpaceFix fix = this.getSpace(Long.valueOf(spaceId));
					String pagePath = fix.getPagePath();
					SpacePage page = this.pageManager.getPage(pagePath);
					if (page != null) {
						Fragment rootFragment = page.getRootFragment();
						List<Fragment> fragments = rootFragment.getChildFragments();
						Map<Long,ArrayList<Integer>> removeFragment = new HashMap<Long,ArrayList<Integer>>();
						if(CollectionUtils.isNotEmpty(fragments)){
							for(Fragment fragment : fragments){
								Long entityId = fragment.getId();
								Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
								if(properties!=null&&properties.size()>0){
									String sectionName = properties.get(PortletEntityProperty.PropertyName.sections.name());
									if(Strings.isNotBlank(sectionName)){
										String[] sections = sectionName.split(",");
										for(int i=0; i<sections.length; i++){
											if(sections[i].equals(sectionId)){
												String value = properties.get("singleBoardId:"+i);
												if(Strings.isNotBlank(value)&&value.equals(singleBoardId)){
													ArrayList<Integer> list= removeFragment.get(entityId);
													if(CollectionUtils.isEmpty(list)){
														list = new ArrayList<Integer>();
													}
													list.add(i);
													removeFragment.put(entityId, list);
												}
											}
										}
									}
								}
							}
						}
						if(removeFragment!=null&&removeFragment.size()>0){
							Set<Long> removeEntityIds = removeFragment.keySet();
							for(Long removeEntityId : removeEntityIds){
								List<Integer> indexs = removeFragment.get(removeEntityId);
								Collections.sort(indexs);
								for(int i=indexs.size()-1;i>=0;i--){
									this.deleteFragment(removeEntityId, pagePath, indexs.get(i));
								}
							}
						}
					}
				}
			}
			return true;
		}
		} catch (Exception e) {
			result = false;
			log.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * 判断人员是否有权利访问领导空间
	 * 
	 * @param memberId
	 * @return
	 */
	private boolean isLeader(Long memberId) {
		try {
			List<V3xOrgAccount> accounts = orgManager
					.concurrentAccount(memberId);
			for (V3xOrgAccount account : accounts) {
				SpaceFix leaderFixs = getSpace(defaultLeaderSpaces.get(account
						.getId()));
				if (leaderFixs == null) {
					return false;
				}
				List<SpaceSecurity> securitys = leaderFixs.getSpaceUsers();
				if (securitys != null
						&& !securitys.isEmpty()
						&& leaderFixs != null
						&& leaderFixs.getState() == Constants.SpaceState.normal
								.ordinal()) {
					try {
						List<Long> domainIds = this.orgManager
								.getUserDomainIDs(memberId, ORGENT_TYPE_MEMBER,
										ORGENT_TYPE_DEPARTMENT,
										ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM,
										ORGENT_TYPE_LEVEL);
						if (domainIds != null && !domainIds.isEmpty()) {
							for (SpaceSecurity s : securitys) {
								if (domainIds.contains(s.getEntityId()
										.longValue())) {
									return true;
								}
							}
						}
					} catch (Exception e) {
						log.error("判断领导空间:" + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			log.error("判断领导空间:" + e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 得到个人/领导空间 如果没有创建，则返回默认的个人/领导空间
	 * 
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public SpaceFix getPersonSpace(Long memberId, Long accountId)
			throws SpaceException {
		V3xOrgMember member;
		try {
			member = orgManager.getMemberById(memberId);
			List<String[]> spaceSorts = this.getSpaceSort(memberId, accountId, member.getLocale(), false, null);
			String defaultSpaceId = null;
			if(CollectionUtils.isNotEmpty(spaceSorts)){
				for(String[] sort : spaceSorts){
					String spaceType = sort[0];
					String spaceId = sort[1];
					String isDelete = sort[3];
					if(spaceType.equals("0")||spaceType.equals("10")||spaceType.equals("15")||spaceType.equals("16")){
						if(isDelete.equals("false")){
							defaultSpaceId = spaceId;
							break;
						}
					}
				}
				return this.getSpace(Long.valueOf(defaultSpaceId));
			}else{
				return null;
			}
		} catch (BusinessException e) {
			log.error("查询个人空间" + e);
		}
		return null;
	}

	public SpaceFix getDefualtPersonSpace(Long accountId) throws SpaceException {
		SpaceFix spaceFix = getSpaceFix(SpaceType.Default_personal, accountId,
				null);
		return spaceFix;
	}

	private List<SpaceFix> getAccountSpace(Long accountId, SpaceTypeClass type) {
		Set<SpaceFix> result = new HashSet<SpaceFix>();
		switch (type) {
		case personal: {
			result.add(getSpace(defaultPersonalSpaces.get(accountId)));
			result.add(getSpace(defaultLeaderSpaces.get(accountId)));
			result.add(getSpace(defaultOutPersonalSpace.get(accountId)));
/*			List<SpaceFix> custom = this.defaultPersonalCustomSpace
					.get(accountId);*/
			ArrayList<Long> custom = this.defaultPersonalCustomSpace.get(accountId);			
			if (custom != null) {
//				result.addAll(custom);
				result.addAll(getSpaces(custom));
			}
			break;
		}
		case corporation:
			List<SpaceFix> cor = getSpaces(customSpaces.get(accountId));
			if (cor != null)
				result.addAll(cor);
/*			Collection<SpaceFix> list = this.departmentSpaces.values();
			for (SpaceFix fix : list) {
				if (fix.getAccountId().longValue() == accountId) {
					result.add(fix);
				}
			}*/
			//默认部门空间
			Long defaultDep  = this.defaultDepartmentSpaces.get(accountId);
			if(defaultDep != null){
				SpaceFix defaultDe = getSpace(defaultDep);
				if(defaultDe != null){
					result.add(defaultDe);
				}
			}
			final Collection<Long> idList = this.departmentSpaces.values();
			for (Long id : idList) {
				SpaceFix fix = getSpace(id);
				if (fix.getAccountId().longValue() == accountId) {
					result.add(fix);
				}
			}
			break;
		case public_:
			List<SpaceFix> pc = null;
			if (accountId == 1L) {
				result.add(getSpace(groupSpace.get()));
				pc =  getSpaces(publicCustomGroupSpaces.get(accountId));
			} else {
				Long aId = accountSpaces.get(accountId);
				if(aId != null){
					result.add(getSpace(aId));
				}
				pc = getSpaces(publicCustomSpaces.get(accountId));
			}
			if (pc != null) {
				result.addAll(pc);
			}
		}
		return new ArrayList<SpaceFix>(result);
	}

	// 判断是否有重名的个人空间
	public String checkSpace(String name, Integer state, String spaceType,
			Long curId) {
		User user = CurrentUser.get();
		Long accountId = null;
		if (user.isGroupAdmin()) {
			accountId = 1L;
		}else{
			if(curId!=null&&this.getSpace(curId)!=null){
				SpaceFix curSpace = this.getSpace(curId);
				accountId = curSpace.getAccountId();
			}else{
				accountId = user.getAccountId();
			}
		}
		SpaceTypeClass type = SpaceTypeClass.valueOf(spaceType);
		List<SpaceFix> personalAccount = getAccountSpace(accountId, type);
		for (SpaceFix fix : personalAccount) {
			if (fix == null)
				continue;
			String spaceName = Constants.getSpaceName(fix);
			// 同名校验
			if (fix.getId().longValue() != curId) {
				if (spaceName.equals(name))
					return "sameName";
			}
		}
		return "true";
	}

	public synchronized void updatePage(String pagePath, String newLayout,
			String newDecoration, List<Fragment> newFragments,
			Map<Long, Map<String, String>> portletEntityProperties) {
		try {
			SpacePage page = this.pageManager.getPage(pagePath);

			page.setDefaultDecorator(newDecoration, Fragment.Type.layout);

			Fragment root = page.getRootFragment();
			root.setName(newLayout);

			List<Fragment> fragments = root.getChildFragments();
			for (int i = 0; i < fragments.size(); i++) {
				Fragment fragment = fragments.get(i);
				long fragmentId = fragment.getId();

				this.pageManager.removeFragment(fragmentId);
				this.portletEntityPropertyManager.deleteProperties(fragmentId);
				fragments.remove(i);

				i--;
			}

			for (Fragment fragment : newFragments) {
				Map<String, String> properties = portletEntityProperties
						.get(fragment.getId());

				fragment.setParentId(root.getId());
				root.getChildFragments().add(fragment);

				this.pageManager.save(fragment);
				this.portletEntityPropertyManager.save(fragment.getId(),
						properties);
			}

			this.pageManager.updatePage(page);
			this.pageManager.updateFragment(root);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public SpacePage getSpacePage(String pagePath) {
		return this.pageManager.getPage(pagePath);
	}

	// 查询空间是否含有banner,有则返回,无则返回null
	public Banner getSpaceBanner(String spaceType, Long entityId) {
		Banner banner = null;
		return banner;
	}

	public void initAccountSpace(Long accountId) throws SpaceException {
		// 创建默认的部门主管空间（暂取消）
		// createDefaultDeptManagerSpace(accountId);
		
		if (this.defaultOutPersonalSpace.get(accountId) != null && this.accountSpaces.get(accountId) != null) {
			return;
		}
		// 创建默认的个人空间
		createDefaultPersonalSpace(accountId);
		// 创建默认的单位空间
		createCorporationSpace(accountId);
		// 创建默认的领导空间
		createDefaultLeaderSpace(accountId);
		// 创建默认的外部人员空间
		createDefaultOutSpace(accountId);
		//创建默认的部门空间
		createDefaultDepartmentSpace(accountId);
	}

	public void deleteAccountSpace(Long accountId) throws SpaceException {
		this.deleteDefaultPersonalSpace(accountId);
		this.deleteDefaultLeaderSpace(accountId);
		this.deleteCorporationSpace(accountId);
		this.deleteDefaultDepartmentSpace(accountId);
	}

	/**
	 * 恢复默认空间，但是权限保留。 <br>
	 * 以前做法是，将空间删除，然后在list 里面创建新的默认空间。这个做法欠妥。已经引用过的将丢失。 <br>
	 * 修改为：直接复制; 前端个人空间恢复默认的话，还是采用直接删除当前设置的空间。
	 */
	public SpaceFix toDefaultSpace(SpaceType spaceType, String[][] manages, String[][] users, String[][] vistors, Long entityId) throws SpaceException {
		if (spaceType == null) {
			return null;
		}
		
		SpaceFix spaceFix = getSpaceFix(spaceType, entityId, null);
		// 默认空间不存在，返回空
		if (spaceFix == null) {
			return null;
		}
		
		return this.toDefaultSpace(spaceType, manages, users, vistors, entityId, spaceFix);
	}

	public SpaceFix toDefaultSpace(SpaceType spaceType, String[][] manages, String[][] users, String[][] vistors, Long entityId, SpaceFix spaceFix) throws SpaceException {
		String defaultPagePath = "";
		User user = CurrentUser.get();
		if (spaceType == SpaceType.department) {
			defaultPagePath = this.createDefaultDepartmentSpace(user.getAccountId());
		} else {
			defaultPagePath = Constants.getDefaultPagePath(spaceType);
		}
		String destPagePath = spaceFix.getPagePath();
		try {
			this.removePage(destPagePath);
			this.copyPage(defaultPagePath, destPagePath);
			initSpaceSecurity(spaceFix, manages, users, vistors);
			spaceFix.setAllowdefined(true);
			spaceFix.setSpaceMenuEnabled(false);
			super.update(spaceFix);
		} catch (Exception e) {
			log.error("恢复默认空间：", e);
		}
		return spaceFix;
	}

	private void initSpaceSecurity(SpaceFix spaceFix, String[][] manages,
			String[][] users, String[][] vistors) {
		Set<SpaceSecurity> security = new HashSet<SpaceSecurity>();
		if (manages != null) {
			for (int i = 0; i < manages.length; i++) {
				String[] manager = manages[i];
				SpaceSecurity s = new SpaceSecurity(spaceFix.getId(),
						SpaceSecurity.SecurityType.manager, manager[0], Long.parseLong(manager[1]), i);
				security.add(s);
			}
		}
		if (users != null) {
			for (int i = 0; i < users.length; i++) {
				String[] securitie = users[i];
				SpaceSecurity s = new SpaceSecurity(spaceFix.getId(),
						SpaceSecurity.SecurityType.used, securitie[0], Long.parseLong(securitie[1]), i);
				security.add(s);
			}
		}
		//部门空间，追加当前部门到使用授权
		if(Constants.SpaceType.department.ordinal() == spaceFix.getType().intValue()){
			SpaceSecurity s = new SpaceSecurity(spaceFix.getId(), SpaceSecurity.SecurityType.used, "Department", spaceFix.getEntityId(), -1);
			security.add(s);
		}
		
		if (vistors != null) {
			for (int i = 0; i < vistors.length; i++) {
				String[] vistor = vistors[i];
				SpaceSecurity s = new SpaceSecurity(spaceFix.getId(),
						SpaceSecurity.SecurityType.vistor, vistor[0], Long
								.parseLong(vistor[1]), i);
				security.add(s);
			}
		}
		deleteSpaceAllSecurity(spaceFix.getId());
		spaceFix.setSpaceSecurities(new ArrayList<SpaceSecurity>(security));
		if (!security.isEmpty()) {
			super.savePatchAll(security);
		}
	}

	private SpaceFix getPersonalSpace(Long memberId) {
		try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			return getPersonalSpace(member);
		} catch (Exception e) {
		}

		return null;
	}

	private SpaceFix getPersonalSpace(V3xOrgMember member)
			throws SpaceException {
		//从userFix表获取默认个人空间或者自定义空间
		Long spaceId = getUserSpaceId(member.getId());
		// 空间id为空，旧数据
		SpaceFix spaceFix = null;
		// 判断是否是领导,只要授权就可以访问
		if (isLeader(member.getId())) {
			spaceFix = getSpace(defaultLeaderSpaces.get(member.getOrgAccountId()));
		}
		if (spaceFix == null && spaceId != null) {
			SpaceFix fix = this.getSpace(spaceId);
			if (fix != null) {
				if (fix.getType().intValue() == SpaceType.Default_personal
						.ordinal()
						|| fix.getType().intValue() == SpaceType.Default_out_personal
								.ordinal()
						|| canAccessSpace(fix, member.getId())) {
					spaceFix = fix;
				}
			}
		}
		if (spaceFix == null) {
			// 外部人员
			if (member.getIsInternal()) {
				spaceFix = this.createDefaultPersonalSpaceFix(member
						.getOrgAccountId());
			} else {
				spaceFix = this.createDefaultOutSpace(member.getOrgAccountId());
			}
		}
		// 上面取得都是父人员父空间，取得子空间
		SpaceType parentSpace = EnumUtil.getEnumByOrdinal(SpaceType.class,
				spaceFix.getType());
		SpaceType currentSpace = Constants.parseDefaultSpaceType(parentSpace);
		SpaceFix personaFix = this.getSpaceFix(currentSpace, member.getId(),
				null);
		if (personaFix == null) {
			personaFix = new SpaceFix(currentSpace, member.getId(), spaceFix
					.getPagePath(), member.getOrgAccountId());
		}
		personaFix.setSpaceName(Constants.getSpaceName(spaceFix));
		return personaFix;
	}

	public Long getPersonalSpaceId(Long memberId) {
		try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			return getPersonalSpaceId(member);
		} catch (Exception e) {
		}
		return null;
	}

	public Long getPersonalSpaceId4Create(boolean isInternal, Long accountId) {
		try {
			initAccountSpace(accountId);
		} catch (SpaceException e) {
			log.error(e.getMessage(), e);
		}
		SpaceFix spaceFix = null;
		if (isInternal) {
			spaceFix = getSpace(defaultPersonalSpaces.get(accountId));
		} else {
			spaceFix = getSpace(defaultOutPersonalSpace.get(accountId));
		}
		if (spaceFix != null) {
			return spaceFix.getId();
		}
		return null;
	}

	public Long getPersonalSpaceId(V3xOrgMember member) {
		if (member == null)
			return null;
		try {
			initAccountSpace(member.getOrgAccountId());
		} catch (SpaceException e) {
			log.error(e.getMessage(), e);
		}
		Long spaceId = getUserSpaceId(member.getId());
		// 旧数据
		SpaceFix spaceFix = null;
		if (spaceId == null) {
			// 判断是否具有领导空间权限(以前只要是授权领导的，就显示为领导空间)
			if (isLeader(member.getId())) {
				spaceFix = getSpace(defaultLeaderSpaces.get(member.getOrgAccountId()));
				if (spaceFix != null
						&& spaceFix.getState() == Constants.SpaceState.normal
								.ordinal()) {
					return spaceFix.getId();
				}
			}
		} else {
			spaceFix = this.getSpace(spaceId);
			if (spaceFix != null) {
				if (spaceFix.getType().intValue() == SpaceType.Default_personal
						.ordinal()
						|| spaceFix.getType().intValue() == SpaceType.Default_out_personal
								.ordinal()
						|| canAccessSpace(spaceFix, member.getId())) {
					return spaceFix.getId();
				}
			}
		}
		try {
			if (member.getIsInternal()) {
				spaceFix = this.createDefaultPersonalSpaceFix(member
						.getOrgAccountId());
			} else if (!member.getIsInternal()) {
				spaceFix = this.createDefaultOutSpace(member.getOrgAccountId());
			}
		} catch (SpaceException e) {
			log.error(e.getMessage(), e);
		}
		if (spaceFix != null) {
			return spaceFix.getId();
		}
		return null;
	}

	/**
	 * 判断用户是否某一特定空间的空间管理员
	 * @param memberId 当前用户ID
	 * @param spaceId   当前空间ID
	 * @return 是否为当前空间管理员
	 * @throws BusinessException 
	 */
	public boolean isManagerOfThisSpace(long memberId, Long spaceId) throws BusinessException {
		List<Long> managerSpaces = getCanManagerSpace(memberId);
		return CollectionUtils.isNotEmpty(managerSpaces) && managerSpaces.contains(spaceId);
	}
	
	// 查询人员是否可以访问该空间
	private boolean canAccessSpace(SpaceFix spaceFix, Long memberId) {
		try {
			if (spaceFix.getState() != Constants.SpaceState.normal.ordinal()) {
				return false;
			}
			List<SpaceSecurity> list = spaceFix.getSpaceSecurities();
			if (list != null && !list.isEmpty()) {
				List<Long> domainIds = this.orgManager.getUserDomainIDs(
						memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
						ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
						ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM,
						ORGENT_TYPE_LEVEL);
				for (SpaceSecurity security : list) {
					if (domainIds.contains(security.getEntityId())) {
						return true;
					}
				}
			}

		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean canDisableOrDelete(Long spaceId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UserFix.class);
		criteria.add(Restrictions.eq("value", spaceId.toString()));
		return getCountByCriteria(criteria) == 0;
	}

	public void putMember2SpaceSecurity(V3xOrgMember member) {
		try {
			Long spaceId = member.getSpaceId();
			if (spaceId == null)
				return;
			SpaceFix fix = this.getSpace(spaceId);
			// 默认个人空间和外部人员空间不授权
			if (fix != null
					&& fix.getType() != SpaceType.Default_personal.ordinal()
					&& fix.getType() != SpaceType.Default_out_personal
							.ordinal()) {
				List<SpaceSecurity> list = fix.getSpaceSecurities();
				boolean contain = false;
				if (list != null && !list.isEmpty()) {
					List<Long> domainIds = this.orgManager.getUserDomainIDs(
							member.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
							ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
							ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM,
							ORGENT_TYPE_LEVEL);
					for (SpaceSecurity security : list) {
						if (domainIds.contains(security.getEntityId())) {
							contain = true;
							break;
						}
					}
				}
				if (!contain) {
					if (list == null) {
						list = new ArrayList<SpaceSecurity>();
					}
					SpaceSecurity newSecurity = new SpaceSecurity(spaceId,
							SpaceSecurity.SecurityType.used,
							V3xOrgEntity.ORGENT_TYPE_MEMBER, member.getId(), 0);
					list.add(newSecurity);
					fix.setSpaceSecurities(list);
					super.save(newSecurity);
				}
			}
			// 保存配置
			userFixManager.saveOrUpdate(member.getId(), SPACEID, spaceId
					.toString());
		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
		}
	}

	private Long getUserSpaceId(Long memberId) {
		String value = userFixManager.getFixValue(memberId, SPACEID);
		if (Strings.isNotBlank(value)) {
			return Long.parseLong(value);
		}
		return null;
	}

	public boolean canManagerSpace(Long memberId, Long spaceId) {
		if (memberId == null || spaceId == null) {
			return false;
		}

		List<Long> list = this.getCanManagerSpace(memberId);

		// 如果是部门空间，需要判断entityId
		SpaceFix fix = this.getSpace(spaceId);
		if (fix != null && fix.getType() == SpaceType.department.ordinal()) {
			return CollectionUtils.isNotEmpty(list) && list.contains(fix.getEntityId());
		}

		return CollectionUtils.isNotEmpty(list) && list.contains(spaceId);
	}

	/**
	 * 前端删除fragment。
	 * 
	 * @param entityId
	 *            fragment的id 见{@link com.seeyon.v3x.space.domain.Fragment}
	 * @param path
	 *            page.path
	 * @return
	 */
	public String deleteFragment(Long entityId, String path,int index) {
		SpacePage page = this.pageManager.getPage(path);
		Fragment toDelete = this.pageManager.getFragmentById(page, entityId);
		/**
		 * 多频道删除，删除当前频道
		 */
		Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
		String sections = properties.get("sections");
		if(sections!=null){
			String[] sNames = sections.split(",");
			if(sNames.length>1){
				sections = "";
				for(int i=0; i<sNames.length; i++){
					if(i!=index){
						sections = sections + sNames[i]+",";
					}
				}
				sections = sections.substring(0,sections.lastIndexOf(","));
				Map<String,String> newProperties = new HashMap<String,String>();
				Set<String> enties = properties.keySet();
				for(String entry: enties){
					if(entry.equals("sections")){
						newProperties.put("sections", sections);
					}else if(entry.endsWith(":"+(index+1))){
						newProperties.put(entry.replace(":"+(index+1), ":"+index), properties.get(entry));
					}else if(entry.endsWith(":"+(index+2))){
						newProperties.put(entry.replace(":"+(index+2), ":"+(index+1)), properties.get(entry));
					}else if(entry.endsWith(":"+index)){
						
					}else{
						newProperties.put(entry, properties.get(entry));
					}
				}
				this.portletEntityPropertyManager.saveProperties(entityId, newProperties);
			}else{
				List<Fragment> frags = page.getRootFragment().getChildFragments();
				if (page != null && toDelete != null) {
					try {
						Map<String, Map<String, Fragment>> fragments = this.getFragments(path);
						Map<String, Fragment> rowList = fragments.get(toDelete.getLayoutColumn().toString());
						if (rowList != null && !rowList.isEmpty()) {
							int row = toDelete.getLayoutRow();
							for (String rowStr : rowList.keySet()) {
								Fragment fr = rowList.get(rowStr);
								if (!fr.getId().equals(toDelete.getId())
										&& Integer.parseInt(rowStr) > row) {
									Fragment frag = rowList.get(rowStr);
									frag.setLayoutRow(frag.getLayoutRow() - 1);
									this.pageManager.updateFragment(frag);
								}
							}
						}
						fragments.remove(toDelete);
						frags.remove(toDelete);
						this.pageManager.updatePage(page);
						this.pageManager.removeFragment(entityId, path);
						this.portletEntityPropertyManager.deleteProperties(entityId);
					} catch (SpaceException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}else{
			log.error("栏目属性【sectios】为空，EntityId:"+entityId);
		}
		
		return "success";
	}
	
	/**
	 * 缓存删除fragment。
	 * 
	 * @param entityId
	 *            fragment的id 见{@link com.seeyon.v3x.space.domain.Fragment}
	 * @param path
	 *            page.path
	 * @return
	 */
	public void deleteFragment(Long entityId,String spaceId,String editKeyId,Long memberId,int index) {
		/**
		 * 多频道删除，删除当前频道
		 */
		Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
		String sections = properties.get("sections");
		if(sections!=null){
			String[] sNames = sections.split(",");
			if(sNames.length>1){
				sections = "";
				for(int i=0; i<sNames.length; i++){
					if(i!=index){
						sections = sections + sNames[i]+",";
					}
				}
				sections = sections.substring(0,sections.lastIndexOf(","));
				HashMap<String,String> newProperties = new HashMap<String,String>();
				Set<String> enties = properties.keySet();
				for(String entry: enties){
					if(entry.equals("sections")){
						newProperties.put("sections", sections);
					}else if(entry.endsWith(":"+(index+1))){
						newProperties.put(entry.replace(":"+(index+1), ":"+index), properties.get(entry));
					}else if(entry.endsWith(":"+(index+2))){
						newProperties.put(entry.replace(":"+(index+2), ":"+(index+1)), properties.get(entry));
					}else if(entry.endsWith(":"+index)){
						
					}else{
						newProperties.put(entry, properties.get(entry));
					}
				}
				this.portletEntityPropertyManager.addCachePropertys(entityId, newProperties);
			}else{
				List<Fragment> frags = cacheSaveFragment.get(memberId);
				Fragment fragment = null;
				for(Fragment frag : frags){
					if(frag.getId().equals(entityId)){
						fragment = frag;
						break;
					}
				}
				if(fragment!=null){
					int layoutColumn = fragment.getLayoutColumn();
					int layoutRow = fragment.getLayoutRow();
					for(Fragment frag : frags){
						if(frag.getLayoutColumn()==layoutColumn&&frag.getLayoutRow()>layoutRow){
							frag.setLayoutRow(frag.getLayoutRow()-1);
						}
					}
					ArrayList<Fragment> removeFrags = cacheRemoveFragment.get(memberId);
					removeFrags.add(fragment);
					frags.remove(fragment);
				}
			}
		}else{
			log.error("栏目属性【sectios】为空，EntityId:"+entityId);
		}
	}
	
	public boolean isBizConfigPublished(Long bizConfigId, Long userId, Long accountId) {
		try {
			SpaceFix fix = this.getPersonSpace(userId, accountId);
			if(fix != null) {
				String _pagePath = fix.getPagePath();
				Map<String,Map<String,Fragment>> fragments = this.getFragments(_pagePath);
				String ids = portletEntityPropertyManager.getExistedFormBizConfigSectionIds(fragments);
				return Strings.isNotBlank(ids) && ids.indexOf(bizConfigId.toString()) != -1;
			}
		}
		catch(SpaceException e) {
			logger.error("获取业务配置是否已发布到首页空间信息出现异常[业务配置ID=" + bizConfigId + ", 用户ID=" + userId + "]：", e);
		}
		return false;
	}

	public List<SpaceModel> getSpacesOfSection(String sectionId, Long memberId, Long accountId) throws SpaceException {
		List<SpaceModel> result = new ArrayList<SpaceModel>();
		
		Map<Constants.SpaceType, List<SpaceModel>> spaces = this.getAccessSpace(memberId, accountId);
		for (Iterator<Map.Entry<Constants.SpaceType, List<SpaceModel>>> iterator = spaces.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Constants.SpaceType, List<SpaceModel>> entry = iterator.next();
			List<SpaceModel> spaceModels = entry.getValue();
			
			for (SpaceModel spaceModel : spaceModels) {
				List<Fragment> fragments = this.pageManager.getPage(spaceModel.getSpacePath()).getRootFragment().getChildFragments();
				
				for (Fragment fragment : fragments) {
					Map<String, String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
					String sections = props.get("sections");
					if(Strings.isNotBlank(sections)){
						String[] _sections = sections.split(",");
						for(int i = 0; i < _sections.length; i++) {
							if(sectionId.equalsIgnoreCase(_sections[i])) {
								result.add(spaceModel);
							}
						}
					}
				}
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpaceFix> getManagmentSpaceListByUserId(String userid) {
		StringBuffer sbf= new StringBuffer("select sf from SpaceFix sf,SpaceSecurity ss ");
		sbf.append(" where ss.spaceFixId=sf.id and ss.entityId=? and ss.securityType=? ");
		List<SpaceFix> list= super.find(sbf.toString(), new Long(userid),SpaceSecurity.SecurityType.manager.ordinal());
		return list;
	}

	public boolean addPortlet(String pagePath,String[] sectionIds,String[] sectionNames,String[] singleBoards,String[] entityIds,String[] ordinals,String editKeyId,Long memberId,String decoration){
		SpacePage page = pageManager.getPage(pagePath);
		//缓存数据
		long parentId = page.getRootFragment().getPageId();
		
		ArrayList<Fragment> fragments = cacheSaveFragment.get(memberId);
		
		ArrayList<Fragment> removeList = cacheRemoveFragment.get(memberId);
		int column = 0;
		if(Strings.isNotBlank(decoration)){
			if(decoration.equals("D2-M_3-7_T")){
				column = 0;
			}else if(decoration.equals("D2-M_5-5_T")){
				column = 1;
			}else if(decoration.equals("D2-M_7-3_T")){
				column = 1;
			}else if(decoration.equals("D3-M_3-4-3_N")){
				column = 1;
			}else if(decoration.equals("D3-M_3-4-3_T")){
				column = 0;
			}else if(decoration.equals("D3-M_4-4-2_T")){
				column = 1;
			}
		}
		if(entityIds!=null&&entityIds.length>0&&sectionIds!=null&&sectionIds.length>0){
			//部分栏目新增
			List<String> entityIdList = new ArrayList<String>();
			List<String> newSectionList = new ArrayList<String>();
			for(int i=0; i<entityIds.length; i++){
				String entityId = entityIds[i];
				if(Strings.isNotBlank(entityId)){
					entityIdList.add(entityId);
				}else if(Strings.isNotBlank(sectionIds[i])){
					newSectionList.add(sectionIds[i]);
				}
			}
			List<Fragment> removeFragments = new ArrayList<Fragment>();
			for(Fragment fragment : fragments){
				Long id = fragment.getId();
				if(!entityIdList.contains(String.valueOf(id))){
					removeFragments.add(fragment);
				}
			}
			for(Fragment fragment: removeFragments){
				removeList.add(fragment);
				fragments.remove(fragment);
			}
			cacheRemoveFragment.put(memberId, removeList);
			
			Collections.sort(fragments, new CompareSortFragment());
			//重新排列fragment坐标
			int rowNum = 0;
			int currentColumn = 0;
			int currentRow = 0;
			for(int j=0; j<fragments.size();j++){
				Fragment fg = fragments.get(j);
				if(fg.getLayoutColumn() == currentColumn){
					fg.setLayoutRow(currentRow++);
				}else{
					currentColumn = fg.getLayoutColumn();
					currentRow = 0;
					fg.setLayoutRow(currentRow++);
				}
				if(fg.getLayoutColumn()==column){
					rowNum++;
				}
			}
			
			for(int i=0; i<entityIds.length; i++){
				String entityId = entityIds[i];
				if(Strings.isBlank(entityId)&&Strings.isNotBlank(sectionIds[i])){
					Fragment fragment = new Fragment();
					fragment.setIdIfNew();
					fragment.setType(Fragment.Type.portlet);
					fragment.setLayoutColumn(column);
					fragment.setLayoutRow(rowNum++);
					fragment.setName("seeyon::sectionPortlet");
					fragment.setParentId(parentId);
					fragment.setState(String.valueOf(Fragment.State.newFragment.ordinal()));
					fragments.add(fragment);
					
					HashMap<String,String> map = new HashMap<String,String>();
					
					map.put(PortletEntityProperty.PropertyName.sections.name(), sectionIds[i]);
					if(sectionIds[i].equals("noticeSection")){
						Long uuid = UUIDLong.absLongUUID();
						map.put("singleBoardId:0",String.valueOf(uuid));
					}
					if(sectionNames!=null&&sectionNames.length>0){
						map.put("columnsName:0",sectionNames[i]);
					}
					if(singleBoards!=null&&singleBoards.length>0){
						if(Strings.isNotBlank(singleBoards[i])){
							map.put("singleBoardId:0",singleBoards[i]);
						}
					}
					
					// TODO:将pops存入缓存
					portletEntityPropertyManager.addCachePropertys(fragment.getId(), map);
				}
			}
		}else{
			//全部栏目新增
			if(sectionIds!=null&&sectionIds.length>0){
				for(int i=0;i<sectionIds.length;i++){
					Fragment fragment = new Fragment();
					fragment.setIdIfNew();
					fragment.setType(Fragment.Type.portlet);
					fragment.setLayoutColumn(column);
					fragment.setLayoutRow(i);
					fragment.setName("seeyon::sectionPortlet");
					fragment.setParentId(parentId);
					fragment.setState(String.valueOf(Fragment.State.newFragment.ordinal()));
					fragments.add(fragment);
					
					HashMap<String,String> map = new HashMap<String,String>();
					
					map.put(PortletEntityProperty.PropertyName.sections.name(), sectionIds[i]);
					if(sectionNames!=null&&sectionNames.length>0){
						map.put("columnsName:0",sectionNames[i]);
					}
					if(singleBoards!=null&&singleBoards.length>0){
						if(Strings.isNotBlank(singleBoards[i])){
							map.put("singleBoardId:0",singleBoards[i]);
						}
					}
					portletEntityPropertyManager.addCachePropertys(fragment.getId(), map);
				}
			}else{
				for(Fragment fragment : fragments){
					if(!removeList.contains(fragment)){
						removeList.add(fragment);
					}
				}
				cacheRemoveFragment.put(memberId, removeList);
				fragments.clear();
			}
		}
		return true;
	}
	
	@Override
	public boolean updateLayoutIndex(String pagePath,List<Fragment> frags,String editKeyId,Long memberId) {
		//缓存数据
		List<Fragment> fragments = cacheSaveFragment.get(memberId);
		for(Fragment f : frags){
			for(Fragment frag : fragments){
				if(frag.getId().equals(f.getId())){
					frag.setLayoutColumn(f.getLayoutColumn());
					frag.setLayoutRow(f.getLayoutRow());
				}
			}
		}
		return true;
	}
	@Override
	public void updatePage(String pagePath, String newLayout){
		SpacePage page = pageManager.getPage(pagePath);
		page.setDefaultDecorator(newLayout, Fragment.Type.layout);
		this.pageManager.updatePage(page);	
	}
	@Override
	public void updateSpaceSortForPersonalCustom(String spaceId,
			List<V3xOrgMember> memberIds, Long accountId,boolean isDefault) throws BusinessException {
		Long id = Long.valueOf(spaceId);
		SpaceFix spaceFix = this.getSpace(id);
		if(spaceFix==null){
			return;
		}
		if(CollectionUtils.isEmpty(memberIds)){
			return;
		}
		if(spaceFix.getState() == SpaceState.invalidation.ordinal()){
			return;
		}
		
		Map<Long,List<SpaceSort>> mapSorts =  getSpaceSortByMemberId(memberIds,accountId);
		
		SpaceType type = Constants.parseDefaultSpaceType(EnumUtil.getEnumByOrdinal(SpaceType.class, spaceFix.getType()));
		List<SpaceSort> saveList = new ArrayList<SpaceSort>();
		List<SpaceSort> updateList = new ArrayList<SpaceSort>();
		List<SpaceSort> removeList = new ArrayList<SpaceSort>();
		//覆盖个性化
		for(V3xOrgMember member:memberIds){
			long memberId = member.getId();
			List<SpaceSort> spaceSorts = new ArrayList<SpaceSort>();
			if(mapSorts!=null&&mapSorts.size()>0){
				spaceSorts = mapSorts.get(memberId);
			}
			if(CollectionUtils.isEmpty(spaceSorts)){
				SpaceSort sort = new SpaceSort();
				sort.setIdIfNew();
				sort.setAccountId(accountId);
				sort.setIsDeleted(false);
				sort.setSort(1);
				sort.setMemberId(memberId);
				sort.setSpacePath(spaceId);
				sort.setType(type.ordinal());
				saveList.add(sort);
			}else{
				boolean putFirstSort = false;
				boolean isMatched = false;
				for(SpaceSort sort: spaceSorts){
					String path = sort.getSpacePath();
					Integer sortType = sort.getType();
					Integer index = sort.getSort();
					if(index.equals(1)){
						if(path.equals(spaceId)){
							isMatched = true;
							//默认空间
							break;
						}else if(type.equals(SpaceType.leader)&&sortType.equals(SpaceType.leader.ordinal())){
							isMatched = true;
							break;
						}else if(type.equals(SpaceType.personal_custom)&&sortType.equals(SpaceType.personal_custom.ordinal())){
								SpaceFix fix = this.getSpace(Long.valueOf(path));
								Long parentId = fix.getParentId();
								if(parentId!=null&&parentId.equals(id)){
									isMatched = true;
									break;
								}
						}
						if(sort.getSort().equals(1)&&!putFirstSort){
							sort.setSort(spaceSorts.size()+1);
							sort.setIsDeleted(true);
							updateList.add(sort);
							putFirstSort = true;
						}
					}else{
						if(path.equals(spaceId)){
							removeList.add(sort);
							break;
						}
						if(type.equals(SpaceType.leader)&&sortType.equals(SpaceType.leader.ordinal())){
							removeList.add(sort);
							break;
						}
						if(type.equals(SpaceType.personal_custom)&&sortType.equals(SpaceType.personal_custom.ordinal())){
							SpaceFix fix = this.getSpace(Long.valueOf(path));
							Long parentId = fix.getParentId();
							if(parentId!=null&&parentId.equals(id)){
								removeList.add(sort);
								break;
							}
						}
					}
				}
				if(!isMatched){
					SpaceSort s = new SpaceSort();
					s.setIdIfNew();
					s.setAccountId(accountId);
					s.setIsDeleted(false);
					s.setSort(1);
					s.setMemberId(memberId);
					s.setSpacePath(spaceId);
					s.setType(type.ordinal());
					saveList.add(s);
				}
			}
		}	
		if(CollectionUtils.isNotEmpty(removeList)){
			for(SpaceSort sort : removeList){
				super.remove(sort);
			}
		}
		if(CollectionUtils.isNotEmpty(updateList)){
			super.updatePatchAll(updateList);
		}
		if(CollectionUtils.isNotEmpty(saveList)){
			super.savePatchAll(saveList);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<Long,List<SpaceSort>> getSpaceSortByMemberId(List<V3xOrgMember> members,Long accountId){
		if(CollectionUtils.isEmpty(members)){
			return null;
		}
		List<Long> memberIds = new ArrayList<Long>();
		for(V3xOrgMember member:members){
			memberIds.add(member.getId());
		}
		String hql = " from "+SpaceSort.class.getName()+" where memberId in (:memberIds) and accountId = :accountId order by memberId,sort";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("memberIds", memberIds);
		paramMap.put("accountId", accountId);
		
		List<SpaceSort> spaceSorts =  super.find(hql,-1,-1, paramMap);
		if(CollectionUtils.isEmpty(spaceSorts)){
			return null;
		}
		Map<Long,List<SpaceSort>> map = new HashMap<Long,List<SpaceSort>>();
		Long putMemberId = null;
		List<SpaceSort> sorts = null;
		for(SpaceSort spaceSort : spaceSorts){
			Long currentMemberId = spaceSort.getMemberId();
			if(putMemberId==null){
				putMemberId = currentMemberId;
				sorts = new ArrayList<SpaceSort>();
				map.put(putMemberId, sorts);
				sorts.add(spaceSort);
			}else if(!putMemberId.equals(currentMemberId)){
				putMemberId = currentMemberId;
				sorts = new ArrayList<SpaceSort>();
				map.put(putMemberId, sorts);
				sorts.add(spaceSort);
			}else{
				sorts.add(spaceSort);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateSpaceSortBySecurity(Long spaceId, String[][] securities) {
		if (securities != null && securities.length > 0) {
			String hql = "select ss.id,ss.memberId from "+SpaceSort.class.getName()+" as ss where ss.spacePath =?";
			List<Object[]> list = super.find(hql,-1,-1,null, spaceId.toString());
			if (list != null && !list.isEmpty()) {
				List<String> memberList = new ArrayList<String>();
				for (int i = 0; i < securities.length; i++) {
					String[] security = securities[i];
					if (security[0].equals("Department")) {
						List<V3xOrgMember> accountMembers;
						try {
							accountMembers = orgManager.getMembersByDepartment(
									Long.valueOf(security[1]), false);
							if (accountMembers != null
									&& accountMembers.size() > 0) {
								for (V3xOrgMember member : accountMembers) {
									memberList.add(member.getId().toString());
								}
							}
						} catch (NumberFormatException e) {
							log.error(e.getMessage(), e);
							e.printStackTrace();
						} catch (BusinessException e) {
							log.error(e.getMessage(), e);
							e.printStackTrace();
						}
					} else if (security[0].equals("Member")) {
						memberList.add(security[1]);
					}
				}
				for (Object[] obj : list) {
					String memberId = String.valueOf(obj[1]);
					if (!memberList.contains(memberId)) {
						super.delete(SpaceSort.class, Long.valueOf(obj[0].toString()));
					}
				}
			}
		} else {
			String[][] str = {{"spacePath",spaceId.toString()}};
			super.delete(SpaceSort.class,str);
		}
	}
	@Override
	public List<String[]> getCanAccessSpace(Long memberId, Long accountId,
			Locale userLocale, boolean isDefault,
			Map<SpaceType, List<SpaceModel>> accessSpaces)
			throws SpaceException {
		Set<String> queryResult = new HashSet<String>();
		List<String[]> resultList = new ArrayList<String[]>();
		/**
		 * 获取该memberId所有可以访问的空间
		 */
		if (accessSpaces == null) {
			accessSpaces = this.getAccessSpace(memberId, accountId);
		}
		List<SpaceModel> personalPaths = accessSpaces.get(SpaceType.personal);
		List<SpaceModel> leaderPaths = accessSpaces.get(SpaceType.leader);
		List<SpaceModel> outPaths = accessSpaces.get(SpaceType.outer);
		List<SpaceModel> personalCustom = accessSpaces
				.get(SpaceType.personal_custom);

		List<SpaceModel> departmentPaths = accessSpaces
				.get(SpaceType.department);
		List<SpaceModel> corporationPaths = accessSpaces
				.get(SpaceType.corporation);
		List<SpaceModel> groupPaths = accessSpaces.get(SpaceType.group);
		List<SpaceModel> customPaths = accessSpaces.get(SpaceType.custom);
		List<SpaceModel> publicCustom = accessSpaces.get(SpaceType.public_custom);
		List<SpaceModel> publicCustomGroup = accessSpaces.get(SpaceType.public_custom_group);

		/**
		 * 遍历所有空间，组装数组作为备选空间列表的数据
		 */
		//个人空间
		if (personalPaths != null && !personalPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.personal.ordinal());
			for (SpaceModel model : personalPaths) {
				if (model.getId() != null && !queryResult.contains(String.valueOf(model.getId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		//领导空间
		if (leaderPaths != null && !leaderPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.leader.ordinal());
			for (SpaceModel model : leaderPaths) {
				if (model.getId() != null && !queryResult.contains(String.valueOf(model.getId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		//外部人员空间
		if (outPaths != null && !outPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.outer.ordinal());
			for (SpaceModel model : outPaths) {
				if (model.getId() != null && !queryResult.contains(String.valueOf(model.getId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		//单位自定义空间
		if (personalCustom != null && !personalCustom.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.personal_custom.ordinal());
			for (SpaceModel model : personalCustom) {
				if (model.getId() != null && !queryResult.contains(String.valueOf(model.getId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		//部门空间---所有的部门空间全部给显示。
		if (departmentPaths != null && !departmentPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.department.ordinal());
			for (SpaceModel model : departmentPaths) {
				if (model.getEntityId() != null && !queryResult.contains(String.valueOf(model.getEntityId()))
						&& !model.getState().equals( Constants.SpaceState.invalidation)) {
					String[] s = { typeStr, String.valueOf(model.getEntityId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getEntityId()));
				}
			}
		}
		//单位空间
		if (!queryResult.contains("2") && corporationPaths != null && !corporationPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.corporation.ordinal());
			String[] s = { typeStr, typeStr, corporationPaths.get(0).getSpaceName(), "false" };
			resultList.add(s);
		}
		//集团空间
		if (!queryResult.contains("3") && groupPaths != null && !groupPaths.isEmpty()) {
			String typeStr = String.valueOf(SpaceType.group.ordinal());
			String[] s = { typeStr, typeStr, groupPaths.get(0).getSpaceName(), "false" };
			resultList.add(s);
		}
		if (customPaths != null && !customPaths.isEmpty()) {
			int type = SpaceType.custom.ordinal();
			for (SpaceModel model : customPaths) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		if (publicCustom != null && !publicCustom.isEmpty()) {
			int type = SpaceType.public_custom.ordinal();
			for (SpaceModel model : publicCustom) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		if (publicCustomGroup != null && !publicCustomGroup.isEmpty()) {
			int type = SpaceType.public_custom_group.ordinal();
			for (SpaceModel model : publicCustomGroup) {
				if (!queryResult.contains(String.valueOf(model.getId())) && !model.getState().equals(Constants.SpaceState.invalidation)) {
					String[] s = { String.valueOf(type), String.valueOf(model.getId()), model.getSpaceName(), "false" };
					resultList.add(s);
					queryResult.add(String.valueOf(model.getId()));
				}
			}
		}
		// 集成第三方系统
		List<ThirdpartySpace> thirdpartySpaces = ThirdpartySpaceManager.getInstance().getAccessSpaces(orgManager, memberId);
		if (thirdpartySpaces != null && !thirdpartySpaces.isEmpty()) {
			int type = Constants.SpaceType.thirdparty.ordinal();
			for (ThirdpartySpace thirdParty : thirdpartySpaces) {
					String[] s = { String.valueOf(type), thirdParty.getId(),thirdParty.getNameOfResouceBundle(userLocale),"false" };
					resultList.add(s);
					queryResult.add(thirdParty.getId());
			}
		}
		
		return resultList;
	}

	@Override
	public void updateSectionsToFragment(Long entityId, String sectionIds,
			String[] sectionNames,String[] singleBoards,String[] entityIds,String[] ordinals,Long memberId) {
		if(entityId!=null){
			if(Strings.isBlank(sectionIds)){
				List<Fragment> saveFragments = cacheSaveFragment.get(memberId);
				Fragment removeFragment = new Fragment();
				for(Fragment frag : saveFragments){
					if(frag.getId().equals(entityId)){
						removeFragment = frag;
					}
				}
				if(removeFragment!=null){
					int layoutColumn = removeFragment.getLayoutColumn();
					int layoutRow = removeFragment.getLayoutRow();
					for(Fragment frag : saveFragments){
						if(frag.getLayoutColumn()==layoutColumn&&frag.getLayoutRow()>layoutRow){
							frag.setLayoutRow(frag.getLayoutRow()-1);
						}
					}
					saveFragments.remove(removeFragment);
					List<Fragment> removeFragments = cacheRemoveFragment.get(memberId);
					removeFragments.add(removeFragment);
				}
			}else{
				HashMap<String,String> map = new HashMap<String,String>();
				map.put(PortletEntityProperty.PropertyName.sections.name(), sectionIds);
				Map<String,String> props = portletEntityPropertyManager.getPropertys(entityId);
				String[] sections = sectionIds.split(",");
				if(ordinals!=null&&ordinals.length>0){
					for(int i=0; i<sections.length;i++){
						String ordinal = ordinals[i];
						if(Strings.isNotBlank(ordinal)){
							for(String key:props.keySet()){
								String[] _keys = key.split(":");
								if(_keys.length==2&&_keys[1].equals(ordinal)){
									map.put(_keys[0]+":"+i, props.get(key));
								}
							}
						}else{
							if(sections[i].equals("noticeSection")){
								Long uuid = UUIDLong.absLongUUID();
								map.put(String.valueOf("singleBoardId:"+i),String.valueOf(uuid));
							}
							if(sectionNames!=null&&sectionNames.length>0&&sectionNames[i]!=null&&!sectionNames[i].isEmpty()){
								map.put(String.valueOf("columnsName:"+i),sectionNames[i]);
							}
							if(singleBoards!=null&&singleBoards.length>0&&singleBoards[i]!=null&&!singleBoards[i].isEmpty()){
								map.put(String.valueOf("singleBoardId:"+i),singleBoards[i]);
							}
						}
					}
				}else{
					if(sectionNames!=null&&sectionNames.length>0){
						for(int i=0;i<sectionNames.length;i++){
							if(sectionNames[i]!=null&&!sectionNames[i].isEmpty()){
								map.put(String.valueOf("columnsName:"+i),sectionNames[i]);
							}
						}
					}
					if(singleBoards!=null&&singleBoards.length>0){
						for(int i=0;i<singleBoards.length;i++){
							if(singleBoards[i]!=null&&!singleBoards[i].isEmpty()){
								map.put(String.valueOf("singleBoardId:"+i),singleBoards[i]);
							}
						}
					}
				}
				portletEntityPropertyManager.addCachePropertys(entityId, map);
			}
		}
	}

	@Override
	public String toDefaultPersonalSpace(Long memberId, Long accountId,
			Long spaceId,String spaceType) throws SpaceException {
		SpaceFix spaceFix = this.getSpace(spaceId);
		boolean isChanged = false;
		SpaceFix newSpaceFix = null;
		//恢复默认
		if(spaceType.equals(SpaceType.personal.name())){
			newSpaceFix = this.createDefaultPersonalSpaceFix(accountId);
			isChanged = true;
		}else if(spaceType.equals(SpaceType.leader.name())){
			newSpaceFix = this.createDefaultLeaderSpace(accountId);
			isChanged = true;
		}else if(spaceType.equals(SpaceType.outer.name())){
			newSpaceFix = this.createDefaultOutSpace(accountId);
			isChanged = true;
		}else if(spaceType.equals(SpaceType.personal_custom.name())){
			Long parentId = spaceFix.getParentId();
			if(parentId!=null){
				newSpaceFix = this.getSpace(parentId);
				if(newSpaceFix!=null){
					isChanged = true;
				}
			}
		}
		if(isChanged){
			SpacePage page = pageManager.getPage(newSpaceFix.getPagePath());
			String decoration = page.getDefaultLayoutDecorator();
			List<Fragment> frags = page.getRootFragment().getChildFragments();
			//缓存数据
			ArrayList<Fragment> fragments;
			if(CollectionUtils.isNotEmpty(frags)){
				fragments = (ArrayList<Fragment>) this.copyFragmentCache(frags);
			}else{
				fragments = new ArrayList<Fragment>();
			}
			cacheSaveFragment.put(memberId, fragments);
			cacheRemoveFragment.put(memberId, new ArrayList<Fragment>());
			return decoration;
		}else{
			return null;
		}
	}
	@Override
	public String toDefaultSpace(Long memberId,Long spaceId,String pagePath) throws SpaceException {
		SpacePage page = pageManager.getPage(pagePath);
		String decoration = page.getDefaultLayoutDecorator();
		List<Fragment> frags = page.getRootFragment().getChildFragments();
		//缓存数据
		ArrayList<Fragment> fragments;
		if(CollectionUtils.isNotEmpty(frags)){
			fragments = (ArrayList<Fragment>)this.copyFragmentCache(frags);
		}else{
			fragments = new ArrayList<Fragment>();
		}
		cacheSaveFragment.put(memberId, fragments);
		return decoration;
	}
	public void addEditKeyCache(Long editKeyId,String pagePath,Long memberId){
		if(!editSpaceKeyMap.contains(memberId)){
			HashMap<String,Long> map = new HashMap<String,Long>();
			map.put("editKeyId", editKeyId);
			editSpaceKeyMap.put(memberId,map);
			SpacePage page = pageManager.getPage(pagePath);
			List<Fragment> frags = null;
			if(page!=null){
				frags = page.getRootFragment().getChildFragments();
			}
			//缓存数据
			ArrayList<Fragment> fragments;
			if(CollectionUtils.isNotEmpty(frags)){
				fragments = (ArrayList<Fragment>)this.copyFragmentCache(frags);
			}else{
				fragments = new ArrayList<Fragment>();
			}
			cacheSaveFragment.put(memberId, fragments);
			cacheRemoveFragment.put(memberId, new ArrayList<Fragment>());
		}else{
			HashMap<String,Long> map = editSpaceKeyMap.get(memberId);
			Long oldKey = map.get("editKeyId");
			if(!oldKey.equals(editKeyId)){
				//清除id为oldSpaceId的空间的fragment缓存,更新spaceId和keyId
				cacheSaveFragment.remove(memberId);
				cacheRemoveFragment.remove(memberId);
				SpacePage page = pageManager.getPage(pagePath);
				List<Fragment> frags = null;
				if(page!=null){
					frags = page.getRootFragment().getChildFragments();
				}
				//缓存数据
				ArrayList<Fragment> fragments;
				if(CollectionUtils.isNotEmpty(frags)){
					fragments = (ArrayList<Fragment>)this.copyFragmentCache(frags);
				}else{
					fragments = new ArrayList<Fragment>();
				}
				cacheSaveFragment.put(memberId, fragments);
				cacheRemoveFragment.put(memberId, new ArrayList<Fragment>());
				map.put("editKeyId", editKeyId);
				editSpaceKeyMap.put(memberId,map);
			}
		}
	}
	public void removeEditKeyCache(Long memberId){
		cacheRemoveFragment.remove(memberId);
		if(editSpaceKeyMap.contains(memberId)){
			ArrayList<Fragment> fragments = cacheSaveFragment.get(memberId);
			if(CollectionUtils.isNotEmpty(fragments)){
				for(Fragment frag : fragments){
					Long entityId = frag.getId();
					this.portletEntityPropertyManager.removeCachePropertys(entityId);
				}
			}
			cacheSaveFragment.remove(memberId);
		}
		editSpaceKeyMap.remove(memberId);
	}
	private boolean isCurrentEdit(Long editKeyId,Long memberId){
		if(!editSpaceKeyMap.contains(memberId)){
			return false;
		}else{
			HashMap<String,Long> map = editSpaceKeyMap.get(memberId);
			Long oldKey = map.get("editKeyId");
			if(!oldKey.equals(editKeyId)){
				return false;
			}else{
				return true;
			}
		}
	}

	@Override
	public void updateProperty(Long entityId, Map<String, String> properties,
			String tabIndex, Long editKeyId, Long memberId) throws CloneNotSupportedException {
		Map<String,String> portletProperties = this.portletEntityPropertyManager.getPropertys(entityId);
		Set<String> keys = portletProperties.keySet();
		List<String> removeKeys = new ArrayList<String>();
		for(String key : keys){
			if(key.endsWith(":"+tabIndex)){
				removeKeys.add(key);
			}
		}
		for(String key : removeKeys){
			portletProperties.remove(key);
		}
		Set<Map.Entry<String, String>> entitis = properties.entrySet();
		for (Map.Entry<String, String> entry : entitis) {
			if(java.util.regex.Pattern.matches(PortletEntityProperty.PropertyName_No_Save_Pattern, entry.getKey())) {
				continue;
			}
			portletProperties.put(entry.getKey(), entry.getValue());
		}
		portletEntityPropertyManager.addCachePropertys(entityId, (HashMap<String, String>)portletProperties);
	}

	@Override
	public String updateSpaceByCache(Long editKeyId, Long memberId, Long spaceId,String decoration,String toDefault) throws SpaceException {
		boolean isCurrentEdit = isCurrentEdit(editKeyId, memberId);
		SpaceFix spaceFix = this.getSpace(spaceId);
		if(isCurrentEdit){
			editSpaceKeyMap.remove(memberId);
			/**
			 * TODO:
			 * 1、cacheSave缓存Fragment替换原有Fragment
			 * 2、更新properties，循环removeFragments，删除properties，循环saveFragments，保存properties
			 * 3、更新rootFragment，保存布局样式decoration
			 * 4、如果存在页面个性化，先创建个性化页面；如果存在页面还原则只走页面还原逻辑
			 * 5、清空当前member编辑缓存，丢失editKeyId
			 */
			Long accountId = spaceFix.getAccountId();
			String pagePath = spaceFix.getPagePath();
			int spaceType = spaceFix.getType();
			
			// 恢复默认逻辑
			if(toDefault.equals("toDefault")){
				boolean isChanged = false;
				SpaceFix newSpaceFix = null;
				//恢复默认
				if(spaceType==SpaceType.personal.ordinal()){
					newSpaceFix = this.createDefaultPersonalSpaceFix(accountId);
					isChanged = true;
				}else if(spaceType==SpaceType.leader.ordinal()){
					newSpaceFix = this.createDefaultLeaderSpace(accountId);
					isChanged = true;
				}else if(spaceType==SpaceType.outer.ordinal()){
					newSpaceFix = this.createDefaultOutSpace(accountId);
					isChanged = true;
				}else if(spaceType==SpaceType.personal_custom.ordinal()){
					Long parentId = spaceFix.getParentId();
					if(parentId!=null){
						newSpaceFix = this.getSpace(parentId);
						if(newSpaceFix!=null){
							isChanged = true;
						}
					}
				}
				//更新排序表，删除旧空间
				if(isChanged){
					// 更新排序表
					SpaceSort sort = spaceSortManager.getSpaceSort(memberId, spaceId);
					if(sort!=null){
						sort.setSpacePath(newSpaceFix.getId().toString());
						spaceSortManager.updateSpaceSort(sort);
					}
					// 更新userFix表
					String defaultSpaceId = userFixManager.getFixValue(memberId, "spaceId");
					if(Strings.isNotBlank(defaultSpaceId)&&Long.valueOf(defaultSpaceId)==spaceId){
						userFixManager.saveOrUpdate(memberId, "spaceId", newSpaceFix.getId().toString());
					}
					//删除旧空间授权信息
					this.deleteSpaceAllSecurity(spaceId);
					//删除旧空间的page
					try {
						this.removePage(spaceFix.getPagePath());
					} catch (Exception e) {
						log.error("恢复默认失败：", e);
					}
					//删除旧空间
					this.delete(spaceFix);
					allCacheSpace.remove(spaceFix.getId());
					return newSpaceFix.getPagePath();
				}
			}else{
				// 编辑逻辑
				if(spaceType==SpaceType.Default_personal.ordinal()||spaceType==SpaceType.default_leader.ordinal()||spaceType==SpaceType.Default_out_personal.ordinal()||spaceType==SpaceType.Default_personal_custom.ordinal()){
					//个性化逻辑
					spaceFix = this.createPersonalDefineSpace(memberId, accountId, spaceId);
					pagePath = spaceFix.getPagePath();
				}
					
				SpacePage page = pageManager.getPage(pagePath);
				page.setDefaultLayoutDecorator(decoration);
				pageManager.updatePage(page);
				Fragment root = page.getRootFragment();
				
				String rootName = root.getName();
				String layoutName = this.getLayoutName(decoration);
				if(layoutName!=null&&rootName!=null){
					if(rootName == null||!rootName.equals(layoutName)){
						root.setName(layoutName);
						pageManager.updateFragment(root);
					}
				}
				//删除复制的fragment
				if(root != null){
					List<Fragment> fragments = root.getChildFragments();
					if(fragments != null && !fragments.isEmpty()){
						for (Fragment fragment : fragments) {
							this.portletEntityPropertyManager.deleteProperties(fragment.getId());
							this.delete(Fragment.class, fragment.getId());
						}
						if(CollectionUtils.isNotEmpty(root.getChildFragments())){
							root.getChildFragments().clear();
						}
					}
				}
				
				//复制缓存中的待保存fragment到新的page页
				List<Fragment> saveFragments = cacheSaveFragment.get(memberId);
				for(Fragment saveFrag : saveFragments){
					Long entityId = saveFrag.getId();
					saveFrag.setParentId(root.getId());
					Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
					this.portletEntityPropertyManager.save(entityId, properties);
					this.save(saveFrag);
				}
				
				root.getChildFragments().addAll(saveFragments);
				this.pageManager.updatePageByCache(pagePath);
				cacheSaveFragment.remove(memberId);
				return pagePath;
			}
		}
		return spaceFix.getPagePath();
	}
	@Override
	public boolean updateSpace(Long editKeyId, Long memberId,Long spaceId, String decoration) {
		boolean isCurrentEdit = isCurrentEdit(editKeyId, memberId);
		SpaceFix spaceFix = this.getSpace(spaceId);
		if(isCurrentEdit){
			editSpaceKeyMap.remove(memberId);
			String pagePath = spaceFix.getPagePath();
			SpacePage page = pageManager.getPage(pagePath);
			page.setDefaultLayoutDecorator(decoration);
			pageManager.updatePage(page);
			Fragment root = page.getRootFragment();
			
			String rootName = root.getName();
			String layoutName = this.getLayoutName(decoration);
			if(layoutName!=null&&rootName!=null){
				if(rootName == null||!rootName.equals(layoutName)){
					root.setName(layoutName);
					pageManager.updateFragment(root);
				}
			}
			
			//删除复制的fragment
			if(root != null){
				List<Fragment> fragments = root.getChildFragments();
				if(fragments != null && !fragments.isEmpty()){
					for (Fragment fragment : fragments) {
						this.portletEntityPropertyManager.deleteProperties(fragment.getId());
						this.delete(Fragment.class, fragment.getId());
					}
					root.getChildFragments().clear();
				}
			}
			
			//复制缓存中的待保存fragment到新的page页
			List<Fragment> saveFragments = cacheSaveFragment.get(memberId);
			for(Fragment saveFrag : saveFragments){
				Long entityId = saveFrag.getId();
				saveFrag.setParentId(root.getId());
				Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
				this.portletEntityPropertyManager.save(entityId, properties);
				this.save(saveFrag);
			}
			
			root.getChildFragments().addAll(saveFragments);
			this.pageManager.updatePageByCache(pagePath);
			cacheSaveFragment.remove(memberId);
		}
		return true;
	}
	private String getLayoutName(String decoration){
		if(decoration.startsWith("D1")){
			return "jetspeed-layouts::VelocityOneColumn";
		}else if(decoration.startsWith("D2")){
			return "jetspeed-layouts::VelocityTwoColumns";
		}else if(decoration.startsWith("D3")){
			return "jetspeed-layouts::VelocityThreeColumns";
		}else{
			return null;
		}
	}
	public boolean IsPublishedFormBizSection(String singleBoardId,String pagePath){
		if(Strings.isNotBlank(singleBoardId)&&Strings.isNotBlank(pagePath)){
			SpacePage page = pageManager.getPage(pagePath);
			List<Fragment> fragments = page.getRootFragment().getChildFragments();
			if(CollectionUtils.isNotEmpty(fragments)){
				for(Fragment fragment : fragments){
					Long entityId = fragment.getId();
					Map<String,String> properties = this.portletEntityPropertyManager.getPropertys(entityId);
					if(properties!=null&&properties.size()>0){
						String sectionName = properties.get(PortletEntityProperty.PropertyName.sections.name());
						
						if(Strings.isNotBlank(sectionName)){
							String[] sections = sectionName.split(",");
							for(int i=0; i<sections.length; i++){
								if(sections[i].equals("singleBoardFormBizConfigSection")){
									String value = properties.get("singleBoardId:"+i);
									if(Strings.isNotBlank(value)&&value.equals(singleBoardId)){
										return true;
									}
								}
							}
						}
					}
				}
			}
			return false;
		}else{
			return false;
		}
	}

	@Override
	public List<Fragment> copyFragmentCache(List<Fragment> fragments) {
		if(CollectionUtils.isNotEmpty(fragments)){
			List<Fragment> list = new ArrayList<Fragment>();
			for (Fragment fragment : fragments) {
				if(!SectionPortletFunction.isAllowedUserUsed(fragment)){
					continue;
				}
				Fragment newFrag = new Fragment(fragment);
				newFrag.setIdIfNew();
				newFrag.setParentId(fragment.getPageId());
				Map<String,String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
				portletEntityPropertyManager.addCachePropertys(newFrag.getId(), (HashMap<String,String>)props);
				list.add(newFrag);
			}
			return list;
		}else{
			return null;
		}
	}
	public Fragment getCacheFragment(Long editKeyId,Long memberId,int x,int y){
		boolean isCurrentEdit = this.isCurrentEdit(editKeyId, memberId);
		
		if(isCurrentEdit){
			List<Fragment> fragments = cacheSaveFragment.get(memberId);
			if(CollectionUtils.isNotEmpty(fragments)){
				for (Fragment fragment : fragments) {
					if(fragment.getLayoutColumn()==x&&fragment.getLayoutRow()==y){
						return fragment;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void updateSpaceFix(Long spaceId, int state) {
		if(spaceId!=null){
			SpaceFix fix = this.getSpace(spaceId);
			if(fix!=null){
				fix.setState(state);
				super.update(fix);
				putToMemory(EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, fix
						.getType().intValue()), fix, fix.getAccountId());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteCustomedSpace(Long spaceId) {
		if(spaceId!=null){
			SpaceFix spaceFix = this.getSpace(spaceId);
			SpaceType spaceType = EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, spaceFix.getType().intValue());
			Long accountId = spaceFix.getAccountId();
			List<SpaceFix> list = new ArrayList<SpaceFix>();
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			switch(spaceType){
			case Default_personal:
				String personalSQL = " from "+ SpaceFix.class.getName()+" where accountId = :accountId and type = :type";
				parameterMap.put("accountId", accountId);
				parameterMap.put("type", SpaceType.personal.ordinal());
				list = super.find(personalSQL, -1,-1,parameterMap);
				break;
			case default_leader:
				String leaderSQL = " from "+ SpaceFix.class.getName()+" where accountId = :accountId and type = :type";
				parameterMap.put("accountId", accountId);
				parameterMap.put("type", SpaceType.leader.ordinal());
				list = super.find(leaderSQL, -1,-1,parameterMap);
				break;
			case Default_out_personal:
				String outerSQL = " from "+ SpaceFix.class.getName()+" where accountId = :accountId and type = :type";
				parameterMap.put("accountId", accountId);
				parameterMap.put("type", SpaceType.outer.ordinal());
				list = super.find(outerSQL, -1,-1,parameterMap);
				break;
			case Default_personal_custom:
				String personalCustomSQL = " from "+ SpaceFix.class.getName()+" where accountId = :accountId and type = :type and parentId = :parentId";
				parameterMap.put("accountId", accountId);
				parameterMap.put("type", SpaceType.personal_custom.ordinal());
				parameterMap.put("parentId", spaceFix.getId());
				list = super.find(personalCustomSQL, -1,-1,parameterMap);
				break;
			}
			String hql = "update "+SpaceSort.class.getName()+" set spacePath = :newPath where spacePath= :oldPath";
			if(CollectionUtils.isNotEmpty(list)){
				for(SpaceFix fix : list){
					String pagePath = fix.getPagePath();
					SpacePage page = this.pageManager.getPage(pagePath);
					if(page!=null){
						Fragment root= page.getRootFragment();
						List<Fragment> fragments = root.getChildFragments();
						if(CollectionUtils.isNotEmpty(fragments)){
							for(Fragment frag: fragments){
								Long entityId = frag.getId();
								this.portletEntityPropertyManager.deleteProperties(entityId);
							}
						}
					}
					this.pageManager.removePage(pagePath);
					this.deleteSpaceAllSecurity(fix.getId());
					Map<String,Object> paraMap = new HashMap<String,Object>();
					paraMap.put("newPath", String.valueOf(spaceId));
					paraMap.put("oldPath", String.valueOf(fix.getId()));
					super.bulkUpdate(hql, paraMap);
					this.delete(fix);
					allCacheSpace.remove(fix.getId());
				}
			}
		}
	}

	@Override
	public void updateCustomedSpaceName(String spaceName, String spaceType,
			Long accountId,Long spaceId) {
		if(Strings.isNotBlank(spaceName)&&Strings.isNotBlank(spaceType)&&accountId!=null&&spaceId!=null){
			String hql = "update "+ SpaceFix.class.getName()+" set spaceName = :spaceName where accountId = :accountId and type = :type ";
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("spaceName", spaceName);
			paramMap.put("accountId", accountId);
			if(SpaceType.Default_personal.name().equals(spaceType)){
				paramMap.put("type", SpaceType.personal.ordinal());
			}else if(SpaceType.default_leader.name().equals(spaceType)){
				paramMap.put("type", SpaceType.leader.ordinal());
			}else if(SpaceType.Default_out_personal.name().equals(spaceType)){
				paramMap.put("type", SpaceType.outer.ordinal());
			}else if(SpaceType.Default_personal_custom.name().equals(spaceType)){
				hql = "update "+ SpaceFix.class.getName()+" set spaceName = :spaceName where accountId = :accountId and type = :type and parentId = :parentId";
				paramMap.put("type", SpaceType.personal_custom.ordinal());
				paramMap.put("parentId", spaceId);
			}
			super.bulkUpdate(hql, paramMap);
		}
	}

	@Override
	public List<V3xOrgMember> getSecurityMembers(String[][] securities){
		if(securities==null||securities.length<=0){
			return null;
		}
		
		List<V3xOrgMember> accountMembers = null;
		List<V3xOrgMember> memberIds = new ArrayList<V3xOrgMember>();
		try {
			for(int i=0; i< securities.length; i++){
				String[] security = securities[i];
				Long id = Long.valueOf(security[1]);
				//Account,Department,Post,Level,Team,Member
				if(security[0].equals("Account")){
						accountMembers = orgManager.getAllMembers(id);
					if(CollectionUtils.isNotEmpty(accountMembers)){
						memberIds.addAll(accountMembers);
		    		}
				}
				else if(security[0].equals("Department")){
					accountMembers = orgManager.getMembersByDepartment(id, false);
					if(CollectionUtils.isNotEmpty(accountMembers)){
						memberIds.addAll(accountMembers);
		    		}
				}
				else if(security[0].equals("Post")){
					accountMembers = orgManager.getMembersByPost(id);
					if(CollectionUtils.isNotEmpty(accountMembers)){
						memberIds.addAll(accountMembers);
		    		}
				}
				else if(security[0].equals("Level")){
					accountMembers = orgManager.getMembersByLevel(id);
					if(CollectionUtils.isNotEmpty(accountMembers)){
						memberIds.addAll(accountMembers);
		    		}
				}
				else if(security[0].equals("Team")){
					accountMembers = orgManager.getTeamMember(id);
					if(CollectionUtils.isNotEmpty(accountMembers)){
						memberIds.addAll(accountMembers);
		    		}
				}
				else if(security[0].equals("Member")){
					V3xOrgMember  member = orgManager.getMemberById(id);
					memberIds.add(member);
				}
			}
			return memberIds;
		} catch (NumberFormatException e) {
			log.error("空间推送格式转换错误：",e);
		} catch (BusinessException e) {
			log.error("空间推送业务异常：",e);
		}
		return null;
	}

	@Override
	public List<Long> getAllCustomSpace() throws SpaceException {
		Map<com.seeyon.v3x.space.Constants.SpaceType, List<SpaceModel>> accessSpace = getAccessSpace(CurrentUser.get().getId(), CurrentUser.get().getLoginAccount());
		List<SpaceModel> customSpace = OuterWorkerAuthUtil.combineList(accessSpace.get(com.seeyon.v3x.space.Constants.SpaceType.public_custom), accessSpace.get(com.seeyon.v3x.space.Constants.SpaceType.public_custom_group));
		List<Long> customSpaceIds = new ArrayList<Long>();
		if(customSpace != null) {
			for(SpaceModel sm : customSpace) {
				customSpaceIds.add(sm.getId());
			}
		}
		return customSpaceIds;
	}
}