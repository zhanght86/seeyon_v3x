package com.seeyon.v3x.blog.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
/**
 * 类描述： 博客分类操作
 * 创建日期：2007-08-01
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 * @see BaseHibernateDao
 */
public class BlogFamilyDao extends BaseHibernateDao<BlogArticle> {
	
	@SuppressWarnings("unchecked")
	public List<BlogArticle> listArticle(Long familyId, String condition,
			String textfield, String textfield1) {
		condition = StringUtils.trimToEmpty(condition);
		textfield = StringUtils.trimToEmpty(textfield);
		textfield1 = StringUtils.trimToEmpty(textfield1);
		
		//当前用户信息
		User curUser = CurrentUser.get();   
		Long curUserId = curUser.getId();
		Long curUserDepartmentId = curUser.getDepartmentId();
		
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<Object> indexParameter = new ArrayList<Object>();

		StringBuffer hql = new StringBuffer();
		hql.append("select BlogArticle from " + BlogArticle.class.getName() + " as BlogArticle ,");
		hql.append(			  BlogFavorites.class.getName() + " as BlogFavorites ,");
		hql.append(			  BlogEmployee.class.getName() + " as BlogEmployee ");
		hql.append(" where BlogArticle.id = BlogArticleIssueArea.BlogArticle.id");
		hql.append(" and BlogArticle.BlogFamily.id = ?");
		hql.append(" and (");
		hql.append("      (BlogArticleIssueArea.moduleType='Member' and  BlogArticleIssueArea.moduleId= ? ) ");
		hql.append("	  or");
		hql.append("	  (BlogArticleIssueArea.moduleType='Department' and  BlogArticleIssueArea.moduleId= ? ) ");
		hql.append("	  or");
		hql.append("	  (BlogArticle.issueUserId = ?)");
		hql.append("	  or");
		hql.append("	  (BlogArticle.BlogFamily.id=BlogFamilyAuth.BlogFamily.id and BlogFamilyAuth.authType = 0 and BlogFamilyAuth.moduleId= ?)");
		hql.append("     )");
		hql.append(" and BlogArticle.state = 0");
		
		indexParameter.add(familyId);
		indexParameter.add(curUserId);
		indexParameter.add(curUserDepartmentId);
		indexParameter.add(curUserId);
		indexParameter.add(curUserId);
		
		int arrLen = 1;
		if (textfield1.length() > 0) {
			arrLen = 3;
			hql.append(" and issueTime>=:timestamp");
			hql.append(" and issueTime<=:timestamp1 )");
			
			parameterMap.put("timestamp", Datetimes.getTodayLastTime(textfield));
			parameterMap.put("timestamp1", Datetimes.getTodayLastTime(textfield1));
		}
		else if (textfield.length() > 0) {
			arrLen = 1;
			if (condition.equals("subject")) {
				hql.append(" and BlogArticle.articleName like :subject");
				parameterMap.put("subject", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}
			if (condition.equals("issueUser")) {
				hql.append(" and BlogArticle.employeeId in");
				hql.append("(select orgMember.id from OrgMember orgMember");
				hql.append(" where orgMember.name like :MemberName)");
				parameterMap.put("MemberName", "%" + SQLWildcardUtil.escape(textfield) + "%");
			}
		}
		
		hql.append(" order by BlogArticle.topSequence desc,BlogArticle.eliteFlag desc,BlogArticle.issueTime desc");
				
		return super.find(hql.toString(), parameterMap, indexParameter);
	}

	// 根据分类ID获取分类信息
	public BlogFamily getFamilyById(Long familyId) throws Exception {
		BlogFamily blogFamily = null;		
		String hsql = "from BlogFamily as family where family.id=?";
		List list = super.find(hsql, familyId);
		if (list != null && !list.isEmpty()) {
			blogFamily = (BlogFamily)list.get(0); 
		}
		return blogFamily;
	}

	// 删除主题
	@SuppressWarnings("unchecked")
	public void delArticle(String articleId) throws Exception {
		Map<String , Object> map = new HashMap<String , Object>();
		map.put("state", 0 );

		this.update(new Long(articleId), map);
	}

//	查询
//	public List<BlogArticle> querylistArticle(String condition, String textfield, String textfield1) {
//		
//		condition = StringUtils.trimToEmpty(condition);
//		textfield = StringUtils.trimToEmpty(textfield);
//		textfield1 = StringUtils.trimToEmpty(textfield1);
//		
//		if(textfield1.length()<=0)
//		{	
//		Session session = this.getSession();
//		
//		StringBuffer hql = new StringBuffer();
//		hql.append("select count(*) ");
//		hql.append(" from "+ ProjectMember.class.getName());
//		Query query = session.createQuery(hql.toString());
//		return query.list();
//		
//		}
//		else{
//			Session session = this.getSession();
//			
//			StringBuffer hql = new StringBuffer();
//			hql.append("select count(*) ");
//			hql.append(" from "+ ProjectMember.class.getName());
//			Query query = session.createQuery(hql.toString());
//			return query.list();
//		}
//		
//	}
//	
//	public List execute(final String hsql){
//		return (List) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				return session.createQuery(hsql).list();
//			}
//		}, true);
//	}


}

