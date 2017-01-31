package com.seeyon.v3x.organization.inexportutil.manager;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.dao.OrganizationCache;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.inexportutil.DataManager;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.space.manager.SpaceManager;

public class IOManagerImpl implements IOManager {
	private static final Log log = LogFactory
	                         .getLog(IOManagerImpl.class);
	
	OrganizationServices  organizationServices=null;
	
	FileToExcelManager fileToExcelManager;
	
	private FileManager fileManager;
	
	private DataManager dataManagerImpl;
	
	private MetadataManager metadataManager;
	
	private OrganizationCache organization;
	
	private SpaceManager spaceManager;
	
	User opUser=null;
	
	V3xOrgAccount vaccount=null;
	
	public SpaceManager getSpaceManager() {
		return spaceManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}	
	
	public OrganizationCache getOrganization() {
		return organization;
	}
	public void setOrganization(OrganizationCache organization) {
		this.organization = organization;
	}
	
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	
	public DataManager getDataManagerImpl() {
		return dataManagerImpl;
	}
	public void setDataManagerImpl(DataManager dataManagerImpl) {
		this.dataManagerImpl = dataManagerImpl;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public OrganizationServices getOrganizationServices() {
		return organizationServices;
	}
	public void setOrganizationServices(OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	
	public ModelAndView exportReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		DataRow[] datarow = (DataRow[])session.getAttribute("datarowlist");
		if(datarow==null){
			List resultlst=(List)session.getAttribute("resultlst");
			datarow=DataUtil.createDataRowsFromResultObjects(resultlst);
		}
		String importType = (String)session.getAttribute("importType");
		String language = (String)session.getAttribute("language");
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
//		根据选择导入文件语言获取对应locale对象
		if(language.equals("zh_CN")){
			locale = Locale.CHINA;
		}else if(language.equals("en")){
			locale = Locale.ENGLISH;
		}else if(language.equals("zh")){
			locale = Locale.TAIWAN;
		}
		String import_level_report = ResourceBundleUtil.getString(resource, locale, "import.level.report");
		String import_post_report = ResourceBundleUtil.getString(resource, locale, "import.post.report");
		String import_team_report = ResourceBundleUtil.getString(resource, locale, "import.team.report");
		String import_member_report = ResourceBundleUtil.getString(resource, locale, "import.member.report");
		String import_dept_report = ResourceBundleUtil.getString(resource, locale, "import.dept.report");
		String import_account_report = ResourceBundleUtil.getString(resource, locale, "import.account.report");
		String import_report =
			 // "OrgImportReport";
			ResourceBundleUtil.getString(resource, locale, "import.report");
		String import_data = ResourceBundleUtil.getString(resource, locale, "import.data");
		String import_result = ResourceBundleUtil.getString(resource, locale, "import.result");
		String import_description = ResourceBundleUtil.getString(resource, locale, "import.description");
		String title = "";
		String sheetName = "";
//		session.removeAttribute("datarow");
		if(importType.equals("level")){
			title = import_level_report;
			sheetName = import_level_report;
		}else if(importType.equals("post")){
			title = import_post_report;
			sheetName = import_post_report;
		}else if(importType.equals("team")){
			title = import_team_report;
			sheetName = import_team_report;
		}else if(importType.equals("member")){
			title = import_member_report;
			sheetName = import_member_report;
		}else if(importType.equals("department")){
			title = import_dept_report;
			sheetName = import_dept_report;
		}else if(importType.equals("account")){
			title = import_account_report;
			sheetName = import_account_report;
		}
//		将导入结果添加到excel中
		DataRecord dataRecord = new DataRecord();
		try {
			dataRecord.addDataRow(datarow);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		String[] columnName = { import_data , import_result , import_description };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(title);
		dataRecord.setSheetName(sheetName);

		try {
			fileToExcelManager.save(request, response, import_report, dataRecord);
		} catch (Exception e) {
			log.error("error", e);
		}
		return null;
	}
	
	public ModelAndView importExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("organization/selectImportExcel");
		String importType = request.getParameter("importType");
		modelAndView.addObject("importType", importType);
		HttpSession session = request.getSession();
		session.setAttribute("importType", importType);
		List accountlst = this.getOrganizationServices()
		                  .getOrgManagerDirect().getAllAccounts();
		modelAndView.addObject("accountlst", accountlst);
		return modelAndView;
	}
	
	/**
	 * 页面跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView matchField(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".xls";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);

		HttpSession session = request.getSession();
		String selectvalue = request.getParameter("selectvalue");
		
		session.setAttribute("selectvalue", selectvalue);
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		
		session.setAttribute("radiovalue", radiovalue);
		/*
		if(radiovalue == null || "".equals(radiovalue) || "null".equals(radiovalue)){
			throw new Exception("请选择单、多表！");
		}*/
		String sheetnumber = request.getParameter("sheetnumber");
		
		session.setAttribute("sheetnumber", sheetnumber);
		if("multi".equals(radiovalue)){
			if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}	
		}
		
