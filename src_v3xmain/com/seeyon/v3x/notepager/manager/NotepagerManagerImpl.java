package com.seeyon.v3x.notepager.manager;

import java.sql.Timestamp;

import com.seeyon.v3x.notepager.dao.NotepageDao;
import com.seeyon.v3x.notepager.domain.Notepage;
 
public class NotepagerManagerImpl implements NotepagerManager {

	private NotepageDao notepagerdao;

	public NotepageDao getNotepagerdao() {
		return notepagerdao;
	}

	public void setNotepagerdao(NotepageDao notepagerdao) {
		this.notepagerdao = notepagerdao;
	}

	public Long autoSave(String notepagerId, Long memberId, String FormContent)
			throws Exception {
        Notepage note = null;
		if (notepagerId == null || "".equals(notepagerId)) {// 新建
            note = new Notepage();
            note.setIdIfNew();
            note.setMemberId(memberId);
            note.setContent(FormContent);
            note.setCreateDate(new Timestamp(System.currentTimeMillis()));
            note.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			notepagerdao.save(note);
		}
		else {
            Long id = Long.parseLong(notepagerId);
            note = this.notepagerdao.get(id);
            note.setContent(FormContent);
            note.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			notepagerdao.update(note);
		}
		return note.getId();
	}

	public Notepage get(Long memberId) throws Exception {
		return notepagerdao.getNotepageByMemberID(memberId);

	}

}
