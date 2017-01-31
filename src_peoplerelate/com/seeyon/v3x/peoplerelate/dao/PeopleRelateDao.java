package com.seeyon.v3x.peoplerelate.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class PeopleRelateDao extends BaseHibernateDao<PeopleRelate>
{

    @SuppressWarnings("unchecked")
    public List<PeopleRelate> find(String hsql) throws Exception
    {
        return getHibernateTemplate().find(hsql);
    }
    /**
     * 通过关联类型和被关联人员查询出所有主动关联人员集合
     * @param long relatedMemberId
     * @param int type  关联类型  1.上级    2.秘书    3.下级   4. 我的同事
     * @return List<Long>
     * @throws Exception
     */
    public  List<PeopleRelate>  getRelateMemberIdList(long relatedMemberId, int type)
    {
        Object[] values={relatedMemberId,type};
        String hql="from PeopleRelate p where p.relateMemberId =? and p.relateType=?";
        return super.find(hql, values);
    }
    public  List<PeopleRelate>  getRelatedMemberIdList(long relatedMemberId, int type)
    {
        Object[] values={relatedMemberId,type};
        String hql="from PeopleRelate p where p.relatedMemberId =? and p.relateType=?";
        return super.find(hql, values);
    }
    public void deletePeopleRelateRepeat(final Long relateId, final Long relatedId, final int flag)
    {
        this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session s) throws HibernateException, SQLException
            {
                Query query = s
                        .createQuery("delete  PeopleRelate p where p.relatedMemberId =? and p.relateMemberId=? and p.relateWsbs=?");
                query.setLong(0, relatedId);
                query.setLong(1, relateId);
                query.setInteger(2, flag);
                query.executeUpdate();
                return null;
            }
        });
    }
    public void deletePeopleRelatebyRelateType(final List relatedList, final Long relateId,
            final int type)
    {
        this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session s) throws HibernateException, SQLException
            {
                Query query = s
                        .createQuery("delete  PeopleRelate  where relatedMemberId not in (:relatedM) and relateMemberId=:relateM and relateType=:type");
                query.setParameterList("relatedM", relatedList);
                query.setLong("relateM", relateId);
                query.setInteger("type", type);
                query.executeUpdate();
                return null;
            }
        });
    }
    public void deletePeopleRelatebyType(final List relatedList, final Long relateId, final int type)
    {
        this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session s) throws HibernateException, SQLException
            {
                Query query = s
                        .createQuery("delete  PeopleRelate where relateMemberId not in (:relatedM) and relatedMemberId=:relateM and relateType=:type");
                query.setParameterList("relatedM", relatedList);
                query.setLong("relateM", relateId);
                query.setInteger("type", type);
                query.executeUpdate();
                return null;
            }
        });
    }
    public void deletePeopleRelatebyTypeAndWS(final Long relatedList, final int type, final int ws) {
        this.getHibernateTemplate().execute(new HibernateCallback() {
        public Object doInHibernate(Session s) throws HibernateException, SQLException {
          Query query = s.createQuery("delete  PeopleRelate where relateMemberId=:relateM and relateType=:type and relateWsbs=:ws");
          query.setLong("relateM", relatedList);
          query.setInteger("type",type);
          query.setInteger("ws",ws);
          query.executeUpdate();
          return null;
        }
      });
    }
    
    public void deleteRelatedbyType(final Long related, final int type) {
        this.getHibernateTemplate().execute(new HibernateCallback() {
        public Object doInHibernate(Session s) throws HibernateException, SQLException {
          Query query = s.createQuery("delete  PeopleRelate where relatedMemberId=:relateM and relateType=:type");
          query.setLong("relateM", related);
          query.setInteger("type",type);
          query.executeUpdate();
          return null;
        }
      });
    }
    
    @SuppressWarnings("unchecked")
    public List<PeopleRelate> getNotSelectedRelatebyTypeAndWS(final List relatedList, final Long relateMemberId,final int type) {
        return (List<PeopleRelate>) getHibernateTemplate().execute(new HibernateCallback() {
        public Object doInHibernate(Session s) throws HibernateException, SQLException {
         Query query = s.createQuery("from  PeopleRelate  where relatedMemberId not in (:relatedM) and relateMemberId=:relateM and relateType=:type");
               query.setParameterList("relatedM", relatedList);
               query.setLong("relateM", relateMemberId);
               query.setInteger("type", type);
               return query.list();
        }
      });
    }
    
    public void deletePeopleRelateByOne(final Long related, final Long relateId,
            final int type)
    {
        this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session s) throws HibernateException, SQLException
            {
                Query query = s
                        .createQuery("delete  PeopleRelate  where relatedMemberId =:relatedM  and relateMemberId=:relateM and relateType=:type");
                query.setLong("relatedM", related);
                query.setLong("relateM", relateId);
                query.setInteger("type", type);
                query.executeUpdate();
                return null;
            }
        });
    }
}
