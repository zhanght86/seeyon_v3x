/**
 * 
 */
package com.seeyon.v3x.main.section.panel;

import java.util.Map;



/**
 * @author dongyj
 * singleBoard Section Panel
 */
public abstract class BaseSectionPanel {
	
	/**
	 * 得到页签的名称
	 * @param property
	 * @return
	 */
	public Map<String,String> getName(String value){
		return doGetName(value);
	}
	
	/**
	 * 页签是否可用，如果已经删除，则不可用
	 * @param property
	 * @return
	 */
	public boolean isAllowUsed(String value){
		return true;
	}
	
	/**
	 * 子类实现之，显示页签名称
	 * @return
	 */
	protected abstract Map<String,String> doGetName(String value);
	
}
