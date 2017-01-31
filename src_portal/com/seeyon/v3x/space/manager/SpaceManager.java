package com.seeyon.v3x.space.manager;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.Constants.SpaceTypeClass;
import com.seeyon.v3x.space.domain.Banner;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.domain.SpacePage;

/**
 * 空间管理
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-6
 */
public interface SpaceManager {
	
	/**
	 * 取得page对象
	 * 
	 * @param pagePath
	 * @return
	 */
	public SpacePage getSpacePage(String pagePath);
	
	/**
	 * 获取固定空间（个人、部门、单位）的修改信息
	 * 
	 * @param pagePath
	 * @return
	 */
	public SpaceFix getSpaceFix(String pagePath);

	/**
	 * 创建部门空间个人空间
	 * <pre>
	 * 1、创建文件夹/_user/loginName 并限制权限为当前用户
	 * 2、从/seeyon/personal.psml copy到/_user/loginName/default-page.psml
	 * 3、在space_fix表中记录个人空间被修改过
	 * </pre>
	 * 
	 * @param memberId
	 * @param accountId
	 *            所属单位Id
	 * @return 
	 * @throws SpaceException
	 */
	public SpaceFix createPersonalSpace(Long memberId, Long accountId) throws SpaceException;
	/**
	 * 创建个人类型空间
	 * @param memberId
	 * @param accountId
	 *            所属单位Id
	 * @return 
	 * @throws SpaceException
	 */
	public SpaceFix createPersonalDefineSpace(Long memberId,Long accountId,Long spaceId) throws SpaceException;
	/**
	 * 创建部门空间
	 * <pre>
	 * 1、从/seeyon/department/default-page.psml copy到/seeyon/department/uuid.psml
	 * 2、在space表中记录该空间，并把权限指给全部门
	 * </pre>
	 * 
	 * @param departmentId
	 * @param departmentName
	 * @param accountId
	 * @return 
	 * @throws SpaceException
	 */
	public SpaceFix createDepartmentSpace(Long departmentId, String departmentName,
			Long accountId) throws SpaceException;
	
	/**
	 * 创建默认自定义空间(领导空间)
	 * <pre>
	 * 1、从/seeyon/custom/default-page.psml copy到/seeyon/custom/default.psml
	 * 2、在space表中记录该空间，权限指定给领导
	 * </pre>
	 * 
	 * @param accountId
	 * @return 
	 * @throws SpaceException
	 */
	public SpaceFix createDefaultLeaderSpace(Long accountId) throws SpaceException;
	
	
	/**
	 * 删除部门空间
	 * <pre>
	 * 1、查询space_fix表，是否创建了部门空间
	 * 2、删除space对应的数据
	 * 3、删除Page对应的数据
	 * 4、删除portlet_entity_property对应的数据
	 * </pre>
	 * @param departmentId
	 * @throws SpaceException
	 */
	public void deleteDepartmentSpace(Long departmentId) throws SpaceException;
	
	/**
	 * 启用部门空间
	 * 
	 * @param departmentId
	 * @param isEnabled
	 * @throws SpaceException
	 */
	public void enableDepartmentSpace(Long departmentId, boolean isEnabled) throws SpaceException;
	
	/**
	 * 删除个人空间
	 * 
	 * @param memerId
	 * @throws SpaceException
	 */
	public void deletePersonalSpace(Long memerId) throws SpaceException;
	
	/**
	 * 删除集团空间
	 * 
	 * @throws SpaceException
	 */
	public void deleteGroupSpace() throws SpaceException;
	
	/**
	 * 删除单位空间
	 * 
	 * @param accountId
	 * @throws SpaceException
	 */
	public void deleteCorporationSpace(Long accountId) throws SpaceException;
	
	/**
	 * 删除默认的个人空间
	 * 
	 * @param accountId
	 * @throws SpaceException
	 */
	public void deleteDefaultPersonalSpace(Long accountId) throws SpaceException;
	
