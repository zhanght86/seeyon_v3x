package com.seeyon.v3x.doc.manager;

@Deprecated
public class DocCountManagerImpl implements DocCountManager {
//	private DocMetadataDao docMetadataDao;
//
//	private DocResourceDao docResourceDao;
//
//	public DocResourceDao getDocResourceDao() {
//		return docResourceDao;
//	}
//
//	public void setDocResourceDao(DocResourceDao docResourceDao) {
//		this.docResourceDao = docResourceDao;
//	}
//
//	public DocMetadataDao getDocMetadataDao() {
//		return docMetadataDao;
//	}
//
//	public void setDocMetadataDao(DocMetadataDao docMetadataDao) {
//		this.docMetadataDao = docMetadataDao;
//	}
//
//	/*
//	 * 公文统计(non-Javadoc)
//	 * 
//	 * @see com.seeyon.v3x.doc.manager.DocCountManager#countArchivesGroupByYear(java.lang.Long)
//	 */
//	public List<Map> countArchivesGroupByYear(Long docLibId) {
//		String hsql = "select new map( year(a.createTime) as year , "
//				+ "b.integer1 as type, count(*) as count)"
//				+ "from DocResource as a  , DocMetadata as b "
//				+ "where a.id=b.docResourceId and a.frType="
//				+ Constants.SYSTEM_ARCHIVES + " and a.docLibId = " + docLibId
//				+ " group by year(a.createTime), b.integer1 "
//				+ "order by year(a.createTime)";
//		return docMetadataDao.getArchivesCount(hsql);
//	}
//
//	public List<Map> countArchivesGroupByMonth(String year, Long docLibId) {
//		String hsql = "select new map( month(a.createTime) as month , "
//				+ "b.boolean3 as type, count(*) as count)"
//				+ "from DocResource as a  , DocMetadata as b "
//				+ "where a.id=b.docResourceId and a.frType="
//				+ Constants.SYSTEM_ARCHIVES + " and a.docLibId = " + docLibId
//				+ " and year(a.createTime)=" + year
//				+ " group by month(a.createTime), b.boolean3 "
//				+ "order by month(a.createTime)";
//		return docMetadataDao.getArchivesCount(hsql);
//	}
//
//	public List<Map> countInArchivesGroupByDept(String year, Long docLibId) {
//		String hsql = "select new map( b.varchar1 as dept, count(*) as count)"
//				+ " from DocResource as a  , DocMetadata as b "
//				+ " where a.id=b.docResourceId and a.frType="
//				+ Constants.SYSTEM_ARCHIVES + " and a.docLibId = " + docLibId
//				+ " and year(a.createTime)=" + year + " and b.integer1=1"
//				+ " group by b.varchar1 " + " order by count(*)";
//		return docMetadataDao.getArchivesCount(hsql);
//	}
//
//	public List<Map> countOutArchivesGroupByDept(String year, Long docLibId) {
//		String hsql = "select new map( b.reference1 as dept, count(*) as count)"
//				+ " from DocResource as a  , DocMetadata as b "
//				+ " where a.id=b.docResourceId and a.frType="
//				+ Constants.SYSTEM_ARCHIVES
//				+ " and a.docLibId = "
//				+ docLibId
//				+ " and year(a.createTime)="
//				+ year
//				+ " and b.integer1=2"
//				+ " group by b.reference1 " + " order by count(*)";
//		return docMetadataDao.getArchivesCount(hsql);
//	}
//
//	/*
//	 * 类型统计(non-Javadoc)
//	 * 
//	 * @see com.seeyon.v3x.doc.manager.DocCountManager#countByType()
//	 */
//	public List<Map> countByType() {
//		String hsql = "select new map(a.frType as frType,count(*) as count)"
//				+ "from DocResource as a ,DocLib as b where a.docLibId = b.id and b.type!="
//				+ Constants.personalLibType + "a.isFolder=0 group by a.frType "
//				+ "order by count(*)";
//		return docResourceDao.countDocs(hsql);
//	}
//
//	public List<Map> countByTypeDocLib(Long docLibId) {
//		String hsql = "select new map(a.frType as frType,count(*) as count)"
//				+ "from DocResource as a where a.isFolder=0 and a.docLibId="
//				+ docLibId + "group by a.frType order by count(*)";
//		return docResourceDao.countDocs(hsql);
//	}
//
//	/*
//	 * 创建者统计(non-Javadoc)
//	 * 
//	 * @see com.seeyon.v3x.doc.manager.DocCountManager#countDocsByUser(java.sql.Timestamp,
//	 *      java.sql.Timestamp, java.lang.Long)
//	 */
//
//	public List<Map> countDocsByUser(Timestamp begin, Timestamp end,
//			Long docLibId) {
//		String hsql = "select new map( a.createUserId as userId, "
//				+ " count(*) as count) " + "from DocResource as a where "
//				+ " a.isFolder=0 and a.docLibId = " + docLibId
//				+ " and a.createTime>'" + begin + "' and a.createTime<'" + end
//				+ "'" + "group by a.createUserId " + "order by count(*)";
//		return docResourceDao.countDocs(hsql);
//	}
//
//	/*
//	 * 公文借阅统计(non-Javadoc)
//	 * 
//	 * @see com.seeyon.v3x.doc.manager.DocCountManager#countArchivesBorrowByDoc()
//	 */
//	public List<Map> countArchivesBorrowByDoc(java.sql.Timestamp begin,
//			java.sql.Timestamp end, Long docLibId) {
//		String hsql = "select new map(a.docResourceId as docId,count(*) as count)"
//				+ "from DocBorrowHistory as a ,DocResource as b where a.borrowType="
//				+ Constants.SHARETYPE_DEPTBORROW
//				+ " and a.sdate>'"+begin+"' and a.sdate<'"+end+"' and b.docLibId="+docLibId
//				+ " and b.id = a.docResourceId and b.frType = "
//				+ Constants.SYSTEM_ARCHIVES
//				+ " group by a.docResourceId order by count(*)";
//		return docResourceDao.countDocs(hsql);
//	}
//
//	public List<Map> countArchivesBorrowByUser(java.sql.Timestamp begin,
//			java.sql.Timestamp end, Long docLibId) {
//		String hsql = "select new map(a.userId as userId,a.userType as userType,count(*) as count)"
//			+ "from DocBorrowHistory as a ,DocResource as b where a.borrowType="
//			+ Constants.SHARETYPE_DEPTBORROW
//			+ " and a.sdate>'"+begin+"' and a.sdate<'"+end+"' and b.docLibId="+docLibId
//			+ " and b.id = a.docResourceId and b.frType = "
//			+ Constants.SYSTEM_ARCHIVES
//			+ " group by a.userId order by count(*)";
//	return docResourceDao.countDocs(hsql);
//	}

	// /**
	// * 文档库文档统计
	// */
	// public List<Map> countDocsGroupByYear() {
	// String hsql = "select new map(year(a.createTime) as year,"
	// + " a.createUserId as userId, "
	// + "a.docLibId as docLibId, count(*) as count) "
	// + "from DocResource as a ,DocLib as b where a.docLibId = b.id "
	// + "and a.isFolder=0 " + "and b.type<>"
	// + Constants.personalLibType
	// + "group by year(a.createTime), a.createUserId,a.docLibId "
	// + "order by year(a.createTime)";
	// return docResourceDao.countDocs(hsql);
	// }

	// /**
	// * 通用统计
	// */
	// public List<Map> commonCount(String count, String table,
	// String countdition, String groupBy) {
	// String hsql = "select new map( " + count + " , count(*) as count ) "
	// + " from " + table
	// + " where " + countdition
	// + " group by " + groupBy;
	// return docResourceDao.countDocs(hsql);
	// }

}
