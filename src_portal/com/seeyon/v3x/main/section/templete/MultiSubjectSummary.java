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
import com.seeyon.v3x.util.Strings;

/**
 * 多行的，显示标题和摘要，常用新闻、公告
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-27
 */
public class MultiSubjectSummary extends BaseSectionTemplete {

	private static final long serialVersionUID = 2508774780006598481L;

	/**
	 * 显示条数枚举
	 */
	public enum NUMBER {
		two(2, 100), //两条
		four(4, 50); //四条

		private int value;

		private int summaryLength;

		private NUMBER(int value, int summaryLength) {
			this.value = value;
			this.summaryLength = summaryLength;
		}

		public int value() {
			return this.value;
		}

		public int summaryLength() {
			return this.summaryLength;
		}
	}

	/**
	 * 条目
	 */
	private List<MultiSubjectSummary.Entry> entries;

	/**
	 * 显示条数
	 */
	private int number = 2;

	public String getResolveFunction() {
		return "MultiSubjectSummary";
	}

	/**
	 * 添加一个条目
	 * 
	 * @return
	 */
	public MultiSubjectSummary.Entry addEnery() {
		if (this.entries == null) {
			this.entries = new ArrayList<MultiSubjectSummary.Entry>();
		}

		MultiSubjectSummary.Entry enery = new MultiSubjectSummary.Entry();
		this.entries.add(enery);

		return enery;
	}

	public List<MultiSubjectSummary.Entry> getEntries() {
		return entries;
	}

	public int getNumber() {
		return number;
	}

	/**
	 * 设置栏目显示条目总数，默认2条
	 * 
	 * @param number
	 */
	public void setNumber(MultiSubjectSummary.NUMBER number) {
		this.number = number.value();
	}

	/**
	 * 条目
	 * 
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-6-27
	 */
	public class Entry extends ObjectToXMLBase implements Serializable {
		private static final long serialVersionUID = 2948009912137380773L;

		private String subject;

		private Date createDate;

		private String summary;

		private String createMemberName;

		private String link;
		
		private Boolean hasAttachment;

        private int openType;
        
		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public String getCreateMemberName() {
			return createMemberName;
		}

		public void setCreateMemberName(String createMemberName) {
			this.createMemberName = createMemberName;
		}

		public String getSubject() {
			return subject;
		}

		/**
		 * 设置标题
		 */
		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getSummary() {
			return summary;
		}

		/**
		 * 设置摘要
		 * 
		 * @param summary
		 * @param number
		 *            显示的总条数，用于summary长度的截取，必须和MultiSubjectSummary.setNumber()保持一致
		 */
		public void setSummary(String summary, MultiSubjectSummary.NUMBER number) {
			if(summary == null){
				this.summary = "";
			}
			else{
				this.summary = StringEscapeUtils.escapeJavaScript(Strings.getLimitLengthString(summary, number.summaryLength(), "..."));
			}
		}

		public String getLink() {
			return link;
		}

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

		public Boolean getHasAttachment() {
			return hasAttachment;
		}

		public void setHasAttachment(Boolean hasAttachment) {
			this.hasAttachment = hasAttachment;
		}
		
	}

}
