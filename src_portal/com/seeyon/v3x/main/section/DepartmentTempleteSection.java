package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;
/**
 * 部门模板-授权给本部门的模板
 * @author Mazc
 * @version 1.0 2009-5-20
 */
public class DepartmentTempleteSection extends BaseSection {
	
    TempleteManager templeteManager;

	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();

	public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }

    public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "departmentTempleteSection";
	}
	
	@Override
	public String getBaseName() {
		return "departmentTempleteSection";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return "departmentTempleteSection";
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
		int newLine = 3;
		Integer newLineStr = newLine2Column.get(width);
		if(newLineStr != null){
			newLine = newLineStr.intValue();
		}
		
		int row = 8;
        
		ChessboardTemplete c = new ChessboardTemplete();
		c.setLayout(row, newLine);
        long departmentId = CurrentUser.get().getDepartmentId();
        String ownerId = preference.get(PropertyName.ownerId.name());
        if(Strings.isNotEmpty(ownerId)){
            departmentId = Long.parseLong(ownerId);
        }
        
		List<Templete> templeteList = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, 
		        TempleteCategory.TYPE.collaboration_templete.ordinal(),
		        TempleteCategory.TYPE.form.ordinal(),
		        TempleteCategory.TYPE.edoc.ordinal(),
		        TempleteCategory.TYPE.edoc_rec.ordinal(),
		        TempleteCategory.TYPE.edoc_send.ordinal(),
		        TempleteCategory.TYPE.sginReport.ordinal());
		
        if(templeteList != null && !templeteList.isEmpty()){
            if(templeteList.size() > row*newLine){
                templeteList = templeteList.subList(0, row*newLine);
            }
            boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
            for(Templete templete : templeteList){
                ChessboardTemplete.Item item = c.addItem();
                int type = templete.getCategoryType();
                String templeteIconMapping = templete.getType();
                if(templeteIconMapping.equals(Templete.Type.templete.name())){
                    templeteIconMapping = "text_wf";
                }
                item.setIcon("/apps_res/collaboration/images/" + templeteIconMapping + ".gif");
                
                //协同和表单模板
                if(type == TempleteCategory.TYPE.collaboration_templete.ordinal() || type == TempleteCategory.TYPE.form.ordinal()){                    
                    //item.setLink("javascript:callTemplete('/collaboration.do?method=newColl&templeteId=" + templeteId + "','" + templeteId + "')");
                    item.setLink("/collaboration.do?method=newColl&templeteId="+templete.getId());
                }//公文模板
                else if(type == TempleteCategory.TYPE.edoc.ordinal() || type == TempleteCategory.TYPE.edoc_rec.ordinal() || type == TempleteCategory.TYPE.edoc_send.ordinal() || type == TempleteCategory.TYPE.sginReport.ordinal()){
                    //item.setLink("javascript:callTemplete('/edocController.do?method=entryManager&entry=newEdoc&templeteId=" + templeteId + "','" + templeteId + "')");                    
                    long templeteId = templete.getId();
                    if(isGov) {
                		String url = "";
                		if(type == TempleteCategory.TYPE.edoc_rec.ordinal()) {//收文模板
    						url = "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=newEdoc&templeteId=" + templeteId;
                		} else if(type==TempleteCategory.TYPE.sginReport.ordinal()) {//签报模板
    						url = "/edocController.do?method=entryManager&entry=signReport&edocType=2&toFrom=newEdoc&templeteId=" + templeteId;
                		} else {//发文模板
    						url = "/edocController.do?method=entryManager&entry=sendManager&edocType=0&toFrom=newEdoc&templeteId=" + templeteId;
                		}
                		System.out.println(url);
                		item.setLink("javascript:callTemplete('"+url+"','" + templeteId + "')");
                	} else {
                		item.setLink("/edocController.do?method=entryManager&entry=newEdoc&templeteId=" + templete.getId());
                	}
                }
                String templeteSubject = templete.getSubject();
                item.setName(templeteSubject);
                item.setTitle(templeteSubject);
            }
        }

		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/templete.do?method=moreDepartmentTemplate&departmentId="+departmentId);
		return c;
	}

}