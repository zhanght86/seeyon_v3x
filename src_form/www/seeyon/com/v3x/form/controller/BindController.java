package www.seeyon.com.v3x.form.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.util.Utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.SelectPersonOperation;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.FormAppAuthObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormFlowTemplete;
import www.seeyon.com.v3x.form.controller.pageobject.FormOperAuthObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.Operation;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TemplateObject;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TAppBindType;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.FormBody;
import com.seeyon.v3x.collaboration.domain.FormContent;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.controller.TempleteController;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class BindController extends TempleteController {
	private static Log log = LogFactory.getLog(BindController.class);

	private TempleteManager templeteManager;

	private AttachmentManager attachmentManager;

	private TempleteCategoryManager templeteCategoryManager;
	
	private MetadataManager metadataManager;
	
	private OrgManager orgManager; 
	
	private DocHierarchyManager docHierarchyManager;
	
	private ProjectManager projectManager;
	
	private ColSuperviseManager colSuperviseManager;
	
    private NewflowManager newflowManager;
    
    private FormBizConfigManager formBizConfigManager;	
    
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setTempleteManager(TempleteManager templeteManager){
		this.templeteManager = templeteManager;
	}
	
	public void setAttachmentManager(AttachmentManager attachmentManager){
		this.attachmentManager = attachmentManager;
	}
	
	public void setTempleteCategoryManager(TempleteCategoryManager templeteCategoryManager){
		this.templeteCategoryManager = templeteCategoryManager;
	}
	
	public void setMetadataManager(MetadataManager metadataManager){
		this.metadataManager = metadataManager;
	}
    
	public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }
	
	public void setFormBizConfigManager(FormBizConfigManager formBizConfigManager) {
		this.formBizConfigManager = formBizConfigManager;
	}
	
    @Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 显示绑定页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes={RoleType.FormAdmin,RoleType.Administrator})
    public ModelAndView showTemplateBind(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		ModelAndView mv = new ModelAndView("form/formBind/appBind");
		String formId = request.getParameter("id");
		String formBindView = request.getParameter("isFormView");
		SessionObject sessionobject1 = (SessionObject)session.getAttribute("SessionObject");
		int formType = TAppBindType.FLOWTEMPLATE.getValue();
		if(sessionobject1!=null){//表单制作时
			formType = sessionobject1.getFormType()==-1?TAppBindType.FLOWTEMPLATE.getValue():sessionobject1.getFormType();
		}
		if(StringUtils.isNotBlank(formBindView)&&!"".equals(formBindView)&&StringUtils.isNotBlank(formId)){
			mv = new ModelAndView("form/formBind/appBindSystemShow");
			ISeeyonForm_Application afapp=FormHelper.findAppFormByFormId(Long.parseLong(formId));//SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formId));
			if(afapp!=null){
				sessionobject1 = getIOperBase().loadFromnoInfoPath(afapp,formId);
				formType = sessionobject1.getFormType()==-1?TAppBindType.FLOWTEMPLATE.getValue():sessionobject1.getFormType();
				if(formType==TAppBindType.BASEDATA.getValue()){
					sessionobject1.setFormName(afapp.getAppName());
				}
				session.setAttribute("SessionObject",sessionobject1);
			}
		}
		if(sessionobject1.getEditflag() != null 
				&&!"".equals(sessionobject1.getEditflag())
				&&!"null".equals(sessionobject1.getEditflag())){
			if(sessionobject1.getPageflag().equals(IPagePublicParam.BASEINFO)){
	            //baseinfo数据收集
				OperHelper.baseInfoCollectData(request,sessionobject1);		
			}else if(sessionobject1.getPageflag().equals(IPagePublicParam.INPUTDATA)){
	            //收集inputdata页面的数据,当跳转到operconfig时收集数据，在本页操作不再进行收集操作
				if(request.getParameter("saveoperlst") == null
					&&request.getParameter("deltype") == null
					&&request.getParameter("selenum") == null){
//					增加防护
					try{
						OperHelper.inputDataCollectData(request, sessionobject1);
					}catch(SeeyonFormException e){
						log.error("保存录入定义页面信息时出错", e);
						List<String> lst = new ArrayList<String>();
						lst.add(e.getToUserMsg());
						OperHelper.creatformmessage(request,response,lst);
					}
				}
			}else if(sessionobject1.getPageflag().equals(IPagePublicParam.BINDINFO) && !"0".equals(request.getParameter("add"))){
				//添加信息管理绑定信息 by wusb at 2010-03-17
				BindHelper.systemSaveAppBindMain(request, sessionobject1);
			}
		}
		
		sessionobject1.setPageflag(IPagePublicParam.BINDINFO) ;
		
		if(formType==TAppBindType.FLOWTEMPLATE.getValue()){
		
			String flowid = request.getParameter("flowid");
			List newlist = new ArrayList();
			if(flowid !=null && !"".equals(flowid) && !"null".equals(flowid)){
				String[] flow = flowid.split("↗");
				for(int i=0;i<flow.length;i++){
					newlist.add(flow[i]);
				}
			}
			if(flowid !=null)
				sessionobject1.setFlowidlist(newlist);
			List<String> addSubject = new ArrayList<String>();
			List<String> allSubject = new ArrayList<String>();
			Map addMap = this.getSessionObj(session, "add");
			String key = null;
			if(addMap != null) {
				Set keys = addMap.keySet();
				Iterator it = keys.iterator();
				while(it.hasNext()) {
					key = (String)it.next();
					if(!Strings.isDigits(key))
						continue;
					Templete templete = (Templete)addMap.get(key);
					addSubject.add(templete.getSubject());
				}
			}
			
			HashMap flowMap = this.getSessionObj(session, "flow");
			Collection coll = flowMap.values();
			mv.addObject("flowCollection", coll);
			HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
			boolean isQueryFromDB = false;
			if(currentFormNewflow == null || currentFormNewflow.isEmpty()){
	            currentFormNewflow = new HashMap<Long, List<NewflowSetting>>();
	            isQueryFromDB = true;
	        }
	        if(coll !=null){
				Iterator it = coll.iterator();
				while(it.hasNext()) {
					FlowTempletImp templete = (FlowTempletImp)it.next();
					allSubject.add(templete.getName());
					
					//NF 第一次进入页面，加载新流程设置到内存
	                if(isQueryFromDB){
	                    List<NewflowSetting> newflowSettingList = newflowManager.getNewflowSettingList(templete.getId());
	                    currentFormNewflow.put(templete.getId(), newflowSettingList);
	                }
				}
			}
	        session.setAttribute("currentFormNewflow", currentFormNewflow);
	        mv.addObject("delMap", this.getSessionObj(session, "del"));
	        mv.addObject("addList", addSubject);
			mv.addObject("allList", allSubject);
			mv.addObject("formExtendField", getIOperBase().getSelectPeopleField(sessionobject1));
			
		}else if(formType==TAppBindType.INFOMANAGE.getValue()){
			
			List<String> allSubject = new ArrayList<String>();
			sessionobject1.setPageflag(IPagePublicParam.BINDINFO) ;
			String editflagbottom = sessionobject1.getEditflag();	
			mv.addObject("formApp", sessionobject1.getFormid());
			mv.addObject("formAppName", sessionobject1.getFormName());
			mv.addObject("editFlag", editflagbottom);
//			mv.addObject("delMap", this.getSessionObj(session, "del"));
			
			/*//菜单绑定
			List<Map<String,String>> menuList = new ArrayList<Map<String,String>>();
			Map<String, String> orgMenuMap = BindHelper.getOrgMenuMap(CurrentUser.get().getLoginAccount());
	    	Map<String,String> isExistMap = new HashMap<String,String>();
	    	for (String bindMenuId : orgMenuMap.keySet()) {
	    		String bindMenuName = orgMenuMap.get(bindMenuId);
	    		if(StringUtils.isNotBlank(bindMenuName) && !isExistMap.containsKey(bindMenuId) && !isExistMap.containsValue(bindMenuName)){
		    		Map<String,String> menuMap = new HashMap<String,String>();
		    		menuMap.put("bindMenuId", bindMenuId);
	        		menuMap.put("bindMenuName",bindMenuName);
	        		if(sessionobject1.getRootMenus().contains(bindMenuId)){
	        			menuMap.put("isCreateMenuFlag","1");
		        	}else{
		        		menuMap.put("isCreateMenuFlag","0");
		        	}
	        		menuList.add(menuMap);
	        		isExistMap.put(bindMenuId, bindMenuName);
	    		}
			}*/
			Collection<FormAppAuthObject> formBindList = sessionobject1.getFormAppAuthObjectMap().values();
	        for (FormAppAuthObject aao : formBindList) {
	        	if(aao!=null){
	        		allSubject.add(aao.getName());
	        	}
	        	
	        	/*//更新菜单名
	        	String mId = bindObject.getBindMenuId();
	        	String mName = bindObject.getBindMenuName();
	        	if(StringUtils.isNotEmpty(mId)){
	        		mName = orgMenuMap.get(mId);
	        		if(StringUtils.isNotEmpty(mName))
	        			bindObject.setBindMenuName(mName);
	        	}*/
	        	
	        	
	        	
	        	/*if("2".equals(sessionobject1.getFormstate()) && !isExistMap.containsKey(bindObject.getBindMenuId()) && !isExistMap.containsValue(bindObject.getBindMenuName())){
        			if(StringUtils.isNotBlank(bindObject.getBindMenuId()) && StringUtils.isNotBlank(bindObject.getBindMenuName())){
		        		Map<String,String> menuMap = new HashMap<String,String>();
		        		menuMap.put("bindMenuId", bindObject.getBindMenuId());
		        		menuMap.put("bindMenuName", bindObject.getBindMenuName());
		        		if(sessionobject1.getRootMenus().contains(bindObject.getBindMenuId())){
		        			menuMap.put("isCreateMenuFlag","1");
			        	}else{
			        		menuMap.put("isCreateMenuFlag","0");
			        	}
		        		menuList.add(menuMap);
		        		isExistMap.put(bindObject.getBindMenuId(), bindObject.getBindMenuName());
        			}
        		}
	        	if(sessionobject1.getRootMenus().contains(bindObject.getBindMenuId())){
	        		bindObject.setIsCreateMenuFlag("1");
	        	}else{
	        		bindObject.setIsCreateMenuFlag("0");
	        	}*/
	        }
	        mv.addObject("flowCollection",formBindList);
	        mv.addObject("allList", allSubject);
	        
	       /* //查询
	        List<QueryObject> queryConditionList = sessionobject1.getQueryConditionList();
	        mv.addObject("queryConditionList",queryConditionList);
	        
	        //统计
	        List<ReportObject> reportConditionList = sessionobject1.getReportConditionList();
	        mv.addObject("reportConditionList",reportConditionList);*/
	        
//	        mv.addObject("menuList",menuList);
	        
			mv.addObject("formExtendField", getIOperBase().getSelectPeopleField(sessionobject1));
			
		}else if(formType==TAppBindType.BASEDATA.getValue()){
			sessionobject1.setPageflag(IPagePublicParam.BINDINFO) ;
			FormAppAuthObject appAuthObject = sessionobject1.getAppAuthObject();
			String id = appAuthObject.getId();
			String dataField = appAuthObject.getDataField();
			appAuthObject.setDataField(BindHelper.turnData(dataField));
			List listObjAccess = null;
			if(Strings.isBlank(id)){
				id = String.valueOf(UUIDLong.longUUID());
			} else {
				//授权
				FormOperAuthObject operAuthObject = appAuthObject.getAppOperAuthObjectMap().get(sessionobject1.getFormName());
				if(operAuthObject != null){
					listObjAccess = operAuthObject.getObjAccessList();
					Set<TempleteAuth> set=new HashSet<TempleteAuth>();	
		    		for (int k = 0; k < listObjAccess.size(); k++) {
		    			FomObjaccess fobj = (FomObjaccess)listObjAccess.get(k);
		    			TempleteAuth ta = new TempleteAuth();
		    			ta.setAuthId(fobj.getUserid());
		    			SelectPersonOperation spc = new SelectPersonOperation();
		    			ta.setAuthType(spc.getTypeByTypeId(fobj.getUsertype()));
		    			set.add(ta);
					}
	
		    		String authName = this.getAuthName(set);
					mv.addObject("authName", authName);
					mv.addObject("templeteAuths", set);
				}
			}
			mv.addObject("id", id);
			mv.addObject("appAuthObject", appAuthObject);
		}
		
        TAppBindType[] appBindTypeAry = TAppBindType.values();
        List<Integer> appBindTypes = new ArrayList<Integer>();
        for (TAppBindType abt : appBindTypeAry) {
        	appBindTypes.add(abt.getValue());
		}
        String editflagbottom = sessionobject1.getEditflag();
        mv.addObject("editFlag", editflagbottom);
        List<FormPage> fps = sessionobject1.getFormLst();
        boolean isMultiPage = false;
        if(fps!=null && fps.size()>1){
        	isMultiPage=true;
        }
        mv.addObject("isMultiPage", isMultiPage);
		mv.addObject("formApp", sessionobject1.getFormid());
		mv.addObject("formAppName", sessionobject1.getFormName());
        mv.addObject("appBindTypes", appBindTypes);
        mv.addObject("formType", formType);
		return mv;
	}
	
	public ModelAndView checksaveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PrintWriter out = response.getWriter();
		boolean returnstr;	
		String templeteId = java.net.URLDecoder.decode(request.getParameter("templeteId"), "UTF-8");	
		String templeteNum = java.net.URLDecoder.decode(request.getParameter("templeteNum"), "UTF-8");
 
		HttpSession session = request.getSession();
		
			HashMap addMap = this.getSessionObj(session, "add");
			String key = "";
			if(addMap != null){
				Set keys = addMap.keySet();
				Iterator it = keys.iterator();
				while(it.hasNext()){
					key = (String)it.next();
					if(!Strings.isDigits(key)) {
						continue;
					}
					Templete templete = (Templete)addMap.get(key);
					if(StringUtils.isNotBlank(templeteId)){ //修改
						if(Long.parseLong(templeteId) != templete.getId()){
							if(templeteNum.equals(templete.getTempleteNumber())){
								returnstr = true;
						    	out.write(String.valueOf(returnstr));
						    	return null;
							}							
						}
					}else{
						if(templeteNum.equals(templete.getTempleteNumber())){
							returnstr = true;
					    	out.write(String.valueOf(returnstr));
					    	return null;
						}	
					}
					
				}			
		    }
			HashMap updateMap = this.getSessionObj(session, "update");
			if(updateMap != null){
				Set keys = updateMap.keySet();
				Iterator it = keys.iterator();
				while(it.hasNext()){
					key = (String)it.next();
					if(!Strings.isDigits(key)) {
						continue;
					}
					Templete templete = (Templete)updateMap.get(key);
					if(StringUtils.isNotBlank(templeteId)){ //修改
						if(Long.parseLong(templeteId) != templete.getId()){
							if(templeteNum.equals(templete.getTempleteNumber())){
								returnstr = true;
						    	out.write(String.valueOf(returnstr));
						    	return null;
							}							
						}
					}else{
						if(templeteNum.equals(templete.getTempleteNumber())){
							returnstr = true;
					    	out.write(String.valueOf(returnstr));
					    	return null;
						}	
					}
					
				}			
		    }