	/**
	 * 删除默认的部门空间
	 * 
	 * @param accountId
	 * @throws SpaceException
	 */
	public void deleteDefaultDepartmentSpace(Long accountId) throws SpaceException;
	
	/**
	 * 创建单位空间
	 * <pre>
	 * 1、从/seeyon/corporation/default-page.psml copy到/seeyon/corporation/uuid.psml
	 * 2、在space_fix表中记录单位空间被修改过
	 * </pre>
	 * 
	 * @param accountId
	 * @return 单位空间的Page Path
	 * @throws SpaceException
	 */
	public SpaceFix createCorporationSpace(Long accountId) throws SpaceException;
	
	/**
	 * 创建集团空间
	 * <pre>
	 * 1、从/seeyon/group/default-page.psml copy到/seeyon/group/uuid.psml
	 * 2、在space_fix表中记录集团空间被修改过
	 * </pre>
	 * @return
	 * @throws SpaceException
	 */
	public SpaceFix createGroupSpace() throws SpaceException;
    
	/**
	 * 是否创建了部门空间
	 * 
	 * @param departmentId
	 * @return
	 */
	public boolean isCreateDepartmentSpace(Long departmentId);
	
	/**
	 * 是否修改个人空间
	 * @param memberId
	 * @return
	 */
	public boolean isChangePersonalSpace(long memberId);
	
	/**
	 * 是否修改了单位空间
	 * 
	 * @param accountId
	 * @return
	 */
	public boolean isChangeCorporationSpace(long accountId);
	/**
	 * 得到默认的系统空间 包括默认的授权对象（集团）
	 * @return
	 */
	public SpaceFix getDefualtGroupSpaceFix();
    /**
     * 得到SpaceFix对象
     * @param type SpaceType
     * @param entityId, 组织模型实体ID
     * @param customSpaceId 自定义空间ID，非自定义空间为NULL
     * @return
     */
	public SpaceFix getSpaceFix(Constants.SpaceType type, long entityId, Long customSpaceId);
	
	/**
	 * 得到个人空间，如果修改过了，返回修改的路径，否则返回默认的路径
	 * 
	 * @param memberId
	 * @param accountId 主岗所在单位的id
	 * @return
	 */
	public String getPersonalSpacePath(long memberId, long accountId);
	
	/**
	 * 得到单位空间，如果修改过了，返回修改的路径，否则返回默认的路径
	 * 
	 * @param accountId
	 * @return
	 */
	public String getCorporationSpacePath(long accountId);
	
	/**
	 * 是否允许用户自定个人空间
	 * 
	 * @param accountId
	 * @return
	 */
	public boolean isAllowedUserDefinedPersonalSpace(long accountId);
	
	/**
	 * 是否允许用户自定义部门空间
	 * 
	 * @param accountId
	 * @return
	 */
	public boolean isAllowedUserDefinedDepartmentSpace(long accountId);
	
	/**
	 * 默认的个人空间地址，现找单位管理员是修改的地址，再找系统默认的 
	 * 
	 * @param accountId
	 * @return
	 */
	public String getDefaultPersonalSpacePath(long accountId);
	
	/**
	 * 默认的领导空间地址，现找单位管理员是修改的地址，再找系统默认的 
	 * 
	 * @param accountId
	 * @return
	 */
	public String getDefaultLeaderSpacePath(long accountId);
	
	
	/**
	 * 默认的部门空间地址，现找单位管理员是修改的地址，再找系统默认的 
	 * 
	 * @param accountId
	 * @return
	 */
	public String getDefaultDepartmentSpacePath(long accountId);
	
