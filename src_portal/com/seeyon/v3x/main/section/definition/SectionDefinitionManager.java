/**
 * 
 */
package com.seeyon.v3x.main.section.definition;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.link.webmodel.LinkSectionVo;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionProps;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public interface SectionDefinitionManager {
	public SectionDefinition getSectionDefinition(long sectionDefinitionid);
	
	public List<SectionDefinition> getAllSectionDefinition();
	
	public List<SectionDefinition> getSectionsByIds(List<Long> ids);
	
	public List<SectionDefinition> getSectionDefinitionByType(int type);

	public Map<String, String> getSectionProps(long sectionDefinitionid);

	public List<SectionSecurity> getSectionSecurity(long sectionDefinitionid);
	
	public void save(String name, int type, int state, String selectPeopleStr, Map<String, String> props);
	
	public void update(long sectionDefinitionid, String name, int type, int state, String selectPeopleStr, Map<String, String> props);
	
	public void delete(long sectionDefinitionId);
	
	public List<SectionDefinition> getCurrentAccess(List<Long> domainIds, int type);
	
	public int checkSameSection(long sectionDefinitionid, String sectionName,int type);
	
	public List<SectionProps> getSectionPropsByLinkSystemId(String linkSystemId);
	/**
	 * 根据用户权限返回DEE栏目集合
	 * 用与添加栏目选择框
	 * @param domainIds
	 * @return
	 */
	public List<SectionDefinition> getCurrentAccess(List<Long> domainIds);
}
