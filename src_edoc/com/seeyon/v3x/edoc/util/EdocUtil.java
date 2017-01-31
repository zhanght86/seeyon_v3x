package com.seeyon.v3x.edoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormAcl;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class EdocUtil {
	
	private static final Log log = LogFactory.getLog(EdocUtil.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str="sdfs&&dd||dd";
		String sz[]=str.split("\\|");
		System.out.println("vlen="+sz.length);
	}
	
	public static ApplicationCategoryEnum getAppCategoryByEdocType(int edocType)
	{		
		if(edocType==EdocEnum.edocType.sendEdoc.ordinal())
		{
			return ApplicationCategoryEnum.edocSend;
		}
		else if(edocType==EdocEnum.edocType.recEdoc.ordinal())
		{
			return ApplicationCategoryEnum.edocRec;
		}
		else if(edocType==EdocEnum.edocType.signReport.ordinal())
		{
			return ApplicationCategoryEnum.edocSign;
		} 
		return ApplicationCategoryEnum.edoc;
	}
	
	public static String getEdocTypeName(int edocType)
	{
		String keys="menu.edoc.sendManager";
		if(edocType==EdocEnum.edocType.recEdoc.ordinal())
		{
			keys="menu.edoc.recManager";
		}
		else if(edocType==EdocEnum.edocType.signReport.ordinal())
		{
			keys="menu.edoc.signManager";
		} 
		return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources",keys);		
	}
	public static String getEdocStateName(int state)
	{
		String keys="edoc.workitem.state.done";
		if(state==StateEnum.col_pending.getKey())
		{
			keys="edoc.workitem.state.pending";
		}
		else if(state==StateEnum.col_sent.getKey())
		{
			keys="edoc.workitem.state.sended";
		} 
		else if(state==StateEnum.col_waitSend.getKey())
		{
			keys="edoc.workitem.state.darft";
		}
		return ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",keys);		
	}
	public static String getEdocLocationName(int edocType,int state)
	{
		return getEdocTypeName(edocType)+" - "+getEdocStateName(state);
	}
	
	public static int getEdocTypeByAppCategory(int appEnum)
	{
		if(appEnum==ApplicationCategoryEnum.edocSend.getKey())
		{
			return EdocEnum.edocType.sendEdoc.ordinal();
		}
		else if(appEnum==ApplicationCategoryEnum.edocRec.getKey())
		{
			return EdocEnum.edocType.recEdoc.ordinal();
		}
		else
		{
			return EdocEnum.edocType.signReport.ordinal();
		}
	}
	
	public static String getEdocCategroryPendingUrl(int appEnum)
	{
		String url="";
		if(MenuFunction.hasMenu(getMenuIdByApp(appEnum)))
		{
			int edocType=getEdocTypeByAppCategory(appEnum);		
			url="/edocController.do?method=edocFrame&from=listPending&controller=edocController.do&edocType="+edocType;
		}
		else
		{//没有收发文菜单
			url="";
		}
		return url;
	}
	public static Long getMenuIdByApp(int appEnum)
	{
		Long menuId=-1L;
		if(appEnum==ApplicationCategoryEnum.edocSend.getKey())
		{
			menuId=201L;
		}
		else if(appEnum==ApplicationCategoryEnum.edocRec.getKey() || appEnum==ApplicationCategoryEnum.edocRegister.getKey())
		{
			menuId=202L;
		}
		else if(appEnum==ApplicationCategoryEnum.edocSign.getKey())
		{
			menuId=206L;
		}
		else if(appEnum==ApplicationCategoryEnum.exSend.getKey() || appEnum==ApplicationCategoryEnum.exSign.getKey())
		{//公文交换菜单
			menuId=205L;
		}
		return menuId;
	}
	public static String getEdocCategroryUrl(String from,int appEnum)
	{
		String url="";
		int edocType=getEdocTypeByAppCategory(appEnum);
		url="/edocController.do?method=edocFrame&from="+from+"&controller=edocController.do&edocType="+edocType;
		return url;
	}
	
	/*
	public static String getEdocTypeLocalLanguage(int appType)
	{
		return ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "application."+appType+".label");	
	}
	
	public static String getEdocTypeLocalLanguageByEdocType(int edocType)	
	{
		int appType=getAppCategoryByEdocType(edocType).getKey();
		return getEdocTypeLocalLanguage(appType);	
	}
	*/
	
	public static MetadataNameEnum getEdocMetadataNameEnum(int edocType)
	{
		if(edocType==EdocEnum.edocType.sendEdoc.ordinal())
		{
			return MetadataNameEnum.edoc_send_permission_policy;
		}
		else if(edocType==EdocEnum.edocType.recEdoc.ordinal())
		{
			return MetadataNameEnum.edoc_rec_permission_policy;
		}
		else
		{
			return MetadataNameEnum.edoc_qianbao_permission_policy;
		}
	}
	
	public static MetadataNameEnum getEdocMetadataNameEnumByApp(int appType)
	{
		int edocType=getEdocTypeByAppCategory(appType);
		return getEdocMetadataNameEnum(edocType);
	}
	
	public static String getSendFlowpermNameByEdocType(int edocType)
	{
		if(edocType==EdocEnum.edocType.sendEdoc.ordinal())
		{
			return "niwen";
		}
		else if(edocType==EdocEnum.edocType.recEdoc.ordinal())
		{
			return "dengji";
		}
		else
		{
			return "niwen";
		}
	}
	
	/**
	 * 获得前端传入的公文文号信息
	 * @param s [id|文号|当前号|标志位]
	 * @return
	 */
	public static String[] parseDocMark(String s) {
		try {
			return s.split("\\|");
			/*StringTokenizer st = new StringTokenizer(s, "|");
			Vector<String> vector = new Vector<String>();
			while (st.hasMoreTokens()) {				
				vector.add(st.nextToken());
			}
			String[] arr = new String[vector.size()];
			vector.copyInto(arr);
			return arr;
			*/
		}
		catch (Exception e) {
			log.error("解析公文文号时出现错误。" + e.toString());
			return null;
		}
	}
	
	public static String getOfficeFileExt(String ftypt)
	{
		String fe=".htm";
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(ftypt))
		{
			fe=".xls";
		}
		else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(ftypt))
		{
			fe=".doc";
		}
		else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(ftypt))
		{
			fe=".et";
		}
		else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(ftypt))
		{
			fe=".wps";
		}
		return fe;
	}	
	
	
	public static int getEdocTypeByTemplateType(int templateType)
	{
		if(templateType==TempleteCategory.TYPE.edoc_rec.ordinal())
		{
			return EdocEnum.edocType.recEdoc.ordinal();
		}
		else if(templateType==TempleteCategory.TYPE.edoc_send.ordinal())
		{
			return EdocEnum.edocType.sendEdoc.ordinal();
		}
		else if(templateType==TempleteCategory.TYPE.sginReport.ordinal())
		{
			return EdocEnum.edocType.signReport.ordinal();
		}		
		return EdocEnum.edocType.recEdoc.ordinal();
	}
	/**
	 * edoc(4), // 公文
	 * edocSend(19), //发文
	 * edocRec(20),	//收文
	 * edocSign(21),	//签报	
	 * exSend(22), //待发送公文
	 * exSign(23), //待签收公文
	 * edocRegister(24), //待登记公文
	 * exchange(16), // 交换
	 * @param key
	 * @return
	 */
	public static boolean isEdocCheckByAppKey(int key){
		if(ApplicationCategoryEnum.edoc.getKey() == key
				||ApplicationCategoryEnum.edocRec.getKey() ==key
				||ApplicationCategoryEnum.edocRegister.getKey() == key
				||ApplicationCategoryEnum.edocSend.getKey() == key
				||ApplicationCategoryEnum.edocSign.getKey()==key
				||ApplicationCategoryEnum.exSend.getKey() == key
				||ApplicationCategoryEnum.exSign.getKey() == key
				||ApplicationCategoryEnum.exchange.getKey() == key){
			
			return true;
		
		}else{
			return false;
		}
	}
	public static List<ApplicationCategoryEnum>  getAllEdocApplicationCategoryEnum(){
		List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
		apps.add(ApplicationCategoryEnum.edoc);
		apps.add(ApplicationCategoryEnum.edocRec);
		apps.add(ApplicationCategoryEnum.edocRegister);
		apps.add(ApplicationCategoryEnum.edocSend);
		apps.add(ApplicationCategoryEnum.edocSign);
		apps.add(ApplicationCategoryEnum.exSend);
		apps.add(ApplicationCategoryEnum.exSign);
		apps.add(ApplicationCategoryEnum.exchange);
		return apps;
	}
	public static List<Integer>  getAllEdocApplicationCategoryEnumKey(){
		List<Integer> keys = new ArrayList<Integer>();
		keys.add(ApplicationCategoryEnum.edoc.key());
		keys.add(ApplicationCategoryEnum.edocRec.key());
		keys.add(ApplicationCategoryEnum.edocRegister.key());
		keys.add(ApplicationCategoryEnum.edocSend.key());
		keys.add(ApplicationCategoryEnum.edocSign.key());
		keys.add(ApplicationCategoryEnum.exSend.key());
		keys.add(ApplicationCategoryEnum.exSign.key());
		keys.add(ApplicationCategoryEnum.exchange.key());
		keys.add(ApplicationCategoryEnum.info.key());
		return keys;
	}
	
	/**
	 * 将公文单授权部分的属性设置到公文单中
	 * @param list
	 * @param domainId
	 * @return
	 */
	public static List<EdocForm> convertExtendInfo2EdocForm(List<EdocForm> list,Long domainId){
		if(list == null ) return null;
		if(domainId == null) return list;
		
		for(EdocForm ef : list){
			Set<EdocFormExtendInfo> set = ef.getEdocFormExtendInfo();
			boolean hasInfo = false;
			if(set !=null){
				for(EdocFormExtendInfo info :set){
					if(info.getAccountId().longValue() == domainId){
						ef.setStatus(info.getStatus());
						ef.setIsDefault(info.getIsDefault());
						ef.setStatusId(String.valueOf(info.getId()));
						ef.setWebOpinionSet(info.getOptionFormatSet());
						if(ef.getDomainId().equals(info.getAccountId())){
							ef.setIsOuterAcl(false);
						}else{
							ef.setIsOuterAcl(true);
						}
						hasInfo = true;
						break;
					}
				}
			}
			
			if(!hasInfo){
				ef.setStatus(EdocForm.C_iStatus_Draft); //停用
				ef.setIsDefault(false);	 //非默认公文单 
				ef.setStatusId("");
				ef.setIsOuterAcl(true);
			}
		}
		return list;
	}

}
