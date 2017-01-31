package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 督办事项 栏目
 *
 * @author kuanghs
 * @version 1.0 2008-4-23
 */
public class SuperviseThingsEventSection extends BaseSection {

	private static final Log log = LogFactory.getLog(SuperviseThingsEventSection.class);

	private ColSuperviseManager colSuperviseManager;
	private EdocSummaryManager edocSummaryManager;
	private OrgManager orgManager;

	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getBaseName() {
		return "superviseThingsEvent";
	}

	@Override
	public String getId() {
		return "superviseThingsEventSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if(Strings.isNotBlank(name)){
			return name;
		}
		return "superviseThingsEvent";
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
		// 流程来源
		String panel = SectionUtils.getPanel("all", preference);
		List<Integer> entityType = new ArrayList<Integer>();
		//entityType.add(0);
        entityType.add(1);
        entityType.add(2);
//		List<Integer> importantList = null;
//		List<String> categorys = null;
		String tempStr = "";
		if("all".equals(panel)) {
			//全部，协同、公文、表单
		} else {
			tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return 0;
			}
//			if("track_catagory".equals(panel)){//按照分类
//				String[] tempList = tempStr.split(",");
//				categorys =(List<String>)Arrays.asList(tempList);
//			}else if("importLevel".equals(panel)){//按照重要程度
//				String[] tempList = tempStr.split(",");
//				importantList = new ArrayList<Integer>();
//				for(String s : tempList) {
//					importantList.add(new Integer(s));
//				}
//			}
		}
		//这个地方需要优化，不需要取一个LIST的对象，直接用SQL取行数就OK了。
		String portletQc = null;
		List<String> l = new ArrayList<String>();
		Map<String,List<String>> m = new HashMap<String,List<String>>();
		if("importLevel".equals(panel)) portletQc = "importantLevel";
		if("track_catagory".equals(panel)) portletQc = "category";
		if(portletQc != null){
			l = new ArrayList<String>();
			l.add(tempStr);
			m.put(portletQc, l);
		}
		Number n = (Number)colSuperviseManager.getSuperviseModelList(memberId, status, m, entityType, -1,true);
		return n.intValue();
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE,"/colSupervise.do?method=pendingMore" +
				"&fragmentId="+preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()));
		User user = CurrentUser.get();
		Long memberId = user.getId();
		int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
		// 流程来源
		String panel = SectionUtils.getPanel("all", preference);
		List<Integer> entityType = new ArrayList<Integer>();
		//entityType.add(0);
        entityType.add(1);
        entityType.add(2);
		List<Integer> importantList = null;
		List<String> categorys = null;
		String tempStr ="";
		if("all".equals(panel)) {
			//全部，协同、公文、表单
		} else {
			tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return c;
			}
//			if("track_catagory".equals(panel)){//按照分类
//				String[] tempList = tempStr.split(",");
//				categorys =(List<String>)Arrays.asList(tempList);
//			}else if("importLevel".equals(panel)){//按照重要程度
//				String[] tempList = tempStr.split(",");
//				importantList = new ArrayList<Integer>();
//				for(String s : tempList) {
//					importantList.add(new Integer(s));
//				}
//			}
		}

        String rowStr = preference.get("rowList");
        if(Strings.isBlank(rowStr)){
			rowStr = "subject,receiveTime,sendUser,category";
		}
        String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for(String row : rows){
			c.addRowName(row);
		}
		String count = preference.get("count");
		int coun = 8;
		if(Strings.isNotBlank(count)){
			coun = Integer.parseInt(count);
		}
		List<ColSuperviseModel> colSuperviseModelList = null;
		//这个地方需要优化，不需要取一个LIST的对象，直接用SQL取行数就OK了。
		String portletQc = null;
		List<String> l = new ArrayList<String>();
		Map<String,List<String>> m = new HashMap<String,List<String>>();
		if("importLevel".equals(panel)) portletQc = "importantLevel";
		if("track_catagory".equals(panel)) portletQc = "category";
		if(portletQc != null){
			l = new ArrayList<String>();
			l.add(tempStr);
			m.put(portletQc, l);
		}
		colSuperviseModelList = (List<ColSuperviseModel>)colSuperviseManager.getSuperviseModelList(memberId, status, m, entityType, coun,false);
		if (colSuperviseModelList != null) {
			Iterator iterator = colSuperviseModelList.iterator();
			while(iterator.hasNext()){
				ColSuperviseModel model  = (ColSuperviseModel)iterator.next();
				// 如果能取到model.getEntityType()
				if( model!=null && model.getEntityType()!=null ){
					String subject = ColHelper.mergeSubjectWithForwardMembers(model.getTitle(), model.getForwardMember(), model.getResendTime(), orgManager, null);

					// 以下是协同的处理情况
					if(model.getEntityType().equals(new Integer(1))
							|| ApplicationCategoryEnum.collaboration.key() == model.getAppType().intValue()){
						MultiRowFourColumnTemplete.Row row =    c.addRow();
						row.setSubject(subject);
						row.setLink("/colSupervise.do?method=detail&summaryId=" + model.getSummaryId());
						row.setCreateMemberName(Functions.showMemberName(model.getSender()));
						row.setCreateDate(model.getSendDate());
						row.setCategory(ApplicationCategoryEnum.collaboration.key(), "/colSupervise.do?method=mainEntry&status=0");
						row.setBodyType(model.getBodyType());
						row.setImportantLevel(model.getImportantLevel());
						row.setHasAttachments(model.getHasAttachment());
					// 公文
					}else if(model.getEntityType().equals(new Integer(2))
							|| EdocUtil.isEdocCheckByAppKey(model.getAppType())){
						int app = ApplicationCategoryEnum.edoc.key();
						EdocSummary summary = edocSummaryManager.findById(model.getSummaryId());
						if(null!=summary){
							if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
								app = ApplicationCategoryEnum.edocSend.key();
							}if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
								app = ApplicationCategoryEnum.edocRec.key();
							}if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN){
								app = ApplicationCategoryEnum.edocSign.key();
							}
						}
						MultiRowFourColumnTemplete.Row row =    c.addRow();
						row.setSubject(subject);
						//row.setLink("/colSupervise.do?method=detail&summaryId=" + model.getSummaryId());
						row.setLink("/edocSupervise.do?method=detail&superviseId=" + model.getId());
						row.setCreateMemberName(Functions.showMemberName(model.getSender()));
						row.setCreateDate(model.getSendDate());
						
						//branches_a8_v350_r_gov GOV-2995 杨帆 修改首页督办，类型跳转的页面  start
			            boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
			            if(isGovVersion){ //政务版跳转督办的路径
			            	row.setCategory(app, "/edocController.do?method=listEdocSuperviseController&edocType="+(summary!=null?summary.getEdocType():"0"));
			            }else{ //非政务版跳转督办的路径
			            	row.setCategory(app, "/edocSupervise.do?method=mainEntry");
			            }
			            //branches_a8_v350_r_gov GOV-2995 杨帆 修改首页督办，类型跳转的页面  end
			            
						row.setBodyType(model.getBodyType());
						row.setImportantLevel(model.getImportantLevel());
						row.setHasAttachments(model.getHasAttachment());
					// 模板
					}
				}
			}
		}

		return c;

	}

	/**
	 * @param edocSummaryManager the edocSummaryManager to set
	 */
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}


}