//			if(StringUtils.isNotBlank(templeteId)){
//				if(templeteManager.checkTempleteCodeIsUnique(templeteId, templeteNum) == false){
//					returnstr = true;
//			    	out.write(String.valueOf(returnstr));
//			    	return null;
//				}				
//			}			   
//			else{
//				if(templeteManager.checkTempleteCodeIsUnique("", templeteNum) == false){
//					returnstr = true;
//			    	out.write(String.valueOf(returnstr));
//			    	return null;
//				}
//			}
			out.write(String.valueOf(0));		
		return null;
		
	}
	
	
	/**
	 * 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemNewTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("form/formBind/templateSetup");
		String from = request.getParameter("from");
		Integer categoryType = Integer.parseInt(request.getParameter("categoryType"));
		//String categoryId = request.getParameter("categoryId");
		User user = CurrentUser.get();
		
		Long orgAccountId = user.getLoginAccount();
		
		String templeteId = request.getParameter("templeteId");
		long memberId = user.getId();
		V3xOrgMember member = orgManager.getMemberById(memberId);
		modelAndView.addObject("peopleSecretLevel", member.getSecretLevel());//成发集团项目 程炯 传入人员密级
		ColSummary summary = null;
		ColBody body = null;
		HttpSession session = request.getSession();
		
		if(StringUtils.isNotBlank(templeteId)){ //修改
			Templete templete = null;
			List<ColBranch> branchs = null;
			ColSuperviseDetail detail = null;
			List<SuperviseTemplateRole> roles = null;
			List<Attachment> attachments = null;
			HashMap addMap = this.getSessionObj(session, "add");
			Long tId = Long.parseLong(templeteId);
			//List list = (List)addMap.get(tId);
			templete = (Templete)addMap.get(String.valueOf(templeteId));
			boolean find = false;
			if(templete!=null){
				branchs = (List<ColBranch>)addMap.get(templeteId+"branch");
				detail = (ColSuperviseDetail)addMap.get(templeteId+"supervise");
				roles = (List<SuperviseTemplateRole>)addMap.get(templeteId+"role");
				attachments = (List<Attachment>)addMap.get(templeteId+"attachment");
				find = true;
			}
			if(!find){
				HashMap updateMap = this.getSessionObj(session, "update");
				templete = (Templete)updateMap.get(String.valueOf(templeteId));
				if(templete!=null){
					branchs = (List<ColBranch>)updateMap.get(templeteId+"branch");
					detail = (ColSuperviseDetail)updateMap.get(templeteId+"supervise");
					roles = (List<SuperviseTemplateRole>)updateMap.get(templeteId+"role");
					attachments = (List<Attachment>)updateMap.get(templeteId+"attachment");
					find = true;
				}
			}
			if(!find) {
				templete = this.templeteManager.get(Long.parseLong(templeteId));
				if(templete==null){
					PrintWriter pw = response.getWriter();
					pw.println("<script>");
					pw.println("alert(\""+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request),"form.bind.templateError.label")+"\");");
					pw.println("window.close();");
					pw.println("</script>");
					pw.flush();
					return null;
				}
				branchs = templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal());
				detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templete.getId());
				roles = colSuperviseManager.findSuperviseRoleByTemplateId(templete.getId());
				attachments = attachmentManager.getByReference(templete.getId(), templete.getId());
			}
			
			doShowColSuperName(modelAndView, detail, roles);
			
			summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
			body = (ColBody) XMLCoder.decoder(templete.getBody());
			
            BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
            String caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称
			
			if(StringUtils.isNotBlank(caseProcessXML)){			
	            modelAndView.addObject("hasWorkflow", Boolean.TRUE);
	            modelAndView.addObject("process_desc_by", FlowData.DESC_BY_XML);
	            
	            caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
	            
	            List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);
	
	            caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
	
	            modelAndView.addObject("process_xml", caseProcessXML);
	            modelAndView.addObject("workflowInfo", workflowInfo);
			}
			
//			预归档
			Long archiveId = null;
	        String archiveName = "";
	        if(summary.getArchiveId() != null){
	        	archiveId = summary.getArchiveId(); 
	        	archiveName = docHierarchyManager.getNameById(archiveId);
	        }
	        if("".equals(archiveName) || "null".equals(archiveName) || archiveName == null)
	        	summary.setArchiveId(null);
	        modelAndView.addObject("archiveName", archiveName);
			
	        Long quoteformtemId = null;
	        String quoteformtemName = "";
	        if(summary.getQuoteformtemId() !=null){
	        	quoteformtemId = summary.getQuoteformtemId();
	        	quoteformtemName = summary.getQuoteformtemName();
	        }
	        modelAndView.addObject("quoteformtemId", quoteformtemId);
	        modelAndView.addObject("quoteformtemName", quoteformtemName);
	        
			String authName = this.getAuthName(templete.getTempleteAuths());
			modelAndView.addObject("authName", authName);
			
            modelAndView.addObject("attachments", attachments);
            modelAndView.addObject("canDeleteOriginalAtts", true);    //允许删除原附件

            modelAndView.addObject("note", summary.getSenderOpinion());//发起人附言
            
            modelAndView.addObject("templete", templete);
            
            modelAndView.addObject("branchs", branchs);

            
            Map<Long, String> formTempleteMap = new HashMap<Long, String>();
            HashMap flowMap = this.getSessionObj(session, "flow");
            Collection coll = flowMap.values();
            for (Iterator iter = coll.iterator(); iter.hasNext();) {
                FlowTempletImp t = (FlowTempletImp) iter.next();
                formTempleteMap.put(t.getId(), t.getName());
            }
            modelAndView.addObject("formTempleteMap", formTempleteMap);

            //NF 从Session中取新流程设置
            HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
            modelAndView.addObject("newflowSettingList", currentFormNewflow.get(tId));
            session.setAttribute("currentFormTempleteId", tId);
		}
		else { //直接新建
            summary = new ColSummary();
            body = new ColBody();
            
            summary.setCanForward(true);
            summary.setCanArchive(true);
            summary.setCanDueReminder(true);
            summary.setCanModify(true);
            summary.setCanEditAttachment(true);
            summary.setCanTrack(true);
            summary.setCanEdit(true);
            summary.setCanEditAttachment(false);
            session.setAttribute("currentFormTempleteId", -1L);
        }
		
		//HttpSession session = request.getSession();
		SessionObject sessionobject1 = (SessionObject)session.getAttribute("SessionObject");
		String categoryId = sessionobject1.getFormsort().toString();
		long formApp = sessionobject1.getFormid();
		String formAppName = sessionobject1.getFormEditName()==null?sessionobject1.getFormName():sessionobject1.getFormEditName();

		Hashtable hash = FormHelper.getDefault(session);
		modelAndView.addObject("formApp", formApp);
		modelAndView.addObject("formAppName", formAppName);
		modelAndView.addObject("defaultFormId", hash.get("formId"));
		modelAndView.addObject("defaultFirstNodeOperationId", hash.get("firstNodeOperationId"));
		modelAndView.addObject("formLst", sessionobject1.getFormLst());
		
		//modelAndView.addObject("defaultValue", FormHelper.getDefault(formApp));
		modelAndView.addObject("defaultOtherNodeOperationId", hash.get("otherNodeOperationId"));		
		/**
		List<TempleteCategory> templeteCategories = this.templeteCategoryManager.getCategorys(orgAccountId, categoryType);
		StringBuffer categoryHTML = new StringBuffer();
		
		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory category = templeteCategories.get(i);
			if(!"SYS".equalsIgnoreCase(from) && TempleteUtil.isClass1Category(category) && !category.isCanManager(memberId, orgAccountId)){
				templeteCategories.remove(category);
				i--;
			}
		}
		category2HTML(templeteCategories, categoryHTML, new Long(categoryType), 1);
		
		modelAndView.addObject("categoryHTML", categoryHTML);
		**/
		modelAndView.addObject("categoryId", categoryId);
		
        modelAndView.addObject("summary", summary);
        modelAndView.addObject("body", body);
		
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        Metadata colFlowPermPolicyMetadata = colMetadata.get(MetadataNameEnum.col_flow_perm_policy.name());//单独传递，免得它以后改名
        Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colFlowPermPolicyMetadata", colFlowPermPolicyMetadata);
        modelAndView.addObject("comMetadata", comMetadata);
        
        List<ProjectSummary> projectList = projectManager.getProjectList(user,orgAccountId);
		modelAndView.addObject("relevancyProject", projectList);
		modelAndView.addObject("extendFormField",this.iOperBase.getSelectExtendField(this.getSessionObj(session), "选择部门","选择单位"));
		return modelAndView;
	}
	private void doShowColSuperName(ModelAndView modelAndView,
			ColSuperviseDetail detail, List<SuperviseTemplateRole> roles) {
		if(detail != null) {
			Set<ColSupervisor> supervisors = detail.getColSupervisors();
			if(supervisors != null && supervisors.size()>0) {
		    	StringBuffer ids = new StringBuffer();
		    	for(ColSupervisor supervisor:supervisors)
		    		ids.append(supervisor.getSupervisorId() + ",");
		    	modelAndView.addObject("colSupervisors", ids.substring(0, ids.length()-1));
			} 
			modelAndView.addObject("colSupervise", detail);
			
			String superviseRole = "";
			String supervisorNames = "" ;
			
			supervisorNames = detail.getSupervisors()==null?"":detail.getSupervisors();
			
			if(roles != null && roles.size()>0) {
		    	for(SuperviseTemplateRole role : roles){
		    		superviseRole += role.getRole();
		    		superviseRole += ",";
		    		if("sender".equals(role.getRole())) {
		    			supervisorNames += (supervisorNames==null||"".equals(supervisorNames)?"":Constant.getCommonString("common.separator.label")) 
		    			+ com.seeyon.v3x.system.Constants.getString4CurrentUser("sys.role.rolename.Sender");
		    		}else if("senderDepManager".equals(role.getRole()))
		    			supervisorNames += (supervisorNames==null||"".equals(supervisorNames)?"":Constant.getCommonString("common.separator.label"))
		    			+ com.seeyon.v3x.system.Constants.getString4CurrentUser("sys.role.rolename.SenderDepManager");
		    	}
			}
			if(superviseRole.length()>0 && superviseRole.endsWith(",")){
				superviseRole = superviseRole.substring(0,superviseRole.length()-1);
			}
			modelAndView.addObject("colSuperviseRole", superviseRole);
			modelAndView.addObject("supervisorNames", supervisorNames);
		}
	}
	
	public ModelAndView setAppSubject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new  ModelAndView("form/formBind/setAppSubject") ;
		HttpSession session = request.getSession() ;
		SessionObject sessionobject1 = (SessionObject)session.getAttribute("SessionObject");
		List<String> systemValues = this.getIOperBase().checkFormbindSystemValue(sessionobject1) ;
		List<String> fieldsValue = this.getIOperBase().checkFormbindField(sessionobject1, request) ;
		session.setAttribute("fieldsValue", fieldsValue) ;
		session.setAttribute("systemValues", systemValues) ;		
		return mav ;
	}
	
	
	/**
	 * 应用绑定新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemNewTempleteAppBind(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("form/formBind/appBindTemplateSetup");
		String bindIdStr = request.getParameter("id");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String categoryId = sessionobject.getFormsort().toString();
		long formApp = sessionobject.getFormid();
		String formAppName = sessionobject.getFormName();
		
		if(StringUtils.isNotBlank(bindIdStr)){ //修改
			String subject=null;
			FormAppAuthObject aao = sessionobject.getFormAppAuthObjectMap().get(bindIdStr);
			if(aao!=null){
				subject = aao.getName();
	    		modelAndView.addObject("subject", subject);
	    		modelAndView.addObject("dataField", BindHelper.turnData(aao.getDataField()));
	    		modelAndView.addObject("dataFieldValue", aao.getDataFieldValue());
	    		Map<String,String> tableMap = BindHelper.getTableNames(sessionobject,aao.getDataField());
	    		modelAndView.addObject("masterTableName",tableMap.get("masterTableName"));
	    		modelAndView.addObject("slaveTableName",tableMap.get("slaveTableName"));
	    		
	    		modelAndView.addObject("resultSort", aao.getResultSort());
	    		modelAndView.addObject("resultSortValue", aao.getResultSortValue());
	    		
	    		modelAndView.addObject("customQueryField",BindHelper.turnData(aao.getCustomQueryField()));
	    		modelAndView.addObject("customQueryFieldValue", aao.getCustomQueryFieldValue());
	    		FormOperAuthObject resultAoao = null;
	    		if(!aao.getAppOperAuthObjectMap().values().isEmpty()){
	    			for (FormOperAuthObject aoao : aao.getAppOperAuthObjectMap().values()) {
	    				if(aoao!=null){
	    					resultAoao = aoao;
	    					break;
	    				}
					}
	    		}
				modelAndView.addObject("flowCollection", resultAoao);
				modelAndView.addObject("id", aao.getId());
			}
			modelAndView.addObject("operation", "update");
		}
		else { //直接新建
			String idStr =  String.valueOf(UUIDLong.longUUID());
            session.setAttribute("currentFormTempleteId", -1L);
            modelAndView.addObject("operation", "add");
            modelAndView.addObject("id", idStr);
        }
		
		//操作权限
		//新增
		List<FormPage> addAuthorityList = new ArrayList<FormPage>();
		//修改
		List<FormPage> updateAuthorityList = new ArrayList<FormPage>();
		//浏览
		List<FormPage> browseAuthorityList = new ArrayList<FormPage>();
		
		List<FormPage> fps = sessionobject.getFormLst();
		for (FormPage formPage : fps) {
			List<Operation> addOperation = new ArrayList<Operation>();
			List<Operation> updateOperation = new ArrayList<Operation>();
			List<Operation> browseOperation = new ArrayList<Operation>();
			List<Operation> opers = formPage.getOperlst();
			for (Operation oper : opers) {
				if(IXmlNodeName.C_sVluae_add.equals(oper.getType())){
					addOperation.add(oper);
				}else if(IXmlNodeName.C_sVluae_update.equals(oper.getType())){
					updateOperation.add(oper);
				}else if(IXmlNodeName.C_sVluae_readonly.equals(oper.getType())){
					browseOperation.add(oper);
				}
			}
			
			FormPage addFormPage =new FormPage();
			addFormPage.setName(formPage.getName());
			addFormPage.setFormPageId(formPage.getFormPageId());
			addFormPage.setOperlst(addOperation);
			addAuthorityList.add(addFormPage);
			
			FormPage updateFormPage =new FormPage();
			updateFormPage.setName(formPage.getName());
			updateFormPage.setFormPageId(formPage.getFormPageId());
			updateFormPage.setOperlst(updateOperation);
			updateAuthorityList.add(updateFormPage);
			
			FormPage browseFormPage =new FormPage();
			browseFormPage.setName(formPage.getName());
			browseFormPage.setFormPageId(formPage.getFormPageId());
			browseFormPage.setOperlst(browseOperation);
			browseAuthorityList.add(browseFormPage);
			
		}
		//操作权限
		modelAndView.addObject("addAuthorityList", addAuthorityList);
		modelAndView.addObject("updateAuthorityList", updateAuthorityList);
		modelAndView.addObject("browseAuthorityList", browseAuthorityList);
		
		
		List<String> allSubject = new ArrayList<String>();
		Collection<FormAppAuthObject> formBindList = sessionobject.getFormAppAuthObjectMap().values();
		for (FormAppAuthObject bindObject : formBindList) {
        	if(bindObject!=null){
        		Map<String, FormOperAuthObject> oaoMap = bindObject.getAppOperAuthObjectMap();
        		for (FormOperAuthObject formOperAuthObject : oaoMap.values()) {
        			allSubject.add(formOperAuthObject.getName());
				}
        	}
		}
		
		Hashtable hash = FormHelper.getDefault(session);
		modelAndView.addObject("formApp", formApp);
		modelAndView.addObject("formAppName", formAppName);
		modelAndView.addObject("allList", allSubject);
		modelAndView.addObject("defaultFormId", hash.get("formId"));
		modelAndView.addObject("defaultFirstNodeOperationId", hash.get("firstNodeOperationId"));
		modelAndView.addObject("formLst", sessionobject.getFormLst());
		modelAndView.addObject("defaultOtherNodeOperationId", hash.get("otherNodeOperationId"));		
		modelAndView.addObject("categoryId", categoryId);
		modelAndView.addObject("extendFormField",this.iOperBase.getSelectExtendField(this.getSessionObj(session), "选择部门","选择单位"));
		return modelAndView;
	}
	
	/**
	 * 应用授权新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemNewTempleteAppAuth(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("form/formBind/appOperAuthTemplateSetup");
		String idStr = request.getParameter("id");
		String subject = request.getParameter("subject");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String categoryId = sessionobject.getFormsort().toString();
		long formApp = sessionobject.getFormid();
		String formAppName = sessionobject.getFormName();
		
		Map<String,String> browseAuthMap=new HashMap<String,String>();
		
		if(StringUtils.isNotBlank(subject)){ //修改
            FormAppAuthObject abo = sessionobject.getFormAppAuthObjectMap().get(idStr);
            FormOperAuthObject aao = abo.getAppOperAuthObjectMap().get(subject);
			subject = aao.getName();
    		modelAndView.addObject("subject", subject);
    		
    		//添加权限
    		Map<String, String> addMap = aao.getAddShowDetail();
    		if(addMap!=null){
	    		String addFormName = addMap.get("formName");
	    		String addOperName = addMap.get("operName");
	    		if(StringUtils.isNotBlank(addFormName) && addFormName.endsWith("|")){
	    			addFormName=addFormName.substring(0,addFormName.length()-1);
	    		}
	    		if(StringUtils.isNotBlank(addOperName) && addOperName.endsWith("|")){
	    			addOperName=addOperName.substring(0,addOperName.length()-1);
	    		}
	    		modelAndView.addObject("addAuthoritySelect", addFormName+"."+addOperName);
    		}
    		//修改权限
    		Map<String, String> updateMap = aao.getUpdateShowDetail();
    		if(updateMap!=null){
	    		String upFormName = updateMap.get("formName");
	    		String upOperName = updateMap.get("operName");
	    		if(StringUtils.isNotBlank(upFormName) && upFormName.endsWith("|")){
	    			upFormName=upFormName.substring(0,upFormName.length()-1);
	    		}
	    		if(StringUtils.isNotBlank(upOperName) && upOperName.endsWith("|")){
	    			upOperName=upOperName.substring(0,upOperName.length()-1);
	    		}
	    		modelAndView.addObject("updateAuthoritySelect", upFormName+"."+upOperName);
    		}
    		//浏览权限
    		List<Map<String, String>> browseList  = aao.getBrowseShowDetail();
    		if(browseList!=null){
	    		for (Map<String, String> map : browseList) {
	    			browseAuthMap.put(map.get("formName"),map.get("operName"));
				}
    		}
    		modelAndView.addObject("lockedAuthority", aao.isAllowlock());
    		modelAndView.addObject("deleteAuthority", aao.isAllowdelete());
    		modelAndView.addObject("exportAuthority", aao.isAllowexport());
    		modelAndView.addObject("queryAuthority", aao.isAllowquery());
    		modelAndView.addObject("reportAuthority", aao.isAllowstat());
    		modelAndView.addObject("logAuthority", aao.isAllowlog());
    		modelAndView.addObject("printAuthority", aao.isAllowprint());
    		
    		modelAndView.addObject("queryArea", aao.getQueryArea());
    		modelAndView.addObject("queryAreaValue", aao.getQueryAreaValue());
    		
    		modelAndView.addObject("mainBindXml",  aao.getXmlString());
    			
    		//授权
    		List<FomObjaccess> listObjAccess = aao.getObjAccessList();
    		Set<TempleteAuth> set=new HashSet<TempleteAuth>();	
    		for (int k = 0; k < listObjAccess.size(); k++) {
    			FomObjaccess fobj = (FomObjaccess)listObjAccess.get(k);
    			TempleteAuth ta = new TempleteAuth();
    			ta.setAuthId(fobj.getUserid());
    			SelectPersonOperation spc = new SelectPersonOperation();
    			ta.setAuthType(spc.getTypeByTypeId(fobj.getUsertype()));
    			set.add(ta);
			}
    		String authName = this.getAuthName(set);
			modelAndView.addObject("authName", authName);
			modelAndView.addObject("templeteAuths", set);
			modelAndView.addObject("operation", "update");
		}
		else { //直接新建
            session.setAttribute("currentFormTempleteId", -1L);
            modelAndView.addObject("operation", "add");
        }
		
		//操作权限
		//新增
		List<FormPage> addAuthorityList = new ArrayList<FormPage>();
		//修改
		List<FormPage> updateAuthorityList = new ArrayList<FormPage>();
		//浏览
		List<FormPage> browseAuthorityList = new ArrayList<FormPage>();
		
		List<FormPage> fps = sessionobject.getFormLst();
		for (FormPage formPage : fps) {
			List<Operation> addOperation = new ArrayList<Operation>();
			List<Operation> updateOperation = new ArrayList<Operation>();
			List<Operation> browseOperation = new ArrayList<Operation>();
			List<Operation> opers = formPage.getOperlst();
			for (Operation oper : opers) {
				if(IXmlNodeName.C_sVluae_add.equals(oper.getType())){
					addOperation.add(oper);
				}else if(IXmlNodeName.C_sVluae_update.equals(oper.getType())){
					updateOperation.add(oper);
				}else if(IXmlNodeName.C_sVluae_readonly.equals(oper.getType())){
					browseOperation.add(oper);
				}
			}
			
			FormPage addFormPage =new FormPage();
			addFormPage.setName(formPage.getName());
			addFormPage.setFormPageId(formPage.getFormPageId());
			addFormPage.setOperlst(addOperation);
			addAuthorityList.add(addFormPage);
			
			FormPage updateFormPage =new FormPage();
			updateFormPage.setName(formPage.getName());
			updateFormPage.setFormPageId(formPage.getFormPageId());
			updateFormPage.setOperlst(updateOperation);
			updateAuthorityList.add(updateFormPage);
			
			FormPage browseFormPage = new FormPage();
			browseFormPage.setName(formPage.getName());
			browseFormPage.setFormPageId(formPage.getFormPageId());
			browseFormPage.setOperlst(browseOperation);
			browseAuthorityList.add(browseFormPage);
			
		}
		//操作权限
		modelAndView.addObject("addAuthorityList", addAuthorityList);
		modelAndView.addObject("updateAuthorityList", updateAuthorityList);
		modelAndView.addObject("browseAuthorityList", browseAuthorityList);
		modelAndView.addObject("browseAuthMap",browseAuthMap);
		
		Hashtable hash = FormHelper.getDefault(session);
		modelAndView.addObject("id", idStr);
		modelAndView.addObject("subject", subject);
		modelAndView.addObject("formApp", formApp);
		modelAndView.addObject("formAppName", formAppName);
		modelAndView.addObject("defaultFormId", hash.get("formId"));
		modelAndView.addObject("defaultFirstNodeOperationId", hash.get("firstNodeOperationId"));
		modelAndView.addObject("formLst", sessionobject.getFormLst());
		modelAndView.addObject("defaultOtherNodeOperationId", hash.get("otherNodeOperationId"));		
		modelAndView.addObject("categoryId", categoryId);
		modelAndView.addObject("extendFormField",this.iOperBase.getSelectExtendField(this.getSessionObj(session), "选择部门","选择单位"));
		return modelAndView;
	}
	
	
	public ModelAndView formQueryTremSetAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/formBind/queryTermSet");
		return mav; 
	}
	
	/**
	 * 应用绑定保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemSaveAppBindMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		BindHelper.systemSaveAppBindMain(request, sessionobject);
		return showTemplateBind(request,response);
	}
	
	public ModelAndView systemSaveBaseData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
//		String logfieldString=request.getParameter("logxml");
//		sessionobject.setLogfieldString(logfieldString);
		String logdatafield=request.getParameter("logdatafield");
		String[] logdatafields=logdatafield.split(",");
		List list=new ArrayList();
		for(int i=0;i<logdatafields.length;i++){
			list.add(logdatafields[i]);
		}
		sessionobject.setLogFieldList(list);
		String id = request.getParameter("id");
		//区分列表显示还是授权
		String flag = request.getParameter("flag");
		FormAppAuthObject abo = sessionobject.getAppAuthObject();
		abo.setId(id);
		String formName = sessionobject.getFormName();
		if(Strings.isNotBlank(flag)){
			FormOperAuthObject aao = abo.getAppOperAuthObjectMap().get(formName);
			if(aao==null){
	    		aao = new FormOperAuthObject();
	    		abo.getAppOperAuthObjectMap().put(formName, aao);
	    	}
			String authInfo = request.getParameter("authInfo");
	    	String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
	    	List<FomObjaccess> objAccessList = new ArrayList<FomObjaccess>();
	    	if(authInfos != null){
	    		for (String[] strings : authInfos) {
	    			//保存表单对象授权表
	    			FomObjaccess fob=new FomObjaccess();
	    			fob.setIdIfNew();
	    			fob.setRefAppmainId(sessionobject.getFormid());
	    			fob.setObjectname(formName);
	    			fob.setObjecttype(IPagePublicParam.C_iObjecttype_bill);
	    			fob.setState(0);
	    			SelectPersonOperation spc = new SelectPersonOperation();
	    			fob.setUsertype(spc.changeType(strings[0]));
	    			fob.setUserid(Long.parseLong(strings[1]));
	    			objAccessList.add(fob);
	    		}
	    	}
	    	aao.setObjAccessList(objAccessList);
		} else {
			abo.setXmlString(request.getParameter("mainBindXml"));
			abo.setDataField(request.getParameter("dataField"));
			abo.setDataFieldValue(request.getParameter("dataFieldValue"));
			abo.setResultSort(request.getParameter("resultSort"));
			abo.setResultSortValue(request.getParameter("resultSortValue"));
		}
		return showTemplateBind(request,response);
	}
	
	
	/**
	 * 应用绑定保存 - 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemSaveTempleteAppBind(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String bindId = null;
		if(StringUtils.isNotBlank(operation) && "add".equals(operation)){//新建
			//保存表单主表
			Map<String, FormAppAuthObject> authMap = sessionobject.getFormAppAuthObjectMap();
			FormAppAuthObject abo = BindHelper.getAppAuthObject(request, authMap.get(request.getParameter("id")));
			authMap.put(abo.getId(),abo);
			bindId = abo.getId();
		}else if(StringUtils.isNotBlank(operation) && "update".equals(operation)){//修改
			//保存表单主表
			FormAppAuthObject aao = sessionobject.getFormAppAuthObjectMap().get(request.getParameter("id"));
			BindHelper.getAppAuthObject(request, aao);
			bindId = aao.getId();
		}
		
		PrintWriter pw = response.getWriter();
		pw.println("<script language=\"javascript\">");
		pw.println("if(window.dialogArguments){");
		pw.println("  window.returnValue = \"" + request.getParameter("id") +"\";");
		pw.println("}");
		pw.println("window.close();");
		pw.println("</script>");
		return null;
	}
	
	
	/**
	 * 应用授权保存 - 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemSaveTempleteAppAuth(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		String mainBindXml = request.getParameter("mainBindXml");
		String subject = request.getParameter("subject");
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		
		Map<String, FormAppAuthObject> authMap = sessionobject.getFormAppAuthObjectMap();
		
		if(StringUtils.isNotBlank(operation) && "add".equals(operation)){//新建
			//保存表单主表
			/*FormAppAuthObject bindObj = BindHelper.getAppAuthObject(sessionobject,request.getParameter("id"));
			if(bindObj==null){
				bindObj = new FormAppBindObject();
				bindObj.setId(String.valueOf(UUIDLong.longUUID()));
				bindObj.setBindSet(request.getParameter("id"));
			}*/
			
			FormAppAuthObject abo = authMap.get(request.getParameter("id"));
			if(abo==null){
				abo = BindHelper.getAppAuthObject(request, null);
			}
			BindHelper.getOperAuthObject(request, abo);
			authMap.put(abo.getId(),abo);
			
		}else if(StringUtils.isNotBlank(operation) && "update".equals(operation)){//修改
			//保存表单主表
			FormAppAuthObject abo = authMap.get(request.getParameter("id"));
			if(abo!=null){
				BindHelper.getOperAuthObject(request, abo);
			}
		}
		
		PrintWriter pw = response.getWriter();
		pw.println("<script language=\"javascript\">");
		pw.println("if(window.dialogArguments){");
		pw.println("  window.returnValue = \""+ www.seeyon.com.v3x.form.utils.StringUtils.Java2JavaScriptStr(subject+"@@"+mainBindXml)+"\";");
		pw.println("}");
		pw.println("window.close();");
		pw.println("</script>");
		return null;
	}
	
	/**
	 * 系统模板保存 - 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemSaveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		
		String type = request.getParameter("type");
		String from = request.getParameter("from");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));	
		
		Timestamp createDate = new Timestamp(System.currentTimeMillis());
		//String categoryId = request.getParameter("categoryId");
		Templete templete = new Templete();
		bind(request, templete);
		//模板名称过滤\t
		if(templete.getSubject().indexOf('\t') !=-1){
			int Pos_End = -1;
		    for(int i=templete.getSubject().length()-1;i>=0;i--)
		    {
		     if(templete.getSubject().charAt(i) != '\t')
		      {
		         Pos_End = i;
		         break;
		      }
		    }
			String Str_Return = "";
			if(Pos_End!=-1)
			{
				for(int i=0;i<=Pos_End;i++)
				{
					Str_Return = Str_Return + templete.getSubject().charAt(i);
				}
			}
			templete.setSubject(Str_Return);
		}
		templete.setCollSubject(request.getParameter("colSubject")) ;
		FormBody formBody = new FormBody();
		// 正文格式不保存流程
		if (!Templete.Type.text.name().equals(type)) {	
			try{
				templete.setWorkflow(this.doColWorkflow(request,formBody));
			}catch(Exception e){
				FlowData flowData = FlowData.flowdataFromRequest();
				log.error("解析表单模板时发生错误，error xml:\r\n"+flowData.getXml(), e);
				
				PrintWriter pw = response.getWriter();
				pw.println("<script language=\"javascript\">");
				pw.println("alert(\""+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources", LocaleContext.getLocale(request),"form.bind.parseWorkflowError.label")+"\");");
				pw.println("parent.buttonEnable();");
				pw.println("</script>");
				return null;
			}
		}

		// 流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
			String body = this.doColBody(request,formBody);
			templete.setBody(body);
		}
		
		// 基准时长
		String referenceTime = request.getParameter("referenceTime");
		if (Strings.isBlank(referenceTime)) 
			templete.setStandardDuration(0);
		else
			templete.setStandardDuration(Integer.parseInt(referenceTime));

		//属性
		String summary = null;
		if (Templete.Type.templete.name().equals(type)) {
			summary = this.doColSummary(request);
		}
		else{
			ColSummary colSummary = new ColSummary();
			colSummary.setSubject(templete.getSubject());
			
			summary = XMLCoder.encoder(colSummary);
		}
		
		templete.setSummary(summary);
		
		HttpSession session = request.getSession();
		SessionObject obj = this.getSessionObj(session);
		
		templete.setCreateDate(createDate);
		if(Strings.isNotBlank(obj.getAttachManId())){
			templete.setMemberId(Long.parseLong(obj.getAttachManId()));
		}else{
			templete.setMemberId(user.getId());
		}
		
		templete.setIsSystem(true);
		templete.setOrgAccountId(user.getLoginAccount());
		
		if(obj != null){
			if("0".equals(obj.getFormstate()))
			    templete.setState(Templete.State.invalidation.ordinal());
			else
				templete.setState(Templete.State.normal.ordinal());
		}
		
		boolean isSave = templete.isNew();
		templete.setIdIfNew();
		
		long templeteId = templete.getId();
		
		//授权信息
		String authInfo = request.getParameter("authInfo");
		String authInfoChanged = request.getParameter("authInfoChanged");
		String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
		if(authInfos != null){
			int i = 0;
			for (String[] strings : authInfos) {
				TempleteAuth auth = new TempleteAuth();
				
				auth.setIdIfNew();
				auth.setAuthType(strings[0]);
				auth.setAuthId(Long.parseLong(strings[1]));
				auth.setSort(i++);
				auth.setObjectId(templeteId);
				
				templete.getTempleteAuths().add(auth);
			}
		}
		
        //		附件信息
		/*HashMap attMap = new HashMap();
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_fileUrl, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_fileUrl));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_mimeType, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_mimeType));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_size, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_size));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_createDate, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_createDate));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_filename, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_filename));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_type, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_type));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_needClone, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_needClone));
		attMap.put(Constants.FILEUPLOAD_INPUT_NAME_description, request.getParameterValues(Constants.FILEUPLOAD_INPUT_NAME_description));
		*/
		
		//list.add(attMap);
		
