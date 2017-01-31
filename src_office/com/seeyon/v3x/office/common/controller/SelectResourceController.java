package com.seeyon.v3x.office.common.controller;

/**
 * 资源选择窗操作控制类
 */
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.stock.domain.StockInfo;

public class SelectResourceController extends BaseManageController {

	private String indexView;		
	private String headView;
	
	private String stockView;
	private String assetView;
	private String autoView;
	private String bookView;
	
	private OfficeCommonManager officeCommonManager;
	
	public void setOfficeCommonManager(OfficeCommonManager officeCommonManager) {
		this.officeCommonManager = officeCommonManager;
	}
	
	public void setAssetView(String assetView) {
		this.assetView = assetView;
	}
	public void setAutoView(String autoView) {
		this.autoView = autoView;
	}
	public void setBookView(String bookView) {
		this.bookView = bookView;
	}
	public void setHeadView(String headView) {
		this.headView = headView;
	}
	public void setIndexView(String indexView) {
		this.indexView = indexView;
	}
	public void setStockView(String stockView) {
		this.stockView = stockView;
	}
	
	
	/**
	 * 资源选择窗口框架页
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(indexView);
		
		String lossField = request.getParameter("lossField");
		
		if(lossField == null){
			lossField = "1";
		}
		String resourceType = "auto";
		
		if("2".equals(lossField)){
			resourceType = "asset";
		}else if("3".equals(lossField)){
			resourceType = "stock";
		}else if("4".equals(lossField)){
			resourceType = "book";
		}else{
			resourceType = "auto";
		}
		mav.addObject("resourceType", resourceType);
		return mav;
	}
	
	/**
	 * 资源选择窗口框架
	 */
	public ModelAndView head(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(headView);
		return mav;
	}
	
	/**
	 * 车辆列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auto(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(autoView);
		User user = CurrentUser.get();
        String countSql = "select count(*)  as myTotalCount from m_auto_info where del_flag=0 and auto_mge="+user.getId();
		
//		if(!Pagination.isNeedCount().booleanValue()){
			int size = this.officeCommonManager.getCount(countSql);
			Pagination.setRowCount(size);
//		}

		String sql = "select * from m_auto_info where del_flag=0 and auto_mge="+user.getId();
//		Session session = this.officeCommonManager.getCurSession();
//		try{
//			SQLQuery query = session.createSQLQuery(sql);
//			query.setFirstResult(Pagination.getFirstResult());
//			query.setMaxResults(Pagination.getMaxResults());
//			query.addEntity(AutoInfo.class);
//			List list = query.list();
//			
//			mav.addObject("list", list);
//		}catch(Exception e){
//			throw e;
//		}finally{
//			session.close();
//		}
		mav.addObject("list", this.officeCommonManager.getTableRecords(sql, AutoInfo.class));
		return mav;
	}
	
	/**
	 * 办公设备列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView asset(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(assetView);
		User user = CurrentUser.get();
		String countSql = "select count(*)  as myTotalCount from M_Asset_Info where del_flag=0 and asset_mge="+user.getId();
		
//		if(!Pagination.isNeedCount().booleanValue()){
			int size = this.officeCommonManager.getCount(countSql);
			Pagination.setRowCount(size);
//		}
		
		String sql = "select * from M_Asset_Info where del_flag=0 and asset_mge="+user.getId();
//		Session session = this.officeCommonManager.getCurSession();
//		try{
//			SQLQuery query = session.createSQLQuery(sql);
//			query.setFirstResult(Pagination.getFirstResult());
//			query.setMaxResults(Pagination.getMaxResults());
//			query.addEntity(MAssetInfo.class);
//			List list = query.list();
//			
//			mav.addObject("list", list);
//		}catch(Exception e){
//			throw e;
//		}finally{
//			
//			session.close();
//		}
		mav.addObject("list", this.officeCommonManager.getTableRecords(sql, MAssetInfo.class));
		return mav;
	}
	
	
	/**
	 * 办公用品列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView stock(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(stockView);
		User user = CurrentUser.get();
		String countSql = "select count(*)  as myTotalCount from m_stock_info where del_flag=0 and stock_res="+user.getId();
		
//		if(!Pagination.isNeedCount().booleanValue()){
			int size = this.officeCommonManager.getCount(countSql);
			Pagination.setRowCount(size);
//		}
		
		String sql = "select * from m_stock_info where del_flag=0 and stock_res="+user.getId();
		
//		Session session = this.officeCommonManager.getCurSession();
//		try{
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setFirstResult(Pagination.getFirstResult());
//		query.setMaxResults(Pagination.getMaxResults());
//		query.addEntity(StockInfo.class);
//		List list = query.list();
//		
//		mav.addObject("list", list);
//		}catch(Exception e){
//			throw e;
//		}finally{
//			session.close();
//		}
		mav.addObject("list", this.officeCommonManager.getTableRecords(sql, StockInfo.class));
		return mav;
	}
	
	/**
	 * 图书列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView book(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(bookView);
		User user = CurrentUser.get();
		String countSql = "select count(*)  as myTotalCount from M_book_Info where del_flag=0 and book_mge="+user.getId();
		
//		if(!Pagination.isNeedCount().booleanValue()){
			int size = this.officeCommonManager.getCount(countSql);
			Pagination.setRowCount(size);
//		}
		
		String sql = "select * from M_book_Info where del_flag=0 and book_mge="+user.getId();
		
//		Session session = this.officeCommonManager.getCurSession();
//		try{
//		SQLQuery query = session.createSQLQuery(sql);
//		
//		query.setFirstResult(Pagination.getFirstResult());
//		query.setMaxResults(Pagination.getMaxResults());
//		query.addEntity(MBookInfo.class);
//		List list = query.list();
		
		mav.addObject("list", this.officeCommonManager.getTableRecords(sql, MBookInfo.class));
//		}catch(Exception e){
//			throw e;
//		}finally{
//			session.close();
//		}
		return mav;
	}
}
