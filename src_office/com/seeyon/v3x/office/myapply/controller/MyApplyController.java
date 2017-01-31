package com.seeyon.v3x.office.myapply.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meetingroom.manager.MeetingRoomManager;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.manager.AssetManager;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.book.domain.TBookApplyinfo;
import com.seeyon.v3x.office.book.manager.BookManager;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.myapply.manager.MyApplyManager;
import com.seeyon.v3x.office.myapply.util.Constants;
import com.seeyon.v3x.office.myapply.util.str;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class MyApplyController extends BaseManageController {
	
	private transient static final Log LOG = LogFactory
	.getLog(MyApplyController.class);
	private OrgManager orgManager;
	private MyApplyManager myApplyManager;
	private AssetManager assetManager;
	private BookManager bookManager;
	private UserMessageManager userMessageManager;
	private AutoManager autoManager;  //车辆管理类
	private StockManager stockManager;    //办公用品管理类
	private MeetingRoomManager meetingRoomManager;
	
	public void setStockManager(StockManager stockManager) {
		this.stockManager = stockManager;
	}

	public void setAutoManager(AutoManager autoManager) {
		this.autoManager = autoManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MyApplyManager getMyApplyManager() {
		return myApplyManager;
	}

	public void setMyApplyManager(MyApplyManager myApplyManager) {
		this.myApplyManager = myApplyManager;
	}
	
	public void setAssetManager(AssetManager assetManager){
		this.assetManager = assetManager;
	}
	
	public void setBookManager(BookManager bookManager){
		this.bookManager = bookManager;
	}
	
	public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
		this.meetingRoomManager = meetingRoomManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
//      by Yongzhang 2008-05-12 AND 2008-6-27修改原因：查询条件输入时间时报错 ， oracle 报错修改于2008-09-23
        //2008-09-25修改分页
		ModelAndView mav = new ModelAndView("office/myapply/list_myapply");
		try{
			
		
		String listSql = "Select * From t_applylist Where del_flag =:delFlag ";
        
		listSql += " And apply_username =:userName ";
		String condition = request.getParameter("condition");
		String keyWord = request.getParameter("textfield");
		Map<String,Object> map = new HashMap<String,Object>();
		if(Strings.isNotBlank(condition) && Strings.isNotBlank(keyWord))
        {
			String searchSql = this.createSearchSql(Integer.parseInt(condition), SQLWildcardUtil.escape(keyWord),map);
            listSql += " And " + searchSql;
		}
		listSql += " Order by apply_date desc";
        map.put("delFlag", Constants.Del_Flag_Normal);
        map.put("userName", CurrentUser.get().getId());
		SQLQuery query = this.myApplyManager.find(listSql,map);
/*		
//		按申请时间查询
		if(condition != null && condition.length() > 0 && keyWord != null && keyWord.length() > 0){
			if(Integer.parseInt(condition)==Constants.Search_Condition_apply_date){
				query.setDate("applyDate", Datetimes.parse(keyWord, "yyyy-MM-dd"));
			}
		}*/
			
		query.addEntity(TApplylist.class);
		List list = query.list();
		
		if(request.getAttribute("script") != null && ((String)request.getAttribute("script")).length() > 0){
			mav.addObject("script", request.getAttribute("script"));
		}
		mav.addObject("list", pagenate(list));
		}catch(Exception e){
			LOG.error("", e) ;
		}
		return mav;
	}
	
	public ModelAndView del(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String[] id = request.getParameterValues("id");
		StringBuffer sb = new StringBuffer();
		if(id != null && id.length > 0){
			for(int i = 0; i < id.length; i++){
               Long applyId= Long.parseLong(id[i]);
				TApplylist apply = this.myApplyManager.getById(applyId);
				if(apply.getApplyState()== Constants.ApplyStatus_Allow){
					sb.append(apply.getApplyId());
					sb.append(" ");
					continue;
				}
				apply.setDelFlag(Constants.Del_TYPE_APP);
				this.myApplyManager.update(apply);
				//删除审批人待办
		        OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, apply.getApplyId());
                // by YongZhang 2008-09-24
                switch (apply.getApplyType())
                {
                    case 1:

                        // 车辆申请表 等待审核的删除
                        if (apply.getApplyState() == 1)
                        {
                            AutoApplyInfo autoApply = myApplyManager.getAutoApplyInfoById(applyId);
                            autoApply.setDeleteFlag(Constants.Del_Flag_Delete);
                            myApplyManager.UpdateAutoApplyInfo(autoApply);
                        }
                        break;
                    case 2:
                        // 设备申请 删除
                        if (apply.getApplyState() == 1)
                        {
                            TAssetApplyinfo assetApply = myApplyManager.getAssetApplyById(applyId);
                            assetApply.setDelFlag(Constants.Del_Flag_Delete);
                            myApplyManager.UpdateAssetApplyInfo(assetApply);
                        }
                        break;
                    case 3:
                        // 办公用品申请
                        if (apply.getApplyState() == 1)
                        {
                            StockApplyInfo stockApply = myApplyManager
                                    .getStockApplyInfoById(applyId);
                            stockApply.setDeleteFlag(Constants.Del_Flag_Delete);
                            myApplyManager.UpdateStockApplyInfo(stockApply);
                        }
                        break;

                    default:
                        break;
                }
			}
		}
		if(sb.length() > 0){
			String script = "alert('"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.apply.cannotdeleted",sb.toString())+"');";
			request.setAttribute("script", script);
		}
		return index(request, response);
	}
	
	public ModelAndView modify(HttpServletRequest request, HttpServletResponse response)throws Exception{
		long id = Long.parseLong(request.getParameter("id"));
		String fs = request.getParameter("fs");
		TApplylist apply = this.myApplyManager.getById(id);
		if(apply == null ||! apply.getDelFlag().equals(Constants.Del_Flag_Normal)){
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.apply.deleted")+"\')");
				out.println("</script>");
				out.flush();
				ModelAndView modelView = new ModelAndView("office/common/success");
				modelView.addObject("script", reflashScript());
				return modelView;
			} catch (Exception e) {
				LOG.error("", e);
			}	
		}
		ModelAndView mav = null;
		switch(apply.getApplyType()){
			case Constants.ApplyType_Asset:{
				mav = new ModelAndView("office/myapply/edit_asset");
				TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(id);
				MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
				mav.addObject("applyBean", apply);
				mav.addObject("assetApplyBean", assetApply);
				mav.addObject("assetBean", asset);
				if(apply.getApplyUser() != null && apply.getApplyUser() != 0){
					V3xOrgMember useMember = this.orgManager.getMemberById(apply.getApplyUser());
					mav.addObject("useName", useMember.getName());
				}
				if(apply.getApplyUsedep() != null && apply.getApplyUsedep() != 0){
					V3xOrgDepartment useDepartment = this.orgManager.getDepartmentById(apply.getApplyUsedep());
					mav.addObject("useDepName", useDepartment.getName());
				}
				if(asset.getAssetMge() != null){
					V3xOrgMember member = this.orgManager.getMemberById(asset.getAssetMge());
					mav.addObject("assetMge_Id", String.valueOf(asset.getAssetMge()));
					mav.addObject("assetMge_Name", member.getName());
				}
				break;
			}
			case Constants.ApplyType_Book:{
				mav = new ModelAndView("office/myapply/edit_book");
				TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(id);
				MBookInfo book = this.bookManager.getById(bookApply.getBookId());
				mav.addObject("applyBean", apply);
				mav.addObject("bookApplyBean", bookApply);
				mav.addObject("bookBean", book);
				if(book.getBookMge() != null){
					V3xOrgMember member = this.orgManager.getMemberById(book.getBookMge());
					mav.addObject("bookMge_Id", String.valueOf(book.getBookMge()));
					mav.addObject("bookMge_Name", member.getName());
				}
				break;
			}
			case Constants.ApplyType_Auto:{
				mav = new ModelAndView("office/myapply/edit_auto");
				AutoApplyInfo autoApply = this.myApplyManager.getAutoApplyInfoById(id);
				AutoInfo auto = this.myApplyManager.getAutoById(autoApply.getAutoId());
				mav.addObject("applyBean", apply);
				mav.addObject("autoApplyBean", autoApply);
				mav.addObject("autoBean", auto);
				V3xOrgMember driverMember = this.orgManager.getMemberById(auto.getAutoDriver());
				mav.addObject("driverName", driverMember.getName());
				if(auto.getAutoManager() != null && auto.getAutoManager() != 0){
					V3xOrgMember member = this.orgManager.getMemberById(auto.getAutoManager());
					mav.addObject("autoMge_Id", String.valueOf(auto.getAutoManager()));
					mav.addObject("autoMge_Name", member.getName());
				}
				break;
			}
			case Constants.ApplyType_Stock:{
				mav = new ModelAndView("office/myapply/edit_stock");
				StockApplyInfo stockApply = this.myApplyManager.getStockApplyInfoById(id);
				StockInfo stock = this.myApplyManager.getStockById(stockApply.getStockId());
				mav.addObject("applyBean", apply);
				mav.addObject("stockApplyBean", stockApply);
				mav.addObject("stockBean", stock);
				if(stock.getStockRes() != null && stock.getStockRes() != 0){
					V3xOrgMember member = this.orgManager.getMemberById(stock.getStockRes());
					mav.addObject("stockMge_Id", String.valueOf(stock.getStockRes()));
					mav.addObject("stockMge_Name", member.getName());
				}
				break;
			}
		}
		if(fs != null && fs.length() > 0){
			mav.addObject("fs", new Integer(1));
		}
		return mav;
	}
	
	public ModelAndView detail_assetApply(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("office/myapply/detail_asset");
		long applyId;
		if(request.getParameter("id") != null && request.getParameter("id").length() > 0){
			applyId = Long.parseLong(request.getParameter("id"));
		}else{
			applyId = ((Long)request.getAttribute("id")).longValue();
		}
		String fs = request.getParameter("fs");
		TApplylist apply = this.myApplyManager.getById(applyId);
		TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(applyId);
		MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
		mav.addObject("applyBean", apply);
		mav.addObject("assetApplyBean", assetApply);
		mav.addObject("assetBean", asset);
		if(apply.getApplyUser() != null && apply.getApplyUser() != 0){
			V3xOrgMember useMember = this.orgManager.getMemberById(apply.getApplyUser());
			mav.addObject("useName", useMember.getName());
		}
		if(apply.getApplyUsedep() != null && apply.getApplyUsedep() != 0){
			V3xOrgDepartment useDepartment = this.orgManager.getDepartmentById(apply.getApplyUsedep());
			mav.addObject("useDepName", useDepartment.getName());
		}
		if(asset.getAssetMge() != null && asset.getAssetMge() != 0 && asset.getDelFlag() ==0){
			V3xOrgMember member = this.orgManager.getMemberById(asset.getAssetMge());
			mav.addObject("assetMge_Id", String.valueOf(asset.getAssetMge()));
			mav.addObject("assetMge_Name", member.getName());
		}else{
			//如果办公设备不存在
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.asset.deleted")+"\')");
			out.println("</script>");
			out.flush();
			//如果申请的办公设备不存在 删除申请
			this.autoManager.deleteAutoApplyByIds(applyId+"");
			
			mav = new ModelAndView("office/common/success");
			mav.addObject("script", reflashScript());
			return mav;	
		}
		if(fs != null && fs.length() > 0){
			mav.addObject("fs", new Integer(1));
		}
		if(request.getAttribute("reloadlist") != null && request.getAttribute("reloadlist").equals("1")){
			mav.addObject("script", reflashScript());
		}
		return mav;
	}
	
	public ModelAndView detail_bookApply(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("office/myapply/detail_book");
		long applyId;
		if(request.getParameter("id") != null && request.getParameter("id").length() > 0){
			applyId = Long.parseLong(request.getParameter("id"));
		}else{
			applyId = ((Long)request.getAttribute("id")).longValue(); 
		}
		String fs = request.getParameter("fs");
		TApplylist apply = this.myApplyManager.getById(applyId);
		TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(applyId);
		MBookInfo book = this.bookManager.getById(bookApply.getBookId());
		mav.addObject("applyBean", apply);
		mav.addObject("bookApplyBean", bookApply);
		mav.addObject("bookBean", book);
		if(book.getBookMge() != null && book.getBookMge() != 0 && book.getDelFlag() == 0){
			V3xOrgMember member = this.orgManager.getMemberById(book.getBookMge());
			mav.addObject("bookMge_Id", String.valueOf(book.getBookMge()));
			mav.addObject("bookMge_Name", member.getName());
		}else{
			//如果图书不存在
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.book.deleted")+"\')");
			out.println("</script>");
			out.flush();
			//如果申请的图书不存在 删除申请
			this.autoManager.deleteAutoApplyByIds(applyId+"");
			
			mav = new ModelAndView("office/common/success");
			mav.addObject("script", reflashScript());
			return mav;	
		}
		if(fs != null && fs.length() > 0){
			mav.addObject("fs", new Integer(1));
		}
		if(request.getAttribute("reloadlist") != null && request.getAttribute("reloadlist").equals("1")){
			mav.addObject("script", reflashScript());
		}
		return mav;
	}
	
	public ModelAndView detail_autoApply(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("office/myapply/detail_auto");
		long applyId;
		if(request.getParameter("id") != null && request.getParameter("id").length() > 0){
			applyId = Long.parseLong(request.getParameter("id"));
		}else{
			applyId = ((Long)request.getAttribute("id")).longValue(); 
		}
		String fs = request.getParameter("fs");
		TApplylist apply = this.myApplyManager.getById(applyId);
		AutoApplyInfo autoApply = this.myApplyManager.getAutoApplyInfoById(applyId);
		AutoInfo auto = null ;
		V3xOrgMember driverMember = null;
		try {
			auto = this.myApplyManager.getAutoById(autoApply.getAutoId());
			driverMember = this.orgManager.getMemberById(auto.getAutoDriver());
		} catch (Exception e) {
			//如果车辆不存在
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.auto.deleted",autoApply.getAutoId())+"\')");
			out.println("</script>");
			out.flush();
			//如果申请的车辆不存在 删除申请
			this.autoManager.deleteAutoApplyByIds(applyId+"");
			
			mav = new ModelAndView("office/common/success");
			mav.addObject("script", reflashScript());
			return mav;		
		}
		mav.addObject("applyBean", apply);
		mav.addObject("autoApplyBean", autoApply);
		mav.addObject("autoBean", auto);
		mav.addObject("driverName", driverMember.getName());
		if(auto.getAutoManager() != null && auto.getAutoManager() != 0){
			V3xOrgMember member = this.orgManager.getMemberById(auto.getAutoManager());
			mav.addObject("autoMge_Id", String.valueOf(auto.getAutoManager()));
			mav.addObject("autoMge_Name", member.getName());
		}
		if(request.getAttribute("reloadlist") != null && request.getAttribute("reloadlist").equals("1")){
			mav.addObject("script", reflashScript());
		}
		return mav;
	}
	
	public ModelAndView detail_stockApply(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("office/myapply/detail_stock");
		long applyId;
		if(request.getParameter("id") != null && request.getParameter("id").length() > 0){
			applyId = Long.parseLong(request.getParameter("id"));
		}else{
			applyId = ((Long)request.getAttribute("id")).longValue(); 
		}
		TApplylist apply = this.myApplyManager.getById(applyId);
		StockApplyInfo stockApply = this.myApplyManager.getStockApplyInfoById(applyId);
		StockInfo stock = this.myApplyManager.getStockById(stockApply.getStockId());
		if(stock == null || stock.getDeleteFlag() != 0){
			//如果用品不存在
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.stock.deleted")+"\')");
			out.println("</script>");
			out.flush();
			//如果申请的用品不存在 删除申请
			this.autoManager.deleteAutoApplyByIds(applyId+"");
			
			mav = new ModelAndView("office/common/success");
			mav.addObject("script", reflashScript());
			return mav;	
		}
		mav.addObject("applyBean", apply);
		mav.addObject("stockApplyBean", stockApply);
		mav.addObject("stockBean", stock);
		if(stock.getStockRes() != null && stock.getStockRes() != 0){
			V3xOrgMember member = this.orgManager.getMemberById(stock.getStockRes());
			mav.addObject("stockMge_Id", String.valueOf(stock.getStockRes()));
			mav.addObject("stockMge_Name", member.getName());
		}
		if(request.getAttribute("reloadlist") != null && request.getAttribute("reloadlist").equals("1")){
			mav.addObject("script", reflashScript());
		}
		return mav;
	}
	
	public ModelAndView doModify(HttpServletRequest request, HttpServletResponse response)throws Exception{
		long applyId = Long.parseLong(request.getParameter("applyId"));
		TApplylist apply = this.myApplyManager.getById(applyId);
		apply.setApplyDate(new Date());
		
		//如果不存在了
		if(apply == null || ! apply.getDelFlag().equals(Constants.Del_Flag_Normal)){
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "alert.apply.deleted")+"\')");
				out.println("</script>");
				out.flush();
				ModelAndView modelView = new ModelAndView("office/common/success");
				modelView.addObject("script", reflashScript());
				return modelView;
			} catch (Exception e) {
				LOG.error("", e);
			}	
		}
		User user = CurrentUser.get();
		
		OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, apply.getApplyId());
		
		switch(apply.getApplyType()){
			case Constants.ApplyType_Asset:{
				apply.setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				this.myApplyManager.update(apply);
				this.updateAssetApply(apply, request);
				request.setAttribute("id", new Long(applyId));
				request.setAttribute("reloadlist", "1");
				
				//~~~~~~~~~~~~~~`
				TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(applyId);
				MAssetInfo mAssetInfo = this.assetManager.getById(assetApply.getAssetId());
				
				OfficeHelper.addPendingAffair(mAssetInfo.getAssetName(), apply, ApplicationSubCategoryEnum.office_asset);
				
				PrintWriter out;
				try {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "myapply.alert.asset.use", mAssetInfo.getAssetName())+"\')");
					out.println("</script>");
					out.flush();
				} catch (Exception e) {
					LOG.error("", e);
				}	
				
				List<Long> auth = new ArrayList<Long>();
				auth.add(mAssetInfo.getAssetMge());
				
				Collection<MessageReceiver> receivers = MessageReceiver.get(user.getDepartmentId(),auth);
				try {
			    	  userMessageManager.sendSystemMessage(MessageContent.get("office.asset.apply", mAssetInfo.getAssetName(),user.getName())
			    			  , ApplicationCategoryEnum.office, user.getId(), receivers);
			        } catch (MessageException e) {	         
			            logger.error("办公设备申请失败", e);
			        }
			
				//~~~~~~~~~~~~~~~
				
				
				return detail_assetApply(request, response);
			}
			case Constants.ApplyType_Book:{
				apply.setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				this.myApplyManager.update(apply);
				this.updateBookApply(apply, request);
				request.setAttribute("id", new Long(applyId));
				request.setAttribute("reloadlist", "1");
				
				//~~~~~
				
				PrintWriter out;
				try {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage(\"officeLang.books_lend_succeed\"))");
					out.println("</script>");
					out.flush();
				} catch (Exception e) {
					LOG.error("", e);
				}	
						
				TBookApplyinfo tainfo =  this.bookManager.getApplyinfoById(applyId);
				MBookInfo mBookInfo = this.bookManager.getById(tainfo.getBookId());	
				
				OfficeHelper.addPendingAffair(mBookInfo.getBookName(), apply, ApplicationSubCategoryEnum.office_book);
							
				List<Long> auth = new ArrayList<Long>();
				auth.add(apply.getApplyMge());
				Collection<MessageReceiver> receivers = MessageReceiver.get(user.getDepartmentId(),auth);
				 try {
			    	  userMessageManager.sendSystemMessage(MessageContent.get("office.book.apply",mBookInfo.getBookName(),CurrentUser.get().getName())
			    			  , ApplicationCategoryEnum.office, CurrentUser.get().getId(), receivers);
			        } catch (MessageException e) {	         
			            logger.error("图书资料申请失败", e);
			        }

				//~~~~~
				
				
				return detail_bookApply(request, response);
			}
			case Constants.ApplyType_Auto:{
				apply.setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				this.myApplyManager.update(apply);
				this.updateAutoApply(apply, request);
				request.setAttribute("id", new Long(applyId));
				request.setAttribute("reloadlist", "1");
				
				//~~~~~~~~~~
				
				OfficeApply officeApply = this.autoManager.getOfficeApplyById(applyId);
				AutoApplyInfo autoApplyInfo = this.autoManager.getAutoApply(officeApply.getApplyId());
				
				OfficeHelper.addPendingAffair(autoApplyInfo.getAutoId(), officeApply, ApplicationSubCategoryEnum.office_auto);
				
				PrintWriter out;
				try {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "myapply.alert.auto.use", autoApplyInfo.getAutoId())+"\')");
					out.println("</script>");
					out.flush();
				} catch (Exception e) {
					LOG.error("", e);
				}	
				
				List<Long> auth = new ArrayList<Long>();
				auth.add(officeApply.getApplyManager());
				Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(officeApply.getApplyDepId()),auth);
				 try {
			    	  userMessageManager.sendSystemMessage(MessageContent.get("office.car.apply", autoApplyInfo.getAutoId(),CurrentUser.get().getName())
			    			  , ApplicationCategoryEnum.office, CurrentUser.get().getId(), receivers);
			        } catch (MessageException e) {	         
			            logger.error("车辆申请失败", e);
			        }
				//~~~~~~~~~~
								
				return detail_autoApply(request, response);
			}
			case Constants.ApplyType_Stock:{
				apply.setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				this.myApplyManager.update(apply);
				this.updateStockApply(apply, request);
				request.setAttribute("id", new Long(applyId));
				request.setAttribute("reloadlist", "1");
				
				//~~~~~~~~~~~~~~~~~
//				根据申请编号从“办公用品详细申请单一览表”取得申请单情报				
				StockApplyInfo stockApply = this.stockManager.getStockApplyById(applyId);
//				根据办公用品编号取得办公用品详细信息
				StockInfo stockInfo = stockManager.getStockInfoById(new Long(stockApply.getStockId()));
				
				OfficeApply officeApply = this.autoManager.getOfficeApplyById(applyId);
				
				// 审批人首页待办
				OfficeHelper.addPendingAffair(stockInfo.getStockName(), officeApply, ApplicationSubCategoryEnum.office_stock);
				
				PrintWriter out;
				try {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(\'"+ResourceBundleUtil.getString(Constants.MYAPPLY_RESOURCE_NAME, "myapply.alert.asset.use", stockInfo.getStockName())+"\')");
					out.println("</script>");
					out.flush();
				} catch (Exception e) {
					LOG.error("", e);
				}	
				
				List<Long> auth = new ArrayList<Long>();
				auth.add(stockInfo.getStockRes());
				Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(stockInfo.getStockId()),auth);
				 try {
			    	  userMessageManager.sendSystemMessage(MessageContent.get("office.work.apply", stockInfo.getStockName(),CurrentUser.get().getName())
			    			  , ApplicationCategoryEnum.office, CurrentUser.get().getId(), receivers);
			        } catch (MessageException e) {	         
			            logger.error("办公用品申请失败", e);
			        }
				//~~~~~~~~~~~~~~~~~~

				return detail_stockApply(request, response);
			}
		}
		return null;
	}
	
	 
	
	/**
	 * @param apply
	 * @param request
	 * @throws Exception
	 */
	private void updateAssetApply(TApplylist apply, HttpServletRequest request)throws Exception{
		TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(apply.getApplyId());
		assetApply.setApplyCount(Long.parseLong(request.getParameter("apply_count")));
		String long_flag = request.getParameter("long_flag");
		String asset_start = request.getParameter("asset_start");
		String asset_end = request.getParameter("asset_end");
		String asset_purpose = request.getParameter("asset_purpose");
		if(asset_start == null || asset_start.length() == 0){
			asset_start = null;
		}
		if(asset_end == null || asset_end.length() == 0){
			asset_end = null;
		}
		if(asset_purpose == null || asset_purpose.length() == 0){
			asset_purpose = null;
		}
		if(long_flag == null || long_flag.equals("0")){
			if(asset_start != null && asset_start.length() > 0){
				assetApply.setAssetStart(str.strToDate(asset_start));
			}
			if(asset_end != null && asset_end.length() > 0){
				assetApply.setAssetEnd(str.strToDate(asset_end));
			}
			assetApply.setLongFlag(0);
		}else if(long_flag.equals("1")){
			assetApply.setLongFlag(new Integer(1));
			assetApply.setAssetStart(str.strToDate(null));
			assetApply.setAssetEnd(str.strToDate(null));
		}
		assetApply.setAssetPurpose(asset_purpose);
		apply.setApplyUser(Long.parseLong(request.getParameter("apply_user")));
		apply.setApplyUsedep(Long.parseLong(request.getParameter("apply_usedep")));
		this.assetManager.update(assetApply);
		this.myApplyManager.update(apply);
	}
	
	private void updateBookApply(TApplylist apply, HttpServletRequest request)throws Exception{
		TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(apply.getApplyId());
		bookApply.setApplyCount(Long.parseLong(request.getParameter("apply_count")));
		bookApply.setBookStart(str.strToDate(request.getParameter("book_start")));
		bookApply.setBookEnd(str.strToDate(request.getParameter("book_end")));
		this.bookManager.update(bookApply);
	}
	
	private void updateAutoApply(TApplylist apply, HttpServletRequest request)throws Exception{
		AutoApplyInfo autoApply = this.myApplyManager.getAutoApplyInfoById(apply.getApplyId());
		autoApply.setAutoDepartTime(request.getParameter("autoDepartTime"));
		autoApply.setAutoBackTime(request.getParameter("autoBackTime"));
		autoApply.setAutoDep(request.getParameter("autoDep"));
		autoApply.setAutoDes(request.getParameter("autoDes"));
		autoApply.setAutoOrigin(request.getParameter("autoOrigin"));
		this.myApplyManager.UpdateAutoApplyInfo(autoApply);
	}
	
	private void updateStockApply(TApplylist apply, HttpServletRequest request)throws Exception{
		StockApplyInfo stockApply = this.myApplyManager.getStockApplyInfoById(apply.getApplyId());
		stockApply.setApplyCount(Integer.parseInt(request.getParameter("applyCount")));
		this.myApplyManager.UpdateStockApplyInfo(stockApply);
	}
	
	
	private String createSearchSql(int condition, String keyWord,Map<String,Object> map){
		String searchSql = "";
		switch(condition){
			case Constants.Search_Condition_apply_type:{
				searchSql = "apply_type =:applyType ";
				map.put("applyType", Integer.parseInt(keyWord));
				break;
			}
			case Constants.Search_Condition_apply_stat:{
				searchSql = "apply_state =:applyState ";
				map.put("applyState", Integer.parseInt(keyWord));
				break;
			}
			case Constants.Search_Condition_apply_date:{
				searchSql = "apply_date =:applyDate ";
				map.put("applyDate", Datetimes.parse(keyWord, "yyyy-MM-dd"));
				break;
			}
		}
		return searchSql;
	}
	
	private static final String reflashScript(){
		StringBuffer str = new StringBuffer();
		//"parent.listFrame.listIframe.location.reload();";
		/*str.append("var listFrame = parent.listFrame;\r\n");
		str.append("if(listFrame == null){\r\n");
		str.append("listFrame = parent.list;\r\n");
		str.append("}else{\r\n");
		str.append("listFrame = parent.listFrame.listIframe;\r\n");
		str.append("}\r\n");
		str.append("listFrame.location.reload();\r\n");*/
		str.append("parent.list.location.href=parent.list.tempUrl");
		return str.toString();
	}
    public ModelAndView openModelWindow(HttpServletRequest request, HttpServletResponse response)throws Exception
    {
        ModelAndView  mv=new ModelAndView("office/myapply/myapplyFrame");
        return mv;
    }
    public ModelAndView content(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/myapply/myapplyContent");
        return mav;
    }
    private <T> List<T> pagenate(List<T> list)
    {
        if (null == list || list.size() == 0)
            return new ArrayList<T>();
        Integer first = Pagination.getFirstResult();
        Integer pageSize = Pagination.getMaxResults();
        Pagination.setRowCount(list.size());
        List<T> subList = null;
        if (first + pageSize > list.size())
        {
            subList = list.subList(first, list.size());
        }
        else
        {
            subList = list.subList(first, first + pageSize);
        }
        return subList;
    }
}
