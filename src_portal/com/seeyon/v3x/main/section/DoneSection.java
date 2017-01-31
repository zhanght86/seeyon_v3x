package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.manager.impl.AffairCondition;
import com.seeyon.v3x.affair.manager.impl.AffairCondition.SearchCondition;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 已办事项栏目
 *
 * @author lilong
 * @date 2012-01-09
 */
public class DoneSection extends BaseSection {

	private static final Log log = LogFactory.getLog(DoneSection.class);
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
		return "doneSection";
	}

	@Override
	public String getBaseName() {
		return "done";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("done", preference);
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
		MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE,	"/main.do?method=moreDone" +
				"&fragmentId="+preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()));
		User user = CurrentUser.get();
		Long memberId = user.getId();

		Pagination.setNeedCount(false); // 不需要分页
		Pagination.setFirstResult(0);
		// 显示行数
		String count = preference.get("count");
		int coun = 8;
		if (Strings.isNotBlank(count)) {
			coun = Integer.parseInt(count);
		}
		Pagination.setMaxResults(coun);
		// 组装查询条件
		List<Affair> affairs = null;
		AffairCondition condition = new AffairCondition();
		condition.setMemberId(memberId);
		// 流程来源
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
		// 显示列
		String rowStr = preference.get("rowList");
		if (Strings.isBlank(rowStr)) {
			rowStr = "subject,receiveTime,sendUser,category";
		}
		String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for (String row : rows) {
			c.addRowName(row);
		}
		affairs = condition.querySectionAffairSecretLevel(affairManager, StateEnum.col_done.key());//成发集团项目
		if (affairs == null) {
			return c;
		}
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		boolean mtAppAuditFlag = true;
		boolean hasMtAppAuditGrant = false;
		boolean edocDistributeFlag = true;
		boolean hasEdocDistributeGrant = false;
		
		for (Affair affair : affairs) {
			String url = "";
			MultiRowFourColumnTemplete.Row row = c.addRow();

			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();

			String subject = ColHelper.mergeSubjectWithForwardMembers(
					affair.getSubject(), forwardMember, resentTime, orgManager,
					null);
			int app = affair.getApp();
			Long objectId = affair.getObjectId();

			row.setSubject(subject);
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum
					.valueOf(app);

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
			case meeting:
				//branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接 start
				if(isGov) {
					String typeURL = "";
					if(affair.getSubApp() == ApplicationSubCategoryEnum.meetingAudit.key()) {//会议审核
						row.setLink("/mtAppMeetingController.do?method=mydetail&id=" + affair.getObjectId()+"&affairId="+affair.getId());
						if(mtAppAuditFlag) {
							hasMtAppAuditGrant = configGrantManager.hasConfigGrant(user.getLoginAccount(), user.getId(), "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
						}
						if(hasMtAppAuditGrant) {
							typeURL = "/mtMeeting.do?method=entryManager&entry=meetingManager&listMethod=listAudit&listType=listAppAuditingMeetingAudited";
						}
						mtAppAuditFlag = false;
					}else if(affair.getSubApp() == ApplicationSubCategoryEnum.minutesAudit.key()){//branches_a8_v350sp1_r_gov 政务 向凡 添加 修复GOV-4943
						row.setLink("/mtSummary.do?method=mydetail&recordId=" + affair.getObjectId()+"&affairId="+affair.getId());
						typeURL = "/mtSummary.do?method=listHome&from=audit&listType=audited";
					}
					row.setCategory(app, affair.getSubApp(), typeURL);
					break;
				}
				//branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接 end
			case edocSend:
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=sendManager&edocType=0&toFrom=listDone");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 end
			case edocRec:
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=listDone");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 end
			case edocSign:
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 start
				if(isGov) {
					row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
					if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
						row.setCategory(app, "/edocController.do?method=entryManager&entry=signReport&edocType=2&toFrom=listDone");
					}
					break;
				}
				//branches_a8_v350_r_gov GOV-2991  唐桂林修改个人空间-已办事项公文链接 end
			case exSend:
			case exSign:
			case edocRegister:
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
				row.setSubject(subject);
				row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
				row.setCreateDate(affair.getCompleteTime());
				if (MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
					url = "/edocController.do?method=edocFrame&from=list" + from + "&edocType=" + EdocUtil.getEdocTypeByAppCategory(app);
				}
				row.setCategory(affair.getApp(), url);
				break;
			case edocRecDistribute:
				row.setLink("/edocController.do?method=detail&from=Sent&&affairId=" + affair.getId(), OPEN_TYPE.href);
				if(edocDistributeFlag) {
					try {
						hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), CurrentUser.get().getId(), EdocEnum.edocType.distributeEdoc.ordinal());
					} catch(Exception e) {
						hasEdocDistributeGrant = false;
					}
				}	
				if(hasEdocDistributeGrant) {
					url = "/edocController.do?method=entryManager&entry=recManager&edocType=1&toFrom=listDistribute&from=listSent";
				}
				edocDistributeFlag = false;
				row.setCategory(app, url);
				break;				
			case info:
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
				row.setSubject(subject);
				row.setLink("/infoDetailController.do?method=detail&summaryId="+affair.getObjectId()+"&from=" + from + "&affairId=" + affair.getId() + "");
				row.setCreateDate(affair.getCompleteTime());
				url = "/infoNavigationController.do?method=indexManager&entry=infoAuditing&toFrom=listInfoAuditDone&affairId="+affair.getObjectId();
				row.setCategory(affair.getApp(), url);
				break;
			}

				

			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(affair.getSenderId());
				if (member == null && affair.getSenderId() == -1) {
					member = new V3xOrgMember();
					member.setName(affair.getExtProps() == null ? "" : affair
							.getExtProps());
					member.setOrgAccountId(user.getLoginAccount());
				}
			} catch (BusinessException e) {
				log.error("", e);
			}
			row.setCreateMemberName(member == null ? "" : member.getName());
			row.setCreateMemberAlt(Functions.showMemberName(member));
			row.setCreateDate(affair.getCompleteTime());//处理时间
			row.setId(affair.getId());
			row.setObjectId(objectId);
			row.setBodyType(affair.getBodyType());
			row.setImportantLevel(affair.getImportantLevel());
			row.setHasAttachments(affair.isHasAttachments());
			row.setPolicyName(getPolicyName(affair));
		}

		return c;
	}

	private String getPolicyName(Affair affair) {
		String policy = affair.getNodePolicy();
		if (Strings.isNotBlank(policy)) {
			return BPMSeeyonPolicy.getShowName(policy);
		}
		return null;
	}
}
