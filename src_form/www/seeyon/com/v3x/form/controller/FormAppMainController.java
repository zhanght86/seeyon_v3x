package www.seeyon.com.v3x.form.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.ValueImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.inputextend.InputExtend_ExampleDatetime;
import www.seeyon.com.v3x.form.base.inputextend.InputExtend_SelectColSummay;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputExtendManager;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputRelation;
import www.seeyon.com.v3x.form.base.viewevent.inf.IViewEventManager;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.FormAppAuthObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.InitFormObject;
import www.seeyon.com.v3x.form.controller.pageobject.Matchdata;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.controller.pageobject.TemplateObject;
import www.seeyon.com.v3x.form.controller.query.QueryObject;
import www.seeyon.com.v3x.form.controller.report.ReportObject;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormFlowid;
import www.seeyon.com.v3x.form.domain.FormOwnerList;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeField;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeParam;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeTask;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Enum;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FieldInput;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FormView;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_ViewBindEventBind;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_xsd;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_xsl;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.IIP_InputObject;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputOutwrite;
import www.seeyon.com.v3x.form.event.CollaborationEventTask;
import www.seeyon.com.v3x.form.listener.CollaborationFormBindEventListener;
import www.seeyon.com.v3x.form.manager.SeeyonFormAppManagerImpl;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.bind.SeeyonFormBindImpl;
import www.seeyon.com.v3x.form.manager.define.bind.auth.FormAppAuth;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.define.bind.flow.inf.IFlowTemplet;
import www.seeyon.com.v3x.form.manager.define.bind.inf.ISeeyonFormBind;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DisplayValue;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.base.RelationInputAtt;
import www.seeyon.com.v3x.form.manager.define.data.base.RelationInputField;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource.IDataArea;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TAppBindType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldDataType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TviewType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonInputExtend;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.HrStaffInfoField;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.RelationInputFieldInterface;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.RelationInputManager;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.QueryResultImpl;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.inf.IQueryResult.IQueryRecord;
import www.seeyon.com.v3x.form.manager.define.trigger.EventAction;
import www.seeyon.com.v3x.form.manager.define.trigger.EventCalculate;
import www.seeyon.com.v3x.form.manager.define.trigger.EventCondition;
import www.seeyon.com.v3x.form.manager.define.trigger.EventRelatedForm;
import www.seeyon.com.v3x.form.manager.define.trigger.EventValue;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.form.FormDaoManagerImpl;
import www.seeyon.com.v3x.form.manager.inf.IConditionList;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.manager.trigger.EventTriggerForHistoryData;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.CreateTableNumber;
import www.seeyon.com.v3x.form.utils.FormHelper;
import www.seeyon.com.v3x.form.utils.FormTempleteHelper;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;
import DBstep.iMsgServer2000;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.parameter.model.ParameterBean;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.main.phrase.CommonPhrase;
import com.seeyon.v3x.main.phrase.CommonPhraseManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.dee.manager.DEEManager;
import com.seeyon.v3x.plugin.dee.manager.DEEManagerImpl;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.ZipUtil;
import com.seeyon.v3x.util.annotation.SetContentType;
import com.seeyon.v3x.webmail.util.FileUtil;

/**
 * User: lius Date: 2007-1-29 Time: 20:04:32
 */
@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
public class FormAppMainController extends BaseController {
	
	private static final String resource_baseName = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
	private static RuntimeCharset fCurrentCharSet = SeeyonForm_Runtime
	.getInstance().getCharset();
	FileManager fileManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private static Log log = LogFactory.getLog(FormAppMainController.class);
	private MetadataManager metadataManager;
	
	//@ 附件 && 附件管理 added by Meixd 2010-10-21
    private AttachmentManager attachmentManager;
    
    private TempleteCategoryManager templeteCategoryManager;	
    private AppLogManager appLogManager;
    public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	private ColManager colManager;
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}
	
	private CollaborationFormBindEventListener collaborationFormBindEventListener;
    
	
	public void setCollaborationFormBindEventListener(
			CollaborationFormBindEventListener collaborationFormBindEventListener) {
		this.collaborationFormBindEventListener = collaborationFormBindEventListener;
	}
	public static RuntimeCharset getFCurrentCharSet() {
		return fCurrentCharSet;
	}
	public static void setFCurrentCharSet(RuntimeCharset currentCharSet) {
		fCurrentCharSet = currentCharSet;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

    public void setAttachmentManager(AttachmentManager attachmentManager)
    {
        this.attachmentManager = attachmentManager;
    }     
    
    public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	/***********************表单制作首页面**************************/
	public ModelAndView formNewFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/formNewFrame");
		return mav;
	}
	/**
	 * 点击表单制作后的首页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formshow(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		ModelAndView mav = new ModelAndView("form/formcreate/formShow");
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		//对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
		}		
		long userId = CurrentUser.get().getId();
		FormAppMain bm = new FormAppMain();
		String formType = request.getParameter("formType");
		String formmanid =  request.getParameter("formmanid");
		String applicationValue = request.getParameter("state");
		String otherform = request.getParameter("otherform");
		String selectquery = request.getParameter("selectquery");
		String state = request.getParameter("pageformname");
		String formAppName = request.getParameter("formAppName");
		
		if("otherform".equals(otherform)){
			bm.setUserids(formmanid);
		} else {
			bm.setUserids(String.valueOf(userId));
		}
		
		if(!"0".equals(selectquery)){
            //按应用查询
			if(applicationValue != null && !"".equals(applicationValue) && !"null".equals(applicationValue)){
				bm.setCategory(Long.valueOf(applicationValue));
			}		
			//从页面取得状态名称的值
			Locale local = LocaleContext.getLocale(request);
			String resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
			String state1 = ResourceBundleUtil.getString(resource, local, "form.query.allstate.label");
			if(state != null && !"".equals(state) && !"null".equals(state)){
				if(!state.equals(state1))
				  bm.setState(Integer.parseInt(state));
			}
			
			 if( "5".equals(selectquery)){
				if(bm.getCategory().longValue() != 0){
					bm.setCategory(Long.valueOf(0)) ;
				}
				bm.setName(formAppName) ;
				bm.setState(-1) ;
			}
		}
		bm.setFormType(Integer.parseInt(formType));
		bm.setFormstart(1);
		List applst = null;
		try {
			if("otherform".equals(otherform))
				applst =  getIOperBase().queryAllOther(bm);
			else
			    applst = getIOperBase().queryAllData(bm);
			applst = getIOperBase().assignCategory(applst);
			mav.addObject("applst",  getIOperBase().pagenate(applst));

			 // 获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			mav.addObject("state", state);
			mav.addObject("applicationValue", applicationValue);

			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
			mav.addObject("otherform", otherform);
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
		}
			mav.addObject("userId", userId);
			mav.addObject("applicationValue", applicationValue);
			mav.addObject("selectquery", selectquery);
			mav.addObject("state", state);
			mav.addObject("formAppName", formAppName) ;
			mav.addObject("formmanid", formmanid);
			mav.addObject("formType", formType);
			return mav;
		
	}
	
	/**
     * 获取表单对应的附件，
     * @param applst
     * @author Meixd 2010-10-25
     */
