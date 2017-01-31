package com.seeyon.v3x.doc.manager;


@Deprecated
public class DocLinkManagerImpl implements DocLinkManager {
//	private DocLinkDao docLinkDao;
//	private DocResourceDao docResourceDao;
//
//	public DocLinkDao getDocLinkDao() {
//		return docLinkDao;
//	}
//
//	public void setDocLinkDao(DocLinkDao docLinkDao) {
//		this.docLinkDao = docLinkDao;
//	}
//
//	public void addDocLink(long docResId, List<Long> linkResId)throws Exception {
//		User user=CurrentUser.get();
//		for(int i=0;i<linkResId.size();i++){
//			if(docLinkDao.findByIds(docResId, linkResId.get(i))){
//				continue;
//			}
//			DocLink link=new DocLink();
//			link.setIdIfNew();
//			link.setDocResourceId1(docResId);
//			link.setDocResourceId2(linkResId.get(i));
//			link.setCreateUserId(user.getId());
//			link.setCreateTime(new java.sql.Timestamp(new Date().getTime()));
//			docLinkDao.save(link);
//		}
//		
//		
//	}
//
//	/**
//	 * 根据文档id删除所有相关记录
//	 * 包括自己关联的，别人关联自己的
//	 * 如果是文档夹，删除下级所有的
//	 */
//	public void deleteAllLinked(DocResource dr) {
////		List<DocResource> dlist = new ArrayList<DocResource>();
////		dlist.add(dr);
////		if(dr.getIsFolder()) {		
////			String hsql = "from DocResource as a where a.logicalPath like '"
////					+ dr.getLogicalPath() + ".%' or a.id = " + dr.getId();
////			dlist = docResourceDao.find(hsql);
////		}
////		String in = "";
////		for (int i = 0; i < dlist.size(); i++) {
////			in = in + dlist.get(i).getId() + ",";
////		}
////
////		in = in.substring(0, in.length() - 1);
////		
////		String hql2 = "from DocLink where docResourceId1 in (" + in + ") or docResourceId2 in (" + in + ")";
////		docLinkDao.delete(hql2);
//	}
//	
//	public void deleteDocLink(long docResId) {
//////		List<DocLink> list=docLinkDao.findBy("docResourceId1", docResId);
////		String hql = "from DocLink d where d.docResourceId1 = ?";
////		List<DocLink> list = docLinkDao.find(hql, docResId);
////		for(int i=0;i<list.size();i++){
////			DocLink docLink=(DocLink)list.get(i);
////			docLinkDao.delete(docLink);		
////		}
//	}
//
//	public List<DocResource> findDocLinkByDocResId(long docResId) {
////		List<DocLink> list =docLinkDao.findBy("docResourceId1", docResId);
//		String hql = "from DocLink d where d.docResourceId1 = ?";
//		List<DocLink> list = docLinkDao.find(hql, docResId);
//		List<DocResource> the_list=new ArrayList<DocResource>();
//		for(int i=0;i<list.size();i++){
//			DocLink docLink=(DocLink)list.get(i);
//			DocResource docRes=docResourceDao.findUniqueBy("id", docLink.getDocResourceId2());
//			the_list.add(docRes);
//		}
//		return the_list;
//	}
//
//	public void deleteDocLink(long docResId, long... linkResId) {
//////		List<DocLink> list=docLinkDao.findBy("docResourceId1", docResId);
////		String hql = "from DocLink d where d.docResourceId1 = ?";
////		List<DocLink> list = docLinkDao.find(hql, docResId);
////		for(int i=0;i<list.size();i++){
////			DocLink docLink=(DocLink)list.get(i);
////			
////			for(int j=0;j<linkResId.length;j++){
////				if(docLink.getDocResourceId2()==linkResId[j]){
////					docLinkDao.delete(docLink);
////				}
////			}
////		}
//		
//	}
//	
//	/**
//	 * 根据被关联文档删除记录
//	 */
//	public void deleteLinked(long docResourceId) {
//		String hql = "from DocLink d where d.docResourceId2 = " + docResourceId;
//		docLinkDao.delete(hql);
//	}
//
//	public DocResourceDao getDocResourceDao() {
//		return docResourceDao;
//	}
//
//	public void setDocResourceDao(DocResourceDao docResourceDao) {
//		this.docResourceDao = docResourceDao;
//	}

}
