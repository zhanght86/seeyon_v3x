package com.seeyon.v3x.bbs.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.dao.BbsArticleDao;
import com.seeyon.v3x.bbs.dao.BbsBoardAuthDao;
import com.seeyon.v3x.bbs.dao.BbsBoardDao;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.domain.V3xBbsBoardAuth;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceSecurity;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-12
 */
public class BbsBoardManagerImpl implements BbsBoardManager {
	private static Log log = LogFactory.getLog(BbsBoardManagerImpl.class);	
	private List<V3xBbsBoard> allV3xBbsBoards;	
	private Map<Long, V3xBbsBoard> allV3xBbsBoardMaps;	
	private BbsBoardDao bbsBoardDao;
	private BbsArticleDao bbsArticleDao;
	private BbsBoardAuthDao bbsBoardAuthDao;	
	private OrgManager orgManager;
	private SpaceManager spaceManager;
	
	/**
	 * 由于对讨论板块的主要操作是查询，增删改操作相对较少，因而将数据库记录全部加载到内存后，查询性能会有一定提升<br>
	 * <b>此方法只在服务启动时、利用Spring的初始化而调用</b>，除了将讨论板块全部加入内存，每个讨论板块对应的权限记录(管理员、可发帖人员、禁止回帖人员)也都设置进去<br>
	 * 为了保证数据库记录与内存的同步，在对讨论板块进行增删改操作时，需另行将内存中的数据进行同步<br>
	 * 少量版块的增删改操作不宜调用此方法，否则全部重新加载、又涉及到几乎全部组织模型查询操作，相当影响性能，为此增加6个方法<br>
	 * 在不同的操作场景下，可以分别调用如下：<br>
	 * @see #syncMemoryWhenCreateBoards(List)				增加多个讨论板块时
	 * @see #syncMemoryWhenCreateBoard(V3xBbsBoard)			增加单个讨论板块时
	 * @see #syncMemoryWhenUpdateBoard(V3xBbsBoard)			修改单个讨论板块时
	 * @see #syncMemoryWhenDeleteBoard(Long)  				删除单个讨论板块时
	 * @see #syncMemoryWhenDeleteBoards(List)				删除多个讨论板块时
	 * @see #syncMemoryWhenUpdateAuthInfo(Long, List, int)  修改单个讨论板块的授权信息时
	 */
	public void init(){
		long startTime = System.currentTimeMillis();

		allV3xBbsBoards = this.bbsBoardDao.getAllV3xBbsBoard();
		allV3xBbsBoardMaps = new HashMap<Long, V3xBbsBoard>();
		
		if(allV3xBbsBoards.isEmpty()){
			return;
		}
		
		for (V3xBbsBoard board : allV3xBbsBoards) {
			board.getAdmins().clear();
			allV3xBbsBoardMaps.put(board.getId(), board);
		}
		
		List<V3xBbsBoardAuth> allV3xBbsBoardAuth = this.bbsBoardAuthDao.getAllV3xBbsBoardAuth();
		for (V3xBbsBoardAuth auth : allV3xBbsBoardAuth) {
			V3xBbsBoard board = allV3xBbsBoardMaps.get(auth.getBoardId());
			if(board!=null){
				setBoardAuth(board, auth);
			}
		}
		
		log.info("加载所有讨论区板块信息. 耗时：" + (System.currentTimeMillis() - startTime) + " MS");
	}

	private void setBoardAuth(V3xBbsBoard board, V3xBbsBoardAuth auth) {
		int type = auth.getAuthType();
		if(BbsConstants.BBS_AUTH_TYPE.ADMIN.ordinal() == type){ //管理员
			board.getAdmins().add(auth.getModuleId());
		} else if(BbsConstants.BBS_AUTH_TYPE.GENERAL.ordinal() == type){ //发帖员					
			V3xOrgEntity issuer = null;					
			try {
				issuer = orgManager.getEntity(auth.getModuleType(), auth.getModuleId());
				if(issuer!=null){
					board.getIssuerList().add(issuer);
				}
			} catch (BusinessException e) {}
		} else if(BbsConstants.BBS_AUTH_TYPE.NOTREPLY.ordinal() == type){ //禁止回帖
			V3xOrgEntity canNotReply = null;
			try {
				canNotReply = orgManager.getEntity(auth.getModuleType(), auth.getModuleId());
				if(canNotReply!=null){
					board.getCanNotReplyList().add(canNotReply);
				}
			} catch (BusinessException e) {}
		}
	}
	
	/**
	 * 按照板块ID获取对应讨论板块信息，从内存而不是数据库中读取
	 * @param boardId
	 */
	public V3xBbsBoard getBoardById(Long boardId) {
		return boardId==null ? null : this.allV3xBbsBoardMaps.get(boardId);
	}
	
	/**
	 * 判断讨论板块是否还存在
	 * @param boardId
	 */
	public boolean isBoardExist(Long boardId) throws Exception{
		return allV3xBbsBoardMaps.get(boardId) != null;
	}
	
	/**
	 * 获取全部集团讨论板块
	 */
	public List<V3xBbsBoard> getAllGroupBbsBoard(){
		return this.getAllBbsBoardsOfOneSpaceType(-1l, BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal());
	}
	