//    private List<Attachment> getAttachment(List<FormAppMain> applst){
//        if(applst == null)
//            return null;
//        List<Attachment> attlst = null;
//        for(FormAppMain fam : applst){                
//            long id = fam.getId();
//            attlst = attachmentManager.getByReference(id);                
//        }       
//        return attlst;
//    }

	
	
	private List<String> genFormNameList(List<FormAppMain> list){
		List<String> formNameList = new ArrayList<String>();
		Map map = new HashMap();
		for(FormAppMain fam : list){
			String formName = fam.getName();
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
	
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView formcreateBorderFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
	 ModelAndView mav = new ModelAndView("form/formcreate/formShowFrame");
	 mav.addObject("formType", request.getParameter("formType"));
	 mav.addObject("otherform", request.getParameter("otherform"));
     return mav;
    }
	
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView formMaker(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView formMakerback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrameback");
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView formcreateBorderFrameback(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/formShowFrameback");
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
    }
	
	/**
	 * 返回后表单制作后的首页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formshowback(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String formType = request.getParameter("formType");
        //对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
		}		
		ModelAndView mav = new ModelAndView("form/formcreate/formShow");	
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		long userId = CurrentUser.get().getId();
		FormAppMain bm = new FormAppMain();
		FormAppMain bmnew = new FormAppMain();
		bm.setUserids(String.valueOf(userId));
		bmnew.setUserids(String.valueOf(userId));
		String applicationValue = request.getParameter("state");
		//按应用查询
		if(applicationValue != null && !"".equals(applicationValue) && !"null".equals(applicationValue)){
			bm.setCategory(Long.valueOf(applicationValue));
		}
		String state = request.getParameter("pageformname");
		//从页面取得状态名称的值
		Locale local = LocaleContext.getLocale(request);
		String resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
		String state1 = ResourceBundleUtil.getString(resource, local, "form.query.allstate.label");
		if(state != null && !"".equals(state) && !"null".equals(state)){
			if(!state.equals(state1))
			  bm.setState(Integer.parseInt(state));
		}	
		bm.setFormstart(1);
		bm.setFormType(Integer.parseInt(formType));
		List applst = null;
		//List formNamelistnew = null;
		try {
			applst = getIOperBase().queryAllData(bm);
			applst = getIOperBase().assignCategory(applst);
			mav.addObject("applst",  getIOperBase().pagenate(applst));
			
			//获取表单名称列表
			//List<String> formNameList = new ArrayList<String>();
			//formNamelistnew = getIOperBase().queryAllData(bmnew);
			//formNameList = genFormNameList(formNamelistnew);		
			//mav.addObject("formNameList", formNameList);
			 // 获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			mav.addObject("state", state);
			mav.addObject("applicationValue", applicationValue);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
		}
			mav.addObject("userId", userId);
			mav.addObject("formType", formType);
			return mav;
		
	}
	/***********************基础信息页面**************************/
	/**
	 * 导入xsn文件，并提取必要信息，存入session,并生成建表所需的一些信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView baseInfo(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		int formType = Integer.parseInt(request.getParameter("formType"));
		String directry = "";
		List tablefieldlst = null;
		StringBuffer categoryHTML = new StringBuffer();
		if(sessionobject != null){
			directry = sessionobject.getXsnpath();
			tablefieldlst = sessionobject.getTableFieldList();
		}
		//如果tablefieldlst为空，为新建;如果不为空，为修改
		try{
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			if(categoryHTML.length()==0){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(\"" +Constantform.getString4CurrentUser("form.nosort.label")+ "\")");
				out.println("</script>");
				out.flush();
				ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
				mav.addObject("formType", request.getParameter("formType"));
				return mav;
			}
		if(tablefieldlst == null){
				//如果directry有值，说明已经上传过数据，不用重新上传
				if(directry == null || "".equals(directry)){
					sessionobject = new SessionObject();
					session.setAttribute("SessionObject", sessionobject);
					String[] urls=(String[])request.getParameterValues("fileUrl");
					String[] createDates=(String[])request.getParameterValues("fileCreateDate");
					String[] mimeTypes=(String[])request.getParameterValues("fileMimeType");
					String[] names=(String[])request.getParameterValues("filename");
					directry = getIOperBase().getXSNSaveDirectory(fileManager, urls, createDates, mimeTypes, names);
			
					sessionobject.setXsnpath(directry);
					sessionobject.setFormName(OperHelper.subPostfix(names[0]));
					
				}
				//新建时，表单的状态为'草稿'
				sessionobject.setFormstate("0");
				
				//readsource  文件名
				//调解析方法，生成类	把类分别放入session
				getIOperBase().parseXSN(sessionobject, directry,fileManager);
				//getIOperBase().parseXSN(sessionobject, directry);
				InfoPathObject xsf = sessionobject.getXsf();
				InfoPath_xsd xsd = xsf.getIntoxsd();
				
				//得到viewlst
				List formlst = sessionobject.getFormLst();
				if(formlst.size() == 0){
					formlst = new ArrayList(xsf.getViewList().size());
					for(int i=0;i<xsf.getViewList().size();i++){
						InfoPath_xsl xsl = (InfoPath_xsl)xsf.getViewList().get(i);
						sessionobject.getViewWidthvalue().put(i, xsl.getValue());
						SeeyonFormImpl see = new SeeyonFormImpl();
						InfoPath_FormView iformview = new InfoPath_FormView(see);
						iformview.setFViewfile(xsl.getFileName());
						iformview.setViewtype(TviewType.vtHtml);
						List viewlst = new ArrayList();
						//拼formpage所需的viewlst,剩下部分由方法inputdata组装
						viewlst.add(iformview);
						FormPage fp = new FormPage();
						fp.setName(xsf.getViewFileCaption(xsl.getFileName()));
						fp.setViewlst(viewlst);
						fp.setEngine("infopath");
						formlst.add(fp);
					}
				sessionobject.setFormLst(formlst);
				}
				if(formType == TAppBindType.BASEDATA.getValue() && formlst.size() > 1){
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(\"" +Constantform.getString4CurrentUser("form.formcreate.basedata.nomultiview.label")+ "\")");
					out.println("</script>");
					out.flush();
					ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
					mav.addObject("formType", request.getParameter("formType"));
					return mav;
				}
				//读入字段名
				//读入字段
				List masterlst = xsd.getMasternamelst();
				List slave = xsd.getSlavelst();
				List tablst = xsd.getTablst();
				String namespace = xsd.getNamespace();
//				FormTableValue ftv = (FormTableValue)getIOperBase().findBiggestValue();
//				if(ftv.getValue() != null){
//					tablenumber = ftv.getValue();
//				}
				//解析出字段名串
				tablefieldlst = getIOperBase().parseFieldName(masterlst,slave,tablst);
				//控制最大字段数为950，不能超过950(数据库最大列数为1000,但是系统中还存在内部预留字段)
				if(tablefieldlst.size()> 950){
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(\"" +Constantform.getString4CurrentUser("form.createform.maxField.label",tablefieldlst.size())+ "\")");
					out.println("</script>");
					out.flush();
					ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
					mav.addObject("formType", request.getParameter("formType"));
					return mav;
				}
				
		        //输入字段总数
		        sessionobject.setTablefieldsize(tablefieldlst.size());
		        sessionobject.setTableFieldList(tablefieldlst);
		        sessionobject.setNamespace(namespace);
			}   
		//注入此表单ID
		sessionobject.setFormid(Long.valueOf(UUIDLong.longUUID()));
		sessionobject.setFormType(formType);
		sessionobject.setAttachManId(String.valueOf(CurrentUser.get().getId()));
		sessionobject.setAttachManName( CurrentUser.get().getName());
		//把attachment存入到sessionobject中  by Meixd 2010-10-21
        sessionobject.setAttachment(getAttachmentFromRequest(request));
        
        //用于组织页面数据
		List applst = new ArrayList();
		//用于校验上传字段域名
		boolean hasFlag = false;
		boolean caseFlag = false;
		//组织tablefieldlst
        for(int i=0;i<tablefieldlst.size();i++){
        	TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
        	tfd.setBindname(sessionobject.getNamespace()+tfd.getName());
        	tfd.setId(CreateTableNumber.createNormalNumber(i+1));
        	tfd.setFieldname("field"+CreateTableNumber.createNormalNumber(i+1));
        	if(hasSystemFieldName(tfd.getName())){
        		hasFlag = true;
        		break;
        	}
        	//校验是否大小写重复
        	for(int j=tablefieldlst.size()-1;j>i;j--){
        		TableFieldDisplay temp = (TableFieldDisplay)tablefieldlst.get(j);
        		if(tfd.getName().equalsIgnoreCase(temp.getName())){
        			caseFlag = true;
        			break;
        		}
        	}
        	if(caseFlag){
        		break;
        	}
		}
        if(hasFlag){
        	List<String> lst = new ArrayList<String>();
			lst.add(Constantform.getString4CurrentUser("form.system.check"));
			OperHelper.creatformmessage(request,response,lst);
        	ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
			mav.addObject("formType", request.getParameter("formType"));
			return mav;
        }
        if(caseFlag){
        	List<String> lst = new ArrayList<String>();
			lst.add(Constantform.getString4CurrentUser("form.system.check.ignorcase"));
			OperHelper.creatformmessage(request,response,lst);
        	ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
			mav.addObject("formType", request.getParameter("formType"));
			return mav;
        }
        //标识本页面
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			OperHelper.creatformmessage(request,response,lst);			
			ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
			mav.addObject("formType", request.getParameter("formType"));
			return mav;
		}catch (Exception e) {
			log.error("制作表单异常", e);
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
			ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
			mav.addObject("formType", request.getParameter("formType"));
			return mav;
		}
        sessionobject.setPageflag(IPagePublicParam.BASEINFO);
		ModelAndView mav = new ModelAndView("form/formcreate/baseInfo");
        // 获得所属分类下拉列表中的数据
		Map<String, Metadata> appMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.form);
		mav.addObject("appMeta", appMeta);
		mav.addObject("formType", request.getParameter("formType"));
		mav.addObject("categoryHTML", categoryHTML);
		mav.addObject("formsort", sessionobject.getFormsort());
//      NF 移除新流程设置缓存
        session.removeAttribute("currentFormNewflow");
		return mav;
	}
	
	/**
	 * 构造attachment对象
     * @author Meixd 2010-10-21
     */
    public Attachment getAttachmentFromRequest(HttpServletRequest request) throws Exception{        
        String filename = request.getParameter("filename");    
        String mimeType = request.getParameter("fileMimeType");   
        Date createdate = Datetimes.parse(request.getParameter("fileCreateDate"), "yyyy-MM-dd");
//      String size = request.getParameter("size");
        String fileUrl = request.getParameter("fileUrl");       
        Attachment attachment = new Attachment();
        attachment.setCategory(ApplicationCategoryEnum.form.getKey());
        attachment.setType(ATTACHMENT_TYPE.FILE.ordinal());
        attachment.setFilename(filename);     
        attachment.setMimeType(mimeType);
        attachment.setCreatedate(createdate);     
        attachment.setFileUrl(Long.valueOf(fileUrl));
        attachment.setSort(1);
        return attachment;
    }

	/**
	 * 用于修改时候对baseinfo页面进行显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView baseinfoview(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/baseInfo");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");	
		if(sessionobject ==null)
		    return formMakerback(request,response);
		
		if(sessionobject.getEditflag() == null 
				||"".equals(sessionobject.getEditflag())
				||"null".equals(sessionobject.getEditflag())){
            //增加防护
			try{
			OperHelper.inputDataCollectData(request, sessionobject);
			}catch(SeeyonFormException e){
				log.error("保存录入定义页面信息时出错", e);
				List<String> lst = new ArrayList<String>();
				lst.add(e.getToUserMsg());
				OperHelper.creatformmessage(request,response,lst);
			}
		}else{
			if(sessionobject.getPageflag().equals(IPagePublicParam.BASEINFO)){
	            //baseinfo数据收集
				//OperHelper.baseInfoCollectData(request,sessionobject);	
			}else if(sessionobject.getPageflag().equals(IPagePublicParam.INPUTDATA)){
	            //收集inputdata页面的数据,当跳转到operconfig时收集数据，在本页操作不再进行收集操作
				if(request.getParameter("saveoperlst") == null
					&&request.getParameter("deltype") == null
					&&request.getParameter("selenum") == null){
					OperHelper.inputDataCollectData(request, sessionobject);
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
		sessionobject.setPageflag(IPagePublicParam.BASEINFO);
		 // 获得所属分类下拉列表中的数据
		Map<String, Metadata> appMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.form);
		mav.addObject("appMeta", appMeta);
		StringBuffer categoryHTML = new StringBuffer();
		categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
		mav.addObject("categoryHTML", categoryHTML);
		return mav;
	}	
	/************************inputdata****************************/
	
	/**
	 * 组成defaultinput.xml页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView inputData(HttpServletRequest request,
			HttpServletResponse response){
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		if(sessionobject ==null)
			try {
				return formMakerback(request,response);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		ModelAndView mav = new ModelAndView("form/formcreate/inputData");
		//baseinfo数据收集
		OperHelper.baseInfoCollectData(request,sessionobject);
		List<Metadata> formMetadata = null;
		try {
			getIOperBase().isExistsThisForm(sessionobject.getFormName());
            //向session对象中塞入系统变量和扩展绑定,系统枚举,应用枚举,表单枚举  		
			sessionobject = getIOperBase().systemenum(sessionobject);
			//Collection collection = metadataManager.getAllUserDefinedMetadatas();
			Collection collection = metadataManager.getAllUserDefinedMetadatasForForm();
			formMetadata = new ArrayList<Metadata>();
			if(null!=collection && collection.size()>0){
				formMetadata.addAll(collection);
			}
		} catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			//e.printStackTrace();
			OperHelper.creatformmessage(request,response,lst);			
			mav = new ModelAndView("form/formcreate/baseInfo");
            //获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			mav.addObject("formMetadata", formMetadata);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
			return mav;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			//e.printStackTrace();
			OperHelper.creatformmessage(request,response,lst);
			mav = new ModelAndView("form/formcreate/baseInfo");
            //获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			mav.addObject("formMetadata", formMetadata);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
			return mav;
		}
		//用于页面显示
		mav.addObject("formMetadata", formMetadata);
		sessionobject.setPageflag(IPagePublicParam.INPUTDATA);		
		return mav;
	}
	/**
	 * 用于修改时候对inputData页面进行显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView inputdataview(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/inputData");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");	
		if(sessionobject ==null)
			return formMakerback(request,response);
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
		Collection collection = metadataManager.getAllUserDefinedMetadatas();
		List<Metadata> formMetadata = new ArrayList<Metadata>();
		if(null!=collection && collection.size()>0){
			formMetadata.addAll(collection);
		}
		sessionobject.setPageflag(IPagePublicParam.INPUTDATA);	
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
		//向session对象中塞入系统变量和扩展绑定,系统枚举,应用枚举,表单枚举  		
		sessionobject = getIOperBase().systemenum(sessionobject);	
		mav.addObject("formMetadata", formMetadata);
		return mav;
	}
	
	public ModelAndView changeFieldTypeOper(HttpServletRequest request,
			HttpServletResponse response){
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		if(sessionobject ==null)
			try {
				return formMakerback(request,response);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		String fieldName = request.getParameter("fieldName");	
		if(StringUtils.isNotEmpty(fieldName)){
			List<TableFieldDisplay> tablefieldlst = sessionobject.getTableFieldList();
			for (TableFieldDisplay td : tablefieldlst) {
				if(td!=null){
					String formula = td.getFormula();
					if(StringUtils.isNotEmpty(formula) && formula.indexOf(fieldName)!=-1){
						td.setCompute(null);
						td.setFormula(null);
					}
				}
			}
		}
		return null;
	}
	
	public ModelAndView inputdatadetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/inputdatadetail");
		return mav;
	}
	public ModelAndView highlevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/highlevel");
		return mav;
	}
	public ModelAndView highlevellist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/highlevellist");
		return mav;
	}
	
	public ModelAndView highleveldetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/highleveldetail");
		return mav;
	}
	
	/**
	 * 设置关联表单，从选择返回关联表单相关信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setRelationForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/setRelationForm");
		String relationConditionId = request.getParameter("relationConditionId");
		//供关联表单-系统选择时关联条件使用
		if(Strings.isBlank(relationConditionId) || "null".equals(relationConditionId)){
			relationConditionId = String.valueOf(UUIDLong.longUUID());
		}
		mav.addObject("relationConditionId", relationConditionId);
		return mav;
	}
	
	/**
	 * 选择关联表单，从选择返回关联表单相关信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectRelationForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/selectRelationForm");	
		StringBuffer categoryHTML = new StringBuffer();
		categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
		mav.addObject("categoryHTML", categoryHTML);
		return mav;
	}
	
	/**
	 * 选择关联表单，从选择返回关联表单相关信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectRelationFormList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/selectRelationFormList");	
		long userId = CurrentUser.get().getId();
		FormAppMain bm = new FormAppMain();
		bm.setUserids(String.valueOf(userId));
		String applicationValue = request.getParameter("state");
		String otherform = request.getParameter("otherform");
		String selectquery = request.getParameter("selectquery");
		String state = request.getParameter("pageformname");
		String formAppName = request.getParameter("formAppName");
		String selectType = request.getParameter("selectType");
		if(!"0".equals(selectquery)){
            //按应用查询
			if(Strings.isNotBlank(applicationValue)){
				bm.setCategory(Long.valueOf(applicationValue));
			}		
			//从页面取得状态名称的值
			Locale local = LocaleContext.getLocale(request);
			String resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
			String state1 = ResourceBundleUtil.getString(resource, local, "form.query.allstate.label");
			if(state != null && !"".equals(state) && !"null".equals(state)){
				if(!state.equals(state1))
				  bm.setState(Integer.parseInt(state));
			}
			if( "5".equals(selectquery)){
				if(bm.getCategory().longValue() != 0){
					bm.setCategory(Long.valueOf(0)) ;
				}
				bm.setName(formAppName) ;
				bm.setState(-1) ;
			}
		}
		bm.setFormstart(1);
		List applst = null;
		try {
			if("otherform".equals(otherform))
				applst =  getIOperBase().queryAllOther(bm);
			else
			    applst = getIOperBase().queryAllData(bm);

			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");	
			applst = getIOperBase().assignCategory(applst);
			for(int i = applst.size() -1; i >= 0; i--){
				FormAppMain fam = (FormAppMain)applst.get(i);
				if(fam.getId().equals(sessionobject.getFormid()) || fam.getState() != 2){
					applst.remove(i);
					continue;
				}
				//设置关联表单时选择"系统选择"------仅列出由本表单管理员制作的，并且已经设置了唯一标示的无流程表单
				if(Strings.isNotBlank(selectType) && "system".equals(selectType)){
					if(fam.getFormType() == TAppBindType.FLOWTEMPLATE.getValue()){
						applst.remove(i);
						continue;
					}
					SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(fam.getId());
					if(fapp != null){
						List<String> uniqueFieldList = fapp.getUniqueFieldList();
						if(uniqueFieldList.size() == 0){
							applst.remove(i);
							continue;
						}
					}
				}
			}
			mav.addObject("applst",  getIOperBase().pagenate(applst));
			Map<String, Metadata> appMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);

			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
			mav.addObject("otherform", otherform);
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
		}
		mav.addObject("userId", userId);
		mav.addObject("applicationValue", applicationValue);
		mav.addObject("selectquery", selectquery);
		mav.addObject("state", state);
		mav.addObject("formAppName", formAppName) ;				
		return mav;
	}
	
	/**
	 * 获取指定的表单字段信息并组装成字符串返回
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getRefInputAtts(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String refParams = request.getParameter("refParams");
		String extendName = request.getParameter("extendName");
		String selectType = request.getParameter("selectType");
		IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
		ISeeyonInputExtend extend = fextendmanager.findByName(extendName);
		if(extend != null && extend instanceof IInputRelation){
			RelationInputAtt relationInputAtt = ((IInputRelation)extend).getRelationInputAtts(refParams, selectType);
			if(relationInputAtt != null){
				PrintWriter out = response.getWriter(); 
				out.write(relationInputAtt.toJSONobject().toString());				
			}
		}
		return null;
	}
	
	public Map<String, String[]> getUniqueFieldList(String formAppId) throws Exception {
		Map<String, String[]> uniqueFieldMap = new HashMap<String, String[]>(); //key : 数据域名称       value[0] : 1表示唯一标示 2表示数据唯一   value[1] : 数据类型
		if(Strings.isNotBlank(formAppId)){
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formAppId));
			if(fapp != null){
				
				if(fapp.getDataDefine() != null && (fapp.getDataDefine() instanceof SeeyonDataDefine)){
					ISeeyonDataSource dataSource = ((SeeyonDataDefine)fapp.getDataDefine()).getDataSource();
					if(dataSource != null){
						InfoPath_Inputtypedefine inputTypeDefine = dataSource.getDefaultInputtype();
						//唯一标示
						List<String> uniqueFieldList = fapp.getUniqueFieldList();
						for (String uniqueField : uniqueFieldList) {
							inputTypeDefine.field(uniqueField);
							uniqueFieldMap.put("my:" + uniqueField, new String[]{"1", inputTypeDefine.getFieldtype()});
						}
						//数据唯一
						List<IIP_InputObject> fInputList = inputTypeDefine.getInputList();
						for (IIP_InputObject iip_InputObject : fInputList) {
							if(iip_InputObject.isUnique()){
								String columName = iip_InputObject.getDataAreaName();
								if(uniqueFieldMap.get(columName) == null){
									uniqueFieldMap.put(columName, new String[]{"2",iip_InputObject.getFieldType()});
								}
							}
						}
					}
				}
			}
		}
		return uniqueFieldMap;
	}
	
	public Map<String, String[]> getReturnWriteFields(Long formAppId) {
		Map<String, String[]> returnWriteFieldsMap = new LinkedHashMap<String, String[]>();
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
		if(fapp != null){
			InfoPath_Inputtypedefine inputTypeDefine = null;
			if(fapp.getDataDefine() != null && (fapp.getDataDefine() instanceof SeeyonDataDefine)){
				ISeeyonDataSource dataSource = ((SeeyonDataDefine)fapp.getDataDefine()).getDataSource();
				if(dataSource != null){
					inputTypeDefine = dataSource.getDefaultInputtype();
					List<IIP_InputObject> fInputList = inputTypeDefine.getInputList();
					for (IIP_InputObject iip_InputObject : fInputList) {
						if(iip_InputObject instanceof TIP_InputOutwrite){
							inputTypeDefine.field(iip_InputObject.getDataAreaName());
							returnWriteFieldsMap.put(iip_InputObject.getDataAreaName(), new String[]{inputTypeDefine.getFieldtype(), inputTypeDefine.getLength(), inputTypeDefine.getDigit()});
						}
					}
				}
			}
		}
		return returnWriteFieldsMap;
	}
	
	/**
	 * 从页面返回所选表，然后从数据库或缓存中得到所需的全部值，然后在返回时拼成字符串反到前台
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView enumextend(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/enumextend");
		return mav;
	}
	/**
	 * 对计算字段从页面取到值后，并返回父页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView compute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/compute");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		mav.addObject("fieldlst", sessionobject.getTableFieldList());
		mav.addObject("syslst", sessionobject.getSysVariable());

		return mav;
	}	
	
	/**
	 * 对计算字段从页面取到值后，并返回父页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView computetoupper(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/computetoupper");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		
		//List<String> systemValues = this.getIOperBase().checkFormbindSystemValue(sessionobject);
		//branches_a8_v350sp1_r_gov GOV-3301 wangwei表单新建的时候，扩展控件中有职务级别的字样 Start
		List<String> systemValues1 = sessionobject.getSysVariable();
		List<String> systemValues=new ArrayList<String>();
		for(int j=0 ; j<systemValues1.size(); j++){
			String sysName=systemValues1.get(j);
			if((Boolean)SysFlag.is_gov_only.getFlag()){
				if("登录人员职务级别".equals(sysName) || "登录人员职务级别ID".equals(sysName)){
					continue;
				}else{
					systemValues.add(sysName);
				}
			}else{
				if("登录人员职务".equals(sysName) || "登录人员职务ID".equals(sysName)){
					continue;
				}else{
					systemValues.add(sysName);
				}
			}
		}
		//branches_a8_v350sp1_r_gov GOV-3301 wangwei表单新建的时候，扩展控件中有职务级别的字样 end
		session.setAttribute("systemValues", systemValues) ;
		mav.addObject("fieldlst", sessionobject.getTableFieldList());
		return mav;
	}
	/**
	 * 返回表单制作的时候设置扩展属性的页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showSetPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/showSetPage");		
		
		String tableFieldName = request.getParameter("tableFieldName") ;
		if(Strings.isBlank(tableFieldName)){
			return null ;
		}
		
		return mav;
	}
	/****************************operconfig***********************************/
	/**
	 * 组织defaultxml需要的数据
	 */
	public ModelAndView operConfig(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/operConfig");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		if(sessionobject ==null)
		    return formMakerback(request,response);
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
		List newflowidlist = new ArrayList();		
		//收集inputdata页面的数据,当跳转到operconfig时收集数据，在本页操作不再进行收集操作
		if(request.getParameter("saveoperlst") == null
			&&request.getParameter("deltype") == null
			&&request.getParameter("selenum") == null){
//			增加防护
			try{
			OperHelper.inputDataCollectData(request, sessionobject);
			}catch(SeeyonFormException e){
				log.error("保存录入定义页面信息时出错", e);
				List<String> lst = new ArrayList<String>();
				lst.add(e.getToUserMsg());
				OperHelper.creatformmessage(request,response,lst);
			}
		}
		int selenum = 0;
		//单据名称选择数,默认选择第一项
		if(request.getParameter("selenum") != null){
			selenum = Integer.parseInt((String)request.getParameter("selenum"));
		}
		//从formlst中取得值
		FormPage fp = (FormPage)sessionobject.getFormLst().get(selenum);

		//用于保存下面的操作列表
		//request.setAttribute("selenumattr", selenum);
		sessionobject.setSelenumattr(String.valueOf(selenum));
		if(request.getParameter("saveoperlst") != null){
//			增加防护
			try{
				fp = OperHelper.saveOperLst(request,response,fp,selenum) ;
			}catch(SeeyonFormException e){
				log.error(e);
				List<String> lst = new ArrayList<String>();
				lst.add(e.getToUserMsg());
				OperHelper.creatformmessage(request,response,lst);
			}
		}
		if(request.getParameter("deltype") != null){
			OperHelper.delOperation(request,fp,selenum) ;
		}
		sessionobject.setPageflag(IPagePublicParam.OPERCONFIG);	
		mav.addObject("formnamechange", request.getParameter("selenum"));
		String flowname = "";
		for(int i =0;i<sessionobject.getFlowidlist().size();i++){
			if(i<sessionobject.getFlowidlist().size()-1)
			  flowname += (String)sessionobject.getFlowidlist().get(i) +"↗";	
			else
			  flowname += (String)sessionobject.getFlowidlist().get(i);
		}
		if(!"".equals(flowname))
			flowid = flowname;
		mav.addObject("flowid", flowid);
		return mav;
	}
	
	public ModelAndView baseConfigDataSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/baseConfigDataSet");   
		return mav; 
	}
	/**
	 * 接收inputdata页面的值,并放入session  ---还未写完
	 * @param request
	 * @param response
	 */
	public void receiveData(HttpServletRequest request,
			HttpServletResponse response){
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		int size = sessionobject.getTablefieldsize();
		List inputlst = new ArrayList();
		for(int i=0;i<size;i++){
			InfoPath_FieldInput fi = new InfoPath_FieldInput();
			fi.setFName((String)request.getParameter("name"+i));
			try {
				fi.setInputType(fi.str2OperationType((String)request.getParameter("puttype"+i)));
			} catch (SeeyonFormException e) {
				// TODO Auto-generated catch block
				log.error("接收录入定义页面的值", e);
				//e.printStackTrace();
			}
			//解析compute字段
			if(request.getParameter("compute"+i) != null){
				
			}
			//解析扩展字段			
			if(request.getParameter("extend"+i) != null){
				
			}
			inputlst.add(fi);
		}
		sessionobject.setFieldInputList(inputlst);
	}
	
	public ModelAndView operConfigView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/operConfig");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");	
		if(sessionobject ==null)
		    return formMakerback(request,response);
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
		String flowname = "";
		for(int i =0;i<sessionobject.getFlowidlist().size();i++){
			if(i<sessionobject.getFlowidlist().size()-1)
			  flowname += (String)sessionobject.getFlowidlist().get(i) +"↗";	
			else
			  flowname += (String)sessionobject.getFlowidlist().get(i);
		}
		if(!"".equals(flowname))
			flowid = flowname;
		sessionobject.setPageflag(IPagePublicParam.OPERCONFIG);	
		mav.addObject("flowid", flowid);
		return mav;
	}
	
	/**
	 * 操作设置页面点击重复表按钮
	 */
	public ModelAndView operrepeatform(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String namespce = sessionobject.getNamespace();
	    List slalist = new ArrayList();
	    String tablename=null;
	    String slave =null;
	    String groupname = null;
		List fieldlst = sessionobject.getTableFieldList();
		if(fieldlst != null){
			for(int i=0; i<fieldlst.size();i++ ){
				TableFieldDisplay tfd = (TableFieldDisplay)fieldlst.get(i);
				String name = tfd.getTablename();
				if(i==0){
					tablename=tfd.getTablename();
				}else if(!name.equals(tablename)){
					tablename=tfd.getTablename();
					slave = tfd.getTablename();
					groupname = tfd.getEditablename();
					slalist.add(namespce+groupname);
				}
			}
		}	
	ModelAndView mav = new ModelAndView("form/formcreate/repeatform");
	mav.addObject("slavelist", slalist);
	return mav;
	}
	
	/**
	 * 操作设置页面点击开发高级按钮
	 */
	public ModelAndView operhigh(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List eventlist = new ArrayList();
		IViewEventManager manager = SeeyonForm_Runtime.getInstance().getViewEventManager();
		eventlist = manager.getNames();
		ModelAndView mav = new ModelAndView("form/formcreate/operconfighigh");
		mav.addObject("evelist", eventlist);
		return mav;
	}
	
	public ModelAndView operhighlevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List eventlist = new ArrayList();
		IViewEventManager manager = SeeyonForm_Runtime.getInstance().getViewEventManager();
		eventlist = manager.getNames();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String namespce = sessionobject.getNamespace();
		    List slalist = new ArrayList();
		    String tablename=null;
		    String slave =null;
		    String groupname = null;
			List fieldlst = sessionobject.getTableFieldList();
			if(fieldlst != null){
				for(int i=0; i<fieldlst.size();i++ ){
					TableFieldDisplay tfd = (TableFieldDisplay)fieldlst.get(i);
					String name = tfd.getTablename();
					if(i==0){
						tablename=tfd.getTablename();
					}else if(!name.equals(tablename)){
						tablename=tfd.getTablename();
						slave = tfd.getTablename();
						groupname = tfd.getEditablename();
						slalist.add(namespce+groupname);
					}
				}
			}	
		ModelAndView mav = new ModelAndView("form/formcreate/operhighlevel");
		mav.addObject("slavelist", slalist);
		mav.addObject("evelist", eventlist);
		return mav;
	}
	
	public ModelAndView operflagadd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List statelist = new ArrayList();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		statelist = sessionobject.getFormStateList();
		String namespce = sessionobject.getNamespace();
		List slalist = new ArrayList();
		    String tablename=null;
		    String slave =null;
		    String groupname = null;
			List fieldlst = sessionobject.getTableFieldList();
			if(fieldlst != null){
				for(int i=0; i<fieldlst.size();i++ ){
					TableFieldDisplay tfd = (TableFieldDisplay)fieldlst.get(i);
					String name = tfd.getTablename();
					if(i==0){
						tablename=tfd.getTablename();
					}else if(!name.equals(tablename)){
						tablename=tfd.getTablename();
						slave = tfd.getTablename();
						groupname = tfd.getEditablename();
						slalist.add(namespce+groupname);
					}
				}
			}	
		ModelAndView mav = new ModelAndView("form/formcreate/operadd");
		mav.addObject("slavelist", slalist);
		mav.addObject("statlist", statelist);
		return mav;
	}
	public ModelAndView submitadd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List statelist = new ArrayList();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		statelist = sessionobject.getFormStateList();
		ModelAndView mav = new ModelAndView("form/formcreate/submitadd");
		mav.addObject("statlist", statelist);
		return mav;
	}
	/**
	 * 操作设置页面点击初始化设置
	 */
	public ModelAndView createinitoper(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/createinitoper");
		List systlst = new ArrayList();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String fieldname = request.getParameter("fieldname");
		String tablename = request.getParameter("tablename");
		String enumtype = request.getParameter("enumtype");
		Metadata bindMetadata = null;
		if(!"".equals(enumtype) && !"null".equals(enumtype) && enumtype !=null){			
			Element Enumroot = dom4jxmlUtils.paseXMLToDoc(OperHelper.parseSpecialMark(enumtype)).getRootElement();	
			  InfoPath_Enum enums = new InfoPath_Enum();
			  enums.loadFromXml(Enumroot);
			  MetadataManager metadataManager = (MetadataManager) ApplicationContextHolder.getBean("metadataManager");
			  if(enums.getEnumid() != null)
			     bindMetadata = metadataManager.getMetadata(enums.getEnumid());
			  if(enums.isFinalChild()){
					List<MetadataItem> items = this.metadataManager.getLastLevelItemByMetadataId(enums.getEnumid());
					mav.addObject("items", items);
			  }
		}
			
		systlst =  sessionobject.getSysVariable();
		/*
		 List fielist = new ArrayList();
	        for(int i=0;i<sessionobject.getTableFieldList().size();i++){
	        	TableFieldDisplay tb=(TableFieldDisplay)sessionobject.getTableFieldList().get(i);
	        	fielist.add(tb.getName());
	        }
	        */
		//mav.addObject("selefieldlst", fielist);	
		mav.addObject("systemlist", systlst);
		mav.addObject("fieldname", fieldname);
		mav.addObject("tablename", tablename);
		mav.addObject("bindMetadata", bindMetadata);
		return mav;
	}	
	/**
	 * 表单填写完成后，保存表单相应的xml及生成对应的数据库对象
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView finish(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String thispage = (String)request.getParameter("thispage");
		//是否直接发布标识
		String publishsign = (String)request.getParameter("publishsign");
		String url = null;
		if(IPagePublicParam.BASEINFO.equalsIgnoreCase(thispage) 
				||IPagePublicParam.INPUTDATA.equalsIgnoreCase(thispage)
				||IPagePublicParam.OPERCONFIG.equalsIgnoreCase(thispage)){
			url = "form/formcreate/" + thispage;
		}else if(IPagePublicParam.QUERYSET.equalsIgnoreCase(thispage)){
			url = "form/formquery/query_queryset";
		}else if(IPagePublicParam.REPORTSET.equalsIgnoreCase(thispage)){
			url = "form/formreport/stat_statset";
		}else if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage)){
			url = "form/formBind/appBind";
		}else if(IPagePublicParam.TRIGGERSET.equalsIgnoreCase(thispage)){
			url = "form/formtrigger/triggerSet";
		}
		ModelAndView mav = new ModelAndView(url);
		String flowid = request.getParameter("flowid");
		User user = CurrentUser.get();
		try {
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			if(sessionobject ==null)
			    return formMakerback(request,response);
			if(request.getParameter("selcondition") != null){
				request.setAttribute("selenumattr", (String)request.getParameter("selcondition"));
				sessionobject.setSelenumattr((String)request.getParameter("selcondition"));
			}
			
			if(thispage.equalsIgnoreCase(IPagePublicParam.BINDINFO)){
				//添加信息管理绑定信息 by wusb at 2010-03-17
				BindHelper.systemSaveAppBindMain(request, sessionobject);
			}
            
            //保存formAppMain同时保存infopath附件文件 by Meixd 2010-10-21
			if(!attachmentManager.hasAttachments(sessionobject.getFormid(), sessionobject.getFormid())){
		        Attachment attachment = sessionobject.getAttachment();            
		        attachment.setReference(sessionobject.getFormid());            
		        attachment.setSubReference(sessionobject.getFormid());
		        attachment.setSize((long)699886);          
		        List<Attachment> attachments = new ArrayList<Attachment>();
		        attachments.add(attachment);
		        this.attachmentManager.create(attachments);
			}
	
			List newlist = new ArrayList();
			if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid)){
				String[] flow = flowid.split("↗");
				for(int i=0;i<flow.length;i++){
					newlist.add(flow[i]);
				}
			}
			for(int i =0;i<newlist.size();i++){
				String flowname = (String) newlist.get(i);
				//List<FormFlowid>  flowlist =  getIOperBase().queryFlowIdByVariableName(flowname);
				List<FormFlowid>  flowlist =  getIOperBase().queryFlowIdByVariableName(flowname,user.getLoginAccount());
				if(flowlist.size()==0){
				   throw new DataDefineException(1,Constantform.getString4CurrentUser("form.flowid.label")+flowname+Constantform.getString4CurrentUser("form.flowiddel.label"),Constantform.getString4CurrentUser("form.flowid.label")+flowname+Constantform.getString4CurrentUser("form.flowiddel.label"));
				}
				for(int j=0;j<flowlist.size();j++){
					FormFlowid formflowid = (FormFlowid)flowlist.get(j);
					if("Y".equalsIgnoreCase(formflowid.getState()))
					    //throw new DataDefineException(1,"您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。","您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。");
				        throw new DataDefineException(1,Constantform.getString4CurrentUser("form.flowid.label")+formflowid.getVariablename()+Constantform.getString4CurrentUser("form.flowid.replac.label"),Constantform.getString4CurrentUser("form.flowid.label")+formflowid.getVariablename()+Constantform.getString4CurrentUser("form.flowid.replac.label"));
				}
			}
			if(flowid !=null)
				sessionobject.setFlowidlist(newlist);	
			isExistscategoryThisForm(sessionobject.getFormsort());
			
			getIOperBase().LoadFromCab(session, fileManager);	
			getIOperBase().formenumnewifuse(sessionobject);
			
            //新建表单操作日志
	        appLogManager.insertLog(user, AppLogAction.Form_New, user.getName(), sessionobject.getFormName());
			if("2".equals(publishsign)){
				/*//保存挂接菜单数据 by wusb at 2010-05-19
				if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue())
					BindHelper.saveMenuAndUpdateMenuSetting(sessionobject,true);*/
				
				getIOperBase().publishForm(sessionobject.getFormid(), 2, sessionobject.getFormName(), false);
                //发布表单操作日志
		        appLogManager.insertLog(user, AppLogAction.Form_Publish, user.getName(), sessionobject.getFormName());
			}				
			}catch(SeeyonFormCheckException e){
				List<String> lst = new ArrayList<String>();
				List<SeeyonFormException> exceptionList = e.getList();
				for(SeeyonFormException e1 : exceptionList){
					lst.add(e1.getMessage());
				}
				log.error("保存表单异常", e);
				OperHelper.creatformmessage(request,response,lst);
				//ModelAndView mav = new ModelAndView("form/formcreate/operConfig");
				if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage))
					return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
				else
					return mav;				
			}
		    catch (SeeyonFormException e) {
				List<String> lst = new ArrayList<String>();
				lst.add(e.getToUserMsg());
				//e.printStackTrace();
				log.error("保存表单异常", e);
				OperHelper.creatformmessage(request,response,lst);
				//ModelAndView mav = new ModelAndView("form/formcreate/operConfig");
				if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage))
					return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
				else
					return mav;			
			}catch (Exception e) {
				// TODO Auto-generated catch block
				List<String> lst = new ArrayList<String>();
				lst.add(e.getMessage());
				//e.printStackTrace();
				log.error("保存表单异常", e);
				OperHelper.creatformmessage(request,response,lst);
				//ModelAndView mav = new ModelAndView("form/formcreate/operConfig");
				if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage))
					return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
				else
					return mav;			
			}
          return formMaker(request,response);	
	}
	
	/****************************表单修改***************************************/
	/**
	 * 用于修改时候对baseinfo页面进行显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formcreate/baseInfo");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");		
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		if(sessionobject == null){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject",sessionobject);
		}
		String aAppName = (String)request.getParameter("aAppName");
		String id = (String)request.getParameter("id");
		//注入表单ID和表单名称
		sessionobject.setFormid(Long.valueOf(id));
        sessionobject.setFormName(aAppName);
		String path = sessionobject.getXsnpath();
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));
		if(afapp!=null){
			sessionobject = getIOperBase().loadFromnoInfoPath(afapp,id);
			session.setAttribute("SessionObject", sessionobject);
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(sessionobject.getFormName());
              //08-05-14修改
	    	  afapp.setFId(sessionobject.getFormid());
	    	  try{
	    		  afapp.loadFromDB();
				  sessionobject = getIOperBase().loadFromnoInfoPath(afapp,id);
				  if("2".equals(sessionobject.getFormstate())){
					  SeeyonForm_Runtime.getInstance().getAppManager().regApp(afapp);
				  }
			  } catch(Exception e){
  				log.error("修改表单定义解析错误", e);
 			  }finally{
 				 afapp.unloadAppHibernatResorece();
 			  }
			  session.setAttribute("SessionObject", sessionobject);
			 
	    }
		sessionobject.setFormName(aAppName);
        //注入表单ID和表单名称
		sessionobject.setFormid(Long.valueOf(id));
		sessionobject.setEditflag("edit");
		sessionobject.setXsnpath("edit");
		//向session对象中塞入系统变量和扩展绑定,系统枚举,应用枚举,表单枚举  		
		sessionobject = getIOperBase().systemenum(sessionobject);	
		sessionobject.setOldformsort(sessionobject.getFormsort());
		sessionobject.setPageflag(IPagePublicParam.BASEINFO);
		addInputDataFlowIdListToSessionObject(sessionobject);
        //获得所属分类下拉列表中的数据
		Map<String, Metadata> appMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.form);
		mav.addObject("appMeta", appMeta);
		StringBuffer categoryHTML = new StringBuffer();
		categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
		mav.addObject("categoryHTML", categoryHTML);
		mav.addObject("sort",sessionobject.getFormsort());
        //NF 移除新流程设置缓存
        session.removeAttribute("currentFormNewflow");
		return mav;
	}

	
	private void addInputDataFlowIdListToSessionObject(SessionObject sessionobject) throws DataDefineException{
		List<FormFlowid> flowidlist= getIOperBase().getFlowidListbyformid("inputData_" + sessionobject.getFormid().toString());
		List<Long> newflowidlist = new ArrayList<Long>();
	    for(int i=0;i<flowidlist.size();i++){
	    	FormFlowid formflowid = flowidlist.get(i);
	    	if("Y".equalsIgnoreCase(formflowid.getState()))
	    		newflowidlist.add(formflowid.getId());   
	    }
	    sessionobject.setFlowIdListForInputData(newflowidlist);
	    sessionobject.setOldFlowIdListForInputData(newflowidlist);
	}
	
	public ModelAndView datamatch(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String directry = "";
		List newtablefieldlst = null;
		List tablefieldlst = null;
		ModelAndView mav = new ModelAndView("form/formcreate/datamatch");   
        //  新上传中有而后台中没有的数据即新增列表
		List newlist = new ArrayList (); 
		//新上传中有后台中也有的数据即已匹配数据列表
		List matchlist = new ArrayList();		
		//新上传中没有而后台中有的数据即删除列表
		List delelist = new ArrayList();
	    try{    			 
			//如果tablefieldlst为空，为新建;如果不为空，为修改
			if(tablefieldlst == null){
					//如果directry有值，说明已经上传过数据，不用重新上传
					if(directry == null || "".equals(directry)){
						sessionobject = new SessionObject();
						session.setAttribute("SessionObject", sessionobject);					
						String[] urls=(String[])request.getParameterValues("fileUrl");
						String[] createDates=(String[])request.getParameterValues("fileCreateDate");
						String[] mimeTypes=(String[])request.getParameterValues("fileMimeType");
						String[] names=(String[])request.getParameterValues("filename");
						directry = getIOperBase().getXSNSaveDirectory(fileManager, urls, createDates, mimeTypes, names);			
						sessionobject.setXsnpath(directry);
					}
			}
	        //应用名称
			String aAppName = (String)request.getParameter("aAppName");
	        sessionobject.setFormName(aAppName);
	        String id = (String)request.getParameter("id");
	        sessionobject.setFormid(Long.parseLong(id));
	        //后台数据的字段名列表
	       
	        ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));
			if(afapp!=null){
			  sessionobject.Datadefine(afapp);
			  sessionobject.getOldmaertlst();
			  sessionobject.getOldslavelst();
			}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(aAppName);
	          //08-05-14修改
			  afapp.setFId(Long.parseLong(id));  
			  try{
				  afapp.loadFromDB();
				  sessionobject.Datadefine(afapp);
				  sessionobject.getOldmaertlst();
				  sessionobject.getOldslavelst();
			  }catch(Exception e){
	  			  log.error("表单定义解析出错", e);
	 		  }finally{
	 			  afapp.unloadAppHibernatResorece();
			  } 		 
		    }
			//调解析方法，生成类	把类分别放入session
			getIOperBase().parseXSN(sessionobject, directry,fileManager);
			//getIOperBase().parseXSN(sessionobject, directry);
			InfoPathObject xsf = sessionobject.getXsf();
			InfoPath_xsd xsd = xsf.getIntoxsd();	
		    List masterlst = xsd.getMasternamelst();
		    List slave = xsd.getSlavelst();
			String namespace = xsd.getNamespace();
			List newtablst = xsd.getTablst();
			List tablst = sessionobject.getTablelist();
			//解析出字段名串       
			newtablefieldlst = getIOperBase().parsenewName(masterlst, slave, tablst,newtablst);
			sessionobject.setNewTableFieldList(newtablefieldlst);
			//新上传得字段
			List newmasterlst = new ArrayList();
			List newslavelst = new ArrayList();
			String tablename = null;
			String mastername = null;
			
			//旧的字段
			List oldmasterlst = sessionobject.getOldmaertlst();
			List oldslavelst = sessionobject.getOldslavelst();
			
			for(int i=0;i < newtablefieldlst.size();i++){
				TableFieldDisplay tem = (TableFieldDisplay)newtablefieldlst.get(i);
				String table = tem.getTablename();
				String field = tem.getName();
				//第一次进来的是主表
	
				if(i==0){
					tablename = table;
					mastername = table;
				}
				if(mastername.equals(table)){
					newmasterlst.add(table+"↗"+field);
				}
				//以后进来的是子表
				if(!tablename.equals(table)){
					newslavelst.add(table+"↗"+field);	
				}
			}
			for(int i=0;i<oldmasterlst.size();i++){
				Object match = oldmasterlst.get(i); 
				Matchdata obj = new Matchdata();
				if(newmasterlst.contains(match) == true){            
				    String matchs= (String) match;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
				    matchlist.add(obj);
				}else{
				 	String matchs= (String) match;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
				    delelist.add(obj);
				}
			}
			
			for(int i=0;i<oldslavelst.size();i++){
				Object match = oldslavelst.get(i); 		
				Matchdata obj = new Matchdata();
				if(newslavelst.contains(match) == true){             
				    String matchs= (String) match;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
				    matchlist.add(obj);
				}else{
					String matchs= (String) match;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
				    delelist.add(obj);
				}
			}
		
			for(int a=0;a<newmasterlst.size();a++){
				Object newcreate1 = newmasterlst.get(a);
				Matchdata obj = new Matchdata();
				if(oldmasterlst.contains(newcreate1)== false){
					String matchs= (String) newcreate1;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
					newlist.add(obj);
				}			
			}
			for(int i=0;i < newslavelst.size();i++){
				Object match = newslavelst.get(i); 	
				Matchdata obj = new Matchdata();
				if(oldslavelst.contains(match) == false){             
				    String matchs= (String) match;
				    String tabname = OperHelper.AddTableName(matchs);
				    String fiename = OperHelper.AddFieldName(matchs);
				    obj.setValue(fiename);
					obj.setTableName(tabname);
				    newlist.add(obj);
				}
			}
			//判断系统内置变量与同名大小写
			boolean hasFlag = false;
			boolean caseFlag = false;
			for(int i=0; i < newlist.size();i++){
				Matchdata obj = (Matchdata)newlist.get(i);
				if(hasSystemFieldName(obj.getValue())){
					hasFlag = true;
					break;
	        	}
				for(int j = newlist.size()-1;j>i;j--){
					Matchdata tObj = (Matchdata)newlist.get(j);
					if(obj.getValue().equalsIgnoreCase(tObj.getValue())){
						caseFlag = true;
						break;
					}
				}
				if(caseFlag){
					break;
				}
			}
			if(hasFlag){
	        	List<String> lst = new ArrayList<String>();
				lst.add(Constantform.getString4CurrentUser("form.system.check1"));
				OperHelper.creatformmessage(request,response,lst);
				mav.addObject("errorsign", "error");
				mav.addObject("selefieldlst", matchlist);	
				//用于页面的新增下拉列表框
				mav.addObject("newselelst", newlist);
		        //用于页面的删除下拉列表框
				mav.addObject("deleselelst", delelist);
				return mav;
	        }
			if(caseFlag){
				List<String> lst = new ArrayList<String>();
				lst.add(Constantform.getString4CurrentUser("form.system.check.ignorcase"));
				OperHelper.creatformmessage(request,response,lst);
				mav.addObject("errorsign", "error");
				mav.addObject("selefieldlst", matchlist);	
				//用于页面的新增下拉列表框
				mav.addObject("newselelst", newlist);
		        //用于页面的删除下拉列表框
				mav.addObject("deleselelst", delelist);
				return mav;
			}
	    }catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			 log.error("上传infopath表单定义解析出错", e);
			OperHelper.creatformmessage(request,response,lst);			
			mav.addObject("errorsign", "error");
			mav.addObject("selefieldlst", matchlist);	
			//用于页面的新增下拉列表框
			mav.addObject("newselelst", newlist);
	        //用于页面的删除下拉列表框
			mav.addObject("deleselelst", delelist);
			return mav;
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			 log.error("上传infopath表单定义解析出错", e);
			OperHelper.creatformmessage(request,response,lst);
			mav.addObject("errorsign", "error");
			mav.addObject("selefieldlst", matchlist);	
			//用于页面的新增下拉列表框
			mav.addObject("newselelst", newlist);
	        //用于页面的删除下拉列表框
			mav.addObject("deleselelst", delelist);
			return mav;
		}
		
		//把attachment存入到sessionobject中  by Meixd 2010-10-21
        try
        {
            sessionobject.setAttachment(getAttachmentFromRequest(request));
        }
        catch (Exception e)
        {
           log.error(e);
        }
		    
		//用于页面已匹配下拉列表框
		mav.addObject("selefieldlst", matchlist);	
		//用于页面的新增下拉列表框
		mav.addObject("newselelst", newlist);
        //用于页面的删除下拉列表框
		mav.addObject("deleselelst", delelist);
		mav.addObject("errorsign", "false");
		return mav;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editbaseinfo(HttpServletRequest request,
		 	HttpServletResponse response) throws Exception {
		    long userId = CurrentUser.get().getId();
		    getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
			getIOperBase().editBaseInfo(request); 
			//控制最大字段数为950，不能超过950,数据库中最大列数为1000,但是系统中有字段要修改
			SessionObject sessionobject = (SessionObject)request.getSession().getAttribute("SessionObject");
			if(sessionobject!=null){
				if(sessionobject.getTablefieldsize()>950){
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(\"" +Constantform.getString4CurrentUser("form.createform.maxField.label",sessionobject.getTablefieldsize())+ "\")");
					out.println("</script>");
					out.flush();
					ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
					mav.addObject("formType", sessionobject.getFormType());
					return mav;
				}
			}
			
			ModelAndView mav = new ModelAndView("form/formcreate/baseInfo");	
            //获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
			mav.addObject("userId", userId);	
			mav.addObject("formType", sessionobject.getFormType());
			return mav;
		}
	/**
	 * 修改时，保存所有信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editSave(HttpServletRequest request,
			HttpServletResponse response) {
		String thispage = (String)request.getParameter("thispage");
		String flag = (String)request.getParameter("flag");
		long userId = CurrentUser.get().getId();
//		是否直接发布标识
		String publishsign = (String)request.getParameter("publishsign");
		String url = null; 
		if(IPagePublicParam.BASEINFO.equalsIgnoreCase(thispage)
				||IPagePublicParam.INPUTDATA.equalsIgnoreCase(thispage)
				||IPagePublicParam.OPERCONFIG.equalsIgnoreCase(thispage)){
			//url = "form/formcreate/" + thispage;
			url = "form/formcreate/formcreateBorderFrame";
		}else if(IPagePublicParam.QUERYSET.equalsIgnoreCase(thispage)){
			//url = "form/formquery/query_queryset";
			url = "form/formcreate/formcreateBorderFrame";
		}else if(IPagePublicParam.REPORTSET.equalsIgnoreCase(thispage)){
			//url = "form/formreport/stat_statset";
			url = "form/formcreate/formcreateBorderFrame";
		}else if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage)){
			url = "form/formcreate/formcreateBorderFrame";
		}else if(IPagePublicParam.TRIGGERSET.equalsIgnoreCase(thispage)){
			url = "form/formcreate/formcreateBorderFrame";
		}
		//ModelAndView mav = new ModelAndView(url);
		Long sort = 0l;
		String exceptionflag = "";
		String flowid = request.getParameter("flowid");
		User user = CurrentUser.get();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		try {
		if(sessionobject == null)
			return formMaker(request,response);	
		FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
		FormOwnerList fol = new FormOwnerList();
		fol.setAppmainId(sessionobject.getFormid());
		fol.setOwnerId(userId);
		List formownerlst = formDaoManager.queryOwnerListByCondition(fol);
	    if(formownerlst.size() ==0)
	    	throw new DataDefineException(1,"单位管理员已经修改了此表单的授权，您没有权限进行修改。","单位管理员已经修改了此表单的授权，您没有权限进行修改。");
		if(sessionobject.getEnumnamemap().size() !=0){
			sessionobject.getOldenumnamemap().clear();
			HashMap hash =  sessionobject.getEnumnamemap();
    		Iterator it = hash.entrySet().iterator();
    		while(it.hasNext()){
    			Map.Entry entry = (Map.Entry)it.next();
    			String enumname = entry.getKey().toString();
    			sessionobject.getOldenumnamemap().put(enumname, enumname);
    		}
		}	
		List newlist = new ArrayList();
		if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid)){
			String[] flow = flowid.split("↗");
			for(int i=0;i<flow.length;i++){
				if(!"".equals(flow[i]) && !"null".equals(flow[i]) && flow[i] !=null){
					if(newlist.contains(flow[i])){
						throw new DataDefineException(1,Constantform.getString4CurrentUser("form.flowid.label")+flow[i]+Constantform.getString4CurrentUser("form.flowid.replac.label"),Constantform.getString4CurrentUser("form.flowid.label")+flow[i]+Constantform.getString4CurrentUser("form.flowid.replac.label"));
					}
				   newlist.add(flow[i]);				
				}
			}
		}else{
			newlist = sessionobject.getFlowidlist();
		}
		for(int j=0;j<newlist.size();j++){
			Object delect = newlist.get(j); 
			if(sessionobject.getOldflowidlist().contains(delect) == false){            
			    String matchs= (String) delect;
			    List<FormFlowid>  flowlist =  getIOperBase().queryFlowIdByVariableName(matchs,user.getLoginAccount());
			    if(flowlist.size()==0){
			    	throw new DataDefineException(1,Constantform.getString4CurrentUser("form.flowid.label")+matchs+Constantform.getString4CurrentUser("form.flowiddel.label"),Constantform.getString4CurrentUser("form.flowid.label")+matchs+Constantform.getString4CurrentUser("form.flowiddel.label"));
			    }
			    for(int f=0;f<flowlist.size();f++){
					FormFlowid formflowid = (FormFlowid)flowlist.get(f);
					if("Y".equalsIgnoreCase(formflowid.getState()) && !formflowid.getAppname().equals(sessionobject.getFormid().toString()))
						//throw new SeeyonFormException(1,"您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。","您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。");
					    //throw new DataDefineException(1,"您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。","您设置的流水号"+formflowid.getVariablename()+"已经被占用，请重新设置。");
				        throw new DataDefineException(1,Constantform.getString4CurrentUser("form.flowid.label")+formflowid.getVariablename()+Constantform.getString4CurrentUser("form.flowid.replac.label"),Constantform.getString4CurrentUser("form.flowid.label")+formflowid.getVariablename()+Constantform.getString4CurrentUser("form.flowid.replac.label"));
			    }
			}
		}
		if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid))
			sessionobject.setFlowidlist(newlist);	
		isExistscategoryThisForm(sessionobject.getFormsort());
		
		/*String uniquexml=request.getParameter("uniquexml");
		if(uniquexml != null){
			sessionobject.setUniqueFieldString(uniquexml);
		}*/
		String uniqueFieldListString=request.getParameter("uniquedatafield");
		if(uniqueFieldListString != null ){
			String[] uniqueField=uniqueFieldListString.split(",");
			List uniqueFieldList=new ArrayList();
			for(int i=0;i<uniqueField.length;i++){
				uniqueFieldList.add(uniqueField[i]);
			}
			sessionobject.setUniqueFieldList(uniqueFieldList);
		}
		getIOperBase().editSave(sessionobject,request,response,fileManager);
		sessionobject.setFormName(sessionobject.getFormName());	
		sort = sessionobject.getFormsort();
		getIOperBase().formenumeditifuse(sessionobject);
        //修改表单操作日志
		appLogManager.insertLog(user, AppLogAction.Form_Edit, user.getName(), sessionobject.getFormName());
        //修改表单所属人操作日志
		if(user.getId() !=Long.parseLong(sessionobject.getAttachManId()))
			appLogManager.insertLog(user, AppLogAction.Form_EditAuth, user.getName(), sessionobject.getFormName(),sessionobject.getAttachManName());
		
		if(sessionobject.getTemplateobj() != null && sessionobject.getTemplateobj().isChanged()){
			appLogManager.insertLog(user, AppLogAction.Form_ChangeAuth, user.getName(), sessionobject.getFormName());
		}
		/**应用日志记录 《》修改了表单《》查询《》的授权***/
		if(sessionobject.getQueryConditionList() != null){
			for(QueryObject queryObject : sessionobject.getQueryConditionList()){
				if(queryObject.isChanged()){
					appLogManager.insertLog(user, AppLogAction.Form_ChangeQueryAuth, user.getName(), sessionobject.getFormName(),queryObject.getQueryName());
				}
			}
		}
		/**应用日志记录 《》修改了表单《》统计《》的授权***/
		if(sessionobject.getReportConditionList() != null){
			for(ReportObject reportObject : sessionobject.getReportConditionList()){
				if(reportObject.isChanged()){
					appLogManager.insertLog(user, AppLogAction.Form_ChangeReportAuth, user.getName(), sessionobject.getFormName(),reportObject.getReportName());
				}
			}
		}
        /**
         * 新老数据编辑修改时，均不保存附件记录，从infopath导入修改时都保存附件记录，保证下载的文件都是最新导入的
         * 若之前有infopath导入修改记录，则再次导入时删除之前记录
         * @author by Meixd 2010-10
         */
		if(sessionobject.getAttachment() != null){
		    Attachment attachment = sessionobject.getAttachment();
	        if(attachmentManager.getByReference(sessionobject.getFormid()) != null){
	            attachmentManager.deleteByReference(sessionobject.getFormid());
	        }
	        attachment.setReference(sessionobject.getFormid());                  
	        attachment.setSubReference(sessionobject.getFormid());
	        attachment.setSize((long)655988);          
	        List<Attachment> attachments = new ArrayList<Attachment>();
	        attachments.add(attachment);
	        this.attachmentManager.create(attachments);
		}
        		
        //提示用户操作成功
