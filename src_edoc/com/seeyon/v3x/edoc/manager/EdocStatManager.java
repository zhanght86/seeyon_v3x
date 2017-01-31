package com.seeyon.v3x.edoc.manager;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.edoc.domain.EdocStat;
import com.seeyon.v3x.edoc.domain.EdocStatCondObj;
import com.seeyon.v3x.edoc.domain.EdocStatDisObj;
import com.seeyon.v3x.edoc.domain.EdocSummary;

public interface EdocStatManager {
	
	/**
	 * 创建公文统计记录。
	 * @param edocId 公文id
	 * @param edocType 公文类型：发文|签报
	 * @param subject 公文标题
	 * @param docType 公文种类
	 * @param docMark 公文文号
	 * @param createDate 建文日期 格式：yyyy-mm-dd
	 * @param deptId 发文部门id
	 * @param sendTo 主送单位
	 * @param copyTo 抄送单位
	 * @param issuer 签发人
	 * @param copies 份数
	 * @param flowState 公文流转状态（待发送，流转中，结束，撤销）
	 * @param domainId 单位id
	 * @throws Exception
	 */
	public void createEdocStat(long edocId,
			int edocType,
			String subject,
			String docType,
			String docMark,
			String createDate,			
			long deptId,
			String sendTo,
			String copyTo,
			String issuer,
			int copies,
			int flowState, 
			long domainId,int contentNo) throws Exception;
	
//	/**
//	 * 创建公文统计记录（登记保存时调用）。
//	 * @param edocId 公文id
//	 * @param subject 公文标题
//	 * @param docType 公文种类
//	 * @param docMark 公文文号
//	 * @param createDate 建文日期 格式：yyyy-mm-dd
//	 * @param deptId 收文部门id 
//	 * @param sendTo 主送单位
//	 * @param copyTo 抄送单位
//	 * @param issuer 签发人
//	 * @param copies 份数
//	 * @param flowState 公文流转状态（待发送，流转中，结束，撤销）
//	 * @param domainId 单位id
//	 * @throws Exception
//	 */
//	public void createEdocStat(long edocId,			
//			String subject,
//			String docType,
//			String docMark,
//			String createDate,
//			long deptId,
//			String sendTo,
//			String copyTo,
//			String issuer,
//			int copies,
//			int flowState,
//			long domainId) throws Exception;

	/**
	 * 更新公文统计信息。
	 * 
	 * 以下情况更新公文统计信息：
	 * 1、修改公文基本信息时；
	 * 2、公文流程状态改变时；
	 * 3、发文封发时；
	 * 4、对公文进行归档操作时。
	 * 
	 * @param edocStat
	 * @throws Exception
	 */
	public void updateEdocStat(EdocStat edocStat) throws Exception;
	
	public EdocStat getEdocStat(long id);
	
	/**
	 * 根据公文id得到公文统计记录。
	 * @param edocId 公文id
	 * @return EdocStat
	 */
	public EdocStat getEdocStatByEdocId(long edocId);
	
	/**
	 * 根据公文id得到公文统计记录。
	 * @param edocId 公文id
	 * @return EdocStat
	 */
	public List<EdocStat> getEdocStatsByEdocId(long edocId);
	
	/**
	 * 统计公文，返回统计结果表。
	 * @param esco
	 * @param groupType
	 * @return
	 */
	public List<EdocStatDisObj> statEdoc(EdocStatCondObj esco, int groupType);
	
