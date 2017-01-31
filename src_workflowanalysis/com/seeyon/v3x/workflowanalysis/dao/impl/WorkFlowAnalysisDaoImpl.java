package com.seeyon.v3x.workflowanalysis.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflowanalysis.dao.WorkFlowAnalysisDao;
import com.seeyon.v3x.workflowanalysis.domain.CompareModel;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
@SuppressWarnings("rawtypes")
public class WorkFlowAnalysisDaoImpl extends BaseHibernateDao<WorkFlowAnalysis> implements WorkFlowAnalysisDao{
	
	public boolean isCount(int year,int month){
		StringBuilder sb = new StringBuilder();
		sb.append(" from WorkFlowAnalysis ");
		sb.append(" where year = ? and month = ? ");
		return super.getQueryCount(sb.toString(), new Object[]{year,month}, 
				new Type[]{Hibernate.INTEGER,Hibernate.INTEGER})>0;
	}
	
	public List<WorkFlowAnalysis> createWorkFlowAnalysis(Date startDate, Date endDate,int app){
		StringBuilder hql = new StringBuilder();
		hql.append(" select ");
		hql.append(" tem.id, ");  
		hql.append(" max(tem.subject), ");
		hql.append(" max(tem.memberId), ");
		hql.append(" count(s.id), ");
		hql.append(" max(tem.standardDuration) as sd, ");
		hql.append(" avg(s.overWorkTime), ");
		hql.append(" avg(s.runWorkTime), ");
		hql.append(" max(tem.categoryType), ");
		hql.append(" s.orgAccountId ");
		if(ApplicationCategoryEnum.collaboration.key() == app){
			hql.append(" from ColSummary s,Templete tem ");
		}else{
			hql.append(" from EdocSummary s,Templete tem ");
		}
		hql.append(" where ");
		hql.append(" s.templeteId = tem.id ");
		hql.append(" and s.templeteId is not null ");
		
		if(startDate != null && endDate != null){
			if(ApplicationCategoryEnum.collaboration.key() == app){
				hql.append(" and s.finishDate between ? and ? ");
			}else{
				hql.append(" and s.completeTime between ? and ? ");
			}
		}
		//流程正常结束的。
		hql.append(" and s.state = ? ");
		//非草稿的。
		hql.append(" and s.caseId is not null ");  
		hql.append(" group by tem.id , s.orgAccountId ");
		//缺省按使用率（全局）排序。
	//	hql.append(" order by count(s.id) DESC ");
		
		List l = super.find(hql.toString(),startDate,endDate,Constant.flowState.finish.ordinal());
		List<WorkFlowAnalysis> wfal= convertRS2Model(l);
		return wfal;
	}
	
	private List<WorkFlowAnalysis> convertRS2Model(List l ){
		if(l == null) return null;
		List<WorkFlowAnalysis> nl = new ArrayList<WorkFlowAnalysis>(l.size());
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		
		cal.set(Calendar.MONTH, month-1);
		
		for(Object o : l){
			Object[] m = (Object[])o;
			//模板ID
			Long templeteId = (Long)m[0];
			//模板名字
			String templeteName = (String)m[1];
			//模板所属人
			Long memberId = (Long)m[2];
			//实例数
			Integer caseCount = (Integer)m[3];
			//基准时长
			Integer standarduration  = (Integer)m[4];
			//平均超期时长
			Integer avgOverTime = null;
			if(m[5] != null){
				avgOverTime = ((Number)m[5]).intValue();
			}else{
				avgOverTime = 0;
			}
			//平均运行时长
			Integer avgRunTime = null;
			if(m[6] != null){
				avgRunTime = ((Number)m[6]).intValue();
			}else{
				avgRunTime=0;
			}
			//分类
			Integer catagoryType = (Integer)m[7];
			//单位ID
			Long orgAccountId  =(Long)m[8];
			
			WorkFlowAnalysis wfa =  new WorkFlowAnalysis();
			wfa.setIdIfNew();
			wfa.setTempleteId(templeteId);
			wfa.setCaseCount(caseCount);
			wfa.setStandardTime(standarduration);
			wfa.setAvgOverTime(avgOverTime);
			wfa.setAvgRunTime(avgRunTime);
			
			//类别
			wfa.setCatagory(catagoryType);
			
			//年 
			wfa.setYear(year);
			//月
			wfa.setMonth(month);
			//日期
			wfa.setStatDate(cal.getTime());
			//单位
			wfa.setOrgAccountId(Long.valueOf(orgAccountId));
			//超时实例数
			//总实例数
			
			nl.add(wfa);
		}
		return nl;
	}
	
	
	public Map<String,Integer> getOverCaseCountByApp(Date startDate,Date endDate ,int app){
		StringBuilder sb = new StringBuilder();
		sb.append(" select templeteId,count(id),s.orgAccountId  ");
		if(app == ApplicationCategoryEnum.edoc.key()){
			sb.append(" from EdocSummary as s ");
		}else{
			sb.append(" from ColSummary as s");
		}
		sb.append(" where ");
		sb.append(" s.templeteId is not null ");
		if(startDate != null && endDate != null){
			if(ApplicationCategoryEnum.collaboration.key() == app){
				sb.append(" and s.finishDate between ? and ? ");
			}else{
				sb.append(" and s.completeTime between ? and ? ");
			}
		}
		//流程正常结束的。
		sb.append(" and s.state = ? ");
		//超期的
		sb.append(" and s.overWorkTime>0 ");
		sb.append(" group by s.templeteId , s.orgAccountId ");
		
		List l = super.find(sb.toString(),startDate,endDate,Constant.flowState.finish.ordinal());
		Map<String,Integer> overMap = new HashMap<String,Integer>();
		for(Object o : l){
			Object[] a = (Object[])o;
			Long templeteId = (Long)a[0];
			Integer count = (Integer)a[1];
			Long orgAccountId = (Long)a[2];
			overMap.put(templeteId+"_"+orgAccountId, count);
		}
		return overMap;
	}
	
