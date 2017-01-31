package com.seeyon.v3x.calendar.constants;


/**
 * 优先级别
 * @author hub
 *
 */
public enum PriorityType {
	low(1),
	middle(2),
	hight(3),
	;
	private int key;
	
	public int getKey(){
		return this.key;
	}
	
	public int key(){
		return this.key;
	}
	
	PriorityType(int key){
		this.key = key;
	}
	public static PriorityType valueOf(int key){
		PriorityType[] enums = PriorityType.values();

		if (enums != null) {
			for (PriorityType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
}
