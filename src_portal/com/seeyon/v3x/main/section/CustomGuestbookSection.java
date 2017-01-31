package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.guestbook.manager.GuestbookManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;

/**
 * @author Macx
 *团队空间留言板栏目
 */
public class CustomGuestbookSection extends BaseSection {

    private static final Log log = LogFactory.getLog(CustomGuestbookSection.class);
    
    private GuestbookManager guestbookManager;
    
	private StaffInfoManager staffInfoManager;
    
    public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public void setGuestbookManager(GuestbookManager guestbookManager) {
        this.guestbookManager = guestbookManager;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getId() {
        return "customGuestbookSection";
    }
    
    @Override
	public String getBaseName() {
		return "customGuestbookSection";
	}

    @Override
    protected String getName(Map<String, String> preference) {
    	return SectionUtils.getSectionName("customGuestbookSection", preference);
    }

    @Override
    protected Integer getTotal(Map<String, String> preference) {
        return null;
    }

    @Override
    protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete();        
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        Long spaceId = null;
        if(Strings.isNotBlank(ownerId)){
        	spaceId = Long.parseLong(ownerId);
        }
        
        List<LeaveWord> leaveWordList = new ArrayList<LeaveWord>();
        try{
            leaveWordList = guestbookManager.getLeaveWords4Space(spaceId, 15);
        }
        catch(BusinessException e){
            log.error("留言板栏目加载异常:", e);
        }
        String falgStr = String.valueOf(Math.random());
        falgStr = falgStr.substring(2, falgStr.length());
        StringBuffer html = new StringBuffer();
        html.append("<div class=\"messageReplyDiv\"><input id=\"messageReplyDivHidden"+falgStr+"\" name='messageReplyDivHidden' type=\"hidden\" value=\""+spaceId+"\"/><div class=\"replyDivHidden\" id=\"replyDiv"+falgStr+"\"></div>");
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
	        		html.append(MainHelper.leaveWord2HTML(leaveWord));
        		}
        	}	
        }
        html.append("</div>");
        html.append("</div>");
        html.append("<input id=\"hiddenSpace1\" name='hiddenSpace1' type=\"hidden\" value=\"custom\"/>");
        html.append("<script>initDiv('"+falgStr+"')</script>");
        ht.setHtml(html.toString());
        ht.setModel(HtmlTemplete.ModelType.block);
        ht.addBottomButton("leaveword_issue_label", "javascript:showLeaveWordDiv(\'"+falgStr+"\','" + spaceId + "')");
        ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/guestbook.do?method=moreLeaveWordNew&departmentId=" + spaceId +"&custom=true");
        
        return ht;
    }


}
