package com.seeyon.v3x.common.taglibs;

import javax.servlet.jsp.JspTagException;

import com.seeyon.v3x.common.taglibs.support.SelectPeopleSupport;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 Aug 30, 2006
 */
public class SelectPeopleTag extends SelectPeopleSupport {

	private static final long serialVersionUID = -3884805311261527140L;

	public SelectPeopleTag() {
		super();
	}

	public int doStartTag() throws JspTagException {
		return super.doStartTag();
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		super.release();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPanels(String panels) {
		this.panels = panels;
	}

	public void setJsFunction(String jsFunction) {
		this.jsFunction = jsFunction;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setShowMe(boolean showMe) {
		this.showMe = showMe;
	}

	public void setDepartmentId(Long departmentId) {
		super.departmentId = departmentId.toString();
	}

	public void setLevelId(Long levelId) {
		super.levelId = levelId.toString();
	}

	public void setMemberId(Long memberId) {
		super.memberId = memberId.toString();
	}

	public void setPostId(Long postId) {
		super.postId = postId.toString();
	}

	public void setViewPage(String viewPage) {
		super.viewPage = viewPage;
	}

	public void setId(String id) {
		super.id = id;
	}

	public void setMaxSize(int maxSize) {
		super.maxSize = maxSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	
	public void setOriginalElements(String originalElements){
		this.originalElements = originalElements;
	}
	
	public void setShowAllAccount(boolean showAllAccount){
		super.showAllAccount = showAllAccount;
	}
	
	public void setInclude(boolean include){
		super.include = include;
	}
	public String getTargetWindow() {
		return targetWindow;
	}

	public void setTargetWindow(String targetWindow) {
		this.targetWindow = targetWindow;
	}

	public Boolean getIsAutoClose() {
		return isAutoClose;
	}

	public void setIsAutoClose(Boolean isAutoClose) {
		this.isAutoClose = isAutoClose;
	}
}
