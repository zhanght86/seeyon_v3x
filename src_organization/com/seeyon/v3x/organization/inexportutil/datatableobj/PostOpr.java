package com.seeyon.v3x.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.inexportutil.DataObject;
import com.seeyon.v3x.organization.inexportutil.DataStringUtil;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.inexportutil.msg.MsgContants;
import com.seeyon.v3x.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.v3x.organization.inexportutil.pojo.ImpExpPost;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.util.Datetimes;
/**
 * 
 * @author kyt
 *
 */
public class PostOpr extends AbstractImpOpr implements IImexPort {   
	
	public String[] getFixedField(HttpServletRequest request){
		Locale local = LocaleContext.getLocale(request);
		
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String post_name = ResourceBundleUtil.getString(resource, local, "org.post_form.name");
		String post_type = ResourceBundleUtil.getString(resource, local, "org.post_form.type");
		String post_code = ResourceBundleUtil.getString(resource, local, "org.post_form.type.code");
		String post_sortId = ResourceBundleUtil.getString(resource, local, "org.post_form.type.sort");
		String post_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
		String post_account = ResourceBundleUtil.getString(resource, local, "org.account.lable");
		String post_description = ResourceBundleUtil.getString(resource, local, "org.post_form.description");
		String post_list = ResourceBundleUtil.getString(resource, local, "org.post_form.list");
		String company_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String company_updateDate = ResourceBundleUtil.getString(resource, local, "org.account_form.updatetime.label");
		
		String []fieldname={
			"name:"+post_name+":name",
			"code:"+post_code+":code",
			"enable:"+state_Enabled+":enable",
			"type:"+post_type+":type",
			"sort_id:"+post_sortId+":sort",
			"create_time:"+company_createDate+":create",
			"update_time:"+company_updateDate+":update",
			"desciption:"+post_description+":ciption",
			"org_account_id:"+post_account+":accountid"
	};	
		return fieldname;
	}
	/**
	 * INSERT INTO v3x_org_post VALUES 
	 * ('-9101587670459267670', '行业伙伴总监', '', '1', '4', '70', '2007-08-01 14:08:11', 
	 * '2007-08-01 14:08:11', '', '-5362937964371884064', '0');
	 */
	public List creatInsertSql(List volst) throws Exception {//tanglh
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgPost voa = (V3xOrgPost)volst.get(i);
			if(voa==null)
				continue;
			if(!StringUtils.hasText(voa.getName()))//voa.getName()必填
				continue;
			if(voa.getTypeId()<1)//voa.getName()必填
				continue;
			StringBuffer sb = getInsertHeadStringbuffer();
						
			sb.append("'"+String.valueOf(UUIDLong.longUUID())+"',");	//id
			sb.append("'"+(voa.getName()==null?"":voa.getName())+"',");							//name
			sb.append("'"+(voa.getCode()==null?"":voa.getCode())+"',");							//code
			//
			//logger.info(""+sb.toString());
			if(voa.getEnabled()){
				sb.append("'"+1+"',");
			}else{
				sb.append("'"+0+"',");									//enable
			}
			sb.append("'"+	voa.getTypeId()+"',");						//typeid

			if(voa.getSortId().intValue() == 0){
				sb.append("'"+i+"',");						//sortid
			}else{
				sb.append("'"+voa.getSortId()+"',");						//sortid
			}
			
			sb.append(DataStringUtil.createDateTimeString(null, voa.getCreateTime(), null));//tanglh
					//"TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//createtime
			sb.append(DataStringUtil.createDateTimeString(",", voa.getUpdateTime(), null));//
					//"TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF'),");					//updatetime
			sb.append(",'"+(voa.getDesciption()==null?"":voa.getDesciption())+"',");					//description
			sb.append("'"+voa.getOrgAccountId()+"',");				//accesspermission
			if(voa.getIsDeleted())
				sb.append("'"+1+"'");						//isdeleted   
			else sb.append("'"+0+"'");
			
			sb.append(");");
			returnlst.add(sb);
		}
		return returnlst;
	}
	
	StringBuffer getInsertHeadStringbuffer(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(" INSERT INTO v3x_org_post (  ");
		sb.append("id,name,code,enable,type,sort_id,create_time,update_time,desciption,org_account_id,is_deleted");
		sb.append(") VALUES ( ");
		
		return sb;
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
			V3xOrgPost voa = (V3xOrgPost)volst.get(i);
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
		return new V3xOrgPost();
	}

	public List assignVO(OrgManagerDirect od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();		
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgPost voa = new V3xOrgPost();
			List valuelst = accountList.get(i);
			Method med [] =  voa.getClass().getMethods();
			if(DataUtil.isNotNullValue(valuelst)){
			for(int j=0;j<med.length;j++){
				Method mdd = med [j];
				if(mdd.getName().indexOf("set") != -1){
					//logger.info("mdd.getName()="+mdd.getName());
					for(int m=0;m<volst.size();m++){
						DataObject dao = (DataObject)volst.get(m);
						if(mdd.getName().toLowerCase().indexOf(DataUtil.submark(dao.getFieldName()).toLowerCase()) == 3){
							if(dao.getColumnnum() != -1){
								Class cl[] = mdd.getParameterTypes();
								if(cl[0].getName().equals("java.lang.Integer")){
									//logger.info("java.lang.Integer");
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
									//logger.info("java.lang.Long");
									if(DataUtil.submark(dao.getFieldName())
											.toLowerCase()
											.indexOf("type") != -1){//post type
										java.lang.Long  pt=getPostType(metadataManager,
												valuelst.get(dao.getColumnnum()).toString());
										mdd.invoke(
												voa, new Object[]{pt});
										
									}else  if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
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
										mdd.invoke(
												voa, new Object[]{
														Byte.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
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
	
	//获取岗位类别列表   
	HashMap<String,String> itemMap=null;
	void initItemMap(MetadataManager metadataManager){
		List<MetadataItem> itemList = metadataManager.getMetadataItems("organization_post_types");
		this.itemMap = new HashMap<String,String>();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		
		for(MetadataItem item : itemList){
			this.itemMap.put(item.getLabel(), item.getValue());
			logger.info("item.getLabel()="+item.getLabel());
			logger.info("item.getValue()="+item.getValue());
			String lableC = ResourceBundleUtil.getString(resource, Locale.CHINA, item.getLabel());			
			logger.info("lableC="+lableC);
			if(!lableC.equals("")){
				this.itemMap.put(lableC, item.getValue());
			}
			
			String lableE = ResourceBundleUtil.getString(resource, Locale.ENGLISH, item.getLabel());
			logger.info("lableE="+lableE);
			if(!lableE.equals("")){
				this.itemMap.put(lableE, item.getValue());
			}
		}
	}
	java.lang.Long getPostType(MetadataManager metadataManager,String typename){
		if(metadataManager==null || !StringUtils.hasText(typename))
			return new Long(-1);
		////获取岗位类别列表
		//List<MetadataItem> itemList = metadataManager.getMetadataItems("organization_post_types");
		/*
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Set<String> keys=ResourceBundleUtil.getKeys(resource, typename);
		Iterator<String> it=keys.iterator();
		while(it.hasNext()){
			String key=it.next();
			List<MetadataItem> mis=metadataManager.getMetadataItems(key);
			if(mis==null || mis.isEmpty())
				continue;
			
			MetadataItem mi=mis.get(0);
			try{
				return Integer.getInteger(
						mi.getValue());
			}catch(Exception e){
				continue;
			}
		}
		*/
		
		try{
			if(itemMap==null)
				this.initItemMap(metadataManager);
			String v=itemMap.get(typename);
			logger.info(typename+" type="+v);
			if(StringUtils.hasText(v))
				return new Long(v);
		}catch(Exception e){
			logger.error("error", e);
		}
		return new Long(-1);
	}
	public List creatUpdateSql(List volst) throws Exception {
		List returnlst = new ArrayList();
		for(int i=0;i<volst.size();i++){
			V3xOrgPost voa = (V3xOrgPost)volst.get(i);
			if(voa==null)
				continue;
			if(!StringUtils.hasText(voa.getName()))//voa.getName()必填
				continue;
			if(voa.getTypeId()<1)//voa.getName()必填
				continue;
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE v3x_org_post SET ");
			
			sb.append("name='"+(voa.getName()==null?"":voa.getName())+"'");							//name
			sb.append(voa.getCode()==null?"":" , code='"+voa.getCode()+"'");							//code
			if(voa.getEnabled()){
				sb.append(" , enable='"+1+"'");
			}else{
				sb.append(" , enable='"+0+"'");									//enable
			}
			sb.append(voa.getTypeId()==null?"":" , type='"+voa.getTypeId()+"'");	
			sb.append(voa.getSortId()==null?"":" , sort_id='"+voa.getSortId()+"'");						//sortid
			sb.append(DataStringUtil.createDateTimeString(
					",create_time=", voa.getCreateTime(), null));//tanglh
					//voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
			sb.append(DataStringUtil.createDateTimeString(
					",update_time=", voa.getUpdateTime(), null));//tanglh
					//voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
			sb.append(voa.getDesciption()==null?"":" , desciption='"+voa.getDesciption()+"'");					//description
			sb.append(voa.getOrgAccountId()==null?"":" , org_account_id='"+voa.getOrgAccountId()+"'");						//superior
			if(voa.getIsDeleted())
				sb.append(" , is_deleted='"+1+"'");						//isdeleted
			else sb.append(" , is_deleted='"+0+"'");
			
			sb.append(" where id='"+voa.getId()+"'");
			returnlst.add(sb);
		}
		return returnlst;
	}
	private V3xOrgPost doRemove(V3xOrgPost voa,List inList){
		for(int j=0;j<inList.size();j++){
			V3xOrgPost v3oavo = (V3xOrgPost)inList.get(j);
			if(v3oavo.getName().equals(voa.getName())){
				return v3oavo;
			}
		}
		return null;
	}
	public Map devVO(OrgManagerDirect od,List volst) throws Exception{
		List v3xorgaccountvolst = od.getAllPosts(((V3xOrgPost)volst.get(0)).getOrgAccountId(),false);
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		//这段有时间再改进
		
		int i=0;
		V3xOrgPost ftempobj;
		while (i<newlst.size()){
			V3xOrgPost voa = (V3xOrgPost)newlst.get(i);
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
	
	protected String getAccountName(ImpExpPojo pojo){
		ImpExpPost p=(ImpExpPost)pojo;
		return p.getAccountName();
	}
	
	protected ImpExpPojo transToPojo(List<String> org)throws Exception{
		ImpExpPost  iep=new ImpExpPost();
		
		logger.info("org.size()="+org.size());
		if(org.size()<4){
			throw new Exception(this.getMsgProvider()
	                  .getMsg(MsgContants.ORG_IO_MSG_ERROR_FILEDATA));//ORG_IO_MSG_ERROR_FILEDATA
		}
		
		if(!StringUtils.hasText(
				(String)org.get(0)))
			throw new Exception(this.getMsgProvider()
	                  .getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTNAME));//ORG_IO_MSG_ERROR_MUST_POSTNAME
		iep.setName(org.get(0).trim());
		logger.info(iep.getName());
		
		if(!StringUtils.hasText(
				(String)org.get(1))){
			iep.setCode("");//"_default_code""."
		}else
		    iep.setCode(org.get(1).trim());
		
		if(!StringUtils.hasText(
				(String)org.get(2)))
			throw new Exception(this.getMsgProvider()
	                  .getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTTYPE));//ORG_IO_MSG_ERROR_MUST_POSTTYPE
		iep.setType(org.get(2).trim());
		
		if(!StringUtils.hasText(
				(String)org.get(3)))
			throw new Exception(this.getMsgProvider()
	                  .getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT));
		iep.setAccountName(org.get(3).trim());
		
		return iep;
	}

	/*
	 * 进行ImpExpPost特有的检查
	 * 
	 * @see
	 * com.seeyon.v3x.organization.inexportutil.datatableobj.AbstractImpOpr#
	 * pojoCheck(com.seeyon.v3x.organization.services.OrganizationServices,
	 * com.seeyon.v3x.common.metadata.manager.MetadataManager,
	 * com.seeyon.v3x.organization.inexportutil.pojo.ImpExpPojo)
	 */
	@Override
	protected void pojoCheck(OrganizationServices organizationServices,
			MetadataManager metadataManager, ImpExpPojo pojo) throws Exception {
		ImpExpPost postPojo = (ImpExpPost) pojo;
		if (this.getPostType(metadataManager, postPojo.getType()) < 0) {
			throw new Exception(this.getMsgProvider().getMsg(
					MsgContants.ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE));
		}
	}

	protected V3xOrgEntity existEntity(OrganizationServices  organizationServices
			,ImpExpPojo pojo,V3xOrgAccount voa)throws Exception{
		//post  按CODE匹配比较好  "code",iep.getCode()   因为校验重名是好用, "name", iep.getName()
		ImpExpPost  iep=(ImpExpPost)pojo;
		List pms=organizationServices.getOrgManagerDirect()
		              .getEntityList(V3xOrgPost.class.getSimpleName()
		            		        , "name", iep.getName()
		            		        , voa.getId());
		V3xOrgPost pc=null;
		if(pms!=null &&! pms.isEmpty())
			pc=(V3xOrgPost)pms.get(0);
		
		return pc;
	}
	
	protected V3xOrgEntity copyToEntity(OrganizationServices  organizationServices
			                         ,MetadataManager metadataManager,ImpExpPojo pojo
			                         ,V3xOrgEntity ent,V3xOrgAccount voa)throws Exception{
		//ImpExpPost pj=(ImpExpPost)pojo;
		
		return copyToPost(organizationServices,metadataManager,(ImpExpPost)pojo
				        ,(V3xOrgPost)ent,voa);
	}
	
	protected V3xOrgEntity copyToPost(OrganizationServices  organizationServices
            ,MetadataManager metadataManager,ImpExpPost pojo
            ,V3xOrgPost ent,V3xOrgAccount voa)throws Exception{
		if(pojo==null)
			throw new Exception("null ImpExpPost object to cover to V3xOrgPost object");
		
		V3xOrgPost vop=null;
		if(ent!=null){
			vop=organizationServices
			                   .getOrgManagerDirect()
			                   .getPostById(ent.getId());
		}
		if(vop==null){
			vop=new V3xOrgPost();
		}
		
		vop.setName(pojo.getName());
		vop.setCode(pojo.getCode());
		vop.setOrgAccountId(voa.getId());
		
		long typeid=this.getPostType(metadataManager, pojo.getType());
		logger.info("typeid="+typeid);
		if(typeid<0)
			typeid=1;
		vop.setTypeId(typeid);
		
		return vop;
	}
	
	protected void add(OrganizationServices  organizationServices
			,V3xOrgEntity ent)throws Exception{
		logger.info("add post="+ent.getName());
		organizationServices.addPost(
				(V3xOrgPost)ent);
		logger.info("ok add post="+ent.getName());
	}

	protected void update(OrganizationServices  organizationServices
			,V3xOrgEntity ent)throws Exception{
		logger.info("update post="+ent.getName());
		organizationServices.updatePost(
				(V3xOrgPost)ent);
		logger.info("ok update post="+ent.getName());
	}
	
	protected String msg4AddNoDouble(ImpExpPojo pj){
		return this.getMsgProvider()
		                  .getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME)
		                  +pj.getName();
	}
	
}//end class
