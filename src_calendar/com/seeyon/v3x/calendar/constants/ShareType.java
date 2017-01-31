package com.seeyon.v3x.calendar.constants;


/**
 * 共享类型
 * @author hub
 *
 */
public enum ShareType {
	personal(1),//私人事件
	publicity(2),//公开给个人
	superior(3),//共享给上级
	junior(4),//共享给下级
	department(5),//共享给部门
	project(6),//共享给项目
	assistant(7),//共享给助手
	;
	
	private int key;
	
	ShareType(int key){
		this.key = key;
	}
	
	public int getKey(){
		return this.key;
	}
	
	public int key(){
		return this.key;
	}
	
	
	
	public static ShareType valueOf(int key){
		ShareType[] enums = ShareType.values();

		if (enums != null) {
			for (ShareType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
		
	}
}
