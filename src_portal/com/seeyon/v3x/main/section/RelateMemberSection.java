package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.POSITION_TYPE;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Datetimes;

/**
 * 关联人员栏目
 */
public class RelateMemberSection extends BaseSection {

	private static final Log log = LogFactory.getLog(RelateMemberSection.class);

	private PeopleRelateManager peoplerelateManager;
	
	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> newLine2Column1 = new HashMap<Integer, Integer>();

	public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager) {
		this.peoplerelateManager = peoplerelateManager;
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
		return "relateMemberSection";
	}
	
	@Override
	public String getBaseName() {
		return "relateMember";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("relateMember", preference);
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
			defaultCount = 10;
			column = 5;
			newLineStr = newLine2Column1.get(width);
		}
		if (newLineStr != null) {
			column = newLineStr.intValue();
		}
		int count = SectionUtils.getSectionCount(defaultCount, preference);

		String panel = SectionUtils.getPanel("all", preference);
		String panelValue = null;
		String designated = "1,2,3,4";
		if ("designated".equals(panel)) {
			panelValue = panel + "_value";
			designated = preference.get(panelValue);
		}

		ChessboardTemplete c = new ChessboardTemplete();
		c.setHasNewMail(MenuFunction.hasNewMail());
		c.setHasNewColl(MenuFunction.hasNewCollaboration());
		if ("list".equals(columnsStyle)) {
			c.setPosition(POSITION_TYPE.left);
		} else if ("picture".equals(columnsStyle)) {
			c.setPosition(POSITION_TYPE.top);
			c.setIconWidth(50);
			c.setIconHeight(60);
			c.setTdHeight(104);
		}
		int loopCount = 0;

		try {
			Map<RelationType, List<PeopleRelate>> peopleRelatesList = peoplerelateManager.getAllPeopleRelates(CurrentUser.get().getId(), "picture".equals(columnsStyle), designated);
			List<PeopleRelate> leaderlist = peopleRelatesList.get(RelationType.leader);
			List<PeopleRelate> assistantlist = peopleRelatesList.get(RelationType.assistant);
			List<PeopleRelate> juniorlist = peopleRelatesList.get(RelationType.junior);
			List<PeopleRelate> confrerelist = peopleRelatesList.get(RelationType.confrere);

			int total = leaderlist.size() + assistantlist.size() + juniorlist.size() + confrerelist.size();
			if (total < count) {
				count = total;
			}
			int row = count / column + ((count % column > 0) ? 1 : 0);
			if ("list".equals(columnsStyle)) {
				if (row < 8) {
					row = 8;
				}
			}else{
				if(row < 2){
					row = 2;
				}
			}
			c.setLayout(row, column);
			
			for (PeopleRelate peopleRelate : leaderlist) {
				if (loopCount >= count) {
					break;
				}
				this.addPeopleRelateItem(peopleRelate, c, columnsStyle);
				loopCount++;
			}

			for (PeopleRelate peopleRelate : assistantlist) {
				if (loopCount >= count) {
					break;
				}
				this.addPeopleRelateItem(peopleRelate, c, columnsStyle);
				loopCount++;
			}

			for (PeopleRelate peopleRelate : juniorlist) {
				if (loopCount >= count) {
					break;
				}
				this.addPeopleRelateItem(peopleRelate, c, columnsStyle);
				loopCount++;
			}

			for (PeopleRelate peopleRelate : confrerelist) {
				if (loopCount >= count) {
					break;
				}
				this.addPeopleRelateItem(peopleRelate, c, columnsStyle);
				loopCount++;
			}
		} catch (Exception e) {
			log.error("", e);
		}

		c.addBottomButton("set_relate_member", "/relateMember.do?method=relate");
		
		String moreLink = "/relateMember.do?method=relateMore";
		if (panelValue != null) {
			moreLink += "&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name()) + "&panelValue=" + panelValue;
		}
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);

		return c;
	}

	private void addPeopleRelateItem(PeopleRelate peopleRelate, ChessboardTemplete c, String columnsStyle) {
		ChessboardTemplete.Item item = c.addItem();
		Long relateMemberId = peopleRelate.getRelateMemberId();
		if ("list".equals(columnsStyle)) {
			if (peopleRelate.getRelateType() == RelationType.leader.key()) {
				item.setIcon("/apps_res/v3xmain/images/section/leader.gif");
			} else if (peopleRelate.getRelateType() == RelationType.assistant.key()) {
				item.setIcon("/apps_res/v3xmain/images/section/assistant.gif");
			} else if (peopleRelate.getRelateType() == RelationType.junior.key()) {
				item.setIcon("/apps_res/v3xmain/images/section/junior.gif");
			} else if (peopleRelate.getRelateType() == RelationType.confrere.key()) {
				item.setIcon("/apps_res/v3xmain/images/section/relatemember.gif");
			}
		} else if ("picture".equals(columnsStyle)) {
			String icon = "/apps_res/hr/images/photo.JPG";
			if (peopleRelate.getRelateImageId() != null) {
				icon = "/fileUpload.do?method=showRTE&fileId=" + peopleRelate.getRelateImageId() + "&createDate=" + Datetimes.formatDate(peopleRelate.getRelateImageDate()) + "&type=image";
			}
			item.setIcon(icon);
		}
		item.setName(peopleRelate.getRelateMemberName());
		item.setTitle(Functions.showMemberAlt(relateMemberId));
		item.setLink("/relateMember.do?method=relateMemberInfo&memberId=" + relateMemberId + "&relatedId=" + peopleRelate.getRelatedMemberId());
		item.setShowOption("1");
		item.setOptionId(relateMemberId);
		item.setOptionEmail(peopleRelate.getRelateMemberEmail());
		item.setMaxLength(9);
	}

}