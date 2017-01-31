package www.seeyon.com.v3x.form.controller.report;


import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jfree.chart.JFreeChart;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.ValueImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputExtendManager;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.FormApp;
import www.seeyon.com.v3x.form.controller.formservice.ChangeObjXml;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.query.QueryController;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormDataState;
import www.seeyon.com.v3x.form.domain.FormOwnerList;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.chart.BarChartInfoFactory;
import www.seeyon.com.v3x.form.manager.define.chart.LineChartInfoFactory;
import www.seeyon.com.v3x.form.manager.define.chart.PieChartInfoFactory;
import www.seeyon.com.v3x.form.manager.define.chart.ReportChartGenerator;
import www.seeyon.com.v3x.form.manager.define.chart.inf.IChartFacotry;
import www.seeyon.com.v3x.form.manager.define.chart.inf.IChartInfoFactory;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonInputExtend;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.ParseUserCondition;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QueryRunner;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.ReportDataColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportHeadColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportSumDataColum;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.IConditionList_Report;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.ReportResultImpl;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_AcrossColHead;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_DataColum;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.Result_Value;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.inf.IReportResult;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.IConditionList;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.shareMap.V3xShareMap;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.report.chart.impl.ReportChart;
import com.seeyon.v3x.report.chart.tool.ChartUtil;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.SetContentType;
public class ReportController  extends BaseController {
	private static Log log = LogFactory.getLog(ReportController.class);
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private MetadataManager metadataManager;	
	private CollaborationController collaborationController;
	private TempleteCategoryManager templeteCategoryManager;
	private FileToExcelManager fileToExcelManager;

	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	
	public CollaborationController getCollaborationController() {
		return collaborationController;
	}
	public void setCollaborationController(CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}
	
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	
	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	
	public FormDaoManager getFormDaoManager() {
		return formDaoManager;
	}
	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
	public ModelAndView formReportSet(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_statset");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		if(sessionobject == null){
			ModelAndView mavform = new ModelAndView("form/formcreate/formcreateBorderFrame");
			return mavform;
		}
		if(sessionobject.getEditflag() != null 
				&&!"".equals(sessionobject.getEditflag())
				&&!"null".equals(sessionobject.getEditflag())){
			if(sessionobject.getPageflag().equals(IPagePublicParam.BASEINFO)){
	            //baseinfo数据收集
				OperHelper.baseInfoCollectData(request,sessionobject);		
			}else if(sessionobject.getPageflag().equals(IPagePublicParam.INPUTDATA)){
	            //收集inputdata页面的数据,当跳转到operconfig时收集数据，在本页操作不再进行收集操作
				if(request.getParameter("saveoperlst") == null
					&&request.getParameter("deltype") == null
					&&request.getParameter("selenum") == null){
//					增加防护
					try{
					OperHelper.inputDataCollectData(request, sessionobject);
					}catch(SeeyonFormException e){
						log.error("保存录入定义页面信息时出错", e);
						List<String> lst = new ArrayList<String>();
						lst.add(e.getToUserMsg());
						OperHelper.creatformmessage(request,response,lst);
					}
				}
			}else if(sessionobject.getPageflag().equals(IPagePublicParam.BINDINFO)){
				//添加信息管理绑定信息 by wusb at 2010-03-17
				BindHelper.systemSaveAppBindMain(request, sessionobject);
			}
		}
		if("1".equals(request.getParameter("bindLastStep"))){
			//添加信息管理绑定信息 by wusb at 2010-03-17
			BindHelper.systemSaveAppBindMain(request, sessionobject);
		}
		String flowid = request.getParameter("flowid");
		List newlist = new ArrayList();
		if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid)){
			String[] flow = flowid.split("↗");
			for(int i=0;i<flow.length;i++){
				newlist.add(flow[i]);
			}
		}
		if(flowid !=null)
			sessionobject.setFlowidlist(newlist);
		sessionobject.setPageflag(IPagePublicParam.REPORTSET);		
		return mav; 
	}
	
	public ModelAndView formReportShow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("form/formreport/formreportBorderFrame");
    }
	
	public ModelAndView formReportIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_statset");   
		return mav; 
    }
	
	public ModelAndView formReportCustomSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_customset");   
		return mav; 
	}
	
	public ModelAndView formCrossReportResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_crossreportresult");   
		return mav; 
	}
	public ModelAndView formConditionSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_conditionset");   
		return mav; 
	}
	public ModelAndView formInputRangeSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_inputrangeset");   
		return mav; 
	}
	public ModelAndView formReportDataField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_statdatafield");   
		return mav; 
	}
	public ModelAndView formReportDataFieldSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_statdatafieldset");   
		return mav; 
	}
	public ModelAndView formReportRowHeader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_rowheader");   
		return mav; 
	}
	public ModelAndView formReportColumnHeader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_columnheader");   
		return mav; 
	}
	
	public ModelAndView formSumReportDataField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_statresultsum");   
		return mav; 
	}
	
	public ModelAndView chartSetForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/chartSetForm");   
		return mav; 
	}
	
	public ModelAndView chartShowForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/chartShowForm"); 
		Map<String,String> displayConditions = new LinkedHashMap<String,String>();
        IReportResult resultData = getResultData(request,response,displayConditions);
        
        String key = "ReportResult" + UUIDLong.longUUID();
        V3xShareMap.put(key, resultData);
        mav.addObject("reportResultKey", key);
		return mav; 
	}
	
	private static final int DEFAULT_CHART_WIDTH = 400;
	private static final int DEFAULT_CHART_HEIGHT = 300;
	
	@SetContentType
	public ModelAndView showReportMap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = request.getParameter("reportResultKey");
		IReportResult resultData = (IReportResult)V3xShareMap.get(key);
		 if(Strings.isNotBlank(request.getParameter("formid"))){
			String formid = request.getParameter("formid");
	        String reportname = new String(request.getParameter("reportname").getBytes("8859_1"), "UTF-8");
	        String chartName = new String(request.getParameter("chartName").getBytes("8859_1"), "UTF-8");
	        String chartType = request.getParameter("chartType");
	        String planid = request.getParameter("planid");
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
					.getInstance().getAppManager().findById(Long.parseLong(formid));
	        SeeyonReportImpl report = (SeeyonReportImpl)fapp.findReportByName(reportname); 
	        ConditionListReportImpl reportImpl	 = null ;
	       
	        if(report == null){		
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(reportname)) ;
				if(formQueryPlan != null){
					report = (SeeyonReportImpl)fapp.findReportByName(formQueryPlan.getQueryName());
					if(report != null){
						Document doc  = dom4jxmlUtils.paseXMLToDoc(formQueryPlan.getPlanDefine());
						Element root = doc.getRootElement();
						Element userConditionListElement = root.element(IXmlNodeName.UserConditionList);
						if(userConditionListElement != null){
							ConditionListReportImpl userConditionList = new ConditionListReportImpl();
							userConditionList.loadFromXml(userConditionListElement);
							userConditionList.setProvider(report.getDBProvider()) ;
							reportImpl = (ConditionListReportImpl)userConditionList.copy() ;						
						}
					}					
				}
	        }
	        
	        if(resultData == null){
	        	resultData =  report.showReport(reportImpl);
	        }
	        if(report != null){
		        ReportChartInfo chartInfo = report.getChartInfos().get(chartName);
		    	IChartInfoFactory chartInfoFacotry = null;
		    	if(ChartUtil.CHART_TYPE_LINE.equals(chartType)){
		    		chartInfoFacotry = new LineChartInfoFactory();
		    	}else if(ChartUtil.CHART_TYPE_BAR.equals(chartType)){
		    		chartInfoFacotry = new BarChartInfoFactory();
		    	}else if(ChartUtil.CHART_TYPE_PIE.equals(chartType)){
		    		chartInfoFacotry = new PieChartInfoFactory();
		    	}
		    	boolean is3d = Boolean.parseBoolean(request.getParameter("is3d"));
		    	boolean isRowToCol = Boolean.parseBoolean(request.getParameter("isRowToCol"));
		    	IChartFacotry chartFacotry = new ReportChartGenerator(chartInfo, resultData,isRowToCol);
		    	ReportChart chart = chartFacotry.createReportChart(chartInfoFacotry, is3d);
		    	JFreeChart jfreechart = chart.doDraw();
				ServletOutputStream out = response.getOutputStream();
				try{
		    		response.setContentType("image/jpeg");		    		
		    		int width = NumberUtils.toInt(request.getParameter("width"), DEFAULT_CHART_WIDTH);
		    		int height = NumberUtils.toInt(request.getParameter("height"), DEFAULT_CHART_HEIGHT);
		    		ChartUtil.createImg(jfreechart,out , width, height, "JEPG");
		    	}catch(Exception e){
		    		log.error("创建图表出现异常", e) ;
		    	}finally{
		    		if(out != null){
		    			out.close() ;
		    		}
		    	}		    	
	        }	    	
		}
		return null; 
	}
	

	
	public ModelAndView addCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession httpSession = request.getSession();
		SessionObject sessionObject = (SessionObject)httpSession
		         .getAttribute("SessionObject");
		ReportObject reportObject = new ReportObject();
		bind(request,reportObject);			
		if(!"".equals(reportObject.getReportAuthorValue()) || !"null".equalsIgnoreCase(reportObject.getReportAuthorValue()) || reportObject.getReportAuthorValue() !=null){
	    	reportObject.setReportAuthorName("");
	    	reportObject.setReportAuthorValue("");
	    }
		String showDetail = request.getParameter("detailid");	
		String formnames = request.getParameter("formnames");	
		String formids = "";
		String operationids  = "";
		if(Strings.isNotBlank(showDetail)){
		    for(int a=0;a<showDetail.split("\\|").length ;a++){
		    	 String showDetailsArray[] = showDetail.split("\\|");
		    	 String showDetails[] = showDetailsArray[a].split("\\.");
		    	 formids += showDetails[0]+"|";
		    	 operationids += showDetails[1]+"|";   		    	       		         	
		    }
		}
		Map<String,ReportChartInfo> chartInfos = new LinkedHashMap<String, ReportChartInfo>();
		String[] chartNames = request.getParameterValues("chartName");
		String[] rowNames = request.getParameterValues("rowNames");
		String[] colNames = request.getParameterValues("colNames");
		for(int i = 0; i < chartNames.length; i++){
			if(Strings.isBlank(chartNames[i])) continue;
			ReportChartInfo chartInfo = new ReportChartInfo();
			chartInfo.setName(chartNames[i]);
			chartInfo.setRowNames(rowNames[i].trim());
			chartInfo.setColNames(colNames[i].trim());
			chartInfos.put(chartNames[i], chartInfo);
		}
		reportObject.setChartInfos(chartInfos);
	    reportObject.setFormId(formids);
	    reportObject.setOperationId(operationids);
	    reportObject.setForm(formnames);
	    
	    //生成report的xml
		reportObject.setColumnheadValue(request.getParameter("columnheadValue"));
		reportObject.setRowheadValue(request.getParameter("rowheadValue"));
		reportObject.setSumDataFieldValue(request.getParameter("sumDataFieldvalue"));                                                       

		String reportid =  String.valueOf(Long.valueOf(UUIDLong.longUUID()));
		reportObject.setReportid(reportid);
		
        sessionObject.getReportConditionList().add(reportObject);
		return super.redirectModelAndView("/formreport.do?method=formReportIndex");
	}
	
