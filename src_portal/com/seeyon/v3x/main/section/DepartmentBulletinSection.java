package com.seeyon.v3x.main.section;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class DepartmentBulletinSection extends BaseSection {
    private static Log log = LogFactory.getLog(DepartmentBulletinSection.class);
    private OrgManager orgManager;
    
    private BulDataManager bulDataManager;
    
    private BulTypeManager bulTypeManager;

    public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public OrgManager getOrgManager() {
        return orgManager;
    }
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    public BulDataManager getBulDataManager() {
        return bulDataManager;
    }

    public void setBulDataManager(BulDataManager bulDataManager) {
        this.bulDataManager = bulDataManager;
    }
    
    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getId() {
        return "departmentBulletinSection";
    }
    
    @Override
	public String getBaseName() {
		return "departmentBulletin";
	}

    @Override
    public String getName(Map<String, String> preference) {
        return "departmentBulletin";
    }

    @Override
    public Integer getTotal(Map<String, String> preference) {
        return null;
    }
    private final int[] width = {70,16,14}; //宽度百分比 
    
    @Override
    public BaseSectionTemplete projection(Map<String, String> preference) {
        Long departmentId = CurrentUser.get().getDepartmentId();
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(ownerId != null){
            departmentId = Long.parseLong(ownerId);
        }
        
        User user = CurrentUser.get();
        Long userId = user.getId();
        List<BulData> bulDatas = null;
        try {
            bulDatas = bulDataManager.deptFindByReadUserForIndex(departmentId, user);
        } catch(Exception e){
            log.error("部门空间-读取公告异常：", e);
        } 
        
        MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
        
        if(bulDatas != null && !bulDatas.isEmpty()) {        
	        int rand = new Random().nextInt();
	        
	        for (BulData bulData : bulDatas) {	        	
	            MultiRowVariableColumnTemplete.Row row = t.addRow(); 	            
	            MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();	            
	            String title = bulData.getTitle();
	            int maxLength = 28;
				if(bulData.getAttachmentsFlag()) {
					maxLength -= 2;
				}
				//部门公告区分置顶
				if(bulData.getTopOrder()==0) {
					subjectCell.setCellContent(title);
					subjectCell.setHasAttachments(bulData.getAttachmentsFlag());
				} else {
					ResourceBundle bundle = ResourceBundle.getBundle("com.seeyon.v3x.bulletin.resources.i18n.BulletinResources", CurrentUser.get().getLocale());
					String label = ResourceBundleUtil.getString(bundle, "label.top");
					String topTitle = "<font color=red>[" + label +  "]</font>" + Strings.getLimitLengthString(title, maxLength, "..")
									  + "<span class='attachment_" + bulData.getAttachmentsFlag() + "'></span>";
					subjectCell.setCellContentHTML(topTitle);
				}		
				subjectCell.setBodyType(StringUtils.isBlank(bulData.getExt5()) ? bulData.getDataFormat() : com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
	            subjectCell.setCellWidth(width[0]);
	            String url = "/bulData.do?method=userView&id="+bulData.getId()+"&random="+rand;
	            subjectCell.setLinkURL(url,OPEN_TYPE.href_blank);
	            
	            //设置已阅或未读样式	            
	            if(bulData.getReadFlag()!=null && bulData.getReadFlag().booleanValue())
					subjectCell.setClassName("AlreadyReadByCurrentUser");
	            else
	            	subjectCell.setClassName("ReadDifferFromNotRead");
	            
	            //姓名
	            MultiRowVariableColumnTemplete.Cell ownerCell = row.addCell();
	            ownerCell.setCellWidth(width[1]);
	            
	            V3xOrgMember member = null;
	            try {
	            	member = orgManager.getMemberById(bulData.getPublishUserId());
	            } catch (BusinessException e) {
	                log.error("获取人员出现异常", e);
	            }
	            if(member==null || member.getIsDeleted())
	            	ownerCell.setCellContentHTML("&nbsp;&nbsp;");
	            else
	            	ownerCell.setCellContent(member.getName());
	            
	            //创建时间
	            MultiRowVariableColumnTemplete.Cell createTimeCell = row.addCell();
	            createTimeCell.setCellWidth(width[2]);
	            
	            //按照首页标准时间格式显示，当天则显示时间如16：45，否则显示日期如05/19 modified by Meng Yang 2009-06-05
	            Date todayFirstTime = Datetimes.getTodayFirstTime();      
	            Date bulCreateDate = bulData.getCreateDate();
	            String time = null;
	            if(bulCreateDate.getTime() < todayFirstTime.getTime()) 
	            	time = Datetimes.format(bulCreateDate, "MM-dd");
	             else 
	            	time = Datetimes.format(bulCreateDate, "HH:mm");
	            createTimeCell.setCellContent(time);
	        }
        }
		try {
			 boolean isDeptBulManager = this.bulTypeManager.isManagerOfThisDept(userId, departmentId);
			 if(isDeptBulManager){
				 t.addBottomButton(newBul, "/bulData.do?method=create&spaceType=2&bulTypeId="+departmentId);
		     }
		} catch (BusinessException e) {
			log.error("", e);
		}
        t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bulData.do?method=bulMore&spaceType=2&typeId="+departmentId+"&from=top");
        return t;        
    }
    
    private String newBul = "new_bull_alt";

}