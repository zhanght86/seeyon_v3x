/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import java.util.Date;

import javax.servlet.jsp.JspException;

import com.seeyon.v3x.common.taglibs.support.ShowContentSuppert;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-26
 */
public class ShowContentTag extends ShowContentSuppert {

	private static final long serialVersionUID = 2435833357874853151L;
	
	public ShowContentTag() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
//		evaluateExpressions();
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
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	public void setType(String type) {
		this.type = type;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}
	public void setOfficeFileRealSize(Long officeFileRealSize){
		this.officeFileRealSize = officeFileRealSize ;
	}

	public void setSummaryId(String summaryId){
		this.summaryId = summaryId;
	}
	
	public void setCreateDate(Date date) {	
		if(date != null){
			super.lastUpdateTime=date.getTime();
			super.createDate = com.seeyon.v3x.util.Datetimes.formatDatetime(date);
		}
		else
		{
			super.lastUpdateTime=0L;
		}
	}
}
