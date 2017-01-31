package com.seeyon.v3x.formbizconfig.manager;

import java.util.List;

import com.seeyon.v3x.formbizconfig.domain.V3xBizAuthority;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfig;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfigItem;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.menu.domain.Menu;


/**
 * 业务生成器配置
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */
public interface V3xBizConfigManager {
	
	/**
	 * 查询业务配置记录
	 */
	public List<V3xBizConfig> findAllByCondition(SearchModel searchModel);
	
	public V3xBizConfig findBizConfigById (Long bizConfigId);
	
	public V3xBizConfig findBizConfigByMenuId (Long menuId);
	
	public List<V3xBizConfigItem> findBizConfigItemByBizConfigId(Long bizConfigId);
	
	public List<V3xBizAuthority> findBizAuthorityByBizConfigId(Long bizConfigId);
	
	public List<Long> findAccessMenuIdsByScopeIds(List<Long> entIdsList);

	/**
	 * 保存业务配置记录
	 */
	public void saveBizConfig(V3xBizConfig v3xBizConfig, List<V3xBizConfigItem> v3xBizConfigItemList, List<V3xBizAuthority> v3xBizAuthorityList);
	
	/**
	 * 修改业务配置记录
	 */
	public void updateBizConfig(V3xBizConfig v3xBizConfig, List<V3xBizConfigItem> v3xBizConfigItemList, List<V3xBizAuthority> v3xBizAuthorityList);
	
	/**
	 * 批量删除多条业务配置记录
	 */
	public void deleteBizConfig(List<Long> bizConfigIds);
	
	public boolean checkBizConfigName(String name, Long accountId, String bizConfigId);
	
	public List<Menu> getAllFormBindMenus();

}