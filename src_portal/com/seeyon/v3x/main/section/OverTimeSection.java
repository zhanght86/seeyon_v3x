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
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

public class OverTimeSection extends BaseSection{
	private static final Log log = LogFactory.getLog(OverTimeSection.class);
	private AffairManager affairManager;
	
	private OrgManager orgManager;
	
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	@Override
	public String getIcon() {
		return null;
	}
	
	@Override
	public String getId() {
		
		return "overTimeSection";
	}
	
	@Override
	protected String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if(Strings.isNotBlank(name)){
			return name;
		}
		return "overTime";
	}
	
	@Override
	public String getBaseName() {
		return  "overTime";
	}
	
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean isPloxy = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelList;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}else{
			isPloxy = false;
		}
		Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		if(isPloxy){
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.collaboration.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
	    			}else if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}else if(_agentOption == ApplicationCategoryEnum.meeting.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.meeting.key(), agentModel);
	    			}
	    		}
	    	}
		}
		List<ApplicationCategoryEnum> categorys = new ArrayList<ApplicationCategoryEnum>();
		categorys.add(ApplicationCategoryEnum.collaboration);
		categorys.add(ApplicationCategoryEnum.edoc);
		return affairManager.countPending(memberId, agentModelMap, "3", null, null, agentToFlag,categorys);
	}
	
	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {

        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
		User user = CurrentUser.get();
		Long memberId = user.getId();
		Long proxyId = user.getAgentToId();
		
		Pagination.setNeedCount(false); //不需要分页
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(8);
		
		String url="";
		
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean isPloxy = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelList;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}else{
			isPloxy = false;
		}
		Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		if(isPloxy){
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.collaboration.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
	    			}else if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}else if(_agentOption == ApplicationCategoryEnum.meeting.key()){
	    				agentModelMap.put(ApplicationCategoryEnum.meeting.key(), agentModel);
	    			}
	    		}
	    	}
		}
		if((_agentModelToList != null && !_agentModelToList.isEmpty()) || _agentModelList != null && !_agentModelList.isEmpty()){
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_AGENT, "/main.do?method=agentPending&type=all");
		}
		
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=overTimePending");
		List<ApplicationCategoryEnum> categorys = new ArrayList<ApplicationCategoryEnum>();
		categorys.add(ApplicationCategoryEnum.collaboration);
		categorys.add(ApplicationCategoryEnum.edoc);
		List<Affair> affairs = affairManager.queryPendingList(memberId, agentModelMap, "3", null, null, agentToFlag,categorys);
		
		if(affairs == null){
			return null;
		}
		
		for (Affair affair : affairs) {
			url="";
			MultiRowFourColumnTemplete.Row row = c.addRow();
			
			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();
			
			String subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
			int app = affair.getApp();
			
			row.setSubject(subject);
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
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
				row.setCreateMemberName(member.getName());
				row.setCreateMemberAlt(Functions.showMemberName(member));
			}
			catch (BusinessException e) {
				log.error("", e);
			}
			
			row.setCreateDate(affair.getCreateDate());
			
			if(proxyId.equals(affair.getMemberId())){
				row.setAgent(true); //代理
			}
			row.setBodyType(affair.getBodyType());
			row.setImportantLevel(affair.getImportantLevel());
			row.setHasAttachments(affair.isHasAttachments());
            //超期事件突出显示
            row.setDistinct(true);
            row.addExtIcons("/common/images/timeout.gif");
		}
		return c;
	}
}
