package com.seeyon.v3x.office.auto.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;


/**
 * 车辆申请的相关操作控制类
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-09
 */
public class AutoApplyController extends BaseManageController
{

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private UserMessageManager userMessageManager;

    public void setAutoManager(AutoManager autoManager)
    {
        this.autoManager = autoManager;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
    }

    public ModelAndView frame(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/auto/autoApplyFrame");
        return mav;
    }

    /**
     * 查看车辆的申请一览记录
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView apply(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/auto/showApplyList");

        String autoId = request.getParameter("autoId");
        List applyList = this.autoManager.getApplyListByAutoId(autoId);
        mav.addObject("list", applyList);
        return mav;
    }

    /**
     * 车辆申请,进入车辆申请界面
     */
    protected void onCreate(HttpServletRequest request,
            HttpServletResponse arg1, ModelAndView modelView) throws Exception
    {

        // 取得参数
        String autoId = request.getParameter("autoId"); // 车牌号

        // 根据车牌号取得车辆详细信息对象
        AutoInfo autoInfo = autoManager.getAutoInfoById(autoId);
        if(autoInfo == null){
        	modelView
            .addObject(
                    "script",
                    "alert(parent.v3x.getMessage(\"officeLang.auto_lend_failed\"));\nparent.list.location.href=parent.list.tempUrl;");
        	
        	return ;
        }
        
        if (autoInfo.getAutoManager() != null)
        {
            V3xOrgMember manageMember = this.orgManager.getMemberById(autoInfo
                    .getAutoManager());

            String managerName = "";
            if (manageMember != null)
            {
                managerName = manageMember.getName();
            }
            modelView.addObject("managerName", managerName);
        }
        // 保存对象到视图模型中
        modelView.addObject("bean", autoInfo);

        String curTime = Datetimes.formatDatetimeWithoutSecond(new Date());
        modelView.addObject("curtime", curTime);
    }