	/**
	 * 默认的单位空间地址，现找单位管理员是修改的地址，再找系统默认的 
	 * 
	 * @param accountId
	 * @return
	 */
	public String getDefaultCorportionSpacePath(long accountId);

	
	/**
	 * 创建默认的个人空间
	 * 
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public String createDefaultPersonalSpace(long accountId) throws SpaceException;
	
	/**
	 * 创建默认的部门空间
	 * 
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public String createDefaultDepartmentSpace(long accountId) throws SpaceException;
	
	/**
	 * 修改固定空间（部门、单位、集团）的基本信息
	 * 
	 * @param pagePath
	 *            page路径
	 * @param managers
	 *            管理员
	 * @param securities
	 *            访问者
	 * @param slogan
	 *            口号
	 * @param banner
	 * @param motto
	 *            格言，只有部门空间有
	 * @param allowdefined
	 *            是否允许前端用户配置
	 * @param spaceName 空间名称
	 * @param state 空间状态
	 */
	public void updateFixDCGSpaceInfo(String pagePath, String[][] managers,
			String[][] securities, String[][] vistors, Banner banner,
			String motto, boolean allowdefined, String spaceName, int state,
			boolean spaceMenuEnabled);

	public void updateFixDCGSpaceInfo(String pagePath, String[][] managers,
			String[][] securities, String[][] vistors, Banner banner,
			String motto, boolean allowdefined, String spaceName, int state);

	/**
	 * 修改固定个人空间的基本信息，update所有的
	 * 
	 * @param accountId
	 * @param slogan
	 * @param banner
	 * @param allowdefined
	 */
	public void updateFixPersonalSpaceInfo(long accountId, String spaceName,Banner banner, boolean allowdefined);


	/**
	 * 根据Space.id取得空间
	 * 
	 * @param id
	 * @return
	 */
	public SpaceFix getSpace(Long id);
	
    
    /**
     * 创建自定义空间,首先判断是否存在，若存在不创建
     * @return
     * @throws SpaceException
     */
    public SpaceFix createCustomSpace(SpaceType customType,Long spaceId, Long accountId) throws SpaceException;
    
	/**
	 * 修改一个空间
	 * 
	 * @param space
	 * @throws SpaceException
	 */
	//public void updateCustomSpace(SpaceFix space) throws SpaceException;

	/**
	 * 删除一个空间
	 * 
	 * @param spaceId
	 * @throws SpaceException
	 */
	public void deleteCustomSpace(Long spaceId, Long accountId, boolean isFromAjax) throws SpaceException;
	
	/**
	 * 得到我能访问的所有空间的PagePath
	 * 
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	public Map<Constants.SpaceType, List<SpaceModel>> getAccessSpace(Long memberId, Long accountId) throws SpaceException;
	
	/**
	 * 得到我能访问的单位空间的PagePath
	 * 
	 * @param accountId
     * @param isCheckEnabled 是否需要校验‘空间是否启用’
	 * @return
	 */
	public List<SpaceModel> getAccessCorporationSpace(long accountId, boolean isCheckEnabled) throws SpaceException;
	
    /**
     * 得到我能访问的集团空间的PagePath
     * @param isCheckEnabled 是否需要校验‘空间是否启用’
     */
    public List<SpaceModel> getAccessGroupSpace(boolean isCheckEnabled,Long memberId) throws SpaceException;
    
	/**
	 * 得到我能访问的空间的排序
	 * 
	 * @param memberId
     * @param accountId
     * @param userLocale
     * @param isDefault 是否是取默认的排序 {personal, department, corporation, /seeyon/custom/23459187189745.psml}
     * @param accessSpaces 通过方法accessSpaces返回的值，增加这个参数避免首页重复查询
	 * @return List<String[]> String[]={spaceType, spaceId, spaceName}
	 */
	public List<String[]> getSpaceSort(Long memberId, Long accountId, Locale userLocale, boolean isDefault, Map<SpaceType, List<SpaceModel>> accessSpaces) throws SpaceException;
	/**
	 * 得到我能访问的空间的排序
	 * 
	 * @param memberId
     * @param accountId
     * @param userLocale
     * @param isDefault 是否是取默认的排序 {personal, department, corporation, /seeyon/custom/23459187189745.psml}
     * @param accessSpaces 通过方法accessSpaces返回的值，增加这个参数避免首页重复查询
	 * @return List<String[]> String[]={spaceType, spaceId, spaceName}
	 */
	public List<String[]> getCanAccessSpace(Long memberId, Long accountId, Locale userLocale, boolean isDefault, Map<SpaceType, List<SpaceModel>> accessSpaces) throws SpaceException;
	