//	专为保存授权 提供的方法
	public ModelAndView addConditionforau(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject)session
										.getAttribute("SessionObject");
		List<ReportObject> reportConditionList = sessionObject.getReportConditionList();	
		String queryN = arg0.getParameter("queryN");
		String authorvalue = arg0.getParameter("reportauthorvalue");
		
		for(int i=0;i<reportConditionList.size();i++){
			ReportObject reportObject = (ReportObject)reportConditionList.get(i);		
			if(reportObject.getReportName().equals(queryN)){		   
				bind(arg0, reportObject);
				reportObject.setReportAuthorValue(authorvalue);
			    break;
	         }
		}
		return super.redirectModelAndView("/formreport.do?method=formReportIndex");	  
	} 
	
	public ModelAndView delCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//int id=Integer.parseInt(request.getParameter("id"));
		String reportallname = request.getParameter("reportallname");
		HashMap reportnamemap = new HashMap();
		if(reportallname.indexOf("↗") > -1){
			   String[] idname = reportallname.split("↗");
			   for(int i = 0 ; i < idname.length; i++){
				   reportnamemap.put(idname[i], idname[i]) ;			   
			   }
		}
		HttpSession httpSession = request.getSession();
		SessionObject sessionObj = (SessionObject)httpSession.getAttribute("SessionObject");
		//sessionObj.getReportConditionList().remove(id);
		for(int i=0;i<sessionObj.getReportConditionList().size();i++){
	    	ReportObject queryobj =(ReportObject)sessionObj.getReportConditionList().get(i);
	    	if(reportnamemap.get(queryobj.getReportName()) !=null){
	    		sessionObj.getReportConditionList().remove(i);
	    		i--;
	    	}	    		
	    }
		return super.redirectModelAndView("/formreport.do?method=formReportIndex");
	}
	
	public ModelAndView updateCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
		int id = Integer.parseInt(request.getParameter("id"));	
		String reportname = request.getParameter("reportName");
		HttpSession httpSession = request.getSession();
		SessionObject sessionObject =(SessionObject)httpSession.getAttribute("SessionObject");
		List<ReportObject> reportConditionList = sessionObject.getReportConditionList();
		for(int i=0;i<reportConditionList.size();i++){
		  ReportObject reportObject = (ReportObject)reportConditionList.get(i);
		 if(i == id){
			 if(!"".equals(reportname) && !"null".equals(reportname) && reportname !=null){
				 bind(request,reportObject);
				 if("null".equals(reportObject.getReportAuthorValue()))
					 reportObject.setReportAuthorValue("");
				 if("null".equals(reportObject.getReportAuthorName()))
					 reportObject.setReportAuthorName("");
				 /*
				     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
				     * 视图1.操作|视图2.操作  .......
				     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
				     * 操作为：操作1|操作2的形式
				     */
	      			String showDetail = request.getParameter("detailid");	
	      			String formnames = request.getParameter("formnames");	
	      			String formids = "";
	      			String operationids  = "";
	      			if(Strings.isNotBlank(showDetail)){
		      		    for(int a=0;a<showDetail.split("\\|").length ;a++){
		      		    	 String showDetailsArray[] = showDetail.split("\\|");
		      		    	 String showDetails[] = showDetailsArray[a].split("\\.");
		      		    	 formids += showDetails[0]+"|";
		      		    	 operationids += showDetails[1]+"|";   		    	       		         	
		      		    }	
	      			}
	      		   reportObject.setFormId(formids);
	      		   reportObject.setOperationId(operationids);
	      		   reportObject.setForm(formnames);
			 }else{
				 String reportAuthorName = request.getParameter("reportAuthorName");
				 String reportAuthorValue = request.getParameter("reportAuthorValue");
				 reportObject.setReportAuthorName(reportAuthorName);
				 reportObject.setReportAuthorValue(reportAuthorValue);
//				对showdetail特殊处理
      			String showDetail = request.getParameter("detailid");	
      			String formnames = request.getParameter("formnames");	
      			String formids = "";
      			String operationids  = "";
      			if(Strings.isNotBlank(showDetail)){
      				for(int a=0;a<showDetail.split("\\|").length ;a++){
	      		    	 String showDetailsArray[] = showDetail.split("\\|");
	      		    	 String showDetails[] = showDetailsArray[a].split("\\.");
	      		    	 formids += showDetails[0]+"|";
	      		    	 operationids += showDetails[1]+"|";   		    	       		         	
	      		    }	      				
      			}
	      		  reportObject.setFormId(formids);
	      		  reportObject.setOperationId(operationids);
	      		  reportObject.setForm(formnames);
			 }
				Map<String,ReportChartInfo> chartInfos = new LinkedHashMap<String, ReportChartInfo>();
				String[] chartNames = request.getParameterValues("chartName");
				String[] rowNames = request.getParameterValues("rowNames");
				String[] colNames = request.getParameterValues("colNames");
				for(int k = 0; k < chartNames.length; k++){
					if(Strings.isBlank(chartNames[k])) continue;
					ReportChartInfo chartInfo = new ReportChartInfo();
					chartInfo.setName(chartNames[k]);
					chartInfo.setRowNames(rowNames[k]);
					chartInfo.setColNames(colNames[k]);
					chartInfos.put(chartNames[k], chartInfo);
				}
				reportObject.setChartInfos(chartInfos);
             //生成report的xml
             reportObject.setColumnheadValue(request.getParameter("columnheadValue"));
             reportObject.setRowheadValue(request.getParameter("rowheadValue"));
             reportObject.setSumDataFieldValue(request.getParameter("sumDataFieldvalue"));   
             break;
		  }
		}				
		return super.redirectModelAndView("/formreport.do?method=formReportIndex");
	}
	
