package com.seeyon.v3x.a8genius.controller;

import static java.io.File.separator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ProductVersionEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.UserMessageManagerImpl;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.identification.manager.IdentificationManager;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.PropertiesUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;

/**
 * @version 2009-10-26
 * A8精灵
 *
 */
public class A8geniusController extends BaseController
{
	public A8geniusController() {
		super();
		init();
	}

	private OrgManager orgManager;
	private IdentificationManager identificationManager;
	private StringBuffer sLinkTypes ;
	private PartitionManager partitionManager;
	private SystemConfig systemConfig; 
	private FileManager fileManager;
	private OnLineManager onLineManager;
	
	private Properties config = null;
	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setIdentificationManager(IdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setOnLineManager(OnLineManager onLineManager) {
        this.onLineManager = onLineManager;
    }
	
    public void init(){
    	sLinkTypes = new StringBuffer();
		Map<String, String> messageLinkTypes = UserMessageManagerImpl.getMessageLinkType();

		for (String key:messageLinkTypes.keySet()) {
			sLinkTypes.append(key).append("=").append(messageLinkTypes.get(key)).append("\r\n");
		}
		
		String confFilePath = SystemEnvironment.getA8ApplicationFolder() + "/client/a8wizard/a8wizard.properties" ;
		config = PropertiesUtil.getFromAbsolutepath(confFilePath);
	}	
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		return null;
	}
	
	/**
	 * 弹出窗口框架页
	 */
	public ModelAndView window(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mv = new ModelAndView("geniues/window");
		mv.addObject("url", request.getParameter("url"));
		mv.addObject("jsessionid", request.getParameter("jsessionid"));
		return mv;
	}
	
