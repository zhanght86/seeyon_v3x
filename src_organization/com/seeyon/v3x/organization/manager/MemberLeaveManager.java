/**
 * Id: MemberLeaveService.java, v1.0 2011-12-1 wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.v3x.organization.manager;

import java.sql.Timestamp;
import java.util.List;

import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * @Project/Product: A8（A8）
 * @Description: 人员离职管理
 * @Copyright: Copyright (c) 2011 of Seeyon, Ltd.
 * @author: wangchw
 * @time: 2011-12-1 下午06:01:53
 * @version: v1.0
 */
public interface MemberLeaveManager {

	/**
	 * 改变离职用户的状态信息：将离职用户踢下线和将用户状态信息设置为离职状态
	 * @param agent_to_id
	 * @param user
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public void changeUserSate(String agent_to_id,User user) throws Exception;
	
	/**
	 * 根据userid获得该用户管理的讨论板块列表
	 * @param userid
	 * @return
	 */
	public List getBbsList(String userid);
	
	/**
	 * 根据userid获得该用户审核的公告板块列表
	 * @param userid
	 * @return
	 */
	public List getBulTypeAuditList(String userid);
	
	/**
	 * 根据用户id，获得由其管理的公告列表
	 * @param userid
	 * @return
	 */
	public List getBulTypeList(String userid);
	
	/**
	 * 获得所属人为指定人员的表单模板列表
	 * @param userid
	 * @return
	 * @throws BusinessException
	 * @throws DataDefineException
	 */
	public List getFormAppList(String userid) throws BusinessException, DataDefineException;
	
	/**
	 * 根据userid获得该用户审核的调查板块列表
	 * @param userid
	 * @return
	 */
	public List getInquiryAuditList(String userid);
	
	/**
	 * 根据userid获得该用户管理的调查板块列表
	 * @param userid
	 * @return
	 */
	public List getInquiryList(String userid);
	
	/**
	 * 根据指定的用户id，获得由其管理的部门空间列表
	 * @param userid
	 * @return
	 */
	public List getManagementSpaceList(String userid);
	
	/**
	 * 根据userid获得该用户审核的新闻板块列表
	 * @param userid
	 * @return
	 */
	public List getNewsAuditList(String userid);
	
	/**
	 * 根据userid获得该用户管理的新闻板块列表
	 * @param userid
	 * @return
	 */
	public List getNewsList(String userid);
	
	/**
	 * 根据userid获得该用户是否为综合办公的管理员
	 * @param userid
	 * @return
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public List getOfficeAdminListByUserId(String userid) throws NumberFormatException, BusinessException;
	
	/**
	 * 根据userid获得该用户还没有归还的综合办公物品列表
	 * @param userid
	 * @return
	 */
	public List getOfficeDeviceListByUserId(String userid);
	
	/**
	 * 根据用户id获得其所负责的所有项目列表
	 * @param userid
	 * @return
	 */
	public List getProjectManagerListByUserId(String userid);
	
	/**
	 * 获得离职人员的角色列表
	 * @param userid
	 * @return
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public List<String[]> getRolesByUserId(String userid) throws NumberFormatException, BusinessException;
	
	/**
	 * 
	 * @param leave1UserId
	 * @param agent_to_id
	 * @param distance
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public V3xAgent handleCurrentAgentInfo1(String leave1UserId,String agent_to_id,Timestamp startTime,Timestamp endTime,User user) throws Exception;
	
	/**
	 * 
	 * @param leave2UserId
	 * @param agent_to_id
	 * @param distance
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public V3xAgent handleCurrentAgentInfo2(String leave2UserId,String agent_to_id,Timestamp startTime,Timestamp endTime,User user) throws Exception;
	
	/**
	 * 
	 * @param leave4UserId
	 * @param agent_to_id
	 * @param distance
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public V3xAgent handleCurrentAgentInfo4(String leave4UserId,String agent_to_id, Timestamp startTime,Timestamp endTime, User user) throws Exception;
	
	/**
	 * 
	 * @param leave9UserId
	 * @param agent_to_id
	 * @param distance
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public V3xAgent handleCurrentAgentInfo9(String leave9UserId,String agent_to_id, Timestamp startTime,Timestamp endTime, User user) throws Exception;
	
	/**
	 * 
	 * @param agent_to_id
	 * @param agent
	 * @param leave1UserId
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyInfo1(String agent_to_id,V3xAgent agent,String leave1UserId) throws NumberFormatException, Exception;
	
	/**
	 * 
	 * @param agent_to_id
	 * @param agent
	 * @param leave2UserId
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyInfo2(String agent_to_id, V3xAgent agent,String leave2UserId) throws NumberFormatException, Exception;
	
	/**
	 * 
	 * @param agent_to_id
	 * @param agent
	 * @param leave4UserId
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyInfo4(String agent_to_id, V3xAgent agent,String leave4UserId) throws NumberFormatException, Exception;
	
	/**
	 * 
	 * @param agent_to_id
	 * @param agent
	 * @param leave9UserId
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyInfo9(String agent_to_id, V3xAgent agent,String leave9UserId) throws NumberFormatException, Exception;
	
	/**
	 * 处理我指派给别人干活的代理列表(模板流程)
	 * @param agent_to_id
	 * @param agent
	 * @param leave1UserId
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyedInfo1(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception;

	/**
	 * 处理我指派给别人干活的代理列表(自由协同)
	 * @param agent_to_id
	 * @param agent
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyedInfo2(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception;
	
	/**
	 * 处理我指派给别人干活的代理列表(公文流程)
	 * @param agent_to_id
	 * @param agent
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyedInfo4(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception;

	/**
	 * 处理我指派给别人干活的代理列表(公共信息待审核)
	 * @param agent_to_id
	 * @param agent
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void handleOldProxyedInfo9(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception;
}
