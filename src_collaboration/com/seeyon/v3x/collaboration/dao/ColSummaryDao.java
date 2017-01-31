package com.seeyon.v3x.collaboration.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.util.Strings;

public class ColSummaryDao extends BaseHibernateDao<ColSummary> {
	public List<ColSummary> getColSummaryList( 
			Long acccountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,boolean isPaging) {
		List<ColSummary> colList=new ArrayList<ColSummary>();
		StringBuilder sb = new StringBuilder();
		getQueryHql(sb);
		sb.append(" order by summary.runWorkTime,summary.id  ");
		Map<String, Object> parameter = setParameter2Map(
				acccountId,
				templeteId, workFlowState,
				startDate, endDate);
		if(isPaging){
			colList=super.find(sb.toString(), parameter);
		}else{
			colList=super.find(sb.toString(), -1, -1, parameter);
		}
		return colList;
	}
	private Map<String, Object> setParameter2Map(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("orgAccountId", accountId);
		parameter.put("templeteId", templeteId);
		parameter.put("state", workFlowState);
		parameter.put("startDate", startDate);
		parameter.put("endDate", endDate);
		return parameter;
	}
	private Map<String,Integer>  getInfo(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate){
		StringBuilder sb = new StringBuilder();
		sb.append(" select " );
		sb.append(" avg(summary.runWorkTime),");
		sb.append(" count(summary.id) ");
		getQueryHql(sb);
		Map<String, Object> parameter = setParameter2Map(
				accountId,
				templeteId, workFlowState, startDate, endDate);
		List l = super.find(sb.toString(),-1,-1, parameter);
		Map<String,Integer> map = new HashMap<String,Integer>(); 
		if(Strings.isNotEmpty(l)){
			Object[] obj = (Object[])l.get(0);
			Integer  avgRunWorkTime = 0;
			if(obj[0]!=null){
				avgRunWorkTime = ((Number)obj[0]).intValue();
			}
			Integer c = ((Number)obj[1]).intValue();
			map.put("AVG", avgRunWorkTime);
			map.put("COUNT", c);
		}
		return map;
	}
	public Integer  getCaseCountGTSD(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,Integer standarduration){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(summary.id) ");
		getQueryHql(sb);
		sb.append(" and summary.runWorkTime > :sd ");
		Map<String, Object> map = setParameter2Map(
				accountId,
				templeteId, workFlowState, startDate, endDate);
		map.put("sd", standarduration == null ?0L:Long.valueOf(standarduration));
		List l = super.find(sb.toString(),-1,-1, map);
		if(Strings.isNotEmpty(l)){
			return ((Number)l.get(0)).intValue();
		}
		return 0;
	}
	/**
	 * 取这个模板这段时间的平均运行时长。
	 * @param accountId
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Integer getAvgRunWorkTimeByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, 
			Date startDate, 
			Date endDate){
		
			StringBuilder sb = new StringBuilder();
			sb.append("select createDate,finishDate,runWorkTime,state ");
			getQueryHql(sb);
			
			Map<String, Object> parameter = setParameter2Map(
					accountId,
					templeteId,
					workFlowState, 
					startDate, 
					endDate);
			List<Object[]> l = (List<Object[]>)super.find(sb.toString(),-1,-1, parameter);
			
			Long sumRunWorkTime = 0L;
			Long avgRunWorkTime = 0L;
			if(Strings.isNotEmpty(l)){
				for(Object[] obj : l){
					Date sdate = (Date)obj[0];
					Date edate = (Date)obj[1];
					Long runWrokTime = null;
					if(obj[2]!=null){
						runWrokTime = ((Number)obj[2]).longValue();
					}
					Integer state = 0;
					if(obj[3]!=null){
						state = ((Number)obj[3]).intValue();
					}
					//如果有已经计算出来的运行时长，直接取运行时长
					if(runWrokTime != null){
						sumRunWorkTime += runWrokTime;
						continue;
					}else{
						if(edate == null)
							edate = new Date();
						
						sumRunWorkTime += Functions.getMinutesBetweenDatesByWorkTime(sdate,edate,accountId);
					}
				}
				avgRunWorkTime = sumRunWorkTime / l.size();
			}
			return  avgRunWorkTime.intValue();
	}
	public Integer getCaseCountByTempleteId (Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate){
		Integer c = getInfo(accountId,
				templeteId, workFlowState, startDate, endDate).get("COUNT");
		return  c == null ? 0: c;
	}
	
	public Double getOverCaseRatioByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState,
			Date startDate,
			Date endDate){
		
		StringBuilder sb = new StringBuilder();
		sb.append("select createDate,finishDate,deadline,overWorkTime,state");
		getQueryHql(sb);
		
		Map<String, Object> parameter = setParameter2Map(
				accountId,
				templeteId,
				workFlowState, 
				startDate, 
				endDate);
		
		List<Object[]> l = (List<Object[]>)super.find(sb.toString(),-1,-1, parameter);
		
		Integer countAll = 0;
		Integer countOver = 0;
		if(Strings.isNotEmpty(l)){
			for(Object[] obj : l){
				countAll++;
				Date sdate = (Date)obj[0];
				Date edate = (Date)obj[1];
				Long  deadline = 0L;
				if(obj[2]!=null){
					deadline = ((Number)obj[2]).longValue();
				}
				
				//没有设置流程期限就不算超期。
				if(deadline == null || deadline == 0)
					continue;
				
				Long overWorkTime = 0L;
				if(obj[3]!=null){
					overWorkTime  =  ((Number)obj[3]).longValue();
				}
				
				Integer state = 0;
				if(obj[4]!=null){
					state  =  ((Number)obj[4]).intValue();
				}
				
				if(overWorkTime>0) {
					countOver++;
				}else{
					if(edate == null)
						edate = new Date();
					
					Long run = Functions.getMinutesBetweenDatesByWorkTime(sdate,edate,accountId);
					Long workDeadline = Functions.convert2WorkTime(deadline, accountId);
					if(run>workDeadline){
						countOver++;
					}
				}
			}
		}
		double ratio = 0.0;
		if(countAll!=0){
			ratio = countOver/(countAll*1.0);
		}
		return  ratio;
	}
	private void getQueryHql(StringBuilder sb) {
		sb.append(" from ColSummary as summary ");
		sb.append(" where ");
		sb.append(" summary.templeteId=:templeteId ");
		sb.append(" and summary.state in (:state) ");
		sb.append(" and (summary.createDate between :startDate and :endDate) ");
		sb.append(" and summary.orgAccountId = :orgAccountId");
	}
}
