package com.seeyon.v3x.plugin.videoconf.manager;

import java.util.*;

import com.seeyon.v3x.common.propertymapper.idmapper.GuidMapper;
import com.seeyon.v3x.organization.domain.*;

/**
 * 
 * @author modified by <a href="mailto:Radish Lee@seeyon.com"></a>
 * @version 2012-01-19
 */
public interface VideoConfSynchronManager {

	public static final String MAP_ACCOUNT = "account.videoConf";

	public static final String MAP_ACCOUNT_ERROR = "account.videoConf.error";

	public static final String MAP_ACCOUNT_BEFORE = "before.account.videoConf";

	public static final String MAP_DEPARTMENT = "department.videoConf";

	public static final String MAP_DEPARTMENT_ERROR = "department.videoConf.error";

	public static final String MAP_DEPARTMENT_BEFORE = "before.department.videoConf";

	public static final String MAP_MEMBER = "member.videoConf";

	public static final String MAP_MEMBER_ERROR = "member.videoConf.error";

	/**
	 * 异步方式同步数据
	 * 
	 * @param List
	 *            <V3xOrgAccount> accountList,boolean isOverOrgDate,GuidMapper
	 *            guidMapper
	 * @return void
	 * @throws Exception
	 */
	public String synchronStart(List<V3xOrgAccount> accountList,boolean isOverOrgDate, GuidMapper guidMapper) throws Exception;


}
