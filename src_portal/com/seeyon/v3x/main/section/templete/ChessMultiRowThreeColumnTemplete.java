package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.util.Datetimes;

/**
 * 
 * 棋盘式 、3列模板
 * 
 */
public class ChessMultiRowThreeColumnTemplete extends BaseSectionTemplete {

	private static final long serialVersionUID = -4423167823073107716L;

	private List<ChessMultiRowThreeColumnTemplete.Item> items;

	private List<ChessMultiRowThreeColumnTemplete.Row> rows;

	private int columnNumber = 1;

	private int rowNumber = 8;

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getResolveFunction() {
		return "chessMultiRowThreeColumnTemplete";
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
	
	public List<String> getRowList() {
		return rowList;
	}

	public ChessMultiRowThreeColumnTemplete.Item addItem() {
		if (items == null) {
			items = new ArrayList<ChessMultiRowThreeColumnTemplete.Item>();
		}
		ChessMultiRowThreeColumnTemplete.Item item = new ChessMultiRowThreeColumnTemplete.Item();
		items.add(item);
		return item;
	}

	public List<ChessMultiRowThreeColumnTemplete.Item> getItems() {
		return items;
	}

	public ChessMultiRowThreeColumnTemplete.Row addRow() {
		if (rows == null) {
			rows = new ArrayList<ChessMultiRowThreeColumnTemplete.Row>();
		}
		ChessMultiRowThreeColumnTemplete.Row row = new ChessMultiRowThreeColumnTemplete.Row();
		rows.add(row);
		return row;
	}

	public List<ChessMultiRowThreeColumnTemplete.Row> getRows() {
		return rows;
	}

	/**
	 * 当前时间
	 * 
	 */
	public long getTodayFirstTime() {
		return Datetimes.getTodayFirstTime().getTime();
	}

	/**
	 * 棋盘式数据 name
	 * 
	 */
	public class Item extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = -4916028656389145375L;

		private String icon;

		private String name;

		private String link;

		private String title;

		private Integer maxLength;

		private boolean hasAttachments;

		private int openType = OPEN_TYPE.href.ordinal();

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(Integer maxLength) {
			this.maxLength = maxLength;
		}

		public boolean isHasAttachments() {
			return hasAttachments;
		}

		public void setHasAttachments(boolean hasAttachments) {
			this.hasAttachments = hasAttachments;
		}

		public int getOpenType() {
			return openType;
		}

		public void setOpenType(OPEN_TYPE openType) {
			this.openType = openType.ordinal();
		}

	}

	/**
	 * 行数据 subject createDate createMemberName
	 */
	public class Row extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = -8784476984664313807L;

		private String icon;

		private String subject;

		private String link;

		private String title;

		private Integer maxLength;

		private Boolean hasAttachments;

		private Date createDate;

		private String createMemberName;

		private int openType = OPEN_TYPE.href.ordinal();

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(Integer maxLength) {
			this.maxLength = maxLength;
		}

		public Boolean getHasAttachments() {
			return hasAttachments;
		}

		public void setHasAttachments(Boolean hasAttachments) {
			this.hasAttachments = hasAttachments;
		}

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

		public int getOpenType() {
			return openType;
		}

		public void setOpenType(OPEN_TYPE openType) {
			this.openType = openType.ordinal();
		}

	}
}
