package www.seeyon.com.v3x.form.controller;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.appform.manager.AppFormLockManager;
import www.seeyon.com.v3x.form.appform.manager.AppFormManager;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.OperatorImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.condition.inf.IDataColum;
import www.seeyon.com.v3x.form.base.condition.inf.IOperator;
import www.seeyon.com.v3x.form.base.condition.inf.IProvider;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.controller.query.QueryController;
import www.seeyon.com.v3x.form.controller.query.QueryHelper;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormLog;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Operation;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.bind.auth.FormAppAuth;
import www.seeyon.com.v3x.form.manager.define.bind.auth.OperationAuth;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.base.RelationInputField;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource.IDataArea;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.HrStaffInfoField;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.ParseUserCondition;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.ShowDetail;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.FormAppAuthResult;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.RecordAuth;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.form.FormLogManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.manager.trigger.EventTriggerForHistoryData;
import www.seeyon.com.v3x.form.utils.FormFlowHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;
import www.seeyon.com.v3x.form.utils.FormLogEnum.FormOperationTypeEnum;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColQuoteformRecord;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.LockObject;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColQuoteformRecordManger;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
/**
 * 
 * @author xgghen
 *
 */
@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
public class AppFormController extends BaseController{
	
	private final static Log log = LogFactory.getLog(AppFormController.class);
	
	public final static String INPUT_SUFFIX = "_input" ;
	
	
	
	private AppFormManager appFormManager = SeeyonForm_Runtime.getInstance().getAppFormManager() ;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private static final String resource_baseName = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
    private TempleteCategoryManager templeteCategoryManager;	
	private FileToExcelManager fileToExcelManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private ColQuoteformRecordManger colQuoteformRecordManger ;
	private FormLogManager formLogManager = (FormLogManager)SeeyonForm_Runtime.getInstance().getBean("formLogManager");;
	private ColManager colManager;
    private NewflowManager newflowManager;
    private OrgManager orgManager;

	public void setColQuoteformRecordManger(ColQuoteformRecordManger colQuoteformRecordManger) {
		this.colQuoteformRecordManger = colQuoteformRecordManger;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

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
	
	public void setColManager(ColManager colManager) {
        this.colManager = colManager;
    }
	
    public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }
    
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	/**
	 * 
	 */
	public ModelAndView index (HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null ;
	}
	
