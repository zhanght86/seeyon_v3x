/**
 * 修改红杉树会议状态controller
 * @author radishlee
 * @since 2011-12-21
 * @describe 修改红杉树会议状态controller
 */
package com.seeyon.v3x.plugin.videoconf.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.propertymapper.idmapper.GuidMapper;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.v3x.plugin.videoconf.manager.VideoConfSynchronManager;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;
import com.seeyon.v3x.videoconference.util.ParseXML;

public class VideoConfController extends BaseController{
	
	private static final long serialVersionUID = -8345488221246316458L;
	private static final String START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String END_TAG = "</RtWebMsg>";
	private static final Log logger = LogFactory.getLog(VideoConfController.class);
	private OrgManagerDirect orgManagerDirect;
	private GuidMapper guidMapper;
	private VideoConfSynchronManager videoConfSynchronManager;
	
	/**
	 * 修改会议状态接口
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 修改会议状态
	 */
	@NeedlessCheckLogin
	public ModelAndView updateVideoConfStatue(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String remoteIp = req.getRemoteAddr();
		String baseUrl = VideoConferenceConfig.WEBBASEURL;
		if(!(baseUrl.indexOf(":")==5||baseUrl.indexOf(":")==4)){
			logger.error("视频会议服务器IP地址配置错误！请检查...url="+VideoConferenceConfig.WEBBASEURL);
			return null;
		}

		String url[] = VideoConferenceConfig.WEBBASEURL.split(":");  
		String ip = url[1]==null?url[1]:url[1].substring(2, url[1].length());
		
		if(!remoteIp.equals(ip)){
			logger.error("系统视频会议模块受到恶意攻击!!恶意ip地址为"+remoteIp);
			return null;
		}
		
		String thread = String.valueOf(Thread.currentThread().hashCode());
		String logPrefix = "ip:[" + remoteIp + "] thread:[" + thread + "] ";
		try {
			InputStream in = req.getInputStream();
			StringBuffer buf = new StringBuffer();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, "utf-8"));