	/**
	 * 弹出窗口框架主区域
	 */
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mv = new ModelAndView("geniues/windowMain");
		mv.addObject("url", request.getParameter("url"));
		mv.addObject("jsessionid", request.getParameter("jsessionid"));
		return mv;
	}
	/*
	 * 为精灵窗口框架模拟的top和left页面
	 */
	public ModelAndView fake(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mv = new ModelAndView("geniues/fake"+request.getParameter("frame"));
		return mv;
	}	
	@NeedlessCheckLogin
	public ModelAndView redirect(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String url = request.getParameter("url");
		String jsessionid = request.getParameter("jsessionid");

		PrintWriter out = response.getWriter();
		if (StringUtils.isBlank(url)) {
			out.println("url is null");
		}
		else if (StringUtils.isBlank(jsessionid)) {
			out.println("jsessionid is null");
		}
		else {
			if(url.indexOf("indexOpenWindow")!=-1){ //打开首页
				out.println("<script type=\"text/javascript\">");
				out.println("<!--");
				out.println("document.cookie =\"JSESSIONID=" + jsessionid + ";Path=/\";");
				out.println("location.href =\"" + url+"\";");
				out.println("//-->");
				out.println("</script>");	
			}
			else{
				ModelAndView mv = new ModelAndView("geniues/redirect");
				if(url.indexOf("message.do")!=-1){ //
					mv.addObject("sendMessage", true);
				}
				else{
					mv.addObject("sendMessage", false);
				}
				mv.addObject("url", url);
				mv.addObject("jsessionid", jsessionid);
				
				return mv;				
			}
		}
		
		out.flush();
		out.close();
		
		return null;
	}
	
	
	/**
	 * 为精灵消息界面提供参数
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@NeedlessCheckLogin
	public ModelAndView geniuesWin(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		ModelAndView mv = new ModelAndView("geniues/geniuesWin");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String jsessionid = request.getParameter("jsessionid");

		PrintWriter out = response.getWriter();
		if (StringUtils.isBlank(jsessionid)) {
			out.println("jsessionid is null");
		}else {
			    mv.addObject("systemConfig", systemConfig().toString().replace("\r\n", " "));			    
			    mv.addObject("sLink", sLinkTypes.toString().replace("\r\n", " "));
				return mv;
		}
		return null;
	}

    public ModelAndView getLoginInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String name = StringUtils.EMPTY;
    	User user = CurrentUser.get();
    	
    	name = user.getName();
    	boolean isAdmin  = user.isAdmin();
    	
    	PrintWriter out = response.getWriter();
    	
    	out.println("user.name=" + name);
    	out.println("user.isAdmin=" + isAdmin);
    	out.println("online.number=" + onLineManager.getOnlineNumber());
    	
    	return null;
    }
    
	@NeedlessCheckLogin    
    public ModelAndView getSystemConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

    	PrintWriter out = response.getWriter();
		out.println(systemConfig());
		
		//TODO 最好做个映射表
		String geniusVersion = request.getParameter("geniusVersion"); //1.0101022
		if(geniusVersion != null && geniusVersion.startsWith("2.0")){
			out.println("allow.login=true");
		}
		
//		// ca 设置验证串
//		if(SystemEnvironment.hasPlugin("ca")){
//			String toSign = String.valueOf(System.currentTimeMillis());
//			request.getSession().setAttribute("ToSign",toSign );
//			out.println("toSign="+toSign);
//		}
    	return null;
    }
	
	public StringBuffer systemConfig() throws Exception {
    	StringBuffer result = new StringBuffer();
    	String chgLn = "\r\n";
    	// 需要验证加密狗
    	result.append("checkDog=").append(SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.identificationDog.name())?"1":"0").append(chgLn);
    	// 需要验证码
    	result.append("checkVerifyCode=").append("enable".equals(this.systemConfig.get("verify_code"))?"1":"0").append(chgLn);
    	// sessionId的名称，缺省为jsessionid
    	result.append("sessionIdName=").append("JSESSIONID").append(chgLn);
    	// seeyon上下文名称，缺省为seesyon
    	result.append("seeyonContext=").append("seeyon").append(chgLn);
    	//消息轮询时间
    	result.append("message.interval.second=").append(SystemProperties.getInstance().getIntegerProperty("message.interval.second", 30)).append(chgLn);
    	//文件上传最大限制
    	String fileUpload_maxSize = SystemProperties.getInstance().getProperty("fileUpload.maxSize");
    	result.append("fileUpload.maxSize=").append(fileUpload_maxSize).append(chgLn);
    	result.append("client.version=").append(config.getProperty("version", "")).append(chgLn);
    	
    	result.append("productVersion=" + Functions.getVersion()).append(chgLn);
    	result.append("buildId=B" + Datetimes.format(SystemEnvironment.getProductBuildDate(), "yyMMdd") + "." + SystemEnvironment.getProductBuildVersion()).append(chgLn);
    	result.append("productCategory=" + ProductInfo.getEditionA()).append(chgLn);
    	
    	//需要验证CA
    	if(SystemEnvironment.hasPlugin("ca")){
	    	result.append("checkCA=").append(SystemEnvironment.hasPlugin("ca")?"1":"0").append(chgLn);
	    	result.append("ca.factory=").append(SystemProperties.getInstance().getProperty("ca.factory")).append(chgLn);
	    	result.append("ca.filterstr=").append(SystemProperties.getInstance().getProperty("ca.filterstr")).append(chgLn);
    	}
		return result;
	}
	
	@NeedlessCheckLogin    
    public ModelAndView upgrade(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	if("getFile".equals(request.getParameter("q")))
    	{
    		response.sendRedirect("/seeyon/client/a8wizard/upgrade.exe");
    	}
    	return null;
    }
	
    public ModelAndView uploadFiles(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			
			V3xOrgMember member = orgManager.getMemberByLoginName(CurrentUser.get().getLoginName());
			
			PrintWriter out = response.getWriter();
			String error = "uploadError:";
			String uploadok = "uploadOk:";
			
			//这是来上传的异常，大小是由系统总配置，50M
			String maxUploadSizeExceeded = multipartRequest.getParameter("MaxUploadSizeExceeded");
			if (maxUploadSizeExceeded != null) {
				//1 文件大小超过系统规定大小。最大50
				out.println(error);
				out.println("1");
				return null;
			}			
			String ex = multipartRequest.getParameter("unknownException");
			if(ex != null){
				//上传文件异常
				out.println(error);
				out.println("2");
				return null;
			}
			Date createDate = new Date();
			String destDirectory=partitionManager.getFolder(createDate, true);
			StringWriter  sb=new StringWriter();
			Iterator fileNames = multipartRequest.getFileNames();
			if (fileNames == null) {
				//上传文件为空
				out.println(error);
				out.println("3");
				return null;
			}
			while (fileNames.hasNext()) {
				Object name = fileNames.next();
				V3XFile file=null;
				if (name == null || "".equals(name)) {
					continue;
				}
				MultipartFile fileItem = multipartRequest.getFile(String
						.valueOf(name));
				if (fileItem == null) {
					continue;
				}
				long maxFileSize=-1l;
				String fileUpload_maxSize = SystemProperties.getInstance().getProperty("fileUpload.maxSize");
				if(Strings.isNotBlank(fileUpload_maxSize))
				{
					maxFileSize=Long.parseLong(fileUpload_maxSize);	
				}
				//限制大小
				if(fileItem.getSize() > maxFileSize){
					out.println(error);
					out.println("1");
					return null;
				}
				long fileId = UUIDLong.longUUID();
				File destFile = null;
				// 保存硬盘
				try {
					destFile = new File(destDirectory + separator+ String.valueOf(fileId));

					String encryptVersion = CoderFactory.getInstance().getEncryptVersion();
					if(encryptVersion != null && !IConfigPublicKey.NO.equals(encryptVersion)) {
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
						CoderFactory.getInstance().upload(fileItem.getInputStream(), bos,encryptVersion);
					}else
						fileItem.transferTo(destFile);
				}
				catch (Exception e) {
					return null;
				}

				file = new V3XFile(fileId);
				file.setIdIfNew();
				file.setCreateDate(createDate);
				file.setFilename(String.valueOf(name));
				file.setSize(fileItem.getSize());
				file.setMimeType(fileItem.getContentType());
				file.setCreateMember(member.getId());
				file.setAccountId(member.getOrgAccountId());
				file.setType(Constants.ATTACHMENT_TYPE.FILE.ordinal());
				fileManager.save(file);
				sb.append(fileId+"|");
			}		
			out.println(uploadok);
			out.println(sb.toString());
		return null;
    }
    
}