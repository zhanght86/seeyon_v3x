package com.seeyon.v3x.bbs.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleIssueArea;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleReply;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.bbs.webmodel.ArticleReplyModel;
import com.seeyon.v3x.bbs.webmodel.BbsCountArticle;
import com.seeyon.v3x.bbs.webmodel.BoardModel;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.util.cache.ClickDetail;
import com.seeyon.v3x.util.cache.DataCache;

/**
 * @author lydong
 */
public class BbsController extends BaseController {	
	private static Log log = LogFactory.getLog(BbsController.class);
	private BbsBoardManager bbsBoardManager;
	private BbsArticleManager bbsArticleManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;	
	private UserMessageManager userMessageManager;	
	private IndexManager indexManager;	
	private SpaceManager spaceManager;	
	private UpdateIndexManager updateIndexManager;	
	private ProjectManager projectManager;	
	private AppLogManager appLogManager;	
	private StaffInfoManager staffInfoManager;
	private ProjectPhaseEventManager projectPhaseEventManager;
	private static DataCache<V3xBbsArticle> dataCache;
	
	public static DataCache<V3xBbsArticle> getDataCache() {
		return dataCache;
	}

	/**
	 * 初始加载缓存
	 */
	public void init() {
		dataCache = new DataCache<V3xBbsArticle>(this.bbsArticleManager);
	}
    
    /**
     * 公共信息管理-讨论管理首页
     * 需要将该管理页嵌入页签中，所以添加此方法
     * added by Mazc 07-12-11
     */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
    public ModelAndView bbsManageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {       
        return new ModelAndView("bbs/bbsmanager/index");
    }

