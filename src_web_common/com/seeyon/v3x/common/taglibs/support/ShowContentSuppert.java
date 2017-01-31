/**
 * 
 */
package com.seeyon.v3x.common.taglibs.support;

import static com.seeyon.v3x.common.constants.Constants.EDITOR_RTE_BAR_TYPE_BASIC;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD;
import static com.seeyon.v3x.common.constants.Constants.OFFICE_EDIT_TYPE_0_0;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.math.NumberUtils;

import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants.OFFICS_FILE_TYPE;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.taglibs.util.Constants;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag;
import com.seeyon.v3x.util.Strings;


/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-26
 */
public abstract class ShowContentSuppert extends BodyTagSupport {

	private static final long serialVersionUID = -1967360290677077151L;

	protected String type;
	public static final long IMAGE_MAX_SIZE = 1048576;
	protected String content;
	
	protected String contentName;
	protected String viewMode;
	protected String htmlId;
	
	protected String createDate;
	protected String barType;
	protected int category;
	protected Long lastUpdateTime;
	
	//正文实际的大小，标签传入，取自v3x_file表的fileSize，主要用来判断office插件是否能打开当前正文。
	protected Long officeFileRealSize;

	protected String summaryId = null;
    
	private OfficeTransManager officeTransManager;
	private FileManager fileManager;
	public ShowContentSuppert() {
		init();
	}

	private void init() {
		type = EDITOR_TYPE_HTML;
		htmlId = createDate = content = "";
		contentName="";
		barType = EDITOR_RTE_BAR_TYPE_BASIC;
		category = 0;
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		if (type == null || "".equals(type)) {
			type = EDITOR_TYPE_HTML;
		}

		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
		JspWriter out = pageContext.getOut();
		
		Long maxSize = null;
		String maxSizeStr = SystemProperties.getInstance().getProperty("fileUpload.image.maxSize");
		if(Strings.isNotBlank(maxSizeStr)){
			maxSize = Long.parseLong(maxSizeStr);
		}
		else{
			maxSize = IMAGE_MAX_SIZE;
		}
		String ImageUploadURL = LinkTag.calculateURL("/fileUpload.do?method=processUpload&type=1&applicationCategory=" + category + "&extensions=jpg,gif,jpeg,png&maxSize=" + maxSize, pageContext);
		String FlashUploadURL = LinkTag.calculateURL("/fileUpload.do?method=processUpload&type=1&applicationCategory=" + category + "&extensions=swf,fla&maxSize=" + maxSize, pageContext);;

		String basePath = request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();
		try {
			out.println("<div style='display:none'><input type='hidden' name='bodyType' id='bodyType' value='"
					+ type + "'><input type=\"hidden\" name=\"bodyCreateDate\" value=\"" + createDate + "\"></div>");
			out.println("<input id=\"contentNameId\" type=\"hidden\" name=\"contentName\" value=\"" + contentName + "\">");
			if (type.equals(EDITOR_TYPE_HTML)) {
				//DIV显示只读
				out.println("<div id ='htmlContentDiv'>");
				out.println(Constants.getString("showContent.RTE.html", htmlId, content));
				out.println("</div>");
				out.println("<script type=\"text/javascript\">");
				out.println("var webRoot = \""+basePath+"\"");
				out.println("</script>");
			}
			else if (type.equals(EDITOR_TYPE_OFFICE_WORD) || type.equals(EDITOR_TYPE_OFFICE_EXCEL)
					|| type.equals(EDITOR_TYPE_WPS_WORD) || type.equals(EDITOR_TYPE_WPS_EXCEL)||
					type.equals(EDITOR_TYPE_PDF)){
				User user = CurrentUser.get();
				boolean officeTransformConversion = OfficeTransHelper.isOfficeTran();
				if(officeTransformConversion){
					if(type.equals(EDITOR_TYPE_OFFICE_WORD) || type.equals(EDITOR_TYPE_OFFICE_EXCEL)){
						viewMode = viewMode==null ? "view" : viewMode;
						if("view".equals(viewMode)){
							showOfficeHtmlContent(request,out, user, basePath);
						}else{
							showOfficeContent(out, user, basePath);
						}
					}else{
						showOfficeContent(out, user, basePath);
					}
				}else{
					showOfficeContent(out, user, basePath);
				}
//				}else{
//					String error = ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, "error.info.browser");
//					String download = ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, "addressbook.toolbar.download.label");
//					String text = ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, "common.toolbar.content.label");
//					String html = error + "<a class=\"like-a\" style=\"font-size: 14px;\" href=\"/seeyon/fileUpload.do?method=doDownload4Office&fileId=" + 
//						content + "&createDate=" + createDate + "&type=" + type + "\" target=\"downloadFileFrame\"> " + download + " </a>" + text;
//					out.println(Constants.getString("showContent.RTE.html", htmlId, html));
//				}
			}
			else if(type.equals(EDITOR_TYPE_FORM)){
				Map<String, String> systemValue = FormHelper.getFormSystemValue();
				out.println(Constants.getString("showContent.form.html", basePath, content,Functions.resSuffix()));
				out.println("<script>");
				if(Strings.isNotBlank(summaryId)){
					out.println("var _selfColSummary=\""+summaryId+"\";");
				}
				out.println("var systemValueMap = new Properties();");
				for (String systemValueName : systemValue.keySet()) {
					out.println("systemValueMap.put('" + systemValueName + "','" + Functions.escapeJavascript(systemValue.get(systemValueName)) + "');");
				}
				out.println("templateForm(document.getElementById(\"tarea\").value,document.getElementById(\"scrollDiv\"));");
				out.println("</script>");
			}
		}
		catch (IOException ioe) {
			throw new JspTagException(ioe.toString(), ioe);
		}
		
		init();

		return super.doEndTag();
	}

