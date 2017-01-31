package com.seeyon.v3x.plugin.dee.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.util.StringUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.dee.common.db.redo.model.*;
import com.seeyon.v3x.plugin.dee.manager.DeeSynchronLogManager;
import com.seeyon.v3x.plugin.dee.util.Request2BeanUtil;
import com.seeyon.v3x.util.Strings;

/** 
 * @author 作者: zhanggong 
 * @version 创建时间：2012-5-22 上午02:50:48 
 * 类说明 
 */
@CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
public class DeeSynchronLogController extends BaseController {
	private static final String baseName = "com.seeyon.v3x.plugin.dee.resources.i18n.DeeResources";
	/**
	 * 日志
	 */
	private static final Log log = LogFactory.getLog(DeeSynchronLogController.class);
	
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();
	
	private DeeSynchronLogManager deeSynchronLogManager;
	

	public DeeSynchronLogManager getDeeSynchronLogManager() {
		return deeSynchronLogManager;
	}

	public void setDeeSynchronLogManager(DeeSynchronLogManager deeSynchronLogManager) {
		this.deeSynchronLogManager = deeSynchronLogManager;
	}

	/**
	 * 跳转日志显示Frame
	 * @return
	 * @throws Exception
	 */
	public ModelAndView synchronLogFrame(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//跳转到上下布局页面
		return new ModelAndView("plugin/dee/exceptionLog/synchronLogFrame");
	}
	
