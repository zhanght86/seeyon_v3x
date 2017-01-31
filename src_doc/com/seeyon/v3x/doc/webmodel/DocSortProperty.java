package com.seeyon.v3x.doc.webmodel;

import java.io.Serializable;
import java.util.Date;

public class DocSortProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4430670036208835597L;
	
	private Long id;

	private String docImageType;//排序页的图片显示类型

	private String docName;//文档的名字

	private String docContentType;//文档的类型，e.g.文档、文档夹、协同、公文等

	private String docCreater;//文档的创建人

	private Date docLastUpdateDate;//文档的最后修改时间

	public String getDocContentType() {
		return docContentType;
	}

	public void setDocContentType(String docContentType) {
		this.docContentType = docContentType;
	}

	public String getDocCreater() {
		return docCreater;
	}

	public void setDocCreater(String docCreater) {
		this.docCreater = docCreater;
	}

	public String getDocImageType() {
		return docImageType;
	}

	public void setDocImageType(String docImageType) {
		this.docImageType = docImageType;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}
	public DocSortProperty(){
		
	}

	public Date getDocLastUpdateDate() {
		return docLastUpdateDate;
	}

	public void setDocLastUpdateDate(Date docLastUpdateDate) {
		this.docLastUpdateDate = docLastUpdateDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
