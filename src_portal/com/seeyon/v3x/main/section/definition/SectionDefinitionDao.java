/**
 * 
 */
package com.seeyon.v3x.main.section.definition;

import java.util.List;

import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionProps;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public interface SectionDefinitionDao {
	public List<SectionDefinition> getAll();
	
	public List<SectionProps> getAllSectionProps();

	public void save(SectionDefinition definition, List<SectionProps> props,
			List<SectionSecurity> securities);
	
	public void update(SectionDefinition definition, List<SectionProps> props,
			List<SectionSecurity> securities);


	public void delete(long sectionDefinitionId);
	
	public List<SectionSecurity> getSectionSecurity(long sectionDefinitionid);
	
	public List<Long> getCurrentAccess(List<Long> domainIds, int type);
	
	public int  countSectionByNameAndType(String name,int type);
	
	SectionDefinition getDefinition(Long id); 
	SectionProps getProps(Long id);
	List<SectionProps> getPropsByDefinitionId(Long id);
	
}
