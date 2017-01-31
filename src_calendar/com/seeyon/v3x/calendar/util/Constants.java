package com.seeyon.v3x.calendar.util;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 日程事件的常量定义---NEW
 *
 * @author wolf
 *
 */
public class Constants {

	public static final int AL_FLAG_0 = 0; // 提醒标识---0默认不提醒

	public static final int AL_FLAG_1 = 1; // 提醒时间---1 提醒

//	public static final int EVENT_TYPE_1 = 1; // 事件类型 --1.自办
//
//	public static final int EVENT_TYPE_2 = 2; // 事件类型--2.安排
//
//	public static final int EVENT_TYPE_3 = 3; // 事件类型--3.委托
//
//	public static final int EVENT_TYPE_4 = 4; // 事件类型--4.项目

//	public static final int PRI_Type_1 = 1; // 优先级--1.低
//
//	public static final int PRI_Type_2 = 2; // 优先级--2.中
//
//	public static final int PRI_Type_3 = 3; // 优先级--3.高

//	public static final int SHARE_TYPE_1 = 1; // 共享类型--1.私人事件
//
//	public static final int SHARE_TYPE_2 = 2; // 共享类型--2.公开事件
//
//	public static final int SHARE_TYPE_3 = 3; // 共享类型--3.共享给上级
//
//	public static final int SHARE_TYPE_4 = 4; // 共享类型--4.共享给下级
//
//	public static final int SHARE_TYPE_5 = 5; // 共享类型--5.共享给部门
//
//	public static final int SHARE_TYPE_6 = 6; // 共享类型--6.共享给项目

	public static final int SIGNIFY_TYPE_1 = 1; // 重要程度（1.重要紧急）
//
	public static final int SIGNIFY_TYPE_2 = 2; // 重要程度（2.不重要紧急）
//
	public static final int SIGNIFY_TYPE_3 = 3; // 重要程度（3.重要不紧急）
//
	public static final int SIGNIFY_TYPE_4 = 4; // 重要程度（4.不重要不紧急）

//	public static final int STATES_1 = 1; // 事件完成类型（1.待安排 ）
//
//	public static final int STATES_2 = 2; // 事件完成类型（2.已安排）
//
//	public static final int STATES_3 = 3; // 事件完成类型（3.进行中 ）
//
//	public static final int STATES_4 = 4; // 事件完成类型（4.已完成）

//	public static final int WORK_TYPE_1 = 1; // 工作类型（1.自办）
//
//	public static final int WORK_TYPE_2 = 2; // 工作类型（2.督办）
//
//	public static final int WORK_TYPE_3 = 3; // 工作类型（3.协办）

	public static final int EVENT_FLAG_0 = 0; // 事件当前类型标识（0.初试状态 ）

	public static final int EVENT_FLAG_1 = 1; // 事件当前类型标识（1.已安排）

	public static final int EVENT_FLAG_2 = 2; // 事件当前类型标识（2.已委托）

	/**
	 *  对事件进行直接委托，安排
	 */
	public static final int TRAN_TYPE_1 = 1;

	/**
	 *  事件 --公开他人
	 */
	public static final int TRAN_TYPE_2 = 2;

	/**
	 *  事件 --公开部门
	 */
	public static final int TRAN_TYPE_3 = 3;

	/**
	 * 事件 --公开项目
	 */
	public static final int TRAN_TYPE_4 = 4;

	/**
	 * 委托，安排事件被修改
	 */
	public static final int TRAN_TYPE_5 = 5;

	/**
	 * 日期格式 2008-01-01
	 */
	/**
	 * 共享事件 -- 全部
	 */
	public static final String ALL = "all";
	/**
	 * 共享事件 -- 部门
	 */
	public static final String DEPARTMENT ="department";
	/**
	 * 共享事件 -- 项目
	 */
	public static final String PROJECT = "project";
	/**
	 * 共享事件 -- 他人
	 */
	public static final String OTHERS = "others"; 
	/**
	 * 标题
	 */
	public static final String SUBJECT = "subject";
	/**
	 * 重要程度
	 */
	public static final String SIGNIFYTYPE = "signifyType";
	/**
	 * 开始时间
	 */
	public static final String BEGINDATE = "beginDate";
	/**
	 * 所属人
	 */
	public static final String 	RECEIVEMEMBERNAME = "receiveMemberName";
	
