package com.seeyon.v3x.bbs.manager;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.bbs.dao.BbsArticleDao;
import com.seeyon.v3x.bbs.dao.BbsArticleIssueAreaDao;
import com.seeyon.v3x.bbs.dao.BbsArticleReplyDao;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleIssueArea;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleReply;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.util.BbsUtil;
import com.seeyon.v3x.bbs.webmodel.AnonymousCountModel;
import com.seeyon.v3x.bbs.webmodel.BbsCountArticle;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.cache.ClickDetail;

/**
 * 类描述： 创建日期：2007-02-08
 * 
 * @author liaoj
 * @version 1.0
 * @since JDK 5.0
 */
public class BbsArticleManagerImpl extends BaseHibernateDao<V3xBbsArticle>
		implements BbsArticleManager, IndexEnable {
	private static Log log = LogFactory.getLog(BbsArticleManagerImpl.class);
	private BbsBoardManager bbsBoardManager;
	private OrgManager orgManager;
	private BbsArticleDao bbsArticleDao;
	private BbsArticleReplyDao bbsArticleReplyDao;
	private BbsArticleIssueAreaDao vbsArticleIssueAreaDao;

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setVbsArticleIssueAreaDao(
			BbsArticleIssueAreaDao vbsArticleIssueAreaDao) {
		this.vbsArticleIssueAreaDao = vbsArticleIssueAreaDao;
	}

	public void setBbsArticleReplyDao(BbsArticleReplyDao bbsArticleReplyDao) {
		this.bbsArticleReplyDao = bbsArticleReplyDao;
	}

	public void setBbsArticleDao(BbsArticleDao bbsArticleDao) {
		this.bbsArticleDao = bbsArticleDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * 方法描述：获取单位主页显示的讨论区新主题 add by Dongjw ,2007-05-23
	 * 
	 * @param pagesize
	 * @return List
	 */
	public List<V3xBbsArticle> queryArticleList(int pageSize, String condition,
			String textfield, String textfield1) {
		return this.queryArticleList(
				BbsConstants.BBS_BOARD_AFFILITER.CORPORATION, pageSize, false,
				condition, textfield, textfield1, null);
	}
	
	public List<V3xBbsArticle> queryCustomArticleList(long spaceId, int spaceType, int pageSize, String condition,
			String textfield, String textfield1) {
		if (spaceType == 5) {
			return this.queryArticleList(
					spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM, pageSize, false, condition, textfield, textfield1, null);
		} else {
			return this.queryArticleList(
					spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP, pageSize, false, condition, textfield, textfield1, null);
		}
	}

	private static final String SELECT_FIELDS = "select distinct a.id, a.articleName, a.boardId, a.issueUserId, "
			+ "a.clickNumber, a.replyNumber, a.issueTime, a.eliteFlag, a.messageNotifyFlag, a.resourceFlag, "
			+ "a.topSequence, a.anonymousFlag, a.anonymousReplyFlag, a.identifier ";

	private Object[] queryArticleList(
			BbsConstants.BBS_BOARD_AFFILITER affiliateroom, int pageSize,
			boolean eliteFlag, String condition, String textfield,
			String textfield1, boolean isDept, String boardId,
			boolean orderByTop) {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		Long cuurrentDeptId = user.getDepartmentId();
		List<Long> domainIds = this.getDomainIds4User(user);

		List<V3xBbsBoard> adminBoardIds = null; // 所有的板块-------内存取
		List<Long> adminBoardId = new ArrayList<Long>();
		if (affiliateroom.equals(BbsConstants.BBS_BOARD_AFFILITER.CORPORATION)) { // 单位
			adminBoardIds = this.bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
		} else if (affiliateroom.equals(BbsConstants.BBS_BOARD_AFFILITER.GROUP)) { // 集团
			adminBoardIds = this.bbsBoardManager.getAllGroupBbsBoard();
		} else if (affiliateroom.equals(BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT)) { // 部门
			adminBoardIds = this.bbsBoardManager.getAllDeptBbsBoard(cuurrentDeptId);
		}
		// 取板块ID
		if (adminBoardIds != null && adminBoardIds.size() > 0) {
			for (V3xBbsBoard board : adminBoardIds) {
				adminBoardId.add(board.getId());
			}
		}

		String[] hql = new String[4];
		// 所要获取的内容，随需更换
		hql[0] = SELECT_FIELDS;
		// 按照人名查询时，将人员表关联进来
		hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
				+ V3xBbsArticleIssueArea.class.getName() + " as c ";
		// 查询条件，随需增加
		hql[2] = " where a.id = c.articleId and (c.moduleId in (:domainIds) or a.issueUserId=:currentUserId) ";
		// 排序或分组统计，随需更换(单位最新讨论和集团最新讨论等处不需要按照置顶顺序排序，只按发布时间排序)
		hql[3] = orderByTop ? " order by a.topSequence desc,a.issueTime desc"
				: " order by a.issueTime desc";

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("domainIds", domainIds);
		namedParameters.put("currentUserId", currentUserId);

		// 查询
		if ("issueUser".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
					+ V3xBbsArticleIssueArea.class.getName() + " as c, "
					+ V3xOrgMember.class.getName() + " as m ";
			// 加入一个匿名判断，如果当前用户希望搜索"匿名"用户发帖,那么应当允许用户查找到所有匿名发表的讨论主题,否则只能查询到实名发帖
			if (!textfield.contains(ResourceBundleUtil.getString(
					BbsUtil.BBS_I18N_RESOURCE, "anonymous.label"))) {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=0 and m.name like :creatorName ";
				namedParameters.put("creatorName", "%"
						+ SQLWildcardUtil.escape(textfield) + "%");
			} else {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=1 ";
			}
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[2] += " and a.articleName like :articleName";
			namedParameters.put("articleName", "%"
					+ SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				hql[2] += " and a.issueTime>= :beginTime ";
				namedParameters.put("beginTime", stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				hql[2] += " and a.issueTime<= :endTime ";
				namedParameters.put("endTime", stamp);
			}
		}

		if (eliteFlag) {
			hql[2] += " and a.eliteFlag=true ";
		}

		if (!adminBoardIds.isEmpty()) {
			hql[2] += " and a.boardId in (:adminBoardId) and a.state=0 ";
			namedParameters.put("adminBoardId", adminBoardId);
		} else {
			hql[2] += " and a.boardId=0  and  a.state=0  ";// 板块一个都没有的时候
		}

		// 查询单板块的讨论
		if (Strings.isNotBlank(boardId)) {
			hql[2] += " and a.boardId= :boardId ";
			namedParameters.put("boardId", Long.parseLong(boardId));
		}
		return new Object[] { hql, namedParameters };
	}
	
	private Object[] queryArticleList(long spaceId, BbsConstants.BBS_BOARD_AFFILITER affiliateroom, int pageSize, boolean eliteFlag, String condition, 
			String textfield, String textfield1, boolean isDept, String boardId, boolean orderByTop) {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		List<Long> domainIds = this.getDomainIds4User(user);
		List<V3xBbsBoard> adminBoardIds = null; // 所有的板块-------内存取
		List<Long> adminBoardId = new ArrayList<Long>();
		adminBoardIds = this.bbsBoardManager.getAllCustomAccBbsBoard(spaceId, affiliateroom.ordinal());
		// 取板块ID
		if (adminBoardIds != null && adminBoardIds.size() > 0) {
			for (V3xBbsBoard board : adminBoardIds) {
				adminBoardId.add(board.getId());
			}
		}
		String[] hql = new String[4];
		// 所要获取的内容，随需更换
		hql[0] = SELECT_FIELDS;
		// 按照人名查询时，将人员表关联进来
		hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , " + V3xBbsArticleIssueArea.class.getName() + " as c ";
		// 查询条件，随需增加
		hql[2] = " where a.id = c.articleId and (c.moduleId in (:domainIds) or a.issueUserId=:currentUserId) ";
		// 排序或分组统计，随需更换(单位最新讨论和集团最新讨论等处不需要按照置顶顺序排序，只按发布时间排序)
		hql[3] = orderByTop ? " order by a.topSequence desc,a.issueTime desc" : " order by a.issueTime desc";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("domainIds", domainIds);
		namedParameters.put("currentUserId", currentUserId);
		// 查询
		if ("issueUser".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , " + V3xBbsArticleIssueArea.class.getName() + " as c, "
					+ V3xOrgMember.class.getName() + " as m ";
			// 加入一个匿名判断，如果当前用户希望搜索"匿名"用户发帖,那么应当允许用户查找到所有匿名发表的讨论主题,否则只能查询到实名发帖
			if (!textfield.contains(ResourceBundleUtil.getString(BbsUtil.BBS_I18N_RESOURCE, "anonymous.label"))) {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=0 and m.name like :creatorName ";
				namedParameters.put("creatorName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			} else {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=1 ";
			}
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[2] += " and a.articleName like :articleName";
			namedParameters.put("articleName", "%" + SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				hql[2] += " and a.issueTime>= :beginTime ";
				namedParameters.put("beginTime", stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				hql[2] += " and a.issueTime<= :endTime ";
				namedParameters.put("endTime", stamp);
			}
		}
		if (eliteFlag) {
			hql[2] += " and a.eliteFlag=true ";
		}
		if (!adminBoardIds.isEmpty()) {
			hql[2] += " and a.boardId in (:adminBoardId) and a.state=0 ";
			namedParameters.put("adminBoardId", adminBoardId);
		} else {
			hql[2] += " and a.boardId=0  and  a.state=0  ";// 板块一个都没有的时候
		}
		// 查询单板块的讨论
		if (Strings.isNotBlank(boardId)) {
			hql[2] += " and a.boardId= :boardId ";
			namedParameters.put("boardId", Long.parseLong(boardId));
		}
		return new Object[] { hql, namedParameters };
	}

	/**
	 * 获取某个类型讨论（单位、集团）的最新讨论主题
	 * 
	 * @param affiliateroom
	 * @param pageSize
	 *            为-1表示自动分页
	 * @param eliteFlag
	 *            是否取精华
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<V3xBbsArticle> queryArticleList(
			BbsConstants.BBS_BOARD_AFFILITER affiliateroom, int pageSize,
			boolean eliteFlag, String condition, String textfield,
			String textfield1, String boardId) {
		Object[] result = this.queryArticleList(affiliateroom, pageSize,
				eliteFlag, condition, textfield, textfield1, false, boardId,
				false);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		String hqlStr = hql[0] + hql[1] + hql[2] + hql[3];

		List<Object[]> list = null;
		if (pageSize > 0) {
			list = (List<Object[]>) super.find(hqlStr, 0, pageSize,
					namedParameters);
		} else {
			list = (List<Object[]>) super.find(hqlStr, "a.id", true,
					namedParameters);
		}
		return this.objArr2ArticleList(list);
	}
	
	/**
	 * 获取某个类型讨论（自定义单位、集团）的最新讨论主题
	 * @param spaceId
	 * @param affiliateroom
	 * @param pageSize
	 *            为-1表示自动分页
	 * @param eliteFlag
	 *            是否取精华
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<V3xBbsArticle> queryArticleList(long spaceId, BbsConstants.BBS_BOARD_AFFILITER affiliateroom, int pageSize, boolean eliteFlag, String condition, String textfield,
			String textfield1, String boardId) {
		Object[] result = this.queryArticleList(spaceId, affiliateroom, pageSize, eliteFlag, condition, textfield, textfield1, false, boardId, false);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		String hqlStr = hql[0] + hql[1] + hql[2] + hql[3];
		List<Object[]> list = null;
		if (pageSize > 0) {
			list = (List<Object[]>) super.find(hqlStr, 0, pageSize, namedParameters);
		} else {
			list = (List<Object[]>) super.find(hqlStr, "a.id", true, namedParameters);
		}
		return this.objArr2ArticleList(list);
	}

	/**
	 * 将获取的数组结果集转换为讨论集合，抽取出来以便单点维护
	 */
	private List<V3xBbsArticle> objArr2ArticleList(List<Object[]> list) {
		/*
		 * 获取字段为： a.id, a.articleName, a.boardId, a.issueUserId, a.clickNumber,
		 * a.replyNumber, a.issueTime a.eliteFlag, a.messageNotifyFlag,
		 * a.resourceFlag, a.topSequence, a.anonymousFlag, a.anonymousReplyFlag,
		 * a.identifier
		 */
		List<V3xBbsArticle> articleList = null;
		V3xBbsArticle v3xBbsArticle = null;
		if (CollectionUtils.isNotEmpty(list)) {
			articleList = new ArrayList<V3xBbsArticle>();
			for (Object[] obj : list) {
				v3xBbsArticle = new V3xBbsArticle();
				int n = 0;
				v3xBbsArticle.setId((Long) obj[n++]);
				v3xBbsArticle.setArticleName((String) obj[n++]);
				v3xBbsArticle.setBoardId((Long) obj[n++]);
				v3xBbsArticle.setIssueUserId((Long) obj[n++]);
				v3xBbsArticle.setClickNumber((Integer) obj[n++]);
				v3xBbsArticle.setReplyNumber((Integer) obj[n++]);
				v3xBbsArticle.setIssueTime((Timestamp) obj[n++]);
				v3xBbsArticle.setEliteFlag((Boolean) obj[n++]);
				v3xBbsArticle.setMessageNotifyFlag((Boolean) obj[n++]);
				v3xBbsArticle.setResourceFlag((Byte) obj[n++]);
				v3xBbsArticle.setTopSequence((Integer) obj[n++]);
				v3xBbsArticle.setAnonymousFlag((Boolean) obj[n++]);
				v3xBbsArticle.setAnonymousReplyFlag((Boolean) obj[n++]);
				v3xBbsArticle.setIdentifier((String) obj[n++]);
				articleList.add(v3xBbsArticle);
			}
		}
		return articleList;
	}

	/**
	 * 获取外单位讨论所需HQL
	 */
	private Object[] queryOtherAccountSQL(boolean isElite, String condition,
			String textfield, String textfield1) {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();

		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(currentUserId,
					ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT,
					ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM);
		} catch (BusinessException e) {
			log.error("", e);
		}

		String[] hql = new String[4];
		// 所要获取的内容，随需更换
		hql[0] = SELECT_FIELDS;
		// 按照人名查询时，将人员表关联进来
		hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
				+ V3xBbsArticleIssueArea.class.getName() + " as c ";
		// 查询条件，随需增加
		hql[2] = " where a.id = c.articleId and a.accountId!=:accountId and c.moduleId in (:domainIds)";
		// 排序或分组统计，随需更换
		hql[3] = " order by a.topSequence desc,a.issueTime desc";

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("domainIds", domainIds);
		namedParameters.put("accountId", user.getLoginAccount());

		// 查询
		if ("issueUser".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
					+ V3xBbsArticleIssueArea.class.getName() + " as c, "
					+ V3xOrgMember.class.getName() + " as m ";
			hql[2] += " and a.issueUserId=m.id and and a.state=0 and m.name like :creatorName ";
			namedParameters.put("creatorName", "%"
					+ SQLWildcardUtil.escape(textfield) + "%");
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[2] += " and a.articleName like :articleName";
			namedParameters.put("articleName", "%"
					+ SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				hql[2] += " and a.issueTime>= :beginTime ";
				namedParameters.put("beginTime", stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				hql[2] += " and a.issueTime<= :endTime ";
				namedParameters.put("endTime", stamp);
			}
		}

		if (isElite) {
			hql[2] += " and a.eliteFlag=true ";
		}
		return new Object[] { hql, namedParameters };
	}

	public List<V3xBbsArticle> queryOtherAccountEliteArticleList(
			String condition, String textfield, String textfield1) {
		return this.queryOtherAccountArticleList(true, condition, textfield,
				textfield1);
	}

	public List<V3xBbsArticle> queryOtherAccountArticleList(String condition,
			String textfield, String textfield1) {
		return this.queryOtherAccountArticleList(false, condition, textfield,
				textfield1);
	}

	@SuppressWarnings("unchecked")
	private List<V3xBbsArticle> queryOtherAccountArticleList(boolean isElite,
			String condition, String textfield, String textfield1) {
		Object[] result = queryOtherAccountSQL(isElite, condition, textfield,
				textfield1);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		List<Object[]> list = (List<Object[]>) super.find(hql[0] + hql[1]
				+ hql[2] + hql[3], "id", true, namedParameters);
		return this.objArr2ArticleList(list);
	}

	@SuppressWarnings("unchecked")
	public int getOtherAccountArticleNumber() {
		Object[] result = queryOtherAccountSQL(false, null, null, null);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		String countSql = "select count(distinct a.id) " + hql[1] + hql[2]
				+ " group by a.boardId";
		Object totalCount = super.findUnique(countSql, namedParameters);
		return (totalCount == null) ? 0 : (Integer) totalCount;
	}

	@SuppressWarnings("unchecked")
	public int getOtherAccountEliteArticleNumber() {
		Object[] result = queryOtherAccountSQL(true, null, null, null);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		String countSql = "select count(distinct a.id) " + hql[1] + hql[2]
				+ " group by a.boardId";
		Object totalCount = super.findUnique(countSql, namedParameters);
		return (totalCount == null) ? 0 : (Integer) totalCount;
	}

	@SuppressWarnings("unchecked")
	public int getOtherAccountBoardsReplyNumber() {
		Object[] result = queryOtherAccountSQL(true, null, null, null);
		String[] hql = (String[]) result[0];
		Map<String, Object> namedParameters = (Map<String, Object>) result[1];
		String countSql = "select sum(a.replyNumber) " + hql[1] + hql[2]
				+ " group by a.boardId";
		Object totalCount = super.findUnique(countSql, namedParameters);
		return (totalCount == null) ? 0 : (Integer) totalCount;
	}

	/**
	 * 方法描述：获取集团空间讨论区新主题 add by xut ,2007-08-29
	 * 
	 * @param pagesize
	 * @return List
	 */
	public List<V3xBbsArticle> queryGroupArticleList(int pageSize) {
		return this.queryArticleList(BbsConstants.BBS_BOARD_AFFILITER.GROUP,
				pageSize, false, null, null, null, null);
	}

	/**
	 * 方法描述：获取讨论区所有版块的所有主题信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> listAllArticle(boolean isGroup,
			String condition, String textfield, String textfield1,
			String boardId) throws Exception {
		if (isGroup) {
			return this.queryArticleList(
					BbsConstants.BBS_BOARD_AFFILITER.GROUP, -1, false,
					condition, textfield, textfield1, boardId);
		} else {
			return this.queryArticleList(
					BbsConstants.BBS_BOARD_AFFILITER.CORPORATION, -1, false,
					condition, textfield, textfield1, boardId);
		}
	}

	public List<V3xBbsArticle> listAllArticle(long spaceId, int spaceType, String condition, String textfield, String textfield1, String boardId) throws Exception {
		if (spaceType == 5) {
			return this.queryArticleList(spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM, -1, false, condition, textfield, textfield1, boardId);
		} else {
			return this.queryArticleList(spaceId, BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP, -1, false, condition, textfield, textfield1, boardId);
		}
	}
	
	/**
	 * 方法描述：获取讨论区所有版块的精华帖信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> listAllElitePost(String condition,
			String textfield, String textfield1) throws Exception {
		return this.queryArticleList(
				BbsConstants.BBS_BOARD_AFFILITER.CORPORATION, -1, true,
				condition, textfield, textfield1, null);
	}
	
	public List<V3xBbsArticle> listAllElitePost(long spaceId, int spaceType, String condition, String textfield, String textfield1) throws Exception {
		
		return this.queryArticleList(spaceId, spaceType == 5 ? BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM : BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP, 
				-1, true, condition, textfield, textfield1, null);
	}

	public List<V3xBbsArticle> listAllGROUPElitePost(String condition,
			String textfield, String textfield1) throws Exception {
		return this.queryArticleList(BbsConstants.BBS_BOARD_AFFILITER.GROUP,
				-1, true, condition, textfield, textfield1, null);
	}

	/**
	 * 方法描述：获取讨论区某一版块的精华帖信息
	 * 
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> listBoardElitePost(Long boardId,
			String condition, String textfield, String textfield1) {
		return allArticleByBoardIdList(boardId, -1, true, condition, textfield,
				textfield1, false);
	}

	public List<V3xBbsArticle> listArticleByBoardId(Long boardId, int pageSize)
			throws Exception {
		return allArticleByBoardIdList(boardId, pageSize, false, null, null,
				null, false);
	}

	/**
	 * 单版块讨论更多页面，置顶显示在前面
	 */
	public List<V3xBbsArticle> listArticleByBoardId(Long boardId,
			String condition, String textfield, String textfield1)
			throws Exception {
		return allArticleByBoardIdList(boardId, 0, false, condition, textfield,
				textfield1, false);
	}

	/**
	 * 方法描述：获取讨论区某一版块的所有主题信息
	 * 
	 * @param boardId
	 *            版块编号
	 * @return List 主题信息的集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> listArticleByBoardId(Long boardId,
			String condition, String textfield, String textfield1,
			boolean isDept) throws Exception {
		return this.ArticleByBoardIdList(boardId, -1, false, condition,
				textfield, textfield1, isDept);
	}

	/**
	 * 辅助方法：获取当前用户的各种组织模型ID
	 */
	private List<Long> getDomainIds4User(User user) {
		List<Long> domainIds = null;
		try {
			if (user.isInternal()) {
				domainIds = this.orgManager
						.getUserDomainIDs(user.getId(),
								V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account",
								"Department", "Team", "Member", "Post",
								"Level", "Role");
			} else {
				domainIds = this.orgManager.getUserDomainIDs(user.getId(),
						V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team",
						"Member", "Post", "Level", "Role");
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return domainIds;
	}

	/**
	 * 获取讨论区某一版块的帖信息----新方法
	 * 
	 * @param boardId
	 * @param eliteFlag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<V3xBbsArticle> ArticleByBoardIdList(Long boardId,
			int pageSize, boolean eliteFlag, String condition,
			String textfield, String textfield1, boolean isDept) {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();

		List<Long> domainIds = this.getDomainIds4User(user);

		// 我是管理员
		boolean isAdmin = this.bbsBoardManager.validUserIsAdmin(boardId,
				currentUserId);
		if (isDept) {
			isAdmin = true;
		}

		String[] hql = new String[4];
		// 所要获取的内容，随需更换
		hql[0] = SELECT_FIELDS;
		// 按照人名查询时，将人员表关联进来
		hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
				+ V3xBbsArticleIssueArea.class.getName() + " as c ";
		// 查询条件，随需增加
		hql[2] = " where a.state=0 and a.boardId = :boardId and a.id=c.articleId ";
		// 排序或分组统计，随需更换
		hql[3] = " order by a.topSequence desc,a.issueTime desc";
		if (isDept) {
			// 部门讨论情况下无需加入发布范围关联查询
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a ";
			hql[2] = " where a.state=0 and a.boardId = :boardId ";
		}

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("boardId", boardId);

		// 查询
		if ("issueUser".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
					+ V3xBbsArticleIssueArea.class.getName() + " as c, "
					+ V3xOrgMember.class.getName() + " as m ";
			if (isDept) {
				hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
						+ V3xOrgMember.class.getName() + " as m ";
			}
			// 加入一个匿名判断，如果当前用户希望搜索"匿名"用户发帖,那么应当允许用户查找到所有匿名发表的讨论主题,否则只能查询到实名发帖
			if (!textfield.contains(ResourceBundleUtil.getString(
					BbsUtil.BBS_I18N_RESOURCE, "anonymous.label"))) {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=0 and m.name like :creatorName ";
				namedParameters.put("creatorName", "%"
						+ SQLWildcardUtil.escape(textfield) + "%");
			} else {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=1 ";
			}
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[2] += " and a.articleName like :articleName ";
			namedParameters.put("articleName", "%"
					+ SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				hql[2] += " and a.issueTime>= :beginTime ";
				namedParameters.put("beginTime", stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				hql[2] += " and a.issueTime<= :endTime ";
				namedParameters.put("endTime", stamp);
			}
		}

		if (eliteFlag) {
			hql[2] += " and a.eliteFlag=true ";
		}

		if (!isDept) {
			if (!isAdmin) {// 我是管理员
				hql[2] += " and (c.moduleId in (:domainIds) or a.issueUserId=:currentUserId) ";
				namedParameters.put("domainIds", domainIds);
				namedParameters.put("currentUserId", currentUserId);
			}
		}

		List<Object[]> list = null;
		if (pageSize > 0) {
			list = (List<Object[]>) super.find(hql[0] + hql[1] + hql[2]
					+ hql[3], 0, pageSize, namedParameters);
		} else {
			list = (List<Object[]>) super.find(hql[0] + hql[1] + hql[2]
					+ hql[3], "a.id", true, namedParameters);
		}

		return this.objArr2ArticleList(list);
	}

	/**
	 * 获取讨论区某一版块的帖信息----新方法(不过滤管理员)
	 * 
	 * @param boardId
	 * @param eliteFlag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<V3xBbsArticle> allArticleByBoardIdList(Long boardId,
			int pageSize, boolean eliteFlag, String condition,
			String textfield, String textfield1, boolean isDept) {
		User user = CurrentUser.get();
		Long currentUserId = user.getId();
		
		// 判断是否是当前板块的管理员
		boolean isAdmin = this.bbsBoardManager.validUserIsAdmin( boardId, currentUserId );
		
		List<Long> domainIds = this.getDomainIds4User(user);

		String[] hql = new String[4];
		// 所要获取的内容，随需更换
		hql[0] = SELECT_FIELDS;
		// 按照人名查询时，将人员表关联进来
		if( !isAdmin ){
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
					+ V3xBbsArticleIssueArea.class.getName() + " as c ";
		}else{
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a ";
		}
		
		// 查询条件，随需增加
		hql[2] = " where a.state=0 and a.boardId = :boardId ";
		// 排序或分组统计，随需更换
		hql[3] = " order by a.topSequence desc,a.issueTime desc";

		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("boardId", boardId);

		// 查询
		if ( ("issueUser".equals(condition) || "author".equals(condition) ) && Strings.isNotBlank(textfield)) {//自定义团队空间的“本空间讨论”按发起者查询时为author
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a , "
					+ V3xBbsArticleIssueArea.class.getName() + " as c, "
					+ V3xOrgMember.class.getName() + " as m ";
			// 加入一个匿名判断，如果当前用户希望搜索"匿名"用户发帖,那么应当允许用户查找到所有匿名发表的讨论主题,否则只能查询到实名发帖
			if (!textfield.contains(ResourceBundleUtil.getString(
					BbsUtil.BBS_I18N_RESOURCE, "anonymous.label"))) {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=0 and m.name like :creatorName ";
				namedParameters.put("creatorName", "%"
						+ SQLWildcardUtil.escape(textfield) + "%");
			} else {
				hql[2] += " and a.issueUserId=m.id and a.anonymousFlag=1 ";
			}
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			hql[2] += " and a.articleName like :articleName";
			namedParameters.put("articleName", "%"
					+ SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				hql[2] += " and a.issueTime>= :beginTime ";
				namedParameters.put("beginTime", stamp); 
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				hql[2] += " and a.issueTime<= :endTime ";
				namedParameters.put("endTime", stamp);
			}
		}

		if (eliteFlag) {
			hql[2] += " and a.eliteFlag=true ";
		}

		if (!isDept) {
			if( !isAdmin ){//普通用户需要加上发布范围c.moduleId限制和发布者a.issueUserId限制；板块管理员无限制
				hql[2] += " and a.id = c.articleId and (c.moduleId in (:domainIds) or a.issueUserId=:currentUserId) ";
				namedParameters.put("domainIds", domainIds);
				namedParameters.put("currentUserId", currentUserId);
			}
		} else {
			hql[1] = " from " + V3xBbsArticle.class.getName() + " as a ";
		}

		//log.info("bbs.sql="+hql[0] + hql[1] + hql[2]+ hql[3]);
		//log.info("bbs.param="+namedParameters);
		
		List<Object[]> list = null;
		if (pageSize > 0) {
			list = (List<Object[]>) super.find(hql[0] + hql[1] + hql[2]
					+ hql[3], 0, pageSize, namedParameters);
		} else {
			list = (List<Object[]>) super.find(hql[0] + hql[1] + hql[2]
					+ hql[3], "a.id", true, namedParameters);
		}

		return this.objArr2ArticleList(list);
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getBoardsArticleNumber(boolean isGroup) {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		if (isGroup) {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.GROUP;
		} else {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.CORPORATION;
		}

		Map<Long, Integer> result = new HashMap<Long, Integer>();

		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, false,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getCustomBoardsArticleNumber(long spaceId, int spaceType, boolean isElite) {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		if (spaceType == 5) {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM;
		} else {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.PUBLIC_CUSTOM_GROUP;
		}
		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams;
		if (isElite) {
			hqlAndParams = this.queryArticleList(spaceId, affiliateroom, -1, true, null, null, null, false, null, true);
		} else {
			hqlAndParams = this.queryArticleList(spaceId, affiliateroom, -1, false, null, null, null, false, null, true);
		}
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1] + hql[2] + " group by a.boardId";
		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1, -1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}
		return result;
	}

	/**
	 * 管理员查看其可以管理的讨论版块时，查看每个版块的帖子总数，无需自己在发布范围内
	 */
	public Map<Long, Integer> getBoardsArticleNumber4Admin(
			List<Long> adminBoardIds) {
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		String countSql = "select a.boardId, count(distinct a.id) " + " from "
				+ V3xBbsArticle.class.getName() + " as a "
				+ " where a.boardId in(:adminBoardIds) and a.state=0 "
				+ " group by a.boardId";
		namedParameters.put("adminBoardIds", adminBoardIds);
		Map<Long, Integer> result = new HashMap<Long, Integer>();

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	/**
	 * 管理员查看其可以管理的讨论版块时，查看每个版块的精华帖总数，无需自己在发布范围内
	 */
	public Map<Long, Integer> getBoardsElitePostNumber4Admin(
			List<Long> adminBoardIds) {
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		String countSql = "select a.boardId, count(distinct a.id) "
				+ " from "
				+ V3xBbsArticle.class.getName()
				+ " as a "
				+ " where a.boardId in(:adminBoardIds) and a.state=0 and a.eliteFlag=true "
				+ " group by a.boardId";
		namedParameters.put("adminBoardIds", adminBoardIds);
		Map<Long, Integer> result = new HashMap<Long, Integer>();

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getDeptBoardsArticleNumber(boolean isDept) {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT;

		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, false,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql,
				namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getBoardsElitePostNumber(boolean isGroup) {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		if (isGroup) {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.GROUP;
		} else {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.CORPORATION;
		}

		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, true,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getDeptBoardsElitePostNumber() {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT;

		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, true,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getBoardsReplyNumber(boolean isGroup) {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		if (isGroup) {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.GROUP;
		} else {
			affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.CORPORATION;
		}

		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, false,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, count(distinct a.id) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getDeptBoardsReplyNumber() {
		BbsConstants.BBS_BOARD_AFFILITER affiliateroom = null;
		affiliateroom = BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT;

		Map<Long, Integer> result = new HashMap<Long, Integer>();
		Object[] hqlAndParams = this.queryArticleList(affiliateroom, -1, false,
				null, null, null, false, null, true);
		String[] hql = (String[]) hqlAndParams[0];
		Map<String, Object> namedParameters = (Map<String, Object>) hqlAndParams[1];
		String countSql = "select a.boardId, sum(a.replyNumber) " + hql[1]
				+ hql[2] + " group by a.boardId";

		List<Object[]> totalCount = (List<Object[]>) super.find(countSql, -1,
				-1, namedParameters);
		if (totalCount != null) {
			for (Object[] objects : totalCount) {
				result.put((Long) objects[0], (Integer) objects[1]);
			}
		}

		return result;
	}

	/**
	 * 方法描述：判断讨论区某一版块今天是否有新的主题
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public Boolean hasNewTodayArticle(Long boardId) throws Exception {
		// TODO 加字段
		return false;
	}

	/**
	 * 方法描述：判断讨论区某一版块今天是否有回复信息
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public Boolean hasNewTodayReplyPost(Long boardId) throws Exception {
		// TODO 加字段
		return false;
	}

	/**
	 * 方法描述：新建讨论区主题
	 * 
	 * @param v3xBbsArtile
	 *            主题信息
	 * @throws Exception
	 */
	public void createArticle(V3xBbsArticle v3xBbsArtile) throws Exception {
		this.save(v3xBbsArtile);
	}

	/**
	 * 方法描述：修改讨论区主题
	 * 
	 * @param v3xBbsArtile
	 *            主题信息
	 * @throws Exception
	 */
	public void updateArticle(V3xBbsArticle v3xBbsArtile) throws Exception {
		this.update(v3xBbsArtile);
	}

	/**
	 * 方法描述：修改讨论区主题下方的回复 added by Meng Yang 2009-05-07
	 * 
	 * @param v3xBbsArticleReply
	 *            讨论区主题下方的回复
	 * @throws Exception
	 */
	public void updateArticleReply(V3xBbsArticleReply v3xBbsArticleReply)
			throws Exception {
		this.update(v3xBbsArticleReply);
	}

	/**
	 * 方法描述：回复讨论区主题
	 * 
	 * @param v3xBbsArtile
	 *            主题信息
	 * @throws Exception
	 */
	public void replyArticle(V3xBbsArticleReply v3xBbsArticleReply,
			int oldReplyNumber) throws Exception {
		this.save(v3xBbsArticleReply);
		Long articleId = v3xBbsArticleReply.getArticleId();

		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("replyNumber", oldReplyNumber + 1);
		this.update(articleId, columns);
	}

	/**
	 * 创建讨论回复，同时更新主贴的回复总数、点击总数
	 */
	public void replyArticle(V3xBbsArticleReply v3xBbsArticleReply,
			int oldReplyNumber, int clickNumber) throws Exception {
		this.save(v3xBbsArticleReply);
		Long articleId = v3xBbsArticleReply.getArticleId();

		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("replyNumber", oldReplyNumber + 1);
		columns.put("clickNumber", clickNumber);
		this.update(articleId, columns);
	}

	/**
	 * 方法描述：根据主题ID查询该帖信息
	 * 
	 * @return V3xBbsArticle
	 * @throws Exception
	 * 
	 */
	public V3xBbsArticle getArticleById(Long articleid) throws Exception {
		return this.get(articleid);
	}

	/**
	 * 方法描述：获取讨论区某一主题的回复信息
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception
	 */
	public List<V3xBbsArticleReply> listReplyByArticleId(Long articleId)
			throws Exception {
		return bbsArticleReplyDao.listReplyByArticleId(articleId);
	}

	/**
	 * 方法描述：获取讨论区某一主题的回复信息 按条数抽取
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception
	 */
	public List<V3xBbsArticleReply> listReplyByArticleId(Long articleId,
			int beginRow, int pageSize,String orderValue) throws Exception {
		return bbsArticleReplyDao.listReplyByArticleId(articleId, beginRow,
				pageSize,orderValue);
	}

	/**
	 * 方法描述：获取讨论区某一主题的回复信息总数
	 * 
	 * @return List 回复信息的集合
	 * @throws Exception
	 */
	public int countReplyByArticleId(Long articleId) throws Exception {
		return bbsArticleReplyDao.countReplyByArticleId(articleId);
	}

	/**
	 * 方法描述：根据ID查询该回复帖信息
	 * 
	 * @return V3xBbsArticleReply
	 * @throws Exception
	 * 
	 */
	public V3xBbsArticleReply getReplyPostById(Long postId) throws Exception {
		return bbsArticleReplyDao.getReplyPostById(postId);
	}

	/**
	 * 方法描述：逻辑删除主题信息,将主题的state设置为1（为删除状态）
	 * 
	 * @param articleId
	 *            主题Id
	 * @throws Exception
	 */
	public void deleteArticle(Long articleId) throws Exception {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("state", (byte) 1);
		this.update(articleId, columns);
	}

	/**
	 * 方法描述：逻辑删除某一条回复帖信息，,将回复帖的state设置为1（为删除状态）
	 * 
	 * @param replyPostId
	 *            回复帖ID
	 * @throws Exception
	 */
	public void deleteReplyPost(Long replyPostId, Long articleId)
			throws Exception {
		bbsArticleReplyDao.deleteReplyPost(replyPostId, articleId);
	}

	/**
	 * 方法描述：逻辑删除某一主题下的所有回复帖信息
	 * 
	 * @param articleId
	 *            主题ID
	 * @throws Exception
	 */
	public void deleteReplyPostByArticleId(Long articleId) throws Exception {
		bbsArticleReplyDao.deleteReplyPostByArticleId(articleId);
	}

	/**
	 * 方法描述：点击某一主题，该主题的点击数加一
	 * 
	 * @param articleId
	 *            主题ID
	 * @throws Exception
	 */
	public void updateClickNumber(Long articleId, int oldClickNumber)
			throws Exception {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("clickNumber", oldClickNumber + 1);
		this.update(articleId, columns);
	}

	/**
	 * 方法描述：添加发布范围
	 */
	public void addArticleIssueArea(List<V3xBbsArticleIssueArea> list)
			throws Exception {
		vbsArticleIssueAreaDao.addArticleIssueAreas(list);
	}

	/**
	 * 添加发布范围
	 * 
	 * @param articleId
	 * @param moduleType
	 * @param moduleId
	 */
	public void addArticleIssueArea(Long articleId, String moduleType,
			Long moduleId) {
		V3xBbsArticleIssueArea issueArea = new V3xBbsArticleIssueArea(
				articleId, moduleType, moduleId);
		this.save(issueArea);
	}

	/**
	 * 方法描述：获取发布范围
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticleIssueArea> getIssueArea(Long articleId)
			throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				V3xBbsArticleIssueArea.class).add(
				Expression.eq("articleId", articleId));
		return super.executeCriteria(criteria, -1, -1);
	}

	/**
	 * 获取索引信息
	 */
	public IndexInfo getIndexInfo(long id) throws Exception {
		V3xBbsArticle article = getArticleById(id);
		if (article == null){
			log.warn("ID为"+id+"的讨论不存在");
			return null;
		}
		IndexInfo indexInfo = new IndexInfo();
		indexInfo.setTitle(article.getArticleName());
		// 添加判断是否匿名发布者： huangfj 2011-09-29
		if (article.getAnonymousFlag()) {
			//匿名者 不可链接查询
			indexInfo.setStartMemberId(-1L);
		} else {
			indexInfo.setStartMemberId(article.getIssueUserId());
		}
		indexInfo.setHasAttachment(article.isHasAttachments());
		indexInfo.setTypeId(article.getBoardId());
		StringBuffer content = new StringBuffer();
		if (article.getContent() != null) {
			content.append(article.getContent().replaceAll("null", ""));
		}
		List<V3xBbsArticleReply> replies = listReplyByArticleId(id);
		if (replies != null) {
			for (V3xBbsArticleReply reply : replies) {
				if (reply.getContent() != null) {
					content.append(reply.getContent().replaceAll("null", ""));
					// 添加判断是否匿名回帖者： huangfj 2011-09-29
					if (reply.getAnonymousFlag()) {
						// 在此添加：匿名
						content.append(" "+ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE,"anonymous.label") + " ");
					} else {
						content.append(" "+orgManager.getMemberById(reply.getReplyUserId()).getName() + " ");
					}
					content.append(reply.getReplyName() + " ");
				}
			}
		}
		indexInfo.setContent(content.toString());
		indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
		indexInfo.setEntityID(article.getId());
		indexInfo.setAppType(ApplicationCategoryEnum.bbs);
		indexInfo.setCreateDate(new Date(article.getIssueTime().getTime()));
		// 添加判断是否匿名发布者： huangfj 2011-09-29
		if (article.getAnonymousFlag()) {
			// 在此添加：匿名
			indexInfo.setAuthor(ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE, "anonymous.label"));
		} else {
			String issueUserName = this.orgManager.getMemberById(article.getIssueUserId()).getName();
			indexInfo.setAuthor(issueUserName);
		}
		List<V3xBbsArticleIssueArea> scopes = getIssueArea(id);
		List<String> ownerList = new ArrayList<String>();

		ownerList.add(article.getIssueUserId().toString());// 在此添加作者
		List<String> departmentList = new ArrayList<String>();
		List<String> accountList = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();
		for (V3xBbsArticleIssueArea scope : scopes) {
			Long moduleId = scope.getModuleId();
			if (moduleId == null) {
				continue;
			}
			if ("Member".equals(scope.getModuleType())) {
				ownerList.add(String.valueOf(moduleId));
				V3xOrgMember member = orgManager.getMemberById(moduleId);
				if (member == null) {
					continue;
				}
				sb.append(member.getName() + " ");
			} else if ("Department".equals(scope.getModuleType())) {
				departmentList.add(String.valueOf(moduleId));
				V3xOrgDepartment dept = orgManager.getDepartmentById(moduleId);
				if (dept == null) {
					continue;
				}
				sb.append(dept.getName() + " ");
			} else if ("Account".equals(scope.getModuleType())) {
				// 判断是否是集团
				V3xOrgAccount account = orgManager.getAccountById(moduleId);
				if (account == null) {
					continue;
				}
				if (account.getIsRoot()) {
					ownerList.add("ALL");
				} else {
					accountList.add(String.valueOf(moduleId));
				}

				sb.append(account.getName() + " ");
			}
		}
		indexInfo.setKeyword(sb.toString());
		// 部门讨论发布范围
		V3xBbsBoard board = bbsBoardManager.getBoardById(article.getBoardId());
		if (board != null
				&& board.getAffiliateroomFlag() == BbsConstants.BBS_BOARD_AFFILITER.DEPARTMENT
						.ordinal()) {
			departmentList.add(article.getBoardId().toString());
		}
		AuthorizationInfo authorizationInfo = new AuthorizationInfo();
		if (ownerList.size() > 0)
			authorizationInfo.setOwner(ownerList);
		if (departmentList.size() > 0)
			authorizationInfo.setDepartment(departmentList);
		if (accountList.size() > 0)
			authorizationInfo.setAccount(accountList);
		indexInfo.setAuthorizationInfo(authorizationInfo);

		List<Long> projectList = new ArrayList<Long>();
		projectList.add(article.getBoardId());
		indexInfo.getAuthorizationInfo().setProject(projectList);

		// 在此对附件做出适配
		IndexUtil.convertToAccessory(indexInfo);
		return indexInfo;
	}

	/**
	 * 方法描述：获取主页显示的部门讨论区新主题 add by Dongjw ,2007-05-23
	 * 
	 * @param pagesize
	 * @return List
	 */
	public List<V3xBbsArticle> DeptqueryArticleList(Long departmentId,
			int pageSize, String condition, String textfield, String textfield1) {
		return this.ProjectqueryArticleList(departmentId, pageSize, null,
				condition, textfield, textfield1);
	}

	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> ProjectqueryArticleList(Long departmentId,
			int pageSize, Long phaseId, String condition, String textfield,
			String textfield1) {
		List<Object> indexParameters = new ArrayList<Object>();
		String middleHql = " from " + V3xBbsArticle.class.getName()
				+ " as a where a.boardId=? and a.state=0 ";
		indexParameters.add(departmentId);

		if ("author".equals(condition) && Strings.isNotBlank(textfield)) {
			middleHql = " from " + V3xBbsArticle.class.getName() + " as a, " + V3xOrgMember.class.getName() + " as m "
				+ " where a.boardId=? and a.state=0 and a.issueUserId=m.id and m.name like ? ";
			indexParameters.add("%" + SQLWildcardUtil.escape(textfield) + "%");
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			middleHql += " and a.articleName like ? ";
			indexParameters.add("%" + SQLWildcardUtil.escape(textfield) + "%");
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				middleHql += " and a.issueTime>=? ";
				indexParameters.add(stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				middleHql += " and a.issueTime<=? ";
				indexParameters.add(stamp);
			}
		}

		if (phaseId != null && phaseId != 1) {
			middleHql += " and a.id in (select ph.eventId from "
					+ ProjectPhaseEvent.class.getName() + " as ph"
					+ " where ph.phaseId=? and ph.eventType="
					+ ApplicationCategoryEnum.bbs.key() + ") ";
			indexParameters.add(phaseId);
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select a.id, a.articleName, a.boardId, a.issueUserId, a.clickNumber, a.replyNumber, a.issueTime, a.eliteFlag, a.messageNotifyFlag, a.resourceFlag, a.topSequence, a.anonymousFlag, a.anonymousReplyFlag, a.identifier ");
		hql.append(middleHql);
		hql.append(" order by a.topSequence desc,a.issueTime desc");
		
		if (pageSize > 0) {
			return this.objArr2ArticleList((List<Object[]>) super.find(hql
					.toString(), 0, pageSize, null, indexParameters));
		} else {
			return this.objArr2ArticleList((List<Object[]>) super.find(hql
					.toString(), "a.id", true, null, indexParameters));
		}
	}

	/**
	 * 条件查询项目讨论区某阶段的所有主题
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> projectQueryArticleListByCondition(String condition, Long departmentId, int pageSize,
			Long phaseId, Map<String, Object> paramMap) {
		List<Object> indexParameters = new ArrayList<Object>();
		String middleHql = " from " + V3xBbsArticle.class.getName()
				+ " as a where a.boardId=? and a.state=0 ";
		indexParameters.add(departmentId);

		if ("author".equals(condition)) {
			if (paramMap.get("author") != null && !"".equals(paramMap.get("author").toString())) {
				middleHql = " from "
						+ V3xBbsArticle.class.getName()
						+ " as a, "
						+ V3xOrgMember.class.getName()
						+ " as m "
						+ " where a.boardId=? and a.state=0 and a.issueUserId=m.id and m.name like ? ";
				indexParameters.add("%" + SQLWildcardUtil.escape(paramMap.get("author").toString()) + "%");
			} else 
				return null ;
		} 
		if ("title".equals(condition)) {
			if (paramMap.get("title") != null && !"".equals(paramMap.get("title").toString())) {
				middleHql += " and a.articleName like ? ";
				indexParameters.add("%" + SQLWildcardUtil.escape(paramMap.get("title").toString()) + "%");
			} else 
				return null ;
		}
		if ("publishDate".equals(condition)) {
			if (paramMap.get("publishDate") != null && !"".equals(paramMap.get("publishDate").toString())) {
				middleHql += " and a.issueTime>=? and a.issueTime<=?" ;
				indexParameters.add(Datetimes.getTodayFirstTime(paramMap.get("publishDate").toString())) ;
				indexParameters.add(Datetimes.getTodayLastTime(paramMap.get("publishDate").toString())) ;
			} else
				return null ;
		}

		if (phaseId != null && phaseId != 1) {
			middleHql += " and a.id in (select ph.eventId from "
					+ ProjectPhaseEvent.class.getName() + " as ph"
					+ " where ph.phaseId=? and ph.eventType="
					+ ApplicationCategoryEnum.bbs.key() + ") ";
			indexParameters.add(phaseId);
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select a.id, a.articleName, a.boardId, a.issueUserId, a.clickNumber, a.replyNumber, a.issueTime, a.eliteFlag, a.messageNotifyFlag, a.resourceFlag, a.topSequence, a.anonymousFlag, a.anonymousReplyFlag, a.identifier ");
		hql.append(middleHql);
		hql.append(" order by a.topSequence desc,a.issueTime desc");
		if (pageSize > 0) {
			return this.objArr2ArticleList((List<Object[]>) super.find(hql
					.toString(), 0, pageSize, null, indexParameters));
		} else {
			return this.objArr2ArticleList((List<Object[]>) super.find(hql
					.toString(), "a.id", true, null, indexParameters));
		}
	}
	
	/**
	 * 方法描述：获取主页显示的部门讨论区所有主题 add by Dongjw ,2007-05-23
	 * 
	 * @param pagesize
	 * @return List
	 */
	public List<V3xBbsArticle> deptlistAllArticle(long departmentId,
			String condition, String textfield, String textfield1)
			throws Exception {
		return this.DeptqueryArticleList(departmentId, -1, condition,
				textfield, textfield1);
	}

	// 版块发帖统计
	public List<BbsCountArticle> countArticle(String countType,
			String departmentid, Long boardId) throws Exception {
		return bbsArticleDao.countArticle(countType, departmentid, boardId);
	}

	/**
	 * 获取匿名发帖统计结果，包含按照日、周、月和全部的统计结果
	 * 
	 * @param anonymousList
	 */
	public AnonymousCountModel getAnonymousCount4Statistic(
			List<BbsCountArticle> anonymousList) {
		long dayNum = 0; // 本日匿名发帖总数
		long weekNum = 0; // 本周匿名发帖总数
		long monthNum = 0; // 本月匿名发帖总数
		long allNum = 0; // 匿名发帖总数
		if (anonymousList != null && anonymousList.size() > 0) {
			for (BbsCountArticle countArticle : anonymousList) {
				dayNum += countArticle.getDayCount();
				weekNum += countArticle.getWeekCount();
				monthNum += countArticle.getMonthCount();
				allNum += countArticle.getAllCount();
			}
		}
		return new AnonymousCountModel(dayNum, weekNum, monthNum, allNum);
	}

	// 置顶主题
	public void topArticle(String articleId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topSequence", BbsConstants.BBS_ARTICLE_IS_TOP);
		this.update(new Long(articleId), map);
	}

	// 取消置顶主题
	public void cancelTopArticle(String articleId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topSequence", BbsConstants.BBS_ARTICLE_ISNOT_TOP);
		this.update(Long.parseLong(articleId), map);
	}

	/**
	 * 将管理员选中的讨论主题取消置顶
	 * 
	 * @param articleIds
	 */
	public void cancelTopArticle(String[] articleIds) {
		List<Long> ids = this.getIdsFromStr(articleIds);
		if (ids != null && ids.size() > 0)
			this.bbsArticleDao.cancelTop(ids);
	}

	/**
	 * 将字符串数组解析为<code>List<Long></code>
	 * 
	 * @param articleIds
	 * @return
	 */
	private List<Long> getIdsFromStr(String[] articleIds) {
		if (articleIds != null && articleIds.length > 0) {
			List<Long> ids = new ArrayList<Long>();
			for (String idStr : articleIds) {
				ids.add(Long.parseLong(idStr));
			}
			return ids;
		}
		return null;
	}

	/**
	 * 将管理员选中的讨论主题置顶
	 * 
	 * @param articleIds
	 */
	public void topArticle(String[] articleIds) {
		List<Long> ids = this.getIdsFromStr(articleIds);
		if (ids != null && ids.size() > 0)
			this.bbsArticleDao.top(ids);
	}

	/**
	 * 将管理员选中的讨论主题标识为精华帖
	 * 
	 * @param articleIds
	 */
	public void eliteArticle(String[] articleIds) {
		List<Long> ids = this.getIdsFromStr(articleIds);
		if (ids != null && ids.size() > 0)
			this.bbsArticleDao.elite(ids);
	}

	/**
	 * 将管理员选中的讨论主题取消精华标识
	 * 
	 * @param articleIds
	 */
	public void cancelEliteArticle(String[] articleIds) {
		List<Long> ids = this.getIdsFromStr(articleIds);
		if (ids != null && ids.size() > 0)
			this.bbsArticleDao.cancelElite(ids);
	}

	// 精华主题
	public void eliteArticle(String articleId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("eliteFlag", true);
		this.update(Long.parseLong(articleId), map);
	}

	// 取消精华主题
	public void cancelEliteArticle(String articleId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("eliteFlag", false);
		this.update(Long.parseLong(articleId), map);
	}

	// 删除主题
	public void delArticle(String articleId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", BbsConstants.BBS_ARTICLE_ISNOT_ACTIVE);
		this.update(Long.parseLong(articleId), map);
	}

	/**
	 * 综合查询
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsArticle> iSearch(ConditionModel cModel) throws Exception {
		String title = cModel.getTitle();
		final Date beginDate = cModel.getBeginDate();
		final Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		Long currentUserId = CurrentUser.get().getId();

		Map<String, Object> params = new HashMap<String, Object>();
		String selectFields = SELECT_FIELDS;

		String from = " from "
				+ V3xBbsArticle.class.getName()
				+ " a, "
				+ V3xBbsArticleIssueArea.class.getName()
				+ " c "
				+ "where a.state=0 and a.id=c.articleId and c.moduleId in (:domainIds) ";

		// 如果是我发布的，无需进行发布范围判定和重复过滤，如果是（他人）发给我的，则需进行发布范围判定和重复过滤
		if (fromUserId != null && fromUserId.equals(currentUserId)) {
			selectFields = "select a.id, a.articleName, a.boardId, a.issueUserId, a.clickNumber, a.replyNumber, "
					+ "a.issueTime, a.eliteFlag, a.messageNotifyFlag, a.resourceFlag, a.topSequence, "
					+ "a.anonymousFlag, a.anonymousReplyFlag, a.identifier ";

			from = " from " + V3xBbsArticle.class.getName()
					+ " a where a.state=0 and a.issueUserId= :userId ";
			params.put("userId", fromUserId);
		} else {
			List<Long> domainIds = this.getDomainIds4User(CurrentUser.get());
			params.put("domainIds", domainIds);
		}

		StringBuffer queryCondition = new StringBuffer("");
		// 他人发给我的
		if (fromUserId != null && !fromUserId.equals(currentUserId)) {
			queryCondition.append(" and a.issueUserId= :sender ");
			params.put("sender", fromUserId);
		}
		if (Strings.isNotBlank(title)) {
			queryCondition.append(" and a.articleName like :articleName ");
			params
					.put("articleName", "%" + SQLWildcardUtil.escape(title)
							+ "%");
		}
		if (beginDate != null) {
			queryCondition.append(" and a.issueTime>= :beginDate ");
			params.put("beginDate", beginDate);
		}
		if (endDate != null) {
			queryCondition.append(" and a.issueTime<= :endDate ");
			params.put("endDate", endDate);
		}
		String queryHql = selectFields + from + queryCondition.toString()
				+ " order by a.issueTime desc";

		List<Object[]> result = null;
		// 我发布的
		if (fromUserId != null && fromUserId.equals(currentUserId)) {
			result = super.find(queryHql, params);
		}
		// （他人）发给我的
		else {
			result = super.find(queryHql, "a.id", true, params);
		}
		return this.objArr2ArticleList(result);
	}

	/**
	 * 方法描述：获取单位主页显示的讨论区新主题 供web services调用 add by Yongzhang ,2009-02-21
	 * 
	 * @param pagesize
	 * @return List
	 */
	public List<V3xBbsArticle> queryArticleListToWS(long accountId,
			long personId, int pageSize, String condition, String textfield,
			String textfield1) {
		List<V3xBbsArticle> articleList = new ArrayList<V3xBbsArticle>();
		Map<String, Object> sql = this.getQueryArticleListTows(accountId,
				personId, BbsConstants.BBS_BOARD_AFFILITER.CORPORATION,
				pageSize, false, condition, textfield, textfield1, false, null);
		if (sql == null) {
			return articleList;
		}

		String middleHql = (String) sql.get("SQL");
		List<Object> indexParameters = (List<Object>) sql
				.get("IndexParameters");
		Map<String, Object> namedParameters = (Map<String, Object>) sql
				.get("NamedParameters");

		String hql = SELECT_FIELDS;
		hql += middleHql;
		hql += " order by a.topSequence desc,a.issueTime desc";

		List<Object[]> list = null;
		if (pageSize > 0) {
			list = super.find(hql, 0, pageSize, namedParameters,
					indexParameters);
		} else {
			list = super.find(hql, "a.id", true, namedParameters,
					indexParameters);
		}
		return objArr2ArticleList(list);
	}

	private Map<String, Object> getQueryArticleListTows(long accountId,
			long personId, BbsConstants.BBS_BOARD_AFFILITER affiliateroom,
			int pageSize, boolean eliteFlag, String condition,
			String textfield, String textfield1, boolean isDept, String boardId) {
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(personId,
					V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account", "Department",
					"Team", "Member", "Post", "Level", "Role");
		} catch (BusinessException e) {
			log.error("", e);
		}

		List<V3xBbsBoard> adminBoardIds = null; // 所有的板块-------内存取
		List<Long> adminBoardId = new ArrayList<Long>();
		if (affiliateroom.equals(BbsConstants.BBS_BOARD_AFFILITER.CORPORATION)) { // 单位
			adminBoardIds = this.bbsBoardManager
					.getAllCorporationBbsBoard(accountId);

		} else if (affiliateroom.equals(BbsConstants.BBS_BOARD_AFFILITER.GROUP)) { // 集团
			adminBoardIds = this.bbsBoardManager.getAllGroupBbsBoard();
		}
		// 取部门ID
		for (V3xBbsBoard board : adminBoardIds) {
			adminBoardId.add(board.getId());

		}

		List<Object> indexParameters = new ArrayList<Object>();
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		String middleHql = "";
		middleHql += " from " + V3xBbsArticle.class.getName() + " as a ";
		middleHql += " where ";
		// ~~~~查询用
		if ("issueUser".equals(condition) && Strings.isNotBlank(textfield)) {
			middleHql = " from " + V3xBbsArticle.class.getName() + " as a, "
					+ V3xOrgMember.class.getName() + " as m ";
			middleHql += " where (a.issueUserId=m.id) and ( a.anonymousFlag=0 )";
			middleHql += " and (m.name like ?) ";

			indexParameters.add("%" + textfield + "%");
			middleHql += " and ";
		} else if ("subject".equals(condition) && Strings.isNotBlank(textfield)) {
			middleHql += "  (a.articleName like ?)";
			indexParameters.add("%" + textfield + "%");
			middleHql += " and ";
		} else if ("issueTime".equals(condition)) {
			if (Strings.isNotBlank(textfield)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				middleHql += "  (a.issueTime>=?) and ";
				indexParameters.add(stamp);
			}
			if (Strings.isNotBlank(textfield1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				middleHql += " (a.issueTime<=?) and ";
				indexParameters.add(stamp);
			}

		}
		if (eliteFlag) {
			middleHql += " (a.eliteFlag=true) and ";
		}

		middleHql += "  a.id in(select distinct b.id ";
		middleHql += " from " + V3xBbsArticle.class.getName() + " as b ,"
				+ V3xBbsArticleIssueArea.class.getName() + " as c ";
		middleHql += " where  ";

		if (!adminBoardIds.isEmpty()) {
			middleHql += "  ( b.boardId in (:adminBoardId) ";
			namedParameters.put("adminBoardId", adminBoardId);
			middleHql += " and b.state=0 ";
			middleHql += " and b.id = c.articleId ";
		} else {
			middleHql += " (  b.boardId=0  and  b.state=0  ";// 板块一个都没有的时候
			middleHql += " and b.id = c.articleId ";
		}
		// 查询单板块的讨论
		if (boardId != null && !boardId.equals("")) {
			middleHql += " and b.boardId=" + boardId;
		}
		// if(!isAdmin){//我是管理员
		middleHql += " and ( c.moduleId in (:domainIds) ";
		namedParameters.put("domainIds", domainIds);

		middleHql += " or b.issueUserId=:currentUserId ";
		namedParameters.put("currentUserId", personId);

		middleHql += " )";
		// }
		middleHql += " )";
		middleHql += " )";

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("SQL", middleHql);
		result.put("IndexParameters", indexParameters);
		result.put("NamedParameters", namedParameters);

		return result;
	}

	public void updateClick(long dataId, int clickNumTotal,
			Collection<ClickDetail> details) {
		String hql = "update " + V3xBbsArticle.class.getName()
				+ " as a set a.clickNumber=? where a.id=?";
		this.bbsArticleDao.bulkUpdate(hql, null, clickNumTotal, dataId);
	}

	/**
	 * 删除某个讨论的所有发布范围
	 * 
	 * @param id
	 */
	public void deleteArticleIssueAreasByArticleId(Long articleId) {
		vbsArticleIssueAreaDao
				.delete(new Object[][] { { "articleId", articleId } });
	}

}