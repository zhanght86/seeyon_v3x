/**
 *
 */
package com.seeyon.v3x.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.ServerState;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.LoginConstants;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.parser.HTMLFileParser;
import com.seeyon.v3x.common.shareMap.V3xShareMap;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.usermessage.domain.UserHistoryMessage;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystem;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystemManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.manager.DocSpaceManager;
import com.seeyon.v3x.doc.webmodel.DocSpaceVO;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.main.AccountSymbol;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.manager.MainManager;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.main.section.panel.SectionPanel;
import com.seeyon.v3x.main.shortcut.ShortcutMenu;
import com.seeyon.v3x.main.shortcut.domain.Shortcut;
import com.seeyon.v3x.main.shortcut.manager.ShortcutManager;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.menu.model.MenuSpareProfile;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.notepager.domain.Notepage;
import com.seeyon.v3x.notepager.manager.NotepagerManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareNameEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Cookies;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;
import com.seeyon.v3x.util.annotation.SetContentType;
/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-30
 */
public class MainController extends BaseController{
	private static final Log log = LogFactory.getLog(MainController.class);

    private AffairManager affairManager;
    
    private EdocManager edocManager;

	private OrgManager orgManager;

	private OrgManagerDirect orgManagerDirect;

	private NotepagerManager notepagerManager;

	private OnLineManager onLineManager;

    private UserMessageManager userMessageManager;

    private FileToExcelManager fileToExcelManager;

    private SpaceManager spaceManager;

    private ConfigManager configManager;

    private ShortcutManager shortcutManager;

    private MobileMessageManager mobileMessageManager;

    private MenuManager menuManager;

    private StaffInfoManager staffInfoManager;

    private DocSpaceManager docSpaceManager;

    private MetadataManager metadataManager;

    private ExtendedMessageSystemManager extendedMessageSystemManager;

    private String clientAbortExceptionName = "ClientAbortException";

    private PortletEntityPropertyManager portletEntityPropertyManager;
    
    private ConfigGrantManager configGrantManager;
    
	public EdocManager getEdocManager() {
		return edocManager;
	}

	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	
    public void setConfigGrantManager(ConfigGrantManager configGrantManager) {
		this.configGrantManager = configGrantManager;
	}
    private OrganizationEventListener eventListener = OrganizationEventComposite.getInstance();

    public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

    private List<V3xOrgDepartment> parentDepartments;

    private SystemConfig systemConfig;
    
    private MainManager mainManager;
    
    private MtMeetingManagerCAP mtMeetingManagerCAP;
    
    private ColManager colManager;
    
    private EdocSummaryManager edocSummaryManager;
    
    public ColManager getColManager() {
		return colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}

	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	private UpdateIndexManager updateIndexManager;
	public OrganizationServices getOrganizationServices() {
		return organizationServices;
	}

	public void setOrganizationServices(OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}

	private OrganizationServices organizationServices;

	/**
	 * 用户关闭下载窗口时候，有servlet容器抛出的异常
	 * @param clientAbortExceptionName 类的simapleName，如<code>ClientAbortException</code>
	 */
	public void setClientAbortExceptionName(String clientAbortExceptionName) {
		this.clientAbortExceptionName = clientAbortExceptionName;
	}

	public void setDocSpaceManager(DocSpaceManager docSpaceManager) {
		this.docSpaceManager = docSpaceManager;
	}

	public void setAffairManager(AffairManager affairManager){
        this.affairManager = affairManager;
    }

    public void setFileToExcelManager(FileToExcelManager fileToExcelManager){
        this.fileToExcelManager=fileToExcelManager;
    }

    public void setUserMessageManager(UserMessageManager userMessageManager){
        this.userMessageManager=userMessageManager;
    }

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setNotepagerManager(NotepagerManager notepagerManager) {
		this.notepagerManager = notepagerManager;
	}

	public void setOnLineManager(OnLineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setShortcutManager(ShortcutManager shortcutManager) {
		this.shortcutManager = shortcutManager;
	}

    public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
        this.mobileMessageManager = mobileMessageManager;
    }

    public void setMenuManager(MenuManager menuManager)
    {
        this.menuManager = menuManager;
    }

