package com.seeyon.v3x.edoc.dao;

import java.util.*;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.*;
import com.seeyon.v3x.edoc.domain.EdocOpinion.OpinionType;

public class EdocOpinionDao extends BaseHibernateDao<EdocOpinion> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 根据排序读取最终意见，不要改变读取排列顺序
	 * @param summaryId
	 * @return
	 */
	/*
	public List<EdocOpinion> findEdocOpinionBySummaryId(long summaryId)
	{
		String hsql="from EdocOpinion as eo where eo.edocSummary.id=? order by eo.createTime desc,eo.nodeId,eo.createUserId";
		Object[]values={new Long(summaryId)};
		List<EdocOpinion> ls=super.find(hsql, values);
		return ls;
	}
	*/
	/**
	 * 根据排序读取最终意见
	 * @param summaryId
	 * @param timeSort 排序规则
	 * @return
	 */
	public List<EdocOpinion>findEdocOpinionBySummaryId(long summaryId,boolean timeSort){
		String hsql = "";
		if(timeSort) {
			hsql ="from EdocOpinion as eo where eo.edocSummary.id=? order by eo.createTime desc,eo.nodeId,eo.createUserId";
		}else {
		    hsql="from EdocOpinion as eo where eo.edocSummary.id=? order by eo.createTime asc,eo.nodeId,eo.createUserId";
		}		
		Object[]values={new Long(summaryId)};
		List<EdocOpinion> ls=super.find(hsql, values);
		return ls;
	}
	
	/**
	 * 只读取每个节点每个人的最后处理意见，回退处理多次，只取最后一次
	 * @param summaryId
	 * @return
	 */
	public List<EdocOpinion> findLastEdocOpinionBySummaryId(long summaryId,boolean timeSort)
	{
		List<EdocOpinion> ls=findEdocOpinionBySummaryId(summaryId,timeSort);
		List<EdocOpinion> nls=new ArrayList<EdocOpinion>();
		Hashtable <String,EdocOpinion> hs=new Hashtable <String,EdocOpinion>();
		String key;
		for(EdocOpinion eo:ls)
		{
			//兼容历史数据，历史数据没法判断最终处理，要全部显示出来；发起人附言全部显示出来
			if(eo.getNodeId()==-1 || eo.getOpinionType()==EdocOpinion.OpinionType.senderOpinion.ordinal())
			{
				nls.add(eo);
				continue;
			}
			key=Long.toString(eo.getNodeId())+eo.getCreateUserId();
			if(hs.get(key)!=null)
			{
				//排序改变之后,增加日期比较判断
				if(hs.get(key).getCreateTime().after(eo.getCreateTime())){continue;}
				nls.remove(hs.get(key));			
			}		
			nls.add(eo);
			hs.put(key,eo);
		}
		return nls;
	}
	
	public EdocOpinion findBySummaryIdAndAffairId(long summaryId,long affairId)
	{
		EdocOpinion eo=null;
		String hsql="from EdocOpinion as eo where eo.edocSummary.id=? and eo.affairId=?";
		Object[]values={new Long(summaryId),new Long(affairId)};
		List<EdocOpinion> ls=super.find(hsql, values);
		if(ls!=null && ls.size()>0){eo=ls.get(0);}
		return eo;		
	}

	/**
	 * 删除处理意见，不包括发起人意见
	 * 
	 * @param summaryId
	 */
	public void deleteDealOpinion(Long summaryId)
	{		
		String hsql="delete from EdocOpinion as eo where eo.edocSummary.id=? and eo.opinionType<>"+EdocOpinion.OpinionType.senderOpinion.ordinal();
		//String hsql="from EdocOpinion as eo where eo.edocSummary.id="+summaryId+" and eo.opinionType<>"+EdocOpinion.OpinionType.senderOpinion.ordinal();
		super.bulkUpdate(hsql, null, summaryId);
		//System.out.println("eeeeer:");
		//super.delete(hsql, values);		
	}
	public void deleteOpinionBySummaryId(Long summaryId)
	{
		String hsql="delete from EdocOpinion as eo where eo.edocSummary.id=?";
		super.bulkUpdate(hsql, null, summaryId);
	}

	/**
	 * 根据公文id查询根据节点元素绑定的排序了的意见,只读取每个节点每个人的最后处理意见，回退处理多次，只取最后一次
	 * 
	 * @param summaryId
	 *            公文id
	 * @param policy
	 *            节点元素名
	 * @param sortType
	 *            排序方式
	 * @param isOnlyShowLast
	 *            同一个人的意见，是否只显示最新的一条
	 * @param isbound  查询的意见是绑定的还是未绑定的意见  
	 * @return
	 */
	public List<Object[]> findLastSortOpinionBySummaryIdAndPolicy(
			long summaryId, List<String> policy, String sortType,
			boolean isOnlyShowLast,boolean isBound) {
		List ls = null;
		Map parameterMap = new HashMap();
		parameterMap.put("summaryId", summaryId);
		if(policy!=null &&!policy.isEmpty()){
			parameterMap.put("policy", policy);
		}

		if (isOnlyShowLast) {
			StringBuffer hqlBuffer = new StringBuffer();
			hqlBuffer
					.append("select edocOpinion,dept.name from EdocOpinion as edocOpinion ")
					.append(", V3xOrgDepartment as dept, V3xOrgLevel as orgLevel, V3xOrgMember as orgMember")
					.append(" where dept.id = orgMember.orgDepartmentId")
					.append(" and orgLevel.id = orgMember.orgLevelId")
					.append(" and orgMember.id = edocOpinion.createUserId")
					.append(" and edocOpinion.id in ")
					.append(" 	(")
					.append(" 	SELECT tempEdocOption1.id ")
					.append(" 	FROM EdocOpinion as tempEdocOption1 ")
					.append(" 	WHERE tempEdocOption1.createTime = ")
					.append("		(")
					.append(" 		SELECT MAX(tempEdocOption2.createTime) ")
					.append(" 		FROM EdocOpinion tempEdocOption2 ")
					.append(" 		WHERE tempEdocOption2.createUserId = tempEdocOption1.createUserId")
					.append(" 		AND tempEdocOption2.edocSummary.id = :summaryId")
					//不显示暂存代办的意见。
					.append("       and tempEdocOption2.opinionType != "+EdocOpinion.OpinionType.provisionalOpinoin.ordinal());
					if(policy!= null && !policy.isEmpty()){
						if(isBound){
							if(policy.contains("niwen") || policy.contains("dengji")){ //拟文登记policy 为null
								hqlBuffer.append(" and ( tempEdocOption2.policy in (:policy) or   tempEdocOption2.policy is null)");
							}else{
								hqlBuffer.append(" and  tempEdocOption2.policy in (:policy) ");
							}
						}else{
							if(policy.contains("niwen") || policy.contains("dengji")){
								hqlBuffer.append(" and tempEdocOption2.policy not in (:policy) ");
							}else{
								hqlBuffer.append(" and (tempEdocOption2.policy not in (:policy) or tempEdocOption2.policy is null) ");
							}
						}
					}
					hqlBuffer.append("		)")
					.append(" 	AND tempEdocOption1.edocSummary.id = :summaryId ")
					.append("	)")
					.append(" and edocOpinion.edocSummary.id = :summaryId");
					
					hqlBuffer.append(" order by ");
					if ("0".equals(sortType)) {
						hqlBuffer.append(" edocOpinion.createTime");
					} else if ("1".equals(sortType)) {
						hqlBuffer.append("edocOpinion.createTime desc");
					} else if ("2".equals(sortType)) {
						//sunj:公文意见框按职务级别排时，如果职务级别相同，按人员排序号排（小的在前），
						//如果人员编号相同，按时间顺序排（先处理的排在前）
						hqlBuffer.append("orgLevel.levelId desc,orgMember.sortId asc,edocOpinion.createTime asc ");
					} else if ("3".equals(sortType)) {
						hqlBuffer.append("dept.sortId, orgLevel.levelId desc,orgMember.code asc,edocOpinion.createTime asc");
					}else
						hqlBuffer.append(" edocOpinion.createTime");
			ls = find(hqlBuffer.toString(), -1, -1, parameterMap);
		} else {
			// 全部显示
			/*
			 * SELECT a.* FROM edoc_opinion a, v3x_org_department b,
			 * v3x_org_level d, v3x_org_member c
			 * 
			 * WHERE b.ID = c.org_department_id AND d.ID = c.org_level_id AND
			 * c.ID = a.create_user_id AND a.edoc_id = 1791605713944201898
			 * 
			 * ORDER BY b.sort_id ,d.sort_id,a.create_time ;
			 */

			List<Object> objects = new ArrayList<Object>();
			objects.add(summaryId);
			objects.add(policy);
			StringBuffer hqlBuffer = new StringBuffer();
			hqlBuffer
					.append(
							"select edocOpinion,dept.name from EdocOpinion as edocOpinion, V3xOrgDepartment as dept, V3xOrgLevel as orgLevel, V3xOrgMember as orgMember")
					.append(" where dept.id = orgMember.orgDepartmentId")
					.append(" and orgLevel.id = orgMember.orgLevelId").append(
							" and orgMember.id = edocOpinion.createUserId")
					.append(" and edocOpinion.edocSummary.id = :summaryId");
					if(policy!=null &&!policy.isEmpty()){
						if(isBound){
							if(policy.contains("niwen") || policy.contains("dengji")){ //拟文登记policy 为null
								hqlBuffer.append(" and ( edocOpinion.policy in (:policy) or   edocOpinion.policy is null)");
							}else{
								hqlBuffer.append(" and  edocOpinion.policy in (:policy) ");
							}
						}else{
							if(policy.contains("niwen") || policy.contains("dengji")){
								hqlBuffer.append(" and edocOpinion.policy not in (:policy) ");
							}else{
								hqlBuffer.append(" and (edocOpinion.policy not in (:policy) or edocOpinion.policy is null) ");
							}
						}
					}
					hqlBuffer.append(" order by ");
					if ("0".equals(sortType)) {
						hqlBuffer.append(" edocOpinion.createTime");
					} else if ("1".equals(sortType)) {
						hqlBuffer.append("edocOpinion.createTime desc");
					} else if ("2".equals(sortType)) {
						hqlBuffer.append("orgLevel.levelId desc,orgMember.sortId asc,edocOpinion.createTime asc ");
					} else if ("3".equals(sortType)) {
						hqlBuffer.append("dept.sortId, orgLevel.levelId desc,orgMember.code asc,edocOpinion.createTime asc");
					}else
						hqlBuffer.append("edocOpinion.createTime");
			ls = find(hqlBuffer.toString(), -1, -1, parameterMap);
		}
		return ls;
	}
	/**
	 * 根据公文id查询根据节点元素绑定的排序了的意见
	 * 
	 * @param summaryId
	 *            公文id
	 * @param policy
	 *            节点元素名
	 * @return
	 */
	public List findEdocOpinionBySummaryIdAndPolicy(long summaryId,
			String policy, String sortType) {
		/*
		 * SELECT a.* FROM edoc_opinion a, v3x_org_department b, v3x_org_level
		 * d, v3x_org_member c
		 * 
		 * WHERE b.ID = c.org_department_id AND d.ID = c.org_level_id AND c.ID =
		 * a.create_user_id AND a.edoc_id = 1791605713944201898
		 * 
		 * ORDER BY b.sort_id ,d.sort_id,a.create_time ;
		 */
		List<Object> objects = new ArrayList<Object>();
		objects.add(summaryId);
		objects.add(policy);
		StringBuffer hqlBuffer = new StringBuffer();
		hqlBuffer
				.append(
						"select edocOpinion,dept.name from EdocOpinion as edocOpinion, V3xOrgDepartment as dept, V3xOrgLevel as orgLevel, V3xOrgMember as orgMember")
				.append(" where dept.id = orgMember.orgDepartmentId").append(
						" and orgLevel.id = orgMember.orgLevelId").append(
						" and orgMember.id = edocOpinion.createUserId").append(
						" and edocOpinion.edocSummary.id = ?").append(
						" and edocOpinion.policy =?").append(
						" order by ");
		if ("0".equals(sortType)) {
			hqlBuffer.append(" edocOpinion.createTime");
		} else if ("1".equals(sortType)) {
			hqlBuffer.append("edocOpinion.createTime desc");
		} else if ("2".equals(sortType)) {
			hqlBuffer.append("orgLevel.levelId");
		} else if ("3".equals(sortType)) {
			hqlBuffer.append("dept.sortId");
		}else
			hqlBuffer.append("edocOpinion.createTime");
		return find(hqlBuffer.toString(), -1, -1, null, objects);
	}
	public List findOtherOpinionBySummaryId(Long summaryId) {
		List ls = new ArrayList();
		//查询是否有处理人的单位id为空的
		StringBuffer hql=new StringBuffer();
		Map paramMap = new HashMap();
		paramMap.put("summaryId", summaryId);
		hql.append("select edocOpinion from EdocOpinion as edocOpinion ");
		hql.append("where edocOpinion.edocSummary.id=:summaryId and ");
		hql.append("edocOpinion.createUserId in ( ");
		hql.append(" select orgMember.id from V3xOrgMember as orgMember where orgMember.id in (select opinion.createUserId from EdocOpinion as opinion where opinion.edocSummary.id=:summaryId) and (orgMember.orgDepartmentId=-1 or orgMember.orgLevelId is null) ");
		hql.append(")");
		List<EdocOpinion> opinionList=find(hql.toString(),-1,-1,paramMap);
		Object[] objArr=new Object[2];
		if(opinionList!=null&&opinionList.size()>0){
			for(EdocOpinion obj:opinionList){
				objArr[0]=obj;
				objArr[1]="";
				if(!ls.contains(objArr)){
					ls.add(objArr);
				}
			}
		}
		return ls;
	}
}
