package com.seeyon.v3x.system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.LockLoginInfoFactory;
import com.seeyon.v3x.common.authenticate.LockLoginInfoFactory.LockLoginInfo;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;

/**
 * 新增加控制层，完成对锁定账户进行管理
 * 
 * @author Administrator
 * 
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class LockedUserManagerController extends BaseController {

	
	private AppLogManager appLogManager;
	
	private SystemConfig systemConfig;
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * 增加外部分页
	 * @param <T>
	 * @param list
	 * @return
	 */
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>(0);
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
	 * 初始化锁定账户列表
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws Exception
	 */
	public ModelAndView initHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.listLockedUsers(request, response);
	}
	
	public ModelAndView listLockedUsers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/lockedUser/lockedUserManager");
		
		//取得查询条件的值
		String textfield = request.getParameter("textfield");
		LockLoginInfoFactory lif = LockLoginInfoFactory.getInstance();
		List<LockLoginInfo> lockedLoginInfoListAll = lif.getAll();
		List<LockLoginInfo> lockedLoginInfoList = new ArrayList<LockLoginInfo>();
		
		//在条件不为空的情况下
		result.addObject("textfield", textfield);
		
		if(lockedLoginInfoListAll!=null && lockedLoginInfoListAll.size()>0){
			boolean isPatteryName = textfield != null && !"".equals(textfield);
			
			int userLoginCountCfi = Integer.parseInt(systemConfig.get(IConfigPublicKey.USER_LOGIN_COUNT));
			long forbiddenLoginTimeCfi = Long.parseLong(systemConfig.get(IConfigPublicKey.FORBIDDEN_LOGIN_TIME));
			for(LockLoginInfo lli : lockedLoginInfoListAll){
				if(System.currentTimeMillis() - lli.getLockTime() > forbiddenLoginTimeCfi * 3600000){
					continue; //已经过了超时期了，可以不显示，因为可以再登录了
				}
				if(lli.getCount() < userLoginCountCfi){
					continue;// 不显示没有被锁定锁住的用户
				}
				if(isPatteryName && !lli.getLoginName().contains(textfield)){
					continue;
				}
				
				lockedLoginInfoList.add(lli);
			}
		}
		
		//参照组织模型添加查询后，再进行分页
		lockedLoginInfoList = this.pagenate(lockedLoginInfoList);
		
		result.addObject("lockedLoginInfoList", lockedLoginInfoList);	
		
		return result;	
	}
	

	/**
	 * 删除锁定账户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView destroy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//查询出系统用户
		User user = CurrentUser.get();
			
		String ids = RequestUtils.getStringParameter(request, "ids");
		String [] loginNames = ids.split(",");
		if(loginNames.length>0){	
			for(int i =0;i<loginNames.length;i++){
				String loginName = loginNames[i];
				LockLoginInfoFactory.getInstance().remove(loginName);
				//用户解锁时添加日志
				//appLogManager.insertLog(user, AppLogAction.Organization_NewTeam, user.getName(),team.getName());
				appLogManager.insertLog(user, AppLogAction.Systemmanager_RemoveLockUser,
						user.getName(), loginName);
			}
		}
		
		return redirectModelAndView("/lockedUserManager.do?method=initHome");
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

}
