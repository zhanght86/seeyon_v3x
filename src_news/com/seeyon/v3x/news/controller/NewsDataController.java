package com.seeyon.v3x.news.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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

import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.parser.StrExtractor;
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
import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsLog;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.domain.NewsTypeManagers;
import com.seeyon.v3x.news.domain.NewsTypeModel;
import com.seeyon.v3x.news.manager.BaseNewsManager;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsReadManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.news.util.NewsDataLock;
import com.seeyon.v3x.news.util.NewsDataLockAction;
import com.seeyon.v3x.news.util.NewsUtils;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.cache.ClickDetail;
import com.seeyon.v3x.util.cache.DataCache;

/**
 * 新闻模块最重要的Controller。包括了新闻发起员、新闻审核员、新闻管理员、普通用户的操作。
 * 对文件的加锁和解锁包括两个操作:
 * 一个是修改edit,一个是审核audit,auditOper
 * @author wolf
 */
public class NewsDataController extends BaseController {
	private NewsDataManager newsDataManager;	
	private NewsReadManager newsReadManager;
	private AttachmentManager attachmentManager;
	private IndexManager indexManager;
	private AffairManager affairManager;	
	private AppLogManager appLogManager;
	private static final Log log = LogFactory.getLog(NewsDataController.class);
	private UpdateIndexManager updateIndexManager;
	private DataCache<NewsData> dataCache;
	private UserMessageManager userMessageManager;
	private NewsTypeManager newsTypeManager;	
	private OrgManager orgManager;
	private FileManager fileManager;
	private MtContentTemplateManagerCAP mtContentTemplateManagerCAP;	
	private DocHierarchyManager docHierarchyManager;
	private SpaceManager spaceManager;
	private PortletEntityPropertyManager portletEntityPropertyManager;

	@Override
	/**
	 * 进入新闻首页方法
	 * @author lucx
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		User user = CurrentUser.get();
		String spaceId = request.getParameter("spaceId");
		ModelAndView mav = new ModelAndView("news/user/data_list_iframe");
		String accountName = orgManager.getAccountById(CurrentUser.get().getLoginAccount()).getShortname();
		String groupName =orgManager.getRootAccount(CurrentUser.get().getLoginAccount()).getShortname();
		if (Strings.isNotBlank(spaceId)) {
			String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
			mav.addObject("spaceName", spaceName);
			mav.addObject("publicCustom", true);
		}
		mav.addObject("accountName", accountName);
		mav.addObject("groupName", groupName);
		Map<Long, List<NewsData>> map = null;

		List<NewsType> typeList = null;
		
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr))
		{
			spaceTypeInt = Integer.valueOf(spaceTypeStr);

		}
		mav.addObject("spaceType", spaceTypeInt);
		boolean ngroup = (spaceTypeInt != Constants.NewsTypeSpaceType.group.ordinal());
		if (ngroup) {
			if (spaceTypeInt == Constants.NewsTypeSpaceType.public_custom.ordinal()) {
				typeList = newsDataManager.getAllTypeList(Long.parseLong(spaceId), "publicCustom");
			} else if (spaceTypeInt == Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
				typeList = newsDataManager.getAllTypeList(Long.parseLong(spaceId), "publicCustomGroup");
			}else {
				typeList = newsDataManager.getAllTypeList(user.getLoginAccount());
			}
		} else {
			typeList = newsDataManager.getGroupAllTypeList();
		}
		//查询所有新闻的类型
		if (typeList != null)
			Collections.sort(typeList);

		// 外部人员guolv
		List<NewsType> typeList2 = new ArrayList<NewsType>();
		typeList2.addAll(typeList);
		if (typeList2 != null && !CurrentUser.get().isInternal())
			for (NewsType nt : typeList2)
			{
				if (!nt.getOutterPermit())
					typeList.remove(nt);
			}
		//map代表的是一个新闻类型下对应的多条新闻
		try
		{
			map = newsDataManager.findByReadUserHome(user.getId(), typeList);
		} catch (BusinessException e)
		{
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		mav.addObject("group", (ngroup ? "" : "group"));

		List<List<NewsData>> list2 = new ArrayList<List<NewsData>>();
		int pendingTotal = 0;
		//审核员对某个类型的待办总数,前题条件是当前用户为审核员
		for (NewsType bt : typeList)
		{
			if (bt.getCanAuditOfCurrent())
			{
				pendingTotal = this.newsDataManager.getPendingCountOfUser(user.getId(), spaceTypeInt);
				break;
			}
		}
		//创建模板
		List<NewsTypeModel> ntms = new ArrayList<NewsTypeModel>();
		for (NewsType bt : typeList)
		{
			list2.add(map.get(bt.getId()));
			ntms.add(new NewsTypeModel(bt, user.getId(), this.orgManager
					.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
							V3xOrgEntity.ORGENT_TYPE_ACCOUNT,
							V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
							V3xOrgEntity.ORGENT_TYPE_TEAM,
							V3xOrgEntity.ORGENT_TYPE_POST,
							V3xOrgEntity.ORGENT_TYPE_ROLE,
							V3xOrgEntity.ORGENT_TYPE_LEVEL,
							V3xOrgEntity.ORGENT_TYPE_MEMBER)));

		}

		for (NewsTypeModel a : ntms) {
			String idns = "";
			String Ids = a.getNewsType().getManagerUserIds();
			if (Strings.isNotBlank(Ids)) {
				String[] idStrs = Ids.split(",");
				for (String str : idStrs) {
					V3xOrgMember member = orgManager.getMemberById(Long.valueOf(str));
					if (member!=null && member.isValid() && !member.getIsDeleted())
						idns += str + ",";
				}
				a.getNewsType().setManagerUserIds(idns);
			}
		}
		mav.addObject("typeList", ntms);
		mav.addObject("list2", list2);
		mav.addObject("pendingTotal", pendingTotal);

		return mav;
	}

	/**
	 * 新闻首页的查询
	 * @author lucx
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView indexSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		destoryOperate(request);
		ModelAndView mav = new ModelAndView("news/user/news_search");

		User user = CurrentUser.get();

		List<NewsData> list = null;
		//设置查询的条件和字段
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		List<NewsType> typeList = null;
		String spaceId = request.getParameter("spaceId");
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr))
		{
			spaceTypeInt = Integer.valueOf(spaceTypeStr);

		}
		mav.addObject("spaceType", spaceTypeInt);
		//ngroup判断是不是企业新闻
		boolean ngroup = (spaceTypeInt != Constants.NewsTypeSpaceType.group.ordinal());
		if (ngroup) {
			if(Strings.isNotBlank(spaceId)){
				typeList = newsDataManager.getAllTypeList(Long.parseLong(spaceId), spaceTypeInt == 5 ? "publicCustom" : "publicCustomGroup");
			}else{
				typeList = newsDataManager.getAllTypeList(user.getLoginAccount());
			}
		} else {
			typeList = newsDataManager.getGroupAllTypeList();
		}

		// 外部人员guolv
		List<NewsType> typeList2 = new ArrayList<NewsType>();
		typeList2.addAll(typeList);
		if (typeList2 != null && !CurrentUser.get().isInternal())
			for (NewsType nt : typeList2)
			{
				if (!nt.getOutterPermit())
					typeList.remove(nt);
			}

		try
		{
			if(user==null)
			{
				list=Collections.EMPTY_LIST;
			}else
			{
				//从管理员界面的新闻列表页面点击
				if (ngroup){
					if(Strings.isNotBlank(spaceId)){
						list = newsDataManager.findByReadUser(user.getId(), condition, textfield, typeList, Long.parseLong(spaceId), spaceTypeInt == 5 ? "publicCustom" : "publicCustomGroup");
						mav.addObject("publicCustom", true);
						mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
					}else{
						list = newsDataManager.findByReadUser(user.getId(), condition, textfield, typeList, user.getLoginAccount());
					}
				} else {
					list = newsDataManager.groupFindByReadUser(user.getId(), condition, textfield, typeList);
				}
			}
		} catch (BusinessException e)
		{
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		mav.addObject("group", (ngroup ? "" : "group"));
		mav.addObject("typeList", typeList);

		mav.addObject("list", list);
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);
		return mav;
	}

	/**
	 * //集团发布显示板块列表~~~~~~~
	 * 
	 * @author lucx
	 * 
	 */
	public ModelAndView groupIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		destoryOperate(request);

		//Long userId = CurrentUser.get().getId();

		ModelAndView mav;
		mav = new ModelAndView("news/user/data_list_group_iframe");
		request.getSession().setAttribute("news.groupSign", "0");// 集团新闻空间进入创建SESSION

		List<NewsType> typeList = newsDataManager.getGroupAllTypeList();// 取的所有的集团新闻！
		// List<NewsType>
		// typeList=newsTypeManager.getGroupNewsType();//取的所有的集团新闻！不帶有新聞個數
		// List<NewsType>
		// typeList=newsDataManager.getManagerGroupBulType(userId,
		// true);//取的有我来管理的集团新闻
		mav.addObject("typeList", typeList);

