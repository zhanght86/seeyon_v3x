package com.seeyon.v3x.bbs.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.webmodel.BbsCountArticle;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.Datetimes;

/**
 * 类描述：
 * 创建日期：2007-02-08
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 * @see BaseHibernateDao
 */
public class BbsArticleDao extends BaseHibernateDao<V3xBbsArticle> {

	// 版块发帖统计
	public List<BbsCountArticle> countArticle(String countType,
			String departmentId, Long boardId) throws Exception {
		List<BbsCountArticle> rtn = new ArrayList<BbsCountArticle>();
		Date now = new Date();

		// 统计回贴总量
		String sql1 = "";
		Date param2 = null;
		Date param3 = null;

		rtn = this.getCountArticle(countType, departmentId, boardId, rtn, 0, sql1, param2, param3);

		// 统计本日回贴
		sql1 = " and (t1.issueTime between ? and ?) ";
		param2 = Datetimes.getTodayFirstTime();
		param3 = Datetimes.getTodayLastTime();
		
		rtn = this.getCountArticle(countType, departmentId, boardId, rtn, 1, sql1, param2, param3);

		// 统计本周回贴
		sql1 = " and (t1.issueTime between ? and ?)";

		param2 = Datetimes.getFirstDayInWeek(now);
		param3 = Datetimes.getLastDayInWeek(now);

		rtn = this.getCountArticle(countType, departmentId, boardId, rtn, 2, sql1, param2, param3);

		// 统计本月回贴
		sql1 = " and (t1.issueTime between ? and ?)";
		param2 = Datetimes.getFirstDayInMonth(now);
		param3 = Datetimes.getLastDayInMonth(now);

		rtn = this.getCountArticle(countType, departmentId, boardId, rtn, 3, sql1, param2, param3);

		return rtn;
	}

	// 版块发帖统计
	// countType 统计类型（0-按部门统计,1-某部门按发布者统计,2-按发布者统计）
	// timeFlag 统计时间范围(0-所有,1-本日,2-本周,3-本月)
	@SuppressWarnings("unchecked")
	private List<BbsCountArticle> getCountArticle(String countType,
			String departmentId, Long boardId, List<BbsCountArticle> inputList,
			int timeFlag, String sql1, Date param2, Date param3) {
		List<BbsCountArticle> rtn = new ArrayList<BbsCountArticle>();
		
		List<Object[]> list = null;
		StringBuffer hsql = new StringBuffer();
		//统计：不包括匿名发帖
		String pubHsqlWithoutAnonymous = ",count(t1.id) from " + V3xBbsArticle.class.getName() + " as t1 where t1.boardId=? and t1.state=0 and t1.anonymousFlag = 0";
		//统计：包括匿名发帖
		String pubHsqlBesidesAnonymous = ",count(t1.id) from " + V3xBbsArticle.class.getName() + " as t1 where t1.boardId=? and t1.state=0";
		//统计：仅仅匿名发帖
		String pubHsqlJustAnonymous = ",count(t1.id) from " + V3xBbsArticle.class.getName() + " as t1 where t1.boardId=? and t1.state=0 and t1.anonymousFlag = 1";
		
		switch (Integer.parseInt(countType)) {
		case BbsConstants.BBS_COUNT_ARTICLE_TYPE_DEPARTMENT:
			hsql.append("select t1.department");
			hsql.append(pubHsqlBesidesAnonymous);
			hsql.append(sql1);
			//hsql.append(" and t1.state=0");
			hsql.append(" group by t1.department");
			
			List<Object> params = new ArrayList<Object>();
			params.add(boardId);
			if(param2 != null) params.add(param2);
			if(param3 != null) params.add(param3);
			
			list = find(hsql.toString(), -1, -1, null, params);
			break;
		//目前仅剩下按照部门、发布者进行统计。某部门按发布者统计选项实际已取消，以下代码实际并未起作用
		// ===========================================================================
		case BbsConstants.BBS_COUNT_ARTICLE_TYPE_DEPARTMENT_PERSON:
			hsql.append("select t1.issueUserId");
			hsql.append(pubHsqlBesidesAnonymous);
			hsql.append(" and t1.department=?");
			hsql.append(sql1);
			//hsql.append(" and t1.state=0");
			hsql.append(" group by t1.issueUserId");
			
			List<Object> params1 = new ArrayList<Object>();
			params1.add(boardId);
			params1.add(departmentId);
			if(param2 != null) params1.add(param2);
			if(param3 != null) params1.add(param3);
			list = find(hsql.toString(), -1, -1, null, params1);
			break;
		// ===========================================================================
		//按照发布者进行统计（不包括匿名发帖数）
		case BbsConstants.BBS_COUNT_ARTICLE_TYPE_PERSON:
			hsql.append("select t1.issueUserId");
			hsql.append(pubHsqlWithoutAnonymous);
			hsql.append(sql1);
			//hsql.append(" and t1.state=0");
			hsql.append(" group by t1.issueUserId");

			List<Object> params2 = new ArrayList<Object>();
			params2.add(boardId);
			if(param2 != null) params2.add(param2);
			if(param3 != null) params2.add(param3);
			list = find(hsql.toString(), -1, -1, null, params2);
			break;
		//按照发布者进行统计（特殊情况：匿名单独列出进行统计）
		case BbsConstants.BBS_COUNT_ARTICLE_TYPE_PERSON_ANONYMOUS:
			hsql.append("select t1.issueUserId");
			hsql.append(pubHsqlJustAnonymous);
			hsql.append(sql1);
			hsql.append(" group by t1.issueUserId");
			List<Object> params3 = new ArrayList<Object>();
			params3.add(boardId);
			if(param2 != null) params3.add(param2);
			if(param3 != null) params3.add(param3);
			list = find(hsql.toString(), -1, -1, null, params3);
			break;			
		}

		if(list != null){
			for (Object[] array : list) {
				Long value1 = (Long)array[0];
				Long value2 = new Long(array[1].toString());
	
				boolean isFind = false;
				BbsCountArticle newBbsCountArticle = new BbsCountArticle();
	
				for (BbsCountArticle bbsCountArticle : inputList) {
					if (bbsCountArticle.getModuleId().equals(value1)) {
						newBbsCountArticle = bbsCountArticle;
						inputList.remove(bbsCountArticle);
						isFind = true;
						break;
					}
				}
	
				if (!isFind) {
					newBbsCountArticle = new BbsCountArticle();
					newBbsCountArticle.setModuleId(value1);
				}
	
				switch (timeFlag) {
				case 0: // 总量统计
					newBbsCountArticle.setAllCount(value2);
					break;
				case 1: // 本日统计
					newBbsCountArticle.setDayCount(value2);
					break;
				case 2: // 本周统计
					newBbsCountArticle.setWeekCount(value2);
					break;
				case 3: // 本月统计
					newBbsCountArticle.setMonthCount(value2);
					break;
				default:
					break;
	
				}
				rtn.add(newBbsCountArticle);
			}
	
			for (BbsCountArticle bbsCountArticle : inputList) {
				rtn.add(bbsCountArticle);
			}
		}
		
		return rtn;
	}
	
