/**
 * 
 */
package com.seeyon.v3x.common.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.NoSuchPartitionException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.fileupload.util.FileUploadUtil;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.OperationControllable;
import com.seeyon.v3x.common.utils.OperationCounter;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.hbcb.domain.FileDownload;
import com.seeyon.v3x.hbcb.manager.FileDownloadManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.annotation.SetContentType;
/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-11-15
 */
public class FileUploadController extends BaseController {
	private final static Log log = LogFactory.getLog(FileUploadController.class);
	// 下载限制
	private Integer maxDownloadConnections = SystemProperties.getInstance().getIntegerProperty("fileDowload.maxConnections",65535);
	private OperationControllable downloadCounter = new OperationCounter(maxDownloadConnections);
	
	private static Map<String, String> RTE_type = new HashMap<String, String>();
	static{
		RTE_type.put("image", "image/jpeg");
		RTE_type.put("flash", "application/x-shockwave-flash");
	}
	
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";

	private FileManager fileManager;
	
	private String clientAbortExceptionName = "ClientAbortException";
	
	private String contentTypeCharset = "UTF-8"; //使用统一编码 LEIGF 20090911
	
	private String htmlSuffix;
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public void setHtmlSuffix(String htmlSuffix) {
		this.htmlSuffix = htmlSuffix;
	}

	/**
	 * 用户关闭下载窗口时候，有servlet容器抛出的异常
	 * @param clientAbortExceptionName 类的simapleName，如<code>ClientAbortException</code>
	 */
	public void setClientAbortExceptionName(String clientAbortExceptionName) {
		this.clientAbortExceptionName = clientAbortExceptionName;
	}
	
	// 2017-3-20 诚佰公司 添加注入
	private FileDownloadManager fileDownloadManager;
	public void setFileDownloadManager(FileDownloadManager fileDownloadManager) {
		this.fileDownloadManager = fileDownloadManager;
	}
	// 诚佰公司
	

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView processUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/fileUpload/upload");

		String extensions = request.getParameter("extensions");
		String applicationCategory = request.getParameter("applicationCategory");
		String destDirectory = null;//request.getParameter("destDirectory"); //为了安全，此字段永远为空 tanmf
		String destFilename = null;//request.getParameter("destFilename");   //为了安全，此字段永远为空 tanmf
		String typeStr = request.getParameter("type");
		String maxSizeStr = request.getParameter("maxSize");
		String from = request.getParameter("from");

		com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE type = null;
		if (StringUtils.isNotBlank(typeStr)) {
			type = EnumUtil.getEnumByOrdinal(com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.class, new Integer(typeStr));
		}
		else{
			type = com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE;
		}
		
		ApplicationCategoryEnum category = null;
		if(StringUtils.isNotBlank(applicationCategory)){
			category = ApplicationCategoryEnum.valueOf(new Integer(applicationCategory));
		}
		else{
			category = ApplicationCategoryEnum.global;
			log.warn("上传文件：v3x:fileUpload没有设定applicationCategory属性，将设置为‘全局’。");
		}
		
