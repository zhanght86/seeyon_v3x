package com.seeyon.v3x.system.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;

public class ProxyController extends BaseController {
	private static Log log = LogFactory.getLog(ProxyController.class);
	
    private OrgManagerDirect orgManagerDirect;
	
    private UserMessageManager userMessageManager;
	
    private OnLineManager onLineManager;
	
    private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
	public void setOnLineManager(OnLineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入授权人界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView proxyFrame(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/proxy/proxyframe");
		User user = CurrentUser.get();
		Long logerId = user.getId();
		try {
			// 进入后进行查找是否本人被其他人选为代理,并且查处选择人
			V3xOrgMember agentToMember = orgManager.getMemberById(logerId);
			if(agentToMember != null){
				if (agentToMember.getAgentToId() != -1) {
					agentToMember = orgManager.getMemberById(agentToMember.getAgentToId());
					result.addObject("proxyMan", agentToMember.getName());
					result.addObject("otherLoger", 1);
					result.addObject("showProxy", 1);
					return result;
				}
			}
			// 比较本人是否已经申请代理
			V3xOrgMember agentMember = orgManager.getMemberById(logerId);
			if ( agentMember != null ){
				long agentId = agentMember.getAgentId();
				if (agentId != -1) {
					agentMember = orgManager.getMemberById(agentId);
					if(agentMember == null){ //人员可能被删除
						agentMember = orgManager.getMemberById(agentId);
					}
					
					if(agentMember != null){
						result.addObject("proxyName", agentMember.getName());
					}
					result.addObject("proxyshow", 1);
					result.addObject("showProxy", 1);
					// 显示标题
					result.addObject("showTitle", 1);
					
					return result;
				}
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		// 第一次进入的时候返回的结果
		result.addObject("proxyshow", 0);
		result.addObject("showProxy", 0);
		// 显示标题
		result.addObject("showTitle", 1);
		return result;
	}

	/**
	 * 对登录人进行授权代理
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView proxyAdd(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/proxy/proxyframe");
		String proxyName = request.getParameter("proxyName");
		String proxyId = request.getParameter("proxyauto");
		User user = CurrentUser.get();
		Long loggerId = user.getId();
		
        //代理人实例化
        V3xOrgMember agentMember = null;
		
		PrintWriter out = null;
		try {
            //代理人，当前登录者
            agentMember = orgManager.getMemberById(loggerId);
            //被代理人，判断是否已选择其他人或者被其他人选择为代理人
            V3xOrgMember agentToMember = orgManagerDirect.getMemberById(Long.valueOf(proxyId));
			
            if (agentToMember.getAgentToId() != -1){
                V3xOrgMember agentMember1 = orgManager.getMemberById(agentToMember.getAgentToId());
				try {
					out = response.getWriter();
					out.println("<script>");
//					out.println("alert('"+agentMember1.getName()+"已经选择"+proxyName+"为代理,请重新选择!')");
					out.println("alert('"+Constants.getString4CurrentUser("proxy.already.1",agentMember1.getName(),proxyName)+"')");
//					out.println("alert(parent.v3x.getMessage('syteem_proxy_already1'));");
					out.println("</script>");
					result.addObject("proxyshow", 0);
					result.addObject("showProxy", 0);
					//显示标题
					result.addObject("showTitle", 1);
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if(agentToMember.getAgentId() != -1){
                V3xOrgMember agentMember1 = orgManager.getMemberById(agentToMember.getAgentId());
				try {
					out = response.getWriter();
					out.println("<script>");
//					out.println("alert('"+proxyName+"已经选择"+agentMember1.getName()+"为代理,不能在被选择为代理,请重新选择!')");
					out.println("alert('"+Constants.getString4CurrentUser("proxy.already.2",proxyName,agentMember1.getName())+"')");
					out.println("</script>");
					result.addObject("proxyshow", 0);
					result.addObject("showProxy", 0);
					// 显示标题
					result.addObject("showTitle", 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            
			//是否本人此时已被其他人选为代理
            if (agentMember.getAgentToId() != -1) {
                agentToMember = orgManager.getMemberById(agentMember.getAgentToId());
                try {
                    out = response.getWriter();
                    out.println("<script>");
                    out.println("alert('"+agentToMember.getName() + Constants.getString4CurrentUser("proxy.name.passivity")+"')");
                    out.println("</script>");
                    result.addObject("proxyMan", agentToMember.getName());
                    result.addObject("otherLoger", 1);
                    result.addObject("showProxy", 1);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
            
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
		// 被代理人实例化
		V3xOrgMember agentToMember = null;
		try {
			if (proxyId != null && !proxyId.equals("")) {
				if (user.getId() != Long.valueOf(proxyId)) {
					// 将登录选择的代理人的 ID 放入登录人的代理字段中
					
					agentMember.setAgentId(Long.valueOf(proxyId));
					agentMember.setAgentTime(new Date());
					orgManagerDirect.updateEntity(agentMember);
					agentToMember = orgManagerDirect.getMemberById(Long.valueOf(proxyId));
					agentToMember.setAgentToId(loggerId);
					orgManagerDirect.updateEntity(agentToMember);
					// 显示标题
					result.addObject("showTitle", 1);
					
					//发送消息
					List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
					receivers.add(new MessageReceiver(null,Long.valueOf(proxyId)));
					String messageResource = "sys.addAgent.offline";
					if(this.onLineManager.isOnline(agentToMember.getLoginName()))
						messageResource = "sys.addAgent.online";
					userMessageManager.sendSystemMessage(new MessageContent(messageResource,  
							agentMember.getName()), ApplicationCategoryEnum.global, loggerId, receivers);
				} else {
//					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('代理人不能是本人!')");
					out.println("</script>");
					result.addObject("proxyshow", 0);
					result.addObject("showProxy", 0);
					return result;
				}
			} else {
				return result;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		result.addObject("proxyName", proxyName);
		result.addObject("proxyshow", 1);
		result.addObject("showProxy", 1);
		return result;
	}

	/**
	 * 对本人的代理进行取消
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView proxyModify(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/proxy/proxyframe");
		User user = CurrentUser.get();
		V3xOrgMember member = null;
		try {
			member = orgManagerDirect.getMemberById(user.getId());
			Long agentToId = member.getAgentId();
			member.setAgentId(V3xOrgEntity.DEFAULT_NULL_ID);
			member.setAgentTime(null);
			orgManagerDirect.updateEntity(member);
			
			V3xOrgMember member1 = orgManagerDirect.getMemberById(agentToId);
			if(member1 != null){
				member1.setAgentToId(V3xOrgEntity.DEFAULT_NULL_ID);
				orgManagerDirect.updateEntity(member1);
			}
			// 显示标题
			result.addObject("showTitle", 1);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		result.addObject("proxyshow", 0);
		result.addObject("showProxy", 0);
		return result;
	}
}