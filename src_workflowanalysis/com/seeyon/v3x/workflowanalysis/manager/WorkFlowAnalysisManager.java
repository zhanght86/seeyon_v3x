package com.seeyon.v3x.workflowanalysis.manager;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.workflowanalysis.domain.CompareModel;
import com.seeyon.v3x.workflowanalysis.domain.MemberAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.NodeAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.SimpleSummaryModel;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
public interface WorkFlowAnalysisManager {
	
	 
	public List<WorkFlowAnalysis> getWorkFlowAnalysisModelList(
			List<Long> templeteIds,
			Integer  startYear,Integer startMonth,
			Integer  endYear,Integer endMonth,
			Integer category,
			Long orgAccountId) throws Exception;
	/**
	 * 综合分析
	 *  @param templeteInfo :模板信息
	 *  如果是具体模板，则该字段为保存模板的ID的List
	 *  如果是全部模板，则参数为长度为1的List，并且第一个对象的值为WorkFlowAnalysis.AllTemplete.
	 *  21212121,3232,121212
	 *  @param startDate 发起时间 区间的开始点 格式：yyyy-MM
	 *  @param endDate   发起时间区间的结束点  格式：yyyy-MM
	 *  @param appType : 应用类型 {@link com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.Type}
	 *  @param orgAccountId : 单位ID
	 */
	public  List<WorkFlowAnalysis> getWorkFlowList(
			String appType, 
			List<Long> templeteIds,
			String beginDate, 
			String endDate, 
			Long   userId,
			Long   loginAccountId) throws Exception;
	/**
	 * 流程效率分析
	 * @param templeteId
	 * @param startDate
	 * @param endDate
	 * @param isPaging 是否分页
	 * @return
	 */
	public List<SimpleSummaryModel> getEfficiencyAnalysis(
			Long accountId,
			Long templeteId,
			Date startDate,
			Date endDate,boolean isPaging);
	
	/**
	 * 流程超时分析
	 * @param templeteId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<SimpleSummaryModel> getOverTimeAnalysis(
			Long accountId,
			Long templeteId,
			Date startDate,
			Date endDate,
			List<Integer> states,boolean isPaging);
	
	/**
	 * 对比分析
	 * @return
	 */
	public CompareModel getCompareAnalysis(
			Long templeteId,
			Date startDate,
			Date endDate);
	/**
	 * 节点分析
	 * @param templeteId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<NodeAnalysis> getNodeAnalysisiList(
		    Long templeteId,
			Long accountId,
		    boolean	isCol,
			List<Integer> states,
			Date startDate,
			Date endDate);
	
	/**
	 * 节点分析
	 * @param templeteId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<NodeAnalysis> getNodeAnalysisiList(
		    Templete templete,
			Long accountId,
		    boolean	isCol,
			List<Integer> states,
			Date startDate,
			Date endDate);
	
	 /**
     * 查找某个模板，指定节点指定时间段的所有事项。
     * 节点分析 -- 节点权限分析 
     * @param states {@link com.seeyon.v3x.affair.constants.StateEnum}
     * @return
     */
    public List<Affair> getAffairByActivityId(
    		Long templeteId,
    		Long orgAccountId,
    		boolean isCol,
    		List<Integer> states,
    		Long activityId,
    		Date startDate,
    		Date endDate);
	
    /**
     * 节点分析  - 处理人分析。
     * @param templeteId
     * @param activityId
     * @param startDate
     * @param endDate
     * @return
     */
	public List<MemberAnalysis> getMemberAnalysis(
		Long templeteId,
		Long orgAccountId,
		boolean isCol,
		List<Integer> states,
		Long activityId,
		Date startDate,
		Date endDate);
	
	
	
	/**
	 * 流程分析.
	 */
	public void doWorkFlowAnalysis();
	/**
	 * 获取指定模版的基准时间，已经转化为工作时间。
	 * @param templeteIds
	 * @return
	 */
	public Map<Long ,Integer> getTempleteWorkStandarduraion(List<Long> templeteIds,Long LoginAccountId);
}
