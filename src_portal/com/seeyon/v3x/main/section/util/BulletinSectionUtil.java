package com.seeyon.v3x.main.section.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MoveMultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.PictureTitleAndBriefTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.PictureTitleAndBriefTemplete.PictureData;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class BulletinSectionUtil {

	/**
	 * 空间公告栏目显示
	 * 
	 * @param preference
	 * @param bulDatas 栏目数据
	 * @param spaceType 空间类型：单位/集团/自定义团队空间/自定义单位空间/自定义集团空间
	 * @param single 是否单板块
	 * @param bulDataManager
	 * @return
	 */
	public static BaseSectionTemplete setBulSectionData(Map<String, String> preference, String panelValue, List<BulData> bulDatas, int spaceType, boolean single, BulDataManager bulDataManager) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		String[] rows = SectionUtils.getRowList("subject,publishDate,type", preference);
		String spaceId = preference.get(PropertyName.ownerId.name());
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		int rand = new Random().nextInt();
		
		String group = spaceType == 0 ? "&group=group" : "";
		String paramSpaceId = "";
		String orgType = "";
		if (spaceType == NewsTypeSpaceType.group.ordinal()) {
			orgType = "&orgType=group";
		} else if (spaceType == NewsTypeSpaceType.corporation.ordinal()) {
			orgType = "&orgType=account";
		} else {
			orgType = "&orgType=publicCustom";
			paramSpaceId = "&spaceId=" + spaceId;
		}
		String nameLink = "/bulData.do?method=userView&random=" + rand + paramSpaceId + "&id=";
		String typeLink = "/bulData.do?method=bulMore&from=top" + orgType + "&spaceType=" + spaceType + paramSpaceId + "&typeId=";
		String indexLink = "/bulData.do?method=index" + group + "&spaceType=" + spaceType + paramSpaceId + "&where=space";
		String moreLink = "";
		if (single) {
			moreLink = "/bulData.do?method=bulMore&from=top" + orgType + "&spaceType=" + spaceType + "&typeId=" + boardId;
		} else {
			moreLink = "/bulData.do?method=bulMore&from=top" + orgType + "&spaceType=" + spaceType + "&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name()) + paramSpaceId;
			if (panelValue != null) {
				moreLink += "&panelValue=" + panelValue;
			}
		}
		
		if ("list".equals(columnsStyle)) {// 列表
			MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
			c.addRowName("subject");
			for (String row : rows) {
				c.addRowName(row);
			}

			setMultiRowData(c, bulDatas, single, nameLink, typeLink);

			c.addBottomButton("bulletin_index_label", indexLink);
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return c;
		} else if ("imageandlist".equals(columnsStyle)) {// 标题摘要
			PictureTitleAndBriefTemplete t = new PictureTitleAndBriefTemplete();
			rows = SectionUtils.getRowList("subject,publishDate", preference);
			t.addRowName("subject");
			for (String row : rows) {
				t.addRowName(row);
			}

			if (CollectionUtils.isNotEmpty(bulDatas)) {
				for (BulData bulData : bulDatas) {
					PictureData data = t.addData();
					data.setSubject(bulData.getTitle());
					String brief = "";
					BulBody body = bulDataManager.getBody(bulData.getId());
					String content = body.getContent();
					if ("HTML".equals(body.getBodyType()) && Strings.isNotBlank(content)) {
						brief = StrExtractor.getHTMLContent(content);
					}
					data.setBrief(brief);
					data.setLink(nameLink + bulData.getId(), OPEN_TYPE.href_blank);
					if (single) {
						data.setType(Functions.showMemberName(bulData.getCreateUser()), null);
					} else {
						data.setType(bulData.getTypeName(), typeLink + bulData.getType().getId());
					}
					data.setCreateDate(Datetimes.formatDate(bulData.getPublishDate()));
				}
			}

			t.addBottomButton("bulletin_index_label", indexLink);
			t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return t;
		} else if ("move".equals(columnsStyle)) {// 滚动列表
			MoveMultiRowThreeColumnTemplete m = new MoveMultiRowThreeColumnTemplete();
			m.addRowName("subject");
			for (String row : rows) {
				m.addRowName(row);
			}
			
			setMultiRowData(m, bulDatas, single, nameLink, typeLink);
			
			m.addBottomButton("bulletin_index_label", indexLink);
			m.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return m;
		}
		
		return null;
	}

	public static void setMultiRowData(MultiRowThreeColumnTemplete t, List<BulData> bulDatas, boolean single, String nameLink, String typeLink) {
		if (CollectionUtils.isNotEmpty(bulDatas)) {
			for (BulData bulData : bulDatas) {
				MultiRowThreeColumnTemplete.Row row = t.addRow();
				if (single && bulData.getTopOrder() > 0) {
					row.setSubjectHTML(BulletinUtils.getTopedBulTitleHtml(bulData));
				} else {
					row.setSubject(bulData.getTitle());
					row.setBodyType(StringUtils.isBlank(bulData.getExt5()) ? bulData.getDataFormat() : com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
					row.setHasAttachments(bulData.getAttachmentsFlag());
				}
				row.setLink(nameLink + bulData.getId(), OPEN_TYPE.href_blank);
				if (single) {
					row.setCategory(Functions.showMemberName(bulData.getCreateUser()), null);
				} else {
					row.setCategory(bulData.getTypeName(), typeLink + bulData.getType().getId());
				}
				row.setCreateDate(bulData.getPublishDate());
				row.setClassName(BooleanUtils.isTrue(bulData.getReadFlag()) ? "AlreadyReadByCurrentUser" : "ReadDifferFromNotRead");
			}
		}
	}
	
	/**
	 * 栏目样式对应的显示条数
	 * @param preference
	 * @param columnsStyle
	 * @return
	 */
	public static int getSectionCount(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		int defaultCount = 8;
		if ("imageandlist".equals(columnsStyle)) {// 标题摘要
			defaultCount = 3;
		}
		return SectionUtils.getSectionCount(defaultCount, preference);
	}

}