//	统计条件授权 更新 操作
	public ModelAndView updateConditionAuth(HttpServletRequest request,HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		String reportname = request.getParameter("reportName");
		List<ReportObject> reportConditionList = sessionObject.getReportConditionList();
		//int id = Integer.parseInt(arg0.getParameter("id"));
	    String ids = request.getParameter("ids");
	    HashMap reportidmap = new HashMap();
		if(ids.indexOf(",") > -1){
			String[] idsarry = ids.split(",");
			   for(int i = 0 ; i < idsarry.length; i++){
				   reportidmap.put(idsarry[i], idsarry[i]) ;			   
			   }
		}   
		for(int i=0;i<reportConditionList.size();i++){
			ReportObject reportObject = (ReportObject)reportConditionList.get(i);		
	            //if(i == id){	
		     Integer id = i;
	            if(reportidmap.get(id.toString()) != null){
	               if(!"".equals(reportname) && !"null".equals(reportname) && reportname !=null){
	            	   bind(request,reportObject);

	            	   /*
	            	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	            	     * 视图1.操作|视图2.操作  .......
	            	     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	            	     * 操作为：操作1|操作2的形式
	            	     */
	            		String showDetail = request.getParameter("detailid");	
	            		String formnames = request.getParameter("formnames");	
	            		String formids = "";
	            		String operationids  = "";
	            		if(!"".equals(showDetail)){
	            			for(int a=0;a<showDetail.split("\\|").length ;a++){
	               	    	 String showDetailsArray[] = showDetail.split("\\|");
	               	    	 String showDetails[] = showDetailsArray[a].split("\\.");
	               	    	 formids += showDetails[0]+"|";
	               	    	 operationids += showDetails[1]+"|";   		    	       		         	
	               	    }
	            		 reportObject.setFormId(formids);
	               	     reportObject.setOperationId(operationids);
	               	     reportObject.setForm(formnames);
	            		}	
	               }else{
	            	   String reportAuthorName = request.getParameter("reportAuthorNamelist"+i);
	  				   String reportAuthorValue = request.getParameter("reportauthorValuelist"+i);

	  				   reportObject.setReportAuthorName(reportAuthorName);
		  			   if(Strings.isNotBlank(reportObject.getReportAuthorValue()) && !reportObject.getReportAuthorValue().equals(reportAuthorValue)){
							 reportObject.setChanged(true) ;
						}	  				   
	  				   reportObject.setReportAuthorValue(reportAuthorValue);
	  				   
	  				 /*
	  				     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	  				     * 视图1.操作|视图2.操作  .......
	  				     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	  				     * 操作为：操作1|操作2的形式
	  				     */
	  				 if("".equals(reportObject.getFormId()) || "null".equals(reportObject.getFormId()) || reportObject.getFormId() ==null){
	  					String showDetail = request.getParameter("detailid");	
	  					String formnames = request.getParameter("formnames");	
	  					if(!"".equals(showDetail)){
	  						String formids = "";
	  	  					String operationids  = "";
	  	  				    for(int a=0;a<showDetail.split("\\|").length ;a++){
	  	  				    	 String showDetailsArray[] = showDetail.split("\\|");
	  	  				    	 String showDetails[] = showDetailsArray[a].split("\\.");
	  	  				    	 formids += showDetails[0]+"|";
	  	  				    	 operationids += showDetails[1]+"|";   		    	       		         	
	  	  				    }
	  	  				reportObject.setFormId(formids);
	  	  			    reportObject.setOperationId(operationids);
	  	  	            reportObject.setForm(formnames);
	  					}
	  				 }
	  					
	               }
	         }
		}
		return super.redirectModelAndView("/formreport.do?method=formReportIndex");
		 
	}
	
	
	public ModelAndView formReportRowHeaderTitleSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_rowheadertitle");   
		return mav; 
	}
	
	public ModelAndView formReportColumnHeaderTitleSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/stat_columnheadertitle");   
		return mav; 
	}
	public ModelAndView formdetailhtml(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/detail");   
		return mav; 
	}
	
	public ModelAndView openShowReportQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/openShowReportQuery");   
		return mav; 
	}
	
	public ModelAndView openShowReportChartQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/openShowReportChartQuery");   
		return mav; 
	}
	
	/**
	 * 表单统计设置时的预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formReportPreViewMake(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String reporttype = request.getParameter("reporttype");
		ModelAndView mav = null;
		if("1".equals(reporttype)){
			   mav = new ModelAndView("form/formreport/stat_crossreportpreviewmake"); 
		}else if("0".equals(reporttype)){
			   mav = new ModelAndView("form/formreport/stat_statpreviewmake"); 
		}
		return mav; 
		
	}
	
	/**
	 * 表单统计执行时的预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formReportPreView(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		List<ReportObject> reportConditionList = sessionObject.getReportConditionList();
		List<ReportDataColum> datacolumlist = new ArrayList<ReportDataColum>();
		List<ReportHeadColum> headlist = new ArrayList<ReportHeadColum>();
		HashMap sumdatamap = new HashMap();
		String colhead = null;
		String reporttype = null;
		String reportname = request.getParameter("reportname");	
		String formid = request.getParameter("formid");
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid));  
		String formname = afapp.getAppName();
		for(int i=0;i<reportConditionList.size();i++){
			ReportObject reportObject = (ReportObject)reportConditionList.get(i);
            if(reportname.equals(reportObject.getReportName()) && formname.equals(reportObject.getFormname())){	
            	reportObject.getReportDataFieldValue();
				ChangeObjXml objxml = new ChangeObjXml();
				Element Computeroot = dom4jxmlUtils.paseXMLToDoc(objxml.createReportListXml(0, reportObject)).getRootElement();	
				SeeyonReportImpl  seeyon = new SeeyonReportImpl();
				seeyon.loadFromXml(Computeroot);
				reporttype = reportObject.getReportType();
				datacolumlist = seeyon.getSchema().getDataColumList();
				if("1".equals(reporttype)){
					colhead = seeyon.getSchema().getColHead().getColumTitle();
					headlist = seeyon.getSchema().getRowHeadList();
				}else if("0".equals(reporttype)){
					headlist = seeyon.getSchema().getRowHeadList();
				}		
				for(int a=0;a<seeyon.getSumDataColumList().size();a++){
					ReportSumDataColum sundata = (ReportSumDataColum)seeyon.getSumDataColumList().get(a);
					sumdatamap.put(sundata.getColumTitle(), sundata.getColumTitle());
				}
				break;
            }
		}
		QueryUserConditionDefin querydefine = new QueryUserConditionDefin();			
		querydefine.setTitle(ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request), "form.stat.statdate"));
		ModelAndView mav = new ModelAndView("form/formreport/stat_statpreview"); 
		if("1".equals(reporttype)){
		   mav = new ModelAndView("form/formreport/stat_crossreportpreview"); 
		   mav.addObject("colhead",colhead);
		}
		mav.addObject("headlist", headlist);
		mav.addObject("datalist", datacolumlist);
		mav.addObject("sumdatamap", sumdatamap);
		mav.addObject("reportname", reportname);
		mav.addObject("formname", formname);
		return mav; 
	}
	
	/**
	 * 点击表单统计后的首页面

	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formReportList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		 //对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
			sessionobject = null;
		}
		if(sessionobject ==null){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject", sessionobject);
		}
		ModelAndView mav = new ModelAndView("form/formreport/stat_statlist");
		if(!FormBizConfigUtils.validate(mav, request, response, FormBizConfigConstants.MENU_FORM_STATISTIC))
			return null;
		
		try {
			List<Long> appidlist = new ArrayList<Long>();
			String formids = request.getParameter("formIds");
			if(formids !=null){
				String[] formidnum = formids.split(",");
				for(int i=0;i<formidnum.length;i++){
					if(formidnum[i] !=null && !"".equals(formidnum[i].trim()) && !"null".equals(formidnum[i]))
						appidlist.add(Long.parseLong(formidnum[i]));
				}
			}
			//如果是从表单业务配置而来的，而业务配置所配置的表单模板全部不可用或被删除，那么此时的表单ID集合为空，所看到的页面不显示当前用户有权查看的全部统计模板
			String flag = request.getParameter("flag");
			if((Strings.isNotBlank(flag) && appidlist.size()>0) || Strings.isBlank(flag)) {
				//获取未停用表单列表
				List <FormAppMain> newAppList = new ArrayList<FormAppMain>();
	            // 获取表单分类列表
	            Set<TempleteCategory> templeteCategories = new LinkedHashSet<TempleteCategory>();
	            // 获取表单名称列表
	            Set<String> formNameList = new LinkedHashSet<String>();
				getIOperBase().getformAccess(newAppList, templeteCategories, formNameList, appidlist, IPagePublicParam.C_iObjecttype_Report) ;
				/**
				List<Long> formobjlist = this.getUserDomainIds();
				List applst = new ArrayList();
				applst = getIOperBase().queryAllAccess(formobjlist,appidlist,2);
				List <FormAppMain> formapplist = getIOperBase().assignReport(applst,sessionobject, CurrentUser.get());
				//获取未停用表单列表
				List <FormAppMain> newAppList = new ArrayList<FormAppMain>();
	            // 获取表单分类列表
	            Set<TempleteCategory> templeteCategories = new LinkedHashSet<TempleteCategory>();
	            // 获取表单名称列表
	            Set<String> formNameList = new LinkedHashSet<String>();
	            int count = 1;
	            for (FormAppMain app : formapplist) {//为防止时间一样生成树时缺少节点，重新赋值
	            	if(app.getFormstart() == 0) continue;
	            	app.setSystemdatetime(String.valueOf(System.currentTimeMillis() + (++count * 37)));
	            	newAppList.add(app);
	                formNameList.add(app.getName());
	                TempleteCategory templete = templeteCategoryManager.get(app.getCategory());
	                if(templete != null){
	                	templeteCategories.add(templete);	
	                	while(templete.getParentId() != null){
	                		templete = templeteCategoryManager.get(templete.getParentId());
	                		if(templete.getId() == 0 || templete.getId() == 4)continue;
	                		templeteCategories.add(templete);	
	                	}
	                }
	            }
	            **/
	            //根据当前用户获取我的模板列表
	            List<FormQueryPlan> plans = getFormDaoManager().findByUserId(CurrentUser.get().getId(), IPagePublicParam.C_iObjecttype_Report);
	            
	            //如果没有自定义查询项，过滤掉我的统计
	            List<FormQueryPlan> removePlans = new ArrayList<FormQueryPlan>();
	            for(FormQueryPlan plan : plans){
	            	ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(plan.getAppmainId());
	            	if(afapp != null){
	            		SeeyonReportImpl report = (SeeyonReportImpl)afapp.findReportByName(plan.getQueryName()); 
	            		if(report == null || report.getReportColumList().size() == 0){
	            			removePlans.add(plan);
	            		}
	            	}
	            }
	            if(CollectionUtils.isNotEmpty(removePlans)){
	            	plans.removeAll(removePlans);
	            }
	            
	            mav.addObject("applst", newAppList);
	            mav.addObject("plans", plans);
	            mav.addObject("templeteCategories", templeteCategories);
	            mav.addObject("formNameList", formNameList);
			}
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			log.error("点击表单统计后的首页面报错",e);
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			log.error("点击表单统计后的首页面报错",e);
			OperHelper.creatformmessage(request,response,lst);
		}finally{
			return mav;
		}
	}
	
	
	public ModelAndView bizConfigFormReportList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		ModelAndView mav = new ModelAndView("formbizconfig/write/show_bizconfig_formreport");
	    String categoryHTML=iOperBase.categoryHTML(templeteCategoryManager).toString();
	    mav.addObject("categoryReportHTML", categoryHTML);
		return mav;
	}
	
	/**
	 * 返回该登录用户能看到的所有的统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView bizReportListTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/show_bizconfig_formreport_tree");
		String queryType = request.getParameter("queryType");
		String condition = request.getParameter("condition");
		String textfield = null;
		if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(condition)) {
			textfield = request.getParameter("categoryId");
		} else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
			textfield = request.getParameter("textfield");
		}
		User user = CurrentUser.get() ;
		Long accountId = user.getLoginAccount();
		List<ISeeyonForm_Application> fappList =  SeeyonForm_Runtime.getInstance().getAppManager().getAppList();
		Map<Long,List<FormApp>> categoryAndFromMap = new HashMap<Long,List<FormApp>>();
		Set<TempleteCategory> templeteCategories = new LinkedHashSet<TempleteCategory>();
		if(fappList != null){
			Set<Long> formAppIdSet = new HashSet<Long>();
			if("creator".equals(queryType)){
				FormOwnerList fol = new FormOwnerList();
				fol.setOrg_account_id(accountId);
				fol.setOwnerId(user.getId());
				List<FormOwnerList> formownerlst = ((FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager")).queryOwnerListByCondition(fol);
				for (FormOwnerList formOwnerList : formownerlst) {
					formAppIdSet.add(formOwnerList.getAppmainId());
				}
			}
			for(ISeeyonForm_Application fapp:fappList){
				SeeyonForm_ApplicationImpl fappImpl = (SeeyonForm_ApplicationImpl)fapp;
				if(fappImpl.getFormstart() == 0){//过滤掉停用的表单
					continue;
				}
				TempleteCategory category = templeteCategoryManager.get(fappImpl.getCategory());
				if(category != null){
					if(accountId.equals(category.getOrgAccountId())){
						if("account".equals(queryType) || formAppIdSet.contains(fappImpl.getId())){
							if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
								if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(condition)) {
									if(!String.valueOf(category.getId()).equals(textfield)) continue;
								}
							}
							boolean flag = false;
							List<FormApp> list = new ArrayList<FormApp>();
							List<ISeeyonReport> reportList =  fappImpl.getReportList();
							if(reportList != null){
								for(ISeeyonReport report: reportList){
									if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
										if(report.getReportName().indexOf(textfield)==-1) continue;
									}
									FormApp formAppObject = new FormApp();
									formAppObject.setId(report.getId());
									formAppObject.setName(report.getReportName());
									formAppObject.setAppFormId(String.valueOf(fappImpl.getId()));
									formAppObject.setSourceType(fappImpl.getFormType());
									list.add(formAppObject);
									flag = true;
								}
							}
							List<FormApp> allList = categoryAndFromMap.get(category.getId());
							if(allList==null){
								categoryAndFromMap.put(category.getId(),list);
							}else{
								allList.addAll(list);
								categoryAndFromMap.put(category.getId(),allList);
							}
							if(flag){
								templeteCategories.add(category);	
				            	while(category.getParentId() != null){
				            		category = templeteCategoryManager.get(category.getParentId());
				            		if(category.getId() == 0 || category.getId() == 4)continue;
				            		templeteCategories.add(category);	
				            	}
							}
						}
					}
				}
			}
		}
		mav.addObject("templeteCategories", templeteCategories);
		mav.addObject("categoryAndFromMap", categoryAndFromMap);
		return mav;
	}
	
	public ModelAndView saveFormReportPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryController.saveOrUpdateFormPlan(request, formDaoManager);
		return null;
	}
	public ModelAndView deleteFormReportPlan(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String planId = request.getParameter("planid");
		formDaoManager.deleteFormQueryPlanById(Long.parseLong(planId));
		return null;
	}
	
	/**
	 * 表单统计首页面的执行设置
	 */
	public ModelAndView formReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    ModelAndView mav = new ModelAndView("form/formreport/stat_formstat"); 
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String formid = request.getParameter("formid");
		String reportname = request.getParameter("reportname");
		String planid = request.getParameter("planid");
		if(Strings.isEmpty(formid) || Strings.isEmpty(reportname)){
			return mav;
		}
		if(sessionobject == null 
			|| sessionobject.getReportlist() == null
			|| sessionobject.getReportlist().size() < 1
			|| sessionobject.getReportConditionList() == null 
			|| sessionobject.getReportConditionList().size() < 1){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject", sessionobject);
		}
		User user = CurrentUser.get();
		List<Long> formobjlist = FormBizConfigUtils.getUserDomainIds(user, null);
		List applst =new ArrayList();
		List<Long> appidlist = new ArrayList<Long>();
		applst = getIOperBase().queryAllAccess(formobjlist,appidlist,2);
		getIOperBase().assignReport(applst,sessionobject, user);
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid));  
		String name = afapp.getAppName();
		InfoPath_Inputtypedefine inputTypeDefine = null;
		if(afapp.getDataDefine() != null && (afapp.getDataDefine() instanceof SeeyonDataDefine)){
			ISeeyonDataSource dataSource = ((SeeyonDataDefine)afapp.getDataDefine()).getDataSource();
			if(dataSource != null){
			  inputTypeDefine = dataSource.getDefaultInputtype();
			}
		}
		if(inputTypeDefine == null && sessionobject.getSeedatadefine() != null){
			ISeeyonDataSource dataSource =  sessionobject.getSeedatadefine().getDataSource();
			if(dataSource != null){
				inputTypeDefine = dataSource.getDefaultInputtype();	
			}
		}
		
		sessionobject.setFormsort(afapp.getCategory());
		sessionobject = getIOperBase().systemenum(sessionobject);
		SeeyonReportImpl report = (SeeyonReportImpl)afapp.findReportByName(reportname);
		if(report == null){
			mav.addObject("formname", name);
			return mav;
		}
	    ParseUserCondition parseUserCondition = new ParseUserCondition();
	    List<QueryColum> reportColumns = report.getReportColumList();
		for(QueryColum reportColumn : reportColumns){
			inputTypeDefine.field(reportColumn.getDataAreaName());
			parseUserCondition.parseCustomCondition(reportColumn,inputTypeDefine);
		}
		ConditionListReportImpl conditionList = (ConditionListReportImpl)report.getUserConditionList();	
		Set<String> userConditions = new HashSet<String>();//存放用户输入条件名称，当多个条件引用时只生成一个
		if(conditionList.isHasUserCondition()){
			DataColumImpl dataColumn = null;
			QueryUserConditionDefin userConditionDefin = null;
			for(ICondition condition : conditionList.getConditionList()){
				if(condition instanceof DataColumImpl){
					dataColumn = (DataColumImpl)condition;
				}
				if(condition instanceof QueryUserConditionDefin){
					userConditionDefin = (QueryUserConditionDefin)condition;
				}
				if(userConditionDefin != null && dataColumn != null
						&& !userConditions.contains(userConditionDefin.getParamName())){
					parseUserCondition.parseUserCondition(dataColumn, userConditionDefin,inputTypeDefine);
					userConditions.add(userConditionDefin.getParamName());
					dataColumn = null;
					userConditionDefin = null;
					
				}
			} 
		}
		if(reportColumns != null && reportColumns.size() > 0){
			mav.addObject("columnNames", parseUserCondition.getColumnNames());
			mav.addObject("blankInput", parseUserCondition.getBlankInput());
		    mav.addObject("inputs", parseUserCondition.getInputs());
		}
		//当主表为Group时去掉单据状态和流程状态选项
		if(report.getDBProvider().getDataSource().findGroupByTableName(report.getQuerySource().getMasterTable()) == null){
			ConditionListImpl filter = (ConditionListImpl)report.getFilter();
			boolean isFlowForm = ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue() == ((SeeyonForm_ApplicationImpl)afapp).getFormType();
			parseUserCondition.genStatusHTML(filter.getConditionList(), isFlowForm);
			if(isFlowForm){
				mav.addObject("statusRow", parseUserCondition.getStatusRow());
			}
		}
		if(Strings.isNotBlank(planid)){
			FormQueryPlan plan = getFormDaoManager().getFormQueryPlanById(Long.parseLong(planid));
			if(plan == null){
				PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert(parent.v3x.getMessage(\"formLang.formreport_myplan_delete\"));");
                out.println("</script>");
                out.flush();
                out.close();
				return null;
			}
			Document doc = null;
			doc  = dom4jxmlUtils.paseXMLToDoc(plan.getPlanDefine());
			Element root = doc.getRootElement();
			Element userConditionListElement = root.element(IXmlNodeName.UserConditionList);
			if(userConditionListElement != null){
				ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
				userConditionList.loadFromXml(userConditionListElement);
				List<ICondition> conditions = userConditionList.getConditionList();
				parseUserCondition.parsePlanCondition(conditions, reportColumns, inputTypeDefine);
				List<String> columns = parseUserCondition.getColumns();
				List<String> inputColumns = parseUserCondition.getInputColumns();
				mav.addObject("columns", columns);
				mav.addObject("inputColumns", inputColumns);
			}
		
			mav.addObject("plan", plan);
		} 
		if(!conditionList.isHasUserCondition()){
			mav.addObject("query", "query");
		}
		if(conditionList.getConditionList().size() > 0){
		    mav.addObject("condition", parseUserCondition.getCondition());
		}
		Set<String> chartNames =  report.getChartInfos().keySet();
		if(chartNames.size() > 0){
			mav.addObject("chartNames", report.getChartInfos().keySet());			
		}
	    mav.addObject("realValue", parseUserCondition.getRealValue());
		mav.addObject("formname", name);
		return mav; 
	} 
	
	/**
	 * 表单统计的执行结果

	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formReportResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formid = request.getParameter("formid");
        String reportname = request.getParameter("reportname");
        Map<String,String> displayConditions = new LinkedHashMap<String,String>();
        String formname = null;
        IReportResult resultData = null;
        SeeyonReportImpl report = null;
        String[] sumDataField = null;
	    try {
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
					.getInstance().getAppManager().findById(Long.parseLong(formid));
			formname = fapp.getAppName();
			report = (SeeyonReportImpl)fapp.findReportByName(reportname);
	    	resultData = getResultData(request,response,displayConditions);
			if ((report.getSumDataColumList().size() == 1)
					&& (report.getSchema().isAcrossReport())
					&& (report.getSchema().getDataColumList().size() == 1)) {
				sumDataField = new String[resultData.getColCount()];
				addSepcSumDataField(resultData, report.getSumDataColumList(),
						sumDataField, fapp);
			} else if (report.getSumDataColumList().size() != 0) {
				sumDataField = new String[resultData.getColCount()];
				addSumDataField(resultData, report.getSumDataColumList(),
						sumDataField, fapp);
			}
	    } catch (SeeyonFormException e) {
	        List<String> lst = new ArrayList<String>();
	        lst.add(e.getToUserMsg());
	        OperHelper.creatformmessage(request, response, lst);
	        return new ModelAndView("form/formquery/showQueryResultException");
	    } 
	    
	    ModelAndView mav = new ModelAndView("form/formreport/showReportResult");
	    if(resultData != null && resultData.getSchema().isAcrossReport() && resultData.getSchema().getDataColumList().size() != 1){
	    	mav.addObject("reportType","2");
	    } else {
	    	mav.addObject("reportType","0");
	    }
	    HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		if(sessionObject != null){//为区分交叉报表和普通报表表头样式
			List<ReportObject> reportConditionList = sessionObject.getReportConditionList();
			for(int i=0;i<reportConditionList.size();i++){
				ReportObject reportObject = (ReportObject)reportConditionList.get(i);
	            if(reportname.equals(reportObject.getReportName()) && formname.equals(reportObject.getFormname())){
	            	//交叉表类型为1，普通报表类型为0，表头样式后缀交叉为2，普通为1，所以+1
	            	if("0".equals(reportObject.getReportType())){
	            		mav.addObject("reportType","0");
	            	}else{
	            		mav.addObject("reportType",String.valueOf(Integer.parseInt(reportObject.getReportType()) + 1));
	            	}
	            	break;
	            }
			}			
		}else{
			mav.addObject("reportType","1");
		}
		mav.addObject("statdate", Datetimes.formatDate(new Date()));
	    mav.addObject("ReportResult",resultData);
	    mav.addObject("sumfield", sumDataField);
	    mav.addObject("displayConditions", displayConditions);
	    mav.addObject("formname", formname);
	    String from = request.getParameter("from");
	    if (Strings.isNotBlank(from)) {
	        mav.addObject("from", from);
	    }
	    if(report != null && report.getShowDetail() != null){
	    	String formName= resultData.getRunner().getReport().getShowDetail().getFormName();
	    	String opername = resultData.getRunner().getReport().getShowDetail().getOperName();
	    	String showdetail = formName+"."+opername;
	    	mav.addObject("showdetail", showdetail);
	    	mav.addObject("penetrate", "true");
	    }
	    return mav;

	}
	

    private IReportResult getResultData(HttpServletRequest request,HttpServletResponse response,
    		Map<String,String> displayConditions) throws SeeyonFormException{
    	ConditionListReportImpl conditionList = null;
        String formid = request.getParameter("formid");
        String reportname = request.getParameter("reportname");
        SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
                .getInstance().getAppManager().findById(Long.parseLong(formid));

        SeeyonReportImpl report = (SeeyonReportImpl)fapp.findReportByName(reportname);
        conditionList = (ConditionListReportImpl)report.getUserConditionList().copy();
		
		QueryController.genConditionStr(request, displayConditions, conditionList, false); 
        
        IReportResult resultData = report.showReport(null, conditionList, request, response);

        return resultData;

    }
		
	/**
	 * 实现一般类型最后尾行合计
	 * @param aResultData 数据结果数据集合
	 * @param aList       尾行合计list
	 */
	
	public static void addSumDataField(IReportResult aResultData, List<ReportSumDataColum> aList, String[] aSumDataField, SeeyonForm_ApplicationImpl aApp){
		Result_DataColum dataColum;
		aSumDataField[0] = Constantform.getString4CurrentUser("clacType.Total");
		for(ReportSumDataColum sumColum : aList){//合计数据域
			for(int i = 0; i < aResultData.getRowCount(); i++){
				for(int j = 0; j < aResultData.getColCount(); j++){
					if(aResultData.getCell(i,j) instanceof Result_DataColum){//如果是数据列名字段
						dataColum = (Result_DataColum)aResultData.getCell(i,j);
						if((dataColum.getFieldName().equals(sumColum.getDataAreaName())) &&
						   (dataColum.getShowString().equals(sumColum.getColumTitle())) &&
						   (dataColum.getCalcType() == sumColum.getCalctype())){
							getSumDataField(j, aSumDataField, aResultData, dataColum.getCalcType(), aApp, dataColum.getFieldName());
						}
					}
					
				}
			}
			
		}
	}
	
	/**
	 * 实现特殊类型最后尾行合计(只有一个数据列并且是交叉报表)
	 * @param aResultData 数据结果数据集合
	 * @param aList       尾行合计list
	 */
	
	public static void addSepcSumDataField(IReportResult aResultData, List<ReportSumDataColum> aList, String[] aSumDataField, SeeyonForm_ApplicationImpl aApp){
		
		
		Result_DataColum dataColum;
		aSumDataField[0] = Constantform.getString4CurrentUser("clacType.Total");
			
		if(aResultData.getRowCount() != 1){//如果行数大于一行
			for(ReportSumDataColum sumColum : aList){//合计数据域
				for(int i = 0; i < aResultData.getRowCount(); i++){
					for(int j = 0; j < aResultData.getColCount(); j++){
						if(aResultData.getCell(i,j) instanceof Result_AcrossColHead){
							getSpecSumDataField(j, aSumDataField, aResultData, sumColum, aApp);
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * 产生一般列的合计数
	 * @param aColum
	 * @param aStrs
	 * @param aResultData
	 * @param aCalcType
	 */
	private static void getSumDataField(int aColum, String[] aStrs, IReportResult aResultData, int aCalcType, SeeyonForm_ApplicationImpl aApp, String aFieldName){
		BigDecimal fTotal = new BigDecimal(0);
		Result_Value fvalue;
		for(int i = 0; i < aResultData.getRowCount(); i++){
			if(aResultData.getCell(i,aColum) instanceof Result_Value){
				fvalue = (Result_Value)aResultData.getCell(i,aColum);
				if(Strings.isNotBlank(fvalue.getValue()) && !ReportResultImpl.IS_NULL.equals(fvalue.getValue())){
					fTotal = fTotal.add(new BigDecimal(fvalue.getValue()));
				}
			}
		}
		if(ReportDataColum.C_iCalcType_Count == aCalcType){//如果是计数
			aStrs[aColum] = String.valueOf(fTotal.intValue());
		}else if(ReportDataColum.C_iCalcType_Sum == aCalcType){//如果是求和
			aStrs[aColum] = QueryRunner.formatValue(aApp, aFieldName, fTotal);
		}
		
	}
	
	/**
	 * 产生特殊列的合计数
	 * @param aColum
	 * @param aStrs
	 * @param aResultData
	 * @param aCalcType
	 */
	private static void getSpecSumDataField(int aColum, String[] aStrs, IReportResult aResultData, ReportSumDataColum aSumDataColum, SeeyonForm_ApplicationImpl aApp){
		BigDecimal fTotal = new BigDecimal(0);
		Result_Value fvalue;
		for(int i = 0; i < aResultData.getRowCount(); i++){
			if(aResultData.getCell(i,aColum) instanceof Result_Value){
				fvalue = (Result_Value)aResultData.getCell(i,aColum);
				if(Strings.isNotBlank(fvalue.getValue()) && !ReportResultImpl.IS_NULL.equals(fvalue.getValue())){
					fTotal = fTotal.add(new BigDecimal(fvalue.getValue()));
				}
			}
		}
		if(ReportDataColum.C_iCalcType_Count == aSumDataColum.getCalctype()){//如果是计数
			aStrs[aColum] = String.valueOf(fTotal.intValue());
		}else if(ReportDataColum.C_iCalcType_Sum == aSumDataColum.getCalctype()){//如果是求和
			aStrs[aColum] = QueryRunner.formatValue(aApp, aSumDataColum.getDataAreaName(), fTotal);
		}
		
	}
	
	public ModelAndView showReportQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/resultDetailBorderFrame");
		BASE64Decoder baseDecoder = new BASE64Decoder();
		String[] params = request.getParameter("str").split(",");
		//进行参数转码
		for(int i = 0; i < params.length; i++){
			params[i] = new String(baseDecoder.decodeBuffer(params[i]));
		}
		String appId = request.getParameter("formid");
		String showdetail = request.getParameter("showdetail");
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appId));
        boolean isFlow = fapp.getFormType() == ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue();
		String reportName = request.getParameter("reportname");
		ISeeyonReport report = fapp.findReportByName(reportName);
		ConditionListImpl conditionList = (ConditionListImpl)((SeeyonReportImpl)report).getUserConditionList().copy();
		Map<String, String> displayConditions = new HashMap<String, String>();
		QueryController.genConditionStr(request, displayConditions, conditionList,false); 
		List<String[]> list = ((SeeyonReportImpl)report).showReportQuery(params, (IConditionList_Report)conditionList);
		if(((SeeyonReportImpl)report).getShowDetail() != null){
			request.getSession().setAttribute("appShowDetail", ((SeeyonReportImpl)report).getShowDetail().getShowDetailStr());			
		}
		request.getSession().setAttribute("isFlow", isFlow);
		request.getSession().setAttribute("resultDetailList", list);
		request.getSession().setAttribute("formname", fapp.getAppName());
		request.getSession().setAttribute("formid", appId);
		request.getSession().setAttribute("reportname", reportName);
		request.getSession().setAttribute("showdetail", showdetail);
		return mav;
	}
	
	public ModelAndView resultDetailBorderFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formreport/resultDetailList");   
		return mav; 
	}
	
	public ModelAndView hasSummaryId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String masterId = request.getParameter("id");
		String appid = request.getParameter("formid");
		SeeyonForm_ApplicationImpl fapp=(SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appid));  
		ColManager colManger = (ColManager)ApplicationContextHolder.getBean("colManager");
		String summaryIds="";
		boolean newflowsign = false;
		List colsummarylist = new ArrayList();
		List summarylist = new ArrayList();
		colsummarylist = colManger.getSummaryIdByFormIdAndRecordId(fapp.getId(), null, Long.valueOf(masterId));
		if(colsummarylist !=null){
			if(colsummarylist.size() >1){
				for(int i=0;i<colsummarylist.size();i++){
					ColSummary col = (ColSummary)colsummarylist.get(i);
					if(col.getNewflowType() !=null){
						if(col.getNewflowType() == 1)
							newflowsign = true;
					}
					Long summaryId = col.getId();
					summarylist.add(summaryId);
					summaryIds += summaryId.toString() +"|";
				}
			}else{
				ColSummary col = (ColSummary)colsummarylist.get(0);
				if(col.getNewflowType() !=null){
					if(col.getNewflowType() == 1)
						newflowsign = true;
				}
				Long summaryId = col.getId();
				summarylist.add(summaryId);
				summaryIds = summaryId.toString();
			}
		}else{
			summaryIds = "";
		}
		//校验子流程是否还存在
		if(newflowsign){
			List formdatastatelist = new ArrayList();
			FormDaoManager formManger = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
			formdatastatelist = formManger.getSummaryList(summarylist, null,null);
		    if(formdatastatelist.size() == 0)
		    	summaryIds = "";
		}
		byte[] b;
		if("".equals(summaryIds))
			b = "null".getBytes("UTF-8");
		else
			b = summaryIds.toString().getBytes("UTF-8");
		
//		Long summaryId = colManger.getSummaryIdByFormIdAndRecordId(fapp.getId(), Long.valueOf(formId), Long.valueOf(masterId));
//		byte[] b;
//		if(summaryId == null)
//			b = "null".getBytes("UTF-8");
//		else
//			b = summaryId.toString().getBytes("UTF-8");
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.getOutputStream().write(b);
	    return null;
	}	
	
	public ModelAndView collFrameViewRelate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		 ModelAndView mav = new ModelAndView("form/formquery/collFrameViewRelate");
		    String showdetail = request.getParameter("showdetail");
		    String appid = request.getParameter("appid");
		    String queryname = request.getParameter("queryname");
		    //来自表单统计
		    if(Strings.isBlank(queryname) && Strings.isNotBlank(request.getParameter("reportname"))){
		    	queryname = request.getParameter("reportname");
		    }
		    String summaryIdStr = request.getParameter("summaryId");
		    mav.addObject("summaryId", summaryIdStr);
		    mav.addObject("appid", appid);
		    mav.addObject("queryname", queryname);
		    mav.addObject("showdetail", showdetail);
		    return mav;
	}
	
	
	public ModelAndView collViewRelate(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    ModelAndView mav = new ModelAndView("form/formreport/collViewRelate");
	    String showdetail = request.getParameter("showdetail");
	    String appid = request.getParameter("appid");
       String reportname = request.getParameter("reportname");
	    String summaryIdStr = request.getParameter("summaryId");
	    String[] summaryid = summaryIdStr.split("\\|");
	    List summaryidlist = new ArrayList();
		for(int i=0;i<summaryid.length;i++){
			summaryidlist.add(summaryid[i]);
		}
			
	    boolean stateflag = false;
	    boolean finishflag = false;
	    List stateidlist = new ArrayList();
	    List finishedlist = new ArrayList();
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appid));  
	    if(afapp.getReportList().size() !=0){
	    	for(int i=0;i<afapp.getReportList().size();i++){
	    		SeeyonReportImpl ireport = (SeeyonReportImpl)afapp.getReportList().get(i);
	    		if(ireport.getReportName().equals(reportname) && appid.equals(afapp.getId().toString())){  
	    			ConditionListImpl filter = (ConditionListImpl)ireport.getFilter();
	    			if(filter.getConditionList().size() != 0){
	    				for (ICondition fitem  : filter.getConditionList()){
	    					if(fitem instanceof DataColumImpl){
	    						//如果是系统值 
	    						if(((DataColumImpl)fitem).getSys() != null){
	    							if("state".equals(((DataColumImpl)fitem).getSys()))
	    								stateflag = true;
	    							else if("finishedflag".equals(((DataColumImpl)fitem).getSys()))
	    								finishflag = true;
	    						}
	    					}else if(fitem instanceof ValueImpl){
	    					   if(stateflag && fitem.getDisplay().indexOf("单据状态") >-1)	
	       					       stateidlist.add(fitem.getRun());
	       					   if(finishflag && fitem.getDisplay().indexOf("流程状态") >-1)
	       						   finishedlist.add(fitem.getRun());
	    					}   				
	    				}   			
	    			}
	    		}   
	    	}
	    }

		List formdatastatelist = new ArrayList();
		HashMap formdatastatmap = new HashMap();
		//if(stateflag || finishflag){
			FormDaoManager formManger =  (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
			formdatastatelist = formManger.getSummaryList(summaryidlist, stateidlist,finishedlist);
		//}
		
		
	    ColManager colManger = (ColManager)ApplicationContextHolder.getBean("colManager");
	    List relateFlowList = colManger.getSummaryList(summaryidlist);

	    for(int i=0 ;i<formdatastatelist.size();i++){
	    	FormDataState fm = (FormDataState)formdatastatelist.get(i);
	    	formdatastatmap.put(fm.getSummaryid(), fm.getSummaryid());
	    }
	    if(formdatastatmap.size() >0){
	        for(int i=0;i<relateFlowList.size();i++){
		    	ColSummary col = (ColSummary)relateFlowList.get(i);
		    	if(formdatastatmap.get(col.getId()) == null){
		    		relateFlowList.remove(i);
		    		i--;
		    	}
		    }
	    }

	    mav.addObject("relateFlowList",getIOperBase().pagenate(relateFlowList));
	    mav.addObject("showdetail", showdetail);
	    mav.addObject("appid", appid);
	    return mav;
	}
	
	
	public ModelAndView showRecordDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String summaryId = request.getParameter("summaryId");
		String[] strs = request.getParameter("showdetail").split("\\.");
		Long appid = Long.parseLong(request.getParameter("appid"));
		SeeyonForm_ApplicationImpl fapp=(SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(appid);  
		HashMap formmap = new HashMap();
		for (int j = 0; j < fapp.getFormList().size(); j++) {
			SeeyonFormImpl sf = (SeeyonFormImpl) fapp.getFormList().get(j);
			formmap.put(sf.getFormId(), sf.getFormName());			
		}
		String formNames= "";
		
		String formId = strs[0];
		
		if(strs[0].indexOf("|") !=-1){
			for(int i = 0;i<strs[0].split("\\|").length ;i++){
				if(formmap.get(Long.parseLong(strs[0].split("\\|")[i])) !=null)
					formNames +=formmap.get(Long.parseLong(strs[0].split("\\|")[i])) + "|";  
			}
		}
		
		String operationId = strs[1];
		AffairManager affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
		
		Long affairId = 0l;
		try{
			affairId = affairManager.getByIdForForm(Long.valueOf(summaryId));
		   if(affairId == 0)
				throw new DataDefineException(1,"您选择的协同已经被删除","您选择的协同已经被删除");
		   
		   ColManager colManger = (ColManager)ApplicationContextHolder.getBean("colManager");
		   
		   ColSummary col =  colManger.getColSummaryById(Long.valueOf(summaryId), false);
		   if(col == null)
				throw new DataDefineException(1,"您选择的协同已经被删除","您选择的协同已经被删除");
		  
		   boolean newflowsign = false;
		   if(col.getNewflowType() !=null){
				if(col.getNewflowType() == 1)
					newflowsign = true;
			}
			//校验子流程是否还存在
			if(newflowsign){
				List formdatastatelist = new ArrayList();
		        List summarylist = new ArrayList();
		        summarylist.add(Long.valueOf(summaryId));
				FormDaoManager formManger = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
				formdatastatelist = formManger.getSummaryList(summarylist, null,null);
				if(formdatastatelist.size() == 0)
					throw new DataDefineException(1,"您选择的协同已经被删除","您选择的协同已经被删除");
			}
			
		}
		catch(Exception e){
		List<String> lst = new ArrayList<String>();
			lst.add("您选择的协同已经被删除");
			OperHelper.creatformmessage(request,response,lst);
			return null;
		}
		
		//来自表单统计
		String queryname = request.getParameter("queryname");
	    if(Strings.isBlank(queryname) && Strings.isNotBlank(request.getParameter("reportname"))){
	    	queryname = request.getParameter("reportname");
	    }
		if(Strings.isBlank(queryname)){
			log.warn("遗漏的统计名称参数，可能导致表单协同的关联文档无法查看。" + request.getQueryString());
		}
		//SECURITY 访问控制
		if(!SecurityCheck.hasFormQueryPermission(request, response, CurrentUser.get(), appid, queryname, summaryId)){
			return null;
		}
		return  new ModelAndView("form/formreport/resultDetail")
				.addObject("summaryId", summaryId)
				.addObject("affairId", affairId)
				.addObject("isDesign", true)
				.addObject("type", "form")
				.addObject("from", "send")
				.addObject("isQuote", "")
				.addObject("formId", formId)
				.addObject("operationId", operationId)
				.addObject("openLocation", "")
				.addObject("formNames", formNames);
			
			
		
	}
	
	/**
	 * 解析用户条件
	  <?xml version="1.0" encoding="UTF-8"?>
		<UserConditionList>
			<UserCondition paramname="1" value="1" />
			<UserCondition paramname="2" value="2" />
		</UserConditionList>
	 */
	public Map<String, String> parseConditionXML(String conditionXML) throws Exception{
		BASE64Decoder baseDecoder = new BASE64Decoder();
		String XMLContent = new String(baseDecoder.decodeBuffer(conditionXML));
		
		Document userConditionDoc = dom4jxmlUtils.paseXMLToDoc(XMLContent);
		List userConditionList = userConditionDoc.getRootElement().elements();
		Map map = new HashMap();
		for(int i = 0; i < userConditionList.size(); i++){
			Element e = (Element)userConditionList.get(i);
			map.put(new String(baseDecoder.decodeBuffer(e.attribute(IXmlNodeName.paramname).getValue())), new String(baseDecoder.decodeBuffer(e.attribute(IXmlNodeName.value).getValue())));
		}
		return map;
		
	}
	

	
	public ModelAndView formReportResultCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String reportname = request.getParameter("reportname");
		String formname = request.getParameter("formname");
		String formid = request.getParameter("formid");
		String description= request.getParameter("description");
		String condition = request.getParameter("condition");
		//前台传的条件值（条件paramName名称↗前台输入的条件值,条件paramName名称↗前台输入的条件值.....）
		String conditionvalue = request.getParameter("conditionvalue");
		String[] conditionlist = null;
		HashMap conditionlistMap = new HashMap();
		String[] conditionvaluelist = null;
		HashMap conditionvaluelistMap = new HashMap();
		if(!"".equals(condition) && condition !=null){
			conditionlist = condition.split(",");
		}
		if(conditionlist !=null){
			for(int i=0;i<conditionlist.length;i++){
	        	String id =OperHelper.AddTableName(conditionlist[i]);
	        	String name = OperHelper.AddFieldName(conditionlist[i]);
	        	conditionlistMap.put(Integer.parseInt(id), name);
	        }
		}
		if(!"".equals(conditionvalue) && conditionvalue !=null){
			conditionvaluelist = conditionvalue.split(",");
		}
		if(conditionvaluelist !=null){
			for(int i=0;i<conditionvaluelist.length;i++){
	        	String titlename =OperHelper.AddTableName(conditionvaluelist[i]);
	        	String value = OperHelper.AddFieldName(conditionvaluelist[i]);
	        	conditionvaluelistMap.put(titlename, value);
	        }
		}
		StringBuffer sb = new StringBuffer();
		
		BASE64Encoder encoder = new BASE64Encoder();//对查询条件进行加码
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		
	 	SeeyonForm_ApplicationImpl fapp=(SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid)); 

		request.removeAttribute("fconditon");
		ModelAndView mav = new ModelAndView("form/formquery/showQueryCondition");
		
		
		//查找userCondition赋值
		IInputExtendManager fmanager = null;
		ISeeyonInputExtend fvalue = null;
		SeeyonReportImpl report = null;
		SeeyonReportImpl seereport =null;
		IConditionList conditionList = null;
		String conditionXML = "";
		String reportype = null;
