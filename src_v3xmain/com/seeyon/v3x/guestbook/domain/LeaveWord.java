package com.seeyon.v3x.guestbook.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 部门空间留言本
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class LeaveWord extends com.seeyon.v3x.common.domain.BaseModel implements Serializable
{

    private static final long serialVersionUID = 1318440256455098479L;

    private long departmentId;
    
    private long creatorId;
    
    private String content;
    
    private Date createTime;
    
    private int indexShow;
    
    private Long replyId;
    
    private Long replyerId;
   
	private String urlImage;
	
	private String idflag;
	
	public String getIdflag() {
		return idflag;
	}
	public void setIdflag(String idflag) {
		this.idflag = idflag;
	}
	public Long getReplyerId() {
		return replyerId;
	}
	public void setReplyerId(Long replyerId) {
		this.replyerId = replyerId;
	}
	public Long getReplyId() {
		return replyId;
	}
	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

    
	public int getIndexShow() {
		return indexShow;
	}
	public void setIndexShow(int indexShow) {
		this.indexShow = indexShow;
	}
	public String getUrlImage() {
		return urlImage;
	}
	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}
	public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
    public Date getCreateTime()
    {
        return createTime;
    }
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
    public long getDepartmentId()
    {
        return departmentId;
    }
    public void setDepartmentId(long departmentId)
    {
        this.departmentId = departmentId;
    }
    public long getCreatorId()
    {
        return creatorId;
    }
    public void setCreatorId(long creatorId)
    {
        this.creatorId = creatorId;
    }   
}
