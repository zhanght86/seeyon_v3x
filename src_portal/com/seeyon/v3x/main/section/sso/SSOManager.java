/**
 * 
 */
package com.seeyon.v3x.main.section.sso;

import java.util.Map;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 */
public interface SSOManager {

	/**
	 * 清除原有的SSO信息
	 * 
	 * @param memberId
	 *            当前人员的id
	 * @param linkSystemId
	 *            关联系统的id
	 */
	public void clearSSO(long memberId, long linkSystemId);

	/**
	 * 先单点登录，再获取远程页面的内容<br>
	 * 
	 * 如果登录不成功，请在response中增加header项：LoginError=***
	 * 
	 * @param memberId
	 *            当前人员的id
	 * @param linkSystemId
	 *            关联系统的id
	 * @param sessionTimeout
	 *            登录失效时长，单位分钟，-1表示永久不失效
	 * @param pageURL
	 *            远程页面的URL
	 * @param pageCharset
	 *            远程页面的字符集
	 * @return
	 * @throws NoSuchLinkSystemException
	 * @throws LoginFailingException
	 */
	public String useSSO(long memberId, long linkSystemId,
			int sessionTimeout, String pageURL, String pageCharset)
			throws NoSuchLinkSystemException, LoginFailingException;

	/**
	 * 获取单点登录的cookie，之前必须调用{@link #useSSO(long, long, int, String, String)}
	 * 
	 * @param memberId
	 *            当前人员的id
	 * @param linkSystemId
	 *            关联系统的Id
	 * @return
	 */
	public Map<String, String> getCookies(long memberId, long linkSystemId);

}