//		PrintWriter out = response.getWriter();
//		out.println("<script>");
//		//out.println("alert("保存操作成功");
//		out.println("alert('"+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request),"formapp.saveoperok.label")+"')");
//		out.println("</script>");
//		out.flush();
		
		//重新加载缓存
//		ISeeyonForm_Application afapp = SeeyonForm_Runtime.getInstance().getAppManager().findById(sessionobject.getFormid());
//		FormAppMain fam = ((FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager")).findApplicationById(sessionobject.getFormid());
//		afapp.setAppName(sessionobject.getFormName());
//    	afapp.setFId(fam.getId());
//    	afapp.loadFromDB();
    	
		if("2".equals(publishsign)){
			/*//保存挂接菜单数据 by wusb at 2011-05-19
			if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue())
				BindHelper.saveMenuAndUpdateMenuSetting(sessionobject,true);*/
			
			getIOperBase().publishForm(sessionobject.getFormid(), 2, sessionobject.getFormName(), false);
            //发布表单操作日志
			appLogManager.insertLog(user, AppLogAction.Form_Publish, user.getName(), sessionobject.getFormName());
		}/*else{
			//保存挂接菜单数据 by wusb at 2011-05-19
			if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue()){
				if("2".equals(sessionobject.getFormstate())){
					BindHelper.saveMenuAndUpdateMenuSetting(sessionobject,false);
					BindHelper.saveMenuProfile(sessionobject.getFormid());
				}
			}
		}*/
		}catch(SeeyonFormCheckException e){ 
			exceptionflag = "true";
			List<String> lst = new ArrayList<String>();
			List<SeeyonFormException> exceptionList = e.getList();
			for(SeeyonFormException e1 : exceptionList){
				lst.add(e1.getMessage());
			}	
			//e.printStackTrace();
			log.error("保存表单异常", e);
			OperHelper.creatformmessage(request,response,lst);
		}	
		catch (SeeyonFormException e) {
			exceptionflag = "true";
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			//e.printStackTrace();
			log.error("保存表单异常", e);
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			exceptionflag = "true";
			// TODO Auto-generated catch block
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			//e.printStackTrace();
			log.error("保存表单异常", e);
			OperHelper.creatformmessage(request,response,lst);
		}
			
			if(exceptionflag.equals("true")){
				if(IPagePublicParam.BINDINFO.equalsIgnoreCase(thispage)){
					//url = "form/formBind/appBind";
					return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
				}else if(IPagePublicParam.BASEINFO.equalsIgnoreCase(thispage)
						||IPagePublicParam.INPUTDATA.equalsIgnoreCase(thispage)
						||IPagePublicParam.OPERCONFIG.equalsIgnoreCase(thispage)){
					url = "form/formcreate/" + thispage;
				}else if(IPagePublicParam.QUERYSET.equalsIgnoreCase(thispage)){
					url = "form/formquery/query_queryset";
				}else if(IPagePublicParam.REPORTSET.equalsIgnoreCase(thispage)){
					url = "form/formreport/stat_statset";
				} else if(IPagePublicParam.TRIGGERSET.equalsIgnoreCase(thispage)){
					return super.redirectModelAndView("/triggerController.do?method=formTriggerSet");
				}
			}		
			ModelAndView mav = new ModelAndView(url);
            //获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			Collection collection = metadataManager.getAllUserDefinedMetadatas();
			List<Metadata> formMetadata = new ArrayList<Metadata>();
			if(null!=collection && collection.size()>0){
				formMetadata.addAll(collection);
			}
			mav.addObject("categoryHTML", categoryHTML);
			mav.addObject("appMeta", appMeta);
			mav.addObject("sort",sort);
			mav.addObject("flag", flag);
			mav.addObject("userId", userId);
			mav.addObject("flowid", flowid);
			mav.addObject("formMetadata", formMetadata);
			mav.addObject("formType", sessionobject.getFormType());
			return mav;
		
	}

	@CheckRoleAccess(roleTypes={RoleType.FormAdmin,RoleType.Administrator})
	public ModelAndView formdetailcheck(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        String id =java.net.URLDecoder.decode(request.getParameter("id"), "UTF-8");
        PrintWriter out = response.getWriter(); 
		FormAppMain fm = getIOperBase().findAppbyId(Long.parseLong(id));
		StringBuffer returnstr = new StringBuffer();	
		if(fm == null){
            //提示用户该表单已经被删除
			returnstr.append("true");
			out.write(String.valueOf(returnstr.toString()));			
		}else{
	    	returnstr.append("false");
	    	out.write(String.valueOf(returnstr.toString()));
	    }
		return null;
	}

	@CheckRoleAccess(roleTypes={RoleType.FormAdmin,RoleType.Administrator})	
	public ModelAndView formdetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String)request.getParameter("id");
		String name = (String)request.getParameter("name");		
		HttpSession session = request.getSession();
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));
		InitFormObject ifobj = new InitFormObject();
		ifobj.setFormid(id);
		ifobj.setFormname(name);
		List forlst = null;
		session.setAttribute(IPagePublicParam.initformobj, ifobj);
		if(afapp!=null){
			session.setAttribute(IPagePublicParam.tempSapp, afapp);
			forlst = afapp.getFormList();
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(name);
              //08-05-14修改
	    	  afapp.setFId(Long.parseLong(id));
	    	  try{
	    		  afapp.loadFromDB();
				  session.setAttribute(IPagePublicParam.tempSapp, afapp);
				  forlst = afapp.getFormList();
			  }catch(Exception e){
					log.error("修改表单定义解析错误", e);
			  }finally{
				  afapp.unloadAppHibernatResorece();
			  } 
	    }			
		//返给前台显示
		//当点击此数据时，下面要有显示（与吴轩联调）
		SeeyonFormImpl sfi = (SeeyonFormImpl)forlst.get(0);
		InfoPath_FormView ifv = (InfoPath_FormView)sfi.getFviewList().get(0);
		ModelAndView mav = new ModelAndView("form/formcreate/formdetaillist");
		mav.addObject("formlst", forlst);
		String sheetname = (String)request.getParameter("sheetname");
		if(sheetname == null || "".equals(sheetname)||"null".equals(sheetname)){
			mav.addObject("sheetname", sfi.getFormName());
		}else{
			mav.addObject("sheetname", sheetname);
		}
		mav.addObject("formname", name);
		mav.addObject("formid", id);
		mav.addObject("design", "true");
		return mav;
	}	
	
	@CheckRoleAccess(roleTypes={RoleType.FormAdmin,RoleType.Administrator})	
	public ModelAndView creatformxml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String formoperation = (String)request.getParameter("formOperation");
        	response.setContentType("text/html; charset=UTF-8"); 
		request.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		String sheetname = request.getParameter("sheetname");
		String formname = request.getParameter("formname");
		String formid = request.getParameter("formid");
		//ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(formname);	
		//08-05-19修改
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formid));
		String returnstr = null;
		if(afapp!=null){
			returnstr = getIOperBase().returnViewStr(afapp,request,sheetname, "01");
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(formname);
//			08-05-19修改
			  afapp.setFId(Long.parseLong(formid));
			  try{
				  afapp.loadFromDB();
				  returnstr = getIOperBase().returnViewStr(afapp,request,sheetname, "01");
			  }catch(Exception e){
	  			  log.error("表单定义解析出错", e);
	 		  }finally{
	 			 afapp.unloadAppHibernatResorece();
			  } 	 
	    }	
		out.write(String.valueOf(returnstr));
		return null;
	}
	/**********************表单发布*****************************/	
	/**
	 * 表单发布
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formPublish(HttpServletRequest request,HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		//对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
		}
		String id = (String)request.getParameter("id");
		String isdel = (String)request.getParameter("isdelete");
		String state = (String)request.getParameter("state");
		String aAppName = (String)request.getParameter("aAppName");
		User user = CurrentUser.get();
		try{
			if(isdel != null){
				//发布需要清空数据
				getIOperBase().publishForm(Long.valueOf(id), Integer.valueOf(state), aAppName,true);
			}else{
				//发布和预发布不需要清空数据
				getIOperBase().publishForm(Long.valueOf(id), Integer.valueOf(state), aAppName,false);
			}
		}catch(SeeyonFormCheckException e){
			List<String> lst = new ArrayList<String>();
			List<SeeyonFormException> exceptionList = e.getList();
			for(SeeyonFormException e1 : exceptionList){
				lst.add(e1.getMessage());
			}	
			OperHelper.creatformmessage(request,response,lst);
		}
         //发布表单操作日志
		appLogManager.insertLog(user, AppLogAction.Form_Publish, user.getName(), aAppName);
//		long userId = CurrentUser.get().getId();
//		FormAppMain bm = new FormAppMain();
//		bm.setUserids(String.valueOf(userId));
//		List applst = getIOperBase().queryAllData(bm);
//		
//		ModelAndView mav = new ModelAndView("form/formcreate/formShow");
//		mav.addObject("applst", getIOperBase().assignCategory(applst));
//		return mav;
		return formMaker(request,response);
	}		
	/**********************表单删除*****************************/	
	
	
	public ModelAndView judgeuersdelect(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter(); 
		String idandnames = java.net.URLDecoder.decode(request.getParameter("formidname"), "UTF-8");
		List dellist = new ArrayList();
		if(idandnames.indexOf("↗") > -1){
		   String[] idname = idandnames.split("↗");
		   for(int i = 0 ; i < idname.length; i++){
			   if(idname[i].indexOf("↖") > -1){
				   String[] idandname = idname[i].split("↖");				   
				   String id = idandname[0];
				   String aAppName = idandname[1];
				   //ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(aAppName); 
				   //08年5月14日修改，注册唯一标识为id
				   ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));  
                   if(afapp !=null){
                	   List tablelst = new ArrayList();
    			   	   SeeyonDataDefine seeyondifine = (SeeyonDataDefine)afapp.getDataDefine();	
    				   DataDefine define = (DataDefine)seeyondifine.getDataDefine();
    				   tablelst = define.getTableLst();
    				   boolean sign = false;
    				   for(int j=0;j<tablelst.size();j++){
    							FormTable ftvv = (FormTable)tablelst.get(j);
    							if(ftvv.getName().indexOf("formmain") > -1){
    								Session runSession = null;
        							Statement stmt = null;
        							ResultSet rs = null;
        							Connection conn = null;
        					 		try {
        								runSession =  afapp.getSessionFactory().openSession();
        								String sql = "select count(*) from " + ftvv.getName();
        								conn = runSession.connection();
        								stmt = conn.createStatement();
        								rs = stmt.executeQuery(sql);
        								while(rs.next()){
        									if(rs.getInt(1) == 0)
        										sign = false;
        									else
        										sign = true;
        								}
        							}catch(Exception e){
        								//e.printStackTrace();
        								log.error("删除动态表出错", e);
        							}finally{
        								try{								
        									if(rs != null){
        										rs.close();
        										rs = null;
        									}
        									if(stmt != null){
        										stmt.close();
        										stmt = null;
        									}
        									if(conn != null){
        										conn.close();
        										conn = null;
        									}
        									if (runSession!=null){
        										runSession.close();
        										runSession=null;
        									}
        								}catch(Exception e){
        									//e.printStackTrace();
        									log.error("删除动态表出错", e);
        								}								
        							}	
    							}					
    				   }
    				   if(sign == true)
    				      dellist.add(aAppName+"已经被调用，产生数据，不能删除。");			   
    			   }
                   }				   
		   }
		}		
		StringBuffer returnstr = new StringBuffer();	
        
	    if(dellist.size() !=0){
	    	//returnstr = true;
	    	for(int i=0;i<dellist.size();i++){
	    		returnstr.append(dellist.get(i));
	    	}
	    	//response.setCharacterEncoding("UTF-8");
	    	out.write(String.valueOf(returnstr.toString()));
	    }else{
	    	returnstr.append("false");
	    	out.write(String.valueOf(returnstr.toString()));
	    }	
		return null;
	}
	
	/**
	 * 点击删除表单操作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formDelete(HttpServletRequest request,HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		//对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
		}
		String idandnames = request.getParameter("formidname");
		if(idandnames.indexOf("↗") > -1){
		   String[] idname = idandnames.split("↗");
		   for(int i = 0 ; i < idname.length; i++){
			   if(idname[i].indexOf("↖") > -1){
				   String[] idandname = idname[i].split("↖");				   
				   String id = idandname[0];
				   String aAppName = idandname[1];
				   FormDaoManager fDao = new FormDaoManagerImpl();
				   FormAppMain fAppDomain = null;			
					//根据表单名称读取数据库信息
				   //fAppDomain = fDao.findApplicationByName(SeeyonForm_Runtime.getInstance().getCharset().JDK2DBIn(aAppName));
				   fAppDomain = fDao.findApplicationById(Long.parseLong(id));
				   if(fAppDomain == null)
					   return mav;
				   getIOperBase().delForm(Long.valueOf(id),aAppName,fileManager);
				   //删除表单时同时删除表单模版附件记录
				   attachmentManager.deleteByReference(Long.valueOf(id));
			   }
		   }
		}
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
		//return formMakerback(request,response);
	}	
	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * checkFormField 用于表单字段修改时,数据字段的检验
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkFormFields(HttpServletRequest request,HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String checkStr = getIOperBase().checkFormFields(sessionobject, request);
		String number = request.getParameter("number");
		String isForList = request.getParameter("isForList");
		request.setAttribute("checkStr", checkStr);
		request.setAttribute("number", number);
		request.setAttribute("isForList", isForList);
		request.setAttribute("formState", sessionobject.getFormstate());
		String flowid = request.getParameter("flowid");
		List newflowidlist = new ArrayList();
		String url = "";
		List<Metadata> formMetadata=null;
		if("".equals(checkStr)){
			if("2".equals(number)){
				if(sessionobject.getEditflag() != null 
						&&!"".equals(sessionobject.getEditflag())
						&&!"null".equals(sessionobject.getEditflag())){
					if(sessionobject.getPageflag().equals(IPagePublicParam.BASEINFO)){
			            //baseinfo数据收集
						OperHelper.baseInfoCollectData(request,sessionobject);	
					}
				}		
				sessionobject.setPageflag(IPagePublicParam.INPUTDATA);		
				//向session对象中塞入系统变量和扩展绑定,系统枚举,应用枚举,表单枚举  		
				sessionobject = getIOperBase().systemenum(sessionobject);
				Collection collection = metadataManager.getAllUserDefinedMetadatasForForm();
				formMetadata = new ArrayList<Metadata>();
				if(null!=collection && collection.size()>0){
					formMetadata.addAll(collection);
				}
				url = "form/formcreate/inputData";
			}else if("3".equals(number)){
				if(sessionobject.getPageflag().equals(IPagePublicParam.INPUTDATA)){
		            //收集inputdata页面的数据,当跳转到operconfig时收集数据，在本页操作不再进行收集操作
					if(request.getParameter("saveoperlst") == null
						&&request.getParameter("deltype") == null
						&&request.getParameter("selenum") == null){
//						增加防护
						try{
						OperHelper.inputDataCollectData(request, sessionobject);
						}catch(SeeyonFormException e){
							log.error("保存录入定义页面信息时出错", e);
							List<String> lst = new ArrayList<String>();
							lst.add(e.getToUserMsg());
							OperHelper.creatformmessage(request,response,lst);
						}
					}
				}
				if(flowid == null || "".equals(flowid) || "null".equals(flowid)){
					List<FormFlowid> flowidlist= getIOperBase().getFlowidListbyformid(sessionobject.getFormid().toString());
				    for(int i=0;i<flowidlist.size();i++){
				    	FormFlowid formflowid = flowidlist.get(i);
				    	if("Y".equalsIgnoreCase(formflowid.getState()))
				    		newflowidlist.add(formflowid.getVariablename());   
				    }
				    sessionobject.setFlowidlist(newflowidlist);
				    sessionobject.setOldflowidlist(newflowidlist);
				}		
				sessionobject.setPageflag(IPagePublicParam.OPERCONFIG);
				url = "form/formcreate/operConfig";
			}else if("4".equals(number)){
				sessionobject.setPageflag(IPagePublicParam.QUERYSET);
				url = "form/formquery/query_queryset";			
			}else if("5".equals(number)){
				sessionobject.setPageflag(IPagePublicParam.REPORTSET);	
				url = "form/formreport/stat_statset";
			}else if("6".equals(number)){
				sessionobject.setPageflag(IPagePublicParam.BINDINFO) ;
				return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
			}else if("7".equals(number)){//触发设置
				sessionobject.setPageflag(IPagePublicParam.TRIGGERSET) ;
				return super.redirectModelAndView("/triggerController.do?method=formTriggerSet");
			}else{
				sessionobject.setPageflag(IPagePublicParam.BASEINFO);
				url = "form/formcreate/baseInfo";
			}		
		}else{
			sessionobject.setPageflag(IPagePublicParam.BASEINFO);
			url = "form/formcreate/baseInfo";
		}	
		String flowname = "";
		for(int i =0;i<sessionobject.getFlowidlist().size();i++){
			if(i<sessionobject.getFlowidlist().size()-1)
			  flowname += (String)sessionobject.getFlowidlist().get(i) +"↗";	
			else
			  flowname += (String)sessionobject.getFlowidlist().get(i);
		}
		
		
		ModelAndView mav = new ModelAndView(url);
        //获得所属分类下拉列表中的数据
		Map<String, Metadata> appMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.form);
		mav.addObject("appMeta", appMeta);
		mav.addObject("formMetadata", formMetadata);
		StringBuffer categoryHTML = new StringBuffer();
		categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
		mav.addObject("categoryHTML", categoryHTML);
        if("3".equals(number))
		   mav.addObject("flowid", flowname);
		return mav;
	}
	
	public ModelAndView helpPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/Help");
		return mav;
		
	}
	public ModelAndView helpformPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/formHelp");
		return mav;
		
	}
	public ModelAndView dataMatchHelp(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/datamatchhelp");
		return mav;
		
	}
	
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView openaddtext(HttpServletRequest request,HttpServletResponse response) throws Exception{
		super.noCache(response);
		ModelAndView mav = new ModelAndView("form/formcreate/addtextarea");
		String inputname = (String)request.getParameter("inputname");
		String fieldlength = (String)request.getParameter("fieldlength");
		String username= "";
		Date date = new Date() ;
		User fnowUser = CurrentUser.get();
		if (fnowUser != null)
			username = " "+"["+fnowUser.getName()+ " "+Datetimes.format(date, "yyyy-MM-dd HH:mm") +"]";
		mav.addObject("inputname", inputname); 
		mav.addObject("fieldlength", fieldlength);
		mav.addObject("username", username);  
		return mav;
		
	}
	
	/**
	 * 产生验证码进行验证
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView affirmValidDataCode(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/affirmValidDataCode");
		return mav;
	}

	public ModelAndView selectFormAdmin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formcreate/selectformadmin");
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		V3xOrgRole formRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_FORMADMIN);
		List<V3xOrgMember> v3xorgmemlist = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT,formRole.getOrgAccountId(),formRole.getId());
		mav.addObject("v3xorgmemlist", v3xorgmemlist);
		return mav;
	}

	
	/**
	 * 修改点击另存为时，保存所有信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView othereditSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String thispage = (String)request.getParameter("thispage");
		String flag = (String)request.getParameter("flag");
		flag = "true"; 
		String url = null; 
		if(IPagePublicParam.BASEINFO.equalsIgnoreCase(thispage)
				||IPagePublicParam.INPUTDATA.equalsIgnoreCase(thispage)
				||IPagePublicParam.OPERCONFIG.equalsIgnoreCase(thispage)){
			url = "form/formcreate/formcreateBorderFrame";
		}
		Long sort = 0l;
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		try {
		    
		if(sessionobject == null)
			return formMaker(request,response);	
		FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
		FormOwnerList fol = new FormOwnerList();
		fol.setAppmainId(sessionobject.getFormid());
		fol.setOwnerId(CurrentUser.get().getId());
		List formownerlst = formDaoManager.queryOwnerListByCondition(fol);
	    if(formownerlst.size() ==0)
	    	throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.formauth.label"),Constantform.getString4CurrentUser("form.base.formauth.label"));
		
	    String formname = (String)request.getParameter("formname");
		sessionobject.setFormEditName(formname);
		if(!sessionobject.getFormName().equals(sessionobject.getFormEditName()))
			getIOperBase().isExistsThisForm(sessionobject.getFormEditName());
		else
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.tablenameisexist.label", formname),Constantform.getString4CurrentUser("form.base.tablenameisexist.label", formname));
		if(sessionobject.getEnumnamemap().size() !=0){
			sessionobject.getOldenumnamemap().clear();
			HashMap hash =  sessionobject.getEnumnamemap();
    		Iterator it = hash.entrySet().iterator();
    		while(it.hasNext()){
    			Map.Entry entry = (Map.Entry)it.next();
    			String enumname = entry.getKey().toString();
    			sessionobject.getOldenumnamemap().put(enumname, enumname);
    		}
		}
		isExistscategoryThisForm(sessionobject.getFormsort());
		getIOperBase().othereditSave(sessionobject,request,fileManager);
		sessionobject.setFormName(sessionobject.getFormName());	
		sort = sessionobject.getFormsort();
		getIOperBase().formenumeditifuse(sessionobject);
		//另存为时保存infopath attachment
		try{
			otherSaveAttmachment(Long.valueOf(request.getParameter("oldFormid")), sessionobject.getFormid(), request);
		} catch (Exception e){
			log.error("保存infopath attachment 失败！", e);
		}
        //提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"+Constantform.getString("formapp.saveoperok.label")+"')");
		out.println("</script>");
		out.flush();
		}catch (Exception e) {
			log.error("表单另存为失败！",e);
			sessionobject.setFormEditName(null);
			List<String> lst = new ArrayList<String>();
			lst.add(Constantform.getString("formapp.saveoperfail.label") + "," + e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
		}

		ModelAndView mav = new ModelAndView(url);			
        //获得所属分类下拉列表中的数据
		Map<String, Metadata> appMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.form);
		StringBuffer categoryHTML = new StringBuffer();
		categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
		mav.addObject("categoryHTML", categoryHTML);
		mav.addObject("appMeta", appMeta);
		mav.addObject("sort",sort);
		mav.addObject("flag", flag);
		long userId = CurrentUser.get().getId();
		mav.addObject("userId", userId);
		mav.addObject("formType", sessionobject.getFormType());
		return mav;
		
	}
	/**
	 * 点击另存为时保存infopath附件 by Meixd 2010-10-26
	 * @param oldFormid
	 * @throws Exception
	 */
	private void otherSaveAttmachment(Long oldFormid, Long newFormId, HttpServletRequest request) throws Exception{
	    HttpSession session = request.getSession();
        SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
	    List<Attachment> attList = attachmentManager.getByReference(oldFormid);
        Attachment attachment = null;
        Attachment newAttachment = null;
        List<Attachment> newAttList = null;
        Date date = new Date();
        Long newId = UUIDLong.longUUID();
        if(sessionobject.getAttachment() == null){
            if(attList != null && attList.size() > 0){
                attachment = attList.get(0);
                newAttachment = new Attachment();
                newAttachment.setCategory(attachment.getCategory());
                newAttachment.setType(attachment.getType());
                newAttachment.setReference(newFormId);
                newAttachment.setSubReference(newFormId);  
                newAttachment.setFilename(attachment.getFilename());
                newAttachment.setMimeType(attachment.getMimeType());
                newAttachment.setCreatedate(date);
                newAttachment.setSize(attachment.getSize());
                newAttachment.setFileUrl(newId);
                newAttachment.setSort(attachment.getSort());
                newAttList = new ArrayList<Attachment>();
                newAttList.add(newAttachment);
                this.fileManager.clone(attachment.getFileUrl(), attachment.getCreatedate(), newId, date);
            }else{
                return;
            }
        }
        if(attList != null && attList.size() > 0){
            attachment = attList.get(0);
            newAttachment = new Attachment();
            newAttachment.setCategory(attachment.getCategory());
            newAttachment.setType(attachment.getType());
            newAttachment.setReference(newFormId);
            newAttachment.setSubReference(newFormId);  
            newAttachment.setFilename(attachment.getFilename());
            newAttachment.setMimeType(attachment.getMimeType());
            newAttachment.setCreatedate(date);
            newAttachment.setSize(attachment.getSize());
            newAttachment.setFileUrl(newId);
            newAttachment.setSort(attachment.getSort());
            newAttList = new ArrayList<Attachment>();
            newAttList.add(newAttachment);
            this.fileManager.clone(attachment.getFileUrl(), attachment.getCreatedate(), newId, date);
        }else{
            newAttachment = new Attachment();
            newAttachment.setCategory(sessionobject.getAttachment().getCategory());
            newAttachment.setType(sessionobject.getAttachment().getType());
            newAttachment.setReference(newFormId);
            newAttachment.setSubReference(newFormId);  
            newAttachment.setFilename(sessionobject.getAttachment().getFilename());
            newAttachment.setMimeType(sessionobject.getAttachment().getMimeType());
            newAttachment.setCreatedate(date);
            newAttachment.setSize(Long.valueOf(896444));
            newAttachment.setFileUrl(newId);
            newAttachment.setSort(sessionobject.getAttachment().getSort());
            newAttList = new ArrayList<Attachment>();
            newAttList.add(newAttachment);
            this.fileManager.clone(sessionobject.getAttachment().getFileUrl(), sessionobject.getAttachment().getCreatedate(), newId, date);
        }
        this.attachmentManager.create(newAttList);
	}
	
	
	public ModelAndView formstopBorderFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formstop/formStopFrame");
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
    }
	
	public ModelAndView formstopMaker(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formstop/formstopBorderFrame");
		mav.addObject("formType", request.getParameter("formType"));
		return mav;
	}
	
	public ModelAndView formstop(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		ModelAndView mav = new ModelAndView("form/formstop/formStop");
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		//对session进行清理
		if(sessionobject != null){
			session.removeAttribute("SessionObject");
		}
		
		long userId = CurrentUser.get().getId();
		FormAppMain bm = new FormAppMain();
		FormAppMain bmnew = new FormAppMain();
		bm.setUserids(String.valueOf(userId));
		bmnew.setUserids(String.valueOf(userId));
		String applicationValue = request.getParameter("state");
		String formmanid =  request.getParameter("formmanid");
		String selectquery = request.getParameter("selectquery");
		String state = request.getParameter("pageformname");
		String formAppName = request.getParameter("formAppName");
		String formType = request.getParameter("formType");
		if(!"0".equals(selectquery)){
            //按应用查询
			if(applicationValue != null && !"".equals(applicationValue) && !"null".equals(applicationValue)){
				bm.setCategory(Long.valueOf(applicationValue));
			}		
			//从页面取得状态名称的值
			Locale local = LocaleContext.getLocale(request);
			String resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
			String state1 = ResourceBundleUtil.getString(resource, local, "form.query.allstate.label");
			String formmanid1 = ResourceBundleUtil.getString(resource, local, "form.formadmin.label");
			if(state != null && !"".equals(state) && !"null".equals(state)){
				if(!state.equals(state1))
				  bm.setState(Integer.parseInt(state));
			}
			if(Strings.isNotBlank(formAppName)){
				if(bm.getCategory().longValue() != 0){
					bm.setCategory(Long.valueOf(0)) ;
				}
				bm.setName(formAppName) ;
			}
			/*if(formmanid != null && !"".equals(formmanid) && !"null".equals(formmanid)){
				if(!formmanid.equals(formmanid1))
				  bm.setUserids(formmanid);
				else
				  bm.setUserids("");
			}*/
		}
		bm.setFormstart(0);
		bm.setFormType(Integer.parseInt(formType));
		List applst = null;
		//List formNamelistnew = null;
		try {
			applst = getIOperBase().queryAllData(bm);
			applst = getIOperBase().assignCategory(applst);
			mav.addObject("applst",  getIOperBase().pagenate(applst));	
			//获取表单名称列表
			//List<String> formNameList = new ArrayList<String>();
			//formNamelistnew = getIOperBase().queryAllData(bmnew);
			//formNameList = genFormNameList(formNamelistnew);		
			//mav.addObject("formNameList", formNameList);
			 // 获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			mav.addObject("appMeta", appMeta);
			mav.addObject("state", state);
			mav.addObject("applicationValue", applicationValue);
			StringBuffer categoryHTML = new StringBuffer();
			categoryHTML = getIOperBase().categoryHTML(templeteCategoryManager);
			mav.addObject("categoryHTML", categoryHTML);
		}catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			OperHelper.creatformmessage(request,response,lst);
		}
			mav.addObject("userId", userId);
			mav.addObject("applicationValue", applicationValue);
			mav.addObject("selectquery", selectquery);
			mav.addObject("state", state);
			mav.addObject("formAppName", formAppName);
			mav.addObject("formType", formType);
			return mav;
		
	}
	//将表单停用
	public ModelAndView stopbyformstart(HttpServletRequest request,
			HttpServletResponse response) throws SeeyonFormException {		
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");		
		if(sessionobject == null){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject",sessionobject);
		}
		//String id = (String)request.getParameter("id");
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		User user = CurrentUser.get();
		String idandnames = request.getParameter("formdisableId");
		if(idandnames.indexOf("↗") > -1){
		   List<String[]> labelsList= new ArrayList<String[]>();
		   String[] idname = idandnames.split("↗");
		   for(int i = 0 ; i < idname.length; i++){
			   if(idname[i].indexOf("↖") > -1){
				   String[] idandname = idname[i].split("↖");				   
				   String id = idandname[0];
				   String aAppName = idandname[1];
				   ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));
				   int state=0;
					if(afapp!=null){
						SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) afapp;
						state = 2;
						SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
					    HashMap flowMap = new LinkedHashMap();
					    for(int j =0;j< seeformbind.getFlowTempletList().size();j++){
					       FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(j);
					       flowMap.put(flow.getId(), flow);
					    }
					    if(seeformbind.getFlowTempletList().size()!=0){
					        TemplateObject temobj= new TemplateObject();
					        temobj.setFlowMap(flowMap);
					        sessionobject.setTemplateobj(temobj);
					    }
						session.setAttribute("SessionObject", sessionobject);
					}else if(afapp == null){
						  afapp = new SeeyonForm_ApplicationImpl();
						  afapp.setAppName(aAppName);
						  //08-05-15日修改将id作为注册信息标识
						  afapp.setFId(Long.parseLong(id));
						  afapp.loadFromDB();
						  SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) afapp;
						  state = 0;
						  SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
						    HashMap flowMap = new HashMap();
						    for(int j =0;j< seeformbind.getFlowTempletList().size();j++){
						       FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(j);
						       flowMap.put(flow.getId(), flow);
						    }
						    if(seeformbind.getFlowTempletList().size()!=0){
						        TemplateObject temobj= new TemplateObject();
						        temobj.setFlowMap(flowMap);
						        sessionobject.setTemplateobj(temobj);
						    }
						  session.setAttribute("SessionObject", sessionobject);
						  afapp.unloadAppHibernatResorece();
				    }
					getIOperBase().updateByformstart(Long.parseLong(id),0,sessionobject);
					
					//停用表单时  同步删除表单触发设置中已触发的调度
					for(FormEvent event : ((SeeyonForm_ApplicationImpl)afapp).getTriggerConfigList()){
						EventTriggerForHistoryData.removeTriggerForHistoryData(Long.parseLong(id),event.getId());
					}
					
					if(state==1 || state==2){
						if(fmanager.findById(Long.parseLong(id),true) != null){
							   fmanager.unRegApp(Long.parseLong(id));
						}
						SeeyonForm_ApplicationImpl newSapp = new SeeyonForm_ApplicationImpl();
						newSapp.setAppName(aAppName);
						newSapp.setFId(Long.parseLong(id));
						newSapp.loadFromDB();
						if(fmanager.findById(Long.parseLong(id),true) == null)
							fmanager.regApp(newSapp);
					}
					
					String[] label = new String[2];
					label[0] = user.getName();
					label[1] = aAppName;
					labelsList.add(label);
			   }
		   }
