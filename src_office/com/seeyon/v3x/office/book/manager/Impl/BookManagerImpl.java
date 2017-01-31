package com.seeyon.v3x.office.book.manager.Impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.book.dao.BookApplyInfoDAO;
import com.seeyon.v3x.office.book.dao.BookDepartInfoDAO;
import com.seeyon.v3x.office.book.dao.BookInfoDAO;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.book.domain.TBookApplyinfo;
import com.seeyon.v3x.office.book.domain.TBookDepartinfo;
import com.seeyon.v3x.office.book.manager.BookManager;
import com.seeyon.v3x.office.book.util.Constants;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.myapply.dao.ApplyListDAO;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.util.Datetimes;

public class BookManagerImpl implements BookManager {
	private static final Log log = LogFactory.getLog(BookManagerImpl.class);
	private BookInfoDAO bookInfoDAO;
	private BookApplyInfoDAO bookApplyInfoDAO;
	private BookDepartInfoDAO bookDepartInfoDAO;
	private ApplyListDAO applyListDAO;
	private UserMessageManager userMessageManager;
	
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setApplyListDAO(ApplyListDAO applyListDAO) {
		this.applyListDAO = applyListDAO;
	}
	public void setBookApplyInfoDAO(BookApplyInfoDAO bookApplyInfoDAO) {
		this.bookApplyInfoDAO = bookApplyInfoDAO;
	}
	public void setBookDepartInfoDAO(BookDepartInfoDAO bookDepartInfoDAO) {
		this.bookDepartInfoDAO = bookDepartInfoDAO;
	}
	public void setBookInfoDAO(BookInfoDAO bookInfoDAO) {
		this.bookInfoDAO = bookInfoDAO;
	}
	public BookInfoDAO getBookInfoDAO() {
		return bookInfoDAO;
	}
	public ApplyListDAO getApplyListDAO() {
		return applyListDAO;
	}
	public BookDepartInfoDAO getBookDepartInfoDAO() {
		return bookDepartInfoDAO;
	}
	public BookApplyInfoDAO getBookApplyInfoDAO() {
		return bookApplyInfoDAO;
	}
	/**/
	
	public void save(MBookInfo mBookInfo) {
		this.bookInfoDAO.save(mBookInfo);
	}
	
	public void save(TBookDepartinfo tBookDepartInfo){
		this.bookDepartInfoDAO.save(tBookDepartInfo);
	}
	
	public void update(MBookInfo mBookInfo){
		this.bookInfoDAO.update(mBookInfo);
	}
	
	public void update(TBookDepartinfo tBookDepartInfo){
		this.bookDepartInfoDAO.update(tBookDepartInfo);
	}
	
	public void update(TBookApplyinfo bookApply){
		this.bookApplyInfoDAO.update(bookApply);
	}

	public SQLQuery find(String sql,Map m) {
		SQLQuery query = this.bookInfoDAO.find(sql,m);
		return query;
	}

	public int getCount(String sql,Map m) {
		return this.bookInfoDAO.getCount(sql,m);
	}
	
	public SQLQuery findApply(String sql){
		SQLQuery query = this.bookApplyInfoDAO.find(sql);
		return query;
	}

	public int getApplyCount(String sql){
		return this.bookApplyInfoDAO.getCount(sql);
	}

	public MBookInfo getById(long id) {
		return this.bookInfoDAO.load(id);
	}
	
	public TBookApplyinfo getApplyinfoById(long id){
		return this.bookApplyInfoDAO.load(id);
	}
	
	public TBookDepartinfo getDepartinfoById(long id){
		return this.bookDepartInfoDAO.load(id);
					
	}
	
