package com.seeyon.v3x.agent.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.authenticate.domain.V3xAgentDetailModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.util.MeetingAgent;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 代理设置controller
 *
 * @author jincm
 * @version 1.0 2008-7-30
 */
public class AgentIntercalateController extends BaseController {
	
	private AgentIntercalateManager agentIntercalateManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	private TempleteManager templeteManager;
	private TempleteCategoryManager templeteCategoryManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setAgentIntercalateManager(
			AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * 代理设置信息查看列表
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("agent/list");
		List<V3xAgent> _agentList = agentIntercalateManager.queryAvailabilityList1(user.getId());
		List<V3xAgent> _agentToList = agentIntercalateManager.queryAvailabilityList(user.getId());
		//String seeyonResourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		List<V3xAgent> agentList = new ArrayList<V3xAgent>();
		boolean isProxy = false;
//		boolean agentToFlag = true;
		if(_agentList != null && !_agentList.isEmpty()){
//			agentToFlag = false;
			agentList = _agentList;
			isProxy = true;
		}
		
		if(_agentToList != null && !_agentToList.isEmpty()){
		    isProxy = true;
			if(agentList!=null){
			    agentList.addAll(_agentToList);
			}else{
			    agentList = _agentToList;
			}
		}
	
		
		if(isProxy){
			for(V3xAgent agent : agentList){
				String agentOptionName = AgentUtil.getAgentOptionName(agent);
				agent.setAgentOptionName(agentOptionName);
			}
		}
		
		int size = agentList.size();
		Pagination.setRowCount(size);
        int pageSize = NumberUtils.toInt(request.getParameter("pageSize"),Pagination.getMaxResults());
        int page = NumberUtils.toInt(request.getParameter("page"), 1);
        if (pageSize < 1) {
            pageSize = Pagination.getMaxResults();
        }
        if (page < 1) {
            page = 1;
        }
        
        int fromIndex=(page-1)*pageSize;
        int toIndex=page*pageSize>size?size:page*pageSize;
        agentList=agentList.subList(fromIndex, toIndex);
        
		
		mav.addObject("agentToFlag", true);
		mav.addObject("agentList", agentList);
        mav.addObject("curtUserId", user.getId());
        
		return mav;
	}
	