//		 停用表单操作日志
	        appLogManager.insertLogs(user, AppLogAction.Form_Stop,labelsList);
		}

		ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
		long userId = CurrentUser.get().getId();
		mav.addObject("userId", userId);
		mav.addObject("formType", request.getParameter("formType"));
		return mav;		
	}
	
    //	将表单启用
	public ModelAndView startbyformstart(HttpServletRequest request,
			HttpServletResponse response) throws SeeyonFormException {		
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");		
		if(sessionobject == null){
			sessionobject = new SessionObject();
			session.setAttribute("SessionObject",sessionobject);
		}
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		//String id = (String)request.getParameter("id");
		User user = CurrentUser.get();
		String idandnames = request.getParameter("formenableId");	
		
		if(idandnames.indexOf("↗") > -1){
		   List<String[]> labelsList= new ArrayList<String[]>();
		   String[] idname = idandnames.split("↗");
		  
		   for(int i = 0 ; i < idname.length; i++){
			   if(idname[i].indexOf("↖") > -1){
				   String[] idandname = idname[i].split("↖");				   
				   String id = idandname[0];
				   String aAppName = idandname[1];
				   ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));
				   int state=0;
				   if(afapp!=null){
						SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) afapp;
						state = 2;
						SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
					    HashMap flowMap = new LinkedHashMap();
					    for(int j =0;j< seeformbind.getFlowTempletList().size();j++){
					       FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(j);
					       flowMap.put(flow.getId(), flow);
					    }
					    if(seeformbind.getFlowTempletList().size()!=0){
					        TemplateObject temobj= new TemplateObject();
					        temobj.setFlowMap(flowMap);
					        sessionobject.setTemplateobj(temobj);
					    }
						session.setAttribute("SessionObject", sessionobject);
					}else if(afapp == null){
						  afapp = new SeeyonForm_ApplicationImpl();
						  afapp.setAppName(aAppName);
						  //08-05-15日修改将id作为注册信息标识
						  afapp.setFId(Long.parseLong(id));  
						  try{
							  afapp.loadFromDB();
							  SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) afapp;
							  state = 0;
							  SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
							    HashMap flowMap = new HashMap();
							    for(int j =0;j< seeformbind.getFlowTempletList().size();j++){
							       FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(j);
							       flowMap.put(flow.getId(), flow);
							    }
							    if(seeformbind.getFlowTempletList().size()!=0){
							        TemplateObject temobj= new TemplateObject();
							        temobj.setFlowMap(flowMap);
							        sessionobject.setTemplateobj(temobj);
							    }
							  session.setAttribute("SessionObject", sessionobject);
						  } catch(Exception e){
							  log.error("修改表单定义解析错误", e);
			 			  }finally{
			 				 afapp.unloadAppHibernatResorece();
			 			  }	   
				    }
				   getIOperBase().updateByformstart(Long.parseLong(id),1,sessionobject);

					//启用表单时  同步新建表单触发设置中的调度
					try {
						for(FormEvent event : ((SeeyonForm_ApplicationImpl)afapp).getTriggerConfigList()){
							EventTriggerForHistoryData.addTriggerForHistoryData(Long.parseLong(id),event.getId());
						}
					} catch (Exception e) {
						throw new SeeyonFormException(-1,"",e);
					}
					
					if(state==1 || state==2){
						if(fmanager.findById(Long.parseLong(id),true) != null){
							   fmanager.unRegApp(Long.parseLong(id));
						}
						SeeyonForm_ApplicationImpl newSapp = new SeeyonForm_ApplicationImpl();
						newSapp.setAppName(aAppName);
						newSapp.setFId(Long.parseLong(id));
						newSapp.loadFromDB();
						if(fmanager.findById(Long.parseLong(id),true) == null)
							fmanager.regApp(newSapp);
					}
					
				   String[] label = new String[2];
				   label[0] = user.getName();
				   label[1] = aAppName;
				   labelsList.add(label);
                   
			   }
		   }
