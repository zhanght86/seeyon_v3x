package com.seeyon.v3x.office.common.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class OfficeTypeInfoController extends BaseManageController {

	private String indexView ;
	private OfficeCommonManager officeCommonManager;
	
	public void setIndexView(String indexView) {
		this.indexView = indexView;
	}
	
	public void setOfficeCommonManager(OfficeCommonManager officeCommonManager) {
		this.officeCommonManager = officeCommonManager;
	}
	
	/**
	 * 类别信息首页
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(indexView);
		return mav;
	}
	
	public ModelAndView frame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("office/common/typeInfoFrame");
		return mav;
	}
	
	public ModelAndView content(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("office/common/typeInfoContent");
		return mav;
	}
	
	/**
	 * 列表页
	 */
	protected void onQuery(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		String fieldName = request.getParameter("condition");		//查询字段名
		if(fieldName == null){
			fieldName = "";
		}
		String fieldValue = request.getParameter("textfield");		//查询字段值
		if(fieldValue == null){
			fieldValue = "";
		}
		User curUser =CurrentUser.get();
		List list = this.officeCommonManager.getTypeInfoList(fieldName,fieldValue,curUser.getLoginAccount());
		
		modelView.addObject("list", list);
		modelView.addObject("fieldName", fieldName);
		modelView.addObject("fieldValue", fieldValue);
		
//		if(list.size()<=0 && !"".equals(fieldName)){
//			modelView.clear();
//			modelView.setViewName(this.successView);
//			
//			modelView.addObject("script", "alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.nofound")+"\');window.history.back(-1);");
//		}
	}
	
	/**
	 * 修改 
	 */
	protected void onEdit(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		String typeId = request.getParameter("typeId");
		
		OfficeTypeInfo typeInfo = this.officeCommonManager.getTypeInfoById(Long.parseLong(typeId));
		
		modelView.addObject("bean", typeInfo);
		
	}
	
	/**
	 * 查看
	 */
	protected void onShow(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		String typeId = request.getParameter("typeId");
		
		OfficeTypeInfo typeInfo = this.officeCommonManager.getTypeInfoById(Long.parseLong(typeId));
		
		modelView.addObject("bean", typeInfo);
	}

	/**
	 * 删除操作
	 */
	protected void onRemoveSelected(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView) throws Exception {
		
		String modelIds = request.getParameter("modelIds");		//丢失报损编号集
		
		if(modelIds == null || "".equals(modelIds)){
			//如果没做选择，不作处理
		}else{
            StringBuffer sb = new StringBuffer();
            String[] typeIdArray=StringUtils.split(modelIds, ",");
            for (int i = 0; i < typeIdArray.length; i++)
            {
               if(officeCommonManager.checkTypeInOffice(new Long(typeIdArray[i])))
               {
                   sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.use")+"\');\n");
                   sb.append("parent.location.reload();\n");
                   modelView.addObject("script", sb.toString());
                   return;
               }
            }
			//对应丢失报损编号集的删除标识改为1
			this.officeCommonManager.deleteOfficeTypeByIds(modelIds);
			sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.delete.success")+"\');\n");
			sb.append("parent.location.reload();\n");
			
			modelView.addObject("script", sb.toString());
		}
		
	}
	
	protected void onSave(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView, boolean arg3) throws Exception {
		
		String typeId = request.getParameter("typeId");
		String modelId = request.getParameter("modelId");
		 String typeInfo = request.getParameter("typeInfo");
		
		OfficeTypeInfo officeType = new OfficeTypeInfo();
		officeType.setModelId(modelId);
		officeType.setTypeInfo(typeInfo.trim());
		User curUser =CurrentUser.get();
		
		officeType.setDepartId(new Long(curUser.getLoginAccount()));
		StringBuffer sb = new StringBuffer();
		
		if("".equals(typeId)){
			officeType.setTypeId(new Long(UUIDLong.longUUID()));
			if(this.officeCommonManager.checkDuplicate(officeType, false)){
				sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.name.exit")+"\');\n");
				sb.append("window.history.back(-1);");
			}else{
				officeType.setCreateDate(new Date());
				officeType.setDeleteFlag(new Integer(0));
				
				this.officeCommonManager.createTypeInfo(officeType);
				sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.success")+"\');\n");
				sb.append("parent.location.reload();\n");
			}	
			
		}else{
			officeType.setTypeId(new Long(typeId));
			if(this.officeCommonManager.checkDuplicate(officeType, true)){
				officeType.setModifyDate(new Date());
				officeType.setDeleteFlag(new Integer(0));
				sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.name.exit")+"\');\n");
				sb.append("window.history.back(-1);");
			}else{
				officeType.setModifyDate(new Date());
				officeType.setDeleteFlag(new Integer(0));
				this.officeCommonManager.updateTypeInfo(officeType);
				sb.append("alert(\'"+ResourceBundleUtil.getString(OfficeModelType.OFFICE_TYPE_RESOURCE_NAME, "type.alert.success")+"\');\n");
				sb.append("parent.location.reload();\n");
			}
			
		}
		

		modelView.addObject("script", sb.toString());
	}
	
}
