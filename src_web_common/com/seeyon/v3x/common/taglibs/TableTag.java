package com.seeyon.v3x.common.taglibs;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.seeyon.v3x.common.taglibs.support.TableSupport;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-13
 */
public class TableTag extends TableSupport {

	private static final long serialVersionUID = 1035914981665506648L;

	public TableTag() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.common.taglibs.support.TableColumnSupport#release()
	 */
	public void release() {
		super.release();
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public void setPageSize(int _pageSize) {
		this.pageSize = _pageSize;
	}

	public void setSize(int _size) {
		this.size = _size;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public void setVarIndex(String varIndex) {
		this.varIndex = varIndex;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public void setShowPager(boolean showPager) {
		this.showPager = showPager;
	}

	public void setOnRowClick(String onRowClick) {
		this.onRowClick = onRowClick;
	}

	public void setOnRowDblClick(String onRowDblClick) {
		this.onRowDblClick = onRowDblClick;
	}

	public void setWidth(String width) {
		this.width = width;
	}
	
	public void setLeastSize(int leastSize){
		super.leastSize = leastSize;
	}
	
	public void setIsChangeTRColor(boolean isChangeTRColor){
		super.isChangeTRColor = isChangeTRColor;
	}
	public boolean isDragable() {
		return super.dragable;
	}

	public void setDragable(boolean dragable) {
		super.dragable = dragable;
	}
	public int getSubHeight() {
		return subHeight;
	}

	public void setSubHeight(int subHeight) {
		this.subHeight = subHeight;
	}
	/**
	 * 先从指定的资源中查找，再查找默认的
	 * 
	 * @param locCtxt
	 * @throws JspTagException
	 */
    public void setBundle(LocalizationContext locCtxt) throws JspTagException {
        this.bundleAttrValue = locCtxt;
    }
    
    /**
     * 分页提交方式，默认get
     * 
     * @param formMethod get or post
     */
    public void setFormMethod(String formMethod){
    	if("get".equalsIgnoreCase(formMethod) || "post".equalsIgnoreCase(formMethod)){
    		super.formMethod = formMethod;
    	}
    }
}
