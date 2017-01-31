package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;

import www.seeyon.com.v3x.form.utils.StringUtils;

public class Operation_BindEvent implements Serializable{
	private static final long serialVersionUID = -4040215492792507068L;

	private String id;

	private String name;
	
	private String operationType;
	
	private String eventTriger;

	private String model;

	private String taskType;

	private String taskName;

	private String taskId;
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getEventTriger() {
		return eventTriger;
	}

	public void setEventTriger(String eventTriger) {
		this.eventTriger = eventTriger;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String createBindEventXml(){
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("<EventBind ")
			.append(" id=\"" + StringUtils.Java2XMLStr(id) + "\"")
			.append(" name=\"" + StringUtils.Java2XMLStr(name) + "\"")
			.append(" operationType=\"" + StringUtils.Java2XMLStr(operationType) + "\"")
			.append(" eventTriger=\"" + StringUtils.Java2XMLStr(eventTriger) + "\"")
			.append(" model=\"" + StringUtils.Java2XMLStr(model) + "\"")
			.append(" taskType=\"" + StringUtils.Java2XMLStr(taskType) + "\"")
			.append(" taskName=\"" + StringUtils.Java2XMLStr(taskName) + "\"")
			.append(" taskId=\"" + StringUtils.Java2XMLStr(taskId) + "\"")
			.append(" />");
		return sqlsb.toString();
	}
	
}