	// 查询版块信息框架
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView listBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/bbsmanager/bbsmanageframe");
	}
	
	// 查询版块信息
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView listBoardMain(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/bbsmanager/bbsmanage");
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		boolean isGroup = user.isGroupAdmin();
		String spaceId = httpServletRequest.getParameter("spaceId");
		String spaceType = httpServletRequest.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<V3xBbsBoard> bbsBoardList = new ArrayList<V3xBbsBoard>();
		if (Strings.isNotBlank(spaceId)) {
			bbsBoardList = this.bbsBoardManager.getBbsBoards4Page(Long.parseLong(spaceId), spaceTypeInt);
		} else {
			bbsBoardList = this.bbsBoardManager.getBbsBoards4Page(isGroup, accountId);
		}
		
		//判断此版块下是否有帖子，有的话做标识不允许删除
		List<Boolean> delBsList = new ArrayList<Boolean>();
		for( V3xBbsBoard board : bbsBoardList ){
			boolean hasArticle = bbsBoardManager.hasArticleByBoardId(board.getId());
			delBsList.add(hasArticle);
		}
		return mav.addObject("list", bbsBoardList).addObject("delBsList", delBsList);
	}

	// 新建版块信息框架
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView listBoardAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/bbsmanager/bbsmanagecreate");
	}

	// 新增版块初始化信息
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView newBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/bbsmanager/createboard");
	}

	// 新增版块信息
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView createBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		V3xBbsBoard bbsBoard = setVO(request, response);
		bbsBoard.setIdIfNew();
		this.bbsBoardManager.createBbsBoard(bbsBoard, request.getParameter("bbsBoardAdmin"));
		
		//对管理员、审核员设定记录应用日志
		this.saveManagersChangeLog(bbsBoard, true);
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		return super.redirectModelAndView("/bbs.do?method=listBoard&spaceType="+spaceType+"&spaceId=" + spaceId, "parent.parent");
	}

	// 查询版块信息(修改)
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView listBoardModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/bbsmanager/bbsmanagemodify");
	}

	/**
	 * 进入修改讨论版块信息页面 
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView oldBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		V3xBbsBoard bbsBoard = bbsBoardManager.getBoardById(Long.parseLong(request.getParameter("id")));
		return new ModelAndView("bbs/bbsmanager/modifyboard").addObject("bbsBoard", bbsBoard);
	}
	
	/**
	 * 单位、集团讨论版块管理员和审核员设置与变更时保存日志
	 * @param bbsBoard
	 */
	private void saveManagersChangeLog(V3xBbsBoard bbsBoard, boolean isNew) {
		User user = CurrentUser.get();
		String actionText = Constants.getActionText(isNew);
		if(user.isGroupAdmin()) {
			this.appLogManager.insertLog(user, AppLogAction.Group_BbsManagers_Update, user.getName(), bbsBoard.getName(), actionText);
		} else {
			String accountName = null;
			try {
				accountName = this.orgManager.getAccountById(user.getLoginAccount()).getName();
			} catch(Exception e) {
				
			}
			this.appLogManager.insertLog(user, AppLogAction. Account_BbsManagers_Update, user.getName(), accountName, bbsBoard.getName(), actionText);
		}
		
	}

	/**
	 * 完成对讨论板块信息的修改，包括持久化与同步内存
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView modifyBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		V3xBbsBoard bbsBoard = setVO(request, response);
		List<Long> auth = CommonTools.parseStr2Ids(request, "bbsBoardAdmin");
		this.bbsBoardManager.updateV3xBbsBoard(bbsBoard, auth);
		String spaceType = request.getParameter("spaceType");
		//对管理员、审核员设定记录应用日志
		this.saveManagersChangeLog(bbsBoard, false);
		String spaceId = request.getParameter("spaceId");
		return super.redirectModelAndView("/bbs.do?method=listBoard&spaceId=" + spaceId + "&spaceType=" + spaceType, "parent.parent");
	}

	// 查询版块信息
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView listBoardDel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/bbsmanager/bbsmanagedelete");
	}

	// 删除版块信息
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
	public ModelAndView delBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.bbsBoardManager.deleteBoards(request.getParameterValues("id"));
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		return super.redirectModelAndView("/bbs.do?method=listBoard&spaceType="+spaceType+"&spaceId=" + spaceId, "parent");
	}

	// setVO
	private V3xBbsBoard setVO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		V3xBbsBoard board = null;
		if(Strings.isNotBlank(request.getParameter("id"))){
			board = bbsBoardManager.getBoardById(Long.parseLong(request.getParameter("id")));
		} else {
			board = new V3xBbsBoard();
			//讨论板块未设定创建时间和修改时间的区分，排序时使用创建时间，在修改时，不设此值，避免修改过后排序发生变动
			board.setBoardTime(new Timestamp(System.currentTimeMillis()));
		}
		//讨论区名称
		board.setName(request.getParameter("name"));
		//讨论区描述
		board.setDescription(request.getParameter("description"));
		//置顶数
		board.setTopNumber(Integer.parseInt(request.getParameter("topNumber")));
		
		board.setOrderFlag(Integer.parseInt(request.getParameter("orderFlag")));
		//是否允许匿名发帖
		if(Strings.isNotBlank(request.getParameter("anonymousFlag")))
			board.setAnonymousFlag(Byte.parseByte(request.getParameter("anonymousFlag")));	
		
		//是否允许匿名回复
		if(Strings.isNotBlank(request.getParameter("anonymousReplyFlag")))
			board.setAnonymousReplyFlag(Byte.parseByte(request.getParameter("anonymousReplyFlag")));
		
		//设置单位
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if(account.getIsRoot()) {
			board.setAffiliateroomFlag(BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal());
			board.setAccountId(accountId);
		}else if (Strings.isNotBlank(spaceId)) {
			board.setAffiliateroomFlag(spaceTypeInt);
			board.setAccountId(Long.parseLong(spaceId));
		}else{
			board.setAffiliateroomFlag(BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
			board.setAccountId(accountId);
		}
		return board;
	}

	//版块授权
	public ModelAndView authBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		
		String boardId = request.getParameter("boardId");
		String authType = request.getParameter("authType");
		String authInfo = null;
		String oldAuthInfo = null;
		//授权的ID集合
		List<Long> auth = new ArrayList<Long>();
		
		V3xBbsBoard board = bbsBoardManager.getBoardById(Long.valueOf(boardId));
		if(BbsConstants.AUTH_TO_POST.equals(authType)){//授权发帖权限
			authInfo = request.getParameter("authIssueIds");
			oldAuthInfo = board.getAuthInfo(BbsConstants.AUTH_TO_POST);
		}else if(BbsConstants.FORBIDDEN_TO_REPLY.equals(authType)){//禁止回帖权限
			authInfo = request.getParameter("authReplyIds");
			oldAuthInfo = board.getAuthInfo(BbsConstants.FORBIDDEN_TO_REPLY);
		}
		
		if(!oldAuthInfo.equals(authInfo)){
			//构造消息接收者
			Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(authInfo);
			for (V3xOrgMember entity : membersSet){
				if(!userId.equals(entity.getId())){
					auth.add(entity.getId());
				}
			}
			Collection<MessageReceiver> receivers = MessageReceiver.get(Long.parseLong(boardId), auth);
			
			String messageKey = "";
			if(BbsConstants.AUTH_TO_POST.equals(authType)){//授权发帖权限
				messageKey = "bbs.auth";
				this.bbsBoardManager.authGeneric(Long.parseLong(boardId), auth, authInfo);
			}else if(BbsConstants.FORBIDDEN_TO_REPLY.equals(authType)){//禁止回帖权限
				messageKey = "bbs.no.reply";
				this.bbsBoardManager.authNoReply(Long.parseLong(boardId), auth, authInfo);
			}
			
			//发送消息
			try {
				userMessageManager.sendSystemMessage(MessageContent.get(messageKey, board.getName(), CurrentUser.get().getName()),
						ApplicationCategoryEnum.bbs, CurrentUser.get().getId(), receivers, Long.parseLong(boardId));
			} catch (MessageException e) {
				logger.error("send message failed", e);
			}
			
			appLogManager.insertLog(user, AppLogAction.Bbs_PostAuth_Update, userName, board.getName());
		}
		
		super.rendJavaScript(response, "parent.window.close();");
		return null;
	}
	
	// 查询版块信息框架
	public ModelAndView listArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/boardmanager/boardmanageframe").addObject("boardId", request.getParameter("boardId"));
	}

	// 初始化管理某版块 TODO js代码转移到jsp中去，方便维护
	public ModelAndView listArticleMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boardmanager/boardmanage");
		User user = CurrentUser.get();
		String boardIdStr = request.getParameter("boardId");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		Long boardId = Strings.isNotBlank(boardIdStr) ? Long.valueOf(boardIdStr) : user.getDepartmentId();
		Long userId = user.getId();
		
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
		if(board == null) {
			super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.board.deleted") + "');" +
										   "try {getA8Top().contentFrame.topFrame.backToPersonalSpace(); } catch(e) {}");
			return  null;
		}
		
		boolean isDept = Strings.isNotBlank(request.getParameter("dept"));
		boolean isAdmin = false;
		if(isDept){
			List<Long> managerDepartments = new ArrayList<Long>();
			if("true".equals(custom)) {
				managerDepartments = spaceManager.getCanManagerSpace(userId);
				List<SpaceFix> departmentList = new ArrayList<SpaceFix>();
				for(Long depId : managerDepartments){
					if(!depId.equals(boardId)){
						departmentList.add(spaceManager.getSpace(depId));
						}
					}
				mav.addObject("custom", custom);
				mav.addObject("departmentList", null);
				mav.addObject("departmentListLength", null);
			} else {
				managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
				List<V3xOrgDepartment> departmentList = new ArrayList<V3xOrgDepartment>();
				for(Long depId : managerDepartments){
					if(!depId.equals(boardId)){
						departmentList.add(orgManager.getDepartmentById(depId));
						}
					}
				mav.addObject("departmentList", departmentList);
				mav.addObject("departmentListLength", departmentList.size());
			}
			mav.addObject("isSpaceManager", managerDepartments.contains(boardId));
			
			// 为避免用户需逐个重复确定部门主管和部门空间管理员, 在用户进行部门讨论管理页面之前对部门讨论版块管理员进行强制更新, 性能会下降, 但用户较便利
			V3xOrgDepartment curDepartment = orgManager.getDepartmentById(Long.valueOf(boardIdStr));
			mav.addObject("curDepartment", curDepartment);
			if(curDepartment!=null) {
				// 只在部门讨论情况下进行更新
				this.bbsBoardManager.updateDeptBBSBoard(boardId);
			}
			// 历史遗留问题：部门讨论在强制更新之后，才开始判断是否具有管理权限
			isAdmin = bbsBoardManager.validUserIsAdmin(boardId, user.getId());
			List<Long> spaceAdmin = spaceManager.getCanManagerSpace(userId);
			boolean isSpaceAdmin = false;
			if(spaceAdmin.contains(boardId)) {
				isSpaceAdmin = true;
			}
			if(!isAdmin) {
				if(!isSpaceAdmin) {
					super.printV3XJS(response.getWriter());
					super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.noright2admin.deleted") + "');" +
							"try {getA8Top().contentFrame.topFrame.back(); } catch(e) {}");
					return  null;
				}
			}
		} else {
			isAdmin = bbsBoardManager.validUserIsAdmin(boardId, user.getId());
			if(!isAdmin) {
				super.printV3XJS(response.getWriter());
				super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.noright2admin.deleted") + "');" +
											   "try {getA8Top().contentFrame.topFrame.back(); } catch(e) {}");
				return  null;
			}
			List<V3xBbsBoard> v3xBbsBoardManageList = new ArrayList<V3xBbsBoard>();
			//获取我可以管理的板块，切换使用
			if (Strings.isNotBlank(spaceId)) {
				v3xBbsBoardManageList = this.bbsBoardManager.getCanAdminCustomBbsBoards(userId, user.getLoginAccount(), Integer.parseInt(spaceType));
			} else {
				v3xBbsBoardManageList = this.bbsBoardManager.getCanAdminGroupOrCorpBbsBoards(Strings.isNotBlank(request.getParameter("group")), userId, user.getLoginAccount());
			}
			
			if(v3xBbsBoardManageList!=null && v3xBbsBoardManageList.size()>0) {
				v3xBbsBoardManageList.remove(board);
			}
			mav.addObject("v3xBbsBoardManageList", v3xBbsBoardManageList);
		}
		
		Pagination.setNeedCount(true);
		List<ArticleModel> boardArticleModelList = null;
		//判断一下当前用户是否是该帖子板块的管理员
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		if(isAdmin || isDept) {
			List<V3xBbsArticle> bbsBoardList = this.bbsArticleManager.listArticleByBoardId(boardId, condition, textfield, textfield1, isDept);
			boardArticleModelList = this.getArticleModelList(bbsBoardList, boardId);
		}
		return mav.addObject("board", board).addObject("list", boardArticleModelList);	
	}
	
	public ModelAndView listShowArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bbs/boardmanager/boardmanagershow");
	}
	
	//显示主题详细信息
	public ModelAndView showArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boardmanager/showArticle");
		Long articleId = Long.valueOf(request.getParameter("id"));
		
		V3xBbsArticle article = bbsArticleManager.getArticleById(articleId);
		mav.addObject("article", article);
		
		// 获取发布范围
		List<V3xBbsArticleIssueArea> issueArea = bbsArticleManager.getIssueArea(articleId);
		mav.addObject("issueArea", issueArea);
		
		V3xOrgMember issueUser = orgManager.getEntityById(V3xOrgMember.class, article.getIssueUserId());
		// 设置发布者名称
		mav.addObject("issueUserName", issueUser.getName());
		
		return mav;
	}
	
	// 置顶主题
	public ModelAndView topArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.bbsArticleManager.topArticle(request.getParameterValues("id"));
		return super.redirectModelAndView("/bbs.do?method=listArticleMain&boardId=" +  request.getParameter("boardId") 
				+"&dept="+ request.getParameter("dept") + "&custom=" + request.getParameter("custom") + "&group=" + request.getParameter("group")
				+ "&spaceType=" + request.getParameter("spaceType") + "&spaceId=" + request.getParameter("spaceId"));
	}
	

	// 取消置顶主题
	public ModelAndView cancelTopArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.bbsArticleManager.cancelTopArticle(request.getParameterValues("id"));
		return super.redirectModelAndView("/bbs.do?method=listArticleMain&boardId=" +  request.getParameter("boardId") 
				+"&dept="+ request.getParameter("dept") + "&custom=" + request.getParameter("custom") +"&group=" + request.getParameter("group")
				+ "&spaceType=" + request.getParameter("spaceType") + "&spaceId=" + request.getParameter("spaceId"));
	}

	// 精华主题
	public ModelAndView eliteArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.bbsArticleManager.eliteArticle(request.getParameterValues("id"));
		return super.redirectModelAndView("/bbs.do?method=listArticleMain&boardId=" +  request.getParameter("boardId") 
				+"&dept="+ request.getParameter("dept") + "&custom=" + request.getParameter("custom") +"&group=" + request.getParameter("group")
				+ "&spaceType=" + request.getParameter("spaceType") + "&spaceId=" + request.getParameter("spaceId"));
	}

	// 取消精华主题
	public ModelAndView cancelEliteArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.bbsArticleManager.cancelEliteArticle(request.getParameterValues("id"));
		return super.redirectModelAndView("/bbs.do?method=listArticleMain&boardId=" +  request.getParameter("boardId") 
				+"&dept="+ request.getParameter("dept") + "&custom=" + request.getParameter("custom") +"&group=" + request.getParameter("group")
				+ "&spaceType=" + request.getParameter("spaceType") + "&spaceId=" + request.getParameter("spaceId"));
	}

	//删除主题
	public ModelAndView delArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String [] bbsBoardIdArray = request.getParameterValues("id");
		String isFromProject=request.getParameter("isFromProject"); 
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		if("true".equals(isFromProject)) {
			String ids = request.getParameter("ids");
			if(Strings.isNotBlank(ids))
				bbsBoardIdArray = ids.split(",");
		}
		
		User user = CurrentUser.get();
		String userName = user.getName();
		if (bbsBoardIdArray != null) {
			for (String id : bbsBoardIdArray) {
				V3xBbsArticle article = this.getArticleFromCacheOrDB(Long.valueOf(id));
				bbsArticleManager.delArticle(id);
				//在此删除全文检索的纪录
				indexManager.deleteFromIndex(ApplicationCategoryEnum.bbs, Long.parseLong(id));
				//应用日志
				appLogManager.insertLog(user, AppLogAction.Bbs_Delete, userName, article.getArticleName());
				//缓存同步
				this.removeCache(NumberUtils.toLong(id));
			}
		}
		if("true".equals(isFromProject)) {
			super.rendJavaScript(response, "parent.location.reload(true);");
	    	return null;
		} else {
			return super.redirectModelAndView("/bbs.do?method=listArticleMain&boardId=" + request.getParameter("boardId") + "&dept=" + request.getParameter("dept") +"&custom=" + request.getParameter("custom") 
					+ "&group=" + request.getParameter("group") + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		}
	}

	// 统计某版块发帖数量
	public ModelAndView countArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boardmanager/countarticle");
		String boardId = request.getParameter("boardId");
		String group = request.getParameter("group");
		String dept = request.getParameter("dept");
		String custom = request.getParameter("custom");
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(Long.valueOf(boardId));
		String countType = request.getParameter("countType");
		String departmentId = "";
		if (Integer.parseInt(countType) == BbsConstants.BBS_COUNT_ARTICLE_TYPE_DEPARTMENT_PERSON) {
			departmentId = request.getParameter("departmentId");
		}
		//部门统计---------6-22日修改将部门、个人统计列到一页
		List<BbsCountArticle> bbsCountArticleList = bbsArticleManager.countArticle(BbsConstants.BBS_COUNT_ARTICLE_TYPE_DEPARTMENT + "", departmentId, new Long(boardId));

		//个人统计(真名发帖)
		List<BbsCountArticle> senderList = bbsArticleManager.countArticle(BbsConstants.BBS_COUNT_ARTICLE_TYPE_PERSON + "", departmentId, new Long(boardId));
		
		//匿名统计
		List<BbsCountArticle> anonymousList = bbsArticleManager.countArticle(BbsConstants.BBS_COUNT_ARTICLE_TYPE_PERSON_ANONYMOUS + "", departmentId, new Long(boardId));
		if(anonymousList!=null && anonymousList.size()>0)
			mav.addObject("anonymousCount", bbsArticleManager.getAnonymousCount4Statistic(anonymousList));

		mav.addObject("list", bbsCountArticleList);
		mav.addObject("sendlist", senderList);
		mav.addObject("countType", countType);
		mav.addObject("board", board);
		if(Strings.isNotBlank(group)){
			mav.addObject("group", group);
		}
		if(Strings.isNotBlank(dept)){
			mav.addObject("dept", dept);
		}
		
		return mav.addObject("custom", custom != null ? true : false);
	}

	// default
	public ModelAndView index(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		return null;
	}
	
	private List<ArticleModel> getArticleModelList(List<V3xBbsArticle> articleList)throws Exception{
		return getArticleModelList(articleList, true, null);
	}
	
	private List<ArticleModel> getArticleModelList(List<V3xBbsArticle> articleList, Long boardId)throws Exception{
		return getArticleModelList(articleList, true, boardId);
	}

	//将List<V3xBbsArticle>转换为List<ArticleModel>(liaoj)
	private List<ArticleModel> getArticleModelList(List<V3xBbsArticle> articleList, boolean needIssueArea, Long boardId)throws Exception{
		V3xBbsBoard v3xBbsBoard = null;
		if(boardId != null){
			v3xBbsBoard = this.bbsBoardManager.getBoardById(boardId);
		}
		
		List<ArticleModel> articleModelList = new ArrayList<ArticleModel>();
		
		if (articleList != null) {
			// 构造显示列表
			for (V3xBbsArticle v3xBbsArticle : articleList) {
				ArticleModel articleModel = new ArticleModel(v3xBbsArticle);
				articleModel.setClickNumber(getSyncClickNumber(v3xBbsArticle.getClickNumber(), v3xBbsArticle.getId()));
				
				if(v3xBbsBoard == null){
					articleModel.setBoard(this.bbsBoardManager.getBoardById(v3xBbsArticle.getBoardId()));
				} else {
					articleModel.setBoard(v3xBbsBoard);
				}
				articleModelList.add(articleModel);
			}
		}
		
		return articleModelList;
	}
	
	private int getSyncClickNumber(Integer dbNum, Long id) {
		V3xBbsArticle bbs = dataCache.get(id);
		if(bbs != null) {
			return bbs.getClickNumber();
		}
		return dbNum;
	}
	
	// 显示讨论区所有的主题信息，按照时间降序排列(liaoj)
	public ModelAndView listAllArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/listArticle");
		//集团更多返回判断标志
		boolean isGroup = false;
		boolean moreList = false;
		
		String boardId = request.getParameter("boardId");
		if(Strings.isNotBlank(boardId)){
			V3xBbsBoard board = this.bbsBoardManager.getBoardById(Long.valueOf(boardId));
			if(board==null){
				super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.board.deleted") + "');self.history.back();");
				return null;
			}
			moreList = true;
			mav.addObject("typeId", board.getId()) ;
		    mav.addObject("moreList", moreList);
			mav.addObject("boardName", board.getName());
		}
		if(Strings.isNotBlank(request.getParameter("group"))){
			isGroup = true;
			mav.addObject("group", "group");
		}
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		List<V3xBbsArticle> v3xBbsArticleList;
		
		if(Strings.isNotBlank(boardId)){
			v3xBbsArticleList = bbsArticleManager.listArticleByBoardId(Long.valueOf(boardId), condition, textfield, textfield1);
		}else{
			if (Strings.isNotBlank(spaceId)) {
				v3xBbsArticleList = bbsArticleManager.listAllArticle(Long.parseLong(spaceId), Integer.parseInt(spaceType), condition, textfield, textfield1, boardId);
				String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
				mav.addObject("spaceName", spaceName);
				mav.addObject("publicCustom", true);
			} else {
				v3xBbsArticleList = bbsArticleManager.listAllArticle(isGroup, condition, textfield, textfield1, boardId);
			}
		}
		
		List<ArticleModel> allArticleModelList = this.getArticleModelList(v3xBbsArticleList);

		return mav.addObject("articleModellist", allArticleModelList).addObject("boardId", boardId);
	}
	
	
	// 更多页面添加查询功能
	public ModelAndView oneTypeBbsSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/listArticle");
		//集团更多返回判断标志
		boolean isGroup = false;
		boolean moreList = false;
		
		String boardId = request.getParameter("boardId");
		String typeId = request.getParameter("typeId") ;
		if(Strings.isNotBlank(boardId)){
			V3xBbsBoard board = this.bbsBoardManager.getBoardById(Long.valueOf(typeId));
			if(board==null){
				super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.board.deleted") + "');self.history.back();");
				return  null;
			}
			moreList = true;
			mav.addObject("typeId", board.getId()) ;
		    mav.addObject("moreList", moreList);
			mav.addObject("boardName", board.getName());
		}
		if(Strings.isNotBlank(request.getParameter("group"))){
			isGroup = true;
			mav.addObject("group", "group");
		}
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		List<V3xBbsArticle> v3xBbsArticleList = new ArrayList<V3xBbsArticle>();
		if(Strings.isNotBlank(spaceId)){
			v3xBbsArticleList = bbsArticleManager.listAllArticle(Long.parseLong(spaceId), "5".equals(spaceType) ? 5 : 6, condition, textfield, textfield1 , typeId );
		}else{
			v3xBbsArticleList = bbsArticleManager.listAllArticle(isGroup, condition, textfield, textfield1 , typeId );
		}
		List<ArticleModel> allArticleModelList = this.getArticleModelList(v3xBbsArticleList);

		return mav.addObject("articleModellist", allArticleModelList).addObject("boardId", boardId);
	}
	
	
	//显示讨论区所有的主题信息，按照时间降序排列(liaoj)
	public ModelAndView bbsSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/listQuery");
		//集团更多返回判断标志
		boolean isGroup = false;
		if(Strings.isNotBlank(request.getParameter("group"))){
			isGroup = true;
			mav.addObject("group", "group");
		}
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		List<V3xBbsArticle> v3xBbsArticleList = new ArrayList<V3xBbsArticle>();
		if(Strings.isNotBlank(spaceId)){
			v3xBbsArticleList = bbsArticleManager.listAllArticle(Long.parseLong(spaceId), "5".equals(spaceType) ? 5 : 6, condition, textfield, textfield1 , null );
		}else{
			
			v3xBbsArticleList = bbsArticleManager.listAllArticle(isGroup, condition, textfield, textfield1 , null );
		}
		List<ArticleModel> allArticleModelList = this.getArticleModelList(v3xBbsArticleList);
		
		return mav.addObject("articleList", allArticleModelList);
	}
	
	/**
	 * 在用户发起部门讨论或点击部门讨论栏目底部"更多"按钮或点击菜单"部门讨论"之后<br>
	 * 均进入此页面，显示部门讨论区所有的主题信息，按照时间降序排列<br>
	 */
	public ModelAndView deptlistAllArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Map<SpaceType, List<SpaceModel>> spacePath = this.spaceManager.getAccessSpace(user.getId(), user.getLoginAccount());
		String custom = request.getParameter("custom");
		List<SpaceModel> deptSpaceModels =  spacePath.get(SpaceType.department);
		if(CollectionUtils.isEmpty(deptSpaceModels) && !"true".equals(custom)) {
			PrintWriter out = response.getWriter();
			super.printV3XJS(out);
			out.println("<script>");
			out.println("	alert('" + this.getBbsI18NValue("bbs.nodeptspace.label") + "');");
			out.println("	window.getA8Top().contentFrame.topFrame.realignSpaceMenu('" + user.getLoginAccount() + "');");
			out.println("</script>");
			out.flush();
			return super.redirectModelAndView("bbs.do?method=listLatestFiveArticleAndAllBoard");
		}
		
		ModelAndView mav = new ModelAndView("bbs/boarduse/listDepArticle");
		mav.addObject("deptSpaceModels", deptSpaceModels);
		if ("true".equals(custom)) {
			Long spaceId = Long.parseLong(request.getParameter("departmentId"));
			List<Long> managerSpaces = spaceManager.getCanManagerSpace(user.getId());
			if(CollectionUtils.isNotEmpty(managerSpaces) && managerSpaces.contains(spaceId)) {
				mav.addObject("isSpaceBbsManager", true);
			}
		}
		
		/**
		 * 当用户点击"部门讨论"菜单时，部门ID参数值为空，需为其设定一个默认显示的部门讨论：
		 * 	1.用户在本单位办公，如果其主岗所在部门开通部门空间，则默认显示其主岗所在部门的讨论区，反之，显示其可以访问的第一个部门空间对应讨论区；
		 * 	2.用户在兼职单位办公，显示其可以访问的第一个部门空间对应讨论区。
		 * 当用户点击部门空间的"部门讨论"栏目底部"更多"按钮时，部门ID参数值不为空，此时页面指向指定的部门讨论区
		 */
		Long boardId = null;
		if(Strings.isBlank(request.getParameter("departmentId"))) {
			boolean mainDeptSpaceOpened = this.spaceManager.isCreateDepartmentSpace(user.getDepartmentId());
			boardId = user.getLoginAccount() == user.getAccountId() && mainDeptSpaceOpened ? user.getDepartmentId() : deptSpaceModels.get(0).getEntityId();
		} else {
			boardId = Long.parseLong(request.getParameter("departmentId"));
		}
		
		boolean bbsManagerFlag = bbsBoardManager.validUserIsAdmin(boardId, user.getId());
		Pagination.setNeedCount(true);
		List<V3xBbsArticle> articleList = new ArrayList<V3xBbsArticle>();
		if ("true".equals(custom)) {
			articleList = bbsArticleManager.listArticleByBoardId(boardId, request.getParameter("condition"), request.getParameter("textfield"), request.getParameter("textfield1"));
		} else {
			articleList = bbsArticleManager.deptlistAllArticle(boardId, request.getParameter("condition"), request.getParameter("textfield"), request.getParameter("textfield1"));
		}
		
		List<ArticleModel> allArticleModelList = this.getArticleModelList(articleList, false, null);
		if("true".equals(custom)) {
			mav.addObject("custom", true);
		}
		return mav.addObject("boardId", boardId).addObject("bbsManagerFlag", bbsManagerFlag).addObject("articleModellist", allArticleModelList);
	}
	
	/**
	 * 更多项目讨论
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView moreProjectBbs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectBbs");
		long projectId = Long.parseLong(request.getParameter("projectId"));
		// 查看项目信息 需要加载项目进度列表
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}
		
		List<V3xBbsArticle> v3xBbsArticleList = bbsArticleManager.ProjectqueryArticleList(projectId, -1, phaseId, null, null, null);
		
		List<ArticleModel> bbsList = this.getArticleModelList(v3xBbsArticleList, false, null);
		
		Long cpid = CurrentUser.get().getId();
		V3xBbsBoard bb=bbsBoardManager.getBoardById(projectId);
		boolean isManager=false;
		List <Long> pers=bb.getAdmins();
		for(Long pid:pers)
		{
			if(cpid.longValue()==pid.longValue())
			{
				isManager=true;
				break;
			}
		}
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		mav.addObject("isProjectManager",isManager);
		mav.addObject("bbsList", bbsList);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("morePro", ApplicationCategoryEnum.bbs);
		return mav;
	}
	
	/**
	 * 条件查询更多项目讨论
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView queryMoreProjectBbsByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectBbs");
		
		long projectId = Long.parseLong(request.getParameter("projectId"));
		String condition = request.getParameter("condition") ;
		String title = request.getParameter("title") ;
		String author = request.getParameter("author") ;
		String publishDate = request.getParameter("publishDate") ;
		Map<String,Object> paramMap = new HashMap<String,Object>() ;
		paramMap.put("title", title) ;
		paramMap.put("author", author) ;
		paramMap.put("publishDate", publishDate) ;
		
		// 查看项目信息 需要加载项目进度列表
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}
		
		List<V3xBbsArticle> v3xBbsArticleList = bbsArticleManager.projectQueryArticleListByCondition(condition, projectId, -1, phaseId, paramMap);
		List<ArticleModel> bbsList = this.getArticleModelList(v3xBbsArticleList, false, null);
		
		Long cpid = CurrentUser.get().getId();
		V3xBbsBoard bb=bbsBoardManager.getBoardById(projectId);
		boolean isManager=false;
		List <Long> pers=bb.getAdmins();
		for(Long pid:pers)
		{
			if(cpid.longValue()==pid.longValue())
			{
				isManager=true;
				break;
			}
		}
		// 查询条件回置
		mav.addObject("condition", condition);
		if ("title".equals(condition))
			mav.addObject("title", title);
		else if ("author".equals(condition))
			mav.addObject("author", author);
		else if ("publishDate".equals(condition))
			mav.addObject("publishDate", publishDate);
		
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		mav.addObject("isProjectManager",isManager);
		mav.addObject("bbsList", bbsList);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("morePro", ApplicationCategoryEnum.bbs);
		return mav;
	}
	
	
	
	/**
     * 讨论区首页   改造后    2008-3-6
     * 显示讨论区主题列表,讨论区所有的版块
	 */ 
	public ModelAndView listLatestFiveArticleAndAllBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("bbs/boarduse/bbsMain");
		String accountName = orgManager.getAccountById(CurrentUser.get().getLoginAccount()).getShortname();
		String groupName =orgManager.getRootAccount(CurrentUser.get().getLoginAccount()).getShortname();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		if (Strings.isNotBlank(spaceId)) {
			String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
			mav.addObject("spaceName", spaceName);
			mav.addObject("publicCustom", true);
		}
		mav.addObject("accountName", accountName);
		mav.addObject("groupName", groupName);
		boolean isGroup = false;
		if(Strings.isNotBlank(request.getParameter("group"))){
			isGroup = true;
			mav.addObject("group", "group");
		}
		
		User user = CurrentUser.get();

		List<V3xBbsBoard> v3xBbsBoardList = null;
		List<BoardModel> boardModelList = new ArrayList<BoardModel>();
		List<List<ArticleModel>> boardAndBbsList = new ArrayList<List<ArticleModel>>();

		if(isGroup){
			v3xBbsBoardList = this.bbsBoardManager.getAllGroupBbsBoard();
		}else{
			if(Strings.isNotBlank(spaceId)){
				v3xBbsBoardList = this.bbsBoardManager.getAllCustomAccBbsBoard(Long.parseLong(spaceId), Integer.parseInt(spaceType));
			}else{
				v3xBbsBoardList = this.bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
			}
		}
		Map<Long, Integer> boardsArticleNumber = new HashMap<Long, Integer>();
		Map<Long, Integer> boardsElitePostNumber = new HashMap<Long, Integer>();
		if (Strings.isNotBlank(spaceId)) {
			boardsArticleNumber = this.bbsArticleManager.getCustomBoardsArticleNumber(Long.parseLong(spaceId), Integer.parseInt(spaceType), false);
			boardsElitePostNumber = this.bbsArticleManager.getCustomBoardsArticleNumber(Long.parseLong(spaceId), Integer.parseInt(spaceType), true);
		} else {
			boardsArticleNumber = this.bbsArticleManager.getBoardsArticleNumber(isGroup);
			boardsElitePostNumber = this.bbsArticleManager.getBoardsElitePostNumber(isGroup);
		}
		
		for (V3xBbsBoard v3xBbsBoard : v3xBbsBoardList) {
			Long boardId = v3xBbsBoard.getId();
			BoardModel boardModel = new BoardModel();
			boardModel.setBoard(v3xBbsBoard);

			// 判断该版块是否有新帖
			if ((bbsArticleManager.hasNewTodayArticle(v3xBbsBoard.getId())) || (bbsArticleManager.hasNewTodayReplyPost(boardId))) {
				boardModel.setHasNewPostFlag(true);
			}else {
				boardModel.setHasNewPostFlag(false);
			}
			
			boardModel.setId(boardId);
			boardModel.setBoardName(v3xBbsBoard.getName());
			boardModel.setBoardDescription(v3xBbsBoard.getDescription());

			//计算该版块的主题数
			boardModel.setArticleNumber(toInt(boardsArticleNumber.get(boardId)));			
			boardModel.setElitePostNumber(toInt(boardsElitePostNumber.get(boardId)));
			//判断当前用户是否是管理员
			boardModel.setIsAdminFlag(bbsBoardManager.validUserIsAdmin(v3xBbsBoard.getId(), user.getId()));			
			//判断当前用户是否有发帖权限
			boardModel.setHasAuthIssue(bbsBoardManager.validIssueAuth(v3xBbsBoard.getId(), user.getId()));
			
			boardModelList.add(boardModel);
		}
		
		for(V3xBbsBoard board : v3xBbsBoardList){			
			List<V3xBbsArticle> articleTempList = new ArrayList<V3xBbsArticle>();			
			//循环板块抽讨论，每个板块6条，讨论首页用
			articleTempList = bbsArticleManager.listArticleByBoardId(board.getId(),6);			
			List<ArticleModel> listTemp = this.getArticleModelList(articleTempList);			
			boardAndBbsList.add(listTemp);	
		}	
		
		for (V3xBbsBoard bt : v3xBbsBoardList){
		   List<Long> idns = new ArrayList<Long>(); 
		   List<Long> Ids = bt.getAdmins();
		 if(Ids!=null){				
			for (Long id : Ids) {
		      V3xOrgMember member = orgManager.getMemberById(id);				
		      if (member.isValid() && !member.getIsDeleted())
				idns.add(member.getId());
			}		   
			bt.setAdmins(idns);
		 }
	   }		
		
		mav.addObject("boardModelList", boardModelList);
		mav.addObject("boardAndBbsList", boardAndBbsList);
		return mav;
	}
	
	private static int toInt(Integer num){
		return num != null ? num.intValue() : 0;
	}
	
	//集团空间有发布权限版块
	public ModelAndView listGroupBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/groupSpaceBoardList");
		List<BoardModel> boardModelList = new ArrayList<BoardModel>();
		List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager.getAllGroupBbsBoard();
		User currentUser = CurrentUser.get();
		
		Map<Long, Integer> boardsArticleNumber = this.bbsArticleManager.getBoardsArticleNumber(true);
		Map<Long, Integer> boardsElitePostNumber = this.bbsArticleManager.getBoardsElitePostNumber(true);
		Map<Long, Integer> boardsReplyNumber = this.bbsArticleManager.getBoardsReplyNumber(true);
		
		for (V3xBbsBoard v3xBbsBoard : v3xBbsBoardList) {
			Long boardId = v3xBbsBoard.getId();
			boolean isAdminAuth = bbsBoardManager.validUserIsAdmin(boardId, currentUser.getId());
			boolean isIssueAuth = bbsBoardManager.validIssueAuth(boardId, currentUser.getId());
			if(!isAdminAuth && !isIssueAuth){
				continue;
			}
			
			BoardModel boardModel = new BoardModel();
			boardModel.setIsAdminFlag(true);
			boardModel.setBoard(v3xBbsBoard);

			// 判断该版块是否有新帖
			if ((bbsArticleManager.hasNewTodayArticle(boardId)) || (bbsArticleManager.hasNewTodayReplyPost(boardId))) {
				boardModel.setHasNewPostFlag(true);
			} else {
				boardModel.setHasNewPostFlag(false);
			}

			boardModel.setId(boardId);
			boardModel.setBoardName(v3xBbsBoard.getName());
			boardModel.setBoardDescription(v3xBbsBoard.getDescription());

			// 计算该版块的主题数
			boardModel.setArticleNumber(toInt(boardsArticleNumber.get(boardId)));
			// 计算该版块的总帖数=主题数+回复帖数
			boardModel.setSumPostNumber(toInt(boardsArticleNumber.get(boardId)) + toInt(boardsReplyNumber.get(boardId)));
			// 计算该版块的精华帖数
			boardModel.setElitePostNumber(toInt(boardsElitePostNumber.get(boardId)));
			
			boardModelList.add(boardModel);
		}

		mav.addObject("boardModelList", boardModelList);
		mav.addObject("group", "group");

		return mav;
	}
	
	// 获得一个部门的所有部门管理员id
	public List<Long> getDeptManagerIds(Long deptId){	
		List<V3xOrgMember> members = null;
		try {
			V3xOrgRole depManager = orgManager.getRoleByName("DepManager");
			members = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, deptId, depManager.getId());
			
			V3xOrgRole depAdmin = orgManager.getRoleByName("DepAdmin");
			List<V3xOrgMember> members2 = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, deptId, depAdmin.getId());
			
			if(members2 != null){
				if(members == null)
					members = new ArrayList<V3xOrgMember>();
				
				members.addAll(members2);			
			}		
			
		} catch (BusinessException e) {			
		}
		if(members == null || members.size() == 0)
			return new ArrayList<Long>();
		
		// 唯一过滤
		Set<V3xOrgMember> membersSet = new HashSet<V3xOrgMember>();
		membersSet.addAll(members);
		
		List<Long> ret = new ArrayList<Long>();
		for(V3xOrgMember m : membersSet){
			ret.add(m.getId());
		}

		return ret;
	}
	
	
	/**
	 * //部门空间版块
	 */
	public ModelAndView listDeptBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("bbs/boardmanager/allboardmanage");
		String deptId= request.getParameter("boardId");
		
		List<V3xBbsBoard> v3xBbsBoardList = null;
		List<BoardModel> boardModelList = new ArrayList<BoardModel>();
		List<String> boardNameList = new ArrayList<String>();
		
		v3xBbsBoardList = this.bbsBoardManager.getAllDeptBbsBoard(Long.parseLong(deptId));
	
		Map<Long, Integer> boardsArticleNumber = this.bbsArticleManager.getDeptBoardsArticleNumber(true);
		Map<Long, Integer> boardsElitePostNumber = this.bbsArticleManager.getDeptBoardsElitePostNumber();

		for (V3xBbsBoard v3xBbsBoard : v3xBbsBoardList) {
			v3xBbsBoard.setAdmins(this.getDeptManagerIds(v3xBbsBoard.getId()));
			
			Long boardId = v3xBbsBoard.getId();
			Boolean isAdminFlag = bbsBoardManager.validUserIsAdmin(boardId, user.getId());
			
			BoardModel boardModel = null;

			// 构造主题名称列表
			boardNameList.add(v3xBbsBoard.getName());

			boardModel = new BoardModel();
			boardModel.setBoard(v3xBbsBoard);

			// 判断该版块是否有新帖
			if ((bbsArticleManager.hasNewTodayArticle(boardId)) || (bbsArticleManager.hasNewTodayReplyPost(boardId))) {
				boardModel.setHasNewPostFlag(true);
			} else {
				boardModel.setHasNewPostFlag(false);
			}

			boardModel.setId(boardId);
			boardModel.setBoardName(v3xBbsBoard.getName());
			boardModel.setBoardDescription(v3xBbsBoard.getDescription());

			// 计算该版块的主题数
			boardModel.setArticleNumber(toInt(boardsArticleNumber.get(boardId)));
			// 计算该版块的精华帖数
			boardModel.setElitePostNumber(toInt(boardsElitePostNumber.get(boardId)));
			boardModel.setIsAdminFlag(isAdminFlag);
			boardModelList.add(boardModel);
		}

		mav.addObject("boardModelList", boardModelList);
		mav.addObject("dept", "dept");
		return mav;
	}

	// 显示讨论区所有的精华帖信息(liaoj)
	public ModelAndView listAllElite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/listAllElite");
		String group = request.getParameter("group");
		boolean isGroup = false;
		if(Strings.isNotBlank(group)){
			isGroup = true;
			mav.addObject("group", "true");
		}
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		List<V3xBbsArticle> allEliteArticleList = null;
		if(!isGroup){
			if(Strings.isNotBlank(spaceId)){
				allEliteArticleList = bbsArticleManager.listAllElitePost(Long.parseLong(spaceId), Integer.parseInt(spaceType), condition, textfield, textfield1);
			}else{
				allEliteArticleList = bbsArticleManager.listAllElitePost(condition, textfield, textfield1);
			}
		} else{
			allEliteArticleList = bbsArticleManager.listAllGROUPElitePost(condition, textfield, textfield1);
		}

		List<ArticleModel> allEliteArticleModelList = this.getArticleModelList(allEliteArticleList);
		return mav.addObject("allElite", allEliteArticleModelList);
	}

	// 显示讨论区某一版块的精华帖信息(liaoj)
	public ModelAndView listBoardElite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String group = request.getParameter("group");
		ModelAndView mav = new ModelAndView("bbs/boarduse/listBoardElite");
		Long boardId = Long.valueOf(request.getParameter("id"));
		
		String condition = request.getParameter("condition");
		String field = request.getParameter("textfield");
		String field1 = request.getParameter("textfield1");
		
		List<V3xBbsArticle> boardEliteArticleList = bbsArticleManager.listBoardElitePost(boardId, condition, field, field1);

		List<ArticleModel> boardEliteArticleModelList = this.getArticleModelList(boardEliteArticleList);

		mav.addObject("boardElite", boardEliteArticleModelList);
		
		///版块信息
		V3xBbsBoard v3xBbsBoard = bbsBoardManager.getBoardById(boardId);
		
		mav.addObject("group", group);
		// 版块名称
		mav.addObject("board", v3xBbsBoard);
		return mav;
	}

	// 显示某一版块的所有主题信息(liaoj)
	public ModelAndView listBoardArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/showBoard");
		String condition = request.getParameter("condition");
		String field = request.getParameter("textfield");
		String field1 = request.getParameter("textfield1");
		boolean isGroup = false;
		if(Strings.isNotBlank(request.getParameter("group"))) {
			isGroup = true;
			mav.addObject("group", "group");
		}
		if(Strings.isNotBlank(request.getParameter("dept"))){
			mav.addObject("dept", "dept");
		}

		Long boardId = Long.parseLong(request.getParameter("boardId"));
		List<V3xBbsArticle> boardArticleList = bbsArticleManager.listArticleByBoardId(boardId, condition, field, field1,false);
		List<ArticleModel> boardArticleModelList = this.getArticleModelList(boardArticleList, boardId);
		mav.addObject("boardArticle", boardArticleModelList);

		//版块信息
		V3xBbsBoard v3xBbsBoard = bbsBoardManager.getBoardById(boardId);

		//判断当前用户是否是管理员
		User currentUser = CurrentUser.get();
		boolean isAdminFlag = bbsBoardManager.validUserIsAdmin(boardId, currentUser.getId());
		
		mav.addObject("isAdminFlag", isAdminFlag);
		mav.addObject("board", v3xBbsBoard);
		
		//判断当前用户是否有在当前版块发帖的权限
		boolean issueAuthFlag = bbsBoardManager.validIssueAuth(boardId, currentUser.getId()) || isAdminFlag;
		mav.addObject("issueAuthFlag", issueAuthFlag);

		//论坛版块列表
		List<V3xBbsBoard> boardList = null; 
		if(isGroup){
			boardList = this.bbsBoardManager.getAllGroupBbsBoard();
		} else{
			boardList = this.bbsBoardManager.getAllCorporationBbsBoard(currentUser.getLoginAccount());
		}
		mav.addObject("boardList", boardList.iterator());
		if ("alertDialog".equals(request.getParameter("dialogArgument"))){
			//TODO check
			super.rendJavaScript(response, "window.close()");
		}
		return mav;
	}
	
	/**
	 * 外单位
	 */
	public ModelAndView listOtherAccountBoardArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {			
		ModelAndView mav = new ModelAndView("bbs/boarduse/showOtherAccountBoard");
		String condition = request.getParameter("condition");
		String field = request.getParameter("textfield");
		String field1 = request.getParameter("textfield1");
				
		List<V3xBbsArticle> boardArticleList = this.bbsArticleManager.queryOtherAccountArticleList(condition, field, field1);		
		List<ArticleModel> boardArticleModelList = this.getArticleModelList(boardArticleList);		
		mav.addObject("boardArticle", boardArticleModelList);
		
		//论坛版块列表
		List<V3xBbsBoard> boardList = this.bbsBoardManager.getAllCorporationBbsBoard(CurrentUser.get().getLoginAccount());
		mav.addObject("boardList", boardList.iterator());
		return mav;
	}
	
	/**
	 * 显示外单位的精华帖信息
	 */
	public ModelAndView listOtherAccountBoardElite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/listOtherAccountBoardElite");
		String condition = request.getParameter("condition");
		String field = request.getParameter("textfield");
		String field1 = request.getParameter("textfield1");
		
		List<V3xBbsArticle> boardEliteArticleList = bbsArticleManager.queryOtherAccountEliteArticleList(condition, field, field1);
		List<ArticleModel> boardEliteArticleModelList = this.getArticleModelList(boardEliteArticleList);
		return mav.addObject("boardElite", boardEliteArticleModelList);
	}
	
	/**
	 * 获取<b>讨论</b>对应国际化值
	 * @param key
	 */
	private String getBbsI18NValue(String key) {
		return ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE, key);
	}

	/**
	 * 进入发布讨论主题页面，初始化相关信息如所在讨论版块、发布范围等等
	 */
	public ModelAndView issuePost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long userDepartmentId=user.getDepartmentId();
		Long userPostId = user.getPostId();
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		//部门讨论情况下boardId可能传入空值
		String boardIdStr = request.getParameter("boardId");
		Long boardId = Strings.isNotBlank(boardIdStr) ? Long.parseLong(boardIdStr) : user.getDepartmentId();
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
		// 板块被删除，但是页面还停留入口
		if (board == null) {
			super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.board.deleted") + "');self.history.back();");
			return null;
		}
		
		ModelAndView mav = new ModelAndView("bbs/boarduse/issuePost");
		boolean isGroup = false;
		if(Strings.isNotBlank(request.getParameter("group"))){
			isGroup = true;
			mav.addObject("group", "group");
		}
		mav.addObject("userDepartmentId", userDepartmentId);
		mav.addObject("userPostId", userPostId);
		mav.addObject("boardName2", board.getName());
		Long issueUserId = user.getId();
		mav.addObject("issueUserId", issueUserId);
		StaffInfo staff = staffInfoManager.getStaffInfoById(issueUserId);
		if(staff != null) {
			String issuerImage = staff.getSelf_image_name();
			if(StringUtils.isNotBlank(issuerImage))
				mav.addObject("image", issuerImage.startsWith("fileId") ? 0 : 1).addObject("issuerImage", issuerImage);
 		}
		
		if(board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal()){ //部门空间
			List<Long> departmentListId = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
			List<V3xOrgDepartment> canAccessDepartment = new ArrayList<V3xOrgDepartment>();
			for(Long depId : departmentListId){
				canAccessDepartment.add(orgManager.getDepartmentById(depId));
			}
			mav.addObject("departmentList", canAccessDepartment);
			mav.addObject("departmentListLength", canAccessDepartment.size());
			mav.addObject("DEPARTMENTAffiliateroomFlag", true);
			mav.addObject("DEPARTMENTissueArea", this.getDeptBbsPublishScope(boardId));
		}else if(board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.CUSTOM.ordinal()) {
			List<Long> spaceListId = spaceManager.getCanManagerSpace(user.getId());
			List<SpaceFix> canAccessSpace = new ArrayList<SpaceFix>();
			for(Long id : spaceListId) {
				canAccessSpace.add(spaceManager.getSpace(id));
			}
			StringBuffer publisthScopeSpace = new StringBuffer();
			List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(boardId);
			for(Object[] arr : issueAreas) {
				publisthScopeSpace.append(StringUtils.join(arr, "|") + ",");
			}
			mav.addObject("custom", true);
			mav.addObject("departmentList", canAccessSpace);
			mav.addObject("departmentListLength", null);
			mav.addObject("DEPARTMENTAffiliateroomFlag", true);
			mav.addObject("DEPARTMENTissueArea", publisthScopeSpace.substring(0, publisthScopeSpace.length() - 1));
		}else if(board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.PROJECT.ordinal()){ //项目讨论
			mav = new ModelAndView("bbs/boarduse/issuePostProject");
			mav.addObject("PROGECTAffiliateroomFlag", true);//不要匿名		
			mav.addObject("PROGECTissueArea", this.getProjectBbsPublishScope(boardId));			
			mav.addObject("board", board);
            //by Yongzhang 2008-05-08修改关联项目页面位置问题
            mav.addObject("projectBbs", Integer.valueOf(1));
			return mav;
		}else{ //当前用户可发帖的版块列表
			if (Strings.isNotBlank(spaceId)) {
				StringBuffer publisthScopeSpace = new StringBuffer();
				List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(Long.parseLong(spaceId));
				for(Object[] arr : issueAreas) {
					publisthScopeSpace.append(StringUtils.join(arr, "|") + ",");
				}
				mav.addObject("DEPARTMENTissueArea", publisthScopeSpace.substring(0, publisthScopeSpace.length() - 1));
				mav.addObject("publicCustom", true);
				mav.addObject("canIssueBoardList", this.getCustomIssueBoardList(user.getId(), Long.parseLong(spaceId), Integer.parseInt(spaceType)));
			} else {
				mav.addObject("canIssueBoardList", this.getIssueBoardList(user, isGroup));
			}
		}
		return mav.addObject("board", board);
	}
	/**
	 * 获取当前用户可以发帖的自定义集团或单位讨论区列表，以便发帖时可以切换所要发到的讨论区
	 * @param userId
	 * @param spaceId
	 * @param spaceType
	 * @return
	 * @throws BusinessException
	 */
	private List<V3xBbsBoard> getCustomIssueBoardList(long userId, long spaceId, int spaceType) throws BusinessException {
		List<V3xBbsBoard> canIssueBoardList = null;
		canIssueBoardList = this.bbsBoardManager.getCanIssueCustomBoard(userId, spaceId, spaceType);
		return canIssueBoardList;
	}
	
	/**
	 * 获取当前用户可以发帖的集团或单位讨论区列表，以便发帖时可以切换所要发到的讨论区
	 * @param user    当前用户
	 * @param isGroup 集团或单位讨论情况
	 */
	private List<V3xBbsBoard> getIssueBoardList(User user, boolean isGroup) throws BusinessException {
		List<V3xBbsBoard> canIssueBoardList = null;	
		long loginAccount = user.getLoginAccount();
		if(isGroup) {
			canIssueBoardList = this.bbsBoardManager.getCanIssueGroupBoard(user.getId());
		} else {
			canIssueBoardList = this.bbsBoardManager.getCanIssueCorporationBoard(user.getId(), loginAccount);
		}
		return canIssueBoardList;
	}
	
	/**
	 * 获取关联项目讨论区发帖时的默认讨论范围
	 * @param boardId 关联项目讨论区ID
	 */
	private String getProjectBbsPublishScope(Long boardId) throws Exception {
		StringBuffer projectArea = new StringBuffer(); 
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(boardId, false);	
		List<V3xOrgMember> chargeLists = projectCompose.getChargeLists();
		List<V3xOrgMember> memberLists = projectCompose.getMemberLists();
		List<V3xOrgMember> interfixLists = projectCompose.getInterfixLists();
		List<V3xOrgMember> principalLists = projectCompose.getPrincipalLists();
		List<V3xOrgMember> assistantLists = projectCompose.getAssistantLists();
		
		if(chargeLists!=null && chargeLists.size()>0){
			for(V3xOrgMember member : chargeLists){
				projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId()+",");
			}
		}
		if(memberLists!=null && memberLists.size()>0){
			for(V3xOrgMember member : memberLists){
				projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId()+",");
			}			
		}
		if(interfixLists!=null && interfixLists.size()>0){
			for(V3xOrgMember member : interfixLists){
				projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId()+",");
			}
		}
		if(principalLists!=null && principalLists.size()>0){
			for(V3xOrgMember member : principalLists){
				projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId()+",");
			}
		}
		if(assistantLists!=null && assistantLists.size()>0){
			for(V3xOrgMember member : assistantLists){
				projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId()+",");
			}
		}
		
		return projectArea.toString();
	}
	
	/**
	 * 获取部门讨论区发帖时的默认讨论范围：能够访问部门空间的全体人员
	 * @param boardId
	 */
	private String getDeptBbsPublishScope(Long boardId) throws BusinessException {
		List<Object[]> _issueAreas = this.spaceManager.getSecuityOfDepartment(boardId);
		List<String> result = new UniqueList<String>();
		for (Object[] objects : _issueAreas) {
			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(objects[0].toString())) {
				V3xOrgMember member = orgManager.getMemberById((Long)objects[1]);
				if(member == null || !member.isValid() || member.getOrgDepartmentId().equals(boardId))
					continue;
			}
			result.add(objects[0] + "|" + objects[1]);
		}
		return StringUtils.join(result, ",");
	}
	
	/**
	 * 发布讨论时，发送消息对象中加入当前讨论板块的管理员
	 * @param receivers   发布范围内的消息接受对象
	 * @param board	 	      所发布的讨论所在的讨论板块
	 */
	private void addAdmins2MsgReceivers(Collection<Long> receivers, V3xBbsBoard board) {
		List<Long> admins = board.getAdmins();
		if(admins!=null && admins.size()>0) {
			for(Long adminId : admins) {
				if(!receivers.contains(adminId)) {
					receivers.add(adminId);
				}
			}
		}
	}
	
    /**
     * 进入修改讨论主题页面：只允许修改正文内容和附件和接收新回复通知三项内容
     */
	public ModelAndView modifyPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/modifyPost");
		Long articleId = NumberUtils.toLong(request.getParameter("articleId"));
        V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
		
        //帖子被删除
		if(article==null || article.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.article.delorcanceled") + "');" + jsAction);
			return null;
		}
		
		if(Strings.isNotBlank(request.getParameter("group"))){
			mav.addObject("group", "group");
		}
		
		Long boardId = article.getBoardId();
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
		User user = CurrentUser.get();
		if(board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal()){ //部门空间
			List<Long> departmentListId = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
			List<V3xOrgDepartment> canAccessDepartment = new ArrayList<V3xOrgDepartment>();
			for(Long depId : departmentListId){
				canAccessDepartment.add(orgManager.getDepartmentById(depId));
			}
			mav.addObject("departmentList", canAccessDepartment);
			mav.addObject("departmentListLength", canAccessDepartment.size());
			mav.addObject("DEPARTMENTAffiliateroomFlag", true);
			mav.addObject("issueArea", this.getDeptBbsPublishScope(boardId));
		}else if(board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.PROJECT.ordinal()){ //项目讨论
			mav = new ModelAndView("bbs/boarduse/issuePostProject");
			mav.addObject("PROGECTAffiliateroomFlag", true);//不要匿名	
			mav.addObject("PROGECTissueArea", this.getProjectBbsPublishScope(boardId));			
			mav.addObject("board", board);
			mav.addObject("boardId", boardId);
			mav.addObject("article", article);
            //by Yongzhang 2008-05-08修改关联项目页面位置问题
            mav.addObject("projectBbs", Integer.valueOf(1));  
            mav.addObject("attachments", attachmentManager.getByReference(articleId, articleId));
			return mav;
		} else {
			String issueArea = this.getIssueAreaOfArticle(article, board);
			mav.addObject("issueArea", issueArea);
		}
		mav.addObject("board", board);
		mav.addObject("boardId", boardId);
		mav.addObject("article", article);
		mav.addObject("attachments", attachmentManager.getByReference(articleId, articleId));
		return mav;
	}
	/**
	 * 发送消息
	 * @param bbsArticle
	 * @param boardId
	 * @param newMsgReceivers
	 * @param bbsArticleManager
	 * @param userMessageManager
	 * @throws Exception
	 */
	public void sendMessage(V3xBbsArticle bbsArticle, Long boardId, List<Long> oldMsgReceivers, List<Long> newMsgReceivers) throws Exception {
		Long issueUserId = bbsArticle.getIssueUserId();
		if(oldMsgReceivers.contains(issueUserId)){
			oldMsgReceivers.remove(issueUserId);
		}
		if(newMsgReceivers.contains(issueUserId)){
			newMsgReceivers.remove(issueUserId);
		}
		//修改前后，不变的人员
		List<Long> remained = CommonTools.getIntersection(oldMsgReceivers, newMsgReceivers);
		//新增的人员
		List<Long> added = CommonTools.getAddedCollection(oldMsgReceivers, newMsgReceivers);
		//减少的人员
		List<Long> reduced = CommonTools.getReducedCollection(oldMsgReceivers, newMsgReceivers);
		
		Long senderId = -1L;
		String createUserName = this.getBbsI18NValue("anonymous.label");
		//判断是否匿名
		if(Boolean.FALSE.equals(bbsArticle.getAnonymousFlag())){
			senderId = CurrentUser.get().getId();
			createUserName = CurrentUser.get().getName();
		}
		
		Long referenceId = bbsArticle.getId();
		
		String articleName = bbsArticle.getArticleName();
		
		//给不变的人员，发送“修改”消息
		userMessageManager.sendSystemMessage(MessageContent.get("bbs.modify", articleName),
			ApplicationCategoryEnum.bbs, senderId,
			MessageReceiver.get(referenceId, remained, "message.link.bbs.open", referenceId), boardId);
		//给新增的人员，发送“新建”消息
		userMessageManager.sendSystemMessage(MessageContent.get("bbs.send", articleName, createUserName),
				ApplicationCategoryEnum.bbs, senderId,
				MessageReceiver.get(referenceId, added, "message.link.bbs.open", referenceId), boardId);
		//给减少的人员，发送“取消”消息
		userMessageManager.sendSystemMessage(MessageContent.get("bbs.cancel", articleName, createUserName),
				ApplicationCategoryEnum.bbs, senderId,
				MessageReceiver.get(referenceId, reduced), boardId);
	}
	/**
	 * 保存对讨论正文内容的修改
	 */
	public ModelAndView modifyArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long boardId = Long.parseLong(request.getParameter("boardId"));
		Long articleId = Long.parseLong(request.getParameter("articleId"));
		
		V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
		if(article==null || article.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.article.delorcanceled") + "');" + jsAction);
			return null;
		}
		
		User issueUser = CurrentUser.get();	
		String userName = issueUser.getName();
		
		//保存正文和附件
		article.setContent(request.getParameter("content"));
		article.setHasAttachments(false);
		String attaFlag = attachmentManager.update(ApplicationCategoryEnum.bbs, article.getId(), article.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	article.setHasAttachments(true);
        }
        V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
        String oldIssueArea = this.getIssueAreaOfArticle(article, board);
        List<Long> oldMsgReceivers = CommonTools.getMemberIdsByTypeAndId(oldIssueArea, orgManager);
       
        String issueAreaStr = request.getParameter("issueArea");
        List<Long> newMsgReceivers = CommonTools.getMemberIdsByTypeAndId(issueAreaStr, orgManager);
        if(!oldIssueArea.equals(issueAreaStr)){
        	bbsArticleManager.deleteArticleIssueAreasByArticleId(article.getId());
        	String[][] issueAreas = Strings.getSelectPeopleElements(issueAreaStr);
        	if(issueAreas != null){
        		for (String[] issueArea : issueAreas) {
        			bbsArticleManager.addArticleIssueArea(article.getId(), issueArea[0], Long.parseLong(issueArea[1]));
        		}
        	}
        }
        //发送消息
        sendMessage(article,boardId,oldMsgReceivers,newMsgReceivers);
		
		article.setModifyTime(new Timestamp(System.currentTimeMillis()));
		article.setMessageNotifyFlag(request.getParameter("messageNotifyFlag") != null);
		
		int clickCount = dataCache.getClickTotal(article.getId());
		if(clickCount == 0 && article.getClickNumber() != null)
			clickCount = article.getClickNumber().intValue();
		article.setClickNumber(clickCount);
		
		bbsArticleManager.updateArticle(article);
		appLogManager.insertLog(issueUser, AppLogAction.Bbs_Modify, userName, article.getArticleName());
		
		//同步缓存
		article.setIssueArea(issueAreaStr);
		this.syncCache(article, clickCount);
		
		return  this.redirectModelAndView("/bbs.do?method=showPost&articleId="+articleId+"&pageSizePara="+request.getParameter("pageSizePara")+"&nowPagePara="+request.getParameter("nowPagePara"));
		
	}
	
	public void clickCache(Long articleId,Long userId) {
		dataCache.click(articleId, new ClickDetail(userId, new Timestamp(System.currentTimeMillis())));
		V3xBbsArticle article = dataCache.get(articleId);
		if(article==null) return; 
		synchronized(readCountLock){
			article.setClickNumber(dataCache.getClickTotal(articleId));
		}		
		//发送消息
		NotificationManager.getInstance().send(NotificationType.BbsClickArticle, new CacheInfo(articleId, userId));
	}
	
	public void syncCache(V3xBbsArticle article, int clickCount) {
		dataCache.save(article.getId(), article, System.currentTimeMillis(), clickCount);
		//发送消息
		NotificationManager.getInstance().send(NotificationType.BbsModifyArticle, new CacheInfo(article.getId(), clickCount));
	}
	
	public void removeCache(Long articleId) {
		dataCache.remove(articleId);
		//发送消息
		NotificationManager.getInstance().send(NotificationType.BbsDeleteArticle, articleId);
	}
	
	//发布新帖(liaoj)
	public ModelAndView createArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long boardId = Long.parseLong(request.getParameter("boardId"));
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);		
		boolean DEPARTMENTAffiliateroomFlag = "true".equals(request.getParameter("DEPARTMENTAffiliateroomFlag"));
		boolean PROGECTAffiliateroomFlag = "true".equals(request.getParameter("PROGECTAffiliateroomFlag"));
		
		//板块被删除，而在发布页直接发布，做下板块的判断
		if(board==null) {
			super.rendJavaScript(response, "self.history.back();");
			return null;
		}
		
		V3xBbsArticle article = new V3xBbsArticle();
		article.setIdIfNew();
		article.setState((byte) BbsConstants.BBS_ARTICLE_IS_ACTIVE);
		String articleName = request.getParameter("articleName");
		article.setArticleName(articleName);
		article.setContent(request.getParameter("content"));

		User issueUser = CurrentUser.get();
		Long userId = issueUser.getId();
		String userName = issueUser.getName();
		
		article.setIssueUserId(userId);
		article.setAccountId(board.getAccountId());
		article.setPost(issueUser.getPostId());
		article.setIssueTime(new Timestamp(System.currentTimeMillis()));
		article.setMessageNotifyFlag(request.getParameterValues("messageNotifyFlag") != null);
		article.setAnonymousFlag(request.getParameterValues("anonymous") != null);
		article.setAnonymousReplyFlag(request.getParameterValues("anonymousReply") != null);
		article.setResourceFlag(request.getParameter("resourceFlag") != null ? Byte.parseByte(request.getParameter("resourceFlag")) : 0);
		article.setEliteFlag(false);
		article.setTopSequence(Integer.valueOf(0));
		article.setBoardId(boardId);
		if("true".equals(custom)) {
			article.setDepartment(boardId);
		} else {
			article.setDepartment(issueUser.getDepartmentId());
		}

		// 发布范围
		String issueAreaStr = request.getParameter("issueArea");
		String[][] issueAreas = Strings.getSelectPeopleElements(issueAreaStr);		
		
		//发送讨论区消息
		List<Long> bbsIssueIds = new ArrayList<Long>();
		if(DEPARTMENTAffiliateroomFlag && !"true".equals(custom)){ //部门讨论
			List<Object[]> _issueAreas = new ArrayList<Object[]>();
			_issueAreas = this.spaceManager.getSecuityOfDepartment(boardId);
			if(_issueAreas != null){
				issueAreas = new String[_issueAreas.size()][2];
				StringBuffer sb = new StringBuffer();
				int i = 0;
				for (Object[] objects : _issueAreas) {
					issueAreas[i++] = new String[]{String.valueOf(objects[0]), String.valueOf(objects[1])};
					sb.append(String.valueOf(objects[0]) + "|" + String.valueOf(objects[1]) + ",");
				}
				issueAreaStr = sb.substring(0, sb.length() - 1);
			}
		}
		
		bbsIssueIds = CommonTools.getMemberIdsByTypeAndId(issueAreaStr, orgManager);
		//自定义空间讨论
		if ("true".equals(custom) || Strings.isNotBlank(spaceId)) {
			List<V3xOrgMember> customMembers = spaceManager.getSpaceMemberBySecurity("true".equals(custom) ? boardId : Long.parseLong(spaceId), -1);
			if (CollectionUtils.isNotEmpty(customMembers)) {
				List<Long> customList = new ArrayList<Long>();
				for (V3xOrgMember m : customMembers) {
					customList.add(m.getId());
				}
				bbsIssueIds = CommonTools.getIntersection(bbsIssueIds, customList);
			}
		}
		
		try{
			bbsArticleManager.createArticle(article);//保存讨论
		} catch(Exception e) {
			log.error("保存讨论失败", e);
		}
		
		if(!"true".equals(custom)) {
			//板块管理员也加入消息发送范围
			this.addAdmins2MsgReceivers(bbsIssueIds, board);
		} else {
			//自定义团队空间管理员加入消息发送范围(去重，发布范围有管理员时)
			List<V3xOrgMember> managers = spaceManager.getSpaceMemberBySecurity(boardId, 1);
			List<Long> managerIds = CommonTools.getEntityIds(managers);
			bbsIssueIds.removeAll(managerIds);
			bbsIssueIds.addAll(managerIds);
		}
		//判断是否是匿名发送
		if(request.getParameterValues("anonymous") == null){
			userMessageManager.sendSystemMessage(MessageContent.get("bbs.send", article.getArticleName(), issueUser.getName()).setBody(article.getContent(), "HTML", article.getIssueTime()),
					ApplicationCategoryEnum.bbs, issueUser.getId(),
					MessageReceiver.get(article.getId(), bbsIssueIds,"message.link.bbs.open", article.getId()), boardId);
		}else{
			userMessageManager.sendSystemMessage(MessageContent.get("bbs.anonymous.send", article.getArticleName(), this.getBbsI18NValue("anonymous.label")),
					ApplicationCategoryEnum.bbs, -1,
					MessageReceiver.get(article.getId(), bbsIssueIds,"message.link.bbs.open", article.getId()), boardId);
		}
		
		//系统日志
		appLogManager.insertLog(issueUser, AppLogAction.Bbs_Publish, userName, articleName, board.getName());
		if(!DEPARTMENTAffiliateroomFlag && issueAreas != null){ //部门讨论不存发布范围    
			if(issueAreas != null){
				for (String[] issueArea : issueAreas) {
					this.bbsArticleManager.addArticleIssueArea(article.getId(), issueArea[0], Long.parseLong(issueArea[1]));
				}
			}
		}
		 //自定义团队讨论保存发布范围
		if ("true".equals(custom) && issueAreas != null) {
			for (String[] issueArea : issueAreas) {
				this.bbsArticleManager.addArticleIssueArea(article.getId(), issueArea[0], Long.parseLong(issueArea[1]));
			}
		}
		//保存附件
		String attaFlag = attachmentManager.create(ApplicationCategoryEnum.bbs, article.getId(), article.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	article.setHasAttachments(true);
        }
		
		//在此加入全文检索
		try {
			IndexEnable indexEnable = (IndexEnable)bbsArticleManager;
			IndexInfo indexInfo=indexEnable.getIndexInfo(article.getId());
			indexManager.index(indexInfo);
		} catch (Exception e) {
			log.error("全文检索", e);
		}
		
		//如果是项目讨论,存入该项目下当前阶段
		ProjectSummary projectSummary = projectManager.getProject(boardId);
		if(projectSummary != null){
			if(projectSummary.getPhaseId() != 1){
    			ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.bbs.key(), article.getId(), projectSummary.getPhaseId());
    			projectPhaseEventManager.save(projectPhaseEvent);
    		}
		}
		
		if(DEPARTMENTAffiliateroomFlag){
			return super.redirectModelAndView("/bbs.do?method=deptlistAllArticle&departmentId=" + boardId + "&custom=" + custom);
		}else if(PROGECTAffiliateroomFlag){
			return super.redirectModelAndView("/bbs.do?method=moreProjectBbs&projectId=" + boardId+ "&managerFlag=" + request.getParameter("managerFlag") + "&custom=" + custom);
		}else{
			return super.redirectModelAndView("/bbs.do?method=listLatestFiveArticleAndAllBoard&id=" + boardId+"&group=" + group + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		}
	}
	
	/**
	 * showPost页面防护js动作：关闭窗口、刷新其父窗口(如果能够获取到的话)
	 */
	private String jsAction = "if(window.opener) " +
							  "    try {window.opener.getA8Top().reFlesh();}catch(e) {}" +
							  "window.close();";
	
	/**
	 * 查看讨论主题及对应的回复-Frame
	 */
	public ModelAndView showPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/showPostFrame");
		Long articleId = NumberUtils.toLong(request.getParameter("articleId"));
		String custom = request.getParameter("custom");
		V3xBbsArticle article = dataCache.get(articleId);
		if(article == null){
			article = bbsArticleManager.getArticleById(articleId);
		}
		 mav.addObject("article", article);
		 mav.addObject("custom", custom);
		 return mav;
	}

	/**
	 * 在bbs.xml中配置默认显示回复条数，目前设置为一页默认显示50条回复
	 */
	private int replyCounts;
	public void setReplyCounts(int replyCounts) {
		this.replyCounts = replyCounts;
	}
	
	//用于点击次数更新同步
	private final byte[] readCountLock = new byte[0];
	
	private boolean inIssueArea(String issueArea) {
		List<Long> areaIds = CommonTools.parseTypeAndIdStr2Ids(issueArea);
		List<Long> domainIds = CommonTools.getUserDomainIds(CurrentUser.get(), orgManager);
		return CollectionUtils.isNotEmpty(CommonTools.getIntersection(areaIds, domainIds));
	}
	
	/**
	 * 查看讨论主题及对应的回复
	 */
	public ModelAndView showPostFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/showPost");
		String from = request.getParameter("from");
		User user = CurrentUser.get();
		Long userId = user.getId();
		Long articleId = NumberUtils.toLong(request.getParameter("articleId"));
		V3xBbsArticle article = dataCache.get(articleId);
		boolean hasCache = article != null;
		if(article == null){
			article = bbsArticleManager.getArticleById(articleId);
		}
		
		Long boardId = null;
		V3xBbsBoard board = null;
		//判断当前用户是否是管理员("删除"总是可见）
		boolean isAdmin = false;
		boolean alert = false;
		if(article == null){
			alert = true;
		}else{
			boardId = article.getBoardId();
			isAdmin = bbsBoardManager.validUserIsAdmin(boardId, userId);
			board = this.bbsBoardManager.getBoardById(boardId);
			if(article.getState().intValue() == BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE || board == null){
				alert = true;
			}
			else { 
				String issueArea = this.getIssueAreaOfArticle(article, board);
				mav.addObject("issueArea", issueArea);
				if(!article.getIssueUserId().equals(userId) && !inIssueArea(issueArea) && !isAdmin) {
					alert = true;
				}
			}
		}
		if(alert){
			return mav.addObject("alertInfo", this.getBbsI18NValue("bbs.article.delorcanceled"));
		}
		article.setDepartment(orgManager.getMemberById(article.getIssueUserId()).getOrgDepartmentId());
		mav.addObject("article", article);
		mav.addObject("board", board);
		mav.addObject("currentUserIsAdmin", isAdmin);
		
		Long issueUserId = article.getIssueUserId();
		StaffInfo staff = staffInfoManager.getStaffInfoById(issueUserId);
		if(staff != null) {
			String issuerImage = staff.getSelf_image_name();
			if(StringUtils.isNotBlank(issuerImage))
				mav.addObject("image", issuerImage.startsWith("fileId") ? 0 : 1).addObject("issuerImage", issuerImage);
 		}
		
		String nowPagePara = request.getParameter("nowPagePara");
		int nowPage = 1; 		//当前页
		int size = 0;  			//总条数
		int pageSize = NumberUtils.toInt(request.getParameter("pageSizePara"), replyCounts); 		//每页显示条数，默认每页显示50条，用户可自定义
		
		int pages = 1;			//总页数
		int beginReply = 0; 	//开始显示的记录
		
		size = bbsArticleManager.countReplyByArticleId(articleId);
		pages = (size + pageSize - 1) / pageSize;	// 总页数
		if(pages==0){
			pages = 1;
		}
		if(Strings.isBlank(from)) {
			if (hasCache) {
				this.clickCache(articleId,userId);
			} else {
				int readCount = 0;
				synchronized(readCountLock){
					readCount = article.getClickNumber() == null ? 0 : article.getClickNumber().intValue();
					article.setClickNumber(readCount + 1);
				}
				// 保存到缓存
				this.syncCache(article, readCount + 1);
			}
		}
		
		if(Strings.isNotBlank(nowPagePara) && !nowPagePara.equals("1")){
            nowPage = NumberUtils.toInt(nowPagePara, 1);
			beginReply = (nowPage-1) * pageSize;
		} else if("reply".equals(from)){
			//从回复进来的显示最后一页
			nowPage = pages;
			beginReply = (nowPage-1)*pageSize;
		}

		mav.addObject("canModify", userId.equals(issueUserId));
		boolean canReply = bbsBoardManager.validReplyAuth(boardId, userId);
		mav.addObject("canReply", canReply);
		
		boolean canDeleteArticleFlag = false;
		if(isAdmin){
			canDeleteArticleFlag = true;
		}
		else{
			if(!(article.getEliteFlag() || (article.getTopSequence()!=0) || (!userId.equals(issueUserId)))){
				canDeleteArticleFlag = true;
			}
		}
		mav.addObject("canDeleteArticleFlag", canDeleteArticleFlag);
		mav.addObject("attachments", attachmentManager.getByReference(articleId));
		int orderFlag=0;
		try {
			orderFlag = board.getOrderFlag();
		} catch (Exception e) {
		}
		String orderValue="";
		if(orderFlag==1){
			orderValue="desc";
		}else{
			orderValue="asc";
		}
		List<V3xBbsArticleReply> replyList = bbsArticleManager.listReplyByArticleId(articleId, beginReply, pageSize,orderValue);

		List<ArticleReplyModel> replyModelList = this.getReplyModelList(replyList, isAdmin, user, article);

		mav.addObject("size", size);
		mav.addObject("pageSize", pageSize);
		mav.addObject("pages", pages);
		mav.addObject("nowPage", nowPage);
		mav.addObject("replyModelList", replyModelList);
		return mav;
	}
	
	/**
	 * 获取帖子的讨论范围，先从缓存的帖子属性中取，如取不到，再从数据库取一次
	 */
	private String getIssueAreaOfArticle(V3xBbsArticle article, V3xBbsBoard board) throws Exception {
		String result =null;// article.getIssueArea();
		if (StringUtils.isBlank(result)) {
			if (board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT.ordinal()) {
				result = this.getDeptBbsPublishScope(board.getId());
			} else if (board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.PROJECT.ordinal()) {
				result = this.getProjectBbsPublishScope(board.getId());
			} else {
				List<V3xBbsArticleIssueArea> areas = bbsArticleManager.getIssueArea(article.getId());
				if(CollectionUtils.isNotEmpty(areas)) {
					StringBuffer sb = new StringBuffer();
					for(V3xBbsArticleIssueArea area : areas) {
						sb.append(area.getModuleType() + "|" + area.getModuleId() + ",");
					}
					result = sb.substring(0, sb.length() - 1);
				}
			}
			article.setIssueArea(result);
		}
		return result;
	}
	
	/**
	 * 将讨论主题的回复组装以便在前端显示，抽取以便调用及单点维护
	 * @param replyList 讨论主题回复集合
	 * @param isAdmin   当前用户是否讨论版块管理员
	 * @param currentUser 当前用户
	 * @param article     讨论主题
	 */
	private List<ArticleReplyModel> getReplyModelList(List<V3xBbsArticleReply> replyList, boolean isAdmin, 
			User currentUser, V3xBbsArticle article) throws Exception {
		List<ArticleReplyModel> replyModelList = new ArrayList<ArticleReplyModel>();		
		if (replyList != null && replyList.size()>0) {
			Map<Long, V3xBbsArticleReply> map = new HashMap<Long, V3xBbsArticleReply>();
			for (V3xBbsArticleReply articleReply : replyList) {	
			map.put(articleReply.getId(), articleReply);
		}
			for(V3xBbsArticleReply articleReply : replyList){
				ArticleReplyModel replyModel = new ArticleReplyModel();
				StaffInfo staff = staffInfoManager.getStaffInfoById(articleReply.getReplyUserId());
				if(staff != null) {
					String imageInfo = staff.getSelf_image_name();
					if(StringUtils.isNotBlank(imageInfo)) {
						replyModel.setImageType(imageInfo.startsWith("fileId") ? "0" : "1");
						replyModel.setSelf_image_name(imageInfo);
					}
				}
				
				replyModel.setId(articleReply.getId());
				replyModel.setContent(articleReply.getContent());
				replyModel.setReplyName(articleReply.getReplyName());
				replyModel.setReplyTime(articleReply.getReplyTime());
				replyModel.setUseReplyFlag(articleReply.getUseReplyFlag());
				replyModel.setReplyUserId(articleReply.getReplyUserId());
				replyModel.setAnonymousFlag(articleReply.getAnonymousFlag());
				replyModel.setModifyTime(articleReply.getModifyTime());
				
				if (articleReply.getUseReplyFlag().byteValue() == (byte)3) {
					V3xBbsArticleReply refPost = map.get(articleReply.getUseReplyId());
					if(refPost == null)
						refPost = this.bbsArticleManager.getReplyPostById(articleReply.getUseReplyId());
					
					if(refPost != null) {
						replyModel.setRefPostContent(refPost.getContent());
						replyModel.setRefPostIssueTime(refPost.getReplyTime());
						V3xOrgMember refPostIssueUser = this.orgManager.getEntityById(V3xOrgMember.class, refPost.getReplyUserId());
						//只有真名发表的被引用回复，才把真实姓名传入作为引用内容显示
						if(refPost.getAnonymousFlag()==null || !refPost.getAnonymousFlag().booleanValue()) {
							replyModel.setRefPostIssueUserName(refPostIssueUser!= null ? refPostIssueUser.getName() : "");
						}
					}
				}		
				
				Byte canDeleteReplyPostFlag = 0;
				if(isAdmin){
					canDeleteReplyPostFlag = (byte)1;
				}
				else{
					if(!(article.getEliteFlag() || (article.getTopSequence()!=0) || (currentUser.getId()!=articleReply.getReplyUserId())) || currentUser.getId() == article.getIssueUserId()){
						canDeleteReplyPostFlag = (byte)1;
					}
				}
				replyModel.setCanBeDeleteFlag(canDeleteReplyPostFlag);
				
				Byte canBeEditedFlag = currentUser.getId()==articleReply.getReplyUserId() ? (byte)1 : 0;
				replyModel.setCanBeEditedFlag(canBeEditedFlag);
				replyModelList.add(replyModel);
			}
		}
		//Collections.sort(replyModelList);
		return replyModelList;
	}
	
	/**
	 * 回复讨论主题或修改自己的回复时，显示回复人姓名及其部门名称，格式："菜鸟杨(研发二部)"
	 */
	private String getReplyUserNameWithDeptName() {
		String result = null;
		try {
			result = CurrentUser.get().getName() + "(" + orgManager.getEntityById(V3xOrgDepartment.class, CurrentUser.get().getDepartmentId()).getName() + ")";
		} catch(BusinessException e) {
			log.error("", e);
		}
		return result;
	}
	
	/**
	 * 从讨论查看页面点击"编辑"，进入修改回复页面，页面中只允许修改正文和附件，其他内容均读入且置灰
	 * 为方便讨论发起人知道其回复人进行的操作，在修改回复时，用户可以选择是否发送修改该回复的消息给讨论主题发起人
	 */
	public ModelAndView toEditReply (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/replyArticle");
		String replyOrEdit = request.getParameter("replyOrEdit");
		if(Strings.isNotBlank(replyOrEdit)){
			mav.addObject("replyOrEdit", true);
		}
		Long postId = Long.parseLong(request.getParameter("postId"));
		V3xBbsArticleReply replyPost = bbsArticleManager.getReplyPostById(postId); 
		
		Long articleId = NumberUtils.toLong(request.getParameter("articleId"));
		V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
		boolean articleInValid = article==null || article.getState() == BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE;
		String articleInValidAlertInfo = this.getBbsI18NValue("bbs.article.delorcanceled");
		
		//如果回复被删除，但主贴未删除，返回主贴，如果主贴已删除，关闭窗口
		if(replyPost==null  || replyPost.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, "alert('" + (articleInValid ? articleInValidAlertInfo : this.getBbsI18NValue("bbs.reply.deleted")) + "');" + 
										   (articleInValid ? jsAction : "self.history.back();"));
			return  null;
		}
		mav.addObject("replyPost", replyPost);
		
		
		if(articleInValid){
			super.rendJavaScript(response, "alert('" + articleInValidAlertInfo +"');" + jsAction);
			return null;
		}
		mav.addObject("article", article);
		
		Long boardId = article.getBoardId();
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
		mav.addObject("board", board);
		
		return mav.addObject("replyUserName", this.getReplyUserNameWithDeptName()).addObject("attachments", attachmentManager.getByReference(articleId, postId));		
	}
	
	/** 
	 * 先从缓存取，如果没有，直接从数据库取
	 */
	private V3xBbsArticle getArticleFromCacheOrDB(Long articleId) throws Exception {
		V3xBbsArticle article = null;
		if(articleId != null) {
			article = dataCache.get(articleId);
			if(article == null) {
				article = this.bbsArticleManager.getArticleById(articleId);
			}
		}
		return article;
	}
	
	/**
	 * 修改当前用户所发的回复并保存，随后返回讨论讨论主题查看页面
	 */
	public ModelAndView editReply (HttpServletRequest request, HttpServletResponse response) throws Exception {
		String postIdStr = request.getParameter("postId");
		Long postId = postIdStr==null ? null : Long.parseLong(postIdStr);
		V3xBbsArticleReply reply = postId==null ? null : bbsArticleManager.getReplyPostById(postId); 
		
		Long articleId = reply==null ? null : reply.getArticleId();
		V3xBbsArticle article = articleId==null ? null : this.getArticleFromCacheOrDB(articleId);
		boolean articleInValid = article==null || article.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE;
		String articleInValidAlertInfo = this.getBbsI18NValue("bbs.article.delorcanceled");
		
		//如果回复被删除，但主贴未删除，返回主贴，如果主贴已删除，关闭窗口
		if(reply==null || reply.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, (articleInValid ? "" : "alert('" + this.getBbsI18NValue("bbs.reply.deleted") + "');") + 
					   					   "self.history.back();");
			return  null;
		}
		
		
		if(articleInValid){
			super.rendJavaScript(response, "alert('" + articleInValidAlertInfo +"');" + jsAction);
			return null;
		}
		
		reply.setContent(request.getParameter("content"));
		//保存正文内容之前，先判断正文内容是否为空，无任何文字但用户插入了图片，不算为空 2009-05-13 added
