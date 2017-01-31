package www.seeyon.com.v3x.form.controller.report;

import org.dom4j.Attribute;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.inf.IXmlObject;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.utils.StringUtils;

public class ReportChartInfo implements IXmlObject{
	
	private static RuntimeCharset fCurrentCharSet = SeeyonForm_Runtime.getInstance().getCharset();
	
	/**
	 * 图表名称
	 */
	private String name;
	
	/**
	 * 图表分类项串
	 */
	private String rowNames;
	
	/**
	 * 图表图例项串
	 */
	private String colNames;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRowNames() {
		return rowNames;
	}

	public void setRowNames(String rowNames) {
		this.rowNames = rowNames;
	}

	public String getColNames() {
		return colNames;
	}

	public void setColNames(String colNames) {
		this.colNames = colNames;
	}

	public void loadFromXml(Element aelement) throws SeeyonFormException {
		if (aelement == null) {
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NotsendNode,
					Constantform.getString4CurrentUser("DataDefine.NotsendReportColumNode"));
		}
		Attribute name = aelement.attribute(IXmlNodeName.name);
		if(name != null){
			setName(fCurrentCharSet.SelfXML2JDK(name.getValue()));
		}
		Attribute rowNameStr = aelement.attribute(IXmlNodeName.ReportHead);
		if(rowNameStr != null){
			rowNames = rowNameStr.getValue();
		}
		Attribute colNameStr = aelement.attribute(IXmlNodeName.ReportColum);
		if(colNameStr != null){
			colNames = colNameStr.getValue();
		}
	}

	public String getXml(){
		return "<ReportChart name=\"" + fCurrentCharSet.JDK2SelfXML(name) 
		       + "\" ReportHead=\"" + StringUtils.Java2XMLStr(rowNames) + "\" "  
		       + "ReportColum=\"" + StringUtils.Java2XMLStr(colNames) + "\" />\r\n";
	}
}
