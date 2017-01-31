package com.seeyon.v3x.bbs.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-11
 */
public interface BbsBoardManager {
	/**
	 * 按照板块ID获取对应讨论板块信息，从内存而不是数据库中读取
	 * @param boardId
	 */
	public V3xBbsBoard getBoardById(Long boardId) throws Exception;
	
	/**
	 * 判断讨论板块是否还存在
	 * @param boardId
	 */
	public boolean isBoardExist(Long boardId) throws Exception;
	
	/**
	 * 获取全部集团讨论板块
	 */
	public List<V3xBbsBoard> getAllGroupBbsBoard();
	
	/**
	 * 获取指定单位下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllCorporationBbsBoard(long accountId);
	
	/**
	 * 获取指定单位下自定义空间的全部讨论板块(集团/单位)
	 */
	public List<V3xBbsBoard> getAllCustomBbsBoard(long accountId) throws BusinessException;
	
	/**
	 * 获取指定自定义单位或集团下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllCustomAccBbsBoard(long spaceId, int spaceType);
	
	/**
	 * 获取指定部门下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllDeptBbsBoard(long deptId);
	
	/**
	 * 获取全部集团讨论板块的ID集合
	 */
	public List<Long> getAllGroupBbsBoardId();
	
	/**
	 * 获取指定单位的全部单位讨论板块ID集合
	 */
	public List<Long> getAllCorporationBbsBoardId(long accountId);
	
	/**
	 * 单位管理员或集团管理员在管理讨论版块时，获取分页列表
	 * @param isGroup   是否进行集团讨论设置
	 * @param accountId 如果进行单位讨论设置，对应的单位ID
	 */
	public List<V3xBbsBoard> getBbsBoards4Page(boolean isGroup, Long accountId);
	
	/**
	 * 自定义单位或集团管理员在管理讨论版块时，获取分页列表
	 */
	public List<V3xBbsBoard> getBbsBoards4Page(Long spaceId, int spaceType);
	
	/**
	 * 获取所有单位板块类型    
	 * PublicInfoMenuCheckImpl 调用判断是否显示菜单
	 * @deprecated 目前此方法已不被使用
	 */
	public List<V3xBbsBoard> getAllCorporationBbsBoard();
	
	/**
	 * 创建部门讨论区(如部门空间开通时，为其创建对应的部门讨论板块)
	 * @param departmentId    部门ID
	 * @param accountId		  单位ID
	 * @param departmentName  部门名称
	 */
	public void createDepartmentBbsBoard(Long departmentId, Long accountId, String departmentName);
	
	/**
	 * 保存讨论版块，设定版块管理员，包括持久化和内存同步
	 * @param board    待保存的新版块
	 * @param admins   版块管理员ID集合
	 */
	public void createV3xBbsBoard(V3xBbsBoard board, List<Long> admins);
	
	/**
	 * 保存讨论版块，设定版块管理员，完成持久化与内存同步
	 * @param board    待保存的新版块
	 * @param adminStr 版块管理员ID拼串
	 */
	public void createBbsBoard(V3xBbsBoard board, String adminStr);
	
	/**
	 * 更新讨论版块，设定版块管理员，完成持久化与内存同步
	 * @param board    待保存的新版块
	 * @param admins   版块管理员ID集合
	 */
	public void updateV3xBbsBoard(V3xBbsBoard board, List<Long> admins);
	
	/**
	 * 部门主管修改部门空间管理员时，讨论版块管理员需要同步更新
	 */
	public void updateDeptBBSBoardManager(Long deptId, String managers) throws BusinessException;
	
	/**
	 * 当单位管理员在进行部门修改操作时，会改变其部门主管、管理员信息，
	 * 需要将改动的部门主管信息同步到部门讨论版块的管理员设置
	 * @param updatedDeptId 修改后的部门ID
	 * @param spaceAdmins 当前部门讨论版块管理员中为空间管理员的部分
	 */
	public void updateDeptBBSBoardManager(Long deptId, List<Long> spaceAdmins) throws BusinessException;
	
	/**
	 * 获取部门讨论版块管理员中非部门主管的空间管理员
	 * @param boardId 部门讨论版块ID/部门ID
	 * @return 部门讨论版块管理员中非部门主管类型的空间管理员
	 */
	public List<Long> getDeptSpaceAdminss(Long boardId) throws BusinessException;
	
	/**
	 * 获取指定部门的主管人员
	 * @param departmentId 部门ID
	 * @return 部门主管人员ID List
	 */
	public List<Long> getDeptManagers(Long departmentId) throws BusinessException ;
	
	/**
	 * 点击"部门讨论管理"时强制更新部门讨论版块的管理员，以便用户无需再对部门主管和部门空间管理员重新设定，性能会有下降，但用户会较为便利
	 * added by Meng Yang 2009-06-16
	 * @throws BusinessException 
	 */
	public void updateDeptBBSBoard(Long deptId) throws BusinessException;
	/**
	 * 删除单个讨论板块并同步内存数据
	 */
	public void deleteV3xBbsBoard(Long id);
	
	/**
	 * 批量删除多个讨论版块并同步内存数据
	 * @param boardIdStrArray   版块ID字符串数据
	 */
	public void deleteBoards(String[] boardIdStrArray);
	
