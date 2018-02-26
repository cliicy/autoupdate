package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class ByteSPParameter extends StoredProcedureParameter<Byte> {
	public static final int JDBCTYPE = Types.TINYINT;
	public ByteSPParameter(Byte value) {
		super(JDBCTYPE,-1,value,true);
	}
	public ByteSPParameter(Byte value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
