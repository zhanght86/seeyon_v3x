package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.POSITION_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 关联系统栏目
 */
public class NewLinkSystemSection extends BaseSection {

	private static final Log log = LogFactory.getLog(NewLinkSystemSection.class);

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
		return "newLinkSystemSection";
	}
	
	@Override
	public String getBaseName() {
		return "newLinkSystem";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("newLinkSystem", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));

		int defaultCount = 32;
		int column = 4;
		Integer newLineStr = null;
		if ("list".equals(columnsStyle)) {
			newLineStr = newLine2Column.get(width);
		} else if ("picture".equals(columnsStyle)) {
			defaultCount = 15;
			column = 5;
			newLineStr = newLine2Column1.get(width);
		}
		if (newLineStr != null) {
			column = newLineStr.intValue();
		}
		int count = SectionUtils.getSectionCount(defaultCount, preference);
		

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
			List<LinkSystem> linkSystems = outerlinkManager.findAllLinkSystemByUser(CurrentUser.get().getId());

			if (linkSystems != null) {
				if (linkSystems.size() > count) {
					linkSystems = linkSystems.subList(0, count);
				} else {
					count = linkSystems.size();
				}
				
				int row = count / column + ((count % column > 0) ? 1 : 0);
				if ("list".equals(columnsStyle)) {
					if (row < 8) {
						row = 8;
					}
				}
				c.setLayout(row, column);

				for (LinkSystem system : linkSystems) {
					ChessboardTemplete.Item item = c.addItem();
					String url = "/linkManager.do?method=linkConnect&linkId=" + system.getId();
					String icon = system.getImage();
					if (Strings.isBlank(icon) || (icon.indexOf("default.gif") != -1)) {
						icon = "/apps_res/link/images/default.gif";
					} else {
						int start = icon.indexOf("/fileUpload.do");
						int end = icon.indexOf("width=") - 2;
						icon = icon.substring(start, end);
					}
					item.setIcon(icon);
					item.setName(system.getName());
					item.setLink(url);
					item.setOpenType(OPEN_TYPE.href_blank);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		c.addBottomButton("set_link_system", "/linkManager.do?method=userLinkMain");
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/linkManager.do?method=linkMore");

		return c;
	}

}