				String line = null;

				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}
				logger.info("红杉树传递状态信息： "+buf.toString());
			} catch (Exception e) {
				resp.getWriter().write(getErrorResponseXml(e.getMessage()));
				logger.error(logPrefix + e.getMessage(), e);
				return null;
			}
			
			List stateMsgList = ParseXML.parseXML4StartMsg(buf.toString());
			for (int i = 0; i < stateMsgList.size(); i++) {
				Map stateMsgMap = (Map) stateMsgList.get(i);

				//红杉树推送会议状态参数
				String meetingId = (String) stateMsgMap.get("MeetingID");
				String status = (String) stateMsgMap.get("Status");
				//会议实际开始时间（会议系统客户端启动的时间）
				long time = Long.parseLong((String) stateMsgMap.get("Time"));
				MtMeetingManagerCAP m = (MtMeetingManagerCAP)ApplicationContextHolder.getBean("mtMeetingManagerCAP");
				List meetingList= m.getMeetingByInfowarelabMeetingId(meetingId);
				
				if(meetingList==null||meetingList.size()==0){
				    logger.info("数据库视频会议Id号（ext4）="+meetingId+"为空，视频会议在数据库级被误删!!!!");
				    continue;
				}
				
				MtMeetingCAP bean = (MtMeetingCAP)meetingList.get(0);
				long beginTime = bean.getBeginDate().getTime();
				long endTime = bean.getEndDate().getTime();

				//status=1时，time表示会议开始时间
				//status=2时，time表示会议结束时间
	//			if ("1".equals(status)) {
					//改变会议状态
	//				if (beginTime - time > 0) {//如果在会议开始之前开启会议。会议状态变成即将召开（15）
						//m.updateState(bean.getId(), Constants.DATA_STATE_WILL_START);
	//				} else if (beginTime - time <= 0) {
						//m.updateState(bean.getId(), Constants.DATA_STATE_START);//正在召开(20)
	//				}
					//logger.info("启动视频会议并且改变会议状态");
//				} else if ("2".equals(status)) {
					//m.updateState(bean.getId(), Constants.DATA_STATE_FINISH);//会议已经结束（30）
					//logger.info("结束视频会议并且改变会议状态");
					
//					ResourceManagerCAP resourceManager = (ResourceManagerCAP)ApplicationContextHolder.getBean("resourceManagerCAP");
//					resourceManager.delResourceIppByAppId(bean.getId());
//					
//					MtResourcesManager mtResourcesManager = (MtResourcesManager)ApplicationContextHolder.getBean("resourcesManager"); 
//					mtResourcesManager.deleteByMeetingId(bean.getId());
//					//将事项清空
//					AffairManager affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
//					affairManager.deleteByObject(ApplicationCategoryEnum.meeting, bean.getId());
//					//将生成的日程事件置为"已完成"状态
//					CalEventManagerCAP calEventManager = (CalEventManagerCAP)ApplicationContextHolder.getBean("calEventManagerCAP");
//					List<CalEventCAP> events = calEventManager.getAllCalEventByAppId(bean.getId(), ApplicationCategoryEnum.meeting.getKey());
//					for(CalEventCAP event : events){
//						event.setStates(4);
//						event.setCompleteRate(100f);
//						calEventManager.save(event, false);
//					}
//				}
			}
		} catch (Exception e) {
			logger.error(logPrefix + e.getMessage(), e);
			resp.getWriter().write(getErrorResponseXml(e.getMessage()));
			return null;
		}

		resp.getWriter().write(getSuccessResponseXml());
		return null;

	}
	
	/**
	 * 返回成功信息
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 返回成功信息
	 */
	private String getSuccessResponseXml() {
		StringBuffer sb = new StringBuffer(START_TAG);
		sb.append("<RtWebMsg xmlns=\"http://www.infowarelab.org/conference/webmsg\">");
		sb.append("<Header>");
		sb.append("<MsgType>UpdateMeetingStatusResponse</MsgType>");
		sb.append("</Header>");
		sb.append("<Body>");
		sb.append("<Result>0</Result>");
		sb.append("<Description>success</Description>");
		sb.append("</Body>");
		sb.append(END_TAG);
		logger.info("成功返回红杉树客户端信息： "+sb.toString());
		return sb.toString();
	}
    
	/**
	 * 返回失败信息
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 返回失败信息
	 */
	private String getErrorResponseXml(String description) {
		StringBuffer sb = new StringBuffer(START_TAG);
		sb.append("<RtWebMsg xmlns=\"http://www.infowarelab.org/conference/webmsg\">");
		sb.append("<Header>");
		sb.append("<MsgType>UpdateMeetingStatusResponse</MsgType>");
		sb.append("</Header>");
		sb.append("<Body>");
		sb.append("<Result>-1</Result>");
		sb.append("<Description>fail</Description>");
		sb.append("</Body>");
		sb.append(END_TAG);
		logger.info("失败返回红杉树客户端信息： "+sb.toString());
		return sb.toString();
	}
	
	/**
	 * 心跳接口
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 返回成功信息
	 */
	@NeedlessCheckLogin
	public ModelAndView synchorservertime(HttpServletRequest req, HttpServletResponse resp){
        String remoteIp = req.getRemoteAddr();
		
		String baseUrl = VideoConferenceConfig.WEBBASEURL;
		if(!(baseUrl.indexOf(":")==5||baseUrl.indexOf(":")==4)){
			logger.error("视频会议服务器IP地址配置错误！请检查...url="+VideoConferenceConfig.WEBBASEURL);
			return null;
		}
		
		String url[] = VideoConferenceConfig.WEBBASEURL.split(":");  
		String ip = url[1]==null?url[1]:url[1].substring(2, url[1].length());
		if(!remoteIp.equals(ip)){
			logger.error("收到恶意攻击!!恶意ip地址为"+remoteIp);
			return null;
		}
		logger.info("调用视频会议时间同步接口：第三方系统ip地址="+ip);
		
		String result = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>                                         "+
		"<videoconf xmlns:my=\"www.seeyon.com/videoconf/2012\">                             "+
		"	<username><![CDATA[    lirb   ]]></username>                                  "+
		"	<password><![CDATA[    Ywh8GGYcaJqemduRmlbouKwP6pI=     ]]></password>                          "+
		"	<seeyonserverip><![CDATA[ "+VideoConferenceConfig.WEBBASEURL+"   ]]></seeyonserverip>                    "+ 
		"	<seeyonservertime><![CDATA[  "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" ]]></seeyonservertime>         "+
		"	<timearea><![CDATA[    Asia/Shanghai   ]]></timearea>                           "+
		"</videoconf>                                                                     ";
		
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			logger.info("调用视频会议时间同步接口："+e);
		}
		return null;
	}

	
	/**
	 * 主页面
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 返回成功信息
	 */
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView framesetThree(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    return new ModelAndView("plugin/uc/videoconf/framset_three");
	}
    
    /**
	 * 同步页面
	 * @author radishlee
	 * @since 2011-12-21
	 * @describe 返回成功信息
	 */
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView synchronOrg(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    return new ModelAndView("plugin/uc/videoconf/videoconfsynchronizaion");
	}
    
    
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView openModelWindow(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    return new ModelAndView("plugin/uc/videoconf/modelwindows");
	}
    
    /**
     * 单位树形结构 - account Tree
     */
    @CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView showLeftTree(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelAndView = new ModelAndView("plugin/uc/videoconf/left_tree");
        List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
        List<WebV3xOrgAccount> resultlist = new ArrayList<WebV3xOrgAccount>();  
        Long groupAccountId = null;
        for (V3xOrgAccount account : accountlist)
        {
            if ((Boolean)SysFlag.sys_isGroupVer.getFlag()) 
            {
                if (account.getIsRoot())
                {
                    groupAccountId = account.getId();
                }

            }
            else
            {
                if (account.getIsRoot())
                {
                    continue;
                }
            }
            WebV3xOrgAccount webaccount = new WebV3xOrgAccount();

            webaccount.setV3xOrgAccount(account);
            Long superId = account.getSuperior();
            if (null != superId && superId != 0)
            {
                V3xOrgAccount superaccount = orgManagerDirect.getAccountById(superId);
                if (null != superaccount)
                {
                    webaccount.setSuperiorName(superaccount.getShortname());
                }
            }
            resultlist.add(webaccount);

        }
        modelAndView.addObject("accountlist", resultlist);
        modelAndView.addObject("groupAccountId", groupAccountId);
        return modelAndView;
    }

    
    /**
     * 各个单位的同步状态
     */
    @CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView showAccountState(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelAndView = new ModelAndView("plugin/uc/videoconf/account_state");
//        List<GKESynedDateValueBean> list = new ArrayList<GKESynedDateValueBean>();
//        String selectIdFlag=request.getParameter("selectIdFlag");
//        List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
//        Map<String, GKESynedDateValueBean> entityList=null;
//		try {
//			entityList = synchronManager
//			        .getAllSynedAccountOrDept(V3xOrgAccount.class.getSimpleName(),"");
//		} catch (Exception e) {
//			logger.error("", e);
//		}
////        ProductEditionEnum productEdition = ProductInfo.getEdition();
//        for (V3xOrgAccount account : accountlist)
//        {
////            if (productEdition.ordinal() != ProductEditionEnum.entgroup.ordinal()&&productEdition.ordinal() != ProductEditionEnum.governmentgroup.ordinal())
////            {
//                if (account.getIsRoot()||account.getSuperior()==-1)
//                {
//                    continue;
//                }
////            }
//            String accountId = account.getId().toString();
//            Object object=entityList.get(accountId);
//            if (object!= null)
//            {
//            	GKESynedDateValueBean valueBean=(GKESynedDateValueBean)object;
//            	valueBean.setEntityName(account.getName());
//            	valueBean.setEnabled(account.getEnabled());
//                list.add(valueBean);
//            }
//            else
//            {
//                String accountGid = guidMapper.getGuid(Long.parseLong(accountId),
//                        GKESynchronManager.MAP_ACCOUNT_BEFORE);
//                if (StringUtils.hasText(accountGid))         
//                {
//                    list.add(new GKESynedDateValueBean(accountId, account.getName(),
//                            GKESynedDateValueBean.SYNSTATE_HAS, GKESynedDateValueBean.ACCOUNT_TYPE,
//                            GKESynedDateValueBean.ISSYNEDSTATE_NOT,account.getEnabled()));
//                }
//                else
//                {
//                    list.add(new GKESynedDateValueBean(accountId, account.getName(),
//                            GKESynedDateValueBean.SYNSTATE_NOT, GKESynedDateValueBean.ACCOUNT_TYPE,
//                            GKESynedDateValueBean.ISSYNEDSTATE_NOT,account.getEnabled()));
//                }
//
//            }
//        }
//        if(!StringUtils.hasText(selectIdFlag))
//        {
//        	 Map<Long,String> dbAccount = guidMapper.getAll(GKESynchronManager.MAP_ACCOUNT_BEFORE);
//        	 if(dbAccount==null||dbAccount.isEmpty()) selectIdFlag="0";
//        }
//        modelAndView.addObject("accountlist", selectIdFlag);
//        modelAndView.addObject("accountlist", list);
        return modelAndView;
    }

    
    
    /**
     * 显示出单位下的所有部门列表
     */
    @CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView showChildDeptInfo(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/uc/videoconf/deptlist");
//
//        int selectIdFlag = 0;
//
//        String accountId = request.getParameter("id");
//        long accountIdLong=Long.parseLong(accountId);
//        String accountGid = guidMapper.getGuid(accountIdLong,
//                GKESynchronManager.MAP_ACCOUNT_BEFORE);
//        Map<String, GKESynedDateValueBean> entityList=null;
//		try {
//			entityList = synchronManager
//			        .getAllSynedAccountOrDept(V3xOrgDepartment.class.getSimpleName(),accountId);
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//
//        if (accountGid != null && !accountGid.equals(""))
//            selectIdFlag = 1;
//        List<GKESynedDateValueBean> deptlist = new ArrayList<GKESynedDateValueBean>();
//
//        List<V3xOrgEntity> departmentList = orgManagerDirect
//                .getEntityListNoRelation(V3xOrgDepartment.class.getSimpleName(), "orgAccountId",accountIdLong,
//                		accountIdLong);
//        if (departmentList != null && departmentList.size() > 1)
//        {
//            Collections.sort(departmentList, new BeanComparator("path"));
//        }
//        for (V3xOrgEntity dept : departmentList) 
//        {
//            String gid = guidMapper.getGuid(dept.getId(), GKESynchronManager.MAP_DEPARTMENT_BEFORE);
//            Object object = entityList.get(dept.getId().toString());
//            if (StringUtils.hasText(gid))  
//            {
//                if (object != null)
//                {
//                	GKESynedDateValueBean gkeSynedDateValueBean = (GKESynedDateValueBean) object;
//                	gkeSynedDateValueBean.setEntityName(dept.getName());
//                	gkeSynedDateValueBean.setEnabled(dept.getEnabled());
//                    deptlist.add(gkeSynedDateValueBean);
//                    
//
//                }
//                else
//                {
//                	deptlist.add(new GKESynedDateValueBean(dept.getId().toString(), dept.getName(),
//                            GKESynedDateValueBean.SYNSTATE_HAS,
//                            GKESynedDateValueBean.ISSYNEDSTATE_NOT,dept.getEnabled()));
//                }
//
//            }
//            else
//            {
//                if (object != null)
//                {
//                	deptlist.add(new GKESynedDateValueBean(dept.getId().toString(), dept.getName(),
//                            GKESynedDateValueBean.SYNSTATE_NOT,
//                            GKESynedDateValueBean.ISSYNEDSTATE_HAS,dept.getEnabled()));
//                }
//                else
//                {
//                	deptlist.add(new GKESynedDateValueBean(dept.getId().toString(), dept.getName(),
//                            GKESynedDateValueBean.SYNSTATE_NOT,
//                            GKESynedDateValueBean.ISSYNEDSTATE_NOT,dept.getEnabled()));
//                }
//            }
//
//        }
//        mav.addObject("selectIdFlag", selectIdFlag);
//        mav.addObject("accountId", accountId);
//        mav.addObject("deptlist", deptlist);
        return mav;
    }

    /****
     * @describe 开始同步
     * @param isDelAllDate
     * @param isOverOrgDate
     * @return String
     * @throws Exception
     */
    
    public String asynchronism(String isDelAllDate, String isOverOrgDate) throws Exception
    {
        
       
        boolean isDelAllDate1 = isDelAllDate.equals("0") ? false : true;       // 是清空原有数据(默认清空)
        boolean isOverOrgDate1 = isOverOrgDate.equals("0") ? false : true;      // 是否覆盖原有数据(默认不覆盖)
        List<V3xOrgAccount> accountList = orgManagerDirect.getAllAccounts();    //查找所有单位，用于单位管理 选择标准
        long endtime = 0;
        logger.info("视频会议组织结构同步开始：本集团共有 " + accountList.size() + " 家单位");   
        try
        {
            long start = System.currentTimeMillis();
            // 按单位同步组织模型数据
            String flag="";
            accountList=this.orderByLen(accountList,null); 
            flag=videoConfSynchronManager.synchronStart(accountList, isOverOrgDate1, guidMapper);
            endtime = System.currentTimeMillis() - start;
            logger.info("组织同步结束，用时：" + endtime + "MS");
        }
        catch (Throwable ex)
        {
            logger.error("fail to synchron org !", ex);
            return "fail";
        }
        return "success";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<V3xOrgAccount> orderByLen(List<V3xOrgAccount> srcList,List<V3xOrgAccount> newList)
    {
    	Map<Long,V3xOrgAccount> mapAccount=new HashMap<Long,V3xOrgAccount>();
    	if(newList==null){newList=new ArrayList<V3xOrgAccount>();}
    	for (V3xOrgAccount account : srcList) {
    		mapAccount.put(account.getId(), account);
		}
        for(V3xOrgAccount account : srcList)
        {
        	 Long parentAccountId=account.getSuperior();
        	 if(parentAccountId==-1||parentAccountId==null)
             {
                  newList.add(account);
                  continue;
             }
        	 V3xOrgAccount account1 = mapAccount.get(parentAccountId);
        	 if(account1==null)
        	   {
        		   continue;
        	   }
        	   if(!newList.contains(account1))
        	   {
        		   newList.add(account1);
        	   }
        	   newList.add(account);
        }
        return newList;
    }
    
    
	public void setGuidMapper(GuidMapper guidMapper) {
		this.guidMapper = guidMapper;
	}
    
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setVideoConfSynchronManager(
			VideoConfSynchronManager videoConfSynchronManager) {
		this.videoConfSynchronManager = videoConfSynchronManager;
	}
}
