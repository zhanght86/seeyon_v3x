package com.seeyon.v3x.plugin.deeSection.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.dao.BaseDao;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionProps;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionSecurity;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class DeeSectionDaoImpl extends BaseDao<DeeSectionDefine> implements DeeSectionDao{
	
	@Override
	public void saveDeeSection(DeeSectionDefine deeSection) {
		super.save(deeSection);
	}

	@Override
	public void updateDeeSection(DeeSectionDefine deeSection) {
		super.update(deeSection);
	}

	@Override
	public void deleteDeeSection(long id) {
		super.delete(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeeSectionDefine> getAllDeeSection() {
		return super.find("from "+ DeeSectionDefine.class.getName(), -1, -1, null);
	}

	@Override
	public DeeSectionDefine getDeeSectinById(long id) {
		return super.findUniqueBy("id", id);
	}

	@Override
	public void save(DeeSectionDefine deeSection, List<DeeSectionSecurity> securities) {
		this.saveDeeSection(deeSection);
		
		if(securities != null && !securities.isEmpty()){
			for (DeeSectionSecurity security : securities) {
				super.save(security);
			}
		}
	}
	
	@Override
	public void update(DeeSectionDefine deeSection, List<DeeSectionSecurity> securities) {
		super.update(deeSection);
		
		String hql = "delete from "+ DeeSectionSecurity.class.getName() +" where deeSectionId = ?";
		super.bulkUpdate(hql, null, deeSection.getId());
		
		if(securities != null && !securities.isEmpty()){
			for (DeeSectionSecurity security : securities) {
				super.save(security);
			}
		}
	}

	@Override
	public List<DeeSectionSecurity> getSectionSecurity(long entityId) {
		String hql = " from "+ DeeSectionSecurity.class.getName() + " where deeSectionId = ?";
		List<DeeSectionSecurity> securities = super.find(hql, entityId);
		return securities;
	}

	@Override
	public List<DeeSectionProps> getAllSectionProps() {
		return getHibernateTemplate().loadAll(DeeSectionProps.class);
	}

	@Override
	public List<DeeSectionProps> getPropsByDeeSectionId(long deeSectionId) {
		return super.find("from "+DeeSectionProps.class.getName()+" where deeSectionId=? order by sort asc",deeSectionId);
	}

	@Override
	public void saveDeeSectionProps(long id, Map<String, Map<String,String>> props) {
		String hql = "delete from "+ DeeSectionProps.class.getName() +" where deeSectionId = ?";
		super.bulkUpdate(hql, null, id);
		
		if(props!=null&&!props.isEmpty()){
			Set<String> keys = props.keySet();
			for(String key : keys){
				DeeSectionProps sectionProp = new DeeSectionProps();
				Map<String,String> map = props.get(key);
				sectionProp.setIdIfNew();
				sectionProp.setDeeSectionId(id);
				sectionProp.setPropName(key);
				sectionProp.setPropValue(map.get("displayName"));
				sectionProp.setPropMeta(map.get("fieldType"));
				sectionProp.setIsShow(Integer.valueOf(map.get("isShow")));
				String sort = map.get("sort");
				if(Strings.isNotBlank(sort)){
					sectionProp.setSort(Integer.valueOf(sort));
				}
				super.save(sectionProp);
			}
		}
		
	}

	@Override
	public List<DeeSectionDefine> getAllDeeSection(String sectionName) {
		if(Strings.isNotBlank(sectionName)){
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("name", "%"+SQLWildcardUtil.escape(sectionName.trim())+"%");
			return super.find("from "+ DeeSectionDefine.class.getName()+" where deeSectionName like :name", -1, -1,parameterMap);
		}else{
			return super.find("from "+ DeeSectionDefine.class.getName(), -1, -1, null);
		}
	}

	@Override
	public List<DeeSectionDefine> getDeeSectionByName(String sectionName) {
		if(Strings.isNotBlank(sectionName)){
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("name", SQLWildcardUtil.escape(sectionName.trim()));
			return super.find("from "+ DeeSectionDefine.class.getName()+" where deeSectionName = :name", -1, -1,parameterMap);
		}else{
			return null;
		}
	}

	@Override
	public List<DeeSectionSecurity> getDeeSectionBySecurity(List<Long> entityIds) {
		String hql = " from " + DeeSectionSecurity.class.getName() + " where entityId in (:entityId)";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("entityId", entityIds);
		return super.find(hql.toString(), -1, -1, nameParameters);
	}
}
