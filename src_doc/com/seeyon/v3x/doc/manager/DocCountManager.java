package com.seeyon.v3x.doc.manager;

@Deprecated
public interface DocCountManager {
//	/**
//	 * 按年份和收发等统计公文
//	 * 
//	 * @param docLibId
//	 * @return Map的key为"year"(年份)、"type"(收发)、"count"(数量)
//	 */
//	public List<Map> countArchivesGroupByYear(Long docLibId);
//
//	/**
//	 * 按月份和收发等统计公文
//	 * 
//	 * @param year
//	 * @param docLibId
//	 * @return Map的key为"month"(月份)、"type"(收发)、"count"(数量)
//	 */
//	public List<Map> countArchivesGroupByMonth(String year, Long docLibId);
//
//	/**
//	 * 按发文单位统计
//	 * 
//	 * @param year
//	 * @param docLibId
//	 * @return Map的key为"dept"(单位名称)、"count"(数量)
//	 */
//	public List<Map> countInArchivesGroupByDept(String year, Long docLibId);
//
//	/**
//	 * 按发起部门统计
//	 * 
//	 * @param year
//	 * @param docLibId
//	 * @return Map的key为"dept"(部门id)、"count"(数量)
//	 */
//	public List<Map> countOutArchivesGroupByDept(String year, Long docLibId);
//
//	/**
//	 * 按类别统计(全部)
//	 * 
//	 * @return Map的key为"frType"(类别)、"count"(数量)
//	 */
//	public List<Map> countByType();
//
//	/**
//	 * 按类别统计(库)
//	 * 
//	 * @return Map的key为"frType"(类别)、"count"(数量)
//	 */
//	public List<Map> countByTypeDocLib(Long docLibId);
//
//	/**
//	 * 按创建者统计
//	 * 
//	 * @param begin
//	 * @param end
//	 * @param docLibId
//	 * @return Map的key为"userId"(用户id)、"count"(数量)
//	 */
//	public List<Map> countDocsByUser(java.sql.Timestamp begin,
//			java.sql.Timestamp end, Long docLibId);
//
//	/**
//	 * 按借阅次数统计公文借阅
//	 * 
//	 * @param begin
//	 * @param end
//	 * @param docLibId
//	 * @return Map的key为"docId"(公文id)、"count"(借阅次数)
//	 */
//	public List<Map> countArchivesBorrowByDoc(java.sql.Timestamp begin,
//			java.sql.Timestamp end, Long docLibId);
//
//	/**
//	 * 按借阅人统计公文借阅
//	 * 
//	 * @param begin
//	 * @param end
//	 * @param docLibId
//	 * @return Map的key为"userId"(用户id)、"userType"(用户类型)、"count"(借阅次数)
//	 */
//	public List<Map> countArchivesBorrowByUser(java.sql.Timestamp begin,
//			java.sql.Timestamp end, Long docLibId);

	// /**
	// * 文档按年份、创建者、文档库统计
	// *
	// * @return Map的key为"year"(年份)、"userId"(创建者)、"docLibId"(文档库)、"count"(数量)
	// */
	// public List<Map> countDocsGroupByYear();
	//	

	// /**
	// * 通用统计
	// *
	// * @param count
	// * 统计显示的字段,例如：year(createTime) as year (不需要count(*))
	// * @param table
	// * 统计的表，例如：DocResource
	// * @param countdition
	// * 统计的条件，例如：isFolder=0
	// * @param groupBy
	// * 统计的字段，例如：year(createTime)
	// * @return Map的key为统计显示的字段的别名, "count"(数量)
	// */
	// public List<Map> commonCount(String count, String table,
	// String countdition, String groupBy);

	// /**
	// * 统计公文借阅
	// *
	// * @return Map的key为"docId"(公文id)、"year"(年份)、"count"(借阅次数)
	// */
	// public List<Map> countArchivesBorrow();

}
