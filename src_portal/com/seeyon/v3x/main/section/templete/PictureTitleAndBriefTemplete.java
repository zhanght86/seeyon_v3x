/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;

/**
 * @author dongyj
 *
 */
public class PictureTitleAndBriefTemplete extends BaseSectionTemplete {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3771923938577560521L;

	public String getResolveFunction() {
		return "pictureTitleAndBriefTemplete";
	}
	
	private List<PictureData> datas ;
	
	private List<String> rowList;
	
	public void addRowName(String rowName){
		if(this.rowList == null){
			this.rowList = new ArrayList<String>();
		}
		if(!this.rowList.contains(rowName)){
			this.rowList.add(rowName);
		}
	}
	
	public List<String> getRowList() {
		return rowList;
	}
	
	public List<PictureData> getDatas() {
		return datas;
	}

	/**
	 * 增加一行数据
	 * @return
	 */
	public PictureData addData(){
		if(datas == null){
			datas = new ArrayList<PictureData>();
		}
		PictureData pic = new PictureData();
		datas.add(pic);
		return pic;
	}
	
	public class PictureData extends ObjectToXMLBase implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7908108667585295599L;
		//图片
		private PortalPicture picture;
		//标题
		private String subject;
		//简要
		private String brief;
		//创建时间
		private String createDate;
		/**
		 * 点击标题的链接地址
		 */
		private String link;
		
		private int openType;
		
		private boolean hasAttachment;
		
		/**
		 * 类型
		 */
		private String type;
		
		private String typeLink;
		
		public boolean isHasAttachment() {
			return hasAttachment;
		}

		public void setHasAttachment(boolean hasAttachment) {
			this.hasAttachment = hasAttachment;
		}
		
		public String getType(){
			return type;
		}
		
		public String getTypeLink() {
			return typeLink;
		}

		public void setType(String type, String typeLink) {
			this.type = type;
			this.typeLink = typeLink;
		}
		
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
		
		public String getUrl() {
			return link;
		}
		
		public PortalPicture getPicture() {
			return picture;
		}
		public void setPicture(PortalPicture picture) {
			this.picture = picture;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
		public String getBrief() {
			return brief;
		}
		public void setBrief(String brief) {
			this.brief = brief;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
	}
}
