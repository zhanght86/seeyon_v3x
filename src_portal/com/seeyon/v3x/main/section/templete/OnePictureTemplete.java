package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
/**
 * @author Administrator
 *  图片滚动式
 */
public class OnePictureTemplete extends BaseSectionTemplete{
	
	private static final long serialVersionUID = -125861337120224552L;
	
	private List<OnePictureTemplete.PictureItem> items ;
	
	private int pictureNum = 1 ;

	public String getResolveFunction() {
		return "onePictureTemplete";
	}
	/**
	 * 添加一项数据
	 * @return
	 */
	public OnePictureTemplete.PictureItem addItem(){
		if(null == items){
			items = new ArrayList<OnePictureTemplete.PictureItem>();
		}
		OnePictureTemplete.PictureItem  item = new OnePictureTemplete.PictureItem() ;
		items.add(item) ;
		
		return item ;
	}
	/**
	 * 一次显示图片的数目
	 * @param pictureNum
	 */
	public void setPictureNum(int pictureNum){	
		this.pictureNum = pictureNum ;
	}
	
	public int getPictureNum() {
		return pictureNum;
	}

	public List<OnePictureTemplete.PictureItem> getItems() {
		return items;
	}	
	
	public class PictureItem extends  ObjectToXMLBase implements Serializable {
		private static final long serialVersionUID = -4210710408773230061L;
		
		private Long id ;
		
		private String name ;
		
		private String path ;
		
		private String link;
		
		private String alt ;
		
		private int width ;
		
		private int height ;
		
		private String title ;
		
		/**
		 * 链接打开方式 
		 */
		private int openType = OPEN_TYPE.openWorkSpace.ordinal();

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public String getAlt() {
			return alt;
		}

		public void setAlt(String alt) {
			this.alt = alt;
		}

		public int getOpenType() {
			return openType;
		}

		public void setOpenType(OnePictureTemplete.OPEN_TYPE openType) {
			this.openType = openType.ordinal();
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}

}
