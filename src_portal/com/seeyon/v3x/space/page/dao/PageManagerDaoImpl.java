/**
 * 
 */
package com.seeyon.v3x.space.page.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseDao;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpacePage;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-19
 */
public class PageManagerDaoImpl extends BaseDao<SpacePage> implements PageManagerDao {

	public void deleteFragment(long id) {
        String s = "delete from " + Fragment.class.getName() + " a where id=?";
        super.bulkUpdate(s, null, id);
	}

	public void updatePage(SpacePage page) {
		super.update(page);
	}

	public void updateFragment(Fragment fragment) {
		super.update(fragment);
	}
	
	public void saveFragment(Fragment fragment) {
		super.save(fragment);
	}
	
	public void savePage(SpacePage page){
		super.save(page);
	}

	public List<SpacePage> getAllPage() {
		return super.find("from " + SpacePage.class.getName(), -1, -1, null);
	}
	
	public List<Fragment> getAllFragment() {
		return super.find("from " + Fragment.class.getName(), -1, -1, null);
	}

	public void deletePage(SpacePage page) {
		super.delete(page.getId());
	}

}
