package com.youedata.service;

import java.io.Serializable;
import java.util.Date;

public class CodeInfo implements Serializable  {
	private static final long serialVersionUID = -1L;
    
	private String codeKey;
	
	private Date gmtCreate;
	
	private String codeVal;
	
	public String getCodeKey() {
		return codeKey;
	}
	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getCodeVal() {
		return codeVal;
	}
	public void setCodeVal(String codeVal) {
		this.codeVal = codeVal;
	}

	@Override
	public String toString() {
		return "CodeInfo{" +
				"codeKey='" + codeKey + '\'' +
				", gmtCreate=" + gmtCreate +
				", codeVal='" + codeVal + '\'' +
				'}';
	}
}