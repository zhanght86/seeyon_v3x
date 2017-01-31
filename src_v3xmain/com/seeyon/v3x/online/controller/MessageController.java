/**
 * 
 */
package com.seeyon.v3x.online.controller;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.videoconference.manager.CreateVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.DeleteVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.JoinVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.StartVideoConferenceManagerCAP;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.v3x.common.constants.Constants.LoginUserState;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.online.OnlineUser.SecondePost;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.Constants;
import com.seeyon.v3x.common.usermessage.UserMessageFilterConfigManager;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.usermessage.domain.UserHistoryMessage;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystem;
import com.seeyon.v3x.common.usermessage.extended.ExtendedMessageSystemManager;
import com.seeyon.v3x.common.usermessage.pipeline.MessagePipeline;
import com.seeyon.v3x.common.usermessage.pipeline.MessagePipelineManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.mail.manager.MessageMailManager;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.online.OffLineUserModel;
import com.seeyon.v3x.online.OnlineAccountModel;
import com.seeyon.v3x.online.OnlineUserModel;
import com.seeyon.v3x.online.manager.WIMManager;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.videoconference.manager.VideoConferenceManager;
import com.seeyon.v3x.videoconference.util.ParseXML;
import com.seeyon.v3x.videoconference.util.VideoConfUtil;
/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-28
 */
public class MessageController extends BaseController {
    private static final Log log = LogFactory.getLog(MessageController.class);

    private UserMessageManager userMessageManager;
    
    private OnLineManager onLineManager; 

    private OrgManager orgManager;
    
    private FileManager fileManager;
    
    private MobileMessageManager mobileMessageManager;
    
    private UserMessageFilterConfigManager userMessageFilterConfigManager;
    
    private MessageMailManager messageMailManager;
    
    private InquiryManager inquiryManager;
    
    private BbsBoardManager bbsBoardManager;
    
    private BulDataManager bulDataManager;
    
    private NewsDataManager newsDataManager;
    
    private SpaceManager spaceManager;
    
    private MessagePipelineManager messagePipelineManager;
    
    private ExtendedMessageSystemManager extendedMessageSystemManager;
    
    private PeopleRelateManager peoplerelateManager;
    
    private OrgManagerDirect orgManagerDirect;
    
    private AddressBookManager addressBookManager;
    
    private SystemConfig systemConfig;
    
    private ProjectManager projectManager;
    
    private WIMManager wimManager;
	
	private AffairManager affairManager;
	
	private ColManager colManager;
	
	private VideoConferenceManager videoConferenceManager;
    
    public void setWimManager(WIMManager wimManager) {
		this.wimManager = wimManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setVideoConferenceManager(
			VideoConferenceManager videoConferenceManager) {
		this.videoConferenceManager = videoConferenceManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
    
    public void setOnLineManager(OnLineManager onLineManager) {
        this.onLineManager = onLineManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }   

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
        this.mobileMessageManager = mobileMessageManager;
    }
    
    public void setUserMessageFilterConfigManager(
            UserMessageFilterConfigManager userMessageFilterConfigManager) {
        this.userMessageFilterConfigManager = userMessageFilterConfigManager;
    }
    
    public void setMessageMailManager(MessageMailManager messageMailManager) {
        this.messageMailManager = messageMailManager;
    }

    public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
        this.bbsBoardManager = bbsBoardManager;
    }

    public void setNewsDataManager(NewsDataManager newsDataManager) {
        this.newsDataManager = newsDataManager;
    }

    public void setBulDataManager(BulDataManager bulDataManager) {
        this.bulDataManager = bulDataManager;
    }

    public void setInquiryManager(InquiryManager inquiryManager) {
        this.inquiryManager = inquiryManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
    
    public void setMessagePipelineManager(
			MessagePipelineManager messagePipelineManager) {
		this.messagePipelineManager = messagePipelineManager;
	}

	public void setExtendedMessageSystemManager(
			ExtendedMessageSystemManager extendedMessageSystemManager) {
		this.extendedMessageSystemManager = extendedMessageSystemManager;
	}
	
	public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager) {
		this.peoplerelateManager = peoplerelateManager;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}
	
