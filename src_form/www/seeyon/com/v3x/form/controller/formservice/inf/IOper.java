package www.seeyon.com.v3x.form.controller.formservice.inf;

import java.util.List;

import org.dom4j.DocumentException;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;

public interface IOper {
	/**
	 * 用于装配应用名称,所属人名称
	 * @param categorylst
	 * @return
	 * @throws DataDefineException 
	 * @throws BusinessException 
	 */
	public List assignCategory(List categorylst) throws DataDefineException, BusinessException;
	
	/**
	 * 组织前台数据
	 */
	public  List getFixOperlst(List tablefieldlst);

	/**
	 * 装配operconfig页面默认的三种操作
	 * @param aStr
	 * @return
	 */
	public  void addDefaultOperLst(FormPage fp,int selenum);
	
	/**
	 * 新增时把table名与其表号存入数据库
	 * @param tablefieldlst
	 * @param fdm
	 * @throws DataDefineException
	 */
	public void insertNewestTableName(List tablefieldlst) throws DataDefineException;
	
	/**
	 * 表单填写完成后，保存表单相应的xml及生成对应的数据库对象
	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 */
	public void LoadFromCab(SessionObject sessionobject) throws SeeyonFormException;
	

	/**
	 * 	 
	 * @param str
	 * @return
	 */
	public String parseQuotationMark(String str);
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public String parseSpecialMark(String str);
	
	public List queryAllData(BaseModel bm) throws DataDefineException;
	
	public BaseModel findBiggestValue() throws DataDefineException;
	
	/**
	 * 把table名与其表号存入数据库
	 * @param tablefieldlst
	 * @throws DataDefineException
	 */
	public void saveTableValue(List tablefieldlst) throws DataDefineException;
	
	
	/**
	 * 对接收上传文件方法的封装
	 * @param fileManager
	 * @param urls
	 * @param createDates
	 * @param mimeTypes
	 * @param names
	 * @return
	 * @throws SeeyonFormException
	 */
	public  String getXSNSaveDirectory(FileManager fileManager,String[] urls,
			String[] createDates,String[] mimeTypes,String[] names) throws SeeyonFormException;
	
	
	/**
	 * 解析XSN文件
	 * @param so
	 * @param directry
	 * @throws SeeyonFormException
	 */
	public  void parseXSN(SessionObject so,String directry) throws SeeyonFormException;
	
	
	/**
	 * 装配一个新的显示字段lst
	 * @param tablenumber
	 * @param masterlst
	 * @param slavelst
	 * @param tablst
	 * @return
	 */
	public  List parseFieldName(Long tablenumber,List masterlst,List slavelst,List tablst);
	
	/**
	 * 
	 */
	public  List parsenewName(List masterlst,List slavelst,List tablst);


	/**
	 * 
	 * @param iapp
	 * @param xsf
	 *            新生成的xsf
	 * @param tablefieldlst
	 *            原有的tablefieldlst
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 */
	public SessionObject loadFromDb(ISeeyonForm_Application iapp, InfoPathObject xsf,
			List tablefieldlst) throws SeeyonFormException, DocumentException;


}
