package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
/**
 * 日程事件的四列模板：标题 开始时间 结束时间 状态
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class CalendarFourColumnTemplete extends BaseSectionTemplete {
	private static final long serialVersionUID = -6071504232545791572L;

	private List<CalendarFourColumnTemplete.Row> rows;

	@Override
	public String getResolveFunction() {
		return "calendarFourColumnTemplete";
	}

	public Row addRow() {
		if (this.rows == null) {
			this.rows = new ArrayList<CalendarFourColumnTemplete.Row>();
		}

		CalendarFourColumnTemplete.Row row = new CalendarFourColumnTemplete.Row();
		this.rows.add(row);

		return row;
	}

	public List<CalendarFourColumnTemplete.Row> getRows() {
		return this.rows;
	}

	/**
	 * 行数据
	 */
	public class Row extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = -4929958760306861618L;

		private String subject;

		private String link;

		private String beginDate;

		private String endDate;

		private Integer maxLength;

		private Boolean hasAttachments;

		private String state;
		
		private String stateEditLink;
		
		private String timeFlag;

		public Row() {

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

		public void setLink(String link) {
			this.link = link;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(Integer maxLength) {
			this.maxLength = maxLength;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

	

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getStateEditLink() {
			return stateEditLink;
		}

		public void setStateEditLink(String stateEditLink) {
			this.stateEditLink = stateEditLink;
		}

		public String getBeginDate() {
			return beginDate;
		}

		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getTimeFlag() {
			return timeFlag;
		}

		public void setTimeFlag(String timeFlag) {
			this.timeFlag = timeFlag;
		}
		
	}
}
