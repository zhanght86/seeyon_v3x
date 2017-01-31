package com.seeyon.v3x.bulletin.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulRead;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.domain.BulTypeManagers;
import com.seeyon.v3x.bulletin.domain.BulTypeModel;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulReadManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.bulletin.util.BulDataCache;
import com.seeyon.v3x.bulletin.util.BulDataLock;
import com.seeyon.v3x.bulletin.util.BulDataLockAction;
import com.seeyon.v3x.bulletin.util.BulReadCount;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.bulletin.util.hql.BulletinHqlUtils;
import com.seeyon.v3x.bulletin.util.hql.SearchInfo;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.cache.ClickDetail;

/**
 * 公告模块最重要的Controller，包括了普通用户、公告发起者、公告审核员、公告管理员的各种操作，全部方法的编排遵循以下的顺序：<br>
 * <b>1.普通用户：</b><br>
 * 1.1访问单位公告首页、集团公告首页、单位最新公告更多页面、集团最新公告更多页面、部门公告更多页面、某一特定板块公告更多页面；<br>
 * 1.2在以上各种页面按照公告发起者、公告标题及公告发布日期进行查询；<br>
 * 1.3用户点击已经发布的公告进行阅读（满足条件时也可查看该公告的阅读情况）。<br> 
 * <b>2.公告发起者：</b><br>
 * 2.1点击"发布公告"按钮进入查看自己发起的全部公告；<br>
 * 2.2新建公告、编辑公告、保存公告、发布审核通过的公告、删除公告（已发布或未发布的、真实删除）。<br> 
 * <b>3.公告审核员：</b><br>
 * 3.1在单位空间或集团空间中点击公共信息管理，进入其要审核的公告列表页面；<br>
 * 3.2查看待审核的公告详细信息、进行审核操作（直接发布、审核通过、审核不通过）、将审核通过的公告取消审核<br>
 * <b>4.公告管理员：</b><br>
 * 4.1点击"板块管理"按钮进入查看该板块下已发布的全部公告；<br>
 * 4.2对已发布的公告进行：置顶、取消发布、删除（逻辑删除）、归档、授权（发起公告权限）、统计（根据阅读次数、发起者、发起月份和状态（发布或已归档）进行统计）<br>。
 * @author wolf -- Edited by Rookie Young from 2009-04-08 on 
 */
public class BulDataController extends BaseController {
	private BulDataManager bulDataManager;
	private AttachmentManager attachmentManager;
	private OrgManager orgManager;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private BulReadManager bulReadManager;
	private AffairManager affairManager;	
	private AppLogManager appLogManager;	
	private DocHierarchyManager docHierarchyManager;	
	private SpaceManager spaceManager;  //部门空间的访问者
	private MtContentTemplateManagerCAP mtContentTemplateManagerCAP;    //公告格式，可以由单位管理员或集团管理员制定
	private UserMessageManager userMessageManager;
	private BulTypeManager bulTypeManager;
	private BulDataCache bulDataCache;
	private PortletEntityPropertyManager portletEntityPropertyManager;
	private static final Log log = LogFactory.getLog(BulDataController.class);
	
	/**
	 * 用户访问单位公告、集团公告主页，列出所有单位公告板块或集团公告板块下最新6条公告
	 * 在下面单独列出一行显示板块的管理员、审核员，并根据当前用户对该板块的权限（发起权限或管理权限）显示功能按钮"发布公告"、"板块管理"
	 */
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		String spaceId = request.getParameter("spaceId");
		ModelAndView mav = new ModelAndView("bulletin/user/data_list_iframe");
		int spaceTypeInt = NumberUtils.toInt(request.getParameter("spaceType"), Constants.BulTypeSpaceType.corporation.ordinal());
		User user = CurrentUser.get();
		Long userId = user.getId();
		mav.addObject("spaceType", spaceTypeInt);
		
		//当前访问的空间是单位空间还是集团空间
		List<BulType> typeList = null;
		boolean isNotInGroupSpace = (spaceTypeInt!=Constants.BulTypeSpaceType.group.ordinal());
		if (isNotInGroupSpace) {
			if (Strings.isNotBlank(spaceId)) {
				typeList = bulDataManager.getAllTypeListOfCustom(Long.parseLong(spaceId), spaceTypeInt);
				String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
				mav.addObject("spaceName", spaceName);
				mav.addObject("publicCustom", true);
			} else {
				typeList = bulDataManager.getAllTypeListExcludeDept();
				String accountName = orgManager.getAccountById(user.getLoginAccount()).getShortname();
				mav.addObject("accountName", accountName);
			}
		} else {
			typeList = bulDataManager.groupAllBoardList();
			String groupName = orgManager.getRootAccount(user.getLoginAccount()).getShortname();
			mav.addObject("groupName", groupName);
		}
		
		Map<Long, List<BulData>> map = null;
		List<List<BulData>> list2 = new ArrayList<List<BulData>>();
		List<BulTypeModel> btms = new ArrayList<BulTypeModel>();
		
		if (CollectionUtils.isNotEmpty(typeList)) {
			Collections.sort(typeList);
			
			try {
				map = bulDataManager.findByReadUserHome(userId, typeList);
			} catch (BusinessException e) {
				mav = new ModelAndView("bulletin/error");
				mav.addObject("_my_exception", e);
				return mav;
			}
			
			for(BulType bt : typeList) {
				list2.add(map.get(bt.getId()));
				btms.add(new BulTypeModel(bt, CommonTools.getUserDomainIds(userId, orgManager)));
			}

			String typeManagerIds = null;
			for (BulTypeModel typeWebModel : btms) {
				StringBuffer typeValidManagerIds = new StringBuffer("");
				typeManagerIds = typeWebModel.getBulType().getManagerUserIds();
				if (Strings.isNotBlank(typeManagerIds)) {
					String[] idStrs = typeManagerIds.split(",");
					for (String str : idStrs) {
						V3xOrgMember member = orgManager.getMemberById(Long.valueOf(str));
						//过滤掉被删除或被停用的板块管理员
						if (member != null && member.isValid()) {
							typeValidManagerIds.append(str + ",");
						}
					}
					typeWebModel.getBulType().setManagerUserIds(typeValidManagerIds.toString());
				}
			}
		}

