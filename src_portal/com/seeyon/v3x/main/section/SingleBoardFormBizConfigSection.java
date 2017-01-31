package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FormOwnerList;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigColumnManager;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 具备栏目挂接的表单业务配置，发布到个人空间后的单版块栏目<br>
 * 包括表单流程对应三种事项(待办、跟踪、督办)、表单查询、表单统计、表单上报以及信息中心的入口展现<br>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2009-8-11
 */
public class SingleBoardFormBizConfigSection extends BaseSection {	
	private static Log log = LogFactory.getLog(SingleBoardFormBizConfigSection.class);
	private FormBizConfigManager formBizConfigManager;
	private FormBizConfigColumnManager formBizConfigColumnManager;
	private TempleteManager templeteManager;
	private OrgManager orgManager;
	private ColSuperviseManager colSuperviseManager;
	private AffairManager affairManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private static final String MAIN_I18N_RES = "com.seeyon.v3x.main.resources.i18n.MainResources";
	
	/** 只有表单查询和表单统计时，三列展开：查询或统计模板名称、制作人、分类名称，设定其宽度 */
	private static final int[] THREE_CELLS_WIDTH = {62, 16, 22};
	/** 表单流程的各种事项分四列展开：事务名称、日期、发起人、分类名称，设定其宽度  */
	private static final int[] FOUR_CELLS_WIDTH = {60, 14, 14, 12};
	/** 标题使用样式：默认样式  */
	private static final String CSS_STYLE = "defaulttitlecss";
	/** URL的Context Path  */
	private static final String CONTEXT_PATH = SystemEnvironment.getA8ContextPath();
	/** 表单查询图标路径 */
	private static final String Icon_Form_Query = "/common/images/left/icon/302.gif";
	/** 表单统计图表路径 */
	private static final String Icon_Form_Statistic = "/common/images/left/icon/203.gif";
	/** 栏目默认显示数据条数：8条 */
	private static final int DEFAULT_SECTION_ROW_COUNT = 8;
	
	private static boolean containstime = true;
	private static boolean containssendUser = true;
	private static boolean containscategory = true;
	
	private static String todayLabel = Constant.getMainString("event.today");
	
	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardFormBizConfigSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
//		String columnsName = preference.get("columnsName");
		
