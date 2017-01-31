package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 文档树节点vo
 */
public class DocTreeVO  extends DocAclVO {
	// 打开图标
	private String openIcon;
	// 关闭图标
	private String closeIcon;
	// 文档库类型
	private byte docLibType;
	// 显示名称，已经国际化处理
	private String showName;
	// 如果是共享外单位文档库，设定对应的外单位ID以便在前端显示外单位简称以示区分
	private long otherAccountId;

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public byte getDocLibType() {
		return docLibType;
	}

	public void setDocLibType(byte docLibType) {
		this.docLibType = docLibType;
	}

	public DocTreeVO(DocResource dr) {
		super(dr);
	}

	public String getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(String closeIcon) {
		this.closeIcon = closeIcon;
	}

	public String getOpenIcon() {
		return openIcon;
	}

	public void setOpenIcon(String openIcon) {
		this.openIcon = openIcon;
	}

	public long getOtherAccountId() {
		return otherAccountId;
	}

	public void setOtherAccountId(long otherAccountId) {
		this.otherAccountId = otherAccountId;
	}

}
