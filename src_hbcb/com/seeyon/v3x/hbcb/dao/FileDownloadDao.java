package com.seeyon.v3x.hbcb.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hbcb.domain.FileDownload;
import com.seeyon.v3x.util.Strings;

public class FileDownloadDao extends BaseHibernateDao<FileDownload> {

	private static final Log log = LogFactory.getLog(FileDownloadDao.class);

	/**
	 * 返回某员工某文件的下载记录
	 */
	@SuppressWarnings("unchecked")
	public FileDownload getFileDownload(Long memberId, Long fileId)
			throws Exception {
		FileDownload rc = new FileDownload();
		String hql = "From FileDownload where memberId = :memberId and fileId = :fileId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("memberId", memberId);
		params.put("fileId", fileId);
		List<FileDownload> list = this.find(hql, -1, -1, params);
		if (list != null && !list.isEmpty()) {
			rc = list.get(0);
		}
		return rc;
	}

	/**
	 * 文件下载搜索
	 * 
	 * @param condition
	 *            查询条件
	 * @param textfield
	 *            查询值
	 * @param searchManager
	 * 
	 */
	public static List<FileDownload> searchFiledownload(String condition,
			String textfield, SearchManager searchManager, boolean isPaginate) {
		Long accountId = CurrentUser.get().getLoginAccount();
		StringBuilder strbuf = new StringBuilder();
		Map<String, Object> param = new HashMap<String, Object>();
		strbuf.append("select a from " + FileDownload.class.getName()
				+ " as a ");
		strbuf.append(" where a.state=0");

		if (!CurrentUser.get().isSystemAdmin()) {
			strbuf.append(" and a.accountId=:accountId");
			param.put("accountId", accountId);
		}

		parseQueryCondition(condition, textfield, strbuf, param);
		strbuf.append(" order by a.ts desc");

		List<FileDownload> list = searchManager.searchByHql(strbuf.toString(),
				param, isPaginate);

		return list;
	}

	private static void parseQueryCondition(String condition, String textfield,
			StringBuilder strbuf, Map<String, Object> param) {

		if (Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
			if (condition.equals("memberName")) {
				strbuf.append(" and a.member.name like :textfield");
				param.put("textfield", "%" + textfield + "%");
			} else if (condition.equals("filename")) {
				strbuf.append(" and a.filename like :textfield");
				param.put("textfield", "%" + textfield + "%");
			} else if (condition.equals("departmentId")) {
				strbuf.append(" and a.departmentId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}
		} else if (condition != null && condition.equals("all")) {
			// isPaginate = true;
		} else {
			// isPaginate = true;
		}
	}

}
