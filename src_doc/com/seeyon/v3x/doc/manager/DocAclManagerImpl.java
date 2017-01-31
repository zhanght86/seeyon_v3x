package com.seeyon.v3x.doc.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.dao.DocAclDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.PotentModel;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.DocMVCUtils;
import com.seeyon.v3x.doc.util.DocUtils;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.util.Strings;

public class DocAclManagerImpl implements DocAclManager {
	private static final Log log = LogFactory.getLog(DocAclManagerImpl.class);
	
	private DocAclDao docAclDao;
	private DocResourceDao docResourceDao;
	
	@SuppressWarnings("unused")
	private OrgManager orgManager;
	private ProjectManager projectManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public DocResourceDao getDocResourceDao() {
		return docResourceDao;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	public DocAclDao getDocAclDao() {
		return docAclDao;
	}
	public void setDocAclDao(DocAclDao docAclDao) {
		this.docAclDao = docAclDao;
	}

	public Set<Integer> getDocResourceAclList(DocResource doc, String userIds) {
		return getDocResourceAclList(doc,userIds,null);
	}
	
	public Set<Integer> getDocResourceAclList(DocResource doc, String userIds, Map<Long, Set<Integer>> aclMap) {
		/**
		 * 取得文档的逻辑路径，获得此节点上级的全部节点 根据节点和用户反向从DocAcl表中查找有权限的记录
		 * 如过查到则直接返回，未查到则继续查询上级节点
		 */
		Set<Integer> potentSet = new HashSet<Integer>();
		// lihf 2007.08.06 修改
		if(aclMap==null){
			aclMap = this.getGroupedAclSet(doc, userIds);
		}
		Set<Long> keyset = aclMap.keySet();
		for(Long orgId : keyset){
			Set<Integer> set = aclMap.get(orgId);
			for(Integer potent : set){
				potentSet.add(potent);				
			}
		}
		
		return potentSet;
	}
	
	public Set<Integer> getDocResourceAclList(long projectId, String userIds) {
		/**
		 * 取得文档的逻辑路径，获得此节点上级的全部节点 根据节点和用户反向从DocAcl表中查找有权限的记录
		 * 如过查到则直接返回，未查到则继续查询上级节点
		 */
		String hql1 = "from DocResource where sourceId = ? and frType = ?";
		List<DocResource> list1 = docResourceDao.find(hql1, projectId, Constants.FOLDER_CASE);
		if (list1 == null || list1.size() == 0)
			return null;

		DocResource docProject = list1.get(0);		
		return this.getDocResourceAclList(docProject, userIds);
	}
	
	/**
	 * <pre>
	 * 得到一个类似：“all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list”
	 * 格式的权限串，在js中调用ajax使用
	 * </pre>
	 */
	public String getAclString(long docId){
		DocResource doc = this.docResourceDao.get(docId);
		if (doc == null)
			return null;
		String path = doc.getLogicalPath();

		String[] docIdsArray = path.split("\\.");
		String praId = docIdsArray[0];
		DocHierarchyManager docHierarchyManager = (DocHierarchyManager) ApplicationContextHolder.getBean("docHierarchyManager");
		DocResource myDocRoot = docHierarchyManager.getPersonalFolderOfUser(CurrentUser.get().getId());
		DocResource praDoc = this.docResourceDao.get(Long.valueOf(praId));

		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		Set<Integer> sets = this.getDocResourceAclList(doc, orgIds);
		boolean all = sets.contains(Constants.ALLPOTENT);
		boolean edit = sets.contains(Constants.EDITPOTENT);
		boolean add = sets.contains(Constants.ADDPOTENT);
		boolean readonly = sets.contains(Constants.READONLYPOTENT);
		boolean browse = sets.contains(Constants.BROWSEPOTENT);
		boolean list = sets.contains(Constants.LISTPOTENT);

		if (praDoc.getFrType() == Constants.FOLDER_MINE && myDocRoot.getId() == praDoc.getId()) {
			all = true;
			edit = true;
			add = true;
			readonly = true;
			browse = true;
			list = true;
		}

		if (doc.getFrType() == Constants.FOLDER_SHAREOUT
				|| doc.getFrType() == Constants.FOLDER_BORROWOUT
				|| doc.getFrType() == Constants.FOLDER_SHARE
				|| doc.getFrType() == Constants.FOLDER_BORROW
				|| doc.getFrType() == Constants.FOLDER_PLAN
				|| doc.getFrType() == Constants.FOLDER_PLAN_WEEK
				|| doc.getFrType() == Constants.FOLDER_PLAN_MONTH
				|| doc.getFrType() == Constants.FOLDER_PLAN_DAY
				|| doc.getFrType() == Constants.FOLDER_PLAN_WORK) {
			all = false;
			edit = false;
			add = false;
			readonly = false;
			browse = true;
			list = true;
		}

		return "all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list;
	}
	
	/**
	 * 文档权限控制 ，是否有打开查看的权限
	 */
	public boolean hasOpenAcl(long docId){
		// 有共享和借阅权限的
		DocResource doc = this.docResourceDao.get(docId);
		if(doc == null){
			log.warn("[" + this.getClass().getCanonicalName() + ".hasOpenAcl]获取文档资源不存在，id：" + docId);
			return false;
		}
		if(Long.valueOf(Constants.FORMAT_TYPE_SYSTEM_ARCHIVES).equals(doc.getMimeTypeId())) {
			return true;
		}
		String path = doc.getLogicalPath();
		String[] docIdsArray = path.split("\\.");
		String praId = docIdsArray[0];

		long userId = CurrentUser.get().getId();
		DocResource myDocRoot = this.getPersonalFolderOfUser(userId);
		DocResource praDoc = this.docResourceDao.get(Long.valueOf(praId));

		String orgIds = Constants.getOrgIdsOfUser(userId);
		Set<Integer> sets = this.getDocResourceAclList(doc, orgIds);
		boolean all = sets.contains(Constants.ALLPOTENT);
		boolean edit = sets.contains(Constants.EDITPOTENT);
		boolean add = sets.contains(Constants.ADDPOTENT);
		boolean readonly = sets.contains(Constants.READONLYPOTENT);
		boolean browse = sets.contains(Constants.BROWSEPOTENT);
		boolean dborrow = sets.contains(Constants.DEPTBORROW);
		boolean pborrow = sets.contains(Constants.PERSONALBORROW);
		boolean pshare = sets.contains(Constants.PERSONALSHARE);
		if (praDoc != null && praDoc.getFrType() == Constants.FOLDER_MINE && myDocRoot != null && myDocRoot.getId() == praDoc.getId())
			all = true;
		// TODO 写入权限待清理。只有写入权限时有权查看自己创建的文档
		boolean acl = all || edit || readonly || browse || pshare || (add && doc.getCreateUserId() == userId);
		if(acl){
			return true;
		}
		
		if (dborrow || pborrow) {
			String potent = this.getBorrowPotent(docId);
			if ("00".equals(potent)) {
				return false;
			}
			return true;
		}
		
		return false;
	}
	/**
	 * 取得权限数据
	 * 根据组织模型id进行了区分
	 */
	public Map<Long, Set<Integer>> getGroupedAclSet(DocResource doc, String orgIds){
		Map<Long, Set<Integer>> ret = new HashMap<Long, Set<Integer>>();
		
		// 1. 抽取从当前docResource开始向上，所有树形结构链上的节点的对于当前orgIds的权限
		String path = doc.getLogicalPath();
		String docIds = path.replace('.', ',');
		String[] docIdsArray = path.split("\\.");
		List<DocAcl> aclList = this.getDocAclFromDocIdsAndOrgIds(docIds, orgIds);
		
		Map<Long, Map<Long, Set<Integer>>> groupedAcl = new HashMap<Long, Map<Long, Set<Integer>>>();
		Date currentDate = new Date();
		for (DocAcl da : aclList) {
			if ((da.getEdate() != null && da.getEdate().before(currentDate)) || (da.getSdate() != null && da.getSdate().after(currentDate))) {
				continue;
			}
			
			Map<Long, Set<Integer>> map = groupedAcl.get(da.getDocResourceId());
			if(map == null) {
				map = new HashMap<Long, Set<Integer>>();
				Set<Integer> set = new HashSet<Integer>();
				set.add(da.getPotenttype());
				map.put(da.getUserId(), set);
				groupedAcl.put(da.getDocResourceId(), map);
			} else {
				Set<Integer> set = map.get(da.getUserId());
				if(set == null) {
					set = new HashSet<Integer>();
					set.add(da.getPotenttype());
					map.put(da.getUserId(), set);
				} else {
					set.add(da.getPotenttype());
				}
			}
		}
		
		// 2. 从树形结构顶部开始向下循环，按组织模型id的不同进行区分，记录权限记录。
		//    对于同一个组织模型id，后边的权限覆盖前面的。
		for(int i = 0; i < docIdsArray.length; i++){
			Map<Long, Set<Integer>> map = groupedAcl.get(Long.valueOf(docIdsArray[i]));
			if(map == null)
				continue;
			else{
				Set<Long> keySet = map.keySet();
				for(Long orgId : keySet){	
					// 不继承时候，覆盖权限
					ret.put(orgId, map.get(orgId));
				}				
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocAcl> getDocAclFromDocIdsAndOrgIds(String docIds, String orgIds) {
		String hql = "from DocAcl where docResourceId in (:dids) and userId in (:oids)";
		Map<String, Object> nmap = new HashMap<String, Object>();
		Set<Long> docIdColl = Constants.parseStrings2Longs(docIds, ",");
		nmap.put("dids", docIdColl);
		nmap.put("oids", Constants.parseStrings2Longs(orgIds, ","));
		return docAclDao.find(hql, -1, -1, nmap);
	}
	
	/**
	 * 批量取得某个权限组的文档（夹）本身的权限，不涉及继承
	 * Map<docId, Set<DocAcl>>
	 */
	public Map<Long, Set<DocAcl>> getAclSet(String docIds, String orgIds){
		List<DocAcl> aclList = this.getDocAclFromDocIdsAndOrgIds(docIds, orgIds);
		
		Map<Long, Set<DocAcl>> map = new HashMap<Long, Set<DocAcl>>();
		Set<Long> docIdColl = Constants.parseStrings2Longs(docIds, ",");
		for(Long id : docIdColl){
			map.put(id, new HashSet<DocAcl>());
		}
		if(aclList != null)
			for(DocAcl da : aclList){
				map.get(da.getDocResourceId()).add(da);
			}
		
		return map;
	}

	public List<DocResource> findNextNodeOfTree(DocResource doc, String userIds) {
		String hsql = "from DocResource as a where a.parentFrId=? and a.isFolder=1  order by a.frOrder asc";
		List<DocResource> list = docResourceDao.find(hsql, doc.getId());
		return this.filterTreeNodes(doc, list, userIds);
	}
	
	public List<DocResource> findNextNodeOfTreeWithOutAcl(DocResource doc){
		String hsql = "from DocResource as a where a.parentFrId=? and a.isFolder=1 ";
		return docResourceDao.find(hsql, doc.getId());
	}
//	 过滤树上的节点
	private List<DocResource> filterTreeNodes(DocResource parent, List<DocResource> list, String orgIds){
		List<DocResource> ret = new ArrayList<DocResource>();
		if(list == null || list.size() == 0)
			return ret;
		
		// 1. 一次抽出所有子文档夹的权限记录 Map<docId, Set<DocAcl>>
		Set<Long> docIds = new HashSet<Long>();
		for(DocResource dr : list){
			docIds.add(dr.getId());
		}
		Set<Long> orgSet = Constants.parseStrings2Longs(orgIds, ",");
		Map<Long, Set<Integer>> parentAclMap = this.getGroupedAclSet(parent, orgIds);
		Set<Integer> parentAcl = this.getDocResourceAclList(parent, orgIds);
		boolean parentHasPotent = this.hasPotent(parentAcl);
		Map<Long, Map<Long, Set<Integer>>> aclMap = this.getAclMapOfDocs(docIds, parent,orgSet);
		// 2. 遍历子文档夹，取出对应的权限
		List<DocResource> subs = new ArrayList<DocResource>();
		for(DocResource dr : list){
			Map<Long, Set<Integer>> map = aclMap.get(dr.getId());
			if(map == null){
				// 2.1 没有记录，说明继承	
				// 2.1.1 是“集团、单位、公文、项目”文档库的根展开(因为这些库的根是默认对所有人开放的)
				// 2.1.2 是其他的展开(说明上级目录已经验证过权限), 在树上显示 true
				if(parentHasPotent){					
					ret.add(dr);
				}else 
					subs.add(dr);
				dr.setAclSet(parentAcl);
			}else{
				Map<Long, Set<Integer>> ownmap = new HashMap<Long, Set<Integer>>();
				// 按照顺序添加
				ownmap.putAll(parentAclMap);
				ownmap.putAll(aclMap.get(dr.getId()));
				
				Set<Integer> potentSet = new HashSet<Integer>();
				Set<Long> keyset = ownmap.keySet();
				for(Long orgId : keyset){
					Set<Integer> set = ownmap.get(orgId);
					potentSet.addAll(set);
				}
				
				dr.setAclSet(potentSet);
				
				// 2.2 有记录，说明不继承	
				// 2.2.1 有权限，在树上显示 true
				// 2.2.2 没有权限，向下查找文档夹，看是否有权限 nextPath
				if(this.hasPotent(potentSet)){
					ret.add(dr);
				}else 
					subs.add(dr);
			}
		}

		if(subs.size() > 0){
			subs = this.hasPotentInNextPath(subs, orgSet);
			ret.addAll(subs);
		}
		return ret;
	}
	
	/**
	 * 重载方法getAclMapOfDocs 
	 * 使用连接查询 ，去除in字句
	 */
	@SuppressWarnings("unchecked")
	private Map<Long, Map<Long, Set<Integer>>> getAclMapOfDocs(Set<Long> docIds,DocResource doc ,Set<Long> orgIds){
		String hqll = "select docAcl.docResourceId, docAcl.userId, docAcl.potenttype  from DocAcl docAcl , " +
		" DocResource docResource where " +
		"  docResource.isFolder=1 and docResource.parentFrId=:parentId" +
		" and docAcl.userId in (:oids) " +
		" and docAcl.sharetype = " + Constants.SHARETYPE_DEPTSHARE +" and docResource.id = docAcl.docResourceId  "  ;	
		
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("parentId", doc.getId());
		nmap.put("oids", orgIds) ;
		
		List<Object[]> list = (List<Object[]>)docAclDao.find(hqll, -1, -1, nmap);	
		
		Map<Long, Map<Long, Set<Integer>>> ret = new HashMap<Long, Map<Long, Set<Integer>>>();
		for(Long did : docIds){
			ret.put(did, new HashMap<Long, Set<Integer>>());
		}
		
		for(Object[] arr : list){
			Map<Long, Set<Integer>> map = ret.get((Long)arr[0]);
			Set<Integer> set = map.get((Long)arr[1]);
			if(set == null){
				set = new HashSet<Integer>();
				map.put((Long)arr[1], set);
			}
			set.add((Integer)arr[2]);
		}
		
		return ret;
	}
	// 取得某个权限组对多个文档的权限 Map<docId, Set<DocAcl>>
	@SuppressWarnings("unchecked")
	private Map<Long, Map<Long, Set<Integer>>> getAclMapOfDocs(Set<Long> docIds, Set<Long> orgIds){
		Map<Long, Map<Long, Set<Integer>>> ret = new HashMap<Long, Map<Long, Set<Integer>>>();
		for(Long did : docIds){
			ret.put(did, new HashMap<Long, Set<Integer>>());
		}
		
		String hql = "select docResourceId, userId, potenttype  from DocAcl where docResourceId in (:dids) and userId in (:oids) and sharetype = " + Constants.SHARETYPE_DEPTSHARE;
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("dids", docIds);
		nmap.put("oids", orgIds);
		List<Object[]> list = (List<Object[]>)docAclDao.find(hql, -1, -1, nmap);
		
		for(Object[] arr : list){
			Map<Long, Set<Integer>> map = ret.get((Long)arr[0]);
			Set<Integer> set = map.get((Long)arr[1]);
			if(set == null){
				set = new HashSet<Integer>();
				map.put((Long)arr[1], set);
			}
			set.add((Integer)arr[2]);
		}
		
		return ret;
	}
	
	// 判断是否有权限
	private boolean hasPotent(Set<Integer> set){
		for(Integer a:set) {
			if (a.intValue() != Constants.NOPOTENT)
				return true;
		}
		return false;
	}
	
	// 判断
	@SuppressWarnings("unchecked")
	private List<DocResource> hasPotentInNextPath(List<DocResource> subs, Set<Long> orgIds){
		StringBuffer buf = new StringBuffer("select doc.logicalPath, doc.parentFrId from DocResource doc, DocAcl da where doc.docLibId = :libid and doc.isFolder = true and doc.id = da.docResourceId ");
		buf.append(" and da.userId in(:oids) and da.sharetype = ");
		buf.append(Constants.SHARETYPE_DEPTSHARE);
		buf.append(" and da.potenttype != ");
		buf.append(Constants.NOPOTENT);
		buf.append(" and (");
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("libid", subs.get(0).getDocLibId());
		nmap.put("oids", orgIds);
		for(int i = 0; i < subs.size(); i++){
			DocResource dr = subs.get(i);
			if(i > 0)
				buf.append(" or ");
			buf.append("doc.logicalPath like :lp" + i);
			nmap.put("lp" + i, dr.getLogicalPath() + ".%");
		}
		buf.append(") ");

		List<DocResource> ret = new ArrayList<DocResource>();
		List<Object[]> list = this.docAclDao.find(buf.toString(), -1, -1, nmap);
		if(list == null || list.size() == 0)
			return ret;
		
		for(DocResource dr : subs){
			for(Object[] arr : list){
				String lp = (String)arr[0];
				if(lp.startsWith(dr.getLogicalPath() + ".")){
					ret.add(dr);
					break;
				}
			}
		}
		return ret;
	}

	public List<DocResource> findNextNodeOfTablePageByDate(final DocResource doc, String userIds, int currentPage, int pageSize) {
		String orderStr = " order by doc.frOrder, doc.lastUpdate desc, doc.mimeOrder";
		return findResources(doc,	userIds, currentPage, pageSize, orderStr);
	}
	
	public List<DocResource> findNextNodeOfTablePage(final DocResource doc, String userIds, int currentPage, int pageSize,String... type) {
		String orderStr = " order by doc.frOrder, doc.lastUpdate desc ";
		return findResources(doc,	userIds, currentPage, pageSize, orderStr,type);
	}
	
 	@SuppressWarnings("unchecked")
	private List<DocResource> findResources(final DocResource doc, String userIds, int currentPage, int pageSize,String orderString,String... type) {
 		V3xOrgMember member = null;
 		try {
			member = orgManager.getMemberById(CurrentUser.get().getId());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		// 1. 取得父文档夹权限	
		Map<Long, Set<Integer>> parentAclMap = this.getGroupedAclSet(doc, userIds);
		Set<Integer> parentAcl = this.getDocResourceAclList(doc, userIds, parentAclMap);
		boolean parentHasPotent = this.hasPotent(parentAcl);
		
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		// 2.1 如果有权限
		List<DocResource> list = null;
		final String  orderStr= orderString;
		
		boolean isOwner = false;
		
		User user = CurrentUser.get();
		if (user != null) {
			Long userId = user.getId();
			//对项目文档特殊判断
			List<Long> ownerSet = DocMVCUtils.getLibOwners(doc);
			isOwner = ownerSet != null && ownerSet.contains(userId);
		}
		
		if (isOwner) {
			String hql = "from DocResource doc where doc.parentFrId = ? and (doc.secretLevel <="+member.getSecretLevel()+" or doc.secretLevel is null)";//成发集团项目
			if (currentPage == -1 && pageSize == -1) {
				list = this.docResourceDao.find(hql + orderString, -1, -1, null, doc.getId());
			} else {
				list = this.docResourceDao.findWithCount(hql + orderString, "select count(doc.id) " + hql, null, doc.getId());
			}
		} else if (parentHasPotent && Strings.isNotBlank(userIds)) {
			// 只有写入权限时，只能查看自己创建的文档
			boolean justHasAddPotent = parentAcl != null
					&& ((parentAcl.size() == 1 && parentAcl.iterator().next() == Constants.ADDPOTENT) 
							|| (parentAcl.size() == 2 && parentAcl.contains(Constants.ADDPOTENT) && parentAcl.contains(Constants.NOPOTENT)));
			
			if(justHasAddPotent) {
				String hql = "from " + DocResource.class.getCanonicalName() + " as doc where doc.parentFrId=? and doc.createUserId=? and (doc.secretLevel <="+member.getSecretLevel()+" or doc.secretLevel is null)";//成发集团项目
				list = this.docResourceDao.findWithCount(hql + orderString, "select count(doc.id) " + hql, null, doc.getId(), CurrentUser.get().getId());
			}
			else {
				String hql = "";
				boolean hasHighRight = true;
				//关联文档需要浏览及以上权限
				if(type.length>0 && "quote".equals(type[0])){
					// 只有列表权限时，只能查看非  无权限的子文件夹
					if(parentAcl != null && ((parentAcl.size() == 1 && parentAcl.iterator().next() == Constants.LISTPOTENT) 
							|| (parentAcl.size() == 2 && parentAcl.contains(Constants.LISTPOTENT) && parentAcl.contains(Constants.NOPOTENT)))){

						hql = "from DocResource doc where doc.parentFrId = :parentId and doc.isFolder = true and (doc.id not in" 
								+"(select doc2.id from DocResource doc2, DocAcl da2 where doc2.parentFrId = :parentId"
								+ " and doc2.isFolder = true and doc2.id = da2.docResourceId and da2.userId in(:userIds)"
								+ " and da2.sharetype = " + Constants.SHARETYPE_DEPTSHARE
								+ " and da2.potenttype = " + Constants.NOPOTENT + "))"
								+ " and (doc.secretLevel <= " +member.getSecretLevel()+" or doc.secretLevel is null)";//成发集团项目
						hasHighRight = false;
					}
				} 
				if(hasHighRight){
					// TODO 性能优化带来功能上业务逻辑变化，有待保证业务逻辑不变的同时进行性能调优
					hql = "from DocResource doc where doc.parentFrId = :parentId and (doc.id in ("
							+ " select doc3.id from DocResource doc3, DocAcl da3 where doc3.parentFrId = :parentId and doc3.id = da3.docResourceId"
							+ " and da3.userId in(:userIds)"
							+ " and da3.sharetype = " + Constants.SHARETYPE_DEPTSHARE
							+ " and da3.potenttype != " + Constants.NOPOTENT
							+ ") or doc.id not in(select doc2.id from DocResource doc2, DocAcl da2 "
							+ " where doc2.parentFrId = :parentId and doc2.isFolder = true and doc2.id = da2.docResourceId"
							+ " and da2.userId in(:userIds)" + "))"
							+ " and (doc.secretLevel <=" + member.getSecretLevel() + " or doc.secretLevel is null)";//成发集团项目
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("parentId", doc.getId());
				params.put("userIds", userIdLongs);
				
				list = this.docResourceDao.findWithCount(hql + orderString, "select count(doc.id) " + hql, params);
			}
		}
		else {
			final String hql = "from DocResource doc, DocAcl da where doc.id = da.docResourceId and doc.parentFrId = :pid and " 
				+ " da.userId in (:userIds) and da.sharetype = " + Constants.SHARETYPE_DEPTSHARE 
				+ " and da.potenttype != " + Constants.NOPOTENT + " and (doc.secretLevel <=" + member.getSecretLevel() + " or doc.secretLevel is null)";//成发集团项目
			list = (List<DocResource>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
		            int rowCount = (Integer)(session.createQuery("select count(distinct doc.id) " + hql)
		            	.setParameterList("userIds", userIdLongs)
		            	.setLong("pid", doc.getId()).list().get(0));
			        Pagination.setRowCount(rowCount);
					
					return session.createQuery("select distinct doc " + hql + orderStr)
						.setParameterList("userIds", userIdLongs)
						.setLong("pid", doc.getId())
						.setFirstResult(Pagination.getFirstResult())
						.setMaxResults(Pagination.getMaxResults()).list();
				}
	    	});
		}
		if(list == null)
			list = new ArrayList<DocResource>();
		
		Set<Long> docids = new HashSet<Long>();
		for(DocResource td : list){
			if(td.getIsFolder())
				docids.add(td.getId());
		}
		// 有子文档夹
		if(docids.size() > 0){			
			Map<Long, Map<Long, Set<Integer>>> aclMap = this.getAclMapOfDocs(docids, Constants.parseStrings2Longs(userIds, ","));
			// 添加权限记录
			for(DocResource d : list){
				if(d.getIsFolder()){
					Map<Long, Set<Integer>> map = aclMap.get(d.getId());
					if(map == null){
						d.setAclSet(parentAcl);
					}else{
						Map<Long, Set<Integer>> ownmap = new HashMap<Long, Set<Integer>>();
						// 按照顺序添加
						ownmap.putAll(parentAclMap);
						ownmap.putAll(aclMap.get(d.getId()));
						
						Set<Integer> potentSet = new HashSet<Integer>();
						Set<Long> keyset = ownmap.keySet();
						for(Long orgId : keyset){
							Set<Integer> set = ownmap.get(orgId);
							potentSet.addAll(set);
						}
						
						d.setAclSet(potentSet);
					}
				}else{
					d.setAclSet(parentAcl);
				}	
			}			
		}else{
			for(DocResource td : list){
				td.setAclSet(parentAcl);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<DocResource> getResourcesByConditionAndPotentPage(
			List<DocResource> docList, DocResource parent, String userIds, int currentPage,
			int pageSize,String... type) {
		List<DocResource> ret = new ArrayList<DocResource>();
		List<DocResource> list = new ArrayList<DocResource>();
		// 1. 得到所有有权限记录的 logicalPath, userId, potenttype
		String hql = "select dr.logicalPath, acl.userId, acl.potenttype from DocResource as dr, DocAcl as acl" +
		 			 " where dr.id = acl.docResourceId and dr.logicalPath like :logicalPath and (acl.sharetype = " + Constants.SHARETYPE_DEPTSHARE + " or acl.sharetype = " + Constants.SHARETYPE_PERSSHARE + ") and acl.userId in (:userIds)" +
		 			 " order by dr.logicalPath, acl.potenttype, dr.createTime desc";
		
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("userIds", userIdLongs);
		// 过滤掉当前文档库外的无效记录，减少遍历次数
		String parentLP = parent.getLogicalPath();
		if(parentLP.indexOf(".") != -1)
			namedParameterMap.put("logicalPath", parentLP.substring(0, parentLP.indexOf(".")) + "%");
		else
			namedParameterMap.put("logicalPath", parentLP + "%");
        List<Object> indexParameter = null;
        List<Object[]> los = docAclDao.find(hql, -1, -1, namedParameterMap, indexParameter);
		if(los == null || los.size() == 0)
			return ret;
		List<String> noAclList = new ArrayList<String>(); //存放没有权限的记录，用于过滤权限时校验
		// 2. 组织封装有权限的数据 Map<logicalPath., Map<userId,Set<potenttype>>>
		Map<String, Map<String, Set<Integer>>> map = new HashMap<String, Map<String, Set<Integer>>>();
		for(Object[] lo : los){
			if(lo[2].toString().equals(String.valueOf(Constants.NOPOTENT))){
				noAclList.add(lo[0].toString()+".");				
				continue;
			}			
			
			Map<String, Set<Integer>> submap = map.get(lo[0] + ".");
			if(submap == null){
				submap = new HashMap<String, Set<Integer>>();
				Set<Integer> set = new HashSet<Integer>();
				set.add((Integer)(lo[2]));
				submap.put(lo[1].toString(), set);
				map.put(lo[0] + ".", submap);
			}else{
				Set<Integer> set = submap.get(lo[1].toString());
				if(set == null){
					set = new HashSet<Integer>();
					set.add((Integer)(lo[2]));
					submap.put(lo[1].toString(), set);
				}else{
					set.add((Integer)(lo[2]));
				}
			}
		}
		
		Map<String, Set<Integer>> map2 = new HashMap<String, Set<Integer>>();
		for(String lp : map.keySet()){
			Map<String, Set<Integer>> submap = map.get(lp);
			Set<Integer> aclset = new HashSet<Integer>();
			for(String uid : submap.keySet()){
				aclset.addAll(submap.get(uid));
			}
			map2.put(lp, aclset);
		}

		// 3. 权限过滤
		Set<String> keySet = map2.keySet();
		//写入权限
		Set<Integer> addAcl = new HashSet<Integer>();
		addAcl.add(Constants.ADDPOTENT);
		for(DocResource td : docList){
			String lp = td.getLogicalPath();
			while(true){
				// 存在因继承而产生的无权限记录时，应被单独设置的权限覆盖 modified by yangm at 2011-01-25
				if(noAclList.contains(lp + ".") && !keySet.contains(lp + ".")){
					break;
				}
				
				if(keySet.contains(lp + ".")){
					Set<Integer> aclSet = map2.get(lp + ".");
					//只有写入权限时
					if(aclSet.equals(addAcl)){
						//只有自己创建的文档才显示
						if(td.getCreateUserId()!=null && CurrentUser.get().getId() == td.getCreateUserId().longValue()){
							td.setAclSet(aclSet);
							list.add(td);
						}
						break;
					}
					if(type.length>0 && "quote".equals(type[0])){
						//关联文档需要列表以上权限的权限
						if(aclSet.contains(Constants.ALLPOTENT) || aclSet.contains(Constants.EDITPOTENT) || 
								aclSet.contains(Constants.READONLYPOTENT) || aclSet.contains(Constants.BROWSEPOTENT)){
							td.setAclSet(aclSet);
							list.add(td);
							break;
						} 
					}else{
						td.setAclSet(aclSet);
						list.add(td);
						break;
					}
				}
				
				int loc = lp.lastIndexOf(".");
				if(loc == -1)
					break;
				lp = lp.substring(0, lp.lastIndexOf("."));
			}
		}
		return FormBizConfigUtils.pagenate(list);
	}
	
	/**
	 * lihf 2008.03.20
	 * 不传 DocResource 对象，改传 logicalPath 集合
	 * 暂供综合查询使用
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getResourcesByConditionAndPotentPageNoDr(
			Map<Long, String> docMap, String userIds, int currentPage,
			int pageSize) {
		List<DocResource> ret = new ArrayList<DocResource>();
		if(docMap == null || docMap.size() == 0)
			return ret;
		
		// 1. 得到所有有权限记录的 logicalPath, userId, potenttype
		String hql = "select dr.logicalPath, acl.userId, acl.potenttype from DocResource as dr, DocAcl as acl";
		hql += " where dr.id = acl.docResourceId and acl.sharetype = " + Constants.SHARETYPE_DEPTSHARE + " and acl.userId in (:userIds)";
		hql += " order by dr.logicalPath, acl.potenttype desc";
		
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("userIds", userIdLongs);
        List<Object> indexParameter = null;
        List<Object[]> los = docAclDao.find(hql, -1, -1, namedParameterMap, indexParameter);
        
		if(los == null || los.size() == 0)
			return ret;
		// 2. 组织封装有权限的数据 Map<logicalPath., Map<userId,Set<potenttype>>>
		Map<String, Map<String, Set<Integer>>> map = new HashMap<String, Map<String, Set<Integer>>>();
		for(Object[] lo : los){
			if(lo[2].toString().equals(String.valueOf(Constants.NOPOTENT)))
				continue;			
			
			Map<String, Set<Integer>> submap = map.get(lo[0] + ".");
			if(submap == null){
				submap = new HashMap<String, Set<Integer>>();
				Set<Integer> set = new HashSet<Integer>();
				set.add((Integer)(lo[2]));
				submap.put(lo[1].toString(), set);
				map.put(lo[0] + ".", submap);
			}else{
				Set<Integer> set = submap.get(lo[1].toString());
				if(set == null){
					set = new HashSet<Integer>();
					set.add((Integer)(lo[2]));
					submap.put(lo[1].toString(), set);
				}else{
					set.add((Integer)(lo[2]));
				}
			}
		}
		// 增加继承来的权限
		List<String> slist = new ArrayList<String>();
		Set<String> mapkey = map.keySet();
		slist.addAll(mapkey);
		Collections.sort(slist);
		for(String ss : slist){
			Map<String, Set<Integer>> tmap = map.get(ss);
			for(String tk : slist){
				if(tk.startsWith(ss) && !tk.equals(ss)){
					Map<String, Set<Integer>> newmap = new HashMap<String, Set<Integer>>();
					newmap.putAll(tmap);
					newmap.putAll(map.get(tk));
					map.put(tk, newmap);
				}
			}
		}
		
		// Map<logicalPath, Set<Integer>>
		Map<String, Set<Integer>> map2 = new HashMap<String, Set<Integer>>();
		for(String lp : map.keySet()){
			Map<String, Set<Integer>> submap = map.get(lp);
			Set<Integer> aclset = new HashSet<Integer>();
			for(String uid : submap.keySet()){
				aclset.addAll(submap.get(uid));
			}
			map2.put(lp, aclset);
		}

		// 3. 权限过滤
//		 lihf: 2007.09.12 不使用存储过程完成查询  end
		List<Long> idList = new ArrayList<Long>();
		Set<Long> idKeySet = docMap.keySet(); 
		Set<String> keySet = map2.keySet();
		for(Long tid : idKeySet){			
			
				String lp = docMap.get(tid);
				while(true){
					if(keySet.contains(lp + ".")){
//						td.setAclSet(map2.get(lp + "."));
//						list.add(td);
						idList.add(tid);
						break;
					}else{
						int loc = lp.lastIndexOf(".");
						if(loc == -1)
							break;
						lp = lp.substring(0, lp.lastIndexOf("."));
					}
				}
			
		}
		
		if(idList.size() == 0)
			return ret;
		
		
		Pagination.setRowCount(idList.size());

		int first = Pagination.getFirstResult();
		int end1 = first + pageSize;
		int end2 = idList.size();//list.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		
		String idStr = "";
		for (int i = first; i < end; i++) {
			idStr += "," + idList.get(i);
		}
		if(idStr.length() == 0)
			return ret;
		
		ret = docResourceDao.getDocsByIds(idStr.substring(1));
		for(DocResource doc : ret){
			doc.setAclSet(map2.get(doc.getLogicalPath()));
		}
		return ret;
	}

	public void setPotentNoInherit(Long userId, String userType, Long docId, boolean isAlert, Long alertId) {
		DocAcl docAcl = new DocAcl();
		docAcl.setDocResourceId(docId);
		docAcl.setIdIfNew();
		docAcl.setPotenttype(Constants.NOPOTENT);
		docAcl.setSharetype(Constants.SHARETYPE_DEPTSHARE);
		docAcl.setUserId(userId);
		docAcl.setUserType(userType);
		docAcl.setIsAlert(isAlert);
		docAcl.setDocAlertId(alertId);
		docAclDao.save(docAcl);
	}

	@SuppressWarnings("unchecked")
	public void deletePotent(DocResource doc) {
		/**
		 * 查找文档的全部下级节点，删除权限表中关于这些节点的记录
		 */
		List<Long> dlist = new ArrayList<Long>();
		dlist.add(doc.getId());
		if (doc.getIsFolder()) {
			String hsql = "select distinct da.docResourceId from DocAcl as da, DocResource as dr where da.docResourceId=dr.id and dr.logicalPath like :lp";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lp", doc.getLogicalPath() + "%");
			List<Long> dlist1 = docResourceDao.find(hsql, -1, -1, map);
			dlist.addAll(dlist1);
		}

		String queryString = "delete from DocAcl as a where a.docResourceId in (:dlist)";
		List<List<Long>> lists = this.getSubLists(dlist);
		for (int i = 0; i < lists.size(); i++) {
			Map<String, Object> namedParameterMap = new HashMap<String, Object>();
			namedParameterMap.put("dlist", lists.get(i));
			docAclDao.bulkUpdate(queryString, namedParameterMap);
		}
	}

	@SuppressWarnings("unchecked")
	public List<DocResource> getBorrowDocsPageOld(final String userIds, final Long ownerId,
			int currentPage, int pagesize) {
		List<DocResource> docList = new ArrayList<DocResource>();
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("rawtypes")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
					+ " and ownerId=:owner and a.sdate<=:start and a.edate>=:end";
				String hql2 = "select count(*) " + hsql;
				Timestamp time = new Timestamp(System.currentTimeMillis());
	        	Query query = session.createQuery(hql2).setLong("owner", ownerId)
					.setTimestamp("start", time).setTimestamp("end", time);
	    		List list2 = query.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
	            query = session.createQuery(hsql)
	            .setParameterList("userIds", Constants.parseStrings2Longs(userIds, ","))
	            .setLong("owner", ownerId)
				.setTimestamp("start", time)
				.setTimestamp("end", time);
				List<DocAcl> ret = (List<DocAcl>)query.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();
								
				return ret;
			}
    	});
		
				
		for (int i = 0; i < list.size(); i++) {
			DocResource doc = docResourceDao.get(list.get(i).getDocResourceId());
			docList.add(doc);
		}
		return docList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocResource> getBorrowDocsPage(final String userIds, final Long ownerId, int currentPage, int pagesize) {
		V3xOrgMember member = null;
		Long memberId = CurrentUser.get().getId();
		try {
			member = orgManager.getMemberById(memberId);
		} catch (BusinessException e) {
			log.error("查询他人借阅给自己的文档时获取人员密级错误", e);
		}
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		
        List<Long> list = (List<Long>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("rawtypes")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
					+ " and ownerId=:owner and a.sdate<=:start and a.edate>=:end";
				String hql2 = "select count(distinct a.docResourceId) " + hsql;
				Timestamp time = new Timestamp(System.currentTimeMillis());
	        	Query query = session.createQuery(hql2)
		        	.setParameterList("userIds", userIdLongs)
	        		.setTimestamp("start", time)
					.setTimestamp("end", time)
					.setLong("owner", ownerId);
	    		List list2 = query.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
	            query = session.createQuery("select distinct a.docResourceId " + hsql)
	            	.setParameterList("userIds", userIdLongs)
	            	.setTimestamp("start", time)
	            	.setTimestamp("end", time)
	            	.setLong("owner", ownerId);
				return (List<Long>)query.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();								
			}
    	});
        
        
		List<DocResource> docList = null;
		if(list != null && list.size() > 0){
			Set<Long> idset = new HashSet<Long>();
			for(Long id : list){
				idset.add(id);
			}
			
			String hql = "from DocResource where id in(:ids) and (secretLevel <= " + member.getSecretLevel() + " or secretLevel is null)";//成发集团项目 程炯 根据密级进行筛选
			Map<String, Object> amap = new HashMap<String, Object>();
			amap.put("ids", idset);
			docList = this.docResourceDao.find(hql, -1, -1, amap);
		}else
			return new ArrayList<DocResource>();

		return docList;
	}

	@SuppressWarnings("unchecked")
	public Set<Long> getBorrowUserIds(final String userIds) {
		/**
		 * 查找出个人借阅的权限记录 将记录的所有人加入Set中
		 */
		Set<Long> uidSet = new HashSet<Long>();
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
					+ " and a.sdate<=:start and a.edate>=:end";
				Timestamp time = new Timestamp(System.currentTimeMillis());
				Query query = session.createQuery(hsql)
				.setParameterList("userIds", userIdLongs)
				.setTimestamp("start", time).setTimestamp("end", time);
				List<DocAcl> ret = (List<DocAcl>)query.list();
				return ret;
			}
    	});
		
		
		// 2007.10.18 加入组织模型有效性判断
		List<DocAcl> list_valid = new ArrayList<DocAcl>();
		if(list != null)
			for(DocAcl acl : list){
				if(Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, acl.getOwnerId())){
					list_valid.add(acl);
				}
			}
		
		for (int i = 0; i < list_valid.size(); i++) {
			uidSet.add(list_valid.get(i).getOwnerId());
		}
		return uidSet;
	}

	@SuppressWarnings("unchecked")
	public Set<Long> getBorrowUserIdsPage(final String userIds, int currentPage,
			int pagesize) {
		Set<Long> uidSet = new HashSet<Long>();
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
		
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
					+ " and a.sdate<=:start and a.edate>=:end";
				Timestamp time = new Timestamp(System.currentTimeMillis());
				Query query = session.createQuery(hsql)
					.setParameterList("userIds", userIdLongs)
					.setTimestamp("start", time)
					.setTimestamp("end", time);
				List<DocAcl> ret = (List<DocAcl>)query.list();
				return ret;
			}
    	});
		
		
		// 2007.10.18 加入组织模型有效性判断
		List<DocAcl> list_valid = new ArrayList<DocAcl>();
		if(list != null)
			for(DocAcl acl : list){
				if(Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, acl.getOwnerId())){
					list_valid.add(acl);
				}
			}
	    int dept = this.getDeptBorrowDocsCount(userIds) > 0 ? 1 : 0;
	    Pagination.setRowCount(list_valid.size() + dept);		
		
		List<DocAcl> list_valid_ret = new ArrayList<DocAcl>();
		int first = Pagination.getFirstResult();
		int end1 = first + Pagination.getMaxResults();
		int end2 = list_valid.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		list_valid_ret = list_valid.subList(first, end);		
		
		for (DocAcl da : list_valid_ret) {
			uidSet.add(da.getOwnerId());
		}
		
		return uidSet;
		

	}

	@SuppressWarnings("unchecked")
	public List<DocResource> getShareRootDocs(String userIds, Long ownerId) {
		/**
		 * 查找个人共享的权限记录 根据记录的文档id查找出全部文档资源
		 */
		// List<DocResource> docList = new ArrayList<DocResource>();
		String hsql = "from DocAcl as a where a.userId in (:ids) and a.sharetype=" + Constants.SHARETYPE_PERSSHARE
				+ " and ownerId=:oid  order by a.docResourceId";
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(userIds, ","));
		nmap.put("oid", ownerId);
		List<DocAcl> list = docAclDao.find(hsql, -1, -1, nmap);
		String in = "";
		for (int i = 0; i < list.size(); i++) {
			in = in + list.get(i).getDocResourceId() + ",";
			// in=in+list.get(i).getDocResource().getId();
		}
		if (in.length() > 0) {
			in = in.substring(0, in.length() - 1);
			String sql = "from DocResource as a where a.id in (:dids)";
			Map<String, Object> nmap2 = new HashMap<String, Object>();
			nmap2.put("dids", Constants.parseStrings2Longs(in, ","));
			List<DocResource> docList = docResourceDao.find(sql, -1, -1, nmap2);
			return docList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DocResource> getShareRootDocsPage(String userIds, Long ownerId,
			int currentPage, int pagesize) {
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(CurrentUser.get().getId());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		List<DocResource> docList = new ArrayList<DocResource>();
		String hsql = "from DocAcl as a where a.userId in (:ids) and a.sharetype=" + Constants.SHARETYPE_PERSSHARE
				+ " and ownerId=:oid  order by a.docResourceId";
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(userIds, ","));
		nmap.put("oid", ownerId);
		List<DocAcl> list = docAclDao.find(hsql, -1, -1, nmap);
		List<Long> in = new ArrayList<Long>();
		for (int i = 0; i < list.size(); i++) {
			in.add(list.get(i).getDocResourceId());
		}
		if (!in.isEmpty()) {
			//成发集团项目 程炯 使用文档密级对可见的列表进行筛选
			String sql = "from DocResource as a where a.id in (:in) and ( a.secretLevel <="+member.getSecretLevel()+" or a.secretLevel is null ) order by a.frOrder,a.id";
			
			Map<String, Object> namedParameterMap = new HashMap<String, Object>();
			namedParameterMap.put("in", in);
			
			docList = docResourceDao.find(sql, namedParameterMap);
		}
		
		return docList;
	}

	// 查询共享所有人列表
	@SuppressWarnings("unchecked")
	public Set<Long> getShareUserIds(String userIds) {
		/**
		 * 查找出个人共享的权限记录 将记录的所有人加入Set中
		 */
		Set<Long> uidSet = new HashSet<Long>();
		String hsql = "from DocAcl as a where a.userId in (:ids) and a.sharetype=" + Constants.SHARETYPE_PERSSHARE;
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(userIds, ","));
		List<DocAcl> list = docAclDao.find(hsql, -1, -1, nmap);
		
		// 2007.10.18 加入组织模型有效性判断
		List<DocAcl> list_valid = new ArrayList<DocAcl>();
		if(list != null)
			for(DocAcl acl : list){
				if(Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, acl.getOwnerId())){
					list_valid.add(acl);
				}
			}
		
		for (int i = 0; i < list_valid.size(); i++) {
			uidSet.add(list_valid.get(i).getOwnerId());
		}
		return uidSet;
	}

	// 查询共享所有人列表（分页）
	@SuppressWarnings("unchecked")
	public Set<Long> getShareUserIdsPage(String userIds, int currentPage, int pagesize) {
		Set<Long> uidSet = new HashSet<Long>();
		String hsql = "from DocAcl as a where a.userId in (:ids) and a.sharetype=" + Constants.SHARETYPE_PERSSHARE;
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(userIds, ","));
		List<DocAcl> list = docAclDao.find(hsql, -1, -1, nmap);
		
		// 2007.10.18 加入组织模型有效性判断
		List<DocAcl> list_valid = new ArrayList<DocAcl>();
		if(list != null)
			for(DocAcl acl : list){
				if(Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, acl.getOwnerId())){
					list_valid.add(acl);
				}
			}
		Pagination.setRowCount(list_valid.size());
		
		List<DocAcl> list_valid_ret = new ArrayList<DocAcl>();
		int first = Pagination.getFirstResult();
		int end1 = first + Pagination.getMaxResults();
		int end2 = list_valid.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		list_valid_ret = list_valid.subList(first, end);		
		
		
		for (DocAcl da : list_valid_ret) {
			uidSet.add(da.getOwnerId());
		}
		return uidSet;
	}

	// 设置继承权限
	@SuppressWarnings("unchecked")
	public void setPotentInherit(Long docId, byte docLibType, long docLibId) {
		// 删除所有非继承的权限
		try{
			String hql = "delete from DocAcl as a where a.docResourceId = ?"
					+ " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE;
			String hql1 = "from DocAcl as a where a.docResourceId = ?"
				+ " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE;
			List<DocAcl> srclist = docAclDao.find(hql1, docId);
			if(srclist == null || srclist.size() == 0)
				return;
			docAclDao.bulkUpdate(hql, null, new Object[]{docId});
			String hql3 = "from DocAcl where ";
			Map<String, Object> nmap = new HashMap<String, Object>();
			// 在删除权限的时候判断 在当前自定义库下，是否仍有有权限的文档记录，没有则删除 member 记录
			if(docLibType == Constants.USER_CUSTOM_LIB_TYPE.byteValue()){
				String hql2 = "from DocResource where docLibId = ?";
				List<DocResource> list = docResourceDao.find(hql2, docLibId);
				StringBuffer drids = new StringBuffer("");
				int resourcesSize = list.size() > srclist.size() ? list.size() : srclist.size();
				if (resourcesSize > 1000) {
					int x = 0;
					int i = 0;
					while (true) {
						StringBuffer drids1 = new StringBuffer("");
						int starSize = i;
						int endSize = i + 500;
						if (endSize > list.size()) {
							endSize = list.size();
						}
						List<DocResource> subList = list.subList(starSize, endSize);
						for(DocResource d : subList){
							drids1.append(",").append(d.getId());
							drids.append(",").append(d.getId());
						}
						String ids = drids1.toString().substring(1, drids1.toString().length());
						x ++;
						i = endSize;
						nmap.put("dids", Constants.parseStrings2Longs(ids, ","));
						if(!drids.toString().equals("")){
							String userids = "";
							for(DocAcl da : srclist){
								userids += "," + da.getUserId();
							}
							nmap.put("uids", Constants.parseStrings2Longs(userids.substring(1), ","));
							List<DocAcl> list2 = docAclDao.find(hql3 + " docResourceId in(:dids) and userId in(:uids)", -1, -1, nmap);
							if(list2 == null || list2.size() == 0){
								this.deleteLibMember(docLibId, userids.substring(1, userids.length()));
							}
						}
						if (endSize >= list.size()) {
							break;
						}
					}
				} else {
					for(DocResource d : list){
						drids.append(",").append(d.getId());
					}
					String ids = drids.toString().substring(1, drids.toString().length());
					nmap.put("dids", Constants.parseStrings2Longs(ids, ","));
					 hql3 += " docResourceId in(:dids) and userId in(:uids)";
						if(!drids.toString().equals("")){
							String userids = "";
							for(DocAcl da : srclist){
								userids += "," + da.getUserId();
							}
							nmap.put("uids", Constants.parseStrings2Longs(userids.substring(1), ","));
							List<DocAcl> list2 = docAclDao.find(hql3, -1, -1, nmap);
							if(list2 == null || list2.size() == 0){
								this.deleteLibMember(docLibId, userids.substring(1, userids.length()));
							}
						}				 
				}
			}
		}catch(Exception e){
			log.error("设置权限出现异常" , e);
		}
	}

	// 设置个人共享权限
	public Long setPersonalSharePotent(Long userId, String userType,
			Long docId, Long ownerId, Long alertId) {
		DocAcl acl = new DocAcl();
		acl.setDocResourceId(docId);
		acl.setIdIfNew();
		acl.setOwnerId(ownerId);
		acl.setPotenttype(Constants.PERSONALSHARE);// 个人共享暂时设为8
		acl.setSharetype(Constants.SHARETYPE_PERSSHARE);
		acl.setUserId(userId);
		acl.setUserType(userType);
		
		if(alertId != null){
			acl.setIsAlert(true);
			acl.setDocAlertId(alertId);
		}
		
		docAclDao.save(acl);
		return acl.getId();
	}

	// 设置个人借阅的权限
	public void setNewPersonalBorrowPotent(Long userId, String userType,
			Long docId, Long ownerId, java.sql.Timestamp sdate,
			java.sql.Timestamp edate,Byte lenPotent,String lenPotent2) {
		DocAcl acl = new DocAcl();
		acl.setDocResourceId(docId);
		acl.setIdIfNew();
		acl.setOwnerId(ownerId);
		acl.setPotenttype(Constants.PERSONALBORROW);
		acl.setSharetype(Constants.SHARETYPE_PERSBORROW);
		acl.setUserId(userId);
		acl.setUserType(userType);
		acl.setEdate(edate);
		acl.setSdate(sdate);
		acl.setLenPotent(lenPotent);
		acl.setLenPotent2(lenPotent2);
		docAclDao.save(acl);
	}

	// 更新个人借阅的权限，只许更新开始、结束时间
	public void updatePersonalBorrowPotent(Long docAclId,
			java.sql.Timestamp sdate, java.sql.Timestamp edate) {
		DocAcl acl = docAclDao.get(docAclId);
		acl.setEdate(edate);
		acl.setSdate(sdate);
		docAclDao.update(acl);
	}

	/** 删除借阅权限  */
	public void deleteBorrowDoc(Long docAclId) {
		docAclDao.delete(docAclId.longValue());
	}
	
	/** 删除共享权限  */
	public void deleteShareDoc(Long docAclId) {
		docAclDao.delete(docAclId.longValue());
	}

	// 获取单位借阅列表（分页）
	@SuppressWarnings("unchecked")
	public List<DocResource> getDeptBorrowDocsPageOld(final String userIds,
			int currentPage, int pagesize) {
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("rawtypes")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (" + userIds
					+ ") and a.sharetype=" + Constants.SHARETYPE_DEPTBORROW
					+ " and a.sdate<=:starttime and a.edate>=:endtime";
				String hql2 = "select count(*) " + hsql;
				Timestamp time = new Timestamp(System.currentTimeMillis());
	        	Query query = session.createQuery(hql2).setTimestamp("starttime", time)
				.setTimestamp("endtime", time);
	    		List list2 = query.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
	            query = session.createQuery(hsql).setTimestamp("starttime", time)
				.setTimestamp("endtime", time);
				List<DocAcl> ret = (List<DocAcl>)query.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();
								
				return ret;
			}
    	});
        
        
		List<DocResource> docList = new ArrayList<DocResource>();
		for (int i = 0; i < list.size(); i++) {
			DocResource doc = docResourceDao.get(list.get(i).getDocResourceId());
			docList.add(doc);
		}
		return docList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocResource> getDeptBorrowDocsPage(final String userIds, int currentPage, int pagesize) { 
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(CurrentUser.get().getId());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(userIds, ",");
        List<Long> list = (List<Long>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("rawtypes")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_DEPTBORROW
					+ " and a.sdate<=:starttime and a.edate>=:endtime";
				String hql2 = "select count(distinct a.docResourceId) " + hsql;
				Timestamp time = new Timestamp(System.currentTimeMillis());
	        	Query query = session.createQuery(hql2)
	        	.setParameterList("userIds",userIdLongs)
	        	.setTimestamp("starttime", time)
				.setTimestamp("endtime", time);
	    		List list2 = query.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
	            query = session.createQuery("select distinct a.docResourceId " + hsql)
	            .setParameterList("userIds",userIdLongs)
	            .setTimestamp("starttime", time)
				.setTimestamp("endtime", time);
				return (List<Long>)query.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();								
			}
    	});
        
        
		List<DocResource> docList = null;
		if(list != null && list.size() > 0){
			Set<Long> idsset = new HashSet<Long>();
			for(Long id : list){
				idsset.add(id);
			}
			
			String hql = "from DocResource where id in(:ids) and ( secretLevel <=" +member.getSecretLevel()+" or secretLevel is null)";//成发集团项目 程炯 对可见的列表进行密级筛选
			Map<String, Object> amap = new HashMap<String, Object>();
			amap.put("ids", idsset);
			docList = this.docResourceDao.find(hql, -1, -1, amap);
		}else
			return new ArrayList<DocResource>();

		return docList;
	}

	// 获取单位借阅列表记录数
	@SuppressWarnings("unchecked")
	public int getDeptBorrowDocsCount(final String userIds) {
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("userIds", Constants.parseStrings2Longs(userIds, ","));
		String hsql = "from DocAcl as a where a.userId in (:userIds) and a.sharetype=" + Constants.SHARETYPE_DEPTBORROW;
		List<DocAcl> list2 = docAclDao.find(hsql, -1, -1, nmap);
		return list2.size();
	}

	// 设置单位借阅权限
	public void setNewDeptBorrowPotent(Long userId, String userType,
			Long docId, Timestamp sdate, Timestamp edate,Byte lenPotent,String lenPotent2) {
		DocAcl acl = new DocAcl();
		acl.setDocResourceId(docId);
		acl.setEdate(edate);
		acl.setIdIfNew();
		acl.setPotenttype(Constants.DEPTBORROW);
		acl.setSdate(sdate);
		acl.setSharetype(Constants.SHARETYPE_DEPTBORROW);
		acl.setUserId(userId);
		acl.setUserType(userType);
		acl.setLenPotent(lenPotent);
		acl.setLenPotent2(lenPotent2);
		docAclDao.save(acl);

	}

	// 更新单位借阅权限
	public void updateDeptBorrowPotent(Long docAclId, Timestamp sdate,
			Timestamp edate) {
		DocAcl acl = docAclDao.get(docAclId);
		acl.setEdate(edate);
		acl.setSdate(sdate);
		docAclDao.update(acl);

	}

	// 设置单位共享权限
	public void setDeptSharePotent(Long userId, String userType, Long docId,
			int potenttype, boolean isAlert, Long alertId, int minOrder) {
		DocResource docRes=docResourceDao.get(docId);
		DocLib docLib=this.docUtils.getDocLibById(docRes.getDocLibId());
		DocAcl acl = new DocAcl();
		acl.setDocResourceId(docId);
		acl.setIdIfNew();
		acl.setPotenttype(potenttype);
		acl.setSharetype(Constants.SHARETYPE_DEPTSHARE);
		acl.setUserId(userId);
		acl.setUserType(userType);
		acl.setIsAlert(isAlert);
		acl.setDocAlertId(alertId);
		acl.setAclOrder(minOrder + 1);

		docAclDao.save(acl);
		if(docLib.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue()
				&& potenttype != Constants.NOPOTENT){
			this.setLibMember(docLib.getId(), userId, userType);
		}
	}

	// 判断是否有权限删除文档夹/文档
	@SuppressWarnings("unchecked")
	public boolean canBeDelete(DocResource doc, String userIds) {
		/**
		 * 取得文档的逻辑路径，获得此节点上级的全部节点 根据节点和用户反向从DocAcl表中查找有权限的记录
		 * 如过查到且权限为:编辑权限or全部权限,则证明本级可删除,跳出循环;否则继续查找上级节点权限 查询全部下级节点
		 * 根据节点id循环查询是否有权限删除 如果全部有权限则证明下级文档可删除;否则下级文档不可删除
		 * 如果本级和下级都可删除则返回true;否则返回false
		 */
		boolean flag = false;// 是否有权限标记
		String lp = doc.getLogicalPath();
		String[] lps = lp.split("\\.");
		// 从下而上的查找权限信息（查找本级是否可删除）
		for (int i = lps.length - 1; i >= 0; i--) {
			String hsql = "from DocAcl as a where a.docResourceId = :docid and a.userId in (:userids) order by a.potenttype";
			Map<String, Object> namedMap = new HashMap<String, Object>();
			namedMap.put("docid", Long.parseLong(lps[i]));
			namedMap.put("userids", Constants.parseStrings2Longs(userIds, ","));
			List<DocAcl> list = docAclDao.find(hsql, -1, -1, namedMap);//log.info("canBeDelete使用namedMap，in 结果：" + list);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).getPotenttype() == Constants.ALLPOTENT
						|| list.get(j).getPotenttype() == Constants.EDITPOTENT) {// 编辑权限or全部权限
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		// 自上而下的查找权限信息（查找下级是否可删除）
		boolean flagNext = true;
		String hsql2 = "from DocResource as a where a.logicalPath like :lp order by a.frOrder ,a.id";
		Map<String, Object> namedMap = new HashMap<String, Object>();
		namedMap.put("lp", lp + ".%");
		List<DocResource> dlist = docResourceDao.find(hsql2, -1, -1, namedMap);//log.info("canBeDelete使用namedMap，like 结果：" + dlist);
		for (int i = 0; i < dlist.size(); i++) {
			String hsql3 = "from DocAcl as a where a.docResourceId = :docid and a.userId in (:userids) order by a.potenttype desc";
			Map<String, Object> nameMap = new HashMap<String, Object>();
			nameMap.put("docid", dlist.get(i).getId());
			nameMap.put("userids", Constants.parseStrings2Longs(userIds, ","));
			List<DocAcl> alist = docAclDao.find(hsql3,  -1, -1, nameMap);
			for (int j = 0; j < alist.size(); j++) {
				if (alist.get(j).getPotenttype() == Constants.NOPOTENT) {
					flagNext = false;
				}
				if (alist.get(j).getPotenttype() == Constants.ALLPOTENT
						|| alist.get(j).getPotenttype() == Constants.EDITPOTENT) {
					flagNext = true;
				}
			}
			if (!flagNext) {
				break;
			}
		}

		return flag && flagNext;
	}

	// 设置文档库成员
	public void setLibMember(Long docLibId, Long userId, String userType) {
		this.docUtils.getDocLibManager().setLibMember(docLibId, userId, userType);
	}

	// 删除文档库成员
	public void deleteLibMember(Long docLibId, String userIds) {	
		this.docUtils.getDocLibManager().deleteLibMember(docLibId, userIds);
	}

	// 删除权限
	@SuppressWarnings("unchecked")
	public void deletePotentByUser(Long docId, Long userId, String userType, byte docLibType, long docLibId) {
		String hsql = "delete from DocAcl as a where a.docResourceId=? and a.userId=?" + " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE + " and a.userType=?";
		docAclDao.bulkUpdate(hsql, null, docId, userId, userType);

		/*String hql3 = "from DocAcl where docResourceId in(select doc.id from DocResource doc where doc.docLibId =:ids and doc.isFolder = true ) and userId = :userid and sharetype = "
				+ Constants.SHARETYPE_DEPTSHARE + " and potenttype != " + Constants.NOPOTENT;*/
		
		String hql3 = "select da from " + DocAcl.class.getName() + " da, " + DocResource.class.getName() + " dr where da.docResourceId = dr.id "
				+ "and dr.docLibId = :ids and dr.isFolder = true and da.userId = :userid and da.sharetype = " + Constants.SHARETYPE_DEPTSHARE + " and da.potenttype != " + Constants.NOPOTENT;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", docLibId);
		map.put("userid", userId);
		List<DocAcl> list2 = docAclDao.find(hql3, -1, -1, map);
		if (list2 == null || list2.size() == 0) {
			this.deleteLibMember(docLibId, "" + userId);
		}
	}
	
	// 删除权限
	@SuppressWarnings("unchecked")
	public void deletePotentByMaUser(Long docId, Long userId, String userType, byte docLibType, long docLibId) {
		String hsql = "delete from DocAcl as a where a.docResourceId=? and a.userId=?" + " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE + " and a.userType=?";
		docAclDao.bulkUpdate(hsql, null, docId, userId, userType);

		/*String hql3 = "from DocAcl where docResourceId in(select doc.id from DocResource doc where doc.docLibId =:ids and doc.isFolder = true ) and userId = :userid and sharetype = "
					  + Constants.SHARETYPE_DEPTSHARE + " and potenttype != " + Constants.NOPOTENT;*/
		
		String hql2 = "select da.id from " + DocAcl.class.getName() + " da, " + DocResource.class.getName() + " dr where da.docResourceId = dr.id "
				+ "and dr.docLibId = :ids and dr.isFolder = true and da.userId = :userid and da.sharetype = " + Constants.SHARETYPE_DEPTSHARE;
		
		String hql3 = hql2 + " and da.potenttype != " + Constants.NOPOTENT;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", docLibId);
		map.put("userid", userId);
		
		List<Long> list2 = docAclDao.find(hql3, -1, -1, map);
		if (CollectionUtils.isEmpty(list2)) {
			this.deleteLibMember(docLibId, "" + userId);
		}
		/*String hql4 = "delete from DocAcl where docResourceId in(select doc.id from DocResource doc where doc.docLibId =:ids and doc.isFolder = true ) and userId = :userid and sharetype = "
				+ Constants.SHARETYPE_DEPTSHARE;*/
		
		List<Long> list3 = docAclDao.find(hql2, -1, -1, map);
		if (CollectionUtils.isNotEmpty(list3)) {
			// 存放判定数据
			Map<String, Object> map3 = new HashMap<String, Object>();
			int len = list3.size();
			// in中的条件每次最多900
			int count = 900;
			int size = len % count;
			if(size == 0) {
				size = len / count;
			} else {
				size = (len / count) + 1;
			}
			StringBuffer hql4 = new StringBuffer("delete from " + DocAcl.class.getName() + " where id in (:ids)");
			for(int i = 0; i < size; i++) {
				int fromIndex = i * count;
				int toIndex = Math.min(fromIndex + count, len);
				map3.put("ids", list3.subList(fromIndex, toIndex));
				docAclDao.bulkUpdate(hql4.toString(), map3);
			}
			
		}

		//TODO
		//此处可能会有问题, 出现过客户bug
		//文档库管理员无需删组织模型, 查看时单独处理
		/*String aclIds = Constants.getOrgIdsOfUser(userId);
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", docLibId);
		nmap.put("userIds", Constants.parseStrings2Longs(aclIds, ","));

		String hql5 = "delete from DocAcl where docResourceId in (select doc.id from DocResource doc where doc.docLibId =:ids and doc.isFolder = true ) and userId in (:userIds) and sharetype = "
				+ Constants.SHARETYPE_DEPTSHARE + "and potenttype = " + Constants.NOPOTENT;
		docAclDao.bulkUpdate(hql5, nmap);*/
	}

	// 删除权限
	public void deletePotentByUser(Long aclId) {
		docAclDao.delete(aclId.longValue());
	}

	// 查找继承的权限记录
	@SuppressWarnings("unchecked")
	public List<DocAcl> getDocAclListByInherit(Long docId) {
		/**
		 * 取得文档资源的逻辑路径 从文档资源的上级节点开始反向查找单位共享的权限记录
		 */
		List<DocAcl> list = new ArrayList<DocAcl>();
		String notin = "";

		String lp = docResourceDao.get(docId).getLogicalPath();
		String[] ss = lp.split("\\.");
		Set<Long> ids = new HashSet<Long>();
		if(ss.length==1) return null;
		for (int i = ss.length - 2; i >= 0; i--) {
			String hsql = "";
			Map<String, Object> namedParameterMap = null;
			if (notin.length() == 0) {
				hsql = "select a.userId, a.userType from DocAcl as a where a.docResourceId=? and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE
						+ " group by a.userId,a.userType";
			} else {
				hsql = "select a.userId, a.userType from DocAcl as a where a.docResourceId=? and a.userId not in (:userId) and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE
						+ " group by a.userId,a.userType";
				namedParameterMap = new HashMap<String, Object>();
				namedParameterMap.put("userId", Constants.parseStrings2Longs(notin, ","));
			}
			List<Object[]> l = docAclDao.find(hsql, -1,-1,namedParameterMap, Long.parseLong(ss[i])); // 分组查找上级的权限
			aaa:
			for (Object[] acl : l) {
				Long userId = (Long)(acl[0]);
				for(DocAcl a: list){
					if(a.getUserId() == userId){
						continue aaa;
					}
				}
				String userType = acl[1].toString();
				String hsql2 = "from DocAcl as a where a.docResourceId = ? and a.userId=?"
						+ " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE 
						+ " and a.userType=? order by a.potenttype";// 查找上级的权限
				List<DocAcl> l2 = docAclDao.find(hsql2, Long.valueOf(ss[i]), userId, userType);
				for (DocAcl a : l2) {
					if (a.getPotenttype() == Constants.NOPOTENT) {
						notin = notin + userId + ",";
					} else {
						if(list.size()==0) list.add(a);
						
						else {
							for(DocAcl pot : list){
								ids.add(pot.getUserId());
							}
							list.add(a);
						}
					}
				}
			}
		}
		
		return this.filterInvalid(list);
	}

	// 查找非继承的权限记录
	// lihf 
	@SuppressWarnings("unchecked")
	public List<List<DocAcl>> getDocAclListByNew(Long docId) {
		/**
		 * 查找本级的权限记录
		 */
		List<List<DocAcl>> list = new ArrayList<List<DocAcl>>();
		String hsql = "select a.userId, a.userType from DocAcl as a where a.docResourceId=? and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE
				+ " group by a.userId,a.userType";
		List<Object[]> l = docAclDao.getHibernateTemplate().find(hsql, docId);// 分组查找
		for (Object[] acl : l) {
			Long userId = (Long)(acl[0]);
			String userType = acl[1].toString();
			String hsql2 = "from DocAcl as a where a.docResourceId = ?"
					+ " and a.userId=?" + " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE
					+ " and userType=? order by a.potenttype ";
//					+ "' and a.potenttype!=" + Constants.NOPOTENT;
			List<DocAcl> l2 = docAclDao.find(hsql2, docId, userId, userType);// 根据分组查找记录
			if (!list.contains(l2) && l2.size() > 0)
				list.add(l2);
		}
		
		// 进行组织模型实体有效性判断
		List<List<DocAcl>> list_ret = new ArrayList<List<DocAcl>>();
		for(List<DocAcl> sublist : list){
			if(sublist != null && sublist.size() > 0){
				if(Constants.isValidOrgEntity(sublist.get(0).getUserType(), sublist.get(0).getUserId()))
					list_ret.add(sublist);
			}
		}
		
		return list_ret;
	}

	// 个人借阅权限列表
	public List<DocAcl> getPersonalBorrowList(final Long docResourceId) {
		String hsql = "from DocAcl as a where a.docResourceId=? and a.sharetype =" + Constants.SHARETYPE_PERSBORROW + " and a.potenttype=" + Constants.PERSONALBORROW + " and a.edate>=?";
		List<DocAcl> l  = this.docAclDao.find(hsql, docResourceId, new java.sql.Timestamp(System.currentTimeMillis()));
		return filterInvalid(l);
	}

	// 单位借阅权限列表
	public List<DocAcl> getDeptBorrowList(final Long docResourceId) {
		String hsql = " from DocAcl as a where a.docResourceId=? and a.sharetype =" + Constants.SHARETYPE_DEPTBORROW + 
					  " and a.potenttype=" + Constants.DEPTBORROW + " and a.edate>=?";
		List<DocAcl> docAclList = this.docAclDao.find(hsql, docResourceId, new Timestamp(System.currentTimeMillis()));
		return this.filterInvalid(docAclList);
	}

	// 个人共享权限列表
	public List<DocAcl> getPersonalShareList(Long docResourceId) {
		String hsql = "from DocAcl as a where a.docResourceId=? and a.sharetype =" + Constants.SHARETYPE_PERSSHARE
					  + " and a.potenttype=" + Constants.PERSONALSHARE;
		List<DocAcl> docAclList = docAclDao.find(hsql, docResourceId);
		return this.filterInvalid(docAclList);
	}

	/** 进行组织模型实体有效性判断  */
	private List<DocAcl> filterInvalid(List<DocAcl> docAclList) {
		List<DocAcl> list_ret = new ArrayList<DocAcl>();
		if(CollectionUtils.isNotEmpty(docAclList)) {
			for(DocAcl acl : docAclList){
				if(Constants.isValidOrgEntity(acl.getUserType(), acl.getUserId())) {
					list_ret.add(acl);
				}
			}
		}
		return list_ret;
	}


	/** 个人共享继承的权限列表   */
	@SuppressWarnings("unchecked")
	public List<DocAcl> getPersonalShareInHeritList(Long docResourceId) {
		DocResource doc = docResourceDao.get(docResourceId);
		if(doc != null) {
			DocResource parDoc = docResourceDao.get(doc.getParentFrId());
			if(parDoc != null) {
				String lp = parDoc.getLogicalPath();
				Long drId = this.getDrId4Acl(lp);
				if(drId != null) {
					String hsql = "from DocAcl as a where a.docResourceId=?" + " and a.sharetype=" + 
								  Constants.SHARETYPE_PERSSHARE + " and a.potenttype=" + Constants.PERSONALSHARE;
					List<DocAcl> l = docAclDao.find(hsql, drId);
					return filterInvalid(l);
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取子文档夹继承权限所依赖的最近父文档夹ID
	 * @param logicalPath	子文档夹上一级父文档夹的逻辑路径	
	 */
	@SuppressWarnings("unchecked")
	private Long getDrId4Acl(String logicalPath) {
		String hql = "select docResourceId, count(id) from DocAcl where docResourceId in (:drIds) " +
					 "and sharetype=:sharetype and potenttype=:potenttype group by docResourceId";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		List<Long> drIds = FormBizConfigUtils.parseStr2Ids(logicalPath, "[.]");
		nameParameters.put("drIds", drIds);
		nameParameters.put("sharetype", Constants.SHARETYPE_PERSSHARE);
		nameParameters.put("potenttype", Constants.PERSONALSHARE);
		
		List<Object[]> result = this.docAclDao.find(hql, -1, -1, nameParameters);
		
		Long ret = null;
		if(CollectionUtils.isNotEmpty(result)) {
			Map<Long, Integer> map = new HashMap<Long, Integer>();
			for(Object[] arr : result) {
				map.put((Long)arr[0], (Integer)arr[1]);
			}
			
			Collections.reverse(drIds);
			for(Long drId : drIds) {
				if(map.get(drId) != null && map.get(drId).intValue() > 0) {
					ret = drId;
					break;
				}
			}
		}
		if(log.isDebugEnabled()) {
			log.debug(ret == null ? "哥，翻遍祖宗十八代都找不到订阅记录，洗洗睡吧..." : "哥，找到订阅记录了!年谱中，这位爷的身份证号是[" + ret + "]");
		}
		return ret;
	}

	/** 删除权限   */
	public void deletePotentByUser(Long docResourceId, Long userId, String userType, int potent) {
		String hsql = "delete from DocAcl as a where a.docResourceId=? and a.userId=? and a.userType=? and a.potenttype=?";
		docAclDao.bulkUpdate(hsql, null, docResourceId, userId, userType, potent);
	}

	/** 是否不继承   */
	public boolean isNoInherit(Long userId, String userType, Long docResourceId) {
		String hsql = "select count(a.id) from DocAcl as a where a.docResourceId = ? and a.userId=? and a.userType=? and a.potenttype = " + Constants.NOPOTENT;
		Integer count = (Integer)docAclDao.findUnique(hsql, null, docResourceId, userId, userType);
		return count != null && count.intValue() > 0;
	}

	// 删除借阅
	public void deleteBorrow(Long docResourceId, boolean isMine) {
		String hsql = "delete from DocAcl as a where a.docResourceId=?";
		if (isMine) {
			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
				+ " and a.potenttype=" + Constants.PERSONALBORROW;
		} else {
			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_DEPTBORROW 
				+ " and a.potenttype=" + Constants.DEPTBORROW;
		}
		docAclDao.bulkUpdate(hsql, null, docResourceId);
	}
	/**
	 * 将不在usersId这个集合中的借阅对象删除
	 */
	public void deleteBorrow(Long docResourceId, List<Long> usersId, boolean isMine){
		docAclDao.deleteBorrow(docResourceId, usersId, isMine) ;
	}
	
	/**
	 * 判断是不是有此人的在此文档的借阅
	 * @param docResourceId
	 * @param userid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasAclertBoorrow(Long docResourceId ,Long userid ,boolean isMine ){
		boolean flag = false ;
		DetachedCriteria dc ;
		if(isMine){
			dc = DetachedCriteria.forClass(DocAcl.class).add(Restrictions.eq("docResourceId", docResourceId))
			.add(Restrictions.eq("userId", userid))
			.add(Restrictions.eq("sharetype", Constants.SHARETYPE_PERSBORROW))
			.add(Restrictions.eq("potenttype", Constants.PERSONALBORROW));	
		}else{
			 dc = DetachedCriteria.forClass(DocAcl.class).add(Restrictions.eq("docResourceId", docResourceId))
			.add(Restrictions.eq("userId", userid))
			.add(Restrictions.eq("sharetype", Constants.SHARETYPE_DEPTBORROW))
			.add(Restrictions.eq("potenttype", Constants.DEPTBORROW));	
			
		}

		List<DocAcl> docAclList = docAclDao.executeCriteria(dc, -1, -1) ;
		if(docAclList.size() != 0 ){
			flag = true ;
		}	
		return flag ;
	}


	// 删除个人共享
	public void deletePersonalShare(Long docResourceId) {
		String hsql = "delete from DocAcl as a where a.docResourceId=? and a.sharetype=" + Constants.SHARETYPE_PERSSHARE		
			+ " and a.potenttype=" + Constants.PERSONALSHARE;
		docAclDao.bulkUpdate(hsql,null,docResourceId);
	}
	
	public void deletePersonalShare(Long docResourceId , List<Long> userIds , boolean isPersonLib) {
		docAclDao.deletePersonalShare(docResourceId ,userIds ,isPersonLib) ;
	}

	/**
	 * 判断是不是有此人的在此文档的共享
	 * @param docResourceId
	 * @param userid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasAclertShare(Long docResourceId ,Long userid ,boolean isMine ){
		boolean flag = false ;
		DetachedCriteria dc ;
		if(isMine){
			dc = DetachedCriteria.forClass(DocAcl.class).add(Restrictions.eq("docResourceId", docResourceId))
			.add(Restrictions.eq("userId", userid))
			.add(Restrictions.eq("sharetype", Constants.SHARETYPE_PERSSHARE))
			.add(Restrictions.eq("potenttype", Constants.PERSONALSHARE));	
		}else{
			 dc = DetachedCriteria.forClass(DocAcl.class).add(Restrictions.eq("docResourceId", docResourceId))
			.add(Restrictions.eq("userId", userid))
			.add(Restrictions.eq("sharetype", Constants.SHARETYPE_DEPTSHARE));
		}

		List<DocAcl> docAclList = docAclDao.executeCriteria(dc, -1, -1) ;
		if(docAclList.size() != 0 ){
			flag = true ;
		}	
		return flag ;
	}
	
	
	// 删除单位共享
	public void deleteDeptShareByDoc(Long docResourceId) {
		String hsql = "delete from DocAcl as a where a.docResourceId=? and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE;	
		docAclDao.bulkUpdate(hsql, null, docResourceId);
	}
	
	public void deleteProjectFolderShare(Long projectFolderId, List<Long> oldProjectMemberIds) {
		String hsql = "delete from DocAcl as a where a.docResourceId=:projectFolderId and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE +
					  " and a.userId in (:userIds) and a.userType=:member";	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("projectFolderId", projectFolderId);
		params.put("userIds", oldProjectMemberIds);
		params.put("member", V3xOrgEntity.ORGENT_TYPE_MEMBER);
		
		docAclDao.bulkUpdate(hsql, params);
	}
	
	public Map<Long, String> getSpecialAclsByDocResourceId(DocResource dr, Set<Integer> aclLevels) {
		return this.docAclDao.getAclMap4Index(dr.getLogicalPath());
	}

	@Deprecated
	public Map<Long, String> getSpecialAclsByDocResourceId(Long docResourceId, Set<Integer> aclLevels) {
		DocResource dr = docResourceDao.get(docResourceId);
		return this.getSpecialAclsByDocResourceId(dr, aclLevels);
	}
	
	/**
	 * 取消权限记录里的订阅标记
	 */
	public void cancelAlertByAlertIds(String alertIds){
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("alertIds", Constants.parseStrings2Longs(alertIds, ","));
		String hql = "update DocAcl set isAlert = false, docAlertId = null where docAlertId in (:alertIds) and isAlert = true";
		
		docAclDao.bulkUpdate(hql, nameParameters);
	}
	
	/**
	 * 取得某个文档夹的共享数据
	 */
	public List<PotentModel> getGrantVOs(long docResId, boolean isGroupRes) {
		List<PotentModel> objs = new ArrayList<PotentModel>();

		DocResource dr = docResourceDao.get(docResId);
		List<Long> ownerIds =  DocMVCUtils.getLibOwners(dr);
		// 继承
		List<DocAcl> l = this.getDocAclListByInherit(docResId);
		int order = 0;
		Set<Long> set = new LinkedHashSet<Long>();
		if(CollectionUtils.isNotEmpty(l)){
			for (DocAcl a : l) {
				set.add(a.getUserId());
			}
			
			boolean alert = false;
			Long alertId = -1l;
			for (Long uid : set) {
				PotentModel p = new PotentModel();
				p.setUserId(uid);
				
				if(ownerIds.contains(uid))
					p.setIsLibOwner(true);
				
				for (DocAcl a : l) {
					if (a.getUserId() == uid) {
						order = a.getAclOrder();
						alert = a.getIsAlert();
						alertId = a.getDocAlertId();
						if (p.getUserName() == null) {
							String userName = Constants.getOrgEntityName(a.getUserType(), a.getUserId(), isGroupRes);
	
							p.setUserName(userName);
							p.setUserType(a.getUserType());
						}
						p.copyAcl(a);
					}
				}
				
				if (!this.isNoInherit(p.getUserId(), p.getUserType(), docResId)) {
					p.setInherit(true);
					
					p.setAlert(alert);
					p.setAlertId(alertId);
					p.setAclOrder(order);
					
					objs.add(p);
				}
			}
		}
		// 非继承
		List<List<DocAcl>> l2 = this.getDocAclListByNew(docResId);
		for (List<DocAcl> l3 : l2) {
			PotentModel p = null;
			boolean flag = false;
			if (objs != null && objs.size() > 0) {
				for (PotentModel pm : objs) {
					for (DocAcl temp : l3) {
						if (temp.getUserId() == pm.getUserId()) {
							flag = true;
							p = pm;
							break;
						}
					}
					if (flag) {
						break;
					}
				}
			}
			if (!flag) {
				p = new PotentModel();
			} 
			else{
				p.setAllAcl(false);
			}
			
			boolean isAlert = false;
			long alertId = 0L;
			for (DocAcl acl2 : l3) {
				isAlert = acl2.getIsAlert();
				order = acl2.getAclOrder();
				if(isAlert)
					alertId = acl2.getDocAlertId();
				if (p.getUserId() == null) {
					p.setUserId(acl2.getUserId());
					if(ownerIds.contains(acl2.getUserId()))
						p.setIsLibOwner(true);
					p.setUserType(acl2.getUserType());
					String userName = Constants.getOrgEntityName(acl2.getUserType(), acl2.getUserId(), isGroupRes);
					p.setUserName(userName);
					p.setUserType(acl2.getUserType());
				}
				p.copyAcl(acl2);
			}
			
			p.setAlert(isAlert);
			p.setAlertId(alertId);
			p.setInherit(false);
			p.setAclOrder(order);
			if (!flag) {	
				objs.add(p);
			}
		}
		this.filterNoPotent(objs);
		Collections.sort(objs);
		return objs;
	}
	
	/** 过滤掉没有权限的记录  */
	private void filterNoPotent(List<PotentModel> list){
		if(CollectionUtils.isNotEmpty(list)) {
			for (Iterator<PotentModel> iterator = list.iterator(); iterator.hasNext();) {
				PotentModel pm = (PotentModel) iterator.next();
				if(!pm.hasPotent())
					iterator.remove();
			}
		}
	}
	
	/**
	 * 得到某个文档的某种共享类型的权限列表
	 */
	public List<DocAcl> getAclList(long docResId, byte sharetype){
		String hql = "from DocAcl where docResourceId = ? and sharetype = ?";
		return this.docAclDao.find(hql, docResId, sharetype);
	}
	
	public List<DocAcl> getAclListByPotent(long docResId, int potenttype , long userId){
		String hql = "from DocAcl where docResourceId = ? and potenttype = ? and userId = ?";
		return this.docAclDao.find(hql, docResId, potenttype , userId );
	}
	
	@SuppressWarnings("unchecked")
	public boolean  hasAcl(DocResource dr , long parentId){
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		String ids = Constants.getOrgIdsOfUser(CurrentUser.get().getId());

		Set<Long> set = Constants.parseStrings2Longs(ids, ",");

		if (dr.getIsFolder())
			namedParameterMap.put("id", dr.getId());
		else
			namedParameterMap.put("id", parentId);
		namedParameterMap.put("userId", Constants.parseStrings2Longs(ids, ","));
		String hql = "from DocAcl where docResourceId=:id and sharetype = "
				+ Constants.SHARETYPE_DEPTSHARE + " and userId in(:userId)";

		List<DocAcl> acls = this.docAclDao.find(hql, -1, -1, namedParameterMap);
		if (acls.size() > 0) {
			boolean flag = true;
			for (DocAcl al : acls) {
				if (al.getPotenttype() == Constants.NOPOTENT)
					flag = false;
			}
			return flag;
		} else {
			List<DocAcl> iacls = this.getDocAclListByInherit(dr.getId());
			if (iacls.size() > 0) {
				boolean flag = false;
				for (DocAcl acl : iacls) {
					if (set.contains(acl.getUserId()) && acl.getPotenttype() != Constants.NOPOTENT)
						flag = true;

				}
				return flag;

			} else
				return false;
		}
	}
	
	
	private DocUtils docUtils;

	public DocUtils getDocUtils() {
		return docUtils;
	}

	public void setDocUtils(DocUtils docUtils) {
		this.docUtils = docUtils;
	}
	
	/** 暂不使用 */
	public void init(){	}
	
	/**
	 * 取得普通文档的借阅权限
	 */
	
	@SuppressWarnings("unchecked")
	public String getBorrowPotent(final long docId){
		String ret = "00";
		
		final List<Long> userIds = Constants.getOrgIdsOfUser1(CurrentUser.get().getId());		
		
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(final Session session) throws HibernateException, SQLException {
				final String hsql = "from DocAcl as a where a.docResourceId = :did and a.userId in (:userIds) and (a.sharetype=" 
					+ Constants.SHARETYPE_DEPTBORROW  + " or a.sharetype= " +Constants.SHARETYPE_PERSBORROW
					+ " )and a.sdate<=:starttime and a.edate>=:endtime";
				final Timestamp time = new Timestamp(System.currentTimeMillis());				
	            return session.createQuery(hsql)
	            .setParameterList("userIds", userIds)
	            .setTimestamp("starttime", time)
				.setTimestamp("endtime", time)
				.setLong("did", docId)
				.list();
			}
    	});
		
		if(list != null){
			
			String lp21_ = "0";
			String lp22_ = "0";
			for(DocAcl da : list){
				
				String lp2 = da.getLenPotent2();
				if(lp2 != null){
					String str1 = lp2.substring(0, 1);
					String str2 = lp2.substring(1);
					lp21_ = (lp21_.equals("1") || str1.equals("1") ? "1" : "0");
					lp22_ = (lp22_.equals("1") || str2.equals("1") ? "1" : "0");
				}
				
				if("11".equals(lp21_ + lp22_))
					return "11";
			} 
			
			ret = lp21_ + lp22_;
		}
		
		return ret;
	}

	/**
	 * 取得公文借阅权限
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String getEdocBorrowPotent(final long docId){
		String ret = "000";
		
		final List<Long> userIds = Constants.getOrgIdsOfUser1(CurrentUser.get().getId());		
		
		List<DocAcl> list = (List<DocAcl>)docAclDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocAcl as a where a.docResourceId = :did and a.userId in (:userIds) and (a.sharetype=" 
					+ Constants.SHARETYPE_DEPTBORROW + " or a.sharetype= " + Constants.SHARETYPE_PERSBORROW
					+ ") and a.sdate<=:starttime and a.edate>=:endtime";
				Timestamp time = new Timestamp(System.currentTimeMillis());				
	            return session.createQuery(hsql)
	            .setParameterList("userIds", userIds)
	            .setTimestamp("starttime", time)
				.setTimestamp("endtime", time)
				.setLong("did", docId)
				.list();
			}
    	});
		
		if(list != null){
			byte lp_ = Constants.LENPOTENT_CONTENT;
			String lp21_ = "0";
			String lp22_ = "0";
			for(DocAcl da : list){
				Byte lp = da.getLenPotent();
				if(lp != null && lp.equals(Constants.LENPOTENT_ALL))
					lp_ = lp;
				String lp2 = da.getLenPotent2();
				if(lp2 != null){
					String str1 = lp2.substring(0, 1);
					String str2 = lp2.substring(1);
					lp21_ = (lp21_.equals("1") || str1.equals("1") ? "1" : "0");
					lp22_ = (lp22_.equals("1") || str2.equals("1") ? "1" : "0");
				}
				
				if("111".equals(lp_ + lp21_ + lp22_))
					return "111";
			}
			
			ret = lp_ + lp21_ + lp22_;
		}
		
		return ret;
	}
	/**
	 * 取得公文共享权限
	 * 
	 */
	public String getEdocSharePotent(final long docId){
		String ret = "100";
		DocResource doc = this.docResourceDao.get(docId);
		if(doc == null){
			return ret;
		}else{
			if(doc.getFrType()==Constants.FORMAT_TYPE_LINK){ 
		        Long dId=doc.getSourceId();
		        doc=this.docResourceDao.get(dId);
	        }
		}
		
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		
		Set<Integer> sets = this.getDocResourceAclList(doc, orgIds);
		boolean all = sets.contains(Constants.ALLPOTENT);
		boolean edit = sets.contains(Constants.EDITPOTENT);
		boolean add = sets.contains(Constants.ADDPOTENT);
		boolean readonly = sets.contains(Constants.READONLYPOTENT);

		if(all||edit||add||readonly)  ret="111";
		
		return ret;
	}
	
	/**
	 * 根据订阅id得到对应的授权对象
	 */
	@SuppressWarnings("unchecked")
	public List<DocAcl> getAclListByAlertId(List<Long> alertIds){
		if(alertIds == null){
			return null;
		}
		String hql = "from DocAcl where docAlertId in(:alertIds)";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("alertIds", alertIds);
		return this.docAclDao.find(hql, nameParameters);
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public DocResource getPersonalFolderOfUser(long userId) {
		String hql = "select dr from DocResource as dr where dr.createUserId = ? and dr.frType = ?";
		List<DocResource> list = docResourceDao.find(hql, userId, Constants.FORMAT_TYPE_FOLDER_MINE);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;

	}
	public int getMinOrder() {
		return this.getMaxOrMinOrder(false);
	}

	public int getMaxOrder() {
		return this.getMaxOrMinOrder(true);
	}
	
	/**
	 * 获取当前表中的最大或最小aclOrder	 
	 * @param max	是否取最大（还是最小）
	 * @return	最大或最小排序号
	 */
	private int getMaxOrMinOrder( boolean max) {
	
			String hql = "select " + (max ? "max" : "min") + " (a.aclOrder) from DocAcl a";
			Integer result = (Integer)this.docAclDao.findUnique(hql,null);
			return result == null ? 0 : result;
		
		
	}
	public  String hasAclToDeleteAll(DocResource doc , Long userId){	
		String names = "";
		String orgIds = Constants.getOrgIdsOfUser(userId);
		final Set<Long> userIdLongs = Constants.parseStrings2Longs(orgIds, ",");
		
		String hql = " select distinct doc.id from DocResource doc , DocAcl acl where doc.logicalPath like (:path) and acl.userId in(:userIdLongs) and doc.id = acl.docResourceId";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("path", doc.getLogicalPath() +".%");
		//nmap.put("lp" + i, dr.getLogicalPath() + ".%");
		nameParameters.put("userIdLongs", userIdLongs);		
		List<Long> list = docAclDao.find(hql, -1, -1, nameParameters);
		if (list.size()>0) {
			for(Long id : list){
			DocResource doc1 = this.docResourceDao.get(id);
			Set<Integer> sets = this.getDocResourceAclList(doc1, orgIds);
			if (!sets.contains(Constants.ALLPOTENT)) 
				  names = names+ doc1.getFrName()+ ",";
		     }
		}
		return names;
	}
	
	/**
	 * 分离List，解决in内参数过多，分开处理
	 * 
	 * @param list
	 * @return
	 */
	public List<List<Long>> getSubLists(List<Long> list) {
		List<List<Long>> result = new ArrayList<List<Long>>();
		int maxSize = 999;
		if (list != null) {
			int size = list.size();
			if (size <= maxSize) {
				result.add(list);
			} else {
				for (int i = 0;;) {
					int toIndex = i + maxSize;
					if (toIndex >= size) {
						result.add(list.subList(i, size));
						break;
					} else {
						result.add(list.subList(i, toIndex));
					}
					i = toIndex;
				}
			}
		}
		return result;
	}

}