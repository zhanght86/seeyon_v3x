package com.seeyon.v3x.bulletin.util;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;


/**
 * 公告管理的数据字典工具类
 * @deprecated
 * @author wolf
 *
 */
public class MetaDataUtil {
	
	private static MetadataManager mm=null;
	
	public static Metadata getMetaDatas(String key){
		if(mm==null)
			mm=(MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		
		return mm.getMetadataMap(ApplicationCategoryEnum.bulletin).get(key);
	}
}
