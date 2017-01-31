package com.seeyon.v3x.plugin.deeSection.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionProps;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionSecurity;


public interface DeeSectionManager  {
	
	public void createDeeSection(DeeSectionDefine deeSection);
	
	public void updateDeeSection(DeeSectionDefine deeSection);
	
	public void deleteDeeSection(String[] ids);
	
	public List<DeeSectionDefine> findAllDeeSection();
	
	public List<DeeSectionDefine> findAllDeeSection(String sectionName);
	
	public DeeSectionDefine findDeeSectionById(long id);
	
	public List<DeeSectionProps> getSectionProps(long id);
	
	public void saveSectionProps(long id,Map<String,Map<String,String>> props);
	
	public Map<String,Object> getFlowList(String flowType,String moduleName,String flowName,int pageNum,int pageSize);
	
	public void save(DeeSectionDefine deeSection, String[][] security);
	
	public void update(DeeSectionDefine deeSection, String[][] security);
	
	public List<DeeSectionSecurity> getSectionSecurity(long entityId);
	
	public String getShowField(String flowId);
	
	public Map<String,Map<String,String>> getShowFieldMap(String flowId);
	
	public boolean hasCurrentSectionName(String sectionName,String id);

	public List<DeeSectionDefine> getDeeSectionIdBySecurity(List<Long> entityIds);
}
