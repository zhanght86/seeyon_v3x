package com.seeyon.v3x.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.inexportutil.DataObject;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.util.Datetimes;
/**
 * 
 * @author kyt
 *
 */
public class AccountOpr implements IImexPort {
	//
	//
	public String[] getFixedField(HttpServletRequest request){
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出文件中的国际化文本
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String permission_all = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.all");
		String permission_up = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.up");
		String permission_upAnddown = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.upAnddown");
		String permission_upAndpar = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.upAndpar");
		String company_name = ResourceBundleUtil.getString(resource, local, "org.account_form.name.label");
		String company_shotName = ResourceBundleUtil.getString(resource, local, "org.account_form.shortname.label");
		String company_secondName = ResourceBundleUtil.getString(resource, local, "org.account_form.secondName.label");
		String company_sortId = ResourceBundleUtil.getString(resource, local, "org.account_form.sortId.label");
		String company_code = ResourceBundleUtil.getString(resource, local, "org.account_form.code.label");
		String company_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String company_updateDate = ResourceBundleUtil.getString(resource, local, "org.account_form.updatetime.label");
		String company_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
		String company_superior = ResourceBundleUtil.getString(resource, local, "org.account_form.superior.label");
		String company_alias = ResourceBundleUtil.getString(resource, local, "org.account_form.alias.label");
		String company_permission = ResourceBundleUtil.getString(resource, local, "org.account_form.permission.label");
		String company_type = ResourceBundleUtil.getString(resource, local, "org.account_form.type.label");
		String company_level = ResourceBundleUtil.getString(resource, local, "org.account_form.level.label");
		String company_kind = ResourceBundleUtil.getString(resource, local, "org.account_form.kind.label");
		String company_manager = ResourceBundleUtil.getString(resource, local, "org.account_form.manager.label");
		String company_address = ResourceBundleUtil.getString(resource, local, "org.account_form.address.label");
		String company_zipCode = ResourceBundleUtil.getString(resource, local, "org.account_form.zipCode.label");
		String company_telephone = ResourceBundleUtil.getString(resource, local, "org.account_form.telephone.label");
		String company_fax = ResourceBundleUtil.getString(resource, local, "org.account_form.fax.label");
		String company_ipAddress = ResourceBundleUtil.getString(resource, local, "org.account_form.ipAddress.label");
		String company_accountMail = ResourceBundleUtil.getString(resource, local, "org.account_form.accountMail.label");
		String company_decription = ResourceBundleUtil.getString(resource, local, "org.account_form.decription.label");
		String company_adminName = ResourceBundleUtil.getString(resource, local, "org.account_form.adminName.label");
		String company_adminEmail = ResourceBundleUtil.getString(resource, local, "org.account_form.adminEmail.label");
		String company_adminID = ResourceBundleUtil.getString(resource, local, "org.account_form.adminid.label");
		String company_passWord = ResourceBundleUtil.getString(resource, local, "org.account_form.adminPass.label");
		String company_role_assign = ResourceBundleUtil.getString(resource, local, "org.account_form.role.assign");
		String company_list = ResourceBundleUtil.getString(resource, local, "org.account_form.list");
		String company_isRoot_yes = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.yes");
		String company_isRoot_no = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.no");
		String commpany_isRoot = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.label");
		String commpany_GroupShortname = ResourceBundleUtil.getString(resource, local, "org.account_form.groupshortname.label");		
		String []fieldname={
			"name:"+company_name+":name",
			"second_name:"+company_secondName+":second",
			"code:"+company_code+":code",
			"alias:"+company_alias+":alias",
			"shortname:"+company_shotName+":short",
			"group_shortname:"+commpany_GroupShortname+":groupshortname",
			"sort_id:"+company_sortId+":sort",
			"enable:"+state_Enabled+":enable",
			"admin_name:"+company_adminName+":adminname",
			"admin_id:"+company_adminID+":adminid",
			"admin_email:"+company_adminEmail+":adminemail",
			"create_time:"+company_createDate+":creat",
			"update_time:"+company_updateDate+":update",
			"superior:"+company_superior+":superior",
			"decription:"+company_decription+":cription",
			"level_scope:"+company_level+":level",
			"access_permission:"+company_permission+":accesspermission",
			"isroot:"+commpany_isRoot+":root"
	};
		return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_account VALUES 
	 * ('-7402591981046643031', '用友华表', '', 'ufida-hb', 'ufida-hb', '华表', 
	 * '', '4', '1', 'ufida-hb', '-1', '', '2007-07-26 00:00:00', '2007-07-26 13:46:30', 
	 * 'ufida-hb', '-4496559578009091517', '0', '0', '0', '0');
	 */
	public List creatInsertSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgAccount voa = (V3xOrgAccount)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" INSERT INTO v3x_org_account VALUES ( ");
			voa.setId(UUIDLong.longUUID());
			sb.append("'"+String.valueOf(voa.getId())+"',");	//id
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+(voa.getSecondName()==null?"":voa.getSecondName())+"',");					//secondname
			sb.append("'"+(voa.getCode()==null?"":voa.getCode())+"',");							//code
			sb.append("'"+(voa.getAlias()==null?"":voa.getAlias())+"',");							//alias
			sb.append("'"+(voa.getShortname()==null?"":voa.getShortname())+"',");						//shortname
			sb.append("'"+(voa.getGroupShortname()==null?"":voa.getGroupShortname())+"',");				//groupshortname

			if(voa.getSortId().intValue() == 0){
				sb.append("'"+i+"',");						//sortid
			}else{
				sb.append("'"+voa.getSortId()+"',");						//sortid
			}					//sortid
			if(voa.getEnabled()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//enable
			}
			sb.append("'"+(voa.getAdminName()==null?"":voa.getAdminName())+"',");						//adminname
			sb.append("'"+voa.getAdminId().longValue()+"',");						//adminid
			sb.append("'"+(voa.getAdminEmail()==null?"":voa.getAdminEmail())+"',");					//adminemail
			sb.append("TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append("TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append("'"+(voa.getDecription()==null?"":voa.getDecription())+"',");					//description
			sb.append("'"+voa.getSuperior()+"',");						//superior
			sb.append("'"+voa.getLevelScope()+"',");					//levelscope
			if(voa.getIsRoot())
				sb.append("'"+1+"',");						//isroot
			else sb.append("'"+0+"',");
			//默认权限为  "全否" 对应为：7
			sb.append("'"+(voa.getAccessPermission()==null?"7":voa.getAccessPermission())+"',");				//accesspermission
			if(voa.getIsDeleted())
				sb.append("'"+1+"'");						//isdeleted
			else sb.append("'"+0+"'");
			
			sb.append(")");
			returnlst.add(sb);
		}
		return returnlst;
	}

	public List matchLanguagefield(List statrlst,HttpServletRequest request) throws Exception {
		for(int i=0;i<statrlst.size();i++){
			DataObject dao = (DataObject)statrlst.get(i);
//			if(dao.getFieldName().equals("superior")){
//				statrlst.remove(i);
//				i--;
//			}else{
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
//			}
		}
		return statrlst;
	}

	public void validateData(List volst) throws Exception {
		for(int i=0;i<volst.size();i++){
			V3xOrgAccount voa = (V3xOrgAccount)volst.get(i);
			StringBuffer sb = new StringBuffer();
			
			if(voa.getName()!= null && !"".equals(voa.getName())&&"null".equals(voa.getName())){
				if(voa.getName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 名称 ,长度太长!");
			}
			if(voa.getSecondName()!= null && !"".equals(voa.getSecondName())&&"null".equals(voa.getSecondName())){
				if(voa.getSecondName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 第二名称 ,长度太长!");
			}			
			if(voa.getCode()!= null && !"".equals(voa.getCode())&&"null".equals(voa.getCode())){
				if(voa.getCode().length()>30)
					throw new Exception("数据 " +voa.getName()+ " 的 代码 ,长度太长!");
			}
			
			if(voa.getAlias()!= null && !"".equals(voa.getAlias())&&"null".equals(voa.getAlias())){
				if(voa.getAlias().length()>20)
					throw new Exception("数据 " +voa.getName()+ " 的 别名 ,长度太长!");
			}
			
			if(voa.getShortname()!= null && !"".equals(voa.getShortname())&&"null".equals(voa.getShortname())){
				if(voa.getShortname().length()>20)
					throw new Exception("数据 " +voa.getName()+ " 的 简称 ,长度太长!");
			}	
			
			if(voa.getGroupShortname()!= null && !"".equals(voa.getGroupShortname())&&"null".equals(voa.getGroupShortname())){
				if(voa.getGroupShortname().length()>100)
					throw new Exception("数据 " +voa.getName()+ " 的 单位简称 ,长度太长!");
			}	

					
			if(voa.getAdminName()!= null && !"".equals(voa.getAdminName())&&"null".equals(voa.getAdminName())){
				if(voa.getAdminName().length()>100)
					throw new Exception("数据 " +voa.getName()+ " 的 管理员名称 ,长度太长!");
			}	
			if(voa.getAdminId()!= null ){
				if(voa.getAdminId().toString().length()>20)
					throw new Exception("数据 " +voa.getName()+ " 的 管理员ID ,长度太长!");
			}	
			if(voa.getAdminEmail()!= null && !"".equals(voa.getAdminEmail())&&"null".equals(voa.getAdminEmail())){
				if(voa.getAdminEmail().length()>100)
					throw new Exception("数据 " +voa.getName()+ " 的 管理员邮件地址 ,长度太长!");
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
		return new V3xOrgAccount();
	}

	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst)throws Exception{
		List returnlst = new ArrayList();		
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgAccount voa = new V3xOrgAccount();
			List valuelst = accountList.get(i);
			Method med [] =  voa.getClass().getMethods();
			if(DataUtil.isNotNullValue(valuelst)){
			for(int j=0;j<med.length;j++){
				Method mdd = med [j];
				if(mdd.getName().indexOf("set") != -1){
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
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString().trim()+" 00:00:00", "yyyy-MM-dd HH:mm:ss")});
									}else{
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString(), "yyyy-MM-dd")});
									}
								}else if(cl[0].getName().equals("java.lang.Boolean")){
									mdd.invoke(voa, new Object[]{Boolean.valueOf(valuelst.get(dao.getColumnnum()).toString())});
								}else if(cl[0].getName().equals("java.lang.Long")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{new Long(valuelst.get(dao.getColumnnum()).toString())});
									}else{
										mdd.invoke(voa, new Object[]{new Long(0)});
									}			
								}else if(cl[0].getName().equals("int")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Integer.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{0});
									}			
								}else if(cl[0].getName().equals("com.seeyon.v3x.organization.domain.V3xOrgAccount")){
									V3xOrgAccount vox = new V3xOrgAccount();
									vox.setName(valuelst.get(dao.getColumnnum()).toString());
									mdd.invoke(voa, new Object[]{vox});		
								}else if(cl[0].getName().equals("java.lang.Byte")){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Byte.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{Byte.valueOf("1")});
									}			
								}else{
									mdd.invoke(voa, new Object[]{valuelst.get(dao.getColumnnum())});
								}
							}
						}
					}
				}
			}
			returnlst.add(voa);
			}
		}
		return returnlst;
	}

	public List creatUpdateSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgAccount voa = (V3xOrgAccount)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_account SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getSecondName()==null?"":" , second_name='"+voa.getSecondName()+"'");					//secondname
			sb.append(voa.getCode()==null?"":" , code='"+voa.getCode()+"'");							//code
			sb.append(voa.getAlias()==null?"":" , alias='"+voa.getAlias()+"'");							//alias
			sb.append(voa.getShortname()==null?"":" , shortname='"+voa.getShortname()+"'");						//shortname
			sb.append(voa.getGroupShortname()==null?"":" , group_shortname='"+voa.getGroupShortname()+"'");				//groupshortname
			sb.append(voa.getSortId()==null?"":" , sort_id='"+voa.getSortId()+"'");						//sortid
			if(voa.getEnabled()){
				sb.append(" , enable='"+1+"'");
			}else{
				sb.append(" , enable='"+0+"'");									//enable
			}
			sb.append(voa.getAdminName()==null?"":" , admin_name='"+voa.getAdminName()+"'");						//adminname
			sb.append(voa.getAdminId()==null?"":" , admin_id='"+voa.getAdminId().longValue()+"'");						//adminid
			sb.append(voa.getAdminEmail()==null?"":" , admin_email='"+voa.getAdminEmail()+"'");					//adminemail
			sb.append(voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
			sb.append(voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
			sb.append(voa.getDecription()==null?"":" , decription='"+voa.getDecription()+"'");					//description
			sb.append(voa.getSuperior()==null?"":" , superior='"+voa.getSuperior()+"'");						//superior
			sb.append(" , level_scope='"+voa.getLevelScope()+"'");					//levelscope
			if(voa.getIsRoot())
				sb.append(" , isroot='"+1+"'");						//isroot
			else sb.append(" ,  isroot='"+0+"'");
			//默认权限为  "全否" 对应为：7
			sb.append(voa.getAccessPermission()==null?" , access_permission='7'":" , access_permission='"+voa.getAccessPermission()+"' ");				//accesspermission
			if(voa.getIsDeleted())
				sb.append(" , is_deleted='"+1+"'");						//isdeleted
			else sb.append(" , is_deleted='"+0+"'");
			
			sb.append(" where id='"+voa.getId()+"'");
			returnlst.add(sb);
		}
		return returnlst;
	}
	public Map devVO(OrgManagerDirect od,List volst) throws Exception{
		Pagination.setNeedCount(false);
		List v3xorgaccountvolst = od.getAllAccounts();
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		for(int i=0;i<newlst.size();i++){
			V3xOrgAccount voa = (V3xOrgAccount)newlst.get(i);
			for(int j=0;j<v3xorgaccountvolst.size();j++){
				V3xOrgAccount v3oavo = (V3xOrgAccount)v3xorgaccountvolst.get(j);
				if(v3oavo.getName().equals(voa.getName())){
					duplst.add(v3oavo);
					newlst.remove(i);
					i--;
				}
			}
		}
		Map mp = new HashMap();
		//重复的
		mp.put("dup", duplst);
		//剩下的
		mp.put("new", newlst);
		return mp;
	}
	
	public Map importOrg(OrganizationServices  organizationServices
			,MetadataManager metadataManager
			,List<List<String>> fromList
			,V3xOrgAccount voa,boolean ignoreWhenUpdate
			    )throws Exception{
		return null;
	}
	
	public void setLocale(Locale val){
		
	}
}
