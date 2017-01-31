package com.seeyon.v3x.bulletin.util.hql;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;

/**
 * 公共信息Hql工具类基类，提供公告、新闻、讨论、调查Hql工具类若干共用方法
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-7-30
 */
public class PublicInfoHqlUtils {
	private static final Log logger = LogFactory.getLog(PublicInfoHqlUtils.class);

	/**
	 * 从用户在综合查询界面输入的各项值获取搜索信息
	 * @param conditionModel 综合查询条件模型
	 * @return SearchInfo 不会为空
	 */
	public static SearchInfo getSearchInfo(ConditionModel conditionModel) {
		SearchInfo result = new SearchInfo();

		result.setTitle(conditionModel.getTitle());
		result.setBeginDate(conditionModel.getBeginDate());
		result.setEndDate(conditionModel.getEndDate());
		result.setCreatorId(conditionModel.getFromUserId());
		result.setFromIsearch(true);

		return result;
	}
	
	public static final String Only_One_Date = "-1";

	/**
	 * 从用户在公告列表搜索框选择的搜索类型和输入的搜索值获取搜索信息
	 * @param condition 	搜索类型
	 * @param textfield 	搜索值
	 * @param textfield1 	搜索值(按照日期进行查询时需设置结束日期)
	 * @return SearchInfo	可能为空，获取结果使用时需进行校验
	 */
	public static SearchInfo getSearchInfo(String condition, String textfield, String textfield1) {
		SearchInfo result = null;
		if(StringUtils.isNotBlank(condition)) {
			SearchType type = null;
			try {
				type = SearchType.getSearchType(condition);
			} catch(IllegalArgumentException e) {
				logger.warn("不合法的搜索类型：[" + condition +"]", e);
			}

			if(type != null) {
				result = new SearchInfo();
				switch(type) {
				case By_Title :
					result.setTitle(textfield);
					break;
				case By_Publish_User :
					result.setCreatorName(textfield);
					break;
				//暂将发布、创建日期合在一起，日后需要分开时只需扩展SearchInfo属性即可
				case By_Publish_Date :
				case By_Create_Date :
					if(StringUtils.isNotBlank(textfield)) {
						result.setBeginDate(Datetimes.getTodayFirstTime(textfield));
					}
					
					if(Only_One_Date.equals(textfield1)) {
						result.setEndDate(Datetimes.getTodayLastTime(textfield));
					}
					else if(StringUtils.isNotBlank(textfield1)) {
						result.setEndDate(Datetimes.getTodayLastTime(textfield1));
					}
					break;
				case By_Bul_Type :
					result.setBulTypeId(NumberUtils.toLong(textfield));
					break;
				case By_Top_Flag :
					result.setTopFlag(true);
				case None:
					break;
				}
			}
		}
		
		return result;
	}

	/**
	 * 从用户在公共信息列表搜索框选择的搜索类型和输入的搜索值获取搜索信息
	 * @return SearchInfo	可能为空，获取结果使用时需进行校验
	 * @see #getSearchInfo(String, String, String)
	 */
	public static SearchInfo getSearchInfo(HttpServletRequest request) {
		return getSearchInfo(request.getParameter("condition"), request.getParameter("textfield"), request.getParameter("textfield1"));
	}

	/** 从用户在综合查询界面输入的各项值获取用户信息，所得结果不会为空  */
	public static UserInfo getUserInfo(ConditionModel conditionModel, OrgManager orgManager) {
		User user = conditionModel.getUser();
		Long userId = user.getId();
		UserInfo result = new UserInfo(Constants.VisitRole.User, userId);

		if(!userId.equals(conditionModel.getFromUserId()))
			result.setDomainIds(FormBizConfigUtils.getUserDomainIds(user, orgManager));

		return result;
	}
	
	/** 将当前用户信息包装为UserInfo */
	public static UserInfo getUserInfo(OrgManager orgManager) {
		User user = CurrentUser.get();
		return getUserInfo(user, orgManager);
	}
	
	public static UserInfo getUserInfo(User user, OrgManager orgManager) {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, user.getId());
		userInfo.setDomainIds(FormBizConfigUtils.getUserDomainIds(user, orgManager));
		return userInfo;
	}
	
	public static UserInfo getUserInfo(Long userId, OrgManager orgManager) {
		UserInfo userInfo = new UserInfo(Constants.VisitRole.User, userId);
		userInfo.setDomainIds(FormBizConfigUtils.getUserDomainIds(userId, orgManager));
		return userInfo;
	}

}
