package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.PotentModel;

public interface DocAclManager {
	// //////////////////////个人共享//////////////////////
	/**
	 * 设置个人共享权限
	 * 
	 * @param userId
	 * @param userType
	 * @param doc
	 * @param ownerId
	 */
	public Long setPersonalSharePotent(Long userId, String userType,
			Long docId, Long ownerId, Long alertId);

	/**
	 * 查询共享所有人列表
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return Set<Long>
	 */
	public Set<Long> getShareUserIds(String userIds);

	/**
	 * 查询共享所有人列表（分页）
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return Set<Long>
	 */
	public Set<Long> getShareUserIdsPage(String userIds, int currentPage,
			int pagesize);

//	/**
//	 * 查询共享所有人列表记录数
//	 * 
//	 * @param userIds
//	 * @return
//	 */
//	public int getShareUserIdsCount(String userIds);

	/**
	 * 根据所有人查询共享第一级文档夹
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @param ownerId文档夹所有人id
	 * @return List<DocResource>
	 */
	public List<DocResource> getShareRootDocs(String userIds, Long ownerId);

	/**
	 * 根据所有人查询共享第一级文档夹（分页）
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @param ownerId文档夹所有人id
	 * @return List<DocResource>
	 */
	public List<DocResource> getShareRootDocsPage(String userIds, Long ownerId,
			int currentPage, int pagesize);

//	/**
//	 * 根据所有人查询共享第一级文档夹记录数
//	 * 
//	 * @param userIds
//	 * @param ownerId
//	 * @return
//	 */
//	public int getShareRootDocsCount(String userIds, Long ownerId);

	// /**
	// * 查找个人共享文件夹的下级
	// *
	// * @param docResourceId
	// * @return
	// */
	// public List<DocResource> getShareNextDocs(Long docResourceId);

	/**
	 * 删除共享权限
	 * 
	 * @param docAclId
	 */
	public void deleteShareDoc(Long docAclId);

	/**
	 * 根据文档资源id获取个人共享权限列表
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocAcl> getPersonalShareList(Long docResourceId);

	/**
	 * 根据文档资源id获取个人共享继承的权限列表
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocAcl> getPersonalShareInHeritList(Long docResourceId);

	/**
	 * 删除个人共享
	 * 
	 * @param docResourceId
	 */
	public void deletePersonalShare(Long docResourceId);
	
	public void deletePersonalShare(Long docResourceId , List<Long> userIds , boolean isPersonLib) ;

	// public List<DocResource> getPersonalShareDocsByCondition(DocResource
	// doc,String condition);

	// //////////////////////个人共享//////////////////////

	// //////////////////个人借阅/////////////////////

	/**
	 * 设置个人借阅权限
	 * 
	 * @param userId
	 * @param userType
	 * @param doc
	 * @param ownerId
	 * @param sdate
	 * @param esdate
	 */
	public void setNewPersonalBorrowPotent(Long userId, String userType,
			Long docId, Long ownerId, java.sql.Timestamp sdate,
			java.sql.Timestamp esdate,Byte lenPotent,String lenPotent2);

	/**
	 * 更新个人借阅权限
	 * 
	 * @param docAclId
	 * @param sdate
	 * @param esdate
	 */
	public void updatePersonalBorrowPotent(Long docAclId,
			java.sql.Timestamp sdate, java.sql.Timestamp esdate);

	/**
	 * 查询借阅所有人id列表
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return Set<Long>
	 */
	public Set<Long> getBorrowUserIds(final String userIds);

	/**
	 * 查询借阅所有人id列表(分页)
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return Set<Long>
	 */
	public Set<Long> getBorrowUserIdsPage(final String userIds, int currentPage,
			int pagesize);

	/**
	 * 查询借阅所有人id列表记录数
	 * 
	 * @param userIds
	 * @return
	 */
//	public int getBorrowUserIdsCount(String userIds);

	/**
	 * 根据所有人查询借阅文档
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @param ownerId文档所有人id
	 * @return List<DocResource>
	 */

//	public List<DocResource> getBorrowDocs(final String userIds, final Long ownerId);

	/**
	 * 根据所有人查询借阅文档(分页)
	 * 
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @param ownerId文档所有人id
	 * @return List<DocResource>
	 */

	public List<DocResource> getBorrowDocsPage(final String userIds, final Long ownerId,
			int currentPage, int pagesize);