	/**
	 * 获取指定单位下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllCorporationBbsBoard(long accountId){
		return this.getAllBbsBoardsOfOneSpaceType(accountId, BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
	}
	
	/**
	 * 获取指定自定义单位或集团下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllCustomAccBbsBoard(long spaceId, int spaceType){
		return this.getAllBbsBoardsOfOneSpaceType(spaceId, spaceType);
	}
	
	/**
	 * 获取指定部门下的全部讨论板块
	 */
	public List<V3xBbsBoard> getAllDeptBbsBoard(long deptId){
		return this.getAllBbsBoardsOfOneSpaceType(deptId, BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal());
	}
	
	/**
	 * 获取某种空间类型下的全部讨论板块
	 * @param entityId   空间ID：如单位ID、部门ID等<br>
	 * 					 集团空间情况下由于无需进行单位ID匹配，该值为-1l<br>
	 * 				     单位空间情况下如该值为-1，表明取出全部单位讨论板块<br>
	 * @param spaceType	 空间类型：集团、单位、部门或项目
	 */
	private List<V3xBbsBoard> getAllBbsBoardsOfOneSpaceType(long entityId, int spaceType) {
		List<V3xBbsBoard> boards = new ArrayList<V3xBbsBoard>();
		for (V3xBbsBoard board : allV3xBbsBoards) {
			boolean eqEntityId = false;
			if (spaceType==BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal()) {
				eqEntityId = true;
			} else if (spaceType==BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal()
					|| spaceType==BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM.ordinal()
					|| spaceType==BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP.ordinal()) {
				eqEntityId = board.getAccountId()==entityId || entityId==-1l;
			} else {
				eqEntityId = board.getId()==entityId;
			}
			if(eqEntityId && board.getAffiliateroomFlag()==spaceType) {
				boards.add(board);
			}
		}
		
		this.sortBoards(boards);	
		return boards;
	}
	
	/**
	 * 对讨论板块进行排序
	 * @param boards
	 */
	private void sortBoards(List<V3xBbsBoard> boards) {
		Comparator<V3xBbsBoard> comp = new V3xBbsBoard();		
		Collections.sort(boards, comp);
	}
	
