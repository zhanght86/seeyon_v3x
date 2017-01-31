package com.seeyon.v3x.bulletin.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulPublishScope;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.Strings;

public class BulPublishScopeDao extends BaseHibernateDao<BulPublishScope> {

	/**
	 * 保存公告发布范围，如果是修改，则先删除旧的公告发布范围，再生成新的发布范围
	 * @param data		公告
	 * @param isNew		是否为新建时的保存
	 */
	public void savePublishScope(BulData data, boolean isNew){
		if(!isNew){
			this.delete(new Object[][]{{"bulDataId", data.getId()}});
		}		
		List<BulPublishScope> scopes = new ArrayList<BulPublishScope>();		
		String scopeStr = data.getPublishScope();
		String[][] eles = Strings.getSelectPeopleElements(scopeStr);
		if(eles != null && eles.length > 0){
			for(int i = 0; i < eles.length; i++) {
				BulPublishScope scope = new BulPublishScope(data.getId(), NumberUtils.toLong(eles[i][1]), eles[i][0]);
				scopes.add(scope);
			}				
			this.savePatchAll(scopes);
		}
	}

}
