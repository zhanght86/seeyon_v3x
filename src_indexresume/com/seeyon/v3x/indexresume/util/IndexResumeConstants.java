package com.seeyon.v3x.indexresume.util;


public class IndexResumeConstants {
	public static final String INDEX_RESUME_CONFIGURATION = "com.v3x.plugin.indexresume.Configuration";
	/**
	 * 启用状态
	 */
	public static final String RESUME_STATE = "RESUME_STATE";
	/**
	 * 恢复开始启动的时间
	 */
	public static final String RESUME_START_TIME = "RESUME_START_TIME";
	/**
	 * 恢复程序停止的时间
	 */
	public static final String RESUME_END_TIME = "RESUME_END_TIME";
	/**
	 * 恢复类型
	 */
	public static final String RESUME_APP_TYPE = "RESUME_APP_TYPE";
	/**
	 * 恢复选择范围
	 */
	public static final String RESUME_DATE_SCOPE = "RESUME_DATE_SCOPE";//如:2009-03-20|2010-04-12
	
	/**
	 * 恢复结束标记
	 */
	public static final String RESUME_OVER_FLAG = "OVER";//如:2009-03-20|2010-04-12
	/**
	 * 一次取最多100条的固定数目进行恢复操作
	 */
	public static final int PAGE_SIZE=100;
	/**
	 * 分页处理
	 */
	public static int[]  getFromIndex(int totalCount)
	{
		if(totalCount==0){return null;}
		  int totalPage = totalCount%IndexResumeConstants.PAGE_SIZE==0? totalCount/IndexResumeConstants.PAGE_SIZE:totalCount/IndexResumeConstants.PAGE_SIZE + 1;
		  int[] fromIndexArray=new int[totalPage];
		  
		  for (int i = 1; i < totalPage+1; i++) {
			    int fromIndex=(i-1)*IndexResumeConstants.PAGE_SIZE;
				fromIndexArray[i-1]=fromIndex;
		  }
		  return fromIndexArray;
	}
}
