package com.seeyon.v3x.peoplerelate.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.webmodel.DocTreeVO;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 关联人员相关操作web控制层
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * 
 */
public class PeopleRelateController extends BaseController
{

    private static final Log log = LogFactory.getLog(PeopleRelateController.class);

    private PeopleRelateManager peoplerelateManager;

    private AffairManager affairManager;

    private OrgManager orgManager;
    
    private SpaceManager spaceManager;

    private UserMessageManager userMessageManager;

    private OnLineManager onLineManager;

    private DocHierarchyManager docHierarchyManager;

    private PlanManager planManager;

    private BlogManager blogManager;
    
    private CalEventManager calEventManager;
    
    private PortletEntityPropertyManager portletEntityPropertyManager;

    private static final String RESOURCESURL = "com.seeyon.v3x.peoplerelate.resources.i18n.RelateResources";

    // private GKESendMessage gkeSendMessage;

    public void setBlogManager(BlogManager blogManager)
    {
        this.blogManager = blogManager;
    }

    public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager)
    {
        this.docHierarchyManager = docHierarchyManager;
    }

    public void setPlanManager(PlanManager planManager)
    {
        this.planManager = planManager;
    }

    public void setOnLineManager(OnLineManager onLineManager)
    {
        this.onLineManager = onLineManager;
    }

    public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
    }

    public void setAffairManager(AffairManager affairManager)
    {
        this.affairManager = affairManager;
    }

    public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager)
    {
        this.peoplerelateManager = peoplerelateManager;
    }

    public PeopleRelateManager getPeoplerelateManager() {
		return peoplerelateManager;
	}

	public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public CalEventManager getCalEventManager() {
		return calEventManager;
	}

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}
	
	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	// public void setGkeSendMessage(GKESendMessage gkeSendMessage)
    // {
    // this.gkeSendMessage = gkeSendMessage;
    // }
    @Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return null;
    }

    // public ModelAndView sendMessage(HttpServletRequest request,
    // HttpServletResponse response) throws Exception {
    //        
    // String userName=CurrentUser.get().getLoginName();
    // String message=request.getParameter("content");
    // String receiveLoginName=request.getParameter("receiveLoginName");
    // GKESendMessageValueBean value= new GKESendMessageValueBean();
    //        
    // value.receiverAccount=receiveLoginName;
    // value.senderAccount=userName;
    // value.body=StringEscapeUtils.escapeXml(message);
    // value.htmlBody=StringEscapeUtils.escapeXml(message);
    //        
    // gkeSendMessage.sendMessage(value);
    // return null;
    // }
    // public ModelAndView tosendMessagePage(HttpServletRequest request,
    // HttpServletResponse response) throws Exception {
    //        
    // String memberId=request.getParameter("memberId");
    //        
    // V3xOrgMember member=orgManager.getMemberById(Long.parseLong(memberId));
    //        
    // ModelAndView mav= new ModelAndView("peoplerelate/sendMessage");
    //        
    //       
    // mav.addObject("receiveName", member.getName());
    // mav.addObject("receiveLoginName", member.getLoginName());
    // return mav;
    // }
    
    public ModelAndView addRelativePeople(HttpServletRequest request, HttpServletResponse response){
    	ModelAndView mav = new ModelAndView("peoplerelate/addpeoplerelate");
    	String receiverId = request.getParameter("receiverId");
    	boolean isExit = false;
    	Integer relateType = 0;
    	String type = "";
    	try {
    		PeopleRelate peopleRelate = peoplerelateManager.getPeopleRelate(CurrentUser.get().getId(), Long.parseLong(receiverId));
    		if(peopleRelate != null){
    			relateType = peopleRelate.getRelateType();
    			mav.addObject("relateType", relateType);
    		}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	if(!Strings.isEmpty(receiverId)){
    		try {
    			boolean f1 = peoplerelateManager.isRelateExist(Long.valueOf(receiverId),  CurrentUser.get().getId(),1);
    			if(f1){
    				type = "1";
    			}else{
    				boolean f2 = peoplerelateManager.isRelateExist(Long.valueOf(receiverId),  CurrentUser.get().getId(),2);
    				if(f2){
    					type = "2";
    				}else{
    					boolean f3 = peoplerelateManager.isRelateExist(Long.valueOf(receiverId),  CurrentUser.get().getId(),3);
    					if(f3){
    						type = "3";
    					}else{
    						boolean f4 = peoplerelateManager.isRelateExist(Long.valueOf(receiverId),  CurrentUser.get().getId(),4);
    						if(f4){
    							type="4";
    						}
    					}
    				}
    			}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mav.addObject("type", type);
    		mav.addObject("receiverId", receiverId);
    		mav.addObject("isExit", isExit);
    		
    		//addPeopleRelate
    	}
    	
    	return mav;
    }
    
    public ModelAndView saveRelatePeople(HttpServletRequest request, HttpServletResponse response){
    	
    	String receiverId = request.getParameter("receiverId");
    	String relateType = request.getParameter("relateType");
    	Long uid = CurrentUser.get().getId();
    	boolean falg = true;
    	if(!Strings.isEmpty(receiverId) && !Strings.isEmpty(relateType)){
    		List list = new ArrayList();
    		try {
    			PeopleRelate pr = peoplerelateManager.getPeopleRelate(new Long(receiverId), uid);
    			//System.out.println(pr+"==============================="+relateType);
    			list =  peoplerelateManager.getAllRelateMemberList(uid, Integer.valueOf(relateType));
    			if(pr!=null){
        			pr.setRelateWsbs(PeopleRelate.wsbs_unsure); 
        			pr.setRelateType(Integer.valueOf(relateType)); 
        			pr.setOrderNum(list.size()+1) ;	
                	peoplerelateManager.updatePeopleRelate(pr) ;
    			}else{
        			pr = new PeopleRelate();
        			pr.setIdIfNew();
        			pr.setRelateMemberId(Long.valueOf(receiverId));
        			pr.setRelateWsbs(PeopleRelate.wsbs_unsure); // 设置握手标识2为未确认
        			pr.setRelateType(Integer.valueOf(relateType)); // 设置关联类型为上级领导
        			// 被关联人员id
        			pr.setRelatedMemberId(uid);
        			pr.setOrderNum(list.size()+1) ;	
        			peoplerelateManager.addPeopleRelate(pr);
    			}
    			//relateMember.set.assistant-relateMember.set.junior-relateMember.set.confrere
                
    			String key = "relateMember.set.leader";
    			if("1".equals(relateType)){
    				key = "relateMember.set.leader";
        			List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
        			receivers.add(MessageReceiver.get(null, new Long(receiverId),
        					"message.link.relateMember.set.leader", CurrentUser.get().getId()
        					+ "", receiverId, "setLeader"));
        			userMessageManager.sendSystemMessage(MessageContent.get(key, "",
        					CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
        					CurrentUser.get().getId(), receivers);
        			receivers = null;    
    			}else if("2".equals(relateType)){
    				key = "relateMember.set.assistant";
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
                        receivers.add(MessageReceiver.get(null, new Long(receiverId),
                                "message.link.relateMember.set.leader", CurrentUser.get().getId()
                                        + "", receiverId, "setJunior"));
                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                                CurrentUser.get().getId(), receivers);
                        receivers = null;
    				
    			}else if("3".equals(relateType)){
    				key = "relateMember.set.junior";
    				
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
                        receivers.add(MessageReceiver.get(null, new Long(receiverId),
                                "message.link.relateMember.set.leader", CurrentUser.get().getId()
                                        + "", receiverId, "setAssistant"));
                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                                CurrentUser.get().getId(), receivers);
                        receivers = null;    				
    			}else if("4".equals(relateType)){
    				key = "relateMember.set.confrere";
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();

                    receivers.add(MessageReceiver.get(null, new Long(receiverId)));
                    userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                            CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                            CurrentUser.get().getId(), receivers);
                    receivers = null;
    			}
    			falg = true;
    		} catch (NumberFormatException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	}else{
    		falg = false;
    	}
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			if(falg){
				out.println("window.parent.returnValue()");
			}else{
				out.println("window.parent.funException()");
			}
			out.println("</script>");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
    /**
     * 关联人员设置、更多...
     * @param request
     * @param response
     * @return
     */
	public ModelAndView relate(HttpServletRequest request, HttpServletResponse response) {
		String oper = (String) request.getAttribute("oper");
		String oper1 = request.getParameter("oper");
		String alertString = request.getParameter("alertString");

		Long uid = CurrentUser.get().getId();
		String viewpage = "peoplerelate/peoplerelate";
		List<PeopleRelate> myRelateList = new ArrayList<PeopleRelate>();

		// 判断是否为更多页面
		boolean fromMore = false;
		// 栏目内容来源指定人员，需要过滤
		String designated = null;

		if (Strings.isNotBlank(oper) || Strings.isNotBlank(oper1)) {
			// 查看更多关联人员
			viewpage = "peoplerelate/moreRelateMember";
			fromMore = true;

			try {
				String fragmentId = request.getParameter("fragmentId");
				if (Strings.isNotBlank(fragmentId)) {
					String ordinal = request.getParameter("ordinal");
					String panelValue = request.getParameter("panelValue");
					Map<String, String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
					designated = preference.get(panelValue);
				}
			} catch (Exception e) {
				log.error("", e);
			}
		} else {
			try {
				myRelateList = peoplerelateManager.getPeopleRelateList(uid);
			} catch (Exception e) {
				logger.error("获取把我设为关联人员的列表时异常：", e);
			}
		}

		ModelAndView mav = new ModelAndView(viewpage);
		Map<RelationType, List<PeopleRelate>> peopleRelatesList = new HashMap<RelationType, List<PeopleRelate>>();
		try {
			peopleRelatesList = peoplerelateManager.getAllPeopleRelates(uid, fromMore, designated);
		} catch (Exception e) {
			logger.error("获取关联人员失败：", e);
		}

		List<PeopleRelate> leaderlist = peopleRelatesList.get(RelationType.leader);
		List<PeopleRelate> assistantlist = peopleRelatesList.get(RelationType.assistant);
		List<PeopleRelate> juniorlist = peopleRelatesList.get(RelationType.junior);
		List<PeopleRelate> confrerelist = peopleRelatesList.get(RelationType.confrere);

		mav.addObject("alertString", alertString);
		mav.addObject("myRelateList", myRelateList);
		mav.addObject("leaderlist", leaderlist);
		mav.addObject("assistantlist", assistantlist);
		mav.addObject("juniorlist", juniorlist);
		mav.addObject("confrerelist", confrerelist);
		return mav;
	}
    
    /**
     * 进入关联人员、部门人员更多页面，默认显示关联人员部分
     * @param request
     * @param response
     * @return
     */
    public ModelAndView relateMore(HttpServletRequest request, HttpServletResponse response)
    {
    	ModelAndView mav = new ModelAndView("peoplerelate/homeEntry");
        Long deptId = CurrentUser.get().getDepartmentId();
        V3xOrgDepartment dept = new V3xOrgDepartment();
        try {
			dept = orgManager.getDepartmentById(deptId);
		} catch (Exception e) {
			logger.error("获取部门失败", e);
		}
        mav.addObject("isRelateOrDept", "relate");
        mav.addObject("dept", dept);
        return mav;
    }

    /**
     * 设置关联人员并返回主界面进行显示
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView setRelate(HttpServletRequest request, HttpServletResponse response)
    {
        long start = System.currentTimeMillis();
        String[] leads = request.getParameterValues("leaders");
        String[] assistants = request.getParameterValues("assistants");
        String[] juniors = request.getParameterValues("juniors");
        String[] confreres = request.getParameterValues("confreres");
        PeopleRelate pr = null;// by Yongzhang 2008-05-15解决消息重复提醒
        Long uid = CurrentUser.get().getId();
        StringBuffer sb = new StringBuffer();
        // 先将与我关联的人员清空 然后重新设置
        try
        {
            // 2008-06-2 by Yongzhang 删除不包括在本次提交的人员全部删除掉
            peoplerelateManager
                    .delRelateMembers(buildString(leads), uid, RelationType.leader.key());
            peoplerelateManager.delRelateMembers(buildString(assistants), uid,
                    RelationType.assistant.key());
            peoplerelateManager.delRelateMembers(buildString(juniors), uid, RelationType.junior
                    .key());
            peoplerelateManager.delRelateMembers(buildString(confreres), uid, RelationType.confrere
                    .key());
        }
        catch (Exception e1)
        {
            logger.error("清空我的关联人员出错", e1);
        }

        // 设置各上级领导
        if (leads != null)
        {
            for (int i = 0; i < leads.length; i++)
            {
                boolean exist = false;
                boolean exist1 = false;
                boolean exist2 = false;
                List list = null;
                try
                {
                    pr = new PeopleRelate();
                    pr.setIdIfNew();
                    pr.setRelateMemberId(new Long(leads[i]));
                    pr.setRelateWsbs(PeopleRelate.wsbs_unsure); // 设置握手标识2为未确认
                    pr.setRelateType(RelationType.leader.key()); // 设置关联类型为上级领导
                    // 被关联人员id
                    pr.setRelatedMemberId(uid);
                    pr.setOrderNum(i+1) ;
                    // 判断该关联人员是否存在，如果不存在进行插入和消息提醒
                    exist = peoplerelateManager.isRelateExist(new Long(leads[i]), uid,
                            RelationType.leader.key());
                    if (exist)
                    {
                    	peoplerelateManager.updatePeopleRelate(pr) ;
                    	continue ;
                    }
                    // exist1 = peoplerelateManager.isRelateExist(uid, new
                    // Long(leads[i]));
                    list = peoplerelateManager.getPeopleRelateIsExitRelate(uid, new Long(leads[i]));
                    if (list != null && list.size() > 0)
                    {
                        exist1 = true;
                    }
                    exist2 = peoplerelateManager.isRelateExist(new Long(leads[i]), uid);
                }
                catch (Exception e)
                {
                    logger.error("判断关联人员是否存在时出错", e);
                }
                String key = "relateMember.set.leader";
                if (!exist1 && !exist2)
                {
                    try
                    {
                        peoplerelateManager.addPeopleRelate(pr);
                        pr = null;
                    }
                    catch (Exception e)
                    {
                        logger.error("添加关联人员时出错", e);
                    }
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
                    try
                    {
                        receivers.add(MessageReceiver.get(null, new Long(leads[i]),
                                "message.link.relateMember.set.leader", CurrentUser.get().getId()
                                        + "", leads[i], "setLeader"));
                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                                CurrentUser.get().getId(), receivers);
                        receivers = null;
                    }
                    catch (Exception e)
                    {
                        logger.error("send message Exception", e);
                    }
                    catch (Throwable e)
                    {
                        logger.error("send message failed", e);
                    }
                }

                if (!exist && exist1)
                {
                    try
                    {
                        boolean confreresFlag = peoplerelateManager.isRelateExistNotConfreres(uid,
                                new Long(leads[i]), RelationType.confrere.key());
                        if (!exist2 && !confreresFlag)
                        {
                            peoplerelateManager.addPeopleRelate(pr);
                        }
                        else
                        {
                            V3xOrgMember member = orgManager.getMemberById(new Long(leads[i]));
                            PeopleRelate peopleRelate = ((PeopleRelate) list.get(0));
                            sb.append(ResourceBundleUtil.getString(this.RESOURCESURL,
                                            "relate.alert.exit", member.getName(),getRelateType(peopleRelate)));
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("组织模型出错", e);
                    }
                }
            }

        }

        // 设置各助手/秘书
        if (assistants != null)
        {
            for (int i = 0; i < assistants.length; i++)
            {
                boolean exist = false;
                boolean exist1 = false;
                boolean exist2 = false;
                List list = null;
                try
                {
                    pr = new PeopleRelate();
                    pr.setIdIfNew();
                    pr.setRelateMemberId(new Long(assistants[i]));
                    pr.setRelateWsbs(PeopleRelate.wsbs_unsure); // 设置握手标识2为未确认
                    pr.setRelateType(RelationType.assistant.key()); // 设置关联类型2为助手/秘书
                    // 被关联人员id
                    pr.setRelatedMemberId(uid);
                    pr.setOrderNum(i+1);
                    exist = peoplerelateManager.isRelateExist(new Long(assistants[i]), uid,
                            RelationType.assistant.key());
                    if (exist)
                    {
                    	peoplerelateManager.updatePeopleRelate(pr) ;
                    	continue;
                    }
//                    exist1 = peoplerelateManager.isRelateExist(uid, new Long(assistants[i]));
                    list = peoplerelateManager.getPeopleRelateIsExitRelate(uid, new Long(assistants[i]));
                    if (list != null && list.size() > 0)
                    {
                        exist1 = true;
                    }
                    exist2 = peoplerelateManager.isRelateExist(new Long(assistants[i]), uid);
                }
                catch (Exception e)
                {
                    logger.error("判断关联人员是否存在时出错", e);
                }
                if (!exist1 && !exist2)
                {
                    try
                    {
                        peoplerelateManager.addPeopleRelate(pr);
                        pr = null;
                    }
                    catch (Exception e)
                    {
                        logger.error("添加关联人员时出错", e);
                    }
                    String key = "relateMember.set.assistant";
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();

                    try
                    {
                        receivers.add(MessageReceiver.get(null, new Long(assistants[i]),
                                "message.link.relateMember.set.leader", CurrentUser.get().getId()
                                        + "", assistants[i], "setAssistant"));
                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                                CurrentUser.get().getId(), receivers);
                        receivers = null;
                    }
                    catch (MessageException e)
                    {
                        logger.error("send message failed", e);
                    }
                }

                if (!exist && exist1)
                {
                    try
                    {
                        V3xOrgMember member = orgManager.getMemberById(new Long(assistants[i]));
                        PeopleRelate peopleRelate = ((PeopleRelate) list.get(0));
                        sb.append(ResourceBundleUtil.getString(this.RESOURCESURL,
                                        "relate.alert.exit", member.getName(),getRelateType(peopleRelate)));
                    }
                    catch (Exception e)
                    {
                        logger.error("组织模型出错", e);
                    }
                }
            }

        }

        // 设置各下级人员
        if (juniors != null)
        {
            for (int i = 0; i < juniors.length; i++)
            {
                boolean exist = false;
                boolean exist1 = false;
                boolean exist2 = false;
                List list = null;
                try
                {
                    pr = new PeopleRelate();
                    pr.setIdIfNew();
                    pr.setRelateMemberId(new Long(juniors[i]));
                    pr.setRelateWsbs(PeopleRelate.wsbs_unsure);
                    pr.setRelateType(RelationType.junior.key()); // 设置关联类型为我的下级
                    // 被关联人员id
                    pr.setRelatedMemberId(uid);
                    pr.setOrderNum(i+1) ;
                    exist = peoplerelateManager.isRelateExist(new Long(juniors[i]), uid,
                            RelationType.junior.key());
                    if (exist)
                    {
                    	peoplerelateManager.updatePeopleRelate(pr) ;
                    	continue;
                    }
//                    exist1 = peoplerelateManager.isRelateExist(uid, new Long(juniors[i]));
                    list = peoplerelateManager.getPeopleRelateIsExitRelate(uid, new Long(juniors[i]));
                    if (list != null && list.size() > 0)
                    {
                        exist1 = true;
                    }
                    exist2 = peoplerelateManager.isRelateExist(new Long(juniors[i]), uid);
                }
                catch (Exception e)
                {
                    logger.error("判断关联人员是否存在时出错", e);
                }
                if (!exist2 && !exist1)
                {
                    try
                    {
                        peoplerelateManager.addPeopleRelate(pr);
                        pr = null;
                    }
                    catch (Exception e)
                    {
                        logger.error("添加关联人员时出错", e);
                    }
                    String key = "relateMember.set.junior";
                    List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
                    try
                    {
                        receivers.add(MessageReceiver.get(null, new Long(juniors[i]),
                                "message.link.relateMember.set.leader", CurrentUser.get().getId()
                                        + "", juniors[i], "setJunior"));
                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
                                CurrentUser.get().getId(), receivers);
                        receivers = null;
                    }
                    catch (MessageException e)
                    {
                        logger.error("send message failed", e);
                    }
                }

                if (!exist && exist1)
                {
                    try
                    {
                        boolean confreresFlag = peoplerelateManager.isRelateExistNotConfreres(uid,
                                new Long(juniors[i]), RelationType.confrere.key());
                        boolean assistFlag = peoplerelateManager.isRelateExist(uid, new Long(
                                juniors[i]), RelationType.assistant.key());
                        if (!exist2 && !confreresFlag && !assistFlag)
                        {
                            peoplerelateManager.addPeopleRelate(pr);
                        }
                        else
                        {

                            V3xOrgMember member = orgManager.getMemberById(new Long(juniors[i]));

                            PeopleRelate peopleRelate = ((PeopleRelate) list.get(0));
                            sb.append(ResourceBundleUtil.getString(this.RESOURCESURL,
                                            "relate.alert.exit", member.getName(),getRelateType(peopleRelate)));
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("组织模型出错", e);
                    }
                }
            }

        }

        // 设置各我的同事
        if (confreres != null)
        {
            String key = "relateMember.set.confrere";
            for (int i = 0; i < confreres.length; i++)
            {
                boolean exist = false;
                boolean exist1 = false;
                boolean exist2 = false;
                List list = null;
                try
                {
                    pr = new PeopleRelate();
                    pr.setIdIfNew();
                    pr.setRelateMemberId(new Long(confreres[i]));
                    pr.setRelateType(RelationType.confrere.key()); // 设置关联类型为我的同事
                    // 我的同事无需握手
                    pr.setRelateWsbs(PeopleRelate.wsbs_sure);
                    // 被关联人员id
                    pr.setRelatedMemberId(uid);
                    pr.setOrderNum(i+1) ;
                    exist = peoplerelateManager.isRelateExist(new Long(confreres[i]), uid);
                    if (exist)
                    {
                    	peoplerelateManager.updatePeopleRelate(pr) ;
                    	continue;
                    }
                    exist1 = peoplerelateManager.isRelateExist(uid, new Long(confreres[i]));
                    list = peoplerelateManager.getPeopleRelateIsExitRelate(uid, new Long(confreres[i]));
                    if (list != null && list.size() > 0)
                    {
                        exist1 = true;
                    }
                    exist2 = peoplerelateManager.isRelateExist(uid, new Long(confreres[i]),
                            RelationType.confrere.key());
                }
                catch (Exception e)
                {
                    logger.error("判断关联人员是否存在时出错", e);
                }
                if (!exist && !exist1)
                {
                    try
                    {
                        peoplerelateManager.addPeopleRelate(pr);

//                        List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
//
//                        receivers.add(MessageReceiver.get(null, new Long(confreres[i])));
//                        userMessageManager.sendSystemMessage(MessageContent.get(key, "",
//                                CurrentUser.get().getName()), ApplicationCategoryEnum.relateMember,
//                                CurrentUser.get().getId(), receivers);
//                        receivers = null;
                    }
                    catch (MessageException e)
                    {
                        logger.error("send message failed", e);
                    }
                    catch (Exception e)
                    {
                        logger.error("添加关联人员时出错", e);
                    }
                }
                if (exist1)
                {
                    try
                    {
                        // peoplerelateManager.deletePeopleRelateByOne(uid, new
                        // Long(confreres[i]), RelationType.confrere.key());
                        if (exist2)
                        {
                            peoplerelateManager.addPeopleRelate(pr);

                            /*List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();

                            receivers.add(MessageReceiver.get(null, new Long(confreres[i])));
                            userMessageManager.sendSystemMessage(MessageContent.get(key, "",
                                    CurrentUser.get().getName()),
                                    ApplicationCategoryEnum.relateMember,
                                    CurrentUser.get().getId(), receivers);
                            receivers = null;*/
                        }
                        else
                        {
                            V3xOrgMember member = orgManager.getMemberById(new Long(confreres[i]));

                            PeopleRelate peopleRelate = ((PeopleRelate) list.get(0));
                            sb.append(ResourceBundleUtil.getString(this.RESOURCESURL,
                                            "relate.alert.exit", member.getName(),getRelateType(peopleRelate)));
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("组织模型出错", e);
                    }
                }
            }

        }

        // 是否为更多页面的标识
        //request.setAttribute("alertString", sb.toString());
        //request.setAttribute("oper", "more");
        request.setAttribute("errMsgAlert", true);
        if(sb.length() == 0 ){
        	sb.append(ResourceBundleUtil.getString(this.RESOURCESURL, CurrentUser.get()
                    .getLocale(), "relate.set.succeed")) ;
        }else{
        	request.setAttribute("errMsg",sb.toString());
        	return super.redirectModelAndView("/relateMember.do?method=relateMore", "parent") ;
        }
        
        request.setAttribute("errMsg",sb.toString());
        log.info("设置关联人员用时：" + (System.currentTimeMillis() - start));
        return super.redirectModelAndView("/relateMember.do?method=relateMore&oper=more", "parent");
    }

	public ModelAndView relateMemberInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("peoplerelate/relateMemberInfo");
		
		Long memberId = NumberUtils.toLong(request.getParameter("memberId"));
		Long relatedId = NumberUtils.toLong(request.getParameter("relatedId"));
		Long spaceId = NumberUtils.toLong(request.getParameter("spaceId"));
		mav.addObject("relatedId", relatedId);
		
		Date date = new Date();
		Date beginDate = Datetimes.getFirstDayInMonth(date);
		beginDate = Datetimes.addMonth(beginDate, -1);
		Date endDate = Datetimes.getLastDayInMonth(date);
		endDate = Datetimes.addMonth(endDate, 1);

		List<CalEvent> events = calEventManager.getAllEventListByUserId(memberId, beginDate, endDate);
		Map<String, List<String>> dayToEvent = new HashMap<String, List<String>>();
		if (CollectionUtils.isNotEmpty(events)) {
			for (CalEvent calEvent : events) {
				Date date1 = calEvent.getBeginDate();
				Date date2 = calEvent.getEndDate();

				Date sDate = date1.before(beginDate) ? beginDate : date1;
				Date eDate = date2.after(endDate) ? endDate : date2;

				while (sDate.compareTo(eDate) < 1) {
					String day = Datetimes.format(sDate, "yyyy-M-d");
					List<String> es = dayToEvent.get(day);
					if (es == null) {
						es = new ArrayList<String>();
						dayToEvent.put(day, es);
					}

					es.add(calEvent.getSubject());
					sDate = Datetimes.addDate(sDate, 1);
				}
			}
		}
		mav.addObject("dateSet", dayToEvent);
		
		V3xOrgMember member = orgManager.getMemberById(memberId);
		mav.addObject("member", member);
		
		//在线状态
		mav.addObject("OnlineUser", onLineManager.isOnlineUser(member.getLoginName()));
		
		//部门或团队空间名称
		if (spaceId != null) {
			SpaceFix spaceEntity = spaceManager.getSpace(spaceId);
			mav.addObject("department", spaceEntity != null ? spaceEntity.getSpaceName() : "");
		} else {
			V3xOrgDepartment depart = orgManager.getDepartmentById(member.getOrgDepartmentId());
			mav.addObject("department", depart != null ? depart.getName() : "");
		}
		
		//岗位名称
		V3xOrgPost post = orgManager.getPostById(member.getOrgPostId());
		mav.addObject("post", post != null ? post.getName() : "");
		
		//职务级别名称
		V3xOrgLevel level = orgManager.getLevelById(member.getOrgLevelId());
		mav.addObject("level", level != null ? level.getName() : "");
		
		// 办公电话
		orgManager.loadEntityProperty(member);
		mav.addObject("officeNum", member.getProperty("officeNum"));
		
		// 获取关联类型判断是领导还是助手等
		if (Strings.isBlank(request.getParameter("departmentId"))) { // 不是来自部门成员
			PeopleRelate pr = peoplerelateManager.getPeopleRelate(new Long(memberId), new Long(relatedId));
			PeopleRelate pred = peoplerelateManager.getPeopleRelate(new Long(relatedId), new Long(memberId));
			
			if (pr != null) {
				mav.addObject("relateType", pr.getRelateType());
			}
			if (pred != null) {
				mav.addObject("relatedType", pred.getRelateType());
			}
		}
		
		// 关联人员发给我的
		List<Affair> senderList = new ArrayList<Affair>();
		
		List<Affair> colList = (List<Affair>) affairManager.getSenderOrMemberColAndEdocList(relatedId, memberId, 8);
		List<Affair> mtList = (List<Affair>) affairManager.getSenderOrMemberMtList(relatedId, memberId, 8);
		List<Plan> planList = planManager.getSenderOrMemberPlan(relatedId, memberId, 8);
		
		if(CollectionUtils.isNotEmpty(colList)){
			for (Affair af : colList) {
				senderList.add(af);
			}
		}
		
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
		if(CollectionUtils.isNotEmpty(mtList)){
			for (Affair af : mtList) {
	        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 start
	        	if(isGovVersion 
	        			&& af.getSubApp()!=null
	        			&& af.getApp()==ApplicationCategoryEnum.meeting.getKey()
	        			&& af.getSubApp()== ApplicationSubCategoryEnum.minutesAudit.getKey()){
	        		continue;
	        	}
	        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 end
				senderList.add(af);
			}
		}
		
		if(CollectionUtils.isNotEmpty(planList)){
			for (Plan plan : planList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(plan.getId());
				affair.setSubject(plan.getTitle());
				affair.setApp(ApplicationCategoryEnum.plan.getKey());
				affair.setMemberId(plan.getCreateUserId());
				affair.setCreateDate(new Timestamp(plan.getCreateTime().getTime()));
				senderList.add(affair);
			}
		}
		
		// 我发给关联人员的
		List<Affair> memberList = new ArrayList<Affair>();
		
		List<Affair> colListM = (List<Affair>) affairManager.getSenderOrMemberColAndEdocList(memberId, relatedId, 8);
		List<Affair> mtListM = (List<Affair>) affairManager.getSenderOrMemberMtList(memberId, relatedId, 8);
		List<Plan> planListM = planManager.getSenderOrMemberPlan(memberId, relatedId, 8);
		
		if(CollectionUtils.isNotEmpty(colListM)){
			for (Affair af : colListM) {
				memberList.add(af);
			}
		}
		
		
		if(CollectionUtils.isNotEmpty(mtListM)){
			for (Affair af : mtListM) {
	        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 start
	        	if(isGovVersion 
	        			&& af.getSubApp()!=null
	        			&& af.getApp()==ApplicationCategoryEnum.meeting.getKey()
	        			&& af.getSubApp()== ApplicationSubCategoryEnum.minutesAudit.getKey()){
	        		continue;
	        	}
	        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 end
				memberList.add(af);
			}
		}
		
		if(CollectionUtils.isNotEmpty(planListM)){
			for (Plan plan : planListM) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(plan.getId());
				affair.setSubject(plan.getTitle());
				affair.setApp(ApplicationCategoryEnum.plan.getKey());
				affair.setMemberId(plan.getCreateUserId());
				affair.setCreateDate(new Timestamp(plan.getCreateTime().getTime()));
				memberList.add(affair);
			}
		}
		
		// 获取关联人员共享给我的文档
		List<DocTreeVO> docVO = docHierarchyManager.getShareDocsByOwnerId(memberId);

		// 截取8条到前台
		if (senderList.size() > 8) {
			senderList = senderList.subList(0, 8);
		}
		if (memberList.size() > 8) {
			memberList = memberList.subList(0, 8);
		}
		
		mav.addObject("senderList", senderList);
		mav.addObject("memberList", memberList);
		mav.addObject("docVO", docVO);
		
		// 判断关联人员是否开通了blog
		mav.addObject("isBlogOpen", blogManager.blogIsOpen(memberId));
		return mav;
	}

    // 更多关联人员发给我的或我发给关联人员的
    @SuppressWarnings("deprecation")
    public ModelAndView morePendingOrSending(HttpServletRequest request,
            HttpServletResponse response)
    {
        ModelAndView mav = new ModelAndView("peoplerelate/morePendingOrSending");
        String from = request.getParameter("from");
        String memberId = request.getParameter("memberId");
        String relatedId = request.getParameter("relatedId");
        mav.addObject("memberId", memberId);
        mav.addObject("relatedId", relatedId);
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        // 办公电话
        String officeNum = "";
        boolean isOnline = false;
        // 获取关联类型判断是领导还是助手等
        if (Strings.isNotBlank(request.getParameter("departmentId")))
        { // 来自部门成员
            V3xOrgDepartment d = new V3xOrgDepartment();
            try
            {
                d = this.orgManager.getDepartmentById(Long.parseLong(request
                        .getParameter("departmentId")));
            }
            catch (Exception e)
            {
                logger.error("获取部门失败", e);
            }
            mav.addObject("departmentName", d.getName());
        }
        else
        {
            PeopleRelate pr = new PeopleRelate();
            try
            {
                pr = peoplerelateManager.getPeopleRelate(new Long(memberId), new Long(relatedId));
            }
            catch (Exception e)
            {
                logger.error("获取关联人员失败", e);
            }
            if (pr!=null) {
            	int relateType = pr.getRelateType();
                mav.addObject("relateType", relateType);
            }
        }
        V3xOrgMember member = new V3xOrgMember();
        try
        {
            member = orgManager.getMemberById(new Long(memberId));
        }
        catch (Exception e)
        {
            logger.error("获取人员失败", e);
        }

        try
        {
            orgManager.loadEntityProperty(member);
        }
        catch (BusinessException e1)
        {
            logger.error("获取扩展属性失败", e1);
        }

        try
        {
            officeNum = member.getProperty("officeNum");
        }
        catch (BusinessException e1)
        {
            logger.error("获取办公电话失败", e1);
        }

        // 我发给关联人员的标识 在此将关联人员id与被关联人员id对调
        if (from != null && !from.equals(""))
        {
            String temp = relatedId;
            relatedId = memberId;
            memberId = temp;
            mav.addObject("from", "send");
        }
        List<Affair> senderList = new ArrayList<Affair>();
        // List<Affair> colAndMtList =
        // (List<Affair>)affairManager.getSenderOrMemberList(new
        // Long(relatedId),new Long(memberId));
        List<Affair> colList = (List<Affair>) affairManager.getSenderOrMemberColAndEdocList(new Long(
                relatedId), new Long(memberId));
        List<Affair> mtList = (List<Affair>) affairManager.getSenderOrMemberMtList(new Long(
                relatedId), new Long(memberId));
        for (Affair af : colList)
        {
            senderList.add(af);
        }
        for (Affair af : mtList)
        {
        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 start
        	if(isGovVersion 
        			&& af.getSubApp()!=null
        			&& af.getApp()==ApplicationCategoryEnum.meeting.getKey()
        			&& af.getSubApp()== ApplicationSubCategoryEnum.minutesAudit.getKey()){
        		continue;
        	}
        	//branches_a8_v350_r_gov  杨帆 添加  政务版屏蔽掉关联人员查询会议纪要的数据 end
            senderList.add(af);
        }
        List<Plan> planList = new ArrayList<Plan>();
        try
        {
            planList = planManager.getSenderOrMemberPlan(new Long(relatedId), new Long(memberId));
        }
        catch (Exception e)
        {
            logger.error("获取计划列表失败", e);
        }
        // for(Affair af : colAndMtList){
        // senderList.add(af);
        // }
        for (Plan plan : planList)
        {
            Affair affair = new Affair();
            affair.setIdIfNew();
            affair.setObjectId(plan.getId());
            affair.setSubject(plan.getTitle());
            affair.setApp(ApplicationCategoryEnum.plan.getKey());
            affair.setMemberId(plan.getCreateUserId()); // 计划的创建者 判断显示回复还是总结用到
            affair.setCreateDate(new Timestamp(plan.getCreateTime().getTime()));
            senderList.add(affair);
        }
        // 处理分页
        Pagination.setRowCount(senderList.size());
        int first = Pagination.getFirstResult();
        int pageSize = Pagination.getMaxResults();
        int end1 = first + pageSize;
        int end2 = senderList.size();
        int end = 0;
        if (end1 > end2)
            end = end2;
        else
            end = end1;
        senderList = senderList.subList(first, end);

        Long departId = member.getOrgDepartmentId();
        Long levelId = member.getOrgLevelId();
        Long postId = member.getOrgPostId();
        V3xOrgDepartment depart = new V3xOrgDepartment();
        try
        {
            depart = orgManager.getDepartmentById(departId);
            String department = depart.getName(); // 部门名称
            mav.addObject("department", department);
        }
        catch (Exception e)
        {
            logger.error("获取部门失败", e);
        }
        V3xOrgLevel level = new V3xOrgLevel();
        try
        {
            level = orgManager.getLevelById(levelId);
            String mlevel = level.getName(); // 职务级别
            mav.addObject("level", mlevel);
        }
        catch (Exception e)
        {
            logger.error("获取职务级别失败", e);
        }
        V3xOrgPost post = new V3xOrgPost();
        try
        {
            post = orgManager.getPostById(postId);
            String postname = post.getName(); // 岗位名称
            mav.addObject("post", postname);
        }
        catch (Exception e)
        {
            logger.error("获取岗位失败", e);
        }
        
        isOnline = onLineManager.isOnline(member.getLoginName());
        mav.addObject("member", member);
        mav.addObject("isOnline", isOnline);
        mav.addObject("senderList", senderList);
        
        mav.addObject("officeNum", officeNum);

        return mav;
    }

    // 点击消息时的处理
    public ModelAndView setRelateMember(HttpServletRequest request, HttpServletResponse response)
    {
    	super.noCache(response) ;
        String relateMemberId = request.getParameter("memberId");// 关联人员
        String relatedMemberId = request.getParameter("relatedId");// 被关联人员，即登录者-->我
        String oper = request.getParameter("oper");
        String resource = "com.seeyon.v3x.peoplerelate.resources.i18n.RelateResources";
        String setCoWorkerSucceed = ResourceBundleUtil.getString(resource, CurrentUser.get()
                .getLocale(), "relate.set.succeed");
        PrintWriter out = null;
        try
        {
            out = response.getWriter();
        }
        catch (IOException e)
        {
        }
        try
        {
            PeopleRelate pr = null;
            boolean exist = false;

            // 判断此人员是否与我已经建立关联关系 如果已建立的话就不添加
            exist = peoplerelateManager.isRelateExistUnSure(new Long(relateMemberId), new Long(
                    relatedMemberId), PeopleRelate.wsbs_sure);
            if (exist)
            {
                // by Yongzhang 2008-09-18 删除掉不能再建立双向关联关系的记录
                // peoplerelateManager.deleteRelatePeopleRepeat(new
                // Long(relatedMemberId), new Long(
                // relateMemberId), PeopleRelate.wsbs_sure);
                setCoWorkerSucceed = ResourceBundleUtil.getString(resource, CurrentUser.get()
                        .getLocale(), "relate.set.hasrelate");
                out.println("<script>");
                out.println("alert('" + setCoWorkerSucceed + "');window.close();");
                out.println("</script>");
                out.close();
                return null;
            }

            // 判断是否已经取消关联关系，注意设置的顺序问题！！被关联人员--->关联人员
            boolean isExistUnSure = peoplerelateManager.isRelateExistUnSure(new Long(
                    relatedMemberId), new Long(relateMemberId), PeopleRelate.wsbs_unsure);
            if (!isExistUnSure)
            {
                setCoWorkerSucceed = ResourceBundleUtil.getString(resource,
                            "relate.set.cancellation");
                out.println("<script>");
                out.println("alert('" + setCoWorkerSucceed + "');window.close();");
                out.println("</script>");
                out.close();
                return null;
            }

            // 如果操作为setLeader代表有人将你设置为上级领导，需要设置他为你的下级
            if (oper.equals("setLeader") && !exist)
            {
                // 判断是否已经有上下级关系了
                pr = new PeopleRelate();
                pr.setIdIfNew();
                pr.setRelatedMemberId(new Long(relatedMemberId));
                pr.setRelateMemberId(new Long(relateMemberId));
                pr.setRelateType(RelationType.junior.key());
                pr.setRelateWsbs(PeopleRelate.wsbs_sure);
                try
                {
                    peoplerelateManager.addPeopleRelate(pr);
                }
                catch (Exception e)
                {
                    logger.error("添加关联人员时异常", e);
                }
                try
                {
                    // 更新与你关联人员的握手表识
                    peoplerelateManager.updateWsbs(new Long(relatedMemberId), new Long(
                            relateMemberId));
                }
                catch (Exception e)
                {
                    logger.error("更新握手标识时异常", e);
                }
            }
            else if (!exist)
            {
                pr = new PeopleRelate();
                pr.setIdIfNew();
                pr.setRelatedMemberId(new Long(relatedMemberId));
                pr.setRelateMemberId(new Long(relateMemberId));
                pr.setRelateType(RelationType.leader.key());
                pr.setRelateWsbs(PeopleRelate.wsbs_sure);
                try
                {
                    peoplerelateManager.addPeopleRelate(pr);
                }
                catch (Exception e)
                {
                    logger.error("添加关联人员时异常", e);
                }
                try
                {
                    // 更新与你关联人员的握手表识
                    peoplerelateManager.updateWsbs(new Long(relatedMemberId), new Long(
                            relateMemberId));
                }
                catch (Exception e)
                {
                    logger.error("更新握手标识时异常", e);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("查看是否存在关联人员时异常", e);
        }
        out.println("<script>");
        out.println("alert('" + setCoWorkerSucceed + "');window.close();");
        out.println("</script>");
        out.close();
        return null;
    }

    // 2008-06-2 by Yongzhang
    @SuppressWarnings("unchecked")
    private List buildString(String[] typeStrings)
    {
        List returnString = new ArrayList();
        if (typeStrings != null && typeStrings.length > 0)
        {
            for (int i = 0; i < typeStrings.length; i++)
            {
                returnString.add(new Long(typeStrings[i]));
            }
        }
        return returnString;
    }

	private String getRelateType(PeopleRelate peopleRelate) {
		String relationType = "";
		if (peopleRelate != null) {
			switch (peopleRelate.getRelateType()) {
			case 1:
				relationType = ResourceBundleUtil.getString(this.RESOURCESURL, "relate.type.leader");
				break;
			case 2:
				relationType = ResourceBundleUtil.getString(this.RESOURCESURL, "relate.type.assistant");
				break;
			case 3:
				relationType = ResourceBundleUtil.getString(this.RESOURCESURL, "relate.type.alert.junior");
				break;
			default:
				relationType = ResourceBundleUtil.getString(this.RESOURCESURL, "relate.type.alert.confrere");
				break;
			}
		}
		return relationType;
	}
    
	/**
	 * portal显示人员类型
	 * 
	 */
	public ModelAndView showDesignated(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("peoplerelate/showDesignated");
		return mav;
	}

}