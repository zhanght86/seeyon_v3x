package com.seeyon.v3x.taskmanage.utils;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.StatisticPeriod;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 工作管理和项目任务统计中的统计条件模型
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-8
 */
public class StatisticCondition {
	
	private int status;
	private List<Long> memberIds;
	private Date beginDate;
	private Date endDate;
	private Long projectId;
	private Long projectPhaseId;
	
	/**
	 * 工作管理和项目任务统计场景下，将请求信息解析为统计条件模型
	 */
	public static StatisticCondition parse(HttpServletRequest request) {
		StatisticCondition sc = new StatisticCondition();
		
		sc.setStatus(NumberUtils.toInt(request.getParameter("status")));
		
		if(Strings.isNotBlank(request.getParameter("projectId"))) {
			sc.setProjectId(NumberUtils.toLong(request.getParameter("projectId")));
		}
		if(Strings.isNotBlank(request.getParameter("projectPhaseId"))) {
			sc.setProjectPhaseId(NumberUtils.toLong(request.getParameter("projectPhaseId")));
		}
		
		int timeRange = NumberUtils.toInt(request.getParameter("timeRange"));
		StatisticPeriod sp = StatisticPeriod.parseOrdinal(timeRange);
		if(sp == StatisticPeriod.Custom) {
			if(Strings.isNotBlank(request.getParameter("beginDate"))) {
		    	sc.setBeginDate(Datetimes.getTodayFirstTime(request.getParameter("beginDate")));
		    }
			
			if(Strings.isNotBlank(request.getParameter("endDate"))) {
		    	sc.setEndDate(Datetimes.getTodayLastTime(request.getParameter("endDate")));
		    }
		}
		else {
			sc.setBeginDate(TaskUtils.getBeginDate(sp));
			sc.setEndDate(TaskUtils.getEndDate(sp));
		}
		
		if(Strings.isNotBlank(request.getParameter("memberIds"))) {
			sc.setMemberIds(FormBizConfigUtils.parseStr2Ids(request.getParameter("memberIds")));
		}
		
		return sc;
	}
	
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
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getProjectPhaseId() {
		return projectPhaseId;
	}
	public void setProjectPhaseId(Long projectPhaseId) {
		this.projectPhaseId = projectPhaseId;
	}
	public List<Long> getMemberIds() {
		return memberIds;
	}
	public void setMemberIds(List<Long> memberIds) {
		this.memberIds = memberIds;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
