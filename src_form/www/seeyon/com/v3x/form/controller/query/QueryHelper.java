package www.seeyon.com.v3x.form.controller.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.formservice.inf.IPageObjectCheck;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.IIP_InputObject;
import www.seeyon.com.v3x.form.manager.define.bind.auth.FormAppAuth;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.inf.IConditionList;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
/*
 * 此类用于查询条件设置的校验,如果校验不成功则抛出异常,成功返回true
 */
public class QueryHelper implements IPageObjectCheck{
	private String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private List<SeeyonFormException> exceptionList = new ArrayList<SeeyonFormException>();
	public List<SeeyonFormException> isMatch(SessionObject sessionobject) throws SeeyonFormException{
		if("true".equals(sessionobject.getOthersave()))
			return exceptionList;
		List<QueryObject> queryConditionList =  sessionobject.getQueryConditionList();
		if(queryConditionList.size() == 0)
			return exceptionList;
		//进行校验操作
		checkField(queryConditionList,sessionobject.getFormLst());
		return exceptionList;
	}
	
	public void updateQueryObject(List<QueryObject> list, List<String> delTablename, Map fieldMap, String deleteValue, String namespace) throws SeeyonFormException{
		//删除更新操作
		for(QueryObject qo : list){
			delField(qo, deleteValue, namespace);
			updateFieldName(qo, fieldMap, namespace);
		}
		
	}
	
	//更新字段名称
	/*
	 *  fieldMap  (旧字段名,新字段名)
	 *  namespace 名字空间
	 */
	private void updateFieldName(QueryObject qo, Map fieldMap, String namespace) throws SeeyonFormException{
		
		//如果有字段名更新
		if(fieldMap != null){ 
			if(!fieldMap.isEmpty()){
				//限制条件更新
				updateQueryArea(qo, fieldMap, namespace);
				//用户条件更新
				updateQueryCondition(qo, fieldMap, namespace);
				//用户自定义条件的更新
				updateCustomQuery(qo, fieldMap, namespace);
				//查询数据域更新
				updateDataField(qo, fieldMap, namespace);
				//查询结果排序更新
				updateResultSort(qo, fieldMap, namespace);
			}
		}
		
	}
	
	//删除不存在的字段
	/*
	 * deleteValue 删除字段（删除字段名1,删除字段2,删除字段名3）
	 */
	private void delField(QueryObject qo, String deleteValue, String namespace) throws SeeyonFormException{
		//如果有删除的字段
		if(deleteValue != null){ 
			if(!deleteValue.equals("")){
				String[] delFields = deleteValue.split(",");
				Map fieldMap = new HashMap();
				for(int i = 0; i < delFields.length; i++){
					fieldMap.put(delFields[i], "");
				}
			
				//限制条件删除
				updateQueryArea(qo, fieldMap, namespace);
				//用户条件删除
				updateQueryCondition(qo, fieldMap, namespace);
				//查询数据域删除
				updateDataField(qo, fieldMap, namespace);
				//数据域排序删除
				updateResultSort(qo, fieldMap, namespace);
			}
		}
	}
	
	

	//进行字段校验
	/*
	 * formList用于显示明细检验
	 */
	private void checkField(List<QueryObject> list, List<FormPage> formList) throws SeeyonFormException{
		exceptionList = new ArrayList<SeeyonFormException>();
		for(QueryObject qo : list){
			//限制条件校验
			checkQueryArea(qo);
			//用户条件校验
			checkQueryCondition(qo);
			//查询数据域校验
			checkDataField(qo);
		}
	
	}
	
	//去掉数据域名称前面的"my:"
	private String getColumName(String aDataColumName){
		if(aDataColumName == null)
			return null;
		int i = aDataColumName.indexOf(":");
		if(i != -1){
			return aDataColumName.substring(i + 1);
		}else 
			return aDataColumName;
	}
	
