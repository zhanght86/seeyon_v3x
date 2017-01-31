package com.seeyon.v3x.edoc.util;

import java.util.List;

import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.util.Strings;

public class MetaUtil {
	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(MetaUtil.class);
	public static void refMeta(EdocSummary summary)
	{
		EdocFormManager edocFormManager= (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		EdocElementManager elementManager=(EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");
		MetadataManager metadataManager=(MetadataManager)ApplicationContextHolder.getBean("metadataManager");

		List list = null;
		try{
		list=edocFormManager.getEdocFormElementByFormId(summary.getFormId());
		}catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
		EdocElement element=null;
		Metadata metadata = null;;
		Long elementId = null;
		int i,len=list.size();
		for(i=0;i<len;i++)
		{
			elementId = ((EdocFormElement)list.get(i)).getElementId();
			if(null==elementId){continue;}	
			String elementIdStr = elementId.toString();
			if(elementIdStr.length()==1){
				elementIdStr = "00"+elementIdStr;
			}else if(elementIdStr.length()==2){
				elementIdStr = "0"+elementIdStr;				
			}
			element=elementManager.getEdocElement(elementIdStr);
			if(null!=element){
				Long metadataId = element.getMetadataId();
				if(null==metadataId){continue;}
					metadata = metadataManager.getMetadata(metadataId);
					setEdocSummaryValue(summary,element.getFieldName(), metadata);
			}
		}
				
	}
	/**
	 * 把html页面提交过来的input的值设置到edocsummary对象内
	 * @param summary
	 * @param inputName
	 * @param inputValue
	 * @return
	 */
	public static void setEdocSummaryValue(EdocSummary summary,String inputName, Metadata metadata)
	{		
		if("doc_type".equals(inputName))
		{			
			refMetadataItem(summary.getDocType(), metadata);
		}
		else if("send_type".equals(inputName))
		{			
			refMetadataItem(summary.getSendType(), metadata);
		}
		else if("secret_level".equals(inputName))
		{			
			refMetadataItem(summary.getSecretLevel(), metadata);
		}
		else if("urgent_level".equals(inputName))
		{			
			refMetadataItem(summary.getUrgentLevel(), metadata);
		}
		else if("keep_period".equals(inputName))
		{
			refMetadataItem(String.valueOf(summary.getKeepPeriod()), metadata);
		}
		else if("list1".equals(inputName))
		{			
			refMetadataItem(summary.getList1(), metadata);
		}
		else if("list2".equals(inputName))
		{			
			refMetadataItem(summary.getList2(), metadata);
		}
		else if("list3".equals(inputName))
		{			
			refMetadataItem(summary.getList3(), metadata);
		}
		else if("list4".equals(inputName))
		{			
			refMetadataItem(summary.getList4(), metadata);
		}
		else if("list5".equals(inputName))
		{			
			refMetadataItem(summary.getList5(), metadata);
		}
		else if("list6".equals(inputName))
		{			
			refMetadataItem(summary.getList6(), metadata);
		}
		else if("list7".equals(inputName))
		{			
			refMetadataItem(summary.getList7(), metadata);
		}
		else if("list8".equals(inputName))
		{			
			refMetadataItem(summary.getList8(), metadata);
		}
		else if("list9".equals(inputName))
		{			
			refMetadataItem(summary.getList9(), metadata);
		}
		else if("list10".equals(inputName))
		{			
			refMetadataItem(summary.getList10(), metadata);
		}
		else if("list11".equals(inputName))
		{			
			refMetadataItem(summary.getList11(), metadata);
		}
		else if("list12".equals(inputName))
		{			
			refMetadataItem(summary.getList12(), metadata);
		}
		else if("list13".equals(inputName))
		{			
			refMetadataItem(summary.getList13(), metadata);
		}
		else if("list14".equals(inputName))
		{			
			refMetadataItem(summary.getList14(), metadata);
		}
		else if("list15".equals(inputName))
		{			
			refMetadataItem(summary.getList15(), metadata);
		}
		else if("list16".equals(inputName))
		{
			refMetadataItem(summary.getList16(), metadata);
		}
		else if("list17".equals(inputName))
		{			
			refMetadataItem(summary.getList17(), metadata);
		}
		else if("list18".equals(inputName))
		{			
			refMetadataItem(summary.getList18(), metadata);
		}
		else if("list19".equals(inputName))
		{			
			refMetadataItem(summary.getList19(), metadata);
		}
		else if("list20".equals(inputName))
		{			
			refMetadataItem(summary.getList20(), metadata);
		}
		return;
	}
	
	public static void refMetadataItem(String inputValue, Metadata metadata){
		if(!Strings.isBlank(inputValue) && null!=metadata){
			MetadataManager metadataManager=(MetadataManager)ApplicationContextHolder.getBean("metadataManager");
			MetadataItem item = metadataManager.getMetadataItem(metadata.getName(), inputValue);
			if(null!=item){
			metadataManager.refMetadata(metadata.getId(), metadata.getIsSystem());
			metadataManager.refMetadataItem(metadata.getId(), item.getId(), metadata.getIsSystem());
			}
		}
	}

}