		Long bizConfigId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()), -1l);
		try {
			if(bizConfigId != -1l) {
				FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
				// 仅当业务配置存在、具备栏目挂接，且当前用户有权（创建者或在共享范围中）使用时，才予以显示
				boolean valid = bizConfig != null && bizConfig.hasColumnConfig() && formBizConfigManager.isCreatorOrInShareScope(bizConfig, CurrentUser.get().getId());
				if(valid) {
					/*if (Strings.isNotBlank(columnsName)) {
						return columnsName;
					}*/
					boolean isEmpty = this.templeteManager.checkTempletes4BizConfigIsEmpty(bizConfigId);
					if(!isEmpty) {
						return bizConfig.getName();
					}
				}
					
			}
		} catch(Exception e){
			log.error("对表单栏目正常查看条件进行校验时出现异常：", e);
		}
		return null;
	}
	@Override
	public String getBaseName(Map<String, String> preference) {
		return this.getName(preference);
	}
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}
	
	/**
	 * 当栏目不具备正常查看的条件之一时，栏目展现为给出提示信息和刷新操作按钮，栏目会随着刷新操作消失
	 */
	private void showInfoWhenInvalid(MultiRowVariableColumnTemplete c) {		
		// 五行展现：提示信息、各种原因、[刷新操作]按钮
		String jsAction = "javascript:refreshPage();";
		for(int i = 0; i <= 5; i++) {
			MultiRowVariableColumnTemplete.Row row = c.addRow();
			MultiRowVariableColumnTemplete.Cell cell = row.addCell();
			if(i == 0)
				cell.setCellContent(FormBizConfigUtils.getI18NValue("bizconfig.columninvalid.label"));
			else
				cell.setCellContent(FormBizConfigUtils.getI18NValue("bizconfig.columninvalid.reason" + i));
			cell.setLinkURL(jsAction);
		}
		
		MultiRowVariableColumnTemplete.Row row2 = c.addRow();
		MultiRowVariableColumnTemplete.Cell cell2 = row2.addCell();
		cell2.setCellContent(FormBizConfigUtils.getI18NValue("bizconfig.columninvalid.refresh"));
		cell2.setLinkURL(jsAction);
		
		MultiRowVariableColumnTemplete.Row row3 = c.addRow();
		MultiRowVariableColumnTemplete.Cell cell3 = row3.addCell();
		cell3.setCellContentHTML("<a class='" + CSS_STYLE + "' href='" + jsAction + "'><font color='red'>[" + FormBizConfigUtils.getI18NValue("bizconfig.columninvalid.refreshoper") + "]</font></a>");
		cell3.setAlt(FormBizConfigUtils.getI18NValue("bizconfig.columninvalid.refreshoper"));
	}
	
	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String[] rows = SectionUtils.getRowList("subject,time,sendUser,category", preference);
		List<String> rowList = new ArrayList<String>();
		for (String row : rows) {
			rowList.add(row);
		}
		containstime = rowList.contains("time");
		containssendUser = rowList.contains("sendUser");
		containscategory = rowList.contains("category");
		
		MultiRowVariableColumnTemplete c = new MultiRowVariableColumnTemplete();
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
		User user = CurrentUser.get();
		
		String spaceType = preference.get(PropertyName.spaceType.name());
		
		// 该处的栏目被用户在点击系统消息配置时用别的栏目取代，此时所获取的idStr为空
		Long bizConfigId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()), -1);
		FormBizConfig bizConfig = bizConfigId==-1l ? null : this.formBizConfigManager.findById(bizConfigId);
		// 业务配置所选择的全部表单模板
		List<Templete> temps = this.templeteManager.getTempletes4BizConfigWithoutAuthCheck(bizConfigId);
		if(bizConfig == null || !bizConfig.hasColumnConfig() || !formBizConfigManager.isCreatorOrInShareScope(bizConfig, user.getId())
				|| CollectionUtils.isEmpty(temps)) {
			this.showInfoWhenInvalid(c);
			return c;
		}
		
		List<Long> domainIds = CommonTools.getUserDomainIds(user, this.orgManager);
		List<FormBizConfigColumn> columns = null;
		try {
			columns = this.formBizConfigColumnManager.getSelectedColumns(domainIds, bizConfigId);
		} catch (Exception e) {
			log.error("获取业务配置对象栏目挂接项时出现异常：", e);
		} 
		
		Map<Integer, Boolean> existMap = FormBizConfigUtils.getColumnCategoryExistInfo(columns);
		boolean isFormFlowExist = existMap.get(FormBizConfigConstants.COLUMN_FORM_FLOW) != null;
		boolean isFormQueryExist = existMap.get(FormBizConfigConstants.COLUMN_FORM_QUERY) != null;
		boolean isFormStatExist = existMap.get(FormBizConfigConstants.COLUMN_FORM_STATISTIC) != null;
		
		// 表单流程三种事项排序号，事项排列先后顺序应与用户配置结果一致
		int[] sortIds = FormBizConfigUtils.getFormFlowsSortId(columns);
		boolean pendingExist = sortIds[0] > 0;
		boolean trackExist = sortIds[1] > 0 ;
		boolean superviseExist = sortIds[2] > 0;
		
		List<Long> tempIds = CommonTools.getIds(temps);
		String tempIds4Url = StringUtils.join(tempIds, ",");
		int sectionCount = SectionUtils.getSectionCount(DEFAULT_SECTION_ROW_COUNT, preference);
		if(isFormFlowExist) {
			// 表单流程事项可以显示的理论总数
			int formFlowTotal = sectionCount - (isFormQueryExist ? 1 : 0)  - (isFormStatExist ? 1 : 0);
			
			// 记录待办、跟踪、督办事项的实际总数，以便不足时以空行补足
			int affairsTotal = 0;	
			if(pendingExist || trackExist || superviseExist) {
				int textLength = this.getLimitTextLength(width);
				V3xOrgMember member = null;
				try {
					member = this.orgManager.getMemberById(user.getId());
				} catch (BusinessException e) {
					log.error("获取当前用户对应人员信息时出现异常，人员ID[" + user.getId() + "]：", e);
				}
				
				// 按照表单流程事项理论总数分别抽取三种事项记录并记录所得真实数量
				List<Affair> pendings = pendingExist ? this.getPendingAffairs(formFlowTotal, tempIds) : null;
				List<Affair> tracks = trackExist ? this.getTrackAffairs(formFlowTotal, tempIds) : null;
				List<ColSuperviseModel> supervises = superviseExist ? this.getSuperviseAffairs(formFlowTotal, tempIds) : null;
				
				int pendingActualTotal = member != null && member.getAgentId() == -1 && CollectionUtils.isNotEmpty(pendings) ? pendings.size() : 0;
				int trackActualTotal = CollectionUtils.isNotEmpty(tracks) ? tracks.size() : 0;
				int superviseActualTotal = CollectionUtils.isNotEmpty(supervises) ? supervises.size() : 0;
				
				List<AffaritCountModel> result = CalculateAffairsCount.getAffairDisplayInfo(formFlowTotal, pendingExist, trackExist, superviseExist, 
						pendingActualTotal, trackActualTotal, superviseActualTotal, sortIds);
				
				boolean debugEnabled = log.isDebugEnabled(); 
				if(CollectionUtils.isNotEmpty(result)) {
					String linkUrlCommon = "/formBizConfig.do?method=enterManagerCenter&bizConfigId=" + bizConfigId + "&tempIds=" + tempIds4Url + "&type=column&flowId=";
					AffairDisplayParam param = null;
					for(AffaritCountModel model : result) {
						int displayCount = model.getDisplayCount();
						affairsTotal += displayCount;
						
						switch(model.getType()) {
						case AffaritCountModel.PENDING :
							param = new AffairDisplayParam(tempIds, textLength, linkUrlCommon + AffaritCountModel.PENDING_URL_PARAM);
							this.showPendingAffairs(CommonTools.getSubList(pendings, 0, displayCount), param, c);
							if(debugEnabled) {
								log.debug("待办事项显示：抽取了[" + pendingActualTotal + "]条，实际显示[" + displayCount + "]条");
							}
							break;
						case AffaritCountModel.TRACK :
							param = new AffairDisplayParam(tempIds, textLength, linkUrlCommon + AffaritCountModel.TRACK_URL_PARAM);
							this.showTrackAffairs(CommonTools.getSubList(tracks, 0, displayCount), param, c);
							if(debugEnabled) {
								log.debug("跟踪事项显示：抽取了[" + trackActualTotal + "]条，实际显示[" + displayCount + "]条");
							}
							break;
						case AffaritCountModel.SUPERVISE :
							param = new AffairDisplayParam(tempIds, textLength, linkUrlCommon + AffaritCountModel.SUPERVISE_URL_PARAM);
							this.showSuperviseAffairs(CommonTools.getSubList(supervises, 0, displayCount), param, c);
							if(debugEnabled) {
								log.debug("督办事项显示：抽取了[" + superviseActualTotal + "]条，实际显示[" + displayCount + "]条");
							}
							break;
						}
					}
				}
			}
			
			this.addBlankRows4FormFlows(formFlowTotal - affairsTotal, c);
			
			if(isFormQueryExist)
				this.showFormQuery(bizConfigId, width, tempIds4Url, columns, c);
			
			if(isFormStatExist)
				this.showFormStatistic(bizConfigId, width, tempIds4Url, columns, c);
		} 
		else {
			this.showFormQueryAndStatistic(columns, bizConfigId, tempIds4Url, width, c, sectionCount);
		}	
		
		// 设定底部按钮：表单上报(只出现当前用户有权使用的、业务配置所选择的表单模板)、信息中心
		List<Templete> tempsCanUse = this.templeteManager.getTempletes4BizConfig(bizConfigId, domainIds);
        this.setBottomButtons(bizConfigId, bizConfig, tempsCanUse, tempIds4Url, existMap, c, spaceType);
		return c;
	}
	
	/**
	 * 按照表单流程理论总数抽取的待办事项记录
	 * @param formflowTotal		表单流程事项允许总数量
	 * @param tempIdsList		对应表单模板ID集合
	 */
	private List<Affair> getPendingAffairs(int formflowTotal, List<Long> tempIdsList) {
		Long memberId = CurrentUser.get().getId();
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
		List<AgentModel> agentModelList = null;
		
		boolean agentToFlag = false;
		boolean isPloxy = false;
		
		if(_agentModelList != null && !_agentModelList.isEmpty()) {
			isPloxy = true;
			agentModelList = _agentModelList;
		} 
		else if(_agentModelToList != null && !_agentModelToList.isEmpty()) {
			isPloxy = true;
			agentModelList = _agentModelToList;
			agentToFlag = true;
		} 
		else {
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
	    			}
	    		}
	    	}
		}
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(formflowTotal);
		return affairManager.queryPendingListOfColl4BizConfig(memberId, agentModelMap, agentToFlag, tempIdsList);
	}
	
	/**
	 * 显示待办事项
	 */
	public void showPendingAffairs(List<Affair> affairsPending, AffairDisplayParam param, MultiRowVariableColumnTemplete c) {
		boolean dataValid = CollectionUtils.isNotEmpty(affairsPending);
		if(dataValid) {				
			for(Affair affair : affairsPending) {
				String forwardMember = affair.getForwardMember();
				Integer resentTime = affair.getResentTime();
				
				MultiRowVariableColumnTemplete.Row row = c.addRow();
				// 标题
				MultiRowVariableColumnTemplete.Cell cell = row.addCell();
				String important = "<span class='importance_" + affair.getImportantLevel() + " div-float'></span>";
				String att = affair.isHasAttachments() ? "<span class='attachment_table_true div-float'></span>" : "";
				String body = Strings.isNotBlank(affair.getBodyType()) && !"HTML".equals(affair.getBodyType()) ? "<span class='div-float bodyType_" + affair.getBodyType() + "'></span>" : "";
				int limitLen = param.getTextLength() - (Strings.isNotBlank(att) ? 2 : 0) - (Strings.isNotBlank(body) ? 4 : 0);
				
				cell.setCellContentHTML(important +"<span class=\"div-float\">"+ ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), limitLen, forwardMember, resentTime, orgManager, null) +"</span>"+ att + body);				
				cell.setLinkURL("/collaboration.do?method=detail&from=Pending&affairId=" + affair.getId());
				cell.setAlt(affair.getSubject());
				cell.setCellWidth(FOUR_CELLS_WIDTH[0]);
				if(affair.getIsOvertopTime()) {
					cell.addExtIcon("/common/images/timeout.gif");
				} else if(affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0) {
					cell.addExtIcon("/common/images/overTime.gif");
				}
				
				if (containstime) {
					// 日期
					MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
					String date = Datetimes.formatDate(affair.getCreateDate());
					String today = Datetimes.formatDate(Datetimes.getTodayFirstTime());
					if (date.equals(today)) {
						date = todayLabel;
					}
					cell2.setCellContent(date);
					cell2.setCellWidth(FOUR_CELLS_WIDTH[1]);
				} else {
					row.addCell();
				}
				
				if (containssendUser) {
					// 事务发起者
					MultiRowVariableColumnTemplete.Cell cell3 = row.addCell();
					this.setMemberName4Cell(cell3, affair.getSenderId());
					
					cell3.setCellWidth(FOUR_CELLS_WIDTH[2]);
				} else {
					row.addCell();
				}
				
				if (containscategory) {
					// 分类名称:此处为"待办"，点击分类名称"待办"，进入待办事项列表（与所选模板相对应）
					MultiRowVariableColumnTemplete.Cell cell4 = row.addCell();
					cell4.setCellContentHTML("<a href=\"javascript:openLink('" + CONTEXT_PATH +  param.getLinkUrl() + "')\" >" + FormBizConfigUtils.getI18NValue("bizconfig.pending.label") + "</a>");
					cell4.setCellWidth(FOUR_CELLS_WIDTH[3]);
				} else {
					row.addCell();
				}
			}
		}
	}
	
	/**
	 * 按照表单流程理论总数抽取的跟踪事项记录
	 * @param formflowTotal		表单流程事项允许总数量
	 * @param tempIdsList		对应表单模板ID集合
	 */
	private List<Affair> getTrackAffairs(int formflowTotal, List<Long> tempIdsList) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(formflowTotal);
		return affairManager.queryTrackList4BizConfig(memberId, tempIdsList);
	}
	
	/**
	 * 显示跟踪事项
	 */
	public void showTrackAffairs(List<Affair> trackAffairs, AffairDisplayParam param, MultiRowVariableColumnTemplete c) {
		if(CollectionUtils.isNotEmpty(trackAffairs)) {
			for(Affair affair : trackAffairs) {
				String forwardMember = affair.getForwardMember();
				Integer resentTime = affair.getResentTime();
				String from = null;					
				
				MultiRowVariableColumnTemplete.Row row = c.addRow();
				// 标题
				MultiRowVariableColumnTemplete.Cell cell = row.addCell();
				switch(StateEnum.valueOf(affair.getState())){
					case col_sent : from = "Sent"; break;
					case col_done : from = "Done"; break;
					default : from = "Done";
				}
				String important = "<span class='importance_" + affair.getImportantLevel() + " inline-block'></span>";
				String att = affair.isHasAttachments() ? "<span class='attachment_table_true inline-block'></span>" : "";
				String body = Strings.isNotBlank(affair.getBodyType()) && !"HTML".equals(affair.getBodyType()) ? "<span class='inline-block bodyType_" + affair.getBodyType() + "'></span>" : "";
				int limitLen = param.getTextLength() - (Strings.isNotBlank(att) ? 2 : 0) - (Strings.isNotBlank(body) ? 4 : 0);
				
				cell.setCellContentHTML(important +"<span class=\"inline-block\">"+ ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), limitLen, forwardMember, resentTime, orgManager, null) +"</span>"+ att + body);				
				cell.setLinkURL("/collaboration.do?method=detail&from=" + from + "&affairId=" + affair.getId());
				cell.setAlt(affair.getSubject());
				cell.setCellWidth(FOUR_CELLS_WIDTH[0]);
				if(affair.getIsOvertopTime()){
					cell.addExtIcon("/common/images/timeout.gif");
				}
				
				if (containstime) {
					// 日期
					MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
					String date = Datetimes.formatDate(affair.getCreateDate());
					String today = Datetimes.formatDate(Datetimes.getTodayFirstTime());
					if (date.equals(today)) {
						date = todayLabel;
					}
					cell2.setCellContent(date);
					cell2.setCellWidth(FOUR_CELLS_WIDTH[1]);
				} else {
					row.addCell();
				}
				
				if (containssendUser) {
					// 事务发起者		
					MultiRowVariableColumnTemplete.Cell cell3 = row.addCell();
					this.setMemberName4Cell(cell3, affair.getSenderId());
					cell3.setCellWidth(FOUR_CELLS_WIDTH[2]);
				} else {
					row.addCell();
				}
				
				if (containscategory) {
					// 分类名称，此处为"跟踪"，点击分类名称"跟踪"，进入跟踪事项列表（与所选模板相对应）
					MultiRowVariableColumnTemplete.Cell cell4 = row.addCell();
					cell4.setCellContentHTML("<a href=\"javascript:openLink('" + CONTEXT_PATH +  param.getLinkUrl() + "')\" >" + FormBizConfigUtils.getI18NValue("bizconfig.track.label") + "</a>");
					cell4.setCellWidth(FOUR_CELLS_WIDTH[3]);
				} else {
					row.addCell();
				}
			}
		}
	}
	
	/**
	 * 按照表单流程理论总数抽取的督办事项记录
	 * @param formflowTotal		表单流程事项允许总数量
	 * @param tempIdsList		对应表单模板ID集合
	 */
	private List<ColSuperviseModel> getSuperviseAffairs(int formflowTotal, List<Long> tempIdsList) {
		Long memberId = CurrentUser.get().getId();
		int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
		// 只取出协同类型、且与表单模板相关的督办事项
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(formflowTotal);
		return this.colSuperviseManager.getSuperviseCollListByCondition(null, null, null, memberId, status, tempIdsList);
	}
	
	/**
	 * 显示督办事项
	 */
	public void showSuperviseAffairs(List<ColSuperviseModel> supervises, AffairDisplayParam param, MultiRowVariableColumnTemplete c) {
		if(CollectionUtils.isNotEmpty(supervises)) {
			for(ColSuperviseModel model : supervises) {
				MultiRowVariableColumnTemplete.Row row = c.addRow();
				// 标题
				MultiRowVariableColumnTemplete.Cell cell = row.addCell();
				// 日期
				MultiRowVariableColumnTemplete.Cell cell2 = null;
				// 事务发起者
				MultiRowVariableColumnTemplete.Cell cell3 = null;
				// 分类名称，此处为"跟踪"，点击分类名称"督办"，进入督办事项列表（与所选模板相对应）
				MultiRowVariableColumnTemplete.Cell cell4 = null;				
				if( model!=null && model.getEntityType()!=null ) {
					// 标题
					String important = "<span class='importance_" + model.getImportantLevel() + " inline-block'></span>";
					String att = model.getHasAttachment() ? "<span class='attachment_table_true inline-block'></span>" : "";
					String body = Strings.isNotBlank(model.getBodyType()) && !"HTML".equals(model.getBodyType()) ? "<span class='inline-block bodyType_" + model.getBodyType() + "'></span>" : "";
					int limitLen = param.getTextLength() - (Strings.isNotBlank(att) ? 2 : 0) - (Strings.isNotBlank(body) ? 4 : 0);
					
					cell.setCellContentHTML(important +"<span class=\"inline-block\">"+ Strings.getLimitLengthString(model.getTitle(), limitLen, "...") +"</span>"+ att + body);
					cell.setLinkURL("/colSupervise.do?method=detail&summaryId=" + model.getSummaryId());					
					cell.setAlt(model.getTitle());
					cell.setCellWidth(FOUR_CELLS_WIDTH[0]);	
					
					if (containstime) {
						// 日期
						cell2 = row.addCell();
						String date = Datetimes.formatDate(model.getSendDate());
						String today = Datetimes.formatDate(Datetimes.getTodayFirstTime());
						if (date.equals(today)) {
							date = todayLabel;
						}
						cell2.setCellContent(date);
						cell2.setCellWidth(FOUR_CELLS_WIDTH[1]);
					} else {
						row.addCell();
					}
					
					if (containssendUser) {
						// 事务发起者
						cell3 = row.addCell();
						this.setMemberName4Cell(cell3, model.getSender());
						
						cell3.setCellWidth(FOUR_CELLS_WIDTH[2]);							
					} else {
						row.addCell();
					}
					
					if (containscategory) {
						// 分类名称，此处为"督办"，点击分类名称"督办"，进入跟踪事项列表（与所选模板相对应）
						cell4 = row.addCell();
						cell4.setCellContentHTML("<a href=\"javascript:openLink('" + CONTEXT_PATH +  param.getLinkUrl() + "');\" >" + FormBizConfigUtils.getI18NValue("bizconfig.supervise.label") + "</a>");
						cell4.setCellWidth(FOUR_CELLS_WIDTH[3]);
					} else {
						row.addCell();
					}
				}
			}
		}
	}
	
	/**
	 * 显示人员姓名单元格，如果人员无效，该列显示为空白
	 */
	private void setMemberName4Cell(MultiRowVariableColumnTemplete.Cell cell, Long memberId) {
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(memberId);
		} 
		catch (BusinessException e) {
			log.error("读取人员信息出现异常，人员ID[" + memberId + "]", e);
		}
		
		if(member != null) {
			cell.setCellContent(member.getName());
			cell.setAlt(Functions.showMemberName(member));
		} 
		else {
			cell.setCellContentHTML("&nbsp;&nbsp;");
		}
	}
	
	/**
	 * 表单流程各项事项实际总数之和不能填充满时，多余的行以空白填充，以确保表单查询或表单统计在最下面一行
	 * @param count 应当填充的行
	 * @param c		成倍行不定列栏目模板
	 */
	private void addBlankRows4FormFlows(int count, MultiRowVariableColumnTemplete c) {
		for(int i=0; i<count; i++) {
			MultiRowVariableColumnTemplete.Row row = c.addRow();
			for(int j=0; j<4; j++) {
				MultiRowVariableColumnTemplete.Cell cell = row.addCell();					
				cell.setCellContentHTML("&nbsp;&nbsp;");
				cell.setCellWidth(FOUR_CELLS_WIDTH[j]);
			}
		}
	}
	
	/**
	 * 一行集中显示表单查询或表单统计
	 */
	private void showFormQueryOrStatistic(Long bizConfigId, int width, String tempIds4Url, List<FormBizConfigColumn> columns, MultiRowVariableColumnTemplete c, boolean isQuery) {
		String icon = "<img src=\"/seeyon" + (isQuery ? Icon_Form_Query : Icon_Form_Statistic) + "\" height=\"14\" align=\"absmiddle\"/>&nbsp;&nbsp;";
		MultiRowVariableColumnTemplete.Row row = c.addRow();
		MultiRowVariableColumnTemplete.Cell cell1 = row.addCell();
		
		StringBuffer content = new StringBuffer("");
		content.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		int total = 0;
		int limitCount = 0;
		try {
			List<FormBizConfigColumn> queryOrStatColumns = this.findQueryOrReportColumns4Auth(columns, isQuery ? FormBizConfigConstants.COLUMN_FORM_QUERY : FormBizConfigConstants.COLUMN_FORM_STATISTIC);
			if(CollectionUtils.isNotEmpty(queryOrStatColumns)) {
				total = queryOrStatColumns.size();
				int[] result = this.getTempleteTotalAndTextLength(width, total);
				limitCount = result[0];
				int limitTitleLength = result[1];
				
				List<FormBizConfigColumn> actualColumns = CommonTools.getSubList(queryOrStatColumns, 0, limitCount);
				for(FormBizConfigColumn column : actualColumns) {
					content.append("<td>" + icon + "<a class='" + CSS_STYLE + "' href=\"javascript:showResult('" + (isQuery ? "query" : "report" ) + "', '" + 
										column.getFormId() +"', '" + bizConfigId + "', '" + Strings.escapeJavascript(column.getName()) +"')\" title='" + 
										Strings.escapeJavascript(column.getName()) + "' >" + Strings.escapeJavascript(Strings.getLimitLengthString(column.getName(), limitTitleLength, "...")) + 
								   "</a></td>");
				}
			} else {
				content.append("&nbsp;&nbsp;");
			}
		} catch(Exception e) {
			log.error("表单" + (isQuery ? "查询" : "统计") + "模板在表单业务配置栏目底部一行集中显示时出现异常：", e);
		}
		content.append("</tr></table>");
		cell1.setCellContentHTML(content.toString());
		cell1.setCellWidth(FOUR_CELLS_WIDTH[0]);
		
		MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
		if (total > limitCount) {
			cell2.setCellContentHTML("......");
		} else {
			cell2.setCellContentHTML("&nbsp;&nbsp;");
		}
		cell2.setCellWidth(FOUR_CELLS_WIDTH[1]);
		MultiRowVariableColumnTemplete.Cell cell3 = row.addCell();
		cell3.setCellContentHTML("&nbsp;&nbsp;");
		cell3.setCellWidth(FOUR_CELLS_WIDTH[2]);
		
		MultiRowVariableColumnTemplete.Cell cell4 = row.addCell();
		String linkUrl = "/formBizConfig.do?method=enterManagerCenter&bizConfigId=" + bizConfigId +"&tempIds="+tempIds4Url+"&type=column&tabNum=" + (isQuery ? 1 : 2);
		cell4.setCellContentHTML("<a  href=\"javascript:openLink('" + CONTEXT_PATH + linkUrl + "')\" >" + FormBizConfigUtils.getI18NValue("bizconfig." + (isQuery ? "query" : "statistic") + ".label") + "</a>");
		cell4.setCellWidth(FOUR_CELLS_WIDTH[3]);
	}
	
	/**
	 * 获取在给定的栏目挂接项中具有使用权限的表单查询<b>或</b>表单统计模板对应栏目
	 * @param columns  给定的栏目挂接项集合<b>其中的表单查询或统计模板已经经过了权限校验，无需在此重复校验</b>
	 * @param category 属于表单查询还是表单统计
	 */
	private List<FormBizConfigColumn> findQueryOrReportColumns4Auth(List<FormBizConfigColumn> columns, int category) throws DataDefineException, BusinessException {
		List<FormBizConfigColumn> result = new ArrayList<FormBizConfigColumn>();
		if(CollectionUtils.isNotEmpty(columns)) {
			for(FormBizConfigColumn column : columns) {
				if(column.getParentCategory() == category) {
					result.add(column);
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取在给定的栏目挂接项中具有使用权限的表单查询<b>和</b>表单统计模板对应栏目
	 * @param columns  给定的栏目挂接项集合<b>其中的表单查询或统计模板已经经过了权限校验，无需在此重复校验</b>
	 */
	private List<FormBizConfigColumn> findQueryAndReportColumns4Auth(List<FormBizConfigColumn> columns) throws DataDefineException, BusinessException {
		List<FormBizConfigColumn> result = new ArrayList<FormBizConfigColumn>();
		if(CollectionUtils.isNotEmpty(columns)) {
			for(FormBizConfigColumn column : columns) {
				if(column.isFormQueryOrStatistic()) {
					result.add(column);
				}
			}
		}
		return result;
	}

	/**
	 * 在表单流程存在时，如果存在表单查询，用一行、按照当前用户的权限在栏目底部予以展现：
	 * 展现形式为："表单查询:模板1名称缩写[图标] 模板2名称缩写[图标] 模板3名称缩写[图标]..."
	 */
	private void showFormQuery(Long bizConfigId, int width, String tempIds4Url, List<FormBizConfigColumn> columns, MultiRowVariableColumnTemplete c) {
		this.showFormQueryOrStatistic(bizConfigId, width, tempIds4Url, columns, c, true);
	}
	
	/**
	 * 在表单流程存在时，如果存在表单统计，用一行、按照当前用户的权限在栏目底部予以展现：
	 * 展现形式为："表单统计:模板1名称缩写[图标] 模板2名称缩写[图标] 模板3名称缩写[图标]"
	 */
	private void showFormStatistic(Long bizConfigId, int width, String tempIds4Url, List<FormBizConfigColumn> columns, MultiRowVariableColumnTemplete c) {
		this.showFormQueryOrStatistic(bizConfigId, width, tempIds4Url, columns, c, false);
	}
	
	/**
	 * 如果不存在表单流程，则栏目纵向列出当前用户有权使用的所有表单查询、统计模板
	 */
	private void showFormQueryAndStatistic(List<FormBizConfigColumn> columns, Long bizConfigId, String tempIds4Url, int width, MultiRowVariableColumnTemplete c, int sectionCount) {
		try {				
			List<FormBizConfigColumn> q_r_columns = this.findQueryAndReportColumns4Auth(columns);
			q_r_columns = CommonTools.getSubList(q_r_columns, 0, sectionCount);				
			if(CollectionUtils.isNotEmpty(q_r_columns)) {
				int limitTextLength = this.getLimitTextLength4Temp(width);
				String linkUrl = "/formBizConfig.do?method=enterManagerCenter&bizConfigId=" + bizConfigId + "&tempIds=" + tempIds4Url + "&type=column&tabNum=";
				
				boolean isQuery = false;
				for(FormBizConfigColumn column : q_r_columns) {
					isQuery = column.getParentCategory() == FormBizConfigConstants.COLUMN_FORM_QUERY;
					
					// 表单模板名称+图标
					MultiRowVariableColumnTemplete.Row row = c.addRow();
					MultiRowVariableColumnTemplete.Cell cell = row.addCell();
					cell.setCellContent(Strings.getLimitLengthString(column.getName(), limitTextLength, "..."));					
					cell.setAlt(column.getName());
					cell.addExtIcon(isQuery ? Icon_Form_Query : Icon_Form_Statistic);
					cell.setLinkURL("javascript:showResult('" + (isQuery ? "query" : "report") + "', '" + column.getFormId() +"', '" + bizConfigId + "', '" + Strings.escapeJavascript(column.getName()) +"')");
					cell.setCellWidth(THREE_CELLS_WIDTH[0]);
					
					// 制作人
					MultiRowVariableColumnTemplete.Cell cell1 = row.addCell();
					FormOwnerList fol = new FormOwnerList();
					fol.setAppmainId(column.getFormId());
					FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
					fol = (FormOwnerList)formDaoManager.queryOwnerListByCondition(fol).get(0);
					this.setMemberName4Cell(cell1, fol.getOwnerId());
					cell1.setCellWidth(THREE_CELLS_WIDTH[1]);	
					
					// 所属操作类型：表单查询或表单统计											
					MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
					cell2.setCellContentHTML("<a href=\"javascript:openLink('" + CONTEXT_PATH + linkUrl + (isQuery ? 1 : 2) + "')\" >" + (isQuery ? ResourceBundleUtil.getString(MAIN_I18N_RES, "menu.formquery.label") : ResourceBundleUtil.getString(MAIN_I18N_RES, "menu.formstat.label")) + "</a>");
					cell2.setCellWidth(THREE_CELLS_WIDTH[2]);
				}
			}
		} catch(Exception e) {
			log.error("表单查询、统计模板在表单业务配置栏目中显示时出现异常：", e);
		}
	}
	
	/**
	 * 设定底部按钮：表单上报、信息中心
	 * @param tempsCanUse	      	业务配置所设定的、当前用户有权使用的表单模板
	 * @param bizConfigId 	业务配置ID
	 * @param bizConfig   	业务配置
	 * @param tempIds4Url 	表单模板ID字符串
	 * @param c           	栏目模板
	 */
	private void setBottomButtons(Long bizConfigId, FormBizConfig bizConfig, List<Templete> tempsCanUse, String tempIds4Url, Map<Integer, Boolean> existMap, MultiRowVariableColumnTemplete c, String spaceType) {
		// 仅当上报对应栏目挂接项选中时才在下方出现对应功能按钮
		boolean isFormReportExist = existMap.get(Integer.valueOf(FormBizConfigConstants.COLUMN_FORM_REPORT)) != null;
		
		// 上报：点击时跳出一个浮动框，列出当前用户选择的表单模板（且未停用），点击时新建协同
		if(isFormReportExist) {
			String buttonActionUrl = "javascript:alert('" + FormBizConfigUtils.getI18NValue("bizconfig.formreport.invalid") + "');";
			if(CollectionUtils.isNotEmpty(tempsCanUse)) {
				StringBuffer sb4FormReport = new StringBuffer("");
				for(Templete templete : tempsCanUse) {
					sb4FormReport.append(templete.getId()).append(",").append(Strings.escapeJavascript(templete.getSubject())).append("|");
				}
				// 添加标识，以便在section.js的处理方法中将其的href改为onClick以获取鼠标点击坐标，从而动态获取浮动框的定位坐标
				buttonActionUrl = "FormBizConfig%javascript:showDiv('" + sb4FormReport.toString() + "', '" + bizConfigId + "')";
			}
			c.addBottomButton(FormBizConfigUtils.getI18NValue("bizconfig.report.label"), buttonActionUrl);
		}
		
		String infoCenterButtonName = bizConfig.getName() + FormBizConfigUtils.getI18NValue("bizconfig.center.label");
		c.addBottomButton(infoCenterButtonName, "/formBizConfig.do?method=enterManagerCenter&bizConfigId=" + bizConfigId + "&tempIds=" + tempIds4Url + "&spaceType=" + spaceType + "&type=column");
	}
	
	/**
	 * 获取不同宽度下表单流程事项标题的字数上限
	 * @param width 不同栏目布局下的所占宽度：2、3、4、5、7、8、10
	 */
	public int getLimitTextLength(int width) {
		int length = 0;
		switch(width) {
		case 2:
		case 3:
			length = 20;
			break;
		case 4:
			length = 40;
			break;
		case 5:
			length = 46;
			break;
		case 7:
			length = 70;
			break;
		case 8:	
			length = 80;
			break;
		case 10:
			length = 100;
			break;
		}
		return length;
	}
	
	/**
	 * 获取不同宽度下只有表单查询和统计模板时，统计模板名称所能展现的字数上限
	 * @param width 不同栏目布局下的所占宽度：2、3、4、5、7、8、10
	 */
	public int getLimitTextLength4Temp(int width) {
		int length = 0;
		switch(width) {
		case 2:
		case 3:
			length = 20;
			break;
		case 4:
			length = 40;
			break;
		case 5:
			length = 80;
			break;
		case 7:
			length = 100;
			break;
		case 8:	
			length = 120;
			break;
		case 10:
			length = 150;
			break;
		}
		return length;
	}
	
	/**
	 * 获取在不同展现宽度情况下，所能展现的表单查询或表单模板总数及模板字数上限
	 * @param width 不同栏目布局下的所占宽度：2、3、4、5、7、8、10
	 * @param actualTotal 实际模板总数
	 * @return 模板展现总数、模板字数上限
	 */
	private int[] getTempleteTotalAndTextLength(int width, int actualTotal) {
		int[] result = new int[2];
		int textLength = SectionUtils.getTextMaxLength(width);
		switch(width) {
		case 2:
		case 3:
			result[0] = 1;
			result[1] = actualTotal > 1 ? 10 : 20;
			break;
		case 4:
			result[0] = 2;
			result[1] = actualTotal > 1 ? 10 : 20;
			break;
		case 5:
			result[0] = 3;
			result[1] = actualTotal > 0 && actualTotal < 3 ? 30 / actualTotal : textLength / 3;
			break;
		case 7:
			result[0] = 4;
			result[1] = actualTotal > 0 && actualTotal < 4 ? 60 / actualTotal : textLength / 4;
			break;
		case 8:
			result[0] = 5;
			result[1] = actualTotal > 0 && actualTotal < 5 ? 80 / actualTotal : textLength / 5;
			break;
		case 10:
			result[0] = 6;
			result[1] = actualTotal > 0 && actualTotal < 6 ? 100 / actualTotal : textLength / 6;
			break;
		}
		
		return result;
	}
	
	/**
	 * 封装表单业务配置栏目中表单流程对应三种事项显示时所需要的参数，避免对应方法参数列过长，参数个数过多<br>
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-4-1
	 */
	static class AffairDisplayParam {
		/** 对应的表单模板ID集合  */
		private List<Long> tempIdsList;
		
		/** 栏目在不同宽度下时，流程标题所能显示的字数上限  */
		private int textLength;
		
		/** 不同流程事项对应分类（如：待办、跟踪、督办）的链接地址  */
		private String linkUrl;
		
		public AffairDisplayParam(List<Long> tempIdsList, int textLength, String linkUrl) {
			super();
			this.tempIdsList = tempIdsList;
			this.textLength = textLength;
			this.linkUrl = linkUrl;
		}

		public String getLinkUrl() {
			return linkUrl;
		}
		public void setLinkUrl(String linkUrl) {
			this.linkUrl = linkUrl;
		}
		public List<Long> getTempIdsList() {
			return tempIdsList;
		}
		public void setTempIdsList(List<Long> tempIdsList) {
			this.tempIdsList = tempIdsList;
		}
		public int getTextLength() {
			return textLength;
		}
		public void setTextLength(int textLength) {
			this.textLength = textLength;
		}
	}
	
	/**
	 * 在<b>2</b>种以上事项并存时，在事项总数限定情况下，需要充分占有、均匀分配栏目显示空间<br>
	 * 为此创建此类用于优化处理方式，以得到一个相对较为合理的各种事项显示数量组合<br>
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-4-1
	 */
	static class CalculateAffairsCount {
		/**
		 * 计算待办、跟踪、督办三种事项在总数限定的情况下，显示得较为均匀合理的数量组合<br>
		 * @param formFlowTotal			表单流程事项可以显示的理论总条数，可以是6、7、8
		 * @param pengdingExist			是否存在待办事项
		 * @param trackExist			是否存在跟踪事项
		 * @param superviseExist		是否存在督办事项
		 * @param pendingActualTotal	待办事项存在时，先按照formFlowTotal数量抽取待办事项，并获取其真实条数（小于等于formFlowTotal）
		 * @param trackActualTotal		跟踪事项存在时，先按照formFlowTotal数量抽取跟踪事项，并获取其真实条数（小于等于formFlowTotal）
		 * @param superviseActualTotal	督办事项存在时，先按照formFlowTotal数量抽取督办事项，并获取其真实条数（小于等于formFlowTotal）
		 * @param sortIds				三种事项原始排序号数组，依次为：待办、跟踪、督办
		 * @return List&lt;AffaritCountModel&gt;	流程事项展现数量模型集合
		 */
		public static List<AffaritCountModel> getAffairDisplayInfo(int formFlowTotal, boolean pengdingExist, boolean trackExist, boolean superviseExist, 
				int pendingActualTotal, int trackActualTotal, int superviseActualTotal, int[] sortIds) {
			List<AffaritCountModel> result = new ArrayList<AffaritCountModel>();
			
			int[] counts = arrangeAverage(pendingActualTotal, trackActualTotal, superviseActualTotal, formFlowTotal);
			
			if(pengdingExist && counts[0] > 0)
				result.add(new AffaritCountModel(AffaritCountModel.PENDING, counts[0], sortIds[0]));
			
			if(trackExist && counts[1] > 0)
				result.add(new AffaritCountModel(AffaritCountModel.TRACK, counts[1], sortIds[1]));
			
			if(superviseExist && counts[2] > 0)
				result.add(new AffaritCountModel(AffaritCountModel.SUPERVISE, counts[2], sortIds[2]));
			
			if(result.size() > 1) {
				// 三种事项的显示顺序需与用户对栏目挂接项中表单流程下的子节点顺序配置结果一致
				Collections.sort(result);
			}
			
			return result;
		}
		
		/**
		 * 均匀分配三种事项数量
		 * @param pending		待办
		 * @param track			跟踪
		 * @param supervise		督办
		 * @param sumNum		总数
		 * @return int[] 事项显示条数数组：0 - 待办显示条数，1 - 跟踪显示条数，2 - 督办显示条数
		 */
		private static int[] arrangeAverage(int pending, int track, int supervise, int sumNum) {
			int max;
			for(; pending + track + supervise > sumNum; ) {
				max = NumberUtils.max(pending, track, supervise);
				if(pending == max && pending + track + supervise > sumNum)
					pending --;
				
				if(track == max && pending + track + supervise > sumNum)
					track --;
				
				if(supervise == max && pending + track + supervise > sumNum)
					supervise --;
			}
			return new int[] {pending, track, supervise};
		}
	}
	
	static class AffaritCountModel implements Comparable<AffaritCountModel> {
		public static final int PENDING = 0, TRACK = 1, SUPERVISE = 2;
		public static final String PENDING_URL_PARAM = "pending", TRACK_URL_PARAM = "track", SUPERVISE_URL_PARAM = "supervise";
		/**
		 * 事件类型：待办、跟踪或督办
		 */
		private int type;
		/**
		 * 流程事项所展现的条数
		 */
		private int displayCount;
		/**
		 * 流程事项的排序号，决定三种事项出现的先后顺序
		 */
		private int sortId;
		
		/**
		 * 排序实现
		 */
		public int compareTo(AffaritCountModel target) {
			return Integer.valueOf(this.getSortId()).compareTo(Integer.valueOf(target.getSortId()));
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + displayCount;
			result = prime * result + sortId;
			result = prime * result + type;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AffaritCountModel other = (AffaritCountModel) obj;
			if (displayCount != other.displayCount)
				return false;
			if (sortId != other.sortId)
				return false;
			if (type != other.type)
				return false;
			return true;
		}

		public AffaritCountModel(int type, int displayCount, int sortId) {
			super();
			this.type = type;
			this.displayCount = displayCount;
			this.sortId = sortId;
		}

		public int getDisplayCount() {
			return displayCount;
		}
		public void setDisplayCount(int displayCount) {
			this.displayCount = displayCount;
		}
		public int getSortId() {
			return sortId;
		}
		public void setSortId(int sortId) {
			this.sortId = sortId;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	}

	public void setFormBizConfigManager(FormBizConfigManager formBizConfigManager) {
		this.formBizConfigManager = formBizConfigManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public void setFormBizConfigColumnManager(FormBizConfigColumnManager formBizConfigColumnManager) {
		this.formBizConfigColumnManager = formBizConfigColumnManager;
	}
	public boolean isAllowUserUsed(String bizConfigId) {
		if(Strings.isNotBlank(bizConfigId)){
			Long configId = NumberUtils.toLong(bizConfigId, -1l);
			if(configId == -1l){
				return false;
			}
			FormBizConfig bizConfig = this.formBizConfigManager.findById(configId);
			// 仅当业务配置存在、具备栏目挂接，且当前用户有权（创建者或在共享范围中）使用时，才予以显示
			boolean valid = bizConfig != null && bizConfig.hasColumnConfig() && formBizConfigManager.isCreatorOrInShareScope(bizConfig, CurrentUser.get().getId());
			if(valid) {
				boolean isEmpty = this.templeteManager.checkTempletes4BizConfigIsEmpty(configId);
				if(isEmpty) {
					return false;
				}
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}