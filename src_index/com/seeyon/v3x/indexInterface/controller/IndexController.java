package com.seeyon.v3x.indexInterface.controller;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.index.ConfigHolder;
import com.seeyon.v3x.index.IndexApplicationCategoryUtil;
import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.datamodel.SearchResult;
import com.seeyon.v3x.index.share.datamodel.SearchResultWapper;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.Constant;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.indexInterface.ProxyManager;
import com.seeyon.v3x.indexInterface.domain.UrlLinkDAO;
import com.seeyon.v3x.indexInterface.util.IndexSearchHelper;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.util.Datetimes;
public class IndexController extends BaseController {
	
	private static final Log log = LogFactory
    .getLog(IndexController.class);
	private FileManager fileManager;
	private IndexManager indexManager;
	private UrlLinkDAO urlLinkDAO;
	private OrgManager orgManager;
	private ConfigHolder configHolder;
	private ProjectManager projectManager;
	private DocLibManager docLibManager;
	private DocHierarchyManager docHierarchyManager;
	private IndexApplicationCategoryUtil indexApplicationCategoryUtil;
	public void setIndexApplicationCategoryUtil(
			IndexApplicationCategoryUtil indexApplicationCategoryUtil) {
		this.indexApplicationCategoryUtil = indexApplicationCategoryUtil;
	}

	public void setConfigHolder(ConfigHolder configHolder) {
		this.configHolder = configHolder;
	}

	/**
	 * 显示全文检索配置信息
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView showIndexConfig(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv=new ModelAndView("index/indexConfig");
		Properties prop = IndexPropertiesUtil.getInstance().readProperties();
		
		Iterator it=prop.entrySet().iterator();
		while(it.hasNext()){
		    Map.Entry entry=(Map.Entry)it.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    mv.addObject(key.trim(), value.trim());
		}
		return mv;
	}
	
	public ModelAndView openHelp(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv=new ModelAndView("index/help");
		return mv;
	}
	/**
	 * 更新全文检索配置信息
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView updateIndexConfig(HttpServletRequest request,
			HttpServletResponse response) {
		
		Properties prop = IndexPropertiesUtil.getInstance().readProperties();
		
		prop.setProperty("modelName", (request.getParameter("modelName")== null) ? "" : request.getParameter("modelName").trim());
		prop.setProperty("indexIp", (request.getParameter("indexIp")== null) ? "" : request.getParameter("indexIp").trim());
		prop.setProperty("indexPort", (request.getParameter("indexPort")== null) ? "" : request.getParameter("indexPort").trim());
		prop.setProperty("indexServiceName", (request.getParameter("indexServiceName")== null) ? "" : request.getParameter("indexServiceName").trim());
		prop.setProperty("indexParseTimeSlice", (request.getParameter("indexParseTimeSlice")== null) ? "" : request.getParameter("indexParseTimeSlice").trim());
		prop.setProperty("indexUpdateTimeSlice", (request.getParameter("indexUpdateTimeSlice")== null) ? "" : request.getParameter("indexUpdateTimeSlice").trim());
		prop.setProperty("a8Ip", (request.getParameter("a8Ip")== null) ? "" : request.getParameter("a8Ip").trim());
		
		String outMsg = ResourceBundleUtil.getString("com.seeyon.v3x.indexInterface.resource.i18n.IndexResources", "com.seeyon.v3x.index.setupSuccess");
		
		// 本地磁盘保存
		IndexPropertiesUtil.getInstance().writeProperties(prop);
		
		// 远程保存
		if(prop!=null && prop.getProperty("modelName")!=null && prop.getProperty("modelName").equals("remote")){
			try {

				// 开远程rmi
				IndexUtil.getRMIClientProxy((ProxyManager)indexManager);
//				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
//				proxyFactory
//						.setServiceInterface(com.seeyon.v3x.index.share.interfaces.IndexManager.class);
//				String indexAttServiceAddress = "rmi://"+prop.getProperty("indexIp")+":"+prop.getProperty("indexPort")+"/"+prop.getProperty("indexServiceName");
//				proxyFactory.setServiceUrl(indexAttServiceAddress);
//				//proxyFactory.setRefreshStubOnConnectFailure(true);//true表示持续连接
//				try {
//					proxyFactory.afterPropertiesSet();
//				} catch (Exception e) {
//					//System.out.println(e);
//					log.error(e);
//				}
//				IndexManager object = (IndexManager) proxyFactory.getObject();
//				if(object==null)
//				{
//					log.warn("远程全文检索服务异常... 请检查配置"+object);
//					throw new Exception("远程全文检索服务异常... 请检查配置");
//				}
//				((ProxyManager)indexManager).setRealManager(object);
//				
				// 远程保存
				indexManager.setA8Info(prop.getProperty("a8Ip"), prop
						.getProperty("indexParseTimeSlice"), prop.getProperty("indexUpdateTimeSlice"),
						prop.getProperty("indexPort"), prop
								.getProperty("indexServiceName"));
			} catch (Exception e) {
				//e.printStackTrace();
				log.error("init index error", e);
				outMsg = ResourceBundleUtil.getString("com.seeyon.v3x.indexInterface.resource.i18n.IndexResources", "com.seeyon.v3x.index.setupFailure");
			}

		}
		
		return super.redirectModelAndView("/indexInterface.do?method=showIndexConfig&outMsg="+outMsg);		
	
	}
	
	/*
	 * 显示上传文件界面
	 */
	public ModelAndView showSelectPage(HttpServletRequest request,
            HttpServletResponse response){
		ModelAndView mv=new ModelAndView("index/selectfile");
		return mv;
		
	}
	
