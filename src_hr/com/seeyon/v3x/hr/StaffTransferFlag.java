package com.seeyon.v3x.hr;

public class StaffTransferFlag {

	/**
	 * 处理结果
	 */
	public final static int CONSENT = 1;//同意
	public final static int DISACCORD = 2;//不同意
	public final static int UNSETTLED = 3;//未处理
	
	/**
	 * 调配类型
	 */
	public final static int FULLMEMBER = 1;//转正
	public final static int TRANSFERPOST = 2;//平级调任
	public final static int UPGRADE = 3;//升职
	public final static int DEMOTION = 4;//降职
	public final static int DIMISSION = 5;//离职
	public final static int OTHER =6;//其它
	
	/**
	 * 变动类型
	 */
	public final static int TRANSFER = 1;//调配
	public final static int DIMISSIONS = 2;//离职
	
	
}
