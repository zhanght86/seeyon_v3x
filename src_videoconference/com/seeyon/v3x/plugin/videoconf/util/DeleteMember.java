package com.seeyon.v3x.plugin.videoconf.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;


public class DeleteMember {
	private static final Log log = LogFactory.getLog(DeleteMember.class);
	// 删除用户
	public static String deleteMember(String strUrl, String userName,String password,V3xOrgMember member) {
		String result = "";
		try {

			String url = strUrl;

			String strXML = "<?xml version='1.0' encoding='UTF-8'?>";
			strXML += "<Message><header>";
			strXML += "<action>" + "delCemUser" + "</action>";
			strXML += "<service>" + "siteadmin" + "</service>";
			strXML += "<siteName>" + "box" + "</siteName>";
			strXML += "<type>" + "XML" + "</type>";
			strXML += "<userName>" + userName + "</userName>";
			strXML += "<password>" + password + "</password>";
			strXML += "<version>" + "30" + "</version>";
			strXML += "</header>";
			// body开始
			strXML += "<body>";
			strXML += "<parameter>";
			strXML += "<type>" + "1" + "</type>";
			strXML += "<value>" + member.getLoginName() + "</value>";
			strXML += "</parameter>";
			strXML += "</body>";
			strXML += "</Message>";

			result = SendXMLToRedFir.send(url, strXML);
		} catch (Exception e) {
			log.error(e);
		}

		return result;
	}
}
