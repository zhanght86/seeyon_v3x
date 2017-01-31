package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.util.Strings;

/**
 * 包含文件
 * 复合文档属性 vo
 * 固定部分 + 可变部分(items)
 */
public class DocPropVO extends FolderItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5923071748006647051L;

	public DocPropVO(DocResource docResource) {
		super(docResource);
		this.setDocPropVOProperties(docResource);
	}

	// 大小
	private String size;
	// 状态
	private byte status;
	// 访问次数
	private int accessCount;
	// 评论次数
	private int commentCount;
	// 是否允许评论
	private boolean commentEnabled;
	private boolean isShortCut;
	private boolean isPigeonhole;

	private void setDocPropVOProperties(DocResource dr) {
		this.setSize(Strings.formatFileSize(dr.getFrSize(), true));
		this.setStatus(dr.getStatus());
		this.setAccessCount(dr.getAccessCount());
		this.setCommentCount(dr.getCommentCount());
		this.setCommentEnabled(dr.getCommentEnabled());
	}

	@Override
	public void setDocResource(DocResource docResource) {
		super.setDocResource(docResource);
		this.setDocPropVOProperties(docResource);
	}

	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public boolean isCommentEnabled() {
		return commentEnabled;
	}

	public void setCommentEnabled(boolean commentEnabled) {
		this.commentEnabled = commentEnabled;
	}

	public boolean getIsPigeonhole() {
		return isPigeonhole;
	}

	public void setIsPigeonhole(boolean isPigeonhole) {
		this.isPigeonhole = isPigeonhole;
	}

	public boolean getIsShortCut() {
		return isShortCut;
	}

	public void setIsShortCut(boolean isShortCut) {
		this.isShortCut = isShortCut;
	}
}
