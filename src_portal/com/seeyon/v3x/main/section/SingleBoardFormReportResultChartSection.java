package com.seeyon.v3x.main.section;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.report.ReportChartInfo;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
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
 * 表单统计图表栏目
 * @author xgghen 2011-03-24
 */
public class SingleBoardFormReportResultChartSection extends BaseSection{
	
	private static final Log log = LogFactory.getLog(SingleBoardFormReportResultChartSection.class);
	private static final SeeyonForm_Runtime runtime = SeeyonForm_Runtime.getInstance();
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	
	@Override
	public String getId() {
		return "singleBoardFormReportResultChartSection";
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
			String reportShowType = formAndReport[2];
			if(seeyonReport != null){
				if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
					return Strings.isNotBlank(formAndReport[3]) ? formAndReport[3] : null;
				} else {
					return seeyonReport.getReportName() ;
				}
			}
			if(NumberUtils.isNumber(formAndReport[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0])) ;
				if(formQueryPlan != null){
					if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
						return Strings.isNotBlank(formAndReport[3]) ? formAndReport[3] : null;
					}
					return formQueryPlan.getPlanName() ;
				}
			}
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
			
		return null;
	}
	@Override
	public String getBaseName(Map<String, String> preference) {
		return this.getName(preference);
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
	private static final int DEFAULT_HEIGHT = 200;

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
				html.append(reportResultToHtml(reportImpl, seeyonReport, chartType, false, true, null, singleBoardId, width, height));
			}			
		}
		catch(SeeyonFormException e){
			log.error("填充表单统计图标栏目数据过程中出现异常：", e);
		}
        html.append("</div>");
		ht.setHtml(html.toString());
		ht.setHeight(String.valueOf(height + 8));
		ht.setModel(HtmlTemplete.ModelType.inner);
		ht.setShowBottomButton(true);
		ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/formreport.do?method=formReport&formid=" + formAndReport[1] + "&planid=" + planid + "&isWindow=true&reportname=%" + reportname + "%FormReport");
        return ht ;
	}
	
	private String reportResultToHtml(ConditionListReportImpl reportImpl,SeeyonReportImpl seeyonReport ,
			String chartType,boolean is3d,boolean isRowToCol,String chartName ,String singleBoardId, int width, int height){		
		if( seeyonReport == null || singleBoardId == null || chartType == null){
			return "" ;
		}
		try{
			String[] formAndReport = getFormAndQuery(singleBoardId);
			if(formAndReport.length >= 4){
				String showType = formAndReport[2];
				if(FormSectionWebModel.ReportShowType.Picture.toString().equals(showType)){
					chartName =  formAndReport[3];
					Map<String,ReportChartInfo> map = seeyonReport.getChartInfos();
					if(map ==  null || Strings.isBlank(chartName)){
						return "";
					}
					if(Strings.isBlank(chartName)){											
						for(String keys : map.keySet()){
							chartName = keys;
							break;
						}						
					}else if(Strings.isNotBlank(chartName)){
						if(map.get(chartName) == null){
							return "";
						}
					}
				}
			}
			StringBuilder str = new StringBuilder();
			final String src = "/seeyon/formreport.do?method=showReportMap&formid="+Functions.urlEncoder(formAndReport[1])+
							   "&reportname="+Functions.urlEncoder(formAndReport[0])+ "&is3d="+is3d+"&isRowToCol="+isRowToCol+
							   "&width=" + width + "&height=" + height + "&chartName="+Functions.urlEncoder(chartName);
			str.append("<img  align='middle' src =\""+src+"&chartType="+chartType+"\" >");
			return  str.toString();
		}catch(Exception e){
			log.error("统计结果转成HTML出问题", e);
		}
		return "" ;
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
			String reportShowType = formAndReport[2];
			if(seeyonReport != null){
				try {
					if(!iOperBase.checkAccess(CurrentUser.get(), NumberUtils.toLong(formAndReport[1]), formAndReport[0], IPagePublicParam.C_iObjecttype_Report)){
						return false;
					}
				} catch (Exception e) {
					log.error("表单统计授权校验失败", e);
					return false;
				}
				if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
					return Strings.isNotBlank(formAndReport[3]) ? true : false;
				} else {
					return true;
				}
			}
			if(NumberUtils.isNumber(formAndReport[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndReport[0])) ;
				if(formQueryPlan != null){
					if(FormSectionWebModel.ReportShowType.Picture.name().equals(reportShowType)){
						return Strings.isNotBlank(formAndReport[3]) ? true : false;
					}
					return true;
				}
			}
			
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
			
		return false;
	}
}