	/**
	 * 状态
	 */
	public static final String STATES = "states";

	/**
	 * 操作成功标志
	 */
	public static final String RESULT_SUCCESS = "success";

	/**
	 * 操作失败标志
	 */
	public static final String RESULT_FAILURE = "failure";

	/**
	 * 是否处于开发测试阶段
	 */
	public static final Boolean IS_TEST = true;

	/**
	 * 事件管理的CATEGORY = 11
	 */
	public static final int CALENDAR_CATEGORY = ApplicationCategoryEnum.calendar
			.getKey();

	private static final String resource_cal = "com.seeyon.v3x.calendar.resources.i18n.CalendarResources";

	public static final String COMMON_RESOURCE_BASENAME = "com.seeyon.v3x.calendar.resources.i18n.CalendarResources";

	/**
	 * 取得事件状态的值 参数 int类型 ，如key为cal.event.states.2 返回 "已安排"
	 */
	public static String getStateValue(int state) {
		return ResourceBundleUtil.getString(resource_cal, "cal.event.states."
				+ state);
	}

	/**
	 * 取得工作类型的值
	 *
	 */
	public static String getPlanType(String type) {
		return ResourceBundleUtil.getString(resource_cal, "cal.event.planType."
				+ type);
	}

	/**
	 * 根据key获取本地化后的字符串
	 *
	 * @param key
	 * @return
	 */
	public static String getResourceStr(String key, Object... values) {
		return ResourceBundleUtil.getString(resource_cal, key, values);
	}

	/** 首页日程事件栏目中所要获取的日程事件数量：8条 */
	public static final int Section_Fetch_Count = 8;

	/** 最多显示2条更早（7日内）的日程事件 */
	public static final int Section_Fetch_Earlier_Count = 2;

	/** 最多显示2条更晚（含明日）（7日内）的日程事件 */
	public static final int Section_Fetch_Later_Count = 2;


	/** 事件状态：未安排 */
	public static final int Status_NotArranged = 1;
	/** 事件状态：已安排 */
	public static final int Status_Arranged = 2;
	/** 事件状态：进行中 */
	public static final int Status_InProcess = 3;
	/** 事件状态：已完成 */
	public static final int Status_Finished = 4;

	/** 日程事件时间范围类型：今日(含跨日)、更晚(含明日)、更早 */
	public static enum DateRangeType {
		today,
		antipodean,
		tomorrow,
		later,
		earlier;
	}

	public static enum PeriodicalType {
		Once,//一次性事件--不记录数据库
		EveryDay,
		EveryWeek,
		EveryMonthDay,
		EveryMonthWeekDay,
		EveryYearMonthDay,
		EveryYearMonthWeekDay
	}
	/**
	 * 周期性提醒类型：按天提醒、按周提醒、按月提醒、按年提醒
	 */
	public static enum PeriodicalStyle {
		/** 不选 */
		None,
		/** 按天提醒 */
		Day,
		/** 按周提醒 */
		Week,
		/** 按月提醒 */
		Month,
		/** 按年提醒 */
		Year
	}

	/*所有事件重要程度类型*/
	private static List<Integer> allSignifyType = null;
	
	/*得到所有事件重要程度类型*/
	public static List<Integer> getAllSignifyType(){   
        if(allSignifyType == null){   
        	allSignifyType = new ArrayList<Integer>();
        	allSignifyType.add(SIGNIFY_TYPE_1);   
        	allSignifyType.add(SIGNIFY_TYPE_2);
        	allSignifyType.add(SIGNIFY_TYPE_3);
        	allSignifyType.add(SIGNIFY_TYPE_4);
        }   
        return allSignifyType;   
	}
}
