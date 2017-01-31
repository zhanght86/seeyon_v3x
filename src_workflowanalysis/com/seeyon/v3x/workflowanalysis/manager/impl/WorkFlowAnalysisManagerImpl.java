package com.seeyon.v3x.workflowanalysis.manager.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflowanalysis.dao.WorkFlowAnalysisDao;
import com.seeyon.v3x.workflowanalysis.domain.CompareModel;
import com.seeyon.v3x.workflowanalysis.domain.MemberAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.NodeAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.SimpleSummaryModel;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisManager;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public class WorkFlowAnalysisManagerImpl implements WorkFlowAnalysisManager{
	
	private static final Log log = LogFactory.getLog(WorkFlowAnalysisManagerImpl.class);

	private AffairManager affairManager;
	private TempleteManager templeteManager;
	private EdocSummaryManager edocSummaryManager;
	private ColManager colManager;
	private WorkFlowAnalysisDao workFlowAnalysisDao;
	private WorkTimeManager workTimeManager;
	private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;
	
	
	public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}
	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	@Override
	public List<WorkFlowAnalysis> getWorkFlowAnalysisModelList(
			List<Long> templeteIds,
			Integer  startYear,Integer startMonth,
			Integer  endYear,Integer endMonth,
			Integer category,
			Long orgAccountId)throws Exception {
		
		return workFlowAnalysisDao.getWorkFlowAnalysisList(
				templeteIds, 
				startYear, 
				startMonth, 
				endYear, 
				endMonth, 
				category, 
				orgAccountId);
	}
	
	@Override
	public void doWorkFlowAnalysis() {
		try{
			log.info("开始执行流程效率分析定时调度任务。。。。");
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			//计算上一月，所以month就应该比实际值少1。
			boolean isCount  = workFlowAnalysisDao.isCount(year, month);
			//如果没有计算开始计算。
			if(!isCount){
				log.info("开始计算并插入流程效率分析数据。。。。。。");
				//上月第一天
				Date startDate = getFirstDayLastMonth();
				//上月最后一天
				Date endDate = getLastDayLastMonth();
				
				//日期：如果日期是3.5升级之前的日期则不统计,如果是升级当月的，则只统计升级那天到月底的数据。
			    String installDateStr = Functions.getProductInstallDate4WF(); 
			    Date installDate = Datetimes.parse(installDateStr);
			  
			    //升级之前
			    if(Datetimes.getLastDayInMonth(endDate).before(installDate))
			    	return ;
			    
			    //升级当月
			    if(startDate.before(installDate)){
			    	startDate = installDate;
			    }
				
				//分析协同表
				List<WorkFlowAnalysis> wfal = 
					workFlowAnalysisDao.createWorkFlowAnalysis(startDate , endDate , ApplicationCategoryEnum.collaboration.key());
				//分析公文表
				wfal.addAll(workFlowAnalysisDao.createWorkFlowAnalysis(startDate , endDate , ApplicationCategoryEnum.edoc.key()));
				
				//计算超时率，效率
				setRadioValue(wfal,startDate,endDate);
				
				//保存到数据库
				workFlowAnalysisDao.saveAll(wfal);
			}
		}catch(Exception e){
			log.error("定时调度流程分析出错。",e);
		}
	}
	private Map<String,Integer> getOverCaseCount(Date startDate,Date endDate){
		Map<String,Integer> m = workFlowAnalysisDao.getOverCaseCountByApp(startDate,endDate,  ApplicationCategoryEnum.collaboration.key());
		Map<String,Integer> em = workFlowAnalysisDao.getOverCaseCountByApp( startDate,endDate, ApplicationCategoryEnum.edoc.key());
		m.putAll(em);
		return m;
	}
	private Map<Long,Integer> getAllCaseCount(Date startDate,Date endDate){
		Map<Long,Integer> map = new HashMap<Long,Integer>();
		workFlowAnalysisDao.getAllCaseCountByApp(startDate,endDate,  ApplicationCategoryEnum.collaboration.key(),map);
	    workFlowAnalysisDao.getAllCaseCountByApp( startDate,endDate, ApplicationCategoryEnum.edoc.key(),map);
		return map;
	}
	private void setRadioValue(List<WorkFlowAnalysis> l,Date startDate,Date endDate){
		//超时实例Map
		Map<String,Integer> overMap =  getOverCaseCount( startDate, endDate);
		//总实例MAP
		Map<Long,Integer> allMap =  getAllCaseCount( startDate, endDate);
		
		for(WorkFlowAnalysis wfa : l){
			Integer allCaseCount = allMap.get(wfa.getOrgAccountId());
			Integer overCaseCount = overMap.get(wfa.getTempleteId()+"_"+wfa.getOrgAccountId());
			//使用率：使用率=本模板实例数/所有流程实例总数
			Integer caseCount = wfa.getCaseCount();
			if(allCaseCount!=null && allCaseCount!=0   && caseCount!=null){
				wfa.setUseRadio(caseCount/(allCaseCount*1.0));
			}else{
				wfa.setUseRadio(null);
			}
			
			//超时率=本模板超时的流程数/本模板流程总数
			if(overCaseCount!=null && caseCount!= null && caseCount!=0){
				wfa.setOverTimeRatio(overCaseCount/(caseCount*1.0));
			}else{
				wfa.setOverTimeRatio(null);
			}
			
			//效率：效率=基准时长/平均运行时长
			Integer avgRunTime = wfa.getAvgRunTime();
			Integer standarduration = wfa.getStandardTime();
			if( avgRunTime != null && avgRunTime!=0  && standarduration!=null){
				Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(standarduration), wfa.getOrgAccountId());
				wfa.setEfficiency(workStandarDuration/(avgRunTime*1.0));
			}else{
				wfa.setEfficiency(null);
			}
			
			if(allCaseCount!=null){
				wfa.setAllCaseCount(allCaseCount);
			}else{
				wfa.setAllCaseCount(0);
			}
			
			if(overCaseCount!=null){
				wfa.setOverCaseCount(overCaseCount);
			}else{
				wfa.setOverCaseCount(0);
			}
		}
	}
	private Date getFirstDayLastMonth(){
		Date date = Datetimes.addMonth(new Date(),-1);
		Date fd = Datetimes.getFirstDayInMonth(date);
		return fd;
	}
	private Date getLastDayLastMonth(){
		Date date = Datetimes.addMonth(new Date(),-1);
		Date fd = Datetimes.getLastDayInMonth(date);
		return fd;
	}
	
	

	/**
	 * 效率分析
	 */
	public List<SimpleSummaryModel> getEfficiencyAnalysis(
			Long accountId,
			Long templeteId,
			Date startDate,
			Date endDate,boolean isPaging){
		//1、查找模板
		Templete templete = templeteManager.get(templeteId);
		if(templete == null ) 
			return new ArrayList<SimpleSummaryModel>();
		//1、查找List
		List<Integer> sl = new ArrayList<Integer>();
		sl.add(Constant.flowState.finish.ordinal());
		sl.add(Constant.flowState.terminate.ordinal());
		List<SimpleSummaryModel> l = getSimpleSummaryModelList(
				accountId,
				startDate,
				endDate, 
				templete,
				sl,isPaging);
		//2、计算效率 {效率=基准时长/运行时长}
		Integer standarDuration = templete.getStandardDuration();
		for(SimpleSummaryModel ssm : l){
			Long runWorkTime = ssm.getRunWorkTime();
			if(standarDuration!= null && ssm.getRunWorkTime()!=null && ssm.getRunWorkTime()!=0 ){
				Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(standarDuration), templete.getOrgAccountId());
				ssm.setEfficiency(workStandarDuration/(runWorkTime*1.0));
			}
		}
		return l;
	}
	/**
	 * 超时分析
	 */
	public List<SimpleSummaryModel> getOverTimeAnalysis(
			Long accountId,
			Long templeteId,
			Date startDate, Date endDate, List<Integer> states,boolean isPaging) {
		//1、查找模板
		Templete templete = templeteManager.get(templeteId);
		if(templete == null ) 
			return new ArrayList<SimpleSummaryModel>();
		//1、查找List
		//List<Integer> sl = new ArrayList<Integer>();
		//sl.add(Constant.flowState.finish.ordinal());
		List<SimpleSummaryModel> l = getSimpleSummaryModelList(
				accountId,
				startDate, endDate, templete,states,isPaging);
		return l;
	}
	
	private List<SimpleSummaryModel> getSimpleSummaryModelList(
			Long accountId,
			Date startDate, 
			Date endDate,
			Templete templete,
			List<Integer> sl,boolean isPaging) {
		
		List<SimpleSummaryModel> l = new ArrayList<SimpleSummaryModel>();
		if(templete.getCategoryType().equals(TempleteCategory.TYPE.collaboration_templete.ordinal())
			||templete.getCategoryType().equals(TempleteCategory.TYPE.form.ordinal())){
			List<ColSummary> csl 
				= colManager.getColSummaryList(
						accountId,
						templete.getId(), 
						sl, startDate, endDate,isPaging);
			l = convertColSummary2SimpleSM(csl);
		}else{
			List<EdocSummary> esl 
			= edocSummaryManager.getEdocSummaryList(
					accountId,
					templete.getId(), 
					sl, startDate, endDate,isPaging);
			l = convertEdocSummary2SimpleSM(esl);
		}
		return l;
	}
	
	public CompareModel getCompareAnalysis(Long templeteId,
			Date startDate, Date endDate) {
		
		Templete templete = templeteManager.get(templeteId);
		if(templete == null ) 
			return null;
		
		CompareModel cm = workFlowAnalysisDao.getCompareModel(templeteId, startDate, endDate, templete.getCategoryType());
		
		Integer standuration = templete.getStandardDuration();
		
		cm.setStandarduaration(standuration);

		//效率=基准时长/平均运行时长
		if(standuration!=null && cm.getAvgRunTime()!=null && cm.getAvgRunTime()!=0){
			Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(standuration), templete.getOrgAccountId());
			cm.setEfficiency(workStandarDuration/(cm.getAvgRunTime()*1.0));
		}
		return cm;
	}
	public  List<WorkFlowAnalysis> getWorkFlowList(
			String appType, 
			List<Long> templeteIds,
			String beginDate, 
			String endDate, 
			Long   userId,
			Long   loginAccountId) throws Exception {
		
			int bYear;
			int bMonth;
			int eYear;
			int eMonth;
			//第一次进来没有查询条件。
			if(Strings.isBlank(appType)) 
				appType = String.valueOf(TempleteCategory.TYPE.form.ordinal());
			
			if (templeteIds!=null
					&& templeteIds.size() == 1
					&& WorkFlowAnalysis.AllTemplete == templeteIds.get(0)) {
					//查询全部
					List<Long> tids = workFlowAnalysisAclManager.getLoginAccountTempleteIdByUserId(userId,Integer.valueOf(appType));
					templeteIds.clear();
					templeteIds.addAll(tids);
			}
			
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			//默认显示上个月
			if(Strings.isBlank(beginDate)){
				bYear = year;
				bMonth = month ;
			}else{
				String begin[] = beginDate.split("-");
				bYear = Integer.parseInt(begin[0]); 	
				bMonth = Integer.parseInt(begin[1]);
			}
			
			if(Strings.isBlank(endDate)){
				eYear = year;
				eMonth =month;
			}else{
				String end[] = endDate.split("-");
				eYear = Integer.parseInt(end[0]);
				eMonth = Integer.parseInt(end[1]);
			}
		
			List<WorkFlowAnalysis> wfa
				= getWorkFlowAnalysisModelList(
						templeteIds, 
						bYear, 
						bMonth,
						eYear, 
						eMonth,
						Integer.parseInt(appType), 
						loginAccountId);
		return wfa;
	}
	private List<Long> getTempleteIds(List<Templete> tl){
		List<Long> tidl = new ArrayList<Long>();
		for(Templete t : tl){
			if(t!=null){
				tidl.add(t.getId());
			}
		}
		return tidl;
	}
	private List<SimpleSummaryModel> convertEdocSummary2SimpleSM(List<EdocSummary> l){
		if(l== null || l.isEmpty()) 
			return new ArrayList<SimpleSummaryModel>();
		
		List<SimpleSummaryModel>  nl = new ArrayList<SimpleSummaryModel>();
		SimpleSummaryModel  ssm  = null;
		for(EdocSummary es : l){
			ssm = new SimpleSummaryModel();
			ssm.setId(es.getId());
			ssm.setSubject(es.getSubject());
			ssm.setAppTypeName(EdocEnum.getEdocAppName(es.getEdocType()));
			if(es.getState() == com.seeyon.v3x.collaboration.Constant.flowState.finish.ordinal()
					||es.getState() == com.seeyon.v3x.collaboration.Constant.flowState.terminate.ordinal()){
				ssm.setRunWorkTime(es.getRunWorkTime());
				ssm.setOverWorkTime(es.getOverWorkTime());
			}else{
				try {
					long time = workTimeManager.getDealWithTimeValue(es.getCreateTime(),new Date(), es.getOrgAccountId());
					time = time/(60*1000); //毫秒转化为分钟
					long workDeadLine = 0l;
					Long deadline = es.getDeadline();
					if( deadline!=null && deadline!=0){
						workDeadLine = workTimeManager.convert2WorkTime(deadline, es.getOrgAccountId());	
					}
					ssm.setRunWorkTime(time) ;
					//设置了流程期限才计算超时时长。
					if(es.getDeadline()!=null && es.getDeadline()!=0){
						long over = time - workDeadLine;
						ssm.setOverWorkTime(over >0 ? over : 0);
					}
				} catch (WorkTimeSetExecption e) {
					log.error("",e);
				}
			}
			
			ssm.setDeadline(es.getDeadline());
			if(es.getDeadline()==null || es.getDeadline() == 0){
				ssm.setOverWorkTime(0L);
			}
			nl.add(ssm);
		}
		return nl;
	}
	private List<SimpleSummaryModel> convertColSummary2SimpleSM(List<ColSummary> l){
		if(l== null || l.isEmpty()) 
			return new ArrayList<SimpleSummaryModel>();
		
		List<SimpleSummaryModel>  nl = new ArrayList<SimpleSummaryModel>();
		SimpleSummaryModel  ssm  = null;
		for(ColSummary es : l){
			ssm = new SimpleSummaryModel();
			ssm.setId(es.getId());
			ssm.setSubject(es.getSubject());
			if(es.getState() == com.seeyon.v3x.collaboration.Constant.flowState.finish.ordinal()
				||es.getState() == com.seeyon.v3x.collaboration.Constant.flowState.terminate.ordinal()){
				ssm.setRunWorkTime(es.getRunWorkTime());
				ssm.setOverWorkTime(es.getOverWorkTime());
			}else{ //流程未结束
				try {
					long time = workTimeManager.getDealWithTimeValue(es.getCreateDate(),new Date(), es.getOrgAccountId());
					time = time/(60*1000); //毫秒转化为分钟
					long workDeadLine = 0l;
					Long deadline =es.getDeadline();
					if( deadline!=null && deadline!=0){
						workDeadLine = workTimeManager.convert2WorkTime(deadline, es.getOrgAccountId());	
					}
					ssm.setRunWorkTime(time) ;
					if(es.getDeadline()!=null && es.getDeadline()!=0){
						long over = time - workDeadLine;
						ssm.setOverWorkTime(over >0 ? over : 0);
					}
				} catch (WorkTimeSetExecption e) {
					log.error("",e);
				}
			}
			ssm.setDeadline(es.getDeadline());
			if(es.getDeadline()==null || es.getDeadline() == 0){
				ssm.setOverWorkTime(0L);
			}
			nl.add(ssm);
		}
		return nl;
	}

	
	
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setWorkFlowAnalysisDao(WorkFlowAnalysisDao workFlowAnalysisDao) {
		this.workFlowAnalysisDao = workFlowAnalysisDao;
	}
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	
	@Override
	public List<NodeAnalysis> getNodeAnalysisiList(
			Long templeteId,
			Long accountId,
		    boolean	isCol,
			List<Integer> states,
			Date startDate,
			Date endDate) {
		
		Templete templete = templeteManager.get(templeteId);
		if(templete == null
				||templete.getWorkflow() == null )
			return new ArrayList<NodeAnalysis>();
		
		return getNodeAnalysisiList(templete, accountId, isCol, states, startDate, endDate);
		
	}
	@Override
	public List<NodeAnalysis> getNodeAnalysisiList(
			Templete templete,
			Long accountId,
		    boolean	isCol,
			List<Integer> states,
			Date startDate,
			Date endDate) {
		
		if(templete == null 
				|| startDate == null 
				|| endDate == null
				|| accountId == null
				|| Strings.isEmpty(states))
			return new ArrayList<NodeAnalysis>();
		
		//1.解析模板流程，得到所有的节点及节点权限的名字。 
		
		BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
		List<NodeAnalysis>  nal = new ArrayList<NodeAnalysis>();
		if(process!=null){
			List<BPMActivity> actives = process.getActivitiesList();
			NodeAnalysis na = null;
			for(Iterator<BPMActivity> it= actives.iterator(); it.hasNext();){
				BPMActivity activity = it.next();
				if(null==activity.getActorList()){
					continue;
				}
				String id= ((BPMActor)(activity.getActorList().get(0))).getParty().getId();
				if("split".equals(id) 
						|| "join".equals(id)
						|| V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(id))
					continue;
				    
				BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
				if(seeyonPolicy == null ) continue;
				na = new NodeAnalysis();
				na.setId(seeyonPolicy.getId());
				na.setPolicyName(seeyonPolicy.getName());
				na.setId(activity.getId());
				na.setName(activity.getName());
				nal.add(na);
			}
			//2.得到某个节点超期的节点数
			Map<Long,Integer> overMap = affairManager.getOverNodeCount(
					templete.getId(), 
					accountId,
				    isCol,
					states,
					startDate,
					endDate);
			//3.得到某个节点所有的节点数。
			Map<Long,String> rMap =affairManager.getNodeCountAndSumRunTime(
					templete.getId(), 
					accountId,
				    isCol,
					states,
					startDate, 
					endDate);
			
			for(Iterator<NodeAnalysis> it = nal.iterator();it.hasNext();){
				NodeAnalysis nodeAnalysis = it.next();
			
				Long activityId = Long.valueOf(nodeAnalysis.getId());
				Integer overCount = overMap.get(activityId);
				if(overCount == null) overCount = 0;
				String infos = rMap.get(activityId);
				if(Strings.isNotBlank(infos)){
					String[] info = infos.split("[_]");
					Integer nodeCount = Integer.valueOf(info[0]);
					Long sumRunWorkTime = 0L;
					if(Strings.isNotBlank(info[1]) && !"null".equals(info[1])) {
						sumRunWorkTime = Long.valueOf(info[1]);
					}
					nodeAnalysis.setOverRadio(overCount/(nodeCount*1.0));
					nodeAnalysis.setAvgRunWorkTime((sumRunWorkTime/nodeCount));
				}
			}
		}else{
			log.info("流程效率分析解析流程出错:templeteId:"+templete.getId());
		}
		return nal;
	}
	@Override
	public List<Affair> getAffairByActivityId(
			Long templeteId, 
			Long orgAccountId,
    		boolean isCol,
			List<Integer> states,
			Long activityId,
			Date startDate, 
			Date endDate) {
		
		return affairManager.getAffairByActivityId(
				templeteId,
				orgAccountId,
				isCol,
				states,
				activityId, 
				startDate, 
				endDate);
	}
	@Override
	public List<MemberAnalysis> getMemberAnalysis(
			Long templeteId, 
			Long orgAccountId,
    		boolean isCol,
    		List<Integer> states,
			Long activityId,
			Date startDate, 
			Date endDate) {
		Map<Long,String> smap = affairManager.getStaticsByActivityId(
				templeteId, 
				orgAccountId,
				isCol,
				states,
				activityId, 
				startDate, 
				endDate);
		Map<Long,Integer> omap = affairManager.getOverCountByMember(
				templeteId, 
				orgAccountId,
				isCol,
				states,
				activityId, 
				startDate, 
				endDate);
		
		Set<Long> s =smap.keySet();
		List<MemberAnalysis> mal = new ArrayList<MemberAnalysis>();
		MemberAnalysis ma = null;
		for(Long memberId:s){
			String infos = smap.get(memberId);
			String[] info = infos.split("[_]");
			ma = new MemberAnalysis();
			ma.setMemberId(memberId);
			Integer count = 0;
			if(!"null".equalsIgnoreCase(info[0]) && info[0]!=null){
				count = Integer.valueOf(info[0]);
			}
			ma.setCount(count);
			if(!"null".equalsIgnoreCase(info[1]) && info[1]!=null){
				ma.setAvgRunTime(Long.valueOf(info[1]));
			}
			Integer overCount = omap.get(memberId);
			if(overCount!=null && count!= null && count!=0 ){
				ma.setOverRadio(overCount / (count*1.0));
			}
			mal.add(ma);
		}
		return mal;
	}
	@Override
	public Map<Long, Integer> getTempleteWorkStandarduraion(
			List<Long> templeteIds,Long loginAccountId) {
		
		List<Templete> ts = templeteManager.getAllSystemTempletesByEntityIds(templeteIds, null);
		Integer workTimeOfDay = 0;
		try {
			Calendar cal = Calendar.getInstance();
	    	int year =cal.get(Calendar.YEAR);
			workTimeOfDay = workTimeManager.getEachDayWorkTime(year, loginAccountId);
		} catch (WorkTimeSetExecption e1) {
			log.error("",e1);
		}
		Map<Long, Integer> m = new HashMap<Long, Integer>();
		for(Templete t :ts){
			Integer sd = t.getStandardDuration() == null ? 0 :t.getStandardDuration();
			Long wsd =0L;
			if(sd!=null){
				 wsd = workTimeManager.convert2WorkTime(Long.valueOf(sd), loginAccountId, workTimeOfDay);
			}
			m.put(t.getId(), wsd.intValue());
		}
		return m;
	}
}
