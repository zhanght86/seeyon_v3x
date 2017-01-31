/**
 * $Id: AddressBookManager.java,v 1.16 2011/02/24 05:57:58 renhy Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.manager;

import java.io.File;
import java.util.List;

import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.addressbook.domain.AddressBookTeam;

/**
 *
 * <p/> Title: 个人组/类别<外部接口>
 * </p>
 * <p/> Description: 个人组/类别<外部接口>
 * </p>
 * <p/> Date: 2007-5-25
 * </p>
 * @author paul(qdlake@gmail.com)
 */
public interface AddressBookManager {
	
	public final static int TYPE_DISCUSS = 4; //讨论组
	public final static int TYPE_EMAIL = 3; //外部联系人-邮件
	public final static int TYPE_CATEGORY = 2; //外部联系人-类别
	public final static int TYPE_OWNTEAM = 1; //个人组
	
	/**
	 * 添加外部联系人
	 * @param member 外部联系人
	 */
	public void addMember(AddressBookMember member);
	public void updateMember(AddressBookMember member);
	public AddressBookMember getMember(Long memberId);
	/**
	 * 该用户创建的所有外部联系人
	 * @param creatorId 用户ID
	 * @return 外部联系人列表
	 */
	public List<AddressBookMember> getMembersByCreatorId(Long creatorId);
	public List<AddressBookMember> getMembersByTeamId(Long teamId);
	public void removeCategoryMembersByIds(Long creatorId, List<Long> memberIds);
	public void removeMembersByIds(Long creatorId, List<Long> memberIds);
	/**
	 * 该用户创建的所有类别
	 * @param creatorId 用户ID
	 * @return 类别列表
	 */
	public List<AddressBookTeam> getTeamsByCreatorId(Long creatorId);
	
	/**
	 * 添加个人组/类别
	 * @param team
	 */
	public void addTeam(AddressBookTeam team);
	public AddressBookTeam getTeam(Long teamId);
	
	/**
	 * 修改类别
	 * @param team
	 */
	public void updateTeam(AddressBookTeam team);
	
	/**
	 * 删除类别，以及关联成员
	 * @param teamId 类别id
	 */
	public void removeTeamById(Long teamId);
	
	/**
	 * 按名称查找员工
	 * @param name 员工名称
	 * @return
	 */
	public List getOrgMemByName(String name);
	public List getMemberByName(String name); //外部联系人
	
	/**
	 * 按手机号码查找员工
	 * @param tel 员工手机号码
	 * @return
	 */
	public List getMemberByTel(String tel);
	
	/**
	 * 按职务级别查找员工
	 * @param levelName 职务级别
	 * @return
	 */
	public List getOrgMemberByLevelName(String levelName);
	public List getMemberByLevelName(String levelName);//外部联系人
	
	/**
	 * 根据类型（邮件、类别）判断是否存在
	 */
	public boolean isExist(int type, String name, Long createId, Long accountId, String memberId); //外部联系人
	
	public String doImport(File file,String categoryId, String memberId)throws Exception;

	public String doCsvImport(File file, String categoryId,
			String memberId) throws Exception;
    
    public boolean isExistSameUserName(AddressBookMember member,Long createrId);
}
