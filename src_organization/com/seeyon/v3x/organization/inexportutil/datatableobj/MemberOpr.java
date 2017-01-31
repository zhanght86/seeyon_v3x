package com.seeyon.v3x.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.inexportutil.DataObject;
import com.seeyon.v3x.organization.inexportutil.DataStringUtil;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.ResultObject;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.inexportutil.msg.MsgContants;
import com.seeyon.v3x.organization.inexportutil.pojo.ImpExpMember;
import com.seeyon.v3x.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.util.Datetimes;
/**
 * 
 * @author kyt
 *
 */
public class MemberOpr extends AbstractImpOpr implements IImexPort {
	private static final String LOGINNAME = "loginname";
	private static final Log log = LogFactory.getLog(MemberOpr.class);
	// 导入人员时识别的性别集合（只要为男、M、MALE都视为男性）
	private static Set<String> males = new HashSet<String>();
	private static Set<String> females = new HashSet<String>();
	static{
		males.add("男");females.add("女");
		males.add("M");females.add("F");
		males.add("MALE");females.add("FEMALE");
	}
	
	public String[] getFixedField(HttpServletRequest request){
		Locale locale = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String state_Enabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.unuse");
		String member_primaryLanguange_zh_CN = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh_CN");
		String member_primaryLanguange_zh = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh");
		String member_primaryLanguange_en = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.en");
		String member_type_inner = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.inner");
		String member_type_out = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.out");
		String member_name = ResourceBundleUtil.getString(resource, locale, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, locale, "org.member_form.loginName.label");
		String member_password = ResourceBundleUtil.getString(resource, locale, "org.member_form.password.label");
		String member_primaryLanguange = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange");
		String member_kind = ResourceBundleUtil.getString(resource, locale, "org.member_form.kind");
		String member_state = ResourceBundleUtil.getString(resource, locale, "org.state.lable");
		String member_code = ResourceBundleUtil.getString(resource, locale, "org.member_form.code");
		String member_sortId = ResourceBundleUtil.getString(resource, locale, "org.member_form.sort");
		String member_deptName = ResourceBundleUtil.getString(resource, locale, "org.member_form.deptName.label");
		String member_primaryPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryPost.label");
		String member_secondPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.secondPost.label");
		String member_levelName = ResourceBundleUtil.getString(resource, locale, "org.member_form.levelName.label");
		String member_type = ResourceBundleUtil.getString(resource, locale, "org.member_form.type");
		String member_memberState = ResourceBundleUtil.getString(resource, locale, "org.member_form.member.state");
		String member_roles = ResourceBundleUtil.getString(resource, locale, "org.member_form.roles");
		String member_tel = ResourceBundleUtil.getString(resource, locale, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, locale, "org.member_form.account");
		String member_description = ResourceBundleUtil.getString(resource, locale, "org.member_form.description");
		String member_list = ResourceBundleUtil.getString(resource, locale, "org.member_form.list");		
		String company_createDate = ResourceBundleUtil.getString(resource, locale, "org.account_form.createdtime.label");
		String company_updateDate = ResourceBundleUtil.getString(resource, locale, "org.account_form.updatetime.label");

		String islogin = ResourceBundleUtil.getString(resource, locale, "org.member_form.islogin.label");
		String isvirtual = ResourceBundleUtil.getString(resource, locale, "org.member_form.isvirtual.label");		
		String isassign = ResourceBundleUtil.getString(resource, locale, "org.member_form.isassigned.label");
		String isadmin = ResourceBundleUtil.getString(resource, locale, "org.member_form.isAdmin.label");
		String isinternal = ResourceBundleUtil.getString(resource, locale, "org.member_form.isinternal.label");
		String agentname = ResourceBundleUtil.getString(resource, locale, "org.member_form.agentname.label");
		String agentid = ResourceBundleUtil.getString(resource, locale, "org.member_form.agentid.label");
		String agenttime = ResourceBundleUtil.getString(resource, locale, "org.member_form.agenttime.label");
		
		String []fieldname={
				"loginname:"+member_loginName+":loginname",
			"name:"+member_name+":name",
			"code:"+member_code+":code",
			"primary_languange:"+member_primaryLanguange+":primary",
			"is_loginable:"+islogin+":loginable",
			"is_virtual:"+isvirtual+":virtual",
			"is_assigned:"+isassign+":assigned",
			"is_admin:"+isadmin+":admin",
			"sort_id:"+member_sortId+":sort",
			"state:"+member_state+":state",
			"type:"+member_type+":type",
			"is_internal:"+isinternal+":internal",
			"enabled:"+state_Enabled+":enabled",
			"create_time:"+company_createDate+":create",
			"update_time:"+company_updateDate+":update",
			"tel_number:"+member_tel+":tel",
			"agent_id:"+agentname+":agentid",
			"agent_to_id:"+agentid+":agenttoid",
			"agent_time:"+agenttime+":agenttime",
			"description:"+member_description+":cription",
			"org_department_id:"+member_deptName+":departmentid",
			"org_level_id:"+member_levelName+":levelid",
			"org_account_id:"+member_account+":accountid",
			"org_post_id:"+member_primaryPost+":postid"

	};
		return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_member VALUES ('-9152317794285632979', '林海', '', 
	 * 'zh_CN', '1', '65', '1', '1', '1', '1', '0', '1', '0', '2007-08-01 16:45:28', 
	 * '2007-08-01 16:45:28', '', '-7636518349712111434', '8108742633208761789', 
	 * '-5362937964371884064', '98936634471236322', '-1', '-1', null, '','', '0');
	 */
	public List creatInsertSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgMember voa = (V3xOrgMember)volst.get(i);
			if(voa==null)
				continue;
			if(!StringUtils.hasText(voa.getName()))//voa.getName()必填
				continue;
			
			StringBuffer sb = getInsertHeadStringbuffer();
			Long id = UUIDLong.longUUID();
			voa.setId(id);
			sb.append("'"+String.valueOf(id)+"',");	//id
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+(voa.getCode()==null?"":voa.getCode())+"',");							//code
			sb.append("'"+(voa.getPrimaryLanguange()==null?"":voa.getPrimaryLanguange())+"',");					//secondname
			if(voa.getEnabled()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//enable
			}
			if(voa.getSortId().intValue() == 0){
				sb.append("'"+i+"',");						//sortid
			}else{
				sb.append("'"+voa.getSortId()+"',");						//sortid
			}
			sb.append("'"+voa.getState()+"',");						//state
			sb.append("'"+voa.getType()+"',");					//type
			if(voa.getIsInternal()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//is_internal
			}
			if(voa.getIsLoginable()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//is_loginable
			}
			if(voa.getIsVirtual()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//is_virtual
			}
			if(voa.getIsAssigned()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//is_assigned
			}
			if(voa.getIsAdmin()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//is_admin
			}
			
