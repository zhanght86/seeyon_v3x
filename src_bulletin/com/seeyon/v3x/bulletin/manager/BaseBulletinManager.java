package com.seeyon.v3x.bulletin.manager;

import com.seeyon.v3x.bulletin.util.BulletinUtils;

/**
 * 公告模块的Manager的基类，主要是为了增加一个工具类<code>BulletinUtils</code>
 * @author wolf
 *
 */
public class BaseBulletinManager {
	private BulletinUtils bulletinUtils;

	/**
	 * 获取BulletinUtils工具类
	 * @return
	 */
	public BulletinUtils getBulletinUtils() {
		return bulletinUtils;
	}

	public void setBulletinUtils(BulletinUtils bulletinUtils) {
		this.bulletinUtils = bulletinUtils;
	}
	
	
}
