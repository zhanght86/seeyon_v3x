package com.seeyon.v3x.common.taglibs;

import static com.seeyon.v3x.common.taglibs.util.Constants.getString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-30
 * @deprecated
 */
public class ToolbarTag extends BodyTagSupport {

	private static final long serialVersionUID = 3111340319136382706L;

	private Collection<String> buttons;

	public ToolbarTag() {
		super();
		buttons = new ArrayList<String>();
	}

	public void addButton(String arg) {
		buttons.add(arg);
	}

	public int doStartTag() throws JspException {
		buttons.clear();
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();

		try {
			out.println(getString("div.begin.html", "toolBar"));

			/**
			 * 输出按钮
			 */
			if (!buttons.isEmpty()) {
				for (String button : buttons) {
					out.println(button);
				}
			}
			
//			if(super.getBodyContent()!=null){
//				out.println(super.getBodyContent().getString());
//			}

			out.println(getString("div.end.html"));
		}
		catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}

		return EVAL_BODY_INCLUDE;
	}

	public void release() {
		super.release();
	}

}
