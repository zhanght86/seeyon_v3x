package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.util.annotation.HandleNotification;

/**
 * BBS监听，包括增加、修改和删除讨论区。
 */
public class BbsNotificationHandler {
	private static final Log logger = LogFactory
			.getLog(BbsNotificationHandler.class);
	private BbsBoardManager bbsBoardManager;

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	private List<V3xBbsBoard> getBbsBoards(Object[] idArray)
			throws BusinessException {
		List<Long> idList = new ArrayList<Long>(idArray.length);
		for (Object id : idArray) {
			idList.add((Long)id);
		}
		return this.getBbsBoardManager().getBbsBoards(idList, false);

	}

	private V3xBbsBoard getBbsBoard(Long id) throws BusinessException {
		Long[] idArray = { id };
		List<V3xBbsBoard> boards = getBbsBoards(idArray);
		if (boards == null || boards.size() == 0)
			return null;
		return boards.get(0);

	}

	@HandleNotification(type = NotificationType.BbsAddBoard)
	public void addBoards(Object o) {
		if (o instanceof Object[]) {
			try {
				Object[] oArray = (Object[]) o;
				this.getBbsBoardManager().syncMemoryWhenCreateBoards(
						getBbsBoards(oArray));
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("更新BBS Board：");
					for (Object id : oArray) {
						logger.debug("更新后："
								+ BeanUtils.describe(this.getBbsBoardManager()
										.getBoardById((Long)id)));
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@HandleNotification(type = NotificationType.BbsUpdateBoard)
	public void updateBoard(Object o) {
		if (o instanceof Long) {
			try {

				Long id = (Long) o;
				V3xBbsBoard board = getBbsBoard(id);
				if (board != null) {
					this.getBbsBoardManager().syncMemoryWhenUpdateBoard(board);
				} else {
					logger.error("没有id为" + id + "的BBS讨论区。");
				}
				// 调试信息
				if (logger.isDebugEnabled()) {
					logger.debug("更新BBS Board：");

					logger.debug("更新后："
							+ BeanUtils.describe(this.getBbsBoardManager()
									.getBoardById(id)));

				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@HandleNotification(type = NotificationType.BbsDeleteBoard)
	public void deleteBoards(Object o) {
		if (o instanceof Object[]) {
			try {
				Object[] idArray = (Object[]) o;
				List<Long> idList = new ArrayList<Long>(idArray.length);
				for (Object id : idArray) {
					idList.add((Long)id);
				}
				this.getBbsBoardManager().syncMemoryWhenDeleteBoards(idList);
				// 调试信息
				if (logger.isDebugEnabled()) {
					for (Long id : idList) {
						if (this.getBbsBoardManager().getBoardById(id) == null) {
							logger.debug("删除BBS讨论区成功！id=" + id);
						} else {
							logger.error("删除BBS讨论区失败！id=" + id);
						}
					}

				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