    public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
        this.staffInfoManager = staffInfoManager;
    }

    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
	
	public void setMainManager(MainManager mainManager) {
		this.mainManager = mainManager;
	}

	@NeedlessCheckLogin
    public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/login/login");

		Locale currentLocale = LocaleContext.make4Frontpage(request);

		modelAndView.addObject("currentLocale", currentLocale);
		modelAndView.addObject("loginImgFileName", MainDataLoader.getInstance().getLoginImagePath());
		modelAndView.addObject("loginTitleName", Functions.getPageTitle());
		modelAndView.addObject("productCategory", ProductInfo.getEditionA());
        modelAndView.addObject("maxOnline", ProductInfo.getMaxOnline());
        modelAndView.addObject("ServerState", ServerState.getInstance().isShutdown());
        modelAndView.addObject("ServerStateComment", ServerState.getInstance().getComment());
        modelAndView.addObject("OnlineNumber", this.onLineManager.getOnlineNumber());
        modelAndView.addObject("verifyCode", "enable".equals(this.systemConfig.get("verify_code")));

       if(LDAPConfig.getInstance().getIsEnableLdap()&&request.getServerName().equalsIgnoreCase(LDAPConfig.getInstance().getA8ServerDomainName())){
    	   String adssoToken=request.getHeader("authorization");
    	   if(adssoToken==null){
    		   modelAndView.addObject("adSSOEnable",true);
    	   }else{
//    		   modelAndView.addObject("adLoginName",ADSSOEvent.getInstance().getADLoginName(adssoToken));
    		   modelAndView.addObject("authorization",adssoToken);
    	   }
        }
       
		String exceptPlugin = "";
		if(!SystemEnvironment.hasPlugin("videoconf")){
			exceptPlugin += "@videoconf";
		}
		if(!SystemEnvironment.hasPlugin("https")){
			exceptPlugin += "@seeyonRootCA";
		}
		if(!SystemEnvironment.hasPlugin("identificationDog")){
			exceptPlugin += "@identificationDog";
		}
		if(!SystemEnvironment.hasPlugin("officeOcx")){
			exceptPlugin += "@officeOcx";
		}
		
		modelAndView.addObject("exceptPlugin", exceptPlugin);
       
		return modelAndView;
	}

    @NeedlessCheckLogin
	public ModelAndView changeLocale(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Locale locale = LocaleContext.parseLocale(request.getParameter(LoginConstants.LOCALE));
		LocaleContext.setLocale(request, locale);
		Cookies.add(response, LoginConstants.LOCALE, locale.toString(), Cookies.COOKIE_EXPIRES_FOREVER);

		return this.login(request, response);
	}

	public ModelAndView notepager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    ModelAndView mav = new ModelAndView("v3xmain/person/notepager");

		Long memberId = CurrentUser.get().getId();
		Notepage notepage = notepagerManager.get(memberId);
		mav.addObject("notepage", notepage);
		return mav;
	}

    /**
     * 首页-历史消息管理->取得当前用户的信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ModelAndView showMessages(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User member = CurrentUser.get();
		ModelAndView modelAndView = new ModelAndView("v3xmain/message/showHistoryMessage");
		String showType = request.getParameter("showType");
		if("0".equals(showType)){//系统消息
			String condition = request.getParameter("condition");
			String textField1 = request.getParameter("textfield");
			String textField2 = request.getParameter("textfield1");
			String readType = request.getParameter("readType");
			List<UserHistoryMessage> messageList = userMessageManager.getAllSystemMessages(member.getId(), condition, textField1, textField2, true, readType);
			List<ExtendedMessageSystem> otherapp = extendedMessageSystemManager.getAllExtendedSystem();
			modelAndView.addObject("otherapp", otherapp);
			modelAndView.addObject("messageList", messageList);
		}else{//在线信息
	    	List<UserHistoryMessage> messageTreeList = userMessageManager.getHistoryMessageTree(member.getId());
	    	List<Long> memberList = new ArrayList<Long>();
	    	List<Long> deptList = new ArrayList<Long>();
	    	List<Long> teamList = new ArrayList<Long>();
	    	List<V3xOrgMember> memberListTemp = new ArrayList<V3xOrgMember>();
	    	List<V3xOrgDepartment> deptListTemp = new ArrayList<V3xOrgDepartment>();
	    	List<V3xOrgTeam> teamListTemp = new ArrayList<V3xOrgTeam>();

	    	for(UserHistoryMessage message: messageTreeList){
	    		Long id = null;
	    		if(message.getMessageType() == 1){
	    			id = member.getId() == message.getSenderId() ? message.getReceiverId() : message.getSenderId();
	    			if(!memberList.contains(id) && id != member.getId()){
	    				V3xOrgMember m = orgManager.getMemberById(id);
	    				if(m!=null){
	    					memberListTemp.add(m);
	    					memberList.add(id);
	    				}
	    			}
	    		}else if(message.getMessageType() == 2){
	    			id = message.getReferenceId();
	    			if(!deptList.contains(id)){
	    				V3xOrgDepartment d = orgManager.getDepartmentById(id);
	    				if(d!=null){
	    					deptListTemp.add(d);
	    					deptList.add(id);
	    				}
	    			}
	    		}else if(message.getMessageType() == 3 || message.getMessageType() == 4 || message.getMessageType() == 5){
	    			id = message.getReferenceId();
	    			if(!teamList.contains(id)){
	    				V3xOrgTeam t = orgManager.getTeamById(id);
	    				if(t!=null){
	    					teamListTemp.add(t);
	    					teamList.add(id);
	    				}
	    			}
				}
	    	}

	    	Comparator cmp1 = new CompareNameEntity();
	    	Comparator cmp2 = new CompareNameEntity();
	    	Comparator cmp3 = new CompareNameEntity();
	        Collections.sort(memberListTemp, cmp1);
	        Collections.sort(deptListTemp, cmp2);
	        Collections.sort(teamListTemp, cmp3);

	        memberList.clear();
	        deptList.clear();
	        teamList.clear();
	        Iterator<V3xOrgMember> iter1 = memberListTemp.iterator();
	        while (iter1.hasNext()) {
	        	V3xOrgMember o = (V3xOrgMember) iter1.next();
	        	memberList.add(o.getId());
	        }
	        Iterator<V3xOrgDepartment> iter2 = deptListTemp.iterator();
	        while (iter2.hasNext()) {
	        	V3xOrgDepartment d = (V3xOrgDepartment) iter2.next();
	        	deptList.add(d.getId());
	        }
	        Iterator<V3xOrgTeam> iter3 = teamListTemp.iterator();
	        while (iter3.hasNext()) {
	        	V3xOrgTeam t = (V3xOrgTeam) iter3.next();
	        	teamList.add(t.getId());
	        }
	    	modelAndView.addObject("memberList", memberList);
	    	modelAndView.addObject("memberSize", memberList.size());
	    	modelAndView.addObject("deptList", deptList);
	    	modelAndView.addObject("teamList", teamList);
	    	modelAndView.addObject("teamSize", deptList.size() + teamList.size());
		}
		return modelAndView;
    }

    /**
	 * 历史消息管理->清空所有信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView removeMessages(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        User member = CurrentUser.get();
        Long internalId = member.getId();
        String showType = request.getParameter("showType");
        if("0".equals(showType)){
            userMessageManager.removeAllMessages(internalId, 0);
        }
        else{
            userMessageManager.removeAllMessages(internalId, 1);
        }

        return super.redirectModelAndView("/main.do?method=showMessages&isClear=Y&showType="+showType);
    }

    /**
     * 历史消息管理->导出为EXCEL文件
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView saveAsExcel(HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        User member = CurrentUser.get();
        Long internalId = member.getId();
        String showType = request.getParameter("showType");
        String fileName = null;
        DataRecord myDataRecord = new DataRecord();

        if("0".equals(showType)){//如果是系统提醒消息，则需显示消息类型列
        	fileName = Constant.getValueFromMainRes("message.tag.systemMessage.label");
        	List<UserHistoryMessage> messageList = userMessageManager.getAllSystemMessages(internalId,null,null,null,false);

            myDataRecord.setColumnName(new String[]{
        		Constant.getValueFromMainRes("message.tableHeader.category"),
        		Constant.getValueFromMainRes("message.tableHeader.sender"),
        		Constant.getValueFromCommonRes("common.date.sendtime.label"),
        		Constant.getValueFromMainRes("message.tableHeader.title")
            });

            myDataRecord.setColumnWith(new short[]{12, 12, 24, 50});
            if(messageList != null && !messageList.isEmpty()){
            	for (UserHistoryMessage obj : messageList) {
    				DataRow row = new DataRow();
    			         //信息类型
    			     String categoryStr = Constant.getApplicationCategory(obj.getMessageCategory());

    			     row.addDataCell(categoryStr, DataCell.DATA_TYPE_TEXT);
    			     row.addDataCell(obj.getSenderName(),DataCell.DATA_TYPE_TEXT);
    			     row.addDataCell(Datetimes.formateToLocaleDatetime(obj.getCreationDate()), DataCell.DATA_TYPE_DATETIME);
    			     row.addDataCell(obj.getMessageContent(), DataCell.DATA_TYPE_TEXT);
    			     myDataRecord.addDataRow(row);
    			}
            }
        }
        else{
        	 myDataRecord.setColumnName(new String[]{Constant.getValueFromMainRes("message.tableHeader.sender"), Constant.getValueFromMainRes("message.tableHeader.title")});
             fileName = Constant.getValueFromMainRes("message.tag.personMessage.label");
             myDataRecord.setColumnWith(new short[]{40, 120});
             String exportType = request.getParameter("exportType");
             Long exportId = NumberUtils.toLong(request.getParameter("exportId"));
             List<UserHistoryMessage> messageList = userMessageManager.getAllHistoryMessage(null, internalId, exportId, true, exportType, null, null, false);
        	 if(messageList != null && !messageList.isEmpty()){
        		 for (UserHistoryMessage obj : messageList) {
                     DataRow row = new DataRow();
                     HTMLFileParser parser = new HTMLFileParser();
                     parser.setStr(obj.getMessageContent());
                     row.addDataCell(obj.getSenderName() + "  " + Datetimes.formateToLocaleDatetime(obj.getCreationDate()), DataCell.DATA_TYPE_TEXT);
                     row.addDataCell(parser.getContentString(), DataCell.DATA_TYPE_TEXT);
    			     myDataRecord.addDataRow(row);
                 }
        	 }
        }

        myDataRecord.setSheetName(fileName);
        myDataRecord.setTitle(fileName);

        fileToExcelManager.save(request, response, fileName, myDataRecord);
        return null;
    }

    /**
     * 常用工具-天气预报
     * @deprecated 该功能已废弃
     */
    public ModelAndView weather(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/tools/weather");
        String weatherConfig = "";
        ConfigItem configItem_weather = configManager.getConfigItem(com.seeyon.v3x.system.Constants.CONFIG_CATRGORY_COMMON_TOOLS, com.seeyon.v3x.system.Constants.CONFIG_ITEM_WEATHER);
        if(configItem_weather!=null && configItem_weather.getExtConfigValue().equals("on")){
            weatherConfig = configItem_weather.getConfigValue();
        }
        modelAndView.addObject("weatherConfig", weatherConfig);
		return modelAndView;
	}
    /**
     * 常用工具-计算器
     */
    public ModelAndView calculator(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("common/tools/calculator");
        return modelAndView;
    }
    /**
     * 常用工具-万年历
     */
    public ModelAndView calendar(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	Boolean f = (Boolean)(BrowserFlag.HideBrowsers.getFlag(request));
    	String url = "common/tools/calendar";
    	if(f.booleanValue()==false){
    		url = "common/tools/calendarSupport";
    	}
        ModelAndView modelAndView = new ModelAndView(url);
        return modelAndView;
    }

    /**
     * 更多待办事项
     */
    @SuppressWarnings("unchecked")
	public ModelAndView morePending(HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/morePending");
    	String fragmentId = request.getParameter("fragmentId");
    	String ordinal = request.getParameter("ordinal");
    	List<SectionPanel> panels = portletEntityPropertyManager.getSectionPanel(Long.parseLong(fragmentId), ordinal, "panel", "");
    	if(panels == null){
    		//TODO warning the user that the fragment has been deleted.
    	}
//    	if(panels.isEmpty()){
//    		panels.add(new SectionPanel("all", PortalConstants.getPanelName("all", null)));
//    		panels.add(new SectionPanel("overTime",PortalConstants.getPanelName("overTime", null)));
//    	}
    	String currentPanel = request.getParameter("currentPanel");
    	Long memberId = CurrentUser.get().getId();

    	Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
		apps.add(ApplicationCategoryEnum.collaboration);
		apps.add(ApplicationCategoryEnum.edoc);
		apps.add(ApplicationCategoryEnum.meeting);
		apps.add(ApplicationCategoryEnum.inquiry);
		apps.add(ApplicationCategoryEnum.bulletin);
		apps.add(ApplicationCategoryEnum.news);
		apps.add(ApplicationCategoryEnum.office);
		apps.add(ApplicationCategoryEnum.info);
		apps.add(ApplicationCategoryEnum.meetingroom);
		apps.add(ApplicationCategoryEnum.edocRecDistribute);
		apps.add(ApplicationCategoryEnum.infoStat);
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,apps);
//		if(!ma.isEmpty()){
//			panels.add(new SectionPanel("agent",PortalConstants.getPanelName("agent", null)));
//		}
		condition.setAgent(agentToFlag, ma);
		//根据传递过来的fragmentId和ordinal获取栏目点击更多过来的过滤条件preference
		Map<String,String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
		String tempStr = preference.get(currentPanel+"_value");
		/*if(panels.size() != 1){
			for(SectionPanel panel : panels){
				condition.removeAllCondition();
				if("overTime".equals(panel.getId())){
					condition.addSearch(SearchCondition.overTime, null, null);
				}else if(!"all".equals(panel.getId()) && !"agent".equals(panel.getId())){
					Integer count = 0;
					if(Strings.isNotBlank(tempStr)){
						if("templete_pending".equals(currentPanel)){
							condition.addSearch(SearchCondition.templete, tempStr, null);
							count = condition.getPendingCount(affairManager);
						}else if("Policy".equals(currentPanel)){
							condition.addSearch(SearchCondition.policy4Portal, tempStr, null);
							count = condition.getPendingCount(affairManager);
						}else if("importLevel_pending".equals(currentPanel)){
							condition.addSearch(SearchCondition.importLevel, tempStr, null);
							count = condition.getPendingCount(affairManager);
						}else if("sender".equals(currentPanel)){
							count = (Integer)affairManager.getAffairListBySender(memberId, tempStr, condition, true);
						}
					}
					panel.setAffairCount(count);
					continue;
				}
				if("agent".equals(panel.getId())){
					Integer count = condition.getAgentPendingCount(affairManager);
					panel.setAffairCount(count);
				}else{
					Integer count = condition.getPendingCount(affairManager);
					panel.setAffairCount(count);
				}
			}
		}*/
		modelAndView.addObject("panelSize", panels.size());
    	modelAndView.addObject("allPanels", panels);
    	modelAndView.addObject("currentPanel", currentPanel);
    	
    	condition.removeAllCondition();
    	List<Affair> listAffair  = null;
		if ("all".equals(currentPanel)) {
			//ignore
    	}
		else if("overTime".equals(currentPanel)){
			condition.addSearch(SearchCondition.overTime, null, null);
		}
		else if("freeCol".equals(currentPanel)) {//自由协同
			condition.addSearch(SearchCondition.catagory, "catagory_coll", null);
		}
		else if("templete_pending".equals(currentPanel)){
			condition.addSearch(SearchCondition.templete, tempStr, null);
		}
		else if("Policy".equals(currentPanel)){
			condition.addSearch(SearchCondition.policy4Portal, tempStr, null);
		}
		else if("importLevel_pending".equals(currentPanel)){
			condition.addSearch(SearchCondition.importLevel, tempStr, null);
		}
		else if("catagory".equals(currentPanel)){
			condition.addSearch(SearchCondition.catagory, tempStr, null);
		}
		
    	String conditions = request.getParameter("condition");
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			/*if (Strings.isNotBlank(textField1) && Strings.isNotBlank(textField2)) {
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}*/
    			/* branches_a8_v350sp1_r_gov GOV-5053 政务 向凡 注释上面代码 修改为下面的方式，修复由于查询时间段是 只输入一个时间条件导致 时间没有被格式化，从而查询出的数据不准确 Start */
    			if(Strings.isNotBlank(textField1) && "createDate".equals(con.toString())){
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    			}
    			if(Strings.isNotBlank(textField2) && "createDate".equals(con.toString())){
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}
    			/* branches_a8_v350sp1_r_gov GOV-5053 End*/
    			condition.addSearch(con, textField1, textField2);
    		}
    	}
    	
		if("sender".equals(currentPanel)){
			//查询指定发起人，用于查询指定发起人的时候查询比较复杂，所以采用SQL的方式进行查询，其他情况维持原来的逻辑不变
			listAffair = (List<Affair>)affairManager.getAffairListBySender(memberId,tempStr,condition,false);
			Pagination.setRowCount((Integer)affairManager.getAffairListBySender(memberId,tempStr,condition,true));//重新设置分页信息
		}
		else{
	    	if("agent".equals(currentPanel) && ma != null && !ma.isEmpty()){//代理人的待办事项被被移除，此处代码无效2012-03-24lilong
				listAffair = condition.queryAgentPendingAffair(affairManager);
			}
			else{
				listAffair = condition.queryPendingAffairSecretLevel(affairManager);//成发集团项目 程炯 获取根据密级筛选后的待办事项
				Pagination.setRowCount(condition.getPendingCountSecretLevel(affairManager));//成发集团项目 程炯 获取根据密级筛选后的待办事项的数量
			}
		}
		
    	modelAndView.addObject("pendingList", listAffair);
    	Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("ExtendTitle",preference.get("columnsName"));
        /** 政务版   是否有公文登记、会议审核权限**/
        boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGov){
        	//是否具有 公文登记权限
        	boolean isEdocCreateRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.recEdoc.ordinal());
        	modelAndView.addObject("isEdocCreateRegister", isEdocCreateRegister);
        	//是否具有 会议审核权限
        	boolean hasMtAppAuditGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
        	modelAndView.addObject("hasMtAppAuditGrant", hasMtAppAuditGrant);
        	//是否具有信息报送审核权限
        	boolean hasInfoAuditGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "info_config_grant", "info_config_grant_audit");
			modelAndView.addObject("hasInfoAuditGrant", hasInfoAuditGrant);
        	//是否具有 公文收文分发限
        	boolean hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.distributeEdoc.ordinal());
        	modelAndView.addObject("hasEdocDistributeGrant", hasEdocDistributeGrant); 
        }
    	return modelAndView;
    }
    
    //wangjingjing begin
    /**
     * 政务【我的提醒】【待办公文】连接  【待办公文】包括 【公文交换,待发送,待办发文,待办收文办文】
     */
    @SuppressWarnings("unchecked")
	public ModelAndView morePolicy4PortalPending(HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/morePending");
    	
    	Long memberId = CurrentUser.get().getId();
    	
		String ExtendTitle = null;
    	
		List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
		apps.add(ApplicationCategoryEnum.collaboration);
		apps.add(ApplicationCategoryEnum.edoc);
		apps.add(ApplicationCategoryEnum.meeting);
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,apps);
		
		Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		condition.setAgent(agentToFlag, ma);
		
    	condition.removeAllCondition();
    	//更多待办的条件查询
    	String conditions = request.getParameter("condition");
    	String panelName = "";//现在待办事项的更多都显示的是待办工作
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			if (Strings.isNotBlank(textField1) && Strings.isNotBlank(textField2)) {
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}
    			condition.addSearch(con, textField1, textField2);
    		}
    		//branches_a8_v350_r_govGOV-3868 唐桂林 首页-个人空间-左侧我的提醒中，待办公文，列表查询框，所有的查询条件选择后，不填写查询值，直接查询，返回的数据不一致 start
    		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
    		if(isGov) {
    			String pendingType = request.getParameter("pendingType");
	    		if("banwenPending".equals(pendingType)){
	    			//不包括阅文S10
    				condition.addSearch(SearchCondition.policy4Portal, "A___19,A___16,A___21,A___22,A___23,A___24,A___34,S9___all", null);
					ExtendTitle = "common.my.edocPending.title";
	    		}else if("yuewenPending".equals(pendingType)){
	    			condition.addSearch(SearchCondition.policy4Portal, "S10___all", null);
					ExtendTitle = "menu.person.space.37";
	    		}else if("meetingNotification".equals(pendingType)){
	    			condition.addSearch(SearchCondition.policy4Portal, "S5___all", null);
					ExtendTitle = "menu.person.space.4";
	    		}
    		}
    		//branches_a8_v350_r_govGOV-3868 唐桂林 首页-个人空间-左侧我的提醒中，待办公文，列表查询框，所有的查询条件选择后，不填写查询值，直接查询，返回的数据不一致 end
    	}
    	else {
    		String pendingType = request.getParameter("pendingType");
    		if("banwenPending".equals(pendingType)){
    			boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
    			if(isGov) {
    				//不包括阅文S10
    				condition.addSearch(SearchCondition.policy4Portal, "A___19,A___16,A___21,A___22,A___23,A___24,A___34,S9___all", null);
    			} else {
    				condition.addSearch(SearchCondition.policy4Portal, "A___19,A___16,A___22,S9___all", null);
    			}
				ExtendTitle = "common.my.edocPending.title";
    		}else if("yuewenPending".equals(pendingType)){
    			condition.addSearch(SearchCondition.policy4Portal, "S10___all", null);
				ExtendTitle = "menu.person.space.37";
    		}else if("meetingNotification".equals(pendingType)){
    			condition.addSearch(SearchCondition.policy4Portal, "S5___all", null);
				ExtendTitle = "menu.person.space.4";
    		}else{
    			return null;
    		}
    	}
		
    	List<Affair> listAffair = condition.queryPendingAffair(affairManager);

    	SectionPanel panel = new SectionPanel("Policy",panelName);
		panel.setAffairCount(condition.getAgentPendingCount(affairManager));
		List<SectionPanel> panels = new ArrayList<SectionPanel>();
		panels.add(panel);
		
		modelAndView.addObject("ExtendTitle", ExtendTitle);
		
		modelAndView.addObject("panelSize", 1);
    	modelAndView.addObject("allPanels", panels);
    	modelAndView.addObject("currentPanel", panel.getId());
    	modelAndView.addObject("pendingList", listAffair);
    	Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);
        //lijl添加---------------------------------------------------Start
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGovVersion){
        	modelAndView.addObject("colMetadata", colMetadata);
        	boolean isEdocCreateRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.recEdoc.ordinal());
        	if(isEdocCreateRegister){
        		modelAndView.addObject("isEdocCreateRegister", isEdocCreateRegister);
        	}

        }
        //lijl添加---------------------------------------------------End
    	return modelAndView;
    }
    //wangjingjing end
    
    @SuppressWarnings("unchecked")
	public ModelAndView morePending4App(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/morePending");
    	
    	String app = request.getParameter("app");
    	
    	Long memberId = CurrentUser.get().getId();
    	Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
    	
		String ExtendTitle = null;
		String ExtendId = null;
		boolean isHiddenBach = false;
		
		AffairCondition condition = null;
		
		if("agent".equals(app)){ //所有代理
			List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
			apps.add(ApplicationCategoryEnum.collaboration);
			apps.add(ApplicationCategoryEnum.edoc);
			apps.add(ApplicationCategoryEnum.meeting);
			apps.add(ApplicationCategoryEnum.inquiry);
			apps.add(ApplicationCategoryEnum.bulletin);
			apps.add(ApplicationCategoryEnum.news);
			boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
			
			/** 政务版   是否有信息报送的插件**/
            String infoSearchValue="";
            if(isGov){
			   apps.add(ApplicationCategoryEnum.info);//政务版——新增信息报送代理，会议室代理
			   infoSearchValue=",A___32,A___29";
			}
			
			condition = new AffairCondition(memberId, StateEnum.col_pending, apps);
			condition.addSearch(SearchCondition.policy4Portal, "A___1,A___4,A___6,A___8"+infoSearchValue, null);//政务版——新增信息报送代理查询，A__32
			condition.setAgent(agentToFlag, ma);
			
			ExtendTitle = "menu.person.space.8";
			ExtendId = "8";
		}
		else if("Coll".equals(app)){ //协同
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.collaboration
			);
			condition.setAgent(agentToFlag, ma);
			ExtendTitle = "menu.person.space.1";
			ExtendId = "1";
		}
		else if("Edoc".equals(app)){ //公文
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.edoc
			);
			condition.setAgent(agentToFlag, ma);
			ExtendTitle = "menu.person.space.2";
			ExtendId = "2";
		}
		else if("Meeting".equals(app)){ //会议
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.meeting
			);
			condition.setAgent(agentToFlag, ma);
			ExtendTitle = "menu.person.space.4";
			ExtendId = "4";
			isHiddenBach = true;
		}
		else if("PubInfo".equals(app)){ //公共信息
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.bulletin,
					ApplicationCategoryEnum.news,
					ApplicationCategoryEnum.inquiry
			);
			condition.addSearch(SearchCondition.policy4Portal, "A___8", null);
			condition.setAgent(agentToFlag, ma);
			ExtendTitle = "menu.person.space.6";
			ExtendId = "6";
			isHiddenBach = true;
		}
		else if("Inquiry".equals(app)){ //调查待填
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.inquiry
			);
			condition.addSearch(SearchCondition.policy4Portal, "A___10___1", null);
			ExtendTitle = "menu.person.space.5";
			ExtendId = "5";
			isHiddenBach = true;
		}
		else if("ZHBG".equals(app)){ //综合办公
			condition = new AffairCondition(memberId, StateEnum.col_pending,
					ApplicationCategoryEnum.office
			);
			ExtendTitle = "menu.person.space.7";
			ExtendId = "7";
			isHiddenBach = true;
		}
		else{ //
			return modelAndView;
		}
		
		//更多待办的条件查询
    	String conditions = request.getParameter("condition");
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			if (Strings.isNotBlank(textField1) && Strings.isNotBlank(textField2)) {
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}
    			condition.addSearch(con, textField1, textField2);
    		}
    	}
    	
    	/**xiangfan 2012-04-07 添加 start*/
		/** 政务版   是否有信息报送的插件**/
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGovVersion){
        	//是否具有 公文登记权限
        	boolean isEdocCreateRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.recEdoc.ordinal());
        	//是否具有 公文收文分发限
        	boolean hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.distributeEdoc.ordinal());
        	//是否具有 会议审核权限
        	boolean hasMtAppAuditGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
        	modelAndView.addObject("isEdocCreateRegister", isEdocCreateRegister);
        	modelAndView.addObject("hasMtAppAuditGrant", hasMtAppAuditGrant);
        	modelAndView.addObject("hasEdocDistributeGrant", hasEdocDistributeGrant); 
        }
    	/**xiangfan 添加 end*/
    	
    	List<Affair> listAffair = null;
    	if("agent".equals(app)){ //所有代理
    		listAffair = condition.queryAgentPendingAffair(affairManager);
    	}
    	else{
    		listAffair = condition.queryPendingAffairSecretLevel(affairManager);//成发集团项目
    	}
    	
    	modelAndView.addObject("isHiddenBach", isHiddenBach);
    	modelAndView.addObject("panelSize", 1);
    	modelAndView.addObject("SampleSearch", true);
    	modelAndView.addObject("ExtendTitle", ExtendTitle);
    	modelAndView.addObject("ExtendId", ExtendId);
    	
    	modelAndView.addObject("pendingList", listAffair);
    	Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
    	modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
    	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
    	modelAndView.addObject("colMetadata", colMetadata);
    	
    	return modelAndView;
    }
    /**
     * 公文更多待办事项
     */
    @SuppressWarnings("unchecked")
	public ModelAndView edocPending(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/edocPending");

    	String currentPanel = request.getParameter("currentPanel");
    	
    	Long memberId = CurrentUser.get().getId();

		Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edoc
		);
		
		condition.setAgent(agentToFlag, ma);
		
		String fragmentId = request.getParameter("fragmentId");
		String ordinal = request.getParameter("ordinal");
		if(StringUtils.isNotBlank(fragmentId) && StringUtils.isNotBlank(ordinal)) {
			Map<String,String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
			String panel = SectionUtils.getPanel("all", preference);
			if("all".equals(panel)) {
				
			}
			else{
				if(StringUtils.isBlank(preference.get(panel+"_value"))) {
					modelAndView.addObject("tempeleteCount", 0);
					modelAndView.addObject("countEdoc", 0);
					modelAndView.addObject("overTimeColCount", 0);
					return modelAndView;
				}
				String tempStr = preference.get(panel+"_value");
				if("track_catagory".equals(currentPanel)){//分类
					condition.addSearch(SearchCondition.applicationEnum, tempStr, null);
				}
				else if("importLevel".equals(currentPanel)){//重要程度
					condition.addSearch(SearchCondition.importLevel, tempStr, null);
				}
			}
		}
		
		boolean isAudit = "true".equals(request.getParameter("isAudit"));
		if(isAudit){
			condition.addSearch(SearchCondition.nodePerm, "audit", null);
		}
		
		//取模板总数
		condition.addSearch(SearchCondition.templete, null, null);
		int tempeleteCount = condition.getPendingCountSecretLevel(affairManager);//成发集团项目
		condition.removeConditon(SearchCondition.templete);
		
		//取超期总数
		condition.addSearch(SearchCondition.overTime, null, null);
		int overTimeColCount = condition.getPendingCountSecretLevel(affairManager);//成发集团项目
		condition.removeConditon(SearchCondition.overTime);
		
		//取总数
		int countEdoc = condition.getPendingCountSecretLevel(affairManager);//成发集团项目
		
		String searchType = request.getParameter("searchType");
		if("1".equals(searchType)){ //当前子页面是：模板
			condition.addSearch(SearchCondition.templete, null, null);
		}
		else if("3".equals(searchType)){ //当前子页面是：超期
			condition.addSearch(SearchCondition.overTime, null, null);
		}
		
		//更多待办的条件查询
    	String conditions = request.getParameter("condition");
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			if (Strings.isNotBlank(textField1) && Strings.isNotBlank(textField2)) {
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}
    			condition.addSearch(con, textField1, textField2);
    		}
    	}
		
		List<Affair> pendingList = condition.queryPendingAffairSecretLevel(affairManager);//成发集团项目
		
		modelAndView.addObject("tempeleteCount", tempeleteCount);
		modelAndView.addObject("countEdoc", countEdoc);
		modelAndView.addObject("overTimeColCount", overTimeColCount);
		modelAndView.addObject("pendingList", pendingList);

		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
    	modelAndView.addObject("colMetadata", colMetadata);

    	return modelAndView;
    }

    @SuppressWarnings("unchecked")
	private AffairCondition initAffairCondition(HttpServletRequest request,
			HttpServletResponse response,Long memberId){
    	Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.collaboration,
				ApplicationCategoryEnum.edoc,
				ApplicationCategoryEnum.meeting,
				ApplicationCategoryEnum.bulletin,
				ApplicationCategoryEnum.news,
				ApplicationCategoryEnum.inquiry,
				ApplicationCategoryEnum.office
		);
		condition.addSearch(SearchCondition.nodePerm, "audit", null);
		condition.setAgent(agentToFlag, ma);
		String conditions = request.getParameter("condition");
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			condition.addSearch(con, textField1, textField2);
    		}
    	}
    	return condition;
    }

    /**
     * 审批事项更多页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView auditMore(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/agentPending");
    	Long memberId = CurrentUser.get().getId();
    	AffairCondition condition = initAffairCondition(request, response, memberId);

    	List<Affair> listAffair = condition.queryPendingAffair(affairManager);
    	modelAndView.addObject("pendingList", getAllAffair(listAffair));
		Integer count = condition.getPendingCount(affairManager);
		modelAndView.addObject("allCount", count);

		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("isAudit", true);

    	return modelAndView;
    }
	/**
	 * 委托出去的待办事项
	 */
	public ModelAndView agentPending(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/agentPending");
		Long memberId = CurrentUser.get().getId();
		AffairCondition condition = initAffairCondition(request, response, memberId);
		List<Affair> pendingList = condition.queryAgentPendingAffair(affairManager);

		modelAndView.addObject("pendingList", getAllAffair(pendingList));
		Integer count = condition.getAgentPendingCount(affairManager);
		modelAndView.addObject("allCount", count);

		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("isAudit", false);
		return modelAndView;
	}
	public ModelAndView overTimePending(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/overTimePending");
		User user = CurrentUser.get();
		Long memberId = user.getId();
		AffairCondition condition = initAffairCondition(request, response, memberId);
		condition.addSearch(SearchCondition.overTime, null, null);

		List<Affair> affairs = condition.queryPendingAffair(affairManager);
		modelAndView.addObject("result",getAllAffair(affairs));
		int countOverTime = Pagination.getRowCount();
		Pagination.setRowCount(countOverTime);
		modelAndView.addObject("countOverTime",countOverTime);

		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
    	modelAndView.addObject("colMetadata", colMetadata);
		return modelAndView;
	}
	/**
	 * 处理下是否超期的问题
	 * 根据时间来进行判断，Affair字段的更新存在问题
	 * @return
	 * @deprecated 此方法作废：
	 *             是否超时要根据工作时间判断，Affair的属性isOvertopTime由定时任务affairIsOvertopTimeJob
	 *             （IsOvertopTimeJob.java）负责设置， 该任务在发送协同时由ListMapTask.java注册。
	 */
	private List<Affair> getAllAffair (List<Affair> list) {
		return list;
/*
		List<Affair> backList = new ArrayList<Affair>() ;
		if(list != null) {
			for(Affair affair : list) {
		        java.sql.Timestamp startDate1 = affair.getCreateDate();

				Date now = new Date(System.currentTimeMillis());
				if(affair.getCreateDate() != null
						&& affair.getDeadlineDate() != null
						&& affair.getDeadlineDate() != 0
						&& affair.getCompleteTime() == null){
                      if(now.getTime() - startDate1.getTime() > affair.getDeadlineDate().longValue()*60000) {
                    	  affair.setIsOvertopTime(true) ;
                      }
				}
				backList.add(affair) ;
			}
		}
		return backList ;
*/
	}

	/**
	 * 更多跟踪事项
	 * @author muj
	 * modify by lilong at 2012-01-13
	 */
	public ModelAndView moreTrack(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/moreTrack");
		List<Affair> allTrackList = getMoreList4SectionContion(request, 0, true);

		int countTrack = Pagination.getRowCount();
		Pagination.setRowCount(countTrack);

		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);

		modelAndView.addObject("allTrackList", allTrackList);
		modelAndView.addObject("countTrack", countTrack);
		modelAndView.addObject("entry", "home&listMethod=listInfoReport&listType=listInfoReported&menuId=3101");
		return modelAndView;
	}

	/**
	 * 更多已办事项
	 * @author lilong 2012-01-12
	 */
	public ModelAndView moreDone(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/moreDone");
		List<Affair> allDoneList = getMoreList4SectionContion(request, StateEnum.col_done.key(), false);

		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
		modelAndView.addObject("allDoneList", allDoneList);
		//branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接 start
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		if(isGov) {
			boolean hasMtAppAuditGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
			modelAndView.addObject("hasMtAppAuditGrant", hasMtAppAuditGrant);
			boolean hasInfoAuditGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "info_config_grant", "info_config_grant_audit");
			modelAndView.addObject("hasInfoAuditGrant", hasInfoAuditGrant);
			
        	//是否具有 公文登记权限
        	boolean isEdocCreateRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.recEdoc.ordinal());
        	//是否具有 公文收文分发限
        	boolean hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.distributeEdoc.ordinal());
        	modelAndView.addObject("hasEdocDistributeGrant", hasEdocDistributeGrant);
        	modelAndView.addObject("isEdocCreateRegister", isEdocCreateRegister);
		}
		//branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接 end
		return modelAndView;
	}

	/**
	 * 更多已发事项
	 * @author lilong 2012-01-12
	 */
	public ModelAndView moreSent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/moreSent");
		List<Affair> allSentList = getMoreList4SectionContion(request, StateEnum.col_sent.key(), false);

		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
		modelAndView.addObject("allSentList", allSentList);
		/** 政务版   是否有公文登记、会议审核权限**/
        boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGov) {
        	boolean hasInfoReportGrant = configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), "info_config_grant", "info_config_grant_report");
        	modelAndView.addObject("hasInfoReportGrant", hasInfoReportGrant);        	
        }
		return modelAndView;
	}

	/**
	 * 方便已发、已办、跟踪更多事项查询方法
	 * @param request
	 * @param state
	 * @return
	 * @author lilong 2012-01-13
	 */
	private List<Affair> getMoreList4SectionContion(HttpServletRequest request, int state, boolean isTrack) {
		List<Affair> affairList = new ArrayList<Affair>();
		
  		AffairCondition condition = new AffairCondition();
  		condition.setMemberId(CurrentUser.get().getId());
  		
  		String fragmentId = request.getParameter("fragmentId");
		String ordinal = request.getParameter("ordinal");
        Map<String,String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
        String panel = SectionUtils.getPanel("all", preference);
		if("all".equals(panel)) {
			//全部
		}
		else {
			String tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return affairList;
			}
			if("track_catagory".equals(panel)){//分类
				condition.addSearch(SearchCondition.catagory, tempStr, null);
			}
			else if("importLevel".equals(panel)){//重要程度
				condition.addSearch(SearchCondition.importLevel, tempStr, null);
			}
		}
		
    	String conditions = request.getParameter("condition");
    	if(Strings.isNotBlank(conditions)){
    		String textField1 = request.getParameter("textfield");
    		String textField2 = request.getParameter("textfield1");
    		
    		SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
    		if(con != null){
    			if (Strings.isNotBlank(textField1) 
    					&& Strings.isNotBlank(textField2) 
    					&& AffairCondition.SearchCondition.createDate.name().equals(conditions)) {
    				textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
    				textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
    			}
    			condition.addSearch(con, textField1, textField2);
    		}
    	}
    	
		if(isTrack) {
			affairList = condition.queryTrackAffair(affairManager);
		}
		else {
			List<Affair> list = condition.querySectionAffair(affairManager,state);
			affairList=setFlowStateForAffair(list);
		}	
		return affairList;
	}
	/**
     * 为取得的Affair列表中的Affair设置flowState属性
     * @param list
     * @return
     */
	private List<Affair> setFlowStateForAffair(List<Affair> list){
		List<Affair> affairList = new ArrayList<Affair>();
		for(Affair affair:list){
			Long summaryId=affair.getObjectId();
			ColSummary colSummary=null;
			EdocSummary edocSummary=null;
			try {
				if(affair.getApp()==1){//协同
					colSummary=colManager.getColSummaryById(summaryId, false);
					affair.setFlowState(colSummary.getState());
				}else if(affair.getApp()==4 || (affair.getApp()>=19 && affair.getApp()<=24)){//公文
					edocSummary=edocManager.getEdocSummaryById(summaryId, false);
					affair.setFlowState(edocSummary.getState());
				}
			} catch (Exception e) {
				log.error("获取Summary对象失败", e);
			}
			affairList.add(affair);
		}
		return affairList;
	}
    /**
     * 取消跟踪
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView cancelTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/moreTrack");
    	String[] affairIds = request.getParameterValues("affairId");
		if (affairIds != null) {
			for (String string : affairIds) {
				long affairId = Long.parseLong(string);
				Map<String, Object> columnValue = new HashMap<String, Object>();
				columnValue.put("isTrack", false);
				this.affairManager.update(affairId, columnValue);
			}
		}
		Long memberId = CurrentUser.get().getId();
		List<Affair> allTrackList = affairManager.queryTrackList(memberId, memberId, null, null, null);
		modelAndView.addObject("allTrackList", allTrackList);
		modelAndView.addObject("countTrack",affairManager.countTrack(memberId, memberId, null, null, null));
		return modelAndView;
	}

    /**
     * TOP
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/top");
		User user = CurrentUser.get();
		Long memberId = user.getId();
		Long accountId = user.getLoginAccount();

		String userType = "";
        String accountName = " ";
        String secondAccountName = " ";

		//加载单位标示设置数据
		AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbol(accountId);

        String showMenu = (String)(SysFlag.valueOf("frontPage_showMenu").getFlag());
        V3xOrgAccount rootAccount = orgManager.getRootAccount();
        //显示集团名称
        boolean isShowGroupShortName = (Boolean)(SysFlag.frontPage_showGroupShortName.getFlag());
        if(isShowGroupShortName && !accountSymbol.isHiddenGroupName()){
            if(user.isSystemAdmin() || user.isAuditAdmin() || user.isGroupAdmin() || user.isSecretAdmin()){ //集团版本集团管理员和系统管理员显示集团全称
                accountName = rootAccount.getName();
                secondAccountName = rootAccount.getSecondName();
            }
            else if(orgManager.isAccountInGroupTree(accountId)){
                String groupShortName = rootAccount.getShortname();
                if(Strings.isBlank(groupShortName)){
                    groupShortName = "";
                }
                accountName = groupShortName + "&nbsp;";
            }
        }
        //显示单位名称
        if(!accountSymbol.isHiddenAccountName()){
            if(isShowGroupShortName){ //集团版本
                if(!user.isSystemAdmin() && !user.isAuditAdmin() && !user.isGroupAdmin() && !user.isSecretAdmin()){
                    V3xOrgAccount account = orgManager.getAccountById(accountId);
                    if(orgManager.isAccountInGroupTree(accountId)){
                        accountName += account.getName();
                    }
                    else{
                        accountName = account.getName();
                    }
                    secondAccountName = account.getSecondName();
                }
            }
            else{
                List<V3xOrgAccount> accountsList = orgManager.getAllAccounts();
                for(V3xOrgAccount theAccount : accountsList){
                    if(!theAccount.getIsRoot()){
                        accountName = theAccount.getName();
                        secondAccountName = theAccount.getSecondName();
                        break;
                    }
                }
            }
        }

		if(user.isSystemAdmin()){
			userType = Constant.USER_TYPE.system.name();
		}
        else if(user.isGroupAdmin()){
            userType = Constant.USER_TYPE.group.name();
            boolean isShowMobileMenu = SystemEnvironment.hasPlugin("mobileWap") || mobileMessageManager.isValidateMobileMessage();
            modelAndView.addObject("isShowMobileMenu", isShowMobileMenu);
            //是否启动访问控制
            boolean isIpcontrol = false;
            String enableIpcontrol = systemConfig.get(IConfigPublicKey.IP_CONTROL_ENABLE);
            if(enableIpcontrol != null && "enable".equals(enableIpcontrol)){
            	isIpcontrol = true;
            }
            modelAndView.addObject("isIpcontrol", isIpcontrol);
        }
		else if(user.isAdministrator()){
		   userType = Constant.USER_TYPE.unit.name();
		   boolean isShowMobileMenu = SystemEnvironment.hasPlugin("mobileWap") || mobileMessageManager.isValidateMobileMessage();
		   modelAndView.addObject("isShowMobileMenu", isShowMobileMenu);
		   //是否启动访问控制
           boolean isIpcontrol = false;
           String enableIpcontrol = systemConfig.get(IConfigPublicKey.IP_CONTROL_ENABLE);
           if(enableIpcontrol != null && "enable".equals(enableIpcontrol)){
           	isIpcontrol = true;
           }
           modelAndView.addObject("isIpcontrol", isIpcontrol);
		}
		else if(user.isAuditAdmin()){
			userType = Constant.USER_TYPE.audit.name();
		}
		else if(user.isSecretAdmin()){
			userType = Constant.USER_TYPE.secret.name();
		}
		else{
            userType = Constant.USER_TYPE.user.name();

			Map<Constants.SpaceType, List<SpaceModel>> spacePath = this.spaceManager.getAccessSpace(memberId, accountId);
			modelAndView.addObject("spacePath", spacePath);

			List<String[]> spaceSort = spaceManager.getSpaceSort(memberId, accountId, user.getLocale(), false, spacePath);
			modelAndView.addObject("spaceSort", spaceSort);

			//if(spacePath.containsKey(Constants.SpaceType.department)){
			List<Long> managerDepartments = this.spaceManager.getCanManagerSpace(memberId);
			modelAndView.addObject("managerDepartments", managerDepartments);
			//}

            V3xOrgMember member = orgManager.getMemberById(memberId);
            //判断是否具有发送手机短信的权限(是否显示手机图标)
            boolean isShowMobile = false;
            if(member!=null && mobileMessageManager.isCanSend(memberId, user.getLoginAccount())){
                isShowMobile = true;
            }
            modelAndView.addObject("isCanSendSMS", isShowMobile);

            //是否需要播放声音
            // orgManager.loadEntityProperty(member);
            boolean isEnableMsgSound = false;
            String enableMsgSoundConfig = this.systemConfig.get(IConfigPublicKey.MSG_HINT);
            if(enableMsgSoundConfig != null){
                if("enable".equals(enableMsgSoundConfig)){
                    isEnableMsgSound = "true".equals(member.getProperty("enableMsgSound"));
                }
            }
            modelAndView.addObject("isEnableMsgSound", isEnableMsgSound);
            //消息查看后是否需要从消息框中移出  2009年7月23日 dongyj
            modelAndView.addObject("msgClosedEnable", "true".equals(member.getProperty("msgClosedEnable")));

            //随OA启动登陆到RTX caofei 2009年4月8日 add start code
            // 检查插件是否启动成功
            boolean isShowRtxClient = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("rtx"); // 检测插件是否启动
            // 检查RTX是否设置为自动启动
            modelAndView.addObject("isShowRtxClient", isShowRtxClient);
	        // 如果启动插件执行
	        if(isShowRtxClient){
	            modelAndView.addObject("isEnableRtxClient", !"false".equals(member.getProperty("enableRtxClient")));
	        }
            // 随OA启动登陆到RTX caofei 2009年4月8日 add start code
		}

		Object[] pwdExpirationInfo = (Object[])V3xShareMap.get("PwdExpirationInfo-" + user.getLoginName());
		modelAndView.addObject("pwdExpirationInfo", pwdExpirationInfo);

        //读出系统的标志  显示菜单
		modelAndView.addObject("userType", userType);
        modelAndView.addObject("showMenu", showMenu);

        String isModifyPass = SystemProperties.getInstance().getProperty("person.disable.modify.password");
        modelAndView.addObject("isModifyPass", isModifyPass);
        modelAndView.addObject("accountSymbol", accountSymbol);
        modelAndView.addObject("rootAccount", rootAccount);
		modelAndView.addObject("accountName", accountName);
		modelAndView.addObject("secondAccountName", secondAccountName);
		modelAndView.addObject("onlineNumber", onLineManager.getOnlineNumber());

		//新增内容，从内存中读取系统开关配置，密码过期期限
		int pwdExpirationTime = 0;
		String pwdExpirationTimeCfi = systemConfig.get(IConfigPublicKey.PWD_EXPIRATION_TIME);
		if(pwdExpirationTimeCfi != null){
			pwdExpirationTime = Integer.parseInt(pwdExpirationTimeCfi);
		}
		modelAndView.addObject("pwdExpirationTime", pwdExpirationTime);
		Date sysDate = new Date();
		modelAndView.addObject("sysDate", sysDate);
		return modelAndView;
	}

	@SuppressWarnings("unchecked")
	public ModelAndView left(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = null;
		User user = CurrentUser.get();
		long memberId = user.getId();
		if (user.isAdmin()) {
			modelAndView = new ModelAndView("common/sysleft");
		} else {
			modelAndView = new ModelAndView("common/left");

			// 快捷设置
			Map<String, List<ShortcutMenu>> shortcutMenusMap = shortcutManager.getShortcutMenus(user.getAccessSystemMenu(), memberId, user.getLoginAccount());
			modelAndView.addObject("shortcutMenus", shortcutMenusMap.get(ShortcutMenu.TYPE.shortcut.name()));
			modelAndView.addObject("toolsMenus", shortcutMenusMap.get(ShortcutMenu.TYPE.tools.name()));

			boolean isAllowedUserDefinedDepartmentSpace = this.spaceManager.isAllowedUserDefinedDepartmentSpace(user.getLoginAccount());
			modelAndView.addObject("isAllowedUserDefinedDepartmentSpace", isAllowedUserDefinedDepartmentSpace);
		}

		Map<Integer, List<AgentModel>> ma = (Map<Integer, List<AgentModel>>) AgentUtil.getUserAgentToMap(memberId)[1];
		modelAndView.addObject("agentFlag", !ma.isEmpty());

		Map<String, Integer> myInfo = mainManager.myInfo();
		modelAndView.addObject("myInfo", myInfo);

		return modelAndView;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/seeyon");

		return modelAndView;
	}

	public ModelAndView main(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/main");

		String mainFrameSrc = null;
		User member = CurrentUser.get();
        String loginName = member.getLoginName();
        //后台管理员
        if(orgManager.isAdministrator(loginName) || orgManager.isAuditAdmin(loginName) || orgManager.isSecretAdmin(loginName)){
			mainFrameSrc = "/main.do?method=showSystemNavigation";
		}
		modelAndView.addObject("mainFrameSrc", mainFrameSrc);
		return modelAndView;
	}

	/**
	 * 另存为组
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveAsTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();

		V3xOrgTeam team = new V3xOrgTeam();

		team.setName(request.getParameter("teamName"));
		team.setOwnerId(user.getId());
		team.setType(V3xOrgEntity.TEAM_TYPE_PERSONAL);
		team.setOrgAccountId(user.getLoginAccount());
/*		try {
			this.orgManagerDirect.addTeam(team);
		}
		catch (Exception e) {
			log.error("", e);
			super.rendJavaScript(response, "alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\");");
			return null;
		}*/

		String teamMemberIds = request.getParameter("memberIds");
		List<Long> members = new ArrayList<Long>();
		if(Strings.isNotBlank(teamMemberIds)){
			String[] memberIds = teamMemberIds.split(",");
			for (String string : memberIds) {
				long id = Long.parseLong(string);

				V3xOrgMember member = new V3xOrgMember();
				member.setId(id);
				members.add(member.getId());
			}
		}

		try {
			team.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			this.orgManagerDirect.addTeam(team);

			//Team(id, type, name, depId, memberArrayList, description)
			super.rendJavaScript(response, "parent.endSaveAsTeam('" + team.getId() + "');");
		}
		catch (Exception e) {
			log.error("", e);
			super.rendJavaScript(response, "alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\");");
		}

		return null;
	}

    /**
     * 菜单设置 - 显示
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView menuSetting(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("v3xmain/person/menuSetting");

        User user = CurrentUser.get();
        long memberId = user.getId();

        List<MenuSpareProfile> menuSpareProfileList = menuManager.getAllMenuSpareProfile(memberId, user.getLoginAccount());

        List<Menu> bizMenus = new ArrayList<Menu>();
        // 表单业务配置所挂接的菜单
        List<Long> domainIds = CommonTools.getUserDomainIds(user, orgManager);
		List<Menu> bizConfigMenus = this.menuManager.getAccessMenusThroughFormBizConfig(memberId, domainIds);
		CommonTools.addAllIgnoreEmpty(bizMenus, bizConfigMenus);
		// 业务表单所挂接的菜单
		Set<Long> bindMenuIdSet = FormBizConfigUtils.getAccessFirMenuIdsByMemberId(memberId).keySet();
		Set<Menu> bindMenuSet = new HashSet<Menu>();
		for (Long menuId : bindMenuIdSet) {
			Menu menu = menuManager.getMenuById(menuId);
			if(menu!=null)
				bindMenuSet.add(menu);
		}
		CommonTools.addAllIgnoreEmpty(bizMenus, bindMenuSet);

		if(CollectionUtils.isNotEmpty(bizMenus)) {
			Collections.sort(bizMenus);

			for(Menu menu : bizMenus) {
				MenuSpareProfile p = new MenuSpareProfile(menu.getId(), menu.getName());
				menuSpareProfileList.add(p);
			}
		}

        List<Long> allMenuSpareProfileIds = new ArrayList<Long>();
        Map<Long, MenuSpareProfile> menuProfileMaps = new HashMap<Long, MenuSpareProfile>();
        for (MenuSpareProfile menuSpare : menuSpareProfileList) {
            menuProfileMaps.put(menuSpare.getId(), menuSpare);
            allMenuSpareProfileIds.add(menuSpare.getId());
        }

        List<Long> menuProfileSettingIds = menuManager.getMenuProfile(memberId);
        List<Long> usableMenuProfileIds = new ArrayList<Long>();
        //尚未配置菜单
        if(menuProfileSettingIds.isEmpty()){
            usableMenuProfileIds = allMenuSpareProfileIds;
        }
        else{//筛选菜单是否在当前权限之内
            for(Long menuId : menuProfileSettingIds){
                if(menuProfileMaps.keySet().contains(menuId)){
                    usableMenuProfileIds.add(menuId);
                }
            }
        }

        modelAndView.addObject("menuProfileIds", usableMenuProfileIds);
        modelAndView.addObject("menuProfileMaps", menuProfileMaps);
        modelAndView.addObject("menuSpareProfileList", menuSpareProfileList);

        return modelAndView;
    }

    /**
     * 菜单设置 - 更新
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView updateMenuSetting(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        long memberId = CurrentUser.get().getId();
        //更新菜单设置
        boolean isRefreshTop = false;
        String menuSpareIds = request.getParameter("menuSpareIds");
        String isRefreshTopStr = request.getParameter("isRefreshTop");
        //表单应用绑定进行刷新菜单
        if(Strings.isNotBlank(isRefreshTopStr) && "true".equals(isRefreshTopStr)){
        	isRefreshTop = true;
        }

        if(Strings.isNotBlank(menuSpareIds) && !menuSpareIds.equals(request.getParameter("oldMenuSpareIds"))){
            List<Long> menuIds = new ArrayList<Long>();
            StringTokenizer token = new StringTokenizer(menuSpareIds, ",");
            while(token.hasMoreTokens()){
                menuIds.add(Long.parseLong(token.nextToken()));
            }
            menuManager.saveMenuProfile(memberId, menuIds);
            isRefreshTop = true;
        }

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constant.getValueFromMainRes("personalSetting.menu.ok")+"');");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/main.do?method=menuSetting&refreshTop=" + isRefreshTop);
    }

	/**
	 * 快捷方式与工具栏设置 - 显示
	 */
	public ModelAndView showShortcutSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("v3xmain/person/shortcut");

        //我的快捷设置
		User user = CurrentUser.get();
        long memberId = user.getId();
        long loginAccountId = user.getLoginAccount();

        //快捷设置
        Shortcut shortcut = shortcutManager.getShortcutByMemberId(memberId);
        List<ShortcutMenu> allShortcutMenus = shortcutManager.getMyAvailableShortcutMenus(user.getAccessSystemMenu(), memberId, loginAccountId);
        Map<String, List<ShortcutMenu>> shortcutMenusMap = shortcutManager.getShortcutMenus(allShortcutMenus, user.getAccessSystemMenu(), memberId, loginAccountId);
        List<ShortcutMenu> allToolsMenus = shortcutManager.getAllToolsMenus(user.getAccessSystemMenu());

        //过滤掉外部人员的个人考勤
        ShortcutMenu temp = new ShortcutMenu();
        if (!user.isInternal()) {
        	for(ShortcutMenu shortCutMenu : allShortcutMenus) {
        		if (shortCutMenu.getId() == 803) {
        			temp = shortCutMenu;
        		}
        	}
        	allShortcutMenus.remove(temp);
        	if (shortcut != null) {
        		List<String> cutList = new ArrayList<String>();
        		String[] shortCut = shortcut.getShortcutSet().split(",");
        		for(int i = 0 ; i < shortCut.length ; i++) {
        			if ("803".equals(shortCut[i])) {
        				continue;
        			}
        			cutList.add(shortCut[i]);
        		}
        		StringBuffer sb = new StringBuffer();
        		if (cutList.size() > 0) {
        			for(int i = 0 ; i<cutList.size() ; i++) {
        				sb.append(cutList.get(i));
        				sb.append(",");
        			}
        		}
        		sb.deleteCharAt(sb.length()-1);
        		shortcut.setShortcutSet(sb.toString());
        	}
        	
        }
        
        modelAndView.addObject("shortcut", shortcut);
        modelAndView.addObject("shortcutMenus", shortcutMenusMap.get(ShortcutMenu.TYPE.shortcut.name()));
        modelAndView.addObject("toolsMenus", shortcutMenusMap.get(ShortcutMenu.TYPE.tools.name()));
        modelAndView.addObject("allShortcutMenus", allShortcutMenus);
        modelAndView.addObject("allToolsMenus", allToolsMenus);

        return modelAndView;
	}

	/**
	 * 快捷方式与工具栏设置 - 更新
	 */
	public ModelAndView updateShortcutSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
        boolean isUpdateShortcut = true; //更新快捷设置
        boolean isUpdateTools = true; //更新工具栏
        long memberId = CurrentUser.get().getId();

        //更新快捷设置
        String idStr = request.getParameter("shortcutId");
		String shortcutSet = request.getParameter("shortcutSetStr");
		String toolsSet = request.getParameter("toolsSetStr");
        if(Strings.isNotBlank(idStr)){
            Shortcut shortcut = shortcutManager.getShortcut(Long.parseLong(idStr));
            if(shortcut != null){
                if(shortcutSet.equals(shortcut.getShortcutSet())){
                    isUpdateShortcut = false;
                }
                if(toolsSet.equals(shortcut.getToolsSet())){
                    isUpdateTools = false;
                }
                if(isUpdateShortcut || isUpdateTools){
                    shortcut.setShortcutSet(shortcutSet);
                    shortcut.setToolsSet(toolsSet);
                    shortcutManager.update(shortcut);
                }
            }
        }else{
            Shortcut shortcut = new Shortcut();
            shortcut.setIdIfNew();
            shortcut.setMemberId(memberId);
            shortcut.setShortcutSet(shortcutSet);
            shortcut.setToolsSet(toolsSet);
            shortcutManager.save(shortcut);
        }

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constant.getValueFromMainRes("shortcut.alert.successful")+"');");
        out.println("</script>");
		out.flush();
        return super.redirectModelAndView("/main.do?method=showShortcutSet&updateShortcut=" + isUpdateShortcut + "&updateTools=" + isUpdateTools);
	}



	//关于对话框
	public ModelAndView showAbout(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("common/about");

		modelAndView.addObject("productVersion", Functions.getVersion());
		modelAndView.addObject("buildId", "B" + Datetimes.format(SystemEnvironment.getProductBuildDate(), "yyMMdd") + "." + SystemEnvironment.getProductBuildVersion());
		modelAndView.addObject("productCategory", ProductInfo.getEditionA());
		modelAndView.addObject("maxOnline", ProductInfo.getMaxOnline());
		modelAndView.addObject("m1MaxOnline", ProductInfo.getM1MaxOnline());

		return modelAndView;
	}

	public ModelAndView navigation(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("common/navigation");

		return modelAndView;
	}

    //显示系统菜单导航页面
    public ModelAndView showSystemNavigation(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("common/systemNavigation");
        return modelAndView;
    }

    public ModelAndView departmentSpace(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	ModelAndView mv = new ModelAndView("sysMgr/space/departmentSpace");
        //TODO
    	String pagePath = null;
		EnumMap<PortletEntityProperty.PropertyName, String> pageParams = spaceManager.getPortletEntityProperty(pagePath);

		String ownerId = pageParams.get(PortletEntityProperty.PropertyName.ownerId);
    	long departmentId = user.getDepartmentId();
    	if(Strings.isNotBlank(ownerId)){
    		departmentId = Long.parseLong(ownerId);
    	}

    	List<V3xOrgMember> depManagerName = MainHelper.getDepManagerName(departmentId, orgManager);
    	List<V3xOrgMember> memberList = orgManager.getMembersByDepartment(departmentId, false);
    	mv.addObject("memberList", memberList);
    	mv.addObject("depManagerMember", depManagerName);

        return mv;
    }

	public ModelAndView departmentSpaceMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/space/departmentSpaceMore");
		Long userId = CurrentUser.get().getId();
		String deptStr = request.getParameter("departmentId");
		String spaceStr = request.getParameter("spaceId");
		Long deptId = null;
		Long spaceId = null;
		SpaceFix space = null;
		if (Strings.isNotBlank(deptStr)) {
			deptId = Long.parseLong(deptStr);
		}
		if (Strings.isNotBlank(spaceStr)) {
			spaceId = Long.parseLong(spaceStr);
		}

		List<PeopleRelate> myMemberList = new ArrayList<PeopleRelate>();
		V3xOrgDepartment myDept = null;
		List<V3xOrgMember> depManagersList = new ArrayList<V3xOrgMember>();
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		if (spaceId != null) {
			space = spaceManager.getSpace(spaceId);
			depManagersList = spaceManager.getSpaceMemberBySecurity(spaceId, 1);
			List<Object[]> _issueAreas = this.spaceManager.getSecuityOfSpace(spaceId);
			StringBuffer entityInfos = new StringBuffer();
			for (Object[] arr : _issueAreas) {
				entityInfos.append(StringUtils.join(arr, "|") + ",");
			}
			Set<V3xOrgMember> memberSet = this.orgManager.getMembersByTypeAndIds(entityInfos.substring(0, entityInfos.length() - 1));
			if (memberSet != null && memberSet.size() > 0) {
				memberList = new ArrayList<V3xOrgMember>(memberSet);
			}
		} else {
			try {
				myDept = orgManager.getDepartmentById(deptId);
			} catch (Exception e) {
				logger.error("", e);
			}
			depManagersList = MainHelper.getDepManagerName(deptId, orgManager);
			memberList = orgManager.getMembersByDepartment(deptId, true);
		}

		if (CollectionUtils.isNotEmpty(depManagersList)) {
			for (V3xOrgMember member : depManagersList) {
				V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
				myMemberList.add(getPeopleRelateInfo(userId, member, dept));
			}
			memberList.removeAll(depManagersList);
		}

		if (CollectionUtils.isNotEmpty(memberList)) {
			for (V3xOrgMember member : memberList) {
				V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
				myMemberList.add(getPeopleRelateInfo(userId, member, dept));
			}
		}

		Map<Long, List<PeopleRelate>> membersList = new LinkedHashMap<Long, List<PeopleRelate>>();
		if (deptId != null) {
			List<V3xOrgDepartment> deptList = orgManager.getChildDepartments(deptId, false);
			parentDepartments = new ArrayList<V3xOrgDepartment>();
			this.setParentDepartments(deptList, deptId);
			if (parentDepartments.size() > 0) {
				for (V3xOrgDepartment dept : parentDepartments) {
					List<V3xOrgMember> members = orgManager.getMembersByDepartment(dept.getId(), true);
					List<PeopleRelate> deptMemberList = new ArrayList<PeopleRelate>();
					List<V3xOrgMember> subDepManagersList = MainHelper.getDepManagerName(dept.getId(), orgManager);
					if (CollectionUtils.isNotEmpty(subDepManagersList)) {
						for (V3xOrgMember member : subDepManagersList) {
							deptMemberList.add(getPeopleRelateInfo(userId, member, dept));
						}
						members.removeAll(subDepManagersList);
					}

					if (CollectionUtils.isNotEmpty(members)) {
						for (V3xOrgMember member : members) {
							deptMemberList.add(getPeopleRelateInfo(userId, member, dept));
						}
					}
					membersList.put(dept.getId(), deptMemberList);
				}
			}
		}

		mv.addObject("isSpace", space != null ? true : false);
		mv.addObject("space", space);
		mv.addObject("myDept", myDept);
		mv.addObject("myMemberList", myMemberList);
		mv.addObject("membersList", membersList);
		return mv;
	}

	private PeopleRelate getPeopleRelateInfo(Long userId, V3xOrgMember member, V3xOrgDepartment dept) throws Exception, BusinessException {
		PeopleRelate p = new PeopleRelate();
		p.setRelatedMemberId(userId);
		p.setRelateMemberId(member.getId());
		p.setRelateMemberName(member.getName());
		p.setRelateMemberDept(dept != null ? dept.getName() : "");
		p.setRelateMemberEmail(member.getEmailAddress());
		p.setRelateMemberTel(member.getProperty("officeNum"));
		p.setRelateMemberHandSet(member.getTelNumber());
		StaffInfo staff = staffInfoManager.getStaffInfoById(member.getId());
		if (staff != null) {
			p.setRelateImageId(staff.getImage_id());
			p.setRelateImageDate(staff.getImage_datetime());
		}
		return p;
	}
	
    /**
     * 我所在的部门下的子部门排序
     * @param list 子部门集合
     * @param id 我的部门ID
     */
    private void setParentDepartments(List<V3xOrgDepartment> list, Long id) {
        try {
            for (V3xOrgDepartment dept : list) {
            	V3xOrgDepartment parent = orgManager.getParentDepartment(dept.getId());
                if( parent != null && parent.getId().equals(id)){
                	parentDepartments.add(dept);
                	this.setParentDepartments(list, dept.getId());
                }
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
	}

    /**
     * 进入关联人员、部门人员更多页面，默认显示部门人员部分
     * @param request
     * @param response
     * @return
     */
    public ModelAndView departmentMore(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("peoplerelate/homeEntry");
    	String deptStr = request.getParameter("departmentId");
    	String spaceStr = request.getParameter("spaceId");
    	V3xOrgDepartment dept = null;
    	SpaceFix space = null;
    	Long spaceId = null;
    	Long deptId = null;
    	if (deptStr != null) {
    		deptId = Long.parseLong(deptStr);
    	}
    	if (spaceStr != null) {
    		spaceId = Long.parseLong(spaceStr);
    	}
    	if (spaceId != null) {
    		space = spaceManager.getSpace(spaceId);
    	} else {
    		dept = new V3xOrgDepartment();
    		try {
    			dept = orgManager.getDepartmentById(deptId);
    		} catch (Exception e) {
    			logger.error("获取部门失败", e);
    		}
    	}
        mav.addObject("isRelateOrDept", "department");
        mav.addObject("isSpace", space != null ? true : false);
        mav.addObject("dept", dept);
        mav.addObject("space", space);
        return mav;
    }


    /**
     * 个人信息设置 - 显示
     */
    public ModelAndView personalInfo(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/person/personalInfo");
        Long memberId = CurrentUser.get().getId();
		StaffInfo staff = staffInfoManager.getStaffInfoById(memberId);
		modelAndView.addObject("staff", staff);
		if (null != staff) {
			if (Strings.isNotBlank(staff.getSelf_image_name())) {
				if(staff.getSelf_image_name().startsWith("fileId")){
					modelAndView.addObject("image", 0);
				}else{
					modelAndView.addObject("image", 1);
				}
			}
 		}
        V3xOrgMember member = orgManager.getMemberById(memberId);
        ContactInfo contactInfo = staffInfoManager.getContactInfoById(memberId);
        modelAndView.addObject("member", member);
        orgManager.loadEntityProperty(member);
        String officeNum = member.getProperty("officeNum");
        String enableMsgSound = member.getProperty("enableMsgSound");//是否启动消息声音提示
        String msgCloseEnable = member.getProperty("msgClosedEnable");//消息查看后删除
        // 随OA启动登陆TX CAOFEI CAOFEI 2009-4-8 start add code
        String enableRtxClient = member.getProperty("enableRtxClient");//是否随OA启动登陆RTX
        if(enableRtxClient == null ){
        	enableRtxClient="true";
        }
        if(IndexInitConfig.hasLuncenePlugIn())
        {
        	String setShowIndexSummary=member.getProperty(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY);
        	modelAndView.addObject(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY, setShowIndexSummary);
        }
        // 随OA启动登陆TX CAOFEI CAOFEI 2009-4-8 end add code
        modelAndView.addObject("officeNum", officeNum);//个人情况，包括家庭电话
        modelAndView.addObject("enableMsgSound", enableMsgSound);//是否起用消息声音提示
        modelAndView.addObject("msgClosedEnable", msgCloseEnable);//消息查看后是否删除

        modelAndView.addObject("enableRtxClient", enableRtxClient);//随OA启动登陆RTX caofei 2009-4-8 start add code
        modelAndView.addObject("contactInfo", contactInfo);//个人情况，包括家庭电话
        // 启用消息声音提示
        boolean systemMsgSoundEnable = false;
        String enableMsgSoundConfig = this.systemConfig.get(IConfigPublicKey.MSG_HINT);
        if(enableMsgSoundConfig != null){
            systemMsgSoundEnable = "enable".equals(enableMsgSoundConfig);
        }
        modelAndView.addObject("systemMsgSoundEnable", systemMsgSoundEnable);

        //检查插件是否启动成功 CAOFEI 2009-4-8 start add code
        boolean isShowRtxClient = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("rtx"); // 检测插件是否启动
        modelAndView.addObject("isShowRtxClientPersonal", isShowRtxClient);
        // 随OA启动登陆RTX
        boolean systemRtxClientEnable = false;
        if(member.getProperty("enableRtxClient") !=null ){
        	systemRtxClientEnable ="true".equals(member.getProperty("enableRtxClient"));
        }
        modelAndView.addObject("systemRtxClientEnable", systemRtxClientEnable);
        String extendConfig = member.getProperty("extendConfig");
        if(Strings.isNotBlank(extendConfig)){
        	modelAndView.addObject("extendConfig", extendConfig);
        }
        //检查插件是否启动成功 CAOFEI CAOFEI 2009-4-8 end add code
        return modelAndView;
    }

    /**
     * 个人信息设置 - 更新
     */
    public ModelAndView updatePersonalInfo(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Long memberId = CurrentUser.get().getId();
        V3xOrgMember member = orgManager.getMemberById(memberId);
        
        //为事件调用记录修改前的人员
        V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
        BeanUtils.copyProperties(memberBeforeUpdate, member);
        
        StaffInfo staffinfo = staffInfoManager.getStaffInfoById(memberId);
		String imageName = request.getParameter("filename");
		if (staffinfo == null) {
			staffinfo = new StaffInfo();
			staffinfo.setSelf_image_name(imageName);
			staffinfo.setOrg_member_id(memberId);
			staffInfoManager.addStaffInfo(staffinfo);
		} else {
			staffinfo.setSelf_image_name(imageName);
			staffInfoManager.updateStaffInfo(staffinfo);
		}
        Locale locale = LocaleContext.parseLocale(request.getParameter("primaryLanguange"));
        member.setLocale(locale);

        member.setTelNumber(request.getParameter("telNumber"));
        member.setEmailAddress(request.getParameter("email"));
        member.setDescription(request.getParameter("comment")) ;

        if(Strings.isNotBlank(request.getParameter("telephone"))){
        	member.setProperty("officeNum", request.getParameter("telephone"));
        }else{
        	member.setProperty("officeNum", "");
        }
        //启用消息声音提示
        if(Strings.isNotBlank( request.getParameter("enableMsgSound"))){
        	member.setProperty("enableMsgSound", request.getParameter("enableMsgSound"));
        }else{
        	member.setProperty("enableMsgSound", "");
        }
        //启用消息查看后删除
        if(Strings.isNotBlank(request.getParameter("msgClosedEnable"))){
        	 member.setProperty("msgClosedEnable", request.getParameter("msgClosedEnable"));
        }else{
        	 member.setProperty("msgClosedEnable", "");
        }
        if(Strings.isNotBlank(request.getParameter("enableRtxClient"))){
        	//随OA启动登陆RTX CAOFEI 2009-4-8 add code
            member.setProperty("enableRtxClient", request.getParameter("enableRtxClient"));
        }else{
        	 member.setProperty("enableRtxClient", "");
        }
        if(IndexInitConfig.hasLuncenePlugIn()){
        	String isIndexShow=request.getParameter(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY);
        	if(StringUtils.isBlank(isIndexShow)){isIndexShow="true";};
        	member.setProperty(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY,isIndexShow);
        }

        if(Strings.isNotBlank(request.getParameter("extendConfig"))){
        	member.setProperty("extendConfig", request.getParameter("extendConfig"));
        }else{
        	member.setProperty("extendConfig", "false");
        }

        ContactInfo contact = staffInfoManager.getContactInfoById(memberId);
        if(contact == null){
            ContactInfo contactInfo = new ContactInfo();
            bind(request,contactInfo);
            contactInfo.setMember_id(memberId);
            staffInfoManager.addContactInfo(contactInfo,member);
        }
        else{
            bind(request,contact);
            contact.setMember_id(memberId);
            staffInfoManager.updateContactInfo(contact,member);
        }

    	updateIndexManager.update(memberId,ApplicationCategoryEnum.organization.getKey());
    	
    	//触发事件
		eventListener.updateMember(memberBeforeUpdate, member);

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+com.seeyon.v3x.system.Constants.getString4CurrentUser("system.manager.ok")+"')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/main.do?method=personalInfo");
    }

    /**
     * 存储空间使用
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView storeSpaceLook(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/person/storeSpaceLook");
        long userId = CurrentUser.get().getId();
        DocStorageSpace dss = this.docSpaceManager.getDocSpaceByUserId(userId);
        DocSpaceVO vo = new DocSpaceVO(dss);
        modelAndView.addObject("vo", vo);
        return modelAndView;
    }

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@SetContentType
    public ModelAndView vistaDown(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	String str = "Windows Registry Editor Version 5.00\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\New Windows]\r\n" +
    			"\"PopupMgr\"=\"no\"\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\0]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\1]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\2]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\4]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range1]\r\n" +
    			"\"http\"=dword:00000002\r\n" +
    			"\":Range\"=\"" + request.getServerName() + "\"\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\New Windows\\Allow]\r\n" +
    			"\"" + request.getServerName() + "\"=hex:\r\n" +
    			"[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Internet Explorer\\Main\\FeatureControl\\FEATURE_WINDOW_RESTRICTIONS]\r\n" +
    			"\"iexplore.exe\"=dword:00000000\r\n" +
    			"\"explorer.exe\"=dword:00000000";

    	response.setContentType("application/x-msdownload; charset=UTF-8");
    	response.setHeader("Content-disposition", "attachment;filename=\"IE.reg\"");

    	OutputStream out = null;
    	try {
    		out = response.getOutputStream();

    		IOUtils.write(str, out);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.debug("用户关闭下载窗口: " + e.getMessage());
			}
			else{
				log.error("", e);
			}
		}
		finally{
			IOUtils.closeQuietly(out);
		}

    	return null;
    }
	
	@SetContentType
	@NeedlessCheckLogin
    public ModelAndView ieSetDown(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		String ipRegex = "(localhost)|((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3})";
		Pattern pattern = Pattern.compile(ipRegex);
		Matcher matcher = pattern.matcher(request.getServerName());
		boolean isIp = matcher.matches();
		String range = "";
		if (isIp) {
			range = "[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\" + request.getServerName() + "]\r\n"
					+ "\"http\"=dword:00000002\r\n" + "\":Range\"=\"" + request.getServerName() + "\"\r\n" + "\"https\"=dword:00000002\r\n" + "\":Range\"=\""
					+ request.getServerName() + "\"\r\n";
		} else {
			String str = request.getServerName();
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";
			if (str.indexOf(".") > -1) {
				s1 = str.substring(str.lastIndexOf("."));
				s2 = str.substring(0, str.lastIndexOf("."));
				s3 = s2.substring(s2.lastIndexOf(".") + 1);
				s4 = s2.substring(0, s2.lastIndexOf("."));
			} else {
				s4 = str;
			}
			range = "[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Domains\\" + (str.indexOf(".") > -1 ? (s3 + s1 + "\\") : "") + s4 + "]\r\n"
					+ "\"http\"=dword:00000002\r\n" + "\"https\"=dword:00000002\r\n";
		}

    	String str = "Windows Registry Editor Version 5.00\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\New Windows]\r\n" +
    			"\"PopupMgr\"=\"no\"\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\0]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\1]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\2]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"\"1001\"=dword:00000001\r\n" +
    			"\"1200\"=dword:00000000\r\n" +
    			"\"1405\"=dword:00000000\r\n" +
    			"\"1201\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3]\r\n" +
    			"\"1609\"=dword:00000000\r\n" +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\4]\r\n" +
    			"\"1609\"=dword:00000000\r\n" + range +
    			"[HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\New Windows\\Allow]\r\n" +
    			"\"" + request.getServerName() + "\"=hex:\r\n" +
    			"[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Internet Explorer\\Main\\FeatureControl\\FEATURE_WINDOW_RESTRICTIONS]\r\n" +
    			"\"iexplore.exe\"=dword:00000000\r\n" +
    			"\"explorer.exe\"=dword:00000000";

    	response.setContentType("application/x-msdownload; charset=UTF-8");
    	response.setHeader("Content-disposition", "attachment;filename=\"IE.reg\"");
    	
    	OutputStream out = null;
    	try {
    		out = response.getOutputStream();

    		IOUtils.write(str, out);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.debug("用户关闭下载窗口: " + e.getMessage());
			}
			else{
				log.error("", e);
			}
		}
		finally{
			IOUtils.closeQuietly(out);
		}

    	return null;
    }

	@SetContentType
    public ModelAndView officeDown(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	response.setContentType("application/x-msdownload; charset=UTF-8");
    	response.setHeader("Content-disposition", "attachment;filename=\"Office.zip\"");

    	String filename = SystemEnvironment.getA8ApplicationFolder() + File.separator +"common" + File.separator + "office" + File.separator + "HandWrite.cab";

    	OutputStream out = null;
    	InputStream in = null;
    	try {
    		in = new FileInputStream(new File(filename));
    		out = response.getOutputStream();

    		IOUtils.copy(in, out);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.debug("用户关闭下载窗口: " + e.getMessage());
			}
			else{
				log.error("", e);
			}
		}
		finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
    	return null;
    }

	@SetContentType
    public ModelAndView certDown(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String certFilePath = SystemEnvironment.getA8ApplicationFolder() + File.separator + "USER-DATA" + File.separator + "https" + File.separator + "ca.crt";
        File file = new File(certFilePath);
        if(!file.exists()){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + Constant.getValueFromMainRes("fileupload.document.FileNoFound") + "');");
            out.println("</script>");
            return null;
        }

        response.setContentType("application/x-msdownload; charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=\"ca.crt\"");

        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();

            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
                log.debug("用户关闭下载窗口: " + e.getMessage());
            }
            else{
                log.error("", e);
            }
        }
        finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        return null;
    }

	public void setExtendedMessageSystemManager(
			ExtendedMessageSystemManager extendedMessageSystemManager) {
		this.extendedMessageSystemManager = extendedMessageSystemManager;
	}

	/**
	 * 通用提醒窗口，可以实现不再提醒
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView commonAlert(HttpServletRequest request,
	           HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("common/alert");
		return modelAndView ;
	}
	
	@SetContentType
	@NeedlessCheckLogin
	public ModelAndView loadPortalIframeIDJS(HttpServletRequest request,
	           HttpServletResponse response) throws Exception{
		response.setContentType("text/javascript;charset=UTF-8");
		String iframeIdString=SystemProperties.getInstance().getProperty("portal.iframe.id");
		if(Strings.isBlank(iframeIdString)){
			iframeIdString = "top.frames['frame_A8']";
		}
		
		String ETag = String.valueOf(iframeIdString.hashCode());
		if(WebUtil.checkEtag(request, response, ETag)){ //有缓存
			return null;
		}
		
		WebUtil.writeETag(request, response, ETag);
		
		PrintWriter out=response.getWriter();
		out.println("var portalOfA8IframeStr = \"" + Strings.escapeJavascript(iframeIdString) + "\";");
		out.flush();
		out.close();
		return null ;
	}

}