	/**
	 * 代理设置历史信息查看列表
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView historyList(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("");
		
		return mav;
	}
	
	/**
	 * 代理提示页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView agentAlert(HttpServletRequest request,
	           HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("agent/agentAlert");
		return modelAndView ;
	}
	
	/**
	 * 修改是否提醒状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView agentNoAlert(HttpServletRequest request,
	           HttpServletResponse response) throws Exception{
		
		String ids = request.getParameter("ids");
		long currentId = CurrentUser.get().getId();
		
		if (Strings.isBlank(ids)) return null;
		
		String agentIds[] = ids.split("_");
		for (String id : agentIds) {
			agentIntercalateManager.updateIsAgentRemind(Long.parseLong(id), false, currentId);
		}
		PrintWriter out = response.getWriter();
    	out.println("<script>");
    	out.println("  window.close();");
    	out.println("</script>");
		return null ;
	}
	
	/**
	 * 新建或修改代理设置
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView createOrUpdateAgent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("agent/newAgent");
		String from = request.getParameter("from");
		Date firstDay = new Date(System.currentTimeMillis());
		Date lastDay = Datetimes.getLastDayInWeek(new Date(System.currentTimeMillis()));
		if("new".equals(from)){
			V3xAgent agent = new V3xAgent();
			mav.addObject("agent", agent);
			mav.addObject("firstDay", firstDay);
			mav.addObject("lastDay", lastDay);
			mav.addObject("operationType", "new");
			mav.addObject("agentToFlag", true);
		}else{
			Long id = Long.parseLong(request.getParameter("id"));
			V3xAgent agent = agentIntercalateManager.getById(id);
			boolean agentToFlag = false;
			if(agent.getAgentToId() == user.getId()){
				agentToFlag = true;
				V3xOrgMember member = this.orgManager.getMemberById(agent.getAgentId());
				mav.addObject("agentName", member.getName());
			}else{
				V3xOrgMember member = this.orgManager.getMemberById(agent.getAgentToId());
				mav.addObject("agentName", member.getName());
			}
			List<V3xAgentDetailModel> details = agentIntercalateManager.getDetailModelByAgentId(agent.getId());
			boolean freeColl = false;
			boolean tempAll = false;
			if(details != null){
				StringBuilder ids = new StringBuilder();
				StringBuilder names = new StringBuilder();
				String separator = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.separator.label");

				for (V3xAgentDetailModel model : details) {
					if (model.getEntityId() == 1) {
						freeColl = true;
						continue;
					}
					
					if (model.getEntityId() == 2) {
						tempAll = true;
						continue;
					}
					
					ids.append(model.getEntityId() + ",");
					names.append(model.getEntityName() + separator);
				}
				mav.addObject("ids", ids).addObject("names",names.length()==0 ? names : names.substring(0, names.length() - 1));
			}else if(agent.getAgentOption()!=null){
				String[] option = agent.getAgentOption().split("&");
				if(option != null){
					for(String o:option){   //旧数据或者选择了全部协同
						if("1".equals(o)){
							freeColl = true;
							tempAll = true;
							break;
						}
					}
				}
			}
			mav.addObject("firstDay", agent.getStartDate());
			mav.addObject("lastDay", agent.getEndDate());
			mav.addObject("agentToFlag", agentToFlag);
			mav.addObject("agent", agent);
			mav.addObject("operationType", "modify").addObject("freeColl", freeColl).addObject("tempAll", tempAll);
		}
		return mav;
	}
	
	/**
	 * 保存代理设置
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView saveAgent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long agentId = Long.parseLong(request.getParameter("surrogateValue"));
		String beginDate = request.getParameter("beginDate");
		Timestamp startDate = new Timestamp(Datetimes.parseDatetimeWithoutSecond(beginDate).getTime());
		String _endDate = request.getParameter("endDate");
		Timestamp endDate = new Timestamp(Datetimes.parseDatetimeWithoutSecond(_endDate).getTime());
		Timestamp now = new Timestamp(System.currentTimeMillis());
		String appTypeStr = "";
		String[] agentOption = request.getParameterValues("agentOption");
		String templateIds = request.getParameter("templateIds");  //模板id，如果是全部模板为2
		boolean selectFreeColl = false;                       //选择了自由流程
		boolean selectTemp = false;                           //选择了模板
		
		for(int i=0; i<agentOption.length; i++){
			if("0".equals(agentOption[i])){  //协同自由流程
				selectFreeColl = true;
				continue;
			}else if("1".equals(agentOption[i])){  //协同模板,2为全部模板，否则为模板id，使用“，”分割
				selectTemp = true;
				continue;
			}else if("2".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.edoc.key();
			}else if("3".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.meeting.key();
			}else if("4".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.bulletin.getKey() + "&" + ApplicationCategoryEnum.inquiry.getKey() + "&" + ApplicationCategoryEnum.news.getKey();
			}else if("5".equals(agentOption[i])){ //政务版--信息报送
				appTypeStr += ApplicationCategoryEnum.info.key();//政务版信息报送。
			}
			appTypeStr += "&";
		}
		boolean isAllColl = selectFreeColl && "2".equals(templateIds) && selectTemp;    //是否选择了全部协同，即自由流程+全部模板
		
		if(selectFreeColl || selectTemp)
			appTypeStr += ApplicationCategoryEnum.collaboration.key();
		
		V3xAgent agent = new V3xAgent();
		agent.setIdIfNew();
		agent.setAgentId(agentId);
		agent.setAgentToId(user.getId());
		agent.setCreateDate(now);
		agent.setStartDate(startDate);
		agent.setEndDate(endDate);
		agent.setAgentOption(appTypeStr);
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		
		//保存detail对象，如果选择了全部协同则不需要保存
		List<V3xAgentDetail> details = null;
		List<V3xAgentDetailModel> models = null;
		if(!isAllColl){
			details = new ArrayList<V3xAgentDetail>();
			models = new ArrayList<V3xAgentDetailModel>();
			V3xAgentDetail detail = null;
			V3xAgentDetailModel model = null;
			if(templateIds!= null && selectTemp){
				String[] ids = templateIds.split(",");
				for(String id:ids){
					detail = new V3xAgentDetail();
					detail.setIdIfNew();
					detail.setAgentId(agent.getId());
					detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
					detail.setEntityId(Long.parseLong(id));
					details.add(detail);
					
					model = new V3xAgentDetailModel();
					model.setId(detail.getId());
					model.setAgentId(detail.getAgentId());
					model.setApp(detail.getApp());
					model.setEntityId(detail.getEntityId());
					models.add(model);
				}
			}
			if(selectFreeColl){
				detail = new V3xAgentDetail();
				detail.setIdIfNew();
				detail.setAgentId(agent.getId());
				detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
				detail.setEntityId(1l);        //1为自由流程
				details.add(detail);
				
				model = new V3xAgentDetailModel();
				model.setId(detail.getId());
				model.setAgentId(detail.getAgentId());
				model.setApp(detail.getApp());
				model.setEntityId(detail.getEntityId());
				models.add(model);
			}
		}
		agentIntercalateManager.save(agent,details);
		
		//放入内存
		/*Date currentDate = new Date(System.currentTimeMillis());
		if(currentDate.compareTo(agent.getStartDate()) != -1){*/
			AgentModel agentModel = new AgentModel();
			agentModel.setAgentId(agent.getAgentId());
			agentModel.setAgentToId(agent.getAgentToId());
			agentModel.setId(agent.getId());
			agentModel.setAgentOption(agent.getAgentOption());
			agentModel.setStartDate(agent.getStartDate());
			agentModel.setEndDate(agent.getEndDate());
			agentModel.setAgentDetail(models);
			MemberAgentBean.getInstance().put(agent.getAgentId(), agentModel, null);
			MemberAgentBean.getInstance().put(agent.getAgentToId(), null, agentModel);
			//给代理人发送消息提醒
			agent.setAgentDetails(details);
			agentIntercalateManager.sendAgentSettingMessage(agent);
		//}
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		appLogManager.insertLog(user, AppLogAction.Agent_New,user.getName(),orgManager.getMemberById(agentId).getName(),agentOptionName,Datetimes.formatDatetimeWithoutSecond(agentModel.getStartDate()),
                Datetimes.formatDatetimeWithoutSecond(agentModel.getEndDate()));
		super.rendJavaScript(response, "parent.reFlesh();");
		return null;
	}
	
   
	
	/**
	 * 更新代理设置
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView updateAgent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long currentAgentId = Long.parseLong(request.getParameter("currentAgentId"));
		V3xAgent agent = agentIntercalateManager.getById(currentAgentId);
		
		List<AgentModel> agentModels = MemberAgentBean.getInstance().getAgentModelList(agent.getAgentId());
		List<AgentModel> agentModelTos = MemberAgentBean.getInstance().getAgentModelToList(agent.getAgentToId());
		
		/*if(agentModels != null && !agentModels.isEmpty()){
//			MemberAgentBean.getInstance().remove(agent.getAgentId(), agent.getAgentToId());
		}*/
		
		Long agentId = Long.parseLong(request.getParameter("surrogateValue"));
		Long agentToId = agent.getAgentToId();
		String beginDate = request.getParameter("beginDate");
		Timestamp startDate = new Timestamp(Datetimes.parseDatetimeWithoutSecond(beginDate).getTime());
		String _endDate = request.getParameter("endDate");
		Timestamp endDate = null;
		if(!"".equals(_endDate))
			endDate = new Timestamp(Datetimes.parseDatetimeWithoutSecond(_endDate).getTime());
		
		String appTypeStr = "";
		String[] agentOption = request.getParameterValues("agentOption");
		String templateIds = request.getParameter("templateIds");  //模板id，如果是全部模板为2
		boolean selectFreeColl = false;                       //选择了自由流程
		boolean selectTemp = false;                           //选择了模板
		
		for(int i=0; i<agentOption.length; i++){
			if("0".equals(agentOption[i])){  //协同自由流程
				selectFreeColl = true;
				continue;
			}else if("1".equals(agentOption[i])){  //协同模板,2为全部模板，否则为模板id，使用“，”分割
				selectTemp = true;
				continue;
			}else if("2".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.edoc.key();
			}else if("3".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.meeting.key();
			}else if("4".equals(agentOption[i])){
				appTypeStr += ApplicationCategoryEnum.bulletin.getKey() + "&" + ApplicationCategoryEnum.inquiry.getKey() + "&" + ApplicationCategoryEnum.news.getKey();
			}else if("5".equals(agentOption[i])){ //政务版--信息报送
				appTypeStr += ApplicationCategoryEnum.info.key();//政务版信息报送。
			}
			appTypeStr += "&";
		}
		boolean isAllColl = selectFreeColl && "2".equals(templateIds) && selectTemp;    //是否选择了全部协同，即自由流程+全部模板
		
		if(selectFreeColl || selectTemp)
			appTypeStr += ApplicationCategoryEnum.collaboration.key();
		
		agent.setAgentId(agentId);
		agent.setAgentToId(agentToId);
		agent.setStartDate(startDate);
		agent.setEndDate(endDate);
		agent.setAgentOption(appTypeStr);
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		
//		保存detail对象，如果选择了全部协同则不需要保存
		List<V3xAgentDetail> details = null;
		List<V3xAgentDetailModel> models = null;
		if(!isAllColl){
			details = new ArrayList<V3xAgentDetail>();
			models = new ArrayList<V3xAgentDetailModel>();
			V3xAgentDetail detail = null;
			V3xAgentDetailModel model = null;
			if(templateIds!= null && selectTemp){
				String[] ids = templateIds.split(",");
				for(String id:ids){
					detail = new V3xAgentDetail();
					detail.setIdIfNew();
					detail.setAgentId(agent.getId());
					detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
					detail.setEntityId(Long.parseLong(id));
					details.add(detail);
					
					model = new V3xAgentDetailModel();
					model.setId(detail.getId());
					model.setAgentId(detail.getAgentId());
					model.setApp(detail.getApp());
					model.setEntityId(detail.getEntityId());
					models.add(model);
				}
			}
			if(selectFreeColl){
				detail = new V3xAgentDetail();
				detail.setIdIfNew();
				detail.setAgentId(agent.getId());
				detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
				detail.setEntityId(1l);
				details.add(detail);
				
				model = new V3xAgentDetailModel();
				model.setId(detail.getId());
				model.setAgentId(detail.getAgentId());
				model.setApp(detail.getApp());
				model.setEntityId(detail.getEntityId());
				models.add(model);
			}
		}
		agentIntercalateManager.update(agent,details);
		
		AgentModel newAgentModel = new AgentModel();
		newAgentModel.setAgentId(agent.getAgentId());
		newAgentModel.setAgentToId(agent.getAgentToId());
		newAgentModel.setId(agent.getId());
		newAgentModel.setAgentOption(agent.getAgentOption());
		newAgentModel.setStartDate(agent.getStartDate());
		newAgentModel.setEndDate(agent.getEndDate());
		newAgentModel.setAgentDetail(models);
		
		//TODO 更新内存消息提醒未实现
		/*Date now = new Date(System.currentTimeMillis());
//		String nowStr = Datetimes.formatDate(now);
//		now = Datetimes.getTodayFirstTime(nowStr);
		Date createDate = agent.getStartDate();//new Date(agent.getStartDate().getTime());
		if(now.compareTo(createDate) != -1){*/
			//更新内存
			if(agentModels != null && !agentModels.isEmpty()){
				List<Long> ids;
				for(AgentModel agentModel : agentModels){
					if(agentModel.getId().equals(agent.getId())){
						//判断是否更换新的代理人
						if(!newAgentModel.getAgentId().equals(agentModel.getAgentId())){
							//如果原代理人只有1条记录，直接删除
							if(agentModels.size()==1){
								MemberAgentBean.getInstance().remove(agentModel.getAgentId(), null);
							}else{
								//如果原代理人有多条记录，从list中删除
								ids = new ArrayList<Long>(1);
								ids.add(agentModel.getAgentId());
								MemberAgentBean.getInstance().remove(agentModel.getAgentId(), ids, true, false);
							}
							MemberAgentBean.getInstance().put(newAgentModel.getAgentId(), newAgentModel, null);
						}
						agentModel.setAgentId(newAgentModel.getAgentId());
						agentModel.setAgentToId(newAgentModel.getAgentToId());
						agentModel.setAgentOption(newAgentModel.getAgentOption());
						agentModel.setStartDate(newAgentModel.getStartDate());
						agentModel.setEndDate(newAgentModel.getEndDate());
						agentModel.setAgentDetail(models);
						MemberAgentBean.getInstance().notifyUpdateAgentModel(agent.getAgentId());
						break;
					}
				}
			}else{
				//if(now.compareTo(createDate) != -1){
					MemberAgentBean.getInstance().put(agent.getAgentId(), newAgentModel, null);
				//}
			}
			
			if(agentModelTos != null && !agentModelTos.isEmpty()){
				for(AgentModel agentModelTo : agentModelTos){
					if(agentModelTo.getId().equals(agent.getId())){
						agentModelTo.setAgentId(newAgentModel.getAgentId());
						agentModelTo.setAgentToId(newAgentModel.getAgentToId());
						agentModelTo.setAgentOption(newAgentModel.getAgentOption());
						agentModelTo.setStartDate(newAgentModel.getStartDate());
						agentModelTo.setEndDate(newAgentModel.getEndDate());
						agentModelTo.setAgentDetail(models);
						MemberAgentBean.getInstance().notifyUpdateAgentModelTo(agent.getAgentToId());
						break;
					}
				}
			}else{
				//if(now.compareTo(createDate) != -1){
					MemberAgentBean.getInstance().put(agent.getAgentToId(), null, newAgentModel);
				//}
			}
		/*}else{
			MemberAgentBean.getInstance().remove(agent.getAgentId(), agent.getAgentToId());
		}*/
		//给代理人发送消息提醒
		agent.setAgentDetails(details);
		agentIntercalateManager.sendAgentSettingMessage(agent);
		/*if(agentModels != null && !agentModels.isEmpty()){
			MemberAgentBean.getInstance().setAgentModelList(newAgentModel.getAgentId(), agentModels);
			MemberAgentBean.getInstance().setAgentModelToList(newAgentModel.getAgentToId(), agentModelTos);
		}*/
		
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.Agent_Update,user.getName(),orgManager.getMemberById(agentId).getName(),agentOptionName,Datetimes.formatDatetimeWithoutSecond(newAgentModel.getStartDate()),
	                Datetimes.formatDatetimeWithoutSecond(newAgentModel.getEndDate()));
		  
		super.rendJavaScript(response, "parent.reFlesh();");
		return null;
	}
	
	/**
	 * 查看代理设置详细信息
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAgentDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("agent/agentDetail");
		Long id = Long.parseLong(request.getParameter("id"));
		V3xAgent agent = agentIntercalateManager.getById(id);
		boolean agentToFlag = false;
		if(agent.getAgentToId() == user.getId()){
			agentToFlag = true;
			V3xOrgMember member = this.orgManager.getMemberById(agent.getAgentId());
			mav.addObject("agentName", member.getName());
		}else{
			V3xOrgMember member = this.orgManager.getMemberById(agent.getAgentToId());
			mav.addObject("agentName", member.getName());
		}
		List<V3xAgentDetailModel> details = agentIntercalateManager.getDetailModelByAgentId(agent.getId());
		boolean freeColl = false;
		boolean tempAll = false;
		if(details != null){
			StringBuilder ids = new StringBuilder();
			StringBuilder names = new StringBuilder();
			String separator = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.separator.label");

			for (V3xAgentDetailModel model : details) {
				if (model.getEntityId() == 1) {
					freeColl = true;
					continue;
				}

				if (model.getEntityId() == 2) {
					tempAll = true;
					continue;
				}

				ids.append(model.getEntityId() + ",");
				names.append(model.getEntityName() + separator);
			}
			mav.addObject("ids", ids).addObject("names",names.length()==0 ? names : names.substring(0, names.length() - 1));
		}else if(agent.getAgentOption()!=null){
			String[] option = agent.getAgentOption().split("&");
			if(option != null){
				for(String o:option){   //旧数据或者选择了全部协同
					if("1".equals(o)){
						freeColl = true;
						tempAll = true;
						break;
					}
				}
			}
		}
		mav.addObject("agentToFlag", agentToFlag);
		mav.addObject("agent", agent).addObject("freeColl", freeColl).addObject("tempAll", tempAll);
		return mav;
	}
	
	/**
	 * 取消代理设置
	 * @author jincm 2008-7-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView cancelAgent(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String[] selectedIds = request.getParameterValues("id");
		List<Long> ids;
		for(int i=0; i<selectedIds.length; i++){
			V3xAgent agent = agentIntercalateManager.getById(Long.parseLong(selectedIds[i]));
			agent.setCancelFlag(true);
			agent.setCancelDate(new Timestamp(System.currentTimeMillis()));
			agentIntercalateManager.update(agent,null);
			
			//更新内存
			List<AgentModel> agentModels = MemberAgentBean.getInstance().getAgentModelList(agent.getAgentId());
			List<AgentModel> agentModelTos = MemberAgentBean.getInstance().getAgentModelToList(agent.getAgentToId());
			AgentModel _agentModel = null;
			if(agentModels != null && !agentModels.isEmpty()){
				for(AgentModel agentModel : agentModels){
					if(agentModel.getId().equals(agent.getId())){
						_agentModel = agentModel;
						break;
					}
				}
				//agentModels.remove(_agentModel);
				if(_agentModel != null){
					ids = new ArrayList<Long>(1);
					ids.add(_agentModel.getId());
					MemberAgentBean.getInstance().remove(_agentModel.getAgentId(), ids, true, true);
				}
				MemberAgentBean.getInstance().notifyUpdateAgentModel(agent.getAgentId());
				//给代理人发送消息提醒
				agentIntercalateManager.sendCancelAgentSettingMessage(agent);
			}
			
			AgentModel _agentModelTo = null;
			if(agentModelTos != null && !agentModelTos.isEmpty()){
				for(AgentModel agentModelTo : agentModelTos){
					if(agentModelTo.getId().equals(agent.getId())){
						_agentModelTo = agentModelTo;
						break;
					}
				}
				if(_agentModelTo != null){
					ids = new ArrayList<Long>(1);
					ids.add(_agentModelTo.getId());
					MemberAgentBean.getInstance().remove(_agentModelTo.getAgentToId(), ids, false, true);
				}
				//agentModelTos.remove(_agentModelTo);
				MemberAgentBean.getInstance().notifyUpdateAgentModelTo(agent.getAgentToId());
			}
			String agentOptionName = AgentUtil.getAgentOptionName(agent);
			appLogManager.insertLog(user, AppLogAction.Agent_Delete,user.getName(),orgManager.getMemberById(agent.getAgentId()).getName(),agentOptionName);
		}
		super.rendJavaScript(response, "parent.reFlesh();");
		return null;
	}
	
	/**
     * 页面框架
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView agentFrame(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        return new ModelAndView("agent/agentFrame");
    }
	
	/**
     * 默认方法为查看代理设置get()
     * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
   @Override
   public ModelAndView index(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
       return null;
   }
   
   public ModelAndView showTempletsFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
	   ModelAndView mav = new ModelAndView("agent/agent_show_templets_frame").addObject("categoryHTML", iOperBase.categoryHTML(templeteCategoryManager).toString());
	   return mav;
   }
   
   public ModelAndView showTemplets(HttpServletRequest request,HttpServletResponse response) throws Exception{
	   ModelAndView mav = new ModelAndView("agent/agent_show_templets");	  
	   User user = CurrentUser.get();
	   String searchType = request.getParameter("condition");
	   String textfield = null;
	   if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(searchType)) {
		   textfield = request.getParameter("categoryId");
	   } else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(searchType)) {
		   textfield = request.getParameter("textfield");
	   }
	   //查询当前用户已办过的所有模板
	   List<Templete> templates = this.templeteManager.getTemplatesByAffair(user.getId(), Integer.parseInt(request.getParameter("type")), StateEnum.col_done.getKey(), searchType, textfield);
	   TempleteCategorysWebModel categorysModel = templeteCategoryManager.getCategorys(user.getLoginAccount(), searchType, textfield, templates);
	   mav.addObject("tempList", templates).addObject("categorysModel", categorysModel);
	   return mav;
   }

   public void setTempleteManager(TempleteManager templeteManager) {
	   this.templeteManager = templeteManager;
   }

public void setTempleteCategoryManager(
		TempleteCategoryManager templeteCategoryManager) {
	this.templeteCategoryManager = templeteCategoryManager;
}
}