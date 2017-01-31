package com.seeyon.v3x.common.isignature.domain;

import java.sql.Blob;
import java.util.Date;

public class ISignatureHtml extends com.seeyon.v3x.common.domain.BaseModel implements java.io.Serializable {
	   private Long id;
	   //文档ID
	   private Long documentId;
	   //签章
	   private transient Blob signature;
	   
	   private Long userId;
	   private String hostName;
	   private Date signDate;	// 签章时间
	
	   
	   
	   public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getDocumentId() {
			return documentId;
		}
		public void setDocumentId(Long documentId) {
			this.documentId = documentId;
		}
		
		public Blob getSignature() {
			return signature;
		}
		public void setSignature(Blob signature) {
			this.signature = signature;
		}
		
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getHostName() {
			return hostName;
		}
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
		public Date getSignDate() {
			return signDate;
		}
		public void setSignDate(Date signDate) {
			this.signDate = signDate;
		}
   
}

