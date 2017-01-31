package www.seeyon.com.v3x.form.controller.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.joinwork.bpm.definition.BPMProcess;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.systemvalue.UserFlowId;
import www.seeyon.com.v3x.form.base.systemvalue.inf.ISystemValueManager;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Operation;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_ViewBindField;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.bind.flow.inf.IFlowTemplet;
import www.seeyon.com.v3x.form.manager.define.bind.inf.ISeeyonFormBind;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonSystemValue;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.form.FormLogManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
public class TriggerController extends BaseController{
	
	private final static Log log = LogFactory.getLog(TriggerController.class);
	
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private static final String resource_baseName = "www.seeyon.com.v3x.form.resources.i18n.FormResources";
    private TempleteCategoryManager templeteCategoryManager;	
	private FileToExcelManager fileToExcelManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private OrgManager orgManager; 
	private TempleteManager templeteManager;
	private FormLogManager formLogManager = (FormLogManager)SeeyonForm_Runtime.getInstance().getBean("formLogManager");;
	   
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
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

	/**
	 * 
	 */
	public ModelAndView index (HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null ;
	}
	
	/**
	 * 表单制作--触发设置页签
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTriggerSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formtrigger/triggerSet");
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
//		String flowid = request.getParameter("flowid");
//		List newlist = new ArrayList();
//		if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid)){
//			String[] flow = flowid.split("↗");
//			for(int i=0;i<flow.length;i++){
//				newlist.add(flow[i]);
//			}
//		}
//		if(flowid !=null)
//			sessionobject.setFlowidlist(newlist);
		sessionobject.setPageflag(IPagePublicParam.TRIGGERSET);
		return mav; 
	}
	
	/**
	 * 增加触发器到session中，还没持久化
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addTriggerSession(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		TriggerHelper.addTriggerObject(request, sessionobject);
		return super.redirectModelAndView("/triggerController.do?method=formTriggerNew");
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectTemplateIndex(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/selectTemplate/selectTemplateIndex");
		boolean isFormAdmin = MainHelper.isFORMAdmin(orgManager);
		String categoryHTML = iOperBase.categoryHTML(templeteCategoryManager).toString();
		return mav.addObject("isFormAdmin", isFormAdmin).addObject("categoryHTML", categoryHTML);
	}
	
	/**
	 * 
	 */
	public ModelAndView  showFormTemplets (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/selectTemplate/selectTemplets");
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		String condition = request.getParameter("condition");
		String textfield = null;
		FormAppMain fam = new FormAppMain();
		fam.setUserids(""+user.getId());
		//缺省值，如果没有查询是否启用，默认只显示启用的
		fam.setFormstart(1);//没有枚举，暂时我也扔数字了
		if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(condition)) {
			textfield = request.getParameter("categoryId");
			fam.setCategory(Long.parseLong(textfield));
		} else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
			textfield = request.getParameter("textfield");
		}
		List<Templete> tempList = null;
		try {
			List applst =  getIOperBase().queryAllData(fam);
			HttpSession session = request.getSession();
			SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			String fieldValue=null;
			if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
				fieldValue = textfield;
			}
			tempList = queryAllTemplate(applst,sessionobject,fieldValue);
		} catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			log.error("列表显示该分类下的所有的模板出错!", e);
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			log.error("列表显示该分类下的所有的模板出错!", e);
			OperHelper.creatformmessage(request,response,lst);
		}
		
		TempleteCategorysWebModel categorysModel = templeteCategoryManager.getCategorys(accountId, condition, textfield, tempList);	
		return mav.addObject("tempList", tempList).addObject("categorysModel", categorysModel);
	}
	
	/**
	 * @author Administrator
	 *
	 */
	public class TemplateObject extends Templete{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Long formAppId;

		public Long getFormAppId() {
			return formAppId;
		}

		public void setFormAppId(Long formAppId) {
			this.formAppId = formAppId;
		}
	}
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView taskTree(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/selectTask/taskTree");
//		DEEManager dm = (DEEManager)SeeyonForm_Runtime.getInstance().getBean("deeManager");
//		dm.init();
		List<FlowTypeBean> flowTypeList = DEEConfigService.getInstance().getFlowTypeList();
		if(flowTypeList==null){
			flowTypeList = new ArrayList<FlowTypeBean>();
		}
		mav.addObject("typeList", flowTypeList);
		return mav;
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView taskList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/selectTask/taskList");
		String flowType = request.getParameter("type_id");
		String flowName = request.getParameter("flowName");
		String taskType = request.getParameter("taskType");
		if(flowName!=null){
			flowName = Functions.urlDecoder(flowName);
		}
		String model = DEEConfigService.MODULENAME_FORM;
		if(Strings.isNotBlank(taskType)){
			model = DEEConfigService.MODULENAME_DATA;
		}
		List<FlowBean> flowList = new ArrayList<FlowBean>();//
		int pageNumber = Strings.isBlank(request.getParameter("page")) ? 1 : Integer.parseInt(request.getParameter("page"));
		Map<String,Object> listObj = DEEConfigService.getInstance().getFlowList(flowType, model, flowName, pageNumber, Pagination.getMaxResults());
		if(listObj!=null){
			flowList = (List<FlowBean>)listObj.get(DEEConfigService.MAP_KEY_RESULT);
			int rowCount = Integer.parseInt(listObj.get(DEEConfigService.MAP_KEY_TOTALCOUNT).toString());
			Pagination.setRowCount(rowCount);
		}
		mav.addObject("flowList", flowList);
		return mav;
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delTriggers(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String[] triggerUpdateId = request.getParameterValues("triggerUpdateId");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		Map<Long,FormEvent> triggerConfigMap = sessionobject.getTriggerConfigMap();
		if(triggerUpdateId!=null&&triggerUpdateId.length>0){
			for(String id:triggerUpdateId){
				triggerConfigMap.remove(Long.parseLong(id));
				//删除触发设置时 对历史数据的调度做删除操作
//				EventTriggerForHistoryData.removeTriggerForHistoryData(formAppId, Long.parseLong(id));
			}
		}
		return super.redirectModelAndView("/triggerController.do?method=formTriggerNew");
	}
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTriggerNew(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/triggerSet");
		return mav;
	}
	
	/**
	 * 取表单模板列表
	 * @param categorylst
	 * @return
	 */
	private List<Templete> queryAllTemplate(List categorylst,SessionObject sessionobject,String fieldValue){
		List<Templete> result = new ArrayList<Templete>();
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		ISeeyonFormAppManager iSeeyonFormAppManager = (ISeeyonFormAppManager)SeeyonForm_Runtime.getInstance().getBean("form_appmanager");
		for(int i=0;i<categorylst.size();i++){
			TemplateObject formApp = null;
			FormAppMain fam = null;
            if(categorylst.get(i) instanceof FormAppMain){
            	fam = (FormAppMain)categorylst.get(i);
            	
            }else{
				Object[] arr = (Object[])categorylst.get(i);
				for(int a=0;a<arr.length;a++){
					if(arr[a] instanceof FormAppMain){
						fam = (FormAppMain)arr[1];
					}
				}
            }
            if(fam!=null){
	            ISeeyonForm_Application app = iSeeyonFormAppManager.findById(fam.getId());
	            if(app==null||fam.getId().equals(sessionobject.getFormid()))continue;
	        	ISeeyonFormBind bind = app.getSeeyonFormBind();
	        	List<IFlowTemplet> list = bind.getFlowTempletList();
	        	for(IFlowTemplet temp:list){
	        		if(fieldValue!=null&&temp.getName().indexOf(fieldValue)==-1)continue;
	        		formApp = new TemplateObject();
					formApp.setId(temp.getId());
					formApp.setSubject(temp.getName());
					formApp.setCategoryId(fam.getCategory());
					formApp.setOrgAccountId(accountId);
					formApp.setFormAppId(fam.getId());
	        		result.add(formApp);
	        	}
            }
		}
		return result;
	}
	
	/**
	 * 数据拷贝
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dataCopySet (HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formtrigger/selectDataCondition/dataCopySet");
		String formid = request.getParameter("formId"); 
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		List<TableFieldDisplay> tableFieldList=sessionobject.getTableFieldList();
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(formid)) ;
		String targetFormName = fapp.getAppName();
		SeeyonDataDefine seedade = (SeeyonDataDefine)fapp.getDataDefine();
		String formName = sessionobject.getFormName();
		List<TableFieldDisplay> list = FormHelper.getTableFieldDisplayById(formid);
		String templeteId = request.getParameter("templeteId");  
		Templete templete = templeteManager.get(Long.valueOf(templeteId));	
		BPMProcess process = BPMProcess.fromXML(templete.getWorkflow()); 
		if(process == null ){
			return null ;
		}
		String[] formInfo = FormHelper.getFormPolicy(process);
		String formId = formInfo[1];
		SeeyonFormImpl fform=(SeeyonFormImpl)fapp.findFromById(Long.parseLong(formId));
		String operationId = formInfo[2]; 
		InfoPath_Operation operation = null;
  	    operation = fform.findOperationById(Long.parseLong(operationId));
  	    
		List<TableFieldDisplay> targetTableFieldList = new ArrayList<TableFieldDisplay>();
		for(TableFieldDisplay temp:list){
			TableFieldDisplay newTableFieldDisplay = new TableFieldDisplay();
			ISystemValueManager fISystemValueManager = SeeyonForm_Runtime.getInstance().getSystemValueManager();
			InfoPath_ViewBindField fViewBind = operation.findInitMapByName(temp.getBindname(), false);
			if(fViewBind != null && fViewBind.getFValueClassName() != null){
				ISeeyonSystemValue fsysvalue = fISystemValueManager.findByName(fViewBind.getFValueClassName());
				if(fsysvalue instanceof UserFlowId){
					continue;
				}
			}
			BeanUtils.copyProperties(newTableFieldDisplay, temp);
			if(temp.getInputtype().equals("relation")||(temp.getInputtype().equals("extend")&& temp.getExtend().equals("选择关联表单..."))){
				String[]relationInputType=FormHelper.getRelationInfo(list,temp);
				newTableFieldDisplay.setInputtype(relationInputType[1]);
				newTableFieldDisplay.setExtend(relationInputType[4]);
				newTableFieldDisplay.setDivenumtype(relationInputType[6]);
				if(Strings.isBlank(relationInputType[5])){
					newTableFieldDisplay.setDivenumlevel(1);
				}else{
					newTableFieldDisplay.setDivenumlevel(Integer.parseInt(relationInputType[5]));
				}
			}else if(temp.getInputtype().equals("select") || temp.getInputtype().equals("radio")){
				newTableFieldDisplay.setDivenumlevel(1);
			}
			if(Strings.isBlank(newTableFieldDisplay.getCompute())){
				targetTableFieldList.add(newTableFieldDisplay);
			}
		}
		String mainTableName = "";
		Boolean flag = true;
		List<TableFieldDisplay> newTableFieldList=new ArrayList<TableFieldDisplay>();
		for(TableFieldDisplay temp : tableFieldList){
			TableFieldDisplay newTableFieldDisplay= new TableFieldDisplay();
			BeanUtils.copyProperties(newTableFieldDisplay, temp);
			if(temp.getInputtype().equals("relation")||(temp.getInputtype().equals("extend")&& temp.getExtend().equals("选择关联表单..."))){
				String[]relationInputType = FormHelper.getRelationInfo(tableFieldList,temp);
				newTableFieldDisplay.setInputtype(relationInputType[1]);
				newTableFieldDisplay.setExtend(relationInputType[4]);
				newTableFieldDisplay.setDivenumtype(relationInputType[6]);
				if(Strings.isBlank(relationInputType[5])){
					newTableFieldDisplay.setDivenumlevel(1);
				}else{
					newTableFieldDisplay.setDivenumlevel(Integer.parseInt(relationInputType[5]));
				}
			}else if(temp.getInputtype().equals("select") || temp.getInputtype().equals("radio")){
				newTableFieldDisplay.setDivenumlevel(1);
			}
			if(flag){
				if(newTableFieldDisplay.getTablename().indexOf("main")!=-1){
					mainTableName = newTableFieldDisplay.getTablename();
					flag = false;
				} 
			}
			newTableFieldList.add(newTableFieldDisplay);
		}
		mav.addObject("newTableFieldList", newTableFieldList);
		mav.addObject("targetTableList", targetTableFieldList);
		mav.addObject("srcMainTableName", mainTableName);
		mav.addObject("formName", formName);
		mav.addObject("targetFormName", targetFormName);
		mav.addObject("targetMainTableName", seedade.getDataSource().getMasterTableName());
		return mav;
	}
	
	public TempleteManager getTempleteManager() {
		return templeteManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

}