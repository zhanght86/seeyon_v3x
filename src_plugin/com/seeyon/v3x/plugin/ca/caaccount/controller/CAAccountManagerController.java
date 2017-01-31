package com.seeyon.v3x.plugin.ca.caaccount.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.annotations.Unsupported;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.ca.caaccount.dao.CAAccountDao;
import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.plugin.ca.caaccount.manager.CAAccountManager;
import com.seeyon.v3x.plugin.ca.caaccount.manager.CAAccountManagerImpl;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebCAAccountVo;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebImportCAAccountResultVo;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;

public class CAAccountManagerController extends BaseController {
	
	private OrgManager orgManager;
	private static Log log = LogFactory.getLog(CAAccountManagerController.class);
	private CAAccountManager caAccountManager;
	private FileToExcelManager fileToExcelManager;
	private FileManager fileManager;
	private CAAccountDao caAccountDao;
	
	public CAAccountDao getCaAccountDao() {
		return caAccountDao;
	}

	public void setCaAccountDao(CAAccountDao caAccountDao) {
		this.caAccountDao = caAccountDao;
	}
	
	public OrgManager getOrgManager() {
		return orgManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
    
    public void setCaAccountManager(CAAccountManager caAccountManager) {
        this.caAccountManager = caAccountManager;
    }
    
    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }
    
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public String getId() {
           return "caAccountManagerController";
    }

    protected String getName(Map<String, String> preference) {
            return "caAccountManagerController";
    }
    
    //进入ca帐号管理最外层MainFrame 

	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView goToCAAccountManage(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        return new ModelAndView("caaccount/caAccountManageMainFrame");
    }
    
