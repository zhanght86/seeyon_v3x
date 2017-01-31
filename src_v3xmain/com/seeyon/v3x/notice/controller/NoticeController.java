package com.seeyon.v3x.notice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.notice.domain.Notice;
import com.seeyon.v3x.notice.manager.NoticeManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;

/**
 * 栏目公示板内容
 */
public class NoticeController extends BaseController {

	private static final Log log = LogFactory.getLog(NoticeController.class);

	private NoticeManager noticeManager;

	private SpaceManager spaceManager;
	
	private PortletEntityPropertyManager portletEntityPropertyManager;

	private OrgManager orgManager;

	private UserMessageManager userMessageManager;

	/**
	 * 保存公示板内容
	 * @param sendMessage 是否发送消息
	 * @param content 内容
	 * @param spaceType 空间类型
	 * @param spaceIdS 空间ID
	 * @param fragmentIdS 频道ID
	 * @param ordinal 频道中栏目的位置
	 * @param boardIdS 栏目singleBoardId
	 * @return
	 * @throws Exception
	 */
	public boolean save(String sendMessage, String content, String spaceType, String spaceIdS, String fragmentIdS, String ordinal, String boardIdS) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		Long spaceId = NumberUtils.toLong(spaceIdS);
		String spaceName = "";
		Long fragmentId = NumberUtils.toLong(fragmentIdS);
		String fragmentName = "";
		Long boardId = NumberUtils.toLong(boardIdS);
		try {
			Map<String, String> preference = portletEntityPropertyManager.getPropertys(fragmentId, ordinal);
			fragmentName = SectionUtils.getSectionName(ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "notice.label"), preference);
		} catch (Exception e) {
			log.error("", e);
		}
		
		try {
			Date date = new Date();
			Notice notice = noticeManager.getByBoardId(boardId);
			if (notice != null) {
				notice.setParamValue(content);
				notice.setUpdateDate(date);
				noticeManager.update(notice);
			} else {
				notice = new Notice(content, boardId, date, date);
				notice.setIdIfNew();
				notice.setParamName("0");
				noticeManager.save(notice);
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}

		if ("true".equals(sendMessage)) {
			// 给空间人员发送消息
			// 自定义团队空间、自定义单位空间、自定义集团空间：取空间管理员和空间授权人员
			// 部门空间：取部门成员和取空间管理员和空间授权人员
			// 单位空间：如果没有授权，取全单位人员，包括内部人员、外部人员、兼职人员；如果有授权，取空间管理员和空间授权人员
			// 集团空间：如果没有授权，取全集团人员；如果有授权，取空间管理员和空间授权人员
			try {
				List<Long> msgReceiverIds = null;
				if (Constants.SpaceType.custom.name().equals(spaceType)
						|| Constants.SpaceType.public_custom.name().equals(spaceType)
						|| Constants.SpaceType.public_custom_group.name().equals(spaceType)) {
					SpaceFix spaceFix = spaceManager.getSpace(spaceId);
					spaceName = spaceFix.getSpaceName();
					List<V3xOrgMember> securityMembers = spaceManager.getSpaceMemberBySecurity(spaceId, -1);
					msgReceiverIds = CommonTools.getEntityIds(securityMembers);
				} else if (Constants.SpaceType.department.name().equals(spaceType)) {
					V3xOrgDepartment dept = Functions.getDepartment(spaceId);
					spaceName = dept != null ? dept.getName() : "";
					List<Object[]> securityMembers = spaceManager.getSecuityOfDepartment(spaceId);
					msgReceiverIds = CommonTools.getMemberIdsByTypeAndId(CommonTools.getTypeAndIdStrs(securityMembers), orgManager);
				} else if (Constants.SpaceType.corporation.name().equals(spaceType)) {
					SpaceFix spaceFix = spaceManager.getSpaceFix(Constants.SpaceType.corporation, spaceId, null);
					spaceName = spaceFix.getSpaceName();
					List<V3xOrgMember> securityMembers = spaceManager.getSpaceMemberBySecurity(spaceFix.getId(), 0);
					if (CollectionUtils.isNotEmpty(securityMembers)) {
						securityMembers = spaceManager.getSpaceMemberBySecurity(spaceFix.getId(), -1);
						msgReceiverIds = CommonTools.getEntityIds(securityMembers);
					} else {
						List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
						// 内部人员
						List<V3xOrgMember> intMembers = orgManager.getAllMembers(spaceId);
						if (CollectionUtils.isNotEmpty(intMembers)) {
							members.addAll(intMembers);
						}
						
						// 外部人员
						List<V3xOrgMember> extMembers = orgManager.getAllExtMembers(spaceId);
						if (CollectionUtils.isNotEmpty(extMembers)) {
							members.addAll(extMembers);
						}
						
						// 兼职人员
						Map<Long, List<V3xOrgMember>> conCurrent = orgManager.getConcurentPostByAccount(spaceId);
						for (List<V3xOrgMember> list : conCurrent.values()) {
							members.addAll(list);
						}
						
						msgReceiverIds = CommonTools.getEntityIds(members);
					}
				} else if (Constants.SpaceType.group.name().equals(spaceType)) {
					SpaceFix spaceFix = spaceManager.getSpaceFix(Constants.SpaceType.group, V3xOrgEntity.NULL_ACCOUNT_ID, null);
					spaceName = spaceFix.getSpaceName();
					List<V3xOrgMember> securityMembers = spaceManager.getSpaceMemberBySecurity(spaceFix.getId(), 0);
					if (CollectionUtils.isNotEmpty(securityMembers)) {
						securityMembers = spaceManager.getSpaceMemberBySecurity(spaceFix.getId(), -1);
						msgReceiverIds = CommonTools.getEntityIds(securityMembers);
					} else {
						List<V3xOrgMember> members = orgManager.getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
						msgReceiverIds = CommonTools.getEntityIds(members);
					}
				}
				
				userMessageManager.sendSystemMessage(MessageContent.get("notice.send", spaceName, fragmentName, content.trim()),
						ApplicationCategoryEnum.guestbook, userId, MessageReceiver.get(boardId, msgReceiverIds));
			} catch (Exception e) {
				log.error("", e);
			}
		}

		return true;
	}

	public void setNoticeManager(NoticeManager noticeManager) {
		this.noticeManager = noticeManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

}