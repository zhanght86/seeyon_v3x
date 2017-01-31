package com.seeyon.v3x.edoc.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.office.UserUpdateObject;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;

public class EdocSummaryManagerImpl implements EdocSummaryManager {
	
	private EdocSummaryDao edocSummaryDao;
	private static OrgManager orgManager;
	private static OnLineManager onLineManager;
	private static final Log log = LogFactory.getLog(EdocSummaryManagerImpl.class);
	private synchronized void init() {
		if(onLineManager == null){
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			onLineManager = (OnLineManager)ApplicationContextHolder.getBean("onLineManager");
		}
	}
	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao)
	{
		this.edocSummaryDao=edocSummaryDao;
	}

	public EdocSummaryManagerImpl() {
		init();
	}
	
	public EdocSummary findById(long id) {
		return edocSummaryDao.get(id);		
	}
	
	public void saveEdocSummary(EdocSummary o){		
			edocSummaryDao.save(o);		
	}
	
	public void saveOrUpdateEdocSummary(EdocSummary o){
		edocSummaryDao.saveOrUpdate(o);		
	}
	
	public EdocSummary getSummaryByProcessId(String processId){
		 
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocSummary.class);
		criteria.add(Restrictions.eq("processId", processId));
		List<EdocSummary> list = edocSummaryDao.getHibernateTemplate().findByCriteria(criteria);
		if(null!=list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}		
	}
	/**
	 * 根据内部文号判断文号内部文号是否已经使用
	 * @param serialNo  内部文号
	 * @return
	 */
	public int checkSerialNoExsit(String serialNo,Long loginAccout){
		return edocSummaryDao.checkSerialNoExsit(null,serialNo,loginAccout);
	}
	/**
	 * 根据内部文号判断文号内部文号是否已经使用
	 * @param summaryId  公文ID
	 * @param serialNo   内部文号
	 * @param loginAccount  登录单位
	 * @return (1：存在  0：不存在)
	 */
	public int checkSerialNoExsit(String summaryId,String serialNo,Long loginAccount){
		return edocSummaryDao.checkSerialNoExsit(summaryId,serialNo,loginAccount);
	}
//	yangzd=============================================避免文单正文多人同时修改代码开始===================================
	//用office的处理文件ID做为key保存的修改记录
	private final static CacheAccessable cacheFactory = CacheFactory.getInstance(EdocSummaryManager.class);
	private static CacheMap<String,UserUpdateObject> useObjectList = cacheFactory.createMap("FlowIdMsgListMap");
	
	//修改对象,放入对象修改列表
	public synchronized UserUpdateObject editObjectState(String objId)
	{
		if(objId==null || "".equals(objId)){return null;}
		User user=CurrentUser.get();
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		if(os==null)
		{//无人修改
			os=new UserUpdateObject();
			try{
				EdocSummary es=this.findById(Long.parseLong(objId));
				if(es!=null)
				{
					os.setLastUpdateTime(es.getUpdateTime());
				}
				else
				{
					os.setLastUpdateTime(null);
					return os;
				}
				os.setObjId(objId);			
				os.setUserId(user.getId());
				os.setUserName(user.getName());
				addUpdateObj(os);
			}catch(Exception e)
			{				
			}			
		}
		else
		{
			if(os.getUserId()==user.getId())
			{
				os.setCurEditState(false);
			}
			else
			{
				//有用户修改时，要判断用户是否在线,如果用户不在线，删除修改状态
				boolean editUserOnline=true;
				V3xOrgMember member = null; //当前office控件编辑用户
				try{
					member = orgManager.getEntityById(V3xOrgMember.class, os.getUserId());
					editUserOnline=onLineManager.isOnline(member.getLoginName());
				}
				catch(Exception e1){
					log.warn("检查文档是否被编辑，文档编辑用户不存在[" + os.getUserId() + "]", e1);					
				}
				if(editUserOnline)
				{
					os.setCurEditState(true);
				}
				else
				{
					//编辑用户已经离线，修改文档编辑人为当前用户
					os.setUserId(user.getId());
					os.setCurEditState(false);					
				}
			}						
		}
		return os;
	}
	//检查对象是否被修改
	public synchronized UserUpdateObject checkObjectState(String objId)
	{
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		if(os==null){os=new UserUpdateObject();}
		return os;
	}
	public synchronized boolean deleteUpdateObj(String objId)
	{
		User user=CurrentUser.get();
		if(user==null) return true;
		long userId = user.getId();
		return deleteUpdateObj(objId, String.valueOf(userId));
	}
	public synchronized boolean deleteUpdateObj(String objId, String userId) {
		UserUpdateObject os=null;
		if(objId!=null&&!objId.equals(""))
		{
			os=useObjectList.get(objId);
			if(os==null){return true;}
			if(userId.equals(String.valueOf(os.getUserId())))
			{
				useObjectList.remove(objId);
//				//发送集群通知
//				NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectRomove, new String[]{objId,userId});
			}
		}
		return true;
	}
	public synchronized boolean addUpdateObj(UserUpdateObject uo)
	{		
		useObjectList.put(uo.getObjId(),uo);	
		//发送集群通知
//		NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectAdd, uo);

		return true;
	}
	//yangzd=============================================避免文单正文多人同时修改代码结束===================================
	public static Map<String, UserUpdateObject> getUseObjectList() {
		return useObjectList.toMap();
	}
	public static void setUseObjectList(Map<String, UserUpdateObject> uol) {
//		EdocSummaryManagerImpl.useObjectList = useObjectList;
		useObjectList.replaceAll(uol);
	}
	
	@Override
	public List<EdocSummary> getEdocSummaryList(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,boolean isPaging) {
		return 
		edocSummaryDao.getEdocSummaryList(accountId,templeteId,workFlowState,startDate,endDate,isPaging);
	}
	@Override
	public Integer getCaseCountByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate) {
		return edocSummaryDao.getCaseCountByTempleteId(
				accountId,
				templeteId, workFlowState, startDate, endDate);
	}
	@Override
	public Integer getAvgRunWorkTimeByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate) {
		return edocSummaryDao.getAvgRunWorkTimeByTempleteId(accountId,
				templeteId, workFlowState, startDate, endDate);
	}
	@Override
	public Integer getCaseCountGTSD(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,
			Integer standarduration) {
		return edocSummaryDao.getCaseCountGTSD(
				accountId,templeteId, workFlowState, startDate, endDate, standarduration);
	}
	public Double getOverCaseRatioByTempleteId(Long accountId,
			Long templeteId,
			List<Integer> workFlowState,
			Date startDate,
			Date endDate){
		return edocSummaryDao.getOverCaseRatioByTempleteId(
				accountId, 
				templeteId, 
				workFlowState, 
				startDate, 
				endDate);
	}
}