    /**
     * 删除空间排序
     * @param memberId
     * @param accountId
     */
    public void deleteSpaceSort(Long memberId, Long accountId);
    
    /**
     * 根据类型及对应ID删除不可用的空间排序
     * 如：关联系统被删除，关联项目被取消或废弃等情况，需直接删除对应的空间排序记录
     * @param memberId
     * @param accountId
     * @param spacePath
     * @param type
     */
    public void deleteSpaceSortByTypeAndSpacePath(Long memberId, Long accountId, String spacePath, int type);
    
    /**
     * 更新空间排序
     * @param sortList
     * @param memberId
     * @param accountId
     */
    public void updateSpaceSort(List<String[]> sortList ,List<String[]> disSort, Long memberId, Long accountId);
    
	/**
	 * 得到布局模板[布局模板, 模板修饰]
	 * 
	 * @param pagePath
	 * @return
	 */
	public String[] getLayoutType(String pagePath);
	
	/**
	 * 根据PSML的path得到所有的portlet-fragment
	 * 
	 * @param pagePath
	 * @return
	 * @throws SpaceException
	 */
	public Map<String,Map<String,Fragment>> getFragments(String pagePath) throws SpaceException;
	/**
	 * 根据PSML的path得到所有的portlet-fragment
	 * 如果空间处于编辑状态，则获取缓存中的fragment
	 * @param pagePath
	 * @param editKeyId
	 * @param memberId
	 * @return
	 * @throws SpaceException
	 */
	public Map<String, Map<String, Fragment>> getFragments(String pagePath,Long editKeyId,Long memberId)throws SpaceException;
	
	/**
	 * 得到能够访问部门空间的组织模型实体
	 * 
	 * @param departmentId
	 *            部门Id
	 * @return Object[]: 0-entityType 1-entityId
	 */
	public List<Object[]> getSecuityOfDepartment(Long departmentId);
	
	/**
	 * 得到部门空间管理员的组织模型实体
	 * 
	 * @param departmentId
	 *            部门Id
	 * @return Object[]: 0-entityType 1-entityId
	 */
	public List<Object[]> getSpaceAdminsOfDepartment (Long departmentId);
	
	/**
	 * 获取部门空间管理员人员ID集合
	 * @param departmentId
	 * @return
	 */
	public List<Long> getSpaceAdminIdsOfDepartment (Long departmentId);
	
	/**
	 * 得到我能管理的部门
	 * 
	 * @param memberId
	 * @return
	 */
	public List<Long> getManagerDepartments(Long memberId, Long accountId);
	
	public List<Long> getCanManagerSpace(Long memberId);
	
	/**
	 * 获取我能管理的自定义团队空间
	 * 
	 * @param memberId
	 * @return
	 */
	public List<Long> getCanManagerCustomSpace(Long memberId) throws Exception;
	
	/**
	 * 能否管理部门空间
	 * 
	 * @param memberId
	 * @param departmentId
	 * @return
	 */
	public boolean canManagerDepartments(Long memberId, Long departmentId);
	
	/**
	 * 
	 * @param pagePath
	 * @return 
	 */
	public EnumMap<PortletEntityProperty.PropertyName, String> getPortletEntityProperty(String pagePath);
    