	/**
	 * 取消置顶
	 * @param ids  所要取消置顶的讨论主题ID集合
	 */
	public void cancelTop(List<Long> ids) {
		this.updateTopSequence(ids, BbsConstants.BBS_ARTICLE_ISNOT_TOP);
	}
	
	/**
	 * 修改选中讨论主题的置顶状态（为置顶或非置顶）
	 * @param ids  所要修改置顶状态的讨论主题ID集合
	 * @param state2Update  置顶状态更新值
	 */
	private void updateTopSequence(List<Long> ids, int state2Update) {
		String hql = "update " + V3xBbsArticle.class.getName() + " as a set a.topSequence=:state2Update where a.id in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state2Update", state2Update);
		params.put("ids", ids);
		this.bulkUpdate(hql, params);
	}
	
	/**
	 * 置顶
	 * @param ids  所要置顶的讨论主题ID集合
	 */
	public void top(List<Long> ids) {
		this.updateTopSequence(ids, BbsConstants.BBS_ARTICLE_IS_TOP);
	}
	
	/**
	 * 标识为精华帖
	 * @param ids  所要标识为精华帖的讨论主题ID集合
	 */
	public void elite(List<Long> ids) {
		this.updateEliteFlag(ids, true);
	}
	
	/**
	 * 修改选中讨论主题的精华帖标识（为置顶或非置顶）
	 * @param ids  所要修改精华帖标识状态的讨论主题ID集合
	 * @param state2Update  标识精华(true)或取消精华(false)
	 */
	private void updateEliteFlag(List<Long> ids, boolean eliteFlag) {
		String hql = "update " + V3xBbsArticle.class.getName() + " as a set a.eliteFlag=:eliteFlag where a.id in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eliteFlag", eliteFlag);
		params.put("ids", ids);
		this.bulkUpdate(hql, params);
	}
	
	/**
	 * 取消精华帖标识
	 * @param ids  所要取消精华帖标识的主题ID集合
	 */
	public void cancelElite(List<Long> ids) {
		this.updateEliteFlag(ids, false);
	}

}
