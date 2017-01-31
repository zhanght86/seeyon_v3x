package com.seeyon.v3x.main.section;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import sun.misc.BASE64Encoder;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.report.ReportChartInfo;
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
import com.seeyon.v3x.report.chart.tool.ChartUtil;
import com.seeyon.v3x.space.domain.FormSectionWebModel;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;
/**
 * @author xgghen 2011-03-24
 * @deprecated	废弃，图表和表格混在一起，参数配置及展现等不便分离
 * @see SingleBoardFormReportResultChartSection
 * @see SingleBoardFormReportResultTableSection
 */
public class SingleBoardformreportResultSection extends BaseSection{
	
	private static final Log log = LogFactory.getLog(SingleBoardformreportResultSection.class);
	private static final SeeyonForm_Runtime runtime = SeeyonForm_Runtime.getInstance();
	
	@Override
	public String getId() {
		return "singleBoardformreportResultSection";
	}
	
	@Override
	protected String getName(Map<String, String> preference) {
		/*String showtitleName = preference.get("showtitleName");
		
		if(Strings.isNotBlank(showtitleName)){
			return showtitleName ;
		}*/
		
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
		try{
			ISeeyonReport seeyonReport = fapp.findReportByName(formAndReport[0]) ;
			String reportShowType = formAndReport[2];
			if(seeyonReport != null){
				if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
					return Strings.isNotBlank(formAndReport[3]) ? formAndReport[3] : null;
				} else {
					return seeyonReport.getReportName() ;
				}
			}
			FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
			FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0])) ;
			if(formQueryPlan != null){
				if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
					return Strings.isNotBlank(formAndReport[3]) ? formAndReport[3] : null;
				}
				return formQueryPlan.getPlanName() ;
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
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	private static final int DEFAULT_WIDTH = 276;
	private static final int DEFAULT_HEIGHT = 208;

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete();        
		String singleBoardId = preference.get(PortletEntityProperty.PropertyName.singleBoardId.name());
		String[] formAndReport = getFormAndQuery(singleBoardId);
		String chartType = StringUtils.defaultIfEmpty(preference.get("chartType"), ChartUtil.CHART_TYPE_BAR);
		int width = NumberUtils.toInt(preference.get("chartWidth"), DEFAULT_WIDTH);
		int height = NumberUtils.toInt(preference.get("chartHeight"), DEFAULT_HEIGHT);
		
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
        
        html.append("<div id='' style='vertical-align: middle;text-align: center;overflow-y:hidden;' class='scrollList position_relative'>");
        ConditionListReportImpl reportImpl = null;
        SeeyonReportImpl seeyonReport = null ;
		try{
			seeyonReport = (SeeyonReportImpl)fapp.findReportByName(formAndReport[0]);
			
			if(seeyonReport == null){	
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0]));
				if(formQueryPlan != null){
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
				html.append(reportResultToHtml(reportImpl, seeyonReport, chartType, false, true, null, singleBoardId, width, height));
			}			
		}
		catch(SeeyonFormException e){
			log.error("填充表单统计图标栏目数据过程中出现异常：", e);
		}
        html.append("</div>");
		ht.setHtml(html.toString());
		ht.setHeight(String.valueOf(height + 50));
		ht.setModel(HtmlTemplete.ModelType.inner);
		ht.setShowBottomButton(true);
		ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/formreport.do?method=formReport&formid=" + formAndReport[1] + "&reportname=%" + formAndReport[0] + "%FormReport");
        return ht ;
	}
	
	private String reportResultToHtml(ConditionListReportImpl reportImpl,SeeyonReportImpl seeyonReport ,
			String chartType,boolean is3d,boolean isRowToCol,String chartName ,String singleBoardId, int width, int height){		
		if( seeyonReport == null || singleBoardId == null || chartType == null){
			return "" ;
		}
		try{
			String[] formAndReport = getFormAndQuery(singleBoardId);
			if(formAndReport.length < 4){
				IReportResult resultData = seeyonReport.showReport(reportImpl) ;	
				return retultToTable(resultData) ;		
			}else{
				String showType = formAndReport[2] ;			
				if(FormSectionWebModel.ReportShowType.Talbe.toString().equals(showType)){
					IReportResult resultData = seeyonReport.showReport(reportImpl) ;	
					return retultToTable(resultData) ;
				}else{					
					chartName =  formAndReport[3] ;
					Map<String,ReportChartInfo> map = seeyonReport.getChartInfos() ;
					
					if(map ==  null || Strings.isBlank(chartName)){
						IReportResult resultData = seeyonReport.showReport(reportImpl) ;	
						return retultToTable(resultData) ;
					}
					if(Strings.isBlank(chartName)){											
						for(String keys : map.keySet()){
							chartName = keys ;
							break ;
						}						
					}else if(Strings.isNotBlank(chartName)){
						if(map.get(chartName) == null){
							return "" ;
						}
					}
					
				}							
			}
			

			StringBuilder str = new StringBuilder() ;
			
			final String src = "/seeyon/formreport.do?method=showReportMap&formid="+Functions.urlEncoder(formAndReport[1])+
							   "&reportname="+Functions.urlEncoder(formAndReport[0])+ "&is3d="+is3d+"&isRowToCol="+isRowToCol+
							   "&width=" + width + "&height=" + height + "&chartName="+Functions.urlEncoder(chartName) ;
			str.append("<img  align='middle' src =\""+src+"&chartType="+chartType+"\" >");
			return  str.toString();
		}catch(Exception e){
			log.error("统计结果转成HTML出问题", e);
		}
		return "" ;
	}
	
	private String retultToTable(IReportResult resultData){
		BASE64Encoder baseEncoder = new BASE64Encoder();
		StringBuilder str = new StringBuilder() ;
		String background = "";
		if(resultData.getSchema().isAcrossReport() && resultData.getSchema().getDataColumList().size() != 1){
			background = "background: url(../collaboration/images/manage.stat.bg2.gif) repeat-x;";
		} else {
			background = "background: url(../collaboration/images/manage.stat.bg3.gif) repeat-x;";
		}
		str.append( "<table width=\"100%\" style=\"border-top: solid 1px #D7D7D7;border-left: solid 1px #D7D7D7;" + background + "\" cellspacing=\"0\" cellpadding=\"0\" class='sort headcenter'>");
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
							str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...")+"&nbsp;");
							str.append("</div>") ;	
							str.append("</td>") ;		
			 			}else{	
		 					str.append("<td class=\"sorttd\" nowrap style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">") ;
		 					str.append("<div align=\"center\">") ;	
			 				str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...")+"&nbsp;");
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
		 						str.append("value=\"" + baseEncoder.encode(((Result_Value)resultData.getCell(i,j)).getValue().getBytes()) +"\">");
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

			 					 str.append(Functions.getLimitLengthString(resultData.getCell(i,j).getShowString(),20,"...")) ;
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
			str.append( "</TABLE>"	) ;
			
		return str.toString() ;
	}

	@Override
	public boolean isAllowUsed() {
		User user = CurrentUser.get();
		return !user.isAdmin();
	}
}
