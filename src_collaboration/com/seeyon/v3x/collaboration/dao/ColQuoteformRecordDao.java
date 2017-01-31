package com.seeyon.v3x.collaboration.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ColQuoteformRecord;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class ColQuoteformRecordDao extends BaseHibernateDao<ColQuoteformRecord>{
	
	public void create(ColQuoteformRecord colQuoteformRecord){
		super.save(colQuoteformRecord) ;
	}
	
	public void delObj(ColQuoteformRecord colQuoteformRecord){
		super.delete(colQuoteformRecord) ;
	}
	
	public void delQuoteformRecord(Long summaryId ,Long refSummaryId ,String fieldName){
		String hql = "delete from ColQuoteformRecord  colQuoteformRecord where colQuoteformRecord.colSummaryId =:summaryId and colQuoteformRecord.refColSummaryId =:refSummaryId and colQuoteformRecord.fieldName =:fieldName"  ;
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("refSummaryId", refSummaryId) ;
		nameParameters.put("fieldName", fieldName) ;
		super.bulkUpdate(hql, nameParameters) ;
	}
	
	public void delAllQuoteformRecord(Long summaryId ){
		String hql = "delete from ColQuoteformRecord colQuoteformRecord where colQuoteformRecord.colSummaryId =:summaryId "  ;
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;

		super.bulkUpdate(hql, nameParameters) ;
	}
	
	public List<Long> getQuoteIdList(Long summaryId ){
		String hql = "select distinct colQuoteformRecord.colSummaryId from ColQuoteformRecord colQuoteformRecord " +
				" where colQuoteformRecord.refColSummaryId =:summaryId "  ;
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		return super.find(hql, -1, -1, nameParameters) ;
		
	}
	public ColQuoteformRecord getColQuoteformRecord(Long summaryId,String fieldName){
		String hql="from ColQuoteformRecord colQuoteformRecord where colQuoteformRecord.fieldName=:fieldName and colQuoteformRecord.colSummaryId=:summaryId ";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("fieldName", fieldName) ;
		return (ColQuoteformRecord)super.findUnique(hql, nameParameters);
	}
	public ColQuoteformRecord getColQuoteformRecord(Long summaryId,Long subRecordId,String fieldName){
		String hql="from ColQuoteformRecord colQuoteformRecord where colQuoteformRecord.fieldName=:fieldName and colQuoteformRecord.colSummaryId=:summaryId and colQuoteformRecord.subRecordId = :subRecordId";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("fieldName", fieldName) ;
		nameParameters.put("subRecordId", subRecordId) ;
		return (ColQuoteformRecord)super.findUnique(hql, nameParameters);
	}
	public void delChangedColQuoteRecord(Long summaryId,String fieldName){
		String hql="delete from ColQuoteformRecord colQuoteformRecord where colQuoteformRecord.fieldName=:fieldName and colQuoteformRecord.colSummaryId=:summaryId ";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("fieldName", fieldName) ;
		super.bulkUpdate(hql, nameParameters);
	}
	public void delChangedColQuoteRecord(Long summaryId,String fieldName, Long subRecordId){
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		String hql="delete from ColQuoteformRecord colQuoteformRecord where colQuoteformRecord.fieldName=:fieldName and colQuoteformRecord.colSummaryId=:summaryId ";
		if(subRecordId != null){
			hql += " and colQuoteformRecord.subRecordId=:subRecordId";
			nameParameters.put("subRecordId", subRecordId) ;
		}
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("fieldName", fieldName) ;
		super.bulkUpdate(hql, nameParameters);
	}
}