	public ModelAndView ShowSearchPage(HttpServletRequest request,
            HttpServletResponse response){
		ModelAndView mv=new ModelAndView("index/search");
		return mv;
		
	}
	/*
	 * 索引已上传的文件
	 * 文件的类型如下，根据下列数据选择：
	 * Type:application/vnd.ms-powerpoint
	 * Type:application/vnd.ms-excel
	 * Type:application/msword
	 * Type:text/plain        --text与HTML
	 * Type:application/pdf
	 * 
	 */
	public ModelAndView indexFile(HttpServletRequest request,
            HttpServletResponse response){
		String[] urls=(String[])request.getParameterValues("fileUrl");
		String[] createDates=(String[])request.getParameterValues("fileCreateDate");
		String[] mimeTypes=(String[])request.getParameterValues("fileMimeType");
		String[] names=(String[])request.getParameterValues("filename");
		for(int i=0;i<urls.length;i++){
			Long fileId=Long.parseLong(urls[i]);
			
			Date createDate=null;
			createDate = Datetimes.parseDatetime(createDates[i]);;
			try {
				File file=fileManager.getFile(fileId, createDate);
				int contentType=getContentType(mimeTypes[i]);
				
				IndexInfo info=new IndexInfo();
				info.setEntityID(fileId);
				info.setTitle(names[i]);
				info.setMimeContent(file);
				info.setContentType(contentType);
				info.setAppType(ApplicationCategoryEnum.collaboration);
				indexManager.index(info);
				
			} catch (BusinessException e) {
				log.error(e.getMessage(), e);
			}
		}
		ModelAndView mv=new ModelAndView("index/sucess");
		return mv;
	}
	public int getContentType(String type){
		int contentType=-1;
		if("application/msword".equals(type)){
			contentType=IndexInfo.CONTENTTYPE_WORD;
		}
		if("application/vnd.ms-excel".equals(type)){
			contentType=IndexInfo.CONTENTTYPE_XLS;
		}
		if("application/vnd.ms-powerpoint".equals(type)){
			contentType=IndexInfo.CONTENTTYPE_PPT;
		}
		if("text/plain".equals(type)){
			contentType=IndexInfo.CONTENTTYPE_TXT;
		}
		if("application/pdf".equals(type)){
			contentType=IndexInfo.CONTENTTYPE_PDF;
		}
		return contentType;
	}
	public ModelAndView goToAdvancePage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		if("content".equals(request.getParameter("page"))){
			mv=new ModelAndView("index/searchResultNew");
			List<Integer> appLibs=new ArrayList<Integer>(indexApplicationCategoryUtil.getAllAppInt());
			mv.addObject("appLibs",appLibs);
		}else{
			mv=new ModelAndView("index/index");
		}
		return mv;
		
	}

	/*
	 * keyword为空，刷新页面，跳转到状态条jsp
	 */
	public ModelAndView showNullList(HttpServletRequest request,
            HttpServletResponse response){
		ModelAndView mv=new ModelAndView("index/searchResultError");
		return mv;
		
	}
	/*
	 * 查询方法
	 */
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("index/index");
		String key = request.getParameter("keyword");
		String advanceOptionStr=request.getParameter("AdvanceOption");
		mv.addObject("keyword", key);
		mv.addObject("AdvanceOption", advanceOptionStr);
		return mv;
	}
	/*
	 * 查询方法
	 */
	public ModelAndView searchAll(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		try {
			String sortName = request.getParameter("sortName");
			String author=request.getParameter(IndexInfo.APP_AUTHOR);
			String title=request.getParameter(IndexInfo.TITLE);
			Boolean hasResult=false;
			String pageStr = request.getParameter("page");
			int pageSize = RequestUtils.getIntParameter(request, "pageSize", configHolder.getSizeOfPage());
		
			if (pageStr == null)
				pageStr = "1";
			int page = Integer.parseInt(pageStr);
			
			//点击后退按钮，翻页页码不正确
			if("pre".equals(request.getParameter("preornext"))){
				page = page -1;
			}else if("next".equals(request.getParameter("preornext"))){
				page = page +1;
			}
			
			int firstResult = (page-1)*pageSize;
			
			String searchBeginDate=request.getParameter(IndexInfo.PARAMETER_SEARCHDATE_BEGIN);
			String searchEndDate=request.getParameter(IndexInfo.PARAMETER_SEARCHDATE_END);

			User user = CurrentUser.get();
			
			Long id = user.getId();
			Long departmentId=user.getDepartmentId();
			Long account = user.getAccountId();
			
			String key = request.getParameter("keyword");

			if(StringUtils.isBlank(key)){
				key="";
			}
			if("iframeSearch".equals(request.getParameter("iframeSearch"))){
				mv = new ModelAndView("index/searchResultList");
			}else{
				mv = new ModelAndView("index/searchResultNew");
				List<Integer> appLibs=new ArrayList<Integer>(indexApplicationCategoryUtil.getAllAppInt());
				mv.addObject("appLibs", appLibs);
				mv.addObject("keyword", key);
				return mv;
			}
			if(StringUtils.isBlank(key)&&StringUtils.isBlank(author)&&StringUtils.isBlank(title))
			{
				return mv;
			}
			String originalKey=key;
			String originalAuthor=author;
			String originalTitle=title;
			//--------------防止页面被挤变形--------------------------- 
			if(key!=null && key.length()>40){
				key = key.substring(0,40);
			}
			String authorKey = IndexSearchHelper.getAuthorKey();
			SearchResultWapper resultWapper = null;
			SearchResult[] results = null;
			// authorKey=null;
			String[] libStr=request.getParameterValues("library");
//			String libStr=request.getParameter("library");
			String[] lib=null;
			if(libStr!=null){
				lib=new String[libStr.length];
				for (int i = 0; i < libStr.length; i++) {
					ApplicationCategoryEnum app=ApplicationCategoryEnum.valueOf(Integer.parseInt(libStr[i]));
					lib[i]=app.name();
				}
			}else{
				 List<String> indexNames=indexApplicationCategoryUtil.getIndexAllAppName();
				 lib=indexNames.toArray(new String[indexNames.size()]);
			}
			Map<String, String> keyMap=new HashMap<String, String>();//用于放置查询关键字的Map
			//将输入的*，(,),[,],{,} 转义+ - && || !  ^ " ~ ? : \
            key = IndexSearchHelper.replaceSearchKey(key);
            author=IndexSearchHelper.replaceSearchKey(author);
            title=IndexSearchHelper.replaceSearchKey(title);
			keyMap.put(IndexInfo.PARAMETER_KEYWORD, key);
			keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_BEGIN, searchBeginDate);
			keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_END, searchEndDate);
			keyMap.put(IndexInfo.SORT_TYPE, sortName);
			keyMap.put(IndexInfo.APP_AUTHOR, author);
			keyMap.put(IndexInfo.TITLE, title);
			keyMap.put("pageSize", pageSize+"");
			long time0 = System.currentTimeMillis();
			try {
				resultWapper = indexManager.search(authorKey, keyMap,lib, firstResult);
				results = resultWapper.getSearchResults();
			} catch (Throwable e) {
				log.error("全文检索异常: "+e.getMessage(),e);
				mv.addObject("hasError", ResourceBundleUtil.getString("com.seeyon.v3x.indexInterface.resource.i18n.IndexResources", "com.v3x.index.error"));
				return mv;
			}
			long time1 = System.currentTimeMillis();
			long time3 = time1 - time0;
			String time=new DecimalFormat("0.00").format(Double.parseDouble(time3+"")/Double.parseDouble("1000"));
		
			if (!ArrayUtils.isEmpty(results)) {
				resultUrlHandle(resultWapper,id);
				hasResult=true;
			}
			pageCountHandle(page,resultWapper,mv,pageSize);
			
			V3xOrgMember member;
			try {
				member = orgManager.getMemberById(id);
				String setShowIndexSummary=member.getProperty(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY);
				mv.addObject(com.seeyon.v3x.indexInterface.Constant.ISSHOWINDEXSUMMARY, setShowIndexSummary);
			} catch (BusinessException e) {
			}
			mv.addObject("hasResult", hasResult);
			mv.addObject("keyword", originalKey);
			mv.addObject("author", originalAuthor);
			mv.addObject("title", originalTitle);
			mv.addObject("totalCount", resultWapper.getResultCount());
			mv.addObject("time", time);
			mv.addObject("searchResults", results);
			mv.addObject("isAfterSearch",Boolean.TRUE);
