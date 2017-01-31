package com.seeyon.v3x.collaboration;

import java.util.Locale;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * User: lius Date: 2006-10-20 Time: 11:18:58
 */
public class Constant {
	
	public static enum SendType {
		normal, resend, forward, auto
	}

	public static enum ConfigCategory {
		action_to_col_definition, col_flow_perm_policy
	}
	
	public static enum OperationLogActionType{
		editBody, //修改正文
		transferTemplete, //调用模板
		sendColl, //发送协同
		finishItem, //完成协同工作项
		deleteItem, //删除协同工作项
		takeBackItem, //取回协同工作项
		stepBackItem, //回退协同工作项
		cancelColl, //撤销协同
		stepStop, //终止
		zcdb, //暂存待办协同工作项
		insertPeople, //加签操作
		deletePeople, //减签操作
		inform, //知会
		colAssign, //会签
		forwardColl, //转发
		pigeonhole, //归档
		modifyWorkflow, //督办修改流程
		modifyPolicy, //督办修改节点属性
	}
	
	/**
	 * 流程状态 
	 */
	public static enum flowState
	{
		run, //运行中
		terminate, //终止结束
		cancel, //取消（实际没有用，通过affair的state=2/substate来决定）
		finish, //正常结束
		deleted //被删除(公文：结束后归档，再从档案中删除)
	}
	
	public static enum workManageSetShowContent{
		show,
		hidden
	}
	
	public static enum superviseState{
		supervising,supervised,waitSupervise
	}
	
	public static enum superviseType{
		template,summary,edoc
	}
	
	public static enum suerviseLogType{
		hasten,reply
	}

    /**
     * 新流程与主流程的关联类型 
     */
    public static enum FlowRelateType{
        selfExistent, //彼此独立
        continueByNewflowEnd //新流程结束后主流程才可继续
    }
    
    public static enum NewflowType{
        main, // 主流程
        child//子流程
    }
    
    public static enum BranchDepartmentStatus{
    	includeChild,  //包含子部门
    	excludeChild   //不包含子部门
    }
    //成发集团项目 程炯 2012-8-29 协同密级
    public static enum SecretLevel{
    	none,  //无
    	noSecret,  //非密
    	secret, //秘密
    	secretMore, //机密
    	TopSecret //绝密
    }
    //end
    public static enum FormVouch{
    	vouchDefault("0"),
    	vouchPass("1"),
    	vouchBack("2");
    	
    	String key;
    	 FormVouch(String key){
    		 this.key = key;
    	 }
    	 public String getKey(){
    		 return this.key;
    	 }
    }
    public static enum ColSummaryVouch{
    	vouchDefault(0),
    	vouchPass(1),
    	vouchBack(2);
    	
    	 int key;
    	 ColSummaryVouch(int key){
    		 this.key = key;
    	 }
    	 public int getKey(){
    		 return this.key;
    	 }
    }
	public static final String resource_baseName = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
	public static final String resource_common_baseName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	public static final String resource_main_baseName = "com.seeyon.v3x.main.resources.i18n.MainResources";
	public static final String resource_sysMgr_baseName = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
	public static final String resource_organization_baseName = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
	
	/**
	 * 获取协同资源文件(CollaborationResource)中对应的国际化值
	 */
	public static String getString(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, key, parameters);
	}
	/**
	 * 获取公共(SeeyonCommonResources)资源文件中对应的国际化值
	 */
	public static String getCommonString(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_common_baseName, key, parameters);
	}
	/**
	 * 获取主(MainResources)资源文件中对应的国际化值
	 */
	public static String getMainString(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_main_baseName, key, parameters);
	}
	
	public static String getOrgString(String key, Object... parameters){
		return ResourceBundleUtil.getString(resource_organization_baseName, key, parameters);
	}
	
	public static String getSysString(String key,Object... parameters){
		return ResourceBundleUtil.getString(resource_sysMgr_baseName, key, parameters);
	}

	public static String getString4CurrentUser(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, key, parameters);
	}

	public static String getString(String key, Locale locale,
			Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, locale, key,
				parameters);
	}
}
