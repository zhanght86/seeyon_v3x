package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import com.seeyon.v3x.doc.dao.DocListColumnDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;

/**
 * 默认显示栏目
 */
public final class DefaultListColumn {
	private final static long Column_ID_IMAGE = 1;
	
	private final static long Column_ID_Name = 2;

	private final static long Column_ID_Type = 3;

	private final static long Column_ID_Size = 4;

	private final static long Column_ID_CreateUser = 5;

	private static final  long Column_ID_CreateTime = 6;

	private final static long Column_ID_LastUser = 7;
	
	private final static long Column_ID_LastUpdate = 8;

	private DocListColumnDao docListColumnDao;

	private MetadataDefManager metadataDefManager;
	
	private static List<DocMetadataDefinition> defaultColumns = null;

	private final static long[] DefaultListColumns = { Column_ID_IMAGE, Column_ID_Name,
			Column_ID_Type, Column_ID_Size, Column_ID_CreateUser,
			Column_ID_LastUpdate};

	/**
	 * 取得默认显示栏目
	 * 
	 */
	public List<DocMetadataDefinition> getDefaultListColumns() {
		if (defaultColumns == null || defaultColumns.size() <= 0) {
			defaultColumns = new ArrayList<DocMetadataDefinition>();
			List<DocMetadataDefinition> metadataDefs = metadataDefManager.findDefaultMetadataDef();
			Hashtable<Long, DocMetadataDefinition> hashtable = new Hashtable<Long, DocMetadataDefinition>();
			for(DocMetadataDefinition t: metadataDefs) {
				hashtable.put(t.getId(), t);
			}
			for (int i = 0; i < DefaultListColumns.length; i++) {
				defaultColumns.add(hashtable.get(DefaultListColumns[i]));
			}
		}

		return defaultColumns;
	}

	public DocListColumnDao getDocListColumnDao() {
		return docListColumnDao;
	}

	public void setDocListColumnDao(DocListColumnDao docListColumnDao) {
		this.docListColumnDao = docListColumnDao;
	}

	public MetadataDefManager getMetadataDefManager() {
		return metadataDefManager;
	}

	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}

}
