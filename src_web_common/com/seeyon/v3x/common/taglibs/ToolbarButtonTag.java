package com.seeyon.v3x.common.taglibs;

import static com.seeyon.v3x.common.i18n.ResourceBundleUtil.getString;
import com.seeyon.v3x.common.taglibs.util.Constants;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import com.seeyon.v3x.common.taglibs.util.ExpressionUtil;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-31
 * @deprecated
 */
public class ToolbarButtonTag extends BodyTagSupport {

	public static final String TAG_NAME = "button";

	private String label;

	private String htmlId;

	private String alt;

	private Object onclick;

	private String icon;

	private Collection<String> items;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7030499455845201698L;

	public ToolbarButtonTag() {
		super();
		items = new ArrayList<String>();
	}

	public void init() {
		label = "";
		htmlId = "";
		alt = "";
		onclick = "";
		icon = "/common/images/toolbar/defaultICON.gif";
	}

	public void addItem(String item) {
		items.add(item);
	}

	@Override
	public int doStartTag() throws JspException {
		items.clear();
		evaluateExpressions();
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		Tag t = findAncestorWithClass(this, ToolbarTag.class);
		if (t == null) {
			throw new TagStructureException("button", "toolbar");
		}

		ToolbarTag parent = (ToolbarTag) t;

		StringBuffer sb = new StringBuffer();

		label = escapeHtml(getString(pageContext, label));
		alt = escapeHtml(getString(pageContext, alt));

		if (!items.isEmpty()) {
			sb.append("\r\n");
			String itemDivId = htmlId + "-item";

			sb.append("<div ").append("id=\"" + itemDivId + "\" ").append(
					"style=\"display:none\" class='itemsDiv'>").append("\r\n");

			for (String item : items) {
				sb.append(item).append("\r\n");
			}
			sb.append("</div>");

			onclick = "showToolbarButtonItem(this, '" + itemDivId + "', "
					+ items.size() + ")";
		}

		icon = org.apache.taglibs.standard.tag.common.core.UrlSupport
				.resolveUrl(icon, null, pageContext);

		String input = Constants.getString("toolbar.button.html", htmlId, alt,
				onclick, label + sb.toString(), icon);

		parent.addButton(input);

		return EVAL_PAGE;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOnclick(Object onclick) {
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

	private void evaluateExpressions() {
		onclick = ExpressionUtil.evaluateExpression(TAG_NAME, this,
				pageContext, "onclick", onclick, String.class);
	}

}
