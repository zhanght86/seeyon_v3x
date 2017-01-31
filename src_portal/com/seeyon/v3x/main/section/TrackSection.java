/**
 *
 */
package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

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
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;
/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-14
 */
public class TrackSection extends BaseSection {
	private static final Log log = LogFactory.getLog(TrackSection.class);
	private AffairManager affairManager;

	private OrgManager orgManager;

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String getId() {
		return "trackSection";
	}
	
	@Override
	public String getBaseName() {
		return "track";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("track", preference);
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		User user = CurrentUser.get();
		String panel = SectionUtils.getPanel("all", preference);
		AffairCondition condition = new AffairCondition();
		condition.setMemberId(user.getId());
		if("all".equals(panel)) {
			//全部
		} else {
			String tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				return 0;
			}
			if("track_catagory".equals(panel)){//分类
				condition.addSearch(SearchCondition.catagory, tempStr, null);
			}else if("importLevel".equals(panel)){//重要程度
				condition.addSearch(SearchCondition.importLevel, tempStr, null);
			}
		}

		return condition.getTrackCountSecretLevel(affairManager);//成发集团项目 程炯 获取根据密级筛选后的跟踪事项的数量
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
        String panel = SectionUtils.getPanel("all", preference);
        c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/main.do?method=moreTrack" +
				"&fragmentId=" + preference.get(PropertyName.entityId.name()) +
				"&ordinal="+preference.get(PropertyName.ordinal.name()) +
				"&currentPanel="+panel);
		User user = CurrentUser.get();

		Pagination.setNeedCount(false); //不需要分页
		Pagination.setFirstResult(0);
		String count = preference.get("count");
		int coun = 8;
		if(Strings.isNotBlank(count)){
			try{
			coun = Integer.parseInt(count);
			}catch(Exception e){}
		}
		Pagination.setMaxResults(coun);

		String rowStr = preference.get("rowList");
		if(Strings.isBlank(rowStr)){
			rowStr = "subject,receiveTime,sendUser,category";
		}
		String[] rows = rowStr.split(",");
		c.addRowName("subject");
		for(String row : rows){
			c.addRowName(row);
		}

		//查询条件组装
		AffairCondition condition = new AffairCondition();
		condition.setMemberId(user.getId());
//		String panel = SectionUtils.getPanel("all", preference);
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

		List<Affair> affairs = condition.queryTrackAffairSecretLevel(affairManager);//成发集团项目 程炯 获取根据密级筛选后的跟踪事项
		if(affairs == null){
			return c;
		}
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		for (Affair affair : affairs) {
			MultiRowFourColumnTemplete.Row row = c.addRow();

			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(affair.getSenderId());
				if(member==null&&affair.getSenderId()==-1)
				{
					member=new V3xOrgMember();
					member.setName(affair.getExtProps()==null?"":affair.getExtProps());
					member.setOrgAccountId(user.getLoginAccount());
				}
				affair.setSender(member);
			}
			catch (BusinessException e) {
				log.error("", e);
			}

			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();

			int app = affair.getApp();
			ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(app);
			String subject = null;

			String from = null;
			switch(StateEnum.valueOf(affair.getState())){
			case col_sent : from = "Sent"; break;
			case col_pending : from = "Pending"; break;
			case col_done : from = "Done"; break;
			default : from = "Done";
			}
			String toFrom = null;
			switch(StateEnum.valueOf(affair.getState())){
			case col_sent : toFrom = "listSent"; break;
			case col_pending : toFrom = "listPending"; break;
			case col_done : toFrom = "listDone"; break;
			default : toFrom = "listDone";
			}
			switch (appEnum) {
				case collaboration :
					subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);

					row.setSubject(subject);
					row.setLink("/collaboration.do?method=detail&from=" + from + "&affairId=" + affair.getId());

					row.setCategory(affair.getApp(), "/collaboration.do?method=collaborationFrame&from=" + from);
					break;
				case edocSend:
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 start
					if(isGov) {
						subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
						row.setSubject(subject);
						row.setCreateMemberName(affair.getSender().getName());
						row.setCreateDate(affair.getCreateDate());
						row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
							row.setCategory(app, "/edocController.do?method=entryManager&entry=sendManager&edocType=0&track=1&toFrom="+toFrom);
						}
						break;
					}					
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 end
				case edocRec:
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 start
					if(isGov) {
						subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
						row.setSubject(subject);
						row.setCreateMemberName(affair.getSender().getName());
						row.setCreateDate(affair.getCreateDate());
						row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
							row.setCategory(app, "/edocController.do?method=entryManager&entry=recManager&edocType=1&track=1&toFrom="+toFrom);
						}
						break;
					}
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 end
				case edocSign:
					subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
					row.setSubject(subject);
					row.setCreateMemberName(affair.getSender().getName());
					row.setCreateDate(affair.getCreateDate());

					String url = "";
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 start
					if(isGov) {
						row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
						url = "/edocController.do?method=entryManager&entry=signReport&edocType=2&track=1&toFrom="+toFrom;
					} else {
						row.setLink("/edocController.do?method=detail&from=" + from + "&affairId=" + affair.getId() + "");
						if(MenuFunction.hasMenu(EdocUtil.getMenuIdByApp(appEnum.getKey()))) {
							url = "/edocController.do?method=edocFrame&from=list" + from + "&edocType=" + EdocUtil.getEdocTypeByAppCategory(app);
						}
					}
					//branches_a8_v350_r_gov GOV-2989  唐桂林修改个人空间-跟踪事项公文链接 end
					row.setCategory(affair.getApp(), url);
					break;
				case info:
					subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), forwardMember, resentTime, orgManager, null);
					row.setSubject(subject);
					row.setLink("/infoDetailController.do?method=detail&summaryId="+affair.getObjectId()+"&affairId="+affair.getId()+"&from=Pending");
					row.setCreateMemberName(affair.getSender().getName());
					row.setCreateDate(affair.getCreateDate());
					String entry = "home&listMethod=listInfoReport&listType=listInfoReported&menuId=3101";
					row.setCategory(affair.getApp(), "/infoNavigationController.do?method=indexManager&entry="+entry);
					break;
			}
			row.setCreateMemberName(Functions.showMemberName(member));
			row.setCreateDate(affair.getCreateDate());
			row.setBodyType(affair.getBodyType());
			row.setImportantLevel(affair.getImportantLevel());
			row.setHasAttachments(affair.isHasAttachments());
            boolean isOverTime = affair.getIsOvertopTime();
            //超期事件突出显示
            row.setDistinct(isOverTime);
            if(isOverTime){
                row.addExtIcons("/common/images/overTime.gif");
            }
		}

		return c;
	}

}