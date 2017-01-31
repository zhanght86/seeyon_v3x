package com.seeyon.v3x.calendar.constants;


/**
 * 事件类型
 * @author hub
 *
 */
public enum EventType {
	self(1),//自办
	arrange(2),//安排
	consign(3),//委托
	;
	private int key;
	
	EventType(int key){
		this.key = key;
	}
	
	public int getKey(){
		return this.key;
	}
	
	public int key(){
		return this.key;
	}
	
	public static EventType valueOf(int key){
		EventType[] enums = EventType.values();

		if (enums != null) {
			for (EventType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
}
