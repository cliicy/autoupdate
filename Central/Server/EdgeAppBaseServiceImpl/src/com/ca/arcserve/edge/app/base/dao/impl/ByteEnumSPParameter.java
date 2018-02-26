package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class ByteEnumSPParameter extends StoredProcedureParameter<Byte> {
	public static final int JDBCTYPE = Types.TINYINT;
	public ByteEnumSPParameter(Byte value) {
		super(JDBCTYPE,-1,value,true);
	}
	public ByteEnumSPParameter(Byte value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