	/**
	 * 辅助在Manager层进行分页处理
	 */
	private <T> List<T> paginate(List<T> list) {
		if (list == null || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
	 * 单位管理员或集团管理员在管理讨论版块时，获取分页列表
	 * @param isGroup   是否进行集团讨论设置
	 * @param accountId 如果进行单位讨论设置，对应的单位ID
	 */
	public List<V3xBbsBoard> getBbsBoards4Page(boolean isGroup, Long accountId) {
		List<V3xBbsBoard> boards = isGroup ? this.getAllGroupBbsBoard() : this.getAllCorporationBbsBoard(accountId);
		this.sortBoards(boards);
		return this.paginate(boards);
	}
	
	/**
	 * 自定义单位管理员在管理讨论版块时，获取分页列表
	 */
	public List<V3xBbsBoard> getBbsBoards4Page(Long spaceId, int spaceType) {
		List<V3xBbsBoard> boards = this.getAllCustomAccBbsBoard(spaceId, spaceType);
		this.sortBoards(boards);
		return this.paginate(boards);
	}
	
	/**
	 * 获取所有单位板块类型    
	 * PublicInfoMenuCheckImpl 调用判断是否显示菜单
	 * @deprecated 目前此方法已不被使用
	 */
	public List<V3xBbsBoard> getAllCorporationBbsBoard(){
		return this.getAllBbsBoardsOfOneSpaceType(-1l, BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
	}
	
	/**
	 * 获取全部集团讨论板块的ID集合
	 */
	public List<Long> getAllGroupBbsBoardId(){
		List<V3xBbsBoard> boards = getAllGroupBbsBoard();
		return this.getBbsBoardIds(boards);
	}
	
	/**
	 * 获取指定单位的全部单位讨论板块ID集合
	 */
	public List<Long> getAllCorporationBbsBoardId(long accountId){
		List<V3xBbsBoard> boards = getAllCorporationBbsBoard(accountId);
		return this.getBbsBoardIds(boards);
	}
	
	/**
	 * 获取讨论板块的ID集合
	 * @param boards
	 */
	private List<Long> getBbsBoardIds(List<V3xBbsBoard> boards) {
		List<Long> result = new ArrayList<Long>();
		if(boards!=null && boards.size()>0) {
			for (V3xBbsBoard board : boards) {
				result.add(board.getId());
			}		
		}
		return result;
	}
	
	/**
	 * 创建部门讨论区(如部门空间开通时，为其创建对应的部门讨论板块)
	 * @param departmentId    部门ID
	 * @param accountId		  单位ID
	 * @param departmentName  部门名称
	 */
	public void createDepartmentBbsBoard(Long departmentId, Long accountId, String departmentName){
		if(this.getBoardById(departmentId) != null){
			return;
		}
		V3xBbsBoard board = new V3xBbsBoard();
		board.setId(departmentId);
		board.setName(departmentName);
		
		//部门讨论版块不允许匿名发起和匿名回复
		board.setAnonymousFlag((byte)BbsConstants.BBS_BOARD_ANONYONMOUS_NO);
		board.setAnonymousReplyFlag((byte)BbsConstants.BBS_BOARD_ANONYONMOUS_REPLY_NO);
		board.setTopNumber(BbsConstants.BBS_BOARD_PUTTER_THREE);	//置顶个数3个默认
		board.setAccountId(accountId);
		board.setBoardTime(new Timestamp(System.currentTimeMillis()));
		//新建部门讨论版块时，需将部门主管设定为讨论版块管理员
		List<Long> admins = new ArrayList<Long>();
		if(spaceManager.getSpace(departmentId) != null) {
			board.setAffiliateroomFlag(BbsConstants.BBS_BOARD_AFFILITER.CUSTOM.ordinal());
			try {
				List<V3xOrgMember> members = spaceManager.getSpaceMemberBySecurity(departmentId, 1);
				if(members != null) {
					for(V3xOrgMember member : members) {
						admins.add(member.getId());
					}
				}
			} catch (BusinessException e) {
				log.error("读取空间管理员出现异常", e);
			}
		} else {
			board.setAffiliateroomFlag(BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal());
			try {
				admins = this.getDeptManagers(departmentId);
			} catch (BusinessException e) {
				log.error("读取部门主管出现异常", e);
			}	
		}
		this.createV3xBbsBoard(board, admins);
	}
	
	/**
	 * 获取指定部门的主管人员
	 * @param departmentId 部门ID
	 */
	public List<Long> getDeptManagers(Long departmentId) throws BusinessException {
		V3xOrgRole depManager = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
		List<V3xOrgMember> deptManagers = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, departmentId, depManager.getId());
		return CommonTools.getEntityIds(deptManagers);
	}
	
	/**
	 * 部门主管修改部门空间管理员时，讨论版块管理员需要同步更新
	 */
	public void updateDeptBBSBoardManager(Long deptId, String managers) throws BusinessException {
		V3xBbsBoard board = this.getBoardById(deptId);
		if(board==null) {
			return;
		}
		//获取修改前讨论版块管理员：部门主管
		List<Long> deptManagers = this.getDeptManagers(deptId);
		//获取更新部门空间信息后部门空间管理员的ID List
		List<Long> spaceManagers = CommonTools.getMemberIdsByTypeAndId(managers, orgManager);
		List<Long> all = CommonTools.getSumCollection(deptManagers, spaceManagers);
		this.updateV3xBbsBoard(board, all);
	}
	
	/**
	 * 简化逻辑，废弃
	 * @deprecated
	 */
	public List<Long> getDeptSpaceAdminss(Long boardId) throws BusinessException {
		return this.spaceManager.getSpaceAdminIdsOfDepartment(boardId);
	}
	
	/**
	 * 当单位管理员在进行部门修改操作时，会改变其部门主管、管理员信息，
	 * 需要将改动的部门主管信息同步到部门讨论版块的管理员设置
	 * @param updatedDeptId 修改后的部门ID
	 * @param deptAdmins 部门主管
	 */
	public void updateDeptBBSBoardManager(Long updatedDeptId, List<Long> deptAdmins) throws BusinessException {
		V3xBbsBoard board = this.getBoardById(updatedDeptId);
		if(board==null)
			return;
		
		List<Long> spaceAdmins = this.spaceManager.getSpaceAdminIdsOfDepartment(updatedDeptId);
		List<Long> newAdmins = CommonTools.getSumCollection(spaceAdmins, deptAdmins);
		this.updateV3xBbsBoard(board, newAdmins);
	}
	
	/**
	 * 点击"部门讨论管理"时强制更新部门讨论版块的管理员，以便用户无需再对部门主管和部门空间管理员重新设定，性能会有下降，但用户会较为便利
	 */
	public void updateDeptBBSBoard(Long boardId) throws BusinessException {
		V3xBbsBoard board = boardId==null ? null : this.getBoardById(boardId);
		if(board==null)
			return;
		
		SpaceFix spaceFix = spaceManager.getSpaceFix(Constants.SpaceType.department, boardId.longValue(), null);
		// 如果部门空间仍开通，先获得部门空间管理员
		if(spaceFix!=null) {
			List<SpaceSecurity> spaceManagers = spaceFix.getSpaceManagements();		
			Set<Long> admins = new HashSet<Long>();
			if(spaceManagers!=null && spaceManagers.size()>0) {
				for(SpaceSecurity ss : spaceManagers) {
					Set<V3xOrgMember> members = orgManager.getMembersByTypeAndIds(ss.getEntityType() + "|" + ss.getEntityId().longValue());
					for(V3xOrgMember member : members) {
						admins.add(member.getId());
					}
				}
			}
			
			// 得到当前部门的主管
			V3xOrgRole depManager = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
			List<V3xOrgMember> deptLeaders = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, boardId, depManager.getId());
			CommonTools.addAllIgnoreEmpty(admins, CommonTools.getEntityIds(deptLeaders));
			
			// 空间管理员+部门主管=部门讨论版块管理员总数
			this.updateV3xBbsBoard(board, new ArrayList<Long>(admins));
		}
	}
	
	/**
	 * 保存讨论版块，设定版块管理员，包括持久化和内存同步
	 * @param board    待保存的新版块
	 * @param admins   版块管理员ID集合
	 */
	public void createV3xBbsBoard(V3xBbsBoard board, List<Long> admins){
		this.bbsBoardDao.save(board);
		this.saveAuthRecord4Board(board.getId(), admins, BbsConstants.BBS_AUTH_TYPE.ADMIN);
		board.setAdmins(admins);
		this.syncMemoryWhenCreateBoard(board);
	}
	
	/**
	 * 保存版块的指定类型权限记录
	 * @param boardId  版块ID
	 * @param admins   权限对应人员ID集合
	 * @param authType 权限类型
	 */
	private void saveAuthRecord4Board(Long boardId, List<Long> admins, BbsConstants.BBS_AUTH_TYPE authType) {
		if(admins!=null && admins.size()>0){
			for (Long admin : admins) {
				V3xBbsBoardAuth auth = new V3xBbsBoardAuth(admin, V3xOrgEntity.ORGENT_TYPE_MEMBER, boardId, BbsConstants.BBS_AUTH_TYPE.ADMIN);
				this.bbsBoardDao.getHibernateTemplate().save(auth);
			}
		}
	}
	
	/**
	 * 删除版块的指定类型权限记录
	 * @param boardId  版块ID
	 * @param authType 权限类型
	 */
	private void deleteAuthRecord4Board(Long boardId, BbsConstants.BBS_AUTH_TYPE authType) {
		this.bbsBoardDao.getHibernateTemplate().bulkUpdate("delete from " + V3xBbsBoardAuth.class.getName() + " where boardId=? and authType=?", new Object[]{boardId, authType.ordinal()});
	}
	
	/**
	 * 保存讨论版块，设定版块管理员，之后更新内存
	 * @param board    待保存的新版块
	 * @param adminStr 版块管理员ID拼串
	 */
	public void createBbsBoard(V3xBbsBoard board, String adminStr) {
		List<Long> adminIds = Strings.isNotBlank(adminStr) ? this.getIdsFromStrArray(adminStr.split(",")) : null;
		this.createV3xBbsBoard(board, adminIds);
	}
	
	/**
	 * 更新讨论版块，设定版块管理员，完成持久化与内存同步
	 * @param board    待保存的新版块
	 * @param admins   版块管理员ID集合
	 */
	public void updateV3xBbsBoard(V3xBbsBoard board, List<Long> admins){
		this.bbsBoardDao.getHibernateTemplate().update(board);
		board.setAdmins(admins);
		this.doAuth(board.getId(), admins, BbsConstants.BBS_AUTH_TYPE.ADMIN);
		this.syncMemoryWhenUpdateBoard(board);
	}
	
	/**
	 * 删除单个讨论板块并同步内存数据
	 */
	public void deleteV3xBbsBoard(Long id){
		this.bbsBoardDao.delete(id.longValue());
		this.syncMemoryWhenDeleteBoard(id);
	}
	
	/**
	 * 批量删除多个讨论版块并同步内存数据
	 * @param boardIdStrArray   版块ID字符串数据
	 */
	public void deleteBoards(String[] boardIdStrArray) {
		if(boardIdStrArray!=null && boardIdStrArray.length>0) {
			List<Long> boardIds = this.getIdsFromStrArray(boardIdStrArray);
			try {
				this.bbsBoardDao.deleteBoards(boardIds);
				this.syncMemoryWhenDeleteBoards(boardIds);
			} catch (org.springframework.dao.DataIntegrityViolationException me) {
				log.error("删除版块出错", me);
			} catch(Exception e) {
				log.error("更新版块内存数据出现异常：", e);
			}
		}
	}
	
	/**
	 * 辅助方法：将获取的板块ID字符串数组解析为板块ID集合
	 * @param boardIdStrArray  板块ID字符串数组
	 */
	private List<Long> getIdsFromStrArray(String[] boardIdStrArray) {
		if(boardIdStrArray!=null && boardIdStrArray.length>0) {
			List<Long> ids = new ArrayList<Long>();
			for(String id : boardIdStrArray) {
				ids.add(Long.parseLong(id));
			}
			return ids;
		}
		return null;
	}
	
	public void authGeneric(Long boardId, List<Long> authTargets, String authInfoArr){
		this.doAuth(boardId, Strings.getSelectPeopleElements(authInfoArr), BbsConstants.BBS_AUTH_TYPE.GENERAL);
		this.syncMemoryWhenUpdateAuthInfo(boardId, authTargets, authInfoArr, BbsConstants.BBS_AUTH_TYPE.GENERAL.ordinal());
	}
	
	public void authNoReply(Long boardId, List<Long> authTargets, String authInfoArr){
		this.doAuth(boardId, Strings.getSelectPeopleElements(authInfoArr), BbsConstants.BBS_AUTH_TYPE.NOTREPLY);
		this.syncMemoryWhenUpdateAuthInfo(boardId, authTargets, authInfoArr, BbsConstants.BBS_AUTH_TYPE.NOTREPLY.ordinal());
	}
	
	/**
	 * 删除版块禁止回帖权限的记录，完成持久化与内存同步
	 */
	public void noAuthNoReply(Long boardId) {
		this.bbsBoardDao.getHibernateTemplate().bulkUpdate("delete from " + V3xBbsBoardAuth.class.getName() + " where boardId=? and authType=?", new Object[]{boardId, 2});
		V3xBbsBoard board = this.getBoardById(boardId);
		this.syncMemoryWhenUpdateBoard(board);
	}
	
	/**
	 * 删除版块发帖权限的记录，完成持久化与内存同步
	 */
	public void noAuthGeneric(Long boardId){
		this.bbsBoardDao.getHibernateTemplate().bulkUpdate("delete from " + V3xBbsBoardAuth.class.getName() + " where boardId=? and authType=?", new Object[]{boardId, 1});
		V3xBbsBoard board = this.getBoardById(boardId);
		this.syncMemoryWhenUpdateBoard(board);
	}
	
	private void doAuth(Long boardId, List<Long> members, BbsConstants.BBS_AUTH_TYPE authType){
		this.deleteAuthRecord4Board(boardId, authType);
		this.saveAuthRecord4Board(boardId, members, authType);
	}

	private void doAuth(Long boardId, String[][] authInfos, BbsConstants.BBS_AUTH_TYPE authType){
		this.deleteAuthRecord4Board(boardId, authType);
		if(authInfos != null && authInfos.length>0) {
			for (String[] authInfo : authInfos) {
				V3xBbsBoardAuth auth = new V3xBbsBoardAuth(Long.parseLong(authInfo[1]), authInfo[0], boardId, authType);
				this.bbsBoardDao.getHibernateTemplate().save(auth);
			}
		}
	}
	
	/**
	 * 检查用户是否有具有在指定版块发贴的权限
	 */
	public boolean validIssueAuth(Long boardId, Long userId) throws BusinessException {
		return this.validAuth(this.allV3xBbsBoardMaps.get(boardId), userId, true);
	}
	
	/**
	 * 检查用户是否有具有在指定版块回贴的权限
	 */
	public boolean validReplyAuth(Long boardId, Long userId) throws BusinessException {
		return !this.validAuth(this.allV3xBbsBoardMaps.get(boardId), userId, false);
	}
	
	/**
	 * 检查用户是否有具有在指定版块发贴或回贴的权限
	 * @param boardId
	 * @param userId
	 * @param issueOrReply true检查发贴、false检查回贴
	 * @return
	 * @throws BusinessException
	 */
	private boolean validAuth(V3xBbsBoard board, Long userId, boolean issueOrReply) throws BusinessException {
		if (board == null) {
			return false;
		}
		String[] types = {V3xOrgEntity.ORGENT_TYPE_MEMBER, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, 
				V3xOrgEntity.ORGENT_TYPE_ACCOUNT, V3xOrgEntity.ORGENT_TYPE_LEVEL, 
				V3xOrgEntity.ORGENT_TYPE_POST, V3xOrgEntity.ORGENT_TYPE_TEAM};
		
		List<Long> domainIds = this.orgManager.getUserDomainIDs(userId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, types);
		List<V3xOrgEntity> list = issueOrReply ? board.getIssuerList() : board.getCanNotReplyList();
		List<Long> issuerIds = CommonTools.getEntityIds(list);
		return CollectionUtils.isNotEmpty(CommonTools.getIntersection(domainIds, issuerIds));
	}
	
	/**
	 * 检查用户是否有具有在指定版块的管理权限
	 */
	public boolean validUserIsAdmin(Long boardId, Long userId ){
		V3xBbsBoard board = this.allV3xBbsBoardMaps.get(boardId);
		if(board == null){
			return false;
		}		
		return board.getAdmins().contains(userId);
	}
	
	/**
	 * 得到我能发布讨论的集团讨论板块
	 */
	public List<V3xBbsBoard> getCanIssueGroupBoard(Long memberId) throws BusinessException {
		return this.getCanIssueBoards(memberId, -1l, BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal());
	}
	
	private List<V3xBbsBoard> getCanIssueBoards(Long memberId, Long accountId, int spaceType) throws BusinessException {
		List<V3xBbsBoard> result = new ArrayList<V3xBbsBoard>();
		for (V3xBbsBoard board : allV3xBbsBoards) {
			boolean eqAccountId = spaceType==BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal() ? true : board.getAccountId().equals(accountId);
			if((board.getAdmins().contains(memberId) || this.validAuth(board, memberId, true)) 
					&& eqAccountId && board.getAffiliateroomFlag()==spaceType){
				result.add(board);
			}
		}
		return result;
	}
	
	/**
	 * 得到我能发布讨论的指定单位下的讨论板块
	 */
	public List<V3xBbsBoard> getCanIssueCorporationBoard(Long memberId, Long accountId) throws BusinessException {
		return this.getCanIssueBoards(memberId, accountId, BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
	}
	
	/**
	 * 得到我能发布讨论的指定自定义集团或单位下的讨论板块
	 */
	public List<V3xBbsBoard> getCanIssueCustomBoard(Long userId, Long spaceId, int spaceType) throws BusinessException {
		if (spaceType == 5) {
			return this.getCanIssueBoards(userId, spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM.ordinal());
		} else {
			return this.getCanIssueBoards(userId, spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP.ordinal());
		}
	}
	
	/**
	 * 获取用户可以管理的集团讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminGroupBoard(Long memberId){
		return this.getCanAdminBoards(memberId, -1l, BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal());
	}
	
	/**
	 * 获取用户可以管理的单位讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminCorporationBoard(Long memberId, long accountId){
		return this.getCanAdminBoards(memberId, accountId, BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
	}
	
	public Map<Long, V3xBbsBoard> getCanAdminCustomBoard(Long memberId, long spaceId, int spaceType){
		return this.getCanAdminBoards(memberId, spaceId, spaceType);
	}
	
	/**
	 * 获取用户可以管理的单位下的部门讨论板块ID - 板块 Map
	 */
	public Map<Long, V3xBbsBoard> getCanAdminDeptBoard(Long memberId, long accountId){
		return this.getCanAdminBoards(memberId, accountId, BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal());
	}
	
	/**
	 * 辅助获取用户可以管理的指定空间类型讨论板块ID - 板块 Map
	 * @param memberId   用户ID
	 * @param accountId  单位ID
	 * @param spaceType  板块所属空间类型：集团、单位、部门或项目讨论
	 */
	private Map<Long, V3xBbsBoard> getCanAdminBoards(Long memberId, long accountId, int spaceType){
		Map<Long, V3xBbsBoard> result = new HashMap<Long, V3xBbsBoard>();
		for (V3xBbsBoard board : this.allV3xBbsBoardMaps.values()) {
			//集团讨论板块情况下无需进行单位ID匹配(可能会有隐患...)
			boolean eqAccountId = spaceType==BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal() ? true : board.getAccountId().equals(accountId);
			
			if(board.getAffiliateroomFlag()==spaceType && eqAccountId && board.getAdmins().contains(memberId)){
				result.put(board.getId(), board);
			}
		}
		return result;
	}
	
	/**
	 * 获取有权管理的集团或单位讨论板块
	 * @param isGroup 	是否要获取集团讨论板块
	 * @param memberId	用户ID
	 * @param accountId 用户登录单位ID
	 */
	public List<V3xBbsBoard> getCanAdminGroupOrCorpBbsBoards(boolean isGroup, Long memberId, Long accountId) {
		Map<Long, V3xBbsBoard> map = isGroup ? this.getCanAdminGroupBoard(memberId) : this.getCanAdminCorporationBoard(memberId, accountId);
		if(map!=null && map.size()>0) {
			List<V3xBbsBoard> result = new ArrayList<V3xBbsBoard>();
			result.addAll(map.values());
			return result;
		}
		return null;
	}
	
	public List<V3xBbsBoard> getCanAdminCustomBbsBoards(Long memberId, Long spaceId, int spaceType) {
		Map<Long, V3xBbsBoard> map = this.getCanAdminCustomBoard(memberId, spaceId, spaceType);
		if(map!=null && map.size()>0) {
			List<V3xBbsBoard> result = new ArrayList<V3xBbsBoard>();
			result.addAll(map.values());
			return result;
		}
		return null;
	}
	
	/**
	 * 判断某板块下是否含有帖子
	 * @param boardId
	 */
	public boolean hasArticleByBoardId(Long boardId){
		String hql = "select count(a.id) From " + V3xBbsArticle.class.getName() + " a where a.boardId=? and a.state=? ";
		int count = (Integer)this.bbsArticleDao.findUnique(hql, null, boardId, Byte.parseByte(BbsConstants.BBS_ARTICLE_IS_ACTIVE+""));
		return count>0;
	}
	
	/**
	 * 用于新建单位时初始化讨论板块，复制虚拟单位的讨论区到所要创建的单位，随后同步数据库与内存
	 * @param accountId  新建单位的ID
	 */
	public void initBbsBoard(long accountId) {
		List<V3xBbsBoard> boards4Copy = this.bbsBoardDao.getByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		List<V3xBbsBoard> boardsOfTheAccount = new ArrayList<V3xBbsBoard>();
		if(boards4Copy != null) {
			for(V3xBbsBoard board:boards4Copy) {
				V3xBbsBoard newBoard = new V3xBbsBoard();
				newBoard.setIdIfNew();
				newBoard.setAnonymousFlag(board.getAnonymousFlag());
				newBoard.setAnonymousReplyFlag(board.getAnonymousReplyFlag());
				newBoard.setAffiliateroomFlag(board.getAffiliateroomFlag());
				newBoard.setAccountId(accountId);
				newBoard.setDescription(board.getDescription());
				newBoard.setName(board.getName());
				newBoard.setBoardTime(new Timestamp(new Date().getTime()));
				newBoard.setTopNumber(board.getTopNumber());
				newBoard.setSort(board.getSort());
				boardsOfTheAccount.add(newBoard);
			}
			this.bbsBoardDao.savePatchAll(boardsOfTheAccount);
			this.syncMemoryWhenCreateBoards(boardsOfTheAccount);
		}
	}
	
	/**
	 * 在新增<b>多个</b>讨论区时(如新建单位初始化讨论区时)，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	public synchronized void syncMemoryWhenCreateBoards(List<V3xBbsBoard> boards) {
		if(boards!=null && boards.size()>0) {
			this.allV3xBbsBoards.addAll(boards);
			
			List<Long> idList = new ArrayList<Long>(boards.size());
			for(V3xBbsBoard board : boards) {
				this.allV3xBbsBoardMaps.put(board.getId(), board);
				idList.add(board.getId());
			}
			// 发送增加通知
			NotificationManager.getInstance().send(NotificationType.BbsAddBoard, idList.toArray());				
		}
	}
	
	/**
	 * 在新增<b>单个</b>讨论区时(单位或集团管理员进行操作时)，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	private synchronized void syncMemoryWhenCreateBoard(V3xBbsBoard board) {
		List<V3xBbsBoard> boards = new ArrayList<V3xBbsBoard>();
		boards.add(board);
		this.syncMemoryWhenCreateBoards(boards);
	}
	
	/**
	 * 在修改讨论区时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	public synchronized void syncMemoryWhenUpdateBoard(V3xBbsBoard board) {
		for(int i=0; i<this.allV3xBbsBoards.size(); i++) {
			V3xBbsBoard element = this.allV3xBbsBoards.get(i);
			if(element.getId().equals(board.getId())) {
				this.allV3xBbsBoards.set(i, board);
				break;
			}
		}
		
		this.allV3xBbsBoardMaps.put(board.getId(), board);
		
		// 发送更新通知
		NotificationManager.getInstance().send(NotificationType.BbsUpdateBoard, board.getId());		
	}
	
	/**
	 * 在删除<b>多个</b>讨论区时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	public synchronized void syncMemoryWhenDeleteBoards(List<Long> boardIds) {
		if(boardIds!=null && boardIds.size()>0) {
			for(Long boardId : boardIds) {
				for (Iterator<V3xBbsBoard> iter = this.allV3xBbsBoards.iterator(); iter.hasNext();) {
					V3xBbsBoard element = (V3xBbsBoard) iter.next();
					if(element.getId().equals(boardId)) {
						iter.remove();
					}
				}
			}
			
			for(Long boardId : boardIds) {
				this.allV3xBbsBoardMaps.remove(boardId);
			}
			
			// 发送删除通知
			Long[] arr= new Long[boardIds.size()];
			for (int i = 0; i < boardIds.size(); i++) {
				arr[i] = boardIds.get(i);
			}
			NotificationManager.getInstance().send(NotificationType.BbsDeleteBoard, arr);
		}
	}
	
	/**
	 * 在删除<b>单个</b>讨论区时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @see #init
	 */
	private synchronized void syncMemoryWhenDeleteBoard(Long boardId) {
		List<Long> boardIds = new ArrayList<Long>();
		boardIds.add(boardId);
		this.syncMemoryWhenDeleteBoards(boardIds);
	}
	
	/**
	 * 版块管理员在修改版块权限时，辅助进行数据库与内存的同步<br>
	 * 避免在进行少量操作时，直接调用<b>只应调用一次的初始化方法<code>init()</code></b>导致性能问题<br>
	 * @param boardId   讨论区版块ID
	 * @param authTargets	授权对象人员的ID集合
	 * @param authType	授权类型：发帖或禁止回帖
	 * @see #init
	 */
	private synchronized void syncMemoryWhenUpdateAuthInfo(Long boardId, List<Long> authTargets, String authInfo, int authType) {
		V3xBbsBoard board = this.getBoardById(boardId);
		List<V3xOrgEntity> result = null;
		try {
			result = this.orgManager.getEntities(authInfo);
		} catch (BusinessException e) {
			log.error("", e);
		}
		if(authType==BbsConstants.BBS_AUTH_TYPE.GENERAL.ordinal()) {
			board.setIssuerList(result);
		} else if(authType==BbsConstants.BBS_AUTH_TYPE.NOTREPLY.ordinal()) {
			board.setCanNotReplyList(result);
		}
		this.syncMemoryWhenUpdateBoard(board);
	}
	
	/**
	 * 更新讨论板块排序，包括持久化和内存同步
	 */
	public void updateBbsBoardOrder(String[] bbsBoardIds) {
		if (bbsBoardIds != null && bbsBoardIds.length > 0) {
			for (int i = 0; i < bbsBoardIds.length; i++) {
				Long boardId = NumberUtils.toLong(bbsBoardIds[i]);
				
				this.bbsBoardDao.updateSortNum(boardId, i); // 持久化

				V3xBbsBoard board = this.getBoardById(boardId); // 内存同步
				board.setSort(i);
				
				this.syncMemoryWhenUpdateBoard(board);
			}
		}
	}
	
	/**
	 * 按名称查询讨论板块
	 * @deprecated 实际被废弃，目前该处屏蔽了搜索功能
	 */
	public List<V3xBbsBoard> queryByName(String typename, boolean group) {
		long userId = CurrentUser.get().getId() ;
		List<V3xBbsBoard> list = new ArrayList<V3xBbsBoard>();
		List<V3xBbsBoard> allList = this.bbsBoardDao.findAllBbsBoard(userId, typename ,group) ;
		Set<Long> set = new HashSet<Long>();
		if(allList!=null && allList.size()>0) {
			for(V3xBbsBoard v3xBbsBoard : allList){
				set.add(v3xBbsBoard.getId()) ;
			}
		}	
		
		for(V3xBbsBoard v3xBbsBoard : allV3xBbsBoards){
			if(set.contains(v3xBbsBoard.getId())){
				list.add(v3xBbsBoard) ;
			}
		}		
		return list ;
	}
	
	/**
	 * 用于前端AJAX调用：在发送部门讨论页面(issuePost.jsp)，切换讨论所要发布的部门时，返回相关信息，以便同步发布范围、发布部门名称等
	 */
	public String getBbsMessage(String departmentId) throws BusinessException{
		Long boardId = Long.valueOf(departmentId);
		User user = CurrentUser.get();
		StringBuffer publisthScopeDep = new StringBuffer("Department|"+boardId);
		List<Object[]> _issueAreas = this.spaceManager.getSecuityOfDepartment(Long.valueOf(boardId));
		for (Object[] objects : _issueAreas) {
			if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equalsIgnoreCase(objects[0]+"") && boardId!=((Long)objects[1]).longValue()) {
				publisthScopeDep=publisthScopeDep.append(",Department|"+objects[1]);
			} else if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equalsIgnoreCase(objects[0]+"")) {
				//人员的ID是当前部门的,应该去掉
				if(orgManager.getMemberById((Long)objects[1]).getOrgDepartmentId()!=user.getDepartmentId()) {
					publisthScopeDep=publisthScopeDep.append(",Member|"+objects[1]);	
				}
			} else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equalsIgnoreCase(objects[0]+"")) {
				publisthScopeDep=publisthScopeDep.append(",Team|"+objects[1]);
			}
		}
		