		String language = request.getParameter("languagevalue");
		session.setAttribute("language", language);
		
		List<List<String>> accountList = null;
		accountList = fileToExcelManager.readExcel(realfile);
		
		//tanglh test
		if(accountList!=null && accountList.size()>2){
			log.info("读取默认工作簿的数据，其中个行大小如下");
			for(int i=2;i<accountList.size();i++){
				log.info("accountList i="+i);
				List l=accountList.get(i);
				if(l==null){
					log.info("accountList'subList is null");
					continue;
				}
				log.info("accountList'subList size="+l.size());
			}
			
//			tanglh test

		}
		session.setAttribute("excellst", accountList);
		
		return null;

		//return super.redirectModelAndView("/organization.do?method=popMatchPage");

	}	
	public ModelAndView matchField1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".xls";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);

		HttpSession session = request.getSession();
		String selectvalue = request.getParameter("selectvalue");
		
		session.setAttribute("selectvalue", selectvalue);
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		
		session.setAttribute("radiovalue", radiovalue);
		/*
		if(radiovalue == null || "".equals(radiovalue) || "null".equals(radiovalue)){
			throw new Exception("请选择单、多表！");
		}*/
		String sheetnumber = request.getParameter("sheetnumber");
		
		session.setAttribute("sheetnumber", sheetnumber);
		if("multi".equals(radiovalue)){
			if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}	
		}
		
		String language = request.getParameter("languagevalue");
		session.setAttribute("language", language);
		
		//从后台取出数据字段列表
		List datastrulst = getDataManagerImpl().getDataStructure(selectvalue);
		DataUtil du = new DataUtil(selectvalue);
		datastrulst = du.getCHNString(datastrulst, request);
		session.setAttribute("datastrulst", datastrulst);
		List<List<String>> accountList = null;
		if("multi".equals(radiovalue)){
			accountList = fileToExcelManager.readExcel(realfile);
		}else if("single".equals(radiovalue)){
			//读取默认工作簿的数据
			accountList = fileToExcelManager.readExcel(realfile);
			
			//tanglh test
			if(accountList!=null && accountList.size()>2){
				log.info("读取默认工作簿的数据，其中个行大小如下");
				for(int i=2;i<accountList.size();i++){
					log.info("accountList i="+i);
					List l=accountList.get(i);
					if(l==null){
						log.info("accountList'subList is null");
						continue;
					}
					log.info("accountList'subList size="+l.size());
				}
			}
			
//			tanglh test

		}
		session.setAttribute("excellst", accountList);
		/*
		//读取表头数据，一般为第二行
		List proList = accountList.get(1);
		
		//得到匹配的list,及不匹配的list 组成的map
		allmap = DataUtil.getMatchList(proList, datastrulst);
		
		session.setAttribute("allmap", allmap);
*/
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.getA8Top().contentFrame.topFrame.matchfiled();");
		out.println("</script>");		
		return null;
		/*
		out.println("<script>");
		//out.println("parent.window.close();");
		//out.println("parent.top.contentFrame.topFrame.matchfiled();");
		//out.println("parent.window.close();");//tanglh
		out.println("document.location.href= '${organizationURL}?method=popMatchPage';");//tanglh
		out.println("</script>");		
		
		return super.redirectModelAndView("/organization.do?method=popMatchPage");
*/
	}	
	public ModelAndView popMatchPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		HttpSession session = request.getSession();
		ModelAndView modelAndView = new ModelAndView("organization/matchField");
		Map allmap = (Map)session.getAttribute("allmap");

		//用于页面已匹配下拉列表框
		modelAndView.addObject("matchlst", allmap.get("0"));	
		//用于页面的新增下拉列表框
		modelAndView.addObject("excellst", allmap.get("1"));
        //用于页面的删除下拉列表框
		modelAndView.addObject("strulst", allmap.get("2"));
		
		modelAndView.addObject("language", (String)session.getAttribute("language"));
		modelAndView.addObject("selectvalue", (String)session.getAttribute("selectvalue"));
		modelAndView.addObject("radiovalue",(String) session.getAttribute("radiovalue"));
		modelAndView.addObject("sheetnumber", (String)session.getAttribute("sheetnumber"));
		
		
		session.removeAttribute("allmap");
		return modelAndView;
	}
	
	public ModelAndView importReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("organization/importReport");
		HttpSession session = request.getSession();
		
		String selectvalue = request.getParameter("selectvalue");
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
//		跳过为 1  覆盖为 0
		String repeat = request.getParameter("repeat");
		
		String language = request.getParameter("language");
		
		String impURL = request.getParameter("impURL");
		if(!StringUtils.hasText(impURL)){
			impURL=(String)session.getAttribute("impURL");
		}
		log.info("impURL="+impURL);
		
		List resultlst=(List)session.getAttribute("resultlst");
		if(resultlst==null){
			resultlst=DataUtil.getResult4Imp(selectvalue);
		}
		if(resultlst==null){
			resultlst=new ArrayList();
		}
		
		modelAndView.addObject("selectvalue", selectvalue);
		session.setAttribute("selectvalue", selectvalue);
		session.setAttribute("importType", selectvalue);
		modelAndView.addObject("repeat", repeat);
		session.setAttribute("repeat", repeat);
		modelAndView.addObject("language", language);
		session.setAttribute("language", language);
		modelAndView.addObject("impURL", impURL);
		session.setAttribute("impURL", impURL);
		
		
		
		//query.setFirstResult(Pagination.getFirstResult());
		//query.setMaxResults(Pagination.getMaxResults());
		//Pagination.setRowCount(resultlst.size());
		List subl=DataUtil.pageForList(resultlst);
		
		modelAndView.addObject("resultlst", subl);
		
		return modelAndView;
	}
	/**
	 * 页面跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String doImport4Redirect(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//ModelAndView modelAndView = new ModelAndView("organization/importReport");		
		HttpSession session = request.getSession();

		String selectvalue = request.getParameter("selectvalue");
		if(selectvalue==null){
			selectvalue=(String)request.getAttribute("selectvalue");
		}
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".xls";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);
		
		session.setAttribute("selectvalue", selectvalue);
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		
		session.setAttribute("radiovalue", radiovalue);
		/*
		if(radiovalue == null || "".equals(radiovalue) || "null".equals(radiovalue)){
			throw new Exception("请选择单、多表！");
		}*/
		String sheetnumber = request.getParameter("sheetnumber");
		
		session.setAttribute("sheetnumber", sheetnumber);
		if("multi".equals(radiovalue)){
			if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}	
		}

		//session.setAttribute("excellst", accountList);
		
		//跳过为 1  覆盖为 0
		String repeat = request.getParameter("repeat");
		boolean ignoreWhenUpdate="0".equals(repeat)
		                     ?false:true;
		
		String language = request.getParameter("language");
		//modelAndView.addObject("language", language);
		//session.setAttribute("language", language);
		Locale locale = null;
