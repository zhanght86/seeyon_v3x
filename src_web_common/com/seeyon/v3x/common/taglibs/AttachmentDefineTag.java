/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author Administrator
 * 
 */
public class AttachmentDefineTag extends BodyTagSupport {
	public static final String TAG_NAME = "attachmentDefine";

	private List<Attachment> attachments;

	private static final long serialVersionUID = 7467222936911100044L;

	public AttachmentDefineTag() {
		init();
	}

	public void init() {
		attachments = null;
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
				
				String url = LinkTag.calculateURL("RENDER", "/fileUpload.do", pageContext);
				
				out.println("<script>");
				out.println("<!--");
				out.println("var theToShowAttachments = new ArrayList();");
				out.println("var downloadURL = \"" + url + "\";");
				String fileUrl = "";
				if (attachments != null && !attachments.isEmpty()) {
					for (Attachment att : attachments) {
						String genesisIdStr = att.getGenesisId() != null? String.valueOf(att.getGenesisId()) : "";
						if(att.getFileUrl() == null){
							fileUrl = genesisIdStr;
						}
						else{
							fileUrl = String.valueOf(att.getFileUrl());
						}
						out.print("theToShowAttachments.add(new Attachment(");
						out.print("'" + att.getId() + "',");
						out.print("'" + att.getReference() + "',");
						out.print("'" + att.getSubReference() + "',");
						out.print("'" + att.getCategory() + "',");
						out.print("'" + att.getType() + "',");
						out.print("'" + Strings.escapeJavascript(att.getFilename()) + "',");
						out.print("'" + att.getMimeType() + "',");
						out.print("'" + Datetimes.formatDatetime(att.getCreatedate()) + "',");
						out.print("'" + att.getSize() + "',");
						out.print("'" + fileUrl + "',");
						out.print("'" + genesisIdStr + "',");
						out.print("null,");
						out.print("'" + att.getExtension() + "',");
						out.print("'" + att.getIcon() + "',");
						out.print("true,");
						out.print("'" + OfficeTransHelper.isOfficeTran() + "'");
						out.println("));");
					}
				}
				out.println("//-->");
				out.println("</script>");
				
				out.println("<div style=\"display:none;\">");
				out.println("<iframe name=\"downloadFileFrame\" id=\"downloadFileFrame\" frameborder=\"0\" width=\"0\" height=\"0\"></iframe>");
				out.println("</div>\n");
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

	public void setAttachments(List<Attachment> attachments) {	
		this.attachments = attachments;
	}
}
