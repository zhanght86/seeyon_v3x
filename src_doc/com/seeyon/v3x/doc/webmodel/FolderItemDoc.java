package com.seeyon.v3x.doc.webmodel;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 复合文档
 */
public class FolderItemDoc extends FolderItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3385782702990043082L;

	private String body;

	private long size;

	private byte status;

	private int accessCount;

	private int commentCount;
	
	private String checkOutUserName;
	
	private long contentTypeId;
	
	private boolean hasAtt;
	// 附件
	private List<Attachment> atts = new ArrayList<Attachment>();
	
	// 关联文档
	private List<DocResource> linkedDocs;	
	
	// 文档评论
	private List<DocForumVO> forums;	
	
	//	是否有附件
	public boolean getHasAtt(){
		return hasAtt;
	}
	
	public void setHasAtt(boolean hasAtt){
		this.hasAtt = hasAtt;
	}

	public List<DocForumVO> getForums() {
		return forums;
	}

	public void setForums(List<DocForumVO> forums) {
		this.forums = forums;
	}

	public List<Attachment> getAtts() {
		return atts;
	}

	public void setAtts(List<Attachment> atts) {
		this.atts = atts;
	}

	public List<DocResource> getLinkedDocs() {
		return linkedDocs;
	}

	public void setLinkedDocs(List<DocResource> linkedDocs) {
		this.linkedDocs = linkedDocs;
	}

	private void setFolderItemDocProperties(DocResource dr) {
		this.setSize(dr.getFrSize());
		this.setStatus(dr.getStatus());
		this.setAccessCount(dr.getAccessCount());
		this.setCommentCount(dr.getCommentCount());
		this.setContentTypeId(dr.getFrType());
	}
	
	@Override
	public void setDocResource(DocResource docResource) {
		super.setDocResource(docResource);
		this.setFolderItemDocProperties(docResource);
	}

	public FolderItemDoc(DocResource dr) {
		super(dr);
		this.setFolderItemDocProperties(dr);
	}

	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public float getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getCheckOutUserName() {
		return checkOutUserName;
	}

	public void setCheckOutUserName(String checkOutUserName) {
		this.checkOutUserName = checkOutUserName;
	}

	public long getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(long contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	 @Override
	 public String toString() {
		 return "FolderItemDoc[" + super.getName() + ", desc: " + super.getDesc() + ", path: " + super.getPath() + " ]";
	 }
}
