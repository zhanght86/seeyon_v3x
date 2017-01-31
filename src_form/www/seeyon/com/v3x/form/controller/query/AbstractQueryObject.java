package www.seeyon.com.v3x.form.controller.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SelectPersonOperation;
import www.seeyon.com.v3x.form.domain.FomObjaccess;

/**
 * 抽象类，提取QueryObject和ReportObject的共有方法，去除重复代码。
 * @author wangwenyou
 *
 */
public abstract class AbstractQueryObject {
	//产生list,用于数据库form_objaccess存储记录
	/*
	 * user  Member|-7700668784483677330,Team|-8365864659089404545,Post|8944611032511497461
	 * appId 
	 * state 表单状态
	 */
	protected void genObjAccessList(String user, long appId, int state,int objectType,String name,List<FomObjaccess> objAccessList) throws SeeyonFormException{
		FomObjaccess foa;
		if(!"".equals(user) && !"null".equals(user) && user !=null){
			String[] userArray = user.split(",");//取得Member|-7700668784483677330
			String[] s ;
			Set<Long> useridmap = new HashSet<Long>();
			for(int i = 0; i < userArray.length; i++){
				foa = new FomObjaccess();
				s = userArray[i].split("\\|");
				foa.setRefAppmainId(appId);//所属表单
				foa.setObjectname(name);//对象名称
				foa.setObjecttype(objectType);//对象类型 1查询对象
				foa.setState(state);//状态

	            if(s.length > 1){
	            	SelectPersonOperation spc = new SelectPersonOperation();
					String userType = s[0];
					if(s.length>2) userType = userType + s[2];
					foa.setUsertype(spc.changeType(userType));//用户类型
	    			foa.setUserid(Long.valueOf(s[1]));//用户对象ID
	    			//加入防护，如果查询(统计)的授权人id相同，则去掉重复记录，在找到更好方法前临时使用。
	    			if(!useridmap.contains(foa.getUserid())){
	    				objAccessList.add(foa);
	    			}
	    			useridmap.add(foa.getUserid());
	            }
				
			}
		}else{
			foa = new FomObjaccess();
			foa.setRefAppmainId(appId);//所属表单
			foa.setObjectname(name);//对象名称
			foa.setObjecttype(objectType);//对象类型 1查询对象
			foa.setState(state);//状态
			objAccessList.add(foa);	
		}
	}
}
