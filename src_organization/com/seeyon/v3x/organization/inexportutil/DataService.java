package com.seeyon.v3x.organization.inexportutil;

import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgPost;

/**
 * 
 * @author kyt
 *
 */
public class DataService {
	public static final String accountid = "org_account_id";
	public static final String levelid = "org_level_id";
	public static final String postid = "org_post_id";
	public static final String depid = "org_department_id";
	
	/**
	 * 用于传入关联的外键的值
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	public static Long setForeignKey(List lst,String fieldname,String value){
		if(fieldname.equalsIgnoreCase(accountid)){
			for(int i=0;i<lst.size();i++){
				V3xOrgAccount voa = (V3xOrgAccount)lst.get(i);
				if(value.equals(voa.getName())){
					return voa.getId();
				}
			}
		}else if(fieldname.equalsIgnoreCase(levelid)){
			for(int i=0;i<lst.size();i++){
				V3xOrgLevel voa = (V3xOrgLevel)lst.get(i);
				if(value.equals(voa.getName())){
					return voa.getId();
				}
			}
		}else if(fieldname.equalsIgnoreCase(postid)){
			for(int i=0;i<lst.size();i++){
				V3xOrgPost voa = (V3xOrgPost)lst.get(i);
				if(value.equals(voa.getName())){
					return voa.getId();
				}
			}
		}else if(fieldname.equalsIgnoreCase(depid)){
			for(int i=0;i<lst.size();i++){
				V3xOrgDepartment voa = (V3xOrgDepartment)lst.get(i);
				if(value.equals(voa.getName())){
					return voa.getId();
				}
			}
		}else{
			return new Long(0);
		}
		return  new Long(0);
	}
}
