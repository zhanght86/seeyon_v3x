package com.seeyon.v3x.peoplerelate;


/**
 * 关联人员类型
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-8
 */
public enum RelationType {
	leader(1), // 领导
	assistant(2), // 秘书
	junior(3), // 下级
	confrere(4), //同事
	otherEscapeLeader(5);//除了领导的所有人员

	private int key;

	private RelationType(int key) {
		this.key = key;
	}

	public int key() {
		return key;
	}
	
	/**
	 * 根据key得到枚举类型
	 * 
	 * @param key
	 * @return
	 */
	public static RelationType valueOf(int key) {
		RelationType[] enums = RelationType.values();

		if (enums != null) {
			for (RelationType enum1 : enums) {
				if (enum1.key() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
}
