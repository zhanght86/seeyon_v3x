package com.seeyon.v3x.notice.manager;

import com.seeyon.v3x.notice.domain.Notice;

/**
 * 栏目公示板内容
 */
public interface NoticeManager {

	/**
	 * 根据栏目singleBoardId获取公示板内容
	 * @param boardId 栏目singleBoardId
	 * @return
	 */
	public Notice getByBoardId(Long boardId);

	/**
	 * 保存公示板内容
	 * @param notice
	 */
	public void save(Notice notice);

	/**
	 * 更新公示板内容
	 * @param notice
	 */
	public void update(Notice notice);

}