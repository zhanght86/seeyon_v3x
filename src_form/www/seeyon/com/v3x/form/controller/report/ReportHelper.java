package www.seeyon.com.v3x.form.controller.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IPageObjectCheck;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.ReportDataColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportHeadColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportSchema;
import www.seeyon.com.v3x.form.manager.define.report.ReportSumDataColum;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.inf.IReportResult;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ReportHelper implements IPageObjectCheck{
	private String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static Log log = LogFactory.getLog(ReportHelper.class);
	private List<SeeyonFormException> exceptionList = new ArrayList<SeeyonFormException>();
	public List<SeeyonFormException> isMatch(SessionObject sessionobject) throws SeeyonFormException{
		if("true".equals(sessionobject.getOthersave()))
			return exceptionList;
		List<ReportObject> ReportConditionList =  sessionobject.getReportConditionList();
		if(ReportConditionList.size() == 0)
			return exceptionList;
		//进行校验操作
		checkField(ReportConditionList,sessionobject.getTableFieldList(), sessionobject.getFormLst());
		return exceptionList;
	}
	
	public void updateReportObject(List<ReportObject> list, List<String> delTablename, Map fieldMap, String deleteValue, String namespace) throws SeeyonFormException{
		//删除更新操作
		for(ReportObject qo : list){
			delField(qo, deleteValue, namespace);
			updateFieldName(qo, fieldMap, namespace);
		}
		
	}
	
	
	
	
	//更新字段名称
	/*
	 *  fieldMap  (旧字段名,新字段名)
	 *  namespace 名字空间
	 */
	private void updateFieldName(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		
		//如果有字段名更新
		if(fieldMap != null){ 
			if(!fieldMap.isEmpty()){
				//限制条件更新
				updateReportArea(ro, fieldMap, namespace);
				//用户条件更新
				updateReportCondition(ro, fieldMap, namespace);
				//自定义统计的更新
				updateCustomReport(ro, fieldMap, namespace);
				//行头 列头更新
				updateHeader(ro, fieldMap, namespace);
				//查询数据域更新
				updateDataField(ro, fieldMap, namespace);
				//合计数据域更新
				updateSumDataField(ro, fieldMap, namespace);
				
			}
		}
		
	}
	
	//删除不存在的字段
	/*
	 * deleteValue 删除字段（删除字段名1,删除字段2,删除字段名3）
	 */
	private void delField(ReportObject ro, String deleteValue, String namespace) throws SeeyonFormException{
		//如果有删除的字段
		if(deleteValue != null){ 
			if(!deleteValue.equals("")){
				String[] delFields = deleteValue.split(",");
				Map fieldMap = new HashMap();
				for(int i = 0; i < delFields.length; i++){
					fieldMap.put(delFields[i], "");
				}
			
				//限制条件删除
				updateReportArea(ro, fieldMap, namespace);
				//用户条件删除
				updateReportCondition(ro, fieldMap, namespace);
				//行头 列头删除
				updateHeader(ro, fieldMap, namespace);
				//查询数据域删除
				updateDataField(ro, fieldMap, namespace);
				//合计数据域更新
				updateSumDataField(ro, fieldMap, namespace);
				
			}
		}
	}
	
	

	//进行字段校验
	private void checkField(List<ReportObject> list, List<TableFieldDisplay> fieldList, List<FormPage> formList) throws SeeyonFormException{
		exceptionList = new ArrayList<SeeyonFormException>();
		for(ReportObject ro : list){
			//限制条件校验
			checkReportArea(ro);
			//用户条件校验
			checkReportCondition(ro);
			//行头列头校验
			checkHeader(ro);
			//查询数据域校验
			checkDataField(ro, fieldList);
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
	private void updateReportArea(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		//还原成java对象
		
		Document ReportAreaDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getReportAreaValue()));
		Element ReportAreaRoot = ReportAreaDoc.getRootElement();
		ConditionListImpl filter = new ConditionListImpl();
		filter.loadFromXml(ReportAreaRoot);
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
			
		ro.setReportArea(filter.getDisplay());
		ro.setReportAreaValue("<Filter>/r/n" + filter.getXML() + "</Filter>/r/n");
		
	}
/**
 * 更新infopath后修改自定义统计项的显示，
 * 对其中的XML 的数据进行更新
 * @param ro
 * @param fieldMap
 * @param namespace
 * @throws SeeyonFormException
 */
	private void updateCustomReport(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		//还原成java对象
		if(Strings.isBlank(ro.getCustomReportFieldValue())){
			return ;
		}
		Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getCustomReportFieldValue()));
		if(QueryConditionDoc == null){
			return ;
		}
		Element cueryCustomRoot = QueryConditionDoc.getRootElement();
		if(cueryCustomRoot == null){
			return ;
		}
		List showColumNodes = cueryCustomRoot.selectNodes("ShowColum") ;
		StringBuffer customReportField = new StringBuffer() ;
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
						customReportField.append(title.getValue()) ;
					}else{
						customReportField.append(","+title.getValue()) ;
					}
				}				
			}
			if(flag){
				ro.setCustomReportField(customReportField.toString());
				ro.setCustomReportFieldValue(cueryCustomRoot.asXML()) ;
			}
			
		}		
	}
	
	//用户条件更新
	private void updateReportCondition(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
	//还原成java对象
		Document ReportConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getconditionvalue()));
		Element ReportConditionRoot = ReportConditionDoc.getRootElement();
		ConditionListReportImpl userConditionList = new ConditionListReportImpl();
		userConditionList.loadFromXml(ReportConditionRoot);
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
			
		ro.setCondition(userConditionList.getDisplay());
		ro.setconditionvalue("<UserConditionList>/r/n" + userConditionList.getXML() + "</UserConditionList>/r/n");
	}
	
	//更新交叉报表的行头 列头
	private void updateHeader(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		String str = null;
		if(ro.getReportType().equals("1")){//如果是交叉表
			str = "<ReportHead acrossreport=\"true\">/r/n" + ro.getColumnheadValue()
			            + ro.getRowheadValue() + "</ReportHead>";
			Document headerDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + str);
			Element headRoot = headerDoc.getRootElement();
			ReportSchema schema = new ReportSchema();
			schema.loadFromXml(headRoot);
			List<ReportHeadColum> rowHeadList = schema.getRowHeadList();
			ReportHeadColum colHead = schema.getColHead();
			if(colHead != null)
				rowHeadList.add(colHead);//行头列头一起更新
			for(ReportHeadColum rhc : rowHeadList){
				String name = getColumName(rhc.getDataAreaName());
				if(fieldMap.get(name) != null){
					if(!fieldMap.get(name).equals(""))
						rhc.setDataAreaName(namespace + fieldMap.get(name));
					else
						rhc.setDataAreaName("");
				}
			}
			if(colHead != null)
				rowHeadList.remove(rowHeadList.size() - 1);
			genHeader(ro, rowHeadList, colHead);
		}else if(ro.getReportType().equals("0")){
			str = "<ReportHead acrossreport=\"false\">/r/n" + ro.getRowheadValue() + "</ReportHead>";
			Document headerDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + str);
			Element headRoot = headerDoc.getRootElement();
			ReportSchema schema = new ReportSchema();
			schema.loadFromXml(headRoot);
			List<ReportHeadColum> rowHeadList = schema.getRowHeadList();
			for(ReportHeadColum rhc : rowHeadList){
				String name = getColumName(rhc.getDataAreaName());
				if(fieldMap.get(name) != null){
					if(!fieldMap.get(name).equals(""))
						rhc.setDataAreaName(namespace + fieldMap.get(name));
					else
						rhc.setDataAreaName("");
				}
			}
			genHeader(ro, rowHeadList, null);
		}
	}
	
	//查询数据域更新
	private void updateDataField(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		
		//查询数据域还原成java对象
		Document dataFieldDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getReportDataFieldValue()));
		Element dataFieldRoot = dataFieldDoc.getRootElement();
		List<ReportDataColum> dataColumList = new ArrayList<ReportDataColum>();
		List ShowColumList = dataFieldRoot.elements();
		
		
		for(Object item : ShowColumList){
			Element e = (Element)item;
			ReportDataColum reportColum = new ReportDataColum();
			reportColum.loadFromXml(e);
			dataColumList.add(reportColum);
		}
		//查询数据域值更新
		for(ReportDataColum rdc : dataColumList){
			String name = getColumName(rdc.getDataAreaName());
			if(fieldMap.get(name) != null){
				if(!fieldMap.get(name).equals(""))
					rdc.setDataAreaName(namespace + fieldMap.get(name));
				else
					rdc.setDataAreaName("");
			}
		}
		
		genDataField(ro, dataColumList);
	}
	
	//通过list为ReportObject的genDataField和genDataFieldValue赋值
	private void genDataField(ReportObject ro, List<ReportDataColum> list){
		
		//查询数据域显示值
		StringBuffer sbDataField = new StringBuffer();
		for(ReportDataColum rdc : list){
			String columName = getColumName(rdc.getDataAreaName());
			if(!columName.equals("")){
				//如果字段名和别名相同,只返回字段名
				if(columName.equals(rdc.getColumTitle())){
					sbDataField.append(columName);
				}else{//否则返回 字段名(别名)
					sbDataField.append(columName);
					sbDataField.append("(");
					sbDataField.append(rdc.getColumTitle());
					sbDataField.append(")");
				}
				sbDataField.append(" ");
				sbDataField.append(ReportDataColum.calctype2Dispy(rdc.getCalctype()));
				sbDataField.append(",");	
			}
			
		}
		if(sbDataField.toString().length() == 0)
			ro.setReportDataField("");
		else
			ro.setReportDataField(sbDataField.toString().substring(0, sbDataField.toString().length() - 1));
		
		//查询数据域值
		StringBuffer sbDataFieldValue = new StringBuffer();
		sbDataFieldValue.append("<ShowDataList>/r/n");
		for(ReportDataColum rdc : list){
			if(!rdc.getDataAreaName().equals(""))
				sbDataFieldValue.append(rdc.getXml());
		}
		sbDataFieldValue.append("</ShowDataList>/r/n");
		ro.setReportDataFieldValue(sbDataFieldValue.toString());
	}
	
	
	//合计数据域更新
	private void updateSumDataField(ReportObject ro, Map fieldMap, String namespace) throws SeeyonFormException{
		
		//查询数据域还原成java对象
		Document dataFieldDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getSumDataFieldValue()));
		Element dataFieldRoot = dataFieldDoc.getRootElement();
		List<ReportSumDataColum> dataColumList = new ArrayList<ReportSumDataColum>();
		List sumColumList = dataFieldRoot.elements();
		
		if((sumColumList != null) && (sumColumList.size() != 0)){
			for(Object item : sumColumList){
				Element e = (Element)item;
				ReportSumDataColum reportColum = new ReportSumDataColum();
				reportColum.loadFromXml(e);
				dataColumList.add(reportColum);
			}
			//查询数据域值更新
			for(ReportSumDataColum rsdc : dataColumList){
				String name = getColumName(rsdc.getDataAreaName());
				if(fieldMap.get(name) != null){
					if(!fieldMap.get(name).equals(""))
						rsdc.setDataAreaName(namespace + fieldMap.get(name));
					else
						rsdc.setDataAreaName("");
				}
			}
			
			genSumDataField(ro, dataColumList);
		}else{
			ro.setSumDataField("");
			ro.setSumDataFieldValue("<SumDataList>/r/n</SumDataList>/r/n");
		}
	}
	
