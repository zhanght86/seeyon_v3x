package com.seeyon.v3x.calendar.constants;


/**
 * 重要级别
 * @author hub
 *
 */
public enum SignifyType {

	importance_exigence(1),
	no_importance_exigence(2),
	importance_no_exigence(3),
	no_importance_no_exigence(4),
	;
	private int key;
	
	SignifyType(int key){
		this.key = key;
	}
	
	public int getKey(){
		return this.key;
	}
	public int key(){
		return this.key;
	}
	
	public static SignifyType valueOf(int key){
		SignifyType[] enums = SignifyType.values();

		if (enums != null) {
			for (SignifyType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
}