//		根据选择导入文件语言获取对应locale对象
		if(language.equals("zh_CN")){
			locale = Locale.CHINA;
		}else if(language.equals("en")){
			locale = Locale.ENGLISH;
		}else if(language.equals("zh")){
			locale = Locale.TAIWAN;
		}
		
		String impURL = request.getParameter("impURL");//???
		session.setAttribute("impURL", impURL);

		List<List<String>> accountList = null;
		List reall=new ArrayList();
		accountList = fileToExcelManager.readExcel(realfile);
		
		//tanglh test
		if(accountList!=null && accountList.size()>2){
			log.info("读取默认工作簿的数据，其中个行大小如下");
			for(int i=2;i<accountList.size();i++){
				log.info("accountList i="+i);
				List l=accountList.get(i);
				if(l==null){
					log.info("accountList'subList is null");
					continue;
				}
				log.info("accountList'subList size="+l.size());
			}
			reall=accountList.subList(2, accountList.size());
		}	
//			tanglh test
			
		organizationServices.setLoadData(false);
		DataUtil du = new DataUtil(selectvalue);
		Map reportmap=du.getIip().importOrg(organizationServices, metadataManager, reall
				             , this.getVaccount(), ignoreWhenUpdate);
		
		List resultlst=this.createResultObjectList(reportmap);
		
		try{
//			organizationServices.reloadOrganizationModel();//重新加载一次组织模型
			organizationServices.reloadAccountData(this.getVaccount().getId()); // 重新加载单位的组织模型
		}catch(Exception e){
			
		}
		organizationServices.setLoadData(true);
		
		session.setAttribute("resultlst", resultlst);
		DataUtil.putResult4Imp(selectvalue, resultlst);
		
		return DataUtil.getImportReportURL(selectvalue, repeat, language, impURL);
	/*
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.top.contentFrame.topFrame.importReport();");
		out.println("</script>");
		
		return null;*/
	}
	public ModelAndView doImport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("organization/importReport");		
		HttpSession session = request.getSession();

		String selectvalue = request.getParameter("selectvalue");
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".xls";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);
		
		session.setAttribute("selectvalue", selectvalue);
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		
		session.setAttribute("radiovalue", radiovalue);
		/*
		if(radiovalue == null || "".equals(radiovalue) || "null".equals(radiovalue)){
			throw new Exception("请选择单、多表！");
		}*/
		String sheetnumber = request.getParameter("sheetnumber");
		
		session.setAttribute("sheetnumber", sheetnumber);
		if("multi".equals(radiovalue)){
			if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}	
		}

		//session.setAttribute("excellst", accountList);
		
		//跳过为 1  覆盖为 0
		String repeat = request.getParameter("repeat");
		boolean ignoreWhenUpdate="0".equals(repeat)
		                     ?false:true;
		
		String language = request.getParameter("language");
		modelAndView.addObject("language", language);
		session.setAttribute("language", language);
		Locale locale = null;
