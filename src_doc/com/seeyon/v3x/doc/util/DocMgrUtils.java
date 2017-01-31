package com.seeyon.v3x.doc.util;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.doc.dao.DocBodyDao;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;

/**
 * 用于分离知识管理中业务逻辑层部分私有方法以便复用
 * 将原先需要复用的私有方法包装为静态方法
 * @author <a href="mailto:yangmeng84@sina.com">菜鸟杨</a> 2010-11-6
 */
public class DocMgrUtils {
	
	/**
	 * 根据文档的特征删除文档的正文、源文件
	 * @param dr	文档
	 * @throws BusinessException	删除源文件时可能会抛出此异常
	 */
	public static void deleteBodyAndSource(DocResource dr, DocMimeTypeManager docMimeTypeManager, FileManager fileManager, DocBodyDao docBodyDao) throws BusinessException {
		DocMimeType mimeType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
		long formatType = mimeType.getFormatType();

		if (formatType == Constants.FORMAT_TYPE_DOC_A6 || dr.isImage()) {
			docBodyDao.delete(dr.getId().longValue());
		} 
		if (mimeType.isMSOrWPS() || dr.isPDF()) {
			DocBody docBody = docBodyDao.get(dr.getId());
			if (docBody != null) {
				fileManager.deleteFile(NumberUtils.toLong(docBody.getContent()), true);
			}
			docBodyDao.delete(dr.getId().longValue());
		} else if (formatType == Constants.FORMAT_TYPE_DOC_FILE) {
			fileManager.deleteFile(dr.getSourceId(), true);
		}
	}
	
}
