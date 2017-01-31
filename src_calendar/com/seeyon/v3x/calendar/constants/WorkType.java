package com.seeyon.v3x.calendar.constants;


/**
 * 工作类型
 * @author hub
 *
 */
public enum WorkType {
	self(1),
	supervise(2),
	assist(3),
	;
	private int key;
	
	WorkType(int key){
		this.key = key;
	}
	public int getKey(){
		return this.key;
	}
	public int key(){
		return this.key;
	}
	
	public static WorkType valueOf(int key){
		WorkType[] enums = WorkType.values();

		if (enums != null) {
			for (WorkType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
}
