package com.seeyon.v3x.office.common;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomApp;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.myapply.domain.TApplylist;

public class OfficeHelper {

	private static Log log = LogFactory.getLog(OfficeHelper.class);

	private static AffairManager affairManager = null;

	public static AffairManager getAffairManager() {
		if (affairManager == null) {
			affairManager = (AffairManager) ApplicationContextHolder.getBean("affairManager");
		}
		return affairManager;
	}

	/**
	 * 综合办公-增加审批人首页待办 
	 * 
	 * @param objectName 申请的物品名称
	 * @param object
	 * @param subapp 车辆(0)、办公用品(1)、办公设备(2)、图书资料(3)、会议室(4)
	 */
	public static void addPendingAffair(String objectName, Object object, ApplicationSubCategoryEnum subapp) {
		try {
			Affair affair = new Affair();

			String label = "office.apply.label.1";
			Long memberId = null;
			Long senderId = null;
			Long objectId = null;
			if (object instanceof TApplylist) {// 办公设备，图书资料
				TApplylist tApplylist = (TApplylist) object;
				if ("4".equals(String.valueOf(tApplylist.getApplyType()))) {
					label = "office.apply.label.2";
				}
				memberId = tApplylist.getApplyMge();
				senderId = tApplylist.getApplyUsername();
				objectId = tApplylist.getApplyId();
			} else if (object instanceof OfficeApply) {// 办公用品，车辆
				OfficeApply officeApply = (OfficeApply) object;
				if ("3".equals(officeApply.getApplyType())) {
					label = "office.apply.label.3";
				}
				memberId = officeApply.getApplyManager();
				senderId = officeApply.getApplyUserName();
				objectId = officeApply.getApplyId();
			} else if (object instanceof MeetingRoomApp) {// 会议室
				MeetingRoomApp meetingRoomApp = (MeetingRoomApp) object;
				memberId = meetingRoomApp.getMeetingRoom().getV3xOrgMember().getId();
				senderId = meetingRoomApp.getV3xOrgMember().getId();
				objectId = meetingRoomApp.getId();
			}

			String subject = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", label, objectName);
			affair.setSubject(subject);
			affair.setMemberId(memberId);
			affair.setSenderId(senderId);
			affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			affair.setObjectId(objectId);

			affair.setIdIfNew();
			affair.setIsTrack(false);
			affair.setIsDelete(false);
			affair.setState(StateEnum.col_pending.key());
			affair.setApp(ApplicationCategoryEnum.office.key());
			affair.setSubApp(subapp);
			affair.serialExtProperties();
			getAffairManager().addAffair(affair);
		} catch (Exception e) {
			log.error("综合办公-增加审批人首页待办：", e);
		}
	}

	
	/**
	 * 综合办公-删除审批人首页待办
	 * 
	 * @param app
	 * @param objectId
	 */
	public static void delPendingAffair(ApplicationCategoryEnum app, Long objectId) {
		try {
			getAffairManager().deleteByObject(ApplicationCategoryEnum.office, objectId);
		} catch (Exception e) {
			log.error("综合办公-删除审批人首页待办：", e);
		}
	}

}