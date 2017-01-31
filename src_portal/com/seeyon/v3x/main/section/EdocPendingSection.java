package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:dongyj@seeyon.com">dongyj</a>
 * @version 1.0 2009-7-28 10:14
 */
public class EdocPendingSection extends BaseSection {
	private static final Log log = LogFactory.getLog(EdocPendingSection.class);

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
	public String getBaseName() {
		return "edocPending";
	}

	@Override
	public String getId() {
		return "edocPendSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		String cannelName = preference.get("columnsName");
		if(Strings.isNotBlank(cannelName)){
			return cannelName;
		}
		return "edocPending";
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		// 流程来源
		String currentPanel = SectionUtils.getPanel("all", preference);

		User user = CurrentUser.get();
		Long memberId = user.getId();

		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edoc
		);
		
		Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		
		if("all".equals(currentPanel)) {
			//全部
		}
		else {
			String tempStr = preference.get(currentPanel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return 0;
			}
			
			if("track_catagory".equals(currentPanel)){//分类
				condition.addSearch(SearchCondition.applicationEnum, tempStr, null);
			}
			else if("importLevel".equals(currentPanel)){//重要程度
				condition.addSearch(SearchCondition.importLevel, tempStr, null);
			}
		}
		
		condition.setAgent(agentToFlag, ma);
		
		return condition.getPendingCountSecretLevel(affairManager);//成发集团项目 程炯 待办公文密级筛选
	}

	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String currentPanel = SectionUtils.getPanel("all", preference);
		
        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
        c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=edocPending" +
				"&fragmentId=" + preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()) + "&currentPanel=" + currentPanel);
        // 流程来源
		String rowStr = preference.get("rowList");
        if(Strings.isBlank(rowStr)){
			rowStr = "subject,receiveTime,sendUser,category";
		}
        String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for(String row : rows){
			c.addRowName(row);
		}

        User user = CurrentUser.get();
		Long memberId = user.getId();

		Pagination.setNeedCount(false); // 不需要分页
		Pagination.setFirstResult(0);
		// 显示行数
		String count = preference.get("count");
		int coun = 8;
		if (Strings.isNotBlank(count)) {
			coun = Integer.parseInt(count);
		}
		Pagination.setMaxResults(coun);


		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edoc
		);
		
		Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		
		if("all".equals(currentPanel)) {
			//全部
		}
		else {
			String tempStr = preference.get(currentPanel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return c;
			}
			
			if("track_catagory".equals(currentPanel)){//分类
				condition.addSearch(SearchCondition.applicationEnum, tempStr, null);
			}
			else if("importLevel".equals(currentPanel)){//重要程度
				condition.addSearch(SearchCondition.importLevel, tempStr, null);
			}
		}
		
		condition.setAgent(agentToFlag, ma);
		
		List<Affair> affairs = condition.queryPendingAffairSecretLevel(affairManager);//成发集团项目 程炯 待办公文密级筛选

		if(affairs == null){
			return null;
		}
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();		
		for(Affair affair : affairs){
			String url = "";
			MultiRowFourColumnTemplete.Row row = c.addRow();

			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();

			String subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
			int app = affair.getApp();

			row.setSubject(subject);
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
			switch(appEnum){
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
				if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
					//branches_a8_v350_r_gov GOV-2641  唐桂林修改政务收文阅件链接 start
					if(isGov) {
						url="/edocController.do?method=entryManager&entry=recManager&objectId="+affair.getObjectId();
					} else {
						url = "/edocController.do?method=entryManager&entry=recManager";
					}					
					//branches_a8_v350_r_gov GOV-2641  唐桂林修改政务收文阅件链接 end
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
				if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
					//branches_a8_v350_r_gov GOV-2073  唐桂林修改政务待发送链接 start
					if(isGov) {
						url = "/edocController.do?method=entryManager&entry=sendManager&toFrom=listFenfa";
					} else {
						url = "/exchangeEdoc.do?method=listMainEntry&modelType=toSend";
					}					
					//branches_a8_v350_r_gov GOV-2073  唐桂林修改政务待发送链接 end
				}
				row.setCategory(app, url);
				break;
			case exSign:
				if(isGov) {	//政务公文版，首页待登记及登记信息链接	
					String modelType = "toReceive";
					if(affair.getApp()==23 && affair.getExtProperty("edocRecieveRetreat")!=null) {
						row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
						modelType = "retreat";
					}
					row.setLink("/exchangeEdoc.do?method=receiveDetail&modelType="+modelType+"&id="+affair.getSubObjectId()+"&affairId="+affair.getId());
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/edocController.do?method=entryManager&entry=recManager&toFrom=listRecieve"+"&affairId="+affair.getId();
						if(affair.getApp()==23 &&  affair.getExtProperty("edocRecieveRetreat")!=null) {
							url += "&listType=listRecieveRetreat";
						}
					}					
				} else {
					row.setLink("/exchangeEdoc.do?method=receiveDetail&modelType=toReceive&id="+affair.getSubObjectId());
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/exchangeEdoc.do?method=listMainEntry&modelType=toReceive";
					}
				}				
				row.setCategory(app, url);
				break;
			case edocRegister:
				//branches_a8_v350_r_gov GOV-2657  唐桂林修改政务公文登记链接 start
				if(isGov) {	//政务公文版，首页待登记及登记信息链接	
					row.setLink("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdocRegister&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId()+"&affairId="+affair.getId(), BaseSectionTemplete.OPEN_TYPE.href);
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/edocController.do?method=entryManager&entry=recManager&toFrom=listRegister&edocType="+EdocEnum.edocType.recEdoc.ordinal();
						if(affair.getApp()==24 && affair.getExtProperty("edocRegisterRetreat")!=null) {
							row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
							url += "&listType=registerRetreat";
						}
					}
				} else {
					row.setLink("/edocController.do?method=entryManager&entry=newEdoc&comm=register&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId(), BaseSectionTemplete.OPEN_TYPE.href);
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/edocController.do?method=entryManager&entry=edocFrame&from=listRegisterPending";
					}
				}		
				//branches_a8_v350_r_gov GOV-2657  唐桂林修改政务公文登记链接 end
				row.setCategory(app, url);
				break;
			case edocRecDistribute:
				row.setLink("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdoc&id="+affair.getObjectId()+"&affairId=" + affair.getId(), OPEN_TYPE.href);
				url = "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=listDistribute";
				row.setCategory(app, url);
				break;
			}
			
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(affair.getSenderId());
				row.setCreateMemberName(member.getName());
				row.setCreateMemberAlt(Functions.showMemberName(member));
			}
			catch (Throwable e) {
				log.error("", e);
			}
			if(member==null){
				row.setCreateMemberName("");
				row.setCreateMemberAlt("");
			}

			row.setCreateDate(affair.getCreateDate());

			row.setBodyType(affair.getBodyType());
			row.setImportantLevel(affair.getImportantLevel()); //没有重要这一说
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

		return c;
	}

}