//		保存分支条件
        String[] arr = request.getParameterValues("branchs");
        List<ColBranch> branchs = null;
        if(arr != null) {
        	String tmp = null;
        	String[] tmps = null;
        	branchs = new ArrayList<ColBranch>();
        	for(int i=0;i<arr.length;i++) {
        		tmp = arr[i];
        		if(tmp != null) {
        			tmps = tmp.split("↗");
        			if(tmps != null) {
        				ColBranch branch = new ColBranch();
        				branch.setTemplateId(templeteId);
        				branch.setLinkId(Long.parseLong(tmps[0]));
        				branch.setId(Long.parseLong(tmps[1]));
        				branch.setConditionType(Integer.parseInt(tmps[2]));
        				branch.setFormCondition(tmps[3]);
        				branch.setConditionTitle(tmps[4]);
        				branch.setIsForce("1".equals(tmps[5])?1:0);
        				if(tmps.length > 6) {
	        				branch.setConditionDesc("".equals(tmps[6])||"null".equals(tmps[6])?null:tmps[6]);
	        				if(tmps.length>7)
	        					branch.setConditionBase(tmps[7]);
        				}
        				branch.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
        				branchs.add(branch);
        			}
        		}
        	}
        		//this.templeteManager.saveBranch(branchs);
        }
        
        //把督办信息保存到session
        ColSuperviseDetail detail = this.saveColSupervise(request, templete);
        List<SuperviseTemplateRole> roles = this.saveSuperviseTemplateRole(request, templete);
        //end
        
        List<Attachment> attachments = this.attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.collaboration, templeteId, templeteId, request);
		if(attachments != null && attachments.size()>0)
			templete.setHasAttachments(true);
        
        
        HashMap flowMap = this.getSessionObj(session, "flow");
		HashMap addMap = this.getSessionObj(session, "add");
		if(Strings.isNotBlank(authInfoChanged) && authInfoChanged.equals("true")){
			setChange(session);
		}
		
		if(isSave){
			/*SessionObject obj = (SessionObject)session.getAttribute("SessionObject");
			TemplateObject tempObj = obj.getTemplateobj();
			if(tempObj==null){
				tempObj = new TemplateObject();
				obj.setTemplateobj(tempObj);
			}
			HashMap templateMap = tempObj.getAddMap();
			if(templateMap==null){
				templateMap = new HashMap();
				tempObj.setAddMap(templateMap);
			}
			templateMap.put(templete.getId(), list);*/
			this.saveToMap(addMap, templete, branchs, detail,roles,attachments);
		}else{
			if(addMap.get(String.valueOf(templeteId))!=null){
				this.saveToMap(addMap, templete, branchs, detail,roles,attachments);
			}
            else{
				HashMap updateMap = this.getSessionObj(session, "update");
				this.saveToMap(updateMap, templete, branchs, detail,roles,attachments);
			}
		}
		
		this.setFlowTemplate(flowMap, templete);
        
