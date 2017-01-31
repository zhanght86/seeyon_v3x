package com.seeyon.v3x.main;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-20
 */
public class MainHelper {
	private static final Log log = LogFactory.getLog(MainHelper.class);
	
	/**
	 * 显示名称
	 * 
	 * @param orgManager
	 * @return
	 */
	public static String getAccountShowName(OrgManager orgManager, V3xOrgAccount root) {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();

		String accountName = null;
		boolean isShowGroupShortName = (Boolean)(SysFlag.frontPage_showGroupShortName.getFlag());
		try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			if(account != null){ //当前登录者没有单位
			    accountName = account.getName();			    
			    if (isShowGroupShortName && !account.getIsRoot() && orgManager.isAccountInGroupTree(accountId)) { //当前单位不是集团
			        if(root != null){ //有根单位
                        String groupShortName = root.getShortname(); 
                        if(Strings.isBlank(groupShortName)){
                            groupShortName = "";
                        }
			            accountName = groupShortName + "&nbsp;" + accountName;
			        }
			    }
			}
			else{
			    if(root != null){ //有根单位
			        return root.getName();
			    }
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return accountName;
	}

    // 判断当前用户是否为单位管理员
    public static boolean isAccountAdmin(OrgManager orgManager) {
    	boolean isAccountAdmin = false;
    	User user = CurrentUser.get();
    	try {
    		Long roleId = null;
    		roleId = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTADMIN).getId();
    		isAccountAdmin = orgManager.isInDomain(roleId, user.getId());
    	}
    	catch (BusinessException e) {
    		log.error("", e);
    	}
    	return isAccountAdmin;
    }
    
    //判断某用户是否是某部门的主管
    public static boolean isDepartmentManager(Long userId, Long departmentId, OrgManager orgManager){
        List<V3xOrgMember> deptManagers = null;
        try{
            V3xOrgRole role = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
            if(role != null){                
                deptManagers = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, departmentId, role.getId());
            }
        }
        catch (BusinessException e){
            log.error("",e);
        }
        if(deptManagers != null && !deptManagers.isEmpty()){
	        for(V3xOrgMember member : deptManagers){
	            if(member.getId().longValue() == userId.longValue()){
	                return true;
	            }
	        }
        }
        return false;
    }
    
    //获取部门主管的名字
    public static List<V3xOrgMember> getDepManagerName(Long departmentId, OrgManager orgManager){
        List<V3xOrgMember> deptManagers = null;
        try
        {
        	V3xOrgRole role = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
            if(role != null){                
                deptManagers = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, departmentId, role.getId());
            }
        }
        catch (BusinessException e)
        {
            log.error("",e);
        }

        
        return deptManagers;
    }
    
    //判断当前用户是否是HR管理员
    public static boolean isHRAdmin(OrgManager orgManager)
    {
        boolean isHRAdmin = false;
        User user = CurrentUser.get();
        Long roleId = null;
        try
        {
            V3xOrgRole hrRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_HRADMIN);
            if(hrRole != null){                
                roleId = hrRole.getId();
                isHRAdmin = orgManager.isInDomain(roleId, user.getId());
            }
        }
        catch (BusinessException e)
        {
            log.error("", e);
        }
       return isHRAdmin;
    }
    
    //  判断当前用户是否是表单管理员
    public static boolean isFORMAdmin(OrgManager orgManager)
    {
        boolean isFORMAdmin = false;
        User user = CurrentUser.get();
        Long roleId = null;
        try
        {
            V3xOrgRole formRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_FORMADMIN);
            if(formRole != null){                
                roleId = formRole.getId();
                isFORMAdmin = orgManager.isInDomain(roleId, user.getId());
            }
        }
        catch (BusinessException e)
        {
            log.error("", e);
        }
       return isFORMAdmin;
    }
    
    /**
     * 判断当前用户所登录的单位 是否是集团下的单位
     * 非集团下的单位不能访问集团空间、集团文档等
     * @param orgManager
     * @param accountId
     * @return
     * @throws BusinessException
     */
    public static boolean isAccountInGroup(OrgManager orgManager, long accountId) throws BusinessException{
        return orgManager.isAccountInGroupTree(accountId);
    }
    
    /**
     * 将留言对象转换为HTML，输出到首页留言栏目
     * @param leaveWord
     * @return
     */
    public static String leaveWord2HTML(LeaveWord leaveWord){
        StringBuffer sb = new StringBuffer();
        if(leaveWord.getIndexShow()==1){
        	sb.append("<div class='messageDivFirst'>");
        }else{
        	if(leaveWord.getIndexShow()<5){
        		sb.append("<div class='messageDiv'>");
        	}else{
        		sb.append("<div class='messageDivHidden'>");
        	}
        }
        
        sb.append("<table cellpadding='0' cellspacing='0' width='100%' style='table-layout:fixed'><tr>");
		sb.append("<td class='phtoImgTD'><div class='phtoImg'><img src=\""+leaveWord.getUrlImage()+"\" width='40' height='40'/></div></td>");
        sb.append("<td>");
        
        sb.append("<div class='messageContent'>");
        sb.append("<span class='peopleName'>");
        String name = Functions.showMemberName(leaveWord.getCreatorId());
        sb.append(name);
        if(leaveWord.getReplyerId()!=null && (long)leaveWord.getReplyerId()!=leaveWord.getCreatorId()){
        	String replyName = Functions.showMemberName(leaveWord.getReplyerId());
            sb.append("<span class='replySay'>"+ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.reply", null)+":");
            sb.append("</span>");
            sb.append(replyName);
            
        }
        sb.append("</span>");
        sb.append("<span class='peopleSay'>"+ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.speak", null)+":");
        sb.append("</span>");
        sb.append("<span class='peopleMessage'>");
        String con = leaveWord.getContent();
        sb.append(leaveWord.getContent());
        sb.append("</span>");
        sb.append("</div>");
        
        sb.append(" <div class='messageTime'>");
        if(leaveWord.getReplyId()!=null){
        	sb.append("<span class='reply'><a href=\"javascript:replyMessage('"+leaveWord.getReplyId()+"','"+leaveWord.getDepartmentId()+"','"+leaveWord.getCreatorId()+"','"+leaveWord.getIdflag()+"')\">"+ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.reply", null)+"</a></span>");
        }else{
        	sb.append("<span class='reply'><a href=\"javascript:replyMessage('"+leaveWord.getId()+"','"+leaveWord.getDepartmentId()+"','"+leaveWord.getCreatorId()+"','"+leaveWord.getIdflag()+"')\">"+ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.reply", null)+"</a></span>");
        }
        String dateTime = Datetimes.format(leaveWord.getCreateTime(), "MM-dd HH:mm");
        sb.append("<span class='meaageTime'>"+dateTime+"</span>");
        sb.append("</div>");
       
        
        
        
        sb.append("</td>");
        sb.append("</tr></table>");
        sb.append("</div>");
        
        
        
//        sb.append("<div><img src='/seeyon/common/images/icon.gif' width='10' height='10'>&nbsp;");
//        sb.append("  <span style='color: #1039b2'>" + name + " ( " + dateTime + " )</span>");
//        sb.append("</div>");
//        sb.append("<div style='padding: 2px 0px 4px 16px'> " + Functions.toHTML(leaveWord.getContent()) + "</div>");
        return sb.toString();
    }
    
}