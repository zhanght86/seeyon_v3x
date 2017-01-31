package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.doc.domain.DocMetadataDefinition;

/**
 * 默认查询条件
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-11-30
 */
public final class DefaultSearchCondition {
	private static final Log logger = LogFactory.getLog(DefaultSearchCondition.class);
	
	/**
	 * 个人、单位、集团、项目、自定义文档库：名称、内容类型、关键字、创建时间、创建人、发起人、发起时间
	 */
	private static List<DocMetadataDefinition> defaultSearchCondition = null;
	
	/**
	 * 公文档案库：公文标题、公文文号、内部文号、主题词、发文单位、建文人、建文日期、公文种类
	 */
	private static List<DocMetadataDefinition> defaultEdocSearchCondition = null;
	private final static long Search_EDOC_TITLE = 101;
	private final static long Search_EDOC_SERIALNO = 131;
	private final static long Search_EDOC_INNER_SERIALNO = 132;
	private final static long Search_EDOC_KEYWORD = 142;
	private final static long Search_EDOC_SENDER_ACCOUNT = 136;
	private final static long Search_EDOC_AUTHOR = 146;
	public final static long Search_EDOC_SEND_DATE = 104;
	
	private final static long Search_ID_FrName = 2;
	private final static long Search_ID_FrType = 3;
	private final static long Search_ID_KeyWords = 9;
	private static final  long Search_ID_CreateTime = 6;
	private final static long Search_ID_CreateUser = 5;
	private final static long Search_ID_Publisher = 110;
	private final static long Search_ID_PublishTime = 104;
	
	/** 非公文档案库默认查询条件 */
	private final static long[] DefaultSearchConditionIDs = {Search_ID_FrName, Search_ID_FrType, Search_ID_KeyWords,
		Search_ID_CreateTime, Search_ID_CreateUser, Search_ID_Publisher, Search_ID_PublishTime};
	
	/** 公文档案库默认查询条件 */	
	private final static long[] DefaultEdocSearchConditionIDs = {Search_EDOC_TITLE, Search_EDOC_SERIALNO, Search_EDOC_INNER_SERIALNO,
		Search_EDOC_KEYWORD, Search_EDOC_SENDER_ACCOUNT, Search_EDOC_AUTHOR, Search_EDOC_SEND_DATE};
	
	/**
	 * 获取非公文档案库默认查询条件
	 * @return
	 */
	public List<DocMetadataDefinition> getDefaultSearchCondition() {
		if(CollectionUtils.isEmpty(defaultSearchCondition))
			this.initSearchCondition();
		return defaultSearchCondition;
	}

	/**
	 * 获取公文档案库默认查询条件
	 * @return
	 */
	public List<DocMetadataDefinition> getDefaultEdocSearchCondition() {
		if(CollectionUtils.isEmpty(defaultEdocSearchCondition))
			this.initEdocSearchCondition();
		return defaultEdocSearchCondition;
	}

	private MetadataDefManager metadataDefManager;
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	
	/**
	 * 加载公文档案库、非公文档案库默认查询条件进入缓存
	 */
	public void init() {
		this.initSearchCondition();
		this.initEdocSearchCondition();
		
		if(logger.isDebugEnabled())
			logger.debug("文档默认查询条件初始化完成");
	}
	
	/**
	 * 加载非公文档案库默认查询条件
	 */
	private void initSearchCondition() {
		if(CollectionUtils.isEmpty(defaultSearchCondition)) {
			defaultSearchCondition = new ArrayList<DocMetadataDefinition>(DefaultSearchConditionIDs.length);
			for (int i = 0; i < DefaultSearchConditionIDs.length; i++) {
				defaultSearchCondition.add(metadataDefManager.getMetadataDefById(DefaultSearchConditionIDs[i]));
			}
		}
	}
	
	/**
	 * 加载公文档案库默认查询条件
	 */
	private void initEdocSearchCondition() {
		if(CollectionUtils.isEmpty(defaultEdocSearchCondition)) {
			defaultEdocSearchCondition = new ArrayList<DocMetadataDefinition>(DefaultEdocSearchConditionIDs.length);
			for (int i = 0; i < DefaultEdocSearchConditionIDs.length; i++) {
				defaultEdocSearchCondition.add(metadataDefManager.getMetadataDefById(DefaultEdocSearchConditionIDs[i]));
			}
		}
	}
	
}