    /**
     * 进入CA登陆验证设置
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView goToCAAccountOptions(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("caaccount/caAccountOptions");
    	boolean isMustUseCALogin = caAccountManager.getSystemMustUseCALogin();
    	String noCheckIp = caAccountManager.getSystemNoCheckIP();
    	mav.addObject("isMustUseCALogin", isMustUseCALogin);
    	mav.addObject("noCheckIp", noCheckIp);
    	return mav;
    }
    
    /**
     * 保存CA登陆验证配置
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	public ModelAndView saveCAConfig(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean isMustUseCALogin = "on".equals(request
				.getParameter("isMustUseCALogin"));
		String noCheckIp = request.getParameter("noCheckIp");
		caAccountManager.saveSystemMustUseCALogin(isMustUseCALogin, noCheckIp);
		return super.redirectModelAndView("/caAccountManagerController.do?method=goToCAAccountOptions");
	}
    
    //列表展示ca帐号
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView listCAAccount(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("caaccount/listCAAccount");
        String condition = request.getParameter("condition");
        String value = request.getParameter("textfield");
        List<WebCAAccountVo> webCAAccountVolist = caAccountManager.searchByCondition(condition, value);
        mav.addObject("webCAAccountVolist", webCAAccountVolist);
        mav.addObject("condition", condition);
        mav.addObject("textfield", value);        
        return mav;
    }
    
    //进入toolbar页面
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView showCAAccountMenu(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        return new ModelAndView("caaccount/showCAAccountMenu");
    }
    
    //进入添加/修改ca帐号的页面
    public ModelAndView showAddOrEditCAAccount(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        PrintWriter out = response.getWriter();
        ModelAndView mav = new ModelAndView("caaccount/showAddOrEditCAAccount");
        String operationType = request.getParameter("operationType");
        String readOnly = "";
        if(operationType != null && operationType.equals("add")){
            
        } else if(operationType != null){
        	if(operationType.equals("view")){
        		readOnly = "readonly";
        	}
            String memberId = request.getParameter("memberId");
            long memberId_long = 0l;
            try{
                memberId_long = Long.parseLong(memberId);
            } catch(NumberFormatException nfe){
                String errorReason = "memberId不能转化为long型，memberId:" + memberId;
                log.error(errorReason);
                out.println("<script>");
                out.println("alert('" + errorReason + "')");
                out.println("</script>");
                out.flush();
                return null;
            }
            if(memberId != null && memberId.trim().length() > 0){
                CAAccount caAccount = caAccountManager.findByMemberId(memberId_long);
                if(caAccount != null){
                    mav.addObject("keyNum", caAccount.getKeyNum());
                    mav.addObject("aclIds", memberId);
                    mav.addObject("caAccount", caAccount);
                }
                V3xOrgMember v3xOrgMember = orgManager.getMemberById(memberId_long);
                if(v3xOrgMember == null) {
                    String errorReason = "通过登录名" + memberId + "找不到v3xOrgMember";
                    log.error(errorReason);
                    out.println("<script>");
                    out.println("alert('" + errorReason + "')");
                    out.println("</script>");
                    out.flush();
                    return null;
                } 
            }
        }
        mav.addObject("operationType", operationType);
        mav.addObject("readOnly", readOnly);
        return mav;
    }
	
    //添加或者修改ca帐号
	public ModelAndView addOrEditCAAccount(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        PrintWriter out = response.getWriter();
        String operationType = request.getParameter("operationType");
        String memberId = request.getParameter("grantedMemberId");
        V3xOrgMember v3xOrgMember = orgManager.getMemberById(Long.valueOf(memberId));
        String keyNum = request.getParameter("keyNum").trim();
        CAAccount caaccount = new CAAccount();
        bind(request, caaccount);
        //新增CA账号
        if(operationType.equals("add")){
            if(v3xOrgMember == null){
                out.println("<script>");
                out.println("parent.promptCannotFindMember();");
                out.println("</script>");
                return null;
            }
            if(caAccountManager.isMemberIdExist(v3xOrgMember.getId())){
                out.println("<script>");
                out.println("parent.promptAccountExist();");
                out.println("</script>");
            }else if(caAccountManager.isKeyNumExist(caaccount.getKeyNum())){
            	out.println("<script>");
            	out.println("parent.promptKeyNumExist();");
            	out.println("</script>");
            } else {
            	caaccount.setIdIfNew();
                caaccount.setMemberId(Long.valueOf(memberId));
            	caAccountDao.save(caaccount);
                out.println("<script>");
                out.println("parent.promptAddSuccess();");
                out.println("</script>");
                log.info(v3xOrgMember.getName()+"的CA账号添加成功");
                return super.refreshWorkspace();
            }  
        } else {
        	//修改CA账号
            caAccountManager.updateCAKeyByMemberId(v3xOrgMember.getId(), caaccount);
            out.println("<script>");
            out.println("parent.promptEditSuccess();");
            out.println("</script>");
            log.info(v3xOrgMember.getLoginName()+"的CA账号修改成功");
            return super.refreshWorkspace();
        }
        return null;
    }
	
	//删除ca帐号
	public ModelAndView destroyAccount(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
	    PrintWriter out = response.getWriter();
	    String[] memberIds = request.getParameterValues("id");
	    String opSuccess = ResourceBundleUtil.getString(CAAccountManagerImpl.RESOURCE_NAME, "organization.yes");
        String opFail = ResourceBundleUtil.getString(CAAccountManagerImpl.RESOURCE_NAME, "organization.yes");
        String opResult = null;
        long[] memberIds_long = new long[memberIds.length];
        for(int i = 0; i < memberIds_long.length; i++){
            try{
                memberIds_long[i] = Long.parseLong(memberIds[i]);
            } catch(NumberFormatException nfe){
                String errorReason = "memberId不能转化为long型，memberId:" + memberIds[i];
                log.error(errorReason);
                out.println("<script>");
                out.println("alert('" + errorReason + "')");
                out.println("</script>");
                out.flush();
                return listCAAccount(request, response);
            }
        }
        boolean result = this.caAccountManager.deleteBy(memberIds_long);
        out.println("<script>");
        if(result){
            opResult = opSuccess;
        } else {
            opResult = opFail;
        }
        out.println("alert('" + opResult + "');");
        out.println("</script>");   
        out.flush();
        return listCAAccount(request, response);
    }
	
	/**
	 * 批量停启用CA账号
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView changeStatus(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		String[] ids = request.getParameterValues("id");
		boolean status = request.getParameter("status").equals("1") ? true:false;
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				CAAccount caAccount = caAccountManager.findByMemberId(Long.valueOf(ids[i]));
				caAccount.setCaState(status);
				caAccountDao.update(caAccount);
				log.info(caAccount.getMemberId()+"CA账号状态改变成功");
			}
		}
		return listCAAccount(request, response);
	}
	
	//进入导入excel的页面
	public ModelAndView importExcel(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("caaccount/selectImportExcel");
        return modelAndView;
    }
	
	//导入ca帐号
	public ModelAndView doImport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        
        User u = CurrentUser.get();
        if(u==null){
            //DataUtil.outNullUserAlertScript(out);
            return null;
        }
        if(DataUtil.doingImpExp(u.getId())){
            //DataUtil.outDoingImpExpAlertScript(out);
            return null;
        }
               
        DataUtil.putImpExpAction(u.getId(), "import");
        try{
            File file = getUploadFile(request);
            String path = file.getAbsolutePath()+".xls";
            File realfile = new File(path);
            DataUtil.CopyFile(file,realfile);
            
            List<List<String>> caAccountList = fileToExcelManager.readExcel(realfile);
            List<List<String>> caRealAccountList = new ArrayList<List<String>>();
            if(caAccountList != null && caAccountList.size() > 2){
                caRealAccountList = caAccountList.subList(2, caAccountList.size());
            }
            List<String> importLoginName=new ArrayList<String>();
            List<String> importKeyNum=new ArrayList<String>();
            
            for(int i = 0; i < caRealAccountList.size(); i++){
                List<String> record = caRealAccountList.get(i);
                StringBuffer msg=new StringBuffer();
                //防护，以免越界
                if(record.size()<2){
                	continue;
                }
                String loginName = record.get(0);
                String keyNum = record.get(1);
                if(Strings.isBlank(loginName)&&Strings.isBlank(keyNum)){
                	continue;
                }
                if(Strings.isBlank(loginName)){
                	msg.append("第");
                	msg.append(i+3);
                	msg.append("行的协同登录名为空，停止导入");
                	printErrMsg(out,msg.toString());
                    DataUtil.removeImpExpAction(u.getId());
                	return null;
                }
                if(Strings.isBlank(keyNum)){
                	msg.append("第");
                	msg.append(i+3);
                	msg.append("行的CA Key唯一标识为空，停止导入");
                	printErrMsg(out,msg.toString());
                    DataUtil.removeImpExpAction(u.getId());
                	return null;
                }
                if(importLoginName.contains(loginName)){
                	msg.append("第");
                	msg.append(i+3);
                	msg.append("行的协同登录名有重复，停止导入");
                	printErrMsg(out,msg.toString());
                    DataUtil.removeImpExpAction(u.getId());
                	return null;
                }
                if(importKeyNum.contains(keyNum)){
                	msg.append("第");
                	msg.append(i+3);
                	msg.append("行的CA Key唯一标识有重复，停止导入");
                	printErrMsg(out,msg.toString());
                    DataUtil.removeImpExpAction(u.getId());
                	return null;
                }
                importLoginName.add(loginName);
                importKeyNum.add(keyNum);
            }
            String repeat = request.getParameter("repeat");
            String impURL = request.getParameter("impURL");
            List<WebImportCAAccountResultVo> webImportCAAccountResultVoList = caAccountManager.importCAAccount(repeat, caRealAccountList);
            HttpSession session = request.getSession();
            session.setAttribute("webImportCAAccountResultVoList", webImportCAAccountResultVoList);
            session.setAttribute("impURL", impURL);
            session.setAttribute("repeat", repeat);
        }catch(Exception e){
            DataUtil.removeImpExpAction(u.getId());
            throw e;
        }
        DataUtil.removeImpExpAction(u.getId());  
        
        out.println("<script>");
        out.println("parent.hideProcDiv();");
        out.println("window.returnValue = false;");
        out.println("window.close();");
        out.println("</script>");
        out.flush();
        return null;
    }
	public void printErrMsg(PrintWriter out,String msg){
		 out.println("<script>");
		 out.println("alert('"+msg+"');");
         out.println("parent.hideProcDiv();");
         out.println("window.returnValue = false;");
         out.println("window.close();");
         out.println("</script>");
         out.flush();
	}
	
	public ModelAndView doImportResult(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("caaccount/importReport");
        HttpSession session = request.getSession();
        mav.addObject("impURL", session.getAttribute("impURL"));
        mav.addObject("repeat", session.getAttribute("repeat"));
        mav.addObject("webImportCAAccountResultVoList", session.getAttribute("webImportCAAccountResultVoList"));
        session.removeAttribute("impURL");
        session.removeAttribute("repeat");
        session.removeAttribute("webImportCAAccountResultVoList");
        return mav;
    }
	
	//下载模板
    public ModelAndView downloadTemplate(HttpServletRequest request,
            HttpServletResponse response) throws Exception {     
        String path = "";
        String filename = "";
        
        response.setContentType("application/x-msdownload; charset=UTF-8");
        
        path = SystemEnvironment.getA8ApplicationFolder() + "/apps_res/edoc/file/caaccount/CAAccount";
        filename = URLEncoder.encode("CAAccount.xls", "UTF-8"); 
        
        response.setHeader("Content-disposition", "attachment;filename=\"" +filename+ "\"");
        
        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
            out = response.getOutputStream();
            
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            if (e.getClass().getSimpleName().equals("ClientAbortException")) {
                log.debug("用户关闭下载窗口: " + e.getMessage());
            }
            else{
                log.error("", e);
            }
        }
        finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }        
        return null;        
    }
    
    //导出ca帐号到excel
    public ModelAndView expCAAccountToExcel(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User u = CurrentUser.get();
        if(u == null){
            //DataUtil.outNullUserAlertScript(out);
            return null;
        }
        if(DataUtil.doingImpExp(u.getId())){
            //DataUtil.outDoingImpExpAlertScript(out);
            return null;
        }
        String condition = request.getParameter("condition");
        String value = request.getParameter("textfield");
        List<WebCAAccountVo> webCAAccountVolist = caAccountManager.searchByCondition(condition, value);
        String fName = "CAAccountList_" + u.getLoginName();
        
        DataUtil.putImpExpAction(u.getId(), "export");
        DataRecord dataRecord=null;
        try{
            dataRecord = caAccountManager.exportCAAccount(webCAAccountVolist);
        }catch(Exception e){
            DataUtil.removeImpExpAction(u.getId());
            throw e;
        }
        DataUtil.removeImpExpAction(u.getId());
        try {
            log.info("expCAAccountToExcel");
            fileToExcelManager.save(request, response, fName, dataRecord);
        } catch (Exception e) {
            log.error("error",e);
        }     
        return null;
    }
    
    //选择浏览器中与登录名对应的CA证书
    @NeedlessCheckLogin
    public ModelAndView findKeyNumByLoginName(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	PrintWriter out = response.getWriter();
    	String loginName = request.getParameter("loginName");
    	String result = caAccountManager.findKeyNumByLoginName(loginName);
    	out.print(result);
		out.flush();
		out.close();
    	return null;
    }
    
    //精灵需要的参数
    @NeedlessCheckLogin
    public ModelAndView forA8geniusParameters(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String chgLn = "\r\n";
    	PrintWriter out = response.getWriter();
    	StringBuffer resultStr = new StringBuffer();
    	String loginName = request.getParameter("loginName");
    	String result = caAccountManager.findKeyNumByLoginName(loginName);
    	resultStr.append("caCondition="+result).append(chgLn);
    	String toSign = String.valueOf(System.currentTimeMillis());
    	//实现天威诚信2.0以上增加'LOGONDATA:'
    	resultStr.append("toSign="+"LOGONDATA:"+toSign).append(chgLn);
    	request.getSession().setAttribute("ToSign", toSign);
    	out.println(resultStr);
    	out.flush();
    	return null;
    }
    
    //从request中获取上传的文件
    private File getUploadFile(HttpServletRequest request) throws Exception {
        Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();     
        File fil = null;
        try {
            V3XFile v3x = null;
            v3xFiles = fileManager.uploadFiles(request, "xls", null);           
            String key="";
            if(v3xFiles != null) {
                Iterator<String> keys = v3xFiles.keySet().iterator();
                while(keys.hasNext()) {
                    key = keys.next();
                    v3x = (V3XFile)v3xFiles.get(key);                   
                }
            }
            fil = fileManager.getFile(v3x.getId(), v3x.getCreateDate());
        } catch (Exception e) {
            log.error("", e);
        }
        return fil;
    }
}
