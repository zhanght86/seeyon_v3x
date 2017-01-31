package com.seeyon.v3x.doc.manager.cap;

import com.seeyon.cap.doc.domain.DocResourceCAP;
import com.seeyon.cap.doc.manager.DocHierarchyManagerCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;

public class DocHierarchyManagerCAPImpl implements DocHierarchyManagerCAP {

	private DocHierarchyManager docHierarchyManager;

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	@Override
	public DocResourceCAP getDocResourceById(Long docResourceId) {
		DocResource docResource = docHierarchyManager.getDocResourceById(docResourceId);
		if (docResource == null) {
			return null;
		}
		DocResourceCAP docResourceCAP = new DocResourceCAP();
		BeanUtils.convert(docResourceCAP, docResource);
		return docResourceCAP;
	}

	@Override
	public String getNameById(Long docResourceId) {
		return docHierarchyManager.getNameById(docResourceId);
	}

}