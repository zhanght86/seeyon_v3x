package com.seeyon.v3x.main.section.panel;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.util.Strings;

public class NewsSectionPanel extends BaseSectionPanel {
	private NewsTypeManager newsTypeManager;
	
	

	/**
	 * @param newsTypeManager the newsTypeManager to set
	 */
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}



	@Override
	protected Map<String, String> doGetName(String value) {
		Map<String,String> nameMap = new HashMap<String,String>();
		if(Strings.isNotBlank(value)){
			String[] ids = value.split(",");
			for(String id : ids){
				if("newest".equals(id) || "group".equals(id)){
					continue;
				}
				
				NewsType newsType = newsTypeManager.getById(Long.parseLong(id));
				if(newsType != null){
					nameMap.put(newsType.getId().toString(), newsType.getTypeName());
				}
			}
		} 
		/*if(CollectionUtils.isNotEmpty(newsTypeList)){
			for(NewsType newsType : newsTypeList){
				nameMap.put(newsType.getId().toString(), newsType.getTypeName());
			}
		}*/
		return nameMap;
	}

}
