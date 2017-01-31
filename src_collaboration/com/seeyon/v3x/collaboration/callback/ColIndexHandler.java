package com.seeyon.v3x.collaboration.callback;

import org.apache.log4j.Logger;

import com.seeyon.v3x.common.callback.CallbackHandler;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;


public class ColIndexHandler extends CallbackHandler {
	
	public static final Logger logger = Logger.getLogger(ColIndexHandler.class);
	
	public static IndexManager indexManager;
	
	public static IndexEnable colManager;
	
	
	public ColIndexHandler(){
		registerMyself();
	}
	/*
	 * 只需要一个参数就是协同的id
	 * (non-Javadoc)
	 * @see com.seeyon.v3x.common.callback.CallbackHandler#invoke(java.lang.String[])
	 */
	@Override
	public void invoke(String... args) {
		try {
			if(!IndexInitConfig.hasLuncenePlugIn())
				return;
			if(colManager==null) colManager=(IndexEnable)ApplicationContextHolder.getBean("colManager");
			
			Long id=Long.parseLong(args[0]);
			IndexInfo info=colManager.getIndexInfo(id);
			indexManager.index(info);
		} catch (NumberFormatException e) {
			logger.error("数据格式化异常", e);
		} catch (Exception e) {
			logger.error("", e);
		}
		

	}

	@Override
	protected void registerMyself() {
		
		registerCallbackHandler(CallbackHandler.CALLBACK_COLLABORATION_INDEX,
				this);

	}
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
//		System.out.println("here set indexManager???");
		ColIndexHandler.indexManager = indexManager;
	}
	public static void setColManager(IndexEnable colManager) {
		ColIndexHandler.colManager = colManager;
	}

}
