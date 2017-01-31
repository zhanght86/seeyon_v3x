package com.seeyon.v3x.space.page.dao;

import java.util.List;

import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpacePage;

public interface PageManagerDao {
	
	public List<SpacePage> getAllPage();
	
	public List<Fragment> getAllFragment();
	
	public void updatePage(SpacePage page);

	public void deleteFragment(long id);
	
	public void deletePage(SpacePage page);
	
	public void saveFragment(Fragment fragment);
	
	public void updateFragment(Fragment fragment);
	
	public void savePage(SpacePage page);

}