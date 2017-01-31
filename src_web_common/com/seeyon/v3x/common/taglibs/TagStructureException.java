/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import javax.servlet.jsp.JspTagException;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-2
 */
public class TagStructureException extends JspTagException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3865887453695937279L;

	/**
	 * @param arg0
	 */
	public TagStructureException(String arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	public TagStructureException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TagStructureException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public TagStructureException(Throwable arg0) {
		super(arg0);
	}

	public TagStructureException(String subTagName, String supTagName) {
		this("Tag '" + subTagName + "' must be contained within Tag '"
				+ supTagName + "'");
	}
}
