package com.seeyon.v3x.inquiry.util;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 调查的工具类,关于国际化资源文件方面的,此类有待扩展啊!!
 * 
 * @author IORIadmin
 * 
 */
public class ConstantsInquiry
{
	public static final String INQUIRY_RESOURCE_BASENAME = "com.seeyon.v3x.inquiry.resources.i18n.InquiryResources";
	//定义一下授权类型,位于InquirySurveytypeextend表中的manager_desc,0表示管理员,1,表示审核员
	public static final int INQUIRY_MANAGER_DESC_ADMIN=0;
	public static final int INQUIRY_MANAGER_DESC_AUDIT=1;
	
	//定义一下调查的几种状态
	/*-1.发布未开始
	1.审核未通过
	2.审核通过但未发布
	3.保存待发
	4.未审核
	5.终止状态
	6单位模板
	7集团模板
	8.发布状态
	10.归档*/
	public static final int INQUIRY_NO_AUDIT=4;
	
	/** 获取<b>调查</b>的国际化值 */
	public static String getI18NValues(String key, Object... values) {
		return ResourceBundleUtil.getString(INQUIRY_RESOURCE_BASENAME, key, values);
	}
}
