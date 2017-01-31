package com.seeyon.v3x.office.book.manager;

import java.util.*;

import org.hibernate.SQLQuery;
import org.hibernate.type.Type;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.office.book.domain.*;

public interface BookManager
{
	public void save(MBookInfo mBookInfo);
	
	public void save(TBookDepartinfo tBookDepartInfo);
	
	public void update(MBookInfo mBookInfo);
	
	public void update(TBookDepartinfo tBookDepartInfo);
	
	public void update(TBookApplyinfo bookApply);
	
	public SQLQuery find(String sql,Map map);

	public int getCount(String sql,Map map);
	
	public SQLQuery findApply(String sql);

	public int getApplyCount(String sql);
	
	public MBookInfo getById(long id);
	
	public TBookApplyinfo getApplyinfoById(long id);
	
	public TBookDepartinfo getDepartinfoById(long id);
	
	public void createApply(long bookId, long userId, long depId, String apply_count, String book_start, String book_end);
	
// add by liusg 2007-10-11
	
	public int getWeekCount(long userId);
	
	public int getMonthCount(long userId);
	
	public int getTotalCount(long userId);
	
	public int getTotalNoBackCount(long userId);
	
	 /**
     * 按部门统计图书资料
     * @return
     */
	public List getBookSummayByDep(boolean needPage);
    
    public List findBookField();
    /**
     * 管理员管理的图书移交功能
     *
     */
    public void updateBookMangerBatch(long oldManager,long newManager,User user);
    public void updateBookMangerBatch(long oldManager,long newManager,User user,boolean fromFlag);
    
    /**
     * 得到图书资料登记列表
     * @param field
     * @param fiedValue
     * @param managerId
     * @return
     */
    public List getBookRegList(String field,String fiedValue,Long managerId);
    /**
     * 得到可以申请的图书资料列表
     * @param field
     * @param fiedValue
     * @param managerId 所有管理员的列表
     * @return
     */
    public List getBookAppList(String field,String fiedValue,List managerId);
    
    /**
     * 得到图书申请的列表
     * @param field
     * @param fiedValue
     * @param applyId 申请编号
     * @return
     */
    public List getBookPermList(String field,String fiedValue,Long adminId);
    
    /**
     * 得到图书借出、归还列表
     * @param field
     * @param fiedValue
     * @param applyId 申请编号
     * @return
     */
    public List getBookStorageList(String field,String fiedValue,List applyId);
    /**
     * 按人员统计图书资料
     * @return
     */
    public List getBookSummayByMember(boolean needPage);
    /**
     * 判断是否有同名的图书资料
     * @param name
     * @return boolean
     */
    public boolean hasSameBookName(String name);
    
    /**
     * 根据userid获得该用户还没有归还的图书资料信息列表
     * @param userid
     * @return
     */
	public List getBookBackListByUserId(String userid);
    
}