    /**
     * 根据“查询条件”检索表“车辆详细信息表” 结果显示在车辆列表中(类型==1 AND 管理者==当前登录用户)
     */
    protected void onQuery(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {

        String fieldName = request.getParameter("condition"); // 查询字段名
        if (fieldName == null)
        {
            fieldName = "";
        }

        String fieldValue = request.getParameter("textfield"); // 查询字段值

        if (fieldValue == null)
        {
            fieldValue = "";
        }

        Map keyMap = new HashMap();

        // keyMap.put("auto_mge", autoManagerId);
        keyMap.put("autoStatus", new Integer(0));
        // keyMap.put("del_flag", new Integer(0));

        // 取得车辆详细信息列表
        List autoList = new ArrayList();
        User user = CurrentUser.get();
        // 设置检索条件/车辆申请的可申请的车辆列表
        autoList = autoManager.getAutoInfoApply(fieldName, fieldValue, keyMap,user.getLoginAccount());
        // 保存结果到视图模型中
        modelView.addObject("list", autoList);
/*        if (autoList.size() <= 0 && !"".equals(fieldName))
        {
            modelView.clear();
            modelView.setViewName(this.successView);

            modelView
                    .addObject(
                            "script",
                            "alert(parent.v3x.getMessage(\"officeLang.auto_info_apply_nothing_prompt\"));window.location.href = window.location.href;");
        }*/
    }
    /**
     * 单击/双击选择车辆的详细信息
     */
    protected void onShow(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {

        // 取得参数
        String autoId = request.getParameter("autoId"); // 车辆编号

        // 根据车辆编号从DB取得车辆详细信息
        AutoInfo autoInfo = autoManager.getAutoInfoById(autoId);
        if(autoInfo == null){
        	modelView
            .addObject(
                    "script",
                    "alert(parent.v3x.getMessage(\"officeLang.auto_lend_failed\"));\nparent.list.location.href=parent.list.tempUrl;");
        	return ;
        }
        
        if (autoInfo.getAutoManager() != null)
        {
            V3xOrgMember manageMember = this.orgManager.getMemberById(autoInfo.getAutoManager());

            String managerName = "";
            if (manageMember != null)
            {
                managerName = manageMember.getName();
            }
            modelView.addObject("managerName", managerName);
        }

        // 保存对象到视图模型中
        modelView.addObject("bean", autoInfo);

    }

	/**
	 * 保存车辆申请
	 */
	protected void onSave(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView, boolean arg3) throws Exception {
		String autoId = request.getParameter("autoId");
		String departTime = request.getParameter("departTime"); // 出车日期
		String backTime = request.getParameter("backTime"); // 归车日期
		String autoDep = request.getParameter("autoDep"); // 出车地
		String autoDes = request.getParameter("autoDes"); // 目的地
		String autoOrigin = request.getParameter("autoOrigin"); // 出车事由
		
		AutoInfo autoInfo = autoManager.getAutoInfoById(autoId);
		if (autoInfo == null) {
			modelView.addObject("script", "alert(parent.v3x.getMessage(\"officeLang.auto_lend_failed\"));\nparent.list.location.href=parent.list.tempUrl;");
			return;
		}

		Long applyManager = autoInfo.getAutoManager();
		User curUser = CurrentUser.get();
		Long applyUserName = new Long(curUser.getId());// 申请人ID
		Long applyDepId = new Long(curUser.getDepartmentId());// 申请人部门ID

		// 初始化申请单对象
		OfficeApply officeApply = new OfficeApply(); // 申请单对象
		officeApply.setApplyDate(new Date()); // 申请日期
		officeApply.setApplyDepId(applyDepId); // 部门ID
		officeApply.setApplyManager(applyManager); // 管理者
		officeApply.setApplyState(new Integer(1)); // 待审状态 1
		officeApply.setApplyType("1"); // 申请类型
		officeApply.setApplyUserName(applyUserName); // 申请人
		officeApply.setDeleteFlag(new Integer(0)); // 删除表示

		// 车辆管理申请单
		AutoApplyInfo applyInfo = new AutoApplyInfo();
		applyInfo.setAutoId(autoId);
		applyInfo.setDeleteFlag(new Integer(0));
		if (Strings.isNotBlank(backTime)) {
			applyInfo.setAutoBackTime(backTime.trim());
		}
		if (Strings.isNotBlank(departTime)) {
			applyInfo.setAutoDepartTime(departTime.trim());
		}
		applyInfo.setAutoDep(autoDep);
		applyInfo.setAutoDes(autoDes);
		applyInfo.setAutoOrigin(autoOrigin);
		applyInfo.setDeleteFlag(new Integer(0));
		this.autoManager.saveAutoApply(officeApply, applyInfo);
		
		// 审批人首页待办
		OfficeHelper.addPendingAffair(autoInfo.getAutoId(), officeApply, ApplicationSubCategoryEnum.office_auto);
		
		// 更新车辆使用状态
		// 当车辆被申请的时候，车辆详细信息中的删除表示被修改为 3 （以记录此车曾经被使用）
		// 当申请用车时车辆使用状态置为1（有问题，已经改正）
		if (autoInfo != null) {
			// autoInfo.setAutoState(new Integer(1));
			// autoInfo.setDeleteFlag(new Integer(3));
			autoInfo.setDeleteFlag(new Integer(0));
			this.autoManager.updateAutoInfo(autoInfo);
		}

		modelView.addObject("script", "alert(parent.v3x.getMessage(\"officeLang.auto_lend_succeed\"));\nparent.list.location.href=parent.list.tempUrl;");
		
		// 给管理员发送消息
		try {
			userMessageManager.sendSystemMessage(MessageContent.get("office.car.apply", autoInfo.getAutoId(), curUser.getName()), 
					ApplicationCategoryEnum.office, curUser.getId(), 
					MessageReceiver.get(officeApply.getApplyId(), applyManager, "message.link.office.auto", String.valueOf(officeApply.getApplyId())));
		} catch (MessageException e) {
			logger.error("车辆申请失败：", e);
		}
	}
    
}