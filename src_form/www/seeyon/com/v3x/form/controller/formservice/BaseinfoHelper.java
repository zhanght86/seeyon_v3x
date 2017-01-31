package www.seeyon.com.v3x.form.controller.formservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.formenum.inf.IFormEnumManager;
import www.seeyon.com.v3x.form.base.formenum.inf.IFormEnumManager.TFormEnumType;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.inf.IPageObjectCheck;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.controller.query.QueryObject;
import www.seeyon.com.v3x.form.controller.report.ReportObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Calculate;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Enum;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Calculate.Calculate_Function;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Calculate.ICalculate_Base;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.query.ConditionInput;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Calculate.Calculate_DataColum;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

public class BaseinfoHelper implements IPageObjectCheck{
	/*
	 * 此类用于计算字段设置的校验,如果校验不成功则抛出异常,成功返回true
	 */
		private List<SeeyonFormException> exceptionList = new ArrayList<SeeyonFormException>();
		private String xmlHead = "<Calculate>";
		private String xmlHead1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		private String xmlEnd = "</Calculate>";
		public List<SeeyonFormException> isMatch(SessionObject sessionobject) throws SeeyonFormException{
			//进行校验操作
			exceptionList.clear();
			List tablefieldlist = sessionobject.getTableFieldList();
			Long formsort = sessionobject.getFormsort();
			checkField(tablefieldlist,formsort,sessionobject);
			return exceptionList;
		}
		
