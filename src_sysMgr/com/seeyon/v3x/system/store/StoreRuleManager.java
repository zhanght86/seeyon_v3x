package com.seeyon.v3x.system.store;

import java.util.List;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2011-12-26
 */
public interface StoreRuleManager {

	public List<StoreRule> listAll();

	public void save(StoreRule bean);

	public void update(StoreRule bean);
	
	public void updateState(long storeRuleId, StoreRule.State state, int result);

	public StoreRule get(long id);

}