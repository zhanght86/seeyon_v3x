package com.seeyon.v3x.space.page;

import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpacePage;

public interface PageManager {

	/**
	 * 复制Page：page、fragment(layout、portlet)
	 * @param srcPagePath
	 * @param destPagePath
	 * @return
	 */
	public SpacePage copyPage(String srcPagePath, String destPagePath);

	public SpacePage getPage(String pagePath);

	public void removePage(String pagePath);
	
	public Fragment getFragmentById(SpacePage page, long fragmentId);
	
	/**
	 * 修改空间配置，Page不变，只更新layoutDecorator，因此要删除Fragment，然后重新创建
	 * @param page
	 * @param fragmentId
	 */
	public void removeFragment(long fragmentId);
	
	public void removeFragment(long fragmentId,String pagePath);
	
	public void updatePage(SpacePage page);
	
	public void updateFragment(Fragment fragment);
	
	public void save(Fragment fragment);
	/**
	 * 更新空间布局的缓存信息	
	 * @param pagePath
	 * @return
	 */
	public void updatePage(String pagePath);
	public void updatePageByCache(String pagePath);
}