    /**
     * 得到单位/集团管理员可以管理的空间列表
     * @param accountId 1L返回集团，单位ID返回该单位
     * @return
     */
    public List<SpaceModel> getAdminCanManagerSpace(Long accountId,SpaceTypeClass spaceType,String condition,String value) throws SpaceException;
    
    public List<SpaceModel> getAdminCanManagerSpace(Long accountId,SpaceTypeClass spaceType,String condition,String value,Boolean needPage) throws SpaceException;
    /**
     * AJAX 删除自定义空间
     * @param beDeleteSpaceIds
     * @param accountId
     * @return
     * @throws SpaceException
     */
    public boolean deleteCustomSpaces4AJAX(String[] beDeleteSpaceIds, String accountId) throws SpaceException;
    
    /**
     * 删除默认的领导空间
     * @param accountId
     * @throws SpaceException
     */
    public void deleteDefaultLeaderSpace(Long accountId) throws SpaceException;
    
	
	/**
	 * 迁移部门空间，用于部门迁移时调用
	 * @param departmentId 部门的ID
	 * @param newAccountId 部门搬迁后的新单位ID
	 */
	public void transplantDepartmentSpace(Long departmentId, Long newAccountId);
	
	/**
	 * 删除个人空间下的指定栏目
	 * @param memberId 用户ID
	 * @param accountId 登录单位Id
	 * @param sectionId 栏目Id
	 * @return boolean值结果
	 * @throws SpaceException
	 */
	public boolean removePersonalSpaceSection(Long memberId, Long accountId, String sectionId, String singleBoardId) throws SpaceException;
	
	/**
	 * 得到个人/领导空间  如果没有创建，则返回默认的个人/领导空间
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public SpaceFix getPersonSpace(Long memberId,Long accountId)throws SpaceException;
	
	/**
	 * 更新空间前的验证
	 * 1.是否存在同名的空间名
	 * 2.空间是否可以停用
	 * 3.空间是否可以删除
	 * @param name
	 * @return
	 */
	public String checkSpace(String name,Integer state,String spaceType,Long curId);
	
	public void updatePage(String pagePath, String newLayout, String newDecoration, List<Fragment> newFragments, Map<Long, Map<String, String>> portletEntityProperties);

	/**
	 * 给单位创建默认的个人空间
	 * @param accountId
	 * @throws SpaceException
	 */
	public void initAccountSpace(Long accountId) throws SpaceException;
	
	public SpaceFix toDefaultSpace(SpaceType spaceType, String[][] manages, String[][] users, String[][] vistors, Long entityId, SpaceFix spaceFix) throws SpaceException;

	public SpaceFix toDefaultSpace(SpaceType spaceType, String[][] manages, String[][] users, String[][] vistors, Long entityId) throws SpaceException;
	
	/**
	 * 取得人员个人空间的空间id<br>
	 * 旧数据：旧数据是么踹这个spaceId的，这里取spaceId的方法：<br>
	 * 1.如果以前是领导空间，返回领导空间id。
	 * 2.如果不是领导，此人是内部人员则返回默认个人空间，如果此人是外部人员，返回外部人员空间
	 * @param member
	 * @return
	 */
	public Long getPersonalSpaceId(V3xOrgMember member);
	
	/**
	 * 新建人员后，将人员授权添加到授权里
	 * @param member
	 */
	public void putMember2SpaceSecurity(V3xOrgMember member);
	
	/**
	 * 新建人员 返回默认的空间id。
	 * @param isInternal
	 * @param accountId
	 * @return
	 */
	public Long getPersonalSpaceId4Create(boolean isInternal,Long accountId);

	/**
	 * 判断该用户是否可以管理该空间（部门空间单独处理，需要判断entityId）
	 * 
	 * @param memberId
	 * @param spaceId
	 * @return
	 */
	public boolean canManagerSpace(Long memberId, Long spaceId);