	/**
	 * 根据所有人查询借阅文档记录数
	 * 
	 * @param userIds
	 * @param ownerId
	 * @return
	 */
//	public int getBorrowDocsCount(String userIds, Long ownerId);

	/**
	 * 删除借阅
	 * 
	 * @param acl权限对象
	 */
	public void deleteBorrowDoc(Long docAclId);

	/**
	 * 根据文档资源id获取个人借阅的权限列表
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocAcl> getPersonalBorrowList(final Long docResourceId);

	// public List<DocAcl> getPersonalBorrowInHeritList(Long docResourceId);
	// //////////////////个人借阅/////////////////////

	// /////////////////////单位借阅////////////////

	/**
	 * 设置单位借阅权限
	 * 
	 * @param userId
	 * @param userType
	 * @param doc
	 * @param ownerId
	 * @param sdate
	 * @param esdate
	 */
	public void setNewDeptBorrowPotent(Long userId, String userType,
			Long docId, java.sql.Timestamp sdate, java.sql.Timestamp edate,Byte lenPotent,String lenPontent);

	/**
	 * 更新单位借阅权限
	 * 
	 * @param docAclId
	 * @param sdate
	 * @param esdate
	 */
	public void updateDeptBorrowPotent(Long docAclId, java.sql.Timestamp sdate,
			java.sql.Timestamp esdate);

	/**
	 * 是否有单位借阅文档
	 * 
	 * @param userId
	 * @param deptId
	 * @param groupId
	 * @return
	 */
//	public boolean hasDeptBorrow(String userIds);

	/**
	 * 获取单位借阅文档
	 * 
	 * @param userId
	 * @param deptId
	 * @param groupId
	 * @return
	 */

//	public List<DocResource> getDeptBorrowDocs(String userIds);

	/**
	 * 获取单位借阅文档(分页)
	 * 
	 * @param userIds
	 * @return
	 */

	public List<DocResource> getDeptBorrowDocsPage(final String userIds,
			int currentPage, int pagesize);

	/**
	 * 获取单位借阅文档记录数
	 * 
	 * @param userIds
	 * @return
	 */
	public int getDeptBorrowDocsCount(final String userIds);

	/**
	 * 根据文档资源id获取单位借阅权限列表
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocAcl> getDeptBorrowList(final Long docResourceId);

	// public List<DocAcl> getDeptBorrowInHeritList(Long docResourceId);

	// /////////////////////单位借阅////////////////

	// ///////////////单位共享////////////////

	/**
	 * 设置文档库成员（分配共享权限后调用本接口）
	 * 
	 * @param docLibId
	 * @param userId
	 * @param userType
	 */
	public void setLibMember(Long docLibId, Long userId, String userType);

	/**
	 * 根据文档库id删除文档库成员
	 * 
	 * @param docLibId
	 */
	public void deleteLibMember(Long docLibId, String userIds);

//	/**
//	 * 设置文档库成员（修改共享权限后调用本接口）
//	 * 
//	 * @param userId
//	 * @param userType
//	 */
//	public void setLibMember(Long userId, String userType);

//	/**
//	 * 自定义文档库id显示列表
//	 * 
//	 * @param uids
//	 * @return
//	 */
//	public Set<Long> getDocLibIds(String uids);

	/**
	 * 获取文档资源的权限列表
	 * 
	 * @param doc文档资源对象
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return Set<Integer>
	 */
	public Set<Integer> getDocResourceAclList(DocResource doc, String userIds);
	public Set<Integer> getDocResourceAclList(long docId, String userIds);
	public Set<Integer> getDocResourceAclList(DocResource doc, String userIds, Map<Long, Set<Integer>> aclMap);
	
	/**
	 * 取得权限数据
	 * 根据组织模型id进行了区分
	 * @author lihf
	 */
	public Map<Long, Set<Integer>> getGroupedAclSet(DocResource doc, String orgIds);

//	/**
//	 * 获取文档资源的权限列表
//	 * 
//	 * @param doc
//	 * @return
//	 */
//	public List<DocAcl> getDocAclListByDoc(DocResource doc);

	// /**
	// * 获取登陆人员的根资源
	// *
	// * @param userId人员id
	// * @param depId部门id
	// * @param groupId组id
	// * @return List<DocResource>
	// */
	// public List<DocResource> getRootResource(Long userId, Long depId,
	// Long groupId);

