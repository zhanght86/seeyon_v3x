/**
 * 
 */
package com.seeyon.v3x.bbs.dao;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsArticleIssueArea;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class BbsArticleIssueAreaDao extends BaseHibernateDao<V3xBbsArticleIssueArea>{

	//添加发布范围
	public void addArticleIssueAreas(List<V3xBbsArticleIssueArea> list) throws Exception {
		for (V3xBbsArticleIssueArea v3xBbsArticleIssueArea : list) {
			this.save(v3xBbsArticleIssueArea);
		}
	}
	

}
