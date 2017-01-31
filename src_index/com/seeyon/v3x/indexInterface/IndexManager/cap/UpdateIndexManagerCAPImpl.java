package com.seeyon.v3x.indexInterface.IndexManager.cap;

import com.seeyon.cap.indexInterface.IndexManager.UpdateIndexManagerCAP;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;

public class UpdateIndexManagerCAPImpl implements UpdateIndexManagerCAP {

	private UpdateIndexManager updateIndexManager;

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	@Override
	public void update(Long entityId, Integer type) {
		updateIndexManager.update(entityId, type);
	}

}