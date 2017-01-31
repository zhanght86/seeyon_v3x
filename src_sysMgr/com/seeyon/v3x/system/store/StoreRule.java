/**
 * 
 */
package com.seeyon.v3x.system.store;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a> 2011-12-26
 */
public class StoreRule extends BaseModel {

	private static final long serialVersionUID = -711278366808815188L;
	
	/**
	 * 数据范围
	 */
	public static enum DataScorp{
		selfFlow, //自由协同
		templateFlow, //模板协同
	}
	
	/**
	 * 流程状态
	 */
	public static enum FlowState{
		running, //流转中
		end,  //结束
	}
	
	/**
	 * 运行状态 
	 */
	public static enum State{
		running,
		end
	}

	private Date beginDate;
	
	private Date endDate;
	
	private String dataScorp;
	
	private String flowState;
	
	//格式如：20:00
	private String startTime;
	
	//格式如：04:30
	private String stopTime;
	
	private int state = StoreRule.State.running.ordinal();
	
	//处理结果，条数
	private int result = 0;
	
	private Date createDate = new Date();

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDataScorp() {
		return dataScorp;
	}

	public void setDataScorp(String dataScorp) {
		this.dataScorp = dataScorp;
	}

	public String getFlowState() {
		return flowState;
	}

	public void setFlowState(String flowState) {
		this.flowState = flowState;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
