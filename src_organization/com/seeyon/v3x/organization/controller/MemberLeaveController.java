/**
 * Id: MemberLeaveService.java, v1.0 2011-12-1 wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.v3x.organization.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.main.manager.MainManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.MemberLeaveManager;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 
 * @Project/Product: A8（A8）
 * @Description: 人员离职管理
 * @Copyright: Copyright (c) 2011 of Seeyon, Ltd.
 * @author: wangchw
 * @time: 2011-12-1 下午05:28:03
 * @version: v1.0
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator,RoleType.DepartmentAdmin,RoleType.HrAdmin})
public class MemberLeaveController extends BaseController {
	/**
	 * 日志记录类
	 */
	private static final Log log = LogFactory.getLog(MemberLeaveController.class);
	/**
	 * 协同
	 */
	private ColManager colManager;
	/**
	 * 公文
	 */
	private EdocManager edocManager;
	/**
	 * 表单模板
	 */
	private TempleteManager templeteManager;
	/**
	 * 元数据
	 */
	private MetadataManager metadataManager;
	/**
	 * 离职办理接口
	 */
	private MemberLeaveManager memberLeaveManager;
	/**
	 * 个人事项操作接口
	 */
	private AffairManager affairManager;
	/**
	 * 代理接口
	 */
	private AgentIntercalateManager agentIntercalateManager;
	
	private OrgManager orgManager;
	
	private AppLogManager appLogManager;
	/**
	 * 公共信息发布统计接口
	 */
	private MainManager mainManager;
	/**
	 * @param agentIntercalateManager the agentIntercalateManager to set
	 */
	public void setAgentIntercalateManager(
			AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}

	/**
	 * @param affairManager the affairManager to set
	 */
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	/**
	 * @param memberLeaveManager the memberLeaveManager to set
	 */
	public void setMemberLeaveManager(MemberLeaveManager memberLeaveManager) {
		this.memberLeaveManager = memberLeaveManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }
	
	public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }
	
	public void setColManager(ColManager colManager) {
        this.colManager = colManager;
    }
	
	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	/**
     * 显示离职人员协同相关交接页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showLeaveManagementColPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String userid= request.getParameter("userid");
    	if(log.isInfoEnabled()){
    		log.info("userid:="+userid);
    	}
    	ModelAndView mav = new ModelAndView("organization/member/showLeaveManagementColPage");
    	//根据userid获得用户对应的协同(表单)模板流程待办数目
    	int pendingCount1= colManager.queryPendingCountByUserAndApp(userid, ApplicationCategoryEnum.collaboration.key(), StateEnum.col_pending.key(),true,false,false);
    	//根据userid获得用户对应的自由协同待办数目
    	int pendingCount2= colManager.queryPendingCountByUserAndApp(userid, ApplicationCategoryEnum.collaboration.key(), StateEnum.col_pending.key(),false,true,false);
    	Map<Integer,Integer> groupCountMap= edocManager.queryPendingCountByGroup(userid, StateEnum.col_pending.key());
    	//根据userid获得用户对应的公文发文处理待办数目
    	Integer pendingCount4Int= groupCountMap.get(ApplicationCategoryEnum.edocSend.key());
    	int pendingCount41= pendingCount4Int ==null? 0: pendingCount4Int.intValue();
    	//根据userid获得用户对应的公文收文处理待办数目
    	Integer pendingCount5Int= groupCountMap.get(ApplicationCategoryEnum.edocRec.key());
    	int pendingCount51= pendingCount5Int==null?0:pendingCount5Int.intValue();
    	//根据userid获得用户对应的公文签报处理待办数目
    	Integer pendingCount6Int= groupCountMap.get(ApplicationCategoryEnum.edocSign.key());
    	int pendingCount61= pendingCount6Int==null?0:pendingCount6Int.intValue();
    	//根据userid获得用户对应的公文交换待办待办数目
    	Integer pendingCount71Int= groupCountMap.get(ApplicationCategoryEnum.exSend.key());
    	Integer pendingCount72Int= groupCountMap.get(ApplicationCategoryEnum.exSign.key());
    	int pendingCount71= pendingCount71Int==null?0:pendingCount71Int.intValue();
    	int pendingCount72= pendingCount72Int==null?0:pendingCount72Int.intValue();
    	int pendingCount711= pendingCount71+pendingCount72;
    	//根据userid获得用户对应的公文待登记待办数目
    	Integer pendingCount8Int= groupCountMap.get(ApplicationCategoryEnum.edocRegister.key());
    	int pendingCount81= pendingCount8Int==null?0:pendingCount8Int.intValue();
    	
    	int pendingCount4= pendingCount41+pendingCount51+pendingCount61+pendingCount711+pendingCount81;
    	
    	
    	//根据userid获得用户对应的公共信息-公告待审核数目
//    	Integer pendingCount91Int= groupCountMap.get(ApplicationCategoryEnum.bulletin.key());
//    	int pendingCount91= pendingCount91Int==null?0:pendingCount91Int.intValue();
//    	//根据userid获得用户对应的公共信息-调查待审核数目
//    	Integer pendingCount92Int= groupCountMap.get(ApplicationCategoryEnum.inquiry.key());
//    	int pendingCount92= pendingCount92Int==null?0:pendingCount92Int.intValue();
//    	//根据userid获得用户对应的公共信息-讨论待审核数目
//    	//Integer pendingCount93Int= groupCountMap.get(ApplicationCategoryEnum.bbs.key());
//    	//int pendingCount93= pendingCount93Int==null?0:pendingCount93Int.intValue();
//    	//根据userid获得用户对应的公共信息-新闻待审核数目
//    	Integer pendingCount94Int= groupCountMap.get(ApplicationCategoryEnum.news.key());
//    	int pendingCount94= pendingCount94Int==null?0:pendingCount94Int.intValue();
//    	int pendingCount9= pendingCount91+pendingCount92+pendingCount94;
    	int pendingCount9= mainManager.getPubInfo(Long.parseLong(userid), false, null);
    	
    	boolean isExchange= false;
    	List<V3xOrgEntity> myRoles= orgManager.getUserDomain(Long.parseLong(userid), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ROLE);
    	for (V3xOrgEntity v3xOrgEntity : myRoles) {
			if(null!=v3xOrgEntity){
				if(v3xOrgEntity.getName().equals(EdocRoleHelper.acountExchangeRoleName) 
						|| v3xOrgEntity.getName().equals(EdocRoleHelper.departmentExchangeRoleName)){
					isExchange= true;
					break;
				}
			}
		}
    	//获取该离职用户作为代理人的代理信息
    	boolean edocAgent= false;//公文
    	String edocAgentMember= "";
    	boolean commonAgent= false;//公共信息
    	String commonAgentMember= "";
//    	boolean colAgent= false;//协同
    	boolean freeColAgent= false;//自由协同
    	String freeColAgentMember= "";
    	boolean moduleColAgent= false;//模板协同
    	String moduleColAgentMember= "";
    	long endTime= Timestamp.valueOf("9999-12-31 23:59:59").getTime();
    	Locale local = LocaleContext.getLocale(request);
    	List<V3xAgent> agentList = agentIntercalateManager.queryAvailabilityList(Long.valueOf(Long.parseLong(userid)));
    	if( null!=agentList && agentList.size()>0){
    		for (V3xAgent v3xAgent : agentList) {
    			long agentEndTime= v3xAgent.getEndDate().getTime();
    			if( agentEndTime== endTime){//应该为管理员设置的
    				String agentMember= orgManager.getMemberById(v3xAgent.getAgentId()).getName();
    				//String agentTipMsg= Constant.getString("member.leave.transferto.member.tip",local,agentMember);
    				String agentOptionStr = v3xAgent.getAgentOption();
        			String[] options = agentOptionStr.split("&");
        			boolean flag = false;
        			for (int i = 0; i < options.length; ++i) {
    		            if (options[i] != null) {
    		              if ("4".equals(options[i].trim())){
    		            	  //pendingCount4 = 0;
    		            	  edocAgent= true;
    		            	  edocAgentMember= agentMember;
    		              }else if (("7".equals(options[i].trim())) || 
    		                ("10".equals(options[i].trim())) || 
    		                ("8".equals(options[i].trim()))){
    		            	  //pendingCount9 = 0;
    		            	  commonAgent= true;
    		            	  commonAgentMember= agentMember;
    		              }else if ("1".equals(options[i].trim())) {
    		            	  flag = true;
    		              }
    		            }
    		         }
        			if (flag) {
    		            List<V3xAgentDetail> v3xdetails = v3xAgent.getAgentDetails();
    		            if ((v3xdetails != null) && (v3xdetails.size() > 0)) {
    		            	for (V3xAgentDetail v3xAgentDetail : v3xdetails) {
    			                if (v3xAgentDetail.getEntityId().longValue() == 2L){
    			                	//pendingCount1 = 0;
    			                	moduleColAgentMember= agentMember;
    			                }else if (v3xAgentDetail.getEntityId().longValue() == 1L){
    			                	//pendingCount2 = 0;
    			                	freeColAgentMember= agentMember;
    			                }
    						}
    		            }else {
    		            	//pendingCount1 = 0;
    		            	//pendingCount2 = 0;
    		            	moduleColAgentMember= agentMember;
    		            	freeColAgentMember= agentMember;
    		            }
        			}
    			}
			}
    	}
    	
    	mav.addObject("leaved_userid", userid);
    	mav.addObject("pendingCount1", pendingCount1);
    	mav.addObject("moduleColAgent", moduleColAgent);
    	mav.addObject("pendingCount2", pendingCount2);
    	mav.addObject("freeColAgent", freeColAgent);
    	mav.addObject("pendingCount4", pendingCount4);
    	mav.addObject("edocAgent", edocAgent);
    	mav.addObject("pendingCount9", pendingCount9);
    	mav.addObject("commonAgent", commonAgent);
    	mav.addObject("userid", userid);
    	mav.addObject("isExchange", isExchange);
    	mav.addObject("moduleColAgentMember", moduleColAgentMember);
    	mav.addObject("freeColAgentMember", freeColAgentMember);
    	mav.addObject("edocAgentMember", edocAgentMember);
    	mav.addObject("commonAgentMember", commonAgentMember);
//    	mav.addObject("myagentMemberIdList", myagentMemberIdList);
    	return mav;
    }
    
    
    /**
     * 显示离职人员协同相关交接页面(左侧内容)
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showLeftContentFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String userid= request.getParameter("userid");
    	if(log.isInfoEnabled()){
    		log.info("userid:="+userid);
    	}
    	ModelAndView mav = new ModelAndView("organization/member/showLeftContent");
    	//根据userid获得用户所在的所有流程模板列表
    	List colTemplateList= templeteManager.getListByUserId(userid);
    	//根据userid获得该用户审核的公告板块列表
    	List bulTypAuditList= memberLeaveManager.getBulTypeAuditList(userid);
    	//根据userid获得该用户管理的调查板块列表
    	List inquiryList= memberLeaveManager.getInquiryList(userid);
    	//根据userid获得该用户审核的新闻板块列表
    	List newsAuditList= memberLeaveManager.getNewsAuditList(userid);
    	//根据userid获得该用户是否为综合办公的管理员
    	List officeAdminList= memberLeaveManager.getOfficeAdminListByUserId(userid);
    	//根据userid获得该用户还没有归还的综合办公物品列表
    	List officeDeviceList= memberLeaveManager.getOfficeDeviceListByUserId(userid);
    	
    	mav.addObject("leaved_userid", userid);
    	mav.addObject("colTemplateList", colTemplateList);
    	mav.addObject("userid", userid);
    	mav.addObject("inquiryList", inquiryList);
    	mav.addObject("bulTypAuditList", bulTypAuditList);
    	mav.addObject("newsAuditList", newsAuditList);
    	mav.addObject("officeAdminList", officeAdminList);
    	mav.addObject("officeDeviceList", officeDeviceList);
    	return mav;
    }
    
    /**
     * 显示离职人员协同相关交接页面(左侧内容)
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showRightContentFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String userid= request.getParameter("userid");
    	if(log.isInfoEnabled()){
    		log.info("userid:="+userid);
    	}
    	ModelAndView mav = new ModelAndView("organization/member/showRightContent");
    	//根据userid获得用户的所有角色关系列表
    	List roleNameList= memberLeaveManager.getRolesByUserId(userid);
    	//根据userid获得该用户负责的项目列表
    	List projectManagerList= memberLeaveManager.getProjectManagerListByUserId(userid);
    	//根据userid获得该用户的表单模板列表
    	List formAppList= memberLeaveManager.getFormAppList(userid);
    	//根据userid获得该用户管理的空间列表
    	List spaceList= memberLeaveManager.getManagementSpaceList(userid);	
    	//根据userid获得该用户管理的讨论板块列表
    	List bbsList= memberLeaveManager.getBbsList(userid);
    	//根据userid获得该用户管理的新闻板块列表
    	List newsList= memberLeaveManager.getNewsList(userid);
    	//根据userid获得该用户管理的公告板块列表
    	List bulTypList= memberLeaveManager.getBulTypeList(userid);
    	//根据userid获得该用户审核的调查板块列表
    	List inquiryAuditList= memberLeaveManager.getInquiryAuditList(userid);
    	mav.addObject("leaved_userid", userid);
    	mav.addObject("userid", userid);
    	mav.addObject("roleNameList", roleNameList);
    	mav.addObject("projectManagerList", projectManagerList);
    	mav.addObject("formAppList", formAppList);
    	mav.addObject("spaceList", spaceList);
    	mav.addObject("bulTypList", bulTypList);
    	mav.addObject("bbsList", bbsList);
    	mav.addObject("newsList", newsList);
    	mav.addObject("inquiryAuditList", inquiryAuditList);
    	return mav;
    }
    
    /**
    * 显示待办frame
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ModelAndView showList4LeaveFrame(HttpServletRequest request,
           HttpServletResponse response) throws Exception{
   		ModelAndView modelAndView = new ModelAndView("organization/member/list4LeaveFrame");
   		String leaved_userid= request.getParameter("leaved_userid");
   		String type= request.getParameter("type");
   		modelAndView.addObject("from", request.getParameter("from")) ;
   		modelAndView.addObject("leaved_userid", leaved_userid) ;
   		modelAndView.addObject("type", type) ;
   		return modelAndView;
   }


   /**
    * 显示离职交接待办列表
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ModelAndView showList4Leave(HttpServletRequest request,HttpServletResponse response) throws Exception{
   		String condition = request.getParameter("condition");
       	String textfield = request.getParameter("textfield");
       	String textfield1 = request.getParameter("textfield1");
   		ModelAndView mav = new ModelAndView("organization/member/showList4Leave");
   		String leaved_userid= request.getParameter("leaved_userid");
   		String type= request.getParameter("type");
   		List queryList = new ArrayList();
	   	if("1".equals(type)){//模板流程
	   		queryList = colManager.queryPendingByUserAndApp(
	   				leaved_userid, 
	   				ApplicationCategoryEnum.collaboration.key(), 
	   				StateEnum.col_pending.key(), 
	   				true,
	   				false, 
	   				false,
	   				condition, 
	   				textfield,
	   				textfield1);
   		}else if("2".equals(type)){//重要自由协同
	   		queryList = colManager.queryPendingByUserAndApp(
	   				leaved_userid,
	   				ApplicationCategoryEnum.collaboration.key(), 
	   				StateEnum.col_pending.key(), 
	   				false,
	   				true, 
	   				false,
	   				condition, 
	   				textfield,
	   				textfield1);
   		}else if("4".equals(type)){//公文待办
	   		queryList = edocManager.queryPendingByUserAndApp(
	   				leaved_userid,
	   				ApplicationCategoryEnum.edoc.key(),
	   				StateEnum.col_pending.key(), 
	   				false,
	   				false,
	   				condition, 
	   				textfield,
	   				textfield1);
	   	}else if("9".equals(type)){//公共信息发布审核
	   		queryList = colManager.queryCommonPendingByUserAndApp(
	   				leaved_userid, 
	   				StateEnum.col_pending.key(),
	   				condition, 
	   				textfield,
	   				textfield1);
	   	}
   		mav.addObject("csList", queryList);
       	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
       	mav.addObject("colMetadata", colMetadata);
       	Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
       	mav.addObject("comImportanceMetadata", comImportanceMetadata);
       	mav.addObject("leaved_userid", leaved_userid);
       	mav.addObject("type", type);
   		return mav;
   }

   /**
    * 保存离职交接信息
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ModelAndView save4Leave(HttpServletRequest request,HttpServletResponse response) throws Exception{
	   	String agent_to_id= request.getParameter("agent_to_id");
	   	String leave1UserId= request.getParameter("leave1userId");
	   	String leave2UserId= request.getParameter("leave2userId");
	   	String leave4UserId= request.getParameter("leave4userId");
	   	String leave9UserId= request.getParameter("leave9userId");
	   	try{
		   	User user = CurrentUser.get();
		   	//首先将离职用户踢下下线，将账号设置成离职状态，将离职用户的代理和被代理设置取消
		   	memberLeaveManager.changeUserSate(agent_to_id,user);
		   	//离职工作交接
		   	//获得此人未处理协同的最早时间
		   	Timestamp startTime= affairManager.getMinStartTimeByUserId(agent_to_id);
		   	//代理协同的结束时间
		   	Timestamp endTime= Timestamp.valueOf("9999-12-31 23:59:59");
		   	if(leave1UserId!=null && !"".equals(leave1UserId.trim())){//模板协同
		   		V3xAgent agent = memberLeaveManager.handleCurrentAgentInfo1(leave1UserId,agent_to_id,startTime,endTime,user);
				//找出agent_to_id代理别人的所有记录
		   		//memberLeaveManager.handleOldProxyInfo1(agent_to_id,agent,leave1UserId);
		   		//找出离职人员指派给别人的模板流程代理记录，并删除这种代理关系
		   		memberLeaveManager.handleOldProxyedInfo1(agent_to_id,agent);
		   	}
		   	if(leave2UserId!=null && !"".equals(leave2UserId.trim())){//自由协同
		   		V3xAgent agent = memberLeaveManager.handleCurrentAgentInfo2(leave2UserId,agent_to_id,startTime,endTime,user);
		   		//memberLeaveService.handleOldProxyInfo2(agent_to_id,agent,leave2UserId);
		   		memberLeaveManager.handleOldProxyedInfo2(agent_to_id,agent);
		   	}
		   	if(leave4UserId!=null && !"".equals(leave4UserId.trim())){//公文待办
		   		V3xAgent agent = memberLeaveManager.handleCurrentAgentInfo4(leave4UserId,agent_to_id,startTime,endTime,user);
		   		//memberLeaveService.handleOldProxyInfo4(agent_to_id,agent,leave4UserId);
		   		memberLeaveManager.handleOldProxyedInfo4(agent_to_id,agent);
		   	}
		   	if(leave9UserId!=null && !"".equals(leave9UserId.trim())){//公共信息审批
		   		V3xAgent agent = memberLeaveManager.handleCurrentAgentInfo9(leave9UserId,agent_to_id,startTime,endTime,user);
		   		//memberLeaveService.handleOldProxyInfo9(agent_to_id,agent,leave9UserId);
		   		memberLeaveManager.handleOldProxyedInfo9(agent_to_id,agent);
		   	}
		   	appLogManager.insertLog(user, AppLogAction.Organization_MemberLeave,user.getName(),orgManager.getMemberById(Long.parseLong(agent_to_id)).getName());
	   	}catch(Throwable e){
	   		log.error("离职办理失败!", e);
	   	}
	   	return null;
   }

	/**
	 * @param mainManager the mainManager to set
	 */
	public void setMainManager(MainManager mainManager) {
		this.mainManager = mainManager;
	}

}