package com.seeyon.v3x.office.book.dao;

import java.util.*;

import org.hibernate.SQLQuery;
import com.seeyon.v3x.office.book.domain.*;

public interface BookDepartInfoDAO {
	
	public void save(TBookDepartinfo tBookDepartinfo);
	
	public void update(TBookDepartinfo tBookDepartinfo);
	
	public SQLQuery find(String sql);

	public int getCount(String sql);
	
	public TBookDepartinfo load(long id);

	/**
	 * 根据userid获得该用户还没有归还的图书资料信息列表
	 * @param userid
	 * @return
	 */
	public List getBookBackListByUserId(String userid);
}