//			mv.addObject("AdvanceOption", advanceOption);
			mv.addObject(IndexInfo.PARAMETER_SEARCHDATE_BEGIN, searchBeginDate);
			mv.addObject(IndexInfo.PARAMETER_SEARCHDATE_END, searchEndDate);
			mv.addObject("author", originalAuthor);
			mv.addObject("sortName", sortName);
			if(libStr!=null)
			{
				mv.addObject("libStr", libStr);
			}
		} catch (RuntimeException e) {
			log.error("", e);
//			e.printStackTrace();
		}

		return mv;

	}
	/*
	 * 和权  索引升级（3.5）
	 */
	public ModelAndView indexUpgrade(HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter out  = null;
		try {
			out = response.getWriter();
			out.println("<script type=\"text/javascript\">");
			if(indexManager.isToAdding()){
				out.println("alert(\"全文检索升级中,请稍候!\")");
			}else{
				if(indexManager.addIndexToRecord()){
					out.println("alert(\"全文检索已经升级完成\")");
				}else{
					out.println("alert(\"全文检索已经升级过。\")");
				}
			}
		} catch (Exception e) {
			log.error("", e);
			out.println("alert('"+e+"')");
		}finally{
			if(out!= null){
				out.println("</script>");
				out.flush();
				out.close();
			}
		}
		return null;
	}

	/* @page:当前要求页
	 * @resultWapper:搜索的结果集
	 * @mv:返回的MV
	 * 在此进行分页计算，总页数，是否显现上一页、下一页
	 */
	public void pageCountHandle(int page,SearchResultWapper resultWapper,ModelAndView mv,int pageSize){
		Boolean showPrePage = false;
		Boolean showNextPage = false;
		boolean firstPage=false;
		boolean lastPage=false;
		
		int prePage = 0;
		int nextPage = 0;
		if(pageSize<=0||pageSize>999)
		{
			 pageSize = configHolder.getSizeOfPage();
		}
		int totalCount = resultWapper.getResultCount();
		if(totalCount<(page-1)*pageSize){
			page = 1;
		}
		int lastResult = (page - 1) * pageSize + resultWapper.getSearchResults().length;
		
		int totalPage = totalCount % pageSize ==0? totalCount / pageSize:totalCount / pageSize + 1;
		if ((page - 1) >= 1) {
			showPrePage = true;
			prePage = page - 1;
		}
		if ((page + 1) <= totalPage) {
			showNextPage = true;
			nextPage = page + 1;
		}
		
		if(totalPage!=1&&page!=1)
		{
			firstPage=true;
		}
		if(totalPage!=0&&totalPage!=1&&page!=totalPage)
		{
			lastPage=true;
		}
		mv.addObject("prePage", prePage);
		mv.addObject("nextPage", nextPage);
		mv.addObject("showPrePage", showPrePage);
		mv.addObject("showNextPage", showNextPage);
		mv.addObject("totalPage", totalPage);
		mv.addObject("currentPage", page);
		mv.addObject("firstResult", (page-1)*pageSize + 1);
		mv.addObject("lastResult", lastResult);
		mv.addObject("firstPage", firstPage);
		mv.addObject("lastPage", lastPage);
		mv.addObject("pageSize", pageSize);
		
	}
	/*
	 * 在此对结果的URL进行处理
	 */
	public void resultUrlHandle(SearchResultWapper resultWapper,Long id){
		SearchResult[] results=resultWapper.getSearchResults();
		for (int i = 0; i < results.length; i++) {
			int appInt=Integer.parseInt(results[i].getAppType());
			
			if (appInt == ApplicationCategoryEnum.collaboration
					.getKey()||appInt == ApplicationCategoryEnum.form
					.getKey()) {
				
				String affairId="-1";
				try {
					affairId = urlLinkDAO.findAffairID(ApplicationCategoryEnum.collaboration
							.getKey(), results[i].getId().toString(),
							id.toString());
				} catch (Exception e) {
					log.error("",e);
					affairId="-1";
				}
				results[i].setLinkId(affairId);
			}else if (appInt == ApplicationCategoryEnum.edoc.getKey()||
					appInt == ApplicationCategoryEnum.edocSend.getKey()||
					appInt == ApplicationCategoryEnum.edocRec.getKey()||
					appInt == ApplicationCategoryEnum.edocSign.getKey()) {
				
				String affairId="-1";
				try {
					List<ApplicationCategoryEnum>  apps =new ArrayList<ApplicationCategoryEnum>();
					apps.add(ApplicationCategoryEnum.edocSend);
					apps.add(ApplicationCategoryEnum.edocRec);
					apps.add(ApplicationCategoryEnum.edocSign);
					affairId = urlLinkDAO.findAffairID(
							apps, results[i].getId().toString(),
							id.toString());
				} catch (Exception e) {
					log.error("",e);
					affairId="-1";
				}
				results[i].setLinkId(affairId);
			}else if(appInt== ApplicationCategoryEnum.doc.getKey()){
				//文档夹共享,移动不更新doc资源实体,只更新权限
				DocResource dr=docHierarchyManager.getDocResourceById(Long.parseLong(results[i].getId()));
				if(isUpateDocPath(results[i].getChangeTime(), dr))
				{
					if(dr==null){return;}
					results[i].setFolderId(String.valueOf(dr.getIsFolder()?dr.getId():dr.getParentFrId()));
					String[] drIds = StringUtils.split(dr.getLogicalPath(), '.');
					StringBuilder pathName = new StringBuilder();
					int y=0;
					for(String drId : drIds) {
						if(y==drIds.length-1)
						{
							continue;
						}
						Long folderId = NumberUtils.toLong(drId);
						DocResource folder = docHierarchyManager.getDocResourceById(folderId);
						if(folder==null){continue;}
						String folderName = Constants.getDocI18nValue(folder.getFrName());
						pathName.append(folderName+">");
						y++;
					}
					results[i].setDocPath(StringUtils.removeEnd(pathName.toString(), ">"));
				}
				Long userId = CurrentUser.get().getId();
				if(dr != null) {
					if(docHierarchyManager.hasDownloadPermission(dr, userId, Constants.getOrgIdsOfUser(userId))){
						if(StringUtils.isBlank(results[i].getFileId()))
						{
							if(dr == null || com.seeyon.v3x.doc.util.Constants.isPigeonhole(dr.getFrType())){continue;}
							Long sourceId=dr.getSourceId();
							if(sourceId!=null)
							{
								results[i].setFileId(sourceId.longValue()+"");
							}
						}
					}else{
						results[i].setFileId("");
					}
				}				
			}
		}
		
	}

	private boolean isUpateDocPath(String changeTime, DocResource dr) {
		if(dr==null)return false;
		return StringUtils.isBlank(changeTime)?true:!changeTime.equals(dr.getLastUpdate().toString());
	}
	

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public FileManager getFileManager() {
		return fileManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setUrlLinkDAO(UrlLinkDAO urlLinkDAO) {
		this.urlLinkDAO = urlLinkDAO;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

}