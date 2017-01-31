package com.seeyon.v3x.mobile.message.dialect;

import java.util.Locale;

/**
 * 生成短信回复提示
 * 如：
 * 基础：回复2d3d+内容
 * 会议：回复ax3d+y/n+内容
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-3
 */
public interface MobileAppDialect {

	public String getAppDialect(Locale locale, String featureCode);
	
	public boolean parseRecieve(String content,Long objectId,Long senderId,Long srcId);
}
