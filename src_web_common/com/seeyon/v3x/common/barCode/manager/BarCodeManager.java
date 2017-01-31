package com.seeyon.v3x.common.barCode.manager;

public interface BarCodeManager {
	/**
	 *保存或更新二维码信息
	 *@param objectId  对象id，比如：公文id
	 *@param fileName  服务器保存的物理文件名
	 *@param categoryId 应用分类 
	*/
	public void saveBarCode(Long objectId,Long fileName,String fileExt,Integer categoryId);
}
