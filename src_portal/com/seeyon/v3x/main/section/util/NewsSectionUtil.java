package com.seeyon.v3x.main.section.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MoveMultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.OneImageAndListTemplete;
import com.seeyon.v3x.main.section.templete.PictureTemplete;
import com.seeyon.v3x.main.section.templete.PictureTitleAndBriefTemplete;
import com.seeyon.v3x.main.section.templete.PortalPicture;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.PictureTemplete.Picture;
import com.seeyon.v3x.main.section.templete.PictureTitleAndBriefTemplete.PictureData;
import com.seeyon.v3x.news.domain.NewsBody;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class NewsSectionUtil {
	
	private static final Log log = LogFactory.getLog(NewsSectionUtil.class);
	
	private static final String newIcon = "/apps_res/v3xmain/images/section/new.gif";
	
	private static SpaceManager spaceManager = null;

	private static SpaceManager getSpaceManager() {
		if (spaceManager == null) {
			spaceManager = (SpaceManager) ApplicationContextHolder.getBean("spaceManager");
		}

		return spaceManager;
	}
	
	/**
	 * 空间新闻栏目显示
	 * 
	 * @param preference
	 * @param columnsStyle 栏目样式
	 * @param newsDatas 栏目数据
	 * @param spaceType 空间类型：单位/集团/自定义团队空间/自定义单位空间/自定义集团空间
	 * @param single 是否单板块
	 * @param newsDataManager
	 * @param fileManager
	 * @return
	 */
	public static BaseSectionTemplete setNewsSectionData(Map<String, String> preference, String columnsStyle, String panelValue, List<NewsData> newsDatas, int spaceType, boolean single, NewsDataManager newsDataManager, FileManager fileManager) {
		String spaceId = preference.get(PropertyName.ownerId.name());
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		int rand = new Random().nextInt();
		
		String group = spaceType == 0 ? "&group=group" : "";
		String orgType = "";
		String paramSpaceId = "";
		if (spaceType == NewsTypeSpaceType.group.ordinal()) {
			orgType = "&orgType=group";
		} else if (spaceType == NewsTypeSpaceType.corporation.ordinal()) {
			orgType = "&orgType=account";
		} else {
			orgType = "&orgType=publicCustom";
			paramSpaceId = "&spaceId=" + spaceId;
		}
		String nameLink = "/newsData.do?method=userView&random=" + rand + paramSpaceId + "&id=";
		String typeLink = "/newsData.do?method=newsMore&from=top" + orgType + "&spaceType=" + spaceType + paramSpaceId + "&typeId=";
		String indexLink = "/newsData.do?method=index" + group + "&spaceType=" + spaceType + paramSpaceId + "&where=space";
		String moreLink = "";
		if (single) {
			moreLink = "/newsData.do?method=newsMore&from=top" + orgType + "&spaceType=" + spaceType + "&typeId=" + boardId;
		} else {
			moreLink = "/newsData.do?method=newsMore&from=top" + orgType + "&spaceType=" + spaceType  + "&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name()) + paramSpaceId;
			if (panelValue != null) {
				moreLink += "&panelValue=" + panelValue;
			}
		}
		
		boolean isCustom = false;
		String spaceName = "";
		if (spaceType == NewsTypeSpaceType.custom.ordinal()) {
			isCustom = true;
			SpaceFix fix = getSpaceManager().getSpace(NumberUtils.toLong(spaceId));
			if (fix != null) {
				spaceName = fix.getSpaceName();
			}
		}

		if ("list".equals(columnsStyle)) {// 列表
			MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
			String[] rows = SectionUtils.getRowList("subject,publishDate,type", preference);
			c.addRowName("subject");
			for (String row : rows) {
				c.addRowName(row);
			}

			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (NewsData newsData : newsDatas) {
					setMultiRowData(c.addRow(), newsData, single, nameLink, typeLink, isCustom, spaceName);
				}
			}

			c.addBottomButton("news_index_label", indexLink);
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return c;
		} else if ("imageandlist".equals(columnsStyle)) {// 头条图片摘要+列表
			OneImageAndListTemplete t = new OneImageAndListTemplete();
			String[] rows = SectionUtils.getRowList("subject,publishDate", preference);
			t.addRowName("subject");
			for (String row : rows) {
				t.addRowName(row);
			}
			
			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (int i = 0; i < newsDatas.size(); i++) {
					NewsData newsData = newsDatas.get(i);
					if (i == 0) {
						String brief = newsData.getBrief();
						if (Strings.isBlank(brief)) {
							NewsBody body = newsDataManager.getBody(newsData.getId());
							String content = body.getContent();
							if ("HTML".equals(body.getBodyType()) && Strings.isNotBlank(content)) {
								brief = StrExtractor.getHTMLContent(content);
							}
						}
						V3XFile file = null;
						try {
							file = fileManager.getV3XFile(newsData.getImageId());
						} catch (BusinessException e) {
							log.error("文件不存在：", e);
						}
						String fileId = "";
						String imageDate = "";
						if (file != null) {
							fileId = file.getId().toString();
							imageDate = Datetimes.formatDate(file.getCreateDate());
						}
						String categoryLabel = null;
						String categoryLink = null;
						if (single) {
							categoryLabel = Functions.showMemberName(newsData.getCreateUser());
						} else {
							categoryLabel = isCustom ? spaceName : newsData.getType().getTypeName();
							categoryLink = typeLink + newsData.getType().getId();
						}
						t.setFirstRow(newsData.getTitle(), brief, fileId, imageDate, nameLink + newsData.getId(), OPEN_TYPE.href_blank, categoryLabel, categoryLink, newsData.getPublishDate());
					} else {
						setMultiRowData(t.addRow(), newsData, single, nameLink, typeLink, isCustom, spaceName);
					}
				}
			}
			
			t.addBottomButton("news_index_label", indexLink);
			t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return t;
		} else if ("image".equals(columnsStyle)) {// 图片播放
			PictureTemplete t = new PictureTemplete();
			t.setModel(PictureTemplete.Model.index);
			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (NewsData newsData : newsDatas) {
					V3XFile file = null;
					try {
						file = fileManager.getV3XFile(newsData.getImageId());
					} catch (BusinessException e) {
						log.error("文件不存在：", e);
					}
					Long fileId = 0L;
					Date createDate = new Date();
					if (file != null) {
						fileId = file.getId();
						createDate = file.getCreateDate();
					}
					Picture picture = t.addPicture(fileId, createDate);
					picture.setSubject(newsData.getTitle());
					picture.setLink("javascript:openBlank('/seeyon/" + nameLink + newsData.getId() + "', 'workSpaceRight')");
				}
			}
			return t;
		} else if ("imageLeft".equals(columnsStyle)) {// 图片向左滚动播放
			PictureTemplete t = new PictureTemplete();
			t.setModel(PictureTemplete.Model.leftScroll);
			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (NewsData newsData : newsDatas) {
					V3XFile file = null;
					try {
						file = fileManager.getV3XFile(newsData.getImageId());
					} catch (BusinessException e) {
						log.error("文件不存在：", e);
					}
					Long fileId = 0L;
					Date createDate = new Date();
					if (file != null) {
						fileId = file.getId();
						createDate = file.getCreateDate();
					}
					Picture picture = t.addPicture(fileId, createDate);
					picture.setSubject(newsData.getTitle());
					picture.setLink("javascript:openBlank('/seeyon/" + nameLink + newsData.getId() + "', 'workSpaceRight')");
				}
			}
			return t;
		} else if ("imageandtitle".equals(columnsStyle)) {// 标题摘要
			PictureTitleAndBriefTemplete t = new PictureTitleAndBriefTemplete();
			String[] rows = SectionUtils.getRowList("subject,publishDate", preference);
			t.addRowName("subject");
			for (String row : rows) {
				t.addRowName(row);
			}

			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (NewsData newsData : newsDatas) {
					PictureData data = t.addData();
					PortalPicture pic = new PortalPicture();
					V3XFile file = null;
					try {
						file = fileManager.getV3XFile(newsData.getImageId());
					} catch (BusinessException e) {
						log.error("文件不存在：", e);
					}
					if (file != null) {
						pic.setPicId(file.getId().toString());
						pic.setCreateDate(Datetimes.formatDate(file.getCreateDate()));
					}
					data.setPicture(pic);
					data.setSubject(newsData.getTitle());
					String brief = newsData.getBrief();
					if (Strings.isBlank(brief)) {
						NewsBody body = newsDataManager.getBody(newsData.getId());
						String content = body.getContent();
						if ("HTML".equals(body.getBodyType()) && Strings.isNotBlank(content)) {
							brief = StrExtractor.getHTMLContent(content);
						}
					}
					data.setBrief(brief);
					data.setLink(nameLink + newsData.getId(), OPEN_TYPE.href_blank);
					data.setType(isCustom ? spaceName : newsData.getType().getTypeName(), typeLink + newsData.getType().getId());
					data.setCreateDate(Datetimes.formatDate(newsData.getPublishDate()));
				}
			}
			return t;
		} else if ("move".equals(columnsStyle)) {// 滚动列表
			MoveMultiRowThreeColumnTemplete t = new MoveMultiRowThreeColumnTemplete();
			String[] rows = SectionUtils.getRowList("subject,publishDate,type", preference);
			t.addRowName("subject");
			for (String row : rows) {
				t.addRowName(row);
			}
			
			if (CollectionUtils.isNotEmpty(newsDatas)) {
				for (NewsData newsData : newsDatas) {
					setMultiRowData(t.addRow(), newsData, single, nameLink, typeLink, isCustom, spaceName);
				}
			}
			
			t.addBottomButton("news_index_label", indexLink);
			t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
			return t;
		}
		
		return null;
	}
	
	public static void setMultiRowData(MultiRowThreeColumnTemplete.Row row, NewsData newsData, boolean single, String nameLink, String typeLink, boolean isCustom, String spaceName) {
		row.setSubject(newsData.getTitle());
		row.setBodyType(newsData.getDataFormat());
		row.setHasAttachments(newsData.getAttachmentsFlag());
		row.setLink(nameLink + newsData.getId(), OPEN_TYPE.href_blank);
		if (newsData.getTopOrder() > 0) {
			row.addExtIcons(newIcon);
		}
		if (single) {
			row.setCategory(Functions.showMemberName(newsData.getCreateUser()), null);
		} else {
			row.setCategory(isCustom ? spaceName : newsData.getType().getTypeName(), typeLink + newsData.getType().getId());
		}
		row.setCreateDate(newsData.getPublishDate());
		row.setClassName(BooleanUtils.isTrue(newsData.getReadFlag()) ? "AlreadyReadByCurrentUser" : "ReadDifferFromNotRead");
	}
	
	/**
	 * 栏目样式对应的显示条数
	 * @param preference
	 * @param columnsStyle
	 * @return
	 */
	public static int getSectionCount(Map<String, String> preference, String columnsStyle) {
		int defaultCount = 8;
		if ("imageandlist".equals(columnsStyle)) {// 头条图片摘要+列表
			defaultCount = 5;
		} else if ("image".equals(columnsStyle)) {// 图片播放
			defaultCount = 5;
		} else if ("imageLeft".equals(columnsStyle)) {// 图片向左滚动播放
			defaultCount = 5;
		} else if ("imageandtitle".equals(columnsStyle)) {// 标题摘要
			defaultCount = 2;
		}
		return SectionUtils.getSectionCount(defaultCount, preference);
	}
	
}