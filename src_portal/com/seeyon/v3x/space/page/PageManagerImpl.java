/**
 * 
 */
package com.seeyon.v3x.space.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpacePage;
import com.seeyon.v3x.space.page.dao.PageManagerDao;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-19
 */
public class PageManagerImpl extends BaseHibernateDao implements PageManager {
	private static final Log log = LogFactory.getLog(PageManager.class);
	
	private final CacheAccessable cacheFactory = CacheFactory.getInstance(PageManager.class);
	
	/*
	 * key: pagePath
	 */
	private final CacheMap<String, SpacePage> pageCache = cacheFactory.createMap("Page");
	
	private PageManagerDao pageManagerDao;
	
	public PageManagerImpl(){
		
	}
	
	public void setPageManagerDao(PageManagerDao pageManagerDao) {
		this.pageManagerDao = pageManagerDao;
	}
	
	public void init(){
		List<SpacePage> pages = this.pageManagerDao.getAllPage();
		List<Fragment> fragments = this.pageManagerDao.getAllFragment();
		
		Map<Long, Fragment> tempFragmentId2Fragment = new HashMap<Long, Fragment>();
		for (Fragment fragment : fragments) {
			tempFragmentId2Fragment.put(fragment.getId(), fragment);
		}
		
		Map<Long, SpacePage> tempPageId2Page = new HashMap<Long, SpacePage>();
		Map<String, SpacePage> tempPagePath2Page = new HashMap<String, SpacePage>();
		for (SpacePage page : pages) {
			tempPageId2Page.put(page.getId(), page);
			tempPagePath2Page.put(page.getPath(), page);
		}
		
		for (Fragment fragment : fragments) {
			Long pageId = fragment.getPageId();
			Long parentId = fragment.getParentId();
			if(pageId != null){ //layout fragment
				SpacePage page = tempPageId2Page.get(pageId);
				if(page == null){
					log.warn("Fragement[" + fragment.getId() + "]的Page[" + pageId + "]不存在");
					continue;
				}
				
				page.setRootFragment(fragment);
			}
			else if(parentId != null){ //portlet fragment
				Fragment rootFragment = tempFragmentId2Fragment.get(parentId);
				if(rootFragment == null){
					log.warn("Fragement[" + fragment.getId() + "]的Parent[" + parentId + "]不存在");
					continue;
				}
				
				rootFragment.getChildFragments().add(fragment);
			}
		}
		this.pageCache.putAll(tempPagePath2Page);		
		log.info("加载Portal Page信息完成，共" + pageCache.size());
	}
	@SuppressWarnings("unchecked")
	public void updatePage(String pagePath){
		SpacePage page = this.pageCache.get(pagePath);
		Fragment frag = page.getRootFragment();
		frag.getChildFragments().clear();
		if(frag!=null){
			long parent = frag.getId();
			String sql =  "from Fragment where parentId = ?";
			//获取更新后的fragments数据
			List<Fragment> frags = super.find(sql,parent);
			//更新缓存数据
			frag.setChildFragments(frags);
		}
	}
	public SpacePage copyPage(String srcPagePath, String destPagePath){
		SpacePage srcPage = this.pageCache.get(srcPagePath);
		if(srcPage == null){
			return null;
		}
		
		SpacePage destPage = this.pageCache.get(destPagePath);
		if(destPage != null){
			return destPage;
		}
		
		destPage = new SpacePage(srcPage);
		destPage.setIdIfNew();
		destPage.setPath(destPagePath);
		
		this.pageManagerDao.savePage(destPage);
		
		Fragment srcRoot = srcPage.getRootFragment(); 
		if(srcRoot != null){
			Fragment destRoot = new Fragment(srcRoot);
			destRoot.setIdIfNew();
			destRoot.setPageId(destPage.getId());
			
			this.pageManagerDao.saveFragment(destRoot);
			destPage.setRootFragment(destRoot);
			
			List<Fragment> fragments = srcRoot.getChildFragments();
			if(fragments != null && !fragments.isEmpty()){
				for (Fragment fragment : fragments) {
					Fragment destChild = new Fragment(fragment);
					destChild.setIdIfNew();
					destChild.setParentId(destRoot.getId());
					
					this.pageManagerDao.saveFragment(destChild);
					destRoot.getChildFragments().add(destChild);
				}
			}
		}
		
		this.pageCache.put(destPagePath, destPage);
		
		return destPage;
	}
	
	public SpacePage getPage(String pagePath){
		return this.pageCache.get(pagePath);
	}
	
	public void removePage(String pagePath){
		SpacePage page = this.getPage(pagePath);

		if(page == null){
			return;
		}
		
		Fragment root = page.getRootFragment(); 
		if(root != null){
			List<Fragment> fragments = root.getChildFragments();
			if(fragments != null && !fragments.isEmpty()){
				for (Fragment fragment : fragments) {
					this.pageManagerDao.deleteFragment(fragment.getId());
				}
			}
			
			this.pageManagerDao.deleteFragment(page.getRootFragment().getId());
		}
		
		this.pageManagerDao.deletePage(page);
		this.pageCache.remove(page.getPath());
	}
	
	public void removeFragment(long fragmentId){
		this.pageManagerDao.deleteFragment(fragmentId);
	}
	
	public void removeFragment(long fragmentId,String pagePath){
		removeFragment(fragmentId);
		this.pageCache.notifyUpdate(pagePath);
	}
	
	public Fragment getFragmentById(SpacePage page, long fragmentId) {
		List<Fragment> fragments = page.getRootFragment().getChildFragments();
		for (Fragment fragment : fragments) {
			if(fragmentId == fragment.getId()){
				return fragment;
			}
		}
		
		return null;
	}
	
	public void save(Fragment fragment){
		this.pageManagerDao.saveFragment(fragment);
	}

	public void updateFragment(Fragment fragment) {
		this.pageManagerDao.updateFragment(fragment);
	}

	public void updatePage(SpacePage page){
		this.pageManagerDao.updatePage(page);
		this.pageCache.notifyUpdate(page.getPath());
	}
	public void updatePageByCache(String pagePath){
		this.pageCache.notifyUpdate(pagePath);
	}
}
