/**
 * 
 */
package com.seeyon.v3x.space.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.space.domain.UserFix;

/**
 * @author dongyj
 *
 */
public class UserFixManagerImpl extends BaseHibernateDao<UserFix> implements UserFixManager {
	private static final Log log = LogFactory.getLog(UserFixManagerImpl.class);
	
	private static CacheMap<Long,HashMap<String,String>> userFixCache = null;
	
	public void init(){
		log.info("加载个人空间配置.....");
		CacheAccessable cacheFactory = CacheFactory.getInstance(UserFixManager.class);
		userFixCache = cacheFactory.createMap("defaultPersonalSpaces");
		
		DetachedCriteria criteria = DetachedCriteria.forClass(UserFix.class);
		List<UserFix> list = executeCriteria(criteria, -1, -1);
		for(UserFix fix : list){
			HashMap<String,String> userFix = userFixCache.get(fix.getMemberId());
			if(userFix == null){
				userFix = new HashMap<String,String>();
			}
			userFix.put(fix.getProKey(), fix.getValue());
			userFixCache.put(fix.getMemberId(), userFix);
		}
		log.info("加载个人空间配置结束");
	}
	
	public String getFixValue(Long memberId, String key) {
		Map<String,String> property = userFixCache.get(memberId);
		if(property != null){
			return property.get(key);
		}
		return null;
	}

	public void saveOrUpdate(Long memberId, String key, String value) {
		HashMap<String,String> property = userFixCache.get(memberId);
		if(property == null){
			property = new HashMap<String,String>();
			savePro(memberId,key,value);
			property.put(key, value);
			userFixCache.put(memberId, property);
		}else{
			property.put(key, value);
			updatePro(memberId,key,value);
			userFixCache.put(memberId, property);
		}
	}
	
	private void savePro(Long memberId,String key,String value){
		UserFix fix = new UserFix();
		fix.setIdIfNew();
		fix.setMemberId(memberId);
		fix.setProKey(key);
		fix.setValue(value);
		super.save(fix);
	}
	
	private void updatePro(Long memberId,String key,String value){
		String hql = "update UserFix set value = ? where memberId=? and proKey=? ";
		super.bulkUpdate(hql, null, value,memberId,key);
	}
	
	public void removeUserPro(Long memberId,String key){
		Object[][] where = null;
		if(key == null){
			where = new Object[][]{{"memberId",memberId}};
		}else{
			where = new Object[][]{{"memberId",memberId},{"proKey",key}};
		}
		super.delete(where);
	}

	@Override
	public void updateUserFixBySecurity(Long spaceId,List<V3xOrgMember> memberIds) {
		//清空操作
		String hql = "delete from UserFix where proKey=? and value=?";
		super.bulkUpdate(hql,null,"spaceId",String.valueOf(spaceId));
		List<UserFix>  userFixes = new ArrayList<UserFix>();
		if(CollectionUtils.isNotEmpty(memberIds)){ 
			for(V3xOrgMember member : memberIds){
				UserFix userFix = new UserFix();
				userFix.setIdIfNew();
				userFix.setMemberId(member.getId());
				userFix.setProKey("spaceId");
				userFix.setValue(String.valueOf(spaceId));
				userFixes.add(userFix);
			}
			super.savePatchAll(userFixes);
		}
	}
}
