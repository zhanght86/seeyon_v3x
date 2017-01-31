package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * @author dongyj
 * 
 *<pre>
 * <b>图片加列表模板</b>
 * 第一列为图片居左 右边为标题加摘要
 * 下面为列表
 * 列表内容为
 * 标题  发起时间  所属板块
 * 列表参数可配置
 * </pre>
 * @version 1.0
 * 
 */
public class OneImageAndListTemplete extends MultiRowThreeColumnTemplete {

	private static final long serialVersionUID = 7348988387317440572L;

	@Override
	public String getResolveFunction() {
		return "oneImageAndListTemplete";
	}

	public FirstRow firstRow;

	public FirstRow getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(String title, String abStr, String id, String imageDate, String link, OPEN_TYPE openType, String categoryLabel, String categoryLink, Date createDate) {
		FirstRow f = new FirstRow();
		f.setTitle(title);
		f.setAbStr(abStr);
		f.setImageId(id);
		f.setImageDate(imageDate);
		f.setLink(link, openType);
		f.setCategoryLabel(categoryLabel);
		f.setCategoryLink(categoryLink);
		f.setCreateDate(createDate);
		this.firstRow = f;
	}

	public class FirstRow extends ObjectToXMLBase implements Serializable {
		
		private static final long serialVersionUID = 7260035115842628503L;
		
		// 标题
		private String title;
		
		// 摘要
		private String abStr;
		
		// 图片id
		private String imageId;
		
		// 图片创建日期
		private String imageDate;

		private String link;

		private int openType;

		private String categoryLabel;

		private String categoryLink;
		
		private Date createDate;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAbStr() {
			return abStr;
		}

		public void setAbStr(String abStr) {
			this.abStr = abStr;
		}

		public String getImageId() {
			return imageId;
		}

		public void setImageId(String imageId) {
			this.imageId = imageId;
		}

		public String getImageDate() {
			return imageDate;
		}

		public void setImageDate(String imageDate) {
			this.imageDate = imageDate;
		}
		
		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public void setLink(String link, OPEN_TYPE openType) {
			this.link = link;
			this.openType = openType.ordinal();
		}

		public int getOpenType() {
			return openType;
		}

		public void setOpenType(int openType) {
			this.openType = openType;
		}

		public String getCategoryLabel() {
			return categoryLabel;
		}

		public void setCategoryLabel(String categoryLabel) {
			this.categoryLabel = categoryLabel;
		}

		public String getCategoryLink() {
			return categoryLink;
		}

		public void setCategoryLink(String categoryLink) {
			this.categoryLink = categoryLink;
		}
		
		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}
	}

}