		return departmentId+";"+publisthScopeDep.toString()+";"+orgManager.getDepartmentById(boardId).getName();
	}
	
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public void setOrgManager(OrgManager orgManager){
        this.orgManager = orgManager;
    }
	public void setBbsBoardDao(BbsBoardDao bbsBoardDao) {
		this.bbsBoardDao = bbsBoardDao;
	}
	public void setBbsBoardAuthDao(BbsBoardAuthDao bbsBoardAuthDao) {
		this.bbsBoardAuthDao = bbsBoardAuthDao;
	}
	public void setBbsArticleDao(BbsArticleDao bbsArticleDao) {
		this.bbsArticleDao = bbsArticleDao;
	}

	public List<V3xBbsBoard> getBbsBoards(List<Long> idList, boolean useCache)
			throws BusinessException {
		if(idList==null) return Collections.emptyList();
		List<V3xBbsBoard> result = new ArrayList<V3xBbsBoard>();
		if(!useCache) {
			List<V3xBbsBoard> all =  this.bbsBoardDao.getAllV3xBbsBoard();
			Map<Long,V3xBbsBoard> boardMap = new HashMap<Long, V3xBbsBoard>();
			for (V3xBbsBoard board : all) {
				Long id = board.getId();
				if(idList.contains(id))
				{
					result.add(board);
					boardMap.put(id, board);
				}
			}
			
			List<V3xBbsBoardAuth> allV3xBbsBoardAuth = this.bbsBoardAuthDao.getAllV3xBbsBoardAuth();
			for (V3xBbsBoardAuth auth : allV3xBbsBoardAuth) {
				V3xBbsBoard board = boardMap.get(auth.getBoardId());
				if(board!=null) setBoardAuth(board,auth);
			}
		}
		else
		{
			for (Long id : idList) {
				result.add(getBoardById(id));
			}
		}
		return result;
	}
	
	public void delMember(Long id) throws BusinessException {
		List<V3xBbsBoard> types = new ArrayList<V3xBbsBoard>();
		for (V3xBbsBoard board : this.allV3xBbsBoardMaps.values()) {
			if(board.getAdmins().contains(id)){
				types.add(board);
			}
		}
		if(types != null){
			for(V3xBbsBoard bbsBoard : types){
				List<Long> admins = bbsBoard.getAdmins();
				admins.remove(id);
				this.updateV3xBbsBoard(bbsBoard, admins);
			}
		}
	}

	@Override
	public List<V3xBbsBoard> getBbsTypeByUserId(String userid) {
		List<V3xBbsBoard> list= bbsBoardDao.findBbsBoardListByUserId(userid);
		return list;
	}

	@Override 
	public List<V3xBbsBoard> getAllCustomBbsBoard(long accountId) throws BusinessException {
		List<V3xBbsBoard> list = new ArrayList<V3xBbsBoard>();
		List<Long> customSpaceIds = spaceManager.getAllCustomSpace();
		for(V3xBbsBoard bt : allV3xBbsBoards){
			if(customSpaceIds.contains(bt.getAccountId()))
				list.add(bt);
		}
		this.sortBoards(list);
		return list;
	}
}