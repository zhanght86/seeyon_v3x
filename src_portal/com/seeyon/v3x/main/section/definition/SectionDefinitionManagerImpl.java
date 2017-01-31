/**
 * 
 */
package com.seeyon.v3x.main.section.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionProps;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class SectionDefinitionManagerImpl implements SectionDefinitionManager {
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(SectionDefinitionManagerImpl.class);
//	private Map<Long, SectionDefinition> SectionDefinitionMap = new HashMap<Long, SectionDefinition>();
	private CacheMap<Long, SectionDefinition> SectionDefinitionMap;

//	private List<SectionDefinition> SectionDefinitionList = new ArrayList<SectionDefinition>();
//	private CacheList<SectionDefinition> SectionDefinitionList;

//	private Map<Long, List<SectionProps>> SectionPropsMap = new HashMap<Long, List<SectionProps>>();
	private CacheMap<Long, ArrayList<SectionProps>> SectionPropsMap;

	private SectionDefinitionDao sectionDefinitionDao;
	
	public void setSectionDefinitionDao(SectionDefinitionDao sectionDefinitionDao) {
		this.sectionDefinitionDao = sectionDefinitionDao;
	}

	public void init(){ 
		SectionDefinitionMap = cacheFactory.createLinkedMap("SectionDefinitionMap");
//		SectionDefinitionList = cacheFactory.createList("SectionDefinitionList");
		SectionPropsMap = cacheFactory.createMap("SectionPropsMap");		
		
		SectionDefinitionMap.setDataLoader(new AbstractMapDataLoader<Long, SectionDefinition> (SectionDefinitionMap) {
			@Override
			protected Map<Long, SectionDefinition> loadLocal() {
				Map<Long, SectionDefinition> result = new HashMap<Long, SectionDefinition>();
				List<SectionDefinition> ds = sectionDefinitionDao.getAll();
				for (SectionDefinition definition : ds) {
					result.put(definition.getId(), definition);
//					SectionDefinitionList.add(definition);
				}
				return result;
			}

			@Override
			protected SectionDefinition loadLocal(Long k) {
				return sectionDefinitionDao.getDefinition(k);
			}
		});
		SectionDefinitionMap.reload();
		
//		SectionDefinitionMap.clear();
////		SectionDefinitionList.clear();
//		SectionPropsMap.clear();
		SectionPropsMap.setDataLoader(new AbstractMapDataLoader<Long, ArrayList<SectionProps>>(SectionPropsMap) {

			@Override
			protected Map<Long, ArrayList<SectionProps>> loadLocal() {
				Map<Long, ArrayList<SectionProps>> result = new HashMap<Long, ArrayList<SectionProps>>();
				List<SectionProps> ps = sectionDefinitionDao.getAllSectionProps();
				for (SectionProps props : ps) {
					ArrayList<SectionProps> prop = result.get(props.getSectionDefinitionId());
					if(prop == null){
						prop = new ArrayList<SectionProps>();
						result.put(props.getSectionDefinitionId(), prop);
					}
					prop.add(props);
				}
				return result;
			}

			@Override
			protected ArrayList<SectionProps> loadLocal(Long k) {
				return new ArrayList<SectionProps>(sectionDefinitionDao.getPropsByDefinitionId(k));
			}
		});
		SectionPropsMap.reload();
	}

	public List<SectionDefinition> getAllSectionDefinition() {
		return new ArrayList<SectionDefinition>(SectionDefinitionMap.values());
		//return SectionDefinitionList.toList();
	}
	
	public SectionDefinition getSectionDefinition(long sectionDefinitionid) {
		return SectionDefinitionMap.get(sectionDefinitionid);
	}
	public List<SectionDefinition> getSectionsByIds(List<Long> ids){
		List<SectionDefinition> r = new ArrayList<SectionDefinition>();
		for(Long id :ids){
			r.add(SectionDefinitionMap.get(id));
		}
		return r;
	}
	
	public List<SectionDefinition> getSectionDefinitionByType(int type){
		List<SectionDefinition> r = new ArrayList<SectionDefinition>();
		for (SectionDefinition d : SectionDefinitionMap.values()) {
			if(d.getType() == type){
				r.add(d);
			}
		}
		
		return r;
	}

	public Map<String, String> getSectionProps(long sectionDefinitionid) {
		Map<String, String> r = new HashMap<String, String>();
		List<SectionProps> props =  SectionPropsMap.get(sectionDefinitionid);
		if(props != null){
			for (SectionProps prop : props) {
				r.put(prop.getPropName(), prop.getPropValue());
			}
		}
		
		return r;
	}

	public List<SectionSecurity> getSectionSecurity(long sectionDefinitionid) {
		return this.sectionDefinitionDao.getSectionSecurity(sectionDefinitionid);
	}
	
	public void save(String name, int type, int state, String selectPeopleStr, Map<String, String> props){
		SectionDefinition d = new SectionDefinition();
		d.setIdIfNew();
		d.setCreateDate(new Date());
		d.setName(name);
		d.setSort(0);
		d.setType(type);
		d.setState(state);
		
		List<SectionSecurity> securities = new ArrayList<SectionSecurity>();
		String[][] es = Strings.getSelectPeopleElements(selectPeopleStr);
		if(es != null){
			for (int i = 0; i < es.length; i++) {
				SectionSecurity s = new SectionSecurity();
				s.setIdIfNew();
				s.setSectionDefinitionId(d.getId());
				s.setEntityType(es[i][0]);
				s.setEntityId(Long.parseLong(es[i][1]));
				s.setSort(i);
				
				securities.add(s);
			}
		}
		
		ArrayList<SectionProps> sectionProps = new ArrayList<SectionProps>();
		if(props != null && !props.isEmpty()){
			for (Iterator<String> iter = props.keySet().iterator(); iter.hasNext();) {
				String n = iter.next();
				
				SectionProps p = new SectionProps();
				p.setIdIfNew();
				p.setSectionDefinitionId(d.getId());
				p.setPropName(n);
				p.setPropValue(props.get(n));
				
				sectionProps.add(p);
			}
		}
		
		this.sectionDefinitionDao.save(d, sectionProps, securities);
		
		if(state == SectionDefinition.State.normal.ordinal()){
			this.SectionDefinitionMap.put(d.getId(), d);
//			this.SectionDefinitionList.add(d);
			
			this.SectionPropsMap.put(d.getId(), sectionProps);
			
//			Collections.sort(SectionDefinitionList);
//			SectionDefinitionList.sort();
		}
	}
	
	public void update(long sectionDefinitionid, String name, int type, int state, String selectPeopleStr, Map<String, String> props){
		SectionDefinition d = this.getSectionDefinition(sectionDefinitionid);
		d.setName(name);
        d.setType(type);
		d.setState(state);
		SectionDefinitionMap.notifyUpdate(sectionDefinitionid);
		List<SectionSecurity> securities = new ArrayList<SectionSecurity>();
		String[][] es = Strings.getSelectPeopleElements(selectPeopleStr);
		if(es != null){
			for (int i = 0; i < es.length; i++) {
				SectionSecurity s = new SectionSecurity();
				s.setIdIfNew();
				s.setSectionDefinitionId(d.getId());
				s.setEntityType(es[i][0]);
				s.setEntityId(Long.parseLong(es[i][1]));
				s.setSort(i);
				
				securities.add(s);
			}
		}
		
		ArrayList<SectionProps> sectionProps = new ArrayList<SectionProps>();
		if(props != null && !props.isEmpty()){
			for (Iterator<String> iter = props.keySet().iterator(); iter.hasNext();) {
				String n = iter.next();
				
				SectionProps p = new SectionProps();
				p.setIdIfNew();
				p.setSectionDefinitionId(d.getId());
				p.setPropName(n);
				p.setPropValue(props.get(n));
				
				sectionProps.add(p);
			}
		}
		
		this.sectionDefinitionDao.update(d, sectionProps, securities);
		
		if(state == SectionDefinition.State.normal.ordinal()){
			this.SectionDefinitionMap.put(d.getId(), d);
/*			if(SectionDefinitionList != null && !SectionDefinitionList.isEmpty()){
				for (int i=0; i<SectionDefinitionList.size(); i++) {
					if(d.equals(SectionDefinitionList.get(i))){
						SectionDefinitionList.set(i, d);
						break;
					}
				}
			}*/
			
			this.SectionPropsMap.put(d.getId(), sectionProps);
			
//			Collections.sort(SectionDefinitionList);
//			SectionDefinitionList.sort();
		}
		else{
			this.SectionDefinitionMap.remove(d.getId());
//			this.SectionDefinitionList.remove(d);
			this.SectionPropsMap.remove(d.getId());
		}
	}
	
	public void delete(long sectionDefinitionId){
		this.sectionDefinitionDao.delete(sectionDefinitionId);
		this.SectionDefinitionMap.remove(sectionDefinitionId);
/*		if(SectionDefinitionList != null && !SectionDefinitionList.isEmpty()){
			//避免ConcurrentModificationException
			List<SectionDefinition> forDeleteList = new ArrayList<SectionDefinition>();
			for (SectionDefinition s : SectionDefinitionList.toList()) {
				if(s.getId().equals(sectionDefinitionId)){
					forDeleteList.add(s);
				}
			}
			if(!forDeleteList.isEmpty()){	
				SectionDefinitionList.removeAll(forDeleteList);
			}
		}*/
		this.SectionPropsMap.remove(sectionDefinitionId);
	}
	
	public List<SectionDefinition> getCurrentAccess(List<Long> domainIds, int type){
		List<SectionDefinition> sectionDefinitions = new ArrayList<SectionDefinition>();
		List<Long> s = this.sectionDefinitionDao.getCurrentAccess(domainIds, type);
		
		for (Long long1 : s) {
			SectionDefinition d = this.SectionDefinitionMap.get(long1);
			if(d != null && d.getType() == type){
				sectionDefinitions.add(d);
			}
		}
		
		Collections.sort(sectionDefinitions);
		
		return sectionDefinitions;
	}
	
	public int checkSameSection(long sectionDefinitionid, String sectionName, int type){
	    if(sectionDefinitionid != -1){
    	    SectionDefinition sd = this.sectionDefinitionDao.getDefinition(sectionDefinitionid);
    	    if(sd != null){
    	        if(sd.getName().equals(sectionName) && sd.getType() == type){
    	            return 0;
    	        }
    	    }
	    }
		int count = this.sectionDefinitionDao.countSectionByNameAndType(sectionName,type);
		return count;
	}

    @Override
    public List<SectionProps> getSectionPropsByLinkSystemId(String linkSystemId) {
        List<List<SectionProps>> SectionPropsList = new ArrayList<List<SectionProps>>(SectionPropsMap.values());
        List<SectionProps> retList = new ArrayList<SectionProps>();
        for(List<SectionProps> list : SectionPropsList){
            for(SectionProps sectionProps : list){
                String propName = sectionProps.getPropName();
                String propValue = sectionProps.getPropValue();
                if(propName == null || propName.trim().length() == 0){
                    continue;
                }
                if(propValue == null || propValue.trim().length() == 0){
                    continue;
                }
                if((propName.equals("ssoWebcontentLinkSystemId") || propName.equals("ssoIframeLinkSystemId") || propName.equals("iframeLinkSystemId")) && propValue.equals(linkSystemId)){
                    retList.add(sectionProps);
                }
            }
        }
        return retList;
    }

	@Override
	public List<SectionDefinition> getCurrentAccess(List<Long> domainIds) {
		// TODO Auto-generated method stub
		return null;
	}
}