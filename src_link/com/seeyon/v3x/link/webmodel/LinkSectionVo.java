package com.seeyon.v3x.link.webmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class LinkSectionVo {
	private SectionDefinition sectionDefinition;
	private Map<String, String> sectionProps = new HashMap<String, String>();
	private List<SectionSecurity> sectionSecurities = new ArrayList<SectionSecurity>();
	
	public LinkSectionVo() {
    }
	
    public SectionDefinition getSectionDefinition() {
        return sectionDefinition;
    }
    
    public void setSectionDefinition(SectionDefinition sectionDefinition) {
        this.sectionDefinition = sectionDefinition;
    }
    
    public Map<String, String> getSectionProps() {
        return sectionProps;
    }
    
    public void setSectionProps(Map<String, String> sectionProps) {
        this.sectionProps = sectionProps;
    }
    
    public List<SectionSecurity> getSectionSecurities() {
        return sectionSecurities;
    }
    
    public void setSectionSecurities(List<SectionSecurity> sectionSecurities) {
        this.sectionSecurities = sectionSecurities;
    }
}