	public void createApply(long bookId, long userId, long depId, String apply_count, String book_start, String book_end){
		Long l_Apply_count = null;
		if(apply_count != null && apply_count.length() > 0){
			l_Apply_count = new Long(Long.parseLong(apply_count));
		}
		if(book_start == null || book_start.length() == 0){
			book_start = null;
		}
		if(book_end == null || book_end.length() == 0){
			book_end = null;
		}
		int count = 0;
		try{
			count = this.applyListDAO.getCount("Select max(apply_id) as "+Constants.Total_Count_Field+" From t_applylist",null);
			if(count == 0){
				count = 40000000;
			}else{
				count = count + 1;
			}
		}catch(Exception ex){
			count = 40000000;
		}
		TApplylist applyList = new TApplylist();
		//applyList.setApplyId(UUIDLong.longUUID());
		applyList.setApplyId(new Long(count));
		applyList.setApplyUsername(new Long(userId));
		applyList.setApplyDepId(new Long(depId));
        //edit date by bianteng start
		applyList.setApplyDate(new Date());
        //edit date by bianteng end
		applyList.setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
		applyList.setApplyType(new Integer(com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Book));
		MBookInfo bookInfo = this.getById(bookId);
		if(bookInfo.getBookMge() != null){
			applyList.setApplyMge(bookInfo.getBookMge());
		}
		applyList.setDelFlag(new Integer(com.seeyon.v3x.office.book.util.Constants.Del_Flag_Normal));
		TBookApplyinfo bookApplyInfo = new TBookApplyinfo();
		bookApplyInfo.setApplyId(applyList.getApplyId());
		bookApplyInfo.setBookId(bookId);
		bookApplyInfo.setApplyCount(l_Apply_count);
		if(book_start != null && book_start.length() > 0){
			bookApplyInfo.setBookStart(com.seeyon.v3x.office.book.util.str.strToDate(book_start));
		}
		if(book_end != null && book_end.length() > 0){
			bookApplyInfo.setBookEnd(com.seeyon.v3x.office.book.util.str.strToDate(book_end));
		}
		bookApplyInfo.setDelFlag(new Integer(com.seeyon.v3x.office.book.util.Constants.Del_Flag_Normal));
		this.applyListDAO.save(applyList);
		this.bookApplyInfoDAO.save(bookApplyInfo);
		
		// 审批人首页增加待办
		OfficeHelper.addPendingAffair(bookInfo.getBookName(), applyList, ApplicationSubCategoryEnum.office_book);
		
		// 给管理员发送消息
		try {
			userMessageManager.sendSystemMessage(MessageContent.get("office.book.apply", bookInfo.getBookName(), CurrentUser.get().getName()), 
					ApplicationCategoryEnum.office, userId, 
					MessageReceiver.get(applyList.getApplyId(), bookInfo.getBookMge(), "message.link.office.book", String.valueOf(applyList.getApplyId())));
		} catch (MessageException e) {
			log.error("图书资料申请失败：", e);
		}
	}
	
