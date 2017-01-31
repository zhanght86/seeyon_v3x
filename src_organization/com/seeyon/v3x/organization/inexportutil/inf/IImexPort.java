package com.seeyon.v3x.organization.inexportutil.inf;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.services.OrganizationServices;

/**
 * 
 * @author kyt
 *
 */
public interface IImexPort {
	public static String [] booleanchnvalue={
		"是.","否","启用.","未启用"
		};
	public static String [] booleanengvalue={
		"yes.","no","Y.","N"
		};	
	
	public static final String RESULT_ADD="add";
	public static final String RESULT_UPDATE="update";
	public static final String RESULT_IGNORE="ignore";
	public static final String RESULT_ERROR="error";
	
	public final static String NULL_ENTITY_TAG="_null_entiy_tag";
	
	/**
	 * 匹配中文字
	 * @return
	 * @throws Exception
	 */
	public List matchLanguagefield(List statrlst,HttpServletRequest request) throws Exception;
	/**
	 * 校验vo的值
	 * @param volst
	 * @throws Exception
	 */
	public void validateData(List volst) throws Exception;
	/**
	 * 生成可执行的插入sql语句
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public List creatInsertSql(List volst) throws Exception;
	
	/**
	 * 生成可执行的插入sql语句
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public List creatUpdateSql(List volst) throws Exception;
	/**
	 * 得到相关的实体bean
	 * @return
	 */
	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception;
	/**
	 * 上传数据与数据库中的数据进行比较，分别分出  数据库中有的数据列表  和没有的数据列表
	 * @param od
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public Map devVO(OrgManagerDirect od,List volst) throws Exception;
//	/**
//	 * 设置外键关联的字段值
//	 * @param od
//	 * @param volst
//	 * @return
//	 * @throws Exception
//	 */
//	public List setForeignKey(OrgManagerDirect od,List volst)throws Exception;
	
	//tanglh
	public Map importOrg(OrganizationServices  organizationServices
			,MetadataManager metadataManager
			,List<List<String>> fromList
			,V3xOrgAccount voa,boolean ignoreWhenUpdate
			    )throws Exception;
	
	public void setLocale(Locale val);
}//end class