	private void showOfficeHtmlContent(HttpServletRequest req, JspWriter out, User user, String basePath) throws IOException {
		Long fileId = NumberUtils.toLong(content);
		V3XFile v3XFile = null;
		try {
			v3XFile = getFileManager().getV3XFile(fileId);
		} catch (Exception e) {
			
		}

		if (!OfficeTransHelper.allowTrans(v3XFile)) {
			showOfficeContent(out, user, basePath);
			return;
		}

		String htmlURL = OfficeTransHelper.buildCacheUrl(v3XFile, false);

		final String officeHtml = "<script>trans2Html=true;</script>" + "<iframe style=\"width:100%;height:100%;margin-left:10px;margin-right:10px;\" src=\"" + htmlURL + "\"></iframe>";
		out.println(officeHtml);
		
		// 添加一个这个变量用来页面控制office插件的加载.
		req.setAttribute("isSucessTrans2Html", true);
	}

	private void showOfficeContent(JspWriter out, User user, String basePath)
			throws IOException {
		String userName = user.getName();
		OFFICS_FILE_TYPE fileType = getContentType(type);
		if(lastUpdateTime==null){lastUpdateTime=0L;}
		String officeOcxUploadMaxSize = SystemProperties.getInstance().getProperty("officeFile.maxSize");
		String isRetainedTraces = SystemProperties.getInstance().getProperty("officeSaveLocal.isRetainedTraces");// Word或者Wps正文保存到本地是否保留修改痕迹
		String realSize = officeFileRealSize == null ? "":String.valueOf(officeFileRealSize/1024);
		out.println(Constants.getString("showContent.office.html", basePath,
				content.toString(), createDate, "", OFFICE_EDIT_TYPE_0_0,
				DBstep.iMsgServer2000.Version(), "", true, Strings.escapeJavascript(userName), lastUpdateTime.toString(), 
				officeOcxUploadMaxSize, DBstep.iMsgServer2000.Version("iWebPdf"),realSize,Functions.resSuffix(),isRetainedTraces
				));
		if(type.equals(EDITOR_TYPE_PDF)){
			out.println("<script>showPdfDiv('" + fileType.name() + "');</script>");
		}else{
			out.println("<script>showOfficeDiv('" + fileType.name() + "');</script>");
		}
	}
	
	private OFFICS_FILE_TYPE getContentType(String contentType)
	{
		if(EDITOR_TYPE_OFFICE_WORD.equalsIgnoreCase(contentType)){return OFFICS_FILE_TYPE.doc;}
		if(EDITOR_TYPE_OFFICE_EXCEL.equalsIgnoreCase(contentType)){return OFFICS_FILE_TYPE.xls;}
		if(EDITOR_TYPE_WPS_WORD.equalsIgnoreCase(contentType)){return OFFICS_FILE_TYPE.wps;}
		if(EDITOR_TYPE_WPS_EXCEL.equalsIgnoreCase(contentType)){return OFFICS_FILE_TYPE.et;}		
		if(EDITOR_TYPE_PDF.equalsIgnoreCase(contentType)){return OFFICS_FILE_TYPE.pdf; }
		return OFFICS_FILE_TYPE.doc;		
	}
	private OfficeTransManager getOfficeTransManager(){
		if(officeTransManager==null)
			officeTransManager =(OfficeTransManager) ApplicationContextHolder.getBean("officeTransManager");
		return officeTransManager;
	}
	private FileManager getFileManager(){
		if(fileManager==null)
			fileManager =(FileManager) ApplicationContextHolder.getBean("fileManager");
		return fileManager;
	}
}
