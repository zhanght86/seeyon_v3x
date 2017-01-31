package com.seeyon.v3x.doc.webmodel;



import com.seeyon.v3x.doc.domain.DocLib;

/**
 * 文档库列表vo
 */
public class DocLibTableVo {
	private DocLib doclib;
	// 创建人
	private String createName;
	// 管理员
	private String managerName;
	// 文档库类型
	private String docLibType;
	// 文档库的根
	private DocAclVO root;
	// 图片名称
	private String icon;
	// 当前用户是否管理员
	private boolean isOwner;

	// 公文，项目没有共享
	private boolean noShare;

	public boolean getNoShare() {
		return noShare;
	}

	public void setNoShare(boolean noShare) {
		this.noShare = noShare;
	}

	public boolean getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDocLibType() {
		return docLibType;
	}

	public void setDocLibType(String docLibType) {
		this.docLibType = docLibType;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}



	public DocLibTableVo(DocLib doclib){
		this.doclib=doclib;
	}
	
	public DocLib getDoclib() {
		return doclib;
	}

	public void setDoclib(DocLib doclib) {
		this.doclib = doclib;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public DocAclVO getRoot() {
		return root;
	}

	public void setRoot(DocAclVO root) {
		this.root = root;
	}



	
	
}
