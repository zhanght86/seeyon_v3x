package com.seeyon.v3x.doc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.type.Type;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class DocResourceDao extends BaseHibernateDao<DocResource> {
	
	/**
	 * 注意，此处在保存文档时，获取主键ID的策略自2010-10-22起进行了调整(可对照CVS历史版本记录)<br>
	 * 此前的策略是通过自增组件，先获取doc_resources表中的最大ID，加1后设为当前文档ID<br>
	 * 此策略需要保证同步，以免出现主键重复，单机环境尚可，在集群环境下存在困难，改为使用UUID<br>
	 * @see com.seeyon.v3x.doc.util.Constants#getNewDocResourceId
	 * @editor Rookie Young
	 */
	public Long saveAndGetId(DocResource dr) {
		if(dr.isNew()) {
			// 由于此前文档Id自增均为正数，在前端jsp/js中变量命名时无需考虑负数转换问题，如：
			// var property_${Id} = ...;
			// 类似代码，Id如为负数，则一般需将"-"转换为"_"
			// 为避免Id生成策略的调整对前端代码影响太大，强制只生成正数UUID
			dr.setId(UUIDLong.absLongUUID());
		}
			
		dr.setFrName(dr.getFrName().trim());
		Long ret = dr.getId();
	
		if (dr.getParentFrId() != 0L)
			dr.setLogicalPath(this.get(dr.getParentFrId()).getLogicalPath() + "." + ret);
		else
			dr.setLogicalPath(String.valueOf(ret));
		this.save(dr);
		
		return ret;
	}
	public boolean isDocResourceExsit(Long archiveId){
		int count = getQueryCount("from DocResource where id = ?",new Object[]{archiveId},new Type[]{Hibernate.LONG});
		return count>0 ? true: false;
	}
	/**
	 * 取得多个docResource
	 */
	public List<DocResource> getDocsByIds(String ids){
		if(Strings.isBlank(ids))
			return new ArrayList<DocResource>();
		List<Long> idList = FormBizConfigUtils.parseStr2Ids(ids);
		return getDocsByIds(idList);
	}
	
	@SuppressWarnings("unchecked")
	public List<DocResource> getDocsByIds(List<Long> idList) {
		String hql = "from DocResource where id in (:ids) order by createTime desc";
		return this.find(hql, -1, -1, FormBizConfigUtils.newHashMap("ids", idList));
	}

	@SuppressWarnings("unchecked")
	public List<DocResource> getDocsBySourceId(List<Long> resourceId){
		DetachedCriteria criteria = DetachedCriteria.forClass(DocResource.class);
		criteria.add(Expression.in("sourceId",resourceId));
		return super.executeCriteria(criteria,-1,-1);
	}
	
	/**
	 * 获取文档夹下的全部子文档
	 * @param dr	文档夹
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getSubDocResources(DocResource dr) {
		String hsql = "from DocResource as a where a.logicalPath like :lp or a.id = :aid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lp", dr.getLogicalPath() + ".%");
		map.put("aid", dr.getId());
		return this.find(hsql, -1, -1, map);
	}
	
	/**
	 * 根据文档夹ID获取对应文档夹下的全部子文档
	 * @param drId	文档夹Id
	 */
	public List<DocResource> getSubDocResources(Long drId) {
		DocResource dr = this.get(drId);
		return this.getSubDocResources(dr);
	}
	
	/**
	 * 废弃，改为更简洁、效率更高的同名方法
	 * @see #judgeSamePigeonhole(Long, Long, List)
	 */
	@Deprecated
	public List<DocResource> judgeSamePigeonhole(final Long parentId ,final Long contentId){
		String hql = "from DocResource dr where dr.parentFrId = ? and frType = ?";
		return this.find(hql, parentId, contentId);		
	}
	
	/**
	 * 获取文档综合查询的结果
	 * @param cModel	综合查询值模型
	 * @param docType	查询的文档类型
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> iSearch(ConditionModel cModel, DocType docType) {
		String title = cModel.getTitle();
		String keywords = cModel.getKeywords();
		Date beginDate = cModel.getBeginDate();
		Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		Long docLibId = cModel.getDocLibId();
		long userId = cModel.getUser().getId();

		Map<String, Object> nmap = new HashMap<String, Object>();

		StringBuffer sb = new StringBuffer("from DocResource as dr where docLibId = :docLibId and frType = :docType");
		nmap.put("docLibId", docLibId);
		nmap.put("docType", docType.getId());
		if (Strings.isNotBlank(title)) {
			sb.append(" and frName like :lp");
			nmap.put("lp", "%" + title + "%");
		}
		if (Strings.isNotBlank(keywords)) {
			sb.append(" and keyWords like :lk");
			nmap.put("lk", "%" + keywords + "%");
		}
		if (fromUserId != null) {
			sb.append(" and createUserId = :fUserId");
			nmap.put("fUserId", fromUserId);
		} else {
			sb.append(" and createUserId != :userId");
			nmap.put("userId", userId);
		}
		if (beginDate != null) {
			sb.append(" and createTime >= :begin");
			nmap.put("begin", beginDate);
		}
		if (endDate != null) {
			sb.append(" and createTime <= :end");
			nmap.put("end", endDate);
		}

		sb.append(" order by frOrder ");
		return this.find(sb.toString(), -1, -1, nmap);
	}

	public Session getDocSession() {
		return super.getSession();
	}

	public void releaseDocSession(Session session) {
		super.releaseSession(session);
	}

	/**
	 * 修改文档库名称时，对应的根文档夹名称也需要同步修改
	 * @param docLibId		文档库ID
	 * @param newLibName	文档库新名称，需赋值给根文档夹
	 */
	public void updateRootFolderName(Long docLibId, String newLibName) {
		String hql = "update " + DocResource.class.getName() + " set frName=? where parentFrId=0 and docLibId=?";
		this.bulkUpdate(hql, null, newLibName.trim(), docLibId);
	}
	
	/**
	 * 在归档协同时，判断当前选中的协同是否有至少一个已被归档
	 * @param docResId	归档源文档夹ID
	 * @param contentId	文档类型ID
	 * @param srIds	待归档的、选中的协同ID(col_summary主键ID)集合
	 * @return	当前选中的协同是否有至少一个已被归档
	 */
	public boolean judgeSamePigeonhole(Long docResId, Long contentId, List<Long> srIds) {
		String hql = "select count(d.id) from " + DocResource.class.getCanonicalName() + " as d, " + Affair.class.getCanonicalName() + " as a " +
					 "where d.sourceId=a.id and d.parentFrId=:drId and d.frType=:frType and a.objectId in (:colIds)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("drId", docResId);
		params.put("frType", contentId);
		params.put("colIds", srIds);
			
		Integer count = (Integer)this.findUnique(hql, params);
		return count != null && count.intValue() > 0;
	}
	
	/**
	 * 获取项目阶段下的文档
	 * @param phaseId				项目阶段ID
	 * @param projectLogicalPath	项目文档夹的逻辑路径
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getDocsOfProjectPhase(Long phaseId, String projectLogicalPath) {
		String hql = "from DocResource as d where d.isFolder=false and d.frType!=" + Constants.LINK_FOLDER + 
		 			 " and d.logicalPath like :logicalPath order by d.lastUpdate desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("logicalPath", projectLogicalPath + ".%");
		return this.find(hql, params);
	}

	

	/**
	 * 获取项目阶段下所有有权限的文档
	 * @param projectLogicalPath	项目文档夹的逻辑路径
	 * @param orgIds
	 * @param hasAcl
	 * @param paramMap  查询条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getDocsOfProjectPhase(String projectLogicalPath,String orgIds, boolean hasAcl,Map<String,String> paramMap) {
		//获取所有有权限的文档夹
		String hql = "from DocResource as d where d.isFolder=false and d.frType!=" + Constants.LINK_FOLDER + 
		 	" and d.logicalPath like :logicalPath ";
		Map<String, Object> params = new HashMap<String, Object>();
		//条件查询
		if(paramMap!=null && paramMap.containsKey("condition")){
			String condition = paramMap.get("condition");
			String value = paramMap.get(condition);
			if(value!=null && !"".equals(value)){
				if ("name".equals(condition)) {
					hql += " and frName like :name ";
					params.put("name", "%" + value + "%");
				} else if ("modifyDate".equals(condition) ) {
					hql += " and lastUpdate >= :firstTime and lastUpdate <= :lastTime ";
					params.put("firstTime", Datetimes.getTodayFirstTime(value));
					params.put("lastTime", Datetimes.getTodayLastTime(value));
				}
			}
		}
		//上级有权限的   查看非 无权限的  目录
		String whereSql ="";
		if(hasAcl){
			whereSql = "and d.parentFrId not in (select doc.id from DocResource doc, DocAcl da "  +
		 	"where doc.isFolder=true and doc.id = da.docResourceId and da.userId in(:orgIds) and doc.logicalPath like :logicalPath " +
		 	"and da.sharetype = " + Constants.SHARETYPE_DEPTSHARE + " and da.potenttype = " + Constants.NOPOTENT+" )";
			
		//上级无权限的  查看有权限的目录
		}else{
			whereSql = "and d.parentFrId in (select doc.id from DocResource doc, DocAcl da "  +
			 	"where doc.isFolder=true and doc.id = da.docResourceId and da.userId in(:orgIds) and doc.logicalPath like :logicalPath " +
			 	"and da.sharetype = " + Constants.SHARETYPE_DEPTSHARE + " and da.potenttype != " + Constants.NOPOTENT+" )";
		}
		hql += whereSql + " order by d.lastUpdate desc";
		params.put("logicalPath", projectLogicalPath + "%");
		params.put("orgIds", Constants.parseStrings2Longs(orgIds, ","));
		return this.find(hql, params);
	}
	/**
	 * 在修改、替换、历史版本恢复过程中，同步更新其对应映射文件的名称
	 * @param docResourceId		源ID
	 * @param newName			新名称
	 */
	public void updateLinkName(Long docResourceId, String newName) {
		String hql = "update " + DocResource.class.getCanonicalName() + " set frName=? where sourceId=? and (frType=? or frType=?)";
		this.bulkUpdate(hql, null, newName, docResourceId, Constants.FORMAT_TYPE_LINK, Constants.FORMAT_TYPE_LINK_FOLDER);
	}
	/**
	 *成发集团项目 程炯 2012-9-19 修改文档的密级
	 */
	public void updateDocSecretLevel(Long docResourceId,Integer secretLevel){
		String hql = "update " + DocResource.class.getCanonicalName() + " set secretLevel =? where id = ?";
		this.bulkUpdate(hql,null, secretLevel, docResourceId);
	}
}