package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;

class StringSPParameter extends StoredProcedureParameter<String> {
	public static final int JDBCTYPE = Types.NVARCHAR;
	boolean encrypt = false;
	boolean wildcardConversion = false;
	public StringSPParameter(String value) {
		super(JDBCTYPE,-1,value,true);
	}
	public StringSPParameter(String value,int jdbcType,boolean encrypt, boolean wildcardConversion) {
		super(jdbcType,-1,value,true);
		this.encrypt = encrypt;
		this.wildcardConversion = wildcardConversion;
	}
	public boolean isEncrypt() {
		return encrypt;
	}
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}
	public boolean isWildcardConversion() {
		return wildcardConversion;
	}
	public void setWildcardConversion(boolean wildcardConversion) {
		this.wildcardConversion = wildcardConversion;
	}
	@Override
	public String getValue() {
		String value =  super.getValue();
		if(value==null) return null;
		
		if (wildcardConversion) {
			value = DaoUtils.convertWildcard(value);
		}
		
		if(encrypt && this.isInPara())
			return
				DaoFactory.getEncrypt().encryptString(value);
		else if(encrypt && !this.isInPara())
		    return DaoFactory.getEncrypt().decryptString(value);
		else
			return value;
	}

}
