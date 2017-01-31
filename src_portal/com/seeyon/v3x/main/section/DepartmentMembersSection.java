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
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 部门成员栏目
 * Mazc 2009-5-19
 */
public class DepartmentMembersSection extends BaseSection
{
    private static final Log log = LogFactory.getLog(DepartmentMembersSection.class);
    private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
    private OrgManager orgManager;
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
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
        return "departmentMembersSection";
    }
    
    @Override
	public String getBaseName() {
		return "departmentMembersSection";
	}

    protected String getName(Map<String, String> preference) {
		String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
		if (Strings.isNotBlank(ownerId)) {
			long departmentId = -1;
			try {
				departmentId = Long.parseLong(ownerId);
				V3xOrgDepartment dept = orgManager.getDepartmentById(departmentId);
				if(dept!=null){
					return dept.getName();
				}
			}
			catch (Exception e) {
				log.error("获取部门[id=" + departmentId + "]名称时出现异常 ：", e);
			}
		}
		
		return "departmentMembersSection";
    }
    
    protected String getTotalUnit(Map<String, String> preference) {
    	return ResourceBundleUtil.getString("com.seeyon.v3x.main.section.resources.i18n.SectionResources", "section.TotalUnit.ren");
    }

    protected Integer getTotal(Map<String, String> preference) {
        long departmentId = CurrentUser.get().getDepartmentId();
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(Strings.isNotBlank(ownerId)){
            departmentId = Long.parseLong(ownerId);
            try {
                List<V3xOrgMember> memberList = orgManager.getMembersByDepartment(departmentId, false);
                if(memberList != null && !memberList.isEmpty()){
                    return memberList.size();
                }
            }
            catch (BusinessException e) {
                log.error(e);
            }
        }
        return null;
    }

    @Override
    protected BaseSectionTemplete projection(Map<String, String> preference) {
        int width = Integer.parseInt(preference.get(PropertyName.width.name()));
        //不管是单位管理员设置的还是 个人设置的，统一用个人设置的单
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
        
        long departmentId = user.getDepartmentId();
        if(Strings.isNotBlank(ownerId)){
           departmentId = Long.parseLong(ownerId);
        }
       
        List<V3xOrgMember> depManagersList = MainHelper.getDepManagerName(departmentId, orgManager);
        List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
        try {
            memberList = orgManager.getMembersByDepartment(departmentId, false);
        }
        catch (BusinessException e) {
            log.error(e);
        }
        if(!depManagersList.isEmpty()){
            allCount = allCount - depManagersList.size();
            memberList.removeAll(depManagersList);
        }
        if(!memberList.isEmpty() && memberList.size() > allCount){
            memberList = memberList.subList(0, allCount);
        }
        for(V3xOrgMember m : depManagersList){
            String link = "javascript:void(null)";
            if(!m.getId().equals(uid)){
                link = "/relateMember.do?method=relateMemberInfo&memberId="+ m.getId() +"&relatedId="+ uid +"&departmentId=" + departmentId;
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
                link = "/relateMember.do?method=relateMemberInfo&memberId="+ m.getId() +"&relatedId="+ uid +"&departmentId=" + departmentId;
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
        boolean isDeptAdmin = false;
        try {
        	isDeptAdmin = orgManager.isDepAdminRole(uid, departmentId);
		} catch (Exception e) {
			log.error("", e);
		}
		if(isDeptAdmin){
			ct.addBottomButton("menu_organization_top_member", "/organization.do?method=organizationFrame&from=Member&deptAdmin=1");
			ct.addBottomButton("menu_organization_top_team", "/organization.do?method=organizationFrame&from=TeamDept&deptAdmin=1");
		}
		
        ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=departmentMore&departmentId=" + departmentId);
        
        return ct;
    }

}
