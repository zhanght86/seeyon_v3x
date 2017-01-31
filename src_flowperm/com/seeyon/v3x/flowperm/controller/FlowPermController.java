package com.seeyon.v3x.flowperm.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.util.NodePolicy;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocElementFlowPermAcl;
import com.seeyon.v3x.edoc.manager.EdocElementFlowPermAclManager;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.flowperm.util.Constants;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

@CheckRoleAccess(roleTypes={RoleType.Administrator,RoleType.AccountEdocAdmin})
public class FlowPermController extends BaseController {
	
	private FlowPermManager flowPermManager;
	
	private MetadataManager metadataManager;
	
	private ConfigManager configManager;
	
	private OrgManager orgManager;
	
	private EdocElementFlowPermAclManager edocElementFlowPermAclManager;
	
	private EdocElementManager edocElementManager;

	private AppLogManager appLogManager;
	
	//系统预制类型节点权限
	public static final int SYSTEM_FLOWPERMTYOE = 0;
	
	public FlowPermManager getFlowPermManager() {
		return flowPermManager;
	}

	public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ModelAndView listMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		/*
		//自动生成节点权限
		//--start--
		User user = CurrentUser.get();
		FlowPermHelper.initialFlowPermByDomainId(user.getLoginAccount());
		FlowPermHelper.initialV3xOrgMemberSecurityPermission();//自动为每个人员初始化权限（1：全部权限）
		//--end--
		*/
		
		ModelAndView mav = null;
		String category = request.getParameter("category");
		
		/**
		 * 根据category来判断选择流程权限的类型
		 * col_flow_perm_policy 为协同流程，进入permManage子目录下的页面
		 * edoc 为公文流程，进入edocPermManage子目录下的页面
		 */
		
		if(null!=category && !"".equals(category) && category.equals("col_flow_perm_policy")){
			mav = new ModelAndView("flowperm/permManage/list_main");
		}else if(null!=category && !"".equals(category) && category.equals("edoc")){
			mav = new ModelAndView("flowperm/edocPermManage/list_main");
		}
		