	/**
	 * 
	 * 设置不继承上级权限
	 * 
	 * lihf: 不继承不用设置，有权限记录便代表从此处开始不继承，
	 *       原来的 potentType = 9 应该代表从此处开始 没有权限
	 * 
	 * @param doc
	 */
//	public void setPotentNoInherit(Long userId, String userType, Long docId, boolean isAlert, Long alertId);

	/**
	 * 设置继承上级权限
	 * 
	 * @param doc
	 */
	public void setPotentInherit(Long docId, byte docLibType, long docLibId);

	/**
	 * 查询树的下级文件夹
	 * 
	 * @param doc文件夹
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return List<DocResource>
	 */
	public List<DocResource> findNextNodeOfTree(DocResource doc, String userIds);
	
	/**
	 * 查询下级文档夹不过滤权限
	 * @param doc文件夹
	 */
	public List<DocResource> findNextNodeOfTreeWithOutAcl(DocResource doc);

	/**
	 * 查询表的下级文档夹及文档
	 * 
	 * @param doc文件夹
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return List<DocResource>
	 */
//	public List<DocResource> findNextNodeOfTable(DocResource doc, String userIds);

	/**
	 * 查询表的下级文档夹及文档(分页)
	 * 
	 * @param doc文件夹
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return List<DocResource>
	 */
	public List<DocResource> findNextNodeOfTablePage(DocResource doc,
			String userIds, int currentPage, int pageSize,String... type);

	/**
	 * 从首页查询文档下级文档及文档夹
	 * @param doc
	 * @param userIds
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public List<DocResource> findNextNodeOfTablePageByDate(final DocResource doc,
			String userIds, int currentPage, int pageSize); 
//	/**
//	 * 查询表的下级文档夹及文档记录数
//	 * 
//	 * @param doc
//	 * @param userIds
//	 * @return
//	 */
//	public int findNextNodeOfTableCount(DocResource doc, String userIds);

	/**
	 * 删除文档资源时删除权限
	 * 如果是文档夹，删除下面所有权限记录
	 * @param doc资源对象
	 */
	public void deletePotent(DocResource doc);

//	/**
//	 * 得到符合条件且有权限的doc
//	 * 
//	 * @param list符合条件的doc的list
//	 * @param userId人员id
//	 * @param depId部门id
//	 * @param groupId组id
//	 * @return List<DocResource>
//	 */
//	public List<DocResource> getResourcesByConditionAndPotent(
//			List<DocResource> list, String userIds);

	/**
	 * 得到符合条件且有权限的doc(分页)
	 * 
	 * @param list符合条件的doc的list
	 * @param userId人员id
	 * @param depId部门id
	 * @param groupId组id
	 * @return List<DocResource>
	 */
	public List<DocResource> getResourcesByConditionAndPotentPage(
			List<DocResource> list, DocResource parent, String userIds, int currentPage,
			int pageSize,String... type);
	
	/**
	 * lihf 2008.03.20
	 * 不传 DocResource 对象，改传 logicalPath 集合
	 * 暂供综合查询使用
	 */
	public List<DocResource> getResourcesByConditionAndPotentPageNoDr(
			Map<Long, String> docMap, String userIds, int currentPage,
			int pageSize);

