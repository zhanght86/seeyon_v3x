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
 * 多行3列模板，依次是：subject createDate category
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-11
 */
public class MultiRowThreeColumnTemplete extends BaseSectionTemplete {
	private static final long serialVersionUID = -5777301102904930154L;

	private List<MultiRowThreeColumnTemplete.Row> rows;

	@Override
	public String getResolveFunction() {
		return "multiRowThreeColumnTemplete";
	}
	
	private List<String> rowList;
	
	public void addRowName(String rowName){
		if(this.rowList == null){
			this.rowList = new ArrayList<String>();
		}
		if(!this.rowList.contains(rowName)){
			this.rowList.add(rowName);
		}
	}

	public MultiRowThreeColumnTemplete.Row addRow() {
		if (this.rows == null) {
			this.rows = new ArrayList<MultiRowThreeColumnTemplete.Row>();
		}

		MultiRowThreeColumnTemplete.Row row = new MultiRowThreeColumnTemplete.Row();
		this.rows.add(row);

		return row;
	}

	public List<MultiRowThreeColumnTemplete.Row> getRows() {
		return this.rows;
	}
    
    /**
     * 当前时间
     * 
     * @return
     */
    public long getTodayFirstTime(){
        return Datetimes.getTodayFirstTime().getTime();
    }
    
	/**
	 * 行数据
	 * 
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-5-12
	 */
	public class Row extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = -1023162415065244156L;

		private String subject;
		
		private String subjectHTML;

		private String link;

		private String className;

		private String alt;

		private Integer importantLevel;

		private Boolean hasAttachments;

		private String bodyType;

		private List<String> extIcons;

		private Date createDate;

		private String categoryLabel;

		private Integer applicationCategoryKey;

		private String categoryLink;

        private int openType;
        
		public Row() {

		}

		public String getAlt() {
			return alt;
		}

		public void setAlt(String alt) {
			this.alt = alt;
		}

		public String getBodyType() {
			return bodyType;
		}

		public void setBodyType(String bodyType) {
			this.bodyType = bodyType;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public List<String> getExtIcons() {
			return extIcons;
		}

		public void addExtIcons(String extIcon) {
			if (this.extIcons == null) {
				this.extIcons = new ArrayList<String>();
			}

			this.extIcons.add(extIcon);
		}

		public Boolean getHasAttachments() {
			return hasAttachments;
		}

		public void setHasAttachments(Boolean hasAttachments) {
			this.hasAttachments = hasAttachments;
		}

		public String getLink() {
			return link;
		}

		/**
		 * 标题链接，直接用/*.do?method=**&...
		 * 
		 * @param link
		 *            如
		 *            row.setLink("/collaboration.do?method=detail&from=Done&affairId=" +
		 *            affair.getId())
		 */
		public void setLink(String link) {
			this.link = link;
		}

        /**
         * 标题链接，直接用/*.do?method=**&...
         * 
         * @param link
         * @param openType 打开方式
         */
        public void setLink(String link, OPEN_TYPE openType) {
            this.link = link;
            this.openType = openType.ordinal();
        }
        
        public int getOpenType() {
            return openType;
        }

        public void setOpenType(OPEN_TYPE openType) {
            this.openType = openType.ordinal();
        }
        
		public Integer getImportantLevel() {
			return importantLevel;
		}

		public void setImportantLevel(Integer importantLevel) {
			this.importantLevel = importantLevel;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		/**
		 * 类别链接，直接用/*.do?method=**&...
		 * 
		 * @param label
		 *            直接输出的文本，不做国际化
		 * @param link
		 *            如 "/collaboration.do?method=detail&from=Done&affairId=" +
		 *            affair.getId())
		 */
		public void setCategory(String label, String link) {
			this.categoryLabel = label;
			this.categoryLink = link;
		}

		/**
		 * 
		 * @param applicationCategoryKey
		 *            应用分类，参见{@link ApplicationCategoryEnum}
		 * @param link
		 */
		public void setCategory(int applicationCategoryKey, String link) {
			this.applicationCategoryKey = applicationCategoryKey;
			this.categoryLink = link;
		}

		public String getCategoryLabel() {
			return categoryLabel;
		}

		public Integer getApplicationCategoryKey() {
			return applicationCategoryKey;
		}

		public String getCategoryLink() {
			return categoryLink;
		}

		public String getSubjectHTML() {
			return subjectHTML;
		}
		
		/**
		 * 标题的HTML代码，设置这个属性后，其它参数{hasAttachments, extIcons}仍然不起作用
		 * 
		 * @see SectionUtils.mergeSubject(String subject, int maxLength, Integer importantLevel, Boolean hasAttachments, String bodyType, List<String> extIcons)
		 * 
		 * @param cellContentHTML
		 */
		public void setSubjectHTML(String subjectHTML) {
			this.subjectHTML = subjectHTML;
		}

	}

	public List<String> getRowList() {
		return rowList;
	}
	
}
