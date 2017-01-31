package com.seeyon.v3x.calendar.constants;


/**
 * 完成类型
 * @author hub
 *
 */
public enum CompleteType {
pending(1),
finished(2),
InProgress(3),
done(4),
;
	private int key;
	
	CompleteType(int key){
		this.key = key;
	}
	public int key(){
		return this.key;
	}
	public int getKey(){
		return this.key;
	}
	
	public static CompleteType valueOf(int key){
		CompleteType[] enums = CompleteType.values();

		if (enums != null) {
			for (CompleteType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}

}
