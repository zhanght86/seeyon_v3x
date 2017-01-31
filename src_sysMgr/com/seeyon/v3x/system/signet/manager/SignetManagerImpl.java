package com.seeyon.v3x.system.signet.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.system.signet.dao.DocumentSignatureDao;
import com.seeyon.v3x.system.signet.dao.SignetDao;
import com.seeyon.v3x.system.signet.domain.V3xDocumentSignature;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.util.TextEncoder;

public class SignetManagerImpl implements SignetManager {
	
	private SignetDao signetDao;
	
	private DocumentSignatureDao documentSignatureDao;
	
	/**
	 * 加密说明：字段password、markBody是加密的
	 */
	private CacheMap<Long,V3xSignet> allV3xSignet = null;
	
	public void setDocumentSignatureDao(DocumentSignatureDao documentSignatureDao) {
		this.documentSignatureDao=documentSignatureDao;
	}
	
	public void setSignetDao(SignetDao signetDao) {
		this.signetDao = signetDao;
	}

	/**
	 * 加载所有印章管理
	 */
	public void init() {
		CacheAccessable cacheFactory = CacheFactory.getInstance(SignetManager.class);
		allV3xSignet = cacheFactory.createLinkedMap("allV3xSignet");
	
		allV3xSignet.setDataLoader(new AbstractMapDataLoader<Long, V3xSignet>(allV3xSignet) {
			@Override
			protected Map<Long, V3xSignet> loadLocal() {
				List<V3xSignet> temp = signetDao.findAll();
				Map<Long,V3xSignet> map = new HashMap<Long,V3xSignet>();
				if(!temp.isEmpty()){
					for (V3xSignet sig : temp) {
						map.put(sig.getId(), sig);
					}
				}
				return map;
			}

			@Override
			protected V3xSignet loadLocal(Long k) {
				return signetDao.getSignet(k);
			}
		});
		allV3xSignet.reload();
	}

	public void delete(long id) {
		signetDao.delete(id);
		allV3xSignet.remove(id);
/*		V3xSignet temp = getSignet(id);
		if(temp != null){
			allV3xSignet.remove(temp);
		}*/
	}

	public List<V3xSignet> findAll() throws Exception {
		List<V3xSignet> result = new ArrayList<V3xSignet>(allV3xSignet.size());
		for (V3xSignet sig : allV3xSignet.values()) {
			result.add(sig);
		}
		return result;
	}
	
	
	public void deleteByAccountId(Long accountId){
		signetDao.deleteByAccountId(accountId);
	}

	public void save(V3xSignet signet) throws Exception {
		try{
			signetDao.create(signet);
			allV3xSignet.put(signet.getId(),signet);
		}catch(Exception e){
			allV3xSignet.remove(signet.getId());
		}
	}
	
	public void save(V3xDocumentSignature v3xDocumentSignature)	throws Exception {
		documentSignatureDao.save(v3xDocumentSignature);
	}

	public void update(V3xSignet signet) throws Exception {
		signetDao.update(signet);
		allV3xSignet.notifyUpdate(signet.getId());
	}

	public V3xSignet getSignet(Long id) {
		return allV3xSignet.get(id);
/*		for (int i = 0; i < allV3xSignet.size(); i++) {
			V3xSignet temp = allV3xSignet.get(i);
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		
		return null;*/
	}

	/**
	 * 通过 AJAX 进行密码判断
	 * @param id
	 * @param oldPassword 原密码，明文
	 * @param isOnlyEnable
	 * @return
	 */
	public int getSignet(long id, String oldPassword, boolean isOnlyEnable) {
		if (isOnlyEnable == true) {
			V3xSignet signet = this.getSignet(id);
			if(signet != null && !oldPassword.equals(TextEncoder.decode(signet.getPassword()))){
				return 0;
			}
		}
		
		return 1;
	}
		
	//	获取指定文档上面的签章信息
	public java.util.List<V3xDocumentSignature> findDocumentSignatureByDocumentId(String docId)	throws Exception
	{
		return documentSignatureDao.findByRecordId(docId);
	}
	
