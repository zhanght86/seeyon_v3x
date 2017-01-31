package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.bulletin.controller.BulDataController;
import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.bulletin.util.BulDataLock;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.util.annotation.HandleNotification;
/**
 * 公告监听，包括增加、修改公告板块以及在修改或审核公告时的加锁、解锁
 */
public class BulletinNotificationHandler {
	private static final Log logger = LogFactory.getLog(BulletinNotificationHandler.class);
	private BulDataController bulDataController;
	private BulDataManager bulDataManager;
	private BulTypeManager bulTypeManager;

	@HandleNotification(type=NotificationType.BulletinAddType)
	public void addBulletinType(Object o) {
		try {
			if(o!=null && o instanceof BulType) {
				BulType type = (BulType) o;
				this.getBulTypeManager().initPartAdd(type);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机增加公告板块成功，增加的板块信息为：\n" + BeanUtils.describe(this.getBulTypeManager().getById(type.getId())));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机增加公告板块时出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.BulletinUpdateType)
	public void updateBulletinType(Object o) {
		try {
			if(o!=null && o instanceof BulType) {
				BulType type = (BulType) o;
				this.getBulTypeManager().initPartEdit(type);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机修改公告板块成功，修改后的板块信息为：\n" + BeanUtils.describe(this.getBulTypeManager().getById(type.getId())));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机修改公告板块时出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.BulletinLock)
	public void lockBulletinWhenEditOrAudit(Object o) {
		try {
			if(o!=null && o instanceof BulDataLock) {
				BulDataLock lock = (BulDataLock) o;
				this.getBulDataManager().lock(lock.getNewsid(), lock.getUserid(), lock.getAction());
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机公告加锁动作成功，锁信息为：\n" + BeanUtils.describe(lock));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机公告加锁出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.BulletinUnLock)
	public void unlockBulletinAfterEditOrAduit(Object o) {
		try {
			if(o!=null) {
				Long bulletinId = (Long) o;
				this.getBulDataManager().unlock(bulletinId);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机公告解锁动作[公告ID：" + bulletinId + "]成功.");
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机公告解锁出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.BulDataClickArticle)
	public void clickBulletin(Object o) {
		try {
			if(o instanceof CacheInfo && o != null) {
				CacheInfo info = (CacheInfo) o;
				Long dataId = info.getDataId();
				Long userId = info.getUserId();
				if(dataId != null) {
					try {
						this.bulDataController.clickCache(dataId, userId);
						if(logger.isDebugEnabled()) {
							logger.debug("点击公告主题时同步集群缓存成功，公告主题ID=" + dataId);
						}
					} 
					catch (Exception e) {
						logger.error("点击公告主题时同步集群缓存失败：", e);
					}
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机公告查看出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.BulDataModifyArticle)
	public void updateBulletin(Object o) {
		if(o instanceof CacheInfo && o != null) {
			CacheInfo info = (CacheInfo) o;
			Long dataId = info.getDataId();
			int clickCount = info.getClickCount();
			if(dataId != null) {
				try {
					BulData bean = this.bulDataManager.getById(dataId);
					BulBody body = this.bulDataManager.getBody(dataId);
					bean.setContent(body.getContent());
					bean.setContentName(body.getContentName());
					if(bean != null) {
						this.bulDataController.syncCache(bean, clickCount);
						if(logger.isDebugEnabled()) {
							logger.debug("修改公告主题时同步集群缓存成功，公告主题ID=" + dataId);
						}
					}
				} 
				catch (Exception e) {
					logger.error("修改公告主题时同步集群缓存失败：", e);
				}
			}
		}
	}
	
	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public BulDataController getBulDataController() {
		return bulDataController;
	}

	public void setBulDataController(BulDataController bulDataController) {
		this.bulDataController = bulDataController;
	}
	
}