//      NF 从Session中取新流程设置
        HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
        List<NewflowSetting> newflowSettingList = this.saveNewflowSettings(request, templeteId);
        currentFormNewflow.put(templeteId, newflowSettingList);
        session.setAttribute("currentFormTempleteId", currentFormNewflow);
        
		/*if(!isSave){
			// 删除原有附件
			this.attachmentManager.deleteByReference(templeteId);
		}

		// 保存附件
		String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, templeteId, templeteId, request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	templete.setHasAttachments(true);
        }*/
		
        //将当前模板推送到首页-我的模板
       /* TempleteConfigManager templeteConfigManager = (TempleteConfigManager)ApplicationContextHolder.getBean("templeteConfigManager");
        List<Long> authMemberIdsList = new ArrayList<Long>();
        Set<V3xOrgMember> memberSet = this.orgManager.getMembersByTypeAndIds(authInfo);
        if(memberSet != null){
	        for(V3xOrgMember member : memberSet){
	            authMemberIdsList.add(member.getId());
	        }
	        templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templeteId, 1);
        }
        */
        
		/*
		if (isSave) { //新建
			templeteManager.save(templete);
		}
		else { // 修改
			this.templeteManager.update(templete);
		}*/
		
		//return super.redirectModelAndView("/genericController.do?ViewPage=collaboration/templete/systemIndex&categoryType=" + categoryType + "&categoryId=" + templete.getCategoryId() + "&from=" + from);
		PrintWriter pw = response.getWriter();
		pw.println("<script language=\"javascript\">");
		pw.println("if(window.dialogArguments){");
		pw.println("  window.returnValue = \"true\";");
		pw.println("}");
		pw.println("window.close();");
		pw.println("</script>");
		return null;
	}
	
	private static StringBuffer category2HTML(List<TempleteCategory> categories, 
			StringBuffer categoryHTML, Long currentNode, int level){
		for (TempleteCategory category : categories) {
			Long parentId = category.getParentId();
			if(parentId == currentNode || (parentId != null && parentId.equals(currentNode))){
				
				categoryHTML.append("<option value='" + category.getId() + "'>");
				
				for (int i = 0; i < level; i++) {
					categoryHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				
				categoryHTML.append(Strings.toHTML(category.getName()) + "</option>\n");
				
				category2HTML(categories, categoryHTML, category.getId(), level + 1);
			}
		}
		
		return categoryHTML;
	}
	
	/**
	 * 生成流程内容
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String doColWorkflow(HttpServletRequest request,FormBody formBody) throws Exception{
		FlowData flowData = FlowData.flowdataFromRequest();
		
		//如果用户没有设置发起人的表单节点操作，在这里设缺省的操作
		String xml = flowData.getXml();
		Document doc = null;
		HttpSession session = request.getSession();
		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
		HashMap usedOperationMap = this.getSessionObj(session, "usedOperation");
		usedOperationMap.clear();
		xml= Utils.specailCharReplacement(xml,Utils.regExp_angleBrackets,Utils.xmlSpecialChar_angleBrackets);
		xml= Utils.specailCharReplacement(xml,Utils.regExp_Tab,Utils.xmlSpecialChar_Tab);
		xml= Utils.specailCharReplacement(xml,Utils.regExp_Enter,Utils.xmlSpecialChar_Enter);
		xml= Utils.specailCharReplacement(xml,Utils.regExp_Newline,Utils.xmlSpecialChar_Newline);
		doc = DocumentHelper.parseText(xml);
		Element ele = (Element) doc.selectSingleNode("//processes/process");
		List<Element> nodes = ele.selectNodes("node");
		for(Element node:nodes){
			//6代表人工节点
			Map<String,String> nodeAttributeMap= Utils.getNodeAttributes(node);
			if("6".equals(nodeAttributeMap.get("type")) || "start".equalsIgnoreCase(nodeAttributeMap.get("id"))) {
				List<Element> subNodes = node.selectNodes("seeyonPolicy");
				Element policyNode = subNodes.get(0);
				Map<String,String> nodeAttributeMap1= Utils.getNodeAttributes(policyNode);
				if(policyNode!=null){
					String operation = nodeAttributeMap1.get("operationName");
					if("start".equals(nodeAttributeMap.get("id"))){     				            		
						boolean isAddOperation = FormHelper.isAddOperation(nodeAttributeMap1.get("formApp"), nodeAttributeMap1.get("form"), operation,sessionObject);
						if("undefined".equals(nodeAttributeMap1.get("formApp"))||!isAddOperation){
							policyNode.attribute("formApp").setValue(request.getParameter("defaultFormApp"));
							policyNode.attribute("form").setValue(request.getParameter("defaultForm"));
							policyNode.attribute("operationName").setValue(request.getParameter("defaultFirstNodeOperationId"));
							nodeAttributeMap1.put("formApp", request.getParameter("defaultFormApp"));
							nodeAttributeMap1.put("form", request.getParameter("defaultForm"));
							nodeAttributeMap1.put("operationName", request.getParameter("defaultFirstNodeOperationId"));
						}
						formBody.setFormApp(nodeAttributeMap1.get("formApp"));
						formBody.setForm(nodeAttributeMap1.get("form"));
						formBody.setOperationName(nodeAttributeMap1.get("operationName"));
						operation = nodeAttributeMap1.get("operationName");
					}
					if(operation!=null&&!"".equals(operation)&&!"undefined".equals(operation))
						usedOperationMap.put(operation, operation);
				}
			}
		}
		flowData.setXml(doc.asXML());
		String processId = null;
		if (!flowData.isEmpty()) {
            processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, null, true);
        }
		FlowData flowData1 = ColHelper.getProcessPeople(processId);

		return flowData1.getXml();
	}
	
	/**
	 * 生成正文内容
	 * @param request
	 * @param createDate
	 * @return
	 * @throws Exception
	 */
	private String doColBody(HttpServletRequest request,FormBody formBody) throws Exception{
		//ColBody colBody = new ColBody();
		FormContent formContent = new FormContent();
		bind(request, formContent);
		//Datetimes.parseDatetime方法修改当为空时抛出异常后，报错修改
		if(request.getParameter("bodyCreateDate") != null || "null".equals(request.getParameter("bodyCreateDate"))){
			Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
			if (bodyCreateDate != null) {
				formContent.setCreateDate(bodyCreateDate);
			}
		}
        List<FormBody> list = new ArrayList<FormBody>();
        list.add(formBody);
        formContent.setForms(list);
		return XMLCoder.encoder(formContent);
	}
	
	/**
	 * 生成协同属性信息
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String doColSummary(HttpServletRequest request) throws Exception{
		ColSummary colSummary = new ColSummary();
		bind(request, colSummary);
		colSummary.setId(null);

		String note = request.getParameter("note");// 发起人附言
		// 附言内容为空，就不记录了
//		if (StringUtils.isNotBlank(note)) {
			ColOpinion senderOpinion = new ColOpinion();
			senderOpinion.setContent(note);
			senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion);
			senderOpinion.affairIsTrack = request.getParameterValues("isTrack") != null;

			colSummary.getOpinions().add(senderOpinion);
//		}

		return XMLCoder.encoder(colSummary);
	}
	
	public ModelAndView deleteTemplate(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession(false);
		String[] ids = request.getParameterValues("ids");
		//Long[] templateIds = new Long[ids.length];
		HashMap addMap = this.getSessionObj(session, "add");
		HashMap updateMap = this.getSessionObj(session, "update");
		HashMap flowMap = this.getSessionObj(session, "flow");
		HashMap delMap = this.getSessionObj(session, "del");
        
		//NF 如果已被设置为子流程，则必须先解除引用才可删除
		HashMap<Long, List<String>> currentTempleteMainflow = this.getTemplateMainflowsMap(session);
		for(int i=0;i<ids.length;i++){
			Long templateId = Long.parseLong(ids[i].substring(0,ids[i].indexOf(":")));
            List<String> mainNames = currentTempleteMainflow.get(templateId);
            if(mainNames != null && !mainNames.isEmpty()){
                //当前模板已被设置为以下模板的子流程，必须先解除关联才能删除。
                StringBuffer bf = new StringBuffer();
                for (String name : mainNames) {
                	if(name==null)continue;
                    if(bf.length() > 0){
                        bf.append("、");
                    }
                    bf.append("《").append(name).append("》");
                }
                if(bf.length()>0){
	                try {
	                    PrintWriter pw = response.getWriter();
	                    pw.println("<script>");
	                    pw.println("alert(\""+ Functions.escapeJavascript(Constant.getString("newflow.tip.cannotDelete.wasChild", bf.toString())) + "\");");
	                    pw.println("</script>");
	                    pw.flush();
	                }
	                catch (IOException e) {
	                    log.error(e);
	                }
	                return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
                }
            }else{
            	 try {
     				mainNames= newflowManager.getNewflowRunningList(templateId);
     				if(null!=mainNames && !mainNames.isEmpty()){
     					//当前模板已被设置为以下模板的子流程，必须先解除关联才能删除。
     	                StringBuffer bf = new StringBuffer();
     	                for (String name : mainNames) {
     	                	if(name==null)continue;
     	                    if(bf.length() > 0){
     	                        bf.append("、");
     	                    }
     	                    bf.append("《").append(name).append("》");
     	                }
     	                if(bf.length()>0){
     		                try {
     		                    PrintWriter pw = response.getWriter();
     		                    pw.println("<script>");
     		                    pw.println("alert(\""+ Functions.escapeJavascript(Constant.getString("newflow.tip.cannotDelete.wasChild", bf.toString())) + "\");");
     		                    pw.println("</script>");
     		                    pw.flush();
     		                }
     		                catch (IOException e) {
     		                    log.error(e);
     		                }
     		                return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
     	                }
     				}
     			} catch (ColException e) {
     				logger.warn(e);
     			}
            }
            Object obj = addMap.remove(String.valueOf(templateId));
			if(obj==null){
			    updateMap.remove(String.valueOf(templateId));
			    delMap.put(templateId, flowMap.get(templateId));
			}
			flowMap.remove(templateId);
		}
		return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
	}
	
	//删除应用绑定
	public ModelAndView deleteTemplateAppBind(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession(false);
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String[] ids = request.getParameterValues("ids");
		for(int i=0;i<ids.length;i++){
			String mainId = ids[i];
			sessionobject.getFormAppAuthObjectMap().remove(mainId);
		}
		return super.redirectModelAndView("/bindForm.do?method=showTemplateBind&formType="+sessionobject.getFormType());
	}
	
	//删除应用授权
	public ModelAndView deleteTemplateAppAuth(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession(false);
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String id = request.getParameter("id");
		String subject = request.getParameter("subject");
		FormAppAuthObject abo = sessionobject.getFormAppAuthObjectMap().get(id);
		if(abo!=null){
			abo.getAppOperAuthObjectMap().remove(subject);
			abo.getAppOperAuthObjectMap().clear();
		}
		PrintWriter pw = response.getWriter();
		pw.println("true");
		return null;
	}
	
	private void setChange(HttpSession session){
		SessionObject obj = this.getSessionObj(session);
		if(obj==null){
		  return ;
		}
		TemplateObject tempObj = obj.getTemplateobj();
		if(tempObj == null){
			return ;			
		}
		tempObj.setChanged(true) ;
	}
	
	public HashMap getSessionObj(HttpSession session,String scope){
		SessionObject obj = this.getSessionObj(session);
		if(obj==null){
			obj = new SessionObject();
			session.setAttribute("SessionObject", obj);
		}
		TemplateObject tempObj = obj.getTemplateobj();
		if(tempObj==null){
			tempObj = new TemplateObject();
			obj.setTemplateobj(tempObj);
		}
		HashMap templateMap = null;
		if("add".equals(scope)){
			templateMap = tempObj.getAddMap();
			if(templateMap==null){
				templateMap = new HashMap();
				tempObj.setAddMap(templateMap);
			}
		}else if("update".equals(scope)){
			templateMap = tempObj.getUpdateMap();
			if(templateMap==null){
				templateMap = new HashMap();
				tempObj.setUpdateMap(templateMap);
			}
		}else if("del".equals(scope)){
			templateMap = tempObj.getDelMap();
			if(templateMap==null){
				templateMap = new HashMap();
				tempObj.setDelMap(templateMap);
			}
		}else if("flow".equals(scope)){
			templateMap = tempObj.getFlowMap();
			if(templateMap==null){
				templateMap = new LinkedHashMap();
				tempObj.setFlowMap(templateMap);
			}
		}else if("usedOperation".equals(scope)){
			templateMap = tempObj.getUsedOperationMap();
			if(templateMap==null){
				templateMap = new HashMap();
				tempObj.setUsedOperationMap(templateMap);
			}
		}
		return templateMap;
	}
	
	public void setFlowTemplate(HashMap flowMap,Templete template){
		FlowTempletImp flowTemp = new FlowTempletImp();
		flowTemp.setId(template.getId());
		flowTemp.setName(template.getSubject());
		//flowTemp.setCategory(template.getCategoryId());
		flowMap.put(template.getId(), flowTemp);
	}
	
	public String getAuthName(Set<TempleteAuth> auths){
		if(auths==null || auths.size()==0)
			return "";
		List<Object[]> entities = new ArrayList<Object[]>();
		for(TempleteAuth auth: auths){
			entities.add(new Object[]{auth.getAuthType(),auth.getAuthId()});
		}
		return Functions.showOrgEntities(entities,"、");
	}
	
	private SessionObject getSessionObj(HttpSession session){
		return (SessionObject)session.getAttribute("SessionObject");
	}
	
	public static String  printClassLoader(Class clazz){
	    if (clazz == null)
	      return "";
	    ClassLoader floader = clazz.getClassLoader();
	    List flist=new ArrayList(10);
	    while (floader != null) {
	      flist.add(floader.getClass().getName());
	      floader=floader.getParent();
	    }
	    StringBuffer fb=new StringBuffer();
	    fb.append("class = "+clazz.getName()+"\r\n");
	    for (int i = flist.size()-1; i >= 0; i--) {
	      fb.append("   "+flist.get(i)+"\r\n");
	    }
	    return fb.toString();
	  }
	
	private ColSuperviseDetail saveColSupervise(HttpServletRequest request,Templete template) {
		ColSuperviseDetail detail = null;
		String supervisorId = request.getParameter("supervisorId");
        String supervisorNames = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        String superviseId = request.getParameter("superviseId");
        Long awakeDates = Strings.isBlank(awakeDate)?null:Long.parseLong(awakeDate);
        String role = request.getParameter("superviseRole");
        
        //新建督办时督办人和督办时间都不为null才保存,修改时不做限制
        if((supervisorId != null && !"".equals(supervisorId) && awakeDate != null && !"".equals(awakeDate) 
        		&& (superviseId==null||"".equals(superviseId)))||(superviseId!=null && !"".equals(superviseId))||(role!=null && !"".equals(role))) {
	    	User user = CurrentUser.get();
	        String superviseTitle = request.getParameter("superviseTitle");
	        String[] idsStr = null;
	        //重要程度
	        int importantLevel = 1;
	        detail = new ColSuperviseDetail();
	        if(superviseId != null && !"".equals(superviseId))
	        	detail.setId(Long.parseLong(superviseId));
	        //detail.setIdIfNew();
	        detail.setTitle(superviseTitle);
	        detail.setSenderId(user.getId());
	        detail.setStatus(Constant.superviseState.supervising.ordinal());
	        detail.setSupervisors(supervisorNames);
	        detail.setCount(0);
	        detail.setTemplateDateTerminal(awakeDates);
	        detail.setEntityType(Constant.superviseType.template.ordinal());
	        detail.setEntityId(template.getId());
	        if(!"".equals(supervisorId)) {
	        	idsStr = supervisorId.split(",");
		        Set<ColSupervisor> supervisors = new HashSet<ColSupervisor>();
				for(String id:idsStr) {
					ColSupervisor colSupervisor = new ColSupervisor();
					colSupervisor.setIdIfNew();
					colSupervisor.setSuperviseId(detail.getId());
					colSupervisor.setSupervisorId(Long.parseLong(id));
					supervisors.add(colSupervisor);
				}
				detail.setColSupervisors(supervisors);
	        }
        }
        return detail;
    }
	
	private List<SuperviseTemplateRole> saveSuperviseTemplateRole(HttpServletRequest request,Templete template) {
		String role = request.getParameter("superviseRole");
		List<SuperviseTemplateRole> roles = null;
		if(role != null && !"".equals(role)) {
			roles = new ArrayList<SuperviseTemplateRole>();
			String[] strs = role.split(",");
			for(String str : strs){
				if(!Strings.isBlank(str)){
					SuperviseTemplateRole templateRole = new SuperviseTemplateRole();
					templateRole.setIdIfNew();
					templateRole.setSuperviseTemplateId(template.getId());
					templateRole.setRole(str);
					roles.add(templateRole);
				}
			}
		}
		return roles;
	}
    
    private List<NewflowSetting> saveNewflowSettings(HttpServletRequest request, Long templeteId){
        List<NewflowSetting> newflowSettingList = null;
        String[] settingStrs = request.getParameterValues("NewflowSettings");
        if(settingStrs != null && settingStrs.length > 0){
            newflowSettingList = new ArrayList<NewflowSetting>();
            for (String settingStr : settingStrs) {
                String[] setArr = settingStr.split("@");
                NewflowSetting setting = new NewflowSetting();
                setting.setIdIfNew();
                setting.setTempleteId(templeteId);
                setting.setNodeId(setArr[0]);
                setting.setNewflowTempleteId(Long.parseLong(setArr[1]));
                setting.setNewflowSender(setArr[2]);
//                setting.setTriggerCondition(setArr[3]);
//                setting.setConditionTitle(setArr[4]);
                if(null!=setArr[3] && !"".equals(setArr[3].trim())){
               	 	setting.setTriggerCondition(setArr[3].replaceAll("'", "\""));
                }else{
                	setting.setTriggerCondition(setArr[3]);
                }
                if(null!=setArr[4] && !"".equals(setArr[4].trim())){
                	setting.setConditionTitle(setArr[4].replaceAll("'", "\""));
                }else{
                	setting.setConditionTitle(setArr[4]);
                }
                setting.setConditionBase(setArr[5]);
                setting.setIsForce(Boolean.valueOf(setArr[6]));
                setting.setFlowRelateType(Integer.parseInt(setArr[7]));
                setting.setIsCanViewMainFlow(Boolean.valueOf(setArr[8]));
                setting.setIsCanViewByMainFlow(Boolean.valueOf(setArr[9]));
                setting.setCreateTime(new Date());
                newflowSettingList.add(setting);
            }
        }
        return newflowSettingList;
    }
	
	private void saveToMap(Map map,Templete templete,List<ColBranch> branchs,ColSuperviseDetail detail,List<SuperviseTemplateRole> roles,List<Attachment> attachments) {
		String templateId = templete.getId().toString();
		map.put(templateId, templete);
		if(branchs != null && branchs.size()>0)
			map.put(templateId+"branch", branchs);
		if(detail != null)
			map.put(templateId+"supervise", detail);
		if(roles != null)
			map.put(templateId+"role", roles);
		map.put(templateId+"attachment", attachments);
	}
    
    private void saveToNewflowMap(Map newflowMap, Long templateId, List<NewflowSetting> newflowList){
        newflowMap.put(templateId, newflowList);
    }
    
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
    
    private HashMap<Long, List<String>> getTemplateMainflowsMap(HttpSession session){
        HashMap<Long, List<String>> result = new HashMap<Long, List<String>>();
        HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
        if(currentFormNewflow!=null){
            Map<Long, String> allTemplateNamesMap = BindHelper.getTemplateNamesMap(session);
            if(allTemplateNamesMap != null){
	            Set<Long> keys = currentFormNewflow.keySet();
	            Iterator it = keys.iterator();
	            while(it.hasNext()){
	                Long templeteId = (Long)it.next();
	                String mainflowName = allTemplateNamesMap.get(templeteId);
	                List<NewflowSetting> newflowSettingList = (List<NewflowSetting>)currentFormNewflow.get(templeteId);
	                if(newflowSettingList != null && !newflowSettingList.isEmpty()){
	                    for (NewflowSetting setting : newflowSettingList) {
	                        Long currentChildId = setting.getNewflowTempleteId();
	                        if(result.get(currentChildId) == null){
	                            result.put(currentChildId, new ArrayList<String>());
	                        }
	                        result.get(currentChildId).add(mainflowName);
	                    }
	                }
                }
            }             
        }
        return result;
    }
    
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView showFlowList (HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("form/formapp/showFlowList") ;
		String id = request.getParameter("formId") ;
		if(Strings.isBlank("id")){
			return null ;
		}
						
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(id));				
		
		List<FormFlowTemplete> list = new ArrayList<FormFlowTemplete>() ;
		if(afapp!=null){
			SessionObject sessionobject = getIOperBase().loadFromnoInfoPath(afapp,id);
			if(sessionobject.getTemplateobj() != null && sessionobject.getTemplateobj().getFlowMap() != null ){
				Map map = sessionobject.getTemplateobj().getFlowMap() ;
				Collection<FlowTempletImp> flow = map.values() ;
				
				for(FlowTempletImp templeteimp : flow){	
					Templete templete = this.templeteManager.get(templeteimp.getId());
					FormFlowTemplete formFlowTemplete = new FormFlowTemplete();
					formFlowTemplete.setAuthName(this.getAuthName(templete.getTempleteAuths())) ;
					String superVisName = getShowColSuperName(templete);
					//督办人员保存时带有前“、”号删除，为解决历史数据中的问题，新的数据需要从保存部分解决。
					if(Strings.isNotBlank(superVisName) && superVisName.startsWith("、")) superVisName = superVisName.substring(1);
					formFlowTemplete.setSuperVisName(superVisName) ;	
					formFlowTemplete.setId(templete.getId()) ;
					formFlowTemplete.setTempleteName(templeteimp.getName()) ;
					list.add(formFlowTemplete) ;
				}				
			}

		}
		
		mav.addObject("formFlowTempleteList",getIOperBase().pagenate(list)) ;	
		
		return mav ;				
	}
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
	public ModelAndView showWorkFlow (HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("form/formapp/showWorkFlow") ;
		String id = request.getParameter("templeteId") ;
		if(Strings.isBlank("id")){
			return null ;
		}
		
		Templete templete = this.templeteManager.get(Long.valueOf(id));
		if(templete == null){
			PrintWriter out = response.getWriter() ;			
			out.println("<script>") ;
			out.println("alert('该模板已经被删除') ;") ;
			out.println("window.close();") ;
			out.println("</script>") ;
			return null ;
		}
        BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
        String caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称
		
		List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);
		
		Metadata nodePermissionPolicy = metadataManager.getMetadata(MetadataNameEnum.col_flow_perm_policy);
		
        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
        caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
        
        mav.addObject("workflowInfo", workflowInfo);
        mav.addObject("workflow", caseProcessXML);
        mav.addObject("nodePermissionPolicy", nodePermissionPolicy);
        mav.addObject("workflowRule", templete.getWorkflowRule());	
        mav.addObject("branchs", this.templeteManager.getBranchsByTemplateId(Long.valueOf(id),ApplicationCategoryEnum.collaboration.ordinal()));
        mav.addObject("templete", templete) ;
        mav.addObject("templeteId", id);
        return mav ;				
	}
    
	private String getColDelSuperVise(Long id ,int entityType){
		ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),id);
		if(detail == null){
			return "" ;
		}
		if(detail.getSupervisors() == null){
			return "" ;
		}
		return detail.getSupervisors() ;
	}
	
	private String getColRoseSuperVise(Long id){
		List<SuperviseTemplateRole> roles = colSuperviseManager.findSuperviseRoleByTemplateId(id);
		if(roles == null){
			return "" ;
		}
		StringBuffer str = new StringBuffer() ;
		for(SuperviseTemplateRole superviseTemplateRole : roles){
			if("sender".equals(superviseTemplateRole.getRole())) {				
				
					str.append(Constant.getCommonString("common.separator.label")) ;
						
				str.append(com.seeyon.v3x.system.Constants.getString4CurrentUser("sys.role.rolename.Sender"));
    		}else if("senderDepManager".equals(superviseTemplateRole.getRole())){
    			
					str.append(Constant.getCommonString("common.separator.label")) ;
				
    			str.append(com.seeyon.v3x.system.Constants.getString4CurrentUser("sys.role.rolename.SenderDepManager")) ;
    		}
    			
		}		
		return str.toString() ;
	}
	
	private String getShowColSuperName(Templete templete){
		if(templete == null){
			return "" ;
		}		
		return getColDelSuperVise(templete.getId(),Constant.superviseType.template.ordinal()) + getColRoseSuperVise(templete.getId()) ;
	}
	
}