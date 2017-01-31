package com.seeyon.v3x.cluster.listener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.news.controller.NewsDataController;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.news.util.NewsDataLock;
import com.seeyon.v3x.util.annotation.HandleNotification;
/**
 * 新闻监听，包括增加、修改新闻板块以及在修改或审核新闻时的加锁、解锁
 */
public class NewsNotificationHandler {

	private static final Log logger = LogFactory.getLog(NewsNotificationHandler.class);
	private NewsDataController newsDataController;
	private NewsDataManager newsDataManager;
	private NewsTypeManager newsTypeManager;
	
	@HandleNotification(type=NotificationType.NewsAddType)
	public void addNewsType(Object o) {
		try {
			if(o!=null && o instanceof NewsType) {
				NewsType type = (NewsType) o;
				this.getNewsTypeManager().initPartAdd(type);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机增加新闻板块成功，增加的板块信息为：\n" + BeanUtils.describe(this.getNewsTypeManager().getById(type.getId())));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机增加新闻板块时出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.NewsUpdateType)
	public void updateNewsType(Object o) {
		try {
			if(o!=null && o instanceof NewsType) {
				NewsType type = (NewsType) o;
				this.getNewsTypeManager().initPartEdit(type);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机修改新闻板块成功，修改后的板块信息为：\n" + BeanUtils.describe(this.getNewsTypeManager().getById(type.getId())));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机修改新闻板块时出现异常：", e);
		}	
	}
	
	@HandleNotification(type=NotificationType.NewsLock)
	public void lockNewsWhenEditOrAudit(Object o) {
		try {
			if(o!=null && o instanceof NewsDataLock) {
				NewsDataLock lock = (NewsDataLock) o;
				this.getNewsDataManager().lock(lock.getNewsid(), lock.getUserid(), lock.getAction());
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机新闻加锁动作成功，锁信息为：\n" + BeanUtils.describe(lock));
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机新闻加锁出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.NewsUnLock)
	public void unlockNewsAfterEditOrAduit(Object o) {
		try {
			if(o!=null) {
				Long newsId = (Long) o;
				this.getNewsDataManager().unlock(newsId);
				if(logger.isDebugEnabled()) {
					logger.debug("双机同步，备机新闻解锁动作[新闻ID：" + newsId + "]成功.");
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机新闻解锁出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.NewsClickArticle)
	public void clickNews(Object o) {
		try {
			if(o instanceof CacheInfo && o != null) {
				CacheInfo info = (CacheInfo) o;
				Long dataId = info.getDataId();
				Long userId = info.getUserId();
				if(dataId != null) {
					try {
						this.newsDataController.clickCache(dataId, userId);
						if(logger.isDebugEnabled()) {
							logger.debug("点击新闻主题时同步集群缓存成功，新闻主题ID=" + dataId);
						}
					} 
					catch (Exception e) {
						logger.error("点击新闻主题时同步集群缓存失败：", e);
					}
				}
			}
		} catch(Exception e) {
			logger.error("双机同步，备机新闻查看出现异常：", e);
		}
	}
	
	@HandleNotification(type=NotificationType.NewsModifyArticle)
	public void updateNews(Object o) {
		if(o instanceof CacheInfo && o != null) {
			CacheInfo info = (CacheInfo) o;
			Long dataId = info.getDataId();
			int clickCount = info.getClickCount();
			if(dataId != null) {
				try {
					NewsData bean = newsDataManager.getById(dataId);
					bean.setContent(newsDataManager.getBody(dataId).getContent());
					if(bean != null) {
						this.newsDataController.syncCache(bean, clickCount);
						if(logger.isDebugEnabled()) {
							logger.debug("修改新闻主题时同步集群缓存成功，新闻主题ID=" + dataId);
						}
					}
				} 
				catch (Exception e) {
					logger.error("修改新闻主题时同步集群缓存失败：", e);
				}
			}
		}
	}
	
	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}
	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}
	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public NewsDataController getNewsDataController() {
		return newsDataController;
	}

	public void setNewsDataController(NewsDataController newsDataController) {
		this.newsDataController = newsDataController;
	}
}