	@Override
    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return null;
    }
	
	/**
	 * 精灵消息提示页面
	 */
	public ModelAndView A8geniusMsgWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/message/A8geniusMsgWindow");
		User user = CurrentUser.get();
		
		//是否需要播放声音
		boolean isEnableMsgSound = false;
		V3xOrgMember member = orgManager.getMemberById(user.getId());
        String enableMsgSoundConfig = systemConfig.get(IConfigPublicKey.MSG_HINT);
        if(enableMsgSoundConfig != null){
            if("enable".equals(enableMsgSoundConfig)){
                isEnableMsgSound = "true".equals(member.getProperty("enableMsgSound"));                    
            }
        }
        mav.addObject("isEnableMsgSound", isEnableMsgSound);
        
        //消息查看后是否从消息框中移出
        mav.addObject("msgClosedEnable", "true".equals(member.getProperty("msgClosedEnable")));
        
		return mav;
	}
	
	/**
	 * 在线人员窗口
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView showOnlineUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/message/onlineIM");
		User user = CurrentUser.get();
		boolean isAdmin = user.isAdmin();

		if(!isAdmin){
			//是否需要播放声音
			boolean isEnableMsgSound = false;
			V3xOrgMember member = orgManager.getMemberById(user.getId());
			String enableMsgSoundConfig = this.systemConfig.get(IConfigPublicKey.MSG_HINT);
			if(enableMsgSoundConfig != null){
				if("enable".equals(enableMsgSoundConfig)){
					isEnableMsgSound = "true".equals(member.getProperty("enableMsgSound"));
				}
			}
			mav.addObject("isEnableMsgSound", isEnableMsgSound);
			
			int deptOnlineNumber = 0;
    		List<V3xOrgMember> deptMembers = orgManager.getMembersByDepartment(user.getDepartmentId(), false);
            if(CollectionUtils.isNotEmpty(deptMembers)){
                for(V3xOrgMember m : deptMembers){
                	if(onLineManager.isOnline(m.getLoginName())){
                		deptOnlineNumber++;
                	}
                }
        	}
            mav.addObject("deptOnlineNumber", deptOnlineNumber);//部门在线人数
		}
		mav.addObject("allOnlineNumber", onLineManager.getOnlineNumber());//整个集团在线总人数
		mav.addObject("isAdmin", user.isAdmin());
		
		return mav;
	}
	
	/**
	 * 在线人员部门列表
	 */
	public ModelAndView showOnlineUserTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/message/onlineTree");
		User user = CurrentUser.get();
		Long accountId = user.getAccountId();
		boolean isAdmin = user.isAdmin();
		
    	//能够访问的的单位列表
    	List<V3xOrgAccount> accountList = orgManager.accessableAccounts(user.getId());
    	Collections.sort(accountList, CompareSortEntity.getInstance());
    	Long currentAccountId = this.getCurrentAccountId(request, user, accountList);
        
        //是否需要显示单位切换
        boolean isShowAccountSwitch = (Boolean)(SysFlag.frontPage_online_showAccountSwitch.getFlag());
        
		V3xOrgAccount account = null;
		if (isShowAccountSwitch) {
			account = orgManagerDirect.getAccountById(currentAccountId);

			// 系统没建单位的时候，取集团
			if (account == null) {
				account = accountList.get(0);
				currentAccountId = account.getId();
			}
		} else {
			for (V3xOrgAccount acc : accountList) {
				if (!acc.getIsRoot()) {
					account = acc;
					break;
				}
			}
		}
        
		List<OnlineAccountModel> onlineAccountList = new ArrayList<OnlineAccountModel>();
		if (isShowAccountSwitch) {
			List<String> rootAccountIds = new ArrayList<String>();
			for (V3xOrgAccount acc : accountList) {
				if (acc.getSuperior() == -1) {
					rootAccountIds.add(String.valueOf(acc.getId()));
				}
				
				Long accId = acc.getId();

				int onlineNum = acc.getIsRoot() ? onLineManager.getOnlineNumber() : onLineManager.getOnlineNumber(accId);
				int num = orgManager.getAllMembers(accId).size();

				OnlineAccountModel onlineAccount = new OnlineAccountModel();
				onlineAccount.setAccount(acc);
				onlineAccount.setId(acc.getId());
				onlineAccount.setSuperior(acc.getSuperior());
				onlineAccount.setName(acc.getName() + "(" + onlineNum + "/" + num + ")");
				
				onlineAccountList.add(onlineAccount);
			}
			
			//1、集团管理员
			//2、单位管理员或者个人, 并且在集团根下
			if (user.isGroupAdmin() || (user.isAdministrator() && CollectionUtils.isEmpty(rootAccountIds)) || 
					(!user.isAdmin() && user.isInternal() && CollectionUtils.isEmpty(rootAccountIds))) {
				boolean isGroupAccessable = Functions.isGroupAccessable(user.getLoginAccount());
				if(isGroupAccessable) {
					V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();
					
					rootAccountIds.add(String.valueOf(rootAccount.getId()));
					
					int onlineNum = onLineManager.getOnlineNumber();
					int num = orgManager.getAllMembers(rootAccount.getId()).size();
					
					OnlineAccountModel onlineAccount = new OnlineAccountModel();
					onlineAccount.setAccount(rootAccount);
					onlineAccount.setId(rootAccount.getId());
					onlineAccount.setSuperior(rootAccount.getSuperior());
					onlineAccount.setName(rootAccount.getName() + "(" + onlineNum + "/" + num + ")");

					onlineAccountList.add(onlineAccount);
				}
				
			}
			
			mav.addObject("rootAccountIds", rootAccountIds);
			mav.addObject("accountList", onlineAccountList);
		}
        
        //是否需要选中默认部门
        boolean isSameAccount = isAdmin ? false : currentAccountId.equals(accountId);
        
        List<WebV3xOrgDepartment> deptList = null;
        List<V3xOrgEntity> internalRight = null;
        if(user.isInternal()){
        	if (isShowAccountSwitch && account.getIsRoot()) {
        		List<V3xOrgAccount> childAccountList = orgManager.getChildAccount(account.getId(), false, true);
        		Collections.sort(childAccountList, CompareSortEntity.getInstance());
        		mav.addObject("isRoot", true);
        		mav.addObject("childAccountList", childAccountList);
        	} else {
        		deptList = new ArrayList<WebV3xOrgDepartment>();
        		List<V3xOrgDepartment> dList = orgManager.getAllDepartments(account.getId(), false, false);
        		Map<String,V3xOrgDepartment> deptPathMap = new HashMap<String, V3xOrgDepartment>();
        		for(V3xOrgDepartment d : dList){
        			deptPathMap.put(d.getPath(), d);
        		}
        		Collections.sort(dList, CompareSortEntity.getInstance());
        		for(V3xOrgDepartment dept : dList){
        			if(!isAdmin && !dept.getIsInternal() && !OuterWorkerAuthUtil.canAccessOuterDep(user.getId(), user.getDepartmentId(), user.getLoginAccount(), dept, orgManager)){
        				continue;//防护不能查看的外部门
        			}
        			
        			V3xOrgDepartment pdept = deptPathMap.get(dept.getParentPath());
        			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
        			webdept.setV3xOrgDepartment(dept);
        			if (null != pdept) {
        				webdept.setParentId(pdept.getId());
        				webdept.setParentName(pdept.getName());
        			}
        			deptList.add(webdept);
        		}
        	}
        }else{
        	deptList = OuterWorkerAuthUtil.getOuterDeptList(mav, user, currentAccountId, orgManager);
        	internalRight = orgManager.getExternalMemberWorkScope(user.getId(), false);
        }
        
        if (CollectionUtils.isNotEmpty(deptList)) {
        	Map<String, Integer> deptOnlineNumMap = new HashMap<String, Integer>();
            Map<String, Integer> deptNumMap = new HashMap<String, Integer>();
            Map<String,V3xOrgDepartment> deptMap = new HashMap<String,V3xOrgDepartment>(deptList.size());
            for(WebV3xOrgDepartment wod:deptList){
    			deptMap.put(wod.getV3xOrgDepartment().getPath(), wod.getV3xOrgDepartment());
    		}
            Map<Long, Set<OnlineUserModel>> deptOnlineList= this.getDepartmentOnlineUser(deptMap, account, user, internalRight);
            
            for(WebV3xOrgDepartment wdp : deptList){
            	Long deptId = wdp.getV3xOrgDepartment().getId();
				int deptOnlineNum = deptOnlineList.get(deptId) == null ? 0 : deptOnlineList.get(deptId).size();
            	int deptNum = orgManager.getMembersByDepartment(deptId, false, deptMap).size();
            	deptOnlineNumMap.put(deptId.toString(), deptOnlineNum);
            	deptNumMap.put(deptId.toString(), deptNum);
            }
            mav.addObject("deptOnlineNumMap", deptOnlineNumMap);
            mav.addObject("deptNumMap", deptNumMap);
        }
        
        mav.addObject("isShowAccountSwitch", isShowAccountSwitch);
        mav.addObject("currentAccountId", currentAccountId);
		mav.addObject("isSameAccount", isSameAccount);
		mav.addObject("account", account);
		mav.addObject("deptList", deptList);
    	return mav;
	}
	
	/**
	 * 在线人员讨论组列表
	 */
	public ModelAndView showOnlineUserTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/message/onlineTeam");
		User user = CurrentUser.get();

		Map<String, Integer> onlineNumMap = new HashMap<String, Integer>();
		Map<String, Integer> numMap = new HashMap<String, Integer>();
		
		// 人员的部门列表
		List<V3xOrgDepartment> deptList = orgManager.getDepartmentsByUser(user.getId());

		List<V3xOrgEntity> internalRight = null;
		if (!user.isInternal()) {
			internalRight = orgManager.getExternalMemberWorkScope(user.getId(), false);
		}

		for (V3xOrgDepartment dept : deptList) {
			Long deptId = dept.getId();
			int deptOnlineNum = this.getOnlineUserListByDepartment(deptId, user, internalRight).size();
			int deptNum = orgManager.getMembersByDepartment(deptId, false).size();
			onlineNumMap.put(deptId.toString(), deptOnlineNum);
			numMap.put(deptId.toString(), deptNum);
		}

		// 人员的组列表
		List<V3xOrgTeam> teamList = wimManager.getAllDiscussTeam(user.getId());

		for (V3xOrgTeam team : teamList) {
			
			Long teamId = team.getId();
			int teamOnlineNum = 0;
			int teamNum = 0;
			List<Long> memberIds = new UniqueList<Long>();
			memberIds.addAll(team.getAllMembers());
			memberIds.addAll(team.getAllRelatives());
			for (Long memberId : memberIds) {
				V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
				if (member != null && member.isValid()) {
					teamNum++;
					if (onLineManager.isOnline(member.getLoginName())) {
						teamOnlineNum++;
					}
				}
			}
			onlineNumMap.put(teamId.toString(), teamOnlineNum);
			numMap.put(teamId.toString(), teamNum);
		}

		mav.addObject("deptList", deptList);
		mav.addObject("teamList", teamList);
		mav.addObject("onlineNumMap", onlineNumMap);
		mav.addObject("numMap", numMap);
		
		return mav;
	}
	
	/**
	 * 在线人员列表
	 */
	public ModelAndView showOnlineUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/message/onlineList");
    	User user = CurrentUser.get();
    	boolean isAdmin = user.isAdmin();
    	
    	//能够访问的的单位列表
    	List<V3xOrgAccount> accountList = orgManager.accessableAccounts(user.getId());
    	Long currentAccountId = this.getCurrentAccountId(request, user, accountList);
    	
    	List<V3xOrgEntity> internalRight = null;
        if(!user.isInternal()){
        	internalRight = orgManager.getExternalMemberWorkScope(user.getId(),false);
        }
        
        //选择的单位是否为集团根
        V3xOrgAccount selectAccount = orgManagerDirect.getAccountById(NumberUtils.toLong(request.getParameter("currentAccountId")));
        boolean isRoot = selectAccount != null && selectAccount.getIsRoot();
        //是否为全集团范围内查询
        boolean isRootQuery = "true".equals(request.getParameter("isRoot"));
        mav.addObject("isRoot", isRoot || isRootQuery);
        
        List<OnlineUserModel> onlineUserList = new ArrayList<OnlineUserModel>(); //在线人员列表
        List<OffLineUserModel> offlineUserList = new ArrayList<OffLineUserModel>(); //离线人员列表
        
    	boolean isShowOffline = "checked".equals(request.getParameter("showoffline"));
        
        String condition = request.getParameter("condition");
        boolean isFirstInit = Strings.isBlank(condition);
        
        if((isFirstInit && !isAdmin && user.isInternal())){ //首次进入, 普通内部人员
            //部门path查询匹配
            String departmentPath = "";
            if(user.getLoginAccount() != user.getAccountId()){//登录兼职单位
                Map<Long,List<ConcurrentPost>> deptConcurrentMap = orgManager.getConcurentPostsByMemberId(currentAccountId, user.getId());
                if(deptConcurrentMap != null && !deptConcurrentMap.isEmpty()){
                    //如果有多个兼职部门，取第一个
                    Set<Long> deptIds = deptConcurrentMap.keySet();
                    Long deptId = (Long) deptIds.toArray()[0];
                    V3xOrgDepartment department = orgManager.getDepartmentById(deptId);
                    if(department != null){
                        departmentPath = department.getPath();
                    }
                }
            }else{
	        	V3xOrgDepartment department = orgManager.getDepartmentById(user.getDepartmentId());
	        	if(department != null){
	        		departmentPath = department.getPath();
	            }
            }
            
            if(isShowOffline){
	            V3xOrgDepartment dept = orgManager.getDepartmentByPath(departmentPath);
	            if(dept!=null){
	            	List<V3xOrgMember> members = orgManager.getMembersByDepartment(dept.getId(), false, dept.getOrgAccountId());
	            	for (V3xOrgMember m : members) {
	            	    boolean isOnLine = onLineManager.isOnline(m.getLoginName());
	            	    OnlineUser  onlineUser = onLineManager.isOnlineUser(m.getLoginName());
	                    if (!isOnLine || (isOnLine && m.getIsInternal() && onlineUser != null && !onlineUser.getCurrentAccountId().equals(currentAccountId))) {//兼职人员在线但是不登陆本单位
	            			V3xOrgPost post = null;
	        	            Long showDeptId = null;
	            			V3xOrgDepartment userEntryDept = orgManager.getDepartmentById(m.getOrgDepartmentId());
	            			if(currentAccountId.equals(m.getOrgAccountId()) && (departmentPath.equals(userEntryDept.getPath()) || userEntryDept.getPath().startsWith(departmentPath + "."))) {
	            				showDeptId = m.getOrgDepartmentId();
	            				post = orgManager.getPostById(m.getOrgPostId());
	            			} else {
	            				List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(m.getId());
	            				for(ConcurrentPost cnt : cntList) {
	            					if(dept.getId().equals(cnt.getCntDepId())) {
	            						showDeptId = cnt.getCntDepId();
	            						post = orgManager.getPostById(cnt.getCntPostId());
	            						break;
	            					}
	            				}
	            			}
	            			if (post == null) {
	            				if (m.getOrgDepartmentId() != user.getDepartmentId()) {
	            					List<MemberPost> secondPostByDeptId = m.getSecondPostByDeptId(user.getDepartmentId());
	            					for (MemberPost posts : secondPostByDeptId) {
	            						showDeptId = user.getDepartmentId();
	            						if(posts.getDepId().equals(user.getDepartmentId())) {
	            							post = orgManager.getPostById(posts.getPostId());
	            						}
	            					}
	            				}
	            			}
	            			if (post != null) {
                                offlineUserList.add(new OffLineUserModel(m,post,orgManager.getDepartmentById(showDeptId)));
	            			} else {
	            				logger.info("人员" + m.getId() + "姓名 ：" + m.getName() + "没有岗位");
	            			}
	            		}
	            	}
	            }
            }
            List<OnlineUser> onlineUserSet = onLineManager.getOnlineList(currentAccountId);
            if(onlineUserSet != null && !onlineUserSet.isEmpty()){
                for(OnlineUser u : onlineUserSet){
                    if(u.getAccoutId().equals(currentAccountId) && (u.getDepartmentPath().equals(departmentPath) || u.getDepartmentPath().startsWith(departmentPath + "."))){ //主岗所在单位
                        if(canDisplay(user,u,internalRight)){
                        	onlineUserList.add(new OnlineUserModel(u));
                        }
                    }
                    else{ //本单位副岗 查找兼职单位信息
                        SecondePost secondPost = u.getSecondePost(currentAccountId, departmentPath);
                        if(secondPost != null){
                            OnlineUserModel m = new OnlineUserModel(u);
                            m.setDepartmentName(secondPost.getDepartmentSimpleName());
                            m.setPostName(secondPost.getPostName());
                            m.setPluralist(true);
                            if(canDisplay(user,u,internalRight)){
                            	onlineUserList.add(m);
                            }
                        } else if (u.getInternalId() == user.getId()) {// 兼职,兼职人员登录，但是没有
							OnlineUserModel m = new OnlineUserModel(u);
							m.setDepartmentName(null);
							m.setPostName(null);
							m.setPluralist(true);
							onlineUserList.clear();
							onlineUserList.add(m);
							break;
                        }
                    }
                }
            }
        }else if((isFirstInit && isAdmin) ||(isFirstInit && !user.isInternal()) || "SelectAccount".equals(condition)){ //首次进入的管理员或外部人员, 或者是选择某单位
        	
            if(isShowOffline && !isRoot){
        	    List<V3xOrgMember> members = orgManager.getAllMembers(NumberUtils.toLong(request.getParameter("currentAccountId")));
        		for (V3xOrgMember member : members) {
        		    boolean isOnLine = onLineManager.isOnline(member.getLoginName());
        		    OnlineUser  onlineUser = onLineManager.isOnlineUser(member.getLoginName());
                    if (!isOnLine || (isOnLine && member.getIsInternal() && onlineUser != null && !onlineUser.getCurrentAccountId().equals(currentAccountId))) {//兼职人员在线但是不登陆本单位
        				Long showDeptId = null;
            			V3xOrgPost post = null;
        				if(member.getOrgAccountId().equals(currentAccountId)) {
        					post = orgManager.getPostById(member.getOrgPostId());
        					showDeptId = member.getOrgDepartmentId();
        				} else {
        					List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(member.getId());
        					for(ConcurrentPost cnt : cntList) {
        						if(currentAccountId.equals(cnt.getCntAccountId())) {
        							post = orgManager.getPostById(cnt.getCntPostId());
        							if (cnt.getCntDepId() != null) {
        								showDeptId = cnt.getCntDepId();
        							} else {
        								showDeptId = member.getOrgDepartmentId();
        							}
        							break;
        						}
        					}
        				}
        				if (post != null && orgManager.getDepartmentById(showDeptId) != null) {
        					offlineUserList.add(new OffLineUserModel(member,post,orgManager.getDepartmentById(showDeptId)));
        				} else {
        					logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "没有岗位");
        					if (post != null) {
        						logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "部门为空" + showDeptId);
        					}
        				}
        			}
        		}
        	}
        	
        	onlineUserList = this.getOnlineUserListByAccount(currentAccountId, user, internalRight);
        	
			mav.addObject("queryType", "account");
			mav.addObject("queryValue", currentAccountId);
        }else if("ByDepartment".equals(condition)){ //按部门ID查询
            String departmentId = request.getParameter("departmentId");
            if(Strings.isNotBlank(departmentId)){
            	
            	V3xOrgDepartment dept = orgManager.getDepartmentById(NumberUtils.toLong(departmentId));
            	currentAccountId = dept.getOrgAccountId();
            	String deptPath = dept.getPath();
            	
            	if(isShowOffline){
	            	List<V3xOrgMember> members = orgManager.getMembersByDepartment(dept.getId(), false, dept.getOrgAccountId());
	            	for (V3xOrgMember m : members) {
	            	    boolean isOnLine = onLineManager.isOnline(m.getLoginName());
	            	    OnlineUser  onlineUser = onLineManager.isOnlineUser(m.getLoginName());
	                    if (!isOnLine || (isOnLine && m.getIsInternal() && onlineUser != null && !onlineUser.getCurrentAccountId().equals(currentAccountId))) {//兼职人员在线但是不登陆本单位
	            			Long showDeptId = null;
	            			V3xOrgPost post = null;
	            			V3xOrgDepartment userEntryDept = orgManager.getDepartmentById(m.getOrgDepartmentId());
	            			if(currentAccountId.equals(m.getOrgAccountId()) && (deptPath.equals(userEntryDept.getPath()) || userEntryDept.getPath().startsWith(deptPath + "."))) {
	            				showDeptId = m.getOrgDepartmentId();
	            				post = orgManager.getPostById(m.getOrgPostId());
	            			} else {
	            				List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(m.getId());
	            				for(ConcurrentPost cnt : cntList) {
	            					if(Long.valueOf(departmentId).equals(cnt.getCntDepId())) {
	            						showDeptId = cnt.getCntDepId();
	            						post = orgManager.getPostById(cnt.getCntPostId());
	            						break;
	            					}
	            				}
	            			}
	            			if (post == null) {
	            				if (m.getOrgDepartmentId() != NumberUtils.toLong(departmentId)) {
	            					List<MemberPost> secondPostByDeptId = m.getSecondPostByDeptId(NumberUtils.toLong(departmentId));
	            					for (MemberPost posts : secondPostByDeptId) {
	            						showDeptId = NumberUtils.toLong(departmentId);
	            						if(posts.getDepId().equals(NumberUtils.toLong(departmentId))) {
	            							post = orgManager.getPostById(posts.getPostId());
	            						}
	            					}
	            				}
	            			}
	            			if (post != null) {
	            				offlineUserList.add(new OffLineUserModel(m,post,orgManager.getDepartmentById(showDeptId)));
	            			} else {
	            				logger.info("人员" + m.getId() + "姓名 ：" + m.getName() + "没有岗位");
	            			}
	            		}
					}
	            }
            	
            	onlineUserList = this.getOnlineUserListByDepartment(NumberUtils.toLong(departmentId), user, internalRight);
            }

			mav.addObject("queryType", "department");
			mav.addObject("queryValue", departmentId);
        }
        else if("ByTeam".equals(condition)){ //按讨论组ID查询
        	String teamId = request.getParameter("departmentId");
        	V3xOrgTeam team = orgManager.getTeamById(NumberUtils.toLong(teamId));
			if(team != null){
				List<Long> memberIds = new UniqueList<Long>();
				memberIds.addAll(team.getAllMembers());
				memberIds.addAll(team.getAllRelatives());
				
				for(Long memberId : memberIds){
					V3xOrgMember member = orgManager.getMemberById(memberId);
					if(member != null && member.isValid()){
						OnlineUser onlineUser = onLineManager.isOnlineUser(member.getLoginName());
	        			if(onlineUser != null){
	        				onlineUserList.add(new OnlineUserModel(onlineUser));
	        			}else if(isShowOffline){
	        				if (orgManager.getPostById(member.getOrgPostId()) != null) {
	        					offlineUserList.add(new OffLineUserModel(member,orgManager.getPostById(member.getOrgPostId()),orgManager.getDepartmentById(member.getOrgDepartmentId())));
	        				} else {
	        					logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "没有岗位");
	        				}
	        			}
					}
				}
			}
			
			mav.addObject("queryType", "team");
			mav.addObject("queryValue", teamId);
        }
        else if("MyRelate".equals(condition)){ //关联人员
        	List<PeopleRelate> myRelateList = peoplerelateManager.getPeopleRelatedList(user.getId());
        	for(PeopleRelate p : myRelateList){
        		if(p != null){
        			V3xOrgMember member = orgManager.getMemberById(p.getRelateMemberId());
        			if(member == null || !member.isValid()){
        				continue;
        			}
        			OnlineUser onlineUser = onLineManager.isOnlineUser(member.getLoginName());
        			if(onlineUser != null){
        				onlineUserList.add(new OnlineUserModel(onlineUser));
        			}
        			else if(isShowOffline){
        				if (orgManager.getPostById(member.getOrgPostId()) != null) {
        					offlineUserList.add(new OffLineUserModel(member,orgManager.getPostById(member.getOrgPostId()),orgManager.getDepartmentById(member.getOrgDepartmentId())));
        				} else {
        					logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "没有岗位");
        				}
        			}
        		}
        	}
        }
        else if("ByName".equals(condition)){ //按姓名查询
        	// 选中某单位在该单位中查, 选中某部门在该部门中查, 选中某讨论组在该组中查
			// 选中集团根则在全集团查, 其它默认在当前单位中查
        	String queryType = request.getParameter("queryType");
        	String queryValue = request.getParameter("queryValue");
            String nameKey = request.getParameter("userName");
            
            if (isRootQuery) {
            	for(V3xOrgAccount account : accountList){
            		if(isShowOffline){
	            		List<V3xOrgMember> members = orgManager.getAllMembers(account.getOrgAccountId());
 	            		for (V3xOrgMember member : members) {
 	            			if(!onLineManager.isOnline(member.getLoginName()) && member.getName().indexOf(nameKey) !=  -1){
	            				Long showDeptId = member.getOrgDepartmentId();
	            				V3xOrgPost post = orgManager.getPostById(member.getOrgPostId());
	            				List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(member.getId());
	            				for(ConcurrentPost cnt : cntList) {
	            					if(account.getOrgAccountId().equals(cnt.getCntAccountId())) {
	            						post = orgManager.getPostById(cnt.getCntPostId());
	            						showDeptId = cnt.getCntDepId();
	            						break;
	            					}
	            				}
	            				if (post != null) {
	            					offlineUserList.add(new OffLineUserModel(member,post,orgManager.getDepartmentById(showDeptId)));
	            				} else {
	            					logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "没有岗位");
	            				}
	            			}
						}
            		}
            		
                    List<OnlineUser> onlineUserSet = onLineManager.getOnlineList(account.getOrgAccountId());
                    if(CollectionUtils.isNotEmpty(onlineUserSet)){
                        for(OnlineUser u : onlineUserSet){
                            if(u.getName().indexOf(nameKey) !=  -1){
                            	if(u.getAccoutId().equals(u.getCurrentAccountId())) {
                            		onlineUserList.add(new OnlineUserModel(u));
                            	} else {
                            		List<OnlineUser.SecondePost> spList = u.getSecondePosts();
                            		for(OnlineUser.SecondePost sp : spList) {
                            			if(u.getCurrentAccountId().equals(sp.getAccountId())) {
                            				OnlineUserModel m = new OnlineUserModel(u);
                							m.setDepartmentName(sp.getDepartmentSimpleName());
                							m.setPostName(sp.getPostName());
                							m.setPluralist(true);
                							onlineUserList.add(m);
                            			}
                            		}
                            	}
                        		
                            }
                        }
                    }
                }
            	mav.addObject("isRootQuery", true);
            }
            else {
            	List<OnlineUserModel> onlineUserModelSet = new ArrayList<OnlineUserModel>();
            	List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
            	
            	if ("team".equals(queryType)) {
					V3xOrgTeam team = orgManager.getTeamById(NumberUtils.toLong(queryValue));
					if (team != null) {
						List<Long> memberIds = new UniqueList<Long>();
						memberIds.addAll(team.getAllMembers());
						memberIds.addAll(team.getAllRelatives());

						for (Long memberId : memberIds) {
							V3xOrgMember member = orgManager.getMemberById(memberId);
							if (member != null && member.isValid()) {
								members.add(member);
								OnlineUser onlineUser = onLineManager.isOnlineUser(member.getLoginName());
								if (onlineUser != null) {
									onlineUserModelSet.add(new OnlineUserModel(onlineUser));
								}
							}
						}
					}
				} else if ("department".equals(queryType)) {
        			
        			V3xOrgDepartment dept = orgManager.getDepartmentById(NumberUtils.toLong(queryValue));
        			members = orgManagerDirect.getMembersByDepartment(dept.getId(), false, dept.getOrgAccountId());
        			
        			onlineUserModelSet = this.getOnlineUserListByDepartment(NumberUtils.toLong(queryValue), user, internalRight);
        		} else if ("account".equals(queryType)) {
        			
        			members = orgManager.getAllMembers(NumberUtils.toLong(queryValue));
        			
        			onlineUserModelSet = this.getOnlineUserListByAccount(NumberUtils.toLong(queryValue), user, internalRight);
        		} else {
        			
        			members = orgManager.getAllMembers(currentAccountId);
        			
        			onlineUserModelSet = this.getOnlineUserListByAccount(currentAccountId, user, internalRight);
        		}
        		
        		for (V3xOrgMember member : members) {
        			if (!member.getEnabled() || member.getIsDeleted()){
        				continue;
        			}
        			boolean isOnLine = onLineManager.isOnline(member.getLoginName());
                    OnlineUser  onlineUser = onLineManager.isOnlineUser(member.getLoginName());
                    if ((!isOnLine || (isOnLine && member.getIsInternal() && onlineUser != null && !onlineUser.getCurrentAccountId().equals(currentAccountId)))
                            && member.getName().indexOf(nameKey) !=  -1) {//兼职人员在线但是不登陆本单位
        				Long showDeptId = member.getOrgDepartmentId();
        				V3xOrgPost post = orgManager.getPostById(member.getOrgPostId());
        				List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(member.getId());
        				if("department".equals(queryType)) {
        					// 查询的部门
        					V3xOrgDepartment dept = orgManager.getDepartmentById(NumberUtils.toLong(queryValue));
        					// 人员实体部门
        					V3xOrgDepartment userEntryDept = orgManager.getDepartmentById(member.getOrgDepartmentId());
        					if(!(dept.getOrgAccountId().equals(member.getOrgAccountId()) && (dept.getPath().equals(userEntryDept.getPath()) || userEntryDept.getPath().startsWith(dept.getPath() + ".")))) {
        						for(ConcurrentPost cnt : cntList) {
        							if(dept.getId().equals(cnt.getCntDepId())) {
        								post = orgManager.getPostById(cnt.getCntPostId());
        								showDeptId = cnt.getCntDepId();
        								break;
        							}
        						}
        					}
        				} else if("account".equals(queryType)) {
        					if(!Long.valueOf(queryValue).equals(member.getOrgAccountId())) {
        						for(ConcurrentPost cnt : cntList) {
        							if(Long.valueOf(queryValue).equals(cnt.getCntAccountId())) {
        								post = orgManager.getPostById(cnt.getCntPostId());
        								showDeptId = cnt.getCntDepId();
        								break;
        							}
        						}
        					}
        				}
        				if (post != null && Strings.isNotBlank(post.getName())) {
        					offlineUserList.add(new OffLineUserModel(member,post,orgManager.getDepartmentById(showDeptId)));
        				} else {
        					logger.info("人员" + member.getId() + "姓名 ：" + member.getName() + "没有岗位");
        				}
        			}
				}
        		
        		if(CollectionUtils.isNotEmpty(onlineUserModelSet)){
                    for(OnlineUserModel u : onlineUserModelSet){
                        if(u.getName().indexOf(nameKey) !=  -1){
                    		 onlineUserList.add(u);
                        }
                    }
                }
            }
            
            mav.addObject("queryType", queryType).addObject("queryValue", queryValue).addObject("userName", nameKey);
        }
        
        boolean canMoveToOffline = false; //是否可以踢人(系统管理员、集团管理员、本单位的单位管理员)
        if(user.isSystemAdmin() || user.isGroupAdmin() || (user.isAdministrator() && user.getLoginAccount() == currentAccountId)){
            canMoveToOffline = true;
        }
        
        
        List<OffLineUserModel> newOfflineUserList = new ArrayList<OffLineUserModel>();
        for (OffLineUserModel offLineUserModel : offlineUserList) {
			if(Functions.checkLevelScope(user.getId(), offLineUserModel.getId())) {
				newOfflineUserList.add(offLineUserModel);
			}
		}
        //存放外部人员所能访问的人员id集合
        List<Long> memberIds = new ArrayList<Long>();
        if(!user.isInternal()){
        	//获取外部人员能访问的所有实体
        	V3xOrgMember memberById = orgManager.getMemberById(user.getId());
        	if (memberById != null) {
        		memberIds.add(memberById.getId());
        	}
        	List<V3xOrgEntity> memberWorkScopeForExternal = orgManager.getExternalMemberWorkScope(user.getId(), false);
        	for (V3xOrgEntity entity : memberWorkScopeForExternal) {
        		//实体有三种情况类型Account，Department，Member，工作范围不能夸单位，所以不处理单位的情况，
        		if ("Account".equals(entity.getEntityType())) {
        			List<V3xOrgMember> membersByDepartment = orgManager.getAllMembers(entity.getId());
        			List<V3xOrgMember> membersExt = orgManager.getAllExtMembers(entity.getId());
        			membersByDepartment.addAll(membersExt);
        			if (membersByDepartment != null && membersByDepartment.size() > 0) {
        				for (V3xOrgMember member : membersByDepartment) {
        					memberIds.add(member.getId());
        				}
        			}
        		}
        		if ("Department".equals(entity.getEntityType())) {
        			List<V3xOrgMember> membersByDepartment = orgManager.getMembersByDepartment(entity.getId(), false);
        			if (membersByDepartment != null && membersByDepartment.size() > 0) {
        				for (V3xOrgMember member : membersByDepartment) {
        					memberIds.add(member.getId());
        				}
        			}
        		}
        		if ("Member".equals(entity.getEntityType())) {
        			memberIds.add(entity.getId());
        		}
        	}
        }
        //将离线人员中所有不在工作范围内的人员剔除
        if (!user.isInternal()) {
        	for (int i = 0 ; i <  newOfflineUserList.size() ; i ++ ) {
        		OffLineUserModel offlineuser = newOfflineUserList.get(i);
        		if (memberIds.size() > 0 && !memberIds.contains(offlineuser.getId())) {
        			newOfflineUserList.remove(i);
        			i -- ;
        		}
        	}
        }
		mav.addObject("offlineUserList", newOfflineUserList);
    	
        //我是内部人员，取出的能访问的外部人员
        if(user.isInternal() && !user.isAdmin()){
        	List<V3xOrgMember> memberWorkScopeForExternal = orgManager.getMemberWorkScopeForExternal(user.getId(), false);
        	Set<Long> memberWorkScopeForExternalSet = new HashSet<Long>(memberWorkScopeForExternal.size());
        	for (V3xOrgMember v3xOrgMember : memberWorkScopeForExternal) {
        		memberWorkScopeForExternalSet.add(v3xOrgMember.getId());
			}
        	
        	for (int j = 0; j < onlineUserList.size(); j++) {
        		OnlineUserModel onlineUser = onlineUserList.get(j);
				if(!onlineUser.isInternal() && !memberWorkScopeForExternalSet.contains(onlineUser.getId())){
					onlineUserList.remove(j);
					j--;
				}
			}
        	onlineUserList = checkWorkScope(onlineUserList, user);
        }
        if (!user.isInternal()) {
        	for (int i = 0 ; i <  onlineUserList.size() ; i ++ ) {
        		OnlineUserModel offlineuser = onlineUserList.get(i);
        		if (memberIds.size() > 0 && !memberIds.contains(offlineuser.getId())) {
        			onlineUserList.remove(i);
        			i -- ;
        		}
        	}
        }
        mav.addObject("canMoveToOffline", canMoveToOffline);
        mav.addObject("isShowMemberMenu", !isAdmin);//是否显示人员浮动菜单
        
        mav.addObject("currentAccountId", currentAccountId); //当前所选单位ID
        mav.addObject("onlineUserList", onlineUserList); //所选单位在线人员列表
        boolean isShowAccountSwitch = (Boolean)(SysFlag.frontPage_online_showAccountSwitch.getFlag());
        mav.addObject("isShowAccountShortName", "ByName".equals(condition) && isShowAccountSwitch); //筛选条件
    	return mav;
	}
	
	/**
	 * 获取在线列表查看单位ID
	 */
	private Long getCurrentAccountId(HttpServletRequest request, User user, List<V3xOrgAccount> accountList) throws Exception{
		Long currentAccountId = user.getLoginAccount();
		String accountIdStr = request.getParameter("currentAccountId");
        if(Strings.isNotBlank(accountIdStr)){
            currentAccountId = NumberUtils.toLong(accountIdStr);
        }else if(user.isSystemAdmin() || user.isGroupAdmin() || user.isAuditAdmin()){
        	V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();
            for(V3xOrgAccount account : accountList){
                if(!account.getIsRoot() && account.getSuperior().equals(rootAccount.getId())){
                    currentAccountId = account.getId();
                    break;
                }
            }
        }
        return currentAccountId;
	}
	
	/**
	 * 获取某单位在线人员列表
	 */
	private List<OnlineUserModel> getOnlineUserListByAccount(Long currentAccountId, User user, List<V3xOrgEntity> internalRight) throws Exception{
		List<OnlineUserModel> onlineUserList = new ArrayList<OnlineUserModel>();
		List<OnlineUser> onlineUserSet = onLineManager.getOnlineList(currentAccountId);
        if(onlineUserSet != null && !onlineUserSet.isEmpty()){
            for (OnlineUser u : onlineUserSet) {
                if(u.getAccoutId().equals(currentAccountId)){
                	if(canDisplay(user,u,internalRight)){
                		onlineUserList.add(new OnlineUserModel(u));
                		continue;
                	}
                }else{
                    SecondePost secondPost = u.getSecondePost(currentAccountId, "");
                    if(secondPost != null){
                        OnlineUserModel m = new OnlineUserModel(u);
                        m.setDepartmentName(secondPost.getDepartmentSimpleName());
                        m.setPostName(secondPost.getPostName());
                        m.setPluralist(true);
                        if(canDisplay(user,u,internalRight)){
	                	    onlineUserList.add(m);
	                	    continue;
                        }
                    }else if(u.getInternalId() == user.getId() || u.getCurrentAccountId().longValue() == currentAccountId){//兼职，但是没有 指定部门
                    	OnlineUserModel m = new OnlineUserModel(u);
                    	m.setDepartmentName(null);
						m.setPostName(null);
						m.setPluralist(true);
						onlineUserList.add(m);
						continue;
                   }else if(!user.isInternal() &&canDisplay(user,u,internalRight)){//外部人员系统组验证
                	   OnlineUserModel m = new OnlineUserModel(u);
                	   onlineUserList.add(m);
                   }
                }
            }
        }
        return onlineUserList;
	}
	
	/**
	 * 获取某部门在线人员列表
	 */
	private List<OnlineUserModel> getOnlineUserListByDepartment(Long departmentId, User user, List<V3xOrgEntity> internalRight) throws Exception {
		List<OnlineUserModel> onlineUserList = new ArrayList<OnlineUserModel>();
		V3xOrgDepartment department = orgManager.getDepartmentById(departmentId);
		if (department != null) {
			String departmentPath = department.getPath();
			Long currentAccountId = department.getOrgAccountId();
			List<OnlineUser> onlineUserSet = onLineManager.getOnlineList(currentAccountId);
			if (onlineUserSet != null && !onlineUserSet.isEmpty()) {
				for (OnlineUser u : onlineUserSet) {
					if (u == null || u.getAccoutId() == null || u.getDepartmentPath() == null) {
						continue;
					}

					if (u.getAccoutId().equals(currentAccountId) && (u.getDepartmentPath().equals(departmentPath) || u.getDepartmentPath().startsWith(departmentPath + "."))) { // 主岗所在单位
						if (canDisplay(user, u, internalRight)) {
							onlineUserList.add(new OnlineUserModel(u));
						}
					} else {// 本单位副岗 查找兼职单位信息
						SecondePost secondPost = u.getSecondePost(currentAccountId, departmentPath);
						if (secondPost != null) {
							OnlineUserModel m = new OnlineUserModel(u);
							m.setDepartmentName(secondPost.getDepartmentSimpleName());
							m.setPostName(secondPost.getPostName());
							m.setPluralist(true);
							if (canDisplay(user, u, internalRight)) {
								onlineUserList.add(m);
							}
						}
					}
				}
			}
		}
		return onlineUserList;
	}
	
	/**
	 * 创建讨论组
	 */
	public ModelAndView createTeam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("v3xmain/message/createTeam");
		String createType = request.getParameter("createType");
		Long id = NumberUtils.toLong(request.getParameter("otherId"));
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		//讨论组限制人数(100)
		int memberSize = 1;//直接创建讨论组,除去自己
		if("2".equals(createType)){
			V3xOrgMember member = this.orgManager.getMemberById(id);
			memberList.add(member);
			memberSize = 2;//单人聊天邀请,除去自己和当前聊天者
		}else if("3".equals(createType)){
			V3xOrgTeam team = orgManagerDirect.getTeamById(id);
			List<Long> idList  = team.getMemberList(V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			for(Long memberId : idList){
				V3xOrgMember member = this.orgManager.getMemberById(memberId);
				memberList.add(member);
			}
			mav.addObject("teamName", team.getName());
			mav.addObject("updateTeam", "true");
			memberSize = memberList.size();//讨论组邀请,除去讨论组已有人员
		}
		mav.addObject("memberList", memberList);
		mav.addObject("memberSize", (Constants.TEAM_ALLOW_SIZE - memberSize));
		return mav;
	}
	
	/**
	 * 判断讨论组名称是否已经存在
	 */
	public boolean isExist(String teamName){
		User user = CurrentUser.get();
		return addressBookManager.isExist(AddressBookManager.TYPE_DISCUSS, teamName, user.getId(), user.getLoginAccount(), null);
	}
	
	/**
	 * 判断人员是否在线
	 */
	public boolean isOnline(Long id){
		OnlineUser onlineUser = Functions.getOnlineUser(id);
		if(onlineUser != null && onlineUser.getState() != null && onlineUser.getState() == LoginUserState.online){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获取讨论组的名称
	 */
	public String getTeamName(String type, Long id){
		String teamName = "";
		if(id != null){
			if("2".equals(type)){
				teamName = Functions.getDepartment(id).getName();
			}else if("3".equals(type) || "4".equals(type) || "5".equals(type)){
				teamName = Functions.getTeamName(id);
			}
		}
		return teamName;
	}
	
	/**
	 * 保存讨论组
	 */
	public ModelAndView saveTeam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		String createType = request.getParameter("createType");
		String teamMemIDs = request.getParameter("teamMemIDs");
		List<Long> members = CommonTools.parseStr2Ids(teamMemIDs);
		Long id = NumberUtils.toLong(request.getParameter("otherId"));
		
		V3xOrgTeam team = null;
		
		if("1".equals(createType) || "2".equals(createType)){
			team = new V3xOrgTeam();
			bind(request, team);
			team.setId(NumberUtils.toLong(request.getParameter("dID")));
			team.setOrgAccountId(user.getLoginAccount());
			team.setOwnerId(user.getId());
			team.setType(V3xOrgEntity.TEAM_TYPE_DISCUSS);
			team.setIsPrivate(true);
			team.setDepId(user.getDepartmentId());
			members.add(user.getId());
			if("2".equals(createType)){
				members.add(id);
			}
			team.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			orgManagerDirect.addTeam(team);
		}else{
			team = orgManagerDirect.getTeamById(id);
			team.addTeamMember(members,V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			orgManagerDirect.updateEntity(team);
		}
		return null;
	}
	
	/**
	 * 判断当前用户是否为组的创建者
	 */
	public boolean isOwner(Long teamId) throws Exception{
		User user = CurrentUser.get();
		if(teamId != null){
			V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
			return team.getOwnerId().equals(user.getId());
		}
		return false;
	}
	
	/**
	 * 判断讨论组是否可用
	 */
	public boolean isDeleted(Long teamId) throws Exception{
		if(teamId != null){
			V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
			if(team == null || team.getIsDeleted() || !team.isValid()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 删除讨论组
	 */
	public ModelAndView deleteTeam(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long teamId = RequestUtils.getLongParameter(request, "teamId");
		V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
		orgManagerDirect.deleteEntity(team);
		return null;
	}
    
    private boolean canDisplay(User user,OnlineUser u ,List<V3xOrgEntity> internalRight) throws BusinessException{
    	if(user.isSystemAdmin() || user.isAuditAdmin() || user.isGroupAdmin() || user.isAdministrator()){
    		return true;
    	}
    	if(user.isInternal()){//内部人员查看
    		V3xOrgMember umember = orgManager.getMemberById(u.getInternalId());
    		if(umember != null){
    			if(umember.getIsInternal()){//内容部人员可以查看
    				return true;
    			}else{
    				V3xOrgDepartment uDepartment = orgManager.getDepartmentById(u.getDepartmentId());
    				if (uDepartment.getPath().indexOf(".") > 0
    						&& (uDepartment.getPath().indexOf(".") == uDepartment.getPath().lastIndexOf("."))) {
    					//父节点是单位，全单位人员都能看到这个外部人员
    					return true;
    				}
    	    		V3xOrgDepartment userDepartment = orgManager.getDepartmentById(user.getDepartmentId());
    	    		//跨靠部门
    	    		if(uDepartment.getParentPath().equals(userDepartment.getParentPath()) && uDepartment.getOrgAccountId().equals(userDepartment.getOrgAccountId())){
    	    			return true;
    	    		}
    	    		List<V3xOrgTeam> outerTeam = null;
    	    		if(user.getLoginAccount() == u.getAccoutId()){
    	    			outerTeam = orgManager.getTeamsByMember(user.getId(),u.getAccoutId());
    	    		}else{
    	    			outerTeam = orgManager.getTeamsByMember(user.getId(),u.getAccoutId());
    	    			if(outerTeam != null){
    	    				//本单位组
    	    				outerTeam.addAll(orgManager.getTeamsByMember(user.getId(),user.getLoginAccount()));
    	    			}else{
    	    				outerTeam = orgManager.getTeamsByMember(user.getId(),user.getLoginAccount());
    	    			}
    	    		}
    	    		//同组的外部人员可以查看
    	    		if(outerTeam != null && !outerTeam.isEmpty()){
    	    			for(V3xOrgTeam team : outerTeam){
    	    				List<Long> mems = team.getAllMembers();
    	    				for(Long memberId : mems){
    	    					if(memberId.longValue() == u.getInternalId()){
    	    						return true;
    	    					}
    	    				}
    	    			}
    	    		}
    	    		//外部人员权限内的人员
    	    		internalRight = orgManager.getExternalMemberWorkScope(umember.getId(),false);
    	    		List<Long> depIds = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
    	    		for(V3xOrgEntity entity : internalRight){
    	    			if(entity.getId().longValue() == user.getId()){
    	    				return true;
    	    			}
    	    			if(entity.getId().longValue() == user.getDepartmentId()){
    	    				return true;
    	    			}
    	    			if(entity.getId().longValue() == user.getLoginAccount()){
    	    				return true;
    	    			}
    	    			if(depIds.contains(entity.getId().longValue())){
    	    				return true;
    	    			}
    	    		}
    			}
    		}
    	}else{
    		//同一个单位
    		if(user.getId()== u.getInternalId() || u.getDepartmentId().equals(user.getDepartmentId())){
    			return true;
    		}
    		//跨靠单位
    		V3xOrgDepartment uDepartment = orgManager.getDepartmentById(u.getDepartmentId());
    		V3xOrgDepartment userDepartment = orgManager.getDepartmentById(user.getDepartmentId());
    		if(uDepartment.getParentPath().equals(userDepartment.getParentPath())&& uDepartment.getOrgAccountId().equals(userDepartment.getOrgAccountId())){
    			return true;
    		}
    		//可以访问人员
    		if(internalRight == null){
    			internalRight = orgManager.getExternalMemberWorkScope(user.getId(),false);
    		}
    		
    		for(V3xOrgEntity entity : internalRight){
    			if(entity.getId().longValue() == u.getInternalId().longValue()){
    				return true;
    			}
    			if(entity.getId().longValue() == u.getDepartmentId().longValue()){
    				return true;
    			}
    			if(entity.getId().longValue() == u.getAccoutId().longValue()){
    				return true;
    			}
    		}
    		List<V3xOrgTeam> outerTeam = null;
    		if(user.getLoginAccount() == u.getAccoutId()){
    			outerTeam = orgManager.getTeamsByMember(user.getId(),u.getAccoutId());
    		}else{
    			outerTeam = orgManager.getTeamsByMember(user.getId(),u.getAccoutId());
    			if(outerTeam != null){
    				//本单位组
    				outerTeam.addAll(orgManager.getTeamsByMember(user.getId(),user.getLoginAccount()));
    			}else{
    				outerTeam = orgManager.getTeamsByMember(user.getId(),user.getLoginAccount());
    			}
    		}
    		//同组的外部人员可以查看
    		if(outerTeam != null && !outerTeam.isEmpty()){
    			for(V3xOrgTeam team : outerTeam){
    				List<Long> mems = team.getAllMembers();
    				for(Long memberId : mems){
    					if(memberId.longValue() == u.getInternalId()){
    						return true;
    					}
    				}
    			}
    		}
    	}
    	return false;
    }
    public ModelAndView moveMemberToOffline(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String loginName = request.getParameter("loginName");
        String currentAccountId = request.getParameter("currentAccountId");
        //强制下线
        OnlineRecorder.moveToOffline(loginName, LoginOfflineOperation.adminKickoff);
        return super.redirectModelAndView("/message.do?method=showOnlineUserList&currentAccountId="+currentAccountId);
    }
    
    /**
     * 显示IM聊天窗口
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	String view = "v3xmain/message/sendIMMessage";
    	ModelAndView mav = new ModelAndView(view);
    	String type = request.getParameter("type");
    	Long id = RequestUtils.getLongParameter(request, "id");
    	List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
    	List<OnlineUser> onlineMembers = new ArrayList<OnlineUser>();
    	List<V3xOrgMember> offlineMembers = new ArrayList<V3xOrgMember>();
    	boolean haveOnline = false;
    	if("1".equals(type)){
    		V3xOrgMember member = orgManager.getMemberById(id);
    		haveOnline = onLineManager.isOnline(member.getLoginName());
    	}else if("2".equals(type)){
    		V3xOrgDepartment dept = this.orgManager.getDepartmentById(id);
    		if(dept != null){
    			members = this.orgManager.getMembersByDepartment(dept.getId(), true, false, dept.getOrgAccountId(), false);
    		}
    	}else if("3".equals(type) || "4".equals(type) || "5".equals(type)){
			V3xOrgTeam team = orgManagerDirect.getTeamById(id);
			if(team != null){
				List<Long> memberIds = new UniqueList<Long>();
				memberIds.addAll(team.getAllMembers());
				memberIds.addAll(team.getAllRelatives());
				members = new ArrayList<V3xOrgMember>();
				for(Long memberId : memberIds){
					V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
					if(member != null && member.isValid()){
						members.add(member);
					}
				}
				if(wimManager.isColRelativeTeam(team.getId())&&(members.size()==2||members.size()==1)){
					mav.addObject("colteam", true);
				}else{
					mav.addObject("colteam", false);
				}
			}
    	}
    	for(V3xOrgMember m : members){
    		OnlineUser onlineUser = Functions.getOnlineUser(m.getId());
    		if(onlineUser != null && onlineUser.getState() != null && onlineUser.getState() == LoginUserState.online){
    			onlineMembers.add(onlineUser);
    		}else{
    			offlineMembers.add(m);
    		}
    	}
    	
    	if(!onlineMembers.isEmpty()&&onlineMembers.size()>1){
    		haveOnline = true;
    	}
    	haveOnline = false;
    	mav.addObject("type", type);
    	mav.addObject("haveOnline", haveOnline);
    	mav.addObject("onlineMembers", onlineMembers);
    	mav.addObject("offlineMembers", offlineMembers);
    	return mav;
    }
	
	/**
	 * 发起协同讨论
	 */
	public Map<String, String> createCollDisscuss(Long summaryId) throws Exception {
		User user = CurrentUser.get();
		Map<String, String> result = new HashMap<String, String>();
		if (summaryId == null) {
			result.put("cando", "false");
			result.put("success", "false");
			return result;
		}

		ColSummary colSummary = colManager.getColSummaryById(summaryId, false);
		List<Affair> affairs = affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.collaboration, summaryId, false);
		List<Long> members = new UniqueList<Long>();
		for (Affair affair : affairs) {
			members.add(affair.getMemberId());
		}

		if (members.size() > 100) {
			result.put("cando", "false");
			result.put("success", "true");
			return result;
		}

		V3xOrgTeam orgTeam = wimManager.getRelativeTeamByColId(summaryId);
		List<Long> member = new ArrayList<Long>();
		if (orgTeam != null) {
			List<V3xOrgMember> list = orgManager.getTeamMember(orgTeam.getId());
			member = CommonTools.getEntityIds(list);
		}

		if (orgTeam == null) {
			orgTeam = new V3xOrgTeam();
			orgTeam.setId(UUIDLong.longUUID());
			orgTeam.setOrgAccountId(user.getLoginAccount());
			orgTeam.setType(V3xOrgEntity.TEAM_TYPE_COLTEAM);
			orgTeam.setIsPrivate(true);
			orgTeam.setName(colSummary.getSubject());
			orgTeam.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			orgTeam.setOwnerId(colSummary.getId());
			orgManagerDirect.addTeam(orgTeam);
		} else {
			members.removeAll(member);
			orgTeam.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			orgManagerDirect.updateEntity(orgTeam);
		}

		result.put("cando", "true");
		result.put("teamId", String.valueOf(orgTeam.getId()));
		result.put("teamName", orgTeam.getName());
		return result;
	}

	public ModelAndView createMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		User user = CurrentUser.get();
		PrintWriter out = response.getWriter();
		
		String result = "";
		
		Map<String,Object> videoParamMap = new HashMap<String, Object>();
		String type = request.getParameter("messageType");
		String meetingName;
		int num = 0;
		String referenceId = request.getParameter("receiverIds");
		
		List<V3xOrgMember> orgMembers = new ArrayList<V3xOrgMember>();
		if(type.equals("1")){
			V3xOrgMember member = orgManager.getMemberById(Long.parseLong(referenceId));
			orgMembers.add(member);
			orgMembers.add(orgManager.getMemberById(user.getId()));
			meetingName = user.getName()+"_"+member.getName()+" 讨论会议";
		}else if(type.equals("2")){
			V3xOrgDepartment dept = this.orgManager.getDepartmentById(Long.parseLong(referenceId));
			meetingName = dept.getName()+" 讨论会议";
			orgMembers = orgManager.getMembersByDepartment(dept.getId(), true, false, dept.getOrgAccountId(), false);
		}else{
			V3xOrgTeam orgTeam = orgManagerDirect.getTeamById(Long.parseLong(referenceId));
			meetingName = orgTeam.getName()+" 讨论会议";
			List<Long> members = orgTeam.getAllMembers();
			members.addAll(orgTeam.getAllRelatives());
			for (Long long1 : members) {
				V3xOrgMember member = orgManager.getMemberById(long1);
				orgMembers.add(member);
			}
		}
		num = orgMembers.size();
		for (int i = 0; i < num; i++) {
			V3xOrgMember member = orgMembers.get(i);
			if(member.getId()!= user.getId()){
				if(onLineManager.isOnline(member.getLoginName())){
					break;
				}
			}
			if(i==num-1){
				result = "({\"success\":\"false\",\"confKey\":\"MainLang.message_vomeeting_createerror_offline\"})";
				out.println(result);
				out.flush();
				out.close();
				return null;
			}
		}
		videoParamMap.put("subject", meetingName);
		videoParamMap.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(VideoConfUtil.toGMTDate(new Date(), "-479")).replace(" ", "T"));
		videoParamMap.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(VideoConfUtil.toGMTDate(new Date((new Date().getTime()+3600000)),"-480")).replace(" ", "T"));
		videoParamMap.put("attendeeAmount", String.valueOf(num));
		videoParamMap.put("hostName",user.getLoginName());
		videoParamMap.put("agenda","");
		videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
		videoParamMap.put("creator", user.getLoginName());
		videoParamMap.put("userName", user.getLoginName());
		videoParamMap.put("password", videoConferenceManager.getEncryptedPassWD(user.getId()));
			
		String meetingInf = ((CreateVideoConferenceManagerCAP)ApplicationContextHolder.getBean("createInfoWareInstantMeetingManagerImplCAP")).createVideoConferenceCap(videoParamMap);

		log.info(meetingInf);
		String confKey = "";
		try{
			meetingInf.replace("\"", "");
    		confKey = meetingInf.substring(meetingInf.indexOf("<confKey>")+9, meetingInf.lastIndexOf("</confKey>"));
    		
    		if(!Strings.isBlank(confKey)){
    			result = "({\"success\":\"true\",\"confKey\":\""+confKey+"\",\"tnum\":\""+(num-1)+"\"})";
    			out.println(result);
    			out.flush();
    			out.close();
    			return null;
    		}else{
    			throw new RuntimeException();
    		}
    	}catch(Exception e){
    		result = "({\"success\":\"false\",\"confKey\":\"MainLang.message_vomeeting_createerror\"})";
    		out.println(result);
    		out.flush();
    		out.close();
    		return null;
    	}
	}

	public ModelAndView joinmeeting(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String confKey = request.getParameter("confKey");
		String hostname = request.getParameter("meeting_id");
		String iscreater = request.getParameter("iscreater");
		
		Map<String,String> videoParamMap = new HashMap<String,String>();
     	try {
     		videoParamMap.put("displayName", CurrentUser.get().getName());
     		videoParamMap.put("attendeeName", CurrentUser.get().getName());
			videoParamMap.put("confKey", confKey);
			videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
			String email = orgManager.getMemberById(CurrentUser.get().getId()).getEmailAddress();
			videoParamMap.put("userName", CurrentUser.get().getLoginName());
			
			if(email==null||"".equals(email)){
				videoParamMap.put("email",CurrentUser.get().getLoginName()+"@seeyon.com");
			}else{
				videoParamMap.put("email",email);
			}
			videoParamMap.put("password", videoConferenceManager.getEncryptedPassWD(CurrentUser.get().getId()));
		} catch (SQLException e1) {
			logger.error(e1);
		} catch (BusinessException e) {
			logger.error(e);
		}
		
		String result;
		if(Strings.isNotBlank(iscreater)&&iscreater.equals("true")){
			videoParamMap.put("hostName", orgManager.getMemberById(Long.parseLong(hostname)).getLoginName());
			result = ((StartVideoConferenceManagerCAP)ApplicationContextHolder.getBean("startVideoConferenceManagerCAP")).startVideoConferenceCap(videoParamMap);
		}else{
			result = ((JoinVideoConferenceManagerCAP)ApplicationContextHolder.getBean("joinVideoConferenceManagerCAP")).joinVideoConferenceCap(videoParamMap);
		}
		
		ModelAndView mav = new ModelAndView("v3xmain/message/joinMeeting");
		
		logger.info("参加红杉树视频会议："+result);
		@SuppressWarnings("rawtypes")
		Map paramMap = ParseXML.parseXML(result);
        try {
        	String token = StringUtils.trim((String) paramMap.get("token"));
        	String ciURL = StringUtils.trim((String) paramMap.get("ciURL"));
			
        	mav.addObject("ciURL",ciURL);
        	mav.addObject("token",token);
        	
        	mav.addObject("success", true);
		} catch (Exception e) {
			// TODO: handle exception
			mav.addObject("success", false);
		}
		
		return mav;
	}

	public ModelAndView deletemeeting(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		Map<String,String> videoParamMap = new HashMap<String,String>();
		videoParamMap.put("confKey",request.getParameter("confKey"));
		videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
     	videoParamMap.put("userName", user.getLoginName());
     	videoParamMap.put("password", videoConferenceManager.getEncryptedPassWD(user.getId()));
     	String deleteInf = ((DeleteVideoConferenceManagerCAP)ApplicationContextHolder.getBean("deleteVideoConferenceManagerCAP")).deleteVideoConferenceCap(videoParamMap);
     	if(StringUtils.contains(deleteInf, "0x")||!StringUtils.contains(deleteInf,"SUCCESS")){
			throw new Exception("删除失败！Error Code(RedFir):"+deleteInf);
     	}
     	return null;
	}
    
    /**
     * 获取当前聊天记录
     */
    public ModelAndView showThisHistoryMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("v3xmain/message/showThisHistoryMessage");
    	User user = CurrentUser.get();
    	
    	List<UserHistoryMessage> messageList = null;
		int type = NumberUtils.toInt(request.getParameter("type"));
		Long id = NumberUtils.toLong(request.getParameter("id"));
		String createDate = request.getParameter("createDate");
		
		int nowPage = 1; //当前页
		int size = 0; //总条数
		int pageSize = 20; //每页显示条数
		int pages = 1; //总页数
		int start = 0; //开始显示的记录
		
		size = userMessageManager.getThisHistoryMessage(Constants.valueOf(type), user.getId(), id, createDate);
		
		pages = (size + pageSize - 1) / pageSize;	//计算总页数
		if(pages == 0){
			pages = 1;
		}
		
		String nowPagePara = request.getParameter("nowPagePara");
		if(Strings.isNotBlank(nowPagePara) && !nowPagePara.equals("1")){
            nowPage = NumberUtils.toInt(nowPagePara, 1);
            start = (nowPage - 1) * pageSize;
		}
		
		messageList = userMessageManager.getThisHistoryMessage(Constants.valueOf(type), user.getId(), id, createDate, start, pageSize);
		
		mav.addObject("size", size);
		mav.addObject("pages", pages);
		mav.addObject("nowPage", nowPage);
        mav.addObject("messageList", messageList);
    	return mav;
    }
    
	/**
	 * 获取当前所有聊天记录
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView showAllHistoryMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		if (StringUtils.isNotBlank(request.getParameter("init"))) {
			mav = new ModelAndView("v3xmain/message/showInitPage");
			return mav;
		}

		User user = CurrentUser.get();
		mav = new ModelAndView("v3xmain/message/showAllHistoryMessage");
		int type = NumberUtils.toInt(request.getParameter("type"));
		Long id = NumberUtils.toLong(request.getParameter("id"));
		boolean search = StringUtils.isNotBlank(request.getParameter("search")) ? true : false;
		String area = request.getParameter("area");
		String time = request.getParameter("time");
		String content = request.getParameter("content");

		List<UserHistoryMessage> messageList = userMessageManager.getAllHistoryMessage(Constants.valueOf(type), user.getId(), id, search, area, time, content, true);

		int size = Pagination.getRowCount();
		if (messageList != null) {
			if (size == 0) {
				size = messageList.size();
			}
		}

		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"));
		if (pageSize < 1) {
			pageSize = 20;
		}

		int pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}

		int page = NumberUtils.toInt(request.getParameter("page"));
		if (page < 1) {
			page = 1;
		} else if (page > pages) {
			page = pages;
		}

		Map<String, List<UserHistoryMessage>> map = new LinkedHashMap();
		if (CollectionUtils.isNotEmpty(messageList)) {
			for (UserHistoryMessage msg : messageList) {
				String date = Datetimes.formatDate(msg.getCreationDate());
				List<UserHistoryMessage> list = map.get(date);
				if (list == null) {
					list = new ArrayList<UserHistoryMessage>();
					map.put(date, list);
				}
				list.add(msg);
			}
		}

		mav.addObject("size", size);
		mav.addObject("pageSize", pageSize);
		mav.addObject("pages", pages);
		mav.addObject("page", page);
		mav.addObject("map", map);
		return mav;
	}
    
    /**
     * 删除聊天记录
     */
    public ModelAndView deleteMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	String deleteType = request.getParameter("deleteType");
		String deleteIds = request.getParameter("deleteIds");
		List<Long> ids = new ArrayList<Long>();
		if(StringUtils.isNotBlank(deleteIds)){
			if("4".equals(deleteType) || "5".equals(deleteType)){
				ids.add(NumberUtils.toLong(deleteIds));
			}else if("1".equals(deleteType)){
				String[] idStrs = deleteIds.split(",");
				for(String str : idStrs){
					if(StringUtils.isNotBlank(str)){
						ids.add(NumberUtils.toLong(str));
					}
				}
			}
		}
		userMessageManager.deleteMessage(user.getId(), deleteType, ids);
		
		if("1".equals(deleteType)){
			return super.redirectModelAndView("main.do?method=showMessages&showType=1&type=1&id=" + request.getParameter("id"));
		}else{
			return super.redirectModelAndView("main.do?method=showMessages&showType=1");
		}
    }
    
    /**
     * 旧的发送消息窗口,用于人员卡片上的发送消息等
     */
	public ModelAndView showSendDlg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/message/sendMessage");
		String receiverIdsStr = request.getParameter("receiverIds");

		long userId = CurrentUser.get().getId();
		List<String> rs = new ArrayList<String>();
		boolean checkLevelScope = false;

		if (StringUtils.isNotBlank(receiverIdsStr)) {
			StringTokenizer tokenizer = new StringTokenizer(receiverIdsStr, ",");
			while (tokenizer.hasMoreElements()) {
				String r = (String) tokenizer.nextElement();
				if (Functions.checkLevelScope(userId, NumberUtils.toLong(r))) {
					rs.add(r);
				} else {
					checkLevelScope = true;
				}
			}
		}

		if (rs.isEmpty() && checkLevelScope) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "message.checkLevelScope.alert") + "')");
			out.println("window.close()");
			out.println("</script>");
			return null;
		}

		modelAndView.addObject("receiverIds", StringUtils.join(rs, ","));
		modelAndView.addObject("receiverNum", rs.size());
		return modelAndView;
	}
    
    /**
     * 首页-发送手机短信 - 显示发送对话框
     */ 
    public ModelAndView showSendSMSDlg(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/message/sendSMS");
        
        String receiverIdsStr = request.getParameter("receiverIds");
        modelAndView.addObject("receiverIds", receiverIdsStr);

        return modelAndView;
    }

    /**
     * 首页-发送手机短信 - 发送处理
     */ 
    public ModelAndView sendSMS(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String receiverIdsStr = request.getParameter("receiverIds");

        List<Long> legitimacyReceiverIdsList = new ArrayList<Long>();//合法的用户id数组
        String noTelNumberMembers = null; //没有设置手机号的用户
        String content = request.getParameter("content");
        if(receiverIdsStr != null && receiverIdsStr.length()>0){
            String[] receiverIdsStrArray = receiverIdsStr.split(",");
            String separator = Constant.getValueFromCommonRes("common.separator.label");
            for(String receiverIdStr : receiverIdsStrArray){
                Long memberId = Long.parseLong(receiverIdStr);
                try {
                    V3xOrgMember member = orgManager.getMemberById(memberId);
                    if(member != null){
                        if(Strings.isNotBlank(member.getTelNumber())){
                            legitimacyReceiverIdsList.add(memberId);
                        }else{
                            if(noTelNumberMembers != null){
                                noTelNumberMembers += separator + member.getName();
                            }else{
                                noTelNumberMembers = member.getName();
                            }
                        }
                    }
                }
                catch (BusinessException e) {
                    log.error("", e);
                }
            }
        }
        
        //有合法的接收者
        if(!legitimacyReceiverIdsList.isEmpty()){
            Long[] receiverIdsArray = (Long[])legitimacyReceiverIdsList.toArray(new Long[legitimacyReceiverIdsList.size()]);
            mobileMessageManager.sendMobilePersonMessage(content, MobileConstants.getCurrentId(), new Date(), receiverIdsArray);
            //存在尚未填写手机号码的用户
            if(noTelNumberMembers != null ){
                super.rendJavaScript(response,"parent.showSendResult('" + noTelNumberMembers + "');");
            }else{
                super.rendJavaScript(response,"parent.showSendResult();");
            }
        }//所有用户均未填写手机号码
        else{
            super.rendJavaScript(response,"parent.showErrorResult();");
        }

        return null;
    }
    public ModelAndView showMessageSettingModel(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/message/messageSettingModel");
    	return modelAndView;
    }
    /**
     * 个人消息转移设置 - 显示
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showMessageSetting(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/message/messageSetting");
        
        User user = CurrentUser.get();
        long memberId = user.getId();
        long accountId = user.getAccountId();
        long loginAccountId = user.getLoginAccount();
        
        String type = request.getParameter("messageType");
        String fromModel = request.getParameter("fromModel");
        modelAndView.addObject("fromModel", fromModel);
        //所有的消息通道
        List<MessagePipeline> allTypes = messagePipelineManager.getAllMessagePipeline();
        
        if(Strings.isBlank(type)){//切换消息类型后
        	type = allTypes.get(0).getName();
        }
        
        modelAndView.addObject("allTypes", allTypes);
        
        MessagePipeline messagePipeline = messagePipelineManager.getMessagePipeline(type);
        
        String errorMsg = messagePipeline.isAllowSetting(user);
        //没有异常，标示可以配置
        if(Strings.isBlank(errorMsg)){
        	List<Integer> _enabledAppEnum = messagePipeline.getAllowSettingCategory(user);
        	if(_enabledAppEnum != null && !_enabledAppEnum.isEmpty()){
	        	modelAndView.addObject("enabledAppEnum", _enabledAppEnum); //可配的应用
        	}
        
	        Map<Integer, Set<String>> userMessageConfigMap = userMessageFilterConfigManager.getUserMessageConfig(memberId, type);
	        modelAndView.addObject("messageConfigMap", userMessageConfigMap);
	        
	        //公告
	        List<BulType> typeList = bulDataManager.getAllTypeListExcludeDept();
	        typeList = OuterWorkerAuthUtil.combineList(typeList,bulDataManager.getCustomAllTypeList());
	        modelAndView.addObject("bulletinTypes", typeList);
	        List<BulType> groupTypeList = bulDataManager.groupAllBoardList();
	        modelAndView.addObject("groupBulletinTypes", groupTypeList);
	        
	        //新闻
	        List<NewsType> newsTypeList =newsDataManager.getAllTypeList(loginAccountId);
	        newsTypeList = OuterWorkerAuthUtil.combineList(newsTypeList,newsDataManager.getAllCustomTypeList(loginAccountId));
	        modelAndView.addObject("newsTypes", newsTypeList);
	        List<NewsType> groupNewsTypeList =newsDataManager.getGroupAllTypeList();
	        modelAndView.addObject("groupNewsTypes", groupNewsTypeList);
	        
	        //讨论
	        List<V3xBbsBoard> bbsBoards = bbsBoardManager.getAllCorporationBbsBoard(loginAccountId);
	        bbsBoards = OuterWorkerAuthUtil.combineList(bbsBoards, bbsBoardManager.getAllCustomBbsBoard(loginAccountId));
	        modelAndView.addObject("bbsBoards", bbsBoards);
	        List<V3xBbsBoard> groupBbsBoards = bbsBoardManager.getAllGroupBbsBoard();
	        modelAndView.addObject("groupBbsBoards", groupBbsBoards);
	        
	        //调查
	        List<SurveyTypeCompose> inquiryTypes = inquiryManager.getUserIndexInquiryList(false,false);
	        inquiryTypes = OuterWorkerAuthUtil.combineList(inquiryTypes, inquiryManager.getAllCustomInquiryList());
	        modelAndView.addObject("inquiryTypes", inquiryTypes);
	        List<SurveyTypeCompose> groupInquiryTypes = inquiryManager.getGroupInquiryTypeList();
	        modelAndView.addObject("groupInquiryTypes", groupInquiryTypes);
	        
	        //可以访问的部门
	        Map<com.seeyon.v3x.space.Constants.SpaceType, List<SpaceModel>> accessSpace = spaceManager.getAccessSpace(memberId, accountId);
	        List<SpaceModel> deptSpace = accessSpace.get(com.seeyon.v3x.space.Constants.SpaceType.department);
	        List<SpaceModel> customDeptSpace = accessSpace.get(com.seeyon.v3x.space.Constants.SpaceType.custom);
	        if(deptSpace != null && !deptSpace.isEmpty()){
	            modelAndView.addObject("deptSpace", deptSpace);
	        }
	        if(customDeptSpace != null && !customDeptSpace.isEmpty()){
	            modelAndView.addObject("customDeptSpace", customDeptSpace);
	        }
	        //追加插件等其他消息系统应用ID
	        List<ExtendedMessageSystem> otherMsgSystemList = extendedMessageSystemManager.getAllExtendedSystem();
	        modelAndView.addObject("otherMsgSystemList", otherMsgSystemList);
	        
	        
	        //得到配置了指定栏目的空间
	        List<SpaceModel>   guestbookSectionList = spaceManager.getSpacesOfSection("guestbookSection", memberId, accountId);
	        guestbookSectionList = OuterWorkerAuthUtil.combineList(guestbookSectionList, spaceManager.getSpacesOfSection("customGuestbookSection", memberId, accountId));
	        modelAndView.addObject("guestbookSectionList", guestbookSectionList);
	        modelAndView.addObject("guestbookListLength", guestbookSectionList.size());
	        List<ProjectSummary> projectList = projectManager.getProjectList(user);
	        modelAndView.addObject("projectList", projectList);
	        modelAndView.addObject("projectListLength", projectList.size());
        }
        else{
        	modelAndView.addObject("errorMsg", errorMsg);
        }
        
        modelAndView.addObject("type", type);
        
        return modelAndView;
    }
    /**
     * 个人消息转移设置 - Form处理
     */
	public ModelAndView updateMessageSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<Integer, Set<String>> userMessageConfigMap = new HashMap<Integer, Set<String>>();
		long memberId = CurrentUser.get().getId();

		// 消息类型 系统消息、短信、邮件
		String type = request.getParameter("messageType");
		String fromModel = request.getParameter("fromModel");

		Map<Integer, Set<String>> oldUserMessageConfigMap = userMessageFilterConfigManager.getUserMessageConfig(memberId, type);
		int[] apps = { ApplicationCategoryEnum.bulletin.ordinal(),
				ApplicationCategoryEnum.news.ordinal(),
				ApplicationCategoryEnum.inquiry.ordinal(),
				ApplicationCategoryEnum.bbs.ordinal() };
		this.setPublicMessage(userMessageConfigMap, oldUserMessageConfigMap, request, apps);

		String[] selectedApps = request.getParameterValues("App");
		if (selectedApps != null && selectedApps.length > 0) {
			for (String appEnumStr : selectedApps) {
				Integer appEnum = Integer.parseInt(appEnumStr);
				
				if(appEnum != ApplicationCategoryEnum.bulletin.ordinal() && appEnum != ApplicationCategoryEnum.news.ordinal() && 
						appEnum != ApplicationCategoryEnum.inquiry.ordinal() && appEnum != ApplicationCategoryEnum.bbs.ordinal()) {
					Set<String> configSet = new HashSet<String>();
					
					// 添加ALL
					if ("ALL".equals(request.getParameter("AllInput_" + appEnum))) {
						configSet.add(Constants.MessageFilterOption.ALL.name());
						// 是否添加协同onlyImportant项 ,存储数据为 "ALL" 或者 "ALL,onlyImportant"
						if (appEnum == ApplicationCategoryEnum.collaboration.ordinal()) {
							// 添加所勾选的应用子项
							String[] configOptions = request.getParameterValues("Option_" + appEnum);
							if (configOptions != null && configOptions.length > 0) {
								for (String optionValue : configOptions) {
									if (Constants.MessageFilterOption_Collaboration.onlyImportant.name().equals(optionValue)) {
										configSet.add(optionValue);
										break;
									}
								}
							}
						}
					} else {
						// 添加所勾选的应用子项
						String[] configOptions = request.getParameterValues("Option_" + appEnum);
						if (configOptions != null && configOptions.length > 0) {
							for (String optionValue : configOptions) {
								configSet.add(optionValue);
							}
						}
					}
					
					userMessageConfigMap.put(appEnum, configSet);
				}
			}
		}

		userMessageFilterConfigManager.saveUserMessageConfig(memberId, type, userMessageConfigMap);

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + com.seeyon.v3x.system.Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();

		return super.redirectModelAndView("/message.do?method=showMessageSetting&fromModel="+fromModel+"&messageType=" + type, "parent");
	}
    
    /**
	 * 设置公共信息-个人消息转移
	 */
	private void setPublicMessage(Map<Integer, Set<String>> userMessageConfigMap, Map<Integer, Set<String>> oldUserMessageConfigMap, HttpServletRequest request, int... apps) {
		if (apps != null && apps.length > 0) {
			for (int app : apps) {
				Set<String> configSet = new HashSet<String>();

				// 以前设置的所有单位级板块
				Set<String> oldConfigSet = null;
				if (oldUserMessageConfigMap != null) {
					oldConfigSet = oldUserMessageConfigMap.get(app);
					if (oldConfigSet != null && oldConfigSet.contains("ALL")) {
						oldConfigSet.remove("ALL");
					}
				} else {
					oldConfigSet = new HashSet<String>();
				}

				// 当前单位下所有单位级板块
				String[] allOptions = request.getParameterValues("Option_Hidden_" + app);
				List<String> allOptionsList = null;
				if (allOptions != null && allOptions.length > 0) {
					allOptionsList = CommonTools.parseArr2List(allOptions);
				} else {
					allOptionsList = new ArrayList<String>();
				}

				// 当前单位下选中的单位级板块
				String[] selectOptions = request.getParameterValues("Option_" + app);
				List<String> selectOptionsList = null;
				if (selectOptions != null && selectOptions.length > 0) {
					selectOptionsList = CommonTools.parseArr2List(selectOptions);
				} else {
					selectOptionsList = new ArrayList<String>();
				}

				// 当前单位下取消选中的单位级板块
				List<String> unSelectOptionsList = CommonTools.getReducedCollection(allOptionsList, selectOptionsList);

				List<String> resultLilst = CommonTools.getSumCollection(CommonTools.getReducedCollection(oldConfigSet, unSelectOptionsList), selectOptionsList);

				if(CollectionUtils.isNotEmpty(resultLilst)){
					configSet.addAll(resultLilst);
				}
				
				userMessageConfigMap.put(app, configSet);
			}
		}
	}
    
    /**
     * 删除在线交流消息的附件<br>
     * 关闭在线交流消息窗口时由AJAX调用
     */
    public void deleteAttachmentsOfPerMsg(String[] filesIdArray, String[] createDateArray){
        if(filesIdArray.length == 0){
            return;
        }
        try {
            for (int i=0; i<filesIdArray.length; i++) {
                Long fileId = Long.parseLong(filesIdArray[i]);
                Date createDate = Datetimes.parseDatetime(createDateArray[i]);
                fileManager.deleteFile(fileId, createDate, true);                
            }
        }
        catch (Exception e) {
        }
    }
    
    /**
     * 是否需要播放消息声音<br>
     * 现已在TOP页赋值,此方法暂未使用到.
     * @deprecated
     * @return
     */
    public boolean isEnableMsgSound(){
        boolean isEnableMsgSound = false;
        String enableMsgSoundConfig = this.systemConfig.get(IConfigPublicKey.MSG_HINT);
        if(enableMsgSoundConfig != null){
            isEnableMsgSound = "enable".equals(enableMsgSoundConfig);
        }
        if(!isEnableMsgSound){
            return false;
        }
        
        Long memberId = CurrentUser.get().getId();
        V3xOrgMember member = null;
        try {
            member = orgManager.getMemberById(memberId);
            orgManager.loadEntityProperty(member);
            String enableMsgSound = member.getProperty("enableMsgSound");
            isEnableMsgSound = "true".equals(enableMsgSound);
        }
        catch (BusinessException e) {
            log.warn("",e);
        }
        return isEnableMsgSound;
    }

    /**
     * 邮件消息发送测试，AJAX调用
     * @param smtpHostName
     * @param sysEmailAddress
     * @param emailPassword
     * @param recEmailAddress
     * @return
     */
    public boolean testSendEMail(String smtpHostName, String sysEmailAddress, String emailPassword, String recEmailAddress,String userName,String smtpPort){
        if(Strings.isBlank(userName) || Strings.isBlank(smtpHostName) || Strings.isBlank(sysEmailAddress) || Strings.isBlank(emailPassword) || Strings.isBlank(recEmailAddress)|| Strings.isBlank(smtpPort)){
            return false;
        }
        return messageMailManager.testEmailSend(smtpHostName, sysEmailAddress, emailPassword, recEmailAddress,userName,smtpPort);
    }
    
    /**
     * 私人通讯录发送短信
     * @return
     */
    public ModelAndView showSendPersonalSMSDlg(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("v3xmain/message/sendPersonalSMS");
        return modelAndView;
    }
    
    /**
     * Form提交处理
     * @return
     */
    public ModelAndView sendPersonalSMS(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String phoneMembers = request.getParameter("phoneMembers");
        String content = request.getParameter("content");
        mobileMessageManager.sendPersonalMessage(content, MobileConstants.getCurrentId(), new Date(), phoneMembers);
        super.rendJavaScript(response,"parent.showSendPersonalSMSResult();");
        return null;
    }
    
    /**
     * 更新系统消息未读状态
     * @param id
     */
    public void updateSystemMessageState(long id) {
    	try {
			this.userMessageManager.updateSystemMessageState(id);
		} catch (Exception e) {
			log.error("更新系统消息未读状态失败:" + e);
		}
    }
    
    /**
     * 更新用户系统消息未读状态
     */
    public void updateSystemMessageStateByUser() {
    	User user = CurrentUser.get();
    	try {
			this.userMessageManager.updateSystemMessageStateByUser(user.getId());
		} catch (Exception e) {
			log.error("更新用户系统消息未读状态失败:" + e);
		}
    }

    /**
     * 检查工作范围方法</br>
     * 统一调用公共组件中检查工作范围的代码Functions.checkLevelScope
     * @param onlineUserList
     * @param user
     * @author lilong
     * @date 2012-04-10
     * @return
     */
    private List<OnlineUserModel> checkWorkScope(List<OnlineUserModel> onlineUserList, User user) {
    	//单位工作范围检查
    	for (int j = 0; j < onlineUserList.size(); j++) {
    		OnlineUserModel onlineUser = onlineUserList.get(j);
			if(!Functions.checkLevelScope(user.getId(), onlineUser.getId())){
				onlineUserList.remove(j);
				j--;
			}
		}
    	return onlineUserList;
    }
    
	/**
	 * 获取部门在线人员列表
	 */
	private Map<Long, Set<OnlineUserModel>> getDepartmentOnlineUser(Map<String,V3xOrgDepartment> deptMap, V3xOrgAccount account, User user, List<V3xOrgEntity> internalRight) throws Exception {
		Map<Long, Set<OnlineUserModel>> departmentOfOnlineUsers = new HashMap<Long, Set<OnlineUserModel>>(deptMap.size());
		Long currentAccountId = account.getId();
		List<OnlineUser> onlineUserSet = onLineManager.getOnlineList(currentAccountId);
		if (onlineUserSet != null && !onlineUserSet.isEmpty()) {
			int accountLevelScope = orgManager.getAccountById(currentAccountId).getLevelScope();
			for (OnlineUser u : onlineUserSet) {
			    boolean canDisplay = accountLevelScope < 0 ? true : canDisplay(user, u, internalRight);

				if(departmentOfOnlineUsers.get(u.getDepartmentId()) ==null){
					departmentOfOnlineUsers.put(u.getDepartmentId(), new HashSet<OnlineUserModel>());
				}
				if (u == null || u.getAccoutId() == null || u.getDepartmentPath() == null) {
					continue;
				}
				if (u.getAccoutId().equals(currentAccountId)) { // 主岗所在单位
					if (canDisplay) {
						String[] parentString = u.getDepartmentPath().split("\\.");
						String parentPath ="";
						for(int i =0;i<parentString.length;i++){
							if(i==0){
								parentPath = parentString[i];
							}else{
								parentPath += "."+parentString[i];
								Set<OnlineUserModel> set = departmentOfOnlineUsers.get(deptMap.get(parentPath).getId());
								if(set ==null){
									departmentOfOnlineUsers.put(deptMap.get(parentPath).getId(), new HashSet<OnlineUserModel>());
								}
								departmentOfOnlineUsers.get(deptMap.get(parentPath).getId()).add(new OnlineUserModel(u));
							}
						}
					}
				} else {// 本单位副岗 查找兼职单位信息
					List<OnlineUser.SecondePost> secondPosts = u.getSecondePosts();
					if (secondPosts != null) {
						for(OnlineUser.SecondePost secondPost:secondPosts){
							if(secondPost.getAccountId().equals(currentAccountId)){
								OnlineUserModel m = new OnlineUserModel(u);
								m.setDepartmentName(secondPost.getDepartmentSimpleName());
								m.setPostName(secondPost.getPostName());
								m.setPluralist(true);
								
                                if (canDisplay&&departmentOfOnlineUsers.get(secondPost.getDepartmentId()) != null) {
									departmentOfOnlineUsers.get(secondPost.getDepartmentId()).add(new OnlineUserModel(u));
									String[] parentString = secondPost.getDepartmentPath().split("\\.");
									String parentPath ="";
									for(int i =0;i<parentString.length;i++){
										if(i==0){
											parentPath = parentString[i];
										}else{
											parentPath += "."+parentString[i];
										}
										if(deptMap.get(parentPath)!=null){
										    departmentOfOnlineUsers.get(deptMap.get(parentPath).getId()).add(new OnlineUserModel(u));
										}
									}
								}
							}
						}
					}
				}
			}
	    }	
		return departmentOfOnlineUsers;
	}
}