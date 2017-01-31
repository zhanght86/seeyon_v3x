package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.exception.DocException;

public interface DocSpaceManager {
	/**
	 * 为个人文档库分配空间大小
	 * @param docLibId			文档库ID
	 * @param userId			用户ID
	 * @param totalSize			个人文档空间大小（默认为100MB）
	 * @param mailSize 个人邮件空间大小（缺省为100M）
	 */
	public DocStorageSpace addDocSpace(long userId,long totalSize,long mailSize);
	
	/**
	 * 修改用户个人初始空间大小
	 * @param spaceId			
	 * @param totalSize		空间大小（分配的文档空间不能小于已使用的空间大小）
	 * @param mailTotalSize 邮件空间大小
	 * @param blogTotalSize 博客空间大小
	 */
	public void modifyDocSpace(long spaceId,long totalSize,long mailTotalSize,
			long blogTotalSize)throws DocException;
	
	/**
	 * 设置用户个人邮件空间的大小
	 * @param userId
	 * @param mailTotalSize			邮件空间大小,单位为MB 不能小于5MB
	 * @throws DocException
	 */
	public void setMailSpace(long userId,String mailTotalSize)throws DocException;
	
	/**
	 * 增加已经使用的邮件空间
	 * @param userId
	 * @param size				以字节为单位的值  例如：1 MB  size=1024*1024	
	 */
	public void addMailSpaceSize(long userId,long size)throws DocException;
	
	/**
	 * 减少已经使用的邮件空间
	 * @param userId
	 * @param size
	 */
	public void deleteMailSpaceSize(long userId,long size)throws DocException;
	
	/**
	 * 增加已经使用的博客空间
	 * @param userId
	 * @param size				以字节为单位的值  例如：1 MB  size=1024*1024	
	 */
	public void addBlogSpaceSize(long userId,long size)throws DocException;
	
	/**
	 * 减少已经使用的博客空间
	 * @param userId
	 * @param size
	 */
	public void deleteBlogSpaceSize(long userId,long size)throws DocException;
	
	/**
	 * 根据ID获取一个空间对象
	 * @param docSpaceId
	 * @return
	 */
	public DocStorageSpace getDocSpaceById(long docSpaceId);
	
	/**
	 * 通过用户ID获取文档库空间对象
	 * @param userId			用户ID
	 * @return					文档库空间对象
	 */
	public DocStorageSpace getDocSpaceByUserId(long userId);
	
	/**
	 * 增加已经使用的文档库空间
	 * @param userId			用户ID
	 * @param size				增加的大小（不能超过预置空间大小）
	 */
	public void addUsedSpaceSize(long userId, long size)throws DocException;
	
	/**
	 * 减少已经使用了的文档库空间
	 * @param userId			用户ID
	 * @param size				减少的大小（不能大于已用空间大小）
	 */
	public void subUsedSpaceSize(long userId, long size);
	
	/**
	 * 查看所有用户的文档库空间使用情况
	 * @return	
	 */
	public List<DocStorageSpace> getDocStorageSpaces();
	
	public List<DocStorageSpace> getDocStorageSpacesByAccount(final long accountId);
	
	/**
	 * 根据个人空间使用状态查询
	 * @param status			使用的状态
	 * @return
	 */
	public List<DocStorageSpace> getDocSpaceByStatus(int status);
	
	public List<DocStorageSpace> getStorageSpacesByDeptId(long deptId);
	
	/**
	 * 为用户分配博客空间大小。
	 * @param userId 用户id
	 * @param size 博客空间大小 
	 */
	public void assignBlogSpace(long userId, long size) throws DocException;
	
	public String judgeBlogSpace(Long attSizeSum);
	
}
