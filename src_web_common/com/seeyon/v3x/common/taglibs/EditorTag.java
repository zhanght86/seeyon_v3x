package com.seeyon.v3x.common.taglibs;

import java.util.Date;

import javax.servlet.jsp.JspException;

import com.seeyon.v3x.common.taglibs.support.EditorSupport;

/**
 * 正文编辑器
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-18
 */
public class EditorTag extends EditorSupport {

	private static final long serialVersionUID = 2310987345274178253L;

	public EditorTag() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		super.release();
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setBarType(String _barType) {
		this.barType = _barType;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public void setCategory(int category) {
		super.category = category;
	}

	public void setCreateDate(Date date) {
		if (date == null) {
			super.isNew = true;
			date = new Date();
		}
		
		super.createDate = com.seeyon.v3x.util.Datetimes.formatDatetime(date);

	}
	
	public void setSummaryId(String summaryId){
		this.summaryId = summaryId ;
	}
	
	public void setEditType(String editType) {
		super.editType = editType;
	}
	
	public void setOriginalNeedClone(boolean originalNeedClone) {
		this.originalNeedClone = originalNeedClone;
	}
}