	/**
	 * 输入查询条件，查询发文统计记录。
	 * @param edocType 公文类型
	 * @param flowState 公文流转状态（流转中、已封发）
	 * @param beginDate 开始日期（建文日期）
	 * @param endDate 结束日期（建文日期）
	 * @param deptId 部门id
	 * @param domainId 单位id
	 * @return List<EdocStat>
	 */
	public List<EdocStat> querySentEdocStat(int flowState, Date beginDate, Date endDate, long deptId, long domainId);
	/**
	 * 输入查询条件，查询发文统计记录。
	 * @param edocType 公文类型
	 * @param flowState 公文流转状态（流转中、已封发）
	 * @param beginDate 开始日期（建文日期）
	 * @param endDate 结束日期（建文日期）
	 * @param deptIds 部门集合
	 * @param domainId 单位id
	 * @param needPagination 是否需要分页
	 * @return List<EdocStat>
	 */
	public List<EdocStat> querySentEdocStat(int flowState, Date beginDate, Date endDate, Collection<Long> deptIds, long domainId,boolean needPagination);
	/**
	 * 查询已归档公文。
	 * @param beginDate 开始日期（建文日期）
	 * @param endDate 结束日期（建文日期）
	 * @param deptIds 部门id集合
	 * @param domainId 单位id
	 * @param needPagination 是否需要分页
	 * @return List<EdocStat>
	 */
	public List<EdocStat> queryArchivedEdocStat(Date beginDate, Date endDate, Collection<Long> deptIds, long domainId,boolean needPagination);
	/**
	 * 查询收文、签报统计记录。
	 * @param beginDate 开始日期（登记日期）
	 * @param endDate 结束日期（登记日期）
	 * @param deptId 部门id集合
	 * @param domainId 单位id
	 * @param needPagination 是否需要分页
	 * @return List<EdocStat>
	 */
	public List<EdocStat> queryEdocStat(int edocType, Date beginDate, Date endDate, Collection<Long> deptIds, long domainId,boolean needPagination);
	
	/**
	 * 查询收文、签报统计记录。
	 * @param beginDate 开始日期（登记日期）
	 * @param endDate 结束日期（登记日期）
	 * @param deptId 部门id
	 * @param domainId 单位id
	 * @return List<EdocStat>
	 */
	public List<EdocStat> queryEdocStat(int edocType, Date beginDate, Date endDate, long deptId, long domainId);
	
	/**
	 * 查询已归档公文。
	 * @param beginDate 开始日期（建文日期）
	 * @param endDate 结束日期（建文日期）
	 * @param deptId 部门id
	 * @param domainId 单位id
	 * @return List<EdocStat>
	 */
	public List<EdocStat> queryArchivedEdocStat(Date beginDate, Date endDate, long deptId, long domainId);
	
//	public List<EdocStat> queryDetail(EdocStatDisObj esdo);
	/**
	 * 根据公文对象生产统计记录
	 * @param summary
	 * @param user
	 * @throws Exception
	 */
	public void createState(EdocSummary summary,User user) throws Exception;
	
	public void createEdocStat(long edocId,
			int edocType,
			String subject,
			String docType,
			String docMark,
			String createDate,
			long deptId,
			String sendTo,
			String copyTo,
			String issuer,
			int copies,
			int flowState,
			long domainId,int contentNo,
			String serialNo,long createrUserId ,long accountId
			) throws Exception ;
	
	/**
	 * 公文元素修改后，修改对应的统计数据记录
	 * @param summary
	 * @throws Exception
	 */
	public void updateElement(EdocSummary summary) throws Exception;
	/**
	 * 更新流程状态
	 * @param edocId
	 * @param flowState
	 * @throws Exception
	 */
	public void updateFlowState(Long edocId,int flowState) throws Exception;
	/**
	 * 设置为封发
	 * @param edocId
	 * @throws Exception
	 */
	public void setSeal(Long edocId) throws Exception;
	/**
	 * 设置为归档
	 * @param edocId
	 * @throws Exception
	 */
	public void setArchive(Long edocId) throws Exception;
	
	/**
	 * 保存公文备考信息。
	 * @param id 公文统计记录id
	 * @param remark 备考信息
	 * @throws Exception
	 */
	public void saveEdocRemark(Long id, String remark) throws Exception;
	
	/**
	 * 删除公文统计记录
	 * @param summaryId
	 * @throws Exception
	 */
	public void deleteEdocStat(Long summaryId)throws Exception;
	
	public List<EdocStat> queryEdocStatAll(int edocType, Date beginDate, Date endDate, long deptId, long domainId);
	public List<EdocStat> querySentEdocStatAll(int flowState, Date beginDate, Date endDate, long deptId, long domainId);
	public List<EdocStat> queryArchivedEdocStatAll(Date beginDate, Date endDate, long deptId, long domainId);
}