//		根据选择导入文件语言获取对应locale对象
		if(language.equals("zh_CN")){
			locale = Locale.CHINA;
		}else if(language.equals("en")){
			locale = Locale.ENGLISH;
		}else if(language.equals("zh")){
			locale = Locale.TAIWAN;
		}
		
		String impURL = request.getParameter("impURL");//???
		modelAndView.addObject("impURL", impURL);//???

		List<List<String>> accountList = null;
		accountList = fileToExcelManager.readExcel(realfile);
		
		//tanglh test
		if(accountList!=null && accountList.size()>2){
			log.info("读取默认工作簿的数据，其中个行大小如下");
			for(int i=2;i<accountList.size();i++){
				log.info("accountList i="+i);
				List l=accountList.get(i);
				if(l==null){
					log.info("accountList'subList is null");
					continue;
				}
				log.info("accountList'subList size="+l.size());
			}
		}	
//			tanglh test
		
		List reall=new ArrayList();
		if(accountList.size()>2)
		reall=accountList.subList(2, accountList.size());
		
		
		
		DataUtil du = new DataUtil(selectvalue);
		Map reportmap=du.getIip().importOrg(organizationServices, metadataManager, reall
				             , this.getVaccount(), ignoreWhenUpdate);
		
		List resultlst=this.createResultObjectList(reportmap);
		
		session.setAttribute("resultlst", resultlst);
		modelAndView.addObject("resultlst", resultlst);
		modelAndView.addObject("repeat", repeat);//repeat
	
		//organizationServices.reloadOrganizationModel();//重新加载一次组织模型
		organizationServices.reloadAccountData(this.getVaccount().getId()); // 重新加载单位的组织模型
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.closeOnbeforeunload('?method=);");
		out.println("</script>");
		
		return null;
	}
	public ModelAndView doImport1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();		
		HttpSession session = request.getSession();

		String selectvalue = request.getParameter("selectvalue");
		if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		if(radiovalue == null || "".equals(radiovalue) || "null".equals(radiovalue)){
			throw new Exception("请选择单、多表！");
		}
		String sheetnumber = request.getParameter("sheetnumber");
		if("multi".equals(radiovalue)){
			if(selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)){
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}	
		}
		//跳过为 1  覆盖为 0
		String repeat = request.getParameter("repeat");
		if(repeat == null || "".equals(repeat) || "null".equals(repeat)){
			throw new Exception("请选择上传策略！");
		}
		String language = request.getParameter("language");
		modelAndView.addObject("language", language);
		session.setAttribute("language", language);
		Locale locale = null;
