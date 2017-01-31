/**
 * 
 */
package com.seeyon.v3x.main.section.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionProps;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class SectionDefinitionDaoImpl extends BaseHibernateDao<SectionDefinition> implements SectionDefinitionDao {
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public List<SectionDefinition> getAll(){
		return super.find("from SectionDefinition order by sort,createDate");
	}
	
	@SuppressWarnings("unchecked")
	public List<SectionProps> getAllSectionProps(){
		return getHibernateTemplate().loadAll(SectionProps.class);
	}
	
	public void delete(long sectionDefinitionId) {
		super.delete(SectionProps.class, new Object[][]{{"sectionDefinitionId", sectionDefinitionId}});
		super.delete(SectionSecurity.class, new Object[][]{{"sectionDefinitionId", sectionDefinitionId}});
		super.delete(SectionDefinition.class, sectionDefinitionId);
	}

	public void save(SectionDefinition definition, List<SectionProps> props,
			List<SectionSecurity> securities) {
		super.save(definition);
		
		if(props != null && !props.isEmpty()){
			for (SectionProps prop : props) {
				super.save(prop);
			}
		}
		
		if(securities != null && !securities.isEmpty()){
			for (SectionSecurity security : securities) {
				super.save(security);
			}
		}
	}

	public void update(SectionDefinition definition, List<SectionProps> props,
			List<SectionSecurity> securities) {
		super.update(definition);
		
		super.delete(SectionProps.class, new Object[][]{{"sectionDefinitionId", definition.getId()}});
		super.delete(SectionSecurity.class, new Object[][]{{"sectionDefinitionId", definition.getId()}});
		
		if(props != null && !props.isEmpty()){
			for (SectionProps prop : props) {
				super.save(prop);
			}
		}
		
		if(securities != null && !securities.isEmpty()){
			for (SectionSecurity security : securities) {
				super.save(security);
			}
		}
	}
	
	public List<SectionSecurity> getSectionSecurity(long sectionDefinitionid){
		return getHibernateTemplate().find("from SectionSecurity where sectionDefinitionId=? order by sort", sectionDefinitionid);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getCurrentAccess(List<Long> domainIds, int type) {
		String hql = "select distinct sectionDefinitionId from SectionSecurity where entityId in (:userIds)";
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("userIds", domainIds);
		return super.find(hql, -1, -1, namedParameterMap);
	}

	public int  countSectionByNameAndType(String name,int type){
		DetachedCriteria criteria = DetachedCriteria.forClass(SectionDefinition.class);
		criteria.add(Restrictions.eq("name",name));
		criteria.add(Restrictions.eq("type", type));
		return super.getCountByCriteria(criteria);
	}

	@Override
	public SectionDefinition getDefinition(Long id) {
		return get(id);
	}

	@Override
	public SectionProps getProps(Long id) {
		return (SectionProps)getHibernateTemplate().get(SectionProps.class, id);
	}

	@Override
	public List<SectionProps> getPropsByDefinitionId(Long id) {
		return super.find("from SectionProps where sectionDefinitionId=?",id);
	}
}
