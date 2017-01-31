package com.seeyon.v3x.common.taglibs;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.PropertiesUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.common.taglibs.util.Constants;

public class WebBarCodeTag extends BodyTagSupport {
	
	private String manufacturer;
	private String readerId;
	private String writerId;
	private String basePath;
	private String licenseFile;    //授权文件在上下文中路径，如:/common/barCode/iWebBarcode.lic
	private String scriptFile;     //javascript文件在上下文中路径，如：/common/barCode/js/barCode.js
	private String readerCallBack;  //reader回调的js方法
	private String resSuffix;
	private Properties prop;
	
	public String getReaderCallBack() {
		return readerCallBack;
	}

	public void setReaderCallBack(String readerCallBack) {
		this.readerCallBack = readerCallBack;
	}

	public String getLicenseFile() {
		return licenseFile;
	}

	public void setLicenseFile(String licenseFile) {
		this.licenseFile = licenseFile;
	}

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}

	public WebBarCodeTag() {
		init();
	}
	
	public void init() {
		if(Strings.isBlank(manufacturer)) {
			manufacturer = "jinge";
		}
		resSuffix = Functions.resSuffix();
		if(Strings.isBlank(licenseFile)) {
			licenseFile = "/common/barCode/iWebBarcode.lic";
		}
		if(Strings.isBlank(scriptFile)) {
			scriptFile = "/common/barCode/js/barCode.js" + resSuffix;
		}
	}
	
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		basePath = Strings.getBaseContext(request);
		prop = PropertiesUtil.getFromServletContext(licenseFile, pageContext.getServletContext());
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		//金格二维码
		if("jinge".equals(manufacturer)) {
			StringBuilder builder = new StringBuilder();
			builder.append(this.loadLicense());
			if(Strings.isNotBlank(readerId)) {
				builder.append(this.initActiveXReader());
				builder.append("<script language=\"javascript\" event=\"OnReceiveMsg()\" for=\""+readerId+"\">");
				builder.append(readerCallBack + "("+readerId+");");
				builder.append("</script>");
			}
			if(Strings.isNotBlank(writerId)) {
				builder.append(this.initActiveXWriter());
				builder.append("<script>");
				builder.append(writerId + ".CopyRight=CopyRight;");
				builder.append("</script>");
			}
			builder.append("<script language=\"javascript\" src =\"" + this.basePath + this.scriptFile + this.resSuffix + "\"></script>");
			JspWriter out = pageContext.getOut();
			User user = CurrentUser.get();
			try {
				OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
				V3xOrgDepartment department = orgManager.getDepartmentById(user.getDepartmentId());
				String departmentName = department==null?"":department.getName();
				out.println("<script>");
				out.println("var barCodeDepartment = '" + departmentName + "';");
				out.println("</script>");
				out.println(builder.toString());
			}catch (Exception e) {
				throw new JspTagException(e.toString(), e);
			}
		}
		return EVAL_PAGE;
	}
	
	private String initActiveXReader() {
		/*StringBuilder builder = new StringBuilder("var strObj=\"<object id=\"" + readerId + "\" width=\"0\" height=\"0\" classid=\"CLSID:AD650675-9B4A-43D2-A8CA-F49B00A9BD92\" codeBase=\"" + basePath + "/common/barCode/PDF417Reader.ocx#version=\" + ReaderVer + \"\"></object>\";");
		builder.append("document.write(strObj);");
		return builder.toString();*/
		return Constants.getString("barCode.reader.html", readerId,basePath,prop.getProperty("ReaderVer"));
	}
	
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	public String getWriterId() {
		return writerId;
	}

	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}

	private String initActiveXWriter() {
		/*StringBuilder builder = new StringBuilder();
		builder.append("var strObj = \"<object id=\"" + writerId + "\" width=\"0\" height=\"0\" classid=\"CLSID:8AA64ECD-DFCB-4B88-A2B0-6A5C465D3F15\" codebase=\"" + basePath + "/common/barCode/PDF417Manager.dll#version=\"+ManagerVer+\"\" ></object>\";");
		builder.append("document.write(strObj);");
		return builder.toString();*/
		return Constants.getString("barCode.writer.html", writerId,basePath,prop.getProperty("ManagerVer"));
	}
	
	private String loadLicense() {
		return "<script language=\"javascript\" type=\"text/javascript\" charset=\"UTF-8\" src=\"" + basePath + licenseFile + "\"></script>";
	}
}
