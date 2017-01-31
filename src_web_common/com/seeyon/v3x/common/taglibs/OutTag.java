/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.tag.common.core.Util;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-11
 */
public class OutTag extends BodyTagSupport {
	private static final long serialVersionUID = -5136966056581011486L;

	private Object value; // tag attribute

	private String def; // tag attribute

	private boolean escapeXml; // tag attribute

	private boolean escapeHtml;

	private boolean escapeJavaScript;

	private boolean needBody; // non-space body needed?

	// *********************************************************************
	// Constructor

	public OutTag() {
		super();
		init();
	}

	// *********************************************************************
	// Tag logic

	// evaluates 'value' and determines if the body should be evaluted
	public int doStartTag() throws JspException {
		needBody = false; // reset state related to 'default'
		this.bodyContent = null; // clean-up body (just in case container is
		// pooling tag handlers)

		try {
			// print value if available; otherwise, try 'default'
			if (value != null) {
				out(pageContext, escapeXml, escapeHtml, escapeJavaScript, value);
				return SKIP_BODY;
			}
			else {
				// if we don't have a 'default' attribute, just go to the body
				if (def == null) {
					needBody = true;
					return EVAL_BODY_BUFFERED;
				}

				// if we do have 'default', print it
				if (def != null) {
					// good 'default'
					out(pageContext, escapeXml, escapeHtml, escapeJavaScript,
							def);
				}
				return SKIP_BODY;
			}
		}
		catch (IOException ex) {
			throw new JspException(ex.toString(), ex);
		}
	}

	// prints the body if necessary; reports errors
	public int doEndTag() throws JspException {
		try {
			if (!needBody)
				return EVAL_PAGE; // nothing more to do

			// trim and print out the body
			if (bodyContent != null && bodyContent.getString() != null)
				out(pageContext, escapeXml, escapeHtml, escapeJavaScript,
						bodyContent.getString().trim());
			return EVAL_PAGE;
		}
		catch (IOException ex) {
			throw new JspException(ex.toString(), ex);
		}
	}

	// *********************************************************************
	// Public utility methods

	/**
	 * Outputs <tt>text</tt> to <tt>pageContext</tt>'s current JspWriter.
	 * If <tt>escapeXml</tt> is true, performs the following substring
	 * replacements (to facilitate output to XML/HTML pages): & -> &amp; < ->
	 * &lt; > -> &gt; " -> &#034; ' -> &#039;
	 * 
	 * See also Util.escapeXml().
	 */
	public static void out(PageContext pageContext, boolean escapeXml,
			boolean escapeHtml, boolean escapeJavaScript, Object obj)
			throws IOException {
		JspWriter w = pageContext.getOut();
		if (escapeXml) {
			// escape XML chars
			if (obj instanceof Reader) {
				Reader reader = (Reader) obj;
				char[] buf = new char[4096];
				int count;
				while ((count = reader.read(buf, 0, 4096)) != -1) {
					writeEscapedXml(buf, count, w);
				}
			}
			else {
				String text = obj.toString();
				writeEscapedXml(text.toCharArray(), text.length(), w);
			}
		}
		else if (escapeHtml) {
			String str = obj.toString();
			str = com.seeyon.v3x.util.Strings.toHTML(str);
			w.write(str);
		}
		else if (escapeJavaScript) {
			String str = obj.toString();
			str = escapeJavaScript(str);
			w.write(str);
		}
		else {
			// write chars as is
			if (obj instanceof Reader) {
				Reader reader = (Reader) obj;
				char[] buf = new char[4096];
				int count;
				while ((count = reader.read(buf, 0, 4096)) != -1) {
					w.write(buf, 0, count);
				}
			}
			else {
				w.write(obj.toString());
			}
		}
	}

	/**
	 * 
	 * Optimized to create no extra objects and write directly to the JspWriter
	 * using blocks of escaped and unescaped characters
	 * 
	 */
	private static void writeEscapedXml(char[] buffer, int length, JspWriter w)
			throws IOException {
		int start = 0;

		for (int i = 0; i < length; i++) {
			char c = buffer[i];
			if (c <= Util.HIGHEST_SPECIAL) {
				char[] escaped = Util.specialCharactersRepresentation[c];
				if (escaped != null) {
					// add unescaped portion
					if (start < i) {
						w.write(buffer, start, i - start);
					}
					// add escaped xml
					w.write(escaped);
					start = i + 1;
				}
			}
		}
		// add rest of unescaped portion
		if (start < length) {
			w.write(buffer, start, length - start);
		}
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		super.release();
		init();
	}

	// *********************************************************************
	// Accessor methods

	public void setValue(Object value_) {
//		try {
//			value = ExpressionUtil.evalNotNull("out", "value", value_,
//					Object.class, this, pageContext);
//		}
//		catch (Exception ex) {
//		}
		this.value = value_;
	}

	public void setDefault(String default_) {
//		try {
//			def = (String) ExpressionUtil.evalNotNull("out", "default",
//					default_, String.class, this, pageContext);
//		}
//		catch (Exception ex) {
//		}
		
		this.def = default_;
	}

	public void setEscapeHtml(boolean escapeHtml) {
		this.escapeHtml = escapeHtml;
	}

	public void setEscapeJavaScript(boolean escapeJavaScript) {
		this.escapeJavaScript = escapeJavaScript;
	}

	public void setEscapeXml(boolean escapeXml) {
		this.escapeXml = escapeXml;
	}

	/**
	 * 
	 * 
	 */
	private void init() {
		value = def = null;
		escapeHtml = escapeJavaScript = escapeXml = false;
	}


}