		if (request.getParameter("id") != null){
			mav.addObject("id", request.getParameter("id"));
		}
		/**
		 * 统一设置公文流程category为'edoc' / 协同则为'col_flow_perm_policy'
		 */
		mav.addObject("category",category);
		return mav;
	}

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get();
		long loginAccount = user.getLoginAccount();
		
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());//判断是否为集团版
		if(isGroupVer && orgManager.isGroupAdmin(user.getLoginName())){//当前用户是否是集团管理员
			loginAccount = 1L;
		}
		
		//按照条件进行查询
		String expressionType = request.getParameter("expressionType");
		String expressionValue = request.getParameter("expressionValue");
		String category = request.getParameter("category");
		
		ModelAndView mav = null;
		//协同节点权限
		if(null!=category && !"".equals(category) && category.equals("col_flow_perm_policy")){
			
			mav = new ModelAndView("flowperm/permManage/list_iframe");
			List<FlowPerm> list = null;
			List<FlowPerm> returnList = new ArrayList<FlowPerm>();
			if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
				//按照条件进行查询
				if("isRef".equals(expressionType)){//按照引用状态进行条件查询
					list = flowPermManager.getFlowpermsByRef(category, Integer.valueOf(expressionValue), loginAccount);
					
				}else if("enabled".equals(expressionType)){//按照停启用状态进行条件查询
					list = flowPermManager.getFlowpermsByStatus(category, Integer.valueOf(expressionValue), loginAccount);
					
				}else if("name".equals(expressionType)){//按照名称进行条件查询
					list = flowPermManager.getFlowPermsByCategory(category, loginAccount);
				}
			}else{//不按照条件查询，搜索全部
				list = flowPermManager.getFlowPermsByCategory(category, loginAccount);
			}
			
			//进行名称查询过滤
			if("name".equals(expressionType)){
				for(FlowPerm flowPerm:list){
					//系统预置-国际化转换汉字
					if(flowPerm.getType() == SYSTEM_FLOWPERMTYOE){
						Locale local = LocaleContext.getLocale(request);
						String resource = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
						String flowPremName = ResourceBundleUtil.getString(resource, local, flowPerm.getLabel());
						flowPerm.setName(flowPremName);
					}
					//进行名称模糊查询
					if(flowPerm.getName().contains(expressionValue)){
						returnList.add(flowPerm);
					}
					
				}
				
				mav.addObject("list", pagenate(returnList));
				
			}else {
				
				mav.addObject("list", pagenate(list));
			}
			
		}
		//协同节点权限结束
		
		if(null!=category && !"".equals(category) && category.equals("edoc")){
			mav = new ModelAndView("flowperm/edocPermManage/list_iframe");
			List<FlowPerm> totalList = new ArrayList<FlowPerm>();
			List<FlowPerm> returnList = new ArrayList<FlowPerm>();
			List<FlowPerm> sendList = null;
			List<FlowPerm> recList = null;
			List<FlowPerm> qianBaoList = null;
			
			if(SystemEnvironment.hasPlugin("edoc")){
				
				if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
					if("isRef".equals(expressionType)){//按照引用状态进行条件查询
						
						sendList = flowPermManager.getFlowpermsByRef("edoc_send_permission_policy", Integer.valueOf(expressionValue), loginAccount);
						recList = flowPermManager.getFlowpermsByRef("edoc_rec_permission_policy", Integer.valueOf(expressionValue), loginAccount);
						qianBaoList = flowPermManager.getFlowpermsByRef("edoc_qianbao_permission_policy", Integer.valueOf(expressionValue), loginAccount);
						
					}else if("enabled".equals(expressionType)){//按照停启用状态进行条件查询
						
						sendList = flowPermManager.getFlowpermsByStatus("edoc_send_permission_policy",Integer.parseInt(expressionValue), loginAccount);
						recList = flowPermManager.getFlowpermsByStatus("edoc_rec_permission_policy",Integer.parseInt(expressionValue), loginAccount);
						qianBaoList = flowPermManager.getFlowpermsByStatus("edoc_qianbao_permission_policy",Integer.parseInt(expressionValue), loginAccount);
						
					}else if("catogry".equals(expressionType)){//按照权限类别进行条件查询
						
						totalList = flowPermManager.getFlowPermsByCategory(expressionValue, loginAccount);
						
					}else if("name".equals(expressionType)){//按照名称查询，先获取所有flowPerm
						
						sendList = flowPermManager.getFlowPermsByCategory("edoc_send_permission_policy", loginAccount);
						recList = flowPermManager.getFlowPermsByCategory("edoc_rec_permission_policy", loginAccount);
						qianBaoList = flowPermManager.getFlowPermsByCategory("edoc_qianbao_permission_policy", loginAccount);
						
					}
				}else{//无条件查询
					sendList = flowPermManager.getFlowPermsByCategory("edoc_send_permission_policy", loginAccount);
					recList = flowPermManager.getFlowPermsByCategory("edoc_rec_permission_policy", loginAccount);
					qianBaoList = flowPermManager.getFlowPermsByCategory("edoc_qianbao_permission_policy", loginAccount);
				}
					
			}
			
			if(totalList.size() == 0){
				totalList.addAll(sendList);
				totalList.addAll(recList);
				totalList.addAll(qianBaoList);
			}
			
			//进行名称查询过滤
			if("name".equals(expressionType)){
				for(FlowPerm flowPerm:totalList){
					//系统预置-国际化转换汉字
					if(flowPerm.getType() == SYSTEM_FLOWPERMTYOE){
						Locale local = LocaleContext.getLocale(request);
						String resource = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
						String flowPremName = ResourceBundleUtil.getString(resource, local, flowPerm.getLabel());
						flowPerm.setName(flowPremName);
					}
					//进行名称模糊查询
					if(flowPerm.getName().contains(expressionValue)){
						returnList.add(flowPerm);
					}
				}
				mav.addObject("list", pagenate(returnList));
			}else {
				mav.addObject("list", pagenate(totalList));
			}
		}
		mav.addObject("category",category);	
		return mav;
	}

	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView mav = null;
		String id = request.getParameter("id");
		String category = request.getParameter("category");
		FlowPerm flowPerm = flowPermManager.getFlowPerm(Long.valueOf(id));
		
		Metadata metadata1 = metadataManager.getMetadata(MetadataNameEnum.node_control_action);
		Metadata metadata2 = metadataManager.getMetadata(MetadataNameEnum.col_basic_action);
		Metadata metadata3 = null;
		Metadata metadata4 = null;
		
		//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，没有常用操作，退回和终止挪到基本操作，取消当前会签、多级会签 start
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        //branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，没有常用操作，退回和终止挪到基本操作，取消当前会签、多级会签end
		
		Metadata metadataControl=new Metadata();
		Metadata metadataBase=new Metadata();
		//阅读节点只有传阅操作 bug29020
		if(null!=metadata1){
			metadataControl.addItems(metadata1.getItems());
		}
		if(null!=metadata2){
			metadataBase.addItems(metadata2.getItems());
		}
		
		
		//FlowPerm flowPerm = flowPermManager.getFlowPerm(category, configItem);
		
		/**
		 * 判断category的值
		 * 暂为 协同： col_flow_perm_policy 
		 * 	   公文 --发文：edoc_send_permission_policy
		 *          -收文：edoc_rec_permission_policy
		 *          -签报: edoc_qianBao_permission_policy
		 */
		
		if(null!=category && !"".equals(category) && "col_flow_perm_policy".equals(category)){
			mav = new ModelAndView("flowperm/permManage/config_modify");
		}else{
			
			metadata3 = metadataManager.getMetadata(MetadataNameEnum.edoc_node_control_action);
			
			if(null!=metadata3){
				metadataControl.addItems(metadata3.getItems());
				metadataControl.removeItem(6102L);
			}
			metadata4 = metadataManager.getMetadata(MetadataNameEnum.edoc_basic_action);
			
			if(null!=metadata4){
				metadataBase.addItems(metadata4.getItems());
				//公文去掉,处理后删除,处理后归档选项
				metadataBase.removeItem(2207L);
				metadataBase.removeItem(6100L);
				metadataBase.removeItem(6102L);
				//metadataBase.removeItem(2228L);
				//metadataBase.removeItem(2214L);

			}
			
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，退回和终止挪到基本操作 start
			if(isGovVersion){
				MetadataItem item_2201= metadataManager.getMetadataItem(metadata1,2201L); //退回
				MetadataItem item_2202= metadataManager.getMetadataItem(metadata1,2202L); //终止
				List<MetadataItem> itemList=new ArrayList<MetadataItem>();
				if(item_2201!=null)itemList.add(item_2201);
				if(item_2202!=null)itemList.add(item_2202);
                if(itemList.size()>0)metadataBase.addItems(itemList);
				
				metadataControl.removeItem(2229L);//多级会签
				metadataControl.removeItem(2201L);//回退
				metadataControl.removeItem(2202L);//终止
				metadataControl.removeItem(2211L);//当前会签
			}
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，退回和终止挪到基本操作end

			//阅读权限特殊处理。
			if("yuedu".equals(flowPerm) && ("edoc_send_permission_policy".equals(category)||"edoc_qianbao_permission_policy".equals(category))){
				List<MetadataItem> allowOperations=new ArrayList<MetadataItem>();
				for(MetadataItem metadataItem : metadataControl.getItems()){
					if(metadataItem.getId().longValue()==2202L    //终止
						||metadataItem.getId().longValue()==2209L //知会
						||metadataItem.getId().longValue()==2305L //传阅 
						||metadataItem.getId().longValue()==2228L //转公告
						||metadataItem.getId().longValue()==2303L //部门归档
						||metadataItem.getId().longValue()==2306L)//文单签批
					{
						allowOperations.add(metadataItem);
					} 
				}
				metadataControl.setItems(allowOperations); 
			}
			mav = new ModelAndView("flowperm/edocPermManage/config_modify");
			/**
			 * 查询所有的公文元素 param 1:条件（1.启用，0.非启用）,param 2: 启使页,param 3:每页显示的记录数
			 */
			
			boolean initiate = false;
			
			List<EdocElementFlowPermAcl> acl_list = edocElementFlowPermAclManager.getEdocElementFlowPermAcls(Long.valueOf(id));
			
			List<EdocElement> elementList = edocElementManager.getEdocElementsByStatus(1, 1, 10000);	
			
			if(null!=acl_list && acl_list.size()>0){
				initiate = true;
			}
			/**
			*判断是否为新添加的元素
			*/
			List<EdocElementFlowPermAcl> final_list = new ArrayList<EdocElementFlowPermAcl>();
			
			for(EdocElement ele:elementList){
					//处理意见,logo图片不出现在设置权限中
					if(isGovVersion){
						if(ele.getType()>=6){continue;}
					}else{
						if(ele.getType()>=6
								|| "attachments".equals(ele.getFieldName())){continue;}
					}
					
					EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
					acl.setIdIfNew();
					if(initiate && !"zhihui".equals(flowPerm.getName())){
						for(EdocElementFlowPermAcl fAcl : acl_list){
							try{//由于公文元素生成多套，删除后倒志错误；
								if(fAcl.getEdocElement().getFieldName().equals(ele.getFieldName())){
									acl.setAccess(fAcl.getAccess());
									break;
								}else{
									acl.setAccess(0);
								}
							}catch(Exception e){}
						}
					}else{
						acl.setAccess(0);
					}
					acl.setEdocElement(ele);			
					final_list.add(acl);
			}
			
			mav.addObject("aclList", final_list);
		}
		
		mav.addObject("flowPerm",flowPerm);
		mav.addObject("metadataNode",metadataControl);
		metadataBase.removeItem(2207L);
		mav.addObject("metadataBasic",metadataBase);
		
		mav.addObject("operType", "change");
		mav.addObject("category",category);
		
		return mav;
	}

	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String configCategory = request.getParameter("category");
		String configItem = request.getParameter("itemName");
		
		PrintWriter writer = response.getWriter();
		
		String id = request.getParameter("id");
		
		String[] ids = id.split(",");
		User user = CurrentUser.get();
		for(int i=0;i<ids.length;i++){
	        FlowPerm flowPerm = flowPermManager.getFlowPerm(Long.valueOf(ids[i]));
			String str = flowPermManager.deleteFlowPerm(Long.valueOf(ids[i]));
			if(null!=str && !"".equals(str)){
				writer.println(str);
			}
			if(!flowPerm.getCategory().equals(MetadataNameEnum.col_flow_perm_policy.toString())){
			   //公文日志
	           appLogManager.insertLog(user, AppLogAction.Edoc_FlowPerm_Delete,user.getName(),flowPerm.getName());
			}
		}
		return super.refreshWindow("parent");
	}
	public ModelAndView change(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get();
		
		PrintWriter print = response.getWriter();
		String id = request.getParameter("id");
		String category = request.getParameter("category");
		
		FlowPerm flowPerm = flowPermManager.getFlowPerm(Long.valueOf(id));
		
		if(null!=flowPerm){
		String basic = request.getParameter("basic");									//基本的操作
		String common = request.getParameter("common");									//常用操作
		String advanced = request.getParameter("advanced");								//高级操作

		String description = request.getParameter("description");						//描述
		String type = request.getParameter("type");										//类型，是否为系统预制，用户定制
		String status = request.getParameter("status");									//状态，是否为启用，禁用
		String location = request.getParameter("location");								//权限的位置
		String isRef = request.getParameter("isRef");								    //是否引用，添加新权限时默认为否
		String name = request.getParameter("itemName");  								//权限名
		String edocType = request.getParameter("edocType");
		String opinionPolicy = request.getParameter("opinionPolicy");
		String attitude = request.getParameter("attitude");
		String batch = request.getParameter("batch");
		
		//1.首先判断是否进行 "停用-->启用" 操作
		if(null!=flowPerm.getIsEnabled()                                  //判断权限的是否启用状态是否为空
				&& Constants.F_status_disabled == flowPerm.getIsEnabled() //判断权限是否为停用状态
				&& null!=status 
				&& Constants.F_status_enabled == Integer.valueOf(status) //判断前台传来的参数是否为启用
				&& null!=name){                                          //判断前台的名称是否为空
				boolean bool = flowPermManager.checkName(category, name, user.getLoginAccount());
				if(bool){
					print.println("<script>");
					print.println("alert(parent._('flowpermLang.flowperm_enable_error'));");
					print.println("self.history.back();");
					print.println("</script>");			
					return null;
				}
		}
		
		
		//2.判断是否进行 "改变名称" 操作
		if(null!=name 
				&& flowPerm.getType()!=Constants.F_type_system         	//权限是否为系统预置,如果是则不允许它修改名称,也不用进行判断
				&&!flowPerm.getName().equals(name)                     	//前台传入的名称是否与原权限的名称相同
				){
				
				if(flowPerm.getIsRef().intValue() == FlowPerm.Node_isRef.intValue()){
					print.println("<script>");
					print.println("alert(parent._('flowpermLang.flowperm_name_change_forbidden'));");
					print.println("self.history.back();");
					print.println("</script>");
					return null;
				}
			
				boolean bool = flowPermManager.checkName(category, name, user.getLoginAccount());
				if(bool){
					print.println("<script>");
					print.println("alert(parent._('flowpermLang.flowperm_duplicatedname'));");
					print.println("self.history.back();");
					print.println("</script>");
					return null;
				}
			}
		
		//判断权限的类别 <-- "公文 ： 发文，收文，签报","协同:  -->"
		if(null!=edocType && !"".equals(edocType) && "0".equals(edocType) || "edoc.formstyle.dispatch".equals(edocType)){
			category = MetadataNameEnum.edoc_send_permission_policy.toString();
		}else if(null!=edocType && !"".equals(edocType) && "1".equals(edocType) || "edoc.formstyle.receipt".equals(edocType)){
			category = MetadataNameEnum.edoc_rec_permission_policy.toString();
		}else if(null!=edocType && !"".equals(edocType) && "2".equals(edocType) || "edoc.formstyle.qianbao".equals(edocType)){
			category = MetadataNameEnum.edoc_qianbao_permission_policy.toString();
		}else if(null==edocType || "".equals(edocType)){
			category = MetadataNameEnum.col_flow_perm_policy.toString();
		}
		
		NodePolicy nodePolicy = new NodePolicy();										//定义 NodePolicy类
		nodePolicy.setAdvancedAction(advanced);                                         //，封装一部分属性，转XML
		nodePolicy.setBaseAction(basic);                                                //存在ext_config_value字段中
		nodePolicy.setCommonAction(common);
		
		//如果是自定义的权限,进行正常状态设置。 如果为系统预置权限, 把原来的状态重新赋值回去,避免空指针异常的出现
		if(null!=type && Integer.valueOf(type) == Constants.F_type_custom){
		if(null!=location && !"".equals(location)){
				nodePolicy.setLocation(Integer.valueOf(location));
			}
		}else{
			nodePolicy.setLocation(flowPerm.getNodePolicy().getLocation());
		}
		
		if(null!=status && !"".equals(status)){
			nodePolicy.setIsEnabled(Integer.valueOf(status));
		}else{
			nodePolicy.setIsEnabled(flowPerm.getNodePolicy().getIsEnabled());
		}
		if(null!=status && !"".equals(isRef)){
			nodePolicy.setIsRef(Integer.valueOf(isRef));
		}else{
			nodePolicy.setIsRef(flowPerm.getNodePolicy().getIsRef());
		}
		
		if(!Strings.isBlank(opinionPolicy)){
			nodePolicy.setOpinionPolicy(Integer.valueOf(opinionPolicy));
		}else{
			nodePolicy.setOpinionPolicy(flowPerm.getNodePolicy().getOpinionPolicy());
		}
		
		if(Strings.isNotBlank(attitude)){
			nodePolicy.setAttitude(Integer.parseInt(attitude));
		}else{
			nodePolicy.setAttitude(flowPerm.getNodePolicy().getAttitude());
		}
		
		if(Strings.isNotBlank(batch)){
			nodePolicy.setBatch(Integer.parseInt(batch));
		}
		
		if(null!=flowPerm){
			flowPerm.setDescription(description);
				if(null!=type && !"".equals(type)){
					flowPerm.setType(Integer.valueOf(type));
				}
			flowPerm.setCategory(category);
			
			//判断是否为系统预置权限,如是,则不修改名称。
			if(null!=type && Integer.valueOf(type) == Constants.F_type_custom){
				flowPerm.setName(name);
			}
			
			flowPerm.setNodePolicy(nodePolicy);
	
			flowPermManager.updateFlowPerm(flowPerm);
			
			if(!category.equals(MetadataNameEnum.col_flow_perm_policy.toString())){
				List<EdocElement> elementList =  edocElementManager.getEdocElementsByStatus(1,1,10000);
				List<EdocElementFlowPermAcl> tempList = new ArrayList<EdocElementFlowPermAcl>();
				for(EdocElement ele:elementList){
					if(ele.getType()==EdocElement.C_iElementType_LogoImg || ele.getType()==EdocElement.C_iElementType_Comment)
					{
						continue;
					}
					
					EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
					acl.setIdIfNew() ;
					String access = request.getParameter(String.valueOf(ele.getId()));
					if(null!=access && !"".equals(access)){
						acl.setAccess(Integer.valueOf(access));
					}else{
						acl.setAccess(Constants.F_ELEMENT_TYPE_READONLY);
					}
					acl.setEdocElement(ele);
					acl.setFlowPermId(flowPerm.getFlowPermId());
					tempList.add(acl);
				}
				edocElementFlowPermAclManager.deleteEdocElementFlowPermAcl(flowPerm.getFlowPermId());//首先删除该权限下的所有元素,然后重新添加.
				edocElementFlowPermAclManager.saveEdocElementFlowPermAcls(tempList);
				//公文日志
				appLogManager.insertLog(user, AppLogAction.Edoc_FlowPermModify,user.getName(),name);
			}else{
				//协同日志
				appLogManager.insertLog(user, AppLogAction.Coll_FlowPrem_Edit, user.getName(), this.getNodeName(flowPerm));
			}
		}
		}
		return super.refreshWindow("parent");
	}

	public ModelAndView newFlowPerm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String categroy = request.getParameter("category");
		ModelAndView mav = null;
		if(null!=categroy && !"".equals(categroy) && "col_flow_perm_policy".equals(categroy)){
			mav = new ModelAndView("flowperm/permManage/config_modify");
		}else{

			List<EdocElement> elementList = edocElementManager.getEdocElementsByStatus(1,1,10000);
			List<EdocElementFlowPermAcl> final_list = new ArrayList<EdocElementFlowPermAcl>();
			
			for(EdocElement ele:elementList){
				//处理意见,logo图片不出现在设置权限中
				if(ele.getType()>=6){continue;}
				EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
				acl.setIdIfNew();
				acl.setEdocElement(ele);			
				final_list.add(acl);
		}
			mav = new ModelAndView("flowperm/edocPermManage/config_modify");
			mav.addObject("category", categroy);
			mav.addObject("aclList", final_list);
			
		}
		mav.addObject("operType","add");
		mav.addObject("category", categroy);
		return mav;

	}
	/**
	 * First:  Because of edoc-flowperm has the authorization of element,so add method must
	 * 		   placed in the controller
	 * Second: And col-floperm no such operation, then invoke flopermManager.add() have ought to implement.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get();
		
		long orgAccountId = user.getLoginAccount();
		
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());//判断是否为集团版
		if(isGroupVer && orgManager.isGroupAdmin(user.getLoginName())){//当前用户是否是集团管理员
			orgAccountId = 1L;
		}		
		
		
		PrintWriter print = response.getWriter();
		
		String basic = request.getParameter("basic");									//基本的操作
		String common = request.getParameter("common");									//常用操作
		String advanced = request.getParameter("advanced");								//高级操作
				
		String category = request.getParameter("category");								//权限的类别，公文，流程...
		String description = request.getParameter("description");						//描述
		String type = request.getParameter("type");										//类型，是否为系统预制，用户定制
		String status = request.getParameter("status");									//状态，是否为启用，禁用
		String location = request.getParameter("location");								//权限的位置
		String isRef = request.getParameter("isRef");								    //是否引用，添加新权限时默认为否
		String name = request.getParameter("itemName");                                 //权限名
		String edocType = request.getParameter("edocType");
		
		String opinionPolicy = request.getParameter("opinionPolicy");
		String batch = request.getParameter("batch");
		String attitude = request.getParameter("attitude");
		
		NodePolicy nodePolicy = new NodePolicy();										//定义 NodePolicy类
		nodePolicy.setAdvancedAction(advanced);                                         //，封装一部分属性，转XML
		nodePolicy.setBaseAction(basic);                                                //存在ext_config_value字段中
		nodePolicy.setCommonAction(common);
		nodePolicy.setIsEnabled(Integer.valueOf(status));
		nodePolicy.setIsRef(Integer.valueOf(isRef));
		nodePolicy.setLocation(Integer.valueOf(location));
		nodePolicy.setOpinionPolicy(Integer.valueOf(opinionPolicy));
		if(Strings.isNotBlank(batch)){
			nodePolicy.setBatch(Integer.parseInt(batch));
		}
		if(Strings.isNotBlank(attitude)){
			nodePolicy.setAttitude(Integer.parseInt(attitude));
		}
		
		FlowPerm flowPerm = new FlowPerm();
		flowPerm.setDescription(description);
		flowPerm.setType(Integer.valueOf(type));
		
		ModelAndView mav = new ModelAndView("flowperm/edocPermManage/config_modify");
			
		if(null!=edocType && !"".equals(edocType) && "0".equals(edocType)){
			category = MetadataNameEnum.edoc_send_permission_policy.toString();
		}else if(null!=edocType && !"".equals(edocType) && "1".equals(edocType)){
			category = MetadataNameEnum.edoc_rec_permission_policy.toString();
		}else if(null!=edocType && !"".equals(edocType) && "2".equals(edocType)){
			category = MetadataNameEnum.edoc_qianbao_permission_policy.toString();
		}else{
			category = MetadataNameEnum.col_flow_perm_policy.toString();
			mav = new ModelAndView("flowperm/permManage/config_modify");
		}
		
		flowPerm.setCategory(category);
		flowPerm.setName(name);
		flowPerm.setNodePolicy(nodePolicy);
		
		if(null!=category && !"".equals(category) && null!=name && !"".equals(name)){
			boolean bool = flowPermManager.checkName(category, name, user.getLoginAccount());
			if(bool){
				Metadata metadata1 = metadataManager.getMetadata(MetadataNameEnum.node_control_action);
				Metadata metadata2 = metadataManager.getMetadata(MetadataNameEnum.col_basic_action);
				Metadata metadata3 = null;
				Metadata metadata4 = null;
				
				Metadata metadataControl=new Metadata();
				Metadata metadataBase=new Metadata();
				
				if(null!=metadata1){
					metadataControl.addItems(metadata1.getItems());
				}
				if(null!=metadata2){
					metadataBase.addItems(metadata2.getItems());
				}
				/**
				 * 判断category的值
				 * 暂为 协同： col_flow_perm_policy 
				 * 	   公文 --发文：edoc_send_permission_policy
				 *          -收文：edoc_rec_permission_policy
				 *          -签报: edoc_qianBao_permission_policy
				 */
				
				if(null!=category && !"".equals(category) && "col_flow_perm_policy".equals(category)){
				}else{
					metadata3 = metadataManager.getMetadata(MetadataNameEnum.edoc_node_control_action);
					if(null!=metadata3){
						metadataControl.addItems(metadata3.getItems());
					}
					metadata4 = metadataManager.getMetadata(MetadataNameEnum.edoc_basic_action);
					
					if(null!=metadata4){
						metadataBase.addItems(metadata4.getItems());
					}
					/**
					 * 查询所有的公文元素 param 1:条件（1.启用，0.非启用）,param 2: 启使页,param 3:每页显示的记录数
					 */
					
					List<EdocElement> elementList = edocElementManager.getEdocElementsByStatus(1,1,10000);
					List<EdocElementFlowPermAcl> final_list = new ArrayList<EdocElementFlowPermAcl>();
					
					for(EdocElement ele:elementList){
						EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
						acl.setIdIfNew();
						acl.setEdocElement(ele);			
						final_list.add(acl);
					}
					mav.addObject("aclList", final_list);
				}
				
				print.println("<script>");
				print.println("alert(parent._('flowpermLang.flowperm_duplicatedname'));");
				print.println("self.history.back();");
				print.println("</script>");
				mav.addObject("flowPerm",flowPerm);
				mav.addObject("metadataNode",metadataControl);
				mav.addObject("metadataBasic",metadataBase);
				mav.addObject("operType", "add");
				mav.addObject("category", category);
				mav.addObject("basic",basic);
				return null;
			}
		}
		
		if(null!=edocType && !"".equals(edocType)){
			
			String xml = XMLCoder.encoder(flowPerm.getNodePolicy());

			ConfigItem item = new ConfigItem();
			item.setIdIfNew();
			item.setCreateDate(new java.sql.Timestamp(System.currentTimeMillis()));
			item.setModifyDate(new java.sql.Timestamp(System.currentTimeMillis()));
			item.setConfigCategory(flowPerm.getCategory());
			item.setConfigDescription(flowPerm.getDescription());
			item.setConfigItem(flowPerm.getName());
			item.setConfigType(String.valueOf(flowPerm.getType()));
			item.setExtConfigValue(xml);
			item.setOrgAccountId(orgAccountId);
			
			configManager.addConfigItem(item);
			//更新缓存
			this.flowPermManager.addNodePolicy(flowPerm.getName(), orgAccountId);
			/*
			try {
				metadataManager.addMetadataItem(MetadataNameEnum.valueOf(flowPerm.getCategory()), flowPerm.getName(), flowPerm.getName(), null, flowPerm.getDescription());
			} catch (NoSuchMetadataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MetadataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			List<EdocElement> elementList =  edocElementManager.getEdocElementsByStatus(1,1,10000);
			List<EdocElementFlowPermAcl> tempList = new ArrayList<EdocElementFlowPermAcl>();
			for(EdocElement ele:elementList){
				
				EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
				acl.setIdIfNew() ;
				String access = request.getParameter(String.valueOf(ele.getId()));
				if(null!=access && !"".equals(access)){
					acl.setAccess(Integer.valueOf(access));
				}else{
					acl.setAccess(Constants.F_ELEMENT_TYPE_READONLY);
				}
				acl.setEdocElement(ele);
				acl.setFlowPermId(item.getId());
				tempList.add(acl);
			}
			edocElementFlowPermAclManager.saveEdocElementFlowPermAcls(tempList);
			//记录日志
			appLogManager.insertLog(user, AppLogAction.Edoc_FlowPrem_Create,user.getName(),name);

		}else{
			flowPermManager.addFlowPerm(flowPerm, orgAccountId);
			appLogManager.insertLog(user, AppLogAction.Coll_FlowPrem_Create, user.getName(), flowPerm.getName());
		}
		
		return super.refreshWindow("parent");
	}
	
	public ModelAndView operationChoose(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，没有常用操作，退回和终止挪到基本操作，取消文单修改、当前会签、多级会签 start
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        //branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，没有常用操作，退回和终止挪到基本操作，取消文单修改、当前会签、多级会签end
        
        
		Metadata metadata1 = metadataManager.getMetadata(MetadataNameEnum.node_control_action);
		Metadata metadata2 = metadataManager.getMetadata(MetadataNameEnum.col_basic_action);
		Metadata metadata3 = null;
		Metadata metadata4 = null;
		
		String flowPermName=request.getParameter("flowPermName");
		
		Metadata tempControlMmetadata = new Metadata();
		if(null!=metadata1){
			tempControlMmetadata.addItems(metadata1.getItems());
		}
		
		Metadata tempBaseMmetadata = new Metadata();
		if(null!=metadata2){
		tempBaseMmetadata.addItems(metadata2.getItems());
		}
		
		String category = request.getParameter("category");
		
		if(null!=category && !"".equals(category) && ("edoc_rec_permission_policy".equals(category) || "edoc_send_permission_policy".equals(category) || "edoc_qianbao_permission_policy".equals(category))){
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，退回和终止挪到基本操作 start
			if(isGovVersion){
				MetadataItem item_2201= metadataManager.getMetadataItem(metadata1,2201L); //退回
				MetadataItem item_2202= metadataManager.getMetadataItem(metadata1,2202L); //终止
				List<MetadataItem> itemList=new ArrayList<MetadataItem>();
				if(item_2201!=null)itemList.add(item_2201);
				if(item_2202!=null)itemList.add(item_2202);
				tempBaseMmetadata.addItems(itemList);
			}
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限操作，退回和终止挪到基本操作end

			
			metadata3 = metadataManager.getMetadata(MetadataNameEnum.edoc_node_control_action);
			if(null!=metadata3){
				//tempControlMmetadata.addItems(metadata3.getItems());
				//tempControlMmetadata.removeItem(2228L);
				//tempControlMmetadata.removeItem(6102L);
				tempControlMmetadata.addItems(metadata3.getItems());
				tempControlMmetadata.removeItem(6102L);
			
			}
			
			metadata4 = metadataManager.getMetadata(MetadataNameEnum.edoc_basic_action);
			if(null!=metadata4){
				tempBaseMmetadata.addItems(metadata4.getItems());
				if("edoc_rec_permission_policy".equals(category) || "edoc_qianbao_permission_policy".equals(category)){
					//如果是收文或签报，屏蔽交换类型
					tempBaseMmetadata.removeItem(2301L);					
				}
				//公文去掉,处理后删除
				tempBaseMmetadata.removeItem(2207L);
				//tempBaseMmetadata.removeItem(2214L);
			}
			tempBaseMmetadata.removeItem(6100L);
			tempBaseMmetadata.removeItem(6102L);
			//tempBaseMmetadata.removeItem(2228L);
			
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限高级操作，取消文单修改、当前会签、多级会签、回退、终止 start
			if(isGovVersion){
				tempControlMmetadata.removeItem(2229L);//多级会签
				tempControlMmetadata.removeItem(2201L);//回退
				tempControlMmetadata.removeItem(2202L);//终止
				tempControlMmetadata.removeItem(2211L);//当前会签
			}
			//branches_a8_v350_r_gov  杨帆 修改公文节点权限高级高操作，取消文单修改、当前会签、多级会签、回退、终止end
		}
		tempBaseMmetadata.removeItem(2207L); //处理后删除
		//如果没有安装office控件,屏蔽盖章,稿纸,套红,文单签批策略
		if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("officeOcx")==false)
		{
			tempControlMmetadata.removeItem(2223L);//盖章
			tempControlMmetadata.removeItem(2304L);//套红
			tempControlMmetadata.removeItem(2307L);//稿纸
			tempControlMmetadata.removeItem(2306L);//文单签批
		}
		//阅读权限特殊处理。
		if("yuedu".equals(flowPermName) && ("edoc_send_permission_policy".equals(category)||"edoc_qianbao_permission_policy".equals(category))){
			List<MetadataItem> allowOperations=new ArrayList<MetadataItem>();
			for(MetadataItem metadataItem : tempControlMmetadata.getItems()){
				if(metadataItem.getId().longValue()==2202L    //终止
					||metadataItem.getId().longValue()==2209L //知会
					||metadataItem.getId().longValue()==2305L //传阅 
					||metadataItem.getId().longValue()==2228L //转公告
					||metadataItem.getId().longValue()==2303L //部门归档
					||metadataItem.getId().longValue()==2306L)//文单签批
				{
					allowOperations.add(metadataItem);   
				} 
			}
			tempControlMmetadata.setItems(allowOperations);
		}
		String param = request.getParameter("param");
		
		ModelAndView mav = new ModelAndView("flowperm/permManage/operation_choose");

		if(!"".equals(param) && param.equals("common") || param.equals("advanced")){			
			mav.addObject("metadata", tempControlMmetadata);
		}
		else if(!"".equals(param) && param.equals("basic")){
			mav.addObject("metadata", tempBaseMmetadata);
		}
		return mav;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}

	public EdocElementFlowPermAclManager getEdocElementFlowPermAclManager() {
		return edocElementFlowPermAclManager;
	}

	public void setEdocElementFlowPermAclManager(
			EdocElementFlowPermAclManager edocElementFlowPermAclManager) {
		this.edocElementFlowPermAclManager = edocElementFlowPermAclManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if(first>list.size()){
			first=0;
		}
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
	 * 得到节点权限名称
	 * @param flowPerm
	 * @return
	 */
	private String getNodeName(FlowPerm flowPerm){
		String resources = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		String nodeName = "";
		if(flowPerm != null){
			if(Strings.isNotBlank(flowPerm.getLabel())){
				nodeName =  ResourceBundleUtil.getString(resources,flowPerm.getLabel());
			} else {
				nodeName =  flowPerm.getName();
			}
		}
		return nodeName;
	}
	
	/**
	 * 是否显示权限列表
	 * @return
	 */
	private boolean checkHasPerm()throws Exception{
		User user = CurrentUser.get();
		boolean isEnterVer=((Boolean)(SysFlag.sys_isEnterpriseVer.getFlag()) || (Boolean)(SysFlag.sys_isGovVer.getFlag()));
		if(isEnterVer==true && orgManager.isAccountAdmin(user.getLoginName())){return true;}
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());
		if(isGroupVer && orgManager.isGroupAdmin(user.getLoginName())){return true;}
		return false;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	/**
	 * 首页配置节点权限。
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView showPerom4Portal(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//wangjingjing begin
		String pageView = "flowperm/permManage/showPerm4Portal";
		if(com.seeyon.v3x.doc.util.Constants.isGovVer()){
			pageView = "flowperm/permManage/showPerm4PortalGov";
		}
		ModelAndView modelAndView = new ModelAndView(pageView);
		//wangjingjing end
		User user = CurrentUser.get();
		Long loginAccount = user.getLoginAccount();
		//查询协同节点
		List<FlowPerm> listCol = flowPermManager.getFlowpermsByStatus("col_flow_perm_policy",1, loginAccount);
		modelAndView.addObject("listCol", listCol);
		if(SystemEnvironment.hasPlugin("edoc") && user.isInternal()){
			//发文
			List<FlowPerm> listFa = flowPermManager.getFlowpermsByStatus("edoc_send_permission_policy",1, loginAccount);
			//收文
			List<FlowPerm> listShou = flowPermManager.getFlowpermsByStatus("edoc_rec_permission_policy",1, loginAccount);
			//签报
			List<FlowPerm> listQian = flowPermManager.getFlowpermsByStatus("edoc_qianbao_permission_policy",1, loginAccount);
			
			modelAndView.addObject("listFa", listFa);
			modelAndView.addObject("listShou", listShou);
			modelAndView.addObject("listQian", listQian);
		}
		//wangjingjing 信息报送 插件节点权限 begin
		//是政务版 且 有 信息报送 插件
		if(com.seeyon.v3x.doc.util.Constants.isGovVer() && SystemEnvironment.hasPlugin("govInfoPlugin") && user.isInternal()){
			//信息报送
			List<FlowPerm> listInfo = flowPermManager.getFlowpermsByStatus("info_send_permission_policy",1, loginAccount);
			modelAndView.addObject("listInfo", listInfo);
		}
		//wangjingjing end
		return modelAndView;
	}
}