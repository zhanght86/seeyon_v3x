package com.seeyon.v3x.plugin.dee.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.resource.util.DeeResourceEnum;
import com.seeyon.v3x.dee.common.db.resource.util.SourceType;
import com.seeyon.v3x.dee.common.db.resource.util.SourceUtil;
import com.seeyon.v3x.dee.common.db2cfg.GenerationCfgUtil;
import com.seeyon.v3x.dee.context.EngineController;
import com.seeyon.v3x.dee.schedule.QuartzManager;




import com.seeyon.v3x.plugin.dee.manager.DeeDataSourceManager;
import com.seeyon.v3x.plugin.dee.model.JDBCResourceBean;
import com.seeyon.v3x.plugin.dee.model.JNDIResourceBean;
import com.seeyon.v3x.plugin.dee.model.ConvertDeeResourceBean;
import com.seeyon.v3x.plugin.dee.util.Request2BeanUtil;
/** 
 * @author 作者: XQ
 * @version 创建时间：2012-7-12
 * 类说明 
 */
@CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
public class DeeDataSourceController extends BaseController {
	
	private static final String baseName = "com.seeyon.v3x.plugin.dee.resources.i18n.DeeResources";
	/**
	 * 日志
	 */
	private static final Log log = LogFactory.getLog(DeeDataSourceController.class);
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();

	private DeeDataSourceManager deeDataSourceManager;
	public DeeDataSourceManager getDeeDataSourceManager() {
		return deeDataSourceManager;
	}

	public void setDeeDataSourceManager(DeeDataSourceManager deeDataSourceManager) {
		this.deeDataSourceManager = deeDataSourceManager;
	}
	/**
	 * 功能：跳转到上下结构的frame页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dataSourceFrame(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//跳转到上下布局页面
		return new ModelAndView("plugin/dee/dataSource/dataSourceShowFrame");
	}
	
	/**
	 * 功能：获取数据跳转列表页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDataSourceList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/dataSource/dataSourceList");
		String condition = request.getParameter("condition");
		String byDis_name = request.getParameter("byDis_name");
		if(StringUtils.isNotBlank(byDis_name)){
			List<DeeResourceBean> deeResourcelist = deeDataSourceManager.findDataSourceList(condition,byDis_name);
			view.addObject("deeResourcelist",pagenate(deeResourcelist));
		}else{
			List<DeeResourceBean> deeResourcelist = deeDataSourceManager.findDataSourceList();
			view.addObject("deeResourcelist",pagenate(deeResourcelist));
		}
		return view;
	}
	/**
	 * 功能：获取数据跳转数据源详细信息页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDataSourceDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/dataSource/dataSourceDetail");
		//获取要修改的数据源ID
		String id = request.getParameter("id");
		//获得数据源对象
		DeeResourceBean deeResource = deeDataSourceManager.findById(id);
		//获取类型信息
		List<SourceType> sourchType =  SourceUtil.getDataSourceType();
		//根据数据源对象获取JNDI信息
		DeeResource rb = null;
		String preStr = "jdbc";
		if (Integer.parseInt(deeResource.getResource_template_id()) == DeeResourceEnum.JDBCDATASOURCE.ordinal()) {
			rb = new JDBCResourceBean();
		} else {
			rb = new JNDIResourceBean();
			preStr = "jndi";
		}
		rb = new ConvertDeeResourceBean(deeResource).getResource();
		//获取的对象放入request
		view.addObject(preStr + "subbean",rb);
		view.addObject("deeResource",deeResource);
		view.addObject("sourchType",sourchType);
		return view;
	}
	/**
	 * 功能：获取数据跳转数据源修改页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDataSourceUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/dataSource/dataSourceUpdate");
		//获取要修改的数据源ID
		String id = request.getParameter("id");
		//获得数据源对象
		DeeResourceBean deeResource = deeDataSourceManager.findById(id);
		//获取类型信息
		List<SourceType> sourchType =  SourceUtil.getDataSourceType();
		//根据数据源对象获取JNDI信息
		DeeResource rb = null;
		String preStr = "jdbc";
		if (Integer.parseInt(deeResource.getResource_template_id()) == DeeResourceEnum.JDBCDATASOURCE.ordinal()) {
			rb = new JDBCResourceBean();
		} else {
			rb = new JNDIResourceBean();
			preStr = "jndi";
		}
		rb = new ConvertDeeResourceBean(deeResource).getResource();
		//获取的对象放入request
		view.addObject(preStr + "subbean",rb);
		view.addObject("deeResource",deeResource);
		view.addObject("sourchType",sourchType);
		view.addObject("retMsg","");
		view.addObject("successFlag","");
		return view;
	}
	/**
	 * 功能：数据源信息更新
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public ModelAndView dataSourceUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView view = new ModelAndView("plugin/dee/dataSource/dataSourceUpdate");
		DeeResourceBean bean = new DeeResourceBean();
		Request2BeanUtil.parseRequest(request, bean);
		String resource_id = request.getParameter("resource_id");
		
		this.setSubBean(request,bean);
		try {
			deeDataSourceManager.update(bean);
			//更新Dee配置文件，重新加载
			GenerationCfgUtil.getInstance().generationMainFile(GenerationCfgUtil.getDEEHome());
			EngineController.getInstance(null).refreshContext();
			view.addObject("sourchType", SourceUtil.getDataSourceType());
			view.addObject("deeResource", bean);
			view.addObject("retMsg", ResourceBundleUtil.getString(this.baseName,"dee.dataSource.succeed.label"));
		} catch (TransformException e) {
			String retMsg = ResourceBundleUtil.getString(this.baseName,"dee.dataSource.error.label") + e.getLocalizedMessage();
			log.error(retMsg, e);
			view.addObject("retMsg", retMsg);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			log.error("DEE数据源刷新出错：", e);
		}
		view.addObject("successFlag","");
		return view;
	}
	
	/** 连接测试 */
	public ModelAndView testcon(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("plugin/dee/dataSource/dataSourceUpdate");
		Boolean temp = false;
		if(testConService(request)){
			request.setAttribute("successFlag", true);
		}else{
			request.setAttribute("successFlag", false);
		}
		DeeResourceBean bean = new DeeResourceBean();
		Request2BeanUtil.parseRequest(request, bean);
		String resource_id = request.getParameter("resource_id");
		this.setSubBean(request,bean);
		view.addObject("sourchType", SourceUtil.getDataSourceType());
		view.addObject("deeResource", bean);
		view.addObject("retMsg","");
		return view;
	}

