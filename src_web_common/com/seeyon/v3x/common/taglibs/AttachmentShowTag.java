/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.taglibs.util.ExpressionUtil;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-11-21
 * @deprecated
 */
public class AttachmentShowTag extends BodyTagSupport {

	private static final long serialVersionUID = 1107340485275666298L;

	public static final String TAG_NAME = "attachmentShow";

	public Long subReference;
	public Boolean isAutoExport;
	public String numberDivId;

	public AttachmentShowTag() {
		init();
	}

	public void init() {
		subReference = null;
		isAutoExport = false;
		numberDivId = "";
	}

	/*
	 * function Attachment(id, reference, subReference, category, type,
	 * filename, mimeType, createdate, size, fileUrl, description)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
			try {
				JspWriter out = pageContext.getOut();
				
				out.print("<div class=\"attachment-single\"");
				
				if(isAutoExport){
					out.print(" onmouseover=\"exportAttachment(this)\"");
				}
				out.println(">");
				
				out.print("<script>");
				out.print("showAttachment('" + this.subReference + "', "
						+ Constants.ATTACHMENT_TYPE.FILE.ordinal() + ", '" + numberDivId + "');");
				out.println("</script>");				
				out.println("</div>");
			}
			catch (IOException e) {
				throw new JspTagException(e.toString(), e);
			}
		
		init();

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public void release() {
		init();
		super.release();
	}
	
	public void setSubReference(String subReference) {
		Long _data = ExpressionUtil.evaluateExpression(TAG_NAME, this,
				pageContext, "subReference", subReference, Long.class);

		this.subReference = _data;
	}

	public void setAutoExport(Boolean isAutoExport) {
		this.isAutoExport = isAutoExport;
	}

	public void setNumberDivId(String numberDivId) {
		this.numberDivId = numberDivId;
	}

}
