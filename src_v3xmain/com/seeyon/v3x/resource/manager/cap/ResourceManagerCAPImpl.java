package com.seeyon.v3x.resource.manager.cap;

import java.util.List;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.resource.domain.ResourceCAP;
import com.seeyon.cap.resource.manager.ResourceManagerCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.resource.domain.Resource;
import com.seeyon.v3x.resource.manager.ResourceManager;

public class ResourceManagerCAPImpl implements ResourceManagerCAP {

	private ResourceManager resourceManager;

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void delResourceIppByAppId(Long appId) {
		resourceManager.delResourceIppByAppId(appId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceCAP> findResourcesByType(String type) {
		List<Resource> list = resourceManager.findResourcesByType(type);
		if (list == null) {
			return null;
		}
		return (List<ResourceCAP>) BeanUtils.converts(ResourceCAP.class, list);
	}

	@Override
	public ResourceCAP getResourceByPk(Long id) {
		Resource resource = resourceManager.getResourceByPk(id);
		if (resource == null) {
			return null;
		}
		ResourceCAP resourceCAP = new ResourceCAP();
		BeanUtils.convert(resourceCAP, resource);
		return resourceCAP;
	}

	@Override
	public void saveOrUpdateImpropriateResources4Meeting(MtMeetingCAP meeting, String oper) {
		resourceManager.saveOrUpdateImpropriateResources4Meeting(meeting, oper);
	}

}