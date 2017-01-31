package com.seeyon.v3x.mobile.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;

import com.seeyon.v3x.mobile.MobileException;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.util.Strings;

/**
 * 取出表单后，对表单字段进行解析
 * @author 董亚杰
 *
 */
public class MobileFormBean {
	private static final Log logger = LogFactory.getLog(MobileFormBean.class);
	//普通的表单字段
	private Map<String,TIP_InputValueAll> fromApp = null;
	
	//是否含有子表单
	private Boolean hasChildForm = null;
	
	private Map<String,List<List<TIP_InputValueAll>>> childFormApp = null;//子表单
	
	private Map<String,List<String>> childFormNames = null;//子表单标头
	
	private Boolean isContainCalculate = null; //是否含有计算字段
	
	private Boolean isContainExtend = null;//需要填写的扩展字段
	
	private Boolean containMapMark = null;//需要填写的签章
	
	private Boolean childFormEdit = null; //是否存在可编辑的子表单，手机暂不支持此操作，给用户提示
	
	private boolean isContainMustWrite = false;//是否含有必填项，且必填项现在为空
	
	private Map<String, List<Map<String, String>>> dataMap;//表单实际数据库值
	
	public Map<String, List<Map<String, String>>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, List<Map<String, String>>> dataMap) {
        this.dataMap = dataMap;
    }

    public Boolean getChildFormEdit() {
		if(childFormEdit == null)
			return false;
		return childFormEdit;
	}

	public MobileFormBean() throws MobileException{
	    fromApp = new LinkedHashMap<String, TIP_InputValueAll>();
        childFormNames = new LinkedHashMap<String, List<String>>();
        childFormApp = new LinkedHashMap<String, List<List<TIP_InputValueAll>>>();
        dataMap = null;
	}
	public MobileFormBean(Map<String, List<Map<String, String>>> dataMap) throws MobileException{
        fromApp = new LinkedHashMap<String, TIP_InputValueAll>();
        childFormNames = new LinkedHashMap<String, List<String>>();
        childFormApp = new LinkedHashMap<String, List<List<TIP_InputValueAll>>>();
        this.dataMap = dataMap;
    }
	/**
	 * 处理各个属性(由构造函数出迁移出来)
	 * @param formList
	 * @param isWap
	 * @throws MobileException
	 */
	public void handle(Map<String,Object> formList,boolean isWap)throws MobileException{
	    if(formList == null){
            throw new MobileException(MobileConstants.getValueFromMobileRes("form.data.hasdelete"));
        }
        if(formList.isEmpty()){
            throw new MobileException(MobileConstants.getValueFromMobileRes("form.data.exception"));
        }
        Set<String> keys = formList.keySet();
        try {
            for(String key : keys){
                Object inValue = formList.get(key);
                if(inValue instanceof TIP_InputValueAll){
                    TIP_InputValueAll tipValue = (TIP_InputValueAll) inValue;
                    String dbValue = null;
                    if(dataMap != null && !dataMap.isEmpty()){
                        dbValue = dataMap.get("main").get(0).get(key);
                    }
                    //校验是否为初始值设置
                    if("edit".equals(tipValue.getAccess()) && (!isWap&&(Strings.isBlank(dbValue)) || (isWap&&Strings.isBlank(tipValue.getValue())))){
                        if(TFieldInputType.fitExtend.equals(tipValue.getType()) && isContainExtend == null){
                            isContainExtend =true;
                        }
                        if((TFieldInputType.fitComboedit.equals(tipValue.getType()) || Strings.isNotBlank(tipValue.getStageCalculateXml())) && isContainCalculate == null){
                            //扩展字段已经有人填写。则可以让处理人继续处理
                            isContainCalculate =true;
                        }
                        if(TFieldInputType.fitHandwrite.equals(tipValue.getType()) && containMapMark == null){
                            containMapMark =true;
                        }
                        if(!tipValue.isIs_null()){
                            isContainMustWrite = true;
                        }
                    }
                    fromApp.put(key, tipValue);
                }else if(inValue instanceof List){//子表单
                    List<List<TIP_InputValueAll>> row = (List<List<TIP_InputValueAll>>) inValue;
                    if(row != null && row.size() > 0){
                        List<String> columnName = new ArrayList<String>();
                        for (int i = 0; i < row.size(); i++) {
                            List<TIP_InputValueAll> column = row.get(i);
                            for(TIP_InputValueAll tip : column){
                                if(i == 0){
                                    columnName.add(tip.getName());
                                    if("edit".equals(tip.getAccess())){
                                        childFormEdit = true;
                                    }
                                }
                                String dbValue = null;
                                boolean isBlank = false;
                                if(dataMap != null && !dataMap.isEmpty()){
                                    Iterator<Entry<String, List<Map<String, String>>>> it =  dataMap.entrySet().iterator();
                                    jump:
                                    while(it.hasNext()){
                                        Entry<String, List<Map<String, String>>> entry = it.next();
                                        if(!entry.getKey().equals("main")){
                                            List<Map<String, String>> list = entry.getValue();
                                            for(Map<String, String> map:list){
                                                dbValue = map.get(tip.getName());
                                                if(Strings.isBlank(dbValue)){
                                                    isBlank = true;
                                                    break jump;
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    if(Strings.isBlank(tip.getValue())){
                                        isBlank = true;
                                    }
                                }

                                if("edit".equals(tip.getAccess()) && isBlank){
                                    if(!tip.isIs_null()){
                                        isContainMustWrite = true;
                                    }
                                }
                            }
                        }
                        hasChildForm = true;
                        childFormNames.put(key, columnName);
                        childFormApp.put(key, row);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("读取表单异常",e);
            throw new MobileException(MobileConstants.getValueFromMobileRes("form.data.exception"));
        }
	}
	public Map<String, List<List<TIP_InputValueAll>>> getChildFormApp() {
		return childFormApp;
	}

	public Map<String, List<String>> getChildFormNames() {
		return childFormNames;
	}

	public Boolean getContainMapMark() {
		if(containMapMark == null)
			return false;
		return containMapMark;
	}

	public Map<String, TIP_InputValueAll> getFromApp() {
		return fromApp;
	}

	public Boolean getHasChildForm() {
		if(hasChildForm == null) 
			return false;
		return hasChildForm;
	}

	public Boolean getIsContainCalculate() {
		if(isContainCalculate == null){
			return false;
		}
		return isContainCalculate;
	}

	public Boolean getIsContainExtend() {
		if(isContainExtend == null) 
			return false;
		return isContainExtend;
	}

	public boolean isContainMustWrite() {
		return isContainMustWrite;
	}
}
