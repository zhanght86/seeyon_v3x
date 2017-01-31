package www.seeyon.com.v3x.form.controller.formservice.inf;

import java.util.List;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;


public interface IPageObjectCheck {
	
	  /**
	   * 此方法用来对查询、统计、表单绑定注入SessionObject中的对象，做匹配检查
	   * 
	   * 如果在进行查询、统计、表单绑定动作后，再修改原有SessionObject中的对象，
	   * 造成不匹配的情况返回为List<Exception>
	   * 
	   * 
	   * @param sessionobject sessionobject 页面操作对象
	   * 
	   * @throws SeeyonFormException 创建失败抛出异常，可以根据getErrCode获取错误的类型。<br>
	   * 错误编码参考C_iStorageErrode_XXX的常量
	   */	
	public List<SeeyonFormException> isMatch(SessionObject sessionobject) throws SeeyonFormException;
}
