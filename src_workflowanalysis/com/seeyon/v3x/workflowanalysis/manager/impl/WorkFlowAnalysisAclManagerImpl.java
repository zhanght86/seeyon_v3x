package com.seeyon.v3x.workflowanalysis.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.workflowanalysis.dao.WorkFlowAnalysisAclDao;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysisAcl;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;

/**
 * 保存授权的缓存组件
 * Map<人员ID，Map<单位ID，UniqueList<Long>{模板ID}>>，
 * 如果是具体模板：Map.value为List
 * 如果是全部模板：Value为WorkFlowAnalysis.AllTemplete = 1，前台访问的时候再具体去取能够访问的模板。
 * @author mujun
 */
public class WorkFlowAnalysisAclManagerImpl implements WorkFlowAnalysisAclManager{
	private static final Log log = LogFactory.getLog(WorkFlowAnalysisAclManager.class);
	
	private WorkFlowAnalysisAclDao workFlowAnalysisAclDao;
	private TempleteManager templeteManager ;
	

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public void setWorkFlowAnalysisAclDao(WorkFlowAnalysisAclDao workFlowAnalysisAclDao) {
		this.workFlowAnalysisAclDao = workFlowAnalysisAclDao;
	}

	private final CacheAccessable cacheFactory = CacheFactory.getInstance(WorkFlowAnalysisAclManagerImpl.class);
	//<memberId, <AccountId, List<Templete.id>>
	private CacheMap<Long, HashMap<Long, UniqueList<Long>>> analysisAclMap;
	
	public void init(){
		if(analysisAclMap == null){
			analysisAclMap  = cacheFactory.createMap("workFlowAnalysisAcl");
		}
		
		analysisAclMap.clear();
		
		//1.找到数据库的所有信息
		List<WorkFlowAnalysisAcl> all = workFlowAnalysisAclDao.getAllWorkFlowAnalysisAcl();
		for(WorkFlowAnalysisAcl acl: all){
			addToCache(acl);
		}
	}
	