//	通过list为ReportObject的genSumDataField和genSumDataFieldValue赋值
	private void genSumDataField(ReportObject ro, List<ReportSumDataColum> list){
		
		//查询数据域显示值
		StringBuffer sbDataField = new StringBuffer();
		for(ReportSumDataColum rsdc : list){
			String columName = getColumName(rsdc.getDataAreaName());
			if(!columName.equals("")){
				//如果字段名和别名相同,只返回字段名
				if(columName.equals(rsdc.getColumTitle())){
					sbDataField.append(columName);
				}else{//否则返回 字段名(别名)
					sbDataField.append(columName);
					sbDataField.append("(");
					sbDataField.append(rsdc.getColumTitle());
					sbDataField.append(")");
				}
				sbDataField.append(" ");
				sbDataField.append(ReportDataColum.calctype2Dispy(rsdc.getCalctype()));
				sbDataField.append(",");	
			}
			
		}
		if(sbDataField.toString().length() == 0)
			ro.setReportDataField("");
		else
			ro.setReportDataField(sbDataField.toString().substring(0, sbDataField.toString().length() - 1));
		
		//查询数据域值
		StringBuffer sbDataFieldValue = new StringBuffer();
		sbDataFieldValue.append("<SumDataList>/r/n");
		for(ReportSumDataColum rsdc : list){
			if(!rsdc.getDataAreaName().equals(""))
				sbDataFieldValue.append(rsdc.getXml());
		}
		sbDataFieldValue.append("</SumDataList>/r/n");
		ro.setSumDataFieldValue(sbDataFieldValue.toString());
	}
	
	/*
	 * 生成行头和列头
	 * list 行头
	 * colHead 列头
	 */
	private void genHeader(ReportObject ro, List<ReportHeadColum> list, ReportHeadColum colHead){
		StringBuffer sbRowHeader = new StringBuffer();
		StringBuffer sbRowHeaderValue = new StringBuffer();
		//行头显示值
		
		for(ReportHeadColum rhc : list){
			String columName = getColumName(rhc.getDataAreaName());
			if(!columName.equals("")){
				//如果字段名和别名相同,只返回字段名
				if(columName.equals(rhc.getColumTitle())){
					sbRowHeader.append(columName);
				}else{//否则返回 字段名(别名)
					sbRowHeader.append(columName);
					sbRowHeader.append("(");
					sbRowHeader.append(rhc.getColumTitle());
					sbRowHeader.append(")");
				}
				sbRowHeader.append(",");	
			}
			
		}
		if(sbRowHeader.toString().length() == 0)
			ro.setRowHeader("");
		else
			ro.setRowHeader(sbRowHeader.toString().substring(0,sbRowHeader.toString().length() - 1));
		
		//行头数据域值
		for(ReportHeadColum rhc : list){
			if(!rhc.getDataAreaName().equals(""))
				sbRowHeaderValue.append(rhc.getXml());
		}
		ro.setRowheadValue(sbRowHeaderValue.toString());
		
		//列头显示值
		if(colHead != null){
			if(!getColumName(colHead.getDataAreaName()).equals("")){
				ro.setColumnHeader(getColumName(colHead.getDataAreaName()));
				ro.setColumnheadValue(colHead.getXml());
			}else{
				ro.setColumnHeader("");
				ro.setColumnheadValue("");
			}
		}else{
			ro.setColumnHeader("");
			ro.setColumnheadValue("");
		}
		
		
			
		
		
	}
	
	//限制条件校验
	private void checkReportArea(ReportObject ro) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		Document ReportAreaDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getReportAreaValue()));
		Element ReportAreaRoot = ReportAreaDoc.getRootElement();
		ConditionListImpl filter = new ConditionListImpl();
		filter.loadFromXml(ReportAreaRoot);
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
						//e = new SeeyonFormException(1,ro.getReportName() + " 统计数据范围设置不正确!!");
						e = new SeeyonFormException(1,ro.getReportName() + " " + Constantform.getString4CurrentUser("form.stat.dataareaerror.label"));
						exceptionList.add(e);
						break;
					}
				}
				
			}
		}
	}
	
	//用户条件校验
	private void checkReportCondition(ReportObject ro) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		
		Document ReportConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getconditionvalue()));
		Element ReportConditionRoot = ReportConditionDoc.getRootElement();
		ConditionListReportImpl userConditionList = new ConditionListReportImpl();
		userConditionList.loadFromXml(ReportConditionRoot);
		List conditionList = userConditionList.getConditionList();
		ICondition fitem;
		DataColumImpl dataColum = null;
		for(int i = 0; i < conditionList.size(); i++){
			fitem = (ICondition)conditionList.get(i);
			if(fitem instanceof DataColumImpl){//如果是数据字段
				dataColum = (DataColumImpl)fitem;
				String name = getColumName(dataColum.getColumName());
				if(dataColum.getColumName().trim().equals("")){
					//e = new SeeyonFormException(2,ro.getReportName() + " 用户输入条件设置不正确!!");
					e = new SeeyonFormException(2,ro.getReportName() + "   " + Constantform.getString4CurrentUser("form.query.conditionerror.label"));
					exceptionList.add(e);
					break;
				}
			}
		}
	}
	
	//行头列头校验
	private void checkHeader(ReportObject ro) throws SeeyonFormException{
		SeeyonFormException e;
		
		if(ro.getReportType().equals("1")){
			if(ro.getRowHeader().equals("")){
				//e = new SeeyonFormException(4,ro.getReportName() + "交叉表行头设置不正确!!");
				e = new SeeyonFormException(4,ro.getReportName() + "   " + Constantform.getString4CurrentUser("form.stat.rowheadererror.label")); 
				exceptionList.add(e);
				
			}
			if(ro.getColumnHeader().equals("")){
				//e = new SeeyonFormException(4,ro.getReportName() + "交叉表列头设置不正确!!");
				e = new SeeyonFormException(4,ro.getReportName()+ "   " + Constantform.getString4CurrentUser("form.stat.columnheadererror.label")); 
				exceptionList.add(e);
				
			}
			
		}else if(ro.getReportType().equals("0")){
			if(ro.getRowHeader().equals("")){
				//e = new SeeyonFormException(4,ro.getReportName() + "   " + Constantform.getString4CurrentUser("表行头设置不正确"));
				e = new SeeyonFormException(4,ro.getReportName() + "   " + Constantform.getString4CurrentUser("form.stat.commonrowheadererror.label")); 
				exceptionList.add(e);
				
			}
		}
		
	}
	
	//查询数据域校验
	/*
	 * fieldList 用于统计字段类型校验
	 */
	private void checkDataField(ReportObject ro, List<TableFieldDisplay> fieldList) throws SeeyonFormException{
		SeeyonFormException e;
		//还原成java对象
		if(ro.getReportDataField().equals("")){
			//e = new SeeyonFormException(5, ro.getReportName() + "用户统计数据域设置不正确!!");
			e = new SeeyonFormException(5, ro.getReportName() + "   " + Constantform.getString4CurrentUser("form.stat.datafielderror.label"));
			exceptionList.add(e);
		}else{
			Document dataFieldDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead + delTrailSection(ro.getReportDataFieldValue()));
			Element dataFieldRoot = dataFieldDoc.getRootElement();
			List<ReportDataColum> dataColumList = new ArrayList<ReportDataColum>();
			List ShowColumList = dataFieldRoot.elements();
			for(Object item : ShowColumList){
				Element element = (Element)item;
				ReportDataColum reportColum = new ReportDataColum();
				reportColum.loadFromXml(element);
				dataColumList.add(reportColum);
			}
			if(!checkField(dataColumList, fieldList)){
				//e = new SeeyonFormException(5, ro.getReportName() + "用户统计数据域设置不正确!!");
				e = new SeeyonFormException(5, ro.getReportName()+"   " + Constantform.getString4CurrentUser("form.stat.datafielderror.label"));
				exceptionList.add(e);
			}
		}
		
		
	}	
	
	
	
	//去掉字符串最后的/r/n
	private String delTrailSection(String str){
		if(str.endsWith("/r/n"))
			return str.substring(0,str.length() - 4);
		else
			return str;
	}
	
	/*
	 * 用于检验字段类型,全部通过返回true,否则false
	 */
	private boolean checkField(List<ReportDataColum> list, List<TableFieldDisplay> fieldList){
		String fieldType;
		for(ReportDataColum rdc : list){
			for(TableFieldDisplay tfd : fieldList){
				if(rdc.getDataAreaName().equals(tfd.getBindname())){//如果名字匹配
					fieldType = tfd.getFieldtype();//取得字段类型
					if(rdc.getCalctype() != 1){//如果不是计数
						if(!fieldType.equals(IPagePublicParam.DECIMAL)){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
		
	//用于查询列表的显示表单名称
	public List<String> genFormNameList(List<ISeeyonReport> list){
		List<String> formNameList = new ArrayList<String>();
		Map map = new HashMap();
		for(ISeeyonReport report : list){
			String formName = ((SeeyonReportImpl)report).getOwnerApp().getAppName();
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
	public DataRecord exportReportForExcel(Long formid, String formName,String reportname, Map<String,String> displayConditions, List resultList) throws SeeyonFormException{
		DataRecord dataRecord = new DataRecord();
		StringBuilder subTitle = new StringBuilder();
		subTitle.append(Constantform.getString4CurrentUser("form.base.formname.label")).append(":").append(formName).append("\r\n");
		subTitle.append(Constantform.getString4CurrentUser("form.stat.statdate")).append(":").append(Datetimes.formatDate(new Date())).append("\r\n\r\n");

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
		String[] columnNames = null;
		if(resultList != null && resultList.size() > 0){
			IReportResult resultData = (IReportResult)resultList.get(0);
			columnNames = new String[resultData.getColCount()];
//			int[] typeAry = null;
			for(int m = 0; m < resultData.getRowCount(); m++){
				DataRow row = new DataRow();
				for(int n = 0; n < resultData.getColCount(); n++){
					if(resultData.getCell(m,n) == null){
						row.addDataCell(" ", DataCell.DATA_TYPE_BLANK);
					}else{
						if(m==0){
							row.addDataCell(resultData.getCell(m,n).getShowString(), DataCell.DATA_TYPE_TEXT);
						}else{
						    String value = resultData.getCell(m,n).getShowString();
						    if(Strings.isNotBlank(value) && value.contains(",")){
						        value = value.replaceAll(",", "");
						    }
							row.addDataCell(value, getDataType(value));
						}
					}
				}
				dataRecord.addDataRow(row);
//				if(m==0) typeAry = getFieldType(formid,row);
			}
		}
		if(resultList != null && resultList.size() > 1){
			String[] sumDataField = (String[])resultList.get(1);
			DataRow row = new DataRow();
			for(int i = 0; sumDataField != null && i < sumDataField.length; i++){
				if(sumDataField[i] == null)
					row.addDataCell(" ", DataCell.DATA_TYPE_BLANK);
				else{
				    String value = sumDataField[i];
                    if(Strings.isNotBlank(value) && value.contains(",")){
                        value = value.replaceAll(",", "");
                    }
				    row.addDataCell(value, getDataType(value));
				}
			}
			dataRecord.addDataRow(row);
		}

		dataRecord.setColumnName(columnNames);
		dataRecord.setTitle(reportname);
		dataRecord.setSheetName(reportname);
		return dataRecord;
	}
	
	private int	getDataType(String data){
		if(StringUtils.isBlank(data))
			return DataCell.DATA_TYPE_BLANK;
		if("0".equals(data.charAt(0))){
		    return DataCell.DATA_TYPE_TEXT;
		}
		if(data.matches("-?[\\d]+") && data.length()<12)
			return DataCell.DATA_TYPE_INTEGER;
		if(data.matches("-?[\\d.]+") && data.length()<12)
			return DataCell.DATA_TYPE_NUMERIC;
		
		return DataCell.DATA_TYPE_TEXT;
	}
	
	private int[] getFieldType(Long formid,DataRow row){
		int[] typeAry = new int[row.getCell().length];
		Map<String,String> fieldTypeMap = new HashMap<String, String>();
		if(formid!=null){
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formid);
			SeeyonDataDefine seedade = (SeeyonDataDefine) fapp.getDataDefine();
			if(seedade!=null){
				List<FormTable> ftList = seedade.getDataDefine().getTableLst();
				if(ftList!=null){
					for (FormTable formTable : ftList) {
						List<FormField> ffList = formTable.getFieldLst();
						if(ffList!=null){
							for (FormField ff : ffList) {
								if("DECIMAL".equals(ff.getFieldtype())){
									if (ff.getFieldlength() != null && !"".equals(ff.getFieldlength()) && !"null".equals(ff.getFieldlength())){			   
										   if(ff.getFieldlength().indexOf(",") == -1){
											   fieldTypeMap.put(ff.getDisplay(),String.valueOf(DataCell.DATA_TYPE_INTEGER)); 
										   }else{
											   OperHelper oper = new OperHelper();
											   String length = ff.getFieldlength();
											   String digits = oper.splitFieldscale(length);
											   if(StringUtils.isBlank(digits)){
												   fieldTypeMap.put(ff.getDisplay(),String.valueOf(DataCell.DATA_TYPE_INTEGER)); 
											   }else{
												   fieldTypeMap.put(ff.getDisplay(),String.valueOf(DataCell.DATA_TYPE_NUMERIC)); 
											   }
										   } 
									 }else{
										 fieldTypeMap.put(ff.getDisplay(),String.valueOf(DataCell.DATA_TYPE_INTEGER)); 
									 }
								}
							}
						}
					}
				}
			}
		}
		DataCell[] dcList = row.getCell();
		for (int k = 0; k < dcList.length; k++) {
			DataCell cell = dcList[k];
			String content = cell.getContent();
			if(content!=null && fieldTypeMap.containsKey(content)){
				typeAry[k] = Integer.parseInt(fieldTypeMap.get(content));
			}else{
				typeAry[k] = DataCell.DATA_TYPE_TEXT;
			}
		}
		return typeAry;
	}

}
