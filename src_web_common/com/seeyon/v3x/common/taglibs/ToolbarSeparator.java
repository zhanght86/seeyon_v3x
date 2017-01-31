/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import static com.seeyon.v3x.common.taglibs.util.Constants.getString;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-31
 * @deprecated
 */
public class ToolbarSeparator extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7078565530054250869L;

	@Override
	public int doEndTag() throws JspException {
		Tag t = findAncestorWithClass(this, ToolbarTag.class);
		if (t == null) {
			throw new TagStructureException("separator", "toolbar");
		}

		ToolbarTag parent = (ToolbarTag) t;

		parent.addButton(getString("toolbar.separator.html"));

		return super.doEndTag();
	}

}
