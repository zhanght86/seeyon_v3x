package com.seeyon.v3x.bulletin.manager;

import java.util.HashSet;
import java.util.Set;

import com.seeyon.v3x.bulletin.dao.BulTypeManagersDao;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.domain.BulTypeManagers;

/**
 * 主要对公告类型与公告管理员、公告发起员的关联进行操作
 * @author wolf
 *
 */
public class BulTypeManagersManagerImpl extends BaseBulletinManager implements BulTypeManagersManager {
	private BulTypeManagersDao bulTypeManagersDao;

	public BulTypeManagersDao getBulTypeManagersDao() {
		return bulTypeManagersDao;
	}

	public void setBulTypeManagersDao(BulTypeManagersDao bulTypeManagersDao) {
		this.bulTypeManagersDao = bulTypeManagersDao;
	}
	
	/**
	 * 前台管理授权--保存人员
	 */
	public void saveAclByType(BulType type, String[][] userIds, String extFlag){
		if(type == null || userIds == null || extFlag == null)
			return;
		
		this.deleteByType(type, extFlag);
		
		Set<BulTypeManagers> set=type.getBulTypeManagers();
		if(set==null){
			set=new HashSet<BulTypeManagers>();
		}else{
			Set<BulTypeManagers> set2 = new HashSet<BulTypeManagers>();
			set2.addAll(set);
			for(BulTypeManagers btm : set2){
				if(extFlag.equals(btm.getExt1()))
					set.remove(btm);
			}
		}			
		
		for(int i=0;i<userIds.length;i++) {
			BulTypeManagers tm=new BulTypeManagers();
			tm.setIdIfNew();
			tm.setType(type);
			tm.setManagerId(Long.valueOf(userIds[i][1]));
			tm.setExt1(extFlag);
			tm.setExt2(userIds[i][0]);
			tm.setOrderNum(Integer.valueOf(i));
			bulTypeManagersDao.save(tm);
			set.add(tm);
		}
		type.setBulTypeManagers(set);
	}
	
	/**
	 * 后台板块建立保存
	 */
	public void saveAclByTypeManager(BulType type, String[] userIds, String extFlag){
		if(type == null || extFlag == null)
			return;
		
		this.deleteByType(type, extFlag);
		
		Set<BulTypeManagers> set=type.getBulTypeManagers();
		if(set==null){
			set=new HashSet<BulTypeManagers>();
		}else{
			Set<BulTypeManagers> set2 = new HashSet<BulTypeManagers>();
			set2.addAll(set);
			for(BulTypeManagers btm : set2){
				if(extFlag.equals(btm.getExt1()))
					set.remove(btm);
			}
		}			
		
		for(int i=0;i<userIds.length;i++) {
			BulTypeManagers tm=new BulTypeManagers();
			tm.setIdIfNew();
			tm.setType(type);
			tm.setManagerId(Long.valueOf(userIds[i]));
			tm.setOrderNum(Integer.valueOf(i));
			tm.setExt1(extFlag);
			tm.setExt2("Member");
			bulTypeManagersDao.save(tm);
			set.add(tm);
		}
		type.setBulTypeManagers(set);
	}
	
	private void deleteByType(BulType type, String extFlag){
		if(type == null || extFlag == null)
			return;		
		String hql="delete from BulTypeManagers as tm where tm.type.id=? and tm.ext1=?";
		Object[] values=new Object[]{type.getId(),extFlag};
		this.bulTypeManagersDao.bulkUpdate(hql,null, values);
	}
}