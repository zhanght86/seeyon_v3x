package com.seeyon.v3x.messageManager;

public class Constant {
	/**
	 * 自动清除状态: 采用ordinal()值
	 */
	public static enum Message_DELSET {
		NONE, // 两者都不生效
		DAY, // 消息天数生效
		COUNT, //消息数量生效
		ALL, //两者都生效
	};
}
