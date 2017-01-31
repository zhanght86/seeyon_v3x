package www.seeyon.com.v3x.form.controller.formservice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.Operation;
import www.seeyon.com.v3x.form.controller.pageobject.Operation_BindEvent;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.controller.pageobject.TemplateObject;
import www.seeyon.com.v3x.form.controller.query.QueryObject;
import www.seeyon.com.v3x.form.controller.report.ReportChartInfo;
import www.seeyon.com.v3x.form.controller.report.ReportObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeTask;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FieldInput;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FormView;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.ToperationType;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.utils.StringUtils;
import www.seeyon.com.v3x.form.utils.TLogUtils;
import com.seeyon.v3x.util.Strings;

public class ChangeObjXml {
	TLogUtils log = new TLogUtils(ChangeObjXml.class);

	private final static String C_sXML_Head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

	/** ******************对象转换为xml************************ */

	/**************************生成all.xml文件***********************************/
	// 生成all.xml文件
	public String createSeeyonDataDefineXml(int aSpace, Map map, SessionObject sessionobject)
			throws SeeyonFormException {
		long callid = log.debug_CallMethod("createSeeyonDataDefineXml","aSpace", aSpace, "map", map);
		String formname = (String) map.get("FormName");
		Long formid = (Long) map.get("FormId");
		DataDefine datadefine = (DataDefine) map.get("DataDefine");
		List formList = (List) map.get("FormList");

		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(C_sXML_Head);
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace) + "<SeeyonFomDefine ");
		sqlsb.append(StringUtils.space(aSpace) + " name=\"" + StringUtils.Java2XMLStr(formname) + "\"");
		sqlsb.append(StringUtils.space(aSpace) + " id=\"" + StringUtils.Java2XMLStr(formid.toString()) + "\"");
		sqlsb.append(StringUtils.space(aSpace) + ">   \r\n");
		sqlsb.append(StringUtils.space(aSpace + 2) + "<Define>");
		sqlsb.append(" \r\n");
		sqlsb.append(datadefine.creatDefineXml(aSpace));
		sqlsb.append(" \r\n");
		// FormState暂时写死
		//12月18号先去掉State部分
		sqlsb.append(StringUtils.space(aSpace + 4) + "<FormState>");
		sqlsb.append(" \r\n");
		//sqlsb.append(StringUtils.space(aSpace + 8)
		//+ "<State id=\"0\" name= \"草稿\"/> \r\n ");
		/*sqlsb.append(StringUtils.space(aSpace + 8)
				+ "<State id=\"0\" name= \""+Constantform.getString4CurrentUser("form.query.draft.label")+"\"/> \r\n ");
		//sqlsb.append(StringUtils.space(aSpace + 8)
		//+ "<State id=\"1\" name= \"未处理\"/>  \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8)
				+ "<State id=\"1\" name= \""+Constantform.getString4CurrentUser("form.query.nodealwith.label")+"\"/>  \r\n");
		//sqlsb.append(StringUtils.space(aSpace + 8)
		//		+ "<State id=\"2\" name= \"审批\"/>  \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8)
				+ "<State id=\"2\" name= \""+Constantform.getString4CurrentUser("form.query.pass.label")+"\"/>  \r\n");
		*/
		sqlsb.append(StringUtils.space(aSpace + 4) + "</FormState>");
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 4) + "<FormList>");
		sqlsb.append(" \r\n");
		for(int i=0;i<formList.size();i++){
			FormPage formpage = (FormPage)formList.get(i);
			sqlsb.append(getFormXMLString(aSpace,formpage)+"\r\n");
		}
		sqlsb.append(StringUtils.space(aSpace + 4) + "</FormList>");

        //  暂时写死
        sqlsb.append(" \r\n");        
        
        //查询部分的xml
        List querylist = (List)map.get("QueryList");
        sqlsb.append(StringUtils.space(aSpace + 4) + "<QueryList>");
		if(querylist!=null){		
			for(int i = 0;i<querylist.size();i++){
				QueryObject query = (QueryObject)querylist.get(i);
				sqlsb.append(createQueryListXml(8,query));
			}
			sqlsb.append(" \r\n");
		}		
		sqlsb.append(StringUtils.space(aSpace + 4) + "</QueryList>");
        sqlsb.append(" \r\n");
        //统计部分的xml
        List reportlist = (List)map.get("ReportList");
        sqlsb.append(StringUtils.space(aSpace + 4) + "<ReportList>");
		if(reportlist!=null){		
			for(int i = 0;i<reportlist.size();i++){
				ReportObject report = (ReportObject)reportlist.get(i);
				sqlsb.append(createReportListXml(8,report));	
			}
		}
		sqlsb.append(StringUtils.space(aSpace + 4) + "</ReportList>");
		sqlsb.append(" \r\n");
		
		//唯一字段
		sqlsb.append(StringUtils.space(aSpace + 4) + "<UniqueFieldList> \r\n");
		String uniqueString = sessionobject.getUniqueFieldString();
		if(uniqueString != null){
			sqlsb.append(uniqueString);
		}
		sqlsb.append(StringUtils.space(aSpace + 4) + "</UniqueFieldList> \r\n");

		//关联关系
		Collection<RelationCondition> relationConditionList = sessionobject.getRelationConditionMap().values();
		sqlsb.append(StringUtils.space(aSpace + 4) + "<RelationConditionList> \r\n");
		if(relationConditionList!=null){
			for (RelationCondition relationCondition : relationConditionList) {
				sqlsb.append(relationCondition.getXmlString(aSpace + 8));	
			}
		}
		sqlsb.append(StringUtils.space(aSpace + 4) + "</RelationConditionList> \r\n");
		
		sqlsb.append(StringUtils.space(aSpace + 2) + "</Define>");
		sqlsb.append(" \r\n");

		if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){
			sqlsb.append(StringUtils.space(aSpace) + "<Bind>");
			sqlsb.append(" \r\n");
		
			TemplateObject temobj = (TemplateObject)map.get("TemplateObject");
			if(temobj !=null){
			HashMap hash = temobj.getFlowMap();
			Iterator it = hash.entrySet().iterator();
			sqlsb.append(StringUtils.space(aSpace + 4) + "<FlowTempletList>");
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				// entry.getKey() 返回与此项对应的键
				// entry.getValue() 返回与此项对应的值
				sqlsb.append(getFlowTempletXMLString(aSpace,entry));
			}
			sqlsb.append(StringUtils.space(aSpace + 4) + "</FlowTempletList>");
			}		
			//KnowledgeItemList,MenuList未定义完整	
			//暂时写死
	//		sqlsb.append(" \r\n");
	//		sqlsb.append(StringUtils.space(aSpace + 4) + "<KnowledgeItemList>");
	//		sqlsb.append(getKnowledgeItemXMLString(aSpace));
	//		sqlsb.append(StringUtils.space(aSpace + 4) + "</KnowledgeItemList>");
	//		//暂时写死
	//		sqlsb.append(" \r\n");
	//		sqlsb.append(StringUtils.space(aSpace + 4) + "<MenuList>");
	//		sqlsb.append(getMenuXMLString(aSpace));
	//		sqlsb.append(StringUtils.space(aSpace + 4) + "</MenuList>");
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "</Bind>");
		}else if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue()){
			sqlsb.append(StringUtils.space(aSpace) + sessionobject.getMainBindXml());
		} else if(sessionobject.getFormType() == ISeeyonForm.TAppBindType.BASEDATA.getValue()){
			String formCode = sessionobject.getFormCode();
			sqlsb.append(StringUtils.space(aSpace) + "<Bind formcode=\""+ (formCode==null?"":formCode) +"\">");
			sqlsb.append(" \r\n");
			sqlsb.append(getLogFieldXMLString(sessionobject));
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "<FormAppAuthList>");
			sqlsb.append(" \r\n");
			sqlsb.append(sessionobject.getAppAuthObject().getXmlString());
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "</FormAppAuthList>");
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "</Bind>");
		}else{
			sqlsb.append(StringUtils.space(aSpace) + "<Bind></Bind>");
		}
		sqlsb.append(" \r\n");
		
		//增加事件触发xml信息 by wusb at 2012-01-05
		Collection<FormEvent> triggerConfigList = sessionobject.getTriggerConfigMap().values();
		sqlsb.append(StringUtils.space(aSpace) + "<Trigger> \r\n");
		sqlsb.append(StringUtils.space(aSpace+2) + "<EventList> \r\n");
		for (FormEvent formEvent : triggerConfigList) {
			sqlsb.append(formEvent.getXmlString(aSpace+4));
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "</EventList> \r\n");
		sqlsb.append("</Trigger> \r\n");
		
		sqlsb.append(StringUtils.space(aSpace) + "</SeeyonFomDefine>");
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}
	private String getLogFieldXMLString(SessionObject sessionObject)throws SeeyonFormException {
		List logFieldList=sessionObject.getLogFieldList();
		if(logFieldList != null){
			return sessionObject.getLogfieldString().toString();
		}
		return "";
	}

	// 生成all.xml文件的Form部分
	private String getFormXMLString(int aSpace,FormPage formpage) throws SeeyonFormException {
		long callid = log.debug_CallMethod("getFormXMLString","aSpace", aSpace,"formpage",formpage);	
		StringBuffer sqlsb_now = new StringBuffer();
		sqlsb_now.append(StringUtils.space(aSpace + 8) + "<Form ");
		sqlsb_now.append(StringUtils.space(aSpace) + " id=\"" + formpage.getFormPageId() + "\"");
		sqlsb_now.append(StringUtils.space(aSpace) + " name=\"" + StringUtils.Java2XMLStr(formpage.getName()) + "\"");
		sqlsb_now.append(StringUtils.space(aSpace) + " type=\"seeyonform\"");
		sqlsb_now.append(StringUtils.space(aSpace) + ">   \r\n");
        if(formpage.getEngine() == null){
        	sqlsb_now.append(StringUtils.space(aSpace + 12)
    				+ "<Engine>infopath</Engine>");	
        }else{
        	sqlsb_now.append(StringUtils.space(aSpace + 12)
    				+ "<Engine>");	
        	sqlsb_now.append(StringUtils.space(aSpace)
    				+ ""+formpage.getEngine()+"");	
        	sqlsb_now.append(StringUtils.space(aSpace)
    				+ "</Engine>");	
        }
        sqlsb_now.append(" \r\n");
        sqlsb_now.append(StringUtils.space(aSpace + 16) + "<ViewList>");
        sqlsb_now.append(" \r\n");
		for (InfoPath_FormView formview : formpage.getViewlst()) {
			sqlsb_now.append(formview.getViewXMLString(aSpace));
		}
		sqlsb_now.append(StringUtils.space(aSpace + 16) + "</ViewList>");
		sqlsb_now.append(" \r\n");
		sqlsb_now.append(StringUtils.space(aSpace + 16) + "<OperationList>");
		sqlsb_now.append(" \r\n");
		for(int j=0;j<formpage.getOperlst().size(); j++){
		    Operation oper= (Operation)formpage.getOperlst().get(j);
		    sqlsb_now.append(getOperationXMLString(aSpace,oper));
		}
		
		sqlsb_now.append(StringUtils.space(aSpace + 16) + "</OperationList>");
		sqlsb_now.append(" \r\n");
		sqlsb_now.append(StringUtils.space(aSpace + 8) + "</Form>");
		sqlsb_now.append(" \r\n");
		
		log.debug_Return(callid, sqlsb_now.toString());
		//System.out.println(sqlsb_now);
		return sqlsb_now.toString();
	}

	// 生成all.xml文件的operation部分
	private String getOperationXMLString(int aSpace,Operation oper) throws SeeyonFormException {
		long callid = log.debug_CallMethod("getOperationXMLString","aSpace", aSpace,"oper",oper);
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(StringUtils.space(aSpace + 20) + "<Operation ");
		sqlsb.append(StringUtils.space(aSpace) + " id=\"" + oper.getOperationId() + "\"");
		sqlsb.append(StringUtils.space(aSpace) + " name=\"" + StringUtils.Java2XMLStr(oper.getName()) + "\"");
		sqlsb.append(StringUtils.space(aSpace) + " filename=\"" + oper.getFilename()
				+ "\"");
		sqlsb.append(StringUtils.space(aSpace) + " type=\""
				+ oper.getType() + "\"");
		sqlsb.append(StringUtils.space(aSpace) + "/>   \r\n");
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}

    //	生成all.xml文件的ProcessList部分
	private String getProcessXMLString(int aSpace) throws SeeyonFormException {
		long callid = log.debug_CallMethod("getProcessXMLString","aSpace", aSpace);
		StringBuffer sqlsb = new StringBuffer();
        //  没有赋值
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8) + "<Process  ");
		sqlsb.append(StringUtils.space(aSpace) + " id=\"21\"");
		//sqlsb.append(StringUtils.space(aSpace) + " name=\"审批费用报销单\"");
		sqlsb.append(StringUtils.space(aSpace) + " name=\"fee write off bill\"");
		sqlsb.append(StringUtils.space(aSpace) + " type=\"seeyonform\"");
		sqlsb.append(StringUtils.space(aSpace) + "/>   \r\n");
		
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}
	
	
	// 生成all.xml统计部分
	public String createReportListXml(int aSpace, ReportObject reportObject)throws SeeyonFormException {
		
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace) + "<Report ");
		sqlsb.append( " id=\"" +reportObject.getReportid()+ "\"");
		sqlsb.append( " name=\"" + reportObject.getReportName() + "\"");
		sqlsb.append( " type=\"seeyonform\"");
		sqlsb.append( ">   \r\n");
		String mastername = reportObject.getMastername();
		String slavename = reportObject.getSlavename();
	    if(mastername == null || "".equals(mastername)){
	    	mastername = "null";
	    }if(slavename == null ||"".equals(slavename)){
	    	slavename = "null";
	    }
		if(!"null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+2) + "<QuerySource ");
			sqlsb.append( " masterTable=\""+mastername+"\"");
			sqlsb.append( ">   \r\n");
		}
		if(!"null".equals(slavename) &&  "null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+2) + "<QuerySource ");
			sqlsb.append( " masterTable=\""+slavename+"\"");
			sqlsb.append( ">   \r\n");
		}
		if(!"null".equals(slavename)  && !"null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+4) + "<slaveTable  ");
			sqlsb.append( " tableName=\""+slavename+"\"");
			sqlsb.append( " masterTable=\""+mastername+"\"");
			sqlsb.append( " linkfield=\""+mastername+"Id\"");
			sqlsb.append( "/>   \r\n");
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "</QuerySource> ");
		sqlsb.append(" \r\n");
		//统计输入范围xml字符串
		if(Strings.isNotBlank(reportObject.getReportAreaValue())){
			sqlsb.append(reportObject.getReportAreaValue());
			sqlsb.append(" \r\n");	
		}
		if(Strings.isNotBlank(reportObject.getCustomReportFieldValue())){
			sqlsb.append(reportObject.getCustomReportFieldValue());
			sqlsb.append(" \r\n");	
		}
		if(Strings.isNotBlank(reportObject.getconditionvalue())){
			sqlsb.append(reportObject.getconditionvalue());
			sqlsb.append(" \r\n");
		}
		String type = null;
		if("0".equals(reportObject.getReportType())){
			type = "false";
		}else if("1".equals(reportObject.getReportType())){
			type = "true";
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "<ReportHead ");
		sqlsb.append( " acrossreport=\""+type+"\" > \r\n");
		if("false".equals(type)){
			sqlsb.append(reportObject.getRowheadValue());
		}
		if("true".equals(type)){
			sqlsb.append(reportObject.getColumnheadValue());
			sqlsb.append(reportObject.getRowheadValue());
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "</ReportHead> \r\n");

		sqlsb.append(reportObject.getReportDataFieldValue());
		sqlsb.append(reportObject.getSumDataFieldValue());
        //暂时写死
		sqlsb.append(StringUtils.space(aSpace+2) + "<ViewModule  ");
		sqlsb.append( " showcondition=\"stat_formstat.jsp\"");
		sqlsb.append( " getrusult=\"showReportResult.jsp\"");
		sqlsb.append( "/>   \r\n");
		if(Strings.isNotBlank(reportObject.getFormId())){
			sqlsb.append(StringUtils.space(aSpace+2) + "<ShowDetail  ");
			if(reportObject.getFormId().indexOf("|") == -1){
				sqlsb.append( " name=\""+reportObject.getFormId()+"."+reportObject.getOperationId()+"\"");
			}else{
				String detail = "";
				for(int a=0;a<reportObject.getFormId().split("\\|").length;a++){
					  detail += reportObject.getFormId().split("\\|")[a]+"."+reportObject.getOperationId().split("\\|")[a]+"|";
				}
				sqlsb.append( " name=\""+detail+"\"");
			}
			sqlsb.append( "/>   \r\n");	
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "<Description>");
		if(!"".equals(reportObject.getReportDescriptions())){
			sqlsb.append(StringUtils.Java2XMLStr(reportObject.getReportDescriptions()));
		}	
		sqlsb.append("</Description>  \r\n");
		Map<String, ReportChartInfo> chartInfos = reportObject.getChartInfos();
		if(chartInfos!= null && chartInfos.size() > 0){
			sqlsb.append("<ReportChartList>  \r\n");
			for(ReportChartInfo chartInfo : chartInfos.values()){
				sqlsb.append(chartInfo.getXml());
			}
			sqlsb.append("</ReportChartList>  \r\n");
		}
		sqlsb.append(StringUtils.space(aSpace) + "</Report> ");
		sqlsb.append(" \r\n");
		return sqlsb.toString();
     }
	
	
    //	 生成all.xml查询部分
	public String createQueryListXml(int aSpace, QueryObject queryObject)throws SeeyonFormException {
		
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace) + "<Query ");
		sqlsb.append( " id=\"" +queryObject.getQueryId()+ "\"");
		sqlsb.append( " name=\"" + queryObject.getQueryName() + "\"");
		sqlsb.append( " type=\"seeyonform\"");
		sqlsb.append( ">   \r\n");
		String mastername = queryObject.getMastername();
		String slavename = queryObject.getSlavename();
	    if(mastername == null || "".equals(mastername)){
	    	mastername = "null";
	    }if(slavename == null ||"".equals(slavename)){
	    	slavename = "null";
	    }
		if(!"null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+2) + "<QuerySource ");
			sqlsb.append( " masterTable=\""+mastername+"\"");
			sqlsb.append( ">   \r\n");
		}
		if(!"null".equals(slavename) && "null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+2) + "<QuerySource ");
			sqlsb.append( " masterTable=\""+slavename+"\"");
			sqlsb.append( ">   \r\n");
		}
		if(!"null".equals(slavename) && !"null".equals(mastername)){
			sqlsb.append(StringUtils.space(aSpace+4) + "<slaveTable  ");
			sqlsb.append( " tableName=\""+slavename+"\"");
			sqlsb.append( " masterTable=\""+mastername+"\"");
			sqlsb.append( " linkfield=\""+mastername+"Id\"");
			sqlsb.append( "/>   \r\n");
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "</QuerySource> ");
		sqlsb.append(" \r\n");		
		sqlsb.append(queryObject.getQueryAreaValue());
		sqlsb.append(" \r\n");
		sqlsb.append(queryObject.getQueryConditionValue());
		sqlsb.append(" \r\n");
		sqlsb.append(queryObject.getDataFieldValue());
		sqlsb.append(" \r\n");
		sqlsb.append(queryObject.getCustomQueryFieldValue()==null ? "" : queryObject.getCustomQueryFieldValue());
		sqlsb.append(" \r\n");
		sqlsb.append(queryObject.getResultSortValue());
		sqlsb.append(" \r\n");
		SeeyonReportImpl impl = new SeeyonReportImpl();
		sqlsb.append(StringUtils.space(aSpace+2) + "<ViewModule  ");
		sqlsb.append( " showcondition=\"query_formquery.jsp\"");
		sqlsb.append( " getrusult=\"showQueryResult.jsp\"");
		sqlsb.append( "/>   \r\n");	
		if(Strings.isNotBlank(queryObject.getFormId())){
			sqlsb.append(StringUtils.space(aSpace+2) + "<ShowDetail  ");
			if(queryObject.getFormId().indexOf("|") == -1){
				sqlsb.append( " name=\""+queryObject.getFormId()+"."+queryObject.getOperationId()+"\"");
			}else{
				String detail = "";
				for(int a=0;a<queryObject.getFormId().split("\\|").length;a++){
					  detail += queryObject.getFormId().split("\\|")[a]+"."+queryObject.getOperationId().split("\\|")[a]+"|";
				}
				sqlsb.append( " name=\""+detail+"\"");
			}
			
			sqlsb.append( "/>   \r\n");			
		}
		sqlsb.append(StringUtils.space(aSpace+2) + "<Description>");
		if(!"".equals(queryObject.getDescritpion())){
			sqlsb.append(StringUtils.Java2XMLStr(queryObject.getDescritpion()));
		}	
		sqlsb.append("</Description>  \r\n");	
		sqlsb.append(StringUtils.space(aSpace) + "</Query> ");
		sqlsb.append(" \r\n");
		return sqlsb.toString();
     }
	
	// 生成all.xml文件的FlowTemplet部分
	private String getFlowTempletXMLString(int aSpace,Map.Entry entry) {
		long callid = log.debug_CallMethod("getFlowTempletXMLString","aSpace", aSpace,"entry",entry);
		StringBuffer sqlsb = new StringBuffer();
		FlowTempletImp fotemimp = (FlowTempletImp)entry.getValue();
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8) + "<FlowTemplet ");
		sqlsb.append(StringUtils.space(aSpace) + " id=\""+fotemimp.getId()+"\"");
		sqlsb.append(StringUtils.space(aSpace) + " name=\""+StringUtils.Java2XMLStr(fotemimp.getName())+"\"");		
		sqlsb.append(StringUtils.space(aSpace) + " category=\""+fotemimp.getCategory()+"\"");
		sqlsb.append(StringUtils.space(aSpace) + "> </FlowTemplet>  \r\n");
		sqlsb.append(" \r\n");
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}

	// 生成all.xml文件的KnowledgeItem部分
	private String getKnowledgeItemXMLString(int aSpace) {
		long callid = log.debug_CallMethod("getKnowledgeItemXMLString","aSpace", aSpace);
		StringBuffer sqlsb = new StringBuffer();
		// 没有进行赋值
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8) + "<KnowledgeItem ");
		sqlsb.append(StringUtils.space(aSpace) + " id=\"002101\"");
		//sqlsb.append(StringUtils.space(aSpace) + " name=\"填写费用报销单\"");
		sqlsb.append(StringUtils.space(aSpace) + " name=\"write fee write off bill\"");
		sqlsb.append(StringUtils.space(aSpace) + "> </KnowledgeItem>  \r\n");
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}

	// 生成all.xml文件的Menu部分
	private String getMenuXMLString(int aSpace) {
		long callid = log.debug_CallMethod("getMenuXMLString","aSpace", aSpace);
		StringBuffer sqlsb = new StringBuffer();
		// 没有进行赋值
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 8) + "<Menu ");
		sqlsb.append(StringUtils.space(aSpace) + " id=\"003101\"");
		//sqlsb.append(StringUtils.space(aSpace) + " name=\"填写费用报销单\"");
		sqlsb.append(StringUtils.space(aSpace) + " name=\"write fee write off bill\"");
		sqlsb.append(StringUtils.space(aSpace) + "> </Menu>  \r\n");
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
	}
	
	private String OperationType2str(ToperationType aStr)
			throws SeeyonFormException {

		if (ToperationType.otAdd.equals(aStr))
			return IXmlNodeName.C_sVluae_add;
		else if (ToperationType.otUpdate.equals(aStr))
			return IXmlNodeName.C_sVluae_update;
		else if (ToperationType.otDelete.equals(aStr))
			return IXmlNodeName.C_sVluae_delete;
		else
			
			/*throw new DataDefineException(
			DataDefineException.C_iStorageErrode_AttributeDefineError,
			"Operation 的 type 属性定义不正确!");*/

			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_AttributeDefineError,
					Constantform.getString4CurrentUser("form.base.opertypeiswrong.label"));

	}
	
	
	/**************************生成bindschema.xml文件***********************************/
	 //	生成bindschema.xml
	public String creatBindschemaXml(int aSpace,Map map) throws SeeyonFormException {
		long callid = log.debug_CallMethod("creatBindschemaXml","aSpace", aSpace,"map",map);
		List tablefieldlist = (List)map.get("TableFieldList");
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(C_sXML_Head);
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace) + "<DataBind>");
		
		String table=null;
		
		for(int i =0;i<tablefieldlist.size(); i++){
			TableFieldDisplay tablebind = (TableFieldDisplay)tablefieldlist.get(i);
			String tablename = tablebind.getTablename();
			if(i==0){
				table = tablename;
				sqlsb.append(creatTable(aSpace,tablebind,tablefieldlist,tablename));
			}
			if(!table.equals(tablename)){
				table = tablename;
				sqlsb.append(creatTable(aSpace,tablebind,tablefieldlist,tablename));
			}
		}
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace) + "</DataBind>");
		
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
		}

	private String creatTable(int aSpace,TableFieldDisplay tablebind,List tablefieldlist,String tablename) throws SeeyonFormException {
		long callid = log.debug_CallMethod("creatTable","aSpace", aSpace,"tablebind",tablebind,"tablefieldlist",tablefieldlist,"tablename",tablename);		
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" \r\n");
		sqlsb.append(StringUtils.space(aSpace + 4) + "<Table ");
		sqlsb.append(StringUtils.space(aSpace) + " tablename=\""+tablename+"\"");
		sqlsb.append(StringUtils.space(aSpace) + ">   \r\n");	
		String bind=null;
		String field=null;
		for(int i=0 ; i<tablefieldlist.size(); i++){
			tablebind = (TableFieldDisplay)tablefieldlist.get(i);
			String ftable =tablebind.getTablename();
			if(ftable.equals(tablename)){
				bind=tablebind.getBindname();
				field=tablebind.getFieldname();
				sqlsb.append(creatField(aSpace,tablebind,bind,field));
			}			
		}
		sqlsb.append(StringUtils.space(aSpace + 4) + "</Table> ");	
		log.debug_Return(callid, sqlsb.toString());
		return sqlsb.toString();
		}
	private String creatField(int aSpace,TableFieldDisplay tablebind,String bind,String field) throws SeeyonFormException {
			long callid = log.debug_CallMethod("creatField","aSpace", aSpace,"tablebind",tablebind,"bind",bind,"field",field);				
			StringBuffer sqlsb = new StringBuffer();
		       
			    sqlsb.append(StringUtils.space(aSpace + 8) + "<Field  ");
				sqlsb.append(StringUtils.space(aSpace) + " fieldname=\""+field+"\"");
				sqlsb.append(StringUtils.space(aSpace) + " bindname=\""+bind+"\"");
				sqlsb.append(StringUtils.space(aSpace) + "/>   \r\n");			
			log.debug_Return(callid, sqlsb.toString());
			return sqlsb.toString();
		}
	  
	  
	  
	  
	/**************************生成defaultInput.xml文件***********************************/
	  public String createDefaultInputXml(int aSpace, Map map)
		throws SeeyonFormException {
			long callid = log.debug_CallMethod("createDefaultInputXml","aSpace", aSpace, "map", map);			
			List fildinputlist = (List) map.get("FieldInputList");
			
			StringBuffer sqlsb = new StringBuffer();
			sqlsb.append(C_sXML_Head);
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "<FieldInputList>");
			sqlsb.append(" \r\n");
			for(int i=0;i<fildinputlist.size();i++){
				InfoPath_FieldInput  fieldinput = (InfoPath_FieldInput)fildinputlist.get(i);
				sqlsb.append(fieldinput.creatDefaultInputXml(aSpace));
			}
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "</FieldInputList>");
			log.debug_Return(callid, sqlsb.toString());
			return sqlsb.toString();
	  }
	  
	  
	  
    /**************************生成Operation_001.xml文件***********************************/
	  
	/*
	 * 
	 */
	  public String createOperationXml(int aSpace,String opertype,List tablefieldlst, Map map,SessionObject sessionobject)
		throws SeeyonFormException {
			long callid = log.debug_CallMethod("createOperationXml","aSpace", aSpace, "map", map,"sessionobject",sessionobject);			
			StringBuffer sqlsb = new StringBuffer();
			/*List slalist = new ArrayList();
		    String tablename=null;
		    String slave =null;
		    String namespace = null;
			for(int i=0; i<tablefieldlst.size();i++ ){
				TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
				String name = tfd.getTablename();
				String bindname =tfd.getBindname();				
				if(i==0){
					tablename=tfd.getTablename();
					namespace = OperHelper.Namespace(bindname);
				}else if(!name.equals(tablename)){
					tablename=tfd.getTablename();
					slave = tfd.getTablename();
					String slavename = tfd.getEditablename();
					slalist.add(namespace+slavename);
				}
			}*/
			sqlsb.append(C_sXML_Head);
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace) + "<Operation>");
			sqlsb.append(" \r\n");
			sqlsb.append(StringUtils.space(aSpace + 4) + "<FieldList>");
			sqlsb.append(" \r\n");
				    Operation oper = (Operation) map.get("Operation");
					for(int t=0;t<oper.getOperlst().size(); t++){
						Map opermap=(Map)oper.getOperlst().get(t);
						String formprint = (String) opermap.get("formprint"+t);
						String formtransmit = (String)opermap.get("formtransmit"+t);
						if(formprint == null){
							formprint = "N";
						}if(formtransmit ==null){
							formtransmit = "N";
						}
						sqlsb.append(StringUtils.space(aSpace + 8) + "<Field  ");
						sqlsb.append(StringUtils.space(aSpace) + " name=\"" + opermap.get("bindname"+t) + "\"");
						sqlsb.append(StringUtils.space(aSpace) + " access=\""+ opermap.get("formoper"+t) + "\"");			
						sqlsb.append(StringUtils.space(aSpace) + " allowprint=\"");
						sqlsb.append( formprint.equals("Y") ? "true": "false");
						sqlsb.append("\"");
						sqlsb.append(StringUtils.space(aSpace) + " allowtransmit=\"");
						sqlsb.append(formtransmit.equals("Y")  ? "true": "false");
						sqlsb.append("\"");
						sqlsb.append("/> \r\n");
					}
			sqlsb.append(StringUtils.space(aSpace + 4) + "</FieldList>");
			sqlsb.append(" \r\n");
			//if(aSpace == 2){
				/*
				if(IXmlNodeName.C_sVluae_add.equals(opertype)){
					//String substr = "<Submit name=\"提交\" type=\"submit\"  state=\"1\" /> \r\n";
					String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"1\" /> \r\n";
					//String drostr = "<Submit name=\"放弃\" type=\"rollback\" /> \r\n";
					String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
					//String rolstr = "<Submit name=\"回退\" type=\"rollback\"  state=\"0\" /> \r\n";
					String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"0\" /> \r\n";
					sqlsb.append(substr+drostr+rolstr);
					for(int i =0;i<slalist.size();i++){
						 String slavename = (String) slalist.get(i);
						 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"true\"  allowdelete=\"true\"/> \r\n";
						 sqlsb.append(slavestr);
					}
				}else if(IXmlNodeName.C_sVluae_update.equals(opertype)){
					//String substr = "<Submit name=\"提交\" type=\"submit\"  state=\"2\" /> \r\n";
					String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"2\" /> \r\n";
					//String drostr = "<Submit name=\"放弃\" type=\"rollback\" /> \r\n";
					String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
					//String rolstr = "<Submit name=\"回退\" type=\"rollback\"  state=\"1\" /> \r\n";
					String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"1\" /> \r\n";
					sqlsb.append(substr+drostr+rolstr);
					for(int i =0;i<slalist.size();i++){
						 String slavename = (String) slalist.get(i);
						 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n";
						 sqlsb.append(slavestr);
					}
				}else if(IXmlNodeName.C_sVluae_readonly.equals(opertype)){
					//String substr = "<Submit name=\"提交\" type=\"submit\"  state=\"2\" /> \r\n";
					String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"2\" /> \r\n";
					//String drostr = "<Submit name=\"放弃\" type=\"rollback\" /> \r\n";
					String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
					//String rolstr = "<Submit name=\"回退\" type=\"rollback\"  state=\"1\" /> \r\n";
					String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"1\" /> \r\n";
					sqlsb.append(substr+drostr+rolstr);
					for(int i =0;i<slalist.size();i++){
						 String slavename = (String) slalist.get(i);
						 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n";
						 sqlsb.append(slavestr);
					}
				}else{
					//String substr = "<Submit name=\"提交\" type=\"submit\"  state=\"1\" /> \r\n";
					String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"1\" /> \r\n";
					//String drostr = "<Submit name=\"放弃\" type=\"rollback\" /> \r\n";
					String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
					//String rolstr = "<Submit name=\"回退\" type=\"rollback\"  state=\"0\" /> \r\n";
					String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"0\" /> \r\n";
					sqlsb.append(substr+drostr+rolstr);
					for(int i =0;i<slalist.size();i++){
						 String slavename = (String) slalist.get(i);
						 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"true\"  allowdelete=\"true\"/> \r\n";
						 sqlsb.append(slavestr);
					}
				}*/
				String highinitxml=oper.getNewhighinitxml();
				String evenxml=oper.getNewhighevenxml();
				String initxml=oper.getNewinitxml();
				sqlsb.append("<ViewBind> \r\n");
				if(!"".equals(initxml) && !"null".equals(initxml) && initxml !=null){
					sqlsb.append("<OnInit> \r\n");
					sqlsb.append(initxml.replaceAll("↗", ""));
					sqlsb.append("</OnInit> \r\n");
				}					
				if(!"".equals(evenxml) && !"null".equals(evenxml) && evenxml !=null)
					sqlsb.append(evenxml);
				sqlsb.append(" \r\n");
				List<Operation_BindEvent> bindEventList = oper.getBindEventList();
				if(CollectionUtils.isNotEmpty(bindEventList)){
					sqlsb.append(StringUtils.space(aSpace) + "<EventBindList> \r\n");
					for (Operation_BindEvent operation_BindEvent : bindEventList) {
						sqlsb.append(StringUtils.space(aSpace) + operation_BindEvent.createBindEventXml() + " \r\n");
					}
					sqlsb.append(StringUtils.space(aSpace) + "</EventBindList> \r\n");
				}
				
				List<InfoPath_DeeTask> deeTakEventList = oper.getDeeTakEventList();
				if(CollectionUtils.isNotEmpty(deeTakEventList)){
					sqlsb.append(StringUtils.space(aSpace) + "<DeeTaskList> \r\n");
					for (InfoPath_DeeTask dee_BindEvent : deeTakEventList) {
						sqlsb.append(StringUtils.space(aSpace) + dee_BindEvent.getXmlString(1) + " \r\n");
					}
					sqlsb.append(StringUtils.space(aSpace) + "</DeeTaskList> \r\n");
				}
				sqlsb.append("</ViewBind> \r\n");
				if(!"".equals(highinitxml) && !"null".equals(highinitxml) && highinitxml !=null)
					sqlsb.append(highinitxml);
				sqlsb.append(" \r\n");
				sqlsb.append(oper.getNewsubmitxml());
				sqlsb.append(" \r\n");
				sqlsb.append(oper.getNewrepeatxml());
			//}
				/*else {
			String viewbindstr = oper.getViewbindstr();
			String submitstr = oper.getSubmitlststr();
				if(viewbindstr == null){
					viewbindstr = "";
				}if("null".equals(viewbindstr)){
					viewbindstr = "";
				}if(submitstr == null){
					submitstr = "";
				}if("null".equals(submitstr)){
					submitstr = "";
				}				
			if(!"".equals(viewbindstr)){
				sqlsb.append(" \r\n");
				sqlsb.append(OperHelper.parseSpecialMark(oper.getViewbindstr()));
				sqlsb.append(" \r\n");
			}
			if("".equals(viewbindstr) && !"".equals(submitstr))
			{
				sqlsb.append(" \r\n");
				sqlsb.append(OperHelper.parseSpecialMark(oper.getSubmitlststr()));
				sqlsb.append(" \r\n");
			}
			if(sessionobject.getAddtablename().size() !=0){
				for(int i = 0;i<sessionobject.getAddtablename().size();i++){
					String name = (String)sessionobject.getAddtablename().get(i);
					String addname = OperHelper.AddTableName(name);
					if(IXmlNodeName.C_sVluae_add.equals(opertype)){
						sqlsb.append("<SlaveTable name=\""+namespace+addname+"\" allowadd=\"true\"  allowdelete=\"true\"/> \r\n");
					}else if(IXmlNodeName.C_sVluae_update.equals(opertype)){
						sqlsb.append("<SlaveTable name=\""+namespace+addname+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n");
					}else if(IXmlNodeName.C_sVluae_readonly.equals(opertype)){
						sqlsb.append("<SlaveTable name=\""+namespace+addname+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n");
					}else{
						sqlsb.append("<SlaveTable name=\""+namespace+addname+"\" allowadd=\"true\"  allowdelete=\"true\"/> \r\n");
					}	
				}	
			}
			}*/
			sqlsb.append(StringUtils.space(aSpace) + "</Operation>");
			log.debug_Return(callid, sqlsb.toString());
			return sqlsb.toString();
	  }
	 
	  
	  
	  
	  
	  
	/** *******************xml转换为对象*********************** */
}
