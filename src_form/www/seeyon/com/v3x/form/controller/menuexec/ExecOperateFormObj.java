package www.seeyon.com.v3x.form.controller.menuexec;

import java.util.ArrayList;
import java.util.List;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

public class ExecOperateFormObj {
	private String appName;

	private String formName;

	private String operationName;

	private List<String> buttonList = new ArrayList<String>();

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<String> getButtonList() {
		return buttonList;
	}

	public void setButtonList(List<String> buttonList) {
		this.buttonList = buttonList;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	private SeeyonForm_Runtime runtime;
	
	
	
	
	public void init()throws SeeyonFormException{
		runtime=SeeyonForm_Runtime.getInstance();
		ISeeyonForm_Application fapp=runtime.getAppManager().findByName(appName);
		if (fapp==null) 
		//TODO 需要定义错误编码
		   throw new SeeyonFormException(1,"表单不存在,表单名称='"+appName+"'");
		//建立列表
		ISeeyonForm form=fapp.findFromByName(formName);
		buttonList=form.getOperationSubmitList(operationName);
		
	}

}
