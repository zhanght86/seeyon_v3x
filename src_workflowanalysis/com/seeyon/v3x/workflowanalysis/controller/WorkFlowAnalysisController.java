package com.seeyon.v3x.workflowanalysis.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.definition.BPMProcess;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.SimpleTemplete;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.report.chart.ReportChartManager;
import com.seeyon.v3x.report.chart.model.BarHChartInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.util.annotation.SetContentType;
import com.seeyon.v3x.workflowanalysis.domain.CompareModel;
import com.seeyon.v3x.workflowanalysis.domain.MemberAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.NodeAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.NodeAnalysisDetailModel;
import com.seeyon.v3x.workflowanalysis.domain.SimpleSummaryModel;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysisAcl;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisManager;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

import edu.emory.mathcs.backport.java.util.Collections;

public class WorkFlowAnalysisController extends BaseController{
	private static final Log log = LogFactory.getLog(WorkFlowAnalysisController.class);
	
	private WorkFlowAnalysisManager workFlowAnalysisManager;
	private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;
	private ReportChartManager reportChartManager;
	private FileToExcelManager fileToExcelManager;
	private AffairManager affairManager;
	private ColManager colManager;
	private EdocSummaryManager edocSummaryManager;
	

	private TempleteManager templeteManager;
	private MetadataManager metadataManager;
	private WorkTimeManager workTimeManager;
	
	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}

	public void setWorkFlowAnalysisManager(
			WorkFlowAnalysisManager workFlowAnalysisManager) {
		this.workFlowAnalysisManager = workFlowAnalysisManager;
	}
	
	public void setReportChartManager(ReportChartManager reportChartManager) {
		this.reportChartManager = reportChartManager;
	}

	/*private WorkFlowAnalysisManager workFlowAnalysisManager;
	public void setWorkFlowAnalysisManager(
			WorkFlowAnalysisManager workFlowAnalysisManager) {
		this.workFlowAnalysisManager = workFlowAnalysisManager;
	}
	*//**
	 * 综合分析页面
	 *//*
	public ModelAndView viewIPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("");
		String flowState = request.getParameter("flowState");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String templeteInfo = request.getParameter("templeteInfo");
		
		List<WorkFlowAnalysis> l = new ArrayList<WorkFlowAnalysis>();
		try{
			//l = workFlowAnalysisManager.getWorkFlowAnalysisModelList(templeteInfo, flowState, startDate, endDate);
		}catch(Exception e){
			
		}
			
		return mav;
	}*/
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView workFlowAnalysisFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("workFlowAnalysis/workFlowAnalysisFrame");
	}
	
	/**
	 * 授权列表
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView authorizationList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("workFlowAnalysis/authorizationList");
		
		List<WorkFlowAnalysisAcl> workFlowAnalysisAclList = workFlowAnalysisAclManager.getWorkFlowAnalysisAclByAccountId(user.getLoginAccount());
		
		mav.addObject("workFlowAnalysisAclList", workFlowAnalysisAclList);
		return mav;
	}
	
	/**
	 * 新建人员权限
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addAuthorization(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("workFlowAnalysis/addAuthorization");
		return mav.addObject("isModifyOrAdd", true);
	}
	
	/**
	 * 保存或修改新建权限
	 * String isModifyOrAdd = request.getParameter("isModifyOrAdd");
	 * 因为增加和修改为同一页面，此为一标志位 true 是表示为修改操作，为空时表示增加操作
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView saveOrUpdateAuthorization(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String isModifyOrAdd = request.getParameter("isModifyOrAdd");
		String authrizationId = request.getParameter("authrizationId");
		
		String memberNames = request.getParameter("memberNames");
		String memberIds = request.getParameter("memberIds");
		String templeteName = request.getParameter("templeteName");
		String templeteId = request.getParameter("templeteId");
		String type = request.getParameter("type");
		
		WorkFlowAnalysisAcl acl = null;
		boolean isUpdate = Strings.isNotBlank(isModifyOrAdd) 
							&& Boolean.parseBoolean(isModifyOrAdd) 
							&& Strings.isNotBlank(authrizationId);
		
		if (isUpdate) {
			 acl = workFlowAnalysisAclManager.get(Long.valueOf(authrizationId));
		}else{
			 acl = new WorkFlowAnalysisAcl();
			 acl.setIdIfNew();
			 acl.setCreateDate(new Date());
		}
		acl.setOrgAccountId(CurrentUser.get().getLoginAccount());
		acl.setMemberIds(memberIds);
		acl.setMemberNames(memberNames);
		acl.setUpdateDate(new Date());
		if ("all".equals(type)) {
			acl.setTempleteIds(String.valueOf(WorkFlowAnalysis.AllTemplete));
			acl.setTempleteNames("");
		}
		else {
			acl.setTempleteIds(templeteId);
			acl.setTempleteNames(templeteName);
		}
		try {
			if (isUpdate) {
				workFlowAnalysisAclManager.updateWorkFlowAnalysisAcl(acl);
			} else {
				workFlowAnalysisAclManager.saveWorkFlowAnalysisAcl(acl);
			}
		} catch (Exception e) {
			log.error("流程分析授权异常:",e);
			throw e;
		} 
		return super.redirectModelAndView("/workFlowAnalysis.do?method=authorizationList");
	}
	
	/**
	 * 根据id查询一条记录
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView queryAuthorizationById(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("workFlowAnalysis/addAuthorization");
		
		String id = request.getParameter("id") ;
		// flag 有值：查看， 空值：修改
		String view = request.getParameter("flag");
		WorkFlowAnalysisAcl acl = new WorkFlowAnalysisAcl();
		
		if (Strings.isNotBlank(id)) 
			acl = workFlowAnalysisAclManager.queryAuthorizationById(Long.parseLong(id));
		
		if (Strings.isNotBlank(view)) 
			return mav.addObject("workFlowAnalysisAcl", acl).addObject("isModifyOrAdd", false);
		else
			return mav.addObject("workFlowAnalysisAcl", acl).addObject("isModifyOrAdd", true);
	}
	
	/**
	 * 删除单条记录模板权限
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView removeAuthorization(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String[] ids = request.getParameterValues("authorizationId");
		
		if (ids.length > 0) {
			for (int i = 0 ; i < ids.length ; i ++) {
				workFlowAnalysisAclManager.removeWorkFlowAnalysisAclById(Long.parseLong(ids[i]));
			}
		}
		return super.redirectModelAndView("/workFlowAnalysis.do?method=authorizationList") ;
	}
	
	
	/**
	 * 统计  -- 选择模板流程
	 */
	public ModelAndView openTemplateDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/openTemplateDetail");
    	
    	String appType = request.getParameter("appType") ;
    	
    	User user = CurrentUser.get();
    	List<Templete> templeteList = workFlowAnalysisAclManager.getTempleteByUserId(user.getId(), appType == null ? null : Integer.parseInt(appType));
    	
    	if (templeteList != null && templeteList.size() > 0) 
    		mav.addObject("templeteList", templeteList);
    	
    	return mav;
    }
	
	/**
	 * 综合分析框架页面
	 */
	public ModelAndView comprehensiveAnalysisHome(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/comprehensiveAnalysisHome");
    	return mav;
    }
	
	/**
	 *综合分析查询结果列表
	 */
	public ModelAndView comprehensiveAnalysiszListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/comprehensiveAnalysiszList");
		List<WorkFlowAnalysis> wfa = this.queryComprehensiveAnalysiszList(request, response);
		return mav.addObject("workFlowAnalysis", wfa);
    }
	/**
	 * 根据条件查询综合分析
	 * @param atids : 当前用户能访问到的所有模板的ID。
	 * @return 返回结果集   List<WorkFlowAnalysis>
	 * @throws Exception
	 */
	public List<WorkFlowAnalysis> queryComprehensiveAnalysiszList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String appType = request.getParameter("appType");
		String templeteIds = request.getParameter("templeteId");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String templete = request.getParameter("templete");
		String exportToExcel = request.getParameter("exportToExcel");

		List<Long> tidl = new ArrayList<Long>();
		if(String.valueOf(WorkFlowAnalysis.AllTemplete).equalsIgnoreCase(templete)
				||(Strings.isBlank(templete) && Strings.isBlank(templeteIds))){
			tidl.add(WorkFlowAnalysis.AllTemplete);
		}else{
			if(Strings.isNotBlank(templeteIds)){
				String ids[] = templeteIds.split(",");
				for (String s : ids)
					tidl.add(Long.parseLong(s));
			}
		}
	
		User user = CurrentUser.get();
		List<WorkFlowAnalysis> wfa = workFlowAnalysisManager.getWorkFlowList(
				appType, 
				tidl,
				beginDate, 
				endDate, 
				user.getId(),
				user.getLoginAccount());
		
		//设置模板名字和模板ID和所属人名字。
		setTempleteInfo2WorkFlowAnalysis(wfa);
		if(exportToExcel==null || !"1".equals(exportToExcel)){
			wfa = pagenate(wfa);
		}
		return wfa ;
	}
	private void setTempleteInfo2WorkFlowAnalysis(List<WorkFlowAnalysis> wfal){
		if(Strings.isNotEmpty(wfal)){
			Map<Long,SimpleTemplete> m = templeteManager.getSystemTempleteSimpleInfo();
			for(WorkFlowAnalysis wfa : wfal){
				SimpleTemplete st =  m.get(wfa.getTempleteId());
				if(st!=null){
					String subject = st.getSubject();
					Long memberId = st.getMemberId();
					Integer sd = st.getStandardDuration();
					
					wfa.setTempleteSubject(subject);
					wfa.setTempleteMemberId(memberId);
					Integer avgRunTime = wfa.getAvgRunTime();
					if(sd!=null && avgRunTime !=null  && avgRunTime!=0){
						Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(sd),
								CurrentUser.get().getLoginAccount());
						wfa.setEfficiency(workStandarDuration/(avgRunTime*1.0));
					}else{
						wfa.setEfficiency(0.0);
					}
					wfa.setStandardTime(sd);
				}
				
			}
		}
	}
	
	/**
	 * 导出综合分析 至 excel
	 */
	public ModelAndView exportComprehensiveExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.comprehensive.analysis.label") ;
    	
    	String templeteName = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.template.process.label") ;
    	//String catagory = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.application.type.label") ;
    	String memberName = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.the.personal.label") ;
    	String caseCount = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.the.number.of.calls.label") ;
    	String useRadio = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.use.rate.label") ;
    	String avgRunTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.average.run.length.label") ;
    	String standardTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.reference.time.label") ;
    	String efficiency = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.operating.efficiency.label") ;
    	String avgOverTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.the.average.length.of.overtime.label") ;
    	String overTimeRatio = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.overtime.rate.label") ;
    	
    	String[] columnName = {templeteName,memberName,caseCount,useRadio,avgRunTime,standardTime,efficiency,avgOverTime,overTimeRatio} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	List<WorkFlowAnalysis> wfa = this.queryComprehensiveAnalysiszList(request, response);
    	Map<Long,Integer> m  = new HashMap<Long,Integer>();
    	List<Long> tids = new ArrayList<Long>();
    	for(WorkFlowAnalysis wf :wfa){
    		tids.add(wf.getTempleteId());
    	}
    	m = workFlowAnalysisManager.getTempleteWorkStandarduraion(tids, CurrentUser.get().getLoginAccount());
    	if (wfa != null && wfa.size() > 0) {
    		for (int i = 0 ; i < wfa.size() ; i ++) {
        		WorkFlowAnalysis data = wfa.get(i) ;
        		DataRow dataRow = new DataRow();
        		
        		dataRow.addDataCell(data.getTempleteSubject(), DataCell.DATA_TYPE_TEXT) ;
        		
        		dataRow.addDataCell(Functions.showMemberNameOnly(data.getTempleteMemberId()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(data.getCaseCount().toString(), DataCell.DATA_TYPE_DATE) ;
        		dataRow.addDataCell(Functions.showNumber2Percent(data.getUseRadio()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getAvgRunTime()), DataCell.DATA_TYPE_TEXT) ;
        		Integer wsd = m.get(data.getTempleteId()).intValue();
        		dataRow.addDataCell(Functions.showDateByWork(wsd), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showNumber2Percent(data.getEfficiency()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getAvgOverTime()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showNumber2Percent(data.getOverTimeRatio()), DataCell.DATA_TYPE_TEXT) ;
        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;
	}
	
	/**
	 * 综合分析图页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView comprehensiveAnalysiszChartPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return new ModelAndView("workFlowAnalysis/comprehensiveAnalysiszChart");
	}
	/**
	 * 综合分析图
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	public ModelAndView comprehensiveAnalysiszChart(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Set<String> columnKeys = new LinkedHashSet<String>(10);
		List<List<Double>> lstChartData = new ArrayList<List<Double>>(3);
		List<Double> d1 = new ArrayList<Double>();
		List<Double> d2 = new ArrayList<Double>();
		List<Double> d3 = new ArrayList<Double>();
		lstChartData.add(d1);
		lstChartData.add(d2);
		lstChartData.add(d3);
		
		List<WorkFlowAnalysis> wfa = this.queryComprehensiveAnalysiszList(request, response);
		int count = 0;
		//控制图标只显示10条数据。由于图标的查询语句中使用了group by ，所以不能在后台直接获取指定条数
		//TODO 优化
		for (WorkFlowAnalysis workFlowAnalysis : wfa) {
			if(count>=10) break;
			count++;
			d1.add(workFlowAnalysis.getUseRadio());
			d2.add(workFlowAnalysis.getEfficiency());
			d3.add(workFlowAnalysis.getOverTimeRatio());
			
			columnKeys.add(workFlowAnalysis.getTempleteSubject());
		}
		
		Set<String> rowKeys = new LinkedHashSet<String>(3);
		rowKeys.add(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.use.rate.label"));
		rowKeys.add(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.efficiency.label"));
		rowKeys.add(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.overtime.rate.label"));
		
		BarHChartInfo chart = new BarHChartInfo();
		chart.setLstChartData(lstChartData);
		chart.setRowKeys(rowKeys);
		chart.setColumnKeys(columnKeys);
		chart.setChartHeight(Integer.parseInt(request.getParameter("height"))-12);//首页显示不全问题
		chart.setChartWidth(Integer.parseInt(request.getParameter("width")));
		chart.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		
		this.reportChartManager.createChartImage(chart, response, false);
		
		return null;
	}
	
	/**
	 * 效率分析框架页面
	 */
	public ModelAndView efficiencyAnalysisHome(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("workFlowAnalysis/efficiencyAnalysisHome");
    	mav.addObject("finish", Constant.flowState.finish.ordinal());
		mav.addObject("terminate", Constant.flowState.terminate.ordinal());
    	return mav;
    }
	
	/**
	 *效率分析查询结果列表
	 */
	public ModelAndView efficiencyAnalysiszListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	
		ModelAndView mav = new ModelAndView("workFlowAnalysis/efficiencyAnalysiszList");
		String templeteId = request.getParameter("templeteId");
		
		if(Strings.isBlank(templeteId))
			return mav;
		
		//基准时长
		Templete t = null;
		if (Strings.isNotBlank(templeteId)){
			t = templeteManager.get(Long.valueOf(templeteId));
			if(t!=null){
				mav.addObject("standarduration",t.getStandardDuration());
			}
		}
		//平均时长
		Integer avgRunWorkTime = getAvgRunWorkTime(request,t);
		mav.addObject("avgRunWorkTime", avgRunWorkTime);
		
		//低于基准的百分比
		Double radio = getRadioltStandarduration(request,t,null);
		mav.addObject("gtSDRadio", radio);
		
		return mav.addObject("simpleSummaryModel", this.queryEfficiencyAnalysiszList(request, response,true));
    }
	
	private Double getRadioltStandarduration(HttpServletRequest request,Templete t,Long deadline) {
		User user = CurrentUser.get();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String templeteId = request.getParameter("templeteId");
		List<Integer> sl = new ArrayList<Integer>();
		sl.add(Constant.flowState.finish.ordinal());
		sl.add(Constant.flowState.terminate.ordinal());
		if(Strings.isBlank(beginDate) || Strings.isBlank(endDate) || Strings.isBlank(templeteId)){
			return 0.0;
		}
		double radio =0.0;
		//模板总的实例数
		Integer allCaseCount = 0;
		Integer gtSDCaseCount = 0;
		Integer standardDuration = t.getStandardDuration() == null ? 0: t.getStandardDuration();
		Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(standardDuration), t.getOrgAccountId());
		if(TempleteCategory.TYPE.collaboration_templete.ordinal() == t.getCategoryType().intValue()
				||TempleteCategory.TYPE.form.ordinal() == t.getCategoryType().intValue()){
			//模板总的实例数
			allCaseCount = colManager.getCaseCountByTempleteId(
					user.getLoginAccount(),
					Long.valueOf(templeteId), sl,Datetimes.parse(beginDate), Datetimes.parse(endDate));
			//处理时长大于基准时长的实例数
			gtSDCaseCount =  colManager.getCaseCountGTSD(
					user.getLoginAccount(),
					Long.valueOf(templeteId), 
					sl,
					Datetimes.parse(beginDate),
					Datetimes.parse(endDate),
					deadline == null ?  workStandarDuration.intValue():deadline.intValue());
			
		}else{
			allCaseCount = edocSummaryManager.getCaseCountByTempleteId(
					user.getLoginAccount(),
					Long.valueOf(templeteId), sl,Datetimes.parse(beginDate), Datetimes.parse(endDate));
			//处理时长大于基准时长的实例数
			gtSDCaseCount =  edocSummaryManager.getCaseCountGTSD(
					user.getLoginAccount(),
					Long.valueOf(templeteId), 
					sl,
					Datetimes.parse(beginDate),
					Datetimes.parse(endDate),
					deadline == null ?  workStandarDuration.intValue():deadline.intValue());
		}
		
		if(allCaseCount!=null && allCaseCount!=0){
			radio = gtSDCaseCount/(allCaseCount*1.0);
		}
		return radio;
	}
	
	private Double getOverCaseRatio(HttpServletRequest request,Templete t) {
		User user = CurrentUser.get();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String templeteId = request.getParameter("templeteId");
		String states[] = request.getParameterValues("flowstate");
		List<Integer> sl = new ArrayList<Integer>();
		if(states!=null){
			for (String s : states) {
				sl.add(Integer.parseInt(s));
			}
		}
		
		if(Strings.isBlank(beginDate) || Strings.isBlank(endDate) || Strings.isBlank(templeteId)){
			return 0.0;
		}
		double radio =0.0;
		if(TempleteCategory.TYPE.collaboration_templete.ordinal() == t.getCategoryType().intValue()
				||TempleteCategory.TYPE.form.ordinal() == t.getCategoryType().intValue()){
			radio =  colManager.getOverCaseRatioByTempleteId(
					user.getLoginAccount(),
					Long.valueOf(templeteId), 
					sl,
					Datetimes.getTodayFirstTime(beginDate),
					Datetimes.getTodayLastTime(endDate));
			
		}else{
			radio =  edocSummaryManager.getOverCaseRatioByTempleteId(
					user.getLoginAccount(),
					Long.valueOf(templeteId), 
					sl,
					Datetimes.getTodayFirstTime(beginDate),
					Datetimes.getTodayLastTime(endDate));
		}
		
		return radio;
	}
	/**
	 * 效率分析和超期分析都用这个方法,修改时候注意
	 * @param request
	 * @param t
	 * @return
	 */
	private Integer getAvgRunWorkTime(HttpServletRequest request,Templete t){
		try{
			User user =CurrentUser.get();
			String beginDate = request.getParameter("beginDate");
			String endDate = request.getParameter("endDate");
			String templeteId = request.getParameter("templeteId");
			String states[] = request.getParameterValues("flowstate");
			List<Integer> sl = new ArrayList<Integer>();
			
			if(states!=null){
				for (String s : states) {
					sl.add(Integer.parseInt(s));
				}
			}
			
			if(Strings.isBlank(beginDate) || Strings.isBlank(endDate) || Strings.isBlank(templeteId)){
				return 0;
			}
			if(TempleteCategory.TYPE.collaboration_templete.ordinal() == t.getCategoryType().intValue()
					||TempleteCategory.TYPE.form.ordinal() == t.getCategoryType().intValue()){
				return colManager.getAvgRunWorkTimeByTempleteId(
						user.getLoginAccount(),
						Long.valueOf(templeteId), 
						sl, 
						Datetimes.getTodayFirstTime(beginDate), 
						Datetimes.getTodayLastTime(endDate));
			}else{
				return edocSummaryManager.getAvgRunWorkTimeByTempleteId(
						user.getLoginAccount(),
						Long.valueOf(templeteId), 
						sl, 
						Datetimes.getTodayFirstTime(beginDate), 
						Datetimes.getTodayLastTime(endDate));
			}
		} catch(Exception e){
			log.error("根据模板计算基准处理时间出错",e);
			return 0;
		}
	}
	/**
	 * 根据条件查询效率分析
	 * @param isPaging 是否分页
	 * @return 返回结果集   List<SimpleSummaryModel>
	 * @throws Exception
	 */
	public List<SimpleSummaryModel> queryEfficiencyAnalysiszList(HttpServletRequest request,HttpServletResponse response, boolean isPaging) throws Exception {
		
		List<SimpleSummaryModel> ssmList = new ArrayList<SimpleSummaryModel>();
		User user = CurrentUser.get();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String templeteId = request.getParameter("templeteId");
		
		// 9091915311927086785
		if (Strings.isNotBlank(templeteId) && Strings.isNotBlank(beginDate) && Strings.isNotBlank(endDate)) {
			ssmList = workFlowAnalysisManager.getEfficiencyAnalysis(
					user.getLoginAccount(),
					Long.parseLong(templeteId), Datetimes.getTodayFirstTime(beginDate), Datetimes.getTodayLastTime(endDate),isPaging);
		} 
		Collections.sort(ssmList);
		return ssmList ;
	}
	
	/**
	 * 导出效率分析 至 excel
	 */
	public ModelAndView exportEfficiencyExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.efficiency.analysis.label") ;
    	
    	String subject = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.process.header.label") ;
    	String runTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.running.time.label") ;
    	String efficiency = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.operating.efficiency.label") ;
    	
    	String[] columnName = {subject,runTime,efficiency} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	//查询结果不分页
    	List<SimpleSummaryModel> ssmList = this.queryEfficiencyAnalysiszList(request, response,false);
    	//基准时长
		Templete t = null;
		long sd = 0l;
		String templeteId = request.getParameter("templeteId");
		if (Strings.isNotBlank(templeteId)){
			t = templeteManager.get(Long.valueOf(templeteId));
			if(t!=null){
				sd = t.getStandardDuration()==null ? 0L:t.getStandardDuration();
			}
		}
    	if (ssmList != null && ssmList.size() > 0) {
    		for (int i = 0 ; i < ssmList.size() ; i ++) {
        		SimpleSummaryModel data = ssmList.get(i) ;
        		DataRow dataRow = new DataRow();
        		
        		dataRow.addDataCell(data.getSubject(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getRunWorkTime() == null ? 0 : Integer.parseInt(data.getRunWorkTime().toString())), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(sd == 0 ? " －  " : Functions.showNumber2Percent(data.getEfficiency()), DataCell.DATA_TYPE_DATE) ;
        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;
	}
	
	/**
	 *改进分析框架页面
	 */
	public ModelAndView improvementAnalysisHome(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("workFlowAnalysis/improvementAnalysisHome");
    	return mav;
    }
	
	/**
	 *改进分析查询结果列表
	 */
	public ModelAndView improvementAnalysiszListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{

		ModelAndView mav = new ModelAndView("workFlowAnalysis/improvementAnalysiszList");
    	List<CompareModel> compareModelList = this.queryImprovementAnalysiszList(request, response);
		
		if (compareModelList != null && compareModelList.size()>0) {
			mav.addObject("compareModel1", compareModelList.get(0));
			mav.addObject("compareModel2", compareModelList.get(1));
			mav.addObject("compareModelList", compareModelList);
		}
    	return mav;
    }
	
	/**
	 * 根据条件查询该改进分析
	 * @return 返回结果集   List<CompareModel>
	 * @throws Exception
	 */
	public List<CompareModel> queryImprovementAnalysiszList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<CompareModel> compareModelList = new ArrayList<CompareModel>();
		
		String beginDate1 = request.getParameter("beginDate1");
		String beginDate2 = request.getParameter("beginDate2");
		String endDate1 = request.getParameter("endDate1");
		String endDate2 = request.getParameter("endDate2");
		String templeteId = request.getParameter("templeteId");
		
		// 9091915311927086785
		if (Strings.isNotBlank(templeteId) && Strings.isNotBlank(beginDate1) && Strings.isNotBlank(endDate1)
				&& Strings.isNotBlank(beginDate2) && Strings.isNotBlank(endDate2)) {
			
			Date startDate1 = Datetimes.getTodayFirstTime(beginDate1);
			Date endDat1 = Datetimes.getTodayLastTime(endDate1);
			Date startDate2 = Datetimes.getTodayFirstTime(beginDate2);
			Date endDat2 = Datetimes.getTodayLastTime(endDate2);
			
			CompareModel compareModel1 = workFlowAnalysisManager.getCompareAnalysis(Long.parseLong(templeteId), startDate1, endDat1);
			CompareModel compareModel2 = workFlowAnalysisManager.getCompareAnalysis(Long.parseLong(templeteId), startDate2, endDat2);
			compareModelList.add(compareModel1);
			compareModelList.add(compareModel2);
		} 
		return compareModelList;
	}
	
	/**
	 * 导出改进分析 至 excel
	 */
	public ModelAndView exportImprovementExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.improvement.analysis.label") ;
    	
    	String contrastRange = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.contrast.range.table.label") ;
    	String averageRunLength = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.average.run.length.label") ;
    	String longestTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.the.longest.time.label") ;
    	String shortestPeriod = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.the.shortest.period.label") ;
    	String referenceTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.reference.time.label") ;
    	String efficiency = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.operating.efficiency.label") ;
    	
    	String[] columnName = {contrastRange,averageRunLength,longestTime,shortestPeriod,referenceTime,efficiency} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	List<CompareModel> compareModelList = this.queryImprovementAnalysiszList(request, response);
    	
    	if (compareModelList != null && compareModelList.size() > 0) {
    		if (compareModelList.get(0) != null && compareModelList.get(1) != null) {
    			Long templeteId = compareModelList.get(0).getTempleteId();
    			Integer standarduration = 0;
    			if (templeteId != null)
    				standarduration = templeteManager.get(templeteId).getStandardDuration();
    			standarduration = standarduration== null? 0:standarduration;
    			Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(standarduration),CurrentUser.get().getLoginAccount());
    			for (int i = 0 ; i < compareModelList.size() ; i ++) {
        			CompareModel data = compareModelList.get(i) ;
            		DataRow dataRow = new DataRow();
            		// 区间一 、区间二
            		if (i == 0) {
            			dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.section.a.label"), DataCell.DATA_TYPE_TEXT) ;
            		}
            		if (i == 1) {
            			dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.interval.two.label"), DataCell.DATA_TYPE_TEXT) ;
            		}
            		dataRow.addDataCell(Functions.showDateByWork(data.getAvgRunTime() == null ? 0 : Integer.parseInt(data.getAvgRunTime().toString())), DataCell.DATA_TYPE_TEXT) ;
            		dataRow.addDataCell(Functions.showDateByWork(data.getMaxRunTime() == null ? 0 : Integer.parseInt(data.getMaxRunTime().toString())), DataCell.DATA_TYPE_TEXT) ;
            		dataRow.addDataCell(Functions.showDateByWork(data.getMinRunTime() == null ? 0 : Integer.parseInt(data.getMinRunTime().toString())), DataCell.DATA_TYPE_TEXT) ;
            		dataRow.addDataCell(Functions.showDateByWork(data.getStandarduaration() == null ? 0 : workStandarDuration.intValue()), DataCell.DATA_TYPE_TEXT) ;
            		dataRow.addDataCell(Functions.showNumber2Percent(data.getEfficiency()), DataCell.DATA_TYPE_DATE) ;
            		dataRecord.addDataRow(dataRow);
            	}
    		}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;
	}
	
	/**
	 *节点分析框架页面
	 */
	public ModelAndView nodeAnalysisHome(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return new ModelAndView("workFlowAnalysis/nodeAnalysisHome");
    }
	
	/**
	 *节点分析查询结果列表
	 */
	public ModelAndView nodeAnalysiszListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/nodeAnalysiszList");
		List<NodeAnalysis> naList = pagenate(this.queryNodeAnalysiszList(request, response));
		return mav.addObject("nodeAnalysis", naList);
    }
	
	/**
	 * 根据条件查询节点分析
	 * @return 返回结果集   List<NodeAnalysis>
	 * @throws Exception
	 */
	public List<NodeAnalysis> queryNodeAnalysiszList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<NodeAnalysis> naList = new ArrayList<NodeAnalysis>();
		User user = CurrentUser.get();
		
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String templeteId = request.getParameter("templeteId");
		
		List<Integer> states = new ArrayList<Integer>();
		states.add(StateEnum.col_done.key());
		
		boolean isCol = false;
		Templete t = null;
		if(Strings.isNotBlank(templeteId)){
			t = templeteManager.get(Long.valueOf(templeteId));
			if(isColOrFormTemplete(t.getCategoryType())){
				isCol = true;
			}
		}
		if (Strings.isNotBlank(templeteId) 
				&& Strings.isNotBlank(beginDate) 
				&& Strings.isNotBlank(endDate)) {
			
			Date startDate = Datetimes.getTodayFirstTime(beginDate);
			Date endDat = Datetimes.getTodayLastTime(endDate);
			
			naList = workFlowAnalysisManager.getNodeAnalysisiList(
					t,
					user.getLoginAccount(),
		    		isCol,
					states,
					startDate,
					endDat);
		} 
		return naList ;
	}
	
	/**
	 * 导出节点分析 至 excel
	 */
	public ModelAndView exportNodeExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.node.analysis.label") ;
    	
    	String access = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.node.access.label") ;
    	String name = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.node.name.label") ;
    	String overtimeRate = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.overtime.rate.label") ;
    	String averageHandlingTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.average.handling.time.label") ;
    	
    	String[] columnName = {access,name,overtimeRate,averageHandlingTime} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	List<NodeAnalysis> naList = this.queryNodeAnalysiszList(request, response);
    	
    	if (naList != null && naList.size() > 0) {
    		for (int i = 0 ; i < naList.size() ; i ++) {
        		NodeAnalysis data = naList.get(i) ;
        		DataRow dataRow = new DataRow();
        		
        		dataRow.addDataCell(data.getPolicyName(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(data.getName(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showNumber2Percent(data.getOverRadio()), DataCell.DATA_TYPE_DATE) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getAvgRunWorkTime() == null ? 0 : Integer.parseInt(data.getAvgRunWorkTime().toString())), DataCell.DATA_TYPE_DATE) ;
        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;
	}
	
	/**
	 *节点分析-节点权限详细信息框架
	 */
	public ModelAndView nodeAnalysiszNodeAccessFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/nodeAnalysiszNodeAccessFrame");
		return mav ;
	}
	
	/**
	 *节点分析-节点权限详细信息
	 */
	public ModelAndView nodeAnalysiszNodeAccessDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView mav = new ModelAndView("workFlowAnalysis/nodeAnalysiszNodeAccessDetail");
		
		String policyName = request.getParameter("policyName");
		String memberName = request.getParameter("memberName");
		String overRadio = request.getParameter("overRadio");
		String avgRunWorkTime = request.getParameter("avgRunWorkTime");
		
		List<NodeAnalysisDetailModel>  l= this.queryNodeAccessDetailList(request, response);
		
		mav.addObject("affairList", l);
		mav.addObject("policyName", policyName);
		mav.addObject("memberName", memberName);
		mav.addObject("overRadio", Strings.isBlank(overRadio)?0.0:Double.valueOf(overRadio));
		mav.addObject("avgRunWorkTime", avgRunWorkTime);
		
		return mav ;
	}
	
	/**
	 * 根据条件查询节点分析-节点权限详细情况
	 * @return 返回结果集   List<Affair>
	 * @throws Exception
	 */
	public List<NodeAnalysisDetailModel> queryNodeAccessDetailList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<Affair> affairList = new ArrayList<Affair>();
		User user = CurrentUser.get();
		String templeteId = request.getParameter("templeteId");
		String nodeId = request.getParameter("nodeId");
		String startDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		
		List<Integer> states = new ArrayList<Integer>();
		states.add(StateEnum.col_done.key());
		boolean isCol = false;
		if(Strings.isNotBlank(templeteId)){
			Templete t = templeteManager.get(Long.valueOf(templeteId));
			if(isColOrFormTemplete(t.getCategoryType())){
				isCol = true;
			}
		}
		if (Strings.isNotBlank(templeteId) 
				&& Strings.isNotBlank(nodeId) 
				&& Strings.isNotBlank(startDate) 
				&& Strings.isNotBlank(endDate)) {
			
			affairList = workFlowAnalysisManager.getAffairByActivityId(
					Long.parseLong(templeteId),
					user.getLoginAccount(),
		    		isCol,
					states,
					Long.parseLong(nodeId), 
					Datetimes.getTodayFirstTime(startDate), 
					Datetimes.getTodayLastTime(endDate));
		}
		return pagenate(converAffairList2NodeAnalysisDetailModel(affairList));
	}
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if(first>list.size()){
			first=0;
		}
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	public List<NodeAnalysisDetailModel> converAffairList2NodeAnalysisDetailModel(List<Affair> affairs){
		Map<Long,NodeAnalysisDetailModel> m = new HashMap<Long,NodeAnalysisDetailModel>();
		NodeAnalysisDetailModel dm = null;
		for(Affair affair: affairs){
			Long summaryId = affair.getObjectId();
			if(m.get(summaryId)!= null){
				dm = m.get(summaryId);
				dm.setMemberNames(dm.getMemberNames()+","+Functions.showMemberNameOnly(affair.getMemberId()));
				long at = affair.getRunWorkTime()== null ? 0:affair.getRunWorkTime().longValue();
				long dt = dm.getRunWorkTime() == null ? 0 : dm.getRunWorkTime().longValue();
				if(at>dt){
					dm.setRunWorkTime(affair.getRunWorkTime());
					dm.setOverWorkTime(affair.getOverWorkTime());
				}
			}else{
				dm = new NodeAnalysisDetailModel();
				dm.setSubject(affair.getSubject());
				dm.setMemberNames(Functions.showMemberNameOnly(affair.getMemberId()));
				dm.setDeadLine(affair.getDeadlineDate());
				dm.setRunWorkTime(affair.getRunWorkTime());
				dm.setOverWorkTime(affair.getOverWorkTime());
				m.put(summaryId, dm);
			}
		}
		return  new ArrayList<NodeAnalysisDetailModel>(m.values());
	}
	
	/**
	 * 导出节点分析-节点权限详细情况
	 */
	public ModelAndView exportNodeAccessDetailExcel(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.node.analysis.label") ;
    	
    	String subject = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.process.header.label") ;
    	String dealPeople = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.deal.people.label") ;
    	String handlingTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.handling.time.label") ;
    	String timeouts = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.timeouts.label") ;
    	String deadlinelabel = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.processing.period.label") ;
    	
    	String[] columnName = {subject,dealPeople,handlingTime,deadlinelabel,timeouts} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	List<NodeAnalysisDetailModel>  l= this.queryNodeAccessDetailList(request, response);
    	
    	if (l != null && l.size() > 0) {
    		for (int i = 0 ; i < l.size() ; i ++) {
    			NodeAnalysisDetailModel data = l.get(i) ;
        		DataRow dataRow = new DataRow();
        		dataRow.addDataCell(data.getSubject(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(data.getMemberNames(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getRunWorkTime() == null ? 0 : Integer.parseInt(data.getRunWorkTime().toString())), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByNature(data.getDeadLine() == null ? 0 : Integer.parseInt(data.getDeadLine().toString())), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getOverWorkTime() == null ? 0 : Integer.parseInt(data.getOverWorkTime().toString())), DataCell.DATA_TYPE_TEXT) ;
        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
		
		return null ;
	}
	
	/**
	 *节点分析-节点名称详细信息框架页面
	 */
	public ModelAndView nodeAnalysiszNodeNameFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/nodeAnalysiszNodeNameFrame");
		return mav ;
	}
	
	/**
	 *节点分析-节点名称详细信息
	 */
	public ModelAndView nodeAnalysiszNodeNameDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView mav = new ModelAndView("workFlowAnalysis/nodeAnalysiszNodeNameDetail");
		
		String memberName = request.getParameter("memberName");
		List<MemberAnalysis> memberList = this.queryNodeNameDetailList(request, response);
		
		if (memberList!=null)
			memberList=pagenate(memberList);
		mav.addObject("memberList", memberList);
		mav.addObject("memberName", memberName);
		
		return mav ;
	}
	
	/**
	 * 根据条件查询节点分析-节点名称详细情况
	 * @return 返回结果集   List<Affair>
	 * @throws Exception
	 */
	public List<MemberAnalysis> queryNodeNameDetailList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<MemberAnalysis> memberList = null;
		User user = CurrentUser.get();
		String templeteId = request.getParameter("templeteId");
		String nodeId = request.getParameter("nodeId");
		String startDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		
		List<Integer> states = new ArrayList<Integer>();
		states.add(StateEnum.col_done.key());
		
		boolean isCol = false;
		if(Strings.isNotBlank(templeteId)){
			Templete t = templeteManager.get(Long.valueOf(templeteId));
			if(isColOrFormTemplete(t.getCategoryType())){
				isCol = true;
			}
		}
		if (Strings.isNotBlank(templeteId) && Strings.isNotBlank(nodeId) && Strings.isNotBlank(startDate) && Strings.isNotBlank(endDate)) {
			memberList = workFlowAnalysisManager.getMemberAnalysis(
					Long.parseLong(templeteId), 
					user.getLoginAccount(),
		    		isCol,
					states,
					Long.parseLong(nodeId), 
					Datetimes.getTodayFirstTime(startDate), 
					Datetimes.getTodayLastTime(endDate));
		}
		return memberList ;
	}
	
	/**
	 * 导出节点分析-节点名称详细情况
	 */
	public ModelAndView exportNodeNameDetailExcel(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.node.analysis.label") ;
    	
    	String dealPeople = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.deal.people.label") ;
    	String count = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.processing.times.label") ;
    	String overtimeRate = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.overtime.rate.label") ;
    	String ahTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.average.handling.time.label") ;
    	
    	String[] columnName = {dealPeople,count,overtimeRate,ahTime} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	List<MemberAnalysis> memberList = this.queryNodeNameDetailList(request, response);
    	
    	if (memberList != null && memberList.size() > 0) {
    		for (int i = 0 ; i < memberList.size() ; i ++) {
    			MemberAnalysis data = memberList.get(i) ;
        		DataRow dataRow = new DataRow();
        		dataRow.addDataCell(Functions.showMemberName(data.getMemberId()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(data.getCount() == null ? "0" : data.getCount().toString(), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showNumber2Percent(data.getOverRadio()), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showDateByWork(data.getAvgRunTime() == null ? 0 : Integer.parseInt(data.getAvgRunTime().toString())), DataCell.DATA_TYPE_TEXT) ;
        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
		
		return null ;
	}
	
	/**
	 * 查看模板流程
	 */
	public ModelAndView viewWorkflow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = null;
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));

		Templete templete = this.templeteManager.get(templeteId);
		Object object = XMLCoder.decoder(templete.getSummary());
		
		int categoryType = templete.getCategoryType();
		if (TempleteCategory.TYPE.edoc.ordinal() == categoryType
				|| TempleteCategory.TYPE.edoc_send.ordinal() == categoryType
				|| TempleteCategory.TYPE.edoc_rec.ordinal() == categoryType
				|| TempleteCategory.TYPE.sginReport.ordinal() == categoryType) {
			EdocSummary summary = (EdocSummary) object; 
			modelAndView = new ModelAndView("edoc/templete/systemWorkflow");
			modelAndView.addObject("summary", summary);
		} else {
			ColSummary summary = (ColSummary) object;
			modelAndView = new ModelAndView("collaboration/templete/systemWorkflow");
			modelAndView.addObject("summary", summary);
		}
		
		if(!templete.getType().equals("text")){
			BPMProcess process = BPMProcess.fromXML(templete.getWorkflow()); //重新生成，因为要取新的节点名称

			String caseProcessXML = process.toXML();
	        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);

			modelAndView.addObject("hasDiagram", "true");
			modelAndView.addObject("caseProcessXML", caseProcessXML);
			modelAndView.addObject("branchs", this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal()));
		}
		
        Metadata comRemindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		Metadata comDeadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
		modelAndView.addObject("comRemindMetadata", comRemindMetadata);
		modelAndView.addObject("comMetadata", comDeadlineMetadata);
		modelAndView.addObject("type", templete.getType());
		modelAndView.addObject("isShowButton", false);
		
		// 页面标志
		modelAndView.addObject("pageFlag", "workflowAnalysisz");
		return modelAndView;
	}
	
	/**
	 *超时分析框架页面
	 */
	public ModelAndView timeoutAnalysisHome(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("workFlowAnalysis/timeoutAnalysisHome");
		mav.addObject("finish", Constant.flowState.finish.ordinal());
		mav.addObject("run", Constant.flowState.run.ordinal());
		mav.addObject("terminate", Constant.flowState.terminate.ordinal());
		return mav;
	}
	
	/**
	 *超时分析查询结果列表
	 */
	public ModelAndView timeoutAnalysiszListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView mav = new ModelAndView("workFlowAnalysis/timeoutAnalysiszList");
		String templeteId = request.getParameter("templeteId");
		
		List<SimpleSummaryModel> ssmList = this.queryTimeoutAnalysiszList(request, response);
		
		mav.addObject("simpleSummaryModel", ssmList);
		
		Templete t = templeteManager.get(Strings.isBlank(templeteId) ? 0L :Long.valueOf(templeteId));
		
		
		Integer avgRunWorkTime = getAvgRunWorkTime(request,t);
		mav.addObject("avgRunWorkTime", avgRunWorkTime);
		
		
		Double radio = getOverCaseRatio(request,t);
		mav.addObject("radio", radio);
		return mav;
	}
	
	private boolean isColOrFormTemplete(Integer categoryType){
		if(TempleteCategory.TYPE.collaboration_templete.ordinal() == categoryType.intValue()
				||TempleteCategory.TYPE.form.ordinal() == categoryType.intValue()){
			return true;
		}
		return false;
	}
	private Long getTempleteDeadLine(Templete t){
		if(t == null){
			return 0L;
		}
		Long deadLine = 0L;
		if(isColOrFormTemplete(t.getCategoryType())){
			ColSummary summary = (ColSummary) XMLCoder.decoder(t.getSummary());
			deadLine = summary.getDeadline();
		}else{
			EdocSummary summary = (EdocSummary) XMLCoder.decoder(t.getSummary());
			deadLine = summary.getDeadline();
		}
		return deadLine == null ? 0L: deadLine;
	}
	private Long getTempleteWorkDeadLine(Templete t){
		if(t == null){
			return 0L;
		}
		Long deadLine = getTempleteDeadLine(t);
		if(deadLine == null || deadLine == 0L )
			return deadLine;
		Long workDeadLine = workTimeManager.convert2WorkTime(Long.valueOf(deadLine), t.getOrgAccountId());
		return workDeadLine;
	}
	/**
	 * 根据条件查询超时分析
	 * @return 返回结果集   List<SimpleSummaryModel>
	 * @throws Exception
	 */
	public List<SimpleSummaryModel> queryTimeoutAnalysiszList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		List<SimpleSummaryModel> ssmList = new ArrayList<SimpleSummaryModel>();
		
		String templeteId = request.getParameter("templeteId");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String states[] = request.getParameterValues("flowstate");
		User user = CurrentUser.get();
		
		if (Strings.isNotBlank(templeteId) && Strings.isNotBlank(beginDate) && Strings.isNotBlank(endDate) && states !=null && states.length > 0) {
			List<Integer> stateIntList = new ArrayList<Integer>();
			for (String s : states) {
				stateIntList.add(Integer.parseInt(s));
			}
			Date startDate = Datetimes.getTodayFirstTime(beginDate);
			Date endDat = Datetimes.getTodayLastTime(endDate);
			ssmList = workFlowAnalysisManager.getOverTimeAnalysis(
					user.getLoginAccount(),
					Long.parseLong(templeteId), 
					startDate, endDat, stateIntList,true);
		}
		return ssmList;
	}
	
	/**
	 * 导出超时分析 至 excel
	 */
	public ModelAndView exportTimeoutExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String detail = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.timeout.analysis.label") ;
    	
    	String subject = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.process.header.label") ;
    	String runTime = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.running.time.label") ;
    	String timeouts = ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.timeouts.label") ;
    	String cycle = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "process.cycle.label") ;
    	
    	String[] columnName = {subject,runTime,timeouts,cycle} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;
    	Pagination.setNeedCount(false);
    	Pagination.setFirstResult(-1);
    	Pagination.setMaxResults(-1);
    	List<SimpleSummaryModel> ssmList = this.queryTimeoutAnalysiszList(request, response);
    	
    	if (ssmList != null) {
	    	for (int i = 0 ; i < ssmList.size() ; i ++) {
	    		SimpleSummaryModel data = ssmList.get(i) ;
	    		DataRow dataRow = new DataRow();
	    		
	    		dataRow.addDataCell(data.getSubject(), DataCell.DATA_TYPE_TEXT) ;
	    		dataRow.addDataCell(data.getRunWorkTime() == null ? "0" : Functions.showDateByWork(Integer.parseInt(data.getRunWorkTime().toString())), DataCell.DATA_TYPE_TEXT) ;
	    		dataRow.addDataCell(Functions.showDateByWork(data.getOverWorkTime() == null ? 0 : data.getOverWorkTime().intValue()), DataCell.DATA_TYPE_DATE) ;
	    		dataRow.addDataCell(data.getDeadline() == null ? Functions.showDateByNature(0) : Functions.showDateByNature(Integer.parseInt(data.getDeadline().toString())), DataCell.DATA_TYPE_TEXT);
	    		dataRecord.addDataRow(dataRow);
	    	}
    	}
    	
    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;
	}
	
	/**
	 * 帮助信息
	 * 
	 */
	public ModelAndView showHelpDescription(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("workFlowAnalysis/workFlowAnalysisHelp");
		String from = request.getParameter("from");
		String description = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources","common.wfanalysis.help."+from+".description"));
		view.addObject("description", description);
		if("flow".equals(from)){
			view.addObject("from", "common."+from+".statistics.label"); 
		}else{ 
			view.addObject("from", "common."+from+".analysis.label");
		}
		
		return view;
	}
	
	/**
	 *************************************  工具方法    ************************************
	 */
	
	public int getNowYear() {
		Date date = new Date();
		return date.getYear()+1900;
	}
	
	public int getNowMonth() {
		Date date = new Date();
		return date.getMonth()+1;
	}
	
	public int getLastMonth() {
		Date date = new Date();
		return date.getMonth();
	}
}
