package com.seeyon.v3x.hbcb.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hbcb.domain.FileDownload;

public class FileDownloadDao extends BaseHibernateDao<FileDownload>  {
	
	/**
	 * 返回某员工某文件的下载记录
	 */
	@SuppressWarnings("unchecked")
	public FileDownload getFileDownload(Long memberId, Long fileId) throws Exception {
		FileDownload rc = new FileDownload();
		String hql = "From FileDownload where memberId = :memberId and fileId = :fileId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("memberId", memberId);
		params.put("fileId", fileId);
		List<FileDownload> list = this.find(hql, -1, -1, params);
		if(list != null && !list.isEmpty()){
			rc = list.get(0);
		}
		return rc;
	}

}
