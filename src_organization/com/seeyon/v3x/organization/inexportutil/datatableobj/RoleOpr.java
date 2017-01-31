package com.seeyon.v3x.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
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
public class RoleOpr implements IImexPort {
	public String[] getFixedField(HttpServletRequest request){
		String []fieldname={
			"name:角色名:name",
			"type:角色类型:type",
			"bond:角色绑定类型:bond",
			"sort_id:排序号:sort",
			"enable:是否启用:enable",
			"create_time:创建时间:create",
			"update_time:更新时间:update",
			"desciption:角色描述:cription",
			"org_account_id:单位的ID:accountid"
	};
		return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_role VALUES
	 * (1, 'account_exchange', '', 1, 1, 1, 0, '2007-8-16 13:38:17', 
	 * '2007-8-16 14:26:09',-7402591981046643031, '0');
	 */
	public List creatInsertSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgRole voa = (V3xOrgRole)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" INSERT INTO v3x_org_role VALUES ( ");
			
			sb.append("'"+String.valueOf(UUIDLong.longUUID())+"',");	//id
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+(voa.getDescription()==null?"":voa.getDescription())+"',");					//description
			if(voa.getEnabled()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//enable
			}			
			sb.append("'"+voa.getType()+"',");						//type
			sb.append("'"+voa.getBond()+"',");						//bond

			if(voa.getSortId().intValue() == 0){
				sb.append("'"+i+"',");						//sortid
			}else{
				sb.append("'"+voa.getSortId()+"',");						//sortid
			}

			sb.append("TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append("TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append("'"+voa.getOrgAccountId()+"',");				//accountid
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
			V3xOrgRole voa = (V3xOrgRole)volst.get(i);
			StringBuffer sb = new StringBuffer();
			
			if(voa.getName()!= null && !"".equals(voa.getName())&&"null".equals(voa.getName())){
				if(voa.getName().length()>255)
					throw new Exception("数据 " +voa.getName()+ " 的 名称 ,长度太长!");
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
		return new V3xOrgRole();
	}

	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();		
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgRole voa = new V3xOrgRole();
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
			V3xOrgRole voa = (V3xOrgRole)volst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_department SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getDescription()==null?"":" , description='"+voa.getDescription()+"'");					//description
			if(voa.getEnabled()){
				sb.append(" , enable='"+1+"'");
			}else{
				sb.append(" , enable='"+0+"'");									//enable
			}
			sb.append(" , type='"+voa.getType()+"'");	
			sb.append(" , bond='"+voa.getBond()+"'");	
			sb.append(voa.getSortId()==null?"":" , sort_id='"+voa.getSortId()+"'");						//sortid
			sb.append(voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
			sb.append(voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
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
		Pagination.setNeedCount(false);
		List v3xorgaccountvolst = od.getAllRoles(((V3xOrgRole)volst.get(0)).getOrgAccountId(),false);
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		for(int i=0;i<newlst.size();i++){
			V3xOrgRole voa = (V3xOrgRole)newlst.get(i);
			for(int j=0;j<v3xorgaccountvolst.size();j++){
				V3xOrgRole v3oavo = (V3xOrgRole)v3xorgaccountvolst.get(j);
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
