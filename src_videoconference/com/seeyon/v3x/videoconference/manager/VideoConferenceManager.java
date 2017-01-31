/**
 * 视频会议manager层
 * @author radishlee
 * @since 2012-1-10
 * @describe 视频会议业务层
 */
package com.seeyon.v3x.videoconference.manager;

import java.sql.SQLException;

import com.seeyon.v3x.organization.principal.NoSuchPrincipalException;

public interface VideoConferenceManager {
	/**
	 * 根据用户ID返回加密过的密码
	 * @author radishlee
	 * @since 2012-1-10
	 * @describe 根据用户ID返回加密过的密码
	 * @param Long userid
	 * @return String password
	 * @throws NoSuchPrincipalException 
	 */
	public String getEncryptedPassWD(Long userID) throws SQLException, NoSuchPrincipalException;
}