//		if(Strings.isBlank(com.seeyon.v3x.common.parser.StrExtractor.getHTMLContent(reply.getContent()))){
//			if(reply.getContent() == null || !reply.getContent().contains("<img ")){
//				super.rendJavaScript(response, "parent.validateReplyIsNotNull();");
//				return null;
//			}
//		}
		reply.setModifyTime(new Timestamp(System.currentTimeMillis()));
		bbsArticleManager.updateArticleReply(reply);
		
		//在对附件进行增删操作之后，保存附件
		String attaFlag = attachmentManager.update(ApplicationCategoryEnum.bbs, articleId, reply.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	article.setHasAttachments(true);
        }
        
		//是否将修改回复的消息发送给主题发起者，如果修改回复时选中了单选框，那么应将修改回复的信息发送给讨论主题发起人
		boolean sendMsg = Strings.isNotBlank(request.getParameter("messageNotifyFlag_checkbox"));
		if(sendMsg){
			User user = CurrentUser.get();
			Long receiverId = article.getIssueUserId();   //消息接受者为讨论主题的发起人
			MessageReceiver receiver = MessageReceiver.get(articleId, receiverId, "message.link.bbs.open", articleId);
			//给出发送消息内容：**修改了对您发起讨论主题《**》的回复《**》
			MessageContent content = MessageContent.get("bbs.reply.modify", 
					reply.getAnonymousFlag() ? this.getBbsI18NValue("anonymous.label") : user.getName() , article.getArticleName(), reply.getReplyName());
			Long senderId = reply.getAnonymousFlag() ? -1L : user.getId();
			userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.bbs, senderId, receiver, article.getBoardId());
		 }		
		
		//返回讨论主题查看页面
		return this.redirectModelAndView("/bbs.do?method=showPost&articleId="+articleId+"&pageSizePara="+request.getParameter("pageSizePara")+"&nowPagePara="+request.getParameter("nowPagePara"), "parent");
	}
	
	//初始化回复帖(liaoj)，显示板块名称、默认回复标题、回复者姓名以及回复类型
	public ModelAndView replyArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bbs/boarduse/replyArticle");
		Long articleId = NumberUtils.toLong(request.getParameter("articleId"));
		V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
		
		if(article==null || article.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, "alert('" + this.getBbsI18NValue("bbs.article.delorcanceled") +"');" + jsAction);
			return null;
		}
		mav.addObject("article", article);
		Long boardId = article.getBoardId();
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(boardId);
		mav.addObject("board", board);
		int useReplyFlag = Integer.parseInt(request.getParameter("useReplyFlag"));
		if (Strings.isNotBlank(request.getParameter("postId"))) {
			Long postId = Long.valueOf(request.getParameter("postId"));
			if (useReplyFlag == BbsConstants.REPLY_TYPE.referReply.ordinal()) {
				mav.addObject("useReplyId", postId);
			}
		}
		return mav.addObject("useReplyFlag", useReplyFlag).addObject("replyUserName", this.getReplyUserNameWithDeptName());
	}

	//创建回复帖
	public ModelAndView createReplyArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long articleId = Long.parseLong(request.getParameter("articleId"));
		V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
		if(article==null || article.getState()==BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE){
			super.rendJavaScript(response, "self.history.back();");
			return null;
		}
		
		V3xBbsArticleReply reply = new V3xBbsArticleReply();		
		reply.setIdIfNew();
		reply.setReplyName(request.getParameter("replyName"));
		reply.setState((byte) BbsConstants.BBS_ARTICLE_IS_ACTIVE);
		reply.setReplyTime(new Timestamp(System.currentTimeMillis()));
		reply.setReplyUserId(CurrentUser.get().getId());
		reply.setAnonymousFlag(Strings.isNotBlank(request.getParameter("anonymous")));
		
		
		int replyType = Integer.parseInt(request.getParameter("useReplyFlag"));
		if (replyType == BbsConstants.REPLY_TYPE.fast.ordinal()) {
			reply.setContent(Strings.toHTML(request.getParameter("content")));
		} else {
			reply.setContent(request.getParameter("content"));
		}
		reply.setUseReplyFlag((byte) replyType);
		
		//引用回复时，设定所引用的回复ID
		Long useReplyId = null;
		if (replyType == BbsConstants.REPLY_TYPE.referReply.ordinal()) {
			useReplyId = Long.valueOf(request.getParameter("useReplyId"));
			reply.setUseReplyId(useReplyId);
		}
		//引用他人回复进行回复时，给被引用的人发出提示消息
		if (replyType == BbsConstants.REPLY_TYPE.referReply.ordinal()) {
			useReplyId = Long.valueOf(request.getParameter("useReplyId"));
			V3xBbsArticleReply referReply = bbsArticleManager.getReplyPostById(useReplyId);
			MessageReceiver receiver = MessageReceiver.get(articleId, referReply.getReplyUserId(), "message.link.bbs.open", articleId);
			
			MessageContent content = MessageContent.get("bbs.refer.reply", 
					reply.getAnonymousFlag() ? this.getBbsI18NValue("anonymous.label") : user.getName() , article.getArticleName());
			Long senderId = reply.getAnonymousFlag() ? -1L : user.getId();
			
			userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.bbs, senderId, receiver, article.getBoardId());
		}
		//引用主题进行回复时，给被引用的人发出提示消息
		if (replyType == BbsConstants.REPLY_TYPE.referArticle.ordinal()) {
			V3xBbsArticle referArticle = bbsArticleManager.getArticleById(articleId);
			MessageReceiver receiver = MessageReceiver.get(articleId, referArticle.getIssueUserId(), "message.link.bbs.open", articleId);
			MessageContent content = MessageContent.get("bbs.refer.article", 
					reply.getAnonymousFlag() ? this.getBbsI18NValue("anonymous.label") : user.getName() , article.getArticleName());
			Long senderId = reply.getAnonymousFlag() ? -1L : user.getId();
			
			userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.bbs, senderId, receiver, article.getBoardId());
		}
