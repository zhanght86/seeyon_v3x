/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.edoc.dao.EdocMarkAclDAO;
import com.seeyon.v3x.edoc.domain.EdocMarkAcl;
import com.seeyon.v3x.edoc.manager.EdocMarkAclManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class EdocMarkAclManagerImpl implements EdocMarkAclManager{
	
	private EdocMarkAclDAO edocMarkAclDAO;
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setEdocMarkAclDAO(EdocMarkAclDAO edocMarkAclDAO) {
		this.edocMarkAclDAO = edocMarkAclDAO;
	}
	
	/**
	 * 方法描述：保存公文文号使用授权
	 */
	public void saveMarkAcl(List<EdocMarkAcl> edocMarkAclList){
		for(EdocMarkAcl edocMarkAcl : edocMarkAclList){
			this.edocMarkAclDAO.saveEdocMarkAcl(edocMarkAcl);
		}
	}
	
	/**
	 * 方法描述：根据公文文号定义ID查询公文文号使用授权
	 * @return 返回授权部门名称的列表
	 * @throws BusinessException 
	 */
	public List<V3xOrgDepartment> queryMarkAclById(Long edocMarkDefinitionId) throws BusinessException{
		List<EdocMarkAcl> list = this.edocMarkAclDAO.findEdocMarkAclByProperty(
								"edocMarkDefinition.id", edocMarkDefinitionId);
		List<V3xOrgDepartment> aclDeptList = new ArrayList<V3xOrgDepartment>();
		for(EdocMarkAcl edocMarkAcl : list){
			Long deptId = edocMarkAcl.getDeptId();
			V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
			aclDeptList.add(dept);
		}
		return aclDeptList;
	}
	
	public List<EdocMarkAcl> getMarkAclById(Long edocMarkDefinitionId)throws BusinessException{
		List<EdocMarkAcl> list = this.edocMarkAclDAO.findEdocMarkAclByProperty(
				"edocMarkDefinition.id", edocMarkDefinitionId);
		return list;
	}
	
	public void deleteByDefId(Long defId){
		edocMarkAclDAO.deleteEdocMarkAclByDefinitionId(defId);
	}
}
