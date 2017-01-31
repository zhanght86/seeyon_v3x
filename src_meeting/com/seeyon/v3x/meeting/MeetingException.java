package com.seeyon.v3x.meeting;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class MeetingException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MeetingException(String errorCode) {
		super(errorCode, new String[]{});
    }
	
	public MeetingException(String errorCode, String[] errorArgs) {
        super(errorCode,errorArgs);
    }

    public MeetingException(String errorCode, String errorArg) {
    	super(errorCode,errorArg);
    }
}
