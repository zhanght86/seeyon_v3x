package com.seeyon.v3x.news;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class NewsException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewsException(String errorCode) {
		super(errorCode, new String[]{});
    }
	
	public NewsException(String errorCode, String[] errorArgs) {
        super(errorCode,errorArgs);
    }

    public NewsException(String errorCode, String errorArg) {
    	super(errorCode,errorArg);
    }
}