	public int getMonthCount(long userId) {
		//"yyyy-MM-dd"
		java.util.Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1*(c.get(Calendar.DAY_OF_MONTH)));
		/*
		//String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist where del_flag = " + Constants.Del_Flag_Normal + " and apply_username="+userId;
		//sql += " and apply_type=4 and apply_state > 1 and apply_date > '" + df.format(c.getTime()) + "'";
//		String sql = "Select * from t_applylist where del_flag = " + Constants.Del_Flag_Normal + " and apply_username="+userId;
      //by Yongzhang
        String sql = "Select * from t_applylist where  apply_username="+userId;
		sql += " and apply_type=4 and apply_state > 1";
		SQLQuery query = this.findApply(sql);
		query.addEntity(TApplylist.class);
		List list = query.list();
		int count = 0;
		if(list != null){
			for(int i = 0; i < list.size(); i++){
				TApplylist apply = (TApplylist)list.get(i);
				if(apply.getApplyDate() != null && apply.getApplyDate().after(c.getTime())){
					count++;
				}
			}
		}*/
		return this.bookInfoDAO.dateCount(userId, c.getTime());
	}

	public int getTotalCount(long userId) {
        //修改我的申请2008-04-30 by Yongzhang
        //		String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist where del_flag = " + Constants.Del_Flag_Normal + " and apply_username="+userId;
       /* String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist where  apply_username="+userId;
        sql += " and apply_type=4 and apply_state > 1";*/
		return this.bookInfoDAO.totalCount(userId);
	}

	public int getTotalNoBackCount(long userId) {	
		//String sql ="Select (sum(b.book_departcount)-sum(b.book_backcount)) as "+Constants.Total_Count_Field + " from t_applylist t,t_book_departinfo b where t.del_flag = " + Constants.Del_Flag_Normal + " and t.apply_username="+userId;
		//sql += " and t.apply_type=4 and t.apply_state > 1 and t.apply_id =b.apply_id ";   
		//return this.getCount(sql);
		//修改我的申请2008-04-30 and 2008-05-14 by Yongzhang
//	    String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist where del_flag = " + Constants.Del_Flag_Normal + " and apply_username="+userId;
        /*String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist t,t_book_departinfo b where  t.apply_id=b.apply_id  and b.Book_backtime is null and b.Book_departtime is not null and t.apply_username="+userId;
        sql += " and t.apply_type=4 and t.apply_state > 1 and t.apply_state < 5";*/
		return  this.bookInfoDAO.noBackCount(userId);
	}

	public int getWeekCount(long userId) {
		//"yyyy-MM-dd"
		java.util.Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1*(c.get(Calendar.DAY_OF_WEEK) - 1));
	/*	//String sql = "Select count(*) as "+Constants.Total_Count_Field + " from t_applylist where del_flag = " + Constants.Del_Flag_Normal + " and apply_username="+userId;
		//sql += " and apply_type=4 and apply_state > 1 and apply_date > '" + df.format(c.getTime()) + "'";
		String sql = "Select * from t_applylist where  apply_username="+userId;
		sql += " and apply_type=4 and apply_state > 1";
		SQLQuery query = this.findApply(sql);
		query.addEntity(TApplylist.class);
		List list = query.list();
		int count = 0;
		if(list != null){
			for(int i = 0; i < list.size(); i++){
				TApplylist apply = (TApplylist)list.get(i);
				if(apply.getApplyDate() != null && apply.getApplyDate().after(c.getTime())){
					count++;
				}
			}
		}*/
		return bookInfoDAO.dateCount(userId, c.getTime());
	}
	
	public boolean hasSameBookName(String name) {
    	 List list = bookInfoDAO.findBookByName(name);
    	 if(list == null || list.isEmpty()){
    		 return false;
    	 }
		return true;
	}
	public List getBookSummayByDep(boolean needPage) {
		return bookInfoDAO.getBookSummayByDep(needPage);
	}
	public List getBookSummayByMember(boolean needPage) {
		return bookInfoDAO.getBookSummayByMember(needPage);
	}
    

	public List findBookField()
    {
        return bookInfoDAO.findBookField();
    }
    /**
     * 管理员管理的图书移交功能
     * @param
     *       以前的管理员
     * @param
     *       新替换后的管理员
     */
    public void updateBookMangerBatch(long oldManager, long newManager,User user)
    {
    	this.updateBookMangerBatch(oldManager, newManager, user, true);
    }
    
    @Override
	public void updateBookMangerBatch(long oldManager, long newManager,
			User user, boolean fromFlag) {
    	if (fromFlag) {
    		 bookInfoDAO.updateBookMangerBatch(oldManager, newManager,user);
    	     bookInfoDAO.audiTransfer(oldManager, newManager);
		}else {
			bookInfoDAO.updateBookMangerBatch(oldManager, newManager,user,fromFlag);
		}
       
    }
    
    public List getBookAppList(String field, String fieldValue, List managerId) {
    	
    	return bookInfoDAO.listBookApp(field, fieldValue, managerId);
    }
    public List getBookPermList(String field, String fieldValue, Long adminId) {
    	
    	return bookApplyInfoDAO.listBookPerm(field, fieldValue, adminId);
    }
    
    public List getBookRegList(String field, String fieldValue, Long managerId) {
    	return bookInfoDAO.listBookReg(field, fieldValue, managerId);
    }
    
    public List getBookStorageList(String field, String fieldValue, List applyId) {
    	return bookApplyInfoDAO.listBookStroage(field, fieldValue, applyId);
    }
	@Override
	public List getBookBackListByUserId(String userid) {
		List list= bookDepartInfoDAO.getBookBackListByUserId(userid);
		return list;
	}
	
    
}