//		 启用表单操作日志
	        appLogManager.insertLogs(user, AppLogAction.Form_Start,labelsList);
		}		
		//ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
		ModelAndView mav = new ModelAndView("form/formstop/formStopFrame");
		long userId = CurrentUser.get().getId();
		mav.addObject("userId", userId);
		mav.addObject("formType", request.getParameter("formType"));
		return mav;		
	}
	
	//表单手写控件所有js变量
	@SetContentType
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView getHWjsStr(HttpServletRequest request,
			HttpServletResponse response) throws SeeyonFormException {
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();
		String userName=CurrentUser.get().getName();
		StringBuffer szTemp=new StringBuffer();
				
		szTemp.append("webRoot=\"").append(basePath).append("\";").append("\r\n");			
		szTemp.append("htmOcxUserName=\"").append(Functions.toHTML(userName)).append("\";").append("\r\n");
		szTemp.append("hwVer=\"").append(iMsgServer2000.Version("iWebSignature")).append("\";").append("\r\n");
		szTemp.append("fphrase=\"").append(Java2JavaScriptStr(getPhraseContent())).append("\";").append("\r\n");
		
		byte[] returnstr;
		try {
			returnstr = szTemp.toString().getBytes("UTF-8");
			response.getOutputStream().write(returnstr);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
		} catch (Exception e) {
          // e.printStackTrace();
			log.error("手写控件js变量出错", e);
		}
		
		
		return null;
	}
	
