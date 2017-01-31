package com.seeyon.v3x.collaboration.templete.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;

/**
 * 首页模板配置
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public interface TempleteConfigManager
{
    public TempleteConfig getTempleteConfig(long id);

    public void save(TempleteConfig templeteConfig);

    /**
     * 将某人所选模板推送到首页
     * @param templeteIds  所选模板ID的List
     */
    public void pushTempletesToMain(Long memberId, Long[] templeteIds, int[] types);

    /**
     * 将当前的一个模板推送到首页（用于协同－另存为模板）
     * @param templeteId  模板ID
     * @param type 模板分类 个人为-1
     */
    public void pushThisTempleteToMain4Member(Long memberId, Long templeteId, int type);

    /**
     * 将当前模板推送到首页（用于协同模板）<br>
     * @param memberIdsList 人员ID list
     * @param templeteId  模板ID
     * @param type 模板分类 个人为-1
     */
    public void pushThisTempleteToMain4Members(List<Long> memberIdsList, Long templeteId, int type);

    /**
     * 取消发布到首页<br>
     * @param templeteIds 所选模板ID的List
     */
    public void cancelPush(List<Long> configIds);

    /**
     * 根据模板ID删除模板配置<br>
     * @param templeteId 模板ID
     */
    public void clearConfigByTempleteId(Long templeteId);

    /**
     * 首页模板排序<br>
     * @param TempleteConfigIds 模板ID的有序数组
     */
    public void sortTemplete(Long[] templeteConfigIds);

    /**
     * 取得用户发送到首页的所有模板(含查询条件) 成发集团重写 getConfigTempletes 程炯
     * @return
     */
    public List<TempleteConfig> getConfigTempletes(long memberId,String type,String value,String value1,String secretLevel,Object... isNotPagination);
    /**
     * 取得用户发送到首页的所有模板(含查询条件)
     * @return
     */
    public List<TempleteConfig> getConfigTempletes(long memberId,String type,String value,String value1, Object... isNotPagination);

    /**
     * 取得用户发送到首页的所有模板
     * @return
     */
    public List<TempleteConfig> getConfigTempletes(long memberId, Object... isNotPagination);
    
    /**
     * 取得用户发送到首页的所有模板 成发集团项目 程炯  重写getConfigTempletes
     * @return
     */
    public List<TempleteConfig> getConfigTempletes(long memberId,String secretLevel, Object... isNotPagination);

    /**
     * 取得用户发送到首页的模板　包括协同和公文表单<br>
     * 返回指定数目的结果
     * @param count 结果数量
     */
    public List<TempleteConfig> getConfigTempletes(long memberId, int count);

    /**
     * 取得用户发送到首页的协同模板<br>
     * @param memberId 人员ID
     */
    public List<TempleteConfig> getConfigTempletesOfColl(long memberId);


    /**
     * 取得用户发送到首页的公文模板<br>
     * @param memberId 人员ID
     */
    public List<TempleteConfig> getConfigTempletesOfEdoc(long memberId, long accountId);


    /**
     * 取得用户发送到首页的表单模板<br>
     * @param memberId 人员ID
     */
    public List<TempleteConfig> getConfigTempletesOfForm(long memberId);

    /**
     * 清除某用户的所有模板配置<br>
     * 一般用在人员调离原单位后调用
     * @param memberId
     */
    public void clearTempleteConfig(Long memberId);

    /**
     * 推送所有授权给我的模板到我的首页
     * @param memberId
     */
    public void pushAvailabileTemplete4Member(Long memberId);

    /**
     * 修正模板配置信息<br>
     * <li>清理-我已配置但目前没有权限访问的系统模板
     * <li>给新注册人员添加模板配置
     * @param memberId
     */
    public void redressalTempleteConfig(Long memberId);

    /**
	 * 组织模型调整后，推送授权给新组织实体的模板到某人的个人首页
	 * @param memberId
	 * @param departmentId
	 */
	public void pushNewOrgEntityTemplete4Member(String orgType, Long memberId, Long departmentId);

	/**
	 * 得到个人模板配置
	 * @param userId
	 * @param isPage
	 * @return
	 */
	public List<TempleteConfig> getPersonTempleteConfig(Long userId,boolean isPage);

	/**
	 * 为首页栏目分类查询增加方法
	 * @param memeberId
	 * @param count
	 * @param category
	 * @return
	 */
	public List<TempleteConfig> getConfigTempletesByCategory(long memeberId, int count, String category);
	//重写getConfigTempletesByCategory
	public List<TempleteConfig> getConfigTempletesByCategory(long memeberId, int count, String category,String secretLevel);
	
	/**
	 * 首页模板更多根据名称查询（含模板分类过滤）
	 * 
	 * @param memberId 人员ID
	 * @param add_onsCondition 查询条件
	 * @param nameParameters 参数值
	 * @param count 条数
	 * @param hasPersonal 是否包括个人模板
	 * @param category 模板过滤
	 * @param isNotPagination 是否分页
	 * @return
	 */
	public List<TempleteConfig> getMyTempletesByCategory(long memberId, String add_onsCondition, Map<String, Object> nameParameters,
			int count, boolean hasPersonal, String category, Object... isNotPagination);
	//重写getMyTempletesByCategory
	public List<TempleteConfig> getMyTempletesByCategory(long memberId, String add_onsCondition, Map<String, Object> nameParameters,
			int count, boolean hasPersonal, String category,String secretLevel,Object... isNotPagination);
}
