package com.seeyon.v3x.office.common.controller;

/**
 * 丢失报损操作类
 * 
 */
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.domain.OfficeLossInfo;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class OfficeLossController extends BaseManageController {
	
	private static Logger log= Logger.getLogger(OfficeLossController.class);
	private OfficeCommonManager officeCommonManager;
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	private String indexView ;
	
	private String contentView;
	
	public void setOfficeCommonManager(OfficeCommonManager officeCommonManager) {
		this.officeCommonManager = officeCommonManager;
	}
	
	
	public void setIndexView(String indexView) {
		this.indexView = indexView;
	}
	
	
	
	public void setContentView(String contentView) {
		this.contentView = contentView;
	}


	/**
	 * 丢失报损首页
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(indexView);
		return mav;
	}
	
	public ModelAndView frame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("office/loss/lossFrame");
		return mav;
	} 
	public ModelAndView content(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(contentView);
		return mav;
	}
	
	/**
	 * 新建丢失报损信息
	 */
	protected void onCreate(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		User curUser =CurrentUser.get();
		
		OfficeLossInfo lossInfo = new OfficeLossInfo();
		lossInfo.setCreateUser(new Long(curUser.getId()));
		
		String createUserName = curUser.getName();
		
		modelView.addObject("createUserName",createUserName);
		modelView.addObject("bean", lossInfo);
		modelView.addObject("actionType","create");
	}
	
	/**
	 * 详细信息查询列表操作
	 */
	protected void onQuery(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		//取得参数
		String fieldName = request.getParameter("fieldName");		//查询字段名
		if(fieldName == null){
			fieldName = "";
		}
		
		String fieldValue = request.getParameter("fieldValue");		//查询字段值
		
		if(fieldValue == null){
			fieldValue = "";
		}
		
		//管理者 
		Long managerId = new Long(1000);
		User curUser =CurrentUser.get();
		managerId = new Long(curUser.getId());
		
		List list = this.officeCommonManager.findOfficeLossList(fieldName, fieldValue, managerId);
		
		
		//保存结果到视图模型中
		modelView.addObject("list", list);
		modelView.addObject("fieldName", fieldName);
		modelView.addObject("fieldValue", fieldValue);
		
		if(list.size()<=0 && !"".equals(fieldName)){
			modelView.clear();
			modelView.setViewName(this.successView);
			
			modelView.addObject("script", "alert(\'没有找到您需要的信息\');window.history.back(-1);");
		}
		
	}
	
	/**
	 * 丢失报损详细信息修改
	 */
	protected void onEdit(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		//取得办公用品编号
		String lossId = request.getParameter("lossId");    //办公用品编号
		
		//根据办公用品编号取得办公用品详细信息
		OfficeLossInfo lossInfo = officeCommonManager.getOfficeLossById(new Long(lossId));
		
		V3xOrgMember createMember = this.orgManager.getMemberById(lossInfo.getCreateUser());
		if(createMember!=null){
			String createUserName = createMember.getName();
			modelView.addObject("createUserName", createUserName);
		}
		createMember = this.orgManager.getMemberById(lossInfo.getLossManager());
		
		if(createMember!=null){
			String createUserName = createMember.getName();
			modelView.addObject("lossManagerName", createUserName);
		}
		//保存到视图模型中
		modelView.addObject("bean", lossInfo);
		modelView.addObject("actionType","update");
	}
	
	
	/**
	 * 查看显示丢失报损详细信息相关操作
	 */
	protected void onShow(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		//取得参数
		String lossId = request.getParameter("lossId");    //丢失报损编号
		
		OfficeLossInfo lossInfo = null;
		
		try{
			//根据办公用品编号取得办公用品详细信息
			lossInfo=officeCommonManager.getOfficeLossById(new Long(lossId));
			
			V3xOrgMember createMember = this.orgManager.getMemberById(lossInfo.getCreateUser());
			if(createMember!=null){
				String createUserName = createMember.getName();
				modelView.addObject("createUserName", createUserName);
			}
			createMember = this.orgManager.getMemberById(lossInfo.getLossManager());
			
			if(createMember!=null){
				String createUserName = createMember.getName();
				modelView.addObject("lossManagerName", createUserName);
			}
		}catch(Exception e){
			log.error("根据编号取丢失报损详细信息错误："+e.getMessage());
		}
		
		modelView.addObject("bean", lossInfo);
		
	}
	
	/**
	 * 删除所选的丢失报损详细信息
	 */
	protected void onRemoveSelected(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		//取得删除的丢失报损编号集
		String lossIds = request.getParameter("lossIds");		//丢失报损编号集
		
		if(lossIds == null || "".equals(lossIds)){
			//如果没做选择，不作处理
		}else{
			//对应丢失报损编号集的删除标识改为1
			this.officeCommonManager.deleteOfficeLossByIds(lossIds);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("alert(\'已将您选择的记录，成功从数据库中删除。\');\n");
		sb.append("parent.list.location.reload();\n");
		
		modelView.addObject("script", sb.toString());
	}
	
	/**
	 * 登记丢失报损详细信息结果保存相关操作
	 */
	protected void onSave(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView, boolean arg3) throws Exception {
		StringBuffer sb = new StringBuffer();
		//参数取得
		String actionType = request.getParameter("actionType");   //操作类型 create:新增  update:修改
		actionType = actionType.trim();
		
		String lossId = request.getParameter("lossId");		//
		String createUser = request.getParameter("createUser");
		String lossCount = request.getParameter("lossCount");
		String lossDiff = request.getParameter("lossDiff");
		String lossField = request.getParameter("lossField");
		String lossManager = request.getParameter("lossManager");
		String lossMemo = request.getParameter("lossMemo");
		String resourceId = request.getParameter("resourceId");
		String resourceName = request.getParameter("resourceName");
		
		User user = CurrentUser.get();
		int iModel = Integer.parseInt(lossField);
		int modelId=1;
		if(iModel==3){
			modelId=4;
		}else if(iModel==4){
			modelId=3;
		}else{
			modelId=iModel;
		}
		int iResult =this.officeCommonManager.checkAdminModel(modelId, user);
		
		if(iResult >0){
			
			sb.append("alert(\'您没有对该模块进行丢失报损的权限。\');\n");
		}else{
		OfficeLossInfo lossInfo = null;
		
		if("create".equals(actionType)){
			lossInfo = new OfficeLossInfo();
			lossInfo.setLossId(new Long(UUIDLong.longUUID()));
			lossInfo.setCreateDate(new Date());
			lossInfo.setCreateUser(new Long(createUser));
			lossInfo.setDeleteFlag(new Integer(0));
			lossInfo.setLossCount(new Integer(lossCount));
			lossInfo.setLossDiff(lossDiff);
			lossInfo.setLossField(lossField);
			lossInfo.setLossManager(new Long(lossManager));
			lossInfo.setLossMemo(lossMemo);
			lossInfo.setResourceId(resourceId);
			lossInfo.setResourceName(resourceName);
			this.officeCommonManager.createOfficeLoss(lossInfo);
			
		}else{
			try{
				lossInfo = this.officeCommonManager.getOfficeLossById(new Long(lossId));
			}catch(Exception e){
				
			}
			
			if(lossInfo != null){
				lossInfo.setCreateUser(new Long(createUser));
				lossInfo.setLossCount(new Integer(lossCount));
				lossInfo.setLossDiff(lossDiff);
				lossInfo.setLossField(lossField);
				lossInfo.setLossManager(new Long(lossManager));
				lossInfo.setLossMemo(lossMemo);
				lossInfo.setResourceId(resourceId);
				lossInfo.setResourceName(resourceName);
				lossInfo.setModifyDate(new Date());
				this.officeCommonManager.updateOfficeLoss(lossInfo);
			}
			
		}
		
		
		sb.append("alert(\'操作已成功\');\n");
		sb.append("parent.list.location.reload();\n");
		}
		modelView.addObject("script", sb.toString());
	}
}
