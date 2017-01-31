package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;

/**
 * 我的公告：我能访问的所有公告
 * 
 * @author renhy
 * 
 */
public class MyBulletinSection extends BaseSection {
	private static Log log = LogFactory.getLog(MyBulletinSection.class);

	private BulDataManager bulDataManager;

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "myBulletinSection";
	}
	
	@Override
	public String getBaseName() {
		return "myBulletinSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return "myBulletinSection";
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		User user = CurrentUser.get();
		List<BulData> bulDatas = null;

		try {
			bulDatas = bulDataManager.findMyBulDatas(user, null, true);
		} catch (Exception e) {
			log.error("我的公告-读取公告列表异常:", e);
		}

		MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();

		int rand = new Random().nextInt();
		if (bulDatas != null) {
			String type = null;
			for (BulData bulData : bulDatas) {
				Integer spaceType = bulData.getType().getSpaceType();
				type = "(" + ResourceBundleUtil.getString(this.getResourceBundle(), "space." + spaceType + ".name") + ")";

				MultiRowThreeColumnTemplete.Row row = c.addRow();

				int maxLength = 36;
				if (bulData.getAttachmentsFlag()) {
					row.setHasAttachments(true);
					maxLength -= 2;
				}
				row.setSubject(bulData.getTitle());
				row.setBodyType(StringUtils.isBlank(bulData.getExt5()) ? bulData.getDataFormat() : com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
				row.setLink("/bulData.do?method=userView&id=" + bulData.getId() + "&random=" + rand, OPEN_TYPE.href_blank);
				row.setCreateDate(bulData.getPublishDate());
				row.setCategory(type + bulData.getTypeName(), "/bulData.do?method=bulMore&spaceType=" + spaceType + "&typeId=" + bulData.getType().getId());
				
				// 设置已阅或未读样式
				if (bulData.getReadFlag() != null && bulData.getReadFlag().booleanValue()) {
					row.setClassName("AlreadyReadByCurrentUser");
				} else {
					row.setClassName("ReadDifferFromNotRead");
				}
			}
		}
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bulData.do?method=myBulMore");
		return c;
	}
}
