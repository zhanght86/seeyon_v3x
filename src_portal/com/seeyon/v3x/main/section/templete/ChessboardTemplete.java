package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * 棋盘式
 * 左边小图标(默认16*16)+右边标题
 * 上边大图标(默认32*32)+下边标题
 * 标题可以有浮动菜单
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-21
 * 
 */
public class ChessboardTemplete extends BaseSectionTemplete {

	private static final long serialVersionUID = -135861337120224552L;

	private List<ChessboardTemplete.Item> items;

	private int columnNumber = 1;

	private int rowNumber = 8;

	private boolean hasNewMail = false;// 是否有Email权限

	private boolean hasNewColl = true;// 是否有权限发协同
	
	/**
	 * 显示在栏目的最下方，在[更多]之下
	 */
	private String extendHTML;

	private POSITION_TYPE position = POSITION_TYPE.left;// 图标位置

	public static enum POSITION_TYPE {
		left, // 图标位于文字左方
		right, // 图标位于文字右方
		top, // 图标位于文字上方
		bottom, // 图标位于文字下方
	}
	
	private int iconWidth = 16;// 图标宽度
	
	private int iconHeight = 16;// 图标高度
	
	private int tdHeight = 25; // 单元格高度

	@Override
	public String getResolveFunction() {
		return "chessboardTemplete";
	}

	/**
	 * 增加一项数据
	 */
	public ChessboardTemplete.Item addItem() {
		if (items == null) {
			items = new ArrayList<ChessboardTemplete.Item>();
		}

		ChessboardTemplete.Item item = new ChessboardTemplete.Item();
		items.add(item);
		return item;
	}

	public List<ChessboardTemplete.Item> getItems() {
		return items;
	}

	/**
	 * 设置棋盘格式
	 * 
	 * @param rowNumber 行数：一般 8行
	 * @param columnNumber 列数
	 * 
	 */
	public void setLayout(int rowNumber, int columnNumber) {
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}
	
	public boolean isHasNewMail() {
		return hasNewMail;
	}

	public void setHasNewMail(boolean hasNewMail) {
		this.hasNewMail = hasNewMail;
	}

	public boolean isHasNewColl() {
		return hasNewColl;
	}

	public void setHasNewColl(boolean hasNewColl) {
		this.hasNewColl = hasNewColl;
	}
	
	public String getExtendHTML() {
		return extendHTML;
	}

	public void setExtendHTML(String extendHTML) {
		this.extendHTML = extendHTML;
	}

	public POSITION_TYPE getPosition() {
		return position;
	}

	public void setPosition(POSITION_TYPE position) {
		this.position = position;
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}

	public int getTdHeight() {
		return tdHeight;
	}

	public void setTdHeight(int tdHeight) {
		this.tdHeight = tdHeight;
	}

	public class Item extends ObjectToXMLBase implements Serializable {
		
		private static final long serialVersionUID = -4510710408773230061L;

		private String icon;

		private String name;

		private String subjectHTML;// HTML标题

		private String link;

		private String title;

		private Integer maxLength;

		private boolean hasAttachments;

		private Long optionId;

		private String optionEmail;

		private String showOption;// 是否显示操作菜单

		/**
		 * 链接打开方式
		 */
		private int openType = OPEN_TYPE.href.ordinal();

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIcon() {
			return icon;
		}

		/**
		 * 设置图标
		 * 
		 * @param icon 相对于站点根目录的路径，如：/apps_res/bbs/images/newb.gif
		 * 
		 */
		public void setIcon(String icon) {
			this.icon = icon;
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

		public String getSubjectHTML() {
			return subjectHTML;
		}

		public void setSubjectHTML(String subjectHTML) {
			this.subjectHTML = subjectHTML;
		}

		public int getOpenType() {
			return openType;
		}

		public void setOpenType(ChessboardTemplete.OPEN_TYPE openType) {
			this.openType = openType.ordinal();
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		/**
		 * 设置文本显示的最大字节数，可以不设置，则自动截取
		 */
		public void setMaxLength(Integer maxLength) {
			this.maxLength = maxLength;
		}

		public boolean isHasAttachments() {
			return hasAttachments;
		}

		public void setHasAttachments(boolean hasAttachments) {
			this.hasAttachments = hasAttachments;
		}

		public String getOptionEmail() {
			return optionEmail;
		}

		public void setOptionEmail(String optionEmail) {
			this.optionEmail = optionEmail;
		}

		public Long getOptionId() {
			return optionId;
		}

		public void setOptionId(Long optionId) {
			this.optionId = optionId;
		}

		public String getShowOption() {
			return showOption;
		}

		public void setShowOption(String showOption) {
			this.showOption = showOption;
		}

	}

}