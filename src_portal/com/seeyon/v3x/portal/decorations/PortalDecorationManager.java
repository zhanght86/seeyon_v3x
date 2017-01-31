package com.seeyon.v3x.portal.decorations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.constants.LayoutConstants;
import com.seeyon.v3x.portal.exception.LayoutPropertiesNotEnoughException;

public class PortalDecorationManager {
	private static Log log = LogFactory.getLog(PortalDecorationManager.class);
	private static final String Decorations_PATH = File.separator + "WEB-INF" + File.separator + "decorations" + File.separator + "layout";
	private static final String DECORATION_PRO = "decorations.properties";
	private static List<String> layoutTypes = new ArrayList<String>();
	
	public void setLayoutTypes(List<String> layoutTypes) {
		PortalDecorationManager.layoutTypes = layoutTypes;
	}
	
	private static Map<String,PortalDecoration> a8Decoration = null;
	
	public void init (){
		log.info("加载a8首页修饰...");
		long start = System.currentTimeMillis();
		String path = SystemEnvironment.getA8ApplicationFolder() + Decorations_PATH;
		File decorationsFile = new File(path);
		File[] decorations = decorationsFile.listFiles();
		for(File f : decorations){
			if(f.isDirectory()){
				//寻找配置文件
				File pro = new File(f.getPath()+ File.separator +DECORATION_PRO);
				if(pro.exists()){
					FileInputStream ins = null;
					try {
						ins = new FileInputStream(pro);
						Properties p = new Properties();
						p.load(ins);
						addDecoration(p);
						//排序
					} catch (FileNotFoundException e) {
						log.warn("解析seeyon中的框架"+e);
					} catch (IOException e) {
						log.warn("解析seeyon中的框架"+e);
					}finally{
						if(ins != null){
							try {
								ins.close();
							} catch (IOException e) {
								log.error(e);
							}
						}
					}
				}
			}
		}
		sortDecoration();
		log.info("加载a8首页修饰结束,耗时:"+(System.currentTimeMillis()-start)+"ms");
	}
	
	private static void addDecoration(Properties prop){
		if(prop == null){
			return ;
		}
		PortalDecoration decoration = new PortalDecoration();
		try {
			decoration.loadFromProperties(prop);
			addDecoration(decoration);
		} catch (LayoutPropertiesNotEnoughException e) {
			log.warn("加载资源出错,缺少配置:"+e.getProp());
		}
	}
	private static void sortDecoration(){
		List<PortalDecoration> list = getAllDecoration();
		Collections.sort(list);
		for(PortalDecoration portal : list){
			String layoutType = portal.getLayoutType();
			List<String> listDecoration = LayoutConstants.lagoutToDecorations.get(layoutType);
			if(listDecoration == null){
				listDecoration = new ArrayList<String>();
			}
			listDecoration.add(portal.getId());
			LayoutConstants.lagoutToDecorations.put(layoutType, listDecoration);
		}
	}
	
	private static void addDecoration(PortalDecoration decoration){
		if(decoration == null)return;
		if(a8Decoration == null){
			a8Decoration = new HashMap<String,PortalDecoration>();
		}
		a8Decoration.put(decoration.getId(), decoration);
	}
	
	public static List<PortalDecoration> getAllDecoration(){
		List<PortalDecoration> result = new ArrayList<PortalDecoration>(a8Decoration.values());
		Collections.sort(result);
		return result;
	}
	
	public static PortalDecoration getDecoration(String id){
		return a8Decoration.get(id);
	}

	public static List<String> getAllLayoutType(){
		return layoutTypes;
	}
}
