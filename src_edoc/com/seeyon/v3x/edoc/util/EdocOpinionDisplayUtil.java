package com.seeyon.v3x.edoc.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.controller.EdocController;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionDisplayConfig;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class EdocOpinionDisplayUtil {
	private static final Log log = LogFactory.getLog(EdocOpinionDisplayUtil.class);
	
	private static String getAttitude(Integer opinionType,int attitude,MetadataManager metadataManager){
		
		String attitudeStr=null;
		String attitudeI18nLabel = "";
		
		//查找国际化标签。
		if (attitude > 0) {
			if(ColOpinion.OpinionType.backOpinion.ordinal() == opinionType.intValue()){
				attitudeI18nLabel="stepBack.label";
			}else if(EdocOpinion.OpinionType.repealOpinion.ordinal() == opinionType.intValue()){
				attitudeI18nLabel="col.state.5.cancel";
			}else if(EdocOpinion.OpinionType.stopOpinion.ordinal() == opinionType.intValue()){
				attitudeI18nLabel = "col.state.10.stepstop";
			}else{
				attitudeI18nLabel = metadataManager.getMetadataItemLabel(MetadataNameEnum.collaboration_attitude, 
						Integer.toString(attitude));
			}
		}
		
		//查找用于显示的前台态度字符串
		ResourceBundle r = null;
		if (Strings.isNotBlank(attitudeI18nLabel)) {
			r = ResourceBundle.getBundle(
							"com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource",
							CurrentUser.get().getLocale());
			attitudeStr = ResourceBundleUtil.getString(r, attitudeI18nLabel);
		} else if ( attitude == com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL) {
			attitudeStr = null;
		}
		
		if (opinionType == EdocOpinion.OpinionType.senderOpinion.ordinal()) attitudeStr = null;
				
		return attitudeStr;
	}
	/**
	 * 取公文单显示的时候的人名 
	 * @param userId
	 * @param proxyName
	 * @param orgManager
	 * @return
	 */
	private static String getOpinionUserName(Long userId,String proxyName,OrgManager orgManager){
		String doUserName = "";
		try {
			V3xOrgMember member = orgManager.getMemberById(userId);
			doUserName = member.getName();
			if (member.getIsAdmin()) {
				// 如果是管理员终止，不显示管理员名字及时间
				doUserName = "";
			} else {
				doUserName = "<span class='link-blue' onclick='javascript:showV3XMemberCard(\""
						+ userId
						+ "\")'>"
						+ doUserName + "</span>";
			}

			if (!Strings.isBlank(proxyName)) {
				doUserName += ResourceBundleUtil
						.getString(
								"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
								"edoc.opinion.proxy", proxyName);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return doUserName;
	}
	private static String getDepartmentFullName(Long userId,String proxyName,OrgManager orgManager){
		StringBuffer fullName = new StringBuffer();
		fullName.append("&nbsp;&nbsp;");
		try {
			V3xOrgMember member = orgManager.getMemberById(userId);
			Long id = member.getOrgDepartmentId();
			V3xOrgDepartment dept = orgManager.getDepartmentById(id);
			if(dept != null){
				List<V3xOrgDepartment> pDs = orgManager.getAllParentDepartments(id);
				for (V3xOrgDepartment department : pDs) {
					fullName.append(department.getName()).append("&nbsp;");
				}
				fullName.append(dept.getName());
			}
		
			
		} catch (Exception e) {
			log.error(e);
		}
		fullName.append("&nbsp;&nbsp;");
		return fullName.toString();
		
	}
	/**
	 * 将意见对象转化为前台展现的JS串。
	 * @param map
	 * @return
	 */
	public static Map<String,Object> convertOpinionToString( Map<String,EdocOpinionModel> map,
			EdocOpinionDisplayConfig displayConfig,
			MetadataManager metadataManager,
			OrgManager orgManager){
		
		StringBuilder sb  = new StringBuilder();
		Map senderAttMap=new HashMap();
		List<EdocOpinion> senderOpinions=new ArrayList<EdocOpinion>();
		
		Map<String,Object> jsMap = new HashMap<String,Object>();
		for(Iterator<String> it = map.keySet().iterator();it.hasNext();){
			//公文单上元素位置
			String element = it.next();
			EdocOpinionModel model = map.get(element);
			List<EdocOpinion> opinions = model.getOpinions();
			for(EdocOpinion opinion : opinions){
				
				// 公文单不显示暂存待办意见
				if (opinion.getOpinionType().intValue() == EdocOpinion.OpinionType.provisionalOpinoin.ordinal()) 
					continue;
			
				//清空以前的数据.
				sb.delete(0,sb.length());
				String value = (String)jsMap .get(element);
				if(value!=null){
					sb.append(value);
				}
				if(sb.length()>0){
					sb.append("<br>");
				}
				
				String attribute = getAttitude(opinion.getOpinionType(), opinion.getAttribute(), metadataManager);
				
				String userName = getOpinionUserName(opinion.getCreateUserId(),opinion.getProxyName(),orgManager);
				
			
				String content = opinion.getContent();
				sb.append("<span style='clear: left;'>");
				if (attribute != null) {
					sb.append("【").append(attribute).append("】");
				}
				// 意见排序 ：【态度】 意见 部门 姓名 时间
				sb.append(" ").append(Strings.toHTML(content));
				
				if (displayConfig.isShowDeptName()) {
					sb.append(" ").append(getDepartmentFullName(opinion.getCreateUserId(),opinion.getProxyName(),orgManager));
				}
				// 如果是管理员终止，不显示管理员名字及时间
				V3xOrgMember member = getMember(opinion.getCreateUserId(),orgManager);
				if (!member.getIsAdmin()) {
					sb.append(" ").append(userName);
					if (displayConfig.getShowDate() == EdocOpinionDisplayConfig.DateFormat.dateTime.ordinal()) {
						sb.append(" ").append(Datetimes.formatDatetimeWithoutSecond(opinion.getCreateTime()));
					} else if (displayConfig.getShowDate() == EdocOpinionDisplayConfig.DateFormat.date.ordinal()) {
						sb.append(" ").append(Datetimes.formatDate(opinion.getCreateTime()));
					}
				}
				//附件显示
				List<Attachment> tempAtts = opinion.getOpinionAttachments();
				if (tempAtts != null)
				{
					sb.append("<br>");
					StringBuffer attSb = new StringBuffer();
					for (Attachment att : tempAtts) {
						// 不管文件名有多长，显示整体的文件名。yangzd
						String s = com.seeyon.v3x.common.filemanager.manager.Util
								.AttachmentToHtmlWithShowAllFileName(att,true, false);
						sb.append(s);
						attSb.append(s);
					}
					senderAttMap.put(opinion.getId(), attSb);
				}
				sb.append("</span>");
				if (opinion.getOpinionType() == EdocOpinion.OpinionType.senderOpinion.ordinal()) {
					senderOpinions.add(opinion);
				}
				//发起人附言如果没有绑定不向前台显示。前台页面通过下面的对象，有代码+标签的形式展示。
				if("senderOpinion".equals(element)) continue;
				jsMap.put(element, sb.toString());
			}
		}
		jsMap.put("senderOpinionAttStr",senderAttMap );
		jsMap.put("senderOpinionList", senderOpinions);
		return jsMap;	
	}
	
	private static V3xOrgMember getMember(Long id,OrgManager orgManager){
		V3xOrgMember member = new V3xOrgMember() ;
		try {
			member = orgManager.getMemberById(id);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return member;
	}
	 public static String optionToJs(Map hs){
        String key="";
        StringBuffer opinionsJs=new StringBuffer();
        opinionsJs.append("var opinions=[");
        Iterator it = hs.keySet().iterator();
        
        //添加这个变量主要是用来判断是否加，
        boolean isFirst = true;
        String szTemp=null;
        while(it.hasNext()){
        	key= (String)it.next();
        	if("senderOpinionList".equals(key)||"senderOpinionAttStr".equals(key))
        		continue;
        	
        	if(isFirst) isFirst = false;
        	else opinionsJs.append(",");
        	
        	szTemp=hs.get(key).toString();
        	//对于文档名过长的过滤
        	szTemp=subLargerGuanlanWendang(szTemp);
        	//
        	opinionsJs.append("[\"").append(key).append("\",\"").append(Strings.escapeJavascript(szTemp)).append("\"]");
        }
        opinionsJs.append("];");
        opinionsJs.append("\r\n");
        
        String sendOpinionStr="";
        Object sendOpinionObj=hs.get("senderOpinionList");
        if(sendOpinionObj!=null){
        	sendOpinionStr=sendOpinionObj.toString();
        }
        if("[]".equals(sendOpinionStr)) sendOpinionStr="";
        opinionsJs.append("var sendOpinionStr=\""+sendOpinionStr+"\";");
        return opinionsJs.toString();    	
	}

	//对于关联文档名过长的过滤。
	private static String subLargerGuanlanWendang(String str)
	{
		int begin=str.indexOf("style='font-size:12px'>");
		int end=str.indexOf("</a>");
		if(begin!=-1||end!=-1)
		{
			String wname=str.substring(begin, end).replace("style='font-size:12px'>", "");
	    	StringBuffer sb=new StringBuffer();
	    	if(wname.length()<30)
	    	{
	    		return str;
	    	}
	    	else
	    	{
	    		sb.append(str.substring(0, begin));
	    		sb.append("style='font-size:12px'>");
	    		sb.append(wname.substring(0,30));
	    		sb.append("......");
	    		sb.append(str.substring(end));
	    		return sb.toString();
	    	}
		}
		else
		{
			return str;
		}
		
	}
}