	private void addToCache(WorkFlowAnalysisAcl acl){
		String memberIds = acl.getMemberIds();
		String templeteIds = acl.getTempleteIds();
		if(Strings.isBlank(memberIds) || Strings.isBlank(templeteIds)){
			return;
		}
		
		String[] memberId = memberIds.split("[,]");
		String[] templeteId = templeteIds.split("[,]");
		Long accoutId = acl.getOrgAccountId();
		
		for(String smid : memberId){
			Long mid = Long.valueOf(smid);
			HashMap<Long, UniqueList<Long>> value = analysisAclMap.get(mid);
			if(value == null){
				value = new HashMap<Long, UniqueList<Long>>();
			}
			
			UniqueList<Long> valueT = value.get(accoutId);
			if(valueT == null){
				valueT = new UniqueList<Long>();
				value.put(accoutId, valueT);
			}
			
			if(valueT.contains(WorkFlowAnalysis.AllTemplete)){
				continue;
			}
			
			for(String tid : templeteId){
				if(new Long(tid).equals(WorkFlowAnalysis.AllTemplete)){
					valueT.clear();
					valueT.add(WorkFlowAnalysis.AllTemplete);
					continue;
				}
				
				valueT.add(new Long(tid));
			}
			
			analysisAclMap.put(mid, value);
		}
	}
	public List<Long> getTempleteIdByUserId(Long userId,Integer categoryType){
		if(userId == null )
			log.error("getTempleteByUserId.userId 为空！");
		if(analysisAclMap == null )
			log.error("getTempleteByUserId.analysisAclMap 为空！");
		
		List<Long> allTempleteIds = new ArrayList<Long>();
		
		if(analysisAclMap==null)
			return new ArrayList<Long>();
		
		HashMap<Long, UniqueList<Long>> map = analysisAclMap.get(userId);
		if(map!=null){
			Set<Long> accountIds = map.keySet();
			//所有模板
			if(accountIds != null){
				for(Long accoutId :accountIds){
					UniqueList<Long> list = map.get(accoutId);
					if(list != null){
						allTempleteIds.addAll(list);
					}
				}
			}
		}
		return allTempleteIds;
	}
	public List<Long> getLoginAccountTempleteIdByUserId(Long userId,Integer categoryType){
		if(userId == null )
			log.error("getTempleteByUserId.userId 为空！");
		if(analysisAclMap == null )
			log.error("getTempleteByUserId.analysisAclMap 为空！");
		
		List<Long> allTempleteIds = new ArrayList<Long>();
		
		if(analysisAclMap==null)
			return new ArrayList<Long>();
		
		HashMap<Long, UniqueList<Long>> map = analysisAclMap.get(userId);
		if(map!=null){
			Set<Long> accountIds = map.keySet();
			//所有模板
			if(accountIds != null){
				long loginAccountId = CurrentUser.get().getLoginAccount();
				for(Long accoutId :accountIds){
					if(accoutId.longValue() != loginAccountId){continue;}
					UniqueList<Long> list = map.get(accoutId);
					if(list != null){
						allTempleteIds.addAll(list);
					}
				}
			}
		}
		return allTempleteIds;
	}
	/**
	 * 根据用户名来获取能够访问到的模板ID
	 * @param userId
	 * @return
	 */
	public List<Templete> getTempleteByUserId(Long userId,Integer categoryType){
		if(userId == null )
			log.error("getTempleteByUserId.userId 为空！");
		if(analysisAclMap == null )
			log.error("getTempleteByUserId.analysisAclMap 为空！");
		
		List<Templete> allTempletes = new ArrayList<Templete>();
		if(analysisAclMap==null)
			return allTempletes;
		HashMap<Long, UniqueList<Long>> map = analysisAclMap.get(userId);
		if(map!=null){
			Set<Long> accountIds = map.keySet();
			//所有模板
			Integer singleFetch = 1000 ;
			if(accountIds != null){
				for(Long accoutId :accountIds){
					UniqueList<Long> list = map.get(accoutId);
					if(list != null){
						if(list.size()==1 && list.get(0).equals(WorkFlowAnalysis.AllTemplete)){//全部
							List<Templete> templetes = templeteManager.getAllSystemTempletesByAcl(accoutId, categoryType);
							allTempletes.addAll(templetes);
						}else{
							if(list.size()>1000){//每次取1000个。循环多次取
								int count = list.size()/singleFetch;
								for(int i=1 ; i<count+2 ;i++){
									List<Long> sl = null;
									if(i == count+1){
										sl = list.subList((i-1)*singleFetch, list.size());
									}else{
										sl = list.subList((i-1)*singleFetch, i*singleFetch);
									}
									allTempletes.addAll(templeteManager.getAllSystemTempletesByEntityIds(sl, categoryType));
								}
							}else{
								allTempletes.addAll(templeteManager.getAllSystemTempletesByEntityIds(list, categoryType));
							}
						}
					}
				}
			}
		}
		return allTempletes;
	}
	
	public List<Long> getAnalysisAclsByUserId(Long orgAccountId, Long userId) {
		HashMap<Long, UniqueList<Long>> v= this.analysisAclMap.get(userId);
		if(v == null){
			return null;
		}
		
		return v.get(orgAccountId);
	}
	
	public List<WorkFlowAnalysisAcl> getWorkFlowAnalysisAclByAccountId(Long orgAccountId) {
		return workFlowAnalysisAclDao.getWorkFlowAnalysisAclByAccountId(orgAccountId);
	}
	
	public List<WorkFlowAnalysisAcl> getAllWorkFlowAnalysisAcl() {
		return workFlowAnalysisAclDao.getAllWorkFlowAnalysisAcl();
	}

	public void saveWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl) {
		workFlowAnalysisAclDao.saveWorkFlowAnalysisAcl(acl);
		addToCache(acl);
	}

	public WorkFlowAnalysisAcl queryAuthorizationById(Long id) {
		return workFlowAnalysisAclDao.queryAuthorizationById(id);
	}

	public void updateWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl) {
		workFlowAnalysisAclDao.updateWorkFlowAnalysisAcl(acl);
		init();
	}

	public void removeWorkFlowAnalysisAclById(Long id) {
		workFlowAnalysisAclDao.removeWorkFlowAnalysisAclById(id);
		init();
	}
	public WorkFlowAnalysisAcl get(Long id){
		return workFlowAnalysisAclDao.get(id);
	}
}
