package com.seeyon.v3x.log.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;

public class LogonLogHelper  {

	static Log log = LogFactory.getLog(LogonLogHelper.class);
	
	public static DataRecord exportToExcel(String title,List<String[]> results,String[] columnName) {
		DataRecord dataRecord = new DataRecord();
		dataRecord.setSheetName(title);
		dataRecord.setTitle(title);
		dataRecord.setColumnName(columnName);
		if(results != null && results.size() > 0) {
			DataRow[] datarow = new DataRow[results.size()];
			DataRow  row = null;
			int i = 0;
			for(String[] result : results) {
				row = new DataRow();
				for(String obj : result) {
					row.addDataCell(obj, DataCell.DATA_TYPE_TEXT);
				}
				datarow[i] = row;
				i++;
			}
			try {
				dataRecord.addDataRow(datarow);
				datarow = null;
			} catch (Exception e) {
				log.error(e);
			}
		}
		return dataRecord;
	}
}
