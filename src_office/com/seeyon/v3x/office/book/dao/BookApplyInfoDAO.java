package com.seeyon.v3x.office.book.dao;

import java.util.List;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.office.book.domain.TBookApplyinfo;

public interface BookApplyInfoDAO {
	
	public void save(TBookApplyinfo tBookApplyInfo);
	
	public void update(TBookApplyinfo tBookApplyInfo);
	
	public SQLQuery find(String sql);

	public int getCount(String sql);
	
	public TBookApplyinfo load(long id);
	
	/**
	 * 得到图书申请的列表 
	 * @param field
	 * @param fieldValue
	 * @param applyId
	 * @return
	 */
	public List listBookPerm(String field,String fieldValue,Long adminId);
	
	/**
	 *  得到图书 借出归还列表
	 * @param field
	 * @param fieldValue
	 * @param applyId
	 * @return
	 */
	public List listBookStroage(String field,String fieldValue,List applyId);

	/**
	 * 得到管理员 管理的图书的申请
	 * @param ids
	 * @return
	 */
	public List listBookApplyByIds(Long ids);
}
