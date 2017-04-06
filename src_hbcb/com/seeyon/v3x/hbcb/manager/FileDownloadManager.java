package com.seeyon.v3x.hbcb.manager;

import java.util.List;

import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.hbcb.domain.FileDownload;

public interface FileDownloadManager {

	/**
	 * 获取某员工某文件的下载记录
	 */
	public FileDownload getFileDownload(Long memberId, Long fileId)
			throws Exception;

	/**
	 * 文件下载记录保存
	 */
	public void saveFileDownload(FileDownload fileDownload);

	/**
	 * 文件下载记录查询
	 */
	public List<FileDownload> searchFiledownload(String condition,
			String textfield, SearchManager searchManager, boolean isPaginate);

	/**
	 * 文件下载记录删除
	 */
	public void deleteFileDownload(Long id) throws Exception;

}
