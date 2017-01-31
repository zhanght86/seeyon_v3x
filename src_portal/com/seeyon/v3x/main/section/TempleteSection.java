package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.seeyon.oainterface.impl.V3xManagerFactory;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
/**
 * 我的模板-栏目
 * @author Mazc
 * @version 1.0 2007-7-4
 */
public class TempleteSection extends BaseSection {

    TempleteManager templeteManager;

	private TempleteConfigManager templeteConfigManager;
	private ConfigGrantManager configGrantManager;
	private OrgManager orgManager;
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setConfigGrantManager(ConfigGrantManager configGrantManager) {
		this.configGrantManager = configGrantManager;
	}
	
	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();

	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}

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
		return "templeteSection";
	}
	
	@Override
	public String getBaseName() {
		return "templete";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("templete", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		ChessboardTemplete c = new ChessboardTemplete();
		String panel = SectionUtils.getPanel("all", preference);//来源
		c.addBottomButton("set_template", "/templete.do?method=showTemplateConfig");
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/templete.do?method=moreTemplate" +
				"&fragmentId=" + preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()) +
				"&currentPanel="+panel);
		String category = "-1,0,1,2,3,4,5";//全部种类
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		if("all".equals(panel)) {
			//全部
		} else {
			String tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return c;
			}
			category = "";
			String[] temList = tempStr.split(",");
			for(String s : temList) {
				if("catagory_personal_templete".equals(s)) {
					category += "-1,";//个人模板
				} else if("catagory_collOrFormTemplete".equals(s)){
					category += "4,0";//协同和表单模板
				} else if("catagory_edoc".equals(s)) {
					//branches_a8_v350_r_gov 唐桂林屏蔽首页收文模板  GOV-4072.公文收文管理中，分发环节调用模板的时候，正文不见了 start 
					if(isGov) {
						category += "1,2,3,5";//公文模板
					} else {
						category += "1,2,3,5";//公文模板
					}
					//branches_a8_v350_r_gov 唐桂林屏蔽首页收文模板  GOV-4072.公文收文管理中，分发环节调用模板的时候，正文不见了 end
				}
			}
		}

		int width = Integer.parseInt(preference.get(PropertyName.width.name()));

		int newLine = 3;
		Integer newLineStr = newLine2Column.get(width);
		if(newLineStr != null){
			newLine = newLineStr.intValue();
		}

		int count = SectionUtils.getSectionCount(16, preference);


		c.setLayout(8, newLine);
		long memberId = CurrentUser.get().getId();
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(memberId);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
        List<TempleteConfig> templeteList = templeteConfigManager.getConfigTempletesByCategory(memberId, count, category,member.getSecretLevel().toString());
        boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
        boolean isEdoc = Functions.isEnableEdoc();
        for(TempleteConfig templeteConfig : templeteList){
			int type = templeteConfig.getType();
			//没有公文模块不显示发文模板和收文模板
			if(!isPluginEdoc || !isEdoc){
				if(type == TempleteCategory.TYPE.edoc_send.ordinal()
						|| type == TempleteCategory.TYPE.edoc_rec.ordinal()
						|| type == TempleteCategory.TYPE.sginReport.ordinal()
						|| type == TempleteCategory.TYPE.edoc.ordinal()){
					continue;
				}
			}
			ChessboardTemplete.Item item = c.addItem();

            long templeteId = templeteConfig.getTempleteId();

            String templeteIconMapping = templeteConfig.getTempleteType();
            if(templeteIconMapping.equals(Templete.Type.templete.name())){
            	if(type == TempleteCategory.TYPE.edoc.ordinal() || type == TempleteCategory.TYPE.edoc_rec.ordinal() || type == TempleteCategory.TYPE.edoc_send.ordinal() || type == TempleteCategory.TYPE.sginReport.ordinal()){
            		templeteIconMapping = "edoc_temp";
            	}else{
            		if(templeteConfig.getTempleteType().equals("workflow")){
            			templeteIconMapping = "workflow";
            		}else if(templeteConfig.getTempleteType().equals("text")){
            			templeteIconMapping = "text";
            		}else{
            			templeteIconMapping = "text_wf";
            		}
            	}
            }

            item.setIcon("/apps_res/collaboration/images/" + templeteIconMapping + ".gif");
            //协同和表单模板
            if(type == -1 || type == TempleteCategory.TYPE.collaboration_templete.ordinal() || type == TempleteCategory.TYPE.form.ordinal()){
                //TODO 在这里不去校验模板是否存在
                item.setLink("javascript:callTemplete('/collaboration.do?method=newColl&templeteId=" + templeteId + "','" + templeteId + "')");
            }//公文模板
            else if(type == TempleteCategory.TYPE.edoc.ordinal() || type == TempleteCategory.TYPE.edoc_rec.ordinal() || type == TempleteCategory.TYPE.edoc_send.ordinal() || type == TempleteCategory.TYPE.sginReport.ordinal()){
                //TODO 在这里不去校验模板是否存在
            	//branches_a8_v350_r_gov GOV-3054 唐桂林修改政务模板链接 start
            	if(isGov) {
            		String url = "";
            		if(type == TempleteCategory.TYPE.edoc_rec.ordinal()) {//收文模板
						url = "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=newEdoc&templeteId=" + templeteId;
            		} else if(type==TempleteCategory.TYPE.sginReport.ordinal()) {//签报模板
						url = "/edocController.do?method=entryManager&entry=signReport&edocType=2&toFrom=newEdoc&templeteId=" + templeteId;
            		} else {//发文模板
						url = "/edocController.do?method=entryManager&entry=sendManager&edocType=0&toFrom=newEdoc&templeteId=" + templeteId;
            		}
            		item.setLink("javascript:callTemplete('"+url+"','" + templeteId + "')");
            	} else {
            		item.setLink("javascript:callTemplete('/edocController.do?method=entryManager&entry=newEdoc&templeteId=" + templeteId + "','" + templeteId + "')");
            	}
            	//branches_a8_v350_r_gov GOV-3054 唐桂林修改政务模板链接 end
            }
            String templeteSubject = templeteConfig.getSubject();
			// 非当前登录单位的模板，标示单位名称
			if (!templeteConfig.getAccountId().equals(CurrentUser.get().getLoginAccount())) {
				StringBuffer sb = new StringBuffer(Strings.escapeNULL(templeteSubject, ""));
				sb.append("(");
				sb.append(Functions.getAccountShortName(templeteConfig
						.getAccountId()));
				sb.append(")");
				templeteSubject = sb.toString();
			}
			item.setName(templeteSubject);
            item.setTitle(templeteSubject);
        }

		return c;
	}
}