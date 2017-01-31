package www.seeyon.com.v3x.form.controller.formservice;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.inf.IPageObjectCheck;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;

public class PoCheckManager  {
	private List<IPageObjectCheck> checkList=new ArrayList<IPageObjectCheck>();

	public void setInitList(Object aList) throws SeeyonFormException {
		if (aList instanceof Properties)
		   initFromProperties((Properties)aList);
		else if (aList instanceof Map)
 		   initFromHashMap((Map)aList);
	}
	private void initFromProperties(Properties initProperties)throws SeeyonFormException {
		 String fName;
		 String fClassName;
		 IPageObjectCheck fobj;
	     for (Enumeration e = initProperties.propertyNames() ; e.hasMoreElements() ;) {
	    	 fName=(String)e.nextElement();
	    	 fClassName=initProperties.getProperty(fName);
	    	 fobj=newSubInstanc(fClassName);
	    	 checkList.add(fobj);
	      }
		}
	public void init() throws SeeyonFormException {
	}
	private void initFromHashMap(Map initHashMap)throws SeeyonFormException {
		 String fName;
		for (Object fobj: initHashMap.keySet()) {
			fName=(String)fobj;
			checkList.add((IPageObjectCheck)initHashMap.get(fName));
		} 
	}
		
	


	private IPageObjectCheck newSubInstanc(String aClassName)throws SeeyonFormException {
		Class fclass=null;
		Object result;
		try {
			fclass=Class.forName(aClassName);
		} catch (Exception e) {
		    //TODO songjian定义错误编码
			//throw new SeeyonFormException(1,"装载类错误 classname="+aClassName);
			throw new SeeyonFormException(1,"'"+Constantform.getString4CurrentUser("form.input.classloaderror.label")+"' classname="+aClassName);
		}
		try {
			result=fclass.newInstance();
		} catch (Exception e) {
		    //TODO songjian定义错误编码
			//throw new SeeyonFormException(1,"建立类错误 classname="+aClassName);
			throw new SeeyonFormException(1,"'"+Constantform.getString4CurrentUser("form.input.classcreateerror.label")+"' classname="+aClassName);
		}
		return (IPageObjectCheck)result;
	}
	
	public List<SeeyonFormException> doCheck(SessionObject sessionobject) throws SeeyonFormException{
		List<SeeyonFormException> list = new ArrayList<SeeyonFormException>();
		List<SeeyonFormException> excetpionList = null;
		for (IPageObjectCheck check : checkList) {
			excetpionList = check.isMatch(sessionobject);
			if(excetpionList.size() != 0)
				list.addAll(excetpionList);
			
		}
		return list;
	}
}
