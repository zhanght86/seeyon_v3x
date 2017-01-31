/**
 * 
 */
package com.seeyon.v3x.space.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.main.section.panel.SectionPanel;
import com.seeyon.v3x.space.domain.Fragment;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-7
 */
public interface PortletEntityPropertyManager {

	/**
	 * 获取参数
	 * 
	 * @param entityId
	 *            portlet_entity_id
	 * @return
	 */
	public Map<String, String> getPropertys(Long entityId);
	
	/**
	 * 删除
	 * 
	 * @param entityId
	 */
	public void deleteProperties(long entityId);

	/**
	 * 保存参数
	 * 
	 * @param entityId
	 *            portlet_entity_id
	 * @param properties
	 */
	public void save(long entityId, Map<String, String> properties);
	/**
	 * 保存参数
	 * 
	 * @param entityId
	 *            portlet_entity_id
	 * @param properties
	 */
	public void save(long entityId, Map<String, String> properties,String tabIndex);
	
	/**
	 * 
	 * @param srcEntityId
	 * @param destEntityId
	 */
	public void copyPropertys(long srcEntityId, long destEntityId);
	
	/**
	 * 获取个人空间对应栏目中表单业务配置栏目所对应的<b>业务配置</b>ID拼串
	 * @param  fragments  个人空间栏目情况
	 * @return 如：-123123123123,3453234234234...
	 */
	public String getExistedFormBizConfigSectionIds(Map<String,Map<String,Fragment>> fragments);

	/**
	 * 首页修改栏目参数配置
	 * @param entityId fragmentId
	 * @param paramName proName
	 * @param values    value
	 * @return
	 */
	public void updateProperties(long entityId,String[] paramName,String[] values);
	
	/**
	 * 取得栏目下配置的页签。
	 * @param fragmentId 栏目框架Fragment{@link com.seeyon.v3x.space.domain.Fragment} .id
	 * @param ordinal    栏目在fragment中所排的位置
	 * @param panelName  页签名
	 * @param defaultValue 默认栏目
	 * @return
	 */
	public List<SectionPanel> getSectionPanel(Long fragmentId,String ordinal,String panelName,String defaultValue);

	/**
	 * 得到栏目配置。
	 * @param fragmentId
	 * @param ordinal
	 * @return
	 */
	public Map<String,String> getPropertys(Long fragmentId,String ordinal);
	
	public void addCachePropertys(Long entityId,HashMap<String, String> props);
	
	public void removeCachePropertys(Long entityId);
	
	public void reloadProperties(Long entityId);
	/**
	 * 前端多频道删除以后的保存
	 * @param entityId
	 * @param properties
	 */
	public void saveProperties(long entityId,Map<String, String> properties);
}
