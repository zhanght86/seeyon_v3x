/**
 * EdocElementManager.java
 * Created on 2007-4-19
 */
package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 *
 * @author <a href="mailto:handy@seeyon.com">Han Dongyou</a>
 *
 */
public interface EdocElementManager
{        
    
    /**
     * 更新公文元素属性。
     * @param element
     */
    public void updateEdocElement(EdocElement element);  
    
    /**
     * 根据ID返回指定的公文元素对象。
     * @param elementId 公文元素ID
     * @return 公文元素对象
     */
    public EdocElement getEdocElement(String elementId);
    
    /**
     * 返回所有已启用的公文元素列表。
     *  
     * @return List
     */
    public List<EdocElement> getEdocElements();
    
    /**
     * 返回所有公文元素条目数。
     * 
     * @return int
     */
    public int getAllEdocElementCount();
    
    /**
     * 返回所有公文元素列表。
     * 
     * @param startIndex 显示页数
     * @param numResults 每页显示条目数
     * 
     * @return List
     */
    public List<EdocElement> getAllEdocElements(int startIndex, int numResults);
    public List<EdocElement> getAllEdocElements();
    /**
     * 返回指定状态的公文元素条目数。
     * 
     * @param status 公文元素状态
     * @return int
     */
    public int getEdocElementCount(int status);
    
    /**
     * 返回指定状态的公文元素列表。
     * 
     * @param status 公文元素状态
     * @return List
     */
    public List<EdocElement> getEdocElementsByStatus(int status, int startIndex, int numResults);
    public List<EdocElement> getEdocElementsByStatus(int status);   
    /**
     * 返回指定 Id的公文元素
     * add by lindb
     * @param id
     * @return
     */
    public EdocElement getEdocElementsById(long id);
    
    public long getIdByFieldName(String fieldName);
    
    public EdocElement getByFieldName(String fieldName);
    
    public EdocElement getByFieldName(String fieldName,Long userAccountId ) ;
    
    public void initCmpElement();
    
    /**
     * 根据状态和类别（数字，字符，意见...）返回公文元素集合
     * @param status
     * @param type
     * @return
     */
    public List<EdocElement> getByStatusAndType(int status, int type);
    
    /**
     * 检查枚举值是否被引用
     * @param domainId：单位ID
     * @param metadataId
     * @return 引用字段名称 “”：没有引用；
     */
    public String getRefMetadataFieldName(Long domainId,Long metadataId);
    
    /**
     * 用于集群开发 修改elementTable的值
     * @param accountId
     * @param element
     */
    public void updateElementTable(String accountId,EdocElement element);
    
    /**
     * 通过单位取得
     * @param accountId
     * @return
     */
    public List<EdocElement> listElementByAccount(Long accountId);
    /**
     * 保存单位的元素--只在内存中保存
     * @param account
     * @param domainElement
     */
    public void saveCmpElementTable(Long account,List<EdocElement> domainElement);

    /**
     * 根据查询条件，取得公文元素集合， 带分页
     * @param condition 查询条件类型
     * @param textfield 输入的查询条件值
     * @param statusSelect 选择的公文状态
     * @param paginationFlag 分页标志符 1 分页； 0 不分页
     * @return
     */
	public List<EdocElement> getEdocElementsByContidion(String condition,
			String textfield, String statusSelect, int paginationFlag);

}
