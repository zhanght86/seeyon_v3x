package com.seeyon.v3x.edoc.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.office.UserUpdateObject;
import com.seeyon.v3x.edoc.domain.EdocSummary;

public interface EdocSummaryManager {
	public EdocSummary findById(long id);
	
	public void saveEdocSummary(EdocSummary o);
	
	public void saveOrUpdateEdocSummary(EdocSummary o);
	
	public EdocSummary getSummaryByProcessId(String processId);
	
	/**
	 * 根据内部文号判断文号内部文号是否已经使用
	 * @param serialNo  内部文号
	 * @return (1：存在  0：不存在)
	 */
	public int checkSerialNoExsit(String serialNo,Long loginAccount);
	
	
	public  boolean deleteUpdateObj(String objId, String userId) ;
	public  boolean addUpdateObj(UserUpdateObject uo);
	/**
	 * 根据内部文号判断文号内部文号是否已经使用
	 * @param summaryId  公文ID
	 * @param serialNo   内部文号
	 * @param loginAccount  登录单位
	 * @return (1：存在  0：不存在)
	 */
	public int checkSerialNoExsit(String summaryId,String serialNo,Long loginAccount);
	
	/**
	 * @param templeteId  : 模板ID
	 * @param workFlowState ： 流程状态
	 * @param startDate : 开始时间
	 * @param endDate ： 结束时间
	 * @return
	 */
	public List<EdocSummary> getEdocSummaryList(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState,Date startDate,Date endDate,boolean isPaging);
	/**
	 * 根据模板得到此模板某段时间的实例数
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Integer getCaseCountByTempleteId (
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate);
	/**
	 * 根据模板得到此模板某段时间的平均运行时长。
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Integer getAvgRunWorkTimeByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate);
	
	
	/**
	 * 处理时间大于基准时间.
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @param standarduration
	 * @return
	 */
	public Integer  getCaseCountGTSD(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,Integer standarduration);
	/**
	 * 得到某个模板某段时间的超期流程数
	 * @param accountId
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Double getOverCaseRatioByTempleteId(Long accountId,
			Long templeteId,
			List<Integer> workFlowState,
			Date startDate,
			Date endDate);
	
	public UserUpdateObject editObjectState(String objId);
}
