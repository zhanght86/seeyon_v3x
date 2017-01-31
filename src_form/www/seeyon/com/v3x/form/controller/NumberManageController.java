package www.seeyon.com.v3x.form.controller;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.systemvalue.UserFlowId;
import www.seeyon.com.v3x.form.base.systemvalue.WebUserFlowId;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FormFlowid;
import www.seeyon.com.v3x.form.utils.FormFlowidHelper;

import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
@CheckRoleAccess(roleTypes=RoleType.FormAdmin)
public class NumberManageController extends BaseController  {
	private static RuntimeCharset fCurrentCharSet = SeeyonForm_Runtime
	.getInstance().getCharset();
	FileManager fileManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");

	private MetadataManager metadataManager;
	
	private OrgManager orgManager;

	public static RuntimeCharset getFCurrentCharSet() {
		return fCurrentCharSet;
	}
	public static void setFCurrentCharSet(RuntimeCharset currentCharSet) {
		fCurrentCharSet = currentCharSet;
	}
	public MetadataManager getMetadataManager() {		
		return metadataManager;
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
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    public ModelAndView formSerialNumberBorderFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
	 ModelAndView mav = new ModelAndView("form/flowid/serialNumberBorderFrame");    
	 return mav; 	           
    }
    
    public ModelAndView formSerialNumberFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
   	 ModelAndView mav = new ModelAndView("form/flowid/serialNumberFrame");    
   	 return mav; 	           
    }

    public String getDisPlayValue(FormFlowid formFlowid){
    	
		return FormFlowidHelper.getDisPlayValue(formFlowid, true) ;
    }
	
    public ModelAndView formSerialNumberList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
		ModelAndView mav = new ModelAndView("form/flowid/serialNumberList");
		String condition = request.getParameter("condition") ;
		String conditionValue = request.getParameter("textfield") ;	
		long accoutId = CurrentUser.get().getLoginAccount();
		List<FormFlowid> flowidlist = getIOperBase().getFlowidList(accoutId+"", condition, conditionValue) ;
	
		List<WebUserFlowId> webUserFlowIdList = new ArrayList<WebUserFlowId>() ;
		for(FormFlowid formFlowid : flowidlist) {
			WebUserFlowId webUserFlowId = new WebUserFlowId() ;		
			if(formFlowid.getAccountId() != null && formFlowid.getAccountId().longValue() == accoutId){
				webUserFlowId.setFlag(true) ;
			}else{
				webUserFlowId.setFlag(false) ;
			}
			String accountName = null ;
			V3xOrgAccount v3xOrgAccount = orgManager.getAccountById(formFlowid.getAccountId()) ;
			if(v3xOrgAccount != null) {
				accountName = v3xOrgAccount.getName() ;
			}
			webUserFlowId.setAccountName(accountName) ;
			webUserFlowId.setVariablename(formFlowid.getVariablename()) ;
			webUserFlowId.setId(formFlowid.getId()) ;
			webUserFlowId.setState(formFlowid.getState()) ;
			webUserFlowId.setViewValue(getDisPlayValue(formFlowid)) ;	
			webUserFlowIdList.add(webUserFlowId) ;
		}
		mav.addObject("flowidlist",  webUserFlowIdList);	
		return mav;
		   	   
      	 	           
    }
    private boolean isUsed(FormFlowid formFlowid){
    	if(formFlowid == null){
    		return false ;
    	}
    	if("Y".equals(formFlowid.getState())){
    		return true ;
    	}
    	return false ;
    }
    
    
    public ModelAndView addSerialNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
     	 ModelAndView mav = new ModelAndView("form/flowid/newAddSerialNumber");    
     	 return mav; 	           
    }
    
    public ModelAndView hasSerialNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String flowid = request.getParameter("id");
    	FormFlowid formFlowid = getIOperBase().queryFlowIdById(Long.valueOf(flowid));
    	PrintWriter out = response.getWriter();
    	if(formFlowid == null)
    		out.write(String.valueOf("false"));
    	else 
    		out.write(String.valueOf("true"));
    	return null; 	           
   }
    
    
    public ModelAndView isEditSerialNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String flowid = request.getParameter("id");
    	FormFlowid formFlowid = getIOperBase().queryFlowIdById(Long.valueOf(flowid));
    	PrintWriter out = response.getWriter();
    	if(!isUsed(formFlowid))
    		out.write(String.valueOf("true"));
    	else 
    		out.write(String.valueOf("false"));
    	return null; 	           
   } 
   
        
    //保存流水号
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	FormFlowid flowId = new FormFlowid();
    	
    	FormFlowidHelper.saveOrUpdateFlowId(flowId,request) ;
    	
    	getIOperBase().saveFlowId(flowId);
    	
    	UserFlowId userFlowId = new UserFlowId();
    	
    	BeanUtils.copyProperties(userFlowId, flowId);

    	SeeyonForm_Runtime.getInstance().getSystemValueManager().reg(flowId.getId().toString(), userFlowId);
    	
    
    	
    	return super.refreshWorkspace();
    	
   }
    //判断系统变量是否已经存在
    public ModelAndView checkVariableName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<FormFlowid> flowidlist = getIOperBase().getFlowidList(String.valueOf(CurrentUser.get().getLoginAccount()));
    	String variableName = java.net.URLDecoder.decode(request.getParameter("variableName"), "UTF-8");
       	PrintWriter out = response.getWriter();

       	boolean hasFlag = false ;
       	if(flowidlist != null) {
       		for(FormFlowid formFlowid : flowidlist) {
       			if(formFlowid.getVariablename().equals(variableName)){
       				hasFlag = true ;
       				break ;
       			}
       		}
       	}
       	
       	if(hasFlag){
       	 	out.print("true");
       	}else {
       		out.print("false");
       	}
       	
    	return null;
  }
    
  public ModelAndView showFlowidInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  Long id = Long.parseLong(request.getParameter("id"));	
	  FormFlowid formFlowid = getIOperBase().queryFlowIdById(id);
	  if(formFlowid == null) {
		  PrintWriter out = response.getWriter() ;
		  out.println("<Script>") ;
		  out.println("window.location.reload();") ;
		  out.println("</script>") ;
		  return null ;
	  }
	  
	  ModelAndView mav = new ModelAndView("form/flowid/browseSerialNumber");
	  mav.addObject("formFlowid", formFlowid);
	  mav.addObject("suffer", formFlowid.getSuffix()) ;
	  String str = "" ;
	  if(formFlowid.getTimeDate() != null ){
		  str = FormFlowidHelper.getFformatValue(formFlowid.getTimeDate()) ;
	  } 
	 if( formFlowid.getTextTimeBehond() != null ){
			 str = str + formFlowid.getTextTimeBehond() ;
	 }	 
	  mav.addObject("timeDate", str) ;
	  return mav;
  }
    
  public ModelAndView editFlowidInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  Long id = Long.parseLong(request.getParameter("id"));	
	  FormFlowid formFlowid = getIOperBase().queryFlowIdById(id);	  
	  ModelAndView mav = new ModelAndView("form/flowid/editSerialNumber");
	  request.getSession().setAttribute("flowId", formFlowid);	 
	  mav.addObject("formFlowid", formFlowid);
	  mav.addObject("suffer", formFlowid.getSuffix()) ;
	  mav.addObject("timeDate", formFlowid.getTimeDate()) ;
	  mav.addObject("textTimeBehond", formFlowid.getTextTimeBehond()) ;
	  return mav;
  }
  
  //更新流水号
  public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
  	
	
  	FormFlowid flowId = getIOperBase().queryFlowIdById(new Long(request.getParameter("id")));
  	
  	if(flowId ==null){
  	  	super.rendJavaScript(response, "parent.window.close();");
  	  	
  	  	return null;
  	}
  	FormFlowidHelper.saveOrUpdateFlowId(flowId,request) ;
 
  	getIOperBase().updateFlowId(flowId);
  	
  	UserFlowId userFlowId = new UserFlowId();
  	BeanUtils.copyProperties(userFlowId, flowId);
  	
  	SeeyonForm_Runtime.getInstance().getSystemValueManager().unReg(flowId.getId().toString());
  	SeeyonForm_Runtime.getInstance().getSystemValueManager().reg(flowId.getId().toString(), userFlowId);
  	
  	super.rendJavaScript(response, "parent.window.close();");
  	
  	return null;
  	
 }
  
  //删除流水号
  public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
	String ids = request.getParameter("ids");
	String []id = ids.split(",");
    for (int i = 0; i < id.length; i++) {
    	if(Strings.isNotBlank(id[i])){
	    	FormFlowid flowId = getIOperBase().queryFlowIdById(new Long(id[i]));
	    	SeeyonForm_Runtime.getInstance().getSystemValueManager().unReg(flowId.getId().toString());
	    	getIOperBase().deleteFlowId(id[i]);
    	}
	}
  	super.rendJavaScript(response, "parent.location.reload();");
  	return null;
 }
public void setOrgManager(OrgManager orgManager) {
	this.orgManager = orgManager;
}
  
}
