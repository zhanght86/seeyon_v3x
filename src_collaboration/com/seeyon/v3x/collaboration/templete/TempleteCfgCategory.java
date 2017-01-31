package com.seeyon.v3x.collaboration.templete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;

public class TempleteCfgCategory<T> {
	Map<Long,TempleteCategory> idCategory = new HashMap<Long,TempleteCategory>();
    Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
    Map<String,List<T>> result = new HashMap<String,List<T>>();
    Set<String> categoryNames = new LinkedHashSet<String>();
    
    public TempleteCfgCategory(List<TempleteCategory> categorys){
    	for (TempleteCategory category : categorys) {
    		idCategory.put(category.getId(), category);
    		nameCategory.put(category.getName(), category);
		}
    }
    public TempleteCfgCategory(List<TempleteCategory> categorys,List<T> config,TempleteCategoryManager templeteCategoryManager){
    	this(categorys);
    	for (T t : config) {
    		setTempeteConfig(t,templeteCategoryManager);
		}
    }
    public void setTempeteConfig(T config,TempleteCategoryManager templeteCategoryManager){
    	Long categoryId = null;
    	if(config instanceof TempleteConfig){
    		TempleteConfig c = (TempleteConfig) config;
    		categoryId = c.getCategoryId();
    	}else if(config instanceof Templete){
    		Templete t = (Templete) config;
    		categoryId = t.getCategoryId();
    	}
    	if(categoryId == null) return;
    	TempleteCategory categorys = idCategory.get(categoryId);
    	if(categorys == null){
    		categorys = templeteCategoryManager.get(categoryId);
    		if(categorys == null) return;
    		idCategory.put(categorys.getId(), categorys);
    	}
    	TempleteCategory nameCate = nameCategory.get(categorys.getName());
    	if(nameCate == null){
    		nameCategory.put(categorys.getName(), categorys);
    		nameCate = categorys;
    	}
    	List<T> configList = result.get(nameCate.getName());
    	if(configList == null){
    		configList = new ArrayList<T>();
    		result.put(nameCate.getName(), configList);
    	}
    	configList.add(config);
    }
    public Map<String,List<T>> getColNameTemplete(){
    	//模板分类排序
    	List<TempleteCategory> listNameCategorys = new ArrayList<TempleteCategory>(this.nameCategory.values());
    	Collections.sort(listNameCategorys);
    	Map<String,List<T>> theResult = new LinkedHashMap<String,List<T>>();
    	for(TempleteCategory category : listNameCategorys){
    		List<T> list = this.result.get(category.getName());
    		theResult.put(category.getName(), list);
    		categoryNames.add(category.getName());
    	}
    	return theResult;
    }
    public Set<String> getCategoryNames(){
        return this.categoryNames;
    }
}
