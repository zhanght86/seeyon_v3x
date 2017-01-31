package com.seeyon.v3x.cluster.listener;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.util.annotation.HandleNotification;

public class IndexHandler {
	protected static final Log logger = LogFactory.getLog(IndexHandler.class);

	@HandleNotification(type = NotificationType.IndexWriteProperties)
	public void writeProperties(Object o) {
		try {
			if ((o != null) && (o instanceof Properties)) {
				Properties prop = (Properties) o;
				IndexPropertiesUtil.getInstance().writeProperties(prop);
				if (logger.isDebugEnabled()) {
					logger.debug("保存propeties成功:" + prop.toString());
				}
			}else{
				logger.warn("不可识别的消息参数："+o);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
