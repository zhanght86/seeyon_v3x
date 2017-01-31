package com.seeyon.v3x.meetingroom.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */
public class Constants{
	
	/** 会议室状态：正常 */
	public final static int Status_MeetingRoom_Normal = 0;
	/** 会议室状态：停用 */
	public final static int Status_MeetingRoom_Stop = 1;
	/** 会议室需要申请 */
	public final static int Type_MeetingRoom_NeedApp = 1;
	/** 会议室不需要申请 */
	public final static int Type_MeetingRoom_NoNeedApp = 0; 
	/** 删除标志：是 */
	public final static int DelFlag_Yes = 1;
	/** 删除标志：否 */
	public final static int DelFlag_No = 0;
	/** 查询条件：等于 */
	public final static int Condition_not = -1;
	/** 查询条件：等于 */
	public final static int Condition_eq = 0;
	/** 查询条件：大于 */
	public final static int Condition_gt = 1;
	/** 查询条件：大于等于 */
	public final static int Condition_ge = 2;
	/** 查询条件：小于 */
	public final static int Condition_lt = 3;
	/** 查询条件：小于等于 */
	public final static int Condition_le = 4;
	/** 待审批 */
	public final static int Status_App_Wait = 0;
	/** 审批通过 */
	public final static int Status_App_Yes = 1;
	/** 审批未通过 */
	public final static int Status_App_No = 2;
	
	/**
	 * 把静态字段放到HashMap中，方便调用
	 * @return
	 */
	public static HashMap getMeetingRoomConstantsInstance(){
		Class c = Constants.class;
		Field[] fs = c.getFields();
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		for(int i = 0; i < fs.length; i++){
			try{
				hm.put(fs[i].getName(), fs[i].getInt(c));
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return hm;
	}
	
	public static void main(String[] args){
		Locale local = new Locale("zh","CN");
		java.util.ResourceBundle b = java.util.ResourceBundle.getBundle("com.seeyon.v3x.meetingroom.resources.i18n.MeetingRoomResources", local);
		System.out.println(b.getString("mr.tab.add"));
	}
	
}