	/**
	 * 更新讨论版块的发帖权限记录，完成持久化和内存同步
	 * @param boardId      版块ID
	 * @param authTargets  管理员授权发帖的对象ID集合
	 * @param authInfo     授权对象的选人信息
	 */
	public void authGeneric(Long boardId, List<Long> authTargets, String authInfo);
	
	/**
	 * 更新讨论版块的禁止回帖权限记录，完成持久化和内存同步
	 * @param boardId 版块ID
	 * @param authTargets 管理员禁止回帖的对象Type|Ids拼串
	 * @param authInfo 禁止对象的选人信息
	 */
	public void authNoReply(Long boardId, List<Long> authTargets, String authInfo);
	
	/**
	 * 删除版块禁止回帖权限的记录，完成持久化与内存同步
	 */
	public void noAuthNoReply(Long boardId);
	
	/**
	 * 删除版块发帖权限的记录，完成持久化与内存同步
	 */
	public void noAuthGeneric(Long boardId);
	
	/**
	 * 检查用户是否有具有在指定版块发贴的权限
	 */
	public boolean validIssueAuth(Long boardId, Long userId ) throws BusinessException;
	
	/**
	 * 检查用户是否有具有在指定版块回贴的权限
	 */
	public boolean validReplyAuth(Long boardId, Long userId ) throws BusinessException;
	
	/**
	 * 检查用户是否有具有在指定版块的管理权限
	 */
	public boolean validUserIsAdmin(Long boardId, Long userId );
	
	/**
	 * 获取用户可以管理的集团讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminGroupBoard(Long memberId); 
	/**
	 * 获取用户可以管理的单位下的部门讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminDeptBoard(Long memberId, long accountId);  
	/**
	 * 获取用户可以管理的单位讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminCorporationBoard(Long memberId, long accountId); 
	/**
	 * 获取用户可以管理的自定义单位讨论板块ID - 板块 Map
	 * @param memberId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 */
	public Map<Long, V3xBbsBoard> getCanAdminCustomBoard(Long memberId, long spaceId, int spaceType);
	
	/**
	 * 获取有权管理的集团或单位讨论板块
	 * @param isGroup 	是否要获取集团讨论板块
	 * @param memberId	用户ID
	 * @param accountId 用户登录单位ID
	 */
	public List<V3xBbsBoard> getCanAdminGroupOrCorpBbsBoards(boolean isGroup, Long memberId, Long accountId);
	/**
	 * 获取有权管理的自定义单位讨论板块
	 * @param memberId
	 * @param accountId
	 * @param spaceType
	 * @return
	 */
	public List<V3xBbsBoard> getCanAdminCustomBbsBoards(Long memberId, Long accountId, int spaceType);
	
	/**
	 * 得到我能发布讨论的集团讨论板块
	 */
	public List<V3xBbsBoard> getCanIssueGroupBoard(Long memberId) throws BusinessException;
	
	/**
	 * 得到我能发布讨论的指定单位下的讨论板块
	 */
	public List<V3xBbsBoard> getCanIssueCorporationBoard(Long memberId, Long accountId) throws BusinessException;
	/**
	 * 得到我能发布讨论的指定自定义集团或单位下的讨论板块
	 * @param userId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 * @throws BusinessException
	 */
	public List<V3xBbsBoard> getCanIssueCustomBoard(Long userId, Long spaceId, int spaceType) throws BusinessException;
	
	/**
	 * 判断某板块下是否含有帖子
	 * @param boardId
	 */
	public boolean hasArticleByBoardId(Long boardId);
	
	/**
	 * 用于新建单位时初始化讨论板块
	 * @param accountId
	 */
	public void initBbsBoard(long accountId);
	
	/**
	 * 保存版块排序结果
	 * @param bbsBoardIds
	 */
	public void updateBbsBoardOrder(String[] bbsBoardIds);
	
	/**
	 * 按名称查询讨论板块
	 * @deprecated 实际被废弃，目前该处屏蔽了搜索功能
	 */
	public List<V3xBbsBoard> queryByName(String typename, boolean group) ;
	
	/**
	 * 用于前端AJAX调用：在发送部门讨论页面(issuePost.jsp)，切换讨论所要发布的部门时，返回相关信息，以便同步发布范围、发布部门名称等
	 */
	public String getBbsMessage(String departmentId) throws BusinessException;
	
	/**
	 * 在新增<b>多个</b>讨论区时(如新建单位初始化讨论区时)，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	void syncMemoryWhenCreateBoards(List<V3xBbsBoard> boards);	
	/**
	 * 在修改讨论区时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	void syncMemoryWhenUpdateBoard(V3xBbsBoard board);
	
	/**
	 * 在删除<b>多个</b>讨论区时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	void syncMemoryWhenDeleteBoards(List<Long> boardIds);
	/**
	 * 按BbsBoard id列表取BbsBoard列表。
	 * @param idList Board Id列表
	 * @param useCache 是否使用缓存，为true时使用缓存，否则从数据库取。
	 * @return BbsBoard列表。
	 * @throws BusinessException
	 */
	List<V3xBbsBoard> getBbsBoards(List<Long> idList,boolean useCache) throws BusinessException;
	
	/**
	 * 删除人员时修改板块的管理员、审核员、发起者
	 * @param id
	 * @throws BusinessException
	 */
	public void delMember(Long id) throws BusinessException;

	/**
	 * 根据userid获得该用户管理的讨论板块列表
	 * @param userid
	 * @return
	 */
	public List getBbsTypeByUserId(String userid);
}