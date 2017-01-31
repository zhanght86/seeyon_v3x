package com.seeyon.v3x.collaboration.templete.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.webmodel.SimpleTemplete;

public interface TempleteManager {

	public void save(Templete templete);

	/**
	 * 根据模板分类的类型（协同、公文、表单）读取该单位和其他单位授权给本单位该用户有权限访问的所有模板，不做分页
	 * @param userId 当前登录用户ID
	 * @param accountId
	 * @param categoryType 参考{@link com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.TYPE}
	 * @return
	 */
	public List<Templete> getAllSystemTempletes(Long  userId,Long accountId, Integer categoryType);
	
	/**
	 * 根据模板分类的类型（协同、公文、表单）读取该单位和其他单位授权给本单位的所有模板，不做分页
	 * 如果其他单位授权给本单位的部门，岗位，组的模板也一并找出来。
	 * @param accountId
	 * @param categoryType
	 * @return
	 */
	public List<Templete> getAllSystemTempletesByAcl(Long accountId,Integer categoryType);
	/**
	 * 根据模板ID来查找模板。
	 * @param templeteIds
	 * @param categoryType
	 * @return
	 */
	public List<Templete> getAllSystemTempletesByEntityIds(List<Long> templeteIds,Integer categoryType);
	/**
	 * 得到指定分类下所有模板，不做分页
	 * 应用场景：系统管理--点击左边模板分类tree，在右边列表显示
	 * 
	 * @param categoryId
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @return
	 */
	public List<Templete> getAllSystemTempletes(Long categoryId,Integer categoryType, String condition, String textfield, String textfield1);
	
	/**
	 * 统计某个分类下有多少模板
	 * 
	 * @param categoryId
	 * @param orgAccountId
	 * @return
	 */
	public int countAllSystemTempletes(Long categoryId, long orgAccountId);
	
	/**
	 * 得到某用户能访问到的系统模板（包括协同模板、表单模板）<br>
	 * 应用场景：人员调单位时调整其系统模板配置
	 * 
	 * @param categoryType 参考{@link com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.TYPE} 
	 * @return
	 */
	public List<Templete> getSystemTempletesByMemberId(Long memberId, Long accountId, Integer... categoryType);
	//重写getSystemTempletesByMemberId
	public List<Templete> getSystemTempletesByMemberId(Long memberId, Long accountId,String secretLevel,Integer... categoryType);
	
	/***
     * 得到某个组织模型实体可以访问的系统模板（包括协同、表单）
     * 应用场景：<li>1、部门空间-部门模板栏目
     * <li>2、人员调整部门后推送部门模板...
     * @param departmentId
     * @param categoryType
     * @return
     */
    public List<Templete> getSystemTempletesByOrgEntity(String orgEntityType, Long orgEntityId, Integer... categoryType);
    
	/**
	 * 得到当前登录用户的个人模板(启用), 只取登录单位的
	 * 
	 * @return
	 */
	public List<Templete> getPersonalTemplete();
	//成发集团项目 程炯 重写getPersonalTemplete
	public List<Templete> getPersonalTemplete(String secretLevel);
	
	public List<Templete> getPersonalTemplete(Integer[] categoryType);
	
	/**
	 * 得到当前登录用户的个人模板(启用、停用), 只取登录单位的
	 * 
	 * @return
	 */
	public List<Templete> getPersonalAllTemplete();

	public Templete get(Long id);
	
	public void update(Templete templete);
	
	public void delete(long id);
	
	/**
	 * 修改模板授权
	 * 
	 * @param templeteId
	 * @param templeteAuths
	 */
	public void updateAuth(long templeteId, Set<TempleteAuth> templeteAuths);
	
	/**
	 * 修改模板categoryId字段
	 * @param templeteId
	 * @param newCategoryId
	 */
	public void updateCategoryId(long templeteId, Long newCategoryId);

	/**
	 * 修改模板state字段
	 * 
	 * @param templeteId
	 * @param state
	 */
	public void updateTempleteState(long templeteId, Templete.State state);
	
	/***
	 * 检测是否在同一个分类下存在重名
	 * 
	 * @param categoryId
	 * @param subject
	 * @return
	 */
	public List<Long> checkSubject4System(Long categoryId, String subject);
	
	/**
	 * 检测是否在同一个类型下存在重名
	 * 
	 * @param type
	 * @param subject
	 * @return
	 */
	public List<Long> checkSubject4Personal(String type, String subject);
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	public boolean delete(List<String> ids);
	
	/**
     * 保存分支条件
     */
    public void saveBranch(long templateId,List<ColBranch> branchs);
    
    /**
     * 获取分支条件
     * @param id
     * @return
     */
    public ColBranch getBranchById(Long id);
    
    /**
     * 通过模板id取得分支条件
     * @param id
     * @return
     */
    public List<ColBranch> getBranchsByTemplateId(Long id,int appType);
    
    /**
     * 通过模板id和link id取得分支条件
     * @param id
     * @return
     */
    public ColBranch getBranchByTemplateAndLink(int appType,long templateId,long linkId);
    
