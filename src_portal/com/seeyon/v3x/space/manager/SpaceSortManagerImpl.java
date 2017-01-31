package com.seeyon.v3x.space.manager;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.space.domain.SpaceSort;

public class SpaceSortManagerImpl extends BaseHibernateDao<SpaceSort> implements SpaceSortManager{

	@Override
	public SpaceSort getSpaceSort(Long id) {
		return super.findUniqueBy("id", id);
	}

	@Override
	public SpaceSort getSpaceSort(Long memberId, Long spaceId) {
		SpaceSort sort = new SpaceSort();
		sort.setMemberId(memberId);
		sort.setSpacePath(String.valueOf(spaceId));
		List<SpaceSort> sortList = super.findByExample(sort);
		if(CollectionUtils.isNotEmpty(sortList)){
			return sortList.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void updateSpaceSort(SpaceSort sort) {
		super.update(sort);
	}

}
