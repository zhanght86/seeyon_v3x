package com.seeyon.v3x.taskmanage.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.util.Strings;

/**
 * <pre>
 * 任务列表简单属性查询模型，包含了：
 * 1.简单查询条件类型及其值，也即将生死相随桃园三结义的三个参数：condition、textfield、textfield1一统天下；
 * 2.所关联项目的ID及项目阶段的ID；
 * 3.是否取分页记录(反之取全部记录)；
 * 4.随未来需求变更可方便扩展此POJO的属性。
 * </pre>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-13
 */
public class TaskQueryModel {
	
	/**
	 * 任务查询类型：按标题、计划开始时间、计划结束时间、状态和时间(配合统计列表而构造出来的)、重要程度、状态、风险级别、创建人、负责人
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-28
	 */
	public static enum TaskQueryType {
		subject,
		plannedStartTime,
		plannedEndTime,
		importantLevel,
		status,
		riskLevel,
		createUser,
		managers,
		participators;
		
		public static TaskQueryType value(String queryType) {
			if(Strings.isBlank(queryType))
				return null;
			
			TaskQueryType[] types = TaskQueryType.values();
			for(TaskQueryType type : types) {
				if(type.name().equals(queryType)) {
					return type;
				}
			}
			throw new IllegalArgumentException("不合法[QueryType=" + queryType + "]的简单属性查询类型");
		}
	}

	private TaskQueryType queryType;
	private String value1;
	private String value2;
	private boolean pagination = true;
	private Long projectId;
	private Long projectPhaseId;
	
	public TaskQueryModel() {
		
	}

	public TaskQueryModel(TaskQueryType queryType, String value1, String value2) {
		super();
		this.queryType = queryType;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public boolean isValid() {
		return this.queryType != null && (Strings.isNotBlank(this.value1) || Strings.isNotBlank(this.value2));
	}
	
	public static TaskQueryModel parse(HttpServletRequest request) {
		TaskQueryModel tqm = parse(request.getParameter("condition"), request.getParameter("textfield"), request.getParameter("textfield1"));
		
		if(Strings.isNotBlank(request.getParameter("projectId"))) {
			tqm.setProjectId(NumberUtils.toLong(request.getParameter("projectId")));
			tqm.setProjectPhaseId(NumberUtils.toLong(request.getParameter("projectPhaseId"), TaskConstants.PROJECT_PHASE_ALL));
		}
		else {
			tqm.setProjectId(-1l);
			tqm.setProjectPhaseId(TaskConstants.PROJECT_PHASE_ALL);
		}
		
		return tqm;
	}
	
	public static TaskQueryModel parse(String condition, String value1, String value2) {
		TaskQueryType queryType = TaskQueryType.value(condition);
		return new TaskQueryModel(queryType, value1, value2);
	}
	
	public Long getProjectPhaseId() {
		return projectPhaseId;
	}
	public void setProjectPhaseId(Long projectPhaseId) {
		this.projectPhaseId = projectPhaseId;
	}
	public TaskQueryType getQueryType() {
		return queryType;
	}
	public void setQueryType(TaskQueryType queryType) {
		this.queryType = queryType;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public boolean isPagination() {
		return pagination;
	}
	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
}