	/**
	 * 得到符合条件且有权限的doc记录数
	 * 
	 * @param list
	 * @param userIds
	 * @return
	 */
//	public int getResourcesByConditionAndPotentCount(List<DocResource> list,
//			String userIds);
//
//	/**
//	 * 
//	 * @param list
//	 * @param userIds
//	 * @return
//	 */
//	public List<Long> getResourcesByConditionAndPotent2Quanwen(
//			List<String> list, String userIds);

//	/**
//	 * 根据文档id获取可查看人员列表（全文检索）
//	 * 
//	 * @param docId
//	 * @return
//	 */
//	public List<Long> getOwnerListByDocId(Long docId);
//
//	/**
//	 * 根据文档id获取可查看部门列表（全文检索）
//	 * 
//	 * @param docId
//	 * @return
//	 */
//	public List<Long> getDeptListByDocId(Long docId);
//
//	/**
//	 * 根据文档id获取可查看岗位列表（全文检索）
//	 * 
//	 * @param docId
//	 * @return
//	 */
//	public List<Long> getPostListByDocId(Long docId);
//	/**
//	 * 获取有权限的用户\部门\岗位列表（全文检索）
//	 * @param docId
//	 * @return
//	 */
//	public Map<String, List<Long>> getUserListByDocId(Long docId);
//	/**
//	 * 获取权限改变的全部下级文档资源id（非文档夹）
//	 * 
//	 * @param docId
//	 * @return
//	 */
//	public List<Long> getChangedDoc(Long docId);

//	/**
//	 * 得到符合条件且有权限的doc
//	 * 
//	 * @param hsql查询符合条件的文档资源的sql
//	 * @param userId人员id
//	 * @param depId部门id
//	 * @param groupId组id
//	 * @return List<DocResource>
//	 */
//	public List<DocResource> getResourcesByConditionAndPotent(String hsql,
//			String userIds);

//	/**
//	 * 查询拥有权限的doc的hashtable
//	 * 
//	 * @param userId人员id
//	 * @param depId部门id
//	 * @param groupId组id
//	 * @return Hashtable<Long, DocResource>
//	 */
//	public Hashtable<Long, Long> getDocResourcesByPotent(String userIds);
//
//	/**
//	 * 查询拥有权限的doc的hashtable(分页)
//	 * 
//	 * @param userId人员id
//	 * @param depId部门id
//	 * @param groupId组id
//	 * @return Hashtable<Long, DocResource>
//	 */
//	public Hashtable<Long, Long> getDocResourcesByPotentPage(String userIds,
//			int currentPage, int pageSize);

	/**
	 * 查询拥有权限的doc的hashtable的记录数
	 * 
	 * @param userIds
	 * @return
	 */
//	public int getDocResourcesByPotentCount(String userIds);

	/**
	 * 判断是否有权限删除文档夹/文档
	 * 
	 * @param doc
	 * @param userId
	 * @param deptId
	 * @param groupId
	 * @return
	 */
	public boolean canBeDelete(DocResource doc, String userIds);

//	/**
//	 * 获取以某一节点为中心的子树上的全部权限（除不继承、列表）
//	 * 
//	 * @param doc
//	 * @param userId
//	 * @param deptId
//	 * @param groupId
//	 * @return
//	 */
//	public List<DocAcl> getTreeDocAcl(DocResource doc);

//	/**
//	 * 设置继承
//	 * 
//	 * @param docId
//	 */
//	public void docInherit(Long docId);

//	/**
//	 * 设置不继承
//	 * 
//	 * @param docId
//	 * @param userId
//	 * @param userType
//	 */
//	public void docNoInherit(Long docId, Long userId, String userType);

	/**
	 * 删除权限
	 * 
	 * @param docId
	 * @param userId
	 * @param userType
	 */
	public void deletePotentByUser(Long docId, Long userId, String userType, byte docLibType, long docLibId);
	/**
	 * 删除权限
	 * 
	 * @param docId
	 * @param userId
	 * @param userType
	 */
	
	public void deletePotentByMaUser(Long docId, Long userId, String userType, byte docLibType, long docLibId);
//	/**
//	 * 删除权限
//	 * 
//	 * @param docId
//	 * @param userId
//	 * @param userType
//	 */
//	public void deletePotentByUser(Long docId, String userIds);

	/**
	 * 删除权限
	 * 
	 * @param aclId
	 */
	public void deletePotentByUser(Long aclId);

	/**
	 * 设置单位共享权限
	 * 
	 * @param userId
	 * @param userType
	 * @param doc
	 */
	public void setDeptSharePotent(Long userId, String userType, Long docId,
			int potenttype, boolean isAlert, Long alertId, int minOrder);

	/**
	 * 根据文档资源id获取单位共享继承的权限
	 * 
	 * @param docId
	 * @return
	 */
	public List<DocAcl> getDocAclListByInherit(Long docId);

	/**
	 * 根据文档资源id获取单位共享非继承的权限
	 * 
	 * @param docId
	 * @return List<List>: 根据 userId，userType进行分类了的List<DocAcl>
	 */
	public List<List<DocAcl>> getDocAclListByNew(Long docId);

	/**
	 * 删除权限
	 * 
	 * @param docResourceId
	 * @param userId
	 * @param userType
	 * @param potent
	 */
	public void deletePotentByUser(Long docResourceId, Long userId,
			String userType, int potent);