		Long maxSize = null;
		if (StringUtils.isNotBlank(maxSizeStr)) {
			maxSize = new Long(maxSizeStr);
		}

		Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();
		try {
			File destFile = null;
			if(StringUtils.isNotBlank(destFilename)){ //存为指定的文件
				destFile = new File(FilenameUtils.separatorsToSystem(destFilename));
				v3xFiles = fileManager.uploadFiles(request, extensions, destFile, maxSize);
			}
			else if(StringUtils.isNotBlank(destDirectory)){	//存到指定的文件夹
				v3xFiles = fileManager.uploadFiles(request, extensions, destDirectory, maxSize);
			}
			else{ //系统默认分区
				v3xFiles = fileManager.uploadFiles(request, extensions, maxSize);
			}
			
			 if(v3xFiles != null) {
				List<String> keys = new ArrayList<String>(v3xFiles.keySet());
				Collections.sort(keys, new FileFieldComparator());
				
				List<Attachment> atts = new ArrayList<Attachment>();
				for (String key : keys) {
					atts.add(new Attachment(v3xFiles.get(key), category, type));
				}
				
				modelAndView.addObject("atts", atts);
				
				HttpSession session = request.getSession();
				session.setAttribute("repeat", request.getParameter("repeat"));
				
				if(from!=null&&from.equals("a8genius")){
					//在jsonObject中设置中文出现乱码此处需要设置编码（因英福美控件问题，此处先设置为中文，后期调整）
					response.setContentType("text/html;charset=GBK");
					PrintWriter out = response.getWriter();					
					Attachment att = atts.get(0);
					JSONObject jsonObject = new JSONObject();
					jsonObject.putOpt("type", att.getType());	
					jsonObject.putOpt("filename", att.getFilename());					
					jsonObject.putOpt("mimeType", att.getMimeType());
					jsonObject.putOpt("createDate", Datetimes.formatDatetime(att.getCreatedate()));
					jsonObject.putOpt("size", att.getSize());
					jsonObject.putOpt("fileUrl", String.valueOf(att.getFileUrl()));
					jsonObject.putOpt("extension", att.getExtension());
					jsonObject.putOpt("icon", att.getIcon());
        			out.println(jsonObject.toString());
					out.flush();
					return null;
				}
			}
			
		}
		catch (NoSuchPartitionException e) {
			log.error("", e);
			modelAndView.addObject("e", e);
		}
		catch (BusinessException e) {
			log.error("", e);
			modelAndView.addObject("e", e);
		}
		catch (Exception e) {
			log.error("", e);
			modelAndView.addObject("e", new BusinessException("fileupload.exception.unknown", e.getMessage()));
		}		

