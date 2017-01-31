/**
 * $Id: V3xOrgMemberWithLoginName.java,v 1.3 2009/06/03 08:06:48 tanmf Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * V3xOrgMemberWithLoginName.java created by paul at 2007-8-13 上午11:05:57
 *
 */
package com.seeyon.v3x.hr.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.organization.UserPrincipalUtil;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * <tt>V3xOrgMemberWithLoginName</tt>用于直接获取<tt>V3xOrgMember</tt>和LoginName
 * @author paul
 *
 */
public class V3xOrgMemberWithLoginName extends V3xOrgMember {
	private static final long serialVersionUID = 1L;
	private static Log LOG = LogFactory.getLog(V3xOrgMemberWithLoginName.class);
	private V3xOrgMember member;
	private String fullPath;
	public V3xOrgMemberWithLoginName() {
		
	}
	public V3xOrgMemberWithLoginName(V3xOrgMember member, String fullPath) {
		this.member = member;
		this.fullPath = fullPath;
		this.member.setLoginName(this.myLoginName());
		this.copyProperties(this.member);
	}
	
	public V3xOrgMember getMember() {
		return member;
	}
	public void setMember(V3xOrgMember member) {
		this.member = member;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}	
	public String getFullPath() {
		return fullPath;
	}
	
	//截取fullPath得到loginName
	private String myLoginName() {
		return UserPrincipalUtil.getPrincipalNameFromFullPath(fullPath);
	}
	
	//将V3xOrgMember的属性转换到V3xOrgMemberWithLoginName中
	private void copyProperties(V3xOrgMember member) {
		try {
			BeanUtils.copyProperties(this, member);
		} catch (IllegalAccessException e) {
			LOG.error("", e);
		} catch (InvocationTargetException e) {
			LOG.error("", e);
		}
	}
}
