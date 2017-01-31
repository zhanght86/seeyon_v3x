package com.seeyon.v3x.indexresume.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.indexresume.util.IndexResumeConstants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;

/**
 * @author zhangyong
 * @since V3.20
 */
public class IndexResumeDao extends  HibernateDaoSupport {
  private final static Log logger = LogFactory.getLog(IndexResumeDao.class);
  private Map<ApplicationCategoryEnum,String> map1=null;
  private Map<ApplicationCategoryEnum,String> map2=null;
  /**
   * 如下类型通用此方法:
   * form: collaboration:meeting:bulletin:news:calendar
   * @param base
   * @param starDate
   * @param endDate
   * @return
   */
  public Integer findAllV3xEntityCount(final String base,final String properties,final String starDate,final String endDate,final String... isForm)
  {
		String member="";
		if(base.equals(V3xOrgMember.class.getName()))
		{
			member="isDeleted ='0' and enabled='1' and ";
		}
	  String queryCount="select count(id)  from  "+base+" where "+member+properties+">=? and "+properties+"<=? "+(isForm.length>0?" "+isForm[0]:"");
	  
	  return (Integer)getHibernateTemplate().find(queryCount,new Object[]{Datetimes.parseDatetime(starDate),Datetimes.parseDatetime(endDate)}).listIterator().next();
  }

  /**
   * 如下类型通用此方法:
   * form: collaboration:meeting:bulletin:news:calendar
   * @param base
   * @param starDate
   * @param endDate
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Long> findAllV3xEntityList(final String base,final String properties,final String starDate,final String endDate,final int fromIndex,final String... isForm)
  {
	  return (List<Long>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException, SQLException {
				String member="";
				if(base.equals(V3xOrgMember.class.getName()))
				{
					member="isDeleted ='0' and enabled='1' and ";
				}
		String hql="select id from  "+base+" where "+member+properties+">=? and "+properties+"<=? "+(isForm.length>0?" "+isForm[0]:"")+" order by "+properties+" desc";
		Query query = null;		
		
		query = session.createQuery(hql);
		query.setParameter(0, Datetimes.parseDatetime(starDate));
		query.setParameter(1, Datetimes.parseDatetime(endDate));
		query.setFirstResult(fromIndex);
		query.setMaxResults(IndexResumeConstants.PAGE_SIZE);
		
		return query.list();
			}
		});
  }
  public List<Long> findAppTypeIdList(int appType,String starDate,String endDate, int fromIndex)
  {
	  initMap();
	  List<Long> colLongList =null;
	  ApplicationCategoryEnum e=ApplicationCategoryEnum.valueOf(appType);
		switch (e) {
		case form:
			colLongList=findAllV3xEntityList(map1.get(e),"createDate",starDate,endDate,fromIndex,"and bodyType='FORM'");
			logger.debug(map1.get(e));
			break;
		case collaboration:
			colLongList=findAllV3xEntityList(map1.get(e),"createDate",starDate,endDate,fromIndex,"and bodyType<>'FORM'");
			logger.debug(map1.get(e));
			break;
		case meeting:
		case bulletin:
		case news:
		case calendar:
			colLongList=findAllV3xEntityList(map1.get(e),"createDate",starDate,endDate,fromIndex);
			logger.debug(map1.get(e));
			break;
		case doc:
		case plan:
		case organization:
		case taskManage:
			colLongList=findAllV3xEntityList(map2.get(e),"createTime",starDate,endDate,fromIndex);
			logger.debug(map2.get(e));
			break;
		case bbs:
			colLongList= findAllV3xEntityList("V3xBbsArticle","issueTime",starDate,endDate,fromIndex);
			logger.debug("V3xBbsArticle");
			break;
		case inquiry:
			colLongList=findAllV3xEntityList("InquirySurveybasic","sendDate",starDate,endDate,fromIndex);
			logger.debug("InquirySurveybasic");
			break;
		default:
			break;
		}
		return colLongList;
  }
  
  public Integer findPageCount(int appType,String starDate,String endDate)
  {
	  initMap();
	  Integer count=0;
	  ApplicationCategoryEnum e=ApplicationCategoryEnum.valueOf(appType);
		switch (e) {
		case form:
			count= findAllV3xEntityCount(map1.get(e),"createDate",starDate,endDate,"and bodyType='FORM'");
			break;
		case collaboration:
			count= findAllV3xEntityCount(map1.get(e),"createDate",starDate,endDate,"and bodyType<>'FORM'");
			break;
		case meeting:
		case bulletin:
		case news:
		case calendar:
			count= findAllV3xEntityCount(map1.get(e),"createDate",starDate,endDate);
			break;
		case doc:
		case plan:
		case organization:
		case taskManage:
			count= findAllV3xEntityCount(map2.get(e),"createTime",starDate,endDate);
			break;
		case bbs:
			count= findAllV3xEntityCount("V3xBbsArticle","issueTime",starDate,endDate);
			break;
		case inquiry:
			count= findAllV3xEntityCount("InquirySurveybasic","sendDate",starDate,endDate);
			break;
		default:
			break;
		}
		return count;
  }

	private void initMap() {
		if (map1 == null) {
			map1 = new HashMap<ApplicationCategoryEnum, String>();
			map1.put(ApplicationCategoryEnum.form, "ColSummary");
			map1.put(ApplicationCategoryEnum.collaboration, "ColSummary");
			map1.put(ApplicationCategoryEnum.meeting, "MtMeeting");
			map1.put(ApplicationCategoryEnum.bulletin, "BulData");
			map1.put(ApplicationCategoryEnum.news, "NewsData");
			map1.put(ApplicationCategoryEnum.calendar, "CalEvent");
		}
		
		if (map2 == null) {
			map2 = new HashMap<ApplicationCategoryEnum, String>();
			map2.put(ApplicationCategoryEnum.doc, "DocResource");
			map2.put(ApplicationCategoryEnum.plan, "Plan");
			map2.put(ApplicationCategoryEnum.organization, "V3xOrgMember");
			map2.put(ApplicationCategoryEnum.taskManage, "TaskInfo");
		}
	}
	
}