	/**
	 * 判断文档资源权限是否非继承
	 * 
	 * @param userId
	 * @param userType
	 * @param docResourceId
	 * @return
	 */
	public boolean isNoInherit(Long userId, String userType, Long docResourceId);

	/**
	 * 删除借阅
	 * 
	 * @param docResourceId
	 * @param isMine
	 */
	public void deleteBorrow(Long docResourceId, boolean isMine);

	public void deleteBorrow(Long docResourceId, List<Long> userid, boolean isMine);
	/**
	 * 判断是不是有此人的在此文档的借阅
	 * @param docResourceId
	 * @param userid
	 * @return
	 */
	public boolean hasAclertBoorrow(Long docResourceId ,Long userid ,boolean isMine );
	
	public boolean hasAclertShare(Long docResourceId ,Long userid ,boolean isMine );

//	/**
//	 * 根据文档资源获取继承权限的用户
//	 * 
//	 * @param docResourceId
//	 * @return 用户id，用户type
//	 */
//	public Map<Long, String> inheritUser(Long docResourceId);

	/**
	 * 根据文档资源id删除单位权限记录
	 * 
	 * @param docResourceId
	 */
	public void deleteDeptShareByDoc(Long docResourceId);
	
	/**
	 * 找到对当前文档有某种权限的用户
	 * 返回Map<userId, userType>
	 * @deprecated	直接穿入dr即可，减少一条重复、无谓的sql
	 */
	public Map<Long, String> getSpecialAclsByDocResourceId(Long docResourceId, Set<Integer> aclLevels);
	
	/**
	 * 找到对当前文档有某种权限的用户
	 * 返回Map<userId, userType>
	 */
	public Map<Long, String> getSpecialAclsByDocResourceId(DocResource dr, Set<Integer> aclLevels);
	
	/**
	 * 取消权限记录里的订阅标记
	 */
	public void cancelAlertByAlertIds(String alertIds);
	
	/**
	 * 取得某个文档夹的共享数据
	 */
	public List<PotentModel> getGrantVOs(long docResourceId, boolean isGroupRes) ;
	
	/**
	 * 得到某个文档的某种共享类型的权限列表
	 */
	public List<DocAcl> getAclList(long docResId, byte sharetype);
	
	public List<DocAcl> getAclListByPotent(long docResId, int potenttype ,long userId);
	
	
	public boolean  hasAcl(DocResource  dr , long parentId);
	
	/**
	 * 批量取得某个权限组的文档（夹）本身的权限，不涉及继承
	 * Map<docId, Set<DocAcl>>
	 */
	public Map<Long, Set<DocAcl>> getAclSet(String docIds, String orgIds);
	
	public void init();
	
	/**
	 * 取得公文借阅权限
	 * 
	 */
	public String getEdocBorrowPotent(final long docId);
	/**
	 * 取得公文共享权限
	 * @param docId
	 * @return
	 */
	public String getEdocSharePotent(final long docId);
	
	/**
	 *取借阅文档权限
	 */
	public String getBorrowPotent(final long docId);
	
	/**
	 * 根据订阅id得到对应的授权对象
	 */
	public List<DocAcl> getAclListByAlertId(List<Long> alertIds);
	
	/**
	 * 得到一个
	 * “all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list”
	 * 格式的权限串
	 * 
	 * 在js中调用ajax使用
	 */
	public String getAclString(long docId);
	/**
	 * 是否有打开权限
	 * @param docId
	 * @return
	 */
	
	public boolean hasOpenAcl(long docId);
	/**
	 * 取个人文档库根目录
	 * @param userId
	 * @return
	 */
	
	public DocResource getPersonalFolderOfUser(long userId);
	/**
	 * 判断是否对各级子文档夹是否均有删除权限
	 */
	public  String hasAclToDeleteAll(DocResource doc,Long userId);

	/**
	 * 修改项目时，将修改前的项目人员共享授权信息从项目文档共享授权信息记录中删除(避免将授权项目组人员之外的其他共享授权信息被一起删除)
	 * @param projectFolderId		项目文档ID
	 * @param oldProjectMemberIds	修改前的项目人员ID集合
	 */
	public void deleteProjectFolderShare(Long projectFolderId, List<Long> oldProjectMemberIds);


	public int getMaxOrder();
}
