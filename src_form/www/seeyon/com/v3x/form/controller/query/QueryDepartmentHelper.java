package www.seeyon.com.v3x.form.controller.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.OperatorImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.condition.inf.IDataColum;
import www.seeyon.com.v3x.form.base.condition.inf.IOperator;
import www.seeyon.com.v3x.form.base.condition.inf.IProvider;
import www.seeyon.com.v3x.form.base.condition.inf.IValue;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.IIP_InputObject;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputExtend;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputRelation;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.ReportResultImpl;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

public class QueryDepartmentHelper {
	private final static Log log = LogFactory
			.getLog(QueryDepartmentHelper.class);
	private static OrgManager orgManager;
	private static OrgManager getOrgManager(){
		if(orgManager==null){
			orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		}
		return orgManager;
	}
	/**
	 * 按条件分解查询的部门。
	 * @param value 条件的值
	 * @param comparisonOper 条件操作符
	 * @return 如果是部门字段，而且操作符为等于或不等于，值不包含|1时，返回指定部门及其所有子部门的id
	 */
	public static List<Long> getConditionDepartmentList(String value,
			int comparisonOper) {
		if(value==null) return null;
		List<Long> departmentList = null;
		// 等于或不等于才有必要解析子部门
		if(IOperator.C_iOperator_Equal==comparisonOper || IOperator.C_iOperator_notEqual==comparisonOper){
			String[] arr = value.split("\\|");
			// 只要带|就认为是不包含子部门（省略|1的判断）
			
			if(arr.length==1){
				OrgManager orgManager = getOrgManager();
				try {
					if(Strings.isNotBlank(arr[0]) && !ReportResultImpl.IS_NULL.equalsIgnoreCase(arr[0]) && !ReportResultImpl.IS_NOT_NULL.equalsIgnoreCase(arr[0])){
						long deptId = Long.parseLong(arr[0]);
						List<V3xOrgDepartment> departments = orgManager.getChildDepartments(deptId, false);
						if(departments!=null){
							// 有子部门才有必要处理
							if(departments.size()>0){
								departmentList = new ArrayList<Long>();
								departmentList.add(deptId);
								for (V3xOrgDepartment dep : departments) {
									departmentList.add(dep.getId());
								}
							}
						}
					}
				} catch (Throwable e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		return departmentList;
	}	
	/**
	 * 拆解部门条件。拆解条件，变id=xxx为 （id=xxx OR id=xxx）
	 * @param dataColumn
	 * @param op 操作符
	 * @param departmentList
	 * @param provider
	 * @return
	 * @throws SeeyonFormException
	 */
	public static List<ICondition> extractDepartmentCondition(
			IDataColum dataColumn, int op, List<Long> departmentList,
			IProvider provider) throws SeeyonFormException {
		// 拆解条件，变id=xxx为 id=xxx OR id=xxx
		OperatorImpl eq = new OperatorImpl(provider);
		eq.setOperator(op);
		
		OperatorImpl or = new OperatorImpl(provider);
		or.setOperator(IOperator.C_iOperator_Equal==op?IOperator.C_iOperator_Or:IOperator.C_iOperator_And);
		
		OperatorImpl operator = new OperatorImpl(provider);
		operator.setOperator(IOperator.C_iOperator_BracketLeft);
		List<ICondition> tmp = new ArrayList<ICondition>();
		tmp.add(operator); 
		for (Long id : departmentList) {
			tmp.add(dataColumn);
			tmp.add(eq);
			QueryUserConditionDefin ud = new QueryUserConditionDefin();
			ud.setValue(id.toString());
			ud.setValueType(IValue.C_iValueType_Value);
			tmp.add(ud);
			tmp.add(or);
		}
		tmp.remove(tmp.size()-1);
		operator = new OperatorImpl(provider);
		operator.setOperator(IOperator.C_iOperator_BracketRight);
		tmp.add(operator);
		return tmp;
	}	
	/**
	 * 判断指定的字段是否部门字段
	 * @param inputTypeDefine
	 * @param dataColumn
	 * @return
	 * @throws SeeyonFormException
	 */
	public static boolean isDepartmentColumn(
			InfoPath_Inputtypedefine inputTypeDefine, DataColumImpl dataColumn)
			throws SeeyonFormException {
		boolean isDepartmentColumn = false;
		//判断是否部门选择
		IIP_InputObject inputObj;
		if(inputTypeDefine!=null){
			inputObj = inputTypeDefine.findInputByName(dataColumn.getColumName());
			if(inputObj!=null && inputObj instanceof TIP_InputRelation){
				inputObj = FormHelper.getRefInputObject(inputObj);	
			}
			if(inputObj!=null && inputObj instanceof TIP_InputExtend){
				TIP_InputExtend ti = (TIP_InputExtend) inputObj;
				isDepartmentColumn = "选择部门".equals(ti.getFClassName());
			}
		}
		return isDepartmentColumn;
	}	
	
	public static List<ICondition> parseCondition(List<ICondition> conditions,InfoPath_Inputtypedefine inputTypeDefine, IProvider provider) throws SeeyonFormException{
        //拼装用户输入条件开始，这部分条件要加个括号
		if(conditions.size() == 0){
			return conditions;
		}
		List<ICondition> result = new ArrayList<ICondition>(conditions);
		boolean isDepartmentColumn = false;
		List<ICondition> conds = new ArrayList<ICondition>();
		List<ICondition> stack = new ArrayList<ICondition>();
		for (ICondition cond : conditions) {
			if(cond instanceof IDataColum){
				isDepartmentColumn = QueryDepartmentHelper.isDepartmentColumn(inputTypeDefine, (DataColumImpl) cond);
				conds.addAll(stack);
				stack = new ArrayList<ICondition>();
			}
			if(isDepartmentColumn){
				stack.add(cond);
				// 吞掉三个
				if(stack.size()==3){
					isDepartmentColumn = false;
					// 只处理能识别的
					if(stack.get(1) instanceof OperatorImpl && stack.get(2) instanceof IValue ){
						IDataColum dataColumn = (IDataColum)stack.get(0);
						OperatorImpl op =  (OperatorImpl)stack.get(1);
						IValue value =  (IValue)stack.get(2);
						List<Long> departmentList = QueryDepartmentHelper.getConditionDepartmentList(value.getValue(), op.getOperator());
						boolean includeSubDepartment = departmentList!=null && departmentList.size()>0;
						if(includeSubDepartment){
							stack.clear();
            				List<ICondition> tmp = QueryDepartmentHelper.extractDepartmentCondition(dataColumn, op.getOperator(), departmentList,provider); 
    	                	stack.addAll(tmp);
						}else{
//							value.setValue(value.getValue().split("\\|")[0]);
							// 只处理具体值的，系统变量保持原样
							if(value.getValueType()==IValue.C_iValueType_Value){
								// 为了避免直接设置Query对象的值影响其它地方，新建一个对象。
								// 只为生成SQL服务，所以建立一个QueryUserConditionDefin
								QueryUserConditionDefin ud = new QueryUserConditionDefin();
								if(value.getValue()!=null){
									ud.setValue(value.getValue().split("\\|")[0].toString());
								}else{
									ud.setValue(null);
								}
								ud.setValueType(IValue.C_iValueType_Value);
								
								stack.remove(2);
								stack.add(ud);
							}
						}
					}
				}
			}else{
				stack.add(cond);
			}
		}
		conds.addAll(stack);
		result.clear();
		result.addAll(conds);
		OperatorImpl operator = new OperatorImpl(provider);
    	operator.setOperator(IOperator.C_iOperator_BracketLeft);
    	result.add(0,operator);        		
    	
    	operator = new OperatorImpl(provider);
    	operator.setOperator(IOperator.C_iOperator_BracketRight);
    	result.add(operator);			

    	return result;
	}
}
