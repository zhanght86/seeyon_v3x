package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.POSITION_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 单版块关联系统栏目
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-8
 * 
 */
public class SingleBoardLinkSection extends BaseSection {
	
	private static Log log = LogFactory.getLog(SingleBoardLinkSection.class);
	
	private OuterlinkManager outerlinkManager;

	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> newLine2Column1 = new HashMap<Integer, Integer>();
	
	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}
	
	public void setNewLine2Column1(Map<String, String> newLine2Column1) {
		Set<Map.Entry<String, String>> en = newLine2Column1.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column1.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardLinkSection";
	}

	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		Long categoryId = NumberUtils.toLong(singleBoardId);
		try {
			LinkCategory category = outerlinkManager.getCategoryById(categoryId);
			return category != null;
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}
	
	@Override
	public String getBaseName(Map<String, String> preference) {
		Long categoryId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		try {
			LinkCategory category = outerlinkManager.getCategoryById(categoryId);
			if (category != null) {
				ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.link.i18n.LinkResource", CurrentUser.get().getLocale());
				return ResourceBundleUtil.getString(rb, category.getName());
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	@Override
	public String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}

		Long categoryId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		try {
			ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.link.i18n.LinkResource", CurrentUser.get().getLocale());
			LinkCategory category = outerlinkManager.getCategoryById(categoryId);
			if (category != null) {
				return ResourceBundleUtil.getString(rb, category.getName());
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("picture", preference);
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));

		int defaultCount = 15;
		int column = 5;
		Integer newLineStr = null;
		if ("list".equals(columnsStyle)) {
			defaultCount = 32;
			column = 4;
			newLineStr = newLine2Column.get(width);
		} else if ("picture".equals(columnsStyle)) {
			newLineStr = newLine2Column1.get(width);
		}
		if (newLineStr != null) {
			column = newLineStr.intValue();
		}
		int count = SectionUtils.getSectionCount(defaultCount, preference);
		

		Long categoryId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));

		ChessboardTemplete c = new ChessboardTemplete();
		
		if ("list".equals(columnsStyle)) {
			c.setPosition(POSITION_TYPE.left);
		} else if ("picture".equals(columnsStyle)) {
			c.setPosition(POSITION_TYPE.top);
			c.setIconWidth(32);
			c.setIconHeight(32);
			c.setTdHeight(70);
		}
		try {
			List<LinkSystem> inners = outerlinkManager.findOutLinkBySize(CurrentUser.get().getId(), count, categoryId);

			if (inners != null) {
				if (inners.size() < count) {
					count = inners.size();
				}
				int row = count / column + ((count % column > 0) ? 1 : 0);
				if ("list".equals(columnsStyle)) {
					if (row < 8) {
						row = 8;
					}
				}else if("picture".equals(columnsStyle)){
					if(row < 3){
						row = 3;
					}
				}
				c.setLayout(row, column);
				
				for (LinkSystem lsin : inners) {
					ChessboardTemplete.Item item = c.addItem();
					String url = "/linkManager.do?method=linkConnect&linkId=" + lsin.getId();
					String icon = lsin.getImage();
					if (Strings.isBlank(icon) || (icon.indexOf("default.gif") != -1)) {
						icon = "/apps_res/link/images/default.gif";
					} else {
						int start = icon.indexOf("/fileUpload.do");
						int end = icon.indexOf("width=") - 2;
						icon = icon.substring(start, end);
					}
					item.setIcon(icon);
					item.setName(lsin.getName());
					item.setLink(url);
					item.setOpenType(OPEN_TYPE.href_blank);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		
		if (categoryId.longValue() == com.seeyon.v3x.link.util.Constants.LINK_CATEGORY_COMMON_ID) {
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/linkManager.do?method=commonLinkMore&status=0");
		} else {
			c.addBottomButton("set_link_system", "/linkManager.do?method=userLinkMain");
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/linkManager.do?method=linkMore");
		}

		return c;
	}

}