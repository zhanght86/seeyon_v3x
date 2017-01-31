package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.util.Strings;

import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeTask;
import www.seeyon.com.v3x.form.manager.define.form.dataformat.AbstractFormat;
import www.seeyon.com.v3x.form.manager.define.form.dataformat.Format;
import www.seeyon.com.v3x.form.manager.define.form.dataformat.FormatType;

public class TableFieldDisplay implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8679910898983020984L;
	//订货单位
	private String name;
	//ASUT_Customer
	private String tablename;
	
	private String editablename;
	//用于控制表的数量的排号
	private Long tablenumber;
	//CompanyName
	private String fieldname;
	//my:订货单位
	private String bindname;
	//如有修改，存储新修改后的名字
	private String editname;
	private String editbindname;
	/**字段属性**/
	private String id;
	private String length;
	private String fieldtype;
	private String isnull;
	private String digits;
	/**用于控制**/
	private String formoper;
	private String formprint = "Y";
	private String formtransmit = "Y";
	/**高级控制**/
	private String inputtype;	
	private String enumtype;
	//用于页面显示
	private String divenumtype;//枚举id
	private String divenumname;//枚举名称
	private boolean isFinalChild;//是否仅显示末级枚举
	private int divenumlevel;//当前设置枚举深度
	private String compute;	
	private String extend;
	private String formula;//计算公式
	private String displayFormat;//显示格式 例如：人民币大写格式（长格式/短格式）
	
	//关联对象与关联属性
	private String refInputName;
	private String refInputAtt;
	private String refInputType;
	
	//关联表单对象与属性
	private String refParams;
	private boolean isDisplayRelated;
	private boolean isDisplayBaseForm;//是否显示关联的基础表单数据
	private String selectType="user";
	private String relationConditionId;
	
	private InfoPath_DeeTask deeTask;
	public boolean isDisplayBaseForm() {
		return isDisplayBaseForm;
	}

	public void setDisplayBaseForm(boolean isDisplayBaseForm) {
		this.isDisplayBaseForm = isDisplayBaseForm;
	}
	private String onitvalue;
	
	private String onitxml;
	
	private String formatType = "" ;
	
	private String formatTypeName ;
	
	private boolean isUnique = false ;
	
	public boolean isUnique() {
		return isUnique;
	}
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	public String getFormatTypeName() {
		return formatTypeName;
	}
	public void setFormatTypeName(String formatTypeName) {
		this.formatTypeName = formatTypeName;
	}
	public String getFormatType() {
		return formatType;
	}
	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}


	//是否是infopath修改时的新增字段
	private String addfieldsign;
	
	public String getOnitvalue() {
		return onitvalue;
	}
	public void setOnitvalue(String onitvalue) {
		this.onitvalue = onitvalue;
	}
	public String getOnitxml() {
		return onitxml;
	}
	public void setOnitxml(String onitxml) {
		this.onitxml = onitxml;
	}
	public String getDivenumtype() {
		return divenumtype;
	}
	public void setDivenumtype(String divenumtype) {
		this.divenumtype = divenumtype;
	}
	public String getDigits() {
		return digits;
	}
	public void setDigits(String digits) {
		this.digits = digits;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public String getRefInputName() {
		return refInputName;
	}
	public void setRefInputName(String refInputName) {
		this.refInputName = refInputName;
	}
	public String getRefInputAtt() {
		return refInputAtt;
	}
	public void setRefInputAtt(String refInputAtt) {
		this.refInputAtt = refInputAtt;
	}
	public String getRefInputType() {
		return refInputType;
	}
	public void setRefInputType(String refInputType) {
		this.refInputType = refInputType;
	}
	public String getRefParams() {
		return refParams;
	}
	public void setRefParams(String refParams) {
		this.refParams = refParams;
	}
	public boolean isDisplayRelated() {
		return isDisplayRelated;
	}
	public void setDisplayRelated(boolean isDisplayRelated) {
		this.isDisplayRelated = isDisplayRelated;
	}
	public String getEditbindname() {
		return editbindname;
	}
	public void setEditbindname(String editbindname) {
		this.editbindname = editbindname;
	}
	public String getEditname() {
		return editname;
	}
	public void setEditname(String editname) {
		this.editname = editname;
	}
	public String getFormoper() {
		return formoper;
	}
	public void setFormoper(String formoper) {
		this.formoper = formoper;
	}
	public String getFormprint() {
		return formprint;
	}
	public void setFormprint(String formprint) {
		this.formprint = formprint;
	}
	public String getFormtransmit() {
		return formtransmit;
	}
	public void setFormtransmit(String formtransmit) {
		this.formtransmit = formtransmit;
	}
	public String getFieldtype() {
		return fieldtype;
	}
	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIsnull() {
		return isnull;
	}
	public void setIsnull(String isnull) {
		this.isnull = isnull;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public Long getTablenumber() {
		return tablenumber;
	}
	public void setTablenumber(Long tablenumber) {
		this.tablenumber = tablenumber;
	}
	public String getCompute() {
		return compute;
	}
	public void setCompute(String compute) {
		this.compute = compute;
	}
	public String getEnumtype() {
		return enumtype;
	}
	public void setEnumtype(String enumtype) {
		this.enumtype = enumtype;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public String getInputtype() {
		return inputtype;
	}
	public void setInputtype(String inputtype) {
		this.inputtype = inputtype;
	}
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
	public String getEditablename() {
		return editablename;
	}
	public void setEditablename(String editablename) {
		this.editablename = editablename;
	}
	
	public InfoPath_DeeTask getDeeTask() {
		return deeTask;
	}

	public void setDeeTask(InfoPath_DeeTask deeTask) {
		this.deeTask = deeTask;
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
	public String getAddfieldsign() {
		return addfieldsign;
	}
	public void setAddfieldsign(String addfieldsign) {
		this.addfieldsign = addfieldsign;
	}
	public String getDivenumname() {
		return divenumname;
	}
	public void setDivenumname(String divenumname) {
		this.divenumname = divenumname;
	}
	
	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public String getRelationConditionId() {
		return relationConditionId;
	}

	public void setRelationConditionId(String relationConditionId) {
		this.relationConditionId = relationConditionId;
	}

	public boolean isFinalChild() {
		return isFinalChild;
	}
	public void setFinalChild(boolean isFinalChild) {
		this.isFinalChild = isFinalChild;
	}
	public int getDivenumlevel() {
		return divenumlevel;
	}
	public void setDivenumlevel(int divenumlevel) {
		this.divenumlevel = divenumlevel;
	}
	public String getOperationString(Map<String,Format> dataFormat,String formatyType){
		StringBuffer strbuf = new StringBuffer() ;
		strbuf.append("<option value=''></option>");
		if(dataFormat == null){
			return strbuf.toString() ;
		}
		
		AbstractFormat numberformat = (AbstractFormat)dataFormat.get(this.fieldtype) ;
		if(numberformat == null){
			return strbuf.toString() ;
		}
		if(Strings.isBlank(formatyType)){
			formatyType = formatType ;
		}
		Map<String,FormatType> map = numberformat.getPatternMap() ;		
		if(map != null){
			for(String str : map.keySet()){
				if(map.get(str) != null && map.get(str).getFormatType() != null){
					if(map.get(str).getFormatType().equals(formatyType))
						strbuf.append("<option value='"+map.get(str).getFormatType()+"' selected>"+Constantform.getString4CurrentUser(str)+"</option>") ;
					else
						strbuf.append("<option value='"+map.get(str).getFormatType()+"'>"+Constantform.getString4CurrentUser(str)+"</option>") ;
				
				}
			}					                           				
		}
		return strbuf.toString() ;
	}
}
