package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档存储空间，分为三种单独存储
 * 文档空间，博客空间，邮件空间
 */
public class DocStorageSpace extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
//	private long docLibId;
	// 文档空间状态 Constants.SPACE_xxx
	private byte status;
	// 总的文档空间 字节
	private long totalSpaceSize;
	// 已经使用的文档空间 字节
	private long usedSpaceSize;
	// 用户id
	private long userId;
//	 总的邮件空间 字节
	private long mailSpace;
//	 已经使用的邮件空间 字节
	private long mailUsedSpace;
//	 邮件空间状态 Constants.SPACE_xxx
	private byte mailStatus;
//	 总的博客空间 字节
	private long blogSpace;
//	 已经使用的博客空间 字节
	private long blogUsedSpace;
//	博客空间状态 Constants.SPACE_xxx
	private byte blogStatus;
	
	
	/**
	 *  2007.11.27 空间大小控制：
	 *  文档： 100 M -- 10 G
	 *  邮件： 100 M -- 10 G
	 *  博客： 10  M -- 1 G
	 * 
	 */
  

	public byte getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(byte mailStatus) {
		this.mailStatus = mailStatus;
	}

	public long getMailSpace() {
		return mailSpace;
	}

	public void setMailSpace(long mailSpace) {
		this.mailSpace = (mailSpace > 0L ? mailSpace : 0L);
	}

	public long getMailUsedSpace() {
		return mailUsedSpace;
	}

	public void setMailUsedSpace(long mailUsedSpace) {
		this.mailUsedSpace = (mailUsedSpace > 0L ? mailUsedSpace : 0L);
	}

	public long getTotalSpaceSize() {
		return totalSpaceSize;
	}

	public void setTotalSpaceSize(long totalSpaceSize) {
		this.totalSpaceSize = (totalSpaceSize > 0L ? totalSpaceSize : 0L);
	}

	public long getUsedSpaceSize() {
		return usedSpaceSize;
	}

	public void setUsedSpaceSize(long usedSpaceSize) {
		this.usedSpaceSize = (usedSpaceSize > 0L ? usedSpaceSize : 0L);
	}

	public DocStorageSpace() {
    }


//	public long getDocLibId() {
//		return this.docLibId;
//	}
//	public void setDocLibId(long docLibId) {
//		this.docLibId = docLibId;
//	}

	public byte getStatus() {
		return this.status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}

	public long getUserId() {
		return this.userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public long getBlogSpace() {
		return blogSpace;
	}
	
	public void setBlogSpace(long blogSpace) {
		this.blogSpace = (blogSpace > 0L ? blogSpace : 0L);
	}
	
	public long getBlogUsedSpace() {
		return blogUsedSpace;
	}
	
	public void setBlogUsedSpace(long blogUsedSpace) {
		this.blogUsedSpace = (blogUsedSpace > 0L ? blogUsedSpace : 0L);
	}
	
	public byte getBlogStatus() {
		return blogStatus;
	}
	
	public void setBlogStatus(byte blogStatus) {
		this.blogStatus = blogStatus;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}