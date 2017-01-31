package com.seeyon.v3x.project.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.project.util.ProjectConstants;
import com.seeyon.v3x.project.util.ProjectUtils.PhaseRemindType;
import com.seeyon.v3x.util.Datetimes;

public class ProjectPhase extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date phaseBegintime;// 开始时间
	private Date phaseClosetime;// 结束时间
	private String phaseDesc;// 阶段描述
	private String phaseName;// 阶段名称
	private Integer phasePercent;// 阶段完成率,暂未使用
	private ProjectSummary projectSummary;// 对应项目
	private int phaseSort = 0;// 排序号,暂未使用
	private java.util.Set<ProjectEvolution> projectEvolutions;// 项目进展,暂未使用
	
	private Long beforeAlarmDate;//提前提醒
	private Long endAlarmDate;//结束前提醒
	
	public ProjectPhase(Long phaseId, String phaseName, Date phaseBegintime, Date phaseClosetime, Long beforeAlarmDate, Long endAlarmDate, String phaseDesc){
		this.id = phaseId;
		this.phaseName = phaseName;
		this.phaseBegintime = phaseBegintime;
		this.phaseClosetime = phaseClosetime;
		this.beforeAlarmDate = beforeAlarmDate;
		this.endAlarmDate = endAlarmDate;
		this.phaseDesc = phaseDesc;
	}

	public Set<ProjectEvolution> getProjectEvolutions() {
		return projectEvolutions;
	}

	public void setProjectEvolutions(Set<ProjectEvolution> projectEvolutions) {
		this.projectEvolutions = projectEvolutions;
	}

	public int getPhaseSort() {
		return phaseSort;
	}

	public void setPhaseSort(int phaseSort) {
		this.phaseSort = phaseSort;
	}

	public ProjectPhase() {
	}

	public Date getPhaseBegintime() {
		return this.phaseBegintime;
	}

	public void setPhaseBegintime(Date phaseBegintime) {
		this.phaseBegintime = phaseBegintime;
	}

	public Date getPhaseClosetime() {
		return this.phaseClosetime;
	}

	public void setPhaseClosetime(Date phaseClosetime) {
		this.phaseClosetime = phaseClosetime;
	}

	public String getPhaseDesc() {
		return this.phaseDesc;
	}

	public void setPhaseDesc(String phaseDesc) {
		this.phaseDesc = phaseDesc;
	}

	public String getPhaseName() {
		return this.phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public Integer getPhasePercent() {
		return this.phasePercent;
	}

	public void setPhasePercent(Integer phasePercent) {
		this.phasePercent = phasePercent;
	}

	public ProjectSummary getProjectSummary() {
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary) {
		this.projectSummary = projectSummary;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public Long getBeforeAlarmDate() {
		return beforeAlarmDate;
	}

	public void setBeforeAlarmDate(Long beforeAlarmDate) {
		this.beforeAlarmDate = beforeAlarmDate;
	}

	public Long getEndAlarmDate() {
		return endAlarmDate;
	}

	public void setEndAlarmDate(Long endAlarmDate) {
		this.endAlarmDate = endAlarmDate;
	}
	
	/**
	 * 判断当前项目阶段是否具备开始前提醒设置
	 */
	public boolean remindBeforeStart() {
		return this.beforeAlarmDate != ProjectConstants.PHASE_NO_REMIND;
	}

	/**
	 * 判断当前项目阶段是否具备结束前提醒设置
	 */
	public boolean remindBeforeEnd() {
		return this.endAlarmDate != ProjectConstants.PHASE_NO_REMIND;
	}
	
	/**
	 * 根据提醒类型获取对应的提醒时间
	 * @param remindType 提醒类型：项目阶段开始前提醒 or 项目阶段结束前提醒
	 * @return
	 */
	public Date getRemindTime(PhaseRemindType phaseRemindType) {
		switch(phaseRemindType) {
			case PhaseBeforeStart :
				return Datetimes.addMinute(this.phaseBegintime, -this.beforeAlarmDate.intValue());
			case PhaseBeforeEnd :
				return Datetimes.addMinute(this.phaseClosetime, -this.endAlarmDate.intValue());
		}
		throw new IllegalArgumentException("非法提醒类型[RemindType=" + phaseRemindType.name() + "]");
	}
	
}