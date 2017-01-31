package com.seeyon.v3x.doc.manager;

import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.doc.dao.DocMimeTypeDao;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.util.Constants;

public class DocMimeTypeManagerImpl implements DocMimeTypeManager {
	
	private static final Log log = LogFactory.getLog(DocMimeTypeManagerImpl.class);
	
	public static Hashtable<Long,DocMimeType> docMimeTypeTable = null;
	private static List<DocMimeType> docMimeTypes = null;
	private static boolean initialized = false;
	
	private DocMimeTypeDao docMimeTypeDao;

	public DocMimeTypeDao getDocMimeTypeDao() {
		return docMimeTypeDao;
	}

	public void setDocMimeTypeDao(DocMimeTypeDao docMimeTypeDao) {
		this.docMimeTypeDao = docMimeTypeDao;
	}
	
	public void init() {
		if (initialized) {
			return ;
		}

		docMimeTypeTable = new Hashtable<Long, DocMimeType>();
		docMimeTypes = docMimeTypeDao.getAll();
		for (int i = 0; i < docMimeTypes.size(); i++) {
			DocMimeType docMimeType = docMimeTypes.get(i);
			docMimeTypeTable.put(docMimeType.getId(), docMimeType);
		}
		initialized = true;
//		log.info("docMimeTypes 加载完成。");
	}

//	public DocMimeType getDocMimeTypeByDocTypeId(Long docTypeId) {
//		if (!initialized) {
//			init();
//		}
//		DocMimeType ret = null;
//		for (int i = 0; i < docMimeTypes.size(); i++) {
//			DocMimeType docMimeType = docMimeTypes.get(i);
//			if (docMimeType.getDocTypeId().equals(docTypeId)) {
//				ret = docMimeType;
//				break;
//			}
//		}
//		return ret;
//	}

	public long getDocMimeTypeByFilePostix(String postfix) {
		if (!initialized) {
			init();
		}
		long ret = Constants.FORMAT_TYPE_DOC_FILE;
		String postfix2 = postfix;
		if(postfix.toLowerCase().equals("docx")){
			postfix2 = "doc";
		}
		else if(postfix.toLowerCase().equals("xlsx")){
			postfix2 = "xls";
		}
		else if(postfix.toLowerCase().equals("pptx")){
			postfix2 = "ppt";
		}
		else if( postfix.toLowerCase().equals("wps")){
			postfix2 = "wps";
		}
		else if( postfix.toLowerCase().equals("et")){
			postfix2 = "et";
		}
		for (int i = 0; i < docMimeTypes.size(); i++) {
			DocMimeType docMimeType = docMimeTypes.get(i);
			if (docMimeType.getName().toLowerCase().equals(postfix2.toLowerCase())) {
				ret = docMimeType.getId();
				break;
			}
		}		
		return ret;
	}

	public DocMimeType getDocMimeTypeById(Long id) {
		if (!initialized) {
			init();
		}
		return docMimeTypeTable.get(id);
	}

}
