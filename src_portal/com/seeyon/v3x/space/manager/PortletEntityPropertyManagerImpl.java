/**
 * 
 */
package com.seeyon.v3x.space.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.main.section.panel.SectionPanel;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-7
 */
public class PortletEntityPropertyManagerImpl extends
		BaseHibernateDao<PortletEntityProperty> implements
		PortletEntityPropertyManager {
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(PortletEntityPropertyManagerImpl.class);

//	private Map<Long, Map<String, String>> cache = new HashMap<Long, Map<String,String>>();
	private CacheMap<Long, HashMap<String, String>> cache;
	
	private final Object lock = new Object();
	
	public void init(){
		cache = cacheFactory.createMap("Portletcache");
		
		cache.setDataLoader(new AbstractMapDataLoader<Long, HashMap<String, String>>(cache) {

			@Override
			protected Map<Long, HashMap<String, String>> loadLocal() {
				Map<Long, HashMap<String, String>> map = new HashMap<Long, HashMap<String, String>>();
				List<PortletEntityProperty> all = PortletEntityPropertyManagerImpl.super.getAll();
				for (PortletEntityProperty property : all) {
					Long entityId = Long.parseLong(property.getEntityId());
					
					HashMap<String, String> result = map.get(entityId);
					if(result == null){
						result = new HashMap<String, String>();
//						cache.put(entityId, result);
					}
					
					result.put(property.getPropertyName(), property.getPropertyValue());
//					cache.notifyUpdate(entityId);
					map.put(entityId, result);
				}
				return map;
			}

			@Override
			protected HashMap<String, String> loadLocal(Long k) {
				HashMap<String, String>  temp = new HashMap<String, String>();
				DetachedCriteria detachedCriteria = DetachedCriteria.forClass(PortletEntityProperty.class)
					.add(Expression.eq("entityId", String.valueOf(k)));
				
				List<PortletEntityProperty> ps = PortletEntityPropertyManagerImpl.super.executeCriteria(detachedCriteria, -1, -1);
				for (PortletEntityProperty parameter : ps) {
					temp.put(parameter.getPropertyName(), parameter.getPropertyValue());
				}
				return temp;
			}
		});
		cache.reload();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getPropertys(Long entityId) {
		HashMap<String, String> temp = cache.get(entityId);
		
		//缓存中没有，从数据库中查询
		if(temp == null){
/*			temp = new HashMap<String, String>();
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(PortletEntityProperty.class)
				.add(Expression.eq("entityId", String.valueOf(entityId)))
			;
			
			List<PortletEntityProperty> ps = super.executeCriteria(detachedCriteria, -1, -1);
			for (PortletEntityProperty parameter : ps) {
				temp.put(parameter.getPropertyName(), parameter.getPropertyValue());
			}
			
			synchronized (lock) {
				cache.put(entityId, temp);
			}*/
			cache.reload(entityId);
			temp = cache.get(entityId);
			if(temp==null) return  new HashMap<String, String>();
		}
		
		return new HashMap<String, String>(temp);
	}
	
	public void deleteProperties(long entityId){
		super.delete(PortletEntityProperty.class, new Object[][]{{"entityId", String.valueOf(entityId)}});
		synchronized (lock) {
			removeCachePropertys(entityId);
		}
	}

	public void saveProperties(long entityId,Map<String, String> properties){
		super.delete(PortletEntityProperty.class, new Object[][]{{"entityId", String.valueOf(entityId)}});
		Set<Map.Entry<String, String>> entitis = properties.entrySet();
		List<PortletEntityProperty> props = new ArrayList<PortletEntityProperty>();
		for(Map.Entry<String, String> entry : entitis){
			PortletEntityProperty p = new PortletEntityProperty();
			p.setNewId();
			p.setEntityId(String.valueOf(entityId));
			p.setPropertyName(entry.getKey());
			p.setPropertyValue(entry.getValue());
			props.add(p);
		}
		super.savePatchAll(props);
		
		synchronized (lock) {
//			cache.remove(entityId);
			cache.put(entityId, (HashMap<String,String>)properties);
		}
	}
	
	public void save(long entityId, Map<String, String> properties) {
		super.delete(PortletEntityProperty.class, new Object[][]{{"entityId", String.valueOf(entityId)}});
		
		Set<Map.Entry<String, String>> entitis = properties.entrySet();
		for (Map.Entry<String, String> entry : entitis) {
			if(java.util.regex.Pattern.matches(PortletEntityProperty.PropertyName_No_Save_Pattern, entry.getKey())) {
				continue;
			}
			PortletEntityProperty p = new PortletEntityProperty();
			p.setNewId();
			p.setEntityId(String.valueOf(entityId));
			p.setPropertyName(entry.getKey());
			p.setPropertyValue(entry.getValue());
			super.save(p);
		}
		
		PortletEntityProperty propertity = new PortletEntityProperty();
		propertity.setEntityId(String.valueOf(entityId));
		List<PortletEntityProperty> portletProperty = super.findByExample(propertity);
		Map<String,String> cacheProperties = new HashMap<String, String>();
		for(PortletEntityProperty property : portletProperty){
			cacheProperties.put(property.getPropertyName(), property.getPropertyValue());
		}
		synchronized (lock) {
			cache.put(entityId, (HashMap<String, String>)cacheProperties);
		}
	}
	
	public void save(long entityId, Map<String, String> properties,String tabIndex) {
		String sql = "from PortletEntityProperty where entityId = ? and propertyName like ?";
		List<PortletEntityProperty> tabProperties = super.find(sql,String.valueOf(entityId),"%"+tabIndex);
		if(tabProperties!=null){
			for(PortletEntityProperty property:tabProperties){
				super.delete(property);
			}
		}
		Set<Map.Entry<String, String>> entitis = properties.entrySet();
		for (Map.Entry<String, String> entry : entitis) {
			if(java.util.regex.Pattern.matches(PortletEntityProperty.PropertyName_No_Save_Pattern, entry.getKey())) {
				continue;
			}
			PortletEntityProperty p = new PortletEntityProperty();
			p.setEntityId(String.valueOf(entityId));
			p.setPropertyName(entry.getKey());
			List<PortletEntityProperty> portletProperties = super.findByExample(p);
			if(portletProperties!=null&&portletProperties.size()>0){
				p = portletProperties.get(0);
				p.setPropertyValue(entry.getValue());
				super.update(p);
			}else{
				p.setNewId();
				p.setEntityId(String.valueOf(entityId));
				p.setPropertyName(entry.getKey());
				p.setPropertyValue(entry.getValue());
				super.save(p);
			}
		}
		PortletEntityProperty propertity = new PortletEntityProperty();
		propertity.setEntityId(String.valueOf(entityId));
		List<PortletEntityProperty> portletProperty = super.findByExample(propertity);
		Map<String,String> cacheProperties = new HashMap<String, String>();
		for(PortletEntityProperty property : portletProperty){
			cacheProperties.put(property.getPropertyName(), property.getPropertyValue());
		}
		synchronized (lock) {
			cache.put(entityId, (HashMap<String, String>)cacheProperties);
		}
	}

	public void copyPropertys(long srcEntityId, long destEntityId) {
		Map<String, String> props = this.getPropertys(srcEntityId);
		this.save(destEntityId, props);
	}
	
	public String getExistedFormBizConfigSectionIds(Map<String,Map<String, Fragment>> fragments) {
		StringBuilder result = new StringBuilder();
		if(fragments != null && fragments.size() > 0) {
			for(Iterator<Map.Entry<String, Map<String,Fragment>>> it = fragments.entrySet().iterator(); it.hasNext();) {
				Map<String, Fragment> map = it.next().getValue();
				
				for(Iterator<Map.Entry<String, Fragment>> it2 = map.entrySet().iterator(); it2.hasNext(); ) {
					Fragment fragment = it2.next().getValue();
					
					Map<String, String> props = this.getPropertys(fragment.getId());
					String sections = props.get("sections");
					if(Strings.isNotBlank(sections)){
						String[] _sections = sections.split(",");
						for(int i = 0; i < _sections.length; i++) {
							if("singleBoardFormBizConfigSection".equalsIgnoreCase(_sections[i])) { 
								result.append(props.get("singleBoardId:" + i) + ",");
							}
						}
					}
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 
	 */
	public void updateProperties(long entityId, String[] paramName, String[] values) {
		if(paramName == null || paramName.length ==0){
			return;
		}
		Map<String,String> properties = new HashMap<String,String>();
		for(int i =0;i< paramName.length;i++){
			properties.put(paramName[i], values[i]);
		}
		save(entityId,properties);
	}
	
	/**
	 * 取得栏目下配置的页签。
	 * @param fragmentId 栏目框架Fragment{@link com.seeyon.v3x.space.domain.Fragment} .id
	 * @param ordinal    栏目在fragment中所排的位置
	 * @param panelName  页签名
	 * @param defaultValue 默认栏目
	 * @return
	 */
	public List<SectionPanel> getSectionPanel(Long fragmentId,String ordinal,String panelName,String defaultValue){
		List<SectionPanel> result = new ArrayList<SectionPanel>();
		Map<String,String> props = PortalConstants.getFragmentProp(this.getPropertys(fragmentId),ordinal);
		if(props != null){
			String panelIds = props.get(panelName);
			if(panelIds== null){
				panelIds = defaultValue;
			}
			if(Strings.isNotBlank(panelIds)){
				String[] panelValues = panelIds.split(",");
				for(String value : panelValues){
					String type = props.get(value+"_type");
					SectionPanel p = new SectionPanel(value, PortalConstants.getPanelName(value, props),type);
					result.add(p);
				}
			}
		}
		return result;
	}
	
	/**
	 * 得到栏目配置。
	 * @param fragmentId
	 * @param ordinal
	 * @return
	 */
	public Map<String,String> getPropertys(Long fragmentId,String ordinal){
		Map<String,String> props = PortalConstants.getFragmentProp(this.getPropertys(fragmentId),ordinal);
		return props;
	}
	
	public void addCachePropertys(Long entityId,HashMap<String, String> props){
		cache.put(entityId, props);
	}
	
	public void removeCachePropertys(Long entityId){
		cache.remove(entityId);
	}
	public void reloadProperties(Long entityId){
		cache.reload(entityId);
	}
}
