package com.seeyon.v3x.formbizconfig.manager;

import java.util.List;

import www.seeyon.com.v3x.form.base.SeeyonFormException;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigShareScope;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigTempletProfile;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.formbizconfig.webmodel.MenuConfig;
import com.seeyon.v3x.menu.domain.Menu;

/**
 * 表单业务配置业务逻辑接口
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2009-08-12
 */
public interface FormBizConfigManager {
	
	/**
	 * 获取当前用户自己创建和他人共享的全部或按指定条件查询的表单业务配置记录
	 * @param memberId   		   当前用户ID
	 * @param searchModel  		   搜索条件模型，传入时不能为空
	 */
	public List<FormBizConfig> findAll(Long memberId, SearchModel searchModel);
	
	/**
	 * 根据主键ID获取单条表单业务配置记录
	 */
	public FormBizConfig findById(Long bizConfigId);

	/**
	 * 保存一条表单业务配置记录
	 */
	public void saveBizConfig(FormBizConfig formBizConfig);

	/**
	 * 修改表单业务配置
	 */
	public void updateBizConfig(FormBizConfig formBizConfig);

	/**
	 * 修改表单业务配置某些字段
	 * @param fieldNames 字段名称，比如：name、updateDate等
	 * @param values	 所要修改的字段新的值
	 */
	public void updateBizConfigField(Long bizConfigId, String[] fieldNames, Object[] values);

	/**
	 * 用户删除自己创建的多条表单业务配置，并删除对应的共享范围、模板关系、菜单挂接项、栏目挂接项及菜单挂接项与表单业务配置的关系记录
	 * @param bizConfigIds
	 */
	public void deleteReal(List<Long> bizConfigIds);

	/**
	 * 响应用户的批量删除操作(在前端进行了过滤，选中的只可能是用户自己创建的业务配置记录)<br>
	 * 具备菜单挂接的记录，同时更新其创建者、共享者的菜单个性化菜单设置信息<br>
	 * 如果删除的对象中包括具备菜单挂接的记录，则删除之后需要刷新顶部页面菜单显示，反之则不需，返回此值用于其后的js刷新显示<br>
	 * @return 删除之后是否需要刷新菜单显示
	 */
	public boolean deleteBizConfigs(List<Long> bizConfigIds, Long memberId) throws BusinessException;

	/**
	 * 删除业务配置对应创建者和共享者的个性化菜单信息记录<br>
	 * 场景：删除具备菜单挂接的业务配置记录或在信息中心页面取消菜单挂接时
	 * @param bizConfig <b>有效且具备菜单挂接的业务配置</b>，调用此方法时，应对此参数进行这两方面的校验
	 */
	public void deletePersonalMenuSetting(FormBizConfig bizConfig) throws BusinessException;

	/**
	 * 为指定多个人员生成或删除对应的菜单个性化信息记录
	 * @param mainMenuId  表单业务配置对应的一级菜单
	 * @param memberIds   人员对象
	 * @param toAdd 	  如共享则增加，为true；如取消共享则删除，为false
	 */
	public void updateMenuSetting(Long mainMenuId, List<Long> memberIds, boolean toAdd);

	/**
	 * 复制旧有业务配置对应的表单模板关系记录到新业务配置中
	 * @param orginalBizConfigId   旧有业务配置ID
	 * @param newBizConfigId	   新的业务配置ID
	 */
	public void cloneTempleteProfiles(Long orginalBizConfigId, Long newBizConfigId);
	
	/**
	 * 复制旧有业务配置对应的菜单挂接项(一级菜单下的二级菜单)到新业务配置中
	 * @param orginalBizConfigId   旧有业务配置ID
	 * @param newBizConfigId	   新的业务配置ID
	 * @param newParentMenuId	   新的业务配置对应的一级菜单ID
	 */
	public void cloneSubMenus(Long orginalBizConfigId, Long newBizConfigId, Long newParentMenuId) throws CloneNotSupportedException;

	/**
	 * 保存表单业务配置与表单模板关系
	 * @param formBizConfigTempletProfile
	 */
	public void saveTempProfile(FormBizConfigTempletProfile formBizConfigTempletProfile);

