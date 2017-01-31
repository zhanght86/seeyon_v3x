package com.seeyon.v3x.notice.manager;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.notice.domain.Notice;

/**
 * 栏目公示板内容
 */
public class NoticeManagerImpl extends BaseHibernateDao<Notice> implements NoticeManager {

	@SuppressWarnings("unchecked")
	public Notice getByBoardId(Long boardId) {
		String hql = " from Notice as n where n.boardId=?";
		List<Notice> list = this.find(hql, -1, -1, null, boardId);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public void save(Notice notice) {
		super.save(notice);
	}

	public void update(Notice notice) {
		super.update(notice);
	}

}