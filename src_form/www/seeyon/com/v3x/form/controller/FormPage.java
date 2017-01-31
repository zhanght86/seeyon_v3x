package www.seeyon.com.v3x.form.controller;

import java.util.List;

import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FormView;

/**
 * 用于存储all.xml中
 * 			<FormList>
 *					<Form name="软件产品定货单" type="seeyonform">
 *					   <Engine>infopath</Engine>
 *							<ViewList>
 *							    <View viewfile="view1.xsl" viewtype="html"/>
 *							</ViewList>
 *							<OperationList>
 *							    <Operation name="填写" filename="Operation_001.xml" type="add"/>
 *							    <Operation name="审批" filename="Operation_001.xml" type="update"/>
 *							</OperationList>
 *					</Form>
 *			</FormList>	
 * @author kyt
 *
 */
public class FormPage {
	//表单名称
	private String name;
	//用于保存operconfig中的操作列表的值
	private List<Operation> operlst;
	//用于保存viewlst中值
	private List<InfoPath_FormView> viewlst;
	//默认为infopath
	private String engine;
	
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getOperlst() {
		return operlst;
	}
	public void setOperlst(List operlst) {
		this.operlst = operlst;
	}
	public List<InfoPath_FormView> getViewlst() {
		return viewlst;
	}
	public void setViewlst(List<InfoPath_FormView> viewlst) {
		this.viewlst = viewlst;
	}
	
}
