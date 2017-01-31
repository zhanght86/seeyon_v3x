package com.seeyon.v3x.formbizconfig.webmodel;

import com.seeyon.v3x.menu.domain.Menu;
/**
 * 在查看或修改表单业务配置时，配合前端展现菜单已选项使用
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class MenuConfig {
	/** 挂接菜单，均为二级菜单 */
	private Menu menu;
	/** 挂接菜单在表单业务配置中所属类型，比如：新建事项为7、待发事项为8等 */
	private int category;
	/** 新建事项菜单所对应表单模板ID，其他类型挂接菜单无 */
	private String templeteId;
	
	public MenuConfig() {
	}
	
	/**
	 * 定义构造方法
	 * @param menu			已选的挂接二级菜单
	 * @param category		菜单所属类型编号，比如：新建事项为7、待发事项为8等
	 * @param templeteId	新建事项菜单所对应表单模板ID，其他类型挂接菜单无
	 */
	public MenuConfig(Menu menu, int category, String templeteId) {
		this.menu = menu;
		this.category = category;
		this.templeteId = templeteId;
	}
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(String templeteId) {
		this.templeteId = templeteId;
	}
	
}
