package com.seeyon.v3x.batch;

public enum BatchState {
	Normal(0),//正常状态 可以进行批处理或者 批处理成功
	
	NotSupport(11),//不支持此应用批处理
	PolicyNotOpe(12),//节点权限不允许操作
	ProcessLocked(13),//流程正在修改
	NoSuchSummary(14),//流程已经不存在了
	NewFlow(15),//触发新流程。需要选人
	FormNotNull(16),//表单有必填项
	ProcessNeedPerson(17),//流程需要选择人员
	InvidateNode(18),//下个节点有无用的节点。
	
	Error(20),//发生异常
	;
	private int code;
	BatchState(int c){
		this.code = c;
	}
	public int getCode(){
		return this.code;
	}
}