		return modelAndView;
	}
	
	/**
	 * 显示正文编辑器中图片或flash
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	public ModelAndView showRTE(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long fileId = new Long(request.getParameter("fileId"));
		Date createDate = Datetimes.parseDate(request.getParameter("createDate"));
		String type = request.getParameter("type");
		if(type.startsWith("flash")){
			type = "flash";
		}
		String mimeType = RTE_type.get(type);
		
		if(mimeType == null){
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		String small = request.getParameter("showType");
		
		File f = null;
		if(!"small".equals(small)){
			f = this.fileManager.getFile(fileId, createDate);
		}else{
			f = this.fileManager.getThumFile(fileId, createDate);
		}
		if(f == null){
			return null;
		}
		
		String etag = String.valueOf(fileId);
		if(WebUtil.checkEtag(request, response, etag)){ //匹配，没有修改，浏览器已经做了缓存
			return null;
		}
		
		WebUtil.writeETag(request, response, etag);
		response.setContentType(mimeType);
		
		InputStream in = null;
		ServletOutputStream out = response.getOutputStream();
		try {
			in = new FileInputStream(f);
			if (in != null) {
				CoderFactory.getInstance().download(in, out);
			}
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.debug("用户关闭下载窗口: " + e.getMessage());
			}
			else {
				log.error("", e);
			}
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
		return null;
	}
	
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView m = new ModelAndView("common/fileUpload/download");
		
		// 2017-3-20 诚佰公司 添加下载次数判断
		User user = CurrentUser.get();
		String filename = request.getParameter("filename");
		Long fileId = Long.parseLong(request.getParameter("fileId"));
		V3XFile v3xFile = fileManager.getV3XFile(fileId);
		if (v3xFile.getCategory() == 1) { // 只处理用户上传的附件，排除系统自带模板文件
			FileDownload fileDownload = fileDownloadManager.getFileDownload(user.getId(), fileId);
			if (fileDownload.getTimes() == null || fileDownload.getTimes() == 0) {
				fileDownload = new FileDownload();
				fileDownload.setId(UUIDLong.longUUID());
				fileDownload.setState(0);
				fileDownload.setMemberId(user.getId());
				fileDownload.setFileId(fileId);
				fileDownload.setFilename(filename);
				fileDownload.setTimes(1);
				fileDownload.setDepartmentId(user.getDepartmentId());
				fileDownload.setAccountId(user.getAccountId());
				fileDownload.setTs(new Date());
				fileDownloadManager.saveFileDownload(fileDownload);
			} else {
				PrintWriter out = response.getWriter();
	    		String msg = "您已下载过此文件，不能重复下载。";
	    		out.println("<script>alert('"+msg+"');</script>");
				return null;
			}
		}
		// 诚佰公司
				
		if(!downloadCounter.check()){
    		PrintWriter out = response.getWriter();
    		String msg = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "fileuoload.downoad.exceedConnection",this.maxDownloadConnections);
    		out.println("<script>alert('"+msg+"');</script>");
			return null;
		}

		// 通知计数器下载开始。
		downloadCounter.start();
		
		Map<String, String> ps = getParameterMap(request);
		
		m.addObject("ps", ps);
		
		String suffix = FilenameUtils.getExtension(filename).toLowerCase();
		
		//如果从精灵发送的请求，这里直接下载，不区分html页
		String from = request.getParameter("from");
		if(from!=null&&from.equals("a8geniues")){
			if(Pattern.matches(htmlSuffix, suffix)){
				m.addObject("isHTML", true);
			}else{
				m.addObject("isHTML", false);
			}			
		}else{
			if (Pattern.matches(htmlSuffix, suffix) && !"mobile".equals(from)){
				m.addObject("isHTML", true);
			}			
		}
		
		return m;

	}
	@SuppressWarnings("unchecked")
	private Map<String, String> getParameterMap(HttpServletRequest request) {
		Map<String, String> ps = new HashMap<String, String>();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			if(name.equalsIgnoreCase("method")){
				continue;
			}
			
			String value = request.getParameter(name);
			
			ps.put(name, value);
		}
		return ps;
	}
	
	public ModelAndView doDownload4html(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new FileDownloader(request, response) {
			void setOutput() {
				String swf=request.getParameter("swf");
				if("true".equals(swf)){
					setFlashContenttype();
				}
				else{
					setDownloadContentType(response, filename, "attachment", "application/x-msdownload");
				}
			}

			void getInputstream() throws Exception {
				String comm=request.getParameter("comm");
				if("byFileId".equals(comm)) {
					V3XFile v3xfile = fileManager.getV3XFile(fileId);
					createDate = v3xfile.getCreateDate();
					filename = v3xfile.getFilename();			
				}
				else{
					createDate = Datetimes.parseDate(request.getParameter("createDate"));
					filename = request.getParameter("filename");
					//filename=new String(filename.getBytes("iso8859-1"),"UTF-8"); //在浏览器打开和保存使用的编码转换存不同，打开时没有进行编码转换，暂时改用GBK
					filename=new String(filename.getBytes(),"iso8859-1");//针对form提交进行转换
				}
				in = fileManager.getFileInputStream(fileId, createDate);
			}

		}.download();			
	}
	abstract class FileDownloader{
		protected final HttpServletRequest request;
		protected final HttpServletResponse response;
		protected Long fileId = null;
		protected Date createDate = null; 
		protected InputStream in = null;
		protected ServletOutputStream out;		
		protected String filename = null;
		protected String contentType;
		private V3XFile v3xFile;
		public FileDownloader(HttpServletRequest request,
				HttpServletResponse response) {
			super();
			this.request = request;
			this.response = response;
		}
		// 避免多次调用getV3XFile
		protected V3XFile getFile() throws BusinessException {
			if(v3xFile == null)
				v3xFile = fileManager.getV3XFile(fileId);
			return v3xFile;
		}
		public ModelAndView download() throws Exception{
			request.setCharacterEncoding(contentTypeCharset);
			fileId = Long.valueOf(request.getParameter("fileId"));
			
			if(!beforeDownload()) return null; 
			out = response.getOutputStream();	
			try {
				contentType = "application/x-msdownload";
				getInputstream();
				
				if (in == null) {
					showError("FileNoFound", null, out, request, response);
					return null;
				}
				else {
					setOutput();
					CoderFactory.getInstance().download(in, out);
				}
			}
			catch (NoSuchPartitionException e) {
				showError(null, e, out, request, response);
				return null;
			}
			catch (Exception e) {
				if (e.getClass().getSimpleName().equals(clientAbortExceptionName)) {
					log.debug("用户关闭下载窗口: " + e.getMessage());
				}
				else {
					showError("Exception", null, out, request, response);
					return null;
				}
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
				
				if("true".equalsIgnoreCase(request.getParameter("deleteFile"))){
					fileManager.deleteFile(fileId, createDate, true);
				}
				// 通知计数器下载结束，释放。
				downloadCounter.end();
			}

			return null;
		}
		protected void setDownloadContentType(HttpServletResponse response,
				String filename, String mode, String contentType1) {
			   // 分号会造成文件名截断，trim
            String name = filename.replace(";", "");
            response.setContentType(contentType1 + "; charset=" + contentTypeCharset);
            String fname;
            if(name.startsWith("filename=") || name.startsWith("filename*=")){
                fname = ";" + name;
            }else{
                fname = ";filename=\"" + name + "\"";
            }
            response.setHeader("Content-disposition", mode + fname);
		}
		protected void setFlashContenttype() {
			setDownloadContentType(response, filename, "inline", "application/x-shockwave-flash");
		}
		// 下载前控制，可以中止下载，跳转到新的目标页面
		protected boolean beforeDownload() throws Exception{
			return true;
		}
		abstract void setOutput();
		abstract void getInputstream() throws Exception;

	}
	/**
	 * 下载正文（office正文、wps正文）
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	public ModelAndView doDownload4Office(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new FileDownloader(request, response) {
			void setOutput() {
				String type = request.getParameter("type");
				String fileName = FileUploadUtil.getOfficeName(fileId, type);
				setDownloadContentType(response, fileName, "attachment",
						FileUploadUtil.getOfficeHeader(type));
			}

			void getInputstream() throws Exception {
				String comm = request.getParameter("comm");
				if ("byFileId".equals(comm)) {
					V3XFile v3xfile = fileManager.getV3XFile(fileId);
					createDate = v3xfile.getCreateDate();
				} else {
					createDate = Datetimes.parseDate(request
							.getParameter("createDate"));
				}
				in = fileManager.getStandardOfficeInputStream(fileId,
						createDate);
			}
		}.download();
	}
	
	/**
	 * 下载文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	public ModelAndView doDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//filename=java.net.URLEncoder.encode(filename,"UTF-8");
		return new FileDownloader(request, response) {
			protected boolean beforeDownload() throws Exception{
				return true;
			}			
			void setOutput() {
				String swf = request.getParameter("swf");
				if ("true".equals(swf)) {
					setFlashContenttype();
				} else {
					if (!"mobile".equals(request.getParameter("from"))) {
						contentType = "application/x-msdownload";
					}
					setDownloadContentType(response, filename, "attachment", contentType);
				}
			}

			void getInputstream() throws BusinessException {
				String comm = request.getParameter("comm");
				if ("byFileId".equals(comm)) {
					V3XFile v3xfile = getFile();
					createDate = v3xfile.getCreateDate();
					filename = v3xfile.getFilename();
					try {
						filename=java.net.URLEncoder.encode(filename,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e);
					}
				} else if ("mobile".equals(request.getParameter("from"))) {
					V3XFile v3xfile = getFile();
					contentType = v3xfile.getMimeType();
					createDate = v3xfile.getCreateDate();
					filename = FileUploadUtil.escapeFileName(v3xfile);
				} else {
					createDate = Datetimes.parseDate(request.getParameter("createDate"));
					filename = request.getParameter("filename");
					
					boolean isMacOS = false;
					String userAgent = request.getHeader("User-Agent");
					if(userAgent!=null && userAgent.indexOf("Macintosh")>0){
						isMacOS = true;
					}
					String encoding = "GBK";
					if( isMacOS ){
						encoding = "UTF-8";
					}
					try {
						if( filename!=null ){
							filename = new String(filename.getBytes("ISO-8859-1") , encoding );
						}
					} catch (UnsupportedEncodingException e) {
						log.error("UnsupportedEncodingException", e);
					}
				
					filename = FileUtil.getDownloadFileName(request,filename);
					// filename=new
					// String(filename.getBytes("iso8859-1"),"UTF-8");
					// //在浏览器打开和保存使用的编码转换存不同，打开时没有进行编码转换，暂时改用GBK
				}
				String isSystemRecieveForm = request.getParameter("isSystemForm"); // 是否是系统预置的收文单
				String isSystemRedTemplete = request.getParameter("isSystemRedTemplete");// 是否是系统预置的套红模板。
				if ("true".equals(isSystemRecieveForm)) {
					try { // 系统预置收文单去指定目录查找，不去分区查找。
						String formType = request.getParameter("formType");
						String systemFormId = "-2921628185995099164";  //收文
						if("0".equals(formType)){
							systemFormId = "6071519916662539448";
						}else if("1".equals(formType)){
							systemFormId = "-2921628185995099164";
						}
						else{
							systemFormId = "-1766191165740134579";
						}
						in = new FileInputStream(new File(SystemProperties
								.getInstance().getProperty("edoc.folder")
								+ File.separator
								+ "form"
								+ File.separator
								+ systemFormId));
					} catch (Exception e) {
						in = fileManager.getFileInputStream(fileId, createDate);
					}
				} else if ("true".equals(isSystemRedTemplete)) {
					try { // 系统预置示例套红模板去指定目录查找，不去分区查找。
						in = new FileInputStream(new File(SystemProperties
								.getInstance().getProperty("edoc.folder")
								+ File.separator
								+ "template"
								+ File.separator
								+ "-6001972826857714844"));
					} catch (Exception e) {
						in = fileManager.getFileInputStream(fileId, createDate);
					}

				} else {
					in = fileManager.getFileInputStream(fileId, createDate);
				}
			}
		}.download();
	}
	private static void showError(String error, BusinessException e, ServletOutputStream out, HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/html;charset=UTF-8");
		String message = null;
		if(error != null){
			message = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "fileupload.document." + error);
		}
		
		if(e != null){
			message = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", e.getErrorCode(), e.getErrorArgs());
		}
		
		response.addHeader("Rang", "-1"); //不存在
		
		if(message != null){
			try {
				//response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = new PrintWriter(out);
				writer.println("<script type=\"text/javascript\">");
				//writer.println("alert(\"" + Functions.urlEncoder(Functions.escapeJavascript(message)) + "\");");
				writer.println("alert(decodeURI(\"" + Functions.urlEncoder(Functions.escapeJavascript(message)) + "\"));");
				if("blank".equals(request.getParameter("from"))){
					writer.println("window.open(\"/closeIE7.htm\", \"_self\");");
				}
				writer.println("</script>");
				if("mobile".equals(request.getParameter("from"))){//手机端下载
					writer.println(message);
					writer.println("<html><head>");
					writer.println("<meta http-equiv=\"Refresh\" content=\"3;url=mob.do?method=showAffairs\">");
					writer.println("</head></html>");
				}
				writer.flush();
			}
			catch (Throwable e1) {
			}
		}
	}
	
	/**
	 * 删除文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/fileUpload/upload");

		try {
			Long fileId = Long.parseLong(request.getParameter("fileId"));
			Date createDate = Datetimes.parseDatetime(request.getParameter("createDate"));

			this.fileManager.deleteFile(fileId, createDate, true);
		}
		catch (Exception e) {
		}

		return modelAndView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("common/fileUpload/upload");
		String isA8geniusAdded = request.getParameter("isA8geniusAdded");
		modelAndView.addObject("isA8geniusAdded", isA8geniusAdded);
		String str = FileuploadManagerImpl.getMaxSizeStr();		
		modelAndView.addObject("maxSize", str);			
		return modelAndView;
	}
	
	
	public ModelAndView indexforForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("common/fileUpload/uploadForForm");
		String isA8geniusAdded = request.getParameter("isA8geniusAdded");
		modelAndView.addObject("isA8geniusAdded", isA8geniusAdded);
		String str = FileuploadManagerImpl.getMaxSizeStr();		
		modelAndView.addObject("maxSize", str);	
		return modelAndView;
	}

	public ModelAndView indexforFormImage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("common/fileUpload/upLoadImage");
		String isA8geniusAdded = request.getParameter("isA8geniusAdded");
		modelAndView.addObject("isA8geniusAdded", isA8geniusAdded);
		String str = FileuploadManagerImpl.getMaxSizeStr();		
		modelAndView.addObject("maxSize", str);			
		return modelAndView;
	}	
	
	private static class FileFieldComparator implements Comparator<String>, java.io.Serializable {
		private static final long serialVersionUID = -1350845417478340152L;

		public int compare(String o1, String o2) {
			try {
				return new Integer(o1.substring(4)).compareTo(new Integer(o2.substring(4)));
			}
			catch (Exception e) {
				return o1.compareTo(o2);
			}
		}
		
	}
}