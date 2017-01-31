package com.seeyon.v3x.plugin.dee.controller;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.context.EngineController;
import com.seeyon.v3x.dee.schedule.QuartzManager;
import com.seeyon.v3x.plugin.dee.manager.DeeDataSourceManager;
import com.seeyon.v3x.plugin.dee.manager.DeeScheduleManager;
import com.seeyon.v3x.plugin.dee.util.Request2BeanUtil;
import com.seeyon.v3x.services.flow.FlowService;;
@CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
public class DeeScheduleController extends BaseController  {
	private static final String baseName = "com.seeyon.v3x.plugin.dee.resources.i18n.DeeResources";
	/**
	 * 日志
	 */
	private static final Log log = LogFactory.getLog(DeeScheduleController.class);
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();
	private DeeScheduleManager deeScheduleManager;

	public DeeScheduleManager getDeeScheduleManager() {
		return deeScheduleManager;
	}
	public void setDeeScheduleManager(DeeScheduleManager deeScheduleManager) {
		this.deeScheduleManager = deeScheduleManager;
	}
	/**
	 * 功能：跳转到上下结构的frame页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView scheduleFrame(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//跳转到上下布局页面
		return new ModelAndView("plugin/dee/schedule/scheduleShowFrame");
	}
	/**
	 * 功能：获取数据跳转列表页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showScheduleList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/schedule/scheduleList");
		String condition = request.getParameter("condition");
		String byDis_name = request.getParameter("byDis_name");
		String byFlow_name = request.getParameter("byFlow_name");
		if(StringUtils.isNotBlank(byDis_name) || StringUtils.isNotBlank(byFlow_name)){
			view.addObject("deeSchedulelist",pagenate(deeScheduleManager.findScheduleList(condition,byDis_name,byFlow_name)));
		}else{
			view.addObject("deeSchedulelist",pagenate(deeScheduleManager.findScheduleList()));
		}
		
		return view;
	}
	/**
	 * 功能：获取数据跳转定时器详细信息页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showScheduleDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/schedule/scheduleDetail");
		//获取flowId
		String flowId = request.getParameter("id");
		ScheduleBean bean = configService.getScheduleByFlowId(flowId);

		view.addObject("bean", bean);
		view.addObject("retFixed", getISFixed(bean.getQuartz_code()));
		if(bean.getFlow_id()!=null){
			view.addObject("flow",configService.getFlow(bean.getFlow_id()));
		}
		return view;
	}
	/**
	 * 功能：获取数据跳转定时器修改页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showScheduleUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/schedule/scheduleUpdate");
		//获取flowId
		String flowId = request.getParameter("id");
		ScheduleBean bean = configService.getScheduleByFlowId(flowId);

		view.addObject("bean", bean);
		view.addObject("retFixed", getISFixed(bean.getQuartz_code()));
		if(bean.getFlow_id()!=null){
			view.addObject("flow",configService.getFlow(bean.getFlow_id()));
		}
		return view;
	}
	
	/**
	 * 功能：定时器信息更新
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public ModelAndView scheduleUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/schedule/scheduleUpdate");
		String retMsg = ResourceBundleUtil.getString(this.baseName,"dee.schedule.updateSucceed.label");
		ScheduleBean bean = new ScheduleBean();
		Request2BeanUtil.parseRequest(request, bean);
		try {
			bean.setEnable("1".equals(request.getParameter("isEnable")));
			//add by dkywolf 2012-03-15 增加描述信息的保存
			bean.setSchedule_desc(request.getParameter("resource_desc")==null?"":request.getParameter("resource_desc"));
			configService.updateSchedule(bean);
			view.addObject("bean", bean);
			view.addObject("retFixed", getISFixed(bean.getQuartz_code()));
			view.addObject("updateSuccess", "1");
			if(bean.getFlow_id()!=null){
				view.addObject("flow", configService.getFlow(bean.getFlow_id()));
			}
			//更新Dee配置文件，重新加载
			GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
			QuartzHolder.deleteQuartzJobByGroup(QuartzManager.JOB_GROUP_NAME);
			QuartzManager.getInstance().refresh();
		} catch (TransformException e) {
			log.error(e.getMessage(),e);
			retMsg = ResourceBundleUtil.getString(this.baseName,"dee.schedule.updateError.label") + e.getLocalizedMessage();
		} catch (Throwable e) {
			log.error("引擎刷新上下文异常"+e.getLocalizedMessage());
			retMsg = ResourceBundleUtil.getString(this.baseName,"dee.schedule.updateError.label") + e.getLocalizedMessage();
		}
		view.addObject("retMsg",retMsg);
		return view;
	}
	/**
	 * 分页工具方法
	 */
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
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
	private int getISFixed(String dateCode){
		if(dateCode != null && "1".equalsIgnoreCase(dateCode.substring(0,1)))
			return 1;
		else
			return 0;
	}
}

