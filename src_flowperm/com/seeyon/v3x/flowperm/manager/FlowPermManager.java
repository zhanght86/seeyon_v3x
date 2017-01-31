package com.seeyon.v3x.flowperm.manager;

import java.util.List;

import com.seeyon.v3x.flowperm.domain.FlowPerm;

public interface FlowPermManager {
	/**
	 * 判断某单位是否需要更新
	 * @param accountId
	 * @return
	 */
	public boolean isNeedUpdateRef(Long accountId);
	
	/**
	 * 判断节点权限是否需要更新引用，需要才更新
	 * @return
	 */
	public boolean isNeedUpdateRef(String policy, Long accountId);
	
	public void addNodePolicy(String policy, Long accountId);
	
	/**
	 * 根据类别来查找节点权限的集合
	 * @param category 类别：类别：  		1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * 
	 * @param accountId     单位Id：默认为集团Id, 1L;
	 * @return
	 * @throws Exception
	 */
	public List<FlowPerm> getFlowPermsByCategory(String category, long accountId) throws Exception;
	
	
	/**
	 * 
	 * @param category 类别：类别：  		1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 *     
	 * @param status		状态：启用/停用 FlowPerm.Node_isActive / FlowPerm.Node_isNotActive
	 * @param accountId     单位Id：默认为集团Id, 1L;
	 * @return
	 * @throws Exception
	 */
	public List<FlowPerm> getFlowpermsByStatus(String category,int status, long accountId) throws Exception;
	
	
	
	public List<FlowPerm> getFlowpermsByRef(String category,int isRef, long accountId) throws Exception;
	
	
	/**
	 * 
	 * @param category 			 类别：  	1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * 
	 * @param status   			 状态：  启用/停用 FlowPerm.Node_isActive / FlowPerm.Node_isNotActive
	 * @param isIncludeBegin：	 是否包括首节点
	 * @param type 				 权限的类型： com.seeyon.v3x.flowperm.util.Constants.F_TYPE_COLLABORATION 协同
	 * 									    com.seeyon.v3x.flowperm.util.Constants.F_TYPE_EDOC 公文
	 * @param accountId   	     单位Id：默认为集团Id, 1L;
	 * @return
	 */
	public List<FlowPerm> getFlowpermsByStatus(String category,int status,boolean isIncludeBegin, int type, long accountId) throws Exception;

	/**
	 * 添加节点权限（协同）
	 * @param flowPerm
	 * @param accountId  单位Id
	 */
	public void addFlowPerm(FlowPerm flowPerm, long accountId);
	
	/**
	 * 添加节点权限（公文）公文权限需要额外添加节点权限授权得公文元素，所以与协同区分开
	 * @param flowPerm
	 * @param accountId  单位Id
	 */
	public void addEdocFlowPerm(FlowPerm flowPerm, long accountId);

	
	/**
	 * 更新节点权限
	 * @param flowPerm  权限
	 * @param category  类别：  			1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * @param categoryItem 名称：  	    shenhe/yuedu/collaboration...
	 */
	public void updateFlowPerm(FlowPerm flowPerm,String category,String categoryItem, long accountId);

	
	/**
	 * 根据类别与名称删某一特定的节点权限
	 * @param configCategory   类别：  	1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * 
	 * @param configItem      名称：     shenhe/yuedu/collaboration...
	 * @param accountId       单位Id
	 */
	public void deleteFlowPerm(String configCategory,String configItem, long accountId);

	/**
	 * 根据类别与名称获取某一特定的节点权限
	 * @param configCategory   类别：  	1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * 
	 * @param configItem      名称：     shenhe/yuedu/collaboration...
	 * @param accountId       单位Id
	 * @throws Exception
	 */
	public FlowPerm getFlowPerm(String configCategory,String configItem, long accountId) throws Exception;
	
	/**
	 * 根据id查找权限
	 * @param id : 节点权限的ID/v3xConfig的Id
	 * @return
	 * @throws Exception
	 */
	public FlowPerm getFlowPerm(Long id) throws Exception;
	
	/**
	 * 根据id删除
	 * @param id : 节点权限的ID/v3xConfig的Id
	 * @return 如果节点不允许删除（被引用）返回提示信息
	 */
	public String deleteFlowPerm(Long id);
	
	/**
	 * 判断某一操作在某一权限下是否可用
	 * @param uid : 节点权限的ID/v3xConfig的Id
	 * @param action 操作的名称
	 * @return
	 */
	public Boolean isActionAllowed(Long uid, String action);

	/**
	 * 得到某一权限下的所有操作集合
	 * @param uid : 节点权限的ID/v3xConfig的Id
	 * @param appType ：操作类型 Constants.basic/advanced/common
	 * @return
	 */
	public String getActionList(Long uid, String appType);
	
	/**
	 * 更新权限
	 * @param flowPerm
	 */
	public void updateFlowPerm(FlowPerm flowPerm);
	
	/**
	 * 检查是否重名
	 * @param configCategory  类别：  	1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * @param configItem  	  权限的名称：     shenhe/yuedu/collaboration...
	 * @param accountId   	  单位Id
	 * @return
	 * @throws Exception
	 */
	public boolean checkName(String configCategory,String configItem, long accountId) throws Exception;
	
	/**
     * branches_a8_v350_r_gov GOV-2863 常屹 新增 
     * 节点权限重名通过ajax进行判断
     */
	public boolean checkNameByAjax(String configCategory,String configItem) throws Exception;
	
	/**
	 * 
	 * @param configCategory 权限的类别： 1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * 									<<可用MetadataNameEnum.???category???.name();来得到>>
	 * @param configItem	 权限的名称：  shenhe/yuedu/collaboration...
	 * @param status 		 权限的状态：  启用/停用 FlowPerm.Node_isActive / FlowPerm.Node_isNotActive
	 * @param accountId      单位Id
	 * @return
	 */
	public FlowPerm getFlowPermByStatus(String configCategory,String configItem, Integer status, long accountId)throws Exception;
	public void refFlowPermForSmallKing(String configCategory, long accountId , Integer isRef)throws Exception;
	  //是否是系统节点权限
	public boolean isSystemFlowPerm(String name,long accountId);
	public void refFlowPerm(Long id, Long accountId, Integer isRef)throws Exception;
}
