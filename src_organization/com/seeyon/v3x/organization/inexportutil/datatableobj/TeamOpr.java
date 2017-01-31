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
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
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
public class TeamOpr implements IImexPort {
	public String[] getFixedField(HttpServletRequest request){
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String team_type_personal = ResourceBundleUtil.getString(resource, local, "org.team_form.personalteam");
		String team_type_system = ResourceBundleUtil.getString(resource, local, "org.team_form.systemteam");
		String team_type_project = ResourceBundleUtil.getString(resource, local, "org.team_form.projectteam");
		String team_pro_public = ResourceBundleUtil.getString(resource, local, "org.team_form.openteam");
		String team_pro_private = ResourceBundleUtil.getString(resource, local, "org.team_form.privateteam");
		String team_name = ResourceBundleUtil.getString(resource, local, "org.team_form.name");
		String team_type = ResourceBundleUtil.getString(resource, local, "org.team_form.type");
		String team_dept = ResourceBundleUtil.getString(resource, local, "org.team_form.deptName.label");
		String team_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String team_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
//		权限属性
		String post_sortId = ResourceBundleUtil.getString(resource, local, "org.post_form.type.sort");
		String team_purview = ResourceBundleUtil.getString(resource, local, "team.level");
		String team_charge = ResourceBundleUtil.getString(resource, local, "team.charge");
		String team_member = ResourceBundleUtil.getString(resource, local, "team.leaguer");
		String team_leader = ResourceBundleUtil.getString(resource, local, "team.lead");
		String team_relateMember = ResourceBundleUtil.getString(resource, local, "team.correlation.people");
		String team_account = ResourceBundleUtil.getString(resource, local, "team.account");
		String team_description = ResourceBundleUtil.getString(resource, local, "team.description");
		String team_list = ResourceBundleUtil.getString(resource, local, "team.list");
		String company_updateDate = ResourceBundleUtil.getString(resource, local, "org.account_form.updatetime.label");

		String team_code = ResourceBundleUtil.getString(resource, local, "org.team_form.code.label");
		String team_isprivate = ResourceBundleUtil.getString(resource, local, "org.team_form.isprivate.label");
		String team_ownid = ResourceBundleUtil.getString(resource, local, "org.team_form.ownerid.label");
		
		
		String []fieldname={
			"code:"+team_code+":code",
			"name:"+team_name+":name",
			"dep_id:"+team_dept+":depid",
			"enable:"+state_Enabled+":enable",
			"sort_id:"+post_sortId+":sort",
			"create_time:"+team_createDate+":create",
			"update_time:"+company_updateDate+":update",
			"description:"+team_description+":cription",
			"org_account_id:"+team_account+":accountid",
			"is_private:"+team_isprivate+":private",
			"type:"+team_type+":type",
			"owner_id:"+team_ownid+":ownerid"
	};
		return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_team VALUES 
	 * ('-4479237577061416875', '', '民建执委', '3051267862366536575', '1', '2', 
	 * '2007-07-26 00:00:00', '2007-07-26 18:45:48', '', '-4496559578009091517', 
	 * '1', '2', '-1', '0');
	 */
	public List creatInsertSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgTeam voa = (V3xOrgTeam)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" INSERT INTO v3x_org_team VALUES ( ");
			
			sb.append("'"+String.valueOf(UUIDLong.longUUID())+"',");	//id
			sb.append("'"+(voa.getCode()==null?"":voa.getCode())+"',");							//code
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+voa.getDepId()+"',");						//sortid
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
			sb.append("TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append("TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append("'"+(voa.getDescription()==null?"":voa.getDescription())+"',");					//description
			sb.append("'"+voa.getOrgAccountId()+"',");						//superior
			if(voa.getIsPrivate())
				sb.append("'"+1+"',");						//isroot
			else sb.append("'"+0+"',");
			sb.append("'"+voa.getType()+"',");					//levelscope
			sb.append("'"+voa.getOwnerId()+"',");				//accesspermission
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
			V3xOrgTeam voa = (V3xOrgTeam)volst.get(i);
			StringBuffer sb = new StringBuffer();
			
			if(voa.getName()!= null && !"".equals(voa.getName())&&"null".equals(voa.getName())){
				if(voa.getName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 名称 ,长度太长!");
			}			
			if(voa.getCode()!= null && !"".equals(voa.getCode())&&"null".equals(voa.getCode())){
				if(voa.getCode().length()>30)
					throw new Exception("数据 " +voa.getName()+ " 的 代码 ,长度太长!");
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
		return new V3xOrgTeam();
	}

	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();		
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgTeam voa = new V3xOrgTeam();
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
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString().trim()+" 00:00:00")});
									}else{
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString())});
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
			V3xOrgTeam voa = (V3xOrgTeam)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_department SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getCode()==null?"":" , code='"+voa.getCode()+"'");
			sb.append(voa.getDepId()==null?"":" , dep_id='"+voa.getDepId()+"'");
			if(voa.getEnabled()){
				sb.append(" , enable='"+1+"'");
			}else{
				sb.append(" , enable='"+0+"'");									//enable
			}
			sb.append(voa.getSortId()==null?"":" , sort_id='"+voa.getSortId()+"'");						//sortid
			sb.append(voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
			sb.append(voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
			sb.append(voa.getDescription()==null?"":" , description='"+voa.getDescription()+"'");					//description
			sb.append(voa.getOrgAccountId()==null?"":" , org_account_id='"+voa.getOrgAccountId()+"'");						//superior
			if(voa.getIsPrivate())
				sb.append(" , is_private='"+1+"'");						//is_private
			else sb.append(" , is_private='"+0+"'");
						
			sb.append(" , type='"+voa.getType()+"'");	
			sb.append(voa.getOwnerId()==null?"":" , owner_id='"+voa.getOwnerId()+"'");	
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
		List v3xorgaccountvolst = od.getAllTeams(((V3xOrgTeam)volst.get(0)).getOrgAccountId(),false);
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		for(int i=0;i<newlst.size();i++){
			V3xOrgTeam voa = (V3xOrgTeam)newlst.get(i);
			for(int j=0;j<v3xorgaccountvolst.size();j++){
				V3xOrgTeam v3oavo = (V3xOrgTeam)v3xorgaccountvolst.get(j);
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
