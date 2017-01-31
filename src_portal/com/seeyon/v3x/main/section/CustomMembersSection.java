package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * @author Macx <br>
 * 团队空间成员栏目
 */
public class CustomMembersSection extends BaseSection {

    private static final Log log = LogFactory.getLog(CustomMembersSection.class);
    private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
    private SpaceManager spaceManager;
    
    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
    public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}
    
    public String getIcon() {
        return null;
    }

    public String getId() {
        return "customMembersSection";
    }
    
    @Override
	public String getBaseName() {
		return "customMembersSection";
	}

	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customMembersSection", preference);
	}
    
    protected String getTotalUnit(Map<String, String> preference) {
    	return ResourceBundleUtil.getString("com.seeyon.v3x.main.section.resources.i18n.SectionResources", "section.TotalUnit.ren");
    }

    protected Integer getTotal(Map<String, String> preference) {
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(Strings.isNotBlank(ownerId)){
            long spaceId = Long.parseLong(ownerId);
            try {
        		List<V3xOrgMember> members = spaceManager.getSpaceMemberBySecurity(spaceId, -1);
        		List<Long> memberList = CommonTools.getEntityIds(members);
                return memberList.size();
            }catch (BusinessException e) {
            	log.error("获取空间人员总数异常:", e);
            }
        }
        return null;
    }

    @Override
    protected BaseSectionTemplete projection(Map<String, String> preference) {
        int width = Integer.parseInt(preference.get(PropertyName.width.name()));
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        int row = 8;
        int column = 4;
        Integer newLineStr = newLine2Column.get(width);
		if(newLineStr != null){
			column = newLineStr.intValue();
		}
        User user = CurrentUser.get();
        Long uid = user.getId();
        ChessboardTemplete  ct = new ChessboardTemplete();
        ChessboardTemplete.Item item = null;
        ct.setLayout(row, column);
        ct.setHasNewMail(MenuFunction.hasNewMail());
        ct.setHasNewColl(MenuFunction.hasNewCollaboration());
        int allCount = column * row;
        long spaceId = -1;
        if(Strings.isNotBlank(ownerId)){
           spaceId = Long.parseLong(ownerId);
        }
       
        List<V3xOrgMember> spaceManagersList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
        try {
    		spaceManagersList = spaceManager.getSpaceMemberBySecurity(spaceId, 1);
    		memberList = spaceManager.getSpaceMemberBySecurity(spaceId, -1);
        }catch (BusinessException e) {
            log.error("获取空间人员异常", e);
        }
        if(!spaceManagersList.isEmpty()){
            allCount = allCount - spaceManagersList.size();
            memberList.removeAll(spaceManagersList);
        }
        if(!memberList.isEmpty() && memberList.size() > allCount){
            memberList = memberList.subList(0, allCount);
        }
        for(V3xOrgMember m : spaceManagersList){
            String link = "javascript:void(null)";
            if(!m.getId().equals(uid)){
                link = "/relateMember.do?method=relateMemberInfo&memberId="+ m.getId() +"&relatedId="+ uid +"&spaceId=" + spaceId;
            }
            item = ct.addItem();
            item.setIcon("/apps_res/v3xmain/images/section/leader.gif");
            item.setLink(link);
            item.setName(m.getName());
            String sTitle = Functions.showMemberAlt(m);
            item.setTitle(sTitle);
            if(!m.getId().equals(uid)){
            	item.setShowOption("1");
                item.setOptionId(m.getId());
                item.setOptionEmail(m.getEmailAddress());
            }else{
            	item.setShowOption("2");
            }
            item.setMaxLength(9);
        }
        for(V3xOrgMember m : memberList){
            String link = "javascript:void(null)";
            if(!m.getId().equals(uid)){
                link = "/relateMember.do?method=relateMemberInfo&memberId="+ m.getId() +"&relatedId="+ uid +"&spaceId=" + spaceId;
            }
            item = ct.addItem();
            item.setIcon("/apps_res/v3xmain/images/section/relatemember.gif");
            item.setLink(link);
            item.setName(m.getName());
            String sTitle = Functions.showMemberAlt(m);
            item.setTitle(sTitle);
            if(!m.getId().equals(uid)){
            	item.setShowOption("1");
                item.setOptionId(m.getId());
                item.setOptionEmail(m.getEmailAddress());
            }else{
            	item.setShowOption("2");
            }
            item.setMaxLength(9);
        }
        ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=departmentMore&spaceId=" + spaceId);
        return ct;
    }


}
