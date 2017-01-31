package www.seeyon.com.v3x.form.controller;

import java.util.ArrayList;
import java.util.List;

import www.seeyon.com.v3x.form.base.SeeyonFormException;

public class SeeyonFormCheckException extends SeeyonFormException{

	public SeeyonFormCheckException(int aErrCode) {
		super(aErrCode);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<SeeyonFormException> list = new ArrayList<SeeyonFormException>();

	public List<SeeyonFormException> getList() {
		return list;
	}

	public void setList(List<SeeyonFormException> list) {
		this.list = list;
	}
	
	

}
