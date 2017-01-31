package com.seeyon.v3x.main.section;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import sun.misc.BASE64Encoder;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.report.ReportController;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_AcrossColHead;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_DataColum;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_Head;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_Value;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.inf.IReportResult;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;
/**
 * 表单统计表格栏目
 * @author xgghen 2011-03-24
 */
public class SingleBoardFormReportResultTableSection extends BaseSection{
	
	private static final Log log = LogFactory.getLog(SingleBoardFormReportResultTableSection.class);
	private static final SeeyonForm_Runtime runtime = SeeyonForm_Runtime.getInstance();
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	
	@Override
	public String getId() {
		return "singleBoardFormReportResultTableSection";
	}
	
	@Override
	protected String getName(Map<String, String> preference) {
		String singleBoardId = preference.get(PortletEntityProperty.PropertyName.singleBoardId.name());
		String[] formAndReport = getFormAndQuery(singleBoardId);
		
		if(formAndReport == null ||  formAndReport.length < 2 
				|| Strings.isBlank(formAndReport[0]) || Strings.isBlank(formAndReport[1])){
			log.info("表单的统计模板的解析出现问题");
			return null;
		}
		
		/*String showtitleName = preference.get("columnsName");
		if(Strings.isNotBlank(showtitleName)){
			return showtitleName ;
		}*/
		
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndReport[1])) ;
		if(fapp == null){
			log.info("表单的统计模板的解析出现问题");
			return null;
		}
		
		try{
			ISeeyonReport seeyonReport = fapp.findReportByName(formAndReport[0]) ;
			if(seeyonReport != null){
				return seeyonReport.getReportName();
			}
			
			if(NumberUtils.isNumber(formAndReport[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0])) ;
				if(formQueryPlan != null){
					return formQueryPlan.getPlanName() ;
				}
			}
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
			
		return null;
	}
	
	private String[] getFormAndQuery(String formAndQuery){
		if(Strings.isBlank(formAndQuery)){
			return null ;
		}
		if(formAndQuery.indexOf(",") == -1){
			return null ;
		}
		return formAndQuery.split(",") ;
	}
	@Override
	public String getBaseName(Map<String, String> preference) {
		return this.getName(preference);
	}
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	private static final int DEFAULT_HEIGHT = 300;

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete();        
		String singleBoardId = preference.get(PortletEntityProperty.PropertyName.singleBoardId.name());
		String[] formAndReport = getFormAndQuery(singleBoardId);
		
		if(formAndReport == null ||  formAndReport.length < 2 
				|| Strings.isBlank(formAndReport[0]) || Strings.isBlank(formAndReport[1])){
			log.info("表单的统计模板的解析出现问题");
			return null;
		}
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndReport[1])) ;
		if(fapp == null){
			log.info("表单的统计模板的解析出现问题");
			return null;
		}
        StringBuilder html = new StringBuilder();
        String reportname = formAndReport[0];
        String planid = "";
        
        html.append("<div id='' style='vertical-align: middle;text-align: center;' class='scrollList position_relative'>");
        ConditionListReportImpl reportImpl = null;
        SeeyonReportImpl seeyonReport = null ;
		try{
			seeyonReport = (SeeyonReportImpl)fapp.findReportByName(formAndReport[0]);
			
			if(seeyonReport == null){	
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0]));
				if(formQueryPlan != null){
					reportname = formQueryPlan.getQueryName();
					planid = formAndReport[0];
					seeyonReport = (SeeyonReportImpl)fapp.findReportByName(formQueryPlan.getQueryName());
					if(seeyonReport != null){
						Document doc  = dom4jxmlUtils.paseXMLToDoc(formQueryPlan.getPlanDefine());
						Element root = doc.getRootElement();
						Element userConditionListElement = root.element(IXmlNodeName.UserConditionList);
						if(userConditionListElement != null){
							ConditionListReportImpl userConditionList = new ConditionListReportImpl();
							userConditionList.loadFromXml(userConditionListElement);
							userConditionList.setProvider(seeyonReport.getDBProvider());
							reportImpl = (ConditionListReportImpl)userConditionList.copy();						
						}
					}					
				}
			}	
			
			if(seeyonReport != null) {
				html.append(retultToTable(reportname,formAndReport[1],seeyonReport, reportImpl, fapp));
			}			
		}
		catch(SeeyonFormException e){
			log.error("填充表单统计图标栏目数据过程中出现异常：", e);
		}
        html.append("</div>");
        ht.setHeight(String.valueOf(NumberUtils.toInt(preference.get("height"), DEFAULT_HEIGHT)));
		ht.setHtml(html.toString());
		ht.setModel(HtmlTemplete.ModelType.inner);
		ht.setShowBottomButton(true);
		ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/formreport.do?method=formReport&formid=" + formAndReport[1] + "&planid=" + planid + "&isWindow=true&reportname=%" + reportname + "%FormReport");
        return ht ;
	}
	
	private String retultToTable(String reportName,String formId, SeeyonReportImpl seeyonReport, ConditionListReportImpl reportImpl, SeeyonForm_ApplicationImpl fapp)throws SeeyonFormException{
		BASE64Encoder baseEncoder = new BASE64Encoder();
		String penetrate = "";
		StringBuilder str = new StringBuilder() ;
		IReportResult resultData = seeyonReport.showReport(reportImpl);
		String background = "";
        String[] sumDataField = null;
		if ((seeyonReport.getSumDataColumList().size() == 1)
				&& (seeyonReport.getSchema().isAcrossReport())
				&& (seeyonReport.getSchema().getDataColumList().size() == 1)) {
			sumDataField = new String[resultData.getColCount()];
			ReportController.addSepcSumDataField(resultData, seeyonReport.getSumDataColumList(),
					sumDataField, fapp);
		} else if (seeyonReport.getSumDataColumList().size() != 0) {
			sumDataField = new String[resultData.getColCount()];
			ReportController.addSumDataField(resultData, seeyonReport.getSumDataColumList(),
					sumDataField, fapp);
		}
		String showdetail = "";
		if(seeyonReport != null && seeyonReport.getShowDetail() != null){
	    	String formName= resultData.getRunner().getReport().getShowDetail().getFormName();
	    	String opername = resultData.getRunner().getReport().getShowDetail().getOperName();
	    	showdetail = formName+"."+opername;
	    	penetrate = "true";
	    }
		if(resultData.getSchema().isAcrossReport() && resultData.getSchema().getDataColumList().size() != 1){
			background = "background: url(../collaboration/images/manage.stat.bg2.gif) repeat-x;";
		} else {
			background = "background: url(../collaboration/images/manage.stat.bg3.gif) repeat-x;";
		}
		
		str.append("<form id=\"showReportForm\" method=\"post\" name=\"showReportForm\" action=\"\">");
		str.append( "<table id=\"ftable\" width=\"100%\" style=\"border-top: solid 1px #D7D7D7;border-left: solid 1px #D7D7D7;" + background + "\" cellspacing=\"0\" cellpadding=\"0\" class='sort headcenter'>");
			for(int i = 0; i < resultData.getRowCount(); i++){			
				if(i==0){
					str.append("<tr height = '20' style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
				}else{
					str.append("<tr height = '20'>") ;			  		
				}
				
				for(int j = 0; j < resultData.getColCount(); j++){
					if(resultData.getCell(i,j) instanceof Result_Head){
						if((resultData.getSchema().isAcrossReport()) && 
					   		(resultData.getSchema().getDataColumList().size() != 1)){		
							str.append("<td rowspan=2 class=\"sorttd\" nowrap style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
							str.append("<div align=\"center\">") ;	
							str.append("&nbsp;" + resultData.getCell(i,j).getShowString() + "&nbsp;");
							str.append("</div>") ;	
							str.append("</td>") ;		
			 			}else{	
		 					str.append("<td class=\"sorttd\" nowrap style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
		 					str.append("<div align=\"center\">") ;	
			 				str.append("&nbsp;" + resultData.getCell(i,j).getShowString()+"&nbsp;");
			 				str.append("</div>") ;
		 					str.append("</td>") ;	
			 			}
					}else if(resultData.getCell(i,j) instanceof Result_AcrossColHead){	 
		 
						str.append("<td colspan="+((Result_AcrossColHead)resultData.getCell(i,j)).getWidth()+" value="+baseEncoder.encode(((Result_AcrossColHead)resultData.getCell(i,j)).getValue().getBytes())+" class=\"sorttd\" nowrap style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
						str.append("<div align=\"center\">") ;
						str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...") +"&nbsp") ;
						str.append("</div>") ;	
						str.append("</td>") ;
	
			  		}else if(resultData.getCell(i,j) instanceof Result_DataColum){
	
			  			str.append("<td class=\"sorttd\"   value=\""+baseEncoder.encode(((Result_DataColum)resultData.getCell(i,j)).getFieldName().getBytes())+"\" nowrap style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
			  			str.append("<div align=\"center\">") ;
			  			str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...") + "&nbsp;") ;				 				
			  			str.append("</div>") ;	
			  			str.append("</td>") ;
			
		 			}else if(resultData.getCell(i,j) instanceof Result_Value){
		
		 				str.append("<td nowrap class=\"sorttd\" style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\"");
		 				
		 					if(((Result_Value)resultData.getCell(i,j)).getValue() != null)
		 						str.append(" title=\"" + resultData.getCell(i,j).getShowString() + "\"");
		 						str.append(" value=\"" + baseEncoder.encode(((Result_Value)resultData.getCell(i,j)).getValue().getBytes()) +"\">");
		 						str.append("<div align=\"center\">"  ) ;                                      
							
							 if(j < resultData.getSchema().getRowHeadList().size()){	
							   if(resultData.getCell(i,j).getShowString() == null || "null".equals(resultData.getCell(i,j).getShowString()) || "".equals(resultData.getCell(i,j).getShowString()))
								   str.append("&nbsp;");
							   else
								   str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"..."));
							 }
			 				else if(resultData.getCell(i,j).getShowString() == null || "null".equals(resultData.getCell(i,j).getShowString()) || "".equals(resultData.getCell(i,j).getShowString())){
			 					 str.append("&nbsp;");
			 				 } else{
			 					if("true".equals(penetrate)){
									   str.append("<a name=\"fa\"  style=\"color:blue;cursor:hand;\" onclick=\"getQueryCondition("+i+","+j+","+resultData.getSchema().getRowHeadList().size()+")\">");
								 }
			 					str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...")) ;
			 					if("true".equals(penetrate)){
									   str.append("</a>");
								 }
			 				} 			 					
							 str.append("</div>" ) ;
							 str.append("</td>" ) ;		
		   			}else if(resultData.getCell(i,j) == null){	 		
			 			str.append("<td class=\"sorttd\" style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\" nowrap>") ;
			 			str.append(	"<div align=\"center\">&nbsp;</div>") ;
			 			str.append("</td>") ;		
					}
				}
				str.append("</tr>");	
			}
			if(sumDataField != null){
				str.append("<tr>");
				for(int k = 0; k < sumDataField.length; k++){
					str.append("<td class=\"sorttd\" style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\" nowrap>\r\n<div align=\"center\">\r\n");
					if(Strings.isNotBlank(sumDataField[k])){
						str.append(sumDataField[k]);
					}
					str.append("&nbsp;</div>\r\n</td>\r\n");
				}
				str.append("</tr>");
			}
			str.append( "</TABLE>"	) ;
			if("true".equals(penetrate)){
				
				str.append("<input type=\"hidden\" name=\"formid\" value=\""+formId+"\" />");
				str.append("<input type=\"hidden\" name=\"reportname\" value=\""+reportName+"\" />");
				str.append("<input type=\"hidden\" name=\"showdetail\" value=\""+showdetail+"\" />");
				
			}
			str.append("</form>");
		return str.toString() ;
	}

	@Override
	public boolean isAllowUsed() {
		User user = CurrentUser.get();
		return !user.isAdmin();
	}

	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if(Strings.isBlank(singleBoardId)){
			return false;
		}
		String[] formAndReport = getFormAndQuery(singleBoardId);
		
		if(formAndReport == null ||  formAndReport.length < 2 
				|| Strings.isBlank(formAndReport[0]) || Strings.isBlank(formAndReport[1])){
			return false;
		}
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndReport[1])) ;
		if(fapp == null){
			return false;
		}
		
		try{
			ISeeyonReport seeyonReport = fapp.findReportByName(formAndReport[0]) ;
			if(seeyonReport != null){
				try {
					return iOperBase.checkAccess(CurrentUser.get(), NumberUtils.toLong(formAndReport[1]), formAndReport[0], IPagePublicParam.C_iObjecttype_Report);
				} catch (Exception e) {
					log.error("表单统计授权校验失败", e);
					return false;
				}
			}
			
			if(NumberUtils.isNumber(formAndReport[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0])) ;
				if(formQueryPlan != null){
					return true;
				}
			}
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
		return false;
	}
}
