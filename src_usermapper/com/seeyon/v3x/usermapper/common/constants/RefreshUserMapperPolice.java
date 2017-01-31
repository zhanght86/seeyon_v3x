package com.seeyon.v3x.usermapper.common.constants;

public enum RefreshUserMapperPolice {
	append(0x0),
	delete(0x1),
	ignoreWhenExist(0x2),
	refreshAll(0x4),
	refreshSameLogin(0x8);
	
//	 标识 用于数据库存储
	private int key;

	RefreshUserMapperPolice(int key) {
		this.key = key;
	}

	public int getKey() {
		return this.key;
	}

	public int key() {
		return this.key;
	}
	
	static public RefreshUserMapperPolice value4Name(String name){
		try{
			return Enum.valueOf(RefreshUserMapperPolice.class, name);
		}catch(Exception e){
			return null;
		}
	}
	
}//end class
