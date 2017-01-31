package com.seeyon.v3x.common.taglibs.support;

import static com.seeyon.v3x.common.constants.Constants.EDITOR_RTE_BAR_TYPE_BASIC;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL;
import static com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD;
import static com.seeyon.v3x.common.constants.Constants.OFFICE_EDIT_TYPE_1_0;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;

import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.taglibs.util.Constants;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-18
 */
public abstract class EditorSupport extends BodyTagSupport {

	public static final String TAG_NAME = "editor";
	
	public static final long IMAGE_MAX_SIZE = 1048576;

	protected String type;

	protected String content;
	
	protected String contentName;

	protected String barType;

	protected String htmlId;

	protected String createDate;

	protected int category;
	
	protected String editType;
	
	protected String summaryId = null ;
	
	protected boolean originalNeedClone = false;
	
	protected boolean isNew = false;
	
	public EditorSupport() {
		init();
	}

	private void init() {
		type = EDITOR_TYPE_HTML;
		barType = EDITOR_RTE_BAR_TYPE_BASIC;
		htmlId = createDate = "";
		content = null;
		contentName="";
		category = 0;
		editType = OFFICE_EDIT_TYPE_1_0;
		originalNeedClone = false;
		isNew = false;
	}

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		if (type == null || "".equals(type)) {
			type = EDITOR_TYPE_HTML;
		}

		JspWriter out = pageContext.getOut();
		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());

		String basePath = Strings.getBaseContext(request);

		try {
			Long fileId = null;
			boolean needReadFile = false;
			if ((type.equals(EDITOR_TYPE_OFFICE_WORD) || type.equals(EDITOR_TYPE_OFFICE_EXCEL)
					|| type.equals(EDITOR_TYPE_WPS_WORD) || type.equals(EDITOR_TYPE_WPS_EXCEL)
					||type.equals(EDITOR_TYPE_PDF)
			) && NumberUtils.isNumber(content)){
				fileId = new Long(content);
				content = "";
				needReadFile = true;
			}
			else{
				fileId = UUIDLong.longUUID();
			}
			
			if(content == null){
				content = "";
			}
			
			if(!EDITOR_TYPE_FORM.equals(type))
				content = StringEscapeUtils.escapeHtml(content);
			
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

			if(!EDITOR_TYPE_FORM.equals(type)){
				// HTML
				Boolean f = (Boolean)(BrowserFlag.HtmlEditer.getFlag(CurrentUser.get()));
				if(f){
					// 自定义HTML正文编辑器字体
					String costomFontNames = SystemProperties.getInstance().getProperty("editor.FontNames", "");
					if (Strings.isNotEmpty(costomFontNames) && !costomFontNames.endsWith(";")) {
						costomFontNames += ";";
					}
					out.println("<script>var costomFontNames = '" + costomFontNames + "';</script>");
					
					out.println(Constants.getString("editor.RTE.html", basePath, content, 
							LocaleContext.getLanguage(pageContext.getRequest()), barType,
							htmlId, ImageUploadURL, FlashUploadURL, Strings.formatFileSize(maxSize, false),Functions.resSuffix()));
				}else{
					out.println(Constants.getString("editor.RTE.ipad", basePath, content, 
							LocaleContext.getLanguage(pageContext.getRequest()), barType,
							htmlId, ImageUploadURL, FlashUploadURL, Strings.formatFileSize(maxSize, false)));
				}
				// Office
				String originalFileId = ""; //需要复制office正文文件
				String originalCreateDate = ""; //需要复制office正文文件
				if(originalNeedClone){
					originalFileId = String.valueOf(fileId);
					originalCreateDate = createDate;
					
					fileId = UUIDLong.longUUID();
					createDate = com.seeyon.v3x.util.Datetimes.formatDatetime(new Date());
					//同时复制印章
					//插入印章记录用于印章校验
					if(category==ApplicationCategoryEnum.edoc.getKey())
					{
						SignetManager sm=(SignetManager)ApplicationContextHolder.getBean("signetManager");
						sm.insertSignet(Long.parseLong(originalFileId), fileId);
					}
				}
							
				String userName=CurrentUser.get().getName();
				
				String officeOcxUploadMaxSize = SystemProperties.getInstance().getProperty("officeFile.maxSize");
				
				  DBstep.iMsgServer2000 MsgObj=new DBstep.iMsgServer2000();      //创建DBPacket对象
				  String fIdEncode = MsgObj.EncodeBase64(fileId.toString());  
				  String fNameEncode = MsgObj.EncodeBase64(fileId.toString() + ".doc");
				  String uNameEncode = MsgObj.EncodeBase64(userName);
				  String cDateEncode = MsgObj.EncodeBase64(createDate);
				  String isRetainedTraces = SystemProperties.getInstance().getProperty("officeSaveLocal.isRetainedTraces");
				out.println(Constants.getString("editor.office.html", basePath,
						fileId.toString(), createDate, category, editType,
						DBstep.iMsgServer2000.Version(), originalFileId, originalCreateDate, needReadFile, Strings.escapeJavascript(userName), 
						0,officeOcxUploadMaxSize, fIdEncode, fNameEncode, uNameEncode, cDateEncode,DBstep.iMsgServer2000.Version("iWebPdf"),type,Functions.resSuffix(),isRetainedTraces));
	

//				  out.println("<script>var fIdEncode='"+fIdEncode
//						  	+"';\n var fNameEncode='"+fNameEncode
//						  	+"';\n var uNameEncode='"+uNameEncode
//						  	+"';\n var cDateEncode='"+cDateEncode
//						  	+"';</script>");
				out.println("<script>showEditor('" + type + "');</script>");
			}
			else{
				Map<String, String> systemValue = FormHelper.getFormSystemValue();
				out.println(Constants.getString("editor.form.html", basePath, content,Functions.resSuffix()));
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
			
			out.println("<div style='display:none'>");
			out.println("<input type='hidden' name='bodyType' id='bodyType' value='" + type + "'>");
			out.println("<input type=\"hidden\" name=\"bodyCreateDate\" id=\"bodyCreateDate\" value=\"" + createDate + "\">");
			out.println("<input id=\"contentNameId\" type=\"hidden\" name=\"contentName\" value=\"" + contentName + "\">");
			out.println("<input id=\"_a8_no_cache\" type=\"hidden\" name=\"_a8_no_cache\" value=\"\">");
			out.println("</div>");
			out.println("<script>var isNew = "+isNew+";</script>");
			out.println(Constants.getString("editor.onbeforeunload.js"));
		}
		catch (IOException ioe) {
			throw new JspTagException(ioe.toString(), ioe);
		}

		init();

		return super.doEndTag();
	}

	@Override
	public void release() {
		init();
		super.release();
	}

}