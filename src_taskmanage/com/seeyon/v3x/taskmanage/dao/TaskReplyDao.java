package com.seeyon.v3x.taskmanage.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.taskmanage.domain.TaskReply;

/**
 * 任务评论、回复Dao
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskReplyDao extends BaseHibernateDao<TaskReply> {

	/**
	 * 获取任务对应的全部任务回复
	 * @param taskId	任务ID
	 * @return	全部任务回复
	 */
	@SuppressWarnings("unchecked")
	public List<TaskReply> getAllReplysByTaskId(Long taskId) {
		String hql = "from " + TaskReply.class.getCanonicalName() + " where taskId=? order by createTime desc";
		return find(hql, taskId);
	}

}
