package com.seeyon.v3x.cluster.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.controller.BbsController;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.util.CacheInfo;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.util.annotation.HandleNotification;
/**
 * 监听楼主对主贴的修改，同步集群缓存
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-13
 */
public class BbsArticleNotificationHandler {
	private static final Log logger = LogFactory.getLog(BbsArticleNotificationHandler.class);
	private BbsController bbsController;
	private BbsArticleManager bbsArticleManager;

	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}

	public void setBbsController(BbsController bbsController) {
		this.bbsController = bbsController;
	}
	
	@HandleNotification(type=NotificationType.BbsClickArticle)
	public void clickArticle(Object o) {
		if(o instanceof CacheInfo && o != null) {
			CacheInfo info = (CacheInfo) o;
			Long articleId = info.getDataId();
			Long userId = info.getUserId();
			if(articleId != null) {
				try {
					this.bbsController.clickCache(articleId, userId);
					if(logger.isDebugEnabled()) {
						logger.debug("点击讨论主题时同步集群缓存成功，讨论主题ID=" + articleId);
					}
				} 
				catch (Exception e) {
					logger.error("点击讨论主题时同步集群缓存失败：", e);
				}
			}
		}
	}
	
	@HandleNotification(type=NotificationType.BbsModifyArticle)
	public void updateArticle(Object o) {
		if(o instanceof CacheInfo && o != null) {
			CacheInfo info = (CacheInfo) o;
			Long articleId = info.getDataId();
			int clickCount = info.getClickCount();
			
			if(articleId != null) {
				try {
					V3xBbsArticle article = this.bbsArticleManager.getArticleById(articleId);
					if(article != null) {
						this.bbsController.syncCache(article, clickCount);
						
						if(logger.isDebugEnabled()) {
							logger.debug("修改讨论主题时同步集群缓存成功，讨论主题ID=" + articleId);
						}
					}
				} 
				catch (Exception e) {
					logger.error("修改讨论主题时同步集群缓存失败：", e);
				}
			}
		}
	}
	
	@HandleNotification(type=NotificationType.BbsDeleteArticle)
	public void deleteArticle(Object o) {
		if(o != null) {
			try {
				Long articleId = (Long) o;
				this.bbsController.removeCache(articleId);
				
				if(logger.isDebugEnabled()) {
					logger.debug("删除讨论主题时同步集群缓存成功，讨论主题ID=" + articleId);
				}
			} catch(Exception e) {
				logger.error("删除讨论主题时同步集群缓存失败：", e);
			}
		}
	}
	
}
