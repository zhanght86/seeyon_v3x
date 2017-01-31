package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.meeting.util.Constants;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.taglibs.functions.MainFunction;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 待办工作栏目
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-14
 */
public class PendingSection extends BaseSection {

	private static final Log log = LogFactory.getLog(PendingSection.class);

	private String titleId = "pendingSection";

	private AffairManager affairManager;

	private OrgManager orgManager;
	
	private ConfigGrantManager configGrantManager;
    
    public void setConfigGrantManager(ConfigGrantManager configGrantManager) {
		this.configGrantManager = configGrantManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String getId() {
		return titleId;
	}

	@Override
	public String getBaseName() {
		return "pending";
	}
	public String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if(Strings.isNotBlank(name)){
			return name;
		}
		return "pending";
	}

	@Override
	public String getIcon() {
		return null;
	}

	public Integer getTotal(Map<String, String> preference) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		String currentPanel = SectionUtils.getPanel("all", preference);
		
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
				ApplicationCategoryEnum.office,
				ApplicationCategoryEnum.info
		);

		if("all".equals(currentPanel)){
		}else if("overTime".equals(currentPanel)){
			condition.addSearch(SearchCondition.overTime, null, null);
		}else if("freeCol".equals(currentPanel)) {//自由协同
			condition.addSearch(SearchCondition.catagory, "catagory_coll", null);
		}else if(!"agent".equals(currentPanel)){
			String tempStr = preference.get(currentPanel+"_value");
			if(Strings.isBlank(tempStr) || "null".equalsIgnoreCase(tempStr)){
				return 0;
			}else{
				if("templete_pending".equals(currentPanel)){
					condition.addSearch(SearchCondition.templete, tempStr, null);
				}else if("Policy".equals(currentPanel)){
					condition.addSearch(SearchCondition.policy4Portal, tempStr, null);
				}else if("importLevel_pending".equals(currentPanel)){
					condition.addSearch(SearchCondition.importLevel, tempStr, null);
				}else if("catagory".equals(currentPanel)){
					condition.addSearch(SearchCondition.catagory, tempStr, null);
				}
			}
		}
		
		condition.setAgent(agentToFlag, ma);
		
		if("agent".equals(currentPanel) && ma != null && !ma.isEmpty()){
			return condition.getAgentPendingCount(affairManager);
		}
		else{
			if("sender".equals(currentPanel)){
				//查询指定发起人，用于查询指定发起人的时候查询比较复杂，所以采用HQL的方式进行查询，其他情况维持原来的逻辑不变
				String tempStr = preference.get(currentPanel+"_value");
				return (Integer)affairManager.getAffairListBySender(memberId,tempStr,condition,true);
			}
			else{
				return condition.getPendingCountSecretLevel(affairManager);//成发集团项目 程炯 获取根据密级筛选后的待办事项的数量
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public BaseSectionTemplete projection(Map<String, String> preference) {
        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
		User user = CurrentUser.get();
		Long memberId = user.getId();
		Long proxyId = user.getAgentToId();

		Pagination.setNeedCount(false); //不需要分页
		Pagination.setFirstResult(0);
		String count = preference.get("count");
		int coun = 8;
		if(Strings.isNotBlank(count)){
			coun = Integer.parseInt(count);
		}
		Pagination.setMaxResults(coun);
		String currentPanel = SectionUtils.getPanel("all", preference);
		String panels = "all";
		if(preference.get("panel") != null){
			panels = preference.get("panel");
		}
		String[] panelValues = panels.split(",");
		for(String panel : panelValues){
			String panelName = PortalConstants.getPanelName(panel, preference);
			c.addPanel(panel, panelName, null);
		}
		if(Strings.isBlank(currentPanel)){
			currentPanel = panelValues[0];
		}

		//显示列
		String rowStr = preference.get("rowList");
		if(Strings.isBlank(rowStr)){
			rowStr = "subject,receiveTime,sendUser,category";
		}
		String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for(String row : rows){
			c.addRowName(row);
		}

		Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];

		//if(!ma.isEmpty()){
	    //    c.addPanel("agent", "agent", null); 暂不加，通过“我的提醒 - 代理事项”来实现
		//}

		List<Affair> affairs = null;
		List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
		apps.add(ApplicationCategoryEnum.collaboration);
		apps.add(ApplicationCategoryEnum.edoc);
		apps.add(ApplicationCategoryEnum.meeting);
		apps.add(ApplicationCategoryEnum.bulletin);
		apps.add(ApplicationCategoryEnum.inquiry);
		apps.add(ApplicationCategoryEnum.news);
		apps.add(ApplicationCategoryEnum.office);
		apps.add(ApplicationCategoryEnum.info);
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending, apps);

		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=morePending&fragmentId="+preference.get(PropertyName.entityId.name())+"&ordinal="+preference.get(PropertyName.ordinal.name())+"&currentPanel="+currentPanel);
		condition.setAgent(agentToFlag, ma);
		if("all".equals(currentPanel)){
		}else if("overTime".equals(currentPanel)){
			condition.addSearch(SearchCondition.overTime, null, null);
		}else if("freeCol".equals(currentPanel)) {//自由协同
			condition.addSearch(SearchCondition.catagory, "catagory_coll", null);
		}else if(!"agent".equals(currentPanel)){
			String tempStr = preference.get(currentPanel+"_value");
			if(Strings.isBlank(tempStr) || "null".equalsIgnoreCase(tempStr)){
				return c;
			}else{
				if("templete_pending".equals(currentPanel)){
					condition.addSearch(SearchCondition.templete, tempStr, null);
				}else if("Policy".equals(currentPanel)){
					condition.addSearch(SearchCondition.policy4Portal, tempStr, null);
				}else if("importLevel_pending".equals(currentPanel)){
					condition.addSearch(SearchCondition.importLevel, tempStr, null);
				}else if("catagory".equals(currentPanel)){
					condition.addSearch(SearchCondition.catagory, tempStr, null);
				}
			}
		}

		if("agent".equals(currentPanel) && ma != null && !ma.isEmpty()){
			affairs = condition.queryAgentPendingAffair(affairManager);
		}else{
			if("sender".equals(currentPanel)){
				//查询指定发起人，用于查询指定发起人的时候查询比较复杂，所以采用HQL的方式进行查询，其他情况维持原来的逻辑不变
				String tempStr = preference.get(currentPanel+"_value");
				affairs = (List<Affair>)affairManager.getAffairListBySender(memberId,tempStr,condition,false);
			}else{
				affairs = condition.queryPendingAffairSecretLevel(affairManager);//成发集团项目 程炯 获取根据密级筛选后的待办事项
			}
		}

		if(affairs == null){
			return c;
		}
		
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();		
		boolean mtAppAuditFlag = true;
		boolean edocDistributeFlag = true;
		boolean hasMtAppAuditGrant = false;
		boolean hasEdocDistributeGrant = false;
		
		for (Affair affair : affairs) {
			String url="";
			MultiRowFourColumnTemplete.Row row = c.addRow();

			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();

			String subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
			int app = affair.getApp();
			Integer subApp = affair.getSubApp();
			Long objectId = affair.getObjectId();

			if(isGov){
				subject=subject.replaceAll("\r\n", "");
			}
				row.setSubject(subject);
			
			
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
			switch (appEnum) {
			case collaboration :
				row.setLink("/collaboration.do?method=detail&from=Pending&affairId=" + affair.getId());
				row.setCategory(app, "/collaboration.do?method=collaborationFrame&from=Pending");
				break;
			case meetingroom:
				if(null != affair.getSubApp() && affair.getSubApp().equals(ApplicationSubCategoryEnum.meetingRoomAudit.getKey())){
					row.setLink("/meetingroom.do?method=createPerm&openWin=1&id=" + objectId);
					url = "/meetingroom.do?method=index";
					row.setCategory(app, url);
				}
				break;
			case meeting :
				if(com.seeyon.v3x.doc.util.Constants.isGovVer()){
					if(null != affair.getSubApp() && affair.getSubApp().equals(ApplicationSubCategoryEnum.minutesAudit.getKey())){
						row.setLink("/mtSummary.do?method=mydetail&recordId="+affair.getObjectId()+"&mId="+affair.getSenderId());
						url = "/mtSummary.do?method=listHome&from=audit&listType=waitAudit";//branches_a8_v350_r_gov GOV-2503 ，向凡修改 ，【会议】链接界面也不正确，链接到待审核中去了
					}else if(null != affair.getSubApp() && affair.getSubApp().equals(ApplicationSubCategoryEnum.meetingAudit.getKey())){
						row.setLink("/mtAppMeetingController.do?method=mydetail&id="+affair.getObjectId()+"&oper=showContent");
						if(mtAppAuditFlag) {
							hasMtAppAuditGrant = configGrantManager.hasConfigGrant(user.getLoginAccount(), user.getId(), "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
						}	
						if(hasMtAppAuditGrant) {
							url = "/mtMeeting.do?method=entryManager&entry=meetingManager";
						}
						mtAppAuditFlag = false;
					}else if(null != affair.getSubApp() && affair.getSubApp().equals(ApplicationSubCategoryEnum.meetingNotification.getKey())){
						row.setLink("/mtMeeting.do?method=mydetail&id="+affair.getObjectId());
						url = "/mtMeeting.do?method=entryManager&entry=myMeetingManager";
					}else{
						row.setLink("/mtMeeting.do?method=myDetailFrame&id=" + objectId + "&state=" + Constants.DATA_STATE_SEND);
						url = "/mtMeeting.do?method=listHome&stateStr=10";
					}
				}else{
					row.setLink("/mtMeeting.do?method=myDetailFrame&id=" + objectId + "&state=" + Constants.DATA_STATE_SEND);
					url = "/mtMeeting.do?method=listHome&stateStr=10";
				}
				row.setCategory(app, subApp, url);
                break;
			case edocSend:
				row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
				if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))){
					url="/edocController.do?method=entryManager&entry=sendManager";
				}
				row.setCategory(app, url);
				break;
			case edocRec:
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						//branches_a8_v350_r_gov GOV-2641  唐桂林修改政务收文阅件链接 start
						url="/edocController.do?method=entryManager&entry=recManager&objectId="+affair.getObjectId();
						//branches_a8_v350_r_gov GOV-2641  唐桂林修改政务收文阅件链接 end
					}
				} else {
					row.setLink("/edocController.do?method=detail&from=Pending&affairId=" + affair.getId());
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/edocController.do?method=entryManager&entry=recManager";
					}
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
				row.setLink("/exchangeEdoc.do?method=sendDetail&modelType=toSend&id="+affair.getSubObjectId()+"&affairId="+affair.getId());
				if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
					//branches_a8_v350_r_gov GOV-2073  唐桂林修改政务收文阅件链接 start
					//branches_a8_v350_r_gov GOV-5016 唐桂林 公文的首页代办中，有一条公文分发数据，点击公文发文链接进去却没有数据 start
					if(isGov) {
						url = "/edocController.do?method=entryManager&entry=sendManager&toFrom=listFenfa";
						if(affair.getApp()==ApplicationCategoryEnum.exSend.key() &&  affair.getExtProperty("edocExSendRetreat")!=null) {
							row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
							url += "&modelType=sent";
						}
					} else {
						url = "/exchangeEdoc.do?method=listMainEntry&modelType=toSend";
					}					
					//branches_a8_v350_r_gov GOV-5016 唐桂林 公文的首页代办中，有一条公文分发数据，点击公文发文链接进去却没有数据 end
					//branches_a8_v350_r_gov GOV-2073  唐桂林修改政务收文阅件链接 end
				}
				try {
					if(Strings.isBlank(EdocRoleHelper.getUserExchangeAccountIds(user.getLoginAccount()))){
						url="";
					}
				} catch (BusinessException e1) {
					log.error("",e1);
				}
				row.setCategory(app, url);
				break;
			case exSign:
				if(isGov) {
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						String modelType = "toReceive";
						if(affair.getApp()==ApplicationCategoryEnum.exSign.key() &&  affair.getExtProperty("edocRecieveRetreat")!=null) {
							row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
							modelType = "retreat";
						}
						row.setLink("/exchangeEdoc.do?method=receiveDetail&id="+affair.getSubObjectId()+"&affairId="+affair.getId()+"&modelType="+modelType);
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
							url = "/edocController.do?method=entryManager&entry=recManager&toFrom=listRecieve&affairId="+affair.getId();
							if(affair.getApp()==ApplicationCategoryEnum.exSign.key() &&  affair.getExtProperty("edocRecieveRetreat")!=null) {
								row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
								if(affair.getApp()==ApplicationCategoryEnum.exSign.key() &&  affair.getExtProperty("edocRecieveRetreat")!=null) {
									url += "&listType=listRecieveRetreat";
								}
							}
						}						
					}
				}else {
					row.setLink("/exchangeEdoc.do?method=receiveDetail&modelType=toReceive&id="+affair.getSubObjectId()+"&affairId="+affair.getId());
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/exchangeEdoc.do?method=listMainEntry&modelType=toReceive";
					}
				}
				row.setCategory(app, url);
				break;
			case edocRegister:
				//branches_a8_v350_r_gov GOV-2657  唐桂林修改政务公文登记链接 start
				if(isGov) {
					boolean isEdocRegister = false;
					boolean isEdocRegisterRetreat = false;
					row.setLink("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdocRegister&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId()+"&affairId="+affair.getId(), BaseSectionTemplete.OPEN_TYPE.href);
					if(affair.getExtProperties()!=null && affair.getExtProperty("edocRegisterRetreat")!=null) {
						isEdocRegisterRetreat = true;
						row.setLink("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdocRegister&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId()+"&registerId="+affair.getObjectId()+"&comm=update&affairId="+affair.getId(), BaseSectionTemplete.OPEN_TYPE.href);
					}
					/*branches_a8_v350sp1_r_gov GOV-5018  政务向凡 添加 修复在切换到兼职单位是 有登记的URL跳转权限 Start */
					try {
						isEdocRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.recEdoc.ordinal());
					} catch (BusinessException e) {
						isEdocRegister = false;
						e.printStackTrace();
					}
					/*branches_a8_v350sp1_r_gov GOV-5018  政务向凡 添加 修复在切换到兼职单位是 有登记的URL跳转权限 End */
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey())) && isEdocRegister) {
						url = "/edocController.do?method=entryManager&entry=recManager&toFrom=listRegister&edocType="+EdocEnum.edocType.recEdoc.ordinal();
						if(affair.getApp()==24 && affair.getExtProperty("edocRegisterRetreat")!=null) {
							row.setSubject(subject+"("+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "edoc.gov.retreat.label")+")");
							url += "&listType=registerRetreat";
						}
					}
				} else {
					row.setLink("/edocController.do?method=entryManager&entry=newEdoc&comm=register&edocType="+EdocEnum.edocType.recEdoc.ordinal()+"&exchangeId="+affair.getSubObjectId()+"&edocId="+affair.getObjectId()+"&affairId="+affair.getId(), BaseSectionTemplete.OPEN_TYPE.href);
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						url="/edocController.do?method=entryManager&entry=edocFrame&from=listRegisterPending";
					}
				}
				//branches_a8_v350_r_gov GOV-2657  唐桂林修改政务公文登记链接 end
				row.setCategory(app, url);
				break;
			case edocRecDistribute:
				row.setLink("/edocController.do?method=entryManager&entry=recManager&toFrom=newEdoc&id="+affair.getObjectId()+"&affairId=" + affair.getId(), OPEN_TYPE.href);
				if(edocDistributeFlag) {
					try {
						hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.distributeEdoc.ordinal());
					} catch(Exception e) {
						hasEdocDistributeGrant = false;
					}
				}	
				if(hasEdocDistributeGrant) {
					url = "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=listDistribute";
				}
				edocDistributeFlag = false;
				row.setCategory(app, url);
				break;
			case info://信息报送
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
				row.setSubject(subject);
				row.setLink("/infoDetailController.do?method=detail&summaryId="+affair.getObjectId()+"&from=Pending&affairId=" + affair.getId() + "");
				row.setCreateDate(affair.getCompleteTime());
				url = "/infoNavigationController.do?method=indexManager&entry=infoAuditing&affairId="+affair.getObjectId();
				row.setCategory(affair.getApp(), url);
				break;	
				
			// 公共信息部分内容需可复制，其打开方式一律改为href_blank modified by Meng Yang 2009-06-18
			case bulletin:
				String[] bulLinks = MainFunction.getPendingCategoryLink(affair);
				row.setLink(bulLinks[0], OPEN_TYPE.href_blank);
				if ("agent".equals(currentPanel) && user.getId() != affair.getMemberId()) { // 代理人查看公告审核事项，不显示后面的应用链接
					row.setCategory(app, null);
				} else {
					row.setCategory(app, bulLinks[1]);
				}
				break;
			case news:
				String[] newsLinks = MainFunction.getPendingCategoryLink(affair);
				row.setLink(newsLinks[0], OPEN_TYPE.href_blank);
				if ("agent".equals(currentPanel) && user.getId() != affair.getMemberId()) { // 代理人查看新闻审核事项，不显示后面的应用链接
					row.setCategory(app, null);
				} else {
					row.setCategory(app, newsLinks[1]);
				}
				break;
			case inquiry:
				String[] links = MainFunction.getPendingCategoryLink(affair);

				row.setLink(links[0]);
				row.setCategory(app, links[1]);
				break;
			case office: // 综合办公审批
				if (ApplicationSubCategoryEnum.office_auto.key() == subApp.intValue()) { // 车辆
					row.setLink("javascript:openDetailInDlg('/autoAudit.do?method=edit&from=portal&applyId=" + objectId + "', '" + titleId + "', 750, 360)");
					row.setCategory(app, subApp, "/autoInfo.do?method=index&type=3");
				}
				else if (ApplicationSubCategoryEnum.office_stock.key() == subApp.intValue()) { // 办公用品
					row.setLink("javascript:openDetailInDlg('/stockAudit.do?method=edit&from=portal&applyId=" + objectId + "', '" + titleId + "', 750, 360)");
					row.setCategory(app, subApp, "/stockInfo.do?method=index&type=3");
				}
				else if (ApplicationSubCategoryEnum.office_asset.key() == subApp.intValue()) { // 办公设备
					row.setLink("javascript:openDetailInDlg('/asset.do?method=create_perm&from=portal&fs=1&id=" + objectId + "', '" + titleId + "', 750, 360)");
					row.setCategory(app, subApp, "/asset.do?method=jumpUrl&from=portal&url=office/asset/frameset");
				}
				else if (ApplicationSubCategoryEnum.office_book.key() == subApp.intValue()) { // 图书资料
					row.setLink("javascript:openDetailInDlg('/book.do?method=create_perm&from=portal&show=1&id=" + objectId + "', '" + titleId + "', 750, 360)");
					row.setCategory(app, subApp, "/book.do?method=jumpUrl&from=portal&url=office/book/frameset");
				}
				else if (ApplicationSubCategoryEnum.office_meetingroom.key() == subApp.intValue()) { // 会议室
					row.setLink("javascript:openDetailInDlg('/meetingroom.do?method=createPerm&openWin=1&id=" + objectId + "', '" + titleId + "', 750, 360)");
					row.setCategory(app, subApp, "/meetingroom.do?method=index&from=portal");
				}

				break;
			}

			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(affair.getSenderId());
				if (member == null && affair.getSenderId() == -1) {
					member = new V3xOrgMember();
					member.setName(affair.getExtProps() == null ? "" : affair.getExtProps());
					member.setOrgAccountId(user.getLoginAccount());
				}
			} catch (BusinessException e) {
				log.error("", e);
			}

			row.setCreateMemberName(Functions.showMemberName(member));
			row.setCreateMemberAlt(Functions.showMemberName(member));
			row.setCreateDate(affair.getReceiveTime());

			if(proxyId.equals(affair.getMemberId())){
				row.setAgent(true); //代理
			}
			row.setId(affair.getId());
			row.setObjectId(objectId);

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
            row.setPolicyName(getPolicyName(affair));
            //lijl添加,判断是否是政务版本 ->将李鉴林的那部分代码去掉
            //branches_a8_v350_r_gov GOV-3865 唐桂林  首页-个人空间-自定义待办栏目，设置显示节点权限列，权限为"协同"的权限显示为国际资源化key值 start
            if(isGov){
	            String str = row.getPolicyName();
				if(Strings.isNotBlank(str)){
					if(str.length()>8){
						str=str.substring(0,8);
						row.setPolicyName(str+"...");
					}
				}
            }
			//branches_a8_v350_r_gov GOV-3865 唐桂林  首页-个人空间-自定义待办栏目，设置显示节点权限列，权限为"协同"的权限显示为国际资源化key值 end
		}

		return c;
	}

	private String getPolicyName(Affair affair){
		String policy = affair.getNodePolicy();
		if(Strings.isNotBlank(policy)){
			return BPMSeeyonPolicy.getShowName(policy);
		}
		return null;
	}
}