	/**
	 *Dee控制台-同步历史new
	 */
	public ModelAndView deeSynchronLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView view = null;
		String syncId = request.getParameter("syncId");
		String flowName = request.getParameter("flowName");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		String textfield1 = request.getParameter("textfield1");
		//String retMsg = (String)request.getParameter("retMsg");
		//request.removeAttribute("retMsg");
		if(StringUtils.isNotBlank(syncId)){
			view = new ModelAndView("plugin/dee/exceptionLog/synchronLogDetail");
			String[] redoStates = {RedoBean.STATE_FLAG_SUCESS, RedoBean.STATE_FLAG_FAILE, RedoBean.STATE_FLAG_SKIP};
			List<RedoBean> redoList = deeSynchronLogManager.findRedoList(syncId, redoStates, condition, textfield, textfield1);
			view.addObject("resultRedoBean",pagenate(redoList));
			view.addObject("flowName", flowName);
		}else{
			view = new ModelAndView("plugin/dee/exceptionLog/synchronLog");
			List<SyncBean> resultFlowBean = deeSynchronLogManager.findSynchronLog("",condition, textfield, textfield1);
			view.addObject("resultFlowBean", pagenate(resultFlowBean));
		}
		//view.addObject("retMsg", retMsg);
		return view;
	}
	/**
	 * 查看同步异常详情 new
	 */
	public ModelAndView showDeeExceptionDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/exceptionLog/synchronLogDetail");
		String syncId = request.getParameter("syncId");
		String flowName = request.getParameter("flowName");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		String retMsg = (String)request.getAttribute("retMsg");
		String[] redoStates = {RedoBean.STATE_FLAG_FAILE, RedoBean.STATE_FLAG_SKIP}; 
		List<RedoBean> resultList = null;
		try{
			resultList = deeSynchronLogManager.findRedoList(syncId, redoStates, condition, textfield, null);
		}catch(Exception e){
			log.error("查询DEE重发信息出错：" + e.getLocalizedMessage(), e);
		}
		view.addObject("resultList", pagenate(resultList));
		view.addObject("flowName", flowName);
		view.addObject("syncId", syncId);
		view.addObject("condition", condition);
		view.addObject("textfield", textfield);
		view.addObject("retMsg", retMsg);
		return view;
	}
	/**
	 *处理异常数据(重新同步&&忽略异常) new 
	 */
	public ModelAndView deeRedoOrIgnore(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] redoIds = request.getParameterValues("id");
		String syncId = request.getParameter("syncId");
		String flowName = request.getParameter("flowName");
		String optionType = request.getParameter("optionType");
		String retMsg = "";
		if(optionType !=null){
			if("ignore".equals(optionType)){
				//忽略重新发起
				configService.skipRedo(redoIds);
				retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.ignoreTask.label");
			}else{
				//重新发起失败任务(多态)
				Map<String,String> hm = configService.redo(redoIds);
				retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.redoSuccess.label");
				if(hm != null){
					for(String redoId: redoIds){
						if(StringUtils.isNotBlank(hm.get(redoId))){
							retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.redoFailed.label");
							break;
						}
					}
				}
			}
		}
		request.setAttribute("retMsg", retMsg);
		return showDeeExceptionDetail(request,response);
	}
	/**
	 * 功能：删除sync记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView synchronLogDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resource_ids[] = request.getParameterValues("id");// 获取数据是以逗号隔开的字符串
		String retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.delSuccess.label");
		try {
			deeSynchronLogManager.delSyncBySyncId(Strings.join(",", resource_ids));
		} catch (TransformException e) {
			log.error("删除数据源出错：" + e.getLocalizedMessage(), e);
			retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.delFailed.label") + e.getLocalizedMessage();
		}
		//request.setAttribute("retMsg", retMsg);
		PrintWriter out = response.getWriter();
		out.print("<script>alert('"+ retMsg +"');</script>");
		out.flush();
		return super.redirectModelAndView("/DeeSynchronLogController.do?method=deeSynchronLog");
		//return deeSynchronLog(request,response);
		//return super.refreshWorkspace();
	}
	/**
	 * 功能：删除redo记录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exceptionDetailDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String syncId = request.getParameter("syncId");
		String flowName = request.getParameter("flowName");
		String redoId[] = request.getParameterValues("id");// 获取数据是以逗号隔开的字符串
		String retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.delSuccess.label");
		try {
			deeSynchronLogManager.delSyncByRedoId(syncId,Strings.join(",", redoId));
		} catch (TransformException e) {
			log.error("删除出错：" + e.getLocalizedMessage(), e);
			retMsg = ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.delFailed.label") + e.getLocalizedMessage();
		}
		PrintWriter out = response.getWriter();
		out.print("<script>alert('"+ retMsg +"');</script>");
		out.flush();
		return super.redirectModelAndView("/DeeSynchronLogController.do?method=showDeeExceptionDetail&syncId="+syncId+"&flowName="+flowName);
		//return showDeeExceptionDetail(request,response);
	}
	/**
	 * 功能：获得redo的详细参数信息，跳转到弹出窗口
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exceptionDetaiOpeanWin(HttpServletRequest request, HttpServletResponse response){
		ModelAndView view = new ModelAndView("plugin/dee/exceptionLog/openRedo");
		RedoBean bean = new RedoBean();
		Request2BeanUtil.parseRequest(request, bean);
		bean  = deeSynchronLogManager.findRedoById(bean.getRedo_id());
		view.addObject("bean",bean);
		SyncBean syBean = deeSynchronLogManager.findSynchronLog(bean.getSync_id());
		if(syBean != null)
			view.addObject("flowRealName",syBean.getFlow_dis_name());
		return view;
	}
	/**
	 * 功能：修改重发任务里的参数
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exceptionDetaiOpeanUpdate(HttpServletRequest request, HttpServletResponse response){
		ModelAndView view = new ModelAndView("plugin/dee/exceptionLog/openRedo");
		try {
			RedoBean bean = deeSynchronLogManager.findRedoById(request.getParameter("redo_id"));
			bean.setDoc_code((String)request.getParameter("doc_code"));
			deeSynchronLogManager.updateRedoBean(bean);
			view.addObject("bean", deeSynchronLogManager.findRedoById(bean.getRedo_id()));
			SyncBean syBean = deeSynchronLogManager.findSynchronLog(bean.getSync_id());
			if(syBean != null)
				view.addObject("flowRealName",syBean.getFlow_dis_name());
			view.addObject("retMsg",ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.saveSuccess.label"));
		} catch (Exception e) {
			view.addObject("retMsg",ResourceBundleUtil.getString(this.baseName,"dee.synchronLog.saveFailed.label"));
			log.error("请检查数据是否存在"+e.getMessage(), e);
		}
		return view;
	}
	/**
	 *Dee控制台-同步历史
	 */
//	public ModelAndView synchronLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView view = null;
//		String syncId = request.getParameter("syncId");
//		String flowName = request.getParameter("flowName");
//		String condition = request.getParameter("condition");
//		String textfield = request.getParameter("textfield");
//		String textfield1 = request.getParameter("textfield1"); 
//		if(StringUtils.isNotBlank(syncId)){
//			view = new ModelAndView("plugin/ncBusiness/synchronLogDetail");
//			String[] redoStates = {RedoBean.STATE_FLAG_SUCESS, RedoBean.STATE_FLAG_FAILE, RedoBean.STATE_FLAG_SKIP};
//			List<RedoBean> redoList = deeSynchronLogManager.findRedoList(syncId, redoStates, condition, textfield, textfield1);
//			view.addObject("resultRedoBean",pagenate(redoList));
//			view.addObject("flowName", flowName);
//		}else{
//			view = new ModelAndView("plugin/ncBusiness/synchronLogs");
//			List<SyncBean> resultFlowBean = deeSynchronLogManager.findSynchronLog("",condition, textfield, textfield1);
//			view.addObject("resultFlowBean", pagenate(resultFlowBean));
//		}
//		return view;
//	}
	
	/**
	 * Dee控制台-同步异常
	 */
