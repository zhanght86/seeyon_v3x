package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 文档夹vo
 */
public class FolderItemFolder extends FolderItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4461245628997369929L;
	private boolean subfolderEnabled;
	/** 是否允许评论  */
	private boolean commentEnabled;

	public boolean getCommentEnabled() {
		return commentEnabled;
	}
	public void setCommentEnabled(boolean commentEnabled) {
		this.commentEnabled = commentEnabled;
	}
	public boolean getSubfolderEnabled() {
		return subfolderEnabled;
	}
	public void setSubfolderEnabled(boolean subfolderEnabled) {
		this.subfolderEnabled = subfolderEnabled;
	}
	public FolderItemFolder(DocResource docResource) {
		super(docResource);
		this.setSubfolderEnabledProperties(docResource);
	}

	@Override
	public void setDocResource(DocResource docResource) {
		super.setDocResource(docResource);
		this.setSubfolderEnabledProperties(docResource);
	}

	private void setSubfolderEnabledProperties(DocResource dr) {
		this.setSubfolderEnabled(dr.getSubfolderEnabled());
		this.setCommentEnabled(dr.getCommentEnabled());
	}

}
