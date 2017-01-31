package com.seeyon.v3x.meeting.manager.cap;

import java.util.List;

import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;
import com.seeyon.v3x.meeting.manager.MtContentTemplateManager;

public class MtContentTemplateManagerCAPImpl implements MtContentTemplateManagerCAP {

	private MtContentTemplateManager mtContentTemplateManager;

	public void setMtContentTemplateManager(MtContentTemplateManager mtContentTemplateManager) {
		this.mtContentTemplateManager = mtContentTemplateManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtContentTemplateCAP> findGroupTypeAll(String type) throws BusinessException {
		List<MtContentTemplate> list = mtContentTemplateManager.findGroupTypeAll(type);
		if (list == null) {
			return null;
		}
		return (List<MtContentTemplateCAP>) BeanUtils.converts(MtContentTemplateCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtContentTemplateCAP> findTypeAll(String type) throws BusinessException {
		List<MtContentTemplate> list = mtContentTemplateManager.findTypeAll(type);
		if (list == null) {
			return null;
		}
		return (List<MtContentTemplateCAP>) BeanUtils.converts(MtContentTemplateCAP.class, list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MtContentTemplateCAP> findTypeAllNoPage(String type) throws BusinessException {
		List<MtContentTemplate> list = mtContentTemplateManager.findTypeAllNoPage(type);
		if (list == null) {
			return null;
		}
		return (List<MtContentTemplateCAP>) BeanUtils.converts(MtContentTemplateCAP.class, list);
	}
	
	@Override
	public MtContentTemplateCAP getById(Long id) {
		MtContentTemplate mtContentTemplate = mtContentTemplateManager.getById(id);
		if (mtContentTemplate == null) {
			return null;
		}
		MtContentTemplateCAP mtContentTemplateCAP = new MtContentTemplateCAP();
		BeanUtils.convert(mtContentTemplateCAP, mtContentTemplate);
		return mtContentTemplateCAP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveAll(List<MtContentTemplateCAP> mcts) {
		List<MtContentTemplate> list = (List<MtContentTemplate>) BeanUtils.converts(MtContentTemplate.class, mcts);
		mtContentTemplateManager.saveAll(list);
	}
	
}