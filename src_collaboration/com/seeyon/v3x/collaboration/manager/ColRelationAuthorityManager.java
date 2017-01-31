package com.seeyon.v3x.collaboration.manager;


/**
 * @author DEV23
 * 
 */
public interface ColRelationAuthorityManager {

	/**
	 * 表单授权
	 * 
	 * @param summaryIds 协同id
	 * @param authorities 授权对象
	 * @return
	 */
	public boolean create(String[] summaryIds, String authorities) throws Exception;
	
	/**
	 * @param summaryId 协同id
	 * @return
	 * @throws Exception
	 */
	public String getAuthoritiesBySummaryId(String summaryId) throws Exception;
	

	/**
	 * @param summaryId 协同id
	 * @param isUpdateAffairAuth 是否更新节点的授权状态
	 * @return
	 * @throws Exception
	 */
	public boolean delete(Long summaryId,boolean isUpdateAffairAuthority) throws Exception;
}
