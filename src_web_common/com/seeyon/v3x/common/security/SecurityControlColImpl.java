package com.seeyon.v3x.common.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.calendar.domain.AbstractCalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.util.Strings;

/**
 * 安全控制-协同
 * @author Mazc - 2010-4-15
 */
public class SecurityControlColImpl implements SecurityControl {
	private Log log = LogFactory.getLog(SecurityControlColImpl.class);
	
	private ColSuperviseManager colSuperviseManager;
	private NewflowManager newflowManager;
	private AffairManager affairManager;
	private CalEventManager calEventManager;
	
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
	
	public void setNewflowManager(NewflowManager newflowManager) {
		this.newflowManager = newflowManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	/**
	 * 判断是否有权限查看流程 - 用于协同及公文流程部分
	 * <br>合法的权限规则:<ul>
	 * 	<li>我是事项所属人</li>
	 * 	<li>我是所属人的代理人</li>
	 * 	<li>关联协同，验证该协同的前一协同是否有权限</li>
	 * 	<li>流程的督办人</li>
	 * 	<li>归档，验证是否有查看权限</li>
	 * </ul>
	 * @param ApplicationCategoryEnum 应用ID
	 * @param currentUserId 当前用户ID
	 * @param objectId 当前访问对象ID
	 * @param affair 
	 * @param preArchiveId Summary的预归档ID
	 * @param preSummaryId 前一Summary ID
	 * @return
	 */
	public boolean check(HttpServletRequest request, ApplicationCategoryEnum appEnum, Long currentUserId, Long objectId, Affair affair, Long preArchiveId) {
		if(affair == null){
			return false;
		}
		//String cacheKey = String.valueOf(affair.getObjectId());
		//若是关联协同，验证该协同的前一协同是否有权限
		//if(preObjectId != null && SecurityCheck.IsLicitGenesis(currentUserId, objectId, preAppEnum, preObjectId)){
		//	return true;				
		//}
		//缓存中有，直接return
		//if(AccessControlBean.getInstance().isAccess(currentUserId, cacheKey)){
		//	return true;
		//}
		//我是事项所属人, 我是事项代理处理人(过期了)
		if(affair.getMemberId().equals(currentUserId) 
				|| currentUserId.equals(affair.getFromId())
				|| currentUserId.equals(affair.getTransactorId())
				|| currentUserId.equals(affair.getSenderId())){
			return true;
		}
		//我是这个所属人的代理人
		List<Long> agentToList = MemberAgentBean.getInstance().getAgentToMemberId(appEnum.ordinal(), currentUserId);
		if(agentToList != null && !agentToList.isEmpty() && agentToList.contains(affair.getMemberId())){
			return true;			
		}
		
		//若是来自日程
		if("event".equals(request.getParameter("flag"))){
			try {
				String eventIdStr = request.getParameter("eventId");
				
				if(Strings.isNotBlank(eventIdStr)){
					long eventId = Long.parseLong(eventIdStr);
					AbstractCalEvent event = this.calEventManager.getEventById(eventId);
					
					if(event != null && affair.getId().longValue() == event.getFromId()){
						return true;
					}
				}
			}
			catch (Exception e) {
				log.error("", e);
			}
			
			return false;
		}
		
		//我是这个流程的督办人
		if(colSuperviseManager.isSupervisor(currentUserId, objectId)){
			return true;
		}
		//关联文档和预归档优先判断
		if(preArchiveId != null){
			if(SecurityCheck.isDocCanAccess(preArchiveId)){
				return true;
			}
		}

		
		//如果是从文档/公文档案库点开，则校验文档权限并写入
		String docIdStr = request.getParameter("docId");
		if(Strings.isBlank(docIdStr)){
			//部门归档某公文，然后将该公文发送到人员A的学习区，A登录A8以后点击这条系统消息，此时传过来的参数名为docResId,直接在学习区点击传过来的参数是docId.
			docIdStr = request.getParameter("docResId");
		}
		if(Strings.isNotBlank(docIdStr)){ 
			if(SecurityCheck.isDocCanAccess(Long.parseLong(docIdStr))){
				AccessControlBean.getInstance().addAccessControl(appEnum, String.valueOf(objectId), currentUserId);
				return true;
			}
		}
		
		//若是归档，验证是否有查看权限
		if(affair.getArchiveId() != null){
			if(SecurityCheck.isDocCanAccess(affair.getArchiveId())){
				return true;
			}
		}
		
		//如果是新流程，检测是否为真实的流程关系
		String newflowBaseSummaryIdStr = request.getParameter("newflowBaseSummaryId");
		if(Strings.isNotBlank(newflowBaseSummaryIdStr)){
			if(AccessControlBean.getInstance().isAccess(appEnum, newflowBaseSummaryIdStr, currentUserId)){
				try{
					Long baseSummaryId = Long.parseLong(newflowBaseSummaryIdStr);
					//判断是否是真正的新流程关联关系
					if(newflowManager.isRelateNewflow(baseSummaryId, objectId)){
						return true;
					}
				}
				catch(Exception e){
					log.error("判断新流程关联出错！", e);
				}
			}
		}
		
		//最后一道防线，直接检测流程中是否有我的affair
		List<Long> memberIds = new ArrayList<Long>();
		memberIds.add(currentUserId);
		if(agentToList != null && !agentToList.isEmpty()){
			memberIds.addAll(agentToList);
		}
		if(affairManager.hasPermission4TheObject(appEnum, objectId, memberIds)){
			return true;
		}
		
		return false;
	}
}
