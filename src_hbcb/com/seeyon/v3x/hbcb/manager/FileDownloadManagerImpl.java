package com.seeyon.v3x.hbcb.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.hbcb.dao.FileDownloadDao;
import com.seeyon.v3x.hbcb.domain.FileDownload;

public class FileDownloadManagerImpl implements FileDownloadManager {
	private static final Log logger = LogFactory.getLog(FileDownloadManagerImpl.class);

	private FileDownloadDao fileDownloadDao;

	public void setFileDownloadDao(FileDownloadDao fileDownloadDao) {
		this.fileDownloadDao = fileDownloadDao;
	}

	/**
	 * 获取某员工某天的打卡记录
	 * 
	 * @param
	 * @return
	 */
	public FileDownload getFileDownload(Long memberId, Long fileId) throws Exception {
		FileDownload rc = fileDownloadDao.getFileDownload(memberId, fileId);
		return rc;
	}
	
	public void saveFileDownload(FileDownload fileDownload) {
		fileDownloadDao.save(fileDownload);
	}
	
}
