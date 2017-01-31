package com.seeyon.v3x.edoc.dao;

//import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import com.seeyon.v3x.edoc.domain.EdocMark;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.util.SQLWildcardUtil;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
/**
 * Data access object (DAO) for domain model class EdocMark.
 * @see .EdocMark
 * @author MyEclipse - Hibernate Tools
 */
public class EdocMarkDAO extends BaseHibernateDao<EdocMark> {

    private static final Log log = LogFactory.getLog(EdocMarkDAO.class);	

    /**
     * 方法描述：保存公文文号
     */
    public void save(EdocMark edocMark) {
        log.debug("saving EdocMark instance");
        try {
            super.save(edocMark);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
    /**
     * 判断文号是否被占用
     * @param edocId     公文id
     * @param edocMark   文号
     * @return   true 被占用 false 未占用
     */
    public boolean isUsed(Long edocId){
    	
    	boolean used = false;    	
    	String hsql = "select count(*) as count from EdocMark as mark where mark.edocId=?";
    	List list = super.find(hsql, edocId);
    	if (list != null && !list.isEmpty() && list.size() > 0) {
    		if (list.get(0) != null) {
    			int count = (Integer)list.get(0);    			
    			used = count > 0;    			
    		}
    	}
    	return used;
    }
    
    /**
     * 判断文号是否被占用
     * @param edocMark   文号
     * @return   true 被占用 false 未占用
     */
    public boolean isUsed(String markStr,String edocId){
    	
    	Long edocSummaryId=0L;
    	boolean used = false; 
    	try{edocSummaryId=Long.parseLong(edocId);}catch(Exception e){}
    	String hsql = "select count(*) as count from EdocMarkHistory as mark where mark.docMark=? and mark.edocId<>? and mark.edocId<>-1";
    	if(markStr!=null){
    		markStr=SQLWildcardUtil.escape(markStr.trim());
    	}
    	List list = super.find(hsql, markStr,edocSummaryId);
    	if (list != null && !list.isEmpty() && list.size() > 0) {
    		if (list.get(0) != null) {
    			int count = (Integer)list.get(0);    			
    			used = count > 0;    			
    		}
    	}
    	return used;
    }

    public List<EdocMark> findEdocMarkByCategoryId(Long categoryId){
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.categoryId = ? order by mark.docMarkNo", categoryId);
    	return list;
    }
    
    public List<EdocMark> findEdocMarkByCategoryId(Long categoryId,Integer docMarkNo){
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.categoryId = ? and mark.docMarkNo=? order by mark.docMarkNo", categoryId,docMarkNo);
    	return list;
    }
    /**
     * 判断是否有其他公文使用与此流水（包括大流水和小流水）相关的文号
     * @param categoryId     流水ID
     * @param docMarkNo		 docMark表的文号的流水值
     * @param edocId		 公文ID
     * @return               true：有其他公文使用此流水，false:无其他公文使用此流水。
     */
    public boolean judgeOtherEdocUseCategroy(Long categoryId,Integer docMarkNo,Long edocId){
    	String hql="from EdocMark as mark where mark.categoryId = ? and mark.docMarkNo=? and edocId!=?";
    	return super.getQueryCount(hql, new Object[]{categoryId,docMarkNo,edocId}, new Type[]{Hibernate.LONG,Hibernate.INTEGER,Hibernate.LONG})>0;
    }
    public void deleteEdocMarkByCategoryIdAndNo(Long categoryId,Integer docMarkNo){
    	String hql="delete from EdocMark as mark where mark.categoryId = :categoryId and mark.docMarkNo=:docMarkNo";
    	Map<String,Object> nameParameters=new HashMap<String,Object>();
    	nameParameters.put("categoryId", categoryId);
    	nameParameters.put("docMarkNo",docMarkNo);
    	super.bulkUpdate(hql, nameParameters);
    }
    public List<EdocMark> findEdocMarkByEdocIdOrDocMark(Long edocId,String docMark){
    	if(docMark!=null){
    		docMark=SQLWildcardUtil.escape(docMark.trim());
    	}
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocId = ? or mark.docMark=?", edocId,docMark);
    	return list;
    }
    
    public List<EdocMark> findEdocMarkByEdocIdOrDocMark(Long edocId,String docMark,String docMark2){
    	if(docMark!=null){
    		docMark=SQLWildcardUtil.escape(docMark.trim());
    	}
    	if(docMark2!=null){
    		docMark2=SQLWildcardUtil.escape(docMark2.trim());
    	}
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocId = ? or mark.docMark=? or mark.docMark=?", edocId,docMark,docMark2);
    	return list;
    }
    
    public List<EdocMark> findEdocMarkByMarkDefId(Long markDefId){
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocMarkDefinition.id = ? order by mark.docMarkNo", markDefId);
    	return list;
    }
    /**
     * 断号查询，去掉重复
     * @param markDefId
     * @return
     */
    public List<EdocMark> findEdocMarkByMarkDefId4Discontin(Long markDefId){
    	
    	List<EdocMark> list = findEdocMarkByMarkDefId(markDefId);
    	List<EdocMark> nlist = new ArrayList<EdocMark>();
    	Hashtable<String,String> hs=new Hashtable<String,String>();
    	
    	for(EdocMark em:list)
    	{
    		if(!hs.containsKey(em.getDocMark()))
    		{
    			hs.put(em.getDocMark(),em.getDocMark());
    			nlist.add(em);
    		}
    	}    	
    	return nlist;
    }
    
    public EdocMark findEdocMarkByEdocSummaryId(Long edocSummaryId){
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocId = ? order by mark.docMarkNo", edocSummaryId);
    	if(list!=null && list.size()>0)
    	{
    		return list.get(0);
    	}
    	return null;
    }
    /**
     * 根据公文文号和公文ID来查找edoc_mark表中的记录。
     * @param edocSummaryId 公文ID
     * @param edoc_mark	    公文文号
     * @param markNum	    联合发文的时候：第一套公文文号还是第二套。
     * @return
     */
    public EdocMark findEdocMarkByEdocSummaryIdAndEdocMark(Long edocSummaryId,String edocMark,int markNum){
    	if(edocMark!=null){
    		edocMark=SQLWildcardUtil.escape(edocMark.trim());
    	}
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocId = ? and mark.docMark=? and mark.markNum=?  order by mark.docMarkNo desc ", edocSummaryId,edocMark,markNum);
    	if(list!=null && list.size()>0)
    	{
    		return list.get(0);
    	}
    	return null;
    }
    public List<EdocMark> findEdocMarkByEdocSummaryIdAndNum(Long edocSummaryId,int markNum){
    	List<EdocMark> list = super.find("from EdocMark as mark where mark.edocId = ? and mark.markNum=? order by mark.docMarkNo", edocSummaryId,markNum);
    	if(list!=null && list.size()>0)
    	{
    		return list;
    	}
    	return null;
    }
    
   public void deleteEdocMarkByIds(List<Long> ids){
	   String hql="delete from EdocMark where id in(:ids)";
	   Map<String,Object> nameParameters=new HashMap<String,Object>();
	   nameParameters.put("ids", ids);
	   super.bulkUpdate(hql, nameParameters);
   }
}