package com.seeyon.v3x.flowperm.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.util.NodePolicy;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocElementFlowPermAcl;
import com.seeyon.v3x.edoc.manager.EdocElementFlowPermAclManager;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.exception.FlowPermException;
import com.seeyon.v3x.flowperm.util.Constants;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class FlowPermManagerImpl implements FlowPermManager {
	
	private static final Log log = LogFactory.getLog(FlowPermManagerImpl.class);

	private ConfigManager configManager;
	
	private EdocElementFlowPermAclManager edocElementFlowPermAclManager;
	
	private EdocElementManager edocElementManager;
	
	private MetadataManager metadataManager;

	/**
	 * 未引用的协同和公文节点权限，系统启动时初始化。处于集合中的节点才做更新
	 */
	private CacheMap<Long, HashSet<String>> notRefNodePolicyMap;
	
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setEdocElementFlowPermAclManager(
			EdocElementFlowPermAclManager edocElementFlowPermAclManager) {
		this.edocElementFlowPermAclManager = edocElementFlowPermAclManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void init(){
		CacheAccessable c = CacheFactory.getInstance(getClass());
		notRefNodePolicyMap = c.createMap("notRefNodePolicyMap");
		
		init0(this.configManager.listAllAccountConfigByCategory(MetadataNameEnum.col_flow_perm_policy.name()));
		init0(this.configManager.listAllAccountConfigByCategory(MetadataNameEnum.edoc_send_permission_policy.name()));
		init0(this.configManager.listAllAccountConfigByCategory(MetadataNameEnum.edoc_rec_permission_policy.name()));
		init0(this.configManager.listAllAccountConfigByCategory(MetadataNameEnum.edoc_qianbao_permission_policy.name()));
		//branches_a8_v350_r_gov GOV-3567 王为【信息报送-基础数据-节点权限】系统预置的节点权限，被引用后，还是"未被引用"的状态。 start
		init0(this.configManager.listAllAccountConfigByCategory("info_send_permission_policy"));
		//branches_a8_v350_r_gov GOV-3567 王为 【信息报送-基础数据-节点权限】系统预置的节点权限，被引用后，还是"未被引用"的状态。恩典
	} 
	
	private void init0(List<ConfigItem> configItems){
		if(configItems == null || configItems.isEmpty()){
			return;
		}

		for (ConfigItem item : configItems) {
			if(Strings.isNotBlank(item.getExtConfigValue())){
				try{
					NodePolicy nodePolicy = (NodePolicy) XMLCoder.decoder(item.getExtConfigValue());
					if(nodePolicy.getIsRef()==null){
						nodePolicy.setIsRef(0);
					}
					if(nodePolicy.getIsRef().equals(0)){
						HashSet<String> result = notRefNodePolicyMap.get(item.getOrgAccountId());
						if(result == null){
							result = new HashSet<String>();
						}
						result.add(item.getConfigItem());
						notRefNodePolicyMap.put(item.getOrgAccountId(), result);
					}
				}
				catch(Exception e){
					log.error("",e);
				}
			}
		}
	}

	public boolean isNeedUpdateRef(Long accountId){
		return notRefNodePolicyMap.get(accountId) != null;
	}
	
	public boolean isNeedUpdateRef(String policy, Long accountId){
		HashSet<String> result = notRefNodePolicyMap.get(accountId);
		if(result != null && result.contains(policy)){
			result.remove(policy);
			notRefNodePolicyMap.put(accountId, result);
			return true;
		}
		return false;
	}
	
	public void addNodePolicy(String policy, Long accountId){
		HashSet<String> result = notRefNodePolicyMap.get(accountId);
		if(result == null){
			result = new HashSet<String>();
		}
		result.add(policy);
		notRefNodePolicyMap.put(accountId, result);
	}
	
	public List<FlowPerm> getFlowPermsByCategory(String category, long accountId) throws Exception{

		List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
		Collections.sort(list);
		List<FlowPerm> fList = new ArrayList<FlowPerm>();
		for (ConfigItem item : list) {
			FlowPerm flowPerm = configItemToFlowPerm(item);
			fList.add(flowPerm);
		}
		return fList;
	}
	
	public List<FlowPerm> getFlowpermsByStatus(String category,int status, long accountId) throws Exception{
 		List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
 		Collections.sort(list);
		List<FlowPerm> fList = new ArrayList<FlowPerm>();
		for (ConfigItem item : list) {
			FlowPerm flowPerm = configItemToFlowPerm(item);
			if(null!=flowPerm && null!=flowPerm.getNodePolicy() && null!=flowPerm.getNodePolicy().getIsEnabled() && flowPerm.getNodePolicy().getIsEnabled()!=status){
				continue;
			}

			fList.add(flowPerm);
		}
		return fList;		
		
	}
	
	public List<FlowPerm> getFlowpermsByRef(String category,int isRef, long accountId) throws Exception{
 		List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
 		Collections.sort(list);
		List<FlowPerm> fList = new ArrayList<FlowPerm>();
		for (ConfigItem item : list) {
			FlowPerm flowPerm = configItemToFlowPerm(item);
			
			if(null != flowPerm && null != flowPerm.getNodePolicy()){
				if(flowPerm.getNodePolicy().getIsRef()==null){
					flowPerm.getNodePolicy().setIsRef(0);
				}
				if(flowPerm.getNodePolicy().getIsRef()!=isRef){
					continue;	
				}
				fList.add(flowPerm);
			}
		}
		return fList;		
		
	}
	 
	  //是否是系统节点权限
	  public boolean isSystemFlowPerm(String name,long accountId){
		  if(Strings.isBlank(name)) return false;
		  
		  List<FlowPerm> list = null;
		  try{
			  list = getFlowPermsByCategory("edoc_qianbao_permission_policy", accountId);	
			  if(isSystem(name, list)) return true;
			  if(SystemEnvironment.hasPlugin("edoc")){
					list = getFlowPermsByCategory("edoc_send_permission_policy", accountId);
					if(isSystem(name, list)) return true;
					
					list = getFlowPermsByCategory("edoc_rec_permission_policy", accountId);
					if(isSystem(name, list)) return true;
				}
		  }catch(Exception e){
			  log.error(e);
		  }
						  
		  return false;
	  }

	private boolean isSystem(String name, List<FlowPerm> list) {
		for(FlowPerm fp: list){
			  if(name.equals(fp.getName())){
				  if(fp.getType().intValue() == 0) return true;
			  }
		  }
		return false;
	}
	  
	  
	public List<FlowPerm> getFlowpermsByStatus(String category,int status,boolean isIncludeBegin, int type, long accountId) throws Exception{
		List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
		Collections.sort(list);
		List<FlowPerm> fList = new ArrayList<FlowPerm>();
		for (ConfigItem item : list) {
			FlowPerm flowPerm = configItemToFlowPerm(item);
			NodePolicy nodePolicy = flowPerm.getNodePolicy();
			if(nodePolicy==null || nodePolicy.getIsEnabled()==null){continue;}
			
			if(type == Constants.F_TYPE_COLLABORATION){
				if(nodePolicy.getIsEnabled() != status
						||FlowPerm.Node_Location_Start.intValue() == nodePolicy.getLocation()
						||FlowPerm.Node_Location_End.intValue() == nodePolicy.getLocation()){
					continue;
				}					
			}else if(type == Constants.F_TYPE_EDOC){
				if(nodePolicy.getIsEnabled() != status
						||FlowPerm.Node_Location_Start.intValue() == nodePolicy.getLocation()
						){
					continue;
				}					
			}
		
			fList.add(flowPerm);
		}
		return fList;		
		
	}
	
	private FlowPerm configItemToFlowPerm(ConfigItem item)throws Exception
	{
		if(null!=item){
		FlowPerm flowPerm = new FlowPerm();
		
		NodePolicy nodePolicy = new NodePolicy();
		try{
		nodePolicy = (NodePolicy) XMLCoder.decoder(item.getExtConfigValue());
		}catch(Exception e){
			log.error("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName());
			throw new FlowPermException("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName(),e);
		}
		if (nodePolicy != null) {
			flowPerm.setNodePolicy(nodePolicy);
		}
		flowPerm.setName(item.getConfigItem());
		flowPerm.setFlowPermId(item.getId());
		flowPerm.setCategory(item.getConfigCategory());
		flowPerm.setDescription(item.getConfigDescription());
		flowPerm.setType(Integer.valueOf(item.getConfigType()));

		String label = "";
		if(flowPerm.getType().intValue() == FlowPerm.Node_Type_System){
			label = metadataManager.getMetadataItemLabel(item.getConfigCategory(), item.getConfigItem());
			//branches_a8_v350_r_gov GOV-4014 唐桂林 公文基础数据中收文的节点权限绑定，显示的是登记权限，但是在节点权限中显示的是分发，没有登记 start
			if("node.policy.dengji".equals(label) && (Boolean)SysFlag.is_gov_only.getFlag()) {
				label = "node.policy.dengji"+Functions.suffix();
			}
			//branches_a8_v350_r_gov GOV-4014 唐桂林 公文基础数据中收文的节点权限绑定，显示的是登记权限，但是在节点权限中显示的是分发，没有登记 end
		}
		flowPerm.setLabel(label);
		return flowPerm;
		}
		return null;
	}

	public void addFlowPerm(FlowPerm flowPerm, long accountId) {

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
		item.setOrgAccountId(accountId);
		
		
		configManager.addConfigItem(item);
		
		//更新缓存
		addNodePolicy(flowPerm.getName(), accountId);
		/*
		try {
			metadataManager.addMetadataItem(MetadataNameEnum.valueOf(flowPerm.getCategory()), flowPerm.getName(), flowPerm.getName(), null, flowPerm.getDescription());
		} catch (NoSuchMetadataException e) {
			log.info(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MetadataException e) {
			// TODO Auto-generated catch block
			log.info(e);
			e.printStackTrace();
		}*/
	}
	
	/**
	 * @param flowPerm
	 * @param elementList
	 */
	public void addEdocFlowPerm(FlowPerm flowPerm, long accountId){
		
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
		item.setOrgAccountId(accountId);
		
		configManager.addConfigItem(item);
		
		/*
		try {
			metadataManager.addMetadataItem(MetadataNameEnum.valueOf(flowPerm.getCategory()), flowPerm.getName(), flowPerm.getName(), null, flowPerm.getDescription());
		} catch (NoSuchMetadataException e) {
			log.info(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MetadataException e) {
			log.info(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		List<EdocElement> elementList = edocElementManager.getEdocElementsByStatus(1,1,10000);
		
		List<EdocElementFlowPermAcl> list = new ArrayList<EdocElementFlowPermAcl>();
		
		if(null!=elementList && elementList.size()>0){
		
		for(int i=0;i<elementList.size();i++){
			EdocElementFlowPermAcl acl = new EdocElementFlowPermAcl();
				acl.setIdIfNew();
				acl.setFlowPermId(item.getId());
				acl.setEdocElement(elementList.get(i));
				acl.setAccess(0);
				list.add(acl);
			}

		edocElementFlowPermAclManager.saveEdocElementFlowPermAcls(list);
		}
	}

	public void updateFlowPerm(FlowPerm flowPerm,String oldCategory,String oldName, long accountId) {
				
		ConfigItem configItem = null;
		configItem = configManager.getConfigItem(oldCategory,oldName,accountId);
		NodePolicy nodePolicy = flowPerm.getNodePolicy();
		String xml = "";
		if(null!=nodePolicy){
			xml = XMLCoder.encoder(nodePolicy);
		}
		configItem.setConfigItem(flowPerm.getName());
		configItem.setConfigDescription(flowPerm.getDescription());
		configItem.setExtConfigValue(xml);
		configManager.updateConfigItem(configItem);
		
		String type = configItem.getConfigType();

	}
	
	public void updateFlowPerm(FlowPerm flowPerm) {
		
		ConfigItem configItem = null;
		configItem = configManager.getConfigItemByCriteria(flowPerm.getFlowPermId());
		NodePolicy nodePolicy = flowPerm.getNodePolicy();
		String xml = "";
		if(null!=nodePolicy){
			xml = XMLCoder.encoder(nodePolicy);
		}
		configItem.setConfigItem(flowPerm.getName());
		configItem.setConfigDescription(flowPerm.getDescription());
		configItem.setExtConfigValue(xml);
		configManager.updateConfigItem(configItem);
	}

	public void deleteFlowPerm(String configCategory,String configItem, long accountId) {
		 configManager.deleteConfigItem(configCategory, configItem, accountId);
	}

	public FlowPerm getFlowPerm(String configCategory, String configItem, long accountId) throws Exception {
		ConfigItem item = configManager.getConfigItem(configCategory,
				configItem, accountId);
		FlowPerm perm = configItemToFlowPerm(item);
		
		return perm;
	}
	
	public FlowPerm getFlowPermByStatus(String configCategory, String configItem , Integer status, long accountId) throws Exception {
		ConfigItem item = configManager.getConfigItem(configCategory,
				configItem, accountId);
		FlowPerm perm = configItemToFlowPerm(item);
		if(null!=perm && perm.getIsEnabled().intValue() == status.intValue()){//如果权限不为空,并且权限的状态和传入的一致
			return perm;		
		}
		return null; 
	}
	
	public String deleteFlowPerm(Long id){
		ConfigItem configItem = configManager.getConfigItemByCriteria(id);
		
		if(null!=configItem){
			NodePolicy nodePolicy = (NodePolicy)XMLCoder.decoder(configItem.getExtConfigValue());
			if(null!=nodePolicy){//判断对象是否为空(升级脚本时有可能发生为空的现象)
				if(nodePolicy.getIsRef() != null) {
					int ref = nodePolicy.getIsRef();
					if(ref>0){
						log.error("权限以被引用,不允许删除");
						return "<script>alert(parent._('flowpermLang.flowperm_referenced_forbidden'));</script>";
					}
				}
			}
		}
		/*不删除metadatItem的数据
		if(null!=configItem){
		try {
			MetadataItem item = metadataManager.getMetadataItem(MetadataNameEnum.valueOf(configItem.getConfigCategory()), configItem.getConfigItem()); 
			//判断是否存在该权限的原数据
			if(null!=item){
				Long metadataItemId = item.getId(); 
				metadataManager.deleteMetadataItem(MetadataNameEnum.valueOf(configItem.getConfigCategory()), metadataItemId);
			}
		} catch (NoSuchMetadataException e) {
			// TODO Auto-generated catch block
			log.info(e);
			e.printStackTrace();
		} catch (MetadataException e) {
			// TODO Auto-generated catch block
			log.info(e);
			e.printStackTrace();
		}
		}
		*/
		configManager.deleteCriteria(id);
		edocElementFlowPermAclManager.deleteEdocElementFlowPermAcl(id);
		
		this.isNeedUpdateRef(configItem.getConfigItem(), configItem.getOrgAccountId());
		
		return null;
	}
	public Boolean isActionAllowed(Long uid, String action){
		ConfigItem item = configManager.getConfigItemByCriteria(uid);
		Boolean result = false;
		
		if(null!=item  && !"".equals(action) && null!=action){
			
			NodePolicy nodePolicy = (NodePolicy)XMLCoder.decoder(item.getExtConfigValue());
			String common = nodePolicy.getCommonAction();
			String basic = nodePolicy.getBaseAction();
			String advanced = nodePolicy.getAdvancedAction();
			List<String> list = new ArrayList<String>();

			if(null!=common && !"".equals(common)){
				for(String str:common.split(",")){
					list.add(str);
				}
			}
			if(null!=basic && !"".equals(basic)){
				for(String str:basic.split(",")){
					list.add(str);
				}
			}
			if(null!=advanced && !"".equals(advanced)){
				for(String str:advanced.split(",")){
					list.add(str);
				}
			}
			for(String str:list){
				if(action.equals(str)){
					result = true;
				}
			}
		}
			return result;
	}
	
	
	public String getActionList(Long uid, String appType){
		
		ConfigItem item = configManager.getConfigItemByCriteria(uid);
		String actionList = "";
		if(null!=item ){
			NodePolicy nodePolicy = (NodePolicy)XMLCoder.decoder(item.getExtConfigValue());
				if(null!=appType && !"".equals(appType) && "basic".equals(appType) || "basicOperation".equals(appType)){
					actionList = nodePolicy.getBaseAction();
				}
				else if(null!=appType && !"".equals(appType) && "advanced".equals(appType) || "advancedOperation".equals(appType)){
					actionList = nodePolicy.getAdvancedAction();
				}else if(null!=appType && !"".equals(appType) && "common".equals(appType) || "commonOperation".equals(appType)){
					actionList = nodePolicy.getCommonAction();
				}
			return actionList;
			}else{
				return "";
			}
		}

	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}
	
	public FlowPerm getFlowPerm(Long id) throws Exception{
		
		ConfigItem item = configManager.getConfigItemByCriteria(id);		
		FlowPerm perm = configItemToFlowPerm(item);
		return perm;
	}
	
	/**
	 * if the return configItem not be null,thus there is already item named configItem in the db. 
	 * @param configCategory 
	 * @param configItem
	 * @return
	 */
	public boolean checkName(String configCategory,String configItem, long accountId) throws Exception{
			
		boolean bool = false;
		List<String> labelList = getItemLabelsByCategory(configCategory, accountId);
		if(labelList.contains(configItem)){
			return true;
		}
		//判断在系统中是否可以查找到已启用的权限
		FlowPerm perm = this.getFlowPermByStatus(configCategory, configItem, Constants.F_status_enabled, accountId);
		if(null!=perm){
			bool = true;
		}else{
			bool = false;
		}
		return bool;
	}
	
	/**
     * branches_a8_v350_r_gov GOV-2863 常屹 新增 
     * 节点权限重名通过ajax进行判断
     */
	public boolean checkNameByAjax(String configCategory,String configItem) throws Exception{
		long accountId = CurrentUser.get().getLoginAccount();  
		boolean flag = checkName(configCategory,configItem,accountId);
		return flag;
	}
	
	
	private List<String> getItemLabelsByCategory(String configCategory, long accountId) throws Exception{
		List<String> list = new ArrayList<String>();
		List<FlowPerm> permList = getFlowpermsByStatus(configCategory,Constants.F_status_enabled, accountId);//查找出改类别下所有启用的的权限
		String label = "";
		String value = "";
		ResourceBundle r = null;
		
		//将所有系统预置权限的国际化名称存放在集合中
		for(FlowPerm perm:permList){
			if(perm.getType() == Constants.F_type_system){
				label = metadataManager.getMetadataItemLabel(perm.getCategory(), perm.getName());
				if(null!=label && !"".equals(label)){
				r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
					value = ResourceBundleUtil.getString(r, label);
				}
			}
			list.add(value);
		}
		return list;
	}
	
	/**
	 * 
	 * 方法描述： 更新FlowPerm 的 isRef值 
	 * @param configCategory : 分类 col_flow_perm_policy - 协同 || "edoc_send_permission_policy" 发文 ||  "edoc_rec_permission_policy" 收文 || "edoc_qianbao_permission_policy" 签报 
	 * @param accountId : accountId
	 * @param isRef : 引用传【 1 】  不引用传【 0 】
	 *
	 */
	public void refFlowPermForSmallKing(String configCategory, long accountId , Integer isRef)throws Exception{
		List<FlowPerm> permList = this.getFlowPermsByCategory(configCategory, accountId);
		for(FlowPerm flowPerm : permList){
			
			NodePolicy node  = flowPerm.getNodePolicy();
			node.setIsRef(isRef);
			String xml = XMLCoder.encoder(node);
			ConfigItem item = configManager.getConfigItem(flowPerm.getFlowPermId());
			item.setExtConfigValue(xml);
			configManager.updateConfigItemOnlyInMemory(item);
			
		}
	}
	
	/**
	 * 
	 * 方法描述： 更新FlowPerm 的 isRef值 
	 * @param configCategory : 分类 col_flow_perm_policy - 协同 || "edoc_send_permission_policy" 发文 ||  "edoc_rec_permission_policy" 收文 || "edoc_qianbao_permission_policy" 签报 
	 * @param accountId : accountId
	 * @param isRef : 引用传【 1 】  不引用传【 0 】
	 *
	 */
	public void refFlowPerm(Long id, Long accountId, Integer isRef)throws Exception{
		ConfigItem configItem = null;
		FlowPerm flowPerm = this.getFlowPerm(id);
		NodePolicy nodePolicy = flowPerm.getNodePolicy();
		configItem = configManager.getConfigItemByCriteria(id);
		String xml = "";
		if(null != nodePolicy){
			nodePolicy.setIsRef(isRef);
			xml = XMLCoder.encoder(nodePolicy);
		}
		configItem.setExtConfigValue(xml);
		configManager.updateConfigItem(configItem);
	}
}