//	public ModelAndView showExceptionLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView view = new ModelAndView("plugin/ncBusiness/ncSynExceptionLog");
//		String condition = request.getParameter("condition");
//		String textfield = request.getParameter("textfield");
//		String textfield1 = request.getParameter("textfield1");
//		List<SyncBean> resultList = new ArrayList<SyncBean>();
//		List<SyncBean> syncBeans = deeSynchronLogManager.findSynchronLog("", condition, textfield, textfield1);
//		for(SyncBean syncBean : syncBeans){
//			//不成功的同步状态，包含：部分和全部失败记录
//			if(syncBean.getSync_state() != 1){
//				resultList.add(syncBean);
//			}
//		}
//		view.addObject("resultList", pagenate(resultList));
//		view.addObject("condition", condition);
//		view.addObject("textfield", textfield);
//		return view;
//	}
	
	/**
	 * 忽略异常全部重新同步
	 */
//	public ModelAndView redoByFlowid(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		String flowIds = request.getParameter("flow_id");
//		String[] _flowIds = flowIds.split(",");
//		for(String flowId: _flowIds){
//			DEEClient deeClient = new DEEClient();
//			deeClient.execute(flowId);
//		}
//		return super.redirectModelAndView("/NCBusinessController.do?method=showExceptionLog");
//	}
	
	/**
	 * 查看同步异常详情
	 */
//	public ModelAndView showExceptionDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		ModelAndView view = new ModelAndView("plugin/ncBusiness/ncSynExceptionLogDetail");
//		String syncId = request.getParameter("syncId");
//		String flowName = request.getParameter("flowName");
//		String condition = request.getParameter("condition");
//		String textfield = request.getParameter("textfield");
//		String[] redoStates = {RedoBean.STATE_FLAG_FAILE, RedoBean.STATE_FLAG_SKIP}; 
//		List<RedoBean> resultList = deeSynchronLogManager.findRedoList(syncId, redoStates, condition, textfield, null);
//		view.addObject("resultList", pagenate(resultList));
//		view.addObject("flowName", flowName);
//		view.addObject("syncId", syncId);
//		view.addObject("condition", condition);
//		view.addObject("textfield", textfield);
//		return view;
//	}
	
	/**
	 *处理异常数据(重新同步&&忽略异常)
	 */
	public ModelAndView redoOrIgnore(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] redoIds = request.getParameterValues("id");
		String syncId = request.getParameter("syncId");
		String flowName = request.getParameter("flowName");
		String optionType = request.getParameter("optionType");
		if(optionType !=null){
			if("ignore".equals(optionType)){
				//忽略重新发起
				configService.skipRedo(redoIds);
			}else{
				//重新发起失败任务(多态)
				Map<String,String> hm = configService.redo(redoIds);
			}
		}
		return super.redirectModelAndView("/NCBusinessController.do?method=showExceptionDetail&syncId="+syncId+"&flowName="+flowName);
	}
	
	/**
	 * 检测异常任务可否重新执行
	 */
	public boolean checkSynchonException(String[] syncIds){
		boolean result = true;
		for(String syncId : syncIds){
			String[] redoStates = {RedoBean.STATE_FLAG_FAILE};
			//获取同批次重发列表中失败的记录
			List<RedoBean> redoBeansFaile = deeSynchronLogManager.findRedoList(syncId, redoStates, null, null,null);
			if(CollectionUtils.isNotEmpty(redoBeansFaile)){
				result = false;
		        break;
			}
		}
		return result;
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
	
	
	
	/**
	 *Dee控制台-同步历史
	 */
//	public ModelAndView synchronLog_old(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView view = null;
//		String syncId = request.getParameter("syncId");
//		String flowName = request.getParameter("flowName");
//		String condition = request.getParameter("condition");
//		String textfield = request.getParameter("textfield");
//		String textfield1 = request.getParameter("textfield1"); 
//		if(StringUtils.isNotBlank(syncId)){
//			view = new ModelAndView("plugin/ncBusiness/ncBusinessLogDetail");
//			String[] redoStates = {RedoBean.STATE_FLAG_SUCESS, RedoBean.STATE_FLAG_FAILE, RedoBean.STATE_FLAG_SKIP};
//			List<RedoBean> redoList = deeSynchronLogManager.findRedoList(syncId, redoStates, condition, textfield, textfield1);
//			view.addObject("resultRedoBean",pagenate(redoList));
//			view.addObject("flowName", flowName);
//		}else{
//			view = new ModelAndView("plugin/ncBusiness/ncBusinessLog");
//			List<SyncBean> resultFlowBean = deeSynchronLogManager.findSynchronLog("",condition, textfield, textfield1);
//			view.addObject("resultFlowBean", pagenate(resultFlowBean));
//		}
//		return view;
//	}
}