	private boolean testConService(HttpServletRequest request) {
		int type = 0;
		try {
			type = Integer.parseInt(request.getParameter("type"));
			boolean successFlag = false;
			if (type == DeeResourceEnum.JDBCDATASOURCE.ordinal()) {
				JDBCResourceBean jdbcbean = new JDBCResourceBean();
				Request2BeanUtil.parseRequest(request, jdbcbean);
				successFlag = deeDataSourceManager.testCon(jdbcbean);
			} else if (type == DeeResourceEnum.JNDIDataSource.ordinal()) {
				JNDIResourceBean jndibean = new JNDIResourceBean();
				Request2BeanUtil.parseRequest(request, jndibean);
				successFlag = deeDataSourceManager.testJNDICon(jndibean);
			}
			return successFlag;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	public ModelAndView dataSourceDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resource_ids[] = request.getParameterValues("id");// 获取数据是以逗号隔开的字符串
		try {
			deeDataSourceManager.delete(resource_ids);
		} catch (TransformException e) {
			log.error("删除数据源出错：" + e.getLocalizedMessage(), e);
		}
		return showDataSourceList(request,response);
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
	 * 类别设置
	 * 
	 * @param bean
	 */
	private void setSubBean(HttpServletRequest request,DeeResourceBean bean) {
		DeeResource rb = null;
		String preStr = "jdbc";
		if (Integer.parseInt(bean.getResource_template_id()) == DeeResourceEnum.JDBCDATASOURCE
				.ordinal()) {
			rb = new JDBCResourceBean();
		} else {
			rb = new JNDIResourceBean();
			preStr = "jndi";
		}
		Request2BeanUtil.parseRequest(request, rb);
		bean.setDr(rb);
		request.setAttribute(preStr + "subbean", rb);
	}
}
