package com.seeyon.v3x.taskmanage.utils;

import com.seeyon.v3x.util.Strings;
import com.thoughtworks.xstream.XStream;

/**
 * 任务管理常量、枚举定义类
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public abstract class TaskConstants {
	
	/**
	 * 任务状态枚举
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-16
	 */
	public static enum TaskStatus {
		/** 
		 * 未开始
		 */
		NotStarted(1),
		/** 
		 * 进行中
		 */
		Marching(2),
		/** 
		 * 已完成
		 */
		Finished(4),
		/** 
		 * 已延迟
		 */
		Delayed(3),
		/** 
		 * 已取消
		 */
		Canceled(5);
		
		private TaskStatus(int key) {
			this.key = key;
		}
		
		private int key;
		
		public int key() {
			return key;
		}
		
		public static TaskStatus valueOf(int key) {
			TaskStatus[] arr = TaskStatus.values();
			for(TaskStatus ts : arr) {
				if(ts.key() == key) {
					return ts;
				}
			}
			
			throw new IllegalArgumentException("非法任务状态[Status=" + key + "]!");
		}
	}

	/**
	 * 列表类型：我的任务 or 任务管理
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-14
	 */
	public static enum ListType {
		/**
		 * 个人任务
		 */
		Personal(0),
		/**
		 * 已发任务
		 */
		Sent(1),
		
		/**
		 * 任务管理
		 */
		Manage(3),
		
		/**
		 * 项目领导、负责人或助理任务(可以查看项目成员的所有任务)
		 */
		ProjectAll(4),
		/**
		 * 项目成员任务(只能查看成员自己在项目中所领的任务)
		 */
		ProjectMember(5),
		
		/**
		 * 上级任务列表
		 */
		Parent(6),
		/**
		 * 工作统计处的任务列表
		 */
		Statistic(7);
		
		private ListType(int key) {
			this.key = key;
		}
		
		private int key;
		public int key() {
			return this.key;
		}
		
		public static ListType valueOf(int key) {
			ListType[] types = ListType.values();
			for(ListType type : types) {
				if(type.key() == key) {
					return type;
				}
			}
			throw new IllegalArgumentException("非法列表类型[Key=" + key + "]!");
		}
		
		public static ListType parseName(String typeName) {
			if(Strings.isBlank(typeName))
				return Personal;
			
			ListType[] types = ListType.values();
			for(ListType type : types) {
				if(type.name().equals(typeName)) {
					return type;
				}
			}
			throw new IllegalArgumentException("非法列表类型[TypeName=" + typeName + "]!");
		}
	}
	
	/**
	 * 在修改任务时，日程事件需要同步，在此定义同步的操作类型
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-16
	 */
	public static enum CalEventSyncType {
		/**
		 * 修改前导入，修改后不导入，则应删除日程事件
		 */
		Delete,
		/**
		 * 修改前导入，修改后也导入，则应更新日程事件
		 */
		Update,
		/**
		 * 修改前不导入，修改后导入，则应保存日程事件
		 */
		Save,
		/**
		 * 修改前后均不导入，不做任何操作
		 */
		None;
	}
	
	/**
	 * 任务权限类型枚举，包括：新建、分解任务、修改、查看、回复、汇报
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-15
	 */
	public static enum TaskAclEnum {
		/** 新建任务 */
		Add,
		/** 通过分解新建子任务 */
		Decompose,
		/** 修改任务 */
		Edit,
		/** 查看任务 */
		View,
		/** 回复任务 */
		Reply,
		/** 任务汇报  */
		Feedback
	}
	
	/**
	 * 统计时间段，包括日、周、月、自定义时间段及全部。<br>
	 * 其中：<br>
	 * 日、周、月及自定义时间段内只统计<b>进行中、已完成和已延期</b>三种状态的任务总数，<br>
	 * 全部时间范围内则只统计<b>未开始和已取消</b>两种状态的任务总数。
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-9
	 */
	public static enum StatisticPeriod {
		/** 本日  */
		Day(3),
		/** 本周  */
		Week(3),
		/** 本月  */
		Month(3),
		/** 自定义时间段  */
		Custom(3),
		/** 全部(仅限于未开始和已取消两种状态的任务统计)  */
		All(2);
		
		private StatisticPeriod(int statusTotal) {
			this.statusTotal = statusTotal;
		}
		/**
		 * 每种统计时间段内所要统计的状态总数
		 */
		private int statusTotal;
		
		public int statusTotal() {
			return statusTotal;
		}
		
		/**
		 * 获取最终统计结果的数组长度
		 */
		public static int sumStatusTotal() {
			int ret = 0;
			for(StatisticPeriod sp : StatisticPeriod.values()) {
				ret += sp.statusTotal;
			}
			return ret;
		}
		
		public static StatisticPeriod parseOrdinal(int ordinal) {
			for(StatisticPeriod sp : StatisticPeriod.values()) {
				if(sp.ordinal() == ordinal) {
					return sp;
				}
			}
			return null;
		}
	}
	
	/**
	 * 不提醒
	 */
	public static final int NO_REMIND = -1;
	
	/**
	 * 任务管理的国际化资源文件路径
	 */
	public static final String TASK_I18N_RES = "com.seeyon.v3x.taskmanage.resources.i18n.TaskManageResources";
	
	/**
	 * 新建任务入口：分解任务
	 */
	public static final String FROM_DECOMPOSE = "Decompose";
	/**
	 * 新建任务或列表入口：项目任务
	 */
	public static final String FROM_PROJECT = "Project";
	
	/**
	 * 按照人员进行页面导航，值:Member
	 */
	public static final String NAVIGATION_BY_MEMBER = "Member";
	/**
	 * 按照项目进行页面导航，值:Project
	 */
	public static final String NAVIGATION_BY_PROJECT = "Project";

	/**
	 * 辅助开发过程中的调试，输出格式整齐的xml key - value信息
	 */
	public static final XStream xStream4Debug = new XStream();
	
	/**
	 * 项目阶段：全部(等价于取项目所有信息)
	 */
	public static final long PROJECT_PHASE_ALL = 1l;
	
	/**
	 * 不与项目关联，作为任务信息项目关联默认值
	 */
	public static final long PROJECT_NONE = -1l;
	
	/**
	 * 项目任务统计中，对所有人员的任务统计求和时不能简单累加，需要单独查询获取合计结果，用此标识
	 */
	public static final Long STATISTIC_SUM_MEMBERS = -1l;
	
	/**
	 * 重要程度 - 普通，值为1
	 */
	public static final int IMPORTANCE_COMMON = 1;
	
	/**
	 * 重要程度 - 重要，值为2
	 */
	public static final int IMPORTANCE_IMPORTANT = 2;
	
	/**
	 * 重要程度 - 非常重要，值为3
	 */
	public static final int IMPORTANCE_VERY_IMPORTANT = 3;
}
