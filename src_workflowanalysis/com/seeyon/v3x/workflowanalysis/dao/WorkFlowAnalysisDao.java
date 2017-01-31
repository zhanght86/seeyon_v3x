package com.seeyon.v3x.workflowanalysis.dao;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.workflowanalysis.domain.CompareModel;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;

public interface WorkFlowAnalysisDao {
	/**
	 * 保存
	 * @param wfal
	 */
	public void doWorkFlowAnalysis();
	/**
	 * 是否已经计算了指定时间的数据。
	 * @param year
	 * @param month
	 * @return
	 */
	public boolean isCount(int year,int month);
	
	/**
	 * 根据应用类型来分析数据库中的数据。
	 * @param startDate
	 * @param endDate
	 * @param app
	 * @return
	 */
	public List<WorkFlowAnalysis> createWorkFlowAnalysis(Date startDate, Date endDate,int app);
	/**
	 * 根据应用类型得到超期流程实例的数量
	 * @param startDate
	 * @param endDate
	 * @param app
	 * @return
	 */
	public Map<String,Integer> getOverCaseCountByApp(Date startDate,Date endDate ,int app);
	/**
	 * 得到一个单位下面所有的流程实例
	 * @param startDate
	 * @param endDate
	 * @param category  应用类型 {@link com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.Type}
	 * @param orgAccountId
	 * @return
	 */
	public void getAllCaseCountByApp(Date startDate,Date endDate ,int app,Map<Long,Integer> map);

	public void saveAll(List<WorkFlowAnalysis> l);
	public List<WorkFlowAnalysis> getWorkFlowAnalysisList(
			List<Long> templeteIds,
			Integer  startYear,Integer startMonth,
			Integer  endYear,Integer endMonth,
			Integer category,
			Long orgAccountId) throws Exception;
	
	
	/**
	 * 得到某模板某段时间的平均运行时长，最长运行时长，最短运行时长。
	 * @param templeteId
	 * @param startDate
	 * @param endDate
	 * @param category {@link com.seeyon.v3x.collaboration.templete.domain.TempleteCategory}}
	 * @return
	 */
	public CompareModel getCompareModel(Long templeteId, Date startDate,
			Date endDate,Integer category) ;
}