	/**
	 * 前端删除fragment。
	 * @param entityId fragment的id 见{@link com.seeyon.v3x.space.domain.Fragment}
	 * @param path page.path
	 * @return
	 */
	public String deleteFragment(Long entityId,String path,int index);
	/**
	 * 前端缓存删除fragment
	 * @param entityId 当前fragment
	 * @param spaceId 当前空间
	 * @param editKeyId 会话ID
	 * @param memberId 当前用户
	 * @return
	 */
	public void deleteFragment(Long entityId,String spaceId,String editKeyId,Long memberId,int index);
	
	public SpaceFix createDefaultOutSpace(Long accountId) throws SpaceException ;
	
	/**
	 * 判断对于指定用户，某一业务配置对应的单板块栏目是否已经发布到该用户的首页个人空间
	 * @param bizConfigId	业务配置ID
	 * @param userId		用户ID
	 * @param accountId		用户登录单位ID
	 * @return	业务配置栏目是否已发布到用户个人首页空间
	 */
	public boolean isBizConfigPublished(Long bizConfigId, Long userId, Long accountId);
	
	/**
	 * 更新空间栏目
	 * @param pagePath
	 * @param sectionIds
	 * @param sectionNames
	 * @param singleBoards
	 * @return
	 */
	public boolean addPortlet(String pagePath,String[] sectionIds,String[] sectionNames,String[] singleBoards,String[] entityIds,String[] ordinals,String editKeyId,Long memberId,String decoration);
	/**
	 * 更新栏目坐标
	 * @param pagePath
	 * @param sectionId
	 * @param layoutRow
	 * @param layoutColumn
	 * @return
	 */
	public boolean updateLayoutIndex(String pagePath,List<Fragment> fragments,String editKeyId,Long memberId);
	/**
	 * 更新布局信息
	 * @param pagePath
	 * @param newLayout
	 */
	public void updatePage(String pagePath, String newLayout);
	/**
	 * 得到配置了指定栏目的空间，<b>暂不考虑个人空间</b>
	 * 
	 * @param sectionId section.xml的SectionBeanId
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws SpaceException
	 */
	public List<SpaceModel> getSpacesOfSection(String sectionId, Long memberId, Long accountId) throws SpaceException;

