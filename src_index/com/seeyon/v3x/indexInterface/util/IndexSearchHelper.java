package com.seeyon.v3x.indexInterface.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;

public class IndexSearchHelper {

	private static final Log log = LogFactory.getLog(IndexSearchHelper.class);
	
	public static String getAuthorKey(){

		Long id = CurrentUser.get().getId();
		String aclIds = com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser(id);
		StringBuffer ids = new StringBuffer(" (owner:'ALL')");
		for(String aclId : aclIds.split(",")){
			ids.append(" OR(owner:'" + aclId + "')");
		}		
		DocLibManager docLibManager = (DocLibManager)ApplicationContextHolder.getBean("docLibManager");
		List<Long> libs = docLibManager.getLibsByOwner(id);
		if(libs != null && libs.size()>0) {
			for(Long libId:libs) {
				ids.append("OR(owner:'"+com.seeyon.v3x.indexInterface.Constant.DOC_LIB+"|"+libId.toString()+"')");
			}
		}
		//------------关联项目－－－－－－－－－－－－－
		ProjectManager projectManager = (ProjectManager)ApplicationContextHolder.getBean("projectManager");
		List<ProjectSummary> projectList = null;
		try {
			projectList = projectManager.getAllProjectList(id);
			if(projectList!=null&&projectList.size()!=0){
				for(ProjectSummary project : projectList){
					ids = ids.append("OR(project:'"+project.getId()+"')");
				}
			}
		} catch (Exception e1) {
			log.error("",e1);
		}
		return ids.toString();
	}
	/**
	 * 将输入的*，(,),[,],{,} 转义+ - && || !  ^ " ~ ? : \
	 * @param key
	 * @return
	 */
	public static String replaceSearchKey(String key) {
		if(key != null)
		{
			key=key.replaceAll("\\\\","\\\\\\\\").replaceAll("\\(", "\\\\(").replaceAll("\\*", "\\\\*").replaceAll("\\)", "\\\\)").
			replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}").replaceAll("\\?", "\\\\?")
			.replaceAll("\\!", "\\\\!").replaceAll("\\^", "\\\\^").replaceAll("\\+", "\\\\+").replaceAll("\\:", "\\\\:").replaceAll("\\~", "\\\\~")
			.replaceAll("\\&&", "\\\\&&").replaceAll("\\-", "\\\\-").replaceAll("\\\"", "\\\\\"");
//				if(java.util.regex.Pattern.matches("[\\w-\\\\]+", key))
			if(java.util.regex.Pattern.matches("[^\u4e00-\u9fa5]+", key))
			{
				key+="*";
//					if(key.indexOf("-")==-1)
//					{
//						key=key.replaceAll("\\*", "\\\\*");
//					}
			}
			else if(java.util.regex.Pattern.matches("^\\W+[a-zA-Z_0-9-\\\\]+$", key))
			{
				Matcher m = Pattern.compile("^\\W+").matcher(key);
				 if(m.find())
				 {
					 String temp="";
					 temp=m.group()+" ";
					 key=temp+key.substring(m.group().length());
				 }
			}
			else if(java.util.regex.Pattern.matches("^[a-zA-Z_0-9-\\\\]+\\W+$", key))
			{
				Matcher m= Pattern.compile("^\\w+").matcher(key);
				 if(m.find())
				 {
					 String temp="";
					 temp=m.group()+" ";
					 key=temp+key.substring(m.group().length());
				 }
			}
		}
		return key;
	}
}