		return mav;
	}
	/**
	 * 弹出创建新闻时的高级页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView openAdvance(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("news/write/data_advanced_create");
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)){
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		if (spaceTypeInt == 0){// 集团
			mav.addObject("templateList", mtContentTemplateManagerCAP
					.findGroupTypeAll("4"));
		} else {// 单位和部门
			mav.addObject("templateList", mtContentTemplateManagerCAP.findTypeAll("4"));
		}
		return mav;
	}
	/**
	 * 发起员创建新闻
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("news/write/data_create");
		NewsData bean = new NewsData();
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		bean.setReadCount(0);

		// 设置发布部门
		if (bean.getPublishDepartmentId() == null)
		{
			// 设置为发起者所在部门
			Long userId = bean.getCreateUser();
			Long depId = ((BaseNewsManager) this.newsDataManager)
					.getNewsUtils().getMemberById(userId).getOrgDepartmentId();
			bean.setPublishDepartmentId(depId);
		}
		String custom = request.getParameter("custom");
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr))
		{
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		mav.addObject("spaceType", spaceTypeInt);

		String newsTypeStr = request.getParameter("newsTypeId");
		mav.addObject("newsTypeId", newsTypeStr);
		bean.setTypeId(Long.valueOf(newsTypeStr));
		NewsType theType = this.newsTypeManager.getById(Long.valueOf(newsTypeStr));
		bean.setType(theType);
		if ("true".equals(custom)) {
			mav.addObject("custom", custom);
			mav.addObject("theType", theType);
		}
		boolean groupType = false;
		//判断是否是集团新闻
		if ((theType != null)
				&& (theType.getSpaceType().intValue() == Constants.NewsTypeSpaceType.group
						.ordinal()))
			groupType = true;

		bean.setPublishDepartmentName(((BaseNewsManager) this.newsDataManager)
				.getNewsUtils().getDepartmentNameById(
						bean.getPublishDepartmentId(), groupType));

		// 处理模板加载
		String oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper))
		{
			super.bind(request, bean);
			String templateId = request.getParameter("templateId");
			if (StringUtils.isNotBlank(templateId))
			{
				// NewsTemplate
				// template=this.newsDataManager.getNewsTemplateManager().getById(Long.valueOf(templateId));
				MtContentTemplateCAP template = mtContentTemplateManagerCAP
						.getById(Long.valueOf(templateId));// 根据新闻格式ID取格式
				//String attaFlag=null;
				if (template != null)
				{
					bean.setDataFormat(template.getTemplateFormat());
					bean.setContent(template.getContent());
					bean.setCreateDate(template.getCreateDate());
					mav.addObject("templateId", template.getId());
					/*List<Attachment> attachments = attachmentManager.getByReference(attRefId, attRefId);
					mav.addObject("attachments", attachments);*/
				}

				// if(bean.getTypeId()!=null){
				// bean.setType(this.newsDataManager.getNewsTypeManager().getById(bean.getTypeId()));
				// }

				mav.addObject("originalNeedClone", true);
			} else
			{
				bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
				bean.setContent(null);
			}

		} else
		{
			String templateId = "";
			templateId = bean.getType().getDefaultTemplate() == null ? ""
					: bean.getType().getDefaultTemplate().getId().toString();
			if (StringUtils.isNotBlank(templateId))
			{
				// NewsTemplate
				// template=this.newsDataManager.getNewsTemplateManager().getById(Long.valueOf(templateId));
				MtContentTemplateCAP template = mtContentTemplateManagerCAP
						.getById(Long.valueOf(templateId));// 根据新闻格式ID取格式
				bean.setDataFormat(template.getTemplateFormat());
				bean.setContent(template.getContent());
				bean.setCreateDate(template.getCreateDate());
				// if(bean.getTypeId()!=null){
				// bean.setType(this.newsDataManager.getNewsTypeManager().getById(bean.getTypeId()));
				// }
				mav.addObject("templateId", template.getId());
				mav.addObject("originalNeedClone", true);
			}
		}
		String newsTypeIds = request.getParameter("newsTypeId");
		NewsType Type = this.newsTypeManager.getById(Long.valueOf(newsTypeIds));
		int spaceType = Type.getSpaceType();
		Constants.NewsTypeSpaceType newsTypeSpaceType = Constants.valueOfSpaceType(spaceType);
		String spaceId = request.getParameter("spaceId");
		List<NewsType> newsTypeList = new ArrayList<NewsType>();
		if (Strings.isNotBlank(spaceId)) {
			newsTypeList = this.newsTypeManager.getTypesCanNewByMember(user.getId(), newsTypeSpaceType, Long.parseLong(spaceId));
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		} else {
			newsTypeList = this.newsTypeManager.getTypesCanNewByMember(user.getId(), newsTypeSpaceType, user.getLoginAccount());
		}
		
		//处理附件,默认的是不管从正常切换到正常的格式,还是正常的格式转换到格式附件都不应该丢的
		String attaFlag=null;
		//第一次或者说新建的时候bean.getId()肯定是空的
		Long attRefId = null;
		//是不是第一次,true为第一次,false为不是第一次
		String attFlagStr=request.getParameter("attFlag");
		boolean attFlag=true;
		if("false".equalsIgnoreCase(attFlagStr))
		{
			attFlag=false;
		}
		//点击修改时候进来的,这个时候新闻已有ID了
		String idStr = request.getParameter("id");
		if(!"".equalsIgnoreCase(idStr)&&idStr!=null)
		{
			bean.setId(Long.valueOf(idStr));
			attachmentManager.deleteByReference(bean.getId(), bean.getId());
			attaFlag = attachmentManager.create(ApplicationCategoryEnum.news,
					bean.getId(), bean.getId(), request);
			attFlag=false;
			mav.addObject("attRefId", attRefId);
			mav.addObject("attFlag", attFlag);
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}else 
		{
			if(attFlag)
			{
				//第一次切换时执行以下方法
				Long newId = UUIDLong.longUUID(); 
				attaFlag = attachmentManager.create(ApplicationCategoryEnum.news,
						newId, newId, request);
				attRefId = newId;
				attFlag=false;
				mav.addObject("attRefId", attRefId);
				mav.addObject("attFlag", attFlag);
			}
			else
			{
				//第二次切换,会传递一个ID,应该先删除原来的附件,再创建一个ID;
				attRefId=Long.valueOf(request.getParameter("attRefId"));
				attachmentManager.deleteByReference(attRefId, attRefId);
				attaFlag = attachmentManager.create(ApplicationCategoryEnum.news,
						attRefId, attRefId, request);
				attFlag=false;
				mav.addObject("attRefId", attRefId);
				mav.addObject("attFlag", attFlag);
			}
			List<Attachment> attachments = attachmentManager.getByReference(attRefId, attRefId);
			mav.addObject("attachments", attachments);
		}
		if (com.seeyon.v3x.common.filemanager.Constants
				.isUploadLocaleFile(attaFlag))
		{
			bean.setAttachmentsFlag(true);
		}
		/**  图片新闻 **/
		bean.setImageNews(Strings.isNotBlank(request.getParameter("imageNews")));
		bean.setFocusNews(Strings.isNotBlank(request.getParameter("focusNews")));
		String imageId = request.getParameter("imageId");
		if(Strings.isNotBlank(imageId)){
			bean.setImageId(NumberUtils.toLong(imageId));
		}
		
		mav.addObject("bean", bean);
		mav.addObject("constants", new Constants());
		mav.addObject("newsTypeList", newsTypeList);
		if (spaceTypeInt == 0)
		{// 集团
			mav.addObject("templateList", mtContentTemplateManagerCAP
					.findGroupTypeAll("4"));// 统一接口 3 公告
		} else
		{// 单位和部门
			mav.addObject("templateList", mtContentTemplateManagerCAP
					.findTypeAll("4"));// 统一接口 3 公告
		}
		return mav;
	}

	/**
	 * 发起员编辑新闻
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		NewsData bean;
		
		if (StringUtils.isBlank(idStr)){
			bean = new NewsData();
		} else {
			bean = this.dataCache.get(Long.valueOf(idStr));
			if (bean == null)
				bean = newsDataManager.getById(Long.valueOf(idStr));
			bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
		}
		
		User user = CurrentUser.get();
		//检验新闻是否被加锁 
		String action = NewsDataLockAction.NEWLOCK_EDITING;
		NewsDataLock newslock = newsDataManager.lock(Long.valueOf(idStr),action);
		if(newslock!=null && !NewsDataLockAction.NEWLOCK_EDITING.equals(newslock.getAction()) && newslock.getUserid()!=user.getId()) {
			V3xOrgMember orm = orgManager.getMemberById(newslock.getUserid());
			String lockmessage = newslock.getAction();
			super.rendJavaScript(response,  "alert('"+ ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, 
											lockmessage,orm.getName() ) +"');self.history.back();");
			return null;
		}
		
		ModelAndView mav = new ModelAndView("news/write/data_create");
		mav.addObject("bean", bean);

		List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);
		mav.addObject("constants", new Constants());

		int spaceType = bean.getType().getSpaceType();
		Constants.NewsTypeSpaceType newsTypeSpaceType = Constants.valueOfSpaceType(spaceType);
		List<NewsType> newsTypeList = new ArrayList<NewsType>();
		if (Strings.isNotBlank(spaceId)) {
			newsTypeList = this.newsTypeManager.getTypesCanNewByMember(user.getId(), newsTypeSpaceType, Long.parseLong(spaceId));
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		} else {
			newsTypeList = this.newsTypeManager.getTypesCanNewByMember(user.getId(), newsTypeSpaceType, user.getLoginAccount());
		}
		if ("true".equals(custom)) {
			NewsType theType = this.newsTypeManager.getById(Long.valueOf(bean.getTypeId()));
			mav.addObject("custom", custom);
			mav.addObject("theType", theType);
		}
		mav.addObject("bean", bean);
		mav.addObject("constants", new Constants());
		mav.addObject("newsTypeList", newsTypeList);
		mav.addObject("spaceType", spaceType);
		
		if (spaceType == 0) {// 集团
			mav.addObject("templateList", mtContentTemplateManagerCAP.findGroupTypeAll("4"));	// 统一接口 3 公告
		} else {// 单位和部门
			mav.addObject("templateList", mtContentTemplateManagerCAP.findTypeAll("4"));	// 统一接口 3 公告
		}
		return mav;
	}

	/**
	 * 新闻管理员发布、取消发布新闻页面。目前发布、取消发布已经不需要打开新页面，所以不需要该方法了
	 * @deprecated
	 */
	public ModelAndView publish(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String idStr = request.getParameter("id");
		if (StringUtils.isBlank(idStr))
		{
			NewsException e = new NewsException("news_not_exists");
			request.getSession().setAttribute("_my_exception", e);
			return new ModelAndView("news/error");
		}
		NewsData bean = newsDataManager.getById(Long.valueOf(idStr));
		bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
		ModelAndView mav = new ModelAndView("news/manager/data_publish");
		mav.addObject("bean", bean);

		// mav.addObject("formatMetaData",
		// MetaDataUtil.getMetaDatas("news_format"));
		mav.addObject("typeList", this.newsDataManager.getTypeList(CurrentUser
				.get().getId(), true));
		return mav;
	}

	/**
	 * 新闻管理员执行发布、取消发布新闻操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView publishOper(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String userName = user.getName();		
		String idStr = request.getParameter("id");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isBlank(idStr)) {
			idStr = "";
		}
		String form_oper = request.getParameter("form_oper");
		String[] idStrs = idStr.split(",");
		NewsType type = null;
		
		for (String str : idStrs) {
			if (StringUtils.isBlank(str))
				continue;
			
			NewsData bean = this.dataCache.get(Long.valueOf(str));
			if (bean == null)
				bean = newsDataManager.getById(Long.valueOf(str));
			if (bean == null)
				continue;
			type = bean.getType();
			
			List<Long> resultIds = new ArrayList<Long>();
			List<V3xOrgMember> listMemberId = new ArrayList<V3xOrgMember>();
			if ("true".equals(custom)) {
				listMemberId = spaceManager.getSpaceMemberBySecurity(bean.getTypeId(), -1);
			} else if (Strings.isNotBlank(spaceId)) {
				listMemberId = spaceManager.getSpaceMemberBySecurity(Long.parseLong(spaceId), -1);
			} else {
				listMemberId = ((BaseNewsManager) this.newsDataManager)
						.getNewsUtils().getScopeMembers(type.getSpaceType(), user.getLoginAccount(),
								type.getOutterPermit());
			}
			for (V3xOrgMember member : listMemberId) {
				resultIds.add(member.getId());
			}
			
			if ("publish".equals(form_oper)) {
				if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH) {
					continue;
				}
				//判断是否需要审核后才能发布，审核人ID为 0 直接发布。不为 0 给出提示
				if (type != null && type.getAuditUser().intValue() != 0) {
					if (bean.getState() != Constants.DATA_STATE_ALREADY_AUDIT) {
						if (bean.getState().intValue() == Constants.DATA_STATE_NOPASS_AUDIT)
							request.getSession().setAttribute("_my_exception", new BusinessException("news_not_pass"));
						else
							request.getSession().setAttribute("_my_exception", new BusinessException("news_not_audit"));
						break;
					}
				}
				bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
				//如果该新闻类型无审核员，则其最终审核记录状态设置为"无审核"，否则设置为"审核通过" added by Meng Yang 2009-06-11
				if(type!=null && !type.isAuditFlag()) {
					// 无审核员，设置为"无审核"
					bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_NO));
				} else if(type!=null && type.isAuditFlag()) {
					// 有审核员，设置为"审核通过"
					bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PASS));
				}
				bean.setPublishDate(new Date());
				bean.setPublishUserId(CurrentUser.get().getId());

				bean.setReadCount(0);
				bean.setUpdateDate(new Date());
				bean.setUpdateUser(CurrentUser.get().getId());
				this.newsDataManager.updateDirect(bean);
				bean.setTopOrder(Byte.valueOf("1"));

				// 配置发布范围
				if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH)
					this.newsDataManager.getNewsReadManager().configReadByData(bean);

				// 这里加入全文检索
				try {
					IndexEnable indexEnable = (IndexEnable) newsDataManager;
					IndexInfo info = indexEnable.getIndexInfo(bean.getId());
					indexManager.index(info);
				} catch (Exception e) {
					log.error("全文检索失败", e);
				}

				// 直接发布加日志
				appLogManager.insertLog(user, AppLogAction.News_Publish, userName, bean.getTitle());
			} else {
				this.dataCache.remove(Long.valueOf(str));
				// 取消发布，回到草稿状态
				if (bean.getState() != Constants.DATA_STATE_ALREADY_PUBLISH)
					continue;

				bean.setState(Constants.DATA_STATE_NO_SUBMIT);
				bean.setAuditAdvice(null);

				bean.setPublishDate(null);
				bean.setPublishUserId(null);

				bean.setReadCount(0);
				bean.setUpdateDate(null);
				bean.setUpdateUser(null);
				// 取消置顶
				bean.setTopOrder(Byte.valueOf("0"));
				this.newsDataManager.updateDirect(bean);

				// 取消发布加日志
				appLogManager.insertLog(user, AppLogAction.News_CancelPublish, userName, bean.getTitle());

				// 在此从全文检索库中删除
				try {
					indexManager.deleteFromIndex(ApplicationCategoryEnum.news, bean.getId());
				} catch (Exception e) {
					log.error("全文检索：", e);
				}
				//取消发布新闻发送系统消息
				userMessageManager.sendSystemMessage(
						MessageContent.get("news.cancel.publish", bean.getTitle(),this.getLoginUserName(request))
						.setBody(this.newsDataManager.getBody(bean.getId()).getContent(), bean.getDataFormat(),bean.getCreateDate()),
						ApplicationCategoryEnum.news,
						this.getLoginUserId(request),
						MessageReceiver.get(bean.getId(), resultIds, "", "", new Timestamp(System.currentTimeMillis())+ ""),
						bean.getType().getId());
				
			}
			// 发送审合通过消息消息进行发布
			if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
				userMessageManager.sendSystemMessage(
						MessageContent.get("news.auditing", bean.getTitle(),this.getLoginUserName(request))
						.setBody(this.newsDataManager.getBody(bean.getId()).getContent(), bean.getDataFormat(),bean.getCreateDate()),
						ApplicationCategoryEnum.news, 
						this.getLoginUserId(request), 
						MessageReceiver.get(bean.getId(), resultIds,"message.link.news.assessor.audit", 
								String.valueOf(bean.getId()),new Timestamp(System.currentTimeMillis()) + ""), 
						bean.getType().getId());
				//消息日志
			}
		} // for循环结束
		// 在此加上全文检索操作 TODO 在此添加权限相关的信息
		String gotoid = request.getParameter("gotoid");
		if ("gotoid".equals(gotoid)) {
			String showAudit = request.getParameter("showAudit");
			return this.redirectModelAndView("/newsData.do?method=listMain&showAudit="+ showAudit+ "&type="+ type.getId()+ "&spaceType=" + type.getSpaceType() + "&spaceId=" + spaceId + "&custom=" + custom);
		}

		//添加一个标志,表示是从消息框进入审核页面
		String info=request.getParameter("info");
		if("0".equalsIgnoreCase(info)) {
			super.rendJavaScript(response, "window.close()");
			return null;
		}

		return this.redirectModelAndView("/newsData.do?method=publishListMain&newsTypeId=" + type.getId() + "&spaceType=" + type.getSpaceType() + "&spaceId=" + spaceId + "&custom=" + custom);
	}


	public ModelAndView auditListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/audit/data_list_mainEntry");
		User user=CurrentUser.get();
		String from = request.getParameter("from");
		if (from != null) {
			mav.addObject("from", from);
		}

		//拿到新闻的类型,在这里面只是做个标志
		//做个防护--板块被删除
		String boardId = request.getParameter("type");
		NewsType type = null;
		if (boardId != null && !boardId.equals("")) {
			Long typeId = Long.valueOf(boardId);
			type = this.newsTypeManager.getById(typeId);
			if (!type.isUsedFlag()) {// 被删除是false
				super.rendJavaScript(response, "alert('板块已被管理员删除！');self.history.back();");
				return null;
			}
		}
		mav.addObject("newsTypeId", boardId);
		
		String close = request.getParameter("hiddenId");
		if ("hiddenId".equals(close)) 
			super.rendJavaScript(response, "window.close()");

		String spaceId = request.getParameter("spaceId");
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)) 
			spaceTypeInt = Integer.valueOf(spaceTypeStr);

		mav.addObject("spaceType", spaceTypeInt);
		//屏蔽掉页签中的"(*项待审)"字样 2009-11-28
		//int pending = this.newsDataManager.getPendingCountOfUser(user.getId(), spaceTypeInt);
		//mav.addObject("pending", pending);

		List<NewsType> typeList = null;
		if (spaceTypeInt == Constants.NewsTypeSpaceType.corporation.ordinal()) {
			typeList = newsDataManager.getTypeList(user.getId(), true, user.getLoginAccount());
		} else if (spaceTypeInt == Constants.NewsTypeSpaceType.public_custom.ordinal()
				|| spaceTypeInt == Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
			typeList = newsDataManager.getTypeList(user.getId(), spaceTypeInt, Long.parseLong(spaceId));
		} else if (spaceTypeInt == Constants.NewsTypeSpaceType.group.ordinal()) {
			typeList = newsDataManager.getManagerGroupBulType(CurrentUser.get().getId(), true);
		}
		mav.addObject("noManagerLabel",	(typeList == null || typeList.size() == 0));
		return mav;
	}

	public ModelAndView mgrListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/manager/data_list_mainEntry");
		String from = request.getParameter("from");
		String typeId = request.getParameter("type");
		mav.addObject("typeId", typeId);

		if (from != null) {
			mav.addObject("from", from);
		}

		String close = request.getParameter("hiddenId");
		if ("hiddenId".equals(close)) {
			super.rendJavaScript(response, "window.close()");
		}
		return mav;
	}

	public ModelAndView mgrEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/manager/data_list_main");
		String typeId = request.getParameter("typeId");
		mav.addObject("typeId", typeId);
		return mav;
	}
	
	public ModelAndView auditIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("news/audit/data_list_index");
	}
	
	public ModelAndView entry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/audit/data_list_main");
		return mav;
	}
	
	/**
	 * 新闻审核员列表页面
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView auditList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("news/audit/data_list_iframe");
		String spaceTypeStr = request.getParameter("spaceType");
		String showAudit = request.getParameter("showAudit");
		String spaceId = request.getParameter("spaceId");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)) {
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		mav.addObject("showAudit", showAudit);
		mav.addObject("spaceType", spaceTypeInt);
		boolean ngroup = (spaceTypeInt != Constants.NewsTypeSpaceType.group.ordinal());

		List<NewsData> list = null;
		List<NewsType> typeList = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		if (ngroup) {
			if (Strings.isNotBlank(spaceId)) {
				typeList = this.newsTypeManager.getCustomAuditUnitNewsTypeNoPaging(user.getId(), Long.parseLong(spaceId), spaceTypeInt);
				mav.addObject("publicCustom", true);
				mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
			} else {
				typeList = this.newsTypeManager.getAuditUnitNewsTypeNoPaging(user.getId());
			}
		} else
			typeList = this.newsTypeManager.getAuditGroupNewsTypeNoPaging(user.getId());

		try
		{
			if(user == null || (!user.isInternal()))
			{
				list=Collections.EMPTY_LIST;
			}else
			{
				if (StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)) {
					Object value = BulletinUtils.getPropertyObject(BulData.class, condition, textfield);
					list = newsDataManager.getAuditDataListNew(user.getId(), condition, value, spaceTypeInt);
				} else {
					list = newsDataManager.getAuditDataListNew(user.getId(), null, null, spaceTypeInt);
				}
			}
		} catch (BusinessException e)
		{
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		mav.addObject("list", list);
		mav.addObject("typeList", typeList);

		return mav;
	}

	/**
	 * 新闻审核员审核新闻操作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditOper(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String idStr = request.getParameter("id");
		if (StringUtils.isBlank(idStr)) {
			throw new NewsException("news_not_exists");
		}
		NewsData bean = newsDataManager.getById(Long.valueOf(idStr));
		String close = request.getParameter("hiddenId");   //标识上列表下详图页面或打开新窗口页面的情况
		//处理审核两次的情况,只有是未审核的状态才允许审核操作
		if (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_CREATE) {
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, "news.audit.already") + "');" +
								("hiddenId".equalsIgnoreCase(close) ? "parent.getA8Top().close();parent.getA8Top().opener.contentFrame.mainFrame.location.reload(true);" : "getA8Top().parent.getA8Top().reFlesh();"));
			return null;
		}
		String form_oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(form_oper))
		{
			if (form_oper.equals("audit")) {
				//审核通过,但是没有发布,用记点的是通过
				bean.setState(Constants.DATA_STATE_ALREADY_AUDIT);
			} else if (form_oper.equals("publish")) {
				//直接发布,用户点的是直接发布
				bean.setPublishDate(new Date());
				bean.setPublishUserId(CurrentUser.get().getId());
				bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
				bean.setTopOrder(Byte.parseByte("1"));
				bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PUBLISH));
			} else if (form_oper.equals("noaudit")) {
				//审核不通过
				bean.setState(Constants.DATA_STATE_NOPASS_AUDIT);
			} else if (form_oper.equals("cancelaudit")) {
				//用户点取消按钮了
				bean.setState(Constants.DATA_STATE_ALREADY_CREATE);
			}
		} else {
			//尚末提交,也就是暂存
			bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		}

		NewsType type = this.newsDataManager.getNewsTypeManager().getById(bean.getTypeId());
		
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
		
		Long agentId = AgentUtil.getAgentByApp(type.getAuditUser(), ApplicationCategoryEnum.news.getKey());
		Long senderId = user.getId();
		String senderName = user.getName();
		int proxyType = 0;
		if(agentId != null && agentId.equals(senderId)){
			proxyType = 1;
			senderId = type.getAuditUser();
			senderName = type.getAuditUserName();
		}

		// 发送审合通过消息消息
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_AUDIT)) {
			userMessageManager.sendSystemMessage(MessageContent.get(
					"news.alreadyauditing", bean.getTitle(), senderName, proxyType, user.getName()),
					ApplicationCategoryEnum.news, senderId,
					MessageReceiver.get(bean.getId(), bean.getCreateUser(), "message.link.news.writedetail", String.valueOf(bean.getId())), bean.getTypeId());

			// 审合新闻通过加日志
			appLogManager.insertLog(user, AppLogAction.News_AuditPass, senderName, bean.getTitle());
		}
		// 发送审合没有通过消息消息
		if (bean.getState().equals(Constants.DATA_STATE_NOPASS_AUDIT))
		{
			userMessageManager.sendSystemMessage(MessageContent.get(
					"news.not.alreadyauditing", bean.getTitle(), senderName, proxyType, user.getName()),
					ApplicationCategoryEnum.news, senderId,
					MessageReceiver.get(bean.getId(), bean.getCreateUser(), "message.link.news.writedetail", String.valueOf(bean.getId())), bean.getTypeId());

			// 审合新闻没有通过加日志
			appLogManager.insertLog(user, AppLogAction.News_AduitNotPass, senderName, bean.getTitle());
		}
		// 审核员直接发送发送消息
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH))
		{
			Long publishDeptId = bean.getPublishDepartmentId();
			V3xOrgDepartment publishDept = orgManager.getDepartmentById(publishDeptId);
			Long publishAccountId = publishDept.getOrgAccountId();
			
			List<Long> resultIds = new ArrayList<Long>();
			List<V3xOrgMember> listMemberId = ((BaseNewsManager) this.newsDataManager)
					.getNewsUtils().getScopeMembers(bean.getType().getSpaceType(), publishAccountId, bean.getType().getOutterPermit());
			for (V3xOrgMember member : listMemberId) {
				resultIds.add(member.getId());
			}
			V3xOrgMember vom = orgManager.getMemberById(bean.getCreateUser());
			//设置系统消息中的内容
			userMessageManager.sendSystemMessage(
					MessageContent.get("news.auditing", bean.getTitle(),
					vom.getName()).setBody(this.newsDataManager.getBody(bean.getId()).getContent(),bean.getDataFormat(), bean.getCreateDate()),					
					ApplicationCategoryEnum.news, bean.getCreateUser(),
					MessageReceiver.get(bean.getId(), resultIds, "message.link.news.assessor.auditing",
					//0表示用户从系统消息,网页等途径进行读取新闻(除在归档页面)
					String.valueOf(bean.getId())), bean.getTypeId());

			// 这里加入全文检索
			try {
				IndexEnable indexEnable = (IndexEnable) newsDataManager;
				IndexInfo info = indexEnable.getIndexInfo(bean.getId());
				indexManager.index(info);
			} catch (Exception e) {
				log.error("全文检索: ", e);
			}

			// 直接发布加日志
			appLogManager.insertLog(user, AppLogAction.News_AuditPublish, senderName, bean.getTitle());

		}
		this.newsDataManager.update(bean.getId(), summ);

		// 删除待办
		affairManager.deleteByObject(ApplicationCategoryEnum.news, bean.getId());
		//对新闻文件进行解锁
		if(idStr!=null&&!"".equalsIgnoreCase(idStr)) 
			newsDataManager.unlock(Long.valueOf(idStr));
		if ("hiddenId".equals(close)) 
			super.rendJavaScript(response, "parent.closeAndRefresh();");
		else 
			super.rendJavaScript(response, "parent.parent.getA8Top().contentFrame.mainFrame.location.href=parent.parent.getA8Top().contentFrame.mainFrame.location;");
		return null;
	}

	/**
	 * 显示新闻审核页面
	 */
	public ModelAndView audit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isBlank(idStr)) {
			NewsException e = new NewsException("news_not_exists");
			request.getSession().setAttribute("_my_exception", e);
			return new ModelAndView("news/error");
		}
		String needBreak = request.getParameter("needBreak");
		NewsData bean = newsDataManager.getById(Long.valueOf(idStr));
		if(bean==null || bean.isDeletedFlag()) {
			if (bean != null) {
				affairManager.deleteByObject(ApplicationCategoryEnum.news, bean.getId());
			}
			String jsAction = "alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, "news.data.deleted") + "');";
			if(!"true".equals(needBreak))
				jsAction += "window.opener.getA8Top().reFlesh();window.close();";
			else 
				jsAction += "parent.getA8Top().reFlesh();";
			super.rendJavaScript(response, jsAction);
			return null;
		}
		
		//检验文件中否加锁
		String action=NewsDataLockAction.NEWLOCK_AUDITING;
		NewsDataLock newslock=newsDataManager.lock(Long.valueOf(idStr),action);
		if(newslock!=null) {
			String lockaction=newslock.getAction();
			V3xOrgMember orm=orgManager.getMemberById(newslock.getUserid());
			//可以取得相关的锁对象
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, lockaction, orm.getName()) + "')");
			if("true".equalsIgnoreCase(needBreak)) {
				out.println("  parent.getA8Top().reFlesh();");
				out.println("</script>");
				out.flush();
				return null;
			}
			out.println("window.top.close();");
			out.println("</script>");
			out.flush();
			return null;
		}
		ModelAndView mav = new ModelAndView("news/audit/data_frameset");
		String description=request.getParameter("description");
		if("left".equals(description)){
			if (needBreak != null && "true".equals(needBreak)){
				mav = new ModelAndView("news/audit/data_audit_detail");
			}else{
			 mav = new ModelAndView("news/audit/data_audit");
			}
		}else if("right".equals(description)){
			if (needBreak != null && "true".equals(needBreak)){
				mav = new ModelAndView("news/audit/data_diagram_detail");
			}else{
				mav = new ModelAndView("news/audit/data_diagram");
			}
		}
		mav.addObject("id", idStr);
		mav.addObject("needBreak", needBreak);
		//这是什么时候用呢??
		mav.addObject("dataExist", true);
		if(Strings.isNotBlank(spaceId)){
			mav.addObject("publicCustom", true);
			mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		}
		bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
		mav.addObject("bean", bean);
		List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);
		mav.addObject("spaceType", bean.getType().getSpaceType());
		return mav;
	}

	/**
	 * 新闻审核员列表主页面的详细页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		NewsData bean = null;
		ModelAndView mav = new ModelAndView("news/audit/data_list_detail_iframe");
		if (StringUtils.isBlank(idStr)) {
			bean = new NewsData();
		} else {
			bean = newsDataManager.getById(Long.valueOf(idStr));
			// 判断如果删除了在查看。
			if (bean == null) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('管理员已经取消该新闻的发布!')");
				out.println("parent.window.location.reload();");
				out.println("</script>");
				return null;
			} else if (bean.isDeletedFlag() == true) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('管理员已经取消该新闻的发布!')");
				out.println("parent.window.location.reload();");
				out.println("</script>");
				return null;
			}
			bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}
		mav.addObject("bean", bean);
		mav.addObject("spaceType", bean.getType().getSpaceType());
		return mav;
	}

	/**
	 * 取消审核
	 */
	public ModelAndView cancelAudit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ids = request.getParameter("ids");
		if (Strings.isBlank(ids))
			return null;
		String[] idArr = ids.split(",");
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		
		for (String sid : idArr) {
			long dataId = Long.valueOf(sid);
			NewsData bean = newsDataManager.getById(dataId);
			if (bean == null)
				continue;
			NewsType beanType = bean.getType();
			if (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_AUDIT)
				continue;
			bean.setAuditAdvice(null);
			bean.setState(Constants.DATA_STATE_ALREADY_CREATE);
			this.addPendingAffair(beanType, bean);			
			
			//取消审核发消息
			List<Long> auth = new ArrayList<Long>();
			auth.add(bean.getCreateUser());
			Collection<MessageReceiver> receivers = MessageReceiver.get(bean.getId(), auth);
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("news.cancel.audit", bean.getTitle(), userName), ApplicationCategoryEnum.news, userId, receivers);
			} catch (MessageException e) {
				logger.error("新闻取消审核发消息失败", e);
			}

			//取消审核加日志
			appLogManager.insertLog(user, AppLogAction.News_CancelAudit, userName, bean.getTitle());
		}
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.parent.parent.location.reload(true);");
		out.println("</script>");

		return null;
	}
	public ModelAndView focus(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String idStr = StringUtils.defaultIfEmpty(request.getParameter("id"), "");
		String oper = request.getParameter("oper");
		if (StringUtils.isNotBlank(idStr)) {
			List<Long> ids = CommonTools.parseStr2Ids(idStr);
			if (CollectionUtils.isNotEmpty(ids)) {
				for (Long id : ids) {
					NewsData newsData = newsDataManager.getById(id);
					if (Strings.isBlank(oper)) {
						newsData.setFocusNews(true);
						newsDataManager.updateDirect(newsData);
					} else{
						newsData.setFocusNews(false);
						newsDataManager.updateDirect(newsData);
					}
				}
			}
		}
		return this.listMain(request,response);
	}
	/**
	 * 新闻发起员保存新闻的操作	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dataformat = request.getParameter("dataFormat");		
		User user = CurrentUser.get();
		String userName = user.getName();
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		NewsData bean = null;
		String idStr = request.getParameter("id");
		int oldState = Constants.DATA_STATE_NO_SUBMIT;
		Long oldImageId = 1L;
		if (StringUtils.isBlank(idStr)) {
			bean = new NewsData();
		} else {
			//从缓存里面取新闻
			bean = this.dataCache.get(Long.valueOf(idStr));
			if (bean == null) {
				bean = newsDataManager.getById(Long.valueOf(idStr));
			}
			// 为下面发消息做判断---修改的是为审核，和审核不通过的，发不同的消息
			if (bean.getState() == Constants.DATA_STATE_ALREADY_CREATE) {
				oldState = Constants.DATA_STATE_ALREADY_CREATE;
			} else if (bean.getState() == Constants.DATA_STATE_NOPASS_AUDIT) {
				oldState = Constants.DATA_STATE_NOPASS_AUDIT;
			} else if (bean.getState() == Constants.DATA_STATE_ALREADY_PUBLISH) {
				oldState = Constants.DATA_STATE_ALREADY_PUBLISH;
			}
			oldImageId = bean.getImageId();
		}
		super.bind(request, bean);
		
		NewsType oldType = bean.getType();
		boolean toDeleteAffair = false;
		if(oldType!=null)
			toDeleteAffair = oldType.isAuditFlag();
				
		Long typeId = bean.getTypeId();
		NewsType type = this.newsDataManager.getNewsTypeManager().getById(typeId);
		bean.setType(type);

		if (bean.getKeywords() != null && bean.getKeywords().trim().equals(""))
			bean.setKeywords(null);
		if (bean.getBrief() != null && bean.getBrief().trim().equals(""))
			bean.setBrief(null);

		String publishDepartmentId = request.getParameter("publishDepartmentId");
		if (publishDepartmentId != null) {
			bean.setPublishDepartmentId(Long.valueOf(publishDepartmentId));
		}

		if (bean.getState() != null)
			bean.getState().intValue();
		bean.setReadCount(0);
		if (bean.getPublishScope() == null)
			bean.setPublishScope("");

		String form_oper = request.getParameter("form_oper");
		if (StringUtils.isNotBlank(form_oper)) {
			if (form_oper.equals("draft"))
				bean.setState(Constants.DATA_STATE_NO_SUBMIT);
			else if (form_oper.equals("submit")) {
				if (type.isAuditFlag()) {
					bean.setState(Constants.DATA_STATE_ALREADY_CREATE);
				} else {
					// 改为直接发布
					bean.setExt3(String.valueOf(String.valueOf(Constants.AUDIT_RECORD_NO)));
					bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
					bean.setPublishDate(new Date());
					bean.setPublishUserId(CurrentUser.get().getId());
				}
			}
		} else {
			bean.setState(Constants.DATA_STATE_NO_SUBMIT);
		}
		Boolean firstInitFlag = false;
		if (bean.isNew()) {
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setTopOrder(new Byte("0"));
			firstInitFlag = true;
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		bean.setTopOrder(type.getTopCount());
		bean.setDataFormat(dataformat);
		
		/**-------------图片新闻-----------------**/
		boolean imageNews = Strings.isNotBlank(request.getParameter("imageNews"));
	//	boolean focusNews = Strings.isNotBlank(request.getParameter("focusNews"));
		bean.setImageNews(imageNews);
	//	bean.setFocusNews(focusNews);
		if (imageNews) {
			String imageIdStr = request.getParameter("imageId");
			Long imageId = NumberUtils.toLong(imageIdStr, 1L);
			if (imageId != oldImageId) {
				bean.setImageId(imageId);
			}
		} else {
			bean.setImageId(null);
		}
		/**--------------------------------------**/
		
		String attaFlag = null;
		// 已经提交审核的如果修改，需要发送消息
		boolean editAfterAudit = (oldState == Constants.DATA_STATE_ALREADY_CREATE);
		// 审核没有通过的修改
		boolean noAuditEdit = (oldState == Constants.DATA_STATE_NOPASS_AUDIT);
		// 不需要审核的板块发起者可直接修改已发布的新闻, 不需要管理员先撤消
		boolean noAuditPublishEdit = (oldState == Constants.DATA_STATE_ALREADY_PUBLISH);

		boolean isNew = false;
		if (bean.isNew()) {
			//在保存的时候对新闻的格式进行处理
			isNew = true;
			bean.setIdIfNew();
			long attRefId=Long.valueOf(request.getParameter("attRefId"));
			attachmentManager.deleteByReference(attRefId, attRefId);
		} else {
			attachmentManager.deleteByReference(bean.getId(), bean.getId());
		}

		bean.setAttachmentsFlag(false);
		List<Attachment> attList = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.news, bean.getId(), bean.getId(), request);
		if(!imageNews){
			for(Attachment att : attList){
				//非图片格式的新闻 不保存栏目图片
				if(att.getType()==5){
					attList.remove(att);
					break;
				}
			}
		}
		attaFlag = attachmentManager.create(attList);
		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)) {
			bean.setAttachmentsFlag(true);
		}
		if (Strings.isNotBlank(spaceId) && bean.getAccountId() == null) {
			bean.setAccountId(Long.parseLong(spaceId));
			newsDataManager.saveCustomNews(bean, isNew);
		} else {
			newsDataManager.save(bean, isNew);
		}

		// 发送要审合公告消息消息
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_CREATE)) {
			Long agentId = AgentUtil.getAgentByApp(type.getAuditUser(), ApplicationCategoryEnum.news.getKey());

			if (editAfterAudit) {
				// 提交后修改
				userMessageManager.sendSystemMessage(MessageContent.get(
						"news.edit", bean.getTitle(), userName),
						ApplicationCategoryEnum.news, user.getId(), 
						MessageReceiver.get(bean.getId(), type.getAuditUser(), "message.link.news.auditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get(
							"news.edit", bean.getTitle(), userName).add("col.agent"),
							ApplicationCategoryEnum.news, user.getId(), 
							MessageReceiver.get(bean.getId(), agentId, "message.link.news.auditing", String.valueOf(bean.getId())), type.getId());
				}

				// 待审核如果修改加日志
				appLogManager.insertLog(user, AppLogAction.News_Modify, userName, bean.getTitle());
				//审核员未进行审核前修改公告并再次发送，需另行增加一条待办事项记录，先删除旧有记录，再生成新的记录 added by Meng Yang at 2009-07-15
				this.affairManager.deleteByObject(ApplicationCategoryEnum.news, bean.getId());
				this.addPendingAffair(type, bean);
			} else if (noAuditEdit) {
				userMessageManager.sendSystemMessage(MessageContent.get(
						"news.send", bean.getTitle(), userName), ApplicationCategoryEnum.news, user.getId(), 
						MessageReceiver.get(bean.getId(), type.getAuditUser(), "message.link.news.auditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get(
							"news.send", bean.getTitle(), userName).add("col.agent"), ApplicationCategoryEnum.news, user.getId(), 
							MessageReceiver.get(bean.getId(), agentId, "message.link.news.auditing", String.valueOf(bean.getId())), type.getId());
				}

				//审核不通过修改后在发布审核加日志
				appLogManager.insertLog(user, AppLogAction.News_Modify, userName, bean.getTitle());
				
				//审核不通过后修改，再次发送给审核员进行审核，也需要增加一条对应的待办事项 added by Meng Yang at 2009-07-14
				this.addPendingAffair(type, bean);
			} else {
				// 新建提交
				// 2007.12.12 加入审核员的待办事项
				this.addPendingAffair(type, bean);			

				userMessageManager.sendSystemMessage(MessageContent.get("news.send", bean.getTitle(), userName),
						ApplicationCategoryEnum.news, user.getId(), MessageReceiver
								.get(bean.getId(), type.getAuditUser(), "message.link.news.alreadyauditing", String.valueOf(bean.getId())), type.getId());
				
				if(agentId != null){//给代理人发消息,后缀(代理)
					userMessageManager.sendSystemMessage(MessageContent.get("news.send", bean.getTitle(), userName).add("col.agent"),
							ApplicationCategoryEnum.news, user.getId(), MessageReceiver
									.get(bean.getId(), agentId, "message.link.news.alreadyauditing", String.valueOf(bean.getId())), type.getId());
				}

				//发布审核加日志<新建新闻>
				appLogManager.insertLog(user, AppLogAction.News_New, userName, bean.getTitle());
			}
		} else if(bean.getState().equals(Constants.DATA_STATE_NO_SUBMIT)) {
			//发布审核加日志<修改新闻>
			appLogManager.insertLog(user, AppLogAction.News_Modify, userName, bean.getTitle());
		}

		// 直接发送不审合消息
		if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
			Set<Long> resultIds = new HashSet<Long>();
			List<V3xOrgMember> listMemberId = new ArrayList<V3xOrgMember>();
			if("true".equals(custom)) {
				listMemberId = spaceManager.getSpaceMemberBySecurity(typeId, -1);
			}else if (Strings.isNotBlank(spaceId)) {
				listMemberId = spaceManager.getSpaceMemberBySecurity(Long.parseLong(spaceId), -1);
			}else {
				listMemberId = ((BaseNewsManager) this.newsDataManager)
						.getNewsUtils().getScopeMembers(type.getSpaceType(), user.getLoginAccount(),
								type.getOutterPermit());
			}
			for (V3xOrgMember member : listMemberId) {
				resultIds.add(member.getId());
			}
			userMessageManager.sendSystemMessage(MessageContent.get(
					noAuditPublishEdit ? "news.publishEdit" : "news.auditing", bean.getTitle(), userName).setBody(bean.getContent(),
					bean.getDataFormat(), bean.getCreateDate()),
					ApplicationCategoryEnum.news, this.getLoginUserId(request),
					MessageReceiver.get(bean.getId(), resultIds, "message.link.news.assessor.auditing", String.valueOf(bean.getId())), bean.getTypeId());
			this.createHtml(bean);

			// 直接发布加日志
			appLogManager.insertLog(user, noAuditPublishEdit ? AppLogAction.News_Modify : AppLogAction.News_Publish, userName, bean.getTitle());
			
			//如果是从有审核的板块切换到无审核的板块，需要删除之前的待办事项
			if(toDeleteAffair) 
				this.affairManager.deleteByObject(ApplicationCategoryEnum.news, bean.getId());
		}

		// 这里加入全文检索
		try {
			if (bean.getState().equals(Constants.DATA_STATE_ALREADY_PUBLISH)) {
				if (firstInitFlag) {
					IndexEnable indexEnable = (IndexEnable) newsDataManager;
					IndexInfo info = indexEnable.getIndexInfo(bean.getId());
					indexManager.index(info);
				} else {
					// 更新在此进行
					updateIndexManager.update(bean.getId(), ApplicationCategoryEnum.news.getKey());
				}
			}
		} catch (Exception e) {
			log.error("全文检索: ", e);
		}
		
		//对新闻文件进行解锁
		if(!"".equalsIgnoreCase(idStr)&&idStr!=null) {
			newsDataManager.unlock(Long.valueOf(idStr));
		}
		return this.redirectModelAndView("/newsData.do?method=publishListIndex&spaceType=" + bean.getType().getSpaceType()+"&spaceId=" + spaceId + "&newsTypeId=" + bean.getTypeId()+"&custom="+custom);
	}
	
	/**
	 * 为以下几种情况增加待办事项：
	 * 1.新建新闻，发送待审核，增加一条对应的待办事项记录；
	 * 2.已发送的新闻，未审核之前修改，再行发送，增加一条待审核记录，同时删除修改之前已有的待办事项记录；
	 * 3.已审核且不通过的新闻，修改后再次发送待审核，增加一条对应的待办事项记录。
	 * 抽取成为单独方法，便于单点维护 by Meng Yang at 2009-07-15
	 * @param beanType  新闻板块
	 * @param bean		新闻
	 * @throws BusinessException 
	 * @throws BusinessException 
	 */
	public void addPendingAffair(NewsType beanType, NewsData bean) throws BusinessException {
	
		Affair affair = new Affair();
		affair.setIdIfNew();
		affair.setIsTrack(false);
		affair.setIsDelete(false);
		// 利用 subjectId 存储空间类型，将来用于进入不同的页面
		affair.setSubObjectId(Long.valueOf(beanType.getSpaceType().toString()));
		affair.setMemberId(beanType.getAuditUser());
		affair.setState(StateEnum.col_pending.key());
		affair.setSubState(SubStateEnum.col_normal.key());
		affair.setSenderId(bean.getCreateUser());
		affair.setSubject(bean.getTitle());
		affair.setObjectId(bean.getId());
		affair.setApp(ApplicationCategoryEnum.news.key());
		affair.setCreateDate(new Timestamp(bean.getCreateDate().getTime()));
		V3xOrgMember member = orgManager.getMemberById(bean.getCreateUser());
		if (member != null)
			affair.setSender(member);
		affair.setIsSendMessage(false);
		affair.setHasAttachments(bean.getAttachmentsFlag());	
		
		affair.addExtProperty("spaceType", beanType.getSpaceType());
		affair.addExtProperty("spaceId", beanType.getAccountId());
		affair.addExtProperty("typeId", beanType.getId());
		affair.serialExtProperties();
		
		affairManager.addAffair(affair);
	}
	/**
	 * 得到登录用户id
	 * 
	 * @param request
	 * @return
	 */
	private Long getLoginUserId(HttpServletRequest request) {
		User user = CurrentUser.get();
		long currentUserId = user.getId();
		return Long.valueOf(currentUserId);
	}

	/**
	 * 得到登录用户名
	 * 
	 * @param request
	 * @return
	 */
	private String getLoginUserName(HttpServletRequest request) {
		User user = CurrentUser.get();
		String name = user.getName();
		return name;
	}

	/**
	 * 新闻管理员删除新闻，支持批量删除。<b>只做标记，并不实际删除</b> 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		User user = CurrentUser.get();
		String userName = user.getName();
		
		if (StringUtils.isNotBlank(idStr)) {
			String[] idStrs = idStr.split(",");
			List<Long> ids = new ArrayList<Long>();
			for (String str : idStrs) {
				if (StringUtils.isNotBlank(str)) {
					ids.add(Long.valueOf(str));
					NewsData bean = this.dataCache.get(Long.valueOf(str));
					if (bean == null)
						bean = newsDataManager.getById(Long.valueOf(str));
					//删除新闻加日志
					bean.setDeletedFlag(true);
					bean.setTopOrder(Byte.parseByte("0"));
					appLogManager.insertLog(user, AppLogAction.News_Delete, userName, bean.getTitle());
					this.dataCache.remove(Long.valueOf(str));
				}
			}
			if (ids.size() > 0) {
				newsDataManager.deletes(ids);
				// 在此从全文检索库中删除
				try {
					for (Long id : ids) {
						indexManager.deleteFromIndex(ApplicationCategoryEnum.news, id);
					}
				} catch (Exception e) {
					log.error("全文检索：", e);
				}
			}
		}

		return this.listMain(request, response);
	}

	/**
	 * 新闻管理员列表主页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/manager/data_list_main");
		return mav;
	}

	/**
	 * 新闻管理员列表页面
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("type");
		String showAudit = request.getParameter("showAudit");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		ModelAndView mav = new ModelAndView("news/manager/data_list_iframe");
		int spaceTypeInt = NumberUtils.toInt(request.getParameter("spaceType"), Constants.NewsTypeSpaceType.corporation.ordinal());
		mav.addObject("showAudit", showAudit);
		mav.addObject("spaceType", spaceTypeInt);
		NewsType type = null;
		Long typeId = null;
		User user= CurrentUser.get();
		if (Strings.isBlank(typeIdStr)) {
			List<NewsType> typeList = null;
			if (spaceTypeInt == Constants.NewsTypeSpaceType.corporation.ordinal()) {
				typeList = newsDataManager.getTypeList(user.getId(), true,user.getLoginAccount());
			} else if (spaceTypeInt == Constants.NewsTypeSpaceType.group.ordinal()) {
				typeList = newsDataManager.getManagerGroupBulType(user.getId(), true);
			} else if (spaceTypeInt == Constants.NewsTypeSpaceType.public_custom.ordinal()) {
				typeList = newsDataManager.getTypeList(user.getId(), spaceTypeInt, Long.parseLong(spaceId));
			}
			if(typeList!=null && typeList.size()>0) {
				typeId = typeList.get(0).getId();
				type = typeList.get(0);
			}
		} else {
			typeId = Long.valueOf(typeIdStr);
			type = this.newsTypeManager.getById(typeId);
			//板块之间互相切换时，是否显示审核页签应该由对应板块自身决定
			boolean isAuditor = false;
			if(type.isAuditFlag() && type.getAuditUser().longValue()==user.getId())
				isAuditor = true;
			mav.addObject("showAudit", isAuditor);
		}

		if (typeIdStr != null && !typeIdStr.equals("")) {
			initOperate(request, typeIdStr);
		}

		List<NewsData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		//判断一下当前用户是否是管理员,是的话可以浏览
		boolean isAdmin=false;
		boolean isSpaceManager = false;
		if ("true".equals(custom)) {
			isSpaceManager = spaceManager.isManagerOfThisSpace(user.getId(), typeId);
			mav.addObject("custom", custom);
		} else {
			String[] str = type.getManagerUserIds().split(",");
			for (String string : str) {
				if(string.equalsIgnoreCase(String.valueOf(user.getId()))) {
					isAdmin=true;
					break;
				}
			}
		}
		if(isAdmin || isSpaceManager) {
			try {
				if(user == null || (!user.isInternal())) {
					list=Collections.EMPTY_LIST;
				}else {
					//对搜索条件进行判断
					if (StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)) {
						list = newsDataManager.findByProperty(typeId, condition, textfield, user.getId());
					} else if("true".equals(custom)) {
						list = newsDataManager.findByReadUser(user.getId(), typeId, user.getLoginAccount());
					} else {
						list = newsDataManager.findAll(typeId,user.getId());
					}
				}
			} catch (BusinessException e) {
				mav = new ModelAndView("bulletin/error");
				request.getSession().setAttribute("_my_exception", e);
				return mav;
			}
		}

		mav.addObject("list", list);
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);

		mav.addObject("newsTypeId", typeId);
		mav.addObject("theType", type);

		// 授权
		List<NewsTypeManagers> listW = this.newsDataManager.getNewsTypeManager().findTypeWriters(type);
		List<V3xOrgEntity> auList = new ArrayList<V3xOrgEntity>();
		if (listW != null && listW.size()>0) {
			StringBuffer strbuf = new StringBuffer();
			for (NewsTypeManagers managers : listW) {
				strbuf.append(managers.getExt2() + "|" + managers.getManagerId() + ",");
			}
			auList = orgManager.getEntities(strbuf.substring(0, strbuf.length() - 1));
			mav.addObject("managerId", auList);
		}

		boolean ngroup = (spaceTypeInt != Constants.NewsTypeSpaceType.group.ordinal());

		// 类型列表
		List<NewsType> typeList = null;
		if(isAdmin) {
			if (ngroup) {
				if (Strings.isNotBlank(spaceId)) {
					typeList = newsDataManager.getTypeList(user.getId(), spaceTypeInt, Long.parseLong(spaceId));
					mav.addObject("publicCustom", true);
					mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
				} else {
					typeList = newsDataManager.getTypeList(user.getId(), true);
				}
			} else {
				typeList = newsDataManager.getManagerGroupBulType(user.getId(), true);
			}
			if(CollectionUtils.isNotEmpty(typeList))
				typeList.remove(type);
			
			mav.addObject("typeList", typeList);
        }
		return mav;
	}

	/**
	 * 新闻管理员列表主页面的详细页面
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		NewsData bean = null;
		ModelAndView mav = new ModelAndView("news/manager/data_list_detail_iframe");

		if (StringUtils.isBlank(idStr)) {
			bean = new NewsData();
		} else {
			bean = newsDataManager.getById(Long.valueOf(idStr));
			if(bean==null || bean.isDeletedFlag() || bean.getState().equals(Constants.DATA_STATE_ALREADY_PIGEONHOLE)) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, "news.data.noexist") + "');" +
											   "parent.getA8Top().reFlesh();");
				return null;
			}
			bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
			List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}
		mav.addObject("bean", bean);
		return mav;
	}

	/**
	 * 所有用户阅读新闻的列表页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView userList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("type");
		if (typeIdStr != null)
			initOperate(request, typeIdStr);

		User user= CurrentUser.get();

		List<NewsData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		Long typeId = (Long) request.getSession().getAttribute("news.typeId");

		ModelAndView mav;
		if (typeId != null)
			mav = new ModelAndView("news/user/data_list_all");
		else
			mav = new ModelAndView("news/user/data_list_iframe");

		List<NewsType> typeList = null;

		boolean isGroup = false;

		//String groupSign = (String) request.getSession().getAttribute("news.groupSign");// 获取集团空间标志 “0”

		String sgroup = request.getParameter("group");
		boolean ngroup = (sgroup == null || "".equals(sgroup));

		if (!ngroup)
		{
			typeList = this.newsDataManager.getGroupAllTypeList();
			isGroup = true;
			mav.addObject("group", "group");
		} else
		{
			typeList = this.newsDataManager.getAllTypeList(user.getLoginAccount());
		}
		mav.addObject("typeList", typeList);

		if (typeId != null)
		{
			for (NewsType t : typeList)
			{
				if (t.getId().longValue() == typeId.longValue())
				{
					mav.addObject("theType", t);
					break;
				}
			}
		}

		try
		{
			if (StringUtils.isNotBlank(condition)
					&& StringUtils.isNotBlank(textfield))
			{
				Object value = NewsUtils.getPropertyObject(NewsData.class,
						condition, textfield);
				if (typeId != null)
					list = newsDataManager.findByReadUser(user.getId(), typeId,
							condition, value);
				else
					list = newsDataManager.findByReadUser(user.getId(), condition,
							value, typeList,user.getLoginAccount());
			} else
			{
				if (typeId != null)
					list = newsDataManager.findByReadUser(user.getId(), typeId,user.getLoginAccount());
				else
					list = newsDataManager.findByReadUser(user.getId(), typeList,user.getLoginAccount(),null);
			}
		} catch (BusinessException e)
		{
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}
		mav.addObject("list", list);
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);
		mav.addObject("isGroup", isGroup);
		return mav;
	}

	/**
	 * 为所有用户点击板块显示这个板块的所有的列表页面的方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView moreUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user= CurrentUser.get();
		List<NewsData> list = null;
		Long typeId = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		String typeID = request.getParameter("type");
		if (typeID != null && typeID.length() > 0)
		{
			typeId = Long.parseLong(typeID);
		}

		try
		{
			if (typeId != null)
			{
				// 第一次进入
				list = newsDataManager.findByReadUser(user.getId(), typeId,user.getLoginAccount());
			} else
			{
				// 查询
				typeId = Long.valueOf(request.getParameter("typeId"));

				if (StringUtils.isNotBlank(condition)
						&& StringUtils.isNotBlank(textfield))
				{
					list = newsDataManager.findByReadUser(user.getId(), typeId,
							condition, textfield);
				} else
				{
					list = newsDataManager.findByReadUser(user.getId(), typeId,user.getLoginAccount());
				}

			}
		} catch (BusinessException e)
		{
			ModelAndView mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		ModelAndView mav;
		mav = new ModelAndView("news/user/data_list_all");
		mav.addObject("typeId", typeId);
		mav.addObject("list", list);
		return mav;

	}

	/**
	 * 用户阅读新闻，增加新闻阅读次数，设置该用户对该新闻的阅读情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView userViewOld(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String idStr = request.getParameter("id");
		if (StringUtils.isBlank(idStr))
		{
			NewsException e = new NewsException("news_not_exists");
			request.getSession().setAttribute("_my_exception", e);
			return new ModelAndView("news/error");
		}
		NewsData bean = newsDataManager.getById(Long.valueOf(idStr),
				CurrentUser.get().getId());

		ModelAndView mav = new ModelAndView("news/user/data_view");
		if (bean == null
				|| bean.isDeletedFlag()
				|| (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_PUBLISH && bean
						.getState().intValue() != Constants.DATA_STATE_ALREADY_PIGEONHOLE))
		{
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('"
					+ ResourceBundleUtil.getString(
							Constants.NEWS_RESOURCE_BASENAME,
							"news.data.noexist") + "');");
			out.println("window.close();");
			out.println("</script>");
			return null;
		} else
			mav.addObject("dataExist", true);
		bean.setContent(newsDataManager.getBody(bean.getId()).getContent());

		// 增加阅读次数
		int readCount = bean.getReadCount() == null ? 0 : bean.getReadCount()
				.intValue();
		bean.setReadCount(readCount + 1);
		this.newsDataManager.updateDirect(bean);

		mav.addObject("bean", bean);
		if (bean.getAttachmentsFlag())
		{
			List<Attachment> attachments = attachmentManager.getByReference(
					bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		} else
		{
			mav.addObject("attachments", new ArrayList<Attachment>());
		}

		return mav;
	}

	private final Object readCountLock = new Object();
	
	/**
	 * 阅读新闻
	 */
	public ModelAndView userView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		if (StringUtils.isBlank(idStr)) {
			NewsException e = new NewsException("news_not_exists");
			request.getSession().setAttribute("_my_exception", e);
			return new ModelAndView("news/error");
		}
		long dataId = Long.valueOf(idStr);
		NewsData bean = this.dataCache.get(dataId);
		boolean hasCache = false;
		if (bean == null) {
			bean = newsDataManager.getById(Long.valueOf(idStr), userId);
		} else {			
			hasCache = true;
		}
		
		String fromPigeonhole = request.getParameter("fromPigeonhole");
		ModelAndView mav = new ModelAndView("news/user/data_view");
		if(Strings.isNotBlank(spaceId)){
			mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
		}
		boolean flag = false;
		if(bean == null || bean.isDeletedFlag()
				|| (bean.getState().intValue() != Constants.DATA_STATE_ALREADY_PUBLISH && bean.getState().intValue() != Constants.DATA_STATE_ALREADY_PIGEONHOLE)
				|| (bean.getState().intValue() == Constants.DATA_STATE_ALREADY_PIGEONHOLE && !"true".equals(fromPigeonhole))){

			flag = true;
		}else{
			NewsType type = bean.getType();
			if (type == null || !type.isUsedFlag()){
				flag = true;
			}
		}
		// 防护：已发布的欣闻被删除或被管理员归档，其他用户点击系统消息链接时候给出提示<归档的新闻，在文档中心处可以正常查看>
		if (flag)
		{
			String from = request.getParameter("from");
			String jsAction = "list".equalsIgnoreCase(from) ? "parent.getA8Top().reFlesh();" : 
															  "if(top.opener) {" +
															  "	top.opener.getA8Top().reFlesh();window.close();" +
															  "}";
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME,
							"news.data.noexist") + "');" + jsAction);
			return null;
		} else
			mav.addObject("dataExist", true);
		
		if (hasCache) {
			//设置阅读状态，以便已阅和未读状态标识能被获取 added by Meng Yang 2009-05-20
			this.newsReadManager.setReadState(bean, userId); 
			this.clickCache(dataId, userId);
		} else {	
			//设置阅读状态，以便已阅和未读状态标识能被获取 added by Meng Yang 2009-05-20
			this.newsReadManager.setReadState(bean, userId); 
			
			// 增加阅读次数，添加同步防护
			int readCount = 0;
			synchronized(readCountLock){
				readCount = bean.getReadCount() == null ? 0 : bean.getReadCount().intValue();
				bean.setReadCount(readCount + 1);
			}
			this.newsDataManager.updateDirect(bean);
			bean.setContent(newsDataManager.getBody(bean.getId()).getContent());

			// 保存到缓存
//			this.dataCache.save(dataId, bean, bean.getPublishDate().getTime(),
//					(bean.getReadCount() == null ? 0 : bean.getReadCount()));
			this.syncCache(bean, (bean.getReadCount() == null ? 0 : bean.getReadCount()));
		}
		V3XFile files = fileManager.getV3XFile(bean.getImageId());
		String formatDate = "";
		if (files != null) {
			formatDate = Datetimes.formatDate(files.getCreateDate());
		}
		mav.addObject("imageDate", formatDate);
		mav.addObject("bean", bean);
		
		if (bean.getAttachmentsFlag())
		{
			List<Attachment> attachments = attachmentManager.getByReference(
					bean.getId());
			mav.addObject("attachments", attachments);
		} else
		{
			mav.addObject("attachments", new ArrayList<Attachment>());
		}

		return mav;
	}
	
	public void clickCache(Long dataId,Long userId) {
		this.dataCache.click(dataId, new ClickDetail(userId, new Timestamp(System.currentTimeMillis())));
		NewsData bean = this.dataCache.get(dataId);
		if(bean==null) return; 
		synchronized(readCountLock){
			bean.setReadCount(this.dataCache.getClickTotal(dataId));
		}		
		//发送消息
		NotificationManager.getInstance().send(NotificationType.NewsClickArticle, new CacheInfo(dataId, userId));
	}
	
	public void syncCache(NewsData bean, int clickCount) {
		this.dataCache.save(bean.getId(), bean, bean.getPublishDate().getTime(), clickCount);
		//发送消息
		NotificationManager.getInstance().send(NotificationType.NewsModifyArticle, new CacheInfo(bean.getId(), clickCount));
	}

	/**
	 * 以html形式查看
	 */
	public ModelAndView userViewHtml(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String idStr = request.getParameter("id");
		User user = CurrentUser.get();
		// response.setContentType("text/html; charset=UTF-8");
		String cLan = LocaleContext.getLocale(request).getLanguage();
		PrintWriter out = response.getWriter();
		out.println("<script type=\"text/javascript\">");
		out.println("var cuser = '" + user.getId() + "';");
		out.println("var clan = '" + cLan + "';");
		out.println("</script>");

		request.getRequestDispatcher(
				"/USER-DATA/html/news/" + this.getNewsHtmlFileName(idStr)
						+ ".html").include(request, response);

		return null;
	}

	// 生成html格式新闻
	private void createHtml(NewsData bean)
	{
		Map<String, String> data = new HashMap<String, String>();
		data.put("title", bean.getTitle());
		data.put("publishDate", bean.getPublishDate().toString().substring(0,
				"yyyy-mm-dd hh:mi:ss".length()));
		data.put("publishDepartmentName", bean.getPublishDepartmentName());
		data.put("publishUserId", bean.getPublishUserId().toString());
		// data.put("readCount", (bean.getReadCount() == null ? "0" :
		// bean.getReadCount().toString()));
		data.put("content", bean.getContent());
		data.put("brief", this.getBriefHtml(bean.getBrief()));
		data.put("keywords", this.getKeywordHtml(bean.getKeywords()));
		if (!bean.getAttachmentsFlag())
			data.put("atts", "");
		else
		{
			List<Attachment> attList = attachmentManager.getByReference(bean
					.getId(), bean.getId());
			data.put("atts", this.getAttHtml(bean.getId(), attList));
		}
		data.put("aclInfo", this.getAclInfoHtml(bean));
		// this.newsHTMLConvertManager.saveHTML(data,
		// this.getNewsHtmlFileName(bean.getId().toString()));
	}

	private String getNewsHtmlFileName(String idStr)
	{
		return idStr;
	}

	private String getBriefHtml(String brief)
	{
		if (Strings.isBlank(brief))
			return "";
		StringBuffer sb = new StringBuffer(
				"<tr><td colspan=\"3\" class=\"paddingLR\" height=\"80\" valign=\"top\">");
		sb
				.append("<div id=\"newsSummary\" class=\"font-12px\"><b>摘要:</b>&nbsp;&nbsp;");
		sb.append(brief);
		sb.append("</div></td></tr>");

		return sb.toString();
	}

	private String getKeywordHtml(String keyword)
	{
		if (Strings.isBlank(keyword))
			return "";
		StringBuffer sb = new StringBuffer(
				"<tr><td colspan=\"3\" class=\"paddingLR\" height=\"22\" valign=\"top\">");
		sb
				.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		sb
				.append("<tr><td width=\"50\" nowrap=\"nowrap\" class=\"font-12px\">关键字:&nbsp;</td><td width=\"100%\" class=\"font-12px\">");
		sb.append(keyword);
		sb.append("</td></tr></table></td></tr>");

		return sb.toString();

	}

	private String getAttHtml(Long dataId, List<Attachment> attList)
	{
		if (attList == null || attList.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer(
				"<tr id=\"attachmentTr\" style=\"display: none\">");
		sb.append("<td colspan=\"3\" class=\"paddingLR\" height=\"30\">");
		sb
				.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		sb
				.append("<tr><td width=\"50\" nowrap=\"nowrap\" class=\"font-12px\"><b>附件</b>:&nbsp;</td>");
		sb.append("<td width=\"100%\" class=\"font-12px\"><script>");
		sb.append("var theToShowAttachments = new ArrayList();");
		sb
				.append("var downloadURL = \"/portal/_ns:YTd8YzB8ZDB8ZV9zcGFnZT0xPS9maWxlVXBsb2FkLmRv/seeyon/default-page.psml\";");

		for (Attachment att : attList)
		{
			sb.append("theToShowAttachments.add(new Attachment(");
			sb.append("'" + att.getId() + "',");
			sb.append("'" + att.getReference() + "',");
			sb.append("'" + att.getSubReference() + "',");
			sb.append("'" + att.getCategory() + "',");
			sb.append("'" + att.getType() + "',");
			sb.append("'" + Strings.escapeJavascript(att.getFilename()) + "',");
			sb.append("'" + att.getMimeType() + "',");
			sb.append("'" + Datetimes.formatDatetime(att.getCreatedate())
					+ "',");
			sb.append("'" + att.getSize() + "',");
			sb.append("'" + att.getFileUrl() + "',");
			sb.append("'" + (att.getGenesisId()!=null?att.getGenesisId():"") + "',");
			sb.append("null,");
			sb.append("'" + att.getExtension() + "',");
			sb.append("'" + att.getIcon() + "'");
			sb.append("));");
		}

		sb.append("</script><div style=\"display:none;\">");
		sb
				.append("<iframe name=\"downloadFileFrame\" id=\"downloadFileFrame\" frameborder=\"0\" width=\"0\" height=\"0\"></iframe></div>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("showAttachment('" + dataId.toString()
				+ "', 0, 'attachmentTr', '');");
		sb.append("</script></td></tr></table></td></tr>");

		return sb.toString();
	}

	private String getAclInfoHtml(NewsData data)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("var createUserId = '" + data.getCreateUser().toString()
				+ "';");
		sb.append("var dataId = '" + data.getId().toString() + "';");
		// sb.append("</script>");
		return sb.toString();
	}

	/**
	 * 新闻发起员列表主页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView writeListMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		ModelAndView mav = new ModelAndView("news/write/data_list_main");
		return mav;
	}

	/**
	 * 新闻发起员列表页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView writeList(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		List<NewsData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		try
		{
			if (StringUtils.isNotBlank(condition)
					&& StringUtils.isNotBlank(textfield))
			{
				Object value = NewsUtils.getPropertyObject(NewsData.class,
						condition, textfield);
				list = newsDataManager.findWriteByProperty(condition, value);
			} else
			{
				list = newsDataManager.findWriteAll();
			}
		} catch (BusinessException e)
		{
			ModelAndView mav = new ModelAndView("news/error");
			mav.addObject("exception", e);
			return mav;
		}

		ModelAndView mav = new ModelAndView("news/write/data_list_iframe");
		mav.addObject("list", list);
		mav.addObject("typeList", this.newsDataManager.getTypeList(CurrentUser
				.get().getId(), true));

		return mav;
	}

	/**
	 * 新闻发起员列表主页面的详细页面
	 */
	public ModelAndView writeDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String idStr = request.getParameter("id");
		NewsData bean = null;

		ModelAndView mav = new ModelAndView("news/write/data_list_detail_iframe");

		if (StringUtils.isBlank(idStr))
			bean = new NewsData();
		else {
			bean = newsDataManager.getById(Long.valueOf(idStr));
			if(bean==null || !bean.getType().isUsedFlag() || bean.isDeletedFlag() || bean.getState().equals(Constants.DATA_STATE_ALREADY_PIGEONHOLE)) {
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, "news.data.noexist") + "');" +
											   "parent.getA8Top().reFlesh();");
				return null;
			} else
				mav.addObject("existFlag", true);
			bean.setContent(newsDataManager.getBody(bean.getId()).getContent());
			List<Attachment> attachments = attachmentManager.getByReference(
					bean.getId(), bean.getId());
			mav.addObject("attachments", attachments);
		}
		V3XFile files = fileManager.getV3XFile(bean.getImageId());
		String formatDate = "";
		if (files != null) {
			formatDate = Datetimes.formatDate(files.getCreateDate());
		}
		mav.addObject("imageDate", formatDate);
		mav.addObject("bean", bean);
		mav.addObject("adviceRead", true);
		return mav;
	}

	/**
	 * 新闻发起员删除新闻操作。由于此时新闻还没有被审核，没有发布，因此是<b>实际删除</b>
	 */
	public ModelAndView writeDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String spaceId = request.getParameter("spaceId");
		NewsData bean = null;
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();

		if (StringUtils.isBlank(idStr)) {
			idStr = "";
		} else {
			String[] ids = idStr.split(",");
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					bean = newsDataManager.getById(Long.valueOf(id));
					try {
						// 已经提交审核的需要发送消息，删除待办
						if (bean == null)
							continue;
						if (bean.getState().intValue() == Constants.DATA_STATE_ALREADY_CREATE) {
							affairManager.deleteByObject(
									ApplicationCategoryEnum.news, bean.getId());

							userMessageManager.sendSystemMessage(MessageContent
									.get("news.delete", bean.getTitle(), userName), ApplicationCategoryEnum.news, userId,
									MessageReceiver.get(bean.getId(), bean.getType().getAuditUser()), bean.getTypeId());

						}
						try{
							//删除全文检索信息
							if(bean.getState().intValue() == Constants.DATA_STATE_ALREADY_PUBLISH)
								indexManager.deleteFromIndex(ApplicationCategoryEnum.news, bean.getId());
						}catch(Exception e){
							log.error(e);
						}
						appLogManager.insertLog(user, AppLogAction.News_Delete, userName, bean.getTitle());
						
						this.newsDataManager.deleteReal(bean.getId());
						//从缓存中清理被真实删除的新闻，以免二者出现不同步的现象
						this.dataCache.remove(Long.valueOf(id));
					} catch (BusinessException e) {
						request.getSession().setAttribute("_my_exception", e);
						break;
					}
				}
			}
		}
		String spaceType = request.getParameter("spaceType");
		String bulTypeId = request.getParameter("newsTypeId");
		String custom = request.getParameter("custom");
		return this.redirectModelAndView("/newsData.do?method=publishListMain&newsTypeId="
						+ bulTypeId + "&spaceType=" + spaceType + "&spaceId=" + spaceId + "&custom=" + custom);

	}

	/**
	 * 新闻管理员查看新闻操作日志，记录了新闻管理员删除、归档新闻的操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView logList(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		NewsLog log = new NewsLog();
		log.setUserId(CurrentUser.get().getId());
		List<NewsLog> logList = this.newsDataManager.getNewsLogManager()
				.findByExample(log);
		ModelAndView mav = new ModelAndView("news/manager/log_list_iframe");
		mav.addObject("list", logList);
		return mav;
	}

	/**
	 * 新闻管理员配置新闻发起员 新闻管理员可以为自己管理的新闻类型分别配置每种类型的新闻发起者
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView configWrite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String act = request.getParameter("act");
		User user = CurrentUser.get();
		Long userId = user.getId();
		String userName = user.getName();
		Long typeIdTo = (Long) request.getSession().getAttribute("news.typeId");
		NewsType newType = this.newsDataManager.getNewsTypeManager().getById(typeIdTo);
		ModelAndView mav;
		int spaceType = Constants.NewsTypeSpaceType.corporation.ordinal();
		// view：查看配置情况、save：保存配置
		if (act == null || act.equals("view")) {
			mav = new ModelAndView("news/manager/config_write");
			List<NewsTypeManagers> list = this.newsDataManager.getNewsTypeManager().findTypeWriters(newType);
			if (CollectionUtils.isNotEmpty(list)) {
				StringBuffer strbuf = new StringBuffer();
				for (NewsTypeManagers managers : list) {
					strbuf.append(managers.getManagerId() + ",");
				}
				String managerId = strbuf.substring(0, strbuf.length() - 1);
				mav.addObject("managerId", managerId);
			}
			List<NewsType> type = new ArrayList<NewsType>();

			type.add(newType);
			mav.addObject("list", type);
			mav.addObject("spaceType", spaceType);
			return mav;
		} else {
			Long typeId = Long.valueOf(request.getParameter("typeId"));
			String value = request.getParameter("userIds");

			String typeName = newType.getTypeName();
			if (Strings.isBlank(value)) {
				this.newsDataManager.getNewsTypeManager().saveWriteByType(typeId, new String[0][0]);
				//对整个操作过程记录应用日志
				appLogManager.insertLog(user, AppLogAction.News_PostAuth_Update, userName, typeName);
			} else {
				String[][] authInfoArray = Strings.getSelectPeopleElements(value);
				// 构造消息接收者
				List<Long> auth = new ArrayList<Long>();
				Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(value);
				for (V3xOrgMember entity : membersSet) {
					if (!userId.equals(entity.getId())) {
						auth.add(entity.getId());
					}
				}

				// 消息过滤
				List<NewsTypeManagers> old = this.newsTypeManager.findTypeWriters(newType);
				if (CollectionUtils.isNotEmpty(old)) {
					for (NewsTypeManagers ntm : old) {
						auth.remove(ntm.getManagerId());
					}
				}
				
				userMessageManager.sendSystemMessage(MessageContent.get("news.accredit", newType.getTypeName(), userName),
						ApplicationCategoryEnum.news, userId, MessageReceiver.get(newType.getId(), auth, "", String.valueOf(typeId)), newType.getId());
				this.newsDataManager.getNewsTypeManager().saveWriteByType(typeId, authInfoArray);
				
				//对整个操作记录应用日志
				appLogManager.insertLog(user, AppLogAction.News_PostAuth_Update, userName, typeName);
				
			}
			mav = new ModelAndView("news/result");
			return mav;
		}
	}

	/**
	 * 新闻管理员查看新闻统计情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public ModelAndView statistics(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String type = request.getParameter("type");
		String sbulTypeId = request.getParameter("newsTypeId");
		List list = new ArrayList();
		if (sbulTypeId != null && sbulTypeId.length() > 0)
		{
			long bulTypeId = Long.valueOf(sbulTypeId);
			list = newsDataManager.statistics(type, bulTypeId);
		}

		ModelAndView mav;
		mav = new ModelAndView("news/manager/statistics");
		mav.addObject("list", list);
		mav.addObject("type", type);
		mav.addObject("newsTypeId", sbulTypeId);

		return mav;
	}

	/**
	 * 新闻管理员查看统计框架
	 * 
	 * @author lucx
	 * 
	 */
	public ModelAndView statisticsIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String type = request.getParameter("type");
		String groupSign = (String) request.getSession().getAttribute(
				"news.groupSign");// 集团标志
		String bulTypeId = request.getParameter("newsTypeId");

		ModelAndView mav;
		mav = new ModelAndView("news/manager/statisticsIframe");
		mav.addObject("groupSign", groupSign);
		mav.addObject("type", type);
		mav.addObject("newsTypeId", bulTypeId);

		return mav;
	}

	/**
	 * 新闻归档
	 * 
	 * @author lucx
	 * 
	 */
	public ModelAndView pigeonhole(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
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
					if(res != null) {
						String folderName = docHierarchyManager.getNameById(res.getParentFrId());
						appLogManager.insertLog(user, AppLogAction.News_Pigeonhole, userName, res.getFrName(), folderName);
					}
					idList.add(Long.valueOf(idA[i]));
					// 更新缓存中新闻的状态为归档,防止通过系统提示信息查看归档后的新闻
					Long longID = Long.valueOf(idA[i]);
					NewsData bean = this.dataCache.get(longID);
					if(bean!=null) {
						bean.setState(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
						this.dataCache.save(longID, bean, bean.getPublishDate().getTime(), (bean.getReadCount() == null ? 0 : bean.getReadCount()));						
					}
				}
			}
			this.newsDataManager.pigeonhole(idList);

		}

		return super.refreshWorkspace();
	}

	/**
	 * 显示管理员可管理的版块
	 */
	public ModelAndView listBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("news/manager/list_board");
		String spaceTypeStr = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)){
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		mav.addObject("spaceType", spaceTypeInt);
		List<NewsType> typeList = null;
		boolean isShowAudit = false;
		boolean isShowBoard = false;// 判断是否有管理板块的权限
		if (spaceTypeInt == Constants.NewsTypeSpaceType.corporation.ordinal()){
			typeList = newsDataManager.getTypeList(userId, true);
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !newsTypeManager.getAuditUnitNewsTypeNoPaging(userId).isEmpty();
		} else if (spaceTypeInt == Constants.NewsTypeSpaceType.public_custom.ordinal()
				|| spaceTypeInt == Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
			typeList = newsDataManager.getTypeList(userId, spaceTypeInt, Long.parseLong(spaceId));
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !newsTypeManager.getCustomAuditUnitNewsTypeNoPaging(userId, Long.parseLong(spaceId), spaceTypeInt).isEmpty();
		} else if (spaceTypeInt == Constants.NewsTypeSpaceType.group.ordinal()) {
			typeList = newsDataManager.getManagerGroupBulType(userId, true);
			isShowBoard = !typeList.isEmpty();
			isShowAudit = !newsTypeManager.getAuditGroupNewsTypeNoPaging(userId).isEmpty();
		}
		mav.addObject("typeList", typeList);
		mav.addObject("showBoard", isShowBoard);
		mav.addObject("showAudit", isShowAudit);
		return mav;
	}

	/**
	 * 管理首页
	 */
	public ModelAndView listBoardIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("news/manager/list_board_index");
		// 做个防护--板块被删除
		String boardId = request.getParameter("newsTypeId");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		NewsType type = null;
		if (boardId != null && !boardId.equals("")) {
			Long typeId = Long.valueOf(boardId);
			type = this.newsTypeManager.getById(typeId);
			if (!type.isUsedFlag()) {// 被删除是false
				super.rendJavaScript(response, "alert('板块已被管理员删除！');self.history.back();");
				return null;
			}
		}
		ret.addObject("newsTypeId", boardId);
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.NewsTypeSpaceType.corporation.ordinal();
		if (Strings.isNotBlank(spaceTypeStr)){
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
		}
		ret.addObject("spaceType", spaceTypeInt);
		
		//在审核页面显示待审核的条数 <屏蔽掉页签中的"(*项待审)"字样 2009-11-28>
		//int pending = this.newsDataManager.getPendingCountOfUser(CurrentUser.get().getId(), spaceTypeInt);
		//ret.addObject("pending", pending);
		
		boolean ngroup = (spaceTypeInt != Constants.NewsTypeSpaceType.group.ordinal());
		boolean isShowAudit = false;
		boolean isShowBoard = false;
		// 类型列表
		List<NewsType> typeList = null;
		List<NewsType> auditList = null;
		User user = CurrentUser.get();
		if (ngroup) {
			if (Strings.isNotBlank(spaceId)) {
				typeList = newsDataManager.getTypeList(user.getId(), spaceTypeInt, Long.parseLong(spaceId));
				//显示不显示审核的选项卡
				auditList = newsTypeManager.getCustomAuditUnitNewsTypeNoPaging(user.getId(), Long.parseLong(spaceId), spaceTypeInt);
			} else {
				typeList = newsDataManager.getTypeList(user.getId(), true,user.getLoginAccount());
				//显示不显示审核的选项卡
				auditList = newsTypeManager.getAuditUnitNewsTypeNoPaging(user.getId());
			}
			isShowBoard = !typeList.isEmpty();
			isShowAudit = auditList.contains(type);
		} else {
			typeList = newsDataManager.getManagerGroupBulType(user.getId(), true);
			isShowBoard = !typeList.isEmpty();
			//显示不显示审核的选项卡
			List<NewsType> list=newsTypeManager.getAuditGroupNewsTypeNoPaging(user.getId());
			isShowAudit=list.contains(type);
		}
		ret.addObject("typeList", typeList);
		ret.addObject("showBoard", isShowBoard);
		ret.addObject("showAudit", isShowAudit);
		ret.addObject("custom", custom);
		return ret;
	}

	/**
	 * 集团板块列表
	 * 
	 * @author lucx
	 * 
	 */
	public ModelAndView listGroupBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		request.getSession().setAttribute("news.groupSign", "0");// 集团公告空间进入创建SESSION
		boolean isShowAudit = !newsTypeManager.getAuditGroupNewsTypeNoPaging(userId).isEmpty();
		request.getSession().setAttribute("news.isShowAudit", isShowAudit);

		ModelAndView mav;
		mav = new ModelAndView("news/manager/list_board");
		List<NewsType> typeList = newsDataManager.getManagerGroupBulType(userId, true); // 取得有我来管理的集团板块
		mav.addObject("typeList", typeList);
		mav.addObject("from", "Group");
		return mav;
	}
	
	public ModelAndView showAllTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/user/list_all_board");
		List<NewsType> typeList = null;
		User user = CurrentUser.get();
		String group = request.getParameter("group");
		if(Strings.isNotBlank(group)){
			typeList = newsDataManager.getGroupAllTypeList();
		} else {
			typeList = newsDataManager.getAllTypeList(user.getLoginAccount());
		}
		mav.addObject("typeList", typeList);
		return mav;
	}
	
	/**
	 * portal显示板块列表
	 */
	public ModelAndView showDesignated(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/user/showDesignated");
		User user = CurrentUser.get();
		List<NewsType> typeList = null;
		String group = request.getParameter("group");
		String textfield = request.getParameter("textfield");

		if (Strings.isNotBlank(group)) {
			typeList = newsDataManager.getGroupAllTypeList();
		} else {
			typeList = newsDataManager.getAllTypeList(user.getLoginAccount());
		}

		List<NewsType> resultList = new ArrayList<NewsType>();
		if (Strings.isNotBlank(textfield)) {
			for (NewsType type : typeList) {
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

	public void destoryOperate(HttpServletRequest request)
	{
		request.getSession().removeAttribute("news.typeId");
		request.getSession().removeAttribute("news.type");
		request.getSession().removeAttribute("news.isWriter");
		request.getSession().removeAttribute("news.isAuditer");
		request.getSession().removeAttribute("news.isManager");
		request.getSession().removeAttribute("news.isShowPublish");
		request.getSession().removeAttribute("news.isShowAudit");
		request.getSession().removeAttribute("news.isShowManage");
		request.getSession().removeAttribute("news.groupSign");

	}

	public void initOperate(HttpServletRequest request, String typeIdStr)
	{
		Long userId = CurrentUser.get().getId();
		if (typeIdStr == null || "".equals(typeIdStr))
			return;
		// 设置管理按钮
		Long typeId = Long.valueOf(typeIdStr);
		boolean isShowPublish = false;
		boolean isShowAudit = false;
		boolean isShowManage = false;
		boolean isWriter = false;
		boolean isAuditer = false;
		boolean isManager = false;
		// 发起员
		List<NewsType> typeList = this.newsDataManager.getTypeListByWrite(
				CurrentUser.get().getId(), true);
		for (NewsType type : typeList)
		{
			if (type.getId().longValue() == typeId.longValue())
			{
				isWriter = true;
				break;
			}
		}
		NewsType type = this.newsDataManager.getNewsTypeManager().getById(
				typeId);
		if (type.isAuditFlag()
				&& type.getAuditUser().longValue() == userId.longValue())
		{
			isAuditer = true;
		}

		String groupSign = (String) request.getSession().getAttribute(
				"news.groupSign");// 集团标志
		if (groupSign != null)
		{
			isShowAudit = !newsTypeManager
					.getAuditGroupNewsTypeNoPaging(userId).isEmpty();
		} else
		{
			isShowAudit = !newsTypeManager.getAuditUnitNewsTypeNoPaging(userId)
					.isEmpty();
		}

		// isShowAudit=this.newsDataManager.getNewsTypeManager().getNewsTypeDao().findBy("auditUser",
		// userId).size()>0;
		try
		{
			isShowManage = this.newsDataManager.getTypeList(userId, true)
					.size() > 0;
		} catch (Exception e)
		{
			log.error("", e);
		}

		if (type.getManagerUserIds().indexOf(userId.toString()) > -1)
		{
			isManager = true;
		}

		isShowPublish = isWriter || isManager;

		request.getSession().setAttribute("news.typeId", typeId);
		request.getSession().setAttribute("news.type", type);
		request.getSession().setAttribute("news.isWriter", isWriter);
		request.getSession().setAttribute("news.isAuditer", isAuditer);
		request.getSession().setAttribute("news.isManager", isManager);
		request.getSession().setAttribute("news.isShowPublish", isShowPublish);
		request.getSession().setAttribute("news.isShowAudit", isShowAudit);
		request.getSession().setAttribute("news.isShowManage", isShowManage);
	}

	// 更多
	public ModelAndView userListAll(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		destoryOperate(request);
		User user = CurrentUser.get();

		List<NewsData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		List<NewsType> typeList = newsDataManager.getAllTypeList(user.getLoginAccount());
		try
		{
			if (StringUtils.isNotBlank(condition)
					&& StringUtils.isNotBlank(textfield))
			{
				Object value = NewsUtils.getPropertyObject(NewsData.class,
						condition, textfield);
				list = newsDataManager.findByReadUser(user.getId(), condition, value,
						typeList,user.getLoginAccount());
			} else
			{
				list = newsDataManager.findByReadUser(user.getId(), typeList,user.getLoginAccount(), null);
			}
		} catch (BusinessException e)
		{
			ModelAndView mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		ModelAndView mav;
		mav = new ModelAndView("news/user/data_list_all");

		mav.addObject("typeList", typeList);

		mav.addObject("list", list);
		mav.addObject("more", request.getParameter("more"));
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);
		mav.addObject("unit", "unit");
		return mav;
	}

	// 集团更多
	public ModelAndView groupUserListAll(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		destoryOperate(request);
		Long userId = CurrentUser.get().getId();

		List<NewsData> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		List<NewsType> typeList = newsDataManager.getGroupAllTypeList();

		try
		{
			if (StringUtils.isNotBlank(condition)
					&& StringUtils.isNotBlank(textfield))
			{
				Object value = NewsUtils.getPropertyObject(NewsData.class,
						condition, textfield);
				list = newsDataManager.groupFindByReadUser(userId, condition,
						value, typeList);
			} else
			{
				list = newsDataManager.groupFindByReadUser(userId, typeList, null);
			}
		} catch (BusinessException e)
		{
			ModelAndView mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}

		ModelAndView mav;
		mav = new ModelAndView("news/user/data_list_all");

		// List<NewsType> typeList=newsTypeManager.getGroupNewsType();
		mav.addObject("typeList", typeList);
		mav.addObject("group", "group");
		mav.addObject("list", list);
		mav.addObject("more", request.getParameter("more"));
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);

		return mav;
	}

	/**
	 * 发布首页 最外围有边框的IFrame
	 */
	public ModelAndView publishListIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		ModelAndView mav = new ModelAndView(
				"news/write/data_list_publish_index");
		return mav;
	}

	// Edit By Lif Start
	public ModelAndView publishListMainEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		ModelAndView mav = new ModelAndView("news/write/data_list_publish_main");
		return mav;
	}

	public ModelAndView publishListMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		ModelAndView mav = new ModelAndView("news/write/data_list_publish_main");
		return mav;
	}

	//当前用户看到新闻模块的核心方法
	@SuppressWarnings("unchecked")
	public ModelAndView publishList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<NewsData> list = null;
		User user=CurrentUser.get();
		Long typeId = Long.valueOf(request.getParameter("newsTypeId"));
		String custom = request.getParameter("custom");
		NewsType type = null;
		// 判断新闻类型是否存在
		if (typeId != null) {
			type = this.newsTypeManager.getById(typeId);
		}
		//判断新闻类型是否启用
		if (type == null || !type.isUsedFlag()) {
			super.rendJavaScript(response, "alert('板块已被管理员删除！');self.history.back();");
			return null;
		}
		
		try {
			//如果是外部人员返回一个空的列表
			if(user == null || (!user.isInternal())) {
				list=Collections.EMPTY_LIST;	
			}else {
				//如果新闻类型存在的话,查出当前新闻类型下所有的新闻
				if (typeId != null)
					list = newsDataManager.findWriteAll(typeId,user.getId());
				else
					list = newsDataManager.findWriteAll();		
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}
		//列出当前用户能够看到的新闻
		ModelAndView mav = new ModelAndView("news/write/data_list_publish");
		if("true".equals(custom)) {
			mav.addObject("custom", custom);
		}
		if(Strings.isNotBlank(request.getParameter("spaceId"))){
			mav.addObject("publicCustom", true);
			mav.addObject("spaceName", spaceManager.getSpace(Long.parseLong(request.getParameter("spaceId"))).getSpaceName());
		}
		mav.addObject("list", list);
		mav.addObject("newsType", type);
		return mav;
	}

	public void setIndexManager(IndexManager indexManager)
	{
		this.indexManager = indexManager;
	}

	public AffairManager getAffairManager()
	{
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager)
	{
		this.affairManager = affairManager;
	}

	/**
	 * 新闻统一更多页面
	 */
	public ModelAndView newsMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		User user = CurrentUser.get();
		Long userId = user.getId();
		Long loginAccount = user.getLoginAccount();
		String custom = request.getParameter("custom");
		ModelAndView mav = new ModelAndView("news/user/news_more");
		String orgType = request.getParameter("orgType");
		String spaceType = request.getParameter("spaceType");
		String spaceIdS = request.getParameter("spaceId");
		String typeID = request.getParameter("typeId");
		boolean moreList = false;
		List<NewsData> list = null;
		
		Integer imageOrFocus = null;
		String imageOrFocusStr = request.getParameter("imageOrFocus");
		if (Strings.isNotBlank(imageOrFocusStr)) {
			imageOrFocus = Integer.valueOf(imageOrFocusStr);
			mav.addObject("imageOrFocus", imageOrFocus);
		}

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

		try {
			if (Strings.isNotBlank(typeID)) {
				Long typeId = Long.parseLong(typeID);
				NewsType type = this.newsTypeManager.getById(typeId);
				if (type == null || !type.isUsedFlag()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('板块已被管理员删除！')");
					out.println("self.history.back();");
					out.println("</script>");
					return null;
				}
				
				boolean spaceManagerFlag = false;
				if ("true".equals(custom)) {
					spaceManagerFlag = spaceManager.isManagerOfThisSpace(userId, typeId);
					mav.addObject("custom", custom);
					mav.addObject("spaceManagerFlag", spaceManagerFlag);
				}
				
				moreList = true;
				mav.addObject("newsTypeId", typeId);
				mav.addObject("moreList", moreList);
				mav.addObject("typeName", type.getTypeName());
				
				//外部人员判断
				if(user.isInternal() || (!user.isInternal() && type.getOutterPermit())){
					if (Strings.isNotBlank(spaceIdS)) {// 自定义单位、集团空间新闻
						Long spaceId = Long.parseLong(spaceIdS);
						list = newsDataManager.findByReadUser(userId, typeId, spaceId);
						mav.addObject("spaceName", spaceManager.getSpace(spaceId).getSpaceName());
						mav.addObject("publicCustom", true);
					} else {// 单位新闻、集团新闻
						list = newsDataManager.findByReadUser(userId, typeId, loginAccount);
					}
				} else {
					list = new ArrayList<NewsData>();
				}
				mav.addObject("typeId", typeId);
				if (Strings.isBlank(spaceType)) {
					spaceType = type.getSpaceType().toString();
				}
			} else {// 单位新闻
				if ("account".equals(orgType)) {
					List<NewsType> typeList = newsDataManager.getAllTypeList(loginAccount);
					if (CollectionUtils.isNotEmpty(selectTypeList)) {
						typeList = new ArrayList<NewsType>();
						for (Long id : selectTypeList) {
							NewsType type = newsTypeManager.getById(id);
							if (type != null) {
								typeList.add(newsTypeManager.getById(id));
							}
						}
					}
					list = newsDataManager.findByReadUser(userId, typeList, loginAccount, imageOrFocus);
					spaceType = Constants.NewsTypeSpaceType.corporation.ordinal() + "";
				} else if ("publicCustom".equals(orgType)) {// 自定义单位、集团空间新闻
					if (Strings.isNotBlank(spaceIdS)) {
						Long spaceId = Long.parseLong(spaceIdS);
						List<NewsType> typeList = newsDataManager.getAllTypeList(spaceId, "5".equals(spaceType) ? "publicCustom" : "publicCustomGroup");
						list = newsDataManager.findByReadUser(userId, typeList, spaceId, imageOrFocus);
						mav.addObject("spaceName", spaceManager.getSpace(spaceId).getSpaceName());
						mav.addObject("publicCustom", true);
					}
				} else if ("group".equals(orgType)) {// 集团新闻
					if (!user.isInternal()) {
						list = Collections.emptyList();
					} else {
						List<NewsType> typeList = newsDataManager.getGroupAllTypeList();
						if (CollectionUtils.isNotEmpty(selectTypeList)) {
							typeList = new ArrayList<NewsType>();
							for (Long id : selectTypeList) {
								NewsType type = newsTypeManager.getById(id);
								if (type != null) {
									typeList.add(newsTypeManager.getById(id));
								}
							}
						}
						list = newsDataManager.groupFindByReadUser(userId, typeList, imageOrFocus);
					}
					spaceType = Constants.NewsTypeSpaceType.group.ordinal() + "";
				}
			}
		} catch (BusinessException e) {
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}
		
		mav.addObject("orgType", orgType);
		mav.addObject("spaceType", spaceType);
		mav.addObject("list", list);
		return mav;
	}
	
	/**
	 * 更多页面查询
	 */
	public ModelAndView oneNewsSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		destoryOperate(request);
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("news/user/news_more");
		String orgType = request.getParameter("orgType");
		String spaceType = request.getParameter("spaceType");
		String custom = request.getParameter("custom");
		String typeID = request.getParameter("typeId");
		String newsTypeId = request.getParameter("newsTypeId");
		String spaceId = request.getParameter("spaceId");
		List<NewsType> typeList = null;
		List<NewsData> list = null;
		if (Strings.isNotBlank(newsTypeId)) {
			NewsType type = this.newsTypeManager.getById(Long.parseLong(newsTypeId));
			if (type == null || !type.isUsedFlag()) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('板块已被管理员删除！')");
				out.println("self.history.back();");
				out.println("</script>");
				return null;
			}
			typeList = new ArrayList<NewsType>();
			typeList.add(type);
			mav.addObject("typeId", type.getId());
			mav.addObject("newsTypeId", type.getId());
			mav.addObject("moreList", true);
			mav.addObject("typeName", type.getTypeName());
			//外部人员，没有该板块的权限
			if(!user.isInternal() && !type.getOutterPermit()){
				mav.addObject("spaceType", spaceType);
				mav.addObject("list", list);
				mav.addObject("orgType", orgType);
				return mav;
			}
		}
		
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

		// 设置查询的条件和字段
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		try {
			// 这是从单位新闻,集团新闻列表页面点击的
			if (Integer.valueOf(spaceType).intValue() == Constants.NewsTypeSpaceType.corporation.ordinal() || Integer.valueOf(spaceType).intValue() == Constants.NewsTypeSpaceType.custom.ordinal()) {
				if (CollectionUtils.isNotEmpty(selectTypeList)) {
					typeList = new ArrayList<NewsType>();
					for (Long id : selectTypeList) {
						NewsType type = newsTypeManager.getById(id);
						if (type != null) {
							typeList.add(newsTypeManager.getById(id));
						}
					}
				}
				list = newsDataManager.findByReadUser(user.getId(), condition, textfield, typeList, user.getLoginAccount());
			} else if (Integer.valueOf(spaceType).intValue() == Constants.NewsTypeSpaceType.public_custom.ordinal()) {
				list = newsDataManager.findByReadUser(user.getId(), condition, textfield, typeList, Long.parseLong(spaceId), "publicCustom");
			} else if (Integer.valueOf(spaceType).intValue() == Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
				list = newsDataManager.findByReadUser(user.getId(), condition, textfield, typeList, Long.parseLong(spaceId), "publicCustomGroup");
			} else {
				if (typeList == null || typeList.isEmpty()) {
					typeList = newsDataManager.getGroupAllTypeList();
					if (CollectionUtils.isNotEmpty(selectTypeList)) {
						typeList = new ArrayList<NewsType>();
						for (Long id : selectTypeList) {
							NewsType type = newsTypeManager.getById(id);
							if (type != null) {
								typeList.add(newsTypeManager.getById(id));
							}
						}
					}
				}
				list = newsDataManager.groupFindByReadUser(user.getId(), condition, textfield, typeList);
			}
			if (Strings.isBlank(spaceType) && Strings.isNotBlank(newsTypeId)) {
				Long typeId = Long.parseLong(newsTypeId);
				NewsType bt = this.newsTypeManager.getById(typeId);
				if (bt != null) {
					spaceType = bt.getSpaceType().toString();
				}
				mav.addObject("typeId", typeId);
			}
		} catch (BusinessException e) {
			mav = new ModelAndView("news/error");
			mav.addObject("_my_exception", e);
			return mav;
		}
		boolean spaceManagerFlag = false;
		if ("true".equals(custom)) {
			spaceManagerFlag = spaceManager.isManagerOfThisSpace(user.getId(), Long.parseLong(typeID));
			mav.addObject("custom", custom);
			mav.addObject("spaceManagerFlag", spaceManagerFlag);
		}
		mav.addObject("spaceType", spaceType);
		mav.addObject("list", list);
		mav.addObject("orgType", orgType);
		return mav;
	}

	public DataCache<NewsData> getDataCache() {
		return dataCache;
	}

	public void setDataCache(DataCache<NewsData> dataCache) {
		this.dataCache = dataCache;
	}

	/**
	 * 初始化缓存方法
	 */
	public void init() {
		DataCache<NewsData> dc = new DataCache<NewsData>(this.newsDataManager);
		this.dataCache = dc;
	}

	/**
	 * 发布列表浏览时候直接发布---目前功能去掉
	 * 
	 * @author lucx
	 * 
	 */
	public ModelAndView publishIt(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String idStr = request.getParameter("id");
		NewsData bean = newsDataManager.getById(Long.valueOf(idStr));
		NewsType type = bean.getType();

		bean.setState(Constants.DATA_STATE_ALREADY_PUBLISH);
		bean.setPublishDate(new Date());
		bean.setPublishUserId(CurrentUser.get().getId());

		bean.setReadCount(0);
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		//设置审核最终状态：审核通过
		bean.setExt3(String.valueOf(Constants.AUDIT_RECORD_PASS));
		this.newsDataManager.updateDirect(bean);

		// 这里加入全文检索
		try
		{
			IndexEnable indexEnable = (IndexEnable) newsDataManager;
			IndexInfo info = indexEnable.getIndexInfo(bean.getId());
			indexManager.index(info);
		} catch (Exception e)
		{
			log.error("全文检索失败", e);
		}

		List<Long> resultIds = new ArrayList<Long>();
		List<V3xOrgMember> listMemberId = ((BaseNewsManager) this.newsDataManager)
				.getNewsUtils().getScopeMembers(type.getSpaceType(),
						CurrentUser.get().getLoginAccount(),
						type.getOutterPermit());
		for (V3xOrgMember member : listMemberId)
		{
			resultIds.add(member.getId());
		}
		userMessageManager
				.sendSystemMessage(MessageContent.get("news.auditing",
						bean.getTitle(), this.getLoginUserName(request))
						.setBody(
								this.newsDataManager.getBody(bean.getId())
										.getContent(), bean.getDataFormat(),
								bean.getCreateDate()),
						ApplicationCategoryEnum.news, this
								.getLoginUserId(request), MessageReceiver.get(bean.getId(), resultIds, "message.link.news.assessor.auditing", String.valueOf(bean.getId())), bean.getType().getId());

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("  parent.close();");

		out.println("</script>");

		return null;
	}
	
	public ModelAndView uploadNewsImage(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String id = request.getParameter("id");
		String imageId = request.getParameter("imageId");
		String type = request.getParameter("type");
		
		NewsData newsData = newsDataManager.getById(NumberUtils.toLong(id));
		//newsData.setContent(newsDataManager.getBody(newsData.getId()).getContent());
		if(newsData != null){
			newsData.setImageId(NumberUtils.toLong(imageId));
			int imageOrFocus = NumberUtils.toInt(type);
			if(imageOrFocus == Constants.ImageNews){
				newsData.setImageNews(true);
			} else {
				newsData.setFocusNews(true);
			}
		}
		
		attachmentManager.deleteByReference(newsData.getId(), newsData.getId());
		attachmentManager.create(ApplicationCategoryEnum.news, newsData.getId(), newsData.getId(), request);
		
		newsDataManager.updateDirect(newsData);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + ResourceBundleUtil.getString(Constants.NEWS_RESOURCE_BASENAME, "news.upload.success") + "');");
		out.println("parent.window.location.reload();");
		out.println("</script>");
		return null;
	}
	/**
	 * 图片或焦点新闻更多页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView imageNewsMore (HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("news/user/image_news_more");
		User user = CurrentUser.get();
		String spaceId = request.getParameter("spaceId");
		int imageOrFocus = NumberUtils.toInt(request.getParameter("imageOrFocus"),0);
		int spaceType = NumberUtils.toInt(request.getParameter("spaceType"), Constants.NewsTypeSpaceType.corporation.ordinal());
		List<NewsData> newsDataList = new ArrayList<NewsData>();
		if (Strings.isNotBlank(spaceId)) {
			newsDataList = newsDataManager.findByReadUser4ImageNews(user.getId(), Long.parseLong(spaceId), user.isInternal(), imageOrFocus, spaceType);
		} else {
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
			if (selectTypeList != null) {
				newsDataList = newsDataManager.findByReadUser4ImageNews(user, imageOrFocus, spaceType, selectTypeList);
			} else {
				newsDataList = newsDataManager.findByReadUser4ImageNews(user.getId(), user.getLoginAccount(), user.isInternal(), imageOrFocus, spaceType);
			}
		}
		Long[] fileIds = new Long[newsDataList.size()];
		for(int i = 0; i < newsDataList.size(); i++){
			NewsData newsData = newsDataList.get(i);
			newsData.setContent(StrExtractor.getHTMLContent(newsDataManager.getBody(newsData.getId()).getContent()));
			Long imageId = newsData.getImageId();
			if(imageId != null){
				fileIds[i] = imageId; 
			}
		}
		
		Map<String,String> filesMap = new HashMap<String,String>();
		List<V3XFile> files = fileManager.getV3XFile(fileIds);
		if (files != null && files.size() > 0) {
			for(V3XFile file : files){
				filesMap.put(file.getId().toString(), Datetimes.formatDate(file.getCreateDate()));
			}
		}
		
		int size = Pagination.getRowCount();
		
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"),Pagination.getMaxResults());
		if (pageSize < 1) {
			pageSize = Pagination.getMaxResults();
		}
		
		int page = NumberUtils.toInt(request.getParameter("page"), 1);
		if(newsDataList != null){
			if (size == 0) {
				size = newsDataList.size();
			}
		}
		
		int pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}

		if (page < 1) {
			page = 1;
		}
		else if (page > pages) {
			page = pages;
		}
		mav.addObject("pageSize", pageSize);
		mav.addObject("page", page);
		mav.addObject("pages", pages);
		mav.addObject("size", size);
		mav.addObject("filesMap", filesMap);
		mav.addObject("newsDataList", newsDataList);
		mav.addObject("imageOrFocus", imageOrFocus);
		return mav;
	}
	
	public void setNewsReadManager(NewsReadManager newsReadManager) {
		this.newsReadManager = newsReadManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMtContentTemplateManagerCAP(MtContentTemplateManagerCAP mtContentTemplateManagerCAP) {
		this.mtContentTemplateManagerCAP = mtContentTemplateManagerCAP;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

}