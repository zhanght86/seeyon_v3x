package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.doc.domain.DocAlert;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.Constants;

public interface DocAlertManager {
	/**
	 * 订阅文档
	 * @param alertOprType    订阅操作类型 Constants.SUBSCRIBE_OPR_TYPE_XXX
	 * @param alertUserType  	订阅用户类型 Constants.SUBSCRIBE_USER_TYPE_XXX
	 * @param alertUserId		订阅用户id
	 * @param createUserId	创建订阅的用户id
	 * @param docAclId		个人共享的doc_acl记录id，非个人共享时直接传null
	 */
	public long addAlert(Long docResourceId, boolean isFolder, byte alertOprType, 
			String alertUserType, Long alertUserId, Long createUserId, boolean sendMessage,boolean setSubFolder, boolean isFromAcl);
	
	/**
	 * 修改文档订阅类型
	 */
	public void updateAlertOprType(Long alertId, byte newAlertOprType, boolean sendMessage) throws DocException;
	
	/**
	 * 根据文档ID删除通过共享授权而产生的订阅记录
	 * @param docResId	文档ID
	 */
	public void deleteAlertByDrIdFromAcl(Long docResId);
	
	/**
	 * 根据文档订阅id删除订阅
	 */
	public void deleteAlertById(Long alertId);
	public void deleteAlertsByIds(String alertIds);
	
	/**
	 * 根据DocResourceId删除该文档所有订阅
	 * 如果是文档夹，则删除它下面所有内容的订阅
	 */
	public void deleteAlertByDocResourceId(DocResource dr);
//	public void deleteAlertByDocAndUser(DocResource dr,Long userId);
	public void deleteAlertByDocResourceIdOfCurrentUesr(long docResId) ;
	/**
	 * 删除库下面所有的订阅
	 * @param docResId
	 * @param orgType
	 * @param orgId
	 */
	public void deleteAllAlertByDocResourceIdAndOrg(DocResource doc, String orgType, long orgId);
	/**
	 * 判断某文档是否被订阅
	 * @param doc
	 * @param userId
	 * @return
	 */
	public boolean  hasAlert(DocResource doc, long userId);
	
	public void deleteAlertByDocResourceIdAndOrg(long docResId, String orgType, long orgId) ;
	/**
	 * 根据文档Id删除订阅类型为评论的订阅项
	 * @param docResId
	 */
	public void deleteAlertByDocResourceIdAndAlertType(long docResId );
	
	/**
	 * 删除通过共享产生的订阅
	 */
	public void deleteAlertByDocResourceIdAndOrgByAcl(long docResId, String orgType, long orgId);
	
	/**
	 * 删除通过共享产生的订阅，批量删除
	 */
	public void deleteAlertByDocResourceIdAndOrgByAclForBatch(List<Long> lists, String orgType, long orgId);
	/**
	 * 根据DocResourceId删除该文档所有个人共享产生的订阅
	 */
	public void deleteShareAlertByDocResourceId(Long docResourceId);	
	
	/**
	 * 根据DocAlertId查询文档订阅 
	 */
	public DocAlert findAlertById(Long alertId);
	public List<DocAlert> findAlertsByIds(String alertIds);
	/**
	 *判断是否有订阅项
	 * @param alertIds
	 * @return
	 */
	public boolean hasAlert(String alertIds);
	public DocAlert getAlertByDocIdAndOrgOfShare(long docResourceId, String orgType, long orgId);
	
	/**
	 * 根据docResourceId, userId查询文档订阅 
	 */
	public List<DocAlert> findPersonalAlertByDrIdOfCurrentUser(Long docResId) ;
	/**
	 * 查询当前用户的所有订阅，包含部门订阅
	 * @return Map<组织模型id， List<DocAlert>>
	 */
	public Map<Long, List<DocAlert>> findAllAlertsByDrIdOfCurrentUser(Long docResId) throws DocException;
	
	/**
	 * 根据DocResource查询文档订阅
	 * 当alertOprTypes不传时，默认为查询所有类型的订阅
	 */
	public List<DocAlert> findAlertsByDocResourceId(DocResource dr, Byte... alertOprTypes);
	
	/**
	 * 查询用户的所有订阅
	 * 当alertOprTypes不传时，默认为查询所有类型的订阅
	 */
	public List<DocAlert> findAlertsByUserId(String userType, Long userId, byte... alertOprTypes) throws DocException;
//	public List<DocAlert> findAlertsByUserIdByPage(String userType, Long userId, byte... alertOprTypes) throws DocException;
	public List<List<DocAlert>> findAllAlertsOfCurrentUserByPage() ;
	public List<DocAlert> findAlertsByUserIdAndDocResId(String userType, Long userId, Long docResId) throws DocException;
	/** 
	 * 找到所有需要消息提醒的用户
	 */
	public List<DocAlert> getMsgAlert(Long docResourceId) throws DocException; 
	
	/**
	 * 
	 * 
	 */
	public void init();
	
	public List<DocResource> getSubFolderIds(Long docResId ,DocResource dr);
	
	//public List<DocResource> getSubResouceIds(Long docResId ,DocResource dr);
	
	/**
	 * 更新文档订阅到最新列表
	 */
	public void addToLatest(Long docResourceId, Long alertUserId);

	/**
	 * 修改项目时，将修改前的项目人员订阅信息从项目文档订阅信息记录中删除(避免将授权项目组人员之外的其他订阅信息删除)
	 * @param projectFolder			项目文档
	 * @param oldProjectMemberIds	修改前的项目人员ID集合
	 */
	public void deleteProjectFolderAlert(DocResource projectFolder, List<Long> oldProjectMemberIds);
	
}