//	取得常用语的字符串
	private String getPhraseContent(){
		StringBuffer sb = new StringBuffer();
		DetachedCriteria criteria = DetachedCriteria.forClass(CommonPhrase.class)
									.add(Expression.eq("accountId", CurrentUser.get().getLoginAccount()))
									.add(Expression.or(
											Expression.eq("memberId", CurrentUser.get().getId()),
											Expression.eq("type", CommonPhrase.PHRASE_TYPE.system.ordinal())
									))
									.addOrder(Order.asc("createDate"));
		CommonPhraseManager phraseManager = (CommonPhraseManager)ApplicationContextHolder.getBean("phraseManager");
		List<CommonPhrase> phrases = (List<CommonPhrase>)phraseManager.executeCriteria(criteria, -1, -1);
		for(CommonPhrase item : phrases){
			sb.append(item.getContent());
			sb.append("\\r\\n");
		}
		return sb.toString();
	}
	
	 private final String Java2JavaScriptStr(String aValue){
		 if (aValue == null || aValue.length() <= 0)
		      return aValue;
		    StringBuffer result = new StringBuffer(aValue.length()+10);
		    char ftemp;
		    for (int i = 0; i < aValue.length(); i++) {
		      ftemp = aValue.charAt(i);
		      switch (ftemp) {
		      case 34:
		          result.append("\\\"");
		          break;
		      default:
		          if (ftemp >= 0 && ftemp <= 9)
		            result.append("\\u000" + ( (int) ftemp));
		          else if (ftemp >= 10 && ftemp <= 15)
		            result.append("\\u000" + (char)(ftemp - 10 + 'A'));
		          else if (ftemp > 15 && ftemp <= 25)
		            result.append("\\u001" + (int)(ftemp - 16 ));
		          else if (ftemp > 25 && ftemp <= 31)
		            result.append("\\u001" + (char)(ftemp - 26 + 'A'));
		          else
		            result.append(ftemp);
		          break;
		      }
		    }
		    return result.toString();
	 }
	 
	 /**
	  * 专用于保存表单时，判断所属应用是否被删除。
	  * @param name
	  * @return
	  * @throws DataDefineException
	  */
	private boolean isExistscategoryThisForm(Long category) throws DataDefineException{
		User user = CurrentUser.get();
		long orgAccountId = user.getLoginAccount();
        String sign = "false";
		//List<TempleteCategory> templeteCategories = templeteCategoryManager.getCategorys(orgAccountId, 4);
        List<TempleteCategory> templeteCategories = templeteCategoryManager.getCategorys(orgAccountId, 4);
		templeteCategories.addAll(templeteCategoryManager.getCategorys(orgAccountId, 0));

		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory temcategory = templeteCategories.get(i);
			if(temcategory.getId().longValue() == category.longValue())
				sign = "true";
		}
		
		if("false".equals(sign)){
			//throw new DataDefineException(1,"您选择的所属应用不存在或已经被管理员删除，请重新选择。","您选择的所属应用不存在或已经被管理员删除，请重新选择。");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.categoryisdel.label"),Constantform.getString4CurrentUser("form.categoryisdel.label"));
		}else{
			return true;
		}

	}
		
		public ModelAndView dateDifferSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/dateDiffer");    
			return mav; 
			           
		}
		
		/**
		 * 对计算字段从页面取到值后，并返回父页面
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView computeDate(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/computeDate");
			return mav;
		}
		
		public ModelAndView dateCalcSet(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/dateCalc");
			return mav;
		}	
		
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView formheadpage(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formhead/formheadpage");
			return mav;
		} 
		/*
		 * 另存时打开新页面
		 */
		public ModelAndView formohterPage(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/formotherpage");
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
			int formType = sessionObject.getFormType();
			List<QueryObject> queryList = sessionObject.getQueryConditionList();
			List<ReportObject> reportList = sessionObject.getReportConditionList();
			if(formType==ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){
				List<FlowTempletImp> temList = new ArrayList<FlowTempletImp>();
				TemplateObject temobject= sessionObject.getTemplateobj();
				if(temobject !=null){
					HashMap hash = temobject.getFlowMap();
					Iterator it = hash.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						FlowTempletImp fotemimp = (FlowTempletImp)entry.getValue();
						temList.add(fotemimp);
					}
				}
				mav.addObject("temList", temList);
			}else if(formType==ISeeyonForm.TAppBindType.BASEDATA.getValue()){
				Collection<FormAppAuthObject> authColl = sessionObject.getFormAppAuthObjectMap().values();
				List<Map<String,String>> temList = new ArrayList<Map<String,String>>();
		        for (FormAppAuthObject authObject : authColl) {
		    		Map<String,String> map = new HashMap<String,String>();
		    		map.put("id", authObject.getId());
		    		temList.add(map);
		        }
		        mav.addObject("temList", temList);
			}
			mav.addObject("queryList", queryList).addObject("reportList", reportList);
			return mav;
		} 
		

		 /**
		  * 专用于修改表单时，判断所属人是否被修改
		  * @param name
		  * @return
		  * @throws DataDefineException
		  */			
		public ModelAndView isExistsowneridThisForm(HttpServletRequest request,
				HttpServletResponse response) throws Exception {	
			PrintWriter out = response.getWriter(); 
			User user = CurrentUser.get();
			String formappid = java.net.URLDecoder.decode(request.getParameter("formappid"), "UTF-8");
			FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
			FormOwnerList fol = new FormOwnerList();
			fol.setAppmainId(Long.parseLong(formappid));
			fol.setOwnerId(user.getId());
			List formownerlst = formDaoManager.queryOwnerListByCondition(fol);
		
			StringBuffer returnstr = new StringBuffer();	
	        
		    if(formownerlst.size() !=0){
		    	returnstr.append("true");
		    	out.write(String.valueOf(returnstr.toString()));
		    }else{
		    	returnstr.append("false");
		    	out.write(String.valueOf(returnstr.toString()));
		    }	
			return null;
		}
		
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView	fileUpload4Form(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/selectUploadType") ;
			return mav ;
		}
		
		@CheckRoleAccess(roleTypes=RoleType.Administrator)
		public ModelAndView showFlow(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			ModelAndView mav = new ModelAndView("form/formapp/showFlowFrame") ;
			
			return mav ;				
		}
		@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
		public ModelAndView formExtendHRInfo(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			PrintWriter out = response.getWriter(); 
			out.write("{\"refInputAtt\":\"" + ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources", "hr.staffInfo.name.label") + "\"}");
			return null ;				
		}
		
		@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
		public ModelAndView formExtendHRInfoList(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			ModelAndView mav = new ModelAndView("form/formcreate/formExtendHrInfoList") ;
			RelationInputManager relationInputManager =	SeeyonForm_Runtime.getInstance().getRelationInputManager() ;
			Map<String,RelationInputFieldInterface> map = relationInputManager.getAllRelationInputField() ;
			HrStaffInfoField  hrField = (HrStaffInfoField)map.get(RelationInputFieldInterface.HRSTAFFINFO) ;
			if(hrField == null){
				return null ;
			}
			Map<String,RelationInputField> allInitList = hrField.getAllInitList() ;
			if(allInitList == null){
				return null ;
			}
			mav.addObject("refParamList", allInitList.values()) ;
			return mav ;				
		}
			
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView getFinishColOrAppForm(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			//System.out.print("aaaaaaaaaa") ;
			String formId = request.getParameter("formId") ;
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
	    	
			if(fapp.getFormType() == ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){
				return super.redirectModelAndView("/collaboration.do?method=getFinishColFormForm&formId="+formId+"&fromQuery="+fromQuery);
			}else if(fapp.getFormType() == ISeeyonForm.TAppBindType.INFOMANAGE.getValue() || 
					fapp.getFormType() == ISeeyonForm.TAppBindType.BASEDATA.getValue()){
				return super.redirectModelAndView("/appFormController.do?method=getappfromrefFrame&formId="+formId+"&fromQuery="+fromQuery);
			}
			
			return null ;
		}
		
	@CheckRoleAccess(roleTypes = RoleType.NeedNoCheck)
	public List<String> hasAuthorityToUpdate(String[] apps) throws Exception {
		if (apps == null) {
			return null;
		}
		FormDaoManager formDaoManager = (FormDaoManager) SeeyonForm_Runtime
				.getInstance().getBean("formDaoManager");
		FormOwnerList fol = new FormOwnerList();
		List<String> result = new ArrayList<String>();
		User user = CurrentUser.get();
		fol.setOwnerId(user.getId());
		for (String app : apps) {
			int lenSplit = app.indexOf("|");
			String formappid = "";
			if (lenSplit <= -1) {
				continue;
			}
			formappid = app.substring(0, lenSplit);
			fol.setAppmainId(Long.parseLong(formappid));
			List formownerlst = formDaoManager.queryOwnerListByCondition(fol);

			if (formownerlst.size() == 0){
				result.add(app);
			}
		}
//		Map<String,List> resultMap = new HashMap<String,List>();
//		resultMap.put("result", result);
		return result;
	}
		 @CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		 public Map<String,String[]> getFormFileValue(String value ,String[] filedNames, String extendClassName,String formAppId)throws Exception {
			
			 if(Strings.isBlank(extendClassName) || Strings.isBlank(value)){
				 return null ;
			 }
			 if(filedNames == null || filedNames.length <= 0){
				 return null ;
			 }

			List<String> list = new ArrayList<String>() ;
			for(String str :filedNames ){
				list.add(str) ;
			}
			IInputExtendManager inputExtendManager = SeeyonForm_Runtime.getInstance().getInputExtendManager() ;
			ISeeyonInputExtend fvalue = inputExtendManager.findByName(extendClassName) ;
			if(fvalue == null){
				return null ;
			}
			if(fvalue instanceof IInputRelation){
				IInputRelation inputRelation  = (IInputRelation)fvalue ;
				Map<String,DisplayValue> displayValueMap = inputRelation.getRelationValues(list,formAppId,value) ;
				if(displayValueMap == null || displayValueMap.isEmpty()){
					return null ;
				}
				Map<String,String[]> map = new HashMap<String,String[]>();
				for(String str : displayValueMap.keySet()){
					DisplayValue displayValue= displayValueMap.get(str) ;
					if(displayValue == null){
						continue ;
					}
					if(displayValue.getDisplay() == null){
						displayValue.setDisplay(displayValue.getValue()) ;
					}
					map.put(str, new String[]{displayValue.getDisplay(),displayValue.getValue()});
				}
				return map;
			}
			return null;
		}
		 
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public Map<String,String> getInputExtendAttribute(String extendClassName, String refInputAttr, String formAppId)throws Exception {
			if(Strings.isNotBlank(extendClassName)){
				extendClassName = FormHelper.getRelationAttrExtendType(extendClassName,refInputAttr,formAppId);
				
				if(Strings.isNotBlank(extendClassName)){
					if("text".equals(extendClassName)){
						Map<String,String> map = new HashMap<String,String>();
						map.put("type", "text");
						return map;
					}
					IInputExtendManager inputExtendManager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
					ISeeyonInputExtend fvalue = inputExtendManager.findByName(extendClassName);
					if(fvalue != null){
						Map<String,String> map = new HashMap<String,String>();
						map.put("type", extendClassName);
						//要打开的页面
						String strUrl;
						String page = fCurrentCharSet.JDK2SelfXML(fvalue.getInputOnCilikURL(TviewType.vtHtml));   
						if(page == null || page.equals("")){
							strUrl = "";
						}else{
							page = page.replaceAll("&amp;","&");
							//时间空间
							Boolean f = (Boolean)(BrowserFlag.SelectPeople.getFlag(CurrentUser.get()));
							if(fvalue instanceof InputExtend_ExampleDatetime){
								InputExtend_ExampleDatetime fdatetime = (InputExtend_ExampleDatetime)fvalue;
								page = page +"?type=" +fdatetime.getType();
								if(f){
								strUrl = "window.showModalDialog('" + 
								 page + 
								 "',window,'dialogHeight:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowHeight(TviewType.vtHtml)) + "px" + 
								 ";dialogWidth:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowWidth(TviewType.vtHtml)) + "px" + "')";
								}else{
									strUrl = page;
								}
							}else if(fvalue instanceof InputExtend_SelectColSummay){
								//流程空间
									page = page+formAppId ;
									if(f){
									strUrl = "window.showModalDialog('" + 
										page + 
										"',window,'dialogHeight:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowHeight(TviewType.vtHtml)) + "px" + 
										";dialogWidth:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowWidth(TviewType.vtHtml)) + "px" + "')";
									}else{
										strUrl = page;
									}
							}else{
								//其他
								if(f){
								strUrl = "window.showModalDialog('" + 
								 page + 
								 "',window,'dialogHeight:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowHeight(TviewType.vtHtml)) + "px" + 
								 ";dialogWidth:" + fCurrentCharSet.JDK2SelfXML(fvalue.getWindowWidth(TviewType.vtHtml)) + "px" + "')";
								}else{
									strUrl = page;
								}
							}			
						}
						map.put("url", strUrl);
						//要显示的图标
						String strSrc = fCurrentCharSet.JDK2SelfXML(fvalue.getInputImage(TviewType.vtHtml));
						map.put("src", strSrc);
						return map;
					}
				}
			}
			return null;	
		}
			
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public boolean checkFormCode(String fappId, String formCode, int formType) throws DataDefineException{
			return BindHelper.checkFormCode(fappId, formCode, formType);
		}
		

		public ModelAndView newshowFormTemplets(HttpServletRequest request, HttpServletResponse response) throws Exception {
			ModelAndView mav=new ModelAndView("formbizconfig/write/show_bizconfig_formapp");
			iOperBase.setTempleteCategoryManager(templeteCategoryManager);
			String categoryHTML=iOperBase.categoryHTML(templeteCategoryManager).toString();
			mav.addObject("categoryAppHTML", categoryHTML);
			return mav;
		}
		
		/**
		 * 返回该登录用户能看到的所有的业务表单
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView showAppFormTree(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("formbizconfig/write/show_bizconfig_formapp_tree");
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
					if(fappImpl.getFormstart()==1){
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
									boolean flag = false;
									if(fappImpl.getFormType() == ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){
										ISeeyonFormBind bind =  fappImpl.getSeeyonFormBind();
										List<IFlowTemplet> templetList = bind.getFlowTempletList();
										if(templetList != null){
											for(IFlowTemplet templet: templetList){
												if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
													if(templet.getName().indexOf(textfield)==-1) continue;
												}
												FormApp formAppObject = new FormApp();
												formAppObject.setId(String.valueOf(templet.getId()));
												formAppObject.setName(templet.getName());
												formAppObject.setAppFormId(String.valueOf(fappImpl.getId()));
												formAppObject.setSourceType(fappImpl.getFormType());
												list.add(formAppObject);
												flag = true;
											}
										}
									}else{
										List<FormAppAuth> formAppAuthList = fappImpl.getFormAppAuthList();
										if (formAppAuthList != null) {
											for (FormAppAuth formAppAuth : formAppAuthList) {
												if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
													if(formAppAuth.getName().indexOf(textfield)==-1) continue;
												}
												FormApp formAppObject = new FormApp();
												formAppObject.setId(formAppAuth.getId());
												formAppObject.setName(formAppAuth.getName());
												formAppObject.setAppFormId(String.valueOf(fappImpl.getId()));
												formAppObject.setSourceType(fappImpl.getFormType());
												list.add(formAppObject);
												flag = true;
											}
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
			}
			mav.addObject("templeteCategories", templeteCategories);
			mav.addObject("categoryAndFromMap", categoryAndFromMap);
			return mav;
		}
		public Map<String,String> downloadFormTemplete(Long id) throws Exception{
			List<Attachment>  list = attachmentManager.getByReference(id);
			Map<String,String> result = new HashMap();
			if(list != null && !list.isEmpty()){
				Attachment attachment = list.get(0);
				result.put("fileUrl", String.valueOf(attachment.getFileUrl()));
				result.put("filename", attachment.getFilename());
				result.put("createdate", Datetimes.format(attachment.getCreatedate(),"yyyy-MM-dd"));
			}
			return result;
		}
		
		private final static String C_sXML_Head="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		 /**
		  * 专用于表单导出
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		  */			
		public Map<String,String> downloadFormSource(String formIds)throws Exception {
			
 
				Date myDate = new Date(); 
				Map<String,String> result = new HashMap();
				result.put("fileurl", String.valueOf(myDate.getTime()));
				result.put("createdate", Datetimes.format(myDate, "yyyy-MM-dd"));
				
				String[] ids = formIds.split("↗");
				//压缩文件
				ZipUtil fu = new ZipUtil();
				String baseFolder = fileManager.getNowFolder(true);
				//配置文件(含枚举、表单基本信息)
				File paramFile = new File(baseFolder,"param.xml");
				Document paramFileDoc = DocumentHelper.createDocument();
				Element paramDocRoot = paramFileDoc.addElement("root");
				Element enumList = paramDocRoot.addElement("enumList");
				Element formList = paramDocRoot.addElement("FormList");

				List<File> formAppFileList = new ArrayList<File>();
				
				FormDaoManager fDao = new FormDaoManagerImpl();
				for(int i=0;i<ids.length;i++){
					File folder = new File(baseFolder,"Form_"+result.get("fileurl"));
					folder.mkdir();
					FormAppMain aApp = fDao.findApplicationById(Long.parseLong(ids[i]));
					//表单名称
					String fAppName = fCurrentCharSet.DBOut2JDK(aApp.getName());
					if(i==0){
						if(ids.length == 1){ 
							result.put("filename", fAppName+".pak");
						}else{
							result.put("filename", fAppName+"等.pak");
						}
					}
					formAppFileList.add(downloadSingleFormResource(aApp,folder,formList,enumList));
					FileUtil.delDirectory(folder.getPath());
				}
				//配置文件打包
				OutputStream fout = null;
				PrintStream writer = null;
				try {
					fout = new FileOutputStream(paramFile); 
					writer = new PrintStream(fout,false,"UTF-8");
					writer.print(paramFileDoc.asXML());
					writer.flush();
				}finally{
					if(writer != null)
						writer.close();
					if(fout != null )
						fout.close();
					
				}
				formAppFileList.add(paramFile);

				//导出打包
				File zipFile = new File(baseFolder,result.get("fileurl"));
				fu.zip(formAppFileList, zipFile);
				//删除中间文件
				for(File file : formAppFileList){
					file.delete();
				}
	          return result;

		}
		/**
		 * 获取单个表单模板的资源文件
		 * @param aApp
		 * @param folder
		 * @param formList
		 * @param enumList
		 * @return
		 * @throws Exception
		 */
		private File downloadSingleFormResource(FormAppMain aApp,File folder,Element formList,Element enumList)throws Exception{

			List<File> formFileList = new ArrayList<File>();

			//表单名称
			String fAppName = fCurrentCharSet.DBOut2JDK(aApp.getName());
			File appZipFile = new File(folder.getParent(), aApp.getId()+".pak");
			//配置文件
			Element formElement = formList.addElement("form");
			formElement.addAttribute("name", fAppName);
			formElement.addAttribute("id", String.valueOf(aApp.getId()));
			formElement.addAttribute("type", String.valueOf(aApp.getFormType()));
			formElement.addAttribute("start", String.valueOf(aApp.getFormstart()));
			//应用分类
			formElement.addAttribute("categoryName", templeteCategoryManager.get(aApp.getCategory()).getName());

			//获取涉及到的表名
			String dataDefine = fCurrentCharSet.DBOut2JDK(aApp.getDataStructure());
			Document appDoc = dom4jxmlUtils.paseXMLToDoc(dataDefine);
			Element appRoot = appDoc.getRootElement();
			List<Attribute> attributeList = appRoot.selectNodes("//Table/@name");
			String tableName = "";
			for(Attribute attribute : attributeList){
				tableName+=attribute.getValue()+",";
			}
			formElement.addAttribute("tableName", tableName);
			//表单定义
			File appFile = new File(folder, String.valueOf(aApp.getId()));
			OutputStream fout = null;
			PrintStream writer = null;
			try {
				fout = new FileOutputStream(appFile); 
				writer = new PrintStream(fout,false,"UTF-8");
				writer.print(C_sXML_Head+dataDefine);
				writer.flush();
			}finally {
				if(writer != null)
					writer.close();
				if(fout != null )
					fout.close();
			}
			formFileList.add(appFile);
			
			//表单资源内容
			FormTempleteHelper.findFormTempleteResource(aApp,folder,formElement,enumList,metadataManager,formFileList);
			
			//模板xsn文件 
			List<Attachment> attFiles = attachmentManager.getByReference(aApp.getId());
			if(!attFiles.isEmpty()){
				Attachment att = attFiles.get(0);
				Long attachmentId = att.getFileUrl();
				Date attCreateDate = att.getCreatedate();	
				File attFile = fileManager.getFile(attachmentId, attCreateDate);
				if(attFile==null || !attFile.exists()){
					throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.fileisinexistence.label"));
				} 
				File destFile = new File(folder, String.valueOf(att.getFilename()));
				//destFile.createNewFile();
				FileUtil.copy(attFile, destFile);
				formFileList.add(destFile);
				formElement.addAttribute("attName", att.getFilename());
			}
			
			//表单打包 
			ZipUtil fu = new ZipUtil();
			fu.zip(formFileList, appZipFile);
			return appZipFile;
		}

		 /** 
		  * 专用于表单导入
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		  */			
		public ModelAndView uploadFormSource(HttpServletRequest request,
				HttpServletResponse response){
			ModelAndView mav = new ModelAndView("form/formcreate/formcreateBorderFrame");
			//TODO 修复未做(与与判定冲突)
			File att = null;
			File zipFolder = null;
			
			//判断页面入口
			String formType = request.getParameter("formType");
			if(formType==null || "".equals(formType))
				formType = "1";
			mav.addObject("formType", formType);
			try{
				//附件解析
				String fileUrl=(String)request.getParameter("fileUrl");
				if(fileUrl == null){
					throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.fileisinexistence.label"));
				}
				Date fileCreateDate = Datetimes.parse(request.getParameter("fileCreateDate"));
				att = fileManager.getFile(Long.valueOf(fileUrl),fileCreateDate);
				ZipUtil zu = new ZipUtil();
				String baseFolder = fileManager.getFolder(fileCreateDate, true);
				zipFolder = new File(baseFolder,"Form_"+fileUrl);
				zu.unzip(att, zipFolder); 
				SAXReader saxReader = new SAXReader();
				Document paramDoc = saxReader.read(new File(zipFolder,"param.xml"));
				Element paramRoot = paramDoc.getRootElement();

				//预判断(表单名)
				for(Object o : paramRoot.element("FormList").elements("form")){
					Element formElement = (Element)o;
					//表单类型与页面类型不匹配
					String type = formElement.attributeValue("type");
					if(!formType.equals(type)){
						String name = ""; 
						if("1".equals(type)){
							name = Constantform.getString4CurrentUser("form.base.formtype.templete");
						}else if("2".equals(type)){
							name = Constantform.getString4CurrentUser("form.base.formtype.message");
						}else if("3".equals(type)){
							name = Constantform.getString4CurrentUser("form.base.formtype.basedata");
						}
						List<String> lst = new ArrayList<String>();
						lst.add(Constantform.getString4CurrentUser("form.base.formtype.nosame.label",name));
						OperHelper.creatformmessage(request,response,lst);
						return mav;			
					}
					//表单名重复
					String formAppName = formElement.attributeValue("name");
					getIOperBase().isExistsThisForm(formAppName);
				}
				//枚举映射
				Map<String,Metadata> enumMap = new HashMap(); 
				for(Object o : paramRoot.element("enumList").elements("enum")){
					FormTempleteHelper.checkExistDetadata(enumMap,(Element)o,metadataManager);
				}
				//流水号映射
				Map<String,FormFlowid> flowidMap = new HashMap(); 
				for(Object o : paramRoot.element("FormList").elements("form")){
					Element formElement = (Element)o;
					//流水号映射
					for(Object b : formElement.elements("flowid")){
						FormTempleteHelper.checkExistFlowid((Element)b,flowidMap,metadataManager);
					}
				}
				//表名映射
				Map<String,String> tableMap = new HashMap(); 
				//表单导入
				User user = CurrentUser.get();
				for(Object o : paramRoot.element("FormList").elements("form")){
					Element formElement = (Element)o;
					//表名映射
					String[] tableNames = formElement.attributeValue("tableName").split(",");
					for(String tableName: tableNames){
					    String tableNumber = getIOperBase().incrementAndGetBiggestValueSign();
						//表单主表名称
						if(tableName.startsWith(IPagePublicParam.tablename)){
							tableMap.put(tableName,IPagePublicParam.tablename + tableNumber);
						}else if(tableName.startsWith(IPagePublicParam.tableson)){
							tableMap.put(tableName,IPagePublicParam.tableson + tableNumber);
						}
					}
					//组织FormAppMain		
					FormAppMain fam = new FormAppMain();
					//所属应用
					long categoryId=4l;
					categoryId = FormTempleteHelper.checkExistFormCategory(templeteCategoryManager, formElement.attributeValue("categoryName"));
					fam.setCategory(categoryId);
					FormTempleteHelper.uploadSingleFormTemplete(fam,formElement,baseFolder,zipFolder,fileCreateDate,enumMap,flowidMap,tableMap,fileManager,attachmentManager);
				}
			}catch (Exception e) {
				List<String> lst = new ArrayList<String>();
				lst.add(e.getMessage());
				log.error("保存表单导入异常", e);
				OperHelper.creatformmessage(request,response,lst);
				return mav;			
			}finally{
				if(att!=null)
					FileUtil.delDirectory(att.getPath());
				if(zipFolder!=null)
					FileUtil.delDirectory(zipFolder.getPath());
			}
			return mav;
		}
		
		public ModelAndView formUniqueMarkedSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/uniquemarkedset"); 
			return mav; 
		}
		public ModelAndView checkUniqueMarkedSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			List tablefieldlst = sessionobject.getTableFieldList();
			String formpage=request.getParameter("formpage");
			//判断是否有outwrite类型，有进行检查没有就跳出，从inputdata.jsp界面进入的判断不进行该循环。
			if(tablefieldlst != null && !"inputdata".equals(formpage)){
				boolean isHaveOutWrite=false;
				for(int i=0; i<tablefieldlst.size();i++ ){
					TableFieldDisplay td = (TableFieldDisplay)tablefieldlst.get(i);
					if(td.getInputtype().equals("outwrite")){
						isHaveOutWrite=true;
						break;
					}
				}
				if(!isHaveOutWrite){
					return null;
				}
			}
			//进行检查如果没有设置
			PrintWriter out = response.getWriter(); 
			if("inputdata".equals(formpage)){
			//inputdata.jsp进入不能从sessionobject中取. 
				String uniquefield=request.getParameter("uniquefield");
				if(!uniquefield.equals("")){
					String[] uniquefieldArray=uniquefield.split(",");
					if(!(uniquefieldArray[0].equals("")) && uniquefieldArray.length > 0){
						return null;
					}
				}
				out.write("NOTSET");
			}else{
				List uniquefieldList=sessionobject.getUniqueFieldList();
				if(uniquefieldList.size() <  1 ){
					out.write("NOTSET");
				}
			}
			return null; 
		}
		
		public ModelAndView echoSetting(HttpServletRequest request,
				HttpServletResponse response){
			ModelAndView mav = new ModelAndView("form/formcreate/echoSetting");
			return mav;
		}
		
		/**
		 * 设置回写表达式    -- 数字
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView setCalcExpressionForDecimal(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/setCalcExpressionForDecimal");
			String formAppId = request.getParameter("formAppId");
			if(Strings.isBlank(formAppId)){
				PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	String message = ResourceBundleUtil.getString(resource_baseName, "form.showAppFormData.noright");
	        	out.println("alert('" + message + "');");
	        	out.println("window.close();");
	        	out.println("</script>");
	        	return null;
			}
			List<String> refDataAreaList = new ArrayList<String>();
			Map<String, String> refTableNameMap = new HashMap<String, String>();
			Map<String, String> uniqueFieldsTableNameMap = new HashMap<String, String>();
			//获取关联表单中数据类型为数字的字段
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formAppId));
			if(fapp != null){
				SeeyonDataDefine seeyonDataDefine = (SeeyonDataDefine)fapp.getDataDefine();
				if(seeyonDataDefine != null){
//					ISeeyonDataSource dataSource = ((SeeyonDataDefine)fapp.getDataDefine()).getDataSource();
//					if(dataSource != null){
//						InfoPath_Inputtypedefine inputTypeDefine = dataSource.getDefaultInputtype();
//						List<IIP_InputObject> fInputList = inputTypeDefine.getInputList();
//						for (IIP_InputObject iip_InputObject : fInputList) {
//							String fieldType = iip_InputObject.getFieldType();
//							if(TFieldDataType.DECIMAL.name().equals(fieldType)){
//								String dataAreaName = iip_InputObject.getDataAreaName();
//								refDataAreaList.add(BindHelper.getColumName(dataAreaName));
//							}
//						}
//					}
					for(int i = 0; i < seeyonDataDefine.getDataDefine().getTableLst().size(); i++){
						FormTable formtable = (FormTable)seeyonDataDefine.getDataDefine().getTableLst().get(i);
						List<FormField> fieldlst = formtable.getFieldLst();
						for (FormField formField : fieldlst) {
							String fieldType = formField.getFieldtype();
							String displayName = formField.getDisplay();
							refTableNameMap.put(displayName, formtable.getName());
							if(TFieldDataType.DECIMAL.name().equals(fieldType)){
								refDataAreaList.add(displayName);
							}
						}
					}
				}
				
				List<String> uniqueFieldList = fapp.getUniqueFieldList();
				for (String uniqueField : uniqueFieldList) {
					String tableName = refTableNameMap.get(BindHelper.getColumName(uniqueField));
					if(tableName != null){
						uniqueFieldsTableNameMap.put(tableName, tableName);
					}
				}
			}
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			mav.addObject("fieldlst", sessionobject.getTableFieldList());
			mav.addObject("refDataAreaList", refDataAreaList);
			mav.addObject("refTableNameMap", refTableNameMap);
			mav.addObject("uniqueFieldsTableNameMap", uniqueFieldsTableNameMap);
			mav.addObject("refFormName", fapp.getAppName());
			return mav;
		}
		
		/**
		 * 设置回写表达式    -- 日期/文本
		 * @param request
		 * @param response
		 * @return
		 */
		public ModelAndView setCalcExpressionForDateOrVarchar(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/setCalcExpressionForDateOrVarchar");
			String fieldType = request.getParameter("fieldType");
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
			List<TableFieldDisplay> tableFieldList = (List<TableFieldDisplay>)sessionObject.getTableFieldList();
			List<TableFieldDisplay> fieldList = new ArrayList<TableFieldDisplay>();
			if(TFieldDataType.VARCHAR.name().equals(fieldType)){
				for (TableFieldDisplay tableFieldDisplay : tableFieldList) {
					if(TFieldDataType.VARCHAR.name().equals(tableFieldDisplay.getFieldtype())){
						fieldList.add(tableFieldDisplay);
					}
				}
			} else if(TFieldDataType.TIMESTAMP.name().equals(fieldType)){
				for (TableFieldDisplay tableFieldDisplay : tableFieldList) {
					if(TFieldDataType.TIMESTAMP.name().equals(tableFieldDisplay.getFieldtype())){
						fieldList.add(tableFieldDisplay);
					}
				}
			} else if(TFieldDataType.DATETIME.name().equals(fieldType)){
				for (TableFieldDisplay tableFieldDisplay : tableFieldList) {
					if(TFieldDataType.DATETIME.name().equals(tableFieldDisplay.getFieldtype())){
						fieldList.add(tableFieldDisplay);
					}
				}
			}
			mav.addObject("fieldList", fieldList);
			return mav;
		}
		
		public boolean checkTableNameIsSameWithUnique(Long formAppId, String fieldName){
			Map<String, String> refTableNameMap = new HashMap<String, String>();
			Map<String, String> uniqueFieldsTableNameMap = new HashMap<String, String>();
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
			boolean flag = false;
			if(fapp != null){
				SeeyonDataDefine seeyonDataDefine = (SeeyonDataDefine)fapp.getDataDefine();
				if(seeyonDataDefine != null){
					for(int i = 0; i < seeyonDataDefine.getDataDefine().getTableLst().size(); i++){
						FormTable formtable = (FormTable)seeyonDataDefine.getDataDefine().getTableLst().get(i);
						List<FormField> fieldlst = formtable.getFieldLst();
						for (FormField formField : fieldlst) {
							String fieldType = formField.getFieldtype();
							String displayName = formField.getDisplay();
							refTableNameMap.put(displayName, formtable.getName());
						}
					}
				}
				
				String currentFieldNameTable = refTableNameMap.get(BindHelper.getColumName(fieldName));
				
				List<String> uniqueFieldList = fapp.getUniqueFieldList();
				for (String uniqueField : uniqueFieldList) {
					String tableName = refTableNameMap.get(BindHelper.getColumName(uniqueField));
					if(tableName.equals(currentFieldNameTable)){
						flag = true;
						break;
					}
				}
			}
			return flag;
		}
		
		private static final String ECHOSETTINGNAME = "计算回写事件";
		
		/**
		 * 保存回写设置
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView saveEchoSetting(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			HttpSession session = request.getSession();
			SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
			String[] indexArray = request.getParameterValues("hiddenIndex");
			
			//保存关联条件
			OperHelper.setRelationConditionList(request, sessionObject, RelationCondition.SOURCETYPE_RETURNWRITE);
			
			//先删除后保存
			Map<Long,FormEvent> triggerConfigMap = sessionObject.getTriggerConfigMap();
			List<Long> removeEventIdList = new ArrayList<Long>();
			Collection<FormEvent> triggerConfigList = triggerConfigMap.values();
			for (FormEvent formEvent : triggerConfigList) {
				if(formEvent.getSourceType().intValue() == FormEvent.SOURCETYPE_RETURNWRITECONFIG){
					removeEventIdList.add(formEvent.getId());
				}
			}
			for (Long eventId : removeEventIdList) {
				triggerConfigMap.remove(eventId);
			}
			
			FormEvent formEvent = null;
			for(int n = 0; n < indexArray.length; n++){
				String i = indexArray[n];
				Long eventId = Long.valueOf(UUIDLong.longUUID());
				String eventIdStr = request.getParameter("eventId" + i); //事件ID
				if(Strings.isNotBlank(eventIdStr)){
					eventId = Long.parseLong(eventIdStr);
				}
				String relationConditionId = request.getParameter("selectRelationForm" + i);//关联条件的ID
				if(Strings.isBlank(relationConditionId)){
					continue;
				}
				String formAppId = request.getParameter("formAppId" + i);//关联表单ID
				String echoTime = request.getParameter("echoTime" + i);//回写时间点
				String refColumNameArray[] = request.getParameterValues("refColumNames" + i);//要回写的字段
				String calcExpressionArray[] = request.getParameterValues("calcExpression" + i); //回写表达式
				boolean withholding = Boolean.parseBoolean(request.getParameter("withholding" + i));//预提控制
				ISeeyonFormAppManager iSeeyonFormAppManager = new SeeyonFormAppManagerImpl();
				formEvent = new FormEvent(iSeeyonFormAppManager.findById(sessionObject.getFormid()));
				formEvent.setId(eventId);
				formEvent.setName(ECHOSETTINGNAME);
				formEvent.setStatus(1);//启用状态
				formEvent.setSourceType(FormEvent.SOURCETYPE_RETURNWRITECONFIG);
				
				//触发条件
				List<EventCondition> conditionList = new ArrayList<EventCondition>();
				formEvent.setConditionList(conditionList);
				EventValue eventValue = new EventValue();
				eventValue.setValue(echoTime);
				EventCondition eventCondition = new EventCondition();
				eventCondition.setType(EventCondition.TYPE_TRIGGERDOT);
				eventCondition.setValue(eventValue);
				conditionList.add(eventCondition);
				
				//触发动作
				List<EventAction> actionList = new ArrayList<EventAction>();
				EventRelatedForm eventRelatedForm = new EventRelatedForm();
				eventRelatedForm.setRelationConditionId(Long.parseLong(relationConditionId));
				eventRelatedForm.setFormAppId(Long.parseLong(formAppId));
				EventAction eventAction = new EventAction();
				eventAction.setType(EventAction.TYPE_CALCULATE);
				eventAction.setWithholding(withholding);
				eventAction.setRelatedForm(eventRelatedForm);
				List<EventCalculate> calculateList = new ArrayList<EventCalculate>();
				for (int j = 0; j < refColumNameArray.length; j++) {
					if(Strings.isBlank(refColumNameArray[j])){
						continue;
					}
					EventCalculate eventCalculate = new EventCalculate();
					eventCalculate.setFieldName(refColumNameArray[j]);
					eventCalculate.setValue(calcExpressionArray[j]);
					calculateList.add(eventCalculate);
				}
				if(calculateList.size() == 0){
					continue;
				}
				eventAction.setCalculateList(calculateList);
				actionList.add(eventAction);
				formEvent.setActionList(actionList);
				triggerConfigMap.put(formEvent.getId(), formEvent);
			}
			
			PrintWriter out = response.getWriter();
	    	out.println("<script>");
	    	out.println("window.returnValue = 'true';window.close();");
	    	out.println("</script>");
			return null;
		}
		
		
		public List<String> getFieldsByRelationCondition(Long formAppId, Long relationConditionId){
			List<String> fieldsList = new ArrayList<String>();
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
			if(fapp != null){
				RelationCondition relationCondition = fapp.findRelationConditionById(relationConditionId);
				if(relationCondition != null){
					IConditionList conditionList = relationCondition.getConditionList();
					if(conditionList != null){
						ConditionListImpl conditionListImpl = (ConditionListImpl)conditionList;
						List<ICondition> tempConditions = conditionListImpl.getConditionList();
						for (int i = 0; i < tempConditions.size(); i++) {
							ICondition condition = tempConditions.get(i);
							if(condition instanceof DataColumImpl && i % 4 == 0){
								DataColumImpl dataColum = (DataColumImpl) condition;
								fieldsList.add(dataColum.getColumName());
							}
						}
					}
				}
			}
			return fieldsList;
		}
		
		/**
		 * AJAX  关联表单时，设置了系统选择，根据关联条件取得唯一一条记录
		 * @param formAppId
		 * @param relationConditionId
		 * @throws SeeyonFormException 
		 */
		public Map<String, String[]> getValueMapByRelationCondition(Long formAppId, Long relationConditionId, Long refFormAppId, String[] fieldValueArray) throws SeeyonFormException{
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
			if(fapp == null){
				return null;
			}
			ConditionListImpl filter = new ConditionListImpl();
			RelationCondition relationCondition = fapp.findRelationConditionById(relationConditionId);
			if(relationCondition != null){
				IConditionList conditionList = relationCondition.getConditionList();
				filter = (ConditionListImpl) conditionList.copy();
			}
			List<ICondition> conditions = filter.getConditionList();
			int j = 0;
			for (int i = 0; i < conditions.size(); i++) {
				if(i % 4 == 0){
					conditions.set(i, conditions.get(i + 2));
					ValueImpl value = new ValueImpl();
					value.setValueType(ValueImpl.C_iValueType_Value);
					value.setValue(fieldValueArray[j]);
					j++;
					conditions.set(i + 2, value);
				}
			}
			
			//关联表单
			SeeyonForm_ApplicationImpl ifapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(refFormAppId);
			if(ifapp == null){
				return null;
			}
			InfoPath_Inputtypedefine inputTypeDefine = null;
			List<QueryColum> dataColumList = new ArrayList<QueryColum>();
			if(ifapp.getDataDefine() != null && (ifapp.getDataDefine() instanceof SeeyonDataDefine)){
				ISeeyonDataSource dataSource = ((SeeyonDataDefine)ifapp.getDataDefine()).getDataSource();
				if(dataSource != null){
					List<IDataArea> dataAreaList = dataSource.getDataAreaList();
					for (IDataArea dataArea : dataAreaList) {
						QueryColum queryColum = new QueryColum();
						queryColum.setColumTitle(dataArea.getAreaName());
						queryColum.setDataAreaName("my:" + dataArea.getAreaName());
						dataColumList.add(queryColum);
					}
					inputTypeDefine = dataSource.getDefaultInputtype();	
				}
			}
			
			QueryResultImpl resultData = null;
			Map<String,String[]> map = new HashMap<String,String[]>();
			try{
				resultData = (QueryResultImpl)FormHelper.getQueryResult(refFormAppId, dataColumList, filter);
				if(resultData == null){
					return null;
				}
				IQueryRecord record = resultData.getRecord(0);
				for(QueryColum queryColum : dataColumList){
					String dataAreaName = queryColum.getDataAreaName();
					String displayValue = record.getValueByName(dataAreaName);
					if(inputTypeDefine != null){
						inputTypeDefine.field(dataAreaName);
						if("DECIMAL".equals(inputTypeDefine.getFieldtype())){
							displayValue = record.getRealValueByName(dataAreaName);
						}
					}
					map.put(dataAreaName, new String[]{displayValue, record.getRealValueByName(dataAreaName)});
				}
			} catch (Exception e){
				log.error("查询出错！" + e);
			} finally {
				// 释放调用的数据库资源
	            if (resultData != null) {
	                resultData.unInit();
	            }
			}
			if(map.isEmpty()){
				return null;
			}
			return map;
		}
		/**
		 * Ajax根据表单id获得该表单所有的字段列表字符串
		 * @param formAppId
		 * @param formShortName
		 * @param checkbox_label
		 * @param datamodule_label
		 * @param personmodule_label
		 * @param departmentmodule_label
		 * @param postmodule_label
		 * @param levelmodule_label
		 * @param accountmodule_label
		 * @param enum_label
		 * @param enumRef_label
		 * @return
		 * @throws SeeyonFormException
		 */
		public String getFormFieldsByFormAppId(String formAppId,
				String formShortName,
				String checkbox_label,
				String datamodule_label,
				String personmodule_label,
				String departmentmodule_label,
				String postmodule_label,
				String levelmodule_label,
				String accountmodule_label,
				String enum_label,
				String enumRef_label) throws SeeyonFormException{
			StringBuffer sbf= new StringBuffer("");
			try{
				List<TableFieldDisplay> targetTableFieldList=new ArrayList<TableFieldDisplay>();
				List<TableFieldDisplay> list = FormHelper.getTableFieldDisplayById(formAppId);
				for(TableFieldDisplay temp1:list){
					TableFieldDisplay newTableFieldDisplay = new TableFieldDisplay();
					BeanUtils.copyProperties(newTableFieldDisplay, temp1);
					if(temp1.getInputtype().equals("relation")||(temp1.getInputtype().equals("extend")&& temp1.getExtend().equals("选择关联表单..."))){
						String[]relationInputType=FormHelper.getRelationInfo(list,temp1);
						newTableFieldDisplay.setInputtype(relationInputType[1]);
						newTableFieldDisplay.setExtend(relationInputType[4]);
					}
					targetTableFieldList.add(newTableFieldDisplay);
				}		
				//循环遍历targetTableFieldList，拼成下拉列表字符串
				boolean isMainTable = false;
				for (int i=0; i<targetTableFieldList.size();i++) {
					TableFieldDisplay tablefielddis = (TableFieldDisplay) targetTableFieldList.get(i);
					isMainTable = tablefielddis.getTablename() != null && tablefielddis.getTablename().indexOf("main") !=-1;
					boolean isUsed= false;
					if("checkbox".equals(tablefielddis.getInputtype())){//checkbox
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"checkbox\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(checkbox_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("outwrite".equals(tablefielddis.getInputtype())){//外部写入
	    				if(IPagePublicParam.TIMESTAMP.equals(tablefielddis.getFieldtype())){//日期类型
	    					sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
							sbf.append("dataType=\"date\" isMainTable=\"").append(isMainTable).append("\" ");
							if(i==0){
								sbf.append(" selected ");
							}
							sbf.append(">");
							sbf.append(tablefielddis.getName());
							sbf.append("(").append(datamodule_label).append(")");
							sbf.append("</option> ");
							isUsed= true;
	    				}
					}else if("日期选取器".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"date\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(datamodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("选择人员".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"person\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(personmodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("选择部门".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"department\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(departmentmodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("选择岗位".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"post\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(postmodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("选择职务级别".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"level\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(levelmodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("选择单位".equals(tablefielddis.getExtend())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"account\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(accountmodule_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}
					if(!"".equals(tablefielddis.getDivenumtype()) && tablefielddis.getDivenumtype()!=null){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"enum\" isMainTable=\"").append(isMainTable).append("\" ");
						sbf.append("isFinalChild=\"").append(tablefielddis.isFinalChild()).append("\" ");
						sbf.append("enum=\"").append(tablefielddis.getDivenumtype()).append("\" ");
						String sepcialXMl= Strings.toHTML(tablefielddis.getEnumtype(), false);
						sbf.append("enumType=\"").append(sepcialXMl).append("\">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(enum_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if("select".equals(tablefielddis.getRefInputType())){
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"refEnum\" isMainTable=\"").append(isMainTable).append("\" ");
						sbf.append("otherFormAppId=\"").append(formAppId).append("\" ");
						sbf.append("fieldName=\"").append(tablefielddis.getName()).append("\">");
						sbf.append(tablefielddis.getName());
						sbf.append("(").append(enumRef_label).append(")");
						sbf.append("</option> ");
						isUsed= true;
					}else if(("relation".equals(tablefielddis.getInputtype()) 
							|| "选择关联表单...".equals(tablefielddis.getExtend())) 
							&& !IPagePublicParam.DECIMAL.equals(tablefielddis.getFieldtype())){
						String[] inputTypeArray = www.seeyon.com.v3x.form.utils.FormHelper.getRelationInfo(targetTableFieldList,tablefielddis);
						String refType = "";
						boolean flag = false;
						if("checkbox".equals(inputTypeArray[1])){
							refType = "checkbox";
							flag = true;
						} else if("select".equals(inputTypeArray[1])){ 
							refType = "refEnum";
							flag = true;
						} else if("radio".equals(inputTypeArray[1])){ 
							refType = "refEnum";
							flag = true;
						} else if("日期选取器".equals(inputTypeArray[4])){
							refType = "date";
							flag = true;
						} else if("选择人员".equals(inputTypeArray[4])){
							refType = "person";
							flag = true;
						} else if("选择部门".equals(inputTypeArray[4])){
							refType = "department";
							flag = true;
						} else if("选择岗位".equals(inputTypeArray[4])){
							refType = "post";
							flag = true;
						} else if("选择职务级别".equals(inputTypeArray[4])){
							refType = "level";
							flag = true;
						} else if("选择单位".equals(inputTypeArray[4])){
							refType = "account";
							flag = true;
						}
						if(flag){
							sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
							sbf.append(" dataType=\"ref\" refType=\"").append(refType).append("\" ");
							sbf.append(" relationType=\"").append(inputTypeArray[0]).append("\" ");
							sbf.append(" refKey=\"").append(inputTypeArray[3]).append("\" ");
							sbf.append(" refFormAppId=\"").append(inputTypeArray[2]).append("\" ");
							sbf.append(" currentLevelNum=\"").append(inputTypeArray[5]).append("\" ");
							sbf.append(" refEnumId=\"").append(inputTypeArray[6]).append("\" ");
							sbf.append(" isMainTable=\"").append(isMainTable).append("\" ");
							sbf.append("fieldName=\"").append(tablefielddis.getName()).append("\">");
							sbf.append(tablefielddis.getName());
							sbf.append("(").append(enumRef_label).append(")");
							sbf.append("</option> ");
							isUsed= true;
						}
					}
					
					if( IPagePublicParam.DECIMAL.equals(tablefielddis.getFieldtype()) && !isUsed){//数字
						sbf.append(" <option value=\"").append(formShortName).append(".").append(tablefielddis.getName()).append("\" ");
						sbf.append("dataType=\"num\" isMainTable=\"").append(isMainTable).append("\" ");
						if(i==0){
							sbf.append(" selected ");
						}
						sbf.append(">");
						sbf.append(tablefielddis.getName());
						sbf.append("</option> ");
					}
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
				throw new SeeyonFormException(1, e.getMessage(), e);
			}
			return sbf.toString();
		}
		
		/**
		 * Ajax根据表单id获得该表单所有的字段列表字符串js数组格式
		 * @param formAppId
		 * @param formShortName
		 * @return
		 * @throws SeeyonFormException
		 */
		public String[] getFormFieldsStrByFormAppId(String formAppId,
				String formShortName) throws SeeyonFormException{
			StringBuffer sbf_main= new StringBuffer("[");
			StringBuffer sbf_sub= new StringBuffer("[");
			String[] temp= new String[]{"FALSE","FALSE"};
			try{
				List<TableFieldDisplay> targetTableFieldList=new ArrayList<TableFieldDisplay>();
				List<TableFieldDisplay> list = FormHelper.getTableFieldDisplayById(formAppId);
				for(TableFieldDisplay temp1:list){
					TableFieldDisplay newTableFieldDisplay = new TableFieldDisplay();
					BeanUtils.copyProperties(newTableFieldDisplay, temp1);
					if(temp1.getInputtype().equals("relation")||(temp1.getInputtype().equals("extend")&& temp1.getExtend().equals("选择关联表单..."))){
						String[]relationInputType=FormHelper.getRelationInfo(list,temp1);
						newTableFieldDisplay.setInputtype(relationInputType[1]);
						newTableFieldDisplay.setExtend(relationInputType[4]);
					}
					targetTableFieldList.add(newTableFieldDisplay);
				}		
				//循环遍历targetTableFieldList，拼成下拉列表字符串
				boolean hasMainField = false;
				boolean hasSubField = false;
				for (int i=0; i<targetTableFieldList.size();i++) {
					TableFieldDisplay tablefielddis = (TableFieldDisplay) targetTableFieldList.get(i);
					if(IPagePublicParam.DECIMAL.equals(tablefielddis.getFieldtype())
							|| "checkbox".equals(tablefielddis.getInputtype())
							|| ( "outwrite".equals(tablefielddis.getInputtype()) && IPagePublicParam.TIMESTAMP.equals(tablefielddis.getFieldtype()))//外部写入日期类型
							|| "日期选取器".equals(tablefielddis.getExtend())
							|| "选择人员".equals(tablefielddis.getExtend())
							|| "选择部门".equals(tablefielddis.getExtend())
							|| "选择岗位".equals(tablefielddis.getExtend())
							|| "选择职务级别".equals(tablefielddis.getExtend())
							|| "选择单位".equals(tablefielddis.getExtend())
							|| (!"".equals(tablefielddis.getDivenumtype()) && tablefielddis.getDivenumtype()!=null)
							|| "select".equals(tablefielddis.getRefInputType())){
						sbf_main.append(" {key:'").append(formShortName).append(".").append(tablefielddis.getName()).append("',");
						sbf_main.append("value:'").append(i).append("'},");
						hasMainField= true;
						if(tablefielddis.getTablename()!=null && tablefielddis.getTablename().indexOf("formson_")!=-1){//从表
							hasSubField= true;
							sbf_sub.append(" {key:'").append(formShortName).append(".").append(tablefielddis.getName()).append("',");
							sbf_sub.append("value:'").append(i).append("'},");
						}
					}else if(("relation".equals(tablefielddis.getInputtype()) 
							|| "选择关联表单...".equals(tablefielddis.getExtend())) 
							&& !IPagePublicParam.DECIMAL.equals(tablefielddis.getFieldtype())){
						String[] inputTypeArray = www.seeyon.com.v3x.form.utils.FormHelper.getRelationInfo(targetTableFieldList,tablefielddis);
						String refType = "";
						boolean flag = false;
						if("checkbox".equals(inputTypeArray[1])){
							refType = "checkbox";
							flag = true;
						} else if("select".equals(inputTypeArray[1])){ 
							refType = "refEnum";
							flag = true;
						} else if("radio".equals(inputTypeArray[1])){ 
							refType = "refEnum";
							flag = true;
						} else if("日期选取器".equals(inputTypeArray[4])){
							refType = "date";
							flag = true;
						} else if("选择人员".equals(inputTypeArray[4])){
							refType = "person";
							flag = true;
						} else if("选择部门".equals(inputTypeArray[4])){
							refType = "department";
							flag = true;
						} else if("选择岗位".equals(inputTypeArray[4])){
							refType = "post";
							flag = true;
						} else if("选择职务级别".equals(inputTypeArray[4])){
							refType = "level";
							flag = true;
						} else if("选择单位".equals(inputTypeArray[4])){
							refType = "account";
							flag = true;
						}
						if(flag){
							sbf_main.append(" {key:'").append(formShortName).append(".").append(tablefielddis.getName()).append("',");
							sbf_main.append("value:'").append(i).append("'},");
							hasMainField= true;
							if(tablefielddis.getTablename()!=null && tablefielddis.getTablename().indexOf("formson_")!=-1){//从表
								hasSubField= true;
								sbf_sub.append(" {key:'").append(formShortName).append(".").append(tablefielddis.getName()).append("',");
								sbf_sub.append("value:'").append(i).append("'},");
							}
						}
					}
				}
				if(hasMainField){
					String main_temp= sbf_main.toString();
					StringUtils.removeEnd(main_temp, ",");
					int endPos= main_temp.lastIndexOf(",");
					main_temp= main_temp.substring(0, endPos);
					main_temp +="]";
					temp[0]= main_temp;
				}
				if(hasSubField){
					String sub_temp= sbf_sub.toString();
					StringUtils.removeEnd(sub_temp, ",");
					int endPos= sub_temp.lastIndexOf(",");
					sub_temp= sub_temp.substring(0, endPos);
					sub_temp +="]";
					temp[1]= sub_temp;
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
				throw new SeeyonFormException(1, e.getMessage(), e);
			}
			return temp;
		}
		
		/**
		 * 设置数据交换任务
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView setDeeTask(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/setDeeTask");
			return mav;
		}
		
		/**
		 * 选择交换数据，从DEE返回的任务列表中选择关联任务
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView selectDeeTask(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/selectDeeTask");
			return mav;
		}
		
		/**
		 * 获取任务的数据列表
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView selectDeeTaskResult(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView modelAndView = new ModelAndView("form/formcreate/selectDeeTaskResult") ;
	    	return modelAndView ;  
	    }
		/**
		 * 获取任务数据列表
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView selectDeeTaskResultList(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView modelAndView = new ModelAndView("form/formcreate/selectDeeTaskResultList") ;
	    	String isSearch = request.getParameter("isSearch");
	    	int totalCount = 0;
	    	modelAndView.addObject("isSearch",isSearch);
	    	String condition = request.getParameter("condition");
	    	String textfield = request.getParameter("textfield");
			String inputFieldName = request.getParameter("refField");
			String formId = request.getParameter("formId") ;
			String paramStr = request.getParameter("paramStr");
			String pageNumber = request.getParameter("page")==null?"1":request.getParameter("page");
			String pageSize = request.getParameter("pageSize")==null?"20":request.getParameter("pageSize");
			String eventDeeId = request.getParameter("eventDeeId");
			long summaryId = NumberUtils.toLong(request.getParameter("summaryId"));
			String operationId = request.getParameter("operationId");
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");		
			if(eventDeeId==null){
				getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
				if(sessionobject == null){
					sessionobject = new SessionObject();
					session.setAttribute("SessionObject",sessionobject);
				}
			}
			if(Strings.isBlank(formId)){
				return null ;
			}
			InfoPath_ViewBindEventBind eventBind=null;
			List fieldInputList =null;
			SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formId)) ;
			if(fapp!=null&&eventDeeId==null){
				sessionobject = getIOperBase().loadFromnoInfoPath(fapp,formId);
				InfoPath_Inputtypedefine inputTypeDefine = FormHelper.getInfoPathInputtypedefine(fapp,fapp.getFResourceProvider());
				List<IIP_InputObject> fInputList = inputTypeDefine.getInputList();
				fieldInputList = sessionobject.getFieldInputList();
			}else if(fapp!=null){
				 eventBind=fapp.getEventBind(summaryId, NumberUtils.toLong(operationId),eventDeeId);
			}
			
			InfoPath_DeeTask deetask = null;
			List<InfoPath_DeeField> fieldList = null;
			List<InfoPath_DeeParam> taskParamList = null;
			int checkedFieldSize = 0;
		    if (StringUtils.isNotBlank(inputFieldName)) {
			// 获取关联任务
			if (fieldInputList != null && fieldInputList.size() > 0) {
				for (int i = 0; i < fieldInputList.size(); i++) {
					InfoPath_FieldInput fieldInput = (InfoPath_FieldInput) fieldInputList.get(i);
					if (inputFieldName.equals(fieldInput.getName())) {
						deetask = fieldInput.getDeeTask();
						fieldList = deetask.getTaskFieldList();
						taskParamList = deetask.getTaskParamList();
						break;
					}
				}
			}
            }else{
            	if(eventBind==null){
            		return null;
            	}
            	deetask=eventBind.getDeeTask();
            	fieldList=deetask.getTaskFieldList();
            	taskParamList = deetask.getTaskParamList();
            }
	
			//关联任务执行参数及查询条件
			Map paramMap = new HashMap();
			if(paramStr != null && paramStr.length()>0){
				String[] params = paramStr.split("\\|");
				if(params != null && params.length>0){
					for(int k = 0 ; k < params.length; k++){
						String paramName = "";
						String paramValue = "";
						String[] param = params[k].split("=");
						paramName = param[0];
						if(param.length>1){
							paramValue = param[1];
						}
						paramMap.put(paramName, paramValue);
					}
				}
			}
			
			long masterId = 0;
			if (eventDeeId != null) {
				ColSummary summary=null;
				try {
					summary = colManager.getColSummaryById(summaryId, false);
				} catch (ColException e) {
				}
				Map<String, List<Map<String, String>>> map=FormHelper.loadFormPojoById(summary.getFormRecordId(), summary.getFormAppId());
				masterId= summary.getFormRecordId();
				for (InfoPath_DeeParam infoPath_DeeParam : taskParamList) {
					for (List<Map<String, String>> list : map.values()) {
						for (Map<String, String> map2 : list) {
							String value = map2.get(infoPath_DeeParam.getValue());
							if (value != null) {
								paramMap.put(infoPath_DeeParam.getValue(), value);
								break;
							}
						}
					}
					if(paramMap.get(infoPath_DeeParam.getValue())==null){
						paramMap.put(infoPath_DeeParam.getValue(),infoPath_DeeParam.getValue());
					}
				}
				
				deetask.setId(deetask.getName());
			}
			Parameters parameters = new Parameters();
			if(taskParamList != null && taskParamList.size()>0){
				for(int i = 0; i < taskParamList.size(); i++){
					InfoPath_DeeParam param = taskParamList.get(i);
					parameters.add(param.getName(), paramMap.get(param.getValue()));
				}
			}
			if(condition != null && condition.length()>0 && textfield != null){
				if(Strings.isNotBlank(textfield)){
					//对单引号进行防护
					textfield = textfield.replace("'", "''");
				}
				parameters.add("whereString"," where " + condition + " like '%" +SQLWildcardUtil.escape(textfield) +"%'");
			}else{
				parameters.add("whereString"," where 1=1");
			}
			parameters.add(DEEConfigService.PARAM_PAGENUMBER, Integer.valueOf(pageNumber));
			parameters.add(DEEConfigService.PARAM_PAGESIZE, Integer.valueOf(pageSize));
			parameters.add("masterId", masterId);
			List resultDataList = new ArrayList();
			//获取任务数据并解析数据
			DEEClient client = new DEEClient();
			try{
				com.seeyon.v3x.dee.Document deeDoc = client.execute(deetask.getId(),parameters);
				Element tableRoot = null;
				if(deeDoc != null){
					String deeResultXml = deeDoc.toString();
					Document doc = dom4jxmlUtils.paseXMLToDoc(deeResultXml);
					if(doc!=null){
						tableRoot = (Element) doc.getRootElement().element(deetask.getTablename());
						System.out.println("==========================================="+deetask.getTablename());
						totalCount = Integer.parseInt(tableRoot.attribute("totalCount").getValue());
					}
				}
				if(deeDoc != null && totalCount > 0 && tableRoot != null){
					Map resultMap = null;
					Element rowEle = null;
					List rows = tableRoot.elements("row");
					for (Object item : rows) {
						resultMap = new LinkedHashMap();
						rowEle = (Element) item;
						if(fieldList!=null && fieldList.size()>0){
							InfoPath_DeeField field = null;
							InfoPath_DeeField fieldData = null;
							Element filedEle = null;
							checkedFieldSize = 0;
							for(int j = 0; j < fieldList.size(); j++){
								field = fieldList.get(j);
								filedEle = rowEle.element(field.getName().toLowerCase().trim());
								fieldData = new InfoPath_DeeField();
								fieldData.setName(field.getName());
								fieldData.setDisplay(field.getDisplay());
								fieldData.setValue(filedEle==null?"":filedEle.getStringValue());
								fieldData.setChecked(field.getChecked());
								resultMap.put(fieldData.getName(), fieldData);
//								if(Boolean.valueOf(field.getChecked())){
//									checkedFieldSize ++;
//								}
							}
						}
						resultDataList.add(resultMap);
					}
				}
//				else{
//					if(fieldList!=null && fieldList.size()>0){
//						Map resultMap = new LinkedMap();
//						InfoPath_DeeField field = null;
//						checkedFieldSize = 0;
//						for(int j = 0; j < fieldList.size(); j++){
//							field = fieldList.get(j);
//							resultMap.put(field.getName(),field);
//							if(Boolean.valueOf(field.getChecked())){
//								checkedFieldSize ++;
//							}
//						}
//						resultDataList.add(resultMap);
//						isSearch = true;
//					}
//				}
				List<InfoPath_DeeField> fieldList4Query = new ArrayList<InfoPath_DeeField>();
				if(fieldList!=null && fieldList.size()>0){
					InfoPath_DeeField field = null;
					for(int j = 0; j < fieldList.size(); j++){
						field = fieldList.get(j);
						if(Boolean.valueOf(field.getChecked())){
							fieldList4Query.add(field);
							checkedFieldSize ++;
						}
					}
				}
				
				if(checkedFieldSize == 0){
					checkedFieldSize = 1;
				}
				
				Pagination.setRowCount(totalCount);
				float colwidth = (float) 10;
				if(checkedFieldSize>0 && checkedFieldSize<10){
					colwidth = "search".equals(isSearch) ? 100/(float)checkedFieldSize : 96f/(float)checkedFieldSize;
				}
				modelAndView.addObject("columnwidth",colwidth);
				modelAndView.addObject("isSearch",isSearch);
				modelAndView.addObject("fieldlist",fieldList4Query);
				modelAndView.addObject("datalist", resultDataList);
				modelAndView.addObject("deetask", deetask);
				return modelAndView;

			}catch(Exception ex){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(\"" +Constantform.getString4CurrentUser("form.create.input.setting.deetask.resultdata.error.label")+ "\")");
				out.println("window.close();");
				out.println("</script>");
				out.flush();
				throw ex;
			}
		}
		
		/**
		 * 获取dee数据对象
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView getDeeTask(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
					DEEManager manager = new DEEManagerImpl();
					manager.init();
					String taskId = request.getParameter("taskId");
					FlowBean task = DEEConfigService.getInstance().getFlow(taskId);
					if(task != null){
						List<ParameterBean> taskParamList = DEEConfigService.getInstance().getFlowPara(taskId);
						Map<String,String> taskParams = new LinkedHashMap<String,String>();//参数
						Map<String,String> taskResults = new LinkedHashMap<String, String>();//结果集
						if(taskParamList!=null && taskParamList.size()>0){
							for(ParameterBean param : taskParamList){
								if(!"whereString".equals(param.getPARA_NAME())){
									taskParams.put(param.getPARA_NAME(), param.getPARA_VALUE()+","+param.getPARA_DESC());
								}
							}
						}
						String metaXml = task.getFLOW_META();
						if(metaXml.length()>0){
							Document doc  = dom4jxmlUtils.paseXMLToDoc(metaXml);
							Element root = doc.getRootElement();		
							List apps = root.elements("App");
							for (Object item : apps) {
								Element appEle = (Element) item;
								Attribute fattrib = appEle.attribute(IXmlNodeName.name);
								if (fattrib != null){
									String name = fCurrentCharSet.SelfXML2JDK(fattrib.getValue());
									taskResults.put(name, name);
								}
							}
						}
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("taskParamNames", taskParams.keySet());
						jsonObject.put("taskParams", taskParams);
						jsonObject.put("taskResultIds", taskResults.keySet());
						jsonObject.put("taskResults", taskResults);
						jsonObject.put("taskName", task.getDIS_NAME());
						jsonObject.put("taskId", task.getFLOW_ID());
						String string = jsonObject.toString();
						PrintWriter out = response.getWriter(); 
						out.write(string);
					}else{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("error", "error");//dee返回的錯誤信息
						String string = jsonObject.toString();
						PrintWriter out = response.getWriter(); 
						out.write(string);	
					}
					return null;
		}

		/**
		 * 根据选择的dee任务结果集获取对应的字段列表
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
		public ModelAndView getDeeTaskField(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String taskId = request.getParameter("taskId");
			String resultId = request.getParameter("resultId");
			String extendName = request.getParameter("extendName");
			IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
			if(StringUtils.isBlank(extendName)){
				extendName="查询控件交换引擎任务";
			}
			ISeeyonInputExtend extend = fextendmanager.findByName(extendName);
			if(extend != null && extend instanceof IInputRelation){
				RelationInputAtt relationInputAtt = ((IInputRelation)extend).getRelationInputAtts(taskId, resultId);
				if(relationInputAtt != null){
					PrintWriter out = response.getWriter(); 
					out.write(relationInputAtt.toJSONobject().toString());				
				}else{
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("error", "error");
					String string = jsonObject.toString();
					PrintWriter out = response.getWriter(); 
					out.write(string);	
				}
			}
			return null;
		}
		
		public ModelAndView checkRelationIsUsed(HttpServletRequest request, 
				HttpServletResponse response) throws Exception {
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			String relationConditionIdStr = request.getParameter("relationConditionId");
			long relationConditionId = NumberUtils.toLong(relationConditionIdStr);
			Collection<FormEvent> triggerConfigList = sessionobject.getTriggerConfigMap().values();
			for (FormEvent formEvent : triggerConfigList) {
				if(formEvent.getSourceType().intValue() == FormEvent.SOURCETYPE_RETURNWRITECONFIG){
					List<EventAction> actionList = formEvent.getActionList();
					if(!actionList.isEmpty()){
						EventAction eventAction = actionList.get(0); //每一个回写设置对应一个动作EventAction
						EventRelatedForm relationForm = eventAction.getRelatedForm();
						if(relationForm.getRelationConditionId().longValue() == relationConditionId){
							PrintWriter out = response.getWriter(); 
							out.write("true");
							out.close();
							break;
						}
					}
				}
			}
			return null;
		}
		//添加具体的DEE任务
		public ModelAndView editEventBind(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("form/formcreate/editEventBind");
			String eventBindId = request.getParameter("eventBindId");
			if(Strings.isBlank(eventBindId)){
				eventBindId = String.valueOf(UUIDLong.longUUID());
			}
			mav.addObject("eventBindId", eventBindId);
			List<String[]> taskList = new ArrayList<String[]>();
			List<CollaborationEventTask> collaborationEventTaskList = collaborationFormBindEventListener.getAllCollaborationEvent();
			if(CollectionUtils.isNotEmpty(collaborationEventTaskList))
			{
			for (CollaborationEventTask collaborationEventTask : collaborationEventTaskList) {
				taskList.add(new String[]{String.valueOf(collaborationEventTask.getId()), collaborationEventTask.getLabel()});
			}
			}
			mav.addObject("taskList", taskList);
			return mav;
		}
		private boolean hasSystemFieldName(String fieldName){
			List<String> list = new ArrayList<String>();
			//发起时间
			list.add(Constantform.getString4CurrentUser("form.system.start.time.field.label"));
			//发起人
			list.add(Constantform.getString4CurrentUser("form.system.start.member.field.label"));
			//创建人
			list.add(Constantform.getString4CurrentUser("form.system.creator.field.label"));
			//创建时间
			list.add(Constantform.getString4CurrentUser("form.system.createdate.field.label"));
			//审核人
			list.add(Constantform.getString4CurrentUser("form.system.approve.member.field.label"));
			//审核时间
			list.add(Constantform.getString4CurrentUser("form.system.approve.time.field.label"));
			list.add("id");
			//单据状态
			list.add(Constantform.getString4CurrentUser("form.query.sheetstatus.label"));
			//流程状态
			list.add(Constantform.getString4CurrentUser("formquery_sheetfinished.label"));
			//核定人
			list.add(Constantform.getString4CurrentUser("form.system.ratify.member.field.label"));
			//核定状态
			list.add(Constantform.getString4CurrentUser("form.system.ratifyflag.field.label"));
			//核定时间
			list.add(Constantform.getString4CurrentUser("form.system.ratify.time.field.label"));
			if(list.contains(fieldName)){
				return true;
			}
			return false;
		}
}