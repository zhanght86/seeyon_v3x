package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;

public class EdocManager4ISearchManager extends ISearchManager {
	private EdocManager edocManager;	

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.edoc.getKey();
	}

	@Override
	public String getAppShowName() {		
		return null;
	}

	@Override
	public int getSortId() {
		// TODO Auto-generated method stub
		return this.getAppEnumKey();
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) {		
		return edocManager.iSearch(cModel);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public EdocManager getEdocManager() {
		return edocManager;
	}

	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}

}