	private Integer getTotalCaseCount(Date startDate,Date endDate ,Long orgAccountId){
		StringBuilder sb  = new StringBuilder();
		sb.append("select max(w.allCaseCount) ");
		sb.append("from WorkFlowAnalysis as w ");
		sb.append("where ");
		sb.append("w.statDate  between :startDate and :endDate " );
		sb.append("and w.orgAccountId = :orgAccountId ");
		sb.append("group by w.statDate ");
		
		Map<String,Object> namedParameterMap = new HashMap<String,Object>();
		namedParameterMap.put("startDate" ,startDate );
		namedParameterMap.put("endDate" ,endDate );
		namedParameterMap.put("orgAccountId" ,orgAccountId );
		
		Integer count = 0;
		List l = super.find(sb.toString(),-1,-1, namedParameterMap);
		if(Strings.isNotEmpty(l)){
			for(Object o : l ){
				if(o!=null){
					count+=((Number)o).intValue();
				}
			}
		}
		return count;
	}
	
	/**
	 * 在某个时间端，某个单位的所有模板流程的总数
	 * @param startDate
	 * @param endDate
	 * @param app
	 * @return
	 */
	public void getAllCaseCountByApp(Date startDate,Date endDate ,int app,Map<Long,Integer> map){
		StringBuilder sb = new StringBuilder();
		sb.append(" select s.orgAccountId ,count(id) ");
		if(app == ApplicationCategoryEnum.edoc.key()){
			sb.append(" from EdocSummary as s");
		}else{
			sb.append(" from ColSummary as s");
		}
		sb.append(" where ");
		sb.append("  s.templeteId is not null ");
		if(startDate != null && endDate != null){
			if(ApplicationCategoryEnum.collaboration.key() == app){
				sb.append(" and s.finishDate between ? and ? ");
			}else{
				sb.append(" and s.completeTime between ? and ? ");
			}
		}
		//流程正常结束的。
		sb.append(" and s.state = ? ");
		sb.append(" group by s.orgAccountId ");
		
		List l = super.find(sb.toString(),startDate,endDate,Constant.flowState.finish.ordinal());
		for(Object o : l){
			Object[] a = (Object[])o;
			Long orgAccount = (Long)a[0];
			Integer count = (Integer)a[1];
			Integer c = map.get(orgAccount);
			if(c !=null){
				map.put(orgAccount, c+count);
			}else{
				map.put(orgAccount, count);
			}
		}
	}

	@Override
	public void saveAll(List<WorkFlowAnalysis> l) {
		super.savePatchAll(l);
	}

