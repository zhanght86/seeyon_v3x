package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.HashMap;

public class TemplateObject implements Serializable {
	//新增
	private HashMap addMap;
	//flow节点的map
	private HashMap flowMap;
	private HashMap updateMap;
	private HashMap delMap;
	//使用的operation 
	private HashMap usedOperationMap;
	
	private boolean changed = false ;
    
	public HashMap getDelMap() {
		return delMap;
	}

	public void setDelMap(HashMap delMap) {
		this.delMap = delMap;
	}

	public HashMap getFlowMap() {
		return flowMap;
	}

	public void setFlowMap(HashMap flowMap) {
		this.flowMap = flowMap;
	}

	public HashMap getAddMap() {
		return addMap;
	}

	public void setAddMap(HashMap addMap) {
		this.addMap = addMap;
	}

	public HashMap getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(HashMap updateMap) {
		this.updateMap = updateMap;
	}

	public HashMap getUsedOperationMap() {
		return usedOperationMap;
	}

	public void setUsedOperationMap(HashMap usedOperationMap) {
		this.usedOperationMap = usedOperationMap;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
