package com.seeyon.v3x.common.taglibs;

import com.seeyon.v3x.common.taglibs.support.TableColumnSupport;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-9
 */
public class TableColumnTag extends TableColumnSupport {

	private static final long serialVersionUID = -9170338447635614241L;

	public TableColumnTag() {
		super();
	}

	public void setAlt(String _alt) {
		super.altSpecified = true;
		this.alt = _alt;
	}

	public void setLabel(String _label) {
		super.label = _label;
	}

	public void setValue(String _value) {
		this.value = _value;
	}

	public void setType(String _type) {
		this.type = _type;
	}

	public void setWidth(String _width) {
		this.width = _width;
	}

	public void setAlign(String _align) {
		this.align = _align;
	}

	public void setHref(String _href) {
		this.href = _href;
	}

	public void setOnClick(String _onClick) {
		this.onClick = _onClick;
	}

	public void setOnDblClick(String _onDblClick) {
		this.onDblClick = _onDblClick;
	}

	public void setOnmouseover(String _onmouseover) {
		this.onmouseover = _onmouseover;
	}

	public void setOnmouseout(String _onmouseout) {
		this.onmouseout = _onmouseout;
	}

	public void setMaxLength(int _maxLength) {
		this.maxLength = _maxLength;
	}

	public void setEscapeHtml(boolean _escapeHtml) {
		this.escapeHtml = _escapeHtml;
	}

	public void setSymbol(String _symbol) {
		this.symbol = _symbol;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public void setHasAttachments(Boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	public void setImportantLevel(Integer importantLevel) {
		this.importantLevel = importantLevel;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setNowarp(boolean nowarp) {
		this.nowarp = nowarp;
	}
	
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setFlowState(Integer flowState){
		this.flowState = flowState;
	}
	
	public boolean isWidthFixed() {
		return super.widthFixed;
	}

	public void setWidthFixed(boolean widthFixed) {
		super.widthFixed = widthFixed;
	}

	/**
	 * 扩展图标，多个图标请用","分开，如：
	 * 
	 * <pre>
	 * <code>
	 *  &lt;c:url value=&quot;/apps_res/col/a.gif&quot; /&gt;,&lt;c:url value=&quot;/apps_res/col/b.gif&quot; /&gt;
	 * </code>
	 * </pre>
	 * 
	 * @param extIcons
	 */
	public void setExtIcons(String extIcons) {
		if(Strings.isNotBlank(extIcons)){
			this.extIcons = extIcons.split(",");
		}
	}
	
	public void setRelationAuthorized(boolean relationAuthorized) {
		this.relationAuthorized = relationAuthorized;
	}
}
