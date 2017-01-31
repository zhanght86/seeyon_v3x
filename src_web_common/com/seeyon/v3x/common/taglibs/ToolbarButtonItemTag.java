/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import static com.seeyon.v3x.common.i18n.ResourceBundleUtil.getString;
import com.seeyon.v3x.common.taglibs.util.Constants;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-31
 * @deprecated
 */
public class ToolbarButtonItemTag extends BodyTagSupport {

	private String label;

	private String htmlId;

	private String onclick;

	private String alt;

	private String icon;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3424542459405920504L;

	public ToolbarButtonItemTag() {
		super();
	}

	public void init() {
		label = null;
		htmlId = null;
		alt = null;
		onclick = null;
		icon = "/common/images/toolbar/defaultICON.gif";
	}

	@Override
	public int doEndTag() throws JspException {
		Tag t = findAncestorWithClass(this, ToolbarButtonTag.class);
		if (t == null) {
			throw new TagStructureException("buttonItem", "button");
		}

		ToolbarButtonTag parent = (ToolbarButtonTag) t;
		label = getString(pageContext, label);
		alt = getString(pageContext, alt);

		if (org.apache.commons.lang.StringUtils.isNotEmpty(icon)) {
			icon = org.apache.taglibs.standard.tag.common.core.UrlSupport
					.resolveUrl(icon, null, pageContext);
		}

		String input = Constants.getString("toolbar.button.item.html", htmlId,
				onclick, alt, label);

		parent.addItem(input);

		return super.doEndTag();
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void release() {
		init();
		super.release();
	}
}
