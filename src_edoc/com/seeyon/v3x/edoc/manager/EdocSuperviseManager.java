package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.domain.EdocSuperviseRemind;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseDealModel;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseModel;

public interface EdocSuperviseManager {
	public void createSuperviseDetail(ColSuperviseDetail colSuperviseDetail);
	public void supervise(String remindMode,String supervisorMemberId,String supervisorNames,String superviseDate,EdocSummary summary); 
	public void createSupervisor(ColSupervisor colSupervisor);
	public void createSuperviseLog (ColSuperviseLog colSuperviseLog);
	public void createSuperviseRemind(EdocSuperviseRemind edocSuperviseRemind);
	public void changeSuperviseDetail(ColSuperviseDetail detail);
	public List<ColSuperviseDetail> findAll(Integer status); 
	public List<ColSuperviseLog> findLogById(Long superviseId);
	public List<EdocSuperviseModel> findToBeProcessedListBySupervisor(Long supervisorId);
	public List<EdocSuperviseModel> findProcessedListBySupervisor(Long supervisorId);
	public ColSuperviseDetail getSuperviseById(Long id);
	public void sendMessage(Long superviseId,String mode,String processId, String activityId, String additional_remark, long[] people,String summaryId);
	public void updateAllDetail(List list);
	public void updateSuperviseDetail(ColSuperviseDetail detail);
    public void updateBySummaryId(long summaryId);
    public void deleteSuperviseDetail(String ids)throws Exception;
	public void pigeonhole(EdocSummary summary)throws EdocException;
    public void deleteSuperviseDetailAndSupervisors(EdocSummary summary)throws EdocException;
	public ColSuperviseDetail getSuperviseBySummaryId(long summaryId);
	public List<ColSupervisor> getEdocSupervisorBySuperviseId(long superviseId);
	public List<EdocSuperviseDealModel> getAffairModel(long summaryId);
	public int getHastenTimes(long superviseId) ;
	/**
	 * 公文撤销删除所有督办人和公文督办记录，并给所有督办人发送消息
	 * @param summaryId//公文ID
	 * @throws Exception
	 */
	public void deleteSuperviseDetailAndSupervisorWhenDelteOrWithDraw(Long summaryId)throws Exception;
	/**
	 * 根据督办明细的ID和督办人的ID删除该督办人在特定督办条目下的下的督办人记录
	 * @param detailId
	 * @param supervisorId
	 * @throws Exception
	 */
	public void deleteSupervisorBySupervisorIdAndDetailId(long detailId, long supervisorId)throws Exception;
	public void superviseForTemplate(String remindMode,String supervisorMemberId,String supervisorNames,String superviseDate,EdocSummary summary,String title) throws Exception;
	public void superviseForSentList(String remindMode,String supervisorMemberId,String supervisorNames,String superviseDate,Long summaryId,String title);
	public boolean ajaxCheckIsSummaryOver(Long summaryId);
	public List<EdocSuperviseModel> queryByCondition(int status , String condition, String textfield, String textfield1);
	////成发集团项目 重写queryByCondition
	public List<EdocSuperviseModel> queryByCondition(int status , String condition, String textfield, String textfield1,Integer secretLevle);
	public String checkColSupervisor(Long summaryId, Affair senderAffair);
	/**
	 * 查询指定类型公文的问号列表，返回问号格式如下:
	 * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ]
	 * @param edocType 应用类型
	 * @return
	 */
	public String queryMarkList(int edocType,int status);
}
