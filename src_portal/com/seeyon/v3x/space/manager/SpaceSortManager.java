package com.seeyon.v3x.space.manager;

import com.seeyon.v3x.space.domain.SpaceSort;

public interface SpaceSortManager {
	public SpaceSort getSpaceSort(Long id);
	
	public SpaceSort getSpaceSort(Long memberId,Long spaceId);
	
	public void updateSpaceSort(SpaceSort sort);
}