	//限制条件更新
	private void updateQueryArea(QueryObject qo, Map fieldMap, String namespace) throws SeeyonFormException{
		//还原成java对象
		
		Document queryAreaDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getQueryAreaValue()));
		Element queryAreaRoot = queryAreaDoc.getRootElement();
		ConditionListImpl filter = new ConditionListImpl();
		filter.loadFromXml(queryAreaRoot);
		List conditionList = filter.getConditionList();
	
		ICondition fitem;
		DataColumImpl dataColum = null;
		for(int i = 0; i < conditionList.size(); i++){
			fitem = (ICondition)conditionList.get(i);
			if(fitem instanceof DataColumImpl){//如果是数据字段
				dataColum = (DataColumImpl)fitem;
				String name = getColumName(dataColum.getColumName());
				if(name != null){//如果不是审批状态
					if(fieldMap.get(name)!= null){//如果是更新或修改字段
						if(!fieldMap.get(name).equals(""))
							dataColum.setColumName(namespace + fieldMap.get(name));
						else
							dataColum.setColumName("");
					}
				}
				
			}
		}
			
		qo.setQueryArea(filter.getDisplay());
		qo.setQueryAreaValue("<Filter>/r/n" + filter.getXML() + "</Filter>/r/n");
		
	}
	/**
	 * 更新infopath后修改自定义查询项的显示，
	 * 对其中的XML 的数据进行更新
	 * @param qo
	 * @param fieldMap
	 * @param namespace
	 * @throws SeeyonFormException
	 */
	private void updateCustomQuery(QueryObject qo, Map fieldMap, String namespace) throws SeeyonFormException{
		//还原成java对象
		if(Strings.isBlank(qo.getCustomQueryFieldValue())){
			return ;
		}
		Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getCustomQueryFieldValue()));
		if(QueryConditionDoc == null){
			return ;
		}
		Element cueryCustomRoot = QueryConditionDoc.getRootElement();
		if(cueryCustomRoot == null){
			return ;
		}
		List showColumNodes = cueryCustomRoot.selectNodes("ShowColum") ;
		StringBuffer customQueryField = new StringBuffer() ;
		boolean flag = false ;
		if(showColumNodes != null) {
			for(int i = 0 ; i < showColumNodes.size() ; i++ ){
				Element temp = (Element)showColumNodes.get(i) ;
				Attribute  att = temp.attribute("name") ;
				Attribute  title = temp.attribute("title") ;
				if(att != null && title != null){
					String dataFieldName = att.getValue();
					String name = getColumName(dataFieldName);					
					if(fieldMap.get(name) != null){
						if(!fieldMap.get(name).equals("")){
							if(name.equals(title.getValue())){
								title.setValue(fieldMap.get(name) + "") ;
							}							
							att.setValue(namespace + fieldMap.get(name)) ;
						}else{
					    	att.setValue("") ;
					     }
						flag = true ;
					}
					if(i==0){
						customQueryField.append(title.getValue()) ;
					}else{
						customQueryField.append(","+title.getValue()) ;
					}					
				}				
			}		
		}
		if(flag){
			qo.setCustomQueryField(customQueryField.toString()) ;
			qo.setCustomQueryFieldValue(cueryCustomRoot.asXML()) ;
		}
		
	}
	
	//用户条件更新
	private void updateQueryCondition(QueryObject qo, Map fieldMap, String namespace) throws SeeyonFormException{
	//还原成java对象
		Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getQueryConditionValue()));
		Element QueryConditionRoot = QueryConditionDoc.getRootElement();
		ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
		userConditionList.loadFromXml(QueryConditionRoot);
		List conditionList = userConditionList.getConditionList();
		ICondition fitem;
		DataColumImpl dataColum = null;
		for(int i = 0; i < conditionList.size(); i++){
			fitem = (ICondition)conditionList.get(i);
			if(fitem instanceof DataColumImpl){//如果是数据字段
				dataColum = (DataColumImpl)fitem;
				String name = getColumName(dataColum.getColumName());
				if(fieldMap.get(name)!= null){//如果是更新或修改字段
					if(!fieldMap.get(name).equals(""))
						dataColum.setColumName(namespace + fieldMap.get(name));
					else
						dataColum.setColumName("");
				}
			}
		}
			
		qo.setQueryCondition(userConditionList.getDisplay());
		qo.setQueryConditionValue("<UserConditionList>/r/n" + userConditionList.getXML() + "</UserConditionList>/r/n");
	}
	
	//查询数据域更新
	private void updateDataField(QueryObject qo, Map fieldMap, String namespace) throws SeeyonFormException{
		
		//查询数据域还原成java对象
		Document dataFieldDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getDataFieldValue()));
		Element dataFieldRoot = dataFieldDoc.getRootElement();
		List<QueryColum> dataColumList = new ArrayList<QueryColum>();
		List ShowColumList = dataFieldRoot.elements();
		
		
		for(Object item : ShowColumList){
			Element e = (Element)item;
			QueryColum queryColum = new QueryColum();
			queryColum.loadFromXml(e);
			dataColumList.add(queryColum);
		}
		//查询数据域值更新
		for(QueryColum qc : dataColumList){
			String name = getColumName(qc.getDataAreaName());
			if(fieldMap.get(name) != null){
				if(!fieldMap.get(name).equals(""))
					qc.setDataAreaName(namespace + fieldMap.get(name));
				else
					qc.setDataAreaName("");
			}
		}
		
		genDataField(qo, dataColumList);
	}
	
	//查询结果排序更新
	private void updateResultSort(QueryObject qo, Map fieldMap, String namespace){
	
		if(!qo.getResultSort().equals("")){
			String[] resultSortArray = qo.getResultSort().split(",");
			for(int i = 0; i < resultSortArray.length; i++){
				
				//字段名称
				String resultName = resultSortArray[i].substring(0, resultSortArray[i].length() - 1);
				//顺序or倒序
				String resultSign = resultSortArray[i].substring(resultSortArray[i].length() - 1 , resultSortArray[i].length());
				if(fieldMap.get(resultName) != null){
					resultSortArray[i] = fieldMap.get(resultName) + resultSign;
				}
			}
		
			genResultSort(qo, resultSortArray, namespace);
		}
	}
	
	//通过list为QueryObject的genDataField和genDataFieldValue赋值
	private void genDataField(QueryObject qo, List<QueryColum> list){
		
		//查询数据域显示值
		StringBuffer sbDataField = new StringBuffer();
		for(QueryColum queryColum : list){
			String columName = getColumName(queryColum.getDataAreaName());
			if(!columName.equals("")){
				//如果字段名和别名相同,只返回字段名
				if(columName.equals(queryColum.getColumTitle())){
					sbDataField.append(columName);
				}else{//否则返回 字段名(别名)
					sbDataField.append(columName);
					sbDataField.append("(");
					sbDataField.append(queryColum.getColumTitle());
					sbDataField.append(")");
				}
				sbDataField.append(",");	
			}
			
		}
		if(sbDataField.toString().length() == 0)
			qo.setDataField("");
		else
			qo.setDataField(sbDataField.toString().substring(0, sbDataField.toString().length() - 1));
		
		//查询数据域值
		StringBuffer sbDataFieldValue = new StringBuffer();
		sbDataFieldValue.append("<ShowDataList>/r/n");
		for(QueryColum qc : list){
			if(!qc.getDataAreaName().equals(""))
				sbDataFieldValue.append(qc.getXml());
		}
		sbDataFieldValue.append("</ShowDataList>/r/n");
		qo.setDataFieldValue(sbDataFieldValue.toString());
	}
	
	//通过string[]为resultSort的genResultSort和genResultSort赋值
	private void genResultSort(QueryObject qo, String[] str, String namespace){
		String[] resultSortArray = str;
		//查询结果排序显示值更新
		String strResultSort = "";
		for(int i = 0; i < resultSortArray.length; i++){
			if(!(resultSortArray[i].trim().equals("↑")) && (!resultSortArray[i].trim().equals("↓"))){
				strResultSort += resultSortArray[i];
				if(i != resultSortArray.length - 1)
					strResultSort += ",";
			}
			
		}
		qo.setResultSort(strResultSort);
		
		//查询结果排序值更新
		String strResultSortValue = "<OrderBy>/r/n";
		for(int i = 0; i < resultSortArray.length; i++){
			if((!resultSortArray[i].trim().equals("↑")) && (!resultSortArray[i].trim().equals("↓"))){
				//字段名称
				String resultName = resultSortArray[i].substring(0, resultSortArray[i].length() - 1);
				//顺序or倒序
				String resultSign = resultSortArray[i].substring(resultSortArray[i].length() - 1 , resultSortArray[i].length());
				if(resultSign.equals("↑"))
					//strResultSortValue +=  "<OrderColum name=\"" + namespace + resultName + "\"  type=\"0\" description=\"顺序\" />/r/n";
					strResultSortValue +=  "<OrderColum name=\"" + namespace + resultName + "\"  type=\"0\" description=\""+Constantform.getString4CurrentUser("form.query.order.label")+"\" />/r/n";
				if(resultSign.equals("↓"))
					//strResultSortValue +=  "<OrderColum name=\"" + namespace + resultName + "\"  type=\"1\" description=\"倒序\" />/r/n";
					strResultSortValue +=  "<OrderColum name=\"" + namespace + resultName + "\"  type=\"1\" description=\""+Constantform.getString4CurrentUser("form.query.reverseorder.label")+"\" />/r/n";
			}
			
		}
		qo.setResultSortValue(strResultSortValue + "</OrderBy>/r/n");
	}
	
	//限制条件校验
	private void checkQueryArea(QueryObject qo) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		Document queryAreaDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getQueryAreaValue()));
		Element queryAreaRoot = queryAreaDoc.getRootElement();
		ConditionListImpl filter = new ConditionListImpl();
		filter.loadFromXml(queryAreaRoot);
		List conditionList = filter.getConditionList();
			
		ICondition fitem;
		DataColumImpl dataColum = null;
		for(int i = 0; i < conditionList.size(); i++){
			fitem = (ICondition)conditionList.get(i);
			if(fitem instanceof DataColumImpl){//如果是数据字段
				dataColum = (DataColumImpl)fitem;
				String name = getColumName(dataColum.getColumName());
				if(name != null){//如果不是审批状态
					if(dataColum.getColumName().trim().equals("")){
						//e = new SeeyonFormException(1,qo.getQueryName() + "查询数据范围设置不正确!!");
						e = new SeeyonFormException(1,qo.getQueryName() + "   " + Constantform.getString4CurrentUser("form.query.dataareaseterror.label"));
						exceptionList.add(e);
						break;
					}
				}
				
			}
		}
	}
	
	//用户条件校验
	private void checkQueryCondition(QueryObject qo) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		
		Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(qo.getQueryConditionValue()));
		Element QueryConditionRoot = QueryConditionDoc.getRootElement();
		ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
		userConditionList.loadFromXml(QueryConditionRoot);
		List conditionList = userConditionList.getConditionList();
		ICondition fitem;
		DataColumImpl dataColum = null;
		for(int i = 0; i < conditionList.size(); i++){
			fitem = (ICondition)conditionList.get(i);
			if(fitem instanceof DataColumImpl){//如果是数据字段
				dataColum = (DataColumImpl)fitem;
				String name = getColumName(dataColum.getColumName());
				if(dataColum.getColumName().trim().equals("")){
					//e = new SeeyonFormException(2,qo.getQueryName() + "用户输入条件设置不正确!!");
					e = new SeeyonFormException(2,qo.getQueryName() + "   " +Constantform.getString4CurrentUser("form.query.conditionerror.label"));
					exceptionList.add(e);
					break;
				}
			}
		}
	}	
	
	//查询数据域校验
	private void checkDataField(QueryObject qo) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		if("".equals(qo.getDataField())){
			//e = new SeeyonFormException(3, qo.getQueryName() + "用户查询数据域设置不正确!!");
			e = new SeeyonFormException(3, qo.getQueryName() + "   " + Constantform.getString4CurrentUser("form.query.datafielderror.label"));
			exceptionList.add(e);
		}
		if("".equals(qo.getDataFieldValue())){
			e = new SeeyonFormException(3, qo.getQueryName() + "   " + Constantform.getString4CurrentUser("form.query.datafielderror.label"));
			exceptionList.add(e);
		}
				
	}
		
	//去掉字符串最后的/r/n
	private String delTrailSection(String str){
		
		if(str.endsWith("/r/n"))
			return str.substring(0,str.length() - 4);
		else
			return str;
			
		
	}
	
	//用于查询列表的显示表单名称
	public List<String> genFormNameList(List<ISeeyonQuery> list){
		List<String> formNameList = new ArrayList<String>();
		Map map = new HashMap();
		for(ISeeyonQuery query : list){
			String formName = query.getOwnerApp().getAppName();
			if(map.get(formName) == null){
				map.put(formName, "");
			}
		}
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){   
			Map.Entry entry = (Map.Entry)it.next();   
			String name = (String)entry.getKey();
			formNameList.add(name);
		}
		return formNameList;
	}
	
	/**
	 * 往excel中添加数据
	 * QueryResultImpl resultData 记录集
	 * String queryname 查询名称
	 */
	public DataRecord exportQueryForExcel(String formName,String queryname, List<QueryColum> dataColumList,
			Map<String,String> displayConditions, List<List<String>> resultDatas,Map<String,FormField> formFieldMap) throws SeeyonFormException{
		DataRecord dataRecord = new DataRecord();
		StringBuilder subTitle = new StringBuilder();
		subTitle.append(Constantform.getString4CurrentUser("form.base.formname.label")).append(":").append(formName).append("\r\n");
		subTitle.append(Constantform.getString4CurrentUser("form.query.querydate")).append(":").append(Datetimes.formatDate(new Date())).append("\r\n\r\n");
		int index = 0;
		for(Map.Entry<String, String> entry : displayConditions.entrySet()){
			if(index % 3 == 0 && index != 0){
				subTitle.append("\r\n");
			}
			subTitle.append(entry.getKey()).append(":").append(entry.getValue()).append("        ");
			index++;
		}
		if(subTitle.length() > 0){
			dataRecord.setSubTitle(subTitle.toString());			
		}
		
		String[] columnName = new String[dataColumList.size()];
        index = 0;
        String colName = null;
        for(QueryColum queryColum : dataColumList){
            colName = queryColum.getColumTitle();
            if(SeeyonFormPojo.C_sFieldNames.contains(colName))
                colName = Constantform.getString4OtherKey(colName);
            columnName[index++] = colName;
            if(!formFieldMap.containsKey(colName)){
                String dataAreaName = queryColum.getDataAreaName();
                if(dataAreaName != null){
                    if(dataAreaName.contains("my:")){
                        dataAreaName = dataAreaName.substring(3);
                    }
                    if(formFieldMap.containsKey(dataAreaName)){
                        formFieldMap.put(colName, formFieldMap.get(dataAreaName));
                    }
                }
            }
        }
        
		if(resultDatas != null){
			DataRow[] datarow = new DataRow[resultDatas.size()];
			DataRow row = new DataRow();
			for(int i = 0; i < resultDatas.size(); i++){
				row = new DataRow();
				List<String> datas = resultDatas.get(i);
				for(int j = 0; j < datas.size(); j++){	
				    String data = datas.get(j);
				    FormField formField = formFieldMap.get(columnName[j]);
					if(formField != null && "DECIMAL".equalsIgnoreCase(formField.getFieldtype())){
					    if(Strings.isNotBlank(data) && data.contains(",")){
					        data = data.replaceAll(",", "");
					    }
					    if(formField.getFieldlength().contains(",")){
					        row.addDataCell(data, DataCell.DATA_TYPE_NUMERIC);
					    }else{
					        row.addDataCell(data, DataCell.DATA_TYPE_INTEGER);
					    }
					} else {
						row.addDataCell(data, DataCell.DATA_TYPE_TEXT);
					}
				}
				datarow[i] = row;
			}
			dataRecord.addDataRow(datarow);
		}
		
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(queryname);
		dataRecord.setSheetName(queryname);
		return dataRecord;
	}
	
	private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
	private boolean isInteger(String str) {
		str = str.trim();
	    try {
	        Integer.parseInt(str);
	        return true;
	    }
	    catch (NumberFormatException ex) {
	    	if (str.startsWith("+")) {
	    		return isInteger(str.substring(1));
	    	}
	    	return false;
		}
	}



	
	public void getPurview(Long appmainId, Long userId) throws SeeyonFormException{
		IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(appmainId);
		if(afapp!=null){
			List<FormAppAuth> appAuthList = afapp.getFormAppAuthList();
			String userIds = com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser(userId);
			Set<Long> userIdSet = com.seeyon.v3x.doc.util.Constants.parseStrings2Longs(userIds, ",");
			List<Long> domainIds = new ArrayList<Long>(userIdSet);
			List<FomObjaccess> formObjList= iOperBase.getFormQueryOrReportNamesByAppId4User(appmainId, domainIds, false);
			for (FomObjaccess foa : formObjList) {
				FormAppAuth appAuth = afapp.findFormAppAuthByName(foa.getObjectname());
				SeeyonQueryImpl query = (SeeyonQueryImpl)appAuth.getQuery();
				List<QueryColum> dataColList = query.getDataColumList();
				IConditionList filter = query.getFilter();
				
			}
		}
	}
}
