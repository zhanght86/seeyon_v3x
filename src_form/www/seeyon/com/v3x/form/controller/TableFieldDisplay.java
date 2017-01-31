package www.seeyon.com.v3x.form.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFieldDisplay {
	//订货单位
	private String name;
	//ASUT_Customer
	private String tablename;
	//CompanyName
	private String fieldname;
	//my:订货单位
	private String bindname;
	
	
	public String getBindname() {
		return bindname;
	}
	public void setBindname(String bindname) {
		this.bindname = bindname;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public List changetomap(List TableFieldDisplayLst){
		List returnlst = new ArrayList();
		for(int i=0;i<TableFieldDisplayLst.size();i++){
			TableFieldDisplay tfd = (TableFieldDisplay)TableFieldDisplayLst.get(i);
			Map map = new HashMap();
			map.put("fieldname",tfd.getFieldname());
			map.put("bindname",tfd.getBindname());
			map.put("tablename",tfd.getTablename());
			returnlst.add(map);
		}
		return returnlst;
	}
}