    /**
     * 通过模板formParentId取得所有表单个人子模版
     * @param id
     * @return
     */
    public List<Templete> getTemplateByformParentId(Long formParentId);
    
    /**
     * 关联某个项目的协同模板
     * 
     * @param projectId
     * @param size 当size为-1时，采用自动分页
     * @return
     */
    public List<Templete> getTempleteByPropectId(long projectId, int size);

    /**
     * 检查是否有重复的个人协同模板
     * @param tempName
     * @param tempId
     * @return
     */
    public boolean isTempleteUnique(String tempName,Long tempId);
    /**
     * 判断用户是否有调用某模板的权限
     * @param tempId
     * @param userId
     * @return
     */
    public boolean hasAccSystemTempletes(Long tempId,Long userId);
    
    public boolean hasAccSystemTempletes(Long tempId,Long userId, List<Long> domainIds);
    
    /**
     * 校验模板是否存在，用于AJAX调用
     * @param templeteId 模板ID
     * @return
     */
    public boolean checkTempleteIsExist(long templeteId);
    
    /**
     * 模板校验,输出校验结果。如果没有问题，可以调用，返回null.否则返回错误信息
     * @param templeteId
     * @param userId
     * @return
     */
    public String checkTemplete(Long templeteId,Long userId);
    
    /**
     * 通过模板编号获取模板对象
     * @param templeteCode   模板编号
     * @return               模板对象
     */
    public Templete getTempleteByCode(String templeteCode);
    
    /**
     * 校验模板编号是否唯一
     * @param templeteIdStr 模板ID，为空则为新建，否则为修改
     * @param templeteCode 模板编号
     * @return
     */
    public boolean checkTempleteCodeIsUnique(String templeteIdStr, String templeteCode);
    
    /**
     * 修改表单所属人时同步修改模板的所属人
     * @param templeteId
     * @param newMemberId
     * @throws DataDefineException 
     */
	public void updateMemberId(List<Long> templeteIdlist, Long newMemberId) throws DataDefineException ;
	
	/**
	 * 获取表单业务配置对应的表单模板，仅用于前端展现，不取全部字段（包括大字段）
	 * @param bizConfigId 	表单业务配置ID
	 * @param domainIds 	当前用户对应的各种组织模型实体ID集合
	 */
	public List<Templete> getTempletes4BizConfig(Long bizConfigId, List<Long> domainIds);
	
	public boolean checkTempletes4BizConfigIsEmpty(Long bizConfigId);
	
	/**
	 * 获取表单业务配置对应的表单模板，不进行权限过滤，<b>仅用于前端展现</b>，Templete中仅包含id、subject和排序号属性
	 */
	public List<Templete> getTempletes4BizConfigWithoutAuthCheck(Long bizConfigId);
	
	/**
	 * 获取表单业务配置对应的表单模板，需要获取表单模板对应的表单信息用于前端展现
	 * @param bizConfigId	业务配置ID
	 * @param domainIds		当前用户对应的各种组织模型实体ID集合
	 * @see com.seeyon.v3x.formbizconfig.controller.FormBizConfigController#showTempletes4NewCol(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public List<Templete> getTempletesWithFormInfo4BizConfig(Long bizConfigId, List<Long> domainIds);
	
	/**
	 * 根据表单模板ID获取对应的模板
	 */
	public List<Templete> getTempletesByIds(List<Long> ids);
	
	/**
     * 获取用户有权使用、按照指定条件查询的全部表单模板
     * @param memberId 		用户ID
     * @param condition		查询条件类型
     * @param textfield		查询条件值：按照表单模板名称进行查询 or 按照表单模板所属应用类型进行查询
     */
    public List<Templete> getSysFormTempsByMemberId(Long memberId, String condition, String textfield);
	
	/**
     * 获取指定单位按照指定条件查询的全部表单模板
     * @param accountId 	指定单位ID
     * @param condition		查询条件类型
     * @param textfield		查询条件值：按照表单模板名称进行查询 or 按照表单模板所属应用类型进行查询
     */
	public List<Templete> getAllSystemTempletesInAccount(Long accountId, String condition, String textfield, Integer... type);

	/**
	 * 根据id取得模板.批量取。
	 * TODO 这里采用in 的方式。如果超过id超过200，将采用or in的方式。性能值得商榷
	 * @param ids
	 * @return
	 */
	public List<Templete> getListByIds(List<Long> ids);
	
	/**
	 * 通过事项查询对应的模板
	 * @param memberId  人员id
	 * @param app       应用分类
	 * @param state     事项状态
	 * @return
	 */
	public List<Templete> getTemplatesByAffair(Long memberId, int app, int state, String searchType, String textfield);

	/**
	 * 通过用户id查询该包含该用户的所有模板列表
	 * @param userid 用户id
	 * @return
	 */
	public List getListByUserId(String userid);
	
	/**
	 * 得到数据库中所有系统模板的名字和所属人。
	 * @return key:模板ID   value ： 模板名字(subject)|所属人ID(memberId)
	 */
	public Map<Long,SimpleTemplete> getSystemTempleteSimpleInfo();

}