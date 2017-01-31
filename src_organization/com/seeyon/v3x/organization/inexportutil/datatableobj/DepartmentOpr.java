package com.seeyon.v3x.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.inexportutil.DataObject;
import com.seeyon.v3x.organization.inexportutil.DataService;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.util.Datetimes;
/**
 * 
 * @author kyt
 *
 */
public class DepartmentOpr implements IImexPort {
	public String[] getFixedField(HttpServletRequest request){
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String dept_inner = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label.inner");
		String dept_out = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label.out");
		String dept_name = ResourceBundleUtil.getString(resource, local, "org.dept_form.name.label");
		String dept_code = ResourceBundleUtil.getString(resource, local, "org.dept_form.code.label");
		String dept_sortId = ResourceBundleUtil.getString(resource, local, "org.account_form.sortId.label");
		String dept_superDepartment = ResourceBundleUtil.getString(resource, local, "org.dept_form.superDepartment.label");
		String dept_kind = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label");
		String dept_state = ResourceBundleUtil.getString(resource, local, "org.dept_form.enable.label");
		String dept_post = ResourceBundleUtil.getString(resource, local, "org.dept_form.post_fieldset.label");
		String dept_account = ResourceBundleUtil.getString(resource, local, "org.dept_form.account");
		String dept_descript = ResourceBundleUtil.getString(resource, local, "org.dept_form.descript_fieldset.label");
		String dept_manage_info = ResourceBundleUtil.getString(resource, local, "org.dept_form.manager_fieldset.label");
		String dept_list = ResourceBundleUtil.getString(resource, local, "org.dept_form.list");
		String company_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String company_updateDate = ResourceBundleUtil.getString(resource, local, "org.account_form.updatetime.label");
		//是、否
		String yes = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.yes");
		String no = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.no");
		String isCreateDeptSpace = ResourceBundleUtil.getString(resource, local, "org.dept_form.isCreateDeptSpace.label");		
		String path = ResourceBundleUtil.getString(resource, local, "org.dept_form.path.label");		
		String []fieldname={
			"name:"+dept_name+":name",
			"path:"+path+":path",
			"code:"+dept_code+":code",
			"is_internal:"+dept_inner+":internal",
			"sort_id:"+dept_sortId+":sort",
			"enable:"+state_Enabled+":enable",
			"create_time:"+company_createDate+":creat",
			"update_time:"+company_updateDate+":update",
			"description:"+dept_descript+":cription",
			"org_account_id:"+dept_account+":accountid"
	};
	return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_department VALUES 
	 * ('-9015430405055297824', '项目管理', 'xmgl', '0.1.4', '1', '1', 
	 * '20', '2007-08-01 11:36:58', '2007-08-01 11:36:58', 
	 * '', '-5362937964371884064', '0');
	 */
	public List creatInsertSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgDepartment voa = (V3xOrgDepartment)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" INSERT INTO v3x_org_department VALUES ( ");
			
			sb.append("'"+String.valueOf(UUIDLong.longUUID())+"',");	//id
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+(voa.getCode()==null?"":voa.getCode())+"',");							//code
			sb.append("'"+(voa.getPath()==null?"":voa.getPath())+"',");							//path
			if(voa.getEnabled()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");															//enable
			}
			if(voa.getIsInternal())
				sb.append("'"+1+"',");															//isinternal
			else sb.append("'"+0+"',");

			if(voa.getSortId().intValue() == 0){
				sb.append("'"+i+"',");						//sortid
			}else{
				sb.append("'"+voa.getSortId()+"',");						//sortid
			}
			
			sb.append("TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append("TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append("'"+(voa.getDescription()==null?"":voa.getDescription())+"',");			//description
			sb.append("'"+voa.getOrgAccountId()+"',");											//accountid
			if(voa.getIsDeleted())
				sb.append("'"+1+"'");															//isdeleted
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
			V3xOrgDepartment voa = (V3xOrgDepartment)volst.get(i);
			StringBuffer sb = new StringBuffer();
			
			if(voa.getName()!= null && !"".equals(voa.getName())&&"null".equals(voa.getName())){
				if(voa.getName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 名称 ,长度太长!");
			}			
			if(voa.getCode()!= null && !"".equals(voa.getCode())&&"null".equals(voa.getCode())){
				if(voa.getCode().length()>20)
					throw new Exception("数据 " +voa.getName()+ " 的 代码 ,长度太长!");
			}
			
			if(voa.getPath()!= null && !"".equals(voa.getPath())&&"null".equals(voa.getPath())){
				if(voa.getPath().length()>50)
					throw new Exception("数据 " +voa.getName()+ " 的 路径 ,长度太长!");
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
		return new V3xOrgDepartment();
	}

	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();		
		List accountalllst = od.getAllAccounts();
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgDepartment voa = new V3xOrgDepartment();
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
										Long accountID = DataService.setForeignKey(accountalllst, dao.getFieldName(), valuelst.get(dao.getColumnnum()).toString());										
										mdd.invoke(voa, new Object[]{accountID});
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
			V3xOrgDepartment voa = (V3xOrgDepartment)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_department SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getCode()==null?"":" , code='"+voa.getCode()+"'");							//code
			sb.append(voa.getPath()==null?"":" , path='"+voa.getPath()+"'");							//alias
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
			if(voa.getIsDeleted())
				sb.append(" , is_deleted='"+1+"'");						//isdeleted
			else sb.append(" , is_deleted='"+0+"'");
			
			sb.append(" where id='"+voa.getId()+"'");
			returnlst.add(sb);
		}
		return returnlst;
	}
	public Map devVO(OrgManagerDirect od,List volst) throws Exception{
		List v3xorgaccountvolst = od.getAllDepartments(((V3xOrgDepartment)volst.get(0)).getOrgAccountId(),false);
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		for(int i=0;i<newlst.size();i++){
			V3xOrgDepartment voa = (V3xOrgDepartment)newlst.get(i);
			if(v3xorgaccountvolst.size() != 0){
				for(int j=0;j<v3xorgaccountvolst.size();j++){
					V3xOrgDepartment v3oavo = (V3xOrgDepartment)v3xorgaccountvolst.get(j);
					if(v3oavo.getName().equals(voa.getName())){
						duplst.add(v3oavo);
						newlst.remove(i);
						i--;
					}else{
						//需要注入path
						voa.setPath("0."+(i+1));
					}
				}
			}else{
				//需要注入path
				voa.setPath("0."+(i+1));
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
