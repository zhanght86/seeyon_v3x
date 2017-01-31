package com.seeyon.v3x.collaboration.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.collaboration.domain.ManagementSetAcl;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;

/**
 * 工作管理统计Manager
 */
public interface WorkStatManager {
    
    /**
     * 工作管理的统计
     * @param user_id
     * @return
     */
    public Map<Long, int[]> colStat4WorkManage(int app, List<Long> memberIds, Date beginOfDate, Date endOfDate) throws ColException;
    
    /**
     * 查询符合条件的协同列表
     * @param memberId 人员ID
     * @param type 类型，指待办、暂存待办、本日已办、本周已发等..
     * @param beginDate 起始时间
     * @param endDate 结束时间
     * @return
     */
    List<ColSummaryModel> queryColList(Long memberId, int type, Date beginDate, Date endDate) throws ColException;

    /**
     * 超期管理 - 统计某用户的超期数
     * @param memberIdsList
     * @param beginOfDate
     * @param endOfDate
     * @return
     */
    public Map<Long, int[]> colOverTimeStat(int app, List<Long> memberIds, Date beginOfDate, Date endOfDate) throws ColException;
    
    /**
     * 查询符合条件的超期协同列表
     * @param memberId 人员ID
     * @param type 类型，本周超期待办、本月超期已办等..
     * @param beginDate 起始时间
     * @param endDate 结束时间
     * @return
     */
    List<ColSummaryModel> queryOverTimeList(Long memberId, int type, Date beginDate, Date endDate) throws ColException;
    
    /**
     * AJAX更新选定期限的已发、已办数目
     * @param app 应用
     * @param memberIdsArray 人员ID数组
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return [length][0]=人员ID,[length][1]=已办数目,[length][2]=已发数目
     */
    String[][] ajaxUpdateStatValue(int app, String[] memberIdsArray, String beginDateStr, String endDateStr) throws ColException;
    
    /**
     * AJAX更新选定期限的超期待办、超期已办数目
     * @param app 应用
     * @param memberIdsArray 人员ID数组
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return [length][0]=人员ID,[length][1]=超期待办数目,[length][2]=超期已办数目
     */
    String[][] ajaxUpdateOverTimeStat(int app, String[] memberIdsArray, String beginDateStr, String endDateStr) throws ColException;
    
	public List<ManagementSet> findSetListByDomainId(long domainId);
	
	public ManagementSet findById(long id);
	
	public void updateSet(ManagementSet set);
	
	public void deleteSetAndAcls(String setId);
	
	public void saveSet(ManagementSet set);
	
	public void saveSetAcls(ManagementSetAcl acl);
	
	public void saveSetAndAcls(String personId, String aclId, String manageRange);
	
	public void updateSetAndAcls(long id,String personId, String aclId, String manageRange);
	
	public List<Long> getMembersByGrantorIdAndType(long domainId,long memberId , int type);
	
	public boolean hasThePermission(long domainId,long memberId);
	
	public List<EdocSummaryModel> queryEdocList(Long memberId, int type, Date beginDate, Date endDate) throws ColException;
	
    public List<EdocSummaryModel> queryOverEdocTimeList(Long memberId, int type, Date beginDate, Date endDate) throws ColException ;
    
    //查看工作管理授权是否重复，重复将返回提示重复的字符串
    public String checkRepeatPermission(String memberId,String grantId,String manageRange,Long id);
}