	public ModelAndView appFormFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("form/appform/appFormFrame");
	}
	/**
	 * 返回该登录用户能看到的所有的业务表单---只返回当前登录单位的
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAppForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get() ;
		ModelAndView modelAndView = new ModelAndView("form/appform/showappform") ;
		FormAppMain fam = new FormAppMain();
		fam.setFormType(Integer.parseInt(request.getParameter("formType")));
		List<FormAppMain> list = formDaoManager.queryAllData(fam) ;
		modelAndView.addObject("list", list) ;
		List<Long> formAppMainIds = getFormAppMainIds(list) ;
		List<FomObjaccess> 	fomObjaccessList = iOperBase.getFomObjaccessByAppIds(formAppMainIds) ;
		Map<String,List<FormAppAuth>> categoryAndFromMap = new HashMap<String,List<FormAppAuth>>();
		Map<String, Long> formAppAuthAccountIdMap = new HashMap<String, Long>();
		List<Long> _entIdsList = orgManager.getUserDomainIDs(user.getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
		if(list != null){
			for(FormAppMain formAppMain : list){
				if(formAppMain.getCategory() != null){	
					List<FormAppAuth> formAppAuthList = appFormManager.getAllHasRightFormAppAuth(formAppMain.getId(), user , fomObjaccessList, entIdsList) ;
					if(formAppAuthList != null){
						TempleteCategory templete = templeteCategoryManager.get(formAppMain.getCategory());
		                if(templete != null){
		                	String templeteName = templete.getName();
		                	if(categoryAndFromMap.get(templeteName) != null){
		                		categoryAndFromMap.get(templeteName).addAll(formAppAuthList);
		                	}else{
		                		categoryAndFromMap.put(templeteName,formAppAuthList);
		                	}
		                }
		                Long accountId = templete.getOrgAccountId();
		                for (FormAppAuth formAppAuth : formAppAuthList) {
		                	formAppAuthAccountIdMap.put(formAppAuth.getId(), accountId);
						}
					}										 		
				}
			}
		}
		Set<String> categoryNames = categoryAndFromMap.keySet();
		modelAndView.addObject("categoryNames", categoryNames);
		modelAndView.addObject("categoryAndFromMap", categoryAndFromMap);
		modelAndView.addObject("formAppAuthAccountIdMap", formAppAuthAccountIdMap);
		return modelAndView ;
	}
	
	private List<Long> getFormAppMainIds(List<FormAppMain> list){
		if(list == null || list.isEmpty()){
			return new ArrayList<Long>(0) ;
		}
		List<Long> domain = new ArrayList<Long>() ;
		for(FormAppMain formAppMain : list){
			if(formAppMain.getFormstart() == 0) continue;
			domain.add(formAppMain.getId());				
		}
		return domain ;
	}
	/**
	 * 某个业务表单框架
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAppFormFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/appform/showAppFormFrame") ;
		return mav;
	}
	
	/**
	 * 选择某个业务表单视图及授权权限
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectFormOperation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appformId = request.getParameter("appformId") ;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		ModelAndView mav = new ModelAndView("form/appform/formOperationSelect") ;
		if(fapp != null){
			Map<String,String> showDetails = new HashMap<String,String>();
			String showDetail = request.getParameter("showDetail");
			String[] detail = showDetail.split("\\|");
			for(String show : detail){
	            String[] form = show.split("\\.");
	            if(Strings.isNotBlank(form[0])){
	            	ISeeyonForm seeyonForm = fapp.findFromById(Long.valueOf(form[0]));
		            InfoPath_Operation operation = seeyonForm.findOperationById(Long.valueOf(form[1]));
		            showDetails.put(seeyonForm.getFormName() + "." + operation.getOperationName(), show);	            	
	            }
			}
			mav.addObject("showDetails", showDetails);
		}else{
			return null;
		}
		return mav;
	}
	
	/**
	 * 查看某个业务表单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAppFormData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("form/appform/showappformdata") ;
		String appformId = request.getParameter("appformId") ;
		String templeteId = request.getParameter("templeteId") ;
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId)){
			return null ;
		}
		FormAppAuth formAppAuth =  null;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		if(fapp != null){
			formAppAuth = fapp.findFormAppAuthById(templeteId);
		}
 		if(formAppAuth == null){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("history.back();");
        	out.println("</script>");
        	return null;
		}
     	
     	HttpSession session = request.getSession();
     	SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
     	
     	ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(appformId)); 
		
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
     	SeeyonQueryImpl query = (SeeyonQueryImpl)formAppAuth.getQuery();
     	
     	ParseUserCondition parseUserCondition = new ParseUserCondition();
		List<QueryColum> queryColumns = query.getQueryColumList();
		Map<String,String> queryColumnMap = new LinkedHashMap<String, String>();
		for(QueryColum queryColum : queryColumns){
			inputTypeDefine.field(queryColum.getDataAreaName());
			if(ParseUserCondition.isAllowQuery(inputTypeDefine,queryColum.getDataAreaName())){
				parseUserCondition.parseCustomCondition(queryColum,inputTypeDefine);
				queryColumnMap.put(queryColum.getDataAreaName(), Constantform.getString4OtherKey(queryColum.getColumTitle()));
			}
		}
		if(queryColumns != null && queryColumns.size() > 0){
			modelAndView.addObject("columnNames", parseUserCondition.getColumnNames());
			modelAndView.addObject("blankInput", parseUserCondition.getBlankInput());
			modelAndView.addObject("inputs", parseUserCondition.getInputs());
			modelAndView.addObject("inputsMap", parseUserCondition.getInputsMap());
		}
		
		List<Long> _entIdsList = orgManager.getUserDomainIDs(CurrentUser.get().getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
		
		List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, CurrentUser.get(), entIdsList);
		boolean allowExport = false;
		boolean allowQuery = false;
		boolean allowStat = false;
		boolean allowPrint = false;
		boolean allowLog = false;
		boolean allowDelete = false;
		boolean allowLock = false;
		for(OperationAuth auth : operationAuths){
			allowExport = allowExport || auth.isAllowexport();
			allowQuery = allowQuery || auth.isAllowquery();
			allowStat = allowStat || auth.isAllowstat();
			allowPrint = allowPrint || auth.isAllowprint();
			allowLog = allowLog || auth.isAllowlog();
			allowDelete = allowDelete || auth.isAllowdelete();
			allowLock = allowLock || auth.isAllowlock();
			modelAndView.addObject("browseShowDetail",auth.getBrowseShowDetail().getShowDetailStr());
			modelAndView.addObject("updateShowDetail",auth.getUpdateShowDetail().getShowDetailStr());
		}
		if(allowStat){
			List<ISeeyonReport> reportList = new ArrayList<ISeeyonReport>();
			for(ISeeyonReport report : afapp.getReportList()){
				if(iOperBase.checkAccess(CurrentUser.get(), Long.valueOf(appformId), report.getReportName(), IPagePublicParam.C_iObjecttype_Report)){
					reportList.add(report);
				}
			}
			if(reportList.size() > 0){
				modelAndView.addObject("reportList", reportList);
			}
		}
		
		
		if(allowQuery){
			List<ISeeyonQuery> queryList = new ArrayList<ISeeyonQuery>();
			for(ISeeyonQuery qu : afapp.getQueryList()){
				if(iOperBase.checkAccess(CurrentUser.get(), Long.valueOf(appformId), qu.getQueryName(), IPagePublicParam.C_iObjecttype_Query)){
					queryList.add(qu);
				}
			}
			if(queryList.size() > 0){
				modelAndView.addObject("queryList", queryList);
			}
		}
		modelAndView.addObject("addShowDetail",appFormManager.getAddShowDetail(operationAuths));
		modelAndView.addObject("allowExport",allowExport);
		modelAndView.addObject("allowPrint",allowPrint);
		modelAndView.addObject("allowLog",allowLog);
		modelAndView.addObject("allowDelete",allowDelete);
		modelAndView.addObject("allowLock",allowLock);
		modelAndView.addObject("queryColumnMap", queryColumnMap);
		modelAndView.addObject("formAppAuthName", formAppAuth.getName()) ;
		return modelAndView ;
	}
	
	/**
	 * 某个业务表单查询结果
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView appFormQueryResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/appform/appFormQueryResult");
		String appformId = request.getParameter("appformId") ;
		String templeteId = request.getParameter("templeteId") ;
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId)){
			return null ;
		}
		FormAppAuth formAppAuth =  null;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		if(fapp != null){
			formAppAuth = fapp.findFormAppAuthById(templeteId);
		}
		if(formAppAuth == null){
			PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("history.back();");
        	out.println("</script>");
        	return null;
		}
		FormAppAuthResult authResult = new FormAppAuthResult();
		List<Long> _entIdsList = orgManager.getUserDomainIDs(CurrentUser.get().getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
		try{
			List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, CurrentUser.get(),entIdsList);
			if(operationAuths==null || operationAuths.size()==0){
				PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
	        	out.println("alert('" + message + "');");
	        	out.println("if(parent.parent.parent.treeFrame) parent.parent.parent.treeFrame.location.href=parent.parent.parent.treeFrame.location.href;");
	        	out.println("</script>");
	        	return null;
			}
			ConditionListImpl condition = getQueryCondition(request, response, formAppAuth, fapp);
			authResult.genAuthRestul(operationAuths,condition);
		} catch (SeeyonFormException e){
			 List<String> lst = new ArrayList<String>();
	         lst.add(e.getToUserMsg());
	         OperHelper.creatformmessage(request, response, lst);
	         return new ModelAndView("form/formquery/showQueryResultException");
		} 
		int size = authResult.getDataColumList().size();
		float colwidth = (float) 10;
		if(size>0 && size<10){
			colwidth = 94f/(float)size;
		}
		
		mav.addObject("ids", authResult.getRecordIds());
		mav.addObject("resultDatas", authResult.getRecords());
		mav.addObject("dataColumList", authResult.getDataColumList());
		mav.addObject("colwidth", colwidth);
		mav.addObject("formType", fapp.getFormType());
		mav.addObject("formAppAuthName", formAppAuth.getName()) ;
		return mav;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addAppFormData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get() ;
		ModelAndView modelAndView = new ModelAndView("form/appform/addappformdata");
		String appformId = request.getParameter("appformId");
		String templeteId = request.getParameter("templeteId");
		String masterId = request.getParameter("masterId");
		String showDetails = request.getParameter("showDetail");
		String[] showDetail = showDetails.split("\\|")[0].split("\\.");
		if(Strings.isBlank(appformId)|| Strings.isBlank(templeteId) 
				||Strings.isBlank(showDetail[0]) || Strings.isBlank(showDetail[1])){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("window.close();");
        	out.println("</script>");
        	return null;
		}
		boolean hasEdit = FormHelper.hasEditType(appformId, showDetail[0] , showDetail[1]);
		if(hasEdit && Strings.isNotBlank(masterId)){
			if(!appFormManager.hasDataById(appformId,masterId)){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.notexists" );
				out.println("alert('" + message + "');");
				out.println("window.close();");
				out.println("</script>");
				return null;	
			}
			LockObject lockObject = AppFormLockManager.addLock(Long.valueOf(masterId), Long.valueOf(appformId), user.getId(), user.getLoginName(), user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime()) ;
			if(lockObject!=null && !user.getLoginName().equals(lockObject.getLoginName())){
	    		PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	
	        	String message = ResourceBundleUtil.getString(resource_baseName, "form.appfrom.data.islocked",Functions.getMember(lockObject.getOwner()).getName());
	        	out.println("alert('" + message + "');");
	        	out.println("window.close();");
	        	out.println("</script>");
	        	return null;
			}
		}
		if(Strings.isNotBlank(masterId)){
			modelAndView.addObject("attachments", attachmentManager.getByReference(Long.valueOf(masterId))); 
		}
		String operationId = FormHelper.processOperationId(showDetails);
		String content = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), appformId, showDetail[0], operationId , masterId,false) ;
		modelAndView.addObject("content", content) ;
		return modelAndView ;
	}
	
	public ModelAndView viewFormData(HttpServletRequest request,HttpServletResponse response) throws Exception{
		User user = CurrentUser.get() ;
		ModelAndView modelAndView = new ModelAndView("form/appform/viewFormDetail") ;
		String isContent = request.getParameter("isContent");
		boolean isPrint = false;
		if(Strings.isNotBlank(request.getParameter("isPrint"))){
			isPrint=Boolean.parseBoolean(request.getParameter("isPrint"));
		}
		if(Strings.isNotBlank(isContent)){
			modelAndView = new ModelAndView("form/appform/viewFormContent");
		}
		String appformId = request.getParameter("appformId") ;
		String masterId = request.getParameter("masterId");
		String showDetail = request.getParameter("showDetail");
		String showPage = request.getParameter("showPage");
		if(Strings.isBlank(showDetail) || Strings.isBlank(appformId)){
			PrintWriter out = response.getWriter();
			out.println("<script>");
			String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
			out.println("alert('" + message + "');");
			out.println("window.close();");
			out.println("</script>");
			return null;
		}
		if(!appFormManager.hasDataById(appformId,masterId)){
			PrintWriter out = response.getWriter();
			out.println("<script>");
			String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.notexists" );
			out.println("alert('" + message + "');");
			out.println("window.close();");
			out.println("</script>");
			return null;	
		}
		String formId = null;
		String opertaionId = null;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		if(fapp != null){
			Map<String,String> forms = new LinkedHashMap<String,String>();
			String[] detail = showDetail.split("\\|");
			for(int i = 0; i < detail.length; i++){
	            String[] form = detail[i].split("\\.");
	            if(Strings.isNotBlank(form[0])){
	            	ISeeyonForm seeyonForm = fapp.findFromById(Long.valueOf(form[0]));
		            InfoPath_Operation operation = seeyonForm.findOperationById(Long.valueOf(form[1]));
		            forms.put(detail[i],seeyonForm.getFormName() + "(" + operation.getOperationName()+ ")");	            	
		            if(showPage == null && formId == null){
		            	formId = form[0];
		            	opertaionId = form[1];
		            	showPage = detail[i];
		            }
	            }
			}
			modelAndView.addObject("forms", forms);
		}
		if(showPage != null){
			String[] form = showPage.split("\\.");
			formId = form[0];
        	opertaionId = form[1];
		}
		if(Strings.isNotBlank(masterId)){
			modelAndView.addObject("attachments", attachmentManager.getByReference(Long.valueOf(masterId))); 
		}
		
		if(Strings.isNotBlank(isContent)){
			String content = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), appformId, formId, opertaionId, masterId,true) ;
			modelAndView.addObject("content", content);
		}
		modelAndView.addObject("isPrint", isPrint);
		modelAndView.addObject("showPage", showPage);
		modelAndView.addObject("masterId", masterId);
		return modelAndView ;
	}
	
	public ModelAndView saveAppFormData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get() ;
		String formData = request.getParameter("formData") ;
		String lock = request.getParameter("lock") ;
		String appformId = request.getParameter("appformId") ;
		String templeteId = request.getParameter("templeteId") ;
		String showDetailStr = request.getParameter("showDetail") ;
		String saveAndContinue = request.getParameter("continue");
		if(Strings.isBlank(appformId)|| Strings.isBlank(templeteId) || Strings.isBlank(showDetailStr)
				||Strings.isBlank(showDetailStr.split("\\|")[0].split("\\.")[0])|| Strings.isBlank(showDetailStr.split("\\|")[0].split("\\.")[1])){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("window.close();");
        	out.println("</script>");
        	return null;
		}
		String[] showDetail = showDetailStr.split("\\|")[0].split("\\.");
		String masterId = request.getParameter("masterId") ;
		String operationType = null ;
		String flag = "send" ;
		if(Strings.isNotBlank(masterId) ){
			operationType = "update";
			flag = null;
		}
		formData = FormHelper.setFormAppendValue(user.getId(), user.getName(), user.getLoginName(), formData, request.getParameter("formDisplayValue"), appformId, showDetail[0], showDetail[1], masterId);
		
		//记录无流程表单内容
		Map<String,String> newMainMap = new HashMap<String,String>();
		Map<String,List<Map<String,String>>>  newSonMap = new HashMap<String,List<Map<String,String>>>();
		Map<String,String> oldMainMap = new HashMap<String,String>();
		Map<String, List<Map<String, String>>>  oldSonMap = new HashMap<String, List<Map<String, String>>>();
		formLogManager.transXml2Object(formData, newMainMap, newSonMap, appformId);
		if(Strings.isNotBlank(masterId)){
			formLogManager.loadFormPojoById(Long.valueOf(masterId), Long.valueOf(appformId), oldMainMap, oldSonMap);
		}
		
		Long id = null ;
		try{
			if(Strings.isNotBlank(lock) && "true".equals(lock)){
				id = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), appformId, showDetail[0], showDetail[1], masterId, formData, "submit", AppFormManager.lockStateString, operationType, flag,"");
			}else{
				id = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), appformId, showDetail[0], showDetail[1], masterId, formData, "submit", AppFormManager.unlockStateString, operationType,flag,"");
			}
		} catch (DataDefineException e){
			if(e.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
				PrintWriter out = response.getWriter();
              	out.println("<script>");
                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\"); ");
              	out.println("try{parent.enableButtons();}catch(e){}"); 
              	out.println("</script>");
              	return null;
			} else {
    			  log.error("保存表单数据时发生错误", e);
      			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
      		  }
		}
		if( id == -1 ){
			PrintWriter out = response.getWriter();
	    	out.println("<script>");
	    	out.println("parent.cancontinue('false')");
	    	String message = ResourceBundleUtil.getString(resource_baseName, "FormFlowNo.uniqueError");
	    	out.println("alert('" + message + "');");
	    	out.println("window.close();");
	    	out.println("</script>");
	    	return  null;
		}
		
		List<Attachment> oldAtts = null;
		List<Attachment> newAtts = null;
		if(Strings.isNotBlank(masterId)){ 
			oldAtts = attachmentManager.getByReference(id);
			attachmentManager.deleteByReference(Long.valueOf(masterId)) ;
			id = Long.valueOf(masterId);
		}
		
		colQuoteformRecordManger.create(request, id, Long.valueOf(appformId), id);
		attachmentManager.create(ApplicationCategoryEnum.form, id, id, request) ;
		newAtts = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.form, id, id, request);
		
		//记录无流程表单日志记录
		FormOperationTypeEnum  formLogTypeEnum = null;
		String recordId = null;
		if(Strings.isBlank(masterId)){
			formLogTypeEnum = FormOperationTypeEnum.insert;
			recordId = String.valueOf(id);
		}else{
			formLogTypeEnum = FormOperationTypeEnum.modify;
			recordId = masterId;
		}
		formLogManager.recordLogForInsertAndUpdate(user.getId(), user.getName(), user.getLoginName(),appformId,recordId,formLogTypeEnum,newMainMap,newSonMap, oldMainMap, oldSonMap,newAtts, oldAtts,null);
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
			.getInstance().getAppManager().findById(Long.parseLong(appformId));
		log.info(user.getName() + "," + user.getLoginName() + "对表单[" + fapp.getAppName() + "]" + (operationType == null ? "新增" : "修改") + "ID为" + id + "的记录！");
		
		
		if(Strings.isBlank(saveAndContinue)){
			PrintWriter out = response.getWriter();
	    	out.println("<script>");
	    	out.println("window.returnValue = 'rv';window.close();");
	    	out.println("</script>");
		}else{
			PrintWriter out = response.getWriter();
	    	out.println("<script>");
	    	out.println("parent.cancontinue('TRUE','"+appformId+"','"+templeteId+"','"+showDetailStr+"');");
	    	out.println("</script>");
		}
		FormFlowHelper.onFormTiggerEvent(Long.parseLong(appformId),id);
		return null ;
	}
	
	
	public ModelAndView updateLockState(HttpServletRequest request,HttpServletResponse response) throws Exception {
		User user = CurrentUser.get() ;
		String masterId = request.getParameter("masterId") ;
		String appformId = request.getParameter("appformId") ;
		String templeteId = request.getParameter("templeteId");
		String lockState = request.getParameter("lockState") ;
		/*String showDetails = request.getParameter("showDetail");
		String[] showDetail = showDetails.split("\\|")[0].split("\\.");
		if(Strings.isBlank(masterId) || Strings.isBlank(appformId)|| Strings.isBlank(templeteId) || Strings.isBlank(showDetail[0])|| Strings.isBlank(showDetail[1])){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("parent.parent.listFrame.location.reload();") ;
        	out.println("</script>");
        	return null;
		}*/
		String[] ids = null ;
		if(Strings.isNotBlank(masterId)){
			ids = masterId.split(",") ;
			if(ids != null){
				for(String id : ids){
					LockObject lockObject = AppFormLockManager.addLock(Long.valueOf(id), Long.valueOf(appformId), user.getId(), user.getLoginName(), user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime()) ;
					if(lockObject!=null && !user.getLoginName().equals(lockObject.getLoginName())){
			    		PrintWriter out = response.getWriter();
			        	out.println("<script>");
			        	String message = ResourceBundleUtil.getString(resource_baseName, "form.appfrom.lock.locked");
			        	out.println("alert('" + message + "');");
			        	out.println("parent.parent.listFrame.location.reload();") ;
			        	out.println("</script>");
			        	return null;
					}else{
						appformDelLock(appformId,id);
					}
				}
			}
			appFormManager.updateLockState(appformId, templeteId,user,masterId,lockState);	
			
			//记录日志
			FormOperationTypeEnum formLogTypeEnum = null;
			if(AppFormManager.lockStateString.equals(lockState)){
				formLogTypeEnum = FormOperationTypeEnum.lock;
			}else{
				formLogTypeEnum = FormOperationTypeEnum.unlock;
			}
			formLogManager.recordLogforLockAndDelete(appformId, masterId, formLogTypeEnum);
		}
		
		PrintWriter out = response.getWriter();
		
		out.println("<script>");
		out.println("parent.reQuery('"+appformId+"','"+templeteId+"');");
		out.println("</script>");
		
		return null;
	}
	
	public ModelAndView deleteData(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String masterId = request.getParameter("masterId") ;
		String appformId = request.getParameter("appformId") ;
		String templeteId = request.getParameter("templeteId") ;
		
		String[] ids = null ;
		User user = CurrentUser.get() ;
		if(Strings.isNotBlank(masterId)){
			ids = masterId.split(",") ;
			if(ids != null){
				for(String id : ids){
					LockObject lockObject = AppFormLockManager.addLock(Long.valueOf(id), Long.valueOf(appformId), user.getId(), user.getLoginName(), user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime()) ;
					if(lockObject!=null && !user.getLoginName().equals(lockObject.getLoginName())){
			    		PrintWriter out = response.getWriter();
			        	out.println("<script>");
			        	String message = ResourceBundleUtil.getString(resource_baseName, "form.appfrom.delete.locked");
			        	out.println("alert('" + message + "');");
			        	out.println("parent.parent.listFrame.location.reload();") ;
			        	out.println("</script>");
			        	return null;
					}else{
						appformDelLock(appformId,id);
					}
				}
			}
		}
		
		if(Strings.isNotBlank(masterId)){
			ids = masterId.split(",") ;
			if(ids != null){
				for(String id : ids){
					attachmentManager.deleteByReference(Long.valueOf(id)) ;
				}
			}
		}
		 //记录日志
	    formLogManager.recordLogforLockAndDelete(appformId,masterId, FormOperationTypeEnum.delete);
	    
		appFormManager.delDataById(appformId, masterId);
		//删除表单数据时，同时撤销调度
		EventTriggerForHistoryData.removeTriggerForHistoryData(Long.valueOf(appformId), masterId);
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
		.getInstance().getAppManager().findById(Long.parseLong(appformId));
	    log.info(user.getName() + "," + user.getLoginName() + "对表单[" + fapp.getAppName() + "]删除" + "ID为" + ids + "的记录！");
	    
		PrintWriter out = response.getWriter() ;
		
		out.println("<script>") ;
		out.println("parent.reQuery('"+appformId+"','"+templeteId+"');");
		out.println("</script>") ;
		
		return null ;
	}
	
	/**
	 * 导出excel
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportExcel(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String appformId = request.getParameter("appformId");
        String templeteId = request.getParameter("templeteId");
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId)){
			return null;
		}
		FormAppAuth formAppAuth =  null;
		SeeyonDataDefine fdatadefine = null;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		if(fapp != null){
			formAppAuth = fapp.findFormAppAuthById(templeteId);
			fdatadefine = (SeeyonDataDefine)fapp.getDataDefine();
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
		if(formAppAuth == null){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("parent.parent.listFrame.location.reload();") ;
        	out.println("</script>");
        	return null;
		}
		FormAppAuthResult authResult = new FormAppAuthResult();
		List<Long> _entIdsList = orgManager.getUserDomainIDs(CurrentUser.get().getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
		try{
			List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, CurrentUser.get(), entIdsList);
			ConditionListImpl condition = getQueryCondition(request, response, formAppAuth, fapp);
			authResult.genAuthRestul(operationAuths,condition);
			List<List<String>> datas = new ArrayList<List<String>>();
			List<RecordAuth> resultList = authResult.getRecords();
			for(RecordAuth record : resultList){
				datas.add(record.getRowData());
			}
			
			QueryHelper queryHelper = new QueryHelper();
			String fileName = formAppAuth.getName();
			if(authResult.getDataColumList().size() > 255){
			    fileName +=fileName+".xlsx";
			}
        	fileToExcelManager.save(request,response,fileName,queryHelper.exportQueryForExcel(fapp.getAppName(), formAppAuth.getName(), authResult.getDataColumList(), new HashMap<String,String>(), datas,formFieldMap)) ;
           
        	//记录无流程导出日志
    		formLogManager.recordLogForExportExcel(appformId,authResult.getDataColumList(),datas);
		} catch (DataDefineException e) {
        	log.error("导出excel出错", e);
            List<String> lst = new ArrayList<String>();
            lst.add(e.getToUserMsg());
            OperHelper.creatformmessage(request, response, lst);
            return null;
        } 

		return null;
	}
	
	public  ModelAndView getImportExcelModel(HttpServletRequest request,HttpServletResponse response) throws Exception {
		User user = CurrentUser.get() ;
        String appformId = request.getParameter("appformId");
        String templeteId = request.getParameter("templeteId");
        String[] showDetail = request.getParameter("showDetail").split("\\|")[0].split("\\.") ; 
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId) 
				|| Strings.isBlank(showDetail[0]) || Strings.isBlank(showDetail[1])){
			return null;
		}
		List<String> list =  appFormManager.getImportExcelFieldName(appformId, templeteId, showDetail[0], showDetail[1], user) ;
		if(list == null){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("parent.parent.listFrame.location.reload();") ;
        	out.println("</script>");
        	return null;
			
		}
		
		DataRecord record = new DataRecord();
		String[] strTitle = new String[list.size()];
		for(int i = 0 ; i < list.size() ;i++){
			strTitle[i] = list.get(i) ;
		}
		record.setTitle("导入模板");
		record.setColumnName(strTitle) ;
		record.setSheetName("导入模板") ;
		String fileName = null;
		if(list.size() >255){
		    fileName = "导入模板.xlsx";
		}else{
		    fileName = "导入模板.xls";
		}
		fileToExcelManager.save(request,response,fileName,record) ;
		
		return null ;
	}
	public ModelAndView importExcel(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get() ;
        String appformId = request.getParameter("appformId");
        String templeteId = request.getParameter("templeteId");
        String[] showDetail = request.getParameter("showDetail").split("\\|")[0].split("\\.") ; 
        //判断是覆盖还是跳过。
        HttpSession session = request.getSession();
		String iscover = (String) session.getAttribute("repeat");
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId)
				||Strings.isBlank(showDetail[0]) || Strings.isBlank(showDetail[1])){
			return null;
		}
		FormAppAuth formAppAuth =  null;
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
		if(fapp != null){
			formAppAuth = fapp.findFormAppAuthById(templeteId);
		}
		if(formAppAuth == null || showDetail == null){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("parent.parent.listFrame.location.reload();") ;
        	out.println("</script>");
        	return null;
		}
		
		long id = UUIDLong.longUUID();
		attachmentManager.create(ApplicationCategoryEnum.form, id, id,request) ;
		
		List<Attachment> attachments = attachmentManager.getByReference(id) ;
		
		List<List<String>> excelList = null ;
		
		if(attachments != null){
			for(Attachment attachment : attachments){
				File file = fileManager.getFile(attachment.getFileUrl(),attachment.getCreatedate());
				excelList = fileToExcelManager.readExcel(file);
			}
		}
		
		if(excelList == null){
        	log.error("解析excel出现问题！") ;
            List<String> lst = new ArrayList<String>() ;
            lst.add("解析excel出现问题！") ;
            OperHelper.creatformmessage(request, response, lst) ;
            return  super.refreshWorkspace() ;
		}
		String messageData = null ;
		try{
			messageData = appFormManager.saveDataFromImportExcel(appformId,formAppAuth,showDetail[0],showDetail[1],user,excelList,iscover);
		}catch(Exception e){
        	log.error("保存excel出现问题！",e) ;
            List<String> lst = new ArrayList<String>() ;
            lst.add("导入excel出现问题！"+e) ;
            OperHelper.creatformmessage(request, response, lst) ;
            return super.refreshWorkspace() ;
		}
		
		if(Strings.isNotBlank(messageData)){
            List<String> lst = new ArrayList<String>() ;
            lst.add(messageData) ;
            OperHelper.creatformmessage(request, response, lst) ;
		}
		
		attachmentManager.deleteByReference(id) ;
		
		//记录无流程导入日志
		List<List<String>> importInsertExcelList = appFormManager.getImportInsertExcelList();
		List<List<String>> importUpdateExcelList = appFormManager.getImportUpdateExcelList();
		formLogManager.recordLogForImportExcel(appformId, formAppAuth,showDetail[0],showDetail[1],importInsertExcelList,importUpdateExcelList);
		
		PrintWriter out = response.getWriter();
    	out.println("<script>");
    	//String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
    	//out.println("alert('" + message + "');");
    	out.println("parent.parent.listFrame.location.reload();") ;
    	out.println("</script>");
    	return null;
	}
	
	public ModelAndView getappfromrefFrame(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String formId = request.getParameter("formId") ;
		ModelAndView mav = new ModelAndView("form/appform/apprefFormFrame");
		if(Strings.isBlank(formId)){
			return null ;
		}
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formId)) ;
		if(fapp == null){
			return null ;
		}
    	String fromQuery = "" ;
    	if(Strings.isNotBlank(request.getParameter("fromQuery"))){
    		fromQuery = request.getParameter("fromQuery") ;
    	} 
		mav.addObject("formId", formId) ;
		mav.addObject("fromQuery", fromQuery) ;
		mav.addObject("formappName", fapp.getAppName()) ;
		return mav ;
	}
	
	/**
	 * 无流程表单的关联选择列表的数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAppFormData(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String formId = request.getParameter("appformId") ;
		ModelAndView mav = new ModelAndView("form/appform/showrefappformdata");
		if(Strings.isBlank(formId)){
			return null ;
		}
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formId)) ;
		
		if(fapp == null){
			return null ;
		}
		User user = null;
		if(fapp.getFormType()!=ISeeyonForm.TAppBindType.BASEDATA.getValue())
			user = CurrentUser.get();
		List<Long> _entIdsList = orgManager.getUserDomainIDs(CurrentUser.get().getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
		List<FormAppAuth> list = appFormManager.getAllHasRightFormAppAuth(Long.valueOf(formId), user, entIdsList);
		
		if(list == null || list.size()==0){
			PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("history.back();");
        	out.println("</script>");
        	return null;
		}
		
		FormAppAuthResult authResult = new FormAppAuthResult();
		try{
		    List<OperationAuth> firstOperationAuths = new ArrayList<OperationAuth>();
		    ConditionListImpl condition = null;
		    List<ICondition> conditions = new ArrayList<ICondition>();
		    
		    OperationAuth firstOperationAuth = null;
			for(int i = 0; i < list.size(); i++){
				ConditionListImpl filter = null;
				FormAppAuth formAppAuth = list.get(i);
				List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, user, entIdsList);
				if(!operationAuths.isEmpty()){
					OperationAuth operationAuth = operationAuths.get(0).copy();
					if(i == 0){
						firstOperationAuth = operationAuth;
						condition = getQueryCondition(request, response, formAppAuth, fapp);
					}
					filter = (ConditionListImpl)operationAuth.getFilter();
					if(filter == null) continue;
					IProvider provider = filter.getProvider();
					List<ICondition> conditionList = filter.getConditionList();
					if(CollectionUtils.isNotEmpty(conditionList)){
						if(CollectionUtils.isNotEmpty(conditions)){
							OperatorImpl or = new OperatorImpl(provider);
							or.setOperator(IOperator.C_iOperator_Or);
							conditions.add(or);
						}
						OperatorImpl operator = new OperatorImpl(provider);
						operator.setOperator(IOperator.C_iOperator_BracketLeft);
						conditions.add(operator);
						conditions.addAll(conditionList);
						operator = new OperatorImpl(provider);
						operator.setOperator(IOperator.C_iOperator_BracketRight);
						conditions.add(operator);
					} else {
						conditions.clear();
						break;
					}
				}
			}
			ConditionListImpl filter = (ConditionListImpl)firstOperationAuth.getFilter();
			filter.getConditionList().clear();
			filter.getConditionList().addAll(conditions);
			firstOperationAuths.add(firstOperationAuth);
			authResult.genAuthRestul(firstOperationAuths,condition);
		} catch (SeeyonFormException e){
			 List<String> lst = new ArrayList<String>();
	         lst.add(e.getToUserMsg());
	         OperHelper.creatformmessage(request, response, lst);
	         return new ModelAndView("form/formquery/showQueryResultException");
		} 			
		
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		
		InfoPath_Inputtypedefine inputTypeDefine = null;
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
		ParseUserCondition parseUserCondition = new ParseUserCondition();
		Map<String,String> queryColumnMap = new LinkedHashMap<String, String>();
		
		List<QueryColum> queryColumns = authResult.getDataColumList();
		for(QueryColum queryColum : queryColumns){
			inputTypeDefine.field(queryColum.getDataAreaName());
			if(ParseUserCondition.isAllowQuery(inputTypeDefine,queryColum.getDataAreaName())){
				parseUserCondition.parseCustomCondition(queryColum,inputTypeDefine);
				queryColumnMap.put(queryColum.getDataAreaName(), Constantform.getString4OtherKey(queryColum.getColumTitle()));
			}
		}
		if(queryColumns != null && queryColumns.size() > 0){
			mav.addObject("inputsMap", parseUserCondition.getInputsMap());
		}
		String condition = request.getParameter("queryCondition");
		if(Strings.isNotBlank(condition)){
			inputTypeDefine.field(condition);
			String inputName = inputTypeDefine.getFieldname() + INPUT_SUFFIX;
			if(SeeyonFormPojo.C_sFieldNames.contains(condition)){
				inputName = condition + INPUT_SUFFIX;
			}
			String[] textfield = request.getParameterValues(inputName);
			mav.addObject("queryCondition", condition);
			mav.addObject("textfield", textfield);
			mav.addObject("inputName", inputName);
			mav.addObject("textfieldLabel", request.getParameter(inputName + "_label"));
		}
		
		int size = queryColumns.size();
		//BUG_AEIGHT-8157高露洁_表单关联无流程表单时，在弹出的选择框中，产品名很长，显示不全，要手动拉长
		//下面由于前端没有规律，简单找个小规律处理了一下
		float colwidth = 0f;
		if(size>0 && size<=3){
			colwidth = 95/(float)size;
		}else if(size>=4 && size<=10){
			colwidth = 30;
		}else{
			colwidth = size*2;
		}
		mav.addObject("queryColumnMap", queryColumnMap);
		mav.addObject("ids", authResult.getRecordIds());
		mav.addObject("dataColumList", queryColumns);
		mav.addObject("colwidth", colwidth);
		mav.addObject("resultDatas", authResult.getRecords());
		return mav;
	}

	
	public void appformDelLock(String _appformId ,String _masterId){
		User user = CurrentUser.get() ;
		if(Strings.isBlank(_appformId) || Strings.isBlank(_masterId)){
			return ;
		}
		AppFormLockManager.remove(Long.valueOf(_masterId), Long.valueOf(_appformId), user.getId());
	}
	
	public boolean isUnique(String appformId , String fieldName , String fieldValue,String reocrid) throws Exception{
		try {
			return appFormManager.isUnique(appformId, fieldName, fieldValue,reocrid) ;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean fieldInDBIsUnique(String appformId , String fieldName ) throws Exception{
		try {
			return appFormManager.fieldInDBIsUnique(appformId, fieldName) ;
		} catch (Exception e) {
			return false;
		}
	}
	private ConditionListImpl getQueryCondition(HttpServletRequest request,HttpServletResponse response, FormAppAuth formAppAuth, ISeeyonForm_Application afapp) throws SeeyonFormException {
		InfoPath_Inputtypedefine inputTypeDefine = null;
		ConditionListImpl conditionList = new ConditionListQueryImpl();
		SeeyonQueryImpl query = (SeeyonQueryImpl)formAppAuth.getQuery();
		Map<String,String> displayConditions = new LinkedHashMap<String,String>();
		conditionList = (ConditionListQueryImpl)query.getUserConditionList().copy();
		IProvider provider = conditionList.getProvider();
		List<ICondition> conditions = conditionList.getConditionList();
		if(afapp != null){
			inputTypeDefine = new InfoPath_Inputtypedefine(afapp);
			String condition = request.getParameter("queryCondition");
			if(Strings.isNotBlank(condition)){
				inputTypeDefine.field(condition);
				String fieldType = inputTypeDefine.getFieldtype();
				DataColumImpl dataColumn = new DataColumImpl(provider);
                if(SeeyonFormPojo.C_sFieldNames.contains(condition)){
                	dataColumn.setSys(condition);
                    dataColumn.setValueType(IDataColum.C_iValueType_form);
                }else{
                	dataColumn.setColumName(condition);
                    dataColumn.setValueType(IDataColum.C_iValueType_field);
                }
				conditions.add(dataColumn);
				QueryUserConditionDefin userConditionDefin = null;
				String inputName = inputTypeDefine.getFieldname() + INPUT_SUFFIX;
				if(SeeyonFormPojo.C_sFieldNames.contains(condition)){
					inputName = condition + INPUT_SUFFIX;
				}
				String[] textfields = request.getParameterValues(inputName);
				if(textfields!=null){
					for(int i = 0;i < textfields.length; i++){
						String textfield = textfields[i];
		                if(SeeyonFormPojo.C_sFieldName_Start_date.equalsIgnoreCase(condition) && Strings.isNotBlank(textfield)) {
		                	Date firstTime = Datetimes.getTodayFirstTime(textfield);
		            		Date lastTime = Datetimes.getTodayLastTime(textfield);
							OperatorImpl operator = new OperatorImpl(provider);
							operator.setOperator(IOperator.C_iOperator_BigAndEqual);
							conditions.add(operator);
							userConditionDefin = new QueryUserConditionDefin();
							userConditionDefin.setValue(Datetimes.formatDatetimeWithoutSecond(firstTime));
							conditions.add(userConditionDefin);
							operator = new OperatorImpl(provider);
							operator.setOperator(IOperator.C_iOperator_And);
							conditions.add(operator);
							conditions.add(dataColumn);
							operator = new OperatorImpl(provider);
							operator.setOperator(IOperator.C_iOperator_SmallAndEqual);
							conditions.add(operator);
							userConditionDefin = new QueryUserConditionDefin();
							userConditionDefin.setValue(Datetimes.formatDatetimeWithoutSecond(lastTime));
							conditions.add(userConditionDefin);
		                } else  {
		                	boolean isChoiceEle = false;
		                	String isChoiceEleStr = request.getParameter("isChoiceEle");
		                	if(Strings.isNotBlank(isChoiceEleStr)){
		                		isChoiceEle = Boolean.valueOf(isChoiceEleStr);
		                	}
		                	OperatorImpl operator = new OperatorImpl(provider);
							if(Strings.isNotBlank(fieldType) && (IPagePublicParam.DATETIME.equals(fieldType.toUpperCase()) ||
									"DATE".equals(fieldType.toUpperCase()) || 
									IPagePublicParam.TIMESTAMP.equals(fieldType.toUpperCase()) || isChoiceEle) ){
								operator.setOperator(IOperator.C_iOperator_Equal);
							} else {
								operator.setOperator(IOperator.C_iOperator_like);
							}
							userConditionDefin = new QueryUserConditionDefin();
							userConditionDefin.setValue(textfield);
							conditions.add(operator);
							conditions.add(userConditionDefin);
							if(i < textfields.length - 1){
								operator = new OperatorImpl(provider);
								operator.setOperator(IOperator.C_iOperator_Or);
								conditions.add(operator);
								conditions.add(dataColumn);
							}
		                }
					}
				}
			}
		}
		
    	QueryController.genConditionStr(request, displayConditions, conditionList,false);
    	return conditionList;
    }
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	 public String getFormViewOperation(Long summaryId, Long subRecordId, Long formAppId, String fieldName)throws Exception {
		String returnStr = null;
		ColSummary summary = colManager.getColSummaryById(summaryId, true);
		if(summary != null){
			Integer newflowType = summary.getNewflowType();
			if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){
				List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.child.ordinal());
			    if(runningList != null && !runningList.isEmpty()){
			        NewflowRunning running = runningList.get(0);
			        summaryId = running.getMainSummaryId();
			    }
			}
		}
		ColQuoteformRecordManger colQuoteformRecordManager=(ColQuoteformRecordManger)ApplicationContextHolder.getBean("colQuoteformRecordManger");
		ColQuoteformRecord quoteformRecord= null;
		if(subRecordId != null){
			quoteformRecord = colQuoteformRecordManager.getColQuoteformRecord(summaryId,subRecordId,fieldName);
		} else {
			quoteformRecord = colQuoteformRecordManager.getColQuoteformRecord(summaryId,fieldName);
		}
		if(quoteformRecord == null)
			return returnStr;
		Long recordId = quoteformRecord.getRefColSummaryId();
		Long memberId= quoteformRecord.getMemberId();
		returnStr= quoteformRecord.getRefColSummaryId().toString();
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId) ;
		if(fapp != null){
			User user = null;
			if(fapp.getFormType()!=ISeeyonForm.TAppBindType.BASEDATA.getValue()){
				user = new User();
				user.setId(memberId);
			}
			List<Long> _entIdsList = orgManager.getUserDomainIDs(memberId,V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
					V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
			HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
			
			List<FormAppAuth> list = appFormManager.getAllHasRightFormAppAuth(formAppId, user, entIdsList);
			if(list == null || list.size()==0){
	        	return null;
			}
			
			FormAppAuth formAppAuth = list.get(0);
			List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, user, entIdsList);
			if(operationAuths == null || operationAuths.size()==0){
	        	return null;
			} 			
			OperationAuth operationAuth = operationAuths.get(0);
			ShowDetail showDetailObj = operationAuth.getBrowseShowDetail();
			if(showDetailObj!=null){
				String showDetail = showDetailObj.getShowDetailStr();
				returnStr = "&masterId="+recordId+"&showDetail="+showDetail;
			}
		}
		return returnStr;			
	}
	public ModelAndView logManager(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("form/appform/showlog");
		String single = request.getParameter("single");
		String formType = request.getParameter("formType");
		String templeteId = request.getParameter("templeteId");
		List<Integer> formEnumKeyList = new ArrayList<Integer>();
		formEnumKeyList.add(FormOperationTypeEnum.insert.key());
		if(!"3".equals(formType)){
			formEnumKeyList.add(FormOperationTypeEnum.lock.key());
		}
		formEnumKeyList.add(FormOperationTypeEnum.modify.key());
		if(!"3".equals(formType)){
			formEnumKeyList.add(FormOperationTypeEnum.unlock.key());
		}
		formEnumKeyList.add(FormOperationTypeEnum.delete.key());
		formEnumKeyList.add(FormOperationTypeEnum.importExcel.key());
		formEnumKeyList.add(FormOperationTypeEnum.exportExcel.key());
		String pstartOperationTime = request.getParameter("startOperationTime");
		String pendOperationTime = request.getParameter("endOperationTime");
		Timestamp[] dates = this.getDateTime(pstartOperationTime, pendOperationTime);
		Timestamp startOperationTime = dates[0];
		if(startOperationTime == null){
			startOperationTime = new Timestamp(Datetimes.getFirstDayInMonth(new Date()).getTime());
		}
		Timestamp endOperationTime = dates[1];
		
		String appformId = request.getParameter("appformId");
		String masterId = request.getParameter("masterId");
		
		mav.addObject("formEnumKeyList", formEnumKeyList);
		mav.addObject("single", single);
		mav.addObject("startOperationTime",Datetimes.format(new Date(startOperationTime.getTime()), Datetimes.dateStyle));
		mav.addObject("endOperationTime",Datetimes.format(new Date(endOperationTime.getTime()), Datetimes.dateStyle));
		mav.addObject("appformId", appformId);
		mav.addObject("masterId", masterId);
		mav.addObject("templeteId", templeteId);
		mav.addObject("formType", formType);
		mav.addObject("isFirst", "true");
		return mav;
	}
	public ModelAndView logQuery(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/appform/showlogdetail");
		String single = request.getParameter("single");
		String isExportExcel = request.getParameter("isExportExcel");
		//判断是否为首次默认页面,主要用于区分操作时间
		String isFirst = request.getParameter("isFirst");
		
		String pstartOperationTime = request.getParameter("startOperationTime");
		String pendOperationTime = request.getParameter("endOperationTime");
		String pstartCreateTime = request.getParameter("startCreateTime");
		String pendCreateTime = request.getParameter("endCreateTime");
		Timestamp startOperationTime = null;
		Timestamp endOperationTime = null;
		Timestamp startCreateTime = null;
		Timestamp endCreateTime = null;
		if(Strings.isNotBlank(pstartCreateTime)){
			startCreateTime = new Timestamp(Datetimes.parseDatetime(pstartCreateTime).getTime());
		}
		if(Strings.isNotBlank(pendCreateTime)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(Datetimes.parseDatetime(pendCreateTime));
			calendar.add(Calendar.DATE, 1);
			endCreateTime = new Timestamp(calendar.getTimeInMillis());
			
		}
		if(Strings.isNotBlank(isFirst) && !"true".equals(isFirst)){
			if(Strings.isNotBlank(pstartOperationTime)){
				startOperationTime = new Timestamp(Datetimes.parseDatetime(pstartOperationTime).getTime());
			}
			if(Strings.isNotBlank(pendOperationTime)){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(Datetimes.parseDatetime(pendOperationTime));
				calendar.add(Calendar.DATE, 1);
				endOperationTime = new Timestamp(calendar.getTimeInMillis());
			}
		}else{
			Timestamp[] dates = this.getDateTime(pstartOperationTime, pendOperationTime);
			startOperationTime = dates[0];
			if(startOperationTime == null){
				startOperationTime = new Timestamp(Datetimes.getFirstDayInMonth(new Date()).getTime());
			}
			endOperationTime = dates[1];
		}
		
		String appformId = request.getParameter("appformId");
		String masterId = request.getParameter("masterId");
		String operators = request.getParameter("operators");
		String creators = request.getParameter("creators");
		String operationType = request.getParameter("operationType");
		String templeteId = request.getParameter("templeteId") ;
		String formType = request.getParameter("formType");
		
		FormAppAuth formAppAuth =  null;
		FormAppAuthResult authResult = new FormAppAuthResult();
		if(Strings.isBlank(appformId) || Strings.isBlank(templeteId)){
			PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
        	out.println("alert('" + message + "');");
        	out.println("history.back();");
        	out.println("</script>");
        	return null;
		}
		//查找有权限的表单的ID
		if("2".equals(formType)){
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(appformId)) ;
			if(fapp != null){
				formAppAuth = fapp.findFormAppAuthById(templeteId);
			}
			if(formAppAuth == null){
				PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
	        	out.println("alert('" + message + "');");
	        	out.println("history.back();");
	        	out.println("</script>");
	        	return null;
			}
			
			List<Long> _entIdsList = orgManager.getUserDomainIDs(CurrentUser.get().getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
					V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
			HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
			List<OperationAuth> operationAuths = appFormManager.getAllFormAppOperationAuth(formAppAuth, CurrentUser.get(), entIdsList);
			if(operationAuths==null || operationAuths.size()==0){
				PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright" );
	        	out.println("alert('" + message + "');");
	        	out.println("if(parent.parent.parent.treeFrame) parent.parent.parent.treeFrame.location.href=parent.parent.parent.treeFrame.location.href;");
	        	out.println("</script>");
	        	return null;
			}
			ConditionListImpl condition = getQueryCondition(request, response, formAppAuth, fapp);
			authResult.genAuthRestul(operationAuths,condition,false);
		}
		
		
		List<FormLog> formLogList = null;
		if(Strings.isNotBlank(isExportExcel) && "true".equals(isExportExcel)) {
			Pagination.withoutPagination(null);
			Pagination.setFirstResult(0);
			Pagination.setMaxResults(Integer.MAX_VALUE);
		}
		if(("2".equals(formType)&&authResult.getRecordIds().size()>0 ) || "3".equals(formType)){
			formLogList = formLogManager.queryLog(appformId,masterId,operators,creators, operationType,startOperationTime,endOperationTime,startCreateTime,endCreateTime, authResult.getRecordIds(),formType);
		}
		if(Strings.isNotBlank(isExportExcel) && "true".equals(isExportExcel)) {
			String resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
			Locale locale = LocaleContext.getLocale(request);
			String name = ResourceBundleUtil.getString(resource, locale, "form.log.label");
			String[] getColName = getColName(resource,locale,request,Boolean.valueOf(single)) ;
			List<String[]> detailStrings = getDetail(locale,formLogList,getColName.length,Boolean.valueOf(single)) ;
			fileToExcelManager.save(request, response, name, exportToExcel(name, detailStrings, getColName));	
			return null ;
		}
		
		mav.addObject("formLogList", formLogList);
		mav.addObject("single", single);
		mav.addObject("isFirst", isFirst);
		return mav;
	}
	private Timestamp [] getDateTime(String startTime,String endTime) {
		Timestamp [] dates = new Timestamp[2];
		if(Strings.isNotBlank(startTime)) {
			if(startTime.indexOf(" ")  > 0){
				dates[0] = new Timestamp(Datetimes.parseDatetime(startTime).getTime());
			}else{
				dates[0] = new Timestamp(Datetimes.parseDatetime(startTime + " 00:00:00").getTime());
			}
		}else{
			dates[0] = new Timestamp(Datetimes.getFirstDayInMonth(new Date()).getTime());
		}
		if(Strings.isNotBlank(endTime)){
			if(endTime.indexOf(" ")> 0){
				dates[1] = new Timestamp(Datetimes.parseDatetime(endTime).getTime());
			}else{
				dates[1] = new Timestamp(Datetimes.parseDatetime(endTime + " 23:59:59").getTime());
			}
		}else{
			dates[1] = new Timestamp(System.currentTimeMillis());
		}
		return dates;
	}
	private String[] getColName(String resource,Locale locale,HttpServletRequest request,boolean single) {
		String[] columnName = null ;		
		String operator = ResourceBundleUtil.getString(resource, locale, "form.log.operator");
		String operateType = ResourceBundleUtil.getString(resource, locale, "form.log.operationtype");
		String description = ResourceBundleUtil.getString(resource, locale, "form.log.description");
		String operateDate = ResourceBundleUtil.getString(resource, locale, "form.log.operationtime");
		String creator = ResourceBundleUtil.getString(resource, locale, "form.log.creator");
		String createDate = ResourceBundleUtil.getString(resource, locale, "form.log.createtime");
		if(single){
			columnName = new String[]{operator,operateType,description,operateDate} ;	
		}else{
			columnName = new String[]{operator,operateType,description,operateDate,creator,createDate} ;	
		}
		return columnName ;
	}
	private List<String[]> getDetail(Locale locale,List<FormLog> list ,int length,boolean single) throws Exception{
		String prexString = "yyyy-MM-dd HH:mm" ;
		SimpleDateFormat dateFormat = new SimpleDateFormat(prexString);
		List<String[]> returnObjects = new ArrayList<String[]>() ;
		String resource = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		if (list != null && !list.isEmpty()) {
			for (FormLog formLog : list) {
				String[] data = new String[length];
				data[0] = Functions.showMemberName(formLog.getOperator());
				data[1] = ResourceBundleUtil.getString(resource, locale,  "application."+formLog.getOperateType().toString()+".label");
				data[2] = formLog.getDescription();
				data[3] = dateFormat.format(formLog.getOperateDate());
				if(!single){
					data[4] = formLog.getCreator() == null?"":Functions.showMemberName(formLog.getCreator());
					data[5] = formLog.getCreateDate() == null?"":dateFormat.format(formLog.getCreateDate());		
				}
				returnObjects.add(data);
			}
		}
		return returnObjects ;
	}
	private DataRecord exportToExcel(String title,List<String[]> results,String[] columnName){
		DataRecord dataRecord = new DataRecord();
		dataRecord.setSheetName(title);
		dataRecord.setTitle(title);
		dataRecord.setColumnName(columnName);
		if(results != null && results.size() > 0) {
			DataRow[] datarow = new DataRow[results.size()];
			DataRow  row = null;
			int i = 0;
			for(String[] result : results) {
				row = new DataRow();
				for(String obj : result) {
					row.addDataCell(obj, DataCell.DATA_TYPE_TEXT);
				}
				datarow[i] = row;
				i++;
			}
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e);
			}
		}
		return dataRecord;
	}
	public boolean checkIsUniqueMarket(String tableNames, String fieldNames,String formid) throws Exception {
		SessionObject sessionobject = (SessionObject)WebUtil.getRequest().getSession(false).getAttribute("SessionObject");
		if(!sessionobject.isIsdatavalue())
			return true;
		//TODO新建的时候 多个重表为判断
		return appFormManager.toCheckIsUniqueMarket(sessionobject,tableNames, fieldNames, formid,null,null);
	}
	public boolean addFormDataCheckUnique(String formid,String[] addData,String recordId) throws Exception {
		String fieldNames="";
		String fieldArray[]=new String[addData.length];
		List list=new ArrayList();
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0;i<addData.length;i++){
			String temp=addData[i];
			String field[]=temp.split("[|]");
			fieldNames+=field[0]+",";
			fieldArray[i]=field[0];
			if(field.length<2){
				list.add("");
				map.put(field[0], "");
			}else{
				list.add(field[1]);
				map.put(field[0], field[1]);
			}
			
		}
		String tableNames="";
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formid)) ;
		SeeyonDataDefine fdefine = (SeeyonDataDefine)fapp.getDataDefine();
		for(int i=0;i<list.size();i++){
			IDataArea fdataArea = fdefine.getDataSource().findDataAreaByName((fieldArray[i]));
			if(i==list.size()-1){
				tableNames+=fdataArea.getDBTableName();
			}else{
				tableNames+=fdataArea.getDBTableName()+",";
			}
		}
		return appFormManager.toCheckIsUniqueMarket(null,tableNames, fieldNames, formid, map ,recordId );
	}
	
	public String checkIsDataLock(Long _appformId ,Long _masterId){
		try {
			Map<String, List<Map<String, String>>> map = FormHelper.loadFormPojoWithFieldById(_masterId, _appformId);
			if(map!=null && !map.isEmpty()){
				List<Map<String,String>> mainList = map.get("main");
				if(mainList!=null && !mainList.isEmpty()){
					Map<String,String> mainMap = mainList.get(0);
					if(AppFormManager.lockStateString.equals(mainMap.get("state")) || AppFormManager.lockStateString.equals(mainMap.get("STATE"))){
						return "1";
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "0";
	}
	
	public String getUniqueMarketListString(String formid) throws Exception {
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formid)) ;
		List list=fapp.getUniqueFieldList();
		String fields="";
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				if(i==list.size()-1){
					fields+=list.get(i);
				}else{
					fields+=list.get(i)+",";
				}
			}
			return fields;
		}else{
			return null;
		}
	}
	public String getFinalInputType(String formid ,String fieldName)throws Exception {
		if(Strings.isBlank(formid)){
			HrStaffInfoField hrField = FormHelper.getHrStaffInfoField();
			  Map<String,RelationInputField> allInitList = hrField.getAllInitList();
			  if(allInitList != null){
		    	  RelationInputField relationInputField = allInitList.get(fieldName);
		    	  String type="";
		    	  if(TFieldInputType.fitCheckBox.equals(relationInputField.gettFieldInputType())){
		    		  type = "checkbox";
		    	  }else if(TFieldInputType.fitRadio.equals(relationInputField.gettFieldInputType())){
		    		  type = "radio";
		    	  }else if(TFieldInputType.fitSelect.equals(relationInputField.gettFieldInputType())){
		    		  type = "select";
		    	  }else if(TFieldInputType.fitExtend.equals(relationInputField.gettFieldInputType())){
		    		  type = "extend";
		    	  }else if(TFieldInputType.fitLable.equals(relationInputField.gettFieldInputType())){
		    		  type = "label";
		    	  }else if(TFieldInputType.fitText.equals(relationInputField.gettFieldInputType())){
		    		  type = "text";
		    	  }else if(TFieldInputType.fitTextArea.equals(relationInputField.gettFieldInputType())){
		    		  type = "textarea";
		    	  }else if(TFieldInputType.fitComboedit.equals(relationInputField.gettFieldInputType())){
		    		  type = "comboedit";
		    	  }
		    	  return type+","+relationInputField.getTieldInputExtendClassName();
			  }
		}else{
			List<TableFieldDisplay> list=FormHelper.getTableFieldDisplayById(formid);
			for(TableFieldDisplay tableFieldDisplay:list){
				if(tableFieldDisplay.getName().equals(fieldName) ){
					if( tableFieldDisplay.getInputtype().equals("relation")){
						String[]relationInputType=FormHelper.getRelationInfo(list,tableFieldDisplay);
						tableFieldDisplay.setInputtype(relationInputType[1]);
						tableFieldDisplay.setExtend(relationInputType[4]);
					}
					return tableFieldDisplay.getInputtype() + ","+ tableFieldDisplay.getExtend()+","+tableFieldDisplay.getRefInputAtt();
				}
			}
		} 
		return  "NOT";
	}
	public boolean numberOfMainTableRecord(String appformId , String tableName){
		try {
			return appFormManager.getNumberOfMainTableRecord(appformId, tableName);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return true;
	}
}