//		if(replyType!=BbsConstants.REPLY_TYPE.fast.ordinal()) {
//			if(Strings.isBlank(com.seeyon.v3x.common.parser.StrExtractor.getHTMLContent(reply.getContent()))){
//				if(reply.getContent() == null || !reply.getContent().contains("<img ")){
//					super.rendJavaScript(response, "parent.validateReplyIsNotNull();");
//					return null;
//				}
//			}
//		}
		
		if(reply.getReplyName()==null) 
			reply.setReplyName("RE："+article.getArticleName());
		reply.setArticleId(articleId);

		//统一一下回复表的记录和文章表的回复记录数,解决并发情况下出现的问题
		//取出回复表中的总记录
		int size = bbsArticleManager.countReplyByArticleId(articleId);
		if(size >= article.getReplyNumber()) {
			article.setReplyNumber(size + 1);
		}
		bbsArticleManager.replyArticle(reply, size, article.getClickNumber());
		
		// 创建附件
		this.attachmentManager.create(ApplicationCategoryEnum.bbs, articleId, reply.getId(), request);
		//在此更新全文检索
		updateIndexManager.update(articleId, ApplicationCategoryEnum.bbs.getKey());
		// 发送消息给主题发起者
		boolean sendMsg = Strings.isNotBlank((request.getParameter("messageNotifyFlag_checkbox")));
		
		 if(sendMsg) {
			Long receiverId = article.getIssueUserId();
			Long senderId = reply.getAnonymousFlag() ? -1L : user.getId();
			
			if(receiverId.longValue() != senderId.longValue()){
				MessageReceiver receiver = MessageReceiver.get(articleId, receiverId, "message.link.bbs.open", articleId);
				MessageContent content = MessageContent.get("bbs.reply", reply.getAnonymousFlag() ? this.getBbsI18NValue("anonymous.label") : user.getName() , article.getArticleName());
				userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.bbs, senderId, receiver, article.getBoardId());
			}
		 }
            
		StringBuffer urlStr = new StringBuffer();
		urlStr.append("/bbs.do?method=showPost&articleId="+articleId);
		String pageSize = request.getParameter("pageSizePara");
		urlStr.append("&boardId=" + article.getBoardId() + "&from=reply&pageSizePara=" + (Strings.isBlank(pageSize) ? "" : pageSize));
		return this.redirectModelAndView(urlStr.toString(), "parent");
	}
	
	/**
	 * 在查看讨论主题页面点击"删除"删除单条主题，同时删除该主题下的所有回复帖(均为逻辑删除)
	 * 不同于管理员在管理界面进行批量删除操作
	 * @see #delArticle(HttpServletRequest, HttpServletResponse)
	 */
	public ModelAndView deleteArticle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long articleId = Long.valueOf(request.getParameter("articleId"));
		User user = CurrentUser.get();
		
		//删除全文检索，记录操作日志
		try{
			this.indexManager.deleteFromIndex(ApplicationCategoryEnum.bbs, articleId);
			V3xBbsArticle article = this.getArticleFromCacheOrDB(articleId);
			
			appLogManager.insertLog(user, AppLogAction.Bbs_Delete, user.getName(), article!=null ? article.getArticleName() : "");	 
		} catch(Exception e){
			log.error("", e);
		}
		bbsArticleManager.deleteArticle(articleId);
		bbsArticleManager.deleteReplyPostByArticleId(articleId);
		//缓存同步
		this.removeCache(articleId);
		
		super.rendJavaScript(response, "parent.closeAndRefreshWhenDeleted();");
		return null;
	}
	
	// 删除回复帖
	public ModelAndView deleteReplyPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long postId = Long.valueOf(request.getParameter("postId"));
		Long articleId = Long.parseLong(request.getParameter("articleId"));
		bbsArticleManager.deleteReplyPost(postId, articleId);
		
		StringBuffer urlStr = new StringBuffer();
		urlStr.append("bbs.do?method=showPost&articleId=" + request.getParameter("articleId"));
		String pageSize = request.getParameter("pageSizePara");
		String nowPage = request.getParameter("nowPagePara");
		urlStr.append("&boardId=" + request.getParameter("boardId") + "&pageSizePara=" + (Strings.isBlank(pageSize) ? "" : pageSize)
				+ "&nowPagePara=" + (Strings.isBlank(nowPage) ? "" : nowPage));
		return super.redirectModelAndView(urlStr.toString(), "parent");
	}
	
	//初始化搜索
	public ModelAndView initQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//论坛版块列表
		List<V3xBbsBoard> boardList = this.bbsBoardManager.getAllCorporationBbsBoard(CurrentUser.get().getLoginAccount());
		return new ModelAndView("bbs/boarduse/query").addObject("boardList", boardList.iterator());
	}
	
	/**
	 * 搜索主题
	 * @deprecated 该处应该已被废弃，且代码有误，如确有用，直接return null即可
	 */
	public ModelAndView query(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return null;
	}
	
	//列出所有版块信息
	public ModelAndView listAllBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();		
		ModelAndView mav = new ModelAndView("bbs/boardmanager/allboardmanage");
		boolean isGroup = false;
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		String group = request.getParameter("group");	
		if(Strings.isNotBlank(group) && !"false".equals(group)){
			isGroup = true;
			mav.addObject("group", "group");
		}	
		List<Long> boardIdList = null;
		List<BoardModel> boardModelList = new ArrayList<BoardModel>();
		List<String> boardNameList = new ArrayList<String>();
		
		Map<Long, V3xBbsBoard>	result = null;
		if(isGroup) {
			result = this.bbsBoardManager.getCanAdminGroupBoard(user.getId());
		} else {
			if(Strings.isNotBlank(spaceId)){
				result = this.bbsBoardManager.getCanAdminCustomBoard(user.getId(), Long.parseLong(spaceId), Integer.parseInt(spaceType));
			}else{
				result = this.bbsBoardManager.getCanAdminCorporationBoard(user.getId(), user.getLoginAccount());
			}
		}
		if(result!=null && result.size()>0) {
			boardIdList = new ArrayList<Long>();
			boardIdList.addAll(result.keySet());
		} else {
			if(Strings.isNotBlank(spaceId)){
				return super.redirectModelAndView("/bbs.do?method=listArticleMain&dept=&spaceType="+spaceType+"&spaceId="+spaceId);
			}else{
				return super.redirectModelAndView("/bbs.do?method=listArticleMain&dept=dept");
			}
		}
		
		Map<Long, Integer> boardsArticleNumber = this.bbsArticleManager.getBoardsArticleNumber4Admin(boardIdList);
		Map<Long, Integer> boardsElitePostNumber = this.bbsArticleManager.getBoardsElitePostNumber4Admin(boardIdList);
		V3xBbsBoard v3xBbsBoard = null;
		BoardModel boardModel = null;
		for (Long boardId : boardIdList) {
			v3xBbsBoard = result.get(boardId);
			//构造主题名称列表
			boardNameList.add(v3xBbsBoard.getName());
			boardModel = new BoardModel();
			boardModel.setBoard(v3xBbsBoard);
			//判断该版块是否有新帖
			if ((bbsArticleManager.hasNewTodayArticle(boardId)) || (bbsArticleManager.hasNewTodayReplyPost(boardId))) {
				boardModel.setHasNewPostFlag(true);
			} else {
				boardModel.setHasNewPostFlag(false);
			}
			boardModel.setId(boardId);
			boardModel.setBoardName(v3xBbsBoard.getName());
			boardModel.setBoardDescription(v3xBbsBoard.getDescription());
			// 计算该版块的主题数
			boardModel.setArticleNumber(toInt(boardsArticleNumber.get(boardId)));
			// 计算该版块的精华帖数
			boardModel.setElitePostNumber(toInt(boardsElitePostNumber.get(boardId)));
			boardModel.setIsAdminFlag(true);			
			boardModelList.add(boardModel);
		}

		mav.addObject("boardModelList", boardModelList);
		if(!isGroup){
			mav.addObject("unit", "unit");
		}
		return mav;
	}
	
	//查询板块的信息
	@SuppressWarnings("deprecation")
	public ModelAndView boardDoSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		
		ModelAndView mav = new ModelAndView("bbs/boardmanager/allboardmanage");
		boolean isGroup = false;
		String group = request.getParameter("group");
	
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			isGroup = true;
			mav.addObject("group", "group");
		}
		String  contaction  = request.getParameter("condition") ;  //接收方法下拉框的信息
		String  numContaction = request.getParameter("numCondition") ; //接收条件下拉框的信息
		if(numContaction == null){
			numContaction = "" ;
		}
		String  textfiled  = request.getParameter("textfield") ;  //接收文本框的信息
		if(textfiled == null){
			textfiled = "" ;
		}
		List<V3xBbsBoard> v3xBbsBoardList = new ArrayList<V3xBbsBoard>()  ;
		List<BoardModel> boardModelList = new ArrayList<BoardModel>();
		List<String> boardNameList = new ArrayList<String>();
		
		if(contaction != null && contaction.equals("title")){
			v3xBbsBoardList = this.bbsBoardManager.queryByName(textfiled,isGroup) ;
		}else{
			if(isGroup){
				v3xBbsBoardList = this.bbsBoardManager.getAllGroupBbsBoard();
			}else{
				v3xBbsBoardList = this.bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
			}		
		}
		Map<Long, Integer> boardsArticleNumber = this.bbsArticleManager.getBoardsArticleNumber(isGroup);
		Map<Long, Integer> boardsElitePostNumber = this.bbsArticleManager.getBoardsElitePostNumber(isGroup);

		for (V3xBbsBoard v3xBbsBoard : v3xBbsBoardList) {
			Long boardId = v3xBbsBoard.getId();
			Boolean isAdminFlag = bbsBoardManager.validUserIsAdmin(boardId, user.getId());
			if(!isAdminFlag){
				continue;
			}
			BoardModel boardModel = null;

			// 构造主题名称列表
			boardNameList.add(v3xBbsBoard.getName());

			boardModel = new BoardModel();
			boardModel.setBoard(v3xBbsBoard);

			// 判断该版块是否有新帖
			if ((bbsArticleManager.hasNewTodayArticle(boardId)) || (bbsArticleManager.hasNewTodayReplyPost(boardId))) {
				boardModel.setHasNewPostFlag(true);
			} else {
				boardModel.setHasNewPostFlag(false);
			}

			boardModel.setId(boardId);
			boardModel.setBoardName(v3xBbsBoard.getName());
			boardModel.setBoardDescription(v3xBbsBoard.getDescription());

			// 计算该版块的主题数
			boardModel.setArticleNumber(toInt(boardsArticleNumber.get(boardId)));
			// 计算该版块的精华帖数
			boardModel.setElitePostNumber(toInt(boardsElitePostNumber.get(boardId)));

			boardModel.setIsAdminFlag(isAdminFlag);
			
			if( numContaction != null && numContaction.equals("")  ){
				boardModelList.add(boardModel);	
			}else{
				try{  
					if(textfiled != null && textfiled.equals("") ){
					      boardModelList.add(boardModel);	
				       }else{
							int num = Integer.parseInt(textfiled) ;
							if(contaction != null && contaction.equals("subjectNumber")){
								if(numContaction.equals("equal")){
									if(boardModel.getArticleNumber() == num) 
										boardModelList.add(boardModel) ;
								}else if(numContaction.equals("more")){
									if(boardModel.getArticleNumber() > num) 
										boardModelList.add(boardModel) ;
								}else if(numContaction.equals("less")){
									if(boardModel.getArticleNumber() < num) 
										boardModelList.add(boardModel) ;
								}else{
									boardModelList.add(boardModel);	
								}
							}else {
								if(numContaction.equals("equal")){
									if(boardModel.getElitePostNumber() == num) 
										boardModelList.add(boardModel) ;
								}else if(numContaction.equals("more")){
									if(boardModel.getElitePostNumber() > num) 
										boardModelList.add(boardModel) ;
								}else if(numContaction.equals("less")){
									if(boardModel.getElitePostNumber() < num) 
										boardModelList.add(boardModel) ;
								}else{
									boardModelList.add(boardModel);	
								}
							}				    	   
				       }
				    	   

				 }catch(Exception e){					
					
				 }
				}

		}

		mav.addObject("boardModelList", boardModelList);
		if(!isGroup){
			mav.addObject("unit", "unit");
		}

		return mav;
	}	
	
	/**
	 * 讨论版块排序转向页面
	 */
	public ModelAndView orderBbsBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		User user = CurrentUser.get();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<V3xBbsBoard> bbsBoardList = new ArrayList<V3xBbsBoard>();
		if (Strings.isNotBlank(spaceId)) {
			bbsBoardList = bbsBoardManager.getAllCustomAccBbsBoard(Long.parseLong(spaceId), spaceTypeInt);
		} else {
			bbsBoardList = user.isGroupAdmin() ? bbsBoardManager.getAllGroupBbsBoard() : bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
		}
		return new ModelAndView("bbs/bbsmanager/orderBbsBoard").addObject("bbsBoardList", bbsBoardList);
	}
	
	/**
	 * 保存讨论版块排序结果
	 */
	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		bbsBoardManager.updateBbsBoardOrder(request.getParameterValues("projects"));			
		return super.refreshWorkspace();
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}
	
	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}
}