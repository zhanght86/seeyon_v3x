package com.seeyon.v3x.office.book.dao;

import java.util.*;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.office.book.domain.*;

public interface BookInfoDAO {
	
	public void save(MBookInfo mBookInfo);
	
	public void update(MBookInfo mBookInfo);
	
	public SQLQuery find(String sql,Map m);

	public int getCount(String sql,Map m);
	
	public MBookInfo load(long id);
	public int selectLendBook(String applyid);
    /**
     * 查询出所有的图书资料信息
     * 
     * @param field
     *            图书资料是2，图书是1
     * @return
     */
    public List findBookField();
    /**
     * 管理员管理的图书移交功能
     * @param
     *       以前的管理员
     * @param
     *       新替换后的管理员
     */
    public void updateBookMangerBatch(long oldManager,long newManager,User user);
    public void updateBookMangerBatch(long oldManager,long newManager,User user,boolean fromFlag);
    
    /**
     * 管理员管理的图书移交功能
     * @param
     *       以前的管理员
     * @param
     *       新替换后的管理员
     */
    public void audiTransfer(final long oldManager, final long newManager);
    

	/**
	 * 得到图书资料登记列表
	 * @param field
	 * @param fieldValue
	 * @param managerId
	 * @return
	 */
	public List listBookReg(String field,String fieldValue,Long managerId);
	
	/**
	 * 得到可以申请的图书资料列表 
	 * @param field
	 * @param fieldValue
	 * @param applyId
	 * @return
	 */
	public List listBookApp(String field,String fieldValue,List applyId);
	
	/**
	 * 得到一个人 thisDate之前的申请次数
	 * @param userId
	 * @param thisWeeb
	 * @return
	 */
	public Integer dateCount(Long userId,Date thisDate);
	
	/**
	 * 得到 userId 总共申请次数
	 * @param userId
	 * @return
	 */
	public Integer totalCount(Long userId);
	
	/**
	 * 得到userId 没有归还的次数
	 * @param userId
	 * @return
	 */
	public Integer noBackCount(Long userId);
	
	 /**
     * 按部门统计图书资料
     * @return
     */
	public List getBookSummayByDep(boolean needPage);
	/**
     * 按人员统计图书资料
     * @return
     */
    public List getBookSummayByMember(boolean needPage);
    /**
     *  根据名称查找图书
     * @param name
     * @return
     */
    public List findBookByName(String name);
}