//		根据选择导入文件语言获取对应locale对象
		if(language.equals("zh_CN")){
			locale = Locale.CHINA;
		}else if(language.equals("en")){
			locale = Locale.ENGLISH;
		}else if(language.equals("zh")){
			locale = Locale.TAIWAN;
		}
		
		String impURL = request.getParameter("impURL");
		modelAndView.addObject("impURL", impURL);
		String accountid = String.valueOf(CurrentUser.get().getAccountId());		
		
		List datalst = (List)session.getAttribute("datastrulst");
		DataUtil du = new DataUtil(selectvalue);
		List DataObjectlst = DataUtil.setMatchList(request, datalst);
		List accountList = (List)session.getAttribute("excellst");
		List volst = du.getMatchValue(this.getOrganizationServices().getOrgManagerDirect()
				,this.metadataManager,Long.valueOf(accountid),accountList, DataObjectlst);

		//注入 单位id
		if(!"account".equals(selectvalue)){
			DataUtil.setAccountId(accountid, volst);
		}
		
		//tanglh  
		//后面因为volst的对象ID会变掉，这里先缓存先
		List volold=new ArrayList();
		if(volst!=null && selectvalue.equals("member")){
			for(int i=0;i<volst.size();i++){
				V3xOrgMember vm=(V3xOrgMember)volst.get(i);
				if(vm==null)
					continue;
				V3xOrgMember vom=new V3xOrgMember();
				vom.setId(vm.getId());
				vom.setLoginName(vm.getLoginName());
				vom.setPassword(vm.getPassword());
				
				volold.add(vom);
			}
		}
		
		//tanglh
		
		Map mp = du.devideVo(this.getOrganizationServices().getOrgManagerDirect(), volst);
		List inserlst = new ArrayList();
		List updatelst =  new ArrayList();
		if("1".equals(repeat)){
			inserlst = du.getCreateSQL((List)mp.get("new"));
		}else{
			inserlst = du.getCreateSQL((List)mp.get("new"));
			updatelst = du.getUpdateSQL((List)mp.get("dup"));
			inserlst.addAll(updatelst);
		}
		List resultlst = new ArrayList();		
		
		//执行sql语句
		getDataManagerImpl().execSQLList(inserlst);
		
		PrintWriter out = response.getWriter();
		
		DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
		DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
		
		
		DataUtil.setResultToSession(resultlst,session);
		session.setAttribute("resultlst", resultlst);
		session.removeAttribute("excellst");
		session.removeAttribute("datastrulst");
		out.println("<script>");
		out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
		out.println("</script>");
		//tanglh
		//out.println("<script>");          ///
		//out.println("parent.top.contentFrame.topFrame.importReport();");
		//out.println("</script>");
		//return super.redirectModelAndView("/organization.do?method=popMatchPage");
		if(selectvalue.equals("level")){
			/*
			organization.loadOrganizationModel();
			//return redirectModelAndView("/organization.do?method=organizationFrame&from=Level");
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.top.contentFrame.topFrame.importReport();");
			out.println("</script>");
			*/
			return null;
		}else if(selectvalue.equals("post")){
			organization.loadOrganizationModel();
			//return redirectModelAndView("/organization.do?method=organizationFrame&from=Post");
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		}else if(selectvalue.equals("team")){
			/*
			organization.loadOrganizationModel();
			//return  redirectModelAndView("/organization.do?method=organizationFrame&from=Team");
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.top.contentFrame.topFrame.importReport();");
			out.println("</script>");
			*/
			return null;
		}else if(selectvalue.equals("member")){
			//这里做UPDATE代价太大了！！！tanglh
			//Map hm = new HashMap(); 
			List fnew=(List)mp.get("new");
			fnew=this.addMemberLoginname(fnew);
			mp.put("new", fnew);
			if(!"1".equals(repeat)){
				List fup=(List)mp.get("dup");
				fup=this.addMemberLoginname(fup);
				mp.put("dup", fup);
			}
			organization.loadOrganizationModel();
			/*
			List<V3xOrgMember> memberlst = orgManagerDirect.getAllMembers(Long.valueOf(accountid), false);	
			for(V3xOrgMember member : memberlst){
				member.setPassword("123456");
				member.setLoginName((String)hm.get(member.getId().toString()));
				//还要在这里UPDATE一次！！！tanglh
				log.info("updateEntity(member) for member.setLoginName((String)hm.get(member.getId().toString()));");
				
				orgManagerDirect.updateEntity(member);
				
				
				//orgManagerDirect.updateEntity(member);
//				// 添加个人博客记录
//				blogManager.createEmployee(member.getId(), user.getLoginAccount());				
			}
			*/
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
			//return  redirectModelAndView("/organization.do?method=organizationFrame&from=Member");
		}else if(selectvalue.equals("department")){
			/*
			organization.loadOrganizationModel();
			List<V3xOrgDepartment> deptlst = this.getOrganizationServices().getOrgManagerDirect()
			                                     .getAllDepartments(Long.valueOf(accountid), false);
			for(V3xOrgDepartment dept : deptlst){
				dept.init((CallbackAddInitialData)this.getOrganizationServices().getOrgManagerDirect());
				addRelationShip(dept);
			}
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.top.contentFrame.topFrame.importReport();");
			out.println("</script>");
			*/
			return null;
			
			//return  redirectModelAndView("/organization.do?method=showframe&style=list");
		}else if(selectvalue.equals("account")){
			/*
			List accountlst = (List)mp.get("new");
			for(int i=0;i<accountlst.size();i++){
				V3xOrgAccount voa = (V3xOrgAccount)accountlst.get(i);
				organization.reloadAccountData(voa.getId());
				//加管理员
				V3xOrgMember member = new V3xOrgMember();
	
				String adminNameValue = ResourceBundleUtil
						.getString(
								"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
								"org.account_form.adminName.value", "");
	
				member.setLoginName(voa.getAdminName());
				member.setPassword("000000");
				member.setName(adminNameValue);
				member.setIsAdmin(true);
				member.setOrgAccountId(voa.getId());
				member.setOrgDepartmentId(voa.getId());
				orgManagerDirect.addMember(member);		
				addAccountInitialRole(voa);
				// 添加单位文档
				docLibManager.addSysDocLibs(voa.getId());	
				//--向新增的单位增加套红模板..
				edocDocTemplateManager.addEdocTemplate(voa.getId());
				
			}
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.top.contentFrame.topFrame.importReport();");
			out.println("</script>");
			*/
			return null;
			
			//return redirectModelAndView("/organization.do?method=organizationFrame&from=Account");
		}else{
			DataUtil.setResultList(resultlst, (List)mp.get("new"), "",locale);
			DataUtil.setResultList(resultlst, (List)mp.get("dup"), repeat,locale);
			
			
			DataUtil.setResultToSession(resultlst,session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		}
	}
	private List addMemberLoginname(List members){//tanglh
		if(members==null)
			return members;
		List ms=new ArrayList();
		
		for(int i=0;i<members.size();i++){    //
			V3xOrgMember vomember = (V3xOrgMember)members.get(i);
			log.info("vomember.id="+vomember.getId());//tanglh
			log.info("vomember.getLoginName()="+vomember.getLoginName());//tanglh
			//hm.put(vomember.getId().toString(), vomember.getLoginName());
			
			V3xOrgMember mm=null;
			try{
				mm=this.getOrganizationServices().getOrgManagerDirect().getMemberById(vomember.getId());
			}catch(Exception e){
				
			}
			if(mm==null || mm.getIsDeleted()){
				log.info("null mm");
				continue;
			}
			boolean done=true;
			if(!org.springframework.util.StringUtils.hasText(mm.getPassword())){
				mm.setPassword("123456");
				done=false;
			}else{
				log.info("password="+mm.getPassword());
			}
			if(!org.springframework.util.StringUtils.hasText(mm.getLoginName())
					|| "null".equals(mm.getLoginName())){
				mm.setLoginName(vomember.getLoginName());
				done=false;
			}else{
				log.info("LoginName="+mm.getLoginName());
			}
			if(!done){
				try{
					this.getOrganizationServices().getOrgManagerDirect().updateEntity(mm);
					log.info("update member ok!");

				}catch(Exception e){
					log.info("update member error");   
					/*
					//如果不能插入帐户，则人员信息有脏数据，会对前台显示、操作产生影响 
					try{
						mm.setIsAssigned(false);
						mm.setEnabled(false);
						orgManagerDirect.updateEntity(mm);
						members.remove(vomember);
						log.info("del member ok");   
						continue;
					}catch(Exception ee){
						log.info("del member ok",e);   
					}
					*/
					//String sql="delete from v3x_org_member where id="+mm.getId();
					StringBuffer sql=new StringBuffer();
					sql.append("delete from v3x_org_member where id="+mm.getId());
					List sqls=new ArrayList();
					
					try{
						sqls.add(sql);
						getDataManagerImpl().execSQLList(sqls);
						log.info("del member ok");   
					}catch(Exception ee){ 
						log.info("del member ok",ee); 
					}
					//members.remove(vomember);
					continue;
				}
				
				try{/*
//					 添加个人文档库,
					docLibManager.addDocLib(mm.getId());
					*/
				}catch(Exception e){
					
				}
			}
			ms.add(vomember);	
		}	
		return ms;
	}

	private File uploadFile(HttpServletRequest request) throws Exception {
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

	public ModelAndView expOrgToExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DataRecord[] drArray = new DataRecord[5];
		User user = CurrentUser.get();
//		----------------------------------accountSheet---------------------
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String dept_list = ResourceBundleUtil.getString(resource, local, "org.dept_form.list");
		V3xOrgAccount account = this.getOrganizationServices().getOrgManagerDirect().getAccountById(user.getLoginAccount());
		List<V3xOrgDepartment> deptlist = this.getOrganizationServices().getOrgManagerDirect().getAllDepartments(user.getLoginAccount());
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			Long longid = dept.getId();
			V3xOrgDepartment parent = this.getOrganizationServices().getOrgManagerDirect().getParentDepartment(longid);
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			} else {
				if (dept.getPath().indexOf(".") > 0
						&& (dept.getPath().indexOf(".") == dept.getPath()
								.lastIndexOf("."))) {
					webdept.setParentId(dept.getOrgAccountId());
					webdept.setParentName(account.getName());
				}
			}
			resultlist.add(webdept);
		}
		drArray[0] = OrganizationHelper.exportDept(
				resultlist, request, metadataManager, response, fileToExcelManager
				     , this.getOrganizationServices().getOrgManagerDirect(), spaceManager, account);

		//----------------------------------deptSheet------------------------
		
		//----------------------------------teamSheet------------------------
		String team_list = ResourceBundleUtil.getString(resource, local, "team.list");
			drArray[1] = OrganizationHelper.exportTeam(request, metadataManager, response, fileToExcelManager
					   , this.getOrganizationServices().getOrgManagerDirect());
			//-----------------------------teamSheet-------------------------
			
			//-----------------------------levelSheet------------------------
			String level_list = ResourceBundleUtil.getString(resource, local, "org.level_form.list");
				drArray[2] = OrganizationHelper.exportLevel(request, response, fileToExcelManager
						      , this.getOrganizationServices().getOrgManagerDirect());
			//--------------------------levelSheet---------------------------
			
			
			//--------------------------postSheet----------------------------
			String post_list = ResourceBundleUtil.getString(resource, local, "org.post_form.list");
			drArray[3] = OrganizationHelper.exportPost(request, metadataManager, response, fileToExcelManager
					   , this.getOrganizationServices().getOrgManagerDirect());
			//-------------------------------postSheet-----------------------
			
			//-------------------------------memberSheet---------------------
			String member_list = ResourceBundleUtil.getString(resource, local, "org.member_form.list");
			String organization_list = ResourceBundleUtil.getString(resource, local, "org.list");
			drArray[4] = OrganizationHelper.exportMember(request, metadataManager, response, fileToExcelManager
					    , this.getOrganizationServices().getOrgManagerDirect());			
			
			try {
				fileToExcelManager.save(request, response, organization_list+"-" + user.getLoginName(), "location.href", drArray);
			} catch (Exception e) {
				log.error("error", e);
			}
		return null;
	}
	
	public User getOpUser() {
		return opUser;
	}
	public void setOpUser(User opUser) {
		this.opUser = opUser;
	}
	
	public V3xOrgAccount getVaccount() {
		return vaccount;
	}
	public void setVaccount(V3xOrgAccount vaccount) {
		this.vaccount = vaccount;
	}
	public void setVaccountByUser(User u){
		if(u==null)
			return;
		
		try{
			long accountId=u.getAccountId();
			V3xOrgAccount vao=this.getOrganizationServices()
			    .getOrgManagerDirect()
			    .getAccountById(accountId);
			
			setVaccount(vao);
		}catch(Exception e){
			log.error("error", e);
		}
	}
	public void setVaccountByUser(){
		setVaccountByUser(this.opUser);
	}
	
	public List createResultObjectList(Map resultMap){
		List l=new ArrayList();
		
		try{
			List addl=(List)resultMap.get(IImexPort.RESULT_ADD);
			if(addl!=null)
				l.addAll(addl);
			
			List updatel=(List)resultMap.get(IImexPort.RESULT_UPDATE);
			if(updatel!=null)
				l.addAll(updatel);
			
			List ignorel=(List)resultMap.get(IImexPort.RESULT_IGNORE);
			if(ignorel!=null)
				l.addAll(ignorel);
			
			List errorl=(List)resultMap.get(IImexPort.RESULT_ERROR);
			if(updatel!=null)
				l.addAll(errorl);
		}catch(Exception e){
			log.error("error",e);
		}
		
		return l;
	}
	
	public void writeExpEndProcScript(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.closeOnbeforeunload();");
		out.println("</script>");
	}
	
	public static final String EXP_BASE_VIEW="organization/expbase";
	public static final String EXP_REPEATER_VIEW="organization/exprepeater";
	public static final String IMP_BASE_VIEW="organization/impbase"; 
	
	public static final String PARA_TARGET_URL_BASE="paratargeturl"; 
	public static final String PARA_TOMETHOD_BASE="tomethod"; 
	public static final String PARA_METHOD_BASE="method"; 
	
	public ModelAndView toExpRepeater(HttpServletRequest request,
			HttpServletResponse response,String url) throws Exception {
		ModelAndView mv = new ModelAndView(EXP_REPEATER_VIEW);
		mv.addObject(PARA_TARGET_URL_BASE, url);
		
		return mv;
	}
	public ModelAndView toImpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toBaseJSP(request,response,IMP_BASE_VIEW);
	}
	
	public ModelAndView toExpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toBaseJSP(request,response,EXP_BASE_VIEW);
	}
	protected ModelAndView toBaseJSP(HttpServletRequest request,
			HttpServletResponse response,String viewname) throws Exception {
		ModelAndView mv = new ModelAndView(viewname);
		
		String qStr4BaseJsp=getQueryString4BaseJsp(request,response);
		mv.addObject(PARA_TARGET_URL_BASE, qStr4BaseJsp);
		
		return mv;
	}
	protected String getQueryString4BaseJsp(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String org=request.getQueryString();
		log.info("QueryString="+org);
		if(!StringUtils.hasText(org))
			return org;
		int mt=org.indexOf(PARA_TOMETHOD_BASE);
		if(mt<0)
			return org;
		
		return org.substring(mt+2);
	}
	
//	tanglh  判断是否可以执行IO
	public String canIO() throws Exception {
		return this.canIO(catchCurrentUser());
	}
//	tanglh  判断是否可以执行IO
	public String canIO(User u) throws Exception {
		if(u==null){
			return "nouser";
		}		
		
		return canIO(u.getId());
	}
	public String canIO(long userid) throws Exception {
		return DataUtil.doingImpExp(userid)?"doing":"ok";
	}
	public String canIO(String userid) throws Exception {
		try{
			return canIO(Long.parseLong(userid));
		}catch(Exception e){
			return canIO();
		}
	}
	
	private User catchCurrentUser(){
		return CurrentUser.get();
	}
}//end class