	public V3xSignet findByMarknameAndPassword(String markname,String pwd) {
		List<V3xSignet> signets=new ArrayList<V3xSignet>();
		String userId=String.valueOf(CurrentUser.get().getId());
		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getUserName()==null)continue;
			if(temp.getUserName().equals(userId)){
				signets.add(temp);
			}
		}
		for (V3xSignet temp : signets) {
			if(temp.getMarkName() == null || temp.getPassword() == null) continue; //对NULL值进行防护处理
			if(temp.getMarkName().equals(markname) && TextEncoder.decode(temp.getPassword()).equals(pwd) && 
					temp.getUserName().equals(userId)){
				return temp;
			}
		}
		return null;
	}

	public List<V3xSignet> findAllAccountID(Long accountID) throws Exception {
		List<V3xSignet> result = new ArrayList<V3xSignet>();
/*		for (int i = 0; i < allV3xSignet.size(); i++) {
			V3xSignet temp = allV3xSignet.get(i);
			if(temp.getOrgAccountId().equals(accountID)){
				result.add(temp);
			}
		}*/
		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getOrgAccountId().equals(accountID)){
				result.add(temp);
			}
		}	
		return result;
	}

	public boolean insertSignet(Long srcContentId,Long newContentId)
	{
		if(srcContentId==null || newContentId==null){return false;}
		try{
			java.util.List<V3xDocumentSignature> sl=findDocumentSignatureByDocumentId(srcContentId.toString());
			if(sl.size()<=0){return true;}//没有印章不需要复制
			java.util.List<V3xDocumentSignature> slNew=findDocumentSignatureByDocumentId(newContentId.toString());
			if(slNew.size()>0){return true;}//印章已经复制过
			for(V3xDocumentSignature ds:sl)
			{
				V3xDocumentSignature tempDs=new V3xDocumentSignature();
				tempDs.setIdIfNew();
				tempDs.setHostname(ds.getHostname());
				tempDs.setMarkguid(ds.getMarkguid());
				tempDs.setMarkname(ds.getMarkname());
				tempDs.setRecordId(newContentId.toString());
				tempDs.setSignDate(ds.getSignDate());
				tempDs.setUsername(ds.getUsername());
				documentSignatureDao.save(tempDs);
			}			
		}catch(Exception e)
		{
			return false;
		}
		return true;
	}

    /**
     * 得到某人印章
     * @param memberId
     * @return
     * @throws Exception
     */
    public List<V3xSignet> findSignetByMemberId(Long memberId) {
		List<V3xSignet> result = new ArrayList<V3xSignet>();

		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getUserName()==null)continue;
			if(temp.getUserName().equals(memberId.toString())&&temp.getOrgAccountId().longValue()==CurrentUser.get().getLoginAccount()){
				result.add(temp);
			}
		}		
		return result;
    }
    
    /**
     * 判断某人是否有印章
     * @param memberId
     * @return
     * @throws Exception
     */
    public boolean hasSignet(Long memberId){
/*		for (int i = 0; i < allV3xSignet.size(); i++) {
			V3xSignet temp = allV3xSignet.get(i);
			if(temp.getUserName().equals(memberId.toString())){
				return true;
			}
		}*/
    	if(memberId==null) return false;
		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getUserName()==null) continue;
			if(temp.getUserName().equals(memberId.toString())){
				return true;
			}
		}	
		return false;
    }
	@Override
	public void clearSignet(long memberId) throws Exception {
		for (V3xSignet sig : allV3xSignet.values()) {
			if(sig.getUserName()==null) continue;
			if(sig.getUserName().equals(memberId+"")){
				sig.setUserName("");
				sig.setPassword("");
				update(sig);
			}
		}	
	}   
    public boolean checkMarknameIsDuple(String markName) {
/*		for (int i = 0; i < allV3xSignet.size(); i++) {
			V3xSignet temp = allV3xSignet.get(i);
			if(temp.getMarkName().equals(markName)){
				return true;
			}
		}*/
		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getMarkName().equals(markName)){
				return true;
			}
		}			
		return false;
    }
    public boolean checkMarknameIsDupleInAccountScope(String markName,Long orgAccountId) {
		for (V3xSignet temp : allV3xSignet.values()) {
			if(temp.getMarkName().equals(markName)
					&& temp.getOrgAccountId().equals(orgAccountId)){
				return true;
			}
		}			
		return false;
    }

	/**
	 * 
	 * 方法描述： ajax方法，判断是否被取消了权限
	 *
	 */
	public boolean ajaxIsCancelled(String id){
		V3xSignet signet = this.getSignet(Long.parseLong(id));
		if(signet != null && signet.getUserName().equals(String.valueOf(CurrentUser.get().getId()))){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean checkMarknameIsDupleInAccountScope(String markName) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public List<V3xSignet> findAllByAccountId(Long accountId) throws Exception {
		List<V3xSignet> result = new ArrayList<V3xSignet>(allV3xSignet.size());
		for (V3xSignet sig : allV3xSignet.values()) {
			if(sig.getOrgAccountId().equals(accountId)){
				result.add(sig);
			}
		}
		return result;
	}

}
