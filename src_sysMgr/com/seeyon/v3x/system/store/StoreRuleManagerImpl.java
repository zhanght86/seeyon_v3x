/**
 * 
 */
package com.seeyon.v3x.system.store;

import java.util.HashMap;
import java.util.List;

import com.seeyon.v3x.common.dao.BaseDao;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2011-12-26
 */
public class StoreRuleManagerImpl extends BaseDao<StoreRule> implements StoreRuleManager {
	
	public List<StoreRule> listAll(){
		return super.find("from StoreRule", new HashMap());
	}
	
	public void save(StoreRule bean){
		super.save(bean);
	}
	
	public void update(StoreRule bean){
		super.update(bean);
	}
	
	public StoreRule get(long id){
		return super.get(id);
	}
	
	public void updateState(long storeRuleId, StoreRule.State state, int result){
		super.bulkUpdate("update StoreRule set state=?,result=? where id=?", null, state.ordinal(), result, storeRuleId);
	}
}
