package com.seeyon.v3x.common.taglibs.table;

import static com.seeyon.v3x.common.taglibs.util.ResourceUtil.getNodeAttribute;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-5
 */
public class Cell implements Serializable {

	private static final long serialVersionUID = 8005602448043986032L;

	/**
	 * content of the cell.
	 */
	private String content;

	private String align;

	private String href;

	private String onclick;

	private String onDblClick;
	
	private String onmouseover;
	
	private String onmouseout;

	private String alt;

	private String className;

	private String width;

	private String target;
	
	private boolean nowarp;

	public String getTarget() {
		return getNodeAttribute("target", target);
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getClassName() {
		return getNodeAttribute("class", className);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAlign() {
		return getNodeAttribute("align", align);
	}

	public String getStyleAlign() {
		return align;
	}
	
	public void setAlign(String align) {
		this.align = align;
	}

	public String getHref() {
		return getNodeAttribute("href", href);
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getOnclick() {
		return getNodeAttribute("onclick", onclick);
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getContent() {
		if (content == null || content.trim().equals("")) {
			return "&nbsp;";
		}

		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAlt() {
		if(StringUtils.isBlank(alt)){
			return "";
		}
		else{
			return " title=\"" + toHTML(alt) + "\"";
		}
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getOnDblClick() {
		return getNodeAttribute("onDblClick", onDblClick);
	}

	public void setOnDblClick(String onDblClick) {
		this.onDblClick = onDblClick;
	}

	public String getWidth() {
		return getNodeAttribute("width", width);
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getOnmouseout() {
		return getNodeAttribute("onmouseout", onmouseout);
	}

	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}

	public String getOnmouseover() {
		return getNodeAttribute("onmouseover", onmouseover);
	}

	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	public String getNowarp() {
		if(nowarp){
			return getNodeAttribute("nowrap", "nowrap");
		}
		
		return "";
	}

	public void setNowarp(boolean nowarp) {
		this.nowarp = nowarp;
	}
	
	public static String toHTML(String text) {
		if (text == null || text.equals("")) {
			return "";
		}

		char content[] = new char[text.length()];
		text.getChars(0, text.length(), content, 0);
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '\'':
				result.append("&#039;");
				break;
			case '"':
				result.append("&quot;");
				break;
			default:
				result.append(content[i]);
			}
		}

		return result.toString();
	}
}