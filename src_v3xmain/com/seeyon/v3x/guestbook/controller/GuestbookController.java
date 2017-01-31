package com.seeyon.v3x.guestbook.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
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

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.Constants;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.guestbook.domain.LeaveWordVo;
import com.seeyon.v3x.guestbook.manager.GuestbookManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.domain.SpacePage;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;


public class GuestbookController extends BaseController {
	private static final Log log = LogFactory.getLog(GuestbookController.class);
    
    private OrgManager orgManager;
	private ProjectManager projectManager;
    private GuestbookManager guestbookManager;
    private UserMessageManager userMessageManager;	
    private SpaceManager spaceManager;
    private StaffInfoManager staffInfoManager;
    private PeopleRelateManager peoplerelateManager;
    private ProjectPhaseEventManager projectPhaseEventManager;
    
    public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager)
    {
        this.peoplerelateManager = peoplerelateManager;
    }
	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

    public void setguestbookManager(GuestbookManager guestbookManager) {
        this.guestbookManager = guestbookManager;
    }
    
    public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}
    //获取团队空间名称
    public String ajaxGetSpaceName(long idStr) throws Exception{
    	String spaceName = this.spaceManager.getSpace(idStr).getSpaceName();
		return spaceName;
	}

    /**
     * 显示部门空间的留言
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showSpaceLeaveWord(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/showSpaceLeaveWord");
        User user = CurrentUser.get();
        SpacePage page = (SpacePage)request.getAttribute("org.apache.jetspeed.Page");
		EnumMap<PortletEntityProperty.PropertyName, String> pageParams = spaceManager.getPortletEntityProperty(page.getPath());
		
		String ownerId = pageParams.get(PortletEntityProperty.PropertyName.ownerId);
		
        long departmentId = user.getDepartmentId();
        if(Strings.isNotEmpty(ownerId)){
            departmentId = Long.parseLong(ownerId);
        }
        List<LeaveWord> leaveWordList = null;
        leaveWordList = guestbookManager.getLeaveWords4Space(departmentId, 4);
        modelAndView.addObject("departmentId", departmentId);
        modelAndView.addObject("leaveWordList", leaveWordList);
        
        return modelAndView;
    }
    
    /**
     * 显示留言对话框
     */
    public ModelAndView showLeaveWordDlg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("v3xmain/guessbook/leaveWordDlg").addObject("from", request.getParameter("from"));
    }
    
    /**
     * 根据id显示留言内容
     * @param request
     * @param response
     * @return
     * @throws Exception
     * by Yongzhang 2008-6-12
     */
    public ModelAndView showLeaveWordContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/leaveWordContent");
        String Id = request.getParameter("leavewordsId");
        
        LeaveWord leaveword=guestbookManager.getLeaveWordsById(Long.parseLong(Id));
        modelAndView.addObject("Id", Id) ;
        if(leaveword != null){
        	modelAndView.addObject("leaveword", leaveword);	
        }else{ 
            modelAndView.addObject("closeWidow", "true");
        }
        
        return modelAndView;
    }
    
    /**
     * 保存留言
     */
	public ModelAndView saveLeaveWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String departmentIdStr = request.getParameter("departmentId");
		boolean sendMessage = Strings.isNotBlank(request.getParameter("c"));

		User user = CurrentUser.get();
		Long deptOrProjectId = NumberUtils.toLong(departmentIdStr, user.getDepartmentId());
		String leaveWordContent = request.getParameter("leaveWordContent");
		LeaveWord leaveWord = guestbookManager.saveLeaveWord(user.getId(), deptOrProjectId, leaveWordContent);
		
		if(sendMessage) {
			if ("project".equals(request.getParameter("project"))) {
				try {
					// 项目信息 需要加载项目进度 级联删除阶段下的进展
					ProjectCompose projectCompose = projectManager.getProjectComposeByID(deptOrProjectId, true);
					String projectName = projectCompose.getProjectSummary().getProjectName();
					
					List<Long> receiverIds = this.getProjectMsgReceivers(projectCompose);
					if(CollectionUtils.isNotEmpty(receiverIds)) {
						Collection<MessageReceiver> msgReceiver = MessageReceiver.get(deptOrProjectId, receiverIds, "message.link.project.leaveWord", Constants.LinkOpenType.open, deptOrProjectId, true);
						
						userMessageManager.sendSystemMessage(MessageContent.get("project.addleaveword.project.new", user.getName(), projectName,leaveWordContent),
								ApplicationCategoryEnum.guestbook, user.getId(), msgReceiver, deptOrProjectId);
					}
				} 
				catch (Exception e) {
					log.error("获取项目信息、给项目组成员发送留言系统消息过程中出现异常：", e);
				}
			} 
			else {
				try {
					List<Long> receiverIds = this.getDepartmentMsgReceivers(CurrentUser.get().getId(), deptOrProjectId);
					
					if(CollectionUtils.isNotEmpty(receiverIds)) {
						Collection<MessageReceiver> msgReceiver = MessageReceiver.get(deptOrProjectId, receiverIds, "message.link.department.leaveWord", Constants.LinkOpenType.open, deptOrProjectId, false);
						String deptName = this.orgManager.getDepartmentById(deptOrProjectId).getName();
						
						userMessageManager.sendSystemMessage(MessageContent.get("project.addleaveword.department.new", user.getName(), deptName,leaveWordContent),
								ApplicationCategoryEnum.guestbook, user.getId(), msgReceiver, deptOrProjectId);
					}
				} 
				catch(Exception e) {
					log.error("获取部门成员，为其发送留言系统消息过程中出现异常：", e);
				}
			}
		}

		String jsAction = "more".equals(request.getParameter("from")) ? "parent.ok();" : ("parent.ok('" + leaveWord.getCreateTime().getTime() + "');");
		super.rendJavaScript(response, jsAction);
		return null;

	}
	
	public boolean deleteAjaxLeaveWord(String id) throws NumberFormatException, BusinessException{
		guestbookManager.clearSubLeaveWords(Long.valueOf(id));
		return true;
	}
	public boolean deleteAjaxBanchLeaveWords(String idStr) throws NumberFormatException, BusinessException{
		if("".equals(idStr)){
			return false;
		}
		guestbookManager.clearBanchSubLeaveWords(idStr);
		return true;
	}
	
	public boolean saveAjaxLeaveWord(String departmentIdStr,String sendMessage,String leaveWordContent,String replyId,String replyerId,String project) throws Exception {
		return this.saveAjaxLeaveWord(departmentIdStr, sendMessage, leaveWordContent, replyId, replyerId, project, null);
	}
	
	public boolean saveAjaxLeaveWord(String departmentIdStr,String sendMessage,String leaveWordContent,String replyId,String replyerId,String project,Long projectPhaseId) throws Exception {
		User user = CurrentUser.get();
		Long deptOrProjectId = NumberUtils.toLong(departmentIdStr, user.getDepartmentId());
		Long replyIdTemp = null;
		Long replyerIdTemp = null;
		if(!"no".equals(replyId)){
			replyIdTemp = Long.valueOf(replyId);
		}
		if(!"no".equals(replyerId)){
			replyerIdTemp = Long.valueOf(replyerId);
		}
		
		// 回复二级留言时，如果一级留言已删除，则视为一级留言
		if (replyIdTemp != null) {
			LeaveWord replyWord = guestbookManager.getLeaveWordsById(replyIdTemp);
			if (replyWord == null) {
				replyIdTemp = null;
				replyerIdTemp = null;
			}
		}
		
		LeaveWord leaveWord = guestbookManager.saveLeaveWordNew(user.getId(), deptOrProjectId, leaveWordContent,replyIdTemp,replyerIdTemp);
		
		if ("project".equals(project)) {
			//项目留言,存入该项目下当前阶段
    		if(projectPhaseId != null && projectPhaseId != TaskConstants.PROJECT_PHASE_ALL){
    			ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.bbs.key(), leaveWord.getId(), projectPhaseId);
    			projectPhaseEventManager.save(projectPhaseEvent);
    		}
		}
		
		if("true".equals(sendMessage)) {
			if ("project".equals(project)) {
				try {
					// 项目信息 需要加载项目进度 级联删除阶段下的进展
					ProjectCompose projectCompose = projectManager.getProjectComposeByID(deptOrProjectId, true);
					
					String projectName = Strings.nobreakSpaceToSpace(projectCompose.getProjectSummary().getProjectName());
					
					List<Long> receiverIds = this.getProjectMsgReceivers(projectCompose);
					if(CollectionUtils.isNotEmpty(receiverIds)) {
						Collection<MessageReceiver> msgReceiver = MessageReceiver.get(deptOrProjectId, receiverIds, "message.link.project.leaveWord", Constants.LinkOpenType.open, deptOrProjectId, true);
						
						userMessageManager.sendSystemMessage(MessageContent.get("project.addleaveword.project.new", user.getName(), projectName,leaveWordContent),
								ApplicationCategoryEnum.guestbook, user.getId(), msgReceiver,deptOrProjectId);
					}
				} catch (Exception e) {
					log.error("获取项目信息、给项目组成员发送留言系统消息过程中出现异常：", e);
				}
			} else {
				try {
					List<Long> receiverIds = null;
					//团队空间留言
					if ("custom".equals(project)) {
						receiverIds = this.getSpaceMsgReceivers(CurrentUser.get().getId(), deptOrProjectId);
						if(CollectionUtils.isNotEmpty(receiverIds)) {
							Collection<MessageReceiver> msgReceiver = MessageReceiver.get(deptOrProjectId, receiverIds, "message.link.space.leaveWord", Constants.LinkOpenType.open, deptOrProjectId, false);
							String spaceName = this.spaceManager.getSpace(deptOrProjectId).getSpaceName();
							userMessageManager.sendSystemMessage(MessageContent.get("project.addleaveword.space.new", user.getName(), spaceName,leaveWordContent),
									ApplicationCategoryEnum.guestbook, user.getId(), msgReceiver,deptOrProjectId);
						}
					} else {
						receiverIds = this.getDepartmentMsgReceivers(CurrentUser.get().getId(), deptOrProjectId);
						if(CollectionUtils.isNotEmpty(receiverIds)) {
							Collection<MessageReceiver> msgReceiver = MessageReceiver.get(deptOrProjectId, receiverIds, "message.link.department.leaveWord", Constants.LinkOpenType.open, deptOrProjectId, false);
							String deptName = this.orgManager.getDepartmentById(deptOrProjectId).getName();
							
							userMessageManager.sendSystemMessage(MessageContent.get("project.addleaveword.department.new", user.getName(), deptName,leaveWordContent),
									ApplicationCategoryEnum.guestbook, user.getId(), msgReceiver,deptOrProjectId);
						}
					}
				} catch(Exception e) {
					log.error("获取成员，为其发送留言系统消息过程中出现异常：", e);
				}
			}
		}
		return true;
	}
	/**
	 * 部门留言发送消息提醒时，获取部门成员中所要提醒的对象
	 */
	private List<Long> getDepartmentMsgReceivers(Long userId, Long departmentId) throws BusinessException {
		List<Object[]> _issueAreas = this.spaceManager.getSecuityOfDepartment(departmentId);
		StringBuffer entityInfos = new StringBuffer();
		for(Object[] arr : _issueAreas) {
			entityInfos.append(StringUtils.join(arr, "|") + ",");
		}
		
		Set<V3xOrgMember> members = this.orgManager.getMembersByTypeAndIds(entityInfos.substring(0, entityInfos.length() - 1));
		List<Long> receiverIds = CommonTools.getEntityIds(members);
		receiverIds.remove(userId);
		
		return receiverIds;
	}
	
	/**
	 * 团队空间留言发送消息提醒时，获取空间成员中所要提醒的对象
	 */
	private List<Long> getSpaceMsgReceivers(Long userId, Long spaceId) throws BusinessException {
		List<V3xOrgMember> members = this.spaceManager.getSpaceMemberBySecurity(spaceId, -1);
		List<Long> receiverIds = CommonTools.getEntityIds(members);
		receiverIds.remove(userId);
		return receiverIds;
	}

	/**
	 * 项目留言发送消息提醒时，获取项目中所要提醒的对象
	 */
	private List<Long> getProjectMsgReceivers(ProjectCompose projectCompose) {
		List<Long> result = new ArrayList<Long>();
		
		Set<Long> msgMemberIds = new HashSet<Long>();
		CommonTools.addAllIgnoreEmpty(msgMemberIds, CommonTools.getEntityIds(projectCompose.getPrincipalLists()));
		CommonTools.addAllIgnoreEmpty(msgMemberIds, CommonTools.getEntityIds(projectCompose.getAssistantLists()));
		CommonTools.addAllIgnoreEmpty(msgMemberIds, CommonTools.getEntityIds(projectCompose.getMemberLists()));
		CommonTools.addAllIgnoreEmpty(msgMemberIds, CommonTools.getEntityIds(projectCompose.getChargeLists()));
		CommonTools.addAllIgnoreEmpty(msgMemberIds, CommonTools.getEntityIds(projectCompose.getInterfixLists()));
		
		msgMemberIds.remove(CurrentUser.get().getId());
		
		result.addAll(msgMemberIds);
		return result;
	}
    
    /**
     * 清除留言
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView clearLeaveWord(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String departmentId = request.getParameter("departmentId");
        String idStr = request.getParameter("id");
        long id = Long.parseLong(idStr);
        guestbookManager.clearLeaveWord(id);
        return super.redirectModelAndView("/guestbook.do?method=moreLeaveWord&departmentId="+departmentId + "&isManager=true");
    }
    public LeaveWord getImgUrl(LeaveWord leaveWord){
        String urlStr = "/seeyon/apps_res/v3xmain/images/personal/pic.gif";
        try {
            StaffInfo staff = staffInfoManager.getStaffInfoById(leaveWord.getCreatorId());
    		if(staff != null) {
    			String issuerImage = staff.getSelf_image_name();
    			if(StringUtils.isNotBlank(issuerImage)){
    				if(issuerImage.startsWith("fileId")){
    					urlStr = "/seeyon/fileUpload.do?method=showRTE&"+issuerImage+"&type=image";
    				}else{
    					urlStr = "/seeyon/apps_res/v3xmain/images/personal/"+issuerImage;
    				}
    			}
     		}
		} catch (Exception e) {
			 log.error("留言板列表加载异常:", e);
		}
		leaveWord.setUrlImage(urlStr);
    	return leaveWord;
    }
    public PeopleRelate getMyImgUrl(PeopleRelate relate){
        String urlStr = "/seeyon/apps_res/v3xmain/images/personal/pic.gif";
        try {
            StaffInfo staff = staffInfoManager.getStaffInfoById(relate.getRelateMemberId());
    		if(staff != null) {
    			String issuerImage = staff.getSelf_image_name();
    			if(StringUtils.isNotBlank(issuerImage)){
    				if(issuerImage.startsWith("fileId")){
    					urlStr = "/seeyon/fileUpload.do?method=showRTE&"+issuerImage+"&type=image";
    				}else{
    					urlStr = "/seeyon/apps_res/v3xmain/images/personal/"+issuerImage;
    				}
    			}
     		}
		} catch (Exception e) {
			 log.error("个性化头像异常:", e);
		}
		relate.setSelImgUrl(urlStr);
    	return relate;
    }
    public ModelAndView banchDeleteList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/leaveWordListDel");
        String departmentIdStr = request.getParameter("departmentId");
        String project = request.getParameter("project");//项目参数
        String custom = request.getParameter("custom");//空间参数
    	if(custom == null){
    		custom ="false";
    	}
        User user = CurrentUser.get();
        String preFix = "";
        Long departmentId = null; 
        if(Strings.isNotBlank(departmentIdStr)){
            departmentId = Long.parseLong(departmentIdStr);
            V3xOrgDepartment entity = orgManager.getDepartmentById(departmentId);
            if(entity != null){
                preFix = entity.getName();
            }
            else{
                V3xOrgAccount account = orgManager.getAccountById(departmentId);
                if(account != null){
                    preFix = account.getShortname();
                }else{
                	SpaceFix spaceEntity = spaceManager.getSpace(departmentId);
                	if(spaceEntity != null){
                		preFix = spaceEntity.getSpaceName();
                	}
                }
            }
        }else{
            departmentId = user.getDepartmentId();
        }
		String nowPagePara = request.getParameter("page");
		int nowPage = 1; 		//当前页
		int size = 0;  			//总条数
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 15);//每页显示条数，默认每页显示15条，用户可自定义
		
		int pages = 1;			//总页数
		int beginReply = 0; 	//开始显示的记录
		
		size = guestbookManager.getSubLeaveWordsCount(departmentId);
		pages = (size + pageSize - 1) / pageSize;	// 总页数
		if(pages==0){
			pages = 1;
		} 
		if(Strings.isNotBlank(nowPagePara) && !nowPagePara.equals("1")){
            nowPage = NumberUtils.toInt(nowPagePara, 1);
			beginReply = (nowPage-1) * pageSize;
		}
        
		List<LeaveWord> leaveWordList = guestbookManager.getPageSizeLeaveWord(departmentId,beginReply, pageSize);
        List<LeaveWordVo> leaveWordListVo= new ArrayList<LeaveWordVo>();
        for (int i = 0; i < leaveWordList.size(); i++) {
        	LeaveWord leaveWordTemp = leaveWordList.get(i);
        	if(leaveWordTemp!=null){
        		LeaveWordVo leaveWordVo =  new LeaveWordVo();
	    		leaveWordVo.setLeaveWord(leaveWordTemp);
	    		List<LeaveWord> leaveWordReplyList = guestbookManager.getReplyLeaveWord(leaveWordTemp.getId());
	    		if(leaveWordReplyList.size()>0){
	    			leaveWordVo.setHasNodes(true);
	    			leaveWordVo.setSubLeaveWord(leaveWordReplyList);
	    		}else{
	    			leaveWordVo.setHasNodes(false);
	    		}
	    		leaveWordListVo.add(leaveWordVo);
        	}
        }
        
        V3xOrgDepartment myDept = orgManager.getDepartmentById(CurrentUser.get().getDepartmentId());
        //List<PeopleRelate> relativeMemberlist = peoplerelateManager.getPeopleRelatedList(CurrentUser.get().getId());
        List<PeopleRelate> relativeMemberlistTemp = new ArrayList<PeopleRelate>();
        Map<RelationType, List<PeopleRelate>> peopleRelatesList = new HashMap<RelationType, List<PeopleRelate>>();
        try
        {
            peopleRelatesList = peoplerelateManager.getAllPeopleRelates(CurrentUser.get().getId(), true);
        }
        catch (Exception e)
        {
            logger.error("获取关联人员失败", e);
        }
        List<PeopleRelate> leaderlist = peopleRelatesList.get(RelationType.leader);
        List<PeopleRelate> assistantlist = peopleRelatesList.get(RelationType.assistant);
        List<PeopleRelate> juniorlist = peopleRelatesList.get(RelationType.junior);
        List<PeopleRelate> confrerelist = peopleRelatesList.get(RelationType.confrere);
        relativeMemberlistTemp.addAll(leaderlist);
        relativeMemberlistTemp.addAll(assistantlist);
        relativeMemberlistTemp.addAll(juniorlist);
        relativeMemberlistTemp.addAll(confrerelist);
        List<PeopleRelate> relativeMemberlist = new ArrayList<PeopleRelate>();
        
        for (int i = 0; i < relativeMemberlistTemp.size(); i++) {
        	PeopleRelate t =  getMyImgUrl(relativeMemberlistTemp.get(i));
        	if(i<9){
        		relativeMemberlist.add(t);
        	}else{
        		break;
        	}
		}
        
        
        List<V3xOrgMember> departmentMemnerlist = orgManager.getMembersByDepartment(CurrentUser.get().getDepartmentId(),true);
       
    	List<PeopleRelate> myMemberListTemp = new ArrayList<PeopleRelate>();
    	if(departmentMemnerlist != null){
    		for(V3xOrgMember member : departmentMemnerlist){
    			StaffInfo sta = staffInfoManager.getStaffInfoById(member.getId());
    			PeopleRelate p = new PeopleRelate();
    			p.setRelatedMemberId(CurrentUser.get().getId());
    			p.setRelateMemberId(member.getId());
    			p.setRelateMemberEmail(member.getEmailAddress());
    			p.setRelateMemberName(member.getName());
    			p.setRelateMemberDept(myDept.getName());
    			p.setRelateMemberTel(member.getProperty("officeNum"));
    			p.setRelateMemberHandSet(member.getTelNumber());
    			if(sta != null){
    				p.setRelateImageId(sta.getImage_id());
    				p.setRelateImageDate(sta.getImage_datetime());
    			}
    			myMemberListTemp.add(p);
    		}
    	}
    	List<PeopleRelate> myMemberList = new ArrayList<PeopleRelate>();
    	
        for (int i = 0; i < myMemberListTemp.size(); i++) {
        	PeopleRelate t =  getMyImgUrl(myMemberListTemp.get(i));
        	if(i<9){
        		myMemberList.add(t);
        	}else{
        		break;
        	}
		}
    	
        modelAndView.addObject("relativeMemberlist", relativeMemberlist);
        modelAndView.addObject("relativeMemberlistSize", relativeMemberlist.size());
        modelAndView.addObject("departmentMemnerlist", myMemberList);
        modelAndView.addObject("departmentMemnerlistSize", myMemberList.size());
        modelAndView.addObject("myDept", myDept);
        
        modelAndView.addObject("size", size);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("pages", pages);
        modelAndView.addObject("page", nowPage);
        modelAndView.addObject("departmentId", departmentId);
        modelAndView.addObject("preFix", preFix);
        modelAndView.addObject("leaveWordList", leaveWordListVo);
        modelAndView.addObject("leaveWordCount", guestbookManager.getLeaveWordsCount(departmentId));  
        if("true".equals(project)){ //项目留言传递参数
        	modelAndView.addObject("project", true);
        	modelAndView.addObject("custom", custom);
        	ProjectCompose projectCompose = projectManager.getProjectComposeByID(departmentId, true);
            modelAndView.addObject("isProjectManager", this.isManager(projectCompose, null));
            modelAndView.addObject("dispProjectName", projectCompose.getProjectSummary().getProjectName());
        }
        else{
        	modelAndView.addObject("project", false);
        	modelAndView.addObject("custom", custom);
        	modelAndView.addObject("isProjectManager", false);
            List<Long> managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
            List<Long> managerSpaces = spaceManager.getCanManagerSpace(user.getId());
            modelAndView.addObject("isSpaceManager", (managerDepartments.contains(departmentId) || managerSpaces.contains(departmentId)));
        }
        return modelAndView;
    }
    public ModelAndView moreLeaveWordModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/leaveWordDepartmentModel");
    	String project = request.getParameter("project");
    	String custom = request.getParameter("custom");//空间参数
    	if(custom == null){
    		custom ="false";
    	}
    	String departmentId = request.getParameter("departmentId");
    	String fromModel = request.getParameter("fromModel");
    	if (!"true".equals(project)) {
    		User user = CurrentUser.get();
    		long memberId = user.getId();
    		long accountId = user.getAccountId();
    		boolean flag = false;
    		List<SpaceModel> allGuestbookSectionList = new ArrayList<SpaceModel>();
    		//得到配置了指定栏目的空间
    		List<SpaceModel>   guestbookSectionList = spaceManager.getSpacesOfSection("guestbookSection", memberId, accountId);
    		List<SpaceModel>   customGuestbookSectionList = spaceManager.getSpacesOfSection("customGuestbookSection", memberId, accountId);
    		allGuestbookSectionList.addAll(guestbookSectionList);
    		allGuestbookSectionList.addAll(customGuestbookSectionList);
    		for (SpaceModel spaceModel : allGuestbookSectionList) {
    			long spaceId = spaceModel.getEntityId();
    			if (spaceId == Long.parseLong(departmentId)) {
    				flag = true;
    			}
    		}
    		if (!flag) {
    			super.rendJavaScript(response,"alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.guestbook.resources.i18n.GuestbookResources", "guestbook_del_section") + "');window.close();");
    			return null;
    		}
    	} else {
    		ProjectCompose projectCompose = projectManager.getProjectComposeByID(Long.parseLong(departmentId), false);
    		if (projectCompose.getProjectSummary().getProjectState() == 4) {
    			super.rendJavaScript(response,"alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.guestbook.resources.i18n.GuestbookResources", "guestbook_del_section") + "');window.close();");
    			return null;
    		}
    	}
    	modelAndView.addObject("project", project);
    	modelAndView.addObject("custom", custom);
    	modelAndView.addObject("departmentId", departmentId);
    	modelAndView.addObject("fromModel", fromModel);
    	return modelAndView;
    	
    }
    /**
     * 更多留言
     */
    public ModelAndView moreLeaveWordNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String project = request.getParameter("project");//项目参数
    	String custom = request.getParameter("custom");//空间参数
    	if(custom == null){
    		custom ="";
    	}
        ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/leaveWordDepartmentList");
        String departmentIdStr = request.getParameter("departmentId");
        String fromModel =  request.getParameter("fromModel");
        modelAndView.addObject("fromModel", fromModel);
        User user = CurrentUser.get();
        String preFix = "";
        Long departmentId = null; 
        if(Strings.isNotBlank(departmentIdStr)){
            departmentId = Long.parseLong(departmentIdStr);
            V3xOrgDepartment entity = orgManager.getDepartmentById(departmentId);
            if(entity != null){
                preFix = entity.getName();
            }
            else{
                V3xOrgAccount account = orgManager.getAccountById(departmentId);
                if(account != null){
                    preFix = account.getShortname();
                }else{
                	SpaceFix spaceEntity = spaceManager.getSpace(departmentId);
                	if(spaceEntity != null){
                		preFix = spaceEntity.getSpaceName();
                	}
                }
            }
        }else{
            departmentId = user.getDepartmentId();
        }
        
        Long phaseId = null;
        ProjectCompose projectCompose = null;
        if("true".equals(project)){
        	projectCompose = projectManager.getProjectComposeByID(departmentId, true);
        	//某个阶段|当前阶段|所有阶段
    		String phaseIds = request.getParameter("phaseId");
    		if(StringUtils.isNotBlank(phaseIds)){
    			phaseId = NumberUtils.toLong(phaseIds);
    		}else{
    			phaseId = projectCompose.getProjectSummary().getPhaseId();
    		}
        }
        
		String nowPagePara = request.getParameter("page");
		int nowPage = 1; 		//当前页
		int size = 0;  			//总条数
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 15);//每页显示条数，默认每页显示15条，用户可自定义
		
		int pages = 1;			//总页数
		int beginReply = 0; 	//开始显示的记录
		
		if("true".equals(project)){
			size = guestbookManager.getSubLeaveWordsCount(departmentId, phaseId);
		}else{
			size = guestbookManager.getSubLeaveWordsCount(departmentId);
		}
		
		pages = (size + pageSize - 1) / pageSize;	// 总页数
		if(pages==0){
			pages = 1;
		} 
		if(Strings.isNotBlank(nowPagePara) && !nowPagePara.equals("1")){
            nowPage = NumberUtils.toInt(nowPagePara, 1);
			beginReply = (nowPage-1) * pageSize;
		}
        
		List<LeaveWord> leaveWordList = null;
		if("true".equals(project)){
			leaveWordList = guestbookManager.getPageSizeLeaveWord(departmentId, phaseId, beginReply, pageSize);
		}else{
			leaveWordList = guestbookManager.getPageSizeLeaveWord(departmentId, beginReply, pageSize);
		}
		
        List<LeaveWordVo> leaveWordListVo= new ArrayList<LeaveWordVo>();
        for (int i = 0; i < leaveWordList.size(); i++) {
        	LeaveWord leaveWordTemp = leaveWordList.get(i);
        	if(leaveWordTemp!=null){
        		LeaveWordVo leaveWordVo =  new LeaveWordVo();
        		LeaveWord leaveWord = getImgUrl(leaveWordTemp);
	    		leaveWordVo.setLeaveWord(leaveWord);
	    		List<LeaveWord> leaveWordReplyList = guestbookManager.getReplyLeaveWord(leaveWord.getId());
	    		if(leaveWordReplyList.size()>0){
	    			leaveWordVo.setHasNodes(true);
	    			List<LeaveWord> leaveWordReplyListTemp = new ArrayList<LeaveWord>();
	    			for (int j = 0; j < leaveWordReplyList.size(); j++) {
	    				LeaveWord leaveWordSubTemp = getImgUrl(leaveWordReplyList.get(j));
	    				leaveWordReplyListTemp.add(leaveWordSubTemp);
					}
	    			leaveWordVo.setSubLeaveWord(leaveWordReplyListTemp);
	    		}else{
	    			leaveWordVo.setHasNodes(false);
	    		}
	    		leaveWordListVo.add(leaveWordVo);
        	}
        }
        V3xOrgDepartment myDept = orgManager.getDepartmentById(CurrentUser.get().getDepartmentId());
        List<PeopleRelate> relativeMemberlistTemp = new ArrayList<PeopleRelate>();
        Map<RelationType, List<PeopleRelate>> peopleRelatesList = new HashMap<RelationType, List<PeopleRelate>>();
        try
        {
            peopleRelatesList = peoplerelateManager.getAllPeopleRelates(CurrentUser.get().getId(), true);
        }
        catch (Exception e)
        {
            logger.error("获取关联人员失败", e);
        }
        List<PeopleRelate> leaderlist = peopleRelatesList.get(RelationType.leader);
        List<PeopleRelate> assistantlist = peopleRelatesList.get(RelationType.assistant);
        List<PeopleRelate> juniorlist = peopleRelatesList.get(RelationType.junior);
        List<PeopleRelate> confrerelist = peopleRelatesList.get(RelationType.confrere);
        relativeMemberlistTemp.addAll(leaderlist);
        relativeMemberlistTemp.addAll(assistantlist);
        relativeMemberlistTemp.addAll(juniorlist);
        relativeMemberlistTemp.addAll(confrerelist);
        List<PeopleRelate> relativeMemberlist = new ArrayList<PeopleRelate>();
        for (int i = 0; i < relativeMemberlistTemp.size(); i++) {
        	PeopleRelate t =  getMyImgUrl(relativeMemberlistTemp.get(i));
        	if(i<9){
        		relativeMemberlist.add(t);
        	}else{
        		break;
        	}
		}
        
        List<V3xOrgMember> departmentMemnerlist = orgManager.getMembersByDepartment(CurrentUser.get().getDepartmentId(),true);
       
    	List<PeopleRelate> myMemberListTemp = new ArrayList<PeopleRelate>();
    	if(departmentMemnerlist != null){
    		for(V3xOrgMember member : departmentMemnerlist){
    			StaffInfo sta = staffInfoManager.getStaffInfoById(member.getId());
    			PeopleRelate p = new PeopleRelate();
    			p.setRelatedMemberId(CurrentUser.get().getId());
    			p.setRelateMemberId(member.getId());
    			p.setRelateMemberEmail(member.getEmailAddress());
    			p.setRelateMemberName(member.getName());
    			p.setRelateMemberDept(myDept.getName());
    			p.setRelateMemberTel(member.getProperty("officeNum"));
    			p.setRelateMemberHandSet(member.getTelNumber());
    			if(sta != null){
    				p.setRelateImageId(sta.getImage_id());
    				p.setRelateImageDate(sta.getImage_datetime());
    			}
    			myMemberListTemp.add(p);
    		}
    	}
    	List<PeopleRelate> myMemberList = new ArrayList<PeopleRelate>();
    	 for (int i = 0; i < myMemberListTemp.size(); i++) {
         	PeopleRelate t =  getMyImgUrl(myMemberListTemp.get(i));
         	if(i<9){
         		myMemberList.add(t);
         	}else{
         		break;
         	}
 		}
    	
        modelAndView.addObject("relativeMemberlist", relativeMemberlist);
        modelAndView.addObject("relativeMemberlistSize", relativeMemberlist.size());
        modelAndView.addObject("departmentMemnerlist", myMemberList);
        modelAndView.addObject("departmentMemnerlistSize", myMemberList.size());
        modelAndView.addObject("myDept", myDept);
        modelAndView.addObject("size", size);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("pages", pages);
        modelAndView.addObject("page", nowPage);
        modelAndView.addObject("departmentId", departmentId);
        modelAndView.addObject("preFix", preFix);
        modelAndView.addObject("leaveWordList", leaveWordListVo);
        modelAndView.addObject("leaveWordCount", guestbookManager.getLeaveWordsCount(departmentId));  
        if("true".equals(project)){ //项目留言传递参数
        	modelAndView.addObject("project", true);
        	modelAndView.addObject("custom", custom);
            modelAndView.addObject("isProjectManager", this.isManager(projectCompose, null));
            modelAndView.addObject("dispProjectName", projectCompose.getProjectSummary().getProjectName());
            modelAndView.addObject("projectCompose", projectCompose);
        }
        else{
        	modelAndView.addObject("project", false);
        	modelAndView.addObject("custom", custom);
        	modelAndView.addObject("isProjectManager", false);
            List<Long> managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
            List<Long> managerSpaces = spaceManager.getCanManagerSpace(user.getId());
            modelAndView.addObject("isSpaceManager", (managerDepartments.contains(departmentId) || managerSpaces.contains(departmentId)));
        }
        return modelAndView;
    }
    /**
     * 更多留言
     */
    public ModelAndView moreLeaveWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("v3xmain/guessbook/leaveWordList");
        String departmentIdStr = request.getParameter("departmentId");
        String project = request.getParameter("project");//项目参数
        User user = CurrentUser.get();
        String preFix = "";
        Long departmentId = null; 
        if(Strings.isNotBlank(departmentIdStr)){
            departmentId = Long.parseLong(departmentIdStr);
            V3xOrgDepartment entity = orgManager.getDepartmentById(departmentId);
            if(entity != null){
                preFix = entity.getName();
            }
            else{
                V3xOrgAccount account = orgManager.getAccountById(departmentId);
                if(account != null){
                    preFix = account.getShortname();
                }
            }
        }else{
            departmentId = user.getDepartmentId();
        }
        List<LeaveWord> leaveWordList = guestbookManager.getAllLeaveWords(departmentId);
        modelAndView.addObject("departmentId", departmentId);
        modelAndView.addObject("preFix", preFix);
        modelAndView.addObject("leaveWordList", leaveWordList);
        modelAndView.addObject("leaveWordCount", guestbookManager.getLeaveWordsCount(departmentId));  
        if("true".equals(project)){ //项目留言传递参数
        	modelAndView.addObject("project", true);
        	ProjectCompose projectCompose = projectManager.getProjectComposeByID(departmentId, true);
            modelAndView.addObject("isProjectManager", this.isManager(projectCompose, null));
            modelAndView.addObject("dispProjectName", projectCompose.getProjectSummary().getProjectName());
        }
        else{
        	modelAndView.addObject("project", false);
        	modelAndView.addObject("isProjectManager", false);
            List<Long> managerDepartments = spaceManager.getManagerDepartments(user.getId(), user.getLoginAccount());
            modelAndView.addObject("isSpaceManager", managerDepartments.contains(departmentId));
        }
        return modelAndView;
    }
    
    @Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        return null;
    }
    
    public String reloadLeaveWords(String departmentId,String idstr) throws BusinessException {
        long deptId = Long.parseLong(departmentId);
        List<LeaveWord> list = guestbookManager.getLeaveWords4Space(deptId, 15);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
    		LeaveWord leaveWord = list.get(i);
    		leaveWord.setIdflag(idstr);
    		if(leaveWord!=null){
    			leaveWord.setIndexShow(i);
                String urlStr = "/seeyon/apps_res/v3xmain/images/personal/pic.gif";
                try {
                    StaffInfo staff = staffInfoManager.getStaffInfoById(leaveWord.getCreatorId());
            		if(staff != null) {
            			String issuerImage = staff.getSelf_image_name();
            			if(StringUtils.isNotBlank(issuerImage)){
            				if(issuerImage.startsWith("fileId")){
            					urlStr = "/seeyon/fileUpload.do?method=showRTE&"+issuerImage+"&type=image";
            				}else{
            					urlStr = "/seeyon/apps_res/v3xmain/images/personal/"+issuerImage;
            				}
            			}
             		}
        		} catch (Exception e) {
        			 log.error("留言板栏目加载异常:", e);
        		}
        		leaveWord.setUrlImage(urlStr);
        		if(leaveWord.getReplyId()!=null){
        			try {
        				LeaveWord leaveWordReply = guestbookManager.getLeaveWordsById(leaveWord.getReplyId());
        				if(leaveWordReply!=null){
        					leaveWord.setReplyerId(Long.valueOf(leaveWordReply.getCreatorId()));
        				}
        			} catch (Exception e) {
	        			 log.error("回复留言加载异常:", e);
	        		}
        		}
        		result.append(MainHelper.leaveWord2HTML(leaveWord));
    		}
		}
        return result.toString();
    }
    
    /**
	 * 判断是否为项目的负责人或助理(项目助理和项目负责人权限相同)
	 */
	private boolean isManager(ProjectCompose projectCompose, Long memberId){
		boolean isManager = false;
		Long userId = memberId == null ? CurrentUser.get().getId() : memberId;
		if(projectCompose != null){
			List<V3xOrgMember> principalLists = projectCompose.getPrincipalLists();
			List<V3xOrgMember> assistantLists = projectCompose.getAssistantLists();
			principalLists.addAll(assistantLists);
		    if(principalLists != null && !principalLists.isEmpty()){
		    	for(V3xOrgMember member : principalLists){
		    		if(member.getId().longValue() == userId.longValue()){
		    			isManager = true;
		    			break;
		    		}
		    	}
		    }
		}
		return isManager;
	}
}