			sb.append(DataStringUtil.createDateTimeString(null, voa.getCreateTime(), null));//tanglh
					//"TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append(DataStringUtil.createDateTimeString(",", voa.getUpdateTime(), null));//
					//"TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append(",'"+(voa.getDescription()==null?"":voa.getDescription())+"',");					//description
			sb.append("'"+voa.getOrgDepartmentId()+"',");						//org_department_id
			sb.append("'"+voa.getOrgLevelId()+"',");					//org_level_id
			sb.append("'"+voa.getOrgAccountId()+"',");						//org_account_id
			sb.append("'"+voa.getOrgPostId()+"',");					//org_post_id
			sb.append("'"+voa.getAgentId()+"',");						//agent_id
			sb.append("'"+voa.getAgentToId()+"',");					//agent_to_id
			sb.append(voa.getAgentTime()==null?" null ":DataStringUtil.createDateTimeString(null, voa.getAgentTime(), null));//tanglh
					//(voa.getAgentTime()==null? null +",":"'"+voa.getDescription()+"',"));					//agent_time
			sb.append(",'"+(voa.getTelNumber()==null?"":voa.getTelNumber())+"',");					//tel_number
			
			if(voa.getIsDeleted())
				sb.append("'"+1+"',");						//isdeleted
			else sb.append("'"+0+"',");
			sb.append("'"+voa.getEmailAddress()+"'");					//email_address
			sb.append(")");
			returnlst.add(sb);
		}
		return returnlst;
	}
	
	StringBuffer getInsertHeadStringbuffer(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(" INSERT INTO v3x_org_member (  ");
		sb.append("id,name,code,primary_languange,enabled,sort_id,state,type,is_internal,is_loginable,");
		sb.append("is_virtual,is_assigned,is_admin,create_time,update_time,description,org_department_id,org_level_id,");
		sb.append("org_account_id,org_post_id,agent_id,agent_to_id,agent_time,tel_number,is_deleted,email_address");
		sb.append(") VALUES ( ");
		
		return sb;
	}

	public List matchLanguagefield(List statrlst,HttpServletRequest request) throws Exception {
		DataObject logindao = new DataObject();
		logindao.setFieldName(LOGINNAME);
		logindao.setLength(100);
		logindao.setTableName(((DataObject)statrlst.get(0)).getTableName());
		statrlst.add(0, logindao);
		for(int i=0;i<statrlst.size();i++){
			DataObject dao = (DataObject)statrlst.get(i);
			boolean flag = false;
			String[] fieldname = getFixedField(request);
			for(int j=0;j<fieldname.length;j++){
				String field[] = fieldname[j].split(":");
				if(dao.getFieldName().equalsIgnoreCase(field[0])){
					dao.setMatchCHNName(field[1]);
					dao.setMatchENGName(field[2]);
					flag = true;
				}
			}
			if(!flag){
				dao.setMatchCHNName("");
			}
		}
		return statrlst;
	}

	public void validateData(List volst) throws Exception {
		for(int i=0;i<volst.size();i++){
			V3xOrgMember voa = (V3xOrgMember)volst.get(i);
			StringBuffer sb = new StringBuffer();
			
			if(voa.getName()!= null && !"".equals(voa.getName())&&"null".equals(voa.getName())){
				if(voa.getName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 名称 ,长度太长!");
			}
			if(voa.getPrimaryLanguange()!= null && !"".equals(voa.getPrimaryLanguange())&&"null".equals(voa.getPrimaryLanguange())){
				if(voa.getPrimaryLanguange().length()>20)
					throw new Exception("数据 " +voa.getName()+ " 的 首选语言 ,长度太长!");
			}			
			if(voa.getCode()!= null && !"".equals(voa.getCode())&&"null".equals(voa.getCode())){
				if(voa.getCode().length()>30)
					throw new Exception("数据 " +voa.getName()+ " 的 代码 ,长度太长!");
			}
			
			if(voa.getAgentTime()!= null){
				if(voa.getAgentTime().toString().length()>19)
					throw new Exception("数据 " +voa.getName()+ " 的 代理时间 ,长度太长!");
			}
			
			if(voa.getTelNumber()!= null){
				if(voa.getTelNumber().length()>100)
					throw new Exception("数据 " +voa.getName()+ " 的 电话号码 ,长度太长!");
			}	
			
			if(voa.getEmailAddress()!= null && !"".equals(voa.getEmailAddress())&&"null".equals(voa.getEmailAddress())){
				if(voa.getEmailAddress().length()>100)
					throw new Exception("数据 " +voa.getName()+ " 的 电子邮件 ,长度太长!");
			}	

			
			if(voa.getCreateTime()!= null ){
				if(voa.getCreateTime().toString().length()>19)
					throw new Exception("数据 " +voa.getName()+ " 的 创建日期 ,格式为：yyyy-mm-dd hh:mm:ss");
			}	

			if(voa.getUpdateTime()!= null ){
				if(voa.getUpdateTime().toString().length()>19)
					throw new Exception("数据 " +voa.getName()+ " 的 修改日期 ,格式为：yyyy-mm-dd hh:mm:ss");
			}
			
		}
	}

	public V3xOrgEntity getVO() {
		// TODO Auto-generated method stub
		return new V3xOrgMember();
	}
	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();	
		List<V3xOrgDepartment> deptlst = od.getAllDepartments(accountid, false);
		List<V3xOrgLevel> levellst = od.getAllLevels(accountid, false);
		List<V3xOrgPost> postlst = od.getAllPosts(accountid, false);
		for(int i = 2 ; i < accountList.size() ; i++){
			//log.info("accountList i="+i);//tanglh
			V3xOrgMember voa = new V3xOrgMember();
			List valuelst = accountList.get(i);//tanglh
			//log.info("valuelst.size()"+valuelst.size());
			Method med [] =  voa.getClass().getMethods();
			if(DataUtil.isNotNullValue(valuelst)){
			for(int j=0;j<med.length;j++){
				Method mdd = med [j];
				if(mdd.getName().indexOf("set") != -1){
					//log.info("mdd.getName()="+mdd.getName());
					for(int m=0;m<volst.size();m++){
						DataObject dao = (DataObject)volst.get(m);
						if(mdd.getName().toLowerCase().indexOf(DataUtil.submark(dao.getFieldName()).toLowerCase()) == 3){
							if(dao.getColumnnum() != -1){
								Class cl[] = mdd.getParameterTypes();
								if(cl[0].getName().equals("java.lang.Integer")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{new Integer(valuelst.get(dao.getColumnnum()).toString())});
									}else{
										mdd.invoke(voa, new Object[]{new Integer(0)});
									}
								}else if(cl[0].getName().equals("java.util.Date")){
									if("".equals(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Datetimes.getTodayFirstTime()});
									}else if(valuelst.get(dao.getColumnnum()).toString().trim().length() == 10){
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString().trim()+" 00:00:00")});
									}else{
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString())});
									}
								}else if(cl[0].getName().equals("java.lang.Boolean")){
									mdd.invoke(voa, new Object[]{Boolean.valueOf(valuelst.get(dao.getColumnnum()).toString())});
								}else if(cl[0].getName().equals("java.lang.Long")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{new Long(valuelst.get(dao.getColumnnum()).toString())});
									}else if(!DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										if(DataUtil.submark(dao.getFieldName()).toLowerCase().indexOf("departmentid") != -1){
											//log.info("departmentid");
											mdd.invoke(voa, new Object[]{getCorrectDept(deptlst, valuelst.get(dao.getColumnnum()).toString())});
										}else if(DataUtil.submark(dao.getFieldName()).toLowerCase().indexOf("levelid") != -1){
											//log.info("levelid");
											mdd.invoke(voa, new Object[]{getCorrectLevel(levellst, valuelst.get(dao.getColumnnum()).toString())});
										}else if(DataUtil.submark(dao.getFieldName()).toLowerCase().indexOf("postid") != -1){
											//log.info("postid");
											mdd.invoke(voa, new Object[]{getCorrectPost(postlst, valuelst.get(dao.getColumnnum()).toString())});
										}else{
											mdd.invoke(voa, new Object[]{new Long(0)});
										}
									}else{
										mdd.invoke(voa, new Object[]{new Long(0)});
									}			
								}else if(cl[0].getName().equals("int")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Integer.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{0});
									}			
								}else if(cl[0].getName().equals("java.lang.Byte")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Byte.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{Byte.valueOf("1")});
									}			
								}else if(cl[0].getName().equals("com.seeyon.v3x.organization.domain.V3xOrgAccount")){
									V3xOrgAccount vox = new V3xOrgAccount();
									vox.setName(valuelst.get(dao.getColumnnum()).toString());
									mdd.invoke(voa, new Object[]{vox});		
								}else{
									try{
										mdd.invoke(voa, new Object[]{valuelst.get(dao.getColumnnum())});
									}catch(Exception e){
										log.info("error",e);//tanglh
									}
								}
							}
						}
					}
				}
			}
			//log.info("voa.getId()"+voa.getId());
			returnlst.add(voa);
			}
		}
		return returnlst;
	}
	public List creatUpdateSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgMember voa = (V3xOrgMember)volst.get(i);
			if(voa==null)
				continue;
			if(!StringUtils.hasText(voa.getName()))//voa.getName()必填
				continue;
			//if()
			
			//log.info("voa.getId()="+voa.getId());
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_member SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getCode()==null?"":"   , code='"+voa.getCode()+"'");							//code
			sb.append(voa.getPrimaryLanguange()==null?"":"  , primary_languange='"+voa.getPrimaryLanguange()+"'");					//secondname
			if(voa.getEnabled()){
				sb.append("  , enabled='"+1+"'");
			}else{
				sb.append(" , enabled='"+0+"'");									//enable
			}
			sb.append(voa.getSortId()==null?"":" ,sort_id='"+voa.getSortId()+"'");						//sortid
			sb.append(" ,state='"+voa.getState()+"'");					//levelscope
			sb.append(" ,type='"+voa.getType()+"'");					//levelscope
			if(voa.getIsInternal())
				sb.append(" ,is_internal='"+1+"'");						//isroot
			else sb.append(" , is_internal='"+0+"'");
			if(voa.getIsLoginable())
				sb.append("  , is_loginable='"+1+"'");						//isroot
			else sb.append("  ,  is_loginable='"+0+"'");
			if(voa.getIsVirtual())
				sb.append("  , is_virtual='"+1+"'");						//isroot
			else sb.append("   ,  is_virtual='"+0+"'");
			if(voa.getIsAssigned())
				sb.append(" ,is_assigned='"+1+"'");						//isroot
			else sb.append("  , is_assigned='"+0+"'");
			if(voa.getIsAdmin())
				sb.append("  , is_admin='"+1+"'");						//isroot
			else sb.append("  ,  is_admin='"+0+"'");
			sb.append(voa.getCreateTime()==null?" ":DataStringUtil.createDateTimeString(
					"  ,create_time=", voa.getCreateTime(), null));//tanglh
					//voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
			sb.append(voa.getUpdateTime()==null?" ":DataStringUtil.createDateTimeString(
					"  ,update_time=", voa.getUpdateTime(), null));//tanglh
					//voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
			sb.append(voa.getDescription()==null?"":"  ,description='"+voa.getDescription()+"'");					//description
			
			
			sb.append(voa.getOrgDepartmentId()==null?"":"  ,org_department_id='"+voa.getOrgDepartmentId()+"'");							//alias
			sb.append(voa.getOrgLevelId()==null?"":"  ,org_level_id='"+voa.getOrgLevelId()+"'");						//shortname
			sb.append(voa.getOrgAccountId()==null?"":"   ,org_account_id='"+voa.getOrgAccountId()+"'");				//groupshortname
			sb.append(voa.getOrgPostId()==null?"":"   ,org_post_id='"+voa.getOrgPostId()+"'");						//adminname
			sb.append("  ,agent_id='"+voa.getAgentId()+"'");						//adminid
			sb.append("  , agent_to_id='"+voa.getAgentToId()+"'");					//adminemail
			sb.append(voa.getAgentTime()==null?" ":DataStringUtil.createDateTimeString(
					"   ,agent_time=", voa.getAgentTime(), null));//tanglh
					//voa.getAgentTime()==null?"":" AND agent_time=TO_TIMESTAMP('"+voa.getAgentTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");
			sb.append(voa.getTelNumber()==null?"":" AND tel_number='"+voa.getTelNumber()+"'");//superior
			if(voa.getIsDeleted())
				sb.append("  ,is_deleted='"+1+"'");						//isdeleted
			else sb.append("  ,is_deleted='"+0+"'");
			sb.append(voa.getEmailAddress()==null?"":"  ,EMAIL_ADDRESS='"+voa.getEmailAddress()+"'");	
			sb.append("   where id='"+voa.getId()+"'");
			returnlst.add(sb);
		}
		return returnlst;
	}
	private V3xOrgMember doRemove(V3xOrgMember voa,List inList){
		for(int j=0;j<inList.size();j++){
			V3xOrgMember v3oavo = (V3xOrgMember)inList.get(j);
			if(v3oavo.getName().equals(voa.getName())){
				return v3oavo;
			}
		}
		return null;
	}
	public Map devVO(OrgManagerDirect od,List volst) throws Exception{
		List v3xorgaccountvolst = od.getAllMembers((
				(V3xOrgMember)volst.get(0)).getOrgAccountId(),false);
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		//这段有时间再改进
		
		int i=0;
		V3xOrgMember ftempobj;
		while (i<newlst.size()){//在这里判断登录名
			V3xOrgMember voa = (V3xOrgMember)newlst.get(i);
			ftempobj=doRemove(voa,v3xorgaccountvolst);
			
			if (ftempobj!=null){				
				duplst.add(ftempobj);
				newlst.remove(i);
			}else
			   i++;
		}
		
		Map mp = new HashMap();
		//重复的
		mp.put("dup", duplst);
		//剩下的
		mp.put("new", newlst);
		return mp;
	}	
	
	private Long getCorrectDept(List<V3xOrgDepartment> deptlst,String name){
		if(name == null) name="";
		for(V3xOrgDepartment vod : deptlst){
			if(vod.getName().equals(name.trim())){
				return vod.getId();
			}
		}
		return new Long(0);
	}
	private Long getCorrectLevel(List<V3xOrgLevel> levellst,String name){
		if(name == null) name="";
		for(V3xOrgLevel vod : levellst){
			if(vod.getName().equals(name.trim())){
				return vod.getId();
			}
		}
		return new Long(0);
	}
	private Long getCorrectPost(List<V3xOrgPost> postlst,String name){
		if(name == null) name="";
		for(V3xOrgPost vod : postlst){
			if(vod.getName().equals(name.trim())){
				return vod.getId();
			}
		}
		return new Long(0);
	}	
	
	protected String getAccountName(ImpExpPojo pojo){
		ImpExpMember p=(ImpExpMember)pojo;
		return p.getAccountName();
	}	
	
	protected ImpExpPojo transToPojo(List<String> org)throws Exception{
		ImpExpMember  iep=new ImpExpMember();
		log.info("org.size()="+org.size());
		
		if(org.size()<7){
			throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_FILEDATA));
			//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_FILEDATA)
		}
		
		if(!StringUtils.hasText(
				(String)org.get(0)))
			throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_MEMBERNAME));
		//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_MEMBERNAME)
		iep.setName(
				catchNoCammerString(org.get(0).trim()));//tanglh ","
		log.info(iep.getName());
		
		if(!StringUtils.hasText(
				(String)org.get(1)))
			throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_LOGINNAME));
		//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_LOGINNAME)
		iep.setLoginName(
				catchNoCammerString(org.get(1).trim()));
		
		if(!StringUtils.hasText(
				(String)org.get(2))){
			//throw new Exception("must have code");
			/*iep.setCode(
					catchNoCammerString(iep.getLoginName()));*/	//tanglh
			iep.setCode("NOCODE");
		}else
		    iep.setCode(
		    		catchNoCammerString(org.get(2).trim()));		//tanglh
		
		if(!StringUtils.hasText(
				(String)org.get(3)))
			throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT));
		   //this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT)
		iep.setAccountName(org.get(3).trim());
		
		if(!StringUtils.hasText(
				(String)org.get(3))){
			//throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_DEP));
			iep.setDept(IImexPort.NULL_ENTITY_TAG);
		}else
			iep.setDept(org.get(4).trim());
		
		if(!StringUtils.hasText(
				(String)org.get(5))){
			//throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_PPOST));
			iep.setPpost(IImexPort.NULL_ENTITY_TAG);
		}else
		 iep.setPpost(org.get(5).trim());
		
		if(!StringUtils.hasText(
				(String)org.get(6))){
			//throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_LEV));
			iep.setLevel(IImexPort.NULL_ENTITY_TAG);
		}else
		   iep.setLevel(org.get(6).trim());
		
		try{
			if(StringUtils.hasText(
					(String)org.get(7)))
				iep.setTelNumber(org.get(7).trim().replace(",",""));//
		}catch(Exception e){
			
		}
		
		try{
			if(StringUtils.hasText(
					(String)org.get(8)))
				iep.setEMail(org.get(8).trim());//
		}catch(Exception e){
			
		}
		
		// 性别
		try{
			if(StringUtils.hasText(
					(String)org.get(9)))
				iep.setGender(org.get(9).trim());//
		}catch(Exception e){
			
		}
		
		// 生日
		try{
			if(StringUtils.hasText(
					(String)org.get(10)))
				iep.setBirthday(org.get(10).trim());//
		}catch(Exception e){
			
		}		
		
		// 办公电话
		try{
			if(StringUtils.hasText(
					(String)org.get(11)))
				iep.setOfficeNumber(org.get(11).trim());//
		}catch(Exception e){
			
		}		
		
		return iep;
	}
	
	protected V3xOrgEntity existEntity(OrganizationServices  organizationServices
			,ImpExpPojo pojo,V3xOrgAccount voa)throws Exception{
		
		ImpExpMember  iep=(ImpExpMember)pojo;
		V3xOrgMember member = memberLoginNameMap.get(iep.getLoginName());
/*		V3xOrgMember member = organizationServices.getOrgManagerDirect()
		                  .getMemberByLoginName(iep.getLoginName());*/
		return member;
	}
	
	protected V3xOrgEntity copyToEntity(OrganizationServices  organizationServices
			                         ,MetadataManager metadataManager,ImpExpPojo pojo
			                         ,V3xOrgEntity ent,V3xOrgAccount voa)throws Exception{
		
		
		return copyToMember(organizationServices,metadataManager,(ImpExpMember)pojo
		        ,(V3xOrgMember)ent,voa);
	}
	protected V3xOrgEntity copyToMember(OrganizationServices  organizationServices
            ,MetadataManager metadataManager,ImpExpMember pojo
            ,V3xOrgMember ent,V3xOrgAccount voa)throws Exception{
		if(pojo==null)
			throw new Exception("null ImpExpMember object to cover to V3xOrgMember object");
		
		V3xOrgMember vop=null;
		boolean isnew=false;
		if(ent!=null){
			vop=organizationServices
			                   .getOrgManagerDirect()
			                   .getMemberById(ent.getId());
		}
		if(ent==null){
			vop=new V3xOrgMember();
			isnew=true;
		}
		
		vop.setName(pojo.getName());//
		vop.setCode(pojo.getCode());//
		vop.setOrgAccountId(voa.getId());
		vop.setLoginName(pojo.getLoginName());
		
		vop.setEmailAddress(pojo.getEMail());//
		vop.setTelNumber(pojo.getTelNumber());
		
		String gender = pojo.getGender().toUpperCase();
		if(males.contains(gender)){
			vop.setGender(V3xOrgEntity.MEMBER_GENDER_MALE);	
		}
		else if(females.contains(gender)){
			vop.setGender(V3xOrgEntity.MEMBER_GENDER_FEMALE);		
		}
		if(pojo.getBirthday().length()>0)
		{
			try{
				Date birthday;
				if((pojo.getBirthday().indexOf("-")==2))
				{
					birthday = Datetimes.parse(pojo.getBirthday(), "yy-MM-dd");
				}
				else if(pojo.getBirthday().indexOf("年")==2)
				{
					
					birthday = Datetimes.parse(pojo.getBirthday(), "yy年MM月dd日");
				}
				else
				{
					birthday = Datetimes.parse(pojo.getBirthday(),"yyyy年MM月dd日");
				}
				vop.setBirthday(birthday);
			}catch(Exception e)
			{
				// 忽略出生日期解析错误
			}
		}
		
		if(pojo.getOfficeNumber().length()>0)
		{
			vop.setProperty("officeNum", pojo.getOfficeNumber());
		}		
		if(IImexPort.NULL_ENTITY_TAG.equals(pojo.getDept())){
			vop.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
		}else{
			String[] depnc=this.getCodeFromNameCodeString(pojo.getDept());
			V3xOrgDepartment dep=this.getNeedDepartment(organizationServices, depnc, voa);		
			if(dep!=null)
			{
				long depid=dep.getId();
				
				if(isnew)
					vop.setIsInternal(dep.getIsInternal());//根据部门设置
				vop.setOrgDepartmentId(depid);
			}
			else
			{
				vop.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
			}
		}
		
		if(IImexPort.NULL_ENTITY_TAG.equals(pojo.getPpost())
				|| !vop.getIsInternal()){
			vop.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
		}else{
			String[] postnc=this.getCodeFromNameCodeString(pojo.getPpost());
			try{
				/*
				if(vop.getIsInternal()){
					long postid=this.getNeedPost(organizationServices, postnc, voa).getId();
					vop.setOrgPostId(postid);
				}else{
					vop.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
				}*/
				long postid=this.getNeedPost(organizationServices, postnc, voa).getId();
				vop.setOrgPostId(postid);
			}catch(Exception e){
				if(vop.getIsInternal())
					throw e;
				
				//vop.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
			}			
		}
		
		
		if(IImexPort.NULL_ENTITY_TAG.equals(pojo.getLevel())
				|| !vop.getIsInternal()){
			vop.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
		}else{
			String[] levnc=this.getCodeFromNameCodeString(pojo.getLevel());
			try{/*
				if(vop.getIsInternal()){
					long levid=this.getNeedLevel(organizationServices, levnc, voa).getId();
					vop.setOrgLevelId(levid);
				}else{
					vop.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
				}*/		
				long levid=this.getNeedLevel(organizationServices, levnc, voa).getId();
				vop.setOrgLevelId(levid);
			}catch(Exception e){
				if(vop.getIsInternal())
					throw e;
				
				//vop.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
			}					
		}
		
		return vop;
	}
	
	protected void add(OrganizationServices  organizationServices
			,V3xOrgEntity ent)throws Exception{
		log.info("add member="+ent.getName());
		V3xOrgMember m=(V3xOrgMember)ent;
		if(m.getIsInternal()){
			organizationServices.addMember(m);
		}else{
			organizationServices.addUnOrgMember(m);
		}
		log.info("ok add member="+ent.getName());
	}

	protected void update(OrganizationServices  organizationServices
			,V3xOrgEntity ent)throws Exception{
		log.info("update member="+ent.getName());
		V3xOrgMember m=(V3xOrgMember)ent;
		if(m.getIsInternal()){
			organizationServices.updateMember(m);
		}else{
			organizationServices.updateUnOrgMember(m);			        
		}
		log.info("ok update member="+ent.getName());
	}
	
	protected String msg4AddNoDouble(ImpExpPojo pj){//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
		return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME)
		        +((ImpExpMember)pj).getLoginName();
		//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME)
	}
	
	protected void addNoDouble(ImpExpPojo pj,Map<String,Object> stringMap,List pjs,Map mapReport){
		logger.info("do addNoDouble");
		if(pj==null)
			return ;
		
		//boolean ok=true;
		ImpExpMember pm=(ImpExpMember)pj;
		if(stringMap!=null){
			if(stringMap.containsKey(pm.getLoginName())){
				ResultObject ro=this.newResultObject(pj
						   , this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED)
						   , this.msg4AddNoDouble(pm));
				this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
				return ;
			}
			
			stringMap.put(pm.getLoginName(), pm);
		}
		
		if(pjs!=null)
			pjs.add(pj);
		
		return ;
	}
	
	protected String inCurrentAccount(V3xOrgEntity ent,V3xOrgAccount voa
            ,OrganizationServices  organizationServices){
		if(ent==null || voa==null)
			return null;
		
		long voaid=voa.getId();
		long entaid=ent.getOrgAccountId();
		
		if(voaid==entaid)
			return null;		
		
		StringBuffer reason=new StringBuffer();
		try{
			V3xOrgMember m=(V3xOrgMember)ent;
			V3xOrgAccount oa=organizationServices.getOrgManagerDirect().getAccountById(entaid);
			String oan=
				oa==null?null:oa.getName();
			reason.append(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME));
			//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME)
			reason.append(m.getLoginName());
			if(StringUtils.hasText(oan)){
				reason.append(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT)+oan);
				//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT)
			}else
				reason.append(this.getMsgProvider().getMsg
						(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT));
			//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT)
			reason.append(this.getMsgProvider()
					.getMsg(MsgContants.ORG_IO_MSG_NAME_REG));//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_REG)
		}catch(Exception e){
			
		}
		
		return reason.toString();
	}
	
	protected String getOKMsg4Add(V3xOrgEntity ent,OrganizationServices  organizationServices){
		return super.getOKMsg4Add(ent, organizationServices)
		             +getOKMsg4Member(ent);
	}
	protected String getOKMsg4Update(V3xOrgEntity ent,OrganizationServices  organizationServices){
		
		return super.getOKMsg4Update(ent, organizationServices)
		          +getOKMsg4Member(ent);
	}
	protected String getOKMsg4Member(V3xOrgEntity ent){
		String msg="";
		try{
			V3xOrgMember m=(V3xOrgMember)ent;
			msg+="  "+this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME)
			    +"："+m.getLoginName();
		}catch(Exception e){
			
		}
		return msg;
	}
	@Override
	protected Map add(OrganizationServices organizationServices,
			MetadataManager metadataManager, List ents, Map mapReport)
			throws Exception {
		return commit(organizationServices, ents, mapReport,true);
	}
	protected Map update(OrganizationServices  organizationServices
			,MetadataManager metadataManager
			,List ents
			,Map mapReport)throws Exception{
		return commit(organizationServices, ents, mapReport,false);
	}	
	private Map commit(OrganizationServices organizationServices, List ents,
			Map mapReport,boolean isAdd) throws Exception {
		if(ents==null || mapReport==null)
			return mapReport;
		if(ents.size()==0) return mapReport;
		
		OrderedMap memberMap = new ListOrderedMap();
		for(Object o: ents)
		{
			V3xOrgMember m = (V3xOrgMember) o;
			memberMap.put(m.getId(),m);
		}
		V3xOrgMember m = (V3xOrgMember)ents.get(0);
		Map<Long, String> r = organizationServices.synchMember(ents,false,false,m.getOrgAccountId());
		
		for(OrderedMapIterator it = memberMap.orderedMapIterator();it.hasNext();)
		{
			Long id = (Long) it.next();
			V3xOrgMember member = (V3xOrgMember) memberMap.get(id);
			String value = r.get(id);
			if(value!=null)
			{
				if(value.substring(0,1).equals("1"))
				{
					log.error("保存人员出错："+id+" " +value);
					ResultObject ro=null;
                    if(value.equals("1|error add member for the same loginname already existed!"))
                    {
                    	 ro=this.newResultObject(member
								, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED)
								, "外单位下存在相同登录名");
                    }
                    else
                    {
                    	 ro=this.newResultObject(member
								, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED)
								, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION)+value);
                    }
                    mapReport=this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
									
				}
				else
				{
					if(isAdd)
					{
						ResultObject ro=this.newResultObject(member
								, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
								, this.getOKMsg4Add(member, organizationServices));
						mapReport=this.addReport(ro, IImexPort.RESULT_ADD, mapReport);		
					}
					else
					{
						ResultObject ro=this.newResultObject(member
					            , this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
					            , this.getOKMsg4Update(member, organizationServices));
						mapReport=this.addReport(ro, IImexPort.RESULT_UPDATE, mapReport);
					}					
				}
			}
		}		
		return mapReport;
	}	
	
}
