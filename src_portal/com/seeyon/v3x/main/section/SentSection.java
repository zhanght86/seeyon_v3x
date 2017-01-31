package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;
/**
 * 已发事项栏目
 * @author lilong
 * @date 2012-01-09
 */
public class SentSection extends BaseSection {

	private AffairManager affairManager;
	private OrgManager orgManager;
	private ConfigGrantManager configGrantManager;
	
	public void setConfigGrantManager(ConfigGrantManager configGrantManager) {
		this.configGrantManager = configGrantManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String getId() {
		return "sentSection";
	}

	@Override
	public String getBaseName() {
		return "sent";
	}
	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("sent", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
		// 【更多】
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE,"/main.do?method=moreSent" +
				"&fragmentId=" + preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()));
		User user = CurrentUser.get();
		Long memberId = user.getId();
		//不需要分页
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		//显示行数
		String count = preference.get("count");
		int coun = 8;
		if(Strings.isNotBlank(count)){
			coun = Integer.parseInt(count);
		}
		Pagination.setMaxResults(coun);
		//组装查询条件
		List<Affair> affairs = null;
		AffairCondition condition = new AffairCondition();
		condition.setMemberId(memberId);
		//流程来源
		String panel = SectionUtils.getPanel("all", preference);
		if("all".equals(panel)) {
			//全部
		} else {
			String tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return c;
			}
			if("track_catagory".equals(panel)){//分类
				condition.addSearch(SearchCondition.catagory, tempStr, null);
			}else if("importLevel".equals(panel)){//重要程度
				condition.addSearch(SearchCondition.importLevel, tempStr, null);
			}
		}
		//显示列
		String rowStr = preference.get("rowList");
		if (Strings.isBlank(rowStr)) {
			rowStr = "subject,publishDate,type";
		}
		String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for (String row : rows) {
			c.addRowName(row);
		}

//		String currentPanel = preference.get(PropertyName.panelId.name());
//		if(Strings.isBlank(currentPanel)){
//			currentPanel = panelValues[0];
//		}

		//查询
		affairs = condition.querySectionAffairSecretLevel(affairManager, StateEnum.col_sent.key());//成发集团项目
		if(affairs == null){
			return c;
		}
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		boolean infoAuditFlag = true;
		boolean hasInfoReportGrant = false;
		//将结果展现
		for (Affair affair : affairs) {
			String url = "";
			MultiRowThreeColumnTemplete.Row row = c.addRow();
			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();
			String subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
			int app = affair.getApp();
			row.setSubject(subject);
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
			String from = null;
			switch (StateEnum.valueOf(affair.getState())) {
			case col_sent: from = "Sent"; break;
			case col_pending: from = "Pending"; break;
			case col_done: from = "Done"; break;
			default: from = "Done";
			}

			switch (appEnum) {
			case collaboration:
				row.setLink("/collaboration.do?method=detail&from=Sent&affairId=" + affair.getId());
				row.setCategory(app, "/collaboration.do?method=collaborationFrame&from=" + from);
				break;
			case edocSend:
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=sendManager&edocType=0&toFrom=listSent");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 end
			case edocRec:
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=listSent");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 end
			case edocSign:
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=signReport&edocType=2&toFrom=listSent");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2993  唐桂林修改个人空间-已发事项公文链接 end
			case exSend:
			case exSign:
			case edocRegister:
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
				row.setSubject(subject);
				row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
				if (MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
					url = "/edocController.do?method=edocFrame&from=list" + from + "&edocType=" + EdocUtil.getEdocTypeByAppCategory(app);
				}
				row.setCategory(affair.getApp(), url);
				break;
			case info:
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
				row.setSubject(subject);
				row.setLink("/infoDetailController.do?method=detail&summaryId="+affair.getObjectId()+"&from=" + from + "&affairId=" + affair.getId() + "");
				row.setCreateDate(affair.getCompleteTime());
				if(infoAuditFlag) {
					hasInfoReportGrant = configGrantManager.hasConfigGrant(user.getLoginAccount(), user.getId(), "info_config_grant", "info_config_grant_report");
				}
				if(hasInfoReportGrant) {
					url = "/infoNavigationController.do?method=indexManager&entry=infoReport&toFrom=listInfoReported&affairId="+affair.getObjectId();
				}
				infoAuditFlag = false;
				row.setCategory(affair.getApp(), url);
				break;
			}
			
			row.setCreateDate(affair.getCreateDate());
			row.setBodyType(affair.getBodyType());
			row.setImportantLevel(affair.getImportantLevel());
			row.setHasAttachments(affair.isHasAttachments());
		}

		return c;
	}

}
