/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.util.Strings;

/**
 * 显示模式：一条显示为“标题+时间+(类别)+摘要”，下面是若干行列表
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-28
 */
public class OneSummaryAndMultiList extends BaseSectionTemplete {

	private static final long serialVersionUID = 8941443118514414145L;

	public static enum FirstType {
		Up, // 上下结构——第一条在上面，
		Down, // 上下结构——第一条在下面，
		Left, // 左右结构——第一条在左边，
		Right, // 左右结构——第一条在右边，
	}

	private List<OneSummaryAndMultiList.Row> rows;

	private Object[] first;

	private int leastSize = 8;

	private String firstType = FirstType.Up.name();

    private int firstOpenType;
    
	@Override
	public String getResolveFunction() {
		return "oneSummaryAndMultiList";
	}

	/**
	 * 增加列表行对象
	 * 
	 * @return
	 */
	public OneSummaryAndMultiList.Row addRow() {
		OneSummaryAndMultiList.Row row = new OneSummaryAndMultiList.Row();
		if (rows == null) {
			rows = new ArrayList<OneSummaryAndMultiList.Row>();
		}
		rows.add(row);

		return row;
	}

	public int getFirstOpenType() {
        return firstOpenType;
    }

    public void setFirstOpenType(OPEN_TYPE firstOpenType) {
        this.firstOpenType = firstOpenType.ordinal();
    }

    /**
	 * 设置第一条“标题+时间+(类别)+摘要”
	 * 
	 * @param subject
	 * @param summary
	 * @param link
	 * @param date
	 * @param categoryLabel
	 * @param categoryLink
	 * @param photo
     * @param hasAttachments
     * @param extIcons
	 */
	public void setFirstItem(String subject, String summary, String link,
			Date date, String categoryLabel, String categoryLink, String photo, Boolean hasAttachments, List<String> extIcons) {
		
		summary = StrExtractor.getHTMLContent(summary);
		if(Strings.isBlank(summary)){
			summary = "";
		}
		else{
			summary = StringEscapeUtils.escapeJavaScript(Strings.getLimitLengthString(summary, 200, "..."));
		}
		
		first = new Object[9];
		first[0] = SectionUtils.toNotNullString(subject);
		first[1] = summary;
		first[2] = SectionUtils.toNotNullString(link);
		first[3] = date;
		first[4] = SectionUtils.toNotNullString(categoryLabel);
		first[5] = SectionUtils.toNotNullString(categoryLink);
		first[6] = SectionUtils.toNotNullString(photo);
		first[7] = hasAttachments;
		first[8] = extIcons;
	}

	public Object[] getFirst() {
		return first;
	}

	public List<OneSummaryAndMultiList.Row> getRows() {
		return rows;
	}

	public int getLeastSize() {
		return leastSize;
	}

	/**
	 * 列表中至少显示多少行 默认8行
	 * 
	 * @param leastSize
	 */
	public void setLeastSize(int leastSize) {
		this.leastSize = leastSize;
	}

	/**
	 * 列表行对象
	 * 
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-8-28
	 */
	public class Row extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = 9103644718677560428L;

		private String subject;

		private String link;

		private String className;

		private Integer importantLevel;

		private Boolean hasAttachments;

		private String bodyType;

		private List<String> extIcons;

		private Date createDate;

		private String categoryLabel;

		private Integer applicationCategoryKey;

		private String categoryLink;

        private int openType;
        
		public String getBodyType() {
			return bodyType;
		}

		/**
		 * 正文类型
		 * 
		 * @param bodyType
		 * @return
		 */
		public Row setBodyType(String bodyType) {
			this.bodyType = bodyType;
			return this;
		}

		public String getCategoryLabel() {
			return categoryLabel;
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

		public String getCategoryLink() {
			return categoryLink;
		}

		public String getClassName() {
			return className;
		}

		public Row setClassName(String className) {
			this.className = className;
			return this;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public Row setCreateDate(Date createDate) {
			this.createDate = createDate;
			return this;
		}

		public List<String> getExtIcons() {
			return extIcons;
		}

		public Row setExtIcons(List<String> extIcons) {
			this.extIcons = extIcons;
			return this;
		}

		public Boolean getHasAttachments() {
			return hasAttachments;
		}

		public Row setHasAttachments(Boolean hasAttachments) {
			this.hasAttachments = hasAttachments;
			return this;
		}

		public String getLink() {
			return link;
		}

		public String getSubject() {
			return subject;
		}

		public Row setSubject(String subject, String link, OPEN_TYPE openType) {
			this.subject = subject;
			this.link = link;
            this.openType = openType.ordinal();
			return this;
		}

		public Integer getImportantLevel() {
			return importantLevel;
		}

		public Row setImportantLevel(Integer importantLevel) {
			this.importantLevel = importantLevel;
			return this;
		}

		public Integer getApplicationCategoryKey() {
			return this.applicationCategoryKey;
		}
	}

	public String getFirstType() {
		return firstType;
	}

	/**
	 * 第一行显示的方式
	 * 
	 * @param firstType
	 */
	public void setFirstType(OneSummaryAndMultiList.FirstType firstType) {
		this.firstType = firstType.name();
	}
}
