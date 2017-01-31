package com.seeyon.v3x.common.taglibs.support;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.TagStructureException;
import com.seeyon.v3x.common.taglibs.table.Cell;
import com.seeyon.v3x.common.taglibs.table.Header;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-1
 */
public abstract class TableColumnSupport extends BodyTagSupport {
	
	private static Log log = LogFactory.getLog(TableColumnSupport.class);

	public static final String TAG_NAME = "column";

	protected String label;

	protected String value;

	protected String property;

	protected String width;

	protected String type;

	protected String align;

	protected boolean altSpecified;
	
	protected String alt;

	protected String href;

	protected String onClick;

	protected String onDblClick;
	
	protected String onmouseover;
	
	protected String onmouseout;

	protected String className;

	protected int maxLength;

	protected boolean escapeHtml;

	protected String symbol;

	protected String target;

	protected Integer importantLevel;

	protected Boolean hasAttachments;
	
	protected Integer flowState;

	protected String bodyType;
	
	protected boolean read;

	protected boolean nowarp;
	
	protected String orderBy;

	protected String[] extIcons;

	private TableSupport parent;
	
	protected boolean widthFixed;
	
	protected Boolean relationAuthorized;
	
	public TableColumnSupport() {
		super();
		init();
	}

	private void init() {
		label = null;
		value = null;
		width = "";
		type = "";
		alt = "";
		align = "";
		parent = null;
		href = "";
		onClick = "";
		className = "sort";
		onDblClick = "";
		maxLength = 0;
		escapeHtml = true;
		symbol = null;
		property = null;
		target = null;
		importantLevel = null;
		hasAttachments = null;
		bodyType = null;
		read = true;
		onmouseover = null;
		onmouseout = null;
		nowarp = false;
		altSpecified = false;
		extIcons = null;
		orderBy = null;
		flowState = null;
		relationAuthorized = false;
	}

	public int doStartTag() throws JspException {
		Tag t = findAncestorWithClass(this, TableSupport.class);
		if (t == null) {
			throw new TagStructureException("column", "table");
		}

		parent = (TableSupport) t;

		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		// 第一次迭代，记录header信息
		if (parent.isFirstIteration()) {
			if(parent.showHeader){
				String _label = null;
				if(parent.bundleAttrValue != null){
					_label = ResourceBundleUtil.getString(parent.bundleAttrValue, label);
				}

				if(parent.bundleAttrValue == null || _label == null){
					_label = ResourceBundleUtil.getString(pageContext, label);
				}
				
				if(_label != null){
					label = _label;
				}
			}

			Header header = new Header();

			header.setAlt(alt);
			header.setLabel(label);
			header.setType(type);
			header.setAlign(align);
			header.setWidth(width);
			header.setClassName("sort");
			header.setNowarp(nowarp);
			header.setOrderBy(orderBy);
			header.setWidthFixed(widthFixed==true?widthFixed:false);

			parent.addHeader(header);
		}

		if (parent.getCurrentRow() == null) {
			return EVAL_PAGE;
		}

		Cell cell = new Cell();

		// 单元格内容，优先级别： property > value > bodyContent
		String cellContent = null;
		boolean needLimit = false;
		if (property != null) {
			try {
				Object obj = PropertyUtils.getProperty(parent.getCurrentRow().getObject(), property);
				if (obj != null) {
					cellContent = obj.toString();					
					needLimit = true;
				}
			}
			catch (Exception e) {
				log.error("读取Bean属性值", e);
			}
		}
		else if (cellContent == null && value != null) {
			cellContent = value.trim();
			needLimit = true;
		}
		else if (cellContent == null && bodyContent != null) {
			cellContent = bodyContent.getString().trim();
		}
		
		String importantLevelHtml = "";
		String flowStateHtml = "";
		StringBuilder extHTML = new StringBuilder();
		boolean hasIcon = false;
		
		//流程标示
		if (flowState != null && (flowState == 1 || flowState ==3)) {
			maxLength -= 2;
			hasIcon = true;
			flowStateHtml = "<span class='inline-block flowState_" + flowState + "'></span>";
		}
		// 重要程度图标
		if (importantLevel != null && importantLevel > 1) {
			maxLength -= 2;
			hasIcon = true;
			importantLevelHtml = "<span class='inline-block importance_" + importantLevel + "'></span>";
		}
		
		// 附件图标		
		if (BooleanUtils.isTrue(hasAttachments)) {
			maxLength -= 2;
			hasIcon = true;
			extHTML.append("<span class='inline-block attachment_table_").append(hasAttachments).append("'></span>");
		}
		// 关联授权图标		
		if (BooleanUtils.isTrue(relationAuthorized)) {
			maxLength -= 2;
			hasIcon = true;
			extHTML.append("<span class='inline-block relationAuthority_").append(relationAuthorized).append("'></span>");
		}
		// 正文类型图标
		if (bodyType != null && !bodyType.equals(Constants.EDITOR_TYPE_HTML)) {
			maxLength -= 2;
			hasIcon = true;
			extHTML.append("<span class='inline-block bodyType_").append(bodyType).append("'></span>");
		}
		
		if(extIcons != null){
			hasIcon = true;
			for (String extIcon : extIcons) {
				maxLength -= 2;
				extHTML.append("<span class='inline-block' style='background: url(").append(extIcon).append(") no-repeat;width: 16px;height: 16px;'></span>");
			}
		}
		StringBuilder mainContent = new StringBuilder();
		if(hasIcon){
			mainContent.append("<span>").append((needLimit ? limitString(cellContent) : cellContent)).append("</span>");
		}else{
			mainContent.append((needLimit ? limitString(cellContent) : cellContent));
		}
		cellContent = flowStateHtml+importantLevelHtml + mainContent.toString() + extHTML.toString();

		cell.setContent(cellContent);
		cell.setAlign(align);
		cell.setHref(href);
		cell.setOnclick(onClick);
		cell.setOnDblClick(onDblClick);
		cell.setOnmouseover(onmouseover);
		cell.setOnmouseout(onmouseout);
		cell.setAlt(alt);
		cell.setClassName(className + (!this.read ? " no-read" : ""));
		cell.setWidth(width);
		cell.setTarget(target);
		cell.setNowarp(nowarp);

		parent.addCell(cell);

		init();
		
		return EVAL_PAGE;
	}

	public void release() {
		this.init();
		super.release();
	}

	/**
	 * 按照maxLength对value进行裁减
	 * 
	 * @param value
	 * @return
	 */
	private String limitString(String value) {
		if (maxLength > 0 && value != null) {
			if (symbol == null) {
				symbol = "...";
			}
			
			if(!altSpecified){
				alt = value;
			}

			value = Strings.getLimitLengthString(value, maxLength, symbol);
		}
		
		if(escapeHtml){
			value = Strings.toHTML(value);
		}

		return value;
	}

}