		return mav.addObject("list2", list2).addObject("typeList", btms);
	}

	/**
	 * 用户在单位公告、集团公告首页按照公告发起者、公告标题及公告发布日期进行查询 
	 */
	public ModelAndView indexSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		//强制把所有的数据更新到数据库
		this.bulDataCache.getDataCache().updateAll();	
		ModelAndView mav = new ModelAndView("bulletin/user/bulletin_search");
		String spaceId = request.getParameter("spaceId");
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(request);
		int spaceTypeInt = NumberUtils.toInt(request.getParameter("spaceType"), Constants.BulTypeSpaceType.corporation.ordinal());
		mav.addObject("spaceType", spaceTypeInt);
		
		//当前访问的空间是单位空间还是集团空间
		boolean isNotInGroupSpace = (spaceTypeInt != Constants.BulTypeSpaceType.group.ordinal());
		User user = CurrentUser.get();
		List<BulData> list = null;
		long loginAccountId = CurrentUser.get().getLoginAccount();
		if (isNotInGroupSpace) {
			if(Strings.isNotBlank(spaceId)){
				list = this.bulDataManager.find4UserInAccount(user, Long.parseLong(spaceId), spaceTypeInt, searchInfo);
				mav.addObject("accountName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
			}else{
				list = this.bulDataManager.find4UserInAccount(user, loginAccountId, BulTypeSpaceType.corporation.ordinal(), searchInfo);
				String accountName = orgManager.getAccountById(CurrentUser.get().getLoginAccount()).getShortname();
				mav.addObject("accountName", accountName);
			}
		} else {
			V3xOrgAccount rootAccount = this.orgManager.getRootAccount();
			list = this.bulDataManager.find4UserInAccount(user, rootAccount.getId(), BulTypeSpaceType.group.ordinal(), searchInfo);
			String groupName = rootAccount.getShortname();
			mav.addObject("groupName", groupName);
		}
		
		return mav.addObject("list", list);
	}
	
	/**
	 * 统一更多页面：
	 * 单位空间中单位最新公告更多、集团空间中集团最新公告更多（空间中的全部公告板块）
	 * 单位公告首页中每一个单位公告板块更多、集团公告首页中每一个集团公告板块更多、部门空间中部门最新公告最多（单个公告板块）
	 */
	public ModelAndView bulMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		ModelAndView mav = new ModelAndView("bulletin/user/bul_more");
		String spaceId = request.getParameter("spaceId");
		int spaceType = NumberUtils.toInt(request.getParameter("spaceType"), Constants.BulTypeSpaceType.corporation.ordinal());
		Long typeId = NumberUtils.toLong(request.getParameter("typeId"), -1l);
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(request);
		String custom = request.getParameter("custom");
		List<BulData> list = null;
		//标题头名称
		String headerName = null;
		User user = CurrentUser.get();
		
		//自定义空间公告
		if (spaceType == Constants.BulTypeSpaceType.custom.ordinal()) {
			BulType bulType = this.bulTypeManager.getById(typeId);
			if(bulType == null || !bulType.isUsedFlag()) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bultype.deleted.label") + "');");
				return null;
			}
			headerName = bulType.getTypeName();
			list = bulDataManager.findByReadUserByType(typeId, searchInfo);
			List<Long> managerSpaces = spaceManager.getCanManagerSpace(user.getId());
			mav.addObject("spaceManagerFlag", managerSpaces.contains(typeId));
		} else if (spaceType == Constants.BulTypeSpaceType.department.ordinal()) { //部门公告
			Map<SpaceType, List<SpaceModel>> spacePath = this.spaceManager.getAccessSpace(user.getId(), user.getLoginAccount());
			List<SpaceModel> deptSpaceModels = spacePath.get(SpaceType.department);
			
			if(CollectionUtils.isEmpty(deptSpaceModels)) {
				PrintWriter out = response.getWriter();
		        super.printV3XJS(out);
				out.println("<script>");
				out.println("	alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "nodeptspace.label") + "');");
				out.println("	window.getA8Top().contentFrame.topFrame.realignSpaceMenu('" + user.getLoginAccount() + "');");
				out.println("</script>");
				out.flush();
				return super.redirectModelAndView("bulData.do?method=index");
			}
			mav.addObject("deptSpaceModels", deptSpaceModels);
			
			/**
			 * 当用户点击"部门公告"菜单时，部门ID参数值为空，需为其设定一个默认显示的部门公告：
			 * 	1.用户在本单位办公，如果其主岗所在部门开通部门空间，则默认显示其主岗所在部门的公告板块，反之，显示其可以访问的第一个部门空间对应公告板块；
			 * 	2.用户在兼职单位办公，显示其可以访问的第一个部门空间对应公告板块。
			 * 当用户点击部门空间的"部门公告"栏目底部"更多"按钮时，部门ID参数值不为空，此时页面指向指定的部门公告
			 */
			if(typeId == -1l) {
				boolean mainDeptSpaceOpened = this.spaceManager.isCreateDepartmentSpace(user.getDepartmentId());
				typeId = user.getLoginAccount() == user.getAccountId() && mainDeptSpaceOpened ? user.getDepartmentId() : deptSpaceModels.get(0).getEntityId();
			}
			try {
				headerName = this.orgManager.getDepartmentById(typeId).getName();
			} catch(Exception e) {
				log.error("获取部门[id=" + typeId + "]名称时出现异常", e);
			}
			Pagination.setNeedCount(true);
			list = this.bulDataManager.deptFindByReadUser(typeId, user.getId(), searchInfo);
			boolean managerFlag = this.bulTypeManager.isManagerOfThisDept(user.getId(), typeId);
	  		mav.addObject("managerFlag", managerFlag);
		} else {//单位公告或集团公告
			//单位最新公告、集团最新公告栏目底部点击"更多"按钮
			if(typeId == -1l) {
				// 栏目内容来源选择板块，需要过滤
				List<Long> selectTypeList = null;
				try {
					String fragmentId = request.getParameter("fragmentId");
					if (Strings.isNotBlank(fragmentId)) {
						String ordinal = request.getParameter("ordinal");
						Map<String, String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
						String panelValue = request.getParameter("panelValue");
						if (Strings.isNotBlank(panelValue)) {
							String typeIds = preference.get(panelValue);
							selectTypeList = CommonTools.parseStr2Ids(typeIds);
						}
					}
				} catch (Exception e) {
					log.error("", e);
				}
				
				if(spaceType == Constants.BulTypeSpaceType.corporation.ordinal()) {
					if (selectTypeList != null) {
						list = bulDataManager.findByReadUserForIndex(user, -1, selectTypeList, BulTypeSpaceType.corporation, searchInfo);
					} else {
						list = bulDataManager.find4UserInAccount(user, user.getLoginAccount(), spaceType, searchInfo);
					}
					
					headerName = orgManager.getAccountById(user.getLoginAccount()).getName();
				} else if (spaceType == Constants.BulTypeSpaceType.public_custom.ordinal() || spaceType == Constants.BulTypeSpaceType.public_custom_group.ordinal()) {
					list = bulDataManager.find4UserInAccount(user, Long.parseLong(spaceId), spaceType, searchInfo);
					headerName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
					mav.addObject("publicCustom", true);
				} else {
					V3xOrgAccount group = orgManager.getRootAccount(user.getLoginAccount());
					
					if (selectTypeList != null) {
						list = bulDataManager.findByReadUserForIndex(user, -1, selectTypeList, BulTypeSpaceType.group, searchInfo);
					} else {
						list = bulDataManager.find4UserInAccount(user, group.getId(), spaceType, searchInfo);
					}
					
					headerName = group.getName();
				}
			} 
			//单个版块的"更多"
			else {
				BulType bulType = this.bulTypeManager.getById(typeId);
				if(bulType == null || !bulType.isUsedFlag()) {
					super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bultype.deleted.label") + "');" +
												   "self.history.back();");
					return null;
				}
				headerName = bulType.getTypeName();
				list = bulDataManager.findByReadUserByType(typeId, searchInfo);
			}
		}
		return mav.addObject("list", list).addObject("typeId", typeId == -1l ? null : typeId).addObject("headerName", headerName).addObject("custom", "true".equals(custom) ? true : false);
	}
	
	/**
	 * 我的公告：更多页面、查询动作
	 */
	public ModelAndView myBulMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(request);
		List<BulData> list = bulDataManager.findMyBulDatas(CurrentUser.get(), searchInfo, false);
		return new ModelAndView("bulletin/user/my_bul_more", "list", list);
	}
	
	private final Object readCountLock = new Object();
	/**
	 * 点击公告连接，阅读已发布的公告
	 */
	public ModelAndView userView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isBlank(idStr)) {
			log.info("bul.not_exists");
			throw new BulletinException("bulletin.not_exists");
		}
		long dataId = Long.valueOf(idStr);
		BulData bean = this.bulDataCache.getDataCache().get(dataId);
		boolean hasCache = false;
		if (bean == null) {
			bean = bulDataManager.getById(Long.valueOf(idStr));
			List<BulRead> readList = this.bulDataManager.getReadListByData(bean.getId());
			if(!readList.isEmpty() && bean.getReadCount()<readList.size()){
				bean.setReadCount(readList.size());
			}
		} else {
			log.info("bull.id." + bean.getId() + " is cached.");
			hasCache = true;
		}

		String fromPigeonhole = request.getParameter("fromPigeonhole");
		ModelAndView mav = new ModelAndView("bulletin/user/data_view");
		//防护：已发布的公告被删除或被管理员归档，其他用户点击系统消息链接时候给出提示<归档的公告，在文档中心处可以正常查看>;从归档文档查看总是可以看到
		if ((bean==null || !bean.getType().isUsedFlag() || bean.isDeletedFlag() || checkScope(bean) == false
				|| (bean.getState().intValue()!= Constants.DATA_STATE_ALREADY_PUBLISH && bean.getState().intValue() != Constants.DATA_STATE_ALREADY_PIGEONHOLE)
				|| (bean.getState().intValue() == Constants.DATA_STATE_ALREADY_PIGEONHOLE && !"true".equals(fromPigeonhole))) && !"true".equals(fromPigeonhole)) {
			log.info("ex022...bul...bean is null:" + (bean == null));
			if (bean != null) {
				log.info("ex022...bul...bean is from Cache:" + hasCache);
				log.info("ex022...bul...bean.id:" + bean.getId());
				log.info("ex022...bul...bean.type.id:" + bean.getType().getId());
				log.info("ex022...bul...bean.type.isUsed:" + bean.getType().isUsedFlag());
				log.info("ex022...bul...bean.isDeletedFlag:" + bean.isDeletedFlag());
				log.info("ex022...bul...checkScope(bean):" + checkScope(bean));
				log.info("ex022...bul...bean.publishScope:" + bean.getPublishScope());
				log.info("ex022...bul...bean.getState():" + bean.getState());
				log.info("ex022...bul...fromPigeonhole:" + fromPigeonhole);
			}
			return mav.addObject("dataExist", false);
		} else {
			if(bean == null){
				return mav.addObject("dataExist", false);
			}else{
				mav.addObject("dataExist", true);
			}
		}
		if(Strings.isNotBlank(spaceId)){
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		}
		User user = CurrentUser.get();
		//SECURITY 访问安全检查
    	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.bulletin, user, dataId, null, null)){
    		return null;
    	}
		
		recordBulRead(dataId, bean, hasCache, user);

		// 如果在公告创建时没选择访问信息的话，这个地方就不取阅读信息了
		Long userId = CurrentUser.get().getId();
		boolean isManager = false;
		if (Integer.parseInt(bean.getExt1()) == 1) {
			if(userId.longValue() == bean.getCreateUser().longValue())
				isManager = true;
			else
				isManager = this.bulTypeManager.isManagerOfType(bean.getTypeId(), userId);
		}
		mav.addObject("isManager", isManager);

		mav.addObject("bean", bean);

		if (bean.getAttachmentsFlag()) {
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId());
			mav.addObject("attachments", attachments);
		} else 
			mav.addObject("attachments", new ArrayList<Attachment>());
		
		//传入公告样式标识参数，以便显示不同样式
		return mav.addObject("bulStyle", bean.getType().getExt1());
	}
	
	/** 判断当前用户是否在公告发布范围内  */
	public boolean checkScope(BulData bulData) {
		String scopeId = bulData.getPublishScope();
		Long createId = bulData.getCreateUser();
		Long publishId = bulData.getPublishUserId();
		Long auditId = bulData.getAuditUserId();
		Long userId = CurrentUser.get().getId();
		//自定义团队空间  空间管理员默认有权
		if(bulData.getType().getSpaceType()==4){
			List<Long> managerSpaces = spaceManager.getCanManagerSpace(userId);
			if(CollectionUtils.isNotEmpty(managerSpaces) && managerSpaces.contains(bulData.getType().getId()))
				return true;
		}
		boolean isManager = this.bulTypeManager.isManagerOfType(bulData.getTypeId(), userId);
		if (userId.equals(createId) || userId.equals(publishId) || userId.equals(auditId) || isManager) {
			return true;
		} else {
			List<Long> scopeIds = CommonTools.parseTypeAndIdStr2Ids(scopeId);
			List<Long> ids = CommonTools.getUserDomainIds(CurrentUser.get(), orgManager);
			List<Long> intersectIds = CommonTools.getIntersection(scopeIds, ids);
			if (intersectIds ==null || intersectIds.size() == 0) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/** 显示阅读信息的列表，与查看公告基本信息分离开来，避免因为阅读信息过多导致公告查看产生性能问题  */
	public ModelAndView showReadList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/user/data_read_view");
		Long dataId = NumberUtils.toLong(request.getParameter("id"));
		BulData bean = this.bulDataCache.getDataCache().get(dataId);
		if (bean == null) 
			bean = bulDataManager.getById(dataId);
		this.setBulDataReadInfo(bean, mav, null);
		return mav;
	}

	/**
	 * 记录阅读公告信息
	 */
	public void recordBulRead(long dataId, BulData bean, boolean hasCache, User user) {
		this.bulReadManager.setReadState(bean, user.getId());
		if (hasCache) {
			this.clickCache(dataId, CurrentUser.get().getId());
		} else {
			BulBody body = bulDataManager.getBody(bean.getId());
			bean.setContent(body.getContent());
			bean.setContentName(body.getContentName());

			// 增加阅读次数
			int readCount = 0;
			synchronized(readCountLock){
				readCount = bean.getReadCount() == null ? 0 : bean.getReadCount().intValue();
				bean.setReadCount(readCount + 1);
			}
			// 保存到缓存
			this.syncCache(bean, readCount + 1);
		}
	}
	
	public void clickCache(Long dataId,Long userId) {
		this.bulDataCache.getDataCache().click(dataId, new ClickDetail(userId, new Timestamp(System.currentTimeMillis())));
		BulData bean = this.bulDataCache.getDataCache().get(dataId);
		if(bean==null) return;
		synchronized(readCountLock){
			bean.setReadCount(this.bulDataCache.getDataCache().getClickTotal(dataId));
		}
		//发送消息
		NotificationManager.getInstance().send(NotificationType.BulDataClickArticle, new CacheInfo(dataId, userId));
	}
	
	public void syncCache(BulData bean, int clickCount) {
		this.bulDataCache.getDataCache().save(bean.getId(), bean, System.currentTimeMillis(), clickCount);
		//发送消息
		NotificationManager.getInstance().send(NotificationType.BulDataModifyArticle, new CacheInfo(bean.getId(), clickCount));
	}
	
	/**
	 * 公告发起者进入发布首页：最外围有边框的IFrame
	 */
	public ModelAndView publishListIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/write/data_list_publish_index");
		String bultypeId = request.getParameter("bulTypeId");
		String spaceType = request.getParameter("spaceType");
		User user = CurrentUser.get();
		if ("2".equalsIgnoreCase(spaceType)) {
			List<Long> managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
			mav.addObject("isSpaceManager", managerDepartments.contains(Long.valueOf(bultypeId)));
		}
		return mav;
	}

	/**
	 * 公告发起者进入发布首页：上下结构FrameSet
	 */
	public ModelAndView publishListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/write/data_list_publish_main");
	}

	/**
	 * 公告发起者进入发布首页：发起者发起的公告列表
	 */
	public ModelAndView publishList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("bulTypeId");
		Long typeId = Strings.isNotBlank(typeIdStr) ? Long.valueOf(typeIdStr) : null;
		BulType type = typeId!=null ? this.bulTypeManager.getById(typeId) : null;
		if (type==null || !type.isUsedFlag()) {
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bultype.deleted.label") + "');self.history.back();");
			return null;
		}
		List<BulData> list = bulDataManager.findWriteAll(typeId, CurrentUser.get().getId());
		return new ModelAndView("bulletin/write/data_list_publish").addObject("list", list).addObject("bulType", type);
	}
	
	/**
	 * 公告发起者创建公告
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("bulletin/write/data_create");
		String bulTypeIds = request.getParameter("bulTypeId");
		String custom = request.getParameter("custom");
		BulData bean = new BulData();
		bean.setCreateDate(new Timestamp(System.currentTimeMillis()));
		bean.setCreateUser(user.getId());
		bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		bean.setReadCount(0);
		String spaceId = request.getParameter("spaceId");
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.BulTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)) {
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
			if(spaceTypeInt==2){
				List<Long> departmentListId = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
				List<V3xOrgDepartment> canAccessDepartment = new ArrayList<V3xOrgDepartment>();
				for(Long depId : departmentListId){
					if(spaceManager.isCreateDepartmentSpace(depId)){
						canAccessDepartment.add(orgManager.getDepartmentById(depId));
					}
				}
				mav.addObject("departmentList", canAccessDepartment);
				mav.addObject("departmentListLength", canAccessDepartment.size());
			}
		}
		mav.addObject("spaceType", spaceTypeInt);
		BulType type = this.bulTypeManager.getById(Long.valueOf(bulTypeIds));
		bean.setTypeId(Long.valueOf(bulTypeIds));
		bean.setType(type);		
		
		if (type.getSpaceType().intValue() == Constants.BulTypeSpaceType.custom.ordinal()) {
			StringBuffer publisthScopeDep = new StringBuffer();
			List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(type.getId());
			for(Object[] arr : issueAreas) {
				publisthScopeDep.append(StringUtils.join(arr, "|") + ",");
			}
			bean.setPublishScope(publisthScopeDep.substring(0, publisthScopeDep.length() - 1));
		} else if (type.getSpaceType().intValue() == Constants.BulTypeSpaceType.public_custom.ordinal()) {
			StringBuffer publisthScopeDep = new StringBuffer();
			List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(Long.parseLong(spaceId));
			for(Object[] arr : issueAreas) {
				publisthScopeDep.append(StringUtils.join(arr, "|") + ",");
			}
			bean.setPublishScope(publisthScopeDep.substring(0, publisthScopeDep.length() - 1));
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		} else if (type.getSpaceType().intValue() == Constants.BulTypeSpaceType.public_custom_group.ordinal()) {
			StringBuffer publisthScopeDep = new StringBuffer();
			List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(Long.parseLong(spaceId));
			for(Object[] arr : issueAreas) {
				publisthScopeDep.append(StringUtils.join(arr, "|") + ",");
			}
			bean.setPublishScope(publisthScopeDep.substring(0, publisthScopeDep.length() - 1));
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		} else if (type.getSpaceType().intValue() == Constants.BulTypeSpaceType.department.ordinal()) {
			//对部门公告的发布范围单独处理,需要加上本部门,部门访问者
			StringBuffer publisthScopeDep = new StringBuffer();
			publisthScopeDep = publisthScopeDep.append("Department|" + type.getId().toString());
			List<Object[]> _issueAreas = this.spaceManager.getSecuityOfDepartment(type.getId());
			for (Object[] objects : _issueAreas) {
				if (V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equalsIgnoreCase(objects[0] + "") && type.getId()!=((Long) objects[1]).longValue()) {
					publisthScopeDep = publisthScopeDep.append(",Department|" + objects[1]);
				} else if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equalsIgnoreCase(objects[0] + "")) {
					// 人员的ID是当前部门的,应该去掉
					if (orgManager.getMemberById((Long) objects[1]).getOrgDepartmentId() != user.getDepartmentId()) {
						publisthScopeDep = publisthScopeDep.append(",Member|" + objects[1]);
					}
				} else if (V3xOrgEntity.ORGENT_TYPE_TEAM.equalsIgnoreCase(objects[0] + "")) {
					publisthScopeDep = publisthScopeDep.append(",Team|" + objects[1]);
				} 
				//以下为解决此客户BUG：重要A8BUG_V5.71_英利集团（NC-OA） _部门空间授权给全单位，其他部门的人打开部门公告提示：此公告已被删除、取消发布或归档，您无法继续查看其内容
				else if (V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equalsIgnoreCase(objects[0] + "")) {//单位
					publisthScopeDep = publisthScopeDep.append(",Account|" + objects[1]);
				} else if (V3xOrgEntity.ORGENT_TYPE_POST.equalsIgnoreCase(objects[0] + "")) {//岗位
					publisthScopeDep = publisthScopeDep.append(",Post|" + objects[1]);
				} else if (V3xOrgEntity.ORGENT_TYPE_LEVEL.equalsIgnoreCase(objects[0] + "")) {//职级
					publisthScopeDep = publisthScopeDep.append(",Level|" + objects[1]);
				}
			}
			bean.setPublishScope(publisthScopeDep.toString());
		}

		//设置发布部门为当前用户所在部门
		if (bean.getPublishDepartmentId()==null) 
			bean.setPublishDepartmentId(user.getDepartmentId());

		//处理模板加载
		String oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper)) {
			super.bind(request, bean);
			if (request.getParameterValues("noteCallInfo") != null) {
				bean.setExt1("1");// 选中
			} else {
				bean.setExt1("0");// 未选中
			}
			if (request.getParameterValues("printAllow") != null) {
				bean.setExt2("1");// 选中
			} else {
				bean.setExt2("0");// 未选中
			}
			String templateId = request.getParameter("templateId");
			if (StringUtils.isNotBlank(templateId)) {
				MtContentTemplateCAP template = mtContentTemplateManagerCAP.getById(Long.valueOf(templateId));
				if (template != null) {
					bean.setDataFormat(template.getTemplateFormat());
					bean.setContent(template.getContent());
					bean.setCreateDate(new Timestamp(template.getCreateDate().getTime()));
					mav.addObject("templateId", template.getId());
				}
				mav.addObject("originalNeedClone", true);
			} else {
				bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
				bean.setContent(null);
			}
		} else {
			String templateId = "";
			templateId = bean.getType().getDefaultTemplate() == null ? "" : bean.getType().getDefaultTemplate().getId().toString();
			if (StringUtils.isNotBlank(templateId)) {
				MtContentTemplateCAP template = mtContentTemplateManagerCAP.getById(Long.valueOf(templateId));
				bean.setDataFormat(template.getTemplateFormat());
				bean.setContent(template.getContent());
				bean.setCreateDate(new Timestamp(template.getCreateDate().getTime()));
				mav.addObject("templateId", template.getId());
				mav.addObject("originalNeedClone", true);
			}
		}

		//处理附件,默认的是不管从正常切换到正常的格式,还是正常的格式转换到格式附件都不应该丢的
		String attaFlag = null;
		//第一次或者说新建的时候bean.getId()肯定是空的
		Long attRefId = null;
		//是不是第一次,true为第一次,false为不是第一次
		String attFlagStr = request.getParameter("attFlag");
		boolean attFlag = true;
		if ("false".equalsIgnoreCase(attFlagStr)) {
			attFlag = false;
		}
		//点击修改时候进来的,这个时候公告已有ID了
		String idStr = request.getParameter("id");
		if (Strings.isNotBlank(idStr)) {
			bean.setId(Long.valueOf(idStr));
			attachmentManager.deleteByReference(bean.getId(), bean.getId());
			attaFlag = attachmentManager.create(ApplicationCategoryEnum.bulletin, bean.getId(), bean.getId(), request);
			attFlag = false;
			mav.addObject("attRefId", attRefId);
			mav.addObject("attFlag", attFlag);
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		} else {
			if (attFlag) {
				//第一次切换时执行以下方法
				Long newId = UUIDLong.longUUID();
				attaFlag = attachmentManager.create(ApplicationCategoryEnum.bulletin, newId, newId, request);
				attRefId = newId;
				attFlag = false;
				mav.addObject("attRefId", attRefId);
				mav.addObject("attFlag", attFlag);
			} else {
				//第二次切换,会传递一个ID,应该先删除原来的附件,再创建一个ID;
				attRefId = Long.valueOf(request.getParameter("attRefId"));
				attachmentManager.deleteByReference(attRefId, attRefId);
				attaFlag = attachmentManager.create(ApplicationCategoryEnum.bulletin, attRefId, attRefId, request);
				attFlag = false;
				mav.addObject("attRefId", attRefId);
				mav.addObject("attFlag", attFlag);
			}
			List<Attachment> attachments = attachmentManager.getByReference(attRefId, attRefId);
			mav.addObject("attachments", attachments);
		}
		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)) {
			bean.setAttachmentsFlag(true);
		}

		int spaceType = type.getSpaceType();
		Constants.BulTypeSpaceType bulTypeSpaceType = Constants.valueOfSpaceType(spaceType);
		List<BulType> bulTypeList = new ArrayList<BulType>();
		if (Strings.isNotBlank(spaceId)) {
			bulTypeList = this.bulTypeManager.getTypesCanCreate(user.getId(), bulTypeSpaceType, Long.parseLong(spaceId));
		} else {
			bulTypeList = this.bulTypeManager.getTypesCanCreate(user.getId(), bulTypeSpaceType, user.getLoginAccount());
		}

		mav.addObject("bulTypeList", bulTypeList);
		mav.addObject("bean", bean);
		mav.addObject("constants", new Constants());
		mav.addObject("custom", custom != null ? true : false);
		if (spaceTypeInt == 0) {	// 集团
			mav.addObject("templateList", mtContentTemplateManagerCAP.findGroupTypeAll("3"));// 统一接口 3 公告
		} else {	// 单位和部门
			mav.addObject("templateList", mtContentTemplateManagerCAP.findTypeAllNoPage("3"));	  // 统一接口 3 公告
		}
		return mav;
	}	

	/**
	 * 公告发起者编辑公告
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		BulData bean;
		if (StringUtils.isBlank(idStr)) {
			bean = new BulData();
		} else {
			bean = this.bulDataCache.getDataCache().get(Long.valueOf(idStr));
			if(bean==null) 
				bean = bulDataManager.getById(Long.valueOf(idStr));
			
			//如果管理员已将公告删除
			if(bean==null || bean.isDeletedFlag()) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, 
												"bul.data.noexist") + "');self.history.back();");
				return null;
			}
			BulBody body=bulDataManager.getBody(bean.getId());
			bean.setContent(body.getContent());
			bean.setContentName(body.getContentName());
		}
		
		User user = CurrentUser.get();
		/**
		 * 检测当前操作是否可以继续：
		 * 1.如果当前公告被审核员操作锁定，则不允许进行编辑操作，返回；
		 * 2.如果编辑页面解锁失败，当前公告仍被当前用户的编辑操作锁定，由于编辑操作只有当前用户才能进行，也允许其进行编辑操作，避免自己被自己锁住。
		 */
		String action = BulDataLockAction.NEWLOCK_EDITING;
		BulDataLock bullock = bulDataManager.lock(Long.valueOf(idStr), action);
		if (bullock!=null && !BulDataLockAction.NEWLOCK_EDITING.equals(bullock.getAction()) && bullock.getUserid()!=user.getId()) {
			V3xOrgMember orm = orgManager.getMemberById(bullock.getUserid());
			String lockmessage = bullock.getAction();
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, 
														lockmessage, orm.getName()) + "');self.history.back();");

			return null;
		}

		ModelAndView mav = new ModelAndView("bulletin/write/data_create");
		mav.addObject("bean", bean);
		List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);

		
		int spaceType = bean.getType().getSpaceType();
		Constants.BulTypeSpaceType bulTypeSpaceType = Constants.valueOfSpaceType(spaceType);
		List<BulType> bulTypeList = new ArrayList<BulType>();
		if (Strings.isNotBlank(spaceId)) {
			bulTypeList = this.bulTypeManager.getTypesCanCreate(user.getId(), bulTypeSpaceType, Long.parseLong(spaceId));
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		} else {
			bulTypeList = this.bulTypeManager.getTypesCanCreate(user.getId(), bulTypeSpaceType, user.getLoginAccount());
		}
		mav.addObject("bulTypeList", bulTypeList);
		mav.addObject("spaceType", spaceType);
		if (spaceType == 0) {// 集团
			mav.addObject("templateList", mtContentTemplateManagerCAP.findGroupTypeAll("3"));// 统一接口 3 公告
		} else {// 单位和部门
			mav.addObject("templateList", mtContentTemplateManagerCAP.findTypeAll("3"));// 统一接口 3 公告
		}
		return mav;
	}

	/**
	 * 公告发起员保存公告的操作
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dataformat = request.getParameter("dataFormat");
		String ext5 = request.getParameter("ext5");
		String spaceId = request.getParameter("spaceId");
		String custom = request.getParameter("custom");
		BulData bean = null;		
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		
		boolean isPublish = false;
		String idStr = request.getParameter("id");
		int oldState = Constants.DATA_STATE_NO_SUBMIT;
		if (StringUtils.isBlank(idStr)) {
			bean = new BulData();
		} else {
			bean = this.bulDataCache.getDataCache().get(Long.valueOf(idStr));
			if (bean == null) {
				bean = bulDataManager.getById(Long.valueOf(idStr));
			}

			//为下面发消息做判断：被修改的公告是未审核、还是审核未通过，以便发送不同的系统消息
			if (bean.getState() == Constants.DATA_STATE_ALREADY_CREATE) {
				oldState = Constants.DATA_STATE_ALREADY_CREATE;
			} else if (bean.getState() == Constants.DATA_STATE_NOPASS_AUDIT) {
				oldState = Constants.DATA_STATE_NOPASS_AUDIT;
			} else if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH) {
				oldState = Constants.DATA_STATE_ALREADY_PUBLISH;
			}
		}
		super.bind(request, bean);
		Long typeId = bean.getTypeId();
		boolean flag = (Strings.isNotBlank(spaceId) && bean.getAccountId() == null);
		BulType type = this.bulTypeManager.getById(typeId);
		bean.setType(type);

		String form_oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(form_oper)) {
			if (form_oper.equals("draft"))
				bean.setState(Constants.DATA_STATE_NO_SUBMIT);
			else if (form_oper.equals("submit")) {
				//如当前公告类型有审核员，用户点击"发送"按钮时，公告状态设定为"已经提交，还未审核"
				if (type.isAuditFlag()) {
					bean.setState(Constants.DATA_STATE_ALREADY_CREATE);
				} else {
					//如果当前公告类型无审核员，用户点击"发送"按钮时，即为直接发布公告，公告状态设定为"已经发布，还未归档"
					//此时将该公告最终审核记录设置为"无审核"
					bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_NO));
					bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
					bean.setPublishDate(new Timestamp(new Date().getTime()));
					bean.setPublishUserId(CurrentUser.get().getId());
					isPublish = true;
				}
			}
		} else {
			bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		}
		Boolean firstIndexFlag = false;
		if (bean.isNew()) {
			bean.setCreateDate(new Timestamp(System.currentTimeMillis()));
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setTopOrder(new Byte("0"));
			firstIndexFlag = true;
		}

		//访问记录信息
		if (request.getParameterValues("noteCallInfo") != null) {
			bean.setExt1("1");// 选中
		} else {
			bean.setExt1("0");// 未选中
		}
		
		//是否允许打印
		if (request.getParameterValues("printAllow") != null) {
			bean.setExt2("1");// 选中
		} else {
			bean.setExt2("0");// 未选中
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(userId);
		bean.setDataFormat(dataformat);
		bean.setExt5(ext5);

		//已经提交审核的如果修改，需要发送消息
		boolean editAfterAudit = (oldState == Constants.DATA_STATE_ALREADY_CREATE);
		//审核没有通过的修改
		boolean noAuditEdit = (oldState == Constants.DATA_STATE_NOPASS_AUDIT);
		//不需要审核的板块发起者可直接修改已发布的公告, 不需要管理员先撤消
		boolean noAuditPublishEdit = (oldState == Constants.DATA_STATE_ALREADY_PUBLISH);

		boolean isNew = false;
		String attaFlag = null;
		if (bean.isNew()) {
			bean.setIdIfNew();
			isNew = true;
			long attRefId = Long.valueOf(request.getParameter("attRefId"));
			attachmentManager.deleteByReference(attRefId, attRefId);
			bean.setAttachmentsFlag(false);
			attaFlag = attachmentManager.create(ApplicationCategoryEnum.bulletin, bean.getId(), bean.getId(), request);
		} else {
			attachmentManager.deleteByReference(bean.getId(), bean.getId());
			bean.setAttachmentsFlag(false);
			attaFlag = attachmentManager.create(ApplicationCategoryEnum.bulletin, bean.getId(), bean.getId(), request);
		}

		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)) 
			bean.setAttachmentsFlag(true);
		if (flag) {
			bean.setAccountId(Long.parseLong(spaceId));
			bulDataManager.saveCustomBul(bean, isNew);
		} else {
			bulDataManager.save(bean, isNew);
		}
		if (isPublish) {
			if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH)
				this.bulReadManager.configReadByData(bean);
		}

		try {
			if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH) {
				if (firstIndexFlag) {
					IndexEnable inManager = (IndexEnable) bulDataManager;
					IndexInfo indexInfo = inManager.getIndexInfo(bean.getId());
					indexManager.index(indexInfo);
				} else {
					//不是第一次进索引的在此更新
					updateIndexManager.update(bean.getId(), ApplicationCategoryEnum.bulletin.getKey());
				}
			}
		} catch (Exception e) {
			log.error("全文检索：", e);
		}
		//发布不需要经过审核的公告后，发送给公告范围内用户消息
		
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
			List<Long> memberList = new ArrayList<Long>();
			Set<Long> msgReceiverIds = new HashSet<Long>();
			boolean customFlag = false;
			//针对自定义空间过滤掉非空间人员
			if ("true".equals(custom) || flag) {
				customFlag = true;
				List<V3xOrgMember> customMembers = spaceManager.getSpaceMemberBySecurity(flag ? Long.parseLong(spaceId) : typeId, -1);
				if (CollectionUtils.isNotEmpty(customMembers)) {
					String memberIds = bean.getPublishScope();
					memberList = CommonTools.getMemberIdsByTypeAndId(memberIds, orgManager);
					List<Long> customList = new ArrayList<Long>();
					for (V3xOrgMember m : customMembers) {
						customList.add(m.getId());
					}
					memberList = CommonTools.getIntersection(memberList, customList);
				}
			} else {
				msgReceiverIds = this.getAllMembersinPublishScope(bean);
			}
			this.addAdmins2MsgReceivers(customFlag ? memberList : msgReceiverIds, bean);
			
			userMessageManager.sendSystemMessage(MessageContent.get(noAuditPublishEdit ? "bul.publishEdit" : "bul.auditing", bean.getTitle(),
					this.getLoginUserName(request)).setBody(bean.getContent(),
					bean.getDataFormat(), bean.getCreateDate()),
					ApplicationCategoryEnum.bulletin, userId, 
					MessageReceiver.get(bean.getId(), customFlag ? memberList : msgReceiverIds, 
							"message.link.bul.alreadyauditing", String.valueOf(bean.getId())), bean.getTypeId());

			//直接发布加日志
			appLogManager.insertLog(user, noAuditPublishEdit ? AppLogAction.Bulletin_Modify : AppLogAction.Bulletin_Publish, userName, bean.getTitle());			
		}
		
		//发送需要经过审核的公告消息消息
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_CREATE)) {
			Long agentId = AgentUtil.getAgentByApp(type.getAuditUser(), ApplicationCategoryEnum.bulletin.getKey());
			if (editAfterAudit) {
				//提交后修改
				userMessageManager.sendSystemMessage(MessageContent.get(
						"bul.edit", bean.getTitle(), userName),
						ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), type.getAuditUser(),
								"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get(
							"bul.edit", bean.getTitle(), userName).add("col.agent"),
							ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), agentId,
									"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				}
				
				//待审核如果修改加日志
				appLogManager.insertLog(user, AppLogAction.Bulletin_Modify, userName, bean.getTitle());
				
				// 审核员未进行审核前修改公告并再次发送，需另行增加一条待办事项记录 added by Meng Yang at 2009-07-14
				// 先删除旧有记录，再生成新的记录
				this.affairManager.deleteByObject(ApplicationCategoryEnum.bulletin, bean.getId());
				this.addPendingAffair(type, bean);

			} else if (noAuditEdit) {
				userMessageManager.sendSystemMessage(MessageContent.get(
						"bul.send", bean.getTitle(), userName),
						ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), type.getAuditUser(),
								"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get(
							"bul.send", bean.getTitle(), userName).add("col.agent"),
							ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), agentId,
									"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				}

				//审核不通过修改后再发送至审核加日志
				appLogManager.insertLog(user, AppLogAction.Bulletin_Modify, userName, bean.getTitle());
				this.addPendingAffair(type, bean);

			} else {
				this.addPendingAffair(type, bean);

				userMessageManager.sendSystemMessage(MessageContent.get("bul.send", bean.getTitle(), userName),
						ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), type.getAuditUser(),
								"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get("bul.send", bean.getTitle(), userName).add("col.agent"),
							ApplicationCategoryEnum.bulletin, userId, MessageReceiver.get(bean.getId(), agentId,
									"message.link.bul.auditing", String.valueOf(bean.getId())), type.getId());
				}

				//新建保存后发送至审核添加日志
				appLogManager.insertLog(user, AppLogAction.Bulletin_New, userName, bean.getTitle());
			}

		} else if(bean.getState().equals(Constants.DATA_STATE_NO_SUBMIT)) {
			//保存修改加日志
			appLogManager.insertLog(user, AppLogAction.Bulletin_Modify, userName, bean.getTitle());
		}
		return this.redirectModelAndView("/bulData.do?method=publishListIndex&spaceType=" + bean.getType().getSpaceType() + "&bulTypeId=" + bean.getTypeId() + "&spaceId=" + spaceId);
	}
	
	/**
	 * 发布公告时，发送消息对象中加入当前公告板块的管理员
	 * @param receivers   发布范围内的消息接受对象
	 * @param bulData	  所发布的公告
	 */
	private void addAdmins2MsgReceivers(Collection<Long> receivers, BulData bulData) {
		BulType bulType = this.bulTypeManager.getById(bulData.getTypeId());
		this.addAdmins2MsgReceivers(receivers, bulType);
	}
	
	/**
	 * 发布公告时，发送消息对象中加入当前公告板块的管理员
	 * @param receivers   发布范围内的消息接受对象
	 * @param bulType	  所发布的公告所在的公告板块
	 */
	private void addAdmins2MsgReceivers(Collection<Long> receivers, BulType bulType) {
		String managerIds = bulType.getManagerUserIds();
		if(Strings.isNotBlank(managerIds)) {
			String[] ids = managerIds.split(",");
			for(String id : ids) {
				Long addId = Long.parseLong(id);
				if(receivers!=null && !receivers.contains(addId)) {
					receivers.add(addId);
				}
			}
		}
	}
	
	/**
	 * 公告发起者执行发布公告操作
	 * 公告管理员执行发布、取消发布公告操作
	 */
	public ModelAndView publishOper(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		String spaceId = request.getParameter("spaceId");
		String custom = request.getParameter("custom");
		boolean publish = "publish".equals(request.getParameter("form_oper"));
		boolean sendMsgWhenCancelPublish = "true".equals(request.getParameter("sendMessage"));
		BulType type = null;
		
		List<Long> ids = CommonTools.parseStr2Ids(request.getParameter("id"));
		if(CollectionUtils.isNotEmpty(ids)) {
			for (Long bulletinId : ids) {
				BulData bean = this.bulDataCache.getDataCache().get(bulletinId);
				if (bean == null)
					bean = bulDataManager.getById(bulletinId);
				if (bean == null)
					continue;
				type = bean.getType();
				if (publish) {
					if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH)
						continue;
					//判断是否需要审核后才能发布，审核人ID为 0 直接发布。不为 0 给出提示
	
					if (type!= null && type.isAuditFlag()) {
						if (bean.getState() != Constants.DATA_STATE_ALREADY_AUDIT) {
							if (bean.getState().intValue() == Constants.DATA_STATE_NOPASS_AUDIT)
								request.getSession().setAttribute("_my_exception", new BusinessException("bul_not_pass"));
							else
								request.getSession().setAttribute("_my_exception", new BusinessException("bul_not_audit"));
							break;
						}
					}
					
					bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
					//如果该公告类型无审核员，则其最终审核记录状态设置为"无审核"，否则设置为"审核通过"
					if(type!=null && !type.isAuditFlag()) {
						//无审核员，设置为"无审核"
						bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_NO));
					} else if(type!=null && type.isAuditFlag()) {
						//有审核员，设置为"审核通过"
						bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PASS));
					}
					bean.setPublishDate(new Timestamp(new Date().getTime()));
					bean.setPublishUserId(CurrentUser.get().getId());
					bean.setReadCount(0);
					bean.setUpdateDate(new Date());
					bean.setUpdateUser(CurrentUser.get().getId());
					this.bulDataManager.updateDirect(bean);
					
					//发布公告添加操作日志 added by Meng Yang at 2009-08-20
					appLogManager.insertLog(user, AppLogAction.Bulletin_Publish, userName, bean.getTitle());
	
					// 配置发布范围
					if(bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH)
						this.bulReadManager.configReadByData(bean);
					
					//更新数据库再加入全文检索
					try {
						IndexEnable indexEnable = (IndexEnable) bulDataManager;
						IndexInfo info = indexEnable.getIndexInfo(bean.getId());
						indexManager.index(info);
					} catch (Exception e) {
						log.error("全文检索失败", e);
					}
	
				} else {
					this.bulDataCache.getDataCache().remove(bulletinId);
					//取消发布，回到草稿状态
					if (bean.getState() != Constants.DATA_STATE_ALREADY_PUBLISH)
						continue;
	
					bean.setState(Constants.DATA_STATE_NO_SUBMIT);
					bean.setAuditAdvice(null);
					bean.setPublishDate(null);
					bean.setPublishUserId(null);
					bean.setReadCount(0);
					bean.setUpdateDate(null);
					bean.setUpdateUser(null);
					//取消置顶
					bean.setTopOrder(Byte.valueOf("0"));
					this.bulDataManager.updateDirect(bean);
					this.bulReadManager.deleteReadByData(bean);
	
					//取消发布加日志
					appLogManager.insertLog(user, AppLogAction.Bulletin_CancelPublish, userName, bean.getTitle());
					
					//从全文检索中是删除
					try {
						indexManager.deleteFromIndex(ApplicationCategoryEnum.bulletin, bean.getId());
					} catch (Exception e) {
						log.error("全文检索：", e);
					}
					
					//给发起者发送系统消息,当发起者不是管理员的时候会给发起者发送系统消息
					Set<Long> msgReceivers = this.getMsgReceiverWhenCancelPublish(bean, sendMsgWhenCancelPublish);
					
					//过滤掉非自定义空间中人员
					if (("true".equals(custom) || Strings.isNotBlank(spaceId)) && sendMsgWhenCancelPublish) {
						List<V3xOrgMember> customMembers = spaceManager.getSpaceMemberBySecurity(Long.parseLong(spaceId), -1);
						if (CollectionUtils.isNotEmpty(customMembers)) {
							List<Long> customList = new ArrayList<Long>();
							List<Long> tempList = new ArrayList<Long>();
							for (V3xOrgMember m : customMembers) {
								customList.add(m.getId());
							}
							for (Long memberId : msgReceivers) {
								if (!customList.contains(memberId)) {
									tempList.add(memberId);
								}
							}
							msgReceivers.removeAll(tempList);
						}
					}
					if(CollectionUtils.isNotEmpty(msgReceivers)) {	
						userMessageManager.sendSystemMessage(MessageContent.get(
								"bul.cancel.publish", bean.getTitle(), userName).setBody(
								this.bulDataManager.getBody(bean.getId()).getContent(), bean.getDataFormat(),
								bean.getCreateDate()), ApplicationCategoryEnum.bulletin, userId, 
								MessageReceiver.get(bean.getId(), msgReceivers, "", "", new Timestamp(System.currentTimeMillis())+ ""), bean.getType().getId());
					}
				}
				//发送审核通过的公告消息进行发布
				if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
					Set<Long> msgReceiverIds = this.getAllMembersinPublishScope(bean);
					//过滤掉非自定义空间中人员
					if (("true".equals(custom) || Strings.isNotBlank(spaceId)) && sendMsgWhenCancelPublish) {
						List<V3xOrgMember> customMembers = spaceManager.getSpaceMemberBySecurity(Long.parseLong(spaceId), -1);
						if (customMembers != null && customMembers.size() > 0) {
							List<Long> customList = new ArrayList<Long>();
							List<Long> tempList = new ArrayList<Long>();
							for (V3xOrgMember m : customMembers) {
								customList.add(m.getId());
							}
							for (Long memberId : msgReceiverIds) {
								if (!customList.contains(memberId)) {
									tempList.add(memberId);
								}
							}
							msgReceiverIds.removeAll(tempList);
						}
					}
					this.addAdmins2MsgReceivers(msgReceiverIds, bean);
					
					userMessageManager.sendSystemMessage(MessageContent.get("bul.auditing",
							bean.getTitle(),this.getLoginUserName(request)).setBody(this.bulDataManager.getBody(bean.getId()).getContent(), bean.getDataFormat(), bean.getCreateDate()), 
							ApplicationCategoryEnum.bulletin, userId, 
							MessageReceiver.get(bean.getId(),msgReceiverIds,"message.link.bul.alreadyaudit",String.valueOf(bean.getId()),new Timestamp(System.currentTimeMillis())+""), bean.getTypeId());	
				}
			}
		}

		if (publish) {
			return this.redirectModelAndView("/bulData.do?method=publishListMain&bulTypeId="+ type.getId()+ "&spaceType=" + type.getSpaceType() + "&spaceId=" + spaceId);
		} else {
			String showAudit = request.getParameter("showAudit");
			return this.redirectModelAndView("/bulData.do?method=listMain&showAudit="+ showAudit+ "&type="+ type.getId()+ "&spaceType=" + type.getSpaceType() + "&spaceId=" + spaceId);
		}
	}
	
	/**
	 * 取消发布时，获取消息发送对象ID集合
	 * @param bean	被取消发布的公告
	 * @param sendMsgWhenCancelPublish	是否需要发送系统消息给发布范围内的人员
	 * @throws BusinessException
	 */
	private Set<Long> getMsgReceiverWhenCancelPublish(BulData bean, boolean sendMsgWhenCancelPublish) throws BusinessException {
		Set<Long> msgReceivers = new HashSet<Long>();
		if(sendMsgWhenCancelPublish) {
			msgReceivers = this.getAllMembersinPublishScope(bean);
			msgReceivers.remove(CurrentUser.get().getId());
		}
		
		if(bean.getCreateUser() != CurrentUser.get().getId())
			msgReceivers.add(bean.getCreateUser());
		
		return msgReceivers;
		
	}
	
	/**
	 * 公告发起员：查看已发送、待审核的公告详细信息(已发布的公告查看时userView方法)
	 */
	public ModelAndView writeDetail(HttpServletRequest request,  HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		BulData bean = null;
		ModelAndView mav = new ModelAndView("bulletin/write/data_list_detail_iframe");
		if (StringUtils.isBlank(idStr)) {
			bean = new BulData();
		} else {
			bean = bulDataManager.getById(Long.valueOf(idStr));
			//判断如果删除了在查看。
			if (bean==null || bean.isDeletedFlag() || bean.getState().equals(Constants.DATA_STATE_ALREADY_PIGEONHOLE)) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bul.data.noexist") + "');parent.getA8Top().reFlesh();");
				return null;
			}
			
			//读取office正文相关信息
			BulBody body=bulDataManager.getBody(bean.getId());
			bean.setContent(body.getContent());
			bean.setContentName(body.getContentName());
			
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}
		mav.addObject("bean", bean);		
		//板块是否选择了红头显示，由板块的扩展字段ext1来判断：0为标准，1为正式
		mav.addObject("ext1", bean.getType().getExt1());
		return mav;
	}

	/**
	 * 公告发起者删除公告操作，是<b>实际删除</b>
	 */
	public ModelAndView writeDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		BulData bean = null;
		User user = CurrentUser.get();
		String userName = user.getName();	

		if (StringUtils.isBlank(idStr)) {
			idStr = "";
		} else {
			String[] ids = idStr.split(",");
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					bean = bulDataManager.getById(Long.valueOf(id));
					//已经提交审核的需要发送消息，删除待办
					if (bean == null)
						continue;
					if (bean.getState().intValue() == Constants.DATA_STATE_ALREADY_CREATE) {
						affairManager.deleteByObject(ApplicationCategoryEnum.bulletin, bean.getId());

						userMessageManager.sendSystemMessage(MessageContent.get("bul.delete", bean.getTitle(), userName),
								ApplicationCategoryEnum.bulletin, user.getId(),
								MessageReceiver.get(bean.getId(), bean.getType().getAuditUser()), bean.getTypeId());

					}
					indexManager.deleteFromIndex(ApplicationCategoryEnum.bulletin, bean.getId());
					//记录操作日志：
					appLogManager.insertLog(user, AppLogAction.Bulletin_Delete, userName, bean.getTitle());						
					this.bulDataManager.deleteReal(bean.getId());
					this.bulDataCache.getDataCache().remove(Long.valueOf(id));					
				}
			}
		}
		super.rendJavaScript(response, "parent.getA8Top().reFlesh();");
		return null;
	}

	/**
	 * 辅助方法：获取公告发布范围内的全部人员ID集合
	 */
	private Set<Long> getAllMembersinPublishScope(BulData bean) throws BusinessException {
		String publishScope = bean.getPublishScope();
		
		Set<V3xOrgMember> membersInScope = this.orgManager.getMembersByTypeAndIds(publishScope);
		Set<Long> memberIdsInScope = new HashSet<Long>();
		if(membersInScope!=null && membersInScope.size()>0) {
			for(V3xOrgMember member : membersInScope) {
				if(member.isValid() && !member.getIsDeleted().booleanValue())
					memberIdsInScope.add(member.getId());
			}
		}
		//Long loginAccountId = CurrentUser.get().getLoginAccount();
		//处理跨单位兼职情况
		String[][] bulAuditIds = Strings.getSelectPeopleElements(publishScope);
		for(String[] typeAndId : bulAuditIds) {
			/* 发布范围为组时,单位公告可以给组中外单位人员发送消息
			if(typeAndId[0].equals(V3xOrgEntity.ORGENT_TYPE_TEAM)) {
				V3xOrgTeam team = orgManager.getTeamById(Long.valueOf(typeAndId[1]));
				//如果是私有组，剔除非本单位人员
				boolean needFilterOthers = team != null && bean.getType().getSpaceType() != Constants.BulTypeSpaceType.group.ordinal();
				if(needFilterOthers){
					List<V3xOrgMember> teamMember = orgManager.getTeamMember(team.getId());
					for(V3xOrgMember member : teamMember){
						if(!member.getOrgAccountId().equals(loginAccountId)){
							memberIdsInScope.remove(member.getId());
						}
					}
				}
			} else */
			if(typeAndId[0].equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)){//仅当发送范围为单位时，才将兼职人员悉数加入，在发布范围为其他类型时，兼职人员已包含在范围内
				//考虑到集团公告情况下可以选择的发布范围为多个单位，此处的参数不应限定为只是当前用户登陆的单位
				Map<Long, List<V3xOrgMember>> accJian=orgManager.getConcurentPostByAccount(Long.valueOf(typeAndId[1]));
				Set<Entry<Long, List<V3xOrgMember>>> accSet = accJian.entrySet();			
				for (Iterator<Entry<Long, List<V3xOrgMember>>> iter = accSet.iterator(); iter.hasNext();){
					Map.Entry<Long, List<V3xOrgMember>> ele=(Entry<Long, List<V3xOrgMember>>) iter.next();
					for (Iterator<V3xOrgMember> iterator = ele.getValue().iterator(); iterator.hasNext();) {
						V3xOrgMember mem = (V3xOrgMember) iterator.next();
						if(!memberIdsInScope.contains(mem.getId())) {
							memberIdsInScope.add(mem.getId());
						}
					}
				}
			}
		}
		return memberIdsInScope;
	}

	/**
	 * 公告发起者在查看审核通过的公告时，点击页面底部的"发布"按钮将其发布
	 */
	public ModelAndView publishIt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		BulData bean = bulDataManager.getById(Long.valueOf(idStr));
		if (bean == null)
			return null;

		bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
		bean.setPublishDate(new Timestamp(new Date().getTime()));
		bean.setPublishUserId(CurrentUser.get().getId());

		bean.setReadCount(0);
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		
		//设置审核最终状态：审核通过 added by Meng Yang 2009-06-11
		bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PASS));		
		this.bulDataManager.updateDirect(bean);

		// 配置发布范围
		if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH)
			this.bulReadManager.configReadByData(bean);
		
		//更新数据库后再加入全文检索
		try {
			IndexEnable indexEnable = (IndexEnable) bulDataManager;
			IndexInfo info = indexEnable.getIndexInfo(bean.getId());
			indexManager.index(info);
		} catch (Exception e) {
			log.error("全文检索失败", e);
		}

		//获得发布范围内的员工列表，预备发布之后的系统消息发送到列表中的每一个人
		Set<Long> msgReceiverIds = this.getAllMembersinPublishScope(bean);
		this.addAdmins2MsgReceivers(msgReceiverIds, bean);

		userMessageManager.sendSystemMessage(MessageContent.get("bul.auditing",
				bean.getTitle(), this.getLoginUserName(request)).setBody(
				this.bulDataManager.getBody(bean.getId()).getContent(),
				bean.getDataFormat(), bean.getCreateDate()),
				ApplicationCategoryEnum.bulletin, this.getLoginUserId(request),
				MessageReceiver.get(bean.getId(), msgReceiverIds,
						"message.link.bul.alreadyauditing", String.valueOf(bean.getId())), bean.getTypeId());
		
		if("creater".equals(request.getParameter("from"))){
			super.rendJavaScript(response,"if(parent.opener){" +
										  "	parent.opener.getA8Top().reFlesh();parent.close();" +
										  "} else { " +
										  "	parent.getA8Top().reFlesh();" +
										  "}");
		}
		return null;
	}

	/**
	 * 公告审核员：查看待审核的公告列表主页面
	 */
	public ModelAndView auditListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/audit/data_list_mainEntry");
		String from = request.getParameter("from");
		String spaceId = request.getParameter("spaceId");
		if (from != null) {
			mav.addObject("from", from);
		}
		//取得相应的公告类型
		BulType type = null;
		String boardId = request.getParameter("bulTypeId");
		if (Strings.isNotBlank(boardId)) {
			Long typeId = Long.valueOf(boardId);
			type = this.bulTypeManager.getById(typeId);
			if (!type.isUsedFlag()) {
				super.rendJavaScript(response, "alert('板块已被管理员删除！');self.history.back();");
				return null;
			}
		}
		mav.addObject("bulTypeId", boardId);
		String close = request.getParameter("hiddenId");
		if ("hiddenId".equals(close)) {
			super.rendJavaScript(response, "window.close()");
		}

		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.BulTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)) {
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		mav.addObject("spaceType", spaceTypeInt);
		Long userId = CurrentUser.get().getId();
		//屏蔽掉页签中的"(*项待审)"字样 2009-11-28
		//int pending = this.bulDataManager.getPendingCountOfUser(userId, spaceTypeInt);
		//mav.addObject("pending", pending);

		List<BulType> typeList = null;
		if (spaceTypeInt == Constants.BulTypeSpaceType.corporation.ordinal()) {
			typeList = this.bulDataManager.getTypeList(userId, true);
		} else if (spaceTypeInt == Constants.BulTypeSpaceType.group.ordinal()) {
			typeList = this.bulDataManager.getManagerGroupBulType(userId, true);
		} else if (spaceTypeInt == Constants.BulTypeSpaceType.public_custom.ordinal() || spaceTypeInt == Constants.BulTypeSpaceType.public_custom_group.ordinal()) {
			typeList = this.bulDataManager.getTypeList(userId, Long.parseLong(spaceId), spaceTypeInt);
		}
		//如果没有可以管理的公告板块，则不显示"公告管理"页签
		mav.addObject("noManagerLabel", (typeList == null || typeList.size() == 0));
		return mav;
	}

	public ModelAndView auditIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/audit/data_list_index");
	}
	
	/**
	 * 公告审核员：查看待审核的公告列表、框架中转
	 */
	public ModelAndView entry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/audit/data_list_main");
	}

	/**
	 * 公告审核员：查看待审核的公告列表页面
	 */
	public ModelAndView auditList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/audit/data_list_iframe");
		String spaceId = request.getParameter("spaceId");
		String showAudit = request.getParameter("showAudit");
		int spaceTypeInt = NumberUtils.toInt(request.getParameter("spaceType"), Constants.BulTypeSpaceType.corporation.ordinal());
		mav.addObject("showAudit", showAudit);
		mav.addObject("spaceType", spaceTypeInt);
		
		List<BulData> list = null;
		List<BulType> typeList = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		//外部人员过滤
		User user = CurrentUser.get();
		try {			
			if (user!=null && user.isInternal()) {
				if (Strings.isNotBlank(spaceId)) {
					list = bulDataManager.getAuditDataListNew(user.getId(), condition, textfield, spaceTypeInt, Long.parseLong(spaceId));
				} else {
					list = bulDataManager.getAuditDataListNew(user.getId(), condition, textfield, spaceTypeInt);
				}
			}
		} catch (BusinessException e) {
			mav = new ModelAndView("bulletin/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		boolean ngroup = (spaceTypeInt != Constants.BulTypeSpaceType.group.ordinal());
		if (ngroup)
			if (Strings.isNotBlank(spaceId)) {
				typeList = this.bulTypeManager.getAuditUnitBulType(user.getId(), Long.parseLong(spaceId), spaceTypeInt);
			} else {
				typeList = this.bulTypeManager.getAuditUnitBulType(user.getId());
			}
		else
			typeList = this.bulTypeManager.getAuditGroupBulType(user.getId());

		mav.addObject("list", list);
		mav.addObject("typeList", typeList);
		//是否审核页面
		mav.addObject("isAudit", "true");
		return mav;
	}

	/**
	 * 集团公告审核员：查看待审核的集团公告列表页面
	 */
	public ModelAndView auditGroupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isShowAudit = this.bulTypeManager.getBulTypeDao().findBy("auditUser", CurrentUser.get().getId()).size() > 0;
		request.getSession().setAttribute("bulletin.isShowAudit", isShowAudit);

		List<BulData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		try {
			if (StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)) {
				Object value = BulletinUtils.getPropertyObject(BulData.class, condition, textfield);
				list = bulDataManager.getGroupAuditList(CurrentUser.get().getId(), condition, value);
			} else {
				list = bulDataManager.getGroupAuditList(CurrentUser.get().getId(), null, null);
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("bulletin/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		ModelAndView mav = new ModelAndView("bulletin/audit/data_list_iframe");
		mav.addObject("list", list);
		//返回当前用户是审核员的集团公告类型
		mav.addObject("typeList", this.bulTypeManager.getAuditGroupBulType(CurrentUser.get().getId()));
		mav.addObject("from", "Group");
		return mav;
	}

	/**
	 * 公告审核员点击一条待审核的公告时，显示公告审核页面：页面左边是公告信息、右边是审核操作界面
	 */
	public ModelAndView audit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isBlank(idStr)) {
			throw new BulletinException("bulletin.not_exists");
		}
		Long currentUserId = CurrentUser.get().getId();
		Long dataId = Long.parseLong(idStr);
		BulData bean = bulDataManager.getById(dataId);
		//标识是在上列表下详图的页面结构中，还是点击待办事项打开窗口的页面结构中
		String needBreak = request.getParameter("needBreak");
		if(bean==null || bean.isDeletedFlag()) {
			if (bean != null) {
				affairManager.deleteByObject(ApplicationCategoryEnum.bulletin, bean.getId());
			}
			String jsAction = "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bul.data.deleted") + "');";
			if(!"true".equals(needBreak))
				jsAction += "window.opener.getA8Top().reFlesh();window.close();";
			else
				jsAction += "parent.getA8Top().reFlesh();";
			super.rendJavaScript(response, jsAction);
			return null;
		}else{
			BulType type = bulTypeManager.getById(bean.getTypeId());
			
			if(type!= null){
				Long agentId = AgentUtil.getAgentByApp(type.getAuditUser(), ApplicationCategoryEnum.bulletin.getKey());
				if ((!type.getAuditUser().equals(currentUserId)) && ((agentId == null) || (!currentUserId.equals(agentId)))){
			        return null;
			    }
			}
		}
		
		//检验文件中否加锁
		String action = BulDataLockAction.NEWLOCK_AUDITING;
		BulDataLock bullock = bulDataManager.lock(dataId, action);
		if (bullock!=null && !action.equals(bullock.getAction()) && bullock.getUserid()!=currentUserId) {
			String lockaction = bullock.getAction();
			V3xOrgMember orm = orgManager.getMemberById(bullock.getUserid());
			//可以取得相关的锁对象
			String alert = "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, lockaction, orm.getName()) + "');";
			if ("true".equalsIgnoreCase(needBreak)) {
				super.rendJavaScript(response, alert + "parent.getA8Top().reFlesh();");
				return null;
			} else {
				super.rendJavaScript(response, alert + "window.close();");
				return null;
			}
		}
		ModelAndView mav = new ModelAndView("bulletin/audit/data_frameset");
		String description = request.getParameter("description");
		if ("left".equals(description)) {
			if ("true".equals(needBreak)) 
				mav = new ModelAndView("bulletin/audit/data_audit_detail");
			else 
				mav = new ModelAndView("bulletin/audit/data_audit");
		} else if ("right".equals(description)) {
			if ("true".equals(needBreak)) 
				mav = new ModelAndView("bulletin/audit/data_diagram_detail");
			else 
				mav = new ModelAndView("bulletin/audit/data_diagram");
		}
		if(Strings.isNotBlank(spaceId)){
			mav.addObject("publicCustom", true);
			mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		}
		mav.addObject("needBreak", needBreak);
		mav.addObject("id", idStr);
		mav.addObject("dataExist", true);

		//SECURITY 访问安全检查
    	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.bulletin, CurrentUser.get(), dataId, null, null)){
    		return null;
    	}
		BulBody body=bulDataManager.getBody(bean.getId());
		bean.setContent(body.getContent());
		bean.setContentName(body.getContentName());
		mav.addObject("bean", bean);

		List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);
		mav.addObject("spaceType", bean.getType().getSpaceType());
		return mav;
	}
	
	/**
	 * 公告审核员审核公告操作
	 */
	public ModelAndView auditOper(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		if (StringUtils.isBlank(idStr)) {
			throw new BulletinException("bulletin.not_exists");
		}
		User user = CurrentUser.get();
		String close = request.getParameter("hiddenId");   //标识上列表下详图页面或打开新窗口页面的情况
		
		BulData bean = bulDataManager.getById(Long.valueOf(idStr));
		//处理审核两次的情况，只有是未审核的状态才允许审核操作
		if (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_CREATE) {
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bul.audit.already") + "');" +
								("hiddenId".equalsIgnoreCase(close) ? "parent.getA8Top().close();parent.getA8Top().opener.contentFrame.mainFrame.location.reload(true);" : "getA8Top().parent.getA8Top().reFlesh();"));
			return null;
		}

		String form_oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(form_oper)) {
			if (form_oper.equals("audit")) {
				bean.setState(Constants.DATA_STATE_ALREADY_AUDIT);
			} else if (form_oper.equals("publish")) {
				bean.setPublishDate(new Timestamp(new Date().getTime()));
				bean.setPublishUserId(CurrentUser.get().getId());
				bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
				//审核员进行"直接发布"审核操作，该公告发布后，其审核记录设定为"直接发布" added by Meng Yang 2009-06-11
				bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PUBLISH));
			} else if (form_oper.equals("noaudit")) {
				bean.setState(Constants.DATA_STATE_NOPASS_AUDIT);
			} else if (form_oper.equals("cancelaudit")) {
				bean.setState(Constants.DATA_STATE_ALREADY_CREATE);
			}
		} else 
			bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		
		BulType type = this.bulTypeManager.getById(bean.getTypeId());

		bean.setAuditAdvice(request.getParameter("auditAdvice"));
		bean.setAuditDate(new Date());
		bean.setAuditUserId(type.getAuditUser());

		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());

		Map<String, Object> summ = new HashMap<String, Object>();
		summ.put("state", bean.getState());

		if ("publish".equals(form_oper)) {
			summ.put("publishDate", bean.getPublishDate());
			summ.put("publishUserId", bean.getPublishUserId());
			summ.put("ext3", bean.getExt3());
		}

		summ.put("auditAdvice", bean.getAuditAdvice());
		summ.put("auditDate", bean.getAuditDate());
		summ.put("auditUserId", bean.getAuditUserId());
		summ.put("updateDate", bean.getUpdateDate());
		summ.put("updateUser", bean.getUpdateUser());
		
		Long agentId = AgentUtil.getAgentByApp(type.getAuditUser(), ApplicationCategoryEnum.bulletin.getKey());
		Long senderId = user.getId();
		String senderName = user.getName();
		int proxyType = 0;
		if(agentId != null && agentId.equals(senderId)){
			proxyType = 1;
			senderId = type.getAuditUser();
			senderName = type.getAuditUserName();
		}

		//审合公告通过发送系统消息、添加操作日志
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_AUDIT)) {
			userMessageManager.sendSystemMessage(MessageContent.get("bul.alreadyauditing", bean.getTitle(), senderName, proxyType, user.getName()), ApplicationCategoryEnum.bulletin, senderId, 
					MessageReceiver.get(bean.getId(), bean.getCreateUser(), "message.link.bul.writedetail", String.valueOf(bean.getId())), bean.getTypeId());

			appLogManager.insertLog(user, AppLogAction.Bulletin_AuditPass, senderName, bean.getTitle());

		}
		//审合公告没有通过发送系统消息、添加操作日志
		if (bean.getState().equals(Constants.DATA_STATE_NOPASS_AUDIT)) {
			userMessageManager.sendSystemMessage(MessageContent.get("bul.notthrougth", bean.getTitle(), senderName, proxyType, user.getName()),
					ApplicationCategoryEnum.bulletin, senderId, 
					MessageReceiver.get(bean.getId(), bean.getCreateUser(), "message.link.bul.writedetail", String.valueOf(bean.getId())), bean.getTypeId());

			appLogManager.insertLog(user, AppLogAction.Bulletin_AduitNotPass, senderName, bean.getTitle());

		}
		//审核员直接发布公告
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
			//Set<Long> msgReceiverIds = this.getAllMembersinPublishScope(bean);
			List<Long> memberList = new ArrayList<Long>();
			Set<Long> msgReceiverIds = new HashSet<Long>();
			BulType bulType = bean.getType();
			boolean customFlag = false;
			if (bulType.getSpaceType() == 4 || bulType.getSpaceType() == 5 || bulType.getSpaceType() == 6) {
				customFlag = true;
				List<V3xOrgMember> customMembers = spaceManager.getSpaceMemberBySecurity(bulType.getAccountId(), -1);
				if (customMembers != null && customMembers.size() > 0) {
					String memberIds = bean.getPublishScope();
					memberList = CommonTools.getMemberIdsByTypeAndId(memberIds, orgManager);
					List<Long> customList = new ArrayList<Long>();
					List<Long> tempList = new ArrayList<Long>();
					for (V3xOrgMember m : customMembers) {
						customList.add(m.getId());
					}
					for (Long memberId : memberList) {
						if (!customList.contains(memberId)) {
							tempList.add(memberId);
						}
					}
					memberList.removeAll(tempList);
				}
			} else {
				msgReceiverIds = this.getAllMembersinPublishScope(bean);
			}
			
			
			this.addAdmins2MsgReceivers(customFlag ? memberList : msgReceiverIds, bean);
			
			V3xOrgMember member = orgManager.getMemberById(bean.getCreateUser());
			userMessageManager.sendSystemMessage(MessageContent.get("bul.auditing", bean.getTitle(), member.getName())
					.setBody(this.bulDataManager.getBody(bean.getId()).getContent(), bean.getDataFormat(),
					bean.getCreateDate()), ApplicationCategoryEnum.bulletin, bean.getCreateUser(), 
					MessageReceiver.get(bean.getId(), customFlag ? memberList : msgReceiverIds, "message.link.bul.alreadyauditing", String.valueOf(bean.getId())), bean.getTypeId());
			
			//直接发布加日志
			appLogManager.insertLog(user, AppLogAction.Bulletin_AuditPublish, senderName, bean.getTitle());
		}

		this.bulDataManager.update(bean.getId(), summ);
		
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
			//更新数据库后再加入全文检索
			try {
				IndexEnable inManager = (IndexEnable) bulDataManager;
				IndexInfo indexInfo = inManager.getIndexInfo(bean.getId());
				indexManager.index(indexInfo);
			} catch (Exception e) {
				log.error("全文检索：", e);
			}
		}
		
		//删除待办事项
		affairManager.deleteByObject(ApplicationCategoryEnum.bulletin, bean.getId());
		
		//区分打开窗口和上列表下详图页面结构两种情况
		if ("hiddenId".equals(close)) {
			super.rendJavaScript(response, "parent.closeAndRefresh();");
		} else {
			super.rendJavaScript(response, "parent.parent.getA8Top().contentFrame.mainFrame.location.href=parent.parent.getA8Top().contentFrame.mainFrame.location;");
		}
		return null;
	}

	/**
	 * 公告审核员将已审核通过的公告取消审核，支持批量操作，取消审核后的公告重新恢复到待审核状态
	 */
	public ModelAndView cancelAudit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ids = request.getParameter("ids");
		if (Strings.isBlank(ids))
			return null;
		
		String[] idArr = ids.split(",");
		User user = CurrentUser.get();
		String userName = user.getName();
		for (String sid : idArr) {
			long dataId = Long.valueOf(sid);
			BulData bean = bulDataManager.getById(dataId);
			if (bean == null)
				continue;
			
			BulType bulType = bean.getType();
			if (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_AUDIT)
				continue;
			
			bean.setAuditAdvice(null);
			bean.setState(Constants.DATA_STATE_ALREADY_CREATE);			
			this.addPendingAffair(bulType, bean);

			//取消审核给创建者发送消息
			List<Long> msgReceiverIds = new ArrayList<Long>();
			msgReceiverIds.add(bean.getCreateUser());
			Collection<MessageReceiver> receivers = MessageReceiver.get(bean.getId(), msgReceiverIds);
			
			userMessageManager.sendSystemMessage(MessageContent.get("bul.cancel.audit", bean.getTitle(), userName), 
												 ApplicationCategoryEnum.bulletin, user.getId(), receivers);

			//取消审核加日志
			appLogManager.insertLog(user, AppLogAction.Bulletin_CancelAudit, userName, bean.getTitle());
		}
		super.rendJavaScript(response, "parent.parent.parent.location.reload(true);");
		return null;
	}

	/**
	 * 公告审核员点击一条已经审核通过的公告，查看其详细内容
	 */
	public ModelAndView auditDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/audit/data_list_detail_iframe");
		String idStr = request.getParameter("id");
		
		BulData bean = Strings.isBlank(idStr) ? null : bulDataManager.getById(Long.valueOf(idStr));
		if (bean == null || bean.isDeletedFlag()) {
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('bulletin.record_delete'));parent.window.location.reload();");
			return null;
		} 
		
		BulBody body = bulDataManager.getBody(bean.getId());
		bean.setContent(body.getContent());
		bean.setContentName(body.getContentName());
		
		List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
		
		mav.addObject("attachments", attachments);
		mav.addObject("bean", bean);
		mav.addObject("spaceType", bean.getType().getSpaceType());
		return mav;
	}
	
	/**
	 * 为以下几种情况增加待办事项：
	 * 1.新建公告，发送待审核，增加一条对应的待办事项记录；
	 * 2.已发送的公告，未审核之前修改，再行发送，增加一条待审核记录，同时删除修改之前已有的待办事项记录；
	 * 3.已审核且不通过的公告，修改后再次发送待审核，增加一条对应的待办事项记录。
	 */
	private void addPendingAffair(BulType bulType, BulData bean) throws BusinessException {
		Affair affair = new Affair();
		affair.setIdIfNew();
		affair.setIsTrack(false);
		affair.setIsDelete(false);
		//利用 subjectId 存储空间类型，将来用于进入不同的页面
		affair.setSubObjectId(Long.valueOf(bulType.getSpaceType().toString()));
		affair.setMemberId(bulType.getAuditUser());
		affair.setState(StateEnum.col_pending.key());
		affair.setSubState(SubStateEnum.col_normal.key());
		affair.setSenderId(bean.getCreateUser());
		affair.setSubject(bean.getTitle());
		affair.setObjectId(bean.getId());
		affair.setApp(ApplicationCategoryEnum.bulletin.key());
		affair.setCreateDate(new Timestamp(bean.getCreateDate().getTime()));
		V3xOrgMember member = this.orgManager.getMemberById(bean.getCreateUser());
		if (member != null)
			affair.setSender(member);
		affair.setIsSendMessage(false);
		affair.setHasAttachments(bean.getAttachmentsFlag());
		
		affair.addExtProperty("spaceType", bulType.getSpaceType());
		affair.addExtProperty("spaceId", bulType.getAccountId());
		affair.addExtProperty("typeId", bulType.getId());
		affair.serialExtProperties();
		
		affairManager.addAffair(affair);
	}

	/**
	 * 辅助方法：得到登录用户id
	 */
	private Long getLoginUserId(HttpServletRequest request) {
		User user = CurrentUser.get();
		return (user == null ? 0L : user.getId());
	}

	/**
	 * 辅助方法：得到登录用户名
	 */
	private String getLoginUserName(HttpServletRequest request) {
		User user = CurrentUser.get();
		return (user == null ? "" : user.getName());
	}
	
	/**
	 * 具备公告板块管理权限的管理员点击"公共信息管理" - "公告管理"之后进入的单位公告或集团公告管理首页
	 */
	public ModelAndView listBoardIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("bulletin/manager/list_board_index");
		BulType type = null;
		String boardId = request.getParameter("bulTypeId");
		String spaceId = request.getParameter("spaceId");
		if (boardId != null && !boardId.equals("")) {
			Long typeId = Long.valueOf(boardId);
			type = this.bulTypeManager.getById(typeId);
			if (!type.isUsedFlag()) {
				super.rendJavaScript(response, "alert('板块已被管理员删除！');self.history.back();");
				return null;
			}

		}
		ret.addObject("bulTypeId", boardId);
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.BulTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr))
			spaceTypeInt = Integer.valueOf(spaceTypeStr);

		ret.addObject("spaceType", spaceTypeInt);

		//在审核页面显示待审核的条数  <屏蔽掉页签中的"(*项待审)"字样 2009-11-28>
		//int pending = this.bulDataManager.getPendingCountOfUser(CurrentUser.get().getId(), spaceTypeInt);
		//ret.addObject("pending", pending);

		boolean ngroup = (spaceTypeInt != Constants.BulTypeSpaceType.group.ordinal());
		boolean showAudit = false;
		boolean showBoard = false;

		//类型列表
		List<BulType> typeList = null;
		long userId = CurrentUser.get().getId();
		if (ngroup) {
			if (Strings.isNotBlank(spaceId)) {
				typeList = bulDataManager.getTypeList(userId, Long.parseLong(spaceId), spaceTypeInt);
				showBoard = !typeList.isEmpty();
				//显示不显示审核的选项卡
				showAudit = bulTypeManager.getAuditUnitBulType(userId, Long.parseLong(spaceId), spaceTypeInt).contains(type);
			} else {
				typeList = bulDataManager.getTypeList(userId, true);
				showBoard = !typeList.isEmpty();
				//显示不显示审核的选项卡
				showAudit = bulTypeManager.getAuditUnitBulType(userId).contains(type);
			}
		} else {
			typeList = bulDataManager.getManagerGroupBulType(CurrentUser.get()
					.getId(), true);
			showBoard = !typeList.isEmpty();
			//显示不显示审核的选项卡
			showAudit = bulTypeManager.getAuditGroupBulType(userId).contains(type);
		}
		ret.addObject("typeList", typeList);
		ret.addObject("showBoard", showBoard);
		ret.addObject("showAudit", showAudit);
		return ret;
	}

	/**
	 * 具备公告板块管理权限的管理员点击"公共信息管理" - "公告管理"之后进入的单位公告或集团公告管理首页FrameSet页面
	 * @deprecated list_board_frame.jsp这个页面根本不存在，这个方法也根本不被使用...-_-|
	 */
	public ModelAndView listBoardFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/manager/list_board_frame");
	}

	/**
	 * 显示单位公告管理员可管理的公告版块
	 */
	public ModelAndView listBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("bulletin/manager/list_board");
		String spaceTypeStr = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		int spaceTypeInt = Constants.BulTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)) {
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		mav.addObject("spaceType", spaceTypeInt);

		List<BulType> typeList = null;
		boolean isShowAudit = false;
		boolean isShowBoard = false;  // 判断是否有管理板块的权限
		if (spaceTypeInt == Constants.BulTypeSpaceType.corporation.ordinal()) {
			typeList = bulDataManager.getTypeList(userId, true);
			Collections.sort(typeList) ;
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !bulTypeManager.getAuditUnitBulType(userId).isEmpty();
			if(!isShowBoard && !isShowAudit){ //如果没有单位公告管理权限，跳转到部门。部门还没有，跳转到首页
				return super.redirectModelAndView("/bulData.do?method=listMain&spaceType=2&type=menu");
			}
		} else if (spaceTypeInt == Constants.BulTypeSpaceType.public_custom.ordinal()
				|| spaceTypeInt == Constants.BulTypeSpaceType.public_custom_group.ordinal()) {
			typeList = bulDataManager.getTypeList(userId, Long.parseLong(spaceId), spaceTypeInt);
			Collections.sort(typeList) ;
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !bulTypeManager.getAuditUnitBulType(userId, Long.parseLong(spaceId), spaceTypeInt).isEmpty();
		} else if (spaceTypeInt == Constants.BulTypeSpaceType.group.ordinal()) {
			typeList = bulDataManager.getManagerGroupBulType(userId, true);
			Collections.sort(typeList) ;
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !bulTypeManager.getAuditGroupBulType(userId).isEmpty();
		}
		mav.addObject("typeList", typeList);
		mav.addObject("showBoard", isShowBoard);
		mav.addObject("showAudit", isShowAudit);
		return mav;
	}

	/**
	 * 显示集团公告管理员可管理的公告版块
	 */
	public ModelAndView listGroupBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		request.getSession().setAttribute("bulletin.groupSign", "0");// 集团公告空间进入创建SESSION
		ModelAndView mav = new ModelAndView("bulletin/manager/list_board");
		boolean isShowAudit = !bulTypeManager.getAuditGroupBulType(userId).isEmpty();
		List<BulType> typeList = bulDataManager.getManagerGroupBulType(userId, true);
		request.getSession().setAttribute("bulletin.isShowAudit", isShowAudit);
		mav.addObject("typeList", typeList);
		mav.addObject("from", "Group");
		return mav;
	}
	
	/**
	 * 公告管理员点击一个特定板块的"板块管理"按钮进入的主页面
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/manager/data_list_main");
		String bultypeid = request.getParameter("type");
		String spaceType = request.getParameter("spaceType");
//		String spaceId = request.getParameter("spaceId");
		User user = CurrentUser.get();
		if ("2".equalsIgnoreCase(spaceType)) {
			List<Long> managerDepartments = new ArrayList<Long>();
			managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
			if("menu".equals(bultypeid)){
				mav.addObject("bultypeid","menu");
				//如果本部门没有权限，自动跳到下个部门
				if( managerDepartments.contains(user.getDepartmentId())){
					mav.addObject("isSpaceManager",true);
				}else{
					if(managerDepartments.size() > 0){
//						for(Long depId : managerDepartments){
//							if(spaceManager.isCreateDepartmentSpace(depId)){
								mav.addObject("bultypeid", managerDepartments.get(0));
								mav.addObject("isSpaceManager",true);
//								break;
//							}
//						}
					}else{
						mav.addObject("isSpaceManager",false);
					}
				}
			}else{
				boolean isSpaceManager = false;
				if(Strings.isNotBlank(bultypeid))
					isSpaceManager = managerDepartments.contains(Long.valueOf(bultypeid));
				mav.addObject("isSpaceManager", isSpaceManager);
			}
		}
		if ("4".equalsIgnoreCase(spaceType)) {
			mav.addObject("isSpaceManager", true);
		}
		return mav;
	}

	/**
	 * 公告管理员点击一个特定板块的"板块管理"按钮进入的页面
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String typeIdStr = request.getParameter("type");
		String spaceId = request.getParameter("spaceId");
		if("menu".equals(typeIdStr)){
			typeIdStr = String.valueOf((long) CurrentUser.get().getDepartmentId());
		}
		String showAudit = request.getParameter("showAudit");
		ModelAndView mav = new ModelAndView("bulletin/manager/data_list_iframe");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = NumberUtils.toInt(request.getParameter("spaceType"), Constants.BulTypeSpaceType.corporation.ordinal());
		mav.addObject("spaceType", spaceTypeInt);
		
		//部门公告板块管理时才有必要运行以下代码，否则消耗无谓资源
		if(spaceTypeInt==Constants.BulTypeSpaceType.department.ordinal()) {
			List<V3xOrgDepartment> departmentList = new ArrayList<V3xOrgDepartment>();
			List<Long> departmentListId = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
			for(Long depId : departmentListId){
				departmentList.add(orgManager.getDepartmentById(depId));
			}
			Pagination.setNeedCount(true);
			mav.addObject("departmentList", departmentList);
			mav.addObject("departmentListLength", departmentList.size());
		}
		mav.addObject("showAudit", showAudit);
		
		BulType type = null;
		Long typeId = null;
		if (Strings.isBlank(typeIdStr)) {
			List<BulType> typeList = null;
			if (spaceTypeInt == Constants.BulTypeSpaceType.corporation.ordinal()) {
				typeList = this.bulDataManager.getTypeList(CurrentUser.get().getId(), true);
			} else if (spaceTypeInt == Constants.BulTypeSpaceType.group.ordinal()) {
				typeList = this.bulDataManager.getManagerGroupBulType(CurrentUser.get().getId(), true);
			} else if (spaceTypeInt == Constants.BulTypeSpaceType.public_custom.ordinal()) {
				typeList = this.bulDataManager.getTypeList(CurrentUser.get().getId(), Long.parseLong(spaceId), spaceTypeInt);
			}

			if (typeList!=null && typeList.size() > 0) {
				typeId = typeList.get(0).getId();
				type = typeList.get(0);
			}
		} else {
			typeId = Long.valueOf(typeIdStr);
			type = this.bulTypeManager.getById(typeId);
		}

		if (typeIdStr != null && !typeIdStr.equals("")) {
			initOperate(request, typeIdStr);
		}

		List<BulData> list = null;
		SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(request);
		try {
			list = bulDataManager.findAll4Manager(typeId, searchInfo);
		} catch (BusinessException e) {
			mav = new ModelAndView("bulletin/error");
			request.getSession().setAttribute("_my_exception", e);
			return mav;
		}

		mav.addObject("list", list);
		mav.addObject("topCount", type!=null ? type.getTopCount() : 0);
		int topedCount = (typeId!=null ? bulDataManager.getTopedCount(typeId) : 0);
		mav.addObject("topedCount", topedCount);

		if (StringUtils.isNotBlank(spaceType)) {// 判断第二次进来
			// spaceType取不到值时，不重新set值
			request.getSession().setAttribute("bulletin.spaceType", spaceType);
		}

		mav.addObject("bulTypeId", typeId);
		mav.addObject("theType", type);

		List<BulTypeManagers> listW = this.bulTypeManager.findTypeWriters(type);
		List<V3xOrgEntity> auList = null;
		if (listW != null && listW.size() > 0) {
			StringBuffer strbuf = new StringBuffer();
			for (BulTypeManagers managers : listW) {
				strbuf.append(managers.getExt2() + "|" + managers.getManagerId() + ",");
			}
			auList = orgManager.getEntities(strbuf.substring(0, strbuf.length() - 1));
			mav.addObject("managerId", auList);
		}

		boolean group = (spaceTypeInt == Constants.BulTypeSpaceType.group.ordinal());
		boolean account = (spaceTypeInt == Constants.BulTypeSpaceType.corporation.ordinal());

		// 类型列表
		List<BulType> typeList = null;
		long userId = CurrentUser.get().getId();
		if (account) {
			typeList = bulDataManager.getTypeList(userId, true);
		} else if (group) {
			typeList = bulDataManager.getManagerGroupBulType(CurrentUser.get().getId(), true);
		} else if (Strings.isNotBlank(spaceId)) {
			typeList = this.bulDataManager.getTypeList(CurrentUser.get().getId(), Long.parseLong(spaceId), spaceTypeInt);
		} else {
			typeList = new ArrayList<BulType>();
		}
		//下拉类型列表中去掉自己对应的公告类型
		if(type!=null && typeList.contains(type)) {
			typeList.remove(type);
		}
		mav.addObject("typeList", typeList);
		return mav;
	}
	
	/**
	 * 公告管理员删除公告，支持批量删除。<b>只做标记，并不实际删除</b>
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		User user = CurrentUser.get();
		String userName = user.getName();
		if (StringUtils.isBlank(idStr)) {
			idStr = "";
		} else {
			String[] idStrs = idStr.split(",");
			List<Long> ids = new ArrayList<Long>();
			for (String str : idStrs) {
				if (StringUtils.isNotBlank(str)) {
					ids.add(Long.valueOf(str));
					BulData bean = this.bulDataCache.getDataCache().get(Long.valueOf(str));
					if (bean == null)
						bean = bulDataManager.getById(Long.valueOf(str));
					if(bean==null)
						continue;
					//删除公告加日志
					bean.setDeletedFlag(true);
					bean.setTopOrder(Byte.parseByte("0"));
					appLogManager.insertLog(user, AppLogAction.Bulletin_Delete, userName, bean.getTitle());
					//从缓存中清理被真实删除的公告，以免二者出现不同步的现象
					this.bulDataCache.getDataCache().remove(Long.valueOf(str));
				}
			}
			if (ids.size() > 0) {
				bulDataManager.deletes(ids);
				try {
					for (Long id : ids) {
						indexManager.deleteFromIndex(ApplicationCategoryEnum.bulletin, id);
					}
				} catch (Exception e) {
					log.error("全文检索：", e);
				}
			}
		}
		return this.listMain(request, response);
	}

	/**
	 * 公告管理员进入一个公告板块的管理界面之后点击已发布未归档的公告查看其详细内容
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		BulData bean = null;
		ModelAndView mav = new ModelAndView("bulletin/manager/data_list_detail_iframe");

		if (StringUtils.isBlank(idStr)) {
			bean = new BulData();
		} else {
			bean = bulDataManager.getById(Long.valueOf(idStr));
			if(bean==null || bean.isDeletedFlag() || bean.getState().equals(Constants.DATA_STATE_ALREADY_PIGEONHOLE)) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "bul.data.noexist") + "');" +
											   "parent.getA8Top().reFlesh();");
				return null;
			}
			BulBody body=bulDataManager.getBody(bean.getId());
			bean.setContent(body.getContent());
			bean.setContentName(body.getContentName());
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}

		mav.addObject("bean", bean);

		// 板块是否选择了红头显示，由板块的扩展字段ext1来判断：0为标准，1为正式
		mav.addObject("ext1", bean.getType().getExt1());
		return mav;
	}

	/**
	 * 外单位公告板块的链接。返回的数据和其他板块一样走同样的页面
	 * @deprecated 已被废弃。早已没有外单位公告应用的设定
	 */
	public ModelAndView otherUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 辅助方法：获取公告的阅读信息并加入到ModelAndView中进行下一步的数据传输，用于查看公告和查看公告阅读情况时使用
	 * @param bean 发布的公告
	 * @param deptId 只显示一个部门的已阅未读情况时，该值不为空，否则为空
	 * @see #userView   查看公告时，发起者或管理员查看阅读信息时的展现：部门   已阅总数   未阅总数
	 * @see #bulReadIframe  在查看公告页面点击某一部门查看该部门的阅读详细情况：已阅人群（总数）|未阅人群（总数）；每一条阅读信息：（部门、人员姓名、阅读时间）
	 */
	private void setBulDataReadInfo(BulData bean, ModelAndView mav, String deptId) throws Exception {
		User user = CurrentUser.get();
		List<BulRead> readList = this.bulDataManager.getReadListByData(bean.getId());
		if (readList != null) {
			List<BulReadCount> bulreadcount = new ArrayList<BulReadCount>();
			Set<Long> scopeList = this.getAllMembersinPublishScope(bean);
			//将发布范围内的人员按照部门进行分组：key - 部门ID，value - 部门中在发布范围内的总人数
			Map<Long, Integer> map = new TreeMap<Long, Integer>();
			Map<Long, Integer> readCountMap = new TreeMap<Long, Integer>();
			for (Long memberId : scopeList) {
				V3xOrgMember member = orgManager.getMemberById(memberId);
				if (member != null && member.isValid()) {
					Long departmentId = member.getOrgDepartmentId();
					if (map.containsKey(departmentId)) {
						map.put(departmentId, map.get(departmentId) + 1);
					} else {
						map.put(departmentId, 1);
					}
//					if (!member.getOrgAccountId().equals(user.getLoginAccount())) {
//						Map<Long, List<ConcurrentPost>> concurent = orgManager.getConcurentPostsByMemberId(user.getLoginAccount(), member.getId());
//						if (concurent != null && !concurent.isEmpty()) {
//							Iterator<Long> iter = concurent.keySet().iterator();
//							if (iter.hasNext()) {
//								departmentId = iter.next();
//							}
//						} else {
//							// 兼职人员未设置部门、岗位
//							continue;
//						}
//					}
					//处理其他兼职部门
					List<Long> otherDepartmentIdList = getOtherDepartmentIdList( memberId );
					for( Long otherDepartmentId:otherDepartmentIdList ){
						if (map.containsKey(otherDepartmentId)) {
							map.put(otherDepartmentId, map.get(otherDepartmentId) + 1);
						} else {
							map.put(otherDepartmentId, 1);
						}
					}
				}
			}
			
			//公告发起人是否包含在发布范围中(发起人即便不在发布范围中，仍可以阅读公告并生成阅读记录)
			boolean isCreatorInPublishScope = scopeList.contains(bean.getCreateUser());
			//将已分组的部门中的人员，筛选出已阅和未读的人数
				for (BulRead br : readList) {
					V3xOrgMember member = orgManager.getMemberById(br.getManagerId());
					//如果阅读信息对应的用户不为正常状态，则不加入，如果阅读信息对应的用户是公告创建者，而其并不在发布范围中，也不加入
					if(member==null || !member.isValid() || (member.getId().equals(bean.getCreateUser()) && !isCreatorInPublishScope))
						continue;
					if (map.get(member.getOrgDepartmentId()) != null) {
						if (readCountMap.containsKey(member.getOrgDepartmentId())) {
							readCountMap.put(member.getOrgDepartmentId(), readCountMap.get(member.getOrgDepartmentId()) + 1);
						} else {
							readCountMap.put(member.getOrgDepartmentId(), 1);
						}
					}
					//处理其他兼职部门
					List<Long> otherDepartmentIdList = getOtherDepartmentIdList( member.getId() );
					for( Long otherDepartmentId:otherDepartmentIdList ){
						if (map.get(otherDepartmentId) != null) {
							if (readCountMap.containsKey(otherDepartmentId)) {
								readCountMap.put(otherDepartmentId, readCountMap.get(otherDepartmentId) + 1);
							} else {
								readCountMap.put(otherDepartmentId, 1);
							}
						}
					}
				}
				for (Long departmentId : map.keySet()) {
					int readCount = readCountMap.get(departmentId) != null ? readCountMap.get(departmentId) : 0;
					BulReadCount brcount = new BulReadCount();
					brcount.setMemberCount(map.get(departmentId));	// 取部门中在范围内的所有人员总数
					brcount.setDeptId(departmentId);				// 取部门的ID
					brcount.setEndReadCount(readCount);		// 设定已读人数
					// 设定未读人数(负数判断应该是由于之前的代码在处理已阅记录人员时未作其是否有效判断所导致的结果，虽不合理，暂先保留)
					if (map.get(departmentId) - readCount < 0) {
						brcount.setNotReadCount(0);
					} else {
						brcount.setNotReadCount(map.get(departmentId) - readCount);
					}
					bulreadcount.add(brcount);
					if(departmentId.toString().equals(deptId)) {
						mav.addObject("brc", brcount);
					}
				}
				//转换为List以便保持顺序,否则将会导致读取页面时顺序时常变动：按照已阅人数总数、部门id、人员id排序
				Collections.sort(bulreadcount);
				mav.addObject("bulreadcount", bulreadcount);
				//总的已读人员数（不直接从readList.size()取，是因为其中可能包含了无效人员）
				int bulendread = 0;
				for (BulReadCount vv : bulreadcount) {
					bulendread += vv.getEndReadCount();
				}
				
				//如果阅读量小于已阅读人数,设置阅读量等于阅读人数
				if(bean.getReadCount() < bulendread){
					bean.setReadCount(bulendread);
				}
				
				mav.addObject("bulendread", bulendread);
		}
	}
	
	public List<Long> getOtherDepartmentIdList( Long memberId ) throws Exception{
		List<Long> list = new ArrayList<Long>();
		List<V3xOrgAccount> accountList = orgManager.getConcurrentAccounts(memberId);
		if( accountList!=null && !accountList.isEmpty() ){
			for( V3xOrgAccount account:accountList ){
				Map<Long, List<ConcurrentPost>> map = orgManager.getConcurentPostsByMemberId(account.getId(), memberId);
				Set<Long> set = map.keySet();
				list.addAll(set);
			}
		}
		return list;
	}
	/**
	 * 公告阅读页面iframe
	 */
	public ModelAndView bulReadView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/user/bulReadView");
	}
	
	/**
	 * 公告阅读页面点击部门查看，其页面结构是显示上方的已读和未读页签及切换部门下拉选框，下方有一个iframe指向:
	 * @see #bulReadProperty
	 */
	public ModelAndView bulReadIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/user/data_bulReadIframe");
		String deptId = request.getParameter("deptId");
		String beanId = request.getParameter("beanId");
		BulData bean = Strings.isBlank(beanId) ? new BulData() : bulDataManager.getById(Long.valueOf(beanId));
		mav.addObject("bean", bean);
		this.setBulDataReadInfo(bean, mav, deptId);

		mav.addObject("deptId", deptId);		// 阅读的人的部门ID
		mav.addObject("beanId", beanId);		// 公告的ID
		mav.addObject("spaceType", bean.getType().getSpaceType());	// 公告所属公告板块所在空间类型
		return mav;
	}

	/**
	 * 公告阅读页面点击部门查看阅读情况
	 */
	public ModelAndView bulReadProperty(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("bulletin/user/data_bulReadProperties");
		String deptId = request.getParameter("deptId");		// 取到部门的ID，这个是阅读信息部门显示的ID
		String beanId = request.getParameter("beanId");		// 公告ID
		BulData bean = Strings.isBlank(beanId) ? new BulData() : bulDataManager.getById(Long.valueOf(beanId));

		//取得已经阅读的所有的人员的列表
		List<BulRead> readList = this.bulDataManager.getReadListByData(bean.getId());
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		Set<BulReadCount> bulreadcount = new HashSet<BulReadCount>();		// 已读
		Set<BulReadCount> bulnotreadcount = new HashSet<BulReadCount>();	// 未读

		Long currentDepartmentId = Long.valueOf(deptId);
		Set<Long> PublishScopeList = this.getAllMembersinPublishScope(bean);
		
		for (Long v : PublishScopeList) {
			V3xOrgMember member = orgManager.getMemberById(v);
			if (member == null || !member.isValid()) {
				continue;
			}

			// 汇总本部门内在发布范围中的总人数
			Long departmentId = member.getOrgDepartmentId();
//			Map<Long, List<ConcurrentPost>> concurent = orgManager.getConcurentPostsByMemberId(user.getLoginAccount(), member.getId());
//			if (concurent != null && !concurent.isEmpty()) {
//				Iterator<Long> iter = concurent.keySet().iterator();
//				if (iter.hasNext()) {
//					departmentId = iter.next();
//				}
//			}

			if (departmentId.equals(currentDepartmentId)) {
				memberList.add(member);
			}
			
			//处理其他兼职部门
			List<Long> otherDepartmentIdList = getOtherDepartmentIdList( member.getId() );
			for( Long otherDepartmentId:otherDepartmentIdList ){
				if (currentDepartmentId.equals(otherDepartmentId)) {
					memberList.add(member);
				}
			}
		}

		for (V3xOrgMember vm : memberList) {			//遍历里面的人和已经阅读的人的ID 是否相等
			BulReadCount brcnot = new BulReadCount();	// 未读
			BulReadCount brc = new BulReadCount();		// 已读
			int readFlagNum = 0;
			for (BulRead br : readList) {
				V3xOrgMember member = orgManager.getMemberById(br.getManagerId());
				if(member==null || !member.isValid())
					continue;
				
				if (member.getId().equals(vm.getId())) {// 已读--取已读人的信息
					//brc.setDeptId(member.getOrgDepartmentId());
					brc.setDeptId(currentDepartmentId);
					brc.setUserId(member.getId());
					brc.setReadDate(br.getReadDate());
					bulreadcount.add(brc);
					readFlagNum++;
				}
			}
			//此人未读，加入到未读群中
			if (readFlagNum == 0) {
				//brcnot.setDeptId(vm.getOrgDepartmentId());
				brcnot.setDeptId(currentDepartmentId);
				brcnot.setUserId(vm.getId());
				bulnotreadcount.add(brcnot);
			}
		}
		return mav.addObject("bulreadcount", this.convert2OrderedList(bulreadcount))
				  .addObject("bulnotreadcount", this.convert2OrderedList(bulnotreadcount));
	}
	
	
	/**
	 * 将无序的集合转换为有序排列，以便前端展现
	 * @param bulreadcount
	 */
	private List<BulReadCount> convert2OrderedList(Set<BulReadCount> bulreadcount) {
		List<BulReadCount> bulReadCountList = new ArrayList<BulReadCount>();
		if(bulreadcount!=null && bulreadcount.size()>0) {
			bulReadCountList.addAll(bulreadcount);
			Collections.sort(bulReadCountList);
		}
		return bulReadCountList;
	}
	
	/**
	 * 公告管理员配置公告发起员 公告管理员可以为自己管理的公告类型分别配置每种类型的公告发起者
	 */
	public ModelAndView configWrite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String act = request.getParameter("act");
		String spaceType = request.getParameter("spaceType");
		Long typeIdTo = (Long) request.getSession().getAttribute("bulletin.typeId");
		BulType bulType = this.bulTypeManager.getById(typeIdTo);
		ModelAndView mav;
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		
		//回显选人组件ID值
		List<BulTypeManagers> list = this.bulTypeManager.findTypeWriters(bulType);
		String managerId = null;
		if (list != null && list.size()>0) {
			StringBuffer strbuf = new StringBuffer();
			for (BulTypeManagers managers : list) {
				strbuf.append(managers.getManagerId() + ",");
			}
			managerId = strbuf.substring(0, strbuf.length() - 1);			
		}
		List<BulType> type = new ArrayList<BulType>();
		type.add(bulType);
		// view：查看配置情况
		// save：保存配置
		if (act == null || act.equals("view")) {
			mav = new ModelAndView("bulletin/manager/config_write");
			mav.addObject("managerId", managerId);
			mav.addObject("list", type);
		} else {
			Long typeId = Long.valueOf(request.getParameter("typeId"));
			String typeName = bulType.getTypeName();
			
			String value = request.getParameter("userIds");
			if (Strings.isBlank(value)) {
				this.bulTypeManager.saveWriteByType(typeId, new String[0][0]);
				//对整个操作记录应用日志
				appLogManager.insertLog(user, AppLogAction.Bulletin_PostAuth_Update, userName, typeName);
			} 
			else {
				String[][] authInfoArray = Strings.getSelectPeopleElements(value);
				StringBuffer entityNames4OperLog = new StringBuffer("");
				for(String[] strArray : authInfoArray) {
					String entityType = strArray[0];
					String entityID = strArray[1];
					V3xOrgEntity entity = this.orgManager.getEntity(entityType, Long.valueOf(entityID));
					entityNames4OperLog.append(entity.getName() + ",");
				}
				
				//构造消息接收者
				List<Long> auth = new ArrayList<Long>();
				Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(value);
				for (V3xOrgMember entity : membersSet) {
					if (!userId.equals(entity.getId())) {
						auth.add(entity.getId());
					}
				}

				//消息过滤
				List<BulTypeManagers> old = this.bulTypeManager.findTypeWriters(bulType);
				if (old != null) {
					for (BulTypeManagers btm : old) {
						auth.remove(btm.getManagerId());
					}
				}
				
				userMessageManager.sendSystemMessage(MessageContent.get(
						"bul.accredit", bulType.getTypeName(), this.getLoginUserName(request)),
						ApplicationCategoryEnum.bulletin, this.getLoginUserId(request), 
						MessageReceiver.get(bulType.getId(), auth, "", String.valueOf(typeId)), bulType.getId());
				
				this.bulTypeManager.saveWriteByType(typeId,authInfoArray);
				//对整个操作记录应用日志
				appLogManager.insertLog(user, AppLogAction.Bulletin_PostAuth_Update, userName, typeName);
			}
			mav = new ModelAndView("bulletin/result");
		}
		mav.addObject("spaceType", spaceType);
		return mav;
	}

	/**
	 * 公告管理员对公告进行置顶操作，支持批量置顶
	 */
	public ModelAndView top(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = StringUtils.defaultIfEmpty(request.getParameter("id"), "");
		String oper = request.getParameter("oper");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isNotBlank(idStr)) {
			List<Long> ids = CommonTools.parseStr2Ids(idStr);
			if (CollectionUtils.isNotEmpty(ids)) {
				if (Strings.isBlank(oper)) {
					try {
						bulDataManager.top(ids);
					} catch (BusinessException e) {
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('归档的公告不允许再置顶!')");
						out.println("</script>");
					}
				} else {
					this.bulDataManager.cancelTop(ids);
				}
			}
		}

		String spaceType = request.getParameter("spaceType");
		String bulTypeId = request.getParameter("bulTypeId");
		String showAudit = request.getParameter("showAudit");
		return this.redirectModelAndView("/bulData.do?method=listMain&spaceType="
						+ spaceType + "&type=" + bulTypeId + "&showAudit=" + showAudit + "&spaceId=" + spaceId);
	}

	/**
	 * 公告管理员查看公告统计情况页面框架
	 */
	public ModelAndView statisticsIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("bulletin/manager/statisticsIframe");
	}
	
	/**
	 * 公告管理员查看公告统计情况
	 */
	public ModelAndView statistics(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String type = request.getParameter("type");
		String sbulTypeId = request.getParameter("bulTypeId");
		String listIframe = request.getParameter("listIframe");
		ModelAndView mav = new ModelAndView("bulletin/manager/statistics");
		if ("listIframe".equals(listIframe)) {
			mav = new ModelAndView("bulletin/manager/statisticsList");
			List<Object[]> list = new ArrayList<Object[]>();
			if (StringUtils.isNotBlank(sbulTypeId)) {
				// 强制把所有的数据更新到数据库，以保证阅读次数与数据库记录一致（不实时亦可...）
				this.bulDataCache.getDataCache().updateAll();
				long bulTypeId = Long.valueOf(sbulTypeId);
				list = bulDataManager.statistics(type, bulTypeId);
			}
			mav.addObject("list", list);
		}
		return mav;
	}

	/**
	 * 公告板块管理员将已发布的公告归档，支持批量归档
	 */
	public ModelAndView pigeonhole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		User user = CurrentUser.get();
		String userName = user.getName();
		String[] archiveIds = request.getParameterValues("archiveId");
		if (StringUtils.isNotBlank(ids)) {
			String[] idA = ids.split(",");
			List<Long> idList = new ArrayList<Long>();
			for(int i=0;i<idA.length;i++) {
				if (StringUtils.isNotBlank(idA[i])) {
					Long _archiveId = Long.valueOf(archiveIds[i]);
					DocResource res = docHierarchyManager.getDocResourceById(_archiveId);
					//归档记录应用日志 added by Meng Yang at 2009-08-20
					if(res != null){
						String folderName = docHierarchyManager.getNameById(res.getParentFrId());
						appLogManager.insertLog(user, AppLogAction.Bulletin_Pigeonhole, userName, res.getFrName(), folderName);
					}
					
					idList.add(Long.valueOf(idA[i]));
					//更新缓存中新闻的状态为归档,防止通过系统提示信息查看归档后的新闻
					Long longID = Long.valueOf(idA[i]);
					BulData bean = this.bulDataCache.getDataCache().get(longID);
					if (bean != null) {
						bean.setState(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
						this.bulDataCache.getDataCache().save(longID, bean, bean.getPublishDate().getTime(), (bean.getReadCount() == null ? 0 : bean.getReadCount()));						
					}
				}
			}
			this.bulDataManager.pigeonhole(idList);
		}
		return super.refreshWorkspace();
	}
	
	/**
	 * 关联文档	
	 */
	public ModelAndView showList4QuoteFrame(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    ModelAndView modelAndView = new ModelAndView("bulletin/user/list4QuoteFrame");	    	
	    return modelAndView;
	}
	 
	public ModelAndView list4QuoteFrame(HttpServletRequest request, HttpServletResponse response)  {
		return new ModelAndView("collaboration/list4QuoteFrame");
	}

	public void destoryOperate(HttpServletRequest request) {
		request.getSession().removeAttribute("bulletin.typeId");
		request.getSession().removeAttribute("bulletin.type");
		request.getSession().removeAttribute("bulletin.isWriter");
		request.getSession().removeAttribute("bulletin.isAuditer");
		request.getSession().removeAttribute("bulletin.isManager");
		request.getSession().removeAttribute("bulletin.isShowPublish");
		request.getSession().removeAttribute("bulletin.isShowAudit");
		request.getSession().removeAttribute("bulletin.isShowManage");
		request.getSession().removeAttribute("bulletin.groupSign");
		request.getSession().removeAttribute("bulletin.deptSign");
		request.getSession().removeAttribute("bulletin.spaceType");
	}

	public void initOperate(HttpServletRequest request, String typeIdStr) {
		Long userId = CurrentUser.get().getId();
		if (typeIdStr == null || "".equals(typeIdStr.trim()))
			return;
		//设置管理按钮
		Long typeId = Long.valueOf(typeIdStr);
		boolean isShowPublish = false;
		boolean isShowAudit = false;
		boolean isShowManage = false;
		boolean isWriter = false;
		boolean isAuditer = false;
		boolean isManager = false;

		//发起员
		List<BulType> typeList = this.bulDataManager.getTypeListByWrite(CurrentUser.get().getId(), true);
		for (BulType type : typeList) {
			if (type.getId().longValue() == typeId.longValue()) {
				isWriter = true;
				break;
			}
		}
		BulType type = this.bulTypeManager.getById(typeId);
		if (type != null) {
			if (type.isAuditFlag() && type.getAuditUser().longValue() == userId.longValue()) {
				isAuditer = true;
			}
		}

		String groupSign = (String) request.getSession().getAttribute("bulletin.groupSign");// 集团标志
		if (groupSign != null) {
			isShowAudit = !bulTypeManager.getAuditGroupBulType(userId).isEmpty();
		} else {
			isShowAudit = !bulTypeManager.getAuditUnitBulType(userId).isEmpty();
		}

		try {
			isShowManage = this.bulDataManager.getTypeList(userId, true).size() > 0;
		} catch (Exception e) {
			log.error("", e);
		}

		if (type!=null && type.getManagerUserIds().indexOf(userId.toString()) > -1) {
			isManager = true;
		}

		isShowPublish = isWriter || isManager;
		request.getSession().setAttribute("bulletin.typeId", typeId);
		request.getSession().setAttribute("bulletin.type", type);
		request.getSession().setAttribute("bulletin.isWriter", isWriter);
		request.getSession().setAttribute("bulletin.isAuditer", isAuditer);
		request.getSession().setAttribute("bulletin.isManager", isManager);
		request.getSession().setAttribute("bulletin.isShowPublish", isShowPublish);
		request.getSession().setAttribute("bulletin.isShowAudit", isShowAudit);
		request.getSession().setAttribute("bulletin.isShowManage", isShowManage);
	}
	
	/**
	 * portal显示板块列表
	 */
	public ModelAndView showDesignated(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/user/showDesignated");
		List<BulType> typeList = null;
		String group = request.getParameter("group");
		String textfield = request.getParameter("textfield");

		if (Strings.isNotBlank(group)) {
			typeList = bulTypeManager.groupFindAllByNoPage();
		} else {
			typeList = bulTypeManager.boardFindAllByNoPage();
		}

		List<BulType> resultList = new ArrayList<BulType>();
		if (Strings.isNotBlank(textfield)) {
			for (BulType type : typeList) {
				if (type.getTypeName().contains(textfield)) {
					resultList.add(type);
				}
			}
		} else {
			resultList = typeList;
		}

		mav.addObject("typeList", resultList);
		return mav;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	public void setMtContentTemplateManagerCAP(MtContentTemplateManagerCAP mtContentTemplateManagerCAP) {
		this.mtContentTemplateManagerCAP = mtContentTemplateManagerCAP;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public void setBulReadManager(BulReadManager bulReadManager) {
		this.bulReadManager = bulReadManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public void setBulDataCache(BulDataCache bulDataCache) {
		this.bulDataCache = bulDataCache;
	}

	public BulDataCache getBulDataCache() {
		return bulDataCache;
	}
	
	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}
	
}