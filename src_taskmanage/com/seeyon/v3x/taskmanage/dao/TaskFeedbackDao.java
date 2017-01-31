package com.seeyon.v3x.taskmanage.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 任务反馈Dao
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskFeedbackDao  extends BaseHibernateDao<TaskFeedback> {

	/**
	 * 根据任务ID和查询模型获取对应的任务汇报列表
	 * @param taskId	任务ID
	 * @param sm		查询模型
	 * @return	任务汇报列表
	 */
	@SuppressWarnings("unchecked")
	public List<TaskFeedback> getFeedbacks(Long taskId, SearchModel sm) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("from " + TaskFeedback.class.getCanonicalName() + " where taskId=? ");
		params.add(taskId);
		if(sm != null) {
			if(sm.searchByCreator()) {
				hql.append(" and createUser = ? ");
				params.add(NumberUtils.toLong(sm.getSearchValue2()));
			}
			else if(sm.searchByDate()) {
				if(Strings.isNotBlank(sm.getSearchValue1())) {
					hql.append(" and createTime >= ? ");
					params.add(Datetimes.getTodayFirstTime(sm.getSearchValue1()));
				}
				
				if(Strings.isNotBlank(sm.getSearchValue2())) {
					hql.append(" and createTime <= ? ");
					params.add(Datetimes.getTodayLastTime(sm.getSearchValue2()));
				}
			}
		}
		
		hql.append(" order by createTime desc ");
		boolean pagination = sm == null || sm.isPagination();
		return pagination ? find(hql.toString(), null, params) : find(hql.toString(), params.toArray());
	}

	public void deleteByIds(List<Long> ids) {
		String hql = "delete from " + TaskFeedback.class.getCanonicalName() + " where id in (:ids)";
		Map<String, Object> params = FormBizConfigUtils.newHashMap("ids", ids);
		this.bulkUpdate(hql, params);
	}

}
