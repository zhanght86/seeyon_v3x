package com.seeyon.v3x.main.section;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete.ModelType;
import com.seeyon.v3x.notice.domain.Notice;
import com.seeyon.v3x.notice.manager.NoticeManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;

/**
 * 公示板栏目
 */
public class NoticeSection extends BaseSection {
	
	private static Log log = LogFactory.getLog(NoticeSection.class);
	
	private SpaceManager spaceManager;

	private NoticeManager noticeManager;
	
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setNoticeManager(NoticeManager noticeManager) {
		this.noticeManager = noticeManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "noticeSection";
	}
	
	@Override
	public String getBaseName() {
		return "notice";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("notice", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String spaceType = preference.get(PortletEntityProperty.PropertyName.spaceType.name());
		// 部门空间的部门ID，单位空间的单位ID，集团空间的集团ID(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)，自定义团队、自定义单位、自定义集团空间的空间ID
		String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
		String fragmentId = preference.get(PropertyName.entityId.name());
		String ordinal = preference.get(PropertyName.ordinal.name());
		String boardId = preference.get(PropertyName.singleBoardId.name());
		int height = this.getSectionProperty(208, preference, "height");
		int fontSize = this.getSectionProperty(0, preference, "fontSize");
		int fontStyle = this.getSectionProperty(0, preference, "fontStyle");
		int fontColor = this.getSectionProperty(9, preference, "fontColor");
		if (fontColor == 0) {
			fontColor = 9;
		}
		String style = "notice_fontSize_" + fontSize + " notice_fontStyle_" + fontStyle + " notice_fontColor_" + fontColor;

		Notice notice = noticeManager.getByBoardId(NumberUtils.toLong(boardId));

		HtmlTemplete ht = new HtmlTemplete();
		StringBuffer html = new StringBuffer();
		String flag = String.valueOf(Math.random());
		flag = flag.substring(2, flag.length());
		html.append("<div class=\"messageReplyDiv\">");
		html.append("<input id=\"messageReplyDivHidden" + flag + "\" type=\"hidden\" value=\"" + (notice != null ? Functions.toHTML(notice.getParamValue()) : "") + "\"/>");
		html.append("<div class=\"replyDivHidden\" id=\"replyDiv" + flag + "\"></div>");
		html.append("<div id='" + flag + "' class=\"leaveMessageContainer default\" style=\"white-space:normal; word-break:break-all; height:" + (height - 33) +"px;\">");
		html.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed;word-wrap:break-word;word-break:break-all;\"><tr><td valign=\"top\" class=\"" + style + "\" style=\"table-layout:fixed;word-wrap:break-word;word-break:break-all;\">");
		if (notice != null) {
			html.append(Functions.toHTML(notice.getParamValue()));
		}
		html.append("</td></tr></table>");
		html.append("</div>");
		html.append("</div>");

		ht.setHtml(html.toString());
		ht.setHeight(String.valueOf(height));
		ht.setModel(ModelType.block);
		
		User user = CurrentUser.get();
		boolean isSpaceManager = false;
		try {
			isSpaceManager = spaceManager.isManagerOfThisSpace(user.getId(), NumberUtils.toLong(ownerId));
		} catch (Exception e) {
			log.error("", e);
		}

		if (isSpaceManager) {
			ht.addBottomButton("post_content", "javascript:showNoticeDiv('" + flag + "', '" + spaceType + "', '" + ownerId + "', '" + fragmentId + "', '" + ordinal + "', '" + boardId + "')");
		}
		return ht;
	}

	private int getSectionProperty(int defaultValue, Map<String, String> preference, String property) {
		String value = preference.get(property);
		if (Strings.isNotBlank(value)) {
			return NumberUtils.toInt(value);
		}
		return defaultValue;
	}

}