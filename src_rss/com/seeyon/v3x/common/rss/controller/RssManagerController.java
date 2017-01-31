package com.seeyon.v3x.common.rss.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.common.rss.domain.RssChannelInfo;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.domain.RssSubscribe;
import com.seeyon.v3x.common.rss.manager.RssChannelManager;
import com.seeyon.v3x.common.rss.manager.RssManager;
import com.seeyon.v3x.common.rss.webmodel.ChannelInfoVo;
import com.seeyon.v3x.common.rss.webmodel.SubChannelVo;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class RssManagerController extends BaseController {
	
	private RssChannelManager rssChannelManager;
	private RssManager rssManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public RssChannelManager getRssChannelManager() {
		return rssChannelManager;
	}
	
	public void setRssChannelManager(RssChannelManager rssChannelManager) {
		this.rssChannelManager = rssChannelManager;
	}
	
	public RssManager getRssManager() {
		return rssManager;
	}
	
	public void setRssManager(RssManager rssManager) {
		this.rssManager = rssManager;
	}
	
	public OrgManager getOrgManager() {
		return orgManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
  
    public String getId() {
           return "rssManagerController";
     }

    protected String getName(Map<String, String> preference) {
            return "rssManagerController";
     }

	
	@Override
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/rssManageFrame");		
		
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);
		
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView rssToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/rssManagerToolBar");
		
		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		mav.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		
		// 当前是否集团管理员登录
		boolean isGroupAdmin = Constants.isGroupAdmin();
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);
		mav.addObject("isGroupAdmin", isGroupAdmin);
		
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView rssTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("rss/rssmgmt/rssManagerTree");
		//获取所有的类别	
		List<RssCategory> categories = rssChannelManager.getAllRssCategories();		
		modelView.addObject("categories", categories);				
		return modelView;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView rssChannelList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/rssChannelList");
		long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
		List<RssCategoryChannel> channels = null;
		if (categoryId != 0) {
			
			RssCategory category = rssChannelManager.getCategory(categoryId);
			
			channels = rssChannelManager.getChannelByCategoryIdByPage(categoryId);
			
			if(channels != null && channels.size() > 0)
				for(RssCategoryChannel rcc : channels){
					rcc.setCategory(category);
				}
		}
		else {			
			channels = rssChannelManager.getAllRssChannelsByPage();
			
			
			if(channels != null && channels.size() > 0)
				for(RssCategoryChannel rcc : channels){
					RssCategory category = rssChannelManager.getCategory(rcc.getCategoryId());
					rcc.setCategory(category);
				}
		}

		mav.addObject("categoryId", categoryId);
		mav.addObject("channels", channels);
		
		return mav;
	}
	
	
	// 进入新建RSS类别界面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView addCategoryView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/addRssCategory");					
		return mav;
	}
	
	// 创建RSS类别
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView createRssCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String name = request.getParameter("name");			//RSS类别名称				
		try {			
			rssChannelManager.addCategory(name);			
			out.print("<script>");
			out.print("parent.window.returnValue = \"" + true +"\";");
			out.print("window.close();");
			out.print("</script>");
		} 
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'));");
			out.print("</script>");
		}
		return null;
	}	
	
	// 进入编辑Rss类别界面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView editCategoryView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/editRssCategory");
		long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
		RssCategory category = rssChannelManager.getCategory(categoryId);
		mav.addObject("category", category);
		return mav;
	}
	
	// 修改RSS类别名称
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView updateRssCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);			
		String name = request.getParameter("name");			//RSS类别名称
		try {
			rssChannelManager.modifyCategory(categoryId, name);
			out.print("<script>");
			out.print("parent.window.returnValue = \"" + true +"\";");
			out.print("window.close();");
			out.print("</script>");
		}
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'));");
			out.print("</script>");
		}
		return null;
	}
	
	// 删除RSS类别
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView deleteRssCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
		List<RssCategoryChannel> channels = rssChannelManager.getChannelByCategoryId(categoryId);
		if (channels.size() > 0) {
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('RssLang.rss_category_alter_contain_channel'));");
			out.println("</script>");
			return null;
		}
		rssChannelManager.deleteCategory(Long.valueOf(categoryId));
		out.print("<script>");
		out.print("parent.parent.location.reload(true);");
		out.print("</script>");
		return null;		
	}
	
	
	// 进入新建RSS频道界面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView addChannelView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/addRssChannel");
		long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
		List<RssCategory> categories = rssChannelManager.getAllRssCategories();
		int orderNum = rssChannelManager.getMaxChannelOrder();
		mav.addObject("categories", categories);
		mav.addObject("categoryId", categoryId);		
		mav.addObject("orderNum", orderNum);
		
		mav.addObject("categoriesTotal", (categories == null ? 0 : categories.size()));
		
		return mav;
	}
	
	// 创建RSS频道
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView createRssChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		try {
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
			String url = request.getParameter("url");
			int orderNum = RequestUtils.getIntParameter(request, "orderNum", 0);
			if (orderNum == 0) {
				orderNum = rssChannelManager.getMaxChannelOrder();
			}			
			rssChannelManager.addRssChannel(url, name, orderNum, description, categoryId);
			User user = CurrentUser.get();
			appLogManager.insertLog(user, AppLogAction.RssChanelChange_Create,name);
			out.print("<script>");
			out.print("parent.parent.theTop.location.reload(true);");
			out.print("parent.parent.theBottom.location.href=\"/seeyon/common/detail.jsp\";");
			out.print("</script>");
		}
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'));");
			out.print("</script>");
		}
		return null;
	}
	
	// 进入编辑RSS频道界面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView editChannelView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/rssmgmt/editRssChannel");
		long channelId = RequestUtils.getLongParameter(request, "channelId", 0);
		RssCategoryChannel channel = rssChannelManager.getChannel(channelId);
		List<RssCategory> categories = rssChannelManager.getAllRssCategories();
		mav.addObject("channel", channel);
		mav.addObject("categories", categories);
		return mav;
	}
	
	/**
	 * 更新RSS频道
	 */
	@CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
	public ModelAndView updateRssChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		try {
			long channelId = RequestUtils.getLongParameter(request, "channelId", 0);
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			long categoryId = RequestUtils.getLongParameter(request, "categoryId", 0);
			String url = request.getParameter("url");
			int orderNum = RequestUtils.getIntParameter(request, "orderNum", 0);
			rssChannelManager.modifyRssChannel(channelId, name, url, orderNum, description, categoryId);
			User user = CurrentUser.get();
			appLogManager.insertLog(user, AppLogAction.RssChanelChange_Update, name);
			out.print("<script>");
			out.print("parent.parent.theTop.location.reload(true);");
			out.print("parent.parent.theBottom.location.href=\"/seeyon/common/detail.jsp\";");
			out.print("</script>");
		} catch (Exception e) {
			out.print("<script>");
			if (StringUtils.isNotBlank(e.getMessage())) {
				out.print("alert(parent.v3x.getMessage('" + e.getMessage() + "'));");
			} else {
				out.print("alert(parent.v3x.getMessage('RssLang.rss_category_edit_exception'));");
			}
			out.print("</script>");
		}
		return null;
	}
		
	// 删除RSS频道
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView deleteRssChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
//		long channelId = RequestUtils.getLongParameter(request, "channelId", 0);
		String ids = request.getParameter("deleteId");
		Set<Long> idSet = Constants.parseStrings2Longs(ids, ",");
		List<String[]> logs = new ArrayList<String[]>();
		for(Long id : idSet){
			RssCategoryChannel channel = rssChannelManager.getChannel(id);
			logs.add(new String[]{channel.getName()});
		}
		rssChannelManager.deleteRssChannel(ids);
		User user = CurrentUser.get();
		appLogManager.insertLogs(user, AppLogAction.RssChanelChange_Delete, logs);
		out.print("<script>");
		out.print("parent.parent.theTop.location.reload(true);");
		out.print("parent.parent.theBottom.location.href=\"/seeyon/common/detail.jsp\";");
		out.print("</script>");
		return null;
	}	
	
	///////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	//////				用户RSS操作			//////////////
	//////////////////////////////////////////////////////
	
	//订阅管理框架页面
	public ModelAndView subManagerIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("rss/subscription/index");
		List<RssCategory> list=rssChannelManager.getRssCategorys();
		if(list != null && list.isEmpty()==false){
			modelView.addObject("categoryId", list.get(0).getId());
			modelView.addObject("haveCategory", true);
		}else{
			modelView.addObject("haveCategory", false);
		}
		return modelView;
	}
	
	public ModelAndView guide(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("rss/subscription/guide");
		return modelView;
	}
	
	//类别管理列表页面
	public ModelAndView subManagerCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("rss/subscription/rssCategoryList");
		List<RssCategory> list=rssChannelManager.getRssCategorys();			//获取所有的类别
		modelView.addObject("category", list);
		return modelView;
	}
	
	//展现一个类别的所有频道
	public ModelAndView subManagerChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("rss/subscription/rssChannelList");
		String categoryId=request.getParameter("categoryId");		//获取一个频道的类别
		User user=CurrentUser.get();
		RssCategory category=rssChannelManager.getCategory(Long.valueOf(categoryId));  //获取一个类别
		//获取一个类别的频道
		List<RssCategoryChannel> the_channel=rssChannelManager.getChannelByCategoryId(Long.valueOf(categoryId));
		//获取一个用户在一个频道下的所有订阅
		List<RssCategoryChannel> list=rssChannelManager.getSubscribedChannelById(Long.valueOf(categoryId), orgManager.getMemberById(user.getId()).getEntityType(), user.getId());
		List<SubChannelVo> the_list=new ArrayList<SubChannelVo>();
		
		if(the_channel != null && the_channel.isEmpty()==false){
			for(int i=0;i<the_channel.size();i++){
				RssCategoryChannel channel=the_channel.get(i);
				SubChannelVo vo=new SubChannelVo(channel);
				if(list.contains(channel)){
					vo.setSubscribed(true);
				}else{
					vo.setSubscribed(false);
				}
				the_list.add(vo);
			}
		}

		modelView.addObject("category", category);
		modelView.addObject("rssChannel", the_list);
		return modelView;
	}
	
	//添加一个订阅
	public ModelAndView addSubscribe(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		String channelId = request.getParameter("channelId");		//要添加的频道ID
		V3xOrgEntity entity = orgManager.getEntity(V3xOrgMember.class, user.getId());
			//添加一个订阅
		rssManager.addSubscribe(Long.valueOf(channelId), entity.getEntityType(), user.getId());
		out.println("<script>");
		out.println("parent.parent.parent.left.treeFrame.location.reload(true);");
		out.println("</script>");
		return null;
	}
	
	//删除订阅
	public ModelAndView deleteSubscribe(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		String channelId = request.getParameter("channelId");		//频道ID
		rssManager.deleteSubscribes(user.getId(), orgManager.getMemberById(user.getId()).getEntityType(), channelId);
		
		out.println("<script>");
		//out.println("window.location.reload(true);");
		out.println("parent.parent.parent.location.reload(true);");		
		out.println("</script>");
		return null ;
		/**
		return super.refreshWorkspace();**/
	}
	
	public ModelAndView subscribeList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("rss/subscription/rssSubscribeList");
		//获取一个用户订阅的所有频道
		User user=CurrentUser.get();
		List<RssCategoryChannel> list=rssManager.getAllSubscribeInfo(orgManager.getMemberById(user.getId()).getEntityType(), user.getId());
		modelView.addObject("subList", list);
		return modelView;
	}
	
	//展现一个频道的具体信息
	public ModelAndView channelInfoList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("rss/subscription/rssChannelInfoList");
		String channelId=request.getParameter("channelId");   //获取频道ID
		User user=CurrentUser.get();
		RssCategoryChannel channel=rssChannelManager.getChannel(Long.valueOf(channelId));
		List<RssChannelItems> list=rssManager.getSubscribeInfo(Long.valueOf(channelId));		//一个频道所有的栏目
		//获取一个用户对应的已阅读项
		RssSubscribe scribe=rssManager.getSubscribeById(orgManager.getMemberById(user.getId()).getEntityType(), user.getId(), channel.getId());
		List<Long> itemId=rssManager.getReadedItems(scribe.getId(), user.getId());
		List<ChannelInfoVo> the_list=new ArrayList<ChannelInfoVo>();
		for(int i=0;i<list.size();i++){
			RssChannelItems item=list.get(i);
			ChannelInfoVo vo=new ChannelInfoVo(item);
			if(itemId.contains(item.getId())){
				vo.setIsReaded(true);
			}else{
				vo.setIsReaded(false);
			}
			the_list.add(vo);
		}
		modelView.addObject("categoryChannel", channel);
		modelView.addObject("rssChannelItems", the_list);
		return modelView;
	}
	
	public ModelAndView rssUserManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("rss/subscription/rssUserManager");
		User user=CurrentUser.get();
		List<RssCategoryChannel> list=rssManager.getAllSubscribeInfo(orgManager.getMemberById(user.getId()).getEntityType(), user.getId());
		if(list !=null && list.isEmpty()==false){
			modelView.addObject("channelId", list.get(0).getId());
			modelView.addObject("subscribed", true);
		}else{
			modelView.addObject("subscribed", false);
		}
		return modelView;
	}
	
	//标记为已读
	public ModelAndView markReaded(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		User user=CurrentUser.get();
		String channelItemId=request.getParameter("channelItemId");		//已读项
		String channelId=request.getParameter("channelId");			//频道ID
		RssSubscribe sub=rssManager.getSubscribeById( orgManager.getMemberById(user.getId()).getEntityType(),user.getId(), Long.valueOf(channelId));
		rssManager.readedItem(Long.valueOf(channelItemId),sub.getId());
		return null;
	}
	//标记为已读
	//channelItemId 频道ID
	public void markReaded(Long channelId ,Long channelItemId) throws Exception{
		User user=CurrentUser.get();
		RssSubscribe sub=rssManager.getSubscribeById( orgManager.getMemberById(user.getId()).getEntityType(),user.getId(), Long.valueOf(channelItemId));
		rssManager.readedItem(Long.valueOf(channelId),sub.getId()) ;
	}
	
	// 
	
	public ModelAndView rssIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/index");
		return mav;
	}
	
	
	public ModelAndView rssTreeIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/rssTreeIframe");
		return mav;
	}
	
	public ModelAndView rssTreeLabel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/rssTreeLabel");
		return mav;
	}
	
	public ModelAndView listMySubscriptions(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/listMySubscriptions");
		User user = CurrentUser.get();
		List<RssCategory> categories = rssManager.getMyCategories(user.getId());
		List<RssCategoryChannel> channels = rssManager.getMySubscriptions(user.getId());
		mav.addObject("categories", categories);
		mav.addObject("channels", channels);
		return mav;
	}
	
	public ModelAndView rssRightIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/rssRightIframe");
		return mav;
	}
	
	public ModelAndView navigation(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/navigation");
		return mav;
	}
	
	public ModelAndView rssMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/rssMenu");
		return mav;
	}
	
	public ModelAndView listRssItems(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		User user = CurrentUser.get();
		Long userId = user.getId();
		long channelId = RequestUtils.getLongParameter(request, "channelId", 0);
		List<RssChannelItems> items = null;
		List<Long> itemIds = null;	// 获取一个用户对应的已阅读项		
		int count ;
		if (channelId != 0) {
			mav = new ModelAndView("rss/subscription/listRssItems");
			RssCategoryChannel channel = rssChannelManager.getChannel(channelId);
			if (channel == null) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('RssLang.rss_channel_alter_deleted'));");
				out.println("history.go(-1);");
				out.println("</script>");
				return null;
			}		
			RssSubscribe subscribe = rssManager.getSubscribeById(orgManager.getMemberById(userId).getEntityType(), userId, channelId);
			itemIds = rssManager.getReadedItems(subscribe.getId(), userId);
			items = rssManager.getSubscribeInfo(channelId);		//一个频道所有的栏目
			try{
				count = items.size() ;
			}catch(Exception e){
				count = 0 ;
			}
			
			RssChannelInfo rssChannelInfo = rssChannelManager.getRssChannelByCategoryChannelId(channelId);			
			mav.addObject("categoryChannel", channel);			
			mav.addObject("channelInfo", rssChannelInfo);
		}
		else {
			mav = new ModelAndView("rss/subscription/allRssItems");
			int pageNo = RequestUtils.getIntParameter(request, "pageNo", 1);
			int pageSize = 20;//RequestUtils.getIntParameter(request, "pageSize", 20);
			count = rssManager.getMyRecentlyItemsCount(userId);
			items = rssManager.getMyRecentlyItems(userId, pageNo, pageSize);								
			int pageCount = count/pageSize;
			if (count%pageSize != 0) {
				pageCount += 1;
			}
			if (pageCount > 1) {
				StringBuffer sb = new StringBuffer();	
				int k = 10;
				if (pageCount <= 10) {
					k = pageCount;
				}
				for (int i = 1; i <= k; i++) {
					int m = i;
					if (pageCount > 10 && pageNo >= 8) {
						int j = pageNo - 7;
						if (j + 10 > pageCount) {
							j = (pageCount - 10) + 1;
						}
						m = j + i;
					}
					if (m > pageCount)
						break;
					if (m != pageNo) {
						sb.append("&nbsp;[<a href=\"###\" onclick=\"goPage('"+m+"');\">" + m + "</a>]");
					}
					else {
						sb.append("&nbsp;" + m);
					}								
				}	
				if (pageCount > 1) {
					mav.addObject("pageHtml", sb.toString());
				}
			}
			mav.addObject("pageCount", pageCount);
			mav.addObject("pageNo", pageNo);			
		}
		
		List<ChannelInfoVo> results = new ArrayList<ChannelInfoVo>();
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				itemIds = rssManager.getReadedItems(userId);
				RssChannelItems item = items.get(i);
				ChannelInfoVo vo = new ChannelInfoVo(item);
				if(itemIds.contains(item.getId())){
					vo.setIsReaded(true);
				}
				else{
					vo.setIsReaded(false);
				}
				results.add(vo);
			}
		}
		mav.addObject("count", count);
		mav.addObject("rssChannelItems", results);
		
		return mav;
	}
	
	// 管理我的rss订阅
	public ModelAndView manageMySubscriptions(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("rss/subscription/manageMySubscriptions");
		User user = CurrentUser.get();
		Long userId = user.getId();
		List<RssCategory> categories = rssChannelManager.getAllRssCategories();
		List<RssCategoryChannel> channels = rssChannelManager.getAllRssChannels();
		
		List<RssCategoryChannel> subscribedChannels = rssManager.getMySubscriptions(userId);
		// 删除没有频道的类别
		List<RssCategory> theCategories = new ArrayList<RssCategory>();
		for (int i = 0; i < categories.size(); i++) {
			int k = 0;
			RssCategory category = (RssCategory)categories.get(i);
			for (int j = 0; j < channels.size(); j++) {
				RssCategoryChannel channel = (RssCategoryChannel)channels.get(j);
				if (channel.getCategoryId().equals(category.getId())) {
					k++;
				}
			}
			if (k > 0) {
				theCategories.add(category);
			}
		}
		mav.addObject("categories", theCategories);
		mav.addObject("channels", channels);
		mav.addObject("myChannels", subscribedChannels);
		
		
		
		// 如果么有更新数据，这个时候更新
		if(channels != null && channels.size() > 0){

			synchronized(this){
				int total = this.rssManager.getAllSubTotal();
				if(total == 0){
					this.rssChannelManager.updateAllChannelInfo();
				}
			}
				
		}
		
		return mav;
	}
	
	public ModelAndView rssview(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView mav = new ModelAndView("rss/subscription/rssview");
		mav.addObject("link", request.getParameter("link"));
		return mav;
		
	}
}
