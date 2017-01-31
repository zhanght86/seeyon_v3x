package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * @version 1.0 2009-8-24
 */
public class PendingAuditSection extends BaseSection {
	private static final Log log = LogFactory.getLog(PendingAuditSection.class);
	private AffairManager affairManager;

	private OrgManager orgManager;

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String getId() {
		return "pendingAuditSection";
	}

	@Override
	public String getBaseName() {
		return "pendingAudit";
	}
	public String getName(Map<String, String> preference) {
		return "pendingAudit";
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	@Override
	public Integer getTotal(Map<String, String> preference) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
		List<AgentModel> agentModelList = null;
		boolean isPloxy = false;
        boolean isEnabledEdoc = Functions.isEnableEdoc();
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelList;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelToList;
		}
		Map<Integer, AgentModel> agentModelMap = null;
		if(isPloxy){
            agentModelMap = new HashMap<Integer, AgentModel>();
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
                if(agentOptionStr.indexOf(ApplicationCategoryEnum.collaboration.key()) != -1){
                    agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);                    
                }
                if(isEnabledEdoc && agentOptionStr.indexOf(ApplicationCategoryEnum.edoc.key()) != -1){
                    agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);                    
                }
	    	}
		}
        List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
        apps.add(ApplicationCategoryEnum.collaboration);
        if(isEnabledEdoc){
            apps.add(ApplicationCategoryEnum.edoc);
        }
        boolean agentToFlag = (agentModelMap!=null && !agentModelMap.isEmpty());
        return affairManager.countPending(memberId, agentModelMap, null, null, null, agentToFlag, apps, true);
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
		User user = CurrentUser.get();
		Long memberId = user.getId();
        boolean isEnabledEdoc = Functions.isEnableEdoc();
		Pagination.setNeedCount(false); //不需要分页
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(8);
        
        List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
        List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
        List<AgentModel> agentModelList = null;
        boolean isPloxy = false;
        if(_agentModelList != null && !_agentModelList.isEmpty()){
            agentModelList = _agentModelList;
        }else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
            isPloxy = true;
            agentModelList = _agentModelToList;
        }
        Map<Integer, AgentModel> agentModelMap = null;
        if(isPloxy){
            agentModelMap = new HashMap<Integer, AgentModel>();
            for(AgentModel agentModel : agentModelList){
                String agentOptionStr = agentModel.getAgentOption();
                if(agentOptionStr.indexOf(ApplicationCategoryEnum.collaboration.key()) != -1){
                	if(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()) != null){
                		if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()).getStartDate()))
                			agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);        
                	}else{
                		agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);        
                	}
                }
                if(isEnabledEdoc && agentOptionStr.indexOf(ApplicationCategoryEnum.edoc.key()) != -1){
                	if(agentModelMap.get(ApplicationCategoryEnum.edoc.key()) != null ){
                		if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.edoc.key()).getStartDate()))
                			agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
                	}else{
                		agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
                	}
                }
            }
        }
        //添加代理链接
		if((_agentModelToList != null && !_agentModelToList.isEmpty()) || (_agentModelList != null && !_agentModelList.isEmpty())){
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_AGENT, "/main.do?method=agentPending&type=all&isAudit=true");
		}
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=auditMore");
        List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
        apps.add(ApplicationCategoryEnum.collaboration);
        if(isEnabledEdoc){
            apps.add(ApplicationCategoryEnum.edoc);
        }
        boolean agentToFlag = (agentModelMap!=null && !agentModelMap.isEmpty());
        List<Affair> affairs = affairManager.queryPendingList(memberId, agentModelMap, null, null, null, agentToFlag, apps, true);        
		if(affairs != null && !affairs.isEmpty()){
		    for (Affair affair : affairs) {
		        MultiRowFourColumnTemplete.Row row = c.addRow();
		        String forwardMember = affair.getForwardMember();
		        Integer resentTime = affair.getResentTime();
		        String subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
		        int app = affair.getApp();
		        //Long objectId = affair.getObjectId(); 
		        row.setSubject(subject);
		        ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
                String url = "";
		        switch (appEnum) {
		            case collaboration :
		                row.setLink("/collaboration.do?method=detail&from=Pending&affairId=" + affair.getId());
		                row.setCategory(app, "/collaboration.do?method=collaborationFrame&from=Pending");
		                break;
		            case edocSend:
						row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/edocController.do?method=entryManager&entry=sendManager";
						}
						row.setCategory(app, url);
						break;
					case edocRec:
						row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/edocController.do?method=entryManager&entry=recManager";
						}
						row.setCategory(app, url);
						break;
					case edocSign: 
						row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/edocController.do?method=entryManager&entry=signReport";
						}
						row.setCategory(app, url);
						break;
					case exSend:
						row.setLink("/exchangeEdoc.do?method=sendDetail&modelType=toSend&id="+affair.getSubObjectId());
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/exchangeEdoc.do?method=listMainEntry&modelType=toSend";
						}
						row.setCategory(app, url);
						break;
					case exSign:
						row.setLink("/exchangeEdoc.do?method=receiveDetail&modelType=toReceive&id="+affair.getSubObjectId());
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/exchangeEdoc.do?method=listMainEntry&modelType=toReceive";
						}
						row.setCategory(app, url);
						break;
					case edocRegister:
						row.setLink("/edocController.do?method=entryManager&entry=newEdoc&comm=register&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId(), BaseSectionTemplete.OPEN_TYPE.href);
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())))
						{
							url="/edocController.do?method=entryManager&entry=edocFrame&from=listRegisterPending";
						}
						row.setCategory(app, url);			
						break;
	            }
		        
		        V3xOrgMember member = null;
		        try {
		            member = orgManager.getMemberById(affair.getSenderId());
		        }
		        catch (BusinessException e) {
		            log.error("", e);
		        }
                if(member != null){
                    row.setCreateMemberName(member.getName());
                    row.setCreateMemberAlt(Functions.showMemberName(member));
                }
		        row.setCreateDate(affair.getCreateDate());
		        
		        if(isPloxy && !affair.getMemberId().equals(user.getId())){
		            row.setAgent(true); //代理
		        }
		        
		        row.setBodyType(affair.getBodyType());
		        row.setImportantLevel(affair.getImportantLevel());
		        row.setHasAttachments(affair.isHasAttachments());
		        Boolean isOverTime = affair.getIsOvertopTime();
		        //超期事件突出显示
		        row.setDistinct(isOverTime);
		        if(isOverTime){
		            row.addExtIcons("/common/images/timeout.gif");
		        }else if(affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0){
		            row.addExtIcons("/common/images/overTime.gif");
		        }
		    }
		}
		return c;
	}
}