//		BASE64Encoder baseEncoder = new BASE64Encoder();
		
//		for(int i =0;i<sessionObject.getReportlist().size();i++){
//			report = (SeeyonReportImpl) sessionObject.getReportlist().get(i);
//			if(report.getReportName().equals(reportname) && formname.equals(report.getFormname())){
//				seereport = report;
//				conditionList = report.getUserConditionList().copy();
//			}
//		}
		for(int i =0;i<fapp.getReportList().size();i++){
			report = (SeeyonReportImpl) fapp.getReportList().get(i);
			if(report.getReportName().equals(reportname)){
				seereport = report;
				conditionList = report.getUserConditionList().copy();
			}
		}
		//取得查询条件用,分割
		int  i = 0;
		for(Object item : ((ConditionListReportImpl)conditionList).getConditionList()){
			if(item instanceof QueryUserConditionDefin){
				QueryUserConditionDefin userConditionDefin = (QueryUserConditionDefin)item;
				/*//如果是扩展输入字段
				if("".equals((String)request.getParameter("input" + i)))
					sb.append(encoder.encode(" ".getBytes()) + ",");
				else
					sb.append(encoder.encode(((String)request.getParameter("input" + i)).getBytes()) + ",");
				i++;*/
//				if(conditionlistMap.get(i) !=null){
//                    sb.append(encoder.encode(((String)conditionlistMap.get(i)).getBytes()) + ",");
//					i++;
//				}else{
//                    //如果是扩展输入字段
//					if("".equals((String)request.getParameter("input" + i)))
//						sb.append(encoder.encode(" ".getBytes()) + ",");
//					else
//						sb.append(encoder.encode(((String)request.getParameter("input" + i)).getBytes()) + ",");
//					i++;
//				}	
				//根据条件paramName名称对应相应的条件值
				if(conditionvaluelistMap.get(userConditionDefin.getParamName()) !=null){
					if("".equals(conditionvaluelistMap.get(userConditionDefin.getParamName())))
						sb.append(encoder.encode(" ".getBytes()) + ",");
					else
                        sb.append(encoder.encode(conditionvaluelistMap.get(userConditionDefin.getParamName()).toString().getBytes())+ ",");
					i++;
				}
			}
		}
		String s = sb.toString();
		
		super.rendJavaScript(response, "parent.window.dialogArguments.document.getElementById(\"conditionValue\").value=" + "\"" + (Strings.isBlank(s) ? s : s.substring(0, s.length() - 1)) + "\";parent.window.close();");
		return null;
		
	}
	
	//统计结果转协同
	public ModelAndView transmitSeeyon(HttpServletRequest request, HttpServletResponse response){
		String queryname = request.getParameter("reportname");
		String seeyonBody = request.getParameter("seeyonbody");
		ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
		Date date = new Date();
		mav = this.collaborationController.appToColl(queryname + " " + Datetimes.formatDate(date), Constants.EDITOR_TYPE_HTML, date, seeyonBody, null, false);
		return mav;
	}
	
