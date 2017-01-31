package com.seeyon.v3x.bulletin;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class BulletinException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BulletinException(String errorCode) {
		super(errorCode, new String[]{});
    }
	
	public BulletinException(String errorCode, String[] errorArgs) {
        super(errorCode,errorArgs);
    }

    public BulletinException(String errorCode, String errorArg) {
    	super(errorCode,errorArg);
    }
}