	/**
	 * 通过获取的表单模板信息生成对应的表单模板与表单业务配置关系记录
	 * @param templeteIds
	 * @param bizConfigId
	 */
	public void saveTempleteProfiles(String templeteIds, Long bizConfigId);

	/**
	 * 生成菜单时同时生成对应的菜单项与表单业务配置关系
	 * @param menuId 挂接菜单选项对应ID
	 * @param formBizConfigId  表单业务配置ID
	 */
	public void saveProfile4Menu(Long menuId, Long formBizConfigId); 

	/**
	 * 通过获取的一级菜单、二级菜单信息生成对应的菜单项、菜单与表单业务配置记录，同时更新创建者和共享者的个性化菜单信息记录
	 * @param menuIdAndCategorys 	获取的菜单挂接项id和所属类型、菜单挂接项名称字符拼串数组，其中的每个元素按照","分割之后的数组包含两个元素：<br>
	 * 		  element[0]			二级菜单id，如果该二级菜单属于新建事项，则此id为其对应的表单模板id<br>
	 * 		  element[1]			二级菜单所属分类，比如：7（新建事项）、8（待发事项）等<br>
	 * @param menuNames				二级菜单名称<br>
	 * @param bizConfig 			表单业务配置
	 * @param templeteIds 			用户所选择的表单模板ID拼串，用","分隔
	 * @param addMenuProfileList 	可能需要添加个人化菜单信息的共享用户
	 * @param deleteMenuProfileList 可能需要删除个人化菜单信息的被取消共享的用户
	 */
	public void saveMainAndSubMenus(String[] menuIdAndCategorys, String[] menuNames, FormBizConfig bizConfig,  
				String templeteIds, List<Long> addMenuProfileList, List<Long> deleteMenuProfileList) throws Exception;
	
	/**
	 * 保存一级菜单并更新对应人员的个性化菜单信息记录，返回一级菜单ID用于二级子菜单的保存
	 * @param mainMenuName          一级菜单名称，通过业务配置名称获取
	 * @param bizConfig 			表单业务配置
	 * @param memberId 				当前用户ID
	 * @param addMenuProfileList 	可能需要添加个人化菜单信息的共享用户
	 * @param deleteMenuProfileList 可能需要删除个人化菜单信息的被取消共享的用户
	 * @return 						所保存的一级菜单主键ID
	 */
	public Long saveMainMenuAndUpdateMenuSetting(String mainMenuName, Long bizConfigId, 
			Long memberId, List<Long> addMenuProfileList, List<Long> deleteMenuProfileList);
	
	/**
	 * 保存业务配置对应一级父菜单下的二级子菜单，保存这些子菜单与业务配置的关系记录
	 * @param bizConfigId   表单业务配置ID
	 * @param templeteIds   表单业务配置所选择的表单模板ID拼串，用","分隔
	 * @param mainMenuId    业务配置对应一级父菜单ID
	 * @param menuIdAndCategorys 获取的菜单挂接项id和所属类型、菜单挂接项名称字符拼串数组，其中的每个元素按照","分割之后的数组包含两个元素：<br>
	 * 		  element[0]：	二级菜单id，如果该二级菜单属于新建事项，则此id为其对应的表单模板id<br>
	 * 		  element[1]：	二级菜单所属分类，比如：7（新建事项）、8（待发事项）等<br>
	 * @param menuNames     二级菜单名称数组
	 * @throws SeeyonFormException
	 */
	public void saveSubMenusAndProfiles(Long bizConfigId, String templeteIds, 
			Long mainMenuId, String[] menuIdAndCategorys, String[] menuNames) throws SeeyonFormException;

	/**
	 * 根据菜单所属类型及对应表单业务配置ID获取二级菜单，主要用于当前位置显示
	 * @param bizConfigId 表单业务配置ID
	 * @param category	  菜单挂接项所需类型，比如：信息中心、新建事项等
	 */
	public Menu getSubMenu4Location(Long bizConfigId, int category);

	/**
	 * 新建事项菜单挂接项可能有多项，需要具体到其对应的表单模板
	 * @param bizConfigId  表单业务配置ID
	 * @param templeteId   该新建事项对应的表单模板ID
	 */
	public Menu getNewAffairMenu4Location(Long bizConfigId, String templeteId);
	
