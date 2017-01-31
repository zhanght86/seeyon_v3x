/**
 * GuestbookSection.java
 * Created on 2009-5-18
 */
package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.guestbook.manager.GuestbookManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;

/**
 * 留言板栏目
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class GuestbookSection extends BaseSection
{
    private static final Log log = LogFactory.getLog(GuestbookSection.class);
    
    private GuestbookManager guestbookManager;
    
	private StaffInfoManager staffInfoManager;
    
    private OrgManager orgManager;
    
    public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public void setGuestbookManager(GuestbookManager guestbookManager) {
        this.guestbookManager = guestbookManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getId() {
        return "guestbookSection";
    }

    @Override
    protected String getName(Map<String, String> preference) {
        //追加组织名称
        String orgEntityName = "";
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        try{
            if(Strings.isNotBlank(ownerId)){
               V3xOrgDepartment entity = orgManager.getDepartmentById(Long.parseLong(ownerId));
               if(entity != null){
                   orgEntityName = entity.getName();
               }
               else{
                   V3xOrgAccount account = orgManager.getAccountById(Long.parseLong(ownerId));
                   if(account != null){
                       orgEntityName = account.getShortname();
                   }
               }
            }
        }
        catch(BusinessException e){
            log.error("留言板栏目加载标题异常:", e);
        }
        return Constant.getValueFromMainRes("guestbook.title", Strings.escapeJavascript(orgEntityName));
    }

    @Override
    protected Integer getTotal(Map<String, String> preference) {
        return null;
    }

    @Override
    protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete();        
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        User user = CurrentUser.get();
        Long orgEntityId;
        if(Strings.isNotBlank(ownerId)){
            orgEntityId = Long.parseLong(ownerId);
        }
        else{
            orgEntityId = user.getDepartmentId();
        }
        
        List<LeaveWord> leaveWordList = new ArrayList<LeaveWord>();
        try{
            leaveWordList = guestbookManager.getLeaveWords4Space(orgEntityId, 15);
        }
        catch(BusinessException e){
            log.error("留言板栏目加载异常:", e);
        }
        String falgStr = String.valueOf(Math.random());
        falgStr = falgStr.substring(2, falgStr.length());
        StringBuffer html = new StringBuffer();
        html.append("<div class=\"messageReplyDiv\"><input id=\"messageReplyDivHidden"+falgStr+"\" name='messageReplyDivHidden' type=\"hidden\" value=\""+orgEntityId+"\"/><div class=\"replyDivHidden\" id=\"replyDiv"+falgStr+"\"></div>");
        html.append("<div id='"+falgStr+"' class='leaveMessageContainer default'>");
        if(leaveWordList != null && !leaveWordList.isEmpty()){
        	for (int i = 0; i < leaveWordList.size(); i++) {
        		//getLeaveWordsById
        		LeaveWord leaveWord = leaveWordList.get(i);
        		leaveWord.setIdflag(falgStr);
        		if(leaveWord!=null){
        			leaveWord.setIndexShow(i);
	                String urlStr = SystemEnvironment.getA8ContextPath()+"/apps_res/v3xmain/images/personal/pic.gif";
	                try {
	                    StaffInfo staff = staffInfoManager.getStaffInfoById(leaveWord.getCreatorId());
	            		if(staff != null) {
	            			String issuerImage = staff.getSelf_image_name();
	            			if(StringUtils.isNotBlank(issuerImage)){
	            				if(issuerImage.startsWith("fileId")){
	            					urlStr = SystemEnvironment.getA8ContextPath()+"/fileUpload.do?method=showRTE&"+issuerImage+"&type=image";
	            				}else{
	            					urlStr =SystemEnvironment.getA8ContextPath()+"/apps_res/v3xmain/images/personal/"+issuerImage;
	            				}
	            			}
	             		}
	        		} catch (Exception e) {
	        			 log.error("留言板栏目加载异常:", e);
	        		}
	        		leaveWord.setUrlImage(urlStr);
//	        		if(leaveWord.getReplyId()!=null){
//	        			try {
//	        				LeaveWord leaveWordReply = guestbookManager.getLeaveWordsById(leaveWord.getReplyId());
//	        				if(leaveWordReply!=null){
//	        					leaveWord.setReplyerId(Long.valueOf(leaveWordReply.getCreatorId()));
//	        				}
//	        			} catch (Exception e) {
//		        			 log.error("回复留言加载异常:", e);
//		        		}
//	        		}
	        		html.append(MainHelper.leaveWord2HTML(leaveWord));
        		}
        	}	
        }
        html.append("</div>");
        html.append("</div>");
        html.append("<script>initDiv('"+falgStr+"')</script>");
        ht.setHtml(html.toString());
        ht.setModel(HtmlTemplete.ModelType.block);
        ht.addBottomButton("leaveword_issue_label", "javascript:showLeaveWordDiv(\'"+falgStr+"\','" + orgEntityId + "')");
        ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/guestbook.do?method=moreLeaveWordNew&departmentId=" + orgEntityId);
        
        return ht;
    }

}
