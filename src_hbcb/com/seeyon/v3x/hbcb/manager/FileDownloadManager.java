package com.seeyon.v3x.hbcb.manager;

import com.seeyon.v3x.hbcb.domain.FileDownload;

public interface FileDownloadManager {

	/**
	 * 获取某员工某文件的下载记录
	 * 
	 * @param
	 * @return
	 */
	public FileDownload getFileDownload(Long memberId, Long fileId) throws Exception;
	
	public void saveFileDownload(FileDownload fileDownload);

}
