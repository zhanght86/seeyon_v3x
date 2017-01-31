/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;

/**
 * @author dongyj
 *
 * 首页图片类。
 */
public class PortalPicture extends ObjectToXMLBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5535772377394730656L;
	
	/**
	 * 图片类型
	 * 0.系统图片
	 * 1.外部图片
	 */
	private int pictureType;
	
	private String picId;
	
	private String createDate;
	
	private String picUrl;
	
	/**
	 * 点击图片，链接的地址。
	 */
	private String link;

	private int openType;
	/**
	 * 取得图片链接的地址。<br>
	 * 这里指的是点击图片后链接的地址。默认为空。
	 *  
	 * @return
	 */
	public String getLink() {
		return link;
	}
	
	public int getOpenType() {
		return openType;
	}

	public void setLink(String link,OPEN_TYPE openType){
		this.link = link;
		this.openType = openType.ordinal();
	}

	/**
	 * 图片类型	<br>
	 * 0 系统图片<br>
	 * 1 外部图片
	 * @return
	 */
	public int getPictureType() {
		return pictureType;
	}

	public void setPictureType(int pictureType) {
		this.pictureType = pictureType;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	
}
