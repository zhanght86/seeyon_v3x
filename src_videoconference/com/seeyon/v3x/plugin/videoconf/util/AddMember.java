package com.seeyon.v3x.plugin.videoconf.util;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.security.MessageEncoder;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.principal.NoSuchPrincipalException;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;


public class AddMember {
	private static final Log log = LogFactory.getLog(AddMember.class);
	// 新增用户
	public static String addMember(String strUrl, String userName,String password,V3xOrgMember member,MessageEncoder encoder) throws NoSuchPrincipalException, SQLException {
		String result = "";
		String email = member.getEmailAddress() == null?"":member.getEmailAddress();
		try {

			String url = strUrl;

			String strXML = "<?xml version='1.0' encoding='UTF-8'?>";
			strXML += "<Message><header>";
			strXML += "<action>" + "createCemUser" + "</action>";
			strXML += "<service>" + "siteadmin" + "</service>";
			strXML += "<siteName>" + "box" + "</siteName>";
			strXML += "<type>" + "XML" + "</type>";
			strXML += "<userName>" + Constants.SYN_USER_NAME + "</userName>";
			strXML += "<password>" + Constants.SYN_PASSWORD + "</password>";
			strXML += "<version>" + "30" + "</version>";
			strXML += "</header>";
			// body开始
			strXML += "<body>";
			strXML += "<address>"  + "</address>";
			strXML += "<cellphone>" + "13831312345" + "</cellphone>";
			strXML += "<city>" + "</city>";
			strXML += "<company>" + "</company>";
			strXML += "<country>"  + "</country>";
			strXML += "<deptId>" + member.getOrgDepartmentId() + "</deptId>";
			strXML += "<roleIds>";
			strXML += "<roleId>" + Constants.USER_CREATE_CONF +"</roleId>";
			strXML += "</roleIds>";
			strXML += "<duty>" + member.getOrgLevelId() + "</duty>";
			strXML += "<email></email>";
			strXML += "<enabled>" + member.getEnabled() + "</enabled>";
			strXML += "<fax>"  + "</fax>";
			strXML += "<firstName>" + "</firstName>";
			strXML += "<forceCreate>" + "true" + "</forceCreate>";
			strXML += "<gender>" + "0" + "</gender>";
			strXML += "<nickname>" + "</nickname>";
			strXML += "<officePhone>" + "</officePhone>";
			strXML += "<otherEmail>" + "</otherEmail>";
			strXML += "<otherInfo>" + "</otherInfo>";
			strXML += "<otherPhone>"  + "</otherPhone>";
			
			strXML += "<password>" + encoder.encode(member.getLoginName(), "111111") + "</password>";
			strXML += "<postcode>" + "</postcode>";
			strXML += "<province>" + "</province>";
			strXML += "<reportTo>" + "</reportTo>";
			strXML += "<userName>" + member.getLoginName() + "</userName>";
			strXML += "<userType>" + "1" + "</userType>";
			strXML += "</body>";
			strXML += "</Message>";

			result = SendXMLToRedFir.send(url, strXML);

		} catch (Exception e) {
			log.error(e);
		}

		return result;
	}
}
