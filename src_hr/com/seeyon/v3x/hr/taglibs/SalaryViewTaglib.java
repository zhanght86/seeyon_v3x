package com.seeyon.v3x.hr.taglibs;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.webmodel.WebProperty;

public class SalaryViewTaglib extends BodyTagSupport {
	
	private static final long serialVersionUID = 7052244567816556768L;

	public static final String TAG_NAME = "salaryViewTag";

	private List<WebProperty> properties;
	private String language;
	private String model;
	private String readonly;
	
	public void setProperties(List<WebProperty> properties) {
		this.properties = properties;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	
	public void init() {
		properties = null;
		language = null;
		model = null;
		readonly = "";
	}
	
	public void release() {
		super.release();
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		if (CollectionUtils.isNotEmpty(properties)) {
			try {
				JspWriter out = pageContext.getOut();
				out.println("<tr>");
				if (model.equals("staff")) {
					out.println("<td class=\"categorySet-head\"><div class=\"categorySet-body border-top\">");
				} else {
					out.println("<td width=\"50%\">");
				}
				out.println("<table width=\"80%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">");
				out.println("<tr><td class=\"bg-gray\"><div class=\"hr-blue\"><strong>");
				if ("en".equals(language)) {
					out.println(properties.get(0).getPageName_en());
				} else {
					out.println(Functions.toHTML(properties.get(0).getPageName_zh()));
				}
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;</strong></div></td><td>&nbsp;</td><td>&nbsp;</td></tr>");
				for (WebProperty webProperty : properties) {
					Long repositoryId = webProperty.getRepository_id();
					out.println("<tr><td class=\"bg-gray\" width=\"25%\" nowrap=\"nowrap\">" +
							"<input type=\"hidden\" name=\"" + repositoryId + "_Type" + "\" value=\"" + webProperty.getPropertyType() + "\" />");
					String propertyName = "";
					if ("en".equals(language)) {
						propertyName = webProperty.getLabelName_en();
					} else {
						propertyName = Functions.toHTML(webProperty.getLabelName_zh());
					}
					
					out.println("<label>" + propertyName + ":</label></td>");
					out.println("<td class=\"new-column\" width=\"50%\">");
					if (webProperty.getPropertyType() == PagePropertyConstant.Page_Property_Integer) {
						String value = webProperty.getF1() != null ? String.valueOf(webProperty.getF1()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\"" + value + "\" inputName=\"" + propertyName + "\" " + readonly + " ");
						if (webProperty.getNot_null().equals("no"))
							out.println("validate=\"notNull,isInteger,maxLength\" maxSize=\"10\" />");
						else
							out.println("validate=\"isInteger,maxLength\" maxSize=\"10\" />");
					} else if (webProperty.getPropertyType() == PagePropertyConstant.Page_Property_Float) {
						String value = webProperty.getF2() != null ? String.valueOf(webProperty.getF2()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\"" + value + "\" inputName=\"" + propertyName + "\" " + readonly + " ");
						if (webProperty.getNot_null().equals("no"))
							out.println("validate=\"notNull,isNumber,maxLength\" decimalDigits=\"2\" maxSize=\"10\" />");
						else
							out.println("validate=\"isNumber,maxLength\" decimalDigits=\"2\" maxSize=\"10\" />");
					} else if (webProperty.getPropertyType() == PagePropertyConstant.Page_Property_Date) {
						String value = webProperty.getF3() != null ? String.valueOf(webProperty.getF3()) : "";
						out.println("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\"" + value + "\" inputName=\"" + propertyName + "\" " + readonly + " readonly=\"true\" onclick=\"whenstart('/seeyon',this,675,640);\" />");
					} else if (webProperty.getPropertyType() == PagePropertyConstant.Page_Property_Varchar) {
						String value = webProperty.getF4() != null ? String.valueOf(webProperty.getF4()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\"" + value + "\" inputName=\"" + propertyName + "\" " + readonly + " ");
						if (webProperty.getNot_null().equals("no"))
							out.println("validate=\"notNull,maxLength\" maxSize=\"40\" />");
						else
							out.println("validate=\"maxLength\" maxSize=\"40\" />");
					} else {
						String value = webProperty.getF5() != null ? String.valueOf(webProperty.getF5()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\"" + value + "\" inputName=\"" + propertyName + "\" " + readonly + " ");
						if (webProperty.getNot_null().equals("no"))
							out.println("validate=\"notNull,maxLength\" maxSize=\"40\" />");
						else
							out.println("validate=\"maxLength\" maxSize=\"40\" />");
					}
					out.println("</td><td>&nbsp;</td></tr>");
				}
				out.println("</table>");
				if (model.equals("staff")) {
					out.println("</div></td>");
				} else {
					out.println("</td>");
				}
				out.println("</tr>");
			} catch (IOException e) {
				throw new JspTagException(e.toString(), e);
			}
		}
		init();
		return super.doEndTag();
	}

}