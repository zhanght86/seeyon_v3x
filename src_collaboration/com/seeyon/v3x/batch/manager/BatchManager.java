/**
 * 
 */
package com.seeyon.v3x.batch.manager;

import java.util.List;

import com.seeyon.v3x.batch.BatchData;
import com.seeyon.v3x.batch.BatchResult;

/**
 * @author dongyj
 *
 */
public interface BatchManager {
	
	/**
	 * 预批量处理。检查传入的是否可以进行批处理。返回状态
	 * @param affairId
	 * @param summaryId
	 * @param categoryId
	 * @return 如果可以进行批处理，返回resultCode = "0";
	 */
	public BatchResult[] preCheckBatch(Long[] affairId,Long[] summaryId,Integer[] category);
	
	/**
	 * 进行批处理，返回处理信息
	 * @param data
	 * @return
	 */
	public List<BatchResult> doBatch(List<BatchData> data);
}
