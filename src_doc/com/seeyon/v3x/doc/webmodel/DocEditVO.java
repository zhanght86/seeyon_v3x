package com.seeyon.v3x.doc.webmodel;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 文档编辑vo
 */
public class DocEditVO extends FolderItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2006109367173141482L;
	// 正文	
	private String content;
	// 正文类型
	private String bodyType;
	// 正文创建时间
	private Date createDate;	
	// 是否上传文件
	private boolean isFile;
	// 上传文件对应的V3xFile
	private V3XFile file;
	// 附件集合
	private List<Attachment> attachments;
	// 创建时间的截串  yyyy-mm-dd
	private String createDateString;
	// 内容类型
	private long contentTypeId;
	// 是否可以在线编辑
	private boolean canEditOnline;
	
	public long getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(long contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public DocEditVO(DocResource dr) {
		super(dr);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getBodyType() {
		return bodyType;
	}
	
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public boolean getIsFile() {
		return isFile;
	}

	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
		if(isFile)
			this.canEditOnline = false;
	}
	
	public V3XFile getFile() {
		return file;
	}

	public void setFile(V3XFile file) {
		this.file = file;
	}
		
	public List<Attachment> getAttachments() {
		return attachments;
	}
	
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	
	
	public boolean getCanEditOnline() {
		return canEditOnline;
	}

	public void setCanEditOnline(boolean canEditOnline) {
		this.canEditOnline = canEditOnline;
	}
}