	/**
	 * 根据用户id，获得其管理的所有空间列表
	 * @param userid
	 * @return
	 */
	public List<SpaceFix> getManagmentSpaceListByUserId(String userid);
	/**
	 * 自定义空间授权时追加自定义空间到排序表
	 * @param spaceId
	 * @param memberId
	 * @param accountId
	 * @throws SpaceException
	 */
	public void updateSpaceSortForPersonalCustom(String spaceId,List<V3xOrgMember> memberIds,Long accountId,boolean isDefault) throws BusinessException;
	/**
	 * 自定义空间授权变更时，更新个人空间排序表
	 * @param spaceId
	 * @param security
	 */
	public void updateSpaceSortBySecurity(Long spaceId,String[][] securities);
	/**
	 * fragment更新section信息，支持多section
	 * @param entityId
	 * @param sectionIds
	 * @param sectionNames
	 */
	public void updateSectionsToFragment(Long entityId,String sectionIds,String[] sectionNames,String[] singleBoards,String[] entityIds,String[] ordinals,Long memberId);
	/**
	 * 判断用户是否某一特定空间的空间管理员
	 * @param memberId 当前用户ID
	 * @param spaceId   当前空间ID
	 * @return 是否为当前空间管理员
	 * @throws BusinessException 
	 */
	public boolean isManagerOfThisSpace(long memberId, Long spaceId) throws BusinessException;
	/**
	 * 得到指定空间的组织模型实体信息
	 * @param spaceId
	 * @return
	 */
	public List<Object[]> getSecuityOfSpace(Long spaceId);
	/**
	 * 获取空间指定访问权限类型的组织模型实体信息
	 * 
	 * @param spaceId
	 *            空间ID
	 * @param securityType
	 *            访问权限类型：使用(0)、管理(1)、不限定类型(-1)
	 */
	public List<V3xOrgMember> getSpaceMemberBySecurity(Long spaceId, int securityType) throws BusinessException;
	/**
	 * 个人类型空间恢复默认
	 * @param memberId
	 * @param accountId
	 * @param spaceId
	 * @return
	 * @throws SpaceException
	 */
	public String toDefaultPersonalSpace(Long memberId,Long accountId,Long spaceId,String spaceType) throws SpaceException;
	/**
	 * 后台空间恢复默认
	 * @param memberId
	 * @param accountId
	 * @param spaceId
	 * @param spacePath
	 */
	public String toDefaultSpace(Long memberId,Long spaceId,String spacePath)throws SpaceException ;
	/**
	 * 空间编辑keyID存入缓存
	 * @param editKeyId
	 * @param spaceId
	 * @param memberId
	 */
	public void addEditKeyCache(Long editKeyId,String pagePath,Long memberId);
	/**
	 * 空间编辑keyId移出缓存
	 * @param editKeyId
	 * @param spaceId
	 * @param memberId
	 */
	public void removeEditKeyCache(Long editKeyId);
	/**
	 * 缓存更新栏目属性
	 * @param entityId
	 * @param properties
	 * @param tabIndex
	 * @param spaceId
	 * @param editKeyId
	 * @param memberId
	 * @throws CloneNotSupportedException 
	 */
	public void updateProperty(Long entityId,Map<String,String> properties,String tabIndex,Long editKeyId,Long memberId) throws CloneNotSupportedException;
	/**
	 * 前端保存空间
	 * @param editKeyId 会话ID
	 * @param memberId 操作人ID
	 * @param spaceId 空间ID
	 * @param decoration 布局数据
	 * @param toDefault 是否恢复默认
	 */
	public String updateSpaceByCache(Long editKeyId,Long memberId,Long spaceId,String decoration,String toDefault)throws SpaceException;
	/**
	 * 管理员保存空间
	 * @param editKeyId 会话ID
	 * @param memberId 操作人ID
	 * @param spaceId 空间ID
	 * @param decoration 布局数据
	 */
	public boolean updateSpace(Long editKeyId,Long memberId,Long spaceId,String decoration);
	/**
	 * 判断空间是否已发布表单栏目
	 * @param singleBoradId
	 * @param pagePath
	 * @return
	 */
	public boolean IsPublishedFormBizSection(String singleBoradId,String pagePath);
	/**
	 * 复制Fragment到缓存
	 * @param fragments
	 * @return
	 */
	public List<Fragment> copyFragmentCache(List<Fragment> fragments) ;
	/**
	 * 获取缓存中编辑状态的fragment
	 * @param editKeyId
	 * @param memberId
	 * @param x
	 * @param y
	 * @return
	 */
	public Fragment getCacheFragment(Long editKeyId,Long memberId,int x,int y);
	/**
	 * 更新SpaceFix的状态，启用:停用
	 * @param spaceId
	 * @param state
	 */
	public void updateSpaceFix(Long spaceId,int state);
	/**
	 * 收回个性化空间权限
	 * @param spaceId
	 */
	public void deleteCustomedSpace(Long spaceId);
	/**
	 * 同步个人类型空间名称
	 * @param spaceName
	 * @param spaceType
	 * @param accountId
	 * @param spaceId
	 */
	public void updateCustomedSpaceName(String spaceName,String spaceType,Long accountId,Long spaceId);
	/**
	 * 获取权限内人员ID
	 * @param securities
	 * @return
	 */
	public List<V3xOrgMember> getSecurityMembers(String[][] securities);
	/**
	 * 获取当前登录这能够访问的所有自定义空间的ID(只包含单位/集团)
	 * @return
	 */
	public List<Long> getAllCustomSpace() throws SpaceException;
}