package www.seeyon.com.v3x.form.controller.query;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.OperatorImpl;
import www.seeyon.com.v3x.form.base.condition.ValueImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.condition.inf.IDataColum;
import www.seeyon.com.v3x.form.base.condition.inf.IOperator;
import www.seeyon.com.v3x.form.base.condition.inf.IProvider;
import www.seeyon.com.v3x.form.base.condition.inf.IValue;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;
import www.seeyon.com.v3x.form.base.inputextend.InputExtend_ExampleDatetime;
import www.seeyon.com.v3x.form.base.inputextend.InputExtend_SelectColSummay;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputExtendManager;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.FormApp;
import www.seeyon.com.v3x.form.controller.formservice.ChangeObjXml;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormDataState;
import www.seeyon.com.v3x.form.domain.FormOwnerList;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Enum;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FieldInput;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.IIP_InputObject;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputRadio;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputSelect;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonInputExtend;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.HrStaffInfoField;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.FormQueryException;
import www.seeyon.com.v3x.form.manager.define.query.OrderByColum;
import www.seeyon.com.v3x.form.manager.define.query.ParseUserCondition;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.QueryResultImpl;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.inf.IQueryResult.IQueryRecord;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.affair.his.manager.HisAffairManager;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class QueryController extends BaseController {
	private static final String NAMESPACE = "my:";
	private static Log log = LogFactory.getLog(QueryController.class);
	private MetadataManager metadataManager;	
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private CollaborationController collaborationController;
	private TempleteCategoryManager templeteCategoryManager;
	private FileToExcelManager fileToExcelManager;
	private AtomicInteger counter = new AtomicInteger(0);

	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}

	public CollaborationController getCollaborationController() {
		return collaborationController;
	}
	public void setCollaborationController(CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}

	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
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

	public ModelAndView index(HttpServletRequest request,HttpServletResponse response) throws Exception {
		return null;
	}

    @CheckRoleAccess(roleTypes=RoleType.FormAdmin)
    public ModelAndView formQuerySet(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_queryset");
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
	//				增加防护
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
		sessionobject.setPageflag(IPagePublicParam.QUERYSET);
		return mav; 
	}

	public ModelAndView formQueryShow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("form/formquery/formqueryBorderFrame"); 
	} 
	
	public ModelAndView formQueryNew(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_queryset");
		return mav;
	}
	
	public ModelAndView formQueryResultSortSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_queryresultsortset");   
		return mav; 
	} 
	
	public ModelAndView formQueryRangeSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_datarangeset");   
		return mav; 
		           
	}
	
	public ModelAndView formQueryDateWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_datewindow");   
		return mav; 
		           
	}
	
	public ModelAndView formQueryShowDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_showdetail");   
		return mav; 
	}
	
	public ModelAndView formQueryConditionSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_conditionset");   
		return mav; 
	}
	
	public ModelAndView formQueryCustomSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_customset");   
		return mav; 
	}
	
	public ModelAndView formQueryCustomFieldSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_customfieldset");   
		return mav; 
	}
	
	public ModelAndView formQueryDataArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_querydataarea");   
		return mav; 
	}
	
	public ModelAndView formQueryDataAreaSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_querydataareaset");   
		return mav; 
	}
	public ModelAndView formQueryLogSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_logsetting");   
		return mav; 
	}
	
	/*
	 * 点击查询、统计用户输入条件中的增加/修改按钮
	 */
	public ModelAndView formQueryParameterSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_paramset");   
		//Collection collection = metadataManager.getAllUserDefinedMetadatas();
		Collection collection = metadataManager.getAllUserDefinedMetadatasForForm();
		List<Metadata> QueryMetadata = new ArrayList<Metadata>();
		if(null!=collection && collection.size()>0){
			QueryMetadata.addAll(collection);
		}
		mav.addObject("QueryMetadata", QueryMetadata);
		return mav; 
	}
	
	public ModelAndView formQueryResultSort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_resultsort");   
		return mav; 
	}
	
	public ModelAndView formQueryFormStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_formstatus");   
		return mav; 
	}
	public ModelAndView formQueryFormFinish(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_formfinish");   
		return mav; 
	}
	public ModelAndView formlongtextWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/longtextWindow");   
		return mav; 
	}
	public ModelAndView formDateWindow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formquery/date");
		return mav;
	}
	//显示字段输入类型为单选按钮或下拉列表框全部枚举值
	public ModelAndView formInputtype(HttpServletRequest request,HttpServletResponse response) throws Exception{	
		ModelAndView mav = new ModelAndView("form/formquery/showmenuid");
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject) session
				.getAttribute("SessionObject");
		String fieldname = request.getParameter("fieldname");
		Metadata queryMetadata=null;
		String relationType = request.getParameter("relationType");
	    if(StringUtils.isBlank(relationType)){
		    for(int i =0;i<sessionObject.getTableFieldList().size();i++){
				TableFieldDisplay tafield = (TableFieldDisplay)sessionObject.getTableFieldList().get(i);
				if(tafield.getName().equals(fieldname)){
					Element Enumroot = dom4jxmlUtils.paseXMLToDoc(OperHelper.parseSpecialMark(tafield.getEnumtype())).getRootElement();	
					InfoPath_Enum enums = new InfoPath_Enum();
					enums.loadFromXml(Enumroot);
					queryMetadata = metadataManager.getMetadata(enums.getEnumid());
					if(enums.isFinalChild()){
						List<MetadataItem> items = this.metadataManager.getLastLevelItemByMetadataId(enums.getEnumid());
						mav.addObject("items", items).addObject("optionValueUseId", "true");
					}
			     }
			}
	    }else if("1".equals(relationType)){//关联hr人员类型
	    	String refKey = request.getParameter("refKey");
	    	HrStaffInfoField hrField = FormHelper.getHrStaffInfoField();
	    	queryMetadata = hrField.getMetadataById(refKey);
	    }else if("2".equals(relationType)){//关联表单类型
	    	String refInputAttr = request.getParameter("refKey");
	    	String formAppId = request.getParameter("refFormAppId");
	    	if(org.apache.commons.lang.StringUtils.isNotEmpty(formAppId)){
	    		SeeyonForm_ApplicationImpl afapp = (SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formAppId));
				if(afapp!=null){
//					String defauliname = afapp.getFormProperty("defaultInput.xml",afapp.C_iPropertyType_UserDefineXML);
//					InfoPath_Inputtypedefine inpointy = new InfoPath_Inputtypedefine(afapp);	
//					inpointy.loadFromXml(DocumentHelper.parseText(afapp.getFResourceProvider().loadResource(defauliname)).getRootElement());
					InfoPath_Inputtypedefine inpointy = FormHelper.getInfoPathInputtypedefine(afapp,afapp.getFResourceProvider());
					List<IIP_InputObject> iobjectList =inpointy.getInputList();
					for (IIP_InputObject iip_InputObject : iobjectList) {
						if(refInputAttr.equals(iip_InputObject.getDataAreaName())){
							if(iip_InputObject instanceof TIP_InputRadio){
								queryMetadata = metadataManager.getMetadata(((TIP_InputRadio)iip_InputObject).getFEnumId());
							}
							if(iip_InputObject instanceof TIP_InputSelect){
								TIP_InputSelect selectInputObject = (TIP_InputSelect)iip_InputObject;
								queryMetadata = metadataManager.getMetadata(selectInputObject.getFEnumId());
								if(selectInputObject.isFinChild()){
									List<MetadataItem> items = this.metadataManager.getLastLevelItemByMetadataId(selectInputObject.getFEnumId());
									mav.addObject("items", items).addObject("optionValueUseId", "true");
								}
							}
						}
					}  
				}
			}
		}else if("3".equals(relationType)){
    		String refEnumId = request.getParameter("refEnumId");
    		String currentLevelNum = request.getParameter("currentLevelNum");
    		if(Strings.isNotBlank(refEnumId) && Strings.isNotBlank(currentLevelNum)){
    			queryMetadata = this.metadataManager.getMetadata(Long.parseLong(refEnumId));
				if(queryMetadata != null){
					List<MetadataItem> items = this.metadataManager.getLevelItemOfMetadata(queryMetadata,Integer.parseInt(currentLevelNum));
					mav.addObject("items", items).addObject("optionValueUseId", "true");
				}
    		}
    	} else if("select".equals(relationType)){
    		String topEnumId = FormHelper.getRefTopMedata(sessionObject,fieldname);
    		if(topEnumId != null){
    			int levelNum = FormHelper.getFieldDisplayLevel(sessionObject, fieldname);
    			queryMetadata = this.metadataManager.getMetadata(Long.parseLong(topEnumId));
    			if(queryMetadata != null){
    				List<MetadataItem> items = this.metadataManager.getLevelItemOfMetadata(queryMetadata,levelNum);
    				mav.addObject("items", items).addObject("optionValueUseId", "true");
    			}
    		}
		}
		mav.addObject("queryMetadata", queryMetadata);
		return mav;
	}


	//显示字段输入类型为扩展控件
	private String parseString(String extendText, String initValue, String fieldName, SessionObject sessionobject) throws SeeyonFormException{
	    String inputStr = "";
	    IInputExtendManager fmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();                                                                                                                                                                                                                                                 
	    ISeeyonInputExtend fvalue = (ISeeyonInputExtend)fmanager.findByName(extendText);   
	    String page = null;
	    
	    if(fvalue instanceof InputExtend_ExampleDatetime){
			InputExtend_ExampleDatetime fdatetime = (InputExtend_ExampleDatetime)fvalue;
			page = fvalue.getInputOnCilikURL(ISeeyonForm.TviewType.vtHtml);
			page = page +"?type=" +fdatetime.getType();
			inputStr = (new StringBuilder("<span><input name='")).append(fieldName).append("' value='").append(initValue).append("'  size=\"20\"  readonly />").append("<img src='").append(fvalue.getInputImage(ISeeyonForm.TviewType.vtHtml)).append("' onclick =extendEvent(this,\"_window.showModalDialog('").append(page).append("',window,'dialogHeight:").append(fvalue.getWindowHeight(ISeeyonForm.TviewType.vtHtml)).append("px;").append("dialogWidth:").append(fvalue.getWindowWidth(ISeeyonForm.TviewType.vtHtml)).append("px").append("')\") /></span>").toString();
		}else if(fvalue instanceof InputExtend_SelectColSummay){
			String columnName = "my:" + fieldName;
			List FieldInputList = sessionobject.getFieldInputList();
			InfoPath_FieldInput ifipObj = null;
			if(CollectionUtils.isNotEmpty(FieldInputList)){
				for (int i = 0; i < FieldInputList.size(); i++) {
					InfoPath_FieldInput ifip = (InfoPath_FieldInput)FieldInputList.get(i);
					if(columnName.equals(ifip.getFName())){
						ifipObj = ifip;
						break;
					}
				}
			}
			if(ifipObj != null){
				StringBuilder sb = new StringBuilder();
				sb.append("<span><input name='")
				.append(fieldName)
				.append("' value='").append(initValue).append("' ")
				.append(" relInputAtt=\"").append(ifipObj.getRefInputAtt() != null ? ifipObj.getRefInputAtt() : "").append("\" ")
				.append(" extendNameType=\"").append(extendText).append("\" ");
				String refFormId = ifipObj.getRefParams();
				if(Strings.isNotBlank(refFormId)){
					SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(refFormId));
					if(fapp != null){
    					sb.append(" formAppId=\""+ refFormId +"\" formtype=\""+ fapp.getFormType()+ "\"") ;	
    				}else{
    					sb.append(" formAppId=\""+ refFormId +"\" ") ;
    				}
				}
				sb.append(" size=\"20\"  readonly />")
				.append("<img src='")
				.append(fvalue.getInputImage(ISeeyonForm.TviewType.vtHtml))
				.append("' onclick =extendEvent(this,\"_window.showModalDialog('")
				.append(fvalue.getInputOnCilikURL(ISeeyonForm.TviewType.vtHtml))
				.append(refFormId)
				.append("',window,'dialogHeight:")
				.append(fvalue.getWindowHeight(ISeeyonForm.TviewType.vtHtml))
				.append("px;").append("dialogWidth:")
				.append(fvalue.getWindowWidth(ISeeyonForm.TviewType.vtHtml))
				.append("px").append("')\") /></span>");
				inputStr = sb.toString();
			}
		} else {
			inputStr = (new StringBuilder("<span><input name='")).append(fieldName).append("' value='").append(initValue).append("'  size=\"20\"  readonly />").append("<img src='").append(fvalue.getInputImage(ISeeyonForm.TviewType.vtHtml)).append("' onclick =extendEvent(this,\"_window.showModalDialog('").append(fvalue.getInputOnCilikURL(ISeeyonForm.TviewType.vtHtml)).append("',window,'dialogHeight:").append(fvalue.getWindowHeight(ISeeyonForm.TviewType.vtHtml)).append("px;").append("dialogWidth:").append(fvalue.getWindowWidth(ISeeyonForm.TviewType.vtHtml)).append("px").append("')\") /></span>").toString();
		}
	   return inputStr;
	}
	public ModelAndView formextendvalue(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject) session.getAttribute("SessionObject");
		ModelAndView mav = new ModelAndView("form/formquery/showextendvalue");  
	    String fieldName = request.getParameter("fieldname");
	    String fieldType = request.getParameter("fieldtype");
	    String selectId = request.getParameter("selectId");
	    String extendText = request.getParameter("extendText");
	    String relationType = request.getParameter("relationType");
	    if(StringUtils.isBlank(relationType)){
		    boolean isExist = false;
		    for(int i =0;i<sessionObject.getTableFieldList().size();i++){
				TableFieldDisplay td = (TableFieldDisplay)sessionObject.getTableFieldList().get(i);
				if(td.getName().equals(fieldName)){
					extendText = td.getExtend();
					isExist=true;
				}
		    }  
		    if(!isExist){
		    	if(fieldName.startsWith(SeeyonFormPojo.C_sFieldName_Start_member_id)){
		    		extendText = "选择人员";
		    	}else if(fieldName.startsWith(SeeyonFormPojo.C_sFieldName_Start_date)){
		    		extendText = "日期选取器";
		    	}
		    	fieldName = Constantform.getString4OtherKey(fieldName);
		    }
	    }
	    String TextString = parseString(extendText,"",fieldName,sessionObject);
	    mav.addObject("fieldType", fieldType);
	    mav.addObject("selectId", selectId);
	    mav.addObject("extendText", extendText);
	    mav.addObject("TextString", TextString);
	    mav.addObject("isTrigger", request.getParameter("isTrigger"));
	    return mav;
	}
	
	
	public ModelAndView selectCheckbox(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formquery/selectCheckBox");  
	    String fieldName = request.getParameter("fieldname");
	    mav.addObject("fieldName", fieldName);
	  return mav;
	}
	
	//查询条件首页 保存 操作
	public ModelAndView addCondition(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject) session
				.getAttribute("SessionObject");	
		QueryObject qo = new QueryObject();
		bind(arg0, qo);
	    if(!"".equals(qo.getAuthorValue()) || !"null".equalsIgnoreCase(qo.getAuthorValue()) || qo.getAuthorValue() !=null){
	    	qo.setAuthorName("");
	    	qo.setAuthorValue("");
	    }
        /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	     * 操作为：操作1|操作2的形式
	     */
		String showDetail = arg0.getParameter("detailid");	
		String formnames = arg0.getParameter("formnames");	
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
	    qo.setFormId(formids);
	    qo.setOperationId(operationids);
	    qo.setForm(formnames);
	    
		String queryId =  String.valueOf(Long.valueOf(UUIDLong.longUUID()));
		qo.setQueryId(queryId);
		sessionObject.getQueryConditionList().add(qo);
		return super.redirectModelAndView("/formquery.do?method=formQueryNew");
			  
	} 
	
	//专为保存授权 提供的方法
	public ModelAndView addConditionforau(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject)session
										.getAttribute("SessionObject");
		List<QueryObject> queryConditionList = sessionObject.getQueryConditionList();	
		String queryN = arg0.getParameter("queryN");
		String authorvalue = arg0.getParameter("authorvalue");
		for(int i=0;i<queryConditionList.size();i++){
			QueryObject queryObject = (QueryObject)queryConditionList.get(i);		
			if(queryObject.getQueryName().equals(queryN)){		   
				bind(arg0, queryObject);
				queryObject.setAuthorValue(authorvalue);
			    break;
	         }
		}
		return super.redirectModelAndView("/formquery.do?method=formQueryNew");	  
	} 
	
	//	查询条件首页 更新 操作
	public ModelAndView updateCondition(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		String queryname = arg0.getParameter("queryName");
		List<QueryObject> queryConditionList = sessionObject.getQueryConditionList();
		int id = Integer.parseInt(arg0.getParameter("id"));	
		for(int i=0;i<queryConditionList.size();i++){
			QueryObject queryObject = (QueryObject)queryConditionList.get(i);		
	            if(i == id){	
	               if(!"".equals(queryname) && !"null".equals(queryname) && queryname !=null){
	            	    bind(arg0, queryObject);
	            	    
	            	    //加防护，如果数据域拼的xml丢失，则重新拼接。
	            	    if("".equals(queryObject.getDataFieldValue())){           	    	
	            	    	SeeyonQueryImpl seeyonquery = (SeeyonQueryImpl)sessionObject.getSeedatadefine().getFApp().findQueryByName(queryname);
	            	    	StringBuffer sbColumn = new StringBuffer();
	            	    	sbColumn.append("<ShowDataList>\r\n");
	            	    	for(int a = 0;a<seeyonquery.getDataColumList().size();a++){
	            	    		QueryColum queryc = (QueryColum)seeyonquery.getDataColumList().get(a);
	            	    		sbColumn.append(queryc.getXml());
	            	    	}
	            	    	sbColumn.append("</ShowDataList>\r\n");           	    	
	            	    }
	            	    
	            	    if("null".equals(queryObject.getAuthorValue()))
	            	    	queryObject.setAuthorValue("");
	   				    if("null".equals(queryObject.getAuthorName()))
	   					    queryObject.setAuthorName("");
		      			 
	   				    /*
	   				     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	   				     * 视图1.操作|视图2.操作  .......
	   				     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	   				     * 操作为：操作1|操作2的形式
	   				     */
		      			String showDetail = arg0.getParameter("detailid");	
		      			String formnames = arg0.getParameter("formnames");	
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
		      		     queryObject.setFormId(formids);
		      		     queryObject.setOperationId(operationids);
		      		     queryObject.setForm(formnames);
	               }else{
	            	   String queryAuthorName = arg0.getParameter("authorName");
	  				   String queryAuthorValue = arg0.getParameter("authorValue");
	  				   queryObject.setAuthorName(queryAuthorName);
	  				   queryObject.setAuthorValue(queryAuthorValue);
	  				 //对showdetail特殊处理
		      			String showDetail = arg0.getParameter("detailid");	
		      			String formnames = arg0.getParameter("formnames");	
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
		      		     queryObject.setFormId(formids);
		      		     queryObject.setOperationId(operationids);
		      		     queryObject.setForm(formnames);
	               }
				
	
			    break;
	         }
		}
		return super.redirectModelAndView("/formquery.do?method=formQueryNew");
		 
	} 
	
	
	//查询条件授权 更新 操作
	public ModelAndView updateConditionAuth(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject)session
										.getAttribute("SessionObject");
		
		String queryname = arg0.getParameter("queryName");
		List<QueryObject> queryConditionList = sessionObject.getQueryConditionList();
		//int id = Integer.parseInt(arg0.getParameter("id"));
	    String ids = arg0.getParameter("ids");
	    HashMap queryidmap = new HashMap();
		if(ids.indexOf(",") > -1){
			String[] idsarry = ids.split(",");
			   for(int i = 0 ; i < idsarry.length; i++){
				   queryidmap.put(idsarry[i], idsarry[i]) ;			   
			   }
		}   
		for(int i=0;i<queryConditionList.size();i++){
			QueryObject queryObject = (QueryObject)queryConditionList.get(i);		
	            //if(i == id){	
		     Integer id = i;
	            if(queryidmap.get(id.toString()) != null){
	               if(!"".equals(queryname) && !"null".equals(queryname) && queryname !=null){
	            	    bind(arg0, queryObject);
	//	      			 //对showdetail特殊处理
	//	      			String showDetail = arg0.getParameter("detailid");			
	//	      		    String showDetails[] = showDetail.split("\\.");
	//	      		    queryObject.setFormId(showDetails[0]);
	//	      		    queryObject.setOperationId(showDetails[1]);	
	            	    /*
	            	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	            	     * 视图1.操作|视图2.操作  .......
	            	     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	            	     * 操作为：操作1|操作2的形式
	            	     */
	            		String showDetail = arg0.getParameter("detailid");	
	            		String formnames = arg0.getParameter("formnames");	
	            		String formids = "";
	            		String operationids  = "";
	            		if(!"".equals(showDetail)){
	            			for(int a=0;a<showDetail.split("\\|").length ;a++){
	               	    	 String showDetailsArray[] = showDetail.split("\\|");
	               	    	 String showDetails[] = showDetailsArray[a].split("\\.");
	               	    	 formids += showDetails[0]+"|";
	               	    	 operationids += showDetails[1]+"|";   		    	       		         	
	               	    }
	               	    queryObject.setFormId(formids);
	               	    queryObject.setOperationId(operationids);
	               	    queryObject.setForm(formnames);
	            		}
	            	    
	               }else{
	            	   String queryAuthorName = arg0.getParameter("authorNamelist"+i);
	  				   String queryAuthorValue = arg0.getParameter("authorValuelist"+i);
	  				   queryObject.setAuthorName(queryAuthorName);
	  				   if(Strings.isNotBlank(queryObject.getAuthorValue()) && !queryObject.getAuthorValue().equals(queryAuthorValue)){
	  					 queryObject.setChanged(true) ;
	  				   }
	  				   queryObject.setAuthorValue(queryAuthorValue);
	  				   
	////  				对showdetail特殊处理
	//	      			String showDetail = arg0.getParameter("detailid");			
	//	      		    String showDetails[] = showDetail.split("\\.");
	//	      		    queryObject.setFormId(showDetails[0]);
	//	      		    queryObject.setOperationId(showDetails[1]);	
	  				 /*
	  				     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	  				     * 视图1.操作|视图2.操作  .......
	  				     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	  				     * 操作为：操作1|操作2的形式
	  				     */
	  				 if("".equals(queryObject.getFormId()) || "null".equals(queryObject.getFormId()) || queryObject.getFormId() ==null){
	  					String showDetail = arg0.getParameter("detailid");	
	  					String formnames = arg0.getParameter("formnames");	
	  					if(!"".equals(showDetail)){
	  						String formids = "";
	  	  					String operationids  = "";
	  	  				    for(int a=0;a<showDetail.split("\\|").length ;a++){
	  	  				    	 String showDetailsArray[] = showDetail.split("\\|");
	  	  				    	 String showDetails[] = showDetailsArray[a].split("\\.");
	  	  				    	 formids += showDetails[0]+"|";
	  	  				    	 operationids += showDetails[1]+"|";   		    	       		         	
	  	  				    }
	  	  				  queryObject.setFormId(formids);
	  	  				  queryObject.setOperationId(operationids);
	  	  				  queryObject.setForm(formnames);
	  					} 
	  				 }						 
	               }
				
	
			   // break;
	         }
		}
		return super.redirectModelAndView("/formquery.do?method=formQueryNew");
		 
	}
	
	
	
	//查询条件首页 删除 操作
	public ModelAndView delCondition(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		//int id = Integer.parseInt(arg0.getParameter("id"));
		String queryallname = arg0.getParameter("queryallname");
		HashMap querynamemap = new HashMap();
		if(queryallname.indexOf("↗") > -1){
			   String[] idname = queryallname.split("↗");
			   for(int i = 0 ; i < idname.length; i++){
				   querynamemap.put(idname[i], idname[i]) ;			   
			   }
		}
		HttpSession session = arg0.getSession();
		SessionObject sessionObject = (SessionObject) session
				.getAttribute("SessionObject");
		//sessionObject.getQueryConditionList().remove(id);
	    for(int i=0;i<sessionObject.getQueryConditionList().size();i++){
	    	QueryObject queryobj =(QueryObject)sessionObject.getQueryConditionList().get(i);
	    	if(querynamemap.get(queryobj.getQueryName()) !=null){
	    		sessionObject.getQueryConditionList().remove(i);
	    		i--;
	    	}
	    		
	    }
		return super.redirectModelAndView("/formquery.do?method=formQueryNew");
	}
	
	
	/**
	 * 表单查询设置时的预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formQueryPreViewMake(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formquery/query_querypreviewmake"); 	
		return mav; 	
	}
	
	
	/**
	 * 表单查询执行时的预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formQueryPreView(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		List<QueryObject> queryConditionList = sessionObject.getQueryConditionList();
		List<QueryColum> datacolumlist = new ArrayList<QueryColum>();
		String formid = request.getParameter("formid");	
		String queryname = request.getParameter("queryname");
		String planid = request.getParameter("planid");
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
		.getInstance().getAppManager().findById(Long.parseLong(formid));
        String formname = fapp.getAppName();
        if(Strings.isNotBlank(planid)){
        	FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(planid));
        	Document doc  = dom4jxmlUtils.paseXMLToDoc(formQueryPlan.getPlanDefine());
        	Element root = doc.getRootElement();
        	Element showDataList = root.element(IXmlNodeName.ShowDataList);
        	if(showDataList != null){
    			int i = 0;
    			List ShowColumList = showDataList.elements();
    			for(Object item : ShowColumList){
    				Element e = (Element)item;
    				QueryColum queryColum = new QueryColum();
    				queryColum.loadFromXml(e);
    				datacolumlist.add(queryColum);
    				i++;
    			}
        	}
        } else {
			for(int i=0;i<queryConditionList.size();i++){
				QueryObject queryObject = (QueryObject)queryConditionList.get(i);		
	            if(queryname.equals(queryObject.getQueryName()) && formname.equals(queryObject.getFormname())){		   
					queryObject.getDataFieldValue();
					ChangeObjXml objxml = new ChangeObjXml();
					Element Computeroot = dom4jxmlUtils.paseXMLToDoc(objxml.createQueryListXml(0, queryObject)).getRootElement();	
					SeeyonQueryImpl  seeyon = new SeeyonQueryImpl();
					seeyon.loadFromXml(Computeroot);
					datacolumlist = seeyon.getDataColumList();
					break;
	            }
			}
        }
		QueryUserConditionDefin querydefine = new QueryUserConditionDefin();
		querydefine.setTitle(ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request), "form.query.querydate"));
		ModelAndView mav = new ModelAndView("form/formquery/query_querypreview"); 
		mav.addObject("datalist", datacolumlist);
		mav.addObject("formname", formname);
		return mav; 
	}
	
	/**
	 * 点击表单查询后的首页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formQueryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
	    //	对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
			sessionobject = null;
		}
		if(sessionobject ==null){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject", sessionobject);
		}
		ModelAndView mav = new ModelAndView("form/formquery/query_querylist");
		
		if(!FormBizConfigUtils.validate(mav, request, response, FormBizConfigConstants.MENU_FORM_QUERY))
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
			//如果是从表单业务配置而来的，而业务配置所配置的表单模板全部不可用或被删除，那么此时的表单ID集合为空，所看到的页面不显示当前用户有权查看的全部查询模板
			String flag = request.getParameter("flag");
			if((Strings.isNotBlank(flag) && appidlist.size()>0) || Strings.isBlank(flag)) {
				//获取未停用表单列表
				List <FormAppMain> newAppList = new ArrayList<FormAppMain>();
	            // 获取表单分类列表
	            Set<TempleteCategory> templeteCategories = new LinkedHashSet<TempleteCategory>();
	            // 获取表单名称列表
	            Set<String> formNameList = new LinkedHashSet<String>();
	            getIOperBase().getformAccess(newAppList, templeteCategories, formNameList, appidlist, IPagePublicParam.C_iObjecttype_Query) ;
	            
	            //根据当前用户获取我的模板列表
	            List<FormQueryPlan> plans = getFormDaoManager().findByUserId(CurrentUser.get().getId(), IPagePublicParam.C_iObjecttype_Query);
	            //如果没有自定义查询项，过滤掉我的查询
	            List<FormQueryPlan> removePlans = new ArrayList<FormQueryPlan>();
	            for(FormQueryPlan plan : plans){
	            	ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(plan.getAppmainId());
	            	if(afapp != null){
	            		SeeyonQueryImpl query = (SeeyonQueryImpl)afapp.findQueryByName(plan.getQueryName()); 
	            		if(query == null || query.getQueryColumList().size() == 0){
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
			log.error("点击表单查询后的首页面报错",e);
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			log.error("点击表单查询后的首页面报错",e);
			OperHelper.creatformmessage(request,response,lst);
		}finally{
			return mav;
		}
	}
	public ModelAndView bizConfigFormQueryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		ModelAndView mav = new ModelAndView("formbizconfig/write/show_bizconfig_formquery");
	    String categoryHTML=iOperBase.categoryHTML(templeteCategoryManager).toString();
	    mav.addObject("categoryQueryHTML", categoryHTML);
	    return mav;
	}
	
	/**
	 * 返回该登录用户能看到的所有的查询
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView bizQueryListTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/show_bizconfig_formquery_tree");
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
							List<FormApp> list = new ArrayList<FormApp>();
							List<ISeeyonQuery> queryList =  fappImpl.getQueryList();
							boolean flag = false;
							if(queryList != null){
								for(ISeeyonQuery query: queryList){
									if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
										if(query.getQueryName().indexOf(textfield)==-1) continue;
									}
									FormApp formAppObject = new FormApp();
									formAppObject.setId(query.getId());
									formAppObject.setName(query.getQueryName());
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
	
	/**
	 * 表单查询首页面的执行设置
	 */
	public ModelAndView formQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    ModelAndView mav = new ModelAndView("form/formquery/query_formquery"); 
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String formid = request.getParameter("formid");
		String queryname = request.getParameter("queryname");
		String planid = request.getParameter("planid");
		if(Strings.isBlank(formid) || Strings.isBlank(queryname)){
			return mav;
		}
		
		User user = CurrentUser.get();
		if(sessionobject ==null 
			|| sessionobject.getQuerylist() == null 
			|| sessionobject.getQuerylist().size() < 1
			|| sessionobject.getQueryConditionList() == null 
			|| sessionobject.getQueryConditionList().size() < 1){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject", sessionobject);
			List<Long> formobjlist = FormBizConfigUtils.getUserDomainIds(user, null);
			List applst =new ArrayList();
			List<Long> appidlist = new ArrayList<Long>();
			applst = getIOperBase().queryAllAccess(formobjlist,appidlist,1);
			getIOperBase().assignQuery(applst,sessionobject, user);
		}
		if(iOperBase.checkAccess(user,Long.valueOf(formid),queryname,1)){
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
			SeeyonQueryImpl query = (SeeyonQueryImpl)afapp.findQueryByName(queryname);
			if(query == null){
				mav.addObject("formname", name);
				return mav;
			}
			ParseUserCondition parseUserCondition = new ParseUserCondition();
			List<QueryColum> queryColumns = query.getQueryColumList();
			for(QueryColum queryColum : queryColumns){
				inputTypeDefine.field(queryColum.getDataAreaName());
				parseUserCondition.parseCustomCondition(queryColum,inputTypeDefine);
			}
			ConditionListQueryImpl conditionList = (ConditionListQueryImpl)query.getUserConditionList();	
			if(conditionList.isHasUserCondition()){
				DataColumImpl dataColumn = null;
				QueryUserConditionDefin userConditionDefin = null;
				Set<String> userConditions = new HashSet<String>();//存放用户输入条件名称，当多个条件引用时只生成一个
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
			//当主表为Group时去掉单据状态和流程状态选项
			if(query.getDBProvider().getDataSource().findGroupByTableName(query.getQuerySource().getMasterTable()) == null){
				ConditionListImpl filter = (ConditionListImpl)query.getFilter();
				boolean isFlowForm = ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue() == ((SeeyonForm_ApplicationImpl)afapp).getFormType();
				parseUserCondition.genStatusHTML(filter.getConditionList(),isFlowForm);	
				if(isFlowForm){
					mav.addObject("statusRow", parseUserCondition.getStatusRow());
				}
			}
			if(queryColumns != null && queryColumns.size() > 0){
				mav.addObject("columnNames", parseUserCondition.getColumnNames());
				mav.addObject("blankInput", parseUserCondition.getBlankInput());
				mav.addObject("inputs", parseUserCondition.getInputs());
			}
			//保存显示设置
			List<QueryColum> dataColumList = new ArrayList<QueryColum>();
			//保存排序方式
			List<OrderByColum> orderByList=new ArrayList<OrderByColum>();
			if(Strings.isNotBlank(planid)){
				FormQueryPlan plan = getFormDaoManager().getFormQueryPlanById(Long.parseLong(planid));
				if(plan == null){
					PrintWriter out = response.getWriter();
	                out.println("<script>");
	                out.println("alert(parent.v3x.getMessage(\"formLang.formquery_myplan_delete\"));");
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
					parseUserCondition.parsePlanCondition(conditions, queryColumns, inputTypeDefine);
					List<String> columns = parseUserCondition.getColumns();
					List<String> inputColumns = parseUserCondition.getInputColumns();
					mav.addObject("columns", columns);
					mav.addObject("inputColumns", inputColumns);
				}
				Element showDataList = root.element(IXmlNodeName.ShowDataList);
				if(showDataList != null){
					String field = "field";
					int i = 0;
					List showColumList = showDataList.elements();
					for(Object item : showColumList){
						Element e = (Element)item;
						QueryColum queryColum = new QueryColum();
						queryColum.loadFromXml(e);
						//fieldAlias.put(queryColum.getDataAreaName(), field + i );
						dataColumList.add(queryColum);
						i++;
					}
				}
				Element orderBy = root.element(IXmlNodeName.OrderBy);
				if(orderBy != null){
					List eOrderByList = orderBy.elements();
					for(Object item : eOrderByList){
						Element e = (Element)item;
						OrderByColum orderByColum = new OrderByColum();
						orderByColum.loadFromXml(e);
						orderByList.add(orderByColum);
					}
				}
				mav.addObject("plan", plan);
			} else {
				dataColumList = query.getDataColumList();
				orderByList = query.getOrderByList();
			}
			if(!conditionList.isHasUserCondition()){
				mav.addObject("query", "query");
			}
			if(conditionList.getConditionList().size() > 0){
			    mav.addObject("condition", parseUserCondition.getCondition());
			}
		    mav.addObject("realValue", parseUserCondition.getRealValue());
			sessionobject = getIOperBase().systemenum(sessionobject);
		    IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
		    List<TableFieldDisplay> tableFieldList = iOperBase.loadFromnoInfoPath(afapp, formid).getTableFieldList();
		    List<TableFieldDisplay> sessionTableFieldList = new ArrayList<TableFieldDisplay>();
		    
			for(TableFieldDisplay tableField : tableFieldList){
				for(QueryColum colum : query.getDataColumList()){
					if(OperHelper.noNamespace(colum.getDataAreaName()).equals(tableField.getName())){
						sessionTableFieldList.add(tableField);
					}
				}	
			}
			sessionobject.setTableFieldList(sessionTableFieldList);
			
			StringBuilder querycolumns=new StringBuilder();
			for(QueryColum colum : dataColumList){
			    String columTitle = colum.getColumTitle();
				if(!columTitle.equals(OperHelper.noNamespace(colum.getDataAreaName()))){
					columTitle = OperHelper.noNamespace(colum.getDataAreaName()) + "(" + columTitle + ")";
				}
				querycolumns.append("," + columTitle);
			}
		
			StringBuilder queryorders = new StringBuilder();
			for(OrderByColum colum : orderByList){
				queryorders.append("," + OperHelper.noNamespace(colum.getColmunName()));
				if(colum.getType() == 0)
					queryorders.append("↑");
				else if(colum.getType() == 1){
					queryorders.append("↓");
				}
			}
			mav.addObject("queryorders", queryorders.length() > 0 ? queryorders.substring(1) : "");
			String qcs = BindHelper.turnData(querycolumns.substring(1));
			mav.addObject("querycolumns", qcs);
			mav.addObject("formname", name);
			return mav; 
		}else{
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert(\""+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request), "form.showAppFormData.noright")+"\");");
            out.println("</script>");
            out.flush();
            out.close();
			return null;
		}
	} 
	
	public static void saveOrUpdateFormPlan(HttpServletRequest request, FormDaoManager formDaoManager) throws SeeyonFormException{
		Long userId = CurrentUser.get().getId();
	 	String formId = request.getParameter("formid");
        String queryName = request.getParameter("queryname");
        String reportName = request.getParameter("reportname");
        String planName = request.getParameter("planName");
        String planId = request.getParameter("planid");
        ConditionListImpl conditionList = null;
        Map<String,String> displayConditions = new LinkedHashMap<String,String>();
        SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formId));
        if(Strings.isNotBlank(queryName)){
        	SeeyonQueryImpl query = (SeeyonQueryImpl)fapp.findQueryByName(queryName); 
        	conditionList = (ConditionListQueryImpl)query.getUserConditionList().copy();
        } else if(Strings.isNotBlank(reportName)){
        	SeeyonReportImpl report = (SeeyonReportImpl)fapp.findReportByName(reportName);
        	conditionList = (ConditionListReportImpl)report.getUserConditionList().copy();
        }
        genConditionStr(request, displayConditions, conditionList, true);
        StringBuffer sb = new StringBuffer();
        sb.append("<Query>");
        List<ICondition> conditions = conditionList.getConditionList();
        if(conditions != null && conditions.size() != 0){
	        sb.append("<UserConditionList>");
	        for(ICondition item : conditions){
	            sb.append(item.getXml());
	        }
	        sb.append("</UserConditionList>");
        }
        //显示方式
        String dataFieldStr = request.getParameter("dataField");
        if(Strings.isNotBlank(dataFieldStr)){
        	sb.append("<ShowDataList>");
        	String[] dataFields = dataFieldStr.split(",");
        	for(String dataField : dataFields){
        		String alias = dataField;
        		int left = dataField.indexOf("(");
        		int right = dataField.indexOf(")");
        		if(left != -1 &&  right != -1){
        			alias = dataField.substring(left + 1,right);
        			dataField = dataField.substring(0,left);
        		}
        		QueryColum queryColum = new QueryColum();
        		queryColum.setColumTitle(alias);
        		queryColum.setDataAreaName(dataField);
        		sb.append(queryColum.getXml());
        	}
        	sb.append("</ShowDataList>");
        }
        //排序方式
        String resultSortStr = request.getParameter("resultSort");
        if(Strings.isNotBlank(resultSortStr)){
        	sb.append("<OrderBy>");
        	String[] resultSorts = resultSortStr.split(",");
        	for(String resultSort : resultSorts){
        		OrderByColum orderByColum = new OrderByColum();
        		String columName = resultSort.substring(0,resultSort.length() -1);
        		String sortType = resultSort.substring(resultSort.length() -1);
        		orderByColum.setColmunName(columName);
        		if("↑".equals(sortType)){
        			orderByColum.setType(OrderByColum.C_iOrderBy_ASC);
        		}else{
        			orderByColum.setType(OrderByColum.C_iOrderBy_Desc);
        		}
        		sb.append(orderByColum.getXml());
        	}
        	sb.append("</OrderBy>");
        }     
        sb.append("</Query>");
        FormQueryPlan formQueryPlan = null;
        if(Strings.isNotBlank(planId)){
        	formQueryPlan = formDaoManager.getFormQueryPlanById(Long.parseLong(planId));
        } else {
	        formQueryPlan = new FormQueryPlan();
	        formQueryPlan.setId(Long.valueOf(UUIDLong.longUUID()));
	        formQueryPlan.setAppmainId(Long.valueOf(formId));
	        if(Strings.isNotBlank(queryName)){
	        	formQueryPlan.setQueryName(queryName);
	        	formQueryPlan.setPlanType(IPagePublicParam.C_iObjecttype_Query);
	        } else if(Strings.isNotBlank(reportName)) {
	        	formQueryPlan.setQueryName(reportName);
	        	formQueryPlan.setPlanType(IPagePublicParam.C_iObjecttype_Report);
	        }
	        formQueryPlan.setUserId(Long.valueOf(userId));
        }
        formQueryPlan.setPlanName(planName);
        formQueryPlan.setPlanDefine(sb.toString());
        if(Strings.isNotBlank(planId)){
        	formDaoManager.updateFormQueryPlan(formQueryPlan);
        } else {
        	formDaoManager.saveFormPlan(formQueryPlan);
        }
	}
	public ModelAndView saveFormQueryPlan(HttpServletRequest request,HttpServletResponse response) throws Exception {
		saveOrUpdateFormPlan(request,formDaoManager);
		return null;
	}
	
	public ModelAndView deleteFormQueryPlan(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String planId = request.getParameter("planid");
		formDaoManager.deleteFormQueryPlanById(Long.parseLong(planId));
		//return this.redirectModelAndView("/formquery.do?method=formQuery");
		return null;
	}
	
    /**
     * 表单查询的执行结果
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView formQueryResult(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	String formid = request.getParameter("formid");
        String formname = null;
        boolean isFlow = true;
    	QueryResultImpl resultData = null;
        Map<String,String> displayConditions = new LinkedHashMap<String,String>();
        List<Map<String,String>> resultDatas = new ArrayList<Map<String,String>>();
        List<String> ids = new ArrayList<String>();
    	Map<String,String> columns =  new LinkedHashMap<String, String>();
        try {
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
					.getInstance().getAppManager().findById(Long.parseLong(formid));
			formname = fapp.getAppName();
			isFlow = fapp.getFormType() == ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue();
        	resultData = getResultData(request,response,displayConditions,true);
	        String dataFieldStr = request.getParameter("dataField");
	        Map<String,String> queryColumns = new LinkedHashMap<String, String>();
	        if(dataFieldStr != null && !"".equals(dataFieldStr)){
	        	String[] dataFields = dataFieldStr.split(",");
	        	for(String dataField : dataFields){
	        		String alias = dataField;
	        		int left = dataField.indexOf("(");
	        		int right = dataField.indexOf(")");
	        		if(left != -1 &&  right != -1){
	        			alias = dataField.substring(left + 1,right);
	        			dataField = dataField.substring(0,left);
	        		}
	        		queryColumns.put(dataField, alias);
	        	}
	        }
		    for(Map.Entry<String, String> entry : queryColumns.entrySet()){	
				String columnType = getColumnType(fapp, entry.getKey());
//				String align = "left";
				String align = "center";
//				if (IPagePublicParam.TIMESTAMP.equalsIgnoreCase(columnType)
//					 || IPagePublicParam.DATETIME.equalsIgnoreCase(columnType)){
//				     align = "center";
//				}else if(IPagePublicParam.DECIMAL.equalsIgnoreCase(columnType)){
//				     align = "right";
//				}
				String colDisplay = queryColumns.get(entry.getKey());
        		if(SeeyonFormPojo.C_sFieldNames.contains(colDisplay)){
        			colDisplay = Constantform.getString4OtherKey(colDisplay, colDisplay);
				}
				 columns.put(colDisplay, align);
			}
			for(int i = 0; i < resultData.getRecordCount(); i++){ 
				IQueryRecord record = resultData.getRecord(i);
				Map<String,String> row = new LinkedHashMap<String, String>();
			    for(int j = 0; j < resultData.getSchema().size(); j++){	
			    	String dataAreaName = resultData.getSchema().get(j).getDataAreaName();
			    	String columnName = null;
			    	if(SeeyonFormPojo.C_sFieldNames.contains(OperHelper.noNamespace(dataAreaName)))
			    		columnName = queryColumns.get(Constantform.getString4OtherKey(OperHelper.noNamespace(dataAreaName)));
			    	else
			    		columnName = queryColumns.get(OperHelper.noNamespace(dataAreaName));
			    	if(columnName != null){
		        		/*if(SeeyonFormPojo.C_sFieldNames.contains(columnName)){
		        			columnName = Constantform.getString4OtherKey(columnName, columnName);
						}*/
			    		row.put(columnName,record.getValueByName(dataAreaName));
			    	}
				}
			    //添加创建人与创建时间
			    if(queryColumns.containsKey(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_member_id)))){
			    	String columnName = queryColumns.get(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_member_id)));
			    	String startMemberName = record.getValueByName(SeeyonFormPojo.C_sFieldName_Start_member_id);
			    	startMemberName = Strings.isBlank(startMemberName)?"":startMemberName;
			    	row.put(columnName, startMemberName);
			    }
			    if(queryColumns.containsKey(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_date)))){
			    	String columnName = queryColumns.get(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_date)));
			    	String startDate = record.getValueByName(SeeyonFormPojo.C_sFieldName_Start_date);
			    	startDate = Strings.isBlank(startDate)?"":startDate;
			    	row.put(columnName, startDate);
			    }
			    ids.add(String.valueOf(record.getId()));
			    resultDatas.add(row);
			}
        } catch (SeeyonFormException e) {
            List<String> lst = new ArrayList<String>();
            lst.add(e.getToUserMsg());
            OperHelper.creatformmessage(request, response, lst);
            return new ModelAndView("form/formquery/showQueryResultException");
        } finally {
            // 释放调用的数据库资源
            if (resultData != null) {
                resultData.unInit();
            }
        }
    	ModelAndView mav = new ModelAndView("form/formquery/showQueryResult");
    	mav.addObject("querydate", Datetimes.formatDate(new Date()));
    	mav.addObject("columns", columns);
    	mav.addObject("ids", ids);
        mav.addObject("resultDatas", resultDatas);
        mav.addObject("displayConditions", displayConditions);
        mav.addObject("formname", formname);
        String from = request.getParameter("from");
        if (Strings.isNotBlank(from)) {
            mav.addObject("from", from);
        }
        mav.addObject("isFlow", isFlow);
        if(resultData.getRunner().getQuery().getShowDetail() != null){
            String resultFormname= resultData.getRunner().getQuery().getShowDetail().getFormName(); 
            String opername = resultData.getRunner().getQuery().getShowDetail().getOperName();
            String showdetail = resultFormname+"."+opername;
            mav.addObject("showdetail", showdetail);
            mav.addObject("appShowDetail", resultData.getRunner().getQuery().getShowDetail().getShowDetailStr());
        	mav.addObject("penetrate", "true");
        }
        return mav;

    }
    
    private QueryResultImpl getResultData(HttpServletRequest request,HttpServletResponse response,
    		Map<String,String> displayConditions, boolean isPagination) throws SeeyonFormException{
        ConditionListImpl conditionList = null;
        String formid = request.getParameter("formid");
        String queryname = request.getParameter("queryname");
        SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
                .getInstance().getAppManager().findById(Long.parseLong(formid));

        SeeyonQueryImpl query = (SeeyonQueryImpl)fapp.findQueryByName(queryname); 
        conditionList = (ConditionListQueryImpl)query.getUserConditionList().copy();
        genConditionStr(request, displayConditions, conditionList, false);    
        List<OrderByColum> orderByColums = query.getOrderByList();
        String resultSortStr = request.getParameter("resultSort");
        List<OrderByColum> newOrderByColums = new ArrayList<OrderByColum>();
        if(resultSortStr != null && !"".equals(resultSortStr)){
        	String[] resultSorts = resultSortStr.split(",");
        	for(String resultSort : resultSorts){
        		OrderByColum orderByColum = new OrderByColum();
        		String columName = resultSort.substring(0,resultSort.length() -1);
        		String sortType = resultSort.substring(resultSort.length() -1);
        		orderByColum.setColmunName(columName);
        		if("↑".equals(sortType)){
        			orderByColum.setType(OrderByColum.C_iOrderBy_ASC);
        		}else{
        			orderByColum.setType(OrderByColum.C_iOrderBy_Desc);
        		}
        		newOrderByColums.add(orderByColum);
        	}
        }     
    	query.setOrderByList(newOrderByColums);
    	query.setPagination(isPagination);
        QueryResultImpl resultData = query.getResultData(null, conditionList, request,response);
        query.setOrderByList(orderByColums);
        return resultData;

    }

    /**
     * 统过前台传入组织条件，暂以static，以便Report共用
     * @param request
     * @param displayConditions
     * @param conditionList
     * @param isMyPlan 是否为保存我的查询统计
     */
	public static void genConditionStr(HttpServletRequest request,
			Map<String, String> displayConditions,
			ConditionListImpl conditionList, boolean isMyPlan) throws SeeyonFormException{
		String realValue = request.getParameter("realValue");
        Map<String,String> realValues = new HashMap<String, String>();
        if(realValue != null && !"".equals(realValue.trim())){
        	String[] values = realValue.split("→");
        	for(String value : values){
        		if(value != null && value.indexOf("=") != -1){
        			String[] val = value.split("=");
            		if(val == null || val.length < 1)continue;
                	realValues.put(val[0], val[1]);        			
        		}
        	}
        }
        
    	// 为确定一组条件是否选择部门，需要获取定义。
    	HttpSession session = request.getSession();
    	SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
    	String formid = request.getParameter("formid");
    	formid = formid==null?request.getParameter("appformId"):formid;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid));
		InfoPath_Inputtypedefine inputTypeDefine = null;
		if(fapp!=null){
			if(fapp.getDataDefine() != null && (fapp.getDataDefine() instanceof SeeyonDataDefine)){
				ISeeyonDataSource dataSource = ((SeeyonDataDefine)fapp.getDataDefine()).getDataSource();
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
		}
        
    	List<ICondition> conditions = conditionList.getConditionList();
        IProvider provider = conditionList.getProvider();
        String conditionstr = request.getParameter("condition");
        String[] columNames = request.getParameterValues("columName");
		if(conditionList.isHasUserCondition() && conditionstr != null && !"".equals(conditionstr.trim())){
	    	List<ICondition> tempConditions = null;
	    	try {
	    		tempConditions = ((ConditionListImpl)conditionList.copy()).getConditionList();
			} catch (SeeyonFormException e1) {
				tempConditions = new ArrayList<ICondition>();
				for(ICondition condition : conditions){
					tempConditions.add(condition);
				}
			}
	    	conditions.clear();
			int index = 0;
			DataColumImpl dataColumn = null;
			QueryUserConditionDefin userConditionDefin = null;
			//存放用户输入条件与字段对应关系，当同一用户输入条件对应多个字段时只取第一个字段的值，因为在界面中只生成了一相输入域
			Map<String,String> userConditions = new HashMap<String, String>();
			for(int i = 0; i < tempConditions.size(); i++){
				ICondition condition = tempConditions.get(i);
				conditions.add(condition);
				if(condition instanceof DataColumImpl){
					dataColumn = (DataColumImpl)condition;
				}
				if(condition instanceof QueryUserConditionDefin){
					userConditionDefin = (QueryUserConditionDefin)condition;
				}
				if(userConditionDefin != null && dataColumn != null){
					String inputName = userConditions.get(userConditionDefin.getParamName());
					boolean isGenDisplayCondition = false;
					if(inputName == null){
						isGenDisplayCondition = true;
						String fieldName = null;
						try {
							fieldName = dataColumn.getRun();
						} catch (SeeyonFormException e) {
							fieldName = dataColumn.getSys();
						}
				    	if(fieldName.indexOf(".") != -1){
				    		fieldName = fieldName.substring(fieldName.indexOf(".") + 1);    		
				    	}
				    	inputName = fieldName + ParseUserCondition.INPUT_SUFFIX + (index++);
				    	userConditions.put(userConditionDefin.getParamName(), inputName);
					}
					String inputvalues[] = request.getParameterValues(inputName);
	                if(inputvalues == null || inputvalues.length == 0){
	                	userConditionDefin.setValue("");
	                	//querydefine.setValue(ReportResultImpl.IS_NULL);
	                }else if(inputvalues.length == 1){
	                	if(Strings.isNotBlank(inputvalues[0])){
	                		userConditionDefin.setValue(inputvalues[0]);
	                	}else{
	                		userConditionDefin.setValue("");
	                	}
	                	String displayValue = request.getParameter(inputName + "_label");
	                	if(Strings.isBlank(displayValue)){
	                		displayValue = inputvalues[0];	                		
	                	}
	                	if(isGenDisplayCondition){
			            	genDisplayCondition(displayConditions,
									realValues, userConditionDefin.getTitle(), displayValue);
	                	}
	                }else if(inputvalues.length > 1){
	            		OperatorImpl operator = new OperatorImpl(provider);
	                	operator.setOperator(IOperator.C_iOperator_BracketLeft);
	                	conditions.add(conditions.size() - 3,operator);
	                	for(int j = 0; j < inputvalues.length; j ++){
	                		if(j ==0){
	                			userConditionDefin.setValue(inputvalues[j]);
	                			if(isGenDisplayCondition){
	                				genDisplayCondition(displayConditions,
											realValues, userConditionDefin.getTitle(), inputvalues[j]);	                				
	                			}
	                		}else{
		                		operator = new OperatorImpl(provider);
			                	operator.setOperator(IOperator.C_iOperator_Or);
			                	conditions.add(operator);
			                	
			                    DataColumImpl dataColumTemp = new DataColumImpl(provider);
			                    dataColumTemp.setColumName(dataColumn.getColumName());
			                    dataColumTemp.setValueType(IDataColum.C_iValueType_field);
			                    conditions.add(dataColumTemp);
			                    
			                	operator = new OperatorImpl(provider);
			                	operator.setOperator(IOperator.C_iOperator_Equal);
			                	conditions.add(operator);
			                	
			                	QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
			                	userDefin.setValue(inputvalues[j]);
			                	userDefin.setValueType(IValue.C_iValueType_Value);
			                	conditions.add(userDefin);
			                	if(isGenDisplayCondition){
			                		genDisplayCondition(displayConditions,
											realValues, userConditionDefin.getTitle(), inputvalues[j]);
			                	}
	                		}
	                	}
	            		operator = new OperatorImpl(provider);
	                	operator.setOperator(IOperator.C_iOperator_BracketRight);
	                	conditions.add(operator); 
	                }
	    			dataColumn = null;
	    			userConditionDefin = null;
				}
			}
        }else if(columNames != null){
            String[] bracketLefts = request.getParameterValues("bracketLeft");
            String[] comparisonOperators = request.getParameterValues("comparisonOperator");
            String[] logicOperators = request.getParameterValues("logicOperator");
            String[] bracketRights = request.getParameterValues("bracketRight");
            String[] inputFlags = request.getParameterValues("inputFlag");
            conditions.clear();
            int bracketLeftCount = 0;
            int bracketRightCount = 0;
            for(int i = 0; i < columNames.length; i++){
            	String columName = columNames[i];
                if(Strings.isBlank(columName)){
                	continue;
                }
                String bracketLeft = bracketLefts[i];
            	String comparisonOperator = comparisonOperators[i];
                String logicOperator = logicOperators[i];
                String bracketRight = bracketRights[i];
            	
                if(!Strings.isBlank(bracketLeft)){//有右括号
                	OperatorImpl operator = new OperatorImpl(provider);
                	operator.setOperator(Integer.parseInt(bracketLeft));
                	conditions.add(operator);
                	bracketLeftCount ++;
                }  
                
                DataColumImpl dataColumn = new DataColumImpl(provider);
                if(SeeyonFormPojo.C_sFieldNames.contains(columName)){
                	dataColumn.setSys(columName);
                    dataColumn.setValueType(IDataColum.C_iValueType_form);	
                }else{
                	dataColumn.setColumName(columName);
                    dataColumn.setValueType(IDataColum.C_iValueType_field);
                }
				String fieldName;
				try {
					fieldName = dataColumn.getRun();
				} catch (SeeyonFormException e) {
					fieldName = dataColumn.getSys();
				}
		    	if(fieldName.indexOf(".") != -1){
		    		fieldName = fieldName.substring(fieldName.indexOf(".") + 1);    		
		    	}
		    	
		    	String inputName = fieldName + ParseUserCondition.INPUT_SUFFIX;
				String inputvalues[] = request.getParameterValues(inputName + inputFlags[i]);
				if(isMyPlan){
					//我的查询统计
					if(inputvalues == null || inputvalues.length != 0){
						String inputValueStr = "";
						if(inputvalues.length > 1){
							for (int j = 0; j < inputvalues.length; j++) {
								inputValueStr += inputvalues[j];
								if(j != inputvalues.length - 1){
									inputValueStr += ",";
								}
							}
						}
						conditions.add(dataColumn);
	            		OperatorImpl operator = new OperatorImpl(provider);
	            		if(Strings.isBlank(comparisonOperator)){
	            			operator.setOperator(IOperator.C_iOperator_Equal);
	            		}else{
	            			operator.setOperator(Integer.parseInt(comparisonOperator));
	            		}
	            		conditions.add(operator);
	            		QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
	            		if(inputvalues == null || inputvalues.length == 0 || Strings.isBlank(inputvalues[0])){
	                		userDefin.setValue("");
	                	} else if(inputvalues.length == 1){
	                		userDefin.setValue(inputvalues[0]);
	                	} else {
	                		userDefin.setValue(inputValueStr);
	                	}
	            		userDefin.setValueType(IValue.C_iValueType_Value);
	                	conditions.add(userDefin);
					}
				} else {
					//暂进取消录入为空的用ReportResultImpl.IS_NULL的机制，以保证和用户输入条件的一致性
	                if(inputvalues == null || inputvalues.length == 0 || inputvalues.length == 1){
	                	QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
	                	if(inputvalues == null || inputvalues.length == 0 || Strings.isBlank(inputvalues[0])){
	                		userDefin.setValue("");
	                	}else{
                			userDefin.setValue(inputvalues[0]);
	                	}
	                	userDefin.setValueType(IValue.C_iValueType_Value);
	                	String displayValue = request.getParameter(inputName + "_label" + inputFlags[i]);
	                	if(Strings.isBlank(displayValue) && inputvalues != null){
	                		displayValue =  inputvalues[0];       		
	                	}
	                	if(displayValue != null){
	                		genDisplayCondition(displayConditions, realValues,columName, displayValue);                		
	                	}
	                	//由于创建时间作为查询条件只有一个值的可能，所以不需要在else中做处理
	                	if(SeeyonFormPojo.C_sFieldName_Start_date.equalsIgnoreCase(columName) && Strings.isNotBlank(inputvalues[0])) {
	                		//创建时间，特殊处理
	                		int comparisonOper = Integer.parseInt(comparisonOperator);
	                		Date firstTime = Datetimes.getTodayFirstTime(userDefin.getValue());
	                		Date lastTime = Datetimes.getTodayLastTime(userDefin.getValue());
	                		if(IOperator.C_iOperator_Equal == comparisonOper) {
	                			//eg:等于 start_date >= 2011-06-16 00:00 and start_date <= 2011-06-16 23:59
	                			conditions.add(dataColumn);
	                			OperatorImpl operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_BigAndEqual);
	                			conditions.add(operator);
	                			userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(firstTime));
	                			conditions.add(userDefin);
	                			operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_And);
	                			conditions.add(operator);
	                			conditions.add(dataColumn);
	                			operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_SmallAndEqual);
	                			conditions.add(operator);
	                			userDefin = new QueryUserConditionDefin();
	                			userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(lastTime));
	                			userDefin.setValueType(IValue.C_iValueType_Value);
	                			conditions.add(userDefin);
	                		} else if(IOperator.C_iOperator_notEqual == comparisonOper) {
	                			//eg: 不等于 start_date < 2011-06-16 00:00 or start_date > 2011-06-16 23:59
	                			conditions.add(dataColumn);
	                			OperatorImpl operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_Smallthan);
	                			conditions.add(operator);
	                			userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(firstTime));
	                			conditions.add(userDefin);
	                			operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_Or);
	                			conditions.add(operator);
	                			conditions.add(dataColumn);
	                			operator = new OperatorImpl(provider);
	                			operator.setOperator(IOperator.C_iOperator_Bigthan);
	                			conditions.add(operator);
	                			userDefin = new QueryUserConditionDefin();
	                			userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(lastTime));
	                			userDefin.setValueType(IValue.C_iValueType_Value);
	                			conditions.add(userDefin);
	                		} else {
	                			//eg:大于等于 start_date >=2011-06-16 00:00 , 大于 start_date > 2011-06-16 23:59 
	                			//ed:小于等于 start_date <=2011-06-16 23:59 , 小于 start_date < 2011-06-16 00:00
	                			conditions.add(dataColumn);
	                			OperatorImpl operator = new OperatorImpl(provider);
	                			operator.setOperator(comparisonOper);
	                			conditions.add(operator);
	                			if(IOperator.C_iOperator_BigAndEqual == comparisonOper || IOperator.C_iOperator_Smallthan == comparisonOper){
	                				userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(firstTime));
		                		} else if(IOperator.C_iOperator_SmallAndEqual == comparisonOper || IOperator.C_iOperator_Bigthan == comparisonOper){
		                			userDefin.setValue(Datetimes.formatDatetimeWithoutSecond(lastTime));
		                		}
	                			conditions.add(userDefin);
	                		}
	                	} else { // 时间判断结束
	                		int comparisonOper = IOperator.C_iOperator_Equal;
	                		if(Strings.isNotBlank(comparisonOperator)){
	                			comparisonOper = Integer.parseInt(comparisonOperator);
	                		}
	                		//userDefin.setValue(inputvalues[0]);
	                		boolean isDepartmentColumn = QueryDepartmentHelper.isDepartmentColumn(inputTypeDefine, dataColumn);
	        				List<Long> departmentList = null;
	        				if(isDepartmentColumn && inputvalues != null && inputvalues.length != 0)
	        					departmentList = QueryDepartmentHelper.getConditionDepartmentList(inputvalues[0], comparisonOper);
	        				boolean includeSubDepartment = departmentList!=null && departmentList.size()>0;
	        				
	        				if(includeSubDepartment){
	        					userDefin.setValue(inputvalues[0].split("\\|")[0]);
        	                	conditions.addAll(QueryDepartmentHelper.extractDepartmentCondition(dataColumn, comparisonOper, departmentList,provider)); 
                			}else{
			                    conditions.add(dataColumn);
			            		OperatorImpl operator = new OperatorImpl(provider);
			            		if(Strings.isBlank(comparisonOperator)){
			            			operator.setOperator(IOperator.C_iOperator_Equal);
			            		}else{
			            			operator.setOperator(Integer.parseInt(comparisonOperator));
			            		}
			            		conditions.add(operator);
			                	conditions.add(userDefin);
                			}
	                	}
	                }else{
	                	OperatorImpl operator = new OperatorImpl(provider);
	                	operator.setOperator(IOperator.C_iOperator_BracketLeft);
	                	conditions.add(operator); 
	                	conditions.add(dataColumn);
	                	for(int j = 0; j < inputvalues.length; j ++){
	                		if(j != 0){
	                			operator = new OperatorImpl(provider);
	    	                	operator.setOperator(IOperator.C_iOperator_Or);
	    	                	conditions.add(operator);	
	    	                	
	    	                    DataColumImpl dataColumTemp = new DataColumImpl(provider);
	    	                    dataColumTemp.setColumName(dataColumn.getColumName());
	    	                    dataColumTemp.setValueType(IDataColum.C_iValueType_field);
	    	                    conditions.add(dataColumTemp);
	                		}
		                    
		                	operator = new OperatorImpl(provider);
		                	operator.setOperator(IOperator.C_iOperator_Equal);
		                	conditions.add(operator);
		                	
		                	QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
		                	userDefin.setValue(inputvalues[j]);
		                	userDefin.setValueType(IValue.C_iValueType_Value);
		                	conditions.add(userDefin);
			            	genDisplayCondition(displayConditions, realValues,columName, inputvalues[j]);
	                	}
	            		operator = new OperatorImpl(provider);
	                	operator.setOperator(IOperator.C_iOperator_BracketRight);
	                	conditions.add(operator); 
	                }
				}
                if(!Strings.isBlank(bracketRight)){
                	OperatorImpl operator = new OperatorImpl(provider);
                	operator.setOperator(Integer.parseInt(bracketRight));
                	conditions.add(operator);
                	bracketRightCount++;
                }  
            	
                if(i != columNames.length -1){
                	OperatorImpl operator = new OperatorImpl(provider);
                	operator.setOperator(Integer.parseInt(logicOperator));
                	conditions.add(operator);            	
                }
            }
            if(bracketLeftCount != bracketRightCount && !isMyPlan){
            	throw new FormQueryException(FormQueryException.C_iFormQueryErrode_RightAndLeftBracketError);
            }
        }
        //拼装用户输入条件开始，这部分条件要加个括号
		if(conditions.size() > 0){
			OperatorImpl operator = new OperatorImpl(provider);
	    	operator.setOperator(IOperator.C_iOperator_BracketLeft);
	    	conditions.add(0,operator);        		
	    	
	    	operator = new OperatorImpl(provider);
	    	operator.setOperator(IOperator.C_iOperator_BracketRight);
	    	conditions.add(operator);			
		}
		
        //拼装查询设置条件
    	if(request.getParameter("noStatusRow") != null) return;
        String[] finishedflags = request.getParameterValues("finishedflag");
        String[] states = request.getParameterValues("state");
        String[] ratifyflags = request.getParameterValues("ratifyflag");
        if(finishedflags == null || finishedflags.length < 3){//当等于三项时是全选 ，不需要再拼装SQL
        	OperatorImpl operator = new OperatorImpl(provider);
        	operator.setOperator(IOperator.C_iOperator_And);
        	conditions.add(operator);
    		if(finishedflags != null){
            	operator = new OperatorImpl(provider);
            	operator.setOperator(IOperator.C_iOperator_BracketLeft);
            	conditions.add(operator);
    		}
    		
        	if(finishedflags != null && finishedflags.length > 0){
            	String finishedflagName = Constantform.getString4CurrentUser("formquery_sheetfinished.label");
            	displayConditions.put(finishedflagName, querySet(conditions, provider, "finishedflag", finishedflags,realValues));
        	}
        	
        	if(finishedflags != null){
    			operator = new OperatorImpl(provider);
            	operator.setOperator(IOperator.C_iOperator_BracketRight);
            	conditions.add(operator);
    		}
        }
        if(states == null || states.length < 4 || ratifyflags == null || ratifyflags.length < 3){
    		OperatorImpl operator = new OperatorImpl(provider);
        	operator.setOperator(IOperator.C_iOperator_And);
        	conditions.add(operator);
        	
        	String displayCondition = "";
        	String statusName = Constantform.getString4CurrentUser("form.query.sheetstatus.label");
        	
        	if(states != null || ratifyflags != null){
        		OperatorImpl operator_bracketLeft = new OperatorImpl(provider);
        		operator_bracketLeft.setOperator(IOperator.C_iOperator_BracketLeft);
            	conditions.add(operator_bracketLeft); 
        	}
        	
        	if(states != null && states.length > 0){
            	displayCondition += querySet(conditions, provider, "state" , states,realValues);
            	//displayConditions.put(statusName,querySet(conditions, provider, "state" , states,realValues));
            }
        	
        	if(states != null &&  states.length > 0 && ratifyflags != null && ratifyflags.length > 0){
	        	operator = new OperatorImpl(provider);
	        	operator.setOperator(IOperator.C_iOperator_Or);
	        	conditions.add(operator);
        	}
        	
        	if(ratifyflags != null && ratifyflags.length > 0){
        		operator = new OperatorImpl(provider);
        		operator.setOperator(IOperator.C_iOperator_BracketLeft);
            	conditions.add(operator);
            	
            	operator = new OperatorImpl(provider);
        		operator.setOperator(IOperator.C_iOperator_BracketLeft);
            	conditions.add(operator);
            	
        		//String statusName = Constantform.getString4CurrentUser("form.query.sheetstatus.label");
        		if(Strings.isNotBlank(displayCondition)){
        			displayCondition += ","; 
        		}
        		displayCondition += querySet(conditions, provider, "ratifyflag", ratifyflags, realValues);
        		
        		operator = new OperatorImpl(provider);
        		operator.setOperator(IOperator.C_iOperator_BracketRight);
            	conditions.add(operator);
            	
            	operator = new OperatorImpl(provider);
            	operator.setOperator(IOperator.C_iOperator_And);
            	conditions.add(operator);
            	
        		DataColumImpl dataColum = new DataColumImpl(provider);
                dataColum.setSys("state");
                dataColum.setValueType(IDataColum.C_iValueType_form);
                conditions.add(dataColum);	
                
                operator = new OperatorImpl(provider);
            	operator.setOperator(IOperator.C_iOperator_notEqual);
            	conditions.add(operator);
            	
            	QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
            	userDefin.setValue("0");
            	userDefin.setValueType(IValue.C_iValueType_Value);
            	conditions.add(userDefin);
        		
        		operator = new OperatorImpl(provider);
        		operator.setOperator(IOperator.C_iOperator_BracketRight);
            	conditions.add(operator); 
        	}
        	if(Strings.isNotBlank(displayCondition)){
        		displayConditions.put(statusName,displayCondition);
        	}
        	
        	if(states != null || ratifyflags != null){
        		OperatorImpl operator_bracketRight = new OperatorImpl(provider);
        		operator_bracketRight.setOperator(IOperator.C_iOperator_BracketRight);
            	conditions.add(operator_bracketRight); 
        	}
        }
	}
	private static void genDisplayCondition(Map<String, String> displayConditions,
			Map<String, String> realValues, String columName,String inputvalue) {
		if(Strings.isBlank(inputvalue)) return;
		String key = columName + "$" + inputvalue;
		String value = realValues.get(key);
		columName = OperHelper.noNamespace(columName);
		String display = displayConditions.get(columName);
		if(SeeyonFormPojo.C_sFieldNames.contains(columName)){
			columName = Constantform.getString4OtherKey(columName);
		}
		if(value == null)value = inputvalue;
		if(display == null){
			displayConditions.put(columName, value);
		}else{
			displayConditions.put(columName, display + "," + value);
		}
	}

	/**
	 *  获取字段定义类型
	 */
    private String getColumnType(SeeyonForm_ApplicationImpl fapp,String columName) {
		SeeyonDataDefine seeyon = (SeeyonDataDefine) fapp.getDataDefine();
		for (int a = 0; a < seeyon.getDataDefine().getTableLst().size(); a++) {
			FormTable ft = (FormTable) seeyon.getDataDefine().getTableLst().get(a);
			for (int b = 0; b < ft.getFieldLst().size(); b++) {
				FormField ffield = (FormField) ft.getFieldLst().get(b);
				if (columName.equals(ffield.getDisplay()))
					return ffield.getFieldtype();
			}
		}
		return IPagePublicParam.VARCHAR;
	}
	
	/**
	 *  将表单状态和流程状态封装到用户输入条件中 
	 *  此方法不应该为static，但统计中也需要用，待重构时再考虑
	 */
	public static String querySet(List<ICondition> conditions, IProvider provider,String columName, String[] states,Map<String,String> realValues) {
		StringBuilder sb = new StringBuilder();
//    	if(states.length > 1){
//    		OperatorImpl operator = new OperatorImpl(provider);
//        	operator.setOperator(IOperator.C_iOperator_BracketLeft);
//        	conditions.add(operator);        		
//    	}
    	for(int i = 0; i < states.length; i++){
    		if(i > 0){
    			OperatorImpl operator = new OperatorImpl(provider);
            	operator.setOperator(IOperator.C_iOperator_Or);
            	conditions.add(operator);
    		}
    		DataColumImpl dataColum = new DataColumImpl(provider);
            dataColum.setSys(columName);
            dataColum.setValueType(IDataColum.C_iValueType_form);
            conditions.add(dataColum);	
            
            OperatorImpl operator = new OperatorImpl(provider);
        	operator.setOperator(IOperator.C_iOperator_Equal);
        	conditions.add(operator);
        	
        	QueryUserConditionDefin userDefin = new QueryUserConditionDefin();
        	userDefin.setValue(states[i]);
        	userDefin.setValueType(IValue.C_iValueType_Value);
        	conditions.add(userDefin);
        	
        	sb.append("," + realValues.get(columName + ":" + states[i]));
    	}
//    	if(states.length > 1){
//    		OperatorImpl operator = new OperatorImpl(provider);
//        	operator.setOperator(IOperator.C_iOperator_BracketRight);
//        	conditions.add(operator);        		
//    	}
    	return sb.length() > 0 ? sb.substring(1) : sb.toString();
	}

	public ModelAndView hasSummaryId(HttpServletRequest request, HttpServletResponse response) throws Exception {
	//	String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		String masterId = request.getParameter("id");
		String[] strs = request.getParameter("showdetail").split("\\.");
		String formId = strs[0];
		String operationId = strs[1];
		String appName = request.getParameter("formname");
		String appid = request.getParameter("formid");
		SeeyonForm_ApplicationImpl fapp=(SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appid));  
	
		ColManager colManger = (ColManager)ApplicationContextHolder.getBean("colManager");
		HisColManager hisColManger = (HisColManager)ApplicationContextHolder.getBean("hisColManager");
		String summaryIds="";
		List<ColSummary> colsummarylist = null;
	    List<Long> summarylist = new ArrayList<Long>();
	    boolean newflowsign = false;
			colsummarylist = colManger.getSummaryIdByFormIdAndRecordId(fapp.getId(), null, Long.valueOf(masterId));
			if(colsummarylist == null){
				colsummarylist = hisColManger.getSummaryIdByFormIdAndRecordId(fapp.getId(), null, Long.valueOf(masterId));
			}
			if(colsummarylist != null){
				if(colsummarylist.size() >1){
					for(int i=0;i<colsummarylist.size();i++){
						//Long summaryId = (Long) colsummarylist.get(i);
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
			//summaryIds = summaryId.toString();
	//		校验子流程是否还存在
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
		    String summaryIdStr = request.getParameter("summaryId");
		    mav.addObject("summaryId", summaryIdStr);
		    mav.addObject("appid", appid);
		    mav.addObject("queryname", queryname);
		    mav.addObject("showdetail", showdetail);
		    return mav;
	}
		
	public ModelAndView collViewRelate(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    ModelAndView mav = new ModelAndView("form/formquery/collViewRelate");
	    String showdetail = request.getParameter("showdetail");
	    String appid = request.getParameter("appid");
	    String queryname = request.getParameter("queryname");
	    boolean stateflag = false;
	    boolean finishflag = false;
	    List stateidlist = new ArrayList();
	    List finishedlist = new ArrayList();
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appid));  
	    if(afapp.getQueryList().size() !=0){
	    	for(int i=0;i<afapp.getQueryList().size();i++){
	    		SeeyonQueryImpl iquery = (SeeyonQueryImpl)afapp.getQueryList().get(i);
	    		if(iquery.getQueryName().equals(queryname) && appid.equals(afapp.getId().toString())){  
	    			ConditionListImpl filter = (ConditionListImpl)iquery.getFilter();
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
	    
	    String summaryIdStr = request.getParameter("summaryId");
	    String[] summaryid = summaryIdStr.split("\\|");
	    List summaryidlist = new ArrayList();
		for(int i=0;i<summaryid.length;i++){
			summaryidlist.add(summaryid[i]);
		}
		List formdatastatelist = new ArrayList();
		HashMap formdatastatmap = new HashMap();
		//if(stateflag || finishflag){
			FormDaoManager formManger = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
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
	    
	    mav.addObject("relateFlowList", getIOperBase().pagenate(relateFlowList));
	    mav.addObject("showdetail", showdetail);
	    mav.addObject("appid", appid);
	    mav.addObject("queryname", queryname);
	    return mav;
	}
	public ModelAndView showRecordDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String summaryId = request.getParameter("summaryId");
		User user = CurrentUser.get();
		AffairManager affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
		HisAffairManager hisAffairManager = (HisAffairManager)ApplicationContextHolder.getBean("hisAffairManager");
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
		Long affairId = 0l;
		boolean isStoreFlag = false;
		String summaryTitle = "";
		try{
			affairId = affairManager.getByIdForForm(Long.valueOf(summaryId));
			if(affairId == 0L){
				affairId = hisAffairManager.getByIdForForm(Long.valueOf(summaryId));
				isStoreFlag = (affairId != 0L);
			}
		   if(affairId == 0)
				throw new DataDefineException(1,"您选择的协同已经被删除","您选择的协同已经被删除");
		  
		   ColManager colManger = (ColManager)ApplicationContextHolder.getBean("colManager");
		   HisColManager hisColManger = (HisColManager)ApplicationContextHolder.getBean("hisColManager");
		   
		   ColSummary col = null;
		   
		   if(isStoreFlag){
			   col = hisColManger.getColSummaryById(Long.valueOf(summaryId), false);
		   }
		   else{
			   col = colManger.getColSummaryById(Long.valueOf(summaryId), false);
		   }
		   if(col == null)
				throw new DataDefineException(1,"您选择的协同已经被删除","您选择的协同已经被删除");
		   
		   summaryTitle = col.getSubject();
		  
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
		
		if(Strings.isBlank(request.getParameter("queryname"))){
			log.warn("遗漏的查询名称参数，可能导致表单协同的关联文档无法查看。" + request.getQueryString());
		}
		//SECURITY 访问控制
		if(!SecurityCheck.hasFormQueryPermission(request, response, user, appid, request.getParameter("queryname"), summaryId)){
			return null;
		}
		
		return  new ModelAndView("form/formquery/showRecordDetail")
				.addObject("summaryId", summaryId)
				.addObject("affairId", affairId)
				.addObject("isDesign", true)
				.addObject("type", "form")
				.addObject("from", "send")
				.addObject("isQuote", "")
				.addObject("formId", formId)
				.addObject("operationId", operationId)
				.addObject("openLocation", "")
				.addObject("formNames", formNames)
				.addObject("summaryTitle", summaryTitle);
		
	}
	
	public ModelAndView judgeuerenum(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter();

		String enumid = java.net.URLDecoder.decode(request.getParameter("enumid"), "UTF-8");	
		
		boolean returnstr;
		if(!"".equals(enumid) && !"null".equals(enumid) && enumid !=null){
			Metadata queryMetadata = metadataManager.getMetadata(Long.parseLong(enumid));
			  if(queryMetadata ==null){
				  returnstr = true;
			      out.write(String.valueOf(returnstr));
			  }else{
			      returnstr = false;
			      out.write(String.valueOf(returnstr));
			  }		
		}	 
		return null;
	}
	
	
//	查询结果转协同
	public ModelAndView transmitSeeyon(HttpServletRequest request, HttpServletResponse response){
		String queryname = request.getParameter("queryname");
		String seeyonBody = request.getParameter("seeyonbody");
		ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
		Date date = new Date();
		mav = this.collaborationController.appToColl(queryname + " " + Datetimes.formatDate(date), Constants.EDITOR_TYPE_HTML, date, seeyonBody, null, false);
		return mav;
	}
	
	//查询结果导出excel
	public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String formid = request.getParameter("formid");
        String queryname = request.getParameter("queryname");
		if(formid == null || "".equals(formid.trim())){
			return null;
		}
		SeeyonDataDefine fdatadefine = null;
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid)); 
		if(afapp != null){
            fdatadefine = (SeeyonDataDefine)afapp.getDataDefine();
        }
        Map<String,FormField> formFieldMap = new HashMap<String,FormField>();
        if(fdatadefine != null){
            DataDefine dataDefine = fdatadefine.getDataDefine();
            if(dataDefine != null){
                List<FormTable> tableList = dataDefine.getTableLst();
                if(tableList != null){
                    for (FormTable formTable : tableList) {
                        List<FormField> formFieldList = formTable.getFieldLst();
                        if(formFieldList != null){
                            for (FormField formField : formFieldList) {
                                if(formFieldMap.get(formField.getDisplay())== null){
                                    formFieldMap.put(formField.getDisplay(), formField);
                                }
                            }
                        }
                    }
                }
            }
        }
		String formname = afapp.getAppName();
		Map<String,String> displayConditions = new LinkedHashMap<String,String>();
        QueryResultImpl resultData = null;
        try {
            if(counter.incrementAndGet() > 6 ){
                List<String> msgs = new ArrayList<String>();
                msgs.add("对不起，系统正忙，请稍后继续！");
                OperHelper.creatformmessage(request,response,msgs);
                return null;
            }
        	Pagination.setFirstResult(0);
        	Pagination.setMaxResults(65535);
        	resultData = getResultData(request,response,displayConditions, false);
			QueryHelper queryHelper = new QueryHelper();
			
	        if(resultData.getSchema().size() > 256){
				List<String> msgs = new ArrayList<String>();
				msgs.add("Excel的列数最大允许[256]，现导出的列数已经超出范围，请重新设置后再导出！");
				OperHelper.creatformmessage(request,response,msgs);
				return null;
			}
	        
	        if(resultData.getRecordCount() > 65534){
				List<String> msgs = new ArrayList<String>();
				msgs.add("Excel的行数最大允许[65535]，现导出的行数已经超出范围，请重新设置后再导出！");
				OperHelper.creatformmessage(request,response,msgs);
				return null;
			}
	        //对于显示设置列，来导出excel
	        String dataFieldStr = request.getParameter("dataField");
	        List<QueryColum> queryColums = new ArrayList<QueryColum>();
	        if(dataFieldStr != null && !"".equals(dataFieldStr)){
	        	String[] dataFields = dataFieldStr.split(",");
	        	for(String dataField : dataFields){
	        		String alias = dataField;
	        		int left = dataField.indexOf("(");
	        		int right = dataField.indexOf(")");
	        		if(left != -1 &&  right != -1){
	        			alias = dataField.substring(left + 1,right);
	        			dataField = dataField.substring(0,left);
	        		}
	        		QueryColum queryColum = new QueryColum();
	        		queryColum.setColumTitle(alias);
	        		queryColum.setDataAreaName(dataField);
	        		queryColums.add(queryColum);
	        	}
	        }
	        List<List<String>> datas = new ArrayList<List<String>>();
	        for(int i = 0; i < resultData.getRecordCount(); i++){ 
	        	List<String> row = new ArrayList<String>();
	        	IQueryRecord record = resultData.getRecord(i);
	        	for(int j = 0; j < queryColums.size(); j++){	
	        		QueryColum queryColum = queryColums.get(j);
			    	String dataAreaName = queryColum.getDataAreaName();
			    	if(dataAreaName != null){
			    		if(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_member_id)).equals(dataAreaName)){
							dataAreaName = SeeyonFormPojo.C_sFieldName_Start_member_id;
						} else if(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_date)).equals(dataAreaName)){
							dataAreaName = SeeyonFormPojo.C_sFieldName_Start_date;
						}
			    		if(!dataAreaName.startsWith(NAMESPACE) && !SeeyonFormPojo.C_sFieldNames.contains(dataAreaName)){
							dataAreaName = NAMESPACE + dataAreaName;
						}
			    		row.add(record.getValueByName(dataAreaName));
			    	}
	        	}
	        	datas.add(row); 
	        }
			fileToExcelManager.save(request,response,queryname,queryHelper
						.exportQueryForExcel(formname,queryname,queryColums,displayConditions,datas,formFieldMap));
			return null;
        } catch (DataDefineException e) {
        	log.error("统计结果导出excel出错", e);
            List<String> lst = new ArrayList<String>();
            lst.add(e.getToUserMsg());
            OperHelper.creatformmessage(request, response, lst);
            return null;
        } finally {
            counter.getAndDecrement();
            // 释放调用的数据库资源
            if (resultData != null) {
                resultData.unInit();
            }
        }
	}
}