/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.util.Datetimes;

/**
 * @author dongyj
 *
 */
public final class PictureTemplete extends BaseSectionTemplete implements Serializable {
	
	private static final long serialVersionUID = -1767244308892008482L;
	
	public static enum Model{
		rightIcon,
		index,
		leftScroll
	}

	private List<Picture> pictures ;
	
	private Integer currentPage = 0;
	
	private Model model = Model.rightIcon;
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Picture addPicture(Long id, Date createDate){
		if(this.pictures == null){
			this.pictures = new ArrayList<Picture>();
		}
		
		String create = Datetimes.formatDate(createDate);
		Picture picture = new Picture(id,create);
		this.pictures.add(picture);
		return picture;
	}
	
	public Integer getPerCount() {
		return perCount;
	}

	public void setPerCount(Integer perCount) {
		this.perCount = perCount;
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	/**
	 * 要加载的图片数目。
	 * 但是实际返回的肯定不会比这个大。
	 * 如果实际返回的比这个小，那么就不会有下一页了。
	 */
	private Integer perCount;
	
	/**
	 * 一共多少页
	 */
	private Integer totalPage;
	
	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public String getResolveFunction() {
		return "pictureTemplete";
	}
	
	public String getModel() {
		return model.name();
	}

	/**
	 * 默认Model.rightIcon
	 * 
	 * @param model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	public class Picture extends  ObjectToXMLBase implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7030115735499723217L;

		//外部图片url
		private String url;
		private int type;//图片类型--可以是外部图片
		private Long id;//图片id
		private String createDate;
		
		
		private String subject;//标题
		private String link;//图片链接地址
		
		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public Picture(Long id,String createDate){
			this.id = id;
			this.createDate = createDate;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Picture(){}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		
	}
}
