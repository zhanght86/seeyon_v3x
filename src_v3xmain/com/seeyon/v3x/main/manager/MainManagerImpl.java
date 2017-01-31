/**
 * 
 */
package com.seeyon.v3x.main.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.BaseDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-2-18
 */
public class MainManagerImpl extends BaseDao implements MainManager {
	private static Log log = LogFactory.getLog(MainManagerImpl.class);
	
	private UserMessageManager userMessageManager;
	private AffairManager affairManager;
	private TaskInfoManager taskInfoManager;
	//wangjingjing begin
	private BulDataManager bulDataManager;
	private NewsDataManager newsDataManager;
	private OrgManager orgManager;
	//wangjingjing end
	
	//因为有循环依赖，所以采用延迟加载
	public void initBean(){
		userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
		affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
		taskInfoManager = (TaskInfoManager)ApplicationContextHolder.getBean("taskInfoManager");
		//wangjingjing begin
		bulDataManager = (BulDataManager)ApplicationContextHolder.getBean("bulDataManager");
		newsDataManager = (NewsDataManager)ApplicationContextHolder.getBean("newsDataManager");
		orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		//wangjingjing end
	}
	
	public Map<String, Integer> myInfo(){
		Map<String, Integer> result = new HashMap<String, Integer>();
		long memberId = CurrentUser.get().getId();
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(memberId);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
    	Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
		boolean agentToFlag = (Boolean)agentObj[0];
		Map<Integer,List<AgentModel>> ma = (Map<Integer,List<AgentModel>>)agentObj[1];
		
		int Task = taskInfoManager.getCountMyPendingTask(memberId);
		result.put("Task", Task);
		
		int allPendingColl = 0;
		int AllPendingEdoc = 0;
		int AllPendingAgent = 0;
		int Meeting = 0;
		int Inquiry = 0;
		int PubInfo = 0;
		int ZHBG = 0;
		
		int allPendingBanwen = 0;
		int allPendingYuewen = 0;
		int allMeetingNotification = 0;
		//代理事项
		if(!ma.isEmpty()){
			allPendingColl = getPendingColCount4All(memberId, agentToFlag, ma);
			
			if(SystemEnvironment.hasPlugin(ProductInfo.PluginNoMapper.edoc.name())){
				AllPendingEdoc = getPendingEdocCount4All(memberId, agentToFlag, ma);
			}

			AllPendingAgent = getPendingAgent4All(memberId, agentToFlag, ma);
			Meeting = getPendingMeetingCount(memberId, agentToFlag, ma);
			Inquiry = this.getInquiry(memberId, agentToFlag, ma);
			PubInfo = this.getPubInfo(memberId, agentToFlag, ma);
			
			result.put("AllPendingAgent", AllPendingAgent);
			
			if(SystemEnvironment.hasPlugin(ProductInfo.PluginNoMapper.zhbg.name())){
				ZHBG = this.getZHBG(memberId, agentToFlag, ma);
			}
		}
		else{
			String hql = "select app,subApp,count(*) from Affair where memberId=? and state=3 and isDelete=0 and (id in (select a.id from Affair a,ColSummary b where a.objectId = b.id and (b.secretLevel <= "+member.getSecretLevel()+" or b.secretLevel is null)) or id in (select a.id from Affair a,EdocSummary b where a.objectId = b.id and (b.edocSecretLevel <= "+member.getSecretLevel()+" or b.edocSecretLevel is null))) GROUP BY app,subApp";//成发集团项目 程炯 我的提醒根据流程密级进行新的统计
			List<Object[]> temp = super.find(hql, memberId);
			for (Object[] os : temp) {
				Integer appInt = (Integer)os[0];
				Integer subApp = (Integer)os[1];
				Integer count = (Integer)os[2];
				
				ApplicationCategoryEnum app =  ApplicationCategoryEnum.valueOf(appInt);
				if(app == null){
					continue;
				}
				
				switch (app) {
				case collaboration:
					allPendingColl = count;
					break;
				case edoc:
				case edocSend:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)) {
						allPendingBanwen += count;
						break;
					}
				case edocRec:
					if(SysFlag.sys_isGovVer.getFlag().equals(true) && subApp != null) {
						if(subApp == ApplicationSubCategoryEnum.edocRecHandle.getKey()) {
							allPendingBanwen += count;
						}else if(subApp == ApplicationSubCategoryEnum.edocRecRead.getKey()) {
							allPendingYuewen += count;
						}
						break;
					}
				case edocSign:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
						break;
					}
				case edocRegister:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
						break;
					}
				case exSend:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
						break;
					}
				case exSign:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
						break;
					}
				case exchange:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
						break;
					}
				case edocRecDistribute:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)){
						allPendingBanwen += count;
					}else {
						AllPendingEdoc += count;
					}
					break;
				case meeting:
					if(SysFlag.sys_isGovVer.getFlag().equals(true)) {
						if(subApp != null && subApp == ApplicationSubCategoryEnum.meetingNotification.getKey()) {
							allMeetingNotification += count;
						}
					}else {
						Meeting = count;
					}
					break;
				case news:
				case bulletin:
					PubInfo += count;
					break;
				case inquiry:
					if(subApp == ApplicationSubCategoryEnum.inquiry_audit.key()){
						PubInfo += count; //待审核
					}
					else if(subApp == ApplicationSubCategoryEnum.inquiry_write.key()){
						Inquiry = count; //填写
					}
					break;
				case office:
					ZHBG += count;
					break;
				default:
					break;
				}
			}
		}
		
		result.put("AllPendingColl", allPendingColl);
		result.put("AllPendingEdoc", AllPendingEdoc);
		result.put("Meeting", Meeting);
		result.put("Inquiry", Inquiry);
		result.put("PubInfo", PubInfo);
		result.put("ZHBG", ZHBG);
		
		if(SysFlag.sys_isGovVer.getFlag().equals(true)){
			result.put("AllPendingBanwen", allPendingBanwen);
			result.put("AllPendingYuewen", allPendingYuewen);
			result.put("AllMeetingNotification", allMeetingNotification);
		}
		/*if(SysFlag.sys_isGovVer.getFlag().equals(true)){
			//wangjingjing begin
			//政务【我的提醒】【待办公文】总数
			int AllPendingBanwen = this.getPendingBanwen(memberId, agentToFlag, ma);
			result.put("AllPendingBanwen", AllPendingBanwen);
			//政务【我的提醒】查【待办阅文】总数
			int AllPendingYuewen = this.getPendingYuewen(memberId, agentToFlag, ma);
			result.put("AllPendingYuewen", AllPendingYuewen);
			//政务【我的提醒】查【单位新闻】总数
			int AllDeptNews = this.getDeptNews(memberId, agentToFlag, ma);
			result.put("AllDeptNews", AllDeptNews);
			//政务【我的提醒】查【单位公告】总数
			int AllDeptBulletin = this.getDeptBulletin(memberId,agentToFlag, ma);
			result.put("AllDeptBulletin", AllDeptBulletin);
			//政务【我的提醒】查【会议通知】总数
			int AllMeetingNotification = this.getAllMeetingNotification(memberId,agentToFlag, ma);
			result.put("AllMeetingNotification", AllMeetingNotification);
		}*/
		
		//wangjingjing end
		return result;
	}
	//wangjinging begin
	//政务【我的提醒】【待办公文】总数
	public int getPendingBanwen(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
			ApplicationCategoryEnum.edoc
		);
		//19发文 21签报 16交换 22发文分发 23收文签收 24收文登记 34收文分发 4公文全部
		condition.addSearch(SearchCondition.policy4Portal, "A___19,A___16,A___21,A___22,A___23,A___24,A___34,S9___all", null);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}
	//政务【我的提醒】查【待办阅文】总数
	public int getPendingYuewen(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edoc
		);
		condition.addSearch(SearchCondition.policy4Portal, "S10___all", null);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}
	//政务【我的提醒】查【单位新闻】总数
	public int getDeptNews(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		Long result = null;
		List<NewsType> typeList = newsDataManager.getAllTypeList(CurrentUser.get().getAccountId());
		try{
			result = newsDataManager.findByReadUserCount(CurrentUser.get().getId(), typeList, CurrentUser.get().getAccountId(), null);
		}catch(Exception mye){
			log.error("【我的提醒】查【单位新闻】异常",mye);
		}
		return null == result ? 0 : result.intValue();
	}
	//政务【我的提醒】查【单位公告】总数
	public int getDeptBulletin(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		Long result = null;
		try{
			result = bulDataManager.find4UserInAccountCount(CurrentUser.get(), CurrentUser.get().getLoginAccount(), 1, null);
		}catch(Exception mye){
			log.error("【我的提醒】查【单位公告】异常",mye);
		}
		return null == result ? 0 : result.intValue();
	}
	//政务【我的提醒】查【会议通知】总数
	public int getAllMeetingNotification(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edocRec
		);
		condition.addSearch(SearchCondition.policy4Portal, "S5___all", null);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}
	//wangjingjing end

	public int getPendingColCount4All(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.collaboration
		);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}

	public int getPendingEdocCount4All(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.edoc
		);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}

	public int getPendingMeetingCount(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.meeting
		);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}

	public int getPubInfo(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.bulletin,
				ApplicationCategoryEnum.news,
				ApplicationCategoryEnum.inquiry
		);
		condition.addSearch(SearchCondition.policy4Portal, "A___8", null);
		condition.setAgent(agentToFlag, ma);
		return condition.getPendingCount(affairManager);
	}

	public int getInquiry(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){ //SubStateEnum.inquiry_write
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.inquiry
		);
		condition.addSearch(SearchCondition.policy4Portal, "A___10___1", null);
		//condition.setAgent(agentToFlag, ma); //调查待填没有代理
		return condition.getPendingCount(affairManager);
	}

	public int getZHBG(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending,
				ApplicationCategoryEnum.office
		);
		//condition.setAgent(agentToFlag, ma); //综合办公没有代理
		return condition.getPendingCount(affairManager);
	}
	
	public int getPendingAgent4All(long memberId, boolean agentToFlag, Map<Integer,List<AgentModel>> ma){
		List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
		apps.add(ApplicationCategoryEnum.collaboration);
		apps.add(ApplicationCategoryEnum.edoc);
		apps.add(ApplicationCategoryEnum.meeting);
		apps.add(ApplicationCategoryEnum.inquiry);
		apps.add(ApplicationCategoryEnum.bulletin);
		apps.add(ApplicationCategoryEnum.news);
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		if(isGov) {
			apps.add(ApplicationCategoryEnum.meetingroom);
		}
		/** 政务版   是否有信息报送的插件**/
        boolean hasInfoPlugin = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("govInfoPlugin");
        String infoSearchValue="";
        if(hasInfoPlugin){
		   apps.add(ApplicationCategoryEnum.info);//政务版——新增信息报送代理
		   infoSearchValue=",A___32";
		}
		AffairCondition condition = new AffairCondition(memberId, StateEnum.col_pending, apps);
		if(isGov) {
			//condition.addSearch(SearchCondition.policy4Portal, "A___1,A___4,A___6, A___8"+infoSearchValue, null);
		} else {
			condition.addSearch(SearchCondition.policy4Portal, "A___1,A___4,A___6,A___8"+infoSearchValue, null);
		}
		
		condition.setAgent(agentToFlag, ma);
		return condition.getAgentPendingCount(affairManager);
	}
}
