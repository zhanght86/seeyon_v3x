package com.seeyon.v3x.meeting.manager.cap;

import java.util.List;
import java.util.Map;

import com.seeyon.cap.meeting.domain.MtTemplateCAP;
import com.seeyon.cap.meeting.manager.MtTemplateManagerCAP;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.manager.MtTemplateManager;

public class MtTemplateManagerCAPImpl implements MtTemplateManagerCAP {

	private MtTemplateManager mtTemplateManager;

	public void setMtTemplateManager(MtTemplateManager mtTemplateManager) {
		this.mtTemplateManager = mtTemplateManager;
	}

	@Override
	public void delete(Long id) throws BusinessException {
		mtTemplateManager.delete(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtTemplateCAP> findAllNoPaginate(String type) {
		List<MtTemplate> list = mtTemplateManager.findAllNoPaginate(type);
		if (list == null) {
			return null;
		}
		return (List<MtTemplateCAP>) BeanUtils.converts(MtTemplateCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtTemplateCAP> findAllWithoutInit(String type) {
		List<MtTemplate> list = mtTemplateManager.findAllWithoutInit(type);
		if (list == null) {
			return null;
		}
		return (List<MtTemplateCAP>) BeanUtils.converts(MtTemplateCAP.class, list);
	}

	@Override
	public MtTemplateCAP getById(Long id) {
		MtTemplate mtTemplate = mtTemplateManager.getById(id);
		if (mtTemplate == null) {
			return null;
		}
		MtTemplateCAP mtTemplateCAP = new MtTemplateCAP();
		BeanUtils.convert(mtTemplateCAP, mtTemplate);
		return mtTemplateCAP;
	}

	public void update(long templateId, Map<String, Object> colums)  throws BusinessException {
		mtTemplateManager.update(templateId, colums);
	}

}