	/**
	 * 获取业务配置对应的二级菜单并将其包装成MenuConfig用于前端展现
	 * @param bizConfigId  表单业务配置ID
	 */
	public List<MenuConfig> getMenuConfigs(Long bizConfigId);

	/**
	 * 通过共享范围信息保存对应的共享范围记录
	 */
	public void saveShareInfo(String shareScope, Long bizConfigId);

	/**
	 * 删除单条表单业务配置对应的共享范围
	 */
	public void deleteShareScopes(Long bizConfigId);

	/**
	 * 删除表单模板与业务配置的关系记录
	 */
	public void deleteTempletProfiles(Long bizConfigId);

	/**
	 * 删除单条业务配置对应的菜单(包括一级菜单和二级菜单)关系记录
	 */
	public void deleteMenuProfiles(Long bizConfigId);
	
	/**
	 * 删除单条业务配置对应的二级菜单关系记录
	 * @param bizConfigId   业务配置ID
	 * @param mainMenuId    业务配置对应的一级菜单ID
	 */
	public void deleteSubMenuProfiles(Long bizConfigId, Long mainMenuId);

	/**
	 * 判断当前表单业务配置所挂接的菜单项中是否还存在"信息中心"一项，同时返回该子菜单的ID(如存在)
	 * @param  bizConfig 表单业务配置
	 * @return Object[]  [0]："信息中心"菜单是否存在，[1]：如存在，则返回该菜单ID用于当前位置展现
	 */
	public Object[] isInfoCenterMenuExist(FormBizConfig bizConfig);

	/**
	 * 获取业务配置对应的所有共享范围记录
	 * @param formBizConfigId
	 * @return
	 */
	public List<FormBizConfigShareScope> getShareScopes(Long formBizConfigId);

	/**
	 * 获取业务配置对应的共享范围记录Type|ID拼串，辅助在修改业务配置时，判断是否与修改前一致，不做没有必要的共享范围删除、新增操作
	 */
	public String getShareScopeIds(Long formBizConfigId);

	/**
	 * 业务配置类的挂接菜单，是否对当前用户显示：仅对创建者和共享者显示
	 */
	public boolean isShowToUser(long userId, Menu menu);

	/**
	 * 判断当前用户是否业务配置创建者或还在共享范围内，检验其能否查看或使用业务配置相关内容<br>
	 */
	public boolean isCreatorOrInShareScope(FormBizConfig bizConfig, Long memberId);
	
	/**
	 * 判断非表单业务配置创建者是否处在共享范围中
	 */
	public boolean isInShareScopeNotCreator(Long bizConfigId, Long memberId);

	/**
	 * 在业务配置的共享使用者对业务配置进行另存为、查看业务模板、发布到首页或取消发布等等操作时，校验对应的业务配置是否仍旧存在及当前用户是否还在共享范围当中<br>
	 * 供AJAX在前端调用，返回布尔值数组：<br>
	 * [0]-选中的业务配置记录是否存在<br>
	 * [1]-当前用户是否还处在共享范围中<br>
	 */
	public Boolean[] validateIsExistAndInShareScope(Long bizConfigId, Long memberId);

	/**
	 * 获取表单业务配置所对应的表单模板(不进行当前用户对其使用权限的过滤)ID字符串：12234234,34534534534,3456123123...
	 */
	public String getTempleteIds(Long bizConfigId);

	/**
	 * 得到当前用户创建或共享的所有选中栏目挂接的表单业务配置，用于栏目选择
	 * @param memberId 当前用户ID
	 */
	public List<FormBizConfig> getFormBizConfigs4Column(Long memberId);

	/**
	 * 获取表单业务配置所设定的表单模板对应的督办事项总数
	 */
	public int getSuperviseTotalCount4BizConfig(long userId, int status, List<Long> templeteIds);
	
	/**
	  * 检测数据库中是否已经存在该名称 -- 由于表单和其他应用不是同一个上下文，所以此方法暂时写在这个类中，便于AJAX调用
	  * @param planName
	  * @return 存在true,不存在 false
	  */
	 public boolean checkFormQueryPlanName(String planName);

}