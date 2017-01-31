/**
 * 
 */
package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.index.share.datamodel.AreaMappingInfo;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.indexInterface.ProxyManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-23
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class PartitionController extends BaseController {

	private PartitionManager partitionManager;
	
	private ProxyManager proxyManager;

	private long id;

	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}

	/**
	 * 
	 * 222 从首要方法进入的第一个业务方法 读取全部数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView result = new ModelAndView("sysMgr/partition/listPartition");
		List<Partition> partitionlist = partitionManager.getAllPartitions();
		result.addObject("partitionlist", partitionlist);

		return result;
	}
	
	public ModelAndView listBorderFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/partition/partitionFrame");
		return result;
	}

	/**
	 * 111 进入主界面的首要方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView partitionFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/partition/partitionBorderFrame");
		return result;
	}

	/**
	 * 
	 * 进入分区管理的添加分区方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addPartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/partition/partition");
		Partition partition = new Partition(); 
		partition =	partitionManager.getPartition(Long.valueOf(1));
		if(partition != null){
			request.setAttribute("oldPath", partition.getPath());  
		}else{
			request.setAttribute("oldPath", "C:\\");
		}
		result.addObject("partitionName",0);
		result.addObject("partitionForm", "createPartition");
		return result;
	}

	/**
	 * 
	 * 进行分区管理的添加设置
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView createPartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// 添加
		try {
			java.util.Date startDates = Datetimes.parseDate(request
					.getParameter("partition.begintime"));
			java.util.Date endDates = Datetimes.parseDate(request
					.getParameter("partition.endtime"));
			Partition partition = new Partition();
			partition.setId(UUIDLong.longUUID());
			partition.setName(request.getParameter("partition.name"));
			partition.setState(Integer.parseInt(request
					.getParameter("partition.state")));
			partition.setStartDate(startDates);
			partition.setEndDate(endDates);
			partition.setPath(request.getParameter("partition.path"));
			partition.setDescription(request
					.getParameter("partition.description"));
			// 判断分区管理的时间断和路径是否正确
			if (partitionManager.getPartition(startDates, endDates, true).size() == 0 && partitionManager.validatePath(request.getParameter("partition.path"))) {
				
				// 如果是分布式全文检索模式 
				if(IndexInitConfig.isRemoteIndex()){
					// 从request中去sharePath的值。（sharePath以前的默认值也是空字符串）
					String sharePath = request.getParameter("partition.sharePath");
					partition.setSharePath( sharePath==null ? "" : sharePath);
					// 传递分区信息到远程服务器
					try{
						dataTransmissionToRemoteServer(partition);
					}catch (Exception e) {
						System.out.println("分区信息同步失败\n"+e);
						//throw new Exception("分区"+partition.getName()+"同步失败");
						PrintWriter errorOut = response.getWriter();
						errorOut.println("<script>");
						errorOut.println("alert(parent.v3x.getMessage('sysMgrLang.system_partition_sync_error'));");
						errorOut.println("</script>");
						return super.refreshWorkspace();
					}
				}
				
				partitionManager.create(partition);
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
				out.println("</script>");

				return super.refreshWorkspace();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 分区管理的删除管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removePartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String[] ids = request.getParameterValues("id");

			Long l = null;
			PrintWriter out = response.getWriter();
			for (int i = 1; i < ids.length; i++) {
				l = Long.valueOf(ids[i].toString());
				if (partitionManager.getPartition(l).getState() == 0) {  
					out.println("<script>");
					System.out.println(Constants.getString4CurrentUser("partition.stop", partitionManager.getPartition(l).getName()));
					out.println("alert(parent.v3x.getMessage('sysMgrLang.system_partition_show','"+partitionManager.getPartition(l).getName()+"'));");
					out.println("</script>");
					return super.refreshWorkspace();
				} 
                else {
					partitionManager.delete(l);
				}
			}
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
			out.println("</script>");

			return super.refreshWorkspace();
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * 分区管理的单击编辑方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView modifyPartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/partition/partition");
		Long id = Long.valueOf(request.getParameter("id"));
		this.id = id;
		Partition partition = new Partition();
		partition = partitionManager.getPartition(id);

		Partition partitions = new Partition(); 
		partitions = partitionManager.getPartition(Long.valueOf(1));
		if(partitions != null){
			request.setAttribute("oldPath", partitions.getPath());  
		}else{
			request.setAttribute("oldPath", "C:\\");
		}
		result.addObject("disabled", "disabled");
		result.addObject("partition", partition);
		result.addObject("partitionName",1);
		result.addObject("partitionForm", "updatePartition");
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		return result;
	}

	public ModelAndView updatePartition(HttpServletRequest request,HttpServletResponse response)
	 throws Exception{
//		 修改
		PrintWriter out = response.getWriter();
		try {
			Partition partition = new Partition();
			partition.setId(id);
			partition.setName(request.getParameter("partition.name"));
			partition.setState(Integer.parseInt(request
					.getParameter("partition.state")));
			partition.setStartDate(Datetimes.parseDate(request
					.getParameter("partition.begintime")));
			partition.setEndDate(Datetimes.parseDate(request
					.getParameter("partition.endtime")));
			partition.setPath(request.getParameter("partition.path"));
			partition.setDescription(request
					.getParameter("partition.description"));
			
			// 如果是分布式全文检索模式 
			if(IndexInitConfig.isRemoteIndex()){
				// 从request中去sharePath的值。（sharePath以前的默认值也是空字符串）
				String sharePath = request.getParameter("partition.sharePath");
				partition.setSharePath( sharePath==null ? "" : sharePath);
				// 传递分区信息到远程服务器
				try{
					dataTransmissionToRemoteServer(partition);
				}catch (Exception e) {
					System.out.println("分区信息同步失败\n"+e);
					//throw new Exception("分区"+partition.getName()+"同步失败");
                    //回复原path
                    Partition oldPartition = partitionManager.getPartition(id);
                    if(oldPartition != null){
                        partition.setPath(oldPartition.getPath());
                    }
					PrintWriter errorOut = response.getWriter();
					errorOut.println("<script>");
					errorOut.println("alert(parent.v3x.getMessage('sysMgrLang.system_partition_sync_error'));");
					errorOut.println("</script>");
					return super.refreshWorkspace();
				}
			}
			
 			partitionManager.update(partition);
 			
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
			out.println("</script>");
		}
		catch (BusinessException e) {
			out.println("<script>");
			out.println("alert('" + Strings.escapeJavascript(e.getMessage()) + "');");
			out.println("history.back();");
			out.println("</script>");
		}
		
		return super.refreshWorkspace();
	}
	/**
	 * 分区管理的拆分磁盘方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView splitPartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView result = new ModelAndView(
				"sysMgr/partition/splitPartition");
		return result;
	}

	/**
	 * 执行分区管理的方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView executeSplitPartition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = Long.valueOf(request.getParameter("id"));

		PrintWriter out = response.getWriter();
		try {
			java.util.Date date = Datetimes.parseDate(request.getParameter("partition.splittime"));
			
			this.partitionManager.splitPartition(id, 
					request.getParameter("partition.name"), 
					request.getParameter("partition.path"), date, 
					request.getParameter("partition.description"));

                out.println("<script>");
                out.println("alert(parent.v3x.getMessage('sysMgrLang.system_partition_split_ok'));");
                out.println("</script>");
                
				return super.refreshWorkspace();
		}
		catch (BusinessException e) {
			out.println("<script>");
			out.println("alert('" + Strings.escapeJavascript(e.getMessage()) + "');");
			out.println("history.back();");
			out.println("</script>");
		}
		
		return null;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 传输分区数据到远程服务器
	 * @throws Exception Rmi远程保存分区信息失败
	 */
	public void dataTransmissionToRemoteServer(Partition partition) throws Exception{
		// 组装AreaMappingInfo数据
		AreaMappingInfo areaMappingInfo = new AreaMappingInfo();
		areaMappingInfo.setAreaId(partition.getId().toString());
		areaMappingInfo.setSharePath(partition.getSharePath());
		//TODO 预留接口参数 areaMappingInfo.setShareUsername(shareUsername);
		//TODO 预留接口参数 areaMappingInfo.setSharePassword(sharePassword);
		//TODO 预留接口参数 areaMappingInfo.setMappingPath(mappingPath);
		
		// 调用insertOrUpdateAttAreaInfo(AreaMappingInfo areaMappingInfo)远程Rmi接口，将这条分区信息同步到全文检索服务器。
//		RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
//		proxyFactory
//				.setServiceInterface(com.seeyon.v3x.index.share.interfaces.IndexManager.class);
//		proxyFactory.setServiceUrl(IndexInitConfig.getIndexAddress(IndexInitConfig.getProp()));
//		proxyFactory
//				.setRefreshStubOnConnectFailure(true);
//		proxyFactory.afterPropertiesSet();
//		IndexManager object = (IndexManager) proxyFactory.getObject();
//		proxyManager.setRealManager(object);
		IndexUtil.getRMIClientProxy(proxyManager);
		// Rmi远程保存分区信息
		proxyManager.insertOrUpdateAttAreaInfo(areaMappingInfo);

	}

	public ProxyManager getProxyManager() {
		return proxyManager;
	}

	public void setProxyManager(ProxyManager proxyManager) {
		this.proxyManager = proxyManager;
	}
	
}
