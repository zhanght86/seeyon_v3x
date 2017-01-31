/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * 多行的，图标，分类，文本的展现形式<br>
 * 图表是32px * 32px的，在左边，右边的上面是分类(Category),下面是若干个项
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-27
 */
public class MultiIconCategoryItem extends BaseSectionTemplete {

	private static final long serialVersionUID = 271741199381648679L;

	/**
	 * 条目: 对应一个分类+分类下面的若干项 
	 */
	private List<MultiIconCategoryItem.Entry> entries;
	
	private int number = 4;
	
	/**
	 * 项的名称好图标是否在同意行显示 true-同一行 false-上图标下名称 
	 * 默认 - true
	 */
	private Boolean isItemTextIconSameRow;

	public Boolean getIsItemTextIconSameRow() {
		return isItemTextIconSameRow;
	}

	public void setIsItemTextIconSameRow(Boolean isItemTextIconSameRow) {
		this.isItemTextIconSameRow = isItemTextIconSameRow;
	}

	@Override
	public String getResolveFunction() {
		return "multiIconCategoryItem";
	}

	/**
	 * 添加一个条目
	 * 
	 * @return
	 */
	public MultiIconCategoryItem.Entry addEntry() {
		if (this.entries == null) {
			this.entries = new ArrayList<MultiIconCategoryItem.Entry>();
		}

		MultiIconCategoryItem.Entry enery = new MultiIconCategoryItem.Entry();
		this.entries.add(enery);

		return enery;
	}

	public List<MultiIconCategoryItem.Entry> getEntries() {
		return entries;
	}

	public int getNumber() {
		return number;
	}

	/**
	 * 设置栏目显示条目总数，默认4条
	 * 
	 * @param number
	 */
	public void setCategroyNumber(int number) {
		this.number = number;
	}

	/**
	 * 条目
	 * 
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-6-27
	 */
	public class Entry extends ObjectToXMLBase implements Serializable {
		private static final long serialVersionUID = -6697960213352183116L;

		/**
		 * 分类的图标 可以没有
		 */
		private String icon;

		/**
		 * 分类的名称
		 */
		private String category;

		/**
		 * 分类的链接
		 */
		private String categoryLink;

		/**
		 * 分类下面的项： 依次是 名字、链接、链接打开目标窗口、图标、图标尺寸
		 */
		private List<String[]> items;

		private int newLine;

		public String getCategory() {
			return category;
		}

		/**
		 * 分类，可以不设置
		 * 
		 * @param category 需要做国际，可以采用JS来实现，<code>SectionUtils.toJSI18N(String)</code>
		 * @param categoryLink
		 *            没有链接就用null
		 */
		public void setCategory(String category, String categoryLink) {
			this.category = category;
			this.categoryLink = categoryLink;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public int getNewLine() {
			return newLine;
		}

		/**
		 * 每几个换行，默认为0，表示不换行
		 * @param newLine
		 */
		public void setNewLine(int newLine) {
			this.newLine = newLine;
		}

		public List<String[]> getItems() {
			return items;
		}

		/**
		 * 添加项
		 * @param item 项名称 
		 * @param link 链接
		 * @param target 链接打开方式 默认_self
		 * @param icon 图标 可以为空
		 * @param iconX 图标大小PX
		 */
		public void addItem(String item, String link, String target, String icon, int iconX) {
			if (this.items == null) {
				this.items = new ArrayList<String[]>();
			}

			this.items.add(new String[] { item, link, target, icon, String.valueOf(iconX)});
		}

		public String getCategoryLink() {
			return categoryLink;
		}
	}
	
}
