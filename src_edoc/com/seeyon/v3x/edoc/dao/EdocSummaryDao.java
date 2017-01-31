package com.seeyon.v3x.edoc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class EdocSummaryDao extends BaseHibernateDao<EdocSummary> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 根据内部文号判断文号内部文号是否已经使用
	 * @param summaryId  公文ID
	 * @param serialNo   内部文号
	 * @param loginAccount  登录单位
	 * @return (1：存在  0：不存在)
	 */
	public int checkSerialNoExsit(String summaryId,String serialNo,Long orgAccountId){
		if(serialNo==null||"".equals(serialNo))return 0;//为空，或者表单中没有这个字段。
		
		StringBuffer sb=new StringBuffer();
		sb.append("from EdocSummary as summary where ");
		if(summaryId!=null && !"".equals(summaryId)){
			sb.append(" summary.id!=? and ");
		}
		sb.append(" summary.serialNo = ?  and summary.orgAccountId=? ");
		int count =0;
		
		if(summaryId!=null && !"".equals(summaryId)){
			
			Object[] values3 = {Long.parseLong(summaryId),SQLWildcardUtil.escape(serialNo),orgAccountId};
			count = super.getQueryCount(sb.toString(),values3, new Type[]{Hibernate.LONG,Hibernate.STRING,Hibernate.LONG});
		}else {
			Object[] values2 = {SQLWildcardUtil.escape(serialNo),orgAccountId};
			count = super.getQueryCount(sb.toString(),values2, new Type[]{Hibernate.STRING,Hibernate.LONG});
		}
		if(count>0) {
			return 1;    //找到
		}else{			 //没找到
			return 0;	
		}
	}
	public void saveOrUpdate(EdocSummary summary)
	{
		super.getHibernateTemplate().saveOrUpdate(summary);
	}
	public EdocSummary getSummaryByCaseId(long caseId)
	{
		EdocSummary summary=null;
		String hql="from " + EdocSummary.class.getName() + " as summary where summary.caseId = ? ";
		Object[] values = {caseId};
		List<EdocSummary> list = super.find(hql, values);
		if (list.size() == 0) {return null;}
		summary = (EdocSummary) list.get(0);		
		return summary;
	}
	public EdocSummary getSummaryByFormId(int formId)
	{
		EdocSummary summary=null;
		String hql="from EdocSummary as summary where summary.formId = ? ";
		Object[] values = {formId};
		List<EdocSummary> list = super.find(hql, values);
		if (list.size() == 0) {return null;}
		summary = (EdocSummary) list.get(0);		
		return summary;
	}
	public boolean isUseMetadataValue(String fieldNames,String value)
	{
		boolean ret=true;
		String hql="from EdocSummary as summary where";
		String [] fdn=fieldNames.split(",");
		boolean isFirst=true;
		
		Object [] values=new Object[fdn.length];
		Type [] types=new Type[fdn.length];
		int i=0;
		
		for(String fn:fdn)
		{
			if("doc_type".equals(fn)){fn="docType";}
			else if("send_type".equals(fn)){fn="sendType";}
			else if("secret_level".equals(fn)){fn="secretLevel";}
			else if("urgent_level".equals(fn)){fn="urgentLevel";}
			else if("keep_period".equals(fn)){fn="keepPeriod";}
			
			if(isFirst==false){hql+=" or ";}
			hql+=" summary."+fn+"=?";
			isFirst=false;
			
			if("keepPeriod".equals(fn))
			{
				values[i]=value;
				types[i]=Hibernate.STRING;				
			}
			else
			{
				values[i]=Integer.parseInt(value);
				types[i]=Hibernate.INTEGER;
			}
			i++;
		}
		
		int iCount=super.getQueryCount(hql, values, types);
		if(iCount<=0){ret=false;}
		return ret;
	}
	
	public void forceCommit(){
    	super.getSession().flush();
    	super.getSession().clear();
	}

	
	
	/**       流程效率分析                */
	public List<EdocSummary> getEdocSummaryList(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,boolean isPaging) {
		List<EdocSummary> edocList=new ArrayList<EdocSummary>();
		StringBuilder sb = new StringBuilder();
		getQueryHql(sb);
		sb.append(" order by summary.runWorkTime,summary.id  ");
		Map<String, Object> parameter = setParameter2Map(accountId,templeteId, workFlowState,
				startDate, endDate);
		if(isPaging){
			edocList=super.find(sb.toString(), parameter);
		}else{
			edocList=super.find(sb.toString(), -1, -1, parameter);
		}
		return edocList;
	}
	private Map<String, Object> setParameter2Map(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("templeteId", templeteId);
		parameter.put("state", workFlowState);
		parameter.put("startDate", startDate);
		parameter.put("endDate", endDate);
		parameter.put("orgAccountId", accountId);
		return parameter;
	}
	private Map<String,Integer>  getInfo(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate){
		StringBuilder sb = new StringBuilder();
		sb.append(" select " );
		sb.append(" avg(summary.runWorkTime),");
		sb.append(" count(summary.id)  ");
		getQueryHql(sb);
		Map<String, Object> parameter = setParameter2Map(
				accountId,
				templeteId, 
				workFlowState, 
				startDate, 
				endDate);
		List l = super.find(sb.toString(),-1,-1, parameter);
		Map<String,Integer> map = new HashMap<String,Integer>(); 
		if(Strings.isNotEmpty(l)){
			Object[] obj = (Object[])l.get(0);
			Integer avgRunWorkTime = 0;
			if(obj[0]!=null){
				avgRunWorkTime = ((Number)obj[0]).intValue();
			}
			Integer c = ((Number)obj[1]).intValue();
			map.put("AVG", avgRunWorkTime);
			map.put("COUNT", c);
		}
		return map;
	}
	public Integer getAvgRunWorkTimeByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate){
		
		StringBuilder sb = new StringBuilder();
		sb.append("select createTime,completeTime ,runWorkTime,state ");
		getQueryHql(sb);
		
		Map<String, Object> parameter = setParameter2Map(
				accountId,
				templeteId,
				workFlowState, 
				startDate, 
				endDate);
		
		List<Object[]> l = (List<Object[]>)super.find(sb.toString(), -1,-1,parameter);
		
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
	public Integer getCaseCountByTempleteId (
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate){
		Integer c = getInfo(accountId,templeteId, workFlowState, startDate, endDate).get("COUNT");
		return  c == null ? 0: c;
	}
	private void getQueryHql(StringBuilder sb) {
		sb.append(" from EdocSummary as summary ");
		sb.append(" where ");
		sb.append(" summary.templeteId=:templeteId ");
		sb.append(" and summary.state in (:state) ");
		sb.append(" and summary.createTime between :startDate and :endDate ");
		sb.append(" and summary.orgAccountId = :orgAccountId");
	}
	public Integer  getCaseCountGTSD(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,Integer standarduration){
		StringBuilder sb = new StringBuilder();
		sb.append(" select " );
		sb.append(" count(summary.id) ");
		getQueryHql(sb);
		sb.append(" and summary.runWorkTime > :standarduration ");
		Map<String, Object> parameter = setParameter2Map(accountId,templeteId, workFlowState, startDate, endDate);
		parameter.put("standarduration", standarduration == null ? 0L: Long.valueOf(standarduration));
		List l = super.find(sb.toString(),-1,-1, parameter);
		if(Strings.isNotEmpty(l)){
			return ((Number)l.get(0)).intValue();
		}
		return 0;
	}
	
	public Double getOverCaseRatioByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState,
			Date startDate,
			Date endDate){
		
		StringBuilder sb = new StringBuilder();
		sb.append("select createTime,completeTime,deadline,overWorkTime,state");
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
}












