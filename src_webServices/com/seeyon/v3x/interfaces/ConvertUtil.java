package com.seeyon.v3x.interfaces;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class ConvertUtil {
	
	public static SimpleOrgMember getSimpleMember(V3xOrgMember member){
		SimpleOrgMember sMember=new SimpleOrgMember();
		sMember.setId(((Long)member.getId()).toString());
		sMember.setName(member.getName());
		sMember.setLoginName(member.getLoginName());
		long type=member.getType();
		sMember.setUserType(((Long)type).toString());
//		sMember
//		sMember	
		return sMember;
	}
	

}