	@Override
	public void doWorkFlowAnalysis() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<WorkFlowAnalysis> getWorkFlowAnalysisList(
			List<Long> templeteIds,
			Integer  startYear,Integer startMonth,
			Integer  endYear,Integer endMonth,
			Integer category ,
			Long orgAccountId) throws Exception {
		
		//指定模板
		if(Strings.isEmpty(templeteIds)){
			return new ArrayList<WorkFlowAnalysis>();
		}
		Map<String,Object> map = new HashMap<String,Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(" select ");
		sb.append(" templeteId,");               
		sb.append(" max(catagory),");
		sb.append(" sum(caseCount),");
		sb.append(" sum(avgRunTime*caseCount)/sum(caseCount),");
		sb.append(" sum(avgOverTime*overCaseCount),");
		sb.append(" sum(overCaseCount),"); 
		sb.append(" sum(allCaseCount) ");
		sb.append(" from ");
		sb.append(" WorkFlowAnalysis");
		sb.append(" where");
		sb.append(" orgAccountId = :orgAccountId");
		sb.append(" and statDate between :startDate and :endDate ");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, startYear);
		cal.set(Calendar.MONTH, startMonth-1);
		Date startDate = Datetimes.getFirstDayInMonth(cal.getTime());
		
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, endYear);
		cal1.set(Calendar.MONTH, endMonth-1);
		Date endDate = Datetimes.getLastDayInMonth(cal1.getTime());
		
		map.put("startDate",startDate);
		map.put("endDate", endDate);
		if(templeteIds.size() == 1 
				&& templeteIds.get(0) == WorkFlowAnalysis.AllTemplete){
			//全部
		}else{
			sb.append(" and templeteId in (:templeteIds) ");
			map.put("templeteIds", templeteIds);
		}
		sb.append(" and catagory in (:catagory) ");
		sb.append(" group by templeteId ");
		sb.append(" order by sum(caseCount) desc ");
		
		List<Integer> catl = new ArrayList<Integer>();
		if(TempleteCategory.TYPE.edoc.ordinal() == category.intValue()){
			catl.add(TempleteCategory.TYPE.edoc_send.ordinal());
			catl.add(TempleteCategory.TYPE.edoc_rec.ordinal());
			catl.add(TempleteCategory.TYPE.sginReport.ordinal());
			catl.add(TempleteCategory.TYPE.edoc.ordinal());
		}else{
			catl.add(category);
		}
	
		map.put("orgAccountId", orgAccountId);
		map.put("catagory", catl);
		List<Object[]> l = (List<Object[]>)super.find(sb.toString(),-1,-1, map);
		List<WorkFlowAnalysis> wfal = new ArrayList<WorkFlowAnalysis>();
		WorkFlowAnalysis wfa = null;
		Integer countT = getTotalCaseCount(startDate, endDate, orgAccountId);
		for(Object[] object : l ){
			
			//模板ID
			Long templeteId = ((Number)object[0]).longValue();
			//分类
			Integer categoryType = ((Number)object[1]).intValue();
			//流程数
			Integer caseCount = 0;
			if(object[2]!=null){
				caseCount = ((Number)object[2]).intValue();
			}
			//平均使用时间
			Integer avgRunTime = 0 ;
			if(object[3]!=null){
				avgRunTime = ((Number)object[3]).intValue();
			}
			//平均超时时间
			Long sumOverTime = 0L;
			if(object[4]!=null){
				sumOverTime = ((Number)object[4]).longValue();
			}
			
			Integer overCaseCount = 0;
			Integer avgOverTime = 0;
			if(object[5]!=null){
				overCaseCount = ((Number)object[5]).intValue();
				if(overCaseCount!=0){
					avgOverTime =((Number)(sumOverTime/overCaseCount)).intValue();
				}
			}
			//所有的流程数
			Integer allCaseCount = countT;
			
			//超市率
			Double overRadio = 0.0;
			if(caseCount!=0 && overCaseCount!=0){
				overRadio = overCaseCount/(caseCount*1.0);
			}
			Double useRatio =0.0;
			if(allCaseCount!=0  && caseCount!=0 ){
				useRatio = caseCount / (allCaseCount*1.0);
			}
			wfa = new  WorkFlowAnalysis();
			
			wfa.setTempleteId(templeteId);
			wfa.setCatagory(categoryType);
			wfa.setCaseCount(caseCount);
			
			wfa.setAvgOverTime(avgOverTime);
			wfa.setAvgRunTime(avgRunTime);
			wfa.setAllCaseCount(allCaseCount);
			wfa.setUseRadio(useRatio);
			wfa.setOverTimeRatio(overRadio);
			wfa.setOrgAccountId(orgAccountId);
			
			wfal.add(wfa);			
		}
		return wfal;
	}
	
	public CompareModel getCompareModel(Long templeteId, Date startDate,
			Date endDate,Integer category) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select ");
		sb.append(" max(summary.runWorkTime),");
		sb.append(" min(summary.runWorkTime),");
		sb.append(" avg(summary.runWorkTime) ");
		if(TempleteCategory.TYPE.collaboration_templete.ordinal() == category.intValue()
				||TempleteCategory.TYPE.form.ordinal() == category.intValue()){
			sb.append(" from ColSummary as summary ");
		}else{
			sb.append(" from EdocSummary as summary ");
		}
		sb.append(" where ");
		sb.append(" summary.templeteId=:templeteId ");
		sb.append(" and summary.state in (:state) ");
		if(TempleteCategory.TYPE.collaboration_templete.ordinal() == category.intValue()
				||TempleteCategory.TYPE.form.ordinal() == category.intValue()){
			sb.append(" and summary.createDate between :startDate and :endDate ");
		}else{
			sb.append(" and summary.createTime between :startDate and :endDate ");
		}
		
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("templeteId", templeteId);
		parameter.put("startDate", startDate);
		parameter.put("endDate", endDate);
		List<Integer> l = new ArrayList<Integer>();
		l.add(Constant.flowState.finish.ordinal());
		parameter.put("state", l);
		List rs = super.find(sb.toString(), parameter);
		CompareModel cm = new CompareModel();
		cm.setTempleteId(templeteId);
		if(Strings.isNotEmpty(rs)){
			Object[] arr =(Object[])rs.get(0);
			Long maxRunTime = (Long)arr[0];
			Long minRunTime = (Long)arr[1];
			if(arr[2] != null){
				Long avgRunTime = ((Number)arr[2]).longValue();
				cm.setAvgRunTime(avgRunTime);
			}
			if(minRunTime!=null){
				cm.setMinRunTime(minRunTime);
			}
			if(maxRunTime!=null){
				cm.setMaxRunTime(maxRunTime);
			}
		}
		return cm;
	}
}