//	统计结果导出excel
	public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String formid = request.getParameter("formid");
        String reportname = request.getParameter("reportname");
        Map<String,String> displayConditions = new LinkedHashMap<String, String>();
        String formname = null;
        IReportResult resultData = null;
        String[] sumDataField = null;
	    try {
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
					.getInstance().getAppManager().findById(Long.parseLong(formid));
			formname = fapp.getAppName();

	        SeeyonReportImpl report = (SeeyonReportImpl)fapp.findReportByName(reportname);
	        
	    	resultData = getResultData(request,response,displayConditions);

			if ((report.getSumDataColumList().size() == 1)
					&& (report.getSchema().isAcrossReport())
					&& (report.getSchema().getDataColumList().size() == 1)) {
				sumDataField = new String[resultData.getColCount()];
				addSepcSumDataField(resultData, report.getSumDataColumList(),
						sumDataField, fapp);
			} else if (report.getSumDataColumList().size() != 0) {
				sumDataField = new String[resultData.getColCount()];
				addSumDataField(resultData, report.getSumDataColumList(),
						sumDataField, fapp);
			}
			
			List resultList = new ArrayList();
			resultList.add(resultData);
			resultList.add(sumDataField);
			if(resultData != null){
				int colNum = resultData.getColCount();
				if(colNum > 255){
					List<String> msgs = new ArrayList<String>();
					msgs.add("Excel的列数最大允许[255]，现导出的列数[" + colNum + "]已经超出范围，请重新设置后再导出！");
					OperHelper.creatformmessage(request,response,msgs);
					return null;
				}
			}
			fileToExcelManager.save(request,response,reportname,new ReportHelper().exportReportForExcel(Long.parseLong(formid),formname,reportname,displayConditions,resultList));
			return null;
		}catch (DataDefineException e) {
			log.error("统计结果导出excel出错", e);
            List<String> lst = new ArrayList<String>();
            lst.add(e.getToUserMsg());
            OperHelper.creatformmessage(request, response, lst);
            return null;
		}
	}
}