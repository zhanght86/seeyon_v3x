package com.seeyon.v3x.common.barCode.manager;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.barCode.dao.BarCodeDao;
import com.seeyon.v3x.common.barCode.domain.BarCodeInfo;

public class BarCodeManagerImpl implements BarCodeManager {
	
	private static Log log = LogFactory.getLog(BarCodeManagerImpl.class);

	private BarCodeDao barCodeDao;
	
	public void setBarCodeDao(BarCodeDao barCodeDao) {
		this.barCodeDao = barCodeDao;
	}

	/**
	 *保存或更新二维码信息
	 *@param objectId  对象id，比如：公文id
	 *@param fileName  服务器保存的物理文件名
	 *@param categoryId 应用分类 
	*/
	@Override
	public void saveBarCode(Long objectId, Long fileName,String fileExt,Integer categoryId) {
		BarCodeInfo info = this.barCodeDao.getByObjectId(objectId);
		Date today = new Date();
		if(info != null) {
			info.setFileName(fileName);
			info.setFileExt(fileExt);
			info.setUpdateDate(today);
			this.barCodeDao.update(info);
		}else {
			info = new BarCodeInfo();
			info.setNewId();
			info.setCategoryId(categoryId);
			info.setCreateDate(today);
			info.setUpdateDate(today);
			info.setFileName(fileName);
			info.setObjectId(objectId);
			info.setFileExt(fileExt);
			this.barCodeDao.save(info);
		}
	}

}
