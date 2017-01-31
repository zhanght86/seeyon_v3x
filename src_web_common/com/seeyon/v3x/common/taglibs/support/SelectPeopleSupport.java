package com.seeyon.v3x.common.taglibs.support;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.util.Constants;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-29
 */
public abstract class SelectPeopleSupport extends BodyTagSupport {
	public static final String TAG_NAME = "selectPeople";

	protected String label;

	protected String panels;

	protected boolean showMe;

	protected String selectType;

	protected String memberId;

	protected String departmentId;

	protected String postId;

	protected String levelId;

	protected String jsFunction;

	protected int width;

	protected int height;

	protected String viewPage;

	protected String id;
	
	protected String originalElements;
	
	protected Boolean showAllAccount;
	
	protected Boolean include;
	
	protected String targetWindow;
	
	protected Boolean isAutoClose;
	


	/*
	 * 最大选择数， -1表示没有限制
	 */
	protected int maxSize = -1;

	/**
	 * 最少选择数，至少选择一个，可以设置为0个
	 */
	protected int minSize = 1;

	public SelectPeopleSupport() {
		super();
		init();
	}

	private void init() {
		label = null;
		panels = "";
		showMe = true;
		selectType = "";
		memberId = "";
		departmentId = "";
		postId = "";
		levelId = "";
		jsFunction = "";
		width = 608;
		height = 488;
		viewPage = "";
		id = "";
		maxSize = -1;
		minSize = 1;
		originalElements = "";
		showAllAccount = null;
		include = null;
		targetWindow = targetWindow == null ?"getA8Top()":targetWindow;
		isAutoClose = isAutoClose==null? true : isAutoClose;
	}

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		String _url = "/selectPeople.do";

		_url = com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag
				.calculateURL("RENDER", _url, pageContext);

		_url += "?ViewPage=" + viewPage + "&ShowMe="
				+ showMe + "&Panels=" + panels + "&SelectType=" + selectType
				+ "&memberId=" + memberId + "&departmentId=" + departmentId
				+ "&postId=" + postId + "&levelId=" + levelId + "&maxSize="
				+ maxSize + "&minSize=" + minSize + "&id=" + id;
		
		if(Boolean.TRUE.equals(showAllAccount)){
			_url += "&showAllAccount=" + showAllAccount;
		}
		
		if(Boolean.TRUE.equals(include)){
			_url += "&include=" + include;
		}

		try {
			//是否div实现 true 模态对话框 false div实现
			Boolean f = (Boolean)(BrowserFlag.SelectPeople.getFlag(CurrentUser.get()));
			// 向JavaScript字符串输出时转义
			String originalElementsJs = StringEscapeUtils.escapeJavaScript(originalElements);
			if(Boolean.TRUE.equals(include)){
				//嵌入式
				out.println(Constants.getString("selectPeople.function.include", _url, id, originalElementsJs));
			}else{
				if(f && isAutoClose){
					//模态实现模式
					out.println(Constants.getString("selectPeople.function",jsFunction, _url, 488, 608, id, originalElementsJs));
				}else{
					if(isAutoClose){
						//div实现 点击确定 - 自动调用回调函数 - 关闭div窗口 
						out.println(Constants.getString("selectPeople.function.ipad", jsFunction, _url, 500, 608, id, originalElementsJs,targetWindow));
					}else{
						//div实现 点击确定 - 自动调用回调函数 - 不关闭div窗口  在回调函数中关闭选人窗口
						out.println(Constants.getString("selectPeople.function.ipadCommon", jsFunction, _url, 500, 608, id, originalElementsJs,targetWindow));

					}
				}
			}

			if (label != null && !"".equals(label)) {
				label = ResourceBundleUtil.getString(pageContext, label);
				out.println(Constants.getString("selectPeople.div.html", label, id));
			}
		}
		catch (IOException ioe) {
			throw new JspTagException(ioe.toString(), ioe);
		}

		init();

		return EVAL_PAGE;
	}

	public void release() {
		super.release();
		init();
	}

}