		private void checkField(List tablefieldlist,Long formsort,SessionObject sessionobject) throws SeeyonFormException{
			MetadataManager metadataManager = (MetadataManager) ApplicationContextHolder.getBean("metadataManager");
			SeeyonFormException e;
			HashMap calcumap = new HashMap();//普通数字类型计算
			HashMap calcudatemap = new HashMap();//日期差计算
			HashMap calcCharMap = new HashMap();//字符计算，及字符相加
			sessionobject.getEnumnamemap().clear();
			for(int i=0;i<tablefieldlist.size();i++){
				String type = "";
				String name = "";
				TableFieldDisplay tafield = (TableFieldDisplay)tablefieldlist.get(i);
				if(tafield.getCompute()!=null && !"".equals(tafield.getCompute()) && !"null".equals(tafield.getCompute())){
					Element Computeroot = dom4jxmlUtils.paseXMLToDoc(xmlHead+OperHelper.parseSpecialMark(tafield.getCompute())+xmlEnd).getRootElement();	
					InfoPath_Calculate calcu = new InfoPath_Calculate();
					calcu.loadFromXml(Computeroot);
					for(int j=0;j<calcu.getFcalculatelst().size();j++){
						if(calcu.getFcalculatelst().get(j) instanceof Calculate_DataColum){
							Calculate_DataColum calcudate = (Calculate_DataColum)calcu.getFcalculatelst().get(j);
							type = calcudate.getType();
							if("appendValue".equals(type)){
								StringBuffer sb = new StringBuffer();
								String nameString = calcudate.getName();
								boolean appendFlag = false;
								for(int k = 0 ;k < nameString.length() ;k++){
									char ch = nameString.charAt(k);
									if(!appendFlag && ch == '{'){
										appendFlag = true ;		
										continue ;
									}else if(ch == '}'){
										appendFlag = false ;
										String fieldName = sessionobject.getNamespace() + sb.toString();
										calcCharMap.put(fieldName, fieldName);
										sb = new StringBuffer() ;
										continue ;
									}
									if(appendFlag){
										sb.append(ch) ;
									}
								}
							} else {
								calcumap.put(calcudate.getName(),calcudate.getName());
							}
						}else if(calcu.getFcalculatelst().get(j) instanceof Calculate_Function){
							Calculate_Function calcudate = (Calculate_Function)calcu.getFcalculatelst().get(j);	
							name = calcudate.getName();
							for(int a=0;a<calcudate.getList().size();a++){
								if(calcudate.getList().get(a) instanceof Calculate_DataColum){
									Calculate_DataColum datacolumdate = (Calculate_DataColum)calcudate.getList().get(a);
									calcudatemap.put(datacolumdate.getName(), datacolumdate.getName());
								}
								
							}
						}
					}
					String sign = "false";
					//含有"<caclDate"即为日期型与数字的计算
					if(tafield.getCompute().indexOf("calcDate") !=-1){
						sign="true";
						if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.TIMESTAMP)
								&&!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.DATETIME)){
							//e = new SeeyonFormException(1,tafield.getName() + "是日期型被计算字段,字段类型必须为日期类型");     
				        	e = new SeeyonFormException(1,tafield.getName() + Constantform.getString4CurrentUser("form.base.fieldtypemustbedatafraction"));			        	
				        	exceptionList.add(e);
							break;
						}
					}
					//日期差计算
					if("differDate".equals(name)){
						 if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.DECIMAL)){
								//e = new SeeyonFormException(1,tafield.getName() + "是日期差型的被计算字段,字段类型必须为数字类型");		        
					        	e = new SeeyonFormException(1,tafield.getName() + "是日期差型的被计算字段,字段类型必须为数字类型");			        	
					        	exceptionList.add(e);
								break;
						}
					}	
					//大小写转换
					if("toUpper".equalsIgnoreCase(type)){
						sign="true";
						if(!"text".equals(tafield.getInputtype()) && !"lable".equals(tafield.getInputtype())){
			        		//e = new SeeyonFormException(1,tafield.getName() + "定义了货币大小写转换，输入类型必须是文本框或者标签。");	
			        		e = new SeeyonFormException(1,tafield.getName() + Constantform.getString4CurrentUser("form.base.fieldtypemustbelabelfraction"));			        	
				        	exceptionList.add(e);
							break;
			        	}if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.VARCHAR)){
			        		//e = new SeeyonFormException(1,tafield.getName() + "定义了货币大小写转换，字段类型必须是文本类型");	
			        		e = new SeeyonFormException(1,tafield.getName() + "定义了货币大小写转换，字段类型必须是文本类型");			        	
				        	exceptionList.add(e);
							break;
			        	}
					}
					//字符计算（字符相加）
					if("appendValue".equalsIgnoreCase(type)){
						sign="true";
						if(!"text".equals(tafield.getInputtype()) && !"lable".equals(tafield.getInputtype())){
			        		e = new SeeyonFormException(1,tafield.getName() + "设置了字符计算，输入类型必须是文本框或者标签。");			        	
				        	exceptionList.add(e);
							break;
			        	}if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.VARCHAR)){
			        		e = new SeeyonFormException(1,tafield.getName() + "设置了字符计算，字段类型必须是文本类型");			        	
				        	exceptionList.add(e);
							break;
			        	}
					}
					//普通的数字类型计算
					if("false".equals(sign)){
						 if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.DECIMAL)){
								//e = new SeeyonFormException(1,tafield.getName() + "是数字型被计算字段,字段类型必须为数字类型");		        
					        	e = new SeeyonFormException(1,tafield.getName() + Constantform.getString4CurrentUser("form.base.fieldtypemustbedecimalfraction"));			        	
					        	exceptionList.add(e);
								break;
						}
					}		       
				}
			}
			for(int i=0;i<tablefieldlist.size();i++){
				TableFieldDisplay tafield = (TableFieldDisplay)tablefieldlist.get(i);
				if(calcumap.size() !=0){
					if(calcumap.get(tafield.getBindname())!=null){
						 if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.DECIMAL)){
//							    e = new SeeyonFormException(1,tafield.getName() + "是计算字段,字段类型必须为小数类型");
								e = new SeeyonFormException(1,tafield.getName() + Constantform.getString4CurrentUser("form.base.fieldtypemustbedecimalfraction"));
								exceptionList.add(e);
								break;
							}
					}
				}
				if(calcudatemap.size() !=0){
					if(calcudatemap.get(tafield.getBindname())!=null){
						 if(!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.TIMESTAMP)
								 &&!tafield.getFieldtype().equalsIgnoreCase(IPagePublicParam.DATETIME)){
							   //e = new SeeyonFormException(1,tafield.getName() + "是日期差型的计算字段,字段类型必须为日期类型");     
					        	e = new SeeyonFormException(1,tafield.getName() + "是日期差型的计算字段,字段类型必须为日期类型");			        	
					        	exceptionList.add(e);
								break;
							}
					}
				}
				if(calcCharMap.size() != 0){
					if(calcCharMap.get(tafield.getBindname()) != null){
						if(IPagePublicParam.LONGTEXT.equalsIgnoreCase(tafield.getFieldtype()) 
								|| IPagePublicParam.HANDWRITE.equalsIgnoreCase(tafield.getFieldtype())){
							e = new SeeyonFormException(1,tafield.getName() + "是字符计算的字段,字段类型不能为备注类型或签章类型");
							exceptionList.add(e);
							break;
						}
						if("checkbox".equals(tafield.getInputtype()) || "插入附件".equals(tafield.getExtend()) 
								|| "关联文档".equals(tafield.getExtend()) || "插入图片".equals(tafield.getExtend())){
							e = new SeeyonFormException(1,tafield.getName() + "是字符计算的字段,输入类型不能为复选框、插入附件、插入文档或插入图片");
							exceptionList.add(e);
							break;
						}
					}
				}

				if(tafield.getDivenumtype()!=null && !"".equals(tafield.getDivenumtype()) && !"null".equals(tafield.getDivenumtype())){
					Element Enumroot = dom4jxmlUtils.paseXMLToDoc(OperHelper.parseSpecialMark(tafield.getEnumtype())).getRootElement();	
					InfoPath_Enum enums = new InfoPath_Enum();
					enums.loadFromXml(Enumroot);
					  metadataManager = (MetadataManager) ApplicationContextHolder.getBean("metadataManager");
					  if(enums.getEnumid() != null){
						  Metadata formMetadata = metadataManager.getMetadata(enums.getEnumid());
						  if(formMetadata ==null){
							  e = new SeeyonFormException(1,"'"+tafield.getName() + "'的枚举值不存在或已被删除，请重新选择。");							
							  exceptionList.add(e);
							  break;
						  }							 
					  }					     
					/*  
					if(enums.OperationType2str(enums.getFemtype()).equals("app")){
						IFormEnumManager fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(formsort));
						if(fenummanager.getEnumByName(tafield.getDivenumtype()) == null){
							//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
							//e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+" '"+tafield.getDivenumtype()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
							e = new SeeyonFormException(1,"该系统的应用枚举已经取消，请重新选择'"+tafield.getName() + "'的枚举值。");
							
							exceptionList.add(e);
							break;
						}else{
							if(fenummanager.getEnumByName(tafield.getDivenumtype()).getValueList().size()==0){
								e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+ " '"+tafield.getDivenumtype() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
								exceptionList.add(e);
								break;
							}
						}
					}else if(enums.OperationType2str(enums.getFemtype()).equals("system")){
						IFormEnumManager fformenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf("0"));
						if(fformenummanager.getEnumByName(tafield.getDivenumtype()) == null){
							//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
							//e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+" '"+tafield.getDivenumtype()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
							e = new SeeyonFormException(1,"'"+tafield.getName() + "'的枚举值不存在或已被删除，请重新选择。");
							
							exceptionList.add(e);
							break;
						}else{
							if(fformenummanager.getEnumByName(tafield.getDivenumtype()).getValueList().size()==0){
								e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+ " '"+tafield.getDivenumtype() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
								exceptionList.add(e);
								break;
							}
						}
					}else if(enums.OperationType2str(enums.getFemtype()).equals("form")){
						ISeeyonForm_Application app = SeeyonForm_Runtime.getInstance().getAppManager().createApplication();
						IFormEnumManager 	formmangerenum = app.getFormEnumManager();
						if(formmangerenum.getEnumByName(tafield.getDivenumtype()) == null){
							//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
							e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+" '"+tafield.getDivenumtype()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
							exceptionList.add(e);
							break;
						}else{
							if(formmangerenum.getEnumByName(tafield.getDivenumtype()).getValueList().size()==0){
								e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.formField")+" '"+tafield.getName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+ " '"+tafield.getDivenumtype() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
								exceptionList.add(e);
								break;
							}
						}
					}
					*/
					sessionobject.getEnumnamemap().put(enums.getEnumid(), enums.getEnumid());
				}				
			}
			//验证查询条件的枚举设置
			
			List querylist = sessionobject.getQueryConditionList();
			for(int i = 0;i<querylist.size();i++){
				QueryObject query = (QueryObject)querylist.get(i);
				Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(query.getQueryConditionValue()));
				Element QueryConditionRoot = QueryConditionDoc.getRootElement();
				ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
				userConditionList.loadFromXml(QueryConditionRoot);
				List conditionList = userConditionList.getConditionList();
				ICondition fitem;
				QueryUserConditionDefin userconditon = null;
				if(conditionList !=null){
					for(int a= 0; a < conditionList.size(); a++){
						fitem = (ICondition)conditionList.get(a);
						if(fitem instanceof QueryUserConditionDefin){
							userconditon = (QueryUserConditionDefin)fitem;
							ConditionInput input = (ConditionInput)userconditon.getConditionInput();
							if(input!=null){
								/*
								if("app".equals(input.getEnumType())){
									IFormEnumManager fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(formsort));
									if(fenummanager.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										//e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										e = new SeeyonFormException(1,"该系统的应用枚举已经取消，请重新选择查询条件中的枚举'"+input.getEnumName() + "'。");
										exceptionList.add(e);
										break;
									}else{
										if(fenummanager.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}else if("system".equals(input.getEnumType())){
									IFormEnumManager fformenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf("0"));
									if(fformenummanager.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										//e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										e = new SeeyonFormException(1,"您查询条件中的枚举'"+input.getEnumName() + "'已经不存在或已经被删除，请重新选择。");
										exceptionList.add(e);
										break;
									}else{
										if(fformenummanager.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}else if("form".equals(input.getEnumType())){
									ISeeyonForm_Application app = SeeyonForm_Runtime.getInstance().getAppManager().createApplication();
									IFormEnumManager 	formmangerenum = app.getFormEnumManager();
									if(formmangerenum.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										exceptionList.add(e);
										break;
									}else{
										if(formmangerenum.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonquery")+Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}
								*/								
								  if(input.getEnumid() != null){
									  Metadata queryMetadata = metadataManager.getMetadata(input.getEnumid());
									  if(queryMetadata ==null){
										  e = new SeeyonFormException(1,"您查询条件中的枚举'"+input.getEnumName() + "'已经不存在或已经被删除，请重新选择。");					
										  exceptionList.add(e);
										  break;
									  }							 
								  }			
								if(input.getEnumName() !=null && !"null".equals(input.getEnumName()) && !"".equals(input.getEnumName()))
								   sessionobject.getEnumnamemap().put(input.getEnumid(), input.getEnumid());
							}
						}
					}
				}						
			}		
			
			//验证统计条件的枚举设置
			List reportlist = sessionobject.getReportConditionList();
			for(int i = 0;i<reportlist.size();i++){
				ReportObject report = (ReportObject)reportlist.get(i);
				Document ReportConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(report.getconditionvalue()));
				Element ReportConditionRoot = ReportConditionDoc.getRootElement();
				ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
				userConditionList.loadFromXml(ReportConditionRoot);
				List conditionList = userConditionList.getConditionList();
				ICondition fitem;
				QueryUserConditionDefin userconditon = null;
				if(conditionList !=null){
					for(int a= 0; a < conditionList.size(); a++){
						fitem = (ICondition)conditionList.get(a);
						if(fitem instanceof QueryUserConditionDefin){
							userconditon = (QueryUserConditionDefin)fitem;
							ConditionInput input = (ConditionInput)userconditon.getConditionInput();
							if(input!=null){
								/*
								if("app".equals(input.getEnumType())){
									IFormEnumManager fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(formsort));
									if(fenummanager.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										exceptionList.add(e);
										break;
									}else{
										if(fenummanager.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setappenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}else if("system".equals(input.getEnumType())){
									IFormEnumManager fformenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf("0"));
									if(fformenummanager.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										exceptionList.add(e);
										break;
									}else{
										if(fformenummanager.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setsystemenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}else if("form".equals(input.getEnumType())){
									ISeeyonForm_Application app = SeeyonForm_Runtime.getInstance().getAppManager().createApplication();
									IFormEnumManager 	formmangerenum = app.getFormEnumManager();
									if(formmangerenum.getEnumByName(input.getEnumName()) == null){
										//e = new SeeyonFormException(1,"枚举"+" '"+tafield.getDivenumtype()+"' " + "不属于您选择的所属分类");
										e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+" '"+input.getEnumName()+"' " + Constantform.getString4CurrentUser("form.base.notbelongselectedassort.label"));
										exceptionList.add(e);
										break;
									}else{
										if(formmangerenum.getEnumByName(input.getEnumName()).getValueList().size()==0){
											e = new SeeyonFormException(1,Constantform.getString4CurrentUser("InputField.InputSelect.conditonreport")+Constantform.getString4CurrentUser("InputField.InputSelect.setformenum")+ " '"+input.getEnumName() + "' " +Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist"));
											exceptionList.add(e);
											break;
										}
									}
								}
								*/
								if(input.getEnumid() != null){
									  Metadata reportMetadata = metadataManager.getMetadata(input.getEnumid());
									  if(reportMetadata ==null){
										  e = new SeeyonFormException(1,"您统计条件中的枚举'"+input.getEnumName() + "'已经不存在或已经被删除，请重新选择。");					
										  exceptionList.add(e);
										  break;
									  }							 
								  }		
								if(input.getEnumName() !=null && !"null".equals(input.getEnumName()) && !"".equals(input.getEnumName()))
									   sessionobject.getEnumnamemap().put(input.getEnumid(), input.getEnumid());
							}
						}
					}
				}						
			}
			
		
		}
//		去掉字符串最后的/r/n
		private String delTrailSection(String str){
			
			if(str.endsWith("/r/n"))
				return str.substring(0,str.length() - 4);
			else
				return str;
				
			
		}
}
