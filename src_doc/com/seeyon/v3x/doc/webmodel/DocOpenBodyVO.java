package com.seeyon.v3x.doc.webmodel;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.DocMVCUtils;


/**
 * 文档打开正文vo
 */
public class DocOpenBodyVO extends FolderItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8266479776549205401L;
	// 是否上传文件
	private boolean isFile;
	// 正文
	private String body;
	// 正文类型
	private String bodyType;
	// 锁定用户
	private String checkOutUserName;
	// 对应的V3xFile
	private V3XFile file;
	// 创建时间
	private Date createDate;
	// yyyy-mm-dd
	private String createDateString;
	//图片类型
	private Object value;
	//附件大小
	private String size;
	// 是否可以在线编辑
	private boolean canEditOnline;
	// 文档评论
	private List<DocForumVO> forums;	
	
	public void setBodyOfImage(Long docSourceId, Date drCreateTime) {
		this.setBody(DocMVCUtils.getPicBody(docSourceId, drCreateTime));
	}
	
	public Object getValue(){
		return  value;
	}
	
	public void setValue(Object value){
		this.value = value;
	}
	public String getSize(){
		return size;
	}
	public void setSize(String size){
		this.size = size;
	}

	public List<DocForumVO> getForums() {
		return forums;
	}

	public void setForums(List<DocForumVO> forums) {
		this.forums = forums;
	}
	
	public DocOpenBodyVO(DocResource dr) {
		super(dr);
		this.createDateString = dr.getCreateTime().toString().substring(0, 10);
	}

	public DocOpenBodyVO() {
		super();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCheckOutUserName() {
		return checkOutUserName;
	}

	public void setCheckOutUserName(String checkOutUserName) {
		this.checkOutUserName = checkOutUserName;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public boolean getCanEditOnline() {
		return canEditOnline;
	}

	public void setCanEditOnline(boolean canEditOnline) {
		this.canEditOnline = canEditOnline;
	}

}
