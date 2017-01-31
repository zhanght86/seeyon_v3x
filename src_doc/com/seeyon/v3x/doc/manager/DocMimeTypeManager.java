package com.seeyon.v3x.doc.manager;

import com.seeyon.v3x.doc.domain.DocMimeType;

public interface DocMimeTypeManager {
	
	/**
	 * 系统初始化时调用，将数据库表doc_mime_types数据常驻内存中。
	 */
	public void init();
	
	/**
	 * 根据文件名后缀查找对应的 DocMimeType对象
	 * 
	 */
	public long getDocMimeTypeByFilePostix(String postfix);
	
//	/**
//	 * 根据 docTypeId 查找对应的 DocMimeType 对象
//	 * 
//	 */
//	public DocMimeType getDocMimeTypeByDocTypeId(Long docTypeId);
	
	/**
	 * 根据 id 查找对应的 DocMimeType 对象
	 * 
	 */
	public DocMimeType getDocMimeTypeById(Long id);
}
