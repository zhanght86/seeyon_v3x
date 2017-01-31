package com.seeyon.v3x.main.section;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 横幅栏目
 */
public class BannerSection extends BaseBannerSection {

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "banner";
	}

	@Override
	public String getBaseName() {
		return "Banner";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return "Banner";
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		return null;
	}

	public String getHTML(String entityId, String ordinal, String spaceType, String ownerId, Long spaceId) {
		Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, null, null, null);
		int columnsStyle = this.getSectionProperty(0, preference, "columnsStyle");
		int height = this.getSectionProperty(60, preference, "height");
		int fontSize = this.getSectionProperty(4, preference, "fontSize");
		int fontStyle = this.getSectionProperty(1, preference, "fontStyle");
		int fontColor = this.getSectionProperty(0, preference, "fontColor");
		String style = "notice_fontSize_" + fontSize + " notice_fontStyle_" + fontStyle + " notice_fontColor_" + fontColor;

		String path = SystemEnvironment.getA8ContextPath();

		String slogan = preference.get("slogan");
		if (slogan==null) {
			slogan = Constants.getSloganKey();
		}
		slogan = Functions.toHTML(ResourceBundleUtil.getString(getResourceBundle(), slogan));
		if (columnsStyle == 1) {
			slogan = "<marquee behavior='scroll' direction='left' scrollamount='2'>" + slogan + "</marquee>";
		} else if (columnsStyle == 2) {
			slogan = "<marquee behavior='scroll' direction='right' scrollamount='2'>" + slogan + "</marquee>";
		}

		String resSuff = Functions.resSuffix();
		String banner = preference.get("background");
		String defaultBanner = "/apps_res/v3xmain/images/banner/space_banner.gif";
		if (Strings.isBlank(banner)) {
			banner = defaultBanner;
		} else if (!defaultBanner.equals(banner)) {
			String[] banners = banner.split(",");
			if (banners.length == 2) {
				banner = "/fileUpload.do?method=showRTE&fileId=" + banners[0] + "&createDate=" + banners[1] + "&type=image";
				resSuff = "";
			} else {
				banner = defaultBanner;
			}
		}

		return PortalConstants.getString("portal.bannner", path, banner, slogan, style, resSuff, height);
	}

	private int getSectionProperty(int defaultValue, Map<String, String> preference, String property) {
		String value = preference.get(property);
		if (Strings.isNotBlank(value)) {
			return NumberUtils.toInt(value);
		}
		return defaultValue;
	}

}